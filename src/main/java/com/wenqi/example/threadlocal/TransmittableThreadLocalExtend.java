package com.wenqi.example.threadlocal;

import com.alibaba.ttl.TransmittableThreadLocal;

/**
 * 注意: beforeExecute, afterExecute 回调方法不能够移除子线程执行完 Runnable 的 ThreadLocal 得到释放.
 * 具体可看源码实现, beforeExecute, afterExecute 不会影响到 ThreadLocal 的 backup 和 restore
 * @author liangwenqi
 * @date 2024/12/12
 */
public class TransmittableThreadLocalExtend<T>  extends TransmittableThreadLocal<T> {
    public TransmittableThreadLocalExtend() {
        super();
    }

    @Override
    protected void beforeExecute() {
        System.out.println(Thread.currentThread().getName() + ": before TransmittableThreadLocalExtend - get: " + get());
        Transmitter.clear();
        System.out.println(Thread.currentThread().getName() + ": before TransmittableThreadLocalExtend - after remove get: " + get());
    }

    @Override
    protected void afterExecute() {
        System.out.println(Thread.currentThread().getName() + ": TransmittableThreadLocalExtend - get: " + get());
        Transmitter.clear();
        System.out.println(Thread.currentThread().getName() + ": TransmittableThreadLocalExtend - after remove get: " + get());
    }

}
