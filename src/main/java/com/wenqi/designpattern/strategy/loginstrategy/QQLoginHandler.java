package com.wenqi.designpattern.strategy.loginstrategy;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * @author liangwenqi
 * @date 2022/1/19
 */
@Component
@Slf4j
public class QQLoginHandler implements LoginHandler {

    /**
     * 获取登录类型
     *
     * @return
     */
    @Override
    public LoginType getLoginType() {
        return LoginType.QQ;
    }

    /**
     * 登录
     *
     * @param request
     * @return
     */
    @Override
    public LoginResponse handleLogin(LoginRequest request) {
        log.info("QQ登录：userId：{}", request.getUserId());
        return LoginResponse.success("QQ登录成功", null);
    }
}
