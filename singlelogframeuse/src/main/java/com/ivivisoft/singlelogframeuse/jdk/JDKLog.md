## 简单过程分析
1. 创建一个LogManager

   默认是java.util.logging.LogManager，但是也可以自定义，修改系统属性"java.util.logging.manager"即可，源码如下（manager就是LogManager）：
默认是java.util.logging.LogManager，但是也可以自定义，修改系统属性"java.util.logging.manager"即可，源码如下（manager就是LogManager）：

```java (type)
try {
   cname = System.getProperty("java.util.logging.manager");
   if (cname != null) {
       try {
           Class clz = ClassLoader.getSystemClassLoader().loadClass(cname);
           manager = (LogManager) clz.newInstance();
       } catch (ClassNotFoundException ex) {
           Class clz = Thread.currentThread().getContextClassLoader().loadClass(cname);
           manager = (LogManager) clz.newInstance();
       }
   }
} catch (Exception ex) {            
   System.err.println("Could not load Logmanager \"" + cname + "\"");
   ex.printStackTrace();
} if (manager == null) {            
   manager = new LogManager();
}
```
    
2. 加载配置文件

   默认是jre目录下的lib/logging.properties文件，也可以自定义修改系统属性"java.util.logging.log4j.file",源码如下：

```java type)
String fname = System.getProperty("java.util.logging.log4j.file");
if (fname == null) {
   fname = System.getProperty("java.home");
   if (fname == null) {
       throw new Error("Can't find java.home ??");
   }
   File f = new File(fname, "lib");
   f = new File(f, "logging.properties");
   fname = f.getCanonicalPath();
} 
InputStream in = new FileInputStream(fname);
BufferedInputStream bin = new BufferedInputStream(in);
try {
   readConfiguration(bin);
}
```

3. 创建Logger，并缓存起来，放置到一个HashTable中,并把LogManager设置进新创建的logger中

以tomcat为例，它就自定义了上述配置：

在tomcat的启动文件catalina.bat中，有如下设置：

+ 修改属性"java.util.logging.manager",自定义LogManager，使用自己的ClassLoaderLogManager

  if not "%LOGGING_MANAGER%" == "" goto noJuliManager set LOGGING_MANAGER=-Djava.util.logging.manager=org.apache.juli.ClassLoaderLogManager :noJuliManager set JAVA_OPTS=%JAVA_OPTS% %LOGGING_MANAGER%
  ````
+ 修改属性"java.util.logging.log4j.file",自定义配置文件，使用自己的%CATALINA_BASE%\conf\logging.properties文件

  if not "%LOGGING_CONFIG%" == "" goto noJuliConfig set LOGGING_CONFIG=-Dnop if not exist "%CATALINA_BASE%\conf\logging.properties" goto noJuliConfig set LOGGING_CONFIG=-Djava.util.logging.log4j.file="%CATALINA_BASE%\conf\logging.properties" :noJuliConfig set JAVA_OPTS=%JAVA_OPTS% %LOGGING_CONFIG%

## 深入研究 (基于OpenJDK7)
### 相关术语
1. logger:对于logger，需要知道其下几个方面:
    + 代码需要输入日志的地方都会用到Logger，这几乎是一个JDK logging模块的代言人，我们常常用Logger.getLogger("com.aaa.bbb");获得一个logger，然后使用logger做日志的输出。
    + logger其实只是一个逻辑管理单元，其多数操作都只是作为一个中继者传递别的<角色>，比如说：Logger.getLogger(“xxx”)的调用将会依赖于LogManager类，使用logger输入日志信息的时候会调用logger中的所有handler进行日志的输入。
    + logger是有层次关系的，我们可一般性的理解为包名之间的父子继承关系。每个logger通常以Java包名为其名称。子logger通常会从父logger继承logger级别、handler、ResourceBundle名(与国际化信息有关)等。
    + 整个JVM会存在一个名称为空的root logger，所有匿名的logger都会把root logger作为其父

