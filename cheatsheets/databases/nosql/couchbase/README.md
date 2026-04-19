---
title: "Couchbase"
description: "Точка входа в Couchbase: распределённая NoSQL СУБД, сочетающая key-value скорость и документную модель с SQL-подобным языком N1QL."
tags:
  - meta
  - index
  - databases
  - nosql
  - couchbase
type: "index"
updated: "2026-04-17"
---
# Couchbase

Couchbase — распределённая NoSQL-СУБД, которая совмещает key-value скорость (memory-first архитектура) и гибкость документов JSON. Основной язык запросов — N1QL (SQL поверх документов). Из коробки даёт шардирование, репликацию (включая cross-datacenter), встроенный кэш, полнотекстовый поиск, eventing и аналитические сервисы в единой платформе.

Для кого: продукты с высокими требованиями к latency (<1 мс P99), большими объёмами сессионных данных, профилями пользователей, каталогами товаров. Если нужен чистый документный mainstream — MongoDB имеет более широкую экосистему. Если нужен только key-value кэш — Redis. Couchbase выбирают, когда хочется один кластер вместо связки «MongoDB + Redis».

## Полезные ссылки

### Основные документы
- [Couchbase: Основы](couchbase-basics.md) — архитектура, установка, N1QL, интеграция с Java

### Соседние разделы
- [NoSQL базы данных](../README.md)
- [MongoDB](../mongodb/) — документная альтернатива
- [Redis](../redis/README.md) — key-value альтернатива
- [Базы данных](../../README.md)

### Внешние ресурсы
- [Couchbase Documentation](https://docs.couchbase.com/)
- [N1QL Language Reference](https://docs.couchbase.com/server/current/n1ql/n1ql-language-reference/index.html)
- [Couchbase GitHub](https://github.com/couchbase)
- [Baeldung: Couchbase](https://www.baeldung.com/spring-data-couchbase)

## Содержание

- [Что внутри раздела](#что-внутри-раздела)
- [Когда брать Couchbase](#когда-брать-couchbase)
- [Couchbase vs альтернативы](#couchbase-vs-альтернативы)
- [Маршруты чтения](#маршруты-чтения)
- [Куда идти дальше](#куда-идти-дальше)

## Что внутри раздела

[couchbase-basics.md](couchbase-basics.md) покрывает:

- Архитектуру: ноды, сервисы (Data, Index, Query, Search, Eventing, Analytics), buckets, scopes, collections
- Установку через Docker
- Основы N1QL (SELECT, UPDATE, индексы)
- Интеграцию с Java через Couchbase SDK и Spring Data Couchbase
- Troubleshooting и FAQ

## Когда брать Couchbase

- Нужна **низкая задержка** на больших объёмах документов (сессии, профили, каталоги).
- Требуется **in-memory cache + persistent store** в одном сервисе (встроенный memcached-compatible слой).
- Важна **cross-datacenter репликация** (XDCR) для гео-распределённого приложения.
- Хочется **SQL-подобного** языка поверх JSON-документов (N1QL).

**Когда не брать:**
- Маленький проект или MVP — MongoDB / PostgreSQL проще администрировать.
- Сложные транзакции поверх нескольких сущностей — выбирайте реляционную СУБД.
- Чистый кэш — Redis даст меньшую операционную нагрузку.
- Тяжёлая аналитика по миллиардам строк — ClickHouse.

## Couchbase vs альтернативы

| Критерий | Couchbase | MongoDB | Redis | Cassandra |
|----------|-----------|---------|-------|-----------|
| Модель | Document + KV + Cache | Document | KV (+ структуры) | Wide column |
| Язык | N1QL (SQL-like) | MQL + Aggregation | Commands + Lua | CQL (SQL-like) |
| Встроенный кэш | Да (memory-first) | Частично (WiredTiger) | Весь — кэш | Нет |
| Репликация гео | XDCR | Replica sets + shards | Redis Enterprise | Multi-DC native |
| Латентность | Очень низкая (sub-ms) | Низкая | Сверхнизкая | Низкая |
| Когда выбирать | KV + Document в одном, low-latency | Documents mainstream | Кэш и быстрые структуры | Write-heavy, multi-DC |

## Маршруты чтения

- **Ознакомление (1 час):** `couchbase-basics.md` → поднять в Docker → попробовать N1QL.
- **Замена связки MongoDB + Redis:** basics → сравнить примеры с MongoDB → оценить эксплуатацию.
- **Интеграция со Spring:** basics → Spring Data Couchbase → репозитории в своём проекте.

## Куда идти дальше

- NoSQL в целом — [databases/nosql/README.md](../README.md)
- Документные альтернативы — [MongoDB](../mongodb/)
- Key-value альтернатива — [Redis](../redis/README.md)
- Интервью по БД — [interview/databases/mongodb-interview.md](../../../interview/databases/mongodb-interview.md)
