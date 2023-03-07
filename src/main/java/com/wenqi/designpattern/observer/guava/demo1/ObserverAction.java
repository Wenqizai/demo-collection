package com.wenqi.designpattern.observer.guava.demo1;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * @author liangwenqi
 * @date 2022/3/9
 */
public class ObserverAction {
    private Object target;
    private Method method;

    public ObserverAction(Object target, Method method) {
        this.target = target;
        this.method = method;
        /**
         * 设置setAccessible 可以解除私有属性的访问限制
         */
        this.method.setAccessible(true);
    }

    /**
     * event 是method 的 方法的参数
     *
     * @param event
     */
    public void execute(Object event) {
        try {
            method.invoke(target, event);
        } catch (InvocationTargetException | IllegalAccessException e) {
            e.printStackTrace();
        }
    }
}
