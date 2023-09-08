package com.wenqi.designpattern.command.demo02;

/**
 * @author Wenqi Liang
 * @date 9/2/2023
 */
public class BuyStock implements Order{
    private Stock abcStock;

    public BuyStock(Stock abcStock) {
        this.abcStock = abcStock;
    }

    @Override
    public void execute() {
        abcStock.buy();
    }
}
