package com.wenqi.designpattern.memento.demo02;

/**
 * @author Wenqi Liang
 * @date 9/3/2023
 */
public class Memento {
    private String name;
    private int timestamp;

    public Memento(String name, int timestamp) {
        this.name = name;
        this.timestamp = timestamp;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(int timestamp) {
        this.timestamp = timestamp;
    }
}
