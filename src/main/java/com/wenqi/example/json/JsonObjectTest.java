package com.wenqi.example.json;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

/**
 * @author liangwenqi
 * @date 2023/11/24
 */
public class JsonObjectTest {
    public static void main(String[] args) {
        // jsonObject == null
        JSONObject jsonObject = JSON.parseObject("");
        // jsonObject2 == null
        JSONObject jsonObject2 = JSON.parseObject(null);

        // NullPointerException
        System.out.println(jsonObject.get("name"));
        System.out.println(jsonObject2.get("name"));
    }
}
