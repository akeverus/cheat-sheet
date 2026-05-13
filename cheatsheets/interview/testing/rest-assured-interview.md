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
> - [ ] REST Assured — это HTTP-клиент уровня production, заменяющий RestTemplate в боевых сервисах | ❌ ПОСЛЕДСТВИЕ: REST Assured предназначен для тестов (зависит от Hamcrest matchers, медленнее RestTemplate), использование в production добавляет ~5MB лишних зависимостей и thread-safety issues
> - [ ] REST Assured — это GUI-инструмент типа Postman для ручного тестирования | ❌ ПОСЛЕДСТВИЕ: REST Assured — Java DSL без GUI; путаница с Postman приведёт к ожиданию click-based workflow вместо кода
> - [x] Java DSL для тестирования REST API через fluent BDD-синтаксис given-when-then с Hamcrest matchers и встроенной JSON/XML валидацией; стандарт де-факто для integration-тестов REST в Java | ✓ ПРИМЕНЯТЬ: для integration-тестов REST endpoints в JUnit/Spring Boot Test 📋 ПРАВИЛО: REST Assured = тестовый DSL для HTTP, не production-клиент 🔗 См. Q2
> - [ ] REST Assured генерирует контрактные тесты автоматически из OpenAPI-спецификации | ❌ ПОСЛЕДСТВИЕ: REST Assured не генерирует тесты автоматически — это путаница с openapi-generator или Pact; тесты пишутся вручную в BDD-стиле

## Q2. Какой BDD-синтаксис используется?

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
> - [ ] Arrange-Act-Assert через три отдельных метода: `setup()`, `execute()`, `verify()` | ❌ ПОСЛЕДСТВИЕ: REST Assured использует именно fluent chain given().when().then(), а не разделённые методы; попытка вызвать `setup()` приведёт к компиляционной ошибке (нет такого метода)
> - [x] Fluent chain `given().when().then()`: given() — настройка запроса (headers, body, auth), when() — HTTP-операция (get/post/put/delete), then() — assertions (statusCode, body, time); читается как BDD-спецификация | ✓ ПРИМЕНЯТЬ: для всех REST Assured тестов — структурирует тест как поведенческую спецификацию 📋 ПРАВИЛО: given = arrange, when = act, then = assert 🔗 См. Q3
> - [ ] Только `when().then()` без given(); given() устарел в версии 5.x | ❌ ПОСЛЕДСТВИЕ: given() — обязательная часть DSL и не устарел; пропуск given() лишает теста настроек (headers, body, auth), запрос пойдёт с дефолтами
> - [ ] expect().request() — обратный порядок: сначала ожидания, потом запрос | ❌ ПОСЛЕДСТВИЕ: путаница с устаревшим EasyB DSL; REST Assured никогда не имел такого порядка, код не скомпилируется

## Q3. Как отправить POST-запрос с JSON-телом?

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
> - [x] Три способа: `body(jsonString)` с явным `contentType(JSON)`, `body(pojoObject)` с авто-сериализацией через Jackson, `body(Map.of(...))` для динамического JSON; всегда задавать contentType до body() | ✓ ПРИМЕНЯТЬ: POJO — для типизированных DTO, Map — для гибкого тестового JSON, String — для тестов на edge cases (malformed JSON) 📋 ПРАВИЛО: contentType ПЕРЕД body() — иначе сериализация по умолчанию text/plain 🔗 См. Q4
> - [ ] Использовать `param("body", json)` — REST Assured автоматически распознает JSON | ❌ ПОСЛЕДСТВИЕ: param() добавляет query-параметры, не тело; запрос уйдёт как GET с `?body=...` или POST с пустым телом — сервер вернёт 400/415
> - [ ] Достаточно передать строку в `.post(url, body)` вторым аргументом | ❌ ПОСЛЕДСТВИЕ: метод `.post(String path)` принимает только path; такой вызов не скомпилируется — body задаётся через given().body()
> - [ ] Сериализовать POJO вручную через `new ObjectMapper().writeValueAsString(obj)` перед каждым тестом | ❌ ПОСЛЕДСТВИЕ: дублирование кода и зависимость от конкретного Jackson; REST Assured делает это автоматически когда задан contentType(JSON)

