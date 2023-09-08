package com.wenqi.designpattern.command.demo02;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Wenqi Liang
 * @date 9/2/2023
 */
public class Broker {
    private List<Order> orderList = new ArrayList<Order>();

    public void takeOrder(Order order) {
        orderList.add(order);
    }

    public void placeOrders() {
        for (Order order : orderList) {
            order.execute();
        }
        orderList.clear();
    }
}
