package com.wenqi.designpattern.bridge.demo01;

/**
 * @author liangwenqi
 * @date 2023/5/30
 */
public class RedCircle implements DrawAPI {
    @Override
    public void drawCircle(int radius, int x, int y) {
        System.out.println("Drawing Circle [ color: red, radius: " + radius + ", x: " + x + "," + y + "]");
    }
}
