package com.wenqi.designpattern.bridge.demo01;

/**
 * @author liangwenqi
 * @date 2023/5/30
 */
public abstract class Shape {
    protected DrawAPI drawAPI;

    public Shape(DrawAPI drawAPI) {
        this.drawAPI = drawAPI;
    }

    public abstract void draw();
}
