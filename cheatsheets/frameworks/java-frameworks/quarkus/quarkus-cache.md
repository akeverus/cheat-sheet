---
title: "Quarkus: Cache - Кеширование данных"
description: "Полное руководство по кешированию в Quarkus: Caffeine, Redis, cache annotations, cache configuration и best practices"
tags:
  - quarkus
  - cache
  - caffeine
  - redis
  - performance
  - java
difficulty: "intermediate"
prerequisites: ["quarkus/quarkus-basics.md", "quarkus/quarkus-core.md"]
next: ["quarkus-core.md", "quarkus-redis.md"]
updated: "2026-02-11"
related: ["quarkus-core.md", "quarkus-redis.md"]
---

# Quarkus: Cache - Кеширование данных

## Полезные ссылки

[Официальная документация Quarkus](https://quarkus.io/guides/)
[Quarkus GitHub](https://github.com/quarkusio/quarkus)

## Содержание

- [Quarkus: Cache — Кеширование данных](#quarkus-cache-кеширование-данных)
- [Введение](#введение)
  - [Основные возможности](#основные-возможности)
- [Caffeine Cache](#caffeine-cache)
  - [Basic Configuration](#basic-configuration)
  - [Cache Annotations](#cache-annotations)
  - [Cache Configuration](#cache-configuration)
- [Redis Cache](#redis-cache)
  - [Redis Configuration](#redis-configuration)
  - [Redis Cache Usage](#redis-cache-usage)
- [Advanced Cache Patterns](#advanced-cache-patterns)
  - [Cache-Aside Pattern](#cache-aside-pattern)
  - [Write-Through Pattern](#write-through-pattern)
- [Cache Invalidation](#cache-invalidation)
  - [Manual Invalidation](#manual-invalidation)
  - [Conditional Invalidation](#conditional-invalidation)
- [Лучшие практики](#лучшие-практики)
  - [1. Используйте кеш для дорогих операций](#1-используйте-кеш-для-дорогих-операций)
  - [2. Настраивайте TTL правильно](#2-настраивайте-ttl-правильно)
- [ Хорошо](#хорошо)
  - [3. Используйте @CacheKey для правильной инвалидации](#3-используйте-cachekey-для-правильной-инвалидации)
  - [Multiple Caches](#multiple-caches)
- [application.properties](#applicationproperties)
- [Users cache](#users-cache)
- [Products cache](#products-cache)
  - [Cache Statistics](#cache-statistics)
- [Cache Strategies](#cache-strategies)
  - [Write-Behind Pattern](#write-behind-pattern)
  - [Read-Through Pattern](#read-through-pattern)
- [Cache Warming](#cache-warming)
  - [Preloading Cache](#preloading-cache)
- [Distributed Caching](#distributed-caching)
  - [Redis Cluster](#redis-cluster)
  - [Cache Synchronization](#cache-synchronization)
- [Cache Performance](#cache-performance)
  - [Cache Hit Rate Optimization](#cache-hit-rate-optimization)
  - [Cache Size Management](#cache-size-management)
  - [4. Мониторьте hit rate](#4-мониторьте-hit-rate)
  - [5. Используйте distributed cache для масштабирования](#5-используйте-distributed-cache-для-масштабирования)
- [Cache Warming Strategies](#cache-warming-strategies)
  - [Lazy Loading with Cache](#lazy-loading-with-cache)
- [Cache Performance Optimization](#cache-performance-optimization)
  - [Hit Rate Optimization](#hit-rate-optimization)
- [Динамическая настройка размера кеша](#динамическая-настройка-размера-кеша)
  - [Cache Stampede Prevention](#cache-stampede-prevention)
  - [Cache Coherence](#cache-coherence)
- [Cache Monitoring and Metrics](#cache-monitoring-and-metrics)
  - [Cache Metrics Integration](#cache-metrics-integration)
- [Advanced Cache Strategies](#advanced-cache-strategies)
- [Заключение](#заключение)
- [Дополнительные ресурсы](#дополнительные-ресурсы)

## Введение

**Quarkus** предоставляет мощную систему кеширования через интеграцию с **Caffeine** и **Redis**. Это позволяет значительно улучшить производительность приложений за счет кеширования часто используемых данных.

### Основные возможности

- **Caffeine Cache**: **In-memory** кеш
- **Redis Cache**: **Distributed** кеш
- **Cache Annotations**: @**CacheResult**, @**CacheInvalidate**, @**CacheKey**
- **Cache Configuration**: Настройка **TTL**, размеров и стратегий

## Caffeine Cache

### Basic Configuration

**application.properties:**

```properties
quarkus.cache.enabled=true
quarkus.cache.caffeine.max-size=1000
quarkus.cache.caffeine.expire-after-write=10m
```

### Cache Annotations

**Использование **cache** аннотаций:**

```java
import io.quarkus.cache.CacheResult;
import io.quarkus.cache.CacheInvalidate;
import io.quarkus.cache.CacheKey;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class UserService {

    @CacheResult(cacheName = "users")
    public User getUser(@CacheKey Long id) {
        return userRepository.findById(id);
    }

    @CacheInvalidate(cacheName = "users")
    public void invalidateUser(@CacheKey Long id) {
        // Кеш будет инвалидирован
    }
}
```

### Cache Configuration

**Настройка кеша:**

```java
import io.quarkus.cache.Cache;
import io.quarkus.cache.CacheManager;
import jakarta.inject.Inject;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class CacheService {

    @Inject
    CacheManager cacheManager;

    public void configureCache() {
        Cache cache = cacheManager.getCache("users")
            .orElseThrow();

        // Программная настройка кеша
    }
}
```

## Redis Cache

### Redis Configuration

**application.properties:**

```properties
quarkus.redis.hosts=redis://localhost:6379
quarkus.cache.redis.enabled=true
quarkus.cache.redis.ttl=1h
```

### Redis Cache Usage

**Использование **Redis** для кеширования:**

```java
import io.quarkus.cache.CacheResult;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class RedisCacheService {

    @CacheResult(cacheName = "redis-cache")
    public String getCachedData(String key) {
        return fetchDataFromDatabase(key);
    }
}
```

## Advanced Cache Patterns

### Cache-Aside Pattern

**Реализация **cache-aside** паттерна:**

```java
import io.quarkus.cache.Cache;
import io.quarkus.cache.CacheManager;
import jakarta.inject.Inject;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class CacheAsideService {

    @Inject
    CacheManager cacheManager;

    public User getUser(Long id) {
        Cache cache = cacheManager.getCache("users")
            .orElseThrow();

        return cache.get(id, () -> {
            // Загрузка из БД если нет в кеше
            return userRepository.findById(id);
        }).await().indefinitely();
    }
}
```

### Write-Through Pattern

**Реализация **write-through** паттерна:**

```java
@ApplicationScoped
public class WriteThroughService {

    @Inject
    CacheManager cacheManager;

    public User saveUser(User user) {
        User saved = userRepository.save(user);

        Cache cache = cacheManager.getCache("users")
            .orElseThrow();
        cache.put(saved.getId(), saved);

        return saved;
    }
}
```

## Cache Invalidation

### Manual Invalidation

**Ручная инвалидация кеша:**

```java
import io.quarkus.cache.CacheInvalidate;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class InvalidationService {

    @CacheInvalidate(cacheName = "users")
    public void invalidateUser(@CacheKey Long id) {
        // Инвалидация конкретного ключа
    }

    @CacheInvalidateAll(cacheName = "users")
    public void invalidateAllUsers() {
        // Инвалидация всего кеша
    }
}
```

### Conditional Invalidation

**Условная инвалидация:**

```java
@ApplicationScoped
public class ConditionalInvalidationService {

    @CacheInvalidate(cacheName = "users")
    public void updateUser(Long id, User user) {
        userRepository.update(id, user);
        // Кеш будет инвалидирован автоматически
    }
}
```

## Лучшие практики

### 1. Используйте кеш для дорогих операций

```java
// ✅ Хорошо
@CacheResult(cacheName = "expensive-operation")
public ExpensiveResult computeExpensiveResult(String input) {
    // Дорогая операция
}
```

### 2. Настраивайте TTL правильно

```properties
# ✅ Хорошо
quarkus.cache.caffeine.expire-after-write=10m
```

### 3. Используйте @CacheKey для правильной инвалидации

```java
// ✅ Хорошо
@CacheInvalidate(cacheName = "users")
public void updateUser(@CacheKey Long id, User user) {
    // ...
}
```

## Cache Configuration

### Multiple Caches

**Настройка нескольких кешей:**

```properties
# application.properties
quarkus.cache.enabled=true

# Users cache
quarkus.cache.caffeine.users.max-size=1000
quarkus.cache.caffeine.users.expire-after-write=10m

# Products cache
quarkus.cache.caffeine.products.max-size=500
quarkus.cache.caffeine.products.expire-after-write=30m
```

### Cache Statistics

**Включение статистики кеша:**

```properties
# application.properties
quarkus.cache.caffeine.statistics-enabled=true
```

**Получение статистики:**

```java
import io.quarkus.cache.Cache;
import io.quarkus.cache.CacheManager;
import jakarta.inject.Inject;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class CacheStatisticsService {

    @Inject
    CacheManager cacheManager;

    public CacheStatistics getStatistics(String cacheName) {
        Cache cache = cacheManager.getCache(cacheName)
            .orElseThrow();
        return cache.getStatistics();
    }
}
```

## Cache Strategies

### Write-Behind Pattern

**Реализация **write-behind** паттерна:**

```java
@ApplicationScoped
public class WriteBehindService {

    @Inject
    CacheManager cacheManager;

    public void saveUser(User user) {
        Cache cache = cacheManager.getCache("users")
            .orElseThrow();

        // Сохранение в кеш немедленно
        cache.put(user.getId(), user);

        // Асинхронное сохранение в БД
        Uni.createFrom().item(() -> {
            userRepository.save(user);
            return null;
        }).subscribe().with(
            result -> {},
            failure -> logError(failure)
        );
    }
}
```

### Read-Through Pattern

**Реализация **read-through** паттерна:**

```java
@ApplicationScoped
public class ReadThroughService {

    @Inject
    CacheManager cacheManager;

    public User getUser(Long id) {
        Cache cache = cacheManager.getCache("users")
            .orElseThrow();

        return cache.get(id, () -> {
            // Автоматическая загрузка из БД при промахе кеша
            return userRepository.findById(id);
        }).await().indefinitely();
    }
}
```

## Cache Warming

### Preloading Cache

**Предзагрузка кеша:**

```java
import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class CacheWarmingService {

    @Inject
    CacheManager cacheManager;

    @PostConstruct
    void warmCache() {
        Cache cache = cacheManager.getCache("users")
            .orElseThrow();

        List<User> popularUsers = userRepository.findPopularUsers();
        popularUsers.forEach(user -> {
            cache.put(user.getId(), user);
        });
    }
}
```

## Distributed Caching

### Redis Cluster

**Настройка **Redis** кластера:**

```properties
# application.properties
quarkus.redis.hosts=redis://node1:6379,redis://node2:6379,redis://node3:6379
quarkus.cache.redis.enabled=true
quarkus.cache.redis.cluster-mode=true
```

### Cache Synchronization

**Синхронизация кеша между инстансами:**

```java
@ApplicationScoped
public class DistributedCacheService {

    @Inject
    @RedisCache("users")
    Cache cache;

    public void updateUser(User user) {
        // Обновление в Redis (синхронизируется со всеми инстансами)
        cache.put(user.getId(), user);
    }
}
```

## Cache Performance

### Cache Hit Rate Optimization

**Оптимизация **hit rate**:**

```java
@ApplicationScoped
public class OptimizedCacheService {

    @CacheResult(cacheName = "users")
    public User getUser(@CacheKey Long id) {
        // Использование кеша для часто запрашиваемых данных
        return userRepository.findById(id);
    }

    // Не кешируем редко запрашиваемые данные
    public User getRareUser(Long id) {
        return userRepository.findById(id);
    }
}
```

### Cache Size Management

**Управление размером кеша:**

```properties
# application.properties
quarkus.cache.caffeine.max-size=1000
quarkus.cache.caffeine.expire-after-write=10m
quarkus.cache.caffeine.expire-after-access=5m
```

## Cache Warming Strategies

### Preloading Cache

**Предзагрузка кеша:**

```java
@ApplicationScoped
public class CacheWarmingService {

    @Inject
    Cache cache;

    @PostConstruct
    public void warmCache() {
        // Предзагрузка часто используемых данных
        List<User> popularUsers = userService.findPopularUsers();
        popularUsers.forEach(user ->
            cache.put(user.getId(), user)
        );
    }
}
```

### Lazy Loading with Cache

**Ленивая загрузка с кешированием:**

```java
@ApplicationScoped
public class LazyCacheService {

    @CacheResult(cacheName = "users")
    public User getUser(Long id) {
        // Загрузка из БД только если нет в кеше
        return userRepository.findById(id);
    }
}
```

## Cache Performance Optimization

### Hit Rate Optimization

**Оптимизация **hit rate**:**

```java
@ApplicationScoped
public class CacheOptimizationService {

    @Inject
    CacheManager cacheManager;

    public void optimizeCache() {
        Cache cache = cacheManager.getCache("users");
        CacheStatistics stats = cache.getStatistics();

        double hitRate = stats.getHitCount() / (double) stats.getRequestCount();

        if (hitRate < 0.7) {
            // Увеличить размер кеша или TTL
            adjustCacheSettings();
        }
    }
}
```

### Cache Size Management

**Управление размером кеша:**

```properties
# Динамическая настройка размера кеша
quarkus.cache.caffeine.max-size=10000
quarkus.cache.caffeine.initial-capacity=1000
```

## Advanced Cache Patterns

### Cache Stampede Prevention

**Предотвращение **cache stampede**:**

```java
@ApplicationScoped
public class StampedePreventionService {

    private final Map<String, CompletableFuture<User>> loading = new ConcurrentHashMap<>();

    public Uni<User> getUser(Long id) {
        String key = "user:" + id;

        CompletableFuture<User> future = loading.computeIfAbsent(key, k -> {
            return CompletableFuture.supplyAsync(() -> {
                try {
                    return userRepository.findById(id);
                } finally {
                    loading.remove(key);
                }
            });
        });

        return Uni.createFrom().completionStage(future);
    }
}
```

### Cache Coherence

**Обеспечение согласованности кеша:**

```java
@ApplicationScoped
public class CacheCoherenceService {

    @CacheInvalidate(cacheName = "users")
    @CacheInvalidate(cacheName = "user-profiles")
    public void invalidateUserCaches(@CacheKey Long userId) {
        // Инвалидация всех связанных кешей
    }
}
```

## Cache Monitoring and Metrics

### Cache Statistics

**Статистика кеша:**

```java
@ApplicationScoped
public class CacheStatisticsService {

    @Inject
    CacheManager cacheManager;

    public CacheStats getCacheStats(String cacheName) {
        Cache cache = cacheManager.getCache(cacheName);
        CacheStatistics stats = cache.getStatistics();

        return new CacheStats(
            stats.getHitCount(),
            stats.getMissCount(),
            stats.getRequestCount(),
            stats.getHitCount() / (double) stats.getRequestCount()
        );
    }
}
```

### Cache Metrics Integration

**Интеграция с метриками:**

```java
@ApplicationScoped
public class CacheMetricsService {

    @Inject
    MeterRegistry registry;

    @PostConstruct
    void registerMetrics() {
        Gauge.builder("cache.size", this, CacheMetricsService::getCacheSize)
            .description("Cache size")
            .register(registry);

        Gauge.builder("cache.hit.rate", this, CacheMetricsService::getHitRate)
            .description("Cache hit rate")
            .register(registry);
    }

    private double getCacheSize() {
        return cacheManager.getCache("users").getStatistics().getSize();
    }

    private double getHitRate() {
        CacheStatistics stats = cacheManager.getCache("users").getStatistics();
        return stats.getHitCount() / (double) stats.getRequestCount();
    }
}
```

## Advanced Cache Strategies

### Cache-Aside Pattern

**Паттерн **Cache-Aside**:**

```java
@ApplicationScoped
public class CacheAsideService {

    @Inject
    Cache cache;

    public User getUser(Long id) {
        User user = cache.get(id, User.class);
        if (user == null) {
            user = userRepository.findById(id);
            if (user != null) {
                cache.put(id, user);
            }
        }
        return user;
    }
}
```

### Write-Through Pattern

**Паттерн **Write-Through**:**

```java
@ApplicationScoped
public class WriteThroughService {

    @Inject
    Cache cache;

    public void saveUser(User user) {
        // Сохранение в кеш и БД одновременно
        cache.put(user.getId(), user);
        userRepository.save(user);
    }
}
```


## Заключение

**Quarkus Cache** предоставляет мощные инструменты для кеширования данных. Поддержка **Caffeine**, **Redis**, **cache annotations**, различных паттернов кеширования, инвалидации, **distributed caching**, **performance optimization** и других продвинутых возможностей позволяет значительно улучшить производительность приложений. Правильное использование кеширования, настройка **TTL**, управление размером кеша и мониторинг производительности являются ключевыми аспектами создания эффективных систем кеширования.

## Дополнительные ресурсы

- [**Quarkus Cache** Guide](https://quarkus.io/guides/cache)
- [Caffeine Documentation](https://github.com/ben-manes/caffeine/wiki)
- [**Redis** Documentation](https://redis.io/docs/)
- [Cache Patterns](https://docs.microsoft.com/en-us/azure/architecture/patterns/cache-aside)

## См. также

- [[quarkus-actuator|Quarkus: Actuator — Health Checks и Metrics]]
- [[quarkus-basics|Quarkus: Основы]]
- [[quarkus-cloud|Quarkus: Cloud Native — Kubernetes, OpenShift и Service Mesh]]
- [[quarkus-core|Quarkus: Core — CDI, Bean Scopes и Configuration]]
- [[quarkus-data|Quarkus: Data Access — Hibernate ORM, Panache и Repositories]]
