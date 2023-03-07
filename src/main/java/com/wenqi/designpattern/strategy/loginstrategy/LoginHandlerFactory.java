package com.wenqi.designpattern.strategy.loginstrategy;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

import java.util.EnumMap;
import java.util.Map;

/**
 * @author liangwenqi
 * @date 2022/1/19
 */
@Component
public class LoginHandlerFactory implements InitializingBean, ApplicationContextAware {
    private static final Map<LoginType, LoginHandler> LOGIN_HANDLER_MAP = new EnumMap<>(LoginType.class);
    private ApplicationContext applicationContext;

    /**
     * 根据登录类型获取对应的处理器
     */
    public LoginHandler getHandler(LoginType loginType) {
        return LOGIN_HANDLER_MAP.get(loginType);
    }


    @Override
    public void afterPropertiesSet() throws Exception {
        // 将 Spring 容器中所有的 LoginHandler 注册到 LOGIN_HANDLER_MAP
        applicationContext.getBeansOfType(LoginHandler.class)
                .values()
                .forEach(handler -> LOGIN_HANDLER_MAP.put(handler.getLoginType(), handler));
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }
}
