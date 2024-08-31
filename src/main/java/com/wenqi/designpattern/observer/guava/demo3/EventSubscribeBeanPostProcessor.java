package com.wenqi.designpattern.observer.guava.demo3;

import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;

/**
 * @author liangwenqi
 * @date 2022/3/9
 */
@Component
public class EventSubscribeBeanPostProcessor implements BeanPostProcessor {
    /**
     * 事件总线bean由Spring IoC容器负责创建，这里只需要通过@Autowired注解注入该bean即可使用事件总线
     */
    @Autowired
    EventBus eventBus;
    @Autowired
    ApplicationContext applicationContext;
    @Autowired
    private List<Listener> listenerList;

    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
        return bean;
    }

    /**
     * 对于每个容器执行了初始化的 bean，如果这个 bean 的某个方法注解了@Subscribe,则将该 bean 注册到事件总线
     * @param bean
     * @param beanName
     * @return
     * @throws BeansException
     */
    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        return bean;
    }


    public void init() {
        Map<String, Listener> listenerMap = applicationContext.getBeansOfType(Listener.class);
        listenerMap.forEach((key, value) -> eventBus.register(value));
    }


    @PostConstruct
    public void initEvent() {
        for (Listener listener : listenerList) {
            eventBus.register(listener);
        }
    }

    private Object getObject(Object bean) {
        Method[] methods = bean.getClass().getMethods();
        for (Method method : methods) {
            Annotation[] annotations = method.getAnnotations();
            for (Annotation annotation : annotations) {
                if (annotation.annotationType().equals(Subscribe.class)) {
                    eventBus.register(bean);
                    return bean;
                }
            }
        }
        return bean;
    }
}
