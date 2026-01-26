# Logback для Java

Комплексное руководство по Logback: мощной и гибкой реализации SLF4J с поддержкой advanced appenders, фильтров, MDC и production-ready features.

**Дата последнего обновления:** 2026-01-21

## Полезные ссылки

### Официальная документация
- [Logback Documentation](https://logback.qos.ch/documentation.html)
- [Logback Manual](https://logback.qos.ch/manual/)
- [Logback GitHub](https://github.com/qos-ch/logback)

### QOS.ch
- [Logback Configuration](https://logback.qos.ch/manual/configuration.html)
- [Logback Architecture](https://logback.qos.ch/manual/architecture.html)
- [Logback Cookbook](https://logback.qos.ch/cookbook/)

### Статьи и туториалы
- [Logback Configuration Guide](https://www.baeldung.com/logback)
- [Logback Async Logging](https://www.baeldung.com/logback-asynchronous-logging)
- [Logback Filters](https://www.baeldung.com/logback-filters)

### См. также
- `logging-basics.md` - Основы логирования
- `slf4j.md` - SLF4J facade
- `structured-logging.md` - Структурированное логирование

## Содержание

- [Введение в Logback](#введение-в-logback)
- [Архитектура Logback](#архитектура-logback)
- [Configuration](#configuration)
- [Appenders](#appenders)
- [Encoders и Layouts](#encoders-и-layouts)
- [Filters](#filters)
- [MDC и Markers](#mdc-и-markers)
- [TurboFilters](#turbofilters)
- [Variable substitution](#variable-substitution)
- [Conditional configuration](#conditional-configuration)
- [JMX configuration](#jmx-configuration)
- [Performance tuning](#performance-tuning)
- [Testing](#testing)
- [Best Practices](#best-practices)
- [Troubleshooting](#troubleshooting)
- [Заключение](#заключение)

## Введение в Logback

**Logback** — это современная реализация SLF4J, разработанная как преемник Log4j. Logback предоставляет высокую производительность, гибкую конфигурацию и богатый набор функций для enterprise приложений.

### Почему Logback?

Logback решает проблемы традиционных logging фреймворков:

1. **Performance** — до 10x быстрее Log4j
2. **Configuration** — XML и Groovy конфигурация
3. **Features** — MDC, Markers, Filters, TurboFilters
4. **Memory efficiency** — эффективное использование памяти
5. **Extensibility** — легко расширяемый
6. **Spring Boot integration** — seamless интеграция
7. **Production ready** — проверен в production средах

### Logback vs Log4j

| Feature | Logback | Log4j 1.x | Log4j 2.x |
|---------|---------|-----------|-----------|
| **Performance** | ✅ Fast | ❌ Slow | ✅ Fast |
| **Configuration** | ✅ XML/Groovy | ✅ XML/Properties | ✅ XML/JSON/YAML |
| **Async Logging** | ✅ Native | ❌ Manual | ✅ Native |
| **Filters** | ✅ Advanced | ❌ Basic | ✅ Advanced |
| **MDC Support** | ✅ Full | ✅ Full | ✅ Full |
| **Memory Usage** | ✅ Efficient | ❌ High | ✅ Efficient |
| **Maintenance** | ✅ Active | ❌ EOL | ✅ Active |

## Архитектура Logback

### Core components

#### Logger hierarchy
```
Root Logger (level: INFO)
├── com
│   ├── com.example (level: DEBUG)
│   │   ├── com.example.service (inherited DEBUG)
│   │   ├── com.example.controller (inherited DEBUG)
│   │   └── com.example.repository (level: WARN)
│   └── org.springframework (level: WARN)
└── org.hibernate (level: ERROR)
```

#### Logger, Appender, Encoder
```java
// Logger - captures logging requests
Logger logger = LoggerFactory.getLogger("com.example");

// Appender - delivers log events to destination
ConsoleAppender consoleAppender = new ConsoleAppender();

// Encoder - formats log events
PatternLayoutEncoder encoder = new PatternLayoutEncoder();
encoder.setPattern("%d{yyyy-MM-dd HH:mm:ss.SSS} [%thread] %-5level %logger{36} - %msg%n");
```

### LoggerContext

```java
public class LoggerContextExample {
    
    public void demonstrateContext() {
        // Get the logger context
        LoggerContext context = (LoggerContext) LoggerFactory.getILoggerFactory();
        
        // Get root logger
        Logger rootLogger = context.getLogger(Logger.ROOT_LOGGER_NAME);
        
        // Create logger
        Logger logger = context.getLogger("com.example");
        
        // Logger hierarchy
        System.out.println("Logger name: " + logger.getName());
        System.out.println("Parent: " + logger.getParent().getName());
        System.out.println("Level: " + logger.getLevel());
        System.out.println("Effective level: " + logger.getEffectiveLevel());
        
        // List all loggers
        for (Logger log : context.getLoggerList()) {
            System.out.println(log.getName() + " -> " + log.getLevel());
        }
    }
}
```

## Configuration

### XML configuration

#### Basic configuration
```xml
<?xml version="1.0" encoding="UTF-8"?>
<configuration>
    
    <!-- Property definitions -->
    <property name="LOG_HOME" value="/app/logs"/>
    <property name="LOG_PATTERN" 
              value="%d{yyyy-MM-dd HH:mm:ss.SSS} [%thread] %-5level %logger{36} - %msg%n"/>
    
    <!-- Console appender -->
    <appender name="CONSOLE" class="ch.qos.logback.core.ConsoleAppender">
        <encoder>
            <pattern>${LOG_PATTERN}</pattern>
        </encoder>
        <filter class="ch.qos.logback.classic.filter.ThresholdFilter">
            <level>DEBUG</level>
        </filter>
    </appender>
    
    <!-- File appender -->
    <appender name="FILE" class="ch.qos.logback.core.rolling.RollingFileAppender">
        <file>${LOG_HOME}/application.log</file>
        
        <rollingPolicy class="ch.qos.logback.core.rolling.TimeBasedRollingPolicy">
            <fileNamePattern>${LOG_HOME}/application.%d{yyyy-MM-dd}.%i.log</fileNamePattern>
            <maxFileSize>10MB</maxFileSize>
            <maxHistory>30</maxHistory>
            <totalSizeCap>1GB</totalSizeCap>
        </rollingPolicy>
        
        <encoder>
            <pattern>${LOG_PATTERN}</pattern>
        </encoder>
    </appender>
    
    <!-- Async appender -->
    <appender name="ASYNC" class="ch.qos.logback.classic.AsyncAppender">
        <discardingThreshold>20</discardingThreshold>
        <queueSize>512</queueSize>
        <appender-ref ref="FILE"/>
    </appender>
    
    <!-- Logger configuration -->
    <logger name="com.example" level="DEBUG" additivity="false">
        <appender-ref ref="CONSOLE"/>
        <appender-ref ref="ASYNC"/>
    </logger>
    
    <logger name="org.springframework" level="INFO"/>
    <logger name="org.hibernate" level="WARN"/>
    
    <!-- Root logger -->
    <root level="INFO">
        <appender-ref ref="CONSOLE"/>
    </root>
    
</configuration>
```

#### Programmatic configuration
```java
public class ProgrammaticConfig {
    
    public static void configureLogback() {
        LoggerContext context = (LoggerContext) LoggerFactory.getILoggerFactory();
        
        // Reset configuration
        context.reset();
        
        // Create console appender
        ConsoleAppender consoleAppender = new ConsoleAppender();
        consoleAppender.setContext(context);
        consoleAppender.setName("CONSOLE");
        
        // Create encoder
        PatternLayoutEncoder encoder = new PatternLayoutEncoder();
        encoder.setContext(context);
        encoder.setPattern("%d{yyyy-MM-dd HH:mm:ss.SSS} [%thread] %-5level %logger{36} - %msg%n");
        encoder.start();
        
        consoleAppender.setEncoder(encoder);
        consoleAppender.start();
        
        // Get root logger and add appender
        Logger rootLogger = context.getLogger(Logger.ROOT_LOGGER_NAME);
        rootLogger.setLevel(Level.INFO);
        rootLogger.addAppender(consoleAppender);
        
        // Configure specific logger
        Logger exampleLogger = context.getLogger("com.example");
        exampleLogger.setLevel(Level.DEBUG);
        exampleLogger.setAdditivity(false);
        exampleLogger.addAppender(consoleAppender);
    }
}
```

### Spring Boot integration

#### application.yml
```yaml
logging:
  level:
    root: INFO
    com.example: DEBUG
    org.springframework: WARN
    
  pattern:
    console: "%d{yyyy-MM-dd HH:mm:ss.SSS} [%thread] %-5level %logger{36} - %msg%n"
    file: "%d{yyyy-MM-dd HH:mm:ss.SSS} [%thread] %-5level %logger{36} - %msg%n"
    
  file:
    name: logs/application.log
    max-size: 10MB
    max-history: 30
    
  logback:
    rollingpolicy:
      max-file-size: 10MB
      max-history: 30
```

#### Custom logback-spring.xml
```xml
<?xml version="1.0" encoding="UTF-8"?>
<configuration>
    
    <!-- Spring profiles -->
    <springProfile name="dev">
        <logger name="com.example" level="DEBUG"/>
        <appender-ref ref="CONSOLE"/>
    </springProfile>
    
    <springProfile name="prod">
        <logger name="com.example" level="INFO"/>
        <appender-ref ref="FILE"/>
        <appender-ref ref="ERROR_FILE"/>
    </springProfile>
    
    <!-- Common configuration -->
    <property name="LOG_PATTERN" 
              value="%d{yyyy-MM-dd HH:mm:ss.SSS} [%thread] %-5level %logger{36} - %msg%n"/>
    
    <appender name="CONSOLE" class="ch.qos.logback.core.ConsoleAppender">
        <encoder>
            <pattern>${LOG_PATTERN}</pattern>
        </encoder>
    </appender>
    
    <appender name="FILE" class="ch.qos.logback.core.rolling.RollingFileAppender">
        <file>logs/application.log</file>
        <rollingPolicy class="ch.qos.logback.core.rolling.TimeBasedRollingPolicy">
            <fileNamePattern>logs/application.%d{yyyy-MM-dd}.%i.log</fileNamePattern>
            <maxFileSize>10MB</maxFileSize>
        </rollingPolicy>
        <encoder>
            <pattern>${LOG_PATTERN}</pattern>
        </encoder>
    </appender>
    
    <appender name="ERROR_FILE" class="ch.qos.logback.core.FileAppender">
        <file>logs/error.log</file>
        <filter class="ch.qos.logback.classic.filter.LevelFilter">
            <level>ERROR</level>
            <onMatch>ACCEPT</onMatch>
            <onMismatch>DENY</onMismatch>
        </filter>
        <encoder>
            <pattern>${LOG_PATTERN}</pattern>
        </encoder>
    </appender>
    
</configuration>
```

## Appenders

### ConsoleAppender

```xml
<appender name="CONSOLE" class="ch.qos.logback.core.ConsoleAppender">
    <encoder>
        <pattern>%d{yyyy-MM-dd HH:mm:ss.SSS} [%thread] %-5level %logger{36} - %msg%n</pattern>
    </encoder>
    
    <!-- Target: System.out or System.err -->
    <target>System.out</target>
    
    <!-- Immediate flush -->
    <immediateFlush>true</immediateFlush>
    
    <!-- Filters -->
    <filter class="ch.qos.logback.classic.filter.ThresholdFilter">
        <level>DEBUG</level>
    </filter>
</appender>
```

### FileAppender

```xml
<appender name="FILE" class="ch.qos.logback.core.FileAppender">
    <file>logs/application.log</file>
    
    <!-- Append to existing file -->
    <append>true</append>
    
    <!-- Encoder -->
    <encoder>
        <pattern>%d{yyyy-MM-dd HH:mm:ss.SSS} [%thread] %-5level %logger{36} - %msg%n</pattern>
        <charset>UTF-8</charset>
    </encoder>
    
    <!-- Prudent mode (for multi-JVM environments) -->
    <prudent>true</prudent>
</appender>
```

### RollingFileAppender

#### TimeBasedRollingPolicy
```xml
<appender name="ROLLING_FILE" class="ch.qos.logback.core.rolling.RollingFileAppender">
    <file>logs/application.log</file>
    
    <rollingPolicy class="ch.qos.logback.core.rolling.TimeBasedRollingPolicy">
        <!-- Daily rollover -->
        <fileNamePattern>logs/application.%d{yyyy-MM-dd}.%i.log</fileNamePattern>
        
        <!-- Max file size before rollover -->
        <maxFileSize>10MB</maxFileSize>
        
        <!-- Max history -->
        <maxHistory>30</maxHistory>
        
        <!-- Total size cap -->
        <totalSizeCap>1GB</totalSizeCap>
    </rollingPolicy>
    
    <encoder>
        <pattern>%d{yyyy-MM-dd HH:mm:ss.SSS} [%thread] %-5level %logger{36} - %msg%n</pattern>
    </encoder>
</appender>
```

#### SizeAndTimeBasedRollingPolicy
```xml
<appender name="SIZE_TIME_ROLLING" class="ch.qos.logback.core.rolling.RollingFileAppender">
    <file>logs/application.log</file>
    
    <rollingPolicy class="ch.qos.logback.core.rolling.SizeAndTimeBasedRollingPolicy">
        <!-- Rollover daily or when size exceeds -->
        <fileNamePattern>logs/application.%d{yyyy-MM-dd}.%i.log</fileNamePattern>
        
        <!-- Max file size -->
        <maxFileSize>50MB</maxFileSize>
        
        <!-- Max history -->
        <maxHistory>30</maxHistory>
        
        <!-- Total size cap -->
        <totalSizeCap>2GB</totalSizeCap>
    </rollingPolicy>
    
    <encoder>
        <pattern>%d{yyyy-MM-dd HH:mm:ss.SSS} [%thread] %-5level %logger{36} - %msg%n</pattern>
    </encoder>
</appender>
```

### AsyncAppender

```xml
<appender name="ASYNC" class="ch.qos.logback.classic.AsyncAppender">
    <!-- Delegate appender -->
    <appender-ref ref="ROLLING_FILE"/>
    
    <!-- Queue configuration -->
    <queueSize>512</queueSize>
    
    <!-- Discarding threshold (discard INFO/DEBUG when queue 80% full) -->
    <discardingThreshold>20</discardingThreshold>
    
    <!-- Include caller data -->
    <includeCallerData>true</includeCallerData>
    
    <!-- Never block -->
    <neverBlock>true</neverBlock>
</appender>
```

### Custom Appender

```java
public class CustomAppender extends AppenderBase<ILoggingEvent> {
    
    @Override
    protected void append(ILoggingEvent event) {
        // Custom logic to handle log event
        String message = event.getFormattedMessage();
        Level level = event.getLevel();
        String loggerName = event.getLoggerName();
        
        // Send to external system, database, etc.
        sendToExternalSystem(level, loggerName, message, event.getThrowableProxy());
    }
    
    private void sendToExternalSystem(Level level, String logger, String message, 
                                    IThrowableProxy throwable) {
        // Implementation for sending logs
        System.out.println(String.format("[%s] %s - %s", level, logger, message));
        
        if (throwable != null) {
            System.out.println("Exception: " + throwable.getMessage());
        }
    }
}

// Configuration
<appender name="CUSTOM" class="com.example.CustomAppender">
    <!-- Custom properties -->
</appender>
```

## Encoders и Layouts

### PatternLayoutEncoder

```xml
<encoder class="ch.qos.logback.classic.encoder.PatternLayoutEncoder">
    <pattern>%d{yyyy-MM-dd HH:mm:ss.SSS} [%thread] %-5level %logger{36} - %msg%n</pattern>
    <charset>UTF-8</charset>
</encoder>
```

### Pattern reference

| Pattern | Description | Example |
|---------|-------------|---------|
| `%d{pattern}` | Date/time | `2023-12-01 10:30:45.123` |
| `%thread` | Thread name | `main` |
| `%-5level` | Log level (padded) | `INFO ` |
| `%logger{length}` | Logger name | `com.example.MyClass` |
| `%msg` | Log message | `User logged in` |
| `%n` | Newline | |
| `%X{key}` | MDC value | `userId=123` |
| `%replace(text){pattern}{replacement}` | String replacement | |
| `%highlight(pattern){color}` | Colored output | |

### Advanced patterns

```xml
<!-- Detailed pattern -->
<pattern>
    %d{yyyy-MM-dd HH:mm:ss.SSS} 
    [%thread] 
    %-5level 
    %logger{36} 
    [%X{userId:-}] 
    [%X{requestId:-}] 
    - %msg%n
</pattern>

<!-- Colored console pattern -->
<pattern>
    %d{yyyy-MM-dd HH:mm:ss.SSS} 
    %highlight(%-5level) 
    [%thread] 
    %cyan(%logger{36}) 
    - %msg%n
</pattern>

<!-- JSON pattern -->
<pattern>
    {"timestamp":"%d{yyyy-MM-dd HH:mm:ss.SSS}",
     "level":"%level",
     "thread":"%thread",
     "logger":"%logger",
     "message":"%msg"}
</pattern>
```

### Custom Layout

```java
public class CustomLayout extends LayoutBase<ILoggingEvent> {
    
    @Override
    public String doLayout(ILoggingEvent event) {
        StringBuilder sb = new StringBuilder();
        
        // Timestamp
        sb.append(event.getTimeStamp()).append(" ");
        
        // Level
        sb.append("[").append(event.getLevel()).append("] ");
        
        // Logger
        sb.append(event.getLoggerName()).append(" - ");
        
        // Message
        sb.append(event.getFormattedMessage());
        
        // Exception
        if (event.getThrowableProxy() != null) {
            sb.append("\n").append(event.getThrowableProxy().getMessage());
        }
        
        sb.append("\n");
        return sb.toString();
    }
}

// Usage
<encoder class="ch.qos.logback.core.encoder.LayoutWrappingEncoder">
    <layout class="com.example.CustomLayout"/>
</encoder>
```

## Filters

### ThresholdFilter

```xml
<appender name="ERROR_FILE" class="ch.qos.logback.core.FileAppender">
    <file>logs/error.log</file>
    
    <!-- Only ERROR level and above -->
    <filter class="ch.qos.logback.classic.filter.ThresholdFilter">
        <level>ERROR</level>
    </filter>
    
    <encoder>
        <pattern>%d{yyyy-MM-dd HH:mm:ss.SSS} [%thread] %-5level %logger{36} - %msg%n</pattern>
    </encoder>
</appender>
```

### LevelFilter

```xml
<appender name="WARN_APPENDER" class="ch.qos.logback.core.ConsoleAppender">
    <filter class="ch.qos.logback.classic.filter.LevelFilter">
        <level>WARN</level>
        <onMatch>ACCEPT</onMatch>
        <onMismatch>DENY</onMismatch>
    </filter>
    
    <encoder>
        <pattern>%d{yyyy-MM-dd HH:mm:ss.SSS} [%thread] %-5level %logger{36} - %msg%n</pattern>
    </encoder>
</appender>
```

### Custom Filter

```java
public class CustomFilter extends Filter<ILoggingEvent> {
    
    @Override
    public FilterReply decide(ILoggingEvent event) {
        // Filter based on custom logic
        String message = event.getFormattedMessage();
        
        // Skip health check logs
        if (message.contains("health check")) {
            return FilterReply.DENY;
        }
        
        // Accept error logs from specific package
        if (event.getLevel().equals(Level.ERROR) && 
            event.getLoggerName().startsWith("com.example.business")) {
            return FilterReply.ACCEPT;
        }
        
        // Neutral for others
        return FilterReply.NEUTRAL;
    }
}

// Configuration
<filter class="com.example.CustomFilter"/>
```

### EvaluatorFilter

```java
public class SlowQueryFilter extends Filter<ILoggingEvent> {
    
    @Override
    public FilterReply decide(ILoggingEvent event) {
        // Check if message contains slow query warning
        String message = event.getFormattedMessage();
        
        if (message.contains("Slow query") && event.getLevel().equals(Level.WARN)) {
            // Could send alert or additional processing
            handleSlowQuery(event);
            return FilterReply.ACCEPT;
        }
        
        return FilterReply.NEUTRAL;
    }
    
    private void handleSlowQuery(ILoggingEvent event) {
        // Send to monitoring system, etc.
        System.out.println("Slow query detected: " + event.getFormattedMessage());
    }
}

// Configuration
<filter class="com.example.SlowQueryFilter"/>
```

## MDC и Markers

### MDC (Mapped Diagnostic Context)

```java
public class MdcExample {
    
    private static final Logger logger = LoggerFactory.getLogger(MdcExample.class);
    
    public void demonstrateMdc() {
        // Add context
        MDC.put("userId", "12345");
        MDC.put("sessionId", "session-abc");
        MDC.put("requestId", UUID.randomUUID().toString());
        
        logger.info("User logged in");
        logger.debug("Processing user request");
        
        // Nested operation
        processSubOperation();
        
        // Clean up
        MDC.clear();
    }
    
    private void processSubOperation() {
        MDC.put("operation", "validate-payment");
        logger.info("Starting validation");
        
        // Operation logic
        
        MDC.remove("operation");
    }
}
```

#### MDC in pattern
```xml
<pattern>
    %d{yyyy-MM-dd HH:mm:ss.SSS} 
    [%thread] 
    %-5level 
    %logger{36} 
    [%X{userId:-}] 
    [%X{requestId:-}] 
    - %msg%n
</pattern>
```

### Markers

```java
public class MarkerExample {
    
    private static final Logger logger = LoggerFactory.getLogger(MarkerExample.class);
    
    // Define markers
    private static final Marker SECURITY_MARKER = MarkerFactory.getMarker("SECURITY");
    private static final Marker PERFORMANCE_MARKER = MarkerFactory.getMarker("PERFORMANCE");
    private static final Marker BUSINESS_MARKER = MarkerFactory.getMarker("BUSINESS");
    
    public void demonstrateMarkers() {
        // Security events
        logger.info(SECURITY_MARKER, "User {} logged in from {}", "john", "192.168.1.1");
        
        // Performance monitoring
        logger.warn(PERFORMANCE_MARKER, "Slow query detected: {} took {}ms", "SELECT * FROM users", 2500);
        
        // Business events
        logger.info(BUSINESS_MARKER, "Order {} created for customer {}", 12345, "john@example.com");
    }
}
```

#### Marker filtering
```java
public class MarkerFilter extends Filter<ILoggingEvent> {
    
    private final Marker targetMarker;
    
    public MarkerFilter(Marker targetMarker) {
        this.targetMarker = targetMarker;
    }
    
    @Override
    public FilterReply decide(ILoggingEvent event) {
        if (event.getMarker() != null && event.getMarker().contains(targetMarker)) {
            return FilterReply.ACCEPT;
        }
        return FilterReply.NEUTRAL;
    }
}

// Configuration
<filter class="com.example.MarkerFilter">
    <marker>SECURITY</marker>
</filter>
```

## TurboFilters

### TurboFilter basics

```java
public class CustomTurboFilter extends TurboFilter {
    
    @Override
    public FilterReply decide(Marker marker, Logger logger, Level level, String format, 
                            Object[] params, Throwable t) {
        
        // Fast filtering before event creation
        if (level == Level.DEBUG && logger.getName().startsWith("com.example.debug")) {
            return FilterReply.DENY;
        }
        
        // Accept security events
        if (marker != null && "SECURITY".equals(marker.getName())) {
            return FilterReply.ACCEPT;
        }
        
        return FilterReply.NEUTRAL;
    }
}

// Configuration
<turboFilter class="com.example.CustomTurboFilter"/>
```

### Performance turbo filter

```java
public class PerformanceTurboFilter extends TurboFilter {
    
    private final Map<String, Long> lastLogTime = new ConcurrentHashMap<>();
    
    @Override
    public FilterReply decide(Marker marker, Logger logger, Level level, String format, 
                            Object[] params, Throwable t) {
        
        String loggerName = logger.getName();
        
        // Throttle frequent logs
        if ("com.example.frequent".equals(loggerName)) {
            long now = System.currentTimeMillis();
            Long lastTime = lastLogTime.get(loggerName);
            
            if (lastTime != null && (now - lastTime) < 1000) { // Less than 1 second
                return FilterReply.DENY;
            }
            
            lastLogTime.put(loggerName, now);
        }
        
        return FilterReply.NEUTRAL;
    }
}
```

## Variable substitution

### System properties

```xml
<configuration>
    <!-- System property substitution -->
    <property name="LOG_HOME" value="${LOG_HOME:-/tmp/logs}"/>
    <property name="LOG_LEVEL" value="${LOG_LEVEL:-INFO}"/>
    
    <appender name="FILE" class="ch.qos.logback.core.FileAppender">
        <file>${LOG_HOME}/application.log</file>
        <encoder>
            <pattern>%d{yyyy-MM-dd HH:mm:ss.SSS} [%thread] %-5level %logger{36} - %msg%n</pattern>
        </encoder>
    </appender>
    
    <root level="${LOG_LEVEL}">
        <appender-ref ref="FILE"/>
    </root>
</configuration>
```

### Environment variables

```xml
<configuration>
    <!-- Environment variable substitution -->
    <property name="APP_NAME" value="${APP_NAME:-myapp}"/>
    <property name="ENV" value="${ENV:-dev}"/>
    
    <appender name="ROLLING_FILE" class="ch.qos.logback.core.rolling.RollingFileAppender">
        <file>logs/${APP_NAME}-${ENV}.log</file>
        <rollingPolicy class="ch.qos.logback.core.rolling.TimeBasedRollingPolicy">
            <fileNamePattern>logs/${APP_NAME}-${ENV}.%d{yyyy-MM-dd}.%i.log</fileNamePattern>
        </rollingPolicy>
        <encoder>
            <pattern>%d{yyyy-MM-dd HH:mm:ss.SSS} [${APP_NAME}] [%thread] %-5level %logger{36} - %msg%n</pattern>
        </encoder>
    </appender>
</configuration>
```

### Custom property source

```java
public class CustomPropertyDefiner implements PropertyDefiner {
    
    @Override
    public String getPropertyValue() {
        // Custom logic to define property value
        return System.getProperty("app.version", "1.0.0");
    }
}

// Configuration
<define class="com.example.CustomPropertyDefiner">
    <property name="APP_VERSION"/>
</define>

<!-- Usage -->
<pattern>%d{yyyy-MM-dd HH:mm:ss.SSS} [v${APP_VERSION}] [%thread] %-5level %logger{36} - %msg%n</pattern>
```

## Conditional configuration

### Basic conditional

```xml
<configuration>
    <!-- Conditional configuration based on property -->
    <if condition='property("ENV").equals("dev")'>
        <then>
            <logger name="com.example" level="DEBUG"/>
        </then>
        <else>
            <logger name="com.example" level="INFO"/>
        </else>
    </if>
    
    <root level="INFO">
        <appender-ref ref="CONSOLE"/>
    </root>
</configuration>
```

### Complex conditions

```xml
<configuration>
    <!-- Multiple conditions -->
    <if condition='property("ENV").equals("prod") &amp;&amp; property("MONITORING").equals("true")'>
        <then>
            <appender-ref ref="MONITORING_APPENDER"/>
        </then>
    </if>
    
    <!-- Nested conditions -->
    <if condition='property("LOG_LEVEL").equals("TRACE")'>
        <then>
            <logger name="com.example" level="TRACE"/>
            <if condition='property("DETAILED").equals("true")'>
                <then>
                    <logger name="com.example.internal" level="TRACE"/>
                </then>
            </if>
        </then>
    </if>
</configuration>
```

### Spring profile conditions

```xml
<configuration>
    <!-- Spring profile conditions -->
    <springProfile name="dev">
        <logger name="com.example" level="DEBUG"/>
        <appender-ref ref="CONSOLE"/>
    </springProfile>
    
    <springProfile name="prod">
        <logger name="com.example" level="INFO"/>
        <appender-ref ref="FILE"/>
        <appender-ref ref="ERROR_FILE"/>
    </springProfile>
    
    <!-- Multiple profiles -->
    <springProfile name="dev,test">
        <logger name="com.example.test" level="DEBUG"/>
    </springProfile>
    
    <!-- Profile expression -->
    <springProfile name="!prod">
        <logger name="org.springframework" level="DEBUG"/>
    </springProfile>
</configuration>
```

## JMX configuration

### JMX configurator

```xml
<configuration>
    <!-- Enable JMX -->
    <jmxConfigurator/>
    
    <!-- Standard configuration -->
    <appender name="CONSOLE" class="ch.qos.logback.core.ConsoleAppender">
        <encoder>
            <pattern>%d{yyyy-MM-dd HH:mm:ss.SSS} [%thread] %-5level %logger{36} - %msg%n</pattern>
        </encoder>
    </appender>
    
    <root level="INFO">
        <appender-ref ref="CONSOLE"/>
    </root>
</configuration>
```

### JMX monitoring

```java
public class JmxMonitoring {
    
    public void demonstrateJmx() {
        LoggerContext context = (LoggerContext) LoggerFactory.getILoggerFactory();
        
        try {
            // Connect to JMX
            JMXConnector connector = JMXConnectorFactory.connect(
                new JMXServiceURL("service:jmx:rmi:///jndi/rmi://localhost:1099/jmxrmi"));
            
            MBeanServerConnection mbsc = connector.getMBeanServerConnection();
            
            // Get logger information
            ObjectName loggerObjectName = new ObjectName("ch.qos.logback.classic:Name=default,Type=ch.qos.logback.classic.jmx.JMXConfigurator");
            
            // Get logger level
            String level = (String) mbsc.getAttribute(loggerObjectName, "LoggerLevel");
            System.out.println("Current level: " + level);
            
            // Set logger level
            mbsc.setAttribute(loggerObjectName, 
                new Attribute("LoggerLevel", "DEBUG"));
            
            connector.close();
            
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
```

## Performance tuning

### Memory optimization

#### Reuse StringBuilder
```java
public class MemoryOptimizedEncoder extends LayoutWrappingEncoder<ILoggingEvent> {
    
    private final ThreadLocal<StringBuilder> stringBuilder = 
        ThreadLocal.withInitial(() -> new StringBuilder(256));
    
    @Override
    public byte[] encode(ILoggingEvent event) {
        StringBuilder sb = stringBuilder.get();
        sb.setLength(0); // Reset
        
        // Build log message
        sb.append(event.getTimeStamp())
          .append(" [")
          .append(event.getThreadName())
          .append("] ")
          .append(event.getLevel())
          .append(" ")
          .append(event.getLoggerName())
          .append(" - ")
          .append(event.getFormattedMessage())
          .append("\n");
        
        return sb.toString().getBytes(StandardCharsets.UTF_8);
    }
}
```

#### Async logging tuning
```xml
<configuration>
    <!-- Optimized async appender -->
    <appender name="ASYNC_OPTIMIZED" class="ch.qos.logback.classic.AsyncAppender">
        <appender-ref ref="FILE"/>
        
        <!-- Large queue for high throughput -->
        <queueSize>4096</queueSize>
        
        <!-- Discard only TRACE/DEBUG when queue 95% full -->
        <discardingThreshold>5</discardingThreshold>
        
        <!-- Don't include caller data for performance -->
        <includeCallerData>false</includeCallerData>
        
        <!-- Block when queue full (better reliability) -->
        <neverBlock>false</neverBlock>
        
        <!-- Max flush time -->
        <maxFlushTime>1000</maxFlushTime>
    </appender>
</configuration>
```

### CPU optimization

#### Fast pattern layout
```xml
<!-- Use fast encoder for high-performance scenarios -->
<encoder class="ch.qos.logback.classic.encoder.PatternLayoutEncoder">
    <!-- Simple pattern for speed -->
    <pattern>%d{HH:mm:ss.SSS} %-5level [%thread] %logger{0} - %msg%n</pattern>
</encoder>
```

#### TurboFilter for performance
```java
public class PerformanceTurboFilter extends TurboFilter {
    
    @Override
    public FilterReply decide(Marker marker, Logger logger, Level level, String format, 
                            Object[] params, Throwable t) {
        
        // Fast rejection of unwanted logs
        if (level.levelInt < Level.INFO_INT && 
            !logger.getName().startsWith("com.example")) {
            return FilterReply.DENY;
        }
        
        return FilterReply.NEUTRAL;
    }
}

// Configuration
<turboFilter class="com.example.PerformanceTurboFilter"/>
```

## Testing

### Logback testing

#### Capture logs in tests
```java
public class LogbackTesting {
    
    @Test
    void testLogging() {
        // Create logger
        Logger logger = LoggerFactory.getLogger("test.logger");
        
        // Create memory appender
        MemoryAppender memoryAppender = new MemoryAppender();
        memoryAppender.setContext((LoggerContext) LoggerFactory.getILoggerFactory());
        memoryAppender.start();
        
        // Attach to logger
        ((ch.qos.logback.classic.Logger) logger).addAppender(memoryAppender);
        
        // Generate logs
        logger.info("Test message");
        logger.error("Error message", new RuntimeException("Test"));
        
        // Verify logs
        List<ILoggingEvent> events = memoryAppender.getEvents();
        assertEquals(2, events.size());
        
        ILoggingEvent infoEvent = events.get(0);
        assertEquals(Level.INFO, infoEvent.getLevel());
        assertEquals("Test message", infoEvent.getFormattedMessage());
        
        ILoggingEvent errorEvent = events.get(1);
        assertEquals(Level.ERROR, errorEvent.getLevel());
        assertEquals("Error message", errorEvent.getFormattedMessage());
        assertNotNull(errorEvent.getThrowableProxy());
    }
}
```

#### Mock appender
```java
public class MockAppender extends AppenderBase<ILoggingEvent> {
    
    private final List<ILoggingEvent> events = new ArrayList<>();
    
    @Override
    protected void append(ILoggingEvent event) {
        events.add(event);
    }
    
    public List<ILoggingEvent> getEvents() {
        return new ArrayList<>(events);
    }
    
    public void clear() {
        events.clear();
    }
    
    public List<String> getMessages() {
        return events.stream()
            .map(ILoggingEvent::getFormattedMessage)
            .collect(Collectors.toList());
    }
    
    public List<String> getMessages(Level level) {
        return events.stream()
            .filter(event -> event.getLevel().equals(level))
            .map(ILoggingEvent::getFormattedMessage)
            .collect(Collectors.toList());
    }
}

// Test usage
@Test
void testWithMockAppender() {
    Logger logger = LoggerFactory.getLogger("test");
    MockAppender mockAppender = new MockAppender();
    
    // Attach mock appender
    ((ch.qos.logback.classic.Logger) logger).addAppender(mockAppender);
    mockAppender.start();
    
    // Generate logs
    logger.info("Info message");
    logger.warn("Warning message");
    logger.error("Error message");
    
    // Verify
    assertEquals(3, mockAppender.getEvents().size());
    assertEquals(1, mockAppender.getMessages(Level.ERROR).size());
    assertTrue(mockAppender.getMessages().contains("Warning message"));
}
```

### Configuration testing

#### Test configuration loading
```java
public class ConfigurationTesting {
    
    @Test
    void testConfigurationLoading() throws JoranException {
        LoggerContext context = (LoggerContext) LoggerFactory.getILoggerFactory();
        context.reset();
        
        // Load configuration
        JoranConfigurator configurator = new JoranConfigurator();
        configurator.setContext(context);
        
        // Load from classpath
        configurator.doConfigure(getClass().getResource("/logback-test.xml"));
        
        // Verify configuration
        Logger logger = context.getLogger("com.example");
        assertEquals(Level.DEBUG, logger.getLevel());
        
        // Check appenders
        Logger rootLogger = context.getLogger(Logger.ROOT_LOGGER_NAME);
        assertEquals(1, rootLogger.getAppenderCount());
    }
    
    @Test
    void testProgrammaticConfiguration() {
        LoggerContext context = (LoggerContext) LoggerFactory.getILoggerFactory();
        context.reset();
        
        // Programmatic setup
        ConsoleAppender consoleAppender = new ConsoleAppender();
        consoleAppender.setContext(context);
        consoleAppender.setName("CONSOLE");
        
        PatternLayoutEncoder encoder = new PatternLayoutEncoder();
        encoder.setContext(context);
        encoder.setPattern("%msg%n");
        encoder.start();
        
        consoleAppender.setEncoder(encoder);
        consoleAppender.start();
        
        Logger rootLogger = context.getLogger(Logger.ROOT_LOGGER_NAME);
        rootLogger.addAppender(consoleAppender);
        rootLogger.setLevel(Level.INFO);
        
        // Verify
        assertEquals(Level.INFO, rootLogger.getLevel());
        assertNotNull(rootLogger.getAppender("CONSOLE"));
    }
}
```

## Best Practices

### 1. Configuration management

#### Environment-specific configurations
```xml
<!-- Separate config files -->
<!-- logback-dev.xml -->
<configuration>
    <include resource="logback-common.xml"/>
    
    <logger name="com.example" level="DEBUG"/>
    <root level="DEBUG">
        <appender-ref ref="CONSOLE"/>
    </root>
</configuration>

<!-- logback-prod.xml -->
<configuration>
    <include resource="logback-common.xml"/>
    
    <logger name="com.example" level="INFO"/>
    <root level="INFO">
        <appender-ref ref="ROLLING_FILE"/>
        <appender-ref ref="ERROR_FILE"/>
    </root>
</configuration>

<!-- logback-common.xml -->
<included>
    <property name="LOG_PATTERN" value="%d{yyyy-MM-dd HH:mm:ss.SSS} [%thread] %-5level %logger{36} - %msg%n"/>
    
    <appender name="CONSOLE" class="ch.qos.logback.core.ConsoleAppender">
        <encoder>
            <pattern>${LOG_PATTERN}</pattern>
        </encoder>
    </appender>
    
    <!-- Other common configuration -->
</included>
```

#### Configuration validation
```java
@Configuration
public class LogbackValidation {
    
    @PostConstruct
    public void validateConfiguration() {
        LoggerContext context = (LoggerContext) LoggerFactory.getILoggerFactory();
        
        // Check required loggers
        Logger businessLogger = context.getLogger("com.example.business");
        if (businessLogger.getLevel() == null) {
            throw new IllegalStateException("Business logger level not configured");
        }
        
        // Check required appenders
        Logger rootLogger = context.getLogger(Logger.ROOT_LOGGER_NAME);
        if (rootLogger.getAppender("FILE") == null && rootLogger.getAppender("CONSOLE") == null) {
            throw new IllegalStateException("No output appenders configured");
        }
        
        // Validate file permissions
        validateLogFilePermissions();
    }
    
    private void validateLogFilePermissions() {
        // Implementation to check log file permissions
    }
}
```

### 2. Performance best practices

#### Async logging configuration
```xml
<configuration>
    <!-- Performance-optimized async setup -->
    <appender name="ASYNC_FILE" class="ch.qos.logback.classic.AsyncAppender">
        <appender-ref ref="ROLLING_FILE"/>
        
        <!-- Large queue for high throughput -->
        <queueSize>8192</queueSize>
        
        <!-- Discard threshold -->
        <discardingThreshold>10</discardingThreshold>
        
        <!-- Performance settings -->
        <includeCallerData>false</includeCallerData>
        <neverBlock>true</neverBlock>
    </appender>
    
    <root level="INFO">
        <appender-ref ref="ASYNC_FILE"/>
    </root>
</configuration>
```

#### Memory-efficient patterns
```xml
<!-- Avoid expensive operations in patterns -->
<encoder>
    <!-- Good: static pattern -->
    <pattern>%d{yyyy-MM-dd HH:mm:ss.SSS} [%thread] %-5level %logger{36} - %msg%n</pattern>
</encoder>

<!-- Avoid: dynamic patterns that allocate memory -->
<!-- <pattern>%d{yyyy-MM-dd HH:mm:ss.SSS} [%thread] %-5level %logger{36} - %replace(%msg){'\n','\\n'}%n</pattern> -->
```

### 3. Security considerations

#### Safe logging practices
```java
public class SecureLogging {
    
    private static final Logger logger = LoggerFactory.getLogger(SecureLogging.class);
    
    public void logUserAction(User user, String action) {
        // Safe: don't log sensitive data
        logger.info("User {} performed action: {}", user.getId(), action);
        
        // Dangerous: never log passwords, tokens, etc.
        // logger.info("User {} logged in with password: {}", username, password);
    }
    
    public void logPayment(Payment payment) {
        // Mask sensitive data
        String maskedCard = maskCardNumber(payment.getCardNumber());
        logger.info("Payment processed: amount={}, card={}", 
                   payment.getAmount(), maskedCard);
    }
    
    public void logApiCall(String url, Map<String, String> headers) {
        // Remove sensitive headers
        Map<String, String> safeHeaders = new HashMap<>(headers);
        safeHeaders.remove("authorization");
        safeHeaders.remove("x-api-key");
        safeHeaders.remove("cookie");
        
        logger.debug("API call to {} with headers: {}", url, safeHeaders);
    }
    
    private String maskCardNumber(String cardNumber) {
        if (cardNumber == null || cardNumber.length() < 4) {
            return "****";
        }
        return "****-****-****-" + cardNumber.substring(cardNumber.length() - 4);
    }
}
```

#### Audit logging
```java
public class AuditLogger {
    
    private static final Logger auditLogger = LoggerFactory.getLogger("AUDIT");
    private static final Marker AUDIT_MARKER = MarkerFactory.getMarker("AUDIT");
    
    public void logAuditEvent(String userId, String action, String resource, 
                             Map<String, Object> details) {
        MDC.put("userId", userId);
        MDC.put("action", action);
        MDC.put("resource", resource);
        MDC.put("timestamp", Instant.now().toString());
        
        auditLogger.info(AUDIT_MARKER, "Audit event: {} performed {} on {}", 
                        userId, action, resource);
        
        // Log additional details at debug level
        if (auditLogger.isDebugEnabled()) {
            auditLogger.debug("Audit details: {}", details);
        }
        
        MDC.clear();
    }
    
    public void logSecurityEvent(String eventType, String userId, String ipAddress, 
                                Map<String, Object> context) {
        MDC.put("eventType", eventType);
        MDC.put("userId", userId);
        MDC.put("ipAddress", ipAddress);
        MDC.put("severity", "HIGH");
        
        auditLogger.warn(AUDIT_MARKER, "Security event: {} for user {} from {}", 
                        eventType, userId, ipAddress);
        
        MDC.clear();
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
    private final Map<String, Timer> operationTimers = new ConcurrentHashMap<>();
    
    @Autowired
    public LogMetricsCollector(MeterRegistry meterRegistry) {
        this.meterRegistry = meterRegistry;
    }
    
    public void recordError(String errorType) {
        Counter counter = errorCounters.computeIfAbsent(errorType, type -> 
            Counter.builder("log.errors")
                .description("Log error count")
                .tags("error_type", type)
                .register(meterRegistry));
        
        counter.increment();
    }
    
    public void recordOperationTime(String operation, long durationMs) {
        Timer timer = operationTimers.computeIfAbsent(operation, op -> 
            Timer.builder("log.operations")
                .description("Operation duration from logs")
                .tags("operation", op)
                .register(meterRegistry));
        
        timer.record(durationMs, TimeUnit.MILLISECONDS);
    }
    
    // Custom TurboFilter to collect metrics
    public static class MetricsTurboFilter extends TurboFilter {
        
        @Override
        public FilterReply decide(Marker marker, Logger logger, Level level, String format, 
                                Object[] params, Throwable t) {
            
            if (level == Level.ERROR) {
                // Record error metrics
                // (Would inject metrics collector)
            }
            
            return FilterReply.NEUTRAL;
        }
    }
}
```

## Troubleshooting

### Распространенные проблемы

#### Configuration not loading
```java
// Problem: logback.xml not found
// Solution: Check classpath and file location

public class ConfigurationCheck {
    
    public void checkConfiguration() {
        LoggerContext context = (LoggerContext) LoggerFactory.getILoggerFactory();
        
        // Check configuration status
        StatusManager statusManager = context.getStatusManager();
        List<Status> statuses = statusManager.getCopyOfStatusList();
        
        for (Status status : statuses) {
            System.out.println(status.getLevel() + ": " + status.getMessage());
        }
        
        // Check if configuration was loaded
        if (context.getConfigurationCache() == null) {
            System.err.println("No configuration loaded!");
        }
    }
}
```

#### Memory leaks
```java
// Problem: Memory leaks with appenders
// Solution: Properly close appenders

@Configuration
public class LogbackCleanup {
    
    @PreDestroy
    public void cleanup() {
        LoggerContext context = (LoggerContext) LoggerFactory.getILoggerFactory();
        
        // Stop and remove all appenders
        for (Logger logger : context.getLoggerList()) {
            for (Iterator<Appender<ILoggingEvent>> it = logger.iteratorForAppenders(); 
                 it.hasNext(); ) {
                Appender<ILoggingEvent> appender = it.next();
                appender.stop();
            }
        }
        
        // Reset context
        context.reset();
    }
}
```

#### Performance issues
```java
// Problem: Logging slowing down application
// Solution: Profile and optimize

public class LoggingProfiler {
    
    public void profileLogging() {
        Logger logger = LoggerFactory.getLogger("performance.test");
        
        // Test different logging approaches
        long startTime = System.nanoTime();
        
        // Synchronous logging
        for (int i = 0; i < 1000; i++) {
            logger.info("Test message {}", i);
        }
        
        long syncTime = System.nanoTime() - startTime;
        System.out.printf("Sync logging: %.2f ms%n", syncTime / 1e6);
        
        // Test with level check
        startTime = System.nanoTime();
        
        for (int i = 0; i < 1000; i++) {
            if (logger.isInfoEnabled()) {
                logger.info("Test message {}", i);
            }
        }
        
        long checkedTime = System.nanoTime() - startTime;
        System.out.printf("Checked logging: %.2f ms%n", checkedTime / 1e6);
    }
}
```

#### Appender failures
```java
// Problem: File appender can't write
// Solution: Check permissions and fallback

public class RobustFileAppender extends FileAppender<ILoggingEvent> {
    
    @Override
    public void start() {
        try {
            super.start();
        } catch (Exception e) {
            addError("Failed to start file appender", e);
            
            // Fallback to console
            ConsoleAppender<ILoggingEvent> consoleAppender = new ConsoleAppender<>();
            consoleAppender.setContext(getContext());
            consoleAppender.start();
            
            // Replace this appender with console appender
            LoggerContext context = (LoggerContext) getContext();
            Logger rootLogger = context.getLogger(Logger.ROOT_LOGGER_NAME);
            rootLogger.detachAppender(this);
            rootLogger.addAppender(consoleAppender);
        }
    }
}
```

### Debug techniques

#### Configuration debugging
```xml
<configuration debug="true">
    <!-- Debug attribute enables configuration logging -->
    
    <appender name="CONSOLE" class="ch.qos.logback.core.ConsoleAppender">
        <encoder>
            <pattern>%d{yyyy-MM-dd HH:mm:ss.SSS} [%thread] %-5level %logger{36} - %msg%n</pattern>
        </encoder>
    </appender>
    
    <root level="DEBUG">
        <appender-ref ref="CONSOLE"/>
    </root>
</configuration>
```

#### Runtime inspection
```java
public class LogbackInspector {
    
    public void inspectLogback() {
        LoggerContext context = (LoggerContext) LoggerFactory.getILoggerFactory();
        
        System.out.println("=== Logback Configuration ===");
        System.out.println("Configuration file: " + context.getConfigurationCache());
        
        System.out.println("\n=== Loggers ===");
        for (Logger logger : context.getLoggerList()) {
            System.out.printf("Logger: %s, Level: %s, Effective: %s%n",
                logger.getName(),
                logger.getLevel(),
                logger.getEffectiveLevel());
            
            System.out.print("  Appenders: ");
            for (Iterator<Appender<ILoggingEvent>> it = logger.iteratorForAppenders(); 
                 it.hasNext(); ) {
                Appender<ILoggingEvent> appender = it.next();
                System.out.print(appender.getName() + " ");
            }
            System.out.println();
        }
        
        System.out.println("\n=== Status Messages ===");
        StatusManager statusManager = context.getStatusManager();
        for (Status status : statusManager.getCopyOfStatusList()) {
            System.out.println(status.getLevel() + ": " + status.getMessage());
        }
    }
}
```

#### Log file analysis
```java
public class LogAnalyzer {
    
    public void analyzeLogFile(Path logFile) throws IOException {
        Map<Level, Integer> levelCounts = new HashMap<>();
        Map<String, Integer> loggerCounts = new HashMap<>();
        List<Long> timestamps = new ArrayList<>();
        
        Pattern logPattern = Pattern.compile(
            "(\\d{4}-\\d{2}-\\d{2} \\d{2}:\\d{2}:\\d{2}\\.\\d{3}) \\[([^\\]]+)\\] (\\w+) ([^\\s]+) - (.+)");
        
        try (BufferedReader reader = Files.newBufferedReader(logFile)) {
            String line;
            while ((line = reader.readLine()) != null) {
                Matcher matcher = logPattern.matcher(line);
                if (matcher.matches()) {
                    // Extract information
                    String timestamp = matcher.group(1);
                    String thread = matcher.group(2);
                    String level = matcher.group(3);
                    String logger = matcher.group(4);
                    String message = matcher.group(5);
                    
                    // Count levels
                    Level logLevel = Level.valueOf(level);
                    levelCounts.merge(logLevel, 1, Integer::sum);
                    
                    // Count loggers
                    loggerCounts.merge(logger, 1, Integer::sum);
                    
                    // Collect timestamps for rate analysis
                    try {
                        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
                        timestamps.add(format.parse(timestamp).getTime());
                    } catch (ParseException e) {
                        // Ignore timestamp parsing errors
                    }
                }
            }
        }
        
        // Print analysis
        System.out.println("Level distribution: " + levelCounts);
        System.out.println("Top loggers: " + getTopEntries(loggerCounts, 5));
        
        if (!timestamps.isEmpty()) {
            long duration = timestamps.get(timestamps.size() - 1) - timestamps.get(0);
            double rate = (double) timestamps.size() / (duration / 1000.0);
            System.out.printf("Logging rate: %.2f logs/second%n", rate);
        }
    }
    
    private <K, V extends Comparable<V>> Map<K, V> getTopEntries(Map<K, V> map, int n) {
        return map.entrySet().stream()
            .sorted(Map.Entry.<K, V>comparingByValue().reversed())
            .limit(n)
            .collect(Collectors.toMap(
                Map.Entry::getKey,
                Map.Entry::getValue,
                (e1, e2) -> e1,
                LinkedHashMap::new));
    }
}
```

## Заключение

**Logback** — это мощная и гибкая logging библиотека, которая предоставляет enterprise-grade возможности для Java приложений. Она является стандартом де-факто для SLF4J реализаций.

### Ключевые возможности:

1. **High Performance** — асинхронное логирование, эффективное использование памяти
2. **Flexible Configuration** — XML, Groovy, programmatic конфигурация
3. **Rich Features** — MDC, Markers, Filters, TurboFilters
4. **Multiple Appenders** — консоль, файлы, базы данных, внешние системы
5. **Spring Boot Integration** — seamless интеграция с Spring
6. **Extensibility** — custom appenders, filters, encoders
7. **Production Ready** — проверен в крупных enterprise системах

### Архитектурные преимущества:

#### Developer Experience:
- **Easy Configuration** — интуитивная XML конфигурация
- **IDE Support** — отличная поддержка в IDE
- **Documentation** — comprehensive документация
- **Community** — активное сообщество

#### Runtime Features:
- **Async Processing** — неблокирующее логирование
- **Filtering** — гибкая фильтрация логов
- **Context Awareness** — MDC для контекстной информации
- **Extensible Architecture** — поддержка расширений

### Когда использовать Logback:

✅ **Enterprise Applications** — высоконагруженные системы
✅ **Spring Boot Projects** — default logging решение
✅ **High-Performance Logging** — асинхронное логирование
✅ **Complex Routing** — множественные appenders и фильтры
✅ **Structured Logging** — JSON, custom formats
✅ **Production Monitoring** — advanced monitoring возможности
✅ **Legacy Applications** — миграция с Log4j

### Когда НЕ использовать:

❌ **Simple Applications** — для простых проектов достаточно java.util.logging
❌ **Android-only Apps** — для Android лучше использовать встроенное логирование
❌ **Resource-constrained** — значительный memory footprint
❌ **Alternative Frameworks** — если уже используется другая SLF4J реализация

### Best practices:

1. **Async Appenders** — использование асинхронного логирования для производительности
2. **Proper Configuration** — environment-specific конфигурации
3. **MDC Usage** — контекстная информация в логах
4. **Security** — безопасное логирование чувствительных данных
5. **Monitoring** — отслеживание logging метрик
6. **Testing** — тестирование logging конфигураций
7. **Performance** — оптимизация для конкретных сценариев использования

Logback остается золотым стандартом для логирования в Java экосистеме, предоставляя мощные возможности для самых требовательных приложений. Правильное использование Logback позволяет создавать надежные и наблюдаемые системы. 🚀