2. LogManager:

   整个JVM内部所有logger的管理，logger的生成、获取等操作都依赖于它，也包括配置文件的读取。LogManager中会有一个HashTable【private HashTable<String,WeakReference<Logger>> loggers】用于存储目前所有的logger，如果需要获取logger的时候，HashTable已经有存在logger的话就直接返回HashTable中的，如果HashTable中没有logger，则新建一个同时放入HashTable进行保存。
   
3. Handler：

    用来控制日志输出的，比如JDK自带的ConsoleHandler把输出流重定向到System.err输出，每次调用Logger的方法进行输出时都会调用Handler的publish方法，每个logger有多个handler。我们可以利用handler来把日志输入到不同的地方(比如文件系统或者是远程Socket连接). 
 
4. Formatter:

    日志在真正输出前需要进行一定的格式话：比如是否输出时间？时间格式？是否输入线程名？是否使用国际化信息等都依赖于Formatter。 
    
5. Log Level：

    这是做容易理解的一个，也是logging为什么能帮助我们适应从开发调试到部署上线等不同阶段对日志输出粒度的不同需求。JDK Log级别从高到低为OFF(2^31-1)—>SEVERE(1000)—>WARNING(900)—>INFO(800)—>CONFIG(700)—>FINE(500)—>FINER(400)—>FINEST(300)—>ALL(-2^31)，每个级别分别对应一个数字，输出日志时级别的比较就依赖于数字大小的比较。但是需要注意的是：不仅是logger具有级别，handler也是有级别，也就是说如果某个logger级别是FINE，客户希望输入FINE级别的日志，如果此时logger对应的handler级别为INFO，那么FINE级别日志仍然是不能输出的。
    
6. 组件关系:
    + LogManager与logger是1对多关系，整个JVM运行时只有一个LogManager，且所有的logger均在LogManager中   
    + logger与handler是多对多关系，logger在进行日志输出的时候会调用所有的handler进行日志的处理
    + handler与formatter是一对一关系，一个handler有一个formatter进行日志的格式化处理
    + 很明显：logger与level是一对一关系，handler与level也是一对一关系
      

### Logging配置
    
   JDK默认的logging配置文件为：$JAVA_HOME/jre/lib/logging.properties，可以使用系统属性java.util.logging.log4j.file指定相应的配置文件对默认的配置文件进行覆盖，配置文件中通常包含以下几部分定义：
   
   + handlers：用逗号分隔每个Handler，这些handler将会被加到root logger中。也就是说即使我们不给其他logger配置handler属性，在输出日志的时候logger会一直找到root logger，从而找到handler进行日志的输入。
   + .level是root logger的日志级别
   + <handler>.xxx是配置具体某个handler的属性，比如java.util.logging.ConsoleHandler.formatter便是为ConsoleHandler配置相应的日志Formatter
   + logger的配置，所有以[.level]结尾的属性皆被认为是对某个logger的级别的定义，如com.bes.server.level=FINE是给名为[com.bes.server]的logger定义级别为FINE。顺便说下，前边提到过logger的继承关系，如果还有com.bes.server.webcontainer这个logger，且在配置文件中没有定义该logger的任何属性，那么其将会从[com.bes.server]这个logger进行属性继承。除了级别之外，还可以为logger定义handler和useParentHandlers(默认是为true)属性，如com.bes.server.handler=com.bes.test.ServerFileHandler(需要是一个extends java.util.logging.Handler的类)，com.bes.server.useParentHandlers=false(意味着com.bes.server这个logger进行日志输出时，日志仅仅被处理一次，用自己的handler输出，不会传递到父logger的handler)。以下是JDK配置文件示例:
   
    ############################################################
    #  	Default Logging Configuration File
    #
    # You can use a different file by specifying a filename
    # with the java.util.logging.log4j.file system property.
    # For example java -Djava.util.logging.log4j.file=myfile
    ############################################################
    
    ############################################################
    #  	Global properties
    ############################################################
    
    # "handlers" specifies a comma separated list of log Handler
    # classes.  These handlers will be installed during VM startup.
    # Note that these classes must be on the system classpath.
    # By default we only configure a ConsoleHandler, which will only
    # show messages at the INFO and above levels.
    handlers= java.util.logging.ConsoleHandler
    
    # To also add the FileHandler, use the following line instead.
    #handlers= java.util.logging.FileHandler, java.util.logging.ConsoleHandler
    
    # Default global logging level.
    # This specifies which kinds of events are logged across
    # all loggers.  For any given facility this global level
    # can be overriden by a facility specific level
    # Note that the ConsoleHandler also has a separate level
    # setting to limit messages printed to the console.
    .level= INFO
    
    ############################################################
    # Handler specific properties.
    # Describes specific configuration info for Handlers.
    ############################################################
    
    # default file output is in user's home directory.
    java.util.logging.FileHandler.pattern = %h/java%u.log
    java.util.logging.FileHandler.limit = 50000
    java.util.logging.FileHandler.count = 1
    java.util.logging.FileHandler.formatter = java.util.logging.XMLFormatter
    
    # Limit the message that are printed on the console to INFO and above.
    java.util.logging.ConsoleHandler.level = INFO
    java.util.logging.ConsoleHandler.formatter = java.util.logging.SimpleFormatter
    
    # Example to customize the SimpleFormatter output format
    # to print one-line log message like this:
    #     <level>: <log message> [<date/time>]
    #
    # java.util.logging.SimpleFormatter.format=%4$s: %5$s [%1$tc]%n
    
    ############################################################
    # Facility specific properties.
    # Provides extra control for each logger.
    ############################################################
    
    # For example, set the com.xyz.foo logger to only log SEVERE
    # messages:
    com.xyz.foo.level = SEVERE
     

