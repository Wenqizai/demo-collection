package com.wenqi.designpattern.command.demo01;

import java.util.Stack;

/**
 * @author liangwenqi
 * @date 2023/8/30
 */
public class CommandHistory {
    private Stack<Command> history = new Stack<>();

    public void push(Command c) {
        history.push(c);
    }

    public Command pop() {
        return history.pop();
    }

    public boolean isEmpty() {
        return history.isEmpty();
    }
}
