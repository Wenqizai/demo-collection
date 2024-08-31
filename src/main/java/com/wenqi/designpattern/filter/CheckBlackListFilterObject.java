package com.wenqi.designpattern.filter;

import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;

/**
 * 黑名单校验对象
 * @author liangwenqi
 * @date 2021/10/27
 */
@Component
@Order(3)   // 校验的顺序
public class CheckBlackListFilterObject extends AbstractHandler {

    @Override
    void doFilter(HttpServletRequest request, ServletResponse response) {
        System.out.println("校验黑名单");
    }

}
