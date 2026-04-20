---
title: "jOOQ: Type-Safe SQL в Java"
description: "Комплексное руководство по использованию jOOQ (Java Object Oriented Querying) для создания type-safe SQL запросов в Java приложениях."
tags:
  - libraries
  - java
  - java-jooq
difficulty: "intermediate"
prerequisites:
  - java-lombok
next: []
updated: "2026-04-20"
---
# jOOQ: Type-Safe SQL в Java

**Комплексное руководство по использованию `jOOQ` (Java Object Oriented Querying) для создания `type-safe SQL` запросов в `Java` приложениях.**

## Полезные ссылки

### Официальная документация
- [jOOQ](https://www.jooq.org/) — официальный сайт
- [jOOQ Manual](https://www.jooq.org/doc/latest/manual/) — полная документация
- [jOOQ GitHub](https://github.com/jOOQ/jOOQ) — репозиторий проекта

### Интеграция
- [Spring Boot jOOQ](https://docs.spring.io/spring-boot/docs/current/reference/html/data.html#data.sql.jooq) — **Spring Boot** интеграция
- [jOOQ Maven Plugin](https://www.jooq.org/doc/latest/manual/code-generation/codegen-maven/) — генерация кода
- [jOOQ Examples](https://github.com/jOOQ/jOOQ/tree/main/jOOQ-examples) — примеры

## Содержание

- [Введение в jOOQ](#введение-в-jooq)
  - [Почему jOOQ?](#почему-jooq)
  - [Как работает jOOQ?](#как-работает-jooq)
  - [Преимущества и недостатки](#преимущества-и-недостатки)
- [Установка и настройка](#установка-и-настройка)
  - [Maven](#maven)
  - [Gradle](#gradle)
  - [Настройка DSLContext](#настройка-dslcontext)
- [Генерация кода](#генерация-кода)
  - [Базовая конфигурация генератора](#базовая-конфигурация-генератора)
  - [Кастомная стратегия генерации](#кастомная-стратегия-генерации)
  - [Структура сгенерированного кода](#структура-сгенерированного-кода)
- [Основы DSL](#основы-dsl)
  - [Создание DSLContext](#создание-dslcontext)
  - [Выполнение запросов](#выполнение-запросов)
- [SELECT запросы](#select-запросы)
  - [Простые SELECT запросы](#простые-select-запросы)
  - [SELECT с условиями](#select-с-условиями)
  - [ORDER BY и LIMIT](#order-by-и-limit)
- [INSERT, UPDATE, DELETE](#insert-update-delete)
  - [INSERT операции](#insert-операции)
  - [UPDATE операции](#update-операции)
  - [DELETE операции](#delete-операции)
- [JOIN операции](#join-операции)
  - [INNER JOIN](#inner-join)
  - [LEFT и RIGHT JOIN](#left-и-right-join)
  - [CROSS JOIN и UNION](#cross-join-и-union)
- [Условные запросы](#условные-запросы)
  - [WHERE условия](#where-условия)
  - [EXISTS и NOT EXISTS](#exists-и-not-exists)
- [Агрегатные функции](#агрегатные-функции)
  - [Основные агрегаты](#основные-агрегаты)
  - [Продвинутые агрегаты](#продвинутые-агрегаты)
- [Работа с транзакциями](#работа-с-транзакциями)
  - [Spring транзакции](#spring-транзакции)
  - [Программное управление транзакциями](#программное-управление-транзакциями)
- [Интеграция с Spring](#интеграция-с-spring)
  - [Spring Boot конфигурация](#spring-boot-конфигурация)
  - [Repository паттерн](#repository-паттерн)
  - [Service слой](#service-слой)
- [Продвинутые возможности](#продвинутые-возможности)
  - [Кастомные типы и конвертеры](#кастомные-типы-и-конвертеры)
  - [Batch операции](#batch-операции)
  - [RecordMapper для DTO](#recordmapper-для-dto)
- [Best practices](#best-practices)
  - [1. Правильная структура проекта](#1-правильная-структура-проекта)
  - [2. Использование Repository паттерна](#2-использование-repository-паттерна)
  - [3. Обработка ошибок](#3-обработка-ошибок)
  - [4. Оптимизация производительности](#4-оптимизация-производительности)
  - [5. Тестирование](#5-тестирование)
- [Заключение](#заключение)
  - [Преимущества jOOQ](#преимущества-jooq)
  - [Основные паттерны использования](#основные-паттерны-использования)
  - [Когда использовать jOOQ](#когда-использовать-jooq)
  - [Сравнение с альтернативами](#сравнение-с-альтернативами)
- [См. также](#см-также)

## Введение в jOOQ

**jOOQ (Java Object Oriented Querying)** — это библиотека для создания **type-safe SQL** запросов в **Java**. Она предоставляет **fluent API** для построения **SQL** запросов, обеспечивая **compile-time** проверку типов и предотвращая **SQL injection**.

### Почему jOOQ?

**jOOQ** предлагает множество преимуществ:**

1. **Type safety** — Полная проверка типов на этапе компиляции
2. **Fluent API** — Читаемый и интуитивный синтаксис
3. **Code generation** — Автоматическая генерация классов из схемы БД
4. **SQL injection protection** — Автоматическая защита от **SQL injection**
5. **Database agnostic** — Поддержка множества СУБД
6. **Performance** — Оптимизированные запросы
7. **IDE support** — Полная поддержка автодополнения
8. **Migration friendly** — Легкая адаптация к изменениям схемы

### Как работает jOOQ?

**jOOQ** использует **code generation** для создания **Java** классов, представляющих таблицы, поля и другие элементы базы данных. Эти классы позволяют строить **type-safe SQL** запросы используя **fluent API**.

### Преимущества и недостатки

**Преимущества:**
- **Type-safe SQL**
- Защита от **SQL injection**
- Читаемый код
- Поддержка всех **SQL** возможностей
- Хорошая производительность

**Недостатки:**
- Требует генерации кода
- Сложность настройки
- Кривая обучения
- Зависимость от схемы БД

## Установка и настройка

### Maven

Зависимость **Maven** для **jOOQ** и плагин генерации кода из схемы БД.

```xml
<dependency>
    <groupId>org.jooq</groupId>
    <artifactId>jooq</artifactId>
    <version>3.18.6</version>
</dependency>

<!-- Для генерации кода -->
<plugin>
    <groupId>org.jooq</groupId>
    <artifactId>jooq-codegen-maven</artifactId>
    <version>3.18.6</version>
    <executions>
        <execution>
            <phase>generate-sources</phase>
            <goals>
                <goal>generate</goal>
            </goals>
        </execution>
    </executions>
    <configuration>
        <jdbc>
            <driver>org.h2.Driver</driver>
            <url>jdbc:h2:~/test</url>
            <user>sa</user>
            <password></password>
        </jdbc>
        <generator>
            <database>
                <name>org.jooq.meta.h2.H2Database</name>
                <includes>.*</includes>
                <excludes></excludes>
            </database>
            <target>
                <packageName>com.example.db</packageName>
                <directory>target/generated-sources/jooq</directory>
            </target>
        </generator>
    </configuration>
</plugin>
```

### Gradle

```kotlin
plugins {
    id("org.jooq.jooq-codegen-gradle") version "3.18.6"
}

dependencies {
    implementation("org.jooq:jooq:3.18.6")
    runtimeOnly("com.h2database:h2") // Для генерации кода
}

jooq {
    version = "3.18.6"
    edition = nu.studer.gradle.jooq.JooqEdition.OSS

    sample(sourceSets.main) {
        jdbc {
            driver = "org.h2.Driver"
            url = "jdbc:h2:~/test"
            user = "sa"
            password = ""
        }
        generator {
            database {
                name = "org.jooq.meta.h2.H2Database"
                includes = ".*"
            }
            target {
                packageName = "com.example.db"
                directory = "build/generated/jooq"
            }
        }
    }
}
```

### Настройка DSLContext

```java
@Configuration
public class JooqConfig {

    @Bean
    public DSLContext dslContext(DataSource dataSource) {
        return DSL.using(dataSource, SQLDialect.POSTGRES);
    }

    // Для транзакций
    @Bean
    public TransactionProvider transactionProvider(DataSource dataSource) {
        return new SpringTransactionProvider(dataSource);
    }

    // Настройка с транзакциями
    @Bean
    public DSLContext dslContextWithTransactions(
            DataSource dataSource,
            TransactionProvider transactionProvider) {

        return DSL.using(dataSource, SQLDialect.POSTGRES,
            new Settings().withTransactionProvider(transactionProvider));
    }
}
```

## Генерация кода

### Базовая конфигурация генератора

```xml
<!-- jooq-codegen-config.xml -->
<?xml version="1.0" encoding="UTF-8"?>
<configuration>
    <jdbc>
        <driver>org.postgresql.Driver</driver>
        <url>jdbc:postgresql://localhost:5432/myapp</url>
        <user>myuser</user>
        <password>mypassword</password>
    </jdbc>

    <generator>
        <database>
            <name>org.jooq.meta.postgres.PostgresDatabase</name>
            <includes>.*</includes>
            <excludes>
                flyway_schema_history
            </excludes>
            <inputSchema>public</inputSchema>
        </database>

        <target>
            <packageName>com.example.db</packageName>
            <directory>target/generated-sources/jooq</directory>
        </target>

        <strategy>
            <name>org.jooq.codegen.DefaultGeneratorStrategy</name>
        </strategy>
    </generator>
</configuration>
```

### Кастомная стратегия генерации

```java
public class CustomGeneratorStrategy extends DefaultGeneratorStrategy {

    @Override
    public String getJavaClassName(Definition definition, Mode mode) {
        if (mode == Mode.RECORD) {
            return super.getJavaClassName(definition, mode) + "Record";
        }
        return super.getJavaClassName(definition, mode);
    }

    @Override
    public String getJavaPackageName(Definition definition, Mode mode) {
        String packageName = super.getJavaPackageName(definition, mode);

        if (mode == Mode.DAO) {
            return packageName + ".dao";
        }

        return packageName;
    }
}
```

### Структура сгенерированного кода

```java
// Сгенерированные классы
public class Tables {

    // Таблицы
    public static final User USER = User.USER;
    public static final Post POST = Post.POST;
    public static final Comment COMMENT = Comment.COMMENT;
}

public class User extends TableImpl<UserRecord> {

    public static final User USER = new User();

    // Поля таблицы
    public final TableField<UserRecord, Long> ID = createField("id", SQLDataType.BIGINT);
    public final TableField<UserRecord, String> NAME = createField("name", SQLDataType.VARCHAR);
    public final TableField<UserRecord, String> EMAIL = createField("email", SQLDataType.VARCHAR);

    // Конструкторы...
}

public class UserRecord extends UpdatableRecordImpl<UserRecord> {

    // Геттеры и сеттеры для всех полей
    public Long getId() { ... }
    public void setId(Long id) { ... }
    public String getName() { ... }
    public void setName(String name) { ... }
    // ...
}
```

## Основы DSL

### Создание DSLContext

```java
/
 * Различные способы создания DSLContext в jOOQ
 * DSLContext - это основной интерфейс для выполнения SQL запросов через jOOQ DSL
 */

// Способ 1: Создание из DataSource (рекомендуемый способ)
// DataSource управляет пулом соединений автоматически
DSLContext dsl = DSL.using(dataSource, SQLDialect.POSTGRES);
// SQLDialect.POSTGRES указывает что мы используем PostgreSQL и его специфичные функции

// Способ 2: Создание из Connection (для ручного управления соединением)
// Используется когда нужно явно контролировать жизненный цикл соединения
try (Connection conn = dataSource.getConnection()) {  // Получаем соединение из DataSource
    DSLContext dsl = DSL.using(conn, SQLDialect.POSTGRES);  // Создаем DSLContext из Connection
    // Использование dsl для выполнения запросов...
    // Соединение автоматически закроется при выходе из try-with-resources
}

// Способ 3: Создание с кастомными настройками
// Settings позволяет настроить поведение jOOQ: форматирование SQL, логирование и т.д.
Settings settings = new Settings()
    .withRenderFormatted(true)   // Форматированный SQL вывод (с отступами) для читаемости
    .withExecuteLogging(true);   // Логирование всех выполняемых SQL запросов

// Создаем DSLContext с настройками
DSLContext dsl = DSL.using(dataSource, SQLDialect.POSTGRES, settings);
// Теперь все SQL запросы будут форматироваться и логироваться
```

### Выполнение запросов

```java
/
 * Репозиторий для работы с пользователями через jOOQ
 * Демонстрирует базовые операции CRUD с использованием jOOQ DSL
 */
@Repository  // Spring аннотация для компонента доступа к данным
public class UserRepository {

    // DSLContext - основной интерфейс jOOQ для выполнения SQL запросов
    private final DSLContext dsl;

    /
     * Конструктор с внедрением DSLContext через dependency injection
     * @param dsl DSLContext для выполнения SQL запросов
     */
    public UserRepository(DSLContext dsl) {
        this.dsl = dsl;
    }

    /
     * Получение всех пользователей из базы данных
     * SELECT * FROM user
     * @return список всех записей пользователей
     */
    public List<UserRecord> findAll() {
        // selectFrom(USER) эквивалентно SELECT * FROM user
        // fetch() выполняет запрос и возвращает список результатов
        return dsl.selectFrom(USER).fetch();
    }

    /
     * Поиск пользователя по идентификатору
     * SELECT * FROM user WHERE id = ?
     * @param id идентификатор пользователя
     * @return Optional с записью пользователя, если найден
     */
    public Optional<UserRecord> findById(Long id) {
        // selectFrom(USER) - выбираем все поля из таблицы USER
        // .where(USER.ID.eq(id)) - добавляем условие WHERE id = ?
        // USER.ID - это type-safe ссылка на поле id таблицы USER
        // .eq(id) - проверка равенства (equals)
        // fetchOptional() - выполняет запрос и возвращает Optional (пустой если не найдено)
        return dsl.selectFrom(USER)
            .where(USER.ID.eq(id))
            .fetchOptional();
    }
}
```

## SELECT запросы

### Простые SELECT запросы

```java
public class SelectExamples {

    private final DSLContext dsl;

    // SELECT * FROM user
    public List<UserRecord> findAllUsers() {
        return dsl.selectFrom(USER).fetch();
    }

    // SELECT id, name FROM user WHERE active = true
    public List<UserRecord> findActiveUsers() {
        return dsl.select(USER.ID, USER.NAME)
            .from(USER)
            .where(USER.ACTIVE.eq(true))
            .fetch();
    }

    // SELECT с алиасами
    public List<Record2<Long, String>> findUserNames() {
        return dsl.select(USER.ID.as("userId"), USER.NAME.as("userName"))
            .from(USER)
            .fetch();
    }

    // SELECT DISTINCT
    public List<String> findUniqueCities() {
        return dsl.selectDistinct(ADDRESS.CITY)
            .from(ADDRESS)
            .fetch(ADDRESS.CITY);
    }

    // COUNT запросы
    public int countUsers() {
        return dsl.fetchCount(USER);
    }

    public int countActiveUsers() {
        return dsl.fetchCount(
            dsl.selectFrom(USER).where(USER.ACTIVE.eq(true))
        );
    }
}
```

### SELECT с условиями

```java
public class ConditionalSelectExamples {

    // WHERE условия
    public List<UserRecord> findUsersByName(String name) {
        return dsl.selectFrom(USER)
            .where(USER.NAME.eq(name))
            .fetch();
    }

    // LIKE условия
    public List<UserRecord> findUsersByNamePattern(String pattern) {
        return dsl.selectFrom(USER)
            .where(USER.NAME.like("%" + pattern + "%"))
            .fetch();
    }

    // IN условия
    public List<UserRecord> findUsersByIds(List<Long> ids) {
        return dsl.selectFrom(USER)
            .where(USER.ID.in(ids))
            .fetch();
    }

    // BETWEEN условия
    public List<UserRecord> findUsersByAgeRange(int minAge, int maxAge) {
        return dsl.selectFrom(USER)
            .where(USER.AGE.between(minAge, maxAge))
            .fetch();
    }

    // NULL проверки
    public List<UserRecord> findUsersWithEmail() {
        return dsl.selectFrom(USER)
            .where(USER.EMAIL.isNotNull())
            .fetch();
    }

    // Сложные условия
    public List<UserRecord> findComplexQuery(String name, Integer minAge) {
        return dsl.selectFrom(USER)
            .where(USER.NAME.eq(name)
                .and(USER.AGE.ge(minAge))
                .and(USER.ACTIVE.eq(true)))
            .fetch();
    }
}
```

### ORDER `BY` и LIMIT

```java
public class OrderingExamples {

    // ORDER BY
    public List<UserRecord> findUsersOrderedByName() {
        return dsl.selectFrom(USER)
            .orderBy(USER.NAME.asc())
            .fetch();
    }

    // ORDER BY с несколькими полями
    public List<UserRecord> findUsersOrderedByAgeAndName() {
        return dsl.selectFrom(USER)
            .orderBy(USER.AGE.desc(), USER.NAME.asc())
            .fetch();
    }

    // LIMIT и OFFSET
    public List<UserRecord> findUsersPaginated(int page, int size) {
        int offset = page * size;
        return dsl.selectFrom(USER)
            .orderBy(USER.ID)
            .limit(size)
            .offset(offset)
            .fetch();
    }

    // TOP N
    public List<UserRecord> findTop5ActiveUsers() {
        return dsl.selectFrom(USER)
            .where(USER.ACTIVE.eq(true))
            .orderBy(USER.CREATED_AT.desc())
            .limit(5)
            .fetch();
    }
}
```

## INSERT, UPDATE, DELETE

### INSERT операции

```java
public class InsertExamples {

    // INSERT одной записи
    public void insertUser(String name, String email) {
        dsl.insertInto(USER)
            .set(USER.NAME, name)
            .set(USER.EMAIL, email)
            .set(USER.ACTIVE, true)
            .set(USER.CREATED_AT, LocalDateTime.now())
            .execute();
    }

    // INSERT с возвратом ID
    public Long insertUserAndReturnId(String name, String email) {
        return dsl.insertInto(USER)
            .set(USER.NAME, name)
            .set(USER.EMAIL, email)
            .set(USER.ACTIVE, true)
            .set(USER.CREATED_AT, LocalDateTime.now())
            .returning(USER.ID)
            .fetchOne()
            .getId();
    }

    // Batch INSERT
    public void insertUsersBatch(List<UserData> users) {
        dsl.batch(
            users.stream()
                .map(user -> dsl.insertInto(USER)
                    .set(USER.NAME, user.getName())
                    .set(USER.EMAIL, user.getEmail())
                    .set(USER.ACTIVE, true)
                    .set(USER.CREATED_AT, LocalDateTime.now()))
                .collect(Collectors.toList())
        ).execute();
    }

    // INSERT ... SELECT
    public void copyActiveUsersToArchive() {
        dsl.insertInto(ARCHIVE_USER)
            .select(
                dsl.select(USER.ID, USER.NAME, USER.EMAIL, USER.CREATED_AT)
                    .from(USER)
                    .where(USER.ACTIVE.eq(true))
            )
            .execute();
    }

    // ON DUPLICATE KEY UPDATE (для MySQL)
    public void upsertUser(Long id, String name, String email) {
        dsl.insertInto(USER)
            .set(USER.ID, id)
            .set(USER.NAME, name)
            .set(USER.EMAIL, email)
            .onDuplicateKeyUpdate()
            .set(USER.NAME, name)
            .set(USER.EMAIL, email)
            .execute();
    }
}
```

### UPDATE операции

```java
public class UpdateExamples {

    // UPDATE одной записи
    public int updateUserEmail(Long userId, String newEmail) {
        return dsl.update(USER)
            .set(USER.EMAIL, newEmail)
            .set(USER.UPDATED_AT, LocalDateTime.now())
            .where(USER.ID.eq(userId))
            .execute();
    }

    // UPDATE нескольких полей
    public int updateUser(Long userId, UpdateUserRequest request) {
        return dsl.update(USER)
            .set(USER.NAME, request.getName())
            .set(USER.EMAIL, request.getEmail())
            .set(USER.AGE, request.getAge())
            .set(USER.UPDATED_AT, LocalDateTime.now())
            .where(USER.ID.eq(userId))
            .execute();
    }

    // UPDATE с условиями
    public int activateUsersInCity(String city) {
        return dsl.update(USER)
            .set(USER.ACTIVE, true)
            .set(USER.UPDATED_AT, LocalDateTime.now())
            .where(USER.ID.in(
                dsl.select(USER.ID)
                    .from(USER)
                    .join(ADDRESS).on(USER.ADDRESS_ID.eq(ADDRESS.ID))
                    .where(ADDRESS.CITY.eq(city))
            ))
            .execute();
    }

    // Batch UPDATE
    public void updateUsersBatch(List<UserUpdate> updates) {
        List<Query> queries = updates.stream()
            .map(update -> dsl.update(USER)
                .set(USER.EMAIL, update.getNewEmail())
                .where(USER.ID.eq(update.getUserId())))
            .collect(Collectors.toList());

        dsl.batch(queries).execute();
    }
}
```

### DELETE операции

```java
public class DeleteExamples {

    // DELETE по ID
    public int deleteUser(Long userId) {
        return dsl.deleteFrom(USER)
            .where(USER.ID.eq(userId))
            .execute();
    }

    // DELETE с условиями
    public int deleteInactiveUsers() {
        return dsl.deleteFrom(USER)
            .where(USER.ACTIVE.eq(false))
            .execute();
    }

    // DELETE с подзапросом
    public int deleteUsersWithoutPosts() {
        return dsl.deleteFrom(USER)
            .where(USER.ID.notIn(
                dsl.selectDistinct(POST.USER_ID)
                    .from(POST)
            ))
            .execute();
    }

    // Batch DELETE
    public void deleteUsersBatch(List<Long> userIds) {
        dsl.deleteFrom(USER)
            .where(USER.ID.in(userIds))
            .execute();
    }
}
```

## JOIN операции

### INNER JOIN

```java
public class JoinExamples {

    // INNER JOIN
    public List<Record3<String, String, String>> findUsersWithAddresses() {
        return dsl.select(USER.NAME, USER.EMAIL, ADDRESS.CITY)
            .from(USER)
            .join(ADDRESS).on(USER.ADDRESS_ID.eq(ADDRESS.ID))
            .fetch();
    }

    // JOIN с несколькими таблицами
    public List<Record> findPostsWithUserAndComments() {
        return dsl.select()
            .from(POST)
            .join(USER).on(POST.USER_ID.eq(USER.ID))
            .leftJoin(COMMENT).on(COMMENT.POST_ID.eq(POST.ID))
            .fetch();
    }

    // JOIN с алиасами
    public List<Record2<String, Integer>> findUserPostCounts() {
        User u = USER.as("u");
        Post p = POST.as("p");

        return dsl.select(u.NAME, dsl.count(p.ID))
            .from(u)
            .leftJoin(p).on(p.USER_ID.eq(u.ID))
            .groupBy(u.ID, u.NAME)
            .fetch();
    }
}
```

### LEFT и RIGHT JOIN

```java
public class OuterJoinExamples {

    // LEFT JOIN
    public List<Record3<String, String, String>> findAllUsersWithAddresses() {
        return dsl.select(USER.NAME, USER.EMAIL, ADDRESS.CITY)
            .from(USER)
            .leftJoin(ADDRESS).on(USER.ADDRESS_ID.eq(ADDRESS.ID))
            .fetch();
    }

    // RIGHT JOIN
    public List<Record3<String, String, String>> findAllAddressesWithUsers() {
        return dsl.select(USER.NAME, USER.EMAIL, ADDRESS.CITY)
            .from(USER)
            .rightJoin(ADDRESS).on(USER.ADDRESS_ID.eq(ADDRESS.ID))
            .fetch();
    }

    // FULL OUTER JOIN (если поддерживается СУБД)
    public List<Record3<String, String, String>> findAllUsersAndAddresses() {
        return dsl.select(USER.NAME, USER.EMAIL, ADDRESS.CITY)
            .from(USER)
            .fullOuterJoin(ADDRESS).on(USER.ADDRESS_ID.eq(ADDRESS.ID))
            .fetch();
    }

    // Множественные JOIN
    public List<Record> findComplexData() {
        return dsl.select(
                USER.NAME,
                POST.TITLE,
                COMMENT.CONTENT,
                ADDRESS.CITY
            )
            .from(USER)
            .join(POST).on(POST.USER_ID.eq(USER.ID))
            .leftJoin(COMMENT).on(COMMENT.POST_ID.eq(POST.ID))
            .leftJoin(ADDRESS).on(USER.ADDRESS_ID.eq(ADDRESS.ID))
            .where(POST.PUBLISHED.eq(true))
            .fetch();
    }
}
```

### CROSS JOIN и UNION

```java
public class AdvancedJoinExamples {

    // CROSS JOIN (декартово произведение)
    public List<Record2<String, String>> findAllUserAddressCombinations() {
        return dsl.select(USER.NAME, ADDRESS.CITY)
            .from(USER)
            .crossJoin(ADDRESS)
            .fetch();
    }

    // UNION
    public List<Record1<String>> findAllNames() {
        return dsl.select(USER.NAME)
            .from(USER)
            .union(
                dsl.select(AUTHOR.NAME).from(AUTHOR)
            )
            .fetch();
    }

    // UNION ALL
    public List<Record1<String>> findAllNamesWithDuplicates() {
        return dsl.select(USER.NAME)
            .from(USER)
            .unionAll(
                dsl.select(AUTHOR.NAME).from(AUTHOR)
            )
            .fetch();
    }

    // INTERSECT
    public List<Record1<String>> findCommonNames() {
        return dsl.select(USER.NAME)
            .from(USER)
            .intersect(
                dsl.select(AUTHOR.NAME).from(AUTHOR)
            )
            .fetch();
    }

    // EXCEPT/MINUS
    public List<Record1<String>> findUniqueUserNames() {
        return dsl.select(USER.NAME)
            .from(USER)
            .except(
                dsl.select(AUTHOR.NAME).from(AUTHOR)
            )
            .fetch();
    }
}
```

## Условные запросы

### WHERE условия

```java
public class WhereConditionExamples {

    // Простые условия
    public List<UserRecord> findActiveUsers() {
        return dsl.selectFrom(USER)
            .where(USER.ACTIVE.eq(true))
            .fetch();
    }

    // Составные условия
    public List<UserRecord> findUsersByCriteria(UserSearchCriteria criteria) {
        Condition condition = DSL.trueCondition(); // Всегда true

        if (criteria.getName() != null) {
            condition = condition.and(USER.NAME.like("%" + criteria.getName() + "%"));
        }

        if (criteria.getEmail() != null) {
            condition = condition.and(USER.EMAIL.eq(criteria.getEmail()));
        }

        if (criteria.getMinAge() != null) {
            condition = condition.and(USER.AGE.ge(criteria.getMinAge()));
        }

        if (criteria.getMaxAge() != null) {
            condition = condition.and(USER.AGE.le(criteria.getMaxAge()));
        }

        return dsl.selectFrom(USER)
            .where(condition)
            .fetch();
    }

    // Использование Condition API
    public List<UserRecord> findUsersByDynamicCriteria(UserSearchCriteria criteria) {
        List<Condition> conditions = new ArrayList<>();

        Optional.ofNullable(criteria.getName())
            .ifPresent(name -> conditions.add(USER.NAME.like("%" + name + "%")));

        Optional.ofNullable(criteria.getEmail())
            .ifPresent(email -> conditions.add(USER.EMAIL.eq(email)));

        Optional.ofNullable(criteria.getMinAge())
            .ifPresent(minAge -> conditions.add(USER.AGE.ge(minAge)));

        Optional.ofNullable(criteria.getMaxAge())
            .ifPresent(maxAge -> conditions.add(USER.AGE.le(maxAge)));

        return dsl.selectFrom(USER)
            .where(conditions)
            .fetch();
    }
}
```

### EXISTS и NOT EXISTS

```java
public class ExistsExamples {

    // EXISTS
    public List<UserRecord> findUsersWithPosts() {
        return dsl.selectFrom(USER)
            .whereExists(
                dsl.selectOne()
                    .from(POST)
                    .where(POST.USER_ID.eq(USER.ID))
            )
            .fetch();
    }

    // NOT EXISTS
    public List<UserRecord> findUsersWithoutPosts() {
        return dsl.selectFrom(USER)
            .whereNotExists(
                dsl.selectOne()
                    .from(POST)
                    .where(POST.USER_ID.eq(USER.ID))
            )
            .fetch();
    }

    // EXISTS с условиями
    public List<UserRecord> findUsersWithPublishedPosts() {
        return dsl.selectFrom(USER)
            .whereExists(
                dsl.selectOne()
                    .from(POST)
                    .where(POST.USER_ID.eq(USER.ID)
                        .and(POST.PUBLISHED.eq(true)))
            )
            .fetch();
    }
}
```

## Агрегатные функции

### Основные агрегаты

```java
public class AggregateExamples {

    // COUNT
    public int countAllUsers() {
        return dsl.fetchCount(USER);
    }

    public int countActiveUsers() {
        return dsl.fetchCount(
            dsl.selectFrom(USER).where(USER.ACTIVE.eq(true))
        );
    }

    // SUM
    public BigDecimal sumAllOrderAmounts() {
        return dsl.select(sum(ORDER.AMOUNT))
            .from(ORDER)
            .fetchOne()
            .value1();
    }

    // AVG, MIN, MAX
    public Record4<BigDecimal, BigDecimal, BigDecimal, BigDecimal> getOrderStats() {
        return dsl.select(
                avg(ORDER.AMOUNT),
                min(ORDER.AMOUNT),
                max(ORDER.AMOUNT),
                sum(ORDER.AMOUNT)
            )
            .from(ORDER)
            .where(ORDER.STATUS.eq("COMPLETED"))
            .fetchOne();
    }

    // GROUP BY
    public List<Record2<String, Integer>> getUserPostCounts() {
        return dsl.select(USER.NAME, count(POST.ID))
            .from(USER)
            .leftJoin(POST).on(POST.USER_ID.eq(USER.ID))
            .groupBy(USER.ID, USER.NAME)
            .orderBy(count(POST.ID).desc())
            .fetch();
    }

    // HAVING
    public List<Record2<String, Integer>> getProlificUsers() {
        return dsl.select(USER.NAME, count(POST.ID))
            .from(USER)
            .join(POST).on(POST.USER_ID.eq(USER.ID))
            .groupBy(USER.ID, USER.NAME)
            .having(count(POST.ID).ge(5))
            .orderBy(count(POST.ID).desc())
            .fetch();
    }
}
```

### Продвинутые агрегаты

```java
public class AdvancedAggregateExamples {

    // DISTINCT COUNT
    public int countUniqueUsersWithPosts() {
        return dsl.select(countDistinct(POST.USER_ID))
            .from(POST)
            .fetchOne()
            .value1();
    }

    // STRING_AGG (PostgreSQL) или GROUP_CONCAT (MySQL)
    public List<Record2<String, String>> getUserTags() {
        return dsl.select(
                USER.NAME,
                groupConcat(TAG.NAME).separator(", ")
            )
            .from(USER)
            .join(USER_TAG).on(USER_TAG.USER_ID.eq(USER.ID))
            .join(TAG).on(TAG.ID.eq(USER_TAG.TAG_ID))
            .groupBy(USER.ID, USER.NAME)
            .fetch();
    }

    // Window functions
    public List<Record3<String, BigDecimal, BigDecimal>> getOrderRunningTotals() {
        return dsl.select(
                ORDER.ID,
                ORDER.AMOUNT,
                sum(ORDER.AMOUNT).over().partitionBy(ORDER.USER_ID)
                    .orderBy(ORDER.CREATED_AT)
            )
            .from(ORDER)
            .orderBy(ORDER.USER_ID, ORDER.CREATED_AT)
            .fetch();
    }

    // ROLLUP для итогов
    public List<Record3<String, String, Integer>> getDepartmentStats() {
        return dsl.select(
                DEPARTMENT.NAME,
                EMPLOYEE.POSITION,
                count(EMPLOYEE.ID)
            )
            .from(DEPARTMENT)
            .join(EMPLOYEE).on(EMPLOYEE.DEPARTMENT_ID.eq(DEPARTMENT.ID))
            .groupBy(rollup(DEPARTMENT.NAME, EMPLOYEE.POSITION))
            .orderBy(DEPARTMENT.NAME, EMPLOYEE.POSITION)
            .fetch();
    }
}
```

## Работа с транзакциями

### Spring транзакции

```java
@Service
@Transactional
public class UserService {

    private final DSLContext dsl;

    public void createUserWithAddress(CreateUserRequest request) {
        // Создание пользователя
        UserRecord user = dsl.insertInto(USER)
            .set(USER.NAME, request.getName())
            .set(USER.EMAIL, request.getEmail())
            .returning(USER.ID)
            .fetchOne();

        // Создание адреса
        dsl.insertInto(ADDRESS)
            .set(ADDRESS.STREET, request.getStreet())
            .set(ADDRESS.CITY, request.getCity())
            .set(ADDRESS.USER_ID, user.getId())
            .execute();

        // Если что-то пойдет не так, вся транзакция откатится
    }

    @Transactional(readOnly = true)
    public UserWithAddressDto getUserWithAddress(Long userId) {
        return dsl.select(
                USER.ID,
                USER.NAME,
                USER.EMAIL,
                ADDRESS.STREET,
                ADDRESS.CITY
            )
            .from(USER)
            .leftJoin(ADDRESS).on(ADDRESS.USER_ID.eq(USER.ID))
            .where(USER.ID.eq(userId))
            .fetchOneInto(UserWithAddressDto.class);
    }
}
```

### Программное управление транзакциями

```java
@Service
public class TransactionalService {

    private final DSLContext dsl;

    public void executeInTransaction() {
        dsl.transaction(configuration -> {
            // Все операции в этом блоке выполняются в одной транзакции
            DSLContext txDsl = DSL.using(configuration);

            UserRecord user = txDsl.insertInto(USER)
                .set(USER.NAME, "John Doe")
                .set(USER.EMAIL, "john@example.com")
                .returning(USER.ID)
                .fetchOne();

            txDsl.insertInto(AUDIT_LOG)
                .set(AUDIT_LOG.ACTION, "USER_CREATED")
                .set(AUDIT_LOG.USER_ID, user.getId())
                .set(AUDIT_LOG.TIMESTAMP, LocalDateTime.now())
                .execute();

            // Если здесь возникнет исключение, транзакция откатится
        });
    }

    public void executeWithResult() {
        UserRecord result = dsl.transactionResult(configuration -> {
            DSLContext txDsl = DSL.using(configuration);

            // Создание и возврат результата
            return txDsl.insertInto(USER)
                .set(USER.NAME, "Jane Doe")
                .set(USER.EMAIL, "jane@example.com")
                .returning()
                .fetchOne();
        });

        System.out.println("Created user: " + result.getName());
    }
}
```

## Интеграция с Spring

### Spring Boot конфигурация

```java
@Configuration
@EnableTransactionManagement
public class JooqConfiguration {

    @Bean
    public DSLContext dslContext(DataSource dataSource) {
        Settings settings = new Settings()
            .withRenderFormatted(true)
            .withExecuteLogging(true)
            .withRenderSchema(false); // Для PostgreSQL схемы

        return DSL.using(dataSource, SQLDialect.POSTGRES, settings);
    }

    @Bean
    public TransactionProvider transactionProvider(DataSource dataSource) {
        return new SpringTransactionProvider(dataSource);
    }

    // Опционально: DAO классы
    @Bean
    public UserDao userDao(DSLContext dsl) {
        return new UserDao(dsl.configuration());
    }
}
```

### Repository паттерн

```java
@Repository
public class UserRepository {

    private final DSLContext dsl;

    @Autowired
    public UserRepository(DSLContext dsl) {
        this.dsl = dsl;
    }

    public List<UserRecord> findAll() {
        return dsl.selectFrom(USER).fetch();
    }

    public Optional<UserRecord> findById(Long id) {
        return dsl.selectFrom(USER)
            .where(USER.ID.eq(id))
            .fetchOptional();
    }

    public List<UserRecord> findByName(String name) {
        return dsl.selectFrom(USER)
            .where(USER.NAME.like("%" + name + "%"))
            .fetch();
    }

    public UserRecord save(UserRecord user) {
        if (user.getId() == null) {
            return dsl.insertInto(USER)
                .set(user)
                .returning()
                .fetchOne();
        } else {
            dsl.update(USER)
                .set(user)
                .where(USER.ID.eq(user.getId()))
                .execute();
            return user;
        }
    }

    public void deleteById(Long id) {
        dsl.deleteFrom(USER)
            .where(USER.ID.eq(id))
            .execute();
    }

    public boolean existsByEmail(String email) {
        return dsl.fetchExists(
            dsl.selectFrom(USER).where(USER.EMAIL.eq(email))
        );
    }
}
```

### Service слой

```java
@Service
@Transactional
public class UserService {

    private final UserRepository userRepository;
    private final AddressRepository addressRepository;

    @Autowired
    public UserService(UserRepository userRepository, AddressRepository addressRepository) {
        this.userRepository = userRepository;
        this.addressRepository = addressRepository;
    }

    public UserDto createUser(CreateUserRequest request) {
        // Проверка существования email
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new EmailAlreadyExistsException(request.getEmail());
        }

        // Создание пользователя
        UserRecord user = new UserRecord();
        user.setName(request.getName());
        user.setEmail(request.getEmail());
        user.setActive(true);
        user.setCreatedAt(LocalDateTime.now());

        UserRecord savedUser = userRepository.save(user);

        // Создание адреса если указан
        if (request.getAddress() != null) {
            AddressRecord address = new AddressRecord();
            address.setStreet(request.getAddress().getStreet());
            address.setCity(request.getAddress().getCity());
            address.setUserId(savedUser.getId());

            addressRepository.save(address);
        }

        return toDto(savedUser);
    }

    @Transactional(readOnly = true)
    public Page<UserDto> findUsers(UserSearchCriteria criteria, Pageable pageable) {
        // Построение условия
        Condition condition = buildSearchCondition(criteria);

        // Получение общего количества
        int total = dsl.fetchCount(
            dsl.selectFrom(USER).where(condition)
        );

        // Получение данных с пагинацией
        List<UserRecord> users = dsl.selectFrom(USER)
            .where(condition)
            .orderBy(getOrderByFields(pageable.getSort()))
            .limit(pageable.getPageSize())
            .offset(pageable.getOffset())
            .fetch();

        List<UserDto> userDtos = users.stream()
            .map(this::toDto)
            .collect(Collectors.toList());

        return new PageImpl<>(userDtos, pageable, total);
    }

    private Condition buildSearchCondition(UserSearchCriteria criteria) {
        Condition condition = DSL.trueCondition();

        if (criteria.getName() != null) {
            condition = condition.and(USER.NAME.like("%" + criteria.getName() + "%"));
        }

        if (criteria.getEmail() != null) {
            condition = condition.and(USER.EMAIL.eq(criteria.getEmail()));
        }

        if (criteria.getActive() != null) {
            condition = condition.and(USER.ACTIVE.eq(criteria.getActive()));
        }

        return condition;
    }

    private Collection<? extends OrderField<?>> getOrderByFields(Sort sort) {
        List<OrderField<?>> fields = new ArrayList<>();

        for (Sort.Order order : sort) {
            Field<?> field;
            switch (order.getProperty()) {
                case "name":
                    field = USER.NAME;
                    break;
                case "email":
                    field = USER.EMAIL;
                    break;
                case "createdAt":
                    field = USER.CREATED_AT;
                    break;
                default:
                    continue;
            }

            fields.add(order.isAscending() ? field.asc() : field.desc());
        }

        return fields.isEmpty() ? List.of(USER.ID.asc()) : fields;
    }

    private UserDto toDto(UserRecord user) {
        UserDto dto = new UserDto();
        dto.setId(user.getId());
        dto.setName(user.getName());
        dto.setEmail(user.getEmail());
        dto.setActive(user.getActive());
        dto.setCreatedAt(user.getCreatedAt());
        return dto;
    }
}
```

## Продвинутые возможности

### Кастомные типы и конвертеры

```java
// Кастомный тип для JSON
public class JsonbConverter implements Converter<String, Map<String, Object>> {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public Map<String, Object> from(String databaseObject) {
        if (databaseObject == null) {
            return null;
        }
        try {
            return objectMapper.readValue(databaseObject,
                new TypeReference<Map<String, Object>>() {});
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Failed to parse JSON", e);
        }
    }

    @Override
    public String to(Map<String, Object> userObject) {
        if (userObject == null) {
            return null;
        }
        try {
            return objectMapper.writeValueAsString(userObject);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Failed to serialize JSON", e);
        }
    }

    @Override
    public Class<String> fromType() {
        return String.class;
    }

    @Override
    public Class<Map<String, Object>> toType() {
        return (Class<Map<String, Object>>) (Class) Map.class;
    }
}

// Регистрация конвертера
@Configuration
public class JooqConvertersConfig {

    @Bean
    public ConverterProvider converterProvider() {
        return new DefaultConverterProvider(
            new JsonbConverter()
        );
    }
}
```

### Batch операции

```java
@Service
public class BatchService {

    private final DSLContext dsl;

    public void batchInsertUsers(List<UserData> users) {
        // Подготовка batch запросов
        List<InsertSetStep<UserRecord>> inserts = users.stream()
            .map(user -> dsl.insertInto(USER)
                .set(USER.NAME, user.getName())
                .set(USER.EMAIL, user.getEmail())
                .set(USER.ACTIVE, true)
                .set(USER.CREATED_AT, LocalDateTime.now()))
            .collect(Collectors.toList());

        // Выполнение batch
        Batch batch = dsl.batch(inserts);
        int[] results = batch.execute();

        System.out.println("Inserted " + Arrays.stream(results).sum() + " users");
    }

    public void batchUpdateUsers(List<UserUpdate> updates) {
        // Использование BatchBindStep для параметризованных запросов
        BatchBindStep batch = dsl.batch(
            dsl.update(USER)
                .set(USER.EMAIL, param("email", String.class))
                .set(USER.UPDATED_AT, param("updatedAt", LocalDateTime.class))
                .where(USER.ID.eq(param("id", Long.class)))
        );

        // Привязка параметров
        for (UserUpdate update : updates) {
            batch.bind(update.getEmail(), LocalDateTime.now(), update.getUserId());
        }

        // Выполнение
        int[] results = batch.execute();
        System.out.println("Updated " + Arrays.stream(results).sum() + " users");
    }
}
```

### RecordMapper для DTO

```java
@Configuration
public class JooqConfig {

    @Bean
    public RecordMapperProvider recordMapperProvider() {
        return new DefaultRecordMapperProvider(
            // Кастомный маппер для сложных случаев
            new RecordMapper<Record, UserDto>() {
                @Override
                public UserDto map(Record record) {
                    UserDto dto = new UserDto();
                    dto.setId(record.get(USER.ID));
                    dto.setName(record.get(USER.NAME));
                    dto.setEmail(record.get(USER.EMAIL));
                    dto.setActive(record.get(USER.ACTIVE));

                    // Кастомная логика
                    dto.setDisplayName(record.get(USER.NAME) + " (" + record.get(USER.EMAIL) + ")");

                    return dto;
                }
            }
        );
    }
}

// Использование
public List<UserDto> findUsers() {
    return dsl.select(USER.ID, USER.NAME, USER.EMAIL, USER.ACTIVE)
        .from(USER)
        .fetchInto(UserDto.class); // Автоматическое маппинг
}
```

## Best practices

### 1. Правильная структура проекта

```java
// ✅ Хорошо - разделение ответственности
├── config/
│   └── JooqConfig.java
├── domain/
│   ├── User.java
│   ├── UserDto.java
│   └── UserSearchCriteria.java
├── repository/
│   └── UserRepository.java
├── service/
│   └── UserService.java
└── generated/
    └── jooq/
        ├── Tables.java
        └── records/
```

### 2. Использование Repository паттерна

```java
// ✅ Хорошо - чистый repository
@Repository
public class UserRepository {

    private final DSLContext dsl;

    public List<UserRecord> findByCriteria(UserSearchCriteria criteria) {
        SelectConditionStep<UserRecord> query = dsl.selectFrom(USER);

        // Построение условий
        if (criteria.getName() != null) {
            query = query.and(USER.NAME.like("%" + criteria.getName() + "%"));
        }

        if (criteria.getActive() != null) {
            query = query.and(USER.ACTIVE.eq(criteria.getActive()));
        }

        return query.fetch();
    }

    public Page<UserRecord> findPaginated(Pageable pageable) {
        SelectSeekStepN<UserRecord> query = dsl.selectFrom(USER)
            .orderBy(buildOrderBy(pageable.getSort()));

        // Пагинация
        List<UserRecord> content = query
            .limit(pageable.getPageSize())
            .offset(pageable.getOffset())
            .fetch();

        long total = dsl.fetchCount(dsl.selectFrom(USER));

        return new PageImpl<>(content, pageable, total);
    }

    private List<OrderField<?>> buildOrderBy(Sort sort) {
        return sort.stream()
            .map(order -> {
                Field<?> field = getField(order.getProperty());
                return order.isAscending() ? field.asc() : field.desc();
            })
            .collect(Collectors.toList());
    }

    private Field<?> getField(String property) {
        switch (property) {
            case "name": return USER.NAME;
            case "email": return USER.EMAIL;
            case "createdAt": return USER.CREATED_AT;
            default: return USER.ID;
        }
    }
}
```

### 3. Обработка ошибок

```java
// ✅ Хорошо - правильная обработка ошибок
@Service
public class SafeUserService {

    private final UserRepository userRepository;

    public UserDto getUserById(Long id) {
        try {
            UserRecord user = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException(id));

            return convertToDto(user);
        } catch (DataAccessException e) {
            log.error("Database error while fetching user {}: {}", id, e.getMessage());
            throw new DatabaseException("Failed to fetch user", e);
        }
    }

    public UserDto createUser(CreateUserRequest request) {
        // Валидация
        validateUserRequest(request);

        try {
            // Проверка уникальности email
            if (userRepository.existsByEmail(request.getEmail())) {
                throw new DuplicateEmailException(request.getEmail());
            }

            UserRecord user = new UserRecord();
            user.setName(request.getName());
            user.setEmail(request.getEmail());
            user.setActive(true);
            user.setCreatedAt(LocalDateTime.now());

            UserRecord saved = userRepository.save(user);
            return convertToDto(saved);

        } catch (DataIntegrityViolationException e) {
            log.error("Data integrity violation: {}", e.getMessage());
            throw new InvalidDataException("Invalid user data", e);
        } catch (DataAccessException e) {
            log.error("Database error: {}", e.getMessage());
            throw new DatabaseException("Failed to create user", e);
        }
    }

    private void validateUserRequest(CreateUserRequest request) {
        if (request.getName() == null || request.getName().trim().isEmpty()) {
            throw new ValidationException("Name is required");
        }

        if (request.getEmail() == null || !request.getEmail().contains("@")) {
            throw new ValidationException("Valid email is required");
        }
    }

    private UserDto convertToDto(UserRecord user) {
        UserDto dto = new UserDto();
        dto.setId(user.getId());
        dto.setName(user.getName());
        dto.setEmail(user.getEmail());
        dto.setActive(user.getActive());
        dto.setCreatedAt(user.getCreatedAt());
        return dto;
    }
}
```

### 4. Оптимизация производительности

```java
// ✅ Хорошо - оптимизация запросов
@Repository
public class OptimizedUserRepository {

    private final DSLContext dsl;

    // Предкомпилированные запросы для часто используемых операций
    private final SelectQuery<UserRecord> activeUsersQuery;

    public OptimizedUserRepository(DSLContext dsl) {
        this.dsl = dsl;

        // Предкомпилированный запрос
        this.activeUsersQuery = dsl.selectFrom(USER)
            .where(USER.ACTIVE.eq(true))
            .getQuery();
    }

    // Использование предкомпилированного запроса
    public List<UserRecord> findActiveUsers() {
        return activeUsersQuery.fetch();
    }

    // Fetch с ограничением полей
    public List<Record2<Long, String>> findUserNames() {
        return dsl.select(USER.ID, USER.NAME)
            .from(USER)
            .where(USER.ACTIVE.eq(true))
            .fetch();
    }

    // Batch операции для множественных обновлений
    public void updateUserStatuses(List<Long> userIds, boolean active) {
        dsl.update(USER)
            .set(USER.ACTIVE, active)
            .set(USER.UPDATED_AT, LocalDateTime.now())
            .where(USER.ID.in(userIds))
            .execute();
    }

    // Использование EXISTS вместо JOIN для проверки наличия
    public List<UserRecord> findUsersWithPosts() {
        return dsl.selectFrom(USER)
            .whereExists(
                dsl.selectOne()
                    .from(POST)
                    .where(POST.USER_ID.eq(USER.ID))
                    .limit(1)
            )
            .fetch();
    }

    // Пагинация с курсорами для больших наборов данных
    public List<UserRecord> findUsersCursorPaginated(Long cursor, int limit) {
        SelectConditionStep<UserRecord> query = dsl.selectFrom(USER);

        if (cursor != null) {
            query = query.where(USER.ID.gt(cursor));
        }

        return query
            .orderBy(USER.ID.asc())
            .limit(limit)
            .fetch();
    }
}
```

### 5. Тестирование

```java
// ✅ Хорошо - тестирование jOOQ кода
@SpringBootTest
@Testcontainers
public class UserRepositoryTest {

    @Container
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:14");

    @Autowired
    private DSLContext dsl;

    @Autowired
    private UserRepository userRepository;

    @BeforeEach
    void setUp() {
        // Очистка данных перед каждым тестом
        dsl.deleteFrom(USER).execute();
    }

    @Test
    void shouldSaveAndFindUser() {
        // Given
        UserRecord user = new UserRecord();
        user.setName("John Doe");
        user.setEmail("john@example.com");
        user.setActive(true);

        // When
        UserRecord saved = userRepository.save(user);

        // Then
        assertThat(saved.getId()).isNotNull();
        assertThat(saved.getName()).isEqualTo("John Doe");
        assertThat(saved.getEmail()).isEqualTo("john@example.com");
    }

    @Test
    void shouldFindUsersByCriteria() {
        // Given
        createTestUser("John Doe", "john@example.com", true);
        createTestUser("Jane Smith", "jane@example.com", false);
        createTestUser("Bob Johnson", "bob@example.com", true);

        UserSearchCriteria criteria = new UserSearchCriteria();
        criteria.setActive(true);

        // When
        List<UserRecord> users = userRepository.findByCriteria(criteria);

        // Then
        assertThat(users).hasSize(2);
        assertThat(users).allMatch(UserRecord::getActive);
    }

    @Test
    void shouldHandleNotFoundUser() {
        // When & Then
        assertThatThrownBy(() -> userRepository.findById(999L))
            .isInstanceOf(EmptyResultDataAccessException.class);
    }

    @Test
    void shouldUpdateUser() {
        // Given
        UserRecord user = createTestUser("John", "john@example.com", true);

        // When
        user.setName("John Updated");
        UserRecord updated = userRepository.save(user);

        // Then
        assertThat(updated.getName()).isEqualTo("John Updated");
        assertThat(updated.getEmail()).isEqualTo("john@example.com");
    }

    private UserRecord createTestUser(String name, String email, boolean active) {
        UserRecord user = new UserRecord();
        user.setName(name);
        user.setEmail(email);
        user.setActive(active);
        user.setCreatedAt(LocalDateTime.now());
        return userRepository.save(user);
    }
}
```


## Заключение

**jOOQ** — это библиотека для создания **type-safe SQL** запросов в **Java**, которая значительно повышает безопасность и удобство работы с базами данных.

### Преимущества jOOQ

1. **Type safety** — Полная проверка типов на этапе компиляции
2. **Fluent API** — Читаемый и интуитивный синтаксис
3. **Code generation** — Автоматическая генерация классов из схемы БД
4. **SQL injection protection** — Автоматическая защита от **SQL injection**
5. **Database agnostic** — Поддержка множества СУБД
6. **Performance** — Оптимизированные запросы
7. **IDE support** — Полная поддержка автодополнения
8. **Migration friendly** — Легкая адаптация к изменениям схемы

### Основные паттерны использования

1. **Repository паттерн** — Для инкапсуляции доступа к данным
2. **DSL паттерн** — Для построения запросов
3. **Record паттерн** — Для работы с результатами запросов
4. **Transaction паттерн** — Для управления транзакциями
5. **Batch паттерн** — Для массовых операций
6. **Criteria паттерн** — Для динамических запросов

### Когда использовать jOOQ

**Рекомендуется:**
- **Enterprise** приложения с **complex SQL**
- Проекты с высокими требованиями к производительности
- Команды с хорошим знанием **SQL**
- Приложения с **legacy** базами данных
- Системы с **dynamic** запросами

**Особенно полезно:**
- При работе с **complex JOIN**
- Для отчетов и аналитики
- В системах с **stored procedures**
- При миграции с **JDBC**
- Для оптимизации производительности

### Сравнение с альтернативами

| Подход | Преимущества | Недостатки |
|--------|-------------|------------|
| **jOOQ** | **Type-safe**, **fluent**, **flexible** | Сложность настройки |
| **Spring `Data` JPA** | Простота, **conventions** | Ограниченная гибкость **SQL** |
| **MyBatis** | Полный контроль **SQL** | Меньше **type safety** |
| **QueryDSL** | **Type-safe**, **dynamic** | Только для **JPA**/**Hibernate** |
| **Plain JDBC** | Максимальная производительность | Много **boilerplate** |

**jOOQ** рекомендуется как основной инструмент для приложений, где требуется полный контроль над **SQL** запросами с сохранением **type safety** и удобства разработки.


[⬆ Наверх](../)

## См. также

- [[java-apache-httpclient|Apache HttpClient: Мощный HTTP клиент для Java]]
- [[java-apache-poi|Apache POI]]
- [[java-bean-validation|Bean Validation (JSR-380 / Jakarta Validation 3.0)]]
- [[java-hikaricp|HikariCP: Высокопроизводительный Connection Pool]]
- [[java-http-clients|HTTP-клиенты в Java]]
