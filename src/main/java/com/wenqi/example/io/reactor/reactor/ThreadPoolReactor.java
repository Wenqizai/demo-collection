package com.wenqi.example.io.reactor.reactor;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * 多线程Reactor实现
 * 基于Doug Lea论文中的最优方案：I/O线程与业务处理线程分离
 * <p>
 * 多线程Reactor的设计理念：
 * 1. I/O线程：专门处理I/O事件（连接建立、数据读写）
 * 2. 业务线程池：专门处理业务逻辑（协议解析、业务计算）
 * 3. 职责分离：避免业务逻辑阻塞I/O事件循环
 * 4. 可扩展性：充分利用多核CPU的处理能力
 * <p>
 * 架构模式：
 * ┌─────────────┐    ┌─────────────┐    ┌─────────────┐
 * │   Client    │    │   Reactor   │    │ Worker Pool │
 * │   Connections│───▶│  (I/O Thread)│───▶│(Business Logic)│
 * └─────────────┘    └─────────────┘    └─────────────┘
 * <p>
 * Multi-threaded Reactor Implementation
 * Based on the optimal solution from Doug Lea's paper: separation of I/O and business logic threads
 * <p>
 * Design philosophy of multi-threaded Reactor:
 * 1. I/O thread:专门处理I/O事件（连接建立、数据读写）
 * 2. Business thread pool:专门处理业务逻辑（协议解析、业务计算）
 * 3. Responsibility separation: avoid business logic blocking I/O event loop
 * 4. Scalability: fully utilize multi-core CPU processing capability
 * <p>
 * Architecture pattern:
 * ┌─────────────┐    ┌─────────────┐    ┌─────────────┐
 * │   Client    │    │   Reactor   │    │ Worker Pool │
 * │   Connections│───▶│  (I/O Thread)│───▶│(Business Logic)│
 * └─────────────┘    └─────────────┘    └─────────────┘
 *
 * @author liangwenqi
 * @date 2025/11/16
 */
public class ThreadPoolReactor implements Runnable {

    private final Selector selector;
    private final ServerSocketChannel serverSocketChannel;
    private final ExecutorService workerPool;
    private volatile boolean running = true;

    public ThreadPoolReactor(int port, int workerThreads) throws IOException {
        // 创建Selector用于I/O多路复用
        // Create Selector for I/O multiplexing
        this.selector = Selector.open();

        // 创建ServerSocketChannel监听连接
        // Create ServerSocketChannel for listening connections
        this.serverSocketChannel = ServerSocketChannel.open();
        serverSocketChannel.bind(new InetSocketAddress(port));
        serverSocketChannel.configureBlocking(false);

        // 创建业务处理线程池
        // Create business processing thread pool
        this.workerPool = Executors.newFixedThreadPool(workerThreads);

        // 创建多线程版本的Acceptor
        // Create multi-threaded version of Acceptor
        MultiThreadedAcceptor acceptor = new MultiThreadedAcceptor(serverSocketChannel, selector, workerPool);

        // 注册ACCEPT事件
        // Register ACCEPT events
        serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT, acceptor);

