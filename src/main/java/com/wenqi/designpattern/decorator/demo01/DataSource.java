package com.wenqi.designpattern.decorator.demo01;

/**
 * 定义了读取和写入操作的通用数据接口
 *
 * @author liangwenqi
 * @date 2023/6/12
 */
public interface DataSource {
    void writeData(String data);

    String readData();
}
