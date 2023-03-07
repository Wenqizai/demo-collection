package com.wenqi.designpattern.observer.guava.demo3;

import com.google.common.eventbus.Subscribe;
import org.springframework.stereotype.Component;

/**
 * @author liangwenqi
 * @date 2022/3/9
 */
@Component
public class StringEventListener2 implements Listener {

    @Override
    @Subscribe
    public void listen(String event) {
        System.out.println("receive msg:" + event);
    }
}
