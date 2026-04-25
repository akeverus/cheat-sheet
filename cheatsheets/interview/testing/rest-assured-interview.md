---
title: "Вопросы на собеседовании: REST Assured"
description: "REST Assured для тестирования REST API: BDD-синтаксис given/when/then, JsonPath, валидация JSON Schema, аутентификация, спецификации, интеграция с JUnit"
tags:
  - interview
  - testing
  - rest-assured-interview
aliases:
  - "REST Assured interview"
  - "REST Assured собеседование"
  - "REST Assured вопросы"
  - "API testing interview"
  - "Java REST testing"
difficulty: "intermediate"
updated: "2026-04-25"
---
# Вопросы на собеседовании: `REST Assured`

`REST Assured` — Java DSL для тестирования REST API с BDD-синтаксисом `given().when().then()`. Интегрируется с JUnit и Spring Boot Test, поддерживает валидацию JSON Schema, аутентификацию всех типов. Стандарт де-факто для integration-тестирования REST в Java.

Дата последнего обновления: 2026-04-20

## Полезные ссылки

### Официальная документация

- [REST Assured Docs](https://rest-assured.io/) — официальная документация
- [REST Assured GitHub](https://github.com/rest-assured/rest-assured) — репозиторий с примерами
- [Baeldung: REST Assured](https://www.baeldung.com/rest-assured-tutorial) — практическое введение

## Содержание

- [Полезные ссылки](#полезные-ссылки)
- [See also](#see-also)

## Q1. Что такое REST Assured и для чего он используется?

**REST Assured** — Java DSL для тестирования REST API. Предоставляет fluent BDD-синтаксис (Given-When-Then) для отправки HTTP-запросов и валидации ответов.

```xml
<dependency>
    <groupId>io.rest-assured</groupId>
    <artifactId>rest-assured</artifactId>
    <version>5.4.0</version>
    <scope>test</scope>
</dependency>
```

```java
import static io.restassured.RestAssured.*;
import static org.hamcrest.Matchers.*;

@Test
void getUserById() {
    given()
        .baseUri("http://localhost:8080")
        .accept(ContentType.JSON)
    .when()
        .get("/users/1")
    .then()
        .statusCode(200)
        .body("name", equalTo("Alice"))
        .body("email", endsWith("@example.com"))
        .body("age", greaterThanOrEqualTo(18));
}
```

**Применение**: интеграционные тесты REST API, contract testing (частично), smoke tests в CI/CD.


> [!mcq]
> - [x] Правильный ответ | Объяснение концепции 2-3 предложения
> - [ ] Неправильный вариант 1 | Почему ошибка в этом подходе
> - [ ] Неправильный вариант 2 | Это смежное, но отличное понятие
> - [ ] Неправильный вариант 3 | Противоположное направление## Q2. Какой BDD-синтаксис используется?

REST Assured структурирует тест как **Given-When-Then**:

```java
given()                           // Arrange — настройка запроса
    .baseUri("https://api.example.com")
    .header("Authorization", "Bearer " + token)
    .contentType(ContentType.JSON)
    .body(new User("Alice", "alice@example.com"))
.when()                            // Act — выполнение запроса
    .post("/users")
.then()                            // Assert — проверки
    .statusCode(201)
    .body("id", notNullValue())
    .header("Location", containsString("/users/"))
    .time(lessThan(2000L));        // проверка времени ответа
```

Три блока — читаются как спецификация поведения API.


> [!mcq]
> - [x] Правильный ответ | Объяснение концепции 2-3 предложения
> - [ ] Неправильный вариант 1 | Почему ошибка в этом подходе
> - [ ] Неправильный вариант 2 | Это смежное, но отличное понятие
> - [ ] Неправильный вариант 3 | Противоположное направление## Q3. Как отправить POST-запрос с JSON-телом?

```java
// Вариант 1 — JSON как строка
@Test
void createUser() {
    String body = """
        {
            "name": "Alice",
            "email": "alice@example.com"
        }
        """;

    given()
        .contentType(ContentType.JSON)
        .body(body)
    .when()
        .post("/users")
    .then()
        .statusCode(201);
}

// Вариант 2 — объект Java (авто-сериализация)
@Test
void createUserFromObject() {
    CreateUserRequest request = new CreateUserRequest("Alice", "alice@example.com");

    given()
        .contentType(ContentType.JSON)
        .body(request)  // auto-serialized to JSON
    .when()
        .post("/users")
    .then()
        .statusCode(201);
}

// Вариант 3 — Map
@Test
void createUserFromMap() {
    Map<String, Object> body = Map.of(
        "name", "Alice",
        "email", "alice@example.com",
        "age", 30
    );

    given()
        .contentType(ContentType.JSON)
        .body(body)
    .when()
        .post("/users")
    .then()
        .statusCode(201);
}
```


> [!mcq]
> - [x] Правильный ответ | Объяснение концепции 2-3 предложения
> - [ ] Неправильный вариант 1 | Почему ошибка в этом подходе
> - [ ] Неправильный вариант 2 | Это смежное, но отличное понятие
> - [ ] Неправильный вариант 3 | Противоположное направление## Q4. Как извлекать значения из ответа?

```java
// extract() — один из способов получить значение
@Test
void extractValues() {
    Long userId = given()
        .contentType(ContentType.JSON)
        .body(Map.of("name", "Alice"))
    .when()
        .post("/users")
    .then()
        .statusCode(201)
    .extract()
        .jsonPath()
        .getLong("id");

    // Использование извлечённого
    given()
    .when()
        .get("/users/" + userId)
    .then()
        .statusCode(200);
}

// Извлечение всего ответа
@Test
void extractResponse() {
    Response response = given()
    .when()
        .get("/users")
    .then()
        .statusCode(200)
    .extract()
        .response();

    int userCount = response.jsonPath().getList("users").size();
    String firstName = response.jsonPath().getString("users[0].name");
}

// Извлечение в POJO
@Test
void extractAsObject() {
    User user = given()
    .when()
        .get("/users/1")
    .then()
        .statusCode(200)
    .extract()
        .as(User.class);  // авто-десериализация

    assertThat(user.getName()).isEqualTo("Alice");
}
```


> [!mcq]
> - [x] Правильный ответ | Объяснение концепции 2-3 предложения
> - [ ] Неправильный вариант 1 | Почему ошибка в этом подходе
> - [ ] Неправильный вариант 2 | Это смежное, но отличное понятие
> - [ ] Неправильный вариант 3 | Противоположное направление## Q5. Что такое JsonPath и как он используется в REST Assured?

**JsonPath** — синтаксис для навигации по JSON (аналог XPath для XML).

```json
{
  "users": [
    {"id": 1, "name": "Alice", "age": 30},
    {"id": 2, "name": "Bob", "age": 25}
  ],
  "total": 2
}
```

```java
@Test
void jsonPathExamples() {
    given()
    .when()
        .get("/users")
    .then()
        // Простой доступ
        .body("total", equalTo(2))
        .body("users[0].name", equalTo("Alice"))
        .body("users[1].age", equalTo(25))

        // Wildcards
        .body("users.name", hasItems("Alice", "Bob"))
        .body("users.age", everyItem(greaterThan(20)))

        // Filter — найти имя пользователя с id=2
        .body("users.find { it.id == 2 }.name", equalTo("Bob"))

        // FindAll — все имена пользователей старше 28
        .body("users.findAll { it.age > 28 }.name", hasItem("Alice"))

        // Collect
        .body("users.collect { it.name }", hasSize(2));
}
```

Используется Groovy-синтаксис (`it`, `find`, `findAll`, `collect`) для работы с массивами.


> [!mcq]
> - [x] Правильный ответ | Объяснение концепции 2-3 предложения
> - [ ] Неправильный вариант 1 | Почему ошибка в этом подходе
> - [ ] Неправильный вариант 2 | Это смежное, но отличное понятие
> - [ ] Неправильный вариант 3 | Противоположное направление## Q6. Как реализовать аутентификацию?

```java
// Basic Auth
@Test
void basicAuth() {
    given()
        .auth().basic("username", "password")
    .when()
        .get("/api/protected")
    .then()
        .statusCode(200);
}

// OAuth 2.0
@Test
void oauth2() {
    given()
        .auth().oauth2("access-token-here")
    .when()
        .get("/api/protected")
    .then()
        .statusCode(200);
}

// Bearer token (ручной способ)
@Test
void bearerToken() {
    given()
        .header("Authorization", "Bearer " + token)
    .when()
        .get("/api/protected")
    .then()
        .statusCode(200);
}

// Digest Auth
@Test
void digestAuth() {
    given()
        .auth().digest("user", "password")
    .when()
        .get("/api/protected")
    .then()
        .statusCode(200);
}

// Получение токена и использование в следующем запросе
@Test
void loginAndUseToken() {
    String token = given()
        .contentType(ContentType.JSON)
        .body(Map.of("username", "alice", "password", "secret"))
    .when()
        .post("/auth/login")
    .then()
        .statusCode(200)
    .extract()
        .path("token");

    given()
        .auth().oauth2(token)
    .when()
        .get("/users/me")
    .then()
        .body("username", equalTo("alice"));
}
```


> [!mcq]
> - [x] Правильный ответ | Объяснение концепции 2-3 предложения
> - [ ] Неправильный вариант 1 | Почему ошибка в этом подходе
> - [ ] Неправильный вариант 2 | Это смежное, но отличное понятие
> - [ ] Неправильный вариант 3 | Противоположное направление## Q7. Что такое RequestSpecification и когда его использовать?

**RequestSpecification** — переиспользуемая конфигурация запроса (базовый URL, заголовки, аутентификация).

```java
public class ApiTestBase {

    protected RequestSpecification specification;

    @BeforeEach
    void setUp() {
        specification = new RequestSpecBuilder()
            .setBaseUri("https://api.example.com")
            .setBasePath("/v1")
            .setContentType(ContentType.JSON)
            .setAccept(ContentType.JSON)
            .addHeader("X-Request-Id", UUID.randomUUID().toString())
            .setAuth(RestAssured.oauth2(getToken()))
            .log(LogDetail.ALL)                 // логирование всех запросов
            .build();
    }
}

class UserApiTest extends ApiTestBase {
    @Test
    void getUser() {
        given()
            .spec(specification)              // используем общую спецификацию
        .when()
            .get("/users/1")
        .then()
            .statusCode(200);
    }
}
```

Спецификации избавляют от дублирования boilerplate в тестах.


> [!mcq]
> - [x] Правильный ответ | Объяснение концепции 2-3 предложения
> - [ ] Неправильный вариант 1 | Почему ошибка в этом подходе
> - [ ] Неправильный вариант 2 | Это смежное, но отличное понятие
> - [ ] Неправильный вариант 3 | Противоположное направление## Q8. Как использовать ResponseSpecification?

**ResponseSpecification** — переиспользуемая валидация ответов.

```java
@BeforeEach
void setUp() {
    ResponseSpecification responseSpec = new ResponseSpecBuilder()
        .expectStatusCode(200)
        .expectContentType(ContentType.JSON)
        .expectHeader("X-Rate-Limit-Remaining", notNullValue())
        .expectResponseTime(lessThan(2000L))
        .build();
}

@Test
void fetchUser() {
    given().spec(requestSpec)
    .when().get("/users/1")
    .then().spec(responseSpec)           // проверки из спецификации
        .body("name", equalTo("Alice")); // + специфичная проверка
}
```


> [!mcq]
> - [x] Правильный ответ | Объяснение концепции 2-3 предложения
> - [ ] Неправильный вариант 1 | Почему ошибка в этом подходе
> - [ ] Неправильный вариант 2 | Это смежное, но отличное понятие
> - [ ] Неправильный вариант 3 | Противоположное направление## Q9. Как обрабатывать query parameters и path variables?

```java
// Query Parameters
@Test
void queryParams() {
    given()
        .queryParam("page", 0)
        .queryParam("size", 20)
        .queryParam("sort", "name,asc")
    .when()
        .get("/users")                     // → /users?page=0&size=20&sort=name,asc
    .then()
        .statusCode(200);
}

// Path Parameters
@Test
void pathParams() {
    given()
        .pathParam("userId", 123)
        .pathParam("orderId", 456)
    .when()
        .get("/users/{userId}/orders/{orderId}")  // подставит 123 и 456
    .then()
        .statusCode(200);
}

// Form Parameters (application/x-www-form-urlencoded)
@Test
void formParams() {
    given()
        .contentType(ContentType.URLENC)
        .formParam("username", "alice")
        .formParam("password", "secret")
    .when()
        .post("/auth/login")
    .then()
        .statusCode(200);
}

// Multipart file upload
@Test
void fileUpload() {
    given()
        .multiPart("file", new File("test.pdf"), "application/pdf")
        .multiPart("description", "Test file")
    .when()
        .post("/documents")
    .then()
        .statusCode(201);
}
```


> [!mcq]
> - [x] Правильный ответ | Объяснение концепции 2-3 предложения
> - [ ] Неправильный вариант 1 | Почему ошибка в этом подходе
> - [ ] Неправильный вариант 2 | Это смежное, но отличное понятие
> - [ ] Неправильный вариант 3 | Противоположное направление## Q10. Как интегрировать REST Assured со Spring Boot Test?

```java
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
class UserApiIntegrationTest {

    @LocalServerPort
    private int port;

    @BeforeEach
    void setUp() {
        RestAssured.port = port;             // используем тестовый порт
    }

    @Test
    void shouldCreateUser() {
        given()
            .contentType(ContentType.JSON)
            .body(new CreateUserRequest("Alice", "alice@example.com"))
        .when()
            .post("/api/users")
        .then()
            .statusCode(201)
            .body("id", notNullValue())
            .body("name", equalTo("Alice"));
    }
}
```

```java
// Альтернатива — RestAssuredMockMvc для MVC тестов без старта сервера
@WebMvcTest(UserController.class)
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        RestAssuredMockMvc.mockMvc(mockMvc);
    }

    @Test
    void shouldReturnUser() {
        RestAssuredMockMvc.given()
            .contentType(ContentType.JSON)
        .when()
            .get("/api/users/1")
        .then()
            .statusCode(200);
    }
}
```


> [!mcq]
> - [x] Правильный ответ | Объяснение концепции 2-3 предложения
> - [ ] Неправильный вариант 1 | Почему ошибка в этом подходе
> - [ ] Неправильный вариант 2 | Это смежное, но отличное понятие
> - [ ] Неправильный вариант 3 | Противоположное направление## Q11. Как валидировать XML-ответы?

```java
// Пример XML ответа
/*
<users>
    <user id="1">
        <name>Alice</name>
        <email>alice@example.com</email>
    </user>
</users>
*/

@Test
void validateXml() {
    given()
        .accept(ContentType.XML)
    .when()
        .get("/users")
    .then()
        .statusCode(200)
        .contentType(ContentType.XML)
        // XPath запросы
        .body("users.user[0].name", equalTo("Alice"))
        .body("users.user.@id", hasItem("1"));  // @id — атрибут
}

// XSD валидация
@Test
void validateAgainstSchema() {
    given()
    .when()
        .get("/users")
    .then()
        .body(matchesXsdInClasspath("users.xsd"));
}
```


> [!mcq]
> - [x] Правильный ответ | Объяснение концепции 2-3 предложения
> - [ ] Неправильный вариант 1 | Почему ошибка в этом подходе
> - [ ] Неправильный вариант 2 | Это смежное, но отличное понятие
> - [ ] Неправильный вариант 3 | Противоположное направление## Q12. Как протестировать WebSocket или SSE?

REST Assured работает только с REST/HTTP. Для WebSocket/SSE используются другие инструменты:

```java
// Для Server-Sent Events — можно получить стрим как text
@Test
void testSSE() {
    String events = given()
        .accept("text/event-stream")
    .when()
        .get("/api/events/stream")
    .then()
        .statusCode(200)
    .extract()
        .asString();

    assertThat(events).contains("event: order-created");
}

// Для WebSocket — использовать отдельную библиотеку, например:
// - Spring StandardWebSocketClient
// - Tyrus Client (реализация JSR-356)
// - okhttp3.WebSocket
```


> [!mcq]
> - [x] Правильный ответ | Объяснение концепции 2-3 предложения
> - [ ] Неправильный вариант 1 | Почему ошибка в этом подходе
> - [ ] Неправильный вариант 2 | Это смежное, но отличное понятие
> - [ ] Неправильный вариант 3 | Противоположное направление## Q13. Как организовать тестовые данные?

```java
// 1. Test fixtures через JSON файлы
@Test
void fromJsonFile() {
    given()
        .contentType(ContentType.JSON)
        .body(new File("src/test/resources/user.json"))
    .when()
        .post("/users")
    .then()
        .statusCode(201);
}

// 2. Builder pattern для тестовых данных
public class TestDataBuilder {
    public static CreateUserRequest validUser() {
        return new CreateUserRequest("Alice", "alice@example.com", 30);
    }

    public static CreateUserRequest userWithRole(String role) {
        return new CreateUserRequest("Bob", "bob@example.com", 25, role);
    }
}

// 3. @ParameterizedTest для разных сценариев
@ParameterizedTest
@CsvSource({
    "invalid-email, 400",
    "alice@example.com, 201",
    ", 400"
})
void validateUserCreation(String email, int expectedStatus) {
    given()
        .contentType(ContentType.JSON)
        .body(Map.of("name", "Test", "email", email))
    .when()
        .post("/users")
    .then()
        .statusCode(expectedStatus);
}
```


> [!mcq]
> - [x] Правильный ответ | Объяснение концепции 2-3 предложения
> - [ ] Неправильный вариант 1 | Почему ошибка в этом подходе
> - [ ] Неправильный вариант 2 | Это смежное, но отличное понятие
> - [ ] Неправильный вариант 3 | Противоположное направление## Q14. Какие проблемы могут возникнуть при использовании REST Assured?

1. **Groovy closures в assertions** — требуют понимания Groovy-синтаксиса:

```java
.body("users.find { it.id == 2 }.name", equalTo("Bob"))  // это Groovy, не Java
```

2. **Медленные тесты из-за реального HTTP** — для unit-тестов лучше MockMvc.

3. **Flaky tests** при зависимости от порядка в массивах:

```java
// ПЛОХО — порядок может измениться
.body("users[0].name", equalTo("Alice"))

// ХОРОШО — явный фильтр
.body("users.find { it.id == 1 }.name", equalTo("Alice"))
```

4. **Зависимость от внешнего сервиса** — используйте WireMock/MockServer для изоляции.

5. **Большое тело запроса/ответа в логах** — настройте `filter(RequestLoggingFilter)` с условиями.

6. **Thread-safety** — `RestAssured.given()` не thread-safe, используйте локальные спецификации в параллельных тестах.


> [!mcq]
> - [x] Правильный ответ | Объяснение концепции 2-3 предложения
> - [ ] Неправильный вариант 1 | Почему ошибка в этом подходе
> - [ ] Неправильный вариант 2 | Это смежное, но отличное понятие
> - [ ] Неправильный вариант 3 | Противоположное направление## Q15. Чем REST Assured отличается от WebTestClient и TestRestTemplate?

| Критерий | REST Assured | WebTestClient | TestRestTemplate |
|----------|--------------|---------------|------------------|
| Стиль | BDD (given-when-then) | Fluent reactive | Синхронный |
| Платформа | Любой Java проект | Spring WebFlux | Spring Boot Test |
| Mock без сервера | RestAssuredMockMvc | bindToServer() | нет |
| JSON/XML валидация | JsonPath/XPath | StepVerifier + assertions | ResponseEntity + assertJ |
| Изучение | Средняя сложность | Средне-высокая | Проще всех |
| Применение | End-to-end API tests | WebFlux controllers | Quick integration tests |

**REST Assured** — когда важен BDD-стиль и универсальность (не только Spring).
**WebTestClient** — для Spring WebFlux (реактивных) приложений.
**TestRestTemplate** — быстрые интеграционные тесты Spring MVC.

## See also


> [!mcq]
> - [x] Правильный ответ | Объяснение концепции 2-3 предложения
> - [ ] Неправильный вариант 1 | Почему ошибка в этом подходе
> - [ ] Неправильный вариант 2 | Это смежное, но отличное понятие
> - [ ] Неправильный вариант 3 | Противоположное направление- [Spring Testing](../frameworks/spring/spring-testing-interview.md) — MockMvc, WebTestClient, Spring Test context
- [JUnit 5](junit-interview.md) — JUnit как test runner для REST Assured
- [Contract Testing](contract-testing-interview.md) — Pact, Spring Cloud Contract
- [Testcontainers](testcontainers-interview.md) — реальные БД/брокеры в интеграционных тестах
- [Integration Testing](integration-testing-interview.md) — стратегии интеграционного тестирования
- [HTTP & REST](../api/http-rest-interview.md) — REST principles для тестирования
- [OpenAPI](../api/openapi-swagger-interview.md) — спецификации как источник тестов
- [Mockito](mockito-interview.md) — mocking зависимостей тестируемого контроллера
- [Test Automation](test-automation-interview.md) — CI/CD integration
- [Unit Testing](unit-testing-interview.md) — различия с юнит-тестированием
