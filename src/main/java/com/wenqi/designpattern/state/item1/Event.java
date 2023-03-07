package com.wenqi.designpattern.state.item1;

import lombok.Getter;
import lombok.Setter;

import java.util.Map;

/**
 * 事件，维护事件编码，以及事件附属的业务参数信息。
 *
 * @author liangwenqi
 * @date 2022/2/6
 */
public class Event {
    /**
     * 事件的标识
     */
    @Getter
    private String eventCode;

    /**
     * 附属的业务的参数
     */
    @Getter
    @Setter
    private Map<Object, Object> attributes = null;

    public Event(String eventCode) {
        this.eventCode = eventCode;
    }

    public Event(String eventCode, Map<Object, Object> attributes) {
        this.eventCode = eventCode;
        this.attributes = attributes;
    }

    @Override
    public String toString() {
        return eventCode;
    }
}
