---
title: "OrientDB: Основы"
description: "OrientDB — мультимодельная NoSQL БД: графовая и документная модели, SQL-подобный язык, Java API, Gremlin."
tags:
  - databases
  - graph
  - nosql
  - orientdb
difficulty: "intermediate"
updated: "2026-04-20"
---
# OrientDB: Основы

OrientDB — мультимодельная NoSQL БД, поддерживающая документную, графовую, key-value и объектную модели в одном движке.

## Полезные ссылки

### Официальная документация
- [OrientDB Documentation](https://orientdb.org/docs/3.2.x/) — официальная документация

### См. также
- [Neo4j](../neo4j/neo4j-basics.md) — графовая БД с языком Cypher
- [MongoDB](../../nosql/mongodb/mongodb-basics.md) — документная NoSQL БД
- [Apache Cassandra](../../nosql/cassandra/cassandra-basics.md) — wide-column NoSQL

## Содержание

- [Когда выбирать OrientDB](#когда-выбирать-orientdb)
- [Запуск в Docker](#запуск-в-docker)
- [Модели данных](#модели-данных)
- [SQL-подобный язык](#sql-подобный-язык)
- [Индексы](#индексы)
- [Java API](#java-api)
- [Транзакции](#транзакции)
- [Встроенный режим (для тестов)](#встроенный-режим-для-тестов)
- [Gremlin (TinkerPop 3)](#gremlin-tinkerpop-3)
- [OrientDB vs Neo4j](#orientdb-vs-neo4j)
- [Типичные проблемы](#типичные-проблемы)

## Когда выбирать OrientDB

- Данные одновременно требуют документной гибкости и графовых обходов (социальный граф + профили).
- Нужна ACID-транзакционность для нескольких моделей в одной БД.
- Ищете open-source альтернативу Neo4j с документной моделью.

## Запуск в Docker

```bash
docker run -d --name orientdb \
  -p 2424:2424 \
  -p 2480:2480 \
  -e ORIENTDB_ROOT_PASSWORD=rootpwd \
  orientdb:3.2
# Веб-консоль: http://localhost:2480
# Binary protocol: 2424
```

## Модели данных

| Модель | Базовый класс | Пример |
|--------|---------------|--------|
| Документная | Любой Class | `Person`, `Product` |
| Вершина графа | `V` | `User extends V` |
| Ребро графа | `E` | `Follows extends E` |
| Key-Value | Class с UNIQUE-индексом | поиск по ключу |

## SQL-подобный язык

```sql
-- Создание класса
CREATE CLASS Person EXTENDS V;

-- Создание вершин
INSERT INTO Person SET name = 'Alice', age = 30;
CREATE VERTEX Person SET name = 'Bob', age = 25;

-- Создание рёбер
CREATE EDGE Follows FROM (SELECT FROM Person WHERE name = 'Alice')
                     TO   (SELECT FROM Person WHERE name = 'Bob');

-- Обход графа: out() — исходящие, in() — входящие, both() — все
SELECT name FROM (
  TRAVERSE out('Follows') FROM (SELECT FROM Person WHERE name = 'Alice')
  MAXDEPTH 2
);

-- Друзья друзей
SELECT expand(out('Follows').out('Follows'))
FROM Person WHERE name = 'Alice';

-- Кратчайший путь
SELECT shortestPath($from, $to, 'BOTH')
LET $from = (SELECT FROM Person WHERE name = 'Alice'),
    $to   = (SELECT FROM Person WHERE name = 'Charlie');

-- UPDATE и DELETE
UPDATE Person SET age = 31 WHERE name = 'Alice';
DELETE VERTEX Person WHERE name = 'Alice';   -- удаляет и все рёбра
DELETE EDGE Follows WHERE out.name = 'Alice' AND in.name = 'Bob';
```

## Индексы

```sql
-- UNIQUE для key-value доступа
CREATE INDEX Person.email ON Person(email) UNIQUE;

-- NOTUNIQUE для поиска без уникальности
CREATE INDEX Person.city ON Person(city) NOTUNIQUE;

-- Fulltext через Lucene
CREATE INDEX Person.name ON Person(name) FULLTEXT ENGINE LUCENE;

-- Spatial
CREATE INDEX Place.coords ON Place(lat, lon) SPATIAL ENGINE LUCENE;
```

## Java API

```xml
<dependency>
  <groupId>com.orientechnologies</groupId>
  <artifactId>orientdb-client</artifactId>
  <version>3.2.33</version>
</dependency>
```

```java
// Подключение (remote)
OrientDB orient = new OrientDB("remote:localhost", OrientDBConfig.defaultConfig());
ODatabaseSession db = orient.open("mydb", "admin", "admin");

// Документная модель
ODocument person = new ODocument("Person");
person.field("name", "Alice").field("age", 30);
db.save(person);

// Запрос
try (OResultSet rs = db.query("SELECT FROM Person WHERE name = ?", "Alice")) {
    while (rs.hasNext()) {
        OResult row = rs.next();
        System.out.println(row.getProperty("name"));
    }
}

// Графовая модель
OVertex alice = db.newVertex("Person");
alice.setProperty("name", "Alice");
db.save(alice);

OVertex bob = db.newVertex("Person");
bob.setProperty("name", "Bob");
db.save(bob);

OEdge edge = db.newEdge(alice, bob, "Follows");
db.save(edge);

// Обход
for (OEdge e : alice.getEdges(ODirection.OUT, "Follows")) {
    System.out.println(e.getTo().getProperty("name"));
}

db.close();
orient.close();
```

## Транзакции

```java
db.begin();
try {
    OVertex charlie = db.newVertex("Person");
    charlie.setProperty("name", "Charlie");
    db.save(charlie);

    db.newEdge(alice, charlie, "Follows");
    db.commit();
} catch (Exception e) {
    db.rollback();
    throw e;
}
```

## Встроенный режим (для тестов)

```java
OrientDB orient = new OrientDB("embedded:/tmp/testdb", OrientDBConfig.defaultConfig());
if (!orient.exists("testdb")) {
    orient.create("testdb", ODatabaseType.MEMORY);
}
ODatabaseSession db = orient.open("testdb", "admin", "admin");
```

## Gremlin (TinkerPop 3)

```groovy
g.V().hasLabel('Person').has('name', 'Alice')
     .out('Follows').values('name').toList()

// Путь на 2 уровня
g.V().has('name', 'Alice').repeat(out('Follows')).times(2).path().by('name')
```

## OrientDB vs Neo4j

| Критерий | OrientDB | Neo4j |
|----------|----------|-------|
| Модели | Мульти (граф + документ + K-V) | Только граф |
| Язык запросов | SQL (расширенный) | Cypher |
| ACID | Да | Да |
| Лицензия | Apache 2.0 | GPL / Commercial |
| Зрелость | Меньше | Больше |

## Типичные проблемы

| Симптом | Причина | Решение |
|---------|---------|---------|
| Ошибка подключения | Неверный порт / credentials | 2424 (binary), 2480 (HTTP) |
| Медленный TRAVERSE | Нет индекса, большая глубина | Добавить индекс; ограничить `MAXDEPTH` |
| Ребро не удалено с вершиной | Использован `DELETE` вместо `DELETE VERTEX` | `DELETE VERTEX` удаляет рёбра автоматически |
| Deadlock в транзакции | Конкурентное изменение одних вершин | Retry при `ORecordDuplicatedException` |

