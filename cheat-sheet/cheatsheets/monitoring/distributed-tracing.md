# Распределенное трассирование

Распределенное трассирование (Distributed Tracing) - это методология отслеживания запросов в распределенных системах. Позволяет наблюдать за потоком запросов через различные сервисы, выявлять узкие места и проблемы производительности.

## Содержание
- [Основные концепции](#основные-концепции)
- [Jaeger](#jaeger)
- [Zipkin](#zipkin)
- [OpenTelemetry](#opentelemetry)
- [Baggage (Контекстные данные)](#baggage-контекстные-данные)
- [Сэмплирование](#сэмплирование)
- [Интеграция с логированием](#интеграция-с-логированием)
- [Мониторинг и алертинг](#мониторинг-и-алертинг)
- [Лучшие практики](#лучшие-практики)
- [Устранение неполадок](#устранение-неполадок)

## Основные концепции

### Span (Отрезок)
```java
// Span - базовая единица трассировки
// Представляет отдельную операцию в рамках трейса
Span span = tracer.buildSpan("operation-name")
    .withTag("component", "service-name")
    .withTag("http.method", "GET")
    .start();

try {
    // Выполнение операции
    doSomeWork();
} finally {
    span.finish();
}
```

### Trace (Трейс)
```java
// Trace - полный путь запроса через систему
// Состоит из связанных spans
Span parentSpan = tracer.activeSpan();
Span childSpan = tracer.buildSpan("child-operation")
    .asChildOf(parentSpan)
    .start();

// Создание нового трейса
SpanContext context = extractContextFromRequest(request);
Span rootSpan = tracer.buildSpan("http-request")
    .asChildOf(context)
    .start();
```

### Trace Context Propagation
```java
// Распространение контекста между сервисами
// HTTP заголовки
Map<String, String> headers = new HashMap<>();
tracer.inject(span.context(), Format.Builtin.HTTP_HEADERS, headers);

// Извлечение контекста
SpanContext extractedContext = tracer.extract(Format.Builtin.HTTP_HEADERS,
    new TextMapExtractAdapter(headers));
```

## Jaeger

### Базовая настройка
```java
// build.gradle
dependencies {
    implementation 'io.jaegertracing:jaeger-client:1.8.1'
    implementation 'io.opentracing:opentracing-api:0.33.0'
}

// Конфигурация
@Configuration
public class JaegerConfig {

    @Bean
    public Tracer jaegerTracer() {
        return Configuration.fromEnv("service-name")
            .withSampler(Samplers.newConstSampler(true))
            .withReporter(ReporterConfiguration.fromEnv()
                .withLogSpans(true))
            .getTracer();
    }
}
```

### Интеграция с Spring Boot
```java
@SpringBootApplication
public class Application {

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

    @Bean
    public Tracer tracer() {
        return Configuration.fromEnv("my-service")
            .withSampler(Samplers.newProbabilisticSampler(0.1))
            .withReporter(new CompositeReporter(
                ReporterConfiguration.fromEnv().getReporter(),
                new LoggingReporter()
            ))
            .getTracer();
    }

    @Bean
    public BraveTracer braveTracer(Tracer tracer) {
        return BraveTracer.create(tracer);
    }
}
```

### Создание spans в сервисах
```java
@Service
public class UserService {

    @Autowired
    private Tracer tracer;

    public User getUserById(Long id) {
        Span span = tracer.buildSpan("getUserById")
            .withTag("user.id", id)
            .withTag("component", "UserService")
            .start();

        try (Scope scope = tracer.activateSpan(span)) {
            span.log("Fetching user from database");

            User user = userRepository.findById(id);

            span.setTag("user.found", user != null);
            span.log("User fetched successfully");

            return user;
        } catch (Exception e) {
            span.setTag("error", true);
            span.log(Map.of("error.message", e.getMessage()));
            throw e;
        } finally {
            span.finish();
        }
    }
}
```

### HTTP клиент с трассировкой
```java
@Service
public class HttpClient {

    @Autowired
    private Tracer tracer;

    @Autowired
    private RestTemplate restTemplate;

    public String callExternalService(String url) {
        Span span = tracer.buildSpan("http-client-call")
            .withTag("http.method", "GET")
            .withTag("http.url", url)
            .start();

        try (Scope scope = tracer.activateSpan(span)) {
            // Инъекция headers для распространения контекста
            HttpHeaders headers = new HttpHeaders();
            tracer.inject(span.context(),
                Format.Builtin.HTTP_HEADERS,
                new HttpHeadersCarrier(headers));

            HttpEntity<String> entity = new HttpEntity<>(headers);
            ResponseEntity<String> response = restTemplate.exchange(
                url, HttpMethod.GET, entity, String.class);

            span.setTag("http.status_code", response.getStatusCodeValue());
            return response.getBody();

        } catch (Exception e) {
            span.setTag("error", true);
            span.log(Map.of("error.message", e.getMessage()));
            throw e;
        } finally {
            span.finish();
        }
    }
}

// Кастомный carrier для HttpHeaders
public class HttpHeadersCarrier implements TextMap {
    private final HttpHeaders headers;

    public HttpHeadersCarrier(HttpHeaders headers) {
        this.headers = headers;
    }

    @Override
    public Iterator<Map.Entry<String, String>> iterator() {
        return headers.entrySet().stream()
            .flatMap(entry -> entry.getValue().stream()
                .map(value -> new AbstractMap.SimpleEntry<>(entry.getKey(), value)))
            .iterator();
    }

    @Override
    public void put(String key, String value) {
        headers.add(key, value);
    }
}
```

## Zipkin

### Настройка Zipkin клиента
```java
@Configuration
public class ZipkinConfig {

    @Bean
    public ZipkinSpanHandler zipkinSpanHandler() {
        return ZipkinSpanHandler.create(
            WebClientSender.create("http://localhost:9411/api/v2/spans"),
            Resource.get("my-service")
        );
    }

    @Bean
    public BraveTracer braveTracer(ZipkinSpanHandler spanHandler) {
        return BraveTracer.create(
            Tracing.newBuilder()
                .localServiceName("my-service")
                .addSpanHandler(spanHandler)
                .sampler(Sampler.ALWAYS_SAMPLE)
                .build().tracer()
        );
    }
}
```

### Spring Cloud Sleuth
```java
// build.gradle
dependencies {
    implementation 'org.springframework.cloud:spring-cloud-starter-sleuth:3.1.0'
    implementation 'org.springframework.cloud:spring-cloud-sleuth-zipkin:3.1.0'
}

// application.yml
spring:
  sleuth:
    sampler:
      probability: 1.0  # 100% сэмплирование для разработки
  zipkin:
    base-url: http://localhost:9411

logging:
  pattern:
    correlation: "[${spring.application.name:},%X{trace_id:-},%X{span_id:-}]"
```

### Автоматическая трассировка с Sleuth
```java
@RestController
public class UserController {

    @Autowired
    private UserService userService;

    @GetMapping("/users/{id}")
    public User getUser(@PathVariable Long id) {
        // Sleuth автоматически создаст span для этого метода
        return userService.getUserById(id);
    }

    @PostMapping("/users")
    public User createUser(@RequestBody CreateUserRequest request) {
        // Span будет создан автоматически
        return userService.createUser(request.getName(), request.getEmail());
    }
}

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private Tracer tracer;

    public User getUserById(Long id) {
        Span span = tracer.nextSpan().name("getUserById").start();

        try (Scope scope = tracer.withSpan(span)) {
            span.tag("user.id", id.toString());

            User user = userRepository.findById(id);
            span.tag("user.found", user != null);

            return user;
        } finally {
            span.end();
        }
    }
}
```

## OpenTelemetry

### Настройка OpenTelemetry
```java
// build.gradle
dependencies {
    implementation 'io.opentelemetry:opentelemetry-api:1.15.0'
    implementation 'io.opentelemetry:opentelemetry-sdk:1.15.0'
    implementation 'io.opentelemetry:opentelemetry-exporter-jaeger:1.15.0'
    implementation 'io.opentelemetry:opentelemetry-exporter-zipkin:1.15.0'
}

@Configuration
public class OpenTelemetryConfig {

    @Bean
    public OpenTelemetry openTelemetry() {
        Resource resource = Resource.getDefault()
            .merge(Resource.builder()
                .put("service.name", "my-service")
                .put("service.version", "1.0.0")
                .build());

        return OpenTelemetrySdk.builder()
            .setTracerProvider(
                SdkTracerProvider.builder()
                    .addSpanProcessor(BatchSpanProcessor.builder(
                        JaegerGrpcSpanExporter.builder()
                            .setEndpoint("http://localhost:14250")
                            .build()
                    ).build())
                    .setResource(resource)
                    .build()
            )
            .build();
    }

    @Bean
    public Tracer tracer(OpenTelemetry openTelemetry) {
        return openTelemetry.getTracer("my-service", "1.0.0");
    }
}
```

### Создание spans с OpenTelemetry
```java
@Service
public class OrderService {

    @Autowired
    private Tracer tracer;

    public Order processOrder(OrderRequest request) {
        Span span = tracer.spanBuilder("processOrder")
            .setAttribute("order.id", request.getId())
            .setAttribute("order.amount", request.getAmount())
            .startSpan();

        try (Scope scope = span.makeCurrent()) {
            // Добавление событий
            span.addEvent("Starting order validation");

            validateOrder(request);

            span.addEvent("Order validation completed");

            // Создание дочернего span
            Span paymentSpan = tracer.spanBuilder("processPayment")
                .setAttribute("payment.method", request.getPaymentMethod())
                .startSpan();

            try (Scope paymentScope = paymentSpan.makeCurrent()) {
                processPayment(request);
                paymentSpan.addEvent("Payment processed successfully");
            } finally {
                paymentSpan.end();
            }

            Order order = createOrder(request);
            span.setAttribute("order.status", "completed");

            return order;

        } catch (Exception e) {
            span.setStatus(StatusCode.ERROR, e.getMessage());
            span.recordException(e);
            throw e;
        } finally {
            span.end();
        }
    }

    private void validateOrder(OrderRequest request) {
        // Бизнес-логика валидации
        if (request.getAmount() <= 0) {
            throw new IllegalArgumentException("Invalid order amount");
        }
    }

    private void processPayment(OrderRequest request) {
        // Имитация обработки платежа
        try {
            Thread.sleep(100); // Имитация задержки
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    private Order createOrder(OrderRequest request) {
        return new Order(request.getId(), "COMPLETED", LocalDateTime.now());
    }
}
```

### Распространение контекста
```java
@Component
public class TraceContextPropagation {

    @Autowired
    private Tracer tracer;

    public <T> T executeWithContext(String operationName, Supplier<T> operation) {
        Span span = tracer.spanBuilder(operationName).startSpan();

        try (Scope scope = span.makeCurrent()) {
            return operation.get();
        } finally {
            span.end();
        }
    }

    public void executeAsyncWithContext(String operationName, Runnable operation) {
        Span span = tracer.spanBuilder(operationName).startSpan();
        SpanContext context = span.getSpanContext();

        // Выполнение в другом потоке с сохранением контекста
        CompletableFuture.runAsync(() -> {
            try (Scope scope = Context.current().with(span).makeCurrent()) {
                operation.run();
            } finally {
                span.end();
            }
        });
    }
}

// HTTP фильтр для распространения контекста
@Component
public class TraceFilter implements Filter {

    @Autowired
    private Tracer tracer;

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest httpRequest = (HttpServletRequest) request;
        SpanContext extractedContext = extractContext(httpRequest);

        Span span = tracer.spanBuilder("http-request")
            .setParent(Context.current().with(Span.fromContext(extractedContext)))
            .setAttribute("http.method", httpRequest.getMethod())
            .setAttribute("http.url", httpRequest.getRequestURI())
            .startSpan();

        try (Scope scope = span.makeCurrent()) {
            chain.doFilter(request, response);
        } finally {
            span.end();
        }
    }

    private SpanContext extractContext(HttpServletRequest request) {
        TextMapGetter<HttpServletRequest> getter = new TextMapGetter<HttpServletRequest>() {
            @Override
            public Iterable<String> keys(HttpServletRequest carrier) {
                return () -> Collections.enumeration(carrier.getHeaderNames()).asIterator();
            }

            @Override
            public String get(HttpServletRequest carrier, String key) {
                return carrier.getHeader(key);
            }
        };

        return GlobalPropagators.getTextMapPropagator()
            .extract(Context.current(), request, getter);
    }
}
```

## Baggage (Контекстные данные)

### Использование Baggage
```java
@Service
public class ContextService {

    @Autowired
    private Tracer tracer;

    public void processWithContext(String userId, String sessionId) {
        // Создание baggage для передачи контекстных данных
        Baggage baggage = Baggage.builder()
            .put("user.id", userId)
            .put("session.id", sessionId)
            .put("request.timestamp", String.valueOf(System.currentTimeMillis()))
            .build();

        Span span = tracer.spanBuilder("processWithContext")
            .setAllAttributes(baggage.asMap())
            .startSpan();

        try (Scope scope = span.makeCurrent()) {
            // Все дочерние spans унаследуют baggage
            step1();
            step2();
            step3();
        } finally {
            span.end();
        }
    }

    private void step1() {
        Span span = tracer.spanBuilder("step1").startSpan();
        try (Scope scope = span.makeCurrent()) {
            Baggage baggage = Baggage.fromContext(Context.current());
            String userId = baggage.getEntryValue("user.id");
            span.setAttribute("user.id", userId);
            // Выполнение шага 1
        } finally {
            span.end();
        }
    }

    private void step2() {
        Span span = tracer.spanBuilder("step2").startSpan();
        try (Scope scope = span.makeCurrent()) {
            Baggage baggage = Baggage.fromContext(Context.current());
            String sessionId = baggage.getEntryValue("session.id");
            span.setAttribute("session.id", sessionId);
            // Выполнение шага 2
        } finally {
            span.end();
        }
    }

    private void step3() {
        Span span = tracer.spanBuilder("step3").startSpan();
        try (Scope scope = span.makeCurrent()) {
            // Доступ к baggage в любом месте
            Baggage baggage = Baggage.fromContext(Context.current());
            String timestamp = baggage.getEntryValue("request.timestamp");
            span.setAttribute("timestamp", timestamp);
            // Выполнение шага 3
        } finally {
            span.end();
        }
    }
}
```

## Сэмплирование

### Настройка сэмплирования
```java
@Configuration
public class SamplingConfig {

    @Bean
    public Sampler traceIdRatioBasedSampler() {
        // Сэмплирование 10% всех трейсов
        return Sampler.traceIdRatioBased(0.1);
    }

    @Bean
    public Sampler customSampler() {
        return new Sampler() {
            @Override
            public SamplingResult shouldSample(SpanContext parentContext,
                                            String traceId,
                                            String name,
                                            Kind kind,
                                            Attributes attributes,
                                            List<Link> parentLinks) {

                // Кастомная логика сэмплирования
                String serviceName = attributes.get(AttributeKey.stringKey("service.name"));
                if ("critical-service".equals(serviceName)) {
                    return SamplingResult.recordAndSample();
                }

                // Случайное сэмплирование для остальных
                return Sampler.traceIdRatioBased(0.01)
                    .shouldSample(parentContext, traceId, name, kind, attributes, parentLinks);
            }

            @Override
            public String getDescription() {
                return "CustomSampler";
            }
        };
    }

    @Bean
    public Sampler compositeSampler() {
        return Sampler.parentBased(
            Sampler.traceIdRatioBased(0.1)
        ).and(Sampler.alwaysOnForOperations(Set.of("critical-operation")));
    }
}

// Всегда сэмплировать определенные операции
class AlwaysOnForOperationsSampler implements Sampler {
    private final Set<String> operations;

    public AlwaysOnForOperationsSampler(Set<String> operations) {
        this.operations = operations;
    }

    @Override
    public SamplingResult shouldSample(SpanContext parentContext, String traceId,
                                     String name, Kind kind, Attributes attributes,
                                     List<Link> parentLinks) {
        if (operations.contains(name)) {
            return SamplingResult.recordAndSample();
        }
        return SamplingResult.drop();
    }

    @Override
    public String getDescription() {
        return "AlwaysOnForOperationsSampler";
    }
}
```

## Интеграция с логированием

### MDC (Mapped Diagnostic Context)
```java
@Component
public class TraceLoggingEnhancer {

    @Autowired
    private Tracer tracer;

    @PostConstruct
    public void setupLogging() {
        // Настройка MDC для логирования
        ThreadContext.put("traceId", getCurrentTraceId());
        ThreadContext.put("spanId", getCurrentSpanId());
    }

    @Scheduled(fixedRate = 1000)
    public void updateLoggingContext() {
        // Обновление MDC в фоновом потоке
        Span currentSpan = tracer.currentSpan();
        if (currentSpan != null) {
            ThreadContext.put("traceId", currentSpan.getSpanContext().getTraceId());
            ThreadContext.put("spanId", currentSpan.getSpanContext().getSpanId());
        }
    }

    private String getCurrentTraceId() {
        Span currentSpan = tracer.currentSpan();
        return currentSpan != null ? currentSpan.getSpanContext().getTraceId() : "no-trace";
    }

    private String getCurrentSpanId() {
        Span currentSpan = tracer.currentSpan();
        return currentSpan != null ? currentSpan.getSpanContext().getSpanId() : "no-span";
    }
}

// logback.xml с MDC
<configuration>
    <appender name="STDOUT" class="ch.qos.logback.core.ConsoleAppender">
        <encoder>
            <pattern>%d{HH:mm:ss.SSS} [%thread] [traceId=%X{traceId:-}] [spanId=%X{spanId:-}] %-5level %logger{36} - %msg%n</pattern>
        </encoder>
    </appender>

    <root level="INFO">
        <appender-ref ref="STDOUT"/>
    </root>
</configuration>
```

### Структурированное логирование
```java
@Service
public class StructuredLogger {

    private static final Logger logger = LoggerFactory.getLogger(StructuredLogger.class);

    @Autowired
    private Tracer tracer;

    public void logOperation(String operation, Object... params) {
        Span currentSpan = tracer.currentSpan();
        if (currentSpan != null) {
            // Добавление информации о трассировке в логи
            MDC.put("traceId", currentSpan.getSpanContext().getTraceId());
            MDC.put("spanId", currentSpan.getSpanContext().getSpanId());
        }

        try {
            logger.info("Operation {} executed with parameters: {}", operation, params);

            // Логирование в span
            currentSpan.addEvent("Operation executed", Attributes.builder()
                .put("operation.name", operation)
                .put("operation.params.count", params.length)
                .build());

        } finally {
            MDC.clear();
        }
    }

    public void logError(String operation, Exception error) {
        Span currentSpan = tracer.currentSpan();
        if (currentSpan != null) {
            currentSpan.setStatus(StatusCode.ERROR, error.getMessage());
            currentSpan.recordException(error);
        }

        logger.error("Operation {} failed", operation, error);
    }
}
```

## Мониторинг и алертинг

### Метрики на основе трассировки
```java
@Service
public class TracingMetricsCollector {

    @Autowired
    private MeterRegistry meterRegistry;

    @Autowired
    private Tracer tracer;

    private final Counter httpRequestsTotal;
    private final Timer httpRequestDuration;
    private final Counter errorsTotal;

    public TracingMetricsCollector(MeterRegistry meterRegistry) {
        this.httpRequestsTotal = Counter.builder("http_requests_total")
            .description("Total number of HTTP requests")
            .register(meterRegistry);

        this.httpRequestDuration = Timer.builder("http_request_duration_seconds")
            .description("HTTP request duration")
            .register(meterRegistry);

        this.errorsTotal = Counter.builder("errors_total")
            .description("Total number of errors")
            .register(meterRegistry);
    }

    public void recordHttpRequest(String method, String path, int statusCode, long durationMs) {
        httpRequestsTotal.increment();

        httpRequestDuration.record(Duration.ofMillis(durationMs),
            Tags.of("method", method, "path", path, "status", String.valueOf(statusCode)));

        // Добавление в текущий span
        Span currentSpan = tracer.currentSpan();
        if (currentSpan != null) {
            currentSpan.setAttribute("http.method", method);
            currentSpan.setAttribute("http.path", path);
            currentSpan.setAttribute("http.status_code", statusCode);
            currentSpan.setAttribute("http.duration_ms", durationMs);
        }
    }

    public void recordError(String errorType, String message) {
        errorsTotal.increment();

        Span currentSpan = tracer.currentSpan();
        if (currentSpan != null) {
            currentSpan.setStatus(StatusCode.ERROR, message);
            currentSpan.setAttribute("error.type", errorType);
        }
    }
}
```

## Лучшие практики

### Архитектурные рекомендации
```java
// 1. Создание spans на границах сервисов
@RestController
public class ApiGatewayController {

    @Autowired
    private Tracer tracer;

    @Autowired
    private UserServiceClient userService;

    @Autowired
    private OrderServiceClient orderService;

    @GetMapping("/user/{userId}/orders")
    public UserOrders getUserOrders(@PathVariable Long userId) {
        Span span = tracer.spanBuilder("getUserOrders")
            .setAttribute("user.id", userId)
            .startSpan();

        try (Scope scope = span.makeCurrent()) {
            // Параллельные вызовы с дочерними spans
            CompletableFuture<User> userFuture = CompletableFuture.supplyAsync(() ->
                withChildSpan("getUser", () -> userService.getUser(userId)));

            CompletableFuture<List<Order>> ordersFuture = CompletableFuture.supplyAsync(() ->
                withChildSpan("getUserOrders", () -> orderService.getUserOrders(userId)));

            User user = userFuture.join();
            List<Order> orders = ordersFuture.join();

            span.setAttribute("orders.count", orders.size());
            return new UserOrders(user, orders);

        } catch (Exception e) {
            span.recordException(e);
            span.setStatus(StatusCode.ERROR, e.getMessage());
            throw e;
        } finally {
            span.end();
        }
    }

    private <T> T withChildSpan(String operationName, Supplier<T> operation) {
        Span childSpan = tracer.spanBuilder(operationName).startSpan();
        try (Scope scope = childSpan.makeCurrent()) {
            return operation.get();
        } finally {
            childSpan.end();
        }
    }
}

// 2. Асинхронная обработка с сохранением контекста
@Service
public class AsyncProcessor {

    @Autowired
    private Tracer tracer;

    @Async
    public CompletableFuture<Void> processAsync(Data data) {
        // Контекст трассировки сохраняется в асинхронных операциях
        Span span = tracer.spanBuilder("processAsync")
            .setAttribute("data.id", data.getId())
            .startSpan();

        return CompletableFuture.supplyAsync(() -> {
            try (Scope scope = span.makeCurrent()) {
                // Долгая обработка
                heavyProcessing(data);
                span.addEvent("Processing completed");
                return null;
            } finally {
                span.end();
            }
        });
    }
}

// 3. Интеграция с очередями сообщений
@Service
public class MessageProcessor {

    @Autowired
    private Tracer tracer;

    @RabbitListener(queues = "order-queue")
    public void processOrderMessage(OrderMessage message) {
        // Извлечение контекста из заголовков сообщения
        SpanContext spanContext = extractContextFromMessageHeaders(message.getHeaders());

        Span span = tracer.spanBuilder("processOrderMessage")
            .setParent(Context.current().with(Span.fromContext(spanContext)))
            .setAttribute("order.id", message.getOrderId())
            .startSpan();

        try (Scope scope = span.makeCurrent()) {
            processOrder(message);
            span.addEvent("Order processed successfully");
        } catch (Exception e) {
            span.recordException(e);
            span.setStatus(StatusCode.ERROR, e.getMessage());
            throw e;
        } finally {
            span.end();
        }
    }

    private SpanContext extractContextFromMessageHeaders(Map<String, Object> headers) {
        // Извлечение trace context из headers сообщения
        return GlobalPropagators.getTextMapPropagator()
            .extract(Context.current(), headers,
                (carrier, key) -> carrier.get(key));
    }
}
```

### Производительность
```java
@Configuration
public class TracingPerformanceConfig {

    @Bean
    public Sampler productionSampler() {
        // Адаптивное сэмплирование в зависимости от нагрузки
        return Sampler.parentBased(
            new AdaptiveSampler(0.01, 0.1, 1000) // 1% базовое, до 10% при высокой нагрузке
        );
    }

    @Bean
    public BatchSpanProcessor efficientSpanProcessor() {
        return BatchSpanProcessor.builder(
            ZipkinSpanExporter.builder()
                .setEndpoint("http://zipkin:9411/api/v2/spans")
                .build()
        )
        .setMaxExportBatchSize(512)
        .setMaxQueueSize(2048)
        .setExporterTimeout(Duration.ofSeconds(30))
        .setScheduleDelay(Duration.ofSeconds(5))
        .build();
    }

    @Bean
    public Tracer efficientTracer(OpenTelemetry openTelemetry) {
        return openTelemetry.getTracer("my-service", "1.0.0",
            InstrumentationLibraryInfo.create("my-service", "1.0.0"));
    }
}

// Адаптивное сэмплирование
class AdaptiveSampler implements Sampler {
    private final double minSampleRate;
    private final double maxSampleRate;
    private final int threshold;

    public AdaptiveSampler(double minSampleRate, double maxSampleRate, int threshold) {
        this.minSampleRate = minSampleRate;
        this.maxSampleRate = maxSampleRate;
        this.threshold = threshold;
    }

    @Override
    public SamplingResult shouldSample(SpanContext parentContext, String traceId,
                                     String name, Kind kind, Attributes attributes,
                                     List<Link> parentLinks) {

        // Простая логика адаптивного сэмплирования
        double currentLoad = getCurrentSystemLoad();
        double sampleRate = currentLoad > threshold ? maxSampleRate : minSampleRate;

        return Sampler.traceIdRatioBased(sampleRate)
            .shouldSample(parentContext, traceId, name, kind, attributes, parentLinks);
    }

    private double getCurrentSystemLoad() {
        // Получение текущей нагрузки системы
        return 0.5; // Заглушка
    }

    @Override
    public String getDescription() {
        return "AdaptiveSampler";
    }
}
```

## Устранение неполадок

### Распространенные проблемы
```java
object TracingTroubleshooting {

  // Проблема: Контекст трассировки теряется в асинхронных операциях
  // Решение: Явная передача контекста
  val asyncOperation = (data: Data) => {
    val currentSpan = tracer.currentSpan()
    val context = Context.current().with(currentSpan)

    Future {
      context.makeCurrent().use { _ =>
        val childSpan = tracer.spanBuilder("async-operation").startSpan()
        try {
          processData(data)
        } finally {
          childSpan.end()
        }
      }
    }
  }

  // Проблема: Слишком много spans создается
  // Решение: Использовать сэмплирование и группировку
  val throttledOperation = (operation: => Unit) => {
    if (shouldSample()) {
      val span = tracer.spanBuilder("throttled-operation").startSpan()
      try {
        operation
      } finally {
        span.end()
      }
    } else {
      operation
    }
  }

  // Проблема: Перегрузка хранилища трассировок
  // Решение: Настройка retention policies и сэмплирования
  val efficientTracing = Tracing.newBuilder()
    .localServiceName("my-service")
    .sampler(Sampler.traceIdRatioBased(0.01)) // Только 1% трейсов
    .addSpanHandler(BatchSpanProcessor.builder(exporter)
      .setMaxExportBatchSize(100)
      .setMaxQueueSize(1000)
      .setScheduleDelay(Duration.ofSeconds(10))
      .build())
    .build()

  // Проблема: Конфликты с другими instrumentation
  // Решение: Проверка compatibility и правильная конфигурация
  val compatibleTracing = OpenTelemetrySdk.builder()
    .setTracerProvider(SdkTracerProvider.builder()
      .addInstrumentationScopeNameFilter(scope -> !scope.startsWith("conflict"))
      .build())
    .build()
}
```

## Дата последнего обновления
22 января 2026 г.

## Полезные ссылки
- [OpenTelemetry Documentation](https://opentelemetry.io/docs/)
- [Jaeger Documentation](https://www.jaegertracing.io/docs/)
- [Zipkin Documentation](https://zipkin.io/)
- [Spring Cloud Sleuth](https://spring.io/projects/spring-cloud-sleuth)

## См. также
- [Prometheus](monitoring/prometheus.md) - Метрики и мониторинг
- [Grafana](monitoring/grafana.md) - Визуализация метрик
- [Micrometer](java-micrometer.md) - Метрики для JVM
