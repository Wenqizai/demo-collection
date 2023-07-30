# 概述

官方文档：https://mybatis.org/mybatis-3/zh/getting-started.html

参考blog：https://learn.lianglianglee.com/%E4%B8%93%E6%A0%8F/%E6%B7%B1%E5%85%A5%E5%89%96%E6%9E%90%20MyBatis%20%E6%A0%B8%E5%BF%83%E5%8E%9F%E7%90%86-%E5%AE%8C/00%20%E5%BC%80%E7%AF%87%E8%AF%8D%20%20%E9%A2%86%E7%95%A5%20MyBatis%20%E8%AE%BE%E8%AE%A1%E6%80%9D%E7%BB%B4%EF%BC%8C%E7%AA%81%E7%A0%B4%E6%8C%81%E4%B9%85%E5%8C%96%E6%8A%80%E6%9C%AF%E7%93%B6%E9%A2%88.md

MyBatis都是基于`SqlSessionFactory`实例为核心。`SqlSessionFactory`的实例可以通过 `SqlSessionFactoryBuilder` 获得。而 `SqlSessionFactoryBuilder` 则可以从 XML 配置文件或一个预先配置的`Configuration`实例来构建出`SqlSessionFactory`实例。

> 一些注意点

1. SqlSessionFactoryBuilder仅用来创建SqlSessionFactory，创建完毕可以销毁；
2. SqlSession线程不安全，注意共享session问题，最佳实践每个方法请求时开启一个SqlSession，方法结束就关闭；
3. 每个数据库对应一个 SqlSessionFactory 实例；

## 架构

- 架构图

![image-20230424170123990](material/MyBatis/Mybatis架构图.png)

### 基础支撑层

> 类型转换模块

Mybatis是通过操作JDBC来操作数据库，意味着从应用层 -> Mybatis -> JDBC直接的映射关系需要套用一层转换。转换场景主要是：

- SQL绑定传入参数：由Java类型数据转换成JDBC类型数据；
- 执行结果返回ResultSet，需要将JDBC类型数据转换层Java类型数据。

![image-20230424171158321](material/MyBatis/类型转换模块.png)

> **日志模块**

该模块目前可以集成 Log4j、Log4j2、slf4j 等优秀的日志框架。

> **反射工具模块**

MyBatis 的反射工具箱是在 Java 反射的基础之上进行的一层封装，为上层使用方提供更加灵活、方便的 API 接口，同时缓存 Java 的原生反射相关的元数据，提升了反射代码执行的效率，优化了反射操作的性能。

反射工具类包：`org.apache.ibatis.reflection`

> **Binding模块**

建立Mapper的绑定映射关系

> **数据源模块**

待补充。。。


> **缓存模块**

Mybatis 一、二级缓存

> **解析器模块**

mybatis-config.xml、Mapper.xml

> **事务管理模块**

待补充。。。

### 核心处理层

> **配置解析**

Configuration

> **SQL解析与scripting模块**

主要是sql标签的解析，如\<where>, \<if>, \<foreach>, \<set> 等

> **SQL执行、结果集映射模块**

Executor、StatementHandler、ParameterHandler 和 ResultSetHandler。

![image-20230424175717270](material/MyBatis/SQL语句执行流程.png)

> **插件**

提供扩展接口

### 接口层

**接口层是 MyBatis 暴露给调用的接口集合**，这些接口都是使用 MyBatis 时最常用的一些接口，例如，SqlSession 接口、SqlSessionFactory 接口等。其中，最核心的是 SqlSession 接口，你可以通过它实现很多功能，例如，获取 Mapper 代理、执行 SQL 语句、控制事务开关等。

## 目录结构

- annotations : 注解，例如`org.apache.ibatis.annotations.Mapper`
- binding : 数据绑定
- builder : 构建类
- cache : 缓存
- cursor : 游标
- datasource : 数据源设置
- exceptions : 异常
- executor : 执行器
- io : IO
- jdbc : JDBC相关
- lang : JDKversion标记
- logging : 日志
- mapping : 映射关系
- parsing : 解析，xml解析
- plugin : 插件
- reflection : 反射工具类
- scripting : 脚本(sql)组装工具
- session : 会话
- transaction : 事务相关内容
- type : 类型，存放了数据库和JAVA类型之间的关系如`org.apache.ibatis.type.JdbcType`


# 初始化

Mybatis的初始化与Spring的IOC容器初始化类似，通过定位、加载、解析相应的配置文件来完成初始化。配置文件包括：**mybatis-config.xml**（核心）、**映射配置文件**、以及相应的**注解**。

==注：Spring Boot与Mybatis的融合是通过封装`mybatis-spring-boot-starter`的形式，自动完成Mybatis的初始化==

> 入口

Mybatis初始化的入口方法：`org.apache.ibatis.session.SqlSessionFactoryBuilder#build`

build()方法里面，通过委托XMLConfigBuilder（`extends BaseBuilder`）来解析配置文件，并最终构造SqlSessionFactory返回。

```java
public SqlSessionFactory build(InputStream inputStream, String environment, Properties properties) {
    try {
        // 建造者设计模式，解析配置文件
        XMLConfigBuilder parser = new XMLConfigBuilder(inputStream, environment, properties);
        // 解析配置文件的内容构造到Configuration对象，再由Configuration创建DefaultSqlSessionFactory返回
        return build(parser.parse());
    } catch (Exception e) {
        throw ExceptionFactory.wrapException("Error building SqlSession.", e);
    } finally {
        ErrorContext.instance().reset();
        try {
            inputStream.close();
        } catch (IOException e) {
            // Intentionally ignore. Prefer previous error.
        }
    }
}

public SqlSessionFactory build(Configuration config) {
    return new DefaultSqlSessionFactory(config);
}
```

> 初始化流程图

![Mybatis初始化](material/MyBatis/Mybatis初始化.png)

## BaseBuilder

抽象类BaseBuilder定义解析配置文件的方法，并构造配置对象Configuration。BaseBuilder的实现类有：XMLMapperBuilder、XMLConfigBuilder、XMLStatementBuilder等，由他们负责配置文件解析。

```java
public abstract class BaseBuilder {
    // 保存了 Mybatis 的几乎所以核心配置信息，全局唯一
    protected final Configuration configuration;
    // 在 mybatis-config.xml 中可以通过 <typeAliases> 标签定义别名
    protected final TypeAliasRegistry typeAliasRegistry;
    // 在 mybatis-config.xml 中可以通过 <typeHandlers> 标签添加自定义TypeHandler
    // TypeHandler 用于完成JDBC数据类型与Java类型的相互转换，所有的 TypeHandler 都保存在 typeHandlerRegistry 中
    protected final TypeHandlerRegistry typeHandlerRegistry;

    public BaseBuilder(Configuration configuration) {
        this.configuration = configuration;
        this.typeAliasRegistry = this.configuration.getTypeAliasRegistry();
        this.typeHandlerRegistry = this.configuration.getTypeHandlerRegistry();
    }
}
```

我们可以看到`typeAliasRegistry`和`typeHandlerRegistry`都是由configuration get出来的，这意味着configuration在构造构成中会构建这两个对象。

## XMLConfigBuilder

XMLConfigBuilder主要负责解析mybatis-config.xml配置文件，负责解析xml的方法parseConfiguration。

```java
public class XMLConfigBuilder extends BaseBuilder {
    // 标记是否解析过mybatis-config.xml文件
    private boolean parsed;
    // 用于解析 mybatis-config.xml 的解析器
    private final XPathParser parser;
    // 标识 <environment> 配置名称，默认读取<environment>标签的default属性
    private String environment;
    // 创建并缓存 Reflector对象
    private final ReflectorFactory localReflectorFactory = new DefaultReflectorFactory();

    /**
     * 解析的入口，调用了 parseConfiguration() 进行后续的解析
     */
    public Configuration parse() {
        // parsed标志位 的处理
        if (parsed) {
            throw new BuilderException("Each XMLConfigBuilder can only be used once.");
        }
        parsed = true;
        // 在 mybatis-config.xml配置文件 中查找 <configuration> 节点，并开始解析
        parseConfiguration(parser.evalNode("/configuration"));
        return configuration;
    }

    private void parseConfiguration(XNode root) {
        try {
            // 根据 root.evalNode("properties") 中的值就可以知道具体是解析哪个标签的方法咯
            propertiesElement(root.evalNode("properties"));
            Properties settings = settingsAsProperties(root.evalNode("settings"));
            loadCustomVfs(settings);
            typeAliasesElement(root.evalNode("typeAliases"));
            pluginElement(root.evalNode("plugins"));
            objectFactoryElement(root.evalNode("objectFactory"));
            objectWrapperFactoryElement(root.evalNode("objectWrapperFactory"));
            reflectorFactoryElement(root.evalNode("reflectorFactory"));
            settingsElement(settings);
            // read it after objectFactory and objectWrapperFactory issue #631
            environmentsElement(root.evalNode("environments"));
            databaseIdProviderElement(root.evalNode("databaseIdProvider"));
            typeHandlerElement(root.evalNode("typeHandlers"));
            mapperElement(root.evalNode("mappers"));
        } catch (Exception e) {
            throw new BuilderException("Error parsing SQL Mapper Configuration. Cause: " + e, e);
        }
    }
}
```

### \<typeHandlers\>标签

通过解析标签`<typeHandlers>`，注册相应的TypeHander来构建typeHandlerRegistry。解析方法：`org.apache.ibatis.builder.xml.XMLConfigBuilder#typeHandlerElement`。

主要作用：注册自定义TypeHandler，并建立javaType与TypeHandler的映射关系，注册的主要途径：

- `<package>`：扫描package下的TypeHandler (`implements TypeHandler`)，并解析注解标注（`@MappedTypes(Long.class)`）的javaType。
- `<typeHandler>`：根据标签指定的Class信息和javaType类型来构建TypeHandler 。

==注：TypeHandler的映射关系保存在`Map<Type, Map<JdbcType, TypeHandler<?>>> typeHandlerMap = new ConcurrentHashMap<>();`中，如果package扫描的TypeHandler而没有指定注解`@MappedJdbcTypes(value={JdbcType.BIGINT, includeNullJdbcType=true)`，typeHandlerMap中的JdbcType key会存入null。==

```java
private void typeHandlerElement(XNode parent) throws Exception {
    if (parent != null) {
        // 处理 <typeHandlers> 下的所有子标签
        for (XNode child : parent.getChildren()) {
            // 处理 <package> 标签
            if ("package".equals(child.getName())) {
                // 获取指定的包名
                String typeHandlerPackage = child.getStringAttribute("name");
                // 通过 typeHandlerRegistry 的register(packageName)方法
                // 扫描指定包中的所有 TypeHandler类，并进行注册
                typeHandlerRegistry.register(typeHandlerPackage);
            } else {
                // Java数据类型
                String javaTypeName = child.getStringAttribute("javaType");
                // JDBC数据类型
                String jdbcTypeName = child.getStringAttribute("jdbcType");
                String handlerTypeName = child.getStringAttribute("handler");
                Class<?> javaTypeClass = resolveClass(javaTypeName);
                JdbcType jdbcType = resolveJdbcType(jdbcTypeName);
                Class<?> typeHandlerClass = resolveClass(handlerTypeName);
                // 注册
                if (javaTypeClass != null) {
                    if (jdbcType == null) {
                        typeHandlerRegistry.register(javaTypeClass, typeHandlerClass);
                    } else {
                        typeHandlerRegistry.register(javaTypeClass, jdbcType, typeHandlerClass);
                    }
                } else {
                    typeHandlerRegistry.register(typeHandlerClass);
                }
            }
        }
    }
}
```

### \<environments\>标签

通过配置\<environments\>标签，可以让Mybatis连接上多个数据源。但需要注意的是每个SqlSessionFactory实例只能构建其中一个environment。

\<environments\>标签解析方法：`org.apache.ibatis.builder.xml.XMLConfigBuilder#environmentsElement`

```java
private void environmentsElement(XNode context) throws Exception {
    if (context != null) {
        // 如果未指定XMLConfigBuilder的environment字段，则使用<environments>标志指定的default属性
        if (environment == null) {
            environment = context.getStringAttribute("default");
        }
        for (XNode child : context.getChildren()) {
            // 获取指定environment的标签，通过id比较
            String id = child.getStringAttribute("id");
            if (isSpecifiedEnvironment(id)) {
                // 实例化 TransactionFactory
                TransactionFactory txFactory = transactionManagerElement(child.evalNode("transactionManager"));
                // 创建 DataSourceFactory 和 DataSource
                DataSourceFactory dsFactory = dataSourceElement(child.evalNode("dataSource"));
                DataSource dataSource = dsFactory.getDataSource();
                // 创建的Environment对象中封装了上面的TransactionFactory对象和DataSource对象
                Environment.Builder environmentBuilder = new Environment.Builder(id)
                    .transactionFactory(txFactory)
                    .dataSource(dataSource);
                // 为configuration注入environment属性值
                configuration.setEnvironment(environmentBuilder.build());
            }
        }
    }
}
```

### \<databaseIdProvider>标签

不同的数据库厂商在SQL语法上有些差异，但对于业务开发者来说这应该是不关心的，所以ORM框架需要做到屏蔽这种差异。

对于Hibernate，其通过HQL的方式实现。对于Mybatis来说，其通过在mybatis-config.xml配置\<databaseIdProvider>标签来实现。

Mybatis 初始化时，会根据前面解析到的 DataSource 来确认当前使用的数据库产品，然后在解析映射文件时，<u>加载不带 databaseId 属性的sql语句及带有 databaseId 属性的sql语句，其中，带有 databaseId 属性的sql语句优先级更高，会被优先选中。</u>

- 配置

```xml
<!-- 未指定属性时， databaseId通过方法DatabaseMetaData#getDatabaseProductName()获取 -->
<!-- 主要是通过Connection的metadata中获取 -->
<databaseIdProvider type="DB_VENDOR" />

<!-- 指定属性时， databaseId上述方法获取到，通常情况下这些字符串都非常长，而且相同产品的不同版本会返回不同的值 -->
<!-- 这时，我们可以通过设置属性别名来使其变短 -->
<!-- 如果属性name不匹配时，databaseId返回null -->
<databaseIdProvider type="DB_VENDOR">
  <property name="SQL Server" value="sqlserver"/>
  <property name="DB2" value="db2"/>
  <property name="Oracle" value="oracle" />
</databaseIdProvider>
```

- 解析方法

```JAVA
private void databaseIdProviderElement(XNode context) throws Exception {
    DatabaseIdProvider databaseIdProvider = null;
    if (context != null) {
        String type = context.getStringAttribute("type");
        // awful patch to keep backward compatibility
        if ("VENDOR".equals(type)) {
            type = "DB_VENDOR";
        }
        Properties properties = context.getChildrenAsProperties();
        databaseIdProvider = (DatabaseIdProvider) resolveClass(type).getDeclaredConstructor().newInstance();
        databaseIdProvider.setProperties(properties);
    }
    Environment environment = configuration.getEnvironment();
    if (environment != null && databaseIdProvider != null) {
        String databaseId = databaseIdProvider.getDatabaseId(environment.getDataSource());
        configuration.setDatabaseId(databaseId);
    }
}
```

