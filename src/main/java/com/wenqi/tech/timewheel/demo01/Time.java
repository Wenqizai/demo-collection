package com.wenqi.tech.timewheel.demo01;

import java.util.concurrent.TimeUnit;

/**
 * @author liangwenqi
 * @date 2023/7/19
 */
public class Time {
    private Time() {
    }

    public static Long getHiresClockMs() {
        return TimeUnit.NANOSECONDS.toMillis(System.nanoTime());
    }
}
