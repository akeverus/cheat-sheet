---
title: "REST Assured"
description: "REST Assured - это Java DSL для упрощения тестирования и валидации REST API."
tags:
  - libraries
  - java
  - java-rest-assured
difficulty: "intermediate"
prerequisites: []
next: []
updated: "2026-02-11"
---
# REST Assured

**REST Assured** - это **Java DSL** для упрощения тестирования и валидации **REST API**.

## Полезные ссылки

### Официальная документация
- [REST Assured](https://rest-assured.io/) — официальный сайт
- [REST Assured GitHub](https://github.com/rest-assured/rest-assured) — репозиторий проекта
- [REST Assured Documentation](https://github.com/rest-assured/rest-assured/wiki) — документация

### См. также
- [Интеграционное тестирование](../../testing/integration-testing/README.md) — интеграционное тестирование
- [WireMock](java-wiremock.md) — **WireMock** для мокирования **API**

## Содержание

- [Основные возможности](#основные-возможности)
  - [Простые GET запросы](#простые-get-запросы)
  - [POST запросы с JSON](#post-запросы-с-json)
  - [Аутентификация](#аутентификация)
  - [Валидация JSON ответа](#валидация-json-ответа)
  - [XML валидация](#xml-валидация)
  - [Path Parameters](#path-parameters)
  - [Query Parameters](#query-parameters)
  - [Form Parameters](#form-parameters)
  - [File Upload](#file-upload)
  - [Cookies и Headers](#cookies-и-headers)
  - [Response Time Validation](#response-time-validation)
  - [Schema Validation](#schema-validation)
- [Spring Boot Integration](#spring-boot-integration)
  - [Test Slices](#test-slices)
  - [MockMvc Integration](#mockmvc-integration)
- [Configuration](#configuration)
  - [Base Configuration](#base-configuration)
  - [Custom Filters](#custom-filters)
- [Advanced Features](#advanced-features)
  - [Object Mapping](#object-mapping)
  - [Extract Response Data](#extract-response-data)
  - [Specification Reuse](#specification-reuse)
- [Best Practices](#best-practices)
  - [Test Organization](#test-organization)
  - [Data-Driven Tests](#data-driven-tests)
  - [Custom Matchers](#custom-matchers)
- [Integration с другими инструментами](#integration-с-другими-инструментами)
  - [Cucumber Integration](#cucumber-integration)
  - [Allure Reporting](#allure-reporting)

## Основные возможности

### Простые **GET** запросы

Пример теста **GET**-запроса и валидации ответа с **REST Assured** (**Java**).

```java
@Test
public void testGetUser() {
    when()
        .get("/api/users/1")
    .then()
        .statusCode(200)
        .body("name", equalTo("John Doe"))
        .body("id", equalTo(1));
}
```

### **POST** запросы с **JSON**
```java
@Test
public void testCreateUser() {
    String requestBody = """
        {
            "name": "Jane Doe",
            "email": "jane@example.com"
        }
        """;

    given()
        .contentType(ContentType.JSON)
        .body(requestBody)
    .when()
        .post("/api/users")
    .then()
        .statusCode(201)
        .body("id", notNullValue())
        .header("Location", containsString("/api/users/"));
}
```

### Аутентификация
```java
// Basic Auth
given()
    .auth()
    .basic("username", "password")
.when()
    .get("/api/secure")
.then()
    .statusCode(200);

// OAuth2
given()
    .auth()
    .oauth2("access_token")
.when()
    .get("/api/oauth")
.then()
    .statusCode(200);

// Bearer Token
given()
    .header("Authorization", "Bearer " + token)
.when()
    .get("/api/bearer")
.then()
    .statusCode(200);
```

### Валидация **JSON** ответа
```java
@Test
public void testJsonResponseValidation() {
    when()
        .get("/api/products")
    .then()
        .statusCode(200)
        .contentType(ContentType.JSON)
        .body("products.size()", greaterThan(0))
        .body("products[0].name", notNullValue())
        .body("products[0].price", allOf(greaterThan(0.0), lessThan(1000.0)))
        .body("products", hasItems(
            hasEntry("category", "electronics"),
            hasEntry("inStock", true)
        ));
}
```

### **XML** валидация
```java
@Test
public void testXmlResponse() {
    when()
        .get("/api/data.xml")
    .then()
        .statusCode(200)
        .contentType("application/xml")
        .body("root.element.name", equalTo("Test"))
        .body("root.elements.element.size()", equalTo(3));
}
```

### **Path Parameters**
```java
@Test
public void testPathParameters() {
    given()
        .pathParam("userId", 123)
        .pathParam("orderId", "ABC-456")
    .when()
        .get("/api/users/{userId}/orders/{orderId}")
    .then()
        .statusCode(200)
        .body("userId", equalTo(123))
        .body("orderId", equalTo("ABC-456"));
}
```

### **Query Parameters**
```java
@Test
public void testQueryParameters() {
    given()
        .queryParam("status", "active")
        .queryParam("limit", 10)
        .queryParam("sort", "name")
    .when()
        .get("/api/users")
    .then()
        .statusCode(200)
        .body("users.size()", lessThanOrEqualTo(10));
}
```

### **Form Parameters**
```java
@Test
public void testFormParameters() {
    given()
        .contentType(ContentType.URLENC)
        .formParam("username", "john")
        .formParam("password", "secret")
    .when()
        .post("/api/login")
    .then()
        .statusCode(200)
        .cookie("sessionId", notNullValue());
}
```

### **File Upload**
```java
/
 * Тест загрузки файла через REST API
 * Демонстрирует использование multiPart для отправки файлов и дополнительных данных
 */
@Test
public void testFileUpload() {
    // REST Assured DSL для тестирования загрузки файлов
    given()  // Настройка запроса (given - предварительные условия)
        .multiPart("file", new File("test-image.jpg"))      // Добавляем файл для загрузки (имя поля "file")
        .multiPart("description", "Test image")            // Добавляем текстовое поле "description" с описанием
    .when()  // Выполнение запроса (when - действие)
        .post("/api/upload")  // POST запрос на endpoint загрузки файлов
    .then()  // Проверка ответа (then - проверки)
        .statusCode(200)              // Проверяем что статус ответа 200 (OK)
        .body("fileId", notNullValue());  // Проверяем что в теле ответа есть поле "fileId" и оно не null
}
```

### **Cookies** и **Headers**
```java
/
 * Тест работы с cookies и headers в REST API
 * Демонстрирует отправку cookies и headers в запросе и проверку их в ответе
 */
@Test
public void testCookiesAndHeaders() {
    // REST Assured DSL для тестирования с cookies и headers
    given()  // Настройка запроса
        .cookie("sessionId", "abc123")                    // Добавляем cookie "sessionId" со значением "abc123"
        .header("X-API-Key", "my-api-key")                // Добавляем заголовок "X-API-Key" для аутентификации
        .header("Accept-Language", "en-US")               // Добавляем заголовок языка для локализации
    .when()  // Выполнение запроса
        .get("/api/profile")  // GET запрос на endpoint профиля пользователя
    .then()  // Проверка ответа
        .statusCode(200)                                  // Проверяем что статус ответа 200 (OK)
        .cookie("preferences", notNullValue());           // Проверяем что в ответе есть cookie "preferences" и оно не null
}
```

### **Response Time Validation**
```java
/
 * Тест проверки времени ответа API
 * Демонстрирует валидацию производительности endpoint
 */
@Test
public void testResponseTime() {
    // REST Assured DSL для проверки времени ответа
    when()  // Выполнение запроса (when - действие, без given так как нет предварительных условий)
        .get("/api/fast-endpoint")  // GET запрос на быстрый endpoint
    .then()  // Проверка ответа
        .statusCode(200)                    // Проверяем что статус ответа 200 (OK)
        .time(lessThan(1000L));             // Проверяем что время ответа менее 1000 миллисекунд (1 секунда)
        // Это важно для проверки производительности API - endpoint должен отвечать быстро
}
```

### **Schema Validation**
```java
/
 * Тест валидации JSON схемы ответа API
 * Демонстрирует проверку структуры JSON ответа против JSON Schema
 */
@Test
public void testJsonSchemaValidation() {
    // REST Assured DSL для валидации JSON схемы
    when()  // Выполнение запроса
        .get("/api/users/1")  // GET запрос на endpoint получения пользователя по ID
    .then()  // Проверка ответа
        .statusCode(200)  // Проверяем что статус ответа 200 (OK)
        .body(matchesJsonSchemaInClasspath("user-schema.json"));  // Валидация JSON ответа против схемы из classpath
        // JSON Schema определяет структуру, типы полей и обязательные поля
        // Это гарантирует что API возвращает данные в ожидаемом формате
}
```

## **Spring Boot Integration**

### **Test Slices**
```java
/
 * Интеграционный тест REST API с Spring Boot
 * Демонстрирует интеграцию REST Assured с Spring Boot Test
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)  // Spring Boot тест с запуском на случайном порту
@AutoConfigureRestAssured  // Автоматическая настройка REST Assured для работы с Spring Boot
public class UserApiIntegrationTest {

    // TestRestTemplate для альтернативного способа тестирования (не используется в этом примере)
    @Autowired
    private TestRestTemplate restTemplate;

    // Порт на котором запущен Spring Boot сервер (инжектится автоматически)
    @LocalServerPort
    private int port;

    /
     * Тест получения пользователя через REST API
     * Демонстрирует использование REST Assured с Spring Boot на случайном порту
     */
    @Test
    public void testGetUser() {
        // REST Assured DSL для тестирования Spring Boot приложения
        given()  // Настройка запроса
            .port(port)  // Указываем порт на котором запущен Spring Boot сервер
        .when()  // Выполнение запроса
            .get("/api/users/1")  // GET запрос на endpoint получения пользователя с ID=1
        .then()  // Проверка ответа
            .statusCode(200)                    // Проверяем что статус ответа 200 (OK)
            .body("name", equalTo("John Doe")); // Проверяем что поле "name" в JSON ответе равно "John Doe"
    }
}
```

### **MockMvc Integration**
```java
/
 * Тест REST контроллера с использованием MockMvc и REST Assured
 * Демонстрирует интеграцию REST Assured с Spring MockMvc для unit-тестирования контроллеров
 */
@SpringBootTest  // Spring Boot тест с полным контекстом приложения
@AutoConfigureMockMvc  // Автоматическая настройка MockMvc для тестирования контроллеров без запуска сервера
public class UserControllerTest {

    // MockMvc - Spring фреймворк для тестирования контроллеров без запуска HTTP сервера
    @Autowired
    private MockMvc mockMvc;

    /
     * Настройка перед каждым тестом
     * Интегрирует MockMvc с REST Assured для использования REST Assured DSL с MockMvc
     */
    @BeforeEach
    void setUp() {
        // Настраиваем REST Assured для использования MockMvc вместо реального HTTP сервера
        // Это позволяет использовать REST Assured DSL для тестирования контроллеров
        RestAssuredMockMvc.mockMvc(mockMvc);
    }

    /
     * Тест получения пользователя через REST API
     * Использует REST Assured DSL с MockMvc для тестирования контроллера
     */
    @Test
    void testGetUser() {
        // REST Assured DSL для тестирования с MockMvc
        given()  // Настройка запроса
            .contentType(ContentType.JSON)  // Указываем Content-Type заголовок (JSON)
        .when()  // Выполнение запроса
            .get("/api/users/1")  // GET запрос на endpoint получения пользователя с ID=1
        .then()  // Проверка ответа
            .statusCode(200)                    // Проверяем что статус ответа 200 (OK)
            .body("name", equalTo("John Doe")); // Проверяем что поле "name" в JSON ответе равно "John Doe"
    }
}
```

## **Configuration**

### **Base Configuration**
```java
/
 * Базовый класс для REST API тестов
 * Настраивает общие параметры для всех тестов: base URI, port, base path, логирование
 */
public class ApiTestBase {

    /
     * Настройка перед всеми тестами (выполняется один раз)
     * Конфигурирует глобальные параметры REST Assured для всех тестов
     */
    @BeforeAll  // JUnit 5 аннотация - метод выполняется один раз перед всеми тестами
    public static void setup() {
        // Настройка базового URI для всех запросов
        RestAssured.baseURI = "http://localhost";  // Базовый URL сервера (протокол + домен)
        RestAssured.port = 8080;                   // Порт на котором запущен сервер
        RestAssured.basePath = "/api/v1";          // Базовый путь для всех запросов (префикс всех endpoints)

        // Настройка логирования - логировать запросы и ответы только при ошибках валидации
        // Это помогает отладке когда тесты падают - видно что было отправлено и что получено
        RestAssured.enableLoggingOfRequestAndResponseIfValidationFails();
    }

    /
     * Сброс фильтров перед каждым тестом
     * Очищает все фильтры и настройки между тестами для изоляции
     */
    @BeforeEach  // JUnit 5 аннотация - метод выполняется перед каждым тестом
    public void resetFilters() {
        // Сброс всех настроек REST Assured к значениям по умолчанию
        // Это гарантирует что тесты не влияют друг на друга
        RestAssured.reset();
    }
}
```

### **Custom Filters**
```java
/
 * Кастомный фильтр для логирования запросов и ответов в REST Assured
 * Демонстрирует создание собственного фильтра для перехвата и обработки HTTP запросов/ответов
 */
public class LoggingFilter implements Filter {

    /
     * Метод фильтрации - вызывается для каждого HTTP запроса
     * Позволяет перехватить запрос и ответ для логирования или другой обработки
     * @param requestSpec спецификация запроса (метод, URI, headers, body и т.д.)
     * @param responseSpec спецификация ожидаемого ответа (для валидации)
     * @param ctx контекст фильтрации для выполнения следующего фильтра или запроса
     * @return Response объект с данными ответа
     */
    @Override
    public Response filter(FilterableRequestSpecification requestSpec,
                          FilterableResponseSpecification responseSpec,
                          FilterContext ctx) {
        // Логирование запроса - выводим метод и URI перед выполнением запроса
        System.out.println("Request: " + requestSpec.getMethod() + " " + requestSpec.getURI());

        // Выполнение следующего фильтра в цепочке или самого HTTP запроса
        // ctx.next() передает управление следующему фильтру или выполняет запрос если фильтров больше нет
        Response response = ctx.next(requestSpec, responseSpec);

        // Логирование ответа - выводим статус код после получения ответа
        System.out.println("Response: " + response.getStatusCode());

        // Возвращаем ответ для дальнейшей обработки
        return response;
    }
}

// Использование фильтра
given()
    .filter(new LoggingFilter())
.when()
    .get("/api/test");
```

## **Advanced Features**

### **Object Mapping**
```java
// POJO класс
public class User {
    private int id;
    private String name;
    private String email;

    // getters and setters
}

@Test
public void testObjectMapping() {
    User user = given()
        .contentType(ContentType.JSON)
    .when()
        .get("/api/users/1")
    .then()
        .statusCode(200)
        .extract()
        .as(User.class);

    assertEquals("John Doe", user.getName());
}
```

### **Extract Response Data**
```java
@Test
public void testExtractData() {
    String sessionId = given()
        .formParam("username", "john")
        .formParam("password", "pass")
    .when()
        .post("/api/login")
    .then()
        .statusCode(200)
        .extract()
        .cookie("sessionId");

    // Использование извлеченных данных
    given()
        .cookie("sessionId", sessionId)
    .when()
        .get("/api/profile")
    .then()
        .statusCode(200);
}
```

### **Specification Reuse**
```java
public class ApiSpecifications {

    public static RequestSpecification jsonRequest() {
        return given()
            .contentType(ContentType.JSON)
            .accept(ContentType.JSON);
    }

    public static ResponseSpecification successResponse() {
        return expect()
            .statusCode(200)
            .contentType(ContentType.JSON);
    }
}

// Использование
given()
    .spec(ApiSpecifications.jsonRequest())
.when()
    .get("/api/users")
.then()
    .spec(ApiSpecifications.successResponse());
```

## Лучшие практики

### **Test Organization**
```java
public class ApiTestSuite {

    @Test
    public void shouldReturnUserList() {
        // Happy path тест
    }

    @Test
    public void shouldHandleNotFound() {
        // Error handling тест
    }

    @Test
    public void shouldValidateInput() {
        // Validation тест
    }

    @Test
    public void shouldHandleLargePayload() {
        // Performance тест
    }
}
```

### **Data-Driven Tests**
```java
@ParameterizedTest
@CsvSource({
    "1, John Doe",
    "2, Jane Smith",
    "3, Bob Johnson"
})
public void testUserRetrieval(int userId, String expectedName) {
    given()
        .pathParam("id", userId)
    .when()
        .get("/api/users/{id}")
    .then()
        .statusCode(200)
        .body("name", equalTo(expectedName));
}
```

### **Custom Matchers**
```java
public class CustomMatchers {

    public static Matcher<String> isValidEmail() {
        return new TypeSafeMatcher<String>() {
            @Override
            protected boolean matchesSafely(String email) {
                return email.contains("@") && email.contains(".");
            }

            @Override
            public void describeTo(Description description) {
                description.appendText("valid email address");
            }
        };
    }
}

// Использование
.body("email", CustomMatchers.isValidEmail());
```

## **Integration** с другими инструментами

### **Cucumber Integration**
```java
public class ApiSteps {

    @When("I send GET request to {string}")
    public void sendGetRequest(String endpoint) {
        response = given()
            .when()
            .get(endpoint);
    }

    @Then("response status should be {int}")
    public void verifyStatusCode(int statusCode) {
        response.then().statusCode(statusCode);
    }

    @Then("response should contain {string}")
    public void verifyResponseContent(String content) {
        response.then().body(containsString(content));
    }
}
```

### **Allure Reporting**
```java
@Test
@DisplayName("Create new user via API")
@Description("Test user creation endpoint")
public void testCreateUser() {
    given()
        .contentType(ContentType.JSON)
        .body("{\"name\": \"John\", \"email\": \"john@test.com\"}")
    .when()
        .post("/api/users")
    .then()
        .statusCode(201)
        .body("id", notNullValue());
}
```


## Полезные ссылки
- [Официальная документация REST Assured](https://rest-assured.io/)
- [GitHub репозиторий](https://github.com/rest-assured/rest-assured)
- [Spring Boot интеграция](https://docs.spring.io/spring-boot/docs/current/reference/html/io.html#io.testing.rest-assured)
- [Hamcrest matchers](../../testing/unit-testing/junit/hamcrest.md)

## См. также
- [WireMock](java-wiremock.md) — для **mock**-серверов
- [Testcontainers](java-testcontainers.md) — для интеграционных тестов
- [JUnit Advanced](../../testing/unit-testing/junit/junit-advanced.md) — для **unit** тестирования

