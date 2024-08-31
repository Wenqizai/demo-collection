package com.wenqi.example.time;

import org.joda.time.LocalDateTime;
import org.joda.time.LocalTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

/**
 * @author liangwenqi
 * @date 2023/5/12
 */
public class JodaFormatTest {
    public static void main(String[] args) {
        test4();
    }

    private static void test5() {
        DateTimeFormatter formatDateTime = DateTimeFormat.forPattern("HH:mm");

        System.out.println(LocalDateTime.parse("10:54", formatDateTime));
        System.out.println(LocalTime.now().isBefore(LocalDateTime.parse("10:54", formatDateTime)));
    }

    private static void test4() {
        DateTimeFormatter formatDateTime = DateTimeFormat.forPattern("HH:mm");

        System.out.println(LocalDateTime.parse("10:15:01", formatDateTime));
    }


    private static void test3() {
        DateTimeFormatter formatDateTime = DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss");

        System.out.println(LocalDateTime.parse("2023-05-02 20:05:02", formatDateTime));
    }


    private static void test2() {
        DateTimeFormatter formatDateTime = DateTimeFormat.forPattern("HH:mm:ss");

        System.out.println(LocalTime.parse("20:05:02", formatDateTime));
    }

    private static void test1() {
        DateTimeFormatter formatDateTime = DateTimeFormat.forPattern("yyyy/MM/dd");

        System.out.println(LocalTime.parse("2023/05/02", formatDateTime));
    }
}
