package com.wenqi.tech.thread.forkjoin.managedblocker.demo03;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.ForkJoinTask;
import java.util.concurrent.RecursiveTask;
import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * 使用 ForkJoinPool.managedBlock 来解决线程阻塞问题, 线程阻塞时加入线程补偿, 线程数为最大核心数
 *
 * @author Wenqi Liang
 * @date 2022/10/7
 */
public class ManagedBlockerTest02 {

    public static void main(String[] args) {
        test2();
    }

    static String threadDateTimeInfo() {
        return DateTimeFormatter.ISO_TIME.format(LocalTime.now()) + Thread.currentThread().getName();
    }

    static void test2() {
        List<IOBlockerTask<String>> tasks = Stream.generate(() -> new IOBlockerTask<String>(() -> {
            System.out.println(threadDateTimeInfo() + ":simulate io task blocking for 2 seconds···");
            try {
                TimeUnit.SECONDS.sleep(2);
            } catch (InterruptedException e) {
                throw new Error(e);
            }
            return threadDateTimeInfo() + ": io blocking task returns successfully";
        })).limit(30).collect(Collectors.toList());
        tasks.forEach(ForkJoinTask::fork);
        tasks.forEach(e -> {
            try {
                System.out.println(e.get());
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        });

    }

    static class IOBlockerTask<T> extends RecursiveTask<T> {
        private MyManagedBlockerImpl<T> blocker;

        public IOBlockerTask(Supplier<T> supplier) {
            this.blocker = new MyManagedBlockerImpl<>(supplier);
        }

        static class MyManagedBlockerImpl<T> implements ForkJoinPool.ManagedBlocker {
            private Supplier<T> supplier;
            private T result;

            public MyManagedBlockerImpl(Supplier<T> supplier) {
                this.supplier = supplier;
            }

            @Override
            public boolean block() throws InterruptedException {
                // 注意这里创建补偿线程需要时间, 这里需要 sleep 一段时间, 不能直接 return true;
                result = supplier.get();
                return true;
            }

            @Override
            public boolean isReleasable() {
                return false;
            }
        }

        @Override
        protected T compute() {
            try {
                ForkJoinPool.managedBlock(blocker);
                setRawResult((T) blocker.result);
                return getRawResult();
            } catch (InterruptedException e) {
                throw new Error(e);
            }
        }
    }
}
