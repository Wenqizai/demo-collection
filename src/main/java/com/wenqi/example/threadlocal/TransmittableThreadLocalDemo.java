package com.wenqi.example.threadlocal;

import com.alibaba.ttl.TransmittableThreadLocal;
import com.alibaba.ttl.TtlRunnable;
import com.alibaba.ttl.threadpool.TtlExecutors;
import org.apache.commons.lang3.ThreadUtils;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @author liangwenqi
 * @date 2024/12/12
 */
public class TransmittableThreadLocalDemo {

    private static ExecutorService executorService = Executors.newSingleThreadScheduledExecutor();


    public static void main(String[] args) throws Exception {
        test01();
    }

    private static void test01() throws InterruptedException {
        TransmittableThreadLocalExtend<String> ttle = new TransmittableThreadLocalExtend<>();
        executorService = TtlExecutors.getTtlExecutorService(executorService);

        ttle.set(Thread.currentThread().getName() + ": value-set-in-parent");

        System.out.println(Thread.currentThread().getName() + ": parent value -> " + ttle.get());

        executorService.execute(TtlRunnable.get(() -> {
            System.out.println(Thread.currentThread().getName() + ": 1. child value -> " + ttle.get());
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            ttle.set("value-set-in-child");
            System.out.println(Thread.currentThread().getName() + ": 2. child value -> " + ttle.get());
        }));


        executorService.execute(TtlRunnable.get(() -> {
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            System.out.println(Thread.currentThread().getName() + ": 3. child value -> " + ttle.get());
        }));

        Thread.sleep(5 * 1000);

        System.out.println(Thread.currentThread().getName() + ": parent value -> " + ttle.get());

        executorService.shutdown();
    }
}
