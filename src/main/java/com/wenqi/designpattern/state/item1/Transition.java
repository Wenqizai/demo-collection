package com.wenqi.designpattern.state.item1;

import lombok.Getter;

/**
 * 动作基类, 维护触发该动作的事件，触发前状态，触发后状态，以及具体的动作内容。
 * @author liangwenqi
 * @date 2022/2/6
 */
public abstract class Transition {

    /**
     * 触发的事件
     */
    @Getter
    private String eventCode;

    /**
     * 触发当前的状态
     */
    @Getter
    private State currState;

    /**
     * 触发后状态
     */
    @Getter
    private State nextState;

    public Transition(String eventCode, State currState, State nextState) {
        this.eventCode = eventCode;
        this.currState = currState;
        this.nextState = nextState;
    }

    /**
     * 执行动作
     * @param event
     * @return
     */
    public State execute(Event event) {
        System.out.println(String.format("当前是：%s 状态，执行：%s 操作后，流转成：%s 状态。", currState, eventCode, nextState));
        if (this.doExecute(event)) {
            return this.nextState;
        } else {
            return null;
        }
    }

    /**
     * 执行动作的具体业务
     * @param event
     * @return
     */
    protected abstract boolean doExecute(Event event);
}
