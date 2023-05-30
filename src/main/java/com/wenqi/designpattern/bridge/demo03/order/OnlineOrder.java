package com.wenqi.designpattern.bridge.demo03.order;

import com.wenqi.designpattern.bridge.demo03.pay.PaymentMethod;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

/**
 * @author liangwenqi
 * @date 2023/5/30
 */
@Component
public class OnlineOrder extends Order {

    @Autowired
    @Qualifier("aliPay")
    private PaymentMethod aliPay;
    @Autowired
    @Qualifier("bankCardPay")
    private PaymentMethod bankCardPay;
    @Autowired
    @Qualifier("weChatPay")
    private PaymentMethod weChatPay;

    @Override
    public void pay(BigDecimal amount) {
        aliPay.pay(amount);

        bankCardPay.pay(amount);

        weChatPay.pay(amount);
    }
}