- 查找databaseId

```java
public class VendorDatabaseIdProvider implements DatabaseIdProvider {
    // 1. getDatabaseProductName 获取productName（通常比较长，不同版本有差别）
    private String getDatabaseName(DataSource dataSource) throws SQLException {
        String productName = getDatabaseProductName(dataSource);
        // 查找配置的property属性，如果找到则返回别名value，否返回null
        if (this.properties != null) {
            for (Map.Entry<Object, Object> property : properties.entrySet()) {
                if (productName.contains((String) property.getKey())) {
                    return (String) property.getValue();
                }
            }
            // no match, return null
            return null;
        }
        return productName;
    }

    // 通过Connection的metadata中获取productName
    private String getDatabaseProductName(DataSource dataSource) throws SQLException {
        try (Connection con = dataSource.getConnection()) {
            DatabaseMetaData metaData = con.getMetaData();
            return metaData.getDatabaseProductName();
        }

    }

}
```

### \<mappers>标签

\<mappers>标签去哪里查找映射配置文件，及使用了配置注解标识的接口。其中共有4种定义的形式：相对于类路径的资源引用、或完全限定资源定位符（包括 `file:///` 形式的 URL）、或类名、或包名等，4者互斥有着存在一种。

- xml配置文件

```xml
<!-- 4个互斥的，贪方便的写法 -->
<mappers>
    <!-- 针对xml使用 -->
    <!-- 使用相对于类路径的资源引用 -->
    <mapper resource="org/mybatis/builder/AuthorMapper.xml"/>
    <!-- 使用完全限定资源定位符（URL） -->
    <mapper url="file:///var/mappers/AuthorMapper.xml"/>
    
    <!-- 针对mapper使用注解使用 -->
    <!-- 使用映射器接口实现类的完全限定类名 -->
    <mapper class="org.mybatis.builder.AuthorMapper"/>
    <!-- 将包内的映射器接口全部注册为映射器 -->
    <package name="org.mybatis.builder"/>
</mappers>
```

- 解析方法

解析\<mappers>节点，本方法会创建 XMLMapperBuilder对象加载映射文件，如果映射配置文件存在相应的Mapper接口，也会加载相应的Mapper接口，解析其中的注解并完成向 MapperRegistry 的注册。

```java
private void mapperElement(XNode parent) throws Exception {
    if (parent != null) {
        // 遍历子节点<mapper>标签
        for (XNode child : parent.getChildren()) {
            if ("package".equals(child.getName())) {
                // 获取 <package> 子节点 中的包名
                String mapperPackage = child.getStringAttribute("name");
                // 扫描指定的包目录，然后向 MapperRegistry 注册 Mapper接口
                configuration.addMappers(mapperPackage);
            } else {
                // 映射器由一个接口和一个XML配置文件组成，XML文件中定义了一个命名空间namespace
                // 命名空间的值是接口对应的全路径
                String resource = child.getStringAttribute("resource");
                String url = child.getStringAttribute("url");
                String mapperClass = child.getStringAttribute("class");
                // 如果<mapper>节点指定了resource或是url属性，则创建XMLMapperBuilder对象
                // 解析resource或url属性 指定的Mapper配置文件
                if (resource != null && url == null && mapperClass == null) {
                    ErrorContext.instance().resource(resource);
                    InputStream inputStream = Resources.getResourceAsStream(resource);
                    XMLMapperBuilder mapperParser = new XMLMapperBuilder(inputStream, configuration, resource, configuration.getSqlFragments());
                    mapperParser.parse();
                } else if (resource == null && url != null && mapperClass == null) {
                    ErrorContext.instance().resource(url);
                    InputStream inputStream = Resources.getUrlAsStream(url);
                    XMLMapperBuilder mapperParser = new XMLMapperBuilder(inputStream, configuration, url, configuration.getSqlFragments());
                    mapperParser.parse();
                // 如果<mapper>节点指定了class属性，则向 MapperRegistry 注册该Mapper接口
                } else if (resource == null && url == null && mapperClass != null) {
                    Class<?> mapperInterface = Resources.classForName(mapperClass);
                    configuration.addMapper(mapperInterface);
                } else {
                    throw new BuilderException("A mapper element may only specify a url, resource or class, but not more than one.");
                }
            }
        }
    }
}
```

## XMLMapperBuilder

XMLMapperBuilder：主要负责解析mybatis-config.xml配置文件中配置的\<mapper>标签定义，负责找到对应的Mapper和Mapper.xml并加载解析。核心方法：`org.apache.ibatis.builder.xml.XMLMapperBuilder#parse`

```java
public class XMLMapperBuilder extends BaseBuilder {
    public void parse() {
        // 是否已经加载过该配置文件
        if (!configuration.isResourceLoaded(resource)) {
            // 解析 <mapper>节点
            configurationElement(parser.evalNode("/mapper"));
            // 将 resource 添加到 configuration 的 loadedResources属性中，
            // 该属性是一个 HashSet<String>类型的集合，其中记录了已经加载过的映射文件
            configuration.addLoadedResource(resource);
            // 注册 Mapper接口
            bindMapperForNamespace();
        }
        // 处理 configurationElement()方法 中解析失败的 <resultMap>节点
        parsePendingResultMaps();
        // 处理 configurationElement()方法 中解析失败的 <cacheRef>节点
        parsePendingCacheRefs();
        // 处理 configurationElement()方法 中解析失败的 <statement>节点
        parsePendingStatements();
    }

    // 解析xml sql映射放到configuration
    private void configurationElement(XNode context) {
        try {
            // 获取 <mapper>节点 的 namespace属性
            String namespace = context.getStringAttribute("namespace");
            if (namespace == null || namespace.equals("")) {
                throw new BuilderException("Mapper's namespace cannot be empty");
            }
            // 使用 MapperBuilderAssistant对象 的 currentNamespace属性 记录 namespace命名空间
            builderAssistant.setCurrentNamespace(namespace);
            // 解析 <cache-ref>节点，后面的解析方法 也都见名知意
            cacheRefElement(context.evalNode("cache-ref"));
            cacheElement(context.evalNode("cache"));
            parameterMapElement(context.evalNodes("/mapper/parameterMap"));
            // *解析resultMap标签
            resultMapElements(context.evalNodes("/mapper/resultMap"));
            // *解析sql id标签
            sqlElement(context.evalNodes("/mapper/sql"));
            // *解析sql statement标签
            buildStatementFromContext(context.evalNodes("select|insert|update|delete"));
        } catch (Exception e) {
            throw new BuilderException("Error parsing Mapper XML. The XML location is '" + resource + "'. Cause: " + e, e);
        }
    }
}
```

### \<resultMap>标签

Mybatis 通过\<resultMap>节点定义了ORM 规则，建立了业务成DTO与数据库column的映射关系，减少重复代码，提高开发效率。通过解析\<resultMap>标签，将解析结果存入`org.apache.ibatis.mapping.ResultMap`中，其中保存映射关系的类是`org.apache.ibatis.mapping.ResultMapping`。

解析方法：`org.apache.ibatis.builder.xml.XMLMapperBuilder#resultMapElement(org.apache.ibatis.parsing.XNode, java.util.List<org.apache.ibatis.mapping.ResultMapping>, java.lang.Class<?>)`

> ResultMap

```java
public class ResultMap {
    private Configuration configuration;
    // <resultMap> 中的id属性, 唯一
    private String id;
    private Class<?> type;
    // 记录了除 <discriminator>节点 之外的其它映射关系(即，ResultMapping对象集合)
    private List<ResultMapping> resultMappings;
    // 记录了映射关系中带有 ID标志 的映射关系，如：<id>节点 和 <constructor>节点 的 <idArg>子节点
    private List<ResultMapping> idResultMappings;
    // 记录了映射关系中带有 Constructor标志 的映射关系，如：<constructor>所有子元素
    private List<ResultMapping> constructorResultMappings;
    // 记录了映射关系中不带有 Constructor标志 的映射关系
    private List<ResultMapping> propertyResultMappings;
    // 记录了所有映射关系中涉及的 column属性 的集合
    private Set<String> mappedColumns;
    // 记录了所有映射关系中涉及的 property属性 的集合
    private Set<String> mappedProperties;
    // 鉴别器，对应 <discriminator>节点
    private Discriminator discriminator;
    // 是否含有嵌套的结果映射，如果某个映射关系中存在 resultMap属性，
    // 且不存在 resultSet属性，则为true
    private boolean hasNestedResultMaps;
    // 是否含有嵌套查询，如果某个属性映射存在 select属性，则为true
    private boolean hasNestedQueries;
    // 是否开启自动映射
    private Boolean autoMapping;
}
```

> ResultMapping

```java
public class ResultMapping {
    private Configuration configuration;
    // 对应节点的 property属性，表示该列进行映射的属性
    private String property;
    // 对应节点的 column属性，表示从数据库中得到的列名或列名的别名
    private String column;
    // 表示 一个 JavaBean 的完全限定名，或一个类型别名
    private Class<?> javaType;
    // 进行映射列的 JDBC类型
    private JdbcType jdbcType;
    // 类型处理器
    private TypeHandler<?> typeHandler;
    // 该属性通过 id 引用了另一个 <resultMap>节点，它负责将结果集中的一部分列映射成
    // 它所关联的结果对象。这样我们就可以通过 join方式 进行关联查询，然后直接映射成
    // 多个对象，并同时设置这些对象之间的组合关系(nested嵌套的)
    private String nestedResultMapId;
    // 该属性通过 id 引用了另一个 <select>节点，它会把指定的列值传入 select属性 指定的
    // select语句 中作为参数进行查询。使用该属性可能会导致 ORM 中的 N+1问题，请谨慎使用
    private String nestedQueryId;
    private Set<String> notNullColumns;
    private String columnPrefix;
    // 处理后的标志，共有两个：id 和 constructor
    private List<ResultFlag> flags;
    private List<ResultMapping> composites;
    private String resultSet;
    private String foreignColumn;
    // 是否延迟加载
    private boolean lazy;
}
```

> \<resultMap>标签解析方法resultMapElement

最后调用方法buildResultMappingFromContext：主要是解析\<resultMap>下的子标签，构建每一个ResultMapping返回。

当构建完 ResultMapping 对象集合之后，会调用 `resultMapResolver.resolve();`，该方法会调用 MapperBuilderAssistant 的 addResultMap()方法 创建 ResultMap 对象，并添加到 Configuration 的 resultMaps 集合中保存，最终完成\<resultMap>标签的解析构建过程。

```java
private ResultMap resultMapElement(XNode resultMapNode) {
    return resultMapElement(resultMapNode, Collections.emptyList(), null);
}

private ResultMap resultMapElement(XNode resultMapNode, List<ResultMapping> additionalResultMappings, Class<?> enclosingType) {
    ErrorContext.instance().activity("processing " + resultMapNode.getValueBasedIdentifier());
    // type属性，表示结果集将被映射成 type 指定类型的对象
    // type属性要么指定全类名，要么建立Alias
    String type = resultMapNode.getStringAttribute("type",
                                                   resultMapNode.getStringAttribute("ofType",
                                                                                    resultMapNode.getStringAttribute("resultType",
                                                                                                                     resultMapNode.getStringAttribute("javaType"))));
    // 解析type指定的Class类型
    Class<?> typeClass = resolveClass(type);
    if (typeClass == null) {
        // 处理子标签中<association>没有指定resultType或resultMap的对象映射
        typeClass = inheritEnclosingType(resultMapNode, enclosingType);
    }
    Discriminator discriminator = null;
    List<ResultMapping> resultMappings = new ArrayList<>(additionalResultMappings);
    // 获取并处理 <resultMap> 的子节点 (id*, result*, association*, collection*, discriminator)
    List<XNode> resultChildren = resultMapNode.getChildren();
    for (XNode resultChild : resultChildren) {
        // 处理 <constructor>节点
        if ("constructor".equals(resultChild.getName())) {
            processConstructorElement(resultChild, typeClass, resultMappings);
            // 处理 <discriminator>节点 根据结果值来决定使用哪个resultMap
        } else if ("discriminator".equals(resultChild.getName())) {
            discriminator = processDiscriminatorElement(resultChild, typeClass, resultMappings);
        } else {
            // 处理 <id>, <result>, <association>, <collection> 等节点
            List<ResultFlag> flags = new ArrayList<>();
            if ("id".equals(resultChild.getName())) {
                flags.add(ResultFlag.ID);
            }
            // 创建 ResultMapping对象，并添加到 resultMappings集合
            resultMappings.add(buildResultMappingFromContext(resultChild, typeClass, flags));
        }
    }
    String id = resultMapNode.getStringAttribute("id", resultMapNode.getValueBasedIdentifier());
    // 该属性指定了该 <resultMap>节点 的继承关系
    String extend = resultMapNode.getStringAttribute("extends");
    // 为 true 则启动自动映射功能，该功能会自动查找与列明相同的属性名，并调用 setter方法，
    // 为 false，则需要在 <resultMap>节点 内注明映射关系才会调用对应的 setter方法
    Boolean autoMapping = resultMapNode.getBooleanAttribute("autoMapping");
    ResultMapResolver resultMapResolver = new ResultMapResolver(builderAssistant, id, typeClass, extend, discriminator, resultMappings, autoMapping);
    try {
        return resultMapResolver.resolve();
    } catch (IncompleteElementException  e) {
        configuration.addIncompleteResultMap(resultMapResolver);
        throw e;
    }
}
```

### \<sql>标签

我们再Mapper.xml文件中可以定义sql标签，用于sql statement的拼接。解析sql标签简单来说将解析的sql按照key-value的形式放入` Map<String, XNode> sqlFragments;`Map中，其中key有两种形式：

- Mapper全限定类名.id
- id

value为整个sql标签。

具体put方法：`org.apache.ibatis.session.Configuration.StrictMap#put`

```java
private void sqlElement(List<XNode> list) throws Exception {
    if (configuration.getDatabaseId() != null) {
        sqlElement(list, configuration.getDatabaseId());
    }
    sqlElement(list, null);
}

private void sqlElement(List<XNode> list, String requiredDatabaseId) throws Exception {
    // 遍历 <sql>节点
    for (XNode context : list) {
        String databaseId = context.getStringAttribute("databaseId");
        String id = context.getStringAttribute("id");
        // 为 id 添加命名空间
        id = builderAssistant.applyCurrentNamespace(id, false);
        // 检测 <sql> 的 databaseId 与当前 Configuration 中记录的 databaseId 是否一致
        if (databaseIdMatchesCurrent(id, databaseId, requiredDatabaseId)) {
            // 记录到 sqlFragments(Map<String, XNode>) 中保存
            sqlFragments.put(id, context);
        }
    }
}
```

### Mapper绑定

每个映射配置文件 Mapper.xml 的命名空间可以绑定一个 Mapper 接口，并注册到 MapperRegistry 中。执行绑定的方法发生在 Mapper.xml 解析的过程中：`org.apache.ibatis.builder.xml.XMLMapperBuilder#parse`

