package com.wenqi.designpattern.state.item1;

import java.util.ArrayList;
import java.util.List;

/**
 * 简单的请假审批状态机
 *
 * @author liangwenqi
 * @date 2022/2/6
 */
public class AuditStateMachine extends StateMachine {

    public static void main(String[] args) {
        StateMachine sm = new AuditStateMachine();
        State state = sm.execute(StateCodeContents.PENDING, new Event(EventCodeContents.PASS));
        sm.execute(StateCodeContents.PASSED, new Event(EventCodeContents.REFUSE));
        System.out.println(state);
    }

    @Override
    public List<State> declareAllStates() { // 定义状态机的状态
        List<State> stateList = new ArrayList<>();

        State pendingState = new State(StateCodeContents.PENDING);
        State passedState = new State(StateCodeContents.PASSED);
        State refusedState = new State(StateCodeContents.REFUSED);

        pendingState.addTransition(new PassTransition(pendingState, passedState));
        pendingState.addTransition(new RefuseTransition(pendingState, refusedState));

        stateList.add(pendingState);
        stateList.add(passedState);
        stateList.add(refusedState);

        return stateList;
    }

    /**
     * 定义“通过”动作
     */
    public static class PassTransition extends Transition {
        public PassTransition(State currState, State nextState) {
            super(EventCodeContents.PASS, currState, nextState);
        }

        @Override
        protected boolean doExecute(Event event) {
            System.out.println("执行通过操作...");
            return true;
        }

    }

    /**
     * 定义“拒绝”动作
     */
    public static class RefuseTransition extends Transition {
        public RefuseTransition(State currState, State nextState) {
            super(EventCodeContents.REFUSE, currState, nextState);
        }

        @Override
        protected boolean doExecute(Event event) {
            System.out.println("执行拒绝操作...");
            return false;
        }

    }

    /**
     * 事件编码
     */
    public static class EventCodeContents {
        public static final String PASS = "PASS";
        public static final String REFUSE = "REFUSE";
    }

    /**
     * 状态编码
     */
    public static class StateCodeContents {
        public static final String PENDING = "待审批";
        public static final String PASSED = "审批通过";
        public static final String REFUSED = "审批拒绝";
    }
}
