# Spring Cloud Sleuth: Distributed Tracing для Spring

**Комплексное руководство по использованию Spring Cloud Sleuth для distributed tracing в Spring Boot приложениях с интеграцией OpenTelemetry.**

**Дата последнего обновления:** 2026-01-23

## Полезные ссылки

### Официальная документация
- [Spring Cloud Sleuth](https://docs.spring.io/spring-cloud-sleuth/docs/current/reference/html/) - Официальная документация
- [Spring Cloud Sleuth GitHub](https://github.com/spring-cloud/spring-cloud-sleuth) - Репозиторий проекта
- [OpenTelemetry Bridge](https://docs.spring.io/spring-cloud-sleuth/docs/current/reference/html/bridges.html) - Интеграция с OpenTelemetry

### Интеграция
- [Spring Boot Sleuth Starter](https://docs.spring.io/spring-boot/docs/current/reference/html/features.html#features.observability.tracing) - Spring Boot starter
- [Micrometer Tracing](https://docs.spring.io/spring-cloud-sleuth/docs/current/reference/html/bridges.html#micrometer-tracing) - Micrometer интеграция
- [Distributed Tracing](https://spring.io/blog/2022/10/12/observability-with-spring-boot-3) - Spring Boot 3 Observability

## Содержание

- [Введение в Spring Cloud Sleuth](#введение-в-spring-cloud-sleuth)
  - [Почему Spring Cloud Sleuth?](#почему-spring-cloud-sleuth)
  - [Как работает Sleuth?](#как-работает-sleuth)
  - [Ключевые концепции](#ключевые-концепции)
- [Установка и настройка](#установка-и-настройка)
  - [Maven](#maven)
  - [Gradle](#gradle)
  - [Базовая конфигурация](#базовая-конфигурация)
  - [Конфигурация sampling](#конфигурация-sampling)
- [Основы Tracing](#основы-tracing)
  - [Автоматическое tracing](#автоматическое-tracing)
  - [Ручное управление span](#ручное-управление-span)
  - [Логи с trace ID](#логи-с-trace-id)
- [Интеграция с Spring компонентами](#интеграция-с-spring-компонентами)
  - [Web MVC интеграция](#web-mvc-интеграция)
  - [Data JPA интеграция](#data-jpa-интеграция)
  - [Spring Cloud Stream интеграция](#spring-cloud-stream-интеграция)
- [Анотационная конфигурация](#анотационная-конфигурация)
  - [@NewSpan](#newspan)
  - [Кастомные аннотации](#кастомные-аннотации)
- [Распространение контекста](#распространение-контекста)
  - [Baggage propagation](#baggage-propagation)
  - [HTTP headers propagation](#http-headers-propagation)
- [Интеграция с внешними системами](#интеграция-с-внешними-системами)
  - [Zipkin](#zipkin)
  - [Jaeger](#jaeger)
  - [OpenTelemetry Collector](#opentelemetry-collector)
  - [Кастомный экспортер](#кастомный-экспортер)
- [Мониторинг и отладка](#мониторинг-и-отладка)
  - [Tracing endpoint](#tracing-endpoint)
  - [Health indicators](#health-indicators)
  - [Отладка и логирование](#отладка-и-логирование)
- [Best practices](#best-practices)
  - [1. Правильная конфигурация sampling](#1-правильная-конфигурация-sampling)
  - [2. Правильное именование span](#2-правильное-именование-span)
  - [3. Обработка ошибок и исключений](#3-обработка-ошибок-и-исключений)

## Введение в Spring Cloud Sleuth

**Spring Cloud Sleuth** — это библиотека для distributed tracing в Spring Boot приложениях. Она автоматически добавляет trace и span ID к логи, HTTP запросам и сообщениям, обеспечивая traceability в микросервисных архитектурах.

### Почему Spring Cloud Sleuth?

Spring Cloud Sleuth предлагает множество преимуществ:

1. **Автоматическая инструментация** — Работает out-of-the-box с Spring компонентами
2. **Spring Boot интеграция** — Простая настройка через properties
3. **Distributed tracing** — Отслеживание запросов через микросервисы
4. **Correlation ID** — Автоматическая генерация и передача ID
5. **Логи correlation** — Добавление trace ID в логи
6. **OpenTelemetry совместимость** — Интеграция с OpenTelemetry
7. **Reactive support** — Поддержка WebFlux и Reactor
8. **Baggage propagation** — Передача кастомных данных

### Как работает Sleuth?

Sleuth автоматически:
- Генерирует trace ID для входящих запросов
- Создает span для каждого компонента
- Добавляет trace/span ID в HTTP заголовки
- Включает trace ID в логи через MDC
- Передает контекст между потоками и компонентами

### Ключевые концепции

- **Trace** — Полный путь запроса через систему
- **Span** — Единица работы в trace
- **Trace ID** — Уникальный идентификатор trace
- **Span ID** — Уникальный идентификатор span
- **Parent Span ID** — ID родительского span
- **Baggage** — Кастомные данные, передаваемые с trace

## Установка и настройка

### Maven

```xml
<dependency>
    <groupId>org.springframework.cloud</groupId>
    <artifactId>spring-cloud-starter-sleuth</artifactId>
    <version>3.1.9</version>
</dependency>

<!-- Для OpenTelemetry интеграции -->
<dependency>
    <groupId>org.springframework.cloud</groupId>
    <artifactId>spring-cloud-sleuth-otel</artifactId>
    <version>3.1.9</version>
</dependency>

<!-- Для Micrometer Tracing -->
<dependency>
    <groupId>io.micrometer</groupId>
    <artifactId>micrometer-tracing</artifactId>
    <version>1.1.5</version>
</dependency>
```

### Gradle

```kotlin
dependencies {
    implementation("org.springframework.cloud:spring-cloud-starter-sleuth:3.1.9")
    implementation("org.springframework.cloud:spring-cloud-sleuth-otel:3.1.9")
    implementation("io.micrometer:micrometer-tracing:1.1.5")
}
```

### Базовая конфигурация

```yaml
# application.yml
spring:
  application:
    name: my-service

  sleuth:
    enabled: true
    sampler:
      probability: 1.0  # 100% sampling для development

logging:
  level:
    org.springframework.cloud.sleuth: DEBUG

management:
  tracing:
    sampling:
      probability: 1.0
```

### Конфигурация sampling

```java
@Configuration
public class TracingConfiguration {

    @Bean
    public Sampler defaultSampler() {
        return Sampler.ALWAYS_SAMPLE; // Всегда сэмплировать для development
    }

    @Bean
    public Sampler productionSampler() {
        return Sampler.create(0.1); // 10% сэмплирование для production
    }

    // Кастомный sampler
    @Bean
    public Sampler customSampler() {
        return new Sampler() {
            @Override
            public boolean isSampled(SpanContext parentContext, TraceId traceId,
                                   String name, SpanKind spanKind, Attributes attributes,
                                   List<Link> parentLinks, SamplerResult samplerResult) {

                // Сэмплировать все HTTP запросы и ошибки
                if (attributes.get(AttributeKey.stringKey("http.method")) != null) {
                    return true;
                }

                if (attributes.get(AttributeKey.booleanKey("error")) != null) {
                    return true;
                }

                // 50% для остальных операций
                return ThreadLocalRandom.current().nextDouble() < 0.5;
            }
        };
    }
}
```

## Основы Tracing

### Автоматическое tracing

```java
/**
 * REST контроллер с автоматическим tracing через Spring Cloud Sleuth
 * Sleuth автоматически создает span для HTTP запросов и добавляет trace ID в логи
 */
@RestController  // Spring аннотация для REST контроллера
@RequestMapping("/api/users")  // Базовый путь для всех методов контроллера
@Slf4j  // Lombok аннотация для автоматического создания logger
public class UserController {

    // UserService для бизнес-логики - автоматически внедряется Spring
    private final UserService userService;

    /**
     * Конструктор с внедрением UserService через dependency injection
     * @param userService сервис для работы с пользователями
     */
    public UserController(UserService userService) {
        this.userService = userService;
    }

    /**
     * GET endpoint для получения пользователя по ID
     * Sleuth автоматически создает span и добавляет trace ID в логи
     * @param id идентификатор пользователя
     * @return ResponseEntity с данными пользователя
     */
    @GetMapping("/{id}")  // HTTP GET /api/users/{id}
    public ResponseEntity<UserDto> getUser(@PathVariable Long id) {
        // Логирование - Sleuth автоматически добавит trace ID и span ID в логи
        log.info("Getting user with id: {}", id);
        // В логах будет: [traceId=..., spanId=...] Getting user with id: 1

        // Sleuth автоматически выполняет следующие действия:
        // 1. Создает span для этого HTTP запроса с именем "GET /api/users/{id}"
        // 2. Добавляет trace ID и span ID в MDC (Mapped Diagnostic Context) для логирования
        // 3. Передает trace context в userService через ThreadLocal
        // 4. Все логи в этом запросе будут содержать одинаковый trace ID

        // Вызов сервиса - Sleuth автоматически создаст дочерний span для этого вызова
        UserDto user = userService.getUserById(id);
        // userService.getUserById() будет частью того же trace но с отдельным span

        // Логирование результата - также содержит trace ID и span ID
        log.info("User found: {}", user.getName());
        return ResponseEntity.ok(user);  // Возвращаем успешный ответ со статусом 200
    }

    @PostMapping
    public ResponseEntity<UserDto> createUser(@RequestBody CreateUserRequest request) {
        log.info("Creating user: {}", request.getEmail());

        UserDto user = userService.createUser(request);

        log.info("User created with id: {}", user.getId());
        return ResponseEntity.status(HttpStatus.CREATED).body(user);
    }
}

@Service
@Slf4j
public class UserService {

    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Transactional
    public UserDto getUserById(Long id) {
        log.info("Fetching user from database");

        // Sleuth автоматически создаст span для транзакции
        User user = userRepository.findById(id)
            .orElseThrow(() -> new UserNotFoundException(id));

        log.info("User fetched successfully");
        return convertToDto(user);
    }

    @Transactional
    public UserDto createUser(CreateUserRequest request) {
        log.info("Saving user to database");

        User user = new User();
        user.setName(request.getName());
        user.setEmail(request.getEmail());
        user.setCreatedAt(LocalDateTime.now());

        User saved = userRepository.save(user);

        log.info("User saved successfully");
        return convertToDto(saved);
    }

    private UserDto convertToDto(User user) {
        UserDto dto = new UserDto();
        dto.setId(user.getId());
        dto.setName(user.getName());
        dto.setEmail(user.getEmail());
        dto.setCreatedAt(user.getCreatedAt());
        return dto;
    }
}
```

### Ручное управление span

```java
@Service
@Slf4j
public class ManualTracingService {

    private final Tracer tracer;

    public ManualTracingService(Tracer tracer) {
        this.tracer = tracer;
    }

    public void complexOperation(String input) {
        Span span = tracer.nextSpan().name("complex.operation").start();

        try (Tracer.SpanInScope scope = tracer.withSpanInScope(span)) {
            span.tag("operation.input", input);
            span.tag("operation.type", "processing");

            log.info("Starting complex operation with input: {}", input);

            // Шаг 1: Валидация
            Span validationSpan = tracer.nextSpan().name("validation").start();
            try (Tracer.SpanInScope validationScope = tracer.withSpanInScope(validationSpan)) {
                validateInput(input);
                validationSpan.tag("validation.result", "success");
                log.info("Input validation completed");
            } finally {
                validationSpan.finish();
            }

            // Шаг 2: Обработка
            Span processingSpan = tracer.nextSpan().name("processing").start();
            try (Tracer.SpanInScope processingScope = tracer.withSpanInScope(processingSpan)) {
                String result = processInput(input);
                processingSpan.tag("processing.output", result);
                processingSpan.tag("processing.length", result.length());
                log.info("Input processing completed");
            } finally {
                processingSpan.finish();
            }

            // Шаг 3: Сохранение
            Span persistenceSpan = tracer.nextSpan().name("persistence").start();
            try (Tracer.SpanInScope persistenceScope = tracer.withSpanInScope(persistenceSpan)) {
                saveResult(input);
                persistenceSpan.tag("persistence.action", "save");
                log.info("Result saved successfully");
            } finally {
                persistenceSpan.finish();
            }

            span.tag("operation.result", "success");
            log.info("Complex operation completed successfully");

        } catch (Exception e) {
            span.error(e);
            span.tag("operation.result", "error");
            span.tag("error.message", e.getMessage());
            log.error("Complex operation failed", e);
            throw e;
        } finally {
            span.finish();
        }
    }

    private void validateInput(String input) {
        if (input == null || input.trim().isEmpty()) {
            throw new IllegalArgumentException("Input cannot be null or empty");
        }
        // Дополнительная валидация...
    }

    private String processInput(String input) {
        // Имитация обработки
        return input.toUpperCase();
    }

    private void saveResult(String input) {
        // Имитация сохранения
    }
}
```

### Логи с trace ID

```java
@RestController
@RequestMapping("/api/debug")
@Slf4j
public class DebugController {

    @GetMapping("/trace-info")
    public Map<String, String> getTraceInfo() {
        // Sleuth автоматически добавляет trace ID в MDC
        // В логах появится [trace-id, span-id]

        log.info("Processing trace info request");
        log.debug("This is a debug message with trace context");

        // Получение текущего span
        Span currentSpan = tracer.currentSpan();
        if (currentSpan != null) {
            log.info("Current span ID: {}", currentSpan.context().spanId());
            log.info("Current trace ID: {}", currentSpan.context().traceId());
        }

        Map<String, String> traceInfo = new HashMap<>();

        // Через MDC
        traceInfo.put("traceId", MDC.get("traceId"));
        traceInfo.put("spanId", MDC.get("spanId"));
        traceInfo.put("parentSpanId", MDC.get("parentSpanId"));

        // Через Baggage
        traceInfo.put("userId", Baggage.current().getEntryValue("userId"));
        traceInfo.put("requestId", Baggage.current().getEntryValue("requestId"));

        log.info("Returning trace info: {}", traceInfo);

        return traceInfo;
    }

    @PostMapping("/async-operation")
    public CompletableFuture<String> asyncOperation() {
        log.info("Starting async operation");

        return CompletableFuture.supplyAsync(() -> {
            // Trace context автоматически передается в новый поток
            log.info("Executing in async thread");
            simulateWork();
            log.info("Async operation completed");
            return "Async result";
        });
    }

    private void simulateWork() {
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}
```

## Интеграция с Spring компонентами

### Web MVC интеграция

```java
@Configuration
public class WebTracingConfiguration {

    @Bean
    public WebMvcConfigurer tracingWebMvcConfigurer() {
        return new WebMvcConfigurer() {
            @Override
            public void addInterceptors(InterceptorRegistry registry) {
                registry.addInterceptor(new TracingInterceptor());
            }
        };
    }
}

@Component
@Slf4j
public class TracingInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        // Дополнительная логика перед обработкой запроса
        log.info("Incoming request: {} {}", request.getMethod(), request.getRequestURI());

        // Добавление кастомных тегов в span
        Span currentSpan = tracer.currentSpan();
        if (currentSpan != null) {
            currentSpan.tag("http.method", request.getMethod());
            currentSpan.tag("http.uri", request.getRequestURI());
            currentSpan.tag("user.agent", request.getHeader("User-Agent"));
        }

        return true;
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) {
        // Логика после обработки запроса
        log.info("Request processed with status: {}", response.getStatus());
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) {
        // Финальная логика
        if (ex != null) {
            log.error("Request failed", ex);
        } else {
            log.info("Request completed successfully");
        }
    }
}
```

### Data JPA интеграция

```java
@Repository
@Slf4j
public class TracingUserRepository {

    @PersistenceContext
    private EntityManager entityManager;

    private final Tracer tracer;

    public TracingUserRepository(Tracer tracer) {
        this.tracer = tracer;
    }

    public Optional<User> findByIdWithTracing(Long id) {
        Span span = tracer.nextSpan().name("repository.findById").start();

        try (Tracer.SpanInScope scope = tracer.withSpanInScope(span)) {
            span.tag("repository.operation", "findById");
            span.tag("repository.entity", "User");
            span.tag("repository.id", id.toString());

            log.info("Finding user by id: {}", id);

            User user = entityManager.find(User.class, id);

            span.tag("repository.result", user != null ? "found" : "not_found");

            log.info("User lookup completed");

            return Optional.ofNullable(user);

        } catch (Exception e) {
            span.error(e);
            log.error("Error finding user", e);
            throw e;
        } finally {
            span.finish();
        }
    }

    @Transactional
    public User saveWithTracing(User user) {
        Span span = tracer.nextSpan().name("repository.save").start();

        try (Tracer.SpanInScope scope = tracer.withSpanInScope(span)) {
            span.tag("repository.operation", "save");
            span.tag("repository.entity", "User");
            span.tag("repository.isNew", user.getId() == null ? "true" : "false");

            log.info("Saving user: {}", user.getEmail());

            User saved = entityManager.merge(user);
            entityManager.flush();

            span.tag("repository.savedId", saved.getId().toString());

            log.info("User saved successfully");

            return saved;

        } catch (Exception e) {
            span.error(e);
            log.error("Error saving user", e);
            throw e;
        } finally {
            span.finish();
        }
    }

    public List<User> findAllWithCustomQuery() {
        Span span = tracer.nextSpan().name("repository.customQuery").start();

        try (Tracer.SpanInScope scope = tracer.withSpanInScope(span)) {
            span.tag("repository.operation", "customQuery");
            span.tag("repository.query", "findActiveUsers");

            log.info("Executing custom query");

            TypedQuery<User> query = entityManager.createQuery(
                "SELECT u FROM User u WHERE u.active = true", User.class);

            List<User> users = query.getResultList();

            span.tag("repository.resultCount", users.size());

            log.info("Custom query completed, found {} users", users.size());

            return users;

        } catch (Exception e) {
            span.error(e);
            log.error("Error executing custom query", e);
            throw e;
        } finally {
            span.finish();
        }
    }
}
```

### Spring Cloud Stream интеграция

```java
@Configuration
@EnableBinding({Source.class, Sink.class})
@Slf4j
public class MessagingTracingConfiguration {

    @StreamListener(Sink.INPUT)
    public void handleMessage(@Payload String message, @Header("traceId") String traceId) {
        log.info("Received message: {} with traceId: {}", message, traceId);

        Span span = tracer.nextSpan().name("message.process").start();
        span.tag("message.content", message);
        span.tag("message.traceId", traceId);

        try (Tracer.SpanInScope scope = tracer.withSpanInScope(span)) {
            // Обработка сообщения
            processMessage(message);

            log.info("Message processed successfully");
        } catch (Exception e) {
            span.error(e);
            log.error("Error processing message", e);
            throw e;
        } finally {
            span.finish();
        }
    }

    @Bean
    public MessageChannel customOutputChannel() {
        return new DirectChannel();
    }

    @ServiceActivator(inputChannel = "customOutputChannel")
    public Message<?> enrichMessageWithTraceContext(Message<?> message) {
        // Добавление trace context в сообщение
        Span currentSpan = tracer.currentSpan();
        if (currentSpan != null) {
            return MessageBuilder.fromMessage(message)
                .setHeader("traceId", currentSpan.context().traceId())
                .setHeader("spanId", currentSpan.context().spanId())
                .build();
        }

        return message;
    }

    private void processMessage(String message) {
        // Имитация обработки сообщения
        log.info("Processing message: {}", message);
    }
}
```

## Анотационная конфигурация

### @NewSpan

```java
@Service
@Slf4j
public class AnnotatedTracingService {

    @NewSpan("user.lookup")
    public UserDto findUser(@SpanTag("user.id") Long userId) {
        log.info("Looking up user with id: {}", userId);

        // Sleuth автоматически создаст новый span
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new UserNotFoundException(userId));

        log.info("User found: {}", user.getName());
        return convertToDto(user);
    }

    @NewSpan("user.creation")
    public UserDto createUser(@SpanTag("user.email") String email, @SpanTag("user.name") String name) {
        log.info("Creating user: {} {}", name, email);

        User user = new User();
        user.setName(name);
        user.setEmail(email);
        user.setActive(true);
        user.setCreatedAt(LocalDateTime.now());

        User saved = userRepository.save(user);

        log.info("User created with id: {}", saved.getId());
        return convertToDto(saved);
    }

    @NewSpan("complex.business.logic")
    public void performComplexBusinessLogic(
            @SpanTag("operation.param1") String param1,
            @SpanTag("operation.param2") int param2) {

        log.info("Starting complex business logic");

        // Шаг 1
        Span currentSpan = tracer.currentSpan();
        if (currentSpan != null) {
            currentSpan.tag("step", "validation");
        }

        validateParameters(param1, param2);

        // Шаг 2
        if (currentSpan != null) {
            currentSpan.tag("step", "processing");
        }

        processParameters(param1, param2);

        // Шаг 3
        if (currentSpan != null) {
            currentSpan.tag("step", "completion");
        }

        completeOperation();

        log.info("Complex business logic completed");
    }

    @ContinueSpan
    public void continueExistingSpan() {
        // Продолжает существующий span вместо создания нового
        log.info("Continuing existing span");

        Span currentSpan = tracer.currentSpan();
        if (currentSpan != null) {
            currentSpan.tag("continued", "true");
        }

        // Дополнительная работа в том же span
        additionalWork();
    }

    private void validateParameters(String param1, int param2) {
        // Валидация параметров
    }

    private void processParameters(String param1, int param2) {
        // Обработка параметров
    }

    private void completeOperation() {
        // Завершение операции
    }

    private void additionalWork() {
        // Дополнительная работа
    }

    private UserDto convertToDto(User user) {
        // Конвертация в DTO
        return new UserDto();
    }
}
```

### Кастомные аннотации

```java
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@NewSpan
public @interface TracedOperation {

    String name() default "";

    String[] tags() default {};
}

@Aspect
@Component
public class TracedOperationAspect {

    private final Tracer tracer;

    public TracedOperationAspect(Tracer tracer) {
        this.tracer = tracer;
    }

    @Around("@annotation(tracedOperation)")
    public Object traceOperation(ProceedingJoinPoint joinPoint, TracedOperation tracedOperation) throws Throwable {
        String spanName = tracedOperation.name().isEmpty() ?
            joinPoint.getSignature().getName() : tracedOperation.name();

        Span span = tracer.nextSpan().name(spanName).start();

        // Добавление тегов
        for (String tag : tracedOperation.tags()) {
            String[] parts = tag.split("=");
            if (parts.length == 2) {
                span.tag(parts[0], parts[1]);
            }
        }

        // Добавление информации о методе
        span.tag("class", joinPoint.getTarget().getClass().getSimpleName());
        span.tag("method", joinPoint.getSignature().getName());

        try (Tracer.SpanInScope scope = tracer.withSpanInScope(span)) {
            Object result = joinPoint.proceed();

            // Добавление информации о результате
            if (result != null) {
                span.tag("result.type", result.getClass().getSimpleName());
            }

            return result;

        } catch (Exception e) {
            span.error(e);
            throw e;
        } finally {
            span.finish();
        }
    }
}

// Использование кастомной аннотации
@Service
public class CustomAnnotatedService {

    @TracedOperation(name = "user.search", tags = {"operation=search", "entity=user"})
    public List<UserDto> searchUsers(String query, int limit) {
        // Имитация поиска пользователей
        return new ArrayList<>();
    }

    @TracedOperation(name = "data.export", tags = {"operation=export", "format=csv"})
    public byte[] exportData(String format) {
        // Имитация экспорта данных
        return new byte[0];
    }
}
```

## Распространение контекста

### Baggage propagation

```java
@Configuration
public class BaggageConfiguration {

    @Bean
    public BaggageField userIdField() {
        return BaggageField.create("userId");
    }

    @Bean
    public BaggageField requestIdField() {
        return BaggageField.create("requestId");
    }

    @Bean
    public BaggageField sessionIdField() {
        return BaggageField.create("sessionId");
    }
}

@RestController
@RequestMapping("/api")
@Slf4j
public class BaggageController {

    @Autowired
    private BaggageField userIdField;

    @Autowired
    private BaggageField requestIdField;

    @Autowired
    private BaggageField sessionIdField;

    @GetMapping("/profile")
    public ResponseEntity<UserProfile> getUserProfile(@RequestHeader("X-User-Id") String userId,
                                                     @RequestHeader("X-Session-Id") String sessionId) {
        // Установка baggage для распространения контекста
        userIdField.updateValue(userId);
        sessionIdField.updateValue(sessionId);
        requestIdField.updateValue(UUID.randomUUID().toString());

        log.info("Getting profile for user: {}", userId);

        // Baggage автоматически передается во все дочерние операции
        UserProfile profile = userService.getUserProfile();

        log.info("Profile retrieved for user: {}", userId);
        return ResponseEntity.ok(profile);
    }

    @PostMapping("/transfer")
    public ResponseEntity<TransferResult> transferMoney(@RequestBody TransferRequest request) {
        log.info("Processing transfer: {} -> {} : {}",
            request.getFromUserId(), request.getToUserId(), request.getAmount());

        // Baggage сохраняется между сервисами
        TransferResult result = transferService.processTransfer(request);

        log.info("Transfer completed: {}", result.getTransactionId());
        return ResponseEntity.ok(result);
    }
}

@Service
@Slf4j
public class UserService {

    @Autowired
    private BaggageField userIdField;

    @Autowired
    private BaggageField requestIdField;

    public UserProfile getUserProfile() {
        String userId = userIdField.getValue();
        String requestId = requestIdField.getValue();

        log.info("Fetching profile for user {} in request {}", userId, requestId);

        // Имитация получения профиля
        UserProfile profile = new UserProfile();
        profile.setUserId(userId);
        profile.setRequestId(requestId);

        return profile;
    }
}

@Service
@Slf4j
public class TransferService {

    @Autowired
    private BaggageField userIdField;

    @Autowired
    private BaggageField sessionIdField;

    public TransferResult processTransfer(TransferRequest request) {
        String userId = userIdField.getValue();
        String sessionId = sessionIdField.getValue();

        log.info("Processing transfer for user {} in session {}", userId, sessionId);

        // Имитация обработки перевода
        TransferResult result = new TransferResult();
        result.setTransactionId(UUID.randomUUID().toString());
        result.setStatus("COMPLETED");

        return result;
    }
}
```

### HTTP headers propagation

```java
@Configuration
public class HttpTracingConfiguration {

    @Bean
    public RestTemplate restTemplate(RestTemplateBuilder builder) {
        return builder
            .additionalInterceptors(new TracingRestTemplateInterceptor())
            .build();
    }

    @Bean
    public WebClient webClient(WebClient.Builder builder) {
        return builder
            .filter(new TracingWebClientFilter())
            .build();
    }
}

@Component
@Slf4j
public class TracingRestTemplateInterceptor implements ClientHttpRequestInterceptor {

    private final Tracer tracer;

    public TracingRestTemplateInterceptor(Tracer tracer) {
        this.tracer = tracer;
    }

    @Override
    public ClientHttpResponse intercept(HttpRequest request, byte[] body,
                                      ClientHttpRequestExecution execution) throws IOException {

        Span span = tracer.nextSpan().name("http.outgoing").start();

        try (Tracer.SpanInScope scope = tracer.withSpanInScope(span)) {
            span.tag("http.method", request.getMethod().name());
            span.tag("http.url", request.getURI().toString());

            // Добавление trace headers
            request.getHeaders().add("X-Trace-Id", span.context().traceId());
            request.getHeaders().add("X-Span-Id", span.context().spanId());

            // Добавление baggage
            Baggage.current().forEach((key, baggageEntry) -> {
                request.getHeaders().add("X-Baggage-" + key, baggageEntry.getValue());
            });

            log.info("Making outgoing HTTP request: {} {}", request.getMethod(), request.getURI());

            ClientHttpResponse response = execution.execute(request, body);

            span.tag("http.status", response.getStatusCode().value());

            log.info("HTTP request completed with status: {}", response.getStatusCode());

            return response;

        } catch (Exception e) {
            span.error(e);
            log.error("HTTP request failed", e);
            throw e;
        } finally {
            span.finish();
        }
    }
}

@Component
public class TracingWebClientFilter implements ExchangeFilterFunction {

    private final Tracer tracer;

    public TracingWebClientFilter(Tracer tracer) {
        this.tracer = tracer;
    }

    @Override
    public Mono<ClientResponse> filter(ClientRequest request, ExchangeFunction next) {
        Span span = tracer.nextSpan().name("webclient.outgoing").start();

        return Mono.fromCallable(() -> {
            try (Tracer.SpanInScope scope = tracer.withSpanInScope(span)) {
                span.tag("http.method", request.method().name());
                span.tag("http.url", request.url().toString());

                // Создание запроса с trace headers
                ClientRequest tracedRequest = ClientRequest.from(request)
                    .header("X-Trace-Id", span.context().traceId())
                    .header("X-Span-Id", span.context().spanId())
                    .build();

                return next.exchange(tracedRequest)
                    .doOnNext(response -> {
                        span.tag("http.status", response.statusCode().value());
                        span.finish();
                    })
                    .doOnError(error -> {
                        span.error(error);
                        span.finish();
                    });

            } catch (Exception e) {
                span.error(e);
                span.finish();
                throw e;
            }
        }).flatMap(Function.identity());
    }
}
```

## Интеграция с внешними системами

### Zipkin

```yaml
# application.yml
spring:
  sleuth:
    tracer:
      mode: B3  # B3 propagation для Zipkin

  zipkin:
    enabled: true
    base-url: http://localhost:9411
    sender:
      type: web  # Отправка через HTTP

management:
  tracing:
    sampling:
      probability: 1.0
  zipkin:
    tracing:
      endpoint: http://localhost:9411/api/v2/spans
```

### Jaeger

```yaml
# application.yml
spring:
  sleuth:
    tracer:
      mode: B3

management:
  tracing:
    sampling:
      probability: 1.0

# Для OpenTelemetry с Jaeger
otel:
  exporter:
    jaeger:
      endpoint: http://localhost:14250
  traces:
    exporter: jaeger
```

### OpenTelemetry Collector

```yaml
# application.yml
management:
  tracing:
    sampling:
      probability: 1.0

# OpenTelemetry
otel:
  service:
    name: my-spring-service
  traces:
    exporter: otlp
  metrics:
    exporter: otlp

# OTLP exporter
otel:
  exporter:
    otlp:
      endpoint: http://otel-collector:4317
      headers:
        api-key: my-api-key
```

### Кастомный экспортер

```java
@Configuration
public class CustomExporterConfiguration {

    @Bean
    public SpanExporter customSpanExporter() {
        return new CustomSpanExporter();
    }

    @Bean
    public MeterRegistryCustomizer<MeterRegistry> meterRegistryCustomizer() {
        return registry -> {
            registry.config()
                .meterFilter(new CustomMetricsFilter());
        };
    }
}

@Slf4j
public class CustomSpanExporter implements SpanExporter {

    @Override
    public CompletableResultCode export(Collection<SpanData> spans) {
        for (SpanData span : spans) {
            log.info("Exporting span: {} - {}ms",
                span.getName(),
                span.getEndEpochNanos() - span.getStartEpochNanos());
        }

        // Отправка в кастомную систему мониторинга
        sendToCustomMonitoring(spans);

        return CompletableResultCode.ofSuccess();
    }

    @Override
    public CompletableResultCode flush() {
        log.info("Flushing spans");
        return CompletableResultCode.ofSuccess();
    }

    @Override
    public CompletableResultCode shutdown() {
        log.info("Shutting down custom exporter");
        return CompletableResultCode.ofSuccess();
    }

    private void sendToCustomMonitoring(Collection<SpanData> spans) {
        // Имитация отправки в кастомную систему
        spans.forEach(span -> {
            // Отправка в вашу систему мониторинга
            System.out.println("Sending span to custom monitoring: " + span.getName());
        });
    }
}

public class CustomMetricsFilter implements MeterFilter {

    @Override
    public MeterFilterReply accept(Meter.Id id) {
        // Фильтрация метрик
        if (id.getName().startsWith("jvm.")) {
            return MeterFilterReply.DENY; // Исключаем JVM метрики
        }

        if (id.getName().contains("debug")) {
            return MeterFilterReply.DENY; // Исключаем debug метрики
        }

        return MeterFilterReply.NEUTRAL;
    }

    @Override
    public Meter.Id map(Meter.Id id) {
        // Модификация имен метрик
        if (id.getName().startsWith("http.")) {
            return id.withName("web." + id.getName().substring(5));
        }

        return id;
    }
}
```

## Мониторинг и отладка

### Tracing endpoint

```java
@RestController
@RequestMapping("/actuator/tracing")
public class TracingEndpointController {

    private final Tracer tracer;

    public TracingEndpointController(Tracer tracer) {
        this.tracer = tracer;
    }

    @GetMapping
    public Map<String, Object> getCurrentTraceInfo() {
        Span currentSpan = tracer.currentSpan();

        Map<String, Object> traceInfo = new HashMap<>();

        if (currentSpan != null) {
            SpanContext context = currentSpan.context();

            traceInfo.put("traceId", context.traceId());
            traceInfo.put("spanId", context.spanId());
            traceInfo.put("parentSpanId", context.parentSpanId());
            traceInfo.put("sampled", context.isSampled());
            traceInfo.put("spanName", currentSpan.toString());
        } else {
            traceInfo.put("message", "No active span");
        }

        // Информация о baggage
        Map<String, String> baggage = new HashMap<>();
        Baggage.current().forEach((key, entry) -> baggage.put(key, entry.getValue()));
        traceInfo.put("baggage", baggage);

        return traceInfo;
    }

    @PostMapping("/start-span")
    public String startCustomSpan(@RequestParam String name) {
        Span span = tracer.nextSpan().name(name).start();

        try (Tracer.SpanInScope scope = tracer.withSpanInScope(span)) {
            span.tag("custom", "true");
            span.tag("started.by", "endpoint");

            // Имитация работы
            Thread.sleep(100);

            return "Span created: " + span.context().spanId();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return "Interrupted";
        } finally {
            span.finish();
        }
    }

    @GetMapping("/spans")
    public List<Map<String, Object>> getRecentSpans() {
        // В реальном приложении можно хранить недавние span
        // Для демонстрации возвращаем mock данные
        List<Map<String, Object>> spans = new ArrayList<>();

        Span currentSpan = tracer.currentSpan();
        if (currentSpan != null) {
            Map<String, Object> spanInfo = new HashMap<>();
            spanInfo.put("name", currentSpan.toString());
            spanInfo.put("traceId", currentSpan.context().traceId());
            spanInfo.put("spanId", currentSpan.context().spanId());
            spans.add(spanInfo);
        }

        return spans;
    }
}
```

### Health indicators

```java
@Component
public class TracingHealthIndicator implements HealthIndicator {

    private final Tracer tracer;

    public TracingHealthIndicator(Tracer tracer) {
        this.tracer = tracer;
    }

    @Override
    public Health health() {
        try {
            // Проверка работоспособности tracing
            Span testSpan = tracer.nextSpan().name("health.check").start();

            try (Tracer.SpanInScope scope = tracer.withSpanInScope(testSpan)) {
                testSpan.tag("health.check", "tracing");
                // Имитация работы
                Thread.sleep(10);
            } finally {
                testSpan.finish();
            }

            return Health.up()
                .withDetail("tracing", "operational")
                .build();

        } catch (Exception e) {
            return Health.down()
                .withDetail("tracing", "failed")
                .withDetail("error", e.getMessage())
                .build();
        }
    }
}
```

### Отладка и логирование

```java
@Configuration
public class TracingDebugConfiguration {

    @Bean
    @ConditionalOnProperty(value = "tracing.debug.enabled", havingValue = "true")
    public TracingDebugAspect tracingDebugAspect() {
        return new TracingDebugAspect();
    }
}

@Aspect
@Component
@Slf4j
public class TracingDebugAspect {

    @Around("@within(org.springframework.web.bind.annotation.RestController)")
    public Object logControllerMethods(ProceedingJoinPoint joinPoint) throws Throwable {
        Span currentSpan = tracer.currentSpan();

        if (currentSpan != null) {
            log.info("[TRACE] {} - Entering controller method: {}.{}",
                currentSpan.context().traceId(),
                joinPoint.getTarget().getClass().getSimpleName(),
                joinPoint.getSignature().getName());

            // Логирование параметров
            Object[] args = joinPoint.getArgs();
            for (int i = 0; i < args.length; i++) {
                log.debug("[TRACE] {} - Parameter {}: {}",
                    currentSpan.context().traceId(), i, args[i]);
            }
        }

        long startTime = System.nanoTime();

        try {
            Object result = joinPoint.proceed();

            long duration = System.nanoTime() - startTime;

            if (currentSpan != null) {
                log.info("[TRACE] {} - Controller method completed in {}ms",
                    currentSpan.context().traceId(), duration / 1_000_000);

                if (result != null) {
                    log.debug("[TRACE] {} - Result type: {}",
                        currentSpan.context().traceId(), result.getClass().getSimpleName());
                }
            }

            return result;

        } catch (Exception e) {
            if (currentSpan != null) {
                log.error("[TRACE] {} - Controller method failed",
                    currentSpan.context().traceId(), e);
            }
            throw e;
        }
    }

    @Around("@within(org.springframework.stereotype.Service)")
    public Object logServiceMethods(ProceedingJoinPoint joinPoint) throws Throwable {
        Span currentSpan = tracer.currentSpan();

        if (currentSpan != null) {
            log.debug("[TRACE] {} - Entering service method: {}.{}",
                currentSpan.context().traceId(),
                joinPoint.getTarget().getClass().getSimpleName(),
                joinPoint.getSignature().getName());
        }

        try {
            return joinPoint.proceed();
        } catch (Exception e) {
            if (currentSpan != null) {
                log.warn("[TRACE] {} - Service method threw exception: {}",
                    currentSpan.context().traceId(), e.getClass().getSimpleName());
            }
            throw e;
        }
    }
}
```

## Best practices

### 1. Правильная конфигурация sampling

```java
// ✅ Хорошо - адаптивное sampling
@Configuration
public class SamplingConfiguration {

    @Bean
    @ConditionalOnProperty(value = "tracing.environment", havingValue = "production", matchIfMissing = false)
    public Sampler productionSampler() {
        return Sampler.create(0.1); // 10% для production
    }

    @Bean
    @ConditionalOnProperty(value = "tracing.environment", havingValue = "staging", matchIfMissing = false)
    public Sampler stagingSampler() {
        return Sampler.create(0.5); // 50% для staging
    }

    @Bean
    @ConditionalOnMissingBean
    public Sampler defaultSampler() {
        return Sampler.ALWAYS_SAMPLE; // 100% для development
    }

    // Кастомный sampler с правилами
    @Bean
    public Sampler ruleBasedSampler() {
        return new RuleBasedSampler(
            // Всегда сэмплировать ошибки
            new AlwaysSampler().withTag("error", "true"),

            // Всегда сэмплировать HTTP запросы к /api
            new PathSampler("/api/**"),

            // 50% для остальных
            new ProbabilitySampler(0.5)
        );
    }
}

class RuleBasedSampler implements Sampler {

    private final List<SamplingRule> rules;
    private final Sampler defaultSampler;

    public RuleBasedSampler(Sampler defaultSampler, SamplingRule... rules) {
        this.rules = Arrays.asList(rules);
        this.defaultSampler = defaultSampler;
    }

    @Override
    public boolean isSampled(SpanContext parentContext, TraceId traceId, String name,
                           SpanKind spanKind, Attributes attributes, List<Link> parentLinks,
                           SamplerResult samplerResult) {

        // Проверка правил
        for (SamplingRule rule : rules) {
            if (rule.matches(name, attributes)) {
                return rule.shouldSample();
            }
        }

        // Default sampling
        return defaultSampler.isSampled(parentContext, traceId, name, spanKind,
                                      attributes, parentLinks, samplerResult);
    }
}

interface SamplingRule {
    boolean matches(String spanName, Attributes attributes);
    boolean shouldSample();
}

class AlwaysSampler implements SamplingRule {
    private final Map<String, String> requiredTags;

    public AlwaysSampler withTag(String key, String value) {
        requiredTags.put(key, value);
        return this;
    }

    @Override
    public boolean matches(String spanName, Attributes attributes) {
        return requiredTags.entrySet().stream()
            .allMatch(entry -> entry.getValue().equals(
                attributes.get(AttributeKey.stringKey(entry.getKey()))));
    }

    @Override
    public boolean shouldSample() {
        return true;
    }
}

class PathSampler implements SamplingRule {
    private final Pattern pathPattern;

    public PathSampler(String pathPattern) {
        this.pathPattern = Pattern.compile(pathPattern.replace("**", ".*"));
    }

    @Override
    public boolean matches(String spanName, Attributes attributes) {
        String httpUrl = attributes.get(AttributeKey.stringKey("http.url"));
        return httpUrl != null && pathPattern.matcher(httpUrl).matches();
    }

    @Override
    public boolean shouldSample() {
        return true;
    }
}

// ❌ Плохо - неправильное sampling
@Configuration
public class BadSamplingConfiguration {

    // Плохо: всегда сэмплировать в production
    @Bean
    public Sampler badProductionSampler() {
        return Sampler.ALWAYS_SAMPLE; // Создаст слишком много данных
    }

    // Плохо: никогда не сэмплировать
    @Bean
    public Sampler badDevelopmentSampler() {
        return Sampler.NEVER_SAMPLE; // Невозможно отлаживать
    }

    // Плохо: фиксированное значение без учета контекста
    @Bean
    public Sampler badFixedSampler() {
        return Sampler.create(0.5); // Не учитывает важность операций
    }
}
```

### 2. Правильное именование span

```java
// ✅ Хорошо - последовательное именование
@Service
public class NamingBestPractices {

    private final Tracer tracer;

    public NamingBestPractices(Tracer tracer) {
        this.tracer = tracer;
    }

    public void httpOperation(HttpServletRequest request) {
        // Хорошо: описательное имя с методом
        Span span = tracer.nextSpan()
            .name("http." + request.getMethod().toLowerCase())
            .start();

        try (Tracer.SpanInScope scope = tracer.withSpanInScope(span)) {
            span.tag("http.method", request.getMethod());
            span.tag("http.uri", request.getRequestURI());
            // ...
        } finally {
            span.finish();
        }
    }

    public void databaseOperation(String table, String operation) {
        // Хорошо: имя с компонентом и операцией
        Span span = tracer.nextSpan()
            .name("db." + operation.toLowerCase())
            .start();

        try (Tracer.SpanInScope scope = tracer.withSpanInScope(span)) {
            span.tag("db.table", table);
            span.tag("db.operation", operation);
            // ...
        } finally {
            span.finish();
        }
    }

    public void businessOperation(String operation, Map<String, Object> context) {
        // Хорошо: бизнес-ориентированное имя
        Span span = tracer.nextSpan()
            .name("business." + operation.toLowerCase().replace(" ", "."))
            .start();

        try (Tracer.SpanInScope scope = tracer.withSpanInScope(span)) {
            // Добавление бизнес-контекста как тегов
            context.forEach((key, value) ->
                span.tag("business." + key, value.toString()));
            // ...
        } finally {
            span.finish();
        }
    }

    public void externalServiceCall(String serviceName, String operation) {
        // Хорошо: имя с внешним компонентом
        Span span = tracer.nextSpan()
            .name("external." + serviceName.toLowerCase() + "." + operation.toLowerCase())
            .start();

        try (Tracer.SpanInScope scope = tracer.withSpanInScope(span)) {
            span.tag("external.service", serviceName);
            span.tag("external.operation", operation);
            // ...
        } finally {
            span.finish();
        }
    }

    // ❌ Плохо - непоследовательное именование
    public void badNamingExamples() {
        // Плохо: слишком длинные имена
        Span span1 = tracer.nextSpan()
            .name("very.long.span.name.that.is.hard.to.read.and.understand")
            .start();

        // Плохо: слишком короткие имена
        Span span2 = tracer.nextSpan()
            .name("op")
            .start();

        // Плохо: неинформативные имена
        Span span3 = tracer.nextSpan()
            .name("span1")
            .start();

        // Плохо: имена с переменными
        String dynamicName = "operation-" + System.currentTimeMillis();
        Span span4 = tracer.nextSpan()
            .name(dynamicName) // Имя меняется, сложно анализировать
            .start();
    }
}
```

### 3. Обработка ошибок и исключений

```java
// ✅ Хорошо - правильная обработка ошибок
@Service
@Slf4j
public class ErrorHandlingBestPractices {

    private final Tracer tracer;

    public ErrorHandlingBestPractices(Tracer tracer) {
        this.tracer = tracer;
    }

    public void handleErrorsProperly() {
        Span span = tracer.nextSpan().name("operation.with.error.handling").start();

        try (Tracer.SpanInScope scope = tracer.withSpanInScope(span)) {
            // Операция, которая может выбросить исключение
            riskyOperation();

            span.tag("operation.result", "success");

        } catch (BusinessException e) {
            // Бизнес ошибки - не устанавливаем error в span
            span.tag("operation.result", "business_error");
            span.tag("error.type", "business");
            span.tag("error.code", e.getErrorCode());

            log.warn("Business error occurred: {}", e.getMessage());

            throw e; // Перебрасываем бизнес исключение

        } catch (TechnicalException e) {
            // Технические ошибки - устанавливаем error в span
            span.error(e);
            span.tag("operation.result", "technical_error");
            span.tag("error.type", "technical");

            log.error("Technical error occurred", e);

            throw e; // Перебрасываем техническое исключение

        } catch (Exception e) {
            // Неожиданные ошибки - устанавливаем error и логируем
            span.error(e);
            span.tag("operation.result", "unexpected_error");
            span.tag("error.type", "unexpected");
            span.tag("error.severity", "high");

            log.error("Unexpected error occurred", e);

            // Можно обернуть в кастомное исключение
            throw new SystemException("Unexpected error occurred", e);
        } finally {
            span.finish();
        }
    }

    public void handleAsyncErrors() {
        Span parentSpan = tracer.nextSpan().name("async.operation").start();

        try (Tracer.SpanInScope parentScope = tracer.withSpanInScope(parentSpan)) {
            // Асинхронная операция
            CompletableFuture.supplyAsync(this::asyncRiskyOperation)
                .whenComplete((result, throwable) -> {
                    Span childSpan = tracer.nextSpan().name("async.completion").start();

                    try (Tracer.SpanInScope childScope = tracer.withSpanInScope(childSpan)) {
                        if (throwable != null) {
                            childSpan.error(throwable);
                            childSpan.tag("async.result", "error");
                            log.error("Async operation failed", throwable);
                        } else {
                            childSpan.tag("async.result", "success");
                            childSpan.tag("async.value", result);
                            log.info("Async operation completed: {}", result);
                        }
                    } finally {
                        childSpan.finish();
                    }
                });
        } finally {
            parentSpan.finish();
        }
    }

    public void handleValidationErrors() {
        Span span = tracer.nextSpan().name("validation.operation").start();

        List<String> errors = new ArrayList<>();

        try (Tracer.SpanInScope scope = tracer.withSpanInScope(span)) {
            // Валидация
            ValidationResult result = performValidation();

            if (!result.isValid()) {
                errors.addAll(result.getErrors());
                span.tag("validation.errors", errors.size());

                // Создаем ValidationException с деталями
                ValidationException exception = new ValidationException("Validation failed", errors);
                span.error(exception);

                throw exception;
            }

            span.tag("validation.result", "valid");

        } catch (ValidationException e) {
            // Validation errors не являются системными ошибками
            span.tag("operation.result", "validation_failed");
            throw e;
        } finally {
            span.finish();
        }
    }

    // Вспомогательные методы
    private void riskyOperation() {
        if (Math.random() > 0.7) {
            throw new BusinessException("Business logic error", "ERR_001");
        }
        if (Math.random() > 0.9) {
            throw new TechnicalException("Database connection failed");
        }
    }

    private String asyncRiskyOperation() {
        try {
            Thread.sleep(100);
            if (Math.random() > 0.8) {
                throw new RuntimeException("Async operation failed");
            }
            return "Async result";
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("Interrupted");
        }
    }

    private ValidationResult performValidation() {
        // Имитация валидации
        return Math.random() > 0.5 ?
            ValidationResult.valid() :
            ValidationResult.invalid(Arrays.asList("Field is required", "Invalid format"));
    }
}

// Кастомные исключения
class BusinessException extends RuntimeException {
    private final String errorCode;

    public BusinessException(String message, String errorCode) {
        super(message);
        this.errorCode = errorCode;
    }

    public String getErrorCode() {
        return errorCode;
    }
}

class TechnicalException extends RuntimeException {
    public TechnicalException(String message) {
        super(message);
    }
}

class SystemException extends RuntimeException {
    public SystemException(String message, Throwable cause) {
        super(message, cause);
    }
}

class ValidationException extends RuntimeException {
    private final List<String> errors;

    public ValidationException(String message, List<String> errors) {
        super(message);
        this.errors = errors;
    }

    public List<String> getErrors() {
        return errors;
    }
}

class ValidationResult {
    private final boolean valid;
    private final List<String> errors;

    private ValidationResult(boolean valid, List<String> errors) {
        this.valid = valid;
        this.errors = errors;
    }

    public static ValidationResult valid() {
        return new ValidationResult(true, Collections.emptyList());
    }

    public static ValidationResult invalid(List<String> errors) {
        return new ValidationResult(false, errors);
    }

    public boolean isValid() {
        return valid;
    }

    public List<String> getErrors() {
        return errors;
    }
}

// ❌ Плохо - неправильная обработка ошибок
@Service
public class BadErrorHandling {

    private final Tracer tracer;

    public BadErrorHandling(Tracer tracer) {
        this.tracer = tracer;
    }

    public void badErrorHandling() {
        Span span = tracer.nextSpan().name("bad.operation").start();

        try {
            riskyOperation();
        } catch (Exception e) {
            // Плохо: не логируем ошибку в span
            // Плохо: не устанавливаем соответствующие теги
            // Плохо: теряем информацию об ошибке
            throw new RuntimeException("Something went wrong");
        } finally {
            span.finish();
        }
    }

    public void swallowErrors() {
        Span span = tracer.nextSpan().name("error.swallowing").start();

        try {
            riskyOperation();
        } catch (Exception e) {
            // Плохо: подавляем ошибку без логирования
            span.tag("operation.result", "failed");
            // Не устанавливаем error в span
            // Не перебрасываем исключение
        } finally {
            span.finish();
        }
    }

    public void inconsistentErrorReporting() {
        Span span = tracer.nextSpan().name("inconsistent.errors").start();

        try {
            riskyOperation();

            // Плохо: устанавливаем success, но может быть exception
            span.tag("result", "success");

        } catch (BusinessException e) {
            // Иногда устанавливаем error
            span.error(e);
            throw e;
        } catch (TechnicalException e) {
            // Иногда не устанавливаем error
            span.tag("result", "failed");
            // Нет span.error(e)
            throw e;
        } finally {
            span.finish();
        }
    }

    private void riskyOperation() {
        // Имитация рискованной операции
    }
}
```

### 4. Производительность и оптимизация

```java
// ✅ Хорошо - оптимизация tracing
@Configuration
public class PerformanceBestPractices {

    @Bean
    public MeterRegistryCustomizer<MeterRegistry> tracingMetricsCustomizer() {
        return registry -> {
            // Отключаем тяжелые метрики в высоконагруженных средах
            registry.config()
                .meterFilter(MeterFilter.denyNameStartsWith("sleuth."))
                .meterFilter(MeterFilter.denyNameStartsWith("tracing."));
        };
    }

    @Bean
    public SpanExporter optimizedSpanExporter() {
        return BatchSpanProcessor.builder(jaegerExporter())
            .setScheduleDelay(Duration.ofSeconds(5))      // Задержка отправки
            .setMaxExportBatchSize(512)                   // Размер пакета
            .setMaxQueueSize(2048)                        // Размер очереди
            .setExportTimeout(Duration.ofSeconds(10))     // Таймаут экспорта
            .build();
    }

    // Адаптивное sampling
    @Bean
    public Sampler adaptiveSampler() {
        return new AdaptiveSampler();
    }
}

class AdaptiveSampler implements Sampler {

    private final AtomicLong requestCount = new AtomicLong(0);
    private final AtomicLong errorCount = new AtomicLong(0);

    @Override
    public boolean isSampled(SpanContext parentContext, TraceId traceId, String name,
                           SpanKind spanKind, Attributes attributes, List<Link> parentLinks,
                           SamplerResult samplerResult) {

        long totalRequests = requestCount.incrementAndGet();
        boolean hasError = attributes.get(AttributeKey.booleanKey("error")) != null;

        if (hasError) {
            errorCount.incrementAndGet();
        }

        // Адаптивная логика
        double errorRate = (double) errorCount.get() / totalRequests;

        // При высокой частоте ошибок увеличиваем sampling
        if (errorRate > 0.1) { // > 10% ошибок
            return true;
        }

        // При низкой нагрузке сэмплируем все
        if (totalRequests < 1000) {
            return true;
        }

        // Для HTTP запросов всегда сэмплируем
        if (attributes.get(AttributeKey.stringKey("http.method")) != null) {
            return true;
        }

        // Для остальных - 10%
        return ThreadLocalRandom.current().nextDouble() < 0.1;
    }
}

@Service
@Slf4j
public class OptimizedTracingService {

    private final Tracer tracer;
    private final ThreadLocal<Span> spanCache = new ThreadLocal<>();

    public OptimizedTracingService(Tracer tracer) {
        this.tracer = tracer;
    }

    // Batch span creation для снижения накладных расходов
    public void batchOperation(List<String> items) {
        Span batchSpan = tracer.nextSpan().name("batch.operation").start();

        try (Tracer.SpanInScope scope = tracer.withSpanInScope(batchSpan)) {
            batchSpan.tag("batch.size", items.size());

            // Обрабатываем элементы без создания отдельных span для каждого
            List<String> results = items.stream()
                .map(this::processItemLightweight)
                .collect(Collectors.toList());

            batchSpan.tag("batch.processed", results.size());
            batchSpan.tag("batch.successful", results.stream()
                .mapToInt(result -> result.startsWith("OK") ? 1 : 0)
                .sum());

        } finally {
            batchSpan.finish();
        }
    }

    // Легковесная обработка элементов
    private String processItemLightweight(String item) {
        try {
            // Быстрая обработка без дополнительного tracing
            return "OK: " + item.toUpperCase();
        } catch (Exception e) {
            return "ERROR: " + e.getMessage();
        }
    }

    // Условное создание span для редко выполняемых операций
    public void conditionalSpanOperation(boolean shouldTrace) {
        if (shouldTrace) {
            Span span = tracer.nextSpan().name("conditional.operation").start();

            try (Tracer.SpanInScope scope = tracer.withSpanInScope(span)) {
                performOperation();
            } finally {
                span.finish();
            }
        } else {
            // Выполняем без tracing для производительности
            performOperation();
        }
    }

    // Кэширование span для повторяющихся операций
    public void cachedSpanOperation() {
        Span span = spanCache.get();

        if (span == null) {
            span = tracer.nextSpan().name("cached.operation").start();
            spanCache.set(span);

            // Настройка span
            span.tag("cached", "true");
        }

        try (Tracer.SpanInScope scope = tracer.withSpanInScope(span)) {
            // Повторное использование span
            performOperation();

        } catch (Exception e) {
            span.error(e);
            throw e;
        }
        // Не завершаем span - он будет переиспользован
    }

    // Очистка кэша span (вызывать при завершении потока)
    public void clearSpanCache() {
        Span span = spanCache.get();
        if (span != null) {
            span.finish();
            spanCache.remove();
        }
    }

    private void performOperation() {
        // Имитация операции
    }
}

// Аспект для оптимизации tracing
@Aspect
@Component
public class TracingOptimizationAspect {

    private final Tracer tracer;

    public TracingOptimizationAspect(Tracer tracer) {
        this.tracer = tracer;
    }

    // Пропускаем tracing для геттеров и сеттеров
    @Around("execution(* com.example..*.get*(..)) || execution(* com.example..*.set*(..))")
    public Object skipGetterSetterTracing(ProceedingJoinPoint joinPoint) throws Throwable {
        // Пропускаем tracing для методов доступа
        return joinPoint.proceed();
    }

    // Tracing только для медленных методов
    @Around("@annotation(com.example.SlowMethod)")
    public Object conditionalTracing(ProceedingJoinPoint joinPoint) throws Throwable {
        long startTime = System.nanoTime();

        Object result = joinPoint.proceed();

        long duration = System.nanoTime() - startTime;
        long durationMs = duration / 1_000_000;

        // Создаем span только если метод выполняется дольше 100ms
        if (durationMs > 100) {
            Span span = tracer.nextSpan()
                .name("slow.method." + joinPoint.getSignature().getName())
                .start();

            try (Tracer.SpanInScope scope = tracer.withSpanInScope(span)) {
                span.tag("method.duration", durationMs);
                span.tag("method.class", joinPoint.getTarget().getClass().getSimpleName());
                span.tag("method.name", joinPoint.getSignature().getName());
            } finally {
                span.finish();
            }
        }

        return result;
    }

    // Агрегированное tracing для batch операций
    @Around("@annotation(com.example.BatchOperation)")
    public Object batchTracing(ProceedingJoinPoint joinPoint) throws Throwable {
        Object[] args = joinPoint.getArgs();

        // Определяем размер batch из аргументов
        int batchSize = 0;
        if (args.length > 0 && args[0] instanceof Collection) {
            batchSize = ((Collection<?>) args[0]).size();
        }

        Span span = tracer.nextSpan()
            .name("batch." + joinPoint.getSignature().getName())
            .start();

        try (Tracer.SpanInScope scope = tracer.withSpanInScope(span)) {
            span.tag("batch.size", batchSize);

            long startTime = System.nanoTime();
            Object result = joinPoint.proceed();
            long duration = System.nanoTime() - startTime;

            span.tag("batch.duration", duration / 1_000_000);

            if (result instanceof Collection) {
                span.tag("batch.result.size", ((Collection<?>) result).size());
            }

            return result;

        } catch (Exception e) {
            span.error(e);
            throw e;
        } finally {
            span.finish();
        }
    }
}

// Аннотации для аспектов
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface SlowMethod {
}

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface BatchOperation {
}

// ❌ Плохо - неоптимизированное tracing
@Service
public class BadPerformanceTracing {

    private final Tracer tracer;

    public BadPerformanceTracing(Tracer tracer) {
        this.tracer = tracer;
    }

    // Плохо: создаем span для каждой итерации цикла
    public void badLoopTracing(List<String> items) {
        for (String item : items) {
            Span span = tracer.nextSpan().name("process.item").start(); // Плохо!

            try (Tracer.SpanInScope scope = tracer.withSpanInScope(span)) {
                processItem(item);
            } finally {
                span.finish();
            }
        }
    }

    // Плохо: tracing для всех методов, включая тривиальные
    public String badGetterTracing() {
        Span span = tracer.nextSpan().name("get.name").start();

        try (Tracer.SpanInScope scope = tracer.withSpanInScope(span)) {
            return "John Doe"; // Тривиальная операция с tracing
        } finally {
            span.finish();
        }
    }

    // Плохо: неуправляемое создание span
    public void badUncontrolledSpanCreation() {
        // Создаем множество span без необходимости
        for (int i = 0; i < 100; i++) {
            Span span = tracer.nextSpan().name("unnecessary.span." + i).start();
            span.finish(); // Сразу завершаем, но создаем накладные расходы
        }
    }

    private void processItem(String item) {
        // Обработка элемента
    }
}
```

## Заключение

Spring Cloud Sleuth — это мощная библиотека для distributed tracing в Spring Boot приложениях, которая значительно упрощает observability микросервисных систем.

### Преимущества Spring Cloud Sleuth

1. **Автоматическая инструментация** — Работает out-of-the-box с Spring компонентами
2. **Spring Boot интеграция** — Простая конфигурация через properties
3. **Distributed tracing** — Отслеживание запросов через микросервисы
4. **Correlation ID** — Автоматическая генерация и передача ID
5. **Логи correlation** — Добавление trace ID в логи
6. **OpenTelemetry совместимость** — Интеграция с OpenTelemetry
7. **Reactive support** — Поддержка WebFlux и Reactor
8. **Baggage propagation** — Передача кастомных данных

### Основные паттерны использования

1. **HTTP Tracing паттерн** — Автоматическое tracing HTTP запросов
2. **Service Tracing паттерн** — Tracing бизнес-логики сервисов
3. **Database Tracing паттерн** — Tracing операций с БД
4. **Async Tracing паттерн** — Tracing асинхронных операций
5. **Messaging Tracing паттерн** — Tracing сообщений
6. **Custom Span паттерн** — Ручное создание span
7. **Baggage Propagation паттерн** — Передача контекста

### Когда использовать Spring Cloud Sleuth

**Рекомендуется:**
- Микросервисная архитектура
- Распределенные системы
- Spring Boot приложения
- Системы с complex бизнес-процессами
- Приложения с внешними зависимостями

**Особенно полезно:**
- В Spring экосистеме
- При работе с Spring Cloud
- В системах с service mesh
- При использовании Spring Cloud Gateway
- В приложениях с event-driven архитектурой

### Сравнение с альтернативами

| Инструмент | Преимущества | Недостатки |
|------------|-------------|------------|
| **Sleuth** | Spring интеграция, простота | Только Spring экосистема |
| **OpenTelemetry** | Vendor neutral, полный контроль | Более сложная настройка |
| **Jaeger Client** | Специализирован на tracing | Меньше интеграций |
| **Micrometer Tracing** | Часть Micrometer, легковесный | Меньше возможностей |
| **Custom Tracing** | Полный контроль | Требует много работы |

Spring Cloud Sleuth рекомендуется как основной выбор для distributed tracing в Spring Boot приложениях, особенно в микросервисных архитектурах.

---

[⬆️ Наверх](../README.md) | [Предыдущий: OpenTelemetry](../java-opentelemetry.md) | [Следующий: Testcontainers](../java-testcontainers.md)

*Обновлено: 2026-01-23*
