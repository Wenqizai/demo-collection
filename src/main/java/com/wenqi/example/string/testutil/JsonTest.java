package com.wenqi.example.string.testutil;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import org.apache.commons.lang3.StringUtils;

import java.util.Objects;

/**
 * @author liangwenqi
 * @date 2023/4/4
 */
public class JsonTest {
    public static void main(String[] args) {
        JSONObject jsonObject = JSON.parseObject("{\"CODE\":\"S\",\"MESSAGE\":\"数据不存在！\",\"NOW\":\"\"}");
        String dataName = "DATA";
        if (Objects.isNull(jsonObject) || StringUtils.isEmpty(jsonObject.getString(dataName)) || Objects.equals(jsonObject.getString(dataName), "null")) {
            System.out.println(Objects.isNull(jsonObject));
            System.out.println(StringUtils.isEmpty(jsonObject.getString(dataName)));
            System.out.println(Objects.equals(jsonObject.getString(dataName), "null"));
        }

    }
}
