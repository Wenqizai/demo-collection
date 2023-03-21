package com.wenqi.designpattern.flyweight.editor;

/**
 * @author liangwenqi
 * @date 2023/3/21
 */
public class Character {
    private char c;
    private CharacterStyle style;

    public Character(char c, CharacterStyle style) {
        this.c = c;
        this.style = style;
    }
}
