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
updated: "2026-04-17"
---
# Redis

Redis — in-memory хранилище данных с поддержкой богатых структур (strings, hashes, lists, sets, sorted sets, streams, bitmaps, HyperLogLog, geo). Используется как кэш, брокер сообщений, rate limiter, session store, лидерборд, геоиндекс, координатор распределённых блокировок. Один из самых универсальных инструментов в backend-стеке: латентность в десятки микросекунд, простая семантика, огромная экосистема клиентов.

Этот раздел — большой справочник: отдельные файлы по каждой важной теме (структуры, persistence, репликация, кластер, streams, pub/sub, lua, безопасность, наблюдаемость, troubleshooting). Для общей картины читайте `redis-basics.md` + `redis.md`; для решения конкретной задачи — прыжком в профильный файл. Если вам нужна full-fledged документная БД — берите MongoDB / Couchbase; если нужна OLAP-аналитика — ClickHouse; Redis же оптимален там, где нужны скорость и структуры данных.

## Полезные ссылки

### Основные документы
- [[redis-basics|Redis: Основы]] — установка, команды, подключение
- [[redis|Redis обзорно]] — быстрый overview
- [[redis-data-structures|Структуры данных]] — strings, hashes, lists, sets, sorted sets
- [[redis-persistence|Persistence]] — RDB, AOF, гибрид
- [[redis-replication|Репликация]] — master/replica
- [[redis-high-availability|High Availability]] — Sentinel
- [[redis-clustering|Clustering]] — Redis Cluster, sharding, slots
- [[redis-streams|Streams]] — XADD, XREAD, consumer groups
- [[redis-pubsub|Pub/Sub]] — publish/subscribe
- [[redis-transactions|Transactions]] — MULTI/EXEC, WATCH
- [[redis-lua-scripting|Lua scripting]] — EVAL, атомарные скрипты
- [[redis-geospatial|Geospatial]] — GEO команды
- [[redis-performance|Performance]] — настройка, бенчмарки
- [[redis-monitoring|Monitoring]] — INFO, latency, slowlog
- [[redis-security|Security]] — AUTH, ACL, TLS
- [[redis-troubleshooting|Troubleshooting]] — диагностика

### Соседние разделы
- [[README|NoSQL базы данных]]
- [[README|Couchbase]] — альтернатива для KV + Document
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
| Первые шаги и CLI | [[redis-basics]] |
| Все структуры данных | [[redis-data-structures]] |
| Сохранение на диск (RDB/AOF) | [[redis-persistence]] |
| Репликация master/replica | [[redis-replication]] |
| HA через Sentinel | [[redis-high-availability]] |
| Redis Cluster | [[redis-clustering]] |
| Streams (событийная шина) | [[redis-streams]] |
| Pub/Sub | [[redis-pubsub]] |
| Транзакции | [[redis-transactions]] |
| Lua-скрипты | [[redis-lua-scripting]] |
| Геоиндексы | [[redis-geospatial]] |
| Производительность | [[redis-performance]] |
| Мониторинг | [[redis-monitoring]] |
| Безопасность | [[redis-security]] |
| Диагностика | [[redis-troubleshooting]] |

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

- **Junior backend (полдня):** `redis-basics.md` → `redis-data-structures.md` → простейший кэш в своём приложении.
- **Production-готовность:** `persistence` → `replication` → `high-availability` → `monitoring` → `security`.
- **Нагрузочные режимы:** `clustering` → `performance` → `troubleshooting`.
- **Event-driven:** `streams` → `pubsub` → сравнение с Kafka в `development/messaging/`.
- **Сложные паттерны:** `lua-scripting` → `transactions` → идемпотентные операции.

## Куда идти дальше

- Обзор NoSQL — [[README|databases/nosql/README.md]]
- Kafka и брокеры — [development/messaging/](../../../development/messaging/)
- Кэширование в backend — [development/web-backend/](../../../development/web-backend/)
- Паттерны интеграции и кэширования — [[caching-strategies-interview|architecture/caching-strategies-interview.md]]
- Интервью по Redis — [[redis-interview|interview/databases/redis-interview.md]]
