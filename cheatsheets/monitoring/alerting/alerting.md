---
title: "Системы алертинга для Java"
description: "Комплексное руководство по настройке и использованию систем алертинга в Java-приложениях: Alertmanager, PagerDuty, Slack, email-уведомления и лучшие практики."
tags:
  - monitoring
  - alerting
difficulty: "intermediate"
prerequisites: []
next: []
updated: "2026-02-11"
---
# Системы алертинга для Java

Комплексное руководство по настройке и использованию систем алертинга в Java-приложениях: Alertmanager, PagerDuty, Slack, email-уведомления и лучшие практики.

## Полезные ссылки

### Официальная документация
- [Prometheus Alertmanager](https://prometheus.io/docs/alerting/latest/alertmanager/) — официальная документация
- [Alertmanager Configuration](https://prometheus.io/docs/alerting/latest/configuration/) — конфигурация
- [Alerting Rules](https://prometheus.io/docs/prometheus/latest/configuration/alerting_rules/) — правила алертинга

### Java-интеграции
- [Spring Boot Actuator](https://docs.spring.io/spring-boot/docs/current/reference/html/actuator.html) — health checks
- [Micrometer](https://micrometer.io/) — метрики для алертинга
- [Alertmanager Java Client](https://github.com/prometheus/client_java) — Java-клиент

### Статьи и туториалы
- [Alerting Best Practices](https://prometheus.io/docs/practices/alerting/)
- [Avoiding Alert Fatigue](https://prometheus.io/docs/practices/alerting/)

### См. также
- [[prometheus|Prometheus]] — Prometheus-метрики
- [[grafana|Grafana]] — алерты в Grafana
- [[centralized-logging|Централизованное логирование]] — логи для алертов

## Содержание

- [Введение в алертинг](#введение-в-алертинг)
- [Архитектура Alertmanager](#alertmanager-архитектура)
- [Настройка Alertmanager](#настройка-alertmanager)
- [Java-интеграция](#java-интеграция)
- [Правила алертов](#правила-алертов)
- [Каналы уведомлений](#каналы-уведомлений)
- [Лучшие практики](#лучшие-практики)
- [Решение проблем](#решение-проблем)
- [Частые вопросы](#частые-вопросы)

## Введение в алертинг

Алертинг — это процесс автоматического обнаружения проблем в системе и уведомления ответственных лиц. В контексте мониторинга он позволяет своевременно реагировать на инциденты, предотвращать downtime и обеспечивать SLA.

### Почему важен алертинг

- **Раннее обнаружение** — быстрое выявление проблем
- **Автоматизированная реакция** — реагирование на инциденты без ручного опроса
- **Сокращение MTTR** — меньше время восстановления
- **Соблюдение SLA** — контроль соглашений об уровне сервиса
- **Координация команды** — единый канал уведомлений

### Типы алертов

По severity: Critical (полный downtime), Warning (деградация), Info, Resolved.

По источнику: инфраструктура, приложение, бизнес-метрики, безопасность.

По времени: в реальном времени, по расписанию, эскалация при отсутствии реакции.

## Alertmanager архитектура

### Компоненты системы

```text
Alert Sources (Prometheus, Grafana, приложения)
        ↓
Alertmanager (агрегация, дедупликация, silence)
        ↓
Routing & Grouping (group_by, inhibition)
        ↓
Notification Channels (Email, Slack, PagerDuty, Webhook)
```

### Жизненный цикл алерта

1. **Генерация** — правило в Prometheus срабатывает (expr, for, labels, annotations).
2. **Маршрутизация** — Alertmanager направляет алерт по меткам в нужный receiver.
3. **Уведомление** — отправка в Slack, email, PagerDuty и т.д.
4. **Разрешение** — при снятии условия Prometheus отправляет resolved; при send_resolved: true уходит уведомление о восстановлении.

Пример правила и маршрутизации:

```yaml
# Prometheus rule
- alert: HighErrorRate
  expr: rate(http_requests_total{status=~"5.."}[5m]) > 0.1
  for: 5m
  labels:
    severity: critical
    team: backend
  annotations:
    summary: "High error rate detected"
    description: "Error rate is {{ $value }} errors per second"
```

```yaml
# Alertmanager routing
route:
  group_by: ['alertname', 'cluster']
  group_wait: 30s
  group_interval: 5m
  repeat_interval: 4h
  receiver: 'backend-team'
  routes:
  - match:
      team: backend
      severity: critical
    receiver: 'backend-pager'
```

## Настройка Alertmanager

### Установка

**Docker:**

```bash
docker run -d -p 9093:9093 --name alertmanager \
  -v $(pwd)/alertmanager.yml:/etc/alertmanager/config.yml \
  prom/alertmanager:latest \
  --config.file=/etc/alertmanager/config.yml \
  --storage.path=/alertmanager
```

**Linux:** скачать архив с [releases](https://github.com/prometheus/alertmanager/releases), распаковать и запустить `./alertmanager --config.file=alertmanager.yml`.

**Kubernetes:** использовать Helm chart `kube-prometheus-stack` или Deployment + ConfigMap с `alertmanager.yml`.

### Базовая конфигурация

```yaml
global:
  smtp_smarthost: 'smtp.gmail.com:587'
  smtp_from: 'alerts@example.com'

route:
  group_by: ['alertname']
  group_wait: 10s
  group_interval: 10s
  repeat_interval: 1h
  receiver: 'email-notifications'
  routes:
  - match:
      severity: critical
    receiver: 'critical-notifications'
  - match:
      team: backend
    receiver: 'backend-team'

receivers:
- name: 'email-notifications'
  email_configs:
  - to: 'team@example.com'
    send_resolved: true
- name: 'critical-notifications'
  slack_configs:
  - api_url: 'https://hooks.slack.com/services/YOUR/SLACK/WEBHOOK'
    channel: '#alerts-critical'
    send_resolved: true
```

## Java-интеграция

### Spring Boot Health и метрики

Кастомные health indicators и метрики Micrometer позволяют экспортировать данные для алертинга:

```java
@Component
public class DatabaseHealthIndicator implements HealthIndicator {
    @Override
    public Health health() {
        try (Connection c = dataSource.getConnection()) {
            c.createStatement().execute("SELECT 1");
            return Health.up().withDetail("database", "available").build();
        } catch (SQLException e) {
            return Health.down().withDetail("error", e.getMessage()).build();
        }
    }
}
```

Метрики для алертов (ошибки, латентность, пулы соединений) регистрируются через MeterRegistry и подхватываются Prometheus.

### Отправка алертов в Alertmanager

Приложение может слать алерты напрямую в Alertmanager API:

```java
    private static final String ALERTMANAGER_URL = "http://alertmanager:9093/api/v2/alerts";

    public void sendAlert(String alertName, String description, AlertSeverity severity) {
    Map<String, Object> alert = Map.of(
        "labels", Map.of("alertname", alertName, "severity", severity.toString().toLowerCase(), "service", "user-service"),
        "annotations", Map.of("summary", alertName, "description", description),
        "startsAt", OffsetDateTime.now().toString()
    );
    restTemplate.postForEntity(ALERTMANAGER_URL, List.of(alert), String.class);
}
```

## Правила алертов

### Инфраструктура

```yaml
  - alert: HostDown
    expr: up == 0
    for: 5m
  labels: { severity: critical, team: infrastructure }
    annotations:
      summary: "Host {{ $labels.instance }} is down"
      runbook_url: "https://runbook.example.com/host-down"

  - alert: HighCpuUsage
    expr: cpu_usage_percent > 90
    for: 5m
  labels: { severity: warning }
    annotations:
    summary: "High CPU on {{ $labels.instance }}"
```

### Приложение

```yaml
  - alert: HighErrorRate
    expr: rate(http_requests_total{status=~"5.."}[5m]) / rate(http_requests_total[5m]) > 0.05
    for: 5m
  labels: { severity: critical, team: backend }
    annotations:
      summary: "High error rate on {{ $labels.service }}"
      runbook_url: "https://runbook.example.com/high-error-rate"
```

### Бизнес

Примеры: низкая конверсия, высокий процент брошенных корзин, падение выручки — метрики задаются экспортерами или приложением.

## Каналы уведомлений

### Email

В `receivers` задаётся `email_configs` с `to`, `send_resolved`, при необходимости шаблоны и заголовки (Subject). SMTP — в `global` или в конфиге receiver.

### Slack

`slack_configs`: `api_url` (Incoming Webhook), `channel`, `send_resolved: true`, шаблоны `title` и `text` (Go templates с `.GroupLabels`, `.Alerts`, `.CommonAnnotations`).

### PagerDuty

`pagerduty_configs`: `routing_key` (Integration Key Events API v2), `send_resolved: true`, при необходимости `description` и `details` из шаблонов.

Подробнее: [[alertmanager]], [[slack-alerting|Slack Alerting]], [[pagerduty]].

## Лучшие практики

1. **Именование** — понятные имена алертов (HttpRequestRateHigh, DatabaseConnectionPoolExhausted), не «Alert1» или «Problem».
2. **Аннотации** — всегда `summary`, `description`, `runbook_url` в правилах Prometheus.
3. **Пороги** — разумные значения и `for` (например, 5m), чтобы снизить флап.
4. **Маршрутизация** — по `severity` и `team`: критичные в PagerDuty, остальные в Slack/email.
5. **Группировка** — `group_by: ['alertname', 'cluster', 'service']`, настраивать `group_wait` и `repeat_interval`.
6. **Inhibition** — подавлять warning, если уже есть critical по тому же контексту; подавлять «медленный ответ», если сервис down.
7. **Runbooks** — у каждого алерта ссылка на runbook; внутри runbook: симптомы, шаги диагностики, действия, эскалация.
8. **Мониторинг алертинга** — алерт на `up{job="alertmanager"} == 0` и на рост сбоев доставки.

## Решение проблем

| Симптом | Возможная причина | Действие |
|--------|-------------------|----------|
| Алерты не срабатывают | Неверное правило или expr в Prometheus | Проверить `curl http://prometheus:9090/api/v1/rules` и `api/v1/alerts`; проверить `for` и пороги |
| Alertmanager не получает алерты | Prometheus не настроен на alertmanagers | В `prometheus.yml` задать `alerting.alertmanagers` с адресом Alertmanager:9093; проверить сеть и логи |
| Дублирование уведомлений | Слабая группировка или несколько Alertmanager без кластера | Усилить `group_by`, задать `group_interval`/`repeat_interval`; при HA включить `--cluster.peer` |
| Слишком много алертов | Много правил или низкие пороги | Увеличить `for`, объединить правила, использовать inhibition; пересмотреть пороги и группировку |

**Проверки:** логи Alertmanager (`docker logs alertmanager`), статус `GET http://alertmanager:9093/api/v2/status`, тестовый алерт через `POST /api/v2/alerts`.

## Частые вопросы

**Когда направлять алерты в PagerDuty, а когда в Slack?**
Критичные (downtime, полная недоступность) — в PagerDuty с эскалацией и онколлом. Warning и info — в Slack или email, чтобы не создавать усталость от страниц.

**Как уменьшить шум от флапующих алертов?**
Увеличить `for` в правиле Prometheus (например, 5–10 минут), чтобы алерт срабатывал только при стабильном нарушении. В Alertmanager настроить `group_interval` и `repeat_interval`, при необходимости inhibition.

**Нужен ли отдельный Alertmanager для staging?**
Можно один кластер Alertmanager с маршрутизацией по метке `env` (prod/staging) в разные каналы. Либо отдельный экземпляр для изоляции тестовых алертов от продового онколла.
