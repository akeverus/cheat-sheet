# SLF4J для Java

Комплексное руководство по Simple Logging Facade for Java (SLF4J): facade паттерн для логирования, bridging, MDC, Markers и интеграция с различными logging фреймворками.

**Дата последнего обновления:** 2026-01-21

## Полезные ссылки

### Официальная документация
- [SLF4J Documentation](https://www.slf4j.org/)
- [SLF4J Manual](https://www.slf4j.org/manual.html)
- [SLF4J GitHub](https://github.com/qos-ch/slf4j)

### QOS.ch
- [SLF4J API](https://www.slf4j.org/api/index.html)
- [SLF4J Extensions](https://www.slf4j.org/extensions.html)
- [SLF4J Cookbook](https://www.slf4j.org/codes.html)

### Статьи и туториалы
- [SLF4J vs Log4j](https://www.baeldung.com/slf4j-with-log4j2-logback)
- [SLF4J MDC](https://www.baeldung.com/mdc-in-log4j2-slf4j)
- [SLF4J Markers](https://www.baeldung.com/slf4j-markers)

### См. также
- `logging-basics.md` - Основы логирования
- `logback.md` - Logback реализация
- `log4j.md` - Log4j реализация

## Содержание

- [Введение в SLF4J](#введение-в-slf4j)
- [Архитектура SLF4J](#архитектура-slf4j)
- [Logger API](#logger-api)
- [Parameterized logging](#parameterized-logging)
- [MDC (Mapped Diagnostic Context)](#mdc-mapped-diagnostic-context)
- [Markers](#markers)
- [Binding с реализациями](#binding-с-реализациями)
- [Bridging](#bridging)
- [Event recording](#event-recording)
- [Performance considerations](#performance-considerations)
- [Testing с SLF4J](#testing-с-slf4j)
- [Best Practices](#best-practices)
- [Troubleshooting](#troubleshooting)
- [Заключение](#заключение)

## Введение в SLF4J

**SLF4J** (Simple Logging Facade for Java) — это простая facade библиотека для логирования в Java. SLF4J предоставляет единый API для различных logging фреймворков (Logback, Log4j, java.util.logging), позволяя менять реализацию логирования без изменения кода приложения.

### Почему SLF4J?

SLF4J решает ключевые проблемы логирования:

1. **Facade Pattern** — единый API для различных logging фреймворков
2. **Runtime Binding** — выбор реализации во время выполнения
3. **Bridging** — миграция между logging фреймворками
4. **Performance** — оптимизированная обработка параметров
5. **Features** — MDC, Markers, Event API
6. **Clean API** — простое и интуитивное использование
7. **No Dependencies** — только стандартная Java

### Архитектура SLF4J

```
┌─────────────────────────────────────────────────────────────┐
│                    Application Code                         │
├─────────────────────────────────────────────────────────────┤
│  ┌─────────────────────────────────────────────────────┐    │
│  │                    SLF4J API                         │    │
│  │  LoggerFactory │ Logger │ MDC │ Markers │ Events   │    │
│  └─────────────────────────────────────────────────────┘    │
├─────────────────────────────────────────────────────────────┤
│  ┌─────────────────────────────────────────────────────┐    │
│  │                Binding Layer                        │    │
│  │  slf4j-logback │ slf4j-log4j │ slf4j-jul │ custom    │    │
│  └─────────────────────────────────────────────────────┘    │
├─────────────────────────────────────────────────────────────┤
│  ┌─────────────────────────────────────────────────────┐    │
│  │            Logging Implementation                   │    │
│  │  Logback │ Log4j │ java.util.logging │ custom       │    │
│  └─────────────────────────────────────────────────────┘    │
└─────────────────────────────────────────────────────────────┘
```

### SLF4J vs другие подходы

| Feature | SLF4J | Direct Log4j | Direct Logback | java.util.logging |
|---------|-------|--------------|----------------|-------------------|
| **Abstraction** | ✅ | ❌ | ❌ | ❌ |
| **Switching** | ✅ | ❌ | ❌ | ❌ |
| **Performance** | ✅ | ✅ | ✅ | ❌ |
| **Features** | ✅ | ✅ | ✅ | ❌ |
| **Migration** | ✅ | ❌ | ❌ | ❌ |
| **Community** | ✅ | ✅ | ✅ | ✅ |

## Logger API

### Получение Logger

#### Factory pattern
```java
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LoggerExample {
    
    // Class-level logger (recommended)
    private static final Logger logger = LoggerFactory.getLogger(LoggerExample.class);
    
    // Named logger
    private static final Logger namedLogger = LoggerFactory.getLogger("com.example.service");
    
    // Root logger
    private static final Logger rootLogger = LoggerFactory.getLogger(Logger.ROOT_LOGGER_NAME);
    
    public void demonstrateLoggers() {
        logger.info("Message from class logger");
        namedLogger.info("Message from named logger");
        rootLogger.info("Message from root logger");
    }
}
```

#### Logger hierarchy
```java
public class LoggerHierarchy {
    
    public void demonstrateHierarchy() {
        // Create loggers with different names
        Logger parentLogger = LoggerFactory.getLogger("com.example");
        Logger childLogger = LoggerFactory.getLogger("com.example.service");
        Logger grandChildLogger = LoggerFactory.getLogger("com.example.service.user");
        
        // All inherit configuration from parent loggers
        parentLogger.info("Parent message");
        childLogger.info("Child message");
        grandChildLogger.info("Grandchild message");
        
        // Check hierarchy
        System.out.println("Parent of child: " + childLogger.getParent().getName());
        System.out.println("Parent of grandchild: " + grandChildLogger.getParent().getName());
    }
}
```

### Log levels

#### Standard levels
```java
public class LogLevels {
    
    private static final Logger logger = LoggerFactory.getLogger(LogLevels.class);
    
    public void demonstrateLevels() {
        // TRACE - most detailed
        logger.trace("Entering method with parameters: {}", parameters);
        
        // DEBUG - debugging information
        logger.debug("User authentication successful for: {}", username);
        
        // INFO - general information
        logger.info("Application started successfully on port: {}", port);
        
        // WARN - warning conditions
        logger.warn("Deprecated API usage detected: {}", methodName);
        
        // ERROR - error conditions
        logger.error("Failed to process request", exception);
    }
    
    public void conditionalLogging() {
        // Always check level before expensive operations
        if (logger.isDebugEnabled()) {
            logger.debug("Expensive debug info: {}", computeExpensiveData());
        }
        
        // SLF4J automatically optimizes parameterized logging
        logger.debug("User details: {}", user.toDetailedString());
    }
    
    private String computeExpensiveData() {
        // Expensive computation
        return "computed data";
    }
}
```

## Parameterized logging

### String concatenation vs parameters

#### Bad practice - string concatenation
```java
public class BadLogging {
    
    private static final Logger logger = LoggerFactory.getLogger(BadLogging.class);
    
    public void badExamples() {
        String userId = "123";
        String action = "login";
        
        // BAD: String concatenation always executed
        logger.debug("User " + userId + " performed action: " + action);
        
        // BAD: Multiple concatenations
        logger.info("Processing request from " + getClientIP() + " at " + System.currentTimeMillis());
        
        // BAD: Object toString() in concatenation
        logger.warn("Invalid user object: " + user.toString());
    }
}
```

#### Good practice - parameterized logging
```java
public class GoodLogging {
    
    private static final Logger logger = LoggerFactory.getLogger(GoodLogging.class);
    
    public void goodExamples() {
        String userId = "123";
        String action = "login";
        
        // GOOD: Parameters evaluated only if level enabled
        logger.debug("User {} performed action: {}", userId, action);
        
        // GOOD: Multiple parameters
        logger.info("Processing request from {} at {}", getClientIP(), System.currentTimeMillis());
        
        // GOOD: Object parameter
        logger.warn("Invalid user object: {}", user);
        
        // GOOD: Complex expressions
        logger.error("Database error for user {}: {} - {}", userId, errorCode, errorMessage);
    }
    
    public void arrayParameters() {
        String[] items = {"item1", "item2", "item3"};
        
        // Array parameter
        logger.info("Processing items: {}", (Object) items);
        
        // List parameter
        logger.info("Available options: {}", Arrays.asList("A", "B", "C"));
    }
}
```

### Custom parameter formatting

#### Lambda expressions (Java 8+)
```java
public class LambdaLogging {
    
    private static final Logger logger = LoggerFactory.getLogger(LambdaLogging.class);
    
    public void lambdaExamples() {
        // Lambda for expensive operations
        logger.debug("User details: {}", () -> computeUserDetails());
        
        // Lambda with condition
        logger.info("Processing completed in {} ms", () -> System.currentTimeMillis() - startTime);
        
        // Multiple lambda parameters
        logger.warn("Validation failed: {} - {}", 
                   () -> validationErrors.toString(),
                   () -> suggestedFixes.toString());
    }
    
    private String computeUserDetails() {
        // Expensive computation
        return userService.getDetailedUserInfo();
    }
}
```

## MDC (Mapped Diagnostic Context)

### Basic MDC usage

#### Adding context
```java
import org.slf4j.MDC;

public class MdcExample {
    
    private static final Logger logger = LoggerFactory.getLogger(MdcExample.class);
    
    public void demonstrateMdc() {
        // Add context information
        MDC.put("userId", "12345");
        MDC.put("sessionId", "session-abc");
        MDC.put("requestId", UUID.randomUUID().toString());
        
        logger.info("User logged in");
        logger.debug("Processing user request");
        
        // Context is automatically included in logs
        processUserRequest();
        
        // Clean up MDC
        MDC.clear();
    }
    
    private void processUserRequest() {
        MDC.put("operation", "validate-user");
        logger.info("Starting user validation");
        
        // Validation logic
        logger.debug("Validation completed");
        
        MDC.remove("operation");
    }
}
```

#### MDC with try-with-resources
```java
public class MdcContext implements AutoCloseable {
    
    public MdcContext(String key, String value) {
        MDC.put(key, value);
    }
    
    public MdcContext(Map<String, String> context) {
        context.forEach(MDC::put);
    }
    
    @Override
    public void close() {
        MDC.clear();
    }
    
    // Usage
    public void processRequest() {
        try (MdcContext context = new MdcContext("requestId", UUID.randomUUID().toString())) {
            MDC.put("userId", "12345");
            logger.info("Processing request");
            
            // MDC context maintained throughout
            validateRequest();
            processBusinessLogic();
            
        } // MDC automatically cleared
    }
}
```

### MDC in web applications

#### Spring MVC interceptor
```java
@Component
public class MdcInterceptor implements HandlerInterceptor {
    
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, 
                           Object handler) throws Exception {
        
        // Add request context
        MDC.put("requestId", UUID.randomUUID().toString());
        MDC.put("method", request.getMethod());
        MDC.put("uri", request.getRequestURI());
        MDC.put("clientIP", getClientIP(request));
        
        // Add user context if authenticated
        String userId = getCurrentUserId();
        if (userId != null) {
            MDC.put("userId", userId);
        }
        
        return true;
    }
    
    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, 
                              Object handler, Exception ex) throws Exception {
        
        // Log response information
        MDC.put("statusCode", String.valueOf(response.getStatus()));
        MDC.put("duration", String.valueOf(System.currentTimeMillis() - getRequestStartTime()));
        
        if (ex != null) {
            MDC.put("error", ex.getClass().getSimpleName());
            logger.error("Request failed", ex);
        } else {
            logger.info("Request completed");
        }
        
        // Clean up MDC
        MDC.clear();
    }
    
    private String getClientIP(HttpServletRequest request) {
        String xForwardedFor = request.getHeader("X-Forwarded-For");
        if (xForwardedFor != null && !xForwardedFor.isEmpty()) {
            return xForwardedFor.split(",")[0].trim();
        }
        return request.getRemoteAddr();
    }
}
```

#### Async MDC propagation
```java
@Configuration
public class AsyncMdcConfig {
    
    @Bean
    public TaskDecorator mdcTaskDecorator() {
        return new MdcTaskDecorator();
    }
    
    @Bean
    public AsyncTaskExecutor applicationTaskExecutor(TaskDecorator mdcTaskDecorator) {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setTaskDecorator(mdcTaskDecorator);
        executor.setCorePoolSize(5);
        executor.setMaxPoolSize(20);
        executor.setQueueCapacity(100);
        executor.initialize();
        return executor;
    }
    
    static class MdcTaskDecorator implements TaskDecorator {
        
        @Override
        public Runnable decorate(Runnable runnable) {
            // Capture MDC context
            Map<String, String> contextMap = MDC.getCopyOfContextMap();
            
            return () -> {
                // Restore MDC context in new thread
                if (contextMap != null) {
                    MDC.setContextMap(contextMap);
                }
                
                try {
                    runnable.run();
                } finally {
                    // Clean up
                    MDC.clear();
                }
            };
        }
    }
}
```

## Markers

### Basic markers

#### Creating and using markers
```java
import org.slf4j.Marker;
import org.slf4j.MarkerFactory;

public class MarkerExample {
    
    private static final Logger logger = LoggerFactory.getLogger(MarkerExample.class);
    
    // Define markers
    private static final Marker AUDIT_MARKER = MarkerFactory.getMarker("AUDIT");
    private static final Marker SECURITY_MARKER = MarkerFactory.getMarker("SECURITY");
    private static final Marker PERFORMANCE_MARKER = MarkerFactory.getMarker("PERFORMANCE");
    
    public void demonstrateMarkers() {
        // Security events
        logger.info(SECURITY_MARKER, "User {} logged in from {}", userId, ipAddress);
        
        // Audit events
        logger.info(AUDIT_MARKER, "User {} performed action: {}", userId, action);
        
        // Performance monitoring
        logger.warn(PERFORMANCE_MARKER, "Slow query detected: {} took {}ms", query, duration);
        
        // Business events
        logger.info("Business event: {}", eventData); // No marker
    }
}
```

#### Hierarchical markers
```java
public class HierarchicalMarkers {
    
    // Parent markers
    private static final Marker BUSINESS = MarkerFactory.getMarker("BUSINESS");
    private static final Marker TECHNICAL = MarkerFactory.getMarker("TECHNICAL");
    
    // Child markers
    private static final Marker ORDER_PROCESSING = MarkerFactory.getMarker("ORDER_PROCESSING");
    private static final Marker PAYMENT_PROCESSING = MarkerFactory.getMarker("PAYMENT_PROCESSING");
    private static final Marker DATABASE_ERROR = MarkerFactory.getMarker("DATABASE_ERROR");
    
    static {
        // Establish hierarchy
        ORDER_PROCESSING.add(BUSINESS);
        PAYMENT_PROCESSING.add(BUSINESS);
        DATABASE_ERROR.add(TECHNICAL);
    }
    
    public void processOrder() {
        logger.info(ORDER_PROCESSING, "Processing order {}", orderId);
        
        try {
            processPayment();
            logger.info(ORDER_PROCESSING, "Order {} processed successfully", orderId);
            
        } catch (Exception e) {
            logger.error(DATABASE_ERROR, "Failed to process order {}", orderId, e);
        }
    }
    
    private void processPayment() {
        logger.debug(PAYMENT_PROCESSING, "Processing payment for order {}", orderId);
        // Payment logic
    }
}
```

### Marker filtering

#### Custom marker filter
```java
public class MarkerFilter implements Filter {
    
    private final Set<String> allowedMarkers;
    
    public MarkerFilter(String... markers) {
        this.allowedMarkers = Set.of(markers);
    }
    
    @Override
    public FilterReply decide(Marker marker, Logger logger, Level level, String format, 
                            Object[] params, Throwable t) {
        
        if (marker != null && allowedMarkers.contains(marker.getName())) {
            return FilterReply.ACCEPT;
        }
        
        return FilterReply.NEUTRAL;
    }
}
```

## Binding с реализациями

### SLF4J bindings

#### Maven dependencies
```xml
<!-- SLF4J API (always needed) -->
<dependency>
    <groupId>org.slf4j</groupId>
    <artifactId>slf4j-api</artifactId>
    <version>2.0.9</version>
</dependency>

<!-- Logback binding (recommended) -->
<dependency>
    <groupId>ch.qos.logback</groupId>
    <artifactId>logback-classic</artifactId>
    <version>1.4.14</version>
</dependency>

<!-- OR Log4j 2 binding -->
<dependency>
    <groupId>org.apache.logging.log4j</groupId>
    <artifactId>log4j-slf4j2-impl</artifactId>
    <version>2.20.0</version>
</dependency>

<!-- OR java.util.logging binding -->
<dependency>
    <groupId>org.slf4j</groupId>
    <artifactId>slf4j-jdk14</artifactId>
    <version>2.0.9</version>
</dependency>
```

#### Runtime binding detection
```java
public class BindingDetection {
    
    public void detectBinding() {
        // Check which binding is active
        try {
            Class.forName("ch.qos.logback.classic.Logger");
            System.out.println("Logback binding detected");
            
        } catch (ClassNotFoundException e) {
            try {
                Class.forName("org.apache.logging.log4j.core.Logger");
                System.out.println("Log4j 2 binding detected");
                
            } catch (ClassNotFoundException e2) {
                try {
                    Class.forName("java.util.logging.Logger");
                    System.out.println("java.util.logging binding detected");
                    
                } catch (ClassNotFoundException e3) {
                    System.out.println("No SLF4J binding found!");
                }
            }
        }
        
        // Get the actual logger factory
        ILoggerFactory factory = LoggerFactory.getILoggerFactory();
        System.out.println("Logger factory: " + factory.getClass().getName());
        
        // Get a logger
        Logger logger = LoggerFactory.getLogger("test");
        System.out.println("Logger implementation: " + logger.getClass().getName());
    }
}
```

### Multiple bindings issue

#### Conflict resolution
```xml
<!-- Avoid multiple bindings - choose only one -->

<!-- GOOD: Only Logback -->
<dependency>
    <groupId>org.slf4j</groupId>
    <artifactId>slf4j-api</artifactId>
    <version>2.0.9</version>
</dependency>
<dependency>
    <groupId>ch.qos.logback</groupId>
    <artifactId>logback-classic</artifactId>
    <version>1.4.14</version>
</dependency>

<!-- BAD: Multiple bindings cause warnings -->
<dependency>
    <groupId>org.slf4j</groupId>
    <artifactId>slf4j-api</artifactId>
    <version>2.0.9</version>
</dependency>
<dependency>
    <groupId>ch.qos.logback</groupId>
    <artifactId>logback-classic</artifactId>
    <version>1.4.14</version>
</dependency>
<dependency>
    <groupId>org.apache.logging.log4j</groupId>
    <artifactId>log4j-slf4j2-impl</artifactId>
    <version>2.20.0</version>
</dependency>
```

## Bridging

### Legacy logging bridging

#### Log4j 1.x to SLF4J
```xml
<!-- Bridge Log4j 1.x calls to SLF4J -->
<dependency>
    <groupId>org.slf4j</groupId>
    <artifactId>log4j-over-slf4j</artifactId>
    <version>2.0.9</version>
</dependency>

<!-- Remove direct Log4j 1.x dependency -->
<!-- <dependency>
    <groupId>log4j</groupId>
    <artifactId>log4j</artifactId>
    <version>1.2.17</version>
</dependency> -->
```

#### Commons Logging to SLF4J
```xml
<!-- Bridge Commons Logging to SLF4J -->
<dependency>
    <groupId>org.slf4j</groupId>
    <artifactId>jcl-over-slf4j</artifactId>
    <version>2.0.9</version>
</dependency>

<!-- Remove Commons Logging -->
<!-- <dependency>
    <groupId>commons-logging</groupId>
    <artifactId>commons-logging</artifactId>
    <version>1.2</version>
</dependency> -->
```

#### java.util.logging to SLF4J
```xml
<!-- Bridge JUL to SLF4J -->
<dependency>
    <groupId>org.slf4j</groupId>
    <artifactId>jul-to-slf4j</artifactId>
    <version>2.0.9</version>
</dependency>

<!-- Configure JUL bridge -->
public class JulBridgeConfig {
    
    static {
        // Redirect JUL to SLF4J
        SLF4JBridgeHandler.install();
        
        // Optional: remove default JUL handlers
        java.util.logging.Logger rootLogger = java.util.logging.Logger.getLogger("");
        Handler[] handlers = rootLogger.getHandlers();
        for (Handler handler : handlers) {
            rootLogger.removeHandler(handler);
        }
    }
}
```

### Complete migration example

#### Before migration
```java
// Old code using different logging APIs
import org.apache.log4j.Logger;  // Log4j 1.x
import org.apache.commons.logging.Log;  // Commons Logging
import java.util.logging.Logger;  // java.util.logging

public class LegacyCode {
    
    private static final org.apache.log4j.Logger log4j = 
        org.apache.log4j.Logger.getLogger(LegacyCode.class);
    
    private static final org.apache.commons.logging.Log commonsLog = 
        org.apache.commons.logging.LogFactory.getLog(LegacyCode.class);
    
    private static final java.util.logging.Logger jul = 
        java.util.logging.Logger.getLogger(LegacyCode.class.getName());
    
    public void oldLogging() {
        log4j.info("Log4j message");
        commonsLog.info("Commons Logging message");
        jul.info("JUL message");
    }
}
```

#### After migration
```java
// New code using SLF4J
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MigratedCode {
    
    private static final Logger logger = LoggerFactory.getLogger(MigratedCode.class);
    
    public void newLogging() {
        // All messages now go through SLF4J
        logger.info("Unified logging message");
        
        // Legacy calls are bridged to SLF4J automatically
        // No code changes needed in legacy libraries
    }
}
```

#### Maven configuration for migration
```xml
<properties>
    <slf4j.version>2.0.9</slf4j.version>
    <logback.version>1.4.14</logback.version>
</properties>

<dependencies>
    <!-- SLF4J API -->
    <dependency>
        <groupId>org.slf4j</groupId>
        <artifactId>slf4j-api</artifactId>
        <version>${slf4j.version}</version>
    </dependency>
    
    <!-- Logback implementation -->
    <dependency>
        <groupId>ch.qos.logback</groupId>
        <artifactId>logback-classic</artifactId>
        <version>${logback.version}</version>
    </dependency>
    
    <!-- Bridges for legacy logging -->
    <dependency>
        <groupId>org.slf4j</groupId>
        <artifactId>log4j-over-slf4j</artifactId>
        <version>${slf4j.version}</version>
    </dependency>
    
    <dependency>
        <groupId>org.slf4j</groupId>
        <artifactId>jcl-over-slf4j</artifactId>
        <version>${slf4j.version}</version>
    </dependency>
    
    <dependency>
        <groupId>org.slf4j</groupId>
        <artifactId>jul-to-slf4j</artifactId>
        <version>${slf4j.version}</version>
    </dependency>
</dependencies>
```

## Event recording

### Event API

#### Recording events
```java
public class EventRecording {
    
    private static final Logger logger = LoggerFactory.getLogger(EventRecording.class);
    
    public void recordEvents() {
        // Create event builder
        LoggingEventBuilder eventBuilder = logger.atInfo();
        
        // Add context
        eventBuilder.setMessage("User {} performed action {}")
                   .addArgument(userId)
                   .addArgument(action)
                   .addKeyValue("sessionId", sessionId)
                   .addKeyValue("timestamp", System.currentTimeMillis());
        
        // Add marker
        eventBuilder.addMarker(AUDIT_MARKER);
        
        // Add MDC context
        MDC.put("userId", userId);
        MDC.put("action", action);
        
        // Log the event
        eventBuilder.log();
        
        MDC.clear();
    }
    
    public void conditionalEventRecording() {
        LoggingEventBuilder builder = logger.atDebug();
        
        if (builder.isEnabled()) {
            builder.setMessage("Detailed debug information: {}")
                   .addArgument(computeExpensiveDebugInfo())
                   .addKeyValue("component", "UserService")
                   .addKeyValue("operation", "validateUser")
                   .log();
        }
    }
    
    private String computeExpensiveDebugInfo() {
        // Expensive computation
        return "debug details";
    }
}
```

#### Custom event builder
```java
public class CustomEventBuilder {
    
    private final Logger logger;
    private final Level level;
    private final String component;
    
    public CustomEventBuilder(Logger logger, Level level, String component) {
        this.logger = logger;
        this.level = level;
        this.component = component;
    }
    
    public void businessEvent(String eventType, String entityId, String details) {
        LoggingEventBuilder builder = logger.atLevel(level);
        
        builder.setMessage("Business event: {} for entity {}")
               .addArgument(eventType)
               .addArgument(entityId)
               .addKeyValue("component", component)
               .addKeyValue("eventType", eventType)
               .addKeyValue("entityId", entityId)
               .addKeyValue("details", details)
               .addKeyValue("timestamp", Instant.now().toString());
        
        // Add business marker
        builder.addMarker(BUSINESS_MARKER);
        
        builder.log();
    }
    
    public void errorEvent(String operation, Exception exception, Map<String, Object> context) {
        LoggingEventBuilder builder = logger.atError();
        
        builder.setMessage("Error in operation: {}")
               .addArgument(operation)
               .setCause(exception);
        
        // Add context as key-value pairs
        context.forEach((key, value) -> 
            builder.addKeyValue(key, value.toString()));
        
        builder.addKeyValue("component", component);
        builder.addKeyValue("timestamp", Instant.now().toString());
        
        builder.log();
    }
    
    // Usage
    private static final CustomEventBuilder eventBuilder = 
        new CustomEventBuilder(LoggerFactory.getLogger("business"), Level.INFO, "UserService");
    
    public void processUser() {
        eventBuilder.businessEvent("USER_CREATED", userId, "New user registration");
        
        try {
            validateUser(user);
            eventBuilder.businessEvent("USER_VALIDATED", userId, "User validation successful");
            
        } catch (ValidationException e) {
            Map<String, Object> context = Map.of(
                "userId", userId,
                "validationErrors", e.getErrors()
            );
            eventBuilder.errorEvent("USER_VALIDATION", e, context);
        }
    }
}
```

## Performance considerations

### Parameter evaluation optimization

#### SLF4J parameter handling
```java
public class PerformanceOptimization {
    
    private static final Logger logger = LoggerFactory.getLogger(PerformanceOptimization.class);
    
    public void demonstrateOptimization() {
        String userId = "123";
        String action = "login";
        
        // SLF4J optimizes parameter evaluation
        // Parameters are evaluated only if level is enabled
        logger.debug("User {} performed {} at {}", userId, action, System.currentTimeMillis());
        
        // For expensive operations, still check level manually
        if (logger.isTraceEnabled()) {
            logger.trace("Expensive trace info: {}", computeExpensiveInfo());
        }
        
        // Lambda parameters (Java 8+)
        logger.debug("Lambda parameter: {}", () -> computeExpensiveInfo());
    }
    
    private String computeExpensiveInfo() {
        // Expensive computation
        return "expensive result";
    }
}
```

### Logger lookup optimization

#### Logger caching
```java
public class LoggerCaching {
    
    // GOOD: Static final logger (fast lookup)
    private static final Logger logger = LoggerFactory.getLogger(LoggerCaching.class);
    
    // BAD: Logger lookup on each call
    public void badPractice() {
        LoggerFactory.getLogger("dynamic.logger.name").info("Message");
    }
    
    // GOOD: Cache loggers if dynamic names are needed
    private final Map<String, Logger> loggerCache = new ConcurrentHashMap<>();
    
    public Logger getCachedLogger(String name) {
        return loggerCache.computeIfAbsent(name, LoggerFactory::getLogger);
    }
    
    public void useCachedLogger() {
        Logger dynamicLogger = getCachedLogger("com.example." + componentName);
        dynamicLogger.info("Message from {}", componentName);
    }
}
```

### MDC performance

#### MDC cleanup
```java
public class MdcPerformance {
    
    public void properMdcUsage() {
        // BAD: No cleanup
        MDC.put("userId", "123");
        processRequest(); // MDC context remains
        
        // GOOD: Explicit cleanup
        try {
            MDC.put("userId", "123");
            processRequest();
        } finally {
            MDC.clear();
        }
        
        // BETTER: Try-with-resources
        try (MdcContext context = new MdcContext("userId", "123")) {
            processRequest();
        }
    }
    
    // Avoid MDC in high-frequency operations
    public void highFrequencyOperation() {
        // BAD: MDC for every request
        for (int i = 0; i < 10000; i++) {
            MDC.put("requestId", String.valueOf(i));
            logger.debug("Processing item {}", i);
            MDC.remove("requestId");
        }
        
        // GOOD: MDC only when needed
        if (logger.isDebugEnabled()) {
            for (int i = 0; i < 10000; i++) {
                MDC.put("requestId", String.valueOf(i));
                logger.debug("Processing item {}", i);
                MDC.remove("requestId");
            }
        }
    }
}
```

## Testing с SLF4J

### Logger testing

#### Mock logger testing
```java
public class LoggerTesting {
    
    @Test
    void testLoggingWithMock() {
        // Create mock logger
        Logger mockLogger = mock(Logger.class);
        
        // Inject mock logger (using setter or constructor)
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

#### ListAppender for testing
```java
public class ListAppenderTesting {
    
    @Test
    void testWithListAppender() {
        // Create and configure list appender
        ListAppender<ILoggingEvent> listAppender = new ListAppender<>();
        listAppender.start();
        
        // Add to logger
        Logger logger = LoggerFactory.getLogger("test");
        ((ch.qos.logback.classic.Logger) logger).addAppender(listAppender);
        
        // Generate log messages
        logger.info("Test message 1");
        logger.warn("Test warning");
        logger.info("Test message 2");
        
        // Verify captured events
        List<ILoggingEvent> events = listAppender.list;
        
        assertEquals(3, events.size());
        assertEquals("Test message 1", events.get(0).getFormattedMessage());
        assertEquals(Level.WARN, events.get(1).getLevel());
        assertEquals("Test message 2", events.get(2).getFormattedMessage());
        
        // Clean up
        ((ch.qos.logback.classic.Logger) logger).detachAppender(listAppender);
    }
}
```

### MDC testing

#### MDC context testing
```java
public class MdcTesting {
    
    @Test
    void testMdcContext() {
        // Capture initial MDC state
        Map<String, String> initialContext = MDC.getCopyOfContextMap();
        
        try {
            // Set MDC context
            MDC.put("userId", "123");
            MDC.put("requestId", "req-456");
            
            // Execute code that uses MDC
            TestService service = new TestService();
            service.processWithMdc();
            
            // Verify MDC was used correctly
            assertEquals("123", MDC.get("userId"));
            assertEquals("req-456", MDC.get("requestId"));
            
        } finally {
            // Restore initial MDC state
            MDC.clear();
            if (initialContext != null) {
                MDC.setContextMap(initialContext);
            }
        }
    }
    
    @Test
    void testMdcCleanup() {
        MDC.put("testKey", "testValue");
        
        try {
            // Code that should clean up MDC
            TestService service = new TestService();
            service.processAndCleanupMdc();
            
            // Verify MDC was cleaned up
            assertNull(MDC.get("testKey"));
            
        } finally {
            MDC.clear();
        }
    }
    
    static class TestService {
        
        private static final Logger logger = LoggerFactory.getLogger(TestService.class);
        
        public void processWithMdc() {
            logger.info("Processing with MDC context");
            // MDC context should be available in logs
        }
        
        public void processAndCleanupMdc() {
            try {
                logger.info("Processing...");
            } finally {
                MDC.clear();
            }
        }
    }
}
```

### Marker testing

#### Marker verification
```java
public class MarkerTesting {
    
    @Test
    void testMarkerUsage() {
        Logger mockLogger = mock(Logger.class);
        Marker testMarker = MarkerFactory.getMarker("TEST");
        
        TestService service = new TestService(mockLogger);
        
        // Configure mock
        when(mockLogger.isInfoEnabled()).thenReturn(true);
        
        // Execute
        service.logWithMarker("test message", testMarker);
        
        // Verify marker was used
        verify(mockLogger).info(eq(testMarker), eq("Test message: {}"), eq("test message"));
    }
    
    static class TestService {
        
        private final Logger logger;
        
        public TestService(Logger logger) {
            this.logger = logger;
        }
        
        public void logWithMarker(String message, Marker marker) {
            logger.info(marker, "Test message: {}", message);
        }
    }
}
```

## Best Practices

### 1. Logger declaration

#### Consistent logger naming
```java
public class LoggerBestPractices {
    
    // GOOD: Class-level logger with class reference
    private static final Logger logger = LoggerFactory.getLogger(LoggerBestPractices.class);
    
    // GOOD: Named logger for specific component
    private static final Logger auditLogger = LoggerFactory.getLogger("AUDIT");
    
    // AVOID: String literals
    // private static final Logger logger = LoggerFactory.getLogger("com.example.MyClass");
    
    // AVOID: Dynamic logger names (unless necessary)
    // private final Logger logger = LoggerFactory.getLogger(getDynamicName());
    
    // AVOID: Instance loggers (unless necessary)
    // private final Logger logger = LoggerFactory.getLogger(this.getClass());
}
```

### 2. Message formatting

#### Consistent message patterns
```java
public class MessageFormatting {
    
    public void consistentPatterns() {
        // Use consistent patterns across application
        logger.info("User {} logged in", userId);
        logger.info("Order {} created", orderId);
        logger.info("Payment {} processed", paymentId);
        
        // Include context information
        logger.debug("Processing {} for user {} in session {}", operation, userId, sessionId);
        
        // Use meaningful action names
        logger.info("Starting data synchronization");
        logger.info("Data synchronization completed");
        logger.error("Data synchronization failed", exception);
    }
    
    public void avoidCommonMistakes() {
        // AVOID: Generic messages
        logger.info("Error occurred");
        
        // GOOD: Specific messages with context
        logger.error("Failed to connect to database {}: {}", dbUrl, exception.getMessage(), exception);
        
        // AVOID: Log and rethrow (creates duplicate logs)
        try {
            riskyOperation();
        } catch (Exception e) {
            logger.error("Operation failed", e);
            throw e; // Exception will be logged again by framework
        }
        
        // GOOD: Log once
        logger.warn("Operation slow for user {}", userId);
    }
}
```

### 3. MDC usage

#### Proper MDC lifecycle
```java
@Service
public class MdcBestPractices {
    
    public void processRequest(String requestId, String userId) {
        // GOOD: Set MDC at the beginning
        MDC.put("requestId", requestId);
        MDC.put("userId", userId);
        
        try {
            logger.info("Processing request");
            
            // MDC context available throughout
            validateRequest();
            processBusinessLogic();
            
            logger.info("Request processed successfully");
            
        } catch (Exception e) {
            logger.error("Request processing failed", e);
            throw e;
        } finally {
            // CRITICAL: Always clean up MDC
            MDC.clear();
        }
    }
    
    public void asyncProcessing() {
        Map<String, String> context = MDC.getCopyOfContextMap();
        
        CompletableFuture.runAsync(() -> {
            // Restore MDC in async thread
            if (context != null) {
                MDC.setContextMap(context);
            }
            
            try {
                logger.info("Async processing started");
                doAsyncWork();
                logger.info("Async processing completed");
            } finally {
                MDC.clear();
            }
        });
    }
}
```

### 4. Exception logging

#### Proper exception handling
```java
public class ExceptionLogging {
    
    public void handleExceptions() {
        try {
            processData();
        } catch (ValidationException e) {
            // Business exceptions - use WARN level
            logger.warn("Validation failed for input {}: {}", input, e.getMessage());
            
        } catch (DatabaseException e) {
            // Infrastructure exceptions - use ERROR level
            logger.error("Database error while processing {}", entityId, e);
            
        } catch (ExternalServiceException e) {
            // External service errors - use WARN level
            logger.warn("External service unavailable: {}", serviceName, e);
            
        } catch (Exception e) {
            // Unexpected exceptions - use ERROR level with full stack trace
            logger.error("Unexpected error processing {}", entityId, e);
        }
    }
    
    public void logAndRethrow() {
        try {
            callExternalService();
        } catch (IOException e) {
            logger.error("External service call failed for request {}", requestId, e);
            throw new ServiceUnavailableException("Service temporarily unavailable", e);
        }
    }
    
    public void conditionalExceptionLogging() {
        try {
            processLargeDataSet();
        } catch (TimeoutException e) {
            // Timeouts are expected - DEBUG level
            logger.debug("Processing timeout for dataset {} - will retry", datasetId);
            
        } catch (DataCorruptionException e) {
            // Data corruption - ERROR level
            logger.error("Data corruption detected in dataset {}", datasetId, e);
        }
    }
}
```

### 5. Security considerations

#### Safe logging practices
```java
public class SecureLogging {
    
    public void logUserAction(User user, String action) {
        // SAFE: Don't log sensitive information
        logger.info("User {} performed action: {}", user.getId(), action);
        
        // DANGEROUS: Never log passwords, tokens, etc.
        // logger.info("User {} logged in with password: {}", username, password);
    }
    
    public void logAuthentication(String username, boolean success) {
        if (success) {
            logger.info("Authentication successful for user: {}", username);
        } else {
            // Don't log failed password attempts (security risk)
            logger.warn("Authentication failed for user: {}", username);
        }
    }
    
    public void logApiCall(String url, Map<String, String> headers) {
        // Remove sensitive headers
        Map<String, String> safeHeaders = new HashMap<>(headers);
        safeHeaders.remove("authorization");
        safeHeaders.remove("x-api-key");
        safeHeaders.remove("cookie");
        
        logger.debug("API call to {} with headers: {}", url, safeHeaders.keySet());
    }
    
    public void logPayment(PaymentRequest request) {
        // Mask sensitive card data
        String maskedCard = maskCardNumber(request.getCardNumber());
        logger.info("Payment processed: amount={}, card={}", request.getAmount(), maskedCard);
    }
    
    private String maskCardNumber(String cardNumber) {
        if (cardNumber == null || cardNumber.length() < 4) {
            return "****";
        }
        return "****-****-****-" + cardNumber.substring(cardNumber.length() - 4);
    }
}
```

### 6. Performance optimization

#### Level checking
```java
public class PerformanceBestPractices {
    
    public void optimizeLogging() {
        // GOOD: SLF4J automatically optimizes parameterized logging
        logger.debug("User details: {}", user.toDetailedString());
        
        // GOOD: Manual check for expensive operations
        if (logger.isTraceEnabled()) {
            logger.trace("Detailed trace info: {}", computeExpensiveTraceInfo());
        }
        
        // AVOID: Expensive operations in log statements
        // logger.debug("Expensive: " + computeExpensiveData());
        
        // GOOD: Lambda parameters (Java 8+)
        logger.debug("Lambda parameter: {}", () -> computeExpensiveData());
    }
    
    public void batchLogging() {
        // For high-frequency operations, consider batching
        List<String> batchMessages = new ArrayList<>();
        
        for (int i = 0; i < 1000; i++) {
            batchMessages.add("Processed item " + i);
            
            // Log in batches to reduce overhead
            if (batchMessages.size() >= 100) {
                logger.debug("Batch processed: {}", batchMessages);
                batchMessages.clear();
            }
        }
        
        // Log remaining items
        if (!batchMessages.isEmpty()) {
            logger.debug("Final batch processed: {}", batchMessages);
        }
    }
    
    private String computeExpensiveData() {
        // Expensive computation
        return "expensive result";
    }
    
    private String computeExpensiveTraceInfo() {
        // Very expensive trace information
        return "trace details";
    }
}
```

## Troubleshooting

### Распространенные проблемы

#### No operation performed

```java
// Problem: Log messages not appearing
// Check if binding is present

public class NoOpLoggerCheck {
    
    public void checkLogger() {
        Logger logger = LoggerFactory.getLogger("test");
        
        // Check logger implementation
        System.out.println("Logger class: " + logger.getClass().getName());
        
        // Check if it's NoOpLogger
        if (logger instanceof org.slf4j.helpers.NOPLogger) {
            System.err.println("ERROR: No SLF4J binding found! Add a logging implementation.");
        }
        
        // Test logging
        logger.info("Test message");
        logger.warn("Test warning");
        logger.error("Test error");
    }
}
```

#### Multiple bindings warning

```java
// Problem: Multiple SLF4J bindings detected
// Solution: Remove extra bindings from classpath

// Check Maven dependencies
// Only one binding should be present:
// - logback-classic (for Logback)
// - log4j-slf4j2-impl (for Log4j 2)
// - slf4j-jdk14 (for java.util.logging)

// Remove conflicting dependencies:
// - slf4j-log4j12 (old Log4j 1.x binding)
// - slf4j-simple (simple binding for testing)
```

#### Memory leaks with MDC

```java
// Problem: MDC context not cleaned up
// Solution: Always clear MDC

public class MdcLeakPrevention {
    
    public void safeMdcUsage() {
        // BAD: MDC not cleared
        MDC.put("userId", "123");
        processRequest();
        // MDC context remains!
        
        // GOOD: Explicit cleanup
        try {
            MDC.put("userId", "123");
            processRequest();
        } finally {
            MDC.clear();
        }
        
        // BETTER: Try-with-resources
        try (MdcContext context = new MdcContext("userId", "123")) {
            processRequest();
        }
    }
    
    // Async MDC propagation
    public void asyncMdc() {
        Map<String, String> context = MDC.getCopyOfContextMap();
        
        CompletableFuture.runAsync(() -> {
            try {
                // Restore MDC in new thread
                if (context != null) {
                    MDC.setContextMap(context);
                }
                
                logger.info("Async operation");
                
            } finally {
                MDC.clear();
            }
        });
    }
    
    static class MdcContext implements AutoCloseable {
        
        public MdcContext(String key, String value) {
            MDC.put(key, value);
        }
        
        @Override
        public void close() {
            MDC.clear();
        }
    }
}
```

#### Performance issues

```java
// Problem: Logging impacting performance
// Solution: Profile and optimize

public class LoggingPerformanceProfiler {
    
    public void profileLogging() {
        Logger logger = LoggerFactory.getLogger("performance.test");
        int iterations = 100000;
        
        // Test parameterized logging
        long start = System.nanoTime();
        for (int i = 0; i < iterations; i++) {
            logger.debug("Test message {}", i);
        }
        long parameterizedTime = System.nanoTime() - start;
        
        // Test string concatenation
        start = System.nanoTime();
        for (int i = 0; i < iterations; i++) {
            if (logger.isDebugEnabled()) {
                logger.debug("Test message " + i);
            }
        }
        long concatenationTime = System.nanoTime() - start;
        
        System.out.printf("Parameterized: %.2fms, Concatenation: %.2fms%n",
            parameterizedTime / 1e6, concatenationTime / 1e6);
    }
    
    public void optimizeExpensiveLogging() {
        // BAD: Expensive operation always executed
        logger.debug("Expensive data: " + computeExpensiveData());
        
        // GOOD: Check level first
        if (logger.isDebugEnabled()) {
            logger.debug("Expensive data: {}", computeExpensiveData());
        }
        
        // BETTER: Don't log expensive data in production
        if (logger.isDebugEnabled() && isDevelopmentEnvironment()) {
            logger.debug("Expensive data: {}", computeExpensiveData());
        }
    }
    
    private String computeExpensiveData() {
        // Expensive computation
        return "expensive result";
    }
    
    private boolean isDevelopmentEnvironment() {
        return "development".equals(System.getProperty("environment"));
    }
}
```

#### Configuration issues

```java
// Problem: Configuration not loading
// Solution: Verify configuration

@Configuration
public class LoggingConfigurationVerification {
    
    @PostConstruct
    public void verifyConfiguration() {
        LoggerContext context = (LoggerContext) LoggerFactory.getILoggerFactory();
        
        System.out.println("=== SLF4J Configuration Verification ===");
        System.out.println("Logger Factory: " + context.getClass().getName());
        
        // Check root logger
        Logger rootLogger = context.getLogger(Logger.ROOT_LOGGER_NAME);
        System.out.println("Root Logger Level: " + rootLogger.getLevel());
        System.out.println("Root Logger Appenders: " + 
            ((ch.qos.logback.classic.Logger) rootLogger).getAppenderCount());
        
        // Check specific logger
        Logger appLogger = context.getLogger("com.example");
        System.out.println("App Logger Level: " + appLogger.getLevel());
        System.out.println("App Logger Effective Level: " + appLogger.getEffectiveLevel());
        
        // Test logging
        Logger testLogger = LoggerFactory.getLogger("verification");
        testLogger.trace("Trace test");
        testLogger.debug("Debug test");
        testLogger.info("Info test");
        testLogger.warn("Warn test");
        testLogger.error("Error test");
        
        System.out.println("=== Verification Complete ===");
    }
}
```

### Debug techniques

#### SLF4J debug logging

```java
// Enable SLF4J debug logging
// Add JVM parameter: -Dorg.slf4j.simpleLogger.defaultLogLevel=debug

public class Slf4jDebug {
    
    public void enableDebugLogging() {
        // Set system property programmatically
        System.setProperty("org.slf4j.simpleLogger.defaultLogLevel", "debug");
        
        Logger logger = LoggerFactory.getLogger("debug.test");
        logger.debug("Debug logging enabled");
        logger.info("SLF4J debug information will be shown");
    }
    
    public void checkBinding() {
        // Check which SLF4J binding is active
        try {
            Class.forName("ch.qos.logback.classic.Logger");
            System.out.println("Logback binding active");
        } catch (ClassNotFoundException e) {
            try {
                Class.forName("org.apache.logging.log4j.core.Logger");
                System.out.println("Log4j 2 binding active");
            } catch (ClassNotFoundException e2) {
                System.out.println("No binding found - using NOPLogger");
            }
        }
        
        // Get actual logger implementation
        Logger logger = LoggerFactory.getLogger("test");
        System.out.println("Logger implementation: " + logger.getClass().getName());
    }
}
```

#### MDC debugging

```java
public class MdcDebugging {
    
    public void debugMdc() {
        // Set some MDC values
        MDC.put("userId", "123");
        MDC.put("requestId", "req-456");
        MDC.put("sessionId", "sess-789");
        
        // Log with MDC
        Logger logger = LoggerFactory.getLogger("mdc.debug");
        logger.info("Test message with MDC");
        
        // Inspect MDC content
        Map<String, String> mdcMap = MDC.getCopyOfContextMap();
        if (mdcMap != null) {
            System.out.println("Current MDC content:");
            mdcMap.forEach((key, value) -> 
                System.out.println("  " + key + " = " + value));
        }
        
        // Test MDC in different threads
        ExecutorService executor = Executors.newFixedThreadPool(2);
        
        for (int i = 0; i < 2; i++) {
            final int threadId = i;
            executor.submit(() -> {
                MDC.put("threadId", String.valueOf(threadId));
                logger.info("Message from thread {}", threadId);
                
                Map<String, String> threadMdc = MDC.getCopyOfContextMap();
                System.out.println("Thread " + threadId + " MDC: " + threadMdc);
                
                MDC.clear();
            });
        }
        
        executor.shutdown();
        
        // Clean up main thread MDC
        MDC.clear();
    }
}
```

#### Logger hierarchy inspection

```java
public class LoggerHierarchyInspection {
    
    public void inspectHierarchy() {
        LoggerContext context = (LoggerContext) LoggerFactory.getILoggerFactory();
        
        System.out.println("=== Logger Hierarchy ===");
        
        // Inspect all loggers
        for (Logger logger : context.getLoggerList()) {
            String name = logger.getName();
            Level level = ((ch.qos.logback.classic.Logger) logger).getLevel();
            Level effectiveLevel = ((ch.qos.logback.classic.Logger) logger).getEffectiveLevel();
            
            System.out.printf("Logger: %-40s Level: %-8s Effective: %-8s%n",
                name.isEmpty() ? "[ROOT]" : name,
                level != null ? level.toString() : "[INHERITED]",
                effectiveLevel != null ? effectiveLevel.toString() : "[UNKNOWN]");
            
            // Show appenders
            Iterator<Appender<ILoggingEvent>> appenders = 
                ((ch.qos.logback.classic.Logger) logger).iteratorForAppenders();
            
            if (appenders.hasNext()) {
                System.out.print("  Appenders: ");
                while (appenders.hasNext()) {
                    Appender<ILoggingEvent> appender = appenders.next();
                    System.out.print(appender.getName() + " ");
                }
                System.out.println();
            }
        }
        
        System.out.println("=== End Hierarchy ===");
    }
}
```

## Заключение

**SLF4J** — это мощный и гибкий facade для логирования в Java, который обеспечивает единый API для различных logging фреймворков. SLF4J предоставляет современные возможности для enterprise приложений.

### Ключевые возможности:

1. **Facade Pattern** — единый API для Logback, Log4j, java.util.logging
2. **Parameterized Logging** — оптимизированная обработка параметров
3. **MDC Support** — Mapped Diagnostic Context для контекстной информации
4. **Markers** — категоризация и фильтрация лог-сообщений
5. **Event API** — структурированное логирование
6. **Bridging** — миграция между logging фреймворками
7. **Performance** — оптимизированная обработка

### Архитектурные преимущества:

#### Developer Experience:
- **Clean API** — простое и интуитивное использование
- **Type Safety** — compile-time проверка
- **IDE Support** — отличная поддержка в IDE
- **Documentation** — comprehensive документация

#### Runtime Features:
- **Runtime Binding** — выбор реализации во время выполнения
- **Context Awareness** — MDC для передачи контекста
- **Extensibility** — поддержка custom компонентов
- **Thread Safety** — безопасное использование в многопоточных приложениях

### Когда использовать SLF4J:

✅ **Enterprise Applications** — крупные приложения с complex logging
✅ **Library Development** — для библиотек, не зависящих от конкретной реализации
✅ **Migration Projects** — переход между logging фреймворками
✅ **Multiple Teams** — стандартизация логирования в большой организации
✅ **Modern Java** — использование современных возможностей
✅ **Performance Critical** — оптимизированное логирование
✅ **Structured Logging** — MDC и Markers для structured данных

### Когда НЕ использовать:

❌ **Simple Applications** — для простых проектов достаточно конкретной реализации
❌ **Legacy Java** — ограниченная поддержка очень старых версий
❌ **Android-only** — для Android лучше использовать встроенное логирование
❌ **Resource-constrained** — overhead от abstraction layer
❌ **Single Implementation** — если всегда используется один фреймворк

### Best practices:

1. **Facade Usage** — всегда используйте SLF4J API вместо конкретных реализаций
2. **Parameterized Logging** — избегайте string concatenation в лог-сообщениях
3. **MDC Management** — правильно управляйте MDC контекстом
4. **Marker Usage** — используйте markers для категоризации
5. **Performance** — проверяйте уровни перед expensive операциями
6. **Security** — не логируйте sensitive информацию
7. **Testing** — тестируйте logging логику
8. **Migration** — используйте bridging для постепенной миграции

SLF4J остается золотым стандартом для логирования в Java экосистеме, предоставляя мощные возможности для самых требовательных приложений. Правильное использование SLF4J позволяет создавать надежные и наблюдаемые системы с гибкой конфигурацией логирования. 🚀
