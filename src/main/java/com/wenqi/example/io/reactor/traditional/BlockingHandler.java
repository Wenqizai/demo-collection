package com.wenqi.example.io.reactor.traditional;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

/**
 * 传统阻塞I/O模式的处理器
 * 每个连接需要一个独立的线程来处理
 *
 * 传统模式的局限性：
 * 1. 线程资源浪费：每个连接占用一个线程，大部分时间线程处于阻塞状态
 * 2. 可扩展性差：线程数量受操作系统限制，无法支持大量并发连接
 * 3. 内存开销大：每个线程需要独立的栈空间（通常1-2MB）
 * 4. 上下文切换频繁：大量线程导致频繁的CPU上下文切换
 *
 * Traditional Blocking I/O Handler
 * Each connection requires a dedicated thread for processing
 *
 * Limitations of traditional approach:
 * 1. Thread resource waste: Each connection occupies a thread, most of the time threads are blocked
 * 2. Poor scalability: Thread count limited by OS, cannot support large concurrent connections
 * 3. High memory overhead: Each thread needs independent stack space (usually 1-2MB)
 * 4. Frequent context switching: Large number of threads cause frequent CPU context switches
 *
 * @author liangwenqi
 * @date 2025/11/16
 */
public class BlockingHandler implements Runnable {

    private final Socket clientSocket;

    public BlockingHandler(Socket clientSocket) {
        this.clientSocket = clientSocket;
    }

    @Override
    public void run() {
        try (
            BufferedReader reader = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            PrintWriter writer = new PrintWriter(clientSocket.getOutputStream(), true)
        ) {
            String inputLine;
            // 传统模式：线程在这里阻塞等待客户端数据
            // Traditional mode: Thread blocks here waiting for client data
            while ((inputLine = reader.readLine()) != null) {
                System.out.println("Thread " + Thread.currentThread().getId() +
                    " received: " + inputLine);

                // Echo回显：将接收到的数据原样返回给客户端
                // Echo: Return received data back to client as-is
                writer.println(inputLine);

                // 如果客户端发送"quit"，则关闭连接
                // Close connection if client sends "quit"
                if ("quit".equalsIgnoreCase(inputLine)) {
                    break;
                }
            }
        } catch (IOException e) {
            System.err.println("Error handling client connection: " + e.getMessage());
        } finally {
            try {
                clientSocket.close();
                System.out.println("Thread " + Thread.currentThread().getId() +
                    " closed connection: " + clientSocket.getRemoteSocketAddress());
            } catch (IOException e) {
                System.err.println("Error closing client socket: " + e.getMessage());
            }
        }
    }
}