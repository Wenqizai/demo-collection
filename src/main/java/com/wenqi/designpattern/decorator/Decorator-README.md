## 装饰器模式

属于：结构型设计模式

装饰器模式，封装包含与目标对象相同的一系列方法，它会建所有接收到的请求委派给目标对象。由于内部组合了目标对象，那么可以在 目标方法执行前后做一些增强方法处理。

善于组合代替继承，解决多继承的利器。

## Java中应用

Java 核心程序库中有一些关于装饰的示例：

- [`java.io.InputStream`](http://docs.oracle.com/javase/8/docs/api/java/io/InputStream.html)、 [`Output­Stream`](http://docs.oracle.com/javase/8/docs/api/java/io/OutputStream.html)、 [`Reader`](http://docs.oracle.com/javase/8/docs/api/java/io/Reader.html) 和 [`Writer`](http://docs.oracle.com/javase/8/docs/api/java/io/Writer.html) 的所有代码都有以自身类型的对象作为参数的构造函数。
- [`java.util.Collections`](http://docs.oracle.com/javase/8/docs/api/java/util/Collections.html)、 [`checked­XXX()`](http://docs.oracle.com/javase/8/docs/api/java/util/Collections.html#checkedCollection-java.util.Collection-java.lang.Class-)、 [`synchronized­XXX()`](http://docs.oracle.com/javase/8/docs/api/java/util/Collections.html#synchronizedCollection-java.util.Collection-) 和 [`unmodifiable­XXX()`](http://docs.oracle.com/javase/8/docs/api/java/util/Collections.html#unmodifiableCollection-java.util.Collection-) 方法。
- [`javax.servlet.http.HttpServletRequestWrapper`](http://docs.oracle.com/javaee/7/api/javax/servlet/http/HttpServletRequestWrapper.html) 和 [`Http­Servlet­Response­Wrapper`](http://docs.oracle.com/javaee/7/api/javax/servlet/http/HttpServletResponseWrapper.html)

## 场景

编码和压缩装饰

> 场景1

- 当数据即将被**写入磁盘**前， 装饰对数据进行加密和压缩。 在原始类对改变毫无察觉的情况下， 将加密后的受保护数据写入文件。
- 当数据刚**从磁盘读出**后， 同样通过装饰对数据进行解压和解密。