# Централизованное логирование для Java

Комплексное руководство по централизованному логированию в Java: сбор, хранение, поиск и анализ логов из распределенных систем с использованием ELK Stack, Fluentd, CloudWatch и других инструментов.

**Дата последнего обновления:** 2026-01-21

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
- [AWS CloudWatch](https://docs.aws.amazon.com/AmazonCloudWatch/latest/logs/WhatIsCloudWatchLogs.html)
- [Google Cloud Logging](https://cloud.google.com/logging/docs)
- [Azure Monitor](https://docs.microsoft.com/en-us/azure/azure-monitor/logs/data-platform-logs)

### См. также
- `logging-basics.md` - Основы логирования
- `structured-logging.md` - Структурированное логирование
- `log-aggregation.md` - Агрегация логов

## Содержание

- [Введение в централизованное логирование](#введение-в-централизованное-логирование)
- [Архитектура централизованного логирования](#архитектура-централизованного-логирования)
- [ELK Stack](#elk-stack)
- [Fluentd и Fluent Bit](#fluentd-и-fluent-bit)
- [Облачные решения](#облачные-решения)
- [Log shipping](#log-shipping)
- [Хранение и ротация](#хранение-и-ротация)
- [Поиск и анализ](#поиск-и-анализ)
- [Мониторинг и алертинг](#мониторинг-и-алертинг)
- [Безопасность](#безопасность)
- [Производительность](#производительность)
- [Best Practices](#best-practices)
- [Troubleshooting](#troubleshooting)
- [Заключение](#заключение)

## Введение в централизованное логирование

**Централизованное логирование** — это практика сбора, хранения и анализа логов из всех компонентов распределенной системы в едином месте. Это позволяет эффективно мониторить, отлаживать и анализировать работу приложений в production среде.

### Почему централизованное логирование?

Централизованное логирование решает критические проблемы распределенных систем:

1. **Visibility** — полная видимость работы всех компонентов
2. **Correlation** — связь событий между сервисами
3. **Search** — быстрый поиск по всем логам системы
4. **Analysis** — анализ паттернов и трендов
5. **Alerting** — автоматические оповещения о проблемах
6. **Compliance** — аудит и соответствие требованиям
7. **Debugging** — эффективная отладка production проблем
8. **Performance** — мониторинг производительности

### Архитектура централизованного логирования

```
┌─────────────────────────────────────────────────────────────────┐
│                    Application Layer                            │
│  ┌─────────────────────────────────────────────────────────┐    │
│  │  Service A  │  Service B  │  Service C  │  Database     │    │
│  │  ├─ Logs ──┤  ├─ Logs ──┤  ├─ Logs ──┤  ├─ Logs ───┤   │    │
│  └─────────────────────────────────────────────────────────┘    │
└─────────────────────────────────────────────────────────────────┘
                                 │
                                 ▼
┌─────────────────────────────────────────────────────────────────┐
│                   Collection Layer                              │
│  ┌─────────────────────────────────────────────────────────┐    │
│  │ Filebeat │ Fluent Bit │ Logstash │ Custom Collector     │    │
│  └─────────────────────────────────────────────────────────┘    │
└─────────────────────────────────────────────────────────────────┘
                                 │
                                 ▼
┌─────────────────────────────────────────────────────────────────┐
│                   Processing Layer                              │
│  ┌─────────────────────────────────────────────────────────┐    │
│  │  Filter │ Transform │ Enrich │ Parse │ Aggregate       │    │
│  └─────────────────────────────────────────────────────────┘    │
└─────────────────────────────────────────────────────────────────┘
                                 │
                                 ▼
┌─────────────────────────────────────────────────────────────────┐
│                   Storage Layer                                 │
│  ┌─────────────────────────────────────────────────────────┐    │
│  │ Elasticsearch │ OpenSearch │ Cloud Storage │ Database   │    │
│  └─────────────────────────────────────────────────────────┘    │
└─────────────────────────────────────────────────────────────────┘
                                 │
                                 ▼
┌─────────────────────────────────────────────────────────────────┐
│                   Analysis Layer                                │
│  ┌─────────────────────────────────────────────────────────┐    │
│  │ Kibana │ Grafana │ Custom Dashboard │ CLI Tools        │    │
│  └─────────────────────────────────────────────────────────┘    │
└─────────────────────────────────────────────────────────────────┘
```

## ELK Stack

### Elasticsearch

#### Configuration
```yaml
# elasticsearch.yml
cluster.name: my-application-logs
node.name: node-1
path.data: /var/lib/elasticsearch
path.logs: /var/log/elasticsearch

# Network settings
network.host: 0.0.0.0
http.port: 9200

# Discovery
discovery.type: single-node

# Memory settings
bootstrap.memory_lock: true

# Security (basic)
xpack.security.enabled: true
xpack.security.transport.ssl.enabled: true
```

#### Index management
```bash
# Create index template
curl -X PUT "localhost:9200/_template/logs_template" \
  -H 'Content-Type: application/json' \
  -d '{
    "index_patterns": ["logs-*"],
    "settings": {
      "number_of_shards": 3,
      "number_of_replicas": 1,
      "refresh_interval": "30s"
    },
    "mappings": {
      "properties": {
        "@timestamp": { "type": "date" },
        "level": { "type": "keyword" },
        "logger": { "type": "keyword" },
        "message": { "type": "text" },
        "correlationId": { "type": "keyword" },
        "service": { "type": "keyword" },
        "host": { "type": "keyword" }
      }
    }
  }'

# Create index alias
curl -X POST "localhost:9200/_aliases" \
  -H 'Content-Type: application/json' \
  -d '{
    "actions": [
      {
        "add": {
          "index": "logs-*",
          "alias": "all-logs",
          "is_write_index": false
        }
      }
    ]
  }'
```

### Logstash

#### Pipeline configuration
```ruby
# logstash.conf
input {
  beats {
    port => 5044
  }
  
  http {
    port => 8080
    codec => json
  }
}

filter {
  # Parse JSON logs
  if [message] =~ /^\{.*\}$/ {
    json {
      source => "message"
      target => "parsed"
    }
  }
  
  # Extract fields
  mutate {
    copy => { "[parsed][@timestamp]" => "@timestamp" }
    copy => { "[parsed][level]" => "level" }
    copy => { "[parsed][logger]" => "logger_name" }
    copy => { "[parsed][message]" => "log_message" }
    copy => { "[parsed][correlationId]" => "correlation_id" }
  }
  
  # Add metadata
  mutate {
    add_field => {
      "service" => "%{[@metadata][beat][name]}"
      "host" => "%{[@metadata][beat][hostname]}"
      "environment" => "production"
    }
  }
  
  # Parse timestamps
  date {
    match => ["@timestamp", "yyyy-MM-dd'T'HH:mm:ss.SSSZ", "yyyy-MM-dd HH:mm:ss.SSS"]
    target => "@timestamp"
  }
  
  # GeoIP enrichment
  if [client_ip] {
    geoip {
      source => "client_ip"
      target => "geo"
    }
  }
  
  # Remove temporary fields
  mutate {
    remove_field => ["message", "parsed", "@version"]
  }
}

output {
  elasticsearch {
    hosts => ["localhost:9200"]
    index => "logs-%{+YYYY.MM.dd}"
    document_id => "%{correlation_id}"
  }
  
  # Backup to file
  file {
    path => "/var/log/logstash/backup.log"
    codec => json_lines
  }
}
```

#### Multiple pipelines
```ruby
# pipelines.yml
- pipeline.id: beats-input
  path.config: "/etc/logstash/conf.d/beats.conf"
  
- pipeline.id: http-input  
  path.config: "/etc/logstash/conf.d/http.conf"
  
- pipeline.id: file-input
  path.config: "/etc/logstash/conf.d/file.conf"
```

### Kibana

#### Index pattern setup
```bash
# Create index pattern via API
curl -X POST "localhost:5601/api/saved_objects/index-pattern" \
  -H "kbn-xsrf: true" \
  -H "Content-Type: application/json" \
  -d '{
    "attributes": {
      "title": "logs-*",
      "timeFieldName": "@timestamp"
    }
  }'
```

#### Dashboard configuration
```json
{
  "dashboard": {
    "title": "Application Logs Dashboard",
    "description": "Centralized logging dashboard",
    "panelsJSON": [
      {
        "type": "visualization",
        "title": "Error Rate Over Time",
        "gridData": {"x": 0, "y": 0, "w": 24, "h": 8},
        "visState": {
          "type": "line",
          "params": {
            "type": "line",
            "series": [
              {
                "values": [{"x": "@timestamp", "y": "count"}],
                "query": {"match": {"level": "ERROR"}}
              }
            ]
          }
        }
      }
    ]
  }
}
```

## Fluentd и Fluent Bit

### Fluentd configuration

#### Basic Fluentd config
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

<filter application.**>
  @type record_transformer
  <record>
    service ${tag}
    hostname "#{Socket.gethostname}"
    environment production
  </record>
</filter>

<match application.**>
  @type elasticsearch
  host localhost
  port 9200
  index_name logs
  type_name log
  logstash_format true
</match>

<match **>
  @type stdout
</match>
```

#### Advanced Fluentd features
```xml
# Advanced fluentd.conf
<source>
  @type http
  port 9880
  bind 0.0.0.0
  body_size_limit 32m
  keepalive_timeout 10s
</source>

<filter **>
  @type grep
  <exclude>
    key message
    pattern /password|secret|token/i
  </exclude>
</filter>

<filter application.error>
  @type record_transformer
  <record>
    alert true
    severity high
  </record>
</filter>

<match application.error>
  @type copy
  <store>
    @type elasticsearch
    host localhost
    port 9200
    index_name errors
  </store>
  <store>
    @type slack
    webhook_url https://hooks.slack.com/services/...
    channel "#alerts"
    username "Fluentd"
    color danger
    title "Application Error"
    title_link http://kibana.example.com/error-dashboard
  </store>
</match>
```

### Fluent Bit

#### Fluent Bit configuration
```ini
# fluent-bit.conf
[INPUT]
    Name tail
    Path /var/log/application/*.log
    Parser json
    Tag app.*

[INPUT]  
    Name forward
    Listen 0.0.0.0
    Port 24224

[FILTER]
    Name record_modifier
    Match app.*
    Record hostname ${HOSTNAME}
    Record service application

[FILTER]
    Name grep
    Match app.*
    Exclude message /health|metrics/

[OUTPUT]
    Name es
    Match app.*
    Host elasticsearch
    Port 9200
    Index logs
    Type log
```

#### Fluent Bit parsers
```ini
# parsers.conf
[PARSER]
    Name json
    Format json
    Time_Key @timestamp
    Time_Format %Y-%m-%dT%H:%M:%S.%LZ

[PARSER]
    Name log4j
    Format regex
    Regex ^(?<@timestamp>\d{4}-\d{2}-\d{2} \d{2}:\d{2}:\d{2}\.\d{3}) \[(?<thread>[^\]]+)\] (?<level>[A-Z]+) (?<logger>[^\s]+) - (?<message>.+)$
    Time_Key @timestamp
    Time_Format %Y-%m-%d %H:%M:%S.%L

[PARSER]
    Name custom
    Format regex
    Regex ^(?<timestamp>\d+) (?<level>\w+) (?<service>\w+) (?<message>.+)$
```

## Облачные решения

### AWS CloudWatch

#### CloudWatch Logs configuration
```java
@Configuration
public class CloudWatchConfig {
    
    @Bean
    public CloudWatchLogsClient cloudWatchLogsClient() {
        return CloudWatchLogsClient.builder()
            .region(Region.US_EAST_1)
            .build();
    }
    
    @Bean
    public LogAppender cloudWatchAppender(CloudWatchLogsClient client) {
        return CloudWatchAppender.create()
            .logGroup("my-application")
            .logStream("application-logs")
            .client(client)
            .build();
    }
}
```

#### CloudWatch Logs agent
```json
{
  "logs": {
    "logs_collected": {
      "files": {
        "collect_list": [
          {
            "file_path": "/var/log/application/*.log",
            "log_group_name": "application-logs",
            "log_stream_name": "{instance_id}",
            "timestamp_format": "%Y-%m-%d %H:%M:%S"
          }
        ]
      }
    }
  }
}
```

### Google Cloud Logging

#### Google Cloud Logging setup
```java
@Configuration
public class GoogleCloudLoggingConfig {
    
    @Bean
    public Logging logging() {
        return LoggingOptions.getDefaultInstance().getService();
    }
    
    @Bean
    public LogAppender googleCloudAppender(Logging logging) {
        return GoogleCloudAppender.create()
            .logging(logging)
            .logName("application-logs")
            .resourceType("gce_instance")
            .build();
    }
}
```

#### Structured logging for Google Cloud
```java
public class GoogleCloudStructuredLogging {
    
    private static final Logger logger = LoggerFactory.getLogger(GoogleCloudStructuredLogging.class);
    
    public void logStructuredEvent() {
        Map<String, Object> jsonPayload = Map.of(
            "eventType", "user_action",
            "userId", "12345",
            "action", "login",
            "timestamp", Instant.now().toString()
        );
        
        logger.info("User action", 
                   KeyValuePair.of("jsonPayload", jsonPayload),
                   KeyValuePair.of("severity", "INFO"));
    }
}
```

### Azure Monitor

#### Azure Application Insights
```xml
<dependency>
    <groupId>com.microsoft.azure</groupId>
    <artifactId>applicationinsights-logging-logback</artifactId>
    <version>2.6.4</version>
</dependency>
```

```xml
<!-- logback-spring.xml -->
<configuration>
    <include resource="com/microsoft/applicationinsights/logback/initializer/LogbackInitializer.xml"/>
    
    <appender name="aiAppender" class="com.microsoft.applicationinsights.logback.ApplicationInsightsAppender">
        <instrumentationKey>${APPLICATION_INSIGHTS_IKEY}</instrumentationKey>
    </appender>
    
    <root level="INFO">
        <appender-ref ref="aiAppender"/>
    </root>
</configuration>
```

## Log shipping

### Filebeat

#### Filebeat configuration
```yaml
# filebeat.yml
filebeat.inputs:
- type: log
  enabled: true
  paths:
    - /var/log/application/*.log
    - /opt/myapp/logs/*.log
  exclude_files: ['\.gz$']
  fields:
    service: my-application
    environment: production
  fields_under_root: true
  json:
    keys_under_root: true
    add_error_key: true

processors:
- add_host_metadata:
    when.not.contains.tags: forwarded
- add_cloud_metadata: ~
- add_docker_metadata: ~
- add_kubernetes_metadata: ~

output.elasticsearch:
  hosts: ["localhost:9200"]
  index: "logs-%{+yyyy.MM.dd}"
  pipeline: "log-pipeline"

# Alternative output to Logstash
# output.logstash:
#   hosts: ["localhost:5044"]
```

#### Filebeat modules
```yaml
# Enable system module
filebeat.modules:
- module: system
  syslog:
    enabled: true
  auth:
    enabled: true

# Enable nginx module  
- module: nginx
  access:
    enabled: true
    var.paths: ["/var/log/nginx/access.log*"]
  error:
    enabled: true
    var.paths: ["/var/log/nginx/error.log*"]
```

### Logstash shipper

#### Logstash-forwarder configuration
```ruby
# shipper.conf
input {
  file {
    path => "/var/log/application/*.log"
    start_position => "beginning"
    sincedb_path => "/var/lib/logstash/.sincedb"
    codec => json
  }
}

filter {
  mutate {
    add_field => {
      "service" => "my-application"
      "host" => "%{host}"
      "environment" => "production"
    }
  }
}

output {
  redis {
    host => "logstash.example.com"
    port => 6379
    key => "logstash"
    data_type => "list"
  }
}
```

## Хранение и ротация

### Elasticsearch index lifecycle

#### ILM policy
```bash
# Create ILM policy
curl -X PUT "localhost:9200/_ilm/policy/logs_policy" \
  -H 'Content-Type: application/json' \
  -d '{
    "policy": {
      "phases": {
        "hot": {
          "min_age": "0ms",
          "actions": {
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

# Apply policy to index template
curl -X PUT "localhost:9200/_template/logs_template" \
  -H 'Content-Type: application/json' \
  -d '{
    "index_patterns": ["logs-*"],
    "settings": {
      "index.lifecycle.name": "logs_policy",
      "index.lifecycle.rollover_alias": "logs"
    }
  }'
```

### Log rotation strategies

#### Time-based rotation
```xml
<!-- Logback rolling policy -->
<appender name="ROLLING" class="ch.qos.logback.core.rolling.RollingFileAppender">
    <file>logs/application.log</file>
    
    <rollingPolicy class="ch.qos.logback.core.rolling.TimeBasedRollingPolicy">
        <fileNamePattern>logs/application.%d{yyyy-MM-dd}.%i.log</fileNamePattern>
        <maxFileSize>100MB</maxFileSize>
        <maxHistory>30</maxHistory>
        <totalSizeCap>3GB</totalSizeCap>
    </rollingPolicy>
    
    <encoder>
        <pattern>%d{yyyy-MM-dd HH:mm:ss.SSS} [%thread] %-5level %logger{36} - %msg%n</pattern>
    </encoder>
</appender>
```

#### Size-based rotation
```xml
<appender name="SIZE_ROLLING" class="ch.qos.logback.core.rolling.RollingFileAppender">
    <file>logs/application.log</file>
    
    <rollingPolicy class="ch.qos.logback.core.rolling.SizeAndTimeBasedRollingPolicy">
        <fileNamePattern>logs/application.%d{yyyy-MM-dd}.%i.log</fileNamePattern>
        <maxFileSize>50MB</maxFileSize>
        <maxHistory>14</maxHistory>
    </rollingPolicy>
    
    <encoder>
        <pattern>%d{yyyy-MM-dd HH:mm:ss.SSS} [%thread] %-5level %logger{36} - %msg%n</pattern>
    </encoder>
</appender>
```

## Поиск и анализ

### Elasticsearch queries

#### Basic search queries
```bash
# Search for errors in last hour
curl -X GET "localhost:9200/logs-*/_search" \
  -H 'Content-Type: application/json' \
  -d '{
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

# Search by correlation ID
curl -X GET "localhost:9200/logs-*/_search" \
  -H 'Content-Type: application/json' \
  -d '{
    "query": {
      "term": { "correlationId": "abc-123-def" }
    },
    "sort": [{ "@timestamp": { "order": "asc" } }]
  }'

# Search with aggregations
curl -X GET "localhost:9200/logs-*/_search" \
  -H 'Content-Type: application/json' \
  -d '{
    "query": {
      "range": { "@timestamp": { "gte": "now-24h" } }
    },
    "aggs": {
      "errors_by_service": {
        "terms": { "field": "service" },
        "aggs": {
          "error_levels": {
            "terms": { "field": "level" }
          }
        }
      },
      "errors_over_time": {
        "date_histogram": {
          "field": "@timestamp",
          "interval": "1h"
        }
      }
    }
  }'
```

### Kibana visualizations

#### Error rate dashboard
```json
{
  "visualization": {
    "title": "Error Rate Over Time",
    "type": "line",
    "params": {
      "type": "line",
      "addLegend": true,
      "addTooltip": true,
      "legendPosition": "right",
      "series": [
        {
          "values": [
            {
              "x": "@timestamp",
              "y": "count"
            }
          ],
          "query": {
            "bool": {
              "must": [
                { "term": { "level": "ERROR" } }
              ]
            }
          },
          "label": "Errors"
        }
      ]
    },
    "aggs": [
      {
        "id": "1",
        "type": "date_histogram",
        "params": {
          "field": "@timestamp",
          "interval": "auto"
        }
      }
    ]
  }
}
```

### Custom analysis tools

#### Log analysis utility
```java
public class LogAnalysisTool {
    
    private final RestHighLevelClient elasticsearchClient;
    
    public LogAnalysisTool(RestHighLevelClient client) {
        this.elasticsearchClient = client;
    }
    
    public Map<String, Long> getErrorCountsByService(String timeRange) {
        SearchRequest searchRequest = new SearchRequest("logs-*");
        
        BoolQueryBuilder query = QueryBuilders.boolQuery()
            .must(QueryBuilders.termQuery("level", "ERROR"))
            .must(QueryBuilders.rangeQuery("@timestamp").gte("now-" + timeRange));
        
        TermsAggregationBuilder aggregation = AggregationBuilders.terms("by_service")
            .field("service");
        
        searchRequest.source(new SearchSourceBuilder()
            .query(query)
            .aggregation(aggregation)
            .size(0));
        
        try {
            SearchResponse response = elasticsearchClient.search(searchRequest, RequestOptions.DEFAULT);
            Terms terms = response.getAggregations().get("by_service");
            
            return terms.getBuckets().stream()
                .collect(Collectors.toMap(
                    Terms.Bucket::getKeyAsString,
                    Terms.Bucket::getDocCount
                ));
            
        } catch (IOException e) {
            throw new RuntimeException("Failed to analyze logs", e);
        }
    }
    
    public List<String> findCorrelationChain(String correlationId) {
        SearchRequest searchRequest = new SearchRequest("logs-*");
        
        SearchSourceBuilder sourceBuilder = new SearchSourceBuilder()
            .query(QueryBuilders.termQuery("correlationId", correlationId))
            .sort("@timestamp", SortOrder.ASC)
            .fetchSource(new String[]{"message", "@timestamp", "service"}, null);
        
        searchRequest.source(sourceBuilder);
        
        try {
            SearchResponse response = elasticsearchClient.search(searchRequest, RequestOptions.DEFAULT);
            
            return Arrays.stream(response.getHits().getHits())
                .map(hit -> {
                    Map<String, Object> source = hit.getSourceAsMap();
                    return String.format("[%s] %s: %s",
                        source.get("@timestamp"),
                        source.get("service"),
                        source.get("message"));
                })
                .collect(Collectors.toList());
                
        } catch (IOException e) {
            throw new RuntimeException("Failed to find correlation chain", e);
        }
    }
}
```

## Мониторинг и алертинг

### Elasticsearch alerts

#### Watcher configuration
```bash
# Create watcher alert
curl -X PUT "localhost:9200/_watcher/watch/error_rate_alert" \
  -H 'Content-Type: application/json' \
  -d '{
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
                  { "term": { "level": "ERROR" } },
                  { "range": { "@timestamp": { "gte": "now-5m" } } }
                ]
              }
            }
          }
        }
      }
    },
    "condition": {
      "compare": {
        "ctx.payload.hits.total": {
          "gt": 10
        }
      }
    },
    "actions": {
      "email_alert": {
        "email": {
          "to": ["devops@example.com"],
          "subject": "High Error Rate Alert",
          "body": "Found {{ctx.payload.hits.total}} errors in the last 5 minutes"
        }
      },
      "slack_alert": {
        "webhook": {
          "url": "https://hooks.slack.com/services/...",
          "body": "{\"text\":\"High error rate detected: {{ctx.payload.hits.total}} errors\"}"
        }
      }
    }
  }'
```

### Kibana alerts

#### Kibana alert rule
```json
{
  "alert": {
    "name": "High Error Rate",
    "alertTypeId": ".es-query",
    "params": {
      "index": ["logs-*"],
      "timeField": "@timestamp",
      "esQuery": {
        "query": {
          "bool": {
            "must": [
              { "term": { "level": "ERROR" } },
              { "range": { "@timestamp": { "gte": "now-15m" } } }
            ]
          }
        }
      },
      "size": 100,
      "timeWindowSize": 15,
      "timeWindowUnit": "m",
      "thresholdComparator": ">",
      "threshold": [50]
    },
    "actions": [
      {
        "id": "slack",
        "params": {
          "message": "High error rate detected: {{alertState}} - {{context.value}} errors in 15 minutes"
        }
      }
    ]
  }
}
```

### Custom alerting service

```java
@Service
public class LogAlertingService {
    
    private final AlertService alertService;
    private final LogAnalysisTool analysisTool;
    
    @Autowired
    public LogAlertingService(AlertService alertService, LogAnalysisTool analysisTool) {
        this.alertService = alertService;
        this.analysisTool = analysisTool;
    }
    
    @Scheduled(fixedRate = 300000) // Every 5 minutes
    public void checkErrorRate() {
        Map<String, Long> errorCounts = analysisTool.getErrorCountsByService("5m");
        
        for (Map.Entry<String, Long> entry : errorCounts.entrySet()) {
            String service = entry.getKey();
            long errorCount = entry.getValue();
            
            if (errorCount > getErrorThreshold(service)) {
                alertService.sendAlert(
                    "High Error Rate",
                    String.format("Service %s has %d errors in last 5 minutes", service, errorCount),
                    AlertSeverity.HIGH
                );
            }
        }
    }
    
    @Scheduled(fixedRate = 600000) // Every 10 minutes
    public void checkLogGaps() {
        List<String> services = getAllServices();
        
        for (String service : services) {
            long lastLogTime = analysisTool.getLastLogTime(service);
            long minutesSinceLastLog = (System.currentTimeMillis() - lastLogTime) / 60000;
            
            if (minutesSinceLastLog > 30) { // No logs for 30 minutes
                alertService.sendAlert(
                    "Service Log Gap",
                    String.format("Service %s has not logged anything for %d minutes", 
                                service, minutesSinceLastLog),
                    AlertSeverity.MEDIUM
                );
            }
        }
    }
    
    private int getErrorThreshold(String service) {
        // Service-specific thresholds
        switch (service) {
            case "critical-service": return 10;
            case "background-service": return 100;
            default: return 50;
        }
    }
}
```

## Безопасность

### Log sanitization

#### Sensitive data filtering
```java
public class LogSanitizer {
    
    private static final Pattern SENSITIVE_PATTERNS = Pattern.compile(
        "(?i)(password|secret|token|key|credential)\\s*[:=]\\s*[\"']?([^\"'\\s]+)[\"']?");
    
    private static final Set<String> SENSITIVE_KEYS = Set.of(
        "password", "secret", "token", "key", "credential", 
        "authorization", "cookie", "sessionid"
    );
    
    public static String sanitize(String message) {
        if (message == null) {
            return null;
        }
        
        // Remove sensitive key-value pairs
        for (String key : SENSITIVE_KEYS) {
            message = message.replaceAll("(?i)" + key + "\\s*[:=]\\s*[^\\s,)]+", key + "=***");
        }
        
        // Remove sensitive patterns
        message = SENSITIVE_PATTERNS.matcher(message).replaceAll("$1=***");
        
        return message;
    }
    
    public static Map<String, Object> sanitizeMap(Map<String, Object> data) {
        Map<String, Object> sanitized = new HashMap<>(data);
        
        for (String key : SENSITIVE_KEYS) {
            if (sanitized.containsKey(key)) {
                sanitized.put(key, "***");
            }
        }
        
        return sanitized;
    }
}
```

#### Encryption at rest

#### Elasticsearch encryption
```yaml
# elasticsearch.yml
xpack.security.enabled: true
xpack.security.transport.ssl.enabled: true
xpack.security.http.ssl.enabled: true

# Encrypt data at rest
xpack.security.ssl.keystore.path: /etc/elasticsearch/ssl/elastic-certificates.p12
xpack.security.ssl.truststore.path: /etc/elasticsearch/ssl/elastic-certificates.p12
```

#### Log file encryption
```java
public class EncryptedLogAppender extends RollingFileAppender<ILoggingEvent> {
    
    private final Cipher cipher;
    private final SecretKey secretKey;
    
    @Override
    protected void writeOut(ILoggingEvent event) throws IOException {
        String message = getLayout().doLayout(event);
        
        // Encrypt the log message
        byte[] encrypted = encrypt(message.getBytes(StandardCharsets.UTF_8));
        
        // Write encrypted data
        getOutputStream().write(encrypted);
        getOutputStream().write('\n');
    }
    
    private byte[] encrypt(byte[] data) {
        try {
            cipher.init(Cipher.ENCRYPT_MODE, secretKey);
            return cipher.doFinal(data);
        } catch (Exception e) {
            throw new RuntimeException("Encryption failed", e);
        }
    }
}
```

### Access control

#### Kibana spaces
```bash
# Create Kibana space for different teams
curl -X POST "localhost:5601/api/spaces/space" \
  -H "kbn-xsrf: true" \
  -H "Content-Type: application/json" \
  -d '{
    "id": "dev-team",
    "name": "Development Team",
    "description": "Logs for development team"
  }'

# Assign role to space
curl -X PUT "localhost:5601/api/spaces/space/dev-team" \
  -H "kbn-xsrf: true" \
  -H "Content-Type: application/json" \
  -d '{
    "dev-team": {
      "read": true,
      "write": false
    }
  }'
```

## Производительность

### Elasticsearch optimization

#### Index optimization
```bash
# Optimize index for search performance
curl -X POST "localhost:9200/logs-*/_forcemerge?max_num_segments=1"

# Update index settings for better performance
curl -X PUT "localhost:9200/logs-*/_settings" \
  -H 'Content-Type: application/json' \
  -d '{
    "index": {
      "refresh_interval": "30s",
      "number_of_replicas": 0
    }
  }'

# Use index aliases for zero-downtime rollover
curl -X POST "localhost:9200/_aliases" \
  -H 'Content-Type: application/json' \
  -d '{
    "actions": [
      {
        "add": {
          "index": "logs-2023-12-01",
          "alias": "logs-write"
        }
      },
      {
        "remove": {
          "index": "logs-2023-11-30",
          "alias": "logs-write"
        }
      }
    ]
  }'
```

### Logstash performance tuning

#### Pipeline optimization
```ruby
# logstash.conf - optimized
input {
  beats {
    port => 5044
    ssl => true
    ssl_certificate => "/etc/ssl/logstash.crt"
    ssl_key => "/etc/ssl/logstash.key"
  }
}

filter {
  # Use conditional filters to avoid unnecessary processing
  if [level] == "DEBUG" {
    drop {}
  }
  
  # Batch operations
  if [type] == "application" {
    mutate {
      add_field => {
        "processed_at" => "%{@timestamp}"
        "pipeline" => "application"
      }
    }
    
    # Use memory-efficient operations
    fingerprint {
      source => ["message"]
      target => "[@metadata][fingerprint]"
      method => "SHA256"
    }
  }
}

output {
  if [type] == "application" {
    elasticsearch {
      hosts => ["localhost:9200"]
      index => "logs-%{+YYYY.MM.dd}"
      
      # Performance settings
      flush_size => 5000
      idle_flush_time => 10
      
      # Retry settings
      retry_on_conflict => 3
      retry_on_failure => true
    }
  }
}
```

### Filebeat optimization

#### Performance configuration
```yaml
# filebeat.yml - optimized
filebeat.inputs:
- type: log
  paths:
    - /var/log/application/*.log
  processors:
  - drop_event:
      when:
        contains:
          message: "DEBUG"
  - add_fields:
      fields:
        service: my-application
        environment: production

output.elasticsearch:
  hosts: ["localhost:9200"]
  index: "logs-%{+yyyy.MM.dd}"
  
  # Performance settings
  bulk_max_size: 1600
  worker: 4
  compression_level: 6
  
  # Queue settings
  queue.mem:
    events: 4096
    flush.min_events: 512
    flush.timeout: 5s
```

## Best Practices

### 1. Architecture design

#### Scalable architecture
```yaml
# docker-compose.yml for scalable logging stack
version: '3.8'
services:
  elasticsearch:
    image: docker.elastic.co/elasticsearch/elasticsearch:8.11.0
    environment:
      - discovery.type=single-node
      - "ES_JAVA_OPTS=-Xms2g -Xmx2g"
    volumes:
      - esdata:/usr/share/elasticsearch/data
    ports:
      - "9200:9200"
      - "9300:9300"

  logstash:
    image: docker.elastic.co/logstash/logstash:8.11.0
    volumes:
      - ./logstash.conf:/usr/share/logstash/pipeline/logstash.conf
    ports:
      - "5044:5044"
      - "9600:9600"
    depends_on:
      - elasticsearch

  kibana:
    image: docker.elastic.co/kibana/kibana:8.11.0
    ports:
      - "5601:5601"
    depends_on:
      - elasticsearch

  filebeat:
    image: docker.elastic.co/beats/filebeat:8.11.0
    volumes:
      - ./filebeat.yml:/usr/share/filebeat/filebeat.yml
      - /var/log/application:/var/log/application
    depends_on:
      - logstash

volumes:
  esdata:
    driver: local
```

#### Multi-region deployment
```yaml
# Multi-region logging architecture
global:
  region: us-east-1

services:
  logstash-central:
    # Central logstash for aggregation
    region: us-east-1
    
  logstash-region-a:
    # Regional logstash
    region: us-west-2
    
  logstash-region-b:
    # Regional logstash  
    region: eu-west-1
    
  elasticsearch-cluster:
    # Multi-zone cluster
    zones: [us-east-1a, us-east-1b, us-east-1c]
```

### 2. Data management

#### Retention policies
```bash
# Elasticsearch ILM policy for log retention
curl -X PUT "localhost:9200/_ilm/policy/log_retention_policy" \
  -H 'Content-Type: application/json' \
  -d '{
    "policy": {
      "phases": {
        "hot": {
          "min_age": "0ms",
          "actions": {
            "rollover": {
              "max_size": "50gb",
              "max_age": "1d"
            }
          }
        },
        "warm": {
          "min_age": "7d",
          "actions": {
            "allocate": {
              "number_of_replicas": 1
            },
            "shrink": {
              "number_of_shards": 1
            }
          }
        },
        "cold": {
          "min_age": "30d",
          "actions": {
            "allocate": {
              "number_of_replicas": 0
            }
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
```

#### Index templates
```bash
# Comprehensive index template
curl -X PUT "localhost:9200/_template/comprehensive_logs" \
  -H 'Content-Type: application/json' \
  -d '{
    "index_patterns": ["logs-*", "audit-*", "metrics-*"],
    "settings": {
      "number_of_shards": 3,
      "number_of_replicas": 1,
      "index.codec": "best_compression",
      "refresh_interval": "30s"
    },
    "mappings": {
      "properties": {
        "@timestamp": {
          "type": "date",
          "format": "strict_date_optional_time||yyyy-MM-dd HH:mm:ss.SSS"
        },
        "level": {
          "type": "keyword"
        },
        "logger": {
          "type": "keyword"
        },
        "message": {
          "type": "text",
          "analyzer": "standard"
        },
        "correlationId": {
          "type": "keyword",
          "index": true
        },
        "userId": {
          "type": "keyword"
        },
        "service": {
          "type": "keyword"
        },
        "host": {
          "type": "keyword"
        },
        "environment": {
          "type": "keyword"
        },
        "exception": {
          "type": "text"
        },
        "stackTrace": {
          "type": "text"
        },
        "mdc": {
          "type": "object",
          "dynamic": true
        }
      }
    }
  }'
```

### 3. Monitoring and alerting

#### Comprehensive monitoring
```java
@Service
public class CentralizedLoggingMonitor {
    
    private final MeterRegistry meterRegistry;
    private final LogAnalysisTool analysisTool;
    private final AlertService alertService;
    
    @Autowired
    public CentralizedLoggingMonitor(MeterRegistry meterRegistry, 
                                   LogAnalysisTool analysisTool,
                                   AlertService alertService) {
        this.meterRegistry = meterRegistry;
        this.analysisTool = analysisTool;
        this.alertService = alertService;
    }
    
    @Scheduled(fixedRate = 60000) // Every minute
    public void updateMetrics() {
        // Log volume metrics
        Map<String, Long> logCounts = analysisTool.getLogCountsByService("1m");
        logCounts.forEach((service, count) -> {
            Counter.builder("logs.volume")
                .tag("service", service)
                .register(meterRegistry)
                .increment(count);
        });
        
        // Error rate metrics
        Map<String, Long> errorCounts = analysisTool.getErrorCountsByService("1m");
        errorCounts.forEach((service, count) -> {
            Gauge.builder("logs.error_rate")
                .tag("service", service)
                .register(meterRegistry)
                .set(count);
        });
    }
    
    @Scheduled(fixedRate = 300000) // Every 5 minutes
    public void checkSystemHealth() {
        List<String> unhealthyServices = analysisTool.getUnhealthyServices();
        
        if (!unhealthyServices.isEmpty()) {
            alertService.sendAlert(
                "Service Health Check Failed",
                "Following services are not logging: " + String.join(", ", unhealthyServices),
                AlertSeverity.CRITICAL
            );
        }
        
        // Check log ingestion rate
        long ingestionRate = analysisTool.getIngestionRate("5m");
        if (ingestionRate < 1000) { // Less than 1000 logs per 5 minutes
            alertService.sendAlert(
                "Low Log Ingestion Rate",
                String.format("Log ingestion rate is low: %d logs per 5 minutes", ingestionRate),
                AlertSeverity.WARNING
            );
        }
    }
    
    @Scheduled(fixedRate = 3600000) // Every hour
    public void generateReports() {
        // Generate daily log summary report
        Map<String, Object> report = analysisTool.generateDailyReport();
        
        logger.info("Daily log report generated: {}", report);
        
        // Send report to stakeholders
        emailService.sendReport("Daily Log Summary", report);
    }
}
```

## Troubleshooting

### Распространенные проблемы

#### Elasticsearch issues
```bash
# Check cluster health
curl -X GET "localhost:9200/_cluster/health?pretty"

# Check index health
curl -X GET "localhost:9200/logs-*/_stats?pretty"

# Check for unassigned shards
curl -X GET "localhost:9200/_cat/shards?v&h=index,shard,prirep,state,node"

# Force merge indices for performance
curl -X POST "localhost:9200/logs-*/_forcemerge?max_num_segments=1"
```

#### Logstash issues
```bash
# Check logstash status
curl -X GET "localhost:9600/_node/stats"

# Check pipeline stats
curl -X GET "localhost:9600/_node/stats/pipelines"

# Reload configuration
curl -X POST "localhost:9600/_reload"

# Check for dead letter queue
ls -la /var/lib/logstash/dead_letter_queue/
```

#### Filebeat issues
```bash
# Check filebeat status
curl -X GET "localhost:5066/?pretty"

# Check registry file
cat /var/lib/filebeat/registry/filebeat/data.json

# Reset filebeat state
rm -rf /var/lib/filebeat/registry/filebeat/
systemctl restart filebeat
```

#### Network connectivity issues
```bash
# Test Elasticsearch connectivity
curl -X GET "localhost:9200/_cluster/health"

# Test Logstash connectivity
telnet localhost 5044

# Test Filebeat to Logstash
nc -zv localhost 5044

# Check firewall rules
iptables -L | grep 5044
```

### Performance troubleshooting

#### Elasticsearch performance
```bash
# Check slow queries
curl -X GET "localhost:9200/_cluster/state?pretty" | jq '.routing_table.indices[].shards'

# Check hot threads
curl -X GET "localhost:9200/_nodes/hot_threads"

# Profile search queries
curl -X POST "localhost:9200/_profile" \
  -H 'Content-Type: application/json' \
  -d '{
    "query": {
      "match": { "message": "error" }
    }
  }'
```

#### Logstash performance
```bash
# Monitor JVM
jps -l | grep logstash
jstat -gc <logstash_pid>

# Check pipeline performance
curl -X GET "localhost:9600/_node/stats/pipelines?pretty"

# Monitor queue
curl -X GET "localhost:9600/_node/stats/queue?pretty"
```

#### Filebeat performance
```bash
# Check filebeat metrics
curl -X GET "localhost:5066/stats?pretty"

# Monitor harvester stats
curl -X GET "localhost:5066/stats?pretty" | jq '.filebeat.harvester'

# Check disk I/O
iostat -x 1

# Check CPU usage
top -p $(pgrep filebeat)
```

### Data consistency issues

#### Missing logs
```bash
# Check Filebeat registry
cat /var/lib/filebeat/registry/filebeat/data.json | jq '.[] | select(.source == "/var/log/application/app.log")'

# Check Logstash input stats
curl -X GET "localhost:9600/_node/stats/pipelines/main?pretty"

# Check Elasticsearch ingestion
curl -X GET "localhost:9200/_cat/indices/logs-*?v&s=index"

# Manual log injection for testing
echo '{"message":"test log","timestamp":"'$(date -Iseconds)'"}' | nc localhost 5044
```

#### Duplicate logs
```bash
# Check for duplicate Filebeat instances
ps aux | grep filebeat

# Check Logstash duplicate processing
# Add fingerprint filter to prevent duplicates
filter {
  fingerprint {
    source => ["message", "@timestamp"]
    target => "[@metadata][fingerprint]"
    method => "SHA256"
  }
  
  # Remove duplicates based on fingerprint
  duplicate {
    fingerprint => "[@metadata][fingerprint]"
    remove_tag => "duplicate"
  }
}
```

#### Data corruption
```bash
# Validate JSON logs
curl -X GET "localhost:9200/logs-*/_search?size=10" | jq '.hits.hits[]._source | select(. | type != "object")'

# Check for malformed documents
curl -X POST "localhost:9200/logs-*/_validate/query" \
  -H 'Content-Type: application/json' \
  -d '{
    "query": {
      "bool": {
        "must_not": {
          "exists": {
            "field": "@timestamp"
          }
        }
      }
    }
  }'
```

### Capacity planning

#### Sizing guidelines
```bash
# Estimate Elasticsearch cluster size
# Rule of thumb: 1GB heap per 1TB data
# For 10TB data: ~10 nodes with 32GB heap each

# Calculate Logstash throughput
# Typical: 10,000-50,000 events/second per pipeline
# Scale horizontally for higher throughput

# Filebeat sizing
# Memory: ~100MB per 10,000 files
# CPU: 0.5-1 core per 10,000 files
# Network: 100-200 Mbps for high volume
```

#### Monitoring capacity
```bash
# Elasticsearch capacity monitoring
curl -X GET "localhost:9200/_cluster/stats?human&pretty"

# Logstash capacity
curl -X GET "localhost:9600/_node/stats/process?pretty"

# Disk space monitoring
df -h /var/lib/elasticsearch/
du -sh /var/lib/logstash/
```

## Заключение

**Централизованное логирование** — это фундаментальная практика для современных распределенных систем. ELK Stack и альтернативы предоставляют мощные инструменты для сбора, хранения и анализа логов.

### Ключевые возможности:

1. **ELK Stack** — Elasticsearch, Logstash, Kibana для полной logging экосистемы
2. **Fluentd/Fluent Bit** — легковесные альтернативы для контейнеризованных сред
3. **Облачные решения** — CloudWatch, Cloud Logging, Azure Monitor
4. **Log Shipping** — Filebeat, Logstash-forwarder для надежной доставки
5. **Хранение** — ротация, retention policies, compression
6. **Поиск и анализ** — advanced queries, aggregations, visualizations
7. **Мониторинг** — alerting, dashboards, metrics
8. **Безопасность** — encryption, access control, sanitization

### Архитектурные преимущества:

#### Scalability:
- **Horizontal Scaling** — добавление узлов для увеличения capacity
- **Data Partitioning** — разделение данных по времени и типам
- **Load Balancing** — распределение нагрузки между компонентами
- **High Availability** — отказоустойчивость и redundancy

#### Observability:
- **Centralized View** — единая точка для всех логов системы
- **Correlation Analysis** — связь событий между сервисами
- **Performance Monitoring** — отслеживание производительности
- **Root Cause Analysis** — быстрая диагностика проблем

### Когда использовать централизованное логирование:

✅ **Microservices** — связь логов между сервисами
✅ **Distributed Systems** — корреляция событий
✅ **Production Monitoring** — 24/7 наблюдение
✅ **Compliance Requirements** — аудит и соответствие
✅ **Large-scale Applications** — анализ больших объемов данных
✅ **DevOps Culture** — автоматизация и alerting
✅ **Cloud-native** — интеграция с облачными сервисами
✅ **Multi-team Development** — разделение доступа

### Когда НЕ использовать:

❌ **Simple Applications** — для небольших проектов достаточно локальных логов
❌ **Development Only** — не нужен complex stack для разработки
❌ **Resource Constraints** — значительные требования к инфраструктуре
❌ **Real-time Only** — если нужна только real-time обработка
❌ **Single Service** — избыточная сложность для monolithic apps
❌ **Legacy Systems** — сложная интеграция со старыми системами

### Best practices:

1. **ELK Stack Setup** — правильная конфигурация Elasticsearch, Logstash, Kibana
2. **Log Shipping** — надежная доставка с Filebeat/Fluent Bit
3. **Data Modeling** — структурированные логи с correlation IDs
4. **Retention Policies** — управление жизненным циклом данных
5. **Security** — шифрование, access control, sanitization
6. **Monitoring** — alerting, dashboards, capacity planning
7. **Performance** — оптимизация ingestion, search, storage
8. **Backup** — стратегии backup и disaster recovery

Централизованное логирование является cornerstone современной observable архитектуры, обеспечивая visibility и control над distributed системами. Правильная реализация позволяет быстро реагировать на проблемы и обеспечивать высокое качество обслуживания. 🚀
