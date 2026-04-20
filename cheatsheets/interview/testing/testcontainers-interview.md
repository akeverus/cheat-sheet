---
title: "Вопросы на собеседовании: Testcontainers"
description: "Testcontainers в Java: GenericContainer, контейнеры баз данных, Kafka, Redis, жизненный цикл, @Testcontainers/@Container, Spring Boot интеграция (@ServiceConnection, @DynamicPropertySource), singleton-паттерн, WaitStrategy, сети, docker-compose, CI/CD."
tags:
  - interview
  - testing
  - testcontainers-interview
aliases:
  - "Testcontainers"
  - "Testcontainers interview"
  - "Testcontainers собеседование"
  - "Test Containers"
  - "Тестконтейнеры"
difficulty: "intermediate"
updated: "2026-04-13"
---
# Вопросы на собеседовании: `Testcontainers`

`Testcontainers` — Java-библиотека, которая позволяет поднимать Docker-контейнеры прямо из тестового кода. Это даёт возможность писать интеграционные тесты против реальных баз данных, брокеров сообщений, кэшей и любых других сервисов вместо in-memory заглушек. Этот файл покрывает архитектуру библиотеки, контейнеры для различных технологий, интеграцию со `Spring Boot 3.x`, паттерны управления жизненным циклом и оптимизацию для `CI/CD`.

**`Testcontainers`** стал де-факто стандартом интеграционного тестирования в Java-экосистеме. Библиотека решает ключевую проблему: тесты на in-memory заменителях (вроде `H2`) часто не ловят реальные баги, а настройка полноценной инфраструктуры для тестов — сложна и ненадёжна.

## Полезные ссылки

### Официальная документация

