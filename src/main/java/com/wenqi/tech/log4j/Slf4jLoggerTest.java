package com.wenqi.tech.log4j;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author liangwenqi
 * @date 2024/4/16
 */
public class Slf4jLoggerTest {
    Logger logger = LoggerFactory.getLogger(Slf4jLoggerTest.class);

    public void logApp() {
        logger.debug("log4j2 success ===== debug:{}, {}", "a", "b");
        logger.warn("log4j2 success ===== warn");
        logger.info("log4j2 success ===== info");

        try {
            int notTouch = 1 / 0;
        } catch (Exception e) {
            logger.error(System.getProperty("user.home") + " ===== error", e);
        }
    }


    public static void main(String[] args) {
        Slf4jLoggerTest application = new Slf4jLoggerTest();
        application.logApp();
    }
}
