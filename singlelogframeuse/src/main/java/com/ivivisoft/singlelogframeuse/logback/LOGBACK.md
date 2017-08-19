### maven依赖
```xml
<dependency> 
    <groupId>ch.qos.logback</groupId> 
    <artifactId>logback-core</artifactId> 
    <version>1.1.3</version> 
</dependency> 
<dependency> 
    <groupId>ch.qos.logback</groupId> 
    <artifactId>logback-classic</artifactId> 
    <version>1.1.3</version> 
</dependency>
<dependency>
    <groupId>org.slf4j</groupId>
    <artifactId>slf4j-api</artifactId>
    <version>1.7.12</version>
</dependency>
```

### 代码使用@see LogbackTest
+ 官方使用方式，其实就和slf4j集成了起来LogbackTest的Logger、LoggerFactory都是slf4j自己的接口与类.
+ 没有配置文件的情况下，使用的是默认配置。搜寻配置文件的过程如下：

```
1. Logback tries to find a file called logback.groovy in the classpath.

2. If no such file is found, logback tries to find a file called logback-test.xml in the classpath.

3. If no such file is found, it checks for the file logback.xml in the classpath..

4. If no such file is found, and the executing JVM has the ServiceLoader (JDK 6 and above) the ServiceLoader will be used to resolve an implementation of com.qos.logback.classic.spi.Configurator. The first implementation found will be used. See ServiceLoader documentation for more details.

5. If none of the above succeeds, logback configures itself automatically using the BasicConfigurator which will cause logging output to be directed to the console.

The fourth and last step is meant to provide a default (but very basic) logging functionality in the absence of a configuration file.
```

### 源码简析
1. slf4j与底层的日志系统进行绑定在jar包中寻找org/slf4j/impl/StaticLoggerBinder.class 这个类，如在logback-classic中就含有这个类,
如果找到多个StaticLoggerBinder，则表明目前底层有多个实际的日志框架，slf4j会随机选择一个.
2. 使用上述找到的StaticLoggerBinder创建一个实例，并返回一个ILoggerFactory实例： 

        return StaticLoggerBinder.getSingleton().getLoggerFactory()；
   以logback-classic中的StaticLoggerBinder为例，在StaticLoggerBinder.getSingleton()过程中：会去加载解析配置文件 源码如下：
   
```java (type)
public URL findURLOfDefaultConfigurationFile(boolean updateStatus) {

   ClassLoader myClassLoader = Loader.getClassLoaderOfObject(this);
   //寻找logback.configurationFile的系统属性
   URL url = findConfigFileURLFromSystemProperties(myClassLoader, updateStatus);
   if (url != null) {
     return url;
   }
   //寻找logback.groovy
   url = getResource(GROOVY_AUTOCONFIG_FILE, myClassLoader, updateStatus);
   if (url != null) {
     return url;
   }
   //寻找logback-test.xml
   url = getResource(TEST_AUTOCONFIG_FILE, myClassLoader, updateStatus);
   if (url != null) {
     return url;
   }
   //寻找logback.xml
   return getResource(AUTOCONFIG_FILE, myClassLoader, updateStatus);
}
```

目前路径都是定死的，只有logback.configurationFile的系统属性是可以更改的，所以如果我们想更改配置文件的位置（不想放在类路径下），则需要设置这个系统属性：

    System.setProperty("logback.configurationFile", "/path/to/config.xml");
解析完配置文件后，返回的ILoggerFactory实例的类型是LoggerContext（它包含了配置信息）
3. 根据返回的ILoggerFactory实例，来获取Logger,就是根据上述的LoggerContext来创建一个Logger，每个logger与LoggerContext建立了关系，并放到LoggerContext的缓存中，就是LoggerContext的如下属性：

        private Map<String, Logger> loggerCache;
        
其实上述过程就是slf4j与其他日志系统的绑定过程。不同的日志系统与slf4j集成，都会有一个StaticLoggerBinder类，并会拥有一个ILoggerFactory的实现。
