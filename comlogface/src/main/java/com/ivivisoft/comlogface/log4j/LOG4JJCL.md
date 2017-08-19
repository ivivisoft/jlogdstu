### 使用方法
1. maven依赖
```xml (type)
<dependency>
    <groupId>commons-logging</groupId>
    <artifactId>commons-logging</artifactId>
    <version>1.2</version>
</dependency>
<dependency>
    <groupId>log4j</groupId>
    <artifactId>log4j</artifactId>
    <version>1.2.17</version>
</dependency>
```
2. 在类路径下加入log4j的配置文件log4j.properties
3. 使用方式见Log4jJclTest

   代码没变，还是使用commons-logging的接口和类来编程，没有log4j的任何影子。这样，commons-logging就与log4j集成了起来，我们可以通过log4j的配置文件来控制日志的显示级别上述是trace级别(小于debug)，所以trace、debug、info的都会显示出来

### 使用案例分析

案例过程分析，就是看看上述commons-logging的在执行原理的过程中是如何来走的:
1. 获取获取LogFactory的过程

   同上述jcl的过程一样，使用默认的LogFactoryImpl作为LogFactory

2. 根据LogFactory获取Log的过程

   同上述jcl的过程一样，最终会依次根据classesToDiscover中的类名称进行创建：

+ 先是创建org.apache.commons.logging.impl.Log4JLogger

+ 创建成功，因为此时含有log4j的jar包，所以返回的是Log4JLogger，我们看下它与commons-logging是如何集成的：
它内部有一个org.apache.log4j.Logger logger属性，这个是log4j的原生Logger。所以Log4JLogger都是委托这个logger来完成的

+ org.apache.log4j.Logger logger来历

        org.apache.log4j.Logger.getLogger(name)

  使用原生的log4j1的写法来生成，参见之前log4j原生的写法log4j1原生的写法，我们知道上述过程会引发log4j1的配置文件的加载，之后就进入log4j1的世界了

+ 输出日志

  测试案例中我们使用commons-logging输出的日志的形式如下（这里的logger是org.apache.commons.logging.impl.Log4JLogger类型）：

        logger.debug("commons-logging-log4j debug message");

  其实就会转换成log4j原生的org.apache.log4j.Logger对象（就是上述获取的org.apache.log4j.Logger类型的logger对象）的如下输出：

        logger.debug("log4j debug message");

