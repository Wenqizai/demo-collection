package com.wenqi.designpattern.strategy.loginstrategy;

import java.io.Serializable;

/**
 * @author liangwenqi
 * @date 2022/1/19
 */
public interface LoginHandler {
    /**
     * 获取登录类型
     * @return
     */
    LoginType getLoginType();

    /**
     * 登录
     * @param request
     * @return
     */
    LoginResponse handleLogin(LoginRequest request);

}
