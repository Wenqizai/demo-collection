package com.wenqi.example.io.reactor.client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

/**
 * 负载测试客户端
 * 用于测试不同服务器模式的性能表现和可扩展性
 *
 * 负载测试的目标：
 * 1. 测试服务器的并发连接处理能力
 * 2. 对比不同模式（阻塞vs Reactor vs 多线程Reactor）的性能差异
 * 3. 验证Doug Lea论文中关于可扩展性的结论
 * 4. 提供量化的性能指标（响应时间、吞吐量、资源利用率）
 *
 * 测试指标：
 * - 并发连接数 (Concurrent connections)
 * - 请求响应时间 (Request response time)
 * - 成功率 (Success rate)
 * - 吞吐量 (Throughput - requests per second)
 * - 错误率 (Error rate)
 *
 * Load Testing Client
 * Used to test performance and scalability of different server modes
 *
 * Load testing objectives:
 * 1. Test server's concurrent connection handling capability
 * 2. Compare performance differences between different modes (Blocking vs Reactor vs Multi-threaded Reactor)
 * 3. Verify Doug Lea's conclusions about scalability in his paper
 * 4. Provide quantitative performance metrics (response time, throughput, resource utilization)
 *
 * Test metrics:
 * - Concurrent connections
 * - Request response time
 * - Success rate
 * - Throughput (requests per second)
 * - Error rate
 *
 * @author liangwenqi
 * @date 2025/11/16
 */
public class LoadTestClient {

    private final String serverHost;
    private final int serverPort;

    // 性能统计计数器 / Performance statistics counters
    private final AtomicInteger successCount = new AtomicInteger(0);
    private final AtomicInteger errorCount = new AtomicInteger(0);
    private final AtomicLong totalResponseTime = new AtomicLong(0);

    public LoadTestClient(String serverHost, int serverPort) {
        this.serverHost = serverHost;
        this.serverPort = serverPort;
    }

    /**
     * 执行负载测试
     * Execute load test
     */
    public LoadTestResult runLoadTest(int concurrentConnections, int requestsPerConnection, String testMessage) {
        System.out.println("Starting load test...");
        System.out.println("Server: " + serverHost + ":" + serverPort);
        System.out.println("Concurrent connections: " + concurrentConnections);
        System.out.println("Requests per connection: " + requestsPerConnection);
        System.out.println("Test message: " + testMessage);
        System.out.println("----------------------------------------");

        // 重置计数器 / Reset counters
        successCount.set(0);
        errorCount.set(0);
        totalResponseTime.set(0);

        ExecutorService threadPool = Executors.newFixedThreadPool(concurrentConnections);
        CountDownLatch startLatch = new CountDownLatch(1);
        CountDownLatch finishLatch = new CountDownLatch(concurrentConnections);

        long startTime = System.currentTimeMillis();

        // 创建并发连接 / Create concurrent connections
        for (int i = 0; i < concurrentConnections; i++) {
            final int connectionId = i + 1;
            threadPool.submit(() -> {
                try {
                    // 等待统一开始信号
                    // Wait for unified start signal
                    startLatch.await();

                    // 执行测试
                    // Execute test
                    testConnection(connectionId, requestsPerConnection, testMessage);

                } catch (Exception e) {
                    System.err.println("Connection " + connectionId + " failed: " + e.getMessage());
                    errorCount.incrementAndGet();
                } finally {
                    finishLatch.countDown();
                }
            });
        }

        try {
            // 统一开始测试
            // Start test uniformly
            startLatch.countDown();

            // 等待所有连接完成
            // Wait for all connections to complete
            finishLatch.await();

            long endTime = System.currentTimeMillis();

            // 计算结果
            // Calculate results
            long totalTime = endTime - startTime;
            int totalRequests = concurrentConnections * requestsPerConnection;
            int totalSuccess = successCount.get();
            int totalErrors = errorCount.get();
            double avgResponseTime = totalSuccess > 0 ? (double) totalResponseTime.get() / totalSuccess : 0;
            double throughput = (double) totalSuccess / (totalTime / 1000.0);
            double successRate = (double) totalSuccess / totalRequests * 100;

            LoadTestResult result = new LoadTestResult(
                concurrentConnections, requestsPerConnection, totalTime,
                totalSuccess, totalErrors, successRate,
                avgResponseTime, throughput
            );

            // 打印结果
            // Print results
            result.printResult();

            return result;

        } catch (InterruptedException e) {
            System.err.println("Load test interrupted: " + e.getMessage());
            Thread.currentThread().interrupt();
            return null;
        } finally {
            threadPool.shutdown();
        }
    }

