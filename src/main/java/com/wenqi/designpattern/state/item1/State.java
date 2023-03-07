package com.wenqi.designpattern.state.item1;

import lombok.Getter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * 状态，维护状态编码，以及该状态下可支持的动作。
 *
 * @author liangwenqi
 * @date 2022/2/6
 */
public class State {

    /**
     * 状态编码
     */
    @Getter
    private String stateCode;

    /**
     * 当前状态下可允许执行的动作
     */
    @Getter
    private List<Transition> transitions = new ArrayList();

    public State(String stateCode, Transition... transitions) {
        this.stateCode = stateCode;
        this.transitions.addAll(Arrays.asList(transitions));
    }

    /**
     * 添加动作
     * @param transition
     */
    public void addTransition(Transition transition) {
        this.transitions.add(transition);
    }

    @Override
    public String toString() {
        return stateCode;
    }
}
