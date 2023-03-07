package com.wenqi.designpattern.state.item3.task;

import com.wenqi.designpattern.state.item3.related.ActionType;

/**
 * 任务暂停状态
 *
 * @author liangwenqi
 * @date 2022/3/23
 */
public class TaskPaused implements State {
    @Override
    public void update(Task task, ActionType actionType) {
        if (actionType == ActionType.START) {
            task.setState(new TaskOngoing());
        } else if (actionType == ActionType.EXPIRE) {
            task.setState(new TaskExpired());
        }
    }
}
