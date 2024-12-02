package com.wenqi.example.string;

import java.util.Locale;

/**
 * 进制测试
 * @author liangwenqi
 * @date 2024/11/11
 */
public class BaseStringTest {
    public static void main(String[] args) {
        long millis = System.currentTimeMillis();
        System.out.println(millis);
        System.out.println(Integer.toString((int) millis, 35).toUpperCase(Locale.ENGLISH));
    }
}
