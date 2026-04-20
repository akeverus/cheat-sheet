---
title: "Alertmanager (Prometheus)"
description: "Alertmanager — компонент экосистемы Prometheus: маршрутизация, группировка, подавление (inhibition, silence) алертов и доставка уведомлений в Slack, email, PagerDuty, webhook и др. Работает в связке с Prometheus по протоколу Alertmanager API."
tags:
  - monitoring
  - alerting
  - alertmanager
difficulty: "intermediate"
prerequisites: []
next: []
updated: "2026-02-11"
---
# Alertmanager (Prometheus)

Alertmanager — компонент экосистемы Prometheus: маршрутизация, группировка, подавление (inhibition, silence) алертов и доставка уведомлений в Slack, email, PagerDuty, webhook и др. Работает в связке с Prometheus по протоколу Alertmanager API.

## Полезные ссылки

### Официальная документация
- [Alertmanager — Documentation](https://prometheus.io/docs/alerting/latest/alertmanager/)
- [Alertmanager — Configuration](https://prometheus.io/docs/alerting/latest/configuration/)
- [Prometheus — Alerting](https://prometheus.io/docs/alerting/latest/overview/)

### Ресурсы
- [Prometheus — Best practices](https://prometheus.io/docs/practices/alerting/)
- [Grafana — Alerting](https://grafana.com/docs/grafana/latest/alerting/)

### См. также
- [[prometheus|Prometheus]] — сбор метрик
- [Alerting](../) — раздел алертинга
- [[slack-alerting|Slack Alerting]] — уведомления в Slack
- [[pagerduty|PagerDuty]] — инцидент-менеджмент
- [Monitoring](../) — обзор мониторинга

## Содержание

- [Введение](#введение)
- [Установка и настройка](#установка-и-настройка)
- [Конфигурация](#конфигурация)
- [Маршрутизация и группировка](#маршрутизация-и-группировка)
- [Receivers (каналы уведомлений)](#receivers-каналы-уведомлений)
- [Inhibition и Silence](#inhibition-и-silence)
- [Интеграция с Prometheus](#интеграция-с-prometheus)
- [High Availability](#high-availability)
- [Лучшие практики](#лучшие-практики)
- [Решение проблем](#решение-проблем)
- [Частые вопросы](#частые-вопросы)
- [Глоссарий и таблицы](#глоссарий-и-таблицы)

## Введение

Alertmanager принимает алерты от Prometheus Server (или других клиентов, совместимых с Alertmanager API), группирует их, применяет правила подавления и отправляет уведомления в настроенные каналы. Без Alertmanager алерты Prometheus не доставляются пользователям.

**Зачем Alertmanager:** группировка (много алертов в одно уведомление), маршрутизация по меткам (разные receivers), подавление (inhibition, silence), дедупликация, повторная отправка по repeat_interval.

**Основные концепции:** Alert (метки, аннотации), Route (дерево правил → receiver), Receiver (Slack, email, PagerDuty, webhook), Group (алерты с одинаковыми group_by), Inhibition, Silence.

## Установка и настройка

**Бинарник:** скачать с [releases](https://github.com/prometheus/alertmanager/releases), распаковать, запуск:

```bash
./alertmanager --config.file=alertmanager.yml --storage.path=./data
```

**Docker:**

```bash
docker run -d --name alertmanager \
  -v $(pwd)/alertmanager.yml:/etc/alertmanager/alertmanager.yml \
  -p 9093:9093 \
  quay.io/prometheus/alertmanager:latest \
  --config.file=/etc/alertmanager/alertmanager.yml \
  --storage.path=/alertmanager/data
```

**Kubernetes:** Helm chart `prometheus-community/kube-prometheus-stack` или Deployment + ConfigMap. По умолчанию порт 9093; веб-интерфейс: `http://localhost:9093`.

## Конфигурация

Конфиг — YAML (`alertmanager.yml`). Секции: `global`, `templates`, `route`, `receivers`, `inhibit_rules`.

### Минимальный пример

```yaml
global:
  resolve_timeout: 5m

route:
  receiver: 'default'
  group_by: ['alertname', 'job']
  group_wait: 30s
  group_interval: 5m
  repeat_interval: 4h

receivers:
  - name: 'default'
    slack_configs:
      - send_resolved: true
        channel: '#alerts'
        api_url: 'https://hooks.slack.com/services/XXX/YYY/ZZZ'
```

### global и route

| Параметр | Описание |
|----------|----------|
| global.resolve_timeout | Время, после которого алерт считается разрешённым |
| global.smtp_smarthost, smtp_from | SMTP для email |
| route.receiver | Receiver по умолчанию |
| route.group_by | Метки для группировки |
| route.group_wait | Ожидание перед первой отправкой группы |
| route.group_interval | Минимальный интервал между уведомлениями группы |
| route.repeat_interval | Интервал повторной отправки для активной группы |
| route.routes | Дочерние маршруты (match, match_re, receiver, continue) |

Пример маршрутизации по severity:

```yaml
route:
  receiver: 'slack-default'
  group_by: ['alertname', 'job']
  routes:
    - match: { severity: critical }
      receiver: 'pagerduty-critical'
    - match: { severity: warning }
      receiver: 'slack-warnings'
```

## Маршрутизация и группировка

Алерт приходит с метками; Alertmanager обходит дерево route, проверяет match/match_re и направляет в receiver. Алерты с одинаковыми значениями `group_by` объединяются в одну группу — одно уведомление на группу. Это снижает шум при массовом срабатывании.

## Receivers (каналы уведомлений)

**Slack:** `slack_configs` с `api_url` (webhook), `channel`, `send_resolved`, шаблоны `title`, `text` (Go templates).

**Email:** в `global` задать smtp_*; в receiver — `email_configs` с `to`, `send_resolved`, при необходимости `headers.Subject`.

**PagerDuty:** `pagerduty_configs` с `service_key`/`routing_key`, `send_resolved`, `description` (шаблон).

**Webhook:** `webhook_configs` с `url`; Alertmanager отправляет POST с JSON (алерты, commonLabels, commonAnnotations). Используется для кастомных систем и Telegram (через промежуточный сервис).

## Inhibition и Silence

**Inhibition:** если алерт с метками source_match активен, алерты с target_match не отправляются. Пример: при ClusterDown не слать InstanceDown по тому же job.

```yaml
inhibit_rules:
  - source_match: { alertname: 'ClusterUnavailable' }
    target_match: { alertname: 'InstanceDown' }
    equal: ['job']
```

**Silence:** временное отключение алертов по матчерам (создаётся через API или UI). Для плановых работ или отключения шумного алерта.

```bash
curl -X POST -H "Content-Type: application/json" -d '{
  "matchers": [{"name": "alertname", "value": "HighCPU"}],
  "startsAt": "2026-01-31T10:00:00Z",
  "endsAt": "2026-01-31T12:00:00Z",
  "createdBy": "ops",
  "comment": "Planned maintenance"
}' http://localhost:9093/api/v2/silences
```

## Интеграция с Prometheus

В `prometheus.yml`:

```yaml
alerting:
  alertmanagers:
    - static_configs:
        - targets: ['localhost:9093']
      timeout: 10s
```

Правила алертов задаются в Prometheus (rule_files). При срабатывании Prometheus отправляет алерт в Alertmanager по HTTP; Alertmanager группирует, применяет inhibit/silence и шлёт в receivers.

## High Availability

Запуск нескольких экземпляров с `--cluster.peer` (адреса других экземпляров). Экземпляры реплицируют состояние; каждое уведомление отправляется один раз. В `prometheus.yml` можно указать несколько alertmanagers; дедупликация на стороне кластера Alertmanager.

```bash
# Instance 1
./alertmanager --config.file=alertmanager.yml --cluster.peer=host2:9094
# Instance 2
./alertmanager --config.file=alertmanager.yml --cluster.peer=host1:9094
```

Порт 9094 — кластерный протокол.

## Лучшие практики

1. Группировка: `group_by: ['alertname', 'job']`, не по instance без необходимости.
2. Метка severity (critical, warning, info) и маршрутизация: critical → PagerDuty, остальные → Slack.
3. В правилах Prometheus задавать summary, description, runbook_url.
4. resolve_timeout согласовать с интервалом отправки Prometheus.
5. Silence для плановых работ; не заглушать шумные алерты долгим silence — править правило в Prometheus.
6. Тестирование: тестовый алерт через `POST /api/v2/alerts` или amtool.
7. В проде — минимум два экземпляра с --cluster.peer.

## Решение проблем

| Симптом | Возможная причина | Действие |
|--------|-------------------|----------|
| Алерты не приходят в Slack/email | Неверный receiver, webhook или SMTP | Проверить api_url, smtp_*, логи Alertmanager; проверить маршрут по меткам алерта |
| Дублирование уведомлений | Несколько Alertmanager без кластеризации | Включить HA с --cluster.peer или оставить один экземпляр |
| Алерты не доходят до Alertmanager | Prometheus не настроен или сеть | Проверить prometheus.yml → alerting.alertmanagers, доступность порта 9093, логи Prometheus |
| Silence не срабатывает | Матчеры не совпадают с метками алертов | Сверить метки алерта в UI Alertmanager с matchers silence |
| Слишком много/мало групп | Неверный group_by или group_wait | Настроить group_by (меньше меток — больше групп; не включать instance без нужды) |
| Ошибка загрузки конфига | Синтаксис YAML, неверное поле | `amtool check-config alertmanager.yml` или логи при старте |
| PagerDuty не создаёт инцидент | Неверный integration key или формат v1/v2 | Проверить ключ, использовать routing_key для Events API v2 |

В UI нет алертов при firing в Prometheus — проверить доступность Alertmanager из Prometheus и список targets. Алерты приходят дважды — два независимых Alertmanager в alertmanagers без кластера.

## Частые вопросы

**Чем Alertmanager отличается от алертинга в Grafana?**
Alertmanager — отдельный компонент для приёма алертов от Prometheus, группировки и доставки. Grafana имеет встроенный алертинг по своим правилам и может отправлять алерты в Alertmanager как в канал или показывать алерты Alertmanager в дашбордах.

**Нужен ли Alertmanager, если используется только Grafana?**
Если алерты только в Grafana и уведомления идут через Grafana — не обязателен. Нужен, когда алерты генерирует Prometheus и нужна централизованная маршрутизация и группировка.

**Как отправить тестовый алерт?**
`POST http://localhost:9093/api/v2/alerts` с телом JSON (массив алертов с labels, annotations, startsAt). Либо amtool alert add.

**Как включить тишину по расписанию?**
Alertmanager не поддерживает расписание для silence из коробки. Используют CronJob или скрипт, создающий/удаляющий silence через API в нужное время.

**Поддерживает ли Alertmanager Telegram?**
Официально нет. Используют webhook: сервис принимает POST от Alertmanager и шлёт в Telegram Bot API.

**Inhibition vs Silence?**
Inhibition задаётся в конфиге статически (если A активен, B не показывать). Silence — динамический, создаётся через UI/API на период; срабатывает по matchers.

## Глоссарий и таблицы

| Термин | Описание |
|--------|----------|
| Alert | Уведомление от Prometheus (метки, аннотации, время) |
| Route | Правило маршрутизации; дерево match/match_re → receiver |
| Receiver | Канал уведомлений (Slack, email, PagerDuty, webhook) |
| Group | Алерты с одинаковыми group_by; одно уведомление на группу |
| Inhibition | При активном алерте A не отправлять алерты B |
| Silence | Временное отключение по matchers (API/UI) |
| Firing / Resolved | Алерт активен / разрешён |

### Секции alertmanager.yml

| Секция | Назначение |
|--------|------------|
| global | smtp_*, slack_api_url, resolve_timeout |
| templates | Пути к файлам шаблонов |
| route | Дерево маршрутизации |
| receivers | slack_configs, email_configs, pagerduty_configs, webhook_configs |
| inhibit_rules | Правила подавления |

### API v2 (основное)

| Метод | Путь | Описание |
|-------|------|----------|
| GET | /api/v2/status | Статус, конфиг, кластер |
| GET | /api/v2/alerts | Список алертов |
| POST | /api/v2/alerts | Отправить алерты |
| GET/POST/DELETE | /api/v2/silences | Список / создать / удалить silence |

Метрики на `/metrics`: alertmanager_alerts, alertmanager_silences, alertmanager_notifications_total, alertmanager_notification_latency_seconds.
