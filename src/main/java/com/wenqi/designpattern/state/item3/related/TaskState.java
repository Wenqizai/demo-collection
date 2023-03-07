package com.wenqi.designpattern.state.item3.related;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 任务状态枚举
 *
 * @author liangwenqi
 * @date 2022/3/23
 */
@AllArgsConstructor
@Getter
public enum TaskState {
    INIT("初始化"),
    ONGOING("进行中"),
    PAUSED("暂停中"),
    FINISHED("已完成"),
    EXPIRED("已过期"),
    ;

    private final String message;
}
