package com.wenqi.designpattern.observer.guava.demo1;


import java.util.concurrent.Executor;

/**
 * @author liangwenqi
 * @date 2022/3/9
 */
public class AsyncEventBus extends EventBus {
    public AsyncEventBus(Executor executor) {
        super(executor);
    }
}
