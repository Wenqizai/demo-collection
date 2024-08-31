package com.wenqi.designpattern.state.item3.observer;

import org.springframework.util.CollectionUtils;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

/**
 * 抽象目标 (注意并发安全问题)
 *
 * @author liangwenqi
 * @date 2022/3/23
 */
public abstract class Subject {
    /**
     * 延时加载
     */
    protected Set<Observer> observers;
    /**
     * 锁
     */
    private final Object MONITOR = new Object();

    public void add(Observer observer) {
        if (Objects.isNull(observer)) {
            return;
        }
        synchronized (MONITOR) {
            if (Objects.isNull(observers)) {
                observers = new HashSet<>();
            }
            observers.add(observer);
        }
    }

    public void remove(Observer observer) {
        if (Objects.isNull(observer)) {
            return;
        }
        synchronized (MONITOR) {
            if (!CollectionUtils.isEmpty(observers)) {
                observers.remove(observer);
            }
        }
    }

    public void notifyObserver(Long taskId) {
        if (observers == null) {
            return;
        }

        Set<Observer> observersCopy;

        synchronized(MONITOR) {
            // 采用副本, 提高吞吐量
            observersCopy = new HashSet<>(observers);
        }

        for (Observer observer : observersCopy) {
            observer.response(taskId);
        }
    }
}
