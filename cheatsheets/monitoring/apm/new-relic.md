---
title: "New Relic APM"
description: "Мониторинг производительности приложений: транзакции, зависимости, исключения и метрики."
tags:
  - monitoring
  - apm
  - new-relic
difficulty: "intermediate"
prerequisites: []
next: []
updated: "2026-04-20"
---
# New Relic APM

Мониторинг производительности приложений: транзакции, зависимости, исключения и метрики.

## Введение

**New Relic** — платформа APM и observability. Собирает метрики приложений, транзакции, зависимости, ошибки и логи; интегрируется с Java (агент), Kubernetes и облачными провайдерами.

## Полезные ссылки

- [New Relic Documentation](https://docs.newrelic.com/)
- [New Relic Java Agent](https://docs.newrelic.com/docs/apm/agents/java-agent/get-started/introduction-new-relic-java/)
- [New Relic API (NerdGraph)](https://docs.newrelic.com/docs/apis/nerdgraph/)
- [NRQL Reference](https://docs.newrelic.com/docs/query-your-data/nrql-new-relic-query-language/)


### См. также
- [Лучшие практики мониторинга](../monitoring-best-practices.md)
- [Руководство по Observability](../observability-guide.md)
- [Infrastructure Monitoring (обзор)](../infrastructure-monitoring.md)
## Содержание

- [Основные возможности](#основные-возможности)
- [Интеграция с Java](#интеграция-с-java)
- [Конфигурация агента](#конфигурация-агента)
- [Метрики и дашборды](#метрики-и-дашборды)
- [Custom metrics и API агента](#custom-metrics-и-api-агента)
- [NRQL](#nrql)
- [Ошибки и исключения](#ошибки-и-исключения)
- [Алертинг](#алертинг)
- [Distributed Tracing](#distributed-tracing)
- [Интеграция с Kubernetes](#интеграция-с-kubernetes)
- [Таблица: компоненты New Relic](#таблица-компоненты-new-relic)
- [Таблица: золотые сигналы](#таблица-золотые-сигналы)
- [Глоссарий](#глоссарий)
- [Лучшие практики](#лучшие-практики)
- [Чек-лист перед внедрением](#чек-лист-перед-внедрением)
- [Решение проблем](#решение-проблем)
- [Частые вопросы](#частые-вопросы)
- [См. также](#см-также-1)

## Основные возможности

- **APM**: время ответа, throughput, ошибки по транзакциям и эндпоинтам.
- **Distributed Tracing**: трейсы запросов между сервисами.
- **Infrastructure**: метрики хостов, контейнеров, Kubernetes.
- **Logs**: централизованные логи с привязкой к транзакциям.
- **Browser (RUM)**: мониторинг фронтенда.
- **Synthetic Monitoring**: проверка доступности и сценариев извне.


## Интеграция с Java

Добавить агент в JVM при запуске:

```bash
java -javaagent:/path/to/newrelic.jar -jar app.jar
```

Или в `newrelic.yml` указать `license_key` и `app_name`. Агент автоматически инструментирует HTTP, JDBC, JMS, Redis и другие библиотеки — без изменения кода приложения.

При необходимости можно обернуть метод в транзакцию или добавить custom span через API агента.


## Конфигурация агента

Конфигурация через файл `newrelic.yml` или переменные окружения с префиксом `NEW_RELIC_`. Ключевые параметры:

- **license_key** — ключ лицензии (хранить в секретах).
- **app_name** — имя приложения в New Relic (например, `my-service-prod`).
- **distributed_tracing.enabled** — включить распределённую трассировку.
- **transaction_tracer.enabled** — включить трассировку транзакций.
- **error_collector.enabled** — сбор ошибок и исключений.
- **labels** — теги для фильтрации (env:prod, team:backend).

Пример фрагмента `newrelic.yml`:

```yaml
common: &default_settings
  license_key: ${NEW_RELIC_LICENSE_KEY}
  app_name: my-app
  distributed_tracing:
    enabled: true
  labels:
    env: production
    team: backend
```

| Параметр | Назначение |
|----------|------------|
| license_key | Ключ лицензии (хранить в секретах) |
| app_name | Имя приложения в UI |
| distributed_tracing.enabled | Распределённая трассировка |
| error_collector.enabled | Сбор ошибок и исключений |
| labels | Теги (env, team) для фильтрации |
| log_level | Уровень логов агента (info, fine, finer) |


## Метрики и дашборды

- **Application overview**: response time (среднее, медиана, p95, p99), throughput (RPM), error rate (%), Apdex.
- **Transactions**: список транзакций по URL/методам; время ответа, throughput, ошибки, разбивка по внешним вызовам.
- **Databases**: запросы, время выполнения, slow queries.
- **External services**: вызовы внешних HTTP-сервисов; время ответа и ошибки по каждому хосту.
- **Custom metrics**: свои метрики через API агента; отображение в дашбордах и NRQL.


## Custom metrics и API агента

В коде приложения можно отправлять кастомные метрики:

```java
import com.newrelic.api.agent.NewRelic;

NewRelic.recordMetric("Custom/OrdersCreated", count);
NewRelic.addCustomAttribute("orderId", order.getId());
NewRelic.noticeError(throwable);
```

Метрики отображаются в дашбордах; атрибуты привязываются к транзакции и ошибкам. Используйте для бизнес-метрик (количество заказов, конверсии) и контекста при расследовании.

Именование кастомных метрик с префиксом `Custom/` упрощает фильтрацию. Не отправляйте высококардинальные метрики (по каждому user_id) без агрегации.


## NRQL

**NRQL** (New Relic Query Language) — язык запросов к данным New Relic. Примеры:

```sql
SELECT average(duration) FROM Transaction WHERE appName = 'my-app' SINCE 1 hour ago
SELECT count(*) FROM Transaction WHERE error = true FACET name
SELECT percentile(duration, 95) FROM Transaction SINCE 30 minutes ago
SELECT average(cpuPercent) FROM SystemSample FACET hostname SINCE 1 hour ago
```

Эти запросы можно сохранять в виджетах дашбордов и использовать в условиях алертов.


## Ошибки и исключения

- Агент собирает необработанные исключения и HTTP-ошибки (4xx, 5xx); группировка по типу исключения и stack trace.
- Разбивка по транзакциям, эндпоинтам, времени; фильтрация по env, app_name.
- При вызове `NewRelic.noticeError(throwable)` или добавлении атрибутов — контекст привязывается к ошибке.
- В конфигурации можно указать исключения или коды ответа, которые не считать ошибками (например, 404 для health check).


## Алертинг

- **Условия**: пороги по метрикам (latency, error rate, throughput), по количеству ошибок, по SLA.
- **Политики алертов**: группа условий; несколько условий можно объединить в одну политику.
- **Каналы уведомлений**: email, Slack, PagerDuty, webhook. Настроить эскалацию (после N минут без ответа — эскалировать).
- **Runbooks**: в описании алерта указывать ссылку на runbook — что проверять и как устранять инцидент.
- **Избегать шума**: алертить на симптомы для пользователя (недоступность, высокая latency, рост ошибок).


## Distributed Tracing

- Включить `distributed_tracing.enabled: true` в агенте всех сервисов цепочки.
- Агент автоматически добавляет заголовки W3C Trace Context в исходящие HTTP-запросы и читает входящие.
- В UI New Relic — Service Map и цепочка span'ов по одному запросу; видно, какой сервис занял больше всего времени.
- При высокой нагрузке настроить sampling (например, 10% транзакций) для снижения объёма данных.


## Интеграция с Kubernetes

- Установка агента в кластер (DaemonSet или sidecar) для сбора метрик подов, нод, деплойментов.
- Метки пода/неймспейса можно передавать в New Relic для фильтрации (env, app, team).
- `license_key` хранить в Kubernetes Secret, монтировать как переменную окружения.
- Конфигурация (newrelic.yml без секретов) может храниться в репозитории.


## Таблица: компоненты New Relic

| Компонент | Назначение |
|-----------|------------|
| APM | Транзакции, latency, throughput, ошибки приложения |
| Distributed Tracing | Трейсы запросов между сервисами |
| Infrastructure | Метрики хостов, контейнеров, Kubernetes |
| Logs | Централизованные логи с привязкой к транзакциям |
| Browser (RUM) | Мониторинг фронтенда |
| Synthetic | Проверка доступности извне |
| NRQL | Язык запросов к данным |
| Alerts | Условия и каналы уведомлений |


## Таблица: золотые сигналы

| Сигнал | Где смотреть в New Relic |
|--------|---------------------------|
| Latency | APM Transactions duration (avg, p95, p99) |
| Traffic | APM Throughput (RPM) |
| Errors | APM Errors, Error rate в транзакциях |
| Saturation | Infrastructure CPU, Memory, Disk |


## Глоссарий

- **APM** — Application Performance Monitoring; мониторинг производительности приложения.
- **Transaction** — единица работы (например, HTTP-запрос) с длительностью и разбивкой по операциям.
- **Span** — одна операция в трейсе (HTTP-вызов, запрос к БД).
- **Trace** — цепочка span'ов по одному запросу (distributed trace).
- **NRQL** — New Relic Query Language; язык запросов к данным.
- **RUM** — Real User Monitoring; мониторинг фронтенда.
- **Apdex** — метрика удовлетворённости пользователей по времени ответа.


## Лучшие практики

- Использовать осмысленные имена приложений и транзакций.
- Включать distributed tracing для микросервисов.
- Единый trace_id по цепочке запросов: от алерта к метрикам, к трейсу, к логам.
- Настроить алерты на критичные метрики; не создавать избыточный шум.
- Хранить `license_key` в секретах (переменные окружения, vault, Kubernetes Secret).
- При высокой нагрузке включить sampling транзакций.
- Мониторить потребление ресурсов самим агентом.


## Чек-лист перед внедрением

- [ ] License key в секретах (env, vault).
- [ ] Осмысленные имена приложений (app_name) и теги (labels).
- [ ] Включить distributed tracing для микросервисов.
- [ ] Настроить алерты на критичные метрики (latency, error rate, availability).
- [ ] Не создавать избыточный шум алертами.
- [ ] Документировать runbooks для типовых алертов.
- [ ] При высокой нагрузке настроить sampling.


## Решение проблем

| Симптом | Возможная причина | Решение |
|--------|-------------------|---------|
| Приложение не появляется в New Relic | Неверный `license_key` или агент не подключён | Проверить `-javaagent` в параметрах запуска JVM, корректность ключа, логи агента |
| Высокое потребление памяти агентом | Слишком много инструментируемых транзакций | Включить sampling, отключить ненужные модули, увеличить интервал сбора |
| Distributed tracing не показывает цепочку | Не включён `distributed_tracing.enabled` в одном из сервисов | Убедиться, что все сервисы цепочки используют агент с включённым tracing |

## Частые вопросы

**Чем APM отличается от Infrastructure в New Relic?**
APM — метрики приложения (транзакции, эндпоинты, зависимости). Infrastructure — метрики хостов, контейнеров, Kubernetes (CPU, память, диск).

**Что такое Apdex?**
Метрика удовлетворённости пользователей по времени ответа. Классифицирует запросы как satisfied, tolerating и frustrated на основе порога (T).

**Как связать логи с транзакциями?**
Добавить trace_id в MDC логгера. В New Relic Logs поиск по trace_id вернёт все логи одного запроса, что ускоряет расследование инцидентов.


## См. также

- [Datadog APM](datadog.md)
- [Elastic APM](elastic-apm.md)
