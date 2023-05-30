package com.wenqi.designpattern.bridge.demo03.order;

import java.math.BigDecimal;

/**
 * @author liangwenqi
 * @date 2023/5/30
 */
public abstract class Order {
    public abstract void pay(BigDecimal payAmt);
}
