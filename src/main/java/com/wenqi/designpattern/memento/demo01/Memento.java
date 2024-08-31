package com.wenqi.designpattern.memento.demo01;

/**
 * @author Wenqi Liang
 * @date 9/3/2023
 */
public class Memento {
    private String backup;
    private Editor editor;

    public Memento(Editor editor) {
        this.editor = editor;
        this.backup = editor.backup();
    }

    public void restore() {
        editor.restore(backup);
    }
}
