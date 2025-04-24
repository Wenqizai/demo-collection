# demo-collection

## 1. 项目架构概览

这是一个Java学习示例项目集合，基于Spring Boot 2.5.3框架构建，整合了多种技术demo和设计模式实现。
主要包含以下几个方面的内容：
- Spring Boot应用示例
- 各种Java API和工具类的使用示例
- 设计模式的实现
- 技术组件的集成示例（如ElasticSearch、MyBatis等）
- 多线程和高级Java特性的示例

## 2. 主要目录结构及其职责

```
src/main/java/com/wenqi/
├── Main.java                       # 主入口类
├── book/                           # 书籍相关的代码示例
├── designpattern/                  # 设计模式实现
│   ├── PatternApplication.java     # 设计模式应用入口
│   ├── adapter/                    # 适配器模式
│   ├── bridge/                     # 桥接模式
│   ├── command/                    # 命令模式
│   ├── decorator/                  # 装饰器模式
│   ├── facade/                     # 外观模式
│   ├── filter/                     # 过滤器模式
│   ├── flyweight/                  # 享元模式
│   ├── memento/                    # 备忘录模式
│   ├── observer/                   # 观察者模式
│   ├── prototype/                  # 原型模式
│   ├── spi/                        # SPI机制示例
│   ├── state/                      # 状态模式
│   ├── strategy/                   # 策略模式
│   ├── template/                   # 模板方法模式
│   └── theory/                     # 设计模式理论
├── example/                        # Java API使用示例
│   ├── algro/                      # 算法示例
│   ├── array/                      # 数组操作示例
│   ├── b_instanceof_a/             # instanceof示例
│   ├── collection/                 # 集合操作示例
│   ├── date/                       # 日期操作示例
│   ├── function/                   # 函数式编程示例
│   ├── json/                       # JSON操作示例
│   ├── keygen/                     # 密钥生成示例
│   ├── number/                     # 数值操作示例
│   ├── optional/                   # Optional使用示例
│   ├── primitive/                  # 基本类型示例
│   ├── reflect/                    # 反射API示例
│   ├── stream/                     # Stream API示例
│   ├── string/                     # 字符串操作示例
│   ├── threadlocal/                # ThreadLocal使用示例
│   └── time/                       # 时间操作示例
├── springboot/                     # Spring Boot相关示例
│   ├── Application.java            # Spring Boot应用入口
│   ├── HelloWorldController.java   # 简单控制器示例
│   ├── center/                     # 中心模块
│   ├── config/                     # 配置类
│   ├── controller/                 # Web控制器
│   ├── elasticsearch/              # ElasticSearch集成
│   ├── exception/                  # 异常处理
│   ├── mapper/                     # MyBatis映射
│   ├── mybatisplus/                # MyBatis-Plus集成
│   └── pojo/                       # 数据对象
└── tech/                           # 技术组件示例
    ├── compress/                   # 压缩工具
    ├── excel/                      # Excel操作示例
    ├── log4j/                      # Log4j配置示例
    ├── thread/                     # 多线程示例
    └── timewheel/                  # 时间轮实现

src/main/resources/
├── application.properties          # 应用配置文件
├── log4j2.xml                      # Log4j2配置文件
├── mapper/                         # MyBatis映射XML文件
├── mybatis/                        # MyBatis配置
└── mybatisx_template/              # MyBatisX模板
```

## 3. 关键模块的依赖关系图

```
Main
 ↓
SpringBoot Application
 ↓
 ├── Controllers ← Services ← Mappers ← POJO
 ├── Config
 └── Exception Handlers
```

## 4. 核心类和接口的功能说明

- `com.wenqi.springboot.Application`: Spring Boot应用的入口类，启动Web服务
- `com.wenqi.springboot.HelloWorldController`: 示例REST控制器，提供基本HTTP接口
- `com.wenqi.designpattern.PatternApplication`: 设计模式示例的入口类
- 各种设计模式实现类: 提供了各种设计模式的标准实现示例
- 示例类: 在`com.wenqi.example`包下，每个子包都提供了特定Java API的使用示例

## 5. 数据流向图

REST请求 → Controller → Service → Mapper → 数据库
                      ↑
                      └── POJO/DTO

## 6. API接口清单

- GET `/hello/world`: 基本GET请求示例，返回Hello World
- GET `/hello/world2`: 延迟响应示例（2秒延迟）
- POST `/hello/post/world`: 基本POST请求示例，接收JSON数据
- POST `/hello/world/msg`: 消息处理示例，打印接收到的消息

## 7. 常见的代码模式和约定

- 所有控制器使用`@RestController`注解，提供REST API
- 设计模式遵循标准的实现方式，每个模式都有独立的包
- 使用`log4j2`进行日志记录
- 使用`MyBatis`进行数据库访问
- 所有示例代码都组织在特定功能的包下，方便查找和理解