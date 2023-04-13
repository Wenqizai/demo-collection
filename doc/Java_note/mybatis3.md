# 概述

官方文档：https://mybatis.org/mybatis-3/zh/getting-started.html

MyBatis都是基于`SqlSessionFactory`实例为核心。`SqlSessionFactory`的实例可以通过 `SqlSessionFactoryBuilder` 获得。而 `SqlSessionFactoryBuilder` 则可以从 XML 配置文件或一个预先配置的`Configuration`实例来构建出`SqlSessionFactory`实例。

> 一些注意点

1. SqlSessionFactoryBuilder仅用来创建SqlSessionFactory，创建完毕可以销毁；
2. SqlSession线程不安全，注意共享session问题，最佳实践每个方法请求时开启一个SqlSession，方法结束就关闭；
3. 每个数据库对应一个 SqlSessionFactory 实例；