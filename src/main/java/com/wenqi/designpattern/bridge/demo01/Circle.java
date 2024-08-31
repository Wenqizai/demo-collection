package com.wenqi.designpattern.bridge.demo01;

/**
 * @author liangwenqi
 * @date 2023/5/30
 */
public class Circle extends Shape {
    private int x, y, radius;

    public Circle(DrawAPI drawAPI, int x, int y, int radius) {
        super(drawAPI);
        this.x = x;
        this.y = y;
        this.radius = radius;
    }

    public Circle(DrawAPI drawAPI) {
        super(drawAPI);

    }

    @Override
    public void draw() {
        drawAPI.drawCircle(radius, x, y);
    }
}
