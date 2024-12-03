SPI：Service Provider Interface，服务提供者接口。SPI 是一种服务发现机制，通过定义配置接口名文件，写入全限定类名实现类，并由服务加载器读取配置文件，动态加载实现类。这种方式便于第三方实现或扩展 API 接口，使用者也更加便捷地实现框架组件的替换。

![image-20241103234137320](C:/@D/-Development/Study/Codes/demo/demo-collection/src/main/java/com/wenqi/designpattern/spi/resources/img.png)

# 设计思想

> SPI 和 API 

两者都是基于接口，而不是基于实现的设计。

**API**

我们大多数使用的 API 接口交互，一般都是被调用方定义接口和实现类，调用方调用接口来实现对应的功能。也就是客户端和服务端调用关系。服务端定义和实现接口，对外提供 API 接口，由客户端接入实现。

一些常用 API 接口实践：

- 前端/客户端调用后端/服务端；
- service A 通过 feign / http 请求调用 service B。

**SPI**

SPI 与 API 的规范有点相反，SPI 是调用方定义接口，由被调用方实现该接口。调用方通过配置或指定方式来选择某一个被调用方，也就是服务商来实现接口功能。

一些常见的 SPI 接口实践：

- 调用方和实现接口的服务商；
- JDK 定义了接口 `java.sql.Driver`，由 MySQL、Oracle、H2 等数据库服务商提供实现；
- Slf4j 定义日志接口，由不同的日志框架提供实现；
- SPI 思想设计的 Spring Boot spring.factories机制。 

API 和 SPI 的区别是，接口是哪一方定义，实现类是哪一方实现。如果作为一个服务提供方，不希望使用方修改源码的情况下，处理通过 HTTP 请求的方式，SPI 也是一个不错的选择。需要注意的时，SPI 需要使用方引用对应的 jar。

> Spring SPI 和 JDK 原生 SPI

1. JDK 原生的 SPI，每次通过 `ServiceLoader` 加载时都会初始化一个新的实例，没有实现类的缓存，也没有考虑单例；
2. Spring SPI 配置目录是 `META-INF/spring.factories` ，与 IOC 容器集成，实例化 Bean 可以实现依赖注入；
3. Spring 提供条件匹配机制，可以按需加载特定的 SPI 实现。

参看文档：https://cloud.tencent.com/developer/article/2328627

> 总结

1. 解耦，核心代码不依赖外部具体实现；
2. 动态加载；
3. 为接入不同服务提供者，提供更好的扩展性；
4. 符合开闭原则，添加新功能，新特性时，核心代码不变。

# 使用方法

1. 确定需要调用接口：`com.wenqi.designpattern.spi.demo01.Registry`；
2. 实现类：
   - `com.wenqi.designpattern.spi.demo01.ZookeeperRegistry`
   - `com.wenqi.designpattern.spi.demo01.EtcdRegistry`
3. 在 `src/main/resources/META-INF/services`  目录下新建文件，文件命名为调用的接口全类路径：`com.wenqi.designpattern.spi.demo01.Registry`
4. 在文件中定义实现类的全类路径：

```
com.wenqi.designpattern.spi.demo01.EtcdRegistry
com.wenqi.designpattern.spi.demo01.ZookeeperRegistry
```

5. 利用 JDK 内置服务类 `ServiceLoader`，加载定义的实现类，并调用实现方法。

# 实现原理

参考：

https://dongzl.github.io/2021/01/16/04-Java-Service-Provider-Interface/index.html

https://cloud.tencent.com/developer/article/1830990



