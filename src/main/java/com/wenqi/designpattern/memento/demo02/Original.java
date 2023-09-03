package com.wenqi.designpattern.memento.demo02;


/**
 * @author Wenqi Liang
 * @date 9/3/2023
 */
public class Original {
    private String name;
    private int timestamp;

    public Memento createMemento() {
        return new Memento(name, timestamp);
    }

    public void getOriginalFromMemento(Memento memento) {
        name = memento.getName();
        timestamp = memento.getTimestamp();
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

    @Override
    public String toString() {
        return "Original{" +
                "name='" + name + '\'' +
                ", timestamp=" + timestamp +
                '}';
    }
}
