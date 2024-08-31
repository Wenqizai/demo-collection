package com.wenqi.tech.timewheel.demo01;

/**
 * @author liangwenqi
 * @date 2023/7/19
 */
public abstract class TimerTask implements Runnable {
    /**
     * timestamp in milliseconds
     */
    protected Long delayMs = 30000L;
    protected TimerTaskEntry timerTaskEntry;

    public void cancel() {
        synchronized (this) {
            if (timerTaskEntry != null) {
                timerTaskEntry.remove();
            }
            timerTaskEntry = null;
        }
    }

    public void setTimerTaskEntry(TimerTaskEntry entry) {
        // if this timerTask is already held by an existing timer task entry,
        // we will remove such an entry first
        synchronized (this) {
            if (timerTaskEntry != null && timerTaskEntry != entry) {
                timerTaskEntry.remove();
            }
            timerTaskEntry = entry;
        }
    }

    public TimerTaskEntry getTimerTaskEntry() {
        return timerTaskEntry;
    }

    public Long getDelayMs() {
        return delayMs;
    }
}
