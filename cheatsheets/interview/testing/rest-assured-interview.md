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
updated: "2026-11-05"
---
# Вопросы на собеседовании: `REST Assured`

`REST Assured` — Java DSL для тестирования HTTP API с цепочкой `given().when().then()`. Он работает с JUnit, поддерживает проверки JSON и XML, а через отдельный модуль может запускать запросы к Spring MVC без сервера.

## Полезные ссылки

### Официальная документация

- [REST Assured Docs](https://rest-assured.io/) — официальная документация
- [REST Assured GitHub](https://github.com/rest-assured/rest-assured) — репозиторий с примерами
- [Baeldung: REST Assured](https://www.baeldung.com/rest-assured-tutorial) — практическое введение

## Содержание

- [Полезные ссылки](#полезные-ссылки)
- [Q1. Что такое REST Assured и для чего он используется?](#q1)
- [Q2. Какой BDD-синтаксис используется?](#q2)
- [Q3. Как отправить POST-запрос с JSON-телом?](#q3)
- [Q4. Как извлекать значения из ответа?](#q4)
- [Q5. Что такое JsonPath и как он используется в REST Assured?](#q5)
- [Q6. Как реализовать аутентификацию?](#q6)
- [Q7. Что такое RequestSpecification и когда его использовать?](#q7)
- [Q8. Как использовать ResponseSpecification?](#q8)
- [Q9. Как обрабатывать query parameters и path variables?](#q9)
- [Q10. Как интегрировать REST Assured со Spring Boot Test?](#q10)
- [Q11. Как валидировать XML-ответы?](#q11)
- [Q12. Как протестировать WebSocket или SSE?](#q12)
- [Q13. Как организовать тестовые данные?](#q13)
- [Q14. Какие проблемы могут возникнуть при использовании REST Assured?](#q14)
- [Q15. Чем REST Assured отличается от WebTestClient и TestRestTemplate?](#q15)
- [See also](#see-also)

<a id="q1"></a>

## Q1. Что такое REST Assured и для чего он используется?

**REST Assured** — Java-библиотека (DSL) для тестирования REST API: одной цепочкой методов вы отправляете HTTP-запрос и проверяете ответ, не возясь вручную с HTTP-клиентом и парсингом JSON.

Главная ценность — читаемость. Тест строится в стиле **Given-When-Then** и читается почти как обычный текст: «дано — такой запрос, когда — отправляем GET, тогда — статус 200 и поле name равно Alice». Под капотом REST Assured сам выполняет HTTP-вызов, разбирает тело ответа и сверяет значения с Hamcrest-матчерами.

```xml
<dependency>
    <groupId>io.rest-assured</groupId>
    <artifactId>rest-assured</artifactId>
    <version>${rest-assured.version}</version>
    <scope>test</scope>
</dependency>
```

В `pom.xml` задают свойство `rest-assured.version` с версией, совместимой с остальными тестовыми зависимостями проекта.

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

<a id="q2"></a>

## Q2. Какой BDD-синтаксис используется?

Обычный стиль REST Assured — **Given-When-Then**:

- `given()` — **Arrange**: всё, что настраивает запрос (базовый URL, заголовки, авторизация, тело).
- `when()` — переход к **Act**: после него обычно указывают HTTP-метод и путь. Запрос отправляет вызов `get`, `post` и т. п., а не само `when()`.
- `then()` — **Assert**: проверки ответа (статус, поля тела, заголовки, время).

```java
given()                           // Arrange — настройка запроса
    .baseUri("https://api.example.com")
    .header("Authorization", "Bearer " + token)
    .contentType(ContentType.JSON)
    .body(new User("Alice", "alice@example.com"))
.when()                            // далее — HTTP-вызов
    .post("/users")
.then()                            // Assert — проверки
    .statusCode(201)
    .body("id", notNullValue())
    .header("Location", containsString("/users/"))
    .time(lessThan(2000L));        // проверка времени ответа
```

Это соглашение о читаемости, а не запрет API: `when()` возвращает `RequestSpecification`, поэтому до `get()` можно добавить, например, `header(...)`. Есть и стиль `given().expect().statusCode(200).when().get("/users/1")`: ожидания описываются заранее, но проверяются на полученном ответе. Длительность в `time(...)` относится к клиентскому запросу, а не только к работе серверного обработчика.

<a id="q3"></a>

## Q3. Как отправить POST-запрос с JSON-телом?

Тело запроса в `.body(...)` можно задать тремя способами — выбор зависит от того, что под рукой и насколько важна типобезопасность:

- **строкой** — когда нужно полностью контролировать JSON (например, проверить заведомо «битое» тело);
- **Java-объектом** — REST Assured сериализует его по `contentType`, если на classpath есть подходящий маппер, например Jackson или Gson;
- **`Map`** — для небольшого тела без отдельного DTO. Как и для POJO, для JSON-сериализации нужен доступный или явно заданный object mapper. Готовая JSON-строка сериализации уже не требует.

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

<a id="q4"></a>

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

<a id="q5"></a>

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

**Выбор адресации:** если контракт не фиксирует порядок, конкретную сущность ищут через `find` по уникальному полю. Для проверки сортировки или фиксированной позиции нужен индекс (`users[0]`); поиск по ID такую проверку не заменяет (см. Q14).

<a id="q6"></a>

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

<a id="q7"></a>

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

<a id="q8"></a>

## Q8. Как использовать ResponseSpecification?

**ResponseSpecification** хранит повторяемые ожидания ответа: статус, content type, заголовки, тело и время. Её можно подключить через `.then().spec(responseSpec)` и добавить проверки конкретного ответа.

В примере спецификация — поле класса: локальная переменная из `setUp()` недоступна в `fetchUser()`. Предполагается, что тестовый сервер на `localhost:8080` для этого запроса возвращает JSON, статус 200 и указанный заголовок.

```java
import io.restassured.builder.ResponseSpecBuilder;
import io.restassured.http.ContentType;
import io.restassured.specification.ResponseSpecification;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

class UserApiResponseTest {
    private ResponseSpecification responseSpec;

    @BeforeEach
    void setUp() {
        responseSpec = new ResponseSpecBuilder()
            .expectStatusCode(200)
            .expectContentType(ContentType.JSON)
            .expectHeader("X-Rate-Limit-Remaining", notNullValue())
            .expectResponseTime(lessThan(2000L))
            .build();
    }

    @Test
    void fetchUser() {
        given().baseUri("http://localhost:8080")
        .when().get("/users/1")
        .then().spec(responseSpec)
            .body("name", equalTo("Alice"));
    }
}
```

`build()` создаёт описание ожиданий; для проверки нужен реальный ответ: запрос с подключённой спецификацией либо `responseSpec.validate(response)`. Ожидания разрешено задать и до HTTP-вызова через `expect().spec(responseSpec).when().get(...)`.

При слиянии спецификаций нельзя рассчитывать на логическое AND любых конфликтующих настроек: в REST Assured 6.3.0 переданная спецификация может перезаписать status code, status line, content type и root path; ожидания тела, заголовков и cookies объединяются. Учитывайте порядок подключения. [Контракт `ResponseSpecification.spec()`](https://github.com/rest-assured/rest-assured/blob/rest-assured-6.3.0/rest-assured/src/main/java/io/restassured/specification/ResponseSpecification.java).

<a id="q9"></a>

## Q9. Как обрабатывать query parameters и path variables?

REST Assured разделяет виды параметров по отдельным методам. Автоматическое URL-кодирование включено по умолчанию; `urlEncodingEnabled(false)` его отключает. Обычно передают исходные значения: предварительное percent-encoding без учёта настройки может привести к повторному кодированию.

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

<a id="q10"></a>

## Q10. Как интегрировать REST Assured со Spring Boot Test?

Есть два режима интеграции, и выбор между ними — это компромисс «реалистичность против скорости»:

- **Полный старт сервера** — `@SpringBootTest(webEnvironment = RANDOM_PORT)`: Spring поднимает встроенный сервер на случайном порту, его номер берётся через `@LocalServerPort` и задаётся локально через `given().port(port)`. Запросы идут по реальному HTTP через весь стек (фильтры, сериализация, сеть) — максимально близко к продакшену, но медленнее.
- **Без сервера** — `RestAssuredMockMvc` поверх `MockMvc`: запросы проходят через Spring MVC без сети и без поднятия сервера. Зарегистрированные в `MockMvc` servlet-фильтры при этом могут выполняться; поведение самого servlet-контейнера этим режимом не проверяется.

**Эмпирическое правило:** end-to-end и проверки полного пути запроса — через `@SpringBootTest`; быстрые тесты одного контроллера — через `RestAssuredMockMvc`.

```java
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class UserApiIntegrationTest {

    @LocalServerPort
    private int port;

    @Test
    void shouldCreateUser() {
        given()
            .port(port)
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

    @Test
    void shouldReturnUser() {
        RestAssuredMockMvc.given()
            .mockMvc(mockMvc)
            .contentType(ContentType.JSON)
        .when()
            .get("/api/users/1")
        .then()
            .statusCode(200);
    }
}
```

<a id="q11"></a>

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
        // GPath: элементы и атрибуты
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

Аналогичная структурная проверка есть и для JSON — через отдельный модуль `io.rest-assured:json-schema-validator`. Он даёт статический матчер `matchesJsonSchemaInClasspath("user-schema.json")`, который подставляется в `body(...)` точно так же, как `matchesXsdInClasspath`. Файл схемы лежит в classpath (обычно `src/test/resources`). Матчер проверяет выраженные в схеме ограничения: отсутствие JSON-поля обнаружится, если оно обязательно, например через `required`; неподходящий тип — при заданном `type`. Одного `properties` недостаточно, чтобы требовать присутствия поля. Схема может ограничивать и значения, например через `enum`, но не подтверждает автоматически все бизнес-правила или совместимость API. [JSON Schema: обязательные свойства](https://json-schema.org/understanding-json-schema/reference/object#requiredproperties).

<a id="q12"></a>

## Q12. Как протестировать WebSocket или SSE?

REST Assured рассчитан на HTTP-запрос и ответ. Для непрерывного SSE и обмена WebSocket нужен клиент, который умеет читать поток или сообщения по мере поступления.

- **SSE (Server-Sent Events)** — короткий поток, который сервер завершает, можно получить обычным GET и проверить как строку через `extract().asString()`. Если ответ не заканчивается, такой тест будет ждать завершения вместо последовательной обработки событий.
- **WebSocket** — не поддерживается вообще: нужен отдельный клиент (Spring `StandardWebSocketClient`, Tyrus — реализация JSR-356, или `okhttp3.WebSocket`).

```java
// Для Server-Sent Events — можно получить стрим как text
@Test
void testSSE() {
    String events = given()
        .accept("text/event-stream")
    .when()
        .get("/api/events/finite") // тестовый endpoint завершает поток
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

<a id="q13"></a>

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
    ", 400",       // null
    "'', 400"      // пустая строка
})
void validateUserCreation(String email, int expectedStatus) {
    Map<String, Object> payload = new HashMap<>();
    payload.put("name", "Test");
    payload.put("email", email); // HashMap допускает null

    given()
        .contentType(ContentType.JSON)
        .body(payload)
    .when()
        .post("/users")
    .then()
        .statusCode(expectedStatus);
}
```

Это пример контракта API: создание с валидным email даёт 201, с невалидным, `null` или пустой строкой — 400. Реальный сервис может иметь другие правила. Импорты для параметризации — `org.junit.jupiter.params.ParameterizedTest` и `org.junit.jupiter.params.provider.CsvSource`, коллекции — `java.util.Map` и `java.util.HashMap`.

В JUnit 5.10.3 незаполненная CSV-ячейка даёт `null`, а `''` — пустую строку. `Map.of` не допускает `null` и оборвал бы третий случай до HTTP-вызова. Для JSON `null` здесь используется допускающая его `HashMap`; настройка mapper должна сохранять null-значения (например, стандартный `ObjectMapper` Jackson 2). Отсутствующее поле — отдельный вход: ключ `email` вообще не добавляют. Для точного JSON можно передать готовую строку, исключив влияние настроек сериализации. [Семантика `CsvSource`](https://docs.junit.org/5.10.3/api/org.junit.jupiter.params/org/junit/jupiter/params/provider/CsvSource.html).

<a id="q14"></a>

## Q14. Какие проблемы могут возникнуть при использовании REST Assured?

**Подводные камни** и как их обходить:

1. **Groovy внутри строк-путей.** Java-компилятор не проверяет корректность GPath внутри строкового литерала. Подсветка и inspections IDE зависят от её поддержки и настроек; путь нужно проверять исполняемым тестом.

```java
.body("users.find { it.id == 2 }.name", equalTo("Bob"))  // это Groovy, не Java
```

2. **Медленные тесты из-за реального HTTP.** Каждый запрос идёт по сети через весь стек. Для проверки логики одного контроллера дешевле MockMvc (см. Q10).

3. **Flaky-тесты из-за порядка в массивах.** Если контракт не фиксирует порядок, проверка по индексу может сломаться при перестановке элементов. Для поиска сущности используйте уникальное поле; если проверяется сама сортировка, индексы уместны:

```java
// ПЛОХО — порядок может измениться
.body("users[0].name", equalTo("Alice"))

// ХОРОШО — явный фильтр
.body("users.find { it.id == 1 }.name", equalTo("Alice"))
```

4. **Зависимость от внешнего сервиса.** Тест, ходящий в живой сторонний API, нестабилен и непредсказуем — изолируйтесь через WireMock или MockServer.

5. **Раздувание логов.** `LogDetail.ALL` включает полное тело у соответствующего request/response logger. Обычный `RequestLoggingFilter` печатает запрос при каждом проходе; сам по себе он не задаёт условие. Для вывода при провале валидации используйте `.log().ifValidationFails()` на нужной стороне; собственный фильтр требует явной политики.

6. **Отсутствие thread-safety.** Параллельная запись глобальных статических настроек `RestAssured` создаёт гонки: тесты могут перебивать конфигурацию друг друга. Создавайте отдельную спецификацию для теста или запроса. Локальное имя переменной не делает общий изменяемый `RequestSpecification` безопасным для конкурентной модификации.

<a id="q15"></a>

## Q15. Чем REST Assured отличается от WebTestClient и TestRestTemplate?

Все три инструмента могут проверять HTTP API. REST Assured даёт цепочку `given/when/then` и не требует Spring. `WebTestClient` поддерживает Spring MVC и WebFlux как с работающим сервером, так и в mock-режиме. `TestRestTemplate` — синхронный клиент Spring Boot Test для запросов к запущенному серверу.

| Критерий | REST Assured | WebTestClient | TestRestTemplate |
|----------|--------------|---------------|------------------|
| Стиль | BDD (given-when-then) | Fluent reactive | Синхронный |
| Платформа | Java, Spring не обязателен | Spring MVC или WebFlux | Spring Boot Test |
| Без сервера | отдельный модуль `spring-mock-mvc` для MVC | mock-привязка к MVC или WebFlux | нет |
| С сервером | обычный HTTP-клиент | `bindToServer()` | обычный HTTP-клиент |
| Типичный выбор | DSL и проверки ответа | единый клиент для mock и live проверок Spring | синхронные проверки запущенного Boot-приложения |

**Когда что выбирать:**

- **REST Assured** — нужен BDD-стиль и универсальность: проект не на Spring или хочется единый инструмент для разных сервисов.
- **WebTestClient** — нужен клиент Spring с одинаковым стилем проверок для mock-режима и живого сервера. Для WebFlux есть `bindToController()` и `bindToApplicationContext()`; для MVC используют `MockMvcWebTestClient`. `bindToServer()` подключается к работающему серверу по HTTP.
- **TestRestTemplate** — нужны синхронные запросы к запущенному Spring Boot-приложению без DSL REST Assured.

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
