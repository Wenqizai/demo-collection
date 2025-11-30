package com.wenqi.example.io.reactor;

import com.wenqi.example.io.reactor.client.EchoClient;
import com.wenqi.example.io.reactor.client.LoadTestClient;
import com.wenqi.example.io.reactor.reactor.Reactor;
import com.wenqi.example.io.reactor.reactor.ThreadPoolReactor;
import com.wenqi.example.io.reactor.traditional.BlockingServer;

import java.util.Scanner;

/**
 * Reactor模式演示主类
 * 提供统一的入口来运行和测试所有三种I/O模式
 *
 * 演示目标：
 * 1. 展示三种模式的完整实现
 * 2. 提供交互式测试界面
 * 3. 支持性能对比测试
 * 4. 便于学习和理解不同模式的差异
 *
 * Reactor Pattern Demo Main Class
 * Provides unified entry point to run and test all three I/O modes
 *
 * Demo objectives:
 * 1. Show complete implementations of three modes
 * 2. Provide interactive testing interface
 * 3. Support performance comparison testing
 * 4. Facilitate learning and understanding of differences between modes
 *
 * @author liangwenqi
 * @date 2025/11/16
 */
public class ReactorDemo {

    public static void main(String[] args) {
        System.out.println("=== Doug Lea《Scalable IO in Java》完整复现演示 ===");
        System.out.println("=== Complete Reproduction Demo of Doug Lea's 'Scalable IO in Java' ===");
        System.out.println();

        Scanner scanner = new Scanner(System.in);

        while (true) {
            showMenu();
            System.out.print("请选择模式 (1-5): Please select mode (1-5): ");

            try {
                int choice = Integer.parseInt(scanner.nextLine().trim());

                switch (choice) {
                    case 1:
                        startBlockingServer(scanner);
                        break;
                    case 2:
                        startSingleThreadReactor(scanner);
                        break;
                    case 3:
                        startMultiThreadReactor(scanner);
                        break;
                    case 4:
                        runPerformanceComparison(scanner);
                        break;
                    case 5:
                        System.out.println("退出演示 / Exiting demo");
                        scanner.close();
                        return;
                    default:
                        System.out.println("无效选择，请重新输入 / Invalid choice, please try again");
                }
            } catch (NumberFormatException e) {
                System.out.println("请输入数字 / Please enter a number");
            } catch (Exception e) {
                System.err.println("错误 / Error: " + e.getMessage());
            }

            System.out.println();
        }
    }

    private static void showMenu() {
        System.out.println("┌─────────────────────────────────────────────────────────┐");
        System.out.println("│               Reactor Pattern Demo Menu                │");
        System.out.println("├─────────────────────────────────────────────────────────┤");
        System.out.println("│ 1. 传统阻塞I/O模式 / Traditional Blocking I/O Mode      │");
        System.out.println("│ 2. 单线程Reactor模式 / Single-threaded Reactor Mode    │");
        System.out.println("│ 3. 多线程Reactor模式 / Multi-threaded Reactor Mode      │");
        System.out.println("│ 4. 性能对比测试 / Performance Comparison Test          │");
        System.out.println("│ 5. 退出 / Exit                                         │");
        System.out.println("└─────────────────────────────────────────────────────────┘");
    }

    private static void startBlockingServer(Scanner scanner) {
        System.out.println("\n=== 启动传统阻塞I/O服务器 / Starting Traditional Blocking I/O Server ===");
        System.out.print("输入端口号 (默认8080) / Enter port (default 8080): ");
        String portStr = scanner.nextLine().trim();
        int port = portStr.isEmpty() ? 8080 : Integer.parseInt(portStr);

        new Thread(() -> {
            try {
                BlockingServer server = new BlockingServer(port);
                server.start();
            } catch (Exception e) {
                System.err.println("服务器启动失败 / Server startup failed: " + e.getMessage());
            }
        }, "BlockingServer").start();

        System.out.println("传统阻塞服务器已启动在端口 " + port + " / Traditional blocking server started on port " + port);
        System.out.println("可以使用EchoClient进行测试 / Can use EchoClient for testing");

        waitForEnter(scanner);
    }

