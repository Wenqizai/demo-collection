package com.wenqi.designpattern.observer.guava.demo1;

/**
 * @author liangwenqi
 * @date 2022/3/9
 */
public class OrderEventListener {
    @Subscribe
    public void dealWithEvent(OrderMessage event){
        System.out.println("我收到了您的命令，命令内容为： " + event);
    }

    @Subscribe
    public void dealWithEvent2(String event){
        System.out.println("我收到了您的命令，命令内容为2： " + event);
    }

    /**
     * 只能有一个event参数
     *
     * @param event1
     * @param event2
     */
    @Subscribe
    public void dealWithEvent3(String event1, Integer event2){
        System.out.println("我收到了您的命令，命令内容为2： " + event1 + event2);
    }
}
