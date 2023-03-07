package com.wenqi.designpattern.observer.guava.demo1;

/**
 * @author liangwenqi
 * @date 2022/3/9
 */
public class OrderEventListener1 {
    @Subscribe
    private void dealWith(String event) {
        System.out.println("我收到了您的命令，命令内容为： " + event);
    }
}