    private static void startSingleThreadReactor(Scanner scanner) {
        System.out.println("\n=== 启动单线程Reactor服务器 / Starting Single-threaded Reactor Server ===");
        System.out.print("输入端口号 (默认8081) / Enter port (default 8081): ");
        String portStr = scanner.nextLine().trim();
        int port = portStr.isEmpty() ? 8081 : Integer.parseInt(portStr);

        new Thread(() -> {
            try {
                Reactor reactor = new Reactor(port);
                reactor.run();
            } catch (Exception e) {
                System.err.println("Reactor启动失败 / Reactor startup failed: " + e.getMessage());
            }
        }, "SingleThreadReactor").start();

        System.out.println("单线程Reactor服务器已启动在端口 " + port + " / Single-threaded Reactor server started on port " + port);
        System.out.println("可以使用EchoClient进行测试 / Can use EchoClient for testing");

        waitForEnter(scanner);
    }

    private static void startMultiThreadReactor(Scanner scanner) {
        System.out.println("\n=== 启动多线程Reactor服务器 / Starting Multi-threaded Reactor Server ===");
        System.out.print("输入端口号 (默认8082) / Enter port (default 8082): ");
        String portStr = scanner.nextLine().trim();
        int port = portStr.isEmpty() ? 8082 : Integer.parseInt(portStr);

        System.out.print("输入工作线程数 (默认4) / Enter worker threads (default 4): ");
        String workerStr = scanner.nextLine().trim();
        int workers = workerStr.isEmpty() ? 4 : Integer.parseInt(workerStr);

        new Thread(() -> {
            try {
                ThreadPoolReactor reactor = new ThreadPoolReactor(port, workers);
                reactor.run();
            } catch (Exception e) {
                System.err.println("多线程Reactor启动失败 / Multi-threaded Reactor startup failed: " + e.getMessage());
            }
        }, "MultiThreadReactor").start();

        System.out.println("多线程Reactor服务器已启动在端口 " + port + "，工作线程数: " + workers + " / Multi-threaded Reactor server started on port " + port + " with " + workers + " worker threads");
        System.out.println("可以使用EchoClient进行测试 / Can use EchoClient for testing");

        waitForEnter(scanner);
    }

    private static void runPerformanceComparison(Scanner scanner) {
        System.out.println("\n=== 性能对比测试 / Performance Comparison Test ===");
        System.out.println("这个测试需要先启动不同模式的服务器 / This test requires starting servers in different modes first");
        System.out.println("建议端口配置 / Suggested port configuration:");
        System.out.println("  - 传统阻塞服务器: 8080 / Traditional blocking server: 8080");
        System.out.println("  - 单线程Reactor: 8081 / Single-threaded Reactor: 8081");
        System.out.println("  - 多线程Reactor: 8082 / Multi-threaded Reactor: 8082");

        System.out.print("是否继续性能测试? (y/n) / Continue with performance test? (y/n): ");
        String choice = scanner.nextLine().trim().toLowerCase();

        if (!choice.startsWith("y")) {
            return;
        }

        System.out.print("输入服务器地址 (默认localhost) / Enter server address (default localhost): ");
        String host = scanner.nextLine().trim();
        if (host.isEmpty()) host = "localhost";

        System.out.print("输入服务器端口 (默认8080) / Enter server port (default 8080): ");
        String portStr = scanner.nextLine().trim();
        int port = portStr.isEmpty() ? 8080 : Integer.parseInt(portStr);

        try {
            LoadTestClient loadTester = new LoadTestClient(host, port);

            System.out.println("开始性能测试 / Starting performance test...");
            LoadTestClient.LoadTestResult result = loadTester.runLoadTest(
                50,    // 50个并发连接 / 50 concurrent connections
                10,    // 每连接10个请求 / 10 requests per connection
                "Performance Test Message"
            );

            if (result != null) {
                System.out.println("测试完成! / Test completed!");
            }
        } catch (Exception e) {
            System.err.println("性能测试失败 / Performance test failed: " + e.getMessage());
        }

        waitForEnter(scanner);
    }

    private static void waitForEnter(Scanner scanner) {
        System.out.print("\n按回车键返回主菜单 / Press Enter to return to main menu: ");
        scanner.nextLine();
    }

    /**
     * 使用示例 / Usage Examples:
     *
     * 1. 测试单个服务器模式 / Test single server mode:
     *    java ReactorDemo
     *    选择模式1/2/3
     *    然后另开终端: java EchoClient localhost <port>
     *
     * 2. 性能对比测试 / Performance comparison test:
     *    java ReactorDemo
     *    选择模式4
     *    需要先启动对应的服务器
     *
     * 3. 手动测试 / Manual testing:
     *    分别启动三个服务器:
     *    java BlockingServer 8080
     *    java Reactor 8081
     *    java ThreadPoolReactor 8082 4
     *
     *    然后分别进行性能测试:
     *    java LoadTestClient localhost 8080
     *    java LoadTestClient localhost 8081
     *    java LoadTestClient localhost 8082
     */
}