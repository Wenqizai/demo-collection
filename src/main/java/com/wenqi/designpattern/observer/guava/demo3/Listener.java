package com.wenqi.designpattern.observer.guava.demo3;

/**
 * @author liangwenqi
 * @date 2022/3/9
 */
public interface Listener {
    /**
     * 监听方法
     * @param event
     */
    void listen(String event);
}
