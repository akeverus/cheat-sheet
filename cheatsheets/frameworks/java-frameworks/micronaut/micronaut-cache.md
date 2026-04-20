---
title: "Micronaut: Caching — Cache Abstraction и Redis Cache"
description: "Полное руководство по кэшированию в Micronaut: cache abstraction, Redis cache, EhCache, Caffeine и best practices"
tags:
  - micronaut
  - cache
  - redis
  - ehcache
  - caffeine
  - java
  - kotlin
difficulty: "intermediate"
prerequisites: ["micronaut/micronaut-basics.md", "micronaut/micronaut-core.md"]
next: ["micronaut-redis.md", "micronaut-data.md"]
updated: "2026-04-20"
related: ["micronaut-core.md", "micronaut-redis.md"]
---

# Micronaut: Caching — Cache Abstraction и Redis Cache

## Полезные ссылки

[Официальная документация Micronaut](https://docs.micronaut.io/)
[Micronaut GitHub](https://github.com/micronaut-projects/micronaut-core)

## Содержание

- [Введение](#введение)
  - [Основные возможности](#основные-возможности)
- [Настройка Cache](#настройка-cache)
  - [Зависимости](#зависимости)
  - [Конфигурация](#конфигурация)
- [Cache Annotations](#cache-annotations)
  - [@Cacheable](#cacheable)
  - [@CachePut](#cacheput)
  - [@CacheInvalidate](#cacheinvalidate)
- [Redis Cache](#redis-cache)
  - [Настройка Redis Cache](#настройка-redis-cache)
  - [Использование Redis Cache](#использование-redis-cache)
- [Caffeine Cache](#caffeine-cache)
  - [Настройка Caffeine Cache](#настройка-caffeine-cache)
  - [Использование Caffeine Cache](#использование-caffeine-cache)
- [Async Cache](#async-cache)
  - [Async Cache Operations](#async-cache-operations)
- [Cache Statistics](#cache-statistics)
  - [Cache Metrics](#cache-metrics)
- [Лучшие практики](#лучшие-практики)
  - [1. Используйте правильные ключи кэша](#1-используйте-правильные-ключи-кэша)
  - [2. Настраивайте TTL правильно](#2-настраивайте-ttl-правильно)
  - [3. Очищайте кэш при обновлении данных](#3-очищайте-кэш-при-обновлении-данных)
  - [4. Используйте async cache для неблокирующих операций](#4-используйте-async-cache-для-неблокирующих-операций)
  - [5. Мониторьте производительность кэша](#5-мониторьте-производительность-кэша)
- [Cache Configuration](#cache-configuration)
  - [Multiple Cache Managers](#multiple-cache-managers)
  - [Cache Configuration](#cache-configuration-1)
- [Cache Patterns](#cache-patterns)
  - [Cache-Aside Pattern](#cache-aside-pattern)
  - [Write-Through Pattern](#write-through-pattern)
  - [Write-Behind Pattern](#write-behind-pattern)
- [Cache Invalidation Strategies](#cache-invalidation-strategies)
  - [Time-based Invalidation](#time-based-invalidation)
  - [Event-based Invalidation](#event-based-invalidation)
- [Cache Warming](#cache-warming)
  - [Cache Preloading](#cache-preloading)
- [Cache Statistics](#cache-statistics-1)
  - [Cache Hit/Miss Ratio](#cache-hitmiss-ratio)
- [Cache Synchronization](#cache-synchronization)
  - [Distributed Cache Sync](#distributed-cache-sync)
- [Cache Preloading](#cache-preloading-1)
  - [Cache Warmup](#cache-warmup)
- [Заключение](#заключение)
- [Дополнительные ресурсы](#дополнительные-ресурсы)
- [См. также](#см-также)

## Введение

**Micronaut** предоставляет мощную систему кэширования с поддержкой различных провайдеров: **Redis**, **EhCache**, **Caffeine** и других. Это позволяет оптимизировать производительность приложений за счет кэширования часто используемых данных.

### Основные возможности

- **Cache Abstraction**: Абстракция над различными провайдерами кэша
- **Redis Cache**: Кэширование в **Redis**
- **EhCache**: **In-memory** кэширование
- **Caffeine**: Высокопроизводительный **in-memory** кэш
- **Cache Annotations**: Аннотации для декларативного кэширования

## Настройка Cache

### Зависимости

**build.gradle:**

```gradle
dependencies {
    implementation("io.micronaut.cache:micronaut-cache-core")
    implementation("io.micronaut.cache:micronaut-cache-caffeine")
    // или
    implementation("io.micronaut.cache:micronaut-cache-redis")
    // или
    implementation("io.micronaut.cache:micronaut-cache-ehcache")
}
```

### Конфигурация

**application.yml:**

```yaml
micronaut:
  cache:
    caffeine:
      spec: maximumSize=500,expireAfterWrite=5m
    redis:
      enabled: true
      servers: localhost:6379
```

## Cache Annotations

### @Cacheable

```java
import io.micronaut.cache.annotation.Cacheable;
import jakarta.inject.Singleton;

@Singleton
public class UserService {

    @Cacheable("users")
    public User getUser(Long id) {
        return userRepository.findById(id)
            .orElseThrow(() -> new UserNotFoundException(id));
    }

    @Cacheable(value = "users", parameters = {"id", "name"})
    public User getUserByIdAndName(Long id, String name) {
        return userRepository.findByIdAndName(id, name)
            .orElseThrow(() -> new UserNotFoundException(id));
    }
}
```

### @CachePut

```java
import io.micronaut.cache.annotation.CachePut;
import jakarta.inject.Singleton;

@Singleton
public class UserService {

    @CachePut("users")
    public User createUser(User user) {
        User created = userRepository.save(user);
        return created;
    }

    @CachePut(value = "users", parameters = "user.id")
    public User updateUser(User user) {
        return userRepository.update(user);
    }
}
```

### @CacheInvalidate

```java
import io.micronaut.cache.annotation.CacheInvalidate;
import jakarta.inject.Singleton;

@Singleton
public class UserService {

    @CacheInvalidate("users")
    public void deleteUser(Long id) {
        userRepository.deleteById(id);
    }

    @CacheInvalidate(value = "users", all = true)
    public void clearAllUsers() {
        // Очистка всего кэша
    }
}
```

## Redis Cache

### Настройка Redis Cache

**application.yml:**

```yaml
micronaut:
  cache:
    redis:
      enabled: true
      servers: localhost:6379
      default-expiration: PT1H
      caches:
        users:
          expire-after-write: PT30M
        orders:
          expire-after-write: PT1H
```

### Использование Redis Cache

```java
import io.micronaut.cache.SyncCache;
import jakarta.inject.Singleton;
import jakarta.inject.Named;

@Singleton
public class UserService {
    private final SyncCache<String, User> userCache;

    public UserService(@Named("users") SyncCache<String, User> userCache) {
        this.userCache = userCache;
    }

    public User getUser(Long id) {
        String key = "user:" + id;
        return userCache.get(key, () ->
            userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException(id))
        );
    }

    public void putUser(User user) {
        String key = "user:" + user.getId();
        userCache.put(key, user);
    }

    public void evictUser(Long id) {
        String key = "user:" + id;
        userCache.invalidate(key);
    }
}
```

## Caffeine Cache

### Настройка Caffeine Cache

**application.yml:**

```yaml
micronaut:
  cache:
    caffeine:
      spec: maximumSize=1000,expireAfterWrite=10m,expireAfterAccess=5m
      caches:
        users:
          maximumSize: 500
          expireAfterWrite: PT30M
```

### Использование Caffeine Cache

```java
import io.micronaut.cache.SyncCache;
import jakarta.inject.Singleton;
import jakarta.inject.Named;

@Singleton
public class UserService {
    private final SyncCache<String, User> userCache;

    public UserService(@Named("users") SyncCache<String, User> userCache) {
        this.userCache = userCache;
    }

    public User getUser(Long id) {
        String key = "user:" + id;
        return userCache.get(key, () ->
            userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException(id))
        );
    }
}
```

## Async Cache

### Async Cache Operations

```java
import io.micronaut.cache.AsyncCache;
import jakarta.inject.Singleton;
import jakarta.inject.Named;
import reactor.core.publisher.Mono;

@Singleton
public class AsyncUserService {
    private final AsyncCache<String, User> userCache;

    public AsyncUserService(@Named("users") AsyncCache<String, User> userCache) {
        this.userCache = userCache;
    }

    public Mono<User> getUser(Long id) {
        String key = "user:" + id;
        return userCache.get(key, () ->
            Mono.fromCallable(() ->
                userRepository.findById(id)
                    .orElseThrow(() -> new UserNotFoundException(id))
            )
        );
    }
}
```

## Cache Statistics

### Cache Metrics

```java
import io.micronaut.cache.Cache;
import io.micronaut.cache.CacheManager;
import jakarta.inject.Singleton;

@Singleton
public class CacheStatisticsService {
    private final CacheManager cacheManager;

    public CacheStatisticsService(CacheManager cacheManager) {
        this.cacheManager = cacheManager;
    }

    public void printCacheStatistics() {
        Cache<String, User> cache = cacheManager.getCache("users", String.class, User.class);
        // Получение статистики кэша
    }
}
```

## Лучшие практики

### 1. Используйте правильные ключи кэша

```java
// ✅ Хорошо
@Cacheable(value = "users", parameters = "id")
public User getUser(Long id) {
    // ...
}
```

### 2. Настраивайте TTL правильно

```yaml
# ✅ Хорошо
micronaut:
  cache:
    redis:
      default-expiration: PT1H
```

### 3. Очищайте кэш при обновлении данных

```java
// ✅ Хорошо
@CacheInvalidate("users")
public void updateUser(User user) {
    // ...
}
```

### 4. Используйте async cache для неблокирующих операций

```java
// ✅ Хорошо
public Mono<User> getUser(Long id) {
    return asyncCache.get(key, () -> loadUser(id));
}
```

### 5. Мониторьте производительность кэша

```java
// ✅ Хорошо
CacheStatistics stats = cache.getStatistics();
log.info("Cache hit rate: {}", stats.getHitRate());
```

## Cache Configuration

### Multiple Cache Managers

```java
import io.micronaut.cache.CacheManager;
import io.micronaut.cache.SyncCache;
import jakarta.inject.Singleton;
import jakarta.inject.Named;

@Singleton
public class MultiCacheService {
    private final SyncCache<String, User> localCache;
    private final SyncCache<String, User> distributedCache;

    public MultiCacheService(
            @Named("local") SyncCache<String, User> localCache,
            @Named("distributed") SyncCache<String, User> distributedCache) {
        this.localCache = localCache;
        this.distributedCache = distributedCache;
    }

    public User getUser(Long id) {
        String key = "user:" + id;

        // Сначала проверяем локальный кэш
        User user = localCache.get(key, () -> null);
        if (user != null) {
            return user;
        }

        // Затем проверяем распределенный кэш
        user = distributedCache.get(key, () ->
            userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException(id))
        );

        // Сохраняем в локальный кэш
        localCache.put(key, user);

        return user;
    }
}
```

### Cache Configuration

**application.yml:**

```yaml
micronaut:
  cache:
    caches:
      users:
        maximum-size: 1000
        expire-after-write: PT30M
        expire-after-access: PT15M
      orders:
        maximum-size: 500
        expire-after-write: PT1H
    caffeine:
      spec: maximumSize=1000,expireAfterWrite=10m
    redis:
      enabled: true
      servers: localhost:6379
      default-expiration: PT1H
```

## Cache Patterns

### Cache-Aside Pattern

```java
import io.micronaut.cache.SyncCache;
import jakarta.inject.Singleton;
import jakarta.inject.Named;

@Singleton
public class CacheAsideService {
    private final SyncCache<String, User> userCache;

    public CacheAsideService(@Named("users") SyncCache<String, User> userCache) {
        this.userCache = userCache;
    }

    public User getUser(Long id) {
        String key = "user:" + id;

        // Проверяем кэш
        User user = userCache.get(key, () -> null);
        if (user != null) {
            return user;
        }

        // Загружаем из БД
        user = userRepository.findById(id)
            .orElseThrow(() -> new UserNotFoundException(id));

        // Сохраняем в кэш
        userCache.put(key, user);

        return user;
    }
}
```

### Write-Through Pattern

```java
import io.micronaut.cache.annotation.CachePut;
import jakarta.inject.Singleton;

@Singleton
public class WriteThroughService {

    @CachePut("users")
    public User createUser(User user) {
        // Сохранение в БД и кэш одновременно
        return userRepository.save(user);
    }
}
```

### Write-Behind Pattern

```java
import io.micronaut.cache.SyncCache;
import jakarta.inject.Singleton;
import jakarta.inject.Named;
import java.util.concurrent.CompletableFuture;

@Singleton
public class WriteBehindService {
    private final SyncCache<String, User> userCache;

    public WriteBehindService(@Named("users") SyncCache<String, User> userCache) {
        this.userCache = userCache;
    }

    public CompletableFuture<User> createUserAsync(User user) {
        String key = "user:" + user.getId();

        // Сначала сохраняем в кэш
        userCache.put(key, user);

        // Затем асинхронно сохраняем в БД
        return CompletableFuture.supplyAsync(() -> {
            return userRepository.save(user);
        });
    }
}
```

## Cache Invalidation Strategies

### Time-based Invalidation

```yaml
micronaut:
  cache:
    caches:
      users:
        expire-after-write: PT30M
        expire-after-access: PT15M
```

### Event-based Invalidation

```java
import io.micronaut.cache.annotation.CacheInvalidate;
import io.micronaut.context.event.ApplicationEventPublisher;
import jakarta.inject.Singleton;

@Singleton
public class EventBasedCacheService {
    private final ApplicationEventPublisher<UserUpdatedEvent> eventPublisher;

    @CacheInvalidate("users")
    public void updateUser(User user) {
        userRepository.update(user);
        eventPublisher.publishEvent(new UserUpdatedEvent(user));
    }
}
```

## Cache Warming

### Cache Preloading

```java
import io.micronaut.cache.SyncCache;
import jakarta.inject.Singleton;
import jakarta.inject.Named;
import jakarta.annotation.PostConstruct;

@Singleton
public class CacheWarmingService {
    private final SyncCache<String, User> userCache;
    private final UserRepository userRepository;

    public CacheWarmingService(
            @Named("users") SyncCache<String, User> userCache,
            UserRepository userRepository) {
        this.userCache = userCache;
        this.userRepository = userRepository;
    }

    @PostConstruct
    public void warmCache() {
        // Предзагрузка часто используемых данных в кэш
        List<User> popularUsers = userRepository.findPopularUsers();
        popularUsers.forEach(user -> {
            String key = "user:" + user.getId();
            userCache.put(key, user);
        });
    }
}
```

## Cache Statistics

### Cache Hit/Miss Ratio

```java
import io.micronaut.cache.Cache;
import io.micronaut.cache.CacheManager;
import jakarta.inject.Singleton;

@Singleton
public class CacheStatisticsService {
    private final CacheManager cacheManager;

    public CacheStatisticsService(CacheManager cacheManager) {
        this.cacheManager = cacheManager;
    }

    public void printCacheStatistics() {
        Cache<String, User> cache = cacheManager.getCache("users", String.class, User.class);
        // Получение и вывод статистики кэша
        log.info("Cache statistics: {}", cache.getStatistics());
    }
}
```

## Cache Synchronization

### Distributed Cache Sync

```java
import io.micronaut.cache.SyncCache;
import io.micronaut.cache.annotation.CacheInvalidate;
import io.micronaut.context.event.ApplicationEventPublisher;
import jakarta.inject.Singleton;

@Singleton
public class DistributedCacheService {
    private final SyncCache<String, User> userCache;
    private final ApplicationEventPublisher<CacheInvalidationEvent> eventPublisher;

    public DistributedCacheService(
            @Named("users") SyncCache<String, User> userCache,
            ApplicationEventPublisher<CacheInvalidationEvent> eventPublisher) {
        this.userCache = userCache;
        this.eventPublisher = eventPublisher;
    }

    @CacheInvalidate("users")
    public void invalidateUser(Long id) {
        String key = "user:" + id;
        userCache.invalidate(key);
        eventPublisher.publishEvent(new CacheInvalidationEvent(key));
    }
}
```

## Cache Preloading

### Cache Warmup

```java
import io.micronaut.cache.SyncCache;
import jakarta.inject.Singleton;
import jakarta.inject.Named;
import jakarta.annotation.PostConstruct;

@Singleton
public class CacheWarmupService {
    private final SyncCache<String, User> userCache;
    private final UserRepository userRepository;

    public CacheWarmupService(
            @Named("users") SyncCache<String, User> userCache,
            UserRepository userRepository) {
        this.userCache = userCache;
        this.userRepository = userRepository;
    }

    @PostConstruct
    public void warmupCache() {
        List<User> popularUsers = userRepository.findPopularUsers();
        popularUsers.forEach(user -> {
            String key = "user:" + user.getId();
            userCache.put(key, user);
        });
    }
}
```

## Заключение

**Micronaut Cache** предоставляет мощные инструменты для кэширования данных. Поддержка различных провайдеров (Redis, `Caffeine`, EhCache), аннотаций для декларативного кэширования, **async** операций, статистики, множественных менеджеров кэша, паттернов кэширования (cache-aside, `write-through`, write-behind), стратегий инвалидации, **cache warming**, **statistics**, **cache synchronization**, **preloading** и других продвинутых возможностей позволяет оптимизировать производительность приложений.

## Дополнительные ресурсы

- [**Micronaut Cache** Documentation](https://micronaut-projects.github.io/micronaut-cache/latest/guide/)
- [**Redis** Documentation](https://redis.io/docs/)
- [Caffeine Documentation](https://github.com/ben-manes/caffeine/wiki)
- [**EhCache** Documentation](https://www.ehcache.org/documentation/)
- [Cache Patterns](https://docs.microsoft.com/en-us/azure/architecture/patterns/cache-aside)

## См. также

- [[micronaut-actuator|Micronaut: Actuator — Health Checks, Metrics и Endpoints]]
- [[micronaut-basics|Micronaut: Основы]]
- [[micronaut-batch|Micronaut: Batch Processing — Job Processing и Scheduling]]
- [[micronaut-cloud|Micronaut: Cloud Native — Service Discovery, Configuration и Distributed Tracing]]
- [[micronaut-core|Micronaut: Core — Dependency Injection и Bean Management]]
