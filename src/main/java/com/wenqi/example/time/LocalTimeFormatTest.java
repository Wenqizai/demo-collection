package com.wenqi.example.time;


import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

/**
 * @author liangwenqi
 * @date 2023/5/12
 */
public class LocalTimeFormatTest {
    public static void main(String[] args) {
        test6();
    }

    private static void test6() {
        String text = "11:35";
        LocalTime parse = LocalTime.parse(text);
        System.out.println(LocalTime.now().isBefore(parse));
        System.out.println(LocalTime.now().isAfter(parse));
    }

    private static void test5() {
        String text = "20:05:02";
        LocalTime.parse(text);
        System.out.println(LocalTime.parse(text));
        //System.out.println(LocalTime.now().isBefore(LocalTime.parse("11:14")));
    }

    private static void test4() {
        DateTimeFormatter formatDateTime = DateTimeFormatter.ofPattern("HH:mm");

        System.out.println(LocalDateTime.parse("10:15:01", formatDateTime));
    }


    private static void test3() {
        DateTimeFormatter formatDateTime = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

        System.out.println(LocalDateTime.parse("2023-05-02 20:05:02", formatDateTime));
    }


    private static void test2() {
        DateTimeFormatter formatDateTime = DateTimeFormatter.ofPattern("HH:mm:ss");

        System.out.println(LocalTime.parse("20:05:02", formatDateTime));
    }

    private static void test1() {
        DateTimeFormatter formatDateTime = DateTimeFormatter.ofPattern("yyyy/MM/dd");

        System.out.println(LocalTime.parse("2023/05/02", formatDateTime));
    }
}
