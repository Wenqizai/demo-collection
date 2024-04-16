package com.wenqi.tech.log4j;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * @author liangwenqi
 * @date 2024/4/16
 */
public class LoggerManagerTest {
    static {
        LogManager.getRootLogger().atLevel(Level.ALL);
    }

    private final Logger logger = LogManager.getLogger();


    public void TestBuilder() {
        System.out.println(logger.getLevel());
        logger.atError().withThrowable(new Throwable("test")).log("hello throwable");
        System.out.println("##########== 分割线 ==##########");
        logger.atError().withLocation().log("Hello");
    }

    public static void main(String[] args) {
        new LoggerManagerTest().TestBuilder();
    }
}
