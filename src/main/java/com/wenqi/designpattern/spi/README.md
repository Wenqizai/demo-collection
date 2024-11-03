SPI：Service Provider Interface，服务提供者接口。

# 使用方法

1. 确定需要调用接口：`com.wenqi.designpattern.spi.demo01.Registry`；
2. 实现类：
   - `com.wenqi.designpattern.spi.demo01.ZookeeperRegistry`
   - `com.wenqi.designpattern.spi.demo01.EtcdRegistry`
3. 在 `resources/META-INF/services` 目录下新建文件，文件命名为调用的接口全类路径：`com.wenqi.designpattern.spi.demo01.Registry`
4. 在文件中定义实现类的全类路径：

```
com.wenqi.designpattern.spi.demo01.EtcdRegistry
com.wenqi.designpattern.spi.demo01.ZookeeperRegistry
```

5. 利用 JDK 内置服务类 `ServiceLoader`，加载定义的实现类，并调用实现方法。