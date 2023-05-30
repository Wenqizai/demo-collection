package com.wenqi.designpattern.bridge.demo03;

/**
 * @author liangwenqi
 * @date 2023/5/30
 */

import com.wenqi.designpattern.bridge.demo03.order.OnlineOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
public class OnlineOrderService {
    @Autowired
    private OnlineOrder onlineOrder;

    public void pay() {
        onlineOrder.pay(new BigDecimal("10"));
    }
}