```java
public void parse() {
    if (!configuration.isResourceLoaded(resource)) {
        configurationElement(parser.evalNode("/mapper"));
        configuration.addLoadedResource(resource);
        bindMapperForNamespace();
    }
}
```

其中由 XMLMapperBuilder 的 bindMapperForNamespace()方法中，完成了映射配置文件与对应 Mapper 接口的绑定。

```java
// 绑定命名空间
private void bindMapperForNamespace() {
    // 获取当前映射配置文件的命名空间（<Mapper>是逐一解析的），前面解析Mapper.xml标签时保存的
    String namespace = builderAssistant.getCurrentNamespace();
    if (namespace != null) {
        Class<?> boundType = null;
        try {
            // 创建命名空间对应的class
            boundType = Resources.classForName(namespace);
        } catch (ClassNotFoundException e) {
            //ignore, bound type is not required
        }
        if (boundType != null) {
            // mapper还没注册
            if (!configuration.hasMapper(boundType)) {
                // Spring may not know the real resource name so we set a flag
                // to prevent loading again this resource from the mapper interface
                // look at MapperAnnotationBuilder#loadXmlResource
                // 追加个 "namespace:" 的前缀，并添加到 Configuration 的 loadedResources 集合中
                // 这里相当于加一个标志位，防止在addMapper里面再加载一次xml资源
                configuration.addLoadedResource("namespace:" + namespace);
                // 添加到 Configuration 的 mapperRegistry 集合中，另外往这个方法栈的更深处看会发现
                // 其创建了 MapperAnnotationBuilder 对象，并调用了该对象的 parse()方法 解析 Mapper 接口
                // 这里还用调用 MapperAnnotationBuilder#parse 的原因是兼容xml和注解同时有配置的情况
                configuration.addMapper(boundType);
            }
        }
    }
}

public class MapperRegistry {
    public <T> void addMapper(Class<T> type) {
        if (type.isInterface()) {
            if (hasMapper(type)) {
                throw new BindingException("Type " + type + " is already known to the MapperRegistry.");
            }
            boolean loadCompleted = false;
            try {
                knownMappers.put(type, new MapperProxyFactory<T>(type));
                // 解析 Mapper接口 type 中的信息
                MapperAnnotationBuilder parser = new MapperAnnotationBuilder(config, type);
                parser.parse();
                loadCompleted = true;
            } finally {
                if (!loadCompleted) {
                    knownMappers.remove(type);
                }
            }
        }
    }
}

public class MapperAnnotationBuilder {
    public void parse() {
        String resource = type.toString();
        // 是否已经加载过该接口
        if (!configuration.isResourceLoaded(resource)) {
            // 检查是否加载过该接口对应的映射文件，如果未加载，则创建 XMLMapperBuilder对象
            // 解析对应的映射文件，该过程就是前面介绍的映射配置文件解析过程
            loadXmlResource();
            configuration.addLoadedResource(resource);
            assistant.setCurrentNamespace(type.getName());
            // 解析 @CacheNamespace注解
            parseCache();
            // 解析 @CacheNamespaceRef注解
            parseCacheRef();
            // type接口 的所有方法
            Method[] methods = type.getMethods();
            for (Method method : methods) {
                try {
                    if (!method.isBridge()) {
                        // 解析 SelectKey、ResultMap 等注解，并创建 MappedStatement对象
                        parseStatement(method);
                    }
                } catch (IncompleteElementException e) {
                    // 如果解析过程出现 IncompleteElementException异常，可能是因为引用了
                    // 未解析的注解，这里将出现异常的方法记录下来，后面提供补偿机制，重新进行解析
                    configuration.addIncompleteMethod(new MethodResolver(this, method));
                }
            }
        }
        // 遍历 configuration 中的 incompleteMethods集合，集合中记录了未解析的方法
        // 重新调用这些方法进行解析
        parsePendingMethods();
    }
}
```

**问题：假设同时配置了注解和xml怎么办？两者会同时生效么？优先级是怎样的？**

1. 对，同时生效，但是同一个方法来说相同属性只会有一个生效（因为采用strictMap，相同key put时会报错！）

2. 对于xml配置来说，先解析xml，后面解析注解配置

   路径：解析Mapper ->  XMLMapperBuilder -> bindMapperForNamespace ->  MapperAnnotationBuilder 

3. 对于注解来说，先解析注解，再解析xml

   路径：mapperRegistry.addMappers -> MapperAnnotationBuilder  -> loadXmlResource

## XMLStatementBuilder

待补充。。。

## MapperAnnotationBuilder

待补充。。。

# 基础支撑层

## 反射

### Reflector

反射工具包：`org.apache.ibatis.reflection`

Reflector，mybatis反射模块的基础。通常需要反射操作一个类时，都会先把Class封装成一个Reflector对象，Reflector中缓存Class的元数据信息。通过Reflector可以更加便利地操作Class的属性和方法。

- 构造器

```java
public class Reflector {
    // Reflector缓存的Class元信息，在构造Reflector时完成该属性的填充

    // 传入的Class
    private final Class<?> type;
    // Class 可读、可写的属性
    private final String[] readablePropertyNames;
    private final String[] writablePropertyNames;
    // Class setter、getter方法
    private final Map<String, Invoker> setMethods = new HashMap<>();
    private final Map<String, Invoker> getMethods = new HashMap<>();
    // Class 对应的setter的参数类型、getter的return类型
    // key: 属性  value：参数类型/return类型
    private final Map<String, Class<?>> setTypes = new HashMap<>();
    private final Map<String, Class<?>> getTypes = new HashMap<>();
    // 默认的构造方法
    private Constructor<?> defaultConstructor;
    // 所有属性名称的集合，记录到这个集合中的属性名称都是大写的。
    private Map<String, String> caseInsensitivePropertyMap = new HashMap<>();

    // Reflector的构造方法，完成class的属性填充
    public Reflector(Class<?> clazz) {
        type = clazz;
        addDefaultConstructor(clazz);
        addGetMethods(clazz);
        addSetMethods(clazz);
        addFields(clazz);
        readablePropertyNames = getMethods.keySet().toArray(new String[0]);
        writablePropertyNames = setMethods.keySet().toArray(new String[0]);
        for (String propName : readablePropertyNames) {
            caseInsensitivePropertyMap.put(propName.toUpperCase(Locale.ENGLISH), propName);
        }
        for (String propName : writablePropertyNames) {
            caseInsensitivePropertyMap.put(propName.toUpperCase(Locale.ENGLISH), propName);
        }
    }
}
```

- addDefaultConstructor

获取所有的构造器，找到空参构造器作为默认构造器

```java
private void addDefaultConstructor(Class<?> clazz) {
    Constructor<?>[] constructors = clazz.getDeclaredConstructors();
    Arrays.stream(constructors).filter(constructor -> constructor.getParameterTypes().length == 0)
        .findAny().ifPresent(constructor -> this.defaultConstructor = constructor);
}
```

- addGetMethods

```java
private void addGetMethods(Class<?> clazz) {
    Map<String, List<Method>> conflictingGetters = new HashMap<>();
    Method[] methods = getClassMethods(clazz);
    // addMethodConflict: 找到所有的setter方法
    Arrays.stream(methods).filter(m -> m.getParameterTypes().length == 0 && PropertyNamer.isGetter(m.getName()))
        .forEach(m -> addMethodConflict(conflictingGetters, PropertyNamer.methodToProperty(m.getName()), m));
    resolveGetterConflicts(conflictingGetters);
}
```

### Invoker

Reflector在构造过程中，Class中所有属性的 getter/setter 方法都会被封装成 MethodInvoker 对象，没有 getter/setter 的字段也会生成对应的 Get/SetFieldInvoker 对象。

```java
public interface Invoker {
    // method执行invoke
    Object invoke(Object target, Object[] args) throws IllegalAccessException, InvocationTargetException;
    // 获取method的类型，setter返回param类型，getter返回return类型
    Class<?> getType();
}
```
- 类图

![image-20230425135816524](material/MyBatis/反射Invoker类图.png)

### ReflectorFactory

ReflectorFactory主要是用来创建Reflector对象，并提供缓存功能，DefaultReflectorFactory为默认实现。

由下面的缓存实现可知，缓存的没有提供清理功能，缓存的生命周期与DefaultReflectorFactory同步。

```java
public class DefaultReflectorFactory implements ReflectorFactory {
    private boolean classCacheEnabled = true;
    private final ConcurrentMap<Class<?>, Reflector> reflectorMap = new ConcurrentHashMap<>();

    public DefaultReflectorFactory() {
    }

    @Override
    public boolean isClassCacheEnabled() {
        return classCacheEnabled;
    }

    @Override
    public void setClassCacheEnabled(boolean classCacheEnabled) {
        this.classCacheEnabled = classCacheEnabled;
    }

    @Override
    public Reflector findForClass(Class<?> type) {
        if (classCacheEnabled) {
            // synchronized (type) removed see issue #461
            return reflectorMap.computeIfAbsent(type, Reflector::new);
        } else {
            return new Reflector(type);
        }
    }

}
```

### MetaClass

Class的元信息，底层依赖reflector。

### ObjectWrapper

ObjectWrapper 封装的则是对象元信息。在 ObjectWrapper 中抽象了一个对象的属性信息，并提供了查询对象属性信息的相关方法，以及更新属性值的相关方法。

> 属性相关工具

- PropertyTokenizer 工具类负责解析由“.”和“[]”构成的表达式。PropertyTokenizer 继承了 Iterator 接口，可以迭代处理嵌套多层表达式。
- PropertyCopier 是一个属性拷贝的工具类，提供了与 Spring 中 BeanUtils.copyProperties() 类似的功能，实现相同类型的两个对象之间的属性值拷贝，其核心方法是 copyBeanProperties() 方法。
- PropertyNamer 工具类提供的功能是转换方法名到属性名，以及检测一个方法名是否为 getter 或 setter 方法。

### ObjectFactory

如注释：MyBatis使用ObjectFactory来实例化指定的类。

```java
/**
 * MyBatis uses an ObjectFactory to create all needed new Objects.
 */
public interface ObjectFactory {
  default void setProperties(Properties properties) {}

  <T> T create(Class<T> type);

  <T> T create(Class<T> type, List<Class<?>> constructorArgTypes, List<Object> constructorArgs);

  <T> boolean isCollection(Class<T> type);
}
```

## 类型转换

Java类型与数据库字段类型对应映射：

枚举：`org.apache.ibatis.type.JdbcType`

|                       数据库类型                        |  Java类型  |
| :-----------------------------------------------------: | :--------: |
|                      varchar、char                      |   String   |
|                          blob                           |   byte[]   |
|                    Integer unsigned                     |    Long    |
| tinyint unsigned、smallint unsigned、mediumint unsigned |  Integer   |
|                           bit                           |  Boolean   |
|                     bigint unsigned                     | BigInteger |
|                          float                          |   float    |
|                         double                          |   double   |
|                         decimal                         | BigDecimal |

### TypeHandler

类型转换器TypeHandler: `JdbcType  互转  JavaType`

```java
public interface TypeHandler<T> {
    // 在通过PreparedStatement为SQL语句绑定参数时，会将传入的实参数据由JdbcType类型转换成Java类型
    void setParameter(PreparedStatement ps, int i, T parameter, JdbcType jdbcType) throws SQLException;
	// 从ResultSet中获取数据时会使用getResult()方法，其中会将读取到的数据由Java类型转换成JdbcType类型
    T getResult(ResultSet rs, String columnName) throws SQLException;

    T getResult(ResultSet rs, int columnIndex) throws SQLException;

    T getResult(CallableStatement cs, int columnIndex) throws SQLException;

}
```

- `TypeHandler.setParameter()`：完成JavaType到JdbcType的转换；
- `TypeHandler.getResult()`：完成JdbcType到JavaType的转换。

TypeHandler具体的转换逻辑由之类实现，其底层均是依赖JDBC的API。

注：==TypeHandler 主要用于单个参数的类型转换，如果要将多个列的值转换成一个 Java 对象，可以在映射文件中定义合适的映射规则 &lt;resultMap&gt; 完成映射。==

### TypeHandlerRegistry

我们已经知道TypeHandler的转换原理，类型转换逻辑由子类实现，这时延申TypeHandler的两个问题：

1. MyBatis如何管理众多TypeHandler的子类？
2. MyBatis如果找到合适的类型转换器，进行类型转换？

> TypeHandler注册

Mybatis在初始化过程中，会创建所有已知的TypeHandler（包括内置实现和自定义实现），注册到**TypeHandlerRegistry**。

1. TypeHandlerRegistry在构造过程中完成内置TypeHandler的注册：`org.apache.ibatis.type.TypeHandlerRegistry#TypeHandlerRegistry(org.apache.ibatis.session.Configuration)`，
2. TypeHandlerRegistry提供注册自定义TypeHandler的注册方法（在解析mybatis-config.xml定义的标签`<typeHandlers>`过程调用注册）：`org.apache.ibatis.type.TypeHandlerRegistry#register(java.lang.Class<?>)`

- TypeHandlerRegistry

```java
public final class TypeHandlerRegistry {
	// 该集合记录从 JdbcType 到 JavaType 需要使用TypeHandler对象的映射
    private final Map<JdbcType, TypeHandler<?>>  jdbcTypeHandlerMap = new EnumMap<>(JdbcType.class);
    // 该集合记录从 JavaType 到 JdbcType 需要使用TypeHandler对象的映射
    // 一对多的关系，如：Java中String 对应数据库 char、varchar、text 等多个类型
    private final Map<Type, Map<JdbcType, TypeHandler<?>>> typeHandlerMap = new ConcurrentHashMap<>();
    private final TypeHandler<Object> unknownTypeHandler;
    // 该集合记录了全部 TypeHandler 的class类型以及对应的 TypeHandler 实例对象。
    private final Map<Class<?>, TypeHandler<?>> allTypeHandlersMap = new HashMap<>();
    private static final Map<JdbcType, TypeHandler<?>> NULL_TYPE_HANDLER_MAP = Collections.emptyMap();
    private Class<? extends TypeHandler> defaultEnumTypeHandler = EnumTypeHandler.class;
}
```

- 注册方法register

```java
private void register(Type javaType, JdbcType jdbcType, TypeHandler<?> handler) {
    if (javaType != null) {
        Map<JdbcType, TypeHandler<?>> map = typeHandlerMap.get(javaType);
        if (map == null || map == NULL_TYPE_HANDLER_MAP) {
            map = new HashMap<>();
        }
        map.put(jdbcType, handler);
        typeHandlerMap.put(javaType, map);
    }
    allTypeHandlersMap.put(handler.getClass(), handler);
}
```

> TypeHandler获取

获取TypeHandler主要是通过重载方法：`getTypeHandler()`

