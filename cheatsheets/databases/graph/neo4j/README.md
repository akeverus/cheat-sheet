---
title: "Neo4j"
description: "Точка входа в Neo4j: нативная графовая СУБД с языком Cypher для задач, где связи важнее самих записей."
tags:
  - meta
  - index
  - databases
  - graph
  - neo4j
type: "index"
updated: "2026-04-17"
---
# Neo4j

Neo4j — нативная графовая база данных с property graph моделью и декларативным языком запросов Cypher. Оптимизирована для задач с сильносвязанными данными: соцсети, рекомендательные системы, маршрутизация, графы знаний, анти-фрод, impact analysis в IT-инфраструктуре. Там, где SQL требует много JOIN-ов по нескольким уровням связей, Cypher даёт линейную цену обхода.

Для кого: backend-разработчики и аналитики, которым нужно хранить и анализировать связи как первоклассные сущности. Когда связи редки или модель плоская — реляционная БД (PostgreSQL) и документная (MongoDB) обойдутся дешевле; когда граф огромный и требуется распределённая обработка — стоит смотреть на JanusGraph / DGraph / Amazon Neptune.

## Полезные ссылки

### Основные документы
- [Neo4j: Основы](neo4j-basics.md) — архитектура, Cypher, установка, интеграция с Java

### Соседние разделы
- [Графовые БД](../README.md)
- [OrientDB](../orientdb/README.md) — мультимодельная альтернатива
- [NoSQL](../../nosql/README.md)
- [Базы данных](../../README.md)
- [Spring Data](../../../frameworks/java-frameworks/spring/README.md) — есть Spring Data Neo4j

### Внешние ресурсы
- [Neo4j Documentation](https://neo4j.com/docs/)
- [Cypher Manual](https://neo4j.com/docs/cypher-manual/current/)
- [Neo4j Graph Academy](https://graphacademy.neo4j.com/) — бесплатные курсы
- [Graph Algorithms Book (O'Reilly)](https://neo4j.com/graph-algorithms-book/)

## Содержание

- [Что внутри раздела](#что-внутри-раздела)
- [Когда брать Neo4j](#когда-брать-neo4j)
- [Neo4j vs альтернативы](#neo4j-vs-альтернативы)
- [Маршруты чтения](#маршруты-чтения)
- [Куда идти дальше](#куда-идти-дальше)

## Что внутри раздела

[neo4j-basics.md](neo4j-basics.md) покрывает:

- Property graph модель: узлы (nodes), связи (relationships), свойства, метки (labels)
- Установка через Docker и Desktop
- Основы Cypher: `CREATE`, `MATCH`, `MERGE`, `WITH`, `RETURN`, path patterns
- Индексы, ограничения, производительность запросов
- Интеграцию с Java через Neo4j Java Driver и Spring Data Neo4j
- Базовые use-cases: соцсеть, рекомендации

## Когда брать Neo4j

- Данные **по сути граф**: пользователи и друзья, товары и покупки, роли и разрешения, топология сети.
- Запросы требуют **обхода 3+ уровней связей** (friends of friends, shortest path, centrality).
- Нужен явный язык для **паттернов графа** — Cypher декларативен и читаем.
- Размер графа **укладывается в один кластер** (сотни миллионов узлов).

**Когда не брать:**
- Плоская табличная модель — берите PostgreSQL.
- Документы без связей — MongoDB.
- Аналитика на колонках — ClickHouse.
- Огромные графы с distributed sharding — JanusGraph / DGraph.

## Neo4j vs альтернативы

| Критерий | Neo4j | OrientDB | PostgreSQL + recursive CTE | JanusGraph |
|----------|-------|----------|----------------------------|------------|
| Модель | Native property graph | Мультимодель (graph + doc + KV) | Реляционная | Distributed graph |
| Язык | Cypher, GQL | SQL + Gremlin | SQL | Gremlin |
| Масштаб | До миллиардов рёбер на кластере | Средний | Не графовая рабочая нагрузка | Петабайты |
| Порог входа | Низкий | Средний | Высокий для графов | Высокий |
| Когда выбирать | Граф — первоклассный гражданин | Нужны несколько моделей сразу | Редкие графовые запросы | Массивный distributed граф |

## Маршруты чтения

- **Быстрый старт (1-2 часа):** `neo4j-basics.md` → секции установки и Cypher → попробовать запросы на sample-датасете.
- **Backend-интеграция:** Cypher → Java Driver → Spring Data Neo4j → собственный прототип.
- **Дизайн графа:** моделирование узлов/связей → индексы → производительность → [архитектура БД](../../README.md).

## Куда идти дальше

- Сравнение графовых БД — [графовые БД](../README.md)
- Обзор всех СУБД — [databases/README.md](../../README.md)
- Проектирование данных и нормализация — [databases/relational/README.md](../../relational/README.md)
- Интервью по базам данных — [interview/databases/database-architecture-interview.md](../../../interview/databases/database-architecture-interview.md)
