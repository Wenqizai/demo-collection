package com.wenqi.designpattern.filter;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * @author liangwenqi
 * @date 2021/10/27
 */
@Component("ChainPatternDemo")
public class ChainPatternDemo {
    /**
     * 自动注入各个责任链的对象
     */
    @Autowired
    private List<AbstractHandler> abstractHandlerList;

    private AbstractHandler abstractHandler;

    /**
     * Spring注入后自动执行, 责任链的对象连接起来
     */
    @PostConstruct
    public void initializeChainFilter() {
        for (int i = 0; i < abstractHandlerList.size(); i++) {
            if (i == 0) {
                abstractHandler = abstractHandlerList.get(0);
            } else {
                AbstractHandler currentHandler = abstractHandlerList.get(i - 1);
                AbstractHandler nextHandler = abstractHandlerList.get(i);
                currentHandler.setNextHandler(nextHandler);
            }
        }
    }

    /**
     * 直接调用这个方法使用
     * @param request
     * @param response
     * @return
     */
    public ServletResponse exec(HttpServletRequest request, ServletResponse response) {
        abstractHandler.filter(request, response);
        return response;
    }

    public AbstractHandler getAbstractHandler() {
        return abstractHandler;
    }

    public void setAbstractHandler(AbstractHandler abstractHandler) {
        this.abstractHandler = abstractHandler;
    }
}
