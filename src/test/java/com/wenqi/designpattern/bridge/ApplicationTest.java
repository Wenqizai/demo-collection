package com.wenqi.designpattern.bridge;

import com.wenqi.designpattern.PatternApplication;
import com.wenqi.designpattern.bridge.demo03.OnlineOrderService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * @author liangwenqi
 * @date 2023/5/30
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = PatternApplication.class)
public class ApplicationTest {

    @Autowired
    private OnlineOrderService orderService;

    @Test
    public void test(){
        orderService.pay();
    }
}
