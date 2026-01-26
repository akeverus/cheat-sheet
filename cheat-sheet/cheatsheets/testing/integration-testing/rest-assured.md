# REST Assured для Java

Комплексное руководство по использованию REST Assured для тестирования REST API в Java: fluent API, response validation, authentication, data-driven testing, Spring Boot интеграция.

**Дата последнего обновления:** 2026-01-21

## Полезные ссылки

### Официальная документация
- [REST Assured Documentation](https://rest-assured.io/) - Основная документация
- [REST Assured GitHub](https://github.com/rest-assured/rest-assured) - Исходный код и примеры
- [REST Assured Javadoc](https://www.javadoc.io/doc/io.rest-assured/rest-assured/latest/index.html) - API документация

### Java интеграции
- [REST Assured with JUnit 5](https://github.com/rest-assured/rest-assured/wiki/Usage#junit-5-support) - JUnit 5 интеграция
- [REST Assured with Spring](https://github.com/rest-assured/rest-assured/wiki/Usage#spring-mock-mvc) - Spring интеграция
- [REST Assured with WireMock](https://github.com/rest-assured/rest-assured/wiki/Usage#wiremock) - WireMock интеграция

### Best practices
- [REST Assured Best Practices](https://github.com/rest-assured/rest-assured/wiki/Usage#best-practices) - Лучшие практики
- [Given-When-Then Pattern](https://martinfowler.com/bliki/GivenWhenThen.html) - BDD паттерн
- [API Testing Guidelines](https://www.apimatic.io/blog/2020/11/api-testing-best-practices) - Лучшие практики API тестирования

### См. также
- `testing/junit-advanced.md` - JUnit расширения
- `testing/wiremock.md` - HTTP mocking
- `testing/assertj.md` - Assertions
- `spring-testing.md` - Spring testing

## Содержание

- [Введение в REST Assured](#введение-в-rest-assured)
- [Basic API testing](#basic-api-testing)
- [Response validation](#response-validation)
- [Request specification](#request-specification)
- [Authentication](#authentication)
- [Data-driven testing](#data-driven-testing)
- [Error handling](#error-handling)
- [Spring Boot integration](#spring-boot-integration)
- [Advanced scenarios](#advanced-scenarios)
- [Performance testing](#performance-testing)
- [Best practices](#best-practices)
- [Troubleshooting](#troubleshooting)
- [Заключение](#заключение)

## Введение в REST Assured

**REST Assured** — это Java DSL для упрощения тестирования REST API. Он предоставляет fluent API для создания HTTP запросов и валидации ответов, следуя BDD паттерну Given-When-Then.

### Почему REST Assured?

REST Assured предоставляет мощные возможности для тестирования REST API:

1. **Fluent API** — читаемый и текучий синтаксис
2. **BDD Style** — Given-When-Then паттерн
3. **Rich Validations** — множество встроенных проверок
4. **JSON/XML Support** — встроенная работа с JSON и XML
5. **Authentication** — поддержка различных типов аутентификации
6. **Reusable Specs** — переиспользуемые спецификации запросов
7. **Spring Integration** — интеграция с Spring Boot
8. **Data-driven** — поддержка parameterized тестов

### Maven зависимости

**REST Assured** имеет модульную архитектуру, где core модуль предоставляет основную функциональность, а дополнительные модули расширяют возможности для различных типов контента и интеграций.

#### Core REST Assured (обязательный)

**rest-assured** — основной модуль с fluent API для тестирования REST API.

```xml
<dependency>
    <groupId>io.rest-assured</groupId>
    <artifactId>rest-assured</artifactId>
    <version>5.3.0</version>
    <scope>test</scope>
</dependency>
```

**Что включает rest-assured:**
- **Fluent API** — Given-When-Then синтаксис
- **HTTP methods** — GET, POST, PUT, DELETE, PATCH, HEAD, OPTIONS
- **Response validation** — проверки статус кодов, headers, body
- **JSON validation** — встроенная работа с JSON
- **Authentication** — Basic, OAuth, API keys
- **Request specification** — переиспользуемые конфигурации
- **Response specification** — шаблоны валидации ответов
- **Path parameters** — параметры в URL
- **Query parameters** — GET параметры
- **Form parameters** — form-encoded данные

#### JSON/XML Path support

**json-path** — XPath-подобные запросы к JSON данным:

```xml
<dependency>
    <groupId>io.rest-assured</groupId>
    <artifactId>json-path</artifactId>
    <version>5.3.0</version>
    <scope>test</scope>
</dependency>
```

**Возможности JSON Path:**
- **JSON navigation** — навигация по JSON структуре
- **Value extraction** — извлечение значений из JSON
- **Filtering** — фильтрация JSON массивов
- **Complex queries** — сложные запросы к JSON

**xml-path** — XPath запросы к XML данным:

```xml
<dependency>
    <groupId>io.rest-assured</groupId>
    <artifactId>xml-path</artifactId>
    <version>5.3.0</version>
    <scope>test</scope>
</dependency>
```

**Возможности XML Path:**
- **XML navigation** — навигация по XML структуре
- **Namespace support** — работа с XML namespaces
- **XPath expressions** — полная поддержка XPath 1.0
- **Value extraction** — извлечение данных из XML

#### Spring Mock MVC модуль

**spring-mock-mvc** — интеграция с Spring MVC для тестирования контроллеров без HTTP:

```xml
<dependency>
    <groupId>io.rest-assured</groupId>
    <artifactId>spring-mock-mvc</artifactId>
    <version>5.3.0</version>
    <scope>test</scope>
</dependency>
```

**Spring Mock MVC возможности:**
- **Controller testing** — тестирование Spring MVC контроллеров
- **Mock environment** — без реального HTTP сервера
- **Spring context** — полный Spring контекст в тестах
- **Security testing** — интеграция с Spring Security

#### Kotlin модуль

**kotlin-extensions** — Kotlin extensions для более idiomatic кода:

```xml
<dependency>
    <groupId>io.rest-assured</groupId>
    <artifactId>kotlin-extensions</artifactId>
    <version>5.3.0</version>
    <scope>test</scope>
</dependency>
```

**Kotlin extensions features:**
- **Type-safe builders** — типобезопасные билдеры
- **Inline functions** — Kotlin inline функции
- **DSL syntax** — domain-specific language для Kotlin
- **Null safety** — Kotlin null safety гарантии

#### Scala модуль

**scala-support** — Scala интеграция:

```xml
<dependency>
    <groupId>io.rest-assured</groupId>
    <artifactId>scala-support</artifactId>
    <version>5.3.0</version>
    <scope>test</scope>
</dependency>
```

#### JUnit 5 интеграция

**JUnit 5** (рекомендуемый testing framework):

```xml
<dependency>
    <groupId>org.junit.jupiter</groupId>
    <artifactId>junit-jupiter</artifactId>
    <version>5.10.0</version>
    <scope>test</scope>
</dependency>
```

**TestNG** (альтернативный framework):

```xml
<dependency>
    <groupId>org.testng</groupId>
    <artifactId>testng</artifactId>
    <version>7.8.0</version>
    <scope>test</scope>
</dependency>
```

#### Spring Boot интеграция

**Spring Boot Starter Test включает REST Assured:**

```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-test</artifactId>
    <scope>test</scope>
    <!-- REST Assured уже включен -->
</dependency>
```

**Что включает spring-boot-starter-test:**
- **JUnit 5** — основной testing framework
- **REST Assured** — API testing (уже включен!)
- **Mockito** — mocking framework
- **JSONassert** — JSON assertions
- **Spring Test** — Spring testing utilities

#### Gradle зависимости

**Для Gradle проектов с подробными конфигурациями:**

```gradle
dependencies {
    // Core REST Assured - основной модуль для API тестирования
    testImplementation 'io.rest-assured:rest-assured:5.3.0'

    // JSON Path - для работы с JSON ответами
    testImplementation 'io.rest-assured:json-path:5.3.0'

    // XML Path - для работы с XML ответами
    testImplementation 'io.rest-assured:xml-path:5.3.0'

    // Spring Mock MVC - для тестирования Spring контроллеров
    testImplementation 'io.rest-assured:spring-mock-mvc:5.3.0'

    // Kotlin extensions (опционально)
    testImplementation 'io.rest-assured:kotlin-extensions:5.3.0'

    // JUnit 5
    testImplementation 'org.junit.jupiter:junit-jupiter:5.10.0'
    testRuntimeOnly 'org.junit.platform:junit-platform-launcher'
}
```

#### Version management

**Рекомендуется использовать properties для версий:**

```gradle
ext {
    restAssuredVersion = '5.3.0'
    junitVersion = '5.10.0'
}

dependencies {
    testImplementation "io.rest-assured:rest-assured:${restAssuredVersion}"
    testImplementation "io.rest-assured:json-path:${restAssuredVersion}"
    testImplementation "io.rest-assured:xml-path:${restAssuredVersion}"
    testImplementation "org.junit.jupiter:junit-jupiter:${junitVersion}"
}
```

#### BOM (Bill of Materials)

**Для Maven проектов рекомендуется использовать REST Assured BOM:**

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

<dependencies>
    <!-- Версии будут управляться BOM -->
    <dependency>
        <groupId>io.rest-assured</groupId>
        <artifactId>rest-assured</artifactId>
        <scope>test</scope>
    </dependency>

    <dependency>
        <groupId>io.rest-assured</groupId>
        <artifactId>json-path</artifactId>
        <scope>test</scope>
    </dependency>
</dependencies>
```

#### Миграция между версиями

**REST Assured 3.x → 4.x → 5.x:**

**Ключевые изменения в 4.x:**
- **Java 8+** — минимальная версия Java 8
- **Groovy removal** — удалена зависимость от Groovy
- **Performance improvements** — улучшенная производительность
- **New JSON parser** — новый JSON парсер (Jackson)

**Ключевые изменения в 5.x:**
- **Java 11+** — минимальная версия Java 11
- **HTTP Client update** — обновленный HTTP клиент
- **Security improvements** — улучшения безопасности
- **New features** — новые возможности для тестирования

```xml
<!-- REST Assured 3.x (legacy) -->
<dependency>
    <groupId>com.jayway.restassured</groupId>
    <artifactId>rest-assured</artifactId>
    <version>3.3.0</version>
</dependency>

<!-- REST Assured 4.x/5.x (current) -->
<dependency>
    <groupId>io.rest-assured</groupId>
    <artifactId>rest-assured</artifactId>
    <version>5.3.0</version>
</dependency>
```

#### IDE Configuration

**IntelliJ IDEA:**
1. **File → Settings → Build, Execution, Deployment → Build Tools → Gradle**
2. **REST Assured методы будут автоматически распознаны**
3. **Static imports работают из коробки**

**Eclipse:**
1. **Help → Eclipse Marketplace**
2. **Find**: "REST Assured"
3. **Install**: REST Assured Eclipse integration

**VS Code:**
```json
{
    "java.test.config": {
        "name": "JUnit Jupiter + REST Assured",
        "type": "junit",
        "request": "launch",
        "mainClass": "org.junit.platform.console.ConsoleLauncher",
        "args": ["--scan-classpath"],
        "vmargs": ["-ea"],
        "dependencies": [
            "io.rest-assured:rest-assured:5.3.0"
        ]
    }
}
```

#### Troubleshooting зависимостей

**Проблема: ClassNotFoundException**

```bash
# Проверить dependency tree
mvn dependency:tree | grep rest-assured

# Gradle dependencies
gradle dependencies --configuration testRuntimeClasspath
```

**Проблема: Version conflicts**

```xml
<!-- Исключить старую версию -->
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-test</artifactId>
    <exclusions>
        <exclusion>
            <groupId>io.rest-assured</groupId>
            <artifactId>rest-assured</artifactId>
        </exclusion>
    </exclusions>
</dependency>

<!-- Добавить нужную версию -->
<dependency>
    <groupId>io.rest-assured</groupId>
    <artifactId>rest-assured</artifactId>
    <version>5.3.0</version>
    <scope>test</scope>
</dependency>
```

**Проблема: SSL/TLS issues**

```java
// Отключить SSL валидацию для development
RestAssured.config = RestAssured.config()
    .sslConfig(new SSLConfig().relaxedHTTPSValidation());

// Или использовать custom truststore
RestAssured.config = RestAssured.config()
    .sslConfig(SSLConfig.sslConfig()
        .trustStore("path/to/truststore.jks", "password"));
```

**Проблема: Encoding issues**

```java
// Настроить кодировку
RestAssured.config = RestAssured.config()
    .encoderConfig(EncoderConfig.encoderConfig()
        .defaultContentCharset("UTF-8")
        .defaultQueryParameterCharset("UTF-8"));
```

**Проблема: Timeout issues**

```java
// Настроить таймауты
RestAssured.config = RestAssured.config()
    .httpClient(HttpClientConfig.httpClientConfig()
        .setParam("http.connection.timeout", 5000)
        .setParam("http.socket.timeout", 10000));
```

### Basic usage

#### Простое использование
```java
@SpringBootTest
public class RestAssuredBasicTest {

    @Test
    void testGetUsers() {
        given()
            .when()
                .get("/api/users")
            .then()
                .statusCode(200)
                .body("size()", greaterThan(0));
    }

    @Test
    void testCreateUser() {
        String requestBody = """
            {
                "name": "John Doe",
                "email": "john@example.com"
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
                .body("name", equalTo("John Doe"));
    }
}
```

## Basic API testing

### HTTP methods

#### Различные HTTP методы
```java
@SpringBootTest
public class HttpMethodsTest {

    private static final String BASE_URL = "http://localhost:8080/api";

    @Test
    void testGetMethod() {
        given()
            .baseUri(BASE_URL)
            .when()
                .get("/users")
            .then()
                .statusCode(200)
                .contentType(ContentType.JSON)
                .body("users.size()", greaterThan(0));
    }

    @Test
    void testPostMethod() {
        Map<String, Object> user = Map.of(
            "name", "Jane Doe",
            "email", "jane@example.com"
        );

        given()
            .baseUri(BASE_URL)
            .contentType(ContentType.JSON)
            .body(user)
            .when()
                .post("/users")
            .then()
                .statusCode(201)
                .body("name", equalTo("Jane Doe"))
                .body("email", equalTo("jane@example.com"));
    }

    @Test
    void testPutMethod() {
        Map<String, Object> updateData = Map.of(
            "name", "Updated Name",
            "status", "ACTIVE"
        );

        given()
            .baseUri(BASE_URL)
            .contentType(ContentType.JSON)
            .body(updateData)
            .when()
                .put("/users/1")
            .then()
                .statusCode(200)
                .body("name", equalTo("Updated Name"))
                .body("status", equalTo("ACTIVE"));
    }

    @Test
    void testDeleteMethod() {
        given()
            .baseUri(BASE_URL)
            .when()
                .delete("/users/1")
            .then()
                .statusCode(204);
    }

    @Test
    void testPatchMethod() {
        Map<String, Object> patchData = Map.of("status", "INACTIVE");

        given()
            .baseUri(BASE_URL)
            .contentType(ContentType.JSON)
            .body(patchData)
            .when()
                .patch("/users/1")
            .then()
                .statusCode(200)
                .body("status", equalTo("INACTIVE"));
    }

    @Test
    void testHeadMethod() {
        given()
            .baseUri(BASE_URL)
            .when()
                .head("/users")
            .then()
                .statusCode(200)
                .header("Content-Length", notNullValue());
    }

    @Test
    void testOptionsMethod() {
        given()
            .baseUri(BASE_URL)
            .when()
                .options("/users")
            .then()
                .statusCode(200)
                .header("Allow", containsString("GET"))
                .header("Allow", containsString("POST"));
    }
}
```

### Content types

#### Работа с различными content types
```java
@SpringBootTest
public class ContentTypesTest {

    @Test
    void testJsonContent() {
        given()
            .contentType(ContentType.JSON)
            .body("{ \"name\": \"Test User\" }")
            .when()
                .post("/api/users")
            .then()
                .statusCode(201)
                .contentType(ContentType.JSON)
                .body("name", equalTo("Test User"));
    }

    @Test
    void testXmlContent() {
        String xmlBody = """
            <user>
                <name>Test User</name>
                <email>test@example.com</email>
            </user>
            """;

        given()
            .contentType(ContentType.XML)
            .body(xmlBody)
            .when()
                .post("/api/users")
            .then()
                .statusCode(201)
                .contentType(ContentType.XML)
                .body("user.name", equalTo("Test User"));
    }

    @Test
    void testFormData() {
        given()
            .contentType(ContentType.URLENC)
            .formParam("name", "Test User")
            .formParam("email", "test@example.com")
            .when()
                .post("/api/users")
            .then()
                .statusCode(201);
    }

    @Test
    void testMultipartData() {
        given()
            .contentType(ContentType.MULTIPART)
            .multiPart("file", new File("test.txt"), "text/plain")
            .multiPart("metadata", "{ \"type\": \"document\" }", "application/json")
            .when()
                .post("/api/upload")
            .then()
                .statusCode(200);
    }

    @Test
    void testTextContent() {
        given()
            .contentType(ContentType.TEXT)
            .body("Simple text content")
            .when()
                .post("/api/logs")
            .then()
                .statusCode(200);
    }

    @Test
    void testBinaryContent() {
        byte[] binaryData = "Binary content".getBytes();

        given()
            .contentType(ContentType.BINARY)
            .body(binaryData)
            .when()
                .post("/api/binary")
            .then()
                .statusCode(200);
    }
}
```

## Response validation

### Status codes and headers

#### Проверка статус кодов и заголовков
```java
@SpringBootTest
public class ResponseValidationTest {

    @Test
    void testStatusCodes() {
        // Successful responses
        get("/api/users").then().statusCode(200);
        post("/api/users").then().statusCode(201);
        put("/api/users/1").then().statusCode(200);
        delete("/api/users/1").then().statusCode(204);

        // Client errors
        get("/api/users/999").then().statusCode(404);
        post("/api/users").then().statusCode(400); // Assuming validation error

        // Server errors
        get("/api/error").then().statusCode(500);
    }

    @Test
    void testHeaderValidation() {
        given()
            .when()
                .get("/api/users")
            .then()
                .header("Content-Type", equalTo("application/json"))
                .header("Cache-Control", containsString("max-age"))
                .header("X-API-Version", notNullValue())
                .header("Content-Length", not(emptyString()));
    }

    @Test
    void testResponseTime() {
        given()
            .when()
                .get("/api/users")
            .then()
                .time(lessThan(1000L)) // Less than 1 second
                .statusCode(200);
    }

    @Test
    void testContentLength() {
        given()
            .when()
                .get("/api/users")
            .then()
                .header("Content-Length", Integer::parseInt, greaterThan(0));
    }

    @Test
    void testCorsHeaders() {
        given()
            .header("Origin", "http://localhost:3000")
            .when()
                .options("/api/users")
            .then()
                .header("Access-Control-Allow-Origin", equalTo("http://localhost:3000"))
                .header("Access-Control-Allow-Methods", containsString("GET"))
                .header("Access-Control-Allow-Headers", containsString("Content-Type"));
    }
}
```

### JSON response validation

#### Работа с JSON ответами
```java
@SpringBootTest
public class JsonValidationTest {

    @Test
    void testJsonStructure() {
        given()
            .when()
                .get("/api/users/1")
            .then()
                .statusCode(200)
                .body("id", notNullValue())
                .body("name", notNullValue())
                .body("email", matchesPattern(".+@.+"))
                .body("createdAt", notNullValue());
    }

    @Test
    void testJsonArray() {
        given()
            .when()
                .get("/api/users")
            .then()
                .statusCode(200)
                .body("size()", greaterThan(0))
                .body("[0].id", notNullValue())
                .body("[0].name", notNullValue())
                .body("findAll { it.status == 'ACTIVE' }.size()", greaterThan(0));
    }

    @Test
    void testNestedJson() {
        given()
            .when()
                .get("/api/orders/1")
            .then()
                .statusCode(200)
                .body("customer.name", notNullValue())
                .body("customer.email", matchesPattern(".+@.+"))
                .body("items.size()", greaterThan(0))
                .body("items[0].productName", notNullValue())
                .body("items[0].price", greaterThan(0))
                .body("totalAmount", greaterThan(0));
    }

    @Test
    void testJsonPathExpressions() {
        given()
            .when()
                .get("/api/products")
            .then()
                .statusCode(200)
                .body("findAll { it.category == 'Electronics' }.size()", greaterThan(0))
                .body("find { it.price == max(it.price) }.name", notNullValue())
                .body("sum { it.price }", greaterThan(0));
    }

    @Test
    void testJsonSchemaValidation() {
        given()
            .when()
                .get("/api/users/1")
            .then()
                .statusCode(200)
                .body(matchesJsonSchema(new File("src/test/resources/schemas/user.json")));
    }

    @Test
    void testJsonContains() {
        given()
            .when()
                .get("/api/config")
            .then()
                .statusCode(200)
                .body("$", hasKey("database"))
                .body("$", hasKey("cache"))
                .body("database", hasKey("url"))
                .body("database", hasKey("poolSize"));
    }
}
```

### XML response validation

#### Работа с XML ответами
```java
@SpringBootTest
public class XmlValidationTest {

    @Test
    void testXmlStructure() {
        given()
            .accept(ContentType.XML)
            .when()
                .get("/api/users/1")
            .then()
                .statusCode(200)
                .contentType(ContentType.XML)
                .body("user.id", notNullValue())
                .body("user.name", notNullValue())
                .body("user.email", matchesPattern(".+@.+"));
    }

    @Test
    void testXmlNamespaces() {
        given()
            .accept(ContentType.XML)
            .when()
                .get("/api/data")
            .then()
                .statusCode(200)
                .body("data:item", notNullValue())
                .body("data:item.name", notNullValue());
    }

    @Test
    void testXmlAttributes() {
        given()
            .accept(ContentType.XML)
            .when()
                .get("/api/products")
            .then()
                .statusCode(200)
                .body("products.product.@id", notNullValue())
                .body("products.product.@category", notNullValue());
    }

    @Test
    void testXmlPathExpressions() {
        given()
            .accept(ContentType.XML)
            .when()
                .get("/api/orders")
            .then()
                .statusCode(200)
                .body("orders.order.size()", greaterThan(0))
                .body("orders.order[0].customer.name", notNullValue());
    }
}
```

## Request specification

### Reusable specifications

#### Создание переиспользуемых спецификаций
```java
@SpringBootTest
public class RequestSpecificationTest {

    private static final String BASE_URI = "http://localhost:8080";
    private static final String API_PATH = "/api";

    private RequestSpecification baseRequest;
    private ResponseSpecification successResponse;
    private ResponseSpecification errorResponse;

    @BeforeEach
    void setUpSpecifications() {
        // Base request specification
        baseRequest = given()
            .baseUri(BASE_URI)
            .basePath(API_PATH)
            .contentType(ContentType.JSON)
            .accept(ContentType.JSON)
            .header("X-API-Key", "test-key")
            .log().all(); // Log all requests

        // Success response specification
        successResponse = expect()
            .statusCode(200)
            .contentType(ContentType.JSON)
            .time(lessThan(2000L))
            .log().all(); // Log all responses

        // Error response specification
        errorResponse = expect()
            .statusCode(between(400, 599))
            .contentType(ContentType.JSON)
            .body("error", notNullValue())
            .body("message", notNullValue());
    }

    @Test
    void testGetUsers() {
        given(baseRequest)
            .when()
                .get("/users")
            .then()
                .spec(successResponse)
                .body("size()", greaterThan(0));
    }

    @Test
    void testCreateUser() {
        Map<String, Object> user = Map.of(
            "name", "Test User",
            "email", "test@example.com"
        );

        given(baseRequest)
            .body(user)
            .when()
                .post("/users")
            .then()
                .statusCode(201)
                .body("id", notNullValue())
                .body("name", equalTo("Test User"));
    }

    @Test
    void testInvalidRequest() {
        given(baseRequest)
            .body("{}") // Empty request
            .when()
                .post("/users")
            .then()
                .spec(errorResponse)
                .body("error", equalTo("VALIDATION_ERROR"));
    }
}
```

### Advanced specifications

#### Комплексные спецификации
```java
@SpringBootTest
public class AdvancedSpecificationsTest {

    private RequestSpecification authenticatedRequest;
    private RequestSpecification adminRequest;
    private ResponseSpecification paginatedResponse;
    private ResponseSpecification detailedResponse;

    @BeforeEach
    void setUpAdvancedSpecifications() {
        // Authenticated request
        authenticatedRequest = given()
            .auth().oauth2(getAccessToken())
            .header("Authorization", "Bearer " + getAccessToken())
            .contentType(ContentType.JSON);

        // Admin request
        adminRequest = given(authenticatedRequest)
            .header("X-Role", "ADMIN")
            .header("X-Permissions", "FULL_ACCESS");

        // Paginated response
        paginatedResponse = expect()
            .statusCode(200)
            .header("X-Total-Count", notNullValue())
            .header("X-Page-Size", notNullValue())
            .header("X-Current-Page", notNullValue())
            .body("size()", lessThanOrEqualTo(50)); // Max page size

        // Detailed response
        detailedResponse = expect()
            .statusCode(200)
            .body("id", notNullValue())
            .body("createdAt", notNullValue())
            .body("updatedAt", notNullValue())
            .body("version", notNullValue());
    }

    @Test
    void testPaginatedUsers() {
        given(authenticatedRequest)
            .queryParam("page", 1)
            .queryParam("size", 20)
            .when()
                .get("/users")
            .then()
                .spec(paginatedResponse)
                .header("X-Total-Count", Integer::parseInt, greaterThan(0));
    }

    @Test
    void testUserDetails() {
        given(authenticatedRequest)
            .when()
                .get("/users/1")
            .then()
                .spec(detailedResponse)
                .body("name", notNullValue())
                .body("email", matchesPattern(".+@.+"));
    }

    @Test
    void testAdminOperations() {
        Map<String, Object> systemConfig = Map.of(
            "maintenanceMode", true,
            "maxUsers", 1000
        );

        given(adminRequest)
            .body(systemConfig)
            .when()
                .put("/admin/config")
            .then()
                .statusCode(200)
                .body("maintenanceMode", equalTo(true))
                .body("maxUsers", equalTo(1000));
    }

    private String getAccessToken() {
        // Implementation to get access token
        return "test-token";
    }
}
```

## Authentication

### Basic authentication

#### Базовая аутентификация
```java
@SpringBootTest
public class AuthenticationTest {

    @Test
    void testBasicAuth() {
        given()
            .auth().basic("username", "password")
            .when()
                .get("/api/protected")
            .then()
                .statusCode(200);
    }

    @Test
    void testDigestAuth() {
        given()
            .auth().digest("username", "password")
            .when()
                .get("/api/protected")
            .then()
                .statusCode(200);
    }

    @Test
    void testPreemptiveBasicAuth() {
        given()
            .auth().preemptive().basic("username", "password")
            .when()
                .get("/api/protected")
            .then()
                .statusCode(200);
    }

    @Test
    void testFormAuth() {
        given()
            .auth().form("username", "password")
            .when()
                .get("/api/protected")
            .then()
                .statusCode(200);
    }

    @Test
    void testCustomAuth() {
        given()
            .header("Authorization", "Custom token123")
            .when()
                .get("/api/protected")
            .then()
                .statusCode(200);
    }
}
```

### OAuth authentication

#### OAuth 1.0 и 2.0
```java
@SpringBootTest
public class OAuthTest {

    @Test
    void testOAuth1() {
        given()
            .auth().oauth(
                "consumerKey",
                "consumerSecret",
                "accessToken",
                "tokenSecret")
            .when()
                .get("/api/oauth1/protected")
            .then()
                .statusCode(200);
    }

    @Test
    void testOAuth2() {
        String accessToken = obtainAccessToken();

        given()
            .auth().oauth2(accessToken)
            .when()
                .get("/api/oauth2/protected")
            .then()
                .statusCode(200);
    }

    @Test
    void testBearerToken() {
        given()
            .header("Authorization", "Bearer " + getBearerToken())
            .when()
                .get("/api/bearer/protected")
            .then()
                .statusCode(200);
    }

    @Test
    void testJwtToken() {
        String jwtToken = generateJwtToken();

        given()
            .header("Authorization", "Bearer " + jwtToken)
            .when()
                .get("/api/jwt/protected")
            .then()
                .statusCode(200)
                .body("user", notNullValue())
                .body("roles", hasItem("USER"));
    }

    @Test
    void testApiKeyAuth() {
        given()
            .queryParam("api_key", "secret-api-key")
            .when()
                .get("/api/key/protected")
            .then()
                .statusCode(200);
    }

    @Test
    void testHeaderApiKey() {
        given()
            .header("X-API-Key", "secret-api-key")
            .when()
                .get("/api/header-key/protected")
            .then()
                .statusCode(200);
    }

    private String obtainAccessToken() {
        // OAuth2 flow to obtain access token
        return given()
            .formParam("grant_type", "client_credentials")
            .formParam("client_id", "client-id")
            .formParam("client_secret", "client-secret")
            .when()
                .post("/oauth/token")
            .then()
                .extract().path("access_token");
    }

    private String getBearerToken() {
        // Implementation to get bearer token
        return "bearer-token";
    }

    private String generateJwtToken() {
        // Implementation to generate JWT token
        return "jwt-token";
    }
}
```

## Data-driven testing

### Parameterized tests

#### Data-driven подход
```java
@SpringBootTest
public class DataDrivenTest {

    static Stream<Arguments> userCreationData() {
        return Stream.of(
            Arguments.of("John Doe", "john@example.com", true),
            Arguments.of("Jane Smith", "jane@example.com", true),
            Arguments.of("", "invalid@example.com", false),
            Arguments.of("Valid Name", "", false),
            Arguments.of("Valid Name", "invalid-email", false)
        );
    }

    @ParameterizedTest
    @MethodSource("userCreationData")
    void testUserCreation(String name, String email, boolean shouldSucceed) {
        Map<String, Object> user = Map.of(
            "name", name,
            "email", email
        );

        RequestSpecification request = given()
            .contentType(ContentType.JSON)
            .body(user);

        if (shouldSucceed) {
            request.when()
                .post("/api/users")
            .then()
                .statusCode(201)
                .body("name", equalTo(name))
                .body("email", equalTo(email));
        } else {
            request.when()
                .post("/api/users")
            .then()
                .statusCode(400)
                .body("error", notNullValue());
        }
    }

    static Stream<Arguments> httpStatusData() {
        return Stream.of(
            Arguments.of("/api/users", "GET", 200),
            Arguments.of("/api/users", "POST", 201),
            Arguments.of("/api/users/1", "PUT", 200),
            Arguments.of("/api/users/1", "DELETE", 204),
            Arguments.of("/api/nonexistent", "GET", 404)
        );
    }

    @ParameterizedTest
    @MethodSource("httpStatusData")
    void testHttpMethods(String path, String method, int expectedStatus) {
        switch (method) {
            case "GET" -> get(path).then().statusCode(expectedStatus);
            case "POST" -> given().post(path).then().statusCode(expectedStatus);
            case "PUT" -> given().put(path).then().statusCode(expectedStatus);
            case "DELETE" -> delete(path).then().statusCode(expectedStatus);
        }
    }

    static Stream<Arguments> validationErrorData() {
        return Stream.of(
            Arguments.of(Map.of(), "VALIDATION_ERROR"),
            Arguments.of(Map.of("name", ""), "NAME_REQUIRED"),
            Arguments.of(Map.of("email", ""), "EMAIL_REQUIRED"),
            Arguments.of(Map.of("name", "Test", "email", "invalid"), "INVALID_EMAIL")
        );
    }

    @ParameterizedTest
    @MethodSource("validationErrorData")
    void testValidationErrors(Map<String, Object> requestData, String expectedError) {
        given()
            .contentType(ContentType.JSON)
            .body(requestData)
            .when()
                .post("/api/users")
            .then()
                .statusCode(400)
                .body("error", equalTo(expectedError));
    }
}
```

### CSV data provider

#### Тесты на основе CSV данных
```java
@SpringBootTest
public class CsvDataDrivenTest {

    @Test
    void testUsersFromCsv() {
        // Read test data from CSV
        List<Map<String, String>> testData = readCsvData("users-test-data.csv");

        for (Map<String, String> userData : testData) {
            given()
                .contentType(ContentType.JSON)
                .body(userData)
                .when()
                    .post("/api/users")
                .then()
                    .statusCode(Integer.parseInt(userData.get("expectedStatus")))
                    .body("name", equalTo(userData.get("name")))
                    .body("email", equalTo(userData.get("email")));
        }
    }

    @Test
    void testApiResponsesFromCsv() {
        List<Map<String, String>> testCases = readCsvData("api-test-cases.csv");

        for (Map<String, String> testCase : testCases) {
            RequestSpecification request = given()
                .baseUri(testCase.get("baseUri"))
                .header("Authorization", testCase.get("authHeader"));

            Response response = request.when()
                .get(testCase.get("endpoint"));

            // Validate response based on CSV data
            response.then()
                .statusCode(Integer.parseInt(testCase.get("expectedStatus")))
                .body(testCase.get("jsonPath"), equalTo(testCase.get("expectedValue")));
        }
    }

    private List<Map<String, String>> readCsvData(String filename) {
        // Implementation to read CSV file
        // Return list of maps representing test data
        return List.of();
    }
}
```

## Error handling

### Exception testing

#### Тестирование исключений
```java
@SpringBootTest
public class ErrorHandlingTest {

    @Test
    void testConnectionTimeout() {
        given()
            .timeout(1000) // 1 second timeout
            .when()
                .get("/api/slow-endpoint")
            .then()
                .statusCode(anyOf(is(200), is(504))); // Success or timeout
    }

    @Test
    void testInvalidSslCertificate() {
        given()
            .relaxedHTTPSValidation() // Ignore SSL certificate errors
            .when()
                .get("https://self-signed.example.com/api/data")
            .then()
                .statusCode(200);
    }

    @Test
    void testNetworkErrors() {
        // Test with invalid host
        when()
            .get("http://invalid-host.example.com/api/data")
        .then()
            .statusCode(anyOf(is(404), is(500))); // Connection error or not found
    }

    @Test
    void testServerErrors() {
        given()
            .when()
                .get("/api/error")
            .then()
                .statusCode(500)
                .body("error", equalTo("INTERNAL_SERVER_ERROR"))
                .body("message", containsString("Something went wrong"));
    }

    @Test
    void testClientErrors() {
        // Invalid request data
        Map<String, Object> invalidData = Map.of(
            "name", "",
            "email", "invalid-email"
        );

        given()
            .contentType(ContentType.JSON)
            .body(invalidData)
            .when()
                .post("/api/users")
            .then()
                .statusCode(400)
                .body("errors.name", notNullValue())
                .body("errors.email", notNullValue());
    }

    @Test
    void testUnauthorizedAccess() {
        given()
            .when()
                .get("/api/admin/users")
            .then()
                .statusCode(401)
                .header("WWW-Authenticate", notNullValue());
    }

    @Test
    void testForbiddenAccess() {
        given()
            .auth().basic("user", "password") // Non-admin user
            .when()
                .get("/api/admin/config")
            .then()
                .statusCode(403)
                .body("error", equalTo("INSUFFICIENT_PERMISSIONS"));
    }

    @Test
    void testRateLimiting() {
        // Make multiple requests quickly
        for (int i = 0; i < 10; i++) {
            given()
                .when()
                    .get("/api/data")
                .then()
                    .statusCode(anyOf(is(200), is(429))); // Success or rate limited
        }
    }
}
```

## Spring Boot integration

### Spring MockMvc

#### Интеграция с Spring MVC
```java
@SpringBootTest
@AutoConfigureMockMvc
public class SpringMockMvcTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void testWithMockMvc() {
        RestAssuredMockMvc.given()
            .mockMvc(mockMvc)
            .contentType(ContentType.JSON)
            .body("{ \"name\": \"Test User\" }")
            .when()
                .post("/api/users")
            .then()
                .statusCode(201)
                .body("name", equalTo("Test User"));
    }

    @Test
    void testMvcWithAuthentication() {
        RestAssuredMockMvc.given()
            .mockMvc(mockMvc)
            .auth().basic("admin", "password")
            .when()
                .get("/api/admin/users")
            .then()
                .statusCode(200)
                .body("size()", greaterThan(0));
    }

    @Test
    void testMvcWithWebTestClient() {
        // Alternative approach with WebTestClient
        WebTestClient.bindToController(new UserController())
            .build()
            .post()
            .uri("/api/users")
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue("{ \"name\": \"Test User\" }")
            .exchange()
            .expectStatus().isCreated()
            .expectBody()
                .jsonPath("$.name").isEqualTo("Test User");
    }
}
```

### Spring Security

#### Тестирование с Spring Security
```java
@SpringBootTest
@AutoConfigureMockMvc
@WithMockUser(username = "testuser", roles = {"USER"})
public class SpringSecurityTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void testAuthenticatedUserAccess() {
        RestAssuredMockMvc.given()
            .mockMvc(mockMvc)
            .when()
                .get("/api/user/profile")
            .then()
                .statusCode(200)
                .body("username", equalTo("testuser"));
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void testAdminAccess() {
        RestAssuredMockMvc.given()
            .mockMvc(mockMvc)
            .when()
                .get("/api/admin/config")
            .then()
                .statusCode(200);
    }

    @Test
    void testUnauthenticatedAccess() {
        RestAssuredMockMvc.given()
            .mockMvc(mockMvc)
            .when()
                .get("/api/user/profile")
            .then()
                .statusCode(401);
    }

    @Test
    void testJwtAuthentication() {
        String token = generateJwtToken("testuser", List.of("USER"));

        RestAssuredMockMvc.given()
            .mockMvc(mockMvc)
            .header("Authorization", "Bearer " + token)
            .when()
                .get("/api/user/profile")
            .then()
                .statusCode(200)
                .body("username", equalTo("testuser"));
    }

    private String generateJwtToken(String username, List<String> roles) {
        // Implementation to generate JWT token for testing
        return "jwt-token";
    }
}
```

## Advanced scenarios

### File upload/download

#### Работа с файлами
```java
@SpringBootTest
public class FileOperationsTest {

    @Test
    void testFileUpload() {
        given()
            .contentType(ContentType.MULTIPART)
            .multiPart("file", new File("test-document.pdf"), "application/pdf")
            .multiPart("metadata", "{\"type\": \"document\", \"category\": \"test\"}", "application/json")
            .when()
                .post("/api/upload")
            .then()
                .statusCode(200)
                .body("fileId", notNullValue())
                .body("filename", equalTo("test-document.pdf"))
                .body("size", greaterThan(0));
    }

    @Test
    void testFileDownload() {
        String fileId = createTestFile();

        given()
            .when()
                .get("/api/files/" + fileId + "/download")
            .then()
                .statusCode(200)
                .header("Content-Type", equalTo("application/pdf"))
                .header("Content-Disposition", containsString("attachment"))
                .header("Content-Length", notNullValue());
    }

    @Test
    void testLargeFileUpload() {
        // Create a large test file
        File largeFile = createLargeTestFile(10 * 1024 * 1024); // 10MB

        given()
            .contentType(ContentType.MULTIPART)
            .multiPart("file", largeFile, "application/octet-stream")
            .when()
                .post("/api/upload/large")
            .then()
                .statusCode(200)
                .time(lessThan(30000L)); // Less than 30 seconds
    }

    @Test
    void testFileStreaming() {
        given()
            .when()
                .get("/api/files/stream")
            .then()
                .statusCode(200)
                .header("Transfer-Encoding", equalTo("chunked"))
                .body(notNullValue());
    }

    private String createTestFile() {
        // Implementation to create and upload a test file
        return given()
            .contentType(ContentType.MULTIPART)
            .multiPart("file", new File("test.txt"), "text/plain")
            .when()
                .post("/api/upload")
            .then()
                .extract().path("fileId");
    }

    private File createLargeTestFile(long sizeInBytes) {
        // Implementation to create a large test file
        return new File("large-test-file.dat");
    }
}
```

### GraphQL testing

#### Тестирование GraphQL API
```java
@SpringBootTest
public class GraphQLTest {

    @Test
    void testGraphQLQuery() {
        String query = """
            {
                user(id: 1) {
                    id
                    name
                    email
                    posts {
                        id
                        title
                        content
                    }
                }
            }
            """;

        given()
            .contentType(ContentType.JSON)
            .body(Map.of("query", query))
            .when()
                .post("/graphql")
            .then()
                .statusCode(200)
                .body("data.user.id", equalTo(1))
                .body("data.user.name", notNullValue())
                .body("data.user.email", matchesPattern(".+@.+"))
                .body("data.user.posts.size()", greaterThan(0));
    }

    @Test
    void testGraphQLMutation() {
        String mutation = """
            mutation {
                createUser(input: {
                    name: "New User"
                    email: "new@example.com"
                }) {
                    id
                    name
                    email
                }
            }
            """;

        given()
            .contentType(ContentType.JSON)
            .body(Map.of("query", mutation))
            .when()
                .post("/graphql")
            .then()
                .statusCode(200)
                .body("data.createUser.id", notNullValue())
                .body("data.createUser.name", equalTo("New User"))
                .body("data.createUser.email", equalTo("new@example.com"));
    }

    @Test
    void testGraphQLErrors() {
        String invalidQuery = """
            {
                invalidField
            }
            """;

        given()
            .contentType(ContentType.JSON)
            .body(Map.of("query", invalidQuery))
            .when()
                .post("/graphql")
            .then()
                .statusCode(200)
                .body("errors", notNullValue())
                .body("errors[0].message", notNullValue());
    }

    @Test
    void testGraphQLVariables() {
        String query = """
            query GetUser($id: ID!) {
                user(id: $id) {
                    id
                    name
                    email
                }
            }
            """;

        Map<String, Object> variables = Map.of("id", 1);

        given()
            .contentType(ContentType.JSON)
            .body(Map.of(
                "query", query,
                "variables", variables
            ))
            .when()
                .post("/graphql")
            .then()
                .statusCode(200)
                .body("data.user.id", equalTo(1));
    }
}
```

### WebSocket testing

#### Тестирование WebSocket через REST
```java
@SpringBootTest
public class WebSocketTest {

    @Test
    void testWebSocketEndpoint() {
        // Test WebSocket handshake
        given()
            .header("Upgrade", "websocket")
            .header("Connection", "Upgrade")
            .header("Sec-WebSocket-Key", "dGhlIHNhbXBsZSBub25jZQ==")
            .header("Sec-WebSocket-Version", "13")
            .when()
                .get("/ws/chat")
            .then()
                .statusCode(101) // Switching Protocols
                .header("Upgrade", equalTo("websocket"))
                .header("Connection", equalTo("Upgrade"));
    }

    @Test
    void testWebSocketAuthentication() {
        String token = getWebSocketToken();

        given()
            .header("Sec-WebSocket-Protocol", "chat, superchat")
            .header("Authorization", "Bearer " + token)
            .when()
                .get("/ws/authenticated")
            .then()
                .statusCode(101)
                .header("Sec-WebSocket-Protocol", equalTo("chat"));
    }

    private String getWebSocketToken() {
        // Implementation to get WebSocket authentication token
        return "ws-token";
    }
}
```

## Performance testing

### Response time validation

#### Проверка производительности
```java
@SpringBootTest
public class PerformanceTest {

    @Test
    void testResponseTime() {
        given()
            .when()
                .get("/api/users")
            .then()
                .time(lessThan(500L)) // Less than 500ms
                .statusCode(200);
    }

    @Test
    void testSlowEndpoint() {
        // Test endpoint that should be slow but still work
        given()
            .when()
                .get("/api/reports/generate")
            .then()
                .time(both(greaterThan(1000L)).and(lessThan(10000L))) // 1-10 seconds
                .statusCode(200);
    }

    @Test
    void testConcurrentRequests() {
        // Test multiple concurrent requests
        List<CompletableFuture<Response>> futures = IntStream.range(0, 10)
            .mapToObj(i -> CompletableFuture.supplyAsync(() ->
                given().when().get("/api/data").then().extract().response()))
            .collect(Collectors.toList());

        CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).join();

        // Verify all responses
        futures.forEach(future -> {
            try {
                Response response = future.get();
                assertThat(response.getStatusCode()).isEqualTo(200);
                assertThat(response.getTime()).isLessThan(2000L);
            } catch (Exception e) {
                fail("Request failed: " + e.getMessage());
            }
        });
    }

    @Test
    void testLoadTestSimulation() {
        int numberOfRequests = 100;
        long maxResponseTime = 1000L; // 1 second

        long startTime = System.currentTimeMillis();

        // Execute multiple requests
        List<Response> responses = IntStream.range(0, numberOfRequests)
            .parallel()
            .mapToObj(i -> given().when().get("/api/data").then().extract().response())
            .collect(Collectors.toList());

        long totalTime = System.currentTimeMillis() - startTime;

        // Verify performance
        assertThat(responses).allMatch(r -> r.getStatusCode() == 200);
        assertThat(responses).allMatch(r -> r.getTime() < maxResponseTime);

        double requestsPerSecond = (double) numberOfRequests / (totalTime / 1000.0);
        assertThat(requestsPerSecond).isGreaterThan(50.0); // At least 50 req/sec
    }

    @Test
    void testMemoryUsage() {
        // Test that doesn't consume too much memory
        Runtime runtime = Runtime.getRuntime();
        long memoryBefore = runtime.totalMemory() - runtime.freeMemory();

        // Make request that processes large data
        given()
            .when()
                .get("/api/large-dataset")
            .then()
                .statusCode(200)
                .time(lessThan(5000L)); // Less than 5 seconds

        long memoryAfter = runtime.totalMemory() - runtime.freeMemory();
        long memoryUsed = memoryAfter - memoryBefore;

        assertThat(memoryUsed).isLessThan(100 * 1024 * 1024); // Less than 100MB additional usage
    }
}
```

## Best practices

### 1. Test organization

#### Структура тестов
```java
@SpringBootTest
public class ApiTestBestPractices {

    // Constants
    private static final String BASE_URI = "http://localhost:8080";
    private static final String API_PATH = "/api/v1";

    // Reusable specifications
    private RequestSpecification baseRequest;
    private ResponseSpecification successResponse;
    private ResponseSpecification errorResponse;

    @BeforeEach
    void setUp() {
        // Base request setup
        baseRequest = given()
            .baseUri(BASE_URI)
            .basePath(API_PATH)
            .contentType(ContentType.JSON)
            .accept(ContentType.JSON)
            .header("X-API-Key", getApiKey())
            .log().ifValidationFails(); // Log only on failures

        // Success response expectations
        successResponse = expect()
            .statusCode(200)
            .contentType(ContentType.JSON)
            .time(lessThan(2000L))
            .log().ifValidationFails();

        // Error response expectations
        errorResponse = expect()
            .statusCode(between(400, 599))
            .contentType(ContentType.JSON)
            .body("error", notNullValue())
            .body("timestamp", notNullValue());
    }

    @Nested
    @DisplayName("User Management API")
    class UserManagementTests {

        @Test
        @DisplayName("Should create new user successfully")
        void testCreateUser() {
            Map<String, Object> userData = Map.of(
                "name", "Test User",
                "email", "test@example.com",
                "role", "USER"
            );

            given(baseRequest)
                .body(userData)
                .when()
                    .post("/users")
                .then()
                    .statusCode(201)
                    .body("id", notNullValue())
                    .body("name", equalTo("Test User"))
                    .body("email", equalTo("test@example.com"))
                    .body("createdAt", notNullValue());
        }

        @Test
        @DisplayName("Should retrieve user by ID")
        void testGetUser() {
            // Assuming user exists from previous test
            given(baseRequest)
                .when()
                    .get("/users/1")
                .then()
                    .spec(successResponse)
                    .body("id", equalTo(1))
                    .body("name", notNullValue())
                    .body("email", matchesPattern(".+@.+"));
        }

        @Test
        @DisplayName("Should return 404 for non-existent user")
        void testGetNonExistentUser() {
            given(baseRequest)
                .when()
                    .get("/users/99999")
                .then()
                    .statusCode(404)
                    .body("error", equalTo("USER_NOT_FOUND"))
                    .body("message", containsString("not found"));
        }

        @Test
        @DisplayName("Should update user information")
        void testUpdateUser() {
            Map<String, Object> updateData = Map.of(
                "name", "Updated Name",
                "email", "updated@example.com"
            );

            given(baseRequest)
                .body(updateData)
                .when()
                    .put("/users/1")
                .then()
                    .statusCode(200)
                    .body("name", equalTo("Updated Name"))
                    .body("email", equalTo("updated@example.com"))
                    .body("updatedAt", notNullValue());
        }

        @Test
        @DisplayName("Should delete user")
        void testDeleteUser() {
            given(baseRequest)
                .when()
                    .delete("/users/1")
                .then()
                    .statusCode(204);

            // Verify user is deleted
            given(baseRequest)
                .when()
                    .get("/users/1")
                .then()
                    .statusCode(404);
        }
    }

    @Nested
    @DisplayName("Error Handling")
    class ErrorHandlingTests {

        @Test
        @DisplayName("Should handle validation errors")
        void testValidationErrors() {
            Map<String, Object> invalidData = Map.of(
                "name", "",
                "email", "invalid-email"
            );

            given(baseRequest)
                .body(invalidData)
                .when()
                    .post("/users")
                .then()
                    .spec(errorResponse)
                    .body("error", equalTo("VALIDATION_ERROR"))
                    .body("errors.name", notNullValue())
                    .body("errors.email", notNullValue());
        }

        @Test
        @DisplayName("Should handle authentication errors")
        void testAuthenticationError() {
            given()
                .baseUri(BASE_URI)
                .basePath(API_PATH)
                .contentType(ContentType.JSON)
                .header("X-API-Key", "invalid-key")
                .when()
                    .get("/users")
                .then()
                    .statusCode(401)
                    .body("error", equalTo("UNAUTHORIZED"))
                    .header("WWW-Authenticate", notNullValue());
        }
    }

    private String getApiKey() {
        // Implementation to get API key for testing
        return "test-api-key";
    }
}
```

### 2. Response validation strategies

#### Различные стратегии валидации
```java
@SpringBootTest
public class ValidationStrategiesTest {

    @Test
    void testPartialResponseValidation() {
        // Validate only critical fields
        given()
            .when()
                .get("/api/users/1")
            .then()
                .statusCode(200)
                .body("id", notNullValue())
                .body("email", matchesPattern(".+@.+"))
                // Not validating name, address, etc. - partial validation
                .body(not(hasKey("password"))) // Security check
                .body(not(hasKey("internalId"))); // Information hiding check
    }

    @Test
    void testCompleteResponseValidation() {
        // Validate entire response structure
        given()
            .when()
                .get("/api/users/1")
            .then()
                .statusCode(200)
                .body("$", hasKey("id"))
                .body("$", hasKey("name"))
                .body("$", hasKey("email"))
                .body("$", hasKey("createdAt"))
                .body("$", hasKey("status"))
                .body("$", not(hasKey("password")))
                .body("$", not(hasKey("deletedAt")))
                .body("id", notNullValue())
                .body("name", not(emptyString()))
                .body("email", matchesPattern(".+@.+"))
                .body("status", in(Arrays.asList("ACTIVE", "INACTIVE", "PENDING")));
    }

    @Test
    void testSchemaValidation() {
        // Use JSON schema validation
        given()
            .when()
                .get("/api/users/1")
            .then()
                .statusCode(200)
                .body(matchesJsonSchemaInClasspath("schemas/user-response.json"));
    }

    @Test
    void testContractValidation() {
        // Validate API contract
        Response response = given()
            .when()
                .get("/api/users");

        // Check response contract
        assertThat(response.getStatusCode()).isEqualTo(200);
        assertThat(response.getContentType()).isEqualTo("application/json");

        // Validate response structure matches contract
        JsonPath jsonPath = response.jsonPath();
        assertThat(jsonPath.getList("$")).isNotEmpty();
        assertThat(jsonPath.getString("[0].id")).isNotBlank();
        assertThat(jsonPath.getString("[0].email")).matches(".+@.+");

        // Check pagination headers if applicable
        assertThat(response.getHeader("X-Total-Count")).isNotNull();
    }

    @Test
    void testPerformanceValidation() {
        // Validate performance characteristics
        long startTime = System.currentTimeMillis();

        Response response = given()
            .when()
                .get("/api/users")
            .then()
                .extract().response();

        long responseTime = System.currentTimeMillis() - startTime;

        // Functional validation
        assertThat(response.getStatusCode()).isEqualTo(200);

        // Performance validation
        assertThat(responseTime).isLessThan(1000L); // Less than 1 second

        // Content validation
        assertThat(response.getBody().asString().length()).isGreaterThan(0);
    }
}
```

### 3. Test data management

#### Управление тестовыми данными
```java
@SpringBootTest
public class TestDataManagementTest {

    @Autowired
    private TestDataService testDataService;

    private Long testUserId;
    private String testUserEmail = "test@example.com";

    @BeforeEach
    void setUpTestData() {
        // Create test user
        testUserId = testDataService.createTestUser(
            "Test User",
            testUserEmail,
            UserStatus.ACTIVE
        );

        // Create related test data
        testDataService.createTestOrders(testUserId, 3);
        testDataService.createTestAddresses(testUserId, 2);
    }

    @AfterEach
    void cleanUpTestData() {
        // Clean up test data
        testDataService.deleteTestUser(testUserId);
    }

    @Test
    void testUserOperations() {
        // Test operations on test user
        given()
            .when()
                .get("/api/users/" + testUserId)
            .then()
                .statusCode(200)
                .body("email", equalTo(testUserEmail))
                .body("status", equalTo("ACTIVE"));
    }

    @Test
    void testUserOrders() {
        // Test orders for test user
        given()
            .when()
                .get("/api/users/" + testUserId + "/orders")
            .then()
                .statusCode(200)
                .body("size()", equalTo(3))
                .body("[0].userId", equalTo(testUserId.intValue()));
    }

    @Test
    void testUserAddresses() {
        // Test addresses for test user
        given()
            .when()
                .get("/api/users/" + testUserId + "/addresses")
            .then()
                .statusCode(200)
                .body("size()", equalTo(2))
                .body("findAll { it.primary }.size()", equalTo(1));
    }

    @Test
    void testDataIsolation() {
        // Ensure test data doesn't interfere with other tests
        Long otherUserId = testDataService.createIsolatedTestUser();

        try {
            // Verify test user data
            given()
                .when()
                    .get("/api/users/" + testUserId)
                .then()
                    .statusCode(200)
                    .body("email", equalTo(testUserEmail));

            // Verify other user data is separate
            given()
                .when()
                    .get("/api/users/" + otherUserId)
                .then()
                    .statusCode(200)
                    .body("email", not(equalTo(testUserEmail)));
        } finally {
            testDataService.deleteTestUser(otherUserId);
        }
    }
}
```

## Troubleshooting

### Распространенные проблемы

#### Path validation issues

**Symptoms:**
- JSON path expressions don't match expected data

**Solutions:**
```java
@SpringBootTest
public class PathValidationTest {

    @Test
    void testJsonPathDebugging() {
        Response response = given()
            .when()
                .get("/api/users/1")
            .then()
                .extract().response();

        // Print response for debugging
        System.out.println("Response body: " + response.getBody().asString());

        // Extract JSON path manually
        JsonPath jsonPath = response.jsonPath();

        // Debug individual paths
        System.out.println("ID: " + jsonPath.getString("id"));
        System.out.println("Name: " + jsonPath.getString("name"));
        System.out.println("Email: " + jsonPath.getString("email"));

        // Test paths individually
        assertThat(jsonPath.getString("id")).isNotNull();
        assertThat(jsonPath.getString("name")).isNotBlank();
        assertThat(jsonPath.getString("email")).matches(".+@.+");
    }

    @Test
    void testArrayPathValidation() {
        Response response = given()
            .when()
                .get("/api/users")
            .then()
                .extract().response();

        JsonPath jsonPath = response.jsonPath();

        // Debug array access
        System.out.println("Array size: " + jsonPath.getList("$").size());
        System.out.println("First element: " + jsonPath.getString("[0]"));
        System.out.println("All emails: " + jsonPath.getList("email"));

        // Test array operations
        assertThat(jsonPath.getList("$")).isNotEmpty();
        assertThat(jsonPath.getString("[0].email")).isNotNull();
        assertThat(jsonPath.getList("findAll { it.status == 'ACTIVE' }")).isNotEmpty();
    }
}
```

#### Content-Type issues

**Symptoms:**
- Requests fail due to wrong content type

**Solutions:**
```java
@SpringBootTest
public class ContentTypeTest {

    @Test
    void testContentTypeHandling() {
        // Explicit content type
        given()
            .contentType(ContentType.JSON)
            .accept(ContentType.JSON)
            .body(Map.of("name", "Test"))
            .when()
                .post("/api/data")
            .then()
                .statusCode(200)
                .contentType(ContentType.JSON);
    }

    @Test
    void testCharsetHandling() {
        // Specify charset
        given()
            .contentType("application/json; charset=UTF-8")
            .body("{\"name\": \"Тест\"}") // Unicode characters
            .when()
                .post("/api/data")
            .then()
                .statusCode(200);
    }

    @Test
    void testMultipartContentType() {
        // Multipart form data
        given()
            .contentType(ContentType.MULTIPART)
            .multiPart("file", new File("test.txt"))
            .multiPart("metadata", "{\"type\": \"test\"}", "application/json")
            .when()
                .post("/api/upload")
            .then()
                .statusCode(200);
    }
}
```

#### Authentication problems

**Symptoms:**
- Authentication requests fail

**Solutions:**
```java
@SpringBootTest
public class AuthenticationDebugTest {

    @Test
    void testBasicAuthDebug() {
        // Debug authentication
        Response response = given()
            .auth().basic("username", "password")
            .when()
                .get("/api/protected")
            .then()
                .extract().response();

        System.out.println("Status: " + response.getStatusCode());
        System.out.println("Auth header: " + response.getHeader("Authorization"));
        System.out.println("Response: " + response.getBody().asString());

        assertThat(response.getStatusCode()).isEqualTo(200);
    }

    @Test
    void testBearerTokenDebug() {
        String token = obtainToken();

        Response response = given()
            .header("Authorization", "Bearer " + token)
            .when()
                .get("/api/protected")
            .then()
                .extract().response();

        // Debug token
        System.out.println("Token: " + token);
        System.out.println("Status: " + response.getStatusCode());

        if (response.getStatusCode() != 200) {
            System.out.println("Error: " + response.getBody().asString());
        }

        assertThat(response.getStatusCode()).isEqualTo(200);
    }

    private String obtainToken() {
        // Debug token acquisition
        Response tokenResponse = given()
            .formParam("username", "test")
            .formParam("password", "test")
            .when()
                .post("/api/auth/token")
            .then()
                .extract().response();

        System.out.println("Token response: " + tokenResponse.getBody().asString());

        return tokenResponse.jsonPath().getString("access_token");
    }
}
```

### Debug techniques

#### Request/response logging
```java
@SpringBootTest
public class DebugTechniquesTest {

    @Test
    void testWithDetailedLogging() {
        given()
            .log().all() // Log request details
            .contentType(ContentType.JSON)
            .body(Map.of("name", "Test"))
            .when()
                .post("/api/users")
            .then()
                .log().all() // Log response details
                .statusCode(201);
    }

    @Test
    void testConditionalLogging() {
        given()
            .log().ifValidationFails() // Log only on validation failures
            .contentType(ContentType.JSON)
            .body(Map.of("name", "Test"))
            .when()
                .post("/api/users")
            .then()
                .log().ifValidationFails()
                .statusCode(201);
    }

    @Test
    void testCustomLogging() {
        // Custom logging filter
        given()
            .filter((requestSpec, responseSpec, ctx) -> {
                System.out.println("Request: " + ctx.getRequest().getMethod() + " " + ctx.getRequest().getURI());
                Response response = ctx.next(requestSpec, responseSpec);
                System.out.println("Response: " + response.getStatusCode());
                return response;
            })
            .when()
                .get("/api/data")
            .then()
                .statusCode(200);
    }

    @Test
    void testResponseExtraction() {
        // Extract response for detailed inspection
        Response response = given()
            .when()
                .get("/api/users")
            .then()
                .statusCode(200)
            .extract().response();

        // Detailed response analysis
        System.out.println("Status: " + response.getStatusCode());
        System.out.println("Content-Type: " + response.getContentType());
        System.out.println("Headers: " + response.getHeaders());
        System.out.println("Body length: " + response.getBody().asString().length());

        // Extract specific data
        JsonPath jsonPath = response.jsonPath();
        List<String> emails = jsonPath.getList("email");
        System.out.println("Emails found: " + emails.size());

        // Validate extracted data
        assertThat(emails).isNotEmpty();
        assertThat(emails).allMatch(email -> email.contains("@"));
    }
}
```

## Заключение

**REST Assured** — это мощный и гибкий инструмент для тестирования REST API с fluent API и широкими возможностями валидации. Он следует BDD паттерну Given-When-Then и предоставляет богатый набор инструментов для comprehensive API testing.

### Ключевые возможности:

1. **Fluent API** — читаемый и текучий синтаксис в стиле BDD
2. **Rich Validations** — множество встроенных проверок для JSON, XML, headers
3. **Authentication Support** — поддержка различных типов аутентификации
4. **Reusable Specs** — переиспользуемые спецификации запросов и ответов
5. **Data-driven Testing** — поддержка parameterized и CSV-based тестов
6. **Spring Integration** — интеграция с Spring Boot и MockMvc
7. **Content Type Support** — работа с JSON, XML, form data, files
8. **Performance Testing** — валидация response times и throughput

### Архитектурные преимущества:

#### Testability:
- **API Contract Testing** — проверка контрактов между клиентом и сервером
- **Integration Testing** — тестирование полного стека приложения
- **Contract Validation** — проверка API specifications
- **Error Handling Testing** — валидация error responses и edge cases
- **Performance Validation** — проверка response times и scalability

#### Maintainability:
- **Reusable Specifications** — DRY принцип для test setup
- **Descriptive Tests** — self-documenting test cases
- **Organized Test Structure** — nested и categorized tests
- **Data Management** — proper test data lifecycle
- **Debug Capabilities** — comprehensive logging и inspection tools

### Когда использовать REST Assured:

✅ **REST API Testing** — primary tool для REST API validation
✅ **Integration Testing** — тестирование API integrations
✅ **Contract Testing** — проверка API contracts
✅ **BDD Style Testing** — readable, specification-like tests
✅ **Complex Validations** — nested JSON/XML validations
✅ **Authentication Testing** — various auth mechanisms
✅ **Data-driven Testing** — parameterized test scenarios
✅ **Spring Boot APIs** — seamless Spring integration
✅ **Microservices Testing** — testing service interactions
✅ **API Documentation** — executable API documentation

### Когда НЕ использовать:

❌ **Unit Testing** — для unit тестов лучше JUnit + Mockito
❌ **UI Testing** — для UI лучше Selenium/WebDriver
❌ **Performance Load Testing** — для load testing лучше JMeter/Gatling
❌ **Non-HTTP APIs** — только для HTTP-based APIs
❌ **Simple Validations** — для простых проверок может быть избыточно
❌ **Legacy SOAP** — для SOAP лучше специализированные tools
❌ **Real-time APIs** — для WebSocket лучше специализированные clients

### Best practices:

1. **Request Specifications** — reusable request setup
2. **Response Specifications** — reusable response validation
3. **Test Organization** — nested и categorized test structure
4. **Data Management** — proper test data lifecycle
5. **Authentication Handling** — appropriate auth для каждого scenario
6. **Content Validation** — comprehensive JSON/XML validation
7. **Error Testing** — thorough error case coverage
8. **Performance Validation** — response time и throughput checks
9. **Debug Techniques** — logging и inspection tools
10. **Contract Testing** — API contract validation

### Типы Validation по сложности:

#### Basic Validations:
- **Status Codes** — HTTP status code validation
- **Headers** — Content-Type, Cache-Control, custom headers
- **Simple JSON** — basic field presence и values
- **Response Time** — basic performance checks

#### Advanced Validations:
- **Complex JSON** — nested objects, arrays, filtering
- **JSON Path** — advanced path expressions и queries
- **XML Validation** — XPath expressions и namespaces
- **Schema Validation** — JSON schema и XML schema checks

#### Specialized Validations:
- **Authentication** — OAuth, JWT, Basic auth validation
- **File Operations** — upload/download validation
- **GraphQL** — GraphQL query/mutation testing
- **WebSocket** — WebSocket handshake validation

#### Integration Validations:
- **Spring MVC** — MockMvc integration
- **Spring Security** — security context validation
- **Data-driven** — parameterized test validation
- **Contract Testing** — API specification validation

REST Assured является essential инструментом для modern API testing. Он предоставляет comprehensive solution для validation REST APIs с excellent support для various scenarios и integrations. 🚀

**Далее: Selenium (web UI testing)**
