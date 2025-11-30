package com.wenqi.example.io.reactor.reactor;

import java.nio.channels.SelectableChannel;
import java.nio.channels.SelectionKey;

/**
 * Reactor模式中的事件处理器接口
 * 定义了I/O事件处理的统一契约
 *
 * Reactor模式的核心思想：
 * 1. 事件驱动：基于I/O事件触发处理逻辑
 * 2. 非阻塞：使用NIO实现非阻塞I/O操作
 * 3. 单线程分发：一个Reactor线程负责所有事件的分发
 *
 * Event Handler Interface in Reactor Pattern
 * Defines the unified contract for I/O event handling
 *
 * Core ideas of Reactor pattern:
 * 1. Event-driven: Processing logic triggered by I/O events
 * 2. Non-blocking: Use NIO to implement non-blocking I/O operations
 * 3. Single-thread dispatch: One Reactor thread handles all event dispatching
 *
 * @author liangwenqi
 * @date 2025/11/16
 */
public interface Handler {

    /**
     * 处理I/O事件
     * 当通道准备好进行I/O操作时，Reactor会调用此方法
     *
     * Handle I/O events
     * Reactor calls this method when channel is ready for I/O operations
     *
     * @param key 选择键，包含通道和就绪操作类型
     *            Selection key containing channel and ready operation types
     * @throws Exception 处理过程中可能抛出的异常
     *                   Exceptions that may be thrown during processing
     */
    void handle(SelectionKey key) throws Exception;

    /**
     * 获取处理器关联的通道
     * 用于注册到Selector
     *
     * Get the channel associated with this handler
     * Used for registration with Selector
     *
     * @return 可选择的通道
     *         Selectable channel
     */
    SelectableChannel getChannel();

    /**
     * 获取感兴趣的操作类型
     * 用于向Selector注册感兴趣的事件
     *
     * Get the operation types of interest
     * Used to register events of interest with Selector
     *
     * @return 操作类型的位掩码（OP_ACCEPT, OP_READ, OP_WRITE等）
     *         Bit mask of operation types (OP_ACCEPT, OP_READ, OP_WRITE, etc.)
     */
    int getInterestedOps();

    /**
     * 处理异常情况
     * 当I/O操作出现异常时的清理工作
     *
     * Handle exception situations
     * Cleanup work when I/O operations encounter exceptions
     *
     * @param key 出现异常的选择键
     *            Selection key where exception occurred
     * @param ex  异常对象
     *            Exception object
     */
    default void handleException(SelectionKey key, Exception ex) {
        System.err.println("Handler exception: " + ex.getMessage());
        try {
            if (key != null && key.channel() != null) {
                key.channel().close();
            }
        } catch (Exception e) {
            System.err.println("Error closing channel: " + e.getMessage());
        }
    }

    /**
     * 检查处理器是否仍然有效
     * 用于判断是否需要继续监听事件
     *
     * Check if handler is still valid
     * Used to determine whether to continue listening for events
     *
     * @return true表示继续监听，false表示可以停止监听
     *         true to continue listening, false to stop listening
     */
    default boolean isValid() {
        return true;
    }
}