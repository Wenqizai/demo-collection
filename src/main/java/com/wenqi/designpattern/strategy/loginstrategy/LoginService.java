package com.wenqi.designpattern.strategy.loginstrategy;

/**
 * @author liangwenqi
 * @date 2022/1/19
 */
public interface LoginService {
    LoginResponse login(LoginRequest request);
}
