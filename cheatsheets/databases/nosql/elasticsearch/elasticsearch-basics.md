---
title: "Elasticsearch: Основы — Полное руководство по распределенному поисковому движку"
description: "Комплексное руководство по Elasticsearch: архитектура, установка, основные понятия, mapping, анализ текста и работа с данными в Java/Spring приложениях."
tags:
  - databases
  - nosql
  - elasticsearch-basics
type: "overview"
difficulty: "intermediate"
aliases:
  - "Elasticsearch"
  - "elasticsearch basics"
  - "ES"
prerequisites: []
next:
  - "[[elasticsearch-queries]]"
updated: "2026-04-20"
---
# Elasticsearch: Основы — Полное руководство по распределенному поисковому движку

Комплексное руководство по **Elasticsearch**: архитектура, установка, основные понятия, **mapping**, анализ текста и работа с данными в **Java**/**Spring** приложениях.

## Полезные ссылки

### Официальная документация
- [Elasticsearch Documentation](https://www.elastic.co/guide/en/elasticsearch/reference/current/index.html)
- [Elasticsearch Guide](https://www.elastic.co/guide/en/elasticsearch/guide/current/index.html)
- [Java API Client](https://www.elastic.co/guide/en/elasticsearch/client/java-api-client/current/index.html)

### Spring Data Elasticsearch
- [Spring Data Elasticsearch](https://docs.spring.io/spring-data/elasticsearch/docs/current/reference/html/)
- [Spring Boot Elasticsearch](https://docs.spring.io/spring-boot/docs/current/reference/html/data.html#data.nosql.elasticsearch)

### Инструменты и утилиты
- [Kibana](https://www.elastic.co/guide/en/kibana/current/index.html) — визуализация и управление
- [Elasticsearch Head](https://github.com/mobz/elasticsearch-head) — веб-интерфейс
- [Cerebro](https://github.com/lmenezes/cerebro) — **Elasticsearch admin** **UI**

### См. также
- [Индексация](elasticsearch-indexing.md) — индексация документов
- [Запросы](elasticsearch-queries.md) — поиск и запросы
- [Агрегации](elasticsearch-aggregations.md) — агрегации
- [Кластеризация](elasticsearch-clustering.md) — кластеризация
- [Производительность](elasticsearch-performance.md) — производительность

## Содержание

- [Введение в Elasticsearch](#введение-в-elasticsearch)
  - [Ключевые особенности Elasticsearch](#ключевые-особенности-elasticsearch)
    - [Масштабируемость и производительность](#масштабируемость-и-производительность)
    - [Гибкость и мощь](#гибкость-и-мощь)
    - [Надежность и доступность](#надежность-и-доступность)
  - [Типичные сценарии использования](#типичные-сценарии-использования)
    - [Поиск и аналитика](#поиск-и-аналитика)
    - [Мониторинг и observability](#мониторинг-и-observability)
    - [Business intelligence](#business-intelligence)
- [Архитектура Elasticsearch](#архитектура-elasticsearch)
  - [Компоненты системы](#компоненты-системы)
    - [Узлы и роли](#узлы-и-роли)
    - [Шарды и реплики](#шарды-и-реплики)
  - [Распределенная архитектура](#распределенная-архитектура)
    - [Cluster State](#cluster-state)
- [Установка и настройка](#установка-и-настройка)
  - [Установка Elasticsearch](#установка-elasticsearch)
    - [Single Node Installation](#single-node-installation)
    - [Docker Installation](#docker-installation)
    - [Production Setup](#production-setup)
  - [Базовая конфигурация](#базовая-конфигурация)
    - [elasticsearch.yml](#elasticsearchyml)
    - [JVM Configuration](#jvm-configuration)
  - [Безопасность](#безопасность)
    - [Basic Security Setup](#basic-security-setup)
- [Основные понятия](#основные-понятия)
  - [Документы и индексы](#документы-и-индексы)
    - [Document Structure](#document-structure)
    - [Index vs Type (Legacy)](#index-vs-type-legacy)
  - [CRUD операции](#crud-операции)
    - [Create Document](#create-document)
    - [Read Document](#read-document)
    - [Update Document](#update-document)
    - [Delete Document](#delete-document)
- [Индексы и типы](#индексы-и-типы)
  - [Создание индексов](#создание-индексов)
    - [Basic Index Creation](#basic-index-creation)
    - [Index Templates](#index-templates)
  - [Управление индексами](#управление-индексами)
    - [Index Operations](#index-operations)
    - [Index Aliases](#index-aliases)
- [Mapping и схемы](#mapping-и-схемы)
  - [Data Types](#data-types)
    - [Core Data Types](#core-data-types)
    - [Complex Data Types](#complex-data-types)
  - [Dynamic Mapping](#dynamic-mapping)
    - [Dynamic Mapping Rules](#dynamic-mapping-rules)
    - [Runtime Fields](#runtime-fields)
- [Анализ текста](#анализ-текста)
  - [Analyzers](#analyzers)
    - [Built-in Analyzers](#built-in-analyzers)
    - [Language Analyzers](#language-analyzers)
  - [Text Analysis Process](#text-analysis-process)
    - [Analyze API](#analyze-api)
- [Java API и интеграция](#java-api-и-интеграция)
  - [Elasticsearch Java Client](#elasticsearch-java-client)
    - [High Level REST Client (Legacy)](#high-level-rest-client-legacy)
  - [Java API Client 8.x](#java-api-client-8x)
- [Spring Data Elasticsearch](#spring-data-elasticsearch-1)
  - [Configuration](#configuration)
  - [Entity Mapping](#entity-mapping)
  - [Repository Interface](#repository-interface)
  - [Service Layer](#service-layer)
- [Лучшие практики](#лучшие-практики)
  - [Index Design](#index-design)
    - [1. Shard Strategy](#1-shard-strategy)
    - [2. Mapping Best Practices](#2-mapping-best-practices)
    - [3. Data Modeling](#3-data-modeling)
  - [Performance Optimization](#performance-optimization)
    - [1. Query Optimization](#1-query-optimization)
    - [2. Index Optimization](#2-index-optimization)
    - [3. Hardware Considerations](#3-hardware-considerations)
  - [Monitoring and Alerting](#monitoring-and-alerting)
    - [1. Key Metrics](#1-key-metrics)
    - [2. Common Alerts](#2-common-alerts)
    - [3. Troubleshooting](#3-troubleshooting)
  - [Security Best Practices](#security-best-practices)
    - [1. Authentication & Authorization](#1-authentication-authorization)
    - [2. Network Security](#2-network-security)
    - [3. Data Protection](#3-data-protection)
- [Решение проблем](#решение-проблем)
- [Частые вопросы](#частые-вопросы)
- [Заключение](#заключение)
  - [Ключевые особенности:](#ключевые-особенности)
  - [Архитектурные принципы:](#архитектурные-принципы)
  - [Производительность:](#производительность)
  - [Интеграция:](#интеграция)
  - [Best practices:](#best-practices)

## Введение в Elasticsearch

**Elasticsearch** — это распределенный, **RESTful** поисковый и аналитический движок, построенный на основе **Apache Lucene**. **Elasticsearch** предоставляет возможности полнотекстового поиска, аналитики в реальном времени и масштабируемого хранения данных.

### Ключевые особенности Elasticsearch

#### Масштабируемость и производительность
- **Горизонтальная масштабируемость** — добавление узлов для увеличения **capacity**
- **Высокая производительность** — поиск в миллисекундах по терабайтам данных
- **Распределенная архитектура** — автоматическое распределение данных и запросов
- **Real-time** — изменения доступны для поиска немедленно

#### Гибкость и мощь
- **Schema-free** — не требует предварительного определения схемы
- **Full-text search** — мощные возможности текстового поиска
- **Analytics** — агрегации, статистика, группировки
- **Geo-spatial** — поиск по географическим координатам
- **Multi-tenancy** — множественные индексы в одном кластере

#### Надежность и доступность
- **Fault tolerance** — автоматическое восстановление после сбоев
- **High availability** — репликация для отказоустойчивости
- **Durability** — гарантии сохранности данных
- **Backup/Restore** — инструменты для резервного копирования

### Типичные сценарии использования

#### Поиск и аналитика

Пример полнотекстового поиска и агрегаций в **Java API**: **multiMatch** по полям, **terms**-агрегация по категориям; анализ логов по временным интервалам.

```java
// Полнотекстовый поиск в e-commerce
SearchRequest searchRequest = new SearchRequest("products");
SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();
sourceBuilder.query(QueryBuilders.multiMatchQuery("laptop", "name", "description"));
sourceBuilder.aggregation(AggregationBuilders.terms("categories").field("category"));
searchRequest.source(sourceBuilder);

// Аналитика логов
SearchRequest logAnalysis = new SearchRequest("application-logs-*");
logAnalysis.source()
    .query(QueryBuilders.rangeQuery("@timestamp").gte("now-1h"))
    .aggregation(AggregationBuilders.dateHistogram("logs_over_time")
        .field("@timestamp")
        .calendarInterval(DateHistogramInterval.HOUR));
```

#### Мониторинг и observability
```java
// Мониторинг производительности
SearchRequest metricsRequest = new SearchRequest("system-metrics");
metricsRequest.source()
    .query(QueryBuilders.boolQuery()
        .must(QueryBuilders.termQuery("service", "web-api"))
        .must(QueryBuilders.rangeQuery("@timestamp").gte("now-24h")))
    .aggregation(AggregationBuilders.avg("avg_response_time").field("response_time"))
    .aggregation(AggregationBuilders.percentiles("response_time_percentiles")
        .field("response_time").percentiles(50.0, 95.0, 99.0));
```

#### Business intelligence
```java
// Анализ продаж
SearchRequest salesAnalysis = new SearchRequest("sales-data");
salesAnalysis.source()
    .query(QueryBuilders.rangeQuery("sale_date").gte("2023-01-01"))
    .aggregation(AggregationBuilders.terms("top_products").field("product_id")
        .order(BucketOrder.aggregation("total_sales", false)))
    .aggregation(AggregationBuilders.sum("total_sales").field("amount"))
    .aggregation(AggregationBuilders.dateHistogram("sales_trend")
        .field("sale_date")
        .calendarInterval(DateHistogramInterval.MONTH));
```

## Архитектура Elasticsearch

### Компоненты системы

#### Узлы и роли
```java
// Роли узла в кластере: master, data, ingest, coordinating
public enum NodeRole {
    MASTER_ELIGIBLE,  // Может стать master
    DATA,            // Хранит данные и выполняет запросы
    INGEST,          // Преобразование данных перед индексацией
    COORDINATING,    // Координация запросов (все узлы)
    MACHINE_LEARNING // ML задачи
}

public class ElasticsearchNode {

    private String nodeId;
    private String nodeName;
    private Set<NodeRole> roles;
    private NodeAttributes attributes;
    private ClusterState clusterState;

    // Master-eligible node responsibilities
    public void participateInMasterElection() {
        // Участие в выборах master
    }

    public void publishClusterState() {
        // Публикация состояния кластера
    }

    // Data node responsibilities
    public void storeShard(Shard shard) {
        // Хранение шарда
    }

    public void executeSearch(SearchRequest request) {
        // Выполнение поиска
    }

    public void performIndexing(IndexRequest request) {
        // Индексация документов
    }
}
```

#### Шарды и реплики
```java
// Управление шардами и репликацией индекса
public class ShardManagement {

    public static final int DEFAULT_NUMBER_OF_SHARDS = 5;
    public static final int DEFAULT_NUMBER_OF_REPLICAS = 1;

    // Primary shard - основной шард
    public class PrimaryShard {
        private int shardId;
        private List<Document> documents;
        private Translog translog;

        public void indexDocument(Document doc) {
            // Индексация в primary shard
            documents.add(doc);

            // Запись в translog для durability
            translog.append(doc);
        }

        public SearchResult search(Query query) {
            // Поиск в primary shard
            return executeSearch(query);
        }
    }

    // Replica shard - реплика для отказоустойчивости
    public class ReplicaShard {
        private int shardId;
        private PrimaryShard primaryShard;

        public void replicateFromPrimary() {
            // Репликация изменений с primary
            List<Document> changes = primaryShard.getRecentChanges();
            applyChanges(changes);
        }

        public SearchResult search(Query query) {
            // Поиск в реплике (read scaling)
            return executeSearch(query);
        }
    }

    // Shard allocation and rebalancing
    public void allocateShards() {
        // Распределение шардов по узлам
        List<Node> dataNodes = cluster.getDataNodes();

        for (Shard shard : allShards) {
            Node targetNode = selectOptimalNode(shard, dataNodes);
            targetNode.allocateShard(shard);
        }
    }

    private Node selectOptimalNode(Shard shard, List<Node> nodes) {
        return nodes.stream()
            .min(Comparator.comparing(node ->
                calculateAllocationScore(shard, node)))
            .orElseThrow();
    }

    private double calculateAllocationScore(Shard shard, Node node) {
        // Балансировка нагрузки, дискового пространства, etc.
        double diskUsage = node.getDiskUsage();
        double cpuUsage = node.getCpuUsage();
        int shardCount = node.getAllocatedShards().size();

        return diskUsage * 0.4 + cpuUsage * 0.3 + shardCount * 0.3;
    }
}
```

### Распределенная архитектура

#### Cluster State
```java
// Состояние кластера: узлы, индексы, шарды
public class ClusterState {

    private String clusterName;
    private long version;
    private Map<String, IndexMetadata> indices;
    private Map<String, Node> nodes;
    private RoutingTable routingTable;
    private Metadata metadata;

    public void updateIndexMetadata(String indexName, IndexMetadata metadata) {
        indices.put(indexName, metadata);
        version++;
        publishToAllNodes();
    }

    public void updateRoutingTable(RoutingTable newRoutingTable) {
        routingTable = newRoutingTable;
        version++;
        publishToAllNodes();
    }

    private void publishToAllNodes() {
        // Публикация состояния всем узлам
        clusterService.publishClusterState(this);
    }

    // Consistency checks
    public boolean isConsistent() {
        // Проверка консистентности состояния
        return routingTable.isValid() &&
               allNodesAgreeOnVersion(version);
    }
}

public class RoutingTable {

    private Map<String, IndexRoutingTable> indexRoutingTables;

    public ShardRouting getShardRouting(String index, int shardId) {
        IndexRoutingTable indexRouting = indexRoutingTables.get(index);
        return indexRouting.getShardRouting(shardId);
    }

    public List<Node> getNodesForShard(String index, int shardId) {
        ShardRouting routing = getShardRouting(index, shardId);
        return routing.getNodes();
    }
}
```

## Установка и настройка

### Установка Elasticsearch

#### Single Node Installation
```bash
# Скачивание и установка
wget https://artifacts.elastic.co/downloads/elasticsearch/elasticsearch-8.11.0-linux-x86_64.tar.gz
tar -xzf elasticsearch-8.11.0-linux-x86_64.tar.gz
cd elasticsearch-8.11.0/

# Запуск
./bin/elasticsearch

# Проверка
curl -X GET "localhost:9200/"
```

#### Docker Installation
```yaml
# docker-compose.yml
version: '3.8'
services:
  elasticsearch:
    image: docker.elastic.co/elasticsearch/elasticsearch:8.11.0
    environment:
      - discovery.type=single-node
      - xpack.security.enabled=false
      - "ES_JAVA_OPTS=-Xms512m -Xmx512m"
    ports:
      - "9200:9200"
      - "9300:9300"
    volumes:
      - elasticsearch-data:/usr/share/elasticsearch/data
    networks:
      - elastic

volumes:
  elasticsearch-data:

networks:
  elastic:
    driver: bridge
```

#### Production Setup
```bash
# Создание пользователя elasticsearch
sudo useradd -r -s /bin/false elasticsearch

# Настройка директорий
sudo mkdir -p /var/lib/elasticsearch /var/log/elasticsearch
sudo chown -R elasticsearch:elasticsearch /var/lib/elasticsearch /var/log/elasticsearch

# Systemd service
sudo tee /etc/systemd/system/elasticsearch.service > /dev/null <<EOF
[Unit]
Description=Elasticsearch
Wants=network-online.target
After=network-online.target

[Service]
User=elasticsearch
Group=elasticsearch
ExecStart=/usr/share/elasticsearch/bin/elasticsearch
LimitNOFILE=65536
LimitNPROC=4096
Restart=on-failure

[Install]
WantedBy=multi-user.target
EOF

sudo systemctl daemon-reload
sudo systemctl enable elasticsearch
sudo systemctl start elasticsearch
```

### Базовая конфигурация

#### elasticsearch.yml
```yaml
# Cluster configuration
cluster.name: my-application
node.name: node-1
node.roles: [ master, data, ingest ]

# Network configuration
network.host: 0.0.0.0
http.port: 9200

# Discovery (single node)
discovery.type: single-node

# Data paths
path.data: /var/lib/elasticsearch
path.logs: /var/log/elasticsearch

# Memory configuration
bootstrap.memory_lock: true

# Security (basic)
xpack.security.enabled: false
xpack.security.transport.ssl.enabled: false
xpack.security.http.ssl.enabled: false
```

#### JVM Configuration
```bash
# jvm.options
-Xms4g
-Xmx4g
-XX:+UseG1GC
-XX:G1ReservePercent=25
-XX:InitiatingHeapOccupancyPercent=30
-XX:+UnlockExperimentalVMOptions
-XX:+UseCGroupMemoryLimitForHeap
-XX:MaxRAMPercentage=75.0
```

### Безопасность

#### Basic Security Setup
```bash
# Включение security
echo "xpack.security.enabled: true" >> config/elasticsearch.yml
echo "xpack.security.transport.ssl.enabled: true" >> config/elasticsearch.yml
echo "xpack.security.http.ssl.enabled: true" >> config/elasticsearch.yml

# Генерация сертификатов
./bin/elasticsearch-certutil ca
./bin/elasticsearch-certutil cert --ca elastic-stack-ca.p12

# Установка паролей
./bin/elasticsearch-setup-passwords interactive

# Создание пользователей
curl -X POST "localhost:9200/_security/user/kibana_system/_password" \
  -H "Content-Type: application/json" \
  -u elastic \
  -d '{"password":"new_password"}'
```

## Основные понятия

### Документы и индексы

#### Document Structure
```json
// JSON документ в Elasticsearch
{
  "_index": "products",
  "_id": "1",
  "_version": 1,
  "_seq_no": 0,
  "_primary_term": 1,
  "found": true,
  "_source": {
    "name": "Wireless Bluetooth Headphones",
    "description": "High-quality wireless headphones with noise cancellation",
    "price": 199.99,
    "category": "electronics",
    "brand": "AudioTech",
    "tags": ["wireless", "bluetooth", "noise-cancelling"],
    "specifications": {
      "battery_life": "30 hours",
      "weight": "250g",
      "color": "black"
    },
    "created_at": "2023-01-15T10:30:00Z"
  }
}
```

#### Index vs Type (Legacy)
```java
// До Elasticsearch 7.x
PUT /products/electronics/1
{
  "name": "Laptop",
  "category": "electronics"
}

// После Elasticsearch 7.x (type удален)
PUT /products/_doc/1
{
  "name": "Laptop",
  "category": "electronics"
}
```

### CRUD операции

#### Create Document
```bash
# Автоматическая генерация ID
curl -X POST "localhost:9200/products/_doc" \
  -H "Content-Type: application/json" \
  -d '{"name":"Wireless Headphones","price":99.99}'

# Ручное указание ID
curl -X PUT "localhost:9200/products/_doc/1" \
  -H "Content-Type: application/json" \
  -d '{"name":"Wireless Headphones","price":99.99}'

# Bulk indexing
curl -X POST "localhost:9200/_bulk" \
  -H "Content-Type: application/json" \
  -d '
{"index":{"_index":"products","_id":"1"}}
{"name":"Headphones","price":99.99}
{"index":{"_index":"products","_id":"2"}}
{"name":"Keyboard","price":49.99}
'
```

#### Read Document
```bash
# Получение по ID
curl -X GET "localhost:9200/products/_doc/1"

# Проверка существования
curl -X HEAD "localhost:9200/products/_doc/1"

# Получение с routing
curl -X GET "localhost:9200/products/_doc/1?routing=user123"

# Multi-get
curl -X GET "localhost:9200/_mget" \
  -H "Content-Type: application/json" \
  -d '{"docs":[{"_index":"products","_id":"1"},{"_index":"products","_id":"2"}]}'
```

#### Update Document
```bash
# Полное обновление
curl -X PUT "localhost:9200/products/_doc/1" \
  -H "Content-Type: application/json" \
  -d '{"name":"Updated Headphones","price":129.99}'

# Частичное обновление
curl -X POST "localhost:9200/products/_update/1" \
  -H "Content-Type: application/json" \
  -d '{"doc":{"price":149.99}}'

# Update with script
curl -X POST "localhost:9200/products/_update/1" \
  -H "Content-Type: application/json" \
  -d '{
    "script": {
      "source": "ctx._source.price += params.increment",
      "params": {"increment": 10}
    }
  }'

# Upsert
curl -X POST "localhost:9200/products/_update/1" \
  -H "Content-Type: application/json" \
  -d '{
    "script": {"source": "ctx._source.views += 1"},
    "upsert": {"name": "New Product", "views": 1}
  }'
```

#### Delete Document
```bash
# Удаление по ID
curl -X DELETE "localhost:9200/products/_doc/1"

# Удаление по запросу
curl -X POST "localhost:9200/products/_delete_by_query" \
  -H "Content-Type: application/json" \
  -d '{"query":{"term":{"category":"obsolete"}}}'

# Удаление с routing
curl -X DELETE "localhost:9200/products/_doc/1?routing=user123"
```

## Индексы и типы

### Создание индексов

#### Basic Index Creation
```bash
# Создание индекса с настройками по умолчанию
curl -X PUT "localhost:9200/products" \
  -H "Content-Type: application/json" \
  -d '{
    "settings": {
      "number_of_shards": 3,
      "number_of_replicas": 1
    }
  }'

# Создание индекса с кастомными настройками
curl -X PUT "localhost:9200/logs-2023-01" \
  -H "Content-Type: application/json" \
  -d '{
    "settings": {
      "number_of_shards": 5,
      "number_of_replicas": 2,
      "refresh_interval": "30s",
      "index.codec": "best_compression"
    },
    "mappings": {
      "properties": {
        "@timestamp": {"type": "date"},
        "level": {"type": "keyword"},
        "message": {"type": "text"},
        "user_id": {"type": "long"}
      }
    }
  }'
```

#### Index Templates
```bash
# Создание шаблона для индексов логов
curl -X PUT "localhost:9200/_template/logs_template" \
  -H "Content-Type: application/json" \
  -d '{
    "index_patterns": ["logs-*"],
    "settings": {
      "number_of_shards": 3,
      "number_of_replicas": 1,
      "refresh_interval": "30s"
    },
    "mappings": {
      "properties": {
        "@timestamp": {"type": "date"},
        "level": {"type": "keyword"},
        "message": {"type": "text", "analyzer": "standard"},
        "service": {"type": "keyword"},
        "host": {"type": "keyword"}
      }
    }
  }'

# Index Lifecycle Management (ILM)
curl -X PUT "localhost:9200/_ilm/policy/logs_lifecycle" \
  -H "Content-Type: application/json" \
  -d '{
    "policy": {
      "phases": {
        "hot": {
          "min_age": "0ms",
          "actions": {
            "rollover": {
              "max_size": "50gb",
              "max_age": "30d"
            }
          }
        },
        "warm": {
          "min_age": "30d",
          "actions": {
            "forcemerge": {"max_num_segments": 1},
            "shrink": {"number_of_shards": 1},
            "allocate": {"number_of_replicas": 0}
          }
        },
        "cold": {
          "min_age": "90d",
          "actions": {
            "freeze": {},
            "allocate": {"number_of_replicas": 0}
          }
        },
        "delete": {
          "min_age": "365d",
          "actions": {"delete": {}}
        }
      }
    }
  }'
```

### Управление индексами

#### Index Operations
```bash
# Получение информации об индексе
curl -X GET "localhost:9200/products"

# Получение настроек индекса
curl -X GET "localhost:9200/products/_settings"

# Получение mapping индекса
curl -X GET "localhost:9200/products/_mapping"

# Изменение настроек индекса
curl -X PUT "localhost:9200/products/_settings" \
  -H "Content-Type: application/json" \
  -d '{"index":{"number_of_replicas":2}}'

# Закрытие индекса
curl -X POST "localhost:9200/products/_close"

# Открытие индекса
curl -X POST "localhost:9200/products/_open"

# Удаление индекса
curl -X DELETE "localhost:9200/products"
```

#### Index Aliases
```bash
# Создание алиаса
curl -X POST "localhost:9200/_aliases" \
  -H "Content-Type: application/json" \
  -d '{
    "actions": [
      {"add": {"index": "products-2023-01", "alias": "products_current"}},
      {"add": {"index": "products-2023-02", "alias": "products_current"}}
    ]
  }'

# Алиас с фильтром
curl -X POST "localhost:9200/_aliases" \
  -H "Content-Type: application/json" \
  -d '{
    "actions": [{
      "add": {
        "index": "products-*",
        "alias": "active_products",
        "filter": {"term": {"status": "active"}}
      }
    }]
  }'

# Переключение алиаса (blue-green deployment)
curl -X POST "localhost:9200/_aliases" \
  -H "Content-Type: application/json" \
  -d '{
    "actions": [
      {"remove": {"index": "products-v1", "alias": "products"}},
      {"add": {"index": "products-v2", "alias": "products"}}
    ]
  }'
```

## Mapping и схемы

### Data Types

#### Core Data Types
```json
{
  "mappings": {
    "properties": {
      "user_id": {
        "type": "long"
      },
      "username": {
        "type": "keyword",
        "index": true
      },
      "email": {
        "type": "keyword",
        "normalizer": "lowercase_normalizer"
      },
      "full_name": {
        "type": "text",
        "analyzer": "standard",
        "fields": {
          "keyword": {
            "type": "keyword",
            "ignore_above": 256
          }
        }
      },
      "age": {
        "type": "integer"
      },
      "balance": {
        "type": "scaled_float",
        "scaling_factor": 100
      },
      "is_active": {
        "type": "boolean"
      },
      "created_at": {
        "type": "date",
        "format": "yyyy-MM-dd'T'HH:mm:ss.SSSZZ"
      },
      "last_login": {
        "type": "date",
        "format": "epoch_millis"
      }
    }
  }
}
```

#### Complex Data Types
```json
{
  "mappings": {
    "properties": {
      "location": {
        "type": "geo_point"
      },
      "address": {
        "type": "object",
        "properties": {
          "street": {"type": "text"},
          "city": {"type": "keyword"},
          "country": {"type": "keyword"},
          "coordinates": {"type": "geo_point"}
        }
      },
      "tags": {
        "type": "keyword"
      },
      "metadata": {
        "type": "object",
        "enabled": false
      },
      "history": {
        "type": "nested",
        "properties": {
          "timestamp": {"type": "date"},
          "action": {"type": "keyword"},
          "user": {"type": "keyword"}
        }
      },
      "attachments": {
        "type": "binary"
      },
      "vector_embedding": {
        "type": "dense_vector",
        "dims": 128
      }
    }
  }
}
```

### Dynamic Mapping

#### Dynamic Mapping Rules
```json
{
  "mappings": {
    "dynamic": "strict",
    "properties": {
      "name": {"type": "text"},
      "age": {"type": "integer"}
    },
    "dynamic_templates": [
      {
        "strings_as_keywords": {
          "match_mapping_type": "string",
          "mapping": {
            "type": "keyword",
            "normalizer": "lowercase_normalizer"
          }
        }
      },
      {
        "integers_as_longs": {
          "match_mapping_type": "long",
          "mapping": {"type": "long"}
        }
      },
      {
        "dates_as_dates": {
          "match": "*_date",
          "mapping": {"type": "date"}
        }
      }
    ]
  }
}
```

#### Runtime Fields
```json
{
  "mappings": {
    "properties": {
      "price": {"type": "double"},
      "tax_rate": {"type": "double"}
    },
    "runtime": {
      "price_with_tax": {
        "type": "double",
        "script": {
          "source": "emit(doc['price'].value * (1 + doc['tax_rate'].value))"
        }
      },
      "price_category": {
        "type": "keyword",
        "script": {
          "source": """
            double price = doc['price'].value;
            if (price < 10) emit('cheap');
            else if (price < 100) emit('moderate');
            else emit('expensive');
          """
        }
      }
    }
  }
}
```

## Анализ текста

### Analyzers

#### Built-in Analyzers
```json
{
  "settings": {
    "analysis": {
      "analyzer": {
        "custom_analyzer": {
          "type": "custom",
          "tokenizer": "standard",
          "char_filter": ["html_strip"],
          "filter": ["lowercase", "stop", "porter_stem"]
        }
      },
      "tokenizer": {
        "custom_tokenizer": {
          "type": "pattern",
          "pattern": "\\W+"
        }
      },
      "char_filter": {
        "custom_char_filter": {
          "type": "mapping",
          "mappings": ["ph=>f", "qu=>q"]
        }
      },
      "filter": {
        "custom_stemmer": {
          "type": "stemmer",
          "language": "english"
        },
        "custom_stop": {
          "type": "stop",
          "stopwords": ["the", "a", "an", "and", "or"]
        }
      }
    }
  },
  "mappings": {
    "properties": {
      "title": {
        "type": "text",
        "analyzer": "custom_analyzer",
        "search_analyzer": "standard"
      },
      "description": {
        "type": "text",
        "analyzer": "english",
        "fields": {
          "keyword": {"type": "keyword"}
        }
      }
    }
  }
}
```

#### Language Analyzers
```json
{
  "settings": {
    "analysis": {
      "analyzer": {
        "russian_analyzer": {
          "type": "custom",
          "tokenizer": "standard",
          "filter": [
            "lowercase",
            "russian_stop",
            "russian_stemmer"
          ]
        }
      },
      "filter": {
        "russian_stop": {
          "type": "stop",
          "stopwords": "_russian_"
        },
        "russian_stemmer": {
          "type": "stemmer",
          "language": "russian"
        }
      }
    }
  },
  "mappings": {
    "properties": {
      "content": {
        "type": "text",
        "analyzer": "russian_analyzer"
      }
    }
  }
}
```

### Text Analysis Process

#### Analyze API
```bash
# Анализ текста
curl -X GET "localhost:9200/_analyze" \
  -H "Content-Type: application/json" \
  -d '{
    "analyzer": "standard",
    "text": "The quick brown fox jumps over the lazy dog"
  }'

# Результат:
{
  "tokens": [
    {"token": "the", "start_offset": 0, "end_offset": 3, "type": "<ALPHANUM>", "position": 0},
    {"token": "quick", "start_offset": 4, "end_offset": 9, "type": "<ALPHANUM>", "position": 1},
    {"token": "brown", "start_offset": 10, "end_offset": 15, "type": "<ALPHANUM>", "position": 2},
    {"token": "fox", "start_offset": 16, "end_offset": 19, "type": "<ALPHANUM>", "position": 3},
    {"token": "jumps", "start_offset": 20, "end_offset": 25, "type": "<ALPHANUM>", "position": 4},
    {"token": "over", "start_offset": 26, "end_offset": 30, "type": "<ALPHANUM>", "position": 5},
    {"token": "the", "start_offset": 31, "end_offset": 34, "type": "<ALPHANUM>", "position": 6},
    {"token": "lazy", "start_offset": 35, "end_offset": 39, "type": "<ALPHANUM>", "position": 7},
    {"token": "dog", "start_offset": 40, "end_offset": 43, "type": "<ALPHANUM>", "position": 8}
  ]
}
```

## Java API и интеграция

### Elasticsearch Java Client

#### High Level REST Client (Legacy)
```xml
<dependency>
    <groupId>org.elasticsearch.client</groupId>
    <artifactId>elasticsearch-rest-high-level-client</artifactId>
    <version>7.17.10</version>
</dependency>
```

```java
@Configuration
public class ElasticsearchConfig {

    @Bean
    public RestHighLevelClient elasticsearchClient() {
        RestHighLevelClient client = new RestHighLevelClient(
            RestClient.builder(
                new HttpHost("localhost", 9200, "http"),
                new HttpHost("localhost", 9201, "http")
            )
        );
        return client;
    }
}

@Service
public class ElasticsearchService {

    @Autowired
    private RestHighLevelClient client;

    public void indexDocument(String index, String id, Map<String, Object> document) throws IOException {
        IndexRequest request = new IndexRequest(index)
            .id(id)
            .source(document)
            .timeout(TimeValue.timeValueSeconds(5));

        IndexResponse response = client.index(request, RequestOptions.DEFAULT);
        System.out.println("Document indexed: " + response.getResult());
    }

    public Map<String, Object> getDocument(String index, String id) throws IOException {
        GetRequest request = new GetRequest(index, id);
        GetResponse response = client.get(request, RequestOptions.DEFAULT);

        if (response.isExists()) {
            return response.getSourceAsMap();
        } else {
            throw new DocumentNotFoundException("Document not found: " + id);
        }
    }

    public SearchResponse searchDocuments(String index, String query) throws IOException {
        SearchRequest searchRequest = new SearchRequest(index);
        SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();

        // Build query
        QueryBuilder queryBuilder = QueryBuilders.multiMatchQuery(query, "title", "description");
        sourceBuilder.query(queryBuilder);
        sourceBuilder.from(0);
        sourceBuilder.size(10);

        searchRequest.source(sourceBuilder);
        return client.search(searchRequest, RequestOptions.DEFAULT);
    }

    public void updateDocument(String index, String id, Map<String, Object> updates) throws IOException {
        UpdateRequest request = new UpdateRequest(index, id)
            .doc(updates)
            .timeout(TimeValue.timeValueSeconds(5));

        UpdateResponse response = client.update(request, RequestOptions.DEFAULT);
        System.out.println("Document updated: " + response.getResult());
    }

    public void deleteDocument(String index, String id) throws IOException {
        DeleteRequest request = new DeleteRequest(index, id);
        DeleteResponse response = client.delete(request, RequestOptions.DEFAULT);
        System.out.println("Document deleted: " + response.getResult());
    }

    public void bulkIndex(String index, List<Map<String, Object>> documents) throws IOException {
        BulkRequest bulkRequest = new BulkRequest();

        for (Map<String, Object> document : documents) {
            IndexRequest indexRequest = new IndexRequest(index)
                .source(document);
            bulkRequest.add(indexRequest);
        }

        BulkResponse bulkResponse = client.bulk(bulkRequest, RequestOptions.DEFAULT);

        if (bulkResponse.hasFailures()) {
            for (BulkItemResponse itemResponse : bulkResponse.getItems()) {
                if (itemResponse.isFailed()) {
                    System.err.println("Bulk operation failed: " + itemResponse.getFailureMessage());
                }
            }
        }
    }
}
```

### Java API Client 8.x
```xml
<dependency>
    <groupId>co.elastic.clients</groupId>
    <artifactId>elasticsearch-java</artifactId>
    <version>8.11.0</version>
</dependency>
```

```java
@Configuration
public class ElasticsearchJavaApiConfig {

    @Bean
    public ElasticsearchClient elasticsearchClient() {
        RestClient restClient = RestClient.builder(
            new HttpHost("localhost", 9200)
        ).build();

        ElasticsearchTransport transport = new RestClientTransport(
            restClient, new JacksonJsonpMapper()
        );

        return new ElasticsearchClient(transport);
    }
}

@Service
public class ModernElasticsearchService {

    @Autowired
    private ElasticsearchClient client;

    public void indexProduct(Product product) throws IOException {
        IndexRequest<Product> request = IndexRequest.of(i -> i
            .index("products")
            .id(product.getId())
            .document(product)
        );

        IndexResponse response = client.index(request);
        System.out.println("Indexed with version " + response.version());
    }

    public Product getProduct(String id) throws IOException {
        GetRequest request = GetRequest.of(g -> g
            .index("products")
            .id(id)
        );

        GetResponse<Product> response = client.get(request, Product.class);

        if (response.found()) {
            return response.source();
        } else {
            throw new ProductNotFoundException("Product not found: " + id);
        }
    }

    public SearchResponse<Product> searchProducts(String query, int from, int size) throws IOException {
        SearchRequest request = SearchRequest.of(s -> s
            .index("products")
            .query(q -> q
                .multiMatch(m -> m
                    .fields("name", "description")
                    .query(query)
                )
            )
            .from(from)
            .size(size)
        );

        return client.search(request, Product.class);
    }

    public void updateProductPrice(String productId, BigDecimal newPrice) throws IOException {
        UpdateRequest<Product, Product> request = UpdateRequest.of(u -> u
            .index("products")
            .id(productId)
            .doc(Map.of("price", newPrice))
        );

        UpdateResponse<Product> response = client.update(request, Product.class);
        System.out.println("Updated product: " + response.result());
    }

    public void deleteProduct(String productId) throws IOException {
        DeleteRequest request = DeleteRequest.of(d -> d
            .index("products")
            .id(productId)
        );

        DeleteResponse response = client.delete(request);
        System.out.println("Deleted product: " + response.result());
    }

    public void bulkIndexProducts(List<Product> products) throws IOException {
        List<BulkOperation> operations = products.stream()
            .map(product -> BulkOperation.of(o -> o
                .index(i -> i
                    .document(product)
                )
            ))
            .collect(Collectors.toList());

        BulkRequest request = BulkRequest.of(b -> b
            .operations(operations)
        );

        BulkResponse response = client.bulk(request);

        if (response.errors()) {
            for (BulkResponseItem item : response.items()) {
                if (item.error() != null) {
                    System.err.println("Bulk operation failed: " + item.error().reason());
                }
            }
        }
    }
}
```

## Spring Data Elasticsearch

### Configuration
```java
@Configuration
@EnableElasticsearchRepositories(basePackages = "com.example.repository")
public class ElasticsearchConfiguration extends AbstractElasticsearchConfiguration {

    @Bean
    @Override
    public RestHighLevelClient elasticsearchClient() {
        ClientConfiguration clientConfiguration = ClientConfiguration.builder()
            .connectedTo("localhost:9200", "localhost:9201", "localhost:9202")
            .withBasicAuth("elastic", "password")
            .build();

        return RestClients.create(clientConfiguration).rest();
    }

    @Bean
    public ElasticsearchOperations elasticsearchOperations() {
        return new ElasticsearchRestTemplate(elasticsearchClient());
    }
}
```

### Entity Mapping
```java
@Document(indexName = "products", createIndex = false)
@Setting(settingPath = "/settings/products-settings.json")
@Mapping(mappingPath = "/mappings/products-mapping.json")
public class Product {

    @Id
    private String id;

    @Field(type = FieldType.Text, analyzer = "standard")
    private String name;

    @Field(type = FieldType.Text, analyzer = "english")
    private String description;

    @Field(type = FieldType.Double)
    private BigDecimal price;

    @Field(type = FieldType.Keyword)
    private String category;

    @Field(type = FieldType.Keyword)
    private List<String> tags;

    @Field(type = FieldType.Nested)
    private List<Review> reviews;

    @Field(type = FieldType.Date, format = DateFormat.date_hour_minute_second)
    private LocalDateTime createdAt;

    @Field(type = FieldType.Geo_Point)
    private GeoPoint location;

    // constructors, getters, setters
}

@Document(indexName = "reviews")
public class Review {

    @Id
    private String id;

    @Field(type = FieldType.Keyword)
    private String productId;

    @Field(type = FieldType.Integer)
    private Integer rating;

    @Field(type = FieldType.Text)
    private String comment;

    @Field(type = FieldType.Keyword)
    private String author;

    @Field(type = FieldType.Date)
    private LocalDateTime createdAt;

    // constructors, getters, setters
}
```

### Repository Interface
```java
public interface ProductRepository extends ElasticsearchRepository<Product, String> {

    // Поиск по имени
    List<Product> findByName(String name);

    // Поиск по имени или описанию
    List<Product> findByNameOrDescription(String name, String description);

    // Поиск по цене в диапазоне
    List<Product> findByPriceBetween(BigDecimal minPrice, BigDecimal maxPrice);

    // Поиск по категории
    List<Product> findByCategory(String category);

    // Поиск по тегам
    List<Product> findByTags(String tag);

    // Кастомные запросы с @Query
    @Query("{\"bool\": {\"must\": [{\"match\": {\"name\": \"?0\"}}]}}")
    List<Product> findByNameCustom(String name);

    @Query("{\"range\": {\"price\": {\"gte\": ?0, \"lte\": ?1}}}")
    List<Product> findByPriceRange(BigDecimal min, BigDecimal max);

    // Геопоиск
    @Query("{\"geo_distance\": {\"distance\": \"?1km\", \"location\": {\"lat\": ?2, \"lon\": ?3}}}")
    List<Product> findByLocationWithin(String productName, double distance, double lat, double lon);

    // Агрегации
    @Query("{\"aggs\": {\"categories\": {\"terms\": {\"field\": \"category\"}}}}")
    SearchHits<Product> findWithCategoryAggregation();

    // Пагинация
    Page<Product> findByCategory(String category, Pageable pageable);

    // Сортировка
    List<Product> findByCategoryOrderByPriceDesc(String category);

    // Кастомные методы с Criteria
    default List<Product> findExpensiveElectronics(BigDecimal minPrice) {
        Criteria criteria = new Criteria("category").is("electronics")
            .and("price").greaterThanEqual(minPrice);

        Query query = new CriteriaQuery(criteria);
        SearchHits<Product> searchHits = search(query);

        return searchHits.stream()
            .map(SearchHit::getContent)
            .collect(Collectors.toList());
    }
}
```

### Service Layer
```java
@Service
@Transactional
public class ProductService {

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private ElasticsearchOperations elasticsearchOperations;

    public Product createProduct(Product product) {
        product.setCreatedAt(LocalDateTime.now());
        return productRepository.save(product);
    }

    public Optional<Product> getProduct(String id) {
        return productRepository.findById(id);
    }

    public List<Product> searchProducts(String query) {
        return productRepository.findByNameOrDescription(query, query);
    }

    public List<Product> findProductsByPriceRange(BigDecimal min, BigDecimal max) {
        return productRepository.findByPriceBetween(min, max);
    }

    public Page<Product> findProductsByCategory(String category, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("price").descending());
        return productRepository.findByCategory(category, pageable);
    }

    public List<Product> findNearbyProducts(double lat, double lon, double radiusKm) {
        // Геопоиск
        GeoDistanceQuery query = QueryBuilders.geoDistanceQuery("location")
            .point(lat, lon)
            .distance(radiusKm, DistanceUnit.KILOMETERS);

        SearchHits<Product> searchHits = elasticsearchOperations.search(
            query, Product.class
        );

        return searchHits.stream()
            .map(SearchHit::getContent)
            .collect(Collectors.toList());
    }

    public Map<String, Long> getCategoryStats() {
        // Агрегация по категориям
        TermsAggregationBuilder aggregation = AggregationBuilders.terms("categories")
            .field("category");

        SearchQuery searchQuery = new NativeSearchQueryBuilder()
            .addAggregation(aggregation)
            .build();

        Aggregations aggregations = elasticsearchOperations.search(searchQuery, Product.class)
            .getAggregations();

        Terms terms = aggregations.get("categories");

        return terms.getBuckets().stream()
            .collect(Collectors.toMap(
                Terms.Bucket::getKeyAsString,
                Terms.Bucket::getDocCount
            ));
    }

    public void updateProductPrice(String productId, BigDecimal newPrice) {
        Product product = productRepository.findById(productId)
            .orElseThrow(() -> new ProductNotFoundException("Product not found: " + productId));

        product.setPrice(newPrice);
        productRepository.save(product);
    }

    public void deleteProduct(String productId) {
        productRepository.deleteById(productId);
    }

    public void bulkUpdatePrices(Map<String, BigDecimal> priceUpdates) {
        List<Product> productsToUpdate = productRepository.findAllById(priceUpdates.keySet());

        for (Product product : productsToUpdate) {
            BigDecimal newPrice = priceUpdates.get(product.getId());
            if (newPrice != null) {
                product.setPrice(newPrice);
            }
        }

        productRepository.saveAll(productsToUpdate);
    }

    public SearchHits<Product> advancedSearch(String query, String category,
                                            BigDecimal minPrice, BigDecimal maxPrice,
                                            int page, int size) {
        BoolQueryBuilder boolQuery = QueryBuilders.boolQuery();

        // Текстовый поиск
        if (query != null && !query.trim().isEmpty()) {
            boolQuery.must(QueryBuilders.multiMatchQuery(query, "name", "description"));
        }

        // Фильтр по категории
        if (category != null && !category.trim().isEmpty()) {
            boolQuery.filter(QueryBuilders.termQuery("category", category));
        }

        // Фильтр по цене
        if (minPrice != null || maxPrice != null) {
            RangeQueryBuilder priceRange = QueryBuilders.rangeQuery("price");
            if (minPrice != null) {
                priceRange.gte(minPrice);
            }
            if (maxPrice != null) {
                priceRange.lte(maxPrice);
            }
            boolQuery.filter(priceRange);
        }

        NativeSearchQuery searchQuery = new NativeSearchQueryBuilder()
            .withQuery(boolQuery)
            .withPageable(PageRequest.of(page, size))
            .build();

        return elasticsearchOperations.search(searchQuery, Product.class);
    }
}
```

## Лучшие практики

### Index Design

#### 1. Shard Strategy
- **Правильное количество шардов**: 1 шард на 30-50GB данных
- **Не переусердствуй**: Начни с 1-5 шардов на индекс
- **Реплики для redundancy**: Минимум 1 реплика для **production**
- **Rolling индексы**: Для **time-series** данных

#### 2. Mapping Best Practices
- **Явное mapping**: Не полагайся на **dynamic mapping** в **production**
- **Keywords для exact match**: Используй **keyword** тип для фильтров
- **Multi-fields**: Для анализа и агрегаций
- **Disable unused features**: Отключи _all, **norms** если не нужны

#### 3. Data Modeling
- **Denormalize when needed**: Для **search performance**
- **Use nested objects**: Для связанных данных
- **Avoid deep nesting**: Максимум 3-4 уровня вложенности
- **Plan for updates**: Обновления переиндексируют весь документ

### Performance Optimization

#### 1. Query Optimization
- **Filter before query**: Используй **filters** для **frequently changing data**
- **Pagination limits**: Ограничь **size** и **from** параметры
- **Cache queries**: Для повторяющихся запросов
- **Use scroll API**: Для больших **result sets**

#### 2. Index Optimization
- **Merge policy**: Оптимизируй **segment merging**
- **Refresh interval**: Установи оптимальный интервал обновления
- **Translog settings**: Балансируй **durability** и **performance**
- **Codec selection**: Выбери подходящий **compression codec**

#### 3. Hardware Considerations
- **SSD storage**: Обязательно для **production**
- **Memory allocation**: 50% от **system memory** для **heap**
- **CPU cores**: Минимум 4 **cores** для **data nodes**
- **Network bandwidth**: 1Gbps минимум, 10Gbps рекомендуется

### Monitoring and Alerting

#### 1. Key Metrics
- **Search performance**: **Response time**, **throughput**
- **Indexing performance**: **Index rate**, **refresh time**
- **Cluster health**: **Status**, **unassigned shards**
- **Resource usage**: **CPU**, **memory**, **disk**, **network**

#### 2. Common Alerts
- **Red cluster status**: **Critical** — **immediate action required**
- **High `JVM` memory usage**: **Warning** — **investigate memory leaks**
- **Slow queries**: **Info** — **performance monitoring**
- **Disk space low**: **Warning** — **plan capacity expansion**

#### 3. Troubleshooting
- **Slow logs**: Включи **slow log** для анализа
- **Hot threads**: Анализ горячих потоков
- **Circuit breakers**: Мониторинг **circuit breaker exceptions**
- **Index statistics**: Анализ индексных метрик

### Security Best Practices

#### 1. Authentication & Authorization
- **Enable `X-Pack` Security**: Для **production deployments**
- **Strong passwords**: Сложные пароли для системных пользователей
- **Role-based access**: Ограничь доступ по ролям
- **TLS encryption**: Шифрование всех коммуникаций

#### 2. Network Security
- **Firewall rules**: Ограничь доступ к **Elasticsearch ports**
- **VPC/`Security` groups**: Изоляция сети
- **SSL/TLS**: Для всех соединений
- **API keys**: Для **application access**

#### 3. Data Protection
- **Field-level security**: Ограничь доступ к чувствительным полям
- **Index-level permissions**: Контроль доступа к индексам
- **Audit logging**: Включи аудит всех операций
- **Backup encryption**: Шифрование резервных копий

## Решение проблем

| Симптом | Возможная причина | Решение |
|--------|-------------------|---------|
| Медленный поиск или высокий latency | Тяжёлый mapping, отсутствие индексов, слишком много шардов | Оптимизировать mapping (keyword vs text); проверить размер шардов; использовать профилирование (Profile API) |
| Cluster red/yellow, unassigned shards | Нехватка узлов для реплик, диск переполнен, сбой узла | Добавить узлы или снизить число реплик; освободить место; проверить allocation explain API |
| OutOfMemoryError на узле | Большая heap, тяжёлые агрегации, большие запросы | Ограничить размер запросов и агрегаций; настроить circuit breaker; проверить лимиты heap |

## Частые вопросы

**Когда использовать Elasticsearch, а не БД?** Для полнотекстового поиска, логов, метрик и аналитики по большим объёмам — да. Как основное хранилище для транзакционных CRUD — не лучший выбор; часто используют в связке с БД (БД — источник правды, ES — поиск).

**Сколько шардов на индекс?** Ориентир — один шард до десятков ГБ; слишком много мелких шардов даёт накладные расходы. Для начала один primary shard на индекс; масштабировать при росте.

**Как обеспечить консистентность с основной БД?** Реализовать синхронизацию (CDC, логирование, очереди): события из БД обновление/индексация в ES. Понимать, что поиск в ES с задержкой (near real-time) — норма.

## Заключение

**Elasticsearch** — мощный и гибкий поисковый движок, который может решать широкий спектр задач: от простого поиска и аналитики до **complex enterprise search** решений. Основные преимущества:

### Ключевые особенности:

1. **Полнотекстовый поиск** — Быстрый и релевантный поиск по тексту
2. **Распределенная архитектура** — Горизонтальное масштабирование
3. **Real-time analytics** — Агрегации и статистика в реальном времени
4. **Schema flexibility** — Гибкое моделирование данных
5. **Rich `API` ecosystem** — **Java**, **REST**, и множество языков

### Архитектурные принципы:

1. **Инвертированные индексы** — Основа поиска
2. **Шардинг и репликация** — Масштабируемость и отказоустойчивость
3. **Distributed coordination** — Автоматическое управление кластером
4. **RESTful API** — Простой и интуитивный интерфейс

### Производительность:

1. **In-memory operations** — Быстрые запросы и агрегации
2. **Optimized storage** — Эффективное использование дискового пространства
3. **Caching layers** — Множественные уровни кэширования
4. **Async processing** — Неблокирующие операции

### Интеграция:

1. **Spring ecosystem** — Отличная поддержка **Spring Boot** и **Spring Data**
2. **Java API** — Нативный **Java** клиент для комплексных задач
3. **REST API** — Универсальный интерфейс для всех языков
4. **Third-party tools** — **Kibana**, **Logstash**, **Beats**

### Best practices:

1. **Правильное планирование** — **Shard strategy**, **mapping design**
2. **Performance monitoring** — Ключевые метрики и **alerting**
3. **Security hardening** — Аутентификация, авторизация, шифрование
4. **Operational excellence** — **Backup**, **monitoring**, **automation**

**Elasticsearch** — отличный выбор для приложений, требующих мощного поиска, аналитики и масштабируемого хранения данных. С правильной архитектурой и настройкой он обеспечивает высокую производительность и надежность в **production** средах.

**Следующие файлы `Elasticsearch`:**
- **elasticsearch-basics.md** (завершен)
- **elasticsearch-indexing.md**
- **elasticsearch-queries.md**
- **elasticsearch-aggregations.md**
- **elasticsearch-clustering.md**
- **elasticsearch-performance.md**

Продолжение следует...


