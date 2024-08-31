package com.wenqi.designpattern.state.item3.task;

import com.wenqi.designpattern.state.item3.related.ActionType;

/**
 * 任务状态抽象接口
 * @author liangwenqi
 * @date 2022/3/23
 */
public interface State {
    /**
     * 默认实现, 不做任何处理
     * @param task
     * @param actionType
     */
    void update(Task task, ActionType actionType);
}
