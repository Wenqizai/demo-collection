package com.wenqi.designpattern.flyweight.chess;

import java.util.HashMap;
import java.util.Map;

/**
 * 工厂类: 缓存享元信息
 *
 * @author liangwenqi
 * @date 2023/3/21
 */
public class ChessPieceUnitFactory {
    private ChessPieceUnitFactory() {}

    private static final Map<Integer, ChessPieceUnit> pieces = new HashMap<>();

    static {
        pieces.put(1, new ChessPieceUnit(1, "兵", ChessPieceUnit.Color.RED));
        pieces.put(2, new ChessPieceUnit(2, "将", ChessPieceUnit.Color.BLACK));
        // 省略其他...
    }

    public static ChessPieceUnit getChessPiece(int chessPieceId) {
        return pieces.get(chessPieceId);
    }
}
