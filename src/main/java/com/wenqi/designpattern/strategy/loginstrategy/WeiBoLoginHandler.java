package com.wenqi.designpattern.strategy.loginstrategy;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * @author liangwenqi
 * @date 2022/1/19
 */
@Component
@Slf4j
public class WeiBoLoginHandler implements LoginHandler {
    /**
     * 获取登录类型
     *
     * @return
     */
    @Override
    public LoginType getLoginType() {
        return LoginType.WEI_BO;
    }

    /**
     * 登录
     *
     * @param request
     * @return
     */
    @Override
    public LoginResponse handleLogin(LoginRequest request) {
        log.info("微博登录：userId：{}", request.getUserId());
        return LoginResponse.success("微博登录成功", null);
    }
}
