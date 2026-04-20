---
title: "Testcontainers (Testcontainers)"
description: "Библиотека для запуска реальных Docker-контейнеров в интеграционных тестах (Java, JUnit). БД, очереди, веб-серверы, произвольные образы; изоляция, повторяемость, совместимость с CI/CD."
tags:
  - testing
  - integration-testing
  - testcontainers
difficulty: "intermediate"
prerequisites: []
next: []
updated: "2026-04-20"
---
# Testcontainers (Testcontainers)

Библиотека для запуска реальных Docker-контейнеров в интеграционных тестах (Java, JUnit). БД, очереди, веб-серверы, произвольные образы; изоляция, повторяемость, совместимость с CI/CD.


### См. также
- [Вопросы на собеседовании](../../../interview/testing/testcontainers-interview.md) — подготовка к интервью

## Полезные ссылки

| Тип | Ссылка |
|-----|--------|
| Документация | [Testcontainers — Official](https://www.testcontainers.org/) |
| GitHub | [testcontainers-java](https://github.com/testcontainers/testcontainers-java) |
| JUnit 5 | [Getting Started JUnit 5](https://www.testcontainers.org/guides/getting_started_junit_5/) |
| Модули | [Modules](https://www.testcontainers.org/modules/) |
| См. также | [junit](../../unit-testing/junit/junit.md), [Database Testing](../database-testing/database-testing.md), [wiremock](../wiremock.md), [Testing Tools Overview](../../testing-tools/testing-tools-overview.md) |


## Содержание

- [Введение](#введение)
  - [Зачем нужны контейнеры в тестах](#зачем-нужны-контейнеры-в-тестах)
  - [Основные концепции](#основные-концепции)
- [Установка и настройка](#установка-и-настройка)
  - [Maven](#maven)
  - [Gradle](#gradle)
  - [Требования](#требования)
- [Базовое использование](#базовое-использование)
  - [GenericContainer](#genericcontainer)
  - [Жизненный цикл и параметры подключения](#жизненный-цикл-и-параметры-подключения)
- [Модули для БД](#модули-для-бд)
  - [PostgreSQL (основной пример)](#postgresql-основной-пример)
  - [Другие БД и сервисы](#другие-бд-и-сервисы)
- [Модули для очередей и брокеров](#модули-для-очередей-и-брокеров)
  - [Kafka](#kafka)
  - [RabbitMQ](#rabbitmq)
- [Docker Compose](#docker-compose)
- [Сеть, порты, тома и ожидание готовности](#сеть-порты-тома-и-ожидание-готовности)
  - [Сеть и порты](#сеть-и-порты)
  - [Volumes и файлы](#volumes-и-файлы)
  - [Ожидание готовности](#ожидание-готовности)
- [Переиспользование контейнеров](#переиспользование-контейнеров)
- [Интеграция с JUnit 5](#интеграция-с-junit-5)
- [Spring Boot и Testcontainers](#spring-boot-и-testcontainers)
- [CI/CD](#cicd)
- [Лучшие практики](#лучшие-практики)
- [Решение проблем](#решение-проблем)
  - [Таблица типичных проблем](#таблица-типичных-проблем)
- [Частые вопросы](#частые-вопросы)
- [Глоссарий](#глоссарий)
- [Итоговые таблицы](#итоговые-таблицы)
  - [Основные классы контейнеров](#основные-классы-контейнеров)
  - [Часто используемые методы и аннотации](#часто-используемые-методы-и-аннотации)
  - [Когда что использовать](#когда-что-использовать)
- [Рекомендуемый порядок изучения](#рекомендуемый-порядок-изучения)
- [Заключение](#заключение)

## Введение

**Testcontainers** — Java-библиотека для запуска **Docker**-контейнеров во время тестов. Используются реальные сервисы (СУБД, очереди, кэши), что повышает достоверность интеграционных тестов.

### Зачем нужны контейнеры в тестах

- **Реальное окружение** — те же образы и версии, что и в продакшене.
- **Изоляция** — каждый тест/класс может иметь свой контейнер.
- **Повторяемость** — одинаковое поведение на любой машине и в CI.
- **Нет ручной настройки** — достаточно Docker, не нужно ставить локально PostgreSQL, Kafka и т.д.
- **Совместимость с CI** — агенты с Docker (GitHub Actions, GitLab CI, Jenkins) запускают тесты без доп. инфраструктуры.

### Основные концепции

- **Container** — абстракция над Docker-контейнером; старт/стоп привязаны к жизненному циклу теста.
- **GenericContainer** — контейнер по произвольному образу (образ, порты, переменные окружения, команды).
- **Специализированные модули** — преднастроенные контейнеры для PostgreSQL, MySQL, Kafka, Redis с удобными методами (`getJdbcUrl()`, `getHost()`, `getMappedPort()`).
- **Жизненный цикл** — при **@Container** и **@Testcontainers** JUnit сам управляет стартом и остановкой.


## Установка и настройка

### Maven

```xml
<dependency>
    <groupId>org.testcontainers</groupId>
    <artifactId>testcontainers</artifactId>
    <version>1.19.3</version>
    <scope>test</scope>
</dependency>
<dependency>
    <groupId>org.testcontainers</groupId>
    <artifactId>junit-jupiter</artifactId>
    <version>1.19.3</version>
    <scope>test</scope>
</dependency>
<!-- по необходимости: postgresql, mysql, kafka и т.д. -->
<dependency>
    <groupId>org.testcontainers</groupId>
    <artifactId>postgresql</artifactId>
    <version>1.19.3</version>
    <scope>test</scope>
</dependency>
```

### Gradle

```groovy
dependencies {
    testImplementation 'org.testcontainers:testcontainers:1.19.3'
    testImplementation 'org.testcontainers:junit-jupiter:1.19.3'
    testImplementation 'org.testcontainers:postgresql:1.19.3'
}
```

### Требования

- **Java** 8+ (рекомендуется 11+).
- **Docker** установлен и доступен (`docker info`).
- В CI — агент с Docker (Docker-in-Docker или внешний Docker socket).


## Базовое использование

### GenericContainer

Запуск произвольного образа с пробросом портов и переменными окружения:

```java
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.utility.DockerImageName;

GenericContainer<?> container = new GenericContainer<>(
            DockerImageName.parse("postgres:15-alpine"))
            .withExposedPorts(5432)
            .withEnv("POSTGRES_USER", "test")
            .withEnv("POSTGRES_PASSWORD", "test")
            .withEnv("POSTGRES_DB", "test");
        container.start();

        String host = container.getHost();
        Integer port = container.getMappedPort(5432);
// подключение: host:port
container.stop();
```

Для продвинутых сценариев используйте **DockerImageName** (поддержка digest, проверка имени).

### Жизненный цикл и параметры подключения

| Действие | Описание |
|----------|----------|
| **start()** | Создаёт и запускает контейнер; после старта доступны `getHost()`, `getMappedPort()` |
| **stop()** | Останавливает и удаляет контейнер |
| **@Testcontainers** + **@Container** | JUnit 5 сам вызывает старт перед тестами класса и стоп после |

- **getHost()** — хост (обычно `localhost`).
- **getMappedPort(внутреннийПорт)** — внешний порт на хосте.
- Строка JDBC для PostgreSQL: `"jdbc:postgresql://" + container.getHost() + ":" + container.getMappedPort(5432) + "/test"`.


## Модули для БД

### PostgreSQL (основной пример)

```java
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

@Testcontainers
class PostgresIntegrationTest {

    @Container
    static final PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:15-alpine")
            .withDatabaseName("testdb")
            .withUsername("user")
            .withPassword("pass");

    @Test
    void testJdbcUrl() {
        String jdbcUrl = postgres.getJdbcUrl();
        assertThat(jdbcUrl).startsWith("jdbc:postgresql://");
    }
}
```

Методы: `getJdbcUrl()`, `getUsername()`, `getPassword()`, `getDatabaseName()`, `getHost()`, `getMappedPort(5432)`.

### Другие БД и сервисы

| Сервис | Класс | Основной метод / образ |
|--------|-------|------------------------|
| MySQL / MariaDB | **MySQLContainer** | `getJdbcUrl()`, образ `mysql:8.0` |
| MongoDB | **MongoDBContainer** | `getConnectionString()`, образ `mongo:6` |
| Redis | **GenericContainer** | `getMappedPort(6379)`, образ `redis:7-alpine` |
| Elasticsearch | **ElasticsearchContainer** | `getHttpHostAddress()`, образ с `discovery.type=single-node`, `xpack.security.enabled=false` |

Официального модуля для Redis может не быть — **GenericContainer** с образом **redis** достаточно.


## Модули для очередей и брокеров

### Kafka

```java
    @Container
static final KafkaContainer kafka = new KafkaContainer(
        DockerImageName.parse("confluentinc/cp-kafka:7.5.0"));
// kafka.getBootstrapServers()
```

### RabbitMQ

```java
    @Container
    static final RabbitMQContainer rabbit = new RabbitMQContainer("rabbitmq:3-management")
            .withVhost("test");
// rabbit.getAmqpUrl()
```


## Docker Compose

Запуск нескольких сервисов из одного файла:

```java
    @Container
    static final DockerComposeContainer<?> environment =
            new DockerComposeContainer<>(new File("src/test/resources/docker-compose.yml"))
                    .withExposedService("postgres", 5432)
                    .withExposedService("redis", 6379);

// environment.getServicePort("postgres", 5432);
```

**withExposedService** делает порт доступным; порт получают через **getServicePort**.


## Сеть, порты, тома и ожидание готовности

### Сеть и порты

- **withExposedPorts(port1, port2, ...)** — проброс портов; фактический порт — **getMappedPort(port)**.
- **withNetwork(Network)** — подключение к созданной сети (несколько контейнеров).
- **withNetworkAliases("alias")** — имя в сети; обращение по **alias:port**.
- **dependsOn(контейнер)** — старт после готовности другого контейнера.

### Volumes и файлы

- **withClasspathResourceMapping("path", "/container/path", BindMode.READ_ONLY)** — монтирование из classpath.
- **withFileSystemBind(hostPath, containerPath)** — монтирование с хоста.
- **withCopyFileToContainer(MountableFile.forHostPath(path), "/path")** — копирование при старте.
- **withInitScript("init.sql")** — SQL-скрипт инициализации (файл в classpath, например `src/test/resources/init.sql`).

### Ожидание готовности

- **waitingFor(Wait.forListeningPort())** — ждать открытия порта.
- **waitingFor(Wait.forHttp("/health").forStatusCode(200))** — ждать успешного HTTP.
- **waitingFor(Wait.forLogMessage("ready for connections", 1))** — ждать сообщения в логах.
- Специализированные модули (PostgreSQL, Kafka и т.д.) часто уже содержат стратегию ожидания.
- При необходимости: **withStartupTimeout(Duration.ofMinutes(5))**.


## Переиспользование контейнеров

- **@Container** с **static** полем — один контейнер на класс (разумный компромисс скорости и изоляции).
- **Singleton** на несколько тест-классов — вынести контейнер в базовый класс с **static** полем и **start()** в **@BeforeAll**.
- Переиспользование между запусками (reuse) — через переменные окружения и настройки Testcontainers; использовать осторожно, в CI чаще один контейнер на класс.


## Интеграция с JUnit 5

- **@Testcontainers** на классе — включает поддержку жизненного цикла контейнеров.
- **@Container** на поле типа **GenericContainer** (или специализированного) — контейнер стартует перед тестами и останавливается после.
- Поле **static** — один контейнер на класс; не **static** — контейнер на каждый тест (медленнее).


## Spring Boot и Testcontainers

Подстановка URL и кредов через **@DynamicPropertySource**:

```java
@SpringBootTest
@Testcontainers
class MyApplicationIntegrationTest {

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
    private MyRepository repository;

    @Test
    void testRepository() {
        // тест с реальной БД
    }
}
```

Схему можно поднимать теми же миграциями (Flyway/Liquibase), что и в production — Spring подхватит URL из **@DynamicPropertySource**.


## CI/CD

- На агенте должен быть установлен **Docker** и доступ для тестов.
- При необходимости: **DOCKER_HOST**, **TESTCONTAINERS_*** для ryuk, reuse и т.д.
- Тяжёлые образы (Kafka, Elasticsearch) могут требовать больше памяти — увеличьте лимиты для тестовых job’ов.


## Лучшие практики

1. Используйте **специализированные модули** (PostgreSQL, Kafka и т.д.) вместо GenericContainer, когда они есть.
2. **Один контейнер на класс** (@Container + static) — баланс скорости и изоляции.
3. Фиксируйте **версии образов** (например, `postgres:15-alpine`), не `latest`.
4. Инициализацию схемы — через **withInitScript** или миграции (Flyway/Liquibase).
5. В тестах не хранить чувствительные данные продакшена.
6. В CI при отсутствии явного reuse не полагаться на переиспользование контейнеров между job’ами.


## Решение проблем

### Таблица типичных проблем

| Проблема | Возможная причина | Решение |
|----------|-------------------|---------|
| **Could not find a valid Docker environment** | Docker не установлен или недоступен | Установить Docker, проверить `docker info`, права пользователя |
| **Port already in use** | Конфликт портов или старый контейнер | Остановить старые контейнеры (`docker ps -a`), проверить занятость портов |
| **Image pull failed** | Нет доступа к registry | Настроить сеть/прокси, проверить доступ к Docker Hub или внутреннему registry |
| **Container startup timeout** | Сервис долго стартует | Увеличить таймаут (withStartupTimeout), добавить **waitingFor** под готовность |
| **OutOfMemoryError** в CI | Мало памяти у агента | Увеличить память для JVM/агента или использовать лёгкие образы (alpine) |
| **Tests pass locally, fail in CI** | Разные версии Docker/образов, права | Унифицировать версии образов, проверить переменные окружения в CI |
| **withInitScript — No such file** | Файл не в classpath | Положить в `src/test/resources`; путь указывать относительно classpath |
| **Spring context fails to start** | URL/креды не подставлены до инициализации БД | **@DynamicPropertySource** и **@Container** + **static**, чтобы контейнер стартовал до контекста |

## Частые вопросы

- **Нужно ли ставить PostgreSQL/MySQL локально?** — Нет, достаточно Docker.
- **Testcontainers в production?** — Нет, только для тестов.
- **Как ускорить тесты?** — Один контейнер на класс (static @Container), фиксированные образы, при необходимости alpine.
- **Работает ли с Kotlin?** — Да, те же аннотации и вызовы.
- **JUnit 4?** — Поддерживается через **@Rule** (см. документацию).
- **Свой Dockerfile?** — Собрать образ локально или в CI и указать в **DockerImageName.parse("myimage:tag")**.
- **Без Docker?** — Нет, нужен Docker daemon; альтернатива — in-memory БД (H2, HSQLDB) или моки.
- **Конкретная версия PostgreSQL?** — Указать образ, например `postgres:14-alpine` или `postgres:16-alpine`.
- **Отладка падающего теста?** — Вывод **container.getLogs()** при падении, увеличить **withStartupTimeout**, при необходимости проверить `docker ps -a`.
- **Лимит памяти контейнеру?** — **withCreateContainerCmdModifier** и **HostConfig** (см. документацию).
- **Podman?** — Экспериментальная поддержка через переменные окружения (проверить актуальную документацию).
- **Дублирование конфигурации в нескольких классах?** — Базовый класс с `@Testcontainers` и `static @Container`.


## Глоссарий

| Термин | Описание |
|--------|----------|
| **Container** | Экземпляр Docker-контейнера, управляемый Testcontainers |
| **GenericContainer** | Контейнер по произвольному образу |
| **Mapped port** | Порт на хосте, проброшенный в контейнер; **getMappedPort()** |
| **Module** | Специализированный класс (PostgreSQL, Kafka и т.д.) с удобными методами |
| **Ryuk** | Вспомогательный контейнер Testcontainers для очистки ресурсов после JVM |
| **WaitStrategy** | Стратегия ожидания готовности (порт, HTTP, лог) |
| **BindMode** | Режим монтирования тома (READ_ONLY, READ_WRITE) |


## Итоговые таблицы

### Основные классы контейнеров

| Класс | Образ (пример) | Назначение |
|-------|----------------|------------|
| **GenericContainer** | Любой | Универсальный контейнер |
| **PostgreSQLContainer** | postgres:15-alpine | PostgreSQL |
| **MySQLContainer** | mysql:8.0 | MySQL / MariaDB |
| **MongoDBContainer** | mongo:6 | MongoDB |
| **KafkaContainer** | confluentinc/cp-kafka | Apache Kafka |
| **RabbitMQContainer** | rabbitmq:3-management | RabbitMQ |
| **ElasticsearchContainer** | elasticsearch:8.x | Elasticsearch |
| **DockerComposeContainer** | docker-compose.yml | Несколько сервисов |

### Часто используемые методы и аннотации

| Метод / аннотация | Описание |
|-------------------|----------|
| **getHost()** | Хост для подключения (обычно localhost) |
| **getMappedPort(int)** | Внешний порт по внутреннему |
| **getJdbcUrl()** | JDBC URL (в модулях БД) |
| **start()** / **stop()** | Ручное управление жизненным циклом |
| **withExposedPorts(int...)** | Проброс портов |
| **withEnv(String, String)** | Переменная окружения |
| **withInitScript(String)** | SQL-скрипт инициализации (модули БД) |
| **waitingFor(WaitStrategy)** | Ожидание готовности |
| **@Testcontainers** | Включение поддержки жизненного цикла в JUnit 5 |
| **@Container** | Поле с контейнером; JUnit управляет жизненным циклом |
| **@DynamicPropertySource** | Подстановка свойств в Spring Boot из контейнера |

### Когда что использовать

| Сценарий | Рекомендация |
|----------|--------------|
| Одна БД в тестах | **PostgreSQLContainer** / **MySQLContainer** + **@Container** static |
| БД + очередь | Два **@Container** static + **@DynamicPropertySource** |
| Несколько связанных сервисов | **DockerComposeContainer** или несколько контейнеров в одной сети |
| Свой образ | **GenericContainer** + **withExposedPorts** + **waitingFor** |
| Инициализация схемы | **withInitScript** или Flyway/Liquibase в приложении |
| Ускорение в CI | Один контейнер на класс, образы с фиксированными тегами |


## Рекомендуемый порядок изучения

1. **GenericContainer** + **withExposedPorts** + **getHost()** / **getMappedPort()** — базовый цикл.
2. **PostgreSQLContainer** / **MySQLContainer** + **getJdbcUrl()** — интеграция с БД.
3. **@Testcontainers** и **@Container** (JUnit 5) — управление жизненным циклом.
4. **@DynamicPropertySource** (Spring Boot) — подстановка URL и кредов.
5. **waitingFor** — ожидание готовности сервиса.
6. **DockerComposeContainer** — несколько сервисов из одного compose-файла.
7. Сеть (withNetwork, withNetworkAliases), **dependsOn** — связанные контейнеры.
8. Переиспользование, CI/CD, отладка (таблица выше).


## Заключение

**Testcontainers** позволяет запускать реальные Docker-контейнеры (БД, очереди, сервисы) в интеграционных тестах на JUnit 5. Используйте специализированные модули где возможно, фиксируйте версии образов; один контейнер на класс даёт хороший баланс скорости и изоляции. Интеграция с Spring Boot через **@DynamicPropertySource** упрощает подстановку URL и кредов. В CI убедитесь в доступности Docker и при необходимости настройте таймауты и ресурсы.

Для углублённого изучения: [официальная документация](https://www.testcontainers.org/) и [Testing Tools Overview](../../testing-tools/testing-tools-overview.md).
