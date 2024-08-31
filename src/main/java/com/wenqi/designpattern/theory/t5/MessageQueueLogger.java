package com.wenqi.designpattern.theory.t5;

import java.io.IOException;

/**
 * @author Wenqi Liang
 * @date 2022/10/5
 */
public class MessageQueueLogger extends Logger{
    private MessageQueueClient client;

    public MessageQueueLogger(String name, boolean enabled, LeveL minPermittedLevel, MessageQueueClient client) {
        super(name, enabled, minPermittedLevel);
        this.client = client;
    }

    @Override
    protected void doLog(LeveL leveL, String message) throws IOException {
        // 格式化 level 和 message, 输出到消息中间件
        client.send(message);
    }
}
