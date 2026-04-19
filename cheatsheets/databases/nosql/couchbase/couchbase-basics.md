---
title: "Couchbase: Основы"
description: "Комплексное руководство по использованию Couchbase — распределенной NoSQL базы данных."
tags:
  - databases
  - nosql
  - couchbase-basics
difficulty: "intermediate"
prerequisites: []
next: []
updated: "2026-02-11"
---
# **Couchbase**: Основы

**Комплексное руководство по использованию `Couchbase` — распределенной `NoSQL` базы данных.**

## Полезные ссылки

### Официальная документация
- [Couchbase Documentation](https://docs.couchbase.com/) — официальная документация **Couchbase**

### См. также
- [MongoDB](../mongodb/mongodb-basics.md) — документная **NoSQL** БД
- [Redis](../redis/redis-basics.md) — **key-value** хранилище

## Содержание

- [**Couchbase**: Основы](#couchbase-основы)
- [Введение в **Couchbase**](#введение-в-couchbase)
  - [Основные возможности](#основные-возможности)
- [Установка](#установка)
- [Docker установка](#docker-установка)
- [**N1QL** запросы](#n1ql-запросы)
- [Интеграция с **Java**](#интеграция-с-java)
- [Troubleshooting](#решение-проблем)
- [FAQ](#частые-вопросы)

## Введение в **Couchbase**

**Couchbase** — распределенная **NoSQL** база данных, сочетающая возможности **key-value** хранилища и документной БД.

### Основные возможности

- **Key-Value** и **Document** модели
- **N1QL** (**SQL для JSON**)
- Распределенная архитектура
- Высокая производительность

## Установка

Запуск **Couchbase** в **Docker**: порты веб-консоли (**8091–8096**) и протокола (**11210–11211**).

```bash
# Docker установка
docker run -d --name couchbase \
  -p 8091-8096:8091-8096 \
  -p 11210-11211:11210-11211 \
  couchbase:latest
```

## **N1QL** запросы

```sql
-- N1QL запросы (SQL для JSON)
SELECT username, email 
FROM `users` 
WHERE balance > 1000;
```

## Интеграция с **Java**

```java
// Couchbase Java SDK
Cluster cluster = Cluster.connect("localhost", "username", "password");
Bucket bucket = cluster.bucket("mybucket");
Collection collection = bucket.defaultCollection();
```

## Решение проблем

| Симптом | Возможная причина | Решение |
|--------|-------------------|---------|
| Подключение к кластеру не удаётся | Неверный host/порт, firewall | Проверить 11210 (KV), 8091 (консоль); с кластером указывать несколько узлов для bootstrap |
| N1QL запрос медленный | Нет индекса по полям запроса | Создать индекс по полям WHERE/JOIN; использовать EXPLAIN для плана |
| OutOfMemory при большом результате | Загрузка всего результата в память | Пагинация, LIMIT/OFFSET; стриминг при поддержке SDK |

## Частые вопросы

**Couchbase vs MongoDB для документов?** Couchbase даёт N1QL (SQL-подобный запрос по JSON), встроенный кэш и мульти-узловой кластер «из коробки». MongoDB — богатый query API и экосистема. Выбор по предпочтениям команды и сценариям (кэш, мобильная синхронизация).

**Нужны ли индексы для N1QL?** Да. Primary index на bucket для доступа по ключу; secondary — для полей в WHERE, JOIN, сортировке. Без подходящего индекса запрос может быть очень медленным.

