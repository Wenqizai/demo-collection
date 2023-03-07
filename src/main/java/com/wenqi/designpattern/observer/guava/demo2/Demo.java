package com.wenqi.designpattern.observer.guava.demo2;

import com.google.common.eventbus.EventBus;

/**
 * @author liangwenqi
 * @date 2022/3/9
 */
public class Demo {
    public static void main(String[] args) {
        EventBus eventBus = new EventBus();
        eventBus.register(new EventListener());
        eventBus.post(1);
    }
}