### Logging执行原理
#### Logger的获取
1. 首先是调用Logger的如下方法获得一个logger
```java (type)
public static synchronized Logger getLogger(String name) {
       LogManager manager = LogManager.getLogManager();
    return manager.demandLogger(name);
}
```

2. 上面的调用会触发java.util.logging.LoggerManager的类初始化工作，LoggerManager有一个静态化初始化块(这是会先于LoggerManager的构造函数调用的~_~)：
```java (type)
static {
   AccessController.doPrivileged(new PrivilegedAction<Object>() {
       public Object run() {
           String cname =null;
           try {
               cname =System.getProperty("java.util.logging.manager");
               if (cname !=null) {
                  try {
                       Class clz =ClassLoader.getSystemClassLoader().loadClass(cname);
                       manager= (LogManager) clz.newInstance();
                   } catch(ClassNotFoundException ex) {
               Class clz =Thread.currentThread().getContextClassLoader().loadClass(cname);
                      manager= (LogManager) clz.newInstance();
                   }
               }
           } catch (Exceptionex) {
              System.err.println("Could not load Logmanager \"" + cname+ "\"");
              ex.printStackTrace();
           }
           if (manager ==null) {
               manager = newLogManager();
           }
           manager.rootLogger= manager.new RootLogger();
           manager.addLogger(manager.rootLogger);
           Logger.global.setLogManager(manager);
           manager.addLogger(Logger.global);
           return null;
       }
   });
}
```

   从静态初始化块中可以看出LoggerManager是可以使用系统属性java.util.logging.manager指定一个继承自java.util.logging.LoggerManager的类进行替换的，比如Tomcat启动脚本中就使用该机制以使用自己的LoggerManager。

不管是JDK默认的java.util.logging.LoggerManager还是自定义的LoggerManager，初始化工作中均会给LoggerManager添加两个logger，一个是名称为””的root logger，且logger级别设置为默认的INFO；另一个是名称为global的全局logger，级别仍然为INFO。

LogManager”类”初始化完成之后就会读取配置文件(默认为$JAVA_HOME/jre/lib/logging.properties)，把配置文件的属性名<->属性值这样的键值对保存在内存中，方便之后初始化logger的时候使用。

