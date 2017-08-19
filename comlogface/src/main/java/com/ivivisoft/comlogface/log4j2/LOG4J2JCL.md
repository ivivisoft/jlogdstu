### 使用方法
1. maven依赖
```xml (type)
<dependency>
    <groupId>commons-logging</groupId>
    <artifactId>commons-logging</artifactId>
    <version>1.2</version>
</dependency>
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
<dependency>
    <groupId>org.apache.logging.log4j</groupId>
    <artifactId>log4j-jcl</artifactId>
    <version>2.2</version>
</dependency>
```

+ commons-logging
+ log4j-api （log4j2的API包）
+ log4j-core （log4j2的API实现包）
+ log4j-jcl （log4j2与commons-logging的集成包）

2. 编写log4j2的配置文件log4j2.xml
3. 代码调用@see Log4j2JclTest

### 
使用案例分析

案例过程分析，就是看看上述commons-logging的在执行原理的过程中是如何来走的:

1. 先来看下上述 log4j-jcl（log4j2与commons-logging的集成包）的来历：

   我们知道，commons-logging原始的jar包中使用了默认的LogFactoryImpl作为LogFactory，该默认的LogFactoryImpl中的classesToDiscover（到上面查看它的内容）并没有log4j2对应的Log实现类。所以我们就不能使用这个原始包中默认的LogFactoryImpl了，需要重新指定一个，并且需要给出一个apache的Log实现（该Log实现是用于log4j2的），所以就产生了log4j-jcl这个jar包，
log4j2与commons-logging的集成包
这里面的LogFactoryImpl就是要准备替换commons-logging中默认的LogFactoryImpl（其中META-INF/services/下的那个文件起到重要的替换作用，下面详细说）
这里面的Log4jLog便是针对log4j2的，而commons-logging中的原始的Log4JLogger则是针对log4j1的。它们都是commons-logging的Log接口的实现

2. 获取获取LogFactory的过程

   这个过程就和jul、log4j1的集成过程不太一样了。通过java的SPI机制，找到了org.apache.commons.logging.LogFactory对应的实现，即在log4j-jcl包中找到的，其中META-INF/services/org.apache.commons.logging.LogFactory中的内容是：

        org.apache.logging.log4j.jcl.LogFactoryImpl

   即指明了使用log4j-jcl中的LogFactoryImpl作为LogFactory

3. 根据LogFactory获取Log的过程

   就来看下log4j-jcl中的LogFactoryImpl是怎么实现的
```java
public class LogFactoryImpl extends LogFactory {

   private final LoggerAdapter<Log> adapter = new LogAdapter();
   //略
}
```

这个LoggerAdapter是lo4j2中的一个适配器接口类，根据log4j2生产的原生的org.apache.logging.log4j.Logger实例，将它包装成你指定的泛型类。

这里使用的LoggerAdapter实现是LogAdapter，它的内容如下：
```java
public class LogAdapter extends AbstractLoggerAdapter<Log> {

   @Override
   protected Log newLogger(final String name, final LoggerContext context) {
       return new Log4jLog(context.getLogger(name));
   }
   @Override
   protected LoggerContext getContext() {
       return getContext(ReflectionUtil.getCallerClass(LogFactory.class));
   }
}
```
 我们可以看到，它其实就是将原生的log4j2的Logger封装成Log4jLog。这里就可以看明白了，下面来详细的走下流程，看看是什么时候来初始化log4j2的：

+ 首先获取log4j2中的重要配置对象LoggerContext，LogAdapter的实现如上面的源码（使用父类的getContext方法），父类方法的内容如下：

        LogManager.getContext(cl, false);

  我们可以看到这其实就是使用log4j2的LogManager进行初始化的，至此就进入log4j2的初始化的世界了。

+ log4j2的LoggerContext初始化完成后，该生产一个log4j2原生的Logger对象
 
  使用log4j2原生的方式：

        context.getLogger(name)

+ 将上述方式产生的Log4j原生的Logger实例进行包装，包装成Log4jLog

        new Log4jLog(context.getLogger(name));

  至此，我们通过Log4jLog实例打印的日志都是委托给了它内部包含的log4j2的原生Logger对象了。


