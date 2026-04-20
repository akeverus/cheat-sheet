---
title: "ELK Stack"
description: "ELK Stack (Elasticsearch, Logstash, Kibana) — набор инструментов с открытым исходным кодом для централизованного сбора, обработки, хранения и визуализации логов и метрик. Позволяет анализировать большие объёмы данных в реальном времени и строить дашборды."
tags:
  - monitoring
  - logging
  - elk-stack
difficulty: "intermediate"
prerequisites: []
next: []
updated: "2026-02-11"
---
# ELK Stack

ELK Stack (Elasticsearch, Logstash, Kibana) — набор инструментов с открытым исходным кодом для централизованного сбора, обработки, хранения и визуализации логов и метрик. Позволяет анализировать большие объёмы данных в реальном времени и строить дашборды.

## Полезные ссылки

- [Elasticsearch Documentation](https://www.elastic.co/guide/en/elasticsearch/reference/current/index.html)
- [Logstash Documentation](https://www.elastic.co/guide/en/logstash/current/index.html)
- [Kibana Documentation](https://www.elastic.co/guide/en/kibana/current/index.html)
- [Beats Documentation](https://www.elastic.co/guide/en/beats/libbeat/current/index.html)

## Содержание

- [Компоненты ELK Stack](#компоненты-elk-stack)
- [Elasticsearch](#elasticsearch)
- [Logstash](#logstash)
- [Kibana](#kibana)
- [Filebeat (Beats)](#filebeat-beats)
- [Архитектуры развёртывания](#архитектуры-развёртывания)
- [ILM и безопасность](#ilm-и-безопасность)
- [Мониторинг и производительность](#мониторинг-и-производительность)
- [Решение проблем](#решение-проблем)
- [Лучшие практики](#лучшие-практики)
- [Частые вопросы](#частые-вопросы)

## Компоненты ELK Stack

- **Elasticsearch** — поисковый движок и хранилище (индексы, документы, запросы).
- **Logstash** — приём, парсинг, обогащение и отправка логов в Elasticsearch (pipeline: input → filter → output).
- **Kibana** — UI для поиска, визуализаций и дашбордов.
- **Beats** (Filebeat, Metricbeat и др.) — лёгкие агенты сбора логов и метрик с хостов.

## Elasticsearch

### Установка и запуск

```bash
wget https://artifacts.elastic.co/downloads/elasticsearch/elasticsearch-8.6.0-linux-x86_64.tar.gz
tar -xzf elasticsearch-8.6.0-linux-x86_64.tar.gz
cd elasticsearch-8.6.0/
./bin/elasticsearch
curl -X GET "localhost:9200/?pretty"
```

### Конфигурация (elasticsearch.yml)

```yaml
cluster.name: my-elasticsearch-cluster
node.name: node-1
path.data: /var/lib/elasticsearch
path.logs: /var/log/elasticsearch
network.host: 0.0.0.0
http.port: 9200
discovery.seed_hosts: ["host1:9300", "host2:9300"]
cluster.initial_master_nodes: ["node-1", "node-2", "node-3"]
xpack.security.enabled: true
```

JVM: `-Xms4g -Xmx4g` (в jvm.options).

### Индексы и CRUD

```bash
# Индекс с маппингом
curl -X PUT "localhost:9200/my-index" -H 'Content-Type: application/json' -d'
{
  "mappings": {
    "properties": {
      "timestamp": { "type": "date" },
      "level": { "type": "keyword" },
      "message": { "type": "text", "analyzer": "standard" },
      "service": { "type": "keyword" }
    }
  },
  "settings": { "number_of_shards": 3, "number_of_replicas": 1 }
}'

# Документ
curl -X POST "localhost:9200/my-index/_doc" -H 'Content-Type: application/json' -d'
{"timestamp":"2023-01-01T12:00:00Z","level":"INFO","message":"User login","service":"auth-service"}'

# Получение по ID
curl -X GET "localhost:9200/my-index/_doc/1"

# Bulk
curl -X POST "localhost:9200/_bulk" -H 'Content-Type: application/x-ndjson' -d'
{"index":{"_index":"my-index","_id":"1"}}
{"timestamp":"2023-01-01T12:00:00Z","message":"Log 1"}
{"index":{"_index":"my-index","_id":"2"}}
{"timestamp":"2023-01-01T12:01:00Z","message":"Log 2"}
'
```

Просмотр индексов: `curl -X GET "localhost:9200/_cat/indices?v"`. Удаление: `curl -X DELETE "localhost:9200/my-index"`.

## Logstash

### Pipeline (logstash.conf)

```ruby
input {
  beats { port => 5044 }
  http { port => 8080; codec => json }
}

filter {
  if [type] == "nginx" {
    grok { match => { "message" => "%{NGINXACCESS}" } }
    date { match => ["timestamp", "dd/MMM/yyyy:HH:mm:ss Z"]; target => "@timestamp" }
    geoip { source => "client_ip"; target => "geoip" }
  }
  if [type] == "java" {
    grok { match => { "message" => "%{TIMESTAMP_ISO8601:timestamp} %{LOGLEVEL:level} %{DATA:logger} - %{GREEDYDATA:message}" } }
    mutate { lowercase => ["level"] }
  }
  mutate { add_field => { "environment" => "production" } }
  if [level] == "debug" { drop {} }
}

output {
  elasticsearch {
    hosts => ["elasticsearch:9200"]
    index => "logs-%{+YYYY.MM.dd}"
    user => "logstash_writer"
    password => "${ES_PASSWORD}"
  }
  stdout { codec => rubydebug }
}
```

### Фильтры (кратко)

- **grok** — парсинг текста по шаблонам.
- **date** — разбор даты в @timestamp.
- **mutate** — lowercase, rename, remove_field, convert и т.д.
- **json** — парсинг JSON из поля (source/target).
- **geoip** — обогащение по IP (source → target).
- **metrics** — счётчики/агрегаты (meter, flush_interval).

## Kibana

### kibana.yml

```yaml
server.port: 5601
server.host: "0.0.0.0"
elasticsearch.hosts: ["http://elasticsearch:9200"]
elasticsearch.username: "kibana_system"
elasticsearch.password: "${KIBANA_PASSWORD}"
kibana.defaultAppId: "discover"
xpack.security.enabled: true
```

### Индексный паттерн и Saved Objects

```bash
# Создание индексного паттерна через API
curl -X POST "localhost:5601/api/saved_objects/index-pattern/logs-*" \
  -H "kbn-xsrf: true" -H "Content-Type: application/json" \
  -d '{"attributes":{"title":"logs-*","timeFieldName":"@timestamp"}}'

# Экспорт дашбордов
curl -X GET "localhost:5601/api/saved_objects/_export" -H "kbn-xsrf: true" \
  -d '{"type":["dashboard","visualization","search","index-pattern"],"includeReferencesDeep":true}' --output dashboards.ndjson

# Импорт
curl -X POST "localhost:5601/api/saved_objects/_import" -H "kbn-xsrf: true" -F file=@dashboards.ndjson
```

## Filebeat (Beats)

### filebeat.yml

```yaml
filebeat.inputs:
- type: log
  enabled: true
  paths: ["/var/log/app/*.log", "/var/log/nginx/*.log"]
  exclude_lines: ['^DEBUG']
  multiline.pattern: '^[0-9]{4}-[0-9]{2}-[0-9]{2}'
  multiline.negate: true
  multiline.match: after

- type: container
  enabled: true
  paths: ["/var/lib/docker/containers/*/*.log"]

output.logstash:
  hosts: ["logstash:5044"]

processors:
- add_host_metadata: {}
- add_fields: { target: '', fields: { environment: production } }
```

Metricbeat для метрик ОС/Docker/Kubernetes — отдельный конфиг (metricbeat.yml) с модулями system, docker, kubernetes и output.elasticsearch.

## Архитектуры развёртывания

### Docker Compose (сокращённо)

```yaml
services:
  elasticsearch:
    image: docker.elastic.co/elasticsearch/elasticsearch:8.6.0
    environment:
      - discovery.type=single-node
      - xpack.security.enabled=false
      - "ES_JAVA_OPTS=-Xms512m -Xmx512m"
    ports: ["9200:9200", "9300:9300"]
    volumes: [elasticsearch-data:/usr/share/elasticsearch/data]
  logstash:
    image: docker.elastic.co/logstash/logstash:8.6.0
    volumes: [./logstash/pipeline:/usr/share/logstash/pipeline]
    ports: ["5044:5044", "8080:8080"]
    depends_on: [elasticsearch]
  kibana:
    image: docker.elastic.co/kibana/kibana:8.6.0
    environment: [ELASTICSEARCH_HOSTS=http://elasticsearch:9200]
    ports: ["5601:5601"]
    depends_on: [elasticsearch]
  filebeat:
    image: docker.elastic.co/beats/filebeat:8.6.0
    volumes: [./filebeat/filebeat.yml:/usr/share/filebeat/filebeat.yml, /var/lib/docker/containers:/var/lib/docker/containers:ro]
    depends_on: [logstash]
volumes:
  elasticsearch-data:
networks: { default: {} }
```

В Kubernetes: StatefulSet для Elasticsearch (3 реплики, PVC), Deployment для Kibana и Logstash, DaemonSet для Filebeat при необходимости.

## ILM и безопасность

### ILM политика (пример)

```bash
curl -X PUT "localhost:9200/_ilm/policy/logs-policy" -H 'Content-Type: application/json' -d'
{
  "policy": {
    "phases": {
      "hot": { "min_age": "0ms", "actions": { "rollover": { "max_size": "50gb", "max_age": "30d" } } },
      "warm": { "min_age": "30d", "actions": { "shrink": { "number_of_shards": 1 }, "forcemerge": { "max_num_segments": 1 } } },
      "delete": { "min_age": "90d", "actions": { "delete": {} } }
    }
  }
}'
```

Привязка к индексному шаблону: в settings индекса указать `index.lifecycle.name: logs-policy` и при необходимости `index.lifecycle.rollover_alias`.

### Безопасность

- X-Pack: создание пользователей и ролей через API `_security/user`, `_security/role`; для Logstash/Kibana — отдельные пользователи с ограниченными правами на индексы logs-*.
- TLS: в elasticsearch.yml включить xpack.security.http.ssl.enabled и transport ssl; для Logstash input beats — ssl_certificate, ssl_key; в output elasticsearch — hosts с https и cacert.

## Мониторинг и производительность

- **Elasticsearch:** `_cluster/health`, `_nodes/stats`, `_cat/recovery`, `_nodes/hot_threads`.
- **Logstash:** `_node/stats/pipelines`, `_node/hot_threads` (порт 9600).
- **Kibana:** `api/status`, `api/task_manager/_health`.

Тюнинг: bootstrap.memory_lock, thread_pool размеры, JVM heap (до ~50% RAM узла). Logstash: pipeline.workers, pipeline.batch.size; тяжёлые фильтры (grok, geoip) оптимизировать или переносить на Beats.

Watcher (X-Pack): создание watch с trigger (schedule), input (search по logs-*), condition (например, error_count > 100), actions (email, webhook).

## Решение проблем

| Проблема | Действие |
|----------|----------|
| Kibana не видит индексы или поля | Проверить индексный паттерн (Stack Management → Index Patterns): имя типа logs-*, поле времени @timestamp. После смены маппинга обновить паттерн или визуализации. |
| Logstash падает с out of memory | Увеличить heap в jvm.options; снизить pipeline.batch.size и pipeline.workers в logstash.yml; упростить или перенести тяжёлые фильтры на Filebeat. |
| Кластер Elasticsearch red или yellow | Red: часть шардов недоступна — _cluster/allocation/explain, логи узлов (диск, память, сбой узла). Yellow: реплики не размещены (норма для одного узла); для production добавить узлы. |

Диагностика: slow logs в elasticsearch.yml (index.search.slowlog.*, index.indexing.slowlog.*); pipeline stats и hot threads для Logstash.

## Лучшие практики

- Индексирование: шаблоны индексов и ILM для ротации; размер шарда до ~50 GB; refresh_interval под нагрузку.
- Сбор: Filebeat/Logstash с минимальной обработкой на агенте; буферизация и batch; структурированные логи (JSON).
- Безопасность: TLS и аутентификация (X-Pack); доступ по ролям; не экспонировать кластер в интернет без защиты.
- Надёжность: мультинодовый кластер с репликацией; мониторинг heap и дисков; алерты на красный статус и задержки индексации.
- JVM heap до 50% RAM узла; SSD для данных; при необходимости hot/warm архитектура.

## Частые вопросы

**Kibana не видит индексы или поля.** Проверьте индексный паттерн (Stack Management → Index Patterns): он должен совпадать с именем индекса (например, logs-*). Поле времени — @timestamp. После смены маппинга может потребоваться обновить паттерн или пересоздать визуализации.

**Logstash падает с out of memory.** Увеличьте heap в jvm.options (-Xmx/-Xms). Снизьте pipeline.batch.size и pipeline.workers в logstash.yml. Проверьте тяжёлые фильтры (grok, geoip) и по возможности перенесите парсинг на Filebeat.

**Кластер Elasticsearch в статусе red или yellow.** Red — часть шардов недоступна: проверьте _cluster/allocation/explain и логи узлов; частые причины — диск заполнен, нехватка памяти, сбой узла. Yellow — реплики не размещены (норма для одного узла); для production добавьте узлы или настройте number_of_replicas после появления второго узла.
## См. также

- [[prometheus|Prometheus]] — альтернативная система мониторинга
- [[grafana|Grafana]] — визуализация
- [[docker-basics|Docker]] — контейнеризация
