---
title: "Grafana"
description: "Grafana — платформа для аналитики и интерактивной визуализации данных с открытым исходным кодом. Позволяет создавать дашборды и графики на основе данных из различных источников: Prometheus, Elasticsearch, InfluxDB и др."
tags:
  - monitoring
  - metrics
  - grafana
difficulty: "intermediate"
prerequisites: []
next: []
updated: "2026-02-11"
---
# Grafana

Grafana — платформа для аналитики и интерактивной визуализации данных с открытым исходным кодом. Позволяет создавать дашборды и графики на основе данных из различных источников: Prometheus, Elasticsearch, InfluxDB и др.

## Полезные ссылки

- [Официальная документация Grafana](https://grafana.com/docs/grafana/latest/)
- [Grafana GitHub](https://github.com/grafana/grafana)
- [Grafana Plugins](https://grafana.com/docs/grafana/latest/plugins/)
- [Grafana API](https://grafana.com/docs/grafana/latest/developers/http_api/)
- [Grafana Dashboards Gallery](https://grafana.com/grafana/dashboards/)
- [Micrometer + Grafana](https://micrometer.io/docs)

### См. также

- [Prometheus](prometheus.md) — сбор метрик
- [Micrometer](micrometer.md) — метрики JVM
- [Alerting](../alerting/alerting.md) — система алертинга
- [Distributed Tracing](../tracing/distributed-tracing.md) — распределённое трассирование

## Содержание

- [Основы Grafana](#основы-grafana)
- [Data Sources](#data-sources)
- [Создание дашбордов](#создание-дашбордов)
- [Типы панелей](#типы-панелей)
- [Spring Boot интеграция](#spring-boot-интеграция)
- [JVM и метрики приложения](#jvm-и-метрики-приложения)
- [Alerting](#alerting)
- [Плагины и расширения](#плагины-и-расширения)
- [Управление пользователями и доступом](#управление-пользователями-и-доступом)
- [Provisioning](#provisioning)
- [API и автоматизация](#api-и-автоматизация)
- [Лучшие практики](#лучшие-практики)
- [Решение проблем](#решение-проблем)
- [Частые вопросы](#частые-вопросы)

## Основы Grafana

### Архитектура Grafana

Основные компоненты: Grafana Server (веб-сервер и API), Database (SQLite, PostgreSQL, MySQL), Data Sources, Dashboards, Plugins, Users & Teams. Поддерживает 50+ источников данных.

### Установка и запуск

#### Docker

```bash
docker run -d \
  --name grafana \
  -p 3000:3000 \
  -e GF_SECURITY_ADMIN_PASSWORD=admin \
  -v grafana-data:/var/lib/grafana \
  grafana/grafana

# С внешней базой данных
docker run -d \
  --name grafana \
  -p 3000:3000 \
  -e GF_DATABASE_TYPE=postgres \
  -e GF_DATABASE_HOST=postgres:5432 \
  -e GF_DATABASE_NAME=grafana \
  -e GF_DATABASE_USER=grafana \
  -e GF_DATABASE_PASSWORD=password \
  -v grafana-data:/var/lib/grafana \
  grafana/grafana
```

#### Kubernetes

```yaml
apiVersion: apps/v1
kind: Deployment
metadata:
  name: grafana
  namespace: monitoring
spec:
  replicas: 1
  selector:
    matchLabels:
      app: grafana
  template:
    metadata:
      labels:
        app: grafana
    spec:
      containers:
      - name: grafana
        image: grafana/grafana:latest
        ports:
        - containerPort: 3000
        env:
        - name: GF_SECURITY_ADMIN_PASSWORD
          value: "admin"
        - name: GF_DATABASE_TYPE
          value: "postgres"
        - name: GF_DATABASE_HOST
          value: "postgres-service"
        volumeMounts:
        - name: grafana-storage
          mountPath: /var/lib/grafana
      volumes:
      - name: grafana-storage
        persistentVolumeClaim:
          claimName: grafana-pvc

---
apiVersion: v1
kind: Service
metadata:
  name: grafana
  namespace: monitoring
spec:
  selector:
    app: grafana
  ports:
  - port: 3000
    targetPort: 3000
  type: ClusterIP
```

#### Конфигурация

```ini
# /etc/grafana/grafana.ini или GF_ переменные окружения

[server]
http_addr = 0.0.0.0
http_port = 3000
domain = grafana.example.com

[database]
type = postgres
host = postgres:5432
name = grafana
user = grafana
password = password

[security]
admin_user = admin
admin_password = admin
secret_key = your-secret-key

[auth]
disable_login_form = false

[auth.anonymous]
enabled = true
org_role = Viewer

[alerting]
enabled = true

[metrics]
enabled = true
```

## Data Sources

### Prometheus

```json
{
  "name": "Prometheus",
  "type": "prometheus",
  "url": "http://prometheus:9090",
  "access": "proxy",
  "isDefault": true,
  "jsonData": {
    "timeInterval": "15s",
    "queryTimeout": "60s",
    "httpMethod": "POST"
  }
}
```

### Elasticsearch

```json
{
  "name": "Elasticsearch",
  "type": "elasticsearch",
  "url": "http://elasticsearch:9200",
  "access": "proxy",
  "database": "[logstash-]YYYY.MM.DD",
  "jsonData": {
    "esVersion": "7.0.0",
    "timeField": "@timestamp",
    "interval": "Daily",
    "logMessageField": "message",
    "logLevelField": "level"
  }
}
```

### InfluxDB

```json
{
  "name": "InfluxDB",
  "type": "influxdb",
  "url": "http://influxdb:8086",
  "access": "proxy",
  "database": "metrics",
  "jsonData": {
    "httpMode": "POST",
    "version": "Flux"
  }
}
```

### PostgreSQL

```json
{
  "name": "PostgreSQL",
  "type": "postgres",
  "url": "postgres:5432",
  "access": "proxy",
  "database": "metrics",
  "user": "grafana",
  "jsonData": {
    "sslmode": "disable",
    "maxOpenConns": 100,
    "maxIdleConns": 100,
    "connMaxLifetime": 14400
  }
}
```

## Создание дашбордов

### Структура дашборда

```json
{
  "dashboard": {
    "title": "System Monitoring",
    "tags": ["system", "monitoring"],
    "timezone": "browser",
    "refresh": "30s",
    "time": { "from": "now-1h", "to": "now" },
    "panels": [],
    "templating": { "list": [] }
  }
}
```

### Переменные шаблонов

```json
{
  "templating": {
    "list": [
      {
        "name": "datasource",
        "type": "datasource",
        "query": "prometheus",
        "label": "Data Source"
      },
      {
        "name": "instance",
        "type": "query",
        "datasource": "$datasource",
        "query": "label_values(up, instance)",
        "label": "Instance",
        "multi": true,
        "includeAll": true
      }
    ]
  }
}
```

Custom variable: список значений через запятую для выбора окружения или статического набора. В панелях использовать `$service`, `$environment` в PromQL: `up{service="$service"}`.

## Типы панелей

### Graph Panel

```json
{
  "title": "CPU Usage",
  "type": "graph",
  "targets": [{
    "expr": "100 - (avg by(instance) (irate(node_cpu_seconds_total{mode=\"idle\", instance=\"$instance\"}[5m])) * 100)",
    "legendFormat": "{{instance}}",
    "refId": "A"
  }],
  "yAxes": [{ "unit": "percent", "min": 0, "max": 100 }, { "unit": "short" }]
}
```

### Table Panel

```json
{
  "title": "Top CPU Processes",
  "type": "table",
  "targets": [{
    "expr": "topk(10, rate(process_cpu_seconds_total[5m]))",
    "legendFormat": "{{pid}} - {{comm}}",
    "refId": "A"
  }],
  "transform": "table"
}
```

### Gauge Panel

```json
{
  "title": "Memory Usage",
  "type": "gauge",
  "targets": [{
    "expr": "(1 - node_memory_MemAvailable_bytes / node_memory_MemTotal_bytes) * 100",
    "refId": "A"
  }],
  "fieldConfig": {
    "defaults": {
      "unit": "percent",
      "min": 0,
      "max": 100,
      "thresholds": {
        "mode": "absolute",
        "steps": [
          { "color": "green", "value": null },
          { "color": "red", "value": 80 },
          { "color": "dark-red", "value": 90 }
        ]
      }
    }
  }
}
```

### Stat Panel

```json
{
  "title": "Total Requests",
  "type": "stat",
  "targets": [{ "expr": "sum(increase(http_requests_total[1h]))", "refId": "A" }],
  "fieldConfig": {
    "defaults": {
      "unit": "none",
      "thresholds": {
        "mode": "absolute",
        "steps": [
          { "color": "green", "value": null },
          { "color": "red", "value": 1000 }
        ]
      }
    }
  }
}
```

### Heatmap Panel

```json
{
  "title": "Response Time Heatmap",
  "type": "heatmap",
  "targets": [{
    "expr": "rate(http_request_duration_seconds_bucket[5m])",
    "legendFormat": "{{le}}",
    "refId": "A"
  }],
  "heatmap": { "buckets": 20, "hideZeroBuckets": true }
}
```

## Spring Boot интеграция

Подключить Spring Boot Actuator и Micrometer Prometheus. Endpoint `/actuator/prometheus` отдаёт метрики. В Prometheus добавить job с `metrics_path: /actuator/prometheus` и targets приложения. В Grafana источник — уже настроенный Prometheus.

Spring Boot Admin (опционально): server — `spring-boot-admin-starter-server`, client в приложениях — `spring-boot-admin-starter-client`, url сервера в `spring.boot.admin.client.url`. Кастомные health и метрики — через `HealthIndicator` и `MeterRegistry`.

## JVM и метрики приложения

### JVM Memory

Запросы к Prometheus (Micrometer): `jvm_memory_used_bytes{area="heap"}`, `jvm_memory_committed_bytes{area="heap"}`, `jvm_memory_max_bytes{area="heap"}`. Панель типа graph, unit bytes. Пул по областям: `jvm_memory_pool_used_bytes{pool="Eden Space"}`.

### GC

Паузы: `rate(jvm_gc_pause_seconds_sum[5m])`. Количество сборок: `increase(jvm_gc_collection_seconds_count[5m])`.

### Threads

`jvm_threads_live`, `jvm_threads_daemon`, `jvm_threads_peak` — graph.

### HTTP и ошибки

Request rate: `rate(http_server_requests_seconds_count[5m])`. Перцентили: `histogram_quantile(0.95, rate(http_server_requests_seconds_bucket[5m]))`. Error rate: доля запросов со status=~"5.." в процентах.

### БД (HikariCP)

`hikaricp_connections_active`, `hikaricp_connections_idle`, `hikaricp_connections_pending`.

## Alerting

### Настройка алертов

```json
{
  "alert": {
    "name": "High CPU Usage",
    "message": "CPU usage is above 80%",
    "conditions": [{
      "evaluator": { "params": [80], "type": "gt" },
      "operator": { "type": "and" },
      "query": { "params": ["A", "5m", "now"] },
      "reducer": { "params": [], "type": "avg" },
      "type": "query"
    }],
    "executionErrorState": "alerting",
    "frequency": "60s",
    "noDataState": "no_value"
  }
}
```

### Notification Channels

**Email:** `addresses`, `singleEmail`. **Slack:** `url`, `recipient`, `username`. **PagerDuty:** `integrationKey`, `autoResolve`, `severity`. **Webhook:** `url`, `httpMethod` для интеграции с Alertmanager.

## Плагины и расширения

```bash
# Установка
grafana-cli plugins install grafana-piechart-panel
grafana-cli plugins install grafana-worldmap-panel

# Обновление
grafana-cli plugins update-all

# Удаление
grafana-cli plugins remove grafana-piechart-panel
```

## Управление пользователями и доступом

Роли: Viewer (просмотр), Editor (редактирование дашбордов), Admin. Организации и команды настраиваются через UI или SQL/API. Permissions задаются для дашбордов, папок и data sources. API keys и Service account tokens — для автоматизации; хранить в секретах, не в коде. В production: сильный admin password, secret_key из env, при необходимости JWT/OAuth и отключение анонимного доступа.

## Provisioning

### Дашборды через файлы

```yaml
# /etc/grafana/provisioning/dashboards/dashboard.yml
apiVersion: 1
providers:
  - name: 'default'
    orgId: 1
    folder: ''
    type: file
    options:
      path: /var/lib/grafana/dashboards
```

### Data Sources через файлы

```yaml
# /etc/grafana/provisioning/datasources/datasource.yml
apiVersion: 1
datasources:
  - name: Prometheus
    type: prometheus
    access: proxy
    url: http://prometheus:9090
    isDefault: true
  - name: Elasticsearch
    type: elasticsearch
    access: proxy
    url: http://elasticsearch:9200
    database: "[metrics-]YYYY.MM.DD"
```

## API и автоматизация

```bash
# Получение дашбордов
curl -H "Authorization: Bearer YOUR_API_TOKEN" http://localhost:3000/api/search?query=*

# Создание дашборда
curl -X POST -H "Content-Type: application/json" -H "Authorization: Bearer YOUR_API_TOKEN" \
  -d @dashboard.json http://localhost:3000/api/dashboards/db

# Обновление data source
curl -X PUT -H "Content-Type: application/json" -H "Authorization: Bearer YOUR_API_TOKEN" \
  -d @datasource.json http://localhost:3000/api/datasources/1
```

Terraform: `grafana_dashboard`, `grafana_data_source`, `grafana_alert_notification`, `grafana_folder`, `grafana_team`.

## Лучшие практики

- **Организация:** папки по назначению (Infrastructure, Applications, Business), единые соглашения по именам дашбордов и панелей.
- **Панели:** ограничивать число панелей на дашборде, использовать summary-метрики и разумный шаг запросов.
- **Алерты:** понятные имена, уровни severity, использование `for` для снижения флапа.
- **Переменные:** переиспользуемые шаблоны (datasource, env, service) для мульти-окружения.
- **Производительность:** `router_logging = false`, настройка пула БД, отключение лишней аналитики.
- **Безопасность:** пароль и `secret_key` из переменных окружения, `cookie_secure`, JWT/OAuth при необходимости, отключить анонимный доступ в prod.
- **Масштабирование:** HPA в Kubernetes, внешний PostgreSQL для HA, общий backend для сессий.

## Решение проблем

| Симптом | Возможная причина | Действие |
|--------|-------------------|----------|
| Панели не загружают данные | Неверный data source или PromQL | Проверить «Test» data source; выполнить тот же запрос в Explore; проверить временной диапазон |
| Ошибка «Failed to fetch» | Таймаут или недоступность источника | Увеличить `queryTimeout` в настройках data source; проверить сеть и доступность Prometheus |
| Дашборд открывается медленно | Слишком много панелей или тяжёлые запросы | Уменьшить число панелей на странице; увеличить шаг запросов; использовать recording rules в Prometheus |
| Data source «Unable to connect» | Неверный URL или сеть | Проверить доступность с хоста Grafana (curl); проверить proxy/firewall |
| Нет данных за период | Нет метрик или неверный диапазон | Проверить временной диапазон; тот же запрос в Explore; убедиться, что приложение отдаёт метрики |

**Диагностика:** логи — `docker logs grafana` или `/var/log/grafana`; конфигурация — `grafana-cli config:check`; плагины — `grafana-cli plugins ls`.

**Метрики Grafana** (при включённом `[metrics]`): `grafana_stat_totals_dashboard`, `grafana_api_response_status_total`, `grafana_db_request_duration_seconds`.

## Частые вопросы

**Как сделать один дашборд для нескольких окружений (prod/staging)?** Использовать переменные типа query: `label_values(up, env)` и подставлять `$env` в запросы и в названия панелей.

**Как подставить в дашборд список сервисов из Prometheus?** Создать переменную типа Query, datasource — Prometheus, query: `label_values(up, job)` или `label_values(http_requests_total, service)`. В панелях использовать `{job=~"$job"}`.

**Почему алерт не уходит в Slack/PagerDuty?** Проверить конфигурацию канала (URL, ключ), состояние алерта в «Alerting» → «Alert rules», логи Grafana при срабатывании.

**Grafana или Alertmanager для алертов?** Grafana удобна для правил поверх дашбордов и быстрого порога; Alertmanager даёт группировку, inhibition, маршрутизацию по severity. Часто используют оба.

**Как хранить дашборды в Git и применять при деплое?** Provisioning через файлы в `/etc/grafana/provisioning/dashboards` и хранение JSON в репозитории; либо Terraform с `grafana_dashboard` и `config_json = file("...")`.
