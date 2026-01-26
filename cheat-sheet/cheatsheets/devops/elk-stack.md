# ELK Stack

ELK Stack (Elasticsearch, Logstash, Kibana) - это мощный набор инструментов с открытым исходным кодом для централизованного сбора, обработки, хранения и визуализации логов и метрик. ELK позволяет анализировать большие объемы данных в реальном времени и строить дашборды для мониторинга систем.

## Содержание
- [Компоненты ELK Stack](#компоненты-elk-stack)
- [Filebeat (Beats)](#filebeat-beats)
- [Архитектуры развертывания](#архитектуры-развертывания)
- [Индекс lifecycle management](#индекс-lifecycle-management)
- [Безопасность](#безопасность)
- [Мониторинг и алертинг](#мониторинг-и-алертинг)
- [Производительность и оптимизация](#производительность-и-оптимизация)
- [Troubleshooting](#troubleshooting)

## Компоненты ELK Stack

### Elasticsearch

#### Основы Elasticsearch
```bash
# Установка Elasticsearch
wget https://artifacts.elastic.co/downloads/elasticsearch/elasticsearch-8.6.0-linux-x86_64.tar.gz
tar -xzf elasticsearch-8.6.0-linux-x86_64.tar.gz
cd elasticsearch-8.6.0/

# Запуск
./bin/elasticsearch

# Проверка состояния
curl -X GET "localhost:9200/?pretty"
```

#### Конфигурация кластера
```yaml
# elasticsearch.yml
cluster.name: my-elasticsearch-cluster
node.name: node-1
path.data: /var/lib/elasticsearch
path.logs: /var/log/elasticsearch

network.host: 0.0.0.0
http.port: 9200

discovery.seed_hosts: ["host1:9300", "host2:9300"]
cluster.initial_master_nodes: ["node-1", "node-2", "node-3"]

xpack.security.enabled: true
xpack.security.http.ssl.enabled: true
xpack.security.transport.ssl.enabled: true

# JVM настройки
-Xms4g
-Xmx4g
```

#### Создание индексов
```bash
# Создание индекса с маппингом
curl -X PUT "localhost:9200/my-index" -H 'Content-Type: application/json' -d'
{
  "mappings": {
    "properties": {
      "timestamp": {
        "type": "date"
      },
      "level": {
        "type": "keyword"
      },
      "message": {
        "type": "text",
        "analyzer": "standard"
      },
      "service": {
        "type": "keyword"
      },
      "user_id": {
        "type": "long"
      },
      "response_time": {
        "type": "float"
      },
      "tags": {
        "type": "keyword"
      }
    }
  },
  "settings": {
    "number_of_shards": 3,
    "number_of_replicas": 1
  }
}'

# Просмотр индексов
curl -X GET "localhost:9200/_cat/indices?v"

# Удаление индекса
curl -X DELETE "localhost:9200/my-index"
```

#### CRUD операции
```bash
# Создание документа
curl -X POST "localhost:9200/my-index/_doc" -H 'Content-Type: application/json' -d'
{
  "timestamp": "2023-01-01T12:00:00Z",
  "level": "INFO",
  "message": "User login successful",
  "service": "auth-service",
  "user_id": 12345,
  "response_time": 0.234,
  "tags": ["login", "success"]
}'

# Получение документа по ID
curl -X GET "localhost:9200/my-index/_doc/1"

# Обновление документа
curl -X POST "localhost:9200/my-index/_update/1" -H 'Content-Type: application/json' -d'
{
  "doc": {
    "status": "resolved"
  }
}'

# Удаление документа
curl -X DELETE "localhost:9200/my-index/_doc/1"

# Bulk операции
curl -X POST "localhost:9200/_bulk" -H 'Content-Type: application/x-ndjson' -d'
{"index": {"_index": "my-index", "_id": "1"}}
{"timestamp": "2023-01-01T12:00:00Z", "message": "Log message 1"}
{"index": {"_index": "my-index", "_id": "2"}}
{"timestamp": "2023-01-01T12:01:00Z", "message": "Log message 2"}
'
```

### Logstash

#### Конфигурация pipeline
```ruby
# logstash.conf
input {
  beats {
    port => 5044
    ssl => true
    ssl_certificate => "/etc/logstash/ssl/logstash.crt"
    ssl_key => "/etc/logstash/ssl/logstash.key"
  }

  http {
    port => 8080
    codec => json
  }

  kafka {
    bootstrap_servers => "kafka:9092"
    topics => ["app-logs", "system-logs"]
    codec => json
    group_id => "logstash-consumer"
  }
}

filter {
  # Парсинг логов
  if [type] == "nginx" {
    grok {
      match => { "message" => "%{NGINXACCESS}" }
    }

    date {
      match => [ "timestamp", "dd/MMM/yyyy:HH:mm:ss Z" ]
      target => "@timestamp"
    }

    geoip {
      source => "client_ip"
      target => "geoip"
    }
  }

  if [type] == "java" {
    grok {
      match => { "message" => "%{TIMESTAMP_ISO8601:timestamp} %{LOGLEVEL:level} %{DATA:logger} - %{GREEDYDATA:message}" }
    }

    mutate {
      lowercase => [ "level" ]
    }
  }

  # Обогащение данных
  mutate {
    add_field => {
      "environment" => "production"
      "datacenter" => "us-west-2"
    }
  }

  # Фильтрация
  if [level] == "debug" {
    drop {}
  }
}

output {
  elasticsearch {
    hosts => ["elasticsearch:9200"]
    index => "logs-%{+YYYY.MM.dd}"
    user => "logstash_writer"
    password => "${ES_PASSWORD}"
    ssl => true
    cacert => "/etc/logstash/ssl/ca.crt"
  }

  stdout {
    codec => rubydebug
  }
}
```

#### Фильтры Logstash
```ruby
# Grok фильтр для парсинга
filter {
  grok {
    match => {
      "message" => "%{IP:client_ip} %{WORD:method} %{URIPATH:request} %{NUMBER:status} %{NUMBER:bytes} %{QUOTEDSTRING:referrer} %{QUOTEDSTRING:user_agent}"
    }
    patterns_dir => ["/etc/logstash/patterns"]
  }
}

# Date фильтр
filter {
  date {
    match => ["timestamp", "ISO8601", "UNIX", "UNIX_MS"]
    target => "@timestamp"
    timezone => "UTC"
  }
}

# Mutate фильтр
filter {
  mutate {
    lowercase => ["level", "method"]
    uppercase => ["country_code"]
    replace => { "status" => "success" }
    split => { "tags" => "," }
    join => { "tags" => "," }
    merge => { "all_tags" => "tags" }
    rename => { "old_field" => "new_field" }
    remove_field => ["unwanted_field"]
    convert => {
      "response_time" => "float"
      "user_count" => "integer"
    }
  }
}

# JSON фильтр
filter {
  json {
    source => "message"
    target => "parsed_json"
    remove_field => ["message"]
  }
}

# GeoIP фильтр
filter {
  geoip {
    source => "client_ip"
    target => "geoip"
    database => "/etc/logstash/geoip/GeoLite2-City.mmdb"
  }
}

# Metrics фильтр
filter {
  metrics {
    meter => ["events_per_second", "%{host}"]
    add_tag => "metric"
    flush_interval => 10
  }
}
```

### Kibana

#### Установка и настройка
```yaml
# kibana.yml
server.port: 5601
server.host: "0.0.0.0"
server.name: "my-kibana-server"

elasticsearch.hosts: ["http://elasticsearch:9200"]
elasticsearch.username: "kibana_system"
elasticsearch.password: "${KIBANA_PASSWORD}"

kibana.index: ".kibana"
kibana.defaultAppId: "discover"

# Security
xpack.security.enabled: true
xpack.security.encryptionKey: "something_at_least_32_characters"
xpack.security.session.idleTimeout: "1h"
xpack.security.session.lifespan: "24h"

# Logging
logging.dest: "/var/log/kibana/kibana.log"
logging.verbose: false
```

#### Создание индексного паттерна
```bash
# Через API
curl -X POST "localhost:5601/api/saved_objects/index-pattern/logs-*" \
  -H "kbn-xsrf: true" \
  -H "Content-Type: application/json" \
  -d '{
    "attributes": {
      "title": "logs-*",
      "timeFieldName": "@timestamp"
    }
  }'
```

#### Saved Objects API
```bash
# Экспорт дашбордов
curl -X GET "localhost:5601/api/saved_objects/_export" \
  -H "kbn-xsrf: true" \
  -d '{
    "type": ["dashboard", "visualization", "search", "index-pattern"],
    "includeReferencesDeep": true
  }' \
  --output dashboards.ndjson

# Импорт дашбордов
curl -X POST "localhost:5601/api/saved_objects/_import" \
  -H "kbn-xsrf: true" \
  -F file=@dashboards.ndjson
```

## Filebeat (Beats)

### Конфигурация Filebeat
```yaml
# filebeat.yml
filebeat.inputs:
- type: log
  enabled: true
  paths:
    - /var/log/app/*.log
    - /var/log/nginx/*.log
  exclude_lines: ['^DEBUG']
  include_lines: ['ERROR', 'WARN']
  multiline.pattern: '^[0-9]{4}-[0-9]{2}-[0-9]{2}'
  multiline.negate: true
  multiline.match: after

- type: container
  enabled: true
  paths:
    - /var/lib/docker/containers/*/*.log
  processors:
  - add_docker_metadata:
      host: "unix:///var/run/docker.sock"

filebeat.modules:
- module: nginx
  access:
    enabled: true
    var.paths: ["/var/log/nginx/access.log*"]
  error:
    enabled: true
    var.paths: ["/var/log/nginx/error.log*"]

- module: system
  syslog:
    enabled: true
  auth:
    enabled: true

output.logstash:
  hosts: ["logstash:5044"]
  ssl.certificate_authorities: ["/etc/ssl/certs/ca.crt"]
  ssl.certificate: "/etc/ssl/certs/filebeat.crt"
  ssl.key: "/etc/ssl/private/filebeat.key"

processors:
- add_host_metadata:
    netinfo.enabled: true
- add_cloud_metadata: ~
- add_fields:
    target: ''
    fields:
      environment: production
      datacenter: us-west-2
```

### Metricbeat для метрик
```yaml
# metricbeat.yml
metricbeat.config.modules:
  path: ${path.config}/modules.d/*.yml
  reload.enabled: false

metricbeat.modules:
- module: system
  metricsets:
    - cpu
    - load
    - memory
    - network
    - process
    - process_summary
    - socket_summary
    - entropy
    - core
    - diskio
    - socket
    - service
    - users
  enabled: true
  period: 10s
  processes: ['.*']

- module: docker
  metricsets:
    - container
    - cpu
    - diskio
    - healthcheck
    - info
    - memory
    - network
  hosts: ["unix:///var/run/docker.sock"]
  enabled: true
  period: 10s

- module: kubernetes
  enabled: true
  metricsets:
    - node
    - pod
    - container
    - volume
    - system
  period: 10s
  hosts: ["https://kubernetes.default.svc:443"]
  bearer_token_file: /var/run/secrets/kubernetes.io/serviceaccount/token
  ssl.verification_mode: "none"

output.elasticsearch:
  hosts: ["elasticsearch:9200"]
  username: "metricbeat_writer"
  password: "${ES_PASSWORD}"
  ssl.certificate_authorities: ["/etc/ssl/certs/ca.crt"]
```

## Архитектуры развертывания

### Docker Compose ELK
```yaml
version: '3.8'

services:
  elasticsearch:
    image: docker.elastic.co/elasticsearch/elasticsearch:8.6.0
    environment:
      - discovery.type=single-node
      - xpack.security.enabled=false
      - "ES_JAVA_OPTS=-Xms512m -Xmx512m"
    volumes:
      - elasticsearch-data:/usr/share/elasticsearch/data
    ports:
      - "9200:9200"
      - "9300:9300"
    networks:
      - elk

  logstash:
    image: docker.elastic.co/logstash/logstash:8.6.0
    volumes:
      - ./logstash/config/logstash.yml:/usr/share/logstash/config/logstash.yml
      - ./logstash/pipeline:/usr/share/logstash/pipeline
    ports:
      - "5044:5044"
      - "8080:8080"
    environment:
      - LS_JAVA_OPTS=-Xmx256m -Xms256m
    networks:
      - elk
    depends_on:
      - elasticsearch

  kibana:
    image: docker.elastic.co/kibana/kibana:8.6.0
    environment:
      - ELASTICSEARCH_HOSTS=http://elasticsearch:9200
    ports:
      - "5601:5601"
    networks:
      - elk
    depends_on:
      - elasticsearch

  filebeat:
    image: docker.elastic.co/beats/filebeat:8.6.0
    volumes:
      - ./filebeat/filebeat.yml:/usr/share/filebeat/filebeat.yml
      - /var/lib/docker/containers:/var/lib/docker/containers:ro
      - /var/run/docker.sock:/var/run/docker.sock:ro
    networks:
      - elk
    depends_on:
      - logstash

volumes:
  elasticsearch-data:
    driver: local

networks:
  elk:
    driver: bridge
```

### Kubernetes развертывание
```yaml
# Elasticsearch StatefulSet
apiVersion: apps/v1
kind: StatefulSet
metadata:
  name: elasticsearch
  namespace: logging
spec:
  serviceName: elasticsearch
  replicas: 3
  selector:
    matchLabels:
      app: elasticsearch
  template:
    metadata:
      labels:
        app: elasticsearch
    spec:
      containers:
      - name: elasticsearch
        image: docker.elastic.co/elasticsearch/elasticsearch:8.6.0
        env:
        - name: discovery.seed_hosts
          value: "elasticsearch-0.elasticsearch.logging.svc.cluster.local:9300,elasticsearch-1.elasticsearch.logging.svc.cluster.local:9300,elasticsearch-2.elasticsearch.logging.svc.cluster.local:9300"
        - name: cluster.initial_master_nodes
          value: "elasticsearch-0,elasticsearch-1,elasticsearch-2"
        - name: cluster.name
          value: k8s-logs
        - name: node.name
          valueFrom:
            fieldRef:
              fieldPath: metadata.name
        ports:
        - containerPort: 9200
          name: rest
        - containerPort: 9300
          name: inter-node
        volumeMounts:
        - name: data
          mountPath: /usr/share/elasticsearch/data
        resources:
          requests:
            memory: 1Gi
            cpu: 500m
          limits:
            memory: 2Gi
            cpu: 1000m
  volumeClaimTemplates:
  - metadata:
      name: data
    spec:
      accessModes: ["ReadWriteOnce"]
      storageClassName: fast-ssd
      resources:
        requests:
          storage: 50Gi

---
# Kibana Deployment
apiVersion: apps/v1
kind: Deployment
metadata:
  name: kibana
  namespace: logging
spec:
  replicas: 1
  selector:
    matchLabels:
      app: kibana
  template:
    metadata:
      labels:
        app: kibana
    spec:
      containers:
      - name: kibana
        image: docker.elastic.co/kibana/kibana:8.6.0
        env:
        - name: ELASTICSEARCH_HOSTS
          value: http://elasticsearch:9200
        ports:
        - containerPort: 5601
        resources:
          requests:
            memory: 512Mi
            cpu: 250m
          limits:
            memory: 1Gi
            cpu: 500m
```

## Индекс lifecycle management

### ILM политика
```bash
# Создание ILM политики
curl -X PUT "localhost:9200/_ilm/policy/logs-policy" -H 'Content-Type: application/json' -d'
{
  "policy": {
    "phases": {
      "hot": {
        "min_age": "0ms",
        "actions": {
          "rollover": {
            "max_size": "50gb",
            "max_age": "30d"
          },
          "set_priority": {
            "priority": 100
          }
        }
      },
      "warm": {
        "min_age": "30d",
        "actions": {
          "set_priority": {
            "priority": 50
          },
          "shrink": {
            "number_of_shards": 1
          },
          "forcemerge": {
            "max_num_segments": 1
          }
        }
      },
      "cold": {
        "min_age": "60d",
        "actions": {
          "set_priority": {
            "priority": 0
          },
          "freeze": {}
        }
      },
      "delete": {
        "min_age": "90d",
        "actions": {
          "delete": {}
        }
      }
    }
  }
}'

# Применение политики к индексному шаблону
curl -X PUT "localhost:9200/_template/logs-template" -H 'Content-Type: application/json' -d'
{
  "index_patterns": ["logs-*"],
  "settings": {
    "number_of_shards": 3,
    "number_of_replicas": 1,
    "index.lifecycle.name": "logs-policy",
    "index.lifecycle.rollover_alias": "logs"
  },
  "mappings": {
    "properties": {
      "@timestamp": {
        "type": "date"
      },
      "message": {
        "type": "text"
      },
      "level": {
        "type": "keyword"
      }
    }
  }
}'
```

## Безопасность

### X-Pack Security
```bash
# Создание пользователей
curl -X POST "localhost:9200/_security/user/logstash_writer" \
  -H "Content-Type: application/json" \
  -u elastic:$ELASTIC_PASSWORD \
  -d'
{
  "password": "logstash_password",
  "roles": ["logstash_writer"],
  "full_name": "Logstash Writer"
}'

curl -X POST "localhost:9200/_security/user/kibana_system" \
  -H "Content-Type: application/json" \
  -u elastic:$ELASTIC_PASSWORD \
  -d'
{
  "password": "kibana_password",
  "roles": ["kibana_system"],
  "full_name": "Kibana System"
}'

# Создание ролей
curl -X POST "localhost:9200/_security/role/logstash_writer" \
  -H "Content-Type: application/json" \
  -u elastic:$ELASTIC_PASSWORD \
  -d'
{
  "cluster": ["manage_index_templates", "monitor", "manage_ilm"],
  "indices": [
    {
      "names": ["logs-*"],
      "privileges": ["create_index", "create_doc", "index", "auto_configure"]
    }
  ]
}'
```

### TLS/SSL конфигурация
```yaml
# Elasticsearch SSL
xpack.security.http.ssl.enabled: true
xpack.security.http.ssl.keystore.path: certs/elastic-certificates.p12
xpack.security.http.ssl.truststore.path: certs/elastic-certificates.p12

xpack.security.transport.ssl.enabled: true
xpack.security.transport.ssl.verification_mode: certificate
xpack.security.transport.ssl.keystore.path: certs/elastic-certificates.p12
xpack.security.transport.ssl.truststore.path: certs/elastic-certificates.p12

# Logstash SSL
input {
  beats {
    port => 5044
    ssl => true
    ssl_certificate => "/etc/logstash/ssl/logstash.crt"
    ssl_key => "/etc/logstash/ssl/logstash.key"
    ssl_verify_mode => "force_peer"
  }
}

output {
  elasticsearch {
    hosts => ["https://elasticsearch:9200"]
    user => "logstash_writer"
    password => "${ES_PASSWORD}"
    ssl => true
    cacert => "/etc/logstash/ssl/ca.crt"
  }
}
```

## Мониторинг и алертинг

### Elasticsearch monitoring
```bash
# Cluster health
curl -X GET "localhost:9200/_cluster/health?pretty"

# Node stats
curl -X GET "localhost:9200/_nodes/stats?pretty"

# Index stats
curl -X GET "localhost:9200/_stats?pretty"

# Pending tasks
curl -X GET "localhost:9200/_cluster/pending_tasks?pretty"

# Hot threads
curl -X GET "localhost:9200/_nodes/hot_threads?threads=10"
```

### Watcher для алертинга
```bash
# Создание watch
curl -X PUT "localhost:9200/_watcher/watch/error_rate_alert" -H 'Content-Type: application/json' -d'
{
  "trigger": {
    "schedule": {
      "interval": "5m"
    }
  },
  "input": {
    "search": {
      "request": {
        "indices": ["logs-*"],
        "body": {
          "query": {
            "bool": {
              "must": [
                {"match": {"level": "ERROR"}},
                {"range": {"@timestamp": {"gte": "now-5m"}}}
              ]
            }
          },
          "size": 0,
          "aggs": {
            "error_count": {
              "value_count": {
                "field": "level"
              }
            }
          }
        }
      }
    }
  },
  "condition": {
    "compare": {
      "ctx.payload.aggregations.error_count.value": {
        "gt": 100
      }
    }
  },
  "actions": {
    "send_email": {
      "email": {
        "to": ["alerts@company.com"],
        "subject": "High Error Rate Alert",
        "body": "Error rate exceeded threshold: {{ctx.payload.aggregations.error_count.value}}"
      }
    }
  }
}'
```

## Производительность и оптимизация

### Elasticsearch tuning
```yaml
# elasticsearch.yml оптимизации
bootstrap.memory_lock: true

indices.query.bool.max_clause_count: 1024
indices.memory.index_buffer_size: 10%
indices.memory.min_index_buffer_size: 96mb
indices.memory.max_index_buffer_size: 512mb

search.max_open_scroll_context: 500

thread_pool.search.size: 16
thread_pool.search.queue_size: 1000
thread_pool.write.size: 16
thread_pool.write.queue_size: 1000

# JVM оптимизации
-Xms8g
-Xmx8g
-XX:+UseG1GC
-XX:MaxGCPauseMillis=200
-XX:G1HeapRegionSize=16m
```

### Logstash performance
```ruby
# logstash.yml
pipeline.workers: 8
pipeline.batch.size: 125
pipeline.batch.delay: 5

# JVM tuning
-Xms2g
-Xmx2g
-XX:+UseG1GC
-XX:MaxGCPauseMillis=200

# Pipeline оптимизации
filter {
  # Использовать multiple workers для тяжелых фильтров
  grok {
    workers => 4
    match => { "message" => "%{COMBINEDAPACHELOG}" }
  }

  # Кэширование для geoip
  geoip {
    cache_size => 10000
  }
}
```

### Kibana optimization
```yaml
# kibana.yml оптимизации
elasticsearch.shardTimeout: 30000
elasticsearch.requestTimeout: 30000

# Connection pool
elasticsearch.maxSockets: 20
elasticsearch.compression: true

# Logging
logging.dest: /var/log/kibana/kibana.log
logging.silent: false
logging.quiet: false
logging.verbose: false
```

## Troubleshooting

### Распространенные проблемы
```bash
# Elasticsearch проблемы
# Проверка cluster health
curl -X GET "localhost:9200/_cluster/health?pretty"

# Allocation issues
curl -X GET "localhost:9200/_cluster/allocation/explain?pretty"

# Index recovery
curl -X GET "localhost:9200/_cat/recovery?v"

# Circuit breaker errors
curl -X GET "localhost:9200/_nodes/stats/breaker?pretty"

# Logstash проблемы
# Pipeline stats
curl -X GET "localhost:9600/_node/stats/pipelines?pretty"

# Hot threads
curl -X GET "localhost:9600/_node/hot_threads?threads=10"

# Kibana проблемы
# Status check
curl -X GET "localhost:5601/api/status"

# Task manager health
curl -X GET "localhost:5601/api/task_manager/_health"

# Saved objects
curl -X GET "localhost:5601/api/saved_objects/_find?type=dashboard"
```

### Диагностика производительности
```bash
# Elasticsearch slow logs
# В elasticsearch.yml
index.search.slowlog.threshold.query.warn: 10s
index.search.slowlog.threshold.query.info: 5s
index.search.slowlog.threshold.query.debug: 2s
index.search.slowlog.threshold.query.trace: 500ms

index.indexing.slowlog.threshold.index.warn: 10s
index.indexing.slowlog.threshold.index.info: 5s
index.indexing.slowlog.threshold.index.debug: 2s
index.indexing.slowlog.threshold.index.trace: 500ms

# Logstash monitoring
input {
  http {
    port => 8080
    additional_codecs => {
      "application/json" => "json"
    }
  }
}

filter {
  metrics {
    meter => ["events_rate"]
    add_tag => "metric"
  }
}

output {
  if "metric" in [tags] {
    stdout { codec => rubydebug }
  }
}
```

## Дата последнего обновления
22 января 2026 г.

## Полезные ссылки
- [Elasticsearch Documentation](https://www.elastic.co/guide/en/elasticsearch/reference/current/index.html)
- [Logstash Documentation](https://www.elastic.co/guide/en/logstash/current/index.html)
- [Kibana Documentation](https://www.elastic.co/guide/en/kibana/current/index.html)
- [ELK Stack Guide](https://www.elastic.co/guide/en/elastic-stack-get-started/current/index.html)
- [Beats Documentation](https://www.elastic.co/guide/en/beats/libbeat/current/index.html)

## См. также
- [Prometheus](monitoring/prometheus.md) - Альтернативная система мониторинга
- [Grafana](monitoring/grafana.md) - Визуализация данных
- [Distributed Tracing](monitoring/distributed-tracing.md) - Распределенное трассирование
- [Docker](containers/docker-basics.md) - Контейнеризация
