package com.wenqi.example.io;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.io.*;

/**
 * PlaceholderInputStream 测试类
 * 测试缓冲功能和线程安全性
 */
class PlaceholderInputStreamTest {

    @Test
    void testBufferedStreamDetection() throws IOException {
        PlaceholderInputStream placeholder = new PlaceholderInputStream();
        
        // 测试 FileInputStream 会被包装成 BufferedInputStream
        File tempFile = File.createTempFile("test", ".txt");
        tempFile.deleteOnExit();
        
        // 写入测试数据
        try (FileWriter writer = new FileWriter(tempFile)) {
            writer.write("Hello, World! This is a test file for buffering.");
        }
        
        FileInputStream fileInputStream = new FileInputStream(tempFile);
        placeholder.setActualStream(fileInputStream);
        
        // 验证读取功能正常
        byte[] buffer = new byte[10];
        int bytesRead = placeholder.read(buffer);
        assertTrue(bytesRead > 0);
        assertEquals("Hello, Wor", new String(buffer, 0, bytesRead));
        
        placeholder.close();
    }
    
    @Test
    void testAlreadyBufferedStream() throws IOException {
        PlaceholderInputStream placeholder = new PlaceholderInputStream();
        
        // 创建已经缓冲的流
        ByteArrayInputStream byteArrayStream = new ByteArrayInputStream("Test data".getBytes());
        BufferedInputStream bufferedStream = new BufferedInputStream(byteArrayStream);
        
        placeholder.setActualStream(bufferedStream);
        
        // 验证读取功能正常
        byte[] buffer = new byte[4];
        int bytesRead = placeholder.read(buffer);
        assertEquals(4, bytesRead);
        assertEquals("Test", new String(buffer));
        
        placeholder.close();
    }
    
    @Test
    void testByteArrayInputStream() throws IOException {
        PlaceholderInputStream placeholder = new PlaceholderInputStream();
        
        // ByteArrayInputStream 不需要额外缓冲
        ByteArrayInputStream byteArrayStream = new ByteArrayInputStream("ByteArray test".getBytes());
        placeholder.setActualStream(byteArrayStream);
        
        // 验证读取功能正常
        byte[] buffer = new byte[9];
        int bytesRead = placeholder.read(buffer);
        assertEquals(9, bytesRead);
        assertEquals("ByteArray", new String(buffer));
        
        placeholder.close();
    }
    
    @Test
    void testThreadSafety() throws Exception {
        PlaceholderInputStream placeholder = new PlaceholderInputStream();
        
        // 在另一个线程中设置流
        Thread setterThread = new Thread(() -> {
            try {
                Thread.sleep(100); // 模拟延迟
                ByteArrayInputStream stream = new ByteArrayInputStream("Thread safe test".getBytes());
                placeholder.setActualStream(stream);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        });
        
        // 在主线程中读取
        Thread readerThread = new Thread(() -> {
            try {
                byte[] buffer = new byte[6];
                int bytesRead = placeholder.read(buffer);
                assertEquals(6, bytesRead);
                assertEquals("Thread", new String(buffer));
            } catch (IOException e) {
                fail("读取失败: " + e.getMessage());
            }
        });
        
        setterThread.start();
        readerThread.start();
        
        setterThread.join();
        readerThread.join();
        
        placeholder.close();
    }
}