## Q4. Как извлекать значения из ответа?

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
> - [ ] Через `.then().getBody()` сразу после statusCode() | ❌ ПОСЛЕДСТВИЕ: метод getBody() на ValidatableResponse не существует — компиляция упадёт; для извлечения нужен `.extract()` между then() и getter-ами
> - [ ] Сохранять весь ответ в файл и парсить отдельно через ObjectMapper | ❌ ПОСЛЕДСТВИЕ: лишний IO + дублирование парсинга; теряется fluent-стиль теста, тяжело дебажить
> - [ ] REST Assured не позволяет извлекать значения — только assertions через body() | ❌ ПОСЛЕДСТВИЕ: фундаментальное заблуждение; extract() — основной механизм для chained scenarios (login → token → next request)
> - [x] Через `.extract()` после then(): `.extract().jsonPath().getLong("id")` для одного поля, `.extract().response()` для всего ответа, `.extract().as(Pojo.class)` для авто-десериализации в POJO | ✓ ПРИМЕНЯТЬ: chained tests (создать ресурс → использовать его id в следующем запросе), типизированные scenarios через `.as(Pojo.class)` 📋 ПРАВИЛО: extract() ПОСЛЕ then() — сначала валидация, потом извлечение 🔗 См. Q5

## Q5. Что такое JsonPath и как он используется в REST Assured?

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
> - [ ] JsonPath REST Assured использует синтаксис RFC 9535 (Goessner) с `$.users[*].name` | ❌ ПОСЛЕДСТВИЕ: REST Assured использует GPath (Groovy), а не Goessner JsonPath; `$.users[*].name` не сработает — нужно `users.name`
> - [ ] JsonPath проверяет JSON Schema (типы, required-поля) | ❌ ПОСЛЕДСТВИЕ: путаница с json-schema-validator; JsonPath — только навигация по значениям, для схемы используется `matchesJsonSchemaInClasspath()`
> - [x] GPath (Groovy-стиль) синтаксис для навигации по JSON: `users[0].name`, `users.find { it.id == 2 }.name`, `users.findAll { it.age > 28 }.name`; используется в `.body("path", matcher)` для assertions | ✓ ПРИМЕНЯТЬ: для проверки конкретных полей в ответе, фильтрации массивов по условиям 📋 ПРАВИЛО: REST Assured JsonPath = Groovy GPath, не Goessner JSONPath ($.x) 🔗 См. Q6
> - [ ] Только примитивный синтаксис `users[0]` — фильтры и closures недоступны | ❌ ПОСЛЕДСТВИЕ: лишение себя самых полезных возможностей; без `find { it.id == X }` тесты ломаются от смены порядка элементов в массиве

## Q6. Как реализовать аутентификацию?

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
> - [] Только Basic Auth — другие схемы (OAuth, Digest, Bearer) не поддерживаются | ❌ ПОСЛЕДСТВИЕ: REST Assured поддерживает все основные схемы из коробки; такое заблуждение приведёт к ручной реализации OAuth там где есть готовый `.auth().oauth2()`
> - [x] Через `.auth()`: `.basic(user, pass)` для Basic, `.oauth2(token)` для Bearer/OAuth2, `.digest(user, pass)` для Digest; для динамического токена — сначала логин-запрос с extract().path("token"), потом передать токен в `.auth().oauth2()` следующего запроса | ✓ ПРИМЕНЯТЬ: outh2 для JWT/Bearer (самый частый кейс), basic для legacy API, digest для устаревших систем; токен можно вынести в RequestSpecification 📋 ПРАВИЛО: auth() ДО when() — иначе credentials не попадут в запрос 🔗 См. Q7
> - [ ] Только через ручной заголовок `.header("Authorization", "Bearer " + token)` | ❌ ПОСЛЕДСТВИЕ: работает, но для Digest требуется challenge-response — ручной заголовок не справится; auth().digest() делает это автоматически
> - [ ] REST Assured отправляет credentials всегда в plain text, поэтому Basic Auth небезопасен в тестах | ❌ ПОСЛЕДСТВИЕ: для теста безопасности это вторично (тест ходит на localhost/staging); реальная защита — HTTPS на API, REST Assured не управляет транспортом