```java
private <T> TypeHandler<T> getTypeHandler(Type type, JdbcType jdbcType) {
    if (ParamMap.class.equals(type)) {
        return null; // 过滤掉ParamMap类型
    }

    // 根据Java类型查找对应的TypeHandler集合
    // Java数据类型 与 JDBC数据类型 的关系往往是一对多，
    // 所以一般会先根据 Java数据类型 获取 Map<JdbcType, TypeHandler<?>>对象
    // 再根据 JDBC数据类型 获取对应的 TypeHandler对象
    Map<JdbcType, TypeHandler<?>> jdbcHandlerMap = getJdbcHandlerMap(type);
    TypeHandler<?> handler = null;
    if (jdbcHandlerMap != null) {
        // 根据JdbcType类型查找对应的TypeHandler实例
        handler = jdbcHandlerMap.get(jdbcType);
        if (handler == null) {
            // 没有对应的TypeHandler实例，则使用null对应的TypeHandler
            handler = jdbcHandlerMap.get(null);
        }

        if (handler == null) {
            // 如果jdbcHandlerMap只注册了一个TypeHandler，则使用此TypeHandler对象
            handler = pickSoleHandler(jdbcHandlerMap);
        }
    }
    return (TypeHandler<T>) handler;
}
```

## 日志

Mybatis可以接口主流的日志框架，主要采用适配器模式，将各个第三方日志框架接口转换为框架内部自定义的日志接口。MyBatis 自定义的 Log 接口位于 `org.apache.ibatis.logging` 包中，相关的适配器也位于该包中。

### Log实现

> LogFactory初始化Logger

Logger实际上是委托给日志框架的实现来打日志，如Slf4jImpl、Log4jImpl、Jdk14LoggingImpl等。（适配器模式)

```java
public final class LogFactory {
    private static Constructor<? extends Log> logConstructor;

    static {
        // static方法的所有的日志实现都调用一遍tryImplementation, 但是只会实现一个
        tryImplementation(LogFactory::useSlf4jLogging);
        tryImplementation(LogFactory::useCommonsLogging);
        tryImplementation(LogFactory::useLog4J2Logging);
        tryImplementation(LogFactory::useLog4JLogging);
        tryImplementation(LogFactory::useJdkLogging);
        tryImplementation(LogFactory::useNoLogging);
    }
    
    public static synchronized void useSlf4jLogging() {
        setImplementation(org.apache.ibatis.logging.slf4j.Slf4jImpl.class);
    }

    private static void tryImplementation(Runnable runnable) {
        // 只会初始化第一个logConstructor
        if (logConstructor == null) {
            try {
                runnable.run();
            } catch (Throwable t) {
                // ignore
            }
        }
    }

    private static void setImplementation(Class<? extends Log> implClass) {
        try {
            Constructor<? extends Log> candidate = implClass.getConstructor(String.class);
            Log log = candidate.newInstance(LogFactory.class.getName());
            if (log.isDebugEnabled()) {
                log.debug("Logging initialized using '" + implClass + "' adapter.");
            }
            logConstructor = candidate;
        } catch (Throwable t) {
            throw new LogException("Error setting Log implementation.  Cause: " + t, t);
        }
    }
}
```

> 指定Mybatis logImpl

Mybatis可以通过在mybatis-config.xml中配置\<setting>标签来指定logImpl。

```xml
  <settings>
    <setting name="logImpl" value="Log4j"/>
  </settings>
```

- 解析setting标签，获取logImpl

org.apache.ibatis.builder.xml.XMLConfigBuilder#parseConfiguration

```java
private void parseConfiguration(XNode root) {
  try {
    Properties settings = settingsAsProperties(root.evalNode("settings"));
    loadCustomLogImpl(settings);
  } catch (Exception e) {
    throw new BuilderException("Error parsing SQL Mapper Configuration. Cause: " + e, e);
  }
}
```

- logImpl可以使用别名，Mybatis初始化阶段已经注册别名

```java
// 日志实现类
typeAliasRegistry.registerAlias("SLF4J", Slf4jImpl.class);
typeAliasRegistry.registerAlias("COMMONS_LOGGING", JakartaCommonsLoggingImpl.class);
typeAliasRegistry.registerAlias("LOG4J", Log4jImpl.class);
typeAliasRegistry.registerAlias("LOG4J2", Log4j2Impl.class);
typeAliasRegistry.registerAlias("JDK_LOGGING", Jdk14LoggingImpl.class);
typeAliasRegistry.registerAlias("STDOUT_LOGGING", StdOutImpl.class);
typeAliasRegistry.registerAlias("NO_LOGGING", NoLoggingImpl.class);
```

### Log使用

我们知道当开启Mybatis的Debug模式，在执行SQL时会记录一些日志，负责记录日志的工具类均在 `org.apache.ibatis.logging。jdbc` 包中。

由下图可知，负责记录日志的类有：ConnectionLogger、PreparedStatementLogger、ResultSetLogger、StatementLogger。其同时实现了接口`InvocationHandler`，由此可以Mybatis是通过JDK动态代理来记录业务方法前后日志。

==注：分析动态代理模式关键是要找到代理的对象是什么？如JDK动态代理，先要找到这行代码：==

`Proxy.newProxyInstance(ClassLoader loader, Class<?>[] interfaces, InvocationHandler h);`

![image-20230508142327777](material/MyBatis/Mybatis-jdbc日志类图.png)

## 数据源与事务

DataSource及Transaction模块

### DataSource

Mybatis是通过工厂方法模式来获取数据源，同时针对不同的 DataSource，MyBatis 提供了不同的工厂实现，由DataSourceFactory子类实现，来进行创建 DataSource。我们可以在 `mybatis-config.xml` 中的 `<environment>` 标签来指定不同类型的DataSource。

```xml
<environments default="development">
    <environment id="development">
        <dataSource type="POOLED">
            ...
        </dataSource>
    </environment>
    <environment id="product">
        <dataSource type="JNDI">
            ...
        </dataSource>
    </environment>
</environments>
```

- 相关DataSourceFactory

JndiDataSourceFactory是通过解析指定的配置文件的形式来创建对应的DataSource。

![image-20230508152343872](material/MyBatis/DataSourceFactory相关类.png)

#### UnpooledDataSource

PooledDataSource是UnpooledDataSource的池化实现。两者的具体实现均在`getConnection()`方法中。

- `UnpooledDataSource.getConnection()`

```java
private Connection doGetConnection(Properties properties) throws SQLException {
    // 底层依赖JDBC，在获取connection前需要完成 JDBC 驱动的初始化
    initializeDriver();
    // 创建数据库连接
    Connection connection = DriverManager.getConnection(url, properties);
    // 配置数据库连接，如超时时间，事务自动提交，事务的隔离级别等，当然这是session级别的设置
    configureConnection(connection);
    return connection;
}
```

#### PooledDataSource(池实现细节)

- `PooledDataSource.popConnection()`

> 池化的好处：
>
> 1. 在空闲时段**缓存**一定数量的数据库连接备用，防止被突发流量冲垮；
> 2. 实现数据库连接的**重用**，从而提高系统的响应速度；
> 3. **控制**数据库连接上限，防止连接过多造成数据库假死；
> 4. **统一**管理数据库连接，避免连接泄漏。
>
> 
>
> 关于池中连接数的设计考量：总连接数、空闲连接数、阻塞队列、超时时间
>
> 1. **总连接数达到了配置上限**，新的连接需要在**阻塞队列**中等待，等待其他连接释放；
> 2. **空闲连接数达到了配置上限**，后续返回到池中的空闲连接不会进入连接池缓存，而是直接关闭释放掉；
> 3. **连接总数的上限值设置过大**，会导致数据库因连接过多而僵死或崩溃；
> 4. **连接总数的上限值设置过小**，数据库资源等不到充分利用，而造成资源浪费；
> 5. **空闲连接数的上限值设置过大**，需要维护这些空闲连接造成服务资源以及数据库资源的浪费；
> 6. **空闲连接数的上限值设置过小**，当出现瞬间峰值请求时，服务的响应速度就会比较慢；

PooledDataSource本身不管理具体的连接池，真正管理连接的是**PooledState**，从代码中可以看到每次操作连接池时都会对**PooledState**进行加锁。被关联的Conection均由`PooledConnection`进行封装。

- DataSource连接池原理

![DataSource连接池原理](material/MyBatis/DataSource连接池原理.png)

##### PooledConnection

```java
// jdk动态代理
class PooledConnection implements InvocationHandler {
    private static final String CLOSE = "close";
    // 代理的对象 Connection，增强的代理逻辑需要看invoke()方法
    private static final Class<?>[] IFACES = new Class<?>[] { Connection.class };
    private final int hashCode;
    // 记录当前 this 的 PooledConnection 归属于哪个 dataSource
    private final PooledDataSource dataSource;
    // PooledConnection 真正数据库连接对象。
    private final Connection realConnection;
    // PooledConnection 数据库连接代理对象。
    private final Connection proxyConnection;
    // 使用方从连接池中获取连接的时间戳
    private long checkoutTimestamp;
    // 连接创建的时间戳
    private long createdTimestamp;
    // 连接最后一次被使用的时间戳
    private long lastUsedTimestamp;
    // 数据库连接的标识。该标识是由数据库 URL、username 和 password 三部分组合计算出来的 hash 值，主要用于连接对象确认归属的连接池。
    private int connectionTypeCode;
    // 用于标识 PooledConnection 对象是否有效。
    // 该字段的主要目的是防止使用方将连接归还给连接池之后，依然保留该 PooledConnection 对象的引用
    // 并继续通过该引用的 PooledConnection 对象操作数据库。
    private boolean valid;

    // Connection 的代理方法
    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        String methodName = method.getName();
        // 只对 Connection 的 close() 方法进行拦截
        // 因为调用connection.close不是真正关闭connection，而是将connection归还给连接池
        if (CLOSE.equals(methodName)) {
            dataSource.pushConnection(this);
            return null;
        }
        try {
            // 只要不是Object的方法，都需要检测当前PooledConnection是否可用
            if (!Object.class.equals(method.getDeclaringClass())) {
                // issue #579 toString() should never fail
                // throw an SQLException instead of a Runtime
                checkConnection();
            }
            // 调用realConnection的对应方法
            return method.invoke(realConnection, args);
        } catch (Throwable t) {
            throw ExceptionUtil.unwrapThrowable(t);
        }
    }
}
```

##### PoolState

PoolState负责维护管理连接池中所有 PooledConnection 对象的状态。

```java
public class PoolState {
	// 注意：这些属性都是 protected 的，可以通过PoolState.field来访问
    protected PooledDataSource dataSource;
	// 空闲状态的 PooledConnection 对象集合
    protected final List<PooledConnection> idleConnections = new ArrayList<>();
    // 活跃状态的 PooledConnection 对象集合
    protected final List<PooledConnection> activeConnections = new ArrayList<>();
    // 请求数据库连接的次数
    protected long requestCount = 0;
    // 获取连接的累积耗时
    protected long accumulatedRequestTime = 0;
    // 所有连接的 checkoutTime 累加
    // checkoutTime: 表示的是使用方从连接池中取出连接到归还连接的总时长，也就是连接被使用的时长
    protected long accumulatedCheckoutTime = 0;
    // 当连接长时间未归还给连接池时，会被认为该连接超时，该字段记录了超时的连接个数
    protected long claimedOverdueConnectionCount = 0;
    // 记录了累积超时时间
    protected long accumulatedCheckoutTimeOfOverdueConnections = 0;
    // 当连接池全部连接已经被占用之后，新的请求会阻塞等待，该字段就记录了累积的阻塞等待总时间
    protected long accumulatedWaitTime = 0;
    // 记录了阻塞等待总次数
    protected long hadToWaitCount = 0;
    // 无效的连接数
    protected long badConnectionCount = 0;
}
```

##### popConnection()

获取PooledConnection的核心实现：

- 步骤1：检查空闲连接池idleConnections是否有空闲连接，有则直接返回连接；
- 步骤2：活跃连接池activeConnections数量**未达到**上限值；
  - 创建新的PooledConnection -> 检查连接是否可用（ping） -> 加入活跃连接池
- 步骤3：活跃连接池activeConnections数量**达到**上限值；活跃连接池activeConnections中获取最早的活跃连接 -> 检查该连接是否超时 
  - 旧连接超时：若连接是自动提交事务则回滚事务-> 创建新的PooledConnection -> 旧PooledConnection 置为失效  -> 新的PooledConnection加入活跃连接池
  - 旧连接未超时：进入线程等待20s（`state.wait(poolTimeToWait);`）-> 1. 等待线程通知重新获取PooledConnection，重复步骤1 -> 2. 超时等待超20s，返回获取连接失败报错
- 步骤4：检查获取连接是否成功
  - 成功：返回连接 -> 加入活跃连接池activeConnections
  - 失败：记录状态 -> 返回获取连接失败报错

```java
private PooledConnection popConnection(String username, String password) throws SQLException {
    boolean countedWait = false;
    PooledConnection conn = null;
    long t = System.currentTimeMillis();
    int localBadConnectionCount = 0;

    // 异常时会退出，conn = null
    while (conn == null) {
        synchronized (state) {
            // 步骤1
            if (!state.idleConnections.isEmpty()) {
                // Pool has available connection
                conn = state.idleConnections.remove(0);
                if (log.isDebugEnabled()) {
                    log.debug("Checked out connection " + conn.getRealHashCode() + " from pool.");
                }
            } else {
                // 步骤2
                // Pool does not have available connection
                if (state.activeConnections.size() < poolMaximumActiveConnections) {
                    // Can create new connection
                    conn = new PooledConnection(dataSource.getConnection(), this);
                    if (log.isDebugEnabled()) {
                        log.debug("Created connection " + conn.getRealHashCode() + ".");
                    }
                } else {
                    // 步骤3
                    // Cannot create new connection
                    PooledConnection oldestActiveConnection = state.activeConnections.get(0);
                    long longestCheckoutTime = oldestActiveConnection.getCheckoutTime();
                    if (longestCheckoutTime > poolMaximumCheckoutTime) {
                        // Can claim overdue connection
                        state.claimedOverdueConnectionCount++;
                        state.accumulatedCheckoutTimeOfOverdueConnections += longestCheckoutTime;
                        state.accumulatedCheckoutTime += longestCheckoutTime;
                        state.activeConnections.remove(oldestActiveConnection);
                        if (!oldestActiveConnection.getRealConnection().getAutoCommit()) {
                            try {
                                oldestActiveConnection.getRealConnection().rollback();
                            } catch (SQLException e) {
                               /*
                   				Just log a message for debug and continue to execute the following
                   				statement like nothing happened.
                   				Wrap the bad connection with a new PooledConnection, this will help
                   				to not interrupt current executing thread and give current thread a
                   				chance to join the next competition for another valid/good database
                   				connection. At the end of this loop, bad {@link @conn} will be set as null.
                 			  */
                                log.debug("Bad connection. Could not roll back");
                            }
                        }
                        conn = new PooledConnection(oldestActiveConnection.getRealConnection(), this);
                        conn.setCreatedTimestamp(oldestActiveConnection.getCreatedTimestamp());
                        conn.setLastUsedTimestamp(oldestActiveConnection.getLastUsedTimestamp());
                        oldestActiveConnection.invalidate();
                        if (log.isDebugEnabled()) {
                            log.debug("Claimed overdue connection " + conn.getRealHashCode() + ".");
                        }
                    } else {
                        // Must wait
                        try {
                            if (!countedWait) {
                                state.hadToWaitCount++;
                                countedWait = true;
                            }
                            if (log.isDebugEnabled()) {
                                log.debug("Waiting as long as " + poolTimeToWait + " milliseconds for connection.");
                            }
                            long wt = System.currentTimeMillis();
                            state.wait(poolTimeToWait);
                            state.accumulatedWaitTime += System.currentTimeMillis() - wt;
                        } catch (InterruptedException e) {
                            break;
                        }
                    }
                }
            }
            // 步骤4
            if (conn != null) {
                // ping to server and check the connection is valid or not
                if (conn.isValid()) {
                    if (!conn.getRealConnection().getAutoCommit()) {
                        conn.getRealConnection().rollback();
                    }
                    conn.setConnectionTypeCode(assembleConnectionTypeCode(dataSource.getUrl(), username, password));
                    conn.setCheckoutTimestamp(System.currentTimeMillis());
                    conn.setLastUsedTimestamp(System.currentTimeMillis());
                    state.activeConnections.add(conn);
                    state.requestCount++;
                    state.accumulatedRequestTime += System.currentTimeMillis() - t;
                } else {
                    if (log.isDebugEnabled()) {
                        log.debug("A bad connection (" + conn.getRealHashCode() + ") was returned from the pool, getting another connection.");
                    }
                    state.badConnectionCount++;
                    localBadConnectionCount++;
                    conn = null;
                    if (localBadConnectionCount > (poolMaximumIdleConnections + poolMaximumLocalBadConnectionTolerance)) {
                        if (log.isDebugEnabled()) {
                            log.debug("PooledDataSource: Could not get a good connection to the database.");
                        }
                        throw new SQLException("PooledDataSource: Could not get a good connection to the database.");
                    }
                }
            }
        }

    }

    if (conn == null) {
        if (log.isDebugEnabled()) {
            log.debug("PooledDataSource: Unknown severe error condition.  The connection pool returned a null connection.");
        }
        throw new SQLException("PooledDataSource: Unknown severe error condition.  The connection pool returned a null connection.");
    }

    return conn;
}
```

