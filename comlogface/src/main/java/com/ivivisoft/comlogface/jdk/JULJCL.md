### commons-logging与jul(java util logging)集成
1. 添加maven依赖
```xml
<dependency>
    <groupId>commons-logging</groupId>
    <artifactId>commons-logging</artifactId>
    <version>1.2</version>
</dependency>
```
2. 使用案例见JulJclTest.java

#### 使用案例分析
案例过程分析，就是看看上述commons-logging的在执行原理的过程中是如何来走的

1. 获取获取LogFactory的过程

+ 我们没有配置系统属性"org.apache.commons.logging.LogFactory"
+ 我们没有配置commons-logging的commons-logging.properties配置文件
+ 也没有含有"META-INF/services/org.apache.commons.logging.LogFactory"路径的jar包
所以commons-logging会使用默认的LogFactoryImpl作为LogFactory

2. 根据LogFactory获取Log的过程

+ 我们没有配置commons-logging的commons-logging.properties配置文件
+ 我们没有配置系统属性"org.apache.commons.logging.Log"
所以就需要依次根据classesToDiscover中的类名称进行创建。
+ 先是创建org.apache.commons.logging.impl.Log4JLogger
创建失败，因为该类是依赖org.apache.log4j包中的类的

+ 接着创建org.apache.commons.logging.impl.Jdk14Logger
创建成功，所以我们返回的就是Jdk14Logger，看下它是如何与jul集成的
它内部有一个java.util.logging.Logger logger属性，所以Jdk14Logger的info("commons-logging-jcl info message")操作都会转化成由java.util.logging.Logger来实现：
上述logger的来历：
logger = java.util.logging.Logger.getLogger(name);
就是使用jul原生的方式创建的一个java.util.logging.Logger，参见jdk-logging的原生写法
是如何打印info信息的呢？
使用jul原生的方式：
logger.log(Level.WARNING,"commons-logging-jcl info message");
由于jul默认的级别是INFO级别,所以只打出了INFO日志,原生的jdk的logging的日志级别是FINEST、FINE、INFO、WARNING、SEVERE分别对应我们常见的trace、debug、info、warn、error。
