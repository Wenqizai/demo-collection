package com.wenqi.tech.timewheel.demo01;

/**
 * 封装定时任务
 *
 * @author liangwenqi
 * @date 2023/7/19
 */
public class TimerTaskEntry implements Comparable<TimerTaskEntry> {
    private volatile TimerTaskList list;
    public TimerTaskEntry next;
    public TimerTaskEntry prev;
    private TimerTask timerTask;
    private Long expirationMs;

    public TimerTaskEntry() {
    }

    /**
     * 构造器
     *
     * @param timerTask    定时任务
     * @param expirationMs 到期时间
     */
    public TimerTaskEntry(TimerTask timerTask, Long expirationMs) {
        this.timerTask = timerTask;
        this.expirationMs = expirationMs;
    }

    public boolean cancelled() {
        return timerTask.getTimerTaskEntry() != this;
    }

    public void remove() {
        TimerTaskList currentList = list;
        // if remove is called when another thread is moving the entry from a task entry list to another,
        // this may fail to remove the entry due to the change of value of list. Thus, we retry until the list becomes null.
        // In a rare case, this thread sees null and exits the loop, but the other thread insert the entry to another list later.
        while (currentList != null) {
            currentList.remove(this);
            currentList = list;
        }
    }

    @Override
    public int compareTo(TimerTaskEntry that) {
        if (that == null) {
            throw new NullPointerException("TimerTaskEntry is null");
        }
        return Long.compare(this.expirationMs, that.expirationMs);
    }

    public TimerTaskList getList() {
        return list;
    }

    public void setList(TimerTaskList list) {
        this.list = list;
    }

    public TimerTask getTimerTask() {
        return timerTask;
    }

    public Long getExpirationMs() {
        return expirationMs;
    }
}
