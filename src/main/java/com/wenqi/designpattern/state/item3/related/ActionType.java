package com.wenqi.designpattern.state.item3.related;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 行为枚举
 * @author liangwenqi
 * @date 2022/3/23
 */
@AllArgsConstructor
@Getter
public enum ActionType {
    START(1, "开始"),
    STOP(2, "暂停"),
    ACHIEVE(3, "完成"),
    EXPIRE(4, "过期"),
    ;

    private final int code;
    private final String message;
}