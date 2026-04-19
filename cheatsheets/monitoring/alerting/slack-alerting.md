---
title: "Slack Alerting"
description: "Настройка доставки алертов и уведомлений в Slack: каналы, Incoming Webhooks, Slack API, форматирование (Block Kit, mrkdwn), интеграция с Prometheus/Alertmanager и другими системами мониторинга."
tags:
  - monitoring
  - alerting
  - slack-alerting
difficulty: "intermediate"
prerequisites: []
next: []
updated: "2026-02-11"
---
# Slack Alerting

Настройка доставки алертов и уведомлений в Slack: каналы, Incoming Webhooks, Slack API, форматирование (Block Kit, mrkdwn), интеграция с Prometheus/Alertmanager и другими системами мониторинга.

## Полезные ссылки

### Официальная документация
- [Slack — Incoming Webhooks](https://api.slack.com/messaging/webhooks)
- [Slack — Block Kit](https://api.slack.com/block-kit)
- [Slack — Messaging](https://api.slack.com/messaging)

### Ресурсы
- [Slack API — Sending messages](https://api.slack.com/messaging/sending)
- [Alertmanager — Slack configuration](https://prometheus.io/docs/alerting/latest/configuration/#slack_config)

### См. также
- [Alertmanager](alertmanager.md) — маршрутизация алертов в Slack
- [PagerDuty](pagerduty.md) — инцидент-менеджмент, интеграция со Slack
- [Alerting](../) — раздел алертинга
- [Monitoring](../) — обзор мониторинга

## Содержание

- [Введение](#введение)
- [Incoming Webhooks](#incoming-webhooks)
- [Конфигурация Alertmanager для Slack](#конфигурация-alertmanager-для-slack)
- [Форматирование и Block Kit](#форматирование-и-block-kit)
- [Интеграция с другими системами](#интеграция-с-другими-системами)
- [Лучшие практики](#лучшие-практики)
- [Решение проблем](#решение-проблем)
- [Частые вопросы](#частые-вопросы)
- [Глоссарий и таблицы](#глоссарий-и-таблицы)

## Введение

Slack используется как канал доставки уведомлений от систем мониторинга: Prometheus через Alertmanager, Datadog, PagerDuty, Grafana. Сообщения могут приходить в общие каналы (#alerts, #incidents) или в личные сообщения. Формат задаётся Incoming Webhooks (HTTP POST с JSON) или Slack API (гибче, с Block Kit).

**Зачем Slack для алертов:** централизация в одном месте, быстрая реакция и обсуждение, интеграции из коробки, история в канале.

## Incoming Webhooks

Incoming Webhook — отправка сообщения в Slack одним HTTP POST на уникальный URL. URL создаётся в настройках приложения Slack (Incoming Webhooks → Add New Webhook to Workspace), привязан к каналу. Тело запроса — JSON с полем `text`, опционально `blocks` (Block Kit), `attachments`.

**Минимальный пример:**

```json
{
  "text": "Instance 10.0.0.1 is down"
}
```

С указанием канала и имени отправителя (если разрешено в приложении):

```json
{
  "channel": "#alerts",
  "username": "Alertmanager",
  "icon_emoji": ":warning:",
  "text": "Instance 10.0.0.1 is down"
}
```

Рекомендация Slack — не более 1 сообщения в секунду на webhook; при превышении возможен 429 Too Many Requests.

## Конфигурация Alertmanager для Slack

В `alertmanager.yml` в секции receivers добавляется slack_configs:

```yaml
receivers:
  - name: 'slack-default'
    slack_configs:
      - channel: '#alerts'
        api_url: 'https://hooks.slack.com/services/T00000000/B00000000/XXXXXXXXXXXXXXXXXXXXXXXX'
        send_resolved: true
        title: '{{ .Status | toUpper }}: {{ .CommonLabels.alertname }}'
        text: '{{ range .Alerts }}{{ .Annotations.summary }}{{ end }}'
```

- **channel** — канал (#alerts); может переопределять канал webhook, если приложение разрешает.
- **api_url** — URL Incoming Webhook (секрет; хранить в Secret или переменной окружения).
- **send_resolved** — отправлять уведомление при переходе алерта в resolved.
- **title**, **text** — Go templates; данные: .Alerts, .CommonLabels, .CommonAnnotations, .Status.

Глобальный webhook можно задать в `global.slack_api_url`. Подробнее: [Alertmanager](alertmanager.md).

## Форматирование и Block Kit

Slack поддерживает **mrkdwn**: `*жирный*`, `_курсив_`, `` `код` ``, `<URL|текст>` (ссылка). В шаблонах Alertmanager можно использовать эту разметку для имён алертов и ссылок на runbook.

**Block Kit** — конструктор сообщений из блоков (header, section, кнопки). Отправка через Incoming Webhook или chat.postMessage с полем `blocks`. Alertmanager по умолчанию не поддерживает Block Kit (только title, text, color в attachments); для полноценного Block Kit нужен промежуточный сервис: Alertmanager → webhook вашего сервиса → преобразование в blocks → Slack.

Пример простого сообщения с блоками:

```json
{
  "text": "Fallback: InstanceDown",
  "blocks": [
    { "type": "header", "text": { "type": "plain_text", "text": "FIRING: InstanceDown", "emoji": true } },
    { "type": "section", "fields": [
      { "type": "mrkdwn", "text": "*Severity:*\ncritical" },
      { "type": "mrkdwn", "text": "*Instance:*\n10.0.0.1" }
    ]},
    { "type": "section", "text": { "type": "mrkdwn", "text": "<https://runbooks.example.com/instance-down|Open Runbook>" } }
  ]
}
```

## Интеграция с другими системами

- **Alertmanager** — встроенная поддержка slack_configs.
- **PagerDuty** — приложение PagerDuty for Slack показывает инциденты в канале и кнопки Acknowledge/Resolve.
- **Grafana** — в контактных пунктах можно указать Slack (webhook); алерты Grafana приходят в выбранный канал.
- **Datadog, New Relic** — в настройках интеграций указывается Slack webhook.
- Кастомный скрипт — любой код может отправлять HTTP POST на Incoming Webhook с JSON.

## Лучшие практики

1. Отдельный канал для алертов (#alerts или #alerts-<team>), не смешивать с общим чатом.
2. URL webhook не коммитить; хранить в Kubernetes Secret, переменных окружения или секрет-менеджере.
3. Включать send_resolved в Alertmanager, чтобы видеть разрешение инцидента.
4. Осмысленные title и text с ссылками на runbook и дашборды (аннотации в правилах Prometheus).
5. Настраивать group_by в Alertmanager, чтобы не засыпать канал десятками сообщений по одному типу алерта.
6. Rate limit: группировать алерты в Alertmanager; не более ~1 msg/sec на webhook.

## Решение проблем

| Симптом | Возможная причина | Действие |
|--------|-------------------|----------|
| Сообщения не приходят в Slack | Неверный URL webhook, приложение отключено, канал удалён | Проверить URL в настройках приложения Slack, права приложения на канал; тестовый POST с curl |
| 429 Too Many Requests | Превышен лимит отправки | Увеличить группировку в Alertmanager, уменьшить частоту алертов |
| Текст обрезается или не форматируется | Лимит длины сообщения или некорректный mrkdwn | Сократить шаблон; проверить экранирование <, >, & в mrkdwn |
| Resolved не приходит | В Alertmanager не включён send_resolved | Добавить send_resolved: true в slack_configs |
| Канал не переопределяется | Webhook привязан к фиксированному каналу | Создать отдельный webhook для нужного канала или разрешить переопределение в приложении |
| Дублирование сообщений | Два Alertmanager без кластера или два receiver с одним webhook | Включить кластеризацию Alertmanager или убрать дублирующий receiver |

Тест webhook: `curl -X POST -H 'Content-Type: application/json' -d '{"text":"Test"}' WEBHOOK_URL`

## Частые вопросы

**Как создать Incoming Webhook в Slack?**  
Slack → Settings → Integrations → Apps → Build (или Browse). Создать приложение → Incoming Webhooks → On → Add New Webhook to Workspace → выбрать канал → скопировать URL.

**Можно ли отправлять в личные сообщения (DM)?**  
Incoming Webhook по умолчанию привязан к каналу. Для DM нужен Slack API (chat.postMessage с channel: user_id) и Bot Token с правом chat:write.

**Поддерживает ли Alertmanager Block Kit?**  
Нет, только title, text и attachments. Для Block Kit нужен промежуточный сервис, который принимает webhook от Alertmanager и пересылает в Slack в формате blocks.

**Как добавить кнопки в сообщение?**  
Кнопки задаются в Block Kit (блок actions с button). Alertmanager этого не поддерживает напрямую; реализуется через промежуточный сервис или через PagerDuty for Slack (Acknowledge/Resolve для инцидентов).

**Как сделать, чтобы алерт выделялся цветом?**  
В slack_configs задать color: 'danger' (красный), 'warning' (жёлтый), 'good' (зелёный для resolved).

## Глоссарий и таблицы

| Термин | Описание |
|--------|----------|
| Incoming Webhook | URL для отправки сообщения в Slack одним POST; привязан к приложению и каналу |
| Block Kit | Система блоков для сообщений (header, section, actions и др.) |
| mrkdwn | Формат разметки Slack (*жирный*, _курсив_, ссылки) |
| Slack API | REST API для отправки сообщений (требует токен) |
| Bot Token | Токен приложения с правами (например, chat:write) для chat.postMessage |

### Поля slack_configs (Alertmanager)

| Параметр | Описание | По умолчанию |
|----------|----------|--------------|
| api_url | URL Incoming Webhook | из global.slack_api_url |
| channel | Канал (#name) | из webhook |
| username | Имя отправителя | Alertmanager |
| send_resolved | Отправлять при resolved | false |
| title | Заголовок (шаблон) | — |
| text | Текст (шаблон) | — |
| color | good, warning, danger, hex | — |

### Сравнение способов отправки

| Способ | Требования | Использование |
|--------|------------|--------------|
| Incoming Webhook | URL webhook | Alertmanager, скрипты, интеграции |
| Slack API (chat.postMessage) | Bot Token | Кастомные сервисы, треды, DM |

### Данные в шаблоне Alertmanager

- .Alerts — список алертов в группе
- .CommonLabels, .CommonAnnotations — общие метки и аннотации
- .Status — firing или resolved
- .ExternalURL — URL Alertmanager (--web.external-url)

Рекомендуемые аннотации в Prometheus: summary, description, runbook_url, dashboard_url.
