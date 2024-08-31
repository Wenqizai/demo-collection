package com.wenqi.designpattern.observer.normal.demo1;

/**
 * @author liangwenqi
 * @date 2022/3/9
 */
public class Example {
    public static void main(String[] args) {
        ConcreteSubject subject = new ConcreteSubject();
        subject.register(new ConcreteObserverOne());
        subject.register(new ConcreteObserverTwo());
        subject.notify("被观察者状态改变, 通知所有已注册观察者");
    }
}
