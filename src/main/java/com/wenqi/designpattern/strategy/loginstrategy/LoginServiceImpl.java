package com.wenqi.designpattern.strategy.loginstrategy;

import org.springframework.stereotype.Service;

/**
 * @author liangwenqi
 * @date 2022/1/19
 */
@Service
public class LoginServiceImpl implements LoginService {

    private final LoginHandlerFactory loginHandlerFactory;

    public LoginServiceImpl(LoginHandlerFactory loginHandlerFactory) {
        this.loginHandlerFactory = loginHandlerFactory;
    }

    @Override
    public LoginResponse login(LoginRequest request) {
        LoginType loginType = request.getLoginType();
        // 根据 loginType 找到对应的登录处理器
        LoginHandler loginHandler =
                loginHandlerFactory.getHandler(loginType);
        // 处理登录
        return loginHandler.handleLogin(request);
    }
}
