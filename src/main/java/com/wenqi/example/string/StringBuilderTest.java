package com.wenqi.string;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * @author liangwenqi
 * @date 2022/5/25
 */
public class StringBuilderTest {
    public static void main(String[] args) {
        ThreadFactory threadFactory = r -> {
            Thread thread = new Thread();
            thread.setName("my-pool-%d");
            return thread;
        };
        ThreadPoolExecutor executor = new ThreadPoolExecutor(5, 10, 10, TimeUnit.SECONDS, new LinkedBlockingQueue<>(100), threadFactory, new ThreadPoolExecutor.CallerRunsPolicy());
        ThreadPoolExecutor executor2 = new ThreadPoolExecutor(5, 10, 10, TimeUnit.SECONDS, new LinkedBlockingQueue<>(100), threadFactory, new ThreadPoolExecutor.CallerRunsPolicy());
        StringBuilder sbu = new StringBuilder();
        StringBuffer sbf = new StringBuffer();

        List<String> list = Collections.synchronizedList(new ArrayList<>());
        //for (int i = 0; i < 1000; i++) {
        //    executor.execute(() -> sbu.append(1));
        //}
        //System.out.println(sbu.length());
        //System.out.println(sbu.toString());

        for (int i = 0; i < 1000; i++) {
            int finalI = i;
            executor2.execute(() -> {
                sbf.append(1);
                list.add(String.valueOf(finalI));
            });
        }
        System.out.println(list.size());
        System.out.println(sbf.length());
        System.out.println(sbf.toString());

        executor.shutdown();
        executor2.shutdown();
    }
}
