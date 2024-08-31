package com.wenqi.tech.thread.forkjoin.managedblocker.demo02;

import java.util.stream.IntStream;

/**
 * @author Wenqi Liang
 * @date 2022/10/7
 */
public class PrintInFun {
    public static void main(String[] args) {
        // 死锁
        synchronized (System.out) {
            System.out.println("Hello World!");
            IntStream.range(0, 4).parallel().forEach(System.out::println);
        }
    }
}
