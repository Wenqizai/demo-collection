package com.wenqi.designpattern.command.demo02;

/**
 * @author Wenqi Liang
 * @date 9/2/2023
 */
public class SellStock implements Order {
    private Stock abcStock;

    public SellStock(Stock abcStock) {
        this.abcStock = abcStock;
    }

    @Override
    public void execute() {
        abcStock.sell();
    }
}
