package com.wenqi.tech.thread.forkjoin.managedblocker;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ForkJoinPool;

/**
 * @author Wenqi Liang
 * @date 2022/10/7
 */
public class ManagedBlockerDemo {
    public static void main(String[] args) throws InterruptedException {
        BlockingQueue<String> bq = new ArrayBlockingQueue<>(2);
        bq.put("A");
        bq.put("B");
        QueueManagedBlocker<String> blocker = new QueueManagedBlocker<>(bq);
        ForkJoinPool.managedBlock(blocker);
        System.out.println(blocker.getValue());
    }
}
