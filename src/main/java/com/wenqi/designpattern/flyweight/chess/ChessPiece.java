package com.wenqi.designpattern.flyweight.chess;

/**
 * 享元模式 棋子:
 *  可变部分: positionX, positionY
 *  不可变部分: ChessPieceUnit
 *  抽取不可变部分, 可复用对象
 *
 * @author liangwenqi
 * @date 2023/3/21
 */
public class ChessPiece {
    private ChessPieceUnit chessPieceUnit;
    private int positionX;
    private int positionY;

    public ChessPiece(ChessPieceUnit chessPieceUnit, int positionX, int positionY) {
        this.chessPieceUnit = chessPieceUnit;
        this.positionX = positionX;
        this.positionY = positionY;
    }
}
