# Структурированное логирование для Java

Комплексное руководство по структурированному логированию в Java: JSON logging, correlation IDs, distributed tracing, log aggregation и best practices для микросервисных архитектур.

**Дата последнего обновления:** 2026-01-21

## Полезные ссылки

### Спецификации и стандарты
- [Elastic Common Schema](https://www.elastic.co/guide/en/ecs/current/index.html)
- [OpenTelemetry Logging](https://opentelemetry.io/docs/reference/specification/logs/)
- [RFC 5424 - Syslog Protocol](https://tools.ietf.org/html/rfc5424)

### Библиотеки
- [Logstash Logback Encoder](https://github.com/logstash/logstash-logback-encoder)
- [Jackson Structured Logging](https://github.com/FasterXML/jackson)
- [SLF4J Structured Arguments](https://www.slf4j.org/api/org/slf4j/Marker.html)

### Статьи и туториалы
- [Structured Logging with Logback](https://www.baeldung.com/java-structured-logging)
- [JSON Logging Best Practices](https://www.loggly.com/blog/json-logging-best-practices/)
- [Distributed Tracing](https://microservices.io/patterns/observability/distributed-tracing.html)

### См. также
- `logging-basics.md` - Основы логирования
- `logging/centralized-logging.md` - Централизованное логирование
- `java-sleuth.md` - Spring Cloud Sleuth

## Содержание

- [Введение в структурированное логирование](#введение-в-структурированное-логирование)
- [JSON logging](#json-logging)
- [Correlation IDs](#correlation-ids)
- [Distributed tracing](#distributed-tracing)
- [Context propagation](#context-propagation)
- [Log levels и structured data](#log-levels-и-structured-data)
- [Custom fields и metadata](#custom-fields-и-metadata)
- [Performance considerations](#performance-considerations)
- [Log aggregation](#log-aggregation)
- [Monitoring и alerting](#monitoring-и-alerting)
- [Best Practices](#best-practices)
- [Troubleshooting](#troubleshooting)
- [Заключение](#заключение)

## Введение в структурированное логирование

**Структурированное логирование** — это подход к логированию, при котором лог-сообщения содержат структурированные данные (JSON, key-value pairs), а не просто текстовые строки. Это позволяет эффективно анализировать, искать и агрегировать логи в распределенных системах.

### Почему структурированное логирование?

Структурированное логирование решает проблемы традиционного текстового логирования:

1. **Searchability** — эффективный поиск по структурированным полям
2. **Aggregation** — легкая агрегация и анализ данных
3. **Correlation** — связь событий между сервисами
4. **Automation** — автоматическая обработка логов
5. **Consistency** — стандартизированный формат логов
6. **Scalability** — эффективная работа с большими объемами данных
7. **Observability** — лучшая наблюдаемость системы

### Архитектура структурированного логирования

```
┌─────────────────────────────────────────────────────────────┐
│                    Application Layer                        │
├─────────────────────────────────────────────────────────────┤
│  ┌─────────────────────────────────────────────────────┐    │
│  │            Structured Logging Layer                 │    │
│  │  JSON Encoder │ MDC Context │ Correlation IDs       │    │
│  └─────────────────────────────────────────────────────┘    │
├─────────────────────────────────────────────────────────────┤
│  ┌─────────────────────────────────────────────────────┐    │
│  │            Transport Layer                          │    │
│  │  HTTP/HTTPS │ Message Queue │ File System           │    │
│  └─────────────────────────────────────────────────────┘    │
├─────────────────────────────────────────────────────────────┤
│  ┌─────────────────────────────────────────────────────┐    │
│  │            Aggregation Layer                        │    │
│  │  Logstash │ Fluentd │ Filebeat │ Custom Collector   │    │
│  └─────────────────────────────────────────────────────┘    │
├─────────────────────────────────────────────────────────────┤
│  ┌─────────────────────────────────────────────────────┐    │
│  │            Analysis Layer                           │    │
│  │  Elasticsearch │ Splunk │ CloudWatch │ Custom       │    │
│  └─────────────────────────────────────────────────────┘    │
└─────────────────────────────────────────────────────────────┘
```

### Структурированное vs традиционное логирование

| Aspect | Traditional | Structured |
|--------|-------------|------------|
| **Format** | `User 123 logged in` | `{"event":"login","userId":123,"timestamp":"2023-12-01T10:00:00Z"}` |
| **Search** | Text search | Field-based queries |
| **Analysis** | Manual parsing | Automated processing |
| **Correlation** | Manual effort | Built-in correlation |
| **Storage** | Text files | JSON documents |
| **Performance** | Fast writing | Slightly slower writing |

## JSON logging

### Logback JSON configuration

#### Logstash Logback Encoder
```xml
<configuration>
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
    
    <root level="INFO">
        <appender-ref ref="JSON"/>
    </root>
</configuration>
```

#### Custom JSON layout
```java
@Plugin(name = "CustomJson", category = "Core", elementType = "layout", printObject = true)
public class CustomJsonLayout extends LayoutBase<ILoggingEvent> {
    
    private final ObjectMapper objectMapper = new ObjectMapper();
    
    @Override
    public String doLayout(ILoggingEvent event) {
        Map<String, Object> logEntry = new LinkedHashMap<>();
        
        // Standard fields
        logEntry.put("@timestamp", Instant.ofEpochMilli(event.getTimeStamp()).toString());
        logEntry.put("level", event.getLevel().toString());
        logEntry.put("logger", event.getLoggerName());
        logEntry.put("message", event.getFormattedMessage());
        logEntry.put("thread", event.getThreadName());
        
        // MDC context
        Map<String, String> mdc = event.getMDCPropertyMap();
        if (mdc != null && !mdc.isEmpty()) {
            logEntry.put("mdc", mdc);
        }
        
        // Exception
        if (event.getThrowableProxy() != null) {
            logEntry.put("exception", formatException(event.getThrowableProxy()));
        }
        
        try {
            return objectMapper.writeValueAsString(logEntry) + "\n";
        } catch (JsonProcessingException e) {
            return "{\"error\":\"Failed to serialize log entry\"}\n";
        }
    }
    
    private Map<String, Object> formatException(IThrowableProxy throwable) {
        Map<String, Object> exception = new LinkedHashMap<>();
        exception.put("class", throwable.getClassName());
        exception.put("message", throwable.getMessage());
        
        if (throwable.getStackTraceElementProxyArray() != null) {
            List<String> stackTrace = new ArrayList<>();
            for (StackTraceElementProxy element : throwable.getStackTraceElementProxyArray()) {
                stackTrace.add(element.getStackTraceElement().toString());
            }
            exception.put("stackTrace", stackTrace);
        }
        
        return exception;
    }
}
```

### Log4j 2 JSON configuration

#### JSON Layout
```xml
<Configuration>
    <Appenders>
        <Console name="JSON" target="SYSTEM_OUT">
            <JsonLayout compact="false" eventEol="true">
                <KeyValuePair key="appName" value="MyApplication"/>
                <KeyValuePair key="version" value="1.0.0"/>
            </JsonLayout>
        </Console>
    </Appenders>
    
    <Loggers>
        <Root level="INFO">
            <AppenderRef ref="JSON"/>
        </Root>
    </Loggers>
</Configuration>
```

### Structured logging in code

#### SLF4J with structured arguments
```java
public class StructuredLoggingExample {
    
    private static final Logger logger = LoggerFactory.getLogger(StructuredLoggingExample.class);
    
    public void logUserAction(String userId, String action, Map<String, Object> metadata) {
        // Traditional approach
        logger.info("User {} performed action {} with metadata {}", userId, action, metadata);
        
        // Better: structured with key-value pairs
        logger.info("User action performed",
                   KeyValuePair.of("userId", userId),
                   KeyValuePair.of("action", action),
                   KeyValuePair.of("metadata", metadata),
                   KeyValuePair.of("timestamp", System.currentTimeMillis()));
    }
    
    public void logBusinessEvent(String eventType, Object data) {
        Map<String, Object> event = Map.of(
            "eventType", eventType,
            "data", data,
            "timestamp", Instant.now().toString(),
            "service", "user-service"
        );
        
        logger.info("Business event: {}", event);
    }
    
    public void logErrorWithContext(String operation, Exception exception, Map<String, Object> context) {
        Map<String, Object> errorContext = new HashMap<>(context);
        errorContext.put("operation", operation);
        errorContext.put("errorType", exception.getClass().getSimpleName());
        errorContext.put("errorMessage", exception.getMessage());
        errorContext.put("timestamp", Instant.now().toString());
        
        logger.error("Operation failed: {}", errorContext, exception);
    }
}
```

## Correlation IDs

### Request correlation

#### Spring MVC interceptor
```java
@Component
public class CorrelationIdInterceptor implements HandlerInterceptor {
    
    private static final String CORRELATION_ID_HEADER = "X-Correlation-ID";
    private static final String CORRELATION_ID_KEY = "correlationId";
    
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, 
                           Object handler) throws Exception {
        
        String correlationId = request.getHeader(CORRELATION_ID_HEADER);
        
        if (correlationId == null || correlationId.trim().isEmpty()) {
            correlationId = UUID.randomUUID().toString();
        }
        
        // Set in MDC for logging
        MDC.put(CORRELATION_ID_KEY, correlationId);
        
        // Add to response header
        response.setHeader(CORRELATION_ID_HEADER, correlationId);
        
        // Store in request for later use
        request.setAttribute(CORRELATION_ID_KEY, correlationId);
        
        return true;
    }
    
    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, 
                              Object handler, Exception ex) throws Exception {
        
        // Clean up MDC
        MDC.remove(CORRELATION_ID_KEY);
    }
}
```

#### Utility class for correlation IDs
```java
public class CorrelationIdUtils {
    
    private static final String CORRELATION_ID_KEY = "correlationId";
    private static final String CORRELATION_ID_HEADER = "X-Correlation-ID";
    
    public static String getCorrelationId() {
        return MDC.get(CORRELATION_ID_KEY);
    }
    
    public static void setCorrelationId(String correlationId) {
        MDC.put(CORRELATION_ID_KEY, correlationId);
    }
    
    public static String generateCorrelationId() {
        return UUID.randomUUID().toString();
    }
    
    public static void initializeCorrelationId(HttpServletRequest request, HttpServletResponse response) {
        String correlationId = request.getHeader(CORRELATION_ID_HEADER);
        
        if (correlationId == null || correlationId.trim().isEmpty()) {
            correlationId = generateCorrelationId();
        }
        
        setCorrelationId(correlationId);
        response.setHeader(CORRELATION_ID_HEADER, correlationId);
        request.setAttribute(CORRELATION_ID_KEY, correlationId);
    }
    
    public static void propagateCorrelationId(RestTemplate restTemplate) {
        String correlationId = getCorrelationId();
        
        if (correlationId != null) {
            restTemplate.getInterceptors().add((request, body, execution) -> {
                request.getHeaders().add(CORRELATION_ID_HEADER, correlationId);
                return execution.execute(request, body);
            });
        }
    }
    
    public static void cleanup() {
        MDC.remove(CORRELATION_ID_KEY);
    }
}
```

### Async correlation propagation

#### Thread pool executor with correlation
```java
@Configuration
public class AsyncConfig {
    
    @Bean
    public TaskExecutor taskExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(5);
        executor.setMaxPoolSize(20);
        executor.setQueueCapacity(100);
        
        // Enable MDC context propagation
        executor.setTaskDecorator(new MdcTaskDecorator());
        executor.initialize();
        
        return executor;
    }
    
    static class MdcTaskDecorator implements TaskDecorator {
        
        @Override
        public Runnable decorate(Runnable runnable) {
            // Capture current MDC context
            Map<String, String> contextMap = MDC.getCopyOfContextMap();
            
            return () -> {
                try {
                    // Restore MDC context in new thread
                    if (contextMap != null) {
                        MDC.setContextMap(contextMap);
                    }
                    
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

#### WebClient correlation propagation
```java
@Configuration
public class WebClientConfig {
    
    @Bean
    public WebClient webClient() {
        return WebClient.builder()
            .filter((request, next) -> {
                // Add correlation ID to outgoing requests
                String correlationId = CorrelationIdUtils.getCorrelationId();
                if (correlationId != null) {
                    return next.exchange(ClientRequest.from(request)
                        .header("X-Correlation-ID", correlationId)
                        .build());
                }
                return next.exchange(request);
            })
            .build();
    }
}
```

## Distributed tracing

### OpenTelemetry integration

#### Spring Boot with OpenTelemetry
```xml
<dependency>
    <groupId>io.opentelemetry</groupId>
    <artifactId>opentelemetry-api</artifactId>
    <version>1.27.0</version>
</dependency>

<dependency>
    <groupId>io.opentelemetry</groupId>
    <artifactId>opentelemetry-sdk</artifactId>
    <version>1.27.0</version>
</dependency>

<dependency>
    <groupId>io.opentelemetry.instrumentation</groupId>
    <artifactId>opentelemetry-spring-boot-starter</artifactId>
    <version>1.27.0</version>
</dependency>
```

#### Tracing configuration
```java
@Configuration
public class TracingConfig {
    
    @Bean
    public OpenTelemetry openTelemetry() {
        return OpenTelemetrySdk.builder()
            .setTracerProvider(tracerProvider())
            .setPropagators(ContextPropagators.create(W3CTraceContextPropagator.getInstance()))
            .build();
    }
    
    @Bean
    public Tracer tracer() {
        return openTelemetry().getTracer("my-service", "1.0.0");
    }
    
    private TracerProvider tracerProvider() {
        return TracerProvider.builder()
            .addSpanProcessor(BatchSpanProcessor.builder(logSpanExporter()).build())
            .build();
    }
    
    private SpanExporter logSpanExporter() {
        return new SpanExporter() {
            @Override
            public CompletableResultCode export(Collection<SpanData> spans) {
                for (SpanData span : spans) {
                    logger.info("Span: {} - {} - {}ms", 
                              span.getName(), 
                              span.getTraceId(),
                              span.getEndEpochNanos() - span.getStartEpochNanos());
                }
                return CompletableResultCode.ofSuccess();
            }
            
            @Override
            public CompletableResultCode flush() {
                return CompletableResultCode.ofSuccess();
            }
            
            @Override
            public CompletableResultCode shutdown() {
                return CompletableResultCode.ofSuccess();
            }
        };
    }
}
```

### Custom tracing integration

#### Manual span creation
```java
@Service
public class TracingService {
    
    private final Tracer tracer;
    
    @Autowired
    public TracingService(Tracer tracer) {
        this.tracer = tracer;
    }
    
    public void processRequest(String requestId) {
        Span span = tracer.spanBuilder("processRequest")
            .setAttribute("request.id", requestId)
            .setAttribute("service.name", "user-service")
            .startSpan();
        
        try (Scope scope = span.makeCurrent()) {
            // Add structured logging with trace context
            MDC.put("traceId", span.getSpanContext().getTraceId());
            MDC.put("spanId", span.getSpanContext().getSpanId());
            
            logger.info("Processing request", KeyValuePair.of("operation", "request_processing"));
            
            // Business logic
            validateRequest(requestId);
            processBusinessLogic(requestId);
            
            span.setAttribute("result", "success");
            logger.info("Request processed successfully", 
                       KeyValuePair.of("result", "success"));
            
        } catch (Exception e) {
            span.setAttribute("error", true);
            span.setAttribute("error.message", e.getMessage());
            span.recordException(e);
            
            logger.error("Request processing failed", 
                        KeyValuePair.of("error", e.getClass().getSimpleName()), e);
            
            throw e;
        } finally {
            span.end();
            MDC.remove("traceId");
            MDC.remove("spanId");
        }
    }
}
```

## Context propagation

### Reactor context propagation

#### WebFlux correlation
```java
@Configuration
public class WebFluxConfig {
    
    @Bean
    public WebFilter correlationIdFilter() {
        return (exchange, chain) -> {
            String correlationId = exchange.getRequest().getHeaders()
                .getFirst("X-Correlation-ID");
            
            if (correlationId == null) {
                correlationId = UUID.randomUUID().toString();
            }
            
            return chain.filter(exchange)
                .contextWrite(ctx -> ctx.put("correlationId", correlationId))
                .doOnEach(signal -> {
                    if (signal.hasValue() || signal.hasError()) {
                        MDC.put("correlationId", correlationId);
                        
                        if (signal.hasError()) {
                            logger.error("WebFlux error", signal.getThrowable());
                        }
                        
                        MDC.clear();
                    }
                });
        };
    }
}
```

#### Service with Reactor
```java
@Service
public class ReactiveService {
    
    private static final Logger logger = LoggerFactory.getLogger(ReactiveService.class);
    
    public Mono<String> processWithCorrelation(String input) {
        return Mono.deferContextual(contextView -> {
            String correlationId = contextView.getOrDefault("correlationId", "unknown");
            
            return Mono.fromCallable(() -> {
                MDC.put("correlationId", correlationId);
                logger.info("Processing input: {}", input);
                
                // Simulate processing
                String result = "Processed: " + input;
                logger.info("Processing complete: {}", result);
                
                MDC.clear();
                return result;
            });
        });
    }
}
```

### Kafka message headers

#### Producer correlation
```java
@Service
public class KafkaProducerService {
    
    @Autowired
    private KafkaTemplate<String, String> kafkaTemplate;
    
    public void sendMessage(String topic, String message) {
        String correlationId = CorrelationIdUtils.getCorrelationId();
        
        Message<String> kafkaMessage = MessageBuilder
            .withPayload(message)
            .setHeader("X-Correlation-ID", correlationId)
            .setHeader(KafkaHeaders.TOPIC, topic)
            .build();
        
        logger.info("Sending message to topic {}", topic, 
                   KeyValuePair.of("correlationId", correlationId));
        
        kafkaTemplate.send(kafkaMessage);
    }
}
```

#### Consumer correlation
```java
@Service
public class KafkaConsumerService {
    
    @KafkaListener(topics = "user-events")
    public void handleMessage(ConsumerRecord<String, String> record, 
                            @Header("X-Correlation-ID") String correlationId) {
        
        // Set correlation ID for this processing context
        CorrelationIdUtils.setCorrelationId(correlationId);
        
        try {
            logger.info("Received message from topic {}", record.topic(),
                       KeyValuePair.of("partition", record.partition()),
                       KeyValuePair.of("offset", record.offset()));
            
            // Process message
            processMessage(record.value());
            
        } finally {
            CorrelationIdUtils.cleanup();
        }
    }
}
```

## Log levels и structured data

### Semantic logging levels

#### Business vs technical levels
```java
public class SemanticLogging {
    
    private static final Logger businessLogger = LoggerFactory.getLogger("business");
    private static final Logger technicalLogger = LoggerFactory.getLogger("technical");
    private static final Logger auditLogger = LoggerFactory.getLogger("audit");
    
    public void logBusinessEvent(String eventType, Map<String, Object> data) {
        Map<String, Object> event = new HashMap<>(data);
        event.put("eventType", eventType);
        event.put("timestamp", Instant.now().toString());
        event.put("level", "BUSINESS");
        
        businessLogger.info("Business event: {}", event);
    }
    
    public void logTechnicalEvent(String component, String operation, Map<String, Object> context) {
        Map<String, Object> event = new HashMap<>(context);
        event.put("component", component);
        event.put("operation", operation);
        event.put("timestamp", Instant.now().toString());
        event.put("level", "TECHNICAL");
        
        technicalLogger.info("Technical event: {}", event);
    }
    
    public void logAuditEvent(String userId, String action, String resource, Map<String, Object> details) {
        Map<String, Object> audit = new HashMap<>(details);
        audit.put("userId", userId);
        audit.put("action", action);
        audit.put("resource", resource);
        audit.put("timestamp", Instant.now().toString());
        audit.put("level", "AUDIT");
        
        auditLogger.info("Audit event: {}", audit);
    }
}
```

### Conditional structured logging

#### Environment-based logging
```java
public class ConditionalStructuredLogging {
    
    private static final Logger logger = LoggerFactory.getLogger(ConditionalStructuredLogging.class);
    private final boolean detailedLogging = "true".equals(System.getProperty("detailed.logging"));
    
    public void logWithConditionalDetails(String operation, Map<String, Object> basicContext) {
        Map<String, Object> context = new HashMap<>(basicContext);
        context.put("operation", operation);
        context.put("timestamp", Instant.now().toString());
        
        if (detailedLogging) {
            // Add expensive/complex details only in development
            context.put("threadInfo", getThreadInfo());
            context.put("memoryInfo", getMemoryInfo());
            context.put("systemProperties", getSystemProperties());
        }
        
        logger.info("Operation performed: {}", context);
    }
    
    public void logPerformanceMetrics(String operation, long duration, Map<String, Object> context) {
        Map<String, Object> metrics = new HashMap<>(context);
        metrics.put("operation", operation);
        metrics.put("duration", duration);
        metrics.put("timestamp", Instant.now().toString());
        
        // Log performance warnings for slow operations
        if (duration > 1000) {
            logger.warn("Slow operation detected: {}", metrics);
        } else if (duration > 100) {
            logger.info("Operation completed: {}", metrics);
        } else {
            logger.debug("Operation completed: {}", metrics);
        }
    }
    
    private Map<String, Object> getThreadInfo() {
        Thread thread = Thread.currentThread();
        return Map.of(
            "name", thread.getName(),
            "id", thread.getId(),
            "priority", thread.getPriority(),
            "state", thread.getState().toString()
        );
    }
    
    private Map<String, Object> getMemoryInfo() {
        Runtime runtime = Runtime.getRuntime();
        return Map.of(
            "free", runtime.freeMemory(),
            "total", runtime.totalMemory(),
            "max", runtime.maxMemory(),
            "used", runtime.totalMemory() - runtime.freeMemory()
        );
    }
    
    private Map<String, String> getSystemProperties() {
        return System.getProperties().entrySet().stream()
            .collect(Collectors.toMap(
                e -> e.getKey().toString(),
                e -> e.getValue().toString()
            ));
    }
}
```

## Custom fields и metadata

### Application metadata

#### Global context configuration
```java
@Configuration
public class LoggingContextConfig {
    
    @Value("${spring.application.name}")
    private String applicationName;
    
    @Value("${app.version:1.0.0}")
    private String applicationVersion;
    
    @Value("${app.environment:dev}")
    private String environment;
    
    @PostConstruct
    public void configureGlobalContext() {
        // Set global MDC values available to all loggers
        MDC.put("application", applicationName);
        MDC.put("version", applicationVersion);
        MDC.put("environment", environment);
        MDC.put("hostname", getHostname());
        MDC.put("pid", getProcessId());
    }
    
    private String getHostname() {
        try {
            return InetAddress.getLocalHost().getHostName();
        } catch (UnknownHostException e) {
            return "unknown";
        }
    }
    
    private String getProcessId() {
        try {
            return ManagementFactory.getRuntimeMXBean().getName().split("@")[0];
        } catch (Exception e) {
            return "unknown";
        }
    }
}
```

### Dynamic metadata

#### Request-scoped metadata
```java
@Component
public class RequestLoggingContext {
    
    private static final String REQUEST_START_TIME = "requestStartTime";
    private static final String REQUEST_METHOD = "requestMethod";
    private static final String REQUEST_URI = "requestUri";
    private static final String USER_AGENT = "userAgent";
    private static final String CLIENT_IP = "clientIp";
    
    public void initializeRequestContext(HttpServletRequest request) {
        MDC.put(REQUEST_START_TIME, String.valueOf(System.currentTimeMillis()));
        MDC.put(REQUEST_METHOD, request.getMethod());
        MDC.put(REQUEST_URI, request.getRequestURI());
        MDC.put(USER_AGENT, request.getHeader("User-Agent"));
        MDC.put(CLIENT_IP, getClientIp(request));
    }
    
    public void finalizeRequestContext(HttpServletResponse response) {
        long startTime = Long.parseLong(MDC.get(REQUEST_START_TIME));
        long duration = System.currentTimeMillis() - startTime;
        
        MDC.put("responseStatus", String.valueOf(response.getStatus()));
        MDC.put("responseTime", String.valueOf(duration));
        
        // Log request completion
        logger.info("Request completed", 
                   KeyValuePair.of("duration", duration),
                   KeyValuePair.of("status", response.getStatus()));
        
        // Clean up request-specific MDC
        MDC.remove(REQUEST_START_TIME);
        MDC.remove(REQUEST_METHOD);
        MDC.remove(REQUEST_URI);
        MDC.remove(USER_AGENT);
        MDC.remove(CLIENT_IP);
    }
    
    private String getClientIp(HttpServletRequest request) {
        String xForwardedFor = request.getHeader("X-Forwarded-For");
        if (xForwardedFor != null && !xForwardedFor.trim().isEmpty()) {
            return xForwardedFor.split(",")[0].trim();
        }
        
        String xRealIp = request.getHeader("X-Real-IP");
        if (xRealIp != null && !xRealIp.trim().isEmpty()) {
            return xRealIp.trim();
        }
        
        return request.getRemoteAddr();
    }
}
```

### Business context

#### Domain-specific metadata
```java
public class BusinessLoggingContext {
    
    public static void setOrderContext(String orderId, String customerId, BigDecimal amount) {
        MDC.put("orderId", orderId);
        MDC.put("customerId", customerId);
        MDC.put("orderAmount", amount.toString());
        MDC.put("businessDomain", "ORDER_PROCESSING");
    }
    
    public static void setPaymentContext(String paymentId, String orderId, String method) {
        MDC.put("paymentId", paymentId);
        MDC.put("orderId", orderId);
        MDC.put("paymentMethod", method);
        MDC.put("businessDomain", "PAYMENT_PROCESSING");
    }
    
    public static void setUserContext(String userId, String sessionId, String role) {
        MDC.put("userId", userId);
        MDC.put("sessionId", sessionId);
        MDC.put("userRole", role);
        MDC.put("businessDomain", "USER_MANAGEMENT");
    }
    
    public static void clearBusinessContext() {
        MDC.remove("orderId");
        MDC.remove("customerId");
        MDC.remove("orderAmount");
        MDC.remove("paymentId");
        MDC.remove("paymentMethod");
        MDC.remove("userId");
        MDC.remove("sessionId");
        MDC.remove("userRole");
        MDC.remove("businessDomain");
    }
    
    public static void logBusinessEvent(String eventType, Map<String, Object> additionalData) {
        Map<String, Object> event = new HashMap<>();
        event.put("eventType", eventType);
        event.put("timestamp", Instant.now().toString());
        event.put("businessDomain", MDC.get("businessDomain"));
        
        // Add current MDC context
        Map<String, String> mdcContext = MDC.getCopyOfContextMap();
        if (mdcContext != null) {
            event.putAll(mdcContext);
        }
        
        // Add additional data
        if (additionalData != null) {
            event.putAll(additionalData);
        }
        
        Logger businessLogger = LoggerFactory.getLogger("business");
        businessLogger.info("Business event: {}", event);
    }
}
```

## Performance considerations

### Efficient JSON serialization

#### Object reuse
```java
public class EfficientJsonLogging {
    
    private static final ObjectMapper objectMapper = new ObjectMapper();
    private static final ThreadLocal<Map<String, Object>> logBuffer = 
        ThreadLocal.withInitial(LinkedHashMap::new);
    
    public void logEfficiently(String eventType, Map<String, Object> data) {
        Map<String, Object> buffer = logBuffer.get();
        buffer.clear();
        
        // Reuse buffer to avoid allocations
        buffer.put("eventType", eventType);
        buffer.put("timestamp", System.currentTimeMillis());
        buffer.putAll(data);
        
        logger.info("Event: {}", buffer);
    }
    
    public void logWithCustomSerializer() {
        // Use Jackson's streaming API for large objects
        try (StringWriter writer = new StringWriter()) {
            JsonGenerator generator = objectMapper.createGenerator(writer);
            
            generator.writeStartObject();
            generator.writeStringField("event", "large_data");
            generator.writeNumberField("timestamp", System.currentTimeMillis());
            
            // Write large array without loading into memory
            generator.writeArrayFieldStart("data");
            for (int i = 0; i < 10000; i++) {
                generator.writeNumber(i);
            }
            generator.writeEndArray();
            
            generator.writeEndObject();
            generator.close();
            
            logger.info("Large data event: {}", writer.toString());
            
        } catch (IOException e) {
            logger.error("Failed to serialize large data", e);
        }
    }
}
```

### Async logging optimization

#### Batch logging
```java
public class BatchLogging {
    
    private final List<Map<String, Object>> batchBuffer = new CopyOnWriteArrayList<>();
    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
    
    public BatchLogging() {
        // Flush every 5 seconds
        scheduler.scheduleAtFixedRate(this::flushBatch, 5, 5, TimeUnit.SECONDS);
    }
    
    public void addToBatch(String eventType, Map<String, Object> data) {
        Map<String, Object> event = new HashMap<>(data);
        event.put("eventType", eventType);
        event.put("timestamp", Instant.now().toString());
        
        batchBuffer.add(event);
        
        // Flush if batch is large
        if (batchBuffer.size() >= 100) {
            flushBatch();
        }
    }
    
    private void flushBatch() {
        if (batchBuffer.isEmpty()) {
            return;
        }
        
        List<Map<String, Object>> batch = new ArrayList<>(batchBuffer);
        batchBuffer.clear();
        
        logger.info("Batch events: {}", batch);
    }
    
    public void shutdown() {
        scheduler.shutdown();
        flushBatch(); // Final flush
    }
}
```

### Memory-efficient patterns

#### Avoid large objects in logs
```java
public class MemoryEfficientLogging {
    
    public void logLargeObjectCarefully(LargeObject obj) {
        // Don't log the entire object
        // logger.info("Processing large object: {}", obj);
        
        // Log summary instead
        Map<String, Object> summary = Map.of(
            "objectId", obj.getId(),
            "size", obj.getSize(),
            "type", obj.getClass().getSimpleName(),
            "timestamp", Instant.now().toString()
        );
        
        logger.info("Processing large object: {}", summary);
        
        // Log detailed info only at TRACE level
        if (logger.isTraceEnabled()) {
            logger.trace("Large object details: {}", obj);
        }
    }
    
    public void logCollectionSummary(Collection<?> collection) {
        Map<String, Object> summary = Map.of(
            "collectionType", collection.getClass().getSimpleName(),
            "size", collection.size(),
            "isEmpty", collection.isEmpty(),
            "timestamp", Instant.now().toString()
        );
        
        logger.info("Collection summary: {}", summary);
        
        // Log elements only if collection is small
        if (collection.size() <= 10 && logger.isDebugEnabled()) {
            logger.debug("Collection contents: {}", collection);
        }
    }
}
```

## Log aggregation

### Log aggregation patterns

#### Centralized collection
```java
@Configuration
public class LogAggregationConfig {
    
    @Bean
    public LogAggregator logAggregator() {
        return new CompositeLogAggregator(Arrays.asList(
            new ElasticsearchAggregator(),
            new CloudWatchAggregator(),
            new LocalFileAggregator()
        ));
    }
}

public interface LogAggregator {
    void aggregate(Map<String, Object> logEntry);
}

public class ElasticsearchAggregator implements LogAggregator {
    
    @Override
    public void aggregate(Map<String, Object> logEntry) {
        // Send to Elasticsearch
        try {
            String index = "logs-" + LocalDate.now().format(DateTimeFormatter.BASIC_ISO_DATE);
            elasticsearchClient.index(index, logEntry);
        } catch (Exception e) {
            logger.error("Failed to send log to Elasticsearch", e);
        }
    }
}

public class CloudWatchAggregator implements LogAggregator {
    
    @Override
    public void aggregate(Map<String, Object> logEntry) {
        // Send to CloudWatch
        try {
            cloudWatchClient.putLogEvents(logEntry);
        } catch (Exception e) {
            logger.error("Failed to send log to CloudWatch", e);
        }
    }
}
```

### Structured aggregation

#### Log processing pipeline
```java
public class LogProcessingPipeline {
    
    private final List<LogProcessor> processors;
    
    public LogProcessingPipeline(List<LogProcessor> processors) {
        this.processors = processors;
    }
    
    public void processLog(Map<String, Object> logEntry) {
        for (LogProcessor processor : processors) {
            try {
                logEntry = processor.process(logEntry);
                if (logEntry == null) {
                    // Processor filtered out this log
                    return;
                }
            } catch (Exception e) {
                logger.error("Log processor failed", e);
            }
        }
        
        // Send to aggregators
        logAggregators.forEach(aggregator -> aggregator.aggregate(logEntry));
    }
    
    public interface LogProcessor {
        Map<String, Object> process(Map<String, Object> logEntry);
    }
    
    public static class EnrichmentProcessor implements LogProcessor {
        
        @Override
        public Map<String, Object> process(Map<String, Object> logEntry) {
            // Add additional context
            logEntry.put("processedAt", Instant.now().toString());
            logEntry.put("processor", "enrichment");
            
            return logEntry;
        }
    }
    
    public static class FilteringProcessor implements LogProcessor {
        
        @Override
        public Map<String, Object> process(Map<String, Object> logEntry) {
            String level = (String) logEntry.get("level");
            
            // Filter out DEBUG logs in production
            if ("DEBUG".equals(level) && isProduction()) {
                return null;
            }
            
            return logEntry;
        }
    }
}
```

## Monitoring и alerting

### Log-based metrics

#### Structured metrics extraction
```java
@Service
public class LogMetricsExtractor {
    
    private final MeterRegistry meterRegistry;
    
    @Autowired
    public LogMetricsExtractor(MeterRegistry meterRegistry) {
        this.meterRegistry = meterRegistry;
    }
    
    public void extractMetrics(Map<String, Object> logEntry) {
        String level = (String) logEntry.get("level");
        String logger = (String) logEntry.get("logger");
        
        // Count log events by level
        Counter.builder("logs.events")
            .tag("level", level.toLowerCase())
            .tag("logger", extractLoggerCategory(logger))
            .register(meterRegistry)
            .increment();
        
        // Count errors
        if ("ERROR".equals(level)) {
            Counter.builder("logs.errors")
                .tag("logger", extractLoggerCategory(logger))
                .register(meterRegistry)
                .increment();
        }
        
        // Extract performance metrics
        extractPerformanceMetrics(logEntry);
        
        // Extract business metrics
        extractBusinessMetrics(logEntry);
    }
    
    private void extractPerformanceMetrics(Map<String, Object> logEntry) {
        Object duration = logEntry.get("duration");
        if (duration instanceof Number) {
            Timer.builder("logs.performance")
                .tag("operation", (String) logEntry.getOrDefault("operation", "unknown"))
                .register(meterRegistry)
                .record(((Number) duration).longValue(), TimeUnit.MILLISECONDS);
        }
    }
    
    private void extractBusinessMetrics(Map<String, Object> logEntry) {
        String eventType = (String) logEntry.get("eventType");
        if (eventType != null) {
            Counter.builder("logs.business_events")
                .tag("event_type", eventType)
                .register(meterRegistry)
                .increment();
        }
    }
    
    private String extractLoggerCategory(String loggerName) {
        if (loggerName == null) return "unknown";
        
        // Extract top-level package
        int firstDot = loggerName.indexOf('.');
        return firstDot > 0 ? loggerName.substring(0, firstDot) : loggerName;
    }
}
```

### Alerting based on logs

#### Structured log alerting
```java
@Service
public class LogAlertingService {
    
    private final AlertService alertService;
    
    @Autowired
    public LogAlertingService(AlertService alertService) {
        this.alertService = alertService;
    }
    
    public void checkForAlerts(Map<String, Object> logEntry) {
        String level = (String) logEntry.get("level");
        String message = (String) logEntry.get("message");
        
        // Alert on ERROR level
        if ("ERROR".equals(level)) {
            alertService.sendAlert("Application Error", 
                String.format("Error in %s: %s", logEntry.get("logger"), message),
                AlertSeverity.HIGH);
        }
        
        // Alert on security events
        if (message != null && message.contains("SECURITY")) {
            alertService.sendAlert("Security Event", 
                (String) message, 
                AlertSeverity.CRITICAL);
        }
        
        // Alert on performance issues
        Object duration = logEntry.get("duration");
        if (duration instanceof Number && ((Number) duration).longValue() > 5000) {
            alertService.sendAlert("Performance Issue",
                String.format("Slow operation detected: %d ms", duration),
                AlertSeverity.MEDIUM);
        }
        
        // Alert on business rule violations
        String eventType = (String) logEntry.get("eventType");
        if ("BUSINESS_RULE_VIOLATION".equals(eventType)) {
            alertService.sendAlert("Business Rule Violation",
                (String) logEntry.get("details"),
                AlertSeverity.HIGH);
        }
    }
}
```

## Best Practices

### 1. Schema design

#### Consistent field naming
```java
public class LoggingSchemaStandards {
    
    // Standard field names
    public static final String FIELD_TIMESTAMP = "@timestamp";
    public static final String FIELD_LEVEL = "level";
    public static final String FIELD_LOGGER = "logger";
    public static final String FIELD_MESSAGE = "message";
    public static final String FIELD_THREAD = "thread";
    public static final String FIELD_EXCEPTION = "exception";
    
    // Correlation fields
    public static final String FIELD_CORRELATION_ID = "correlationId";
    public static final String FIELD_TRACE_ID = "traceId";
    public static final String FIELD_SPAN_ID = "spanId";
    
    // Business fields
    public static final String FIELD_EVENT_TYPE = "eventType";
    public static final String FIELD_USER_ID = "userId";
    public static final String FIELD_OPERATION = "operation";
    public static final String FIELD_DURATION = "duration";
    
    // System fields
    public static final String FIELD_HOSTNAME = "hostname";
    public static final String FIELD_APPLICATION = "application";
    public static final String FIELD_VERSION = "version";
    public static final String FIELD_ENVIRONMENT = "environment";
    
    public static Map<String, Object> createBaseLogEntry() {
        return Map.of(
            FIELD_TIMESTAMP, Instant.now().toString(),
            FIELD_HOSTNAME, getHostname(),
            FIELD_APPLICATION, getApplicationName(),
            FIELD_VERSION, getApplicationVersion(),
            FIELD_ENVIRONMENT, getEnvironment()
        );
    }
    
    // Helper methods for system information
    private static String getHostname() {
        try {
            return InetAddress.getLocalHost().getHostName();
        } catch (UnknownHostException e) {
            return "unknown";
        }
    }
    
    private static String getApplicationName() {
        return System.getProperty("spring.application.name", "unknown");
    }
    
    private static String getApplicationVersion() {
        return System.getProperty("app.version", "1.0.0");
    }
    
    private static String getEnvironment() {
        return System.getProperty("app.environment", "dev");
    }
}
```

### 2. Content standards

#### Structured message format
```java
public class StructuredMessageStandards {
    
    public static void logUserAction(String userId, String action, Map<String, Object> context) {
        Map<String, Object> logEntry = LoggingSchemaStandards.createBaseLogEntry();
        logEntry.putAll(Map.of(
            LoggingSchemaStandards.FIELD_EVENT_TYPE, "USER_ACTION",
            LoggingSchemaStandards.FIELD_USER_ID, userId,
            LoggingSchemaStandards.FIELD_OPERATION, action,
            "context", context
        ));
        
        logger.info("User action performed: {}", logEntry);
    }
    
    public static void logBusinessOperation(String operation, long duration, 
                                           Map<String, Object> input, Object result) {
        Map<String, Object> logEntry = LoggingSchemaStandards.createBaseLogEntry();
        logEntry.putAll(Map.of(
            LoggingSchemaStandards.FIELD_EVENT_TYPE, "BUSINESS_OPERATION",
            LoggingSchemaStandards.FIELD_OPERATION, operation,
            LoggingSchemaStandards.FIELD_DURATION, duration,
            "input", input,
            "result", result,
            "success", true
        ));
        
        if (duration > 1000) {
            logger.warn("Business operation completed: {}", logEntry);
        } else {
            logger.info("Business operation completed: {}", logEntry);
        }
    }
    
    public static void logError(String operation, Exception exception, Map<String, Object> context) {
        Map<String, Object> logEntry = LoggingSchemaStandards.createBaseLogEntry();
        logEntry.putAll(Map.of(
            LoggingSchemaStandards.FIELD_EVENT_TYPE, "ERROR",
            LoggingSchemaStandards.FIELD_OPERATION, operation,
            "errorType", exception.getClass().getSimpleName(),
            "errorMessage", exception.getMessage(),
            "context", context
        ));
        
        logger.error("Operation failed: {}", logEntry, exception);
    }
    
    public static void logSecurityEvent(String eventType, String userId, String resource, 
                                       Map<String, Object> details) {
        Map<String, Object> logEntry = LoggingSchemaStandards.createBaseLogEntry();
        logEntry.putAll(Map.of(
            LoggingSchemaStandards.FIELD_EVENT_TYPE, "SECURITY_EVENT",
            "securityEventType", eventType,
            LoggingSchemaStandards.FIELD_USER_ID, userId,
            "resource", resource,
            "details", details
        ));
        
        Logger securityLogger = LoggerFactory.getLogger("security");
        securityLogger.info("Security event: {}", logEntry);
    }
}
```

### 3. Implementation guidelines

#### Utility classes
```java
public class StructuredLoggingUtils {
    
    public static StructuredLogger createLogger(Class<?> clazz) {
        return new StructuredLogger(LoggerFactory.getLogger(clazz));
    }
    
    public static StructuredLogger createLogger(String name) {
        return new StructuredLogger(LoggerFactory.getLogger(name));
    }
    
    public static class StructuredLogger {
        
        private final Logger delegate;
        
        public StructuredLogger(Logger delegate) {
            this.delegate = delegate;
        }
        
        public void info(String eventType, Map<String, Object> data) {
            Map<String, Object> logEntry = LoggingSchemaStandards.createBaseLogEntry();
            logEntry.put(LoggingSchemaStandards.FIELD_EVENT_TYPE, eventType);
            logEntry.putAll(data);
            
            delegate.info("{}: {}", eventType, logEntry);
        }
        
        public void error(String eventType, Throwable throwable, Map<String, Object> data) {
            Map<String, Object> logEntry = LoggingSchemaStandards.createBaseLogEntry();
            logEntry.put(LoggingSchemaStandards.FIELD_EVENT_TYPE, eventType);
            logEntry.putAll(data);
            
            delegate.error("{}: {}", eventType, logEntry, throwable);
        }
        
        public void withCorrelationId(String correlationId, Runnable action) {
            String previousId = MDC.get(LoggingSchemaStandards.FIELD_CORRELATION_ID);
            try {
                MDC.put(LoggingSchemaStandards.FIELD_CORRELATION_ID, correlationId);
                action.run();
            } finally {
                if (previousId != null) {
                    MDC.put(LoggingSchemaStandards.FIELD_CORRELATION_ID, previousId);
                } else {
                    MDC.remove(LoggingSchemaStandards.FIELD_CORRELATION_ID);
                }
            }
        }
    }
}
```

## Troubleshooting

### Распространенные проблемы

#### JSON serialization errors
```java
// Problem: Objects not serializable to JSON
// Solution: Custom serialization

public class JsonSerializationFix {
    
    private static final ObjectMapper objectMapper = new ObjectMapper()
        .registerModule(new JavaTimeModule())
        .configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
    
    public String safeToJson(Object obj) {
        try {
            return objectMapper.writeValueAsString(obj);
        } catch (JsonProcessingException e) {
            // Fallback to string representation
            return "{\"error\":\"Serialization failed\",\"object\":\"" + obj.toString() + "\"}";
        }
    }
    
    public void logWithFallback(Object data) {
        String json = safeToJson(data);
        logger.info("Data: {}", json);
    }
}
```

#### Correlation ID propagation issues
```java
// Problem: Correlation IDs lost in async operations
// Solution: Proper context propagation

public class CorrelationFix {
    
    public void asyncOperationWithCorrelation(CompletableFuture<String> future) {
        String correlationId = CorrelationIdUtils.getCorrelationId();
        
        future.whenComplete((result, error) -> {
            // Restore correlation ID in callback
            CorrelationIdUtils.setCorrelationId(correlationId);
            
            try {
                if (error != null) {
                    logger.error("Async operation failed", error);
                } else {
                    logger.info("Async operation completed: {}", result);
                }
            } finally {
                CorrelationIdUtils.cleanup();
            }
        });
    }
    
    public void reactiveOperationWithCorrelation(Mono<String> mono) {
        mono.doOnEach(signal -> {
            // Correlation ID should be available in Reactor context
            String correlationId = signal.getContext().getOrDefault("correlationId", "unknown");
            CorrelationIdUtils.setCorrelationId(correlationId);
            
            try {
                if (signal.hasValue()) {
                    logger.info("Reactive operation value: {}", signal.get());
                } else if (signal.hasError()) {
                    logger.error("Reactive operation error", signal.getThrowable());
                }
            } finally {
                CorrelationIdUtils.cleanup();
            }
        });
    }
}
```

#### Performance degradation
```java
// Problem: Structured logging impacting performance
// Solution: Optimize and measure

public class PerformanceOptimization {
    
    public void benchmarkLogging() {
        Map<String, Object> testData = Map.of(
            "userId", "123",
            "action", "login",
            "timestamp", System.currentTimeMillis(),
            "metadata", Map.of("ip", "192.168.1.1", "userAgent", "Chrome")
        );
        
        // Benchmark traditional logging
        long start = System.nanoTime();
        for (int i = 0; i < 10000; i++) {
            logger.info("User {} performed {} at {}", 
                       testData.get("userId"), 
                       testData.get("action"), 
                       testData.get("timestamp"));
        }
        long traditional = System.nanoTime() - start;
        
        // Benchmark structured logging
        start = System.nanoTime();
        for (int i = 0; i < 10000; i++) {
            logger.info("Structured event: {}", testData);
        }
        long structured = System.nanoTime() - start;
        
        System.out.printf("Traditional: %.2fms, Structured: %.2fms%n", 
            traditional / 1e6, structured / 1e6);
    }
    
    public void optimizeExpensiveLogging() {
        // Only create expensive data structures when needed
        if (logger.isDebugEnabled()) {
            Map<String, Object> expensiveData = buildExpensiveDataStructure();
            logger.debug("Expensive debug data: {}", expensiveData);
        }
        
        // Use lazy evaluation
        logger.debug("Lazy evaluation: {}", () -> buildExpensiveDataStructure());
    }
    
    private Map<String, Object> buildExpensiveDataStructure() {
        // Simulate expensive operation
        try {
            Thread.sleep(1);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        
        return Map.of("expensive", "data", "computed", System.currentTimeMillis());
    }
}
```

### Debug techniques

#### Log inspection tools
```java
public class LogInspectionTools {
    
    public void inspectJsonLog(String jsonLog) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            JsonNode root = mapper.readTree(jsonLog);
            
            System.out.println("=== Log Entry Inspection ===");
            System.out.println("Timestamp: " + root.get("@timestamp").asText());
            System.out.println("Level: " + root.get("level").asText());
            System.out.println("Logger: " + root.get("logger").asText());
            System.out.println("Message: " + root.get("message").asText());
            
            // Check for correlation ID
            JsonNode correlationId = root.get("correlationId");
            if (correlationId != null) {
                System.out.println("Correlation ID: " + correlationId.asText());
            }
            
            // Check for MDC
            JsonNode mdc = root.get("mdc");
            if (mdc != null) {
                System.out.println("MDC entries:");
                mdc.fields().forEachRemaining(entry -> 
                    System.out.println("  " + entry.getKey() + " = " + entry.getValue()));
            }
            
            // Validate structure
            validateLogStructure(root);
            
        } catch (JsonProcessingException e) {
            System.err.println("Invalid JSON log: " + e.getMessage());
        }
    }
    
    private void validateLogStructure(JsonNode logEntry) {
        List<String> requiredFields = Arrays.asList("@timestamp", "level", "logger", "message");
        List<String> missingFields = new ArrayList<>();
        
        for (String field : requiredFields) {
            if (!logEntry.has(field)) {
                missingFields.add(field);
            }
        }
        
        if (!missingFields.isEmpty()) {
            System.err.println("Missing required fields: " + missingFields);
        }
    }
    
    public void analyzeLogPatterns(List<String> logLines) {
        Map<String, Integer> eventTypeCounts = new HashMap<>();
        Map<String, Integer> levelCounts = new HashMap<>();
        Map<String, List<Long>> timestampsByHour = new HashMap<>();
        
        ObjectMapper mapper = new ObjectMapper();
        
        for (String line : logLines) {
            try {
                JsonNode logEntry = mapper.readTree(line);
                
                // Count event types
                String eventType = logEntry.path("eventType").asText("unknown");
                eventTypeCounts.merge(eventType, 1, Integer::sum);
                
                // Count levels
                String level = logEntry.path("level").asText("unknown");
                levelCounts.merge(level, 1, Integer::sum);
                
                // Group by hour
                long timestamp = logEntry.path("@timestamp").asLong();
                String hourKey = Instant.ofEpochMilli(timestamp)
                    .atZone(ZoneId.systemDefault())
                    .format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH"));
                timestampsByHour.computeIfAbsent(hourKey, k -> new ArrayList<>()).add(timestamp);
                
            } catch (JsonProcessingException e) {
                // Skip invalid lines
            }
        }
        
        System.out.println("Event types: " + eventTypeCounts);
        System.out.println("Log levels: " + levelCounts);
        System.out.println("Logs per hour: " + timestampsByHour.entrySet().stream()
            .collect(Collectors.toMap(Map.Entry::getKey, e -> e.getValue().size())));
    }
}
```

#### Configuration validation
```java
@Configuration
public class StructuredLoggingValidation {
    
    @PostConstruct
    public void validateStructuredLoggingSetup() {
        Logger testLogger = LoggerFactory.getLogger("validation");
        
        // Test basic structured logging
        Map<String, Object> testEntry = LoggingSchemaStandards.createBaseLogEntry();
        testEntry.put("testEvent", "validation");
        testEntry.put("timestamp", Instant.now().toString());
        
        testLogger.info("Validation test: {}", testEntry);
        
        // Test correlation ID
        String testCorrelationId = "test-correlation-" + UUID.randomUUID();
        CorrelationIdUtils.setCorrelationId(testCorrelationId);
        testLogger.info("Correlation test: {}", Map.of("correlationId", testCorrelationId));
        CorrelationIdUtils.cleanup();
        
        // Test MDC
        MDC.put("testKey", "testValue");
        testLogger.info("MDC test: {}", Map.of("mdcTest", true));
        MDC.clear();
        
        // Test JSON serialization
        try {
            ObjectMapper mapper = new ObjectMapper();
            String json = mapper.writeValueAsString(testEntry);
            JsonNode parsed = mapper.readTree(json);
            
            if (!parsed.has("@timestamp")) {
                throw new IllegalStateException("Missing @timestamp field in JSON output");
            }
            
            if (!parsed.has("application")) {
                throw new IllegalStateException("Missing application field in JSON output");
            }
            
        } catch (JsonProcessingException e) {
            throw new IllegalStateException("JSON serialization test failed", e);
        }
        
        logger.info("Structured logging validation completed successfully");
    }
}
```

## Заключение

**Структурированное логирование** — это современный подход к логированию, который обеспечивает эффективный анализ, поиск и агрегацию логов в распределенных системах. JSON-формат и correlation IDs позволяют создавать наблюдаемые и debuggable приложения.

### Ключевые возможности:

1. **JSON Logging** — структурированные логи в JSON формате
2. **Correlation IDs** — связь событий между сервисами
3. **Distributed Tracing** — интеграция с OpenTelemetry
4. **Context Propagation** — передача контекста между потоками
5. **Custom Metadata** — расширяемые поля и бизнес-контекст
6. **Performance Optimization** — эффективная сериализация
7. **Log Aggregation** — централизованный сбор и анализ
8. **Monitoring** — метрики и алертинг на основе логов

### Архитектурные преимущества:

#### Observability:
- **Distributed Tracing** — отслеживание запросов через сервисы
- **Correlation** — связь лог-сообщений между компонентами
- **Business Context** — логи с бизнес-значением
- **Performance Monitoring** — метрики на основе логов

#### Maintainability:
- **Structured Search** — эффективный поиск по полям
- **Automated Analysis** — машинная обработка логов
- **Standardization** — единый формат логов
- **Evolution** — безопасное изменение схемы

### Когда использовать структурированное логирование:

✅ **Microservices** — связь событий между сервисами
✅ **Distributed Systems** — correlation и tracing
✅ **Cloud Applications** — интеграция с облачными сервисами
✅ **Business Analytics** — логи с бизнес-метаданными
✅ **DevOps** — автоматизированный анализ и алертинг
✅ **High-Volume Logging** — эффективная агрегация
✅ **Compliance** — аудитируемые логи
✅ **Modern Applications** — JSON-first архитектура

### Когда НЕ использовать:

❌ **Simple Applications** — для базовых логов достаточно text
❌ **Legacy Systems** — сложная интеграция со старым кодом
❌ **Low-Volume Logging** — overhead не оправдан
❌ **Human-Only Analysis** — если логи читают только люди
❌ **Resource-Constrained** — JSON serialization требует ресурсов
❌ **Single Service** — correlation не нужен

### Best practices:

1. **Schema Standards** — единый формат полей и naming
2. **Correlation IDs** — обязательные IDs для всех запросов
3. **Context Propagation** — правильная передача контекста
4. **Performance** — оптимизация expensive операций
5. **Security** — безопасное логирование sensitive данных
6. **Monitoring** — алертинг на основе structured логов
7. **Evolution** — backward-compatible изменения схемы
8. **Validation** — проверка структуры логов

Структурированное логирование является foundation современной observable архитектуры, позволяя создавать надежные и поддерживаемые распределенные системы с эффективным мониторингом и debugging. 🚀
