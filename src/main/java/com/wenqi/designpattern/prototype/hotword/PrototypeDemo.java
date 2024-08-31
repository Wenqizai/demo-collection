package com.wenqi.designpattern.prototype.hotword;

import com.google.common.collect.Lists;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 原型模式: 维护热词池
 *
 * 维护一个搜索热词表concurrentKeyWords, 没10分钟更新一遍热词
 * 要求: 1. 更新版本+1, 以最新的版本为准, 不同版本之间不能共存
 *
 *
 *
 * @author liangwenqi
 * @date 2023/3/7
 */
public class PrototypeDemo {
    /**
     * 热词表
     */
    private Map<String, SearchWord> concurrentKeyWords = new ConcurrentHashMap<>();
    private HashMap<String, SearchWord> concurrentKeyWords2 = new HashMap<>();
    private long lastUpdateTime = -1;

    /**
     * 对比refresh2, 采用深拷贝形式
     */
    public void refresh3() {
        // 深拷贝
        HashMap<String, SearchWord> newKeyWords = new HashMap<>(concurrentKeyWords.size());
        for (Map.Entry<String, SearchWord> entry : concurrentKeyWords.entrySet()) {
            SearchWord oldSearchWord = entry.getValue();
            SearchWord newSearchWord = new SearchWord(oldSearchWord.getKeyword(), oldSearchWord.getCount(), oldSearchWord.getLastUpdateTime());
            newKeyWords.put(entry.getKey(), newSearchWord);
        }

        // 获取数据
        List<SearchWord> needUpdatedSearchWords = getSearchWords(lastUpdateTime);
        long maxNewUpdatedTime = lastUpdateTime;
        for (SearchWord needUpdatedSearchWord : needUpdatedSearchWords) {
            long updateTime = needUpdatedSearchWord.getLastUpdateTime();
            if (updateTime > maxNewUpdatedTime) {
                maxNewUpdatedTime =  updateTime;
            }
            String keyword = needUpdatedSearchWord.getKeyword();
            if (newKeyWords.containsKey(keyword)) {
                // 复用旧对象
                SearchWord oldSearchWord = newKeyWords.get(keyword);
                oldSearchWord.setCount(needUpdatedSearchWord.getCount());
                oldSearchWord.setLastUpdateTime(needUpdatedSearchWord.getLastUpdateTime());
            } else {
                newKeyWords.put(keyword, needUpdatedSearchWord);
            }
        }
        lastUpdateTime = maxNewUpdatedTime;
        concurrentKeyWords = newKeyWords;
    }


    /**
     * 假设SearchWord构建花销很大
     *
     * 对比refresh1, 只在需要构造需要的对象, 复用旧对象, 花销小
     */
    public void refresh2() {
        // 浅拷贝
        HashMap<String, SearchWord> newKeyWords = (HashMap<String, SearchWord>) concurrentKeyWords2.clone();
        // 获取数据
        List<SearchWord> needUpdatedSearchWords = getSearchWords(lastUpdateTime);
        long maxNewUpdatedTime = lastUpdateTime;
        for (SearchWord needUpdatedSearchWord : needUpdatedSearchWords) {
            long updateTime = needUpdatedSearchWord.getLastUpdateTime();
            if (updateTime > maxNewUpdatedTime) {
                maxNewUpdatedTime =  updateTime;
            }
            String keyword = needUpdatedSearchWord.getKeyword();
            if (newKeyWords.containsKey(keyword)) {
                // 复用旧对象
                SearchWord oldSearchWord = newKeyWords.get(keyword);
                oldSearchWord.setCount(needUpdatedSearchWord.getCount());
                oldSearchWord.setLastUpdateTime(needUpdatedSearchWord.getLastUpdateTime());
            } else {
                newKeyWords.put(keyword, needUpdatedSearchWord);
            }
        }
        lastUpdateTime = maxNewUpdatedTime;
        concurrentKeyWords = newKeyWords;
    }


    /**
     * 假设SearchWord构建花销很大
     *
     * 每个对象构造一遍, 花销大
     */
    public void refresh1() {
        List<SearchWord> needUpdatedSearchWords = getSearchWords(lastUpdateTime);
        long maxNewUpdatedTime = lastUpdateTime;
        for (SearchWord needUpdatedSearchWord : needUpdatedSearchWords) {
            long updateTime = needUpdatedSearchWord.getLastUpdateTime();
            if (updateTime > maxNewUpdatedTime) {
                maxNewUpdatedTime =  updateTime;
            }
            String keyword = needUpdatedSearchWord.getKeyword();
            // replace和put的区分, replace当key不存在时, will do nothing
            if (concurrentKeyWords.containsKey(keyword)) {
                concurrentKeyWords.replace(keyword, needUpdatedSearchWord);
            } else {
                concurrentKeyWords.put(keyword, needUpdatedSearchWord);
            }
        }
        lastUpdateTime = maxNewUpdatedTime;
    }

    private List<SearchWord> getSearchWords(long lastUpdateTime) {
        // todo 从数据库中拉取更新时间 > lastUpdateTime的数据
        return Lists.newArrayList();
    }
}
