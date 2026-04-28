---
title: "Централизованное логирование для Java"
description: "Комплексное руководство по централизованному логированию в Java: сбор, хранение, поиск и анализ логов из распределенных систем с использованием ELK Stack, Fluentd, CloudWatch и других инструментов."
tags:
  - monitoring
  - logging
  - centralized-logging
type: "reference"
difficulty: "intermediate"
aliases:
  - "centralized logging"
prerequisites:
  - "[[logging-basics]]"
next:
  - "[[log-aggregation]]"
  - "[[elk-stack]]"
updated: "2026-04-20"
---
# Централизованное логирование для Java

Комплексное руководство по централизованному логированию в Java: сбор, хранение, поиск и анализ логов из распределенных систем с использованием ELK Stack, Fluentd, CloudWatch и других инструментов.

## Полезные ссылки

### ELK Stack
- [Elasticsearch Documentation](https://www.elastic.co/guide/en/elasticsearch/reference/current/index.html)
- [Logstash Documentation](https://www.elastic.co/guide/en/logstash/current/index.html)
- [Kibana Documentation](https://www.elastic.co/guide/en/kibana/current/index.html)
- [Filebeat Documentation](https://www.elastic.co/guide/en/beats/filebeat/current/index.html)

### Альтернативы
- [Fluentd Documentation](https://docs.fluentd.org/)
- [Fluent Bit](https://docs.fluentbit.io/)
- [Graylog Documentation](https://docs.graylog.org/)

### Облачные решения
- [AWS CloudWatch](https://docs.aws.amazon.com/cloudwatch/)
- [Google Cloud Logging](https://cloud.google.com/logging/docs)
- [Azure Monitor](https://docs.microsoft.com/en-us/azure/azure-monitor/)

### См. также
- [Основы логирования](logging-basics.md)
- [Структурированное логирование](structured-logging.md)
- [Агрегация логов](log-aggregation.md)

- [SLF4J для Java](slf4j.md)
- [Logback для Java](logback.md)
## Содержание

- [Введение в централизованное логирование](#введение-в-централизованное-логирование)
  - [Почему централизованное логирование?](#почему-централизованное-логирование)
  - [Архитектура](#архитектура)
- [ELK Stack](#elk-stack-1)
  - [Elasticsearch](#elasticsearch)
  - [Logstash](#logstash)
  - [Kibana](#kibana)
- [Fluentd и Fluent Bit](#fluentd-и-fluent-bit)
  - [Fluentd](#fluentd)
  - [Fluent Bit](#fluent-bit)
- [Облачные решения](#облачные-решения-1)
  - [AWS CloudWatch](#aws-cloudwatch)
  - [Google Cloud Logging](#google-cloud-logging)
  - [Azure Monitor](#azure-monitor)
- [Log shipping](#log-shipping)
  - [Filebeat](#filebeat)
- [Хранение и ротация](#хранение-и-ротация)
  - [ILM в Elasticsearch](#ilm-в-elasticsearch)
  - [Ротация файлов (Logback)](#ротация-файлов-logback)
- [Поиск и анализ](#поиск-и-анализ)
  - [Запросы Elasticsearch](#запросы-elasticsearch)
- [Мониторинг и алертинг](#мониторинг-и-алертинг)
  - [Watcher (пример)](#watcher-пример)
- [Безопасность](#безопасность)
  - [Санитизация в коде](#санитизация-в-коде)
- [Производительность](#производительность)
  - [Команды диагностики](#команды-диагностики)
- [Лучшие практики](#лучшие-практики)
- [Решение проблем](#решение-проблем)
- [Частые вопросы](#частые-вопросы)

## Введение в централизованное логирование

Централизованное логирование — это практика сбора, хранения и анализа логов из всех компонентов распределенной системы в едином месте.

### Почему централизованное логирование?

1. Visibility — полная видимость работы всех компонентов
2. Correlation — связь событий между сервисами
3. Search — быстрый поиск по всем логам
4. Alerting — автоматические оповещения о проблемах
5. Compliance — аудит и соответствие требованиям

### Архитектура

```text
Application Layer → Collection (Filebeat, Fluent Bit) → Processing (Logstash) → Storage (Elasticsearch) → Analysis (Kibana)
```

## ELK Stack

### Elasticsearch

```yaml
# elasticsearch.yml
cluster.name: my-application-logs
node.name: node-1
network.host: 0.0.0.0
http.port: 9200
discovery.type: single-node
xpack.security.enabled: true
```

```bash
# Шаблон индекса
curl -X PUT "localhost:9200/_template/logs_template" -H 'Content-Type: application/json' -d '{
    "index_patterns": ["logs-*"],
  "settings": { "number_of_shards": 3, "number_of_replicas": 1, "refresh_interval": "30s" },
    "mappings": {
      "properties": {
        "@timestamp": { "type": "date" },
        "level": { "type": "keyword" },
        "message": { "type": "text" },
        "correlationId": { "type": "keyword" },
        "service": { "type": "keyword" },
        "host": { "type": "keyword" }
      }
    }
  }'
```

### Logstash

```ruby
# logstash.conf
input {
  beats { port => 5044 }
  http { port => 8080; codec => json }
}

filter {
  if [message] =~ /^\{.*\}$/ {
    json { source => "message"; target => "parsed" }
    }
  mutate {
    copy => { "[parsed][@timestamp]" => "@timestamp", "[parsed][level]" => "level", "[parsed][message]" => "log_message", "[parsed][correlationId]" => "correlation_id" }
    add_field => { "service" => "%{[@metadata][beat][name]}", "host" => "%{[@metadata][beat][hostname]}", "environment" => "production" }
  }
  date { match => ["@timestamp", "yyyy-MM-dd'T'HH:mm:ss.SSSZ"]; target => "@timestamp" }
  mutate { remove_field => ["message", "parsed", "@version"] }
}

output {
  elasticsearch {
    hosts => ["localhost:9200"]
    index => "logs-%{+YYYY.MM.dd}"
    document_id => "%{correlation_id}"
  }
}
```

### Kibana

Индекс-паттерн: `logs-*`, поле времени: `@timestamp`. Дашборды и алерты настраиваются через UI или Saved Objects API.

## Fluentd и Fluent Bit

### Fluentd

```xml
# fluentd.conf
<source>
  @type forward
  port 24224
  bind 0.0.0.0
</source>
<source>
  @type tail
  path /var/log/application/*.log
  pos_file /var/log/fluentd/application.pos
  tag application.*
  format json
</source>
<filter application.>
  @type record_transformer
  <record>
    service ${tag}
    hostname "#{Socket.gethostname}"
  </record>
</filter>
<match application.>
  @type elasticsearch
  host localhost
  port 9200
  index_name logs
  logstash_format true
</match>
```

### Fluent Bit

```ini
# fluent-bit.conf
[INPUT]
    Name tail
    Path /var/log/application/*.log
    Parser json
    Tag app.*
[FILTER]
    Name record_modifier
    Match app.*
    Record hostname ${HOSTNAME}
    Record service application
[OUTPUT]
    Name es
    Match app.*
    Host elasticsearch
    Port 9200
    Index logs
```

## Облачные решения

### AWS CloudWatch

Агент CloudWatch Logs собирает файлы по конфигу (file_path, log_group_name, log_stream_name). В приложении можно использовать AWS SDK или Logback appender для прямой отправки.

### Google Cloud Logging

Структурированные логи через `LoggingOptions.getDefaultInstance().getService()` и JSON payload с severity. Поддерживается автоматический сбор с GCE/GKE.

### Azure Monitor

Application Insights: зависимость `applicationinsights-logging-logback`, в logback-spring.xml — `ApplicationInsightsAppender` с instrumentationKey.

## Log shipping

### Filebeat

```yaml
# filebeat.yml
filebeat.inputs:
- type: log
  enabled: true
  paths: ["/var/log/application/*.log"]
  fields: { service: my-application, environment: production }
  fields_under_root: true
  json: { keys_under_root: true, add_error_key: true }

processors:
- add_host_metadata: ~
- add_cloud_metadata: ~

output.elasticsearch:
  hosts: ["localhost:9200"]
  index: "logs-%{+yyyy.MM.dd}"
# Альтернатива: output.logstash: hosts: ["localhost:5044"]
```

## Хранение и ротация

### ILM в Elasticsearch

```bash
curl -X PUT "localhost:9200/_ilm/policy/logs_policy" -H 'Content-Type: application/json' -d '{
    "policy": {
      "phases": {
      "hot": { "min_age": "0ms", "actions": { "set_priority": { "priority": 100 } } },
      "warm": { "min_age": "30d", "actions": { "set_priority": { "priority": 50 }, "shrink": { "number_of_shards": 1 } } },
      "delete": { "min_age": "90d", "actions": { "delete": {} } }
    }
  }
}'
```

В шаблоне индекса указать: `index.lifecycle.name: logs_policy`, при необходимости `index.lifecycle.rollover_alias`.

### Ротация файлов (Logback)

TimeBasedRollingPolicy или SizeAndTimeBasedRollingPolicy с maxFileSize, maxHistory, totalSizeCap.

```xml
    <rollingPolicy class="ch.qos.logback.core.rolling.SizeAndTimeBasedRollingPolicy">
        <fileNamePattern>logs/application.%d{yyyy-MM-dd}.%i.log</fileNamePattern>
        <maxFileSize>50MB</maxFileSize>
        <maxHistory>14</maxHistory>
    </rollingPolicy>
```

## Поиск и анализ

### Запросы Elasticsearch

```bash
# Ошибки за последний час
curl -X GET "localhost:9200/logs-*/_search" -H 'Content-Type: application/json' -d '{
    "query": {
      "bool": {
        "must": [
          { "term": { "level": "ERROR" } },
          { "range": { "@timestamp": { "gte": "now-1h" } } }
        ]
      }
    },
    "size": 100,
    "sort": [{ "@timestamp": { "order": "desc" } }]
  }'

# По correlation ID
curl -X GET "localhost:9200/logs-*/_search" -H 'Content-Type: application/json' -d '{
  "query": { "term": { "correlationId": "abc-123-def" } },
    "sort": [{ "@timestamp": { "order": "asc" } }]
  }'
```

## Мониторинг и алертинг

Elasticsearch Watcher или Kibana Alerting (.es-query): правило по индексу `logs-*`, условие по полю level и диапазону @timestamp, порог (например, >50 ошибок за 15 мин), действие — email/Slack.

### Watcher (пример)

```bash
curl -X PUT "localhost:9200/_watcher/watch/error_rate_alert" -H 'Content-Type: application/json' -d '{
  "trigger": { "schedule": { "interval": "5m" } },
    "input": {
      "search": {
        "request": {
          "indices": ["logs-*"],
          "body": {
            "query": {
              "bool": {
                "must": [
                  { "term": { "level": "ERROR" } },
                  { "range": { "@timestamp": { "gte": "now-5m" } } }
                ]
              }
            }
          }
        }
      }
    },
  "condition": { "compare": { "ctx.payload.hits.total": { "gt": 10 } } },
    "actions": {
      "email_alert": {
        "email": {
          "to": ["devops@example.com"],
          "subject": "High Error Rate Alert",
          "body": "Found {{ctx.payload.hits.total}} errors in the last 5 minutes"
        }
      }
    }
  }'
```

## Безопасность

- Санитизация: маскировать password, secret, token, key в сообщениях и в map перед отправкой в хранилище.
- Шифрование: TLS для транспорта (Elasticsearch, Logstash, Beats), при необходимости шифрование данных в rest (xpack.security, keystore).
- Доступ: Kibana Spaces и роли для разграничения по командам/проектам.

### Санитизация в коде

```java
    private static final Set<String> SENSITIVE_KEYS = Set.of(
    "password", "secret", "token", "key", "credential", "authorization", "cookie", "sessionid");

    public static String sanitize(String message) {
    if (message == null) return null;
    for (String key : SENSITIVE_KEYS)
            message = message.replaceAll("(?i)" + key + "\\s*[:=]\\s*[^\\s,)]+", key + "=*");
        return message;
}
```

## Производительность

- Elasticsearch: refresh_interval 30s для логов, forcemerge для старых индексов, алиасы для rollover без простоя.
- Logstash: отбрасывать DEBUG через filter, batch (flush_size, idle_flush_time), fingerprint для дедупликации.
- Filebeat: bulk_max_size, worker, queue.mem (events, flush).

### Команды диагностики

```bash
# Здоровье кластера и индексов
curl "localhost:9200/_cluster/health?pretty"
curl "localhost:9200/logs-*/_stats?pretty"
curl "localhost:9200/_cat/shards?v&h=index,shard,prirep,state,node"

# Logstash: пайплайн и очередь
curl "localhost:9600/_node/stats/pipelines?pretty"

# Filebeat: метрики и registry
curl "localhost:5066/stats?pretty"
cat /var/lib/filebeat/registry/filebeat/data.json | jq '.[] | select(.source | contains("app.log"))'
```

## Лучшие практики

1. Единый формат логов (JSON) и обязательные поля: @timestamp, level, message, service, correlationId.
2. Retention через ILM: hot warm delete по возрасту и размеру.
3. Индекс-шаблоны с mappings и настройками по умолчанию для всех лог-индексов.
4. Мониторинг пайплайна: здоровье кластера ES, статистика пайплайнов Logstash, метрики Filebeat (harvester, queue).

## Решение проблем

| Симптом | Причина | Действие |
|--------|---------|----------|
| Жёлтый/красный статус кластера ES, unassigned shards | Нехватка узлов, диск, настройки allocation | Проверить `_cluster/health`, `_cat/shards?v`, лимиты диска; при необходимости временно увеличить replicas или перебалансировать |
| Логи не появляются в Kibana | Сбой Filebeat/Logstash, неверный индекс или pipeline | Проверить registry Filebeat, `_node/stats/pipelines` Logstash, наличие индексов `logs-*` и пайплайна ingest при прямой записи в ES |
| Дублирование записей | Несколько экземпляров Filebeat на одних путях или повторная обработка | Один экземпляр на источник; в Logstash — fingerprint по message+@timestamp и фильтр duplicate |

## Частые вопросы

**Как выбрать между Filebeat и Logstash на стороне сбора?** Filebeat — легкий шipper только для файлов и метрик; Logstash — когда нужна сложная фильтрация, обогащение (GeoIP, lookup) или несколько входов (файлы, HTTP, Kafka) в одном процессе.

**Нужен ли отдельный индекс под каждый сервис?** Часто достаточно одного паттерна `logs-*` с полем `service`; отдельные индексы (например, `audit-*`) имеют смысл при иных retention или доступе.

**Как ограничить рост хранилища?** Настроить ILM с фазами warm/delete, уменьшить max_history в ротации файлов, при необходимости сжимать старые индексы (forcemerge + best_compression) или выносить в холодное хранилище.
