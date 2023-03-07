package com.wenqi.designpattern.observer.normal.demo1;

/**
 * @author liangwenqi
 * @date 2022/3/9
 */
public interface Observer {
    void update(String message);  // String 入参只是举例, 真实业务不会限制
}
