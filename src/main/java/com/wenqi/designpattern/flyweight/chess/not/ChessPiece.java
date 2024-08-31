package com.wenqi.designpattern.flyweight.chess.not;


/**
 * 棋子:
 *  可变部分: positionX, positionY
 *  不可变部分: id, text, color
 * @author liangwenqi
 * @date 2023/3/21
 */
public class ChessPiece {
    private int id;
    private String text;
    private Color color;
    private int positionX;
    private int positionY;

    public enum Color {
        RED, BLACK
    }

    public ChessPiece(int id, String text, Color color, int positionX, int positionY) {
        this.id = id;
        this.text = text;
        this.color = color;
        this.positionX = positionX;
        this.positionY = positionY;
    }
}
