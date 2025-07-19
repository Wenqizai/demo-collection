package com.wenqi.example.lock.livelock;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * 这个类演示了在数据库事务重试机制中可能发生的活锁场景
 */
public class LivelockTransactionExample {

    // 模拟数据库记录
    static class BankAccount {
        private final Lock lock = new ReentrantLock();
        private final String accountId;
        private int balance;
        private final AtomicInteger version = new AtomicInteger(0); // 乐观锁
        
        public BankAccount(String accountId, int initialBalance) {
            this.accountId = accountId;
            this.balance = initialBalance;
        }
        
        public String getAccountId() {
            return accountId;
        }
        
        public boolean lock() {
            return lock.tryLock();
        }
        
        public void unlock() {
            lock.unlock();
        }
        
        public int getBalance() {
            return balance;
        }
        
        public int getVersion() {
            return version.get();
        }
        
        // 模拟乐观锁事务
        public boolean updateBalance(int amount, int expectedVersion) {
            // 检查版本是否匹配（没有并发更新）
            if (version.get() == expectedVersion) {
                balance += amount;
                // 更新版本号，表示记录已经变更
                version.incrementAndGet();
                return true;
            }
            return false; // 版本不匹配，说明另一个事务修改了记录
        }
    }
    
    // 模拟事务管理器
    static class TransactionManager {
        private static final int MAX_RETRIES = 10;
        
        public static boolean transfer(BankAccount from, BankAccount to, int amount) {
            for (int attempt = 0; attempt < MAX_RETRIES; attempt++) {
                // 开始事务
                System.out.println(Thread.currentThread().getName() + 
                    " - 第 " + (attempt + 1) + " 次尝试从 " + from.getAccountId() + 
                    " 转账 " + amount + " 元到 " + to.getAccountId());
                
                // 读取记录
                int fromVersion = from.getVersion();
                int fromBalance = from.getBalance();
                int toVersion = to.getVersion();
                int toBalance = to.getBalance();
                
                // 验证
                if (fromBalance < amount) {
                    System.out.println("账户 " + from.getAccountId() + " 余额不足");
                    return false;
                }
                
                // 没有合适的重试策略可能导致活锁
                try {
                    // 尝试按特定顺序获取两个锁
                    if (from.lock()) {
                        try {
                            Thread.sleep(50); // 模拟处理时间
                            
                            if (to.lock()) {
                                try {
                                    // 尝试更新两个账户
                                    boolean fromUpdated = from.updateBalance(-amount, fromVersion);
                                    boolean toUpdated = to.updateBalance(amount, toVersion);
                                    
                                    if (fromUpdated && toUpdated) {
                                        System.out.println(Thread.currentThread().getName() + 
                                            " - 成功从 " + from.getAccountId() + 
                                            " 转账 " + amount + " 元到 " + to.getAccountId());
                                        return true;
                                    } else {
                                        // 一个或两个更新因为版本不匹配而失败
                                        System.out.println(Thread.currentThread().getName() + 
                                            " - 事务因并发修改而失败");
                                        // 在重试前稍作休眠
                                        Thread.sleep(10 * attempt); // 线性退避
                                    }
                                } finally {
                                    to.unlock();
                                }
                            } else {
                                System.out.println(Thread.currentThread().getName() + 
                                    " - 无法锁定目标账户 " + to.getAccountId());
                                Thread.sleep(10); // 稍等片刻再重试
                            }
                        } finally {
                            from.unlock();
                        }
                    } else {
                        System.out.println(Thread.currentThread().getName() + 
                            " - 无法锁定源账户 " + from.getAccountId());
                        Thread.sleep(10); // 稍等片刻再重试
                    }
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    return false;
                }
            }
            
            System.out.println(Thread.currentThread().getName() + 
                " - 经过 " + MAX_RETRIES + " 次尝试后转账失败");
            return false;
        }
    }
    
    public static void main(String[] args) {
        final BankAccount accountA = new BankAccount("A", 1000);
        final BankAccount accountB = new BankAccount("B", 1000);
        
        // 两个线程尝试在相同的账户之间相反方向转账
        Thread t1 = new Thread(() -> {
            TransactionManager.transfer(accountA, accountB, 500);
        }, "线程-1");
        
        Thread t2 = new Thread(() -> {
            TransactionManager.transfer(accountB, accountA, 300);
        }, "线程-2");
        
        t1.start();
        t2.start();
        
        try {
            t1.join();
            t2.join();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        
        System.out.println("最终余额：");
        System.out.println("账户 A: " + accountA.getBalance() + " 元");
        System.out.println("账户 B: " + accountB.getBalance() + " 元");
    }
} 