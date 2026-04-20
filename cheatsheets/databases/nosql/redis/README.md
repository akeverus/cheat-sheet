---
title: "Redis"
description: "Точка входа в Redis: in-memory хранилище данных для кэша, очередей, счётчиков, геоиндексов, pub/sub и потоков."
tags:
  - meta
  - index
  - databases
  - nosql
  - redis
type: "index"
updated: "2026-04-20"
---
# Redis

Redis — in-memory хранилище данных с поддержкой богатых структур (strings, hashes, lists, sets, sorted sets, streams, bitmaps, HyperLogLog, geo). Используется как кэш, брокер сообщений, rate limiter, session store, лидерборд, геоиндекс, координатор распределённых блокировок. Один из самых универсальных инструментов в backend-стеке: латентность в десятки микросекунд, простая семантика, огромная экосистема клиентов.

Этот раздел — большой справочник: отдельные файлы по каждой важной теме (структуры, persistence, репликация, кластер, streams, pub/sub, lua, безопасность, наблюдаемость, troubleshooting). Для общей картины читайте `redis-basics.md` + `redis.md`; для решения конкретной задачи — прыжком в профильный файл. Если вам нужна full-fledged документная БД — берите MongoDB / Couchbase; если нужна OLAP-аналитика — ClickHouse; Redis же оптимален там, где нужны скорость и структуры данных.

## Полезные ссылки

### Основные документы
- [Redis: Основы](redis-basics.md) — установка, команды, подключение
- [Redis обзорно](redis.md) — быстрый overview
- [Структуры данных](redis-data-structures.md) — strings, hashes, lists, sets, sorted sets
- [Persistence](redis-persistence.md) — RDB, AOF, гибрид
- [Репликация](redis-replication.md) — master/replica
- [High Availability](redis-high-availability.md) — Sentinel
- [Clustering](redis-clustering.md) — Redis Cluster, sharding, slots
- [Streams](redis-streams.md) — XADD, XREAD, consumer groups
- [Pub/Sub](redis-pubsub.md) — publish/subscribe
- [Transactions](redis-transactions.md) — MULTI/EXEC, WATCH
- [Lua scripting](redis-lua-scripting.md) — EVAL, атомарные скрипты
- [Geospatial](redis-geospatial.md) — GEO команды
- [Performance](redis-performance.md) — настройка, бенчмарки
- [Monitoring](redis-monitoring.md) — INFO, latency, slowlog
- [Security](redis-security.md) — AUTH, ACL, TLS
- [Troubleshooting](redis-troubleshooting.md) — диагностика

### Соседние разделы
- [NoSQL базы данных](../../../basics/README.md)
- [Couchbase](../../../basics/README.md) — альтернатива для KV + Document
- [MongoDB](../mongodb/)
- [Messaging / Streaming](../../../development/messaging/)
- [Spring Data Redis](../../../frameworks/java-frameworks/spring/)
- [Кэширование в backend](../../../development/web-backend/)

### Внешние ресурсы
- [Redis Documentation](https://redis.io/docs/)
- [Redis Commands](https://redis.io/commands/)
- [Redis University](https://university.redis.com/) — бесплатные курсы
- [Redis Best Practices](https://redis.io/docs/management/optimization/)

## Содержание

- [Карта тем](#карта-тем)
- [Когда брать Redis](#когда-брать-redis)
- [Redis vs альтернативы](#redis-vs-альтернативы)
- [Маршруты чтения](#маршруты-чтения)
- [Куда идти дальше](#куда-идти-дальше)

## Карта тем

| Тема | Файл |
|------|------|
| Первые шаги и CLI | [redis-basics](redis-basics.md) |
| Все структуры данных | [redis-data-structures](redis-data-structures.md) |
| Сохранение на диск (RDB/AOF) | [redis-persistence](redis-persistence.md) |
| Репликация master/replica | [redis-replication](redis-replication.md) |
| HA через Sentinel | [redis-high-availability](redis-high-availability.md) |
| Redis Cluster | [redis-clustering](redis-clustering.md) |
| Streams (событийная шина) | [redis-streams](redis-streams.md) |
| Pub/Sub | [redis-pubsub](redis-pubsub.md) |
| Транзакции | [redis-transactions](redis-transactions.md) |
| Lua-скрипты | [redis-lua-scripting](redis-lua-scripting.md) |
| Геоиндексы | [redis-geospatial](redis-geospatial.md) |
| Производительность | [redis-performance](redis-performance.md) |
| Мониторинг | [redis-monitoring](redis-monitoring.md) |
| Безопасность | [redis-security](redis-security.md) |
| Диагностика | [redis-troubleshooting](redis-troubleshooting.md) |

## Когда брать Redis

- **Кэш перед БД**: снять чтение с PostgreSQL/Oracle, ускорить hot-path.
- **Session store**: разделить состояние между инстансами stateless-сервисов.
- **Rate limiting / counters**: INCR, TTL, sliding window через sorted sets.
- **Distributed locks**: SET NX EX, Redlock (с оговорками).
- **Очереди и стриминг**: LPUSH/BRPOP, Streams с consumer groups.
- **Leaderboards**: sorted sets с O(log N) операциями.
- **Pub/Sub в пределах одного кластера**: быстрые уведомления.
- **Геопоиск**: GEOSEARCH для ближайших объектов.

**Когда не брать:**
- Большие объёмы, не помещающиеся в RAM — дорого. Используйте диск-ориентированные БД.
- Основной источник истины для бизнес-данных — нужны гарантии транзакций и сложные запросы (Postgres).
- Тяжёлая аналитика — ClickHouse.
- Полнотекстовый поиск — Elasticsearch / OpenSearch (или RediSearch модуль как компромисс).

## Redis vs альтернативы

| Критерий | Redis | Memcached | Couchbase | Hazelcast |
|----------|-------|-----------|-----------|-----------|
| Модель | KV + структуры | Plain KV | KV + Document | Distributed objects + compute |
| Persistence | RDB/AOF | Нет | Полный | Опциональный |
| Структуры данных | Много (list/set/hash/zset/stream) | Нет | Документы | Collections API |
| Кластеризация | Redis Cluster | Ручной sharding | Встроенная | Встроенная |
| Pub/Sub & Streams | Да | Нет | Через Eventing | Да |
| Когда выбирать | Универсальный кэш + структуры | Только простой кэш | KV + полноценные документы | Java-ориентированная распредобработка |

## Маршруты чтения

- **Junior backend (полдня):** `redis-basics.md` `redis-data-structures.md` простейший кэш в своём приложении.
- **Production-готовность:** `persistence` `replication` `high-availability` `monitoring` `security`.
- **Нагрузочные режимы:** `clustering` `performance` `troubleshooting`.
- **Event-driven:** `streams` `pubsub` сравнение с Kafka в `development/messaging/`.
- **Сложные паттерны:** `lua-scripting` `transactions` идемпотентные операции.

## Куда идти дальше

- Обзор NoSQL — [databases/nosql/README.md](../../../basics/README.md)
- Kafka и брокеры — [development/messaging/](../../../development/messaging/)
- Кэширование в backend — [development/web-backend/](../../../development/web-backend/)
- Паттерны интеграции и кэширования — [architecture/caching-strategies-interview.md](../../../interview/architecture/caching-strategies-interview.md)
- Интервью по Redis — [interview/databases/redis-interview.md](../../../interview/databases/redis-interview.md)
