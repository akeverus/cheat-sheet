---
title: "Micronaut: Redis Integration - RedisTemplate, Pub/Sub и Cache"
description: "Полное руководство по интеграции с Redis в Micronaut: RedisTemplate, pub/sub, cache, transactions и best practices"
tags:
  - micronaut
  - redis
  - cache
  - pub-sub
  - messaging
  - java
  - kotlin
difficulty: "intermediate"
prerequisites: ["micronaut/micronaut-basics.md", "micronaut/micronaut-cache.md"]
next: ["micronaut-cache.md", "micronaut-data.md"]
updated: "2026-04-20"
related: ["micronaut-cache.md", "micronaut-messaging.md"]
---

# Micronaut: Redis Integration - RedisTemplate, Pub/Sub и Cache

## Полезные ссылки

[Официальная документация Micronaut](https://docs.micronaut.io/)
[Micronaut GitHub](https://github.com/micronaut-projects/micronaut-core)

## Содержание

- [Введение](#введение)
  - [Основные возможности](#основные-возможности)
- [Настройка Redis](#настройка-redis)
  - [Зависимости](#зависимости)
  - [Конфигурация](#конфигурация)
- [RedisTemplate](#redistemplate)
  - [Basic Operations](#basic-operations)
  - [Hash Operations](#hash-operations)
  - [List Operations](#list-operations)
- [Pub/Sub](#pubsub)
  - [Publisher](#publisher)
  - [Subscriber](#subscriber)
- [Distributed Locks](#distributed-locks)
  - [Lock Implementation](#lock-implementation)
- [Transactions](#transactions)
  - [Redis Transactions](#redis-transactions)
- [Лучшие практики](#лучшие-практики)
  - [1. Используйте connection pooling](#1-используйте-connection-pooling)
  - [2. Настраивайте timeout правильно](#2-настраивайте-timeout-правильно)
  - [3. Используйте pub/sub для событий](#3-используйте-pubsub-для-событий)
  - [4. Используйте distributed locks для критических секций](#4-используйте-distributed-locks-для-критических-секций)
  - [5. Обрабатывайте ошибки Redis](#5-обрабатывайте-ошибки-redis)
- [Set Operations](#set-operations)
  - [Set Operations](#set-operations-1)
- [Sorted Set Operations](#sorted-set-operations)
  - [Sorted Set Operations](#sorted-set-operations-1)
- [Lua Scripts](#lua-scripts)
  - [Executing Lua Scripts](#executing-lua-scripts)
- [Redis Cluster](#redis-cluster)
  - [Cluster Configuration](#cluster-configuration)
- [Redis Sentinel](#redis-sentinel)
  - [Sentinel Configuration](#sentinel-configuration)
- [Redis Pipeline](#redis-pipeline)
  - [Pipeline Operations](#pipeline-operations)
- [Redis Streams](#redis-streams)
  - [Stream Operations](#stream-operations)
- [Redis Bitmaps](#redis-bitmaps)
  - [Bitmap Operations](#bitmap-operations)
- [Redis HyperLogLog](#redis-hyperloglog)
  - [HyperLogLog Operations](#hyperloglog-operations)
- [Заключение](#заключение)
- [Дополнительные ресурсы](#дополнительные-ресурсы)
- [См. также](#см-также)

## Введение

**Micronaut** предоставляет отличную поддержку **Redis** через **Micronaut Redis**. Это позволяет использовать **Redis** для кэширования, **pub**/**sub messaging**, **distributed locks** и других задач.

### Основные возможности

- **RedisTemplate**: Низкоуровневый доступ к **Redis**
- **Pub/Sub**: Публикация и подписка на сообщения
- **Cache**: Кэширование данных
- **Transactions**: Транзакции **Redis**
- **Distributed Locks**: Распределенные блокировки

## Настройка Redis

### Зависимости

**build.gradle:**

```gradle
dependencies {
    implementation("io.micronaut.redis:micronaut-redis-lettuce")
    // или
    implementation("io.micronaut.redis:micronaut-redis-jedis")
}
```

### Конфигурация

**application.yml:**

```yaml
redis:
  uri: redis://localhost:6379
  timeout: 2000ms
  pool:
    max-active: 8
    max-idle: 8
    min-idle: 0
```

## RedisTemplate

### Basic Operations

```java
import io.micronaut.redis.RedisOperations;
import jakarta.inject.Singleton;

@Singleton
public class RedisService {
    private final RedisOperations<String, String> redisOperations;

    public RedisService(RedisOperations<String, String> redisOperations) {
        this.redisOperations = redisOperations;
    }

    public void setValue(String key, String value) {
        redisOperations.set(key, value);
    }

    public Optional<String> getValue(String key) {
        return redisOperations.get(key);
    }

    public void deleteValue(String key) {
        redisOperations.del(key);
    }

    public boolean exists(String key) {
        return redisOperations.exists(key);
    }

    public void setExpiry(String key, Duration expiry) {
        redisOperations.expire(key, expiry);
    }
}
```

### Hash Operations

```java
import io.micronaut.redis.RedisHashOperations;
import jakarta.inject.Singleton;

@Singleton
public class RedisHashService {
    private final RedisHashOperations<String, String, String> hashOperations;

    public RedisHashService(RedisHashOperations<String, String, String> hashOperations) {
        this.hashOperations = hashOperations;
    }

    public void setHashValue(String key, String field, String value) {
        hashOperations.put(key, field, value);
    }

    public Optional<String> getHashValue(String key, String field) {
        return hashOperations.get(key, field);
    }

    public Map<String, String> getAllHashValues(String key) {
        return hashOperations.entries(key);
    }

    public void deleteHashField(String key, String field) {
        hashOperations.remove(key, field);
    }
}
```

### List Operations

```java
import io.micronaut.redis.RedisListOperations;
import jakarta.inject.Singleton;

@Singleton
public class RedisListService {
    private final RedisListOperations<String, String> listOperations;

    public RedisListService(RedisListOperations<String, String> listOperations) {
        this.listOperations = listOperations;
    }

    public void pushLeft(String key, String value) {
        listOperations.leftPush(key, value);
    }

    public void pushRight(String key, String value) {
        listOperations.rightPush(key, value);
    }

    public Optional<String> popLeft(String key) {
        return listOperations.leftPop(key);
    }

    public Optional<String> popRight(String key) {
        return listOperations.rightPop(key);
    }

    public List<String> getRange(String key, long start, long end) {
        return listOperations.range(key, start, end);
    }
}
```

## Pub/Sub

### Publisher

```java
import io.micronaut.redis.RedisOperations;
import jakarta.inject.Singleton;

@Singleton
public class RedisPublisher {
    private final RedisOperations<String, String> redisOperations;

    public RedisPublisher(RedisOperations<String, String> redisOperations) {
        this.redisOperations = redisOperations;
    }

    public void publish(String channel, String message) {
        redisOperations.publish(channel, message);
    }
}
```

### Subscriber

```java
import io.micronaut.redis.RedisOperations;
import io.micronaut.redis.annotation.RedisListener;
import jakarta.inject.Singleton;

@Singleton
public class RedisSubscriber {

    @RedisListener("user-events")
    public void onUserEvent(String message) {
        System.out.println("Received message: " + message);
        // Обработка сообщения
    }

    @RedisListener("order-events")
    public void onOrderEvent(String message) {
        System.out.println("Received order event: " + message);
        // Обработка события заказа
    }
}
```

## Distributed Locks

### Lock Implementation

```java
import io.micronaut.redis.RedisOperations;
import jakarta.inject.Singleton;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Singleton
public class DistributedLockService {
    private final RedisOperations<String, String> redisOperations;

    public DistributedLockService(RedisOperations<String, String> redisOperations) {
        this.redisOperations = redisOperations;
    }

    public String acquireLock(String lockKey, Duration timeout) {
        String lockValue = UUID.randomUUID().toString();
        String result = redisOperations.set(lockKey, lockValue,
            SetArgs.Builder.nx().ex(timeout));

        if ("OK".equals(result)) {
            return lockValue;
        }
        return null;
    }

    public void releaseLock(String lockKey, String lockValue) {
        String script =
            "if redis.call('get', KEYS[1]) == ARGV[1] then " +
            "return redis.call('del', KEYS[1]) " +
            "else return 0 end";
        redisOperations.eval(script, Collections.singletonList(lockKey),
            Collections.singletonList(lockValue));
    }
}
```

## Transactions

### Redis Transactions

```java
import io.micronaut.redis.RedisOperations;
import jakarta.inject.Singleton;

@Singleton
public class RedisTransactionService {
    private final RedisOperations<String, String> redisOperations;

    public RedisTransactionService(RedisOperations<String, String> redisOperations) {
        this.redisOperations = redisOperations;
    }

    public void executeTransaction() {
        redisOperations.multi();
        try {
            redisOperations.set("key1", "value1");
            redisOperations.set("key2", "value2");
            redisOperations.exec();
        } catch (Exception e) {
            redisOperations.discard();
            throw e;
        }
    }
}
```

## Лучшие практики

### 1. Используйте connection pooling

```yaml
# ✅ Хорошо
redis:
  pool:
    max-active: 8
    max-idle: 8
```

### 2. Настраивайте timeout правильно

```yaml
# ✅ Хорошо
redis:
  timeout: 2000ms
```

### 3. Используйте pub/sub для событий

```java
// ✅ Хорошо
@RedisListener("events")
public void onEvent(String message) {
    // Обработка события
}
```

### 4. Используйте distributed locks для критических секций

```java
// ✅ Хорошо
String lockValue = lockService.acquireLock("resource", Duration.ofSeconds(30));
try {
    // Критическая секция
} finally {
    lockService.releaseLock("resource", lockValue);
}
```

### 5. Обрабатывайте ошибки Redis

```java
// ✅ Хорошо
try {
    redisOperations.set(key, value);
} catch (RedisException e) {
    log.error("Redis error", e);
    // Fallback логика
}
```

## Set Operations

### Set Operations

```java
import io.micronaut.redis.RedisSetOperations;
import jakarta.inject.Singleton;

@Singleton
public class RedisSetService {
    private final RedisSetOperations<String, String> setOperations;

    public RedisSetService(RedisSetOperations<String, String> setOperations) {
        this.setOperations = setOperations;
    }

    public void addToSet(String key, String... values) {
        setOperations.add(key, values);
    }

    public Set<String> getSetMembers(String key) {
        return setOperations.members(key);
    }

    public boolean isMember(String key, String value) {
        return setOperations.isMember(key, value);
    }

    public void removeFromSet(String key, String... values) {
        setOperations.remove(key, values);
    }
}
```

## Sorted Set Operations

### Sorted Set Operations

```java
import io.micronaut.redis.RedisSortedSetOperations;
import jakarta.inject.Singleton;

@Singleton
public class RedisSortedSetService {
    private final RedisSortedSetOperations<String, String> sortedSetOperations;

    public RedisSortedSetService(RedisSortedSetOperations<String, String> sortedSetOperations) {
        this.sortedSetOperations = sortedSetOperations;
    }

    public void addToSortedSet(String key, double score, String value) {
        sortedSetOperations.add(key, score, value);
    }

    public List<String> getRange(String key, long start, long end) {
        return sortedSetOperations.range(key, start, end);
    }

    public List<String> getRangeByScore(String key, double min, double max) {
        return sortedSetOperations.rangeByScore(key, min, max);
    }
}
```

## Lua Scripts

### Executing Lua Scripts

```java
import io.micronaut.redis.RedisOperations;
import jakarta.inject.Singleton;

@Singleton
public class LuaScriptService {
    private final RedisOperations<String, String> redisOperations;

    public LuaScriptService(RedisOperations<String, String> redisOperations) {
        this.redisOperations = redisOperations;
    }

    public Long incrementWithLimit(String key, long limit) {
        String script =
            "local current = redis.call('get', KEYS[1]) " +
            "if current == false then " +
            "  current = 0 " +
            "end " +
            "if tonumber(current) < tonumber(ARGV[1]) then " +
            "  return redis.call('incr', KEYS[1]) " +
            "else " +
            "  return tonumber(current) " +
            "end";

        return redisOperations.eval(script,
            Collections.singletonList(key),
            Collections.singletonList(String.valueOf(limit)));
    }
}
```

## Redis Cluster

### Cluster Configuration

**application.yml:**

```yaml
redis:
  cluster:
    enabled: true
    nodes:
      - host: localhost
        port: 7000
      - host: localhost
        port: 7001
      - host: localhost
        port: 7002
```

## Redis Sentinel

### Sentinel Configuration

**application.yml:**

```yaml
redis:
  sentinel:
    enabled: true
    master: mymaster
    nodes:
      - host: localhost
        port: 26379
      - host: localhost
        port: 26380
```

## Redis Pipeline

### Pipeline Operations

```java
import io.micronaut.redis.RedisOperations;
import jakarta.inject.Singleton;
import java.util.List;

@Singleton
public class PipelineService {
    private final RedisOperations<String, String> redisOperations;

    public PipelineService(RedisOperations<String, String> redisOperations) {
        this.redisOperations = redisOperations;
    }

    public List<Object> executePipeline(List<String> keys) {
        return redisOperations.executePipelined(connection -> {
            for (String key : keys) {
                connection.get(key);
            }
            return null;
        });
    }
}
```

## Redis Streams

### Stream Operations

```java
import io.micronaut.redis.RedisOperations;
import jakarta.inject.Singleton;

@Singleton
public class StreamService {
    private final RedisOperations<String, String> redisOperations;

    public StreamService(RedisOperations<String, String> redisOperations) {
        this.redisOperations = redisOperations;
    }

    public void addToStream(String streamKey, String field, String value) {
        redisOperations.xadd(streamKey, Map.of(field, value));
    }

    public List<Map<String, String>> readFromStream(String streamKey, String lastId) {
        return redisOperations.xread(streamKey, lastId, 10);
    }
}
```

## Redis Bitmaps

### Bitmap Operations

```java
import io.micronaut.redis.RedisOperations;
import jakarta.inject.Singleton;

@Singleton
public class BitmapService {
    private final RedisOperations<String, String> redisOperations;

    public BitmapService(RedisOperations<String, String> redisOperations) {
        this.redisOperations = redisOperations;
    }

    public void setBit(String key, long offset, boolean value) {
        redisOperations.setbit(key, offset, value);
    }

    public boolean getBit(String key, long offset) {
        return redisOperations.getbit(key, offset);
    }

    public long bitCount(String key) {
        return redisOperations.bitcount(key);
    }
}
```

## Redis HyperLogLog

### HyperLogLog Operations

```java
@Singleton
public class HyperLogLogService {
    private final RedisOperations<String, String> redisOperations;

    public void addToHyperLogLog(String key, String... values) {
        redisOperations.pfadd(key, values);
    }

    public long countHyperLogLog(String key) {
        return redisOperations.pfcount(key);
    }

    public void mergeHyperLogLog(String destKey, String... sourceKeys) {
        redisOperations.pfmerge(destKey, sourceKeys);
    }
}
```


## Заключение

**Micronaut Redis** предоставляет мощные инструменты для интеграции с **Redis**. Поддержка **RedisTemplate**, **pub**/**sub**, **distributed locks**, **transactions**, **hash**/**list**/**set**/**sorted set operations**, **Lua scripts**, **Redis Cluster**, **Redis Sentinel**, **pipeline operations**, **streams**, **bitmaps**, **HyperLogLog** и других продвинутых возможностей позволяет создавать высокопроизводительные приложения с использованием **Redis**.

## Дополнительные ресурсы

- [**Micronaut Redis** Documentation](https://micronaut-projects.github.io/micronaut-redis/latest/guide/)
- [**Redis** Documentation](https://redis.io/docs/)
- [Lettuce Documentation](https://lettuce.io/core/release/reference/)
- [**Redis** Commands](https://redis.io/commands/)
- [**Redis** Cluster](https://redis.io/docs/management/scaling/)
- [**Redis** Sentinel](https://redis.io/docs/management/sentinel/)
- [**Redis** Streams](https://redis.io/docs/data-types/streams/)
- [**Redis** Bitmaps](https://redis.io/docs/data-types/bitmaps/)
- [**Redis** HyperLogLog](https://redis.io/docs/data-types/hyperloglogs/)

## См. также

- [[micronaut-actuator|Micronaut: Actuator — Health Checks, Metrics и Endpoints]]
- [[micronaut-basics|Micronaut: Основы]]
- [[micronaut-batch|Micronaut: Batch Processing — Job Processing и Scheduling]]
- [[micronaut-cache|Micronaut: Caching — Cache Abstraction и Redis Cache]]
- [[micronaut-cloud|Micronaut: Cloud Native — Service Discovery, Configuration и Distributed Tracing]]
