---
title: "Elasticsearch: Индексация документов - Управление данными и индексацией"
description: "Комплексное руководство по индексации документов в Elasticsearch: bulk operations, update strategies, routing, versioning и оптимизация производительности индексации."
tags:
  - databases
  - nosql
  - elasticsearch-indexing
difficulty: "intermediate"
prerequisites: []
next: []
updated: "2026-02-11"
---
# **Elasticsearch**: Индексация документов - Управление данными и индексацией

Комплексное руководство по индексации документов в **Elasticsearch**: **bulk operations**, **update strategies**, **routing**, **versioning** и оптимизация производительности индексации.

## Полезные ссылки

### Официальная документация
- [Indexing Documents](https://www.elastic.co/guide/en/elasticsearch/reference/current/docs-index_.html)
- [Bulk API](https://www.elastic.co/guide/en/elasticsearch/reference/current/docs-bulk.html)
- [Update API](https://www.elastic.co/guide/en/elasticsearch/reference/current/docs-update.html)
- [Delete API](https://www.elastic.co/guide/en/elasticsearch/reference/current/docs-delete.html)

### Дизайн и оптимизация
- [Indexing Performance](https://www.elastic.co/guide/en/elasticsearch/reference/current/tune-for-indexing-speed.html)
- [Index Settings](https://www.elastic.co/guide/en/elasticsearch/reference/current/index-modules.html)
- [Translog](https://www.elastic.co/guide/en/elasticsearch/reference/current/index-modules-translog.html)

### Инструменты
- [Elasticsearch Bulk Processor](https://www.elastic.co/guide/en/elasticsearch/client/java-api-client/current/java-docs-bulk-processor.html)
- [Logstash](https://www.elastic.co/guide/en/logstash/current/index.html) — **ETL** инструмент
- [Beats](https://www.elastic.co/guide/en/beats/libbeat/current/index.html) — легковесные **shippers**

### См. также
- [Основы](elasticsearch-basics.md) — **Elasticsearch**
- [Запросы](elasticsearch-queries.md) — поиск и запросы
- [Производительность](elasticsearch-performance.md) — производительность

## Содержание

- [**Index API**](#index-api)
  - [**Single Document Indexing**](#single-document-indexing)
    - [**Basic Indexing**](#basic-indexing)
- [Автоматическая генерация ID](#автоматическая-генерация-id)
- [Ручное указание ID](#ручное-указание-id)
- [Индексация с routing](#индексация-с-routing)
- [Индексация с timeout](#индексация-с-timeout)
- [Индексация с version control](#индексация-с-version-control)
    - [**Response Analysis**](#response-analysis)
  - [**Index Parameters**](#index-parameters)
    - [**Control Parameters**](#control-parameters)
- [Определение типа операции](#определение-типа-операции)
- [Pipeline processing](#pipeline-processing)
- [Parent-child relationships (legacy)](#parent-child-relationships-legacy)
- [Wait for active shards](#wait-for-active-shards)
    - [**Ingest Pipelines**](#ingest-pipelines)
- [**Bulk Operations**](#bulk-operations)
  - [**Bulk API**](#bulk-api)
    - [**Basic Bulk Operations**](#basic-bulk-operations)
- [Bulk индексация](#bulk-индексация)
- [Bulk с routing](#bulk-с-routing)
    - [**Bulk Response Analysis**](#bulk-response-analysis)
  - [**Bulk Size Optimization**](#bulk-size-optimization)
    - [**Optimal Bulk Size**](#optimal-bulk-size)
- [Маленькие bulk (неэффективно)](#маленькие-bulk-неэффективно)
- [Оптимальный размер bulk (5-15MB)](#оптимальный-размер-bulk-5-15mb)
- [~1000 операций в одном bulk request](#1000-операций-в-одном-bulk-request)
- [Размер payload: 5-15MB](#размер-payload-5-15mb)
- [... 999 more operations ...](#999-more-operations)
- [Слишком большой bulk (может вызвать timeout)](#слишком-большой-bulk-может-вызвать-timeout)
- [10000+ операций](#10000-операций)
- [Размер payload: >50MB (проблематично)](#размер-payload-50mb-проблематично)
    - [**Bulk Performance Tuning**](#bulk-performance-tuning)
  - [**Error Handling** in **Bulk**](#error-handling-in-bulk)
    - [**Handling Bulk Errors**](#handling-bulk-errors)
- [**Update Operations**](#update-operations)
  - [**Update API**](#update-api)
    - [**Scripted Updates**](#scripted-updates)
- [Простое обновление поля](#простое-обновление-поля)
- [Условное обновление](#условное-обновление)
- [Обновление с scripted upsert](#обновление-с-scripted-upsert)
    - [**Advanced Update Scripts**](#advanced-update-scripts)
- [Обновление массивов](#обновление-массивов)
- [Условное обновление с несколькими полями](#условное-обновление-с-несколькими-полями)
- [Обновление вложенных объектов](#обновление-вложенных-объектов)
  - [**Update** by **Query**](#update-by-query)
    - [**Update Multiple Documents**](#update-multiple-documents)
- [Обновление всех документов по условию](#обновление-всех-документов-по-условию)
- [Update by query с conflicts handling](#update-by-query-с-conflicts-handling)
- [Update by query с batching](#update-by-query-с-batching)
- [**Delete Operations**](#delete-operations)
  - [**Delete** by ID](#delete-by-id)
    - [**Single Document Deletion**](#single-document-deletion)
- [Удаление по ID](#удаление-по-id)
- [Удаление с routing](#удаление-с-routing)
- [Удаление с version control](#удаление-с-version-control)
    - [**Delete Response**](#delete-response)
  - [**Delete** by **Query**](#delete-by-query)
    - [**Delete Multiple Documents**](#delete-multiple-documents)
- [Удаление по условию](#удаление-по-условию)
- [Удаление с conflicts handling](#удаление-с-conflicts-handling)
- [Удаление с batching для больших объемов](#удаление-с-batching-для-больших-объемов)
  - [**Delete Index**](#delete-index)
    - [**Index Deletion**](#index-deletion)
- [Удаление индекса](#удаление-индекса)
- [Удаление нескольких индексов](#удаление-нескольких-индексов)
- [Удаление всех индексов (опасно!)](#удаление-всех-индексов-опасно)
- [Удаление с wildcard](#удаление-с-wildcard)
- [**Routing** и **Versioning**](#routing-и-versioning)
  - [**Custom Routing**](#custom-routing)
    - [**Routing Strategies**](#routing-strategies)
- [Routing на основе user_id](#routing-на-основе-user_id)
- [Поиск с routing (оптимизация)](#поиск-с-routing-оптимизация)
- [Update с routing](#update-с-routing)
    - [**Routing** in **Mapping**](#routing-in-mapping)
  - [**Version Control**](#version-control)
    - [**Internal Versioning**](#internal-versioning)
- [Elasticsearch internal versioning](#elasticsearch-internal-versioning)
- [Update с version check](#update-с-version-check)
    - [**External Versioning**](#external-versioning)
- [External version control (application managed)](#external-version-control-application-managed)
- [Update только если external version выше](#update-только-если-external-version-выше)
    - [**Version Conflicts**](#version-conflicts)
- [**Refresh** и **Flush**](#refresh-и-flush)
  - [**Refresh Operations**](#refresh-operations)
    - [**Manual Refresh**](#manual-refresh)
- [Refresh конкретного индекса](#refresh-конкретного-индекса)
- [Refresh всех индексов](#refresh-всех-индексов)
- [Refresh нескольких индексов](#refresh-нескольких-индексов)
    - [**Refresh Settings**](#refresh-settings)
  - [**Flush Operations**](#flush-operations)
    - [**Index Flush**](#index-flush)
- [Flush конкретного индекса](#flush-конкретного-индекса)
- [Flush всех индексов](#flush-всех-индексов)
- [Flush с параметрами](#flush-с-параметрами)
- [Force flush](#force-flush)
    - [**Translog Settings**](#translog-settings)
- [**Index Lifecycle Management**](#index-lifecycle-management)
  - [**ILM Policies**](#ilm-policies)
    - [**Creating ILM Policy**](#creating-ilm-policy)
    - [**Applying ILM Policy**](#applying-ilm-policy)
  - [**Index Templates with ILM**](#index-templates-with-ilm)
    - [**Template with ILM**](#template-with-ilm)
- [**Optimistic Concurrency Control**](#optimistic-concurrency-control)
  - [**Version-based Updates**](#version-based-updates)
    - [**OCC with Versions**](#occ-with-versions)
- [Получение текущего документа с version](#получение-текущего-документа-с-version)
- [Update с external version](#update-с-external-version)
    - [**Sequence Numbers and Primary Terms**](#sequence-numbers-and-primary-terms)
- [Update с seq_no и primary_term](#update-с-seq_no-и-primary_term)
- [Получение seq_no и primary_term](#получение-seq_no-и-primary_term)
- [Response includes _seq_no and _primary_term](#response-includes-_seq_no-and-_primary_term)
  - [**Retry Logic**](#retry-logic)
    - [**Implementing Retry** on **Conflicts**](#implementing-retry-on-conflicts)
- [**Indexing Performance Tuning**](#indexing-performance-tuning)
  - [**Index Settings Optimization**](#index-settings-optimization)
    - [**Bulk Indexing Settings**](#bulk-indexing-settings)
    - [**Thread Pool Settings**](#thread-pool-settings)
  - [**Hardware Optimization**](#hardware-optimization)
    - [**Disk** I/O **Tuning**](#disk-io-tuning)
- [Для SSD дисков](#для-ssd-дисков)
- [I/O tuning](#io-tuning)
- [Disable swap (важно для Elasticsearch)](#disable-swap-важно-для-elasticsearch)
    - [**Memory Tuning**](#memory-tuning)
- [Huge pages](#huge-pages)
- [Memory settings](#memory-settings)
  - [**JVM Tuning for Indexing**](#jvm-tuning-for-indexing)
    - [GC **Tuning**](#gc-tuning)
- [JVM options for indexing performance](#jvm-options-for-indexing-performance)
- [GC logging](#gc-logging)
- [**Error Handling**](#error-handling)
  - [**Common Indexing Errors**](#common-indexing-errors)
    - [**Mapper Parsing Exceptions**](#mapper-parsing-exceptions)
    - [**Circuit Breaker Exceptions**](#circuit-breaker-exceptions)
  - [**Handling Failures**](#handling-failures)
    - [**Retry Logic for Bulk Operations**](#retry-logic-for-bulk-operations)
- [**Java Bulk Processing**](#java-bulk-processing)
  - [**BulkProcessor Configuration**](#bulkprocessor-configuration)
    - [**Modern Java API Bulk Processing**](#modern-java-api-bulk-processing)
  - [**Reactive Bulk Processing**](#reactive-bulk-processing)
    - [**Reactive Streams with Elasticsearch**](#reactive-streams-with-elasticsearch)
  - [**Bulk Processor with Spring**](#bulk-processor-with-spring)
    - [**Spring Integration**](#spring-integration)
- [**Best Practices**](#лучшие-практики)
  - [**Indexing Strategies**](#indexing-strategies)
    - [1. **Batch Size Optimization**](#1-batch-size-optimization)
    - [2. **Index Settings for Bulk**](#2-index-settings-for-bulk)
    - [3. **Error Handling**](#3-error-handling)
  - [**Performance Optimization**](#performance-optimization)
    - [1. **Hardware Considerations**](#1-hardware-considerations)
    - [2. **Cluster Sizing**](#2-cluster-sizing)
    - [3. **Monitoring and Alerting**](#3-monitoring-and-alerting)
  - [**Data Management**](#data-management)
    - [1. **Index Lifecycle**](#1-index-lifecycle)
    - [2. **Backup Strategy**](#2-backup-strategy)
    - [3. **Security**](#3-security)
  - [**Operational Excellence**](#operational-excellence)
    - [1. **Automation**](#1-automation)
    - [2. **Incident Response**](#2-incident-response)
    - [3. **Capacity Planning**](#3-capacity-planning)
  - [Ключевые компоненты:](#ключевые-компоненты)
  - [Производительность:](#производительность)
  - [Надежность:](#надежность)
  - [Масштабирование:](#масштабирование)
- [Решение проблем](#решение-проблем)

## **Index API**

### **Single Document Indexing**

#### **Basic Indexing**
```bash
# Автоматическая генерация ID
curl -X POST "localhost:9200/products/_doc" \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Wireless Bluetooth Headphones",
    "description": "High-quality wireless headphones with noise cancellation",
    "price": 199.99,
    "category": "electronics",
    "tags": ["wireless", "bluetooth", "noise-cancelling"],
    "created_at": "2023-01-15T10:30:00Z"
  }'

# Ручное указание ID
curl -X PUT "localhost:9200/products/_doc/1" \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Wireless Bluetooth Headphones",
    "description": "High-quality wireless headphones with noise cancellation",
    "price": 199.99,
    "category": "electronics"
  }'

# Индексация с routing
curl -X PUT "localhost:9200/products/_doc/1?routing=user123" \
  -H "Content-Type: application/json" \
  -d '{"name":"Headphones","user_id":"user123"}'

# Индексация с timeout
curl -X PUT "localhost:9200/products/_doc/1?timeout=10s" \
  -H "Content-Type: application/json" \
  -d '{"name":"Headphones"}'

# Индексация с version control
curl -X PUT "localhost:9200/products/_doc/1?version=1&version_type=external" \
  -H "Content-Type: application/json" \
  -d '{"name":"Headphones"}'
```

#### **Response Analysis**
```json
// Успешный ответ индексации
{
  "_index": "products",
  "_id": "1",
  "_version": 1,
  "result": "created",
  "_shards": {
    "total": 2,
    "successful": 1,
    "failed": 0
  },
  "_seq_no": 0,
  "_primary_term": 1
}

// Ответ при обновлении существующего документа
{
  "_index": "products",
  "_version": 2,
  "result": "updated",
  "_shards": {
    "total": 2,
    "successful": 1,
    "failed": 0
  }
}
```

### **Index Parameters**

#### **Control Parameters**
```bash
# Определение типа операции
curl -X POST "localhost:9200/products/_doc/1?op_type=create" \
  -H "Content-Type: application/json" \
  -d '{"name":"New Product"}'

# Pipeline processing
curl -X POST "localhost:9200/products/_doc/1?pipeline=my_pipeline" \
  -H "Content-Type: application/json" \
  -d '{"name":"Product","description":"Raw description"}'

# Parent-child relationships (legacy)
curl -X PUT "localhost:9200/comments/_doc/1?parent=product123" \
  -H "Content-Type: application/json" \
  -d '{"text":"Great product!","user":"user456"}'

# Wait for active shards
curl -X PUT "localhost:9200/products/_doc/1?wait_for_active_shards=2" \
  -H "Content-Type: application/json" \
  -d '{"name":"Product"}'
```

#### **Ingest Pipelines**
```json
// Создание ingest pipeline
PUT _ingest/pipeline/product_enrichment
{
  "description": "Enrich product documents",
  "processors": [
    {
      "set": {
        "field": "indexed_at",
        "value": "{{_ingest.timestamp}}"
      }
    },
    {
      "lowercase": {
        "field": "category"
      }
    },
    {
      "split": {
        "field": "tags",
        "separator": ","
      }
    },
    {
      "remove": {
        "field": "temp_field"
      }
    }
  ]
}

// Использование pipeline при индексации
POST products/_doc/1?pipeline=product_enrichment
{
  "name": "Wireless Headphones",
  "category": "ELECTRONICS",
  "tags": "wireless,bluetooth,noise-cancelling",
  "temp_field": "remove_me"
}
```

## **Bulk Operations**

### **Bulk API**

#### **Basic Bulk Operations**
```bash
# Bulk индексация
curl -X POST "localhost:9200/_bulk" \
  -H "Content-Type: application/json" \
  -d '
{"index":{"_index":"products","_id":"1"}}
{"name":"Headphones","price":199.99}
{"index":{"_index":"products","_id":"2"}}
{"name":"Keyboard","price":89.99}
{"update":{"_index":"products","_id":"1"}}
{"doc":{"price":179.99}}
{"delete":{"_index":"products","_id":"2"}}
'

# Bulk с routing
curl -X POST "localhost:9200/_bulk?routing=user123" \
  -H "Content-Type: application/json" \
  -d '
{"index":{"_index":"user_products","_routing":"user123","_id":"1"}}
{"name":"Headphones","user_id":"user123"}
{"index":{"_index":"user_products","_routing":"user123","_id":"2"}}
{"name":"Keyboard","user_id":"user123"}
'
```

#### **Bulk Response Analysis**
```json
{
  "took": 7,
  "errors": false,
  "items": [
    {
      "index": {
        "_index": "products",
        "_id": "1",
        "_version": 1,
        "result": "created",
        "_shards": {
          "total": 2,
          "successful": 1,
          "failed": 0
        },
        "_seq_no": 0,
        "_primary_term": 1,
        "status": 201
      }
    },
    {
      "update": {
        "_index": "products",
        "_id": "1",
        "_version": 2,
        "result": "updated",
        "_shards": {
          "total": 2,
          "successful": 1,
          "failed": 0
        },
        "_seq_no": 1,
        "_primary_term": 1,
        "status": 200
      }
    }
  ]
}
```

### **Bulk Size Optimization**

#### **Optimal Bulk Size**
```bash
# Маленькие bulk (неэффективно)
curl -X POST "localhost:9200/_bulk" \
  -H "Content-Type: application/json" \
  -d '
{"index":{"_index":"products","_id":"1"}}
{"name":"Product 1"}
'

# Оптимальный размер bulk (5-15MB)
curl -X POST "localhost:9200/_bulk" \
  -H "Content-Type: application/json" \
  -d '
# ~1000 операций в одном bulk request
# Размер payload: 5-15MB
{"index":{"_index":"products","_id":"1"}}
{"name":"Product 1","description":"Long description..."}
# ... 999 more operations ...
'

# Слишком большой bulk (может вызвать timeout)
curl -X POST "localhost:9200/_bulk" \
  -H "Content-Type: application/json" \
  -d '
# 10000+ операций
# Размер payload: >50MB (проблематично)
'
```

#### **Bulk Performance Tuning**
```json
// Настройки индекса для bulk операций
PUT products/_settings
{
  "index": {
    "refresh_interval": "30s",
    "number_of_replicas": 0,
    "translog": {
      "durability": "async",
      "sync_interval": "5s"
    }
  }
}

// Восстановление настроек после bulk
PUT products/_settings
{
  "index": {
    "refresh_interval": "1s",
    "number_of_replicas": 1,
    "translog": {
      "durability": "request",
      "sync_interval": "5s"
    }
  }
}
```

### **Error Handling** in **Bulk**

#### **Handling Bulk Errors**
```json
// Bulk response с ошибками
{
  "took": 30,
  "errors": true,
  "items": [
    {
      "index": {
        "_index": "products",
        "_id": "1",
        "status": 400,
        "error": {
          "type": "mapper_parsing_exception",
          "reason": "failed to parse field [price] of type [double]",
          "caused_by": {
            "type": "number_format_exception",
            "reason": "For input string: \"invalid_price\""
          }
        }
      }
    },
    {
      "index": {
        "_index": "products",
        "_id": "2",
        "status": 409,
        "error": {
          "type": "version_conflict_engine_exception",
          "reason": "version conflict, current version [3] is different than the one provided [2]"
        }
      }
    }
  ]
}

// Обработка ошибок в приложении
POST _bulk
{ "index": { "_index": "products", "_id": "1" } }
{ "name": "Product", "price": "invalid_price" }
{ "index": { "_index": "products", "_id": "2", "version": 2 } }
{ "name": "Updated Product" }
```

## **Update Operations**

### **Update API**

#### **Scripted Updates**
```bash
# Простое обновление поля
curl -X POST "localhost:9200/products/_update/1" \
  -H "Content-Type: application/json" \
  -d '{
    "doc": {
      "price": 149.99,
      "last_updated": "2023-01-20T10:00:00Z"
    }
  }'

# Условное обновление
curl -X POST "localhost:9200/products/_update/1" \
  -H "Content-Type: application/json" \
  -d '{
    "script": {
      "source": "if (ctx._source.price > 100) { ctx._source.price *= 0.9 }",
      "lang": "painless"
    }
  }'

# Обновление с scripted upsert
curl -X POST "localhost:9200/products/_update/1" \
  -H "Content-Type: application/json" \
  -d '{
    "script": {
      "source": "ctx._source.views += params.increment",
      "lang": "painless",
      "params": {
        "increment": 1
      }
    },
    "upsert": {
      "name": "New Product",
      "views": 1
    }
  }'
```

#### **Advanced Update Scripts**
```bash
# Обновление массивов
curl -X POST "localhost:9200/products/_update/1" \
  -H "Content-Type: application/json" \
  -d '{
    "script": {
      "source": "ctx._source.tags.add(params.newTag)",
      "lang": "painless",
      "params": {
        "newTag": "premium"
      }
    }
  }'

# Условное обновление с несколькими полями
curl -X POST "localhost:9200/products/_update/1" \
  -H "Content-Type: application/json" \
  -d '{
    "script": {
      "source": """
        if (ctx._source.category == 'electronics') {
          ctx._source.discount = params.discount;
          ctx._source.discounted_price = ctx._source.price * (1 - params.discount);
        }
      """,
      "lang": "painless",
      "params": {
        "discount": 0.1
      }
    }
  }'

# Обновление вложенных объектов
curl -X POST "localhost:9200/products/_update/1" \
  -H "Content-Type: application/json" \
  -d '{
    "script": {
      "source": """
        ctx._source.specifications.battery_life = params.newBatteryLife;
        ctx._source.specifications.connectivity.add('USB-C');
      """,
      "lang": "painless",
      "params": {
        "newBatteryLife": "36 hours"
      }
    }
  }'
```

### **Update** by **Query**

#### **Update Multiple Documents**
```bash
# Обновление всех документов по условию
curl -X POST "localhost:9200/products/_update_by_query" \
  -H "Content-Type: application/json" \
  -d '{
    "script": {
      "source": "ctx._source.price *= 1.1",
      "lang": "painless"
    },
    "query": {
      "term": {
        "category": "electronics"
      }
    }
  }'

# Update by query с conflicts handling
curl -X POST "localhost:9200/products/_update_by_query?conflicts=proceed" \
  -H "Content-Type: application/json" \
  -d '{
    "script": {
      "source": "ctx._source.status = 'discontinued'",
      "lang": "painless"
    },
    "query": {
      "range": {
        "stock_quantity": {
          "lte": 0
        }
      }
    }
  }'

# Update by query с batching
curl -X POST "localhost:9200/products/_update_by_query?scroll_size=100" \
  -H "Content-Type: application/json" \
  -d '{
    "script": {
      "source": "ctx._source.last_modified = params.timestamp",
      "lang": "painless",
      "params": {
        "timestamp": "2023-01-20T00:00:00Z"
      }
    },
    "query": {
      "range": {
        "@timestamp": {
          "lt": "2023-01-01"
        }
      }
    }
  }'
```

## **Delete Operations**

### **Delete** by `ID`

#### **Single Document Deletion**
```bash
# Удаление по ID
curl -X DELETE "localhost:9200/products/_doc/1"

# Удаление с routing
curl -X DELETE "localhost:9200/user_products/_doc/1?routing=user123"

# Удаление с version control
curl -X DELETE "localhost:9200/products/_doc/1?version=2"
```

#### **Delete Response**
```json
{
  "_index": "products",
  "_id": "1",
  "_version": 3,
  "result": "deleted",
  "_shards": {
    "total": 2,
    "successful": 1,
    "failed": 0
  },
  "_seq_no": 2,
  "_primary_term": 1
}
```

### **Delete** by **Query**

#### **Delete Multiple Documents**
```bash
# Удаление по условию
curl -X POST "localhost:9200/products/_delete_by_query" \
  -H "Content-Type: application/json" \
  -d '{
    "query": {
      "term": {
        "status": "discontinued"
      }
    }
  }'

# Удаление с conflicts handling
curl -X POST "localhost:9200/products/_delete_by_query?conflicts=proceed" \
  -H "Content-Type: application/json" \
  -d '{
    "query": {
      "range": {
        "created_at": {
          "lt": "2020-01-01"
        }
      }
    }
  }'

# Удаление с batching для больших объемов
curl -X POST "localhost:9200/logs-*/_delete_by_query?scroll_size=5000" \
  -H "Content-Type: application/json" \
  -d '{
    "query": {
      "range": {
        "@timestamp": {
          "lt": "now-90d"
        }
      }
    }
  }'
```

### **Delete Index**

#### **Index Deletion**
```bash
# Удаление индекса
curl -X DELETE "localhost:9200/products"

# Удаление нескольких индексов
curl -X DELETE "localhost:9200/logs-2022-*,logs-2021-*"

# Удаление всех индексов (опасно!)
curl -X DELETE "localhost:9200/_all"

# Удаление с wildcard
curl -X DELETE "localhost:9200/temp-*"
```

## **Routing** и **Versioning**

### **Custom Routing**

#### **Routing Strategies**
```bash
# Routing на основе user_id
curl -X PUT "localhost:9200/user_products/_doc/1?routing=user123" \
  -H "Content-Type: application/json" \
  -d '{
    "user_id": "user123",
    "product_name": "Headphones",
    "price": 199.99
  }'

# Поиск с routing (оптимизация)
curl -X GET "localhost:9200/user_products/_search?routing=user123" \
  -H "Content-Type: application/json" \
  -d '{
    "query": {
      "term": {
        "user_id": "user123"
      }
    }
  }'

# Update с routing
curl -X POST "localhost:9200/user_products/_update/1?routing=user123" \
  -H "Content-Type: application/json" \
  -d '{
    "doc": {
      "price": 179.99
    }
  }'
```

#### **Routing** in **Mapping**
```json
// Настройка routing в mapping
PUT user_products
{
  "mappings": {
    "_routing": {
      "required": true
    },
    "properties": {
      "user_id": {
        "type": "keyword"
      },
      "product_name": {
        "type": "text"
      }
    }
  }
}

// Теперь routing обязателен
POST user_products/_doc/1?routing=user123
{
  "user_id": "user123",
  "product_name": "Headphones"
}
```

### **Version Control**

#### **Internal Versioning**
```bash
# Elasticsearch internal versioning
curl -X PUT "localhost:9200/products/_doc/1" \
  -H "Content-Type: application/json" \
  -d '{"name":"Product","version":1}'

# Update с version check
curl -X PUT "localhost:9200/products/_doc/1?version=1&version_type=internal" \
  -H "Content-Type: application/json" \
  -d '{"name":"Updated Product","version":2}'
```

#### **External Versioning**
```bash
# External version control (application managed)
curl -X PUT "localhost:9200/products/_doc/1?version=100&version_type=external" \
  -H "Content-Type: application/json" \
  -d '{"name":"Product v100"}'

# Update только если external version выше
curl -X PUT "localhost:9200/products/_doc/1?version=101&version_type=external_gt" \
  -H "Content-Type: application/json" \
  -d '{"name":"Product v101"}'
```

#### **Version Conflicts**
```json
// Version conflict response
{
  "error": {
    "type": "version_conflict_engine_exception",
    "reason": "version conflict, current version [5] is different than the one provided [3]",
    "index_uuid": "6T_9IuCwR6qR9gFxLwJ6w",
    "shard": "0",
    "index": "products"
  },
  "status": 409
}
```

## **Refresh** и **Flush**

### **Refresh Operations**

#### **Manual Refresh**
```bash
# Refresh конкретного индекса
curl -X POST "localhost:9200/products/_refresh"

# Refresh всех индексов
curl -X POST "localhost:9200/_refresh"

# Refresh нескольких индексов
curl -X POST "localhost:9200/products,orders/_refresh"
```

#### **Refresh Settings**
```json
// Изменение refresh interval
PUT products/_settings
{
  "index": {
    "refresh_interval": "30s"
  }
}

// Отключение refresh для bulk indexing
PUT products/_settings
{
  "index": {
    "refresh_interval": "-1"
  }
}

// Восстановление refresh
PUT products/_settings
{
  "index": {
    "refresh_interval": "1s"
  }
}
```

### **Flush Operations**

#### **Index Flush**
```bash
# Flush конкретного индекса
curl -X POST "localhost:9200/products/_flush"

# Flush всех индексов
curl -X POST "localhost:9200/_flush"

# Flush с параметрами
curl -X POST "localhost:9200/products/_flush?wait_if_ongoing=true"

# Force flush
curl -X POST "localhost:9200/products/_flush?force=true"
```

#### **Translog Settings**
```json
// Настройки translog
PUT products/_settings
{
  "index": {
    "translog": {
      "durability": "request",
      "sync_interval": "5s",
      "flush_threshold_size": "512mb"
    }
  }
}
```

## **Index Lifecycle Management**

### **ILM Policies**

#### **Creating ILM Policy**
```json
PUT _ilm/policy/logs_policy
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
          "allocate": {
            "number_of_replicas": 1
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
        "min_age": "90d",
        "actions": {
          "allocate": {
            "number_of_replicas": 0
          },
          "freeze": {}
        }
      },
      "delete": {
        "min_age": "1y",
        "actions": {
          "delete": {}
        }
      }
    }
  }
}
```

#### **Applying ILM Policy**
```json
PUT logs-000001
{
  "settings": {
    "index.lifecycle.name": "logs_policy",
    "index.lifecycle.rollover_alias": "logs"
  },
  "aliases": {
    "logs": {
      "is_write_index": true
    }
  }
}
```

### **Index Templates with ILM**

#### **Template with ILM**
```json
PUT _template/logs_template
{
  "index_patterns": ["logs-*"],
  "settings": {
    "number_of_shards": 3,
    "number_of_replicas": 1,
    "index.lifecycle.name": "logs_policy",
    "index.lifecycle.rollover_alias": "logs"
  },
  "mappings": {
    "properties": {
      "@timestamp": {
        "type": "date"
      },
      "level": {
        "type": "keyword"
      },
      "message": {
        "type": "text"
      },
      "service": {
        "type": "keyword"
      }
    }
  },
  "aliases": {
    "logs": {
      "is_write_index": true
    }
  }
}
```

## **Optimistic Concurrency Control**

### **Version-based Updates**

#### **OCC with Versions**
```bash
# Получение текущего документа с version
curl -X GET "localhost:9200/products/_doc/1"

# Update с version check
curl -X PUT "localhost:9200/products/_doc/1?version=5" \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Updated Product",
    "version": 6
  }'

# Update с external version
curl -X PUT "localhost:9200/products/_doc/1?version=100&version_type=external" \
  -H "Content-Type: application/json" \
  -d '{"name":"Product v100"}'
```

#### **Sequence Numbers and Primary Terms**
```bash
# Update с seq_no и primary_term
curl -X PUT "localhost:9200/products/_doc/1?if_seq_no=10&if_primary_term=2" \
  -H "Content-Type: application/json" \
  -d '{"name":"Updated Product"}'

# Получение seq_no и primary_term
curl -X GET "localhost:9200/products/_doc/1"
# Response includes _seq_no and _primary_term
```

### **Retry Logic**

#### **Implementing Retry** on **Conflicts**
```java
@Service
public class OptimisticLockingService {

    @Autowired
    private ElasticsearchClient client;

    public void updateProductWithRetry(String productId, Function<Product, Product> updater) {
        int maxRetries = 3;
        int retryCount = 0;

        while (retryCount < maxRetries) {
            try {
                // Получение текущего документа
                GetResponse<Product> getResponse = client.get(g -> g
                    .index("products")
                    .id(productId), Product.class);

                if (!getResponse.found()) {
                    throw new ProductNotFoundException("Product not found: " + productId);
                }

                Product currentProduct = getResponse.source();
                Product updatedProduct = updater.apply(currentProduct);

                // Update с version control
                UpdateRequest<Product, Product> updateRequest = UpdateRequest.of(u -> u
                    .index("products")
                    .id(productId)
                    .doc(updatedProduct)
                    .ifSeqNo(getResponse.seqNo())
                    .ifPrimaryTerm(getResponse.primaryTerm()));

                UpdateResponse<Product> updateResponse = client.update(updateRequest, Product.class);

                // Успешное обновление
                return;

            } catch (ElasticsearchException e) {
                if (e.getMessage().contains("version_conflict_engine_exception")) {
                    retryCount++;
                    if (retryCount >= maxRetries) {
                        throw new ConcurrentModificationException(
                            "Failed to update product after " + maxRetries + " retries", e);
                    }

                    // Exponential backoff
                    try {
                        Thread.sleep(100 * (1 << retryCount));
                    } catch (InterruptedException ie) {
                        Thread.currentThread().interrupt();
                        throw new RuntimeException("Interrupted during retry", ie);
                    }
                } else {
                    throw e;
                }
            }
        }
    }

    // Пример использования
    public void updateProductPrice(String productId, BigDecimal priceIncrease) {
        updateProductWithRetry(productId, product -> {
            Product updated = new Product(product);
            updated.setPrice(product.getPrice().add(priceIncrease));
            updated.setLastModified(Instant.now());
            return updated;
        });
    }
}
```

## **Indexing Performance Tuning**

### **Index Settings Optimization**

#### **Bulk Indexing Settings**
```json
PUT bulk_index/_settings
{
  "index": {
    "refresh_interval": "30s",
    "number_of_replicas": 0,
    "translog": {
      "durability": "async",
      "sync_interval": "120s",
      "flush_threshold_size": "1gb"
    },
    "merge": {
      "scheduler": {
        "max_thread_count": 1
      },
      "policy": {
        "max_merged_segment": "1gb"
      }
    }
  }
}
```

#### **Thread Pool Settings**
```json
PUT _cluster/settings
{
  "persistent": {
    "thread_pool": {
      "bulk": {
        "size": 8,
        "queue_size": 200
      },
      "index": {
        "size": 8,
        "queue_size": 200
      }
    }
  }
}
```

### **Hardware Optimization**

#### **Disk** I/O **Tuning**
```bash
# Для SSD дисков
echo "deadline" > /sys/block/sda/queue/scheduler
echo 0 > /sys/block/sda/queue/rotational
echo 0 > /sys/block/sda/queue/nomerges

# I/O tuning
echo 1024 > /sys/block/sda/queue/nr_requests
echo 256 > /sys/block/sda/queue/read_ahead_kb

# Disable swap (важно для Elasticsearch)
swapoff -a
echo "vm.swappiness = 1" >> /etc/sysctl.conf
```

#### **Memory Tuning**
```bash
# Huge pages
echo 14336 > /proc/sys/vm/nr_hugepages
echo "vm.nr_hugepages = 14336" >> /etc/sysctl.conf

# Memory settings
echo "vm.max_map_count = 262144" >> /etc/sysctl.conf
echo "vm.dirty_ratio = 30" >> /etc/sysctl.conf
echo "vm.dirty_background_ratio = 5" >> /etc/sysctl.conf

sysctl -p
```

### **JVM Tuning for Indexing**

#### `GC` **Tuning**
```bash
# JVM options for indexing performance
JVM_OPTS="$JVM_OPTS -XX:+UseG1GC"
JVM_OPTS="$JVM_OPTS -XX:G1ReservePercent=25"
JVM_OPTS="$JVM_OPTS -XX:InitiatingHeapOccupancyPercent=30"
JVM_OPTS="$JVM_OPTS -XX:MaxGCPauseMillis=200"
JVM_OPTS="$JVM_OPTS -XX:G1HeapRegionSize=16m"

# Memory settings
JVM_OPTS="$JVM_OPTS -Xms16g"
JVM_OPTS="$JVM_OPTS -Xmx16g"
JVM_OPTS="$JVM_OPTS -XX:+UseLargePages"

# GC logging
JVM_OPTS="$JVM_OPTS -Xlog:gc*=info:file=/var/log/elasticsearch/gc.log:time:filecount=10,filesize=100m"
```

## **Error Handling**

### **Common Indexing Errors**

#### **Mapper Parsing Exceptions**
```json
// Типичная ошибка mapping
{
  "error": {
    "type": "mapper_parsing_exception",
    "reason": "failed to parse field [price] of type [double] in document with id '1'",
    "caused_by": {
      "type": "number_format_exception",
      "reason": "For input string: \"not_a_number\""
    }
  }
}

// Решение: валидация данных перед индексацией
public class DataValidator {

    public ValidationResult validateProduct(Product product) {
        ValidationResult result = new ValidationResult();

        // Валидация цены
        if (product.getPrice() != null && product.getPrice().compareTo(BigDecimal.ZERO) < 0) {
            result.addError("Price cannot be negative");
        }

        // Валидация имени
        if (product.getName() == null || product.getName().trim().isEmpty()) {
            result.addError("Product name is required");
        }

        // Валидация категории
        if (product.getCategory() != null && !isValidCategory(product.getCategory())) {
            result.addWarning("Unknown category: " + product.getCategory());
        }

        return result;
    }
}
```

#### **Circuit Breaker Exceptions**
```json
// Circuit breaker tripped
{
  "error": {
    "type": "circuit_breaking_exception",
    "reason": "Data too large, data for [indices:data/write/bulk] would be larger than limit of [128848896/122.9mb]"
  }
}

// Решение: уменьшить bulk size
@Configuration
public class ElasticsearchBulkConfig {

    @Bean
    public BulkProcessor bulkProcessor(RestHighLevelClient client) {
        BulkProcessor.Listener listener = new BulkProcessor.Listener() {
            @Override
            public void beforeBulk(long executionId, BulkRequest request) {
                // Логирование размера bulk
                long sizeInBytes = calculateBulkSize(request);
                if (sizeInBytes > 50 * 1024 * 1024) { // 50MB
                    log.warn("Large bulk request: {} MB", sizeInBytes / (1024 * 1024));
                }
            }

            @Override
            public void afterBulk(long executionId, BulkRequest request, BulkResponse response) {
                if (response.hasFailures()) {
                    log.error("Bulk request {} failed with {} failures",
                             executionId, response.getItems().length);
                }
            }

            @Override
            public void afterBulk(long executionId, BulkRequest request, Throwable failure) {
                log.error("Bulk request {} failed", executionId, failure);
            }
        };

        return BulkProcessor.builder(client::bulkAsync, listener)
            .setBulkActions(1000)  // Выполнять bulk после 1000 операций
            .setBulkSize(new ByteSizeValue(5, ByteSizeUnit.MB))  // Или после 5MB
            .setFlushInterval(TimeValue.timeValueSeconds(5))  // Или каждые 5 секунд
            .setConcurrentRequests(1)  // Одиночный поток для упрощения
            .setBackoffPolicy(BackoffPolicy.exponentialBackoff(TimeValue.timeValueMillis(100), 3))
            .build();
    }

    private long calculateBulkSize(BulkRequest request) {
        // Примерная оценка размера
        return request.numberOfActions() * 1000; // ~1KB per action
    }
}
```

### **Handling Failures**

#### **Retry Logic for Bulk Operations**
```java
@Service
public class BulkIndexingService {

    @Autowired
    private BulkProcessor bulkProcessor;

    @Autowired
    private DeadLetterQueue deadLetterQueue;

    public void indexDocuments(List<Document> documents) {
        for (Document doc : documents) {
            try {
                IndexRequest request = new IndexRequest("products")
                    .id(doc.getId())
                    .source(doc.getSource());

                bulkProcessor.add(request);

            } catch (Exception e) {
                log.error("Failed to add document {} to bulk", doc.getId(), e);

                // Отправка в dead letter queue
                deadLetterQueue.send(doc, e.getMessage());
            }
        }
    }

    public void flushAndWait() {
        try {
            bulkProcessor.flush();
            // Ожидание завершения всех операций
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            log.warn("Interrupted while waiting for bulk flush");
        }
    }

    // Dead letter queue для неудачных операций
    public static class DeadLetterQueue {

        private final Queue<FailedDocument> queue = new ConcurrentLinkedQueue<>();

        public void send(Document doc, String error) {
            queue.add(new FailedDocument(doc, error, Instant.now()));
        }

        public List<FailedDocument> drain(int maxItems) {
            List<FailedDocument> drained = new ArrayList<>();
            FailedDocument item;

            while (drained.size() < maxItems && (item = queue.poll()) != null) {
                drained.add(item);
            }

            return drained;
        }

        // Периодическая обработка failed documents
        @Scheduled(fixedRate = 300000) // Каждые 5 минут
        public void processDeadLetters() {
            List<FailedDocument> failedDocs = drain(100);

            for (FailedDocument failed : failedDocs) {
                if (shouldRetry(failed)) {
                    // Повторная попытка индексации
                    retryIndexing(failed.getDocument());
                } else {
                    // Логирование permanent failure
                    log.error("Permanent indexing failure for document {}: {}",
                             failed.getDocument().getId(), failed.getError());
                }
            }
        }

        private boolean shouldRetry(FailedDocument failed) {
            // Retry logic: не чаще чем раз в час, не более 5 попыток
            return failed.getRetryCount() < 5 &&
                   Duration.between(failed.getFailedAt(), Instant.now()).toHours() >= 1;
        }
    }

    static class FailedDocument {
        private final Document document;
        private final String error;
        private final Instant failedAt;
        private int retryCount;

        // constructor, getters, setters
    }
}
```

## **Java Bulk Processing**

### **BulkProcessor Configuration**

#### **Modern Java API Bulk Processing**
```java
@Configuration
public class ElasticsearchBulkConfiguration {

    @Bean
    public BulkProcessor bulkProcessor(ElasticsearchClient client) {
        BulkListener listener = new BulkListener() {
            @Override
            public void beforeBulk(long executionId, BulkRequest request) {
                log.debug("Executing bulk request {} with {} operations",
                         executionId, request.operations().size());
            }

            @Override
            public void afterBulk(long executionId, BulkRequest request, BulkResponse response) {
                if (response.errors()) {
                    log.warn("Bulk request {} completed with errors", executionId);

                    for (BulkResponseItem item : response.items()) {
                        if (item.error() != null) {
                            log.error("Failed operation: {}", item.error().reason());
                        }
                    }
                } else {
                    log.debug("Bulk request {} completed successfully", executionId);
                }
            }

            @Override
            public void afterBulk(long executionId, BulkRequest request, Throwable failure) {
                log.error("Bulk request {} failed", executionId, failure);
            }
        };

        return BulkProcessor.builder(client::bulk, listener)
            .setBulkActions(1000)  // Execute after 1000 operations
            .setBulkSize(ByteSizeValue.of(5, ByteSizeUnit.MB))  // Or after 5MB
            .setFlushInterval(TimeValue.timeValueSeconds(10))  // Or every 10 seconds
            .build();
    }
}

@Service
public class BulkIndexingService {

    @Autowired
    private BulkProcessor bulkProcessor;

    @Autowired
    private ObjectMapper objectMapper;

    public void indexProductsBulk(List<Product> products) {
        for (Product product : products) {
            try {
                BulkOperation operation = BulkOperation.of(o -> o
                    .index(i -> i
                        .document(product)
                        .index("products")
                        .id(product.getId())
                    ));

                bulkProcessor.add(operation);

            } catch (Exception e) {
                log.error("Failed to add product {} to bulk", product.getId(), e);
            }
        }
    }

    public void indexRawDocuments(List<Map<String, Object>> documents, String indexName) {
        for (Map<String, Object> document : documents) {
            BulkOperation operation = BulkOperation.of(o -> o
                .index(i -> i
                    .document(document)
                    .index(indexName)
                ));

            bulkProcessor.add(operation);
        }
    }

    public CompletableFuture<Void> flushAsync() {
        return bulkProcessor.flush();
    }

    public void close() throws Exception {
        bulkProcessor.close();
    }
}
```

### **Reactive Bulk Processing**

#### **Reactive Streams with Elasticsearch**
```java
@Service
public class ReactiveBulkIndexingService {

    @Autowired
    private ElasticsearchAsyncClient asyncClient;

    public Flux<BulkResponseItem> indexProductsReactive(List<Product> products) {
        return Flux.fromIterable(products)
            .map(this::createBulkOperation)
            .buffer(1000)  // Batch into groups of 1000
            .flatMap(this::executeBulkBatch)
            .flatMapIterable(BulkResponse::items);
    }

    private BulkOperation createBulkOperation(Product product) {
        return BulkOperation.of(o -> o
            .index(i -> i
                .document(product)
                .index("products")
                .id(product.getId())
            ));
    }

    private Mono<BulkResponse> executeBulkBatch(List<BulkOperation> operations) {
        BulkRequest bulkRequest = BulkRequest.of(b -> b
            .operations(operations)
        );

        return Mono.fromFuture(asyncClient.bulk(bulkRequest));
    }

    // Использование
    public void indexLargeDataset() {
        List<Product> allProducts = loadAllProductsFromDatabase();

        indexProductsReactive(allProducts)
            .doOnNext(item -> {
                if (item.error() != null) {
                    log.error("Failed to index product: {}", item.error().reason());
                }
            })
            .doOnComplete(() -> log.info("Bulk indexing completed"))
            .doOnError(error -> log.error("Bulk indexing failed", error))
            .subscribe();
    }
}
```

### **Bulk Processor with Spring**

#### **Spring Integration**
```java
@Configuration
@EnableScheduling
public class BulkIndexingConfiguration {

    @Autowired
    private BulkProcessor bulkProcessor;

    @Autowired
    private ProductRepository productRepository;

    @Bean
    public BulkProcessor bulkProcessor(ElasticsearchClient client) {
        BulkListener listener = new BulkListener() {
            @Override
            public void beforeBulk(long executionId, BulkRequest request) {
                Metrics.counter("elasticsearch.bulk.requests").increment();
                Metrics.timer("elasticsearch.bulk.execution").recordCallable(() -> {
                    // Actual bulk execution is handled by the processor
                    return null;
                });
            }

            @Override
            public void afterBulk(long executionId, BulkRequest request, BulkResponse response) {
                Metrics.counter("elasticsearch.bulk.completed").increment();

                if (response.errors()) {
                    Metrics.counter("elasticsearch.bulk.errors").increment(response.items().length);
                }
            }

            @Override
            public void afterBulk(long executionId, BulkRequest request, Throwable failure) {
                Metrics.counter("elasticsearch.bulk.failures").increment();
            }
        };

        return BulkProcessor.builder(client::bulk, listener)
            .setBulkActions(500)
            .setBulkSize(ByteSizeValue.of(2, ByteSizeUnit.MB))
            .setFlushInterval(TimeValue.timeValueSeconds(5))
            .setRetryPolicy(new BulkProcessorRetryPolicy())
            .build();
    }

    // Периодическая обработка накопленных документов
    @Scheduled(fixedRate = 30000) // Каждые 30 секунд
    public void processPendingDocuments() {
        // Получение документов из очереди/буфера
        List<Product> pendingProducts = getPendingProducts();

        if (!pendingProducts.isEmpty()) {
            for (Product product : pendingProducts) {
                BulkOperation operation = BulkOperation.of(o -> o
                    .index(i -> i
                        .document(product)
                        .index("products")
                    ));

                bulkProcessor.add(operation);
            }

            // Очистка обработанных документов
            markProductsAsProcessed(pendingProducts);
        }
    }

    // Graceful shutdown
    @PreDestroy
    public void shutdown() {
        try {
            log.info("Shutting down bulk processor...");
            bulkProcessor.close();
        } catch (Exception e) {
            log.error("Error shutting down bulk processor", e);
        }
    }

    // Вспомогательные методы для индексации
    private List<Product> getPendingProducts() {
        // Implementation to get pending products
        return new ArrayList<>();
    }

    private void markProductsAsProcessed(List<Product> products) {
        // Implementation to mark products as processed
    }
}

class BulkProcessorRetryPolicy implements RetryPolicy {

    @Override
    public boolean shouldRetry(Exception exception) {
        // Retry on network errors, but not on validation errors
        return isRetryableException(exception);
    }

    @Override
    public long calculateDelay(int retryCount) {
        // Exponential backoff: 100ms, 200ms, 400ms, etc.
        return 100 * (1L << Math.min(retryCount, 6));
    }

    private boolean isRetryableException(Exception exception) {
        return exception instanceof IOException ||
               exception.getMessage().contains("timeout") ||
               exception.getMessage().contains("circuit_breaking_exception");
    }
}
```

## Лучшие практики

### **Indexing Strategies**

#### 1. **Batch Size Optimization**
- **Start small**: 100-500 документов на **bulk**
- **Monitor performance**: Измеряй **throughput** и **latency**
- **Scale up gradually**: Увеличивай **batch size** до оптимального
- **Consider memory**: Не превышай **heap limits**

#### 2. **Index Settings for Bulk**
- **Disable replicas**: Установи 0 реплик на время **bulk**
- **Increase refresh interval**: 30s-60s вместо 1s
- **Async translog**: Для лучшей производительности
- **Monitor JVM**: Следи за **heap usage**

#### 3. **Error Handling**
- **Validate data**: Перед индексацией
- **Implement retries**: Для **transient** ошибок
- **Use dead letter queues**: Для **permanent failures**
- **Monitor error rates**: **Alert** при высоком проценте ошибок

### **Performance Optimization**

#### 1. **Hardware Considerations**
- **SSD storage**: Обязательно для **production**
- **Network**: 10Gbps минимум для **data nodes**
- **CPU**: 8+ **cores** для индексации
- **Memory**: 64GB+ для крупных кластеров

#### 2. **Cluster Sizing**
- **Data nodes**: Минимум 3 для **redundancy**
- **Master nodes**: Выдели **dedicated masters** для крупных кластеров
- **Hot-warm architecture**: Для **time-series** данных
- **Zone awareness**: Для **multi-zone deployments**

#### 3. **Monitoring and Alerting**
- **Index rate**: Документов в секунду
- **Query latency**: **P50**, **P95**, **P99**
- **Disk usage**: Свободное место
- **JVM metrics**: **Heap usage**, `GC` **pauses**

### **Data Management**

#### 1. **Index Lifecycle**
- **Hot phase**: Активная индексация
- **Warm phase**: **Read-only** с меньшими ресурсами
- **Cold phase**: Архивные данные
- **Delete phase**: Автоматическая очистка

#### 2. **Backup Strategy**
- **Snapshot repositories**: **S3**, **NFS**, или **shared storage**
- **Regular snapshots**: Ежедневно/еженедельно
- **Test restores**: Регулярная проверка восстановления
- **Retention policies**: Управление временем хранения

#### 3. **Security**
- **TLS encryption**: Для всех соединений
- **Authentication**: **Basic auth** или интеграция с **LDAP**/`AD`
- **Authorization**: **Role-based access control**
- **Audit logging**: Мониторинг всех операций

### **Operational Excellence**

#### 1. **Automation**
- **Infrastructure as Code**: **Terraform**/**Ansible** для кластера
- **Configuration management**: **Ansible** для конфигураций
- **CI/`CD` pipelines**: Для развертывания приложений
- **Monitoring as Code**: **Prometheus**/**Grafana** конфигурации

#### 2. **Incident Response**
- **Runbooks**: Документированные процедуры
- **Escalation paths**: Кто и когда уведомляется
- **Post-mortem analysis**: Уроки из инцидентов
- **Blame-free culture**: Фокус на решении проблем

#### 3. **Capacity Planning**
- **Growth projections**: На 6-12 месяцев вперед
- **Performance benchmarks**: Регулярные тесты
- **Cost optimization**: **Balance** между производительностью и затратами
- **Disaster recovery**: Тестирование планов восстановления

**Индексация документов — фундаментальная операция в **Elasticsearch**, определяющая производительность и надежность всего кластера. Правильная стратегия индексации включает:**

### Ключевые компоненты:

1. **Bulk API** — Эффективная индексация больших объемов данных
2. **Update operations** — Поддержка изменений с **optimistic locking**
3. **Routing и versioning** — Контроль распределения и конфликтов
4. **Refresh/Flush** — Управление **visibility** и **durability**

### Производительность:

1. **Batch optimization** — Правильный размер **bulk** операций
2. **Hardware tuning** — **SSD**, сеть, **CPU**, память
3. **JVM optimization** — `GC` **tuning** и **memory management**
4. **Index settings** — **Refresh interval**, **replicas**, **translog**

### Надежность:

1. **Error handling** — **Retry logic** и **dead letter queues**
2. **Version control** — **Optimistic concurrency control**
3. **Circuit breakers** — Защита от перегрузок
4. **Monitoring** — Метрики и **alerting**

### **Best practices**:

1. **Data validation** — Перед индексацией
2. **Bulk processing** — Эффективные **Java** клиенты
3. **Index lifecycle** — Управление жизненным циклом
4. **Security** — Аутентификация и авторизация

### Масштабирование:

1. **Horizontal scaling** — Добавление узлов
2. **Index optimization** — **Sharding** и **routing**
3. **Hardware upgrades** — **Vertical scaling**
4. **Architecture patterns** — **Hot-warm-cold**

Оптимальная стратегия индексации обеспечивает высокую производительность, надежность и масштабируемость **Elasticsearch** кластера. Регулярный мониторинг и тюнинг — ключ к поддержанию эффективной работы системы. 🎯

**Продолжение следует:**
- ✅ **elasticsearch-basics.md** (**завершен**)
- ✅ **elasticsearch-indexing.md** (**завершен**)
- 🔄 **elasticsearch-queries.md**
- 🔄 **elasticsearch-aggregations.md**
- 🔄 **elasticsearch-clustering.md**
- 🔄 **elasticsearch-performance.md**

## Решение проблем

**Медленная индексация:** увеличьте `refresh_interval`, отключайте рефреш при массовой загрузке и вызывайте `_refresh` вручную после. Используйте bulk-запросы с разумным размером батча (порядка 1000–5000 документов). Проверьте нагрузку на диск и CPU.

**Ошибки маппинга (mapper_parsing_exception):** проверьте типы полей в документе и маппинг индекса. При конфликте типов переиндексируйте в новый индекс с правильным маппингом. Используйте `dynamic: false` или `strict` для контроля новых полей.

**Жёлтый/красный статус индекса:** при нехватке реплик проверьте количество узлов и настройки `number_of_replicas`. При нехватке места освободите диск, удалите старые индексы или увеличьте кластер. Восстановите реплики после восстановления узлов.

**Высокое использование памяти при индексации:** уменьшите размер bulk-батча, увеличьте heap для узлов данных. Проверьте размер полей и отключите индексацию для полей, по которым не ищут (`index: false`).

Следующий файл - **elasticsearch-queries.md**! 🚀


