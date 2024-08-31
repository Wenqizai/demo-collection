package com.wenqi.designpattern.observer.guava.demo1;

import java.util.concurrent.Executors;

/**
 * @author liangwenqi
 * @date 2022/3/9
 */
public class Demo {
    public static void main(String[] args) {
        EventBus eventBus = new EventBus();
        eventBus.register(new OrderEventListener());
        eventBus.register(new OrderEventListener1());
        OrderMessage msg =new OrderMessage();
        msg.setOrderContent(9L);
        eventBus.post(msg);

        EventBus asyncEventBus = new AsyncEventBus(Executors.newSingleThreadExecutor());
        asyncEventBus.register(new OrderEventListener());
        asyncEventBus.register(new OrderEventListener1());
        OrderMessage asyMsg =new OrderMessage();
        asyMsg.setOrderContent(8L);
        eventBus.post(asyMsg);
    }
}
