---
title: "Testcontainers: Интеграционное тестирование с Docker"
description: "Комплексное руководство по использованию Testcontainers для создания интеграционных тестов с реальными зависимостями в Docker контейнерах."
tags:
  - libraries
  - java
  - java-testcontainers
difficulty: "intermediate"
prerequisites: []
next: []
updated: "2026-02-11"
---
# Testcontainers: Интеграционное тестирование с **Docker**

**Комплексное руководство по использованию `Testcontainers` для создания интеграционных тестов с реальными зависимостями в `Docker` контейнерах.**

## Полезные ссылки

### Официальная документация
- [Testcontainers](https://www.testcontainers.org/) — официальный сайт
- [Testcontainers GitHub](https://github.com/testcontainers/testcontainers-java) — репозиторий проекта
- [Testcontainers Documentation](https://www.testcontainers.org/quickstart/junit_5_quickstart/) — быстрый старт

### Модули
- [Database Containers](https://www.testcontainers.org/modules/databases/) — базы данных
- [Message Brokers](https://www.testcontainers.org/modules/kafka/) — очереди сообщений
- [Web Drivers](https://www.testcontainers.org/modules/webdriver_containers/) — **Selenium**
- [Generic Containers](https://www.testcontainers.org/modules/generic_containers/) — общие контейнеры

## Содержание

- [Введение в Testcontainers](#введение-в-testcontainers)
  - [Почему Testcontainers?](#почему-testcontainers)
  - [Как работает Testcontainers?](#как-работает-testcontainers)
  - [Преимущества и недостатки](#преимущества-и-недостатки)
- [Установка и настройка](#установка-и-настройка)
  - [Maven](#maven)
  - [Gradle](#gradle)
  - [Требования](#требования)
- [Docker должен быть установлен и запущен](#docker-должен-быть-установлен-и-запущен)
- [Для Linux может потребоваться Docker без sudo](#для-linux-может-потребоваться-docker-без-sudo)
  - [Базовая конфигурация](#базовая-конфигурация)
- [Кастомный Docker host](#кастомный-docker-host)
- [Таймауты](#таймауты)
- [Пул образов](#пул-образов)
- [Логирование](#логирование)
- [Database containers](#database-containers)
  - [PostgreSQL](#postgresql)
  - [MySQL](#mysql)
  - [Кастомная база данных](#кастомная-база-данных)
- [Message broker containers](#message-broker-containers)
  - [Kafka](#kafka)
  - [RabbitMQ](#rabbitmq)
- [WebDriver containers](#webdriver-containers)
  - [Selenium](#selenium)
- [Generic containers](#generic-containers)
  - [Кастомные сервисы](#кастомные-сервисы)
  - [Docker Compose](#docker-compose)
- [Конфигурация и оптимизация](#конфигурация-и-оптимизация)
  - [Оптимизация производительности](#оптимизация-производительности)
  - [Управление состоянием](#управление-состоянием)
- [Интеграция с Spring Boot](#интеграция-с-spring-boot)
  - [Test slices](#test-slices)
  - [Конфигурация для CI/CD](#конфигурация-для-cicd)
- [Best practices](#best-practices)
  - [1. Правильная организация тестов](#1-правильная-организация-тестов)
  - [2. Управление ресурсами](#2-управление-ресурсами)
- [Troubleshooting](#troubleshooting)
- [FAQ](#faq)
- [Заключение](#заключение)
  - [Преимущества Testcontainers](#преимущества-testcontainers)
  - [Основные паттерны использования](#основные-паттерны-использования)
  - [Когда использовать Testcontainers](#когда-использовать-testcontainers)
  - [Сравнение с альтернативами](#сравнение-с-альтернативами)

## Введение в **Testcontainers**

**Testcontainers** — это **Java** библиотека, которая позволяет запускать **Docker** контейнеры в рамках **JUnit** тестов. Она предоставляет реальные зависимости (**базы данных, очереди сообщений, веб-серверы**) для интеграционного тестирования, обеспечивая высокую точность и надежность тестов.

### Почему **Testcontainers**?

**Testcontainers** предлагает множество преимуществ:**

1. **Реальные зависимости** — Тесты используют настоящие сервисы, а не **in-memory** заглушки
2. **Изоляция** — Каждый тест получает чистое состояние зависимостей
3. **Совместимость** — Работает с любыми **Docker** образами
4. **Простота использования** — Минимальная конфигурация
5. **Широкая поддержка** — Поддержка популярных технологий
6. **CI/`CD` интеграция** — Работает в **pipeline**
7. **Docker Compose** — Поддержка **multi-container setups**
8. **JUnit 5 интеграция** — Современная тестовая инфраструктура

### Как работает **Testcontainers**?

**Testcontainers** использует **JUnit Testcontainers extension** для автоматического управления жизненным циклом контейнеров:**

1. **@Container** — Аннотация для объявления контейнеров
2. **@Testcontainers** — Включает автоматическое управление
3. **GenericContainer** — Базовый класс для кастомных контейнеров
4. **DockerClient** — Интерфейс для работы с **Docker API**

### Преимущества и недостатки

**Преимущества:**
- Высокая точность тестов
- Реальные сценарии использования
- Простота написания тестов
- Хорошая изоляция
- Поддержка `CI/CD`

**Недостатки:**
- Требует **Docker**
- Медленнее чем **in-memory** тесты
- Сложность отладки
- Ресурсоемкость

## Установка и настройка

### **Maven**

Зависимости **Maven** для **Testcontainers** (**JUnit `Jupiter` и модули `PostgreSQL`, `MySQL`, Kafka**).

```xml
<dependency>
    <groupId>org.testcontainers</groupId>
    <artifactId>junit-jupiter</artifactId>
    <version>1.19.3</version>
    <scope>test</scope>
</dependency>

<!-- Для PostgreSQL -->
<dependency>
    <groupId>org.testcontainers</groupId>
    <artifactId>postgresql</artifactId>
    <version>1.19.3</version>
    <scope>test</scope>
</dependency>

<!-- Для MySQL -->
<dependency>
    <groupId>org.testcontainers</groupId>
    <artifactId>mysql</artifactId>
    <version>1.19.3</version>
    <scope>test</scope>
</dependency>

<!-- Для Kafka -->
<dependency>
    <groupId>org.testcontainers</groupId>
    <artifactId>kafka</artifactId>
    <version>1.19.3</version>
    <scope>test</scope>
</dependency>
```

### **Gradle**

```kotlin
dependencies {
    testImplementation("org.testcontainers:junit-jupiter:1.19.3")
    testImplementation("org.testcontainers:postgresql:1.19.3")
    testImplementation("org.testcontainers:mysql:1.19.3")
    testImplementation("org.testcontainers:kafka:1.19.3")
}
```

### Требования

```bash
# Docker должен быть установлен и запущен
docker --version

# Для Linux может потребоваться Docker без sudo
sudo usermod -aG docker $USER
newgrp docker
```

### Базовая конфигурация

```java
// В большинстве случаев Testcontainers работает "из коробки"
// Для кастомной конфигурации можно использовать system properties

// testcontainers.properties
# Кастомный Docker host
docker.client.strategy=org.testcontainers.dockerclient.UnixSocketClientProviderStrategy
docker.host=unix:///var/run/docker.sock

# Таймауты
docker.client.read.timeout=60s
docker.client.connect.timeout=10s

# Пул образов
image.pull.policy=IfNotPresent
image.pull.timeout=5m

# Логирование
log.level.org.testcontainers=INFO
```

## **Database containers**

### **PostgreSQL**

```java
@SpringBootTest
@Testcontainers
@Slf4j
public class PostgreSQLIntegrationTest {

    @Container
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:14")
        .withDatabaseName("testdb")
        .withUsername("test")
        .withPassword("test")
        .withInitScript("db/init.sql"); // Опционально

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
        registry.add("spring.datasource.driver-class-name", postgres::getDriverClassName);
    }

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Test
    void testDatabaseOperations() {
        // Создание тестовых данных
        User user = new User("John Doe", "john@example.com");
        userRepository.save(user);

        // Проверка сохранения
        Optional<User> found = userRepository.findByEmail("john@example.com");
        assertThat(found).isPresent();
        assertThat(found.get().getName()).isEqualTo("John Doe");

        // JDBC проверка
        Integer count = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM users", Integer.class);
        assertThat(count).isEqualTo(1);
    }

    @Test
    void testComplexQuery() {
        // Создание тестовых данных
        createTestData();

        // Тестирование сложного запроса
        List<UserStats> stats = userRepository.getUserStatistics();
        assertThat(stats).isNotEmpty();

        // Проверка транзакций
        testTransactionalBehavior();
    }

    private void createTestData() {
        List<User> users = Arrays.asList(
            new User("Alice", "alice@test.com"),
            new User("Bob", "bob@test.com"),
            new User("Charlie", "charlie@test.com")
        );
        userRepository.saveAll(users);
    }

    private void testTransactionalBehavior() {
        assertThrows(DataIntegrityViolationException.class, () -> {
            userRepository.save(new User("Alice", "alice@test.com")); // Duplicate email
        });
    }
}
```

### **MySQL**

```java
/
 * Интеграционный тест для MySQL с использованием Testcontainers
 * Демонстрирует настройку MySQL контейнера и тестирование MySQL-специфичных функций
 */
@SpringBootTest  // Spring Boot тест с полным контекстом приложения
@Testcontainers  // Аннотация Testcontainers для автоматического управления контейнерами
public class MySQLIntegrationTest {

    /
     * MySQL контейнер - создается один раз для всех тестов в классе
     * static означает что контейнер будет переиспользоваться между тестами
     */
    @Container  // Аннотация Testcontainers - указывает что это контейнер для тестов
    static MySQLContainer<?> mysql = new MySQLContainer<>("mysql:8.0")  // Создаем MySQL 8.0 контейнер
        .withDatabaseName("testdb")                                      // Имя базы данных для тестов
        .withUsername("test")                                             // Имя пользователя для подключения
        .withPassword("test")                                             // Пароль для подключения
        .withConfigurationOverride("mysql-config")                       // Кастомная конфигурация MySQL из файла
        .withCommand("--default-authentication-plugin=mysql_native_password");  // Команда запуска MySQL с нативным аутентификатором

    /
     * Динамическое добавление свойств Spring Boot из контейнера
     * Позволяет Spring Boot автоматически подключиться к контейнеру MySQL
     * @param registry реестр свойств для динамического добавления значений
     */
    @DynamicPropertySource  // Spring Boot аннотация для динамических свойств
    static void mysqlProperties(DynamicPropertyRegistry registry) {
        // Добавляем JDBC URL из контейнера в Spring свойства
        // mysql::getJdbcUrl - метод reference для получения JDBC URL контейнера
        registry.add("spring.datasource.url", mysql::getJdbcUrl);
        registry.add("spring.datasource.username", mysql::getUsername);  // Имя пользователя из контейнера
        registry.add("spring.datasource.password", mysql::getPassword);  // Пароль из контейнера
    }

    // Репозиторий для работы с продуктами - автоматически внедряется Spring
    @Autowired
    private ProductRepository productRepository;

    /
     * Тест MySQL-специфичных функций
     * Демонстрирует использование Full-text search и JSON полей MySQL
     */
    @Test
    void testMySQLFeatures() {
        // Тестирование MySQL специфичных фич
        // Создаем тестовый продукт для проверки функциональности
        Product product = new Product("Laptop", 999.99, "Electronics");
        productRepository.save(product);  // Сохраняем продукт в БД

        // Full-text search - поиск по текстовым полям с использованием индексов MySQL
        // MySQL поддерживает полнотекстовый поиск для быстрого поиска по тексту
        List<Product> found = productRepository.searchByName("laptop");
        assertThat(found).contains(product);  // Проверяем что продукт найден

        // JSON поля (MySQL 5.7.8+) - тестирование работы с JSON данными в MySQL
        testJsonFields();
    }

    /
     * Тестирование JSON операций в MySQL
     * MySQL 5.7.8+ поддерживает нативные JSON типы и функции для работы с JSON
     */
    private void testJsonFields() {
        // Тестирование JSON операций - использование JSON_EXTRACT для извлечения данных из JSON поля
        // specifications - JSON поле в таблице products
        String jsonQuery = "SELECT * FROM products WHERE JSON_EXTRACT(specifications, '$.category') = 'Electronics'";

        // Выполняем запрос через JdbcTemplate и маппим результаты в объекты Product
        List<Product> electronics = jdbcTemplate.query(jsonQuery,
            (rs, rowNum) -> new Product(rs.getString("name"), rs.getDouble("price"), rs.getString("category")));

        // Проверяем что найдены продукты категории Electronics
        assertThat(electronics).isNotEmpty();
    }
}
```

### Кастомная база данных

```java
/
 * Кастомный тест базы данных с расширенной конфигурацией Testcontainers
 * Демонстрирует продвинутые возможности: init scripts, логирование, TmpFS, переиспользование контейнеров
 */
@SpringBootTest  // Spring Boot тест с полным контекстом
@Testcontainers  // Аннотация Testcontainers для управления контейнерами
public class CustomDatabaseTest {

    /
     * PostgreSQL контейнер с расширенной конфигурацией
     * Демонстрирует различные опции настройки контейнера
     */
    @Container  // Аннотация Testcontainers - указывает что это контейнер для тестов
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:14")  // PostgreSQL 14 контейнер
        .withDatabaseName("integration_test")                                          // Имя базы данных
        .withUsername("test_user")                                                     // Имя пользователя
        .withPassword("test_password")                                                 // Пароль
        .withInitScript("db/migrations/V1__create_tables.sql")                        // Первый init script - создание таблиц
        .withInitScript("db/test-data/V1__insert_test_data.sql")                      // Второй init script - тестовые данные
        .withCommand("postgres", "-c", "log_statement=all")                           // Логирование всех SQL запросов для отладки
        .withTmpFs(Collections.singletonMap("/temp_pgdata", "rw,noexec,nosuid,size=100m"))  // TmpFS для ускорения (в памяти)
        .withLogConsumer(new Slf4jLogConsumer(log))                                    // Логи контейнера в Slf4j
        .withReuse(true);                                                              // Переиспользование контейнера между тестами (быстрее)

    /
     * Динамическое добавление свойств Spring Boot из контейнера
     * @param registry реестр свойств для динамического добавления значений
     */
    @DynamicPropertySource  // Spring Boot аннотация для динамических свойств
    static void databaseProperties(DynamicPropertyRegistry registry) {
        // Добавляем свойства подключения к БД из контейнера
        registry.add("spring.datasource.url", postgres::getJdbcUrl);      // JDBC URL из контейнера
        registry.add("spring.datasource.username", postgres::getUsername); // Имя пользователя из контейнера
        registry.add("spring.datasource.password", postgres::getPassword);  // Пароль из контейнера
    }

    // DataSource для подключения к БД - автоматически настроен Spring Boot
    @Autowired
    private DataSource dataSource;

    /
     * Тест подключения к базе данных
     * Проверяет что контейнер запущен и подключение работает
     * @throws SQLException если произошла ошибка при подключении
     */
    @Test
    void testDatabaseConnectivity() throws SQLException {
        // Получаем соединение из DataSource и проверяем его валидность
        try (Connection conn = dataSource.getConnection()) {  // try-with-resources для автоматического закрытия
            // Проверяем что соединение валидно (timeout 5 секунд)
            assertThat(conn.isValid(5)).isTrue();

            // Получаем метаданные БД для проверки информации о подключении
            DatabaseMetaData metaData = conn.getMetaData();
            log.info("Database: {}", metaData.getDatabaseProductName());      // Имя СУБД (PostgreSQL)
            log.info("Version: {}", metaData.getDatabaseProductVersion());    // Версия PostgreSQL
        }
    }

    /
     * Тест выполнения миграций базы данных
     * Проверяет что init scripts были выполнены и таблицы созданы
     */
    @Test
    void testDatabaseMigrations() {
        // Проверка что миграции выполнены - подсчет таблиц в схеме public
        // information_schema.tables содержит информацию о всех таблицах в БД
        Integer tableCount = jdbcTemplate.queryForObject(
            "SELECT COUNT(*) FROM information_schema.tables WHERE table_schema = 'public'",
            Integer.class);

        assertThat(tableCount).isGreaterThan(0);
    }

    @Test
    void testDataIntegrity() {
        // Проверка referential integrity
        List<Map<String, Object>> orders = jdbcTemplate.queryForList(
            "SELECT o.*, c.name as customer_name FROM orders o JOIN customers c ON o.customer_id = c.id");

        assertThat(orders).isNotEmpty();
        assertThat(orders.get(0)).containsKey("customer_name");
    }
}
```

## **Message broker containers**

### **Kafka**

```java
@SpringBootTest
@Testcontainers
@Slf4j
public class KafkaIntegrationTest {

    @Container
    static KafkaContainer kafka = new KafkaContainer(DockerImageName.parse("confluentinc/cp-kafka:7.4.0"))
        .withNetwork(Network.newNetwork())
        .withNetworkAliases("kafka")
        .waitingFor(Wait.forLogMessage(".*Kafka Server started.*", 1))
        .withEnv("KAFKA_OFFSETS_TOPIC_REPLICATION_FACTOR", "1")
        .withEnv("KAFKA_TRANSACTION_STATE_LOG_MIN_ISR", "1")
        .withEnv("KAFKA_TRANSACTION_STATE_LOG_REPLICATION_FACTOR", "1");

    @Container
    static GenericContainer<?> zookeeper = new GenericContainer<>(DockerImageName.parse("confluentinc/cp-zookeeper:7.4.0"))
        .withNetwork(Network.newNetwork())
        .withNetworkAliases("zookeeper")
        .withEnv("ZOOKEEPER_CLIENT_PORT", "2181")
        .withEnv("ZOOKEEPER_TICK_TIME", "2000");

    @DynamicPropertySource
    static void kafkaProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.kafka.bootstrap-servers", kafka::getBootstrapServers);
        registry.add("spring.kafka.consumer.auto-offset-reset", () -> "earliest");
        registry.add("spring.kafka.consumer.group-id", () -> "test-group");
    }

    @Autowired
    private KafkaTemplate<String, String> kafkaTemplate;

    @Autowired
    private KafkaListenerEndpointRegistry endpointRegistry;

    @Test
    void testKafkaMessaging() throws Exception {
        String topic = "test-topic";
        String message = "Hello Kafka!";

        // Отправка сообщения
        kafkaTemplate.send(topic, message).get(5, TimeUnit.SECONDS);

        // Ожидание обработки сообщения
        await().atMost(10, TimeUnit.SECONDS)
            .until(() -> messageConsumed(message));

        // Проверка метрик
        assertThat(kafkaTemplate.metrics()).isNotEmpty();
    }

    @Test
    void testKafkaStreams() {
        // Тестирование Kafka Streams топологий
        StreamsBuilder builder = new StreamsBuilder();

        KStream<String, String> source = builder.stream("input-topic");
        source.mapValues(String::toUpperCase)
            .to("output-topic");

        Topology topology = builder.build();

        // Валидация топологии
        assertThat(topology.describe().subtopologies()).isNotEmpty();
    }

    @Test
    void testTransactionalProducing() {
        // Тестирование транзакционных продюсеров
        kafkaTemplate.executeInTransaction(kt -> {
            kt.send("topic1", "message1");
            kt.send("topic2", "message2");
            return true;
        });

        // Проверка что сообщения были отправлены атомарно
    }

    private boolean messageConsumed(String expectedMessage) {
        // Проверка что сообщение было потреблено
        // В реальном тесте можно использовать CountDownLatch или TestObserver
        return true;
    }
}
```

### **RabbitMQ**

```java
@SpringBootTest
@Testcontainers
public class RabbitMQIntegrationTest {

    @Container
    static RabbitMQContainer rabbitMQ = new RabbitMQContainer(DockerImageName.parse("rabbitmq:3.12-management"))
        .withExposedPorts(5672, 15672)
        .withAdminPassword("admin")
        .waitingFor(Wait.forLogMessage(".*Server startup complete.*", 1));

    @DynamicPropertySource
    static void rabbitProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.rabbitmq.host", rabbitMQ::getHost);
        registry.add("spring.rabbitmq.port", rabbitMQ::getAmqpPort);
        registry.add("spring.rabbitmq.username", rabbitMQ::getAdminUsername);
        registry.add("spring.rabbitmq.password", rabbitMQ::getAdminPassword);
    }

    @Autowired
    private RabbitTemplate rabbitTemplate;

    @Autowired
    private AmqpAdmin amqpAdmin;

    @Test
    void testRabbitMQMessaging() {
        String queueName = "test-queue";
        String message = "Hello RabbitMQ!";

        // Создание очереди
        Queue queue = new Queue(queueName, true);
        amqpAdmin.declareQueue(queue);

        // Отправка сообщения
        rabbitTemplate.convertAndSend(queueName, message);

        // Получение сообщения
        Object received = rabbitTemplate.receiveAndConvert(queueName, 5000);
        assertThat(received).isEqualTo(message);
    }

    @Test
    void testRabbitMQFeatures() {
        // Тестирование exchanges, bindings, etc.
        DirectExchange exchange = new DirectExchange("test-exchange");
        amqpAdmin.declareExchange(exchange);

        Queue queue = new Queue("test-queue");
        amqpAdmin.declareQueue(queue);

        Binding binding = BindingBuilder.bind(queue).to(exchange).with("test-key");
        amqpAdmin.declareBinding(binding);

        // Отправка через exchange
        rabbitTemplate.convertAndSend("test-exchange", "test-key", "Test message");

        // Получение
        Object received = rabbitTemplate.receiveAndConvert("test-queue");
        assertThat(received).isEqualTo("Test message");
    }
}
```

## **WebDriver containers**

### **Selenium**

```java
@SpringBootTest
@Testcontainers
public class SeleniumIntegrationTest {

    @Container
    static BrowserWebDriverContainer<?> chrome = new BrowserWebDriverContainer<>()
        .withCapabilities(new ChromeOptions()
            .addArguments("--no-sandbox")
            .addArguments("--disable-dev-shm-usage")
            .addArguments("--disable-gpu")
            .addArguments("--window-size=1920,1080"))
        .withRecordingMode(BrowserWebDriverContainer.VncRecordingMode.RECORD_ALL,
            new File("target/selenium-recordings"))
        .withNetwork(Network.newNetwork());

    @Container
    static GenericContainer<?> app = new GenericContainer<>(DockerImageName.parse("my-app:latest"))
        .withExposedPorts(8080)
        .withNetwork(Network.newNetwork())
        .withNetworkAliases("app")
        .waitingFor(Wait.forHttp("/actuator/health").forPort(8080));

    private WebDriver driver;

    @BeforeEach
    void setUp() {
        driver = new RemoteWebDriver(chrome.getSeleniumAddress(), new ChromeOptions());
    }

    @AfterEach
    void tearDown() {
        if (driver != null) {
            driver.quit();
        }
    }

    @Test
    void testWebApplication() {
        String appUrl = "http://" + app.getHost() + ":" + app.getMappedPort(8080);

        driver.get(appUrl + "/login");

        // Тестирование UI
        WebElement username = driver.findElement(By.id("username"));
        WebElement password = driver.findElement(By.id("password"));
        WebElement loginButton = driver.findElement(By.id("login-button"));

        username.sendKeys("testuser");
        password.sendKeys("testpass");
        loginButton.click();

        // Ожидание загрузки
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        wait.until(ExpectedConditions.urlContains("/dashboard"));

        // Проверка результатов
        WebElement welcomeMessage = driver.findElement(By.className("welcome-message"));
        assertThat(welcomeMessage.getText()).contains("Welcome, testuser");
    }

    @Test
    void testResponsiveDesign() {
        driver.manage().window().setSize(new Dimension(375, 667)); // iPhone size

        driver.get("http://app:8080");

        // Тестирование мобильной версии
        WebElement mobileMenu = driver.findElement(By.className("mobile-menu"));
        assertThat(mobileMenu.isDisplayed()).isTrue();
    }
}
```

## **Generic containers**

### Кастомные сервисы

```java
@SpringBootTest
@Testcontainers
@Slf4j
public class GenericContainerTest {

    @Container
    static GenericContainer<?> redis = new GenericContainer<>(DockerImageName.parse("redis:7-alpine"))
        .withExposedPorts(6379)
        .waitingFor(Wait.forLogMessage(".*Ready to accept connections.*", 1))
        .withCommand("redis-server", "--requirepass", "testpass");

    @Container
    static GenericContainer<?> elasticsearch = new GenericContainer<>(DockerImageName.parse("elasticsearch:8.11.0"))
        .withExposedPorts(9200, 9300)
        .withEnv("discovery.type", "single-node")
        .withEnv("xpack.security.enabled", "false")
        .waitingFor(Wait.forHttp("/_cluster/health")
            .forPort(9200)
            .forStatusCode(200)
            .withStartupTimeout(Duration.ofMinutes(5)));

    @Container
    static GenericContainer<?> prometheus = new GenericContainer<>(DockerImageName.parse("prom/prometheus:latest"))
        .withExposedPorts(9090)
        .withClasspathResourceMapping("prometheus.yml", "/etc/prometheus/prometheus.yml", BindMode.READ_ONLY)
        .waitingFor(Wait.forHttp("/-/ready")
            .forPort(9090)
            .forStatusCode(200));

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.redis.host", redis::getHost);
        registry.add("spring.redis.port", redis::getFirstMappedPort);
        registry.add("spring.redis.password", () -> "testpass");

        registry.add("elasticsearch.host", elasticsearch::getHost);
        registry.add("elasticsearch.port", elasticsearch::getFirstMappedPort);
    }

    @Autowired
    private StringRedisTemplate redisTemplate;

    @Autowired
    private RestTemplate restTemplate;

    @Test
    void testRedisIntegration() {
        // Тестирование Redis
        redisTemplate.opsForValue().set("test:key", "test:value");
        String value = redisTemplate.opsForValue().get("test:key");

        assertThat(value).isEqualTo("test:value");

        // Тестирование TTL
        redisTemplate.expire("test:key", 1, TimeUnit.MINUTES);
        Long ttl = redisTemplate.getExpire("test:key");
        assertThat(ttl).isGreaterThan(0);
    }

    @Test
    void testElasticsearchIntegration() {
        String esUrl = "http://" + elasticsearch.getHost() + ":" + elasticsearch.getFirstMappedPort();

        // Создание индекса
        restTemplate.put(esUrl + "/test-index", Map.of(
            "settings", Map.of("number_of_shards", 1),
            "mappings", Map.of("properties", Map.of(
                "title", Map.of("type", "text"),
                "content", Map.of("type", "text")
            ))
        ));

        // Индексация документа
        restTemplate.postForEntity(esUrl + "/test-index/_doc/1", Map.of(
            "title", "Test Document",
            "content", "This is a test document for Elasticsearch integration testing"
        ), String.class);

        // Поиск
        Map<String, Object> searchResult = restTemplate.postForObject(
            esUrl + "/test-index/_search",
            Map.of("query", Map.of("match", Map.of("title", "Test"))),
            Map.class);

        assertThat(searchResult).isNotNull();
        assertThat((Integer) ((Map) searchResult.get("hits")).get("total")).isGreaterThan(0);
    }

    @Test
    void testPrometheusMetrics() {
        String prometheusUrl = "http://" + prometheus.getHost() + ":" + prometheus.getFirstMappedPort();

        // Проверка доступности Prometheus
        ResponseEntity<String> response = restTemplate.getForEntity(
            prometheusUrl + "/-/ready", String.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

        // Проверка метрик
        ResponseEntity<String> metricsResponse = restTemplate.getForEntity(
            prometheusUrl + "/metrics", String.class);

        assertThat(metricsResponse.getBody()).contains("prometheus_build_info");
    }
}
```

### **Docker Compose**

```java
@SpringBootTest
@Testcontainers
public class DockerComposeTest {

    @Container
    static DockerComposeContainer<?> environment = new DockerComposeContainer<>(
        new File("src/test/resources/docker-compose.yml"))
        .withExposedService("postgres_1", 5432)
        .withExposedService("redis_1", 6379)
        .withExposedService("rabbitmq_1", 5672)
        .waitingFor("postgres_1", Wait.forLogMessage(".*database system is ready to accept connections.*", 1))
        .waitingFor("redis_1", Wait.forLogMessage(".*Ready to accept connections.*", 1))
        .waitingFor("rabbitmq_1", Wait.forLogMessage(".*Server startup complete.*", 1));

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        // PostgreSQL
        String postgresHost = environment.getServiceHost("postgres_1", 5432);
        Integer postgresPort = environment.getServicePort("postgres_1", 5432);
        registry.add("spring.datasource.url", () -> "jdbc:postgresql://" + postgresHost + ":" + postgresPort + "/testdb");
        registry.add("spring.datasource.username", () -> "test");
        registry.add("spring.datasource.password", () -> "test");

        // Redis
        String redisHost = environment.getServiceHost("redis_1", 6379);
        Integer redisPort = environment.getServicePort("redis_1", 6379);
        registry.add("spring.redis.host", () -> redisHost);
        registry.add("spring.redis.port", redisPort);

        // RabbitMQ
        String rabbitHost = environment.getServiceHost("rabbitmq_1", 5672);
        Integer rabbitPort = environment.getServicePort("rabbitmq_1", 5672);
        registry.add("spring.rabbitmq.host", () -> rabbitHost);
        registry.add("spring.rabbitmq.port", rabbitPort);
    }

    @Autowired
    private DataSource dataSource;

    @Autowired
    private StringRedisTemplate redisTemplate;

    @Autowired
    private RabbitTemplate rabbitTemplate;

    @Test
    void testMultiServiceIntegration() throws SQLException {
        // Тестирование PostgreSQL
        try (Connection conn = dataSource.getConnection()) {
            assertThat(conn.isValid(5)).isTrue();
        }

        // Тестирование Redis
        redisTemplate.opsForValue().set("integration:test", "success");
        String value = redisTemplate.opsForValue().get("integration:test");
        assertThat(value).isEqualTo("success");

        // Тестирование RabbitMQ
        rabbitTemplate.convertAndSend("test-exchange", "test-key", "Hello from test!");
        Object received = rabbitTemplate.receiveAndConvert("test-queue", 5000);
        assertThat(received).isEqualTo("Hello from test!");
    }
}

// docker-compose.yml
/*
version: '3.8'
services:
  postgres:
    image: postgres:14
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
    image: redis:7-alpine
    ports:
      - "6379:6379"
    healthcheck:
      test: ["CMD", "redis-cli", "ping"]
      interval: 10s
      timeout: 5s
      retries: 5

  rabbitmq:
    image: rabbitmq:3.12-management
    environment:
      RABBITMQ_DEFAULT_USER: guest
      RABBITMQ_DEFAULT_PASS: guest
    ports:
      - "5672:5672"
      - "15672:15672"
    healthcheck:
      test: ["CMD", "rabbitmq-diagnostics", "check_port_connectivity"]
      interval: 30s
      timeout: 10s
      retries: 5
*/
```

## Конфигурация и оптимизация

### Оптимизация производительности

```java
@Configuration
public class TestcontainersConfiguration {

    // Переиспользование контейнеров для ускорения тестов
    static {
        // Включаем reuse для всех контейнеров
        System.setProperty("testcontainers.reuse.enable", "true");

        // Настраиваем стратегию переиспользования
        ContainerDatabaseDriver.registerContainerDatabaseDriver(
            "org.testcontainers.jdbc.ContainerDatabaseDriver");
    }

    @Bean
    @Scope("singleton") // Singleton scope для контейнеров
    public PostgreSQLContainer<?> postgresContainer() {
        return new PostgreSQLContainer<>("postgres:14")
            .withDatabaseName("testdb")
            .withUsername("test")
            .withPassword("test")
            .withReuse(true) // Явно включаем переиспользование
            .withLabel("reuse.UUID", "postgres-test"); // Уникальный идентификатор
    }

    @Bean
    @Scope("singleton")
    public RedisContainer redisContainer() {
        return new RedisContainer(DockerImageName.parse("redis:7-alpine"))
            .withReuse(true)
            .withLabel("reuse.UUID", "redis-test");
    }
}

@SpringBootTest
@Testcontainers
public class OptimizedIntegrationTest {

    // Синглтон контейнеры для переиспользования
    @Autowired
    private PostgreSQLContainer<?> postgres;

    @Autowired
    private RedisContainer redis;

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry,
                                   PostgreSQLContainer<?> postgres,
                                   RedisContainer redis) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);

        registry.add("spring.redis.host", redis::getHost);
        registry.add("spring.redis.port", redis::getFirstMappedPort);
    }

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private StringRedisTemplate redisTemplate;

    @Test
    void testOptimizedIntegration() {
        // Тесты выполняются быстрее благодаря переиспользованию контейнеров
        User user = new User("John", "john@test.com");
        userRepository.save(user);

        redisTemplate.opsForValue().set("user:" + user.getId(), user.getName());

        // Проверки...
    }
}
```

### Управление состоянием

```java
@SpringBootTest
@Testcontainers
@Slf4j
public class StateManagementTest {

    @Container
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:14")
        .withDatabaseName("testdb")
        .withUsername("test")
        .withPassword("test")
        .withInitScript("db/test-init.sql");

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres.getJdbcUrl());
        registry.add("spring.datasource.username", postgres.getUsername());
        registry.add("spring.datasource.password", postgres.getPassword());
    }

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private UserRepository userRepository;

    @BeforeEach
    void setUpTestData() {
        // Очистка и подготовка данных перед каждым тестом
        cleanDatabase();
        insertTestData();
    }

    @AfterEach
    void tearDownTestData() {
        // Очистка после теста
        cleanDatabase();
    }

    private void cleanDatabase() {
        jdbcTemplate.execute("TRUNCATE TABLE users RESTART IDENTITY CASCADE");
        jdbcTemplate.execute("TRUNCATE TABLE orders RESTART IDENTITY CASCADE");
    }

    private void insertTestData() {
        jdbcTemplate.update(
            "INSERT INTO users (name, email, active) VALUES (?, ?, ?)",
            "Test User", "test@example.com", true);

        jdbcTemplate.update(
            "INSERT INTO orders (user_id, amount, status) VALUES (?, ?, ?)",
            1, 99.99, "PENDING");
    }

    @Test
    void testUserCreation() {
        User newUser = new User("Jane Doe", "jane@example.com");
        User saved = userRepository.save(newUser);

        assertThat(saved.getId()).isNotNull();
        assertThat(saved.getName()).isEqualTo("Jane Doe");

        // Проверка в базе данных
        Integer userCount = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM users", Integer.class);
        assertThat(userCount).isEqualTo(2); // 1 из init + 1 новый
    }

    @Test
    void testUserWithOrders() {
        // Получение пользователя с заказами
        Optional<User> user = userRepository.findById(1L);
        assertThat(user).isPresent();

        // Проверка что заказы загружены
        List<Order> orders = orderRepository.findByUserId(1L);
        assertThat(orders).hasSize(1);
        assertThat(orders.get(0).getAmount()).isEqualTo(BigDecimal.valueOf(99.99));
    }
}
```

## Интеграция с **Spring Boot**

### **Test slices**

```java
// Тестирование только репозитория
@DataJpaTest
@Testcontainers
public class RepositoryTest {

    @Container
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:14");

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres.getJdbcUrl());
        registry.add("spring.datasource.username", postgres.getUsername());
        registry.add("spring.datasource.password", postgres.getPassword());
    }

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TestEntityManager entityManager;

    @Test
    void testRepositoryMethods() {
        User user = new User("Test User", "test@example.com");
        User saved = userRepository.save(user);

        entityManager.flush();
        entityManager.clear();

        Optional<User> found = userRepository.findByEmail("test@example.com");
        assertThat(found).isPresent();
        assertThat(found.get().getName()).isEqualTo("Test User");
    }
}

// Тестирование только веб-слоя
@WebMvcTest(UserController.class)
@Testcontainers
public class WebLayerTest {

    @Container
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:14");

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService userService;

    @Test
    void testWebLayer() throws Exception {
        UserDto userDto = new UserDto(1L, "Test User", "test@example.com");

        when(userService.getUserById(1L)).thenReturn(userDto);

        mockMvc.perform(get("/api/users/1"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.name").value("Test User"))
            .andExpect(jsonPath("$.email").value("test@example.com"));
    }
}
```

### Конфигурация для `CI/CD`

```java
@Configuration
public class CICDPipelineConfiguration {

    // Конфигурация для CI/CD пайплайнов
    @Bean
    @Profile("ci")
    public PostgreSQLContainer<?> ciPostgresContainer() {
        return new PostgreSQLContainer<>("postgres:14")
            .withDatabaseName("cidb")
            .withUsername("ciuser")
            .withPassword("cipassword")
            .withTmpFs(Collections.singletonMap("/tmp", "rw")) // Использовать tmpfs для скорости
            .withCommand("--max_connections=100") // Увеличить лимиты для параллельных тестов
            .withReuse(false); // Не переиспользовать в CI
    }

    @Bean
    @Profile("ci")
    public KafkaContainer ciKafkaContainer() {
        return new KafkaContainer(DockerImageName.parse("confluentinc/cp-kafka:7.4.0"))
            .withEnv("KAFKA_NUM_PARTITIONS", "1") // Минимальная конфигурация для CI
            .withEnv("KAFKA_DEFAULT_REPLICATION_FACTOR", "1")
            .withStartupTimeout(Duration.ofMinutes(3)); // Увеличенный таймаут для CI
    }
}

// JUnit 5 extension для параллельного выполнения
public class ParallelTestcontainersExtension implements BeforeAllCallback, AfterAllCallback {

    private static final Map<String, GenericContainer<?>> sharedContainers = new ConcurrentHashMap<>();

    @Override
    public void beforeAll(ExtensionContext context) {
        // Запуск общих контейнеров для параллельных тестов
        sharedContainers.computeIfAbsent("postgres", key ->
            new PostgreSQLContainer<>("postgres:14")
                .withReuse(true));

        sharedContainers.computeIfAbsent("redis", key ->
            new RedisContainer(DockerImageName.parse("redis:7-alpine"))
                .withReuse(true));
    }

    @Override
    public void afterAll(ExtensionContext context) {
        // Очистка после всех тестов
        // Контейнеры с reuse=true остаются запущенными
    }
}
```

## **Best practices**

### 1. Правильная организация тестов

```java
// ✅ Хорошо - структурированные интеграционные тесты
@SpringBootTest
@Testcontainers
@DisplayName("User Management Integration Tests")
@Slf4j
public class UserManagementIntegrationTest {

    @Container
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:14")
        .withDatabaseName("integration_test")
        .withUsername("test")
        .withPassword("test");

    @Autowired
    private UserService userService;

    @Autowired
    private OrderService orderService;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres.getJdbcUrl());
        registry.add("spring.datasource.username", postgres.getUsername());
        registry.add("spring.datasource.password", postgres.getPassword());
    }

    @Nested
    @DisplayName("User CRUD Operations")
    class UserCrudTests {

        @Test
        @DisplayName("Should create new user successfully")
        void shouldCreateNewUser() {
            // Given
            CreateUserRequest request = new CreateUserRequest("John Doe", "john@example.com");

            // When
            UserDto created = userService.createUser(request);

            // Then
            assertThat(created.getId()).isNotNull();
            assertThat(created.getName()).isEqualTo("John Doe");
            assertThat(created.getEmail()).isEqualTo("john@example.com");

            // Verify in database
            UserDto fromDb = userService.getUserById(created.getId());
            assertThat(fromDb).isEqualTo(created);
        }

        @Test
        @DisplayName("Should find user by email")
        void shouldFindUserByEmail() {
            // Given
            UserDto created = userService.createUser(
                new CreateUserRequest("Jane Doe", "jane@example.com"));

            // When
            Optional<UserDto> found = userService.findByEmail("jane@example.com");

            // Then
            assertThat(found).isPresent();
            assertThat(found.get().getId()).isEqualTo(created.getId());
        }

        @Test
        @DisplayName("Should handle duplicate email gracefully")
        void shouldHandleDuplicateEmail() {
            // Given
            userService.createUser(new CreateUserRequest("User 1", "duplicate@example.com"));

            // When & Then
            assertThrows(DuplicateEmailException.class, () ->
                userService.createUser(new CreateUserRequest("User 2", "duplicate@example.com")));
        }
    }

    @Nested
    @DisplayName("User-Order Relationships")
    class UserOrderRelationshipTests {

        @Test
        @DisplayName("Should create order for existing user")
        void shouldCreateOrderForExistingUser() {
            // Given
            UserDto user = userService.createUser(
                new CreateUserRequest("Order User", "order@example.com"));

            // When
            OrderDto order = orderService.createOrder(user.getId(),
                new CreateOrderRequest(BigDecimal.valueOf(99.99)));

            // Then
            assertThat(order.getId()).isNotNull();
            assertThat(order.getUserId()).isEqualTo(user.getId());
            assertThat(order.getAmount()).isEqualTo(BigDecimal.valueOf(99.99));
        }

        @Test
        @DisplayName("Should retrieve user with orders")
        void shouldRetrieveUserWithOrders() {
            // Given
            UserDto user = userService.createUser(
                new CreateUserRequest("User With Orders", "orders@example.com"));

            orderService.createOrder(user.getId(), new CreateOrderRequest(BigDecimal.valueOf(50.00)));
            orderService.createOrder(user.getId(), new CreateOrderRequest(BigDecimal.valueOf(75.00)));

            // When
            UserWithOrdersDto userWithOrders = userService.getUserWithOrders(user.getId());

            // Then
            assertThat(userWithOrders.getOrders()).hasSize(2);
            assertThat(userWithOrders.getTotalOrderAmount()).isEqualTo(BigDecimal.valueOf(125.00));
        }
    }

    @Nested
    @DisplayName("Database Constraints")
    class DatabaseConstraintsTests {

        @Test
        @DisplayName("Should enforce email uniqueness")
        void shouldEnforceEmailUniqueness() {
            // Given - создаем пользователя через SQL для обхода бизнес-логики
            jdbcTemplate.update(
                "INSERT INTO users (name, email, active) VALUES (?, ?, ?)",
                "SQL User", "sql@example.com", true);

            // When & Then - бизнес-логика должна предотвратить дубликат
            assertThrows(DuplicateEmailException.class, () ->
                userService.createUser(new CreateUserRequest("Business User", "sql@example.com")));
        }

        @Test
        @DisplayName("Should enforce foreign key constraints")
        void shouldEnforceForeignKeyConstraints() {
            // When & Then - попытка создать заказ для несуществующего пользователя
            assertThrows(DataIntegrityViolationException.class, () ->
                jdbcTemplate.update(
                    "INSERT INTO orders (user_id, amount, status) VALUES (?, ?, ?)",
                    99999L, 100.00, "PENDING"));
        }
    }

    @AfterEach
    void cleanup() {
        // Очистка данных после каждого теста
        jdbcTemplate.execute("TRUNCATE TABLE orders RESTART IDENTITY CASCADE");
        jdbcTemplate.execute("TRUNCATE TABLE users RESTART IDENTITY CASCADE");
    }
}

// ✅ Хорошо - базовый класс для интеграционных тестов
@SpringBootTest
@Testcontainers
public abstract class BaseIntegrationTest {

    @Container
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:14")
        .withDatabaseName("integration_test")
        .withUsername("test")
        .withPassword("test");

    @Autowired
    protected JdbcTemplate jdbcTemplate;

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres.getJdbcUrl());
        registry.add("spring.datasource.username", postgres.getUsername());
        registry.add("spring.datasource.password", postgres.getPassword());
    }

    @BeforeEach
    void setUpBase() {
        // Базовая подготовка данных
        ensureDatabaseReady();
    }

    private void ensureDatabaseReady() {
        try {
            jdbcTemplate.queryForObject("SELECT 1", Integer.class);
        } catch (Exception e) {
            throw new IllegalStateException("Database not ready for tests", e);
        }
    }

    protected void cleanDatabase() {
        jdbcTemplate.execute("TRUNCATE TABLE orders RESTART IDENTITY CASCADE");
        jdbcTemplate.execute("TRUNCATE TABLE users RESTART IDENTITY CASCADE");
    }

    protected UserDto createTestUser(String name, String email) {
        return UserDto.builder()
            .name(name)
            .email(email)
            .active(true)
            .build();
    }
}

// ❌ Плохо - монолитный тест
@SpringBootTest
@Testcontainers
public class BadIntegrationTest {

    @Container
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:14");

    @Autowired
    private UserService userService;

    @Autowired
    private OrderService orderService;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Test
    void testEverythingAtOnce() {
        // Тестирование всего в одном методе - сложно отлаживать
        UserDto user = userService.createUser(new CreateUserRequest("Test", "test@test.com"));
        OrderDto order = orderService.createOrder(user.getId(), new CreateOrderRequest(BigDecimal.TEN));

        // Смешивание различных проверок
        assertThat(user.getEmail()).isEqualTo("test@test.com");
        assertThat(order.getAmount()).isEqualTo(BigDecimal.TEN);

        // Проверка напрямую в БД без необходимости
        Integer userCount = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM users", Integer.class);
        assertThat(userCount).isEqualTo(1);
    }
}
```

### 2. Управление ресурсами

```java
// ✅ Хорошо - правильное управление ресурсами
@SpringBootTest
@Testcontainers
@Slf4j
public class ResourceManagementTest {

    @Container
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:14")
        .withDatabaseName("resource_test")
        .withUsername("test")
        .withPassword("test")
        .withTmpFs(Collections.singletonMap("/temp_pgdata", "rw,noexec,nosuid,size=100m"))
        .withLogConsumer(new Slf4jLogConsumer(log));

    @Autowired
    private DataSource dataSource;

    @Test
    void testConnectionPooling() throws SQLException {
        // Тестирование что соединения правильно управляются
        long startTime = System.nanoTime();

        // Выполняем несколько операций для тестирования пула
        for (int i = 0; i < 10; i++) {
            try (Connection conn = dataSource.getConnection();
                 PreparedStatement stmt = conn.prepareStatement("SELECT ?");
                 ResultSet rs = stmt.executeQuery()) {

                stmt.setInt(1, i);
                rs.next();
                assertThat(rs.getInt(1)).isEqualTo(i);
            }
        }

        long duration = (System.nanoTime() - startTime) / 1_000_000;
        log.info("Connection pool test completed in {}ms", duration);

        // Проверка что соединения вернулись в пул
        if (dataSource instanceof HikariDataSource) {
            HikariDataSource hikari = (HikariDataSource) dataSource;
            HikariPoolMXBean poolMXBean = hikari.getHikariPoolMXBean();

            log.info("Active connections: {}, Idle connections: {}, Total connections: {}",
                poolMXBean.getActiveConnections(),
                poolMXBean.getIdleConnections(),
                poolMXBean.getTotalConnections());

            assertThat(poolMXBean.getActiveConnections()).isEqualTo(0);
            assertThat(poolMXBean.getTotalConnections()).isLessThanOrEqualTo(hikari.getMaximumPoolSize());
        }
    }

    @Test
    void testResourceCleanup() {
        // Тестирование что ресурсы правильно очищаются
        AtomicInteger activeConnections = new AtomicInteger(0);

        // Имитация нагрузки с проверкой утечек
        IntStream.range(0, 50).parallel().forEach(i -> {
            activeConnections.incrementAndGet();
            try {
                // Имитация работы с ресурсом
                Thread.sleep(ThreadLocalRandom.current().nextInt(10, 50));
                performDatabaseOperation(i);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            } finally {
                activeConnections.decrementAndGet();
            }
        });

        // Проверка что все соединения освобождены
        await().atMost(5, TimeUnit.SECONDS)
            .until(() -> activeConnections.get() == 0);

        assertThat(activeConnections.get()).isEqualTo(0);
    }

    @Test
    void testMemoryUsage() {
        Runtime runtime = Runtime.getRuntime();
        long memoryBefore = runtime.totalMemory() - runtime.freeMemory();

        // Выполняем операции которые могут использовать память
        List<UserDto> users = IntStream.range(0, 1000)
            .mapToObj(i -> new UserDto((long) i, "User" + i, "user" + i + "@test.com"))
            .collect(Collectors.toList());

        // Имитация обработки
        users.parallelStream().forEach(user -> {
            performMemoryIntensiveOperation(user);
        });

        long memoryAfter = runtime.totalMemory() - runtime.freeMemory();
        long memoryUsed = memoryAfter - memoryBefore;

        log.info("Memory used during test: {} bytes", memoryUsed);

        // Проверка что нет значительных утечек
        assertThat(memoryUsed).isLessThan(50 * 1024 * 1024); // < 50MB
    }

    private void performDatabaseOperation(int index) {
        // Имитация операции с БД
        try {
            Thread.sleep(5);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    private void performMemoryIntensiveOperation(UserDto user) {
        // Имитация операции использующей память
        List<String> data = new ArrayList<>();
        for (int i = 0; i < 100; i++) {
            data.add(user.getName() + "_" + i);
        }
        // Обработка данных
        String result = data.stream()
            .filter(s -> s.contains(user.getName()))
            .collect(Collectors.joining(","));
    }
}

// ✅ Хорошо - конфигурация для различных сред
@Configuration
public class EnvironmentSpecificConfiguration {

    @Bean
    @Profile("test")
    public PostgreSQLContainer<?> testPostgresContainer() {
        return new PostgreSQLContainer<>("postgres:14")
            .withDatabaseName("testdb")
            .withUsername("test")
            .withPassword("test")
            .withReuse(true) // Переиспользование для ускорения
            .withTmpFs(Collections.singletonMap("/tmp", "rw")); // TmpFS для скорости
    }

    @Bean
    @Profile("ci")
    public PostgreSQLContainer<?> ciPostgresContainer() {
        return new PostgreSQLContainer<>("postgres:14")
            .withDatabaseName("cidb")
            .withUsername("ciuser")
            .withPassword("cipassword")
            .withReuse(false) // Не переиспользовать в CI
            .withCommand("--max_connections=200") // Больше соединений для параллельных тестов
            .withLogConsumer(new Slf4jLogConsumer(LogFactory.getLog("postgres-ci")));
    }

    @Bean
    @Profile("performance")
    public PostgreSQLContainer<?> performancePostgresContainer() {
        return new PostgreSQLContainer<>("postgres:14")
            .withDatabaseName("perfdb")
            .withUsername("perf")
            .withPassword("perf")
            .withTmpFs(Collections.singletonMap("/var/lib/postgresql/data", "rw,noexec,nosuid,size=1g"))
            .withCommand(
                "postgres",
                "-c", "shared_buffers=256MB",
                "-c", "effective_cache_size=1GB",
                "-c", "maintenance_work_mem=128MB",
                "-c", "checkpoint_completion_target=0.9",
                "-c", "wal_buffers=16MB",
                "-c", "default_statistics_target=100"
            );
    }
}

// ❌ Плохо - неправильное управление ресурсами
@SpringBootTest
@Testcontainers
public class BadResourceManagementTest {

    // Плохо: статическое поле без proper lifecycle management
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:14");

    @Autowired
    private DataSource dataSource;

    @Test
    void testWithResourceLeak() throws SQLException {
        // Плохо: соединение не закрывается в try-with-resources
        Connection conn = dataSource.getConnection();
        PreparedStatement stmt = conn.prepareStatement("SELECT 1");
        ResultSet rs = stmt.executeQuery();

        rs.next();
        assertThat(rs.getInt(1)).isEqualTo(1);

        // Утечка ресурсов! conn, stmt, rs не закрыты
    }

    @Test
    void testMemoryInefficient() {
        // Плохо: загрузка всех данных в память
        List<UserDto> allUsers = userRepository.findAll(); // Может быть миллионы записей

        // Плохо: обработка в памяти вместо в БД
        List<UserDto> activeUsers = allUsers.stream()
            .filter(UserDto::isActive)
            .collect(Collectors.toList());

        assertThat(activeUsers).isNotEmpty();
    }

    @Test
    void testNoIsolation() {
        // Плохо: тест зависит от состояния других тестов
        // Нет очистки данных между тестами
        userRepository.save(new User("Test User", "test@example.com"));

        List<User> allUsers = userRepository.findAll();
        // Тест может упасть если другие тесты тоже добавляют пользователей
        assertThat(allUsers).hasSize(1);
    }
}
```


## Заключение

**Testcontainers** — это библиотека для создания интеграционных тестов с реальными зависимостями в **Docker** контейнерах. Она значительно повышает качество и надежность тестирования за счет использования настоящих сервисов вместо **mock**-объектов.

### Преимущества **Testcontainers**

1. **Реальные зависимости** — Тесты используют настоящие сервисы вместо **in-memory** заглушек
2. **Изоляция** — Каждый тест получает чистое состояние зависимостей
3. **Совместимость** — Работает с любыми **Docker** образами
4. **Простота использования** — Минимальная конфигурация для большинства сценариев
5. **Широкая поддержка** — Модули для популярных технологий (**PostgreSQL, `MySQL`, `Kafka`, `Redis` и др.**)
6. **CI/`CD` интеграция** — Работает в **pipeline** с **Docker**
7. **Параллельное выполнение** — Поддержка параллельных тестов
8. **Переиспользование** — Возможность переиспользования контейнеров для ускорения

### Основные паттерны использования

1. **Database `Integration` паттерн** — Тестирование с реальными базами данных
2. **Message `Broker Integration` паттерн** — Тестирование с **Kafka**, **RabbitMQ**
3. **Web `Testing` паттерн** — **Selenium** тестирование с реальными браузерами
4. **Multi-service паттерн** — **Docker Compose** для комплексных интеграций
5. **Resource `Management` паттерн** — Правильное управление ресурсами контейнеров
6. **CI/CD Optimization** паттерн — Оптимизация для конвейеров `CI/CD`

### Когда использовать **Testcontainers**

**Рекомендуется:**
- **Enterprise** приложения с **complex** интеграциями
- Микросервисная архитектура
- Приложения с внешними зависимостями (**БД, очереди, кэш**)
- Проекты с высокими требованиями к качеству
- Команды практикующие **test-driven development**
- Приложения с **legacy** кодом и сложной логикой

**Особенно полезно:**
- Для тестирования **SQL** запросов и транзакций
- При работе с **message-driven** архитектурой
- Для **end-to-end** тестирования
- В проектах с **distributed caching**
- При интеграции с **external APIs**

### Сравнение с альтернативами

| Подход | Преимущества | Недостатки |
|--------|-------------|------------|
| **Testcontainers** | Реальные сервисы, высокая точность | Требует **Docker**, медленнее |
| **In-memory DB** | Быстрый, не требует **Docker** | Отличается от **production** |
| **Embedded DB** | Быстрый, удобный | Ограниченная совместимость |
| **Mock frameworks** | Быстрый, изолированный | Не проверяет интеграции |
| **Contract testing** | Быстрый, изолированный | Сложная настройка |

**Testcontainers** рекомендуется как основной инструмент для интеграционного тестирования в современных **Java** приложениях, особенно в **enterprise** проектах с **complex** архитектурой.


[⬆️ Наверх](../)

## См. также

- [[java-apache-httpclient|Apache HttpClient: Мощный HTTP клиент для Java]]
- [[java-apache-poi|Apache POI]]
- [[java-bean-validation|Bean Validation (JSR-380 / Jakarta Validation 3.0)]]
- [[java-hikaricp|HikariCP: Высокопроизводительный Connection Pool]]
- [[java-http-clients|HTTP-клиенты в Java]]