3. 1步骤中Logger类发起的getLogger操作将会调用java.util.logging.LoggerManager的如下方法：
```java (type)
Logger demandLogger(String name) {
   Logger result =getLogger(name);
   if (result == null) {
       result = new Logger(name, null);
       addLogger(result);
       result =getLogger(name);
   }
   return result;
 }
```

  可以看出，LoggerManager首先从现有的logger列表中查找，如果找不到的话，会新建一个logger并加入到列表中。当然很重要的是新建logger之后需要对logger进行初始化，这个初始化详见java.util.logging.LoggerManager#addLogger()方法中，改方法会根据配置文件设置logger的级别以及给logger添加handler等操作。

 到此为止logger已经获取到了，你同时也需要知道此时你的logger中已经有级别、handler等重要信息，下面将分析输出日志时的逻辑。 

#### 日志的输出

  首先我们通常会调用Logger类下面的方法，传入日志级别以及日志内容。
```java (type)
public void log(Levellevel, String msg) {
      if (level.intValue() < levelValue ||levelValue == offValue) {
          return;
      }
      LogRecord lr = new LogRecord(level, msg);
      doLog(lr);
}
```
  该方法可以看出，Logger类首先是进行级别的校验，如果级别校验通过，则会新建一个LogRecord对象，LogRecord中除了日志级别，日志内容之外还会包含调用线程信息，日志时刻等；之后调用doLog(LogRecord lr)方法
```java (type)
private void doLog(LogRecord lr) {
      lr.setLoggerName(name);
      String ebname =getEffectiveResourceBundleName();
      if (ebname != null) {
          lr.setResourceBundleName(ebname);
          lr.setResourceBundle(findResourceBundle(ebname));
      }
      log(lr);
}
```
  doLog(LogRecord lr)方法中设置了ResourceBundle信息(这个与国际化有关)之后便直接调用log(LogRecord record) 方法 
```java (type)
public void log(LogRecord record) {
      if (record.getLevel().intValue() <levelValue || levelValue == offValue) {
          return;
      }
      synchronized (this) {
          if (filter != null &&!filter.isLoggable(record)) {
              return;
          }
      }
      Logger logger = this;
      while (logger != null) {
          Handler targets[] = logger.getHandlers();

          if(targets != null) {
              for (int i = 0; i < targets.length; i++){
                       targets[i].publish(record);
                   }
          }

          if(!logger.getUseParentHandlers()) {
                   break;
          }

          logger= logger.getParent();
      }
}
``` 
  很清晰，while循环是重中之重，首先从logger中获取handler，然后分别调用handler的publish(LogRecord record)方法。while循环证明了前面提到的会一直把日志委托给父logger处理的说法，当然也证明了可以使用logger的useParentHandlers属性控制日志不进行往上层logger传递的说法。到此为止logger对日志的控制差不多算是完成，接下来的工作就是看handler的了，这里我们以java.util.logging.ConsoleHandler为例说明日志的输出。
  
```java
public class ConsoleHandler extends StreamHandler {
    public ConsoleHandler() {
          sealed = false;
          configure();
          setOutputStream(System.err);
          sealed = true;
    }
}
```
  ConsoleHandler构造函数中除了需要调用自身的configure()方法进行级别、filter、formatter等的设置之外，最重要的我们最关心的是setOutputStream(System.err)这一句，把系统错误流作为其输出。而ConsoleHandler的publish(LogRecord record)是继承自java.util.logging.StreamHandler的，如下所示：
```java (type)
public synchronized void publish(LogRecord record) {
   if(!isLoggable(record)) {
       return;
   }
   String msg;
   try {
       msg =getFormatter().format(record);
   } catch (Exception ex){
       // We don't want to throw an exception here, but we
       // report the exception to any registered ErrorManager.
       reportError(null,ex, ErrorManager.FORMAT_FAILURE);
       return;
   }
   
   try {
       if (!doneHeader) {
          writer.write(getFormatter().getHead(this));
          doneHeader =true;
       }
       writer.write(msg);
   } catch (Exception ex){
       // We don't want to throw an exception here, but we
       // report the exception to any registered ErrorManager.
       reportError(null,ex, ErrorManager.WRITE_FAILURE);
   }
}
```
  方法逻辑也很清晰，首先是调用Formatter对消息进行格式化，说明一下：格式化其实是进行国际化处理的重要契机。然后直接把消息输出到对应的输出流中。需要注意的是handler也会用自己的level和LogRecord中的level进行比较，看是否真正输出日志。


   
  


