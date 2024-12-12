package com.wenqi.example.threadlocal;

import com.alibaba.ttl.TransmittableThreadLocal;

/**
 * @author liangwenqi
 * @date 2024/12/12
 */
public class TransmittableThreadLocalExtend<T>  extends TransmittableThreadLocal<T> {
    public TransmittableThreadLocalExtend() {
        super();
    }

    @Override
    protected void afterExecute() {
        System.out.println(Thread.currentThread().getName() + ": TransmittableThreadLocalExtend - get: " + get());
        remove();
        System.out.println(Thread.currentThread().getName() + ": TransmittableThreadLocalExtend - after remove get: " + get());
    }

}
