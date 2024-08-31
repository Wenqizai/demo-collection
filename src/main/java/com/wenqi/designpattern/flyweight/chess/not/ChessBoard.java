package com.wenqi.designpattern.flyweight.chess.not;

import java.util.HashMap;
import java.util.Map;

/**
 * 棋盘
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
     * 棋盘初始化, 每个棋子都要new
     */
    private void init() {
        chessPieces.put(1, new ChessPiece(1, "兵", ChessPiece.Color.RED, 0, 0));
        chessPieces.put(2, new ChessPiece(2, "将", ChessPiece.Color.BLACK, 0, 1));
        // 省略其他...
    }

    public void move(int chessPieceId, int toPositionX, int toPositionY) {

    }

}
