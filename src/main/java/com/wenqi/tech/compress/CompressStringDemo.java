package com.wenqi.tech.compress;


import cn.hutool.extra.compress.CompressUtil;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.Base64;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

/**
 * @author liangwenqi
 * @date 2023/3/23
 */
public class CompressStringDemo {
    public static void main(String[] args) throws Exception {
        final String bigValue = StringMaterial.bigValue;
        //base64(bigValue);
        gzip(bigValue);
    }

    /**
     * 32k
     */
    private static void base64(String bigValue) throws UnsupportedEncodingException {
        System.out.println("############### base64 compress ###############");
        final byte[] bytes = bigValue.getBytes();
        final String compressStr = new String(Base64.getEncoder().encode(bytes), "UTF-8");
        System.out.println("Output String length: " + compressStr.length());
    }

    /**
     * 5.9k
     */
    private static void gzip(String bigValue) throws IOException {
        System.out.println("############### gzip compress ###############");
        ByteArrayOutputStream obj = new ByteArrayOutputStream();
        GZIPOutputStream gzip = new GZIPOutputStream(obj);
        gzip.write(bigValue.getBytes("UTF-8"));
        gzip.finish();
        gzip.flush();
        gzip.close();
        String outStr = new String(Base64.getEncoder().encode(obj.toByteArray()), "UTF-8");
        System.out.println(outStr);
        System.out.println("Output String length : " + outStr.length());

        System.out.println("############### gzip decompress ###############");
        GZIPInputStream gis = new GZIPInputStream(new ByteArrayInputStream(Base64.getDecoder().decode(outStr)));
        BufferedReader bf = new BufferedReader(new InputStreamReader(gis, "UTF-8"));
        StringBuilder sb = new StringBuilder();
        String line = "";
        while ((line = bf.readLine()) != null) {
            sb.append(line);
        }
        System.out.println(sb.toString());
        System.out.println("Output String length : " + sb.toString().length());
    }
}
