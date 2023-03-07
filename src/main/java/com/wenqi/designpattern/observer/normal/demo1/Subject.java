package com.wenqi.designpattern.observer.normal.demo1;

/**
 * @author liangwenqi
 * @date 2022/3/9
 */
public interface Subject {
    void register(Observer observer);  // 添加观察者

    void remove(Observer observer);  // 移除观察者

    void notify(String message);  // 通知所有观察者事件
}
