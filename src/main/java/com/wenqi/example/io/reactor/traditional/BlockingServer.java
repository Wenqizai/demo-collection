package com.wenqi.example.io.reactor.traditional;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * 传统阻塞I/O服务器实现
 * 这是Doug Lea论文中作为对比基准的传统模式
 *
 * 传统模式的工作原理：
 * 1. 主线程在ServerSocket.accept()处阻塞，等待新连接
 * 2. 每个新连接创建一个新线程进行处理
 * 3. 每个处理线程在自己的生命周期内专门处理一个连接
 *
 * 性能瓶颈分析：
 * - 线程数量 = 连接数量，无法有效利用系统资源
 * - 大部分时间线程处于I/O等待状态，CPU利用率低
 * - 线程创建和销毁开销大
 * - 系统资源消耗与连接数成正比
 *
 * Traditional Blocking I/O Server Implementation
 * This is the traditional pattern used as a baseline comparison in Doug Lea's paper
 *
 * Working principle of traditional mode:
 * 1. Main thread blocks at ServerSocket.accept() waiting for new connections
 * 2. Each new connection creates a new thread for processing
 * 3. Each handler thread handles one connection throughout its lifecycle
 *
 * Performance bottleneck analysis:
 * - Thread count = connection count, cannot effectively utilize system resources
 * - Most threads are in I/O waiting state, low CPU utilization
 * - High overhead of thread creation and destruction
 * - System resource consumption proportional to connection count
 *
 * @author liangwenqi
 * @date 2025/11/16
 */
public class BlockingServer {

    private final int port;
    private final ExecutorService threadPool;
    private volatile boolean running = true;

    public BlockingServer(int port) {
        this.port = port;
        // 使用固定大小的线程池来控制最大线程数
        // Use fixed-size thread pool to control maximum thread count
        this.threadPool = Executors.newFixedThreadPool(100);
    }

    public void start() {
        System.out.println("Starting Traditional Blocking Server on port " + port);
        System.out.println("This server creates one thread per connection (traditional approach)");
        System.out.println("Thread Pool Size: 100 (maximum concurrent connections)");

        try (ServerSocket serverSocket = new ServerSocket(port)) {
            System.out.println("Server is listening on port " + port);

            while (running) {
                try {
                    // 主线程阻塞等待新连接
                    // Main thread blocks waiting for new connections
                    Socket clientSocket = serverSocket.accept();
                    System.out.println("New client connected: " + clientSocket.getRemoteSocketAddress());

                    // 为每个连接创建一个处理线程
                    // Create a handler thread for each connection
                    BlockingHandler handler = new BlockingHandler(clientSocket);
                    threadPool.execute(handler);

                    // 显示当前活跃线程数
                    // Display current active thread count
                    showThreadStats();

                } catch (IOException e) {
                    if (running) {
                        System.err.println("Error accepting client connection: " + e.getMessage());
                    }
                }
            }
        } catch (IOException e) {
            System.err.println("Could not start server on port " + port + ": " + e.getMessage());
        } finally {
            shutdown();
        }
    }

    /**
     * 显示当前线程池状态
     * Display current thread pool status
     */
    private void showThreadStats() {
        if (threadPool instanceof java.util.concurrent.ThreadPoolExecutor) {
            java.util.concurrent.ThreadPoolExecutor executor =
                (java.util.concurrent.ThreadPoolExecutor) threadPool;
            System.out.println("Thread Pool Stats - Active: " + executor.getActiveCount() +
                ", Queue: " + executor.getQueue().size() +
                ", Completed: " + executor.getCompletedTaskCount());
        }
    }

    public void stop() {
        running = false;
        shutdown();
    }

    private void shutdown() {
        System.out.println("Shutting down server...");
        threadPool.shutdown();
        try {
            if (!threadPool.awaitTermination(5, java.util.concurrent.TimeUnit.SECONDS)) {
                threadPool.shutdownNow();
            }
            System.out.println("Server shutdown complete.");
        } catch (InterruptedException e) {
            threadPool.shutdownNow();
            Thread.currentThread().interrupt();
        }
    }

    /**
     * 传统模式服务器的性能特点：
     * Performance characteristics of traditional mode server:
     *
     * 优点 (Advantages):
     * - 实现简单直观 (Simple and intuitive implementation)
     * - 每个连接独立处理，不会相互影响 (Each connection handled independently)
     * - 调试相对容易 (Relatively easy to debug)
     *
     * 缺点 (Disadvantages):
     * - 无法支持大量并发连接 (Cannot support large concurrent connections)
     * - 资源消耗大 (High resource consumption)
     * - 扩展性差 (Poor scalability)
     * - CPU利用率低 (Low CPU utilization)
     *
     * 适用场景 (Use cases):
     * - 连接数较少的应用 (Applications with few connections)
     * - 简单的内部服务 (Simple internal services)
     * - 学习和原型开发 (Learning and prototype development)
     */
}