package com.wenqi.string;

import java.util.Optional;

/**
 * @author liangwenqi
 * @date 2021/12/9
 */
public class TrimTest {
    public static void main(String[] args) {
        //test1();
        //test2();
        //test3();
        System.out.println(0 ^ 2);
    }

    private static void test1() {
        String code = "";
        System.out.println("@" + Optional.ofNullable(code).orElse("").trim() + "@");
    }

    private static void test2() {
        String code = null;
        System.out.println("@" + Optional.ofNullable(code).orElse("").trim() + "@");
    }

    private static void test3() {
        String code = "aa";
        System.out.println("@" + Optional.ofNullable(code).orElse("").trim() + "@");
    }
}
