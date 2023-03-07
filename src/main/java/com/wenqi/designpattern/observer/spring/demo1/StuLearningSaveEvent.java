package com.wenqi.designpattern.observer.spring.demo1;

import org.springframework.context.ApplicationEvent;

/**
 * @author liangwenqi
 * @date 2022/3/9
 */
public class StuLearningSaveEvent<StatsStuLearningTime> extends ApplicationEvent {

    private StatsStuLearningTime statsStuLearningTime;

    public StuLearningSaveEvent(Object source, StatsStuLearningTime statsStuLearningTime) {
        super(source);
        this.statsStuLearningTime = statsStuLearningTime;
    }

    public StatsStuLearningTime getStatsStuLearningTime(){
        return statsStuLearningTime;
    }
}