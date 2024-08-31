package com.wenqi.tech.compress;

import org.apache.commons.lang3.StringUtils;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

/**
 * @author liangwenqi
 * @date 2023/3/23
 */
public class GzipUtils {
    public static void main(String[] args) {
        final String compress = compress(StringMaterial.bigValue);
        System.out.println(compress);
        System.out.println("compress String length: " + compress.length());

        System.out.println("\n");

        final String deCompress = deCompress(compress);
        System.out.println(deCompress);
        System.out.println("deCompress String length: " + deCompress.length());
    }


    public static String compress(String value) {
        if (StringUtils.isEmpty(value)) {
            return "";
        }
        final ByteArrayOutputStream out = new ByteArrayOutputStream();
        try (GZIPOutputStream gos = new GZIPOutputStream(out)) {
           gos.write(value.getBytes(StandardCharsets.UTF_8));
       } catch (IOException ioException) {
           ioException.printStackTrace();
       } finally {
            try {
                out.close();
            } catch (IOException ioException) {
                ioException.printStackTrace();
            }
        }
       return Base64.getEncoder().encodeToString(out.toByteArray());
    }

    public static String deCompress(String value) {
        if (StringUtils.isEmpty(value)) {
            return "";
        }
        StringBuilder sb = new StringBuilder();
        final ByteArrayInputStream in = new ByteArrayInputStream(Base64.getDecoder().decode(value));
        try(GZIPInputStream gis = new GZIPInputStream(in)) {
            BufferedReader bf = new BufferedReader(new InputStreamReader(gis, StandardCharsets.UTF_8));
            String line;
            while ((line = bf.readLine()) != null) {
                sb.append(line);
            }
        } catch (IOException ioException) {
            ioException.printStackTrace();
        } finally {
            try {
                in.close();
            } catch (IOException ioException) {
                ioException.printStackTrace();
            }
        }
        return sb.toString();
    }
}
