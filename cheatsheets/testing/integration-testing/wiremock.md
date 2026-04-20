---
title: "WireMock для Java"
description: "Краткое руководство по использованию WireMock для мокирования HTTP-сервисов в Java-тестах: standalone-сервер, интеграция с JUnit, сопоставление запросов, шаблоны ответов, stateful-сценарии и интеграция со Spring Boot."
tags:
  - testing
  - integration-testing
  - wiremock
difficulty: "intermediate"
prerequisites: []
next: []
updated: "2026-04-20"
---
# WireMock для Java

Краткое руководство по использованию **WireMock** для мокирования HTTP-сервисов в Java-тестах: standalone-сервер, интеграция с JUnit, сопоставление запросов, шаблоны ответов, stateful-сценарии и интеграция со Spring Boot.

## Полезные ссылки

| Раздел | Ссылка |
|--------|--------|
| Документация | [WireMock Docs](https://wiremock.org/docs/) |
| JUnit 5 | [WireMock JUnit Jupiter](https://wiremock.org/docs/junit-jupiter/) |
| Spring Boot | [WireMock Spring Boot](https://wiremock.org/docs/spring-boot/) |
| Java API | [WireMock Java](https://wiremock.org/docs/java-usage/) |
| Stateful | [Stateful Behaviour](https://wiremock.org/docs/stateful-behaviour/) |

**См. также:** [[junit-advanced|JUnit Advanced]], [[mockito-advanced|Mockito Advanced]], [[spring-testing|Spring Testing]], [[rest-assured|REST Assured]].


## Содержание

- [Введение в WireMock](#введение-в-wiremock)
- [Зависимости](#зависимости)
  - [Maven](#maven)
  - [Gradle](#gradle)
- [Запуск (standalone, Docker)](#запуск-standalone-docker)
- [JUnit 5](#junit-5)
- [Базовый stubbing](#базовый-stubbing)
- [Сопоставление запросов](#сопоставление-запросов)
- [Шаблоны ответов](#шаблоны-ответов)
- [Stateful-сценарии](#stateful-сценарии)
- [Spring Boot](#spring-boot)
- [Продвинутые сценарии](#продвинутые-сценарии)
- [Рекомендации](#рекомендации)
- [Решение проблем](#решение-проблем)
  - [Конфликт портов](#конфликт-портов)
  - [Запрос возвращает 404, стаб не срабатывает](#запрос-возвращает-404-стаб-не-срабатывает)
  - [Стабы из предыдущего теста влияют на текущий](#стабы-из-предыдущего-теста-влияют-на-текущий)
  - [Шаблоны в ответе не подставляются](#шаблоны-в-ответе-не-подставляются)
  - [Конфликт версий WireMock (например, со Spring Boot)](#конфликт-версий-wiremock-например-со-spring-boot)
- [Заключение](#заключение)
- [См. также](#см-также)

## Введение в WireMock

**WireMock** — HTTP mock-сервер для тестирования HTTP-клиентов. Позволяет создавать моковые HTTP endpoint'ы, имитирующие реальные API, внешние системы и микросервисы.

**Зачем использовать:**

- Полная имитация HTTP-серверов (методы, заголовки, тело, задержки).
- Гибкое сопоставление запросов по URL, заголовкам, телу, query-параметрам.
- Динамические ответы через шаблоны (Handlebars).
- Поддержка сценариев с изменением состояния (stateful).
- Запись реальных взаимодействий и воспроизведение.
- Интеграция с JUnit 5 и Spring Boot.


## Зависимости

### Maven

**Обязательно (ядро):**

```xml
<dependency>
    <groupId>com.github.tomakehurst</groupId>
    <artifactId>wiremock-jre8</artifactId>
    <version>2.35.0</version>
    <scope>test</scope>
</dependency>
```

**JUnit 5:**

```xml
<dependency>
    <groupId>com.github.tomakehurst</groupId>
    <artifactId>wiremock-junit5</artifactId>
    <version>2.35.0</version>
    <scope>test</scope>
</dependency>
```

**Spring Boot:**

```xml
<dependency>
    <groupId>com.github.tomakehurst</groupId>
    <artifactId>wiremock-spring-boot</artifactId>
    <version>2.35.0</version>
    <scope>test</scope>
</dependency>
```

### Gradle

```gradle
testImplementation 'com.github.tomakehurst:wiremock-jre8:2.35.0'
testImplementation 'com.github.tomakehurst:wiremock-junit5:2.35.0'
testImplementation 'com.github.tomakehurst:wiremock-spring-boot:2.35.0'
```


## Запуск (standalone, Docker)

**Standalone JAR:**

```bash
java -jar wiremock-standalone-2.35.0.jar --port 8089 --verbose
java -jar wiremock-standalone-2.35.0.jar --port 8089 --root-dir /path/to/stubs
```

**Docker:**

```bash
docker run -d --name wiremock -p 8080:8080 \
  -v $(pwd)/stubs:/home/wiremock \
  wiremock/wiremock:2.35.0 --global-response-templating --verbose --root-dir /home/wiremock
```


## JUnit 5

Использование расширения и аннотации `@WireMockTest`: сервер поднимается автоматически, порт выделяется динамически.

```java
import static com.github.tomakehurst.wiremock.client.WireMock.*;

@WireMockTest
class WireMockJUnit5Test {

    @Test
    void paymentProcessing(WireMockRuntimeInfo wm) {
        int port = wm.getHttpPort();

        stubFor(post(urlEqualTo("/api/payments"))
            .willReturn(aResponse()
                .withStatus(200)
                .withHeader("Content-Type", "application/json")
                .withBody("{\"transactionId\": \"txn_123\", \"status\": \"SUCCESS\"}")));

        PaymentResult result = paymentService.processPayment(createTestPayment());

        assertThat(result.isSuccessful()).isTrue();
        assertThat(result.getTransactionId()).isEqualTo("txn_123");
    }

    @Test
    void userNotFound() {
        stubFor(get(urlEqualTo("/api/users/999"))
            .willReturn(aResponse()
                .withStatus(404)
                .withBody("{\"error\": \"User not found\"}")));

        assertThatThrownBy(() -> userService.getUserById(999L))
            .isInstanceOf(UserNotFoundException.class);
    }
}
```

**Импорты:** `get`, `post`, `urlEqualTo`, `aResponse`, `stubFor` — из `com.github.tomakehurst.wiremock.client.WireMock`.


## Базовый stubbing

Один стаб на метод и URL; при необходимости добавляются заголовки и тело.

| Метод | Пример |
|-------|--------|
| GET | `stubFor(get(urlEqualTo("/api/products")).willReturn(aResponse().withStatus(200).withBody("[]")))` |
| POST | `stubFor(post(urlEqualTo("/api/orders")).willReturn(aResponse().withStatus(201).withBody("{\"id\":\"ORD-1\"}")))` |
| PUT | `stubFor(put(urlEqualTo("/api/users/1")).willReturn(aResponse().withStatus(200)))` |
| DELETE | `stubFor(delete(urlEqualTo("/api/users/1")).willReturn(aResponse().withStatus(204)))` |

**Пример с заголовками и телом:**

```java
        stubFor(get(urlEqualTo("/api/data"))
            .willReturn(aResponse()
                .withStatus(200)
                .withHeader("Content-Type", "application/json")
        .withHeader("X-API-Version", "v2")
                .withBody("{\"data\": \"test\"}")));
```


## Сопоставление запросов

| Что сопоставлять | Метод WireMock | Пример |
|------------------|----------------|--------|
| URL точный | `urlEqualTo("/api/users/1")` | Точное совпадение пути (без query) |
| URL по шаблону | `urlMatching("/api/users/[0-9]+")` | Регулярное выражение |
| Путь без query | `urlPathEqualTo("/api/products")` | Путь без учёта query |
| Query-параметр | `withQueryParam("q", equalTo("laptop"))` | Значение параметра |
| Заголовок | `withHeader("Authorization", equalTo("Bearer x"))` | Значение заголовка |
| Тело JSON | `withRequestBody(equalToJson("{\"name\":\"John\"}"))` | Совпадение JSON |
| JSON Path | `withRequestBody(matchingJsonPath("$.query"))` | Наличие пути в JSON |

**Пример: URL + query + заголовок:**

```java
        stubFor(get(urlPathEqualTo("/api/search"))
            .withQueryParam("q", equalTo("laptop"))
            .withHeader("Authorization", equalTo("Bearer token123"))
    .willReturn(aResponse().withBody("{\"results\": []}")));
```

**Пример: тело запроса (JSON):**

```java
        stubFor(post(urlEqualTo("/api/users"))
            .withRequestBody(equalToJson("{\"name\": \"John\", \"email\": \"john@example.com\"}"))
            .willReturn(aResponse().withStatus(201).withBody("{\"id\": 1}")));
```


## Шаблоны ответов

Включите трансформер `response-template`. В теле ответа можно использовать Handlebars и переменные запроса.

**Доступные переменные (примеры):**

- `{{request.path.[0]}}`, `{{request.path.[1]}}` — сегменты пути
- `{{request.query.paramName}}` — query-параметр
- `{{request.headers.HeaderName}}` — заголовок
- `{{now}}`, `{{now format='yyyy-MM-dd'}}` — дата/время
- `{{randomInt}}`, `{{randomValue length=10 type='ALPHANUMERIC'}}` — случайные значения
- `{{jsonPath request.body '$.field'}}` — поле из тела запроса

**Пример:**

```java
        stubFor(get(urlMatching("/api/users/([0-9]+)"))
            .willReturn(aResponse()
                .withStatus(200)
                .withHeader("Content-Type", "application/json")
        .withBody("{\"id\": {{request.path.[1]}}, \"name\": \"User {{request.path.[1]}}\", \"timestamp\": \"{{now}}\"}")
        .withTransformers("response-template")));
```

Без `.withTransformers("response-template")` подстановки не выполняются.


## Stateful-сценарии

Сценарии задают последовательность ответов в зависимости от состояния (`whenScenarioStateIs`, `willSetStateTo`).

**Пример: два шага (создание платежа проверка статуса):**

```java
// Шаг 1: создание платежа
        stubFor(post(urlEqualTo("/api/payments"))
            .inScenario("Payment Flow")
            .whenScenarioStateIs(STARTED)
            .willReturn(aResponse()
                .withBody("{\"status\": \"PENDING\", \"id\": \"pay_123\"}")
                .withTransformers("response-template"))
            .willSetStateTo("PAYMENT_CREATED"));

// Шаг 2: статус после создания
        stubFor(get(urlEqualTo("/api/payments/pay_123"))
            .inScenario("Payment Flow")
            .whenScenarioStateIs("PAYMENT_CREATED")
    .willReturn(aResponse().withBody("{\"status\": \"PROCESSING\"}")));

// В тесте: сначала createPayment(), затем getPaymentStatus("pay_123")
```


## Spring Boot

Используйте `@AutoConfigureWireMock(port = 0)` для случайного порта и инжект `WireMockServer` или укажите порт в конфигурации теста.

```java
@SpringBootTest
@AutoConfigureWireMock(port = 0)
class SpringWireMockTest {

    @Autowired
    private WireMockServer wireMockServer;

    @Autowired
    private RestTemplate restTemplate;

    @Test
    void restTemplateWithWireMock() {
        wireMockServer.stubFor(get(urlEqualTo("/api/test"))
            .willReturn(aResponse()
                .withStatus(200)
                .withBody("{\"message\": \"Hello from WireMock\"}")));

        String url = "http://localhost:" + wireMockServer.port() + "/api/test";
        ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).contains("Hello from WireMock");
    }
}
```

В `application-test.properties` или тестовом профиле задайте базовый URL сервиса как `http://localhost:${wiremock.server.port}` (или через `@DynamicPropertySource`), чтобы клиент ходил в WireMock.


## Продвинутые сценарии

**Задержка ответа (таймауты):**

```java
stubFor(get(urlEqualTo("/api/slow"))
            .willReturn(aResponse()
                .withStatus(200)
        .withBody("{\"result\": \"ok\"}")
        .withFixedDelay(5000)));
```

**Ошибка и повтор (stateful):**

```java
        stubFor(get(urlEqualTo("/api/retry"))
    .inScenario("Retry")
            .whenScenarioStateIs(STARTED)
            .willReturn(aResponse().withStatus(500))
            .willSetStateTo("FAILED_ONCE"));

        stubFor(get(urlEqualTo("/api/retry"))
    .inScenario("Retry")
            .whenScenarioStateIs("FAILED_ONCE")
    .willReturn(aResponse().withStatus(200).withBody("success")));
```

**Проверка вызовов (verification):**

```java
stubFor(post(urlEqualTo("/api/data")).willReturn(aResponse().withStatus(201)));
apiClient.postData("test");

        verify(postRequestedFor(urlEqualTo("/api/data"))
            .withHeader("Content-Type", equalTo("application/json"))
    .withRequestBody(equalTo("test")));

// Количество вызовов
        verify(3, getRequestedFor(urlEqualTo("/api/status")));
```

**Запись и воспроизведение:** запуск standalone с проксированием на реальный сервис записывает запросы/ответы в файлы; затем тот же каталог можно использовать как `--root-dir` для воспроизведения без реального сервиса.


## Рекомендации

- **Изоляция:** в `@BeforeEach` вызывать `WireMock.reset()`, чтобы стабы не перетекали между тестами.
- **Организация:** выносить настройку стабов в вспомогательные методы или общий базовый класс.
- **Один стаб — один сценарий:** не дублировать логику в десятках стабов; по возможности использовать шаблоны и сценарии.
- **Проверка вызовов:** использовать `verify(...)` там, где важно убедиться в корректных запросах к моку.
- **Порты:** предпочитать динамический порт (`port = 0` или `WireMockRuntimeInfo.getHttpPort()`), чтобы избежать конфликтов в CI и локально.


## Решение проблем

### Конфликт портов

- Использовать динамический порт: `@AutoConfigureWireMock(port = 0)` или `@WireMockTest` (порт через `WireMockRuntimeInfo.getHttpPort()`).
- В тесте подставлять этот порт в URL клиента (через свойства или `@DynamicPropertySource`).

### Запрос возвращает 404, стаб не срабатывает

- Проверить метод (GET/POST/...), путь (с учётом префикса контекста), query и заголовки.
- Включить логирование: `WireMock.addMockServiceRequestListener((request, response) -> { ... })` и посмотреть, что реально приходит на WireMock.

### Стабы из предыдущего теста влияют на текущий

- В `@BeforeEach` вызывать `WireMock.reset()`.

### Шаблоны в ответе не подставляются

- Добавить `.withTransformers("response-template")` к ответу.
- Для standalone/Docker включить `--global-response-templating`.

### Конфликт версий WireMock (например, со Spring Boot)

- В `spring-boot-starter-test` исключить старую зависимость `wiremock-core` и явно подключить нужную версию `wiremock-jre8` и при необходимости `wiremock-junit5` / `wiremock-spring-boot`.


## Заключение

**WireMock** даёт полный контроль над HTTP-взаимодействиями в тестах: стабильные, воспроизводимые сценарии без зависимости от внешних API. Удобно использовать для интеграционных и контрактных тестов микросервисов, клиентов внешних API и сценариев с таймаутами, повторами и разными состояниями.

**Когда уместен:** тестирование HTTP-клиентов, микросервисов, контрактов, отказоустойчивости, разработка без доступа к внешним сервисам.
**Когда не нужен:** чистые unit-тесты без HTTP (достаточно Mockito), тесты, требующие реального интеграционного окружения или не-HTTP протоколов.

**Дальше:** [[rest-assured|REST Assured]] — тестирование REST API.

## См. также

- [[rest-assured|REST Assured для Java]]
