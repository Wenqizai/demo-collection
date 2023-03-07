package com.wenqi.designpattern.observer.guava.demo2;

import com.google.common.eventbus.Subscribe;

/**
 * @author liangwenqi
 * @date 2022/3/9
 */
public class EventListener {

    /**
     * 监听 Integer 类型的消息
     */
    @Subscribe
    public void listenInteger(Integer param) {
        System.out.println("EventListener#listenInteger ->" + param);
    }

    /**
     * 监听 String 类型的消息
     */
    @Subscribe
    public void listenString(String param, Integer event) {
        System.out.println("EventListener#listenString ->" + param);
    }
}
