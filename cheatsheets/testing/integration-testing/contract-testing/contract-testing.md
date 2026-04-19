---
title: "Контрактное тестирование (Contract Testing)"
description: "Проверка совместимости между потребителем (consumer) и провайдером (provider) API по формальному контракту — без поднятия всех сервисов вместе. Основные инструменты: Pact, Spring Cloud Contract."
tags:
  - testing
  - integration-testing
  - contract-testing
difficulty: "intermediate"
prerequisites: []
next: []
updated: "2026-02-11"
---
# Контрактное тестирование (`Contract Testing`)

Проверка совместимости между потребителем (consumer) и провайдером (provider) API по формальному контракту — без поднятия всех сервисов вместе. Основные инструменты: `Pact`, `Spring Cloud Contract`.

## Полезные ссылки

- [Pact — Official](https://pact.io/) | [Pact Docs](https://docs.pact.io/)
- [Pact JVM — GitHub](https://github.com/pact-foundation/pact-jvm)
- [Spring Cloud Contract](https://spring.io/projects/spring-cloud-contract)

### См. также

- [REST Assured](../rest-assured.md)
- [WireMock](../wiremock.md)
- [Testcontainers](../testcontainers/testcontainers.md)

---

## Содержание

- [Зачем контрактное тестирование](#зачем-контрактное-тестирование)
- [Основные концепции](#основные-концепции)
- [Pact: подключение и примеры](#pact-подключение-и-примеры)
- [Spring Cloud Contract: подключение и примеры](#spring-cloud-contract-подключение-и-примеры)
- [CI/CD и Pact Broker](#cicd-и-pact-broker)
- [Лучшие практики](#лучшие-практики)
- [Решение проблем](#решение-проблем)
- [Частые вопросы](#частые-вопросы)
- [Итоговые таблицы](#итоговые-таблицы)

---

## Зачем контрактное тестирование

- **Независимое развёртывание** — consumer и provider деплоятся отдельно; контракт гарантирует совместимость.
- **Раннее обнаружение поломок** — при изменении API тест падает до интеграции в общем окружении.
- **Изоляция** — consumer-тест не требует работающего provider (работает против мока); provider-тест запускается локально.
- **Живая документация** — контракт описывает реальные взаимодействия, а не просто схему.
- **Для микросервисов** — типичная ситуация: много потребителей, один провайдер.

---

## Основные концепции

| Понятие | Описание |
|---------|----------|
| **Consumer** | Сервис-клиент, вызывающий API |
| **Provider** | Сервис-сервер, предоставляющий API |
| **Contract** | Формальное описание взаимодействия: метод, путь, заголовки, тело запроса/ответа |
| **Consumer-driven** | Контракт задаётся потребителем — провайдер обязан ему соответствовать |
| **Pact** | Формат и инструменты для consumer-driven контрактов |
| **Spring Cloud Contract** | Контракты у провайдера (Groovy/YAML), стабы генерируются для потребителя |
| **Pact Broker** | Хранилище контрактов с версионированием, `can-i-deploy` |
| **Stub** | Мок-сервер, отдающий ответы по контракту |

---

## Pact: подключение и примеры

### Maven

```xml
<dependency>
    <groupId>au.com.dius.pact.consumer</groupId>
    <artifactId>junit5</artifactId>
    <version>4.6.5</version>
    <scope>test</scope>
</dependency>
<dependency>
    <groupId>au.com.dius.pact.provider</groupId>
    <artifactId>junit5</artifactId>
    <version>4.6.5</version>
    <scope>test</scope>
</dependency>
```

### Consumer-тест

Consumer пишет тест против Pact mock server. После прогона генерируется JSON-контракт (`target/pacts/`).

```java
@ExtendWith(PactConsumerTestExt.class)
@PactTestFor(providerName = "UserService", port = "0")
class UserServiceContractConsumerTest {

    @Pact(consumer = "OrderService")
    V4Pact userByIdPact(PactDslWithProvider builder) {
        return builder
                .given("user 1 exists")
                .uponReceiving("request for user by id")
                .path("/users/1")
                .method("GET")
                .willRespondWith()
                .status(200)
                .body(new PactDslJsonBody()
                    .numberType("id", 1)
                    .stringType("name", "Alice"))
                .toPact(V4Pact.class);
    }

    @Test
    @PactTestFor(pactMethod = "userByIdPact")
    void getUserById(MockServer mockServer) {
        String response = new RestTemplate().getForObject(
            mockServer.getUrl() + "/users/1", String.class);
        assertThat(response).contains("Alice");
    }
}
```

### Provider-верификация

Provider поднимает своё приложение и проверяет, что каждое взаимодействие из контракта даёт ожидаемый ответ.

```java
@Provider("UserService")
@PactFolder("pacts")  // или @PactBroker(host = "broker.example.com")
@SpringBootTest
@AutoConfigureMockMvc
class UserServiceContractProviderTest {

    @Autowired MockMvc mockMvc;

    @TestTemplate
    @ExtendWith(PactVerificationInvocationContextProvider.class)
    void verifyPact(PactVerificationContext context) {
        context.setTarget(new MockMvcTestTarget(mockMvc));
        context.verifyInteraction();
    }

    @BeforeEach
    void before(PactVerificationContext context) {
        context.setTarget(new MockMvcTestTarget(mockMvc));
    }
}
```

### Pact matchers (гибкое сопоставление)

| Matcher | Описание |
|---------|----------|
| `stringType("name", "ex")` | Строка любого значения |
| `numberType("id", 1)` | Число любого значения |
| `booleanType("active", true)` | Булево |
| `eachLike("items", body)` | Массив с минимум одним элементом |
| `minArrayLike("items", 0)` | Массив (может быть пустым) |
| `regex("field", "[0-9]+", "123")` | Строка по regex |
| `uuid("id")` | UUID |

---

## Spring Cloud Contract: подключение и примеры

### Maven (provider)

```xml
<dependency>
    <groupId>org.springframework.cloud</groupId>
    <artifactId>spring-cloud-starter-contract-verifier</artifactId>
    <scope>test</scope>
</dependency>
```

Плагин генерирует тесты из контрактов:

```xml
<plugin>
    <groupId>org.springframework.cloud</groupId>
    <artifactId>spring-cloud-contract-maven-plugin</artifactId>
    <version>4.1.0</version>
    <extensions>true</extensions>
    <configuration>
        <testFramework>JUNIT5</testFramework>
        <baseClassForTests>com.example.ProviderBaseTest</baseClassForTests>
    </configuration>
</plugin>
```

### Контракт (Groovy)

Контракты лежат в `src/test/resources/contracts`:

```groovy
Contract.make {
    description "get user by id"
    request {
        method GET()
        url("/users/1")
    }
    response {
        status 200
        body([id: 1, name: "Alice"])
        headers { contentType(applicationJson()) }
    }
}
```

### Контракт (YAML)

```yaml
description: get user by id
request:
  method: GET
  url: /users/1
response:
  status: 200
  body:
    id: 1
    name: Alice
  headers:
    Content-Type: application/json
```

### Consumer (Stub Runner)

Consumer подключает `spring-cloud-starter-contract-stub-runner`. Stub-сервер (WireMock) запускается автоматически:

```java
@SpringBootTest
@AutoConfigureStubRunner(
    ids = {"com.example:user-service:+:stubs:8081"},
    stubsMode = StubRunnerProperties.StubsMode.LOCAL)
class OrderServiceContractTest {

    @Test
    void shouldGetUserFromStub() {
        String response = new RestTemplate().getForObject(
            "http://localhost:8081/users/1", String.class);
        assertThat(response).contains("Alice");
    }
}
```

---

## CI/CD и Pact Broker

### Pact Broker

Централизованное хранилище контрактов. Поддерживает:
- **Версионирование** — каждый контракт привязан к версии consumer/provider
- **Теги** — `prod`, `staging`, `feat-xyz`
- **Can-i-deploy** — проверка: «можно ли задеплоить provider v1.2.3 в prod?» Брокер смотрит, все ли потребители в prod верифицировали этот контракт

### Пайплайн

```
Consumer CI:
  тесты → генерация контракта → публикация в Pact Broker

Provider CI:
  загрузка контрактов из Pact Broker → верификация → публикация результата

Перед деплоем:
  can-i-deploy → если все потребители OK → деплой разрешён
```

### Публикация контракта

```bash
mvn pact:publish -Dpact.broker.baseUrl=https://broker.example.com
```

### Spring Cloud Contract (без Pact Broker)

Стабы публикуются как Maven-артефакт (stub jar). Consumer подключает stub runner с координатами артефакта.

---

## Лучшие практики

1. **Один контракт — одно взаимодействие.** Проще поддерживать и читать.
2. **Минимум полей в контракте** — только то, что consumer реально использует. Provider может добавлять поля, но не удалять ожидаемые.
3. **Версионирование** — теги в Pact Broker; `can-i-deploy` перед деплоем.
4. **State handlers** — provider должен подготовить состояние (given) перед верификацией.
5. **Не дублировать E2E** — контрактные тесты не заменяют интеграционные, но сокращают количество тяжёлых сценариев.
6. **Согласование имён** — имена consumer/provider в контракте должны совпадать с именами сервисов.

---

## Решение проблем

| Проблема | Причина | Решение |
|----------|---------|---------|
| Consumer-тест падает на mock server | Неверный путь/метод в контракте | Сверить с реальным вызовом клиента |
| Provider-верификация не находит контракт | Путь к папке/брокеру неверный | Проверить `@PactFolder` / `@PactBroker` |
| Несовпадение тела ответа | Изменилась схема у provider | Обновить контракт у consumer или поправить provider |
| Stub Runner не стартует | Нет артефакта со стабами | Проверить координаты (groupId/artifactId/version) |
| SCC не генерирует тесты | Неверный базовый класс | Проверить `baseClassForTests` в плагине |
| Pact Broker → 401 | Нет токена | Настроить `PACT_BROKER_TOKEN` |
| `can-i-deploy` блокирует | Не все потребители верифицировали | Запустить верификацию от всех потребителей |

---

## Частые вопросы

**Чем Pact отличается от Spring Cloud Contract?**
`Pact` — consumer-driven: контракт в коде consumer. `SCC` — контракты в Groovy/YAML у provider; стабы генерируются для consumer. Оба решают задачу совместимости; выбор зависит от предпочтений и экосистемы.

**Нужен ли Pact Broker?**
Не обязательно — контракты можно передавать файлами. Брокер удобен при многих потребителях и CI/CD (`can-i-deploy`, версии, теги).

**Контрактное тестирование заменяет E2E?**
Нет. Контракт проверяет совместимость API. E2E проверяет полный сценарий. Контрактные тесты сокращают количество E2E и ловят поломки раньше.

**А для сообщений (Kafka, RabbitMQ)?**
Pact и SCC поддерживают message contracts — контракт для тела и заголовков сообщения.

---

## Итоговые таблицы

### Pact vs Spring Cloud Contract

| Аспект | Pact | Spring Cloud Contract |
|--------|------|------------------------|
| Контракт пишет | Consumer | Provider |
| Consumer-тест | Против Pact mock server | Против Stub Runner (WireMock) |
| Provider-тест | Верификация по JSON | Генерация тестов из контрактов |
| Хранилище | Pact Broker или файлы | Maven/Artifactory (stub jar) |
| Can-i-deploy | Да (Pact Broker) | Нет (ручная проверка) |
| Мультиязычность | Да (Pact JS, Go и др.) | Только JVM |
| Message contracts | Да | Да |

### Pact JVM: основные аннотации

| Аннотация | Назначение |
|-----------|------------|
| `@Pact(consumer = "X")` | Определение контракта |
| `@PactTestFor(providerName = "Y")` | Pact mock server |
| `@PactFolder("path")` | Папка с JSON-контрактами |
| `@PactBroker(host = "...")` | Загрузка из Pact Broker |
| `@Provider("Y")` | Имя provider для верификации |
| `@TestTemplate` | Запуск верификации |

### SCC: ключевые компоненты

| Компонент | Назначение |
|-----------|------------|
| `spring-cloud-starter-contract-verifier` | Генерация тестов для provider |
| `spring-cloud-starter-contract-stub-runner` | Запуск стабов для consumer |
| `spring-cloud-contract-maven-plugin` | Генерация тестов и stub jar |
| `baseClassForTests` | Базовый класс тестов (MockMvc/RestAssured) |
| `src/test/resources/contracts` | Папка с контрактами по умолчанию |

---

