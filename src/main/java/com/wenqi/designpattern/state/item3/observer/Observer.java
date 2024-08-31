package com.wenqi.designpattern.state.item3.observer;

/**
 * 抽象观察者
 * @author liangwenqi
 * @date 2022/3/23
 */
public interface Observer {
    void response(Long taskId);
}
