package com.wenqi.tech.log4j;

import com.alibaba.fastjson.JSON;
import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.PropertyConfigurator;
import org.apache.log4j.xml.DOMConfigurator;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Random;

/**
 * @author liangwenqi
 * @date 2024/4/16
 */
public class TestFlowTracing {

    private final Logger logger = LogManager.getLogger();

    public static void main( String[] args ) {
        TestFlowTracing service = new TestFlowTracing();
        service.retrieveMessage();
        service.retrieveMessage();
        service.exampleException();
    }

    private String[] messages = new String[] {
            "Hello, World",
            "Goodbye Cruel World",
            "You had me at hello"
    };
    private Random rand = new Random(1);

    public void setMessages(String[] messages) {
        logger.traceEntry(JSON.toJSONString(messages));
        this.messages = messages;
        logger.traceExit();
    }

    public String[] getMessages() {
        logger.traceEntry();
        return logger.traceExit(messages);
    }

    public String retrieveMessage() {
        logger.entry();

        String testMsg = getMessage(getKey());

        return logger.exit(testMsg);
    }

    public void exampleException() {
        logger.entry();
        try {
            String msg = messages[messages.length];
            logger.error("An exception should have been thrown");
        } catch (Exception ex) {
            logger.catching(ex);
        }
        logger.exit();
    }

    public String getMessage(int key) {
        logger.entry(key);

        String value = messages[key];

        return logger.exit(value);
    }

    private int getKey() {
        logger.entry();
        int key = rand.nextInt(messages.length);
        return logger.exit(key);
    }
}
