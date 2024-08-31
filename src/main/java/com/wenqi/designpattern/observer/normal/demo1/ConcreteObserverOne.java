package com.wenqi.designpattern.observer.normal.demo1;

/**
 * @author liangwenqi
 * @date 2022/3/9
 */
public class ConcreteObserverOne implements Observer {
    @Override
    public void update(String message) {
        // 执行 message 逻辑
        System.out.println("接收到被观察者状态变更-1");
    }
}
