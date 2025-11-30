package com.wenqi.example.io.reactor.reactor;

import java.io.IOException;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;

/**
 * 连接接收器
 * 专门处理OP_ACCEPT事件，负责接受新连接并创建相应的处理器
 *
 * Acceptor的设计模式：
 * 1. 单一职责：只处理连接建立，不处理业务逻辑
 * 2. 工厂模式：为新连接创建对应的Handler
 * 3. 事件驱动：基于Selector的ACCEPT事件触发
 *
 * Connection Acceptor
 * Specializes in handling OP_ACCEPT events, responsible for accepting new connections
 * and creating corresponding handlers
 *
 * Design pattern of Acceptor:
 * 1. Single responsibility: Only handles connection establishment, not business logic
 * 2. Factory pattern: Creates handlers for new connections
 * 3. Event-driven: Triggered by Selector's ACCEPT events
 *
 * @author liangwenqi
 * @date 2025/11/16
 */
public class Acceptor implements Handler {

    private final ServerSocketChannel serverSocketChannel;
    private final Selector selector;

    public Acceptor(ServerSocketChannel serverSocketChannel, Selector selector) {
        this.serverSocketChannel = serverSocketChannel;
        this.selector = selector;
    }

    @Override
    public void handle(SelectionKey key) throws Exception {
        if (!key.isAcceptable()) {
            return; // 只处理ACCEPT事件 / Only handle ACCEPT events
        }

        try {
            // 接受新的客户端连接
            // Accept new client connection
            SocketChannel clientChannel = serverSocketChannel.accept();
            if (clientChannel != null) {
                System.out.println("New client connected: " + clientChannel.getRemoteAddress());

                // 设置为非阻塞模式
                // Set to non-blocking mode
                clientChannel.configureBlocking(false);

                // 为新连接创建EchoHandler
                // Create EchoHandler for new connection
                EchoHandler handler = new EchoHandler(clientChannel);

                // 将新连接注册到Selector，关注READ事件
                // Register new connection to Selector, interested in READ events
                clientChannel.register(selector, SelectionKey.OP_READ, handler);

                // 打印当前连接统计信息
                // Print current connection statistics
                printConnectionStats();
            }
        } catch (IOException e) {
            System.err.println("Error accepting connection: " + e.getMessage());
            throw e;
        }
    }

    /**
     * 打印当前连接统计信息
     * Print current connection statistics
     */
    private void printConnectionStats() {
        int connectionCount = 0;
        for (SelectionKey key : selector.keys()) {
            if (key.channel() instanceof SocketChannel) {
                connectionCount++;
            }
        }
        System.out.println("Current connections: " + connectionCount);
    }

    @Override
    public ServerSocketChannel getChannel() {
        return serverSocketChannel;
    }

    @Override
    public int getInterestedOps() {
        return SelectionKey.OP_ACCEPT;
    }

    /**
     * Acceptor的工作流程：
     * Workflow of Acceptor:
     *
     * 1. 等待ACCEPT事件 (Wait for ACCEPT events):
     *    - Reactor检测到新连接时触发
     *    - 调用handle方法处理连接建立
     *
     * 2. 建立连接 (Establish connection):
     *    - 调用ServerSocketChannel.accept()
     *    - 获取SocketChannel对象
     *
     * 3. 配置连接 (Configure connection):
     *    - 设置为非阻塞模式
     *    - 配置TCP选项（如果需要）
     *
     * 4. 创建处理器 (Create handler):
     *    - 根据业务需求创建对应的Handler
     *    - 这里创建EchoHandler实现Echo协议
     *
     * 5. 注册事件 (Register events):
     *    - 将新连接注册到Selector
     *    - 设置初始兴趣事件（通常是OP_READ）
     *    - 绑定Handler对象到SelectionKey
     *
     * Acceptor vs 传统模式对比：
     * Acceptor vs Traditional mode comparison:
     *
     * 传统模式 (Traditional mode):
     * - 每个连接需要创建一个线程
     * - 线程在accept()处阻塞等待
     * - 资源消耗与连接数成正比
     *
     * Reactor模式 (Reactor mode):
     * - 一个线程处理所有连接的建立
     * - 基于事件驱动的非阻塞处理
     * - 资源消耗与活跃连接数相关
     */
}