##### pushConnection()

PooledConnection归还连接池核心实现：

- 步骤1：PooledConnection连接是否可用，若不可用，则无需归还，等待gc回收
- 步骤2：空闲的连接数是否小于最大空闲连接数，
  - 若是：PooledConnection需要归还连接池。记录状态 -> 创建新的PooledConnection -> 设置时间戳 -> 新的PooledConnection 加入空闲连接池中 -> 旧PooledConnection 置为失效
  - 若否：PooledConnection无需归还连接池，PooledConnection需要close。记录状态 ->  回滚connection的事务 -> connection.close -> 旧PooledConnection 置为失效

> 延申问题：归还PooledConnection只是涉及空闲连接池idleConnections，那么核心连接池activeConnections呢？

```java
protected void pushConnection(PooledConnection conn) throws SQLException {
    synchronized (state) {
        // 移出活跃连接池
        state.activeConnections.remove(conn);
        if (conn.isValid()) { // 检测该 PooledConnection 对象是否可用
            // 步骤2 -> 是
            if (state.idleConnections.size() < poolMaximumIdleConnections && conn.getConnectionTypeCode() == expectedConnectionTypeCode) {
                state.accumulatedCheckoutTime += conn.getCheckoutTime();
                if (!conn.getRealConnection().getAutoCommit()) {
                    conn.getRealConnection().rollback();
                }
                // newConn 沿用旧连接的conn.getRealConnection()和dataSource
                PooledConnection newConn = new PooledConnection(conn.getRealConnection(), this);
                // 加入空闲连接池中
                state.idleConnections.add(newConn);
                newConn.setCreatedTimestamp(conn.getCreatedTimestamp());
                newConn.setLastUsedTimestamp(conn.getLastUsedTimestamp());
                // 旧PooledConnection置为失效状态
                conn.invalidate();
                if (log.isDebugEnabled()) {
                    log.debug("Returned connection " + newConn.getRealHashCode() + " to pool.");
                }
                // 唤醒等待锁的线程
                state.notifyAll();
            } else {
                // 步骤2 -> 否
                state.accumulatedCheckoutTime += conn.getCheckoutTime();
                // 如果connection不是自动提交事务，需要先回滚事务
                if (!conn.getRealConnection().getAutoCommit()) {
                    conn.getRealConnection().rollback();
                }
                // 关闭连接
                conn.getRealConnection().close();
                if (log.isDebugEnabled()) {
                    log.debug("Closed connection " + conn.getRealHashCode() + ".");
                }
                // 旧PooledConnection置为失效状态
                conn.invalidate();
            }
        } else {
            // 步骤1，记录badConnectionCount状态，PooledConnection等待gc
            if (log.isDebugEnabled()) {
                log.debug("A bad connection (" + conn.getRealHashCode() + ") attempted to return to the pool, discarding connection.");
            }
            state.badConnectionCount++;
        }
    }
}
```

### Transaction

我们可以通过mybatis-config.xml中`<environments>`标签来配置事务的相关，可供配置的事务管理器有：`JDBC| MANAGED`，如下：

```xml
<environments default="development">
  <environment id="development">
    <transactionManager type="JDBC"/>
	<transactionManager type="MANAGED"/>
    <dataSource type="POOLED"/>
  </environment>
</environments>
```

Mybatis定义事务接口的抽象：`org.apache.ibatis.transaction.Transaction`

![image-20230519100654273](material/MyBatis/Mybatis-Transaction类图.png)

> JdbcTransaction

直接使用JDBC提供的API对事务进行操作提交或回滚，依赖于从DataSource中获取的Connection来管理事务的作用域。

注意：Mybatis一般会默认设置自动提交事务，处于性能的考虑。（如果大量的Select语句在每次执行时，都调用commit/rollback性能耗费更大）

> ManagedTransaction

方法基本上无实现，而是让**外部容器来管理事务**的整个生命周期（比如 JEE 应用服务器的上下文）。 

默认情况下它会关闭连接，然而一些容器并不希望这样，因此需要将 closeConnection 属性设置为 false 来阻止它默认的关闭行为。

==注意：如果你正在使用 Spring + MyBatis，则没有必要配置事务管理器，因为 Spring 模块会使用自带的管理器来覆盖前面的配置。Spring相关的事务：org.mybatis.spring.transaction.SpringManagedTransaction==

## Binding-Mapper

Bingding模块，主要解决了执行SQL语句，传递参数等绑定问题。

### Why Mapper？

> 有MyBatis之前-iBatis

Mybatis的前身是iBatis，使用iBatis查询一个对象时，通常的语句是:

`SqlSession.queryForObject ("findById", customerId)`

findById就是SQL语句的id标识，customerId为该SQL语句的传参。从上述语句我们可以分析出，id标识使用固定的字符串，以为着如果传递错误的id标识时，在iBaits在初始化时不能发现该id标识是错误的，不能够提前把错误暴露出去。

**解决问题：**

​	1. 建立id标识的唯一行 

​	2. 提前将错误的id标识提前暴露出去

> 有MyBatis之后

MyBatis使用Mapper接口，接口中定义SQL语句、方法名id以及传参。在MyBatis初始化过程中，会将Mapper接口对应的映射配置文件中的SQL语句相关联，如果存在无法关联的SQL语句，MyBatis就会抛出异常，帮助我们及时发现问题。

建立绑定关系之后，我们可以通过使用`Mapper.method()`来执行指定的SQL语句。

**延申问题：**

1. 我们知道接口没有方法的实现，那么调用的Mapper是MyBatis生成的代理对象
2. 那么Mapper代理对象是如何创建的，原理是？

> Mapper相关组件，也是binding模块核心组件

Binding模块包：`org.apache.ibatis.binding`

![image-20230522102910188](material/MyBatis/Binding模块相关组件.png)

### MapperRegistry

MapperRegistry，主要用来保存MapperProxyFactory的对象类，当我们添加一个Mapper时，就会new MapperProxyFactory保存在MapperRegistry，后面用来生成Mapper的对象对象MapperProxy。

```java
public class MapperRegistry {
	// Configuration：在addMapper，初始化过程中需要用到该config
    private final Configuration config;
    // key: Mapper class类型, value: MapperProxyFactory
    private final Map<Class<?>, MapperProxyFactory<?>> knownMappers = new HashMap<>();
}
```

- getMapper

委托MapperProxyFactory生成MapperProxy对象

```java
public <T> T getMapper(Class<T> type, SqlSession sqlSession) {
  final MapperProxyFactory<T> mapperProxyFactory = (MapperProxyFactory<T>) knownMappers.get(type);
  if (mapperProxyFactory == null) {
    throw new BindingException("Type " + type + " is not known to the MapperRegistry.");
  }
  try {
    return mapperProxyFactory.newInstance(sqlSession);
  } catch (Exception e) {
    throw new BindingException("Error getting mapper instance. Cause: " + e, e);
  }
}
```

- addMapper

1. 注册MapperProxyFactory，knownMappers.put()

2. 解析该Mapper，利用MapperAnnotationBuilder和config

```java
public <T> void addMapper(Class<T> type) {
    if (type.isInterface()) {
        if (hasMapper(type)) {
            throw new BindingException("Type " + type + " is already known to the MapperRegistry.");
        }
        boolean loadCompleted = false;
        try {
            knownMappers.put(type, new MapperProxyFactory<>(type));
            // It's important that the type is added before the parser is run
            // otherwise the binding may automatically be attempted by the
            // mapper parser. If the type is already known, it won't try.
            MapperAnnotationBuilder parser = new MapperAnnotationBuilder(config, type);
            parser.parse();
            loadCompleted = true;
        } finally {
            if (!loadCompleted) {
                knownMappers.remove(type);
            }
        }
    }
}
```

### MapperProxyFactory

MapperProxyFactory主要用来生成代理对象MapperProxy。留意这里有个`methodCache`

methodCache由MapperProxyFactory传入MapperProxy，在MapperProxy方法调用时put进入。由MapperProxyFactory传入这个methodCache的原因是，Mybatis希望：*Reuse MethodHandle for default methods*。

**引申：**

Java7之后，引入了另外一种反射使用方式`MethodHandle`，关于两者的区分和使用方式：

- 性能区别

  MethodHandle性能更优，因为访问检查MethodHandle在创建已经完成，而反射在运行时才检查；

- 访问控制区别

  Reflection可以绕过Java的访问限制，可以访问和修改私有的成员

  MethodHandle尊重Java的访问限制，能访问与调用者在同一个包或者具有相应访问权限的成员。

  MyBatis使用的MethodHandle还是Reflection的判断是: `java.lang.reflect.Method#isDefault`(public的interface)

```java
public class MapperProxyFactory<T> {
	// Mapper接口的class类型
    private final Class<T> mapperInterface;
    // key: method, value: MapperMethodInvoker作用是执行代理方法, 区分不同方法修饰符使用不同的反射方式, Method或MethodHandle
    private final Map<Method, MapperMethodInvoker> methodCache = new ConcurrentHashMap<>();

    public MapperProxyFactory(Class<T> mapperInterface) {
        this.mapperInterface = mapperInterface;
    }

    public Class<T> getMapperInterface() {
        return mapperInterface;
    }

    public Map<Method, MapperMethodInvoker> getMethodCache() {
        return methodCache;
    }

    @SuppressWarnings("unchecked")
    protected T newInstance(MapperProxy<T> mapperProxy) {
        return (T) Proxy.newProxyInstance(mapperInterface.getClassLoader(), new Class[] { mapperInterface }, mapperProxy);
    }

    public T newInstance(SqlSession sqlSession) {
        final MapperProxy<T> mapperProxy = new MapperProxy<>(sqlSession, mapperInterface, methodCache);
        return newInstance(mapperProxy);
    }

}
```

### MapperProxy

MapperProxyFactory创建完代理对象MapperProxy之后，业务方法每次调用`Mapper.method()`都会进入`MapperProxy.invoke()`方法。

- invoke

```java
public class MapperProxy<T> implements InvocationHandler, Serializable {
    
    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        try {
            if (Object.class.equals(method.getDeclaringClass())) { // 如果目标方法继承自 Object，则直接调用目标方法
                return method.invoke(this, args);
            } else {
                // 非继承Oject类的方法, 实际上委托给MapperMethodInvoker, 由其来调用invoke()方法
                return cachedInvoker(method).invoke(proxy, method, args, sqlSession);
            }
        } catch (Throwable t) {
            throw ExceptionUtil.unwrapThrowable(t);
        }
    }
}
```

- cachedInvoker

使用MethodHandler情况，反射调用接口中有方法实现的方法：

- 方法修饰符为`public`，并且不包含`abstract`和`static`修饰符。
- 方法所属的类是一个接口。

==其他情况使用MapperMethod，即MyBatis默认实现Mapper代理对象的方式。==

```java
private MapperMethodInvoker cachedInvoker(Method method) throws Throwable {
    try {
        // methodCache由MapperProxyFactory传入，先从缓存中获取
        return methodCache.computeIfAbsent(method, m -> {
            // public interface
            // 默认方法是公共非抽象实例方法，即具有主体的非静态方法，在接口类型中声明。
            if (m.isDefault()) {
                try {
                    // 这里根据JDK版本的不同，获取方法对应的MethodHandle的方式也有所不同
                    if (privateLookupInMethod == null) {
                        return new DefaultMethodInvoker(getMethodHandleJava8(method));
                    } else {
                        return new DefaultMethodInvoker(getMethodHandleJava9(method));
                    }
                } catch (IllegalAccessException | InstantiationException | InvocationTargetException
                         | NoSuchMethodException e) {
                    throw new RuntimeException(e);
                }
            } else {
                // 对于其他方法，会创建MapperMethod并使用PlainMethodInvoker封装
                return new PlainMethodInvoker(new MapperMethod(mapperInterface, method, sqlSession.getConfiguration()));
            }
        });
    } catch (RuntimeException re) {
        Throwable cause = re.getCause();
        throw cause == null ? re : cause;
    }
}
```

- DefaultMethodInvoker

```java
private static class DefaultMethodInvoker implements MapperMethodInvoker {
    @Override
    public Object invoke(Object proxy, Method method, Object[] args, SqlSession sqlSession) throws Throwable {
        // 首先将MethodHandle绑定到一个实例对象上，然后调用invokeWithArguments()方法执行目标方法
        return methodHandle.bindTo(proxy).invokeWithArguments(args);
    }
}
```

- PlainMethodInvoker

```java
private static class PlainMethodInvoker implements MapperMethodInvoker {
    @Override
    public Object invoke(Object proxy, Method method, Object[] args, SqlSession sqlSession) throws Throwable {
        // 直接执行MapperMethod.execute()方法完成方法调用
        return mapperMethod.execute(sqlSession, args);
    }
}
```

### MapperMethodInvoker

MapperProxy执行invoke()方法时，会寻找对应的MapperMethodInvoker，并委托给MapperMethodInvoker执行invoke方法。示例方法：`org.apache.ibatis.binding.MapperProxy#invoke`。

