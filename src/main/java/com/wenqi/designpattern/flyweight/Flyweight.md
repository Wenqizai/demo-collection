## 享元模式

享元，即共享的单元，主要是利用复用的对象，节省内存。

==注意：== 共享的对象一定要不可变的对象，意味着构造完成实例化之后，对象或对象的属性不可被修改（不暴露setter等修改属性的方法）。

- 应用

享元模式常用在：单例、缓存和对象池，参考Integer和String的应用。

- 实现

享元模式的代码实现非常简单，主要是通过工厂模式，在工厂类中，通过一个Map或者List来缓存已经创建好的享元对象，以达到复用的目的。