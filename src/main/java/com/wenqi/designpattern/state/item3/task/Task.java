package com.wenqi.designpattern.state.item3.task;

import com.wenqi.designpattern.state.item3.related.ActionType;
import lombok.Data;

/**
 * @author liangwenqi
 * @date 2022/3/23
 */
@Data
public class Task {
    private Long taskId;
    /**
     * 初始化为初始态
     */
    private State state = new TaskInit();

    /**
     * 更新状态
     * @param actionType
     */
    public void updateState(ActionType actionType) {
        state.update(this, actionType);
    }
}
