package com.wenqi.example.string;

import org.springframework.util.PropertyPlaceholderHelper;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author liangwenqi
 * @date 2023/11/2
 */
public class PlaceholderTest {
    public static void main(String[] args) {
        String str = "我是一个占位符: {1}, 又是另外一个占位符: {2}";
        List<String> values = extractPlaceholderValues(str);
        System.out.println("提取的值: " + values);

        Properties properties = new Properties();
        properties.put("1", "2");
        properties.put("2", "3");
        PropertyPlaceholderHelper helper = new PropertyPlaceholderHelper("{", "}");
        String result = helper.replacePlaceholders(str, properties);

        System.out.println(result);
    }

    public static List<String> extractPlaceholderValues(String input) {
        List<String> placeholderValues = new ArrayList<>();
        Pattern pattern = Pattern.compile("\\{(.*?)\\}");
        Matcher matcher = pattern.matcher(input);
        while (matcher.find()) {
            String placeholder = matcher.group(1);
            placeholderValues.add(placeholder);
        }
        return placeholderValues;
    }

}
