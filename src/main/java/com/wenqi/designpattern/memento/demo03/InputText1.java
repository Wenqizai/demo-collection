package com.wenqi.designpattern.memento.demo03;

import java.util.Objects;
import java.util.Scanner;
import java.util.Stack;

/**
 * 输入 :list -> 输出内存文本的内容
 * 输入 :undo -> 程序撤销上一次输入的文本, 也就是从内存文本中删除上次输入的文本
 *
 * @author Wenqi Liang
 * @date 9/3/2023
 */
public class InputText1 {
    private StringBuilder text = new StringBuilder();

    public String getText() {
        return text.toString();
    }

    public void setText(String text) {
        this.text.replace(0, this.text.length(), text);
    }

    public void append(String input) {
        text.append(input);
    }
}

class SnapshotHolder {
    private Stack<InputText1> snapshots = new Stack<>();

    public InputText1 popSnapshot() {
        return snapshots.pop();
    }

    public void pushSnapshot(InputText1 inputText1) {
        InputText1 deepClonedInputText1 = new InputText1();
        deepClonedInputText1.setText(inputText1.getText());
        snapshots.push(deepClonedInputText1);
    }
}

class ApplicationMain {
    public static void main(String[] args) {
        InputText1 inputText1 = new InputText1();
        SnapshotHolder snapshotHolder = new SnapshotHolder();
        Scanner scanner = new Scanner(System.in);
        while (scanner.hasNext()) {
            String input = scanner.next();
            if (Objects.equals(input, ":list")) {
                System.out.println(inputText1);
            } else if (Objects.equals(input, ":undo")) {
                InputText1 snapshot = snapshotHolder.popSnapshot();
                inputText1.setText(snapshot.getText());
            } else {
                snapshotHolder.pushSnapshot(inputText1);
                inputText1.append(input);
            }
        }
    }
}