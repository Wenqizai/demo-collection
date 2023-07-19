package com.wenqi.tech.timewheel.demo01;

import java.util.Date;

/**
 * @author liangwenqi
 * @date 2023/7/19
 */
public class DelayedOperation extends TimerTask {

    public DelayedOperation(long delayMs) {
        super.delayMs = delayMs;
    }

    @Override
    public void run() {
        System.out.println("do the job : " + new Date());
    }
}
