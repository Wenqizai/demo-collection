package com.wenqi.example.io.reactor.client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Scanner;

/**
 * Echo客户端实现
 * 用于测试不同模式的服务器功能
 *
 * 客户端的功能特点：
 * 1. 支持交互式输入，可以与服务器进行实时对话
 * 2. 自动显示服务器返回的响应
 * 3. 支持"quit"命令优雅退出
 * 4. 可以测试连接建立、数据传输和连接关闭的完整流程
 *
 * 使用方式：
 * 1. 启动任意类型的服务器（BlockingServer、Reactor、ThreadPoolReactor）
 * 2. 运行EchoClient连接到服务器
 * 3. 在控制台输入消息，观察服务器的回显响应
 * 4. 输入"quit"退出连接
 *
 * Echo Client Implementation
 * Used to test the functionality of different server modes
 *
 * Client features:
 * 1. Supports interactive input for real-time conversation with server
 * 2. Automatically displays server responses
 * 3. Supports "quit" command for graceful exit
 * 4. Can test complete flow of connection establishment, data transmission, and connection closure
 *
 * Usage:
 * 1. Start any type of server (BlockingServer, Reactor, ThreadPoolReactor)
 * 2. Run EchoClient to connect to the server
 * 3. Input messages in console to observe server echo responses
 * 4. Input "quit" to exit connection
 *
 * @author liangwenqi
 * @date 2025/11/16
 */
public class EchoClient {

    private final String serverHost;
    private final int serverPort;

    public EchoClient(String serverHost, int serverPort) {
        this.serverHost = serverHost;
        this.serverPort = serverPort;
    }

    /**
     * 启动客户端
     * Start client
     */
    public void start() {
        System.out.println("Starting Echo Client...");
        System.out.println("Connecting to server: " + serverHost + ":" + serverPort);

        try (
            Socket socket = new Socket(serverHost, serverPort);
            PrintWriter writer = new PrintWriter(socket.getOutputStream(), true);
            BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            Scanner scanner = new Scanner(System.in)
        ) {
            System.out.println("Connected to server successfully!");
            System.out.println("Type messages and press Enter to send (type 'quit' to exit):");

            // 启动一个线程来接收服务器的响应
            // Start a thread to receive server responses
            Thread responseThread = new Thread(() -> {
                try {
                    String response;
                    while ((response = reader.readLine()) != null) {
                        System.out.println("Server response: " + response);

                        // 如果收到"quit"响应，客户端也应该退出
                        // If receive "quit" response, client should also exit
                        if ("quit".equalsIgnoreCase(response)) {
                            System.out.println("Server requested connection close");
                            System.exit(0);
                        }
                    }
                } catch (IOException e) {
                    if (!socket.isClosed()) {
                        System.err.println("Error reading server response: " + e.getMessage());
                    }
                }
            });

            responseThread.setDaemon(true);
            responseThread.start();

            // 主线程处理用户输入
            // Main thread handles user input
            while (scanner.hasNextLine()) {
                String userInput = scanner.nextLine().trim();

                if (userInput.isEmpty()) {
                    continue;
                }

                // 发送消息到服务器
                // Send message to server
                writer.println(userInput);
                System.out.println("Sent to server: " + userInput);

                // 检查是否要退出
                // Check if should exit
                if ("quit".equalsIgnoreCase(userInput)) {
                    break;
                }
            }

            System.out.println("Client shutting down...");

        } catch (UnknownHostException e) {
            System.err.println("Unknown host: " + serverHost);
            System.err.println("Please check if the server host is correct.");
        } catch (IOException e) {
            System.err.println("Error connecting to server: " + e.getMessage());
            System.err.println("Please make sure the server is running on " + serverHost + ":" + serverPort);
        }

        System.out.println("Client disconnected.");
    }

    /**
     * 发送单条消息并等待响应
     * 主要用于自动化测试
     *
     * Send single message and wait for response
     * Mainly used for automated testing
     */
    public String sendMessage(String message) {
        try (
            Socket socket = new Socket(serverHost, serverPort);
            PrintWriter writer = new PrintWriter(socket.getOutputStream(), true);
            BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()))
        ) {
            writer.println(message);
            return reader.readLine();
        } catch (IOException e) {
            System.err.println("Error sending message: " + e.getMessage());
            return null;
        }
    }

    /**
     * 测试服务器连接
     * Test server connection
     */
    public boolean testConnection() {
        try {
            Socket socket = new Socket(serverHost, serverPort);
            boolean connected = socket.isConnected();
            socket.close();
            return connected;
        } catch (IOException e) {
            return false;
        }
    }

    /**
     * 命令行入口
     * Command line entry point
     */
    public static void main(String[] args) {
        // 默认连接本地服务器
        // Connect to local server by default
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
                System.err.println("Usage: java EchoClient [host] [port]");
                return;
            }
        }

        EchoClient client = new EchoClient(host, port);

        // 先测试连接是否可用
        // First test if connection is available
        if (!client.testConnection()) {
            System.err.println("Cannot connect to server at " + host + ":" + port);
            System.err.println("Please make sure the server is running.");
            return;
        }

        // 启动客户端
        // Start client
        client.start();
    }

    /**
     * EchoClient的使用示例：
     * EchoClient usage examples:
     *
     * 1. 测试阻塞服务器 (Test blocking server):
     *    java BlockingServer 8080
     *    java EchoClient localhost 8080
     *
     * 2. 测试单线程Reactor服务器 (Test single-threaded Reactor server):
     *    java Reactor 8080
     *    java EchoClient localhost 8080
     *
     * 3. 测试多线程Reactor服务器 (Test multi-threaded Reactor server):
     *    java ThreadPoolReactor 8080 4
     *    java EchoClient localhost 8080
     *
     * 测试场景 (Test scenarios):
     * - 基本Echo功能 (Basic Echo functionality)
     * - 长消息处理 (Long message handling)
     * - 快速连续发送 (Rapid consecutive sending)
     * - 连接关闭处理 (Connection closure handling)
     * - 并发连接测试 (Concurrent connection testing)
     */
}