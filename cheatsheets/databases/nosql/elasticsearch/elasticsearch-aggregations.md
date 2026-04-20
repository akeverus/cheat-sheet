---
title: "Elasticsearch: Агрегации - Аналитика и статистика данных"
description: "Комплексное руководство по агрегациям Elasticsearch: от базовых метрик до сложных вложенных агрегаций, pipeline aggregations и аналитики в реальном времени."
tags:
  - databases
  - nosql
  - elasticsearch-aggregations
difficulty: "intermediate"
prerequisites: []
next: []
updated: "2026-02-11"
---
# Elasticsearch: Агрегации — Аналитика и статистика данных

Комплексное руководство по агрегациям **Elasticsearch**: от базовых метрик до сложных вложенных агрегаций, **pipeline aggregations** и аналитики в реальном времени.

## Полезные ссылки

### Официальная документация
- [Aggregations](https://www.elastic.co/guide/en/elasticsearch/reference/current/search-aggregations.html)
- [Metrics Aggregations](https://www.elastic.co/guide/en/elasticsearch/reference/current/search-aggregations-metrics.html)
- [Bucket Aggregations](https://www.elastic.co/guide/en/elasticsearch/reference/current/search-aggregations-bucket.html)
- [Pipeline Aggregations](https://www.elastic.co/guide/en/elasticsearch/reference/current/search-aggregations-pipeline.html)

### Дизайн и оптимизация
- [Aggregation Performance](https://www.elastic.co/guide/en/elasticsearch/reference/current/tune-for-search-speed.html)
- [Data Visualization](https://www.elastic.co/guide/en/kibana/current/visualize.html)
- [Real-time Analytics](https://www.elastic.co/guide/en/elasticsearch/reference/current/transform.html)

### Инструменты
- [Kibana Lens](https://www.elastic.co/guide/en/kibana/current/lens.html) — **Drag-and-drop** визуализация
- [Elasticsearch SQL](https://www.elastic.co/guide/en/elasticsearch/reference/current/xpack-sql.html)
- [Aggregation Profiler](https://www.elastic.co/guide/en/elasticsearch/reference/current/search-profile.html)

### См. также
- [[elasticsearch-basics|Основы]] — **Elasticsearch**
- [[elasticsearch-queries|Запросы]] — поиск и запросы
- [[elasticsearch-clustering|Кластеризация]] — кластеризация
- [[elasticsearch-performance|Производительность]] — производительность

## Содержание

- [Основы агрегаций](#основы-агрегаций)
  - [Структура запроса с агрегациями](#структура-запроса-с-агрегациями)
    - [Basic Aggregation Query](#basic-aggregation-query)
- [Простая агрегация](#простая-агрегация)
- [Агрегация с фильтром](#агрегация-с-фильтром)
    - [Response Structure](#response-structure)
  - [Aggregation Types](#aggregation-types)
    - [Four Main Categories](#four-main-categories)
- [Metrics Aggregations](#metrics-aggregations)
  - [Single-value Metrics](#single-value-metrics)
    - [Basic Metrics](#basic-metrics)
- [Среднее значение](#среднее-значение)
- [Сумма](#сумма)
- [Минимум/Максимум](#минимуммаксимум)
- [Количество значений](#количество-значений)
    - [Cardinality (уникальные значения)](#cardinality-уникальные-значения)
- [Количество уникальных значений](#количество-уникальных-значений)
- [Cardinality с высокой точностью](#cardinality-с-высокой-точностью)
  - [Multi-value Metrics](#multi-value-metrics)
    - [Stats Aggregation](#stats-aggregation)
- [Полная статистика](#полная-статистика)
- [Response](#response)
    - [Extended Stats](#extended-stats)
- [Расширенная статистика](#расширенная-статистика)
- [Response включает стандартное отклонение, дисперсию, etc.](#response-включает-стандартное-отклонение-дисперсию-etc)
    - [Percentiles](#percentiles)
- [Перцентили](#перцентили)
- [Percentile ranks](#percentile-ranks)
- [Bucket Aggregations](#bucket-aggregations)
  - [Terms Aggregation](#terms-aggregation)
    - [Basic Terms](#basic-terms)
- [Группировка по категориям](#группировка-по-категориям)
- [С минимальным количеством документов](#с-минимальным-количеством-документов)
    - [Terms with Sub-aggregations](#terms-with-sub-aggregations)
- [Terms с подагрегациями](#terms-с-подагрегациями)
  - [Range Aggregations](#range-aggregations)
    - [Numeric Ranges](#numeric-ranges)
- [Диапазоны цен](#диапазоны-цен)
- [Диапазоны с подагрегациями](#диапазоны-с-подагрегациями)
    - [Date Ranges](#date-ranges)
- [Диапазоны дат](#диапазоны-дат)
  - [Histogram Aggregations](#histogram-aggregations)
    - [Numeric Histogram](#numeric-histogram)
- [Гистограмма цен](#гистограмма-цен)
- [Гистограмма с extended bounds](#гистограмма-с-extended-bounds)
- [Date Histogram](#date-histogram)
  - [Time-based Bucketing](#time-based-bucketing)
    - [Basic Date Histogram](#basic-date-histogram)
- [Гистограмма по дням](#гистограмма-по-дням)
- [Гистограмма по часам с часовым поясом](#гистограмма-по-часам-с-часовым-поясом)
    - [Date Histogram with Sub-aggregations](#date-histogram-with-sub-aggregations)
- [Date histogram с метриками](#date-histogram-с-метриками)
  - [Advanced Date Operations](#advanced-date-operations)
    - [Fixed vs Calendar Intervals](#fixed-vs-calendar-intervals)
- [Calendar intervals (рекомендуется)](#calendar-intervals-рекомендуется)
- [Fixed intervals (для равномерных интервалов)](#fixed-intervals-для-равномерных-интервалов)
- [Pipeline Aggregations](#pipeline-aggregations)
  - [Parent Pipeline Aggregations](#parent-pipeline-aggregations)
    - [Derivative](#derivative)
- [Производная (скорость изменения)](#производная-скорость-изменения)
    - [Cumulative Sum](#cumulative-sum)
- [Кумулятивная сумма](#кумулятивная-сумма)
    - [Moving Average](#moving-average)
- [Скользящее среднее](#скользящее-среднее)
  - [Sibling Pipeline Aggregations](#sibling-pipeline-aggregations)
    - [Max/Min Bucket](#maxmin-bucket)
- [Максимальное значение среди buckets](#максимальное-значение-среди-buckets)
    - [Bucket Script](#bucket-script)
- [Арифметические операции над buckets](#арифметические-операции-над-buckets)
- [Matrix Aggregations](#matrix-aggregations)
  - [Matrix Stats](#matrix-stats)
    - [Multi-field Statistics](#multi-field-statistics)
- [Статистика по нескольким полям](#статистика-по-нескольким-полям)
- [Response включает корреляции между полями](#response-включает-корреляции-между-полями)
- [Nested Aggregations](#nested-aggregations)
  - [Nested Objects](#nested-objects)
    - [Nested Aggregation](#nested-aggregation)
- [Агрегации по nested объектам](#агрегации-по-nested-объектам)
    - [Reverse Nested](#reverse-nested)
- [Обратный nested для доступа к parent документу](#обратный-nested-для-доступа-к-parent-документу)
- [Geo Aggregations](#geo-aggregations)
  - [Geo Bounds](#geo-bounds)
    - [Geographic Bounding Box](#geographic-bounding-box)
- [Границы географических данных](#границы-географических-данных)
  - [Geo Centroid](#geo-centroid)
    - [Geographic Center](#geographic-center)
- [Географический центр](#географический-центр)
- [Significant Terms](#significant-terms)
  - [Significant Terms Aggregation](#significant-terms-aggregation)
    - [Finding Significant Correlations](#finding-significant-correlations)
- [Значимые термины](#значимые-термины)
- [Significant terms по сравнению с background](#significant-terms-по-сравнению-с-background)
- [Sampler Aggregation](#sampler-aggregation)
  - [Document Sampling](#document-sampling)
    - [Random Sampling](#random-sampling)
- [Сэмплирование для производительности](#сэмплирование-для-производительности)
    - [Diversified Sampling](#diversified-sampling)
- [Разнообразное сэмплирование](#разнообразное-сэмплирование)
- [Real-time Analytics](#real-time-analytics)
  - [Time Series Analytics](#time-series-analytics)
    - [Rolling Time Windows](#rolling-time-windows)
- [Скользящие временные окна](#скользящие-временные-окна)
    - [Real-time Dashboards](#real-time-dashboards)
- [Агрегации для dashboard](#агрегации-для-dashboard)
  - [Anomaly Detection](#anomaly-detection)
    - [Statistical Anomaly Detection](#statistical-anomaly-detection)
- [Обнаружение аномалий с помощью статистических методов](#обнаружение-аномалий-с-помощью-статистических-методов)
- [Java Aggregations API](#java-aggregations-api)
  - [High Level REST Client](#high-level-rest-client)
    - [Building Aggregations](#building-aggregations)
  - [Java API Client 8.x](#java-api-client-8x)
    - [Modern Aggregation API](#modern-aggregation-api)
- [Performance Optimization](#performance-optimization)
  - [Aggregation Performance Tips](#aggregation-performance-tips)
    - [1. Limit Aggregation Scope](#1-limit-aggregation-scope)
- [Используй фильтры для ограничения данных](#используй-фильтры-для-ограничения-данных)
    - [2. Optimize Terms Aggregation](#2-optimize-terms-aggregation)
- [Ограничь размер terms aggregation](#ограничь-размер-terms-aggregation)
    - [3. Use Approximate Aggregations](#3-use-approximate-aggregations)
- [Cardinality с precision threshold](#cardinality-с-precision-threshold)
- [Percentiles с compression](#percentiles-с-compression)
  - [Caching and Memory Management](#caching-and-memory-management)
    - [Aggregation Caching](#aggregation-caching)
- [Field data cache](#field-data-cache)
- [Shard request cache](#shard-request-cache)
    - [Memory-conscious Aggregations](#memory-conscious-aggregations)
- [Используй circuit breaker для предотвращения OOM](#используй-circuit-breaker-для-предотвращения-oom)
- [Best Practices](#лучшие-практики)
  - [Aggregation Design](#aggregation-design)
    - [1. Choose Right Aggregation Type](#1-choose-right-aggregation-type)
    - [2. Optimize for Performance](#2-optimize-for-performance)
    - [3. Handle Large Datasets](#3-handle-large-datasets)
  - [Common Patterns](#common-patterns)
    - [1. Dashboard Analytics](#1-dashboard-analytics)
- [Комплексная dashboard агрегация](#комплексная-dashboard-агрегация)
    - [2. E-commerce Analytics](#2-e-commerce-analytics)
- [Аналитика продаж](#аналитика-продаж)
    - [3. Log Analytics](#3-log-analytics)
- [Анализ логов](#анализ-логов)
  - [Error Handling](#error-handling)
    - [1. Handle Aggregation Errors](#1-handle-aggregation-errors)
    - [2. Validation and Testing](#2-validation-and-testing)
  - [Scaling Considerations](#scaling-considerations)
    - [1. Large Scale Aggregations](#1-large-scale-aggregations)
    - [2. Real-time Requirements](#2-real-time-requirements)
  - [Ключевые возможности:](#ключевые-возможности)
  - [Архитектурные принципы:](#архитектурные-принципы)
  - [Производительность:](#производительность)
  - [Java интеграция:](#java-интеграция)
  - [Типичные сценарии:](#типичные-сценарии)
- [Решение проблем](#решение-проблем)

## Основы агрегаций

### Структура запроса с агрегациями

#### Basic Aggregation Query
```bash
# Простая агрегация
curl -X GET "localhost:9200/products/_search" \
  -H "Content-Type: application/json" \
  -d '{
    "size": 0,
    "aggs": {
      "avg_price": {
        "avg": {
          "field": "price"
        }
      }
    }
  }'

# Агрегация с фильтром
curl -X GET "localhost:9200/products/_search" \
  -H "Content-Type: application/json" \
  -d '{
    "query": {
      "term": {
        "category": "electronics"
      }
    },
    "size": 0,
    "aggs": {
      "price_stats": {
        "stats": {
          "field": "price"
        }
      }
    }
  }'
```

#### Response Structure
```json
{
  "took": 15,
  "timed_out": false,
  "_shards": {
    "total": 5,
    "successful": 5,
    "skipped": 0,
    "failed": 0
  },
  "hits": {
    "total": {
      "value": 10000,
      "relation": "gte"
    },
    "max_score": null,
    "hits": []
  },
  "aggregations": {
    "avg_price": {
      "value": 149.99
    }
  }
}
```

### Aggregation Types

#### Four Main Categories
1. **Metrics** — Вычисление метрик (sum, avg, min, max, etc.)
2. **Bucket** — Группировка документов в **buckets**
3. **Pipeline** — Агрегации на основе результатов других агрегаций
4. **Matrix** — Операции над несколькими полями

## Metrics Aggregations

### Single-value Metrics

#### Basic Metrics
```bash
# Среднее значение
curl -X GET "localhost:9200/sales/_search" \
  -H "Content-Type: application/json" \
  -d '{
    "size": 0,
    "aggs": {
      "average_price": {
        "avg": {"field": "price"}
      }
    }
  }'

# Сумма
curl -X GET "localhost:9200/sales/_search" \
  -H "Content-Type: application/json" \
  -d '{
    "size": 0,
    "aggs": {
      "total_revenue": {
        "sum": {"field": "revenue"}
      }
    }
  }'

# Минимум/Максимум
curl -X GET "localhost:9200/products/_search" \
  -H "Content-Type: application/json" \
  -d '{
    "size": 0,
    "aggs": {
      "min_price": {"min": {"field": "price"}},
      "max_price": {"max": {"field": "price"}}
    }
  }'

# Количество значений
curl -X GET "localhost:9200/products/_search" \
  -H "Content-Type: application/json" \
  -d '{
    "size": 0,
    "aggs": {
      "price_count": {
        "value_count": {"field": "price"}
      }
    }
  }'
```

#### Cardinality (уникальные значения)
```bash
# Количество уникальных значений
curl -X GET "localhost:9200/users/_search" \
  -H "Content-Type: application/json" \
  -d '{
    "size": 0,
    "aggs": {
      "unique_users": {
        "cardinality": {
          "field": "user_id",
          "precision_threshold": 1000
        }
      }
    }
  }'

# Cardinality с высокой точностью
curl -X GET "localhost:9200/products/_search" \
  -H "Content-Type: application/json" \
  -d '{
    "size": 0,
    "aggs": {
      "unique_categories": {
        "cardinality": {
          "field": "category",
          "precision_threshold": 40000
        }
      }
    }
  }'
```

### Multi-value Metrics

#### Stats Aggregation
```bash
# Полная статистика
curl -X GET "localhost:9200/products/_search" \
  -H "Content-Type: application/json" \
  -d '{
    "size": 0,
    "aggs": {
      "price_stats": {
        "stats": {"field": "price"}
      }
    }
  }'

# Response
{
  "price_stats": {
    "count": 1000,
    "min": 9.99,
    "max": 1999.99,
    "avg": 149.99,
    "sum": 149990.0
  }
}
```

#### Extended Stats
```bash
# Расширенная статистика
curl -X GET "localhost:9200/sales/_search" \
  -H "Content-Type: application/json" \
  -d '{
    "size": 0,
    "aggs": {
      "revenue_stats": {
        "extended_stats": {"field": "revenue"}
      }
    }
  }'

# Response включает стандартное отклонение, дисперсию, etc.
{
  "revenue_stats": {
    "count": 1000,
    "min": 0.0,
    "max": 10000.0,
    "avg": 1500.0,
    "sum": 1500000.0,
    "sum_of_squares": 2500000000.0,
    "variance": 8000000.0,
    "variance_population": 8000000.0,
    "variance_sampling": 8000800.0,
    "std_deviation": 2828.43,
    "std_deviation_population": 2828.43,
    "std_deviation_sampling": 2828.49
  }
}
```

#### Percentiles
```bash
# Перцентили
curl -X GET "localhost:9200/response_times/_search" \
  -H "Content-Type: application/json" \
  -d '{
    "size": 0,
    "aggs": {
      "response_time_percentiles": {
        "percentiles": {
          "field": "response_time_ms",
          "percents": [50, 75, 95, 99, 99.9]
        }
      }
    }
  }'

# Percentile ranks
curl -X GET "localhost:9200/scores/_search" \
  -H "Content-Type: application/json" \
  -d '{
    "size": 0,
    "aggs": {
      "score_percentile_ranks": {
        "percentile_ranks": {
          "field": "score",
          "values": [75, 80, 85, 90]
        }
      }
    }
  }'
```

## Bucket Aggregations

### Terms Aggregation

#### Basic Terms
```bash
# Группировка по категориям
curl -X GET "localhost:9200/products/_search" \
  -H "Content-Type: application/json" \
  -d '{
    "size": 0,
    "aggs": {
      "categories": {
        "terms": {
          "field": "category",
          "size": 10,
          "order": {"_count": "desc"}
        }
      }
    }
  }'

# С минимальным количеством документов
curl -X GET "localhost:9200/products/_search" \
  -H "Content-Type: application/json" \
  -d '{
    "size": 0,
    "aggs": {
      "popular_categories": {
        "terms": {
          "field": "category",
          "min_doc_count": 100,
          "size": 20
        }
      }
    }
  }'
```

#### Terms with Sub-aggregations
```bash
# Terms с подагрегациями
curl -X GET "localhost:9200/products/_search" \
  -H "Content-Type: application/json" \
  -d '{
    "size": 0,
    "aggs": {
      "categories": {
        "terms": {"field": "category"},
        "aggs": {
          "avg_price": {
            "avg": {"field": "price"}
          },
          "price_ranges": {
            "range": {
              "field": "price",
              "ranges": [
                {"to": 50},
                {"from": 50, "to": 100},
                {"from": 100, "to": 200},
                {"from": 200}
              ]
            }
          }
        }
      }
    }
  }'
```

### Range Aggregations

#### Numeric Ranges
```bash
# Диапазоны цен
curl -X GET "localhost:9200/products/_search" \
  -H "Content-Type: application/json" \
  -d '{
    "size": 0,
    "aggs": {
      "price_ranges": {
        "range": {
          "field": "price",
          "ranges": [
            {"to": 50, "key": "cheap"},
            {"from": 50, "to": 100, "key": "moderate"},
            {"from": 100, "to": 200, "key": "expensive"},
            {"from": 200, "key": "luxury"}
          ]
        }
      }
    }
  }'

# Диапазоны с подагрегациями
curl -X GET "localhost:9200/products/_search" \
  -H "Content-Type: application/json" \
  -d '{
    "size": 0,
    "aggs": {
      "price_ranges": {
        "range": {
          "field": "price",
          "ranges": [
            {"to": 100},
            {"from": 100, "to": 500},
            {"from": 500}
          ]
        },
        "aggs": {
          "category_distribution": {
            "terms": {"field": "category"}
          }
        }
      }
    }
  }'
```

#### Date Ranges
```bash
# Диапазоны дат
curl -X GET "localhost:9200/sales/_search" \
  -H "Content-Type: application/json" \
  -d '{
    "size": 0,
    "aggs": {
      "sales_periods": {
        "date_range": {
          "field": "sale_date",
          "format": "yyyy-MM-dd",
          "ranges": [
            {"to": "2023-01-01"},
            {"from": "2023-01-01", "to": "2023-07-01"},
            {"from": "2023-07-01", "to": "2024-01-01"},
            {"from": "2024-01-01"}
          ]
        }
      }
    }
  }'
```

### Histogram Aggregations

#### Numeric Histogram
```bash
# Гистограмма цен
curl -X GET "localhost:9200/products/_search" \
  -H "Content-Type: application/json" \
  -d '{
    "size": 0,
    "aggs": {
      "price_histogram": {
        "histogram": {
          "field": "price",
          "interval": 50,
          "min_doc_count": 1
        }
      }
    }
  }'

# Гистограмма с extended bounds
curl -X GET "localhost:9200/products/_search" \
  -H "Content-Type: application/json" \
  -d '{
    "size": 0,
    "aggs": {
      "price_histogram": {
        "histogram": {
          "field": "price",
          "interval": 25,
          "extended_bounds": {
            "min": 0,
            "max": 500
          }
        }
      }
    }
  }'
```

## Date Histogram

### Time-based Bucketing

#### Basic Date Histogram
```bash
# Гистограмма по дням
curl -X GET "localhost:9200/logs/_search" \
  -H "Content-Type: application/json" \
  -d '{
    "size": 0,
    "aggs": {
      "logs_over_time": {
        "date_histogram": {
          "field": "@timestamp",
          "calendar_interval": "day",
          "format": "yyyy-MM-dd"
        }
      }
    }
  }'

# Гистограмма по часам с часовым поясом
curl -X GET "localhost:9200/logs/_search" \
  -H "Content-Type: application/json" \
  -d '{
    "size": 0,
    "aggs": {
      "hourly_logs": {
        "date_histogram": {
          "field": "@timestamp",
          "calendar_interval": "hour",
          "time_zone": "+02:00",
          "format": "yyyy-MM-dd HH:mm"
        }
      }
    }
  }'
```

#### Date Histogram with Sub-aggregations
```bash
# Date histogram с метриками
curl -X GET "localhost:9200/sales/_search" \
  -H "Content-Type: application/json" \
  -d '{
    "size": 0,
    "aggs": {
      "sales_trend": {
        "date_histogram": {
          "field": "sale_date",
          "calendar_interval": "month",
          "format": "yyyy-MM"
        },
        "aggs": {
          "total_sales": {
            "sum": {"field": "amount"}
          },
          "avg_sale": {
            "avg": {"field": "amount"}
          },
          "unique_customers": {
            "cardinality": {"field": "customer_id"}
          }
        }
      }
    }
  }'
```

### Advanced Date Operations

#### Fixed vs Calendar Intervals
```bash
# Calendar intervals (рекомендуется)
curl -X GET "localhost:9200/sales/_search" \
  -H "Content-Type: application/json" \
  -d '{
    "size": 0,
    "aggs": {
      "monthly_sales": {
        "date_histogram": {
          "field": "sale_date",
          "calendar_interval": "month"
        }
      }
    }
  }'

# Fixed intervals (для равномерных интервалов)
curl -X GET "localhost:9200/metrics/_search" \
  -H "Content-Type: application/json" \
  -d '{
    "size": 0,
    "aggs": {
      "fixed_interval_metrics": {
        "date_histogram": {
          "field": "@timestamp",
          "fixed_interval": "30m"
        }
      }
    }
  }'
```

## Pipeline Aggregations

### Parent Pipeline Aggregations

#### Derivative
```bash
# Производная (скорость изменения)
curl -X GET "localhost:9200/metrics/_search" \
  -H "Content-Type: application/json" \
  -d '{
    "size": 0,
    "aggs": {
      "requests_per_minute": {
        "date_histogram": {
          "field": "@timestamp",
          "fixed_interval": "1m"
        },
        "aggs": {
          "requests": {
            "sum": {"field": "request_count"}
          },
          "requests_per_minute_derivative": {
            "derivative": {
              "buckets_path": "requests"
            }
          }
        }
      }
    }
  }'
```

#### Cumulative Sum
```bash
# Кумулятивная сумма
curl -X GET "localhost:9200/sales/_search" \
  -H "Content-Type: application/json" \
  -d '{
    "size": 0,
    "aggs": {
      "monthly_sales": {
        "date_histogram": {
          "field": "sale_date",
          "calendar_interval": "month"
        },
        "aggs": {
          "sales": {
            "sum": {"field": "amount"}
          },
          "cumulative_sales": {
            "cumulative_sum": {
              "buckets_path": "sales"
            }
          }
        }
      }
    }
  }'
```

#### Moving Average
```bash
# Скользящее среднее
curl -X GET "localhost:9200/metrics/_search" \
  -H "Content-Type: application/json" \
  -d '{
    "size": 0,
    "aggs": {
      "response_times": {
        "date_histogram": {
          "field": "@timestamp",
          "fixed_interval": "1m"
        },
        "aggs": {
          "avg_response": {
            "avg": {"field": "response_time"}
          },
          "moving_avg_response": {
            "moving_avg": {
              "buckets_path": "avg_response",
              "window": 5,
              "model": "simple"
            }
          }
        }
      }
    }
  }'
```

### Sibling Pipeline Aggregations

#### Max/Min Bucket
```bash
# Максимальное значение среди buckets
curl -X GET "localhost:9200/sales/_search" \
  -H "Content-Type: application/json" \
  -d '{
    "size": 0,
    "aggs": {
      "categories": {
        "terms": {"field": "category"},
        "aggs": {
          "total_sales": {
            "sum": {"field": "amount"}
          }
        }
      },
      "max_category_sales": {
        "max_bucket": {
          "buckets_path": "categories>total_sales"
        }
      }
    }
  }'
```

#### Bucket Script
```bash
# Арифметические операции над buckets
curl -X GET "localhost:9200/sales/_search" \
  -H "Content-Type: application/json" \
  -d '{
    "size": 0,
    "aggs": {
      "monthly_stats": {
        "date_histogram": {
          "field": "sale_date",
          "calendar_interval": "month"
        },
        "aggs": {
          "total_sales": {"sum": {"field": "amount"}},
          "total_cost": {"sum": {"field": "cost"}},
          "profit_margin": {
            "bucket_script": {
              "buckets_path": {
                "sales": "total_sales",
                "cost": "total_cost"
              },
              "script": "(params.sales - params.cost) / params.sales"
            }
          }
        }
      }
    }
  }'
```

## Matrix Aggregations

### Matrix Stats

#### Multi-field Statistics
```bash
# Статистика по нескольким полям
curl -X GET "localhost:9200/products/_search" \
  -H "Content-Type: application/json" \
  -d '{
    "size": 0,
    "aggs": {
      "product_matrix_stats": {
        "matrix_stats": {
          "fields": ["price", "rating", "review_count"]
        }
      }
    }
  }'

# Response включает корреляции между полями
{
  "product_matrix_stats": {
    "fields": [
      {
        "name": "price",
        "count": 1000,
        "mean": 149.99,
        "variance": 25000.0,
        "skewness": 1.5
      },
      {
        "name": "rating",
        "count": 1000,
        "mean": 4.2,
        "variance": 0.25,
        "skewness": -0.3
      }
    ],
    "correlation": {
      "price": {
        "rating": -0.15
      },
      "rating": {
        "price": -0.15
      }
    }
  }
}
```

## Nested Aggregations

### Nested Objects

#### Nested Aggregation
```bash
# Агрегации по nested объектам
curl -X GET "localhost:9200/products/_search" \
  -H "Content-Type: application/json" \
  -d '{
    "size": 0,
    "aggs": {
      "reviews": {
        "nested": {
          "path": "reviews"
        },
        "aggs": {
          "avg_rating": {
            "avg": {"field": "reviews.rating"}
          },
          "rating_distribution": {
            "terms": {"field": "reviews.rating"}
          }
        }
      }
    }
  }'
```

#### Reverse Nested
```bash
# Обратный nested для доступа к parent документу
curl -X GET "localhost:9200/products/_search" \
  -H "Content-Type: application/json" \
  -d '{
    "size": 0,
    "aggs": {
      "reviews": {
        "nested": {
          "path": "reviews"
        },
        "aggs": {
          "high_ratings": {
            "filter": {
              "range": {"reviews.rating": {"gte": 4}}
            },
            "aggs": {
              "parent_categories": {
                "reverse_nested": {},
                "aggs": {
                  "categories": {
                    "terms": {"field": "category"}
                  }
                }
              }
            }
          }
        }
      }
    }
  }'
```

## Geo Aggregations

### Geo Bounds

#### Geographic Bounding Box
```bash
# Границы географических данных
curl -X GET "localhost:9200/stores/_search" \
  -H "Content-Type: application/json" \
  -d '{
    "size": 0,
    "aggs": {
      "store_bounds": {
        "geo_bounds": {
          "field": "location",
          "wrap_longitude": true
        }
      }
    }
  }'

# Response
{
  "store_bounds": {
    "bounds": {
      "top_left": {
        "lat": 40.8,
        "lon": -74.1
      },
      "bottom_right": {
        "lat": 40.6,
        "lon": -73.9
      }
    }
  }
}
```

### Geo Centroid

#### Geographic Center
```bash
# Географический центр
curl -X GET "localhost:9200/stores/_search" \
  -H "Content-Type: application/json" \
  -d '{
    "size": 0,
    "aggs": {
      "store_centroid": {
        "geo_centroid": {
          "field": "location"
        }
      }
    }
  }'

# Response
{
  "store_centroid": {
    "location": {
      "lat": 40.7128,
      "lon": -74.0060
    },
    "count": 150
  }
}
```

## Significant Terms

### Significant Terms Aggregation

#### Finding Significant Correlations
```bash
# Значимые термины
curl -X GET "localhost:9200/products/_search" \
  -H "Content-Type: application/json" \
  -d '{
    "query": {
      "term": {"category": "electronics"}
    },
    "size": 0,
    "aggs": {
      "significant_features": {
        "significant_terms": {
          "field": "tags",
          "min_doc_count": 10
        }
      }
    }
  }'

# Significant terms по сравнению с background
curl -X GET "localhost:9200/reviews/_search" \
  -H "Content-Type: application/json" \
  -d '{
    "query": {
      "term": {"rating": 5}
    },
    "size": 0,
    "aggs": {
      "significant_words": {
        "significant_terms": {
          "field": "review_text",
          "jlh": {},
          "size": 20
        }
      }
    }
  }'
```

## Sampler Aggregation

### Document Sampling

#### Random Sampling
```bash
# Сэмплирование для производительности
curl -X GET "localhost:9200/logs/_search" \
  -H "Content-Type: application/json" \
  -d '{
    "size": 0,
    "aggs": {
      "sample": {
        "sampler": {
          "probability": 0.1,
          "max_docs_per_value": 100
        },
        "aggs": {
          "error_types": {
            "terms": {"field": "error_type"}
          }
        }
      }
    }
  }'
```

#### Diversified Sampling
```bash
# Разнообразное сэмплирование
curl -X GET "localhost:9200/user_events/_search" \
  -H "Content-Type: application/json" \
  -d '{
    "size": 0,
    "aggs": {
      "diverse_sample": {
        "diversified_sampler": {
          "field": "user_id",
          "max_docs_per_value": 1
        },
        "aggs": {
          "event_types": {
            "terms": {"field": "event_type"}
          }
        }
      }
    }
  }'
```

## Real-time Analytics

### Time Series Analytics

#### Rolling Time Windows
```bash
# Скользящие временные окна
curl -X GET "localhost:9200/metrics/_search" \
  -H "Content-Type: application/json" \
  -d '{
    "size": 0,
    "aggs": {
      "rolling_avg": {
        "date_histogram": {
          "field": "@timestamp",
          "fixed_interval": "1m"
        },
        "aggs": {
          "cpu_usage": {
            "avg": {"field": "cpu_percent"}
          },
          "cpu_trend": {
            "moving_avg": {
              "buckets_path": "cpu_usage",
              "window": 10,
              "model": "ewma",
              "settings": {"alpha": 0.3}
            }
          }
        }
      }
    }
  }'
```

#### Real-time Dashboards
```bash
# Агрегации для dashboard
curl -X GET "localhost:9200/metrics/_search" \
  -H "Content-Type: application/json" \
  -d '{
    "query": {
      "range": {
        "@timestamp": {
          "gte": "now-1h"
        }
      }
    },
    "size": 0,
    "aggs": {
      "current_stats": {
        "stats": {"field": "response_time"}
      },
      "error_rate": {
        "filter": {"term": {"status": "error"}},
        "aggs": {
          "count": {"value_count": {"field": "request_id"}}
        }
      },
      "top_endpoints": {
        "terms": {
          "field": "endpoint",
          "size": 10
        },
        "aggs": {
          "avg_response_time": {"avg": {"field": "response_time"}},
          "error_count": {
            "filter": {"term": {"status": "error"}},
            "aggs": {"count": {"value_count": {"field": "request_id"}}}
          }
        }
      },
      "response_time_percentiles": {
        "percentiles": {
          "field": "response_time",
          "percents": [50, 95, 99]
        }
      }
    }
  }'
```

### Anomaly Detection

#### Statistical Anomaly Detection
```bash
# Обнаружение аномалий с помощью статистических методов
curl -X GET "localhost:9200/metrics/_search" \
  -H "Content-Type: application/json" \
  -d '{
    "size": 0,
    "aggs": {
      "response_time_analysis": {
        "date_histogram": {
          "field": "@timestamp",
          "fixed_interval": "5m"
        },
        "aggs": {
          "avg_response_time": {"avg": {"field": "response_time"}},
          "response_time_stats": {"extended_stats": {"field": "response_time"}},
          "anomaly_score": {
            "bucket_script": {
              "buckets_path": {
                "avg": "avg_response_time",
                "mean": "response_time_stats.avg",
                "std": "response_time_stats.std_deviation"
              },
              "script": "Math.abs(params.avg - params.mean) / params.std"
            }
          }
        }
      }
    }
  }'
```

## Java Aggregations API

### High Level REST Client

#### Building Aggregations
```java
@Service
public class ElasticsearchAggregationService {

    @Autowired
    private RestHighLevelClient client;

    public AggregationResult getProductAnalytics() throws IOException {
        SearchRequest searchRequest = new SearchRequest("products");
        SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();

        // Build aggregations
        AggregationBuilder categoriesAgg = AggregationBuilders.terms("categories")
            .field("category")
            .size(20)
            .subAggregation(AggregationBuilders.avg("avg_price").field("price"))
            .subAggregation(AggregationBuilders.sum("total_stock").field("stock_quantity"));

        AggregationBuilder priceRangesAgg = AggregationBuilders.range("price_ranges")
            .field("price")
            .addRange(0.0, 50.0)
            .addRange(50.0, 100.0)
            .addRange(100.0, 200.0)
            .addRange(200.0, Double.MAX_VALUE);

        AggregationBuilder statsAgg = AggregationBuilders.stats("price_stats")
            .field("price");

        sourceBuilder.aggregation(categoriesAgg);
        sourceBuilder.aggregation(priceRangesAgg);
        sourceBuilder.aggregation(statsAgg);
        sourceBuilder.size(0);

        searchRequest.source(sourceBuilder);

        SearchResponse response = client.search(searchRequest, RequestOptions.DEFAULT);
        return parseAggregationResults(response.getAggregations());
    }

    public SalesAnalytics getSalesAnalytics(LocalDate startDate, LocalDate endDate) throws IOException {
        SearchRequest searchRequest = new SearchRequest("sales");
        SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();

        // Date histogram aggregation
        DateHistogramAggregationBuilder salesTrend = AggregationBuilders
            .dateHistogram("sales_trend")
            .field("sale_date")
            .calendarInterval(DateHistogramInterval.MONTH)
            .format("yyyy-MM")
            .subAggregation(AggregationBuilders.sum("total_amount").field("amount"))
            .subAggregation(AggregationBuilders.avg("avg_amount").field("amount"))
            .subAggregation(AggregationBuilders.cardinality("unique_customers").field("customer_id"));

        // Terms aggregation for top products
        TermsAggregationBuilder topProducts = AggregationBuilders
            .terms("top_products")
            .field("product_id")
            .size(10)
            .subAggregation(AggregationBuilders.sum("revenue").field("amount"));

        sourceBuilder.aggregation(salesTrend);
        sourceBuilder.aggregation(topProducts);

        // Add date range filter
        BoolQueryBuilder dateFilter = QueryBuilders.boolQuery()
            .filter(QueryBuilders.rangeQuery("sale_date")
                .gte(startDate)
                .lte(endDate));

        sourceBuilder.query(dateFilter);
        sourceBuilder.size(0);

        searchRequest.source(sourceBuilder);

        SearchResponse response = client.search(searchRequest, RequestOptions.DEFAULT);
        return parseSalesAnalytics(response.getAggregations());
    }

    public List<GeoStats> getGeographicAnalytics() throws IOException {
        SearchRequest searchRequest = new SearchRequest("stores");
        SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();

        // Geo bounds aggregation
        GeoBoundsAggregationBuilder geoBounds = AggregationBuilders
            .geoBounds("store_bounds")
            .field("location");

        // Geo centroid aggregation
        GeoCentroidAggregationBuilder geoCentroid = AggregationBuilders
            .geoCentroid("store_centroid")
            .field("location");

        sourceBuilder.aggregation(geoBounds);
        sourceBuilder.aggregation(geoCentroid);
        sourceBuilder.size(0);

        searchRequest.source(sourceBuilder);

        SearchResponse response = client.search(searchRequest, RequestOptions.DEFAULT);
        return parseGeoAnalytics(response.getAggregations());
    }

    public RealTimeMetrics getRealTimeMetrics() throws IOException {
        SearchRequest searchRequest = new SearchRequest("metrics");
        SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();

        // Time-based aggregations
        DateHistogramAggregationBuilder timeSeries = AggregationBuilders
            .dateHistogram("time_series")
            .field("@timestamp")
            .fixedInterval(DateHistogramInterval.minutes(1))
            .subAggregation(AggregationBuilders.avg("avg_cpu").field("cpu_percent"))
            .subAggregation(AggregationBuilders.avg("avg_memory").field("memory_percent"))
            .subAggregation(AggregationBuilders.sum("total_requests").field("request_count"));

        // Moving averages for trend analysis
        MovingAvgAggregationBuilder cpuTrend = AggregationBuilders
            .movingAvg("cpu_trend")
            .bucketsPath("time_series>avg_cpu")
            .window(5)
            .modelBuilder(LinearModel.builder());

        sourceBuilder.aggregation(timeSeries);
        sourceBuilder.aggregation(cpuTrend);

        // Filter for last hour
        RangeQueryBuilder timeFilter = QueryBuilders.rangeQuery("@timestamp")
            .gte("now-1h");

        sourceBuilder.query(timeFilter);
        sourceBuilder.size(0);

        searchRequest.source(sourceBuilder);

        SearchResponse response = client.search(searchRequest, RequestOptions.DEFAULT);
        return parseRealTimeMetrics(response.getAggregations());
    }
}
```

### Java API Client 8.x

#### Modern Aggregation API
```java
@Service
public class ModernAggregationService {

    @Autowired
    private ElasticsearchClient client;

    public AggregationResult performComplexAggregation() throws IOException {
        SearchRequest request = SearchRequest.of(s -> s
            .index("products")
            .size(0)
            .aggregations("categories", a -> a
                .terms(t -> t
                    .field("category")
                    .size(10)
                    .order(List.of(Map.of("_count", SortOrder.Desc)))
                )
                .aggregations("price_stats", sa -> sa
                    .stats(st -> st.field("price"))
                )
                .aggregations("avg_rating", sa -> sa
                    .avg(av -> av.field("rating"))
                )
            )
            .aggregations("price_histogram", a -> a
                .histogram(h -> h
                    .field("price")
                    .interval(25.0)
                )
            )
            .aggregations("overall_stats", a -> a
                .stats(st -> st.field("price"))
            )
        );

        SearchResponse<Void> response = client.search(request, Void.class);
        return parseAggregations(response.aggregations());
    }

    public SalesReport generateSalesReport(LocalDate startDate, LocalDate endDate) throws IOException {
        SearchRequest request = SearchRequest.of(s -> s
            .index("sales")
            .query(q -> q
                .range(r -> r
                    .field("sale_date")
                    .gte(JsonData.of(startDate))
                    .lte(JsonData.of(endDate))
                )
            )
            .aggregations("monthly_sales", a -> a
                .dateHistogram(dh -> dh
                    .field("sale_date")
                    .calendarInterval(CalendarInterval.Month)
                    .format("yyyy-MM")
                )
                .aggregations("total_amount", sa -> sa.sum(su -> su.field("amount")))
                .aggregations("avg_amount", sa -> sa.avg(av -> av.field("amount")))
                .aggregations("unique_customers", sa -> sa.cardinality(c -> c.field("customer_id")))
            )
            .aggregations("top_products", a -> a
                .terms(t -> t
                    .field("product_id")
                    .size(20)
                    .order(List.of(Map.of("_count", SortOrder.Desc)))
                )
                .aggregations("revenue", sa -> sa.sum(su -> su.field("amount")))
                .aggregations("avg_price", sa -> sa.avg(av -> av.field("price")))
            )
            .size(0)
        );

        SearchResponse<Void> response = client.search(request, Void.class);
        return buildSalesReport(response.aggregations());
    }

    public AnomalyReport detectAnomalies() throws IOException {
        SearchRequest request = SearchRequest.of(s -> s
            .index("metrics")
            .query(q -> q
                .range(r -> r
                    .field("@timestamp")
                    .gte(JsonData.of("now-24h"))
                )
            )
            .aggregations("time_series", a -> a
                .dateHistogram(dh -> dh
                    .field("@timestamp")
                    .fixedInterval(DateHistogramInterval.minutes(5))
                )
                .aggregations("avg_response_time", sa -> sa.avg(av -> av.field("response_time")))
                .aggregations("response_time_stats", sa -> sa.extendedStats(es -> es.field("response_time")))
            )
            .size(0)
        );

        SearchResponse<Void> response = client.search(request, Void.class);
        return analyzeAnomalies(response.aggregations());
    }

    private AggregationResult parseAggregations(Aggregations aggregations) {
        // Parse aggregation results
        return new AggregationResult();
    }

    private SalesReport buildSalesReport(Aggregations aggregations) {
        // Build sales report from aggregations
        return new SalesReport();
    }

    private AnomalyReport analyzeAnomalies(Aggregations aggregations) {
        // Analyze time series for anomalies
        return new AnomalyReport();
    }
}
```

## Performance Optimization

### Aggregation Performance Tips

#### 1. Limit Aggregation Scope
```bash
# Используй фильтры для ограничения данных
curl -X GET "localhost:9200/products/_search" \
  -H "Content-Type: application/json" \
  -d '{
    "query": {
      "range": {
        "price": {"gte": 100}
      }
    },
    "size": 0,
    "aggs": {
      "price_ranges": {
        "range": {
          "field": "price",
          "ranges": [
            {"from": 100, "to": 200},
            {"from": 200, "to": 500},
            {"from": 500}
          ]
        }
      }
    }
  }'
```

#### 2. Optimize Terms Aggregation
```bash
# Ограничь размер terms aggregation
curl -X GET "localhost:9200/products/_search" \
  -H "Content-Type: application/json" \
  -d '{
    "size": 0,
    "aggs": {
      "categories": {
        "terms": {
          "field": "category",
          "size": 50,
          "min_doc_count": 10,
          "shard_size": 100
        }
      }
    }
  }'
```

#### 3. Use Approximate Aggregations
```bash
# Cardinality с precision threshold
curl -X GET "localhost:9200/users/_search" \
  -H "Content-Type: application/json" \
  -d '{
    "size": 0,
    "aggs": {
      "unique_users": {
        "cardinality": {
          "field": "user_id",
          "precision_threshold": 10000
        }
      }
    }
  }'

# Percentiles с compression
curl -X GET "localhost:9200/response_times/_search" \
  -H "Content-Type: application/json" \
  -d '{
    "size": 0,
    "aggs": {
      "response_percentiles": {
        "percentiles": {
          "field": "response_time",
          "percents": [50, 95, 99],
          "compression": 100
        }
      }
    }
  }'
```

### Caching and Memory Management

#### Aggregation Caching
```yaml
# elasticsearch.yml - Aggregation cache settings
indices.queries.cache.size: 10%
indices.requests.cache.size: 1%

# Field data cache
indices.fielddata.cache.size: 40%

# Shard request cache
indices.requests.cache.expire: 1h
```

#### Memory-conscious Aggregations
```bash
# Используй circuit breaker для предотвращения OOM
curl -X PUT "localhost:9200/_cluster/settings" \
  -H "Content-Type: application/json" \
  -d '{
    "persistent": {
      "indices.breaker.request.limit": "70%",
      "indices.breaker.fielddata.limit": "60%"
    }
  }'
```

## Лучшие практики

### Aggregation Design

#### 1. Choose Right Aggregation Type
- **Terms** для категориальных данных
- **Date histogram** для временных рядов
- **Range** для числовых диапазонов
- **Cardinality** для уникальных значений (с осторожностью)

#### 2. Optimize for Performance
- **Фильтруй данные** перед агрегацией
- **Ограничивай размер terms aggregations**
- **Используй shard_size** для точности vs производительности
- **Кэшируй** повторяющиеся агрегации

#### 3. Handle Large Datasets
- **Используй pagination** для больших результатов
- **Sampler aggregation** для предварительного анализа
- **Composite aggregation** для эффективной пагинации
- **Shard-level reduction** для распределенных вычислений

### Common Patterns

#### 1. Dashboard Analytics
```bash
# Комплексная dashboard агрегация
curl -X GET "localhost:9200/metrics/_search" \
  -H "Content-Type: application/json" \
  -d '{
    "query": {
      "range": {"@timestamp": {"gte": "now-1h"}}
    },
    "size": 0,
    "aggs": {
      "time_series": {
        "date_histogram": {
          "field": "@timestamp",
          "fixed_interval": "5m"
        },
        "aggs": {
          "cpu_avg": {"avg": {"field": "cpu_percent"}},
          "memory_avg": {"avg": {"field": "memory_percent"}},
          "requests_sum": {"sum": {"field": "request_count"}},
          "errors_count": {
            "filter": {"term": {"status": "error"}},
            "aggs": {"count": {"value_count": {"field": "request_id"}}}
          }
        }
      },
      "top_endpoints": {
        "terms": {"field": "endpoint", "size": 10},
        "aggs": {
          "avg_response_time": {"avg": {"field": "response_time"}},
          "p95_response_time": {
            "percentiles": {"field": "response_time", "percents": [95]}
          }
        }
      },
      "error_rate": {
        "filter": {"term": {"status": "error"}},
        "aggs": {"rate": {"value_count": {"field": "request_id"}}}
      }
    }
  }'
```

#### 2. E-commerce Analytics
```bash
# Аналитика продаж
curl -X GET "localhost:9200/sales/_search" \
  -H "Content-Type: application/json" \
  -d '{
    "size": 0,
    "aggs": {
      "sales_by_category": {
        "terms": {"field": "category", "size": 20},
        "aggs": {
          "total_sales": {"sum": {"field": "amount"}},
          "avg_order_value": {"avg": {"field": "amount"}},
          "unique_customers": {"cardinality": {"field": "customer_id"}},
          "sales_trend": {
            "date_histogram": {"field": "sale_date", "calendar_interval": "month"},
            "aggs": {"monthly_total": {"sum": {"field": "amount"}}}
          }
        }
      },
      "revenue_percentiles": {
        "percentiles": {"field": "amount", "percents": [25, 50, 75, 95, 99]}
      },
      "customer_segments": {
        "range": {
          "field": "customer_lifetime_value",
          "ranges": [
            {"to": 100, "key": "low_value"},
            {"from": 100, "to": 500, "key": "medium_value"},
            {"from": 500, "to": 2000, "key": "high_value"},
            {"from": 2000, "key": "vip"}
          ]
        }
      }
    }
  }'
```

#### 3. Log Analytics
```bash
# Анализ логов
curl -X GET "localhost:9200/logs-*/_search" \
  -H "Content-Type: application/json" \
  -d '{
    "query": {
      "range": {"@timestamp": {"gte": "now-24h"}}
    },
    "size": 0,
    "aggs": {
      "error_distribution": {
        "terms": {"field": "level", "size": 10}
      },
      "errors_over_time": {
        "date_histogram": {"field": "@timestamp", "fixed_interval": "1h"},
        "aggs": {
          "error_count": {
            "filter": {"term": {"level": "ERROR"}},
            "aggs": {"count": {"value_count": {"field": "_id"}}}
          }
        }
      },
      "top_error_messages": {
        "terms": {"field": "message.keyword", "size": 20},
        "aggs": {
          "error_count": {"value_count": {"field": "_id"}},
          "first_occurrence": {"min": {"field": "@timestamp"}},
          "last_occurrence": {"max": {"field": "@timestamp"}}
        }
      },
      "response_time_analysis": {
        "percentiles": {
          "field": "response_time",
          "percents": [50, 95, 99]
        }
      }
    }
  }'
```

### Error Handling

#### 1. Handle Aggregation Errors
- **Circuit breaker exceptions** — уменьши нагрузку
- **Timeout errors** — увеличивай **timeout** или оптимизируй запрос
- **Memory errors** — уменьши **scope** агрегаций
- **Shard failures** — проверь здоровье кластера

#### 2. Validation and Testing
- **Тестируй агрегации** на тестовых данных
- **Валидируй результаты** на соответствие ожиданиям
- **Мониторь производительность** агрегаций
- **Документируй** сложные агрегации

### Scaling Considerations

#### 1. Large Scale Aggregations
- **Используй composite aggregation** для больших **terms**
- **Shard-level aggregations** для предварительной обработки
- **Distributed coordination** для сложных вычислений
- **Async processing** для **long-running aggregations**

#### 2. Real-time Requirements
- **Near `real-time` aggregations** с **refresh intervals**
- **Incremental updates** для часто меняющихся данных
- **Caching strategies** для повторяющихся запросов
- **Pre-computed aggregations** для **dashboard**

**Elasticsearch** агрегации предоставляют мощный набор инструментов для аналитики данных, от простых метрик до сложных многоуровневых аналитических запросов. Правильное использование агрегаций позволяет извлекать ценную информацию из больших объемов данных в реальном времени.

### Ключевые возможности:

1. **Metrics aggregations** — статистические расчеты (avg, sum, percentiles)
2. **Bucket aggregations** — группировка данных (terms, `date_histogram`, ranges)
3. **Pipeline aggregations** — анализ трендов и производных
4. **Matrix aggregations** — многомерная статистика
5. **Geo aggregations** — географический анализ

### Архитектурные принципы:

1. **Distributed computation** — агрегации выполняются на всех шардах
2. **Memory efficiency** — оптимизированные структуры данных
3. **Approximation algorithms** — баланс точности и производительности
4. **Real-time updates** — **near real-time** результаты
5. **Composability** — возможность комбинировать разные типы агрегаций

### Производительность:

1. **Shard-local aggregations** — предварительная обработка на шардах
2. **Coordinator node reduction** — финальная агрегация на **coordinator**
3. **Caching** — кэширование результатов агрегаций
4. **Approximations** — **cardinality**, **percentiles** с **controllable accuracy**
5. **Parallel execution** — одновременная обработка на всех шардах

### Java интеграция:

1. **High `Level REST` Client** — **imperative API** для **complex aggregations**
2. **Java `API Client 8`.x** — **modern fluent API**
3. **Spring `Data` Elasticsearch** — **declarative aggregations**
4. **Reactive support** — **asynchronous aggregation processing**

### Best practices:

1. **Query design** — правильная структура агрегаций
2. **Performance optimization** — фильтры, **limits**, **approximations**
3. **Memory management** — **circuit breakers**, **caching**
4. **Result interpretation** — понимание статистической значимости
5. **Monitoring** — **latency**, **throughput**, **error rates**

### Типичные сценарии:

1. **Business intelligence** — **sales analytics**, **customer segmentation**
2. **Monitoring dashboards** — **real-time metrics**, **alerting**
3. **Log analysis** — **error patterns**, **performance trends**
4. **E-commerce analytics** — **product performance**, **user behavior**
5. **IoT analytics** — **sensor data aggregation**, **anomaly detection**

Агрегации **Elasticsearch** позволяют превращать сырые данные в **actionable insights**, обеспечивая мощную аналитику для принятия решений. Правильное проектирование и оптимизация агрегаций критически важны для масштабируемых и производительных аналитических систем.

**Продолжение следует:**
- **elasticsearch-basics.md** (завершен)
- **elasticsearch-indexing.md** (завершен)
- **elasticsearch-queries.md** (завершен)
- **elasticsearch-aggregations.md** (завершен)
- **elasticsearch-clustering.md**
- **elasticsearch-performance.md**

## Решение проблем

**Медленные агрегации:** ограничьте размер выборки (фильтры, индексы), уменьшите `size` в terms-агрегации, используйте `composite` для глубокой пагинации. Для больших кардинальностей рассмотрите `sampler` или предварительную индексацию в отдельный индекс с преагрегацией.

**OutOfMemory при агрегациях:** увеличьте heap узла или уменьшите объём данных в одном запросе. Ограничьте вложенность и число бакетов. Используйте `execution_hint: map` для terms-агрегации при необходимости.

**Неточные или пустые результаты в terms:** при большой кардинальности используйте `precision_threshold` или принимайте приближённые подсчёты. Проверьте маппинг поля (keyword vs text) и фильтры, исключающие документы.

**Таймауты:** разбейте агрегацию на несколько запросов или используйте асинхронный search с длинным таймаутом. Оптимизируйте запрос — уберите лишние под-агрегации и уменьшите размер бакетов.

Следующий файл — **elasticsearch-clustering.md**!


