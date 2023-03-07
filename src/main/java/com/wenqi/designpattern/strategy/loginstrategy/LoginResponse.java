package com.wenqi.designpattern.strategy.loginstrategy;

/**
 * @author liangwenqi
 * @date 2022/1/19
 */
public class LoginResponse {

    private Integer code;
    private String msg;
    private Object data;

    public LoginResponse(Integer code, String msg, Object data) {
        this.code = code;
        this.msg = msg;
        this.data = data;
    }

    public static LoginResponse success(String msg, Object data){
        return new LoginResponse(200, msg, data);
    }
}
