---
title: "Тестирование базы данных (Database Testing)"
description: "Проверка работы приложения с БД: схема, миграции, репозитории, транзакции, изоляция данных. Основные инструменты: Testcontainers, H2, Flyway, Liquibase, @DataJpaTest, @SpringBootTest."
tags:
  - testing
  - integration-testing
  - database-testing
type: "reference"
difficulty: "intermediate"
aliases:
  - "Тестирование базы данных"
  - "Database Testing"
prerequisites: []
next: []
updated: "2026-04-20"
---
# Тестирование базы данных (Database Testing)

Проверка работы приложения с БД: схема, миграции, репозитории, транзакции, изоляция данных. Основные инструменты: `Testcontainers`, `H2`, `Flyway`, `Liquibase`, `@DataJpaTest`, `@SpringBootTest`.

## Полезные ссылки

- [Testcontainers](https://www.testcontainers.org/)
- [Flyway](https://flywaydb.org/documentation/)
- [Liquibase](https://docs.liquibase.com/)
- [Spring Data JPA](https://docs.spring.io/spring-data/jpa/docs/current/reference/html/)

### См. также

- [Testcontainers](../testcontainers/testcontainers.md)
- [Contract Testing](../contract-testing/contract-testing.md)
- [JUnit](../../unit-testing/junit/junit.md)
- [Testing Tools Overview](../../testing-tools/testing-tools-overview.md)


- [REST Assured для Java](../rest-assured.md)
## Содержание

- [Зачем тестировать БД](#зачем-тестировать-бд)
- [Подходы](#подходы)
- [Подключение](#подключение)
  - [Testcontainers + PostgreSQL (Maven)](#testcontainers-postgresql-maven)
  - [H2 (Maven)](#h2-maven)
  - [Тестовый профиль (application-test.yml)](#тестовый-профиль-application-testyml)
- [Базовое использование](#базовое-использование)
  - [@DataJpaTest с Testcontainers](#datajpatest-с-testcontainers)
  - [@SpringBootTest с Testcontainers](#springboottest-с-testcontainers)
  - [Инициализация данных](#инициализация-данных)
- [Изоляция данных и транзакции](#изоляция-данных-и-транзакции)
- [Миграции в тестах](#миграции-в-тестах)
- [Дополнительные приёмы](#дополнительные-приёмы)
  - [Testcontainers с init-скриптом](#testcontainers-с-init-скриптом)
  - [Нативные запросы через EntityManager](#нативные-запросы-через-entitymanager)
  - [MySQL через Testcontainers](#mysql-через-testcontainers)
- [CI/CD](#cicd)
  - [GitHub Actions](#github-actions)
  - [GitLab CI](#gitlab-ci)
- [Лучшие практики](#лучшие-практики)
- [Решение проблем](#решение-проблем)
- [Частые вопросы](#частые-вопросы)
- [Итоговые таблицы](#итоговые-таблицы)
  - [Основные аннотации](#основные-аннотации)
  - [Когда что использовать](#когда-что-использовать)

## Зачем тестировать БД

- **Проверка схемы и миграций** — миграции применяются без ошибок, схема соответствует ожиданиям приложения.
- **Проверка репозиториев и запросов** — `Spring Data JPA`, кастомные JPQL/нативные запросы работают против реальной СУБД.
- **Повторяемость** — тесты изолированы (транзакция откатывается или БД пересоздаётся); результат не зависит от порядка запуска.
- **Совместимость с CI** — `Testcontainers` или `H2` позволяют запускать тесты на любом агенте без ручной установки БД.


## Подходы

| Подход | Плюсы | Минусы |
|--------|-------|--------|
| `Testcontainers` + PostgreSQL | Реальная СУБД, полная совместимость | Медленнее, нужен Docker |
| `H2` in-memory | Быстро, не нужен Docker | Возможны отличия в SQL-диалекте |
| `@DataJpaTest` | Быстрый срез — только JPA | Не поднимает сервисы и контроллеры |
| `@SpringBootTest` | Полный контекст приложения | Медленнее, больше зависимостей |

Типичная стратегия: `@DataJpaTest` + `Testcontainers` для тестов репозиториев (полная совместимость, быстрый срез). `@SpringBootTest` + `Testcontainers` — для интеграционных сценариев с сервисами. `H2` — когда Docker недоступен или нужна максимальная скорость.


## Подключение

### Testcontainers + PostgreSQL (Maven)

```xml
<dependency>
    <groupId>org.testcontainers</groupId>
    <artifactId>postgresql</artifactId>
    <version>1.19.3</version>
    <scope>test</scope>
</dependency>
<dependency>
    <groupId>org.testcontainers</groupId>
    <artifactId>junit-jupiter</artifactId>
    <version>1.19.3</version>
    <scope>test</scope>
</dependency>
```

### H2 (Maven)

```xml
<dependency>
    <groupId>com.h2database</groupId>
    <artifactId>h2</artifactId>
    <scope>test</scope>
</dependency>
```

### Тестовый профиль (application-test.yml)

```yaml
spring:
  datasource:
    url: jdbc:h2:mem:testdb;DB_CLOSE_DELAY=-1;MODE=PostgreSQL;DATABASE_TO_LOWER=TRUE
    driver-class-name: org.h2.Driver
    username: sa
    password:
  jpa:
    database-platform: org.hibernate.dialect.H2Dialect
    hibernate:
      ddl-auto: validate
  flyway:
    enabled: true
  sql:
    init:
      mode: never
```

При `Testcontainers` значения `url`, `username`, `password` переопределяются через `@DynamicPropertySource`.


## Базовое использование

### @DataJpaTest с Testcontainers

```java
@DataJpaTest
@Testcontainers
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class UserRepositoryTest {

    @Container
    static final PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:15-alpine")
            .withDatabaseName("test")
            .withUsername("test")
            .withPassword("test");

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
    }

    @Autowired
    UserRepository userRepository;

    @Test
    void saveAndFindByEmail() {
        User user = new User("alice@example.com");
        userRepository.save(user);
        assertThat(userRepository.findByEmail("alice@example.com")).isPresent();
    }
}
```

`@AutoConfigureTestDatabase(replace = Replace.NONE)` — не заменять `DataSource` на H2; использовать конфигурацию из `@DynamicPropertySource`.

### @SpringBootTest с Testcontainers

```java
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Testcontainers
class ApplicationIntegrationTest {

    @Container
    static final PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:15-alpine")
            .withDatabaseName("app").withUsername("app").withPassword("app");

    @DynamicPropertySource
    static void props(DynamicPropertyRegistry r) {
        r.add("spring.datasource.url", postgres::getJdbcUrl);
        r.add("spring.datasource.username", postgres::getUsername);
        r.add("spring.datasource.password", postgres::getPassword);
    }

    @Autowired
    UserRepository userRepository;

    @Test
    void fullContextWithDb() {
        assertThat(userRepository.count()).isGreaterThanOrEqualTo(0);
    }
}
```

### Инициализация данных

- **Flyway / Liquibase** — при старте контекста миграции применяются автоматически.
- `@Sql` — выполнить скрипт перед тестом: `@Sql(scripts = {"/clean.sql", "/data.sql"})`.
- `withInitScript` — в `PostgreSQLContainer` при первом запуске контейнера.

```java
@Test
@Sql(scripts = {"/clean-users.sql", "/insert-users.sql"})
void testWithData() {
    assertThat(userRepository.count()).isEqualTo(2);
}
```


## Изоляция данных и транзакции

- `@DataJpaTest` по умолчанию откатывает транзакцию после каждого теста (`@Transactional` с rollback). Данные не накапливаются.
- `@SpringBootTest` без `@Transactional` — изменения в БД сохраняются. Варианты изоляции:
  - Добавить `@Transactional` (откат)
  - Очищать таблицы в `@BeforeEach`
  - Использовать `@Sql` с cleanup-скриптом

```java
    @BeforeEach
    void clean() {
        jdbcTemplate.execute("TRUNCATE TABLE users CASCADE");
}
```

- `@DirtiesContext` — перезапуск контекста после теста. Медленно — используйте только как крайнюю меру.


## Миграции в тестах

В тестовом профиле должны быть включены Flyway/Liquibase с тем же путём к миграциям, что и в основном приложении. Это гарантирует, что миграции работают и схема соответствует коду.

| Аспект | Flyway | Liquibase |
|--------|--------|-----------|
| Формат миграций | SQL по версиям (`V1__create.sql`) | XML, YAML, SQL (changelog) |
| Таблица истории | `flyway_schema_history` | `DATABASECHANGELOG` |
| Spring-свойство | `spring.flyway.enabled` | `spring.liquibase.change-log` |

При `H2` с `MODE=PostgreSQL` многие миграции для PostgreSQL работают, но возможны исключения (специфичные функции). `Testcontainers` — полная совместимость.


## Дополнительные приёмы

### Testcontainers с init-скриптом

```java
@Container
static final PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:15-alpine")
        .withDatabaseName("test").withUsername("test").withPassword("test")
        .withInitScript("init.sql"); // src/test/resources/init.sql
```

### Нативные запросы через EntityManager

```java
@Autowired EntityManager em;

@Test
void nativeQuery() {
    userRepository.save(new User("alice@example.com"));
    em.flush();
    List<?> list = em.createNativeQuery("SELECT email FROM users WHERE email = ?1")
            .setParameter(1, "alice@example.com").getResultList();
    assertThat(list).hasSize(1);
}
```

### MySQL через Testcontainers

Всё аналогично PostgreSQL — другой модуль и образ:

```java
    @Container
static final MySQLContainer<?> mysql = new MySQLContainer<>("mysql:8.0")
        .withDatabaseName("test").withUsername("test").withPassword("test");
```


## CI/CD

### GitHub Actions

```yaml
name: Tests
on: [push, pull_request]
jobs:
  test:
    runs-on: ubuntu-latest
    steps:
      - uses: actions/checkout@v4
      - uses: actions/setup-java@v4
        with:
          java-version: '17'
          distribution: 'temurin'
          cache: maven
      - run: mvn -B test
```

Docker на `ubuntu-latest` доступен по умолчанию — `Testcontainers` работает без дополнительной настройки.

### GitLab CI

```yaml
test:
  image: maven:3.9-eclipse-temurin-17
  variables:
    DOCKER_HOST: tcp://docker:2375
  services:
    - docker:dind
  script:
    - mvn -B test
```


## Лучшие практики

1. **Один контейнер на класс** — `@Container` + `static`; не поднимать на каждый тест.
2. **Миграции как в продакшене** — те же Flyway/Liquibase-миграции; не дублировать схему вручную.
3. **Минимальные данные** — создавать только нужные для сценария записи; не полагаться на глобальный `data.sql`.
4. **Изоляция через транзакцию** — `@DataJpaTest` с откатом; при `@SpringBootTest` — явная очистка или `@Transactional`.
5. **Фиксированные версии образов** — `postgres:15-alpine`, не `latest`.
6. **`@DataJpaTest` где достаточно** — быстрее `@SpringBootTest` для тестов репозиториев.


## Решение проблем

| Проблема | Причина | Решение |
|----------|---------|---------|
| Миграция падает в тесте | SQL несовместим с H2 | Использовать `Testcontainers` с реальной СУБД |
| Тесты падают при параллельном запуске | Общая БД или порт | Один контейнер на класс (`static`), порты назначает Docker |
| `@DataJpaTest` подключается к H2 | `Replace.NONE` не указан | Добавить `@AutoConfigureTestDatabase(replace = Replace.NONE)` |
| Данные из предыдущего теста видны | Нет отката транзакции | `@DataJpaTest` откатывает по умолчанию; при `@SpringBootTest` — очищать или `@Transactional` |
| Контейнер не стартует в CI | Нет Docker на агенте | Убедиться, что Docker доступен |
| `@Sql` не выполняется | Неверный путь к скрипту | Скрипты в `src/test/resources`; путь начинается с `/` |
| Медленные тесты | Много контейнеров | Один контейнер на класс; `@DataJpaTest` вместо `@SpringBootTest` |
| `TRUNCATE` падает | Внешние ключи | `TRUNCATE ... CASCADE` (PostgreSQL) или сначала дочерние таблицы |


## Частые вопросы

**H2 или Testcontainers?**
`H2` — быстрее, проще, без Docker. `Testcontainers` — полная совместимость с боевой СУБД. Для критичных к диалекту сценариев и проверки миграций — `Testcontainers`.

**Нужны ли миграции в тестах?**
Да. Те же Flyway/Liquibase-миграции, что в продакшене — так проверяется, что миграции работают и схема соответствует коду.

**Как очищать данные между тестами?**
`@DataJpaTest` — транзакция откатывается. `@SpringBootTest` — `@Sql` с cleanup-скриптом, `@BeforeEach` с `repository.deleteAll()`, или `TRUNCATE`.

**Можно ли тестировать несколько БД?**
Да — два контейнера с разными настройками. Но обычно достаточно одной.


## Итоговые таблицы

### Основные аннотации

| Аннотация | Назначение |
|-----------|------------|
| `@DataJpaTest` | Срез Spring для JPA (репозитории, сущности) |
| `@SpringBootTest` | Полный контекст приложения |
| `@Testcontainers` | Включение поддержки `Testcontainers` в JUnit 5 |
| `@Container` | Поле с контейнером (`static` — один на класс) |
| `@DynamicPropertySource` | Подстановка свойств (URL БД и т.д.) |
| `@AutoConfigureTestDatabase(replace = NONE)` | Не заменять `DataSource` на H2 |
| `@Sql` | Выполнить SQL-скрипт перед/после теста |
| `@Transactional` | Транзакция с откатом (по умолчанию в `@DataJpaTest`) |
| `@ActiveProfiles("test")` | Загрузить `application-test.yml` |

### Когда что использовать

| Сценарий | Рекомендация |
|----------|--------------|
| Тесты репозиториев | `@DataJpaTest` + `Testcontainers` или `H2` |
| Полные интеграционные тесты | `@SpringBootTest` + `Testcontainers` |
| Быстрые тесты без Docker | `@DataJpaTest` + `H2` |
| Проверка миграций | `Testcontainers` + Flyway/Liquibase |
| CI без Docker | `H2` (с ограничениями по диалекту) |


