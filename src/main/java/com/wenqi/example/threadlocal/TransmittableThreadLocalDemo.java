package com.wenqi.example.threadlocal;

import com.alibaba.ttl.TtlRunnable;
import com.alibaba.ttl.threadpool.TtlExecutors;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @author liangwenqi
 * @date 2024/12/12
 */
public class TransmittableThreadLocalDemo {

    private static ExecutorService executorService = Executors.newFixedThreadPool(1);


    public static void main(String[] args) throws Exception {
//        test01();
        test02();
//        test03();
    }

    /**
     * TransmittableThreadLocal 本质上是一个 InheritableThreadLocal,
     * 依赖 TtlRunnable 完成 capture, replay, restore 来捕捉父线程的 ThreadLocal, 设置到子线程的 ThreadLocal 并备份子线程的 ThreadLocal backup, 然后执行 run() 方法, 然后将子线程恢复到原来的 backup 状态.
     * <p>
     * 如果以 InheritableThreadLocal 的状态出现, 那么 InheritableThreadLocal 的 backup 将会有值, 那么会造成子线程的内存泄露 (因为会 restore backup)
     */
    private static void test01() throws InterruptedException {
        TransmittableThreadLocalExtend<String> ttle = new TransmittableThreadLocalExtend<>();
        executorService = TtlExecutors.getTtlExecutorService(executorService);

        ttle.set("value-set-in-parent");

        System.out.println(Thread.currentThread().getName() + ": parent value -> " + ttle.get());

        executorService.execute(TtlRunnable.get(() -> {
            System.out.println(Thread.currentThread().getName() + ": 1. child value -> " + ttle.get());
            threadSleep(1000);
            ttle.set("value-set-in-child");
            System.out.println(Thread.currentThread().getName() + ": 2. child value -> " + ttle.get());
        }));


        executorService.execute(TtlRunnable.get(() -> {
            threadSleep(2000);
            System.out.println(Thread.currentThread().getName() + ": 3. child value -> " + ttle.get());
        }));

        threadSleep(5 * 1000);

        System.out.println(Thread.currentThread().getName() + ": parent value -> " + ttle.get());

        executorService.shutdown();
    }

    /**
     * 如果不使用 TtlRunnable, 线程池有线程调用 set(), 没有调用 remove(), 则会内存泄漏
     */
    private static void test02() throws InterruptedException {
        TransmittableThreadLocalExtend<String> ttle = new TransmittableThreadLocalExtend<>();

        ttle.set("value-set-in-parent");

        System.out.println(Thread.currentThread().getName() + ": parent value -> " + ttle.get());

        executorService.execute(TtlRunnable.get(() -> {
            System.out.println(Thread.currentThread().getName() + ": 1. child value -> " + ttle.get());
            ttle.set("value-set-in-child");
            System.out.println(Thread.currentThread().getName() + ": 2. child value -> " + ttle.get());
        }));

        threadSleep(1000);

        // 标志位1, 线程池设置了 threadLocal: value-set-in-child - 2
        executorService.execute(() -> {
            ttle.set("value-set-in-child - 2");
            System.out.println(Thread.currentThread().getName() + ": 3. child value -> " + ttle.get());
        });

        // 线程池复用上面 标志位1 的了 threadLocal: value-set-in-child - 2
        executorService.execute(() -> {
            System.out.println(Thread.currentThread().getName() + ": 4. child value -> " + ttle.get());
        });

        threadSleep(1000);

        //  TtlRunnable 的作用是取父线程的 threadLocal, 不会取子线程原来的 threadLocal
        executorService.execute(TtlRunnable.get(() -> {
            System.out.println(Thread.currentThread().getName() + ": 5. child value -> " + ttle.get());
        }));

        threadSleep(1000);

        // 线程池复用上面 标志位1 的了 threadLocal: value-set-in-child - 2, 证明有内存泄漏, 线程池的核心线程一直持有 value-set-in-child - 2
        executorService.execute(() -> {
            System.out.println(Thread.currentThread().getName() + ": 6. child value -> " + ttle.get());
        });

        Thread.sleep(5 * 1000);

        System.out.println(Thread.currentThread().getName() + ": parent value -> " + ttle.get());

        executorService.shutdown();
    }


    /**
     * 使用 TtlExecutors.getTtlExecutorService(executorService); 可以避免前后执行的线程调用 set() 方法的影响, 但是也是无法避免内存泄漏, 因为  restore backup
     * 原理是: ExecutorTtlWrapper 包装了 executorService, execute() 方法的 runnable 被 TtlRunnable 包装.
     */
    private static void test03() throws InterruptedException {
        executorService = TtlExecutors.getTtlExecutorService(executorService);
        test02();
    }

    /**
     * 避免内存泄漏的终极办法, 每次执行需要确保 InheritableThreadLocal 是干净的?
     */
    private static void test04() throws InterruptedException {
    }


    private static void threadSleep(int mills) {
        try {
            Thread.sleep(mills);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}
