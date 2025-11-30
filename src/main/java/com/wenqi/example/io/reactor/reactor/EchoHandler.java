package com.wenqi.example.io.reactor.reactor;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;
import java.nio.charset.StandardCharsets;

/**
 * Echo协议处理器实现
 * 将接收到的数据原样返回给客户端
 *
 * Echo协议的特点：
 * 1. 简单：客户端发送什么，服务器就返回什么
 * 2. 无状态：每次请求都是独立的
 * 3. 适合学习：是网络编程的经典示例
 *
 * 非阻塞I/O处理的要点：
 * 1. 使用ByteBuffer进行数据的读写操作
 * 2. 处理读写不完整的情况（部分读写）
 * 3. 正确处理连接关闭和异常情况
 * 4. 合理管理ByteBuffer的分配和释放
 *
 * Echo Protocol Handler Implementation
 * Returns received data back to client as-is
 *
 * Characteristics of Echo protocol:
 * 1. Simple: Server returns whatever client sends
 * 2. Stateless: Each request is independent
 * 3. Good for learning: Classic example of network programming
 *
 * Key points of non-blocking I/O handling:
 * 1. Use ByteBuffer for read/write operations
 * 2. Handle incomplete read/write situations (partial reads/writes)
 * 3. Properly handle connection closing and exceptions
 * 4. Properly manage ByteBuffer allocation and release
 *
 * @author liangwenqi
 * @date 2025/11/16
 */
public class EchoHandler implements Handler {

    private final SocketChannel socketChannel;
    private final ByteBuffer readBuffer;
    private final ByteBuffer writeBuffer;

    public EchoHandler(SocketChannel socketChannel) {
        this.socketChannel = socketChannel;
        // 分配读写缓冲区，大小为1024字节
        // Allocate read/write buffers with size of 1024 bytes
        this.readBuffer = ByteBuffer.allocate(1024);
        this.writeBuffer = ByteBuffer.allocate(1024);
    }

    @Override
    public void handle(SelectionKey key) throws Exception {
        try {
            if (key.isReadable()) {
                handleRead(key);
            }
            if (key.isWritable()) {
                handleWrite(key);
            }
        } catch (IOException e) {
            System.err.println("Error handling connection: " + e.getMessage());
            key.cancel();
            socketChannel.close();
        }
    }

    /**
     * 处理读事件
     * 从SocketChannel读取数据到readBuffer
     *
     * Handle read events
     * Read data from SocketChannel to readBuffer
     */
    private void handleRead(SelectionKey key) throws IOException {
        readBuffer.clear(); // 清空缓冲区，准备读取数据 / Clear buffer for reading
        int bytesRead = socketChannel.read(readBuffer);

        if (bytesRead == -1) {
            // 客户端关闭连接
            // Client closed connection
            System.out.println("Client disconnected: " + socketChannel.getRemoteAddress());
            key.cancel();
            socketChannel.close();
            return;
        }

        if (bytesRead > 0) {
            // 切换到读模式，准备处理数据
            // Switch to read mode, prepare to process data
            readBuffer.flip();

            // 将数据从readBuffer复制到writeBuffer，准备回显
            // Copy data from readBuffer to writeBuffer for echo
            writeBuffer.clear();
            writeBuffer.put(readBuffer);
            writeBuffer.flip();

            // 读取客户端发送的消息并打印
            // Read message from client and print
            byte[] data = new byte[bytesRead];
            readBuffer.rewind(); // 重置位置，准备完整读取 / Reset position for complete reading
            readBuffer.get(data);
            String message = new String(data, StandardCharsets.UTF_8);
            System.out.println("Received from " + socketChannel.getRemoteAddress() + ": " + message);

            // 检查是否是退出命令
            // Check if it's a quit command
            if ("quit".equalsIgnoreCase(message.trim())) {
                System.out.println("Client " + socketChannel.getRemoteAddress() + " requested to quit");
                key.cancel();
                socketChannel.close();
                return;
            }

            // 注册写事件，准备回显数据
            // Register write event to prepare echo data
            key.interestOps(SelectionKey.OP_WRITE);
        }
    }

    /**
     * 处理写事件
     * 将writeBuffer中的数据写入SocketChannel
     *
     * Handle write events
     * Write data from writeBuffer to SocketChannel
     */
    private void handleWrite(SelectionKey key) throws IOException {
        int bytesWritten = socketChannel.write(writeBuffer);

        if (bytesWritten > 0) {
            System.out.println("Echoed " + bytesWritten + " bytes to " + socketChannel.getRemoteAddress());
        }

        if (!writeBuffer.hasRemaining()) {
            // 数据全部写入完成，重新注册读事件
            // All data written, re-register read event
            writeBuffer.clear();
            key.interestOps(SelectionKey.OP_READ);
        } else {
            // 还有数据未写入，继续监听写事件
            // Still have data to write, continue listening for write events
            key.interestOps(SelectionKey.OP_WRITE);
        }
    }

    @Override
    public SocketChannel getChannel() {
        return socketChannel;
    }

    @Override
    public int getInterestedOps() {
        return SelectionKey.OP_READ; // 初始时只对读事件感兴趣 / Initially only interested in read events
    }

    /**
     * EchoHandler的设计要点：
     * Design key points of EchoHandler:
     *
     * 1. 缓冲区管理 (Buffer Management):
     *    - 使用两个独立的缓冲区分别处理读写
     *    - 避免读写操作的相互干扰
     *    - 正确处理缓冲区的切换和重置
     *
     * 2. 事件处理流程 (Event Handling Flow):
     *    - 读事件：读取数据 -> 准备回显 -> 注册写事件
     *    - 写事件：写入数据 -> 检查完成 -> 重新注册读事件
     *
     * 3. 异常处理 (Exception Handling):
     *    - 正确处理客户端断开连接的情况
     *    - 资源清理和连接关闭
     *    - 异常情况下的优雅退出
     *
     * 4. 协议支持 (Protocol Support):
     *    - 支持"quit"命令优雅退出
     *    - 支持UTF-8编码的文本数据
     *    - 处理部分读写的情况
     */
}