```java
public class MapperProxy<T> implements InvocationHandler, Serializable { 
    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        cachedInvoker(method).invoke(proxy, method, args, sqlSession)
    }
}
```

`cachedInvoker(method)`会返回两种类型的MapperMethodInvoker，分别是`DefaultMethodInvoker`和`PlainMethodInvoker`。

-  DefaultMethodInvoker

  代理方法满足，`public && non-abstract && declared in an interface`使用该MapperMethodInvoker，我们使用的Mapper定义的方法是抽象的，所以不会使用该MapperMethodInvoker。

-  PlainMethodInvoker：

​	我们定义的Mapper方法就是使用这个MapperMethodInvoker。

> 相关类

如下，正在执行代理的业务方法是`MapperMethodInvoker.invoke()`。

- 对于PlainMethodInvoker来说，invoke调用的是`mapperMethod.execute(sqlSession, args)`
- 对于DefaultMethodInvoker来说，invoke调用的是`methodHandle.bindTo(proxy).invokeWithArguments(args)`

```java
interface MapperMethodInvoker {
    Object invoke(Object proxy, Method method, Object[] args, SqlSession sqlSession) throws Throwable;
}

private static class PlainMethodInvoker implements MapperMethodInvoker {
    private final MapperMethod mapperMethod;

    public PlainMethodInvoker(MapperMethod mapperMethod) {
        super();
        this.mapperMethod = mapperMethod;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args, SqlSession sqlSession) throws Throwable {
        return mapperMethod.execute(sqlSession, args);
    }
}

private static class DefaultMethodInvoker implements MapperMethodInvoker {
    private final MethodHandle methodHandle;

    public DefaultMethodInvoker(MethodHandle methodHandle) {
        super();
        this.methodHandle = methodHandle;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args, SqlSession sqlSession) throws Throwable {
        return methodHandle.bindTo(proxy).invokeWithArguments(args);
    }
}
```

### MapperMethod

由上述可知对于调用方法是默认方法（**public, non-abstract, declared in an interface，即声明在接口中, 公共非抽象的方法**），MyBatis会采用MethodHandle的方式进行代理调用；与之相反，MyBatis就采用MapperMethod方式来调用。

MapperMethod即是MyBatis用代理对象执行SQL，返回Object result的地方，执行方法：`org.apache.ibatis.binding.MapperMethod#execute`

```java
public class MapperMethod {
  private final SqlCommand command;
  private final MethodSignature method;
}
```

#### SqlCommand

SqlCommand主要是保存执行SQL的唯一标识（package.InterfaceName.methodName），以及执行SQL命令的类型，如：`INSERT, UPDATE, DELETE, SELECT, FLUSH`。

> 找到匹配的MappedStatement

两个方法 -> mapperInterface 指定的接口（父），declaringClass 方法声明的类（可能是子）

查找逻辑，主要是用id标识configuration中去寻找，寻找方法：`org.apache.ibatis.binding.MapperMethod.SqlCommand#resolveMappedStatement`

1. 从mapperInterface中获取，有则return;
2. `if (mapperInterface.equals(declaringClass))`，即方法声明的类就是父的接口，return null;
3. `if (declaringClass.isAssignableFrom(superInterface))`，即方法声明的类有父类接口，则递归从父类中查找。

```java
public static class SqlCommand {
	// 构造方法中从初始化阶段解析好的MappedStatement中获取id标识和命令类型
    // id标识：全路径包名 + 类名 + 方法名
    private final String name;	// id标识
    private final SqlCommandType type; // SQL命令类型

    public SqlCommand(Configuration configuration, Class<?> mapperInterface, Method method) {
        final String methodName = method.getName();
        final Class<?> declaringClass = method.getDeclaringClass();
        // 找到匹配的MappedStatement：1. 从指定的mapperInterface
        // MappedStatement就是Mapper.xml解析之后的对象，包括SQL语句，ResultMap等
        MappedStatement ms = resolveMappedStatement(mapperInterface, methodName, declaringClass, configuration);
        if (ms == null) {
            if (method.getAnnotation(Flush.class) != null) {
                name = null;
                type = SqlCommandType.FLUSH;
            } else {
                throw new BindingException("Invalid bound statement (not found): "
                                           + mapperInterface.getName() + "." + methodName);
            }
        } else {
            name = ms.getId();
            type = ms.getSqlCommandType();
            if (type == SqlCommandType.UNKNOWN) {
                throw new BindingException("Unknown execution method for: " + name);
            }
        }
    }
}
```

#### MethodSignature

MethodSignature封装执行方法的信息。

```java
public static class MethodSignature {

    private final boolean returnsMany; // 返回值是否为集合或数组
    private final boolean returnsMap; // 返回值是否为Map
    private final boolean returnsVoid; // 返回值是否为void
    private final boolean returnsCursor; // 返回值是否为Cursor
    private final boolean returnsOptional; // 返回值是否为Optional 
    private final Class<?> returnType; // 方法返回值的具体类型
    private final String mapKey;// 如果返回值是Map类型, 可以注解指定 @MapKey 的列名 
    private final Integer resultHandlerIndex; // 标记该方法参数列表中 ResultHandler类型参数的位置, 唯一性校验
    private final Integer rowBoundsIndex; // 标记该方法参数列表中 RowBounds类型参数的位置, 唯一性校验
    /**
     * 这是一个处理 Mapper接口中方法参数列表的解析器，它使用了一个 SortedMap<Integer, String>
     * 类型的容器，记录了参数在参数列表中的位置索引与参数名之间的对应关系，key参数 在参数列表中的索引位置，
     * value参数名(参数名可用@Param注解指定，默认使用参数索引作为其名称)
     * 
     * names 集合会跳过 RowBounds 类型以及 ResultHandler 类型的参数
     */
    private final ParamNameResolver paramNameResolver;// 解析方法参数列表的工具类。

    public MethodSignature(Configuration configuration, Class<?> mapperInterface, Method method) {
        Type resolvedReturnType = TypeParameterResolver.resolveReturnType(method, mapperInterface);
        if (resolvedReturnType instanceof Class<?>) {
            this.returnType = (Class<?>) resolvedReturnType;
        } else if (resolvedReturnType instanceof ParameterizedType) {
            this.returnType = (Class<?>) ((ParameterizedType) resolvedReturnType).getRawType();
        } else {
            this.returnType = method.getReturnType();
        }
        this.returnsVoid = void.class.equals(this.returnType);
        this.returnsMany = configuration.getObjectFactory().isCollection(this.returnType) || this.returnType.isArray();
        this.returnsCursor = Cursor.class.equals(this.returnType);
        this.returnsOptional = Optional.class.equals(this.returnType);
        this.mapKey = getMapKey(method);
        this.returnsMap = this.mapKey != null;
        this.rowBoundsIndex = getUniqueParamIndex(method, RowBounds.class);
        this.resultHandlerIndex = getUniqueParamIndex(method, ResultHandler.class);
        this.paramNameResolver = new ParamNameResolver(configuration, method);
    }
}
```

##### ParamNameResolver

SQL参数解析器，其中 names 字段（SortedMap<Integer, String>类型）记录了各个参数在参数列表中的位置以及参数名称，其中 key 是参数在参数列表中的位置索引，value 为参数的名称。

比如：我们日常使用标记参数@Param，#{param1}, #{paramName}, #{0}, #{1}等等用法，就是ParamNameResolver负责解析存储和获取等。

> 构造方法保存参数信息

```java
public class ParamNameResolver {
     // aMethod(@Param("M") int a, @Param("N") int b) ->  {{0, "M"}, {1, "N"}}
	// aMethod(int a, int b) ->  {{0, "arg0"}, {1, "arg1"}}
	// aMethod(int a, RowBounds rb, int b) -> {{0, "arg0"}, {2, "arg1"}}
    private final SortedMap<Integer, String> names;

    // 构造方法
    public ParamNameResolver(Configuration config, Method method) {
        final Class<?>[] paramTypes = method.getParameterTypes();
        final Annotation[][] paramAnnotations = method.getParameterAnnotations();
        final SortedMap<Integer, String> map = new TreeMap<>();
        int paramCount = paramAnnotations.length;
        // get names from @Param annotations
        for (int paramIndex = 0; paramIndex < paramCount; paramIndex++) {
            // 跳过 RowBounds 类型以及 ResultHandler 类型的参数
            if (isSpecialParameter(paramTypes[paramIndex])) {
                // skip special parameters
                continue;
            }
            String name = null;
            // 解析@Param注解，优先级高
            for (Annotation annotation : paramAnnotations[paramIndex]) {
                if (annotation instanceof Param) {
                    hasParamAnnotation = true;
                    name = ((Param) annotation).value();
                    break;
                }
            }
            // 没有指定@Param注解时
            if (name == null) {
                // @Param was not specified.
                if (config.isUseActualParamName()) {
                    name = getActualParamName(method, paramIndex);
                }
                if (name == null) {
                    // use the parameter index as the name ("0", "1", ...)
                    // gcode issue #71
                    name = String.valueOf(map.size());
                }
            }
            map.put(paramIndex, name);
        }
        names = Collections.unmodifiableSortedMap(map);
    }
}
```

> 调用获取参数信息

```java
public Object getNamedParams(Object[] args) {
    final int paramCount = names.size();
    if (args == null || paramCount == 0) {
        return null;
    } else if (!hasParamAnnotation && paramCount == 1) {
        return args[names.firstKey()];
    } else {
        final Map<String, Object> param = new ParamMap<>();
        int i = 0;
        for (Map.Entry<Integer, String> entry : names.entrySet()) {
            // entry: 0 -> name
            param.put(entry.getValue(), args[entry.getKey()]);
            // add generic param names (param1, param2, ...)
            final String genericParamName = GENERIC_NAME_PREFIX + (i + 1);
            // ensure not to overwrite parameter named with @Param
            if (!names.containsValue(genericParamName)) {
                param.put(genericParamName, args[entry.getKey()]);
            }
            i++;
        }
        return param;
    }
}
```

从获取的Param参数可以看到，param1和param2都会解析一份，因此我们可以在sql总使用#{param1}，#{param2}来获取我们指定的参数。

![image-20230605113354053](material/MyBatis/ParamName的获取.png)

##### execute

execute：SQL参数绑定和结果集绑定的方法，`org.apache.ibatis.binding.MapperMethod#execute`

当我们调用`Mapper.method()`时，我们大致会经历如下的流程：

业务方法`Mapper.method()`	=>	`MapperProxyFactory.newInstance`	=>	`MapperProxy.invoke()` 	=>	

`PlainMethodInvoker.invoke()`	=>	 `MapperMethod.execute()`。

> execute

```java
public Object execute(SqlSession sqlSession, Object[] args) {
    Object result;
    switch (command.getType()) {
        case INSERT: {
            // 从paramNameResolver中获取参数，也就是调用getNamedParams()方法
            Object param = method.convertArgsToSqlCommandParam(args);
            // 对结果的类型进行转换，INSERT,UPDATE,DELETE 类型返回主要是void, Integer, Long, Boolean等， 
            // rowCountResult()方法对其转换
            result = rowCountResult(sqlSession.insert(command.getName(), param));
            break;
        }
        case UPDATE: {
            Object param = method.convertArgsToSqlCommandParam(args);
            result = rowCountResult(sqlSession.update(command.getName(), param));
            break;
        }
        case DELETE: {
            Object param = method.convertArgsToSqlCommandParam(args);
            result = rowCountResult(sqlSession.delete(command.getName(), param));
            break;
        }
        case SELECT:
            if (method.returnsVoid() && method.hasResultHandler()) {
                // 处理返回值为 void 且 ResultSet 通过 ResultHandler 处理的方法
                executeWithResultHandler(sqlSession, args);
                result = null;
            } else if (method.returnsMany()) {
                // 处理返回值为集合 或 数组的方法
                result = executeForMany(sqlSession, args);
            } else if (method.returnsMap()) {
                // 处理返回值为 Map 的方法
                result = executeForMap(sqlSession, args);
            } else if (method.returnsCursor()) {
                // 处理返回值为 Cursor 的方法
                result = executeForCursor(sqlSession, args);
            } else {
                // 处理返回值为单一对象的方法
                Object param = method.convertArgsToSqlCommandParam(args);
                result = sqlSession.selectOne(command.getName(), param);
                if (method.returnsOptional()
                    && (result == null || !method.getReturnType().equals(result.getClass()))) {
                    result = Optional.ofNullable(result);
                }
            }
            break;
        case FLUSH:
            result = sqlSession.flushStatements();
            break;
        default:
            throw new BindingException("Unknown execution method for: " + command.getName());
    }
    if (result == null && method.getReturnType().isPrimitive() && !method.returnsVoid()) {
        throw new BindingException("Mapper method '" + command.getName()
                                   + " attempted to return null from a method with a primitive return type (" + method.getReturnType() + ").");
    }
    return result;
}
```

> rowCountResult

当执行 `insert`、`update`、`delete` 类型的 sql 语句 时，其执行结果都要经过本方法处理

```java
private Object rowCountResult(int rowCount) {
    final Object result;
    if (method.returnsVoid()) {
        // 返回值为 void 时
        result = null;
    } else if (Integer.class.equals(method.getReturnType()) || Integer.TYPE.equals(method.getReturnType())) {
        // 返回值为 Integer 时
        result = rowCount;
    } else if (Long.class.equals(method.getReturnType()) || Long.TYPE.equals(method.getReturnType())) {
        // 返回值为 Long 时
        result = (long)rowCount;
    } else if (Boolean.class.equals(method.getReturnType()) || Boolean.TYPE.equals(method.getReturnType())) {
        // 返回值为 Boolean 时
        result = rowCount > 0;
    } else {
        throw new BindingException("Mapper method '" + command.getName() + "' has an unsupported return type: " + method.getReturnType());
    }
    return result;
}
```

> executeWithResultHandler

如果 Mapper接口 中定义的方法准备使用 ResultHandler 处理查询结果集，则通过此方法处理。

```java
private void executeWithResultHandler(SqlSession sqlSession, Object[] args) {
	// command.getName() => 全类名.方法名 （org.apache.ibatis.binding.BoundBlogMapper.collectRangeBlogs）
    //  获取 sql语句对应的 MappedStatement 对象，该对象中记录了 sql 语句相关信息
    MappedStatement ms = sqlSession.getConfiguration().getMappedStatement(command.getName());
    // 当使用 ResultHandler 处理结果集时，必须指定 ResultMap 或 ResultType
    if (!StatementType.CALLABLE.equals(ms.getStatementType()) 
        && void.class.equals(ms.getResultMaps().get(0).getType())) {
        throw new BindingException("method " + command.getName()
                                   + " needs either a @ResultMap annotation, a @ResultType annotation,"
                                   + " or a resultType attribute in XML so a ResultHandler can be used as a parameter.");
    }
    // 从paramNameResolver中获取参数，也就是调用getNamedParams()方法
    Object param = method.convertArgsToSqlCommandParam(args);
    // 如果实参列表中有 RowBounds 类型参数，rowBoundsIndex != null
    if (method.hasRowBounds()) {
        // 从 args参数列表 中获取 RowBounds对象
        RowBounds rowBounds = method.extractRowBounds(args);
         // 执行查询，并用指定的 ResultHandler 处理结果对象
        sqlSession.select(command.getName(), param, rowBounds, method.extractResultHandler(args));
    } else {
        sqlSession.select(command.getName(), param, method.extractResultHandler(args));
    }
}
```

