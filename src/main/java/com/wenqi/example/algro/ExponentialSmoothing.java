package com.wenqi.example.algro;

/**
 * @author liangwenqi
 * @date 2024/7/15
 */
public class ExponentialSmoothing {
    public static double[] exponentialSmoothing(double[] data, double alpha) {
        int n = data.length;
        double[] smoothedData = new double[n];

        // 初始化第一个平滑值为原始数据的第一个值
        smoothedData[0] = data[0];

        // 计算其他平滑值
        for (int i = 1; i < n; i++) {
            smoothedData[i] = alpha * data[i] + (1 - alpha) * smoothedData[i - 1];
        }

        return smoothedData;
    }
    public static double[] exponentialSmoothing2(double[] data, double alpha) {
        double[] smoothedData = new double[data.length];
        for (int i = 0; i < data.length; i++) {
            double x = (double) i / (data.length - 1);
            smoothedData[i] = 20 + 0.5 * Math.sin(2 * Math.PI * x); // 使用正弦函数进行插值
        }
        return smoothedData;
    }



    public static void main(String[] args) {
        double[] data = {20, 20, 20, 20, 20, 20, 20, 20, 20, 20};
        double alpha = 0.1;

        double[] smoothedData = exponentialSmoothing(data, alpha);

        System.out.println("原始数据: ");
        for (double value : data) {
            System.out.print(value + " ");
        }
        System.out.println("\n平滑后数据: ");
        for (double value : smoothedData) {
            System.out.print(value + " ");
        }
    }
}
