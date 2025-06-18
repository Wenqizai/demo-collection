package com.wenqi.example.io;

import cn.hutool.core.thread.ThreadUtil;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.util.concurrent.CompletableFuture;

/**
 * 异步 AsyncPlaceHolderStream，防止流被提前关闭
 * @author liangwenqi
 * @date 2025/6/17
 */
public class AsyncPlaceHolderStream {
    public static void main(String[] args) {
        AsyncPlaceHolderStream asyncPlaceHolderStream = new AsyncPlaceHolderStream();
        long start = System.currentTimeMillis();
        test01(asyncPlaceHolderStream);

        // 可以读到 11111111，流等待 10 分钟后关闭
//        asyncPlaceHolderStream.downloadDataWithPlaceholderInputStream();
        System.out.println("耗时: " + (System.currentTimeMillis() - start) + "ms");
    }

    /**
     * 只能读到 1111，流被提前关闭了
     */
    private static void test01(AsyncPlaceHolderStream asyncPlaceHolderStream) {
        asyncPlaceHolderStream.downloadDataWithInputStream();
        ThreadUtil.sleep(20 * 1000L);
    }

    /**
     * 下载数据 - 优化版本，解决大数据量导出时PipedInputStream被提前关闭的问题
     * 使用临时文件 + PlaceholderInputStream方案，确保数据完整性
     */
    public void downloadDataWithPlaceholderInputStream() {
        try {
            // 创建临时文件
            File tempFile = File.createTempFile("File_" + System.currentTimeMillis() + "_", ".txt");
            tempFile.deleteOnExit();

            // 创建"占位"输入流
            PlaceholderInputStream placeholderStream = new PlaceholderInputStream();

            // 异步生成File文件
            CompletableFuture.runAsync(() -> {
                try (FileOutputStream fos = new FileOutputStream(tempFile)) {
                    // 读取文件到 stream
                    writeDataToOutputStream(fos);

                    // 文件生成完成后，用真正的FileInputStream替换占位流
                    placeholderStream.setActualStream(Files.newInputStream(tempFile.toPath()));

                } catch (Exception e) {
                    placeholderStream.setError(e);
                }
            });

            // 立即返回占位流进行下载
           readDataFromInputStream(placeholderStream);

        } catch (Exception e) {
            throw new IllegalStateException("File导出准备失败: " + e.getMessage(), e);
        }
    }

    public void downloadDataWithInputStream() {
        try {
            // 创建临时文件
            File tempFile = File.createTempFile("File_" + System.currentTimeMillis() + "_", ".txt");
            tempFile.deleteOnExit();

            // 创建输入流
            InputStream inputStream = new BufferedInputStream(Files.newInputStream(tempFile.toPath()));

            // 异步生成File文件
            CompletableFuture.runAsync(() -> {
                try (FileOutputStream fos = new FileOutputStream(tempFile)) {
                    // 读取文件到 stream
                    writeDataToOutputStream(fos);
                } catch (Exception e) {
                    throw new IllegalStateException("File导出失败: " + e.getMessage(), e);
                }
            });

            // 立即返回占位流进行下载
           readDataFromInputStream(inputStream);
        } catch (Exception e) {
            throw new IllegalStateException("File导出准备失败: " + e.getMessage(), e);
        }
    }

    public void writeDataToOutputStream(OutputStream fos) {
        String txt = "1111";
        try {
            fos.write(txt.getBytes());
            fos.flush();
            Thread.sleep(10 * 1000L);

            fos.write(txt.getBytes());
            fos.flush();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void readDataFromInputStream(InputStream fis) {
        // 读取数据
        try {
            byte[] buffer = new byte[1024];
            int bytesRead;
            while ((bytesRead = fis.read(buffer)) != -1) {
                // 处理数据
                System.out.println(new String(buffer, 0, bytesRead));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            fis.close();
        } catch (IOException e) {
            throw new IllegalStateException("File导出失败: " + e.getMessage(), e);
        }
    }

}
