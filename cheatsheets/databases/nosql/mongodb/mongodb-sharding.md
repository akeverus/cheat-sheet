---
title: "MongoDB: Шардирование - Горизонтальное масштабирование и распределение данных"
description: "Комплексное руководство по шардированию в MongoDB: архитектура, настройка, управление шардами и оптимизация производительности."
tags:
  - databases
  - nosql
  - mongodb-sharding
difficulty: "intermediate"
prerequisites: []
next: []
updated: "2026-02-11"
---
# MongoDB: Шардирование — Горизонтальное масштабирование и распределение данных

Комплексное руководство по шардированию в **MongoDB**: архитектура, настройка, управление шардами и оптимизация производительности.

## Полезные ссылки

### Официальная документация
- [MongoDB Sharding](https://www.mongodb.com/docs/manual/sharding/)
- [Shard Keys](https://www.mongodb.com/docs/manual/core/sharding-shard-key/)
- [Sharded Cluster Administration](https://www.mongodb.com/docs/manual/administration/sharded-cluster/)

### Обучающие материалы
- [MongoDB Sharding](https://www.baeldung.com/java-mongodb-sharding)

### См. также
- [[mongodb-replication|Репликация]] — основа шардирования
- [[mongodb-performance|Производительность]] — шардированные кластеры

## Содержание

- [Введение в шардирование **MongoDB**](#введение-в-шардирование-mongodb)
  - [Преимущества шардирования](#преимущества-шардирования)
  - [Недостатки шардирования](#недостатки-шардирования)
- [Архитектура **Sharded Cluster**](#архитектура-sharded-cluster)
  - [Компоненты кластера](#компоненты-кластера)
    - [**Shard** (Шард)](#shard-шард)
    - [**Config Server** (Сервер конфигурации)](#config-server-сервер-конфигурации)
    - [**Mongos** (Маршрутизатор)](#mongos-маршрутизатор)
  - [Логическая архитектура](#логическая-архитектура)
- [**Shard Key** (Ключ шардирования)](#shard-key-ключ-шардирования)
  - [Выбор **Shard Key**](#выбор-shard-key)
    - [Характеристики хорошего **shard key**](#характеристики-хорошего-shard-key)
    - [Типы **shard keys**](#типы-shard-keys)
      - [**Ranged Sharding** (Диапазонное шардирование)](#ranged-sharding-диапазонное-шардирование)

## Введение в шардирование MongoDB

**Шардирование** (sharding) — это метод горизонтального масштабирования **MongoDB**, при котором данные распределяются между несколькими серверами (шардами). Каждый шард содержит подмножество данных и работает как независимый **replica set**.

Схема шардированного кластера **MongoDB**: **Shards**, **Config Servers**, **Mongos**.

```text
┌─────────────────────────────────────────────────────────────┐
│                     MongoDB Sharded Cluster                 │
├─────────────────────────────────────────────────────────────┤
│  ┌─────────────┐    ┌─────────────┐    ┌─────────────┐      │
│  │   Shard 1   │    │   Shard 2   │    │   Shard 3   │      │
│  │  RS: P+S+S  │    │  RS: P+S+S  │    │  RS: P+S+S  │      │
│  └─────────────┘    └─────────────┘    └─────────────┘      │
│         │                    │                    │         │
│         └────────────┬───────┴────────────┬───────┘         │
│                      │                    │                  │
│               ┌─────────────┐      ┌─────────────┐          │
│               │ Config      │      │   Mongos    │          │
│               │ Servers     │      │   Router    │          │
│               │ RS: P+S+S  │      │             │          │
│               └─────────────┘      └─────────────┘          │
└─────────────────────────────────────────────────────────────┘
```

### Преимущества шардирования

1. **Горизонтальное масштабирование**: Неограниченный рост хранилища и производительности
2. **Высокая доступность**: Комбинация с репликацией
3. **Автоматическая балансировка**: Равномерное распределение данных
4. **Географическое распределение**: Данные ближе к пользователям
5. **Изоляция нагрузки**: Разделение горячих и холодных данных

### Недостатки шардирования

1. **Сложность**: Более сложная архитектура и обслуживание
2. **Операции**: Некоторые запросы требуют координации между шардами
3.. **Индексы**: **Shard key** влияет на производительность запросов
4. **Миграции**: Перемещение данных между шардами

## Архитектура Sharded Cluster

### Компоненты кластера

#### Shard (Шард)
- **Роль**: Хранит подмножество данных
- **Структура**: **Replica set** (обычно 3+ узла)
- **Функции**: **CRUD** операции, локальные индексы

#### Config Server (Сервер конфигурации)
- **Роль**: Хранит метаданные кластера
- **Структура**: **Replica set** (обычно 3 узла)
- **Функции**: Информация о шардах, чанках, коллекциях

#### Mongos (Маршрутизатор)
- **Роль**: Маршрутизация запросов к шардам
- **Структура**: Легковесный процесс (несколько инстансов)
- **Функции**: Разбор запросов, маршрутизация, агрегация результатов

### Логическая архитектура

```text
Application
    │
    ▼
┌─────────────┐
│   Mongos    │ ← Query Router
│   Router    │
└─────────────┘
       │
       ▼
┌─────────────┐    ┌─────────────┐
│ Config      │    │ Shard Key   │
│ Servers     │    │ Evaluation  │
│             │    │             │
│ • Metadata  │    │ • Routing   │
│ • Chunks    │    │ • Target    │
│ • Shards    │    │ • Shard     │
└─────────────┘    └─────────────┘
       │                    │
       └─────────┬──────────┘
                 │
                 ▼
        ┌─────────────────┐
        │     Shards      │
        │  (Data Storage) │
        │                 │
        │ • Chunk Storage │
        │ • Replication   │
        │ • Local Indexes │
        └─────────────────┘
```

## Shard Key (Ключ шардирования)

### Выбор Shard Key

**Shard key** определяет, как данные распределяются между шардами.

#### Характеристики хорошего shard key

1. **Высокая кардинальность**: Много уникальных значений
2. **Равномерное распределение**: Данные равномерно распределяются
3. **Неизменяемость**: Значение не меняется со временем
4. **Частое использование**: Используется в запросах

#### Типы shard keys

##### Ranged Sharding (Диапазонное шардирование)

```java
// Java + Spring implementation available
``````java
// `Java` + `Spring implementation available`
``````java
// Java + Spring implementation available
``````java
// `Java` + `Spring implementation available`
``````java
// Java + Spring implementation available
``````java
// `Java` + `Spring implementation available`
``````java
// Java + Spring implementation available
``````java
// `Java` + `Spring implementation available`
``````java
// Java + Spring implementation available
``````java
// `Java` + `Spring implementation available`
``````java
// Java + Spring implementation available
``````java
// `Java` + `Spring implementation available`
``````java
// Java + Spring implementation available
``````java
// `Java` + `Spring implementation available`
``````java
// Java + Spring implementation available
``````java
// `Java` + `Spring implementation available`
``````java
// Java + Spring implementation available
``````java
// `Java` + `Spring implementation available`
``````java
// Java + Spring implementation available
``````java
// `Java` + `Spring implementation available`
``````java
// Java + Spring implementation available
``````java
// `Java` + `Spring implementation available`
``````java
// Java + Spring implementation available
``````java
// `Java` + `Spring implementation available`
``````java
// Java + Spring implementation available
```