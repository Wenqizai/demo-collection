package com.wenqi.designpattern.strategy.loginstrategy;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.io.Serializable;

/**
 * @author liangwenqi
 * @date 2022/1/19
 */
@Component
@Slf4j
public class WeChatLoginHandler implements LoginHandler {

    @Override
    public LoginType getLoginType() {
        return LoginType.WE_CHAT;
    }

    @Override
    public LoginResponse handleLogin(LoginRequest request) {
        log.info("微信登录: userId:{} ", request.getUserId());
        String weChatName = getWeChatName(request);
        return LoginResponse.success("微信登录成功", weChatName);
    }

    private String getWeChatName(LoginRequest request) {
        return "wupx";
    }
}
