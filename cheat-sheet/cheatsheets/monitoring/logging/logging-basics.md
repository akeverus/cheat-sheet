# Основы логирования в Java

Комплексное руководство по основам логирования в Java: уровни логирования, фреймворки, конфигурация, best practices и паттерны для enterprise приложений.

**Дата последнего обновления:** 2026-01-21

## Полезные ссылки

### Официальная документация
- [Java Logging Overview](https://docs.oracle.com/en/java/javase/17/core/java-logging-overview.html)
- [SLF4J](https://www.slf4j.org/)
- [Logback](https://logback.qos.ch/documentation.html)
- [Log4j](https://logging.apache.org/log4j/2.x/)

### Спецификации
- [JSR 47: Logging](https://jcp.org/en/jsr/detail?id=47)
- [JUL: java.util.logging](https://docs.oracle.com/en/java/javase/17/docs/api/java.logging/java/util/logging/package-summary.html)

### Статьи и туториалы
- [Log4j vs SLF4J](https://www.baeldung.com/slf4j-with-log4j2-logback)
- [Logging Best Practices](https://www.baeldung.com/java-logging-best-practices)
- [Structured Logging](https://www.baeldung.com/java-structured-logging)

### См. также
- `logback.md` - Logback конфигурация
- `slf4j.md` - SLF4J facade
- `structured-logging.md` - Структурированное логирование
- `java-sleuth.md` - Distributed tracing

## Содержание

- [Введение в логирование](#введение-в-логирование)
- [Уровни логирования](#уровни-логирования)
- [Java Util Logging (JUL)](#java-util-logging-jul)
- [SLF4J и Logback](#slf4j-и-logback)
- [Логирование в Spring Boot](#логирование-в-spring-boot)
- [Конфигурация логирования](#конфигурация-логирования)
- [Лог форматы](#лог-форматы)
- [Производительность логирования](#производительность-логирования)
- [Best Practices](#best-practices)
- [Troubleshooting](#troubleshooting)
- [Заключение](#заключение)

## Введение в логирование

**Логирование** — это процесс записи информации о работе приложения для последующего анализа, отладки и мониторинга. В Java логирование является ключевым инструментом для понимания поведения приложения в production среде.

### Почему важно логирование?

Логирование решает критические задачи разработки и эксплуатации:

1. **Отладка** — анализ ошибок и проблем в коде
2. **Мониторинг** — отслеживание состояния приложения
3. **Аудит** — запись важных бизнес-событий
4. **Производительность** — измерение времени выполнения
5. **Безопасность** — отслеживание подозрительной активности
6. **Поддержка** — помощь в решении проблем пользователей

### Основные концепции

#### Logger
Объект, который записывает сообщения в лог

#### Log Level
Уровень важности сообщения (TRACE, DEBUG, INFO, WARN, ERROR)

#### Appender/Layout
Компоненты, определяющие куда и в каком формате писать логи

#### Log Context
Дополнительная информация, связанная с лог-сообщением

### Архитектура логирования

```
┌─────────────────────────────────────────────────────────────┐
│                    Application Code                         │
├─────────────────────────────────────────────────────────────┤
│  ┌─────────────────────────────────────────────────────┐    │
│  │                 Logging API                          │    │
│  │  SLF4J │ JUL │ Log4j │ JBoss Logging                │    │
│  └─────────────────────────────────────────────────────┘    │
├─────────────────────────────────────────────────────────────┤
│  ┌─────────────────────────────────────────────────────┐    │
│  │            Logging Implementation                    │    │
│  │  Logback │ Log4j │ java.util.logging                │    │
│  └─────────────────────────────────────────────────────┘    │
├─────────────────────────────────────────────────────────────┤
│  ┌─────────────────────────────────────────────────────┐    │
│  │               Log Destinations                       │    │
│  │  Console │ File │ Database │ Network │ Queue        │    │
│  └─────────────────────────────────────────────────────┘    │
└─────────────────────────────────────────────────────────────┘
```

## Уровни логирования

### Стандартные уровни

#### TRACE
Самый детальный уровень логирования. Используется для отслеживания выполнения кода на уровне отдельных операций.

```java
logger.trace("Entering method processOrder with orderId: {}", orderId);
// TRACE: Entering method processOrder with orderId: 12345

logger.trace("Processing payment for amount: {} with card: {}", amount, maskCardNumber(cardNumber));
// TRACE: Processing payment for amount: 99.99 with card: ****-****-****-1234
```

#### DEBUG
Информация для отладки приложения. Включает детальную информацию о внутренних состояниях и переменных.

```java
logger.debug("User authentication successful for user: {}", username);
// DEBUG: User authentication successful for user: john.doe

logger.debug("Database connection established: {}", connectionInfo);
// DEBUG: Database connection established: Connection[id=123, pool=main]

logger.debug("Cache hit ratio: {}%", calculateHitRatio());
// DEBUG: Cache hit ratio: 87.5%
```

#### INFO
Общая информация о работе приложения. Важные бизнес-события и этапы выполнения.

```java
logger.info("Application started successfully on port: {}", port);
// INFO: Application started successfully on port: 8080

logger.info("User {} created new order with id: {}", userId, orderId);
// INFO: User 12345 created new order with id: 67890

logger.info("Batch job completed: processed {} records in {}ms", recordCount, duration);
// INFO: Batch job completed: processed 1500 records in 2500ms
```

#### WARN
Предупреждения о потенциальных проблемах. Ситуации, которые не являются ошибками, но требуют внимания.

```java
logger.warn("Deprecated API usage detected: method {} is deprecated", methodName);
// WARN: Deprecated API usage detected: method getUserById is deprecated

logger.warn("High memory usage detected: {}MB used", memoryUsage);
// WARN: High memory usage detected: 850MB used

logger.warn("Slow query detected: {} took {}ms", query, duration);
// WARN: Slow query detected: SELECT * FROM users took 5000ms
```

#### ERROR
Ошибки выполнения. Исключительные ситуации, которые препятствуют нормальной работе приложения.

```java
logger.error("Failed to connect to database", exception);
// ERROR: Failed to connect to database
// java.sql.SQLException: Connection timeout

logger.error("Payment processing failed for order: {}", orderId, exception);
// ERROR: Payment processing failed for order: 67890

logger.error("Unexpected error occurred", exception);
// ERROR: Unexpected error occurred
// java.lang.NullPointerException: null
```

### Когда использовать каждый уровень

| Уровень | Когда использовать | Примеры |
|---------|-------------------|---------|
| **TRACE** | Детальная отладка, development | SQL запросы, параметры методов |
| **DEBUG** | Отладка и разработка | Состояние переменных, cache hits |
| **INFO** | Важные события | Запуск приложения, бизнес-операции |
| **WARN** | Потенциальные проблемы | Deprecated API, высокая нагрузка |
| **ERROR** | Критические ошибки | Исключения, сбои в работе |

## Java Util Logging (JUL)

### Базовое использование

#### Simple JUL example
```java
import java.util.logging.Logger;
import java.util.logging.Level;

public class JulExample {
    
    private static final Logger logger = Logger.getLogger(JulExample.class.getName());
    
    public void basicLogging() {
        // Different log levels
        logger.finest("Finest level message");  // Most detailed
        logger.finer("Finer level message");
        logger.fine("Fine level message");
        logger.config("Config level message");
        logger.info("Info level message");
        logger.warning("Warning level message");
        logger.severe("Severe level message");  // Most severe
    }
    
    public void loggingWithException() {
        try {
            // Some operation that might fail
            riskyOperation();
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Operation failed", e);
        }
    }
}
```

#### Configuration через properties
```java
# logging.properties
handlers=java.util.logging.ConsoleHandler
.level=INFO

# Package specific levels
com.example.level=FINE
com.example.database.level=FINER

# Console handler configuration
java.util.logging.ConsoleHandler.level=ALL
java.util.logging.ConsoleHandler.formatter=java.util.logging.SimpleFormatter

# File handler
java.util.logging.FileHandler.pattern=%h/java%u.log
java.util.logging.FileHandler.limit=50000
java.util.logging.FileHandler.count=1
java.util.logging.FileHandler.formatter=java.util.logging.XMLFormatter
```

#### Programmatic configuration
```java
public class JulConfiguration {
    
    public static void configureLogging() {
        Logger rootLogger = Logger.getLogger("");
        
        // Remove default handlers
        Handler[] handlers = rootLogger.getHandlers();
        for (Handler handler : handlers) {
            rootLogger.removeHandler(handler);
        }
        
        // Add console handler
        ConsoleHandler consoleHandler = new ConsoleHandler();
        consoleHandler.setLevel(Level.ALL);
        consoleHandler.setFormatter(new SimpleFormatter());
        rootLogger.addHandler(consoleHandler);
        
        // Add file handler
        try {
            FileHandler fileHandler = new FileHandler("application.log");
            fileHandler.setLevel(Level.INFO);
            fileHandler.setFormatter(new SimpleFormatter());
            rootLogger.addHandler(fileHandler);
        } catch (IOException e) {
            logger.severe("Failed to create file handler: " + e.getMessage());
        }
        
        // Set levels
        rootLogger.setLevel(Level.INFO);
        Logger.getLogger("com.example").setLevel(Level.DEBUG);
    }
}
```

### JUL limitations

JUL имеет несколько существенных ограничений:

1. **Performance** — низкая производительность
2. **Configuration** — сложная конфигурация
3. **Features** — ограниченный набор возможностей
4. **Maintenance** — больше не развивается активно

По этим причинам в современных приложениях рекомендуется использовать SLF4J + Logback.

## SLF4J и Logback

### SLF4J основы

#### SLF4J API
```java
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Slf4jExample {
    
    private static final Logger logger = LoggerFactory.getLogger(Slf4jExample.class);
    
    public void basicSlf4j() {
        // Parameterized logging (preferred)
        logger.debug("Processing user {} with id {}", username, userId);
        
        // Traditional logging
        logger.info("Application started on port " + port);
        
        // Logging with exception
        try {
            processData(data);
        } catch (Exception e) {
            logger.error("Failed to process data", e);
        }
    }
    
    public void conditionalLogging() {
        // Avoid expensive operations if debug is not enabled
        if (logger.isDebugEnabled()) {
            logger.debug("Expensive debug info: {}", computeExpensiveDebugInfo());
        }
        
        // SLF4J automatically handles this optimization
        logger.debug("Expensive debug info: {}", computeExpensiveDebugInfo());
    }
    
    private String computeExpensiveDebugInfo() {
        // Some expensive computation
        return "debug info";
    }
}
```

#### Logger hierarchy
```java
public class LoggerHierarchy {
    
    // Root logger
    private static final Logger rootLogger = LoggerFactory.getLogger(Logger.ROOT_LOGGER_NAME);
    
    // Package logger
    private static final Logger packageLogger = LoggerFactory.getLogger("com.example");
    
    // Class logger
    private static final Logger classLogger = LoggerFactory.getLogger(LoggerHierarchy.class);
    
    public void demonstrateHierarchy() {
        // Logs will inherit configuration from parent loggers
        rootLogger.info("Root logger message");
        packageLogger.info("Package logger message");
        classLogger.info("Class logger message");
    }
}
```

### Logback configuration

#### Basic logback.xml
```xml
<?xml version="1.0" encoding="UTF-8"?>
<configuration>
    
    <!-- Console appender -->
    <appender name="CONSOLE" class="ch.qos.logback.core.ConsoleAppender">
        <encoder>
            <pattern>%d{yyyy-MM-dd HH:mm:ss.SSS} [%thread] %-5level %logger{36} - %msg%n</pattern>
        </encoder>
    </appender>
    
    <!-- File appender -->
    <appender name="FILE" class="ch.qos.logback.core.rolling.RollingFileAppender">
        <file>logs/application.log</file>
        <rollingPolicy class="ch.qos.logback.core.rolling.TimeBasedRollingPolicy">
            <fileNamePattern>logs/application.%d{yyyy-MM-dd}.%i.log</fileNamePattern>
            <maxFileSize>10MB</maxFileSize>
            <maxHistory>30</maxHistory>
            <totalSizeCap>1GB</totalSizeCap>
        </rollingPolicy>
        <encoder>
            <pattern>%d{yyyy-MM-dd HH:mm:ss.SSS} [%thread] %-5level %logger{36} - %msg%n</pattern>
        </encoder>
    </appender>
    
    <!-- Logger configuration -->
    <logger name="com.example" level="DEBUG" additivity="false">
        <appender-ref ref="CONSOLE"/>
        <appender-ref ref="FILE"/>
    </logger>
    
    <logger name="org.springframework" level="INFO"/>
    <logger name="org.hibernate" level="WARN"/>
    
    <!-- Root logger -->
    <root level="INFO">
        <appender-ref ref="CONSOLE"/>
    </root>
    
</configuration>
```

#### Advanced configuration
```xml
<?xml version="1.0" encoding="UTF-8"?>
<configuration>
    
    <!-- Property definitions -->
    <property name="LOG_PATTERN" 
              value="%d{yyyy-MM-dd HH:mm:ss.SSS} [%thread] %-5level %logger{36} - %msg%n"/>
    
    <!-- Async appender for performance -->
    <appender name="ASYNC" class="ch.qos.logback.classic.AsyncAppender">
        <discardingThreshold>20</discardingThreshold>
        <queueSize>512</queueSize>
        <appender-ref ref="FILE"/>
    </appender>
    
    <!-- JSON appender for structured logging -->
    <appender name="JSON" class="ch.qos.logback.core.ConsoleAppender">
        <encoder class="net.logstash.logback.encoder.LoggingEventCompositeJsonEncoder">
            <providers>
                <timestamp/>
                <logLevel/>
                <loggerName/>
                <message/>
                <mdc/>
                <stackTrace/>
            </providers>
        </encoder>
    </appender>
    
    <!-- Conditional configuration -->
    <if condition='property("ENV").equals("development")'>
        <then>
            <logger name="com.example" level="DEBUG"/>
        </then>
        <else>
            <logger name="com.example" level="INFO"/>
        </else>
    </if>
    
    <!-- Filters -->
    <appender name="FILTERED" class="ch.qos.logback.core.ConsoleAppender">
        <filter class="ch.qos.logback.classic.filter.ThresholdFilter">
            <level>WARN</level>
        </filter>
        <encoder>
            <pattern>${LOG_PATTERN}</pattern>
        </encoder>
    </appender>
    
</configuration>
```

## Логирование в Spring Boot

### Auto-configuration

#### application.yml
```yaml
logging:
  level:
    root: INFO
    com.example: DEBUG
    org.springframework: WARN
    org.hibernate: ERROR
  
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

#### Profile-specific configuration
```yaml
# application-dev.yml
logging:
  level:
    com.example: DEBUG
    org.springframework.web: DEBUG
  file:
    name: logs/dev-application.log

# application-prod.yml
logging:
  level:
    com.example: INFO
    org.springframework: WARN
  file:
    name: logs/prod-application.log
```

### Custom logging configuration

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
    
    <!-- Common appenders -->
    <appender name="CONSOLE" class="ch.qos.logback.core.ConsoleAppender">
        <encoder>
            <pattern>%d{yyyy-MM-dd HH:mm:ss.SSS} [%thread] %-5level %logger{36} - %msg%n</pattern>
        </encoder>
    </appender>
    
    <appender name="FILE" class="ch.qos.logback.core.rolling.RollingFileAppender">
        <file>logs/application.log</file>
        <rollingPolicy class="ch.qos.logback.core.rolling.TimeBasedRollingPolicy">
            <fileNamePattern>logs/application.%d{yyyy-MM-dd}.%i.log</fileNamePattern>
            <maxFileSize>10MB</maxFileSize>
        </rollingPolicy>
        <encoder>
            <pattern>%d{yyyy-MM-dd HH:mm:ss.SSS} [%thread] %-5level %logger{36} - %msg%n</pattern>
        </encoder>
    </appender>
    
    <appender name="ERROR_FILE" class="ch.qos.logback.core.rolling.RollingFileAppender">
        <file>logs/error.log</file>
        <filter class="ch.qos.logback.classic.filter.LevelFilter">
            <level>ERROR</level>
            <onMatch>ACCEPT</onMatch>
            <onMismatch>DENY</onMismatch>
        </filter>
        <encoder>
            <pattern>%d{yyyy-MM-dd HH:mm:ss.SSS} [%thread] %-5level %logger{36} - %msg%n</pattern>
        </encoder>
    </appender>
    
</configuration>
```

### Spring Boot logging features

#### LoggingApplicationListener
```java
@SpringBootApplication
public class LoggingApplication {
    
    private static final Logger logger = LoggerFactory.getLogger(LoggingApplication.class);
    
    public static void main(String[] args) {
        SpringApplication.run(LoggingApplication.class, args);
        logger.info("Application started successfully");
    }
    
    @EventListener
    public void onApplicationEvent(ApplicationReadyEvent event) {
        logger.info("Application is ready to serve requests");
    }
    
    @EventListener
    public void onContextClosed(ContextClosedEvent event) {
        logger.info("Application context is closing");
    }
}
```

#### Custom logger injection
```java
@Service
public class LoggingService {
    
    private final Logger logger;
    
    // Constructor injection of logger
    public LoggingService() {
        this.logger = LoggerFactory.getLogger(LoggingService.class);
    }
    
    // Or use Lombok
    // private static final Logger logger = LoggerFactory.getLogger(LoggingService.class);
    
    public void performOperation() {
        logger.debug("Starting operation");
        
        try {
            // Business logic
            doWork();
            logger.info("Operation completed successfully");
            
        } catch (Exception e) {
            logger.error("Operation failed", e);
            throw e;
        }
    }
}
```

## Конфигурация логирования

### Runtime configuration

#### Programmatic configuration
```java
@Configuration
public class LoggingConfig {
    
    @Autowired
    private Environment environment;
    
    @PostConstruct
    public void configureLogging() {
        LoggerContext loggerContext = (LoggerContext) LoggerFactory.getILoggerFactory();
        
        // Get root logger
        Logger rootLogger = loggerContext.getLogger(Logger.ROOT_LOGGER_NAME);
        
        // Set log level from properties
        String logLevel = environment.getProperty("logging.level.root", "INFO");
        rootLogger.setLevel(Level.valueOf(logLevel));
        
        // Configure specific loggers
        configurePackageLogger(loggerContext, "com.example");
        configurePackageLogger(loggerContext, "org.springframework");
    }
    
    private void configurePackageLogger(LoggerContext context, String packageName) {
        String level = environment.getProperty("logging.level." + packageName);
        if (level != null) {
            Logger logger = context.getLogger(packageName);
            logger.setLevel(Level.valueOf(level));
        }
    }
}
```

#### Dynamic level changes
```java
@RestController
@RequestMapping("/admin/logging")
public class LoggingController {
    
    private final LoggerContext loggerContext = 
        (LoggerContext) LoggerFactory.getILoggerFactory();
    
    @PutMapping("/level/{loggerName}")
    public ResponseEntity<String> changeLogLevel(
            @PathVariable String loggerName,
            @RequestParam String level) {
        
        try {
            Logger logger = loggerContext.getLogger(loggerName);
            logger.setLevel(Level.valueOf(level.toUpperCase()));
            
            return ResponseEntity.ok("Log level changed to " + level);
            
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest()
                .body("Invalid log level: " + level);
        }
    }
    
    @GetMapping("/levels")
    public Map<String, String> getCurrentLevels() {
        Map<String, String> levels = new HashMap<>();
        
        // Get all loggers
        for (Logger logger : loggerContext.getLoggerList()) {
            Level level = logger.getLevel();
            if (level != null) {
                levels.put(logger.getName(), level.toString());
            }
        }
        
        return levels;
    }
}
```

### Environment-specific configuration

#### Docker configuration
```yaml
# docker-compose.yml
version: '3.8'
services:
  app:
    environment:
      - LOGGING_LEVEL_ROOT=INFO
      - LOGGING_LEVEL_COM_EXAMPLE=DEBUG
      - LOGGING_FILE_NAME=/app/logs/application.log
    volumes:
      - ./logs:/app/logs
```

#### Kubernetes configuration
```yaml
# deployment.yml
apiVersion: apps/v1
kind: Deployment
metadata:
  name: my-app
spec:
  template:
    spec:
      containers:
      - name: app
        env:
        - name: LOGGING_LEVEL_ROOT
          value: "INFO"
        - name: LOGGING_LEVEL_COM_EXAMPLE
          value: "DEBUG"
        - name: LOGGING_FILE_NAME
          value: "/app/logs/application.log"
        volumeMounts:
        - name: logs
          mountPath: /app/logs
      volumes:
      - name: logs
        emptyDir: {}
```

## Лог форматы

### Pattern layout

#### Common patterns
```xml
<!-- Basic pattern -->
<pattern>%d{yyyy-MM-dd HH:mm:ss.SSS} [%thread] %-5level %logger{36} - %msg%n</pattern>

<!-- Detailed pattern -->
<pattern>%d{yyyy-MM-dd HH:mm:ss.SSS} [%thread] %-5level %logger{36} [%X{userId:-}] [%X{requestId:-}] - %msg%n</pattern>

<!-- Colored console pattern -->
<pattern>%d{yyyy-MM-dd HH:mm:ss.SSS} %highlight(%-5level) [%thread] %cyan(%logger{36}) - %msg%n</pattern>
```

#### MDC (Mapped Diagnostic Context)
```java
public class MdcExample {
    
    private static final Logger logger = LoggerFactory.getLogger(MdcExample.class);
    
    public void demonstrateMdc() {
        // Add context information
        MDC.put("userId", "12345");
        MDC.put("requestId", UUID.randomUUID().toString());
        MDC.put("sessionId", "session-abc");
        
        logger.info("User logged in");
        logger.debug("Processing user request");
        
        // Nested operation
        processSubOperation();
        
        // Clean up MDC
        MDC.clear();
    }
    
    private void processSubOperation() {
        MDC.put("operation", "validate-payment");
        logger.info("Starting sub-operation");
        
        // Sub-operation logic
        logger.debug("Validating payment details");
        
        MDC.remove("operation");
    }
}
```

### Structured logging

#### JSON format
```xml
<appender name="JSON" class="ch.qos.logback.core.ConsoleAppender">
    <encoder class="net.logstash.logback.encoder.LoggingEventCompositeJsonEncoder">
        <providers>
            <timestamp>
                <fieldName>@timestamp</fieldName>
                <pattern>yyyy-MM-dd'T'HH:mm:ss.SSSZ</pattern>
            </timestamp>
            <logLevel>
                <fieldName>level</fieldName>
            </logLevel>
            <loggerName>
                <fieldName>logger</fieldName>
            </loggerName>
            <message/>
            <mdc/>
            <stackTrace>
                <fieldName>stack_trace</fieldName>
            </stackTrace>
            <threadName>
                <fieldName>thread</fieldName>
            </threadName>
        </providers>
    </encoder>
</appender>
```

#### Custom fields
```java
public class StructuredLoggingExample {
    
    private static final Logger logger = LoggerFactory.getLogger(StructuredLoggingExample.class);
    
    public void logUserAction(String userId, String action, Map<String, Object> metadata) {
        MDC.put("userId", userId);
        MDC.put("action", action);
        MDC.put("timestamp", Instant.now().toString());
        
        // Add metadata to MDC
        metadata.forEach((key, value) -> MDC.put(key, value.toString()));
        
        logger.info("User action performed: {}", action);
        
        MDC.clear();
    }
    
    public void logBusinessEvent(String eventType, Object data) {
        Map<String, Object> eventData = new HashMap<>();
        eventData.put("eventType", eventType);
        eventData.put("data", data);
        eventData.put("timestamp", System.currentTimeMillis());
        
        logger.info("Business event: {}", eventData);
    }
    
    public void logErrorWithContext(String operation, Exception exception, Map<String, Object> context) {
        MDC.put("operation", operation);
        context.forEach((key, value) -> MDC.put(key, value.toString()));
        
        logger.error("Operation failed: {}", operation, exception);
        
        MDC.clear();
    }
}
```

## Производительность логирования

### Performance considerations

#### Avoid expensive operations
```java
public class PerformanceExample {
    
    private static final Logger logger = LoggerFactory.getLogger(PerformanceExample.class);
    
    // BAD: Expensive operation always executed
    public void badExample() {
        logger.debug("User data: " + expensiveToString());
    }
    
    // GOOD: Check level first
    public void goodExample() {
        if (logger.isDebugEnabled()) {
            logger.debug("User data: {}", expensiveToString());
        }
    }
    
    // BEST: Use parameterized logging
    public void bestExample() {
        logger.debug("User data: {}", expensiveToString());
        // SLF4J automatically checks level before evaluating parameters
    }
    
    private String expensiveToString() {
        // Expensive computation
        return "computed data";
    }
}
```

#### Async logging
```xml
<!-- Async appender configuration -->
<appender name="ASYNC" class="ch.qos.logback.classic.AsyncAppender">
    <!-- Discard INFO, DEBUG, TRACE messages if queue is 80% full -->
    <discardingThreshold>20</discardingThreshold>
    
    <!-- Queue size -->
    <queueSize>512</queueSize>
    
    <!-- Include caller data -->
    <includeCallerData>true</includeCallerData>
    
    <!-- The actual appender to delegate to -->
    <appender-ref ref="FILE"/>
</appender>
```

#### Batch logging
```java
public class BatchLoggingExample {
    
    private static final Logger logger = LoggerFactory.getLogger(BatchLoggingExample.class);
    private final List<String> batchBuffer = new ArrayList<>();
    private final int batchSize = 100;
    
    public synchronized void addToBatch(String message) {
        batchBuffer.add(message);
        
        if (batchBuffer.size() >= batchSize) {
            flushBatch();
        }
    }
    
    public synchronized void flushBatch() {
        if (!batchBuffer.isEmpty()) {
            logger.info("Batch processing {} items: {}", batchBuffer.size(), batchBuffer);
            batchBuffer.clear();
        }
    }
    
    // Auto-flush on shutdown
    public void shutdown() {
        flushBatch();
    }
}
```

### Memory considerations

#### String concatenation
```java
public class MemoryExample {
    
    // BAD: String concatenation in log statement
    public void badStringConcatenation() {
        String result = "";
        for (int i = 0; i < 1000; i++) {
            result += "item" + i + ",";
        }
        logger.info("Result: " + result); // Creates intermediate strings
    }
    
    // GOOD: Use parameterized logging
    public void goodParameterizedLogging() {
        StringBuilder result = new StringBuilder();
        for (int i = 0; i < 1000; i++) {
            result.append("item").append(i).append(",");
        }
        logger.info("Result: {}", result.toString());
    }
    
    // BETTER: Lazy evaluation
    public void lazyLogging() {
        if (logger.isDebugEnabled()) {
            StringBuilder result = new StringBuilder();
            for (int i = 0; i < 1000; i++) {
                result.append("item").append(i).append(",");
            }
            logger.debug("Result: {}", result.toString());
        }
    }
}
```

## Best Practices

### 1. Logger naming and organization

#### Consistent logger naming
```java
public class LoggerNaming {
    
    // Class-level logger (recommended)
    private static final Logger logger = LoggerFactory.getLogger(MyClass.class);
    
    // Package-level logger
    private static final Logger packageLogger = LoggerFactory.getLogger("com.example.package");
    
    // Component logger
    private static final Logger componentLogger = LoggerFactory.getLogger("component.name");
    
    // Avoid these patterns:
    // private static final Logger logger = LoggerFactory.getLogger("MyClass"); // String literal
    // private static final Logger logger = LoggerFactory.getLogger(this.getClass()); // In instance method
}
```

#### Logger hierarchy
```java
// Logger hierarchy example
// Root logger (level: INFO)
// ├── com (inherited INFO)
// │   ├── com.example (level: DEBUG)
// │   │   ├── com.example.service (inherited DEBUG)
// │   │   ├── com.example.controller (inherited DEBUG)
// │   │   └── com.example.repository (level: WARN)
// │   └── com.thirdparty (level: WARN)
// └── org.springframework (level: WARN)
```

### 2. Message formatting

#### Consistent message format
```java
public class MessageFormatting {
    
    public void consistentFormatting() {
        // Use consistent patterns
        logger.info("User {} logged in from {}", userId, ipAddress);
        logger.info("Order {} created for user {}", orderId, userId);
        logger.info("Payment of {} processed for order {}", amount, orderId);
        
        // Include context information
        logger.info("Processing request {} for user {} in session {}", 
                   requestId, userId, sessionId);
        
        // Use meaningful operation names
        logger.debug("Starting user validation for {}", userId);
        logger.debug("User validation completed for {}", userId);
        logger.debug("Sending welcome email to {}", email);
    }
    
    public void avoidCommonMistakes() {
        // BAD: No context
        logger.info("Error occurred");
        
        // GOOD: Include relevant information
        logger.error("Failed to process payment for user {}: {}", userId, exception.getMessage());
        
        // BAD: Log and throw
        try {
            processPayment();
        } catch (Exception e) {
            logger.error("Payment failed", e);
            throw e; // Redundant, exception will be logged by framework
        }
        
        // GOOD: Log once at appropriate level
        logger.warn("Payment processing slow for user {}", userId);
    }
}
```

### 3. Exception logging

#### Proper exception handling
```java
public class ExceptionLogging {
    
    public void handleExceptions() {
        try {
            riskyOperation();
        } catch (ValidationException e) {
            // Business logic exception - log at WARN or INFO
            logger.warn("Validation failed for user {}: {}", userId, e.getMessage());
            // Don't log stack trace for expected exceptions
            
        } catch (DatabaseException e) {
            // Infrastructure exception - log at ERROR
            logger.error("Database error while processing user {}", userId, e);
            
        } catch (Exception e) {
            // Unexpected exception - log at ERROR with full context
            logger.error("Unexpected error processing user {} in operation {}", 
                        userId, operation, e);
        }
    }
    
    public void logAndRethrow() {
        try {
            processData();
        } catch (IOException e) {
            logger.error("Failed to read data file: {}", fileName, e);
            throw new DataProcessingException("Data processing failed", e);
        }
    }
    
    public void conditionalExceptionLogging() {
        try {
            externalApiCall();
        } catch (TimeoutException e) {
            // Log timeouts at WARN level
            logger.warn("External API timeout for request {}", requestId);
            
        } catch (RateLimitException e) {
            // Rate limits are expected - log at DEBUG
            logger.debug("Rate limit exceeded for API call", e);
            
        } catch (Exception e) {
            // Other exceptions - ERROR level
            logger.error("External API call failed", e);
        }
    }
}
```

### 4. Security considerations

#### Safe logging practices
```java
public class SecureLogging {
    
    public void logUserAction(User user, String action) {
        // Never log sensitive information
        logger.info("User {} performed action: {}", user.getId(), action);
        
        // Don't log:
        // logger.info("User {} with password {} logged in", username, password);
        // logger.info("Credit card {} processed", cardNumber);
    }
    
    public void logPayment(PaymentRequest request) {
        // Mask sensitive data
        String maskedCard = maskCardNumber(request.getCardNumber());
        logger.info("Processing payment for amount {} with card {}", 
                   request.getAmount(), maskedCard);
    }
    
    public void logApiCall(String url, Map<String, String> headers) {
        // Don't log sensitive headers
        Map<String, String> safeHeaders = new HashMap<>(headers);
        safeHeaders.remove("authorization");
        safeHeaders.remove("x-api-key");
        safeHeaders.remove("cookie");
        
        logger.debug("API call to {} with headers {}", url, safeHeaders);
    }
    
    private String maskCardNumber(String cardNumber) {
        if (cardNumber == null || cardNumber.length() < 4) {
            return "****";
        }
        return "****-****-****-" + cardNumber.substring(cardNumber.length() - 4);
    }
}
```

### 5. Monitoring and alerting

#### Log-based monitoring
```java
public class LogMonitoring {
    
    private static final Logger securityLogger = LoggerFactory.getLogger("security");
    private static final Logger performanceLogger = LoggerFactory.getLogger("performance");
    
    public void logSecurityEvent(String event, Map<String, Object> context) {
        MDC.put("eventType", "security");
        MDC.put("severity", "high");
        context.forEach((key, value) -> MDC.put(key, value.toString()));
        
        securityLogger.warn("Security event: {}", event);
        
        MDC.clear();
    }
    
    public void logPerformanceMetric(String operation, long durationMs) {
        MDC.put("operation", operation);
        MDC.put("duration", String.valueOf(durationMs));
        
        if (durationMs > 5000) {
            performanceLogger.warn("Slow operation detected: {} took {}ms", operation, durationMs);
        } else if (durationMs > 1000) {
            performanceLogger.info("Operation completed: {} took {}ms", operation, durationMs);
        } else {
            performanceLogger.debug("Operation completed: {} took {}ms", operation, durationMs);
        }
        
        MDC.clear();
    }
    
    public void logBusinessMetric(String metric, Object value) {
        MDC.put("metric", metric);
        MDC.put("value", value.toString());
        
        Logger businessLogger = LoggerFactory.getLogger("business");
        businessLogger.info("Business metric: {} = {}", metric, value);
        
        MDC.clear();
    }
}
```

## Troubleshooting

### Распространенные проблемы

#### No log output
```java
// Problem: Logs not appearing
// Solution: Check configuration

// 1. Check if logger is configured
Logger logger = LoggerFactory.getLogger("com.example");
System.out.println("Logger level: " + ((ch.qos.logback.classic.Logger) logger).getLevel());

// 2. Check effective level
System.out.println("Effective level: " + ((ch.qos.logback.classic.Logger) logger).getEffectiveLevel());

// 3. Check appenders
ch.qos.logback.classic.Logger rootLogger = (ch.qos.logback.classic.Logger) LoggerFactory.getLogger(Logger.ROOT_LOGGER_NAME);
System.out.println("Root logger appenders: " + rootLogger.getAppenderCount());
```

#### Memory leaks
```java
// Problem: Memory leaks with MDC
// Solution: Always clear MDC

public class MdcLeakPrevention {
    
    public void safeMdcUsage() {
        MDC.put("userId", "123");
        MDC.put("requestId", UUID.randomUUID().toString());
        
        try {
            processRequest();
        } finally {
            // Always clear MDC
            MDC.clear();
        }
    }
    
    // Use try-with-resources for complex cases
    public void complexMdcUsage() {
        try (MdcContext context = new MdcContext()) {
            context.put("userId", "123");
            context.put("operation", "login");
            
            performOperation();
            
        } // MDC automatically cleared
    }
    
    static class MdcContext implements AutoCloseable {
        
        @Override
        public void close() {
            MDC.clear();
        }
        
        public MdcContext put(String key, String value) {
            MDC.put(key, value);
            return this;
        }
    }
}
```

#### Performance issues
```java
// Problem: Logging impacting performance
// Solution: Optimize logging

public class PerformanceOptimization {
    
    // Use async logging
    // Configure AsyncAppender in logback.xml
    
    // Avoid expensive operations in log statements
    public void optimizedLogging() {
        // BAD
        logger.debug("User details: " + user.toDetailedString());
        
        // GOOD
        if (logger.isDebugEnabled()) {
            logger.debug("User details: {}", user.toDetailedString());
        }
        
        // BETTER: Don't log expensive data in production
        if (logger.isDebugEnabled() && isDevelopment()) {
            logger.debug("User details: {}", user.toDetailedString());
        }
    }
    
    // Batch similar log messages
    private final Map<String, Integer> errorCounts = new ConcurrentHashMap<>();
    
    public void batchErrorLogging(String errorType, String details) {
        int count = errorCounts.merge(errorType, 1, Integer::sum);
        
        // Log only every 100 occurrences
        if (count % 100 == 0) {
            logger.warn("Error '{}' occurred {} times. Last details: {}", errorType, count, details);
        }
    }
}
```

#### Configuration issues
```java
// Problem: Configuration not loading
// Solution: Verify configuration

@Configuration
public class LoggingVerification {
    
    @PostConstruct
    public void verifyLoggingConfiguration() {
        Logger logger = LoggerFactory.getLogger("test");
        
        // Test different levels
        logger.trace("Trace message");
        logger.debug("Debug message");
        logger.info("Info message");
        logger.warn("Warn message");
        logger.error("Error message");
        
        // Check if Logback is being used
        ILoggerFactory factory = LoggerFactory.getILoggerFactory();
        System.out.println("Logging factory: " + factory.getClass().getName());
        
        if (factory instanceof LoggerContext) {
            LoggerContext context = (LoggerContext) factory;
            System.out.println("Logback configuration: " + context.getConfigurationCache());
        }
    }
}
```

### Debug techniques

#### Dynamic log level changes
```java
@RestController
@RequestMapping("/debug/logging")
public class LoggingDebugController {
    
    @Autowired
    private Environment environment;
    
    @PutMapping("/level/{loggerName}/{level}")
    public ResponseEntity<String> setLogLevel(
            @PathVariable String loggerName,
            @PathVariable String level) {
        
        try {
            LoggerContext context = (LoggerContext) LoggerFactory.getILoggerFactory();
            Logger logger = context.getLogger(loggerName);
            logger.setLevel(Level.valueOf(level.toUpperCase()));
            
            return ResponseEntity.ok("Log level for " + loggerName + " set to " + level);
            
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Invalid logger or level: " + e.getMessage());
        }
    }
    
    @GetMapping("/config")
    public Map<String, Object> getLoggingConfig() {
        Map<String, Object> config = new HashMap<>();
        
        LoggerContext context = (LoggerContext) LoggerFactory.getILoggerFactory();
        
        config.put("configurationFile", context.getConfigurationCache());
        config.put("status", context.getStatusManager());
        
        Map<String, String> levels = new HashMap<>();
        for (Logger logger : context.getLoggerList()) {
            Level level = logger.getLevel();
            if (level != null) {
                levels.put(logger.getName(), level.toString());
            }
        }
        config.put("loggerLevels", levels);
        
        return config;
    }
}
```

#### Log file analysis
```java
public class LogAnalysis {
    
    public void analyzeLogFile(Path logFile) throws IOException {
        Map<String, Integer> errorCounts = new HashMap<>();
        Map<String, Integer> levelCounts = new HashMap<>();
        
        try (BufferedReader reader = Files.newBufferedReader(logFile)) {
            String line;
            Pattern pattern = Pattern.compile("(\\d{4}-\\d{2}-\\d{2} \\d{2}:\\d{2}:\\d{2}\\.\\d{3}) (\\w+) (.+)");
            
            while ((line = reader.readLine()) != null) {
                Matcher matcher = pattern.matcher(line);
                if (matcher.find()) {
                    String level = matcher.group(2);
                    levelCounts.merge(level, 1, Integer::sum);
                    
                    if ("ERROR".equals(level)) {
                        // Analyze error patterns
                        analyzeErrorLine(line, errorCounts);
                    }
                }
            }
        }
        
        System.out.println("Log level distribution: " + levelCounts);
        System.out.println("Error patterns: " + errorCounts);
    }
    
    private void analyzeErrorLine(String line, Map<String, Integer> errorCounts) {
        // Simple error pattern analysis
        if (line.contains("NullPointerException")) {
            errorCounts.merge("NullPointerException", 1, Integer::sum);
        } else if (line.contains("SQLException")) {
            errorCounts.merge("SQLException", 1, Integer::sum);
        } else {
            errorCounts.merge("Other", 1, Integer::sum);
        }
    }
}
```

## Заключение

**Логирование** — это фундаментальная практика разработки надежных Java приложений. Правильная реализация логирования обеспечивает:

### Ключевые возможности:

1. **Уровни логирования** — TRACE, DEBUG, INFO, WARN, ERROR для различных типов сообщений
2. **Гибкая конфигурация** — Logback позволяет настроить вывод в файлы, консоль, базы данных
3. **SLF4J facade** — единый API поверх различных реализаций логирования
4. **Spring Boot интеграция** — автоматическая конфигурация и profile-specific настройки
5. **Производительность** — асинхронное логирование и оптимизации
6. **Безопасность** — защита чувствительных данных в логах

### Архитектурные преимущества:

#### Observability:
- **Отладка** — детальная информация о работе приложения
- **Мониторинг** — отслеживание состояния и метрик
- **Аудит** — запись важных бизнес-событий
- **Поддержка** — помощь в решении проблем пользователей

#### Производительность:
- **Асинхронная обработка** — не блокирует основной поток
- **Уровни логирования** — возможность отключения verbose логирования
- **Фильтры и appenders** — гибкая маршрутизация логов

### Когда использовать различные уровни:

✅ **TRACE** — детальная отладка, SQL запросы, параметры методов
✅ **DEBUG** — отладка и разработка, состояние переменных
✅ **INFO** — важные события, бизнес-операции, запуск компонентов
✅ **WARN** — потенциальные проблемы, deprecated API
✅ **ERROR** — критические ошибки, исключения

### Best practices:

1. **Использовать SLF4J + Logback** — современный стек логирования
2. **Parameterized logging** — избегать string concatenation
3. **Уровни логирования** — правильный выбор уровня для каждого сообщения
4. **MDC для контекста** — передача контекстной информации
5. **Безопасность** — не логировать чувствительные данные
6. **Производительность** — асинхронное логирование и проверки уровней
7. **Мониторинг** — использование логов для алертинга и метрик

Логирование является cornerstone надежных enterprise приложений. Правильная реализация логирования позволяет быстро диагностировать проблемы, обеспечивать compliance и поддерживать высокое качество обслуживания. 🚀
