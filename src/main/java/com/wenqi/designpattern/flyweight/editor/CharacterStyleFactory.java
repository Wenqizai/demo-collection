package com.wenqi.designpattern.flyweight.editor;

import java.awt.Font;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * @author liangwenqi
 * @date 2023/3/21
 */
public class CharacterStyleFactory {
    private CharacterStyleFactory() {}

    private static final List<CharacterStyle> STYLES = new ArrayList<>();

    public static CharacterStyle getStyle(Font font, int size, int colorRGB) {
        final CharacterStyle newStyle = new CharacterStyle(font, size, colorRGB);
        for (CharacterStyle style : STYLES) {
            if (Objects.equals(style, newStyle)) {
                return style;
            }
        }
        STYLES.add(newStyle);
        return newStyle;
    }
}
