package com.wenqi.designpattern.bridge.demo01;

/**
 * @author liangwenqi
 * @date 2023/5/30
 */
public class BridgePatternDemo {
    public static void main(String[] args) {
        Shape redCircle = new Circle(new RedCircle(), 100, 100, 10);
        Shape greenCircle = new Circle(new GreenCircle(), 100, 100, 10);

        redCircle.draw();
        greenCircle.draw();
    }
}
