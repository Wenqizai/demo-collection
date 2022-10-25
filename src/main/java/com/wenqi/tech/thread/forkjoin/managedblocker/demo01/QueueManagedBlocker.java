package com.wenqi.tech.thread.forkjoin.managedblocker.demo01;

import java.util.concurrent.BlockingDeque;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ForkJoinPool;

/**
 * @author Wenqi Liang
 * @date 2022/10/7
 */
public class QueueManagedBlocker<T> implements ForkJoinPool.ManagedBlocker{
    final BlockingQueue<T> queue;
    volatile T value = null;

    public QueueManagedBlocker(BlockingQueue<T> queue) {
        this.queue = queue;
    }

    @Override
    public boolean block() throws InterruptedException {
        if (value == null) {
            value = queue.take();
        }
        return true;
    }

    @Override
    public boolean isReleasable() {
        return value != null || (value = queue.poll()) != null;
    }

    public T getValue() {
        return value;
    }
}
