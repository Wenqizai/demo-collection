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
                // 非继承Oject类的方法
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

### MapperMethod

由上述可知对于调用方法是默认方法（**public non-abstract declared in an interface，即公共非抽象的方法，且声明在接口中**），MyBatis会采用MethodHandle的方式进行代理调用；与之相反，MyBatis就采用MapperMethod方式来调用。

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

































































