package com.wenqi.designpattern.decorator.demo01;

/**
 * @author liangwenqi
 * @date 2023/6/12
 */
public class DataSourceDecorator implements DataSource {
    private DataSource wrapper;

    public DataSourceDecorator(DataSource dataSource) {
        this.wrapper = dataSource;
    }


    @Override
    public void writeData(String data) {
        wrapper.writeData(data);
    }

    @Override
    public String readData() {
        return wrapper.readData();
    }
}
