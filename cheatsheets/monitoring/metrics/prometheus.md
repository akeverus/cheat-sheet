---
title: "Prometheus"
description: "Prometheus — система мониторинга и алертинга с открытым исходным кодом. Собирает метрики с целевых объектов по модели pull, хранит их в виде временных рядов и предоставляет язык запросов PromQL для анализа и алертинга."
tags:
  - monitoring
  - metrics
  - prometheus
difficulty: "intermediate"
prerequisites: []
next:
  - go-observability
updated: "2026-04-20"
---
# Prometheus

Prometheus — система мониторинга и алертинга с открытым исходным кодом. Собирает метрики с целевых объектов по модели pull, хранит их в виде временных рядов и предоставляет язык запросов PromQL для анализа и алертинга.

## Полезные ссылки

### Официальная документация
- [Prometheus Documentation](https://prometheus.io/docs/) — основная документация
- [Prometheus Metrics](https://prometheus.io/docs/concepts/data_model/) — модель данных
- [PromQL](https://prometheus.io/docs/prometheus/latest/querying/basics/) — язык запросов
- [Prometheus Best Practices](https://prometheus.io/docs/practices/)

### Java-интеграции
- [Prometheus Java Client](https://github.com/prometheus/client_java) — официальный клиент
- [Micrometer](https://micrometer.io/) — метрики для JVM-приложений
- [Spring Boot Actuator](https://docs.spring.io/spring-boot/docs/current/reference/html/actuator.html)

### См. также
- [Grafana](grafana.md) — визуализация метрик
- [Micrometer](micrometer.md) — метрики в приложениях
- [Observability](../observability-guide.md) — основы Observability
- [Distributed Tracing](../tracing/distributed-tracing.md) — трассировка

- [Redis: Мониторинг](../../databases/nosql/redis/redis-monitoring.md)
## Содержание

- [Основы Prometheus](#основы-prometheus)
  - [Типы метрик](#типы-метрик)
  - [Установка](#установка)
- [Конфигурация Prometheus](#конфигурация-prometheus)
  - [prometheus.yml](#prometheusyml)
  - [Service Discovery](#service-discovery)
- [PromQL — язык запросов](#promql-язык-запросов)
  - [Операторы и функции](#операторы-и-функции)
  - [Примеры по приложению](#примеры-по-приложению)
- [Recording Rules](#recording-rules)
- [Alerting Rules](#alerting-rules)
- [Java Client для Prometheus](#java-client-для-prometheus)
  - [Maven и базовое использование](#maven-и-базовое-использование)
- [Micrometer интеграция](#micrometer-интеграция)
- [Метрики JVM](#метрики-jvm)
- [Интеграция с другими языками](#интеграция-с-другими-языками)
  - [Node.js (prom-client)](#nodejs-prom-client)
  - [Go (prometheus client_golang)](#go-prometheus-client_golang)
- [Визуализация и дашборды](#визуализация-и-дашборды)
- [Лучшие практики](#лучшие-практики)
- [Мониторинг Prometheus](#мониторинг-prometheus)
- [Решение проблем](#решение-проблем)
- [Частые вопросы](#частые-вопросы)

## Основы Prometheus

Компоненты: Prometheus Server (сбор, хранение, запросы), Pushgateway (для batch jobs), Alertmanager (обработка алертов), Exporters (метрики с систем), Service Discovery (Kubernetes, EC2, Consul и др.).

**Pull-модель:** Prometheus сам запрашивает метрики у приложений по HTTP. Преимущества: централизованный контроль, отказоустойчивость, простота приложений, удобная отладка через endpoint `/metrics`.

**Pushgateway** используется, когда pull невозможен (batch jobs, краткоживущие задачи): приложение пушит метрики в Pushgateway, Prometheus скрапит Pushgateway.

**Federation:** региональные инстансы Prometheus отдают агрегированные метрики глобальному Prometheus для масштабирования.

### Типы метрик

- **Counter** — монотонно растущее значение (запросы, ошибки).
- **Gauge** — значение, которое может расти и падать (память, активные соединения).
- **Histogram** — распределение + count/sum, buckets (латентность).
- **Summary** — квантили и сумма (часто на стороне приложения).

### Установка

**Docker:**

```bash
docker run -d --name prometheus -p 9090:9090 \
  -v $(pwd)/prometheus.yml:/etc/prometheus/prometheus.yml \
  prom/prometheus
```

С полным путём к конфигу и retention:

```bash
docker run -d --name prometheus -p 9090:9090 \
  -v $(pwd)/config:/etc/prometheus \
  prom/prometheus \
  --config.file=/etc/prometheus/prometheus.yml \
  --storage.tsdb.path=/prometheus \
  --storage.tsdb.retention.time=200h \
  --web.enable-lifecycle
```

**Kubernetes:** Deployment + Service в namespace monitoring, volume для config и data (см. официальную документацию).

**Systemd:** скачать бинарь, создать пользователя `prometheus`, каталоги `/etc/prometheus`, `/var/lib/prometheus`, скопировать `prometheus` и `promtool`, настроить unit с `ExecStart=/usr/local/bin/prometheus --config.file=/etc/prometheus/prometheus.yml --storage.tsdb.path=/var/lib/prometheus`.

## Конфигурация Prometheus

### prometheus.yml

```yaml
global:
  scrape_interval: 15s
  evaluation_interval: 15s
  scrape_timeout: 10s

rule_files:
  - "alert_rules.yml"
  - "recording_rules.yml"

alerting:
  alertmanagers:
    - static_configs:
        - targets: ['alertmanager:9093']

scrape_configs:
  - job_name: 'prometheus'
    static_configs:
      - targets: ['localhost:9090']

  - job_name: 'node'
    static_configs:
      - targets: ['node-exporter:9100']

  - job_name: 'spring-boot'
    metrics_path: '/actuator/prometheus'
    static_configs:
      - targets: ['spring-app:8080']

  - job_name: 'kubernetes-pods'
    kubernetes_sd_configs:
      - role: pod
    relabel_configs:
      - source_labels: [__meta_kubernetes_pod_annotation_prometheus_io_scrape]
        action: keep
        regex: true
      - source_labels: [__meta_kubernetes_pod_annotation_prometheus_io_port]
        action: replace
        target_label: __address__
        regex: (\d+)
        replacement: $1
      - source_labels: [__meta_kubernetes_namespace]
        action: replace
        target_label: namespace
```

### Service Discovery

**Kubernetes:** `kubernetes_sd_configs` с ролью `pod`, `service`, `node`. Использовать аннотации `prometheus.io/scrape`, `prometheus.io/port`, `prometheus.io/path` и relabel для формирования `__address__` и меток.

**AWS EC2:** `ec2_sd_configs` с region, port, filters по тегам. **Consul:** `consul_sd_configs` с server и списком services.

## PromQL — язык запросов

### Операторы и функции

```promql
# Арифметика и сравнение
cpu_usage + memory_usage
response_time >= 0.5

# Агрегаты
sum(cpu_usage)
avg by (instance) (memory_usage)
max without (endpoint) (response_time)

# rate и increase
rate(http_requests_total[5m])
increase(http_errors_total[1h])

# Квантили histogram
histogram_quantile(0.95, rate(http_request_duration_seconds_bucket[10m]))

# Скользящие окна
avg_over_time(cpu_usage[5m])
max_over_time(memory_usage[1h])

# Селекторы
up{job="api"}
http_requests_total{environment!="test"}
metric_name{label=~"prefix.*"}
```

### Примеры по приложению

HTTP: `rate(http_requests_total[5m])`, `histogram_quantile(0.95, rate(http_request_duration_seconds_bucket[5m]))`. БД: `rate(database_connections_active[5m])`. JVM: `jvm_memory_used_bytes / jvm_memory_max_bytes * 100`, `rate(jvm_gc_collection_seconds_count[5m])`.

Error rate: `rate(http_requests_total{status=~"5.."}[5m]) / rate(http_requests_total[5m])`. Latency p95: `histogram_quantile(0.95, rate(http_request_duration_seconds_bucket[5m]))`. Throughput: `rate(http_requests_total[5m])`.

## Recording Rules

Предвычисление тяжёлых запросов для ускорения дашбордов и алертов:

```yaml
# recording_rules.yml
groups:
  - name: example_rules
    interval: 30s
    rules:
      - record: job:http_requests_total:rate5m
        expr: sum(rate(http_requests_total[5m])) by (job)
      - record: service:response_time:p95
        expr: histogram_quantile(0.95, sum(rate(http_request_duration_seconds_bucket[10m])) by (le, service))
```

## Alerting Rules

```yaml
# alert_rules.yml
groups:
  - name: example_alerts
    rules:
      - alert: InstanceDown
        expr: up == 0
        for: 5m
        labels:
          severity: critical
        annotations:
          summary: "Instance {{ $labels.instance }} is down"

      - alert: HighCpuUsage
        expr: cpu_usage_percent > 90
        for: 10m
        labels:
          severity: warning
        annotations:
          summary: "High CPU on {{ $labels.instance }}"

      - alert: HighHttpErrorRate
        expr: rate(http_requests_total{status=~"5.."}[5m]) / rate(http_requests_total[5m]) * 100 > 5
        for: 5m
        labels:
          severity: warning
```

**Kubernetes Service Discovery:** аннотации на Pod (prometheus.io/scrape, prometheus.io/port, prometheus.io/path); в scrape_configs — kubernetes_sd_configs с role: pod и relabel_configs по этим аннотациям.

**High Availability:** federation (региональные Prometheus отдают выбранные метрики глобальному); Thanos для long-term storage и глобального запроса.

## Java Client для Prometheus

### Maven и базовое использование

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

```java
import io.prometheus.client.Counter;
import io.prometheus.client.Gauge;
import io.prometheus.client.Histogram;
import io.prometheus.client.exporter.HTTPServer;
import io.prometheus.client.hotspot.DefaultExports;

Counter requestsTotal = Counter.build()
    .name("http_requests_total")
    .help("Total number of HTTP requests")
    .labelNames("method", "endpoint", "status")
    .register();
Gauge activeConnections = Gauge.build()
    .name("active_connections")
    .help("Number of active connections")
    .register();
Histogram requestDuration = Histogram.build()
    .name("http_request_duration_seconds")
    .help("Request duration in seconds")
    .labelNames("method", "endpoint")
    .buckets(0.1, 0.5, 1.0, 2.5, 5.0, 10.0)
    .register();

DefaultExports.initialize();
HTTPServer server = new HTTPServer(8080);
```

Кастомный Collector реализует интерфейс Collector и возвращает List<MetricFamilySamples>; регистрируется через DefaultCollector или register().

## Micrometer интеграция

Micrometer даёт vendor-neutral API: один код метрик для разных бэкендов. PrometheusMeterRegistry отдаёт текст в формате Prometheus при scrape.

**Counter, Gauge, Timer** создаются через MeterRegistry (Counter.builder, Gauge.builder, Timer.builder). В Spring Boot MeterRegistry внедряется как бин; метрики автоматически экспортируются на `/actuator/prometheus` при наличии spring-boot-starter-actuator и micrometer-registry-prometheus.

**application.yml:**
```yaml
management:
  endpoints:
    web:
      exposure:
        include: health, info, metrics, prometheus
  endpoint:
    prometheus:
      enabled: true
  metrics:
    export:
      prometheus:
        enabled: true
    distribution:
      percentiles-histogram:
        http.server.requests: true
      percentiles:
        http.server.requests: 0.5, 0.9, 0.95, 0.99
```

Кастомные метрики: Counter/Timer/Gauge через registry в сервисах; для JVM дополнительно включаются management.metrics.enable.jvm и т.п.

## Метрики JVM

Доступны через Micrometer/Actuator: память (jvm_memory_used_bytes по area heap/nonheap), GC (jvm_gc_*), потоки (jvm_threads_*), классы (jvm_classes_*). Кастомные метрики по MemoryPoolMXBean и BufferPoolMXBean можно зарегистрировать вручную через Gauge.builder.

Бизнес-метрики: HTTP (TimedAspect, @Timed), база (HikariCP + MeterRegistry, таймеры на операции), кэш (Caffeine recordStats, gauge по размеру). Имена в стиле http_requests_total, database_operations_total, orders_created_total.

## Интеграция с другими языками

### Node.js (prom-client)

`prom-client`: регистр, `collectDefaultMetrics()`, кастомные Counter/Histogram/Gauge, middleware для HTTP (подсчёт запросов и длительности). Endpoint `/metrics` отдаёт `register.metrics()`.

### Go (prometheus client_golang)

`promauto.NewCounterVec`, `promauto.NewHistogramVec`, `promauto.NewGauge`. Middleware оборачивает handler и записывает метрики. `promhttp.Handler()` для `/metrics`.

## Визуализация и дашборды

В Grafana добавить data source типа Prometheus, URL — адрес Prometheus (например `http://prometheus:9090`). В панелях использовать PromQL. Готовые дашборды можно импортировать по ID из [Grafana Dashboards](https://grafana.com/grafana/dashboards/).

## Лучшие практики

- **Именование:** метрики — lowercase, суффиксы _total, _seconds; лейблы с ограниченным набором значений (method, status, endpoint), без user_id/request_id в лейблах.
- **Кардинальность:** избегать высокой кардинальности (уникальные id в лейблах); использовать группировку uri и ограниченные теги.
- **Histogram vs Summary:** Histogram — для агрегации на стороне Prometheus и PromQL; Summary — для точных квантилей на стороне приложения при необходимости.
- **Архитектура:** HA — два экземпляра Prometheus за балансировщиком; федерация или remote write для масштабирования и долгого хранения (Thanos, VictoriaMetrics, Cortex).
- **Производительность:** адекватный `scrape_interval` (15–30s), ограничение cardinality метрик и меток, recording rules для тяжёлых запросов, настройка retention.
- **Безопасность:** TLS и basic_auth в `scrape_config` для целей; не открывать UI/API наружу без аутентификации; секреты в переменных окружения или файлах прав.

## Мониторинг Prometheus

Скрапить себя: `job_name: 'prometheus'`, targets `localhost:9090`. Полезные метрики: `prometheus_tsdb_head_series`, `prometheus_tsdb_storage_blocks_bytes`, `scrape_duration_seconds`, `up`. Алерты: отсутствие `up{job="prometheus"}`, слишком частые рестарты `changes(process_start_time_seconds{job="prometheus"}[10m]) > 2`.

## Решение проблем

| Симптом | Возможная причина | Действие |
|--------|-------------------|----------|
| Targets не появляются в UI | Неверный scrape_config, сеть, firewall | Проверить /api/v1/targets, конфиг, service discovery |
| Метрики не приходят с приложения | Неверный path/port, приложение не отдаёт /metrics | curl на endpoint приложения; проверить scrape errors в /api/v1/targets |
| Высокое потребление памяти | Большой объём series или длинный retention | Уменьшить retention; отбросить лишние метрики через metric_relabel_configs; увеличить ресурсы |
| Медленные запросы | Тяжёлый PromQL или большой range | Сократить range в запросе; использовать recording rules; ограничить число series (topk, limit) |
| Лавина алертов | Много правил или флап | Группировка и inhibition в Alertmanager; увеличить `for` в правилах; объединить похожие правила |

Проверка конфига: `promtool check config prometheus.yml`. Проверка правил: `promtool check rules alert_rules.yml`. Состояние TSDB: `curl -s http://localhost:9090/api/v1/status/tsdb`.

## Частые вопросы

**Когда использовать Pushgateway?** Для batch/cron jobs, которые живут меньше интервала scrape. Pushgateway принимает метрики push и отдаёт их Prometheus при scrape. Не использовать для долгоживущих сервисов — только pull.

**Как уменьшить cardinality?** Не использовать в метках пользовательские ID, email и т.п. Агрегировать по типу (user_type, endpoint без path). В metric_relabel_configs отбрасывать ненужные метрики или метки (action: drop).

**Как хранить данные дольше 15 дней?** Увеличить `--storage.tsdb.retention.time` в разумных пределах или настроить remote write в Thanos, VictoriaMetrics, Cortex с объектным хранилищем.

**Почему алерт срабатывает не сразу?** Параметр `for` в правиле задаёт время, в течение которого условие должно выполняться. Проверить expr и for; при необходимости уменьшить for или интервал оценки (evaluation_interval).