- [Testcontainers for Java](https://java.testcontainers.org/) — официальная документация
- [Testcontainers Guides](https://testcontainers.com/guides/) — практические гайды
- [Spring Boot: Testcontainers](https://docs.spring.io/spring-boot/reference/testing/testcontainers.html) — встроенная поддержка в Spring Boot
- [Baeldung: Docker Test Containers](https://www.baeldung.com/docker-test-containers) — базовый гайд по Testcontainers
- [Baeldung: Spring Boot + Testcontainers](https://www.baeldung.com/spring-boot-testcontainers-integration-test) — интеграция со Spring Boot
- [Baeldung: Built-in Testcontainers Support](https://www.baeldung.com/spring-boot-built-in-testcontainers) — `@ServiceConnection` в Spring Boot 3.1+
- [Baeldung: Reuse Testcontainers](https://www.baeldung.com/java-reuse-testcontainers) — переиспользование контейнеров
- [Baeldung: Testcontainers JDBC Support](https://www.baeldung.com/testcontainers-jdbc-support) — JDBC-интеграция

## Содержание

- [Полезные ссылки](#полезные-ссылки)
- [See also](#see-also)

**Основы `Testcontainers`**
- [Q1. (!) Что такое `Testcontainers` и какую проблему решает библиотека?](#q1--что-такое-testcontainers-и-какую-проблему-решает-библиотека)
- [Q2. Какие предварительные требования нужны для работы `Testcontainers`?](#q2-какие-предварительные-требования-нужны-для-работы-testcontainers)
- [Q3. (!) Как устроена архитектура `Testcontainers`?](#q3--как-устроена-архитектура-testcontainers)
- [Q4. Что такое `GenericContainer` и когда его использовать?](#q4-что-такое-genericcontainer-и-когда-его-использовать)

**Контейнеры баз данных**
- [Q5. (!) Как использовать `PostgreSQLContainer` в тестах?](#q5--как-использовать-postgresqlcontainer-в-тестах)
- [Q6. Какие контейнеры баз данных поддерживает `Testcontainers`?](#q6-какие-контейнеры-баз-данных-поддерживает-testcontainers)
- [Q7. Как работает JDBC-поддержка `Testcontainers`?](#q7-как-работает-jdbc-поддержка-testcontainers)
- [Q8. (!) В чём преимущество `Testcontainers` перед `H2` и другими in-memory базами?](#q8--в-чём-преимущество-testcontainers-перед-h2-и-другими-in-memory-базами)

**Контейнеры для `Kafka`, `Redis`, `MongoDB`**
- [Q9. Как поднять `Kafka`-контейнер для тестов?](#q9-как-поднять-kafka-контейнер-для-тестов)
- [Q10. Как тестировать приложение с `Redis` через `Testcontainers`?](#q10-как-тестировать-приложение-с-redis-через-testcontainers)
- [Q11. Как использовать `MongoDB`-контейнер?](#q11-как-использовать-mongodb-контейнер)

**Жизненный цикл и аннотации**
- [Q12. (!) Как работают аннотации `@Testcontainers` и `@Container`?](#q12--как-работают-аннотации-testcontainers-и-container)
- [Q13. В чём разница между `static` и instance-полями с `@Container`?](#q13-в-чём-разница-между-static-и-instance-полями-с-container)
- [Q14. Как управлять жизненным циклом контейнера вручную?](#q14-как-управлять-жизненным-циклом-контейнера-вручную)

**Интеграция со `Spring Boot`**
- [Q15. (!) Как подключить `Testcontainers` через `@DynamicPropertySource`?](#q15--как-подключить-testcontainers-через-dynamicpropertysource)
- [Q16. (!) Что такое `@ServiceConnection` в Spring Boot 3.1+ и чем лучше `@DynamicPropertySource`?](#q16--что-такое-serviceconnection-в-spring-boot-31-и-чем-лучше-dynamicpropertysource)
- [Q17. Как использовать `@TestConfiguration` с `Testcontainers` в Spring Boot 3.1+?](#q17-как-использовать-testconfiguration-с-testcontainers-в-spring-boot-31)
- [Q18. Как запустить Spring Boot приложение с `Testcontainers` для локальной разработки?](#q18-как-запустить-spring-boot-приложение-с-testcontainers-для-локальной-разработки)

**Паттерны и оптимизация**
- [Q19. (!) Что такое Singleton Containers паттерн и зачем он нужен?](#q19--что-такое-singleton-containers-паттерн-и-зачем-он-нужен)
- [Q20. Как переиспользовать контейнеры между запусками тестов (`reusable containers`)?](#q20-как-переиспользовать-контейнеры-между-запусками-тестов-reusable-containers)
- [Q21. Как запускать тесты с `Testcontainers` параллельно?](#q21-как-запускать-тесты-с-testcontainers-параллельно)

**`WaitStrategy` и конфигурация**
- [Q22. (!) Что такое `WaitStrategy` и какие стратегии ожидания доступны?](#q22--что-такое-waitstrategy-и-какие-стратегии-ожидания-доступны)
- [Q23. Как настроить сеть между контейнерами?](#q23-как-настроить-сеть-между-контейнерами)
- [Q24. Как использовать модуль `docker-compose`?](#q24-как-использовать-модуль-docker-compose)

**`CI/CD` и production-практики**
- [Q25. (!) Как интегрировать `Testcontainers` в `CI/CD` pipeline?](#q25--как-интегрировать-testcontainers-в-cicd-pipeline)
- [Q26. Какие есть способы ускорить тесты с `Testcontainers`?](#q26-какие-есть-способы-ускорить-тесты-с-testcontainers)
- [Q27. Какие ограничения и подводные камни есть у `Testcontainers`?](#q27-какие-ограничения-и-подводные-камни-есть-у-testcontainers)

**Дополнительные контейнеры и сценарии**
- [Q28. Как использовать `ElasticsearchContainer` в тестах?](#q28-как-использовать-elasticsearchcontainer-в-тестах)
- [Q29. Как тестировать отказоустойчивость с `Toxiproxy`?](#q29-как-тестировать-отказоустойчивость-с-toxiproxy)
- [Q30. (!) Как использовать `LocalStack` для тестирования AWS-сервисов?](#q30--как-использовать-localstack-для-тестирования-aws-сервисов)
- [Q31. Как тестировать с несколькими версиями одного сервиса?](#q31-как-тестировать-с-несколькими-версиями-одного-сервиса)
- [Q32. Как организовать базовый класс для интеграционных тестов с `Testcontainers`?](#q32-как-организовать-базовый-класс-для-интеграционных-тестов-с-testcontainers)

**Продвинутые темы**
- [Q33. Что такое Ryuk и как он управляет cleanup контейнеров?](#q33-что-такое-ryuk-и-как-он-управляет-cleanup-контейнеров)
- [Q34. Как использовать `WireMock` как Testcontainer для моков внешних HTTP API?](#q34-как-использовать-wiremock-как-testcontainer-для-моков-внешних-http-api)
- [Q35. Что такое `Testcontainers Cloud` и когда его использовать?](#q35-что-такое-testcontainers-cloud-и-когда-его-использовать)
- [Q36. Как использовать `DockerComposeContainer` — плюсы и минусы?](#q36-как-использовать-dockercomposecontainer--плюсы-и-минусы)
- [Q37. Как запускать `Testcontainers`-тесты параллельно без конфликтов?](#q37-как-запускать-testcontainers-тесты-параллельно-без-конфликтов)
- [Q38. Как Testcontainers используется в Kotlin-проектах — DSL и особенности?](#q38-как-testcontainers-используется-в-kotlin-проектах--dsl-и-особенности)
- [Q39. Что такое `@ServiceConnection` в Spring Boot 3.1+ и как он работает?](#q39-что-такое-serviceconnection-в-spring-boot-31-и-как-он-работает)
- [Q40. Как `LocalStack` используется для тестирования AWS-сервисов локально?](#q40-как-localstack-используется-для-тестирования-aws-сервисов-локально)

---

## Q1. (!) Что такое `Testcontainers` и какую проблему решает библиотека?

**`Testcontainers`** — это Java-библиотека, предоставляющая легковесные, одноразовые Docker-контейнеры для использования в интеграционных тестах. Она позволяет поднимать реальные экземпляры баз данных, брокеров сообщений, кэшей и любых других сервисов прямо из тестового кода.

**Ключевые проблемы, которые решает:**

| Проблема | Как решает Testcontainers |
|----------|---------------------------|
| In-memory базы (H2) не поддерживают все SQL-диалекты | Тест работает с реальной `PostgreSQL`/`MySQL`/`Oracle` |
| Ручная настройка инфраструктуры для тестов | Контейнер стартует автоматически, конфигурация — в коде |
| «У меня локально работает» | Одинаковое окружение для всех разработчиков и CI |
| Конфликты между параллельными запусками | Каждый тест получает изолированный контейнер |
| Зависимость от shared test-сред | Контейнер одноразовый, состояние не протекает между запусками |

```mermaid
graph TB
    subgraph "Без Testcontainers"
        A[Тесты] --> B[H2 / Mock]
        B -.->|несовместимость| C[Production DB]
    end
    subgraph "С Testcontainers"
        D[Тесты] --> E[Docker Container]
        E -->|тот же движок| F[Production DB]
    end
```

**На собеседовании** важно подчеркнуть: `Testcontainers` не заменяет unit-тесты, а дополняет их на уровне интеграционных тестов, где нужна проверка работы с реальной инфраструктурой.

## Q2. Какие предварительные требования нужны для работы `Testcontainers`?

Для работы `Testcontainers` необходимо:

1. **Docker** — установленный и запущенный Docker-демон (Docker Desktop, Docker Engine, `Colima`, `Rancher Desktop` или `Podman` с совместимостью)
2. **JDK 8+** — библиотека поддерживает Java 8 и выше
3. **Gradle/Maven зависимость** — подключение нужных модулей

```groovy
// build.gradle
dependencies {
    // Основная библиотека
    testImplementation 'org.testcontainers:testcontainers:1.20.4'
    // JUnit 5 интеграция
    testImplementation 'org.testcontainers:junit-jupiter:1.20.4'
    // Модуль для конкретной технологии
    testImplementation 'org.testcontainers:postgresql:1.20.4'
}
```

`Testcontainers` общается с Docker через Docker API (обычно Unix-сокет `/var/run/docker.sock`). При старте библиотека запускает служебный контейнер `ryuk` (Reaper), который гарантирует очистку всех созданных контейнеров даже при аварийном завершении тестов.

**Проверка среды**: если Docker недоступен, тесты упадут с понятным сообщением. Для CI/CD это значит, что runner должен иметь доступ к Docker (Docker-in-Docker или Docker socket mount).

## Q3. (!) Как устроена архитектура `Testcontainers`?

Архитектура `Testcontainers` состоит из нескольких слоёв:

```mermaid
graph TB
    A[Тестовый код JUnit 5] --> B["@Testcontainers / @Container"]
    B --> C[Testcontainers Core]
    C --> D[Docker Client API]
    D --> E[Docker Daemon]
    E --> F["Контейнеры (PostgreSQL, Kafka, Redis...)"]
    C --> G["Ryuk (Resource Reaper)"]
    G -.->|cleanup| F
```

**Основные компоненты:**

- **`GenericContainer`** — базовый класс для любого Docker-образа
- **Специализированные контейнеры** — `PostgreSQLContainer`, `KafkaContainer`, `MongoDBContainer` и др. — наследники `GenericContainer` с преконфигурированными настройками
- **`Ryuk` (Resource Reaper)** — sidecar-контейнер, который отслеживает все созданные ресурсы (контейнеры, сети, volumes) и удаляет их после завершения тестов, даже если JVM аварийно остановилась
- **Docker Client** — обёртка вокруг `docker-java`, общается с Docker API
- **JUnit 5 Extension** (`@Testcontainers`) — управляет жизненным циклом контейнеров в соответствии с жизненным циклом тестов

**Порт-маппинг**: контейнеры стартуют с рандомными портами на хосте (`getMappedPort()`), что исключает конфликты при параллельном запуске.

## Q4. Что такое `GenericContainer` и когда его использовать?

`GenericContainer` — базовый класс, позволяющий запустить любой Docker-образ в тесте. Используется, когда нет специализированного модуля для нужного сервиса.

```java
@Testcontainers
class CustomServiceTest {

    @Container
    static GenericContainer<?> redis = new GenericContainer<>("redis:7-alpine")
            .withExposedPorts(6379)
            .waitingFor(Wait.forListeningPort());

    @Test
    void shouldConnectToRedis() {
        String host = redis.getHost();
        int port = redis.getMappedPort(6379);
        // Подключение к Redis по host:port
    }
}
```

**Ключевые методы `GenericContainer`:**

| Метод | Назначение |
|-------|-----------|
| `withExposedPorts(int...)` | Пробросить порты из контейнера |
| `withEnv(key, value)` | Задать переменную окружения |
| `withCommand(String...)` | Переопределить команду запуска |
| `withFileSystemBind(hostPath, containerPath)` | Смонтировать файл/каталог |
| `waitingFor(WaitStrategy)` | Задать стратегию ожидания готовности |
| `withNetwork(Network)` | Подключить к Docker-сети |
| `getHost()` | Получить хост (обычно `localhost`) |
| `getMappedPort(int)` | Получить маппированный порт на хосте |

`GenericContainer` — правильный выбор, когда нужен нестандартный сервис (например, собственный микросервис, `Minio`, `LocalStack` и т.д.).

## Q5. (!) Как использовать `PostgreSQLContainer` в тестах?

`PostgreSQLContainer` — специализированный контейнер, который знает всё о `PostgreSQL`: порт по умолчанию, healthcheck, создание БД и пользователя.

```java
@Testcontainers
@SpringBootTest
class UserRepositoryTest {

    @Container
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:16-alpine")
            .withDatabaseName("testdb")
            .withUsername("test")
            .withPassword("test")
            .withInitScript("init.sql"); // опционально

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
    }

    @Autowired
    private UserRepository userRepository;

    @Test
    void shouldSaveAndFindUser() {
        User user = new User("John", "john@example.com");
        userRepository.save(user);

        Optional<User> found = userRepository.findByEmail("john@example.com");
        assertThat(found).isPresent();
    }
}
```

**Преимущества специализированного контейнера:**
- Автоматический healthcheck (ждёт пока БД примет подключения)
- Методы `getJdbcUrl()`, `getUsername()`, `getPassword()` — не нужно собирать URL вручную
- Поддержка `withInitScript()` — выполнить SQL при старте
- Правильные настройки `WaitStrategy` из коробки

## Q6. Какие контейнеры баз данных поддерживает `Testcontainers`?

`Testcontainers` предоставляет специализированные модули для большинства популярных баз данных:

| Модуль | Класс контейнера | Артефакт Gradle |
|--------|------------------|-----------------|
| `PostgreSQL` | `PostgreSQLContainer` | `org.testcontainers:postgresql` |
| `MySQL` | `MySQLContainer` | `org.testcontainers:mysql` |
| `MariaDB` | `MariaDBContainer` | `org.testcontainers:mariadb` |
| `Oracle XE` | `OracleContainer` | `org.testcontainers:oracle-xe` |
| `MS SQL Server` | `MSSQLServerContainer` | `org.testcontainers:mssqlserver` |
| `MongoDB` | `MongoDBContainer` | `org.testcontainers:mongodb` |
| `Cassandra` | `CassandraContainer` | `org.testcontainers:cassandra` |
| `ClickHouse` | `ClickHouseContainer` | `org.testcontainers:clickhouse` |
| `CockroachDB` | `CockroachContainer` | `org.testcontainers:cockroachdb` |
| `Neo4j` | `Neo4jContainer` | `org.testcontainers:neo4j` |

Все эти контейнеры наследуют `JdbcDatabaseContainer` (для реляционных) или `GenericContainer` и предоставляют типобезопасные методы конфигурации.

## Q7. Как работает JDBC-поддержка `Testcontainers`?

`Testcontainers` предоставляет специальный JDBC-драйвер, который позволяет стартовать контейнер автоматически просто через строку подключения, без какого-либо Java-кода.

```yaml
# application-test.yml
spring:
  datasource:
    url: jdbc:tc:postgresql:16-alpine:///testdb
    driver-class-name: org.testcontainers.jdbc.ContainerDatabaseDriver
```

**Формат URL**: `jdbc:tc:<engine>:<tag>:///<database>`

- `jdbc:tc:postgresql:16-alpine:///mydb` — PostgreSQL 16
- `jdbc:tc:mysql:8.0:///mydb` — MySQL 8
- `jdbc:tc:mariadb:10.6:///mydb` — MariaDB 10.6

**Дополнительные параметры в URL:**
- `?TC_INITSCRIPT=init.sql` — выполнить скрипт при старте
- `?TC_INITFUNCTION=com.example.Init::setup` — вызвать Java-метод
- `?TC_DAEMON=true` — не останавливать контейнер после закрытия соединения

Это удобно для быстрого старта, но для production-кода лучше использовать явное создание контейнера, чтобы контролировать жизненный цикл и конфигурацию.

## Q8. (!) В чём преимущество `Testcontainers` перед `H2` и другими in-memory базами?

Это один из самых частых вопросов на собеседовании. Ключевые отличия:

| Критерий | `H2` / In-Memory | `Testcontainers` |
|----------|-------------------|-------------------|
| SQL-диалект | Эмуляция (часто неполная) | Реальный движок базы |
| Хранимые процедуры | Не поддерживаются | Полная поддержка |
| Специфичные типы (`JSONB`, `ARRAY`) | Частично или нет | Полная поддержка |
| Расширения (`pg_trgm`, `PostGIS`) | Нет | Да |
| Индексы, планы запросов | Другой оптимизатор | Тот же, что в production |
| Скорость старта | Мгновенно | 2-10 секунд |
| Docker-зависимость | Нет | Да |
| Confidence уровень | Средний | Высокий |

**Реальный пример проблемы с `H2`:**

```sql
-- Работает в PostgreSQL, но НЕ работает в H2
SELECT * FROM users
WHERE metadata @> '{"role": "admin"}'::jsonb;

-- H2 не поддерживает оператор @> для JSONB
```

**Рекомендация**: используйте `H2` только для простых CRUD-тестов, где SQL-диалект не важен. Для всего остального — `Testcontainers`. Подробнее о подходах к тестированию в [[integration-testing-interview|Integration Testing]].

## Q9. Как поднять `Kafka`-контейнер для тестов?

`Testcontainers` предоставляет модуль для `Apache Kafka` на базе образа `confluentinc/cp-kafka`:

```java
@Testcontainers
@SpringBootTest
class OrderEventProducerTest {

    @Container
    static KafkaContainer kafka = new KafkaContainer(
            DockerImageName.parse("confluentinc/cp-kafka:7.6.0")
    );

    @DynamicPropertySource
    static void kafkaProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.kafka.bootstrap-servers", kafka::getBootstrapServers);
    }

    @Autowired
    private KafkaTemplate<String, String> kafkaTemplate;

    @Autowired
    private KafkaListenerEndpointRegistry registry;

    @Test
    void shouldSendAndReceiveEvent() throws Exception {
        kafkaTemplate.send("orders", "order-123", "{\"status\":\"CREATED\"}");
        // проверка через consumer или awaitility
    }
}
```

**Особенности `KafkaContainer`:**
- Включает встроенный `ZooKeeper` (или `KRaft` в новых версиях)
- `getBootstrapServers()` — возвращает адрес для подключения
- `withKraft()` — использовать `KRaft` вместо `ZooKeeper` (Kafka 3.3+)
- Можно настроить количество брокеров через `KafkaContainer` + `Network`

## Q10. Как тестировать приложение с `Redis` через `Testcontainers`?

Для `Redis` можно использовать `GenericContainer` (специализированного контейнера нет в основной поставке):

```java
@Testcontainers
@SpringBootTest
class CacheServiceTest {

    @Container
    static GenericContainer<?> redis = new GenericContainer<>("redis:7-alpine")
            .withExposedPorts(6379);

    @DynamicPropertySource
    static void redisProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.data.redis.host", redis::getHost);
        registry.add("spring.data.redis.port", () -> redis.getMappedPort(6379));
    }

    @Autowired
    private CacheService cacheService;

    @Test
    void shouldCacheAndRetrieveValue() {
        cacheService.put("key1", "value1");
        assertThat(cacheService.get("key1")).isEqualTo("value1");
    }
}
```

В **Spring Boot 3.1+** с `@ServiceConnection` код ещё проще — см. [Q16](#q16--что-такое-serviceconnection-в-spring-boot-31-и-чем-лучше-dynamicpropertysource).

## Q11. Как использовать `MongoDB`-контейнер?

`Testcontainers` предоставляет модуль `mongodb` со специализированным контейнером:

```java
@Testcontainers
@SpringBootTest
class ProductRepositoryTest {

    @Container
    static MongoDBContainer mongo = new MongoDBContainer("mongo:7.0");

    @DynamicPropertySource
    static void mongoProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.data.mongodb.uri", mongo::getReplicaSetUrl);
    }

    @Autowired
    private ProductRepository productRepository;

    @Test
    void shouldSaveDocument() {
        Product product = new Product("Widget", 9.99);
        productRepository.save(product);

        List<Product> products = productRepository.findByName("Widget");
        assertThat(products).hasSize(1);
    }
}
```

**Ключевые особенности `MongoDBContainer`:**
- Запускает `MongoDB` в режиме replica set (нужно для транзакций)
- `getReplicaSetUrl()` — возвращает connection string с replica set
- `getConnectionString()` — базовый connection string
- Поддержка инициализации через JavaScript-скрипты

## Q12. (!) Как работают аннотации `@Testcontainers` и `@Container`?

Аннотации `@Testcontainers` и `@Container` — это JUnit 5 Extension для автоматического управления жизненным циклом контейнеров.

```java
@Testcontainers  // (1) Активирует расширение
class DatabaseTest {

    @Container // (2) Помечает контейнер для управления
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:16-alpine");

    @Test
    void testQuery() {
        assertTrue(postgres.isRunning());
    }
}
```

**Как это работает:**
1. `@Testcontainers` — регистрирует `TestcontainersExtension` для JUnit 5
2. Extension находит все поля, помеченные `@Container`
3. Перед тестами вызывает `container.start()`
4. После тестов вызывает `container.stop()`

```mermaid
sequenceDiagram
    participant JUnit as JUnit 5
    participant Ext as TestcontainersExtension
    participant C as Container
    participant Docker as Docker Daemon

    JUnit->>Ext: @BeforeAll / @BeforeEach
    Ext->>C: start()
    C->>Docker: docker run ...
    Docker-->>C: Container ID
    C-->>Ext: Ready
    JUnit->>JUnit: Выполнение тестов
    JUnit->>Ext: @AfterAll / @AfterEach
    Ext->>C: stop()
    C->>Docker: docker rm -f ...
```

**Важно**: без `@Testcontainers` аннотация `@Container` не работает — контейнер не будет ни запущен, ни остановлен автоматически.

## Q13. В чём разница между `static` и instance-полями с `@Container`?

Модификатор `static` на поле с `@Container` определяет **scope** жизненного цикла контейнера:

| Тип поля | Жизненный цикл | Когда использовать |
|----------|----------------|-------------------|
| `static` | Один контейнер на весь класс (`@BeforeAll` / `@AfterAll`) | Дорогие контейнеры (БД, Kafka) — чтобы не перезапускать |
| Instance | Новый контейнер для каждого теста (`@BeforeEach` / `@AfterEach`) | Нужна полная изоляция между тестами |

```java
@Testcontainers
class LifecycleExample {

    // Стартует ОДИН раз для всех тестов в классе
    @Container
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:16-alpine");

    // Стартует ЗАНОВО для каждого теста
    @Container
    GenericContainer<?> redis = new GenericContainer<>("redis:7-alpine")
            .withExposedPorts(6379);
}
```

**Рекомендация**: для баз данных почти всегда используйте `static` — старт контейнера занимает секунды, а изоляцию данных между тестами лучше обеспечить через `@Transactional` rollback или очистку таблиц.

## Q14. Как управлять жизненным циклом контейнера вручную?

Иногда аннотации не подходят — например, при использовании Singleton-паттерна или в не-JUnit фреймворках. В таких случаях контейнер управляется вручную:

```java
class ManualLifecycleTest {

    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:16-alpine");

    @BeforeAll
    static void startContainer() {
        postgres.start();
    }

    @AfterAll
    static void stopContainer() {
        postgres.stop();
    }

    @Test
    void testWithManualLifecycle() {
        assertThat(postgres.isRunning()).isTrue();
    }
}
```

**Когда нужен ручной контроль:**
- Singleton-паттерн (контейнер на весь тестовый набор)
- Условный запуск (пропустить, если Docker недоступен)
- Кастомная инициализация (выполнить SQL/скрипт между `start()` и тестами)
- Использование с TestNG или другими фреймворками

**Важно**: если вызвать `start()` вручную и при этом использовать `@Container`, контейнер может быть остановлен дважды. Не смешивайте подходы.

## Q15. (!) Как подключить `Testcontainers` через `@DynamicPropertySource`?

`@DynamicPropertySource` — аннотация Spring Boot, которая позволяет зарегистрировать свойства приложения динамически, после старта контейнера.

```java
@Testcontainers
@SpringBootTest
class OrderServiceTest {

    @Container
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:16-alpine");

    @Container
    static KafkaContainer kafka = new KafkaContainer(
            DockerImageName.parse("confluentinc/cp-kafka:7.6.0"));

    @DynamicPropertySource
    static void registerProperties(DynamicPropertyRegistry registry) {
        // PostgreSQL
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
        // Kafka
        registry.add("spring.kafka.bootstrap-servers", kafka::getBootstrapServers);
    }
}
```

**Почему нужен `@DynamicPropertySource`?**
Контейнер стартует с рандомным портом, и JDBC URL становится известен только в runtime. Обычный `@TestPropertySource` с фиксированным значением не подойдёт.

**Механизм**: метод вызывается после старта контейнера, но до инициализации Spring `ApplicationContext`. Это позволяет Spring подхватить правильные значения через `DynamicPropertyRegistry`.

## Q16. (!) Что такое `@ServiceConnection` в Spring Boot 3.1+ и чем лучше `@DynamicPropertySource`?

`@ServiceConnection` — аннотация, появившаяся в Spring Boot 3.1, которая автоматически определяет тип контейнера и регистрирует все необходимые свойства подключения. Заменяет ручное заполнение через `@DynamicPropertySource`.

**До Spring Boot 3.1 (`@DynamicPropertySource`):**

```java
@Container
static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:16-alpine");

@DynamicPropertySource
static void props(DynamicPropertyRegistry registry) {
    registry.add("spring.datasource.url", postgres::getJdbcUrl);
    registry.add("spring.datasource.username", postgres::getUsername);
    registry.add("spring.datasource.password", postgres::getPassword);
    registry.add("spring.datasource.driver-class-name", postgres::getDriverClassName);
}
```

**С Spring Boot 3.1+ (`@ServiceConnection`):**

```java
@Container
@ServiceConnection
static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:16-alpine");

// Всё! Никакого @DynamicPropertySource
```

**Как это работает:**
1. `@ServiceConnection` создаёт `ConnectionDetails` bean
2. Spring Boot определяет тип контейнера (PostgreSQL, Kafka, Redis и т.д.)
3. Автоматически регистрирует все нужные properties

**Поддерживаемые контейнеры** (неполный список):

| Контейнер | Создаёт `ConnectionDetails` для |
|-----------|-------------------------------|
| `PostgreSQLContainer` | `JdbcConnectionDetails` |
| `MySQLContainer` | `JdbcConnectionDetails` |
| `MongoDBContainer` | `MongoConnectionDetails` |
| `KafkaContainer` | `KafkaConnectionDetails` |
| `RedisContainer` / `GenericContainer` (Redis) | `RedisConnectionDetails` |
| `RabbitMQContainer` | `RabbitConnectionDetails` |
| `ElasticsearchContainer` | `ElasticsearchConnectionDetails` |

**Преимущества перед `@DynamicPropertySource`:**
- Меньше boilerplate-кода
- Не нужно помнить имена properties
- Менее подвержено ошибкам (опечатки в ключах)
- Работает через `ConnectionDetails` — type-safe подход

## Q17. Как использовать `@TestConfiguration` с `Testcontainers` в Spring Boot 3.1+?

Spring Boot 3.1+ позволяет выделить конфигурацию контейнеров в отдельный `@TestConfiguration` класс и переиспользовать его:

```java
@TestConfiguration(proxyBeanMethods = false)
class TestcontainersConfig {

    @Bean
    @ServiceConnection
    PostgreSQLContainer<?> postgresContainer() {
        return new PostgreSQLContainer<>("postgres:16-alpine");
    }

    @Bean
    @ServiceConnection
    KafkaContainer kafkaContainer() {
        return new KafkaContainer(DockerImageName.parse("confluentinc/cp-kafka:7.6.0"));
    }
}
```

**Использование в тестах:**

```java
@SpringBootTest
@Import(TestcontainersConfig.class)
class OrderServiceTest {

    @Autowired
    private OrderService orderService;

    @Test
    void shouldCreateOrder() {
        // PostgreSQL и Kafka уже запущены и подключены
    }
}
```

**Использование для локальной разработки** (Spring Boot 3.1+):

```java
public class TestApplication {

    public static void main(String[] args) {
        SpringApplication.from(Application::main)
                .with(TestcontainersConfig.class)
                .run(args);
    }
}
```

Это позволяет запустить приложение с реальными контейнерами вместо установки баз данных локально.

## Q18. Как запустить Spring Boot приложение с `Testcontainers` для локальной разработки?

Начиная со Spring Boot 3.1, `Testcontainers` можно использовать не только в тестах, но и для **локальной разработки** (`dev mode`):

```java
// src/test/java/com/example/TestApplication.java
public class TestApplication {

    public static void main(String[] args) {
        SpringApplication.from(Application::main)
                .with(ContainersConfig.class)
                .run(args);
    }
}

@TestConfiguration(proxyBeanMethods = false)
class ContainersConfig {

    @Bean
    @ServiceConnection
    @RestartScope  // контейнер переживёт DevTools restart
    PostgreSQLContainer<?> postgres() {
        return new PostgreSQLContainer<>("postgres:16-alpine");
    }
}
```

**Запуск**: `./gradlew bootTestRun` (Gradle) или `mvn spring-boot:test-run` (Maven).

**Преимущества:**
- Не нужно устанавливать `PostgreSQL`, `Redis`, `Kafka` локально
- Одинаковая конфигурация у всей команды
- `@RestartScope` — контейнер не перезапускается при `DevTools` restart
- Описание инфраструктуры — в коде, рядом с приложением

## Q19. (!) Что такое Singleton Containers паттерн и зачем он нужен?

**Singleton Containers** — паттерн, при котором один контейнер разделяется между всеми тестовыми классами через наследование от общего базового класса.

```java
// Базовый класс — контейнер стартует один раз
public abstract class AbstractIntegrationTest {

    static final PostgreSQLContainer<?> POSTGRES;
    static final KafkaContainer KAFKA;

    static {
        POSTGRES = new PostgreSQLContainer<>("postgres:16-alpine");
        KAFKA = new KafkaContainer(DockerImageName.parse("confluentinc/cp-kafka:7.6.0"));

        // Старт всех контейнеров параллельно
        Stream.of(POSTGRES, KAFKA).parallel().forEach(GenericContainer::start);
    }

    @DynamicPropertySource
    static void registerProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", POSTGRES::getJdbcUrl);
        registry.add("spring.datasource.username", POSTGRES::getUsername);
        registry.add("spring.datasource.password", POSTGRES::getPassword);
        registry.add("spring.kafka.bootstrap-servers", KAFKA::getBootstrapServers);
    }
}
```

```java
// Конкретный тест наследует базовый класс
@SpringBootTest
class OrderServiceTest extends AbstractIntegrationTest {

    @Test
    void shouldCreateOrder() {
        // POSTGRES и KAFKA уже запущены
    }
}
```

```mermaid
graph TD
    A["AbstractIntegrationTest<br/>(static initializer)"] -->|start()| B[PostgreSQL Container]
    A -->|start()| C[Kafka Container]
    D[OrderServiceTest] -->|extends| A
    E[UserServiceTest] -->|extends| A
    F[PaymentServiceTest] -->|extends| A
    D -.->|использует| B
    D -.->|использует| C
    E -.->|использует| B
    F -.->|использует| B
    F -.->|использует| C
```

**Критическая ошибка**: НЕ используйте `@Testcontainers` + `@Container` с singleton-паттерном. Extension попытается остановить контейнер после каждого тестового класса, что сломает остальные тесты.

**Когда использовать:**
- Много тестовых классов, все используют одну БД
- Старт контейнеров занимает значительное время
- Изоляция данных обеспечивается через `@Transactional` или `TRUNCATE`

## Q20. Как переиспользовать контейнеры между запусками тестов (`reusable containers`)?

**Reusable Containers** — механизм, при котором контейнер не останавливается после тестового запуска и переиспользуется при следующем запуске.

**Шаг 1**: включить в `~/.testcontainers.properties`:

```properties
testcontainers.reuse.enable=true
```

**Шаг 2**: пометить контейнер:

```java
static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:16-alpine")
        .withReuse(true);
```

**Как это работает:**
1. При первом запуске контейнер стартует как обычно
2. После завершения тестов контейнер **не останавливается**
3. При следующем запуске `Testcontainers` находит уже работающий контейнер по хешу конфигурации
4. Тесты подключаются к существующему контейнеру мгновенно

**Ограничения:**
- Данные в контейнере сохраняются между запусками — нужна очистка
- Не работает с `@Container` (контейнер будет остановлен Extension'ом)
- Предназначен для **локальной разработки**, а не для CI/CD
- Ryuk отключается для reusable-контейнеров

**Экономия**: старт `PostgreSQL` контейнера ~ 3-5 секунд, при reuse — подключение мгновенно. Для больших тестовых наборов это экономит минуты.

## Q21. Как запускать тесты с `Testcontainers` параллельно?

Параллельное выполнение тестов с `Testcontainers` требует учёта нескольких факторов:

**JUnit 5 параллельное выполнение:**

```properties
# junit-platform.properties
junit.jupiter.execution.parallel.enabled=true
junit.jupiter.execution.parallel.mode.classes.default=concurrent
```

**Подходы к параллелизации:**

| Подход | Плюсы | Минусы |
|--------|-------|--------|
| Контейнер на класс (`static @Container`) | Полная изоляция | Много контейнеров, большой расход ресурсов |
| Singleton + изоляция данных | Один контейнер, экономия ресурсов | Нужна стратегия изоляции данных |
| Разные схемы/БД на класс | Баланс ресурсов и изоляции | Сложнее реализовать |

**Singleton + изоляция данных** (рекомендуемый подход):

```java
@SpringBootTest
class ParallelTest extends AbstractIntegrationTest {

    @BeforeEach
    void cleanData() {
        // Каждый тест начинает с чистых таблиц
        jdbcTemplate.execute("TRUNCATE TABLE orders CASCADE");
    }

    @Test
    void testA() { /* ... */ }

    @Test
    void testB() { /* ... */ }
}
```

**Важно**: при параллельном выполнении с одним контейнером нужна изоляция на уровне данных. `@Transactional` rollback не всегда работает с параллельными тестами (транзакции блокируют друг друга).

## Q22. (!) Что такое `WaitStrategy` и какие стратегии ожидания доступны?

`WaitStrategy` определяет, когда контейнер считается готовым к работе. Правильный выбор стратегии — критичен для стабильности тестов.

**Доступные стратегии:**

| Стратегия | Описание | Когда использовать |
|-----------|----------|-------------------|
| `Wait.forListeningPort()` | Ждёт открытия TCP-порта | Простые сервисы (Redis, Memcached) |
| `Wait.forHttp("/health")` | Ждёт HTTP 200 на endpoint | Web-приложения, API |
| `Wait.forLogMessage(regex, times)` | Ждёт строку в логах контейнера | Сервисы без healthcheck |
| `Wait.forHealthcheck()` | Использует Docker `HEALTHCHECK` | Образы с встроенным healthcheck |
| `Wait.forSuccessfulCommand(cmd)` | Выполняет команду в контейнере | Кастомная проверка готовности |

**Примеры использования:**

```java
// HTTP healthcheck
new GenericContainer<>("my-service:latest")
    .withExposedPorts(8080)
    .waitingFor(Wait.forHttp("/actuator/health")
        .forStatusCode(200)
        .withStartupTimeout(Duration.ofSeconds(60)));

// Ожидание строки в логах
new GenericContainer<>("elasticsearch:8.12.0")
    .waitingFor(Wait.forLogMessage(".*started.*", 1));

// Комбинирование стратегий
new GenericContainer<>("complex-service:latest")
    .waitingFor(new WaitAllStrategy()
        .withStrategy(Wait.forListeningPort())
        .withStrategy(Wait.forHttp("/ready").forStatusCode(200)));
```

**Типичная ошибка**: использование `Wait.forListeningPort()` для баз данных. Порт может быть открыт, но БД ещё не готова принимать запросы. Специализированные контейнеры (`PostgreSQLContainer`) уже используют правильную стратегию.

## Q23. Как настроить сеть между контейнерами?

Когда тестируемое приложение требует взаимодействия нескольких контейнеров друг с другом (не через хост), нужна общая `Network`:

```java
@Testcontainers
class MultiContainerNetworkTest {

    static Network network = Network.newNetwork();

    @Container
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:16-alpine")
            .withNetwork(network)
            .withNetworkAliases("db");

    @Container
    static GenericContainer<?> app = new GenericContainer<>("my-app:latest")
            .withNetwork(network)
            .withNetworkAliases("app")
            .withEnv("DB_URL", "jdbc:postgresql://db:5432/test") // обращение по alias
            .dependsOn(postgres);
}
```

```mermaid
graph LR
    subgraph Docker Network
        A[app<br/>my-app:latest] -->|"jdbc:postgresql://db:5432"| B[db<br/>postgres:16]
    end
    C[Тестовый код<br/>JUnit] -->|"localhost:randomPort"| A
```

**Ключевые моменты:**
- `withNetworkAliases("name")` — DNS-имя контейнера внутри сети
- `dependsOn(container)` — гарантирует порядок старта
- Внутри Docker-сети используются оригинальные порты (5432, не маппированные)
- `Network.SHARED` — предопределённая сеть для простых случаев

## Q24. Как использовать модуль `docker-compose`?

Модуль `docker-compose` позволяет поднимать целый стек из `docker-compose.yml`:

```java
@Testcontainers
class FullStackTest {

    @Container
    static DockerComposeContainer<?> environment =
        new DockerComposeContainer<>(new File("src/test/resources/docker-compose-test.yml"))
            .withExposedService("postgres", 5432,
                Wait.forListeningPort())
            .withExposedService("redis", 6379,
                Wait.forListeningPort())
            .withExposedService("kafka", 9092,
                Wait.forListeningPort());

    @Test
    void shouldStartFullStack() {
        String pgHost = environment.getServiceHost("postgres", 5432);
        int pgPort = environment.getServicePort("postgres", 5432);
        // Подключение к сервисам
    }
}
```

**`docker-compose-test.yml`:**

```yaml
version: '3.8'
services:
  postgres:
    image: postgres:16-alpine
    environment:
      POSTGRES_DB: testdb
      POSTGRES_USER: test
      POSTGRES_PASSWORD: test
  redis:
    image: redis:7-alpine
  kafka:
    image: confluentinc/cp-kafka:7.6.0
    environment:
      KAFKA_ADVERTISED_LISTENERS: PLAINTEXT://kafka:9092
```

**Когда использовать:**
- Есть готовый `docker-compose.yml` в проекте
- Много взаимосвязанных сервисов
- Нужно поднять копию production-окружения

**Ограничения:**
- Медленнее, чем отдельные контейнеры (docker-compose overhead)
- Сложнее получить динамические свойства (порты, хосты)
- Нет type-safe API (строковые имена сервисов)

## Q25. (!) Как интегрировать `Testcontainers` в `CI/CD` pipeline?

Интеграция `Testcontainers` в CI/CD требует, чтобы runner имел доступ к Docker.

**Основные подходы:**

| CI/CD система | Решение |
|---------------|---------|
| GitHub Actions | Docker доступен по умолчанию |
| GitLab CI | `docker:dind` service или socket mount |
| Jenkins | Docker agent или Docker socket mount |
| TeamCity | Docker build agents |

**GitHub Actions пример:**

```yaml
jobs:
  test:
    runs-on: ubuntu-latest
    steps:
      - uses: actions/checkout@v4
      - uses: actions/setup-java@v4
        with:
          java-version: '21'
          distribution: 'temurin'
      - name: Run tests
        run: ./gradlew test
        # Docker доступен в ubuntu-latest из коробки
```

**GitLab CI пример:**

```yaml
test:
  image: eclipse-temurin:21-jdk
  services:
    - docker:dind
  variables:
    DOCKER_HOST: tcp://docker:2375
    TESTCONTAINERS_HOST_OVERRIDE: docker
  script:
    - ./gradlew test
```

**Оптимизация для CI:**
- **Кэш Docker-образов**: используйте registry mirror или pre-pull образов
- **Testcontainers Cloud**: облачный Docker daemon, образы не качаются на runner
- **`TESTCONTAINERS_RYUK_DISABLED=true`**: отключить Ryuk в CI (контейнеры удалятся с runner'ом)
- Singleton-паттерн — чтобы не перезапускать контейнеры для каждого тест-класса

Подробнее о CI/CD pipeline в [[test-automation-interview|Test Automation]].

## Q26. Какие есть способы ускорить тесты с `Testcontainers`?

Тесты с контейнерами неизбежно медленнее unit-тестов. Вот проверенные способы оптимизации:

**1. Singleton Containers** — один контейнер на весь тестовый набор:

```java
abstract class BaseIT {
    static final PostgreSQLContainer<?> PG;
    static {
        PG = new PostgreSQLContainer<>("postgres:16-alpine");
        PG.start();
    }
}
```

**2. Параллельный старт контейнеров:**

```java
static {
    Startables.deepStart(postgres, kafka, redis).join();
}
```

**3. Лёгкие образы** — использовать `-alpine` варианты:

```java
// Вместо postgres:16 (400+ MB)
new PostgreSQLContainer<>("postgres:16-alpine"); // ~80 MB
```

**4. Reusable Containers** — для локальной разработки:

```java
container.withReuse(true);
```

**5. Разделение тестов по уровням:**

```groovy
// build.gradle
tasks.register('unitTest', Test) {
    useJUnitPlatform { excludeTags 'integration' }
}
tasks.register('integrationTest', Test) {
    useJUnitPlatform { includeTags 'integration' }
}
```

**6. tmpfs для баз данных** — хранить данные в RAM:

```java
new PostgreSQLContainer<>("postgres:16-alpine")
    .withTmpFs(Map.of("/var/lib/postgresql/data", "rw"));
```

| Оптимизация | Экономия |
|-------------|----------|
| Singleton вместо контейнер-на-класс | 60-80% времени старта |
| Alpine-образы | Быстрее pull |
| `tmpfs` | ~20-30% на операциях с диском |
| `Startables.deepStart()` | Параллельный старт вместо последовательного |
| Reusable containers | ~100% времени старта (локально) |

## Q27. Какие ограничения и подводные камни есть у `Testcontainers`?

**Технические ограничения:**

1. **Docker обязателен** — без Docker (или совместимой среды) тесты не запустятся. Это проблема в некоторых корпоративных средах
2. **Время старта** — каждый контейнер добавляет 2-10 секунд. Для большого количества контейнеров это существенно
3. **Потребление ресурсов** — каждый контейнер потребляет RAM и CPU. На CI-runner'ах с ограниченными ресурсами это критично
4. **Нестабильность в CI** — сетевые проблемы при pull образов, таймауты Docker

**Подводные камни:**

| Проблема | Решение |
|----------|---------|
| Порт уже занят | Всегда используйте рандомные порты (`getMappedPort`) |
| Данные протекают между тестами | Очищайте данные в `@BeforeEach` или используйте `@Transactional` |
| `@Container` + Singleton = контейнер остановлен | Не смешивайте паттерны (см. [Q19](#q19)) |
| Образ не найден / pull timeout | Используйте `ImagePullPolicy` или зеркало registry |
| Ryuk удаляет контейнеры преждевременно | Настройте `TESTCONTAINERS_RYUK_DISABLED` или увеличьте timeout |
| Тесты зависят от порядка выполнения | Каждый тест должен быть самодостаточным |

**Когда НЕ использовать `Testcontainers`:**
- Быстрые unit-тесты бизнес-логики — используйте моки
- Простые CRUD без специфики SQL-диалекта — `H2` может быть достаточно
- Среда без Docker (некоторые CI, корпоративные ограничения)
- Smoke-тесты в production — используйте реальную инфраструктуру

## Q28. Как использовать `ElasticsearchContainer` в тестах?

```java
@SpringBootTest
@Testcontainers
class ProductSearchRepositoryTest {

    @Container
    static ElasticsearchContainer elasticsearch =
        new ElasticsearchContainer(
            DockerImageName.parse("docker.elastic.co/elasticsearch/elasticsearch:8.11.0"))
        // Отключаем security для тестов
        .withEnv("xpack.security.enabled", "false")
        .withEnv("discovery.type", "single-node");

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.elasticsearch.uris",
            () -> "http://localhost:" + elasticsearch.getMappedPort(9200));
    }

    @Autowired
    private ProductSearchRepository searchRepository;

    @Autowired
    private ElasticsearchOperations elasticsearchOperations;

    @BeforeEach
    void setUp() {
        // Создаём индекс перед каждым тестом
        IndexOperations indexOps = elasticsearchOperations.indexOps(ProductDocument.class);
        if (!indexOps.exists()) {
            indexOps.createWithMapping();
        }
    }

    @AfterEach
    void tearDown() {
        // Очищаем данные
        elasticsearchOperations.indexOps(ProductDocument.class).delete();
    }

    @Test
    void shouldIndexAndSearchProducts() {
        // Given
        ProductDocument product = new ProductDocument("1", "Spring Boot in Action", "programming");
        searchRepository.save(product);

        // ElasticSearch асинхронно индексирует — делаем refresh
        elasticsearchOperations.indexOps(ProductDocument.class).refresh();

        // When
        List<ProductDocument> results = searchRepository.findByNameContaining("Spring");

        // Then
        assertThat(results).hasSize(1)
            .extracting(ProductDocument::getName)
            .containsExactly("Spring Boot in Action");
    }

    @Test
    void shouldSearchByCategory() {
        searchRepository.saveAll(List.of(
            new ProductDocument("1", "Spring Boot", "programming"),
            new ProductDocument("2", "Clean Code", "programming"),
            new ProductDocument("3", "Design Thinking", "management")
        ));
        elasticsearchOperations.indexOps(ProductDocument.class).refresh();

        List<ProductDocument> results = searchRepository.findByCategory("programming");

        assertThat(results).hasSize(2);
    }
}
```

### Spring Boot 3.1+ через `@ServiceConnection`

```java
@Container
@ServiceConnection
static ElasticsearchContainer elasticsearch =
    new ElasticsearchContainer("docker.elastic.co/elasticsearch/elasticsearch:8.11.0")
    .withEnv("xpack.security.enabled", "false");
// Автоматически настраивает spring.elasticsearch.uris
```

## Q29. Как тестировать отказоустойчивость с `Toxiproxy`?

**Toxiproxy** — прокси, который позволяет симулировать сетевые сбои: задержки, разрывы соединений, bandwidth-ограничения. В Testcontainers есть встроенный модуль.

```java
@SpringBootTest
@Testcontainers
class OrderServiceResilienceTest {

    @Container
    static ToxiproxyContainer toxiproxy = new ToxiproxyContainer("ghcr.io/shopify/toxiproxy:2.5.0")
        .withNetwork(network);

    @Container
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:16-alpine")
        .withNetwork(network)
        .withNetworkAliases("postgres");

    static Network network = Network.newNetwork();

    static ToxiproxyContainer.ContainerProxy postgresProxy;

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        postgresProxy = toxiproxy.getProxy(postgres, 5432);
        registry.add("spring.datasource.url",
            () -> "jdbc:postgresql://" + postgresProxy.getContainerIpAddress()
                  + ":" + postgresProxy.getProxyPort() + "/test");
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
    }

    @Autowired
    private OrderService orderService;

    @Test
    void shouldHandleDatabaseLatency() throws Exception {
        // Добавляем задержку 500мс на каждый пакет
        postgresProxy.toxics()
            .latency("db-latency", ToxicDirection.DOWNSTREAM, 500)
            .setJitter(100);

        // Сервис должен успешно работать с задержкой
        assertThatNoException().isThrownBy(() -> orderService.findAll());

        postgresProxy.toxics().get("db-latency").remove();
    }

    @Test
    void shouldHandleDatabaseConnectionCut() throws Exception {
        // Обрываем соединение
        postgresProxy.toxics()
            .bandwidth("bandwidth-limit", ToxicDirection.DOWNSTREAM, 0);

        // Сервис должен вернуть ошибку или задействовать circuit breaker
        assertThatThrownBy(() -> orderService.findAll())
            .isInstanceOf(DataAccessException.class);

        postgresProxy.toxics().get("bandwidth-limit").remove();
    }

    @Test
    void shouldRetryOnTransientFailure() throws Exception {
        // Слимитируем пропускную способность для первого запроса
        Toxic toxic = postgresProxy.toxics()
            .bandwidth("transient-failure", ToxicDirection.DOWNSTREAM, 0);

        // Через 1 секунду восстанавливаем
        CompletableFuture.runAsync(() -> {
            try {
                Thread.sleep(1000);
                toxic.remove();
            } catch (Exception ignored) {}
        });

        // @Retryable / Resilience4j должен повторить запрос успешно
        List<Order> orders = orderService.findAllWithRetry();
        assertThat(orders).isNotNull();
    }
}
```

## Q30. (!) Как использовать `LocalStack` для тестирования `AWS`-сервисов?

`LocalStack` эмулирует AWS-сервисы (S3, SQS, SNS, DynamoDB и др.) локально. Testcontainers предоставляет модуль `LocalStackContainer`.

```java
@SpringBootTest
@Testcontainers
class S3FileStorageServiceTest {

    @Container
    static LocalStackContainer localStack =
        new LocalStackContainer(DockerImageName.parse("localstack/localstack:3.0"))
            .withServices(Service.S3, Service.SQS);

    static S3Client s3Client;
    static SqsClient sqsClient;

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("aws.region", localStack::getRegion);
        registry.add("aws.accessKey", localStack::getAccessKey);
        registry.add("aws.secretKey", localStack::getSecretKey);
        registry.add("aws.s3.endpoint", () -> localStack.getEndpointOverride(Service.S3).toString());
        registry.add("aws.sqs.endpoint", () -> localStack.getEndpointOverride(Service.SQS).toString());
        registry.add("app.s3.bucket", () -> "test-bucket");
    }

    @BeforeAll
    static void setUp() {
        s3Client = S3Client.builder()
            .endpointOverride(localStack.getEndpointOverride(Service.S3))
            .credentialsProvider(StaticCredentialsProvider.create(
                AwsBasicCredentials.create(localStack.getAccessKey(), localStack.getSecretKey())))
            .region(Region.of(localStack.getRegion()))
            .build();

        // Создаём бакет для тестов
        s3Client.createBucket(b -> b.bucket("test-bucket"));
    }

    @Autowired
    private FileStorageService fileStorageService;

    @Test
    void shouldUploadFileToS3() throws Exception {
        byte[] content = "Test file content".getBytes(StandardCharsets.UTF_8);
        String key = fileStorageService.upload("documents/test.txt", content);

        // Проверяем, что файл реально в S3
        GetObjectResponse response = s3Client.getObject(b ->
            b.bucket("test-bucket").key(key)).response();

        assertThat(response.contentLength()).isEqualTo(content.length);
    }

    @Test
    void shouldPublishEventToSQS() throws Exception {
        sqsClient = SqsClient.builder()
            .endpointOverride(localStack.getEndpointOverride(Service.SQS))
            .credentialsProvider(StaticCredentialsProvider.create(
                AwsBasicCredentials.create(localStack.getAccessKey(), localStack.getSecretKey())))
            .region(Region.of(localStack.getRegion()))
            .build();

        String queueUrl = sqsClient.createQueue(b -> b.queueName("orders")).queueUrl();

        fileStorageService.uploadAndNotify("docs/file.pdf", "content".getBytes());

        // Проверяем сообщение в очереди
        ReceiveMessageResponse messages = sqsClient.receiveMessage(b ->
            b.queueUrl(queueUrl).maxNumberOfMessages(1));

        assertThat(messages.messages()).hasSize(1);
        assertThat(messages.messages().get(0).body()).contains("file.pdf");
    }
}
```

## Q31. Как тестировать с несколькими версиями одного сервиса?

Иногда нужно убедиться, что код работает с разными версиями БД или сервиса (например, при миграции).

```java
// Параметризованный тест с разными версиями PostgreSQL
@ParameterizedTest
@MethodSource("postgresVersions")
void shouldWorkWithAllSupportedPostgresVersions(String version) throws Exception {
    try (PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>(
            DockerImageName.parse("postgres:" + version))) {
        postgres.start();

        // Создаём DataSource для конкретной версии
        HikariConfig config = new HikariConfig();
        config.setJdbcUrl(postgres.getJdbcUrl());
        config.setUsername(postgres.getUsername());
        config.setPassword(postgres.getPassword());

        try (HikariDataSource dataSource = new HikariDataSource(config)) {
            // Запускаем Flyway миграции
            Flyway.configure()
                .dataSource(dataSource)
                .load()
                .migrate();

            // Проверяем совместимость через JDBC напрямую
            try (Connection conn = dataSource.getConnection();
                 PreparedStatement ps = conn.prepareStatement(
                     "SELECT version()")) {
                ResultSet rs = ps.executeQuery();
                rs.next();
                String dbVersion = rs.getString(1);
                assertThat(dbVersion).contains(version.split("\\.")[0]);
            }
        }
    }
}

static Stream<String> postgresVersions() {
    return Stream.of("13-alpine", "14-alpine", "15-alpine", "16-alpine");
}
```

## Q32. Как организовать базовый класс для интеграционных тестов с `Testcontainers`?

Базовый класс — ключевой паттерн для устранения дублирования настройки контейнеров.

```java
// Абстрактный базовый класс
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Testcontainers
public abstract class BaseIntegrationTest {

    // Singleton-контейнеры — запускаются один раз для всей тест-сессии
    @Container
    protected static PostgreSQLContainer<?> postgres =
        new PostgreSQLContainer<>("postgres:16-alpine")
            .withDatabaseName("testdb")
            .withUsername("test")
            .withPassword("test");

    @Container
    protected static KafkaContainer kafka =
        new KafkaContainer(DockerImageName.parse("confluentinc/cp-kafka:7.4.0"));

    @Container
    protected static GenericContainer<?> redis =
        new GenericContainer<>("redis:7-alpine")
            .withExposedPorts(6379);

    // Регистрация свойств один раз
    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);

        registry.add("spring.kafka.bootstrap-servers", kafka::getBootstrapServers);

        registry.add("spring.data.redis.host", redis::getHost);
        registry.add("spring.data.redis.port",
            () -> redis.getMappedPort(6379));
    }

    @Autowired
    protected MockMvc mockMvc;

    @Autowired
    protected ObjectMapper objectMapper;

    // Хелпер для выполнения аутентифицированных запросов
    protected MockHttpServletRequestBuilder authenticatedGet(String url) {
        return get(url).header("Authorization", "Bearer " + getTestToken());
    }

    protected MockHttpServletRequestBuilder authenticatedPost(String url, Object body)
            throws JsonProcessingException {
        return post(url)
            .header("Authorization", "Bearer " + getTestToken())
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(body));
    }

    private String getTestToken() {
        // Возвращаем JWT-токен для тестов
        return "test-jwt-token";
    }
}

// Конкретные тесты наследуют базовый класс
class OrderControllerTest extends BaseIntegrationTest {

    @Test
    void shouldCreateOrder() throws Exception {
        CreateOrderRequest request = new CreateOrderRequest(1L, BigDecimal.TEN);

        mockMvc.perform(authenticatedPost("/api/v1/orders", request))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.status").value("PENDING"));
    }
}

class ProductControllerTest extends BaseIntegrationTest {

    @Test
    void shouldGetProduct() throws Exception {
        mockMvc.perform(authenticatedGet("/api/v1/products/1"))
            .andExpect(status().isOk());
    }
}
```

Благодаря `static` полям с аннотацией `@Container`, контейнеры запускаются один раз и переиспользуются всеми подклассами — это эквивалент Singleton Container паттерна, но организованный через наследование.

Подробнее — в [[testcontainers-interview|Q19: Singleton Containers паттерн]].

**На собеседовании** стоит показать, что вы понимаете trade-off: `Testcontainers` даёт уверенность в интеграции ценой скорости и инфраструктурных требований. Хороший инженер знает, где провести границу между unit и интеграционными тестами (подробнее в [[test-strategies-interview|Стратегии тестирования]]).

---

## Q33. Что такое Ryuk и как он управляет cleanup контейнеров?

**Ryuk** — вспомогательный контейнер, который Testcontainers автоматически запускает перед первым тестом. Его задача — гарантировать очистку всех Docker-ресурсов (контейнеры, сети, volumes), созданных во время тестов, даже если JVM завершится аварийно.

**Как работает:**

```
JVM запускает тест
    ↓
Testcontainers запускает Ryuk-контейнер (ryuk:0.x)
    ↓
Ryuk слушает TCP-соединение от JVM
    ↓
Testcontainers создаёт контейнеры с label "org.testcontainers=true"
    ↓
Тест завершается (нормально или аварийно)
    ↓
Ryuk обнаруживает разрыв TCP-соединения (JVM умерла)
    ↓
Ryuk удаляет все контейнеры с label "org.testcontainers=true"
```

**Зачем нужен:** без Ryuk при `kill -9` JVM (crash, OOM killer) контейнеры останутся висеть — засоряют ресурсы CI-агента и могут конфликтовать со следующими запусками.

**Конфигурация:**

```java
// Отключить Ryuk (например, в Kubernetes где контейнеры и так изолированы)
// В application.properties или системная переменная:
// TESTCONTAINERS_RYUK_DISABLED=true

// Или программно:
System.setProperty("TESTCONTAINERS_RYUK_DISABLED", "true");

// Изменить образ Ryuk (в testcontainers.properties):
// ryuk.container.image=registry.company.com/testcontainers/ryuk:0.7.0
```

**Когда отключать Ryuk:**
- В Kubernetes Pod'ах (инфраструктура CI изолирована)
- В средах без Docker daemon (Testcontainers Cloud)
- При использовании `reusable containers` (`withReuse(true)`) — Ryuk мешает переиспользованию

**Мониторинг:** Ryuk логирует свою работу. При проблемах с cleanup можно смотреть логи контейнера `testcontainers-ryuk-*`.

## Q34. Как использовать `WireMock` как Testcontainer для моков внешних HTTP API?

**WireMock** в виде Testcontainer позволяет поднять настоящий HTTP-сервер для мока внешних API — в отличие от `MockRestServiceServer`, WireMock работает на реальном сетевом уровне и тестирует HTTP-клиент полностью.

**Подключение:**

```xml
<!-- pom.xml -->
<dependency>
    <groupId>org.wiremock.integrations.testcontainers</groupId>
    <artifactId>wiremock-testcontainers-module</artifactId>
    <version>1.0-alpha-13</version>
    <scope>test</scope>
</dependency>
```

**Использование:**

```java
@Testcontainers
@SpringBootTest
class PaymentGatewayTest {

    @Container
    static WireMockContainer wireMock = new WireMockContainer("wiremock/wiremock:3.3.1")
        .withMappingFromResource("payment-stub.json");  // файл из test/resources

    @DynamicPropertySource
    static void configure(DynamicPropertyRegistry registry) {
        registry.add("payment.gateway.url", wireMock::getBaseUrl);
    }

    @Test
    void shouldProcessPayment() throws Exception {
        wireMock.stubFor(post(urlEqualTo("/charge"))
            .willReturn(aResponse()
                .withStatus(200)
                .withBody("""{"transactionId": "tx-123", "status": "SUCCESS"}""")
                .withHeader("Content-Type", "application/json")));

        PaymentResult result = paymentService.charge(order);

        assertThat(result.getTransactionId()).isEqualTo("tx-123");
        wireMock.verify(postRequestedFor(urlEqualTo("/charge"))
            .withRequestBody(containing("orderId")));
    }
}
```

**Стабы из файлов (test/resources/mappings/):**

```json
// payment-stub.json
{
    "request": { "method": "POST", "url": "/charge" },
    "response": {
        "status": 200,
        "body": "{\"status\": \"SUCCESS\"}",
        "headers": { "Content-Type": "application/json" }
    }
}
```

**Преимущества перед MockMvc/MockRestServiceServer:**
- Тестирует реальный HTTP-клиент (RestTemplate, WebClient, Feign, OkHttp)
- Работает при `@SpringBootTest(webEnvironment = RANDOM_PORT)`
- Тестирует retry-логику, timeout, заголовки, сжатие
- Поддержка сценариев (stateful stubs)

## Q35. Что такое `Testcontainers Cloud` и когда его использовать?

**Testcontainers Cloud** — managed-сервис от компании Testcontainers, который выполняет контейнеры удалённо, а не на локальной машине или CI-агенте. JVM-тест «видит» контейнер как обычный Testcontainer, но реально он работает в облаке.

**Архитектура:**

```
CI-агент (без Docker!)          Testcontainers Cloud
┌─────────────────────┐         ┌───────────────────────┐
│  JVM-тест           │ ←────→  │  Docker daemon         │
│  Testcontainers API │  TCP    │  PostgreSQL container  │
│  (без docker.sock)  │         │  Kafka container       │
└─────────────────────┘         └───────────────────────┘
```

**Настройка:**

```bash
# Установить Testcontainers Cloud Agent на CI-агент
# Или задать env vars:
TC_CLOUD_TOKEN=your-token
TC_CLOUD_CONCURRENCY=4  # параллельных потоков
```

```java
// Код тестов не меняется! Прозрачная замена
@Container
static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:16");
// Работает и локально (docker), и в TC Cloud (remote)
```

**Когда использовать:**
- CI-агенты без Docker (GitHub Actions с ограничениями, serverless runners)
- Docker-in-Docker вызывает проблемы (Kubernetes CI, GitLab Shared Runners)
- Нужно масштабировать параллельные тесты на много агентов
- Корпоративные ограничения на Docker на рабочих машинах

**Когда НЕ нужен:**
- Локальная разработка с установленным Docker
- CI с полным доступом к Docker daemon
- Проекты с небольшим числом контейнерных тестов (overhead не оправдан)

**Цена:** платный сервис, есть free tier для OSS-проектов.

---

## Q36. Как использовать `DockerComposeContainer` — плюсы и минусы?

**`DockerComposeContainer`** позволяет запускать несколько сервисов из `docker-compose.yml` вместо описания каждого контейнера вручную.

```yaml
# src/test/resources/docker-compose-test.yml
version: '3.8'
services:
  postgres:
    image: postgres:16
    environment:
      POSTGRES_DB: testdb
      POSTGRES_USER: test
      POSTGRES_PASSWORD: test
  redis:
    image: redis:7-alpine
  kafka:
    image: confluentinc/cp-kafka:7.5.0
    environment:
      KAFKA_BROKER_ID: 1
      KAFKA_ZOOKEEPER_CONNECT: zookeeper:2181
  zookeeper:
    image: confluentinc/cp-zookeeper:7.5.0
```

```java
@Testcontainers
class IntegrationTest {

    @Container
    static DockerComposeContainer<?> compose =
        new DockerComposeContainer<>(
            new File("src/test/resources/docker-compose-test.yml"))
        .withExposedService("postgres", 5432,
            Wait.forListeningPort().withStartupTimeout(Duration.ofSeconds(30)))
        .withExposedService("redis", 6379, Wait.forListeningPort())
        .withLocalCompose(true);  // использовать локальный docker-compose

    @DynamicPropertySource
    static void configure(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", () ->
            "jdbc:postgresql://" + compose.getServiceHost("postgres", 5432) +
            ":" + compose.getServicePort("postgres", 5432) + "/testdb");
    }
}
```

**Плюсы:**
- Переиспользование существующих `docker-compose.yml` из проекта
- Простота описания сложных топологий (несколько зависимых сервисов)
- Знакомый формат для DevOps-команды

**Минусы:**
- Медленнее старта (docker-compose overhead)
- Зависимость от установленного `docker-compose` или Docker Compose V2 (`--with-local-compose`)
- Нет type-safe API для конфигурации — всё через строки
- `waitingFor` менее гибкий, чем у `GenericContainer`
- Сложнее debuggability — что именно не стартануло?
- **Рекомендация:** предпочитать отдельные контейнеры через специализированные классы (`PostgreSQLContainer`, `KafkaContainer`) для лучшего контроля и диагностики

## Q37. Как запускать `Testcontainers`-тесты параллельно без конфликтов?

**Параллельный запуск** с Testcontainers работает хорошо при правильной архитектуре, но требует внимания к изоляции.

**JUnit 5 параллельный запуск:**

```properties
# src/test/resources/junit-platform.properties
junit.jupiter.execution.parallel.enabled=true
junit.jupiter.execution.parallel.mode.default=concurrent
junit.jupiter.execution.parallel.mode.classes.default=concurrent
junit.jupiter.execution.parallel.config.strategy=dynamic
junit.jupiter.execution.parallel.config.dynamic.factor=2
```

**Стратегия 1: Singleton Container (рекомендуемая):**

```java
// Один контейнер на весь тестовый прогон — никаких конфликтов
abstract class BaseIntegrationTest {
    companion object {
        @JvmField
        @Container  // static — один на все подклассы
        val postgres: PostgreSQLContainer<*> = PostgreSQLContainer("postgres:16")
            .apply { start() }
    }
}

// Тесты параллельны, но работают с одним контейнером
// Изоляция — через транзакции (@Transactional @Rollback)
// или через очистку таблиц в @BeforeEach
```

**Стратегия 2: Отдельный контейнер на каждый тест-класс:**

```java
@Testcontainers
class OrderServiceTest {
    @Container
    // НЕ static — отдельный контейнер на каждый класс
    PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:16");
    // При параллельном запуске классов — каждый имеет свой контейнер
    // Дороже по ресурсам, но полная изоляция
}
```

**Проблемы при параллельном запуске:**
- Исчерпание портов — Testcontainers использует random ports, обычно безопасно
- Исчерпание Docker ресурсов (memory, CPU) — ограничивайте `concurrency`
- Конкурентный доступ к shared state в БД — решать через schema-per-test или transactions

```java
// Ограничение параллельности для тяжёлых контейнерных тестов
@ResourceLock("postgres-container")  // JUnit 5 resource lock
class HeavyDatabaseTest { }
```

---

## Q38. Как Testcontainers используется в Kotlin-проектах — DSL и особенности?

**Kotlin-специфика Testcontainers:** официальная поддержка Kotlin идёт через те же Java-API, но существуют расширения и идиомы, делающие код чище.

**`testcontainers-kotlin` — неофициальная Kotlin DSL:**

```kotlin
// Обычный Java-стиль (работает в Kotlin)
@Container
val postgres = PostgreSQLContainer<Nothing>("postgres:16").apply {
    withDatabaseName("testdb")
    withUsername("test")
    withPassword("test")
}

// Companion object для singleton-паттерна
companion object {
    @JvmField
    @Container
    val postgres: PostgreSQLContainer<*> = PostgreSQLContainer("postgres:16")
        .also { it.start() }
}
```

**Kotlin Extension Functions для Testcontainers:**

```kotlin
// Своё расширение для удобного @DynamicPropertySource
fun PostgreSQLContainer<*>.toDataSourceProperties(): Map<String, String> = mapOf(
    "spring.datasource.url" to jdbcUrl,
    "spring.datasource.username" to username,
    "spring.datasource.password" to password,
    "spring.datasource.driver-class-name" to driverClassName
)

@DynamicPropertySource
fun configureProperties(registry: DynamicPropertyRegistry) {
    postgres.toDataSourceProperties().forEach { (key, value) ->
        registry.add(key) { value }
    }
}
```

**Kotlin корутины + Testcontainers:**

```kotlin
@SpringBootTest
@Testcontainers
class CoroutineIntegrationTest {

    @Container
    companion object {
        @JvmField
        val redis: GenericContainer<*> = GenericContainer("redis:7-alpine")
            .withExposedPorts(6379)
    }

    @Test
    fun `should store and retrieve data with coroutines`() = runTest {
        val client = createRedisClient()
        client.set("key", "value")
        assertThat(client.get("key")).isEqualTo("value")
    }
}
```

**Особенности в Kotlin:**
- `Nothing` как generic параметр: `PostgreSQLContainer<Nothing>` вместо `PostgreSQLContainer<?>`
- `@JvmField` обязателен для `@Container` на `companion object`-полях — JUnit 5 Extension читает поля через reflection и видит только Java fields
- `apply { }` и `also { }` — идиоматичная конфигурация контейнеров

## Q39. Что такое `@ServiceConnection` в Spring Boot 3.1+ и как он работает?

**`@ServiceConnection`** — аннотация Spring Boot 3.1+, которая автоматически конфигурирует Spring-бины (DataSource, RedisConnectionFactory, KafkaProducerFactory и т.д.) на основе запущенного Testcontainers-контейнера. Устраняет необходимость в `@DynamicPropertySource`.

**Без `@ServiceConnection` (Spring Boot < 3.1):**

```java
@Container
static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:16");

@DynamicPropertySource
static void configure(DynamicPropertyRegistry registry) {
    registry.add("spring.datasource.url", postgres::getJdbcUrl);
    registry.add("spring.datasource.username", postgres::getUsername);
    registry.add("spring.datasource.password", postgres::getPassword);
}
```

**С `@ServiceConnection` (Spring Boot 3.1+):**

```java
@Container
@ServiceConnection  // Всё! DataSource настроится автоматически
static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:16");
```

**Поддерживаемые контейнеры из коробки:**

| Контейнер | Что настраивает |
|-----------|----------------|
| `PostgreSQLContainer` | `DataSource` |
| `MySQLContainer` | `DataSource` |
| `RedisContainer` | `RedisConnectionFactory` |
| `KafkaContainer` | `KafkaProducerFactory`, `KafkaConsumerFactory` |
| `MongoDBContainer` | `MongoClient` |
| `ElasticsearchContainer` | `ElasticsearchClient` |
| `RabbitMQContainer` | `ConnectionFactory` |

**`@ServiceConnection` в `@TestConfiguration`:**

```java
@TestConfiguration(proxyBeanMethods = false)
class TestcontainersConfiguration {

    @Bean
    @ServiceConnection
    PostgreSQLContainer<?> postgresContainer() {
        return new PostgreSQLContainer<>(DockerImageName.parse("postgres:16"));
    }

    @Bean
    @ServiceConnection
    RedisContainer redisContainer() {
        return new RedisContainer(DockerImageName.parse("redis:7-alpine"));
    }
}
```

```java
// Импорт в тест или в TestApplication
@SpringBootTest
@Import(TestcontainersConfiguration.class)
class MyIntegrationTest { }
```

**Кастомный `ConnectionDetailsFactory`** — для нестандартных контейнеров можно реализовать свой:

```java
@SpringBootConfiguration
public class MyCustomConnectionFactory
    implements ConnectionDetailsFactory<GenericContainer<?>, MyConnectionDetails> {

    @Override
    public MyConnectionDetails getConnectionDetails(GenericContainer<?> container) {
        return new MyConnectionDetails(container.getHost(), container.getMappedPort(1234));
    }
}
```

## Q40. Как `LocalStack` используется для тестирования AWS-сервисов локально?

**LocalStack** — эмулятор AWS API, работающий как Docker-контейнер. Testcontainers предоставляет `LocalStackContainer` с готовым API для его запуска и конфигурации.

**Подключение:**

```xml
<dependency>
    <groupId>org.testcontainers</groupId>
    <artifactId>localstack</artifactId>
    <scope>test</scope>
</dependency>
```

**Базовое использование:**

```java
@Testcontainers
@SpringBootTest
class S3ServiceTest {

    @Container
    static LocalStackContainer localStack =
        new LocalStackContainer(DockerImageName.parse("localstack/localstack:3.0"))
            .withServices(Service.S3, Service.SQS, Service.DYNAMODB);

    static S3Client s3Client;

    @BeforeAll
    static void setup() {
        s3Client = S3Client.builder()
            .endpointOverride(localStack.getEndpointOverride(Service.S3))
            .credentialsProvider(StaticCredentialsProvider.create(
                AwsBasicCredentials.create(
                    localStack.getAccessKey(),
                    localStack.getSecretKey())))
            .region(Region.of(localStack.getRegion()))
            .build();

        s3Client.createBucket(b -> b.bucket("test-bucket"));
    }

    @Test
    void shouldUploadAndDownloadFile() throws Exception {
        byte[] content = "Hello, LocalStack!".getBytes();
        s3Client.putObject(
            b -> b.bucket("test-bucket").key("test.txt"),
            RequestBody.fromBytes(content));

        ResponseBytes<GetObjectResponse> response = s3Client.getObjectAsBytes(
            b -> b.bucket("test-bucket").key("test.txt"));

        assertThat(response.asByteArray()).isEqualTo(content);
    }
}
```

**Интеграция со Spring Boot через `@ServiceConnection`:**

```java
@Container
@ServiceConnection
static LocalStackContainer localStack =
    new LocalStackContainer(DockerImageName.parse("localstack/localstack:3.0"))
        .withServices(Service.S3, Service.SQS);
// Spring Auto-configuration настроит AmazonS3, AmazonSQS автоматически
```

**SQS-тестирование:**

```java
@Test
void shouldSendAndReceiveMessage() {
    SqsClient sqs = SqsClient.builder()
        .endpointOverride(localStack.getEndpointOverride(Service.SQS))
        .credentialsProvider(StaticCredentialsProvider.create(
            AwsBasicCredentials.create(localStack.getAccessKey(), localStack.getSecretKey())))
        .region(Region.of(localStack.getRegion()))
        .build();

    String queueUrl = sqs.createQueue(b -> b.queueName("test-queue")).queueUrl();
    sqs.sendMessage(b -> b.queueUrl(queueUrl).messageBody("test message"));

    ReceiveMessageResponse response = sqs.receiveMessage(b -> b.queueUrl(queueUrl));
    assertThat(response.messages()).hasSize(1);
    assertThat(response.messages().get(0).body()).isEqualTo("test message");
}
```

**Поддерживаемые сервисы LocalStack:**
- S3, SQS, SNS, DynamoDB, Lambda, CloudWatch, Secrets Manager, IAM, Kinesis, Step Functions

**Ограничения LocalStack Free:**
- Часть сервисов (Lambda с layers, некоторые RDS-функции) — только в Pro
- Производительность ниже реального AWS
- Не 100% совместимость API для сложных сценариев

---

## See also

- [[integration-testing-interview|Integration Testing]] — интеграционное тестирование в Spring Boot: `@SpringBootTest`, test slices
- [[unit-testing-interview|Unit Testing]] — модульные тесты и моки: когда Testcontainers не нужен
- [[test-strategies-interview|Стратегии тестирования]] — пирамида тестов и место Testcontainers в ней
- [[test-automation-interview|Test Automation]] — автоматизация и запуск тестов с контейнерами в CI/CD
- [[spring-data-jpa-interview|Spring Data JPA]] — тестирование репозиториев с `@DataJpaTest` + Testcontainers
- [[spring-boot-interview|Spring Boot]] — конфигурация, auto-configuration, профили
- [[docker-interview|Docker]] — контейнеризация: образы, сети, volumes, которые использует Testcontainers
- [[kubernetes-interview|Kubernetes]] — запуск тестов с Testcontainers в Kubernetes CI runner'ах

- [[chaos-engineering-interview|Chaos Engineering]]
- [[contract-testing-interview|Contract Testing]]
- [[integration-testing-interview|Integration Testing]]
- [[load-testing-interview|Load Testing]]
- [[mockito-interview|Mockito]]
- [[mutation-testing-interview|Mutation Testing]]
