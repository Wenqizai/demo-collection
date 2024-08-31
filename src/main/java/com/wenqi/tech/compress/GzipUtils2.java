package com.wenqi.tech.compress;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

/**
 * @author liangwenqi
 * @date 2023/4/27
 */
public class GzipUtils2 {
    public static void main(String[] args) throws IOException {
        final String compress = compress(StringMaterial.bigValue);
        System.out.println(compress);
        System.out.println("compress String length: " + compress.length());

        System.out.println("\n");

        final String deCompress = extract(compress);
        System.out.println(deCompress);
        System.out.println("deCompress String length: " + deCompress.length());
    }

    /**
     * 将字符串进行gzip压缩并BASE64编码
     *
     * @param data 待压缩数据
     * @return 压缩结果
     */
    public static String compress(String data) {
        return compress(data, StandardCharsets.UTF_8.name());
    }

    /**
     * 将字符串进行gzip压缩并BASE64编码
     *
     * @param data     待压缩数据
     * @param encoding 数据编码
     * @return 压缩结果
     */
    public static String compress(String data, String encoding) {
        if (data == null || data.isEmpty()) {
            return data;
        }
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        try (GZIPOutputStream gzip = new GZIPOutputStream(out)) {
            gzip.write(data.getBytes(encoding));
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                out.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return Base64.getEncoder().encodeToString(out.toByteArray());
    }

    /**
     * 将字符串进行BASE64解码并提取gzip压缩数据
     *
     * @param data 待解压数据
     * @return 解压结果
     */
    public static String extract(String data) throws IOException {
        return extract(data, StandardCharsets.UTF_8.name());
    }

    /**
     * 将字符串进行BASE64解码并提取gzip压缩数据
     *
     * @param data     待解压数据
     * @param encoding 编码
     * @return 解压结果
     */
    public static String extract(String data, String encoding) throws IOException {
        if (data == null || data.isEmpty()) {
            return data;
        }
        byte[] out = extractToArray(data);
        if (out == null || out.length == 0) {
            return null;
        }
        return new String(out, Charset.forName(encoding));
    }

    /**
     * 将字符串进行BASE64解码并提取gzip压缩数据
     *
     * @param data 待解压数据
     * @return 解压结果
     */
    public static byte[] extractToArray(String data) throws IOException {
        if (data == null || data.isEmpty()) {
            return null;
        }
        byte[] decode = Base64.getDecoder().decode(data);
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        try (ByteArrayInputStream in = new ByteArrayInputStream(decode);
             GZIPInputStream gzipStream = new GZIPInputStream(in)) {
            byte[] buffer = new byte[256];
            int n;
            while ((n = gzipStream.read(buffer)) >= 0) {
                out.write(buffer, 0, n);
            }
        } finally {
            try {
                out.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return out.toByteArray();
    }
}