## Q7. Что такое RequestSpecification и когда его использовать?

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
> - [ ] Это специальный matcher для проверки RFC 7230 формата HTTP-запроса | ❌ ПОСЛЕДСТВИЕ: путаница со спецификацией HTTP; RequestSpecification — это переиспользуемая конфигурация теста, не HTTP-протокол
> - [ ] Spring-аннотация для конфигурации тестов | ❌ ПОСЛЕДСТВИЕ: RequestSpecification — интерфейс REST Assured, не Spring-аннотация; не работает с @Autowired
> - [ ] Альтернатива given() — взаимоисключающие подходы | ❌ ПОСЛЕДСТВИЕ: они работают ВМЕСТЕ: `given().spec(commonSpec).header("X-Custom", "x")` — spec даёт базу, дополнительные методы расширяют её
> - [x] Переиспользуемая конфигурация запроса (baseUri, basePath, contentType, headers, auth, log level) построенная через RequestSpecBuilder и применяемая через `given().spec(...)`; устраняет boilerplate в общем @BeforeEach или базовом классе тестов | ✓ ПРИМЕНЯТЬ: когда 3+ теста делят общую настройку (baseUri, auth-токен, request-id header); вынести в ApiTestBase 📋 ПРАВИЛО: общее → в spec, специфичное → в конкретный тест 🔗 См. Q8

## Q8. Как использовать ResponseSpecification?

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
> - [ ] ResponseSpecification полностью заменяет body() — после spec() нельзя добавить специфичные проверки | ❌ ПОСЛЕДСТВИЕ: они комбинируются — `.then().spec(commonSpec).body("name", equalTo("Alice"))`; вера в исключающую природу приведёт к дублированию общих проверок
> - [x] Переиспользуемая валидация ответа через ResponseSpecBuilder: expectStatusCode, expectContentType, expectHeader, expectResponseTime, применяется через `.then().spec(...)` и комбинируется со специфичными body()-проверками | ✓ ПРИМЕНЯТЬ: для общих SLA-проверок (responseTime < 2s, rate-limit headers, JSON content-type) одинаковых для группы endpoints 📋 ПРАВИЛО: общая валидация → ResponseSpecification, доменная → body() в конкретном тесте 🔗 См. Q9
> - [ ] ResponseSpecification — это синоним RequestSpecification (один интерфейс) | ❌ ПОСЛЕДСТВИЕ: это разные интерфейсы для разных фаз (request vs response); попытка использовать RequestSpec в then() — компиляционная ошибка
> - [ ] Применяется только для проверки status code, остальное недоступно | ❌ ПОСЛЕДСТВИЕ: ограничивает использование; ResponseSpecification поддерживает headers, content-type, response time, cookies, body matchers — весь спектр проверок

## Q9. Как обрабатывать query parameters и path variables?

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
> - [ ] queryParam() и pathParam() взаимозаменяемы — REST Assured автоматически определяет тип | ❌ ПОСЛЕДСТВИЕ: pathParam подставляется в `{userId}` плейсхолдер пути, queryParam добавляется в `?key=value`; перепутав — получите либо двойной плейсхолдер, либо отсутствующий параметр в URL
> - [ ] Все параметры передавать через `.param()` — это универсальный метод | ❌ ПОСЛЕДСТВИЕ: `.param()` отправит query для GET и form-encoded для POST, что неоднозначно; phpath-плейсхолдеры через param() НЕ подставятся, и URL уйдёт сломанным
> - [ ] Multipart требует особого MIME-парсера, REST Assured этого не умеет | ❌ ПОСЛЕДСТВИЕ: ложное ограничение; REST Assured из коробки поддерживает `.multiPart(name, file, contentType)` для file uploads
> - [x] queryParam() — для `?key=value`, pathParam() — для `{placeholder}` в URL, formParam() — для `application/x-www-form-urlencoded`, multiPart() — для file uploads с явным contentType; каждый метод соответствует своему стандарту HTTP | ✓ ПРИМЕНЯТЬ: queryParam для фильтров/пагинации, pathParam для REST-стиля /users/{id}, formParam для OAuth login форм, multiPart для file API 📋 ПРАВИЛО: query → ?key=val, path → {var}, form → urlencoded body, multipart → file uploads 🔗 См. Q10

