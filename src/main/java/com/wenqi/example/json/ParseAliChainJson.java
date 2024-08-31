package com.wenqi.example.json;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Objects;

/**
 * @author liangwenqi
 * @date 2023/6/2
 */
public class ParseAliChainJson {
    public static void main(String[] args) throws IOException {
        String json = new String(Files.readAllBytes(Paths.get("src\\main\\java\\com\\wenqi\\example\\json\\ParseAliChainJson.txt")), StandardCharsets.UTF_8);
        // 统计异步的时间的方式还是不正确
        countAllSpanTime(json, "async", "");
        countSpendTime(json, "async", "");
    }

    private static void countSpendTime(String json, String target, String path) throws JsonProcessingException {
        JsonNode targetNode = findTargetJsonNode(json, path);

        if (targetNode == null) {
            System.out.println("....");
            return;
        }

        JsonNode locationNode = targetNode.get(0);
        long firstLogTime = locationNode.get("logTime").asLong();
        long firstSpanTime = locationNode.get("span").asLong();
        long startTime = firstLogTime - firstSpanTime;

        long endTime = 0;
        JsonNode findNode = locationNode.get("children");
        for (int i = findNode.size() - 1; i >= 0; i--) {
            JsonNode aNode = findNode.get(i);
            String rpc = aNode.get("rpc").asText();
            if (Objects.equals(rpc, target)) {
                endTime = aNode.get("logTime").asLong();
                break;
            }
        }

        long time = endTime - startTime;

        System.out.println("countSpendTime => 总耗费时间: " + time + "ns");
        System.out.println("countSpendTime => 总耗费时间: " + ((double) time / 1000) + "ms");
    }

    private static void countAllSpanTime(String json, String target, String path) throws JsonProcessingException {
        JsonNode targetNode = findTargetJsonNode(json, path);

        if (targetNode == null) {
            System.out.println("....");
            return;
        }

        int totalSpan = 0;
        JsonNode findNode = targetNode.get(0).get("children");
        for (int i = 0; i < findNode.size(); i++) {
            JsonNode aNode = findNode.get(i);
            String rpc = aNode.get("rpc").asText();
            if (Objects.equals(rpc, target)) {
                int span = aNode.get("span").asInt();
                totalSpan += span;
            }
        }

        System.out.println("countAllSpanTime => 总耗费时间: " + totalSpan + "ns");
        System.out.println("countAllSpanTime => 总耗费时间: " + ((double) totalSpan / 1000) + "ms");
    }

    private static JsonNode findTargetJsonNode(String json, String path) throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode jsonNode = objectMapper.readTree(json);
        JsonNode node = jsonNode.get("data").get("callChainInfoList").get(0).get("children");

        JsonNode targetNode = null;
        for (int i = 0; i < node.size(); i++) {
            JsonNode childNode = node.get(i);
            String httpPath = childNode.get("tagMap").get("http.path").asText();
            if (Objects.equals(httpPath, path) && childNode.get("children").size() > 0) {
                targetNode = childNode.get("children");
                break;
            }
        }
        return targetNode;
    }
}
