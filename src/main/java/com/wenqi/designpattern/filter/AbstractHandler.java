package com.wenqi.designpattern.filter;

import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;

/**
 * 基于Spring的责任链模式
 *
 * @author liangwenqi
 * @date 2021/10/27
 */
public abstract class AbstractHandler {
    /**
     * 责任链的下一个对象
     */
    private AbstractHandler nextHandler;

    public void setNextHandler(AbstractHandler nextHandler) {
        this.nextHandler = nextHandler;
    }

    /**
     * 具体参数拦截逻辑, 给子类去实现
     * @param request
     * @param response
     */
    public void filter(HttpServletRequest request, ServletResponse response) {
        doFilter(request, response);
        if (getNextHandler() != null) {
            getNextHandler().filter(request, response);
        }
    }

    public AbstractHandler getNextHandler() {
        return nextHandler;
    }

    /**
     * 核心逻辑方法, 子类实现
     * @param request
     * @param response
     */
    abstract void doFilter(HttpServletRequest request, ServletResponse response);

}
