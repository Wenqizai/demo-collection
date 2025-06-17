package com.wenqi.example.io;

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
            this.actualStream = actualStream;
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
    public int read(byte[] b, int off, int len) throws IOException {
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

    private void waitForActualStream() throws IOException {
        synchronized (lock) {
            while (actualStream == null && error == null) {
                try {
                    // 等待真正的流准备好或出错
                    lock.wait(10 * 1000 * 60); // 最多等待10分钟
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    throw new IOException("等待Excel数据流时被中断", e);
                }
            }

            if (error != null) {
                throw new IOException("Excel生成失败", error);
            }

            if (actualStream == null) {
                throw new IOException("Excel数据流生成超时，请稍后重试");
            }
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

