package com.wenqi.designpattern.state.item3.observer;

import com.wenqi.designpattern.state.item3.related.ActivityService;

/**
 * 活动观察者
 * @author liangwenqi
 * @date 2022/3/23
 */
public class ActivityObserver implements Observer {
    private ActivityService activityService;

    @Override
    public void response(Long taskId) {
        activityService.notifyFinished(taskId);
    }
}
