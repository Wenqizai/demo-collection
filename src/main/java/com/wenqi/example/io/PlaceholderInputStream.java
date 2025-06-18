package com.wenqi.example.io;

import org.jetbrains.annotations.NotNull;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * 占位输入流 - 在真正的数据流准备好之前阻塞
 * 解决大数据量导出时PipedInputStream被提前关闭的问题
 * <p>
 * 工作原理：
 * 1. downloader立即获得这个占位流开始"读取"
 * 2. 占位流内部会阻塞等待真正的数据流准备完成
 * 3. Excel文件生成完成后，设置真正的FileInputStream
 * 4. 占位流将所有读取操作委托给真正的流
 * <p>
 * 优势：
 * - 避免了PipedInputStream的缓冲区限制
 * - 确保数据完整性，不会出现提前关闭问题
 * - 支持大文件导出，内存占用可控
 * - 自动检测并添加缓冲，提升读取性能
 * 异步 PlaceholderInputStream，防止流被提前关闭
 * @author liangwenqi
 * @date 2025/6/17
 */
public class PlaceholderInputStream extends InputStream {

    private InputStream actualStream = null;
    private Exception error = null;
    private final Object lock = new Object();

    public void setActualStream(InputStream actualStream) {
        synchronized (lock) {
            this.actualStream = wrapWithBufferIfNeeded(actualStream);
            lock.notifyAll();
        }
    }

    public void setError(Exception error) {
        synchronized (lock) {
            this.error = error;
            lock.notifyAll();
        }
    }

    @Override
    public int read() throws IOException {
        waitForActualStream();
        return actualStream.read();
    }

    @Override
    public int read(@NotNull byte[] b, int off, int len) throws IOException {
        waitForActualStream();
        return actualStream.read(b, off, len);
    }

    @Override
    public int available() throws IOException {
        synchronized (lock) {
            if (actualStream != null) {
                return actualStream.available();
            }
            return 0;
        }
    }

    /**
     * 检测输入流是否已经有缓冲，如果没有则用BufferedInputStream包装
     * 这可以显著提升读取性能，特别是对于频繁的小块读取操作
     *
     * @param inputStream 原始输入流
     * @return 带缓冲的输入流
     */
    private InputStream wrapWithBufferIfNeeded(InputStream inputStream) {
        if (inputStream == null) {
            return null;
        }

        // 检查是否已经是缓冲流或其他高性能流
        if (isBufferedStream(inputStream)) {
            return inputStream;
        }

        // 用BufferedInputStream包装，使用默认8192字节缓冲区
        return new BufferedInputStream(inputStream);
    }

    /**
     * 判断输入流是否已经具有缓冲功能
     *
     * @param inputStream 要检查的输入流
     * @return true 如果流已经有缓冲功能
     */
    private boolean isBufferedStream(InputStream inputStream) {
        // BufferedInputStream 已经有缓冲
        if (inputStream instanceof BufferedInputStream) {
            return true;
        }

        // ByteArrayInputStream 在内存中，不需要额外缓冲
        if (inputStream instanceof ByteArrayInputStream) {
            return true;
        }

        // 一些其他已知的高性能流类型
        String className = inputStream.getClass().getSimpleName();
        return className.contains("Buffered") ||
               className.contains("ByteArray") ||
               className.contains("String") ||
               className.equals("PushbackInputStream"); // PushbackInputStream 通常已经有缓冲
    }

    private void waitForActualStream() throws IOException {
        synchronized (lock) {
            long timeoutMs = 10 * 60 * 1000L; // 10分钟超时
            long startTime = System.currentTimeMillis();

            while (actualStream == null && error == null) {
                long remainingTime = timeoutMs - (System.currentTimeMillis() - startTime);

                if (remainingTime <= 0) {
                    throw new IOException("等待数据流超时（10分钟），请稍后重试");
                }

                try {
                    // 等待真正的流准备好或出错，使用剩余时间作为超时
                    lock.wait(remainingTime);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    throw new IOException("等待数据流时被中断", e);
                }

                // 循环会重新检查条件，处理虚假唤醒
            }

            if (error != null) {
                throw new IOException("数据流生成失败", error);
            }

            // 此时 actualStream 必定不为 null，因为循环条件保证了这一点
        }
    }

    @Override
    public void close() throws IOException {
        synchronized (lock) {
            if (actualStream != null) {
                actualStream.close();
            }
        }
    }
}

