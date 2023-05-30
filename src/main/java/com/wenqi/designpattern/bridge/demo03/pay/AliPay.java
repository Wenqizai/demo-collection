package com.wenqi.designpattern.bridge.demo03.pay;

import org.springframework.stereotype.Component;

import java.math.BigDecimal;

/**
 * @author liangwenqi
 * @date 2023/5/30
 */
@Component("aliPay")
public class AliPay implements PaymentMethod {
    @Override
    public void pay(BigDecimal amount) {
        System.out.println("使用支付宝支付：" + amount + " 元");
    }
}
