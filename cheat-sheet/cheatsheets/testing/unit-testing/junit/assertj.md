# AssertJ для Java

Комплексное руководство по использованию AssertJ для создания читаемых и мощных assertions в Java тестах: fluent API, custom assertions, soft assertions, conditional testing и интеграция с JUnit/Spring Boot.

**Дата последнего обновления:** 2026-01-21

## Полезные ссылки

### Официальная документация
- [AssertJ Documentation](https://assertj.github.io/doc/) - Основная документация
- [AssertJ Core](https://assertj.github.io/doc/#assertj-core) - Core assertions
- [AssertJ Core Javadoc](https://www.javadoc.io/doc/org.assertj/assertj-core/latest/index.html) - API документация

### Java интеграции
- [AssertJ with JUnit 5](https://assertj.github.io/doc/#assertj-core-junit5) - JUnit 5 интеграция
- [AssertJ with Spring](https://assertj.github.io/doc/#assertj-core-spring) - Spring assertions
- [AssertJ with Guava](https://assertj.github.io/doc/#assertj-core-guava) - Guava assertions

### Best practices
- [AssertJ Best Practices](https://assertj.github.io/doc/#best-practices) - Лучшие практики
- [Fluent Assertions](https://martinfowler.com/bliki/FluentInterface.html) - О fluent interfaces
- [Readable Assertions](https://github.com/assertj/assertj/wiki/Writing-readable-assertions) - Читаемые assertions

### См. также
- `testing/junit-advanced.md` - JUnit расширения
- `testing/mockito-advanced.md` - Mockito для mocking
- `spring-testing.md` - Spring testing

## Содержание

- [Введение в AssertJ](#введение-в-assertj)
- [Basic assertions](#basic-assertions)
- [String assertions](#string-assertions)
- [Collection assertions](#collection-assertions)
- [Object assertions](#object-assertions)
- [Exception assertions](#exception-assertions)
- [Optional assertions](#optional-assertions)
- [Soft assertions](#soft-assertions)
- [Custom assertions](#custom-assertions)
- [Conditional assertions](#conditional-assertions)
- [Spring integration](#spring-integration)
- [JSON assertions](#json-assertions)
- [File assertions](#file-assertions)
- [Best practices](#best-practices)
- [Troubleshooting](#troubleshooting)
- [Заключение](#заключение)

## Введение в AssertJ

**AssertJ** — это fluent assertion library для Java, которая предоставляет богатый и читаемый API для написания assertions в тестах. AssertJ фокусируется на том, чтобы сделать тесты более выразительными и понятными.

### Почему AssertJ?

AssertJ предоставляет мощные возможности для создания assertions:

1. **Fluent API** — читаемый и текучий синтаксис
2. **Rich assertions** — множество встроенных проверок
3. **Custom assertions** — возможность создания своих assertions
4. **Soft assertions** — группировка нескольких проверок
5. **Type-safe** — проверка типов на этапе компиляции
6. **Descriptive errors** — понятные сообщения об ошибках
7. **IDE support** — отличная поддержка автодополнения
8. **Extensible** — модульная архитектура

### Maven зависимости

**AssertJ** имеет модульную архитектуру, где core модуль предоставляет базовую функциональность, а дополнительные модули расширяют возможности для работы с конкретными библиотеками и типами данных.

#### Core AssertJ (обязательный)

**assertj-core** — основной модуль, содержащий все фундаментальные assertions для стандартных Java типов.

```xml
<dependency>
    <groupId>org.assertj</groupId>
    <artifactId>assertj-core</artifactId>
    <version>3.24.2</version>
    <scope>test</scope>
</dependency>
```

**Что включает assertj-core:**
- **Basic assertions** — для всех примитивных типов и Object
- **String assertions** — специализированные методы для работы со строками
- **Collection assertions** — мощные проверки для списков, множеств, массивов
- **Map assertions** — проверки для Map интерфейсов
- **Exception assertions** — тестирование исключений
- **Optional assertions** — работа с java.util.Optional
- **Path/File assertions** — проверки файловой системы
- **Date/Time assertions** — работа с датами и временем
- **Atomic assertions** — для java.util.concurrent.atomic
- **Soft assertions** — группировка проверок
- **Custom assertions** — фреймворк для создания своих assertions

#### Дополнительные модули

**assertj-json** — специализированные assertions для работы с JSON данными:

```xml
<dependency>
    <groupId>org.assertj</groupId>
    <artifactId>assertj-json</artifactId>
    <version>3.24.2</version>
    <scope>test</scope>
</dependency>
```

**Возможности assertj-json:**
- **JSON structure validation** — проверка структуры JSON документов
- **JSON path assertions** — XPath-подобные запросы к JSON
- **JSON schema validation** — валидация по JSON Schema
- **Pretty printing** — форматированный вывод JSON в ошибках

**assertj-guava** — assertions для Google Guava коллекций:

```xml
<dependency>
    <groupId>org.assertj</groupId>
    <artifactId>assertj-guava</artifactId>
    <version>3.24.2</version>
    <scope>test</scope>
</dependency>
```

**Поддержка Guava типов:**
- **Multimap assertions** — проверки для Multimap
- **Multiset assertions** — проверки для Multiset
- **Table assertions** — проверки для Table
- **Range assertions** — проверки для Range
- **Optional assertions** — Guava Optional (в дополнение к Java 8)

**assertj-joda-time** — assertions для работы с Joda-Time:

```xml
<dependency>
    <groupId>org.assertj</groupId>
    <artifactId>assertj-joda-time</artifactId>
    <version>3.24.2</version>
    <scope>test</scope>
</dependency>
```

**Возможности для Joda-Time:**
- **DateTime assertions** — сравнение дат и времени
- **Duration assertions** — проверки интервалов времени
- **Period assertions** — проверки периодов
- **Time zone handling** — работа с часовыми поясами

**assertj-db** — assertions для работы с базами данных:

```xml
<dependency>
    <groupId>org.assertj</groupId>
    <artifactId>assertj-db</artifactId>
    <version>2.0.2</version>
    <scope>test</scope>
</dependency>
```

**Database testing features:**
- **Table assertions** — проверки содержимого таблиц
- **Row assertions** — проверки отдельных строк
- **Column assertions** — проверки колонок
- **Changes assertions** — проверки изменений в данных

#### Gradle зависимости

**Для Gradle проектов с подробными комментариями:**

```gradle
dependencies {
    // Основной модуль AssertJ - содержит все базовые assertions
    testImplementation 'org.assertj:assertj-core:3.24.2'

    // Расширение для работы с JSON
    testImplementation 'org.assertj:assertj-json:3.24.2'

    // Расширение для Google Guava коллекций
    testImplementation 'org.assertj:assertj-guava:3.24.2'

    // Расширение для Joda-Time (если используется)
    testImplementation 'org.assertj:assertj-joda-time:3.24.2'

    // Расширение для тестирования баз данных
    testImplementation 'org.assertj:assertj-db:2.0.2'

    // Для работы с Spring Boot (уже включен в spring-boot-starter-test)
    // testImplementation 'org.springframework.boot:spring-boot-starter-test'
}
```

#### Version management

**Рекомендуется использовать properties для управления версиями:**

```gradle
ext {
    assertjVersion = '3.24.2'
    assertjDbVersion = '2.0.2'
}

dependencies {
    testImplementation "org.assertj:assertj-core:${assertjVersion}"
    testImplementation "org.assertj:assertj-json:${assertjVersion}"
    testImplementation "org.assertj:assertj-guava:${assertjVersion}"
    testImplementation "org.assertj:assertj-db:${assertjDbVersion}"
}
```

#### BOM (Bill of Materials)

**Для Maven проектов рекомендуется использовать AssertJ BOM:**

```xml
<dependencyManagement>
    <dependencies>
        <dependency>
            <groupId>org.assertj</groupId>
            <artifactId>assertj-bom</artifactId>
            <version>3.24.2</version>
            <type>pom</type>
            <scope>import</scope>
        </dependency>
    </dependencies>
</dependencyManagement>

<dependencies>
    <!-- Версии будут управляться BOM -->
    <dependency>
        <groupId>org.assertj</groupId>
        <artifactId>assertj-core</artifactId>
        <scope>test</scope>
    </dependency>

    <dependency>
        <groupId>org.assertj</groupId>
        <artifactId>assertj-json</artifactId>
        <scope>test</scope>
    </dependency>
</dependencies>
```

#### Spring Boot интеграция

**Spring Boot Starter Test уже включает AssertJ:**

```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-test</artifactId>
    <scope>test</scope>
    <!-- AssertJ включен автоматически -->
</dependency>
```

**Что включает spring-boot-starter-test:**
- **JUnit 5** — основной testing framework
- **AssertJ** — fluent assertions (уже включен!)
- **Mockito** — mocking framework
- **JSONassert** — JSON assertions
- **Spring Test** — Spring testing utilities

#### Миграция с JUnit/Mockito

**Если вы используете обычный JUnit без Spring Boot:**

```xml
<properties>
    <junit.version>5.10.0</junit.version>
    <assertj.version>3.24.2</assertj.version>
</properties>

<dependencies>
    <!-- JUnit 5 -->
    <dependency>
        <groupId>org.junit.jupiter</groupId>
        <artifactId>junit-jupiter</artifactId>
        <version>${junit.version}</version>
        <scope>test</scope>
    </dependency>

    <!-- AssertJ вместо Hamcrest/Mockito assertions -->
    <dependency>
        <groupId>org.assertj</groupId>
        <artifactId>assertj-core</artifactId>
        <version>${assertj.version}</version>
        <scope>test</scope>
    </dependency>
</dependencies>
```

**Миграция типичных assertions:**

```java
// Было (JUnit 4 + Hamcrest)
assertThat(user.getName(), equalTo("John"));
assertThat(users, hasSize(5));
assertThat(user.getEmail(), containsString("@"));

// Стало (AssertJ)
assertThat(user.getName()).isEqualTo("John");
assertThat(users).hasSize(5);
assertThat(user.getEmail()).contains("@");
```

#### IDE Configuration

**IntelliJ IDEA:**
1. **File → Settings → Build, Execution, Deployment → Build Tools → Gradle**
2. **Use Gradle from**: 'gradle-wrapper.properties' file
3. **AssertJ assertions будут автоматически распознаны**

**Eclipse:**
1. **Help → Eclipse Marketplace**
2. **Find**: "AssertJ"
3. **Install**: AssertJ Eclipse integration

**VS Code:**
```json
{
    "java.test.config": {
        "name": "JUnit Jupiter + AssertJ",
        "type": "junit",
        "request": "launch",
        "mainClass": "org.junit.platform.console.ConsoleLauncher",
        "args": ["--scan-classpath"],
        "vmargs": ["-ea"],
        "dependencies": [
            "org.assertj:assertj-core:3.24.2"
        ]
    }
}
```

#### Troubleshooting зависимостей

**Проблема: Version conflicts**

```xml
<!-- Решение: исключить старую версию из транзитивных зависимостей -->
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-test</artifactId>
    <scope>test</scope>
    <exclusions>
        <exclusion>
            <groupId>org.assertj</groupId>
            <artifactId>assertj-core</artifactId>
        </exclusion>
    </exclusions>
</dependency>

<dependency>
    <groupId>org.assertj</groupId>
    <artifactId>assertj-core</artifactId>
    <version>3.24.2</version>
    <scope>test</scope>
</dependency>
```

**Проблема: Missing dependencies**

```bash
# Проверить dependency tree
mvn dependency:tree

# Gradle dependencies
gradle dependencies --configuration testRuntimeClasspath
```

**Проблема: IDE не распознает assertions**

```java
// Добавить static import
import static org.assertj.core.api.Assertions.*;

// Или использовать fully qualified names
org.assertj.core.api.Assertions.assertThat(users).hasSize(5);
```

### Basic usage

#### Простые assertions
```java
@SpringBootTest
public class AssertJBasicTest {

    @Autowired
    private UserService userService;

    @Test
    void testUserCreation() {
        User user = userService.createUser("john@example.com", "John Doe");

        // AssertJ assertions вместо JUnit assertEquals
        assertThat(user).isNotNull();
        assertThat(user.getEmail()).isEqualTo("john@example.com");
        assertThat(user.getName()).isEqualTo("John Doe");
        assertThat(user.getStatus()).isEqualTo(UserStatus.ACTIVE);
    }

    @Test
    void testUserList() {
        List<User> users = userService.getAllUsers();

        // Collection assertions
        assertThat(users).isNotNull();
        assertThat(users).hasSizeGreaterThan(0);
        assertThat(users).extracting("email").contains("john@example.com");
    }
}
```

## Basic assertions

### Numeric assertions

#### Integer и Long assertions
```java
@SpringBootTest
public class NumericAssertionsTest {

    @Autowired
    private OrderService orderService;

    @Test
    void testOrderCalculations() {
        Order order = orderService.createOrder(List.of(
            new OrderItem("item1", BigDecimal.valueOf(10.0), 2),
            new OrderItem("item2", BigDecimal.valueOf(5.0), 3)
        ));

        // BigDecimal assertions
        assertThat(order.getSubtotal()).isEqualByComparingTo(BigDecimal.valueOf(35.0));
        assertThat(order.getTax()).isGreaterThan(BigDecimal.ZERO);
        assertThat(order.getTotal()).isBetween(BigDecimal.valueOf(35.0), BigDecimal.valueOf(50.0));

        // Integer assertions
        assertThat(order.getItemCount()).isEqualTo(5);
        assertThat(order.getItemCount()).isPositive();
        assertThat(order.getItemCount()).isLessThan(10);

        // Long assertions
        assertThat(order.getId()).isNotNull();
        assertThat(order.getId()).isPositive();
    }

    @Test
    void testStatistics() {
        OrderStatistics stats = orderService.getStatistics();

        // Statistics assertions
        assertThat(stats.getTotalOrders()).isGreaterThanOrEqualTo(0);
        assertThat(stats.getAverageOrderValue()).isNotNegative();
        assertThat(stats.getMaxOrderValue()).isGreaterThanOrEqualTo(stats.getMinOrderValue());

        // Percentage assertions
        assertThat(stats.getConversionRate()).isBetween(0.0, 100.0);
    }
}
```

### Boolean assertions

#### Boolean logic assertions
```java
@SpringBootTest
public class BooleanAssertionsTest {

    @Autowired
    private UserService userService;

    @Autowired
    private PaymentService paymentService;

    @Test
    void testUserValidation() {
        User user = new User("valid@example.com", "Valid User");

        // Boolean property assertions
        assertThat(userService.isValidEmail(user.getEmail())).isTrue();
        assertThat(userService.isValidName(user.getName())).isTrue();
        assertThat(userService.isUserActive(user)).isTrue();

        // Boolean method results
        assertThat(userService.userExists(user.getEmail())).isFalse(); // Before creation
        assertThat(userService.canDeleteUser(user)).isTrue();
    }

    @Test
    void testPaymentProcessing() {
        PaymentRequest request = new PaymentRequest(BigDecimal.valueOf(100.0), "valid-card");

        // Payment status assertions
        PaymentResult result = paymentService.processPayment(request);

        assertThat(result.isSuccessful()).isTrue();
        assertThat(result.hasErrors()).isFalse();
        assertThat(result.needsReview()).isFalse();

        // Business rule assertions
        assertThat(paymentService.isPaymentAllowed(request)).isTrue();
        assertThat(paymentService.isAmountValid(request.getAmount())).isTrue();
    }

    @Test
    void testFeatureFlags() {
        // Feature toggle assertions
        assertThat(featureService.isFeatureEnabled("new-checkout")).isTrue();
        assertThat(featureService.isFeatureEnabled("legacy-api")).isFalse();

        // Conditional logic assertions
        if (featureService.isBetaUser(user)) {
            assertThat(featureService.hasAccessToBetaFeatures(user)).isTrue();
        }
    }
}
```

## String assertions

### Basic string assertions

#### String content assertions
```java
@SpringBootTest
public class StringAssertionsTest {

    @Autowired
    private EmailService emailService;

    @Autowired
    private ValidationService validationService;

    @Test
    void testEmailContent() {
        String emailContent = emailService.generateWelcomeEmail("John", "john@example.com");

        // Basic string assertions
        assertThat(emailContent).isNotNull();
        assertThat(emailContent).isNotEmpty();
        assertThat(emailContent).contains("Welcome");
        assertThat(emailContent).contains("John");
        assertThat(emailContent).doesNotContain("Error");

        // Case insensitive assertions
        assertThat(emailContent).containsIgnoringCase("welcome");
        assertThat(emailContent.toLowerCase()).doesNotContain("error");
    }

    @Test
    void testEmailValidation() {
        // Valid emails
        assertThat(validationService.isValidEmail("user@example.com")).isTrue();
        assertThat(validationService.isValidEmail("test.email+tag@gmail.com")).isTrue();

        // Invalid emails
        assertThat(validationService.isValidEmail("")).isFalse();
        assertThat(validationService.isValidEmail("invalid-email")).isFalse();
        assertThat(validationService.isValidEmail("@example.com")).isFalse();
    }

    @Test
    void testPasswordValidation() {
        // Strong passwords
        assertThat(validationService.isStrongPassword("MySecurePass123!")).isTrue();
        assertThat(validationService.isStrongPassword("Complex@Password#2023")).isTrue();

        // Weak passwords
        assertThat(validationService.isStrongPassword("weak")).isFalse();
        assertThat(validationService.isStrongPassword("123456")).isFalse();
        assertThat(validationService.isStrongPassword("password")).isFalse();
    }
}
```

### Advanced string assertions

#### String pattern matching
```java
@SpringBootTest
public class AdvancedStringAssertionsTest {

    @Autowired
    private IdGenerator idGenerator;

    @Autowired
    private MessageFormatter messageFormatter;

    @Test
    void testIdGeneration() {
        String userId = idGenerator.generateUserId();
        String orderId = idGenerator.generateOrderId();

        // UUID pattern assertions
        assertThat(userId).matches("[0-9a-f]{8}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{12}");
        assertThat(orderId).matches("ORD-[0-9]{6}-[A-Z]{3}");

        // Length assertions
        assertThat(userId).hasSize(36); // UUID length
        assertThat(orderId).startsWith("ORD-");
        assertThat(orderId).endsWith("-ABC"); // Assuming ABC suffix
    }

    @Test
    void testMessageFormatting() {
        String message = messageFormatter.formatOrderConfirmation("John", "ORD-123456-ABC", BigDecimal.valueOf(99.99));

        // Multi-line string assertions
        assertThat(message).contains("Dear John");
        assertThat(message).contains("ORD-123456-ABC");
        assertThat(message).contains("$99.99");
        assertThat(message).contains("Thank you");

        // String structure assertions
        assertThat(message).startsWith("Dear");
        assertThat(message).endsWith("Best regards");
        assertThat(message).hasLineCount(5);
    }

    @Test
    void testHtmlContent() {
        String html = htmlGenerator.generateUserProfile("John", "john@example.com");

        // HTML structure assertions
        assertThat(html).startsWith("<!DOCTYPE html>");
        assertThat(html).contains("<title>");
        assertThat(html).contains("John");
        assertThat(html).contains("john@example.com");
        assertThat(html).doesNotContain("<script>"); // Security check
    }
}
```

## Collection assertions

### List assertions

#### Basic list operations
```java
@SpringBootTest
public class ListAssertionsTest {

    @Autowired
    private UserService userService;

    @Autowired
    private ProductService productService;

    @Test
    void testUserList() {
        List<User> users = userService.getAllUsers();

        // Basic list assertions
        assertThat(users).isNotNull();
        assertThat(users).isNotEmpty();
        assertThat(users).hasSizeGreaterThan(0);

        // Element assertions
        assertThat(users).hasAtLeastOneElementOfType(User.class);
        assertThat(users).doesNotHaveDuplicates();

        // Extracting and asserting properties
        assertThat(users).extracting("email")
            .doesNotContainNull()
            .allMatch(email -> email.toString().contains("@"));

        assertThat(users).extracting("status")
            .contains(UserStatus.ACTIVE);
    }

    @Test
    void testProductList() {
        List<Product> products = productService.getAvailableProducts();

        // Filtered assertions
        assertThat(products).filteredOn(product -> product.getPrice().compareTo(BigDecimal.valueOf(100.0)) > 0)
            .hasSizeGreaterThan(0);

        // Sorting assertions
        assertThat(products).extracting("price", BigDecimal.class)
            .isSorted();

        // Complex filtering
        assertThat(products).filteredOn(product ->
                product.getCategory().equals("Electronics") &&
                product.getStock() > 0)
            .isNotEmpty();
    }

    @Test
    void testOrderItems() {
        Order order = orderService.createOrder(List.of(
            new OrderItem("laptop", BigDecimal.valueOf(1000.0), 1),
            new OrderItem("mouse", BigDecimal.valueOf(25.0), 2),
            new OrderItem("keyboard", BigDecimal.valueOf(75.0), 1)
        ));

        // Nested collection assertions
        assertThat(order.getItems())
            .hasSize(3)
            .extracting("productName")
            .contains("laptop", "mouse", "keyboard");

        assertThat(order.getItems())
            .extracting("quantity")
            .contains(1, 2, 1);

        assertThat(order.getItems())
            .extracting("price", BigDecimal.class)
            .contains(BigDecimal.valueOf(1000.0), BigDecimal.valueOf(25.0), BigDecimal.valueOf(75.0));
    }
}
```

### Set assertions

#### Set-specific operations
```java
@SpringBootTest
public class SetAssertionsTest {

    @Autowired
    private PermissionService permissionService;

    @Autowired
    private TagService tagService;

    @Test
    void testUserPermissions() {
        Set<String> permissions = permissionService.getUserPermissions("admin");

        // Set assertions
        assertThat(permissions).isNotNull();
        assertThat(permissions).isNotEmpty();
        assertThat(permissions).contains("READ_USER", "WRITE_USER", "DELETE_USER");

        // Set-specific assertions
        assertThat(permissions).doesNotHaveDuplicates();

        // Size assertions
        assertThat(permissions).hasSizeGreaterThan(5);
        assertThat(permissions).hasSizeLessThan(20);
    }

    @Test
    void testProductTags() {
        Set<String> tags = tagService.getProductTags(123L);

        // Tag assertions
        assertThat(tags).isNotNull();
        assertThat(tags).contains("electronics", "laptop");

        // Case insensitive assertions
        assertThat(tags).filteredOn(tag -> tag.toLowerCase().contains("tech"))
            .isNotEmpty();

        // Subset assertions
        Set<String> premiumTags = Set.of("premium", "expensive", "high-end");
        assertThat(tags).doesNotContainAnyElementsOf(premiumTags);
    }

    @Test
    void testUniqueConstraints() {
        Set<String> usernames = userService.getAllUsernames();

        // Uniqueness assertions
        assertThat(usernames).doesNotHaveDuplicates();
        assertThat(usernames).allMatch(username -> username.length() >= 3);
        assertThat(usernames).noneMatch(username -> username.contains(" "));

        // Case sensitivity
        Set<String> lowerCaseUsernames = usernames.stream()
            .map(String::toLowerCase)
            .collect(Collectors.toSet());

        assertThat(lowerCaseUsernames).hasSameSizeAs(usernames);
    }
}
```

### Map assertions

#### Map structure assertions
```java
@SpringBootTest
public class MapAssertionsTest {

    @Autowired
    private ConfigurationService configService;

    @Autowired
    private MetricsService metricsService;

    @Test
    void testConfigurationMap() {
        Map<String, String> config = configService.getApplicationConfig();

        // Basic map assertions
        assertThat(config).isNotNull();
        assertThat(config).isNotEmpty();
        assertThat(config).hasSizeGreaterThan(5);

        // Key assertions
        assertThat(config).containsKey("database.url");
        assertThat(config).containsKey("cache.ttl");
        assertThat(config).doesNotContainKey("secret.password");

        // Value assertions
        assertThat(config).containsValue("postgresql://localhost:5432/mydb");
        assertThat(config).hasEntrySatisfying("database.pool.size",
            value -> assertThat(Integer.parseInt(value)).isBetween(5, 20));

        // Entry assertions
        assertThat(config).containsEntry("app.name", "MyApplication");
        assertThat(config).hasEntrySatisfying("app.version",
            value -> assertThat(value).matches("\\d+\\.\\d+\\.\\d+"));
    }

    @Test
    void testMetricsMap() {
        Map<String, Number> metrics = metricsService.getCurrentMetrics();

        // Metrics map assertions
        assertThat(metrics).isNotNull();
        assertThat(metrics).containsKey("jvm.memory.used");
        assertThat(metrics).containsKey("http.requests.total");

        // Value type assertions
        assertThat(metrics).hasEntrySatisfying("jvm.memory.used",
            value -> assertThat(value.longValue()).isPositive());

        assertThat(metrics).hasEntrySatisfying("http.requests.total",
            value -> assertThat(value.longValue()).isGreaterThanOrEqualTo(0));

        // Range assertions
        assertThat(metrics).hasEntrySatisfying("cpu.usage",
            value -> assertThat(value.doubleValue()).isBetween(0.0, 100.0));
    }

    @Test
    void testNestedMap() {
        Map<String, Map<String, Object>> nestedConfig = configService.getNestedConfig();

        // Nested map assertions
        assertThat(nestedConfig).isNotNull();
        assertThat(nestedConfig).containsKey("database");

        Map<String, Object> dbConfig = nestedConfig.get("database");
        assertThat(dbConfig).isNotNull();
        assertThat(dbConfig).containsKey("url");
        assertThat(dbConfig).hasEntrySatisfying("poolSize",
            poolSize -> assertThat((Integer) poolSize).isBetween(1, 50));
    }
}
```

## Object assertions

### Basic object assertions

#### POJO assertions
```java
@SpringBootTest
public class ObjectAssertionsTest {

    @Autowired
    private UserService userService;

    @Autowired
    private OrderService orderService;

    @Test
    void testUserObject() {
        User user = userService.createUser("john@example.com", "John Doe");

        // Basic object assertions
        assertThat(user).isNotNull();
        assertThat(user).isInstanceOf(User.class);
        assertThat(user).isNotInstanceOf(AdminUser.class);

        // Property assertions
        assertThat(user).hasFieldOrProperty("email");
        assertThat(user).hasFieldOrProperty("name");
        assertThat(user).hasFieldOrPropertyWithValue("email", "john@example.com");
        assertThat(user).hasFieldOrPropertyWithValue("name", "John Doe");

        // Null checks
        assertThat(user).hasNoNullFieldsOrPropertiesExcept("phoneNumber");
    }

    @Test
    void testOrderObject() {
        Order order = orderService.createOrder(List.of(
            new OrderItem("laptop", BigDecimal.valueOf(1000.0), 1)
        ));

        // Complex object assertions
        assertThat(order).isNotNull();
        assertThat(order.getId()).isNotNull();
        assertThat(order.getStatus()).isEqualTo(OrderStatus.PENDING);
        assertThat(order.getItems()).isNotEmpty();
        assertThat(order.getTotalAmount()).isGreaterThan(BigDecimal.ZERO);

        // Nested object assertions
        assertThat(order.getItems().get(0))
            .hasFieldOrPropertyWithValue("productName", "laptop")
            .hasFieldOrPropertyWithValue("price", BigDecimal.valueOf(1000.0))
            .hasFieldOrPropertyWithValue("quantity", 1);
    }

    @Test
    void testUserComparison() {
        User user1 = userService.createUser("user1@example.com", "User 1");
        User user2 = userService.createUser("user2@example.com", "User 2");

        // Object comparison assertions
        assertThat(user1).isNotEqualTo(user2);
        assertThat(user1).isEqualToComparingOnlyGivenFields(user2, "status"); // Both ACTIVE

        // Field by field comparison
        assertThat(user1).usingRecursiveComparison()
            .ignoringFields("id", "createdAt") // Ignore auto-generated fields
            .isNotEqualTo(user2);
    }
}
```

### Recursive comparison

#### Deep object comparison
```java
@SpringBootTest
public class RecursiveComparisonTest {

    @Autowired
    private OrderService orderService;

    @Test
    void testOrderRecursiveComparison() {
        Order expectedOrder = createExpectedOrder();
        Order actualOrder = orderService.createOrder(expectedOrder.getItems());

        // Recursive comparison ignoring certain fields
        assertThat(actualOrder).usingRecursiveComparison()
            .ignoringFields("id", "createdAt", "updatedAt") // Auto-generated
            .ignoringFields("items.id", "items.order") // Circular references
            .isEqualTo(expectedOrder);

        // More specific recursive assertions
        assertThat(actualOrder).usingRecursiveComparison()
            .comparingOnlyFields("status", "totalAmount", "customer.email")
            .isEqualTo(expectedOrder);
    }

    @Test
    void testUserRecursiveComparison() {
        User expectedUser = new User("john@example.com", "John Doe");
        expectedUser.setPhoneNumber("+1234567890");
        expectedUser.setAddress(new Address("123 Main St", "Springfield", "IL", "62701"));

        User actualUser = userService.createUser("john@example.com", "John Doe");

        // Recursive comparison with custom configuration
        assertThat(actualUser).usingRecursiveComparison()
            .ignoringFields("id", "createdAt", "version") // Technical fields
            .ignoringFieldsMatchingRegexes(".*\\.id$", ".*\\.createdAt$") // All id/createdAt fields
            .withEqualsForFields((a, b) -> normalizePhone(a).equals(normalizePhone(b)), "phoneNumber")
            .isEqualTo(expectedUser);
    }

    @Test
    void testCollectionRecursiveComparison() {
        List<Order> expectedOrders = createExpectedOrders();
        List<Order> actualOrders = orderService.getRecentOrders(2);

        // Recursive comparison for collections
        assertThat(actualOrders).usingRecursiveFieldByFieldElementComparator()
            .ignoringFields("id", "createdAt", "items.id")
            .containsExactlyInAnyOrderElementsOf(expectedOrders);
    }

    private Order createExpectedOrder() {
        Order order = new Order();
        order.setStatus(OrderStatus.PENDING);
        order.setTotalAmount(BigDecimal.valueOf(100.0));
        order.setItems(List.of(
            new OrderItem("laptop", BigDecimal.valueOf(100.0), 1)
        ));
        order.setCustomer(new User("customer@example.com", "Customer"));
        return order;
    }

    private String normalizePhone(String phone) {
        return phone != null ? phone.replaceAll("[^\\d]", "") : null;
    }
}
```

## Exception assertions

### Basic exception assertions

#### Exception type and message
```java
@SpringBootTest
public class ExceptionAssertionsTest {

    @Autowired
    private UserService userService;

    @Autowired
    private PaymentService paymentService;

    @Test
    void testUserNotFoundException() {
        // Assert exception type and message
        assertThatThrownBy(() -> userService.findById(999L))
            .isInstanceOf(UserNotFoundException.class)
            .hasMessage("User not found with id: 999")
            .hasMessageContaining("not found")
            .hasNoCause();
    }

    @Test
    void testValidationException() {
        // Test validation with invalid data
        assertThatThrownBy(() -> userService.createUser("", "John Doe"))
            .isInstanceOf(ValidationException.class)
            .hasMessage("Email is required")
            .hasMessageStartingWith("Email")
            .hasMessageEndingWith("required");
    }

    @Test
    void testPaymentException() {
        PaymentRequest invalidRequest = new PaymentRequest(BigDecimal.ZERO, "");

        assertThatThrownBy(() -> paymentService.processPayment(invalidRequest))
            .isInstanceOf(PaymentException.class)
            .hasMessage("Invalid payment request")
            .hasCauseInstanceOf(ValidationException.class)
            .satisfies(exception -> {
                // Additional custom checks
                assertThat(exception.getErrorCode()).isEqualTo("INVALID_PAYMENT");
                assertThat(exception.getTimestamp()).isNotNull();
            });
    }

    @Test
    void testNoExceptionThrown() {
        // Assert that no exception is thrown
        assertThatCode(() -> userService.createUser("valid@example.com", "Valid User"))
            .doesNotThrowAnyException();
    }
}
```

### Exception chain assertions

#### Nested exceptions
```java
@SpringBootTest
public class ExceptionChainAssertionsTest {

    @Autowired
    private ComplexService complexService;

    @Test
    void testExceptionChain() {
        assertThatThrownBy(() -> complexService.processWithNestedCalls("invalid"))
            .isInstanceOf(BusinessException.class)
            .hasMessage("Processing failed")
            .hasCauseInstanceOf(DataAccessException.class)
            .hasRootCauseInstanceOf(SQLException.class)
            .satisfies(exception -> {
                // Check the entire chain
                Throwable cause = exception.getCause();
                assertThat(cause).isInstanceOf(DataAccessException.class);

                Throwable rootCause = getRootCause(exception);
                assertThat(rootCause).isInstanceOf(SQLException.class);
                assertThat(rootCause.getMessage()).contains("constraint violation");
            });
    }

    @Test
    void testExceptionWithSuppressed() {
        assertThatThrownBy(() -> complexService.processWithSuppressedExceptions("problematic"))
            .isInstanceOf(ProcessingException.class)
            .satisfies(exception -> {
                // Check suppressed exceptions
                Throwable[] suppressed = exception.getSuppressed();
                assertThat(suppressed).hasSize(2);

                assertThat(suppressed[0]).isInstanceOf(ValidationException.class);
                assertThat(suppressed[1]).isInstanceOf(ExternalServiceException.class);

                // Check that main exception has its own message
                assertThat(exception.getMessage()).contains("Multiple errors occurred");
            });
    }

    @Test
    void testCustomExceptionAssertions() {
        assertThatThrownBy(() -> complexService.callExternalService("timeout"))
            .isInstanceOf(ServiceUnavailableException.class)
            .satisfies(exception -> {
                ServiceUnavailableException sue = (ServiceUnavailableException) exception;

                // Custom assertions for specific exception type
                assertThat(sue.getServiceName()).isEqualTo("external-api");
                assertThat(sue.getRetryAfterSeconds()).isGreaterThan(0);
                assertThat(sue.getRetryAfterSeconds()).isLessThanOrEqualTo(300);

                // Check error details
                Map<String, Object> details = sue.getErrorDetails();
                assertThat(details).containsKey("endpoint");
                assertThat(details).containsKey("attempts");
                assertThat(details).hasEntrySatisfying("attempts",
                    attempts -> assertThat((Integer) attempts).isGreaterThan(0));
            });
    }

    private Throwable getRootCause(Throwable throwable) {
        Throwable cause = throwable;
        while (cause.getCause() != null) {
            cause = cause.getCause();
        }
        return cause;
    }
}
```

## Optional assertions

### Optional basic assertions

#### Optional presence and value
```java
@SpringBootTest
public class OptionalAssertionsTest {

    @Autowired
    private UserService userService;

    @Autowired
    private ProductService productService;

    @Test
    void testUserOptional() {
        Optional<User> existingUser = userService.findUserByEmail("john@example.com");
        Optional<User> nonExistingUser = userService.findUserByEmail("nonexistent@example.com");

        // Optional presence assertions
        assertThat(existingUser).isPresent();
        assertThat(nonExistingUser).isEmpty();
        assertThat(existingUser).isNotEmpty();

        // Optional value assertions
        assertThat(existingUser).hasValueSatisfying(user -> {
            assertThat(user.getEmail()).isEqualTo("john@example.com");
            assertThat(user.getName()).isEqualTo("John Doe");
        });

        // Extract and assert
        assertThat(existingUser.get().getEmail()).isEqualTo("john@example.com");
    }

    @Test
    void testProductOptional() {
        Optional<Product> product = productService.findById(123L);

        // Conditional assertions based on presence
        if (product.isPresent()) {
            assertThat(product.get().getPrice()).isGreaterThan(BigDecimal.ZERO);
            assertThat(product.get().getStock()).isGreaterThanOrEqualTo(0);
        } else {
            // Product not found - this might be expected
            assertThat(product).isEmpty();
        }

        // Fluent optional assertions
        assertThat(product)
            .hasValueSatisfying(p -> assertThat(p.getName()).isNotNull())
            .hasValueSatisfying(p -> assertThat(p.getCategory()).isNotNull());
    }

    @Test
    void testOptionalChaining() {
        Optional<Order> order = orderService.findOrderWithCustomer(456L);

        // Nested optional assertions
        assertThat(order)
            .isPresent()
            .hasValueSatisfying(o -> assertThat(o.getCustomer()).isPresent())
            .hasValueSatisfying(o -> assertThat(o.getCustomer().get().getEmail()).isNotNull());

        // Extract nested values
        assertThat(order.flatMap(Order::getCustomer).map(User::getEmail))
            .isPresent()
            .hasValue("customer@example.com");
    }
}
```

### Advanced optional assertions

#### Optional with custom matchers
```java
@SpringBootTest
public class AdvancedOptionalAssertionsTest {

    @Autowired
    private UserService userService;

    @Autowired
    private ConfigurationService configService;

    @Test
    void testOptionalWithComplexAssertions() {
        Optional<User> userOpt = userService.findUserWithDetails(789L);

        // Complex optional assertions
        assertThat(userOpt)
            .isPresent()
            .hasValueSatisfying(user -> {
                // Basic fields
                assertThat(user.getEmail()).contains("@");
                assertThat(user.getName()).isNotBlank();

                // Nested objects
                assertThat(user.getProfile()).isNotNull();
                assertThat(user.getProfile().getPreferences()).isNotEmpty();

                // Collections
                assertThat(user.getRoles()).isNotEmpty();
                assertThat(user.getRoles()).contains("USER");

                // Dates
                assertThat(user.getCreatedAt()).isBefore(Instant.now());
                assertThat(user.getLastLogin()).isBeforeOrEqualTo(Instant.now());
            });
    }

    @Test
    void testOptionalFiltering() {
        List<Optional<User>> users = userService.findUsersByIds(List.of(1L, 2L, 999L));

        // Assert that we have 3 optionals
        assertThat(users).hasSize(3);

        // Assert presence/absence
        assertThat(users.get(0)).isPresent(); // User 1 exists
        assertThat(users.get(1)).isPresent(); // User 2 exists
        assertThat(users.get(2)).isEmpty();   // User 999 doesn't exist

        // Filter and assert present users
        List<User> presentUsers = users.stream()
            .flatMap(Optional::stream)
            .collect(Collectors.toList());

        assertThat(presentUsers).hasSize(2);
        assertThat(presentUsers).extracting("id").contains(1L, 2L);
    }

    @Test
    void testConfigurationOptional() {
        Optional<String> dbUrl = configService.getConfigValue("database.url");
        Optional<Integer> poolSize = configService.getConfigInt("database.pool.size");

        // Assert optional configurations
        assertThat(dbUrl)
            .isPresent()
            .hasValueSatisfying(url -> assertThat(url).startsWith("jdbc:"));

        assertThat(poolSize)
            .isPresent()
            .hasValueSatisfying(size -> assertThat(size).isBetween(1, 100));

        // Test default values
        Optional<String> missingConfig = configService.getConfigValue("nonexistent.key");
        assertThat(missingConfig).isEmpty();

        String valueWithDefault = configService.getConfigValue("nonexistent.key", "default");
        assertThat(valueWithDefault).isEqualTo("default");
    }
}
```

## Soft assertions

### Basic soft assertions

#### Grouped assertions
```java
@SpringBootTest
public class SoftAssertionsTest {

    @Autowired
    private UserService userService;

    @Autowired
    private OrderService orderService;

    @Test
    void testUserCreationWithSoftAssertions() {
        User user = userService.createUser("john@example.com", "John Doe");

        // Soft assertions - all checks are performed even if some fail
        SoftAssertions softly = new SoftAssertions();

        softly.assertThat(user).isNotNull();
        softly.assertThat(user.getEmail()).isEqualTo("john@example.com");
        softly.assertThat(user.getName()).isEqualTo("John Doe");
        softly.assertThat(user.getStatus()).isEqualTo(UserStatus.ACTIVE);
        softly.assertThat(user.getCreatedAt()).isBefore(Instant.now());

        // Don't forget to assert all!
        softly.assertAll();
    }

    @Test
    void testOrderProcessingWithSoftAssertions() {
        Order order = orderService.createOrder(List.of(
            new OrderItem("laptop", BigDecimal.valueOf(1000.0), 1),
            new OrderItem("mouse", BigDecimal.valueOf(25.0), 1)
        ));

        SoftAssertions softly = new SoftAssertions();

        // Order-level assertions
        softly.assertThat(order.getId()).isNotNull();
        softly.assertThat(order.getStatus()).isEqualTo(OrderStatus.PENDING);
        softly.assertThat(order.getTotalAmount()).isEqualByComparingTo(BigDecimal.valueOf(1025.0));

        // Item-level assertions
        softly.assertThat(order.getItems()).hasSize(2);
        softly.assertThat(order.getItems().get(0).getProductName()).isEqualTo("laptop");
        softly.assertThat(order.getItems().get(1).getProductName()).isEqualTo("mouse");

        // Customer assertions (if present)
        softly.assertThat(order.getCustomer()).isNotNull();
        softly.assertThat(order.getCustomer().getEmail()).isNotNull();

        softly.assertAll();
    }

    @Test
    void testComplexObjectWithSoftAssertions() {
        ComplexObject obj = complexService.createComplexObject();

        SoftAssertions softly = new SoftAssertions();

        // Basic properties
        softly.assertThat(obj.getId()).isNotNull();
        softly.assertThat(obj.getName()).isNotBlank();
        softly.assertThat(obj.getCreatedAt()).isNotNull();

        // Nested objects
        softly.assertThat(obj.getMetadata()).isNotNull();
        softly.assertThat(obj.getMetadata().getVersion()).isEqualTo("1.0");
        softly.assertThat(obj.getMetadata().getTags()).isNotEmpty();

        // Collections
        softly.assertThat(obj.getItems()).isNotEmpty();
        softly.assertThat(obj.getItems()).hasSizeGreaterThan(0);
        softly.assertThat(obj.getItems()).allMatch(item -> item.getName() != null);

        // Numeric values
        softly.assertThat(obj.getScore()).isBetween(0.0, 100.0);
        softly.assertThat(obj.getPriority()).isGreaterThan(0);

        softly.assertAll();
    }
}
```

### Auto-closeable soft assertions

#### Using try-with-resources
```java
@SpringBootTest
public class AutoCloseableSoftAssertionsTest {

    @Autowired
    private ValidationService validationService;

    @Test
    void testMultipleValidationsWithAutoCloseable() {
        List<String> emails = List.of(
            "valid@example.com",
            "invalid-email",
            "another@valid.com",
            ""
        );

        // Auto-closeable soft assertions
        try (SoftAssertions softly = new SoftAssertions()) {
            for (int i = 0; i < emails.size(); i++) {
                String email = emails.get(i);
                boolean isValid = validationService.isValidEmail(email);

                if (i % 2 == 0) { // Even indices should be valid
                    softly.assertThat(isValid)
                        .describedAs("Email '%s' at index %d should be valid", email, i)
                        .isTrue();
                } else { // Odd indices should be invalid
                    softly.assertThat(isValid)
                        .describedAs("Email '%s' at index %d should be invalid", email, i)
                        .isFalse();
                }
            }
        } // assertAll() is called automatically
    }

    @Test
    void testUserRegistrationWithAutoCloseable() {
        try (SoftAssertions softly = new SoftAssertions()) {
            User user = userService.createUser("test@example.com", "Test User");

            softly.assertThat(user).isNotNull();
            softly.assertThat(user.getEmail()).isEqualTo("test@example.com");
            softly.assertThat(user.getName()).isEqualTo("Test User");
            softly.assertThat(user.getStatus()).isEqualTo(UserStatus.ACTIVE);

            // Additional async checks
            CompletableFuture<Void> emailCheck = userService.checkWelcomeEmailSent(user.getId());
            softly.assertThat(emailCheck).succeedsWithin(Duration.ofSeconds(5));

            // Database consistency checks
            softly.assertThat(userRepository.existsById(user.getId())).isTrue();
            softly.assertThat(userRepository.findByEmail(user.getEmail())).isPresent();

        } // All assertions checked here
    }

    @Test
    void testDataIntegrityWithAutoCloseable() {
        Order order = createTestOrder();

        try (SoftAssertions softly = new SoftAssertions()) {
            // Order integrity
            softly.assertThat(order.getId()).isNotNull();
            softly.assertThat(order.getItems()).isNotEmpty();
            softly.assertThat(order.getTotalAmount()).isGreaterThan(BigDecimal.ZERO);

            // Item integrity
            for (OrderItem item : order.getItems()) {
                softly.assertThat(item.getId()).isNotNull();
                softly.assertThat(item.getProductName()).isNotBlank();
                softly.assertThat(item.getPrice()).isGreaterThan(BigDecimal.ZERO);
                softly.assertThat(item.getQuantity()).isGreaterThan(0);
            }

            // Customer integrity
            softly.assertThat(order.getCustomer()).isNotNull();
            softly.assertThat(order.getCustomer().getEmail()).isNotBlank();

            // Cross-references
            softly.assertThat(order.getItems())
                .allMatch(item -> item.getOrder().equals(order));

        } // All integrity checks performed
    }
}
```

## Custom assertions

### Abstract assertion classes

#### Custom assertion for User
```java
public class UserAssert extends AbstractAssert<UserAssert, User> {

    public UserAssert(User actual) {
        super(actual, UserAssert.class);
    }

    public static UserAssert assertThat(User actual) {
        return new UserAssert(actual);
    }

    public UserAssert hasEmail(String email) {
        isNotNull();
        if (!Objects.equals(actual.getEmail(), email)) {
            failWithMessage("Expected user to have email <%s> but was <%s>",
                email, actual.getEmail());
        }
        return this;
    }

    public UserAssert hasName(String name) {
        isNotNull();
        if (!Objects.equals(actual.getName(), name)) {
            failWithMessage("Expected user to have name <%s> but was <%s>",
                name, actual.getName());
        }
        return this;
    }

    public UserAssert isActive() {
        isNotNull();
        if (actual.getStatus() != UserStatus.ACTIVE) {
            failWithMessage("Expected user to be ACTIVE but was <%s>", actual.getStatus());
        }
        return this;
    }

    public UserAssert wasCreatedAfter(Instant timestamp) {
        isNotNull();
        if (actual.getCreatedAt().isBefore(timestamp)) {
            failWithMessage("Expected user to be created after <%s> but was <%s>",
                timestamp, actual.getCreatedAt());
        }
        return this;
    }

    public UserAssert hasValidEmail() {
        isNotNull();
        String email = actual.getEmail();
        if (email == null || !email.contains("@")) {
            failWithMessage("Expected user to have valid email but was <%s>", email);
        }
        return this;
    }

    public UserAssert hasRole(String role) {
        isNotNull();
        if (actual.getRoles() == null || !actual.getRoles().contains(role)) {
            failWithMessage("Expected user to have role <%s> but roles were <%s>",
                role, actual.getRoles());
        }
        return this;
    }
}

// Usage in tests
@SpringBootTest
public class CustomUserAssertionsTest {

    @Autowired
    private UserService userService;

    @Test
    void testUserCreationWithCustomAssertions() {
        User user = userService.createUser("john@example.com", "John Doe");

        UserAssert.assertThat(user)
            .hasEmail("john@example.com")
            .hasName("John Doe")
            .isActive()
            .wasCreatedAfter(Instant.now().minusSeconds(10))
            .hasValidEmail()
            .hasRole("USER");
    }

    @Test
    void testUserValidationWithCustomAssertions() {
        User invalidUser = new User("", ""); // Invalid user

        assertThatThrownBy(() -> UserAssert.assertThat(invalidUser).hasValidEmail())
            .isInstanceOf(AssertionError.class)
            .hasMessageContaining("valid email");
    }
}
```

### BDD-style assertions

#### Custom BDD assertions
```java
public class ThenUser extends AbstractAssert<ThenUser, User> {

    public ThenUser(User actual) {
        super(actual, ThenUser.class);
    }

    public static ThenUser then(User actual) {
        return new ThenUser(actual);
    }

    public ThenUser shouldBeActive() {
        isNotNull();
        if (actual.getStatus() != UserStatus.ACTIVE) {
            failWithMessage("Expected user to be active but was <%s>", actual.getStatus());
        }
        return this;
    }

    public ThenUser shouldHaveEmail(String email) {
        isNotNull();
        if (!Objects.equals(actual.getEmail(), email)) {
            failWithMessage("Expected user email to be <%s> but was <%s>", email, actual.getEmail());
        }
        return this;
    }

    public ThenUser shouldHaveBeenCreatedRecently() {
        isNotNull();
        Instant oneHourAgo = Instant.now().minus(1, ChronoUnit.HOURS);
        if (actual.getCreatedAt().isBefore(oneHourAgo)) {
            failWithMessage("Expected user to be created recently but was <%s>", actual.getCreatedAt());
        }
        return this;
    }

    public ThenUser shouldBeValid() {
        return shouldHaveValidEmail()
            .shouldHaveValidName()
            .shouldBeActive();
    }

    private ThenUser shouldHaveValidEmail() {
        isNotNull();
        String email = actual.getEmail();
        if (email == null || !email.contains("@") || email.length() < 5) {
            failWithMessage("Expected user to have valid email but was <%s>", email);
        }
        return this;
    }

    private ThenUser shouldHaveValidName() {
        isNotNull();
        String name = actual.getName();
        if (name == null || name.trim().isEmpty()) {
            failWithMessage("Expected user to have valid name but was <%s>", name);
        }
        return this;
    }
}

// BDD-style test
@SpringBootTest
public class BDDUserTest {

    @Autowired
    private UserService userService;

    @Test
    void testUserCreation() {
        when(userRepository.save(any(User.class))).thenReturn(new User("test@example.com", "Test User"));

        User createdUser = userService.createUser("test@example.com", "Test User");

        then(createdUser).shouldBeValid()
            .shouldHaveEmail("test@example.com")
            .shouldHaveBeenCreatedRecently();
    }

    @Test
    void testUserActivation() {
        User user = userService.createUser("active@example.com", "Active User");

        userService.activateUser(user.getId());

        User activatedUser = userService.findById(user.getId());

        then(activatedUser).shouldBeActive();
    }
}
```

## Conditional assertions

### Basic conditional assertions

#### Assertions based on conditions
```java
@SpringBootTest
public class ConditionalAssertionsTest {

    @Autowired
    private FeatureService featureService;

    @Autowired
    private UserService userService;

    @Test
    void testFeatureDependentBehavior() {
        User user = userService.createUser("test@example.com", "Test User");

        // Conditional assertions based on feature flags
        if (featureService.isFeatureEnabled("advanced-profiling")) {
            assertThat(user.getProfile()).isNotNull();
            assertThat(user.getProfile().getPreferences()).isNotEmpty();
        } else {
            assertThat(user.getProfile()).isNull();
        }

        // Using AssertJ's satisfies
        assertThat(user).satisfies(u -> {
            if (featureService.isBetaUser(u)) {
                assertThat(u.hasAccessToBetaFeatures()).isTrue();
            }
        });
    }

    @Test
    void testEnvironmentSpecificAssertions() {
        String environment = System.getProperty("environment", "test");

        SystemMetrics metrics = systemService.getMetrics();

        // Different assertions based on environment
        switch (environment) {
            case "production":
                assertThat(metrics.getCpuUsage()).isLessThan(80.0);
                assertThat(metrics.getMemoryUsage()).isLessThan(90.0);
                assertThat(metrics.getActiveConnections()).isGreaterThan(10);
                break;

            case "staging":
                assertThat(metrics.getCpuUsage()).isLessThan(60.0);
                assertThat(metrics.getMemoryUsage()).isLessThan(70.0);
                break;

            default: // test
                assertThat(metrics.getResponseTime()).isLessThan(100.0);
                break;
        }
    }

    @Test
    void testConditionalValidation() {
        Order order = orderService.createOrder(List.of(
            new OrderItem("premium", BigDecimal.valueOf(500.0), 1)
        ));

        // Conditional assertions based on order properties
        assertThat(order).satisfies(o -> {
            if (o.getTotalAmount().compareTo(BigDecimal.valueOf(100.0)) > 0) {
                assertThat(o.getPriority()).isEqualTo(OrderPriority.HIGH);
                assertThat(o.requiresApproval()).isTrue();
            } else {
                assertThat(o.getPriority()).isEqualTo(OrderPriority.NORMAL);
                assertThat(o.requiresApproval()).isFalse();
            }
        });
    }
}
```

### Advanced conditional assertions

#### Using predicates and consumers
```java
@SpringBootTest
public class AdvancedConditionalAssertionsTest {

    @Autowired
    private UserService userService;

    @Autowired
    private OrderService orderService;

    @Test
    void testConditionalAssertionsWithPredicates() {
        List<User> users = userService.getAllUsers();

        // Assert based on predicates
        assertThat(users).allMatch(user -> {
            if (user.getStatus() == UserStatus.ACTIVE) {
                return user.getLastLogin() != null;
            } else {
                return user.getDeactivatedAt() != null;
            }
        });

        // Using AssertJ's filteredOn
        assertThat(users).filteredOn(user -> user.getType() == UserType.PREMIUM)
            .allMatch(user -> user.getSubscriptionEnd() != null);

        assertThat(users).filteredOn(user -> user.getType() == UserType.FREE)
            .allMatch(user -> user.getSubscriptionEnd() == null);
    }

    @Test
    void testComplexConditionalLogic() {
        Order order = createComplexOrder();

        // Complex conditional assertions
        assertThat(order).satisfies(o -> {
            BigDecimal total = o.getTotalAmount();

            if (total.compareTo(BigDecimal.valueOf(1000.0)) > 0) {
                // High-value order checks
                assertThat(o.getPriority()).isEqualTo(OrderPriority.HIGH);
                assertThat(o.requiresApproval()).isTrue();
                assertThat(o.getReviewDeadline()).isBefore(o.getCreatedAt().plus(1, ChronoUnit.DAYS));

                // Customer verification
                assertThat(o.getCustomer().isVerified()).isTrue();
                assertThat(o.getCustomer().getCreditScore()).isGreaterThan(700);

            } else if (total.compareTo(BigDecimal.valueOf(100.0)) > 0) {
                // Medium-value order checks
                assertThat(o.getPriority()).isEqualTo(OrderPriority.NORMAL);
                assertThat(o.requiresApproval()).isFalse();
                assertThat(o.getCustomer().isVerified()).isTrue();

            } else {
                // Low-value order checks
                assertThat(o.getPriority()).isEqualTo(OrderPriority.LOW);
                assertThat(o.requiresApproval()).isFalse();
            }
        });
    }

    @Test
    void testDynamicAssertions() {
        TestScenario scenario = loadTestScenario();

        // Dynamic assertions based on scenario configuration
        assertThat(scenario).satisfies(s -> {
            switch (s.getType()) {
                case PERFORMANCE:
                    runPerformanceAssertions(s);
                    break;
                case SECURITY:
                    runSecurityAssertions(s);
                    break;
                case FUNCTIONAL:
                    runFunctionalAssertions(s);
                    break;
                default:
                    fail("Unknown scenario type: " + s.getType());
            }
        });
    }

    private void runPerformanceAssertions(TestScenario scenario) {
        PerformanceMetrics metrics = performanceService.getMetrics();

        assertThat(metrics.getResponseTime()).isLessThan(scenario.getMaxResponseTime());
        assertThat(metrics.getThroughput()).isGreaterThan(scenario.getMinThroughput());
        assertThat(metrics.getErrorRate()).isLessThan(scenario.getMaxErrorRate());
    }

    private void runSecurityAssertions(TestScenario scenario) {
        SecurityAudit audit = securityService.audit();

        assertThat(audit.getFailedLogins()).isLessThan(scenario.getMaxFailedLogins());
        assertThat(audit.getSuspiciousActivities()).isEmpty();
        assertThat(audit.getSecurityScore()).isGreaterThan(scenario.getMinSecurityScore());
    }

    private void runFunctionalAssertions(TestScenario scenario) {
        FunctionalTestResult result = functionalTestService.runTests(scenario);

        assertThat(result.getPassedTests()).isEqualTo(result.getTotalTests());
        assertThat(result.getFailedTests()).isZero();
        assertThat(result.getExecutionTime()).isLessThan(scenario.getMaxExecutionTime());
    }
}
```

## Spring Boot integration

### Spring assertions

#### Spring MVC test assertions
```java
@SpringBootTest
@AutoConfigureMockMvc
public class SpringMvcAssertionsTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void testUserApiWithSpringAssertions() throws Exception {
        User user = new User("api@example.com", "API User");

        mockMvc.perform(post("/api/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(user)))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.email").value("api@example.com"))
            .andExpect(jsonPath("$.name").value("API User"))
            .andExpect(jsonPath("$.status").value("ACTIVE"));

        // Additional assertions
        mockMvc.perform(get("/api/users"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$[0].email").exists());
    }

    @Test
    void testErrorHandlingWithSpringAssertions() throws Exception {
        mockMvc.perform(post("/api/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{}")) // Invalid request
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.error").value("VALIDATION_ERROR"))
            .andExpect(jsonPath("$.message").value("Email is required"));
    }

    @Test
    void testSpringIntegrationWithAssertJ() throws Exception {
        MvcResult result = mockMvc.perform(get("/api/users/1"))
            .andExpect(status().isOk())
            .andReturn();

        User responseUser = objectMapper.readValue(
            result.getResponse().getContentAsString(), User.class);

        // Use AssertJ for detailed object assertions
        assertThat(responseUser)
            .isNotNull()
            .hasFieldOrPropertyWithValue("email", "user1@example.com")
            .hasFieldOrPropertyWithValue("name", "User 1")
            .hasNoNullFieldsOrPropertiesExcept("phoneNumber");
    }
}
```

### Spring context assertions

#### Bean and configuration assertions
```java
@SpringBootTest
public class SpringContextAssertionsTest {

    @Autowired
    private ApplicationContext context;

    @Autowired
    private UserService userService;

    @Test
    void testSpringContextWithAssertJ() {
        // Assert bean existence
        assertThat(context.containsBean("userService")).isTrue();
        assertThat(context.containsBean("userRepository")).isTrue();

        // Assert bean types
        assertThat(context.getBean("userService")).isInstanceOf(UserService.class);
        assertThat(context.getBean("userRepository")).isInstanceOf(UserRepository.class);

        // Assert bean properties
        UserService service = context.getBean(UserService.class);
        assertThat(service).isNotNull();

        // Test @Autowired fields
        assertThat(userService).isNotNull();
    }

    @Test
    void testConfigurationProperties() {
        // Assert configuration properties
        assertThat(context.getEnvironment().getProperty("spring.application.name"))
            .isEqualTo("my-application");

        assertThat(context.getEnvironment().getProperty("server.port"))
            .isEqualTo("8080");

        // Assert profile-specific properties
        String[] activeProfiles = context.getEnvironment().getActiveProfiles();
        assertThat(activeProfiles).contains("test");

        if (Arrays.asList(activeProfiles).contains("production")) {
            assertThat(context.getEnvironment().getProperty("logging.level.root"))
                .isEqualTo("INFO");
        }
    }

    @Test
    void testBeanDependencies() {
        // Assert bean dependencies
        UserService userService = context.getBean(UserService.class);

        // Use reflection to check injected dependencies
        assertThat(userService).hasFieldOrProperty("userRepository");
        assertThat(userService).hasFieldOrProperty("emailService");

        // More detailed dependency checks
        assertThat(context.getBeanDefinition("userService").getDependsOn())
            .isNotNull();
    }
}
```

## JSON assertions

### JSON path assertions

#### JSON structure validation
```java
@SpringBootTest
@AutoConfigureMockMvc
public class JsonAssertionsTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void testJsonResponseStructure() throws Exception {
        mockMvc.perform(get("/api/users/1"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.id").isNumber())
            .andExpect(jsonPath("$.email").isString())
            .andExpect(jsonPath("$.name").isString())
            .andExpect(jsonPath("$.status").value("ACTIVE"))
            .andExpect(jsonPath("$.createdAt").exists())
            .andDo(result -> {
                String json = result.getResponse().getContentAsString();

                // Additional AssertJ JSON assertions
                assertThatJson(json)
                    .isObject()
                    .containsKey("id")
                    .containsKey("email")
                    .containsKey("name")
                    .doesNotContainKey("password"); // Security check
            });
    }

    @Test
    void testJsonArrayResponse() throws Exception {
        mockMvc.perform(get("/api/users"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$[0].id").exists())
            .andExpect(jsonPath("$[0].email").isString())
            .andDo(result -> {
                String json = result.getResponse().getContentAsString();

                assertThatJson(json)
                    .isArray()
                    .hasSizeGreaterThan(0)
                    .eachElement()
                        .isObject()
                        .containsKey("id")
                        .containsKey("email")
                        .containsKey("status");
            });
    }

    @Test
    void testComplexJsonStructure() throws Exception {
        mockMvc.perform(get("/api/orders/1"))
            .andExpect(status().isOk())
            .andDo(result -> {
                String json = result.getResponse().getContentAsString();

                assertThatJson(json)
                    .isObject()
                    .containsKey("id")
                    .containsKey("customer")
                    .containsKey("items")
                    .hasEntrySatisfying("customer",
                        customer -> assertThatJson(customer)
                            .isObject()
                            .containsKey("id")
                            .containsKey("email"))
                    .hasEntrySatisfying("items",
                        items -> assertThatJson(items)
                            .isArray()
                            .isNotEmpty()
                            .eachElement()
                                .isObject()
                                .containsKey("productName")
                                .containsKey("price")
                                .containsKey("quantity"));
            });
    }
}
```

## File assertions

### File content assertions

#### Text file assertions
```java
@SpringBootTest
public class FileAssertionsTest {

    @Autowired
    private ReportService reportService;

    @Autowired
    private ExportService exportService;

    @Test
    void testCsvExport() {
        File csvFile = exportService.exportUsersToCsv();

        // File existence and basic properties
        assertThat(csvFile).exists();
        assertThat(csvFile).isFile();
        assertThat(csvFile).hasExtension("csv");

        // Content assertions
        assertThat(contentOf(csvFile))
            .startsWith("id,email,name,status")
            .contains("john@example.com")
            .contains("ACTIVE")
            .doesNotContain("password"); // Security check

        // Line count assertions
        assertThat(contentOf(csvFile).split("\n")).hasSizeGreaterThan(1);
    }

    @Test
    void testJsonExport() {
        File jsonFile = exportService.exportUsersToJson();

        assertThat(jsonFile).exists();
        assertThat(jsonFile).hasExtension("json");

        // JSON content assertions
        assertThatJson(contentOf(jsonFile))
            .isArray()
            .isNotEmpty()
            .eachElement()
                .isObject()
                .containsKey("id")
                .containsKey("email")
                .containsKey("name");

        // Validate JSON structure
        List<String> lines = Arrays.asList(contentOf(jsonFile).split("\n"));
        assertThat(lines).anyMatch(line -> line.contains("john@example.com"));
    }

    @Test
    void testReportGeneration() {
        File reportFile = reportService.generateUserReport();

        assertThat(reportFile).exists();
        assertThat(reportFile).isFile();
        assertThat(reportFile).hasName("user_report.txt");

        String content = contentOf(reportFile);

        // Report structure assertions
        assertThat(content)
            .startsWith("User Report")
            .contains("Total Users:")
            .contains("Active Users:")
            .contains("Inactive Users:")
            .endsWith("Report End");

        // Content validation
        assertThat(content).matches("(?s).*Total Users: \\d+.*");
        assertThat(content).matches("(?s).*Active Users: \\d+.*");
        assertThat(content).matches("(?s).*Inactive Users: \\d+.*");

        // File size assertions
        assertThat(reportFile).hasSizeGreaterThan(100); // At least 100 bytes
    }

    @Test
    void testFilePermissions() {
        File sensitiveFile = exportService.exportSensitiveData();

        assertThat(sensitiveFile).exists();

        // Permission assertions (Unix-like systems)
        assertThat(sensitiveFile).hasPermissions(OWNER_READ, OWNER_WRITE);
        assertThat(sensitiveFile).doesNotHavePermissions(GROUP_READ, OTHERS_READ, OTHERS_WRITE);

        // Content security assertions
        String content = contentOf(sensitiveFile);
        assertThat(content).doesNotContain("password");
        assertThat(content).doesNotContain("secret");
    }
}
```

## Best practices

### 1. Choosing the right assertion style

#### Fluent vs static imports
```java
// Recommended: Static imports for cleaner code
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
public class AssertionStyleTest {

    @Autowired
    private UserService userService;

    @Test
    void testUserCreation_fluentStyle() {
        User user = userService.createUser("fluent@example.com", "Fluent User");

        // Fluent style - recommended
        assertThat(user)
            .isNotNull()
            .hasFieldOrPropertyWithValue("email", "fluent@example.com")
            .hasFieldOrPropertyWithValue("name", "Fluent User")
            .hasFieldOrPropertyWithValue("status", UserStatus.ACTIVE);
    }

    @Test
    void testExceptionHandling_fluentStyle() {
        // Fluent exception assertions
        assertThatThrownBy(() -> userService.createUser("", "Test"))
            .isInstanceOf(ValidationException.class)
            .hasMessage("Email is required")
            .hasNoCause();
    }

    @Test
    void testCollectionAssertions_fluentStyle() {
        List<User> users = userService.getAllUsers();

        assertThat(users)
            .isNotNull()
            .isNotEmpty()
            .hasSizeGreaterThan(0)
            .allMatch(user -> user.getEmail() != null)
            .anyMatch(user -> user.getStatus() == UserStatus.ACTIVE);
    }
}
```

### 2. Writing descriptive assertions

#### Self-documenting assertions
```java
@SpringBootTest
public class DescriptiveAssertionsTest {

    @Autowired
    private OrderService orderService;

    @Test
    void testOrderProcessing_completeWorkflow() {
        // Given: A valid order with items
        Order order = createValidOrder();

        // When: Processing the order
        Order processedOrder = orderService.processOrder(order);

        // Then: All aspects of the order should be correctly processed
        assertThat(processedOrder)
            .describedAs("processed order should have valid id")
            .isNotNull()
            .extracting(Order::getId)
            .isNotNull();

        assertThat(processedOrder.getStatus())
            .describedAs("order status should be COMPLETED after processing")
            .isEqualTo(OrderStatus.COMPLETED);

        assertThat(processedOrder.getTotalAmount())
            .describedAs("order total should match item prices")
            .isEqualByComparingTo(calculateExpectedTotal(order.getItems()));

        assertThat(processedOrder.getProcessedAt())
            .describedAs("order should have processing timestamp")
            .isNotNull()
            .isAfter(order.getCreatedAt());

        // And: Customer should be notified
        assertThat(orderService.wasCustomerNotified(processedOrder.getId()))
            .describedAs("customer should receive notification for completed order")
            .isTrue();
    }

    @Test
    void testPaymentValidation_businessRules() {
        // Given: Different payment scenarios
        List<PaymentScenario> scenarios = List.of(
            new PaymentScenario(BigDecimal.valueOf(50.0), true, "Valid small payment"),
            new PaymentScenario(BigDecimal.valueOf(150.0), false, "Payment exceeds limit"),
            new PaymentScenario(BigDecimal.ZERO, false, "Zero amount payment")
        );

        // When & Then: Each scenario should behave according to business rules
        scenarios.forEach(scenario -> {
            PaymentResult result = paymentService.validatePayment(scenario.amount);

            assertThat(result.isValid())
                .describedAs("%s: payment validation result", scenario.description)
                .isEqualTo(scenario.expectedValid);
        });
    }

    @Test
    void testConcurrentAccess_dataConsistency() {
        // Given: Shared resource
        ConcurrentResource resource = new ConcurrentResource();

        // When: Multiple threads access concurrently
        ExecutorService executor = Executors.newFixedThreadPool(10);
        List<CompletableFuture<Void>> futures = IntStream.range(0, 100)
            .mapToObj(i -> CompletableFuture.runAsync(() -> resource.increment(), executor))
            .collect(Collectors.toList());

        // Wait for completion
        CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).join();

        // Then: Data should remain consistent
        assertThat(resource.getCounter())
            .describedAs("counter should be exactly 100 after 100 increments")
            .isEqualTo(100);

        executor.shutdown();
    }

    private Order createValidOrder() {
        // Implementation
        return new Order();
    }

    private BigDecimal calculateExpectedTotal(List<OrderItem> items) {
        return items.stream()
            .map(item -> item.getPrice().multiply(BigDecimal.valueOf(item.getQuantity())))
            .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    static class PaymentScenario {
        final BigDecimal amount;
        final boolean expectedValid;
        final String description;

        PaymentScenario(BigDecimal amount, boolean expectedValid, String description) {
            this.amount = amount;
            this.expectedValid = expectedValid;
            this.description = description;
        }
    }
}
```

### 3. Performance considerations

#### Efficient assertions
```java
@SpringBootTest
public class PerformanceAssertionsTest {

    @Autowired
    private PerformanceService performanceService;

    @Test
    void testResponseTime_performanceRequirements() {
        // Given: Performance requirements
        Duration maxResponseTime = Duration.ofMillis(100);
        int requiredThroughput = 100; // requests per second

        // When: Measuring actual performance
        PerformanceMetrics metrics = performanceService.measureResponseTime();

        // Then: Assert performance requirements
        assertThat(metrics.getAverageResponseTime())
            .describedAs("average response time should be under %d ms", maxResponseTime.toMillis())
            .isLessThan(maxResponseTime);

        assertThat(metrics.get95thPercentile())
            .describedAs("95th percentile should be under %d ms", maxResponseTime.toMillis() * 2)
            .isLessThan(maxResponseTime.multipliedBy(2));

        assertThat(metrics.getThroughput())
            .describedAs("throughput should be at least %d req/sec", requiredThroughput)
            .isGreaterThanOrEqualTo(requiredThroughput);

        // And: Error rate should be acceptable
        assertThat(metrics.getErrorRate())
            .describedAs("error rate should be under 1%")
            .isLessThan(0.01);
    }

    @Test
    void testMemoryUsage_efficiencyChecks() {
        // Given: Memory limits
        long maxHeapUsage = 512 * 1024 * 1024; // 512MB
        double maxGcTimePercentage = 5.0; // 5%

        // When: Measuring memory usage
        MemoryMetrics metrics = performanceService.measureMemoryUsage();

        // Then: Assert memory efficiency
        assertThat(metrics.getHeapUsage())
            .describedAs("heap usage should be under %d MB", maxHeapUsage / (1024 * 1024))
            .isLessThan(maxHeapUsage);

        assertThat(metrics.getGcTimePercentage())
            .describedAs("GC time should be under %.1f%% of total time", maxGcTimePercentage)
            .isLessThan(maxGcTimePercentage);

        // And: No memory leaks detected
        assertThat(metrics.getGrowingObjects())
            .describedAs("no significant memory leaks should be detected")
            .isEmpty();
    }

    @Test
    void testScalability_loadTesting() {
        // Given: Load testing parameters
        int concurrentUsers = 100;
        Duration testDuration = Duration.ofMinutes(5);
        Duration maxResponseTimeUnderLoad = Duration.ofSeconds(2);

        // When: Running load test
        LoadTestResults results = performanceService.runLoadTest(concurrentUsers, testDuration);

        // Then: Assert scalability
        assertThat(results.getTotalRequests())
            .describedAs("should handle significant load")
            .isGreaterThan(10000);

        assertThat(results.getAverageResponseTime())
            .describedAs("response time under load should be acceptable")
            .isLessThan(maxResponseTimeUnderLoad);

        assertThat(results.getErrorRate())
            .describedAs("error rate under load should be minimal")
            .isLessThan(0.05);

        // And: System remained stable
        assertThat(results.getMemoryUsageTrend())
            .describedAs("memory usage should not grow significantly")
            .allMatch(usage -> usage < 0.8); // Under 80% of max heap
    }
}
```

### 4. Test organization with assertions

#### Test categories and tagging
```java
@SpringBootTest
@Tag("integration")
public class IntegrationTestAssertions {

    @Autowired
    private FullSystemService fullSystemService;

    @Test
    @DisplayName("Complete user registration workflow")
    void testCompleteUserRegistrationWorkflow() {
        // Test the complete workflow from user registration to activation
        assertThat(fullSystemService.registerUser("workflow@example.com", "Workflow User"))
            .describedAs("user registration should succeed")
            .isNotNull()
            .satisfies(user -> {
                assertThat(user.getStatus()).isEqualTo(UserStatus.PENDING);
                assertThat(user.getActivationToken()).isNotNull();
            });

        // Verify email was sent
        assertThat(emailService.wasEmailSent("workflow@example.com", "Welcome"))
            .describedAs("welcome email should be sent")
            .isTrue();

        // Complete activation
        User activatedUser = fullSystemService.activateUser("activation-token");
        assertThat(activatedUser.getStatus())
            .describedAs("user should be activated")
            .isEqualTo(UserStatus.ACTIVE);
    }
}

@SpringBootTest
@Tag("performance")
public class PerformanceTestAssertions {

    @Autowired
    private PerformanceTestService performanceTestService;

    @Test
    @DisplayName("Database query performance under load")
    void testDatabaseQueryPerformance() {
        // Setup test data
        performanceTestService.setupLargeDataset();

        // Measure performance
        PerformanceResult result = performanceTestService.measureQueryPerformance();

        // Assert performance requirements
        assertThat(result.getAverageQueryTime())
            .describedAs("average query time should be under 100ms")
            .isLessThan(Duration.ofMillis(100));

        assertThat(result.getQueriesPerSecond())
            .describedAs("should handle at least 100 queries per second")
            .isGreaterThan(100);

        assertThat(result.getSlowQueries())
            .describedAs("slow queries should be minimal")
            .hasSizeLessThan(5);
    }
}

@SpringBootTest
@Tag("security")
public class SecurityTestAssertions {

    @Autowired
    private SecurityTestService securityTestService;

    @Test
    @DisplayName("SQL injection prevention")
    void testSqlInjectionPrevention() {
        List<String> maliciousInputs = List.of(
            "'; DROP TABLE users; --",
            "' OR '1'='1",
            "<script>alert('xss')</script>",
            "../../../etc/passwd"
        );

        maliciousInputs.forEach(input -> {
            assertThatThrownBy(() -> securityTestService.processUserInput(input))
                .describedAs("malicious input '%s' should be rejected", input)
                .isInstanceOf(SecurityException.class)
                .hasMessageContaining("potentially dangerous");
        });
    }

    @Test
    @DisplayName("Authorization checks")
    void testAuthorizationEnforcement() {
        // Test different user roles and their permissions
        Map<UserRole, List<String>> rolePermissions = Map.of(
            UserRole.ADMIN, List.of("READ", "WRITE", "DELETE", "ADMIN"),
            UserRole.USER, List.of("READ", "WRITE"),
            UserRole.GUEST, List.of("READ")
        );

        rolePermissions.forEach((role, expectedPermissions) -> {
            List<String> actualPermissions = securityTestService.getPermissionsForRole(role);

            assertThat(actualPermissions)
                .describedAs("role %s should have correct permissions", role)
                .containsExactlyInAnyOrderElementsOf(expectedPermissions);
        });
    }
}
```

## Troubleshooting

### Распространенные проблемы

#### ClassNotFoundException for AssertJ

**Symptoms:**
- Tests fail with ClassNotFoundException for AssertJ classes

**Solutions:**
```xml
<!-- Ensure correct dependency -->
<dependency>
    <groupId>org.assertj</groupId>
    <artifactId>assertj-core</artifactId>
    <version>3.24.2</version>
    <scope>test</scope>
</dependency>

<!-- For Spring Boot, it's usually included -->
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-test</artifactId>
    <scope>test</scope>
</dependency>
```

#### Static import issues

**Symptoms:**
- IDE shows errors for static imports of AssertJ methods

**Solutions:**
```java
// Use explicit imports instead of static
import org.assertj.core.api.Assertions;

// Then use:
Assertions.assertThat(user).isNotNull();

// Or configure IDE to recognize AssertJ static imports
// IntelliJ IDEA: Settings -> Editor -> General -> Auto Import -> Java
// Add: org.assertj.core.api.Assertions.*
```

#### Soft assertions not failing

**Symptoms:**
- Soft assertions pass even when they should fail

**Solutions:**
```java
@Test
void testSoftAssertions() {
    SoftAssertions softly = new SoftAssertions();

    softly.assertThat(1).isEqualTo(2); // This should fail
    softly.assertThat("hello").isEqualTo("world"); // This should also fail

    // Don't forget to call assertAll()!
    softly.assertAll(); // This will fail the test
}
```

#### Custom assertions not working

**Symptoms:**
- Custom assertion methods don't appear in IDE autocomplete

**Solutions:**
```java
// Ensure custom assertion extends AbstractAssert
public class UserAssert extends AbstractAssert<UserAssert, User> {

    // Constructor should call super
    public UserAssert(User actual) {
        super(actual, UserAssert.class);
    }

    // Static factory method
    public static UserAssert assertThat(User actual) {
        return new UserAssert(actual);
    }

    // Fluent methods should return this
    public UserAssert hasEmail(String email) {
        // Implementation
        return this;
    }
}
```

### Debug techniques

#### Verbose assertion output
```java
@SpringBootTest
public class DebugAssertionsTest {

    @Autowired
    private UserService userService;

    @Test
    void testWithVerboseOutput() {
        User user = userService.createUser("debug@example.com", "Debug User");

        // Use describedAs for better error messages
        assertThat(user)
            .describedAs("checking user creation result")
            .isNotNull();

        assertThat(user.getEmail())
            .describedAs("verifying user email is correct")
            .isEqualTo("debug@example.com");

        // For complex objects, use satisfies with detailed checks
        assertThat(user).satisfies(u -> {
            System.out.println("Debug: User ID = " + u.getId());
            System.out.println("Debug: User status = " + u.getStatus());
            System.out.println("Debug: Created at = " + u.getCreatedAt());

            assertThat(u.getId()).isNotNull();
            assertThat(u.getStatus()).isEqualTo(UserStatus.ACTIVE);
            assertThat(u.getCreatedAt()).isBefore(Instant.now());
        });
    }

    @Test
    void testCollectionDebug() {
        List<User> users = userService.getAllUsers();

        System.out.println("Debug: Found " + users.size() + " users");

        // Debug each element
        assertThat(users).allSatisfy(user -> {
            System.out.println("Debug: Checking user " + user.getEmail());
            assertThat(user.getEmail()).isNotNull();
            assertThat(user.getName()).isNotBlank();
        });
    }

    @Test
    void testExceptionDebug() {
        try {
            userService.createUser("", "Test");
            fail("Expected ValidationException");
        } catch (ValidationException e) {
            System.out.println("Debug: Caught expected exception: " + e.getMessage());

            // Verify exception details
            assertThat(e.getMessage()).contains("Email");
            assertThat(e.getField()).isEqualTo("email");
        }
    }
}
```

#### Configuration validation
```java
@Configuration
public class AssertJConfigurationValidator implements InitializingBean {

    @Override
    public void afterPropertiesSet() {
        System.out.println("=== AssertJ Configuration Validation ===");

        // Check AssertJ version
        try {
            String version = Assertions.class.getPackage().getImplementationVersion();
            System.out.println("✓ AssertJ version: " + version);

            // Verify basic functionality
            String test = "test";
            assertThat(test).isEqualTo("test");
            System.out.println("✓ Basic assertions working");

            // Test soft assertions
            SoftAssertions softly = new SoftAssertions();
            softly.assertThat(1).isEqualTo(1);
            softly.assertAll();
            System.out.println("✓ Soft assertions working");

        } catch (Exception e) {
            System.err.println("✗ AssertJ configuration issue: " + e.getMessage());
        }
    }
}
```

## Заключение

**AssertJ** — это мощная и гибкая библиотека для написания читаемых и выразительных assertions в Java тестах. Благодаря fluent API, обширному набору встроенных проверок и возможности создания custom assertions, AssertJ значительно улучшает качество и поддерживаемость тестового кода.

### Ключевые возможности:

1. **Fluent API** — читаемый и текучий синтаксис для всех типов assertions
2. **Rich type support** — специализированные assertions для Collections, Maps, Optional, Exceptions
3. **Soft assertions** — группировка проверок с отложенными ошибками
4. **Custom assertions** — создание domain-specific assertion классов
5. **Spring integration** — поддержка Spring Boot и Spring MVC testing
6. **JSON assertions** — встроенные проверки JSON структур
7. **Conditional assertions** — проверки основанные на условиях

### Архитектурные преимущества:

#### Expressiveness:
- **Self-documenting** — assertions читаются как спецификации
- **Type-safe** — проверка типов на этапе компиляции
- **Extensible** — возможность добавления custom assertions
- **Composable** — комбинация различных типов проверок

#### Test Quality:
- **Descriptive failures** — понятные сообщения об ошибках
- **Rich diagnostics** — детальная информация при падениях
- **Flexible verification** — различные стратегии проверки
- **IDE support** — отличная поддержка в IDE

### Когда использовать AssertJ:

✅ **Unit testing** — expressive assertions для unit тестов
✅ **Integration testing** — проверки сложных объектов и структур
✅ **API testing** — JSON response validation
✅ **Spring Boot testing** — интеграция с Spring ecosystem
✅ **Custom domains** — создание domain-specific assertions
✅ **Legacy code testing** — улучшение существующих тестов
✅ **TDD/BDD** — readable specifications
✅ **Complex validations** — nested object и collection checking

### Когда НЕ использовать:

❌ **Simple JUnit tests** — для базовых assertEquals/assertTrue
❌ **Performance-critical** — overhead от fluent API
❌ **Legacy environments** — ограничения по зависимостям
❌ **Minimal test suites** — избыточная сложность для простых случаев
❌ **Non-Java projects** — специфично для Java экосистемы

### Best practices:

1. **Fluent style** — использование static imports и fluent API
2. **Descriptive assertions** — self-documenting проверки с описаниями
3. **Custom assertions** — инкапсуляция complex проверок
4. **Soft assertions** — группировка связанных проверок
5. **Conditional logic** — проверки основанные на условиях
6. **Performance considerations** — баланс expressiveness vs speed
7. **Test organization** — категоризация тестов по типам assertions

### Типы Assertions по назначению:

#### Basic Types:
- **Objects** — null checks, field validation, recursive comparison
- **Primitives** — numeric comparisons, boolean logic
- **Strings** — content, patterns, case sensitivity
- **Collections** — size, contents, ordering, uniqueness

#### Advanced Types:
- **Optional** — presence, value extraction, chaining
- **Exceptions** — type, message, cause chain, custom properties
- **Files** — existence, content, permissions, size
- **JSON** — structure validation, path-based assertions

#### Specialized:
- **Spring MVC** — HTTP responses, JSON content, status codes
- **Spring Context** — bean validation, configuration properties
- **Performance** — timing, throughput, resource usage

AssertJ является стандартом для modern Java testing. Правильное использование его возможностей обеспечивает высокое качество тестов, легкость поддержки и отличную читаемость кода. 🚀

**Далее: Hamcrest (matcher-based assertions)**