    /**
     * 测试单个连接
     * Test single connection
     */
    private void testConnection(int connectionId, int requestCount, String message) {
        try (
            Socket socket = new Socket(serverHost, serverPort);
            PrintWriter writer = new PrintWriter(socket.getOutputStream(), true);
            BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()))
        ) {
            for (int i = 0; i < requestCount; i++) {
                long requestStart = System.currentTimeMillis();

                try {
                    // 发送请求
                    // Send request
                    writer.println(message);

                    // 读取响应
                    // Read response
                    String response = reader.readLine();
                    long responseTime = System.currentTimeMillis() - requestStart;

                    if (response != null && response.equals(message)) {
                        successCount.incrementAndGet();
                        totalResponseTime.addAndGet(responseTime);
                    } else {
                        errorCount.incrementAndGet();
                    }

                } catch (IOException e) {
                    errorCount.incrementAndGet();
                    break;
                }
            }

        } catch (IOException e) {
            errorCount.incrementAndGet();
        }
    }

    /**
     * 负载测试结果类
     * Load test result class
     */
    public static class LoadTestResult {
        public final int concurrentConnections;
        public final int requestsPerConnection;
        public final long totalTestTime; // ms
        public final int successfulRequests;
        public final int failedRequests;
        public final double successRate; // percentage
        public final double averageResponseTime; // ms
        public final double throughput; // requests per second

        public LoadTestResult(int concurrentConnections, int requestsPerConnection,
                             long totalTestTime, int successfulRequests, int failedRequests,
                             double successRate, double averageResponseTime, double throughput) {
            this.concurrentConnections = concurrentConnections;
            this.requestsPerConnection = requestsPerConnection;
            this.totalTestTime = totalTestTime;
            this.successfulRequests = successfulRequests;
            this.failedRequests = failedRequests;
            this.successRate = successRate;
            this.averageResponseTime = averageResponseTime;
            this.throughput = throughput;
        }

        public void printResult() {
            System.out.println("Load Test Results:");
            System.out.println("Concurrent connections: " + concurrentConnections);
            System.out.println("Requests per connection: " + requestsPerConnection);
            System.out.println("Total test time: " + totalTestTime + " ms");
            System.out.println("Successful requests: " + successfulRequests);
            System.out.println("Failed requests: " + failedRequests);
            System.out.println("Success rate: " + String.format("%.2f%%", successRate));
            System.out.println("Average response time: " + String.format("%.2f ms", averageResponseTime));
            System.out.println("Throughput: " + String.format("%.2f requests/sec", throughput));
            System.out.println("----------------------------------------");
        }
    }

    /**
     * 运行多种负载测试场景
     * Run multiple load test scenarios
     */
    public void runMultipleTests(List<TestScenario> scenarios) {
        System.out.println("Starting multiple load tests for " + serverHost + ":" + serverPort);
        System.out.println("========================================");

        for (TestScenario scenario : scenarios) {
            System.out.println("\nTest Scenario: " + scenario.name);
            runLoadTest(scenario.concurrentConnections, scenario.requestsPerConnection, scenario.testMessage);

            // 测试间隔，让服务器有时间恢复
            // Test interval to give server time to recover
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            }
        }
    }

    /**
     * 测试场景配置
     * Test scenario configuration
     */
    public static class TestScenario {
        public final String name;
        public final int concurrentConnections;
        public final int requestsPerConnection;
        public final String testMessage;

        public TestScenario(String name, int concurrentConnections, int requestsPerConnection, String testMessage) {
            this.name = name;
            this.concurrentConnections = concurrentConnections;
            this.requestsPerConnection = requestsPerConnection;
            this.testMessage = testMessage;
        }
    }

    /**
     * 命令行入口
     * Command line entry point
     */
    public static void main(String[] args) {
        String host = "localhost";
        int port = 8080;

        // 解析命令行参数
        // Parse command line arguments
        if (args.length > 0) {
            host = args[0];
        }
        if (args.length > 1) {
            try {
                port = Integer.parseInt(args[1]);
            } catch (NumberFormatException e) {
                System.err.println("Invalid port number: " + args[1]);
                return;
            }
        }

        LoadTestClient loadTester = new LoadTestClient(host, port);

        // 定义测试场景
        // Define test scenarios
        List<TestScenario> scenarios = new ArrayList<>();
        scenarios.add(new TestScenario("Light Load", 10, 5, "Hello Server"));
        scenarios.add(new TestScenario("Medium Load", 50, 10, "Test Message"));
        scenarios.add(new TestScenario("Heavy Load", 100, 5, "Heavy Load Test"));
        scenarios.add(new TestScenario("Stress Test", 200, 3, "Stress Message"));

        // 运行测试
        // Run tests
        loadTester.runMultipleTests(scenarios);

        System.out.println("\nLoad testing completed!");
        System.out.println("Note: Different server modes will show different performance characteristics:");
        System.out.println("- Blocking Server: Limited scalability, thread resource constraints");
        System.out.println("- Single-threaded Reactor: Good scalability but business logic blocks I/O");
        System.out.println("- Multi-threaded Reactor: Best scalability and performance");
    }

    /**
     * 性能测试的注意事项：
     * Performance testing considerations:
     *
     * 1. 测试环境一致性 (Test Environment Consistency):
     *    - 确保测试环境的一致性
     *    - Ensure test environment consistency
     *    - 避免其他程序干扰测试结果
     *    - Avoid other programs interfering with test results
     *
     * 2. 服务器预热 (Server Warm-up):
     *    - 先运行轻负载测试让服务器预热
     *    - Run light load tests first to warm up the server
     *    - JVM预热会影响初始性能
     *    - JVM warm-up affects initial performance
     *
     * 3. 结果分析 (Result Analysis):
     *    - 关注成功率，不能只看吞吐量
     *    - Focus on success rate, not just throughput
     *    - 考察响应时间的稳定性
     *    - Examine response time stability
     *    - 多次测试取平均值
     *    - Take average of multiple tests
     *
     * 4. 资源监控 (Resource Monitoring):
     *    - 监控CPU和内存使用情况
     *    - Monitor CPU and memory usage
     *    - 观察线程数量变化
     *    - Observe thread count changes
     *    - 注意系统资源限制
     *    - Pay attention to system resource limits
     */
}