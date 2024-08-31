package com.wenqi.designpattern.decorator.demo01;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStream;

/**
 * 简单数据读写器(无装饰器模式)
 *
 * @author liangwenqi
 * @date 2023/6/12
 */
public class FileDataSource implements DataSource {
    private String name;

    public FileDataSource(String name) {
        this.name = name;
    }

    @Override
    public void writeData(String data) {
        File file = new File(name);
        try (OutputStream fos = new FileOutputStream(file)) {
            fos.write(data.getBytes(), 0, data.length());
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    @Override
    public String readData() {
        char[] buffer = null;
        File file = new File(name);
        try (FileReader reader = new FileReader(file)){
            buffer = new char[(int) file.length()];
            reader.read(buffer);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return new String(buffer);
    }
}
