---
title: "REST Assured для Java"
description: "Краткое руководство по тестированию REST API в Java: fluent API, проверка ответов, аутентификация, data-driven тесты, интеграция со Spring Boot."
tags:
  - testing
  - integration-testing
  - rest-assured
difficulty: "intermediate"
prerequisites: []
next: []
updated: "2026-02-11"
---
# REST Assured для Java

Краткое руководство по тестированию REST API в Java: fluent API, проверка ответов, аутентификация, data-driven тесты, интеграция со Spring Boot.

## Полезные ссылки

- [REST Assured Documentation](https://rest-assured.io/) — документация
- [REST Assured GitHub](https://github.com/rest-assured/rest-assured) — исходный код
- [Given-When-Then](https://martinfowler.com/bliki/GivenWhenThen.html) — BDD-подход
- См. также: [[junit-advanced|JUnit Advanced]], [[wiremock]], [[spring-testing|Spring Testing]]

## Содержание

- [Введение](#введение-в-rest-assured)
- [Зависимости](#зависимости)
- [Базовое использование](#базовое-использование)
- [HTTP-методы и типы контента](#http-методы-и-типы-контента)
- [Проверка ответов](#проверка-ответов)
- [Спецификации запросов](#спецификации-запросов)
- [Аутентификация](#аутентификация)
- [Data-driven тесты](#data-driven-тесты)
- [Обработка ошибок](#обработка-ошибок)
- [Интеграция со Spring Boot](#интеграция-со-spring-boot)
- [Расширенные сценарии](#расширенные-сценарии)
- [Производительность](#производительность)
- [Лучшие практики](#лучшие-практики)
- [Решение проблем](#решение-проблем)
- [FAQ](#faq)
- [Заключение](#заключение)

## Введение в REST Assured

**REST Assured** — Java DSL для тестирования REST API с fluent API в стиле BDD (Given-When-Then).

**Почему REST Assured:**

- **Fluent API** — читаемый синтаксис (given/when/then)
- **Проверки** — статус, заголовки, JSON/XML
- **Аутентификация** — Basic, OAuth, JWT, API-ключи
- **Переиспользуемые спецификации** — общая настройка запросов и ответов
- **Интеграция со Spring** — Spring Boot, MockMvc
- **Параметризованные тесты** — JUnit 5 @ParameterizedTest, CSV

## Зависимости

### Maven (обязательный минимум)

```xml
<dependency>
    <groupId>io.rest-assured</groupId>
    <artifactId>rest-assured</artifactId>
    <version>5.3.0</version>
    <scope>test</scope>
</dependency>
```

Опционально: `json-path`, `xml-path` (обычно идут с rest-assured), `spring-mock-mvc` для Spring MVC.

### BOM (рекомендуется)

```xml
<dependencyManagement>
    <dependencies>
        <dependency>
            <groupId>io.rest-assured</groupId>
            <artifactId>rest-assured-bom</artifactId>
            <version>5.3.0</version>
            <type>pom</type>
            <scope>import</scope>
        </dependency>
    </dependencies>
</dependencyManagement>
```

### Gradle

```gradle
testImplementation 'io.rest-assured:rest-assured:5.3.0'
testImplementation 'org.junit.jupiter:junit-jupiter:5.10.0'
```

### Версии

- **4.x:** Java 8+, без Groovy, новый JSON-парсер (Jackson)
- **5.x:** Java 11+, обновлённый HTTP-клиент

Конфликты версий: при использовании `spring-boot-starter-test` при необходимости явно задайте версию rest-assured или исключите транзитивную.

## Базовое использование

Паттерн **given when then**:

```java
    @Test
void getUsers() {
        given()
        .baseUri("http://localhost:8080")
            .when()
                .get("/api/users")
            .then()
                .statusCode(200)
            .contentType(ContentType.JSON)
                .body("size()", greaterThan(0));
    }

    @Test
void createUser() {
        given()
            .contentType(ContentType.JSON)
        .body(Map.of("name", "John", "email", "john@example.com"))
            .when()
                .post("/api/users")
            .then()
                .statusCode(201)
                .body("id", notNullValue())
            .body("name", equalTo("John"));
}
```

Нужны статические импорты: `import static io.restassured.RestAssured.*;` и `import static org.hamcrest.Matchers.*;`

## HTTP-методы и типы контента

| Метод    | Вызов           | Типичный статус |
|----------|------------------|------------------|
| GET      | `.get(path)`     | 200              |
| POST     | `.post(path)`    | 201              |
| PUT      | `.put(path)`     | 200              |
| PATCH    | `.patch(path)`   | 200              |
| DELETE   | `.delete(path)`  | 204              |
| HEAD     | `.head(path)`    | 200              |
| OPTIONS  | `.options(path)` | 200              |

**Типы контента:**

- `ContentType.JSON` — JSON (тело и заголовки)
- `ContentType.XML` — XML
- `ContentType.URLENC` — form (`.formParam("key", "value")`)
- `ContentType.MULTIPART` — загрузка файлов (`.multiPart("file", file, "application/octet-stream")`)

Пример с form и multipart:

```java
given().contentType(ContentType.URLENC)
    .formParam("name", "Test").formParam("email", "test@example.com")
    .when().post("/api/users").then().statusCode(201);

given().contentType(ContentType.MULTIPART)
            .multiPart("file", new File("test.txt"), "text/plain")
    .when().post("/api/upload").then().statusCode(200);
```

## Проверка ответов

**Статус и заголовки:**

```java
            .then()
    .statusCode(200)
    .header("Content-Type", containsString("application/json"))
    .header("X-Request-Id", notNullValue())
    .time(lessThan(2000L));
```

**JSON (Hamcrest):**

```java
            .then()
                .body("id", notNullValue())
    .body("name", equalTo("John"))
                .body("email", matchesPattern(".+@.+"))
                .body("items.size()", greaterThan(0))
    .body("items[0].name", notNullValue())
    .body("$", hasKey("createdAt"));
```

**JSON Schema** (при наличии зависимости json-schema-validator):

```java
.then().body(matchesJsonSchemaInClasspath("schemas/user.json"));
```

**XML:**

```java
            .accept(ContentType.XML)
.when().get("/api/users/1")
            .then()
                .contentType(ContentType.XML)
                .body("user.id", notNullValue())
    .body("user.name", notNullValue());
```

## Спецификации запросов

Переиспользуемая настройка запроса и ожиданий ответа:

```java
RequestSpecification baseReq = given()
    .baseUri("http://localhost:8080")
    .basePath("/api")
            .contentType(ContentType.JSON)
            .accept(ContentType.JSON)
            .header("X-API-Key", "test-key")
    .log().ifValidationFails();

ResponseSpecification successResp = expect()
            .statusCode(200)
            .contentType(ContentType.JSON)
    .time(lessThan(2000L));

    @Test
void withSpec() {
    given(baseReq)
        .when().get("/users")
            .then()
            .spec(successResp)
                .body("size()", greaterThan(0));
}
```

## Аутентификация

**Basic:**

```java
given().auth().basic("user", "password").when().get("/api/protected").then().statusCode(200);
given().auth().preemptive().basic("user", "password").when().get("/api/protected").then().statusCode(200);
```

**OAuth2 / Bearer:**

```java
String token = obtainAccessToken(); // свой метод
given().auth().oauth2(token).when().get("/api/protected").then().statusCode(200);
// или
given().header("Authorization", "Bearer " + token).when().get("/api/protected").then().statusCode(200);
```

**API-ключ в заголовке или query:**

```java
given().header("X-API-Key", "secret").when().get("/api/data").then().statusCode(200);
given().queryParam("api_key", "secret").when().get("/api/data").then().statusCode(200);
```

## Data-driven тесты

Параметризованные тесты (JUnit 5):

```java
    @ParameterizedTest
@MethodSource("userData")
void createUser(String name, String email, int expectedStatus) {
    given()
            .contentType(ContentType.JSON)
        .body(Map.of("name", name, "email", email))
        .when().post("/api/users")
        .then().statusCode(expectedStatus);
}

static Stream<Arguments> userData() {
        return Stream.of(
        Arguments.of("John", "john@example.com", 201),
        Arguments.of("", "bad@mail", 400)
    );
}
```

Данные можно загружать из CSV или JSON и итерировать в тесте.

## Обработка ошибок

Проверка кодов ошибок и тела ответа:

```java
given().when().get("/api/users/999")
            .then()
        .statusCode(404)
        .body("error", notNullValue());

given().contentType(ContentType.JSON).body("{}")
    .when().post("/api/users")
            .then()
                .statusCode(400)
        .body("errors", notNullValue());
```

Таймаут и SSL (только для тестовых окружений):

```java
given().timeout(5000).when().get("/api/slow").then().statusCode(anyOf(is(200), is(504)));
given().relaxedHTTPSValidation().when().get("https://test.example.com/api").then().statusCode(200);
```

## Интеграция со Spring Boot

Тестирование контроллеров без поднятия HTTP-сервера (MockMvc):

```java
@SpringBootTest
@AutoConfigureMockMvc
public class ApiTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void createUser() {
        RestAssuredMockMvc.given()
            .mockMvc(mockMvc)
            .contentType(ContentType.JSON)
            .body("{ \"name\": \"Test\", \"email\": \"test@example.com\" }")
            .when().post("/api/users")
            .then()
                .statusCode(201)
                .body("name", equalTo("Test"));
    }
}
```

С Spring Security — использовать `@WithMockUser` или передавать JWT в заголовке в запросе через RestAssuredMockMvc.

## Расширенные сценарии

**Загрузка файла:**

```java
        given()
            .contentType(ContentType.MULTIPART)
    .multiPart("file", new File("doc.pdf"), "application/pdf")
    .when().post("/api/upload")
            .then()
                .statusCode(200)
        .body("fileId", notNullValue());
```

**GraphQL:**

```java
String query = "{ user(id: 1) { id name email } }";
        given()
            .contentType(ContentType.JSON)
            .body(Map.of("query", query))
    .when().post("/graphql")
            .then()
                .statusCode(200)
        .body("data.user.id", notNullValue());
```

**WebSocket:** REST Assured проверяет только HTTP (например, handshake 101). Полноценный WebSocket — отдельными клиентами.

## Производительность

Проверка времени ответа:

```java
given().when().get("/api/users")
            .then()
        .time(lessThan(500L))
                .statusCode(200);
```

Для нагрузочного тестирования предпочтительны JMeter, Gatling и т.п.

## Лучшие практики

- **Спецификации** — общий `RequestSpecification` и `ResponseSpecification` в `@BeforeEach`.
- **Структура** — сгруппировать тесты по фичам (например, `@Nested` в JUnit 5).
- **Данные** — выделить создание/очистку тестовых данных (фазы до/после теста).
- **Логирование** — `.log().ifValidationFails()` для запроса и ответа при падении.
- **Контракты** — проверка JSON Schema или ключевых полей по контракту API.
- **Один сценарий — один пример** — не дублировать однотипные тесты без необходимости.

## Решение проблем

| Симптом | Возможная причина | Действие |
|--------|-------------------|----------|
| Медленные запросы, таймауты | Сеть, настройки сервера, большие данные | Увеличить таймаут в тесте; проверить окружение и лимиты |
| Ошибки подключения | Неверный host/port, SSL, файрвол | Проверить базовый URL, при необходимости `relaxedHTTPSValidation()` в тестах |
| Путь JSON не находит поле | Другой формат ответа, опечатка в path | Вывести `response.getBody().asString()` и проверить структуру; проверить путь (массивы: `[0].field`) |
| Неверный Content-Type | Сервер ожидает JSON, а уходит другое (или наоборот) | Явно задать `.contentType(ContentType.JSON)` и `.accept(ContentType.JSON)` |
| 401/403 | Нет или неверный токен/ключ | Проверить заголовок Authorization / API-ключ; для Basic — preemptive при необходимости |

**Отладка:** использовать `.log().all()` для запроса и ответа или извлечь `Response` и смотреть `getBody().asString()`, `getHeaders()`, `getStatusCode()`.

## Частые вопросы

**Когда использовать REST Assured?** — Для тестирования REST API (интеграционные, контрактные тесты), особенно с Java/Spring. Для unit-тестов слоя логики лучше JUnit + Mockito; для UI — Selenium и т.п.

**Как настроить под production?** — В production обычно не запускают тесты REST Assured против прод-серверов; тесты идут против тестового окружения. Секреты и URL брать из конфигурации/переменных окружения.

**Где документация?** — [rest-assured.io](https://rest-assured.io/), [GitHub rest-assured](https://github.com/rest-assured/rest-assured).

## Заключение

REST Assured даёт удобный fluent API для тестирования REST API в Java в стиле Given-When-Then: проверка статуса, заголовков, JSON/XML, аутентификация, переиспользуемые спецификации, параметризованные тесты и интеграция со Spring. Подходит для интеграционных и контрактных тестов; для нагрузочного тестирования лучше использовать специализированные инструменты.

## См. также

- [[wiremock|WireMock для Java]]
