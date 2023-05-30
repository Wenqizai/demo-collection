package com.wenqi.designpattern.bridge.demo03.pay;

import java.math.BigDecimal;

/**
 * @author liangwenqi
 * @date 2023/5/30
 */
public interface PaymentMethod {
    void pay(BigDecimal amount);
}
