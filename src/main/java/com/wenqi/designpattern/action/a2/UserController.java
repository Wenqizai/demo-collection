package com.wenqi.designpattern.action.a2;

import java.util.concurrent.TimeUnit;

/**
 * @author Wenqi Liang
 * @date 2022/10/16
 */
public class UserController {
    private final Metrics metrics = new Metrics();

    public UserController() {
        metrics.startRepeatedReport(60, TimeUnit.SECONDS);
    }

    /**
     * 监控代码侵入性强
     * @param user
     */
    public void register(UserVo user) {
        long startTimestamp = System.currentTimeMillis();
        metrics.recordTimestamp("register", startTimestamp);

        long responseTime = System.currentTimeMillis() - startTimestamp;
        metrics.recordTimestamp("register", responseTime);
    }

    /**
     * 代码侵入性强
     * @param telephone
     * @param password
     * @return
     */
    public UserVo login(String telephone, String password) {
        long startTimestamp = System.currentTimeMillis();
        metrics.recordTimestamp("login", startTimestamp);

        long responseTime = System.currentTimeMillis() - startTimestamp;
        metrics.recordTimestamp("login", responseTime);

        return null;
    }
}
