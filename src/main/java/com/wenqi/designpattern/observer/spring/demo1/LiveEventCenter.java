package com.wenqi.designpattern.observer.spring.demo1;

import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

/**
 * @author liangwenqi
 * @date 2022/3/9
 */
@Slf4j
@Component
public class LiveEventCenter {

    @Autowired
    private ApplicationEventPublisher eventPublisher;

    /**
     * 发布接入直播信息事件
     * @param statsStuLearningTime
     */
    public void publish(@NonNull StatsStuLearningTime statsStuLearningTime) {
        log.debug("start to publish live info event:[{}],", statsStuLearningTime);
        eventPublisher.publishEvent(new StuLearningSaveEvent<>(this, statsStuLearningTime));
    }

}