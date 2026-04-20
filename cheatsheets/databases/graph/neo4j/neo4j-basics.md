---
title: "Neo4j: Основы графовой базы данных"
description: "Комплексное руководство по использованию Neo4j — графовой базы данных для работы со связанными данными."
tags:
  - databases
  - graph
  - neo4j-basics
difficulty: "intermediate"
prerequisites: []
next: []
updated: "2026-04-20"
---
# Neo4j: Основы графовой базы данных

**Комплексное руководство по использованию `Neo4j` — графовой базы данных для работы со связанными данными.**

## Полезные ссылки

### Официальная документация
- [Neo4j Documentation](https://neo4j.com/docs/) — официальная документация **Neo4j**
- [Neo4j Cypher Manual](https://neo4j.com/docs/cypher-manual/current/) — руководство по **Cypher**
- [Neo4j GitHub](https://github.com/neo4j/neo4j) — репозиторий проекта

### См. также
- [NoSQL](../../nosql/) — обзор **NoSQL**
- [[orientdb-basics|OrientDB]] — графовая БД **OrientDB**

## Содержание

- [Введение в Neo4j](#введение-в-neo4j)
  - [Почему Neo4j?](#почему-neo4j)
  - [Графовая модель данных](#графовая-модель-данных)
- [Установка и настройка](#установка-и-настройка)
  - [Docker установка](#docker-установка)
- [Основы Cypher](#основы-cypher)
  - [Создание узлов и связей](#создание-узлов-и-связей)
  - [Запросы данных](#запросы-данных)
- [Интеграция с Java](#интеграция-с-java)
  - [Neo4j Java Driver](#neo4j-java-driver)
  - [Spring Data Neo4j](#spring-data-neo4j)
- [Лучшие практики](#лучшие-практики)
- [Решение проблем](#решение-проблем)
- [Частые вопросы](#частые-вопросы)

## Введение в Neo4j

**Neo4j** — это графовая база данных, оптимизированная для работы со связанными данными. Использует модель графа с узлами, связями и свойствами.

### Почему Neo4j?

**Neo4j** предлагает преимущества для связанных данных:

1. **Графовая модель** — Естественное представление связей
2. **Cypher язык** — Интуитивный язык запросов для графов
3. **Высокая производительность** — Оптимизирована для обхода графов
4. **ACID транзакции** — Гарантия целостности данных
5. **Масштабируемость** — Поддержка кластеров

### Графовая модель данных

Пример на **Cypher**: создание узлов **User** и связей **FOLLOWS**, **FRIENDS_WITH** между ними.

```cypher
// Создание узлов (nodes)
CREATE (u1:User {id: 1, name: "Alice", email: "alice@example.com"})
CREATE (u2:User {id: 2, name: "Bob", email: "bob@example.com"})

// Создание связей (relationships)
CREATE (u1)-[:FOLLOWS]->(u2)
CREATE (u1)-[:FRIENDS_WITH]->(u2)
```

## Установка и настройка

### Docker установка

```bash
# Запуск Neo4j в Docker
docker run -d \
  --name neo4j \
  -p 7474:7474 \
  -p 7687:7687 \
  -e NEO4J_AUTH=neo4j/password \
  neo4j:latest
```

## Основы Cypher

### Создание узлов и связей

```cypher
/*
 * Создание узлов и связей в Neo4j
 * Cypher - декларативный язык запросов для графов
 */

// Создание узла пользователя
CREATE (u:User {
    id: 1,
    username: "john_doe",
    email: "john@example.com",
    created_at: datetime()
})

// Создание нескольких узлов
CREATE
    (u1:User {id: 1, username: "alice"}),
    (u2:User {id: 2, username: "bob"}),
    (p1:Post {id: 1, title: "First Post", content: "Hello World"})

// Создание связей
CREATE (u1)-[:CREATED]->(p1)
CREATE (u2)-[:LIKED]->(p1)
CREATE (u1)-[:FOLLOWS]->(u2)
```

### Запросы данных

```cypher
/*
 * Запросы данных в Neo4j через Cypher
 */

// Поиск всех пользователей
MATCH (u:User)
RETURN u

// Поиск пользователя по свойству
MATCH (u:User {username: "alice"})
RETURN u

// Поиск связей
MATCH (u1:User)-[:FOLLOWS]->(u2:User)
RETURN u1, u2

// Поиск пути между узлами
MATCH path = (u1:User)-[:FOLLOWS*1..3]->(u2:User)
WHERE u1.username = "alice"
RETURN path
```

## Интеграция с Java

### Neo4j Java Driver

```java
/*
 * Подключение к Neo4j через Java Driver
 */
import org.neo4j.driver.*;

public class Neo4jConnection {

    /*
     * Создание подключения к Neo4j
     */
    public static Driver createDriver() {
        // Создание драйвера для подключения к Neo4j
        // URI: bolt:// для протокола Bolt, http:// для HTTP
        String uri = "bolt://localhost:7687";  // Bolt протокол (быстрый бинарный протокол)
        String username = "neo4j";              // Имя пользователя по умолчанию
        String password = "password";           // Пароль

        // Driver управляет пулом соединений автоматически
        return GraphDatabase.driver(uri, AuthTokens.basic(username, password));
    }

    /*
     * Создание узла пользователя
     */
    public static void createUser(Driver driver, String username, String email) {
        // Сессия для выполнения операций
        try (Session session = driver.session()) {
            // Выполнение Cypher запроса для создания узла
            // $username и $email - параметры запроса (защита от injection)
            String query = """
                CREATE (u:User {
                    username: $username,
                    email: $email,
                    created_at: datetime()
                })
                RETURN u
                """;

            // Выполнение запроса с параметрами
            Record record = session.writeTransaction(tx -> {
                Result result = tx.run(query,
                    Values.parameters("username", username, "email", email));
                return result.single();  // Возвращаем первую запись
            });

            // Получение созданного узла
            Node userNode = record.get("u").asNode();
            System.out.println("Создан пользователь: " + userNode.get("username"));
        }
    }

    /*
     * Поиск пользователя и его связей
     */
    public static void findUserWithRelations(Driver driver, String username) {
        try (Session session = driver.session()) {
            // Запрос для поиска пользователя и его связей
            String query = """
                MATCH (u:User {username: $username})-[r]->(related)
                RETURN u, type(r) as relationshipType, related
                """;

            // Выполнение запроса
            List<Record> records = session.readTransaction(tx -> {
                Result result = tx.run(query, Values.parameters("username", username));
                return result.list();  // Возвращаем все записи
            });

            // Обработка результатов
            for (Record record : records) {
                Node user = record.get("u").asNode();
                String relType = record.get("relationshipType").asString();
                Node related = record.get("related").asNode();

                System.out.printf("%s -[%s]-> %s%n",
                    user.get("username"), relType, related.get("username"));
            }
        }
    }
}
```

### Spring Data Neo4j

```java
/*
 * Интеграция Neo4j с Spring Boot
 */
@Configuration
public class Neo4jConfiguration {

    @Bean
    public Driver neo4jDriver() {
        return GraphDatabase.driver(
            "bolt://localhost:7687",
            AuthTokens.basic("neo4j", "password")
        );
    }

    @Bean
    public Neo4jTransactionManager transactionManager(Driver driver) {
        return new Neo4jTransactionManager(driver);
    }
}

/
 * Entity класс для узла User
 */
@Node("User")
public class UserNode {
    @Id
    @GeneratedValue
    private Long id;

    private String username;
    private String email;

    @Relationship(type = "FOLLOWS")
    private List<UserNode> following;

    // Геттеры и сеттеры
}

/
 * Репозиторий для работы с узлами
 */
@Repository
public interface UserRepository extends Neo4jRepository<UserNode, Long> {
    UserNode findByUsername(String username);

    @Query("MATCH (u:User)-[:FOLLOWS]->(f:User) WHERE u.username = $username RETURN f")
    List<UserNode> findFollowing(String username);
}
```

## Лучшие практики

1. **Использование индексов** для часто запрашиваемых свойств
2. **Параметризация запросов** для предотвращения **injection**
3. **Ограничение глубины обхода** графа в запросах
4. **Использование транзакций** для атомарности операций

## Решение проблем

| Симптом | Возможная причина | Решение |
|--------|-------------------|---------|
| Медленные запросы по графу | Нет индексов, большая глубина обхода | Индексы на часто используемые свойства; ограничить глубину в MATCH; профилировать запросы (EXPLAIN/PROFILE) |
| OutOfMemory при загрузке | Загрузка всего графа в память | Стриминг результатов, батчи, ограничение выборки; увеличение heap при необходимости |
| Драйвер не подключается | Неверный URI, firewall, SSL | Проверить bolt://host:7687, доступность порта; при SSL — корректные настройки и сертификаты |

## Частые вопросы

**Когда выбирать Neo4j, а не реляционную БД?** Когда связи между сущностями многократные и запросы в основном «обход графа» (друзья друзей, рекомендации, пути). Для простых CRUD и табличных отчётов реляционная БД часто проще.

**Нужны ли индексы в Neo4j?** Да, для свойств, по которым часто ищут или фильтруют (WHERE, MERGE). Без индекса возможен full scan узлов.

**Как ограничить глубину обхода в Cypher?** Использовать `*1..5` в паттерне связей: например, `(a)-[:FOLLOWS*1..5]->(b)`. Иначе запрос может обойти весь граф.