## Q10. Как интегрировать REST Assured со Spring Boot Test?

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
> - [x] Два варианта: `@SpringBootTest(webEnvironment = RANDOM_PORT)` с `RestAssured.port = port` (реальный сервер, full integration) или `RestAssuredMockMvc.mockMvc(mockMvc)` для `@WebMvcTest` (mock context, быстрее но без http-стека) | ✓ ПРИМЕНЯТЬ: RANDOM_PORT для end-to-end (с фильтрами, security, validation), MockMvc — для быстрых контроллерных тестов 📋 ПРАВИЛО: full integration → RANDOM_PORT; MVC-логика без HTTP → RestAssuredMockMvc 🔗 См. Q11
> - [ ] Только через `@AutoConfigureRestAssured` — отдельная аннотация в Spring | ❌ ПОСЛЕДСТВИЕ: такой аннотации не существует; REST Assured интегрируется через ручной setUp() с RestAssured.port = port или RestAssuredMockMvc
> - [ ] Сервер обязательно стартовать на фиксированном порту 8080, иначе тесты упадут | ❌ ПОСЛЕДСТВИЕ: параллельные тесты будут конфликтовать на одном порту; RANDOM_PORT решает это автоматически, нужно лишь подхватить @LocalServerPort
> - [ ] RestAssuredMockMvc требует поднятия полного Spring Boot контекста через @SpringBootTest | ❌ ПОСЛЕДСТВИЕ: смысл RestAssuredMockMvc — работать без полного контекста (через @WebMvcTest); такой подход сводит на нет ускорение MockMvc

## Q11. Как валидировать XML-ответы?

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
> - [ ] REST Assured не поддерживает XML — только JSON | ❌ ПОСЛЕДСТВИЕ: ложное ограничение; REST Assured исходно проектировался для XML и REST и поддерживает XPath, XSD-валидацию через matchesXsdInClasspath()
> - [ ] XML валидируется тем же JsonPath синтаксисом, что и JSON | ❌ ПОСЛЕДСТВИЕ: путаница — для XML используется XPath/GPath с другим синтаксисом (`users.user[0].name`, `users.user.@id`); JsonPath на XML вернёт null или ошибку
> - [ ] Только полное сравнение body со строкой XML возможно | ❌ ПОСЛЕДСТВИЕ: хрупкие тесты — любой пробел/атрибут ломает; нужно проверять конкретные узлы через XPath, не весь XML целиком
> - [x] Через GPath-стиль XPath: `.body("users.user[0].name", equalTo("Alice"))` для элементов, `.body("users.user.@id", hasItem("1"))` для атрибутов (через `@`), а схема валидируется через `.body(matchesXsdInClasspath("schema.xsd"))`; обязательно `.accept(ContentType.XML)` в given() | ✓ ПРИМЕНЯТЬ: для legacy SOAP/XML API, при контракте с XSD-схемой 📋 ПРАВИЛО: атрибуты XML → префикс `@`, XSD валидация → matchesXsdInClasspath 🔗 См. Q12

## Q12. Как протестировать WebSocket или SSE?

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
> - [ ] REST Assured поддерживает WebSocket через `.protocol("ws")` | ❌ ПОСЛЕДСТВИЕ: метод protocol() для ws не существует; REST Assured построен на HTTP-клиенте Apache HttpComponents и не умеет ws-handshake
> - [x] WebSocket не поддерживается — используйте Spring StandardWebSocketClient, Tyrus (JSR-356) или okhttp3.WebSocket; SSE можно частично читать через REST Assured как plain text-стрим с `accept("text/event-stream")` и `extract().asString()`, но это блокирующий вызов | ✓ ПРИМЕНЯТЬ: REST Assured — для request-response HTTP; для full-duplex (WS) — отдельная либа; для SSE с короткой сессией — REST Assured работает 📋 ПРАВИЛО: REST Assured = HTTP request-response, не stream-протоколы 🔗 См. Q13
> - [ ] Достаточно тех же given().when().then() для WebSocket | ❌ ПОСЛЕДСТВИЕ: тест не будет компилироваться или зависнет на handshake — DSL не поддерживает Upgrade-протокол
> - [ ] Для SSE требуется специальный матчер matchesSseStream() | ❌ ПОСЛЕДСТВИЕ: такого матчера не существует; SSE читается как обычный текстовый ответ, парсится строками `event:`/`data:` вручную

