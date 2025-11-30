package com.wenqi.example.io.reactor.reactor;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.util.Iterator;
import java.util.Set;

/**
 * Reactor模式的核心实现
 * 基于Doug Lea《Scalable IO in Java》论文中的设计
 *
 * Reactor模式的核心理念：
 * 1. 事件分发：使用Selector实现I/O多路复用
 * 2. 非阻塞处理：所有I/O操作都是非阻塞的
 * 3. 单线程调度：一个线程负责所有事件的分发和调度
 * 4. 回调机制：通过Handler接口实现事件处理
 *
 * Reactor的核心组件：
 * - Selector：I/O多路复用器，监听I/O事件
 * - SelectionKey：事件键，关联Channel和Handler
 * - Handler：事件处理器，实现具体的业务逻辑
 * - Acceptor：连接接收器，处理新连接建立
 *
 * Core Implementation of Reactor Pattern
 * Based on the design in Doug Lea's "Scalable IO in Java" paper
 *
 * Core concepts of Reactor pattern:
 * 1. Event dispatching: Use Selector to implement I/O multiplexing
 * 2. Non-blocking processing: All I/O operations are non-blocking
 * 3. Single-threaded scheduling: One thread handles all event dispatching and scheduling
 * 4. Callback mechanism: Implement event handling through Handler interface
 *
 * Core components of Reactor:
 * - Selector: I/O multiplexer, monitors I/O events
 * - SelectionKey: Event key, associates Channel and Handler
 * - Handler: Event handler, implements specific business logic
 * - Acceptor: Connection acceptor, handles new connection establishment
 *
 * @author liangwenqi
 * @date 2025/11/16
 */
public class Reactor implements Runnable {

    private final Selector selector;
    private final ServerSocketChannel serverSocketChannel;
    private volatile boolean running = true;

    public Reactor(int port) throws IOException {
        // 创建Selector，用于I/O多路复用
        // Create Selector for I/O multiplexing
        this.selector = Selector.open();

        // 创建ServerSocketChannel，用于监听连接
        // Create ServerSocketChannel for listening connections
        this.serverSocketChannel = ServerSocketChannel.open();
        serverSocketChannel.bind(new InetSocketAddress(port));
        serverSocketChannel.configureBlocking(false);

        // 创建Acceptor处理器，处理新连接
        // Create Acceptor handler to handle new connections
        Acceptor acceptor = new Acceptor(serverSocketChannel, selector);

        // 将ServerSocketChannel注册到Selector，关注ACCEPT事件
        // Register ServerSocketChannel to Selector, interested in ACCEPT events
        serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT, acceptor);

        System.out.println("Reactor Server started on port " + port);
        System.out.println("Using single-threaded Reactor pattern (non-blocking I/O)");
    }

    @Override
    public void run() {
        System.out.println("Reactor event loop started...");

        try {
            while (running) {
                // 阻塞等待I/O事件，设置超时避免无限阻塞
                // Block waiting for I/O events, set timeout to avoid infinite blocking
                int readyChannels = selector.select(1000);

                if (readyChannels == 0) {
                    // 没有就绪的通道，继续循环
                    // No ready channels, continue loop
                    continue;
                }

                // 获取所有就绪的SelectionKey
                // Get all ready SelectionKeys
                Set<SelectionKey> selectedKeys = selector.selectedKeys();
                Iterator<SelectionKey> iterator = selectedKeys.iterator();

                // 遍历所有就绪的事件
                // Iterate through all ready events
                while (iterator.hasNext()) {
                    SelectionKey key = iterator.next();
                    iterator.remove(); // 从集合中移除，避免重复处理

                    try {
                        // 分发事件到对应的Handler
                        // Dispatch event to corresponding Handler
                        dispatch(key);
                    } catch (Exception e) {
                        System.err.println("Error dispatching event: " + e.getMessage());
                        handleException(key, e);
                    }
                }
            }
        } catch (IOException e) {
            System.err.println("Reactor error: " + e.getMessage());
        } finally {
            shutdown();
        }
    }

    /**
     * 事件分发器
     * 根据事件的类型，调用相应Handler的处理方法
     *
     * Event dispatcher
     * Calls appropriate handler's processing method based on event type
     */
    private void dispatch(SelectionKey key) throws Exception {
        // 获取与SelectionKey关联的Handler
        // Get Handler associated with SelectionKey
        Object attachment = key.attachment();

        if (attachment instanceof Handler) {
            Handler handler = (Handler) attachment;

            // 检查Handler是否仍然有效
            // Check if Handler is still valid
            if (handler.isValid()) {
                handler.handle(key);
            } else {
                System.out.println("Invalid handler, cancelling key...");
                key.cancel();
            }
        }
    }

    /**
     * 异常处理
     * 清理资源并关闭连接
     *
     * Exception handling
     * Clean up resources and close connection
     */
    private void handleException(SelectionKey key, Exception ex) {
        try {
            if (key != null) {
                key.cancel();
                if (key.channel() != null) {
                    key.channel().close();
                }
            }
        } catch (Exception e) {
            System.err.println("Error closing resources: " + e.getMessage());
        }
    }

    /**
     * 停止Reactor
     * Stop Reactor
     */
    public void stop() {
        running = false;
        selector.wakeup(); // 唤醒selector，使其退出阻塞 / Wake up selector to exit blocking
    }

    /**
     * 关闭所有资源
     * Close all resources
     */
    private void shutdown() {
        System.out.println("Shutting down Reactor...");
        try {
            // 关闭所有注册的通道
            // Close all registered channels
            for (SelectionKey key : selector.keys()) {
                if (key.channel() != null) {
                    key.channel().close();
                }
            }

            // 关闭Selector
            // Close Selector
            selector.close();

            // 关闭ServerSocketChannel
            // Close ServerSocketChannel
            serverSocketChannel.close();

            System.out.println("Reactor shutdown complete.");
        } catch (IOException e) {
            System.err.println("Error during shutdown: " + e.getMessage());
        }
    }

    /**
     * 单线程Reactor模式的性能特点：
     * Performance characteristics of single-threaded Reactor pattern:
     *
     * 优点 (Advantages):
     * - 高效的事件分发，单线程避免锁竞争
     * - Efficient event dispatching, single thread avoids lock contention
     * - 内存使用少，无线程创建开销
     * - Low memory usage, no thread creation overhead
     * - 实现简单，逻辑清晰
     * - Simple implementation, clear logic
     * - 适合I/O密集型应用
     * - Suitable for I/O-intensive applications
     *
     * 缺点 (Disadvantages):
     * - 业务逻辑处理会阻塞事件循环
     * - Business logic processing blocks event loop
     * - 无法充分利用多核CPU
     * - Cannot fully utilize multi-core CPU
     * - 单个连接的慢速处理会影响所有连接
     * - Slow processing of single connection affects all connections
     *
     * 适用场景 (Use cases):
     * - 连接数多但业务逻辑简单的场景
     * - Scenarios with many connections but simple business logic
     * - 协议解析和数据转发
     * - Protocol parsing and data forwarding
     * - 聊天服务器、游戏服务器等
     * - Chat servers, game servers, etc.
     */
}