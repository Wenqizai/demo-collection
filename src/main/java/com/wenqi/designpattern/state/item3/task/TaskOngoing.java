package com.wenqi.designpattern.state.item3.task;

import com.wenqi.designpattern.state.item3.related.ActionType;
import com.wenqi.designpattern.state.item3.related.ActivityService;
import com.wenqi.designpattern.state.item3.related.TaskManager;

/**
 * 任务进行状态
 * @author liangwenqi
 * @date 2022/3/23
 */
public class TaskOngoing implements State {
    private ActivityService activityService;
    private TaskManager taskManager;

    @Override
    public void update(Task task, ActionType actionType) {
        if (actionType == ActionType.ACHIEVE) {
            task.setState(new TaskFinished());
            // 通知
            activityService.notifyFinished(task.getTaskId());
            taskManager.release(task.getTaskId());
        } else if (actionType == ActionType.STOP) {
            task.setState(new TaskPaused());
        } else if (actionType == ActionType.EXPIRE) {
            task.setState(new TaskExpired());
        }
    }
}
