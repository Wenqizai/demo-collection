package com.wenqi.springboot.center.thread.transaction;

import com.wenqi.springboot.mapper.SpringBootRoleMapper;
import com.wenqi.springboot.pojo.model.Role;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.stereotype.Service;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionException;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.DefaultTransactionDefinition;
import org.springframework.transaction.support.TransactionTemplate;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executors;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * 多线程事务控制
 * <p>
 * 可以行得通的方案: multiThreadInsertInLimit (严重依赖核心线程数)
 *
 * @author liangwenqi
 * @date 2023/8/16
 */
@Slf4j
@Service
public class MultiThreadTransaction {
    private final ThreadPoolExecutor executor = new ThreadPoolExecutor(2, 20, 10, TimeUnit.SECONDS, new SynchronousQueue<>(), Executors.defaultThreadFactory(), new ThreadPoolExecutor.CallerRunsPolicy());

    @Autowired
    private SpringBootRoleMapper springBootRoleMapper;
    @Autowired
    private TransactionTemplate transactionTemplate;
    @Autowired
    private TransactionDefinition transactionDefinition;
    @Autowired
    private DataSourceTransactionManager dataSourceTransactionManager;
    @Autowired
    private SqlSessionTemplate sqlSessionTemplate;
    @Autowired
    private PlatformTransactionManager transactionManager;

    public void multiThreadInsert(String id) {
    }

    /**
     * 子线程不够的情况下, 该方案也行不通
     */
    public void multiThreadInsertOnlyFewWork(String id) {
        List<Role> roleList = buildRoleList(id);
        AtomicBoolean hasException = new AtomicBoolean(false);
        CountDownLatch threadLatches = new CountDownLatch(roleList.size());
        List<TransactionStatus> transactionStatuses = Collections.synchronizedList(new ArrayList<>());
        for (int i = 0; i < roleList.size(); i++) {
            Role role = roleList.get(i);
            int finalI = i;
            executor.execute(() -> {
                try {

                    if (hasException.get()) {
                        return;
                    }

                    DefaultTransactionDefinition def = new DefaultTransactionDefinition();
                    def.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRES_NEW); // 事物隔离级别，开启新事务，这样会比较安全些。
                    TransactionStatus status = transactionManager.getTransaction(def); // 获得事务状态
                    transactionStatuses.add(status);

                    log.info("开始事务: " + finalI);

                    springBootRoleMapper.insertRole(role);
                    //if (finalI == 4) {
                    //    throw new RuntimeException(finalI + " -> 发生了异常");
                    //}

                    log.info("结束事务: " + finalI);
                } catch (Exception e) {
                    log.error("发生了异常: ", e);
                    hasException.set(true);
                    threadLatches.countDown();
                }
            });
        }

        try {
            // 倒计时锁设置超时时间 30s
            boolean await = threadLatches.await(30, TimeUnit.SECONDS);
            if (!await) { // 等待超时，事务回滚
                hasException.set(true);
            }
        } catch (Throwable e) {
            log.error("threadLatch wait exception", e);
            hasException.set(true);
        }

