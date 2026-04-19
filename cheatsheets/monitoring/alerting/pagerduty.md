---
title: "PagerDuty"
description: "PagerDuty — платформа управления инцидентами и онколл-дежурствами. Принимает алерты из систем мониторинга (Prometheus, Alertmanager, Datadog, New Relic и др.), создаёт инциденты, управляет эскалациями и уведомлениями по расписанию дежурств (schedules) и политикам эскалации (escal"
tags:
  - monitoring
  - alerting
  - pagerduty
difficulty: "intermediate"
prerequisites: []
next: []
updated: "2026-02-11"
---
# PagerDuty

PagerDuty — платформа управления инцидентами и онколл-дежурствами. Принимает алерты из систем мониторинга (Prometheus, Alertmanager, Datadog, New Relic и др.), создаёт инциденты, управляет эскалациями и уведомлениями по расписанию дежурств (schedules) и политикам эскалации (escalation policies).

## Полезные ссылки

### Официальная документация
- [PagerDuty — Developer Documentation](https://developer.pagerduty.com/api-reference/)
- [PagerDuty — Events API v2](https://developer.pagerduty.com/docs/events-api-v2/overview/)
- [PagerDuty — Incident Management](https://support.pagerduty.com/docs/incident-management)

### Ресурсы
- [Integration Guide](https://support.pagerduty.com/docs/integration-guide)
- [Best Practices](https://support.pagerduty.com/docs/best-practices)

### См. также
- [Alertmanager](alertmanager.md) — маршрутизация алертов в PagerDuty
- [Slack Alerting](slack-alerting.md) — уведомления в Slack
- [Alerting](../) — раздел алертинга
- [Monitoring](../) — обзор мониторинга

## Содержание

- [Введение](#введение)
- [Основные концепции](#основные-концепции)
- [Регистрация и настройка](#регистрация-и-настройка)
- [Events API v2](#events-api-v2)
- [Управление инцидентами и онколл](#управление-инцидентами-и-онколл)
- [Интеграция с Alertmanager](#интеграция-с-alertmanager)
- [Лучшие практики](#лучшие-практики)
- [Решение проблем](#решение-проблем)
- [Частые вопросы](#частые-вопросы)
- [Глоссарий и таблицы](#глоссарий-и-таблицы)

## Введение

PagerDuty — облачный сервис для управления инцидентами и дежурствами. Системы мониторинга отправляют в PagerDuty события (алерты); PagerDuty создаёт инциденты, назначает их по расписанию дежурств и применяет политики эскалации. Дежурный получает уведомление (звонок, push, SMS, Slack, email) и может принять инцидент, добавить заметки, разрешить его.

**Зачем PagerDuty:** централизация алертов из разных источников, эскалация при отсутствии реакции, расписание дежурств, аналитика (время реакции, разрешения), интеграции (Slack, Jira, ServiceNow, Events API для кастомных источников).

## Основные концепции

| Концепция | Описание |
|-----------|----------|
| Service | Сущность, на которую приходят алерты (например, «Backend API»). Имеет Integration(s) и Escalation Policy. |
| Integration | Точка входа для алертов; у сервиса одна или несколько. Events API v2 использует Integration Key (routing key). |
| Incident | Один инцидент — один алерт или группа. Статусы: triggered, acknowledged, resolved. |
| Escalation Policy | Правило: кому и в каком порядке назначать инцидент (уровни, таймеры, round-robin по schedule). |
| Schedule | Расписание дежурств: кто когда онколл. |
| Event | Сообщение от внешней системы (trigger, acknowledge, resolve) через Events API. |

## Регистрация и настройка

1. Зарегистрироваться на [pagerduty.com](https://www.pagerduty.com/), создать организацию и при необходимости team.
2. Создать Service: Services → New Service — имя, Escalation Policy, Alert Grouping.
3. Добавить Integration к сервису: тип Events API v2. Получить Routing Key (Integration Key).
4. Настроить Escalation Policy: уровни (например, Level 1 — онколл по расписанию), таймеры эскалации (например, 15 минут без ответа).
5. Создать Schedule и добавить пользователей в слоты; привязать Escalation Policy к Schedule для Level 1.
6. У пользователей указать контактные методы (телефон, email, push).

После этого можно отправлять события по Events API v2 с routing_key сервиса.

## Events API v2

**Endpoint:** `POST https://events.pagerduty.com/v2/enqueue`

**Trigger (создать/обновить инцидент):**

```json
{
  "routing_key": "YOUR_INTEGRATION_KEY",
  "event_action": "trigger",
  "dedup_key": "unique-key-optional",
  "payload": {
    "summary": "Instance 10.0.0.1 down",
    "severity": "critical",
    "source": "prometheus",
    "custom_details": {
      "job": "node",
      "instance": "10.0.0.1:9100"
    }
  },
  "client": "My Monitoring",
  "client_url": "https://monitoring.example.com"
}
```

- **routing_key** — ключ интеграции Events API v2.
- **event_action** — trigger, acknowledge или resolve.
- **dedup_key** — опционально; одинаковый ключ группирует события в один инцидент. Для acknowledge/resolve обязателен.
- **payload.summary** — краткое описание. **payload.severity** — critical, error, warning, info. **payload.source** — источник. **payload.custom_details** — произвольный объект.

**Acknowledge:** отправить event_action: "acknowledge" с тем же dedup_key. **Resolve:** event_action: "resolve" с тем же dedup_key.

Ответ при успехе: 202 Accepted, тело `{"status":"success","message":"Event processed","dedup_key":"..."}`. Ошибки — 4xx с описанием (400 — неверный JSON/поля, 403 — неверный routing_key, 429 — rate limit).

## Управление инцидентами и онколл

В веб-интерфейсе: Acknowledge (принять к работе), Resolve (закрыть), Add note, Reassign, Add responders, Snooze. Schedule определяет текущего дежурного; при срабатывании алерта инцидент назначается ему. Escalation Policy задаёт цепочку: Level 1 — онколл по Schedule, таймер 15 мин; при отсутствии ответа — Level 2 (например, тимлид) и т.д.

Контактные методы: SMS, телефон, email, push (мобильное приложение PagerDuty). В Notification Rules настраивается порядок (сначала push, затем SMS, затем звонок). Поддержка тихих часов и Support Hours на уровне сервиса.

## Интеграция с Alertmanager

В alertmanager.yml в receivers добавляется pagerduty_configs:

```yaml
receivers:
  - name: 'pagerduty-critical'
    pagerduty_configs:
      - routing_key: 'YOUR_PAGERDUTY_INTEGRATION_KEY'
        send_resolved: true
        severity: critical
```

В route направить критичные алерты (по метке severity: critical) в этот receiver. Alertmanager при срабатывании отправит trigger; при переходе алерта в resolved (если send_resolved: true) отправит resolve. Dedup_key формируется из меток алерта, поэтому один алерт не создаёт множество инцидентов.

Подробнее: [Alertmanager](alertmanager.md).

## Лучшие практики

1. Один сервис на домен/команду; не смешивать несвязанные системы.
2. Использовать dedup_key при отправке через Events API (например, alertname + instance) для дедупликации.
3. Отправлять resolve при разрешении алерта в мониторинге.
4. Направлять в PagerDuty только критичные; warning/info — в Slack или лог.
5. Escalation: таймеры не менее 5–10 минут; при необходимости несколько уровней с увеличивающимся интервалом.
6. Schedule: учитывать часовые пояса и Override для отпусков.
7. Перед продом проверить тестовый trigger и resolve.

## Решение проблем

| Симптом | Возможная причина | Действие |
|--------|-------------------|----------|
| Инцидент не создаётся | Неверный routing_key или интеграция отключена | Проверить ключ в настройках сервиса, тип интеграции Events API v2; ответ API (4xx) |
| Дублирование инцидентов | Разные dedup_key для одного алерта или отсутствие dedup_key | Задавать стабильный dedup_key (alertname + instance); в Alertmanager группировка даёт один инцидент на группу |
| Resolve не закрывает инцидент | resolve не отправляется или с другим dedup_key | В Alertmanager включить send_resolved: true; dedup_key при resolve должен совпадать с trigger |
| Дежурный не получает уведомление | Неверный контактный метод, тихие часы, пользователь не в Schedule | Проверить контактные методы, Schedule, Escalation Policy, Notification Rules |
| Слишком много инцидентов | Много алертов с разными dedup_key | Увеличить группировку в Alertmanager (group_by); объединять похожие алерты в один dedup_key |
| 400 Bad Request от Events API | Неверный JSON или отсутствует обязательное поле | Проверить routing_key, event_action, для trigger — payload с summary, severity, source |
| 202 Accepted, но инцидент не появляется | Ключ от другого сервиса или отключённая интеграция | Проверить сервис и статус интеграции Events API v2 в PagerDuty |

## Частые вопросы

**В чём разница между Events API v1 и v2?**  
Events API v2 — рекомендуемый: один endpoint, поля payload, dedup_key, severity. v1 использует service_key и другой формат; новые интеграции лучше делать на v2.

**Нужен ли отдельный сервис PagerDuty для staging?**  
Рекомендуется отдельный сервис (или интеграция) для staging, чтобы не создавать продовые инциденты из тестовых алертов.

**Как автоматически разрешать инцидент при resolve в Alertmanager?**  
При send_resolved: true Alertmanager сам отправляет resolve с тем же dedup_key; дополнительная настройка не нужна.

**Можно ли отправлять в PagerDuty из скрипта?**  
Да, любой HTTP POST на https://events.pagerduty.com/v2/enqueue с корректным JSON и routing_key создаёт или обновляет инцидент.

**Как ограничить уведомления по времени (тихие часы)?**  
В PagerDuty в Schedule или Escalation Policy настраиваются Support Hours и поведение вне этих часов (например, только уведомление в приложении без звонка). Либо не отправлять в PagerDuty в нерабочие часы для некритичных алертов на стороне источника.

**Как группировать несколько алертов в один инцидент?**  
Один инцидент определяется dedup_key. Отправлять алерты с одним и тем же dedup_key (например, общий ключ для «все алерты по сервису X за окно»). Либо настроить Alert grouping в настройках сервиса PagerDuty.

## Глоссарий и таблицы

| Термин | Описание |
|--------|----------|
| Service | Логический сервис в PagerDuty; имеет интеграции и Escalation Policy |
| Integration | Точка входа алертов; Events API v2 даёт routing_key |
| Incident | Запись об инциденте (triggered, acknowledged, resolved) |
| Escalation Policy | Правило эскалации по уровням и таймерам |
| Schedule | Расписание дежурств |
| Routing Key | Ключ интеграции Events API v2 |
| Dedup Key | Ключ дедупликации; один ключ = один инцидент |
| Event Action | trigger, acknowledge, resolve |

### Events API v2 — поля тела запроса

| Поле | Обязательное | Описание |
|------|--------------|----------|
| routing_key | да | Ключ интеграции Events API v2 |
| event_action | да | trigger, acknowledge, resolve |
| dedup_key | для ack/resolve; рекомендуется для trigger | Ключ дедупликации |
| payload | для trigger | summary, severity, source + опционально custom_details, images, links |
| client, client_url | нет | Имя и URL клиента |

### Severity в payload

| Значение | Использование |
|----------|----------------|
| critical | Немедленная реакция, звонок |
| error | Важная ошибка |
| warning | Предупреждение |
| info | Информирование |

### Настройка Alertmanager (фрагмент)

```yaml
route:
  receiver: 'default'
  group_by: ['alertname', 'job']
  routes:
    - match: { severity: critical }
      receiver: 'pagerduty-prod'
      group_wait: 10s
      repeat_interval: 30m

receivers:
  - name: 'pagerduty-prod'
    pagerduty_configs:
      - routing_key: 'YOUR_PAGERDUTY_ROUTING_KEY'
        send_resolved: true
        description: '{{ .CommonAnnotations.summary }}'
        client: 'Alertmanager'
        client_url: 'http://alertmanager:9093'
```

Рекомендации по dedup_key: стабильность (один логический алерт — один ключ), формат строка (например, instance-down-10.0.0.1 или {alertname}-{job}-{instance}). В Alertmanager dedup_key строится из групповых меток. Секреты (routing_key, API key) хранить в Kubernetes Secret или секрет-менеджере.
