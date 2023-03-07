package com.wenqi.designpattern.fylweight;

import java.awt.Graphics;

/**
 * 包含每棵树的独特状态
 *
 * @author liangwenqi
 * @date 2022/4/12
 */
public class Tree {
    private int x;
    private int y;
    private TreeType type;

    public Tree(int x, int y, TreeType type) {
        this.x = x;
        this.y = y;
        this.type = type;
    }

    public void draw(Graphics graphics) {
        type.draw(graphics, x, y);
    }
}
