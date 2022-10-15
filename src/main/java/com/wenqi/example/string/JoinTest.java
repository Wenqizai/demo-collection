package com.wenqi.string;

import com.google.common.collect.Lists;
import org.apache.commons.lang3.StringUtils;

import java.util.List;

/**
 * @author liangwenqi
 * @date 2022/1/18
 */
public class JoinTest {
    public static void main(String[] args) {
        List<String> list = null;
        String bigCategoryId = StringUtils.join(Lists.newArrayList(), ",");
        String midCategoryId = StringUtils.join(list, ",");
    }
}
