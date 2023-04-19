# 概述

官方文档：https://mybatis.org/mybatis-3/zh/getting-started.html

MyBatis都是基于`SqlSessionFactory`实例为核心。`SqlSessionFactory`的实例可以通过 `SqlSessionFactoryBuilder` 获得。而 `SqlSessionFactoryBuilder` 则可以从 XML 配置文件或一个预先配置的`Configuration`实例来构建出`SqlSessionFactory`实例。

> 一些注意点

1. SqlSessionFactoryBuilder仅用来创建SqlSessionFactory，创建完毕可以销毁；
2. SqlSession线程不安全，注意共享session问题，最佳实践每个方法请求时开启一个SqlSession，方法结束就关闭；
3. 每个数据库对应一个 SqlSessionFactory 实例；

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













