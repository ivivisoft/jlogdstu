### 背景介绍
log4j2与log4j1发生了很大的变化，不兼容。log4j1仅仅作为一个实际的日志框架，slf4j、commons-logging作为门面，统一各种日志框架的混乱格局，现在log4j2也想跳出来充当门面了，也想统一大家了。哎，日志格局越来越混乱了。
 
 log4j2分成2个部分：
 + log4j-api： 作为日志接口层，用于统一底层日志系统
 + log4j-core : 作为上述日志接口的实现，是一个实际的日志框架

### 使用方法
1. maven依赖
```xml
<dependency>
    <groupId>org.apache.logging.log4j</groupId>
    <artifactId>log4j-api</artifactId>
    <version>2.2</version>
</dependency>
<dependency>
    <groupId>org.apache.logging.log4j</groupId>
    <artifactId>log4j-core</artifactId>
    <version>2.2</version>
</dependency>
```

2. 编写配置文件

编写log4j2.xml配置文件（目前log4j2只支持xml json yuml，不再支持properties文件）

3. 代码调用@see Log4j2Test

### 源码简析
1. 获取底层使用的LoggerContextFactory：
同样LogManager的类加载会去寻找log4j-api定义的LoggerContextFactory接口的底层实现，获取方式有三种：
+ 第一种： 尝试从jar中寻找log4j2.component.properties文件，如果配置了log4j2.loggerContextFactory则使用该LoggerContextFactory
+ 第二种：如果没找到，尝试从jar包中寻找META-INF/log4j-provider.properties文件，如log4j-core-2.2中就有该文件，如下图所示：
如果找到多个，取优先级最高的（该文件中指定了LoggerContextFactory，同时指定了优先级FactoryPriority），如log4j-core-2.2中log4j-provider.properties的文件内容如下：
```properties (type)
LoggerContextFactory = org.apache.logging.log4j.core.impl.Log4jContextFactory
Log4jAPIVersion = 2.1.0
FactoryPriority= 10
```
+ 第三种情况：上述方式还没找到，就使用默认的SimpleLoggerContextFactory

2. 使用LoggerContextFactory获取LoggerContext
3. 根据LoggerContext获取Logger
以log4j-core为例：
+ 会首先判断LoggerContext是否被初始化过了，没有则进行初始化
+ 获取ConfigurationFactory,从配置中获取和插件中获取（log4j-core核心包中有三个YamlConfigurationFactory、JsonConfigurationFactory、XmlConfigurationFactory）
+ 以上文的案例中，会使用XmlConfigurationFactory来加载log4j2.xml配置文件
+ LoggerContext初始化后，就可以获取或者创建Logger了





