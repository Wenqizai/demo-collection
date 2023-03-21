package com.wenqi.designpattern.flyweight.chess;


import java.util.HashMap;
import java.util.Map;

/**
 * 享元模式: 棋盘
 *
 * @author liangwenqi
 * @date 2023/3/21
 */
public class ChessBoard {
    private final Map<Integer, ChessPiece> chessPieces = new HashMap<>();

    public ChessBoard() {
        init();
    }

    /**
     * 棋盘初始化, 每个棋子都复用不可变部分
     */
    private void init() {
        chessPieces.put(1, new ChessPiece(ChessPieceUnitFactory.getChessPiece(1), 0, 0));
        chessPieces.put(2, new ChessPiece(ChessPieceUnitFactory.getChessPiece(2), 0, 0));
        // 省略其他...
    }

    public void move(int chessPieceId, int toPositionX, int toPositionY) {

    }

}
