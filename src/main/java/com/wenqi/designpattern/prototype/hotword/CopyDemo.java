package com.wenqi.designpattern.prototype.hotword;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.HashMap;
import java.util.Map;

/**
 * @author liangwenqi
 * @date 2023/3/7
 */
public class CopyDemo {

    public static void main(String[] args) throws IOException, ClassNotFoundException {
        CopyDemo copyDemo = new CopyDemo();
        // test deep copy
        System.out.println("############ deep copy");
        System.out.println(copyDemo.getKeyWords() == copyDemo.deepCopy2());
        SearchWord oldKeyWord = copyDemo.getKeyWords().get("key");
        SearchWord newKeyWord = copyDemo.deepCopy2().get("key");
        System.out.println(oldKeyWord == newKeyWord);

        // test shallow copy
        System.out.println("############ shallow copy");
        System.out.println(copyDemo.getKeyWords() == copyDemo.shallowCopy());
        SearchWord oldKeyWord2 = copyDemo.getKeyWords().get("key");
        SearchWord newKeyWord2 = copyDemo.shallowCopy().get("key");
        System.out.println(oldKeyWord2 == newKeyWord2);
    }

    private HashMap<String, SearchWord> keyWordMap = new HashMap<>();

    public CopyDemo() {
        keyWordMap.put("key", new SearchWord("keys", 10, 20L));
    }

    /**
     * 方式2: 序列化形式
     */
    public Map<String, SearchWord> deepCopy2() throws IOException, ClassNotFoundException {
        ByteArrayOutputStream bo = new ByteArrayOutputStream();
        ObjectOutputStream oo = new ObjectOutputStream(bo);
        oo.writeObject(keyWordMap);

        ByteArrayInputStream bi = new ByteArrayInputStream(bo.toByteArray());
        ObjectInputStream oi = new ObjectInputStream(bi);

        return (Map<String, SearchWord>) oi.readObject();
    }

    /**
     * 方式1: 遍历
     */
    public void deepCopy() {
        HashMap<String, SearchWord> newKeyWords = new HashMap<>(keyWordMap.size());
        for (Map.Entry<String, SearchWord> entry : keyWordMap.entrySet()) {
            SearchWord oldSearchWord = entry.getValue();
            SearchWord newSearchWord = new SearchWord(oldSearchWord.getKeyword(), oldSearchWord.getCount(), oldSearchWord.getLastUpdateTime());
            newKeyWords.put(entry.getKey(), newSearchWord);
        }
    }

    /**
     * 浅拷贝
     */
    public Map<String, SearchWord> shallowCopy() {
        return (Map<String, SearchWord>) keyWordMap.clone();
    }

    public Map<String, SearchWord> getKeyWords() {
        return keyWordMap;
    }
}
