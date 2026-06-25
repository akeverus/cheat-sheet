---
title: "Вопросы на собеседовании: REST Assured"
description: "REST Assured для тестирования REST API: BDD-синтаксис given/when/then, JsonPath, валидация JSON Schema, аутентификация, спецификации, интеграция с JUnit"
tags:
  - interview
  - testing
  - rest-assured-interview
type: "interview"
difficulty: "intermediate"
aliases:
  - "Вопросы на собеседовании"
  - "REST Assured"
  - "REST Assured interview"
  - "REST Assured собеседование"
prerequisites:
  - "[[rest-assured]]"
next: []
updated: "2026-04-25"
---
# Вопросы на собеседовании: `REST Assured`

`REST Assured` — Java DSL для тестирования REST API с BDD-синтаксисом `given().when().then()`. Интегрируется с JUnit и Spring Boot Test, поддерживает валидацию JSON Schema, аутентификацию всех типов. Стандарт де-факто для integration-тестирования REST в Java.

## Полезные ссылки

### Официальная документация

- [REST Assured Docs](https://rest-assured.io/) — официальная документация
- [REST Assured GitHub](https://github.com/rest-assured/rest-assured) — репозиторий с примерами
- [Baeldung: REST Assured](https://www.baeldung.com/rest-assured-tutorial) — практическое введение

## Содержание

- [Полезные ссылки](#полезные-ссылки)
- [See also](#see-also)

## Q1. Что такое REST Assured и для чего он используется?

**REST Assured** — Java-библиотека (DSL) для тестирования REST API: одной цепочкой методов вы отправляете HTTP-запрос и проверяете ответ, не возясь вручную с HTTP-клиентом и парсингом JSON.

Главная ценность — читаемость. Тест строится в стиле **Given-When-Then** и читается почти как обычный текст: «дано — такой запрос, когда — отправляем GET, тогда — статус 200 и поле name равно Alice». Под капотом REST Assured сам выполняет HTTP-вызов, разбирает тело ответа и сверяет значения с Hamcrest-матчерами.

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

**Сценарии применения:**

- интеграционные тесты REST API (основной кейс);
- contract testing — частично, как проверка, что ответ совпадает с ожидаемым контрактом;
- smoke-тесты в CI/CD — быстрая проверка, что развёрнутый сервис отвечает.

## Q2. Какой BDD-синтаксис используется?

REST Assured делит тест на три блока — **Given-When-Then**, — каждый со своей ответственностью:

- `given()` — **Arrange**: всё, что настраивает запрос (базовый URL, заголовки, авторизация, тело).
- `when()` — **Act**: само действие, то есть HTTP-метод и путь.
- `then()` — **Assert**: проверки ответа (статус, поля тела, заголовки, время).

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

Разделение делает тест самодокументирующимся: блоки читаются как спецификация поведения API, а при падении сразу видно, на каком этапе оно произошло.

## Q3. Как отправить POST-запрос с JSON-телом?

Тело запроса в `.body(...)` можно задать тремя способами — выбор зависит от того, что под рукой и насколько важна типобезопасность:

- **строкой** — когда нужно полностью контролировать JSON (например, проверить заведомо «битое» тело);
- **Java-объектом** — основной способ: REST Assured сам сериализует объект через Jackson/Gson по `contentType`;
- **`Map`** — компромисс, когда отдельный класс заводить лень, а строку хочется избежать.

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

## Q4. Как извлекать значения из ответа?

Когда мало проверить ответ — нужно достать из него значение и использовать дальше (например, ID созданной сущности для следующего запроса), — цепочку завершают вызовом `.extract()`. После него доступны три уровня детализации:

- **`extract().jsonPath().getLong("id")`** — вытащить конкретное поле по JsonPath.
- **`extract().response()`** — получить весь объект `Response` и дальше разбирать его как угодно.
- **`extract().as(User.class)`** — десериализовать всё тело в POJO.

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

## Q5. Что такое JsonPath и как он используется в REST Assured?

**JsonPath** — язык навигации по структуре JSON, аналог XPath для XML. В REST Assured это первый аргумент в `.body("...", matcher)`: вы указываете путь к полю, а матчер проверяет его значение.

Важная деталь: JsonPath в REST Assured основан на **Groovy GPath**, а не на «классическом» JsonPath с `$.` и `[?(...)]`. Отсюда поддержка Groovy-замыканий (`find`, `findAll`, `collect`) прямо в строке пути.

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

Что доступно для работы с массивами:

- **`find { ... }`** — первый элемент, удовлетворяющий условию (`it` — текущий элемент).
- **`findAll { ... }`** — все подходящие элементы.
- **`collect { ... }`** — преобразовать каждый элемент (например, собрать одно поле из всех).
- **`hasItems` / `everyItem` / `hasSize`** — Hamcrest-матчеры для проверки коллекций целиком.

**Рекомендация:** доступ по индексу (`users[0]`) хрупок — порядок в массиве может меняться; для надёжных проверок используйте `find` по уникальному полю (см. Q14).

## Q6. Как реализовать аутентификацию?

REST Assured даёт встроенные методы под каждую распространённую схему авторизации, так что вручную собирать заголовки приходится редко:

- **`auth().basic(user, pass)`** — HTTP Basic.
- **`auth().digest(user, pass)`** — HTTP Digest.
- **`auth().oauth2(token)`** — добавляет `Authorization: Bearer <token>` (универсальный способ для bearer-токенов).
- **ручной `header("Authorization", ...)`** — когда нужна нестандартная схема или полный контроль над заголовком.

**Ловушка собеседования — преэмптивная аутентификация.** Обычный `auth().basic(user, pass)` работает по challenge-схеме: REST Assured сначала отправляет запрос *без* credentials, ждёт от сервера ответ `401` с заголовком `WWW-Authenticate` и только после этого повторяет запрос уже с логином и паролем. Если сервер такой challenge не присылает (а многие API просто сразу отвечают ошибкой), `auth().basic()` молча не сработает — credentials так и не уйдут. Решение — `auth().preemptive().basic(user, pass)`: заголовок `Authorization` отправляется сразу, в первом же запросе, без ожидания 401.

Типичный паттерн в интеграционных тестах — сначала залогиниться (получить токен из ответа через `extract()`), затем подставить его в защищённые запросы.

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

## Q7. Что такое RequestSpecification и когда его использовать?

**RequestSpecification** — собранная заранее конфигурация запроса (базовый URL, путь, заголовки, авторизация, логирование), которую можно переиспользовать во всех тестах через `.spec(specification)`.

Зачем это нужно: без спецификации каждый тест повторяет один и тот же boilerplate — `baseUri`, `contentType`, токен. Это дублирование, и при смене, например, базового URL пришлось бы править десятки тестов. `RequestSpecification` выносит общую настройку в одно место (обычно в базовый класс или `@BeforeEach`), а тесты оставляют только то, что специфично для каждого случая.

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

## Q8. Как использовать ResponseSpecification?

**ResponseSpecification** — то же самое, что `RequestSpecification`, но для проверок ответа. Это вынесенный набор ожиданий (статус, content-type, заголовки, время ответа), который подключается через `.then().spec(responseSpec)`.

Идея та же: проверки, общие для многих эндпоинтов (например, «статус 200, тело — JSON, ответ быстрее 2 секунд»), описываются один раз. В конкретном тесте после `.spec(responseSpec)` остаётся дописать только проверки, уникальные для этого ответа, — общие применятся автоматически.

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

## Q9. Как обрабатывать query parameters и path variables?

REST Assured разводит разные виды параметров по отдельным методам — это безопаснее ручной конкатенации строк, потому что библиотека сама делает URL-кодирование:

- **`queryParam(name, value)`** — параметры строки запроса: `?page=0&size=20`.
- **`pathParam(name, value)`** — подстановка в шаблон пути `{userId}`; путь остаётся читаемым, а значения подставляются по имени.
- **`formParam(name, value)`** — поля формы `application/x-www-form-urlencoded` (классические HTML-формы, логин).
- **`multiPart(...)`** — `multipart/form-data` для загрузки файлов вместе с другими полями.

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

## Q10. Как интегрировать REST Assured со Spring Boot Test?

Есть два режима интеграции, и выбор между ними — это компромисс «реалистичность против скорости»:

- **Полный старт сервера** — `@SpringBootTest(webEnvironment = RANDOM_PORT)`: Spring поднимает встроенный сервер на случайном порту, его номер берётся через `@LocalServerPort` и кладётся в `RestAssured.port`. Запросы идут по реальному HTTP через весь стек (фильтры, сериализация, сеть) — максимально близко к продакшену, но медленнее.
- **Без сервера** — `RestAssuredMockMvc` поверх `MockMvc`: запросы проходят через Spring MVC напрямую, без сети и без поднятия сервера. Заметно быстрее, удобно для `@WebMvcTest` отдельного контроллера, но фильтры/сеть не задействованы.

**Эмпирическое правило:** end-to-end и проверки полного пути запроса — через `@SpringBootTest`; быстрые тесты одного контроллера — через `RestAssuredMockMvc`.

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

## Q11. Как валидировать XML-ответы?

REST Assured работает не только с JSON: для XML вместо JsonPath применяется **XPath-подобный GPath**, а тело сверяется теми же Hamcrest-матчерами. Два уровня проверки:

- **по содержимому** — навигация по дереву (`users.user[0].name`) и обращение к атрибутам через `@` (`users.user.@id`);
- **по структуре** — `matchesXsdInClasspath("users.xsd")` валидирует весь ответ против XSD-схемы, проверяя не отдельные поля, а соответствие документа контракту.

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

Аналогичная структурная проверка есть и для JSON — через отдельный модуль `io.rest-assured:json-schema-validator`. Он даёт статический матчер `matchesJsonSchemaInClasspath("user-schema.json")`, который подставляется в `body(...)` точно так же, как `matchesXsdInClasspath`. Файл схемы лежит в classpath (обычно `src/test/resources`), а валидируется весь ответ целиком: структура документа и типы полей, а не отдельные значения. Это удобно для контрактных smoke-проверок — один матчер ловит и пропавшее поле, и сменившийся тип.

## Q12. Как протестировать WebSocket или SSE?

Коротко: полноценно — никак. REST Assured создан под модель «запрос-ответ» HTTP, а WebSocket и SSE — это потоковые, долгоживущие соединения.

- **SSE (Server-Sent Events)** — частично работает в обход: эндпоинт `text/event-stream` можно вызвать обычным GET и забрать накопленный поток как строку через `extract().asString()`, затем проверить содержимое. Это годится для коротких/конечных стримов, но не для непрерывного потока событий.
- **WebSocket** — не поддерживается вообще: нужен отдельный клиент (Spring `StandardWebSocketClient`, Tyrus — реализация JSR-356, или `okhttp3.WebSocket`).

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

## Q13. Как организовать тестовые данные?

Цель — чтобы данные не дублировались по тестам и были читаемыми. Три приёма, которые хорошо комбинируются:

- **JSON-фикстуры в файлах** — тело запроса хранится в `src/test/resources/*.json` и подаётся как `new File(...)`. Удобно для крупных/реалистичных payload'ов, которые неудобно держать в коде.
- **Builder-методы** — статические фабрики (`validUser()`, `userWithRole(...)`) возвращают готовые объекты. Тест читается по смыслу, а изменение схемы данных правится в одном месте.
- **`@ParameterizedTest`** — один тест прогоняется на наборе входов (валидный/невалидный email и ожидаемый статус). Устраняет копипасту почти одинаковых тестов и заставляет явно перечислить граничные случаи.

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

## Q14. Какие проблемы могут возникнуть при использовании REST Assured?

**Подводные камни** и как их обходить:

1. **Groovy внутри строк-путей.** Замыкания в `.body(...)` — это Groovy, а не Java: IDE их не подсветит и не проверит, ошибку поймаете только в рантайме.

```java
.body("users.find { it.id == 2 }.name", equalTo("Bob"))  // это Groovy, не Java
```

2. **Медленные тесты из-за реального HTTP.** Каждый запрос идёт по сети через весь стек. Для проверки логики одного контроллера дешевле MockMvc (см. Q10).

3. **Flaky-тесты из-за порядка в массивах.** Обращение по индексу ломается, как только сервер вернёт элементы в другом порядке. Фильтруйте по уникальному полю:

```java
// ПЛОХО — порядок может измениться
.body("users[0].name", equalTo("Alice"))

// ХОРОШО — явный фильтр
.body("users.find { it.id == 1 }.name", equalTo("Alice"))
```

4. **Зависимость от внешнего сервиса.** Тест, ходящий в живой сторонний API, нестабилен и непредсказуем — изолируйтесь через WireMock или MockServer.

5. **Раздувание логов.** `LogDetail.ALL` печатает целиком крупные тела запросов/ответов. Логируйте условно — `filter(RequestLoggingFilter)` или `.log().ifValidationFails()`.

6. **Отсутствие thread-safety.** Глобальный статический стейт `RestAssured` не потокобезопасен — при параллельном запуске тесты затирают конфигурацию друг друга. В параллельных тестах используйте локальные `RequestSpecification`, а не глобальные настройки.

## Q15. Чем REST Assured отличается от WebTestClient и TestRestTemplate?

Все три — инструменты для тестирования HTTP API, но различаются стилем и привязкой к Spring. Кратко: REST Assured — про BDD-читаемость и независимость от фреймворка, WebTestClient — про реактивный стек, TestRestTemplate — про простые синхронные тесты Spring MVC.

| Критерий | REST Assured | WebTestClient | TestRestTemplate |
|----------|--------------|---------------|------------------|
| Стиль | BDD (given-when-then) | Fluent reactive | Синхронный |
| Платформа | Любой Java проект | Spring WebFlux | Spring Boot Test |
| Mock без сервера | RestAssuredMockMvc | bindToController() / bindToApplicationContext() | нет |
| JSON/XML валидация | JsonPath/XPath | StepVerifier + assertions | ResponseEntity + assertJ |
| Изучение | Средняя сложность | Средне-высокая | Проще всех |
| Применение | End-to-end API tests | WebFlux controllers | Quick integration tests |

**Когда что выбирать:**

- **REST Assured** — нужен BDD-стиль и универсальность: проект не на Spring или хочется единый инструмент для разных сервисов.
- **WebTestClient** — приложение на Spring WebFlux (реактивный стек); умеет работать и без сервера — через `bindToController()` или `bindToApplicationContext()` запросы идут в mock-окружение без HTTP. А вот `bindToServer()` — наоборот, режим подключения к живому серверу по реальному HTTP.
- **TestRestTemplate** — самый низкий порог входа: быстрые синхронные интеграционные тесты Spring MVC, когда BDD-обвязка не нужна.

## See also

- [Spring Testing](../frameworks/spring/spring-testing-interview.md) — MockMvc, WebTestClient, Spring Test context
- [JUnit 5](junit-interview.md) — JUnit как test runner для REST Assured
- [Contract Testing](contract-testing-interview.md) — Pact, Spring Cloud Contract
- [Testcontainers](testcontainers-interview.md) — реальные БД/брокеры в интеграционных тестах
- [Integration Testing](integration-testing-interview.md) — стратегии интеграционного тестирования
- [HTTP & REST](../api/http-rest-interview.md) — REST principles для тестирования
- [OpenAPI](../api/openapi-swagger-interview.md) — спецификации как источник тестов
- [Mockito](mockito-interview.md) — mocking зависимостей тестируемого контроллера
- [Test Automation](test-automation-interview.md) — CI/CD integration
- [Unit Testing](unit-testing-interview.md) — различия с юнит-тестированием
- [Selenium WebDriver](selenium-interview.md) — локаторы (By), ожидания (implicit/explicit/fluent), Page Object Model,…
- [Cucumber и BDD](cucumber-bdd-interview.md) — REST Assured как проверка API внутри step definitions BDD-сценариев; общий словарь given/when/then