> executeForMany

如果 Mapper 接口中对应方法的返回值为集合(Collection接口实现类) 或 数组，则调用本方法将结果集处理成相应的集合或数组。

```java
private <E> Object executeForMany(SqlSession sqlSession, Object[] args) {
    List<E> result;
    Object param = method.convertArgsToSqlCommandParam(args);
    // 参数列表中是否有 RowBounds类型的参数，rowBoundsIndex != null
    if (method.hasRowBounds()) {
        RowBounds rowBounds = method.extractRowBounds(args);
        // 这里使用了 selectList()方法 进行查询，所以返回的结果集就是 List类型的
        result = sqlSession.selectList(command.getName(), param, rowBounds);
    } else {
        result = sqlSession.selectList(command.getName(), param);
    }
    // 将结果集转换为数组或 Collection集合
    // issue #510 Collections & arrays support
    if (!method.getReturnType().isAssignableFrom(result.getClass())) {
        if (method.getReturnType().isArray()) {
            return convertToArray(result);
        } else {
            return convertToDeclaredCollection(sqlSession.getConfiguration(), result);
        }
    }
    return result;
}
```

> executeForMap

如果 Mapper接口 中对应方法的返回值为类型为 Map，则调用此方法执行 sql语句。

```java
private <K, V> Map<K, V> executeForMap(SqlSession sqlSession, Object[] args) {
    Map<K, V> result;
    Object param = method.convertArgsToSqlCommandParam(args);
    if (method.hasRowBounds()) {
        RowBounds rowBounds = method.extractRowBounds(args);
        // 注意这里调用的是 SqlSession 的 selectMap()方法，返回的是一个 Map类型结果集
        result = sqlSession.selectMap(command.getName(), param, method.getMapKey(), rowBounds);
    } else {
        result = sqlSession.selectMap(command.getName(), param, method.getMapKey());
    }
    return result;
}
```

> executeForCursor

本方法与上面的 executeForMap()方法 类似，只不过 sqlSession 调用的是 selectCursor()。

```java
private <T> Cursor<T> executeForCursor(SqlSession sqlSession, Object[] args) {
    Cursor<T> result;
    Object param = method.convertArgsToSqlCommandParam(args);
    if (method.hasRowBounds()) {
        RowBounds rowBounds = method.extractRowBounds(args);
        result = sqlSession.selectCursor(command.getName(), param, rowBounds);
    } else {
        result = sqlSession.selectCursor(command.getName(), param);
    }
    return result;
}
```

## 缓存管理

缓存管理相关类所在包：org.apache.ibatis.cache

![](.\material\MyBatis\Cache装饰器相关类.png)

### PerpetualCache

Cache的具体实现类，装饰类也是基于此来封装。可以看到PerpetualCache使用Map作为缓存，id作为唯一标识（留意重写的equals和hashCode方法，都是基于id的）。

```java
public class PerpetualCache implements Cache {
  private final String id;
  private final Map<Object, Object> cache = new HashMap<>();
}
```

### blockingCache

blockingCache在原有的基础上**增加一个阻塞获取缓存**功能。具体实现是：每次获取缓存前都需要根据key来获取锁trylock，如果获取不到则阻塞等待。

从`getObject(Object key)`方法可以看到，如果获取的value为null，则一直lock，直到调用当前线程调用`putObject(Object key, Object value)`，或者有线程调用`removeObject(Object key)`才会释放锁。**（坑点）**

removeObject(Object key)：这个方法命名很奇葩，但是人家还是写了注释的。（despite of its name, this method is called only to release locks）

```java
public class BlockingCache implements Cache {
    // 超时时间
    private long timeout;
    // cache的实现
    private final Cache delegate;
    // 保存锁的Map
    private final ConcurrentHashMap<Object, ReentrantLock> locks;

    @Override
    public void putObject(Object key, Object value) {
        try {
            delegate.putObject(key, value);
        } finally {
            releaseLock(key);
        }
    }

    @Override
    public Object getObject(Object key) {
        acquireLock(key);
        Object value = delegate.getObject(key);
        if (value != null) {
            releaseLock(key);
        }
        return value;
    }
}
```

### FifoCache

FifoCache：先进先出缓存，first in, first out cache。该类缓存需要注意一下问题：

- 维护队列
- 队列大小
- 缓存过期

源码中可知，FifoCache使用LinkedList来维护队列的顺序，队列大小默认为1024。在putObject时需要维持队列的大小，超出指定size时，需要remove oldest object。

```java
public class FifoCache implements Cache {

    private final Cache delegate;
    private final Deque<Object> keyList;
    private int size;

    public FifoCache(Cache delegate) {
        this.delegate = delegate;
        this.keyList = new LinkedList<>();
        this.size = 1024;
    }
    
    @Override
    public void putObject(Object key, Object value) {
        cycleKeyList(key);
        delegate.putObject(key, value);
    }

    @Override
    public Object getObject(Object key) {
        return delegate.getObject(key);
    }

    @Override
    public Object removeObject(Object key) {
        return delegate.removeObject(key);
    }

    @Override
    public void clear() {
        delegate.clear();
        keyList.clear();
    }

    private void cycleKeyList(Object key) {
        keyList.addLast(key);
        if (keyList.size() > size) {
            Object oldestKey = keyList.removeFirst();
            delegate.removeObject(oldestKey);
        }
    }
}
```

### LoggingCache

LoggingCache：以debug日志形式，记录缓存的命中率，可以配合其他装饰器缓存使用。

缺点是，没有提供重置方法，没有很直观地算出某段时间的缓存命中率。

```java
public class LoggingCache implements Cache {

    private final Log log;
    private final Cache delegate;
    protected int requests = 0;
    protected int hits = 0;

    public LoggingCache(Cache delegate) {
        this.delegate = delegate;
        this.log = LogFactory.getLog(getId());
    }

    @Override
    public Object getObject(Object key) {
        requests++;
        final Object value = delegate.getObject(key);
        if (value != null) {
            hits++;
        }
        if (log.isDebugEnabled()) {
            log.debug("Cache Hit Ratio [" + getId() + "]: " + getHitRatio());
        }
        return value;
    }

    private double getHitRatio() {
        return (double) hits / (double) requests;
    }
}
```

### LruCache

LruCache：最近使用缓存，least recently used cache。该类缓存需要注意一下问题：

- 维护队列
- 队列大小
- 缓存清理
- 缓存维护

实际上，LinkedHashMap已经维护好了LRU的规则，LruCache只需要维护参与LRU的key，重写`removeEldestEntry()`方法，触发LRU维护的方法，即getObject()，就可以实现LruCahce。

```java
public class LruCache implements Cache {

    private final Cache delegate;
    private Map<Object, Object> keyMap;
    private Object eldestKey;

    public LruCache(Cache delegate) {
        this.delegate = delegate;
        setSize(1024);
    }

    public void setSize(final int size) {
        // access = true， 代表getObject时维护链表的LRU有序性
        keyMap = new LinkedHashMap<Object, Object>(size, .75F, true) {
            private static final long serialVersionUID = 4267176411845948333L;
            // 父类LinkedHashMap默认返回false，需要按需重写
            @Override
            protected boolean removeEldestEntry(Map.Entry<Object, Object> eldest) {
                boolean tooBig = size() > size;
                if (tooBig) {
                    eldestKey = eldest.getKey();
                }
                return tooBig;
            }
        };
    }

    @Override
    public void putObject(Object key, Object value) {
        delegate.putObject(key, value);
        cycleKeyList(key);
    }

    @Override
    public Object getObject(Object key) {
        keyMap.get(key); //touch
        return delegate.getObject(key);
    }

    @Override
    public Object removeObject(Object key) {
        return delegate.removeObject(key);
    }

    @Override
    public void clear() {
        delegate.clear();
        keyMap.clear();
    }

    private void cycleKeyList(Object key) {
        keyMap.put(key, key);
        if (eldestKey != null) {
            delegate.removeObject(eldestKey);
            eldestKey = null;
        }
    }
}
```

### ScheduledCache

懒式清理缓存，当调用`getObejct()`, `putObejct()`, `removeObject()`方法时，判断缓存过期时间，若过期了则触发缓存清理。

### SerializedCache

序列化缓存，`putObject()`时序列化相应的value，`getObject()`时反序列化value。

> 摘取ChatGPT对序列化的回答：
>
> 
>
> Java中的序列化是将对象转换为字节流的过程，可以将对象在网络中进行传输或者存储到文件中。序列化的主要作用是实现对象的持久化和跨网络传输。
>
> 
>
> 以下是一些常见的序列化应用场景：
>
> 1. 对象持久化：通过序列化，可以将对象保存到磁盘上，以便在程序重新启动时恢复对象的状态。这在需要长期保存对象数据或者需要对对象进行传输的情况下非常有用。
> 2. 网络通信：在分布式系统或者客户端-服务器架构中，可以使用序列化将对象转换为字节流，然后通过网络进行传输。接收方可以将字节流反序列化为对象，以便恢复原始对象。
> 3. 缓存：序列化还可以用于将对象存储在缓存中，以提高性能。当从缓存中获取对象时，可以通过反序列化来恢复对象，而不必重新创建对象。
> 4. 远程方法调用（RPC）：序列化在远程方法调用中发挥重要作用。客户端可以将方法参数对象序列化为字节流，然后通过网络将其发送给远程服务器执行。服务器接收到字节流后，可以将其反序列化为对象，并执行相应的方法操作。
>
> 
>
> 相比于使用 JSON 进行序列化，Java 中的序列化（通常是通过 Java 序列化机制实现的）具有以下优点和缺点：
>
> 优点：
>
> 1. 简单易用：Java 序列化是 Java 语言内置的机制，使用起来相对简单，无需额外的库或依赖。
> 2. 对象完整性：Java 序列化可以完整地保存对象的状态，包括对象的字段值、引用关系和类的层次结构，因此可以轻松地保存和恢复对象的完整性。
> 3. 适用性广泛：Java 序列化可以序列化几乎所有的 Java 对象，包括自定义类和标准库中的类，无需额外的配置。
> 4. 支持循环引用：Java 序列化机制可以处理对象之间的循环引用，保证在序列化和反序列化过程中对象的引用关系不会丢失。
>
> 缺点：
>
> 1. **可读性差**：序列化后的字节流是二进制数据，不易于人类阅读和理解。相比之下，JSON 是一种文本格式，具有更好的可读性和可调试性。
> 2. **兼容性限制**：Java 序列化依赖于对象的类结构，如果对象的类发生变化（例如添加、删除或修改字段），就可能导致序列化和反序列化失败。这限制了跨语言和跨平台的兼容性。
> 3. **性能开销**：相对于 JSON 或其他基于文本的序列化格式，Java 序列化通常会产生更大的字节流，并且序列化和反序列化的过程涉及更多的计算和IO操作，可能导致性能开销较高。
> 4. **版本问题**：Java 序列化对类的版本敏感，如果序列化时的类版本与反序列化时的类版本不匹配，会导致反序列化失败。这就需要开发人员在类的演进过程中进行额外的管理和维护。

### SoftCache

软引用cache，GC后会回收这部分的cache。

很多场景下，我们的程序需要在一个对象被 GC 时得到通知，引用队列就是用于收集这些信息的队列。**在创建 SoftReference 对象 时，可以为其关联一个引用队列，当 SoftReference 所引用的对象被 GC 时， JVM 就会将该 SoftReference 对象 添加到与之关联的引用队列中**。当需要检测这些通知信息时，就可以从引用队列中获取这些 SoftReference 对象。不仅是 SoftReference，弱引用和虚引用都可以关联相应的队列。

**[bug]**

==这个hardLinksToAvoidGarbageCollection非常费解，会不会内存泄漏呀，同时get相同key时还能加入这个队列？==

这么多年如果没人用这个cache，同时hardLinksToAvoidGarbageCollection也没暴露出，MyBatis就不管了，哈哈哈。

```java
public class SoftCache implements Cache {
    // 强引用队列，FIFO保存不被GC的Object，目前MyBatis并没有将其暴露，所以里面保存的热点数据也是莫有用
    private final Deque<Object> hardLinksToAvoidGarbageCollection;
    // 关联弱引用队列，用于记录已经被 GC 的缓存项所对应的 SoftEntry对象
    private final ReferenceQueue<Object> queueOfGarbageCollectedEntries;
    private final Cache delegate;
    // 强引用队列大小
    private int numberOfHardLinks;


    @Override
    public void putObject(Object key, Object value) {
        removeGarbageCollectedItems();
        delegate.putObject(key, new SoftEntry(key, value, queueOfGarbageCollectedEntries));
    }

    @Override
    public Object getObject(Object key) {
        Object result = null;
        @SuppressWarnings("unchecked") // assumed delegate cache is totally managed by this cache
        SoftReference<Object> softReference = (SoftReference<Object>) delegate.getObject(key);
        if (softReference != null) {
            result = softReference.get();
            if (result == null) {
                delegate.removeObject(key);
            } else {
                // 如果弱引用不为空，则将该引用添加到强引用队列中
                // See #586 (and #335) modifications need more than a read lock
                synchronized (hardLinksToAvoidGarbageCollection) {
                    hardLinksToAvoidGarbageCollection.addFirst(result);
                    if (hardLinksToAvoidGarbageCollection.size() > numberOfHardLinks) {
                        hardLinksToAvoidGarbageCollection.removeLast();
                    }
                }
            }
        }
        return result;
    }
    
    @Override
    public Object removeObject(Object key) {
        removeGarbageCollectedItems();
        return delegate.removeObject(key);
    }

    @Override
    public void clear() {
        synchronized (hardLinksToAvoidGarbageCollection) {
            hardLinksToAvoidGarbageCollection.clear();
        }
        removeGarbageCollectedItems();
        delegate.clear();
    }

    private void removeGarbageCollectedItems() {
        SoftEntry sv;
        while ((sv = (SoftEntry) queueOfGarbageCollectedEntries.poll()) != null) {
            delegate.removeObject(sv.key);
        }
    }

    private static class SoftEntry extends SoftReference<Object> {
        private final Object key;

        SoftEntry(Object key, Object value, ReferenceQueue<Object> garbageCollectionQueue) {
            super(value, garbageCollectionQueue);
            this.key = key;
        }
    }
}
```

### 缓存构建

构建方法：`org.apache.ibatis.builder.MapperBuilderAssistant#useNewCache`

缓存构建过程在Mybatis初始化的流程来完成，xml启动关联`<cache>`标签，注解启动关联注解`@CacheNamespace`。

### 缓存机制

参考文档：https://tech.meituan.com/2018/01/19/mybatis-cache.html

Mybatis使用一二级缓存来减缓数据库访问的压力，通常情况下我们开发人员基本上都是使用Mybatis的默认配置，对于缓存来说也不例外。

