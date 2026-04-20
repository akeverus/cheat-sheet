---
title: "StatsD"
description: "StatsD — лёгкий демон для приёма метрик по UDP (счётчики, таймеры, gauges, sets). Агрегирует данные и пересылает в бэкенды (Graphite, InfluxDB, Prometheus через statsd_exporter и др.). Широко используется для инструментирования приложений без блокирующих вызовов."
tags:
  - monitoring
  - metrics
  - statsd
difficulty: "intermediate"
prerequisites: []
next: []
updated: "2026-04-20"
---
# StatsD

StatsD — лёгкий демон для приёма метрик по UDP (счётчики, таймеры, gauges, sets). Агрегирует данные и пересылает в бэкенды (Graphite, InfluxDB, Prometheus через statsd_exporter и др.). Широко используется для инструментирования приложений без блокирующих вызовов.

## Полезные ссылки

### Официальная документация
- [StatsD — GitHub (Etsy)](https://github.com/statsd/statsd)
- [StatsD — Documentation](https://github.com/statsd/statsd/wiki)
- [Graphite — Documentation](https://graphite.readthedocs.io/)

### Ресурсы
- [Etsy — Measure Anything, Measure Everything](https://codeascraft.com/2011/02/15/measure-anything-measure-everything/)

### См. также
- [[micrometer|Micrometer]] — метрики в JVM, экспорт в StatsD
- [[prometheus|Prometheus]] — сбор и хранение метрик
- [[infrastructure-monitoring|Infrastructure monitoring]] — обзор мониторинга
- [Monitoring](../) — раздел мониторинга

- [[quarkus-actuator|Quarkus: Actuator — Health Checks и Metrics]]
- [[micronaut-actuator|Micronaut: Actuator — Health Checks, Metrics и Endpoints]]
## Содержание

- [Введение](#введение)
- [Протокол StatsD](#протокол-statsd)
- [Типы метрик](#типы-метрик)
- [Установка и запуск](#установка-и-запуск)
- [Конфигурация](#конфигурация)
- [Бэкенды](#бэкенды)
- [Клиенты (языки и библиотеки)](#клиенты-языки-и-библиотеки)
- [Интеграция с приложениями](#интеграция-с-приложениями)
- [Лучшие практики](#лучшие-практики)
- [Решение проблем](#решение-проблем)
- [Частые вопросы](#частые-вопросы)
- [Глоссарий](#глоссарий)
  - [Итоговые таблицы](#итоговые-таблицы)
- [statsd_exporter (Prometheus)](#statsd_exporter-prometheus)

## Введение

StatsD — сетевой демон, принимающий метрики по UDP в простом текстовом формате. Приложение отправляет строки вида `counter.name:1|c` или `response.time:120|ms`; StatsD агрегирует их (суммы, средние, перцентили) и с заданным интервалом (flush interval) пересылает в бэкенд хранения — чаще всего Graphite, а также InfluxDB, Prometheus (через statsd_exporter), Datadog и др.

**Зачем использовать StatsD:** неблокирующая отправка по UDP; простой протокол (одна строка на метрику); агрегация на стороне сервера; множество бэкендов и клиентов.

**Ограничения:** UDP не гарантирует доставку; при перегрузке пакеты могут теряться. StatsD сам по себе не хранит историю — только пересылает в бэкенд.


## Протокол StatsD

Формат одной метрики: **bucket:value|type** или **bucket:value|type|@sample_rate**.

- **bucket** — имя метрики (иерархия через точку, например app.orders.created).
- **value** — числовое значение (счётчик, время в мс, значение gauge и т.д.).
- **type** — тип: **c** (counter), **ms** (timer), **g** (gauge), **s** (set), **h** (histogram в части реализаций).
- **@sample_rate** — опционально; частота сэмплирования (0–1). Например `|c|@0.1` — учитывать 10% пакетов; StatsD умножит значение на 1/sample_rate для оценки полного счётчика.

Примеры:

```text
orders.count:5|c
api.latency:120|ms
queue.size:42|g
users.unique:user123|s
```

Несколько метрик в одном UDP-пакете разделяются переводом строки (`\n`).


## Типы метрик

| Тип    | Символ | Описание | Пример           |
|--------|--------|----------|------------------|
| Counter| c      | Монотонно растущий счётчик; за интервал суммируется | requests:1|c |
| Timer  | ms     | Длительность в миллисекундах; агрегируется в count, sum, mean, перцентили | latency:150|ms |
| Gauge  | g      | Текущее значение; перезаписывается или инкремент/декремент (+value, -value) | connections:10|g |
| Set    | s      | Уникальные значения за интервал (число уникальных) | users:id123|s |

Counter: каждое значение добавляется к счётчику за интервал. Timer: все значения за интервал агрегируются; бэкенд получает count, sum, mean, min, max, перцентили. Gauge: последнее отправленное значение или инкремент/декремент от текущего. Set: количество уникальных строк за интервал.


## Установка и запуск

StatsD изначально написан на Node.js (требуется Node.js v12+).

```bash
git clone https://github.com/statsd/statsd.git
cd statsd
npm install
node stats.js config.js
```

**Docker:**

```bash
docker run -d --name statsd -p 8125:8125/udp -p 8126:8126/tcp graphite/statsd
```

Порт 8126 — TCP для административных команд (stats, counters). UDP 8125 — приём метрик.

Проверка приёма:

```bash
echo "test.counter:1|c" | nc -u -w0 localhost 8125
```


## Конфигурация

Пример config.js (фрагмент):

```javascript
{
  port: 8125,
  flushInterval: 10000,
  backends: [ "./backends/graphite" ],
  graphite: {
    host: "graphite.example.com",
    port: 2003,
    prefix: "stats"
  },
  deleteIdleStats: true,
  deleteGauges: true
}
```

- **port** — UDP-порт приёма.
- **flushInterval** — интервал (мс) отправки агрегатов в бэкенд.
- **backends** — массив путей к бэкендам (graphite, console, influxdb и т.д.).
- **graphite** — хост, порт, префикс для Graphite.

Дополнительные бэкенды (InfluxDB, Prometheus через statsd_exporter, Datadog) подключаются через плагины; конфигурация зависит от реализации.


## Бэкенды

| Бэкенд        | Назначение |
|---------------|------------|
| Graphite | Классический бэкенд; Carbon принимает метрики по TCP на порту 2003. Иерархия имён (точки пути). |
| Console       | Вывод в консоль (отладка). |
| InfluxDB      | Пересылка в InfluxDB по HTTP или UDP. |
| Prometheus | Официального бэкенда StatsD Prometheus нет; используют **statsd_exporter**: приложение шлёт в statsd_exporter по UDP; Prometheus скрапит statsd_exporter. |
| Datadog       | Бэкенд/расширение для отправки в Datadog. |


## Клиенты (языки и библиотеки)

| Язык    | Библиотека / способ |
|---------|----------------------|
| Node.js | hot-shots, отправка UDP вручную |
| Java    | Micrometer (StatsdMeterRegistry), Java StatsD Client, Dropwizard metrics с StatsD reporter |
| Python  | statsd, python-statsd |
| Go      | go-statsd |
| Ruby    | statsd-ruby, datadog/statsd |
| PHP     | php-statsd, Domnikl/Statsd |
| .NET    | NStatsD, StatsdClient |

Общий принцип: открыть UDP-сокет на адрес StatsD (или statsd_exporter), отправлять строки в формате bucket:value|type. Буферизация и асинхронная отправка снижают накладные расходы.


## Интеграция с приложениями

**Spring Boot + Micrometer:** добавить micrometer-registry-statsd, настроить management.metrics.export.statsd.host, port, flavor. MeterRegistry будет StatsdMeterRegistry; все метрики автоматически отправляются в StatsD.

**Ручная отправка (Python):**

```python
import socket
def send_metric(name, value, type_='c'):
    sock = socket.socket(socket.AF_INET, socket.SOCK_DGRAM)
    sock.sendto(f"{name}:{value}|{type_}".encode(), ("127.0.0.1", 8125))
send_metric("app.requests", 1, "c")
send_metric("app.latency_ms", 45, "ms")
```

**Ручная отправка (bash):** `echo "app.requests:1|c" > /dev/udp/localhost/8125`


## Лучшие практики

1. **Именование** — иерархия через точку (service.component.metric); единый стиль (lowercase), не злоупотреблять глубиной.
2. **Частота отправки** — не отправлять каждое событие с высокой частотой; агрегировать в приложении или использовать sample_rate.
3. **UDP** — учитывать потери; для критичных метрик дублировать в лог или использовать TCP-бэкенд, если доступен.
4. **Flush interval** — согласовать с бэкендом (Graphite — типично 10–60 с); слишком частый flush увеличивает нагрузку на бэкенд.
5. **Кардинальность** — не использовать уникальные идентификаторы (userId, requestId) в имени метрики; ограниченный набор имён и тегов.
6. **Таймеры** — отправлять в ms; StatsD агрегирует в перцентили на стороне сервера.


## Решение проблем

| Симптом | Возможная причина | Действие |
|---------|-------------------|----------|
| Метрики не появляются в Graphite | StatsD не запущен; неверный graphiteHost/port; firewall | Проверить StatsD и сетевое соединение до Graphite; проверить backends в конфиге |
| Потеря метрик | Перегрузка UDP; буфер приложения переполнен | Увеличить буфер отправки; снизить частоту или использовать sample_rate; проверить flushInterval |
| Высокое потребление памяти StatsD | Много уникальных имён метрик (высокая кардинальность) | Ограничить число уникальных имён; включить deleteIdleStats; уменьшить flushInterval |
| Неверные значения счётчика при sample_rate | StatsD умножает на 1/sample_rate | Убедиться, что бэкенд корректно интерпретирует сэмплированные счётчики |
| Метрики в Prometheus | Нужен statsd_exporter | Настроить приложение на отправку в statsd_exporter; Prometheus скрапит statsd_exporter |


## Частые вопросы

**В чём разница между StatsD и Graphite?** StatsD — демон приёма и агрегации метрик по UDP; он не хранит данные, а пересылает их в бэкенд. Graphite — система хранения (Carbon) и визуализации (Graphite web). Типичный стек: приложение StatsD Graphite (Carbon).

**Можно ли использовать StatsD с Prometheus?** Напрямую Prometheus не принимает протокол StatsD. Используют statsd_exporter: приложение шлёт метрики в statsd_exporter по UDP; statsd_exporter экспортирует метрики в формате Prometheus; Prometheus делает scrape statsd_exporter.

**Надёжен ли UDP?** UDP не гарантирует доставку. В локальной сети потери обычно минимальны. Для критичных метрик можно дублировать в лог или использовать TCP-вариант (если бэкенд поддерживает).

**Как добавить теги?** В классическом StatsD тегов нет; иерархия только через точку в имени. В расширениях (Datadog, InfluxDB-бэкенд, statsd_exporter) поддерживаются теги в формате bucket:value|type|#tag1:value1,tag2:value2.


## Глоссарий

| Термин | Описание |
|--------|----------|
| StatsD | Демон приёма метрик по UDP, агрегация, пересылка в бэкенд |
| Bucket | Имя метрики (иерархия через точку) |
| Counter | Счётчик (тип c); суммируется за интервал |
| Timer | Таймер (тип ms); длительность в мс, агрегация в перцентили |
| Gauge | Текущее значение (тип g) |
| Set | Уникальные значения за интервал (тип s) |
| Flush interval | Интервал (мс) отправки агрегатов в бэкенд |
| Backend | Система приёма агрегированных метрик (Graphite, InfluxDB и т.д.) |
| Sample rate | Частота сэмплирования (0–1); StatsD экстраполирует счётчики |
| statsd_exporter | Конвертер StatsD Prometheus (приложение exporter Prometheus scrape) |

### Итоговые таблицы

**Формат строки метрики:** bucket (имя), value (число), type (c, ms, g, s), sample rate (опционально @0.1).

**Порты:**

| Порт | Протокол | Назначение |
|------|----------|------------|
| 8125 | UDP | Приём метрик |
| 8126 | TCP | Админ (stats, counters) в части реализаций |
| 2003 | TCP | Carbon (Graphite) — приём от StatsD |


## statsd_exporter (Prometheus)

statsd_exporter — компонент экосистемы Prometheus: принимает метрики в формате StatsD по UDP и экспортирует их в формате Prometheus для scrape. Приложение отправляет метрики на statsd_exporter (порт 9125 по умолчанию); Prometheus настраивает scrape на statsd_exporter:9112/metrics.

**Запуск:**

```bash
./statsd_exporter --statsd.listen-udp=:9125 --web.listen-address=:9112
```

**Маппинг имён:** по умолчанию точки заменяются на подчёркивания, суффиксы _count, _sum для таймеров. Кастомный маппинг — флаг --statsd.mapping-config (YAML). Пример:

```yaml
mappings:
  - match: app.requests.*.*
    name: app_requests_total
    labels:
      method: $1
      status: $2
    match_type: regex
```

Теги в расширенном формате Datadog/InfluxDB: bucket:value|type|#tag1:val1,tag2:val2. statsd_exporter может парсить теги при соответствующей настройке маппинга.

Для стека Prometheus приложение может слать метрики в формате StatsD в statsd_exporter; Prometheus собирает метрики с statsd_exporter без установки классического StatsD + Graphite.


**Заключение.** StatsD — простой и распространённый способ сбора метрик по UDP с агрегацией на стороне сервера и пересылкой в Graphite, InfluxDB или (через statsd_exporter) в Prometheus. Используйте единый стиль именования, ограничивайте кардинальность и учитывайте потери UDP. Для JVM-приложений удобна связка Micrometer + StatsdMeterRegistry. См. [StatsD Wiki](https://github.com/statsd/statsd/wiki), [statsd_exporter](https://github.com/prometheus/statsd_exporter).
