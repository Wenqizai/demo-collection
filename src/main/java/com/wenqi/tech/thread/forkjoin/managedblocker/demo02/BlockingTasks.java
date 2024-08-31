package com.wenqi.tech.thread.forkjoin.managedblocker.demo02;

import java.util.List;
import java.util.concurrent.ForkJoinPool;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @author Wenqi Liang
 * @date 2022/10/7
 */
public class BlockingTasks {

    public static <T> T callInManagedBlock(final Supplier<T> supplier) {
        final SupplierManagedBlock<T> managedBlock = new SupplierManagedBlock<T>(supplier);
        try {
            ForkJoinPool.managedBlock(managedBlock);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        return managedBlock.getResult();
    }


    private static class SupplierManagedBlock<T> implements ForkJoinPool.ManagedBlocker{
        private final Supplier<T> supplier;
        private T result;
        private boolean done = false;

        public SupplierManagedBlock(Supplier<T> supplier) {
            this.supplier = supplier;
        }

        @Override
        public boolean block() throws InterruptedException {
            return false;
        }

        @Override
        public boolean isReleasable() {
            return false;
        }

        public T getResult() {
            return result;
        }
    }
}
