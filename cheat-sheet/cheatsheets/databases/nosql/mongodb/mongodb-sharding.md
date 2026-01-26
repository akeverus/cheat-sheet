# MongoDB: Шардирование - Горизонтальное масштабирование и распределение данных

Комплексное руководство по шардированию в MongoDB: архитектура, настройка, управление шардами и оптимизация производительности.

**Дата последнего обновления:** 2026-01-21

## Полезные ссылки

### Официальная документация
- [MongoDB Sharding](https://docs.mongodb.com/manual/sharding/)
- [Shard Keys](https://docs.mongodb.com/manual/core/sharding-shard-key/)
- [Sharded Cluster Administration](https://docs.mongodb.com/manual/core/sharded-cluster-administrative-operations/)

### Baeldung
- [MongoDB Sharding](https://www.baeldung.com/mongodb-sharding)

### См. также
- `databases/mongodb/mongodb-replication.md` - Репликация как основа шардирования
- `databases/mongodb/mongodb-performance.md` - Производительность шардированных кластеров

## Содержание

- [Введение в шардирование MongoDB](#введение-в-шардирование-mongodb)
- [Архитектура Sharded Cluster](#архитектура-sharded-cluster)
- [Shard Key (Ключ шардирования)](#shard-key-ключ-шардирования)
- [Настройка Sharded Cluster](#настройка-sharded-cluster)
- [Управление шардами](#управление-шардами)
- [Chunks (Чанки)](#chunks-чанки)
- [Запросы в шардированном кластере](#запросы-в-шардированном-кластере)
- [Мониторинг и обслуживание](#мониторинг-и-обслуживание)
- [Безопасность шардированного кластера](#безопасность-шардированного-кластера)
- [Распространенные проблемы и решения](#распространенные-проблемы-и-решения)
- [Best Practices](#best-practices)
- [Заключение](#заключение)

## Введение в шардирование MongoDB

**Шардирование** (sharding) — это метод горизонтального масштабирования MongoDB, при котором данные распределяются между несколькими серверами (шардами). Каждый шард содержит подмножество данных и работает как независимый replica set.

```
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
3.. **Индексы**: Shard key влияет на производительность запросов
4. **Миграции**: Перемещение данных между шардами

## Архитектура Sharded Cluster

### Компоненты кластера

#### Shard (Шард)
- **Роль**: Хранит подмножество данных
- **Структура**: Replica set (обычно 3+ узла)
- **Функции**: CRUD операции, локальные индексы

#### Config Server (Сервер конфигурации)
- **Роль**: Хранит метаданные кластера
- **Структура**: Replica set (обычно 3 узла)
- **Функции**: Информация о шардах, чанках, коллекциях

#### Mongos (Маршрутизатор)
- **Роль**: Маршрутизация запросов к шардам
- **Структура**: Легковесный процесс (несколько инстансов)
- **Функции**: Разбор запросов, маршрутизация, агрегация результатов

### Логическая архитектура

```
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

Shard key определяет, как данные распределяются между шардами.

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
// Java + Spring implementation available
``````java
// Java + Spring implementation available
``````java
// Java + Spring implementation available
``````java
// Java + Spring implementation available
``````java
// Java + Spring implementation available
``````java
// Java + Spring implementation available
``````java
// Java + Spring implementation available
``````java
// Java + Spring implementation available
``````java
// Java + Spring implementation available
``````java
// Java + Spring implementation available
``````java
// Java + Spring implementation available
``````java
// Java + Spring implementation available
``````java
// Java + Spring implementation available
``````java
// Java + Spring implementation available
``````java
// Java + Spring implementation available
``````java
// Java + Spring implementation available
``````java
// Java + Spring implementation available
``````java
// Java + Spring implementation available
``````java
// Java + Spring implementation available
``````java
// Java + Spring implementation available
``````java
// Java + Spring implementation available
``````java
// Java + Spring implementation available
``````java
// Java + Spring implementation available
``````java
// Java + Spring implementation available
```