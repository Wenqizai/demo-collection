package com.wenqi.designpattern.observer.guava.demo3;

import com.google.common.eventbus.AsyncEventBus;
import com.google.common.eventbus.EventBus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.TaskExecutor;

/**
 * @author liangwenqi
 * @date 2022/3/9
 */
@Configuration
public class EventBusConfig {
    @Autowired
    TaskExecutor taskExecutor;

    /**
     * 定义bean
     * @return
     */
    @Bean
    public EventBus eventBus() {
        return new EventBus();
    }


    @Bean
    public AsyncEventBus asyncEventBus() {
        return new AsyncEventBus(taskExecutor);
    }
}
