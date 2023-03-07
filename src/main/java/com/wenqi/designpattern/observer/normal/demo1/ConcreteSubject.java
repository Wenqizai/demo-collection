package com.wenqi.designpattern.observer.normal.demo1;

import java.util.ArrayList;
import java.util.List;

/**
 * @author liangwenqi
 * @date 2022/3/9
 */
public class ConcreteSubject implements Subject {
    private static final List<Observer> observers = new ArrayList();

    @Override
    public void register(Observer observer) {
        observers.add(observer);
    }

    @Override
    public void remove(Observer observer) {
        observers.remove(observer);
    }

    @Override
    public void notify(String message) {
        observers.forEach(each -> each.update(message));
    }
}
