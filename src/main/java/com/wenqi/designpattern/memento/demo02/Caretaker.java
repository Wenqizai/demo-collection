package com.wenqi.designpattern.memento.demo02;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Wenqi Liang
 * @date 9/3/2023
 */
public class Caretaker {
    private int index = 1;
    Map<Integer, Memento> mementos = new HashMap<>();

    public void add(Memento memento) {
        mementos.put(index, memento);
        index += 1;
    }

    /**
     * 获取备份类
     */
    public Memento get(int key) {
        return mementos.get(key);
    }

    /**
     * 重置
     */
    public void remove() {
        mementos.clear();
    }
}
