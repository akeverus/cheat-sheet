# OrientDB: Основы

**Комплексное руководство по использованию OrientDB — мультимодельной NoSQL базы данных.**

**Дата последнего обновления:** 2026-01-25

## Полезные ссылки

### Официальная документация
- [OrientDB Documentation](https://orientdb.com/docs/) - Официальная документация

### См. также
- `../neo4j/neo4j-basics.md` - Neo4j графовая БД
- `../../nosql/README.md` - NoSQL базы данных

## Содержание

- [Введение в OrientDB](#введение-в-orientdb)
- [Установка](#установка)
- [Работа с графами](#работа-с-графами)
- [Интеграция с Java](#интеграция-с-java)

## Введение в OrientDB

**OrientDB** — мультимодельная NoSQL БД, поддерживающая документную, графовую, key-value и объектную модели.

### Основные возможности

- Мультимодельность
- Графовая модель
- SQL-подобный язык запросов
- ACID транзакции

## Установка

```bash
# Docker установка
docker run -d --name orientdb \
  -p 2424:2424 -p 2480:2480 \
  orientdb:latest
```

## Работа с графами

```sql
-- Создание вершин
CREATE VERTEX User SET name = 'Alice';

-- Создание ребер
CREATE EDGE Follows FROM (SELECT FROM User WHERE name = 'Alice') 
TO (SELECT FROM User WHERE name = 'Bob');
```

## Интеграция с Java

```java
// OrientDB Java API
ODatabaseSession db = pool.acquire();
ODocument user = new ODocument("User");
user.field("name", "Alice");
user.save();
db.close();
```

---

*Обновлено: 2026-01-25*
