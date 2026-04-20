---
title: "Основы проектирования систем (System Design)"
description: "Введение в проектирование масштабируемых систем."
tags:
  - architecture
  - system-design
  - system-design-basics
difficulty: "intermediate"
prerequisites: []
next: []
updated: "2026-04-20"
---
# Основы проектирования систем (System Design)

Введение в проектирование масштабируемых систем.

## Полезные ссылки

- [System Design Primer](https://github.com/donnemartin/system-design-primer)
- [High Scalability](http://highscalability.com/)

## Содержание

- [Основные принципы](#основные-принципы)
  - [Масштабируемость (Scalability)](#масштабируемость-scalability)
  - [Надёжность (Reliability)](#надёжность-reliability)
  - [Производительность (Performance)](#производительность-performance)
- [Компоненты системы](#компоненты-системы)
  - [Load Balancer](#load-balancer)
  - [API Gateway](#api-gateway)
  - [Database](#database)
  - [Cache](#cache)
  - [Message Queue](#message-queue)
  - [CDN](#cdn)
- [Процесс проектирования](#процесс-проектирования)
  - [1. Понимание требований](#1-понимание-требований)
  - [2. Оценка масштаба](#2-оценка-масштаба)
  - [3. Проектирование API](#3-проектирование-api)
  - [4. Проектирование базы данных](#4-проектирование-базы-данных)
  - [5. Проектирование компонентов](#5-проектирование-компонентов)
  - [6. Масштабирование](#6-масштабирование)
- [Пример: Проектирование URL Shortener](#пример-проектирование-url-shortener)
  - [Требования](#требования)
  - [Архитектура](#архитектура)
  - [Алгоритм генерации короткого URL](#алгоритм-генерации-короткого-url)
- [Лучшие практики](#лучшие-практики)
- [Решение проблем](#решение-проблем)
- [Частые вопросы](#частые-вопросы)
- [См. также](#см-также)

## Основные принципы

### Масштабируемость (Scalability)

**Вертикальное масштабирование (Scale Up):**
- Увеличение ресурсов одного сервера (CPU, `RAM`, диск)
- Простота реализации, но ограниченность

**Горизонтальное масштабирование (Scale Out):**
- Добавление новых серверов
- Более сложная архитектура, но практически неограниченное масштабирование

Ниже — пример **Load Balancer** для горизонтального масштабирования (Java).
```java
// Пример: Load Balancer для горизонтального масштабирования
class LoadBalancer {
    private List<Server> servers;
    private LoadBalancingStrategy strategy;

    Server selectServer(Request request) {
        return strategy.select(servers, request);
    }
}

interface LoadBalancingStrategy {
    Server select(List<Server> servers, Request request);
}

class RoundRobinStrategy implements LoadBalancingStrategy {
    private int currentIndex = 0;

    public Server select(List<Server> servers, Request request) {
        Server server = servers.get(currentIndex);
        currentIndex = (currentIndex + 1) % servers.size();
        return server;
    }
}
```

### Надёжность (Reliability)

**Репликация:**
- Дублирование данных и сервисов для отказоустойчивости

**Резервное копирование:**
- Регулярные бэкапы данных

**Мониторинг и алертинг:**
- Отслеживание состояния системы

```java
// Пример: Репликация базы данных
class DatabaseReplication {
    private Database primary;
    private List<Database> replicas;

    void write(Data data) {
        primary.write(data);
        // Асинхронная репликация
        replicas.forEach(replica -> replica.writeAsync(data));
    }

    Data read() {
        // Чтение из реплики для распределения нагрузки
        Database replica = selectReplica();
        return replica.read();
    }
}
```

### Производительность (Performance)

**Кэширование:**
- Хранение часто используемых данных в памяти

**CDN (Content Delivery Network):**
- Распределение статического контента

**Оптимизация запросов:**
- Индексы, денормализация, пагинация

```java
// Пример: Многоуровневое кэширование
class CacheService {
    private Cache<String, Object> l1Cache; // In-memory (быстрый)
    private Cache<String, Object> l2Cache; // Redis (быстрый, распределённый)
    private Database database; // Медленный, но надёжный

    Object get(String key) {
        // L1 Cache
        Object value = l1Cache.get(key);
        if (value != null) return value;

        // L2 Cache
        value = l2Cache.get(key);
        if (value != null) {
            l1Cache.put(key, value);
            return value;
        }

        // Database
        value = database.get(key);
        l2Cache.put(key, value);
        l1Cache.put(key, value);
        return value;
    }
}
```

## Компоненты системы

### Load Balancer
Распределение нагрузки между серверами

### API Gateway
Единая точка входа для всех клиентов

### Database
- **SQL**: **PostgreSQL**, **MySQL** (ACID гарантии)
- **NoSQL**: **MongoDB**, **Cassandra** (горизонтальное масштабирование)

### Cache
- **Redis**, **Memcached** (быстрый доступ к данным)

### Message Queue
- **Kafka**, **RabbitMQ** (асинхронная обработка)

### CDN
Распределение статического контента

## Процесс проектирования

### 1. Понимание требований
- Функциональные требования
- Нефункциональные требования (масштаб, производительность)

### 2. Оценка масштаба
- Количество пользователей
- Объём данных
- Количество запросов в секунду (QPS)

### 3. Проектирование API
- Определение **endpoints**
- Формат данных (JSON, Protobuf)

### 4. Проектирование базы данных
- Схема данных
- Выбор типа БД (SQL/NoSQL)
- Стратегия шардирования

### 5. Проектирование компонентов
- Модули системы
- Взаимодействие между компонентами

### 6. Масштабирование
- Горизонтальное/вертикальное масштабирование
- **Load balancing**
- Кэширование

## Пример: Проектирование URL Shortener

### Требования
- Сокращение длинных **URL**
- Редирект по короткой ссылке
- Масштаб: 100M **URL** в день

### Архитектура

```text
Client → Load Balancer → API Servers → Database
                              ↓
                          Cache (Redis)
```

### Алгоритм генерации короткого URL

```java
class URLShortener {
    private static final String BASE62 = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";

    String shorten(String longUrl) {
        // Генерация уникального ID
        long id = generateUniqueId();

        // Конвертация в base62
        String shortCode = encodeBase62(id);

        // Сохранение в БД
        database.save(shortCode, longUrl);

        return "https://short.ly/" + shortCode;
    }

    String redirect(String shortCode) {
        // Проверка кэша
        String longUrl = cache.get(shortCode);
        if (longUrl != null) {
            return longUrl;
        }

        // Запрос к БД
        longUrl = database.get(shortCode);

        // Сохранение в кэш
        cache.put(shortCode, longUrl);

        return longUrl;
    }
}
```

## Лучшие практики

При проектировании систем придерживайтесь следующего порядка и приоритетов:

- Начинайте с требований (нагрузка, латентность, консистентность), затем выбирайте паттерны (репликация, шардинг, кэш).
- Заложите мониторинг и алертинг с самого начала; метрики и логи — основа диагностики.
- Документируйте архитектурные решения (ADR); фиксируйте компромиссы и причины выбора технологий.
- Горизонтальное масштабирование предпочтительнее вертикального, где это возможно.
- Учитывайте отказоустойчивость: **graceful degradation**, **retry**, **circuit breaker**, идемпотентность операций.
## Решение проблем

| Симптом | Возможная причина | Решение |
|--------|-------------------|---------|
| Узкое место при росте нагрузки | Один компонент не масштабируется | Горизонтальное масштабирование (реплики + LB); кэш, шардирование БД; асинхронная обработка |
| Единая точка отказа | Нет репликации и отказоустойчивости | Реплики, несколько AZ; health checks и автоматическое переключение |
| Сложно оценить масштаб | Нет цифр по RPS, объёму данных | Зафиксировать требования (QPS, латентность, консистентность); прикинуть объём данных и трафика |

## Частые вопросы

**С чего начать проектирование системы?** С требований: функциональных и нефункциональных (нагрузка, латентность, доступность, консистентность). Затем высокоуровневая схема (клиент, API, сервисы, БД, кэш, очереди) и оценка узких мест.

**Когда нужен кэш?** При частом чтении одних и тех же данных и допустимой задержке обновления. Размещать ближе к потребителю (in-memory, Redis); продумать инвалидацию и TTL.

**Как обеспечить консистентность в распределённой системе?** Strong consistency — ограничить репликацию (лидер + синхронные реплики). Для масштаба часто выбирают eventual consistency, идемпотентность операций и компенсирующие действия при сбоях.


## См. также

- [[adr-template|Architectural Decision Records]] — шаблон ADR
- [[design-principles|Design Principles]] — принципы проектирования
- [[enterprise-patterns-overview|Enterprise Patterns]] — обзор enterprise-паттернов
- [[microservices|Microservices]] — микросервисная архитектура

