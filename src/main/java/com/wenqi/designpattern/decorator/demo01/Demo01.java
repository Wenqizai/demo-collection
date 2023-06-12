package com.wenqi.designpattern.decorator.demo01;

/**
 * @author liangwenqi
 * @date 2023/6/12
 */
public class Demo01 {
    public static void main(String[] args) {
        String salaryRecords = "Name,Salary\nJohn Smith,100000\nSteven Jobs,912000";
        DataSourceDecorator encoded = new CompressionDecorator(
                new EncryptionDecorator(
                        new FileDataSource("src\\main\\java\\com\\wenqi\\designpattern\\decorator\\demo01\\OutputDemo.txt")));
        encoded.writeData(salaryRecords);
        DataSource plain = new FileDataSource("src\\main\\java\\com\\wenqi\\designpattern\\decorator\\demo01\\OutputDemo.txt");

        System.out.println("- Input ----------------");
        System.out.println(salaryRecords);
        System.out.println("- Encoded --------------");
        System.out.println(plain.readData());
        System.out.println("- Decoded --------------");
        System.out.println(encoded.readData());
    }
}
