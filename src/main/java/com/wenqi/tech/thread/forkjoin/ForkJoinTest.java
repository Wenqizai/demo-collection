package com.wenqi.tech.thread.forkjoin;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ForkJoinPool;

/**
 * @author Wenqi Liang
 * @date 2022/10/6
 */
public class ForkJoinTest {
    public static void main(String[] args) {
        ForkJoinPool commonPool = ForkJoinPool.commonPool();
        ExecutorService workStealingPool = Executors.newWorkStealingPool();
    }
}
