package com.wenqi.designpattern.command.demo01;

/**
 * @author liangwenqi
 * @date 2023/8/30
 */
public class CopyCommand extends Command {
    public CopyCommand(Editor editor) {
        super(editor);
    }

    @Override
    public boolean execute() {
        editor.clipboard = editor.textField.getSelectedText();
        return false;
    }
}
