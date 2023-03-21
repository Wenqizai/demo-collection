package com.wenqi.designpattern.flyweight.editor;

import java.awt.Font;
import java.util.Objects;

/**
 * @author liangwenqi
 * @date 2023/3/21
 */
public class CharacterStyle {
    private Font font;
    private int size;
    private int colorRGB;

    public CharacterStyle(Font font, int size, int colorRGB) {
        this.font = font;
        this.size = size;
        this.colorRGB = colorRGB;
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) {
            return true;
        }
        if (!(object instanceof CharacterStyle)) {
            return false;
        }
        CharacterStyle that = (CharacterStyle) object;
        return size == that.size &&
                colorRGB == that.colorRGB &&
                Objects.equals(font, that.font);
    }

    @Override
    public int hashCode() {
        return Objects.hash(font, size, colorRGB);
    }
}
