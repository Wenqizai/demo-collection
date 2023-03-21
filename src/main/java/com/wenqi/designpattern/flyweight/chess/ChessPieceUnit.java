package com.wenqi.designpattern.flyweight.chess;

/**
 * 享元类
 * @author liangwenqi
 * @date 2023/3/21
 */
public class ChessPieceUnit {
    private int id;
    private String text;
    private Color color;

    public ChessPieceUnit(int id, String text, Color color) {
        this.id = id;
        this.text = text;
        this.color = color;
    }

    public enum Color {
        RED, BLACK
    }
}
