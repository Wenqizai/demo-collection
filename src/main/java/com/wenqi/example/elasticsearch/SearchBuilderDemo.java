package com.wenqi.example.elasticsearch;

import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.query.TermsQueryBuilder;
import org.elasticsearch.search.builder.SearchSourceBuilder;

/**
 * @author liangwenqi
 * @date 2025/6/27
 */
public class SearchBuilderDemo {
    public static void main(String[] args) {
        // 创建布尔查询构建器
        BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();

        // 添加多个查询条件
        TermsQueryBuilder termsQueryBuilder = QueryBuilders.termsQuery("name", "wenqi", "qi");
        boolQueryBuilder.filter(termsQueryBuilder);
        boolQueryBuilder.filter(QueryBuilders.matchQuery("type", "qi"));
        boolQueryBuilder.filter(QueryBuilders.termQuery("type", "1"));

        // 也可以添加should条件（或关系）
        // boolQueryBuilder.should(QueryBuilders.termQuery("status", "active"));

        // 或者添加must_not条件（非关系）
        // boolQueryBuilder.mustNot(QueryBuilders.termQuery("deleted", true));

        // 将布尔查询添加到SearchSourceBuilder
        SearchSourceBuilder searchSourceBuilder = SearchSourceBuilder.searchSource()
                .query(boolQueryBuilder);

        System.out.println(searchSourceBuilder);
    }
}
