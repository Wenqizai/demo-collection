package com.wenqi.springboot.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.context.request.async.AsyncRequestTimeoutException;

import javax.servlet.http.HttpServletRequest;

/**
 * @author Wenqi Liang
 * @date 2023/6/23
 */
@Slf4j
@ControllerAdvice
public class GlobalControllerExceptionHandler {

    public void handleAsyncRequestTimeoutException(AsyncRequestTimeoutException e, HttpServletRequest request) {
        System.out.println("handleAsyncRequestTimeoutException");
    }
}
