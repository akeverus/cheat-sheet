# Вопросы на собеседовании: Integration Testing

**Комплексное руководство по вопросам собеседования на тему Integration Testing для Senior Java Developer. Включает детальные объяснения концепций, практические примеры на Java + Spring, best practices и troubleshooting.**

**Дата последнего обновления:** 2026-01-25


Integration testing является важной частью тестирования для Senior Java Developer. Он проверяет взаимодействие между компонентами системы и выявляет проблемы интеграции, которые не видны при unit тестировании.Дата последнего обновления: 2026-01-24

## Полезные ссылки

### Официальная документация
- [Spring Boot Testing](https://docs.spring.io/spring-boot/docs/current/reference/html/features.html#features.testing)
- [TestContainers](https://www.testcontainers.org/)
- [WireMock](https://wiremock.org/)

### Ресурсы
- [Testing Pyramid](https://martinfowler.com/bliki/TestPyramid.html)
- [Contract Testing](https://martinfowler.com/articles/consumerDrivenContracts.html)

### См. также
- `unit-testing-interview.md` - Unit тестирование
- `test-strategies-interview.md` - Стратегии тестирования
- `test-automation-interview.md` - Автоматизация тестирования

## Содержание

- [Q1. Что такое integration testing и чем он отличается от unit testing?](#q1-что-такое-integration-testing-и-чем-он-отличается-от-unit-testing)
- [Q2. Какие типы integration testing существуют?](#q2-какие-типы-integration-testing-существуют)
- [Q3. Как тестировать с базами данных?](#q3-как-тестировать-с-базами-данных)
- [Q4. Что такое TestContainers и как его использовать?](#q4-что-такое-testcontainers-и-как-его-использовать)
- [Q5. Как тестировать внешние API?](#q5-как-тестировать-внешние-api)
- [Q6. Что такое contract testing?](#q6-что-такое-contract-testing)
- [Q7. Как тестировать микросервисы?](#q7-как-тестировать-микросервисы)
- [Q8. Как использовать WireMock для тестирования?](#q8-как-использовать-wiremock-для-тестирования)
- [Q9. Как организовать интеграционные тесты в CI/CD?](#q9-как-организовать-интеграционные-тесты-в-cicd)
- [Q10. Какие best practices для integration testing?](#q10-какие-best-practices-для-integration-testing)

## Q1. Что такое integration testing и чем он отличается от unit testing?

Integration testing — это уровень тестирования программного обеспечения, при котором отдельные модули объединяются и тестируются как группа.

### Отличия от Unit Testing

| Аспект | Unit Testing | Integration Testing |
|--------|-------------|-------------------|
| **Scope** | Отдельный класс/метод | Группа компонентов |
| **Dependencies** | Mock/stub | Реальные зависимости |
| **Speed** | Быстрый (миллисекунды) | Медленный (секунды/минуты) |
| **Isolation** | Полная изоляция | Частичная изоляция |
| **Setup** | Минимальный | Сложный |
| **Flakiness** | Стабильные | Могут быть нестабильными |
| **Purpose** | Логика, алгоритмы | Взаимодействие, контракты |

### Пример: Unit vs Integration Testing

```java
// Unit тест - тестируем только бизнес-логику
@Test
void shouldCalculateDiscount() {
 DiscountService service = new DiscountService();
 
 BigDecimal discount = service.calculateDiscount(BigDecimal.valueOf(100), UserType.PREMIUM);
 
 assertEquals(BigDecimal.valueOf(15.00), discount);
}

// Integration тест - тестируем взаимодействие с БД
@Test
void shouldSaveOrderWithDiscount() {
 // Arrange
 User user = userRepository.save(new User("john@example.com", UserType.PREMIUM));
 Product product = productRepository.save(new Product("Laptop", BigDecimal.valueOf(1000)));
 
 // Act
 Order order = orderService.createOrder(user.getId(), product.getId(), 1);
 
 // Assert
 assertNotNull(order.getId());
 assertEquals(BigDecimal.valueOf(850), order.getTotalPrice()); // 1000 - 15% discount
 assertEquals(OrderStatus.CONFIRMED, order.getStatus());
}
```

### Когда использовать Integration Testing

1. Взаимодействие компонентов: Проверка корректности обмена данными
2. Внешние зависимости: Базы данных, внешние API, файловые системы
3. End-to-End сценарии: Полный пользовательский workflow
4. Контракты: Проверка соблюдения интерфейсов между модулями
5. Производительность: Тестирование под реальной нагрузкой

## Q2. Какие типы integration testing существуют?

### 1. Big Bang Integration Testing

Все компоненты интегрируются одновременно и тестируются как единое целое.Преимущества:
- Простота реализации
- Тестирование реального взаимодействия

**Недостатки:
- Трудно локализовать ошибки
- Долго настраивать
- Высокая сложность отладки

**Применение:
```java
@SpringBootTest
@ActiveProfiles("test")
public class BigBangIntegrationTest {
 
 @Autowired
 private UserService userService;
 
 @Autowired
 private OrderService orderService;
 
 @Autowired
 private PaymentService paymentService;
 
 @Test
 void shouldCompleteFullOrderFlow() {
 // Тестируем полный цикл: пользователь -> заказ -> оплата
 User user = userService.createUser("john@example.com", "password");
 Order order = orderService.createOrder(user.getId(), productId, 1);
 PaymentResult payment = paymentService.processPayment(order.getId(), paymentDetails);
 
 assertEquals(PaymentStatus.SUCCESS, payment.getStatus());
 assertEquals(OrderStatus.PAID, orderService.getOrder(order.getId()).getStatus());
 }
}
```

### 2. Top-Down Integration Testing

Начинаем с верхних уровней (UI/API) и постепенно спускаемся вниз, используя stubs для нижних уровней.Преимущества:
- Раннее тестирование основных функций
- Легче обнаруживать ошибки интерфейса

**Недостатки:
- Требует создания stubs
- Нижние уровни тестируются позже

### 3. Bottom-Up Integration Testing

Начинаем с нижних уровней (DAO, services) и постепенно поднимаемся вверх.Преимущества:
- Нижние уровни протестированы первыми
- Не нужны stubs для нижних уровней

**Недостатки:
- Позднее тестирование высокоуровневой логики
- Трудно тестировать пользовательские сценарии

### 4. Sandwich/Hybrid Integration Testing

Комбинация top-down и bottom-up подходов.Преимущества:
- Баланс между подходами
- Параллельное тестирование разных уровней

### 5. Component Integration Testing

Тестирование групп связанных компонентов как единого блока.

```java
// Тестируем слой сервисов без UI
@SpringBootTest
@AutoConfigureMockMvc
public class ServiceLayerIntegrationTest {
 
 @Autowired
 private MockMvc mockMvc;
 
 @Autowired
 private UserRepository userRepository;
 
 @Test
 void shouldCreateUserThroughAPI() throws Exception {
 String userJson = "{\"email\":\"john@example.com\",\"password\":\"secret\"}";
 
 mockMvc.perform(post("/api/users").contentType(MediaType.APPLICATION_JSON).content(userJson)).andExpect(status().isCreated()).andExpect(jsonPath("$.email").value("john@example.com"));
 
 // Проверяем, что пользователь сохранен в БД
 User savedUser = userRepository.findByEmail("john@example.com");
 assertNotNull(savedUser);
 assertNotNull(savedUser.getId());
 }
}
```

## Q3. Как тестировать с базами данных?

### 1. Embedded Databases

Использование in-memory баз данных для тестирования.

```java
@Configuration
@Profile("test")
public class TestDatabaseConfig {
 
 @Bean
 @Primary
 public DataSource dataSource() {
 return new EmbeddedDatabaseBuilder().setType(EmbeddedDatabaseType.H2).addScript("classpath:schema.sql").addScript("classpath:test-data.sql").build();
 }
}

// Тест с embedded БД
@SpringBootTest
@ActiveProfiles("test")
public class UserRepositoryIntegrationTest {
 
 @Autowired
 private UserRepository userRepository;
 
 @Autowired
 private JdbcTemplate jdbcTemplate;
 
 @Test
 void shouldSaveAndRetrieveUser() {
 // Arrange
 User user = new User("john@example.com", "password");
 
 // Act
 User saved = userRepository.save(user);
 
 // Assert
 assertNotNull(saved.getId());
 
 User retrieved = userRepository.findById(saved.getId()).orElse(null);
 assertNotNull(retrieved);
 assertEquals("john@example.com", retrieved.getEmail());
 }
 
 @Test
 void shouldHandleTransactions() {
 // Тестируем транзакционность
 assertThrows(DataIntegrityViolationException.class, () -> {
 userRepository.save(new User(null, "password")); // email is required
 });
 }
}
```

### 2. TestContainers

Использование реальных Docker контейнеров для тестирования.

```java
@SpringBootTest
@Testcontainers
public class UserRepositoryContainerTest {
 
 @Container
 private static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:13").withDatabaseName("testdb").withUsername("test").withPassword("test");
 
 @DynamicPropertySource
 static void configureProperties(DynamicPropertyRegistry registry) {
 registry.add("spring.datasource.url", postgres::getJdbcUrl);
 registry.add("spring.datasource.username", postgres::getUsername);
 registry.add("spring.datasource.password", postgres::getPassword);
 }
 
 @Autowired
 private UserRepository userRepository;
 
 @Test
 void shouldWorkWithRealPostgres() {
 User user = new User("john@example.com", "password");
 User saved = userRepository.save(user);
 
 assertNotNull(saved.getId());
 
 // Тестируем специфичные для PostgreSQL фичи
 List<User> users = userRepository.findByEmailContaining("john");
 assertEquals(1, users.size());
 }
}

// Тест с MongoDB
@SpringBootTest
@Testcontainers
public class ProductRepositoryMongoTest {
 
 @Container
 private static MongoDBContainer mongoDBContainer = new MongoDBContainer("mongo:4.4");
 
 @DynamicPropertySource
 static void configureProperties(DynamicPropertyRegistry registry) {
 registry.add("spring.data.mongodb.uri", mongoDBContainer::getReplicaSetUrl);
 }
 
 @Autowired
 private ProductRepository productRepository;
 
 @Test
 void shouldWorkWithRealMongoDB() {
 Product product = new Product("Laptop", BigDecimal.valueOf(1000));
 Product saved = productRepository.save(product);
 
 assertNotNull(saved.getId());
 
 // Тестируем MongoDB специфичные запросы
 List<Product> expensiveProducts = productRepository.findByPriceGreaterThan(BigDecimal.valueOf(500));
 assertEquals(1, expensiveProducts.size());
 }
}
```

### 3. Database Migration Testing

```java
@SpringBootTest
@ActiveProfiles("test")
@Sql(scripts = "/db/migration/V1__create_tables.sql")
public class DatabaseMigrationTest {
 
 @Autowired
 private JdbcTemplate jdbcTemplate;
 
 @Test
 void shouldApplyMigrationsCorrectly() {
 // Проверяем, что таблицы созданы
 Integer tableCount = jdbcTemplate.queryForObject(
 "SELECT COUNT(*) FROM information_schema.tables WHERE table_schema = 'public'",
 Integer.class);
 
 assertTrue(tableCount > 0);
 
 // Проверяем структуру таблиц
 List<String> columns = jdbcTemplate.queryForList(
 "SELECT column_name FROM information_schema.columns WHERE table_name = 'users'",
 String.class);
 
 assertTrue(columns.contains("id"));
 assertTrue(columns.contains("email"));
 assertTrue(columns.contains("password"));
 }
}
```

### 4. Transactional Tests

```java
@SpringBootTest
@ActiveProfiles("test")
public class TransactionIntegrationTest {
 
 @Autowired
 private UserService userService;
 
 @Autowired
 private UserRepository userRepository;
 
 @Test
 @Transactional
 void shouldRollbackTransactionOnFailure() {
 // Arrange
 User user = new User("john@example.com", "password");
 
 // Act & Assert
 assertThrows(RuntimeException.class, () -> {
 userService.createUserWithProfile(user, null); // Профиль null - ошибка
 });
 
 // Проверяем, что пользователь не сохранен (rollback)
 Optional<User> savedUser = userRepository.findByEmail("john@example.com");
 assertFalse(savedUser.isPresent());
 }
 
 @Test
 void shouldCommitTransactionOnSuccess() {
 // Arrange
 User user = new User("jane@example.com", "password");
 UserProfile profile = new UserProfile("Jane", "Doe");
 
 // Act
 userService.createUserWithProfile(user, profile);
 
 // Assert
 Optional<User> savedUser = userRepository.findByEmail("jane@example.com");
 assertTrue(savedUser.isPresent());
 assertNotNull(savedUser.get().getProfile());
 }
}
```

## Q4. Что такое TestContainers и как его использовать?

TestContainers — это Java библиотека для запуска Docker контейнеров в JUnit тестах.

### Преимущества TestContainers

1. Реальные зависимости: Тестирование с реальными базами данных, брокерами сообщений
2. Изоляция: Каждый тест получает чистую среду
3. Совместимость: Работает с любыми Docker образами
4. Автоматизация: Автоматический запуск и остановка контейнеров

### Основные компоненты

#### 1. Generic Containers

```java
@SpringBootTest
@Testcontainers
public class GenericContainerTest {
 
 @Container
 private static GenericContainer<?> redis = new GenericContainer<>("redis:6-alpine").withExposedPorts(6379);
 
 @DynamicPropertySource
 static void configureProperties(DynamicPropertyRegistry registry) {
 registry.add("spring.redis.host", redis::getHost);
 registry.add("spring.redis.port", redis::getFirstMappedPort);
 }
 
 @Autowired
 private RedisService redisService;
 
 @Test
 void shouldWorkWithRealRedis() {
 redisService.save("key", "value");
 String retrieved = redisService.get("key");
 
 assertEquals("value", retrieved);
 }
}
```

#### 2. Specialized Containers

```java
@SpringBootTest
@Testcontainers
public class SpecializedContainerTest {
 
 @Container
 private static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:13").withDatabaseName("testdb").withUsername("test").withPassword("test").withInitScript("init.sql"); // Запуск скрипта инициализации
 
 @Container
 private static RabbitMQContainer rabbitMQ = new RabbitMQContainer("rabbitmq:3-management").withAdminPassword("admin");
 
 @DynamicPropertySource
 static void configureProperties(DynamicPropertyRegistry registry) {
 // PostgreSQL
 registry.add("spring.datasource.url", postgres::getJdbcUrl);
 registry.add("spring.datasource.username", postgres::getUsername);
 registry.add("spring.datasource.password", postgres::getPassword);
 
 // RabbitMQ
 registry.add("spring.rabbitmq.host", rabbitMQ::getHost);
 registry.add("spring.rabbitmq.port", rabbitMQ::getAmqpPort);
 registry.add("spring.rabbitmq.username", () -> "guest");
 registry.add("spring.rabbitmq.password", () -> "guest");
 }
 
 @Autowired
 private OrderService orderService;
 
 @Autowired
 private RabbitTemplate rabbitTemplate;
 
 @Test
 void shouldProcessOrderWithRealServices() {
 // Создаем заказ в PostgreSQL
 Order order = orderService.createOrder(customerId, productId, 1);
 
 // Проверяем отправку сообщения в RabbitMQ
 Object message = rabbitTemplate.receiveAndConvert("order.queue", 5000);
 assertNotNull(message);
 
 // Проверяем статус заказа
 Order updatedOrder = orderService.getOrder(order.getId());
 assertEquals(OrderStatus.PROCESSING, updatedOrder.getStatus());
 }
}
```

#### 3. Compose Containers

```java
@SpringBootTest
@Testcontainers
public class ComposeContainerTest {
 
 @Container
 private static DockerComposeContainer<?> environment = 
 new DockerComposeContainer<>(new File("docker-compose-test.yml")).withExposedService("postgres_1", 5432).withExposedService("redis_1", 6379).waitingFor("postgres_1", Wait.forHealthcheck());
 
 @DynamicPropertySource
 static void configureProperties(DynamicPropertyRegistry registry) {
 // Получаем URL из docker-compose
 String postgresUrl = environment.getServiceHost("postgres_1", 5432) + ":" +
 environment.getServicePort("postgres_1", 5432);
 registry.add("spring.datasource.url", () -> "jdbc:postgresql://" + postgresUrl + "/testdb");
 
 String redisHost = environment.getServiceHost("redis_1", 6379);
 Integer redisPort = environment.getServicePort("redis_1", 6379);
 registry.add("spring.redis.host", () -> redisHost);
 registry.add("spring.redis.port", () -> redisPort);
 }
 
 // docker-compose-test.yml
 /*
 version: '3.8'
 services:
 postgres:
 image: postgres:13
 environment:
 POSTGRES_DB: testdb
 POSTGRES_USER: test
 POSTGRES_PASSWORD: test
 ports:
 - "5432:5432"
 healthcheck:
 test: ["CMD-SHELL", "pg_isready -U test -d testdb"]
 interval: 10s
 timeout: 5s
 retries: 5
 
 redis:
 image: redis:6-alpine
 ports:
 - "6379:6379"
 */
}
```

#### 4. Container Lifecycle

```java
@SpringBootTest
@Testcontainers
public class ContainerLifecycleTest {
 
 @Container
 private static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:13").withDatabaseName("testdb").withUsername("test").withPassword("test");
 
 @Autowired
 private UserRepository userRepository;
 
 @BeforeAll
 static void setUpAll() {
 // Контейнер уже запущен JUnit 5
 assertTrue(postgres.isRunning());
 }
 
 @Test
 void shouldReuseContainerAcrossTests() {
 // Контейнер переиспользуется между тестами
 User user = new User("test@example.com", "password");
 User saved = userRepository.save(user);
 
 assertNotNull(saved.getId());
 }
 
 @Test
 void shouldHaveCleanDatabaseForEachTest() {
 // Каждый тест получает чистую БД
 List<User> users = userRepository.findAll();
 assertTrue(users.isEmpty());
 }
 
 @AfterAll
 static void tearDownAll() {
 // Контейнер будет остановлен автоматически
 }
}
```

## Q5. Как тестировать внешние API?

### 1. Mock External APIs

```java
@SpringBootTest
@ActiveProfiles("test")
public class ExternalApiMockTest {
 
 @MockBean
 private RestTemplate restTemplate;
 
 @Autowired
 private PaymentService paymentService;
 
 @Test
 void shouldProcessPaymentWithMockedApi() {
 // Arrange
 PaymentRequest request = new PaymentRequest("4111111111111111", BigDecimal.valueOf(100));
 
 PaymentResponse mockResponse = new PaymentResponse("txn_123", PaymentStatus.SUCCESS);
 when(restTemplate.postForObject(anyString(), any(), eq(PaymentResponse.class))).thenReturn(mockResponse);
 
 // Act
 PaymentResult result = paymentService.processPayment(request);
 
 // Assert
 assertEquals(PaymentStatus.SUCCESS, result.getStatus());
 assertEquals("txn_123", result.getTransactionId());
 
 // Verify external API call
 verify(restTemplate).postForObject(
 eq("https://api.payment-gateway.com/charge"), 
 any(PaymentRequest.class), 
 eq(PaymentResponse.class));
 }
}
```

### 2. WireMock for HTTP APIs

```java
@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureWireMock(port = 0) // Random port
public class WireMockIntegrationTest {
 
 @Autowired
 private PaymentService paymentService;
 
 @Test
 void shouldHandleSuccessfulPayment() {
 // Arrange - настройка mock ответа
 stubFor(post(urlEqualTo("/api/charge")).withRequestBody(matchingJsonPath("$.amount", equalTo("100.00"))).willReturn(aResponse().withStatus(200).withHeader("Content-Type", "application/json").withBody("""
 {
 "transactionId": "txn_123",
 "status": "SUCCESS"
 }
 """)));
 
 // Act
 PaymentResult result = paymentService.processPayment(cardDetails, BigDecimal.valueOf(100));
 
 // Assert
 assertEquals(PaymentStatus.SUCCESS, result.getStatus());
 assertEquals("txn_123", result.getTransactionId());
 }
 
 @Test
 void shouldHandlePaymentFailure() {
 // Arrange
 stubFor(post(urlEqualTo("/api/charge")).willReturn(aResponse().withStatus(400).withHeader("Content-Type", "application/json").withBody("""
 {
 "error": "INSUFFICIENT_FUNDS",
 "message": "Not enough funds"
 }
 """)));
 
 // Act & Assert
 assertThrows(InsufficientFundsException.class, () -> {
 paymentService.processPayment(cardDetails, BigDecimal.valueOf(100));
 });
 }
 
 @Test
 void shouldHandleTimeout() {
 // Arrange
 stubFor(post(urlEqualTo("/api/charge")).willReturn(aResponse().withFixedDelay(5000) // 5 second delay.withStatus(200)));
 
 // Act & Assert
 assertThrows(PaymentTimeoutException.class, () -> {
 paymentService.processPayment(cardDetails, BigDecimal.valueOf(100));
 });
 }
}
```

### 3. Contract Testing with Pact

```java
// Consumer side - PaymentService
@ExtendWith(PactConsumerTestExt.class)
@PactTestFor(providerName = "PaymentProvider", port = "8081")
public class PaymentServiceContractTest {
 
 @Pact(consumer = "OrderService")
 public RequestResponsePact successfulPayment(PactDslWithProvider builder) {
 return builder.given("Payment provider is available").uponReceiving("A valid payment request").path("/api/charge").method("POST").body(new PactDslJsonBody().stringType("cardNumber", "4111111111111111").decimalType("amount", 100.00)).willRespondWith().status(200).body(new PactDslJsonBody().stringType("transactionId").stringType("status", "SUCCESS")).toPact();
 }
 
 @Test
 @PactTestFor(pactMethod = "successfulPayment")
 void shouldProcessSuccessfulPayment(MockServer mockServer) {
 // Configure service to use mock server
 PaymentService paymentService = new PaymentService(mockServer.getUrl());
 
 // Act
 PaymentResult result = paymentService.processPayment("4111111111111111", 100.00);
 
 // Assert
 assertEquals(PaymentStatus.SUCCESS, result.getStatus());
 assertNotNull(result.getTransactionId());
 }
}

// Provider side - Payment API
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Provider("PaymentProvider")
@PactFolder("pacts")
public class PaymentProviderContractTest {
 
 @Autowired
 private TestRestTemplate restTemplate;
 
 @State("Payment provider is available")
 public void paymentProviderIsAvailable() {
 // Setup provider state if needed
 }
 
 @Test
 void shouldHonorPaymentContract() {
 // Pact verification will run automatically
 }
}
```

## Q6. Что такое contract testing?

Contract testing — это подход к тестированию, при котором проверяется соблюдение контрактов между сервисами.

### Типы Contract Testing

#### 1. Consumer-Driven Contract Testing

Потребитель определяет ожидаемое поведение провайдера.

```java
// Consumer test (Pact)
@ExtendWith(PactConsumerTestExt.class)
@PactTestFor(providerName = "UserService", port = "8082")
public class UserServiceConsumerTest {
 
 @Pact(consumer = "OrderService")
 public RequestResponsePact getUserDetails(PactDslWithProvider builder) {
 return builder.given("User exists").uponReceiving("A request for user details").path("/api/users/123").method("GET").willRespondWith().status(200).body(new PactDslJsonBody().numberType("id", 123).stringType("name", "John Doe").stringType("email", "john@example.com")).toPact();
 }
 
 @Test
 @PactTestFor(pactMethod = "getUserDetails")
 void shouldGetUserDetails(MockServer mockServer) {
 UserClient client = new UserClient(mockServer.getUrl());
 User user = client.getUser(123L);
 
 assertEquals(123L, user.getId());
 assertEquals("John Doe", user.getName());
 assertEquals("john@example.com", user.getEmail());
 }
}
```

#### 2. Provider Contract Testing

Провайдер проверяет, что он корректно реализует контракты.

```java
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Provider("UserService")
@PactFolder("pacts")
public class UserServiceProviderTest {
 
 @Autowired
 private TestRestTemplate restTemplate;
 
 @State("User exists")
 public void userExists() {
 // Setup test data
 User user = new User(123L, "John Doe", "john@example.com");
 userRepository.save(user);
 }
 
 @Test
 void shouldHonorUserContract() {
 // Pact framework automatically verifies contracts
 }
}
```

### Spring Cloud Contract

```groovy
// Contract definition (Groovy DSL)
org.springframework.cloud.contract.spec.Contract.make {
 request {
 method 'GET'
 url '/api/users/123'
 }
 response {
 status 200
 body(
 id: 123,
 name: "John Doe",
 email: "john@example.com"
 )
 }
}
```

```java
// Generated test
@SpringBootTest
@AutoConfigureStubRunner(ids = "com.example:user-service:+:stubs:8082")
public class UserServiceContractTest {
 
 @Autowired
 private UserClient userClient;
 
 @Test
 void shouldGetUserDetails() {
 User user = userClient.getUser(123L);
 
 assertEquals(123L, user.getId());
 assertEquals("John Doe", user.getName());
 assertEquals("john@example.com", user.getEmail());
 }
}
```

### Преимущества Contract Testing

1. Раннее обнаружение изменений: Выявление breaking changes до релиза
2. Изоляция команд: Команды могут работать независимо
3. Автоматизация: Автоматическая проверка контрактов
4. Документация: Контракты служат документацией API

## Q7. Как тестировать микросервисы?

### 1. Component Testing

Тестирование отдельных микросервисов в изоляции.

```java
@SpringBootTest(
 webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
 properties = {
 "eureka.client.enabled=false", // Отключаем service discovery
 "spring.cloud.config.enabled=false" // Отключаем config server
 }
)
@ActiveProfiles("component-test")
public class UserServiceComponentTest {
 
 @Autowired
 private TestRestTemplate restTemplate;
 
 @MockBean
 private EmailService emailService; // Mock внешние зависимости
 
 @Test
 void shouldCreateUser() {
 UserRequest request = new UserRequest("john@example.com", "password");
 
 ResponseEntity<UserResponse> response = restTemplate.postForEntity(
 "/api/users", request, UserResponse.class);
 
 assertEquals(HttpStatus.CREATED, response.getStatusCode());
 assertNotNull(response.getBody().getId());
 assertEquals("john@example.com", response.getBody().getEmail());
 
 // Verify interactions with mocked services
 verify(emailService).sendWelcomeEmail("john@example.com");
 }
}
```

### 2. Integration Testing with Real Dependencies

```java
@SpringBootTest(
 webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
 properties = {
 "spring.profiles.active=test",
 "eureka.client.enabled=false"
 }
)
@Testcontainers
public class UserServiceIntegrationTest {
 
 @Container
 private static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:13");
 
 @Container
 private static RabbitMQContainer rabbitMQ = new RabbitMQContainer("rabbitmq:3-management");
 
 @DynamicPropertySource
 static void configureProperties(DynamicPropertyRegistry registry) {
 registry.add("spring.datasource.url", postgres::getJdbcUrl);
 registry.add("spring.rabbitmq.host", rabbitMQ::getHost);
 registry.add("spring.rabbitmq.port", rabbitMQ::getAmqpPort);
 }
 
 @Autowired
 private TestRestTemplate restTemplate;
 
 @Autowired
 private RabbitTemplate rabbitTemplate;
 
 @Test
 void shouldCreateUserAndSendEvent() {
 UserRequest request = new UserRequest("john@example.com", "password");
 
 // Create user
 ResponseEntity<UserResponse> response = restTemplate.postForEntity(
 "/api/users", request, UserResponse.class);
 
 assertEquals(HttpStatus.CREATED, response.getStatusCode());
 
 // Verify message was sent to RabbitMQ
 Message message = rabbitTemplate.receive("user.created", 5000);
 assertNotNull(message);
 
 // Parse message
 UserCreatedEvent event = objectMapper.readValue(message.getBody(), UserCreatedEvent.class);
 assertEquals(response.getBody().getId(), event.getUserId());
 }
}
```

### 3. End-to-End Testing

```java
@SpringBootTest(
 classes = {E2ETestConfiguration.class},
 webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT
)
@Testcontainers
public class E2ETest {
 
 @Container
 private static DockerComposeContainer<?> environment = 
 new DockerComposeContainer<>(new File("docker-compose.e2e.yml")).withExposedService("user-service", 8080).withExposedService("order-service", 8081).withExposedService("payment-service", 8082);
 
 @Autowired
 private WebTestClient webTestClient;
 
 @Test
 void shouldCompleteFullOrderFlow() {
 // 1. Create user
 UserResponse user = createUser("john@example.com", "password");
 
 // 2. Create product via product service
 ProductResponse product = createProduct("Laptop", BigDecimal.valueOf(1000));
 
 // 3. Create order
 OrderResponse order = createOrder(user.getId(), product.getId(), 1);
 
 // 4. Process payment
 PaymentResponse payment = processPayment(order.getId(), "4111111111111111");
 
 // 5. Verify order status
 OrderResponse updatedOrder = getOrder(order.getId());
 assertEquals(OrderStatus.COMPLETED, updatedOrder.getStatus());
 assertEquals(BigDecimal.valueOf(1000), updatedOrder.getTotalPrice());
 }
 
 private UserResponse createUser(String email, String password) {
 return webTestClient.post().uri("http://user-service:8080/api/users").contentType(MediaType.APPLICATION_JSON).bodyValue(new UserRequest(email, password)).exchange().expectStatus().isCreated().expectBody(UserResponse.class).returnResult().getResponseBody();
 }
 
 // Similar methods for other services...
}
```

### 4. Consumer-Driven Contract Testing

```java
// Order Service - Consumer of User Service
@ExtendWith(PactConsumerTestExt.class)
@PactTestFor(providerName = "UserService", port = "8080")
public class OrderServiceContractTest {
 
 @Pact(consumer = "OrderService")
 public RequestResponsePact getUserDetails(PactDslWithProvider builder) {
 return builder.uponReceiving("A request for user details").path("/api/users/123").method("GET").willRespondWith().status(200).body(new PactDslJsonBody().numberType("id", 123).stringType("email", "john@example.com")).toPact();
 }
 
 @Test
 @PactTestFor(pactMethod = "getUserDetails")
 void shouldValidateOrderForExistingUser(MockServer mockServer) {
 OrderService orderService = new OrderService(mockServer.getUrl());
 
 // This will use the mocked UserService
 boolean isValid = orderService.validateUserForOrder(123L, orderDetails);
 
 assertTrue(isValid);
 }
}
```

### 5. Chaos Engineering Testing

```java
@SpringBootTest
@Testcontainers
public class ChaosEngineeringTest {
 
 @Container
 private static DockerComposeContainer<?> environment = 
 new DockerComposeContainer<>(new File("docker-compose.test.yml"));
 
 @Autowired
 private OrderService orderService;
 
 @Test
 void shouldHandleServiceFailure() {
 // Simulate payment service failure
 simulateServiceFailure("payment-service");
 
 // Try to create order
 assertThrows(ServiceUnavailableException.class, () -> {
 orderService.createOrder(userId, productId, 1);
 });
 
 // Verify order was not created (compensating transaction)
 assertEquals(0, orderRepository.count());
 }
 
 @Test
 void shouldHandleNetworkLatency() {
 // Introduce network latency
 simulateNetworkLatency("user-service", 5000); // 5 second delay
 
 // Measure response time
 long startTime = System.currentTimeMillis();
 User user = userService.getUser(userId);
 long responseTime = System.currentTimeMillis() - startTime;
 
 assertTrue(responseTime > 5000);
 assertNotNull(user);
 }
 
 @Test
 void shouldRecoverFromServiceRestart() {
 // Restart user service
 restartService("user-service");
 
 // Service should be available after restart
 await().atMost(30, SECONDS).until(() -> {
 try {
 return userService.getUser(userId)!= null;
 } catch (Exception e) {
 return false;
 }
 });
 
 User user = userService.getUser(userId);
 assertNotNull(user);
 }
}
```

## Q8. Как использовать WireMock для тестирования?

WireMock — это инструмент для мокирования HTTP сервисов в тестах.

### 1. Basic WireMock Setup

```java
@SpringBootTest
@AutoConfigureWireMock(port = 0)
public class WireMockBasicTest {
 
 @Autowired
 private ExternalApiClient apiClient;
 
 @Test
 void shouldGetUserData() {
 // Arrange
 stubFor(get(urlEqualTo("/api/users/123")).willReturn(aResponse().withStatus(200).withHeader("Content-Type", "application/json").withBody("""
 {
 "id": 123,
 "name": "John Doe",
 "email": "john@example.com"
 }
 """)));
 
 // Act
 User user = apiClient.getUser(123L);
 
 // Assert
 assertEquals(123L, user.getId());
 assertEquals("John Doe", user.getName());
 assertEquals("john@example.com", user.getEmail());
 
 // Verify request
 verify(getRequestedFor(urlEqualTo("/api/users/123")));
 }
}
```

### 2. Advanced Request Matching

```java
@Test
void shouldHandleComplexRequests() {
 // Match by URL path with parameters
 stubFor(get(urlPathEqualTo("/api/search")).withQueryParam("q", equalTo("laptop")).withQueryParam("category", equalTo("electronics")).withHeader("Authorization", equalTo("Bearer token123")).willReturn(aResponse().withStatus(200).withBody("search results...")));
 
 // Match by request body
 stubFor(post(urlEqualTo("/api/orders")).withRequestBody(matchingJsonPath("$.total", equalTo("100.00"))).withRequestBody(matchingJsonPath("$.items[0].name", equalTo("Laptop"))).willReturn(aResponse().withStatus(201).withBody("order created")));
 
 // Match by custom matcher
 stubFor(put(urlMatching("/api/users/\\d+")).andMatching(request -> {
 String body = request.getBodyAsString();
 return body.contains("premium")? MatchResult.exactMatch(): MatchResult.noMatch();
 }).willReturn(aResponse().withStatus(200)));
}
```

### 3. Response Templating

```java
@Test
void shouldUseResponseTemplating() {
 stubFor(get(urlEqualTo("/api/users/123")).willReturn(aResponse().withStatus(200).withHeader("Content-Type", "application/json").withBody("""
 {
 "id": 123,
 "name": "{{request.query.q}}",
 "timestamp": "{{now}}",
 "randomId": "{{randomValue length=8 type='ALPHANUMERIC'}}"
 }
 """)));
 
 User user = apiClient.getUser(123L);
 
 assertEquals(123L, user.getId());
 assertNotNull(user.getTimestamp());
 assertNotNull(user.getRandomId());
}
```

### 4. State Management

```java
@Test
void shouldManageStateBetweenRequests() {
 // Initial state
 stubFor(get(urlEqualTo("/api/counter")).inScenario("Counter Scenario").whenScenarioStateIs(STARTED).willReturn(aResponse().withBody("0")).willSetStateTo("Incremented"));
 
 // After increment
 stubFor(post(urlEqualTo("/api/counter/increment")).inScenario("Counter Scenario").whenScenarioStateIs("Incremented").willReturn(aResponse().withBody("1")).willSetStateTo("Incremented Again"));
 
 // Verify initial state
 assertEquals("0", apiClient.getCounter());
 
 // Increment
 apiClient.incrementCounter();
 
 // Verify incremented state
 assertEquals("1", apiClient.getCounter());
}
```

### 5. Fault Injection

```java
@Test
void shouldHandleTimeouts() {
 stubFor(get(urlEqualTo("/api/slow-service")).willReturn(aResponse().withFixedDelay(5000) // 5 second delay.withStatus(200).withBody("slow response")));
 
 assertThrows(TimeoutException.class, () -> {
 apiClient.callSlowService();
 });
}

@Test
void shouldHandleServerErrors() {
 stubFor(get(urlEqualTo("/api/unstable-service")).willReturn(aResponse().withStatus(500).withBody("Internal Server Error")));
 
 assertThrows(ApiException.class, () -> {
 apiClient.callUnstableService();
 });
}

@Test
void shouldHandleNetworkFailures() {
 // Simulate connection refused
 removeStub(get(urlEqualTo("/api/failing-service")));
 
 assertThrows(ConnectionException.class, () -> {
 apiClient.callFailingService();
 });
}
```

### 6. WireMock Extensions

```java
@Configuration
public class WireMockConfig {
 
 @Bean
 @Primary
 public WireMockServer wireMockServer() {
 WireMockServer server = new WireMockServer(options().port(8089).extensions(new ResponseTemplateTransformer(true)));
 
 // Custom response transformer
 server.addMockServiceRequestListener(new CustomRequestListener());
 
 return server;
 }
 
 static class CustomRequestListener implements RequestListener {
 @Override
 public void requestReceived(Request request, Response response) {
 // Log all requests for debugging
 System.out.println("WireMock received: " + request.getMethod() + " " + request.getUrl());
 
 // Store requests for later analysis
 requestStore.add(request);
 }
 }
}
```

### 7. JUnit 5 Integration

```java
@ExtendWith(WireMockExtension.class)
public class WireMockJUnit5Test {
 
 @RegisterExtension
 static WireMockExtension wireMock = WireMockExtension.newInstance().options(options().port(8089)).configureStaticDsl(true).build();
 
 @Test
 void shouldUseWireMockExtension() {
 wireMock.stubFor(get("/api/test").willReturn(ok("test response")));
 
 // Test logic
 String response = restTemplate.getForObject("http://localhost:8089/api/test", String.class);
 assertEquals("test response", response);
 }
 
 @Test
 void shouldVerifyRequests() {
 wireMock.stubFor(post("/api/data").willReturn(created()));
 
 // Make request
 restTemplate.postForObject("http://localhost:8089/api/data", data, Void.class);
 
 // Verify
 wireMock.verify(postRequestedFor(urlEqualTo("/api/data")));
 }
}
```

## Q9. Как организовать интеграционные тесты в CI/CD?

### 1. Test Pyramid в CI/CD

```
Unit Tests (Fast,1-2 min)
 ↓
Integration Tests (Medium,5-10 min)
 ↓
End-to-End Tests (Slow,15-30 min)
```

### 2. Parallel Execution

```yaml
#.github/workflows/integration-tests.yml
name: Integration Tests
on:
 push:
 branches: [ main ]
 pull_request:
 branches: [ main ]

jobs:
 integration-tests:
 runs-on: ubuntu-latest
 strategy:
 matrix:
 test-suite: [database, api, messaging, full-integration]
 
 services:
 postgres:
 image: postgres:13
 env:
 POSTGRES_DB: testdb
 POSTGRES_USER: test
 POSTGRES_PASSWORD: test
 ports:
 - 5432:5432
 options: >-
 --health-cmd pg_isready
 --health-interval 10s
 --health-timeout 5s
 --health-retries 5
 
 rabbitmq:
 image: rabbitmq:3-management
 ports:
 - 5672:5672
 - 15672:15672
 
 steps:
 - uses: actions/checkout@v3
 
 - name: Set up JDK
 uses: actions/setup-java@v3
 with:
 java-version: '17'
 distribution: 'temurin'
 
 - name: Cache Maven packages
 uses: actions/cache@v3
 with:
 path:/.m2
 key: ${{ runner.os }}-m2-${{ hashFiles('**/pom.xml') }}
 restore-keys: ${{ runner.os }}-m2
 
 - name: Run ${{ matrix.test-suite }} tests
 run: mvn test -Dtest="*${{ matrix.test-suite }}*Test" -Dspring.profiles.active=test
 env:
 SPRING_DATASOURCE_URL: jdbc:postgresql://localhost:5432/testdb
 SPRING_DATASOURCE_USERNAME: test
 SPRING_DATASOURCE_PASSWORD: test
 SPRING_RABBITMQ_HOST: localhost
 SPRING_RABBITMQ_PORT: 5672
```

### 3. Test Environments

```yaml
# docker-compose.test.yml
version: '3.8'
services:
 postgres:
 image: postgres:13
 environment:
 POSTGRES_DB: testdb
 POSTGRES_USER: test
 POSTGRES_PASSWORD: test
 ports:
 - "5432:5432"
 volumes:
 - postgres_data:/var/lib/postgresql/data
 -./init-scripts:/docker-entrypoint-initdb.d
 
 rabbitmq:
 image: rabbitmq:3-management
 ports:
 - "5672:5672"
 - "15672:15672"
 volumes:
 - rabbitmq_data:/var/lib/rabbitmq
 
 redis:
 image: redis:6-alpine
 ports:
 - "6379:6379"
 command: redis-server --appendonly yes
 volumes:
 - redis_data:/data

volumes:
 postgres_data:
 rabbitmq_data:
 redis_data:
```

### 4. Test Reporting

```xml
<!-- pom.xml -->
<plugin>
 <groupId>org.apache.maven.plugins</groupId>
 <artifactId>maven-surefire-plugin</artifactId>
 <version>3.0.0</version>
 <configuration>
 <reportsDirectory>${project.build.directory}/surefire-reports</reportsDirectory>
 <includes>
 <include>**/*Test.java</include>
 <include>**/*IT.java</include>
 </includes>
 <excludes>
 <exclude>**/*E2ETest.java</exclude>
 </excludes>
 </configuration>
</plugin>

<plugin>
 <groupId>org.jacoco</groupId>
 <artifactId>jacoco-maven-plugin</artifactId>
 <version>0.8.8</version>
 <executions>
 <execution>
 <goals>
 <goal>prepare-agent</goal>
 </goals>
 </execution>
 <execution>
 <id>report</id>
 <phase>test</phase>
 <goals>
 <goal>report</goal>
 </goals>
 </execution>
 </executions>
</plugin>
```

### 5. Test Data Management

```java
@SpringBootTest
@ActiveProfiles("test")
@Sql(scripts = "/test-data/init.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
@Sql(scripts = "/test-data/cleanup.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
public class DataDrivenIntegrationTest {
 
 @Autowired
 private UserRepository userRepository;
 
 @Test
 void shouldFindUsersByCriteria() {
 // Test data is loaded by @Sql
 List<User> premiumUsers = userRepository.findByStatus(UserStatus.PREMIUM);
 assertEquals(3, premiumUsers.size());
 
 List<User> activeUsers = userRepository.findByActive(true);
 assertEquals(5, activeUsers.size());
 }
}

// test-data/init.sql
INSERT INTO users (id, email, status, active) VALUES
(1, 'john@example.com', 'PREMIUM', true),
(2, 'jane@example.com', 'REGULAR', true),
(3, 'bob@example.com', 'PREMIUM', false);

// test-data/cleanup.sql
DELETE FROM users;
```

### 6. Performance Testing in CI

```yaml
#.github/workflows/performance-tests.yml
name: Performance Tests
on:
 push:
 branches: [ main ]

jobs:
 performance:
 runs-on: ubuntu-latest
 
 steps:
 - uses: actions/checkout@v3
 
 - name: Run JMeter tests
 uses: rbhadti94/apache-jmeter-action@v0.5.0
 with:
 testFilePath: performance-tests/user-registration.jmx
 outputReportsFolder: reports/
 outputJtlFiles: true
 
 - name: Publish performance results
 uses: actions/upload-artifact@v3
 with:
 name: jmeter-results
 path: reports/
 
 - name: Check performance thresholds
 run: |
 # Parse JMeter results and check thresholds
 RESPONSE_TIME=$(grep -o 'meanResTime">[0-9]*' reports/*.jtl | grep -o '[0-9]*' | tail -1)
 if [ "$RESPONSE_TIME" -gt "1000" ]; then
 echo "Performance test failed: Response time ${RESPONSE_TIME}ms > 1000ms"
 exit 1
 fi
```

## Q10. Какие best practices для integration testing?

### 1. Test Isolation

```java
@SpringBootTest
@ActiveProfiles("test")
public class IsolatedIntegrationTest {
 
 @Autowired
 private UserRepository userRepository;
 
 @Autowired
 private OrderRepository orderRepository;
 
 @BeforeEach
 void setUp() {
 // Очистка данных перед каждым тестом
 orderRepository.deleteAll();
 userRepository.deleteAll();
 
 // Создание изолированных тестовых данных
 User user = userRepository.save(new User("test@example.com", "password"));
 testUserId = user.getId();
 }
 
 @Test
 void shouldCreateOrderIndependently() {
 // Каждый тест работает с изолированными данными
 Order order = orderService.createOrder(testUserId, productId, 1);
 assertNotNull(order.getId());
 
 // Проверка не влияет на другие тесты
 assertEquals(1, orderRepository.count());
 }
 
 @Test 
 void shouldUpdateOrderIndependently() {
 // Создание данных для этого теста
 Order order = orderService.createOrder(testUserId, productId, 1);
 
 // Обновление
 orderService.updateOrderStatus(order.getId(), OrderStatus.SHIPPED);
 
 // Проверка
 Order updated = orderRepository.findById(order.getId()).orElseThrow();
 assertEquals(OrderStatus.SHIPPED, updated.getStatus());
 }
}
```

### 2. Test Data Management

```java
public class TestDataFactory {
 
 private static final Faker faker = new Faker();
 
 public static User createRandomUser() {
 return User.builder().email(faker.internet().emailAddress()).password(faker.internet().password()).firstName(faker.name().firstName()).lastName(faker.name().lastName()).build();
 }
 
 public static Product createRandomProduct() {
 return Product.builder().name(faker.commerce().productName()).price(BigDecimal.valueOf(faker.number().randomDouble(2, 10, 1000))).category(faker.commerce().department()).build();
 }
 
 public static Order createRandomOrder(User user, List<Product> products) {
 Order order = new Order();
 order.setUser(user);
 order.setOrderItems(products.stream().map(product -> {
 OrderItem item = new OrderItem();
 item.setProduct(product);
 item.setQuantity(faker.number().numberBetween(1, 5));
 return item;
 }).collect(Collectors.toList()));
 return order;
 }
}

@SpringBootTest
public class RandomizedIntegrationTest {
 
 @Autowired
 private UserService userService;
 
 @Test
 void shouldHandleRandomUserData() {
 // Создание случайных тестовых данных
 User randomUser = TestDataFactory.createRandomUser();
 
 // Сохранение и проверка
 User saved = userService.createUser(randomUser);
 assertNotNull(saved.getId());
 assertNotNull(saved.getCreatedAt());
 }
}
```

### 3. Test Categories и Tagging

```java
// Категории тестов
public interface FastTest {
}

public interface SlowTest {
}

public interface DatabaseTest {
}

public interface ExternalApiTest {
}

// Применение категорий
@Tag("database")
@SpringBootTest
@Testcontainers
public class DatabaseIntegrationTest implements DatabaseTest {
 
 @Container
 private static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:13");
 
 @Test
 @Tag("slow")
 void shouldHandleComplexQueries() {
 // Медленный тест базы данных
 }
 
 @Test
 @Tag("fast")
 void shouldValidateConstraints() {
 // Быстрый тест валидации
 }
}

// Запуск тестов по категориям
// mvn test -Dgroups="database,fast" -DexcludedGroups="slow"
// или
// mvn test -Dtest="**/*Test" -Dgroups="!slow"
```

### 4. Test Execution Control

```java
@Configuration
@Profile("test")
public class TestExecutionConfig {
 
 @Bean
 public TestExecutionListener testExecutionListener() {
 return new TestExecutionListener() {
 
 @Override
 public void beforeTestExecution(TestExecutionSummary summary) {
 // Настройка перед выполнением тестов
 System.out.println("Starting integration tests...");
 }
 
 @Override
 public void afterTestExecution(TestExecutionSummary summary) {
 // Анализ результатов после выполнения
 long failedTests = summary.getTestsFailedCount();
 if (failedTests > 0) {
 System.err.println("Integration tests failed: " + failedTests);
 }
 }
 };
 }
}

@SpringBootTest
@ActiveProfiles("test")
public class ControlledIntegrationTest {
 
 @Autowired
 private ApplicationContext context;
 
 @Test
 void shouldVerifyApplicationContext() {
 // Проверка корректности конфигурации
 assertNotNull(context.getBean(UserService.class));
 assertNotNull(context.getBean(OrderService.class));
 
 // Проверка health checks
 HealthIndicator healthIndicator = context.getBean(HealthIndicator.class);
 Health health = healthIndicator.health();
 assertEquals(Status.UP, health.getStatus());
 }
}
```

### 5. Monitoring и Debugging

```java
@SpringBootTest
@ActiveProfiles("test")
public class MonitoredIntegrationTest {
 
 private static final Logger logger = LoggerFactory.getLogger(MonitoredIntegrationTest.class);
 
 @Autowired
 private UserService userService;
 
 @Test
 void shouldCreateUserWithMonitoring() {
 long startTime = System.currentTimeMillis();
 
 try {
 logger.info("Starting user creation test");
 
 User user = new User("test@example.com", "password");
 User created = userService.createUser(user);
 
 long duration = System.currentTimeMillis() - startTime;
 logger.info("User creation test completed in {} ms", duration);
 
 assertNotNull(created.getId());
 
 } catch (Exception e) {
 long duration = System.currentTimeMillis() - startTime;
 logger.error("User creation test failed after {} ms: {}", duration, e.getMessage(), e);
 throw e;
 }
 }
 
 @Test
 void shouldLogTestData() {
 // Логирование тестовых данных для отладки
 User user = userService.createUser(new User("debug@example.com", "password"));
 
 logger.debug("Created test user: id={}, email={}", user.getId(), user.getEmail());
 
 // Логирование состояния БД
 List<User> allUsers = userService.findAllUsers();
 logger.debug("Total users in database: {}", allUsers.size());
 
 assertTrue(allUsers.size() > 0);
 }
}
```

### 6. Test Stability

```java
@SpringBootTest
@ActiveProfiles("test")
public class StableIntegrationTest {
 
 @Autowired
 private UserService userService;
 
 @Test
 @Timeout(30) // Максимум 30 секунд
 void shouldCreateUserWithinTimeout() {
 User user = new User("timeout@example.com", "password");
 User created = userService.createUser(user);
 
 assertNotNull(created.getId());
 }
 
 @Test
 @RepeatedTest(3) // Повторить тест 3 раза
 void shouldBeStableAcrossRuns() {
 User user = TestDataFactory.createRandomUser();
 User created = userService.createUser(user);
 
 assertNotNull(created.getId());
 assertNotNull(created.getCreatedAt());
 }
 
 @Test
 void shouldHandleConcurrentOperations() throws InterruptedException {
 int numberOfThreads = 10;
 ExecutorService executor = Executors.newFixedThreadPool(numberOfThreads);
 CountDownLatch latch = new CountDownLatch(numberOfThreads);
 AtomicInteger successCount = new AtomicInteger(0);
 
 for (int i = 0; i < numberOfThreads; i++) {
 executor.submit(() -> {
 try {
 User user = TestDataFactory.createRandomUser();
 userService.createUser(user);
 successCount.incrementAndGet();
 } catch (Exception e) {
 logger.error("Concurrent user creation failed", e);
 } finally {
 latch.countDown();
 }
 });
 }
 
 assertTrue(latch.await(30, TimeUnit.SECONDS));
 assertEquals(numberOfThreads, successCount.get());
 
 executor.shutdown();
 }
}
```

## Заключение

Integration testing является критически важным уровнем тестирования для Senior Java Developer. Он проверяет взаимодействие компонентов, работу с внешними зависимостями и end-to-end сценарии. Использование правильных инструментов (TestContainers, WireMock, Contract Testing) и следование best practices обеспечивает надежность и поддерживаемость интеграционных тестов в CI/CD pipeline.

