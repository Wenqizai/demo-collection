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
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionException;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionTemplate;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * @author liangwenqi
 * @date 2023/8/16
 */
@Slf4j
@Service
public class MultiThreadTransaction {
    private final ThreadPoolExecutor executor = new ThreadPoolExecutor(2, 10, 10, TimeUnit.SECONDS, new LinkedBlockingQueue<>(100), Executors.defaultThreadFactory(), new ThreadPoolExecutor.CallerRunsPolicy());

    @Autowired
    private SpringBootRoleMapper springBootRoleMapper;
    @Autowired
    private TransactionTemplate transactionTemplate;
    @Autowired
    private TransactionDefinition transactionDefinition;
    @Autowired
    private DataSourceTransactionManager transactionManager;
    @Autowired
    private SqlSessionTemplate sqlSessionTemplate;

    public void multiThreadInsert(String id) {
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
                TransactionStatus transaction = transactionManager.getTransaction(transactionDefinition);
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
            transactionStatuses.forEach(transactionStatus -> transactionManager.rollback(transactionStatus));
        } else {
            transactionStatuses.forEach(transactionStatus -> transactionManager.commit(transactionStatus));
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
        TransactionStatus transaction = transactionManager.getTransaction(transactionDefinition);
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
            transactionManager.rollback(transaction);
        } else {
            transactionManager.commit(transaction);
        }
    }


    /**
     * 不能回滚事务, 怀疑这一句有点问题
     * CompletableFuture.allOf(futureList.toArray(new CompletableFuture[0])).join();
     */
    public void multiThreadInsertWaitButNotRollback2(String id) {
        List<Role> roleList = buildRoleList(id);
        TransactionStatus transaction = transactionManager.getTransaction(transactionDefinition);
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
            transactionManager.rollback(transaction);
        } else {
            transactionManager.commit(transaction);
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
