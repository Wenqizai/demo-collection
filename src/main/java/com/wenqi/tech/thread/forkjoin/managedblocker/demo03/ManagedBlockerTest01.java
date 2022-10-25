package com.wenqi.tech.thread.forkjoin.managedblocker.demo03;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.concurrent.RecursiveTask;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * 普通使用 ForkJoinPool: 阻塞时没有线程补偿, 线程数为最大核心数
 * <p>
 * 由于每个任务固定阻塞2秒，而线程池的parallelism=3，因此总的耗时时间为(30 / 3) * 2 = 20秒
 *
 * @author Wenqi Liang
 * @date 2022/10/7
 */
public class ManagedBlockerTest01 {
    static String threadDateTimeInfo() {
        return DateTimeFormatter.ISO_TIME.format(LocalTime.now()) + Thread.currentThread().getName();
    }

    static void test1() {
        List<RecursiveTask<String>> tasks = Stream.generate(() -> new RecursiveTask<String>() {
            @Override
            protected String compute() {
                System.out.println(threadDateTimeInfo() + ":simulate io task blocking for 2 seconds···");
                try {
                    //线程休眠2秒模拟IO调用阻塞
                    TimeUnit.SECONDS.sleep(2);
                } catch (InterruptedException e) {
                    throw new Error(e);
                }
                return threadDateTimeInfo() + ": io blocking task returns successfully";
            }
        }).limit(30).collect(Collectors.toList());
        tasks.forEach(e -> e.fork());
        tasks.forEach(e -> {
            try {
                System.out.println(e.get());
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        });
    }

    public static void main(String[] args) {
        test1();
    }
}
