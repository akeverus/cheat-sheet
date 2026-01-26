# WireMock для Java

Комплексное руководство по использованию WireMock для mocking HTTP сервисов в Java тестах: standalone server, JUnit integration, request matching, response templating, stateful mocking и Spring Boot интеграция.

**Дата последнего обновления:** 2026-01-21

## Полезные ссылки

### Официальная документация
- [WireMock Documentation](https://wiremock.org/docs/) - Основная документация
- [WireMock JUnit 5](https://wiremock.org/docs/junit-junit5/) - JUnit 5 интеграция
- [WireMock Spring Boot](https://wiremock.org/docs/spring-boot/) - Spring Boot интеграция

### Java интеграции
- [WireMock Java](https://wiremock.org/docs/java-usage/) - Java API
- [WireMock Extensions](https://wiremock.org/docs/extending-wiremock/) - Расширения
- [WireMock Standalone](https://wiremock.org/docs/standalone/) - Standalone server

### Best practices
- [WireMock Best Practices](https://wiremock.org/docs/best-practices/) - Лучшие практики
- [Testing Microservices](https://wiremock.org/docs/testing-microservices/) - Тестирование микросервисов
- [Stateful Mocking](https://wiremock.org/docs/stateful-behaviour/) - Stateful поведение

### См. также
- `testing/junit-advanced.md` - JUnit расширения
- `testing/mockito-advanced.md` - Mockito для unit mocking
- `spring-testing.md` - Spring testing
- `testing/rest-assured.md` - REST API testing

## Содержание

- [Введение в WireMock](#введение-в-wiremock)
- [JUnit 5 integration](#junit-5-integration)
- [Basic stubbing](#basic-stubbing)
- [Request matching](#request-matching)
- [Response templating](#response-templating)
- [Stateful mocking](#stateful-mocking)
- [Spring Boot integration](#spring-boot-integration)
- [Advanced scenarios](#advanced-scenarios)
- [Recording and playback](#recording-and-playback)
- [Best practices](#best-practices)
- [Troubleshooting](#troubleshooting)
- [Заключение](#заключение)

## Введение в WireMock

**WireMock** — это HTTP mock server для тестирования HTTP клиентов. WireMock позволяет создавать mock HTTP endpoints, которые имитируют реальные API сервисы, внешние системы и микросервисы.

### Почему WireMock?

WireMock предоставляет мощные возможности для mocking HTTP взаимодействий:

1. **HTTP Mocking** — полная имитация HTTP серверов
2. **Request Matching** — гибкое сопоставление запросов
3. **Response Templating** — динамические ответы
4. **Stateful Behavior** — поддержка состояний
5. **Recording** — запись реальных взаимодействий
6. **Standalone Mode** — независимый mock server
7. **JUnit Integration** — интеграция с тестами
8. **Spring Boot Support** — интеграция с Spring

### Maven зависимости

**WireMock** имеет модульную архитектуру, где core модуль предоставляет базовую функциональность HTTP mocking, а дополнительные модули обеспечивают интеграцию с различными testing frameworks и Spring Boot.

#### Core WireMock (обязательный)

**wiremock-jre8** — основной модуль WireMock для Java 8+.

```xml
<dependency>
    <groupId>com.github.tomakehurst</groupId>
    <artifactId>wiremock-jre8</artifactId>
    <version>2.35.0</version>
    <scope>test</scope>
</dependency>
```

**Что включает wiremock-jre8:**
- **HTTP Mock Server** — встроенный HTTP сервер для mocking
- **Request Matching** — гибкое сопоставление HTTP запросов
- **Response Stubbing** — создание mock ответов
- **Response Templating** — динамические ответы с Handlebars
- **Stateful Behavior** — сценарии с состояниями
- **Recording** — запись HTTP взаимодействий
- **Verification** — проверка полученных запросов
- **Java API** — программный API для управления mocks

#### JUnit интеграция

**wiremock-junit5** — интеграция с JUnit 5:

```xml
<dependency>
    <groupId>com.github.tomakehurst</groupId>
    <artifactId>wiremock-junit5</artifactId>
    <version>2.35.0</version>
    <scope>test</scope>
</dependency>
```

**Возможности JUnit 5 интеграции:**
- **@WireMockTest** — автоматический запуск WireMock сервера
- **Lifecycle management** — автоматическое управление жизненным циклом
- **Port allocation** — автоматическое выделение портов
- **Test isolation** — изоляция между тестами

**wiremock-junit4** — интеграция с JUnit 4 (legacy):

```xml
<dependency>
    <groupId>com.github.tomakehurst</groupId>
    <artifactId>wiremock-junit4</artifactId>
    <version>2.35.0</version>
    <scope>test</scope>
</dependency>
```

#### Spring Boot интеграция

**wiremock-spring-boot** — интеграция с Spring Boot:

```xml
<dependency>
    <groupId>com.github.tomakehurst</groupId>
    <artifactId>wiremock-spring-boot</artifactId>
    <version>2.35.0</version>
    <scope>test</scope>
</dependency>
```

**Spring Boot возможности:**
- **@AutoConfigureWireMock** — автоматическая конфигурация
- **Property injection** — инъекция портов через @Value
- **Test profiles** — отдельные профили для тестирования
- **Spring context integration** — интеграция с Spring контекстом

#### TestNG интеграция

**wiremock-testng** — интеграция с TestNG:

```xml
<dependency>
    <groupId>com.github.tomakehurst</groupId>
    <artifactId>wiremock-testng</artifactId>
    <version>2.35.0</version>
    <scope>test</scope>
</dependency>
```

#### Standalone server

**wiremock-standalone** — standalone JAR для запуска отдельного сервера:

```xml
<dependency>
    <groupId>com.github.tomakehurst</groupId>
    <artifactId>wiremock-standalone</artifactId>
    <version>2.35.0</version>
    <scope>test</scope>
</dependency>
```

**Использование standalone:**
```bash
# Запуск standalone сервера
java -jar wiremock-standalone-2.35.0.jar --port 8089 --verbose

# С файлами stubs
java -jar wiremock-standalone-2.35.0.jar --port 8089 --root-dir /path/to/stubs
```

#### Docker образ

**WireMock также доступен как Docker образ:**

```bash
# Запуск WireMock в Docker
docker run -d \
  --name wiremock \
  -p 8080:8080 \
  -v $(pwd)/stubs:/home/wiremock \
  wiremock/wiremock:2.35.0 \
  --global-response-templating \
  --verbose \
  --root-dir /home/wiremock
```

#### Расширения и плагины

**wiremock-extensions** — дополнительные расширения:

```xml
<dependency>
    <groupId>com.github.tomakehurst</groupId>
    <artifactId>wiremock-extensions</artifactId>
    <version>2.35.0</version>
    <scope>test</scope>
</dependency>
```

**Доступные расширения:**
- **Response Transformers** — трансформация ответов
- **Request Matchers** — кастомные matchers запросов
- **PostServe Actions** — действия после отправки ответа
- **Admin API** — расширенное управление через API

#### Gradle зависимости

**Для Gradle проектов с подробными конфигурациями:**

```gradle
dependencies {
    // Core WireMock - основной модуль для HTTP mocking
    testImplementation 'com.github.tomakehurst:wiremock-jre8:2.35.0'

    // JUnit 5 integration - интеграция с JUnit 5
    testImplementation 'com.github.tomakehurst:wiremock-junit5:2.35.0'

    // Spring Boot integration - интеграция с Spring Boot
    testImplementation 'com.github.tomakehurst:wiremock-spring-boot:2.35.0'

    // Standalone server - для запуска отдельного сервера
    testImplementation 'com.github.tomakehurst:wiremock-standalone:2.35.0'

    // Extensions - дополнительные возможности
    testImplementation 'com.github.tomakehurst:wiremock-extensions:2.35.0'

    // JUnit 5
    testImplementation 'org.junit.jupiter:junit-jupiter:5.10.0'
    testRuntimeOnly 'org.junit.platform:junit-platform-launcher'
}
```

#### Version management

**Рекомендуется использовать properties для версий:**

```gradle
ext {
    wiremockVersion = '2.35.0'
    junitVersion = '5.10.0'
}

dependencies {
    testImplementation "com.github.tomakehurst:wiremock-jre8:${wiremockVersion}"
    testImplementation "com.github.tomakehurst:wiremock-junit5:${wiremockVersion}"
    testImplementation "com.github.tomakehurst:wiremock-spring-boot:${wiremockVersion}"
    testImplementation "org.junit.jupiter:junit-jupiter:${junitVersion}"
}
```

#### Spring Boot интеграция

**Spring Boot Starter Test может включать WireMock:**

```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-test</artifactId>
    <scope>test</scope>
</dependency>

<!-- Дополнительно добавить WireMock -->
<dependency>
    <groupId>com.github.tomakehurst</groupId>
    <artifactId>wiremock-spring-boot</artifactId>
    <version>2.35.0</version>
    <scope>test</scope>
</dependency>
```

#### Миграция между версиями

**WireMock 2.x → 3.x (предстоящая версия):**

**Ключевые изменения:**
- **Java 11+** — минимальная версия Java 11
- **Performance improvements** — улучшенная производительность
- **New features** — новые возможности
- **Breaking changes** — изменения в API

**Текущая версия (2.x) остается стабильной и поддерживаемой.**

#### IDE Configuration

**IntelliJ IDEA:**
1. **File → Settings → Build, Execution, Deployment → Build Tools → Gradle**
2. **WireMock классы будут автоматически распознаны**
3. **Static imports работают из коробки**

**Eclipse:**
1. **Help → Eclipse Marketplace**
2. **Find**: "WireMock"
3. **Install**: WireMock Eclipse integration

**VS Code:**
```json
{
    "java.test.config": {
        "name": "JUnit Jupiter + WireMock",
        "type": "junit",
        "request": "launch",
        "mainClass": "org.junit.platform.console.ConsoleLauncher",
        "args": ["--scan-classpath"],
        "vmargs": ["-ea"],
        "dependencies": [
            "com.github.tomakehurst:wiremock-jre8:2.35.0"
        ]
    }
}
```

#### Troubleshooting зависимостей

**Проблема: Port conflicts**

```java
// Использовать динамическое выделение портов
@WireMockTest
public class WireMockTest {

    @Autowired
    private WireMockServer wireMockServer;

    @Test
    void testWithDynamicPort() {
        int port = wireMockServer.port();
        // Использовать port в тестах
    }
}
```

**Проблема: Version conflicts**

```xml
<!-- Исключить старую версию -->
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-test</artifactId>
    <exclusions>
        <exclusion>
            <groupId>com.github.tomakehurst</groupId>
            <artifactId>wiremock-core</artifactId>
        </exclusion>
    </exclusions>
</dependency>

<!-- Добавить нужную версию -->
<dependency>
    <groupId>com.github.tomakehurst</groupId>
    <artifactId>wiremock-jre8</artifactId>
    <version>2.35.0</version>
    <scope>test</scope>
</dependency>
```

**Проблема: Memory issues**

```java
// Настроить WireMock для экономии памяти
WireMock.configureFor("localhost", 8089);
WireMock.reset();

// Или использовать in-memory storage
new WireMockServer(WireMockConfiguration.wireMockConfig()
    .dynamicPort()
    .maxRequestJournalEntries(100)
    .useChunkedTransferEncoding(Options.ChunkedEncodingPolicy.NEVER));
```

**Проблема: Slow tests**

```java
// Оптимизировать для скорости
WireMock.configureFor("localhost", 8089);
WireMock.reset();

// Использовать stubFor вместо stubMapping
stubFor(get("/api/users")
    .willReturn(aResponse()
        .withStatus(200)
        .withBody("[]")));

// Избегать verify() в performance-critical тестах
```

**Проблема: File system issues**

```java
// Настроить временную директорию
new WireMockServer(WireMockConfiguration.wireMockConfig()
    .dynamicPort()
    .usingFilesUnderDirectory("/tmp/wiremock"));
```

### Basic usage

#### Простое использование
```java
@SpringBootTest
public class WireMockBasicTest {

    @Autowired
    private ExternalApiClient apiClient;

    @Test
    void testExternalApiCall() {
        // Configure WireMock stub
        stubFor(get(urlEqualTo("/api/users/1"))
            .willReturn(aResponse()
                .withStatus(200)
                .withHeader("Content-Type", "application/json")
                .withBody("{\"id\": 1, \"name\": \"John Doe\", \"email\": \"john@example.com\"}")));

        // Test the client
        User user = apiClient.getUserById(1L);

        assertThat(user.getId()).isEqualTo(1L);
        assertThat(user.getName()).isEqualTo("John Doe");
        assertThat(user.getEmail()).isEqualTo("john@example.com");
    }
}
```

## JUnit 5 integration

### WireMockExtension

#### Basic JUnit 5 setup
```java
@SpringBootTest
@ExtendWith(WireMockExtension.class)
public class WireMockJUnit5Test {

    @Autowired
    private PaymentService paymentService;

    @Test
    void testPaymentProcessing(WireMockRuntimeInfo wmRuntimeInfo) {
        // WireMock server is automatically started
        int port = wmRuntimeInfo.getHttpPort();

        // Configure stub
        stubFor(post(urlEqualTo("/api/payments"))
            .willReturn(aResponse()
                .withStatus(200)
                .withHeader("Content-Type", "application/json")
                .withBody("{\"transactionId\": \"txn_123\", \"status\": \"SUCCESS\"}")));

        // Test the service
        PaymentResult result = paymentService.processPayment(createTestPayment());

        assertThat(result.isSuccessful()).isTrue();
        assertThat(result.getTransactionId()).isEqualTo("txn_123");
    }

    @Test
    void testPaymentFailure(WireMockRuntimeInfo wmRuntimeInfo) {
        // Configure failure scenario
        stubFor(post(urlEqualTo("/api/payments"))
            .willReturn(aResponse()
                .withStatus(400)
                .withHeader("Content-Type", "application/json")
                .withBody("{\"error\": \"INSUFFICIENT_FUNDS\", \"message\": \"Not enough balance\"}")));

        // Test error handling
        assertThatThrownBy(() -> paymentService.processPayment(createTestPayment()))
            .isInstanceOf(PaymentException.class)
            .hasMessage("INSUFFICIENT_FUNDS");
    }
}
```

### Declarative configuration

#### @WireMockTest annotation
```java
@WireMockTest
@SpringBootTest
public class DeclarativeWireMockTest {

    @Autowired
    private UserService userService;

    @Test
    void testUserCreation() {
        // WireMock server is automatically configured
        stubFor(post(urlEqualTo("/api/users"))
            .willReturn(aResponse()
                .withStatus(201)
                .withHeader("Content-Type", "application/json")
                .withBody("{\"id\": 123, \"name\": \"Test User\", \"email\": \"test@example.com\"}")));

        User user = userService.createUser("Test User", "test@example.com");

        assertThat(user.getId()).isEqualTo(123L);
        assertThat(user.getName()).isEqualTo("Test User");
    }

    @Test
    void testUserNotFound() {
        stubFor(get(urlEqualTo("/api/users/999"))
            .willReturn(aResponse()
                .withStatus(404)
                .withHeader("Content-Type", "application/json")
                .withBody("{\"error\": \"User not found\"}")));

        assertThatThrownBy(() -> userService.getUserById(999L))
            .isInstanceOf(UserNotFoundException.class);
    }
}
```

## Basic stubbing

### HTTP methods

#### Different HTTP methods
```java
@SpringBootTest
@WireMockTest
public class HttpMethodsTest {

    @Autowired
    private ApiClient apiClient;

    @Test
    void testGetRequest() {
        stubFor(get(urlEqualTo("/api/products"))
            .willReturn(aResponse()
                .withStatus(200)
                .withBody("[{\"id\": 1, \"name\": \"Laptop\"}]")));

        List<Product> products = apiClient.getAllProducts();
        assertThat(products).hasSize(1);
        assertThat(products.get(0).getName()).isEqualTo("Laptop");
    }

    @Test
    void testPostRequest() {
        stubFor(post(urlEqualTo("/api/orders"))
            .willReturn(aResponse()
                .withStatus(201)
                .withBody("{\"orderId\": \"ORD-123\", \"status\": \"CONFIRMED\"}")));

        Order order = apiClient.createOrder(createOrderRequest());
        assertThat(order.getOrderId()).isEqualTo("ORD-123");
        assertThat(order.getStatus()).isEqualTo("CONFIRMED");
    }

    @Test
    void testPutRequest() {
        stubFor(put(urlEqualTo("/api/users/1"))
            .willReturn(aResponse()
                .withStatus(200)
                .withBody("{\"id\": 1, \"name\": \"Updated User\"}")));

        User updatedUser = apiClient.updateUser(1L, "Updated User");
        assertThat(updatedUser.getName()).isEqualTo("Updated User");
    }

    @Test
    void testDeleteRequest() {
        stubFor(delete(urlEqualTo("/api/users/1"))
            .willReturn(aResponse()
                .withStatus(204)));

        assertThatCode(() -> apiClient.deleteUser(1L)).doesNotThrowAnyException();
    }
}
```

### Response configuration

#### Response headers and body
```java
@SpringBootTest
@WireMockTest
public class ResponseConfigurationTest {

    @Autowired
    private HttpClient httpClient;

    @Test
    void testResponseWithHeaders() {
        stubFor(get(urlEqualTo("/api/data"))
            .willReturn(aResponse()
                .withStatus(200)
                .withHeader("Content-Type", "application/json")
                .withHeader("Cache-Control", "max-age=3600")
                .withHeader("X-API-Version", "v2.1")
                .withBody("{\"data\": \"test\"}")));

        HttpResponse response = httpClient.get("/api/data");

        assertThat(response.getStatusCode()).isEqualTo(200);
        assertThat(response.getHeader("Content-Type")).isEqualTo("application/json");
        assertThat(response.getHeader("X-API-Version")).isEqualTo("v2.1");
        assertThat(response.getBody()).contains("test");
    }

    @Test
    void testBinaryResponse() {
        byte[] imageData = loadTestImage();

        stubFor(get(urlEqualTo("/api/images/1"))
            .willReturn(aResponse()
                .withStatus(200)
                .withHeader("Content-Type", "image/png")
                .withBody(imageData)));

        byte[] responseData = httpClient.getImage("/api/images/1");

        assertThat(responseData).isEqualTo(imageData);
    }

    @Test
    void testLargeResponse() {
        String largeJson = generateLargeJsonResponse();

        stubFor(get(urlEqualTo("/api/large-data"))
            .willReturn(aResponse()
                .withStatus(200)
                .withHeader("Content-Type", "application/json")
                .withBody(largeJson)));

        String response = httpClient.getLargeData();

        assertThat(response).hasSizeGreaterThan(1000000); // 1MB+
        assertThat(response).contains("\"totalRecords\": 10000");
    }

    @Test
    void testErrorResponses() {
        stubFor(get(urlEqualTo("/api/error"))
            .willReturn(aResponse()
                .withStatus(500)
                .withHeader("Content-Type", "application/json")
                .withBody("{\"error\": \"Internal Server Error\", \"code\": \"ISE_001\"}")));

        assertThatThrownBy(() -> httpClient.getData())
            .isInstanceOf(HttpException.class)
            .hasMessageContaining("500");
    }
}
```

## Request matching

### URL matching

#### Different URL matching strategies
```java
@SpringBootTest
@WireMockTest
public class UrlMatchingTest {

    @Autowired
    private ApiClient apiClient;

    @Test
    void testExactUrlMatching() {
        stubFor(get(urlEqualTo("/api/users/123"))
            .willReturn(aResponse().withBody("{\"id\": 123, \"name\": \"John\"}")));

        User user = apiClient.getUser(123L);
        assertThat(user.getName()).isEqualTo("John");

        // This should not match
        assertThatThrownBy(() -> apiClient.getUser(456L))
            .isInstanceOf(Exception.class);
    }

    @Test
    void testUrlPatternMatching() {
        stubFor(get(urlMatching("/api/users/[0-9]+"))
            .willReturn(aResponse().withBody("{\"id\": \"dynamic\", \"name\": \"Any User\"}")));

        // Should match any user ID
        User user1 = apiClient.getUser(123L);
        User user2 = apiClient.getUser(999L);

        assertThat(user1.getName()).isEqualTo("Any User");
        assertThat(user2.getName()).isEqualTo("Any User");
    }

    @Test
    void testUrlPathMatching() {
        stubFor(get(urlPathEqualTo("/api/products"))
            .willReturn(aResponse().withBody("[{\"name\": \"Product 1\"}]")));

        List<Product> products = apiClient.getProducts();
        assertThat(products).hasSize(1);
    }

    @Test
    void testUrlPathTemplateMatching() {
        stubFor(get(urlPathMatching("/api/users/.*"))
            .willReturn(aResponse().withBody("{\"user\": true}")));

        // Should match /api/users/123, /api/users/abc, etc.
        assertThat(apiClient.userExists(123L)).isTrue();
        assertThat(apiClient.userExists(999L)).isTrue();
    }
}
```

### Query parameter matching

#### Query parameters
```java
@SpringBootTest
@WireMockTest
public class QueryParameterTest {

    @Autowired
    private SearchService searchService;

    @Test
    void testQueryParameterMatching() {
        stubFor(get(urlPathEqualTo("/api/search"))
            .withQueryParam("q", equalTo("laptop"))
            .willReturn(aResponse().withBody("{\"results\": [{\"name\": \"Gaming Laptop\"}]}")));

        SearchResult result = searchService.search("laptop");

        assertThat(result.getResults()).hasSize(1);
        assertThat(result.getResults().get(0).getName()).isEqualTo("Gaming Laptop");
    }

    @Test
    void testMultipleQueryParameters() {
        stubFor(get(urlPathEqualTo("/api/products"))
            .withQueryParam("category", equalTo("electronics"))
            .withQueryParam("price_min", equalTo("500"))
            .withQueryParam("price_max", equalTo("1500"))
            .willReturn(aResponse().withBody("{\"products\": [{\"name\": \"Laptop\", \"price\": 999}]}")));

        List<Product> products = searchService.searchByCategory("electronics", 500, 1500);

        assertThat(products).hasSize(1);
        assertThat(products.get(0).getPrice()).isEqualTo(999);
    }

    @Test
    void testOptionalQueryParameters() {
        // Match with or without sort parameter
        stubFor(get(urlPathEqualTo("/api/users"))
            .withQueryParam("status", equalTo("active"))
            .willReturn(aResponse().withBody("{\"users\": [{\"name\": \"Active User\"}]}")));

        List<User> users1 = userService.getUsersByStatus("active");
        List<User> users2 = userService.getUsersByStatusSorted("active", "name");

        assertThat(users1).hasSize(1);
        assertThat(users2).hasSize(1);
    }

    @Test
    void testQueryParameterPatterns() {
        stubFor(get(urlPathEqualTo("/api/orders"))
            .withQueryParam("date", matching("\\d{4}-\\d{2}-\\d{2}"))
            .willReturn(aResponse().withBody("{\"orders\": [{\"date\": \"2023-01-01\"}]}")));

        List<Order> orders = orderService.getOrdersByDate("2023-01-01");
        assertThat(orders).hasSize(1);
    }
}
```

### Header matching

#### HTTP headers
```java
@SpringBootTest
@WireMockTest
public class HeaderMatchingTest {

    @Autowired
    private AuthenticatedApiClient apiClient;

    @Test
    void testHeaderMatching() {
        stubFor(get(urlEqualTo("/api/secure-data"))
            .withHeader("Authorization", equalTo("Bearer token123"))
            .willReturn(aResponse().withBody("{\"data\": \"secure\"}")));

        String data = apiClient.getSecureData("Bearer token123");

        assertThat(data).isEqualTo("secure");
    }

    @Test
    void testContentTypeHeader() {
        stubFor(post(urlEqualTo("/api/data"))
            .withHeader("Content-Type", equalTo("application/json"))
            .willReturn(aResponse().withStatus(201)));

        boolean success = apiClient.postJsonData("{\"key\": \"value\"}");

        assertThat(success).isTrue();
    }

    @Test
    void testAcceptHeader() {
        stubFor(get(urlEqualTo("/api/data"))
            .withHeader("Accept", equalTo("application/xml"))
            .willReturn(aResponse()
                .withHeader("Content-Type", "application/xml")
                .withBody("<data><item>value</item></data>")));

        String xmlData = apiClient.getDataAsXml();

        assertThat(xmlData).contains("<data>");
        assertThat(xmlData).contains("<item>value</item>");
    }

    @Test
    void testCustomHeaders() {
        stubFor(get(urlEqualTo("/api/versioned"))
            .withHeader("X-API-Version", equalTo("v2"))
            .withHeader("X-Client-ID", matching("client_[0-9]+"))
            .willReturn(aResponse().withBody("{\"version\": \"v2\"}")));

        String response = apiClient.getVersionedData("v2", "client_123");

        assertThat(response).contains("\"version\": \"v2\"");
    }

    @Test
    void testAuthorizationFailure() {
        stubFor(get(urlEqualTo("/api/admin"))
            .withHeader("Authorization", equalTo("Bearer admin-token"))
            .willReturn(aResponse().withStatus(200)));

        stubFor(get(urlEqualTo("/api/admin"))
            .withHeader("Authorization", not(equalTo("Bearer admin-token")))
            .willReturn(aResponse().withStatus(403)));

        // Valid token
        assertThat(apiClient.getAdminData("Bearer admin-token")).isNotNull();

        // Invalid token
        assertThatThrownBy(() -> apiClient.getAdminData("Bearer invalid"))
            .isInstanceOf(ForbiddenException.class);
    }
}
```

### Body matching

#### Request body matching
```java
@SpringBootTest
@WireMockTest
public class BodyMatchingTest {

    @Autowired
    private ApiClient apiClient;

    @Test
    void testJsonBodyMatching() {
        stubFor(post(urlEqualTo("/api/users"))
            .withRequestBody(equalToJson("{\"name\": \"John\", \"email\": \"john@example.com\"}"))
            .willReturn(aResponse().withStatus(201).withBody("{\"id\": 1}")));

        Long userId = apiClient.createUser("John", "john@example.com");

        assertThat(userId).isEqualTo(1L);
    }

    @Test
    void testJsonBodyPatternMatching() {
        stubFor(post(urlEqualTo("/api/search"))
            .withRequestBody(matchingJsonPath("$.query"))
            .willReturn(aResponse().withBody("{\"results\": []}")));

        SearchResult result = apiClient.search("{\"query\": \"laptop\", \"filters\": {}}");

        assertThat(result.getResults()).isEmpty();
    }

    @Test
    void testXmlBodyMatching() {
        String xmlBody = "<user><name>John</name><email>john@example.com</email></user>";

        stubFor(post(urlEqualTo("/api/users"))
            .withRequestBody(equalToXml(xmlBody))
            .willReturn(aResponse().withStatus(201)));

        boolean success = apiClient.createUserXml(xmlBody);

        assertThat(success).isTrue();
    }

    @Test
    void testFormDataMatching() {
        stubFor(post(urlEqualTo("/api/login"))
            .withRequestBody(containing("username=john&password=secret"))
            .willReturn(aResponse().withBody("{\"token\": \"jwt123\"}")));

        String token = apiClient.login("john", "secret");

        assertThat(token).isEqualTo("jwt123");
    }

    @Test
    void testMultipartMatching() {
        stubFor(post(urlEqualTo("/api/upload"))
            .withMultipartRequestBody(
                aMultipart()
                    .withName("file")
                    .withHeader("Content-Type", containing("image/"))
            )
            .willReturn(aResponse().withStatus(200)));

        boolean uploaded = apiClient.uploadImage(testImageFile);

        assertThat(uploaded).isTrue();
    }

    @Test
    void testBodySizeMatching() {
        stubFor(post(urlEqualTo("/api/data"))
            .withRequestBody(matching(".*")  // Any body
                .and(matching("(?s).{1000,}"))) // At least 1000 characters
            .willReturn(aResponse().withStatus(413))); // Payload too large

        assertThatThrownBy(() -> apiClient.postLargeData(generateLargeData()))
            .isInstanceOf(PayloadTooLargeException.class);
    }
}
```

## Response templating

### Basic templating

#### Dynamic responses
```java
@SpringBootTest
@WireMockTest
public class ResponseTemplatingTest {

    @Autowired
    private ApiClient apiClient;

    @Test
    void testDynamicResponseWithRequestData() {
        stubFor(get(urlMatching("/api/users/([0-9]+)"))
            .willReturn(aResponse()
                .withStatus(200)
                .withHeader("Content-Type", "application/json")
                .withBody("{\"id\": {{request.path.[1]}}, \"name\": \"User {{request.path.[1]}}\", \"timestamp\": \"{{now}}\"}")));

        User user = apiClient.getUser(123L);

        assertThat(user.getId()).isEqualTo(123L);
        assertThat(user.getName()).isEqualTo("User 123");
        assertThat(user.getTimestamp()).isNotNull();
    }

    @Test
    void testConditionalResponseTemplating() {
        stubFor(get(urlPathEqualTo("/api/status"))
            .willReturn(aResponse()
                .withBody("{ \"status\": \"{{#if (request.query.status)}}{{request.query.status}}{{else}}unknown{{/if}}\" }")));

        // With status parameter
        String status1 = apiClient.getStatus("active");
        assertThat(status1).contains("\"status\": \"active\"");

        // Without status parameter
        String status2 = apiClient.getStatus(null);
        assertThat(status2).contains("\"status\": \"unknown\"");
    }

    @Test
    void testRequestHeaderTemplating() {
        stubFor(get(urlEqualTo("/api/echo"))
            .willReturn(aResponse()
                .withBody("{ \"user-agent\": \"{{request.headers.User-Agent}}\", \"accept\": \"{{request.headers.Accept}}\" }")));

        String response = apiClient.getEcho();

        assertThat(response).contains("user-agent");
        assertThat(response).contains("accept");
    }

    @Test
    void testRandomValueTemplating() {
        stubFor(get(urlEqualTo("/api/random"))
            .willReturn(aResponse()
                .withBody("{ \"id\": {{randomInt}}, \"value\": \"{{randomValue length=10 type='ALPHANUMERIC'}}\" }")));

        String response1 = apiClient.getRandomData();
        String response2 = apiClient.getRandomData();

        // Parse JSON and verify different values
        assertThat(response1).isNotEqualTo(response2);
    }
}
```

### Advanced templating

#### Complex templating scenarios
```java
@SpringBootTest
@WireMockTest
public class AdvancedTemplatingTest {

    @Autowired
    private ComplexApiClient apiClient;

    @Test
    void testArrayTemplating() {
        stubFor(get(urlEqualTo("/api/list"))
            .willReturn(aResponse()
                .withBody("{{#each request.query.items}}{{#if @index}},{{/if}}\"{{this}}\"{{/each}}")
                .withTransformers("response-template")));

        String response = apiClient.getListData("item1,item2,item3");

        assertThat(response).isEqualTo("\"item1\",\"item2\",\"item3\"");
    }

    @Test
    void testJsonPathTemplating() {
        stubFor(post(urlEqualTo("/api/transform"))
            .withRequestBody(matchingJsonPath("$.data"))
            .willReturn(aResponse()
                .withBody("{ \"original\": {{jsonPath request.body '$.data'}}, \"transformed\": \"{{jsonPath request.body '$.data'}}-transformed\" }")
                .withTransformers("response-template")));

        String response = apiClient.transformData("{\"data\": \"test-value\"}");

        assertThat(response).contains("\"original\": \"test-value\"");
        assertThat(response).contains("\"transformed\": \"test-value-transformed\"");
    }

    @Test
    void testDateTemplating() {
        stubFor(get(urlEqualTo("/api/time"))
            .willReturn(aResponse()
                .withBody("{ \"now\": \"{{now}}\", \"today\": \"{{now format='yyyy-MM-dd'}}\", \"timestamp\": {{now epoch}}, \"formatted\": \"{{now format='yyyy-MM-dd HH:mm:ss' timezone='UTC'}}\" }")
                .withTransformers("response-template")));

        String response = apiClient.getTimeData();

        assertThat(response).contains("now");
        assertThat(response).contains("today");
        assertThat(response).contains("timestamp");
        assertThat(response).matches(".*\"today\": \"\\d{4}-\\d{2}-\\d{2}\".*");
    }

    @Test
    void testMathTemplating() {
        stubFor(get(urlMatching("/api/calculate/([0-9]+)/([0-9]+)"))
            .willReturn(aResponse()
                .withBody("{ \"a\": {{request.path.[1]}}, \"b\": {{request.path.[2]}}, \"sum\": {{math request.path.[1] '+' request.path.[2]}}, \"product\": {{math request.path.[1] '*' request.path.[2]}} }")
                .withTransformers("response-template")));

        String response = apiClient.calculate(5, 3);

        assertThat(response).contains("\"sum\": 8");
        assertThat(response).contains("\"product\": 15");
    }

    @Test
    void testConditionalLogicTemplating() {
        stubFor(get(urlPathEqualTo("/api/check"))
            .willReturn(aResponse()
                .withBody("{ \"result\": \"{{#if (gt request.query.value 10)}}high{{else if (gt request.query.value 5)}}medium{{else}}low{{/if}}\" }")
                .withTransformers("response-template")));

        assertThat(apiClient.checkValue(15)).contains("\"result\": \"high\"");
        assertThat(apiClient.checkValue(8)).contains("\"result\": \"medium\"");
        assertThat(apiClient.checkValue(3)).contains("\"result\": \"low\"");
    }
}
```

## Stateful mocking

### Scenario-based mocking

#### Stateful behavior
```java
@SpringBootTest
@WireMockTest
public class StatefulMockingTest {

    @Autowired
    private PaymentService paymentService;

    @Test
    void testPaymentStateMachine() {
        // Initial state: Payment pending
        stubFor(post(urlEqualTo("/api/payments"))
            .inScenario("Payment Flow")
            .whenScenarioStateIs(STARTED)
            .willReturn(aResponse()
                .withBody("{\"status\": \"PENDING\", \"id\": \"pay_123\"}")
                .withTransformers("response-template"))
            .willSetStateTo("PAYMENT_CREATED"));

        // Check status
        stubFor(get(urlEqualTo("/api/payments/pay_123"))
            .inScenario("Payment Flow")
            .whenScenarioStateIs("PAYMENT_CREATED")
            .willReturn(aResponse()
                .withBody("{\"status\": \"PROCESSING\"}")
                .withTransformers("response-template"))
            .willSetStateTo("PAYMENT_PROCESSING"));

        // Complete payment
        stubFor(post(urlEqualTo("/api/payments/pay_123/complete"))
            .inScenario("Payment Flow")
            .whenScenarioStateIs("PAYMENT_PROCESSING")
            .willReturn(aResponse()
                .withBody("{\"status\": \"COMPLETED\"}")
                .withTransformers("response-template"))
            .willSetStateTo("PAYMENT_COMPLETED"));

        // Test the flow
        Payment payment = paymentService.createPayment(100.0);
        assertThat(payment.getStatus()).isEqualTo("PENDING");

        PaymentStatus status = paymentService.getPaymentStatus("pay_123");
        assertThat(status).isEqualTo(PaymentStatus.PROCESSING);

        PaymentResult result = paymentService.completePayment("pay_123");
        assertThat(result.getStatus()).isEqualTo("COMPLETED");
    }

    @Test
    void testUserRegistrationFlow() {
        // Registration
        stubFor(post(urlEqualTo("/api/users"))
            .inScenario("User Registration")
            .whenScenarioStateIs(STARTED)
            .willReturn(aResponse()
                .withStatus(201)
                .withBody("{\"id\": \"user_123\", \"status\": \"PENDING_VERIFICATION\"}")
                .withTransformers("response-template"))
            .willSetStateTo("USER_CREATED"));

        // Email verification
        stubFor(post(urlEqualTo("/api/users/user_123/verify"))
            .inScenario("User Registration")
            .whenScenarioStateIs("USER_CREATED")
            .willReturn(aResponse()
                .withBody("{\"status\": \"VERIFIED\"}")
                .withTransformers("response-template"))
            .willSetStateTo("USER_VERIFIED"));

        // Login after verification
        stubFor(post(urlEqualTo("/api/auth/login"))
            .inScenario("User Registration")
            .whenScenarioStateIs("USER_VERIFIED")
            .willReturn(aResponse()
                .withBody("{\"token\": \"jwt_123\", \"user\": {\"id\": \"user_123\", \"status\": \"ACTIVE\"}}")
                .withTransformers("response-template"));

        // Test the complete flow
        User user = userService.registerUser("john@example.com", "password");
        assertThat(user.getStatus()).isEqualTo("PENDING_VERIFICATION");

        userService.verifyEmail("user_123");
        AuthResult auth = userService.login("john@example.com", "password");

        assertThat(auth.getToken()).isEqualTo("jwt_123");
        assertThat(auth.getUser().getStatus()).isEqualTo("ACTIVE");
    }
}
```

### Advanced stateful scenarios

#### Complex state transitions
```java
@SpringBootTest
@WireMockTest
public class AdvancedStatefulTest {

    @Autowired
    private OrderService orderService;

    @Test
    void testOrderLifecycleWithFailures() {
        // Order creation
        stubFor(post(urlEqualTo("/api/orders"))
            .inScenario("Order Processing")
            .whenScenarioStateIs(STARTED)
            .willReturn(aResponse()
                .withBody("{\"orderId\": \"ord_123\", \"status\": \"CREATED\"}")
                .withTransformers("response-template"))
            .willSetStateTo("ORDER_CREATED"));

        // Payment processing - success
        stubFor(post(urlEqualTo("/api/orders/ord_123/payment"))
            .inScenario("Order Processing")
            .whenScenarioStateIs("ORDER_CREATED")
            .willReturn(aResponse()
                .withBody("{\"status\": \"PAYMENT_SUCCESS\"}")
                .withTransformers("response-template"))
            .willSetStateTo("PAYMENT_PROCESSED"));

        // Inventory check - success
        stubFor(post(urlEqualTo("/api/orders/ord_123/inventory"))
            .inScenario("Order Processing")
            .whenScenarioStateIs("PAYMENT_PROCESSED")
            .willReturn(aResponse()
                .withBody("{\"status\": \"INVENTORY_AVAILABLE\"}")
                .withTransformers("response-template"))
            .willSetStateTo("INVENTORY_CHECKED"));

        // Shipping
        stubFor(post(urlEqualTo("/api/orders/ord_123/ship"))
            .inScenario("Order Processing")
            .whenScenarioStateIs("INVENTORY_CHECKED")
            .willReturn(aResponse()
                .withBody("{\"status\": \"SHIPPED\", \"tracking\": \"trk_456\"}")
                .withTransformers("response-template"))
            .willSetStateTo("ORDER_SHIPPED"));

        // Test successful flow
        Order order = orderService.createOrder(createOrderItems());
        assertThat(order.getStatus()).isEqualTo("CREATED");

        orderService.processPayment(order.getOrderId());
        orderService.checkInventory(order.getOrderId());
        ShippingResult shipping = orderService.shipOrder(order.getOrderId());

        assertThat(shipping.getStatus()).isEqualTo("SHIPPED");
        assertThat(shipping.getTrackingNumber()).isEqualTo("trk_456");
    }

    @Test
    void testConcurrentScenarioStates() {
        // Multiple scenarios can run independently
        stubFor(get(urlEqualTo("/api/service-a"))
            .inScenario("Service A")
            .whenScenarioStateIs(STARTED)
            .willReturn(aResponse().withBody("A1"))
            .willSetStateTo("A_STEP2"));

        stubFor(get(urlEqualTo("/api/service-a"))
            .inScenario("Service A")
            .whenScenarioStateIs("A_STEP2")
            .willReturn(aResponse().withBody("A2"));

        stubFor(get(urlEqualTo("/api/service-b"))
            .inScenario("Service B")
            .whenScenarioStateIs(STARTED)
            .willReturn(aResponse().withBody("B1"))
            .willSetStateTo("B_STEP2"));

        // Services A and B maintain independent state
        assertThat(serviceClient.callServiceA()).isEqualTo("A1");
        assertThat(serviceClient.callServiceB()).isEqualTo("B1");
        assertThat(serviceClient.callServiceA()).isEqualTo("A2");
    }

    @Test
    void testStatefulErrorHandling() {
        // Initial request
        stubFor(post(urlEqualTo("/api/process"))
            .inScenario("Error Handling")
            .whenScenarioStateIs(STARTED)
            .willReturn(aResponse()
                .withStatus(202)
                .withBody("{\"requestId\": \"req_123\", \"status\": \"PROCESSING\"}")
                .withTransformers("response-template"))
            .willSetStateTo("PROCESSING"));

        // Check status - still processing
        stubFor(get(urlEqualTo("/api/process/req_123"))
            .inScenario("Error Handling")
            .whenScenarioStateIs("PROCESSING")
            .willReturn(aResponse()
                .withBody("{\"status\": \"PROCESSING\", \"progress\": 50}")
                .withTransformers("response-template"));

        // Simulate failure
        stubFor(get(urlEqualTo("/api/process/req_123"))
            .inScenario("Error Handling")
            .whenScenarioStateIs("PROCESSING")
            .willReturn(aResponse()
                .withStatus(500)
                .withBody("{\"error\": \"PROCESSING_FAILED\", \"message\": \"Internal error\"}")
                .withTransformers("response-template"))
            .willSetStateTo("FAILED"));

        // Retry after failure
        stubFor(post(urlEqualTo("/api/process/req_123/retry"))
            .inScenario("Error Handling")
            .whenScenarioStateIs("FAILED")
            .willReturn(aResponse()
                .withBody("{\"status\": \"RETRYING\"}")
                .withTransformers("response-template"))
            .willSetStateTo("RETRYING"));

        // Test error handling flow
        ProcessResult result = processor.startProcess(data);
        assertThat(result.getStatus()).isEqualTo("PROCESSING");

        // Check status multiple times
        assertThat(processor.getStatus("req_123").getStatus()).isEqualTo("PROCESSING");

        // Eventually fails
        ProcessStatus failedStatus = processor.getStatus("req_123");
        assertThat(failedStatus.getError()).isEqualTo("PROCESSING_FAILED");

        // Retry
        ProcessResult retryResult = processor.retryProcess("req_123");
        assertThat(retryResult.getStatus()).isEqualTo("RETRYING");
    }
}
```

## Spring Boot integration

### Spring Cloud Contract

#### Spring Cloud Contract with WireMock
```java
@SpringBootTest
@AutoConfigureWireMock
public class SpringCloudContractTest {

    @Autowired
    private FraudDetectionService fraudService;

    @Test
    void testFraudDetectionContract() {
        // Contract: When checking transaction for fraud
        // Given: Transaction details
        // When: POST /api/fraud/check with transaction data
        // Then: Return fraud score and decision

        stubFor(post(urlEqualTo("/api/fraud/check"))
            .withRequestBody(equalToJson("{ \"amount\": 1000.00, \"merchant\": \"suspicious-store\" }"))
            .willReturn(aResponse()
                .withStatus(200)
                .withHeader("Content-Type", "application/json")
                .withBody("{ \"score\": 85, \"decision\": \"REVIEW\", \"reasons\": [\"high_amount\", \"suspicious_merchant\"] }")));

        FraudCheckResult result = fraudService.checkTransaction(1000.00, "suspicious-store");

        assertThat(result.getScore()).isEqualTo(85);
        assertThat(result.getDecision()).isEqualTo(FraudDecision.REVIEW);
        assertThat(result.getReasons()).contains("high_amount", "suspicious_merchant");
    }

    @Test
    void testPaymentGatewayContract() {
        // Contract for payment gateway
        stubFor(post(urlEqualTo("/api/payments/charge"))
            .withHeader("Authorization", equalTo("Bearer merchant_key"))
            .withRequestBody(matchingJsonPath("$.amount"))
            .willReturn(aResponse()
                .withStatus(200)
                .withBody("{ \"transactionId\": \"txn_123\", \"status\": \"SUCCESS\", \"amount\": {{jsonPath request.body '$.amount'}} }")
                .withTransformers("response-template")));

        PaymentResult result = paymentGateway.charge(99.99, "merchant_key");

        assertThat(result.getTransactionId()).isEqualTo("txn_123");
        assertThat(result.getStatus()).isEqualTo("SUCCESS");
        assertThat(result.getAmount()).isEqualTo(99.99);
    }
}
```

### Spring Test integration

#### @SpringBootTest with WireMock
```java
@SpringBootTest
@AutoConfigureWireMock(port = 0) // Random port
public class SpringIntegrationTest {

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private WireMockServer wireMockServer;

    @Test
    void testRestTemplateWithWireMock() {
        // Configure WireMock using the injected server
        wireMockServer.stubFor(get(urlEqualTo("/api/test"))
            .willReturn(aResponse()
                .withStatus(200)
                .withBody("{\"message\": \"Hello from WireMock\"}")));

        // Use RestTemplate to call the stub
        ResponseEntity<String> response = restTemplate.getForEntity(
            "http://localhost:" + wireMockServer.port() + "/api/test", String.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).contains("Hello from WireMock");
    }

    @Test
    void testWebClientWithWireMock() {
        wireMockServer.stubFor(post(urlEqualTo("/api/data"))
            .willReturn(aResponse()
                .withStatus(201)
                .withBody("{\"id\": \"123\", \"created\": true}")));

        WebClient webClient = WebClient.builder()
            .baseUrl("http://localhost:" + wireMockServer.port())
            .build();

        Mono<String> response = webClient.post()
            .uri("/api/data")
            .bodyValue("{\"data\": \"test\"}")
            .retrieve()
            .bodyToMono(String.class);

        StepVerifier.create(response)
            .expectNextMatches(body -> body.contains("\"created\": true"))
            .verifyComplete();
    }
}
```

## Advanced scenarios

### Fault injection

#### Simulating failures
```java
@SpringBootTest
@WireMockTest
public class FaultInjectionTest {

    @Autowired
    private ResilientApiClient apiClient;

    @Test
    void testTimeoutHandling() {
        stubFor(get(urlEqualTo("/api/slow-service"))
            .willReturn(aResponse()
                .withStatus(200)
                .withBody("{\"result\": \"success\"}")
                .withFixedDelay(5000))); // 5 second delay

        assertThatThrownBy(() -> apiClient.callSlowService())
            .isInstanceOf(TimeoutException.class);
    }

    @Test
    void testCircuitBreaker() {
        // First few calls succeed
        stubFor(get(urlEqualTo("/api/unstable"))
            .willReturn(aResponse().withStatus(200))
            .inScenario("Circuit Breaker")
            .whenScenarioStateIs(STARTED)
            .willSetStateTo("SUCCESS_COUNT_1"));

        // Then start failing
        stubFor(get(urlEqualTo("/api/unstable"))
            .willReturn(aResponse().withStatus(500))
            .inScenario("Circuit Breaker")
            .whenScenarioStateIs("SUCCESS_COUNT_1")
            .willSetStateTo("FAILING"));

        // Test circuit breaker behavior
        for (int i = 0; i < 3; i++) {
            assertThat(apiClient.callUnstableService()).isEqualTo("success");
        }

        // Circuit should open after failures
        for (int i = 0; i < 5; i++) {
            assertThatThrownBy(() -> apiClient.callUnstableService())
                .isInstanceOf(CircuitBreakerOpenException.class);
        }
    }

    @Test
    void testRetryLogic() {
        // First call fails
        stubFor(get(urlEqualTo("/api/retry"))
            .inScenario("Retry Test")
            .whenScenarioStateIs(STARTED)
            .willReturn(aResponse().withStatus(500))
            .willSetStateTo("FAILED_ONCE"));

        // Second call succeeds
        stubFor(get(urlEqualTo("/api/retry"))
            .inScenario("Retry Test")
            .whenScenarioStateIs("FAILED_ONCE")
            .willReturn(aResponse().withStatus(200).withBody("success"))
            .willSetStateTo("SUCCEEDED"));

        // Test retry mechanism
        String result = apiClient.callWithRetry();

        assertThat(result).isEqualTo("success");
        // Verify retry was attempted
        verify(2, getRequestedFor(urlEqualTo("/api/retry")));
    }

    @Test
    void testRateLimiting() {
        // Allow first few requests
        stubFor(get(urlEqualTo("/api/rate-limited"))
            .willReturn(aResponse().withStatus(200))
            .inScenario("Rate Limiting")
            .whenScenarioStateIs(STARTED)
            .willSetStateTo("REQUEST_COUNT_1"));

        // Rate limit exceeded
        stubFor(get(urlEqualTo("/api/rate-limited"))
            .willReturn(aResponse()
                .withStatus(429)
                .withHeader("Retry-After", "60"))
            .inScenario("Rate Limiting")
            .whenScenarioStateIs("REQUEST_COUNT_1"));

        // Test rate limiting
        for (int i = 0; i < 5; i++) {
            if (i < 3) {
                assertThat(apiClient.callRateLimitedService()).isEqualTo("success");
            } else {
                assertThatThrownBy(() -> apiClient.callRateLimitedService())
                    .isInstanceOf(RateLimitExceededException.class);
            }
        }
    }
}
```

### Load testing simulation

#### Simulating different loads
```java
@SpringBootTest
@WireMockTest
public class LoadTestingTest {

    @Autowired
    private LoadTestClient loadClient;

    @Test
    void testUnderLoad() {
        // Normal load response
        stubFor(get(urlEqualTo("/api/load-test"))
            .willReturn(aResponse()
                .withStatus(200)
                .withBody("{\"responseTime\": 50, \"success\": true}")
                .withFixedDelay(50))); // 50ms delay

        // Simulate load testing
        LoadTestResults results = loadClient.runLoadTest(100, 10); // 100 req/sec for 10 sec

        assertThat(results.getTotalRequests()).isEqualTo(1000);
        assertThat(results.getAverageResponseTime()).isLessThan(200);
        assertThat(results.getErrorRate()).isEqualTo(0.0);
        assertThat(results.getThroughput()).isGreaterThan(80);
    }

    @Test
    void testDegradedPerformance() {
        // Degraded performance under load
        stubFor(get(urlEqualTo("/api/load-test"))
            .willReturn(aResponse()
                .withStatus(200)
                .withBody("{\"responseTime\": 2000, \"success\": true}")
                .withFixedDelay(2000))); // 2 second delay

        LoadTestResults results = loadClient.runLoadTest(50, 5);

        assertThat(results.getAverageResponseTime()).isGreaterThan(1500);
        assertThat(results.getThroughput()).isLessThan(30);
        // Some requests might timeout
        assertThat(results.getErrorRate()).isGreaterThan(0.0);
    }

    @Test
    void testServiceDegradation() {
        // Service starts degrading under load
        stubFor(get(urlEqualTo("/api/load-test"))
            .inScenario("Load Degradation")
            .whenScenarioStateIs(STARTED)
            .willReturn(aResponse()
                .withStatus(200)
                .withFixedDelay(100))
            .willSetStateTo("UNDER_LOAD"));

        stubFor(get(urlEqualTo("/api/load-test"))
            .inScenario("Load Degradation")
            .whenScenarioStateIs("UNDER_LOAD")
            .willReturn(aResponse()
                .withStatus(503)
                .withBody("{\"error\": \"Service temporarily unavailable\"}")
                .withFixedDelay(5000));

        LoadTestResults results = loadClient.runLoadTest(20, 10);

        // Should see some successes and some failures
        assertThat(results.getSuccessCount()).isGreaterThan(0);
        assertThat(results.getErrorCount()).isGreaterThan(0);
        assertThat(results.getErrorRate()).isBetween(0.3, 0.7);
    }
}
```

## Recording and playback

### Recording real interactions

#### Record and replay
```java
@SpringBootTest
public class RecordingTest {

    @Test
    void testRecording() {
        WireMockServer wireMockServer = new WireMockServer(options()
            .dynamicPort()
            .enableBrowserProxying(true)
            .withRootDirectory("src/test/resources/wiremock"));

        wireMockServer.start();

        // Configure proxy to real service
        // All requests to real service will be recorded

        wireMockServer.stop();
    }

    @Test
    void testPlayback() {
        // Recorded stubs are loaded from files
        WireMockServer wireMockServer = new WireMockServer(options()
            .dynamicPort()
            .withRootDirectory("src/test/resources/wiremock"));

        wireMockServer.start();

        // Now all recorded interactions are available as stubs
        // Tests can run against the recorded responses

        wireMockServer.stop();
    }
}
```

### Snapshot testing

#### Snapshot-based testing
```java
@SpringBootTest
@WireMockTest
public class SnapshotTest {

    @Autowired
    private ApiClient apiClient;

    @Test
    void testApiSnapshot() {
        // Take snapshot of current stubs
        WireMock.takeSnapshot();

        // Run tests against current API behavior
        List<User> users = apiClient.getAllUsers();
        assertThat(users).isNotEmpty();

        // Snapshot can be used for regression testing
        // If API changes, tests will fail and snapshot needs to be updated
    }

    @Test
    void testContractVerification() {
        // Load snapshot as baseline
        WireMock.resetToSnapshot();

        // Verify that current implementation matches recorded behavior
        User user = apiClient.getUser(1L);
        assertThat(user).isNotNull();
        assertThat(user.getEmail()).isNotNull();

        // Any deviation from recorded behavior will fail the test
    }
}
```

## Best practices

### 1. Test organization

#### Organizing WireMock tests
```java
@SpringBootTest
@WireMockTest
public class WireMockOrganizationTest {

    @Autowired
    private PaymentService paymentService;

    @Nested
    @DisplayName("Payment Processing")
    class PaymentProcessingTests {

        @Test
        @DisplayName("Should process successful payment")
        void testSuccessfulPayment() {
            stubPaymentSuccess();

            PaymentResult result = paymentService.processPayment(createValidPayment());

            assertThat(result.isSuccessful()).isTrue();
            assertThat(result.getTransactionId()).isNotNull();
        }

        @Test
        @DisplayName("Should handle payment failure")
        void testPaymentFailure() {
            stubPaymentFailure();

            assertThatThrownBy(() -> paymentService.processPayment(createInvalidPayment()))
                .isInstanceOf(PaymentException.class);
        }

        @Test
        @DisplayName("Should handle network timeout")
        void testNetworkTimeout() {
            stubNetworkTimeout();

            assertThatThrownBy(() -> paymentService.processPayment(createValidPayment()))
                .isInstanceOf(TimeoutException.class);
        }
    }

    @Nested
    @DisplayName("Payment Validation")
    class PaymentValidationTests {

        @Test
        @DisplayName("Should validate payment amount")
        void testAmountValidation() {
            stubValidationError("INVALID_AMOUNT");

            PaymentRequest request = new PaymentRequest(BigDecimal.ZERO, "card123");

            assertThatThrownBy(() -> paymentService.validatePayment(request))
                .isInstanceOf(ValidationException.class)
                .hasMessageContaining("INVALID_AMOUNT");
        }
    }

    private void stubPaymentSuccess() {
        stubFor(post(urlEqualTo("/api/payments"))
            .willReturn(aResponse()
                .withStatus(200)
                .withBody("{\"transactionId\": \"txn_123\", \"status\": \"SUCCESS\"}")));
    }

    private void stubPaymentFailure() {
        stubFor(post(urlEqualTo("/api/payments"))
            .willReturn(aResponse()
                .withStatus(400)
                .withBody("{\"error\": \"INSUFFICIENT_FUNDS\"}")));
    }

    private void stubNetworkTimeout() {
        stubFor(post(urlEqualTo("/api/payments"))
            .willReturn(aResponse()
                .withStatus(200)
                .withFixedDelay(10000))); // 10 second delay
    }

    private void stubValidationError(String errorCode) {
        stubFor(post(urlEqualTo("/api/payments/validate"))
            .willReturn(aResponse()
                .withStatus(400)
                .withBody("{\"error\": \"" + errorCode + "\"}")));
    }
}
```

### 2. Stub management

#### Managing stubs effectively
```java
@SpringBootTest
@WireMockTest
public class StubManagementTest {

    @Autowired
    private MultiServiceClient multiServiceClient;

    @BeforeEach
    void setUpStubs() {
        // Reset all stubs before each test
        WireMock.reset();

        // Common stubs for all tests
        stubUserService();
        stubPaymentService();
        stubNotificationService();
    }

    @Test
    void testCompleteWorkflow() {
        // Additional test-specific stubs
        stubInventoryService();

        WorkflowResult result = multiServiceClient.executeWorkflow(workflowData);

        assertThat(result.isSuccessful()).isTrue();
        verifyWorkflowCalls();
    }

    @Test
    void testWorkflowWithFailure() {
        // Override payment service to fail
        stubPaymentFailure();

        assertThatThrownBy(() -> multiServiceClient.executeWorkflow(workflowData))
            .isInstanceOf(WorkflowException.class);

        verifyErrorHandlingCalls();
    }

    private void stubUserService() {
        stubFor(get(urlMatching("/api/users/.*"))
            .willReturn(aResponse()
                .withStatus(200)
                .withBody("{\"id\": \"{{request.path.[2]}}\", \"name\": \"User {{request.path.[2]}}\"}")
                .withTransformers("response-template")));
    }

    private void stubPaymentService() {
        stubFor(post(urlEqualTo("/api/payments"))
            .willReturn(aResponse()
                .withStatus(200)
                .withBody("{\"transactionId\": \"txn_{{randomInt}}\", \"status\": \"SUCCESS\"}")
                .withTransformers("response-template")));
    }

    private void stubNotificationService() {
        stubFor(post(urlEqualTo("/api/notifications"))
            .willReturn(aResponse().withStatus(202)));
    }

    private void stubInventoryService() {
        stubFor(post(urlEqualTo("/api/inventory/reserve"))
            .willReturn(aResponse()
                .withStatus(200)
                .withBody("{\"reservationId\": \"res_{{randomInt}}\", \"available\": true}")
                .withTransformers("response-template")));
    }

    private void stubPaymentFailure() {
        stubFor(post(urlEqualTo("/api/payments"))
            .willReturn(aResponse()
                .withStatus(400)
                .withBody("{\"error\": \"PAYMENT_FAILED\"}")));
    }

    private void verifyWorkflowCalls() {
        verify(getRequestedFor(urlMatching("/api/users/.*")));
        verify(postRequestedFor(urlEqualTo("/api/payments")));
        verify(postRequestedFor(urlEqualTo("/api/notifications")));
        verify(postRequestedFor(urlEqualTo("/api/inventory/reserve")));
    }

    private void verifyErrorHandlingCalls() {
        verify(postRequestedFor(urlEqualTo("/api/payments")));
        verify(postRequestedFor(urlEqualTo("/api/notifications"))
            .withRequestBody(containing("PAYMENT_FAILED")));
    }
}
```

### 3. Verification strategies

#### Request verification
```java
@SpringBootTest
@WireMockTest
public class VerificationStrategiesTest {

    @Autowired
    private ApiClient apiClient;

    @Test
    void testRequestVerification() {
        stubFor(post(urlEqualTo("/api/data"))
            .willReturn(aResponse().withStatus(201)));

        // Make the request
        apiClient.postData("test data");

        // Verify the request was made
        verify(postRequestedFor(urlEqualTo("/api/data"))
            .withHeader("Content-Type", equalTo("application/json"))
            .withRequestBody(equalTo("test data")));
    }

    @Test
    void testRequestCountVerification() {
        stubFor(get(urlEqualTo("/api/status"))
            .willReturn(aResponse().withBody("OK")));

        // Make multiple requests
        for (int i = 0; i < 3; i++) {
            apiClient.getStatus();
        }

        // Verify exact count
        verify(3, getRequestedFor(urlEqualTo("/api/status")));
    }

    @Test
    void testRequestSequenceVerification() {
        stubFor(post(urlEqualTo("/api/workflow/step1")).willReturn(aResponse().withStatus(200)));
        stubFor(post(urlEqualTo("/api/workflow/step2")).willReturn(aResponse().withStatus(200)));
        stubFor(post(urlEqualTo("/api/workflow/step3")).willReturn(aResponse().withStatus(200)));

        // Execute workflow
        workflowService.executeWorkflow();

        // Verify sequence of calls
        verify(postRequestedFor(urlEqualTo("/api/workflow/step1")));
        verify(postRequestedFor(urlEqualTo("/api/workflow/step2")));
        verify(postRequestedFor(urlEqualTo("/api/workflow/step3")));
    }

    @Test
    void testNoUnexpectedCalls() {
        stubFor(get(urlEqualTo("/api/allowed"))
            .willReturn(aResponse().withBody("OK")));

        // Make expected call
        apiClient.getAllowedData();

        // Verify no calls to other endpoints
        verify(0, getRequestedFor(urlMatching("/api/.*"))
            .and(not(urlEqualTo("/api/allowed"))));
    }

    @Test
    void testRequestBodyVerification() {
        stubFor(post(urlEqualTo("/api/users"))
            .willReturn(aResponse().withStatus(201)));

        User user = new User("john@example.com", "John Doe");
        apiClient.createUser(user);

        // Verify request body contains expected data
        verify(postRequestedFor(urlEqualTo("/api/users"))
            .withRequestBody(matchingJsonPath("$.email", equalTo("john@example.com")))
            .withRequestBody(matchingJsonPath("$.name", equalTo("John Doe"))));
    }
}
```

## Troubleshooting

### Распространенные проблемы

#### Port conflicts

**Symptoms:**
- WireMock fails to start with port binding error

**Solutions:**
```java
// Use dynamic port
@WireMockTest
@SpringBootTest
public class PortConflictTest {

    @Test
    void testWithDynamicPort(WireMockRuntimeInfo wmRuntimeInfo) {
        int port = wmRuntimeInfo.getHttpPort();
        // Use the assigned port
    }
}

// Or specify a specific port
@SpringBootTest
@AutoConfigureWireMock(port = 9999)
public class SpecificPortTest {
    // WireMock will use port 9999
}
```

#### Stub not matching

**Symptoms:**
- Requests return 404 instead of expected response

**Solutions:**
```java
@SpringBootTest
@WireMockTest
public class StubMatchingTest {

    @Test
    void testStubMatching() {
        // Check what requests are actually made
        WireMock.configureFor("localhost", 8080);
        // Add logging
        // WireMock.addRequestListener(...);

        stubFor(get(urlEqualTo("/api/test"))
            .willReturn(aResponse().withBody("OK")));

        // Make request and check if it matches
        // If not matching, check URL, method, headers, body
    }

    @Test
    void testDebugStubMatching() {
        // Log all requests
        WireMock.addMockServiceRequestListener((request, response) -> {
            System.out.println("Request: " + request.getMethod() + " " + request.getUrl());
            System.out.println("Headers: " + request.getHeaders());
            System.out.println("Body: " + request.getBodyAsString());
        });

        stubFor(any(anyUrl())
            .willReturn(aResponse().withStatus(200)));

        // Make your request and see what WireMock receives
    }
}
```

#### Stubs not reset between tests

**Symptoms:**
- Stubs from previous tests affect current test

**Solutions:**
```java
@SpringBootTest
@WireMockTest
public class StubResetTest {

    @BeforeEach
    void resetStubs() {
        WireMock.reset();
    }

    @Test
    void test1() {
        stubFor(get(urlEqualTo("/api/test"))
            .willReturn(aResponse().withBody("test1")));

        // Test logic
    }

    @Test
    void test2() {
        stubFor(get(urlEqualTo("/api/test"))
            .willReturn(aResponse().withBody("test2")));

        // Test logic
    }
}
```

#### Response templating not working

**Symptoms:**
- Template expressions not replaced in responses

**Solutions:**
```java
@SpringBootTest
@WireMockTest
public class TemplatingTest {

    @Test
    void testTemplating() {
        stubFor(get(urlEqualTo("/api/test"))
            .willReturn(aResponse()
                .withBody("{\"value\": \"{{request.query.param}}\"}")
                .withTransformers("response-template"))); // Important!

        // Make request with query param
        // Response should contain the param value
    }
}
```

### Debug techniques

#### Request logging
```java
@SpringBootTest
@WireMockTest
public class DebugTechniquesTest {

    @BeforeEach
    void setUpDebugging() {
        // Log all requests
        WireMock.addMockServiceRequestListener((request, response) -> {
            System.out.println("=== WireMock Request ===");
            System.out.println("Method: " + request.getMethod());
            System.out.println("URL: " + request.getUrl());
            System.out.println("Headers: " + request.getHeaders());
            System.out.println("Body: " + request.getBodyAsString());
            System.out.println("========================");
        });
    }

    @Test
    void testWithLogging() {
        stubFor(any(anyUrl())
            .willReturn(aResponse().withStatus(200)));

        // Make your requests - they'll be logged
    }
}
```

#### Stub inspection
```java
@SpringBootTest
@WireMockTest
public class StubInspectionTest {

    @Test
    void testStubInspection() {
        // Create stub
        StubMapping stub = stubFor(get(urlEqualTo("/api/test"))
            .willReturn(aResponse().withBody("OK")));

        // Inspect stub
        System.out.println("Stub ID: " + stub.getId());
        System.out.println("Request: " + stub.getRequest());
        System.out.println("Response: " + stub.getResponse());

        // Verify stub was created
        List<StubMapping> stubs = WireMock.listAllStubMappings().getMappings();
        assertThat(stubs).hasSizeGreaterThan(0);
    }

    @Test
    void testRequestJournal() {
        // Make some requests
        // Then inspect the request journal
        List<ServeEvent> events = WireMock.getAllServeEvents();

        for (ServeEvent event : events) {
            System.out.println("Request: " + event.getRequest());
            System.out.println("Response: " + event.getResponse());
        }
    }
}
```

## Заключение

**WireMock** — это мощный инструмент для mocking HTTP сервисов в тестах. Он предоставляет полный контроль над HTTP взаимодействиями, позволяя создавать реалистичные тестовые сценарии для микросервисных архитектур.

### Ключевые возможности:

1. **HTTP Stubbing** — полная имитация HTTP серверов с любыми методами и ответами
2. **Request Matching** — гибкое сопоставление запросов по URL, headers, body, query params
3. **Response Templating** — динамические ответы с Handlebars templating
4. **Stateful Behavior** — поддержка сценариев с изменением состояния
5. **JUnit Integration** — seamless интеграция с JUnit 5
6. **Spring Boot Support** — интеграция с Spring экосистемой
7. **Recording** — запись реальных взаимодействий для последующего воспроизведения
8. **Verification** — проверка того, какие запросы были сделаны

### Архитектурные преимущества:

#### Isolation:
- **Service Virtualization** — полная изоляция от внешних зависимостей
- **Controlled Testing** — полный контроль над поведением зависимостей
- **Deterministic Tests** — предсказуемые и repeatable тесты
- **Offline Development** — разработка без работающих внешних сервисов

#### Flexibility:
- **Dynamic Responses** — ответы на основе входных данных
- **Fault Injection** — симуляция ошибок и сбоев
- **Load Simulation** — тестирование под нагрузкой
- **State Management** — сложные сценарии с сохранением состояния

### Когда использовать WireMock:

✅ **Microservices Testing** — тестирование взаимодействия между сервисами
✅ **API Integration** — тестирование клиентов внешних API
✅ **Contract Testing** — проверка контрактов между сервисами
✅ **Fault Tolerance** — тестирование resilience и error handling
✅ **CI/CD Pipelines** — fast и reliable тесты в CI
✅ **Offline Development** — разработка без внешних зависимостей
✅ **Load Testing** — симуляция различных нагрузок
✅ **Legacy Integration** — работа с legacy системами

### Когда НЕ использовать:

❌ **Unit Testing** — для unit тестов лучше Mockito
❌ **Simple Logic** — для простой логики без HTTP
❌ **Performance Critical** — WireMock добавляет overhead
❌ **Complex Business Logic** — не для тестирования business logic
❌ **Real Integration** — не заменяет реальные интеграционные тесты
❌ **Non-HTTP Protocols** — только для HTTP/HTTPS

### Best practices:

1. **Stub Management** — организованное управление stubs
2. **Request Verification** — проверка правильности вызовов
3. **Stateful Scenarios** — использование сценариев для complex flows
4. **Response Templating** — динамические ответы для реалистичности
5. **Error Simulation** — тестирование error handling
6. **Test Isolation** — изоляция тестов друг от друга
7. **Contract Definition** — определение контрактов через stubs
8. **CI Integration** — использование в automated pipelines

### Типы Stubbing по сложности:

#### Basic Stubbing:
- **Simple Responses** — статические ответы для простых случаев
- **Header Matching** — проверка headers в запросах
- **URL Patterns** — гибкое сопоставление URL
- **Query Parameters** — работа с query string

#### Advanced Stubbing:
- **Request Body Matching** — проверка JSON/XML в body
- **Dynamic Responses** — ответы на основе request data
- **Binary Content** — работа с файлами и binary data
- **Custom Matchers** — собственная логика matching

#### Stateful Stubbing:
- **Scenario Management** — управление состоянием через сценарии
- **State Transitions** — переходы между состояниями
- **Complex Workflows** — имитация бизнес-процессов
- **Error Recovery** — тестирование recovery scenarios

#### Specialized Stubbing:
- **Fault Injection** — симуляция сбоев и ошибок
- **Load Simulation** — имитация различных нагрузок
- **Authentication** — тестирование auth mechanisms
- **Rate Limiting** — симуляция throttling

WireMock является essential инструментом для testing microservices и distributed systems. Он позволяет создавать comprehensive и reliable тесты, обеспечивая полный контроль над external dependencies. 🚀

**Далее: REST Assured (API testing)**
