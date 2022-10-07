package com.wenqi.tech.thread.forkjoin.managedblocker;

import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @author Wenqi Liang
 * @date 2022/10/7
 */
public class Locker implements ForkJoinPool.ManagedBlocker {
    final ReentrantLock rtLock;
    volatile boolean isLocked = false;

    public Locker(ReentrantLock rtLock) {
        this.rtLock = rtLock;
    }

    @Override
    public boolean block() throws InterruptedException {
        if (!isLocked) {
            rtLock.lock();
        }
        return true;
    }

    @Override
    public boolean isReleasable() {
        return isLocked || (isLocked == rtLock.tryLock());
    }
}
