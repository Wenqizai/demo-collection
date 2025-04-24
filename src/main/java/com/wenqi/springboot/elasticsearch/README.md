# ElasticSearch模块

## 1. 项目概述

这是基于Spring Boot和ElasticSearch 7.2的集成示例项目，提供了完整的ElasticSearch操作API封装。

- **技术栈**：JDK8, ElasticSearch 7.2, Spring Boot 2.5.3, Elasticsearch-rest-high-level-client
- **项目目标**：演示ElasticSearch的基本操作，包括索引和文档的CRUD，以及常见的搜索功能实现

## 2. 模块结构

```
elasticsearch/
├── README.md                        # 模块说明文档
├── config/                          # 配置相关
│   └── ElasticsearchConfig.java     # ES客户端配置类
├── controller/                      # 控制器层
│   ├── BulkOperationController.java # 批量操作控制器
│   ├── ElasticsearchController.java # 基础ES操作控制器
│   └── WebsiteController.java       # 网站数据操作控制器
├── exception/                       # 异常处理
│   └── BusinessException.java       # 业务异常类
├── model/                           # 数据模型
│   ├── Blog.java                    # 博客实体
│   ├── BulkOperation.java           # 批量操作实体
│   ├── DocsRequest.java             # 文档请求实体  
│   ├── MgetDocsRequest.java         # 批量获取文档请求实体
│   ├── ResponseResult.java          # 通用响应结果
│   └── ScriptUpdateRequest.java     # 脚本更新请求实体
└── service/                         # 服务层
    ├── IElasticsearchService.java   # ES操作服务接口
    ├── IWebsiteService.java         # 网站数据服务接口
    └── impl/                        # 服务实现
        ├── ElasticsearchServiceImpl.java # ES服务实现类
        └── WebsiteServiceImpl.java       # 网站服务实现类
```

## 3. 核心功能说明

### 3.1 索引操作

- 创建索引：支持自定义mapping
- 检查索引是否存在
- 删除索引

### 3.2 文档操作

- 单文档添加/更新
- 批量文档添加
- 获取文档
- 更新文档
- 删除文档
- **指定路由添加/获取/更新/删除文档**：通过可选的routing参数指定路由值，当routing参数为空时，会退化为不使用路由的操作

### 3.3 搜索操作

- 基础搜索（Match查询）
- 短语搜索（MatchPhrase查询）
- 自定义查询构建器（QueryBuilder）
- **指定路由搜索**：通过可选的routing参数限定搜索范围到特定分片，当routing参数为空时，会在所有分片中搜索

### 3.4 批量操作

- 批量添加
- 批量更新
- 批量删除
- **批量获取支持路由**：支持针对每个文档指定不同的路由值

## 4. API接口列表

### 4.1 基础ElasticSearch接口

| 方法 | 路径 | 描述 |
| ---- | ---- | ---- |
| POST | /es/index/{index} | 创建索引 |
| DELETE | /es/index/{index} | 删除索引 |
| GET | /es/index/{index} | 判断索引是否存在 |
| POST | /es/{index}/document | 添加文档 |
| GET | /es/{index}/document/{id} | 获取文档 |
| PUT | /es/{index}/document/{id} | 更新文档 |
| DELETE | /es/{index}/document/{id} | 删除文档 |
| GET | /es/{index}/search | 搜索文档 |
| GET | /es/{index}/phrase-search | 短语搜索 |

### 4.1.1 路由功能接口

| 方法 | 路径 | 描述 |
| ---- | ---- | ---- |
| PUT | /es/{index}/{type}/{id}?routing={routing} | 指定路由添加/更新文档 |
| GET | /es/{index}/{type}/{id}?routing={routing} | 指定路由获取文档 |
| GET | /es/{index}/_mget | 批量获取文档(支持路由) |
| GET | /es/{index}/{type}/_search?routing={routing} | 指定路由搜索文档 |
| GET | /es/{index}/{type}/_search_all | 搜索全部文档 |

### 4.2 批量操作接口

| 方法 | 路径 | 描述 |
| ---- | ---- | ---- |
| POST | /es/bulk | 批量操作 |

### 4.3 网站数据接口

网站数据操作接口提供了更丰富的针对特定领域的搜索和管理功能，具体可参考`WebsiteController`。

## 5. 使用示例

### 5.1 创建索引

```java
// 创建索引请求
POST /es/index/blogs
{
  "mappings": {
    "properties": {
      "title": { "type": "text", "analyzer": "ik_max_word" },
      "content": { "type": "text", "analyzer": "ik_max_word" },
      "date": { "type": "date", "format": "yyyy-MM-dd HH:mm:ss||yyyy-MM-dd" }
    }
  }
}
```

### 5.2 添加文档

```java
// 添加文档请求
POST /es/blogs/document
{
  "title": "ElasticSearch入门指南",
  "text": "这是一篇关于ElasticSearch基础知识的博客...",
  "date": "2023-10-01"
}
```

### 5.3 搜索文档

```java
// 搜索文档请求
GET /es/blogs/search?keyword=ElasticSearch&field=title&clazz=com.wenqi.springboot.elasticsearch.model.Blog
```

### 5.4 使用路由添加文档

```java
// 使用路由添加文档
PUT /website/blog/6?routing=5
{
  "title": "My first blog entry 6",
  "text": "Just trying this out... 6",
  "date": "2014/01/01"
}
```

### 5.5 使用路由获取文档

```java
// 使用正确的路由获取文档
GET /website/blog/6?routing=5

// 使用错误路由或不使用路由可能找不到文档
GET /website/blog/6
GET /website/blog/6?routing=6
```

### 5.6 批量获取文档(支持路由)

```java
// 批量获取文档，支持为每个文档指定不同路由
GET /website/_mget
{
  "docs": [
    { "_id": 6, "routing": 5 }
  ]
}
```

### 5.7 使用路由搜索文档

```java
// 使用路由搜索文档
GET /website/blog/_search?routing=5
{
  "query": {
    "match": {
      "title": "6"
    }
  }
}
```

## 6. 注意事项

1. 确保ElasticSearch服务已启动且配置正确
2. 请在`application.properties`中配置以下属性：
   ```
   elasticsearch.host=localhost
   elasticsearch.port=9200
   elasticsearch.scheme=http
   ```
3. 对于大批量数据操作，请使用批量API以提高性能
4. 路由参数(routing)是可选的，当不提供或为空时，会使用不带路由的方式操作文档
5. 使用路由时，需要确保查询和添加使用相同的路由值，否则可能导致文档查询不到

## 7. 扩展与改进

1. 添加分页支持
2. 增强搜索功能，如聚合查询、高亮显示等
3. 添加索引别名支持
4. 实现索引模板功能
5. 添加集群健康检查
6. 增强路由功能，支持多路由值搜索