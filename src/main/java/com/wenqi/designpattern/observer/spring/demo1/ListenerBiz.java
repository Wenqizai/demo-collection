package com.wenqi.designpattern.observer.spring.demo1;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

/**
 * @author liangwenqi
 * @date 2022/3/9
 */
@Slf4j
@Service
public class ListenerBiz {

    @Async
    @EventListener
    public void saveLiveInfo(StuLearningSaveEvent<StatsStuLearningTime> stuLearningSaveEvent){
        StatsStuLearningTime statsStuLearningTime = stuLearningSaveEvent.getStatsStuLearningTime();
        System.out.println("收到的事件:" + statsStuLearningTime);
    }

}