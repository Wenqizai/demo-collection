package com.wenqi.designpattern.fylweight;

import java.awt.Color;
import java.util.HashMap;
import java.util.Map;

/**
 * 封装创建享元的复杂机制
 *
 * @author liangwenqi
 * @date 2022/4/12
 */
public class TreeFactory {
    static Map<String, TreeType> treeTypes = new HashMap<>();

    public static TreeType getTreeType(String name, Color color, String otherTreeData) {
        TreeType result = treeTypes.get(name);
        if (result == null) {
            result = new TreeType(name, color, otherTreeData);
            treeTypes.put(name, result);
        }
        return result;
    }
}