        System.out.println("ThreadPoolReactor Server started on port " + port);
        System.out.println("Using " + workerThreads + " worker threads for business logic");
        System.out.println("I/O thread handles network operations, worker threads handle business logic");
    }

    @Override
    public void run() {
        System.out.println("ThreadPoolReactor event loop started...");

        try {
            while (running) {
                // I/O线程阻塞等待事件
                // I/O thread blocks waiting for events
                int readyChannels = selector.select(1000);

                if (readyChannels == 0) {
                    continue;
                }

                // 处理就绪的I/O事件
                // Process ready I/O events
                Set<SelectionKey> selectedKeys = selector.selectedKeys();
                Iterator<SelectionKey> iterator = selectedKeys.iterator();

                while (iterator.hasNext()) {
                    SelectionKey key = iterator.next();
                    iterator.remove();

                    try {
                        dispatch(key);
                    } catch (Exception e) {
                        System.err.println("Error dispatching event: " + e.getMessage());
                        handleException(key, e);
                    }
                }
            }
        } catch (IOException e) {
            System.err.println("ThreadPoolReactor error: " + e.getMessage());
        } finally {
            shutdown();
        }
    }

    /**
     * 事件分发器
     * I/O线程只负责事件分发，不处理具体业务逻辑
     * <p>
     * Event dispatcher
     * I/O thread only handles event dispatching, not specific business logic
     */
    private void dispatch(SelectionKey key) throws Exception {
        Object attachment = key.attachment();

        if (attachment instanceof MultiThreadedHandler) {
            MultiThreadedHandler handler = (MultiThreadedHandler) attachment;

            if (handler.isValid()) {
                // I/O线程处理，业务逻辑交给工作线程池
                // I/O thread processes, business logic handed to worker thread pool
                handler.handle(key, workerPool);
            } else {
                key.cancel();
            }
        }
    }

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

    public void stop() {
        running = false;
        selector.wakeup();
    }

    private void shutdown() {
        System.out.println("Shutting down ThreadPoolReactor...");
        try {
            for (SelectionKey key : selector.keys()) {
                if (key.channel() != null) {
                    key.channel().close();
                }
            }
            selector.close();
            serverSocketChannel.close();

            workerPool.shutdown();
            if (!workerPool.awaitTermination(5, java.util.concurrent.TimeUnit.SECONDS)) {
                workerPool.shutdownNow();
            }

            System.out.println("ThreadPoolReactor shutdown complete.");
        } catch (Exception e) {
            System.err.println("Error during shutdown: " + e.getMessage());
        }
    }

    /**
     * 多线程版本的Acceptor
     * Multi-threaded version of Acceptor
     */
    private static class MultiThreadedAcceptor implements MultiThreadedHandler {
        private final ServerSocketChannel serverSocketChannel;
        private final Selector selector;
        private final ExecutorService workerPool;

        MultiThreadedAcceptor(ServerSocketChannel serverSocketChannel, Selector selector, ExecutorService workerPool) {
            this.serverSocketChannel = serverSocketChannel;
            this.selector = selector;
            this.workerPool = workerPool;
        }

        @Override
        public void handle(SelectionKey key, ExecutorService workerPool) throws Exception {
            if (key.isAcceptable()) {
                SocketChannel clientChannel = serverSocketChannel.accept();
                if (clientChannel != null) {
                    System.out.println("New client connected: " + clientChannel.getRemoteAddress());
                    clientChannel.configureBlocking(false);

                    // 创建多线程版本的EchoHandler
                    // Create multi-threaded version of EchoHandler
                    MultiThreadedEchoHandler handler = new MultiThreadedEchoHandler(clientChannel);
                    clientChannel.register(selector, SelectionKey.OP_READ, handler);
                }
            }
        }

        @Override
        public ServerSocketChannel getChannel() {
            return serverSocketChannel;
        }

        @Override
        public int getInterestedOps() {
            return SelectionKey.OP_ACCEPT;
        }

        @Override
        public boolean isValid() {
            return true;
        }
    }

    /**
     * 多线程版本的EchoHandler
     * Multi-threaded version of EchoHandler
     */
    private static class MultiThreadedEchoHandler implements MultiThreadedHandler {
        private final SocketChannel socketChannel;

        MultiThreadedEchoHandler(SocketChannel socketChannel) {
            this.socketChannel = socketChannel;
        }

        @Override
        public void handle(SelectionKey key, ExecutorService workerPool) throws Exception {
            if (key.isReadable()) {
                // I/O线程处理数据读取
                // I/O thread handles data reading
                handleRead(key, workerPool);
            }
        }

        private void handleRead(SelectionKey key, ExecutorService workerPool) {
            workerPool.submit(() -> {
                try {
                    java.nio.ByteBuffer buffer = java.nio.ByteBuffer.allocate(1024);
                    int bytesRead = socketChannel.read(buffer);

                    if (bytesRead == -1) {
                        // 客户端断开连接
                        // Client disconnected
                        System.out.println("Client disconnected: " + socketChannel.getRemoteAddress());
                        key.cancel();
                        socketChannel.close();
                        return;
                    }

                    if (bytesRead > 0) {
                        buffer.flip();
                        byte[] data = new byte[bytesRead];
                        buffer.get(data);
                        String message = new String(data, java.nio.charset.StandardCharsets.UTF_8);

                        System.out.println("Received from " + socketChannel.getRemoteAddress() + ": " + message);

                        if ("quit".equalsIgnoreCase(message.trim())) {
                            System.out.println("Client " + socketChannel.getRemoteAddress() + " requested to quit");
                            key.cancel();
                            socketChannel.close();
                            return;
                        }

                        // 业务逻辑处理（这里就是简单的Echo）
                        // Business logic processing (simple Echo here)
                        String response = message;

                        // 写入响应数据
                        // Write response data
                        java.nio.ByteBuffer writeBuffer = java.nio.ByteBuffer.wrap(response.getBytes(java.nio.charset.StandardCharsets.UTF_8));
                        while (writeBuffer.hasRemaining()) {
                            socketChannel.write(writeBuffer);
                        }

                        System.out.println("Echoed to " + socketChannel.getRemoteAddress() + ": " + response);
                    }
                } catch (IOException e) {
                    System.err.println("Error handling read: " + e.getMessage());
                    try {
                        key.cancel();
                        socketChannel.close();
                    } catch (IOException ex) {
                        System.err.println("Error closing channel: " + ex.getMessage());
                    }
                }
            });
        }

        @Override
        public SocketChannel getChannel() {
            return socketChannel;
        }

        @Override
        public int getInterestedOps() {
            return SelectionKey.OP_READ;
        }

        @Override
        public boolean isValid() {
            return socketChannel.isConnected();
        }
    }

    /**
     * 多线程Reactor的优势：
     * Advantages of multi-threaded Reactor:
     *
     * 1. 性能优化 (Performance Optimization):
     *    - I/O线程专注于网络操作，不会被业务逻辑阻塞
     *    - I/O thread focuses on network operations, won't be blocked by business logic
     *    - 业务线程池充分利用多核CPU处理能力
     *    - Business thread pool fully utilizes multi-core CPU processing capability
     *
     * 2. 可扩展性 (Scalability):
     *    - 支持大量并发连接
     *    - Supports large number of concurrent connections
     *    - 可以根据业务复杂度调整线程池大小
     *    - Can adjust thread pool size based on business complexity
     *
     * 3. 响应性 (Responsiveness):
     *    - 即使某个连接的业务处理很慢，也不会影响其他连接
     *    - Even if one connection's business processing is slow, it won't affect others
     *    - I/O事件能够及时处理
     *    - I/O events can be handled timely
     *
     * 4. 资源利用率 (Resource Utilization):
     *    - 线程数量可控，避免线程过多导致的资源浪费
     *    - Controllable thread count, avoiding resource waste from too many threads
     *    - 合理的负载均衡
     *    - Proper load balancing
     */
}

/**
 * 多线程版本的Handler接口
 * Multi-threaded version of Handler interface
 */
interface MultiThreadedHandler {
    void handle(SelectionKey key, ExecutorService workerPool) throws Exception;

    java.nio.channels.SelectableChannel getChannel();

    int getInterestedOps();

    boolean isValid();
}