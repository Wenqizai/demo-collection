package com.wenqi.example.stream;

import com.google.common.collect.Lists;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 关于 stream 的源码分析: <a href="https://mp.weixin.qq.com/s/UGWoRO5-pFB0p01mc73wLA">...</a>
 * 带着问题出发:
 * 1. 为什么 stream 是懒加载?
 * 2. 当 stream 生成 collection 为空时， 会触发 stream 的懒加载么?
 *
 * <p>
 * 回答:
 * 问题1:
 *  stream 的懒加载是针对 stream 内部实现来说, 并不是使用容器时才触发 stream 的流水线操作.
 *  stream 的懒加载是当执行终端操作时, stream 开始整体的流水线操作, 不改变原理容器, 生成一个新的容器.
 * 问题2:
 *  这个懒加载已经触发了, 当使用终端操作 `.collect(Collectors.toList())` 时触发.
 *
 * <p>
 * 相关终端操作:
 * forEach() forEachOrdered() toArray()
 * reduce() collect() max() min() count()
 * anyMatch() allMatch() noneMatch()
 * findFirst() findAny()
 *
 * <p>
 * 关于执行终端操作才触发流水线懒加载的好处:
 *  1. 在终端操作之前, 可以很好利用流水线功能, 比如并行操作, 处理大量数据更加高效
 *  2. 也能很好建立和组合处理模型 sink, 更加灵活
 *
 * @author liangwenqi
 * @date 2024/8/6
 */
public class StreamAnalysis {
    public static void main(String[] args) {
        List<String> startlist = Lists.newArrayList("a", "b", "c");
        List<String> endList = startlist
                .stream()
                .map(r -> r + "b")
                .filter(r -> r.startsWith("a"))
                .collect(Collectors.toList());
        if (CollectionUtils.isEmpty(endList)) {
            System.out.println("empty");
        }
    }
}
