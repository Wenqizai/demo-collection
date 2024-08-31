package com.wenqi.designpattern.observer.guava.demo1;

import lombok.Data;

/**
 * @author liangwenqi
 * @date 2022/3/9
 */
@Data
public class OrderMessage {

    private Object  orderContent;

    public Object getOrderContent() {
        return orderContent;
    }

    public void setOrderContent(Object orderContent) {
        this.orderContent = orderContent;
    }
}
