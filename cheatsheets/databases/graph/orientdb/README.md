---
title: "OrientDB"
description: "Точка входа в OrientDB: мультимодельная NoSQL СУБД, сочетающая графовую, документную, key-value и объектную модели."
tags:
  - meta
  - index
  - databases
  - graph
  - orientdb
type: "index"
aliases:
  - "OrientDB"
prerequisites: []
next: []
updated: "2026-04-20"
---
# OrientDB

OrientDB — мультимодельная NoSQL СУБД, которая в одной инсталляции поддерживает графовую, документную, key-value и объектную модели данных. Использует расширенный SQL и Gremlin. Подходит для сценариев, где одна часть данных естественно ложится в документы (профили, товары), а другая — в граф (связи, подписки, товарные отношения), и не хочется держать два разных стека.

Для кого: команды, которым нужен гибрид graph + document без двух отдельных хранилищ. Если нужен чистый и зрелый графовый опыт — Neo4j; если документы — MongoDB; если маленький проект с желанием минимизировать инфраструктуру — OrientDB даёт один процесс для нескольких моделей.

## Полезные ссылки

### Основные документы
- [OrientDB: Основы](orientdb-basics.md) — модель, установка, работа с графами, интеграция с Java

### Соседние разделы
- [Графовые БД](../../../basics/README.md)
- [Neo4j](../../../basics/README.md) — нативная графовая альтернатива
- [NoSQL](../../../basics/README.md)
- [MongoDB](../../nosql/mongodb/)
- [Базы данных](../../../basics/README.md)

### Внешние ресурсы
- [OrientDB Documentation](https://orientdb.org/docs/)
- [OrientDB GitHub](https://github.com/orientechnologies/orientdb)
- [Apache TinkerPop / Gremlin](https://tinkerpop.apache.org/)

## Содержание

- [Что внутри раздела](#что-внутри-раздела)
- [Когда брать OrientDB](#когда-брать-orientdb)
- [OrientDB vs альтернативы](#orientdb-vs-альтернативы)
- [Маршруты чтения](#маршруты-чтения)
- [Куда идти дальше](#куда-идти-дальше)

## Что внутри раздела

[orientdb-basics](orientdb-basics.md) покрывает:

- Мультимодельную концепцию (document + graph + key-value + object)
- Установку через Docker
- Работу с классами вершин и рёбер, extended SQL
- Интеграцию с Java
- Базовый troubleshooting и FAQ

## Когда брать OrientDB

- Нужны **несколько моделей** (документы + граф) без двух разных БД.
- Небольшая/средняя нагрузка, где компромисс по «специализированной производительности» приемлем.
- Знакомая команда: хочется писать SQL-подобные запросы, а не отдельный язык.

**Когда не брать:**
- Чистый графовый use-case с высокой нагрузкой — Neo4j надёжнее.
- Чисто документная база — MongoDB / Couchbase зрелее.
- Требуется большое комьюнити и активная поддержка — активность OrientDB ниже, чем у Neo4j и MongoDB; учитывайте это при долгосрочной ставке.

## OrientDB vs альтернативы

| Критерий | OrientDB | Neo4j | MongoDB | ArangoDB |
|----------|----------|-------|---------|----------|
| Модели | Graph + Doc + KV + Object | Graph | Document | Graph + Doc + KV |
| Язык | Extended SQL + Gremlin | Cypher | MQL + Aggregation | AQL |
| Графовые возможности | Средние | Глубокие | Нет | Хорошие |
| Зрелость экосистемы | Средняя | Высокая | Очень высокая | Средняя |
| Когда выбирать | Гибрид в одной БД, зоопарк моделей | Чистый граф | Документы без связей | Прямой мультимодельный конкурент |

## Маршруты чтения

- **Ознакомление (30-60 мин):** `orientdb-basics.md` целиком.
- **Сравнение с Neo4j:** `orientdb-basics.md` + `../neo4j/neo4j-basics.md`, сравнить Cypher и Extended SQL на одинаковых запросах.
- **Выбор хранилища:** этот README + раздел «vs альтернативы» + [графовые БД](../../../basics/README.md).

## Куда идти дальше

- Сравнение графовых БД — [графовые БД](../../../basics/README.md)
- Обзор NoSQL — [databases/nosql/](../../../basics/README.md)
- Проектирование данных — [databases/README.md](../../../basics/README.md)
- Интервью по БД — [interview/databases/](../../../interview/databases/database-architecture-interview.md)
