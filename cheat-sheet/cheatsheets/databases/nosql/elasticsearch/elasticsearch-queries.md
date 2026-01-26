# Elasticsearch: Поиск и запросы - Полное руководство по поисковым запросам

Комплексное руководство по поисковым запросам Elasticsearch: от базовых до сложных запросов, фильтрация, сортировка, пагинация и оптимизация производительности поиска.

**Дата последнего обновления:** 2026-01-21

## Полезные ссылки

### Официальная документация
- [Query DSL](https://www.elastic.co/guide/en/elasticsearch/reference/current/query-dsl.html)
- [Search API](https://www.elastic.co/guide/en/elasticsearch/reference/current/search-search.html)
- [Query Types](https://www.elastic.co/guide/en/elasticsearch/reference/current/query-dsl-queries.html)

### Дизайн и оптимизация
- [Search Best Practices](https://www.elastic.co/guide/en/elasticsearch/reference/current/search.html)
- [Query Optimization](https://www.elastic.co/guide/en/elasticsearch/reference/current/query-optimization.html)
- [Relevance Tuning](https://www.elastic.co/guide/en/elasticsearch/reference/current/relevance-tuning.html)

### Инструменты
- [Elasticsearch Query DSL Tool](https://www.elastic.co/guide/en/elasticsearch/reference/current/query-dsl.html)
- [Kibana Dev Tools](https://www.elastic.co/guide/en/kibana/current/dev-tools.html)
- [Elasticsearch SQL](https://www.elastic.co/guide/en/elasticsearch/reference/current/sql-spec.html)

### См. также
- `databases/elasticsearch/elasticsearch-basics.md` - Основы Elasticsearch
- `databases/elasticsearch/elasticsearch-indexing.md` - Индексация документов
- `databases/elasticsearch/elasticsearch-aggregations.md` - Агрегации
- `databases/elasticsearch/elasticsearch-performance.md` - Производительность

## Содержание

- [Search API](#search-api)
- [Query DSL](#query-dsl)
- [Term Queries](#term-queries)
- [Full-text Queries](#full-text-queries)
- [Compound Queries](#compound-queries)
- [Geo Queries](#geo-queries)
- [Nested Queries](#nested-queries)
- [Function Score Queries](#function-score-queries)
- [Filtering и Post-filtering](#filtering-и-post-filtering)
- [Sorting и Relevance](#sorting-и-relevance)
- [Pagination](#pagination)
- [Search Templates](#search-templates)
- [Query Profiling](#query-profiling)
- [Java Search API](#java-search-api)
- [Best Practices](#best-practices)
- [Заключение](#заключение)

## Search API

### Basic Search

#### URI Search
```bash
# Поиск всех документов
curl -X GET "localhost:9200/products/_search"

# Поиск с q параметром
curl -X GET "localhost:9200/products/_search?q=name:headphones"

# Поиск в нескольких индексах
curl -X GET "localhost:9200/products,orders/_search?q=status:active"

# Поиск с лимитом
curl -X GET "localhost:9200/products/_search?size=50"

# Поиск с пагинацией
curl -X GET "localhost:9200/products/_search?from=100&size=50"
```

#### Request Body Search
```bash
# Поиск с request body
curl -X GET "localhost:9200/products/_search" \
  -H "Content-Type: application/json" \
  -d '{
    "query": {
      "match_all": {}
    },
    "size": 10
  }'

# Поиск с сортировкой
curl -X GET "localhost:9200/products/_search" \
  -H "Content-Type: application/json" \
  -d '{
    "query": {
      "match_all": {}
    },
    "sort": [
      {"price": "desc"},
      {"_score": "desc"}
    ]
  }'

# Поиск с source filtering
curl -X GET "localhost:9200/products/_search" \
  -H "Content-Type: application/json" \
  -d '{
    "query": {
      "match": {
        "name": "headphones"
      }
    },
    "_source": ["name", "price", "category"]
  }'
```

### Search Parameters

#### Common Parameters
```bash
# Timeout
curl -X GET "localhost:9200/products/_search?timeout=5s" \
  -H "Content-Type: application/json" \
  -d '{"query": {"match_all": {}}}'

# Routing
curl -X GET "localhost:9200/user_products/_search?routing=user123" \
  -H "Content-Type: application/json" \
  -d '{"query": {"term": {"user_id": "user123"}}}'

# Search type
curl -X GET "localhost:9200/products/_search?search_type=dfs_query_then_fetch" \
  -H "Content-Type: application/json" \
  -d '{"query": {"match": {"name": "headphones"}}}'

# Allow partial search results
curl -X GET "localhost:9200/products/_search?allow_partial_search_results=false" \
  -H "Content-Type: application/json" \
  -d '{"query": {"match_all": {}}}'
```

## Query DSL

### Structure of Queries

#### Query Context vs Filter Context
```json
{
  "query": {
    "bool": {
      "must": [
        {
          "match": {
            "title": {
              "query": "Search",
              "boost": 2
            }
          }
        }
      ],
      "filter": [
        {
          "term": {
            "status": "published"
          }
        },
        {
          "range": {
            "publish_date": {
              "gte": "2015-01-01"
            }
          }
        }
      ]
    }
  }
}
```

#### Query vs Filter Performance
```java
public class QueryContextAnalyzer {

    // Query context: влияет на score
    public SearchRequest createScoringQuery(String searchTerm) {
        return SearchRequest.of(s -> s
            .index("products")
            .query(q -> q
                .bool(b -> b
                    .must(m -> m
                        .match(mt -> mt
                            .field("name")
                            .query(searchTerm)
                            .boost(2.0f)
                        )
                    )
                    .must(m -> m
                        .match(mt -> mt
                            .field("description")
                            .query(searchTerm)
                            .boost(1.0f)
                        )
                    )
                )
            )
        );
    }

    // Filter context: не влияет на score, кэшируется
    public SearchRequest createFilteredQuery(String category, BigDecimal minPrice) {
        return SearchRequest.of(s -> s
            .index("products")
            .query(q -> q
                .bool(b -> b
                    .filter(f -> f
                        .term(t -> t
                            .field("category")
                            .value(category)
                        )
                    )
                    .filter(f -> f
                        .range(r -> r
                            .field("price")
                            .gte(JsonData.of(minPrice))
                        )
                    )
                )
            )
        );
    }

    // Hybrid approach
    public SearchRequest createHybridQuery(String searchTerm, String category) {
        return SearchRequest.of(s -> s
            .index("products")
            .query(q -> q
                .bool(b -> b
                    // Query context - влияет на релевантность
                    .should(sh -> sh
                        .match(mt -> mt
                            .field("name")
                            .query(searchTerm)
                            .boost(3.0f)
                        )
                    )
                    .should(sh -> sh
                        .match(mt -> mt
                            .field("description")
                            .query(searchTerm)
                            .boost(1.0f)
                        )
                    )
                    .minimumShouldMatch("1")
                    // Filter context - фильтрует без влияния на score
                    .filter(f -> f
                        .term(t -> t
                            .field("category")
                            .value(category)
                        )
                    )
                    .filter(f -> f
                        .term(t -> t
                            .field("status")
                            .value("active")
                        )
                    )
                )
            )
        );
    }
}
```

## Term Queries

### Exact Value Queries

#### Term Query
```bash
# Точное совпадение
curl -X GET "localhost:9200/products/_search" \
  -H "Content-Type: application/json" \
  -d '{
    "query": {
      "term": {
        "category": "electronics"
      }
    }
  }'

# Term с boost
curl -X GET "localhost:9200/products/_search" \
  -H "Content-Type: application/json" \
  -d '{
    "query": {
      "term": {
        "category": {
          "value": "electronics",
          "boost": 2.0
        }
      }
    }
  }'
```

#### Terms Query
```bash
# Несколько значений
curl -X GET "localhost:9200/products/_search" \
  -H "Content-Type: application/json" \
  -d '{
    "query": {
      "terms": {
        "category": ["electronics", "books", "clothing"]
      }
    }
  }'

# Terms lookup (динамические значения из другого документа)
curl -X GET "localhost:9200/products/_search" \
  -H "Content-Type: application/json" \
  -d '{
    "query": {
      "terms": {
        "category": {
          "index": "user_preferences",
          "id": "user123",
          "path": "favorite_categories"
        }
      }
    }
  }'
```

#### Range Query
```bash
# Числовой диапазон
curl -X GET "localhost:9200/products/_search" \
  -H "Content-Type: application/json" \
  -d '{
    "query": {
      "range": {
        "price": {
          "gte": 100,
          "lte": 500,
          "boost": 2.0
        }
      }
    }
  }'

# Диапазон дат
curl -X GET "localhost:9200/logs/_search" \
  -H "Content-Type: application/json" \
  -d '{
    "query": {
      "range": {
        "@timestamp": {
          "gte": "2023-01-01",
          "lt": "2023-02-01",
          "format": "yyyy-MM-dd"
        }
      }
    }
  }'

# Диапазон с относительными датами
curl -X GET "localhost:9200/logs/_search" \
  -H "Content-Type: application/json" \
  -d '{
    "query": {
      "range": {
        "@timestamp": {
          "gte": "now-1h",
          "lt": "now"
        }
      }
    }
  }'
```

#### Exists Query
```bash
# Документы с непустым полем
curl -X GET "localhost:9200/products/_search" \
  -H "Content-Type: application/json" \
  -d '{
    "query": {
      "exists": {
        "field": "discount"
      }
    }
  }'

# Документы без поля
curl -X GET "localhost:9200/products/_search" \
  -H "Content-Type: application/json" \
  -d '{
    "query": {
      "bool": {
        "must_not": {
          "exists": {
            "field": "discontinued"
          }
        }
      }
    }
  }'
```

### Prefix, Wildcard, and Regexp

#### Prefix Query
```bash
# Поиск по префиксу
curl -X GET "localhost:9200/products/_search" \
  -H "Content-Type: application/json" \
  -d '{
    "query": {
      "prefix": {
        "name": {
          "value": "wire"
        }
      }
    }
  }'

# Prefix с boost
curl -X GET "localhost:9200/products/_search" \
  -H "Content-Type: application/json" \
  -d '{
    "query": {
      "prefix": {
        "category": {
          "value": "elect",
          "boost": 1.5
        }
      }
    }
  }'
```

#### Wildcard Query
```bash
# Wildcard поиск
curl -X GET "localhost:9200/products/_search" \
  -H "Content-Type: application/json" \
  -d '{
    "query": {
      "wildcard": {
        "name": {
          "value": "wire*head*",
          "boost": 2.0
        }
      }
    }
  }'

# Несколько wildcards
curl -X GET "localhost:9200/products/_search" \
  -H "Content-Type: application/json" \
  -d '{
    "query": {
      "bool": {
        "should": [
          {"wildcard": {"name": "*phone*"}},
          {"wildcard": {"name": "*head*"}}
        ]
      }
    }
  }'
```

#### Regexp Query
```bash
# Регулярные выражения
curl -X GET "localhost:9200/products/_search" \
  -H "Content-Type: application/json" \
  -d '{
    "query": {
      "regexp": {
        "product_code": {
          "value": "PROD-[0-9]{3}-[A-Z]{2}",
          "flags": "ALL",
          "boost": 1.0
        }
      }
    }
  }'

# Case-insensitive regexp
curl -X GET "localhost:9200/products/_search" \
  -H "Content-Type: application/json" \
  -d '{
    "query": {
      "regexp": {
        "name": {
          "value": "[Ww]ireless.*[Hh]eadphones",
          "flags": "CASE_INSENSITIVE"
        }
      }
    }
  }'
```

## Full-text Queries

### Match Queries

#### Match Query
```bash
# Базовый match
curl -X GET "localhost:9200/products/_search" \
  -H "Content-Type: application/json" \
  -d '{
    "query": {
      "match": {
        "description": "wireless bluetooth headphones"
      }
    }
  }'

# Match с параметрами
curl -X GET "localhost:9200/products/_search" \
  -H "Content-Type: application/json" \
  -d '{
    "query": {
      "match": {
        "description": {
          "query": "wireless bluetooth headphones",
          "operator": "and",
          "minimum_should_match": "75%",
          "boost": 2.0
        }
      }
    }
  }'
```

#### Match Phrase
```bash
# Точная фраза
curl -X GET "localhost:9200/products/_search" \
  -H "Content-Type: application/json" \
  -d '{
    "query": {
      "match_phrase": {
        "description": "noise cancelling"
      }
    }
  }'

# Match phrase с slop
curl -X GET "localhost:9200/products/_search" \
  -H "Content-Type: application/json" \
  -d '{
    "query": {
      "match_phrase": {
        "description": {
          "query": "wireless headphones",
          "slop": 2
        }
      }
    }
  }'
```

#### Match Phrase Prefix
```bash
# Автодополнение
curl -X GET "localhost:9200/products/_search" \
  -H "Content-Type: application/json" \
  -d '{
    "query": {
      "match_phrase_prefix": {
        "name": {
          "query": "wireless head",
          "max_expansions": 10
        }
      }
    }
  }'
```

### Multi-match Query

#### Multi-field Search
```bash
# Поиск в нескольких полях
curl -X GET "localhost:9200/products/_search" \
  -H "Content-Type: application/json" \
  -d '{
    "query": {
      "multi_match": {
        "query": "wireless headphones",
        "fields": ["name^3", "description", "tags"],
        "type": "best_fields",
        "tie_breaker": 0.3
      }
    }
  }'

# Cross-fields search
curl -X GET "localhost:9200/products/_search" \
  -H "Content-Type: application/json" \
  -d '{
    "query": {
      "multi_match": {
        "query": "bluetooth wireless",
        "fields": ["name", "description"],
        "type": "cross_fields",
        "operator": "and"
      }
    }
  }'

# Phrase search в нескольких полях
curl -X GET "localhost:9200/products/_search" \
  -H "Content-Type: application/json" \
  -d '{
    "query": {
      "multi_match": {
        "query": "noise cancelling headphones",
        "fields": ["name", "description"],
        "type": "phrase",
        "slop": 1
      }
    }
  }'
```

### Query String Query

#### Advanced Query Syntax
```bash
# Query string syntax
curl -X GET "localhost:9200/products/_search" \
  -H "Content-Type: application/json" \
  -d '{
    "query": {
      "query_string": {
        "query": "(wireless OR bluetooth) AND headphones -cheap",
        "fields": ["name", "description"],
        "default_operator": "AND"
      }
    }
  }'

# Сложные запросы
curl -X GET "localhost:9200/products/_search" \
  -H "Content-Type: application/json" \
  -d '{
    "query": {
      "query_string": {
        "query": "name:(wireless headphones) AND price:[100 TO 500] AND category:electronics",
        "analyze_wildcard": true,
        "allow_leading_wildcard": false
      }
    }
  }'
```

## Compound Queries

### Bool Query

#### Boolean Logic
```bash
# Must, should, must_not, filter
curl -X GET "localhost:9200/products/_search" \
  -H "Content-Type: application/json" \
  -d '{
    "query": {
      "bool": {
        "must": [
          {"match": {"category": "electronics"}}
        ],
        "should": [
          {"match": {"name": "wireless"}},
          {"match": {"name": "bluetooth"}}
        ],
        "must_not": [
          {"term": {"status": "discontinued"}}
        ],
        "filter": [
          {"range": {"price": {"gte": 50}}}
        ],
        "minimum_should_match": 1
      }
    }
  }'
```

#### Nested Bool Queries
```bash
# Сложные логические выражения
curl -X GET "localhost:9200/products/_search" \
  -H "Content-Type: application/json" \
  -d '{
    "query": {
      "bool": {
        "should": [
          {
            "bool": {
              "must": [
                {"term": {"category": "electronics"}},
                {"range": {"price": {"lte": 100}}}
              ]
            }
          },
          {
            "bool": {
              "must": [
                {"term": {"category": "books"}},
                {"term": {"genre": "fiction"}}
              ]
            }
          }
        ]
      }
    }
  }'
```

### Boosting Query

#### Negative Boosting
```bash
# Повышение релевантности одних документов над другими
curl -X GET "localhost:9200/products/_search" \
  -H "Content-Type: application/json" \
  -d '{
    "query": {
      "boosting": {
        "positive": {
          "match": {
            "name": "wireless"
          }
        },
        "negative": {
          "term": {
            "category": "budget"
          }
        },
        "negative_boost": 0.2
      }
    }
  }'
```

### Constant Score Query

#### Filter as Query
```bash
# Преобразование фильтра в запрос с постоянным score
curl -X GET "localhost:9200/products/_search" \
  -H "Content-Type: application/json" \
  -d '{
    "query": {
      "constant_score": {
        "filter": {
          "term": {
            "category": "electronics"
          }
        },
        "boost": 1.5
      }
    }
  }'

# Использование для кэшированных фильтров
curl -X GET "localhost:9200/products/_search" \
  -H "Content-Type: application/json" \
  -d '{
    "query": {
      "constant_score": {
        "filter": {
          "bool": {
            "must": [
              {"range": {"price": {"gte": 100}}},
              {"term": {"status": "active"}}
            ]
          }
        }
      }
    }
  }'
```

### Disjunction Max Query

#### Best Fields Search
```bash
# Поиск лучшего поля
curl -X GET "localhost:9200/products/_search" \
  -H "Content-Type: application/json" \
  -d '{
    "query": {
      "dis_max": {
        "queries": [
          {"match": {"name": "wireless headphones"}},
          {"match": {"description": "wireless headphones"}}
        ],
        "tie_breaker": 0.3
      }
    }
  }'
```

## Geo Queries

### Geo-point Queries

#### Geo Distance Query
```bash
# Поиск в радиусе
curl -X GET "localhost:9200/stores/_search" \
  -H "Content-Type: application/json" \
  -d '{
    "query": {
      "geo_distance": {
        "distance": "10km",
        "location": {
          "lat": 40.7128,
          "lon": -74.0060
        }
      }
    }
  }'

# Сортировка по расстоянию
curl -X GET "localhost:9200/stores/_search" \
  -H "Content-Type: application/json" \
  -d '{
    "query": {
      "geo_distance": {
        "distance": "50km",
        "location": [40.7128, -74.0060]
      }
    },
    "sort": [
      {
        "_geo_distance": {
          "location": [40.7128, -74.0060],
          "order": "asc",
          "unit": "km"
        }
      }
    ]
  }'
```

#### Geo Bounding Box
```bash
# Прямоугольная область
curl -X GET "localhost:9200/stores/_search" \
  -H "Content-Type: application/json" \
  -d '{
    "query": {
      "geo_bounding_box": {
        "location": {
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
  }'

# Well-known text (WKT)
curl -X GET "localhost:9200/stores/_search" \
  -H "Content-Type: application/json" \
  -d '{
    "query": {
      "geo_shape": {
        "location": {
          "shape": "POLYGON((-74.1 40.8, -73.9 40.8, -73.9 40.6, -74.1 40.6, -74.1 40.8))",
          "relation": "within"
        }
      }
    }
  }'
```

### Geo-shape Queries

#### Complex Shapes
```bash
# Geo shape с polygon
curl -X GET "localhost:9200/regions/_search" \
  -H "Content-Type: application/json" \
  -d '{
    "query": {
      "geo_shape": {
        "boundary": {
          "shape": {
            "type": "polygon",
            "coordinates": [[
              [-74.1, 40.8],
              [-73.9, 40.8],
              [-73.9, 40.6],
              [-74.1, 40.6],
              [-74.1, 40.8]
            ]]
          },
          "relation": "intersects"
        }
      }
    }
  }'

# Geo shape с stored shapes
curl -X GET "localhost:9200/locations/_search" \
  -H "Content-Type: application/json" \
  -d '{
    "query": {
      "geo_shape": {
        "location": {
          "indexed_shape": {
            "index": "shapes",
            "id": "neighborhood123",
            "path": "geometry"
          },
          "relation": "within"
        }
      }
    }
  }'
```

## Nested Queries

### Nested Objects Search

#### Nested Query
```bash
# Поиск в nested объектах
curl -X GET "localhost:9200/products/_search" \
  -H "Content-Type: application/json" \
  -d '{
    "query": {
      "nested": {
        "path": "reviews",
        "query": {
          "bool": {
            "must": [
              {"match": {"reviews.comment": "excellent"}},
              {"range": {"reviews.rating": {"gte": 4}}}
            ]
          }
        }
      }
    }
  }'

# Nested с inner hits
curl -X GET "localhost:9200/products/_search" \
  -H "Content-Type: application/json" \
  -d '{
    "query": {
      "nested": {
        "path": "reviews",
        "query": {
          "match": {"reviews.comment": "great"}
        },
        "inner_hits": {}
      }
    }
  }'
```

#### Parent-Child Relationships
```bash
# Join queries (parent-child)
curl -X GET "localhost:9200/products/_search" \
  -H "Content-Type: application/json" \
  -d '{
    "query": {
      "has_child": {
        "type": "review",
        "query": {
          "range": {
            "rating": {
              "gte": 4
            }
          }
        }
      }
    }
  }'

# Child с parent
curl -X GET "localhost:9200/reviews/_search" \
  -H "Content-Type: application/json" \
  -d '{
    "query": {
      "has_parent": {
        "parent_type": "product",
        "query": {
          "term": {
            "category": "electronics"
          }
        }
      }
    }
  }'
```

## Function Score Queries

### Score Modification

#### Boost by Field Value
```bash
# Boost на основе числового поля
curl -X GET "localhost:9200/products/_search" \
  -H "Content-Type: application/json" \
  -d '{
    "query": {
      "function_score": {
        "query": {
          "match": {
            "name": "headphones"
          }
        },
        "boost": "5",
        "functions": [
          {
            "field_value_factor": {
              "field": "rating",
              "factor": 1.2,
              "modifier": "sqrt"
            }
          }
        ],
        "boost_mode": "multiply"
      }
    }
  }'
```

#### Time-based Decay
```bash
# Decay по времени (свежие документы получают boost)
curl -X GET "localhost:9200/news/_search" \
  -H "Content-Type: application/json" \
  -d '{
    "query": {
      "function_score": {
        "query": {
          "match": {
            "content": "technology"
          }
        },
        "functions": [
          {
            "exp": {
              "publish_date": {
                "origin": "now",
                "scale": "7d",
                "decay": 0.5
              }
            }
          }
        ]
      }
    }
  }'
```

#### Custom Scoring Scripts
```bash
# Пользовательские scoring функции
curl -X GET "localhost:9200/products/_search" \
  -H "Content-Type: application/json" \
  -d '{
    "query": {
      "function_score": {
        "query": {
          "match_all": {}
        },
        "functions": [
          {
            "script_score": {
              "script": {
                "source": "Math.log(1 + doc['popularity'].value) * (1 / (1 + doc['price'].value / 100))"
              }
            }
          }
        ],
        "boost_mode": "replace"
      }
    }
  }'
```

## Filtering и Post-filtering

### Pre-filtering vs Post-filtering

#### Search with Filters
```bash
# Pre-filtering (рекомендуется)
curl -X GET "localhost:9200/products/_search" \
  -H "Content-Type: application/json" \
  -d '{
    "query": {
      "bool": {
        "must": [
          {"match": {"name": "headphones"}}
        ],
        "filter": [
          {"term": {"category": "electronics"}},
          {"range": {"price": {"gte": 50, "lte": 200}}}
        ]
      }
    }
  }'

# Post-filtering (для фасетов)
curl -X GET "localhost:9200/products/_search" \
  -H "Content-Type: application/json" \
  -d '{
    "query": {
      "match": {
        "name": "headphones"
      }
    },
    "post_filter": {
      "bool": {
        "must": [
          {"term": {"category": "electronics"}},
          {"range": {"price": {"gte": 50, "lte": 200}}}
        ]
      }
    },
    "aggs": {
      "categories": {
        "terms": {"field": "category"}
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
  }'
```

## Sorting и Relevance

### Sort Options

#### Field-based Sorting
```bash
# Сортировка по полю
curl -X GET "localhost:9200/products/_search" \
  -H "Content-Type: application/json" \
  -d '{
    "query": {"match_all": {}},
    "sort": [
      {"price": "asc"},
      {"rating": "desc"},
      {"_score": "desc"}
    ]
  }'

# Сортировка с missing values
curl -X GET "localhost:9200/products/_search" \
  -H "Content-Type: application/json" \
  -d '{
    "query": {"match_all": {}},
    "sort": [
      {
        "rating": {
          "order": "desc",
          "missing": "_last"
        }
      }
    ]
  }'
```

#### Script-based Sorting
```bash
# Сортировка с script
curl -X GET "localhost:9200/products/_search" \
  -H "Content-Type: application/json" \
  -d '{
    "query": {"match_all": {}},
    "sort": [
      {
        "_script": {
          "type": "number",
          "script": {
            "source": "doc['price'].value * (1 - doc['discount'].value / 100)"
          },
          "order": "asc"
        }
      }
    ]
  }'
```

### Relevance Tuning

#### Custom Scoring
```bash
# Explicit score
curl -X GET "localhost:9200/products/_search" \
  -H "Content-Type: application/json" \
  -d '{
    "query": {
      "function_score": {
        "query": {"match_all": {}},
        "functions": [
          {
            "filter": {"term": {"category": "electronics"}},
            "weight": 2
          },
          {
            "filter": {"range": {"rating": {"gte": 4}}},
            "weight": 1.5
          }
        ],
        "score_mode": "sum"
      }
    }
  }'
```

#### Explain API
```bash
# Получить объяснение scoring
curl -X GET "localhost:9200/products/_search?explain=true" \
  -H "Content-Type: application/json" \
  -d '{
    "query": {
      "match": {
        "name": "wireless headphones"
      }
    }
  }'

# Explain для конкретного документа
curl -X GET "localhost:9200/products/_explain/1" \
  -H "Content-Type: application/json" \
  -d '{
    "query": {
      "match": {
        "name": "wireless headphones"
      }
    }
  }'
```

## Pagination

### From/Size Pagination

#### Basic Pagination
```bash
# Первая страница
curl -X GET "localhost:9200/products/_search" \
  -H "Content-Type: application/json" \
  -d '{
    "query": {"match_all": {}},
    "from": 0,
    "size": 20
  }'

# Вторая страница
curl -X GET "localhost:9200/products/_search" \
  -H "Content-Type: application/json" \
  -d '{
    "query": {"match_all": {}},
    "from": 20,
    "size": 20
  }'
```

### Search After

#### Cursor-based Pagination
```bash
# Первая страница
curl -X GET "localhost:9200/products/_search" \
  -H "Content-Type: application/json" \
  -d '{
    "query": {"match_all": {}},
    "size": 20,
    "sort": [
      {"_id": "asc"}
    ]
  }'

# Вторая страница (используя последний _id первой страницы)
curl -X GET "localhost:9200/products/_search" \
  -H "Content-Type: application/json" \
  -d '{
    "query": {"match_all": {}},
    "size": 20,
    "search_after": ["product_20"],
    "sort": [
      {"_id": "asc"}
    ]
  }'
```

### Scroll API

#### Deep Pagination
```bash
# Инициализация scroll
curl -X GET "localhost:9200/products/_search?scroll=1m" \
  -H "Content-Type: application/json" \
  -d '{
    "query": {"match_all": {}},
    "size": 1000,
    "sort": ["_doc"]
  }'

# Получение следующей порции (используя scroll_id из ответа)
curl -X GET "localhost:9200/_search/scroll" \
  -H "Content-Type: application/json" \
  -d '{
    "scroll": "1m",
    "scroll_id": "DXF1ZXJ5QW5kRmV0Y2gBAAAAAAAAA..."
  }'

# Очистка scroll context
curl -X DELETE "localhost:9200/_search/scroll/DXF1ZXJ5QW5kRmV0Y2gBAAAAAAAAA..."
```

## Search Templates

### Mustache Templates

#### Template Definition
```bash
# Сохранение шаблона
curl -X PUT "localhost:9200/_scripts/product_search" \
  -H "Content-Type: application/json" \
  -d '{
    "script": {
      "lang": "mustache",
      "source": {
        "query": {
          "bool": {
            "must": [
              {{#search_term}}
              {"match": {"name": "{{search_term}}"}},
              {{/search_term}}
              {{#category}}
              {"term": {"category": "{{category}}"}},
              {{/category}}
              {{#min_price}}
              {"range": {"price": {"gte": {{min_price}}}}},
              {{/min_price}}
              {{#max_price}}
              {"range": {"price": {"lte": {{max_price}}}}},
              {{/max_price}}
            ]
          }
        },
        "from": "{{from}}",
        "size": "{{size}}"
      }
    }
  }'

# Использование шаблона
curl -X GET "localhost:9200/products/_search/template" \
  -H "Content-Type: application/json" \
  -d '{
    "id": "product_search",
    "params": {
      "search_term": "wireless",
      "category": "electronics",
      "min_price": 50,
      "max_price": 200,
      "from": 0,
      "size": 20
    }
  }'
```

### Stored Scripts

#### Script Management
```bash
# Список всех scripts
curl -X GET "localhost:9200/_scripts"

# Получение script
curl -X GET "localhost:9200/_scripts/product_search"

# Удаление script
curl -X DELETE "localhost:9200/_scripts/product_search"
```

## Query Profiling

### Profile API

#### Query Performance Analysis
```bash
# Профилирование запроса
curl -X GET "localhost:9200/products/_search" \
  -H "Content-Type: application/json" \
  -d '{
    "profile": true,
    "query": {
      "match": {
        "name": "wireless headphones"
      }
    }
  }'
```

#### Profile Response Analysis
```json
{
  "profile": {
    "shards": [
      {
        "id": "[shard_id]",
        "searches": [
          {
            "query": [
              {
                "type": "MatchQuery",
                "description": "name:wireless headphones",
                "time": "2.3ms",
                "breakdown": {
                  "score": 1234,
                  "build_scorer": 567,
                  "next_doc": 890,
                  "match": 123,
                  "create_weight": 456,
                  "compute_max_score": 78
                }
              }
            ],
            "rewrite_time": 1234,
            "collector": [
              {
                "name": "CancellableCollector",
                "reason": "search_cancelled",
                "time": "0.5ms"
              }
            ]
          }
        ]
      }
    ]
  }
}
```

## Java Search API

### High Level REST Client

#### Basic Search
```java
@Service
public class ElasticsearchSearchService {

    @Autowired
    private RestHighLevelClient client;

    public SearchResponse searchProducts(String query, int from, int size) throws IOException {
        SearchRequest searchRequest = new SearchRequest("products");
        SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();

        // Build query
        BoolQueryBuilder boolQuery = QueryBuilders.boolQuery();

        if (query != null && !query.trim().isEmpty()) {
            boolQuery.must(QueryBuilders.multiMatchQuery(query, "name", "description")
                .type(MultiMatchQueryBuilder.Type.BEST_FIELDS));
        }

        sourceBuilder.query(boolQuery);
        sourceBuilder.from(from);
        sourceBuilder.size(size);

        // Add sorting
        sourceBuilder.sort("_score", SortOrder.DESC);
        sourceBuilder.sort("price", SortOrder.ASC);

        searchRequest.source(sourceBuilder);

        return client.search(searchRequest, RequestOptions.DEFAULT);
    }

    public SearchResponse advancedSearch(String name, String category,
                                       BigDecimal minPrice, BigDecimal maxPrice,
                                       int page, int size) throws IOException {
        SearchRequest searchRequest = new SearchRequest("products");
        SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();

        BoolQueryBuilder boolQuery = QueryBuilders.boolQuery();

        // Name search
        if (name != null && !name.trim().isEmpty()) {
            boolQuery.must(QueryBuilders.matchQuery("name", name));
        }

        // Category filter
        if (category != null && !category.trim().isEmpty()) {
            boolQuery.filter(QueryBuilders.termQuery("category", category));
        }

        // Price range
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

        sourceBuilder.query(boolQuery);
        sourceBuilder.from(page * size);
        sourceBuilder.size(size);

        searchRequest.source(sourceBuilder);

        return client.search(searchRequest, RequestOptions.DEFAULT);
    }

    public List<Product> getSearchResults(SearchResponse response) {
        List<Product> products = new ArrayList<>();

        for (SearchHit hit : response.getHits().getHits()) {
            Map<String, Object> source = hit.getSourceAsMap();
            Product product = new Product();
            product.setId(hit.getId());
            product.setName((String) source.get("name"));
            product.setPrice(new BigDecimal(source.get("price").toString()));
            product.setCategory((String) source.get("category"));
            product.setScore(hit.getScore());

            products.add(product);
        }

        return products;
    }
}
```

### Java API Client 8.x

#### Modern Search API
```java
@Service
public class ModernElasticsearchSearchService {

    @Autowired
    private ElasticsearchClient client;

    public SearchResponse<Product> searchProducts(String query, int from, int size) throws IOException {
        return client.search(s -> s
            .index("products")
            .query(q -> q
                .bool(b -> b
                    .must(m -> m
                        .multiMatch(mm -> mm
                            .fields("name", "description")
                            .query(query)
                            .type(TextQueryType.BestFields)
                        )
                    )
                )
            )
            .from(from)
            .size(size)
            .sort(so -> so
                .score(sc -> sc.order(SortOrder.Desc))
            )
            .sort(so -> so
                .field(f -> f
                    .field("price")
                    .order(SortOrder.Asc)
                )
            ), Product.class);
    }

    public SearchResponse<Product> searchWithFilters(String category,
                                                   BigDecimal minPrice,
                                                   BigDecimal maxPrice) throws IOException {
        return client.search(s -> s
            .index("products")
            .query(q -> q
                .bool(b -> b
                    .filter(f -> f
                        .term(t -> t
                            .field("category")
                            .value(category)
                        )
                    )
                    .filter(f -> f
                        .range(r -> r
                            .field("price")
                            .gte(JsonData.of(minPrice))
                            .lte(JsonData.of(maxPrice))
                        )
                    )
                )
            ), Product.class);
    }

    public SearchResponse<Product> geoSearch(double lat, double lon, double radiusKm) throws IOException {
        return client.search(s -> s
            .index("stores")
            .query(q -> q
                .geoDistance(gd -> gd
                    .field("location")
                    .distance(radiusKm + "km")
                    .location(l -> l
                        .latlon(ll -> ll
                            .lat(lat)
                            .lon(lon)
                        )
                    )
                )
            )
            .sort(so -> so
                .geoDistance(gd -> gd
                    .field("location")
                    .location(l -> l
                        .latlon(ll -> ll
                            .lat(lat)
                            .lon(lon)
                        )
                    )
                    .order(SortOrder.Asc)
                    .unit(DistanceUnit.Kilometers)
                )
            ), Store.class);
    }

    public SearchResponse<Product> searchWithAggregations(String query) throws IOException {
        return client.search(s -> s
            .index("products")
            .query(q -> q
                .match(m -> m
                    .field("name")
                    .query(query)
                )
            )
            .aggregations("categories", a -> a
                .terms(t -> t
                    .field("category")
                )
            )
            .aggregations("price_ranges", a -> a
                .range(r -> r
                    .field("price")
                    .ranges(
                        ra -> ra.from("0.0").to("50.0"),
                        ra -> ra.from("50.0").to("100.0"),
                        ra -> ra.from("100.0").to("200.0"),
                        ra -> ra.from("200.0")
                    )
                )
            ), Product.class);
    }
}
```

## Best Practices

### Query Design

#### 1. Query Structure
- **Используй bool queries** для сложной логики
- **Фильтры для точных условий**, query для поиска
- **Избегай deep nesting** в bool queries
- **Используй named queries** для debugging

#### 2. Performance Optimization
- **Фильтры кэшируются**, используй их для повторяющихся условий
- **Pre-filter** когда возможно вместо post-filter
- **Используй routing** для targeted searches
- **Оптимизируй pagination** — search_after вместо from/size для deep pagination

#### 3. Relevance Tuning
- **Настраивай boost** для важных полей
- **Используй function_score** для custom scoring
- **Тестируй разные analyzers** для текстового поиска
- **Мониторь explain** для понимания scoring

### Search Patterns

#### 1. Multi-field Search
```json
{
  "query": {
    "multi_match": {
      "query": "wireless headphones",
      "fields": ["name^3", "description^2", "tags"],
      "type": "best_fields",
      "tie_breaker": 0.3
    }
  }
}
```

#### 2. Faceted Search
```json
{
  "query": {
    "match": {"name": "headphones"}
  },
  "post_filter": {
    "term": {"category": "electronics"}
  },
  "aggs": {
    "categories": {"terms": {"field": "category"}},
    "price_ranges": {
      "range": {
        "field": "price",
        "ranges": [
          {"to": 50}, {"from": 50, "to": 100}, {"from": 100}
        ]
      }
    }
  }
}
```

#### 3. Personalized Search
```json
{
  "query": {
    "function_score": {
      "query": {"match": {"name": "headphones"}},
      "functions": [
        {
          "filter": {"term": {"user_favorites": true}},
          "weight": 2
        },
        {
          "filter": {"term": {"category": "electronics"}},
          "weight": 1.5
        }
      ]
    }
  }
}
```

### Error Handling

#### 1. Timeout Management
```bash
# Query timeout
curl -X GET "localhost:9200/products/_search?timeout=5s" \
  -H "Content-Type: application/json" \
  -d '{"query": {"match_all": {}}}'
```

#### 2. Circuit Breaker
- **Настраивай timeouts** для предотвращения hanging queries
- **Используй retry logic** для transient errors
- **Monitor slow queries** и оптимизируй их
- **Implement fallbacks** для degraded search

### Monitoring

#### 1. Key Metrics
- **Query latency** (P50, P95, P99)
- **Query throughput** (queries per second)
- **Cache hit rates** (query cache, field data cache)
- **Error rates** (timeouts, failures)

#### 2. Search Slow Logs
```yaml
# elasticsearch.yml
index.search.slowlog.threshold.query.warn: 10s
index.search.slowlog.threshold.query.info: 5s
index.search.slowlog.threshold.query.debug: 2s
index.search.slowlog.threshold.query.trace: 500ms
```

## Заключение

Elasticsearch Query DSL предоставляет мощный и гибкий набор инструментов для поиска и аналитики данных. От простых term queries до сложных compound queries с агрегациями, Elasticsearch позволяет решать широкий спектр задач поиска.

### Ключевые возможности:

1. **Query DSL** — декларативный язык запросов
2. **Full-text search** — мощные текстовые запросы
3. **Structured queries** — точный поиск по полям
4. **Geo-spatial** — географические запросы
5. **Complex logic** — bool queries и compound operations
6. **Scoring control** — function score и relevance tuning
7. **Real-time** — near real-time search capabilities

### Архитектурные принципы:

1. **Inverted indexes** — основа быстрого поиска
2. **Segment-based storage** — эффективное хранение
3. **Distributed execution** — параллельная обработка
4. **Caching layers** — оптимизация производительности
5. **RESTful API** — стандартизированный интерфейс

### Производительность:

1. **Query optimization** — выбор правильных типов запросов
2. **Filtering strategy** — query vs filter context
3. **Pagination** — эффективная навигация по результатам
4. **Caching** — использование различных типов кэшей

### Java интеграция:

1. **High Level REST Client** — legacy client для complex operations
2. **Java API Client 8.x** — modern fluent API
3. **Spring Data Elasticsearch** — declarative data access
4. **Reactive support** — asynchronous operations

### Best practices:

1. **Query design** — правильная структура и оптимизация
2. **Performance monitoring** — метрики и profiling
3. **Error handling** — timeouts и circuit breakers
4. **Relevance tuning** — scoring и ranking optimization

Эффективное использование Elasticsearch требует глубокого понимания Query DSL и принципов работы поисковых систем. Правильное проектирование запросов обеспечивает высокую производительность и точность поиска. 🎯

**Продолжение следует:**
- ✅ elasticsearch-basics.md (завершен)
- ✅ elasticsearch-indexing.md (завершен)  
- ✅ elasticsearch-queries.md (завершен)
- 🔄 elasticsearch-aggregations.md
- 🔄 elasticsearch-clustering.md
- 🔄 elasticsearch-performance.md

Следующий файл - elasticsearch-aggregations.md! 🚀

