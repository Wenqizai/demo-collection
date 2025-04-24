package com.wenqi.example.optional;



import cn.hutool.core.lang.Pair;

import java.util.Optional;

/**
 * @author Wenqi Liang
 * @date 8/27/2023
 */
public class OptionalTest {
    public static void main(String[] args) {
        Pair<Object, Object> pair = new Pair<>("key", "value");

        Optional.ofNullable(pair).ifPresent(e -> {
            System.out.println(e.getKey());
        });

        System.out.println(Optional.ofNullable(pair).map(Pair::getValue).orElse(null));

        System.out.println(Optional.ofNullable(new Pair<>(null, null)).map(Pair::getValue).orElse("indeed null"));


    }
}
