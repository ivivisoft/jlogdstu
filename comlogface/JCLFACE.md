### apache commons-logging原理简析
LogFactory.getLog(JulJclTest.class)的源码如下:
```java (type)
public static Log getLog(Class clazz) throws LogConfigurationException {
    return getFactory().getInstance(clazz);
}
```
上述获取Log的过程大致分成2个阶段
+ 获取LogFactory的过程 (从字面上理解就是生产Log的工厂)
+ 根据LogFactory获取Log的过程

commons-logging默认提供的LogFactory实现：LogFactoryImpl commons-logging默认提供的Log实现：Jdk14Logger、Log4JLogger、SimpleLog。

1. 获取LogFactory的过程

从下面几种途径来获取LogFactory:
+ 系统属性中获取，即如下形式
  System.getProperty("org.apache.commons.logging.LogFactory")
+ 使用java的SPI机制，来搜寻对应的实现
搜寻路径如下：

        META-INF/services/org.apache.commons.logging.LogFactory
    简单来说就是搜寻哪些jar包中含有搜寻含有上述文件，该文件中指明了对应的LogFactory实现
+ 从commons-logging的配置文件中寻找

  commons-logging也是可以拥有自己的配置文件的，名字为commons-logging.properties，只不过目前大多数情况下，我们都没有去使用它。如果使用了该配置文件，尝试从配置文件中读取属性"org.apache.commons.logging.LogFactory"对应的值
  
+ 最后还没找到的话，使用默认的org.apache.commons.logging.impl.LogFactoryImpl,LogFactoryImpl是commons-logging提供的默认实现
 
2. 根据LogFactory获取Log的过程

这时候就需要寻找底层是选用哪种类型的日志,就以commons-logging提供的默认实现为例，来详细看下这个过程：
+ 从commons-logging的配置文件中寻找Log实现类的类名

  从commons-logging.properties配置文件中寻找属性为"org.apache.commons.logging.Log"对应的Log类名
  
+ 从系统属性中寻找Log实现类的类名
即如下方式获取：
           
        System.getProperty("org.apache.commons.logging.Log")
        
+ 如果上述方式没找到，则从classesToDiscover属性中寻找
  classesToDiscover属性值如下：
```java (type)
private static final String[] classesToDiscover = {
  
    "org.apache.commons.logging.impl.Log4JLogger",
    "org.apache.commons.logging.impl.Jdk14Logger",
    "org.apache.commons.logging.impl.Jdk13LumberjackLogger",
    "org.apache.commons.logging.impl.SimpleLog"
  };
```
它会尝试根据上述类名，依次进行创建，如果能创建成功，则使用该Log，然后返回给用户。



  
  



        




  

  






