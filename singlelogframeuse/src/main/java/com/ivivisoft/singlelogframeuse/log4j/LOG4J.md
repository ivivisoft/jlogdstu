### 使用
1. maven依赖
```xml
<dependency>
    <groupId>log4j</groupId>
    <artifactId>log4j</artifactId>
    <version>1.2.17</version>
</dependency>
```

2. 编写log4j.properties配置文件
```properties (type)
log4j.rootLogger = debug, console
log4j.appender.console = org.apache.log4j.ConsoleAppender
log4j.appender.console.layout = org.apache.log4j.PatternLayout
log4j.appender.console.layout.ConversionPattern = %-d{yyyy-MM-dd HH:mm:ss} %m%n
```
3. 代码使用:@see Log4jTest
4. 注意:
        
        默认到类路径下加载log4j.properties配置文件，如果log4j.properties配置文件不在类路径下，则可以选择如下方式之一来加载配置文件  
        使用classLoader来加载资源       
        PropertyConfigurator.configure(Log4jTest.class.getClassLoader().getResource("log4j/log4j.properties"));    
        使用log4j自带的Loader来加载资源   
        PropertyConfigurator.configure(Loader.getResource("log4j/log4j.properties"));
        
### 获取Logger源码简析
#### 第一种情况:没有指定配置文件路径
1. 第一步： 引发LogManager的类初始化
```java (type)
static public Logger getLogger(Class clazz) {
  return LogManager.getLogger(clazz.getName());
}
```
2. 第二步：初始化一个logger仓库Hierarchy
Hierarchy的源码如下：
```java
public class Hierarchy implements LoggerRepository, RendererSupport, ThrowableRendererSupport {

    private LoggerFactory defaultFactory;
    Hashtable ht;
    Logger root;
  //其他略
}
```
+ LoggerFactory defaultFactory： 就是创建Logger的工厂
+ Hashtable ht：用来存放上述工厂创建的Logger
+ Logger root:作为根Logger

LogManager在类初始化的时候如下方式来实例化Hierarchy：
```java (type)
static {
  Hierarchy h = new Hierarchy(new RootLogger((Level) Level.DEBUG));
  //略
}
```
new RootLogger作为root logger，默认是debug级别
最后把Hierarchy绑定到LogManager上，可以在任何地方来获取这个logger仓库Hierarchy

3. 第三步：在LogManager的类初始化的过程中默认寻找类路径下的配置文件
   
   通过org.apache.log4j.helpers.Loader类来加载类路径下的配置文件：
```java (type)
Loader.getResource("log4j.xml");
Loader.getResource("log4j.properties")
```
优先选择xml配置文件

4. 第四步：解析上述配置文件
+ 如果是xml文件则org.apache.log4j.xml.DOMConfigurator类来解析
+ 如果是properties文件，则使用org.apache.log4j.PropertyConfigurator来解析

解析后的结果：
+ 设置RootLogger的级别
+ 对RootLogger添加一系列我们配置的appender（我们通过logger来输出日志，通过logger中的appender指明了日志的输出目的地）

5. 第五步：当一切都准备妥当后，就该获取Logger了
使用logger仓库Hierarchy中内置的LoggerFactory工厂来创建Logger了，并缓存起来，同时将logger仓库Hierarchy设置进新创建的Logger中.

#### 第二种情况，手动来加载不在类路径下的配置文件
PropertyConfigurator.configure 执行时会去进行上述的配置文件解析，源码如下：
```java (type)
public static void configure(java.net.URL configURL) {
 new PropertyConfigurator().doConfigure(configURL,
                    LogManager.getLoggerRepository());
}
```
+ 仍然先会引发LogManager的类加载，创建出logger仓库Hierarchy，同时尝试加载类路径下的配置文件，此时没有则不进行解析，此时logger仓库Hierarchy中的RootLogger默认采用debug级别，没有appender而已。
+ 然后解析配置文件，对上述logger仓库Hierarchy的RootLogger进行级别的设置，添加appender
+ 此时再去调用Logger.getLogger，不会导致LogManager的类初始化（因为已经加载过了）

#### 第三种情况，配置文件在类路径下，而我们又手动使用PropertyConfigurator去加载
也就会造成2次加载解析配置文件，仅仅会造成覆盖而已（对于RootLogger进行从新设置级别，删除原有的appender，重新加载新的appender），所以多次加载解析配置文件以最后一次为准。

### 简单总结
+ LogManager： 它的类加载会创建logger仓库Hierarchy，并尝试寻找类路径下的配置文件，如果有则解析
+ Hierarchy ： 包含三个重要属性：

        LoggerFactory logger的创建工厂
        Hashtable 用于存放上述工厂创建的logger
        Logger root logger,用于承载解析文件的结果，设置级别，同时存放appender

+ PropertyConfigurator: 用于解析log4j.properties文件
+ Logger : 我们用来输出日志的对象
        

