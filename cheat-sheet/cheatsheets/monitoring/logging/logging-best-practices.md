# Лучшие практики логирования в Java

Комплексное руководство по лучшим практикам логирования в Java-приложениях: от базовых принципов до продвинутых техник, обеспечивающих надежность, производительность и полезность логов.

**Дата последнего обновления:** 2026-01-21

## Полезные ссылки

### Стандарты и спецификации
- [RFC 5424](https://tools.ietf.org/html/rfc5424) - Syslog Protocol
- [Structured Logging](https://messaging.apache.org/docs/structured-logging.html)
- [SLF4J Specification](https://www.slf4j.org/spec.html)

### Java библиотеки
- [SLF4J](https://www.slf4j.org/) - Simple Logging Facade
- [Logback](https://logback.qos.ch/) - Logging Framework
- [Log4j2](https://logging.apache.org/log4j/2.x/) - Advanced Logging
- [java.util.logging](https://docs.oracle.com/javase/8/docs/api/java/util/logging/package-summary.html)

### Статьи и ресурсы
- [Logging Best Practices](https://www.martinfowler.com/articles/logging-best-practices.html)
- [Structured Logging for Microservices](https://www.thoughtworks.com/radar/techniques/structured-logging)
- [The Problem with Logging](https://www.kartar.net/2014/12/the-problem-with-logging/)

### См. также
- `logging-basics.md` - Основы логирования
- `logback.md` - Конфигурация Logback
- `structured-logging.md` - Структурированное логирование
- `centralized-logging.md` - Централизованное логирование

## Содержание

- [Введение](#введение)
- [Принципы эффективного логирования](#принципы-эффективного-логирования)
- [Уровни логирования](#уровни-логирования)
- [Форматирование сообщений](#форматирование-сообщений)
- [Структурированное логирование](#структурированное-логирование)
- [Контекст и корреляция](#контекст-и-корреляция)
- [Производительность](#производительность)
- [Безопасность](#безопасность)
- [Тестирование логирования](#тестирование-логирования)
- [Мониторинг и алертинг](#мониторинг-и-алертинг)
- [Best practices по языкам](#best-practices-по-языкам)
- [Антипаттерны](#антипаттерны)
- [Заключение](#заключение)

## Введение

**Лучшие практики логирования** — это набор рекомендаций и техник, которые помогают создавать эффективную систему логирования, обеспечивающую надежность, наблюдаемость и отлаживаемость приложений.

### Почему важны best practices?

Логирование является критически важной частью разработки ПО:

1. **Отладка и Troubleshooting** — быстрая диагностика проблем
2. **Мониторинг и Observability** — понимание состояния системы
3. **Business Intelligence** — анализ поведения пользователей
4. **Compliance и Audit** — соответствие требованиям
5. **Performance Analysis** — оптимизация производительности
6. **Security Monitoring** — обнаружение угроз и инцидентов

### Основные принципы

#### 1. Логи для людей и машин
```java
// Плохо: Неинформативное сообщение
logger.error("Error occurred");

// Хорошо: Конкретная информация для отладки
logger.error("Failed to process payment for user {}: payment method {} invalid", 
             userId, paymentMethod, exception);

// Отлично: Структурированные данные для анализа
logger.error("Payment processing failed", 
             kv("userId", userId),
             kv("paymentMethod", paymentMethod),
             kv("amount", amount),
             kv("errorCode", error.getCode()));
```

#### 2. Уровни логирования по назначению
- **ERROR** — системные ошибки, требующие внимания
- **WARN** — потенциальные проблемы, не критичные
- **INFO** — важные бизнес-события и состояния
- **DEBUG** — детальная информация для отладки
- **TRACE** — максимально детальная информация

#### 3. Консистентность формата
```java
// Стандартизированный формат сообщений
public class PaymentService {
    
    private static final Logger logger = LoggerFactory.getLogger(PaymentService.class);
    
    public PaymentResult processPayment(PaymentRequest request) {
        String correlationId = request.getCorrelationId();
        
        logger.info("Processing payment started", 
                   kv("correlationId", correlationId),
                   kv("userId", request.getUserId()),
                   kv("amount", request.getAmount()));
        
        try {
            // Business logic
            PaymentResult result = process(request);
            
            logger.info("Payment processed successfully", 
                       kv("correlationId", correlationId),
                       kv("transactionId", result.getTransactionId()));
            
            return result;
            
        } catch (PaymentException e) {
            logger.error("Payment processing failed", 
                        kv("correlationId", correlationId),
                        kv("errorCode", e.getErrorCode()),
                        kv("errorMessage", e.getMessage()));
            throw e;
            
        } catch (Exception e) {
            logger.error("Unexpected error during payment processing", 
                        kv("correlationId", correlationId), e);
            throw new SystemException("Payment processing failed", e);
        }
    }
}
```

## Принципы эффективного логирования

### 1. Логи должны быть читаемыми

#### Избегайте технического жаргона
```java
// Плохо
logger.info("HTTP POST /api/v1/users with payload size 2048 bytes returned 201");

// Хорошо
logger.info("New user registration completed", 
           kv("endpoint", "/api/v1/users"),
           kv("method", "POST"),
           kv("payloadSize", 2048),
           kv("responseCode", 201));
```

#### Используйте понятный язык
```java
// Плохо
logger.error("NPE in UserService.authenticate()");

// Хорошо
logger.error("User authentication failed due to missing user data", 
            kv("service", "UserService"),
            kv("method", "authenticate"),
            kv("error", "NullPointerException"));
```

### 2. Логи должны быть полными

#### Включайте контекст
```java
public class OrderService {
    
    public Order createOrder(CreateOrderRequest request) {
        String userId = SecurityContext.getCurrentUserId();
        String sessionId = SecurityContext.getSessionId();
        String requestId = MDC.get("requestId");
        
        logger.info("Creating new order", 
                   kv("userId", userId),
                   kv("sessionId", sessionId),
                   kv("requestId", requestId),
                   kv("itemCount", request.getItems().size()),
                   kv("totalAmount", request.getTotalAmount()));
        
        Order order = new Order();
        order.setUserId(userId);
        order.setItems(request.getItems());
        
        // Дополнительный контекст
        MDC.put("orderId", order.getId());
        
        try {
            order = orderRepository.save(order);
            
            logger.info("Order created successfully", 
                       kv("orderId", order.getId()),
                       kv("orderNumber", order.getOrderNumber()));
            
            return order;
            
        } catch (Exception e) {
            logger.error("Failed to create order", 
                        kv("userId", userId),
                        kv("itemCount", request.getItems().size()), e);
            throw e;
            
        } finally {
            MDC.remove("orderId");
        }
    }
}
```

### 3. Логи должны быть эффективными

#### Избегайте лишней информации
```java
// Плохо: Слишком много деталей в INFO
logger.info("Database connection pool stats: active={}, idle={}, waiting={}, created={}", 
           pool.getActiveCount(), pool.getIdleCount(), pool.getWaitingCount(), pool.getCreatedCount());

// Хорошо: Ключевые метрики в INFO, детали в DEBUG
logger.info("Database connection pool utilization", 
           kv("activeConnections", pool.getActiveCount()),
           kv("idleConnections", pool.getIdleCount()));

logger.debug("Detailed connection pool statistics", 
            kv("active", pool.getActiveCount()),
            kv("idle", pool.getIdleCount()),
            kv("waiting", pool.getWaitingCount()),
            kv("created", pool.getCreatedCount()),
            kv("destroyed", pool.getDestroyedCount()));
```

## Уровни логирования

### ERROR уровень

#### Когда использовать ERROR
```java
public class DatabaseService {
    
    public <T> T executeInTransaction(TransactionCallback<T> callback) {
        Transaction transaction = null;
        
        try {
            transaction = transactionManager.beginTransaction();
            T result = callback.doInTransaction();
            transaction.commit();
            return result;
            
        } catch (DataIntegrityViolationException e) {
            // ERROR: Нарушение целостности данных - серьезная проблема
            logger.error("Data integrity violation in transaction", 
                        kv("transactionId", transaction != null ? transaction.getId() : null),
                        kv("constraint", e.getConstraintName()), e);
            throw e;
            
        } catch (DeadlockException e) {
            // ERROR: Deadlock - требует внимания администратора
            logger.error("Database deadlock detected", 
                        kv("transactionId", transaction.getId()), e);
            throw e;
            
        } catch (Exception e) {
            // ERROR: Неожиданная ошибка в транзакции
            logger.error("Unexpected error in database transaction", 
                        kv("transactionId", transaction.getId()), e);
            throw e;
            
        } finally {
            if (transaction != null && transaction.isActive()) {
                try {
                    transaction.rollback();
                } catch (Exception e) {
                    logger.warn("Failed to rollback transaction", 
                               kv("transactionId", transaction.getId()), e);
                }
            }
        }
    }
}
```

#### Что логировать в ERROR
- **Системные ошибки** — сбои в работе системы
- **Data corruption** — повреждение данных
- **Security violations** — нарушения безопасности
- **Business rule violations** — критические бизнес-ошибки
- **External service failures** — отказы внешних сервисов
- **Configuration errors** — ошибки конфигурации

### WARN уровень

#### Когда использовать WARN
```java
public class CacheService {
    
    private final Cache<String, Object> cache;
    private final MeterRegistry meterRegistry;
    
    public <T> T get(String key, Class<T> type) {
        T value = cache.get(key, type);
        
        if (value == null) {
            // DEBUG: Cache miss - нормальная ситуация
            logger.debug("Cache miss for key", kv("key", key));
            return null;
        }
        
        // Проверяем, не истек ли TTL
        if (isExpired(value)) {
            // WARN: Устаревшие данные в кеше - потенциальная проблема
            logger.warn("Expired data found in cache", 
                       kv("key", key),
                       kv("age", getAgeInMinutes(value)));
            
            cache.invalidate(key);
            return null;
        }
        
        return value;
    }
    
    private boolean isExpired(Object value) {
        // Проверка на истечение TTL
        return getAgeInMinutes(value) > 30; // 30 минут
    }
    
    private long getAgeInMinutes(Object value) {
        // Расчет возраста данных
        return 0; // Implementation
    }
}
```

#### Что логировать в WARN
- **Performance issues** — проблемы производительности
- **Resource exhaustion** — исчерпание ресурсов
- **Deprecated usage** — использование устаревшего API
- **Configuration warnings** — предупреждения конфигурации
- **Retry attempts** — попытки повторения операций
- **Unusual conditions** — необычные ситуации

### INFO уровень

#### Когда использовать INFO
```java
@Service
public class UserRegistrationService {
    
    private final UserRepository userRepository;
    private final EmailService emailService;
    private final MeterRegistry meterRegistry;
    
    @Autowired
    public UserRegistrationService(UserRepository userRepository, 
                                 EmailService emailService,
                                 MeterRegistry meterRegistry) {
        this.userRepository = userRepository;
        this.emailService = emailService;
        this.meterRegistry = meterRegistry;
    }
    
    @Transactional
    public User registerUser(RegistrationRequest request) {
        String email = request.getEmail();
        
        logger.info("Starting user registration", kv("email", email));
        
        // Проверка существующего пользователя
        if (userRepository.existsByEmail(email)) {
            logger.warn("Registration attempt for existing email", kv("email", email));
            throw new UserAlreadyExistsException("User already exists");
        }
        
        // Создание пользователя
        User user = new User();
        user.setEmail(email);
        user.setName(request.getName());
        
        user = userRepository.save(user);
        
        logger.info("User registered successfully", 
                   kv("userId", user.getId()),
                   kv("email", email));
        
        // Отправка welcome email
        try {
            emailService.sendWelcomeEmail(user);
            logger.debug("Welcome email sent", kv("userId", user.getId()));
            
        } catch (Exception e) {
            // Не критичная ошибка - логируем как WARN
            logger.warn("Failed to send welcome email", 
                       kv("userId", user.getId()), e);
        }
        
        // Метрики
        meterRegistry.counter("user.registrations").increment();
        
        return user;
    }
}
```

#### Что логировать в INFO
- **Business events** — важные бизнес-события
- **System state changes** — изменения состояния системы
- **User actions** — действия пользователей
- **Integration points** — взаимодействие с внешними системами
- **Batch operations** — результаты пакетных операций
- **Startup/shutdown** — запуск и остановка компонентов

### DEBUG и TRACE уровни

#### Когда использовать DEBUG
```java
public class PaymentProcessor {
    
    private static final Logger logger = LoggerFactory.getLogger(PaymentProcessor.class);
    
    public PaymentResult process(PaymentRequest request) {
        logger.debug("Processing payment request", 
                    kv("amount", request.getAmount()),
                    kv("currency", request.getCurrency()),
                    kv("paymentMethod", request.getPaymentMethod()));
        
        // Детальная отладочная информация
        if (logger.isDebugEnabled()) {
            logger.debug("Payment request details: {}", toJson(request));
        }
        
        // Валидация
        validateRequest(request);
        logger.debug("Payment request validation passed");
        
        // Обработка платежа
        PaymentResult result = doProcessPayment(request);
        
        logger.debug("Payment processing completed", 
                    kv("transactionId", result.getTransactionId()),
                    kv("status", result.getStatus()));
        
        return result;
    }
    
    private void validateRequest(PaymentRequest request) {
        logger.trace("Validating payment amount", kv("amount", request.getAmount()));
        
        if (request.getAmount() <= 0) {
            throw new ValidationException("Amount must be positive");
        }
        
        logger.trace("Validating payment method", kv("method", request.getPaymentMethod()));
        
        if (!isSupportedPaymentMethod(request.getPaymentMethod())) {
            throw new ValidationException("Unsupported payment method");
        }
        
        logger.trace("Payment request validation completed");
    }
}
```

## Форматирование сообщений

### Консистентные шаблоны

#### Стандартизированные шаблоны сообщений
```java
public class LoggingStandards {
    
    // Шаблоны для разных типов событий
    public static final String USER_ACTION = "User {} performed action {}";
    public static final String BUSINESS_EVENT = "Business event: {} for {}";
    public static final String SYSTEM_STATE = "System state changed: {} -> {}";
    public static final String PERFORMANCE_METRIC = "Performance metric: {} = {}";
    public static final String ERROR_OCCURRED = "Error occurred in {}: {}";
    
    // Методы для стандартизированного логирования
    public static void logUserAction(Logger logger, String userId, String action) {
        logger.info(USER_ACTION, kv("userId", userId), kv("action", action));
    }
    
    public static void logBusinessEvent(Logger logger, String event, String entity) {
        logger.info(BUSINESS_EVENT, kv("event", event), kv("entity", entity));
    }
    
    public static void logSystemStateChange(Logger logger, String from, String to) {
        logger.info(SYSTEM_STATE, kv("from", from), kv("to", to));
    }
    
    public static void logPerformanceMetric(Logger logger, String metric, double value) {
        logger.info(PERFORMANCE_METRIC, kv("metric", metric), kv("value", value));
    }
    
    public static void logError(Logger logger, String component, String message, Exception e) {
        logger.error(ERROR_OCCURRED, kv("component", component), kv("message", message), e);
    }
}

// Использование стандартов
@Service
public class OrderService {
    
    private static final Logger logger = LoggerFactory.getLogger(OrderService.class);
    
    public Order createOrder(CreateOrderRequest request) {
        String userId = SecurityContext.getCurrentUserId();
        
        LoggingStandards.logUserAction(logger, userId, "CREATE_ORDER");
        
        try {
            Order order = doCreateOrder(request);
            
            LoggingStandards.logBusinessEvent(logger, "ORDER_CREATED", order.getId());
            
            return order;
            
        } catch (Exception e) {
            LoggingStandards.logError(logger, "OrderService", "Failed to create order", e);
            throw e;
        }
    }
}
```

### Избегайте проблем с форматированием

#### Безопасное форматирование
```java
public class SafeLogging {
    
    public static void logUserData(Logger logger, String userId, String sensitiveData) {
        // Плохо: Чувствительные данные в сообщении
        // logger.info("Processing user {} with data {}", userId, sensitiveData);
        
        // Хорошо: Чувствительные данные в контексте
        logger.info("Processing user data", 
                   kv("userId", userId),
                   kv("dataLength", sensitiveData.length()));
        
        // DEBUG уровень для детальной информации (только в dev)
        logger.debug("Detailed user data", 
                    kv("userId", userId),
                    kv("data", maskSensitiveData(sensitiveData)));
    }
    
    public static void logFileOperation(Logger logger, String filePath, String operation) {
        // Избегайте логирования полного пути в production
        String safePath = sanitizeFilePath(filePath);
        
        logger.info("File operation completed", 
                   kv("operation", operation),
                   kv("file", safePath));
    }
    
    private static String maskSensitiveData(String data) {
        if (data == null || data.length() <= 4) return data;
        return data.substring(0, 2) + "***" + data.substring(data.length() - 2);
    }
    
    private static String sanitizeFilePath(String path) {
        // Удаляем чувствительную информацию из пути
        return path.replaceAll("/home/[^/]+/", "/home/user/");
    }
}
```

## Структурированное логирование

### JSON формат

#### Структурированные логи с контекстом
```java
@Configuration
public class StructuredLoggingConfig {
    
    @Bean
    public LoggerContext loggerContext() {
        LoggerContext context = (LoggerContext) LoggerFactory.getILoggerFactory();
        
        // Configure JSON layout
        ch.qos.logback.classic.Logger rootLogger = context.getLogger("ROOT");
        
        // Remove default appenders
        rootLogger.detachAndStopAllAppenders();
        
        // Add JSON appender
        Logger jsonLogger = createJsonLogger(context);
        rootLogger.addAppender(jsonLogger);
        
        return context;
    }
    
    private Logger createJsonLogger(LoggerContext context) {
        // JSON pattern layout
        PatternLayoutEncoder encoder = new PatternLayoutEncoder();
        encoder.setContext(context);
        encoder.setPattern("%message%n");
        encoder.start();
        
        // Console appender
        ConsoleAppender<ILoggingEvent> appender = new ConsoleAppender<>();
        appender.setContext(context);
        appender.setEncoder(encoder);
        appender.start();
        
        // Create logger
        ch.qos.logback.classic.Logger logger = context.getLogger("json");
        logger.addAppender(appender);
        logger.setLevel(Level.INFO);
        
        return logger;
    }
}

// Использование структурированного логирования
@Service
public class StructuredLoggingService {
    
    private static final Logger logger = LoggerFactory.getLogger(StructuredLoggingService.class);
    
    public void processBusinessEvent(BusinessEvent event) {
        // Структурированный лог с бизнес-контекстом
        logger.info("Business event processed", 
                   kv("eventId", event.getId()),
                   kv("eventType", event.getType()),
                   kv("userId", event.getUserId()),
                   kv("timestamp", event.getTimestamp()),
                   kv("metadata", event.getMetadata()),
                   kv("processingTime", System.currentTimeMillis() - event.getCreatedAt()));
    }
    
    public void logPerformanceMetrics(String operation, long duration, boolean success) {
        // Метрики производительности
        logger.info("Operation completed", 
                   kv("operation", operation),
                   kv("duration", duration),
                   kv("success", success),
                   kv("thread", Thread.currentThread().getName()),
                   kv("memory", Runtime.getRuntime().freeMemory()));
    }
    
    public void logErrorWithContext(Exception e, String operation, Map<String, Object> context) {
        // Ошибка с полным контекстом
        logger.error("Operation failed", 
                    kv("operation", operation),
                    kv("errorType", e.getClass().getSimpleName()),
                    kv("errorMessage", e.getMessage()),
                    kv("context", context), e);
    }
}
```

### MDC (Mapped Diagnostic Context)

#### Использование MDC для корреляции
```java
public class CorrelationLoggingAspect {
    
    @Around("@annotation(Correlated)")
    public Object logWithCorrelation(ProceedingJoinPoint joinPoint) throws Throwable {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Correlated correlated = signature.getMethod().getAnnotation(Correlated.class);
        
        // Установка correlation ID
        String correlationId = generateCorrelationId();
        MDC.put("correlationId", correlationId);
        
        // Дополнительный контекст
        MDC.put("operation", correlated.value());
        MDC.put("class", joinPoint.getTarget().getClass().getSimpleName());
        MDC.put("method", signature.getMethod().getName());
        
        try {
            logger.info("Operation started", 
                       kv("correlationId", correlationId),
                       kv("operation", correlated.value()));
            
            long startTime = System.nanoTime();
            Object result = joinPoint.proceed();
            long duration = System.nanoTime() - startTime;
            
            logger.info("Operation completed", 
                       kv("correlationId", correlationId),
                       kv("duration", duration / 1_000_000), // milliseconds
                       kv("success", true));
            
            return result;
            
        } catch (Exception e) {
            logger.error("Operation failed", 
                        kv("correlationId", correlationId), e);
            throw e;
            
        } finally {
            // Очистка MDC
            MDC.remove("correlationId");
            MDC.remove("operation");
            MDC.remove("class");
            MDC.remove("method");
        }
    }
    
    private String generateCorrelationId() {
        return UUID.randomUUID().toString();
    }
}

// Аннотация для маркировки коррелированных операций
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Correlated {
    String value();
}

// Использование
@Service
public class PaymentService {
    
    @Correlated("process_payment")
    public PaymentResult processPayment(PaymentRequest request) {
        // MDC автоматически управляется аспектом
        logger.debug("Validating payment", kv("amount", request.getAmount()));
        
        // Бизнес-логика
        return doProcessPayment(request);
    }
}
```

## Контекст и корреляция

### Request-scoped контекст

#### Thread-local контекст
```java
public class RequestContext {
    
    private static final ThreadLocal<RequestContextData> CONTEXT = new ThreadLocal<>();
    
    public static void init(String requestId, String userId, String sessionId) {
        RequestContextData data = new RequestContextData();
        data.setRequestId(requestId);
        data.setUserId(userId);
        data.setSessionId(sessionId);
        data.setStartTime(System.currentTimeMillis());
        
        CONTEXT.set(data);
        
        // Синхронизация с MDC для логирования
        MDC.put("requestId", requestId);
        MDC.put("userId", userId);
        MDC.put("sessionId", sessionId);
    }
    
    public static RequestContextData get() {
        return CONTEXT.get();
    }
    
    public static void clear() {
        RequestContextData data = CONTEXT.get();
        if (data != null) {
            long duration = System.currentTimeMillis() - data.getStartTime();
            logger.info("Request completed", 
                       kv("requestId", data.getRequestId()),
                       kv("duration", duration));
        }
        
        CONTEXT.remove();
        MDC.clear();
    }
    
    public static class RequestContextData {
        private String requestId;
        private String userId;
        private String sessionId;
        private long startTime;
        
        // Getters and setters
    }
}

// Фильтр для инициализации контекста
@Component
public class RequestContextFilter implements Filter {
    
    @Override
    public void doFilter(ServletRequest request, ServletResponse response, 
                        FilterChain chain) throws IOException, ServletException {
        
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        
        // Генерация request ID
        String requestId = generateRequestId(httpRequest);
        String userId = getUserId(httpRequest);
        String sessionId = getSessionId(httpRequest);
        
        // Инициализация контекста
        RequestContext.init(requestId, userId, sessionId);
        
        try {
            chain.doFilter(request, response);
        } finally {
            RequestContext.clear();
        }
    }
    
    private String generateRequestId(HttpServletRequest request) {
        String existingId = request.getHeader("X-Request-ID");
        return existingId != null ? existingId : UUID.randomUUID().toString();
    }
    
    private String getUserId(HttpServletRequest request) {
        return (String) request.getAttribute("userId");
    }
    
    private String getSessionId(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        return session != null ? session.getId() : null;
    }
}
```

### Distributed tracing

#### OpenTelemetry integration
```java
@Configuration
public class TracingConfig {
    
    @Bean
    public Tracer tracer() {
        return GlobalOpenTelemetry.getTracer("payment-service");
    }
}

// Аспект для автоматического трассирования
@Aspect
@Component
public class TracingAspect {
    
    @Autowired
    private Tracer tracer;
    
    @Around("@annotation(Traced)")
    public Object traceMethod(ProceedingJoinPoint joinPoint) throws Throwable {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Traced traced = signature.getMethod().getAnnotation(Traced.class);
        
        String operationName = traced.value().isEmpty() ? 
            signature.getMethod().getName() : traced.value();
        
        Span span = tracer.spanBuilder(operationName)
            .setAttribute("component", "payment-service")
            .setAttribute("method", signature.getMethod().getName())
            .startSpan();
        
        try (Scope scope = span.makeCurrent()) {
            // Добавление span context в MDC для логирования
            SpanContext spanContext = span.getSpanContext();
            MDC.put("traceId", spanContext.getTraceId());
            MDC.put("spanId", spanContext.getSpanId());
            
            logger.info("Method execution started", 
                       kv("operation", operationName),
                       kv("traceId", spanContext.getTraceId()));
            
            Object result = joinPoint.proceed();
            
            logger.info("Method execution completed", 
                       kv("operation", operationName),
                       kv("success", true));
            
            return result;
            
        } catch (Exception e) {
            span.recordException(e);
            span.setStatus(StatusCode.ERROR, e.getMessage());
            
            logger.error("Method execution failed", 
                        kv("operation", operationName), e);
            
            throw e;
            
        } finally {
            span.end();
            MDC.remove("traceId");
            MDC.remove("spanId");
        }
    }
}

// Аннотация для трассирования
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Traced {
    String value() default "";
}

// Использование
@Service
public class PaymentService {
    
    @Traced("process_payment")
    public PaymentResult processPayment(PaymentRequest request) {
        logger.debug("Processing payment", kv("amount", request.getAmount()));
        
        // Логи автоматически включают traceId и spanId
        return doProcessPayment(request);
    }
}
```

## Производительность

### Guard clauses

#### Условное логирование
```java
public class PerformanceLogging {
    
    private static final Logger logger = LoggerFactory.getLogger(PerformanceLogging.class);
    
    public void processItems(List<Item> items) {
        // Guard clause для избежания лишней работы
        if (!logger.isDebugEnabled()) {
            processWithoutDebugLogging(items);
            return;
        }
        
        // Детальное логирование только если DEBUG включен
        logger.debug("Processing {} items", items.size());
        
        for (int i = 0; i < items.size(); i++) {
            Item item = items.get(i);
            logger.debug("Processing item {}: {}", i, item.getId());
            
            processItem(item);
            
            logger.debug("Item {} processed successfully", item.getId());
        }
    }
    
    public void logPerformanceMetrics(String operation, long duration) {
        // Логирование метрик только если INFO включен
        if (logger.isInfoEnabled()) {
            logger.info("Performance metric", 
                       kv("operation", operation),
                       kv("duration", duration));
        }
        
        // Всегда собираем метрики (независимо от уровня логирования)
        recordMetric(operation, duration);
    }
    
    private void recordMetric(String operation, long duration) {
        // Отправка метрик в систему мониторинга
        meterRegistry.timer("operation.duration", "operation", operation)
                    .record(duration, TimeUnit.MILLISECONDS);
    }
}
```

### Async logging

#### Асинхронное логирование
```xml
<!-- Logback configuration with async appenders -->
<configuration>
    <!-- Async appender for high-throughput logging -->
    <appender name="ASYNC" class="ch.qos.logback.classic.AsyncAppender">
        <discardingThreshold>20</discardingThreshold>
        <queueSize>512</queueSize>
        <appender-ref ref="STDOUT" />
    </appender>
    
    <!-- Console appender -->
    <appender name="STDOUT" class="ch.qos.logback.classic.encoder.PatternLayoutEncoder">
        <encoder>
            <pattern>%d{HH:mm:ss.SSS} [%thread] %-5level %logger{36} - %msg%n</pattern>
        </encoder>
    </appender>
    
    <root level="INFO">
        <appender-ref ref="ASYNC" />
    </root>
</configuration>
```

```java
@Configuration
public class AsyncLoggingConfig {
    
    @Bean
    public AsyncAppender asyncAppender() {
        AsyncAppender asyncAppender = new AsyncAppender();
        
        // Настройка производительности
        asyncAppender.setDiscardingThreshold(20); // Discard less important logs if queue full
        asyncAppender.setQueueSize(512); // Queue size
        asyncAppender.setIncludeCallerData(false); // Performance optimization
        asyncAppender.setNeverBlock(true); // Don't block if queue full
        
        // Добавление console appender
        ConsoleAppender<ILoggingEvent> consoleAppender = createConsoleAppender();
        asyncAppender.addAppender(consoleAppender);
        
        asyncAppender.start();
        return asyncAppender;
    }
    
    private ConsoleAppender<ILoggingEvent> createConsoleAppender() {
        ConsoleAppender<ILoggingEvent> appender = new ConsoleAppender<>();
        
        PatternLayoutEncoder encoder = new PatternLayoutEncoder();
        encoder.setPattern("%d{HH:mm:ss.SSS} [%thread] %-5level %logger{36} - %msg%n");
        encoder.start();
        
        appender.setEncoder(encoder);
        appender.start();
        
        return appender;
    }
}
```

### Log sampling

#### Сэмплирование логов
```java
public class LogSampler {
    
    private final Map<String, Sampler> samplers = new ConcurrentHashMap<>();
    
    public boolean shouldLog(String loggerName, Level level, String message) {
        Sampler sampler = samplers.computeIfAbsent(loggerName, k -> new Sampler());
        return sampler.shouldLog(level);
    }
    
    static class Sampler {
        private final Map<Level, AtomicLong> counters = new ConcurrentHashMap<>();
        private final Map<Level, Long> lastSampleTime = new ConcurrentHashMap<>();
        
        public boolean shouldLog(Level level) {
            AtomicLong counter = counters.computeIfAbsent(level, k -> new AtomicLong(0));
            long count = counter.incrementAndGet();
            
            // Sampling rates based on level
            int sampleRate = getSampleRate(level);
            
            if (count % sampleRate == 0) {
                lastSampleTime.put(level, System.currentTimeMillis());
                return true;
            }
            
            // Always log errors and warnings
            if (level == Level.ERROR || level == Level.WARN) {
                return true;
            }
            
            // Log at least once per minute for INFO
            if (level == Level.INFO) {
                Long lastTime = lastSampleTime.get(level);
                if (lastTime == null || System.currentTimeMillis() - lastTime > 60000) {
                    lastSampleTime.put(level, System.currentTimeMillis());
                    return true;
                }
            }
            
            return false;
        }
        
        private int getSampleRate(Level level) {
            return switch (level) {
                case TRACE -> 1000;
                case DEBUG -> 100;
                case INFO -> 10;
                case WARN -> 1;
                case ERROR -> 1;
            };
        }
    }
}

// Аспект для сэмплирования
@Aspect
@Component
public class SamplingAspect {
    
    @Autowired
    private LogSampler logSampler;
    
    @Around("execution(* *(..)) && @annotation(com.example.LogSampled)")
    public Object sampleLogging(ProceedingJoinPoint joinPoint) throws Throwable {
        // Проверка необходимости логирования
        if (!logSampler.shouldLog(
            joinPoint.getTarget().getClass().getName(), 
            Level.DEBUG, 
            "method_call")) {
            return joinPoint.proceed();
        }
        
        // Логирование с сэмплированием
        logger.debug("Method called with sampling", 
                    kv("class", joinPoint.getTarget().getClass().getSimpleName()),
                    kv("method", joinPoint.getSignature().getName()));
        
        return joinPoint.proceed();
    }
}
```

## Безопасность

### Защита чувствительных данных

#### Data sanitization
```java
public class SecureLogging {
    
    private static final Set<String> SENSITIVE_KEYS = Set.of(
        "password", "token", "secret", "key", "ssn", "creditcard", "pin"
    );
    
    public static void logSecure(Logger logger, String message, Object... args) {
        // Санитизация аргументов
        Object[] sanitizedArgs = sanitizeArgs(args);
        
        logger.info(message, sanitizedArgs);
    }
    
    public static void logSecureMap(Logger logger, String message, Map<String, Object> context) {
        // Санитизация контекста
        Map<String, Object> sanitizedContext = sanitizeContext(context);
        
        logger.info(message, kv("context", sanitizedContext));
    }
    
    private static Object[] sanitizeArgs(Object[] args) {
        Object[] sanitized = new Object[args.length];
        
        for (int i = 0; i < args.length; i++) {
            sanitized[i] = sanitizeValue(args[i]);
        }
        
        return sanitized;
    }
    
    private static Map<String, Object> sanitizeContext(Map<String, Object> context) {
        Map<String, Object> sanitized = new HashMap<>();
        
        for (Map.Entry<String, Object> entry : context.entrySet()) {
            String key = entry.getKey().toLowerCase();
            Object value = entry.getValue();
            
            if (SENSITIVE_KEYS.contains(key)) {
                sanitized.put(entry.getKey(), maskValue(value));
            } else {
                sanitized.put(entry.getKey(), sanitizeValue(value));
            }
        }
        
        return sanitized;
    }
    
    private static Object sanitizeValue(Object value) {
        if (value instanceof String) {
            return sanitizeString((String) value);
        } else if (value instanceof Map) {
            return sanitizeContext((Map<String, Object>) value);
        }
        
        return value;
    }
    
    private static String sanitizeString(String value) {
        // Удаление потенциально опасных символов
        return value.replaceAll("[\\r\\n\\t]", " ");
    }
    
    private static Object maskValue(Object value) {
        if (value == null) return null;
        
        String strValue = value.toString();
        if (strValue.length() <= 4) return "***";
        
        return strValue.substring(0, 2) + "***" + strValue.substring(strValue.length() - 2);
    }
}

// Использование безопасного логирования
@Service
public class AuthenticationService {
    
    public User authenticate(String username, String password) {
        logger.info("User authentication attempt", kv("username", username));
        
        // Безопасное логирование чувствительных данных
        SecureLogging.logSecure(logger, "Authenticating user with password length: {}", 
                               password.length());
        
        // Никогда не логируем пароль
        // logger.info("Authenticating user {}", username, kv("password", password)); // ПЛОХО!
        
        try {
            User user = doAuthenticate(username, password);
            
            SecureLogging.logSecureMap(logger, "User authenticated successfully", 
                Map.of("userId", user.getId(), "username", username));
            
            return user;
            
        } catch (AuthenticationException e) {
            logger.warn("Authentication failed", 
                       kv("username", username),
                       kv("reason", e.getMessage()));
            throw e;
        }
    }
}
```

### Log injection prevention

#### Защита от log injection
```java
public class LogInjectionProtection {
    
    public static String sanitizeLogMessage(String message) {
        if (message == null) return null;
        
        // Удаление символов новой строки и возврата каретки
        message = message.replaceAll("[\\r\\n]", "");
        
        // Удаление других потенциально опасных символов
        message = message.replaceAll("[\\x00-\\x1F\\x7F-\\x9F]", "");
        
        return message;
    }
    
    public static void safeLog(Logger logger, String message, Object... args) {
        // Санитизация сообщения
        String sanitizedMessage = sanitizeLogMessage(message);
        
        // Санитизация аргументов
        Object[] sanitizedArgs = Arrays.stream(args)
            .map(LogInjectionProtection::sanitizeLogArg)
            .toArray();
        
        logger.info(sanitizedMessage, sanitizedArgs);
    }
    
    private static Object sanitizeLogArg(Object arg) {
        if (arg instanceof String) {
            return sanitizeLogMessage((String) arg);
        }
        
        return arg;
    }
}

// Аспект для автоматической защиты
@Aspect
@Component
public class LogInjectionProtectionAspect {
    
    @Around("execution(* org.slf4j.Logger.*(..))")
    public Object protectLogInjection(ProceedingJoinPoint joinPoint) throws Throwable {
        Object[] args = joinPoint.getArgs();
        
        // Защита только для методов логирования с сообщениями
        if (args.length > 0 && args[0] instanceof String) {
            args[0] = LogInjectionProtection.sanitizeLogMessage((String) args[0]);
            
            // Санитизация остальных аргументов
            for (int i = 1; i < args.length; i++) {
                if (args[i] instanceof String) {
                    args[i] = LogInjectionProtection.sanitizeLogMessage((String) args[i]);
                }
            }
        }
        
        return joinPoint.proceed(args);
    }
}
```

## Тестирование логирования

### Unit testing логов

#### Тестирование с Logback
```java
@SpringBootTest
public class LoggingTest {
    
    @Autowired
    private UserService userService;
    
    @Test
    public void testUserRegistrationLogging() {
        // Capture logs
        Logger logger = (Logger) LoggerFactory.getLogger(UserService.class);
        ListAppender<ILoggingEvent> listAppender = new ListAppender<>();
        listAppender.start();
        logger.addAppender(listAppender);
        
        try {
            // Test action
            userService.registerUser(new RegistrationRequest("test@example.com", "Test User"));
            
            // Verify logs
            List<ILoggingEvent> logs = listAppender.list;
            
            assertThat(logs).hasSizeGreaterThanOrEqualTo(2);
            
            // Check info log
            ILoggingEvent infoLog = logs.stream()
                .filter(log -> log.getLevel() == Level.INFO)
                .findFirst().orElseThrow();
            
            assertThat(infoLog.getMessage()).isEqualTo("User registered successfully");
            assertThat(infoLog.getMDCPropertyMap()).containsKey("userId");
            
        } finally {
            logger.detachAppender(listAppender);
        }
    }
    
    @Test
    public void testErrorLogging() {
        Logger logger = (Logger) LoggerFactory.getLogger(UserService.class);
        ListAppender<ILoggingEvent> listAppender = new ListAppender<>();
        listAppender.start();
        logger.addAppender(listAppender);
        
        try {
            // Trigger error
            assertThrows(Exception.class, () -> 
                userService.registerUser(new RegistrationRequest("invalid", "")));
            
            // Verify error log
            List<ILoggingEvent> logs = listAppender.list;
            
            ILoggingEvent errorLog = logs.stream()
                .filter(log -> log.getLevel() == Level.ERROR)
                .findFirst().orElseThrow();
            
            assertThat(errorLog.getThrowableProxy()).isNotNull();
            assertThat(errorLog.getMDCPropertyMap()).containsKey("email");
            
        } finally {
            logger.detachAppender(listAppender);
        }
    }
}
```

### Integration testing

#### Тестирование структурированного логирования
```java
@SpringBootTest
@Testcontainers
public class StructuredLoggingIntegrationTest {
    
    @Container
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:13");
    
    @Autowired
    private OrderService orderService;
    
    @Autowired
    private MeterRegistry meterRegistry;
    
    @Test
    public void testStructuredLoggingWithMetrics() {
        // Mock log capture
        Logger logger = (Logger) LoggerFactory.getLogger(OrderService.class);
        TestAppender testAppender = new TestAppender();
        testAppender.start();
        logger.addAppender(testAppender);
        
        try {
            // Execute business operation
            Order order = orderService.createOrder(createTestOrderRequest());
            
            // Verify structured logs
            List<ILoggingEvent> logs = testAppender.getEvents();
            
            // Check business event log
            ILoggingEvent businessEventLog = findLogByMessage(logs, "Business event processed");
            assertThat(businessEventLog).isNotNull();
            
            // Parse JSON log
            String jsonMessage = businessEventLog.getMessage();
            JsonNode logNode = new ObjectMapper().readTree(jsonMessage);
            
            assertThat(logNode.get("eventId").asText()).isEqualTo(order.getId());
            assertThat(logNode.get("eventType").asText()).isEqualTo("ORDER_CREATED");
            
            // Verify metrics
            Counter registrationCounter = meterRegistry.counter("order.creations");
            assertThat(registrationCounter.count()).isGreaterThan(0);
            
        } finally {
            logger.detachAppender(testAppender);
        }
    }
    
    static class TestAppender extends ListAppender<ILoggingEvent> {
        public List<ILoggingEvent> getEvents() {
            return list;
        }
    }
    
    private ILoggingEvent findLogByMessage(List<ILoggingEvent> logs, String message) {
        return logs.stream()
            .filter(log -> log.getMessage().contains(message))
            .findFirst()
            .orElse(null);
    }
}
```

### Log format testing

#### Тестирование MDC и контекста
```java
public class MDCLoggingTest {
    
    @Test
    public void testMDCContextPropagation() {
        Logger logger = LoggerFactory.getLogger("test");
        
        // Set MDC context
        MDC.put("requestId", "test-request-123");
        MDC.put("userId", "user-456");
        
        try {
            // Log message
            logger.info("Test message", kv("action", "test"));
            
            // Verify MDC was included in log
            // (This would typically be verified by parsing the actual log output)
            
        } finally {
            MDC.clear();
        }
    }
    
    @Test
    public void testCorrelationIdGeneration() {
        String correlationId = CorrelationIdGenerator.generate();
        
        assertThat(correlationId).isNotNull();
        assertThat(correlationId.length()).isGreaterThan(0);
        
        // Verify it's a valid UUID
        assertDoesNotThrow(() -> UUID.fromString(correlationId));
    }
    
    @Test
    public void testRequestContext() {
        // Initialize context
        RequestContext.init("req-123", "user-456", "session-789");
        
        try {
            // Verify context is set
            RequestContextData context = RequestContext.get();
            assertThat(context.getRequestId()).isEqualTo("req-123");
            assertThat(context.getUserId()).isEqualTo("user-456");
            assertThat(context.getSessionId()).isEqualTo("session-789");
            
            // Verify MDC is set
            assertThat(MDC.get("requestId")).isEqualTo("req-123");
            
        } finally {
            RequestContext.clear();
        }
    }
}
```

## Мониторинг и алертинг

### Log metrics

#### Метрики логирования
```java
@Configuration
public class LoggingMetricsConfig {
    
    @Bean
    public LogMetricsCollector logMetricsCollector(MeterRegistry meterRegistry) {
        return new LogMetricsCollector(meterRegistry);
    }
}

@Component
public class LogMetricsCollector {
    
    private final MeterRegistry meterRegistry;
    private final Map<String, Counter> errorCounters = new ConcurrentHashMap<>();
    private final Map<String, Counter> warnCounters = new ConcurrentHashMap<>();
    
    @Autowired
    public LogMetricsCollector(MeterRegistry meterRegistry) {
        this.meterRegistry = meterRegistry;
        
        // Register gauges for log levels
        Gauge.builder("logging.level", () -> getCurrentLogLevel("ROOT"))
            .register(meterRegistry);
    }
    
    public void recordLogEvent(String loggerName, Level level, String message) {
        // Count errors by logger
        if (level == Level.ERROR) {
            Counter errorCounter = errorCounters.computeIfAbsent(loggerName, 
                k -> Counter.builder("log.errors")
                    .tag("logger", k)
                    .register(meterRegistry));
            errorCounter.increment();
        }
        
        // Count warnings
        if (level == Level.WARN) {
            Counter warnCounter = warnCounters.computeIfAbsent(loggerName, 
                k -> Counter.builder("log.warnings")
                    .tag("logger", k)
                    .register(meterRegistry));
            warnCounter.increment();
        }
        
        // Record log rate
        meterRegistry.counter("log.events", "level", level.toString()).increment();
    }
    
    private int getCurrentLogLevel(String loggerName) {
        Logger logger = LoggerFactory.getLogger(loggerName);
        if (logger instanceof ch.qos.logback.classic.Logger) {
            ch.qos.logback.classic.Logger logbackLogger = (ch.qos.logback.classic.Logger) logger;
            return logbackLogger.getLevel() != null ? logbackLogger.getLevel().toInt() : 0;
        }
        return 0;
    }
}

// Аспект для сбора метрик
@Aspect
@Component
public class LoggingMetricsAspect {
    
    @Autowired
    private LogMetricsCollector metricsCollector;
    
    @After("execution(* org.slf4j.Logger.*(..))")
    public void collectLogMetrics(JoinPoint joinPoint) {
        // Extract logger name and level from join point
        // This is simplified - in practice you'd need to inspect the actual log call
        String loggerName = joinPoint.getTarget().toString();
        Level level = extractLevel(joinPoint);
        String message = extractMessage(joinPoint);
        
        metricsCollector.recordLogEvent(loggerName, level, message);
    }
    
    private Level extractLevel(JoinPoint joinPoint) {
        // Extract level from method name
        String methodName = joinPoint.getSignature().getName();
        return switch (methodName) {
            case "error" -> Level.ERROR;
            case "warn" -> Level.WARN;
            case "info" -> Level.INFO;
            case "debug" -> Level.DEBUG;
            case "trace" -> Level.TRACE;
            default -> Level.INFO;
        };
    }
    
    private String extractMessage(JoinPoint joinPoint) {
        Object[] args = joinPoint.getArgs();
        return args.length > 0 && args[0] instanceof String ? (String) args[0] : "";
    }
}
```

### Alerting на основе логов

#### Log-based alerting
```java
@Service
public class LogBasedAlertingService {
    
    @Autowired
    private AlertService alertService;
    
    @Autowired
    private MeterRegistry meterRegistry;
    
    private final Map<String, AlertState> activeAlerts = new ConcurrentHashMap<>();
    
    public void processLogEvent(String loggerName, Level level, String message, 
                               Map<String, String> mdc) {
        
        // Error rate alerting
        if (level == Level.ERROR) {
            checkErrorRateAlert(loggerName);
        }
        
        // Specific error patterns
        if (message.contains("Database connection failed")) {
            alertService.sendAlert(
                AlertSeverity.CRITICAL,
                "Database connectivity issue",
                String.format("Database connection failure detected in %s", loggerName)
            );
        }
        
        // Security alerts
        if (message.contains("Authentication failed") && 
            mdc.getOrDefault("attempts", "0").equals("5")) {
            alertService.sendAlert(
                AlertSeverity.HIGH,
                "Potential brute force attack",
                String.format("Multiple authentication failures for user %s", mdc.get("userId"))
            );
        }
        
        // Performance alerts
        if (message.contains("Request timeout") && level == Level.WARN) {
            checkPerformanceAlert(loggerName, mdc);
        }
    }
    
    private void checkErrorRateAlert(String loggerName) {
        String alertKey = "error_rate_" + loggerName;
        AlertState state = activeAlerts.computeIfAbsent(alertKey, k -> new AlertState());
        
        state.incrementCount();
        
        // Alert if error rate exceeds threshold
        if (state.getRatePerMinute() > 10 && !state.isAlertSent()) {
            alertService.sendAlert(
                AlertSeverity.HIGH,
                "High error rate detected",
                String.format("Error rate for %s is %.2f errors/minute", 
                            loggerName, state.getRatePerMinute())
            );
            state.setAlertSent(true);
        }
    }
    
    private void checkPerformanceAlert(String loggerName, Map<String, String> mdc) {
        String duration = mdc.get("duration");
        if (duration != null) {
            long durationMs = Long.parseLong(duration);
            
            if (durationMs > 30000) { // 30 seconds
                alertService.sendAlert(
                    AlertSeverity.MEDIUM,
                    "Slow request detected",
                    String.format("Request took %d ms in %s", durationMs, loggerName)
                );
            }
        }
    }
    
    static class AlertState {
        private final AtomicLong count = new AtomicLong(0);
        private final long startTime = System.currentTimeMillis();
        private volatile boolean alertSent = false;
        
        public void incrementCount() {
            count.incrementAndGet();
        }
        
        public double getRatePerMinute() {
            long elapsedMs = System.currentTimeMillis() - startTime;
            double elapsedMinutes = elapsedMs / (1000.0 * 60.0);
            return elapsedMinutes > 0 ? count.get() / elapsedMinutes : 0;
        }
        
        public boolean isAlertSent() { return alertSent; }
        public void setAlertSent(boolean alertSent) { this.alertSent = alertSent; }
    }
}
```

## Best practices по языкам

### Java best practices

#### Логирование в Java
```java
// Правильная иерархия логеров
package com.example.service;

public class UserService {
    // Логер для класса
    private static final Logger logger = LoggerFactory.getLogger(UserService.class);
    
    // НЕ используйте общий логер
    // private static final Logger logger = LoggerFactory.getLogger("com.example");
}

// Использование try-with-resources для MDC
public class RequestProcessor {
    
    public void process(Request request) {
        // Автоматическая очистка MDC
        try (MDC.MDCCloseable requestId = MDC.putCloseable("requestId", request.getId());
             MDC.MDCCloseable userId = MDC.putCloseable("userId", request.getUserId())) {
            
            logger.info("Processing request");
            // Обработка...
        }
    }
}

// Правильная обработка исключений
public class DataService {
    
    public User findUser(String id) {
        try {
            return userRepository.findById(id);
            
        } catch (DataAccessException e) {
            // Логируем на уровне, соответствующем серьезности
            logger.error("Database error while finding user", 
                        kv("userId", id), e);
            throw new ServiceException("Unable to find user", e);
            
        } catch (Exception e) {
            // Неожиданная ошибка - ERROR уровень
            logger.error("Unexpected error in findUser", 
                        kv("userId", id), e);
            throw e;
        }
    }
}
```

### Kotlin best practices

#### Логирование в Kotlin
```kotlin
class UserService(private val userRepository: UserRepository) {
    
    companion object {
        private val logger = LoggerFactory.getLogger(UserService::class.java)
    }
    
    fun createUser(request: CreateUserRequest): User {
        logger.info("Creating user", kv("email", request.email))
        
        return try {
            val user = userRepository.save(request.toUser())
            logger.info("User created successfully", kv("userId", user.id))
            user
            
        } catch (e: DataIntegrityViolationException) {
            logger.error("User creation failed - duplicate email", 
                        kv("email", request.email), e)
            throw UserAlreadyExistsException("Email already exists")
            
        } catch (e: Exception) {
            logger.error("Unexpected error creating user", 
                        kv("email", request.email), e)
            throw e
        }
    }
    
    // Inline функции для логирования
    inline fun <T> logExecution(operation: String, block: () -> T): T {
        logger.debug("Starting operation", kv("operation", operation))
        
        return try {
            val result = block()
            logger.debug("Operation completed", kv("operation", operation))
            result
            
        } catch (e: Exception) {
            logger.error("Operation failed", kv("operation", operation), e)
            throw e
        }
    }
}

// Использование корутин с логированием
class AsyncService(private val coroutineScope: CoroutineScope) {
    
    companion object {
        private val logger = LoggerFactory.getLogger(AsyncService::class.java)
    }
    
    suspend fun processAsync(request: Request) = coroutineScope.launch {
        val correlationId = MDC.get("correlationId")
        
        logger.info("Starting async processing", 
                   kv("correlationId", correlationId),
                   kv("requestId", request.id))
        
        try {
            // Async processing
            delay(1000)
            processRequest(request)
            
            logger.info("Async processing completed", 
                       kv("correlationId", correlationId))
            
        } catch (e: Exception) {
            logger.error("Async processing failed", 
                        kv("correlationId", correlationId), e)
            throw e
        }
    }
}
```

### Scala best practices

#### Логирование в Scala
```scala
class OrderService @Autowired()(orderRepository: OrderRepository) {
  
  private val logger = LoggerFactory.getLogger(getClass)
  
  def createOrder(request: CreateOrderRequest): Future[Order] = {
    logger.info("Creating order", kv("userId", request.userId), kv("amount", request.amount))
    
    val order = Order(
      id = UUID.randomUUID().toString,
      userId = request.userId,
      items = request.items,
      total = request.amount
    )
    
    orderRepository.save(order).map { savedOrder =>
      logger.info("Order created successfully", 
                 kv("orderId", savedOrder.id),
                 kv("userId", savedOrder.userId))
      savedOrder
    }.recover {
      case e: ValidationException =>
        logger.warn("Order validation failed", 
                   kv("userId", request.userId),
                   kv("reason", e.getMessage))
        throw e
        
      case e: Exception =>
        logger.error("Failed to create order", 
                    kv("userId", request.userId), e)
        throw new ServiceException("Order creation failed", e)
    }
  }
}

// Имплицитные классы для логирования
object LoggingImplicits {
  
  implicit class LoggerOps(logger: Logger) {
    
    def infoWithContext(message: String, context: (String, Any)*): Unit = {
      val kvs = context.map { case (k, v) => kv(k, v.toString) }
      logger.info(message, kvs: _*)
    }
    
    def errorWithContext(message: String, throwable: Throwable, 
                        context: (String, Any)*): Unit = {
      val kvs = context.map { case (k, v) => kv(k, v.toString) }
      logger.error(message, throwable, kvs: _*)
    }
  }
}

// Использование имплицитов
class PaymentService @Autowired()(paymentProcessor: PaymentProcessor) {
  
  import LoggingImplicits._
  
  private val logger = LoggerFactory.getLogger(getClass)
  
  def processPayment(payment: Payment): Future[PaymentResult] = {
    logger.infoWithContext("Processing payment",
      "amount" -> payment.amount,
      "currency" -> payment.currency,
      "userId" -> payment.userId
    )
    
    paymentProcessor.process(payment).map { result =>
      logger.infoWithContext("Payment processed successfully",
        "transactionId" -> result.transactionId,
        "status" -> result.status
      )
      result
    }.recover {
      case e: PaymentException =>
        logger.errorWithContext("Payment processing failed",
          e,
          "amount" -> payment.amount,
          "reason" -> e.getMessage
        )
        throw e
    }
  }
}
```

### Go best practices

#### Логирование в Go
```go
package service

import (
    "context"
    "github.com/sirupsen/logrus"
)

type UserService struct {
    logger *logrus.Logger
    repo   UserRepository
}

func NewUserService(logger *logrus.Logger, repo UserRepository) *UserService {
    return &UserService{
        logger: logger,
        repo:   repo,
    }
}

func (s *UserService) CreateUser(ctx context.Context, req CreateUserRequest) (*User, error) {
    logger := s.logger.WithFields(logrus.Fields{
        "operation": "create_user",
        "email":     req.Email,
        "userId":    req.UserID,
    })
    
    logger.Info("Creating user")
    
    user := &User{
        ID:    generateID(),
        Email: req.Email,
        Name:  req.Name,
    }
    
    if err := s.repo.Save(ctx, user); err != nil {
        logger.WithError(err).Error("Failed to create user")
        return nil, fmt.Errorf("failed to create user: %w", err)
    }
    
    logger.WithField("userId", user.ID).Info("User created successfully")
    return user, nil
}

// Структурированное логирование с контекстом
func (s *UserService) ProcessBatch(ctx context.Context, users []CreateUserRequest) error {
    logger := s.logger.WithField("batchSize", len(users))
    logger.Info("Starting batch processing")
    
    successCount := 0
    errorCount := 0
    
    for i, req := range users {
        entryLogger := logger.WithFields(logrus.Fields{
            "batchIndex": i,
            "email":      req.Email,
        })
        
        if _, err := s.CreateUser(ctx, req); err != nil {
            entryLogger.WithError(err).Warn("Failed to process user in batch")
            errorCount++
        } else {
            successCount++
        }
    }
    
    logger.WithFields(logrus.Fields{
        "successCount": successCount,
        "errorCount":   errorCount,
    }).Info("Batch processing completed")
    
    return nil
}

// Middleware для логирования HTTP запросов
func LoggingMiddleware(logger *logrus.Logger) gin.HandlerFunc {
    return func(c *gin.Context) {
        start := time.Now()
        requestID := c.GetHeader("X-Request-ID")
        if requestID == "" {
            requestID = generateRequestID()
            c.Header("X-Request-ID", requestID)
        }
        
        entry := logger.WithFields(logrus.Fields{
            "requestId": requestID,
            "method":    c.Request.Method,
            "path":      c.Request.URL.Path,
            "clientIP":  c.ClientIP(),
        })
        
        entry.Info("Request started")
        
        c.Next()
        
        latency := time.Since(start)
        statusCode := c.Writer.Status()
        
        entry.WithFields(logrus.Fields{
            "statusCode": statusCode,
            "latency":    latency,
        }).Info("Request completed")
    }
}
```

## Антипаттерны

### Распространенные ошибки

#### 1. Неинформативные сообщения
```java
// ПЛОХО
logger.error("Error!");
logger.info("Done");

// ХОРОШО
logger.error("Failed to connect to database", 
            kv("host", dbHost), kv("port", dbPort), exception);
logger.info("User registration completed", 
           kv("userId", user.getId()), kv("email", user.getEmail()));
```

#### 2. Логирование чувствительных данных
```java
// ПЛОХО
logger.info("User login", kv("password", password));
logger.debug("Payment data", kv("creditCard", cardNumber));

// ХОРОШО
logger.info("User authentication", kv("userId", userId));
logger.debug("Payment processing", kv("amount", amount), kv("maskedCard", maskCard(cardNumber)));
```

#### 3. Performance проблемы
```java
// ПЛОХО
for (User user : users) {
    logger.debug("Processing user: " + user.toString()); // String concatenation in loop
}

// ХОРОШО
if (logger.isDebugEnabled()) {
    logger.debug("Processing {} users", users.size());
    for (User user : users) {
        logger.debug("Processing user", kv("userId", user.getId()));
    }
}
```

#### 4. Неправильные уровни логирования
```java
// ПЛОХО
logger.error("User not found"); // ERROR для ожидаемой ситуации
logger.info("NullPointerException occurred"); // INFO для ошибки

// ХОРОШО
logger.warn("User not found", kv("userId", userId)); // WARN для ожидаемой проблемы
logger.error("Unexpected null pointer", exception); // ERROR для неожиданной ошибки
```

#### 5. Отсутствие контекста
```java
// ПЛОХО
logger.info("Payment processed");

// ХОРОШО
logger.info("Payment processed successfully", 
           kv("userId", userId),
           kv("amount", amount),
           kv("transactionId", transactionId));
```

#### 6. Лог injection
```java
// ПЛОХО
String message = "User " + username + " logged in";
logger.info(message); // Username может содержать \n или другие символы

// ХОРОШО
logger.info("User logged in", kv("username", username));
```

#### 7. Синхронное логирование в высоконагруженных системах
```java
// ПЛОХО
logger.info("Request processed", kv("data", largeDataObject)); // Блокирующее логирование

// ХОРОШО
if (logger.isInfoEnabled()) {
    // Async logging или sampling
    asyncLogger.info("Request processed", kv("requestId", requestId));
}
```

#### 8. Отсутствие ротации логов
```java
// ПЛОХО - логи растут бесконечно
<appender name="FILE" class="ch.qos.logback.core.FileAppender">
    <file>app.log</file>
</appender>

// ХОРОШО - ротация по размеру и времени
<appender name="FILE" class="ch.qos.logback.core.rolling.RollingFileAppender">
    <file>app.log</file>
    <rollingPolicy class="ch.qos.logback.core.rolling.TimeBasedRollingPolicy">
        <fileNamePattern>app.%d{yyyy-MM-dd}.%i.log</fileNamePattern>
        <maxFileSize>100MB</maxFileSize>
        <maxHistory>30</maxHistory>
    </rollingPolicy>
</appender>
```

## Заключение

**Лучшие практики логирования** — это фундамент для создания надежных, наблюдаемых и поддерживаемых приложений. Правильная реализация логирования позволяет быстро диагностировать проблемы, анализировать производительность и обеспечивать безопасность системы.

### Ключевые принципы:

1. **Логи для людей и машин** — структурированные, понятные сообщения
2. **Правильные уровни логирования** — ERROR, WARN, INFO, DEBUG, TRACE по назначению
3. **Безопасность** — защита чувствительных данных, предотвращение injection
4. **Производительность** — асинхронное логирование, guard clauses, сэмплирование
5. **Контекст и корреляция** — MDC, request tracing, distributed tracing
6. **Структурированное логирование** — JSON формат, key-value pairs
7. **Мониторинг** — метрики, алертинг на основе логов
8. **Тестирование** — unit и integration тесты логирования

### Когда использовать разные подходы:

#### Структурированное логирование:
- ✅ Микросервисы и распределенные системы
- ✅ Production среды с centralized logging
- ✅ Системы с высокими требованиями к observability
- ✅ Когда логи анализируются программами

#### Традиционное логирование:
- ✅ Простые приложения
- ✅ Development и debugging
- ✅ Когда логи читаются только людьми
- ✅ Legacy системы

### Metrics vs Logs:

- **Logs** — для диагностики и отладки конкретных ситуаций
- **Metrics** — для мониторинга трендов и алертинга
- **Трассировка** — для анализа flow через компоненты

### Лучшие практики по умолчанию:

```java
// Рекомендуемая конфигурация
public class LoggingBestPractices {
    
    // 1. Логер для каждого класса
    private static final Logger logger = LoggerFactory.getLogger(MyClass.class);
    
    // 2. Структурированное логирование
    public void processData(String id, Map<String, Object> data) {
        logger.info("Processing data", 
                   kv("id", id),
                   kv("dataSize", data.size()));
        
        try {
            // Business logic
            doProcess(data);
            
            logger.info("Data processed successfully", kv("id", id));
            
        } catch (Exception e) {
            logger.error("Data processing failed", 
                        kv("id", id), e);
            throw e;
        }
    }
    
    // 3. Guard clauses для производительности
    public void debugLargeData(List<Object> largeData) {
        if (logger.isDebugEnabled()) {
            logger.debug("Processing large dataset", 
                        kv("size", largeData.size()));
        }
    }
    
    // 4. Безопасное логирование
    public void logUserAction(String userId, String action) {
        logger.info("User action performed", 
                   kv("userId", userId),
                   kv("action", action));
        // Никогда не логируем пароли, токены и т.д.
    }
}
```

### Инструменты и фреймворки:

- **Logback** — высокопроизводительный, flexible logging framework
- **Log4j2** — advanced features, async logging
- **SLF4J** — facade для abstraction
- **Structured logging** — JSON, key-value pairs
- **Centralized logging** — ELK stack, Loki, CloudWatch
- **Distributed tracing** — OpenTelemetry, Jaeger, Zipkin

### Культура логирования:

1. **Обучение команды** — все разработчики должны понимать best practices
2. **Code review** — проверка логирования в PR
3. **Documentation** — документирование подходов к логированию
4. **Monitoring** — отслеживание качества логов
5. **Evolution** — регулярное улучшение практик логирования

Следование этим практикам обеспечивает создание надежных, наблюдаемых и поддерживаемых систем. 🚀
