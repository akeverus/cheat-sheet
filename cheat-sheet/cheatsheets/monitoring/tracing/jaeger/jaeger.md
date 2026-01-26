# Jaeger для Java

Комплексное руководство по использованию Jaeger для distributed tracing в Java-приложениях: настройка, интеграция с Spring Boot, анализ производительности и отладка распределенных систем.

**Дата последнего обновления:** 2026-01-21

## Полезные ссылки

### Официальная документация
- [Jaeger Documentation](https://www.jaegertracing.io/docs/) - Основная документация
- [Jaeger Architecture](https://www.jaegertracing.io/docs/architecture/) - Архитектура
- [Jaeger Client Libraries](https://www.jaegertracing.io/docs/client-libraries/) - Клиентские библиотеки

### Java интеграции
- [OpenTelemetry Java](https://opentelemetry.io/docs/java/) - Стандарт для tracing
- [Spring Cloud Sleuth](https://spring.io/projects/spring-cloud-sleuth) - Spring интеграция
- [Micrometer Tracing](https://micrometer.io/docs/tracing) - Tracing abstraction

### Best practices
- [Distributed Tracing Guide](https://microservices.io/patterns/observability/distributed-tracing.html)
- [OpenTelemetry Best Practices](https://opentelemetry.io/docs/concepts/observability-principles/)
- [Tracing in Microservices](https://www.oreilly.com/library/view/microservices-up-and/9781492075441/)

### См. также
- `monitoring/observability.md` - Основы Observability
- `monitoring/grafana.md` - Визуализация метрик
- `monitoring/distributed-tracing.md` - Общие концепции

## Содержание

- [Введение в Jaeger](#введение-в-jaeger)
- [Архитектура Jaeger](#архитектура-jaeger)
- [Установка и настройка](#установка-и-настройка)
- [OpenTelemetry интеграция](#opentelemetry-интеграция)
- [Spring Boot интеграция](#spring-boot-интеграция)
- [Micrometer Tracing](#micrometer-tracing)
- [Custom instrumentation](#custom-instrumentation)
- [Context propagation](#context-propagation)
- [Sampling strategies](#sampling-strategies)
- [Storage backends](#storage-backends)
- [Query и анализ](#query-и-анализ)
- [Performance monitoring](#performance-monitoring)
- [Troubleshooting](#troubleshooting)
- [Best practices](#best-practices)
- [Заключение](#заключение)

## Введение в Jaeger

**Jaeger** — это open-source система для distributed tracing, которая помогает отслеживать запросы через сложные распределенные системы. Jaeger собирает, хранит и визуализирует traces — последовательности связанных операций в микросервисной архитектуре.

### Почему Jaeger?

Jaeger предоставляет комплексные возможности для tracing:

1. **Distributed tracing** — отслеживание запросов через сервисы
2. **Performance analysis** — анализ latency и bottlenecks
3. **Root cause analysis** — быстрая диагностика проблем
4. **Service dependencies** — визуализация зависимостей
5. **Sampling** — эффективный сбор данных
6. **Multiple storage** — различные backends для хранения
7. **Open standards** — поддержка OpenTelemetry
8. **Real-time monitoring** — live tracing данных

### Основные компоненты

#### Jaeger Client
- **Instrumentation** — добавление tracing кода
- **Span creation** — создание и управление spans
- **Context propagation** — передача контекста между сервисами
- **Sampling** — выборка traces для анализа

#### Jaeger Agent
- **Data collection** — сбор traces от клиентов
- **Buffering** — буферизация данных
- **Batch sending** — пакетная отправка в collector
- **Load balancing** — распределение нагрузки

#### Jaeger Collector
- **Data processing** — обработка и валидация traces
- **Storage** — сохранение в backend
- **Indexing** — индексация для быстрого поиска
- **Aggregation** — агрегация данных

#### Storage Backends
- **Cassandra** — распределенное хранение
- **Elasticsearch** — поиск и аналитика
- **Memory** — in-memory storage для development
- **Badger** — embedded key-value store

## Архитектура Jaeger

### Hot path (production)

```
┌─────────────────────────────────────────────────────────┐
│                     Application                         │
│  ┌─────────────────────────────────────────────────┐    │
│  │ 1. Span Creation                               │    │
│  │    - @Traced methods                            │    │
│  │    - HTTP requests                              │    │
│  │    - Database calls                             │    │
│  └─────────────────────────────────────────────────┘    │
└─────────────────────────────────────────────────────────┘
                          │
                          ▼
┌─────────────────────────────────────────────────────────┐
│                   Jaeger Agent                          │
│  ┌─────────────────────────────────────────────────┐    │
│  │ 2. Span Collection                             │    │
│  │    - UDP receiver                               │    │
│  │    - Batch processing                           │    │
│  │    - Compression                                │    │
│  └─────────────────────────────────────────────────┘    │
└─────────────────────────────────────────────────────────┘
                          │
                          ▼
┌─────────────────────────────────────────────────────────┐
│                 Jaeger Collector                        │
│  ┌─────────────────────────────────────────────────┐    │
│  │ 3. Span Processing                             │    │
│  │    - Validation                                 │    │
│  │    - Transformation                             │    │
│  │    - Storage indexing                           │    │
│  └─────────────────────────────────────────────────┘    │
└─────────────────────────────────────────────────────────┘
                          │
                          ▼
┌─────────────────────────────────────────────────────────┐
│                 Storage Backend                         │
│  ┌─────────────────────────────────────────────────┐    │
│  │ 4. Persistence                                  │    │
│  │    - Cassandra                                   │    │
│  │    - Elasticsearch                               │    │
│  │    - Memory                                       │    │
│  └─────────────────────────────────────────────────┘    │
└─────────────────────────────────────────────────────────┘
```

### Query path (UI)

```
┌─────────────────────────────────────────────────────────┐
│                     Jaeger UI                           │
│  ┌─────────────────────────────────────────────────┐    │
│  │ Search & Filter                               │    │
│  │ - Service selection                            │    │
│  │ - Time range                                    │    │
│  │ - Tags filtering                               │    │
│  └─────────────────────────────────────────────────┘    │
└─────────────────────────────────────────────────────────┘
                          │
                          ▼
┌─────────────────────────────────────────────────────────┐
│                 Query Service                           │
│  ┌─────────────────────────────────────────────────┐    │
│  │ Trace Retrieval                               │    │
│  │ - Archive storage                              │    │
│  │ - Real-time queries                            │    │
│  │ - Aggregation                                  │    │
│  └─────────────────────────────────────────────────┘    │
└─────────────────────────────────────────────────────────┘
                          │
                          ▼
┌─────────────────────────────────────────────────────────┐
│                 Storage Backend                         │
│  ┌─────────────────────────────────────────────────┐    │
│  │ Data Access                                     │    │
│  │ - Trace storage                                 │    │
│  │ - Index lookup                                  │    │
│  │ - Aggregation queries                           │    │
│  └─────────────────────────────────────────────────┘    │
└─────────────────────────────────────────────────────────┘
```

### Sampling strategies

#### Head sampling
```
Request Flow: Client → Service A → Service B → Service C

Sampling Decision:
├── Sampled (10%) → Full trace collected
├── Not sampled (90%) → No trace data
└── Consistent across all services
```

#### Tail sampling
```
Request Flow: Client → Service A → Service B → Service C
                                      ↓
                              All spans buffered
                                      ↓
                         Sampling decision at end
                    ├── Error detected → Keep full trace
                    ├── High latency → Keep full trace
                    └── Normal → Discard
```

## Установка и настройка

### Установка Jaeger

**Jaeger** может быть развернут различными способами: all-in-one для разработки, распределенное развертывание для production. Выбор архитектуры зависит от требований к масштабируемости и надежности.

#### Docker (all-in-one)

**All-in-one** образ объединяет все компоненты Jaeger в одном контейнере, что идеально подходит для разработки, тестирования и небольших production сред.

```bash
# Базовый запуск для development с debug логированием
docker run -d \
  --name jaeger \
  # UI порт для веб-интерфейса
  -p 16686:16686 \
  # Collector порт для приема traces (HTTP)
  -p 14268:14268 \
  # gRPC порт для приема traces
  -p 14250:14250 \
  # Используем официальный all-in-one образ
  jaegertracing/all-in-one:latest \
  # Детальное логирование для debugging
  --log-level=debug

# Проверка запуска
docker ps | grep jaeger
docker logs jaeger
```

**Запуск с persistent storage:**
```bash
# Jaeger с хранением данных на диске
docker run -d \
  --name jaeger \
  -p 16686:16686 \
  -p 14268:14268 \
  -p 14250:14250 \
  # Volume для хранения traces
  -v jaeger-data:/tmp \
  jaegertracing/all-in-one:latest \
  # Информационное логирование
  --log-level=info \
  # Максимальное количество traces в памяти (для ограничения потребления)
  --memory.max-traces=100000 \
  # Максимальный размер span в памяти
  --memory.max-traces=50000 \
  # Время жизни traces в памяти (24 часа)
  --span-storage-ttl=24h
```

**Docker Compose для полной инфраструктуры:**
```yaml
version: '3.8'
services:
  # Jaeger all-in-one для сбора и анализа traces
  jaeger:
    image: jaegertracing/all-in-one:latest
    container_name: jaeger
    ports:
      # Веб-интерфейс для просмотра traces
      - "16686:16686"
      # HTTP API для приема traces
      - "14268:14268"
      # gRPC API для приема traces (OpenTelemetry)
      - "14250:14250"
    environment:
      # Включение OTLP receiver для OpenTelemetry
      - COLLECTOR_OTLP_ENABLED=true
      # Тип хранилища (memory для development)
      - SPAN_STORAGE_TYPE=memory
      # Максимальное количество traces
      - MEMORY_MAX_TRACES=100000
      # Время жизни traces (24 часа)
      - SPAN_STORAGE_TTL=24h
      # Уровень логирования
      - LOG_LEVEL=info
    volumes:
      # Persistent storage для traces
      - jaeger_data:/tmp
    networks:
      - observability
    # Health check для проверки готовности
    healthcheck:
      test: ["CMD-SHELL", "wget --no-verbose --tries=1 --spider http://localhost:14269/ || exit 1"]
      interval: 30s
      timeout: 10s
      retries: 3
      start_period: 40s
    restart: unless-stopped

  # Prometheus для сбора метрик Jaeger
  prometheus:
    image: prom/prometheus:latest
    container_name: prometheus
    ports:
      - "9090:9090"
    volumes:
      - ./prometheus.yml:/etc/prometheus/prometheus.yml
      - prometheus_data:/prometheus
    networks:
      - observability
    command:
      - '--config.file=/etc/prometheus/prometheus.yml'
      - '--storage.tsdb.path=/prometheus'
      - '--web.console.libraries=/etc/prometheus/console_libraries'
      - '--web.console.templates=/etc/prometheus/consoles'
      - '--storage.tsdb.retention.time=200h'
      - '--web.enable-lifecycle'

volumes:
  jaeger_data:
    driver: local
  prometheus_data:
    driver: local

networks:
  observability:
    driver: bridge
```

#### Kubernetes развертывание

**Kubernetes** обеспечивает надежное и масштабируемое развертывание Jaeger в production средах с автоматическим управлением ресурсами и self-healing.

```yaml
# jaeger-all-in-one-deployment.yaml
apiVersion: apps/v1
kind: Deployment
metadata:
  name: jaeger-all-in-one
  namespace: observability
  labels:
    app: jaeger
    component: tracing
spec:
  # Одна реплика для development/production
  replicas: 1
  strategy:
    type: Recreate  # Для all-in-one лучше Recreate чем RollingUpdate
  selector:
    matchLabels:
      app: jaeger
  template:
    metadata:
      labels:
        app: jaeger
        component: tracing
    spec:
      # Security context для ограничения привилегий
      securityContext:
        runAsUser: 1000
        runAsGroup: 1000
        fsGroup: 1000

      containers:
      - name: jaeger
        image: jaegertracing/all-in-one:latest
        imagePullPolicy: IfNotPresent

        ports:
        - name: ui
          containerPort: 16686
          protocol: TCP
        - name: collector-http
          containerPort: 14268
          protocol: TCP
        - name: collector-grpc
          containerPort: 14250
          protocol: TCP

        # Environment variables
        env:
        - name: COLLECTOR_OTLP_ENABLED
          value: "true"
        - name: SPAN_STORAGE_TYPE
          value: "memory"  # Для production использовать cassandra или elasticsearch
        - name: MEMORY_MAX_TRACES
          value: "100000"
        - name: SPAN_STORAGE_TTL
          value: "24h"
        - name: LOG_LEVEL
          value: "info"

        # Resource limits
        resources:
          requests:
            memory: "256Mi"
            cpu: "100m"
          limits:
            memory: "512Mi"
            cpu: "500m"

        # Liveness probe
        livenessProbe:
          httpGet:
            path: /
            port: 16686
          initialDelaySeconds: 60
          periodSeconds: 30
          timeoutSeconds: 10
          failureThreshold: 3

        # Readiness probe
        readinessProbe:
          httpGet:
            path: /
            port: 16686
          initialDelaySeconds: 30
          periodSeconds: 10
          timeoutSeconds: 5
          failureThreshold: 3

        # Volume mounts
        volumeMounts:
        - name: jaeger-storage
          mountPath: /tmp

      volumes:
      - name: jaeger-storage
        emptyDir: {}  # Для production использовать persistent volume

---
# Service для доступа к Jaeger
apiVersion: v1
kind: Service
metadata:
  name: jaeger
  namespace: observability
  labels:
    app: jaeger
spec:
  type: ClusterIP
  ports:
  - name: ui
    port: 16686
    targetPort: 16686
    protocol: TCP
  - name: collector-http
    port: 14268
    targetPort: 14268
    protocol: TCP
  - name: collector-grpc
    port: 14250
    targetPort: 14250
    protocol: TCP
  selector:
    app: jaeger

---
# Ingress для внешнего доступа
apiVersion: networking.k8s.io/v1
kind: Ingress
metadata:
  name: jaeger-ingress
  namespace: observability
  annotations:
    kubernetes.io/ingress.class: nginx
    nginx.ingress.kubernetes.io/ssl-redirect: "true"
    cert-manager.io/cluster-issuer: "letsencrypt-prod"
spec:
  tls:
  - hosts:
    - jaeger.example.com
    secretName: jaeger-tls
  rules:
  - host: jaeger.example.com
    http:
      paths:
      - path: /
        pathType: Prefix
        backend:
          service:
            name: jaeger
            port:
              number: 16686
```

#### Persistent Volume для хранения traces
```yaml
apiVersion: v1
kind: PersistentVolumeClaim
metadata:
  name: jaeger-pvc
  namespace: observability
spec:
  accessModes:
    - ReadWriteOnce
  resources:
    requests:
      storage: 50Gi
  storageClassName: fast-ssd
```

#### Production развертывание (отдельные компоненты)

**В production рекомендуется разделять компоненты для лучшей масштабируемости и надежности:**

```yaml
# jaeger-collector-deployment.yaml
apiVersion: apps/v1
kind: Deployment
metadata:
  name: jaeger-collector
  namespace: observability
spec:
  replicas: 2  # Несколько реплик для высокой доступности
  selector:
    matchLabels:
      app: jaeger-collector
  template:
    metadata:
      labels:
        app: jaeger-collector
    spec:
      containers:
      - name: collector
        image: jaegertracing/jaeger-collector:latest
        ports:
        - containerPort: 14268  # HTTP
        - containerPort: 14250  # gRPC
        env:
        - name: SPAN_STORAGE_TYPE
          value: "cassandra"
        - name: CASSANDRA_SERVERS
          value: "jaeger-cassandra:9042"
        - name: COLLECTOR_OTLP_ENABLED
          value: "true"
        resources:
          requests:
            memory: "128Mi"
            cpu: "100m"
          limits:
            memory: "256Mi"
            cpu: "200m"

---
# jaeger-query-deployment.yaml
apiVersion: apps/v1
kind: Deployment
metadata:
  name: jaeger-query
  namespace: observability
spec:
  replicas: 1
  selector:
    matchLabels:
      app: jaeger-query
  template:
    metadata:
      labels:
        app: jaeger-query
    spec:
      containers:
      - name: query
        image: jaegertracing/jaeger-query:latest
        ports:
        - containerPort: 16686
        env:
        - name: SPAN_STORAGE_TYPE
          value: "cassandra"
        - name: CASSANDRA_SERVERS
          value: "jaeger-cassandra:9042"
        resources:
          requests:
            memory: "64Mi"
            cpu: "50m"
          limits:
            memory: "128Mi"
            cpu: "100m"
```

### Production setup

#### Separate components
```yaml
# Jaeger Collector
apiVersion: apps/v1
kind: Deployment
metadata:
  name: jaeger-collector
spec:
  replicas: 2
  template:
    spec:
      containers:
      - name: collector
        image: jaegertracing/jaeger-collector:latest
        env:
        - name: SPAN_STORAGE_TYPE
          value: "cassandra"
        - name: CASSANDRA_SERVERS
          value: "cassandra:9042"
        ports:
        - containerPort: 14268
        - containerPort: 14250

# Jaeger Query
apiVersion: apps/v1
kind: Deployment
metadata:
  name: jaeger-query
spec:
  replicas: 2
  template:
    spec:
      containers:
      - name: query
        image: jaegertracing/jaeger-query:latest
        env:
        - name: SPAN_STORAGE_TYPE
          value: "cassandra"
        ports:
        - containerPort: 16686
```

### Storage configuration

#### Cassandra backend
```yaml
# Cassandra StatefulSet
apiVersion: apps/v1
kind: StatefulSet
metadata:
  name: cassandra
spec:
  serviceName: cassandra
  replicas: 3
  template:
    spec:
      containers:
      - name: cassandra
        image: cassandra:3.11
        env:
        - name: CASSANDRA_CLUSTER_NAME
          value: "jaeger"
        - name: CASSANDRA_DC
          value: "dc1"
        - name: CASSANDRA_RACK
          value: "rack1"
        ports:
        - containerPort: 9042
        volumeMounts:
        - name: cassandra-data
          mountPath: /var/lib/cassandra
  volumeClaimTemplates:
  - metadata:
      name: cassandra-data
    spec:
      accessModes: ["ReadWriteOnce"]
      resources:
        requests:
          storage: 100Gi
```

#### Elasticsearch backend
```yaml
# Elasticsearch deployment
apiVersion: apps/v1
kind: Deployment
metadata:
  name: elasticsearch
spec:
  replicas: 3
  template:
    spec:
      containers:
      - name: elasticsearch
        image: docker.elastic.co/elasticsearch/elasticsearch:7.10.0
        env:
        - name: discovery.type
          value: single-node
        - name: ES_JAVA_OPTS
          value: "-Xms512m -Xmx512m"
        ports:
        - containerPort: 9200
```

## OpenTelemetry интеграция

### OpenTelemetry Java Agent

#### Auto-instrumentation
```bash
# Запуск приложения с OpenTelemetry agent
java -javaagent:opentelemetry-javaagent.jar \
  -Dotel.service.name=my-service \
  -Dotel.traces.exporter=jaeger \
  -Dotel.exporter.jaeger.endpoint=http://jaeger:14268/api/traces \
  -jar my-application.jar

# С sampling
java -javaagent:opentelemetry-javaagent.jar \
  -Dotel.service.name=my-service \
  -Dotel.traces.sampler=traceidratio \
  -Dotel.traces.sampler.arg=0.1 \
  -Dotel.traces.exporter=jaeger \
  -Dotel.exporter.jaeger.endpoint=http://jaeger:14268/api/traces \
  -jar my-application.jar
```

#### Manual instrumentation
```xml
<dependency>
    <groupId>io.opentelemetry</groupId>
    <artifactId>opentelemetry-api</artifactId>
    <version>1.25.0</version>
</dependency>
<dependency>
    <groupId>io.opentelemetry</groupId>
    <artifactId>opentelemetry-sdk</artifactId>
    <version>1.25.0</version>
</dependency>
<dependency>
    <groupId>io.opentelemetry</groupId>
    <artifactId>opentelemetry-exporter-jaeger</artifactId>
    <version>1.25.0</version>
</dependency>
```

```java
public class OpenTelemetryConfig {

    public static void setupOpenTelemetry() {
        // Создание tracer provider
        Resource resource = Resource.getDefault()
            .merge(Resource.builder()
                .put(ServiceAttributes.SERVICE_NAME, "my-service")
                .put(ServiceAttributes.SERVICE_VERSION, "1.0.0")
                .build());

        // Jaeger exporter
        JaegerGrpcSpanExporter jaegerExporter = JaegerGrpcSpanExporter.builder()
            .setEndpoint("http://jaeger:14268/api/traces")
            .build();

        // Span processor
        SpanProcessor spanProcessor = BatchSpanProcessor.builder(jaegerExporter)
            .setScheduleDelay(Duration.ofMillis(100))
            .setMaxExportBatchSize(512)
            .build();

        // Tracer provider
        SdkTracerProvider tracerProvider = SdkTracerProvider.builder()
            .addSpanProcessor(spanProcessor)
            .setResource(resource)
            .setSampler(Sampler.traceIdRatioBased(0.1)) // 10% sampling
            .build();

        // Установка как глобального
        OpenTelemetrySdk.builder()
            .setTracerProvider(tracerProvider)
            .buildAndRegisterGlobal();

        // Graceful shutdown
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            tracerProvider.shutdown();
        }));
    }
}
```

## Spring Boot интеграция

### Spring Cloud Sleuth

#### Maven зависимости
```xml
<dependency>
    <groupId>org.springframework.cloud</groupId>
    <artifactId>spring-cloud-starter-sleuth</artifactId>
</dependency>
<dependency>
    <groupId>org.springframework.cloud</groupId>
    <artifactId>spring-cloud-sleuth-zipkin</artifactId>
</dependency>
```

#### Application properties
```yaml
spring:
  application:
    name: user-service
  sleuth:
    sampler:
      probability: 0.1  # 10% sampling
    web:
      enabled: true
    messaging:
      enabled: true
  zipkin:
    base-url: http://zipkin:9411/  # Для совместимости
```

#### Custom tracing
```java
@Service
public class UserService {

    private final Tracer tracer;

    @Autowired
    public UserService(Tracer tracer) {
        this.tracer = tracer;
    }

    public User createUser(CreateUserRequest request) {
        Span span = tracer.nextSpan().name("createUser").start();

        try (Tracer.SpanInScope ws = tracer.withSpanInScope(span)) {
            span.tag("user.email", request.getEmail());
            span.tag("operation", "user_creation");

            // Business logic
            User user = new User();
            user.setEmail(request.getEmail());
            user.setName(request.getName());

            // Database operation
            Span dbSpan = tracer.nextSpan().name("saveUser").start();
            try (Tracer.SpanInScope dbScope = tracer.withSpanInScope(dbSpan)) {
                dbSpan.tag("db.operation", "insert");
                dbSpan.tag("db.table", "users");

                user = userRepository.save(user);

            } finally {
                dbSpan.finish();
            }

            span.tag("user.id", user.getId());
            return user;

        } catch (Exception e) {
            span.error(e);
            throw e;
        } finally {
            span.finish();
        }
    }
}
```

### Spring Boot 3 + Micrometer

#### Configuration
```yaml
management:
  tracing:
    enabled: true
    sampling:
      probability: 0.1
  opentelemetry:
    tracing:
      endpoint: http://jaeger:14268/api/traces
```

```java
@Configuration
public class TracingConfig {

    @Bean
    public OpenTelemetry openTelemetry() {
        Resource resource = Resource.getDefault()
            .merge(Resource.builder()
                .put(ServiceAttributes.SERVICE_NAME, "user-service")
                .put(ServiceAttributes.SERVICE_VERSION, "1.0.0")
                .build());

        JaegerGrpcSpanExporter jaegerExporter = JaegerGrpcSpanExporter.builder()
            .setEndpoint("http://jaeger:14268/api/traces")
            .build();

        BatchSpanProcessor spanProcessor = BatchSpanProcessor.builder(jaegerExporter)
            .build();

        SdkTracerProvider tracerProvider = SdkTracerProvider.builder()
            .addSpanProcessor(spanProcessor)
            .setResource(resource)
            .setSampler(Sampler.traceIdRatioBased(0.1))
            .build();

        return OpenTelemetrySdk.builder()
            .setTracerProvider(tracerProvider)
            .build();
    }

    @Bean
    public Tracer tracer(OpenTelemetry openTelemetry) {
        return openTelemetry.getTracer("user-service", "1.0.0");
    }
}
```

## Micrometer Tracing

### Micrometer Tracing API

#### Observation API
```java
@Service
public class PaymentService {

    private final ObservationRegistry observationRegistry;

    @Autowired
    public PaymentService(ObservationRegistry observationRegistry) {
        this.observationRegistry = observationRegistry;
    }

    public PaymentResult processPayment(PaymentRequest request) {
        return Observation.createNotStarted("processPayment", observationRegistry)
            .contextualName("payment-processing")
            .lowCardinalityKeyValue("payment.method", request.getMethod())
            .highCardinalityKeyValue("payment.id", request.getId())
            .observe(() -> {
                // Payment processing logic
                validatePayment(request);
                return executePayment(request);
            });
    }

    private void validatePayment(PaymentRequest request) {
        Observation.createNotStarted("validatePayment", observationRegistry)
            .lowCardinalityKeyValue("validation.type", "amount")
            .observe(() -> {
                if (request.getAmount() <= 0) {
                    throw new ValidationException("Invalid amount");
                }
            });
    }
}
```

#### Timer API
```java
@Service
public class DatabaseService {

    private final Timer queryTimer;

    @Autowired
    public DatabaseService(MeterRegistry registry) {
        this.queryTimer = Timer.builder("db.query.duration")
            .description("Database query duration")
            .tags("service", "database")
            .register(registry);
    }

    public <T> T executeQuery(String sql, RowMapper<T> mapper) {
        return queryTimer.recordCallable(() -> {
            // Database query logic
            return jdbcTemplate.queryForObject(sql, mapper);
        });
    }
}
```

## Custom instrumentation

### Database tracing

#### JDBC instrumentation
```java
public class TracingJdbcTemplate extends JdbcTemplate {

    private final Tracer tracer;

    public TracingJdbcTemplate(Tracer tracer, DataSource dataSource) {
        super(dataSource);
        this.tracer = tracer;
    }

    @Override
    public <T> T queryForObject(String sql, RowMapper<T> rowMapper, Object... args) {
        Span span = tracer.nextSpan().name("jdbc.query").start();

        try (Tracer.SpanInScope scope = tracer.withSpanInScope(span)) {
            span.tag("db.statement", sql);
            span.tag("db.operation", extractOperation(sql));
            span.tag("db.instance", getDatabaseName());

            return super.queryForObject(sql, rowMapper, args);

        } catch (Exception e) {
            span.error(e);
            throw e;
        } finally {
            span.finish();
        }
    }

    private String extractOperation(String sql) {
        String upperSql = sql.toUpperCase().trim();
        if (upperSql.startsWith("SELECT")) return "SELECT";
        if (upperSql.startsWith("INSERT")) return "INSERT";
        if (upperSql.startsWith("UPDATE")) return "UPDATE";
        if (upperSql.startsWith("DELETE")) return "DELETE";
        return "UNKNOWN";
    }
}
```

#### JPA/Hibernate tracing
```java
@Aspect
@Component
public class JpaTracingAspect {

    @Autowired
    private Tracer tracer;

    @Around("execution(* org.springframework.data.jpa.repository.JpaRepository+.*(..))")
    public Object traceJpaOperations(ProceedingJoinPoint joinPoint) throws Throwable {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        String methodName = signature.getMethod().getName();

        Span span = tracer.nextSpan().name("jpa." + methodName).start();

        try (Tracer.SpanInScope scope = tracer.withSpanInScope(span)) {
            span.tag("jpa.method", methodName);
            span.tag("jpa.repository", joinPoint.getTarget().getClass().getSimpleName());

            Object[] args = joinPoint.getArgs();
            if (args.length > 0) {
                span.tag("jpa.entity.id", String.valueOf(args[0]));
            }

            return joinPoint.proceed();

        } catch (Exception e) {
            span.error(e);
            throw e;
        } finally {
            span.finish();
        }
    }
}
```

### HTTP client tracing

#### RestTemplate tracing
```java
@Configuration
public class RestTemplateConfig {

    @Autowired
    private Tracer tracer;

    @Bean
    public RestTemplate restTemplate() {
        RestTemplate restTemplate = new RestTemplate();

        // Добавление interceptor для tracing
        restTemplate.getInterceptors().add(new TracingClientHttpRequestInterceptor(tracer));

        return restTemplate;
    }
}

public class TracingClientHttpRequestInterceptor implements ClientHttpRequestInterceptor {

    private final Tracer tracer;

    public TracingClientHttpRequestInterceptor(Tracer tracer) {
        this.tracer = tracer;
    }

    @Override
    public ClientHttpResponse intercept(HttpRequest request, byte[] body,
                                      ClientHttpRequestExecution execution) throws IOException {

        Span span = tracer.nextSpan().name("http.client").start();

        try (Tracer.SpanInScope scope = tracer.withSpanInScope(span)) {
            span.tag("http.method", request.getMethod().name());
            span.tag("http.url", request.getURI().toString());

            // Inject trace context into headers
            injectTraceContext(request.getHeaders(), span);

            ClientHttpResponse response = execution.execute(request, body);

            span.tag("http.status_code", response.getStatusCode().value());

            return response;

        } catch (Exception e) {
            span.error(e);
            throw e;
        } finally {
            span.finish();
        }
    }

    private void injectTraceContext(HttpHeaders headers, Span span) {
        SpanContext context = span.getSpanContext();
        headers.add("x-trace-id", context.getTraceId());
        headers.add("x-span-id", context.getSpanId());
    }
}
```

#### WebClient tracing
```java
@Configuration
public class WebClientConfig {

    @Autowired
    private Tracer tracer;

    @Bean
    public WebClient webClient() {
        return WebClient.builder()
            .filter(new TracingExchangeFilterFunction(tracer))
            .build();
    }
}

public class TracingExchangeFilterFunction implements ExchangeFilterFunction {

    private final Tracer tracer;

    public TracingExchangeFilterFunction(Tracer tracer) {
        this.tracer = tracer;
    }

    @Override
    public Mono<ClientResponse> filter(ClientRequest request, ExchangeFunction next) {
        Span span = tracer.nextSpan().name("webclient.request").start();

        try (Tracer.SpanInScope scope = tracer.withSpanInScope(span)) {
            span.tag("http.method", request.method().name());
            span.tag("http.url", request.url().toString());

            // Inject trace context
            ClientRequest tracedRequest = ClientRequest.from(request)
                .header("x-trace-id", span.getSpanContext().getTraceId())
                .header("x-span-id", span.getSpanContext().getSpanId())
                .build();

            return next.exchange(tracedRequest)
                .doOnNext(response -> {
                    span.tag("http.status_code", response.statusCode().value());
                })
                .doFinally(signalType -> span.finish());

        } catch (Exception e) {
            span.error(e);
            throw e;
        }
    }
}
```

### Messaging tracing

#### Kafka tracing
```java
@Configuration
public class KafkaTracingConfig {

    @Autowired
    private Tracer tracer;

    @Bean
    public ProducerFactory<String, String> producerFactory() {
        Map<String, Object> configProps = new HashMap<>();
        configProps.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");

        return new DefaultKafkaProducerFactory<>(configProps);
    }

    @Bean
    public KafkaTemplate<String, String> kafkaTemplate() {
        KafkaTemplate<String, String> template = new KafkaTemplate<>(producerFactory());

        // Добавление tracing interceptor
        template.setProducerInterceptor(new TracingProducerInterceptor(tracer));

        return template;
    }
}

public class TracingProducerInterceptor implements ProducerInterceptor<String, String> {

    private final Tracer tracer;

    public TracingProducerInterceptor(Tracer tracer) {
        this.tracer = tracer;
    }

    @Override
    public ProducerRecord<String, String> onSend(ProducerRecord<String, String> record) {
        Span span = tracer.nextSpan().name("kafka.produce").start();

        try (Tracer.SpanInScope scope = tracer.withSpanInScope(span)) {
            span.tag("kafka.topic", record.topic());
            span.tag("kafka.partition", String.valueOf(record.partition()));

            // Inject trace context into headers
            record.headers().add("x-trace-id",
                span.getSpanContext().getTraceId().getBytes(StandardCharsets.UTF_8));
            record.headers().add("x-span-id",
                span.getSpanContext().getSpanId().getBytes(StandardCharsets.UTF_8));

            return record;

        } finally {
            span.finish();
        }
    }

    @Override
    public void onAcknowledgement(RecordMetadata metadata, Exception exception) {
        if (exception != null) {
            Span span = tracer.nextSpan().name("kafka.produce.error").start();
            try (Tracer.SpanInScope scope = tracer.withSpanInScope(span)) {
                span.error(exception);
            } finally {
                span.finish();
            }
        }
    }

    @Override
    public void close() {}
    @Override
    public void configure(Map<String, ?> configs) {}
}
```

## Context propagation

### Baggage propagation

#### Manual baggage
```java
public class BaggageExample {

    private static final Tracer tracer = GlobalOpenTelemetry.getTracer("baggage-example");

    public void processRequest(String userId, String tenantId) {
        Span span = tracer.spanBuilder("processRequest").startSpan();

        try (Scope scope = span.makeCurrent()) {
            // Set baggage
            Baggage.current()
                .toBuilder()
                .put("user.id", userId)
                .put("tenant.id", tenantId)
                .build()
                .makeCurrent();

            span.setAttribute("user.id", userId);
            span.setAttribute("tenant.id", tenantId);

            // Call other services - baggage will be propagated
            callServiceA();
            callServiceB();

        } finally {
            span.end();
        }
    }

    private void callServiceA() {
        Span span = tracer.spanBuilder("serviceA").startSpan();

        try (Scope scope = span.makeCurrent()) {
            // Access baggage in child span
            String userId = Baggage.current().getEntryValue("user.id");
            String tenantId = Baggage.current().getEntryValue("tenant.id");

            span.setAttribute("service.user.id", userId);
            span.setAttribute("service.tenant.id", tenantId);

            // Service A logic
            System.out.println("Service A called for user: " + userId);

        } finally {
            span.end();
        }
    }
}
```

#### Distributed context

#### W3C Trace Context
```java
public class W3CTraceContextExample {

    public static void extractTraceContext(HttpHeaders headers) {
        // Extract trace context from HTTP headers
        String traceParent = headers.getFirst("traceparent");
        String traceState = headers.getFirst("tracestate");

        if (traceParent != null) {
            // Parse W3C trace context
            // Format: 00-TRACE_ID-SPAN_ID-FLAGS
            String[] parts = traceParent.split("-");
            if (parts.length >= 4) {
                String traceId = parts[1];
                String spanId = parts[2];
                String flags = parts[3];

                // Create child span
                SpanContext parentContext = SpanContext.createFromRemoteParent(
                    TraceId.fromLowerBase16(traceId, 0),
                    SpanId.fromLowerBase16(spanId, 0),
                    TraceFlags.getDefault(),
                    TraceState.getDefault()
                );

                // Continue trace
                Span childSpan = tracer.spanBuilder("child-operation")
                    .setParent(Context.root().with(Span.wrap(parentContext)))
                    .startSpan();

                // ... use child span
                childSpan.end();
            }
        }
    }

    public static void injectTraceContext(HttpHeaders headers, Span span) {
        SpanContext context = span.getSpanContext();

        // Inject W3C trace context
        String traceParent = String.format("00-%s-%s-%s",
            context.getTraceId(),
            context.getSpanId(),
            context.getTraceFlags().asByte()
        );

        headers.set("traceparent", traceParent);

        // Optional: trace state
        if (!context.getTraceState().isEmpty()) {
            headers.set("tracestate", context.getTraceState().serialize());
        }
    }
}
```

## Sampling strategies

### Probabilistic sampling

#### Constant sampling
```java
@Configuration
public class SamplingConfig {

    @Bean
    public Sampler constantSampler() {
        // Sample 10% of all traces
        return Sampler.traceIdRatioBased(0.1);
    }

    @Bean
    public Sampler alwaysOnSampler() {
        // Sample all traces (for development)
        return Sampler.alwaysOn();
    }

    @Bean
    public Sampler alwaysOffSampler() {
        // Sample no traces (for high-throughput)
        return Sampler.alwaysOff();
    }
}
```

#### Parent-based sampling
```java
public class ParentBasedSampler implements Sampler {

    private final Sampler rootSampler;
    private final Sampler remoteParentSampler;
    private final Sampler localParentSampler;

    public ParentBasedSampler(Sampler rootSampler, Sampler remoteParentSampler, Sampler localParentSampler) {
        this.rootSampler = rootSampler;
        this.remoteParentSampler = remoteParentSampler;
        this.localParentSampler = localParentSampler;
    }

    @Override
    public SamplingResult shouldSample(SpanContext parentContext, String traceId,
                                     String name, SpanKind spanKind, Attributes attributes,
                                     List<Link> parentLinks) {

        // If this is a root span (no parent)
        if (parentContext == null) {
            return rootSampler.shouldSample(parentContext, traceId, name, spanKind,
                                          attributes, parentLinks);
        }

        // If parent is remote (from another service)
        if (parentContext.isRemote()) {
            return remoteParentSampler.shouldSample(parentContext, traceId, name, spanKind,
                                                   attributes, parentLinks);
        }

        // If parent is local (from same service)
        return localParentSampler.shouldSample(parentContext, traceId, name, spanKind,
                                              attributes, parentLinks);
    }

    @Override
    public String getDescription() {
        return "ParentBasedSampler";
    }
}
```

#### Dynamic sampling
```java
@Service
public class DynamicSampler implements Sampler {

    private final AtomicReference<Sampler> currentSampler = new AtomicReference<>(
        Sampler.traceIdRatioBased(0.1)
    );

    @Autowired
    private MeterRegistry meterRegistry;

    @Scheduled(fixedRate = 60000) // Every minute
    public void updateSamplingRate() {
        // Get current system load
        double cpuUsage = getCpuUsage();
        double memoryUsage = getMemoryUsage();

        // Adjust sampling rate based on system load
        double samplingRate;
        if (cpuUsage > 80 || memoryUsage > 85) {
            samplingRate = 0.01; // 1% sampling under high load
        } else if (cpuUsage > 60 || memoryUsage > 70) {
            samplingRate = 0.05; // 5% sampling under moderate load
        } else {
            samplingRate = 0.1;  // 10% sampling under normal load
        }

        currentSampler.set(Sampler.traceIdRatioBased(samplingRate));

        // Record sampling rate as metric
        Gauge.builder("tracing.sampling.rate")
            .register(meterRegistry)
            .set(samplingRate);
    }

    @Override
    public SamplingResult shouldSample(SpanContext parentContext, String traceId,
                                     String name, SpanKind spanKind, Attributes attributes,
                                     List<Link> parentLinks) {
        return currentSampler.get().shouldSample(parentContext, traceId, name, spanKind,
                                                attributes, parentLinks);
    }

    @Override
    public String getDescription() {
        return "DynamicSampler";
    }

    private double getCpuUsage() {
        // Implementation to get CPU usage
        return 50.0; // Mock value
    }

    private double getMemoryUsage() {
        // Implementation to get memory usage
        return 60.0; // Mock value
    }
}
```

## Storage backends

### Cassandra configuration

#### Schema initialization
```bash
# Создание keyspace
cqlsh -e "CREATE KEYSPACE IF NOT EXISTS jaeger_v1 WITH REPLICATION = {'class' : 'SimpleStrategy', 'replication_factor' : 1 };"

# Создание таблиц (автоматически делает Jaeger)
# Или вручную:
cqlsh -e "USE jaeger_v1; DESCRIBE TABLES;"
```

#### Production Cassandra
```yaml
# Cassandra cluster configuration
apiVersion: cassandra.k8s.elastic.co/v1beta1
kind: CassandraCluster
metadata:
  name: jaeger-cassandra
spec:
  nodesPerRack: 3
  racks:
  - name: rack1
    zone: us-east1-a
  - name: rack2
    zone: us-east1-b
  - name: rack3
    zone: us-east1-c
  resources:
    requests:
      memory: 8Gi
      cpu: 2000m
    limits:
      memory: 16Gi
      cpu: 4000m
  storage:
    size: 500Gi
```

### Elasticsearch configuration

#### Index templates
```json
{
  "index_patterns": ["jaeger-span-*"],
  "settings": {
    "number_of_shards": 3,
    "number_of_replicas": 1,
    "index.codec": "best_compression",
    "refresh_interval": "30s"
  },
  "mappings": {
    "properties": {
      "traceID": { "type": "keyword" },
      "spanID": { "type": "keyword" },
      "parentSpanID": { "type": "keyword" },
      "operationName": { "type": "keyword" },
      "serviceName": { "type": "keyword" },
      "startTime": { "type": "date" },
      "duration": { "type": "long" },
      "tags": { "type": "object" },
      "logs": { "type": "nested" }
    }
  }
}
```

#### Index lifecycle management
```json
{
  "policy": {
    "phases": {
      "hot": {
        "actions": {
          "rollover": {
            "max_age": "1d",
            "max_size": "50gb"
          }
        }
      },
      "warm": {
        "min_age": "7d",
        "actions": {
          "allocate": {
            "number_of_replicas": 1
          },
          "shrink": {
            "number_of_shards": 1
          }
        }
      },
      "cold": {
        "min_age": "30d",
        "actions": {
          "allocate": {
            "number_of_replicas": 0
          }
        }
      },
      "delete": {
        "min_age": "90d",
        "actions": {
          "delete": {}
        }
      }
    }
  }
}
```

## Query и анализ

### Jaeger Query API

#### Search traces
```bash
# Поиск traces по service
curl "http://jaeger:16686/api/traces?service=user-service&limit=20"

# Поиск с фильтрами
curl "http://jaeger:16686/api/traces?service=user-service&operation=createUser&limit=10&start=1609459200000&end=1609462800000"

# Поиск по trace ID
curl "http://jaeger:16686/api/traces/1234567890abcdef"

# Поиск с тегами
curl "http://jaeger:16686/api/traces?service=user-service&tags=%7B%22error%22%3A%22true%22%7D"
```

#### Dependencies
```bash
# Получение dependency graph
curl "http://jaeger:16686/api/dependencies?endTs=1609462800000&lookback=3600000000000"

# Services
curl "http://jaeger:16686/api/services"

# Operations для service
curl "http://jaeger:16686/api/operations?service=user-service"
```

### Advanced queries

#### Latency analysis
```java
public class TraceAnalyzer {

    private final JaegerQueryApi queryApi;

    public List<TraceLatency> analyzeLatencies(String service, long startTime, long endTime) {
        // Получение traces
        List<Trace> traces = queryApi.searchTraces(service, null, startTime, endTime, 1000);

        return traces.stream()
            .map(this::analyzeTraceLatency)
            .collect(Collectors.toList());
    }

    private TraceLatency analyzeTraceLatency(Trace trace) {
        TraceLatency latency = new TraceLatency();
        latency.setTraceId(trace.getTraceId());

        // Анализ spans
        for (Span span : trace.getSpans()) {
            if (isDatabaseCall(span)) {
                latency.addDatabaseLatency(span.getDuration());
            } else if (isExternalCall(span)) {
                latency.addExternalLatency(span.getDuration());
            } else {
                latency.addServiceLatency(span.getDuration());
            }
        }

        return latency;
    }

    private boolean isDatabaseCall(Span span) {
        return span.getTags().containsKey("db.instance");
    }

    private boolean isExternalCall(Span span) {
        return span.getOperationName().contains("http") ||
               span.getTags().containsKey("http.url");
    }
}
```

#### Error analysis
```java
public class ErrorTraceAnalyzer {

    public List<ErrorSummary> analyzeErrors(String service, Duration timeWindow) {
        long endTime = System.currentTimeMillis();
        long startTime = endTime - timeWindow.toMillis();

        List<Trace> traces = queryApi.searchTraces(service, null, startTime, endTime, 10000);

        Map<String, ErrorSummary> errorSummary = new HashMap<>();

        for (Trace trace : traces) {
            for (Span span : trace.getSpans()) {
                if (hasError(span)) {
                    String errorType = getErrorType(span);
                    ErrorSummary summary = errorSummary.computeIfAbsent(errorType,
                        k -> new ErrorSummary(errorType));

                    summary.incrementCount();
                    summary.addAffectedService(span.getServiceName());
                    summary.addDuration(span.getDuration());
                }
            }
        }

        return new ArrayList<>(errorSummary.values());
    }

    private boolean hasError(Span span) {
        return span.getTags().containsKey("error") ||
               span.getLogs().stream().anyMatch(log -> log.getFields().containsKey("error"));
    }

    private String getErrorType(Span span) {
        String error = (String) span.getTags().get("error");
        if (error != null) return error;

        // Check logs
        for (Log log : span.getLogs()) {
            if (log.getFields().containsKey("error")) {
                return (String) log.getFields().get("error");
            }
        }

        return "unknown";
    }
}
```

## Performance monitoring

### Tracing metrics

#### Custom metrics
```java
@Configuration
public class TracingMetricsConfig {

    @Autowired
    private MeterRegistry registry;

    @Bean
    public MeterFilter tracingMetricsFilter() {
        return new MeterFilter() {
            @Override
            public Meter.Id map(Meter.Id id) {
                if (id.getName().startsWith("tracing")) {
                    return id.withTag("component", "jaeger");
                }
                return id;
            }
        };
    }

    @Bean
    public TracingMetricsCollector tracingMetricsCollector() {
        return new TracingMetricsCollector(registry);
    }
}

@Component
public class TracingMetricsCollector {

    private final MeterRegistry registry;
    private final Counter tracesReceived;
    private final Counter spansProcessed;
    private final Timer traceProcessingTime;

    public TracingMetricsCollector(MeterRegistry registry) {
        this.registry = registry;

        this.tracesReceived = Counter.builder("tracing.traces.received")
            .description("Number of traces received")
            .register(registry);

        this.spansProcessed = Counter.builder("tracing.spans.processed")
            .description("Number of spans processed")
            .register(registry);

        this.traceProcessingTime = Timer.builder("tracing.trace.processing.duration")
            .description("Time taken to process traces")
            .register(registry);
    }

    public void recordTraceReceived(Trace trace) {
        tracesReceived.increment();

        Timer.Sample sample = Timer.start(registry);
        try {
            processTrace(trace);
        } finally {
            sample.stop(traceProcessingTime);
        }
    }

    public void recordSpansProcessed(int count) {
        spansProcessed.increment(count);
    }

    private void processTrace(Trace trace) {
        // Trace processing logic
        recordSpansProcessed(trace.getSpans().size());
    }
}
```

### Performance dashboards

#### Jaeger performance queries
```promql
# Jaeger collector metrics
jaeger_collector_traces_received_total
jaeger_collector_spans_received_total
jaeger_collector_traces_dropped_total

# Processing latency
jaeger_collector_save_latency_bucket

# Storage performance
jaeger_cassandra_write_latency_bucket
jaeger_elasticsearch_index_latency_bucket

# Agent metrics
jaeger_agent_batch_size
jaeger_agent_queue_length

# Query service
jaeger_query_requests_total
jaeger_query_latency_bucket
```

## Troubleshooting

### Распространенные проблемы

#### Traces not appearing

**Symptoms:**
- Traces отправляются но не отображаются в UI
- Collector получает данные но не сохраняет

**Solutions:**
```bash
# Проверить collector logs
docker logs jaeger-collector

# Проверить storage connection
curl http://jaeger-collector:14268/api/traces \
  -H "Content-Type: application/json" \
  -d '{"data":[{"traceID":"test"}]}'

# Проверить sampling
# Убедиться что sampling rate > 0
```

#### Missing spans

**Symptoms:**
- Некоторые spans отсутствуют в traces
- Несогласованные trace IDs

**Solutions:**
```java
// Проверить trace context propagation
public class TraceDebugging {

    public static void debugTraceContext() {
        Span currentSpan = Span.current();
        if (currentSpan != null) {
            System.out.println("Current span: " + currentSpan.getSpanContext().getSpanId());
            System.out.println("Trace ID: " + currentSpan.getSpanContext().getTraceId());
        } else {
            System.out.println("No active span");
        }
    }

    public static void debugBaggage() {
        Baggage baggage = Baggage.current();
        baggage.forEach((key, entry) -> {
            System.out.println(key + ": " + entry.getValue());
        });
    }
}
```

#### High latency

**Symptoms:**
- Высокая latency instrumentation
- Application performance degradation

**Solutions:**
```yaml
# Оптимизация sampling
sampling:
  strategies:
    - operation: "health.check"
      sampler:
        type: "const"
        param: 0  # No sampling for health checks

# Async processing
exporter:
  jaeger:
    endpoint: "http://jaeger:14268/api/traces"
    # Batch export
    batch:
      enabled: true
      maxSize: 512
      timeout: 5s
```

#### Storage issues

**Symptoms:**
- Ошибки записи в storage
- Потеря данных

**Solutions:**
```bash
# Cassandra troubleshooting
# Проверить cluster status
nodetool status

# Проверить keyspace
cqlsh -e "DESCRIBE KEYSPACES;"

# Elasticsearch troubleshooting
# Проверить cluster health
curl http://elasticsearch:9200/_cluster/health

# Проверить индексы
curl http://elasticsearch:9200/_cat/indices
```

### Debug techniques

#### Span debugging
```java
public class SpanDebugger {

    public static void debugSpan(Span span) {
        SpanContext context = span.getSpanContext();

        System.out.println("=== Span Debug Info ===");
        System.out.println("Trace ID: " + context.getTraceId());
        System.out.println("Span ID: " + context.getSpanId());
        System.out.println("Parent Span ID: " + context.getParentSpanId());
        System.out.println("Name: " + span.getName());
        System.out.println("Kind: " + span.getKind());
        System.out.println("Start Time: " + span.getStartEpochNanos());
        System.out.println("Status: " + span.getStatus());
        System.out.println("Ended: " + span.isEnded());

        System.out.println("Attributes:");
        span.getAttributes().forEach((key, value) ->
            System.out.println("  " + key + ": " + value));

        System.out.println("Events:");
        span.getEvents().forEach(event -> {
            System.out.println("  " + event.getName() + " at " + event.getEpochNanos());
            event.getAttributes().forEach((key, value) ->
                System.out.println("    " + key + ": " + value));
        });
    }

    public static void debugTraceContext() {
        Context context = Context.current();
        Span span = Span.fromContext(context);

        if (span != null) {
            debugSpan(span);
        } else {
            System.out.println("No active span in context");
        }
    }
}
```

#### Configuration validation
```java
@Configuration
public class TracingConfigurationValidator {

    @PostConstruct
    public void validateConfiguration() {
        // Проверить OpenTelemetry SDK
        OpenTelemetry openTelemetry = GlobalOpenTelemetry.get();
        TracerProvider tracerProvider = openTelemetry.getTracerProvider();

        if (tracerProvider instanceof SdkTracerProvider) {
            SdkTracerProvider sdkProvider = (SdkTracerProvider) tracerProvider;

            // Проверить span processors
            List<SpanProcessor> processors = sdkProvider.getSpanProcessors();
            System.out.println("Span processors: " + processors.size());

            for (SpanProcessor processor : processors) {
                System.out.println("Processor: " + processor.getClass().getSimpleName());
            }

            // Проверить sampler
            Sampler sampler = sdkProvider.getSampler();
            System.out.println("Sampler: " + sampler.getDescription());

        } else {
            System.err.println("WARNING: Not using SDK TracerProvider");
        }

        // Проверить Jaeger exporter
        // ... additional validation
    }
}
```

## Best practices

### 1. Span naming

#### Consistent naming
```java
public class SpanNamingStandards {

    // HTTP operations
    public static final String HTTP_CLIENT = "http.client";
    public static final String HTTP_SERVER = "http.server";

    // Database operations
    public static final String DB_QUERY = "db.query";
    public static final String DB_CONNECTION = "db.connection";

    // Business operations
    public static final String BUSINESS_USER_CREATE = "user.create";
    public static final String BUSINESS_PAYMENT_PROCESS = "payment.process";

    // Messaging
    public static final String MSG_SEND = "msg.send";
    public static final String MSG_RECEIVE = "msg.receive";

    // Methods
    public static Span createHttpClientSpan(Tracer tracer, String method, String url) {
        return tracer.spanBuilder(HTTP_CLIENT)
            .setAttribute("http.method", method)
            .setAttribute("http.url", url)
            .startSpan();
    }

    public static Span createDatabaseSpan(Tracer tracer, String operation, String table) {
        return tracer.spanBuilder(DB_QUERY)
            .setAttribute("db.operation", operation)
            .setAttribute("db.table", table)
            .startSpan();
    }
}
```

### 2. Attribute standards

#### Semantic conventions
```java
public class TracingAttributes {

    // HTTP attributes
    public static final AttributeKey<String> HTTP_METHOD = AttributeKey.stringKey("http.method");
    public static final AttributeKey<String> HTTP_URL = AttributeKey.stringKey("http.url");
    public static final AttributeKey<Long> HTTP_STATUS_CODE = AttributeKey.longKey("http.status_code");

    // Database attributes
    public static final AttributeKey<String> DB_SYSTEM = AttributeKey.stringKey("db.system");
    public static final AttributeKey<String> DB_OPERATION = AttributeKey.stringKey("db.operation");
    public static final AttributeKey<String> DB_TABLE = AttributeKey.stringKey("db.table");

    // Business attributes
    public static final AttributeKey<String> BUSINESS_OPERATION = AttributeKey.stringKey("business.operation");
    public static final AttributeKey<String> BUSINESS_ENTITY = AttributeKey.stringKey("business.entity");
    public static final AttributeKey<String> BUSINESS_ENTITY_ID = AttributeKey.stringKey("business.entity.id");

    // Error attributes
    public static final AttributeKey<String> ERROR_TYPE = AttributeKey.stringKey("error.type");
    public static final AttributeKey<String> ERROR_MESSAGE = AttributeKey.stringKey("error.message");

    // Helper methods
    public static void addHttpAttributes(Span span, String method, String url, int statusCode) {
        span.setAttribute(HTTP_METHOD, method);
        span.setAttribute(HTTP_URL, url);
        span.setAttribute(HTTP_STATUS_CODE, statusCode);
    }

    public static void addDatabaseAttributes(Span span, String system, String operation, String table) {
        span.setAttribute(DB_SYSTEM, system);
        span.setAttribute(DB_OPERATION, operation);
        span.setAttribute(DB_TABLE, table);
    }

    public static void addErrorAttributes(Span span, Throwable error) {
        span.setAttribute(ERROR_TYPE, error.getClass().getSimpleName());
        span.setAttribute(ERROR_MESSAGE, error.getMessage());
        span.recordException(error);
    }
}
```

### 3. Context propagation

#### Headers standards
```java
public class TraceContextHeaders {

    public static final String TRACE_ID_HEADER = "x-trace-id";
    public static final String SPAN_ID_HEADER = "x-span-id";
    public static final String TRACE_FLAGS_HEADER = "x-trace-flags";
    public static final String TRACE_STATE_HEADER = "x-trace-state";

    // W3C Trace Context
    public static final String TRACE_PARENT_HEADER = "traceparent";
    public static final String TRACE_STATE_W3C_HEADER = "tracestate";

    // Baggage headers
    public static final String BAGGAGE_HEADER = "baggage";

    public static void injectTraceContext(HttpHeaders headers, Span span) {
        SpanContext context = span.getSpanContext();

        // Custom headers
        headers.set(TRACE_ID_HEADER, context.getTraceId());
        headers.set(SPAN_ID_HEADER, context.getSpanId());

        // W3C Trace Context
        String traceParent = String.format("00-%s-%s-%s",
            context.getTraceId(),
            context.getSpanId(),
            context.getTraceFlags().asHex()
        );
        headers.set(TRACE_PARENT_HEADER, traceParent);

        // Baggage
        Baggage baggage = Baggage.current();
        if (!baggage.isEmpty()) {
            String baggageHeader = baggage.asMap().entrySet().stream()
                .map(entry -> entry.getKey() + "=" + entry.getValue().getValue())
                .collect(Collectors.joining(","));
            headers.set(BAGGAGE_HEADER, baggageHeader);
        }
    }

    public static SpanContext extractTraceContext(HttpHeaders headers) {
        String traceParent = headers.getFirst(TRACE_PARENT_HEADER);
        if (traceParent != null) {
            String[] parts = traceParent.split("-");
            if (parts.length >= 4) {
                TraceId traceId = TraceId.fromHex(parts[1], 0);
                SpanId spanId = SpanId.fromHex(parts[2], 0);
                TraceFlags flags = TraceFlags.fromHex(parts[3], 0);

                return SpanContext.create(traceId, spanId, flags, TraceState.getDefault());
            }
        }

        // Fallback to custom headers
        String traceId = headers.getFirst(TRACE_ID_HEADER);
        String spanId = headers.getFirst(SPAN_ID_HEADER);

        if (traceId != null && spanId != null) {
            return SpanContext.create(
                TraceId.fromHex(traceId, 0),
                SpanId.fromHex(spanId, 0),
                TraceFlags.getDefault(),
                TraceState.getDefault()
            );
        }

        return null;
    }
}
```

### 4. Error handling

#### Error spans
```java
public class TracingErrorHandler {

    public static void handleError(Span span, Throwable error) {
        // Set span status
        span.setStatus(StatusCode.ERROR, error.getMessage());

        // Record exception
        span.recordException(error);

        // Add error attributes
        span.setAttribute("error", true);
        span.setAttribute("error.type", error.getClass().getSimpleName());
        span.setAttribute("error.message", error.getMessage());

        // Add stack trace for debugging (optional)
        if (span.isRecording()) {
            StringWriter sw = new StringWriter();
            error.printStackTrace(new PrintWriter(sw));
            span.setAttribute("error.stacktrace", sw.toString());
        }
    }

    public static void handleBusinessError(Span span, String errorCode, String message) {
        span.setStatus(StatusCode.ERROR, message);
        span.setAttribute("business.error", true);
        span.setAttribute("business.error.code", errorCode);
        span.setAttribute("business.error.message", message);
    }

    public static boolean shouldSampleError(Span span, Throwable error) {
        // Always sample errors
        if (error != null) {
            return true;
        }

        // Sample spans with errors
        return span.getStatus().getStatusCode() == StatusCode.ERROR;
    }
}
```

### 5. Performance optimization

#### Efficient instrumentation
```java
public class OptimizedTracing {

    // Использовать guards для expensive operations
    public void logExpensiveOperation(Tracer tracer, String operation, Runnable action) {
        Span span = tracer.spanBuilder(operation).startSpan();

        try (Scope scope = span.makeCurrent()) {
            action.run();
        } finally {
            span.end();
        }
    }

    // Batch span operations
    public void batchSpanOperations(Tracer tracer, List<Runnable> operations) {
        Span batchSpan = tracer.spanBuilder("batch.operations").startSpan();

        try (Scope scope = batchSpan.makeCurrent()) {
            for (int i = 0; i < operations.size(); i++) {
                Span operationSpan = tracer.spanBuilder("operation." + i).startSpan();

                try (Scope opScope = operationSpan.makeCurrent()) {
                    operations.get(i).run();
                } finally {
                    operationSpan.end();
                }
            }
        } finally {
            batchSpan.end();
        }
    }

    // Conditional instrumentation
    public void conditionalTracing(Tracer tracer, boolean shouldTrace, Runnable action) {
        if (!shouldTrace) {
            action.run();
            return;
        }

        Span span = tracer.spanBuilder("conditional.operation").startSpan();
        try (Scope scope = span.makeCurrent()) {
            action.run();
        } finally {
            span.end();
        }
    }

    // Async operations
    public CompletableFuture<Void> traceAsyncOperation(Tracer tracer, String operation,
                                                     Supplier<CompletableFuture<Void>> asyncAction) {
        Span span = tracer.spanBuilder(operation).startSpan();

        return asyncAction.get()
            .whenComplete((result, error) -> {
                if (error != null) {
                    span.recordException(error);
                }
                span.end();
            });
    }
}
```

## Заключение

**Jaeger** — это мощная система для distributed tracing, которая предоставляет всесторонние возможности для отслеживания запросов в сложных распределенных системах. Благодаря интеграции с OpenTelemetry и Spring Boot, Jaeger легко интегрируется в экосистему Java.

### Ключевые возможности:

1. **Distributed tracing** — полное отслеживание запросов через сервисы
2. **OpenTelemetry integration** — стандарты и автоматическая instrumentation
3. **Multiple storage backends** — Cassandra, Elasticsearch, memory
4. **Sampling strategies** — эффективный сбор данных
5. **Rich UI** — анализ traces и dependencies
6. **Context propagation** — передача trace context между сервисами
7. **Performance analysis** — выявление bottlenecks и latency issues
8. **Error tracking** — анализ ошибок и исключений

### Архитектурные преимущества:

#### Scalability:
- **Horizontal scaling** — распределение нагрузки между collector'ами
- **Storage flexibility** — выбор подходящего backend
- **Sampling control** — управление объемом данных
- **Federation** — глобальное агрегирование traces

#### Observability:
- **End-to-end visibility** — полная картина системы
- **Service dependencies** — визуализация зависимостей
- **Performance insights** — анализ latency и throughput
- **Root cause analysis** — быстрая диагностика проблем

### Когда использовать Jaeger:

✅ **Микросервисная архитектура** — отслеживание через границы сервисов
✅ **Distributed systems** — анализ взаимодействия компонентов
✅ **Performance optimization** — выявление bottlenecks
✅ **Error analysis** — диагностика проблем в production
✅ **Service mesh** — интеграция с Istio, Linkerd
✅ **Cloud-native** — Kubernetes и облачные платформы
✅ **Complex workflows** — бизнес-процессы через multiple services
✅ **Real-time monitoring** — live tracing и alerting

### Когда НЕ использовать:

❌ **Monolithic applications** — простые односервисные приложения
❌ **High-frequency trading** — ultra-low latency требования
❌ **Resource constraints** — ограниченные ресурсы для tracing
❌ **Simple APIs** — базовые REST API без complex logic
❌ **Development only** — если tracing нужен только для debugging
❌ **Cost sensitive** — если overhead tracing слишком высок
❌ **Legacy systems** — сложная интеграция со старыми системами

### Best practices:

1. **Semantic naming** — стандартизированные имена spans и attributes
2. **Sampling strategy** — баланс между visibility и performance
3. **Context propagation** — надежная передача trace context
4. **Error handling** — правильная запись ошибок в spans
5. **Storage optimization** — выбор подходящего backend
6. **Performance monitoring** — отслеживание overhead tracing
7. **Security** — защита чувствительных данных в traces
8. **Documentation** — документирование tracing approach

### Интеграция с другими инструментами:

- **Prometheus** — метрики и алертинг
- **Grafana** — визуализация traces и метрик
- **Zipkin** — альтернативная система tracing
- **OpenTelemetry** — стандарты observability
- **Spring Cloud Sleuth** — Spring интеграция
- **Istio** — service mesh integration
- **Kubernetes** — container orchestration
- **ELK Stack** — логи и анализ

### Trace lifecycle:

1. **Instrumentation** — добавление tracing кода
2. **Span creation** — генерация spans для operations
3. **Context propagation** — передача trace context
4. **Collection** — сбор spans Jaeger collector'ом
5. **Storage** — сохранение в backend
6. **Query** — поиск и анализ в UI
7. **Visualization** — dashboards и reports
8. **Optimization** — улучшение на основе insights

Jaeger является стандартом де-факто для distributed tracing в современной экосистеме микросервисов. Правильная настройка и использование Jaeger обеспечивает глубокое понимание работы распределенных систем и быструю реакцию на проблемы. 🚀

**Далее: Zipkin для альтернативного distributed tracing**