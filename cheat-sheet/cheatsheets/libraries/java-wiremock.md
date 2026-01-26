# WireMock

WireMock - это библиотека для создания mock-серверов HTTP API в тестах.

**Дата последнего обновления:** 2026-01-25

## Полезные ссылки

### Официальная документация
- [WireMock](http://wiremock.org/) - Официальный сайт
- [WireMock GitHub](https://github.com/wiremock/wiremock) - Репозиторий проекта
- [WireMock Documentation](http://wiremock.org/docs/) - Документация

### См. также
- `../testing/integration-testing/wiremock.md` - Интеграционное тестирование
- `../libraries/java-rest-assured.md` - REST Assured для тестирования API

## Содержание

- [Основные возможности](#основные-возможности)
  - [Mock Server Setup](#mock-server-setup)
  - [Request Matching](#request-matching)
  - [Response Templating](#response-templating)
  - [Stateful Behavior](#stateful-behavior)
- [Spring Boot Integration](#spring-boot-integration)
  - [HTTPS и SSL](#https-и-ssl)
  - [File Serving](#file-serving)
- [Продвинутые возможности](#продвинутые-возможности)
  - [Request Verification](#request-verification)
  - [Advanced Configuration](#advanced-configuration)
  - [Record and Playback](#record-and-playback)
  - [Custom Extensions](#custom-extensions)
- [JUnit 5 Support](#junit-5-support)
  - [Test Slices](#test-slices)
- [Best Practices](#best-practices)
  - [Configuration Properties](#configuration-properties)
  - [Организация тестов](#организация-тестов)
  - [DSL для сложных сценариев](#dsl-для-сложных-сценариев)

## Основные возможности

### Mock Server Setup
```java
/**
 * Демонстрация базового использования WireMock для создания mock HTTP сервера
 * WireMock позволяет создать фиктивный HTTP сервер для тестирования без реального API
 */
@Rule  // JUnit 4 Rule для автоматического управления WireMock сервером
public WireMockRule wireMockRule = new WireMockRule(8089);
// WireMockRule создает и управляет WireMock сервером на порту 8089
// Сервер автоматически запускается перед тестами и останавливается после

@Test
public void testExternalApiCall() {
    // Настройка mock-ответа - определяем что должен возвращать mock сервер
    // stubFor() регистрирует stub (заглушку) для определенного запроса
    stubFor(get(urlEqualTo("/api/users/1"))  // Ожидаем GET запрос на /api/users/1
        .willReturn(aResponse()               // Возвращаем ответ с настройками
            .withStatus(200)                  // HTTP статус 200 (OK)
            .withHeader("Content-Type", "application/json")  // Content-Type заголовок
            .withBody("{\"id\": 1, \"name\": \"John Doe\"}")));  // Тело ответа (JSON)

    // Вызов API - вызываем реальный метод который делает HTTP запрос
    // Запрос пойдет на WireMock сервер вместо реального API
    User user = userService.getUser(1);
    // userService.getUser() выполнит GET запрос на http://localhost:8089/api/users/1
    // WireMock вернет настроенный mock ответ

    // Проверка - проверяем что получили правильные данные из mock ответа
    assertEquals("John Doe", user.getName());
    // user должен содержать данные из mock ответа: id=1, name="John Doe"
}
```

### Request Matching
```java
/**
 * Демонстрация различных способов сопоставления запросов в WireMock
 * WireMock предоставляет гибкие способы определения какие запросы должны обрабатываться
 */
// Точное совпадение URL - запрос должен точно совпадать с указанным URL
stubFor(get(urlEqualTo("/api/users")));
// Обработает только GET запросы на точно /api/users (без query параметров)

// Совпадение с query параметрами - проверка query параметров в URL
stubFor(get(urlPathEqualTo("/api/users"))  // Проверяем только путь (без query параметров)
    .withQueryParam("status", equalTo("active")));  // Проверяем query параметр status=active
// Обработает GET /api/users?status=active но не /api/users?status=inactive

// Регулярные выражения - гибкое сопоставление URL через regex
stubFor(post(urlMatching("/api/users/[0-9]+")));  // [0-9]+ означает одну или более цифр
// Обработает POST /api/users/1, /api/users/123, но не /api/users/abc
// Полезно для динамических ID в URL

// JSON Path matching - проверка содержимого JSON тела запроса
stubFor(post(urlEqualTo("/api/webhook"))  // POST запрос на /api/webhook
    .withRequestBody(matchingJsonPath("$.eventType", equalTo("USER_CREATED"))));
// matchingJsonPath проверяет JSON тело запроса используя JSONPath выражение
// $.eventType - JSONPath выражение для получения поля eventType из корня JSON
// Обработает только запросы где JSON содержит {"eventType": "USER_CREATED"}

### Response Templating
```java
stubFor(get(urlEqualTo("/api/users/1"))
    .willReturn(aResponse()
        .withStatus(200)
        .withHeader("Content-Type", "application/json")
        .withBody("{ \"id\": 1, \"timestamp\": \"{{now}}\" }")
        .withTransformers("response-template")));
```

### Stateful Behavior
```java
// Первый вызов возвращает 404
stubFor(get(urlEqualTo("/api/resource"))
    .inScenario("Resource Scenario")
    .whenScenarioStateIs(STARTED)
    .willReturn(notFound())
    .willSetStateTo("Resource Created"));

// Второй вызов возвращает 200
stubFor(get(urlEqualTo("/api/resource"))
    .inScenario("Resource Scenario")
    .whenScenarioStateIs("Resource Created")
    .willReturn(okJson("{ \"status\": \"created\" }")));
```

### Spring Boot Integration
```java
@SpringBootTest
@AutoConfigureWireMock(port = 0)
public class UserServiceTest {

    @Autowired
    private WireMockServer wireMockServer;

    @Autowired
    private UserService userService;

    @Test
    public void testUserCreation() {
        wireMockServer.stubFor(post(urlEqualTo("/api/users"))
            .willReturn(created()
                .withHeader("Location", "/api/users/123")));

        User created = userService.createUser(new User("John", "Doe"));

        assertNotNull(created.getId());
    }
}
```

### HTTPS и SSL
```java
// Настройка HTTPS
wireMockRule = new WireMockRule(wireMockConfig()
    .httpsPort(8443)
    .keystorePath("src/test/resources/keystore.jks")
    .keystorePassword("password"));
```

### File Serving
```java
// Статические файлы
stubFor(get(urlEqualTo("/static/file.json"))
    .willReturn(aResponse()
        .withBodyFile("test-data.json")));

// Динамическое содержимое
stubFor(get(urlEqualTo("/api/data"))
    .willReturn(aResponse()
        .withBody("{{request.query.page}}")
        .withTransformers("response-template")));
```

### Request Verification
```java
// Проверка вызовов
verify(getRequestedFor(urlEqualTo("/api/users"))
    .withHeader("Authorization", containing("Bearer")));

// Количество вызовов
verify(exactly(2), postRequestedFor(urlEqualTo("/api/users")));

// Проверка тела запроса
verify(postRequestedFor(urlEqualTo("/api/webhook"))
    .withRequestBody(equalToJson("{ \"action\": \"create\" }")));
```

### Advanced Configuration
```java
WireMockConfiguration config = wireMockConfig()
    .port(8080)
    .httpsPort(8443)
    .bindAddress("127.0.0.1")
    .usingFilesUnderDirectory("/tmp/wiremock")
    .maxRequestJournalEntries(100)
    .jettyAcceptors(4)
    .jettyAcceptQueueSize(100);

wireMockServer = new WireMockServer(config);
```

### Record and Playback
```java
// Запись реальных вызовов
WireMock.startRecording("http://api.example.com");

// Воспроизведение записанных ответов
WireMock.stopRecording();
```

### Custom Extensions
```java
// Кастомный матчер
public class CustomMatcher extends RequestMatcherExtension {

    @Override
    public MatchResult match(Request request, Parameters parameters) {
        // Логика проверки запроса
        return MatchResult.exactMatch();
    }
}

// Регистрация расширения
wireMockServer.addMockServiceRequestListener(new AdminApiVersionCheckDisabledListener());
```

### JUnit 5 Support
```java
@RegisterExtension
static WireMockExtension wm = WireMockExtension.newInstance()
    .options(wireMockConfig().port(8080))
    .build();

@Test
public void testWithWireMock(WireMockRuntimeInfo wmRuntimeInfo) {
    WireMock wireMock = wmRuntimeInfo.getWireMock();

    wireMock.register(stubFor(get("/api/test")
        .willReturn(ok("Hello World"))));

    // Тестовая логика
}
```

## Integration с Spring

### Test Slices
```java
@SpringBootTest
@AutoConfigureWireMock(port = 0)
public class ApiClientIntegrationTest {

    @TestConfiguration
    static class TestConfig {
        @Bean
        @Primary
        public ApiClient testApiClient(WireMockServer wireMockServer) {
            return new ApiClient(wireMockServer.baseUrl());
        }
    }

    @Autowired
    private ApiClient apiClient;

    @Test
    void shouldHandleApiResponse() {
        // Настройка mock
        wireMockServer.stubFor(get("/api/data")
            .willReturn(okJson("""
                {
                    "items": [
                        {"id": 1, "name": "Item 1"},
                        {"id": 2, "name": "Item 2"}
                    ]
                }
                """)));

        // Выполнение теста
        List<Item> items = apiClient.getItems();

        assertThat(items).hasSize(2);
    }
}
```

### Configuration Properties
```yaml
wiremock:
  server:
    port: 8089
    https-port: 8443
  reset-mappings-after-each-test: true
```

## Лучшие практики

### Организация тестов
```java
public class WireMockTestBase {

    protected static final int WIREMOCK_PORT = 8089;

    @Rule
    public WireMockRule wireMockRule = new WireMockRule(WIREMOCK_PORT);

    protected void setupCommonStubs() {
        stubFor(get(urlEqualTo("/health"))
            .willReturn(ok().withBody("OK")));
    }

    @Before
    public void setUp() {
        setupCommonStubs();
    }
}
```

### DSL для сложных сценариев
```java
public class ApiMockDsl {

    private final WireMockServer server;

    public ApiMockDsl(WireMockServer server) {
        this.server = server;
    }

    public void mockUserApi() {
        server.stubFor(get("/api/users")
            .willReturn(okJson("[{\"id\":1,\"name\":\"John\"}]")));

        server.stubFor(post("/api/users")
            .willReturn(created().withHeader("Location", "/api/users/2")));
    }

    public void mockErrorScenario() {
        server.stubFor(get("/api/users")
            .willReturn(serverError().withBody("Internal Server Error")));
    }
}
```

## Дата последнего обновления
22 января 2026 г.

## Полезные ссылки
- [Официальная документация WireMock](https://wiremock.org/docs/)
- [GitHub репозиторий](https://github.com/wiremock/wiremock)
- [Spring Boot интеграция](https://docs.spring.io/spring-boot/docs/current/reference/htmlsingle/#testing.testcontainers.wiremock)
- [JUnit 5 расширение](https://wiremock.org/docs/junit-jupiter/)

## См. также
- [REST Assured](java-rest-assured.md) - для тестирования REST API
- [Testcontainers](java-testcontainers.md) - для интеграционных тестов с реальными контейнерами
- [Mockito](testing/mockito-advanced.md) - для unit тестирования
