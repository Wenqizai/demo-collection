package com.wenqi.designpattern.memento.demo03;

import java.util.Objects;
import java.util.Scanner;
import java.util.Stack;

/**
 * 这里的数据每次都是备份都是全量备份, 消耗大量资源和时间
 *
 * @author Wenqi Liang
 * @date 9/3/2023
 */
public class InputText2 {
    private StringBuilder text = new StringBuilder();

    public String getText() {
        return text.toString();
    }

    public void append(String input) {
        text.append(input);
    }

    public Snapshot createSnapshot() {
        return new Snapshot(text.toString());
    }

    public void restoreSnapshot(Snapshot snapshot) {
        this.text.replace(0,this.text.length(), snapshot.getText());
    }
}

class Snapshot {
    private String text;

    public Snapshot(String text) {
        this.text = text;
    }

    public String getText() {
        return text;
    }
}

class SnapshotHolder2 {
    private Stack<Snapshot> snapshots = new Stack<>();

    public Snapshot popSnapshot() {
        return snapshots.pop();
    }

    public void pushSnapshot(Snapshot snapshot) {
        snapshots.push(snapshot);
    }
}

class ApplicationMain2 {
    public static void main(String[] args) {
        InputText2 inputText = new InputText2();
        SnapshotHolder2 snapshotHolder = new SnapshotHolder2();
        Scanner scanner = new Scanner(System.in);
        while (scanner.hasNext()) {
            String input = scanner.next();
            if (Objects.equals(input, ":list")) {
                System.out.println(inputText);
            } else if (Objects.equals(input, ":undo")) {
                snapshotHolder.popSnapshot();
            } else {
                snapshotHolder.pushSnapshot(inputText.createSnapshot());
                inputText.append(input);
            }
        }
    }
}
