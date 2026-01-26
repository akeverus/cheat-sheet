# Prometheus для Java

Комплексное руководство по использованию Prometheus для мониторинга Java-приложений: настройка, метрики, алертинг, интеграция с Spring Boot и продвинутые техники.

**Дата последнего обновления:** 2026-01-21

## Полезные ссылки

### Официальная документация
- [Prometheus Documentation](https://prometheus.io/docs/) - Основная документация
- [Prometheus Metrics](https://prometheus.io/docs/concepts/data_model/) - Модель данных
- [PromQL](https://prometheus.io/docs/prometheus/latest/querying/basics/) - Язык запросов

### Java интеграции
- [Prometheus Java Client](https://github.com/prometheus/client_java) - Официальный клиент
- [Micrometer](https://micrometer.io/) - Метрики для JVM приложений
- [Spring Boot Actuator](https://docs.spring.io/spring-boot/docs/current/reference/html/actuator.html) - Актуатор для Spring

### Лучшие практики
- [Prometheus Best Practices](https://prometheus.io/docs/practices/)
- [Monitoring with Prometheus](https://www.oreilly.com/library/view/monitoring-with-prometheus/9781492034149/)
- [Prometheus Metrics Guide](https://prometheus.io/docs/practices/naming/)

### См. также
- `monitoring/observability.md` - Основы Observability
- `monitoring/grafana.md` - Grafana для визуализации
- `monitoring/alerting.md` - Система алертинга
- `monitoring/metrics.md` - Метрики в приложениях

## Содержание

- [Введение в Prometheus](#введение-в-prometheus)
- [Архитектура Prometheus](#архитектура-prometheus)
- [Установка и настройка](#установка-и-настройка)
- [Java Client для Prometheus](#java-client-для-prometheus)
- [Micrometer интеграция](#micrometer-интеграция)
- [Spring Boot интеграция](#spring-boot-интеграция)
- [Метрики JVM](#метрики-jvm)
- [Бизнес-метрики](#бизнес-метрики)
- [PromQL запросы](#promql-запросы)
- [Алертинг с Prometheus](#алертинг-с-prometheus)
- [Service Discovery](#service-discovery)
- [High Availability](#high-availability)
- [Best Practices](#best-practices)
- [Troubleshooting](#troubleshooting)
- [Заключение](#заключение)

## Введение в Prometheus

**Prometheus** — это система мониторинга и алертинга с открытым исходным кодом, разработанная для надежного мониторинга распределенных систем. Prometheus собирает метрики с приложений и хранит их в виде временных рядов.

### Почему Prometheus?

Prometheus предлагает мощные возможности для мониторинга:

1. **Модель данных на основе временных рядов** — эффективное хранение и запросы
2. **Язык запросов PromQL** — мощный язык для анализа данных
3. **Service Discovery** — автоматическое обнаружение сервисов
4. **Pull модель** — надежный сбор метрик
5. **Алертинг** — встроенная система оповещений
6. **Интеграция** — богатая экосистема инструментов
7. **Scalability** — горизонтальное масштабирование
8. **Reliability** — отказоустойчивая архитектура

### Основные компоненты

#### Prometheus Server
- **Time Series Database** — хранение метрик
- **Retrieval** — сбор метрик от targets
- **Storage** — локальное хранение данных
- **HTTP Server** — API для запросов и управления

#### Pushgateway
- **Push модель** — для batch jobs и short-lived jobs
- **Bridge** — между push и pull моделями
- **Reliability** — обеспечение доставки метрик

#### Alertmanager
- **Alert routing** — маршрутизация алертов
- **Grouping** — группировка алертов
- **Silencing** — подавление алертов
- **Inhibition** — подавление связанных алертов

#### Exporters
- **Application metrics** — экспорт метрик приложений
- **System metrics** — метрики ОС и инфраструктуры
- **Third-party** — метрики внешних систем

## Архитектура Prometheus

### Pull модель сбора метрик

**Pull модель** является фундаментальным принципом работы Prometheus. В отличие от push модели, где приложения отправляют метрики на сервер, в pull модели Prometheus сам запрашивает метрики у приложений через HTTP.

**Почему pull модель предпочтительнее:**

1. **Централизованный контроль** — Prometheus решает, какие метрики и когда собирать
2. **Отказоустойчивость** — Если приложение недоступно, сбор просто пропускается
3. **Простота приложений** — Не нужно реализовывать отправку метрик
4. **Отладка** — Легко проверить endpoint /metrics вручную
5. **Масштабируемость** — Легче добавить новые targets без изменения приложений

**Процесс сбора метрик:**

```
┌─────────────────────────────────────────────────────────────┐
│                    Prometheus Server                        │
│  ┌─────────────────────────────────────────────────────┐    │
│  │ 1. Service Discovery (Обнаружение сервисов)         │    │
│  │    • DNS: Запросы к DNS для получения IP адресов    │    │
│  │    • Kubernetes: API calls для получения pod'ов     │    │
│  │    • Consul: Запросы к service registry             │    │
│  │    • File-based: Чтение статического файла          │    │
│  │    • AWS/Azure/GCP: API calls к cloud provider'ам  │    │
│  └─────────────────────────────────────────────────────┘    │
│                                                             │
│  ┌─────────────────────────────────────────────────────┐    │
│  │ 2. Target Scraping (Сбор с targets)                │    │
│  │    • HTTP GET /metrics на каждый target             │    │
│  │    • Parse Prometheus exposition format             │    │
│  │    • Handle timeouts и connection errors            │    │
│  │    • Apply relabeling rules                         │    │
│  └─────────────────────────────────────────────────────┘    │
│                                                             │
│  ┌─────────────────────────────────────────────────────┐    │
│  │ 3. Time Series Processing (Обработка рядов)        │    │
│  │    • Add labels (job, instance, etc.)               │    │
│  │    • Apply recording rules                          │    │
│  │    • Store in TSDB with timestamps                  │    │
│  └─────────────────────────────────────────────────────┘    │
│                                                             │
│  ┌─────────────────────────────────────────────────────┐    │
│  │ 4. Storage & Compaction (Хранение и сжатие)        │    │
│  │    • Local storage в формате blocks                 │    │
│  │    • Automatic compaction для оптимизации           │    │
│  │    • Retention policies для удаления старых данных │    │
│  └─────────────────────────────────────────────────────┘    │
└─────────────────────────────────────────────────────────────┘
                              │
                              ▼
┌─────────────────────────────────────────────────────────────┐
│                     Target Applications                     │
│  ┌─────────────────────────────────────────────────────┐    │
│  │ /metrics HTTP endpoint                             │    │
│  │ Формат Prometheus:                                 │    │
│  │ # HELP http_requests_total Общее количество...     │    │
│  │ # TYPE http_requests_total counter                 │    │
│  │ http_requests_total{method="GET",status="200"} 42  │    │
│  │                                                     │    │
│  │ Генерация метрик:                                  │    │
│  │ • Counters: monotonically increasing               │    │
│  │ • Gauges: can go up and down                       │    │
│  │ • Histograms: распределения с buckets             │    │
│  │ • Summaries: квантили без buckets                  │    │
│  └─────────────────────────────────────────────────────┘    │
└─────────────────────────────────────────────────────────────┘
```

**Детали процесса scraping:**

**HTTP Request к target:**
```http
GET /metrics HTTP/1.1
Host: myapp.example.com:8080
User-Agent: Prometheus/2.40.0
Accept: application/openmetrics-text; version=1.0.0; charset=utf-8;q=0.8, text/plain; version=0.0.4; charset=utf-8;q=0.5
```

**Response от приложения:**
```
HTTP/1.1 200 OK
Content-Type: text/plain; version=0.0.4; charset=utf-8

# HELP jvm_memory_used_bytes Used bytes of a given JVM memory area.
# TYPE jvm_memory_used_bytes gauge
jvm_memory_used_bytes{area="heap"} 1.2456789e+08
jvm_memory_used_bytes{area="nonheap"} 7.8901234e+07

# HELP http_requests_total Total number of HTTP requests
# TYPE http_requests_total counter
http_requests_total{method="GET",status="200"} 1547
http_requests_total{method="POST",status="201"} 234
```

**Обработка метрик в Prometheus:**
1. **Parsing** — Разбор формата метрик
2. **Labeling** — Добавление labels (job, instance, etc.)
3. **Storage** — Сохранение в time series database
4. **Compression** — Сжатие данных для эффективного хранения
5. **Retention** — Удаление старых данных согласно политике

### Push модель (Pushgateway)

Для сценариев, где pull модель не подходит:

```
┌─────────────────────────────────────────────────────────┐
│                  Pushgateway                           │
│  ┌─────────────────────────────────────────────────┐    │
│  │ Receives metrics via HTTP POST                  │    │
│  │ Stores metrics in memory                        │    │
│  │ Exposes /metrics endpoint for Prometheus        │    │
│  └─────────────────────────────────────────────────┘    │
└─────────────────────────────────────────────────────────┘
         ▲                           │
         │                           ▼
┌─────────────────┐         ┌─────────────────┐
│  Batch Jobs     │         │  Prometheus     │
│  Short-lived    │         │  Server         │
│  services       │         │                 │
└─────────────────┘         └─────────────────┘
```

### Federation

Для масштабирования и глобального мониторинга:

```
┌─────────────────────────────────────────────────────────┐
│              Global Prometheus                         │
│  ┌─────────────────────────────────────────────────┐    │
│  │ Collects aggregated metrics from regional       │    │
│  │ Prometheus servers                               │    │
│  └─────────────────────────────────────────────────┘    │
└─────────────────────────────────────────────────────────┘
                          │
                 ┌────────┴────────┐
                 ▼                 ▼
        ┌─────────────────┐ ┌─────────────────┐
        │ Regional        │ │ Regional        │
        │ Prometheus 1    │ │ Prometheus 2    │
        └─────────────────┘ └─────────────────┘
                 │                 │
                 ▼                 ▼
        ┌─────────────────┐ ┌─────────────────┐
        │ Local           │ │ Local           │
        │ applications    │ │ applications    │
        └─────────────────┘ └─────────────────┘
```

## Установка и настройка

### Установка Prometheus

#### Docker
```bash
# Запуск Prometheus с базовой конфигурацией
docker run -d \
  --name prometheus \
  -p 9090:9090 \
  -v $(pwd)/prometheus.yml:/etc/prometheus/prometheus.yml \
  prom/prometheus

# С volume для хранения данных
docker run -d \
  --name prometheus \
  -p 9090:9090 \
  -v $(pwd)/prometheus.yml:/etc/prometheus/prometheus.yml \
  -v prometheus-data:/prometheus \
  prom/prometheus
```

#### Kubernetes
```yaml
apiVersion: apps/v1
kind: Deployment
metadata:
  name: prometheus
  namespace: monitoring
spec:
  replicas: 1
  selector:
    matchLabels:
      app: prometheus
  template:
    metadata:
      labels:
        app: prometheus
    spec:
      containers:
      - name: prometheus
        image: prom/prometheus:latest
        ports:
        - containerPort: 9090
        volumeMounts:
        - name: config
          mountPath: /etc/prometheus
        - name: data
          mountPath: /prometheus
      volumes:
      - name: config
        configMap:
          name: prometheus-config
      - name: data
        emptyDir: {}
---
apiVersion: v1
kind: Service
metadata:
  name: prometheus
  namespace: monitoring
spec:
  selector:
    app: prometheus
  ports:
  - port: 9090
    targetPort: 9090
```

### Базовая конфигурация

#### prometheus.yml
```yaml
global:
  scrape_interval: 15s      # Как часто собирать метрики
  evaluation_interval: 15s  # Как часто оценивать правила
  scrape_timeout: 10s       # Таймаут для scrape

rule_files:
  - "alert_rules.yml"       # Файлы с правилами алертинга
  - "recording_rules.yml"   # Файлы с recording rules

scrape_configs:
  - job_name: 'prometheus'
    static_configs:
      - targets: ['localhost:9090']

  - job_name: 'spring-boot-app'
    static_configs:
      - targets: ['localhost:8080']
    metrics_path: '/actuator/prometheus'
    scrape_interval: 5s

  - job_name: 'node-exporter'
    static_configs:
      - targets: ['localhost:9100']

  - job_name: 'pushgateway'
    static_configs:
      - targets: ['localhost:9091']
```

#### Service Discovery
```yaml
scrape_configs:
  - job_name: 'kubernetes-pods'
    kubernetes_sd_configs:
      - role: pod
    relabel_configs:
      - source_labels: [__meta_kubernetes_pod_annotation_prometheus_io_scrape]
        action: keep
        regex: true
      - source_labels: [__meta_kubernetes_pod_annotation_prometheus_io_path]
        action: replace
        target_label: __metrics_path__
        regex: (.+)
      - source_labels: [__meta_kubernetes_pod_annotation_prometheus_io_port]
        action: replace
        target_label: __address__
        regex: (.+)
      - source_labels: [__address__, __meta_kubernetes_pod_annotation_prometheus_io_port]
        action: replace
        regex: ([^:]+)(?::\d+)?;(\d+)
        replacement: $1:$2
        target_label: __address__

  - job_name: 'consul-services'
    consul_sd_configs:
      - server: 'localhost:8500'
        services: ['web', 'api', 'database']
    relabel_configs:
      - source_labels: [__meta_consul_service]
        target_label: service
      - source_labels: [__meta_consul_tags]
        target_label: tags
```

## Java Client для Prometheus

### Прямое использование Java Client

#### Maven зависимости
```xml
<dependency>
    <groupId>io.prometheus</groupId>
    <artifactId>simpleclient</artifactId>
    <version>0.16.0</version>
</dependency>
<dependency>
    <groupId>io.prometheus</groupId>
    <artifactId>simpleclient_httpserver</artifactId>
    <version>0.16.0</version>
</dependency>
<dependency>
    <groupId>io.prometheus</groupId>
    <artifactId>simpleclient_hotspot</artifactId>
    <version>0.16.0</version>
</dependency>
```

#### Базовое приложение
```java
import io.prometheus.client.Counter;
import io.prometheus.client.Gauge;
import io.prometheus.client.Histogram;
import io.prometheus.client.exporter.HTTPServer;
import io.prometheus.client.hotspot.DefaultExports;

public class PrometheusExample {

    private static final Counter requestsTotal = Counter.build()
        .name("http_requests_total")
        .help("Total number of HTTP requests")
        .labelNames("method", "endpoint", "status")
        .register();

    private static final Gauge activeConnections = Gauge.build()
        .name("active_connections")
        .help("Number of active connections")
        .register();

    private static final Histogram requestDuration = Histogram.build()
        .name("http_request_duration_seconds")
        .help("Request duration in seconds")
        .labelNames("method", "endpoint")
        .buckets(0.1, 0.5, 1.0, 2.5, 5.0, 10.0)
        .register();

    public static void main(String[] args) throws Exception {
        // Экспорт метрик JVM
        DefaultExports.initialize();

        // Запуск HTTP сервера для экспорта метрик
        HTTPServer server = new HTTPServer(8080);

        // Симуляция работы приложения
        simulateApplication();
    }

    private static void simulateApplication() {
        while (true) {
            // Имитация HTTP запроса
            String method = "GET";
            String endpoint = "/api/users";
            int status = 200;

            Timer.Sample timer = Timer.startTimer();
            try {
                // Бизнес логика
                Thread.sleep((long) (Math.random() * 1000));

                // Увеличение счетчика
                requestsTotal.labels(method, endpoint, String.valueOf(status)).inc();

                // Обновление gauge
                activeConnections.inc();

            } catch (Exception e) {
                requestsTotal.labels(method, endpoint, "500").inc();
            } finally {
                activeConnections.dec();
                timer.observeDuration();
            }

            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                break;
            }
        }
    }
}
```

#### Custom Collector
```java
import io.prometheus.client.Collector;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class DatabaseMetricsCollector extends Collector {

    @Override
    public List<MetricFamilySamples> collect() {
        List<MetricFamilySamples.Sample> samples = new ArrayList<>();

        // Сбор метрик базы данных
        DatabaseStats stats = getDatabaseStats();

        // Active connections
        samples.add(new MetricFamilySamples.Sample(
            "database_connections_active",
            Arrays.asList("database"),
            Arrays.asList("postgresql"),
            stats.getActiveConnections()
        ));

        // Idle connections
        samples.add(new MetricFamilySamples.Sample(
            "database_connections_idle",
            Arrays.asList("database"),
            Arrays.asList("postgresql"),
            stats.getIdleConnections()
        ));

        // Query duration
        samples.add(new MetricFamilySamples.Sample(
            "database_query_duration_seconds",
            Arrays.asList("query_type"),
            Arrays.asList("select"),
            stats.getAvgSelectDuration()
        ));

        List<MetricFamilySamples> mfs = new ArrayList<>();
        mfs.add(new MetricFamilySamples(
            "database_connections_active",
            Type.GAUGE,
            "Number of active database connections",
            samples
        ));

        return mfs;
    }

    private DatabaseStats getDatabaseStats() {
        // Получение реальных метрик БД
        return new DatabaseStats(5, 10, 0.05);
    }

    static class DatabaseStats {
        private final int activeConnections;
        private final int idleConnections;
        private final double avgSelectDuration;

        public DatabaseStats(int active, int idle, double avgDuration) {
            this.activeConnections = active;
            this.idleConnections = idle;
            this.avgSelectDuration = avgDuration;
        }

        public int getActiveConnections() { return activeConnections; }
        public int getIdleConnections() { return idleConnections; }
        public double getAvgSelectDuration() { return avgSelectDuration; }
    }
}
```

## Micrometer интеграция

### Micrometer Registry

Micrometer предоставляет vendor-neutral API для метрик:

```java
@Configuration
public class MetricsConfig {

    @Bean
    public MeterRegistry meterRegistry() {
        return new PrometheusMeterRegistry(PrometheusConfig.DEFAULT);
    }

    @Bean
    public PrometheusMeterRegistry prometheusRegistry() {
        return new PrometheusMeterRegistry(PrometheusConfig.DEFAULT);
    }
}
```

### Создание метрик

#### Counter
```java
@Service
public class UserService {

    private final Counter userRegistrations;
    private final Counter loginAttempts;
    private final Counter loginFailures;

    @Autowired
    public UserService(MeterRegistry registry) {
        this.userRegistrations = Counter.builder("user_registrations_total")
            .description("Total number of user registrations")
            .tags("service", "user-service")
            .register(registry);

        this.loginAttempts = Counter.builder("login_attempts_total")
            .description("Total number of login attempts")
            .tags("service", "user-service")
            .register(registry);

        this.loginFailures = Counter.builder("login_failures_total")
            .description("Total number of failed login attempts")
            .tags("service", "user-service")
            .register(registry);
    }

    public User registerUser(RegistrationRequest request) {
        try {
            User user = createUser(request);
            userRegistrations.increment();
            return user;
        } catch (Exception e) {
            // Метрики ошибок
            Counter.builder("user_registration_errors_total")
                .tags("error_type", e.getClass().getSimpleName())
                .register(meterRegistry)
                .increment();
            throw e;
        }
    }

    public boolean authenticate(String username, String password) {
        loginAttempts.increment();

        try {
            boolean success = doAuthenticate(username, password);
            if (!success) {
                loginFailures.increment();
            }
            return success;
        } catch (Exception e) {
            loginFailures.increment();
            throw e;
        }
    }
}
```

#### Gauge
```java
@Service
public class QueueMetricsService {

    private final Gauge queueSize;
    private final Gauge processingRate;

    @Autowired
    public QueueMetricsService(MeterRegistry registry, MessageQueue queue) {
        this.queueSize = Gauge.builder("queue_size", queue, MessageQueue::size)
            .description("Current queue size")
            .tags("queue_type", "message_queue")
            .register(registry);

        this.processingRate = Gauge.builder("queue_processing_rate", 
            new AtomicDouble(0.0), AtomicDouble::get)
            .description("Messages processed per second")
            .tags("queue_type", "message_queue")
            .register(registry);
    }

    @Scheduled(fixedRate = 1000)
    public void updateProcessingRate() {
        double rate = calculateProcessingRate();
        ((AtomicDouble) processingRate.getId().getValue()).set(rate);
    }
}
```

#### Timer
```java
@Service
public class PaymentService {

    private final Timer paymentProcessingTimer;
    private final Timer externalApiTimer;

    @Autowired
    public PaymentService(MeterRegistry registry) {
        this.paymentProcessingTimer = Timer.builder("payment_processing_duration")
            .description("Time taken to process payments")
            .tags("service", "payment-service")
            .publishPercentiles(0.5, 0.95, 0.99)
            .register(registry);

        this.externalApiTimer = Timer.builder("external_api_duration")
            .description("Time taken for external API calls")
            .tags("service", "payment-service", "api", "payment-gateway")
            .register(registry);
    }

    public PaymentResult processPayment(PaymentRequest request) {
        return paymentProcessingTimer.recordCallable(() -> {
            // Валидация
            validatePayment(request);

            // Вызов внешнего API
            return externalApiTimer.recordCallable(() -> 
                callPaymentGateway(request));
        });
    }

    public PaymentResult processPaymentManual(PaymentRequest request) {
        Timer.Sample sample = Timer.start(meterRegistry);

        try {
            validatePayment(request);
            PaymentResult result = callPaymentGateway(request);
            sample.stop(paymentProcessingTimer);
            return result;
        } catch (Exception e) {
            sample.stop(paymentProcessingTimer);
            throw e;
        }
    }
}
```

## Spring Boot интеграция

### Spring Boot Actuator

Actuator предоставляет production-ready возможности:

```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-actuator</artifactId>
</dependency>
<dependency>
    <groupId>io.micrometer</groupId>
    <artifactId>micrometer-registry-prometheus</artifactId>
</dependency>
```

#### application.yml
```yaml
management:
  endpoints:
    web:
      exposure:
        include: health,info,metrics,prometheus
  endpoint:
    prometheus:
      enabled: true
  metrics:
    export:
      prometheus:
        enabled: true
        step: 10s
    distribution:
      percentiles-histogram:
        http.server.requests: true
      percentiles:
        http.server.requests: 0.5, 0.9, 0.95, 0.99
```

#### Custom Health Indicator
```java
@Component
public class DatabaseHealthIndicator implements HealthIndicator {

    @Autowired
    private DataSource dataSource;

    @Override
    public Health health() {
        try (Connection conn = dataSource.getConnection()) {
            // Проверка соединения
            conn.createStatement().execute("SELECT 1");

            // Дополнительные проверки
            int activeConnections = getActiveConnections(conn);

            return Health.up()
                .withDetail("activeConnections", activeConnections)
                .withDetail("status", "Database is healthy")
                .build();

        } catch (SQLException e) {
            return Health.down()
                .withDetail("error", e.getMessage())
                .withException(e)
                .build();
        }
    }

    private int getActiveConnections(Connection conn) throws SQLException {
        // Получение количества активных соединений
        try (PreparedStatement stmt = conn.prepareStatement(
            "SELECT count(*) FROM pg_stat_activity WHERE state = 'active'")) {
            ResultSet rs = stmt.executeQuery();
            return rs.next() ? rs.getInt(1) : 0;
        }
    }
}
```

### Custom Metrics Endpoint

#### Service с метриками
```java
@Service
public class BusinessMetricsService {

    private final Counter ordersCreated;
    private final Counter ordersCancelled;
    private final Gauge pendingOrders;
    private final Timer orderProcessingTime;

    @Autowired
    public BusinessMetricsService(MeterRegistry registry) {
        this.ordersCreated = Counter.builder("orders_created_total")
            .description("Total number of orders created")
            .register(registry);

        this.ordersCancelled = Counter.builder("orders_cancelled_total")
            .description("Total number of orders cancelled")
            .register(registry);

        this.pendingOrders = Gauge.builder("orders_pending", 
            this, service -> service.getPendingOrdersCount())
            .description("Number of pending orders")
            .register(registry);

        this.orderProcessingTime = Timer.builder("order_processing_duration")
            .description("Time taken to process orders")
            .publishPercentiles(0.5, 0.95)
            .register(registry);
    }

    public Order createOrder(CreateOrderRequest request) {
        return orderProcessingTime.recordCallable(() -> {
            Order order = new Order(request);
            saveOrder(order);
            ordersCreated.increment();
            return order;
        });
    }

    public void cancelOrder(String orderId) {
        Order order = findOrder(orderId);
        order.cancel();
        updateOrder(order);
        ordersCancelled.increment();
    }

    private int getPendingOrdersCount() {
        // Получение количества ожидающих заказов
        return orderRepository.countByStatus(OrderStatus.PENDING);
    }
}
```

## Метрики JVM

### JVM метрики

Micrometer автоматически собирает метрики JVM:

```yaml
management:
  metrics:
    enable:
      jvm: true
      system: true
      http: true
      data: true
    distribution:
      percentiles-histogram:
        jvm.gc.pause: true
        http.server.requests: true
```

#### Доступные метрики JVM

##### Memory
- `jvm_memory_used_bytes` — используемая память
- `jvm_memory_committed_bytes` — зарезервированная память
- `jvm_memory_max_bytes` — максимальная память
- `jvm_memory_used_bytes{area="heap"}` — heap память
- `jvm_memory_used_bytes{area="nonheap"}` — non-heap память

##### Garbage Collection
- `jvm_gc_memory_allocated_bytes_total` — всего выделено памяти
- `jvm_gc_memory_promoted_bytes_total` — promoted память
- `jvm_gc_max_data_size_bytes` — максимальный размер данных
- `jvm_gc_live_data_size_bytes` — размер живых данных
- `jvm_gc_pause_seconds_count` — количество пауз GC
- `jvm_gc_pause_seconds_sum` — суммарное время пауз GC

##### Threads
- `jvm_threads_daemon` — daemon потоки
- `jvm_threads_live` — живые потоки
- `jvm_threads_peak` — пиковое количество потоков
- `jvm_threads_states` — потоки по состояниям

##### Classes
- `jvm_classes_loaded` — загруженные классы
- `jvm_classes_unloaded` — выгруженные классы
- `jvm_classes_current_loaded` — текущие загруженные классы

### Custom JVM метрики

#### Memory Pool метрики
```java
@Configuration
public class JvmMetricsConfig {

    @Autowired
    private MeterRegistry registry;

    @PostConstruct
    public void init() {
        // Метрики для каждого memory pool
        for (MemoryPoolMXBean pool : ManagementFactory.getMemoryPoolMXBeans()) {
            Gauge.builder("jvm_memory_pool_used", pool, 
                p -> p.getUsage().getUsed())
                .tag("pool", pool.getName())
                .register(registry);

            Gauge.builder("jvm_memory_pool_committed", pool, 
                p -> p.getUsage().getCommitted())
                .tag("pool", pool.getName())
                .register(registry);

            Gauge.builder("jvm_memory_pool_max", pool, 
                p -> p.getUsage().getMax())
                .tag("pool", pool.getName())
                .register(registry);
        }

        // Buffer pool метрики
        for (BufferPoolMXBean pool : ManagementFactory.getPlatformMXBeans(BufferPoolMXBean.class)) {
            Gauge.builder("jvm_buffer_pool_used_buffers", pool, 
                BufferPoolMXBean::getCount)
                .tag("pool", pool.getName())
                .register(registry);

            Gauge.builder("jvm_buffer_pool_used_memory", pool, 
                BufferPoolMXBean::getMemoryUsed)
                .tag("pool", pool.getName())
                .register(registry);
        }
    }
}
```

## Бизнес-метрики

### Application метрики

#### HTTP метрики
```java
@Configuration
public class HttpMetricsConfig {

    @Bean
    public TimedAspect timedAspect(MeterRegistry registry) {
        return new TimedAspect(registry);
    }
}

@RestController
public class UserController {

    @Autowired
    private UserService userService;

    @GetMapping("/users/{id}")
    @Timed(value = "user.get", description = "Time taken to get user")
    public User getUser(@PathVariable String id) {
        return userService.findUser(id);
    }

    @PostMapping("/users")
    @Timed(value = "user.create", description = "Time taken to create user",
           percentiles = {0.5, 0.95, 0.99})
    public User createUser(@RequestBody CreateUserRequest request) {
        return userService.createUser(request);
    }

    @GetMapping("/users")
    @Timed(value = "user.list", description = "Time taken to list users")
    public List<User> listUsers(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        
        // Метрики с тегами
        return Timer.builder("user.list.detailed")
            .tags("pageSize", String.valueOf(size))
            .register(meterRegistry)
            .recordCallable(() -> userService.listUsers(page, size));
    }
}
```

#### Database метрики
```java
@Configuration
public class DataSourceMetricsConfig {

    @Bean
    @Primary
    public DataSource dataSource(MeterRegistry registry) {
        HikariDataSource dataSource = new HikariDataSource();
        
        // Настройка HikariCP
        dataSource.setJdbcUrl("jdbc:postgresql://localhost:5432/mydb");
        dataSource.setUsername("user");
        dataSource.setPassword("password");
        
        // Метрики
        dataSource.setMetricRegistry(registry);
        
        return dataSource;
    }
}

// Custom database метрики
@Repository
public class UserRepository {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private MeterRegistry registry;

    public User save(User user) {
        Counter.builder("database_operations_total")
            .tags("operation", "insert", "table", "users")
            .register(registry)
            .increment();

        return Timer.builder("database_operation_duration")
            .tags("operation", "insert", "table", "users")
            .register(registry)
            .recordCallable(() -> {
                // Insert logic
                return doSave(user);
            });
    }

    public List<User> findByStatus(UserStatus status) {
        return Timer.builder("database_query_duration")
            .tags("query", "findByStatus", "table", "users")
            .register(registry)
            .recordCallable(() -> 
                jdbcTemplate.query("SELECT * FROM users WHERE status = ?", 
                    new UserRowMapper(), status.name()));
    }
}
```

#### Cache метрики
```java
@Configuration
public class CacheMetricsConfig {

    @Bean
    public CacheManager cacheManager(MeterRegistry registry) {
        CaffeineCacheManager cacheManager = new CaffeineCacheManager();
        
        // Метрики для Caffeine
        Caffeine<Object, Object> caffeine = Caffeine.newBuilder()
            .recordStats()
            .maximumSize(1000)
            .expireAfterWrite(Duration.ofMinutes(10));
        
        cacheManager.setCaffeine(caffeine);
        
        return cacheManager;
    }
}

@Service
public class CacheService {

    @Autowired
    private CacheManager cacheManager;

    @Autowired
    private MeterRegistry registry;

    public <T> T get(String key, Class<T> type) {
        Cache cache = cacheManager.getCache("users");
        
        // Метрики попаданий/промахов
        Gauge.builder("cache_size", cache, c -> c.getNativeCache().estimatedSize())
            .register(registry);
        
        return cache.get(key, () -> loadFromDatabase(key, type));
    }

    @Cacheable("users")
    public User getUser(String id) {
        return userRepository.findById(id);
    }

    @CacheEvict(value = "users", key = "#user.id")
    public User updateUser(User user) {
        return userRepository.save(user);
    }
}
```

## PromQL запросы

### Основы PromQL

#### Selectors
```promql
# Все метрики HTTP запросов
http_requests_total

# Метрики с конкретными лейблами
http_requests_total{method="GET", status="200"}

# Регулярные выражения
http_requests_total{method=~"GET|POST"}

# Отрицание
http_requests_total{method!="DELETE"}
```

#### Operators
```promql
# Арифметические операторы
http_requests_total * 2
rate(http_requests_total[5m]) / rate(http_errors_total[5m])

# Сравнение
http_requests_total > 100
up == 1

# Логические операторы
up and http_requests_total
rate(http_requests_total[5m]) > 10 or up == 0
```

### Агрегатные функции

#### Rate и Increase
```promql
# Скорость роста счетчика за 5 минут
rate(http_requests_total[5m])

# Общее увеличение за 5 минут
increase(http_requests_total[5m])

# Скорость ошибок
rate(http_requests_total{status=~"5.."}[5m])
```

#### Aggregations
```promql
# Сумма по всем инстансам
sum(http_requests_total)

# Среднее значение
avg(http_response_time_seconds)

# Максимум
max(http_response_time_seconds)

# Минимум
min(http_response_time_seconds)

# Количество
count(http_requests_total)

# Квантили
quantile(0.95, http_response_time_seconds)
quantile(0.99, http_response_time_seconds)
```

### Практические запросы

#### Application Health
```promql
# Uptime сервисов
up{job="my-service"}

# Health check failures
rate(http_requests_total{endpoint="/health", status!="200"}[5m])

# Error rate
rate(http_requests_total{status=~"5.."}[5m]) / rate(http_requests_total[5m])
```

#### Performance
```promql
# Response time percentiles
histogram_quantile(0.95, rate(http_request_duration_seconds_bucket[5m]))

# Throughput
rate(http_requests_total[5m])

# Memory usage
jvm_memory_used_bytes{area="heap"} / jvm_memory_max_bytes{area="heap"}

# CPU usage
rate(process_cpu_user_seconds_total[5m]) * 100
```

#### Business Metrics
```promql
# User registrations per minute
rate(user_registrations_total[5m])

# Order completion rate
rate(orders_completed_total[5m])

# Payment success rate
rate(payments_successful_total[5m]) / rate(payments_total[5m])
```

### Complex Queries

#### Service Dependencies
```promql
# Services that are down
up == 0

# Services with high error rate
(rate(http_requests_total{status=~"5.."}[5m]) / rate(http_requests_total[5m])) > 0.1

# Services with slow responses
histogram_quantile(0.95, rate(http_request_duration_seconds_bucket[5m])) > 2
```

#### Capacity Planning
```promql
# Memory usage trend
predict_linear(jvm_memory_used_bytes[1h], 3600)

# Request growth
rate(http_requests_total[1h])

# Database connection usage
database_connections_active / database_connections_max
```

## Алертинг с Prometheus

### Alert Rules

#### alert_rules.yml
```yaml
groups:
  - name: application_alerts
    rules:
      - alert: HighErrorRate
        expr: rate(http_requests_total{status=~"5.."}[5m]) / rate(http_requests_total[5m]) > 0.1
        for: 5m
        labels:
          severity: warning
          service: "{{ $labels.service }}"
        annotations:
          summary: "High error rate on {{ $labels.service }}"
          description: "Error rate is {{ $value | printf \"%.2f\" }}% for {{ $labels.service }}"

      - alert: ServiceDown
        expr: up == 0
        for: 2m
        labels:
          severity: critical
          service: "{{ $labels.job }}"
        annotations:
          summary: "Service {{ $labels.job }} is down"
          description: "Service {{ $labels.job }} has been down for more than 2 minutes"

      - alert: HighMemoryUsage
        expr: jvm_memory_used_bytes{area="heap"} / jvm_memory_max_bytes{area="heap"} > 0.9
        for: 10m
        labels:
          severity: warning
          service: "{{ $labels.service }}"
        annotations:
          summary: "High memory usage on {{ $labels.service }}"
          description: "Memory usage is {{ $value | printf \"%.1f\" }}%"

      - alert: SlowResponseTime
        expr: histogram_quantile(0.95, rate(http_request_duration_seconds_bucket[5m])) > 5
        for: 5m
        labels:
          severity: warning
          service: "{{ $labels.service }}"
        annotations:
          summary: "Slow response time on {{ $labels.service }}"
          description: "95th percentile response time is {{ $value }}s"
```

### Recording Rules

#### recording_rules.yml
```yaml
groups:
  - name: application_recording_rules
    rules:
      - record: job:http_requests_total:rate5m
        expr: rate(http_requests_total[5m])

      - record: job:http_requests_total:rate1h
        expr: rate(http_requests_total[1h])

      - record: job:http_errors_total:rate5m
        expr: rate(http_requests_total{status=~"5.."}[5m])

      - record: job:http_error_rate:ratio
        expr: rate(http_requests_total{status=~"5.."}[5m]) / rate(http_requests_total[5m])

      - record: job:http_response_time:95p
        expr: histogram_quantile(0.95, rate(http_request_duration_seconds_bucket[5m]))

      - record: job:jvm_memory_usage:ratio
        expr: jvm_memory_used_bytes{area="heap"} / jvm_memory_max_bytes{area="heap"}

      - record: job:jvm_gc_pause_time:rate
        expr: rate(jvm_gc_pause_seconds_sum[5m])
```

## Service Discovery

### Kubernetes Service Discovery

#### Pod annotations
```yaml
apiVersion: apps/v1
kind: Deployment
metadata:
  name: my-app
spec:
  template:
    metadata:
      annotations:
        prometheus.io/scrape: "true"
        prometheus.io/port: "8080"
        prometheus.io/path: "/actuator/prometheus"
    spec:
      containers:
      - name: app
        image: my-app:latest
        ports:
        - containerPort: 8080
```

#### Prometheus configuration for Kubernetes
```yaml
scrape_configs:
  - job_name: 'kubernetes-services'
    kubernetes_sd_configs:
      - role: service
    relabel_configs:
      - source_labels: [__meta_kubernetes_service_annotation_prometheus_io_scrape]
        action: keep
        regex: true
      - source_labels: [__meta_kubernetes_service_annotation_prometheus_io_scheme]
        action: replace
        target_label: __scheme__
        regex: (https?)
      - source_labels: [__meta_kubernetes_service_annotation_prometheus_io_path]
        action: replace
        target_label: __metrics_path__
        regex: (.+)
      - source_labels: [__address__, __meta_kubernetes_service_annotation_prometheus_io_port]
        action: replace
        regex: ([^:]+)(?::\d+)?;(\d+)
        replacement: $1:$2
        target_label: __address__

  - job_name: 'kubernetes-pods'
    kubernetes_sd_configs:
      - role: pod
    relabel_configs:
      - source_labels: [__meta_kubernetes_pod_annotation_prometheus_io_scrape]
        action: keep
        regex: true
      - source_labels: [__meta_kubernetes_pod_annotation_prometheus_io_path]
        action: replace
        target_label: __metrics_path__
        regex: (.+)
      - source_labels: [__address__, __meta_kubernetes_pod_annotation_prometheus_io_port]
        action: replace
        regex: ([^:]+)(?::\d+)?;(\d+)
        replacement: $1:$2
        target_label: __address__
```

### Consul Service Discovery

#### Prometheus configuration
```yaml
scrape_configs:
  - job_name: 'consul-services'
    consul_sd_configs:
      - server: 'localhost:8500'
        services: ['web', 'api', 'database']
    relabel_configs:
      - source_labels: [__meta_consul_service]
        target_label: service
      - source_labels: [__meta_consul_tags]
        target_label: tags
      - source_labels: [__meta_consul_service_metadata_prometheus_path]
        target_label: __metrics_path__
        regex: (.+)
        replacement: $1
      - source_labels: [__address__]
        regex: '(.+):\d+'
        replacement: '$1:9090'
        target_label: __address__
```

## High Availability

### Federation

#### Federation configuration
```yaml
# Regional Prometheus
scrape_configs:
  - job_name: 'federation'
    honor_labels: true
    metrics_path: '/federate'
    params:
      'match[]':
        - '{job="prometheus"}'
        - '{__name__=~"job:.*"}'
        - '{__name__=~"node.*"}'
    static_configs:
      - targets:
        - 'localhost:9090'
```

#### Global Prometheus
```yaml
scrape_configs:
  - job_name: 'regional-prometheus-1'
    static_configs:
      - targets: ['regional1:9090']
    scrape_interval: 15s
    metrics_path: '/federate'
    params:
      'match[]':
        - '{job="regional-job"}'
        - '{__name__=~"regional:.*"}'

  - job_name: 'regional-prometheus-2'
    static_configs:
      - targets: ['regional2:9090']
    scrape_interval: 15s
    honor_labels: true
```

### Thanos

#### Thanos components
```yaml
# Thanos Sidecar
apiVersion: apps/v1
kind: Deployment
metadata:
  name: prometheus-thanos-sidecar
spec:
  template:
    spec:
      containers:
      - name: thanos-sidecar
        image: quay.io/thanos/thanos:latest
        args:
        - sidecar
        - --prometheus.url=http://localhost:9090
        - --grpc-server-tls-client-ca-file=/etc/thanos/certs/ca.crt
        - --grpc-server-tls-certificate=/etc/thanos/certs/client.crt
        - --grpc-server-tls-key=/etc/thanos/certs/client.key
        - --grpc-server-tls-private-key-password=pass
        ports:
        - name: grpc
          containerPort: 10901
        - name: http
          containerPort: 10902
```

## Best Practices

### 1. Naming conventions

#### Metric names
```promql
# Хорошие имена
http_requests_total
database_connections_active
jvm_memory_used_bytes
user_registrations_total
payment_processing_duration_seconds

# Плохие имена
requests
db_conn
mem_used
users
payment_time
```

#### Labels
```promql
# Хорошие лейблы
http_requests_total{method="GET", status="200", endpoint="/api/users"}
database_connections_active{database="postgresql", pool="main"}
jvm_memory_used_bytes{area="heap", pool="eden"}

# Избегать
http_requests_total{type="incoming", protocol="http"}  # Слишком много лейблов
database_connections_active{host="db1", port="5432"}    # Высокая кардинальность
```

### 2. Cardinality management

#### Избегать high cardinality
```java
// Плохо: Высокая кардинальность
Counter.builder("user_request_total")
    .tags("userId", userId, "timestamp", timestamp, "ip", ipAddress)
    .register(registry);

// Хорошо: Низкая кардинальность
Counter.builder("http_requests_total")
    .tags("method", method, "endpoint", endpoint, "status", status)
    .register(registry);

// Дополнительные метрики для детального анализа
Gauge.builder("active_users", this, service -> service.getActiveUserCount())
    .register(registry);
```

### 3. Histogram vs Summary

#### Когда использовать Histogram
```java
// Histogram для агрегации на стороне сервера
Histogram requestDuration = Histogram.builder("http_request_duration_seconds")
    .description("HTTP request duration")
    .buckets(0.1, 0.5, 1.0, 2.5, 5.0, 10.0, 30.0, 60.0)
    .register(registry);

// Подходит для:
// - Распределенный агрегации
// - PromQL quantile() функций
// - Сложных агрегаций
```

#### Когда использовать Summary
```java
// Summary для клиентских квантилей
Summary requestDuration = Summary.builder("http_request_duration_seconds")
    .description("HTTP request duration")
    .quantile(0.5, 0.05)   // median with 5% error
    .quantile(0.9, 0.01)   // 90th percentile with 1% error
    .quantile(0.99, 0.001) // 99th percentile with 0.1% error
    .register(registry);

// Подходит для:
// - Точных квантилей
// - Клиентских приложений
// - Когда нужна точность
```

### 4. Resource usage

#### Efficient scraping
```yaml
# Оптимизация scrape интервалов
scrape_configs:
  - job_name: 'fast-changing-metrics'
    scrape_interval: 15s
    static_configs:
      - targets: ['app:8080']

  - job_name: 'slow-changing-metrics'
    scrape_interval: 5m
    static_configs:
      - targets: ['system:9100']
```

#### Storage optimization
```yaml
# Настройки хранения
global:
  scrape_interval: 15s
  evaluation_interval: 15s

# Настройки хранения
storage:
  tsdb:
    retention:
      time: 30d
    wal-compression: true
    no-lockfile: true
```

## Troubleshooting

### Распространенные проблемы

#### Targets not showing up
```bash
# Проверить статус targets
curl http://prometheus:9090/api/v1/targets

# Проверить конфигурацию
curl http://prometheus:9090/api/v1/status/config

# Проверить service discovery
curl http://prometheus:9090/api/v1/service-discovery
```

#### Missing metrics
```bash
# Проверить endpoint метрик
curl http://app:8080/actuator/prometheus

# Проверить scrape ошибки
curl http://prometheus:9090/api/v1/targets | jq '.data.activeTargets[] | select(.health != "up")'

# Проверить правила релейблинга
curl http://prometheus:9090/api/v1/targets | jq '.data.activeTargets[0].labels'
```

#### High memory usage
```yaml
# Ограничение памяти
storage:
  tsdb:
    max-block-duration: 2h
    min-block-duration: 2h
    retention:
      time: 30d
      size: 10GB
```

#### Slow queries
```promql
# Оптимизация запросов
# Вместо:
rate(http_requests_total[1h])

# Использовать:
rate(http_requests_total[5m])
```

### Debug techniques

#### Query debugging
```bash
# Проверить синтаксис запроса
curl "http://prometheus:9090/api/v1/query?query=up"

# Проверить range query
curl "http://prometheus:9090/api/v1/query_range?query=up&start=1609459200&end=1609462800&step=60"

# Проверить метрики
curl "http://prometheus:9090/api/v1/series?match[]=up"
```

#### Alert debugging
```bash
# Проверить состояние алертов
curl http://prometheus:9090/api/v1/alerts

# Проверить правила
curl http://prometheus:9090/api/v1/rules

# Проверить алерты в Alertmanager
curl http://alertmanager:9093/api/v2/alerts
```

#### Performance monitoring
```promql
# Prometheus performance метрики
prometheus_tsdb_head_samples_appended_total
prometheus_tsdb_head_series_created_total
prometheus_evaluator_duration_seconds
rate(prometheus_evaluator_iterations_total[5m])
```

### Logging for Prometheus

#### Debug logging
```java
@Configuration
public class PrometheusLoggingConfig {

    @Autowired
    private MeterRegistry registry;

    @Bean
    public LoggingMeterRegistry loggingRegistry() {
        LoggingMeterRegistry loggingRegistry = new LoggingMeterRegistry();
        loggingRegistry.start();
        return loggingRegistry;
    }

    @Scheduled(fixedRate = 30000)
    public void logMetrics() {
        registry.forEachMeter(meter -> {
            meter.measure().forEach(measurement -> {
                logger.debug("Metric: {} = {}", 
                    meter.getId().getName(), measurement.getValue());
            });
        });
    }
}
```

## Заключение

**Prometheus** — это мощная система мониторинга, которая предоставляет комплексные возможности для наблюдения за Java-приложениями. Благодаря интеграции с Micrometer и Spring Boot, Prometheus легко интегрируется в экосистему Java.

### Ключевые возможности:

1. **Модель данных на основе временных рядов** — эффективное хранение и анализ метрик
2. **PromQL** — мощный язык запросов для анализа данных
3. **Service Discovery** — автоматическое обнаружение сервисов
4. **Алертинг** — встроенная система оповещений
5. **Micrometer интеграция** — vendor-neutral API для метрик
6. **Spring Boot Actuator** — готовые production-ready метрики

### Архитектурные преимущества:

#### Scalability:
- **Горизонтальное масштабирование** — federation и Thanos
- **Service Discovery** — автоматическое обнаружение targets
- **Pull модель** — надежный сбор метрик
- **Federation** — глобальный мониторинг

#### Reliability:
- **Local storage** — отказоустойчивое хранение
- **Replication** — дублирование данных
- **Backup/Restore** — восстановление данных
- **High Availability** — отказоустойчивость

### Когда использовать Prometheus:

✅ **Микросервисная архитектура** — мониторинг распределенных систем
✅ **Kubernetes** — нативная интеграция с оркестрацией
✅ **Cloud-native applications** — облачные приложения
✅ **Performance monitoring** — анализ производительности
✅ **Business metrics** — бизнес-метрики
✅ **Infrastructure monitoring** — мониторинг инфраструктуры
✅ **Alerting** — система оповещений
✅ **Historical analysis** — анализ исторических данных

### Когда НЕ использовать:

❌ **Simple applications** — для небольших приложений без комплексного мониторинга
❌ **Real-time requirements** — если нужна суб-секундная точность
❌ **High-frequency trading** — для сверхвысоких частот
❌ **Legacy systems** — сложная интеграция со старыми системами
❌ **Resource constraints** — ограниченные ресурсы для хранения метрик
❌ **Non-time-series data** — для не временных данных

### Best practices:

1. **Правильное именование метрик** — понятные имена и лейблы
2. **Управление кардинальностью** — избегание high cardinality
3. **Оптимальные типы метрик** — Counter, Gauge, Histogram, Summary
4. **Service Discovery** — автоматическое обнаружение сервисов
5. **Алертинг** — своевременные оповещения о проблемах
6. **Resource optimization** — эффективное использование ресурсов
7. **Security** — защита endpoints метрик
8. **Monitoring** — мониторинг самого Prometheus

### Интеграция с другими инструментами:

- **Grafana** — визуализация метрик
- **Alertmanager** — маршрутизация алертов
- **Thanos** — long-term storage и global view
- **Jaeger/Zipkin** — distributed tracing
- **ELK Stack** — логи и анализ
- **Kubernetes** — оркестрация контейнеров

### Метрики vs Логи vs Трассировка:

- **Метрики (Prometheus)** — количественные данные для мониторинга трендов
- **Логи** — качественные данные для диагностики конкретных событий
- **Трассировка** — анализ flow через компоненты системы

Prometheus является фундаментом современного мониторинга и observability. Правильная настройка и использование Prometheus обеспечивает глубокое понимание работы приложений и своевременное реагирование на проблемы. 🚀

**Далее: Grafana для визуализации метрик**