## Q13. Как организовать тестовые данные?

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
> - [ ] Только хардкод JSON-строк прямо в тесте — никаких builder-ов или fixtures | ❌ ПОСЛЕДСТВИЕ: дублирование данных по всем тестам; невозможно переиспользовать «валидный user» в 20 тестах, изменение одного поля требует правки во всех
> - [ ] Использовать production БД с реальными данными | ❌ ПОСЛЕДСТВИЕ: flaky tests при изменении production-данных, риск повреждения; нарушение принципа изоляции тестов
> - [ ] Только @ParameterizedTest и @CsvSource — всё через CSV | ❌ ПОСЛЕДСТВИЕ: CSV неудобен для вложенных структур (объекты, списки); сложные кейсы требуют builder pattern или JSON-файлов
> - [x] Комбинация подходов: JSON-fixtures в `src/test/resources/*.json` для статичных payloads, Builder pattern (`TestDataBuilder.validUser()`) для типизированных DTO с вариациями, @ParameterizedTest с @CsvSource для матрицы edge-cases (валидные/невалидные значения) | ✓ ПРИМЕНЯТЬ: builder для частых вариаций одного объекта, JSON-fixture для сложного nested payload, CSV для проверки граничных значений 📋 ПРАВИЛО: статика → JSON-файл, динамика → builder, матрица → @ParameterizedTest 🔗 См. Q14

## Q14. Какие проблемы могут возникнуть при использовании REST Assured?

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
> - [ ] У REST Assured нет проблем — это идеальный инструмент для любых тестов | ❌ ПОСЛЕДСТВИЕ: ложное чувство уверенности; не учитываются reall trade-offs (медленнее MockMvc для unit-тестов, Groovy-синтаксис, thread-safety)
> - [ ] Главная проблема — отсутствие поддержки Java 17+ | ❌ ПОСЛЕДСТВИЕ: REST Assured 5.x работает с Java 8+ включая Java 21; ложное ограничение приведёт к ненужной миграции
> - [x] Ключевые подводные камни: Groovy closures (`it`, `find`) — отдельный синтаксис; медленнее MockMvc из-за реального HTTP-стека; flaky tests из-за зависимости от порядка элементов (use `find { it.id == X }` вместо `[0]`); `RestAssured.given()` не thread-safe — для параллельных тестов нужны локальные спецификации | ✓ ПРИМЕНЯТЬ: знать про эти грабли при выборе между REST Assured и MockMvc/WebTestClient, использовать WireMock/MockServer для изоляции от внешних сервисов 📋 ПРАВИЛО: real HTTP = медленнее, Groovy GPath = особый синтаксис, parallel = локальный spec 🔗 См. Q15
> - [ ] Достаточно увеличить timeout и все проблемы исчезнут | ❌ ПОСЛЕДСТВИЕ: timeout не решает flaky из-за array order, Groovy-синтаксис или thread-safety; такой подход маскирует баги

## Q15. Чем REST Assured отличается от WebTestClient и TestRestTemplate?

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


> [!mcq]
> - [ ] Все три инструмента идентичны — выбор зависит только от вкуса | ❌ ПОСЛЕДСТВИЕ: WebTestClient привязан к WebFlux/reactive stack, TestRestTemplate — только Spring Boot Test, REST Assured — любой Java; выбор «по вкусу» приведёт к use-WebTestClient в MVC проекте с reactive-stack overhead
> - [ ] WebTestClient — для любых Spring-приложений (и MVC, и WebFlux) | ❌ ПОСЛЕДСТВИЕ: WebTestClient изначально для WebFlux; в Spring MVC он работает но через mock binding, не как естественный выбор — TestRestTemplate проще
> - [x] REST Assured — BDD-стиль для любого Java-проекта (не только Spring), JsonPath/XPath, RestAssuredMockMvc для @WebMvcTest; WebTestClient — fluent reactive DSL для Spring WebFlux с StepVerifier; TestRestTemplate — простой синхронный клиент только в Spring Boot Test, ResponseEntity + AssertJ | ✓ ПРИМЕНЯТЬ: REST Assured для cross-stack E2E API tests, WebTestClient для reactive WebFlux endpoints, TestRestTemplate для быстрых integration-тестов Spring MVC 📋 ПРАВИЛО: BDD/cross-stack → REST Assured, reactive → WebTestClient, простота MVC → TestRestTemplate 🔗 См. See also
> - [ ] TestRestTemplate автоматически работает с MockMvc и не требует поднятия порта | ❌ ПОСЛЕДСТВИЕ: TestRestTemplate требует RANDOM_PORT/DEFINED_PORT — это HTTP-клиент, не mock; путаница с MockMvc приведёт к ConnectionRefused

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
