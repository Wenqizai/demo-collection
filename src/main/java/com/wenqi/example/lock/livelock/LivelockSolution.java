package com.wenqi.example.lock.livelock;

import java.util.Random;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * 这个示例展示如何通过添加随机性来解决活锁问题
 */
public class LivelockSolution {
    
    private static final Random random = new Random();

    static class Resource {
        private final Lock lock = new ReentrantLock();
        private String name;
        
        public Resource(String name) {
            this.name = name;
        }
        
        public String getName() {
            return name;
        }
        
        public boolean tryAcquire(Worker worker, Resource otherResource) {
            try {
                // 添加随机性到获取尝试中
                int waitTime = random.nextInt(100) + 10;
                
                // 尝试获取此资源的锁
                if (lock.tryLock(waitTime, TimeUnit.MILLISECONDS)) {
                    System.out.println(worker.getName() + " 获取到了 " + name);
                    
                    // 尝试获取另一个资源的锁
                    if (otherResource.lock.tryLock(waitTime, TimeUnit.MILLISECONDS)) {
                        System.out.println(worker.getName() + " 获取到了 " + otherResource.getName());
                        return true; // 成功获取两个资源
                    } else {
                        System.out.println(worker.getName() + " 无法获取 " + otherResource.getName());
                        lock.unlock(); // 释放第一个资源
                        System.out.println(worker.getName() + " 释放了 " + name);
                        // 随机延迟后重试
                        Thread.sleep(random.nextInt(50));
                        return false;
                    }
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
            
            return false;
        }
        
        public void release(Worker worker, Resource otherResource) {
            otherResource.lock.unlock();
            System.out.println(worker.getName() + " 释放了 " + otherResource.getName());
            lock.unlock();
            System.out.println(worker.getName() + " 释放了 " + name);
        }
    }
    
    static class Worker {
        private String name;
        private Resource firstResource;
        private Resource secondResource;
        
        public Worker(String name, Resource firstResource, Resource secondResource) {
            this.name = name;
            this.firstResource = firstResource;
            this.secondResource = secondResource;
        }
        
        public String getName() {
            return name;
        }
        
        public void work() {
            while (true) {
                if (firstResource.tryAcquire(this, secondResource)) {
                    try {
                        // 使用这两个资源执行一些工作
                        System.out.println(name + " 正在使用两个资源工作");
                        Thread.sleep(100);
                        break; // 成功完成工作
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                    } finally {
                        firstResource.release(this, secondResource);
                    }
                }
                
                // 添加随机退避以防止立即重试
                try {
                    int backoff = random.nextInt(100);
                    System.out.println(name + " 退避 " + backoff + "ms");
                    Thread.sleep(backoff);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }
            
            System.out.println(name + " 已完成任务");
        }
    }
    
    public static void main(String[] args) {
        final Resource resourceA = new Resource("资源A");
        final Resource resourceB = new Resource("资源B");
        
        final Worker worker1 = new Worker("工作者1", resourceA, resourceB);
        final Worker worker2 = new Worker("工作者2", resourceB, resourceA);
        
        new Thread(() -> worker1.work()).start();
        new Thread(() -> worker2.work()).start();
    }
} 