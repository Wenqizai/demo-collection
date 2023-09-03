package com.wenqi.designpattern.memento.demo01;

import java.awt.*;
import java.io.Serializable;

/**
 * @author Wenqi Liang
 * @date 9/3/2023
 */
public interface Shape extends Serializable {
    int getX();

    int getY();

    int getWidth();

    int getHeight();

    void drag();

    void drop();

    void moveTo(int x, int y);

    void moveBy(int x, int y);

    boolean isInsideBounds(int x, int y);

    Color getColor();

    void setColor(Color color);

    void select();

    void unSelect();

    boolean isSelected();

    void paint(Graphics graphics);
}