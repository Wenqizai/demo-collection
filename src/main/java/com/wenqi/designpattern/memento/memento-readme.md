## 备忘录模式

备忘录模式：Memento Pattern

> what

一种行为设计模式，运行在不暴露对象实现细节的情况下**保存**和**恢复**对象之前的状态。

> 实现

使用一个容器来保存对象的状态，基于内存考虑，保存的对象可使用原型模式和对象系列化操作。

向外提供索引，获取保存对象的快照，并恢复至原来状态。



如下图：

​	我们使用对象是 Originator，当需要备忘生成快照时，就将 Originator 的属性赋值到 Memento。而 Caretaker 负责保存和管理这些 Memento。当需要获取某个快照时，Caretaker 需要将 Memento 的属性重新赋值给 Originator。

![](https://s2.loli.net/2023/09/03/gjMXJ7AKBNwHf3T.png)

> 应用场景

1. 当需要创建对象状态快照来恢复其之前的状态；
1. 备份、撤销、恢复

> 考虑点

1. 备份消耗的资源，内存、磁盘、CPU等；
2. 全量备份改为增量备份，低频全量备份，高频增量备份；
3. 类比 MySQL，binlog 全量备份，redo log 增量备份。

