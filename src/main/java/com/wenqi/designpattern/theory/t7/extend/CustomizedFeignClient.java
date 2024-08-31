package com.wenqi.designpattern.theory.t7.extend;

/**
 * @author Wenqi Liang
 * @date 2022/10/6
 */
public class CustomizedFeignClient extends FeignClient{
    @Override
    public void encode(String url) {
        // 既然改不了父类, 就用子类实现, 重写 encode 方法达到修改的目的
    }
}
