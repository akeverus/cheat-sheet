---
title: "Quarkus: Redis — Caching и Data Structures"
description: "Полное руководство по работе с Redis в Quarkus: caching, data structures, pub/sub, transactions, reactive Redis и best practices"
tags:
  - quarkus
  - redis
  - cache
  - pubsub
  - data-structures
  - java
difficulty: "intermediate"
prerequisites: ["quarkus/quarkus-basics.md", "quarkus/quarkus-cache.md"]
next: ["quarkus-cache.md", "quarkus-reactive.md"]
updated: "2026-04-20"
related: ["quarkus-cache.md", "quarkus-reactive.md"]
---

# Quarkus: Redis — Caching и Data Structures

## Полезные ссылки

[Официальная документация Quarkus](https://quarkus.io/guides/)
[Quarkus GitHub](https://github.com/quarkusio/quarkus)

## Содержание

- [Введение](#введение)
  - [Основные возможности](#основные-возможности)
- [Configuration](#configuration)
  - [Basic Configuration](#basic-configuration)
  - [Multiple Redis Instances](#multiple-redis-instances)
- [Redis Client](#redis-client)
  - [Synchronous Client](#synchronous-client)
  - [Reactive Client](#reactive-client)
- [Data Structures](#data-structures)
  - [Strings](#strings)
  - [Lists](#lists)
  - [Sets](#sets)
  - [Hashes](#hashes)
- [Pub/Sub](#pubsub)
  - [Publisher](#publisher)
  - [Subscriber](#subscriber)
- [Transactions](#transactions)
  - [Redis Transactions](#redis-transactions)
- [Лучшие практики](#лучшие-практики)
  - [1. Используйте reactive для неблокирующих операций](#1-используйте-reactive-для-неблокирующих-операций)
  - [2. Настраивайте connection pooling](#2-настраивайте-connection-pooling)
  - [3. Используйте подходящие data structures](#3-используйте-подходящие-data-structures)
  - [4. Используйте expiration для временных данных](#4-используйте-expiration-для-временных-данных)
- [Sorted Sets](#sorted-sets)
- [Advanced Patterns](#advanced-patterns)
  - [Distributed Lock](#distributed-lock)
  - [Rate Limiting](#rate-limiting)
- [Redis Clustering](#redis-clustering)
  - [Cluster Configuration](#cluster-configuration)
  - [Cluster Operations](#cluster-operations)
- [Redis Performance Tuning](#redis-performance-tuning)
  - [Pipeline Optimization](#pipeline-optimization)
  - [Connection Pool Tuning](#connection-pool-tuning)
- [Заключение](#заключение)
- [Дополнительные ресурсы](#дополнительные-ресурсы)
- [См. также](#см-также)

## Введение

**Quarkus** предоставляет мощную интеграцию с **Redis** через различные клиенты. Это позволяет использовать **Redis** для кеширования, хранения данных, **pub**/**sub** и других задач.

### Основные возможности

- **Redis Client**: Синхронный и асинхронный клиенты
- **Data Structures**: **Strings**, **Lists**, **Sets**, **Hashes**, **Sorted Sets**
- **Pub/Sub**: **Publish**/**Subscribe messaging**
- **Transactions**: **Redis transactions**
- **Reactive**: Реактивный доступ к **Redis**

## Configuration

### Basic Configuration

**application.properties:**

```properties
quarkus.redis.hosts=redis://localhost:6379
quarkus.redis.password=
quarkus.redis.database=0
```

### Multiple Redis Instances

```properties
quarkus.redis.hosts=redis://localhost:6379
quarkus.redis.secondary.hosts=redis://localhost:6380
```

## Redis Client

### Synchronous Client

**Использование синхронного клиента:**

```java
import io.quarkus.redis.client.RedisClient;
import jakarta.inject.Inject;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class RedisService {

    @Inject
    RedisClient redisClient;

    public void setValue(String key, String value) {
        redisClient.set(List.of(key, value));
    }

    public String getValue(String key) {
        return redisClient.get(key).toString();
    }
}
```

### Reactive Client

**Использование реактивного клиента:**

```java
import io.quarkus.redis.client.reactive.ReactiveRedisClient;
import io.smallrye.mutiny.Uni;
import jakarta.inject.Inject;

@ApplicationScoped
public class ReactiveRedisService {

    @Inject
    ReactiveRedisClient reactiveRedisClient;

    public Uni<Void> setValueReactive(String key, String value) {
        return reactiveRedisClient.set(List.of(key, value))
            .replaceWithVoid();
    }

    public Uni<String> getValueReactive(String key) {
        return reactiveRedisClient.get(key)
            .map(response -> response.toString());
    }
}
```

## Data Structures

### Strings

**Работа со строками:**

```java
@ApplicationScoped
public class StringOperations {

    @Inject
    RedisClient redisClient;

    public void stringOperations() {
        // SET
        redisClient.set(List.of("key", "value"));

        // GET
        String value = redisClient.get("key").toString();

        // SETEX (set with expiration)
        redisClient.setex("key", 60, "value");

        // INCR
        redisClient.incr("counter");

        // APPEND
        redisClient.append("key", "suffix");
    }
}
```

### Lists

**Работа со списками:**

```java
@ApplicationScoped
public class ListOperations {

    @Inject
    RedisClient redisClient;

    public void listOperations() {
        // LPUSH
        redisClient.lpush("list", "value1", "value2");

        // RPUSH
        redisClient.rpush("list", "value3");

        // LRANGE
        List<String> values = redisClient.lrange("list", 0, -1);

        // LPOP
        String value = redisClient.lpop("list").toString();

        // LLEN
        Long length = redisClient.llen("list");
    }
}
```

### Sets

**Работа с множествами:**

```java
@ApplicationScoped
public class SetOperations {

    @Inject
    RedisClient redisClient;

    public void setOperations() {
        // SADD
        redisClient.sadd("set", "member1", "member2");

        // SMEMBERS
        Set<String> members = redisClient.smembers("set");

        // SISMEMBER
        Boolean isMember = redisClient.sismember("set", "member1");

        // SREM
        redisClient.srem("set", "member1");
    }
}
```

### Hashes

**Работа с хешами:**

```java
@ApplicationScoped
public class HashOperations {

    @Inject
    RedisClient redisClient;

    public void hashOperations() {
        // HSET
        redisClient.hset("hash", "field1", "value1");

        // HGET
        String value = redisClient.hget("hash", "field1").toString();

        // HGETALL
        Map<String, String> hash = redisClient.hgetall("hash");

        // HDEL
        redisClient.hdel("hash", "field1");
    }
}
```

## Pub/Sub

### Publisher

**Публикация сообщений:**

```java
@ApplicationScoped
public class RedisPublisher {

    @Inject
    ReactiveRedisClient reactiveRedisClient;

    public Uni<Long> publish(String channel, String message) {
        return reactiveRedisClient.publish(channel, message);
    }
}
```

### Subscriber

**Подписка на каналы:**

```java
import io.quarkus.redis.client.reactive.ReactiveRedisClient;
import io.smallrye.mutiny.Multi;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class RedisSubscriber {

    @Inject
    ReactiveRedisClient reactiveRedisClient;

    public Multi<String> subscribe(String channel) {
        return reactiveRedisClient.subscribe(channel)
            .map(response -> response.toString());
    }
}
```

## Transactions

### Redis Transactions

**Использование транзакций:**

```java
@ApplicationScoped
public class TransactionalService {

    @Inject
    ReactiveRedisClient reactiveRedisClient;

    public Uni<List<String>> executeTransaction() {
        return reactiveRedisClient.multi()
            .chain(() -> reactiveRedisClient.set(List.of("key1", "value1")))
            .chain(() -> reactiveRedisClient.set(List.of("key2", "value2")))
            .chain(() -> reactiveRedisClient.exec());
    }
}
```

## Лучшие практики

### 1. Используйте reactive для неблокирующих операций

```java
// ✅ Хорошо
public Uni<String> getValueReactive(String key) {
    return reactiveRedisClient.get(key);
}
```

### 2. Настраивайте connection pooling

```properties
# ✅ Хорошо
quarkus.redis.max-pool-size=10
quarkus.redis.min-pool-size=2
```

### 3. Используйте подходящие data structures

```java
// ✅ Хорошо - для простых значений
redisClient.set("key", "value");

// ✅ Хорошо - для объектов
redisClient.hset("user:1", "name", "John", "email", "john@example.com");
```

### 4. Используйте expiration для временных данных

```java
// ✅ Хорошо
redisClient.setex("key", 3600, "value");  // 1 hour expiration
```

## Sorted Sets

**Работа с отсортированными множествами:**

```java
@ApplicationScoped
public class SortedSetOperations {

    @Inject
    RedisClient redisClient;

    public void sortedSetOperations() {
        // ZADD
        redisClient.zadd("sorted-set", 1.0, "member1");
        redisClient.zadd("sorted-set", 2.0, "member2");

        // ZRANGE
        List<String> members = redisClient.zrange("sorted-set", 0, -1);

        // ZRANK
        Long rank = redisClient.zrank("sorted-set", "member1");

        // ZSCORE
        Double score = redisClient.zscore("sorted-set", "member1");

        // ZREM
        redisClient.zrem("sorted-set", "member1");
    }
}
```

## Advanced Patterns

### Distributed Lock

**Реализация **distributed lock**:**

```java
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@ApplicationScoped
public class DistributedLockService {

    @Inject
    ReactiveRedisClient reactiveRedisClient;

    public Uni<Boolean> acquireLock(String key, int timeoutSeconds) {
        String lockValue = UUID.randomUUID().toString();
        return reactiveRedisClient.setnx(key, lockValue)
            .chain(result -> {
                if (result == 1) {
                    return reactiveRedisClient.expire(key, timeoutSeconds)
                        .map(expireResult -> expireResult == 1);
                }
                return Uni.createFrom().item(false);
            });
    }

    public Uni<Void> releaseLock(String key, String lockValue) {
        return reactiveRedisClient.get(key)
            .chain(value -> {
                if (lockValue.equals(value.toString())) {
                    return reactiveRedisClient.del(key).replaceWithVoid();
                }
                return Uni.createFrom().voidItem();
            });
    }
}
```

### Rate Limiting

**Реализация **rate limiting**:**

```java
@ApplicationScoped
public class RateLimitingService {

    @Inject
    ReactiveRedisClient reactiveRedisClient;

    public Uni<Boolean> isAllowed(String key, int maxRequests, int windowSeconds) {
        String rateLimitKey = "ratelimit:" + key;
        return reactiveRedisClient.incr(rateLimitKey)
            .chain(count -> {
                if (count == 1) {
                    return reactiveRedisClient.expire(rateLimitKey, windowSeconds)
                        .map(expireResult -> true);
                }
                return Uni.createFrom().item(count <= maxRequests);
            });
    }
}
```

## Redis Clustering

### Cluster Configuration

**Настройка кластера:**

```properties
quarkus.redis.hosts=redis://node1:6379,redis://node2:6379,redis://node3:6379
quarkus.redis.cluster-mode=true
```

### Cluster Operations

**Операции с кластером:**

```java
@ApplicationScoped
public class ClusterRedisService {

    @Inject
    ReactiveRedisClient redisClient;

    public Uni<Void> setInCluster(String key, String value) {
        // Redis автоматически определяет нужный узел
        return redisClient.set(List.of(key, value)).replaceWithVoid();
    }
}
```

## Redis Performance Tuning

### Pipeline Optimization

**Оптимизация **pipeline**:**

```java
@ApplicationScoped
public class PipelineOptimization {

    @Inject
    ReactiveRedisClient redisClient;

    public Uni<List<Response>> batchGet(List<String> keys) {
        ReactiveTransaction transaction = redisClient.multi();
        keys.forEach(key -> transaction.get(key));
        return transaction.exec();
    }
}
```

### Connection Pool Tuning

**Настройка пула соединений:**

```properties
quarkus.redis.max-pool-size=20
quarkus.redis.min-pool-size=5
quarkus.redis.max-pool-waiting=24
```


## Заключение

**Quarkus Redis** предоставляет мощные инструменты для работы с **Redis**. Поддержка синхронного и реактивного клиентов, различных **data structures**, **pub**/**sub**, **transactions**, **distributed locks**, **rate limiting** и других продвинутых возможностей позволяет эффективно использовать **Redis** для различных задач. Правильное использование **Redis** паттернов, выбор подходящих **data structures** и оптимизация производительности являются ключевыми аспектами создания эффективных приложений с **Redis**.

## Дополнительные ресурсы

- [**Quarkus Redis** Guide](https://quarkus.io/guides/redis)
- [**Redis** Documentation](https://redis.io/docs/)
- [**Redis Data Types**](https://redis.io/docs/data-types/)
- [**Redis** Commands](https://redis.io/commands/)

## См. также

- [[quarkus-actuator|Quarkus: Actuator — Health Checks и Metrics]]
- [[quarkus-basics|Quarkus: Основы]]
- [[quarkus-cache|Quarkus: Cache — Кеширование данных]]
- [[quarkus-cloud|Quarkus: Cloud Native — Kubernetes, OpenShift и Service Mesh]]
- [[quarkus-core|Quarkus: Core — CDI, Bean Scopes и Configuration]]
