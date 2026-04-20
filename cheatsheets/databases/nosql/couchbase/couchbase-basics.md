---
title: "Couchbase: Основы"
description: "Couchbase — распределённая NoSQL БД: архитектура bucket/scope/collection, N1QL запросы, индексы, Java SDK, репликация."
tags:
  - databases
  - nosql
  - couchbase
difficulty: "intermediate"
updated: "2026-04-20"
---
# Couchbase: Основы

Couchbase — распределённая NoSQL БД, совмещающая key-value хранилище, документную модель и SQL-подобный язык N1QL.

## Полезные ссылки

### Официальная документация
- [Couchbase Documentation](https://docs.couchbase.com/) — официальная документация

### См. также
- [MongoDB](../mongodb/mongodb-basics.md) — документная NoSQL БД
- [Redis](../redis/redis-basics.md) — key-value хранилище
- [Apache Cassandra](../cassandra/cassandra-basics.md) — wide-column NoSQL
- [MongoDB CRUD](../mongodb/mongodb-crud.md) — паттерны работы с документами

## Содержание

- [Архитектура](#архитектура)
- [Запуск в Docker](#запуск-в-docker)
- [N1QL — SQL для JSON](#n1ql-sql-для-json)
- [Индексы](#индексы)
- [Java SDK 3.x](#java-sdk-3x)
- [Spring Data Couchbase](#spring-data-couchbase)
- [Репликация и отказоустойчивость](#репликация-и-отказоустойчивость)
- [Full-Text Search](#full-text-search)
- [Типичные проблемы](#типичные-проблемы)

## Архитектура

```text
Cluster
└── Bucket (логическое пространство, аналог БД)
    └── Scope (пространство имён, аналог схемы)
        └── Collection (аналог таблицы, хранит JSON-документы)
```

- **Bucket** — top-level контейнер; типы: Couchbase (персистентный), Ephemeral (in-memory), Memcached.
- **Scope / Collection** — добавлены в Couchbase 7.x; до этого всё хранилось в `_default._default`.
- **Document** — JSON с уникальным строковым ключом (до 250 байт).

## Запуск в Docker

```bash
docker run -d --name couchbase \
  -p 8091-8096:8091-8096 \
  -p 11210-11211:11210-11211 \
  couchbase:enterprise-7.6.0
# Веб-консоль: http://localhost:8091
```

## N1QL — SQL для JSON

```sql
-- Базовый SELECT
SELECT name, email, address.city AS city
FROM `users`._default._default
WHERE age > 25
ORDER BY name
LIMIT 10;

-- INSERT
INSERT INTO `users`._default._default (KEY, VALUE)
VALUES ("user:1001", {"name": "Alice", "age": 30, "email": "alice@example.com"});

-- UPSERT (вставка или замена)
UPSERT INTO `users`._default._default (KEY, VALUE)
VALUES ("user:1001", {"name": "Alice", "age": 31});

-- UPDATE
UPDATE `users`._default._default
SET age = age + 1
WHERE META().id = "user:1001"
RETURNING name, age;

-- DELETE
DELETE FROM `users`._default._default
WHERE META().id = "user:1001";

-- JOIN двух коллекций
SELECT o.orderId, c.name AS customer
FROM `orders`._default._default AS o
JOIN `users`._default._default AS c ON KEYS o.customerId
WHERE o.status = 'pending';

-- UNNEST — разворот массива внутри документа
SELECT t.name, r.day, r.flight
FROM `travel-sample`.inventory.airline AS t
UNNEST t.schedule AS r
WHERE r.day = 0;

-- Агрегация
SELECT country, COUNT(*) AS cnt
FROM `travel-sample`.inventory.airline
GROUP BY country
HAVING COUNT(*) > 5;
```

## Индексы

```sql
-- Primary index (полное сканирование — только для разработки/отладки)
CREATE PRIMARY INDEX ON `users`;

-- Secondary index на поле
CREATE INDEX idx_user_email ON `users`._default._default(email);

-- Составной индекс с фильтром
CREATE INDEX idx_order_status_date
  ON `orders`._default._default(status, createdAt DESC)
  WHERE status IN ['pending', 'processing'];

-- EXPLAIN для проверки плана
EXPLAIN SELECT * FROM `users`._default._default WHERE email = 'a@b.com';
```

## Java SDK 3.x

```xml
<dependency>
  <groupId>com.couchbase.client</groupId>
  <artifactId>java-client</artifactId>
  <version>3.6.0</version>
</dependency>
```

```java
// Подключение
Cluster cluster = Cluster.connect("localhost",
    ClusterOptions.clusterOptions("admin", "password"));
Bucket bucket = cluster.bucket("users");
bucket.waitUntilReady(Duration.ofSeconds(10));
Collection collection = bucket.defaultCollection();

// Get
GetResult result = collection.get("user:1001");
JsonObject user = result.contentAsObject();

// Upsert
JsonObject doc = JsonObject.create()
    .put("name", "Bob")
    .put("age", 25);
collection.upsert("user:1002", doc);

// Sub-document — обновление отдельного поля без загрузки документа
collection.mutateIn("user:1002", List.of(
    MutateInSpec.upsert("address.city", "Moscow"),
    MutateInSpec.increment("loginCount", 1)
));

// N1QL из Java
QueryResult queryResult = cluster.query(
    "SELECT name FROM `users`._default._default WHERE age > $minAge",
    QueryOptions.queryOptions()
        .parameters(JsonObject.create().put("minAge", 25))
        .scanConsistency(QueryScanConsistency.REQUEST_PLUS)
);
for (JsonObject row : queryResult.rowsAsObject()) {
    System.out.println(row.getString("name"));
}

// TTL — документ живёт 1 час
collection.upsert("session:abc",
    JsonObject.create().put("userId", "user:1"),
    UpsertOptions.upsertOptions().expiry(Duration.ofHours(1)));
```

## Spring Data Couchbase

```yaml
spring:
  couchbase:
    connection-string: localhost
    username: admin
    password: password
  data:
    couchbase:
      bucket-name: users
      auto-index: true
```

```java
@Document
public class User {
    @Id
    private String id;
    @Field private String name;
    @Field private String email;
    @Field private int age;
}

public interface UserRepository extends CouchbaseRepository<User, String> {
    List<User> findByAge(int age);

    @Query("#{#n1ql.selectEntity} WHERE #{#n1ql.filter} AND email = $1")
    Optional<User> findByEmail(String email);
}
```

## Репликация и отказоустойчивость

- **vBuckets** — 1024 виртуальных сегмента; ключи распределяются по CRC32 хешу.
- **Replication factor** — 1–3 реплики на bucket.
- **Failover** — автоматический при потере ноды; данные восстанавливаются из реплик.
- **XDCR** (Cross Datacenter Replication) — асинхронная репликация между кластерами.
- **Durability levels**: `None`, `Majority`, `MajorityAndPersistOnMaster`, `PersistToMajority`.

## Full-Text Search

```java
SearchResult sr = cluster.searchQuery(
    "user-search-index",
    SearchQuery.match("Alice").field("name"),
    SearchOptions.searchOptions().limit(10).highlight()
);
for (SearchRow row : sr.rows()) {
    System.out.println(row.id() + " score=" + row.score());
}
```

## Типичные проблемы

| Симптом | Причина | Решение |
|---------|---------|---------|
| `DocumentNotFoundException` | Документ не существует | Использовать `getOptional` или `try/catch` |
| N1QL медленный | Нет secondary index | `EXPLAIN` запрос; создать индекс по полям WHERE |
| `TempFailException` | Перегрузка, нехватка памяти | Retry с jitter; увеличить `memoryQuota` |
| Outdated read в N1QL | Eventual consistency | `QueryScanConsistency.REQUEST_PLUS` |
| Потеря данных при failover | `replicateTo=0` | Установить Durability `Majority` или выше |

