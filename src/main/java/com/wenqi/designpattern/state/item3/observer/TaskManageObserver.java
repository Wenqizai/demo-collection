package com.wenqi.designpattern.state.item3.observer;

import com.wenqi.designpattern.state.item3.related.TaskManager;

/**
 * 任务管理观察者
 *
 * @author liangwenqi
 * @date 2022/3/23
 */
public class TaskManageObserver implements Observer {
    private TaskManager taskManager;

    @Override
    public void response(Long taskId) {
        taskManager.release(taskId);
    }
}
