# Couchbase: Основы

**Комплексное руководство по использованию Couchbase — распределенной NoSQL базы данных.**

**Дата последнего обновления:** 2026-01-25

## Полезные ссылки

### Официальная документация
- [Couchbase Documentation](https://docs.couchbase.com/) - Официальная документация

### См. также
- `../mongodb/mongodb-basics.md` - MongoDB
- `../redis/redis-basics.md` - Redis

## Содержание

- [Введение в Couchbase](#введение-в-couchbase)
- [Установка](#установка)
- [N1QL запросы](#n1ql-запросы)
- [Интеграция с Java](#интеграция-с-java)

## Введение в Couchbase

**Couchbase** — распределенная NoSQL база данных, сочетающая возможности key-value хранилища и документной БД.

### Основные возможности

- Key-Value и Document модели
- N1QL (SQL для JSON)
- Распределенная архитектура
- Высокая производительность

## Установка

```bash
# Docker установка
docker run -d --name couchbase \
  -p 8091-8096:8091-8096 \
  -p 11210-11211:11210-11211 \
  couchbase:latest
```

## N1QL запросы

```sql
-- N1QL запросы (SQL для JSON)
SELECT username, email 
FROM `users` 
WHERE balance > 1000;
```

## Интеграция с Java

```java
// Couchbase Java SDK
Cluster cluster = Cluster.connect("localhost", "username", "password");
Bucket bucket = cluster.bucket("mybucket");
Collection collection = bucket.defaultCollection();
```

---

*Обновлено: 2026-01-25*
