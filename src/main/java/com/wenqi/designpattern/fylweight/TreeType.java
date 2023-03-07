package com.wenqi.designpattern.fylweight;

import java.awt.Color;
import java.awt.Graphics;

/**
 * 包含多棵树共享的状态
 * @author liangwenqi
 * @date 2022/4/12
 */
public class TreeType {
    private String name;
    private Color color;
    private String otherTreeData;

    public TreeType(String name, Color color, String otherTreeData) {
        this.name = name;
        this.color = color;
        this.otherTreeData = otherTreeData;
    }

    public void draw(Graphics graphics, int x, int y) {
        graphics.setColor(Color.BLACK);
        graphics.fillRect(x - 1, y, 3, 5);
        graphics.setColor(color);
        graphics.fillOval(x - 5, y - 10, 10, 10);
    }
}
