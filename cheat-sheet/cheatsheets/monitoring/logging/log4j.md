# Log4j для Java

Комплексное руководство по Apache Log4j: мощному и гибкому logging фреймворку для Java с поддержкой advanced конфигурации, фильтров, appenders и production-ready features.

**Дата последнего обновления:** 2026-01-21

## Полезные ссылки

### Официальная документация
- [Log4j 2 Documentation](https://logging.apache.org/log4j/2.x/)
- [Log4j 2 Manual](https://logging.apache.org/log4j/2.x/manual/index.html)
- [Log4j 2 GitHub](https://github.com/apache/logging-log4j2)

### Apache
- [Log4j 2 API](https://logging.apache.org/log4j/2.x/log4j-api/apidocs/index.html)
- [Log4j 2 Core](https://logging.apache.org/log4j/2.x/log4j-core/apidocs/index.html)
- [Log4j 2 Examples](https://github.com/apache/logging-log4j-samples)

### Статьи и туториалы
- [Log4j 2 Configuration](https://www.baeldung.com/log4j2-configuration)
- [Log4j 2 Async Logging](https://www.baeldung.com/log4j2-async-config)
- [Log4j 2 vs Logback](https://www.baeldung.com/log4j2-vs-logback)

### См. также
- `logging-basics.md` - Основы логирования
- `logback.md` - Logback альтернатива
- `slf4j.md` - SLF4J facade

## Содержание

- [Введение в Log4j](#введение-в-log4j)
- [Архитектура Log4j](#архитектура-log4j)
- [Configuration](#configuration)
- [Appenders](#appenders)
- [Layouts](#layouts)
- [Filters](#filters)
- [Context и Thread Context](#context-и-thread-context)
- [Routing](#routing)
- [Async logging](#async-logging)
- [JMX configuration](#jmx-configuration)
- [Performance tuning](#performance-tuning)
- [Security](#security)
- [Migration from Log4j 1.x](#migration-from-log4j-1x)
- [Testing](#testing)
- [Best Practices](#best-practices)
- [Troubleshooting](#troubleshooting)
- [Заключение](#заключение)

## Введение в Log4j

**Apache Log4j** — это один из самых популярных и мощных logging фреймворков для Java. Log4j 2.x является полной переработкой предыдущих версий с улучшенной производительностью, гибкостью и безопасностью.

### Почему Log4j?

Log4j решает критические проблемы enterprise логирования:

1. **Performance** — высокая производительность с async logging
2. **Configuration** — гибкая конфигурация через XML, JSON, YAML, Properties
3. **Extensibility** — plugin архитектура для custom компонентов
4. **Security** — встроенная защита от уязвимостей
5. **Cloud Ready** — поддержка cloud-native архитектур
6. **Backwards Compatible** — совместимость с Log4j 1.x через bridge
7. **Production Ready** — проверен в крупнейших системах

### Log4j vs другие фреймворки

| Feature | Log4j 2 | Logback | java.util.logging |
|---------|---------|---------|-------------------|
| **Performance** | ✅ Fast | ✅ Fast | ❌ Slow |
| **Configuration** | ✅ XML/JSON/YAML | ✅ XML/Groovy | ❌ Properties |
| **Async Logging** | ✅ Native | ✅ Native | ❌ Manual |
| **Plugin System** | ✅ Extensive | ❌ Limited | ❌ None |
| **Security** | ✅ Built-in | ❌ Manual | ❌ Manual |
| **Cloud Support** | ✅ Native | ❌ Limited | ❌ None |
| **Maintenance** | ✅ Active | ✅ Active | ✅ Active |

## Архитектура Log4j

### Core components

#### Logger hierarchy
```
LoggerContext
├── Root Logger (level: INFO)
│   ├── com (inherited INFO)
│   │   ├── com.example (level: DEBUG)
│   │   │   ├── com.example.service (inherited DEBUG)
│   │   │   ├── com.example.controller (inherited DEBUG)
│   │   │   └── com.example.repository (level: WARN)
│   │   └── org.springframework (level: WARN)
│   └── org.hibernate (level: ERROR)
└── Async Logger
```

#### Logger, Appender, Layout, Filter
```java
// Logger - captures logging requests
Logger logger = LogManager.getLogger(MyClass.class);

// Appender - sends log events to destination
FileAppender fileAppender = FileAppender.newBuilder()
    .setName("FileAppender")
    .withFileName("application.log")
    .build();

// Layout - formats log events
PatternLayout layout = PatternLayout.newBuilder()
    .withPattern("%d{yyyy-MM-dd HH:mm:ss.SSS} [%t] %-5level %logger{36} - %msg%n")
    .build();

// Filter - filters log events
ThresholdFilter filter = ThresholdFilter.createFilter(
    Level.DEBUG, Filter.Result.ACCEPT, Filter.Result.DENY);
```

### LoggerContext

```java
public class LoggerContextExample {
    
    public void demonstrateContext() {
        // Get logger context
        LoggerContext context = (LoggerContext) LogManager.getContext(false);
        
        // Get configuration
        Configuration config = context.getConfiguration();
        
        // Get root logger
        Logger rootLogger = context.getRootLogger();
        
        // Get logger by name
        Logger logger = context.getLogger("com.example");
        
        // Update configuration
        context.updateLoggers();
        
        // Logger hierarchy
        Logger parent = logger.getParent();
        System.out.println("Logger: " + logger.getName());
        System.out.println("Parent: " + (parent != null ? parent.getName() : "null"));
        System.out.println("Level: " + logger.getLevel());
    }
}
```

## Configuration

### XML configuration

#### Basic XML configuration
```xml
<?xml version="1.0" encoding="UTF-8"?>
<Configuration status="WARN">
    
    <Properties>
        <Property name="LOG_HOME">logs</Property>
        <Property name="LOG_PATTERN">%d{yyyy-MM-dd HH:mm:ss.SSS} [%t] %-5level %logger{36} - %msg%n</Property>
    </Properties>
    
    <!-- Console appender -->
    <Appenders>
        <Console name="Console" target="SYSTEM_OUT">
            <PatternLayout pattern="${LOG_PATTERN}"/>
        </Console>
        
        <!-- File appender -->
        <File name="FileAppender" fileName="${LOG_HOME}/application.log">
            <PatternLayout pattern="${LOG_PATTERN}"/>
        </File>
        
        <!-- Rolling file appender -->
        <RollingFile name="RollingFileAppender" 
                     fileName="${LOG_HOME}/application.log"
                     filePattern="${LOG_HOME}/application-%d{yyyy-MM-dd}-%i.log.gz">
            <PatternLayout pattern="${LOG_PATTERN}"/>
            <Policies>
                <SizeBasedTriggeringPolicy size="10MB"/>
                <TimeBasedTriggeringPolicy/>
            </Policies>
            <DefaultRolloverStrategy max="30"/>
        </RollingFile>
    </Appenders>
    
    <!-- Loggers -->
    <Loggers>
        <Logger name="com.example" level="DEBUG" additivity="false">
            <AppenderRef ref="Console"/>
            <AppenderRef ref="RollingFileAppender"/>
        </Logger>
        
        <Logger name="org.springframework" level="INFO"/>
        <Logger name="org.hibernate" level="WARN"/>
        
        <!-- Root logger -->
        <Root level="INFO">
            <AppenderRef ref="Console"/>
        </Root>
    </Loggers>
    
</Configuration>
```

#### Programmatic configuration
```java
public class ProgrammaticConfig {
    
    public static void configureLog4j() {
        ConfigurationBuilder<BuiltConfiguration> builder = 
            ConfigurationBuilderFactory.newConfigurationBuilder();
        
        // Properties
        builder.addProperty("LOG_HOME", "logs");
        builder.addProperty("LOG_PATTERN", 
            "%d{yyyy-MM-dd HH:mm:ss.SSS} [%t] %-5level %logger{36} - %msg%n");
        
        // Console appender
        AppenderComponentBuilder consoleAppender = builder.newAppender("Console", "CONSOLE")
            .addAttribute("target", ConsoleAppender.Target.SYSTEM_OUT)
            .add(builder.newLayout("PatternLayout")
                .addAttribute("pattern", "${LOG_PATTERN}"));
        
        builder.add(consoleAppender);
        
        // File appender
        AppenderComponentBuilder fileAppender = builder.newAppender("FileAppender", "FILE")
            .addAttribute("fileName", "${LOG_HOME}/application.log")
            .add(builder.newLayout("PatternLayout")
                .addAttribute("pattern", "${LOG_PATTERN}"));
        
        builder.add(fileAppender);
        
        // Root logger
        RootLoggerComponentBuilder rootLogger = builder.newRootLogger(Level.INFO)
            .add(builder.newAppenderRef("Console"))
            .add(builder.newAppenderRef("FileAppender"));
        
        builder.add(rootLogger);
        
        // Apply configuration
        Configurator.initialize(builder.build());
    }
}
```

### JSON configuration

#### JSON config format
```json
{
  "configuration": {
    "status": "WARN",
    "name": "RoutingTest",
    "properties": {
      "property": [
        {
          "name": "LOG_HOME",
          "value": "logs"
        },
        {
          "name": "LOG_PATTERN",
          "value": "%d{yyyy-MM-dd HH:mm:ss.SSS} [%t] %-5level %logger{36} - %msg%n"
        }
      ]
    },
    "appenders": {
      "Console": {
        "name": "Console",
        "target": "SYSTEM_OUT",
        "PatternLayout": {
          "pattern": "${LOG_PATTERN}"
        }
      },
      "File": {
        "name": "FileAppender",
        "fileName": "${LOG_HOME}/application.log",
        "PatternLayout": {
          "pattern": "${LOG_PATTERN}"
        }
      }
    },
    "loggers": {
      "logger": [
        {
          "name": "com.example",
          "level": "DEBUG",
          "additivity": false,
          "AppenderRef": [
            {
              "ref": "Console"
            },
            {
              "ref": "FileAppender"
            }
          ]
        }
      ],
      "root": {
        "level": "INFO",
        "AppenderRef": [
          {
            "ref": "Console"
          }
        ]
      }
    }
  }
}
```

### YAML configuration

#### YAML config format
```yaml
configuration:
  status: WARN
  name: YAMLConfig
  
  properties:
    property:
      - name: LOG_HOME
        value: logs
      - name: LOG_PATTERN
        value: "%d{yyyy-MM-dd HH:mm:ss.SSS} [%t] %-5level %logger{36} - %msg%n"
  
  appenders:
    Console:
      name: Console
      target: SYSTEM_OUT
      PatternLayout:
        pattern: "${LOG_PATTERN}"
    
    File:
      name: FileAppender
      fileName: "${LOG_HOME}/application.log"
      PatternLayout:
        pattern: "${LOG_PATTERN}"
  
  loggers:
    logger:
      - name: com.example
        level: DEBUG
        additivity: false
        AppenderRef:
          - ref: Console
          - ref: FileAppender
    
    root:
      level: INFO
      AppenderRef:
        ref: Console
```

## Appenders

### File appenders

#### FileAppender
```xml
<File name="FileAppender" fileName="logs/application.log" append="true">
    <PatternLayout pattern="%d{yyyy-MM-dd HH:mm:ss.SSS} [%t] %-5level %logger{36} - %msg%n"/>
    <Filters>
        <ThresholdFilter level="DEBUG" onMatch="ACCEPT" onMismatch="DENY"/>
    </Filters>
</File>
```

#### RollingFileAppender
```xml
<RollingFile name="RollingFileAppender"
             fileName="logs/application.log"
             filePattern="logs/application-%d{yyyy-MM-dd}-%i.log.gz">
    
    <PatternLayout pattern="%d{yyyy-MM-dd HH:mm:ss.SSS} [%t] %-5level %logger{36} - %msg%n"/>
    
    <!-- Trigger policies -->
    <Policies>
        <!-- Roll over when file size reaches 10MB -->
        <SizeBasedTriggeringPolicy size="10MB"/>
        
        <!-- Roll over at midnight -->
        <TimeBasedTriggeringPolicy/>
        
        <!-- Roll over when JVM starts -->
        <OnStartupTriggeringPolicy/>
    </Policies>
    
    <!-- Rollover strategy -->
    <DefaultRolloverStrategy max="30"/>
    
</RollingFile>
```

### Network appenders

#### SocketAppender
```xml
<Socket name="SocketAppender" host="logserver.example.com" port="4560">
    <SerializedLayout/>
</Socket>
```

#### HTTP Appender
```xml
<Http name="HttpAppender" url="http://logserver.example.com/logs">
    <Property name="X-API-Key">your-api-key</Property>
    <JsonLayout properties="true"/>
    <Property name="Content-Type">application/json</Property>
</Http>
```

### Database appenders

#### JDBC Appender
```xml
<JDBC name="JDBCAppender" tableName="application_logs">
    <DataSource jndiName="java:/comp/env/jdbc/LoggingDataSource"/>
    
    <ColumnMapping name="EVENT_DATE" type="java.sql.Types.TIMESTAMP"/>
    <ColumnMapping name="LEVEL" type="java.sql.Types.VARCHAR"/>
    <ColumnMapping name="LOGGER" type="java.sql.Types.VARCHAR"/>
    <ColumnMapping name="MESSAGE" type="java.sql.Types.VARCHAR"/>
    <ColumnMapping name="EXCEPTION" type="java.sql.Types.CLOB"/>
    
    <Column name="EVENT_DATE" pattern="%d{yyyy-MM-dd HH:mm:ss.SSS}"/>
    <Column name="LEVEL" pattern="%level"/>
    <Column name="LOGGER" pattern="%logger"/>
    <Column name="MESSAGE" pattern="%message"/>
    <Column name="EXCEPTION" pattern="%exception"/>
</JDBC>
```

### Cloud appenders

#### CloudWatch Appender
```xml
<CloudWatch name="CloudWatchAppender">
    <Region>us-east-1</Region>
    <LogGroup>my-application</LogGroup>
    <LogStream>%i{hostname}</LogStream>
    <PatternLayout pattern="%d{yyyy-MM-dd HH:mm:ss.SSS} [%t] %-5level %logger{36} - %msg%n"/>
</CloudWatch>
```

### Custom appender

```java
@Plugin(name = "CustomAppender", category = "Core", elementType = "appender", printObject = true)
public class CustomAppender extends AbstractAppender {
    
    private String customProperty;
    
    protected CustomAppender(String name, Filter filter, Layout<? extends Serializable> layout, 
                           boolean ignoreExceptions, String customProperty) {
        super(name, filter, layout, ignoreExceptions);
        this.customProperty = customProperty;
    }
    
    @Override
    public void append(LogEvent event) {
        // Custom logic to handle log event
        String message = new String(getLayout().toByteArray(event));
        
        // Send to custom destination (database, queue, external service, etc.)
        sendToCustomDestination(message, event.getLevel(), event.getLoggerName());
    }
    
    private void sendToCustomDestination(String message, Level level, String loggerName) {
        // Implementation for sending logs
        System.out.println(String.format("[%s] %s - %s: %s", 
            customProperty, level, loggerName, message));
    }
    
    @PluginBuilderFactory
    public static Builder newBuilder() {
        return new Builder();
    }
    
    public static class Builder implements org.apache.logging.log4j.core.util.Builder<CustomAppender> {
        
        @PluginBuilderAttribute
        private String name;
        
        @PluginBuilderAttribute
        private String customProperty;
        
        @Override
        public CustomAppender build() {
            return new CustomAppender(name, null, null, true, customProperty);
        }
    }
}

// Configuration usage
<CustomAppender name="CustomAppender" customProperty="MyApp">
    <PatternLayout pattern="%d{yyyy-MM-dd HH:mm:ss.SSS} [%t] %-5level %logger{36} - %msg%n"/>
</CustomAppender>
```

## Layouts

### PatternLayout

```xml
<PatternLayout pattern="%d{yyyy-MM-dd HH:mm:ss.SSS} [%t] %-5level %logger{36} - %msg%n"/>
```

### Pattern reference

| Pattern | Description | Example |
|---------|-------------|---------|
| `%d{pattern}` | Date/time | `2023-12-01 10:30:45.123` |
| `%t` | Thread name | `main` |
| `%-5level` | Log level (padded) | `INFO ` |
| `%logger{length}` | Logger name | `com.example.MyClass` |
| `%msg` | Log message | `User logged in` |
| `%n` | Newline | |
| `%X{key}` | ThreadContext value | `userId=123` |
| `%replace{text}{pattern}{replacement}` | String replacement | |
| `%highlight{pattern}{color}` | Colored output | |

### JSON Layout

```xml
<JsonLayout complete="true" compact="false" eventEol="true">
    <KeyValuePair key="appName" value="MyApplication"/>
    <KeyValuePair key="version" value="1.0.0"/>
</JsonLayout>
```

### XML Layout

```xml
<XmlLayout complete="true" compact="false" includeStacktrace="true">
    <KeyValuePair key="appName" value="MyApplication"/>
</XmlLayout>
```

### Custom Layout

```java
@Plugin(name = "CustomLayout", category = "Core", elementType = "layout", printObject = true)
public class CustomLayout extends AbstractStringLayout {
    
    protected CustomLayout(Charset charset) {
        super(charset);
    }
    
    @Override
    public String toSerializable(LogEvent event) {
        StringBuilder sb = new StringBuilder();
        
        // Custom formatting
        sb.append("CUSTOM: ");
        sb.append(event.getTimeMillis()).append(" ");
        sb.append("[").append(event.getLevel()).append("] ");
        sb.append(event.getLoggerName()).append(" - ");
        sb.append(event.getMessage().getFormattedMessage());
        
        if (event.getThrown() != null) {
            sb.append("\nException: ").append(event.getThrown().getMessage());
        }
        
        return sb.toString();
    }
    
    @PluginBuilderFactory
    public static Builder newBuilder() {
        return new Builder();
    }
    
    public static class Builder implements org.apache.logging.log4j.core.util.Builder<CustomLayout> {
        
        @PluginBuilderAttribute
        private Charset charset = StandardCharsets.UTF_8;
        
        @Override
        public CustomLayout build() {
            return new CustomLayout(charset);
        }
    }
}

// Usage
<CustomAppender name="CustomAppender">
    <CustomLayout/>
</CustomAppender>
```

## Filters

### Built-in filters

#### ThresholdFilter
```xml
<Filters>
    <ThresholdFilter level="DEBUG" onMatch="ACCEPT" onMismatch="DENY"/>
</Filters>
```

#### LevelRangeFilter
```xml
<LevelRangeFilter minLevel="DEBUG" maxLevel="ERROR" onMatch="ACCEPT" onMismatch="DENY"/>
```

#### RegexFilter
```xml
<RegexFilter regex=".*password.*" onMatch="DENY" onMismatch="ACCEPT"/>
```

### Custom filter

```java
@Plugin(name = "CustomFilter", category = "Core", elementType = "filter", printObject = true)
public class CustomFilter extends AbstractFilter {
    
    private final String keyword;
    
    protected CustomFilter(String keyword, Result onMatch, Result onMismatch) {
        super(onMatch, onMismatch);
        this.keyword = keyword;
    }
    
    @Override
    public Result filter(LogEvent event) {
        if (event.getMessage().getFormattedMessage().contains(keyword)) {
            return onMatch;
        }
        return onMismatch;
    }
    
    @PluginBuilderFactory
    public static Builder newBuilder() {
        return new Builder();
    }
    
    public static class Builder implements org.apache.logging.log4j.core.util.Builder<CustomFilter> {
        
        @PluginBuilderAttribute
        private String keyword;
        
        @PluginBuilderAttribute
        private Result onMatch = Result.NEUTRAL;
        
        @PluginBuilderAttribute
        private Result onMismatch = Result.DENY;
        
        @Override
        public CustomFilter build() {
            return new CustomFilter(keyword, onMatch, onMismatch);
        }
    }
}

// Usage
<Filters>
    <CustomFilter keyword="sensitive" onMatch="DENY" onMismatch="ACCEPT"/>
</Filters>
```

## Context и Thread Context

### ThreadContext (MDC equivalent)

#### Basic ThreadContext usage
```java
import org.apache.logging.log4j.ThreadContext;

public class ThreadContextExample {
    
    private static final Logger logger = LogManager.getLogger(ThreadContextExample.class);
    
    public void demonstrateThreadContext() {
        // Add context information
        ThreadContext.put("userId", "12345");
        ThreadContext.put("sessionId", "session-abc");
        ThreadContext.put("requestId", UUID.randomUUID().toString());
        
        logger.info("User logged in");
        logger.debug("Processing user request");
        
        // Context is automatically included in logs
        processUserRequest();
        
        // Clean up context
        ThreadContext.clearAll();
    }
    
    private void processUserRequest() {
        ThreadContext.put("operation", "validate-user");
        logger.info("Starting user validation");
        
        // Validation logic
        
        ThreadContext.remove("operation");
    }
}
```

#### ThreadContext in pattern
```xml
<PatternLayout pattern="%d{yyyy-MM-dd HH:mm:ss.SSS} [%t] %-5level %logger{36} [%X{userId:-}] [%X{requestId:-}] - %msg%n"/>
```

#### ThreadContext stack
```java
public class ThreadContextStackExample {
    
    public void demonstrateStack() {
        // Push context onto stack
        ThreadContext.push("operation1");
        logger.info("Starting operation 1");
        
        try {
            // Nested operation
            ThreadContext.push("operation2");
            logger.info("Starting operation 2");
            
            // More nesting
            ThreadContext.push("operation3");
            logger.info("Starting operation 3");
            
            // Work...
            
            ThreadContext.pop(); // Remove operation3
            logger.info("Completed operation 3");
            
        } finally {
            ThreadContext.pop(); // Remove operation2
            logger.info("Completed operation 2");
        }
        
        ThreadContext.pop(); // Remove operation1
        logger.info("Completed operation 1");
        
        // Clear entire stack
        ThreadContext.clearStack();
    }
}
```

### Context map propagation

#### Async context propagation
```java
@Configuration
public class AsyncContextConfig {
    
    @Bean
    public TaskExecutor taskExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(5);
        executor.setMaxPoolSize(20);
        executor.setQueueCapacity(100);
        
        // Enable context propagation
        executor.setTaskDecorator(new ThreadContextTaskDecorator());
        executor.initialize();
        
        return executor;
    }
    
    static class ThreadContextTaskDecorator implements TaskDecorator {
        
        @Override
        public Runnable decorate(Runnable runnable) {
            // Capture current context
            Map<String, String> contextMap = ThreadContext.getContext();
            
            return () -> {
                try {
                    // Restore context in new thread
                    if (contextMap != null) {
                        ThreadContext.putAll(contextMap);
                    }
                    
                    runnable.run();
                    
                } finally {
                    // Clean up
                    ThreadContext.clearAll();
                }
            };
        }
    }
}
```

## Routing

### Dynamic routing

#### RoutingAppender
```xml
<Appenders>
    <Routing name="RoutingAppender">
        <Routes pattern="$${ctx:route}">
            <!-- Route based on ThreadContext value -->
            <Route>
                <RollingFile name="App-${ctx:route}" 
                           fileName="logs/${ctx:route}/application.log"
                           filePattern="logs/${ctx:route}/application-%d{yyyy-MM-dd}-%i.log.gz">
                    <PatternLayout pattern="%d{yyyy-MM-dd HH:mm:ss.SSS} [%t] %-5level %logger{36} - %msg%n"/>
                    <SizeBasedTriggeringPolicy size="10MB"/>
                </RollingFile>
            </Route>
        </Routes>
    </Routing>
</Appenders>
```

#### Programmatic routing
```java
public class DynamicRoutingExample {
    
    public void routeByTenant() {
        // Set routing context
        ThreadContext.put("route", "tenantA");
        logger.info("Message for tenant A");
        
        ThreadContext.put("route", "tenantB");
        logger.info("Message for tenant B");
        
        ThreadContext.remove("route");
    }
    
    public void routeByLogLevel() {
        // Route ERROR logs to different file
        if (someErrorCondition) {
            ThreadContext.put("route", "errors");
            logger.error("Error message", exception);
            ThreadContext.remove("route");
        }
    }
}
```

## Async logging

### AsyncLogger

#### Configuration
```xml
<Loggers>
    <!-- Async root logger -->
    <AsyncRoot level="INFO">
        <AppenderRef ref="Console"/>
        <AppenderRef ref="RollingFileAppender"/>
    </AsyncRoot>
    
    <!-- Async logger -->
    <AsyncLogger name="com.example" level="DEBUG" additivity="false">
        <AppenderRef ref="Console"/>
        <AppenderRef ref="RollingFileAppender"/>
    </AsyncLogger>
</Loggers>
```

#### AsyncAppender
```xml
<Appenders>
    <Async name="AsyncAppender">
        <AppenderRef ref="RollingFileAppender"/>
        
        <!-- Buffer size -->
        <bufferSize>1024</bufferSize>
        
        <!-- Blocking vs non-blocking -->
        <blocking>false</blocking>
        
        <!-- Error handling -->
        <errorRef>error-appender</errorRef>
    </Async>
</Appenders>
```

### Performance considerations

#### LMAX Disruptor
```xml
<AsyncLoggerConfig name="com.example" level="DEBUG">
    <!-- Use LMAX Disruptor for high performance -->
    <AppenderRef ref="Console"/>
    
    <!-- Disruptor configuration -->
    <property name="bufferSize">131072</property>
    <property name="immediateFlush">false</property>
</AsyncLoggerConfig>
```

## JMX configuration

### JMX configurator

```xml
<Configuration>
    <!-- Enable JMX -->
    <JMX enable="true"/>
    
    <!-- Standard configuration -->
    <Appenders>
        <Console name="Console" target="SYSTEM_OUT">
            <PatternLayout pattern="%d{yyyy-MM-dd HH:mm:ss.SSS} [%t] %-5level %logger{36} - %msg%n"/>
        </Console>
    </Appenders>
    
    <Loggers>
        <Root level="INFO">
            <AppenderRef ref="Console"/>
        </Root>
    </Loggers>
</Configuration>
```

### JMX monitoring

```java
public class JmxMonitoring {
    
    public void demonstrateJmx() throws Exception {
        // Connect to JMX
        JMXConnector connector = JMXConnectorFactory.connect(
            new JMXServiceURL("service:jmx:rmi:///jndi/rmi://localhost:1099/jmxrmi"));
        
        MBeanServerConnection mbsc = connector.getMBeanServerConnection();
        
        // Get logger information
        ObjectName loggerObjectName = new ObjectName("org.apache.logging.log4j2:type=LoggerContext");
        
        // Get logger level
        String level = (String) mbsc.getAttribute(loggerObjectName, "LoggerLevel");
        System.out.println("Current level: " + level);
        
        // Set logger level
        mbsc.setAttribute(loggerObjectName, 
            new Attribute("LoggerLevel", "DEBUG"));
        
        connector.close();
    }
}
```

## Performance tuning

### Memory optimization

#### Object pooling
```xml
<Configuration>
    <!-- Enable object pooling for better performance -->
    <Loggers>
        <AsyncRoot level="INFO">
            <!-- Reuse objects to reduce GC pressure -->
            <property name="objectPoolEnabled">true</property>
            <AppenderRef ref="Console"/>
        </AsyncRoot>
    </Loggers>
</Configuration>
```

#### Layout optimization
```xml
<!-- Use fast layouts for high-throughput scenarios -->
<PatternLayout pattern="%d{yyyy-MM-dd HH:mm:ss.SSS} [%t] %-5level %logger{36} - %msg%n">
    <!-- Disable ANSI escape codes for performance -->
    <disableAnsi>true</disableAnsi>
    
    <!-- No location info for performance -->
    <noConsoleNoAnsi>true</noConsoleNoAnsi>
</PatternLayout>
```

### Garbage collection optimization

#### Immutable log events
```java
public class GcOptimization {
    
    public void demonstrateImmutability() {
        // Log4j 2 creates immutable LogEvent objects
        // This reduces GC pressure in high-throughput scenarios
        
        for (int i = 0; i < 1000000; i++) {
            logger.info("Processing item {}", i);
            // Each log event is immutable and reusable
        }
    }
    
    public void avoidStringConcatenation() {
        // BAD: String concatenation
        logger.debug("User " + userId + " performed " + action + " at " + timestamp);
        
        // GOOD: Parameterized logging
        logger.debug("User {} performed {} at {}", userId, action, timestamp);
        
        // Parameter evaluation is deferred and optimized
    }
}
```

## Security

### Log injection protection

#### Input sanitization
```java
public class SecureLogging {
    
    private static final Logger logger = LogManager.getLogger(SecureLogging.class);
    
    public void logUserAction(String userId, String action) {
        // Sanitize input to prevent log injection
        String sanitizedUserId = sanitizeInput(userId);
        String sanitizedAction = sanitizeInput(action);
        
        logger.info("User {} performed action: {}", sanitizedUserId, sanitizedAction);
    }
    
    public void logApiCall(String url, Map<String, String> headers) {
        // Remove sensitive headers
        Map<String, String> safeHeaders = new HashMap<>(headers);
        safeHeaders.remove("authorization");
        safeHeaders.remove("x-api-key");
        safeHeaders.remove("cookie");
        
        logger.debug("API call to {} with headers: {}", url, safeHeaders.keySet());
    }
    
    private String sanitizeInput(String input) {
        if (input == null) {
            return "";
        }
        
        // Remove or escape dangerous characters
        return input.replaceAll("[\r\n]", "")
                   .replaceAll("%", "%%"); // Double % for pattern escaping
    }
}
```

### Audit logging

#### Secure audit configuration
```xml
<Configuration>
    <!-- Separate audit logger -->
    <Appenders>
        <RollingFile name="AuditAppender" 
                     fileName="logs/audit.log"
                     filePattern="logs/audit-%d{yyyy-MM-dd}-%i.log.gz">
            <PatternLayout pattern="%d{yyyy-MM-dd HH:mm:ss.SSS} [%t] AUDIT %X{userId} %X{action} %X{resource} - %msg%n"/>
            
            <!-- Tamper-proof logging -->
            <Filters>
                <ThresholdFilter level="INFO" onMatch="ACCEPT" onMismatch="DENY"/>
            </Filters>
        </RollingFile>
    </Appenders>
    
    <Loggers>
        <Logger name="AUDIT" level="INFO" additivity="false">
            <AppenderRef ref="AuditAppender"/>
        </Logger>
    </Loggers>
</Configuration>
```

## Migration from Log4j 1.x

### Bridge configuration

#### Maven dependencies
```xml
<!-- Log4j 2 API (replaces log4j 1.x) -->
<dependency>
    <groupId>org.apache.logging.log4j</groupId>
    <artifactId>log4j-api</artifactId>
    <version>2.20.0</version>
</dependency>

<!-- Log4j 2 Core implementation -->
<dependency>
    <groupId>org.apache.logging.log4j</groupId>
    <artifactId>log4j-core</artifactId>
    <version>2.20.0</version>
</dependency>

<!-- Bridge for legacy code (optional) -->
<dependency>
    <groupId>org.apache.logging.log4j</groupId>
    <artifactId>log4j-1.2-api</artifactId>
    <version>2.20.0</version>
</dependency>
```

#### Migration steps
```java
// Before (Log4j 1.x)
import org.apache.log4j.Logger;
import org.apache.log4j.Level;

public class LegacyCode {
    private static final Logger logger = Logger.getLogger(LegacyCode.class);
    
    public void oldLogging() {
        logger.info("This is an info message");
        logger.debug("This is a debug message");
    }
}

// After (Log4j 2.x)
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

public class MigratedCode {
    private static final Logger logger = LogManager.getLogger(MigratedCode.class);
    
    public void newLogging() {
        logger.info("This is an info message");
        logger.debug("This is a debug message");
        
        // New features available
        logger.debug("User {} performed {}", userId, action);
    }
}
```

### Configuration migration

#### Log4j 1.x properties
```properties
# log4j.properties (old)
log4j.rootLogger=INFO, Console, File
log4j.appender.Console=org.apache.log4j.ConsoleAppender
log4j.appender.Console.layout=org.apache.log4j.PatternLayout
log4j.appender.Console.layout.ConversionPattern=%d{yyyy-MM-dd HH:mm:ss} %-5p %c{1} - %m%n
```

#### Log4j 2.x XML
```xml
<!-- log4j2.xml (new) -->
<Configuration>
    <Appenders>
        <Console name="Console" target="SYSTEM_OUT">
            <PatternLayout pattern="%d{yyyy-MM-dd HH:mm:ss} %-5level %c{1} - %msg%n"/>
        </Console>
        
        <File name="FileAppender" fileName="application.log">
            <PatternLayout pattern="%d{yyyy-MM-dd HH:mm:ss} %-5level %c{1} - %msg%n"/>
        </File>
    </Appenders>
    
    <Loggers>
        <Root level="INFO">
            <AppenderRef ref="Console"/>
            <AppenderRef ref="FileAppender"/>
        </Root>
    </Loggers>
</Configuration>
```

## Testing

### Logger testing

#### Mock logger testing
```java
public class LoggerTesting {
    
    @Test
    void testLoggingWithMock() {
        // Create mock logger
        Logger mockLogger = mock(Logger.class);
        
        // Inject mock logger
        TestService service = new TestService(mockLogger);
        
        // Configure mock behavior
        when(mockLogger.isInfoEnabled()).thenReturn(true);
        
        // Execute code
        service.processData("test data");
        
        // Verify logging calls
        verify(mockLogger).info("Processing data: {}", "test data");
        verify(mockLogger).debug("Data validation completed");
        
        // Verify no error logging
        verify(mockLogger, never()).error(anyString());
    }
    
    static class TestService {
        
        private final Logger logger;
        
        public TestService(Logger logger) {
            this.logger = logger;
        }
        
        public void processData(String data) {
            logger.info("Processing data: {}", data);
            
            // Processing logic
            validateData(data);
            
            logger.debug("Data validation completed");
            logger.info("Data processing completed");
        }
        
        private void validateData(String data) {
            // Validation logic
        }
    }
}
```

#### Appender testing
```java
public class AppenderTesting {
    
    @Test
    void testAppenderOutput() {
        // Create in-memory appender
        ListAppender listAppender = ListAppender.createAppender("TestAppender", null, null, false, false);
        listAppender.start();
        
        // Add to logger
        Logger logger = LogManager.getLogger("test");
        ((org.apache.logging.log4j.core.Logger) logger).addAppender(listAppender);
        
        // Generate log messages
        logger.info("Test message 1");
        logger.warn("Test warning");
        logger.info("Test message 2");
        
        // Verify captured events
        List<LogEvent> events = listAppender.getEvents();
        
        assertEquals(3, events.size());
        assertEquals("Test message 1", events.get(0).getMessage().getFormattedMessage());
        assertEquals(Level.WARN, events.get(1).getLevel());
        
        // Clean up
        ((org.apache.logging.log4j.core.Logger) logger).removeAppender(listAppender);
    }
}
```

### Configuration testing

#### Test configuration loading
```java
public class ConfigurationTesting {
    
    @Test
    void testConfigurationLoading() throws Exception {
        // Load test configuration
        ConfigurationSource source = ConfigurationSource.fromResource("log4j2-test.xml", null);
        Configuration config = new XmlConfiguration(new LoggerContext("test"), source);
        
        // Verify configuration
        LoggerConfig rootConfig = config.getRootLogger();
        assertEquals(Level.INFO, rootConfig.getLevel());
        
        // Check appenders
        Map<String, Appender> appenders = config.getAppenders();
        assertTrue(appenders.containsKey("Console"));
        assertTrue(appenders.containsKey("FileAppender"));
    }
    
    @Test
    void testProgrammaticConfiguration() {
        ConfigurationBuilder<BuiltConfiguration> builder = 
            ConfigurationBuilderFactory.newConfigurationBuilder();
        
        // Build test configuration
        builder.add(builder.newAppender("Console", "CONSOLE")
            .addAttribute("target", ConsoleAppender.Target.SYSTEM_OUT));
        
        builder.add(builder.newRootLogger(Level.INFO)
            .add(builder.newAppenderRef("Console")));
        
        Configuration config = builder.build();
        
        // Verify
        LoggerConfig rootConfig = config.getRootLogger();
        assertEquals(Level.INFO, rootConfig.getLevel());
        assertEquals(1, rootConfig.getAppenderRefs().size());
    }
}
```

### ThreadContext testing

#### ThreadContext testing
```java
public class ThreadContextTesting {
    
    @Test
    void testThreadContext() {
        // Set context
        ThreadContext.put("userId", "123");
        ThreadContext.put("requestId", "req-456");
        
        // Verify context
        assertEquals("123", ThreadContext.get("userId"));
        assertEquals("req-456", ThreadContext.get("requestId"));
        
        // Test stack operations
        ThreadContext.push("operation1");
        assertEquals("operation1", ThreadContext.peek());
        
        ThreadContext.push("operation2");
        assertEquals("operation2", ThreadContext.peek());
        
        ThreadContext.pop();
        assertEquals("operation1", ThreadContext.peek());
        
        // Clean up
        ThreadContext.clearAll();
        assertTrue(ThreadContext.getContext().isEmpty());
    }
    
    @Test
    void testThreadContextIsolation() throws InterruptedException {
        ThreadContext.put("main", "value");
        
        AtomicReference<String> threadValue = new AtomicReference<>();
        
        Thread thread = new Thread(() -> {
            // Thread should not see main thread context
            threadValue.set(ThreadContext.get("main"));
            
            // Set thread-specific context
            ThreadContext.put("thread", "threadValue");
            threadValue.set(ThreadContext.get("thread"));
        });
        
        thread.start();
        thread.join();
        
        // Verify isolation
        assertNull(threadValue.get()); // Should not see main thread value initially
        assertEquals("threadValue", ThreadContext.get("thread")); // But thread value should be visible
        
        ThreadContext.clearAll();
    }
}
```

## Best Practices

### 1. Configuration management

#### Environment-specific configurations
```xml
<!-- log4j2-spring.xml -->
<Configuration>
    <!-- Spring profiles -->
    <SpringProfile name="dev">
        <Logger name="com.example" level="DEBUG"/>
    </SpringProfile>
    
    <SpringProfile name="prod">
        <Logger name="com.example" level="INFO"/>
        <AppenderRef ref="RollingFileAppender"/>
    </SpringProfile>
    
    <!-- Common configuration -->
    <Appender name="Console" type="Console">
        <Layout type="PatternLayout" pattern="%d{yyyy-MM-dd HH:mm:ss} %-5level %logger{36} - %msg%n"/>
    </Appender>
    
    <Root level="INFO">
        <AppenderRef ref="Console"/>
    </Root>
</Configuration>
```

#### Configuration validation
```java
@Configuration
public class Log4jConfigurationVerification {
    
    @PostConstruct
    public void verifyConfiguration() {
        LoggerContext context = (LoggerContext) LogManager.getContext(false);
        Configuration config = context.getConfiguration();
        
        // Verify required loggers
        LoggerConfig appLogger = config.getLoggerConfig("com.example");
        if (appLogger.getLevel() == null) {
            throw new IllegalStateException("Application logger level not configured");
        }
        
        // Verify required appenders
        Map<String, Appender> appenders = config.getAppenders();
        if (!appenders.containsKey("Console") && !appenders.containsKey("FileAppender")) {
            throw new IllegalStateException("No output appenders configured");
        }
        
        // Validate file permissions for file appenders
        validateAppenderPermissions(appenders);
        
        logger.info("Log4j configuration verified successfully");
    }
    
    private void validateAppenderPermissions(Map<String, Appender> appenders) {
        for (Appender appender : appenders.values()) {
            if (appender instanceof FileAppender) {
                FileAppender fileAppender = (FileAppender) appender;
                // Check file permissions
            }
        }
    }
}
```

### 2. Performance best practices

#### Async logging configuration
```xml
<Configuration>
    <!-- High-performance async configuration -->
    <Loggers>
        <AsyncRoot level="INFO" bufferSize="131072">
            <AppenderRef ref="RollingFileAppender"/>
        </AsyncRoot>
        
        <!-- Async logger for specific package -->
        <AsyncLogger name="com.example" level="DEBUG" bufferSize="65536">
            <AppenderRef ref="Console"/>
        </AsyncLogger>
    </Loggers>
</Configuration>
```

#### Memory-efficient patterns
```xml
<!-- Optimized patterns for performance -->
<PatternLayout pattern="%d{yyyy-MM-dd HH:mm:ss.SSS} [%t] %-5level %c{1} - %msg%n">
    <!-- Use abbreviated logger names -->
</PatternLayout>

<!-- Avoid expensive lookups -->
<!-- Don't use: %X{expensiveLookup} -->
<!-- Use: Pre-computed context values -->
```

### 3. Security considerations

#### Safe logging practices
```java
public class SecureLogging {
    
    private static final Logger logger = LogManager.getLogger(SecureLogging.class);
    
    public void logUserAction(String userId, String action) {
        // Validate input
        String sanitizedUserId = sanitizeLogInput(userId);
        String sanitizedAction = sanitizeLogInput(action);
        
        logger.info("User {} performed action: {}", sanitizedUserId, sanitizedAction);
    }
    
    public void logAuthentication(String username, boolean success) {
        if (success) {
            logger.info("Authentication successful for user: {}", username);
        } else {
            // Don't log failed password attempts
            logger.warn("Authentication failed for user: {}", username);
        }
    }
    
    public void logPayment(PaymentRequest request) {
        // Mask sensitive data
        String maskedCard = maskCardNumber(request.getCardNumber());
        logger.info("Payment processed: amount={}, card={}", request.getAmount(), maskedCard);
    }
    
    private String sanitizeLogInput(String input) {
        if (input == null) {
            return "";
        }
        
        // Remove control characters and limit length
        return input.replaceAll("\\p{C}", "")
                   .substring(0, Math.min(input.length(), 1000));
    }
    
    private String maskCardNumber(String cardNumber) {
        if (cardNumber == null || cardNumber.length() < 4) {
            return "****";
        }
        return "****-****-****-" + cardNumber.substring(cardNumber.length() - 4);
    }
}
```

### 4. Monitoring and alerting

#### Log-based metrics
```java
@Service
public class LogMetricsCollector {
    
    private final MeterRegistry meterRegistry;
    private final Map<String, Counter> errorCounters = new ConcurrentHashMap<>();
    
    @Autowired
    public LogMetricsCollector(MeterRegistry meterRegistry) {
        this.meterRegistry = meterRegistry;
    }
    
    public void recordLogEvent(String loggerName, Level level, String message) {
        // Record metrics based on log events
        if (level == Level.ERROR) {
            Counter counter = errorCounters.computeIfAbsent(loggerName, name -> 
                Counter.builder("log.errors")
                    .description("Log error count")
                    .tags("logger", name)
                    .register(meterRegistry));
            
            counter.increment();
        }
        
        // Detect patterns for alerting
        if (message.contains("OutOfMemoryError")) {
            // Trigger alert
            alertMemoryIssue(loggerName, message);
        }
    }
    
    private void alertMemoryIssue(String loggerName, String message) {
        // Send alert to monitoring system
        logger.warn("Memory issue detected in {}: {}", loggerName, message);
    }
}
```

## Troubleshooting

### Распространенные проблемы

#### Configuration not loading
```java
// Problem: log4j2.xml not found
// Solution: Check classpath and file location

public class ConfigurationCheck {
    
    public void checkConfiguration() {
        LoggerContext context = (LoggerContext) LogManager.getContext(false);
        Configuration config = context.getConfiguration();
        
        System.out.println("Configuration source: " + config.getConfigurationSource());
        System.out.println("Configuration location: " + config.getConfigurationSource().getLocation());
        
        // List all appenders
        Map<String, Appender> appenders = config.getAppenders();
        System.out.println("Configured appenders: " + appenders.keySet());
        
        // List all loggers
        Map<String, LoggerConfig> loggers = config.getLoggers();
        System.out.println("Configured loggers: " + loggers.keySet());
    }
}
```

#### Multiple binding issues
```java
// Problem: Multiple SLF4J bindings
// Solution: Remove conflicting dependencies

// Check for multiple bindings in Maven
// Only one should be present:
// - log4j-slf4j2-impl (for Log4j 2)
// - logback-classic (for Logback)
// Remove:
// - slf4j-log4j12 (old Log4j 1.x binding)
// - commons-logging (if using slf4j-jcl)
```

#### Performance issues
```java
// Problem: Logging slowing down application
// Solution: Profile and optimize

public class PerformanceProfiler {
    
    public void profileLogging() {
        Logger logger = LogManager.getLogger("performance.test");
        
        long startTime = System.nanoTime();
        
        // Test sync logging
        for (int i = 0; i < 10000; i++) {
            logger.info("Sync message {}", i);
        }
        
        long syncTime = System.nanoTime() - startTime;
        
        // Test async logging
        startTime = System.nanoTime();
        
        for (int i = 0; i < 10000; i++) {
            logger.info("Async message {}", i);
        }
        
        long asyncTime = System.nanoTime() - startTime;
        
        System.out.printf("Sync: %.2fms, Async: %.2fms%n", 
            syncTime / 1e6, asyncTime / 1e6);
    }
    
    public void optimizeExpensiveLogging() {
        // BAD: Expensive operation always executed
        logger.debug("Expensive data: " + computeExpensiveData());
        
        // GOOD: Check level first
        if (logger.isDebugEnabled()) {
            logger.debug("Expensive data: {}", computeExpensiveData());
        }
        
        // BETTER: Use markers for conditional logging
        Marker expensiveMarker = MarkerManager.getMarker("EXPENSIVE");
        logger.debug(expensiveMarker, "Expensive data: {}", () -> computeExpensiveData());
    }
    
    private String computeExpensiveData() {
        return "expensive result";
    }
}
```

#### Memory leaks
```java
// Problem: ThreadContext memory leaks
// Solution: Always clear context

public class ThreadContextLeakPrevention {
    
    public void safeThreadContextUsage() {
        // BAD: Context not cleared
        ThreadContext.put("userId", "123");
        processRequest();
        // Context remains!
        
        // GOOD: Explicit cleanup
        try {
            ThreadContext.put("userId", "123");
            processRequest();
        } finally {
            ThreadContext.clearAll();
        }
        
        // BETTER: Try-with-resources
        try (ThreadContextScope scope = new ThreadContextScope("userId", "123")) {
            processRequest();
        }
    }
    
    static class ThreadContextScope implements AutoCloseable {
        
        public ThreadContextScope(String key, String value) {
            ThreadContext.put(key, value);
        }
        
        @Override
        public void close() {
            ThreadContext.clearAll();
        }
    }
}
```

### Debug techniques

#### Status logger
```xml
<Configuration status="DEBUG">
    <!-- Enable Log4j 2 internal logging -->
    
    <Appenders>
        <Console name="Console" target="SYSTEM_OUT">
            <PatternLayout pattern="%d{yyyy-MM-dd HH:mm:ss.SSS} [%t] %-5level %logger{36} - %msg%n"/>
        </Console>
    </Appenders>
    
    <Loggers>
        <Root level="INFO">
            <AppenderRef ref="Console"/>
        </Root>
    </Loggers>
</Configuration>
```

#### Configuration dump
```java
public class ConfigurationInspector {
    
    public void inspectConfiguration() {
        LoggerContext context = (LoggerContext) LogManager.getContext(false);
        Configuration config = context.getConfiguration();
        
        System.out.println("=== Log4j 2 Configuration Inspection ===");
        System.out.println("Configuration class: " + config.getClass().getName());
        System.out.println("Configuration source: " + config.getConfigurationSource());
        
        // Inspect appenders
        Map<String, Appender> appenders = config.getAppenders();
        System.out.println("\nAppenders:");
        for (Map.Entry<String, Appender> entry : appenders.entrySet()) {
            System.out.println("  " + entry.getKey() + ": " + entry.getValue().getClass().getSimpleName());
        }
        
        // Inspect loggers
        Map<String, LoggerConfig> loggers = config.getLoggers();
        System.out.println("\nLoggers:");
        for (Map.Entry<String, LoggerConfig> entry : loggers.entrySet()) {
            LoggerConfig loggerConfig = entry.getValue();
            System.out.println("  " + entry.getKey() + ": " + loggerConfig.getLevel() + 
                             " (" + loggerConfig.getAppenderRefs().size() + " appenders)");
        }
        
        // Inspect root logger
        LoggerConfig rootConfig = config.getRootLogger();
        System.out.println("\nRoot Logger:");
        System.out.println("  Level: " + rootConfig.getLevel());
        System.out.println("  Appenders: " + rootConfig.getAppenderRefs().size());
        
        System.out.println("=== End Inspection ===");
    }
}
```

#### Thread dump analysis
```java
public class ThreadDumpAnalyzer {
    
    public void analyzeThreadContext() {
        // Get all threads
        Map<Thread, StackTraceElement[]> threadMap = Thread.getAllStackTraces();
        
        System.out.println("=== ThreadContext Analysis ===");
        
        for (Map.Entry<Thread, StackTraceElement[]> entry : threadMap.entrySet()) {
            Thread thread = entry.getKey();
            
            // Check if thread has ThreadContext
            Map<String, String> context = ThreadContext.getContext();
            if (!context.isEmpty()) {
                System.out.println("Thread: " + thread.getName());
                System.out.println("ThreadContext: " + context);
                System.out.println("Stack trace:");
                
                for (StackTraceElement element : entry.getValue()) {
                    if (element.getClassName().contains("log4j") || 
                        element.getClassName().contains("ThreadContext")) {
                        System.out.println("  " + element);
                    }
                }
                System.out.println();
            }
        }
        
        System.out.println("=== End Analysis ===");
    }
}
```

## Заключение

**Apache Log4j 2** — это мощный и современный logging фреймворк, который предоставляет enterprise-grade возможности для Java приложений. Log4j 2 является значительным улучшением по сравнению с Log4j 1.x и предлагает высокую производительность, гибкость и безопасность.

### Ключевые возможности:

1. **High Performance** — async logging, LMAX Disruptor, object pooling
2. **Flexible Configuration** — XML, JSON, YAML, Properties, programmatic
3. **Plugin Architecture** — extensible с custom appenders, layouts, filters
4. **Security** — protection от vulnerabilities, safe configuration
5. **Cloud Ready** — native support для cloud environments
6. **Backwards Compatible** — bridge для Log4j 1.x migration
7. **Monitoring** — JMX support, status logging

### Архитектурные преимущества:

#### Developer Experience:
- **Easy Configuration** — multiple formats, Spring integration
- **IDE Support** — excellent tooling support
- **Documentation** — comprehensive docs и examples
- **Community** — active development и support

#### Runtime Features:
- **Async Processing** — non-blocking logging operations
- **Context Awareness** — ThreadContext для correlation
- **Filtering** — advanced filtering capabilities
- **Extensible** — plugin system для customization

### Когда использовать Log4j 2:

✅ **Enterprise Applications** — large-scale, high-performance systems
✅ **Cloud Applications** — cloud-native logging requirements
✅ **High-Throughput Systems** — applications with high logging volume
✅ **Complex Routing** — advanced log routing requirements
✅ **Security Sensitive** — applications requiring secure logging
✅ **Migration Projects** — upgrading from Log4j 1.x
✅ **Custom Logging** — need for custom appenders/layouts
✅ **Monitoring Integration** — advanced monitoring requirements

### Когда НЕ использовать:

❌ **Simple Applications** — для basic logging достаточно java.util.logging
❌ **Android Apps** — для Android лучше использовать built-in logging
❌ **Resource-constrained** — significant memory footprint
❌ **Legacy Libraries** — if all dependencies use different logging
❌ **Single-threaded** — overhead not justified for simple apps
❌ **Alternative Frameworks** — if team already standardized on Logback

### Best practices:

1. **Async Logging** — использование AsyncLogger/AsyncAppender для performance
2. **Proper Configuration** — environment-specific configurations
3. **ThreadContext Management** — правильное управление context
4. **Security** — безопасное логирование sensitive данных
5. **Monitoring** — использование JMX для runtime monitoring
6. **Migration** — постепенная миграция с bridge libraries
7. **Testing** — тестирование logging configurations
8. **Performance** — оптимизация для конкретных сценариев использования

Log4j 2 остается одним из лучших выборов для enterprise Java logging, предоставляя мощные возможности для самых требовательных приложений. Правильное использование Log4j 2 позволяет создавать надежные и наблюдаемые системы с гибкой конфигурацией логирования. 🚀
