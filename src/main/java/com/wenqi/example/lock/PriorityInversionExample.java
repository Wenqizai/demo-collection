package com.wenqi.example.lock;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * 优先级反转示例
 * 
 * 优先级反转是指：当高优先级线程等待被低优先级线程持有的锁时，
 * 而这个低优先级线程又被中优先级线程抢占CPU时间，导致高优先级线程无法执行的情况。
 * 
 * 这个例子模拟了一个简单的优先级反转场景：
 * 1. 低优先级线程获取共享资源锁
 * 2. 高优先级线程尝试获取同一个锁，但被阻塞
 * 3. 中优先级线程执行，抢占低优先级线程的CPU时间
 * 4. 高优先级线程被迫等待中优先级线程完成，然后等低优先级线程完成并释放锁
 */
public class PriorityInversionExample {
    
    private static final Lock sharedResource = new ReentrantLock();
    private static final Object monitor = new Object();
    
    // 控制线程启动顺序和执行状态
    private static volatile boolean lowPriorityStarted = false;
    private static volatile boolean lowPriorityHasLock = false;
    private static volatile boolean highPriorityWaiting = false;
    private static volatile boolean mediumPriorityDone = false;
    
    public static void main(String[] args) {
        
        // 创建低优先级线程
        Thread lowPriorityThread = new Thread(() -> {
            System.out.println("低优先级线程启动...");
            lowPriorityStarted = true;
            
            try {
                sharedResource.lock(); // 获取共享资源锁
                lowPriorityHasLock = true;
                System.out.println("低优先级线程获取了锁");
                
                // 等待高优先级线程尝试获取锁
                while (!highPriorityWaiting) {
                    Thread.sleep(10);
                }
                
                System.out.println("低优先级线程执行关键区域...");
                
                // 模拟低优先级线程执行工作
                // 此时，低优先级线程可能被中优先级线程抢占
                for (int i = 0; i < 5; i++) {
                    System.out.println("低优先级线程工作中..." + i);
                    Thread.sleep(100);
                }
                
                System.out.println("低优先级线程完成工作并释放锁");
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            } finally {
                sharedResource.unlock(); // 释放锁
            }
        }, "低优先级线程");
        
        // 设置最低优先级
        lowPriorityThread.setPriority(Thread.MIN_PRIORITY);
        
        // 创建高优先级线程
        Thread highPriorityThread = new Thread(() -> {
            System.out.println("高优先级线程启动...");
            
            // 等待低优先级线程先获取锁
            try {
                while (!lowPriorityHasLock) {
                    Thread.sleep(10);
                }
                
                System.out.println("高优先级线程尝试获取锁...");
                highPriorityWaiting = true;
                
                long startWaitTime = System.currentTimeMillis();
                
                sharedResource.lock(); // 尝试获取已被低优先级线程持有的锁，会被阻塞
                try {
                    long endWaitTime = System.currentTimeMillis();
                    System.out.println("高优先级线程获取到锁，等待了 " + 
                            (endWaitTime - startWaitTime) + " 毫秒");
                    
                    // 检查在等待期间中优先级线程是否完成了工作
                    System.out.println("在高优先级线程等待期间，中优先级线程是否完成: " + 
                            mediumPriorityDone);
                    
                    // 模拟高优先级线程工作
                    System.out.println("高优先级线程执行关键区域...");
                    Thread.sleep(50);
                } finally {
                    sharedResource.unlock();
                    System.out.println("高优先级线程释放锁");
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }, "高优先级线程");
        
        // 设置最高优先级
        highPriorityThread.setPriority(Thread.MAX_PRIORITY);
        
        // 创建中优先级线程
        Thread mediumPriorityThread = new Thread(() -> {
            System.out.println("中优先级线程启动...");
            
            // 等待低优先级线程启动并且高优先级线程开始等待
            try {
                while (!highPriorityWaiting) {
                    Thread.sleep(10);
                }
                
                System.out.println("中优先级线程开始执行计算密集任务...");
                
                // 模拟计算密集型工作，抢占低优先级线程的CPU时间
                long sum = 0;
                for (int i = 0; i < 10; i++) {
                    sum = 0;
                    for (long j = 0; j < 100_000_000L; j++) {
                        sum += j;
                    }
                    System.out.println("中优先级线程计算中... " + i);
                }
                
                System.out.println("中优先级线程完成工作：" + sum);
                mediumPriorityDone = true;
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }, "中优先级线程");
        
        // 设置中等优先级
        mediumPriorityThread.setPriority(Thread.NORM_PRIORITY);
        
        // 启动线程
        lowPriorityThread.start();
        
        // 等待低优先级线程启动
        try {
            while (!lowPriorityStarted) {
                Thread.sleep(10);
            }
            Thread.sleep(100); // 额外等待一点时间，确保低优先级线程能获取锁
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        
        highPriorityThread.start();
        mediumPriorityThread.start();
        
        // 等待所有线程完成
        try {
            lowPriorityThread.join();
            highPriorityThread.join();
            mediumPriorityThread.join();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        
        System.out.println("\n总结：这个例子展示了优先级反转的问题。" +
                "尽管高优先级线程应该优先执行，但它被阻塞等待低优先级线程释放锁。" +
                "同时，低优先级线程被中优先级线程抢占CPU时间，" +
                "导致高优先级线程必须等待比它优先级更低的线程完成工作。");
        
        System.out.println("\n解决方案：");
        System.out.println("1. 优先级继承：当低优先级线程持有高优先级线程需要的锁时，临时提升其优先级");
        System.out.println("2. 优先级上限：为所有共享资源访问设置相同的高优先级");
        System.out.println("3. 避免长时间持有锁：减少临界区大小，尽快释放锁");
        System.out.println("4. 使用无锁算法：考虑使用不需要锁的并发数据结构");
    }
} 