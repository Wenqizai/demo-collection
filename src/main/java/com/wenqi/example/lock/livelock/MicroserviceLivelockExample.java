package com.wenqi.example.lock.livelock;

import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 这个类演示了微服务架构中当服务持续重试失败请求而没有适当退避策略时如何发生活锁
 */
public class MicroserviceLivelockExample {

    private static final Random random = new Random();
    
    // 跟踪系统整体负载
    private static AtomicInteger systemLoad = new AtomicInteger(0);
    private static final int LOAD_THRESHOLD = 10;
    
    // 模拟微服务架构中的一个服务
    static class MicroService {
        private final String name;
        private final MicroService[] dependencies;
        private final int maxRetries;
        private final boolean usesExponentialBackoff;
        
        public MicroService(String name, int maxRetries, boolean usesExponentialBackoff, MicroService... dependencies) {
            this.name = name;
            this.maxRetries = maxRetries;
            this.usesExponentialBackoff = usesExponentialBackoff;
            this.dependencies = dependencies;
        }
        
        public String getName() {
            return name;
        }
        
        // 模拟处理依赖于其他服务的请求
        public boolean handleRequest() {
            System.out.println("服务 " + name + " 正在处理请求...");
            
            // 先检查依赖
            for (MicroService dependency : dependencies) {
                if (!callDependency(dependency)) {
                    return false;
                }
            }
            
            // 依赖处理完后处理请求
            try {
                // 模拟实际处理
                Thread.sleep(50);
                System.out.println("服务 " + name + " 成功处理请求");
                return true;
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                return false;
            }
        }
        
        private boolean callDependency(MicroService dependency) {
            int currentLoad = systemLoad.incrementAndGet();
            System.out.println("系统负载: " + currentLoad);
            
            // 如果系统过载，服务开始失败
            boolean systemOverloaded = currentLoad > LOAD_THRESHOLD;
            
            try {
                for (int attempt = 0; attempt < maxRetries; attempt++) {
                    // 如果系统过载，失败概率更高
                    boolean willSucceed = !systemOverloaded || random.nextInt(100) > 75;
                    
                    if (willSucceed) {
                        System.out.println("服务 " + name + " 成功调用 " + dependency.getName());
                        return true;
                    } else {
                        System.out.println("服务 " + name + " 调用 " + dependency.getName() + 
                                " 失败 (第 " + (attempt + 1) + " 次尝试)");
                        
                        if (attempt < maxRetries - 1) {
                            // 这里如果没有适当的退避策略就会发生活锁
                            if (usesExponentialBackoff) {
                                // 指数退避加随机抖动
                                long backoffTime = (long) (Math.pow(2, attempt) * 50 + random.nextInt(50));
                                System.out.println("服务 " + name + " 退避 " + backoffTime + "ms");
                                Thread.sleep(backoffTime);
                            } else {
                                // 固定延迟重试 - 可能导致活锁
                                System.out.println("服务 " + name + " 立即重试");
                                Thread.sleep(10);
                            }
                        }
                    }
                }
                
                System.out.println("服务 " + name + " 在 " + maxRetries + 
                        " 次尝试后无法调用 " + dependency.getName());
                return false;
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                return false;
            } finally {
                systemLoad.decrementAndGet();
            }
        }
    }
    
    public static void main(String[] args) {
        // 创建具有依赖关系的微服务链
        final MicroService serviceD = new MicroService("服务-D", 3, true);
        final MicroService serviceC = new MicroService("服务-C", 3, true, serviceD);
        final MicroService serviceB = new MicroService("服务-B", 3, false, serviceC); // 没有退避策略
        final MicroService serviceA = new MicroService("服务-A", 3, true, serviceB);
        
        // 模拟多个客户端同时发出请求
        for (int i = 0; i < 5; i++) {
            final int clientId = i;
            new Thread(() -> {
                System.out.println("客户端 " + clientId + " 发送请求");
                boolean success = serviceA.handleRequest();
                System.out.println("客户端 " + clientId + " 请求完成，状态: " + 
                        (success ? "成功" : "失败"));
            }, "客户端-" + i).start();
            
            // 客户端之间有小延迟
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
        
        // 在延迟后启动另一组客户端，模拟流量突增
        try {
            Thread.sleep(300);
            System.out.println("\n=== 流量突增发生 ===\n");
            
            for (int i = 5; i < 10; i++) {
                final int clientId = i;
                new Thread(() -> {
                    System.out.println("客户端 " + clientId + " 发送请求");
                    boolean success = serviceA.handleRequest();
                    System.out.println("客户端 " + clientId + " 请求完成，状态: " + 
                            (success ? "成功" : "失败"));
                }, "客户端-" + i).start();
                
                // 流量突增期间客户端请求更频繁
                Thread.sleep(50);
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
} 