Mybatis默认开启一级缓存，二级缓存的开启可看<缓存构建>章节。

> 缓存延申的疑问：
>
> Mybatis默认开启的缓存机制，会不会对GC造成压力和增加FULL GC的频次呢？
>
> 
>
> 一级缓存不会，因为会话结束之后缓存被清理了
>
> 二级缓存就难说了

#### 一级缓存

##### 原理

![image-20230617153651942](material/MyBatis/一级缓存原理图.png)

同一个会话中，有可能执行多次查询条件完全相同的SQL，MyBatis提供的一级缓存可以减少对数据库查询的次数。相同的SQL语句，会优先命中一级缓存。由此可知，一级缓存是Session级别的。

==缓存是Session级别的，意味者其他Session更新了数据，而本次的Session读取到脏数据。（尤其是我们的服务是分布式的时候）==

![](https://awps-assets.meituan.net/mit-x/blog-images-bundle-2018a/d76ec5fe.jpg)

每个SqlSession中持有了Executor，每个Executor中有一个LocalCache。当用户发起查询时，MyBatis根据当前执行的语句生成`MappedStatement`，在Local Cache进行查询，如果缓存命中的话，直接返回结果给用户，如果缓存没有命中的话，查询数据库，结果写入`Local Cache`，最后返回结果给用户。具体实现类的类关系图如上，伪代码如下。

当Executor进行提交commit或回滚rollback时，自动清空LocalCache，所以LocalCache属于Session级别。

```java
protected PerpetualCache localCache;

public <E> List<E> query(MappedStatement ms, Object parameter, RowBounds rowBounds, ResultHandler resultHandler, CacheKey key, BoundSql boundSql) throws SQLException {
  // ms非select语句都要刷新缓存
  if (ms.isFlushCacheRequired()) {
    clearLocalCache();
  }
  // 查缓存
  list = localCache.getObject(key);
  if (list != null) {
    // 这个主要是处理存储过程用的
    handleLocallyCachedOutputParameters(ms, key, parameter, boundSql);
  } else {
    // 缓存为空，查数据库并设置缓存
    list = queryFromDatabase(ms, parameter, rowBounds, resultHandler, key, boundSql);
    localCache.putObject(key, EXECUTION_PLACEHOLDER);
  }

  // STATEMENT级别，每次查询结束都清掉缓存
  if (configuration.getLocalCacheScope() == LocalCacheScope.STATEMENT) {
    clearLocalCache(); // issue #482
  }

  return list;
}
```

##### 配置与使用

Mybatis-config.xml配置如下，可选`SESSION`或者`STATEMENT`，默认是`SESSION`级别。

- SESSION：在一个MyBatis会话中执行的所有语句，都会共享这一个缓存
- STATEMENT：缓存只对当前执行的这一个`Statement`有效（每次查询结束清掉缓存）。

```xml
<!-- 注意这个是开启二级缓存, 一级缓存默认开启, 没有提供修改的入口 -->
<setting name="cacheEnabled" value="true"/>
<!-- 设置一级缓存的级别 -->
<setting name="localCacheScope" value="SESSION"/>
```

#### 二级缓存

##### 原理

二级缓存是用来解决不同的SqlSession之间需要共享缓存的问题。

开启二级缓存后，会使用CachingExecutor装饰Executor，进入一级缓存的查询流程前，现在CachingExecutor进行二级缓存的查询，如下图：

当开启缓存后，数据的查询执行的流程就是 二级缓存 -> 一级缓存 -> 数据库。



![image-20230617163254078](material/MyBatis/二级缓存原理图.png)

CachingExecutor二级缓存实现伪代码如下：

```java
public <E> List<E> query(MappedStatement ms, Object parameterObject, RowBounds rowBounds, ResultHandler resultHandler, CacheKey key, BoundSql boundSql) throws SQLException {
  // 从MappedStatement中获取缓存，同一个MappedStatement是同个缓存
  Cache cache = ms.getCache();
  if (cache != null) {
    flushCacheIfRequired(ms);
    if (ms.isUseCache() && resultHandler == null) { 
      ensureNoOutParams(ms, parameterObject, boundSql);
      // 如果没有脏数据，则从缓存中获取，加锁
      if (!dirty) {
        cache.getReadWriteLock().readLock().lock();
        try {
          @SuppressWarnings("unchecked")
          List<E> cachedList = (List<E>) cache.getObject(key);
          if (cachedList != null) return cachedList;
        } finally {
          cache.getReadWriteLock().readLock().unlock();
        }
      }
      // 二级缓存获取不到，就从simpleExecutor中执行（查询一级缓存或查数据库等）
      List<E> list = delegate.<E> query(ms, parameterObject, rowBounds, resultHandler, key, boundSql);
      // 提交缓存到tcm，注意tcm需要二阶段提交，并没有真正保存的cache中，等到tcm.commit/rollback时才会提交缓存
      tcm.putObject(cache, key, list); // issue #578. Query must be not synchronized to prevent deadlocks
      return list;
    }
  }
  return delegate.<E>query(ms, parameterObject, rowBounds, resultHandler, key, boundSql);
}
```

> 注意：
>
> 1. 二级缓存的cache保存在MappedStatement中，但是在同一个nameSpace下注入的cache都是同一个，这样达到cache在不同Session之间共享的目的。
> 2. 同时所有的cache都在Configuration中的cacheMap保存，key就是NameSpace，其中我们可以通过`<cache-ref>`标签来指定NameSpace，进而获取对应的cache。
> 3. 二级缓存，当Session提交之后才会put到二级缓存，否则缓存是空的。而一级缓存没有此限制。

##### 配置与使用

1. Mybatis-config.xml中配置	=> 	开启二级缓存

```xml
<setting name="cacheEnabled" value="true"/>
```

2. 在Mapper文件中配置cache或者cache-ref	=> 用于声明这个namespace使用二级缓存

```xml
<!-- 该NameSpace开启二级缓存 -->
<cache/>   
<!-- cache-ref代表引用别的命名空间的Cache配置，两个命名空间的操作使用的是同一个Cache。 -->
<cache-ref namespace="mapper.StudentMapper"/>
```

- cache-ref

  相当于多个NameSpace共用同一个缓存，缓存粒度更粗，意味这多个Mapper NameSpace下的所有操作都会对缓存使用造成影响。

# 核心处理层

## 动态SQL

MyBatis 在初始化过程中，会将 Mapper 映射文件中定义的 SQL 语句解析成 `SqlSource` 对象，其中动态标签、SQL 语句文本等，会被解析成对应类型的 `SqlNode` 对象。

我们知道仅用于一个个的 SqlNode 还不足以得到我们需要执行的 SQL 。真正执行的 SQL 是已经绑定用户参数的可执行的SQL 。这是需要将这些 SqlNode 组织起来并绑定参数的功能类。

而MyBatis中的`SqlSource`承担此功能（注意：这里的 SqlSource 只是拥有 SqlNode 的引用而已，真正触发动态 SQL 的拼接是`SqlSource.getBoundSql()` 的调用）。

### DynamicContext

MyBatis 解析 SQL 的链路很长，过程中需要将解析结果缓存，供上下文使用，承担该上下文的对象就是，`org.apache.ibatis.scripting.xmltags.DynamicContext`。

构建时机：MyBatis 启动过程中，在解析  Mapper 中 SQL 时构建，具体入口：`org.apache.ibatis.builder.xml.XMLMapperBuilder#buildStatementFromContext(java.util.List<org.apache.ibatis.parsing.XNode>)`

==**注：**==其中负责解析 SQL 语句地方：`org.apache.ibatis.builder.xml.XMLStatementBuilder#parseStatementNode`。如下可以看到解析完的 SQL 会返回一个 `SqlSource`。（各类的 SqlNode 也在这方法里面完成组装。）

```java
// Parse the SQL (pre: <selectKey> and <include> were parsed and removed)
SqlSource sqlSource = langDriver.createSqlSource(configuration, context, parameterTypeClass);
```

### SqlNode

```java
public interface SqlNode {
    // 传入DynamicContext，将解析的 SQL 片段用 context.sqlBuilder 拼接，
    // 全部的动态SQL片段都解析完成之后，就可以从DynamicContext.sqlBuilder字段中得到完整的SQL
    boolean apply(DynamicContext context);
}
```

- SqlNode 相关实现

![image-20230728171743461](material/MyBatis/SqlNode相关实现类.png)

- 组装 SqlNode 的入口

`org.apache.ibatis.scripting.xmltags.XMLScriptBuilder`

```java
public SqlSource createSqlSource(Configuration configuration, XNode script, Class<?> parameterType) {
  XMLScriptBuilder builder = new XMLScriptBuilder(configuration, script, parameterType);
  return builder.parseScriptNode();
}

public SqlSource parseScriptNode() {
  // 解析动态sql标签， 见下面的 NodeHandler
  List<SqlNode> contents = parseDynamicTags(context);
  // 拼接sqlNode
  MixedSqlNode rootSqlNode = new MixedSqlNode(contents);
  SqlSource sqlSource = null;
  if (isDynamic) {
    sqlSource = new DynamicSqlSource(configuration, rootSqlNode);
  } else {
    sqlSource = new RawSqlSource(configuration, rootSqlNode, parameterType);
  }
  return sqlSource;
}
```

#### 动态标签处理器NodeHandler

我们知道我们可以在 mapper.xml 定义一些动态标签，来达到执行动态 SQL 的目的，常用的动态标签如：`where | if | foreach | set` 等。每个标签都有不同的解析和拼接方式，MyBatis 是使用 NodeHandler 来完成这些动态 SQL 的拼接。

`org.apache.ibatis.scripting.xmltags.XMLScriptBuilder.NodeHandler` 

NodeHandler 是定义在 XMLScriptBuilder 中的私有接口，并维护解析不同动态标签的 Handler。

```java
private interface NodeHandler {
  void handleNode(XNode nodeToHandle, List<SqlNode> targetContents);
}

private Map<String, NodeHandler> nodeHandlers = new HashMap<String, NodeHandler>() {
  private static final long serialVersionUID = 7123056019193266281L;

  {
    put("trim", new TrimHandler());
    put("where", new WhereHandler());
    put("set", new SetHandler());
    put("foreach", new ForEachHandler());
    put("if", new IfHandler());
    put("choose", new ChooseHandler());
    put("when", new IfHandler());
    put("otherwise", new OtherwiseHandler());
    put("bind", new BindHandler());
  }
};
```

#### MixedSqlNode

**MixedSqlNode 在整个 SqlNode 树中充当了树枝节点，也就是扮演了组合模式中 Composite 的角色**，其中维护了一个 `List<SqlNode>` 集合用于记录 MixedSqlNode 下所有的子 SqlNode 对象。

`MixedSqlNode.apply()` ：核心逻辑就是遍历 `List<SqlNode>` 集合中全部的子 SqlNode 对象并调用 apply() 方法，由子 SqlNode 对象完成真正的动态 SQL 处理逻辑。

```java
public class MixedSqlNode implements SqlNode {
  private List<SqlNode> contents;

  public MixedSqlNode(List<SqlNode> contents) {
    this.contents = contents;
  }

  public boolean apply(DynamicContext context) {
    for (SqlNode sqlNode : contents) {
      sqlNode.apply(context);
    }
    return true;
  }
}
```

#### StaticTextSqlNode

StaticTextSqlNode 用来表示非动态 SQL 片段，成员变量只有 text，用来保存 SQL 的文本。

`StaticTextSqlNode.apply()` ：核心逻辑，使用 DynamicContext.sqlBuilder 来拼接 sql 文本片段。

```java
public class StaticTextSqlNode implements SqlNode {
  private String ;

  public StaticTextSqlNode(String text) {
    this.text = text;
  }

  public boolean apply(DynamicContext context) {
    context.appendSql(text);
    return true;
  }
}
```

#### TextSqlNode

TextSqlNode 用来解析包含 `${}` 占位符的动态 SQL 片段。成员变量 text 记录占位符的 SQL 文本内容，如：`AND note = ${note}`

`TextSqlNode.apply()` ：核心逻辑，使用**用户传入的实参**替换 text 中占位符 `${}`的内容（如：`AND note = 'param'`），并 DynamicContext.sqlBuilder 来拼接 sql 文本片段。

```java
public class TextSqlNode implements SqlNode {
  private String text;

  public boolean apply(DynamicContext context) {
    // 创建 ${} 的解析器
    GenericTokenParser parser = createParser(new BindingTokenParser(context));
    // 解析并替换 ${} 里面的内容
    context.appendSql(parser.parse(text));
    return true;
  }

  // 这里解析出来 ${} 里面填充的内容，此时发生在 SQL 调用执行过程中，
  // 故 context.getBindings().get("_parameter") 可以获取到调用的传参
  public String handleToken(String content) {
    Object parameter = context.getBindings().get("_parameter");
    if (parameter == null) {
      context.getBindings().put("value", null);
    } else if (SimpleTypeRegistry.isSimpleType(parameter.getClass())) {
      context.getBindings().put("value", parameter);
    }
    Object value = OgnlCache.getValue(content, context.getBindings());
    return (value == null ? "" : String.valueOf(value)); // issue #274 return "" instead of "null"
  }
}
```

#### IfSqlNode

IfSqlNode：用来解析`<if>`标签动态 SQL 片段。成员变量 test 记录 if 的判断条件，如：`note != null and note != ''`，条件的解析器为 evaluator。而 contents 这是链接到下一个的 SqlNode，因为当 if 条件满足时我们需要执行`<if>`标签中的内容，而 contents 则是标签内容的 SqlNode。

`IfSqlNode.apply()` ：核心逻辑，使用 evaluator 计算保存的 test 条件，true 则下一个节点的apply 方法 `contents.apply()`。

```java
public class IfSqlNode implements SqlNode {
  private ExpressionEvaluator evaluator;
  private String test;
  private SqlNode contents;

  public boolean apply(DynamicContext context) {
    if (evaluator.evaluateBoolean(test, context.getBindings())) {
      contents.apply(context);
      return true;
    }
    return false;
  }
}
```





### SqlSource

SqlSource 负责组装解析后的每个 sqlNode，以如下动态 SQL ，返回的 DynamicSqlSource 为例，展示 SqlSource 的数据结构。 

```xml
<select id="selectByRole" resultType="wenqitest.Role">
  select * from role
    <where>
      <if test="id != null">
        AND id = #{id,javaType=long}
      </if>
      <if test="roleName != null and roleName != ''">
        AND role_name like concat('%', #{roleName,jdbcType=VARCHAR}, '%')
      </if>
      <if test="note != null and note != ''">
        AND note = #{note,jdbcType=VARCHAR}
      </if>
    </where>
</select>
```

![image-20230730152824128](material/MyBatis/SqlSource数据结构.png)

等到执行过程中调用`SqlSource.getBoundSql()`，才会触发动态 SQL 的拼接，从节点 MixedSqlNode 开始，逐一执行 `SqlNode.apply()` 完成整个 SQL 的拼接，调用栈如下：

![image-20230730171052424](material/MyBatis/动态SQL调用栈.png)















































