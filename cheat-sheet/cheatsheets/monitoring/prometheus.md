# Prometheus

Prometheus - это система мониторинга и алертинга с открытым исходным кодом, разработанная для надежного и масштабируемого мониторинга. Prometheus собирает метрики с целевых объектов, сохраняет их в виде временных рядов и предоставляет мощный язык запросов для анализа данных.

## Содержание
- [Основы Prometheus](#основы-prometheus)
- [Конфигурация Prometheus](#конфигурация-prometheus)
- [PromQL - язык запросов](#promql---язык-запросов)
- [Recording Rules](#recording-rules)
- [Alerting Rules](#alerting-rules)
- [Интеграция с приложениями](#интеграция-с-приложениями)
- [Визуализация и дашборды](#визуализация-и-дашборды)
- [Best Practices](#best-practices)
- [Troubleshooting](#troubleshooting)

## Основы Prometheus

### Архитектура Prometheus
```yaml
# Основные компоненты:
# - Prometheus Server: сбор, хранение и обработка метрик
# - Pushgateway: прием метрик от batch jobs
# - Alertmanager: обработка алертов
# - Exporters: сбор метрик с различных систем
# - Service Discovery: автоматическое обнаружение целей
```

### Метрики в Prometheus

#### Типы метрик
```yaml
# Counter - монотонно возрастающее значение
http_requests_total{endpoint="/api/users", method="GET"} 1024

# Gauge - значение, которое может увеличиваться или уменьшаться
memory_usage_bytes{instance="web-server-01"} 1073741824

# Histogram - распределение значений с buckets
http_request_duration_seconds_bucket{le="0.1"} 120
http_request_duration_seconds_bucket{le="0.5"} 180
http_request_duration_seconds_bucket{le="1.0"} 200
http_request_duration_seconds_count 250
http_request_duration_seconds_sum 125.5

# Summary - квантили и сумма
http_response_size_bytes{quantile="0.5"} 512
http_response_size_bytes{quantile="0.9"} 1024
http_response_size_bytes{quantile="0.99"} 2048
http_response_size_bytes_count 1000
http_response_size_bytes_sum 524288
```

### Установка и запуск

#### Docker
```bash
# Запуск Prometheus в Docker
docker run -d \
  --name prometheus \
  -p 9090:9090 \
  -v $(pwd)/prometheus.yml:/etc/prometheus/prometheus.yml \
  prom/prometheus

# С конфигурацией
docker run -d \
  --name prometheus \
  -p 9090:9090 \
  -v $(pwd)/config:/etc/prometheus \
  prom/prometheus \
  --config.file=/etc/prometheus/prometheus.yml \
  --storage.tsdb.path=/prometheus \
  --web.console.libraries=/etc/prometheus/console_libraries \
  --web.console.templates=/etc/prometheus/consoles \
  --storage.tsdb.retention.time=200h \
  --web.enable-lifecycle
```

#### Systemd Service
```bash
# Скачивание и установка
wget https://github.com/prometheus/prometheus/releases/download/v2.40.0/prometheus-2.40.0.linux-amd64.tar.gz
tar xvfz prometheus-*.tar.gz
cd prometheus-*

# Создание пользователя
sudo useradd --no-create-home --shell /bin/false prometheus
sudo mkdir /etc/prometheus
sudo mkdir /var/lib/prometheus
sudo chown prometheus:prometheus /etc/prometheus
sudo chown prometheus:prometheus /var/lib/prometheus

# Копирование файлов
sudo cp prometheus /usr/local/bin/
sudo cp promtool /usr/local/bin/
sudo chown prometheus:prometheus /usr/local/bin/prometheus
sudo chown prometheus:prometheus /usr/local/bin/promtool

# Создание systemd service
sudo tee /etc/systemd/system/prometheus.service > /dev/null <<EOF
[Unit]
Description=Prometheus
Wants=network-online.target
After=network-online.target

[Service]
User=prometheus
Group=prometheus
Type=simple
ExecStart=/usr/local/bin/prometheus \
  --config.file /etc/prometheus/prometheus.yml \
  --storage.tsdb.path /var/lib/prometheus/ \
  --web.console.templates=/etc/prometheus/consoles \
  --web.console.libraries=/etc/prometheus/console_libraries

[Install]
WantedBy=multi-user.target
EOF

sudo systemctl daemon-reload
sudo systemctl start prometheus
sudo systemctl enable prometheus
```

## Конфигурация Prometheus

### Основной конфигурационный файл
```yaml
# prometheus.yml
global:
  scrape_interval: 15s        # Частота сбора метрик
  evaluation_interval: 15s    # Частота оценки правил
  scrape_timeout: 10s         # Таймаут на сбор метрик

rule_files:
  - "alert_rules.yml"         # Файлы с правилами алертов
  - "recording_rules.yml"     # Файлы с recording rules

alerting:
  alertmanagers:
    - static_configs:
        - targets:
          - alertmanager:9093

scrape_configs:
  # Собственный Prometheus
  - job_name: 'prometheus'
    static_configs:
      - targets: ['localhost:9090']

  # Node Exporter
  - job_name: 'node'
    static_configs:
      - targets: ['node-exporter:9100']

  # Spring Boot приложение
  - job_name: 'spring-boot'
    metrics_path: '/actuator/prometheus'
    static_configs:
      - targets: ['spring-app:8080']

  # Docker containers
  - job_name: 'docker'
    static_configs:
      - targets: ['docker-exporter:9323']

  # Kubernetes service discovery
  - job_name: 'kubernetes-services'
    kubernetes_sd_configs:
      - role: service
    relabel_configs:
      - source_labels: [__meta_kubernetes_service_annotation_prometheus_io_scrape]
        action: keep
        regex: true
      - source_labels: [__meta_kubernetes_service_annotation_prometheus_io_path]
        action: replace
        target_label: __metrics_path__
        regex: (.+)
      - source_labels: [__address__, __meta_kubernetes_service_annotation_prometheus_io_port]
        action: replace
        target_label: __address__
        regex: (.+?)(?::\d+)?;(\d+)
        replacement: $1:$2
```

### Service Discovery

#### Kubernetes
```yaml
# Kubernetes service discovery
scrape_configs:
  - job_name: 'kubernetes-pods'
    kubernetes_sd_configs:
      - role: pod
    relabel_configs:
      # Скрапить только pods с аннотацией
      - source_labels: [__meta_kubernetes_pod_annotation_prometheus_io_scrape]
        action: keep
        regex: true
      # Порт из аннотации
      - source_labels: [__meta_kubernetes_pod_annotation_prometheus_io_port]
        action: replace
        target_label: __address__
        regex: (\d+)
        replacement: $1
      # Путь к метрикам
      - source_labels: [__meta_kubernetes_pod_annotation_prometheus_io_path]
        action: replace
        target_label: __metrics_path__
        regex: (.+)
      # Добавить namespace как label
      - source_labels: [__meta_kubernetes_namespace]
        action: replace
        target_label: namespace
      - source_labels: [__meta_kubernetes_pod_name]
        action: replace
        target_label: pod_name

  - job_name: 'kubernetes-nodes'
    kubernetes_sd_configs:
      - role: node
    relabel_configs:
      - action: labelmap
        regex: __meta_kubernetes_node_label_(.+)
      - source_labels: [__meta_kubernetes_node_name]
        regex: (.+)
        action: replace
        target_label: __address__
        replacement: $1:9100
```

#### AWS EC2
```yaml
# AWS EC2 service discovery
scrape_configs:
  - job_name: 'ec2-instances'
    ec2_sd_configs:
      - region: us-east-1
        port: 9100
        filters:
          - name: tag:Environment
            values: [production]
          - name: tag:Application
            values: [web-server]
    relabel_configs:
      - source_labels: [__meta_ec2_instance_id]
        target_label: instance_id
      - source_labels: [__meta_ec2_availability_zone]
        target_label: availability_zone
      - source_labels: [__meta_ec2_tag_Name]
        target_label: instance_name
```

#### Consul
```yaml
# Consul service discovery
scrape_configs:
  - job_name: 'consul-services'
    consul_sd_configs:
      - server: 'consul:8500'
        services: ['web', 'api', 'database']
    relabel_configs:
      - source_labels: [__meta_consul_service]
        target_label: service
      - source_labels: [__meta_consul_node]
        target_label: node
      - source_labels: [__address__]
        regex: '(.*):\d+'
        replacement: '${1}:9090'
        target_label: __address__
```

## PromQL - язык запросов

### Основные операторы
```promql
# Арифметические операторы
cpu_usage + memory_usage
response_time * 1000
total_requests / 60

# Операторы сравнения
cpu_usage > 80
memory_usage < 1024
response_time >= 0.5

# Логические операторы
up == 1 and cpu_usage < 80
http_requests_total > 100 or error_rate > 0.05

# Агрегатные функции
sum(cpu_usage)                    # Сумма
avg(response_time)               # Среднее
max(memory_usage)               # Максимум
min(disk_usage)                  # Минимум
count(http_requests_total)       # Количество
stddev(response_time)           # Стандартное отклонение
quantile(0.95, response_time)    # 95-й перцентиль

# Группировка
sum by (instance) (cpu_usage)           # По instance
avg by (job, instance) (memory_usage)   # По job и instance
max without (endpoint) (response_time)  # Исключая endpoint
```

### Продвинутые запросы
```promql
# Rate и increase
rate(http_requests_total[5m])           # Скорость роста за 5 минут
increase(http_errors_total[1h])         # Прирост за час

# Histogram квантили
histogram_quantile(0.95, rate(http_request_duration_bucket[10m]))

# Скользящие окна
avg_over_time(cpu_usage[5m])            # Среднее за последние 5 минут
max_over_time(memory_usage[1h])         # Максимум за последний час

# Предикаты
up offset 1h                            # Значение час назад
cpu_usage unless memory_usage > 80     # CPU если память не > 80%

# Операторы множеств
http_requests_total{job="api"} and http_requests_total{job="web"}
cpu_usage{instance=~"web.*"}            # Регулярные выражения
memory_usage{environment!="test"}       # Отрицание

# Метки и селекторы
sum by (service) (rate(http_requests_total{service=~"api.*"}[5m]))
avg by (instance) (cpu_usage{datacenter="us-west"})
```

### Метрики приложения
```promql
# HTTP метрики
rate(http_requests_total{job="web-server"}[5m])
histogram_quantile(0.95, rate(http_request_duration_seconds_bucket[5m]))

# База данных
rate(database_connections_active[5m])
avg_over_time(database_query_duration_seconds[10m])

# JVM метрики
jvm_memory_used_bytes / jvm_memory_max_bytes * 100
rate(jvm_gc_collection_seconds_count[5m])

# Бизнес метрики
rate(orders_total{status="completed"}[1h])
avg(response_time) by (endpoint)
```

## Recording Rules

### Создание recording rules
```yaml
# recording_rules.yml
groups:
  - name: example_rules
    rules:
      # Агрегированные метрики
      - record: job:http_requests_total:rate5m
        expr: sum(rate(http_requests_total[5m])) by (job)

      # Метрики производительности
      - record: instance:cpu_usage:rate5m
        expr: avg(rate(cpu_usage_percent[5m])) by (instance)

      # Метрики здоровья
      - record: instance:up:avg
        expr: avg_over_time(up[5m])

      # Композитные метрики
      - record: service:response_time:p95
        expr: histogram_quantile(0.95, sum(rate(http_request_duration_seconds_bucket[10m])) by (le, service))

      # Метрики нагрузки
      - record: cluster:cpu_usage
        expr: avg(cpu_usage_percent) by (cluster)

      # Метрики доступности
      - record: service:availability
        expr: (1 - (rate(http_requests_total{status=~"5.."}[5m]) / rate(http_requests_total[5m]))) * 100
```

### Оптимизация recording rules
```yaml
# Оптимизированные rules для производительности
groups:
  - name: optimized_rules
    interval: 30s  # Частота вычисления

    rules:
      # Избегать сложных вычислений в rules
      - record: api:requests:rate1m
        expr: rate(http_requests_total{job="api"}[1m])

      # Предварительно агрегировать
      - record: service:errors:rate5m
        expr: sum(rate(http_requests_total{status=~"5.."}[5m])) by (service)

      # Использовать offset для сравнения
      - record: cpu_usage:increase:1h
        expr: cpu_usage - cpu_usage offset 1h
```

## Alerting Rules

### Настройка алертов
```yaml
# alert_rules.yml
groups:
  - name: example_alerts
rules:

      # Алерты на недоступность
      - alert: InstanceDown
        expr: up == 0
        for: 5m
        labels:
          severity: critical
        annotations:
          summary: "Instance {{ $labels.instance }} is down"
          description: "Instance {{ $labels.instance }} has been down for more than 5 minutes."

      # Алерты на высокую загрузку CPU
      - alert: HighCpuUsage
        expr: cpu_usage_percent > 90
        for: 10m
        labels:
          severity: warning
        annotations:
          summary: "High CPU usage on {{ $labels.instance }}"
          description: "CPU usage is {{ $value }}% on {{ $labels.instance }}"

      # Алерты на высокую загрузку памяти
      - alert: HighMemoryUsage
        expr: (1 - system_memory_available / system_memory_total) * 100 > 95
        for: 5m
        labels:
          severity: critical
        annotations:
          summary: "High memory usage on {{ $labels.instance }}"
          description: "Memory usage is {{ $value }}% on {{ $labels.instance }}"

      # Алерты на ошибки HTTP
      - alert: HighHttpErrorRate
        expr: rate(http_requests_total{status=~"5.."}[5m]) / rate(http_requests_total[5m]) * 100 > 5
        for: 5m
        labels:
          severity: warning
        annotations:
          summary: "High HTTP error rate on {{ $labels.instance }}"
          description: "HTTP error rate is {{ $value }}% on {{ $labels.instance }}"

      # Алерты на медленные запросы
      - alert: SlowResponseTime
        expr: histogram_quantile(0.95, rate(http_request_duration_seconds_bucket[5m])) > 2
        for: 10m
        labels:
          severity: warning
        annotations:
          summary: "Slow response time on {{ $labels.service }}"
          description: "95th percentile response time is {{ $value }}s on {{ $labels.service }}"

      # Алерты на дисковое пространство
      - alert: LowDiskSpace
        expr: (disk_total - disk_free) / disk_total * 100 > 90
        for: 15m
        labels:
          severity: warning
        annotations:
          summary: "Low disk space on {{ $labels.instance }}"
          description: "Disk usage is {{ $value }}% on {{ $labels.instance }}"

      # Алерты на недоступность сервисов
      - alert: ServiceDown
        expr: count(up{job="my-service"} == 0) / count(up{job="my-service"}) * 100 > 50
        for: 5m
        labels:
          severity: critical
        annotations:
          summary: "Service {{ $labels.job }} is partially down"
          description: "{{ $value }}% of {{ $labels.job }} instances are down"
```

## Интеграция с приложениями

### Spring Boot с Micrometer
```java
@SpringBootApplication
@EnablePrometheusMetrics
public class Application {

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }
}

// Конфигурация метрик
@Configuration
public class MetricsConfig {

    @Bean
    MeterRegistryCustomizer<MeterRegistry> metricsCommonTags() {
        return registry -> registry.config()
            .commonTags("application", "my-app")
            .commonTags("version", "1.0.0");
    }

    @Bean
    public TimedAspect timedAspect(MeterRegistry registry) {
        return new TimedAspect(registry);
    }
}

// Кастомные метрики
@Service
public class OrderService {

    private final Counter ordersCreated;
    private final Counter ordersCancelled;
    private final Timer orderProcessingTime;
    private final Gauge ordersInProgress;

    public OrderService(MeterRegistry registry) {
        this.ordersCreated = Counter.builder("orders_created_total")
            .description("Total number of orders created")
            .register(registry);

        this.ordersCancelled = Counter.builder("orders_cancelled_total")
            .description("Total number of orders cancelled")
            .register(registry);

        this.orderProcessingTime = Timer.builder("order_processing_duration")
            .description("Time taken to process orders")
            .register(registry);

        this.ordersInProgress = Gauge.builder("orders_in_progress", this, OrderService::getOrdersInProgress)
            .description("Number of orders currently being processed")
            .register(registry);
    }

    @Timed(value = "order_creation_time", description = "Time taken to create an order")
    public Order createOrder(CreateOrderRequest request) {
        ordersCreated.increment();

        Timer.Sample sample = Timer.start();
        try {
            // Business logic
            Order order = new Order(request.getCustomerId(), request.getAmount());
            processOrder(order);
            return order;
        } catch (Exception e) {
            ordersCancelled.increment();
            throw e;
        } finally {
            sample.stop(orderProcessingTime);
        }
    }

    private int getOrdersInProgress() {
        // Return current number of orders being processed
        return 5; // placeholder
    }
}

// HTTP метрики
@RestController
public class MetricsController {

    @GetMapping("/api/orders/{id}")
    @Timed(value = "http_requests", extraTags = {"endpoint", "/api/orders/{id}"})
    public Order getOrder(@PathVariable Long id) {
        // Implementation
        return new Order(id, "customer", 100.0);
    }

    @PostMapping("/api/orders")
    @Timed(value = "http_requests", extraTags = {"endpoint", "/api/orders", "method", "POST"})
    public Order createOrder(@RequestBody CreateOrderRequest request) {
        // Implementation
        return new Order(1L, request.getCustomerId(), request.getAmount());
    }
}
```

### Node.js с prom-client
```javascript
const express = require('express');
const promClient = require('prom-client');
const app = express();

// Создание registry
const register = new promClient.Registry();

// Добавление default метрик
promClient.collectDefaultMetrics({ register });

// Кастомные метрики
const httpRequestsTotal = new promClient.Counter({
  name: 'http_requests_total',
  help: 'Total number of HTTP requests',
  labelNames: ['method', 'route', 'status_code'],
  registers: [register]
});

const httpRequestDuration = new promClient.Histogram({
  name: 'http_request_duration_seconds',
  help: 'Duration of HTTP requests in seconds',
  labelNames: ['method', 'route'],
  buckets: [0.1, 0.5, 1, 2, 5],
  registers: [register]
});

const activeConnections = new promClient.Gauge({
  name: 'active_connections',
  help: 'Number of active connections',
  registers: [register]
});

// Middleware для метрик
app.use((req, res, next) => {
  const start = Date.now();

  res.on('finish', () => {
    const duration = (Date.now() - start) / 1000;

    httpRequestsTotal
      .labels(req.method, req.route?.path || req.path, res.statusCode)
      .inc();

    httpRequestDuration
      .labels(req.method, req.route?.path || req.path)
      .observe(duration);
  });

  next();
});

// Routes
app.get('/api/users', (req, res) => {
  // Simulate database query
  setTimeout(() => {
    res.json({ users: [{ id: 1, name: 'John' }] });
  }, Math.random() * 100);
});

app.get('/metrics', async (req, res) => {
  res.set('Content-Type', register.contentType);
  res.end(await register.metrics());
});

// WebSocket connections tracking
const WebSocket = require('ws');
const wss = new WebSocket.Server({ port: 8081 });

wss.on('connection', (ws) => {
  activeConnections.inc();

  ws.on('close', () => {
    activeConnections.dec();
  });
});

app.listen(3000, () => {
  console.log('Server running on port 3000');
});
```

### Go с prometheus client
```go
package main

import (
    "net/http"
    "time"
    "strconv"
    "github.com/prometheus/client_golang/prometheus"
    "github.com/prometheus/client_golang/prometheus/promauto"
    "github.com/prometheus/client_golang/prometheus/promhttp"
)

// Метрики
var (
    httpRequestsTotal = promauto.NewCounterVec(
        prometheus.CounterOpts{
            Name: "http_requests_total",
            Help: "Total number of HTTP requests",
        },
        []string{"method", "endpoint", "status"},
    )

    httpRequestDuration = promauto.NewHistogramVec(
        prometheus.HistogramOpts{
            Name: "http_request_duration_seconds",
            Help: "HTTP request duration in seconds",
            Buckets: prometheus.DefBuckets,
        },
        []string{"method", "endpoint"},
    )

    activeConnections = promauto.NewGauge(
        prometheus.GaugeOpts{
            Name: "active_connections",
            Help: "Number of active connections",
        },
    )
)

// Middleware для метрик
func metricsMiddleware(next http.HandlerFunc) http.HandlerFunc {
    return func(w http.ResponseWriter, r *http.Request) {
        start := time.Now()

        // Создание response writer wrapper для захвата status code
        rw := &responseWriter{ResponseWriter: w, statusCode: 200}

        next.ServeHTTP(rw, r)

        duration := time.Since(start).Seconds()

        httpRequestsTotal.WithLabelValues(
            r.Method,
            r.URL.Path,
            strconv.Itoa(rw.statusCode),
        ).Inc()

        httpRequestDuration.WithLabelValues(
            r.Method,
            r.URL.Path,
        ).Observe(duration)
    }
}

type responseWriter struct {
    http.ResponseWriter
    statusCode int
}

func (rw *responseWriter) WriteHeader(code int) {
    rw.statusCode = code
    rw.ResponseWriter.WriteHeader(code)
}

// HTTP handlers
func usersHandler(w http.ResponseWriter, r *http.Request) {
    // Simulate work
    time.Sleep(time.Duration(rand.Intn(100)) * time.Millisecond)

    w.Header().Set("Content-Type", "application/json")
    w.Write([]byte(`{"users": [{"id": 1, "name": "John"}]}`))
}

func healthHandler(w http.ResponseWriter, r *http.Request) {
    w.WriteHeader(http.StatusOK)
    w.Write([]byte("OK"))
}

func main() {
    // Регистрация default метрик
    prometheus.MustRegister(prometheus.NewBuildInfoCollector())

    // Routes
    http.HandleFunc("/api/users", metricsMiddleware(usersHandler))
    http.HandleFunc("/health", healthHandler)
    http.Handle("/metrics", promhttp.Handler())

    // WebSocket server для демонстрации active connections
    // (реализация опущена для краткости)

    http.ListenAndServe(":8080", nil)
}
```

## Визуализация и дашборды

### Grafana интеграция
```yaml
# Grafana datasource configuration
apiVersion: 1
datasources:
  - name: Prometheus
    type: prometheus
    access: proxy
    url: http://prometheus:9090
    isDefault: true
    editable: true

# Пример дашборда JSON
{
  "dashboard": {
    "title": "System Monitoring",
    "tags": ["system", "monitoring"],
    "timezone": "browser",
    "panels": [
      {
        "title": "CPU Usage",
        "type": "graph",
        "targets": [
          {
            "expr": "100 - (avg by(instance) (irate(node_cpu_seconds_total{mode=\"idle\"}[5m])) * 100)",
            "legendFormat": "{{instance}}"
          }
        ]
      },
      {
        "title": "Memory Usage",
        "type": "graph",
        "targets": [
          {
            "expr": "(1 - node_memory_MemAvailable_bytes / node_memory_MemTotal_bytes) * 100",
            "legendFormat": "{{instance}}"
          }
        ]
      },
      {
        "title": "HTTP Request Rate",
        "type": "graph",
        "targets": [
          {
            "expr": "rate(http_requests_total[5m])",
            "legendFormat": "{{instance}} - {{method}}"
          }
        ]
      }
    ]
  }
}
```

## Best Practices

### Архитектура и масштабирование
```yaml
# Высокодоступная конфигурация Prometheus
# prometheus.yml для HA setup
global:
  scrape_interval: 15s
  evaluation_interval: 15s

# Федерация для масштабирования
scrape_configs:
  - job_name: 'federate'
    scrape_interval: 15s
    honor_labels: true
    metrics_path: '/federate'
    params:
      'match[]':
        - '{job="prometheus"}'
        - '{__name__=~"job:.*"}'
    static_configs:
      - targets:
        - 'prometheus-1:9090'
        - 'prometheus-2:9090'

# Remote write для долговременного хранения
remote_write:
  - url: "http://cortex:9201/api/prom/push"
    remote_timeout: 30s
    queue_config:
      capacity: 10000
      max_shards: 30
      min_shards: 1
      max_samples_per_send: 1000
      batch_send_deadline: 5s
      min_backoff: 30ms
      max_backoff: 5s

# Remote read для запросов
remote_read:
  - url: "http://cortex:9201/api/prom/read"
    remote_timeout: 30s
    read_recent: true
```

### Оптимизация производительности
```yaml
# Оптимизированная конфигурация
global:
  scrape_interval: 30s      # Реже для снижения нагрузки
  scrape_timeout: 10s
  evaluation_interval: 30s

# Ограничения на ресурсы
storage:
  tsdb:
    retention: 30d          # 30 дней хранения
    max_block_duration: 2h
    min_block_duration: 2h

# Ограничения на scrape
scrape_configs:
  - job_name: 'optimized-scrape'
    scrape_interval: 1m     # Разные интервалы для разных jobs
    scrape_timeout: 30s
    static_configs:
      - targets: ['service:8080']
    metric_relabel_configs:
      # Удаление ненужных метрик
      - source_labels: [__name__]
        regex: 'go_.*'       # Удалить Go runtime метрики
        action: drop
      - source_labels: [__name__]
        regex: 'process_.*'  # Удалить process метрики
        action: drop

# Ограничения на алерты
alerting:
  alertmanagers:
    - timeout: 10s
      api_version: v2
      static_configs:
        - targets: ['alertmanager:9093']
```

### Безопасность
```yaml
# Безопасная конфигурация
# prometheus.yml
global:
  scrape_interval: 15s

scrape_configs:
  - job_name: 'secure-scrape'
    scheme: https
    tls_config:
      ca_file: /etc/prometheus/certs/ca.pem
      cert_file: /etc/prometheus/certs/client.pem
      key_file: /etc/prometheus/certs/client.key
      insecure_skip_verify: false
    basic_auth:
      username: 'prometheus'
      password_file: '/etc/prometheus/secrets/password'
    static_configs:
      - targets: ['secure-service:8443']

# Web security
web:
  listen_address: "0.0.0.0:9090"
  external_url: "https://prometheus.example.com"
  route_prefix: "/"
  user_assets: ""
  enable_lifecycle: true
  enable_admin_api: false
  max_connections: 512

# TLS configuration
tls_server_config:
  cert_file: /etc/prometheus/certs/server.pem
  key_file: /etc/prometheus/certs/server.key
  client_auth_type: RequireAndVerifyClientCert
  client_ca_file: /etc/prometheus/certs/ca.pem

# HTTP basic auth
basic_auth_users:
  admin: $2b$12$...  # bcrypt hash
```

### Мониторинг Prometheus
```yaml
# Метрики самого Prometheus
scrape_configs:
  - job_name: 'prometheus-self'
    static_configs:
      - targets: ['localhost:9090']

# Правила для мониторинга Prometheus
groups:
  - name: prometheus_alerts
    rules:
      - alert: PrometheusJobMissing
        expr: absent(up{job="prometheus"})
        for: 5m
  labels:
    severity: warning
        annotations:
          summary: "Prometheus job missing"
          description: "Prometheus job has disappeared"

      - alert: PrometheusTargetDown
        expr: up{job="prometheus"} == 0
        for: 5m
  labels:
          severity: critical
        annotations:
          summary: "Prometheus target down"
          description: "Prometheus target is down"

      - alert: PrometheusTooManyRestarts
        expr: changes(process_start_time_seconds{job="prometheus"}[10m]) > 2
  labels:
    severity: warning
  annotations:
          summary: "Prometheus too many restarts"
          description: "Prometheus has restarted more than twice in the last 10 minutes"
```

## Troubleshooting

### Распространенные проблемы
```bash
# Проверка конфигурации
promtool check config prometheus.yml
promtool check rules alert_rules.yml

# Проверка метрик
curl -s http://localhost:9090/metrics | head -20

# Проверка здоровья
curl -s http://localhost:9090/-/healthy
curl -s http://localhost:9090/-/ready

# Анализ производительности
# Проверка количества активных series
curl -s http://localhost:9090/api/v1/status/tsdb | jq '.data.seriesCount'

# Проверка размера WAL
curl -s http://localhost:9090/api/v1/status/tsdb | jq '.data.headStats.walSize'

# Проверка количества chunks
curl -s http://localhost:9090/api/v1/status/tsdb | jq '.data.headStats.numSeries'
```

### Диагностика проблем
```yaml
# Проблема: High memory usage
# Решение: Настроить retention и sample rate
storage:
  tsdb:
    retention: 15d
    max_block_duration: 2h
    min_block_duration: 2h

# Проблема: Slow queries
# Решение: Оптимизировать PromQL запросы и добавить индексы
# Избегать:
rate(http_requests_total[1h])  # Слишком большой range

# Использовать:
rate(http_requests_total[5m])

# Проблема: Too many time series
# Решение: Уменьшить cardinality метрик
# Избегать:
http_requests_total{user_id="$user_id"}  # High cardinality

# Использовать:
http_requests_total{user_type="premium"}  # Low cardinality

# Проблема: Missing metrics
# Решение: Проверить конфигурацию scrape
curl -s http://localhost:9090/api/v1/targets | jq '.data.activeTargets[] | select(.health != "up")'

# Проблема: Alert storms
# Решение: Настроить alert grouping и inhibition
inhibit_rules:
  - source_match:
      severity: 'critical'
    target_match:
      severity: 'warning'
    equal: ['alertname', 'instance']

# Проблема: Storage issues
# Решение: Настроить remote write
remote_write:
  - url: "http://cortex:9201/api/prom/push"
    queue_config:
      capacity: 25000
      max_samples_per_send: 500
      batch_send_deadline: 5s
```

## Дата последнего обновления
22 января 2026 г.

## Полезные ссылки
- [Официальная документация Prometheus](https://prometheus.io/docs/)
- [Prometheus GitHub](https://github.com/prometheus/prometheus)
- [PromQL Documentation](https://prometheus.io/docs/prometheus/latest/querying/basics/)
- [Grafana Integration](https://grafana.com/docs/grafana/latest/datasources/prometheus/)
- [Micrometer](https://micrometer.io/)

## См. также
- [Grafana](monitoring/grafana.md) - Визуализация метрик
- [Micrometer](java-micrometer.md) - Метрики для JVM приложений
- [Distributed Tracing](monitoring/distributed-tracing.md) - Распределенное трассирование
