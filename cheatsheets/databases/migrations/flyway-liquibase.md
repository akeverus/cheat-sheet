---
title: "Flyway и Liquibase — миграции баз данных"
description: "Практическое руководство по управлению схемой БД: versioned/repeatable миграции, rollback, Spring Boot интеграция, типичные ошибки"
tags:
  - flyway
  - liquibase
  - migrations
  - database
  - spring-boot
  - sql
difficulty: intermediate
updated: "2026-04-20"
---

# Flyway и Liquibase — миграции баз данных

## Полезные ссылки

### Официальная документация
- [Flyway Documentation](https://documentation.red-gate.com/fd) — официальная документация Flyway
- [Liquibase Documentation](https://docs.liquibase.com/) — официальная документация Liquibase

### См. также
- [[postgres-basics|PostgreSQL: основы]] — PostgreSQL как целевая БД миграций
- [[spring-boot|Spring Boot]] — интеграция с автоконфигурацией
- [[flyway-liquibase-interview|Вопросы на собеседовании]] — подготовка к интервью

## Содержание

- [Flyway](#flyway)
  - [Концепции](#концепции)
  - [Именование файлов](#именование-файлов)
  - [Конфигурация Spring Boot](#конфигурация-spring-boot)
  - [Команды CLI / Gradle](#команды-cli-gradle)
  - [Callbacks](#callbacks)
- [Liquibase](#liquibase)
  - [Концепции](#концепции-1)
  - [Форматы changelog](#форматы-changelog)
  - [Preconditions](#preconditions)
  - [Contexts и Labels](#contexts-и-labels)
  - [Команды Liquibase](#команды-liquibase)
- [Flyway vs Liquibase — сравнение](#flyway-vs-liquibase-сравнение)
- [Spring Boot — интеграция](#spring-boot-интеграция)
  - [Порядок инициализации](#порядок-инициализации)
  - [Тесты с Testcontainers](#тесты-с-testcontainers)
- [Best practices](#best-practices)
- [Типичные ошибки](#типичные-ошибки)
- [См. также](#см-также-1)

## Flyway

### Концепции

- **Versioned migration** — выполняется ровно один раз, упорядочена по версии. Формат: `V{version}__{description}.sql`
- **Repeatable migration** — выполняется при каждом изменении чексуммы. Формат: `R__{description}.sql`. Подходит для views, функций, процедур
- **Undo migration** — откат конкретной версии (только Flyway Teams). Формат: `U{version}__{description}.sql`

### Именование файлов

```text
db/migration/
  V1__create_users_table.sql
  V2__add_email_index.sql
  V2.1__add_phone_column.sql
  R__refresh_user_stats_view.sql
```

Правила:
- `V` — prefix для versioned, `R` — для repeatable
- Двойное подчёркивание `__` отделяет версию от описания
- Версия: числа и точки (`1`, `2.1`, `20240101`)
- Пробелы в описании заменяются на `_`

### Конфигурация Spring Boot

```yaml
spring:
  flyway:
    enabled: true
    locations: classpath:db/migration
    baseline-on-migrate: false   # не делать baseline при первом запуске
    out-of-order: false          # запрещать миграции не по порядку
    validate-on-migrate: true    # проверять чексуммы при старте
    table: flyway_schema_history
    schemas: public
    placeholders:
      schema_name: myapp
```

### Команды CLI / Gradle

```bash
# Применить все pending миграции
./gradlew flywayMigrate

# Проверить статус
./gradlew flywayInfo

# Проверить чексуммы без применения
./gradlew flywayValidate

# Починить таблицу истории (после failed migration)
./gradlew flywayRepair

# Пометить текущее состояние как baseline (для существующей БД)
./gradlew flywayBaseline

# Откатить последнюю миграцию (только Flyway Teams + Undo-скрипт)
./gradlew flywayUndo
```

### Callbacks

Flyway вызывает SQL-callbacks на определённых событиях:

```text
db/migration/
  beforeMigrate.sql      # перед всеми миграциями
  afterMigrate.sql       # после всех миграций
  beforeEachMigrate.sql  # перед каждой миграцией
  afterEachMigrate.sql   # после каждой миграции
  afterMigrateError.sql  # при ошибке
```

Java-callback:

```java
@Component
public class FlywayMigrationCallback implements FlywayCallback {
    @Override
    public void afterEachMigrate(Connection connection, MigrationInfo info) {
        log.info("Applied: {}", info.getScript());
    }
}
```


## Liquibase

### Концепции

- **Changelog** — главный файл, описывает порядок применения изменений
- **Changeset** — атомарная единица изменений с уникальным `id` + `author`
- **Preconditions** — условия, при которых changeset выполняется или пропускается
- **Contexts** — теги для выборочного запуска (`test`, `prod`)
- **Labels** — фильтр при деплое (отличие от contexts: labels — AND-логика, contexts — OR)

### Форматы changelog

**XML (наиболее полный API):**

```xml
<?xml version="1.0" encoding="UTF-8"?>
<databaseChangeLog
    xmlns="http://www.liquibase.org/xml/ns/dbchangelog"
    xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
    xsi:schemaLocation="http://www.liquibase.org/xml/ns/dbchangelog
        http://www.liquibase.org/xml/ns/dbchangelog/dbchangelog-4.20.xsd">

    <changeSet id="1" author="sergey">
        <createTable tableName="users">
            <column name="id" type="BIGINT" autoIncrement="true">
                <constraints primaryKey="true"/>
            </column>
            <column name="email" type="VARCHAR(255)">
                <constraints nullable="false" unique="true"/>
            </column>
            <column name="created_at" type="TIMESTAMP" defaultValueComputed="NOW()"/>
        </createTable>
    </changeSet>

    <changeSet id="2" author="sergey">
        <addColumn tableName="users">
            <column name="phone" type="VARCHAR(20)"/>
        </addColumn>
        <rollback>
            <dropColumn tableName="users" columnName="phone"/>
        </rollback>
    </changeSet>

</databaseChangeLog>
```

**YAML:**

```yaml
databaseChangeLog:
  - changeSet:
      id: "3"
      author: sergey
      changes:
        - createIndex:
            indexName: idx_users_email
            tableName: users
            columns:
              - column:
                  name: email
      rollback:
        - dropIndex:
            indexName: idx_users_email
            tableName: users
```

**SQL (нативный, без абстракций):**

```sql
--liquibase formatted sql
--changeset sergey:4
CREATE INDEX idx_users_created_at ON users(created_at);
--rollback DROP INDEX idx_users_created_at;
```

### Preconditions

```xml
<changeSet id="5" author="sergey">
    <preConditions onFail="MARK_RAN">
        <not>
            <tableExists tableName="audit_log"/>
        </not>
    </preConditions>
    <createTable tableName="audit_log">
        <column name="id" type="BIGINT" autoIncrement="true">
            <constraints primaryKey="true"/>
        </column>
    </createTable>
</changeSet>
```

Значения `onFail`: `HALT` (дефолт) | `CONTINUE` | `MARK_RAN` | `WARN`

### Contexts и Labels

```xml
<!-- Только для тестовых данных -->
<changeSet id="seed-1" author="sergey" context="test">
    <insert tableName="users">
        <column name="email" value="test@example.com"/>
    </insert>
</changeSet>

<!-- Только при деплое с label=v2.5 -->
<changeSet id="6" author="sergey" labels="v2.5">
    <addColumn tableName="users">
        <column name="avatar_url" type="TEXT"/>
    </addColumn>
</changeSet>
```

Запуск с фильтром:

```bash
liquibase --contexts=prod update
liquibase --labels="v2.5" update
```

### Команды Liquibase

```bash
liquibase update              # применить все pending changesets
liquibase rollback --tag=v1   # откатить до тега
liquibase rollbackCount 2     # откатить последние 2 changeset'а
liquibase tag v1.0            # поставить тег на текущее состояние
liquibase status              # показать pending changesets
liquibase validate            # проверить changelog на ошибки
liquibase generateChangeLog   # сгенерировать changelog из существующей БД
```


## Flyway vs Liquibase — сравнение

| Критерий | Flyway | Liquibase |
|---|---|---|
| **Rollback** | Только Flyway Teams (платно) или undo-скрипты | Встроен в open-source |
| **Форматы** | SQL, Java | SQL, XML, YAML, JSON |
| **Простота** | Проще, меньше концепций | Богаче API, но сложнее |
| **Spring Boot** | Автоконфигурация из коробки | Автоконфигурация из коробки |
| **Preconditions** | Нет | Есть |
| **Dry-run** | `flywayValidate` | `updateSQL` (генерирует SQL без применения) |
| **CI/CD** | Простая интеграция через CLI / Maven / Gradle | То же + DATABASECHANGELOGLOCK для concurrency |
| **Tracking table** | `flyway_schema_history` | `databasechangelog` + `databasechangeloglock` |


## Spring Boot — интеграция

### Порядок инициализации

По умолчанию миграции запускаются **до** создания Hibernate-схемы. Для корректной работы:

```yaml
spring:
  jpa:
    hibernate:
      ddl-auto: validate  # Hibernate только проверяет, не создаёт
  flyway:
    enabled: true
```

Если нужно управлять порядком вручную:

```java
@Bean
public FlywayMigrationStrategy migrationStrategy() {
    return flyway -> {
        flyway.repair();
        flyway.migrate();
    };
}
```

### Тесты с Testcontainers

```java
@SpringBootTest
@Testcontainers
class MigrationTest {

    @Container
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:16")
            .withDatabaseName("testdb")
            .withUsername("test")
            .withPassword("test");

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
    }

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Test
    void allMigrationsApplyCleanly() {
        // Flyway автоматически запустил все миграции при старте контекста
        Integer count = jdbcTemplate.queryForObject(
            "SELECT COUNT(*) FROM flyway_schema_history", Integer.class);
        assertThat(count).isGreaterThan(0);
    }
}
```


## Best practices

- **Никогда не изменять применённые миграции** — Flyway/Liquibase проверяют чексуммы и упадут. Создавать новую миграцию поверх
- **Backward compatibility** — новые колонки добавлять `nullable` или с дефолтом, чтобы старая версия приложения работала во время rolling deploy
- **Один changeset = одна логическая операция** — не смешивать DDL и DML в одном файле без причины
- **Именование с датой** для команд: `V20240401_001__add_orders_table.sql` — избегает конфликтов при параллельных ветках
- **Тестировать rollback** — если используете Liquibase rollback, проверяйте его в CI так же, как и migrate
- **Не коммитить `baseline-on-migrate: true` в прод-конфиг** — опасно для существующих БД
- **Lock timeout** — в Liquibase настраивать `changeLogLockWaitTimeInMinutes`, чтобы CI не висел бесконечно при зависшем локе


## Типичные ошибки

| Симптом | Причина | Решение |
|---|---|---|
| `Validate failed: checksum mismatch` | Изменили уже применённую миграцию | Откатить изменение файла или запустить `flywayRepair` |
| `Found more than one migration with version X` | Два файла с одинаковой версией | Переименовать один из файлов |
| `Migration V3 was applied out of order` | Применили V3 раньше V2 из другой ветки | Включить `out-of-order: true` или удалить из истории |
| `Unable to acquire change log lock` | Упавший процесс не снял Liquibase-лок | `UPDATE databasechangeloglock SET LOCKED=FALSE` |
| `Table already exists` | Запустили миграцию на непустой БД без baseline | Запустить `flywayBaseline` или добавить precondition |
| `FlywayException: Found non-empty schema(s)` | `baseline-on-migrate=false` на существующей БД | Установить `baseline-on-migrate: true` один раз |
| Hibernate падает после миграции | `ddl-auto: create-drop` затирает схему | Переключить на `validate` или `none` |


## См. также

- [[spring-data-jpa|Spring Data JPA]]
- [[spring-boot|Spring Boot]]
- [[postgres-basics|PostgreSQL Basics]]
- [[hibernate-interview|Hibernate]]
- [[database-transactions-interview|Database Transactions]]
