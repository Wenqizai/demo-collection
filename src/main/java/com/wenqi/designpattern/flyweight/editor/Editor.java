package com.wenqi.designpattern.flyweight.editor;

import java.awt.Font;
import java.util.ArrayList;
import java.util.List;

/**
 * @author liangwenqi
 * @date 2023/3/21
 */
public class Editor {
    private List<Character> chars = new ArrayList<>();

    public void appendCharacter(char c, Font font, int size, int colorRGB) {
        final Character character = new Character(c, CharacterStyleFactory.getStyle(font, size, colorRGB));
        chars.add(character);
    }
}