        if (hasException.get()) {
            transactionStatuses.forEach(transactionStatus -> transactionManager.rollback(transactionStatus));
        } else {
            transactionStatuses.forEach(transactionStatus -> transactionManager.commit(transactionStatus));
        }

    }

    /**
     * 线程池核心线程是2个, 最终能控制事务的只有2个, 其他任务被阻塞了导致最后被回滚了事务
     * <p>
     * 除非: 核心线程数要等于 threadLatches 的个数, 否则方案行不通
     * <p>
     * 解决:
     *      1. 适当提高最大线程数, maximumPoolSize 远大于 threadLatches , 考虑并发请求情况
     *      2. 阻塞队列使用: SynchronousQueue, 不存储元素
     */
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = {Exception.class})
    public void multiThreadInsertInLimit(String id) {
        List<Role> roleList = buildRoleList(id);
        AtomicBoolean hasException = new AtomicBoolean(false);
        CountDownLatch mainLatch = new CountDownLatch(1);
        CountDownLatch threadLatches = new CountDownLatch(roleList.size());
        for (int i = 0; i < roleList.size(); i++) {
            int finalI = i;
            executor.execute(() -> {
                doInChildThread(finalI, hasException, roleList.get(finalI), threadLatches, mainLatch);
            });
        }

        try {
            // 倒计时锁设置超时时间 30s
            boolean await = threadLatches.await(30, TimeUnit.SECONDS);
            if (!await) { // 等待超时，事务回滚
                hasException.set(true);
            }
        } catch (Throwable e) {
            log.error("threadLatch wait exception", e);
            hasException.set(true);
        }

        mainLatch.countDown(); // 切换到子线程执行

    }

    public void doInChildThread(int i, AtomicBoolean hasException, Role role, CountDownLatch threadLatches, CountDownLatch mainLatch) {
        if (hasException.get()) {
            return;
        }
        TransactionStatus transactionStatus = dataSourceTransactionManager.getTransaction(transactionDefinition);
        try {
            log.info("开始事务: " + i);

            springBootRoleMapper.insertRole(role);
            if (i == 4) {
                throw new RuntimeException(i + " -> 发生了异常");
            }

            log.info("结束事务: " + i);
        } catch (Exception e) {
            log.error("发生了异常: ", e);
            hasException.set(true);
        } finally {
            threadLatches.countDown();
        }

        try {
            mainLatch.await();  //等待主线程执行
        } catch (Throwable e) {
            hasException.set(true);
        }

        // 判断是否有错误，如有错误 就回滚事务
        if (hasException.get()) {
            dataSourceTransactionManager.rollback(transactionStatus);
        } else {
            dataSourceTransactionManager.commit(transactionStatus);
        }
    }


    /**
     * 不起作用, 无法回滚或提交
     */
    public void multiThreadInsertNotWork(String id) {
        List<Role> roleList = buildRoleList(id);
        AtomicBoolean hasException = new AtomicBoolean(false);
        List<TransactionStatus> transactionStatuses = new ArrayList<>();
        List<CompletableFuture<Void>> futureList = new ArrayList<>();
        for (int i = 0; i < roleList.size(); i++) {
            int finalI = i;
            CompletableFuture<Void> completableFuture = CompletableFuture.runAsync(() -> {
                if (hasException.get()) {
                    return;
                }
                TransactionStatus transaction = dataSourceTransactionManager.getTransaction(transactionDefinition);
                try {
                    System.out.println("开始事务: " + finalI);
                    springBootRoleMapper.insertRole(roleList.get(finalI));
                    //if (finalI == 4) {
                    //    try {
                    //        Thread.sleep(100);
                    //    } catch (InterruptedException e) {
                    //        throw new RuntimeException(e);
                    //    }
                    //    throw new RuntimeException(finalI + " -> 发生了异常");
                    //}
                    System.out.println("结束事务: " + finalI);
                } catch (TransactionException e) {
                    hasException.set(true);
                    throw new RuntimeException(e);
                } finally {
                    transactionStatuses.add(transaction);
                }
            }, executor);
            futureList.add(completableFuture);
        }
        CompletableFuture.allOf(futureList.toArray(new CompletableFuture[0])).join();

        if (hasException.get()) {
            transactionStatuses.forEach(transactionStatus -> dataSourceTransactionManager.rollback(transactionStatus));
        } else {
            transactionStatuses.forEach(transactionStatus -> dataSourceTransactionManager.commit(transactionStatus));
        }
    }

    /**
     * 不行呀
     */
    public void multiThreadInsertButNotRollback4(String id) {
        List<Role> roleList = buildRoleList(id);
        SqlSessionFactory sqlSessionFactory = sqlSessionTemplate.getSqlSessionFactory();
        SqlSession sqlSession = sqlSessionFactory.openSession();
        Connection connection = sqlSession.getConnection();

        try {
            connection.setAutoCommit(false);

            AtomicBoolean hasException = new AtomicBoolean(false);
            CountDownLatch countDownLatch = new CountDownLatch(roleList.size());
            for (int i = 0; i < roleList.size(); i++) {

                int finalI = i;
                executor.execute(() -> {
                    if (hasException.get()) {
                        return;
                    }
                    try {
                        log.info("开始事务: " + finalI);

                        springBootRoleMapper.insertRole(roleList.get(finalI));
                        if (finalI == 4) {
                            throw new RuntimeException(finalI + " -> 发生了异常");
                        }

                        log.info("结束事务: " + finalI);
                    } catch (Exception e) {
                        log.error("发生了异常: ", e);
                        hasException.set(true);
                    } finally {
                        countDownLatch.countDown();
                    }
                });
            }

            try {
                countDownLatch.wait(10, TimeUnit.SECONDS.ordinal());
            } catch (InterruptedException e) {
                hasException.set(true);
            }

            if (hasException.get()) {
                connection.rollback();
            } else {
                connection.commit();
            }
        } catch (Exception e) {
            if (Objects.nonNull(connection)) {
                try {
                    connection.rollback();
                } catch (SQLException ex) {
                    throw new RuntimeException(ex);
                }
            }
        } finally {
            try {
                connection.close();
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }
    }


    /**
     * 不行呀 -> Spring 把事务用 ThreadLocal 保存起来, 无法用主线程去管理这些事务呀
     */
    public void multiThreadInsertButNotRollback3(String id) {
        List<Role> roleList = buildRoleList(id);
        TransactionStatus transaction = dataSourceTransactionManager.getTransaction(transactionDefinition);
        AtomicBoolean hasException = new AtomicBoolean(false);
        CountDownLatch countDownLatch = new CountDownLatch(roleList.size());

        for (int i = 0; i < roleList.size(); i++) {
            try {
                int finalI = i;
                executor.execute(() -> {
                    if (hasException.get()) {
                        return;
                    }

                    log.info("开始事务: " + finalI);

                    springBootRoleMapper.insertRole(roleList.get(finalI));
                    if (finalI == 4) {
                        try {
                            Thread.sleep(100);
                        } catch (InterruptedException e) {
                            throw new RuntimeException(e);
                        }
                        throw new RuntimeException(finalI + " -> 发生了异常");
                    }

                    log.info("结束事务: " + finalI);
                });
            } catch (Exception e) {
                log.error("发生了异常: ", e);
                hasException.set(true);
            } finally {
                countDownLatch.countDown();
            }
        }

        try {
            countDownLatch.wait(10, TimeUnit.SECONDS.ordinal());
        } catch (InterruptedException e) {
            hasException.set(true);
        }

        if (hasException.get()) {
            dataSourceTransactionManager.rollback(transaction);
        } else {
            dataSourceTransactionManager.commit(transaction);
        }
    }


    /**
     * 不能回滚事务, 怀疑这一句有点问题
     * CompletableFuture.allOf(futureList.toArray(new CompletableFuture[0])).join();
     */
    public void multiThreadInsertWaitButNotRollback2(String id) {
        List<Role> roleList = buildRoleList(id);
        TransactionStatus transaction = dataSourceTransactionManager.getTransaction(transactionDefinition);
        AtomicBoolean hasException = new AtomicBoolean(false);
        List<CompletableFuture<Void>> futureList = new ArrayList<>();
        for (int i = 0; i < roleList.size(); i++) {
            int finalI = i;
            CompletableFuture<Void> future = CompletableFuture.runAsync(() -> {
                if (hasException.get()) {
                    return;
                }
                log.info("开始事务: " + finalI);
                springBootRoleMapper.insertRole(roleList.get(finalI));

                if (finalI == 4) {
                    try {
                        Thread.sleep(100);
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                    throw new RuntimeException(finalI + " -> 发生了异常");
                }

                log.info("结束事务: " + finalI);
            }, executor).handle((r, e) -> {
                if (Objects.nonNull(e)) {
                    hasException.set(true);
                    log.info("发生异常: ", e);
                }
                return r;
            });
            futureList.add(future);
        }
        CompletableFuture.allOf(futureList.toArray(new CompletableFuture[0])).join();

        if (hasException.get()) {
            dataSourceTransactionManager.rollback(transaction);
        } else {
            dataSourceTransactionManager.commit(transaction);
        }
    }


    /**
     * 即使等待所有事务结束, 事务也是无法回滚
     */
    public void multiThreadInsertWaitButNotRollback(String id) {
        List<Role> roleList = buildRoleList(id);
        transactionTemplate.execute(transactionStatus -> {
            try {
                List<CompletableFuture<Void>> futureList = new ArrayList<>();
                for (int i = 0; i < roleList.size(); i++) {
                    int finalI = i;
                    CompletableFuture<Void> future = CompletableFuture.runAsync(() -> {
                        log.info("开始事务: " + finalI);
                        springBootRoleMapper.insertRole(roleList.get(finalI));

                        if (finalI == 4) {
                            try {
                                Thread.sleep(100);
                            } catch (InterruptedException e) {
                                throw new RuntimeException(e);
                            }
                            throw new RuntimeException(finalI + " -> 发生了异常");
                        }

                        log.info("结束事务: " + finalI);
                    }, executor);
                    futureList.add(future);
                }
                CompletableFuture.allOf(futureList.toArray(new CompletableFuture[0])).join();
            } catch (Exception e) {
                log.error("捕获了异常 -> ", e);
                throw new RuntimeException(e);
            }
            return null;
        });

    }


    /**
     * 主线程未等待子线程结束, 事务不能回滚
     */
    public void multiThreadInsertNotRollback(String id) {
        List<Role> roleList = buildRoleList(id);
        transactionTemplate.execute(transactionStatus -> {
            for (int i = 0; i < roleList.size(); i++) {
                int finalI = i;
                CompletableFuture.runAsync(() -> {
                    System.out.println("开始事务: " + finalI);
                    springBootRoleMapper.insertRole(roleList.get(finalI));

                    if (finalI == 4) {
                        try {
                            Thread.sleep(100);
                        } catch (InterruptedException e) {
                            throw new RuntimeException(e);
                        }
                        throw new RuntimeException(finalI + " -> 发生了异常");
                    }

                    System.out.println("结束事务: " + finalI);
                }, executor);
            }
            return null;
        });

    }

    /**
     * 事务正常回滚
     */
    public void normalMultiInsert(String id) {
        List<Role> roleList = buildRoleList(id);
        transactionTemplate.execute(transactionStatus -> {
            for (int i = 0; i < roleList.size(); i++) {
                if (i == 4) {
                    throw new RuntimeException(i + " -> 发生了异常");
                }
                springBootRoleMapper.insertRole(roleList.get(i));
            }
            return null;
        });
    }

    private List<Role> buildRoleList(String id) {
        List<Role> roleList = new ArrayList<>(10);
        for (int i = 0; i < 10; i++) {
            Role role = new Role();
            role.setRoleName("Transaction" + i);
            role.setNote("Multi Thread Transaction-" + id);
            roleList.add(role);
        }
        return roleList;
    }

}
