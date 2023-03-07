package com.wenqi.designpattern.strategy.loginstrategy;

import lombok.Data;

/**
 * @author liangwenqi
 * @date 2022/1/19
 */
@Data
public class LoginRequest {

    private LoginType loginType;
    private Long userId;

}
