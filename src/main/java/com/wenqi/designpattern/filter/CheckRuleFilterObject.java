package com.wenqi.designpattern.filter;

import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;

/**
 * 规则校验对象
 * @author liangwenqi
 * @date 2021/10/27
 */
@Component
@Order(4)   // 校验的顺序
public class CheckRuleFilterObject extends AbstractHandler {

    @Override
    void doFilter(HttpServletRequest request, ServletResponse response) {
        System.out.println("规则校验");
    }

}
