# Hamcrest для Java

Комплексное руководство по использованию Hamcrest matchers для создания гибких и читаемых assertions в Java тестах: matcher composition, custom matchers, integration с JUnit и Spring Boot.

**Дата последнего обновления:** 2026-01-21

## Полезные ссылки

### Официальная документация
- [Hamcrest Documentation](https://hamcrest.org/JavaHamcrest/) - Основная документация
- [Hamcrest Core](https://hamcrest.org/JavaHamcrest/javadoc/2.2/) - API документация
- [Hamcrest Tutorial](https://hamcrest.org/JavaHamcrest/tutorial/) - Руководство по использованию

### Java интеграции
- [JUnit 5 with Hamcrest](https://junit.org/junit5/docs/current/user-guide/#writing-tests-assertions) - JUnit 5 интеграция
- [Hamcrest with Spring](https://docs.spring.io/spring-framework/docs/current/reference/html/testing.html#spring-mvc-test-framework) - Spring testing
- [AssertJ vs Hamcrest](https://assertj.github.io/doc/#assertj-core-migrating-from-junit) - Сравнение с AssertJ

### Best practices
- [Hamcrest Best Practices](https://hamcrest.org/JavaHamcrest/best-practices/) - Лучшие практики
- [Custom Matchers](https://hamcrest.org/JavaHamcrest/custom-matchers/) - Создание кастомных matchers
- [Matcher Composition](https://hamcrest.org/JavaHamcrest/matcher-composition/) - Композиция matchers

### См. также
- `testing/junit-advanced.md` - JUnit расширения
- `testing/assertj.md` - AssertJ assertions
- `testing/mockito-advanced.md` - Mockito для mocking
- `spring-testing.md` - Spring testing

## Содержание

- [Введение в Hamcrest](#введение-в-hamcrest)
- [Core matchers](#core-matchers)
- [Number matchers](#number-matchers)
- [String matchers](#string-matchers)
- [Collection matchers](#collection-matchers)
- [Object matchers](#object-matchers)
- [Logical matchers](#logical-matchers)
- [Custom matchers](#custom-matchers)
- [Matcher composition](#matcher-composition)
- [Spring integration](#spring-integration)
- [Bean matchers](#bean-matchers)
- [XML/JSON matchers](#xmljson-matchers)
- [Best practices](#best-practices)
- [Troubleshooting](#troubleshooting)
- [Заключение](#заключение)

## Введение в Hamcrest

**Hamcrest** — это framework для создания matcher objects, которые можно комбинировать для создания гибких expression для assertions в тестах. Hamcrest предоставляет декларативный способ описания ожиданий.

### Почему Hamcrest?

Hamcrest предоставляет мощные возможности для создания assertions:

1. **Matcher composition** — комбинирование matchers с помощью логических операторов
2. **Custom matchers** — создание domain-specific matchers
3. **Readable assertions** — декларативный синтаксис
4. **Type-safe** — проверка типов на этапе компиляции
5. **Extensible** — возможность создания новых matchers
6. **Framework agnostic** — работает с любыми testing frameworks
7. **Rich diagnostics** — понятные сообщения при ошибках

### Maven зависимости

**Hamcrest** имеет модульную структуру, где core модуль предоставляет базовые matchers, а дополнительные модули расширяют функциональность для специфических областей применения.

#### Core Hamcrest (обязательный)

**hamcrest-core** — базовый модуль с фундаментальными matcher интерфейсами и core matchers.

```xml
<dependency>
    <groupId>org.hamcrest</groupId>
    <artifactId>hamcrest</artifactId>
    <version>2.2</version>
    <scope>test</scope>
</dependency>
```

**Что включает hamcrest-core:**
- **Matcher interface** — базовый интерфейс для всех matchers
- **Core matchers** — equalTo, not, anyOf, allOf, describedAs
- **Type-safe matching** — безопасная работа с типами
- **Descriptive failures** — понятные сообщения об ошибках
- **Matcher composition** — логические операторы для комбинирования

#### Hamcrest Library (рекомендуемый)

**hamcrest-library** — расширенная библиотека с дополнительными matchers для различных типов данных.

```xml
<dependency>
    <groupId>org.hamcrest</groupId>
    <artifactId>hamcrest-library</artifactId>
    <version>2.2</version>
    <scope>test</scope>
</dependency>
```

**Дополнительные возможности:**
- **Collection matchers** — hasSize, contains, everyItem
- **Number matchers** — greaterThan, lessThan, closeTo
- **String matchers** — containsString, startsWith, endsWith
- **Bean matchers** — hasProperty, samePropertyValuesAs
- **XML matchers** — hasXPath
- **Array matchers** — array, arrayContaining

#### Специализированные модули

**hamcrest-date** — matchers для работы с датами:

```xml
<dependency>
    <groupId>org.hamcrest</groupId>
    <artifactId>hamcrest-date</artifactId>
    <version>2.0.7</version>
    <scope>test</scope>
</dependency>
```

**Возможности для дат:**
- **Date matchers** — before, after, sameDay
- **Time matchers** — sameHour, sameMinute
- **Calendar matchers** — sameInstant

**hamcrest-json** — matchers для JSON структур:

```xml
<dependency>
    <groupId>org.hamcrest</groupId>
    <artifactId>hamcrest-json</artifactId>
    <version>0.2</version>
    <scope>test</scope>
</dependency>
```

**JSON matching features:**
- **JSON structure validation** — проверка структуры JSON
- **JSON path matching** — XPath-подобные запросы
- **JSON schema validation** — валидация по схеме

**hamcrest-optional** — matchers для Java 8 Optional:

```xml
<dependency>
    <groupId>com.github.npathai</groupId>
    <artifactId>hamcrest-optional</artifactId>
    <version>2.0.0</version>
    <scope>test</scope>
</dependency>
```

**Optional matchers:**
- **Presence check** — isPresent, isEmpty
- **Value matching** — hasValue, hasValueThat

#### JUnit интеграция

**JUnit 4** (legacy):
```xml
<dependency>
    <groupId>junit</groupId>
    <artifactId>junit</artifactId>
    <version>4.13.2</version>
    <scope>test</scope>
</dependency>
```

**JUnit 5** (рекомендуемый):
```xml
<dependency>
    <groupId>org.junit.jupiter</groupId>
    <artifactId>junit-jupiter</artifactId>
    <version>5.10.0</version>
    <scope>test</scope>
</dependency>
```

**JUnit 5 с Hamcrest:**
```java
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

// В JUnit 5 Hamcrest assertions работают через MatcherAssert.assertThat
assertThat("test", equalTo("test"));
```

#### TestNG интеграция

**TestNG с Hamcrest:**
```xml
<dependency>
    <groupId>org.testng</groupId>
    <artifactId>testng</artifactId>
    <version>7.8.0</version>
    <scope>test</scope>
</dependency>
```

```java
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

// TestNG assertions с Hamcrest
assertThat(result, equalTo(expected));
```

#### Gradle зависимости

**Для Gradle проектов с подробными конфигурациями:**

```gradle
dependencies {
    // Core Hamcrest - базовые matchers
    testImplementation 'org.hamcrest:hamcrest:2.2'

    // Extended library - дополнительные matchers
    testImplementation 'org.hamcrest:hamcrest-library:2.2'

    // Date matchers
    testImplementation 'org.hamcrest:hamcrest-date:2.0.7'

    // Optional matchers
    testImplementation 'com.github.npathai:hamcrest-optional:2.0.0'

    // JUnit 5
    testImplementation 'org.junit.jupiter:junit-jupiter:5.10.0'
    testRuntimeOnly 'org.junit.platform:junit-platform-launcher'
}
```

#### Version management

**Рекомендуется использовать properties для версий:**

```gradle
ext {
    hamcrestVersion = '2.2'
    junitVersion = '5.10.0'
}

dependencies {
    testImplementation "org.hamcrest:hamcrest:${hamcrestVersion}"
    testImplementation "org.hamcrest:hamcrest-library:${hamcrestVersion}"
    testImplementation "org.junit.jupiter:junit-jupiter:${junitVersion}"
}
```

#### Spring Boot интеграция

**Spring Boot Starter Test включает Hamcrest:**

```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-test</artifactId>
    <scope>test</scope>
    <!-- Hamcrest уже включен -->
</dependency>
```

**Что включает spring-boot-starter-test:**
- **JUnit 5** — основной testing framework
- **Hamcrest** — matcher library (уже включена!)
- **Mockito** — mocking framework
- **JSONassert** — JSON assertions
- **Spring Test** — Spring testing utilities

#### Миграция между версиями

**Hamcrest 1.x → 2.x:**

```xml
<!-- Старая версия (Hamcrest 1.x) -->
<dependency>
    <groupId>org.hamcrest</groupId>
    <artifactId>hamcrest-core</artifactId>
    <version>1.3</version>
</dependency>

<!-- Новая версия (Hamcrest 2.x) -->
<dependency>
    <groupId>org.hamcrest</groupId>
    <artifactId>hamcrest</artifactId>
    <version>2.2</version>
</dependency>
```

**Изменения в Hamcrest 2.x:**
- **Объединенные модули** — hamcrest-core и hamcrest-library объединены в hamcrest
- **Java 8+** — минимальная версия Java 8
- **Улучшенные generics** — лучше type safety
- **Новые matchers** — дополнительные matchers для коллекций и строк

#### IDE Configuration

**IntelliJ IDEA:**
1. **File → Settings → Build, Execution, Deployment → Build Tools → Gradle**
2. **Hamcrest matchers будут автоматически распознаны**
3. **Static imports для matchers работают из коробки**

**Eclipse:**
1. **Help → Eclipse Marketplace**
2. **Find**: "Hamcrest"
3. **Install**: Hamcrest Eclipse integration

**Static imports для удобства:**

```java
// Рекомендуется добавлять static imports
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

// Теперь можно писать:
assertThat(users, hasSize(5));
assertThat(user.getName(), equalTo("John"));
```

#### Troubleshooting зависимостей

**Проблема: ClassNotFoundException**

```bash
# Проверить classpath
mvn dependency:tree | grep hamcrest

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
            <groupId>org.hamcrest</groupId>
            <artifactId>hamcrest-core</artifactId>
        </exclusion>
    </exclusions>
</dependency>

<!-- Добавить нужную версию -->
<dependency>
    <groupId>org.hamcrest</groupId>
    <artifactId>hamcrest</artifactId>
    <version>2.2</version>
    <scope>test</scope>
</dependency>
```

**Проблема: NoSuchMethodError**

```java
// Проверить версию Hamcrest в runtime
System.out.println(org.hamcrest.Matchers.class.getPackage().getImplementationVersion());
```

**Проблема: Matcher не найден**

```java
// Импортировать правильный класс
import static org.hamcrest.Matchers.*;  // Для основных matchers
import static org.hamcrest.collection.IsCollectionWithSize.hasSize;  // Для специфических
import static org.hamcrest.collection.IsIterableContainingInAnyOrder.containsInAnyOrder;  // Для коллекций
```

### Basic usage

#### Простые assertions
```java
@SpringBootTest
public class HamcrestBasicTest {

    @Autowired
    private UserService userService;

    @Test
    void testUserCreation() {
        User user = userService.createUser("john@example.com", "John Doe");

        // Hamcrest assertions
        assertThat(user, is(notNullValue()));
        assertThat(user.getEmail(), equalTo("john@example.com"));
        assertThat(user.getName(), equalTo("John Doe"));
        assertThat(user.getStatus(), is(UserStatus.ACTIVE));
    }

    @Test
    void testUserList() {
        List<User> users = userService.getAllUsers();

        // Collection assertions
        assertThat(users, is(not(empty())));
        assertThat(users, hasSize(greaterThan(0)));
        assertThat(users, hasItem(hasProperty("email", equalTo("john@example.com"))));
    }
}
```

## Core matchers

### Equality and identity matchers

#### Basic equality
```java
@SpringBootTest
public class EqualityMatchersTest {

    @Autowired
    private ProductService productService;

    @Test
    void testProductEquality() {
        Product product = productService.createProduct("Laptop", BigDecimal.valueOf(999.99));

        // Equality matchers
        assertThat(product.getName(), equalTo("Laptop"));
        assertThat(product.getPrice(), comparesEqualTo(BigDecimal.valueOf(999.99)));

        // Identity vs equality
        Product sameProduct = productService.getProductById(product.getId());
        assertThat(sameProduct, sameInstance(product)); // Same object
        assertThat(sameProduct, equalTo(product)); // Equal content

        // Null checks
        assertThat(product.getDescription(), nullValue());
        assertThat(product.getCategory(), notNullValue());
    }

    @Test
    void testOrderProcessing() {
        Order order = orderService.createOrder(List.of(
            new OrderItem("book", BigDecimal.valueOf(29.99), 2)
        ));

        // Complex equality assertions
        assertThat(order.getTotalAmount(),
            comparesEqualTo(BigDecimal.valueOf(59.98)));

        assertThat(order.getStatus(), equalTo(OrderStatus.PENDING));

        // Multiple field checks
        assertThat(order.getId(), notNullValue());
        assertThat(order.getCreatedAt(), notNullValue());
        assertThat(order.getItems(), not(empty()));
    }

    @Test
    void testUserValidation() {
        User user = new User("valid@example.com", "Valid User");

        // Validation result assertions
        ValidationResult result = userService.validateUser(user);

        assertThat(result.isValid(), equalTo(true));
        assertThat(result.getErrors(), empty());
        assertThat(result.getWarnings(), empty());

        // Invalid user assertions
        User invalidUser = new User("", "");
        ValidationResult invalidResult = userService.validateUser(invalidUser);

        assertThat(invalidResult.isValid(), equalTo(false));
        assertThat(invalidResult.getErrors(), not(empty()));
        assertThat(invalidResult.getErrors(), hasSize(2)); // email and name errors
    }
}
```

### Null and type matchers

#### Type checking
```java
@SpringBootTest
public class TypeMatchersTest {

    @Autowired
    private PaymentService paymentService;

    @Test
    void testPaymentProcessing() {
        PaymentRequest request = new PaymentRequest(BigDecimal.valueOf(100.0), "card-123");

        PaymentResult result = paymentService.processPayment(request);

        // Type assertions
        assertThat(result, instanceOf(PaymentResult.class));
        assertThat(result.getTransactionId(), instanceOf(String.class));
        assertThat(result.getAmount(), instanceOf(BigDecimal.class));

        // Null/Non-null assertions
        assertThat(result.getTransactionId(), notNullValue());
        assertThat(result.getProcessingDate(), notNullValue());
        assertThat(result.getErrorMessage(), nullValue()); // Successful payment

        // Type-safe casting and assertions
        if (result instanceof SuccessfulPayment) {
            SuccessfulPayment success = (SuccessfulPayment) result;
            assertThat(success.getConfirmationCode(), notNullValue());
            assertThat(success.getConfirmationCode(), matchesPattern("CONF-[0-9]{6}"));
        }
    }

    @Test
    void testGenericTypeAssertions() {
        // Generic collections
        List<User> users = userService.getUsersByStatus(UserStatus.ACTIVE);
        Map<String, Product> productMap = productService.getProductCatalog();

        // Collection type assertions
        assertThat(users, instanceOf(List.class));
        assertThat(productMap, instanceOf(Map.class));

        // Element type assertions
        assertThat(users, everyItem(instanceOf(User.class)));
        assertThat(productMap.values(), everyItem(instanceOf(Product.class)));

        // Generic type parameters (runtime checks)
        assertThat(users.get(0), instanceOf(User.class));
        assertThat(productMap.keySet(), everyItem(instanceOf(String.class)));
    }

    @Test
    void testPolymorphicBehavior() {
        // Test polymorphic return types
        PaymentProcessor processor = paymentService.getPaymentProcessor("credit-card");

        assertThat(processor, instanceOf(CreditCardProcessor.class));
        assertThat(processor, not(instanceOf(PayPalProcessor.class)));

        // Interface implementation checks
        assertThat(processor, instanceOf(PaymentProcessor.class));
        assertThat(processor, instanceOf(Validatable.class));

        // Method return type assertions
        ValidationResult validation = ((Validatable) processor).validate();
        assertThat(validation, instanceOf(ValidationResult.class));
    }
}
```

## Number matchers

### Numeric comparisons

#### Basic numeric assertions
```java
@SpringBootTest
public class NumberMatchersTest {

    @Autowired
    private StatisticsService statisticsService;

    @Test
    void testOrderStatistics() {
        OrderStatistics stats = statisticsService.calculateOrderStatistics();

        // Basic numeric comparisons
        assertThat(stats.getTotalOrders(), greaterThan(0));
        assertThat(stats.getTotalRevenue(), greaterThanOrEqualTo(BigDecimal.ZERO));
        assertThat(stats.getAverageOrderValue(), greaterThan(BigDecimal.valueOf(10.0)));

        // Range assertions
        assertThat(stats.getMinOrderValue(), allOf(
            greaterThanOrEqualTo(BigDecimal.ZERO),
            lessThanOrEqualTo(stats.getMaxOrderValue())
        ));

        assertThat(stats.getOrderCount(), allOf(
            greaterThan(0),
            lessThanOrEqualTo(1000)
        ));
    }

    @Test
    void testPerformanceMetrics() {
        PerformanceMetrics metrics = performanceService.getMetrics();

        // Performance assertions
        assertThat(metrics.getResponseTime(), lessThan(1000L)); // Less than 1 second
        assertThat(metrics.getThroughput(), greaterThan(50)); // More than 50 req/sec
        assertThat(metrics.getErrorRate(), closeTo(0.0, 0.05)); // Within 5% of 0

        // Memory usage assertions
        assertThat(metrics.getMemoryUsage(), allOf(
            greaterThan(0L),
            lessThan(1024L * 1024L * 512L) // Less than 512MB
        ));

        // CPU usage assertions (percentage)
        assertThat(metrics.getCpuUsage(), allOf(
            greaterThanOrEqualTo(0.0),
            lessThanOrEqualTo(100.0)
        ));
    }

    @Test
    void testFinancialCalculations() {
        Invoice invoice = invoiceService.generateInvoice(order);

        // Financial precision assertions
        assertThat(invoice.getSubtotal(), comparesEqualTo(BigDecimal.valueOf(99.99)));
        assertThat(invoice.getTax(), comparesEqualTo(BigDecimal.valueOf(8.00)));
        assertThat(invoice.getTotal(), comparesEqualTo(BigDecimal.valueOf(107.99)));

        // Rounding assertions
        assertThat(invoice.getTotal(), closeTo(BigDecimal.valueOf(108.0), BigDecimal.valueOf(0.01)));

        // Percentage calculations
        assertThat(invoice.getTaxRate(), closeTo(BigDecimal.valueOf(0.08), BigDecimal.valueOf(0.001)));
    }
}
```

### Advanced numeric matchers

#### Statistical assertions
```java
@SpringBootTest
public class AdvancedNumberMatchersTest {

    @Autowired
    private AnalyticsService analyticsService;

    @Test
    void testSalesAnalytics() {
        SalesAnalytics analytics = analyticsService.getMonthlyAnalytics();

        // Statistical assertions
        assertThat(analytics.getTotalSales(), greaterThan(BigDecimal.valueOf(10000.0)));
        assertThat(analytics.getAverageOrderValue(), allOf(
            greaterThan(BigDecimal.valueOf(50.0)),
            lessThan(BigDecimal.valueOf(200.0))
        ));

        // Growth rate assertions
        assertThat(analytics.getGrowthRate(), allOf(
            greaterThan(-0.1), // Not declining too much
            lessThan(0.5)      // Not growing too fast
        ));

        // Percentile assertions
        assertThat(analytics.get95thPercentileOrderValue(),
            greaterThan(analytics.getMedianOrderValue()));

        assertThat(analytics.get99thPercentileOrderValue(),
            greaterThan(analytics.get95thPercentileOrderValue()));
    }

    @Test
    void testQualityMetrics() {
        QualityMetrics metrics = qualityService.getCodeQualityMetrics();

        // Code quality assertions
        assertThat(metrics.getTestCoverage(), greaterThan(0.8)); // > 80%
        assertThat(metrics.getCyclomaticComplexity(), lessThan(10.0));
        assertThat(metrics.getDuplicationPercentage(), lessThan(0.05)); // < 5%

        // Performance quality gates
        assertThat(metrics.getPerformanceScore(), allOf(
            greaterThanOrEqualTo(85),
            lessThanOrEqualTo(100)
        ));

        // Maintainability index
        assertThat(metrics.getMaintainabilityIndex(), greaterThan(70));
    }

    @Test
    void testFinancialRatios() {
        FinancialRatios ratios = financialService.calculateRatios();

        // Financial ratio assertions
        assertThat(ratios.getCurrentRatio(), greaterThan(1.0)); // Liquidity
        assertThat(ratios.getDebtToEquityRatio(), allOf(
            greaterThanOrEqualTo(0.0),
            lessThanOrEqualTo(2.0)
        ));

        assertThat(ratios.getReturnOnEquity(), allOf(
            greaterThan(0.05), // 5% minimum
            lessThan(0.3)      // 30% maximum
        ));

        // Margin calculations
        assertThat(ratios.getGrossMargin(), allOf(
            greaterThan(0.2), // 20% minimum
            lessThan(0.8)     // 80% maximum
        ));
    }
}
```

## String matchers

### Basic string assertions

#### String content and patterns
```java
@SpringBootTest
public class StringMatchersTest {

    @Autowired
    private EmailService emailService;

    @Test
    void testEmailContent() {
        String emailContent = emailService.generateWelcomeEmail("John", "john@example.com");

        // Basic string assertions
        assertThat(emailContent, containsString("Welcome"));
        assertThat(emailContent, containsString("John"));
        assertThat(emailContent, not(containsString("Error")));

        // Case sensitivity
        assertThat(emailContent, containsStringIgnoringCase("welcome"));
        assertThat(emailContent.toLowerCase(), not(containsString("error")));
    }

    @Test
    void testEmailValidation() {
        // Valid emails
        assertThat("user@example.com", matchesPattern(".+@.+\\..+"));
        assertThat("test.email+tag@gmail.com", containsString("@"));
        assertThat("user@subdomain.example.com", endsWith(".com"));

        // Invalid emails
        assertThat("", not(matchesPattern(".+@.+\\..+")));
        assertThat("invalid-email", not(containsString("@")));
        assertThat("@example.com", not(startsWith("a")));
    }

    @Test
    void testIdGeneration() {
        String userId = idGenerator.generateUserId();
        String orderId = idGenerator.generateOrderId();

        // Pattern matching for generated IDs
        assertThat(userId, matchesPattern("[a-f0-9]{8}-[a-f0-9]{4}-[a-f0-9]{4}-[a-f0-9]{4}-[a-f0-9]{12}"));
        assertThat(orderId, matchesPattern("ORD-[0-9]{6}-[A-Z]{3}"));

        // Structure assertions
        assertThat(userId, startsWith(userId.substring(0, 8)));
        assertThat(orderId, startsWith("ORD-"));
        assertThat(orderId, endsWith(orderId.substring(orderId.length() - 3)));
    }
}
```

### Advanced string matchers

#### Complex string validation
```java
@SpringBootTest
public class AdvancedStringMatchersTest {

    @Autowired
    private MessageFormatter messageFormatter;

    @Test
    void testMessageFormatting() {
        String message = messageFormatter.formatOrderConfirmation(
            "John", "ORD-123456-ABC", BigDecimal.valueOf(99.99));

        // Multi-line string assertions
        assertThat(message, stringContainsInOrder(
            Arrays.asList("Dear John", "ORD-123456-ABC", "$99.99", "Thank you")
        ));

        // Structure validation
        assertThat(message, startsWith("Dear"));
        assertThat(message, endsWith("Best regards"));
        assertThat(message, hasLength(greaterThan(100)));

        // Content validation with patterns
        assertThat(message, matchesPattern("(?s).*Dear \\w+.*"));
        assertThat(message, matchesPattern("(?s).*ORD-\\d{6}-[A-Z]{3}.*"));
        assertThat(message, matchesPattern("(?s).*\\$\\d+\\.\\d{2}.*"));
    }

    @Test
    void testHtmlContent() {
        String html = htmlGenerator.generateUserProfile("John", "john@example.com");

        // HTML structure assertions
        assertThat(html, startsWith("<!DOCTYPE html>"));
        assertThat(html, containsString("<title>"));
        assertThat(html, containsString("John"));
        assertThat(html, containsString("john@example.com"));
        assertThat(html, not(containsString("<script>")));

        // Tag validation
        assertThat(html, matchesPattern("(?s).*<html.*>.*"));
        assertThat(html, matchesPattern("(?s).*<body.*>.*"));
        assertThat(html, matchesPattern("(?s).*</body>.*</html>.*"));
    }

    @Test
    void testJsonStringValidation() {
        String jsonResponse = apiClient.getUserData();

        // JSON string structure
        assertThat(jsonResponse, startsWith("{"));
        assertThat(jsonResponse, endsWith("}"));
        assertThat(jsonResponse, containsString("\"email\""));
        assertThat(jsonResponse, containsString("\"name\""));

        // JSON validity (basic checks)
        assertThat(jsonResponse, not(containsString("undefined")));
        assertThat(jsonResponse, not(containsString("null").and(not(containsString(": null")))));
    }

    @Test
    void testPasswordValidation() {
        // Strong passwords
        assertThat("MySecurePass123!", allOf(
            hasLength(greaterThanOrEqualTo(8)),
            matchesPattern(".*[A-Z].*"),     // Uppercase
            matchesPattern(".*[a-z].*"),     // Lowercase
            matchesPattern(".*[0-9].*"),     // Numbers
            matchesPattern(".*[^A-Za-z0-9].*") // Special chars
        ));

        // Weak passwords
        assertThat("weak", anyOf(
            hasLength(lessThan(8)),
            not(matchesPattern(".*[A-Z].*")),
            not(matchesPattern(".*[0-9].*"))
        ));
    }
}
```

## Collection matchers

### List matchers

#### Basic list operations
```java
@SpringBootTest
public class ListMatchersTest {

    @Autowired
    private UserService userService;

    @Test
    void testUserList() {
        List<User> users = userService.getAllUsers();

        // Basic list assertions
        assertThat(users, notNullValue());
        assertThat(users, not(empty()));
        assertThat(users, hasSize(greaterThan(0)));

        // Element assertions
        assertThat(users, everyItem(instanceOf(User.class)));
        assertThat(users, hasItem(hasProperty("status", equalTo(UserStatus.ACTIVE))));

        // Ordering assertions
        assertThat(users, contains(
            hasProperty("email", equalTo("admin@example.com")),
            hasProperty("email", equalTo("user@example.com"))
        ));
    }

    @Test
    void testProductList() {
        List<Product> products = productService.getAvailableProducts();

        // Filtered assertions
        assertThat(products, hasItem(allOf(
            hasProperty("price", greaterThan(BigDecimal.valueOf(100.0))),
            hasProperty("category", equalTo("Electronics"))
        )));

        // Size and content assertions
        assertThat(products, hasSize(lessThan(100)));
        assertThat(products, everyItem(hasProperty("price", greaterThan(BigDecimal.ZERO))));

        // Unique constraints
        assertThat(products, everyItem(hasProperty("sku", notNullValue())));
        // Note: For uniqueness, we'd need custom logic or different matcher
    }

    @Test
    void testOrderItems() {
        Order order = orderService.createOrder(List.of(
            new OrderItem("laptop", BigDecimal.valueOf(1000.0), 1),
            new OrderItem("mouse", BigDecimal.valueOf(25.0), 2),
            new OrderItem("keyboard", BigDecimal.valueOf(75.0), 1)
        ));

        // Nested collection assertions
        assertThat(order.getItems(), hasSize(3));
        assertThat(order.getItems(), contains(
            hasProperty("productName", equalTo("laptop")),
            hasProperty("productName", equalTo("mouse")),
            hasProperty("productName", equalTo("keyboard"))
        ));

        assertThat(order.getItems(), everyItem(hasProperty("quantity", greaterThan(0))));
        assertThat(order.getItems(), everyItem(hasProperty("price", greaterThan(BigDecimal.ZERO))));
    }
}
```

### Set matchers

#### Set-specific operations
```java
@SpringBootTest
public class SetMatchersTest {

    @Autowired
    private PermissionService permissionService;

    @Test
    void testUserPermissions() {
        Set<String> permissions = permissionService.getUserPermissions("admin");

        // Set assertions
        assertThat(permissions, notNullValue());
        assertThat(permissions, not(empty()));
        assertThat(permissions, hasSize(greaterThan(5)));

        // Content assertions
        assertThat(permissions, hasItem("READ_USER"));
        assertThat(permissions, hasItem("WRITE_USER"));
        assertThat(permissions, hasItem("DELETE_USER"));

        // Set-specific: no ordering, but all elements present
        assertThat(permissions, containsInAnyOrder(
            "READ_USER", "WRITE_USER", "DELETE_USER", "ADMIN_ACCESS"
        ));
    }

    @Test
    void testProductTags() {
        Set<String> tags = tagService.getProductTags(123L);

        // Tag assertions
        assertThat(tags, notNullValue());
        assertThat(tags, contains("electronics"));

        // Case insensitive assertions
        assertThat(tags, hasItem(anyOf(
            equalTo("laptop"),
            equalTo("LAPTOP"),
            containsString("tech")
        )));

        // Subset assertions
        Set<String> premiumTags = Set.of("premium", "expensive", "high-end");
        assertThat(tags, not(hasItem(isIn(premiumTags))));
    }

    @Test
    void testUniqueConstraints() {
        Set<String> usernames = userService.getAllUsernames();

        // Uniqueness is implicit in Set
        assertThat(usernames, everyItem(matchesPattern("[a-zA-Z0-9_]+")));
        assertThat(usernames, everyItem(hasLength(greaterThanOrEqualTo(3))));
        assertThat(usernames, everyItem(not(containsString(" "))));

        // Size constraints
        assertThat(usernames, hasSize(greaterThan(0)));
        assertThat(usernames, hasSize(lessThan(10000)));
    }
}
```

### Map matchers

#### Map structure assertions
```java
@SpringBootTest
public class MapMatchersTest {

    @Autowired
    private ConfigurationService configService;

    @Test
    void testConfigurationMap() {
        Map<String, String> config = configService.getApplicationConfig();

        // Basic map assertions
        assertThat(config, notNullValue());
        assertThat(config, not(empty()));
        assertThat(config, hasSize(greaterThan(5)));

        // Key assertions
        assertThat(config, hasKey("database.url"));
        assertThat(config, hasKey("cache.ttl"));
        assertThat(config, not(hasKey("secret.password")));

        // Value assertions
        assertThat(config, hasEntry("database.url", "postgresql://localhost:5432/mydb"));
        assertThat(config, hasEntry(equalTo("app.name"), equalTo("MyApplication")));
    }

    @Test
    void testMetricsMap() {
        Map<String, Number> metrics = metricsService.getCurrentMetrics();

        // Metrics map assertions
        assertThat(metrics, notNullValue());
        assertThat(metrics, hasKey("jvm.memory.used"));
        assertThat(metrics, hasKey("http.requests.total"));

        // Value type assertions
        assertThat(metrics, hasEntry(
            equalTo("jvm.memory.used"),
            instanceOf(Long.class)
        ));

        assertThat(metrics, hasEntry(
            equalTo("http.requests.total"),
            allOf(
                instanceOf(Long.class),
                greaterThan(0L)
            )
        ));
    }

    @Test
    void testNestedMap() {
        Map<String, Map<String, Object>> nestedConfig = configService.getNestedConfig();

        // Nested map assertions
        assertThat(nestedConfig, notNullValue());
        assertThat(nestedConfig, hasKey("database"));

        Map<String, Object> dbConfig = nestedConfig.get("database");
        assertThat(dbConfig, notNullValue());
        assertThat(dbConfig, hasEntry("url", notNullValue()));
        assertThat(dbConfig, hasEntry("poolSize", allOf(
            instanceOf(Integer.class),
            allOf(greaterThan(0), lessThanOrEqualTo(50))
        )));
    }
}
```

## Object matchers

### Property-based assertions

#### Object property matching
```java
@SpringBootTest
public class ObjectMatchersTest {

    @Autowired
    private UserService userService;

    @Test
    void testUserObject() {
        User user = userService.createUser("john@example.com", "John Doe");

        // Basic object assertions
        assertThat(user, notNullValue());
        assertThat(user, instanceOf(User.class));
        assertThat(user, not(instanceOf(AdminUser.class)));

        // Property assertions
        assertThat(user, hasProperty("email"));
        assertThat(user, hasProperty("name"));
        assertThat(user, hasProperty("email", equalTo("john@example.com")));
        assertThat(user, hasProperty("name", equalTo("John Doe")));
    }

    @Test
    void testOrderObject() {
        Order order = orderService.createOrder(List.of(
            new OrderItem("laptop", BigDecimal.valueOf(1000.0), 1)
        ));

        // Complex object assertions
        assertThat(order, notNullValue());
        assertThat(order.getId(), notNullValue());
        assertThat(order.getStatus(), equalTo(OrderStatus.PENDING));
        assertThat(order.getItems(), not(empty()));
        assertThat(order.getTotalAmount(), greaterThan(BigDecimal.ZERO));

        // Property-based assertions
        assertThat(order, hasProperty("id", notNullValue()));
        assertThat(order, hasProperty("status", equalTo(OrderStatus.PENDING)));
        assertThat(order, hasProperty("totalAmount", greaterThan(BigDecimal.ZERO)));
    }

    @Test
    void testUserComparison() {
        User user1 = userService.createUser("user1@example.com", "User 1");
        User user2 = userService.createUser("user2@example.com", "User 2");

        // Property comparison
        assertThat(user1, not(equalTo(user2)));
        assertThat(user1, hasProperty("status", equalTo(user2.getStatus()))); // Both ACTIVE

        // Complex property assertions
        assertThat(user1, allOf(
            hasProperty("email", containsString("@")),
            hasProperty("name", not(emptyString())),
            hasProperty("status", equalTo(UserStatus.ACTIVE))
        ));
    }
}
```

## Logical matchers

### Combining matchers

#### Logical operations
```java
@SpringBootTest
public class LogicalMatchersTest {

    @Autowired
    private ValidationService validationService;

    @Test
    void testUserValidation() {
        User user = new User("valid@example.com", "Valid User");

        // All conditions must be true
        assertThat(user.getEmail(), allOf(
            notNullValue(),
            containsString("@"),
            hasLength(greaterThan(5))
        ));

        // At least one condition must be true
        assertThat(user.getName(), anyOf(
            equalTo("Valid User"),
            equalTo("Test User"),
            startsWith("Valid")
        ));

        // Negation
        assertThat(user.getPhoneNumber(), not(anyOf(
            nullValue(),
            emptyString()
        )));
    }

    @Test
    void testProductValidation() {
        Product product = new Product("Laptop", BigDecimal.valueOf(999.99), "Electronics");

        // Complex logical assertions
        assertThat(product, allOf(
            hasProperty("name", not(emptyString())),
            hasProperty("price", allOf(
                notNullValue(),
                greaterThan(BigDecimal.ZERO),
                lessThan(BigDecimal.valueOf(10000.0))
            )),
            hasProperty("category", notNullValue())
        ));

        // Either/or conditions
        assertThat(product.getCategory(), anyOf(
            equalTo("Electronics"),
            equalTo("Books"),
            equalTo("Clothing")
        ));
    }

    @Test
    void testOrderStatusTransitions() {
        Order order = orderService.createOrder(items);

        // Status transition logic
        assertThat(order.getStatus(), anyOf(
            equalTo(OrderStatus.PENDING),
            equalTo(OrderStatus.CONFIRMED),
            equalTo(OrderStatus.PROCESSING)
        ));

        // Complex business rules
        assertThat(order, allOf(
            hasProperty("status", not(equalTo(OrderStatus.CANCELLED))),
            hasProperty("totalAmount", greaterThan(BigDecimal.ZERO)),
            hasProperty("items", not(empty()))
        ));
    }
}
```

### Advanced logical combinations

#### Complex matcher composition
```java
@SpringBootTest
public class AdvancedLogicalMatchersTest {

    @Autowired
    private BusinessRulesService businessRulesService;

    @Test
    void testComplexBusinessRules() {
        Order order = createTestOrder();

        // Complex nested logical expressions
        assertThat(order, allOf(
            hasProperty("customer", allOf(
                hasProperty("creditScore", greaterThan(600)),
                hasProperty("status", equalTo(CustomerStatus.ACTIVE)),
                hasProperty("totalOrders", greaterThan(0))
            )),
            hasProperty("totalAmount", anyOf(
                allOf(greaterThanOrEqualTo(BigDecimal.valueOf(100.0)), lessThan(BigDecimal.valueOf(500.0))),
                allOf(greaterThanOrEqualTo(BigDecimal.valueOf(500.0)), lessThan(BigDecimal.valueOf(1000.0))),
                greaterThanOrEqualTo(BigDecimal.valueOf(1000.0))
            )),
            hasProperty("items", everyItem(allOf(
                hasProperty("price", greaterThan(BigDecimal.ZERO)),
                hasProperty("quantity", allOf(greaterThan(0), lessThanOrEqualTo(10))),
                hasProperty("product", hasProperty("available", equalTo(true)))
            )))
        ));
    }

    @Test
    void testUserAccessControl() {
        User user = userService.getUserWithRoles("admin");

        // Complex role-based access control
        assertThat(user, allOf(
            hasProperty("roles", hasItem("ADMIN")),
            anyOf(
                hasProperty("department", equalTo("IT")),
                hasProperty("department", equalTo("Management")),
                hasProperty("clearanceLevel", greaterThan(5))
            ),
            not(hasProperty("status", equalTo(UserStatus.SUSPENDED)))
        ));

        // Permission matrix validation
        assertThat(user.getPermissions(), allOf(
            hasItem("READ_ALL"),
            hasItem("WRITE_ALL"),
            not(hasItem("DELETE_SYSTEM")),
            everyItem(matchesPattern("^[A-Z_]+$"))
        ));
    }

    @Test
    void testDataIntegrityRules() {
        List<Order> orders = orderService.getAllOrders();

        // Data integrity across collections
        assertThat(orders, everyItem(allOf(
            hasProperty("id", notNullValue()),
            hasProperty("customer", notNullValue()),
            hasProperty("items", not(empty())),
            hasProperty("totalAmount", equalTo(
                // Sum of item prices * quantities
                new BigDecimal(orders.stream()
                    .flatMap(o -> o.getItems().stream())
                    .mapToDouble(item -> item.getPrice().doubleValue() * item.getQuantity())
                    .sum()
                )
            ))
        )));
    }
}
```

## Custom matchers

### Creating custom matchers

#### Basic custom matcher
```java
public class IsValidEmail extends TypeSafeMatcher<String> {

    @Override
    protected boolean matchesSafely(String email) {
        if (email == null || email.isEmpty()) {
            return false;
        }
        return email.contains("@") && email.length() >= 5;
    }

    @Override
    public void describeTo(Description description) {
        description.appendText("a valid email address");
    }

    @Override
    protected void describeMismatchSafely(String email, Description mismatchDescription) {
        mismatchDescription.appendText("was ").appendValue(email);
    }

    public static Matcher<String> validEmail() {
        return new IsValidEmail();
    }
}

// Usage
@SpringBootTest
public class CustomMatcherTest {

    @Autowired
    private UserService userService;

    @Test
    void testUserEmailValidation() {
        User user = userService.createUser("john@example.com", "John Doe");

        assertThat(user.getEmail(), IsValidEmail.validEmail());
        assertThat(user.getEmail(), not(IsValidEmail.validEmail())); // Invalid email test
    }
}
```

### Feature matcher

#### Property-based custom matcher
```java
public class HasValidCreditCard extends FeatureMatcher<PaymentMethod, String> {

    public HasValidCreditCard() {
        super(equalTo("VALID"), "credit card number", "validity");
    }

    @Override
    protected String featureValueOf(PaymentMethod paymentMethod) {
        if (!(paymentMethod instanceof CreditCard)) {
            return "NOT_CREDIT_CARD";
        }

        CreditCard card = (CreditCard) paymentMethod;
        String number = card.getNumber();

        if (number == null || number.length() < 13 || number.length() > 19) {
            return "INVALID_LENGTH";
        }

        // Luhn algorithm check (simplified)
        if (!isValidLuhn(number)) {
            return "INVALID_CHECKSUM";
        }

        return "VALID";
    }

    private boolean isValidLuhn(String number) {
        // Simplified Luhn algorithm implementation
        int sum = 0;
        boolean alternate = false;
        for (int i = number.length() - 1; i >= 0; i--) {
            int n = Integer.parseInt(number.substring(i, i + 1));
            if (alternate) {
                n *= 2;
                if (n > 9) {
                    n = (n % 10) + 1;
                }
            }
            sum += n;
            alternate = !alternate;
        }
        return (sum % 10) == 0;
    }

    public static Matcher<PaymentMethod> validCreditCard() {
        return new HasValidCreditCard();
    }
}

// Usage
@SpringBootTest
public class FeatureMatcherTest {

    @Autowired
    private PaymentService paymentService;

    @Test
    void testPaymentMethodValidation() {
        PaymentMethod creditCard = new CreditCard("4111111111111111", "12/25", "123");

        assertThat(creditCard, HasValidCreditCard.validCreditCard());

        PaymentMethod invalidCard = new CreditCard("1234567890123456", "12/25", "123");
        assertThat(invalidCard, not(HasValidCreditCard.validCreditCard()));
    }
}
```

### Complex custom matcher

#### Order validation matcher
```java
public class IsValidOrder extends TypeSafeMatcher<Order> {

    private final List<String> errors = new ArrayList<>();

    @Override
    protected boolean matchesSafely(Order order) {
        errors.clear();

        // Basic validation
        if (order.getId() == null) {
            errors.add("Order ID is null");
        }

        if (order.getCustomer() == null) {
            errors.add("Customer is null");
        }

        if (order.getItems() == null || order.getItems().isEmpty()) {
            errors.add("Order has no items");
        } else {
            // Item validation
            for (int i = 0; i < order.getItems().size(); i++) {
                OrderItem item = order.getItems().get(i);
                validateOrderItem(item, i);
            }
        }

        // Amount validation
        if (order.getTotalAmount() == null || order.getTotalAmount().compareTo(BigDecimal.ZERO) <= 0) {
            errors.add("Invalid total amount");
        }

        // Status validation
        if (order.getStatus() == null) {
            errors.add("Order status is null");
        }

        return errors.isEmpty();
    }

    private void validateOrderItem(OrderItem item, int index) {
        if (item.getProductName() == null || item.getProductName().isEmpty()) {
            errors.add("Item " + index + " has no product name");
        }

        if (item.getPrice() == null || item.getPrice().compareTo(BigDecimal.ZERO) <= 0) {
            errors.add("Item " + index + " has invalid price");
        }

        if (item.getQuantity() <= 0) {
            errors.add("Item " + index + " has invalid quantity");
        }
    }

    @Override
    public void describeTo(Description description) {
        description.appendText("a valid order");
    }

    @Override
    protected void describeMismatchSafely(Order order, Description mismatchDescription) {
        mismatchDescription.appendText("was invalid: ").appendText(String.join(", ", errors));
    }

    public static Matcher<Order> validOrder() {
        return new IsValidOrder();
    }
}

// Usage
@SpringBootTest
public class ComplexCustomMatcherTest {

    @Autowired
    private OrderService orderService;

    @Test
    void testOrderValidation() {
        Order order = orderService.createOrder(List.of(
            new OrderItem("laptop", BigDecimal.valueOf(1000.0), 1)
        ));

        assertThat(order, IsValidOrder.validOrder());

        // Test invalid order
        Order invalidOrder = new Order();
        assertThat(invalidOrder, not(IsValidOrder.validOrder()));
    }
}
```

## Matcher composition

### Combining matchers

#### Advanced composition techniques
```java
@SpringBootTest
public class MatcherCompositionTest {

    @Autowired
    private ComplexValidationService validationService;

    @Test
    void testComplexValidationWithComposition() {
        User user = createTestUser();

        // Compose multiple matchers
        Matcher<User> validUserMatcher = allOf(
            hasProperty("email", allOf(
                notNullValue(),
                containsString("@"),
                hasLength(greaterThan(5))
            )),
            hasProperty("name", allOf(
                notNullValue(),
                not(emptyString()),
                hasLength(greaterThan(1))
            )),
            hasProperty("age", allOf(
                greaterThan(0),
                lessThan(150)
            )),
            hasProperty("status", equalTo(UserStatus.ACTIVE))
        );

        assertThat(user, validUserMatcher);
    }

    @Test
    void testPaymentValidationComposition() {
        Payment payment = createTestPayment();

        // Complex payment validation
        assertThat(payment, allOf(
            hasProperty("amount", allOf(
                notNullValue(),
                greaterThan(BigDecimal.ZERO),
                lessThanOrEqualTo(BigDecimal.valueOf(10000.0))
            )),
            hasProperty("method", anyOf(
                equalTo(PaymentMethod.CREDIT_CARD),
                equalTo(PaymentMethod.PAYPAL),
                equalTo(PaymentMethod.BANK_TRANSFER)
            )),
            hasProperty("status", anyOf(
                equalTo(PaymentStatus.PENDING),
                equalTo(PaymentStatus.COMPLETED)
            )),
            not(hasProperty("errorMessage", notNullValue())) // No errors for valid payments
        ));
    }

    @Test
    void testOrderProcessingComposition() {
        Order order = createComplexOrder();

        // Order processing validation
        assertThat(order, allOf(
            hasProperty("id", notNullValue()),
            hasProperty("customer", allOf(
                notNullValue(),
                hasProperty("status", equalTo(CustomerStatus.ACTIVE))
            )),
            hasProperty("items", allOf(
                not(empty()),
                everyItem(allOf(
                    hasProperty("productName", not(emptyString())),
                    hasProperty("price", greaterThan(BigDecimal.ZERO)),
                    hasProperty("quantity", greaterThan(0))
                ))
            )),
            hasProperty("totalAmount", allOf(
                notNullValue(),
                greaterThan(BigDecimal.ZERO),
                // Total should equal sum of item totals
                equalTo(calculateExpectedTotal(order.getItems()))
            )),
            hasProperty("status", not(equalTo(OrderStatus.CANCELLED)))
        ));
    }

    private BigDecimal calculateExpectedTotal(List<OrderItem> items) {
        return items.stream()
            .map(item -> item.getPrice().multiply(BigDecimal.valueOf(item.getQuantity())))
            .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
}
```

### Conditional matcher composition

#### Dynamic matcher building
```java
@SpringBootTest
public class ConditionalCompositionTest {

    @Autowired
    private DynamicValidationService validationService;

    @Test
    void testDynamicValidationRules() {
        ValidationRequest request = createValidationRequest();

        // Build matcher based on request type
        Matcher<ValidationRequest> matcher = buildValidationMatcher(request.getType());

        assertThat(request, matcher);
    }

    private Matcher<ValidationRequest> buildValidationMatcher(ValidationType type) {
        switch (type) {
            case USER_REGISTRATION:
                return allOf(
                    hasProperty("email", validEmail()),
                    hasProperty("password", strongPassword()),
                    hasProperty("age", greaterThanOrEqualTo(13))
                );

            case PAYMENT_PROCESSING:
                return allOf(
                    hasProperty("amount", allOf(
                        greaterThan(BigDecimal.ZERO),
                        lessThanOrEqualTo(BigDecimal.valueOf(10000.0))
                    )),
                    hasProperty("paymentMethod", supportedPaymentMethod()),
                    hasProperty("currency", validCurrency())
                );

            case ORDER_SUBMISSION:
                return allOf(
                    hasProperty("customerId", notNullValue()),
                    hasProperty("items", not(empty())),
                    hasProperty("totalAmount", greaterThan(BigDecimal.ZERO)),
                    hasProperty("shippingAddress", validAddress())
                );

            default:
                return anyOf(
                    hasProperty("basicValidation", equalTo(true)),
                    hasProperty("skipValidation", equalTo(true))
                );
        }
    }

    @Test
    void testContextAwareValidation() {
        List<User> users = userService.getUsersByContext();

        // Different validation rules based on context
        for (User user : users) {
            Matcher<User> contextMatcher = getContextMatcher(user.getContext());

            assertThat(user, contextMatcher);
        }
    }

    private Matcher<User> getContextMatcher(UserContext context) {
        switch (context) {
            case ADMIN:
                return allOf(
                    hasProperty("role", equalTo("ADMIN")),
                    hasProperty("permissions", hasItem("FULL_ACCESS")),
                    hasProperty("twoFactorEnabled", equalTo(true))
                );

            case PREMIUM_USER:
                return allOf(
                    hasProperty("subscriptionType", equalTo("PREMIUM")),
                    hasProperty("maxProjects", greaterThan(10)),
                    hasProperty("supportLevel", equalTo("PRIORITY"))
                );

            case FREE_USER:
                return allOf(
                    hasProperty("subscriptionType", equalTo("FREE")),
                    hasProperty("maxProjects", lessThanOrEqualTo(3)),
                    hasProperty("adsEnabled", equalTo(true))
                );

            default:
                return hasProperty("status", equalTo(UserStatus.ACTIVE));
        }
    }
}
```

## Spring integration

### Spring MVC matchers

#### HTTP response assertions
```java
@SpringBootTest
@AutoConfigureMockMvc
public class SpringMvcMatchersTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void testUserApiResponse() throws Exception {
        mockMvc.perform(post("/api/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"email\":\"test@example.com\",\"name\":\"Test User\"}"))
            .andExpect(status().isCreated())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.id").exists())
            .andExpect(jsonPath("$.email").value("test@example.com"))
            .andExpect(jsonPath("$.status").value("ACTIVE"));

        // Hamcrest-style assertions on response
        mockMvc.perform(get("/api/users/1"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andDo(result -> {
                String responseBody = result.getResponse().getContentAsString();
                assertThat(responseBody, containsString("test@example.com"));
                assertThat(responseBody, not(containsString("error")));
            });
    }

    @Test
    void testErrorHandling() throws Exception {
        mockMvc.perform(post("/api/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{}")) // Invalid request
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.error").value("VALIDATION_ERROR"))
            .andExpect(jsonPath("$.message").value("Email is required"))
            .andDo(result -> {
                String errorResponse = result.getResponse().getContentAsString();
                assertThat(errorResponse, allOf(
                    containsString("VALIDATION_ERROR"),
                    containsString("Email is required"),
                    not(containsString("success"))
                ));
            });
    }

    @Test
    void testCollectionResponse() throws Exception {
        mockMvc.perform(get("/api/users"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$[0].id").exists())
            .andExpect(jsonPath("$[0].email").isString())
            .andDo(result -> {
                String responseBody = result.getResponse().getContentAsString();
                assertThat(responseBody, allOf(
                    startsWith("["),
                    endsWith("]"),
                    containsString("email"),
                    containsString("name")
                ));
            });
    }
}
```

### Spring context matchers

#### Bean validation
```java
@SpringBootTest
public class SpringContextMatchersTest {

    @Autowired
    private ApplicationContext context;

    @Test
    void testSpringBeanConfiguration() {
        // Bean existence
        assertThat(context.containsBean("userService")).isTrue();
        assertThat(context.containsBean("userRepository")).isTrue();

        // Bean type validation
        assertThat(context.getBean("userService"), instanceOf(UserService.class));
        assertThat(context.getBean("userRepository"), instanceOf(UserRepository.class));

        // Bean properties
        UserService userService = context.getBean(UserService.class);
        assertThat(userService, notNullValue());
    }

    @Test
    void testConfigurationProperties() {
        // Environment properties
        assertThat(context.getEnvironment().getProperty("spring.application.name"))
            .equalTo("my-application");

        assertThat(context.getEnvironment().getProperty("server.port"))
            .equalTo("8080");

        // Profile validation
        String[] activeProfiles = context.getEnvironment().getActiveProfiles();
        assertThat(activeProfiles, hasItemInArray("test"));
        assertThat(activeProfiles, not(hasItemInArray("production")));
    }

    @Test
    void testBeanDependencies() {
        // Dependency injection validation
        UserService userService = context.getBean(UserService.class);

        // Validate that dependencies are injected
        assertThat(userService, hasProperty("userRepository"));
        assertThat(userService, hasProperty("emailService"));
        assertThat(userService, hasProperty("validationService"));
    }
}
```

## Bean matchers

### Property-based bean validation

#### Bean property assertions
```java
@SpringBootTest
public class BeanMatchersTest {

    @Autowired
    private BeanValidationService validationService;

    @Test
    void testBeanPropertyValidation() {
        User user = userService.createUser("john@example.com", "John Doe");

        // Bean property assertions using Hamcrest
        assertThat(user, hasProperty("email", equalTo("john@example.com")));
        assertThat(user, hasProperty("name", equalTo("John Doe")));
        assertThat(user, hasProperty("status", equalTo(UserStatus.ACTIVE)));
        assertThat(user, hasProperty("createdAt", notNullValue()));

        // Multiple property validation
        assertThat(user, allOf(
            hasProperty("email", containsString("@")),
            hasProperty("name", not(emptyString())),
            hasProperty("status", notNullValue())
        ));
    }

    @Test
    void testNestedBeanProperties() {
        Order order = orderService.createOrder(List.of(
            new OrderItem("laptop", BigDecimal.valueOf(1000.0), 1)
        ));

        // Nested property assertions
        assertThat(order, hasProperty("customer", allOf(
            hasProperty("email", notNullValue()),
            hasProperty("name", notNullValue()),
            hasProperty("status", equalTo(CustomerStatus.ACTIVE))
        )));

        // Collection property validation
        assertThat(order, hasProperty("items", contains(
            allOf(
                hasProperty("productName", equalTo("laptop")),
                hasProperty("price", comparesEqualTo(BigDecimal.valueOf(1000.0))),
                hasProperty("quantity", equalTo(1))
            )
        )));
    }

    @Test
    void testBeanValidationResults() {
        User invalidUser = new User("", ""); // Invalid user

        Set<ConstraintViolation<User>> violations = validator.validate(invalidUser);

        // Validation result assertions
        assertThat(violations, not(empty()));
        assertThat(violations, hasSize(greaterThan(0)));

        // Specific violation checks
        assertThat(violations, hasItem(hasProperty("message", containsString("email"))));
        assertThat(violations, hasItem(hasProperty("message", containsString("name"))));

        // Violation details
        assertThat(violations, everyItem(hasProperty("propertyPath", notNullValue())));
        assertThat(violations, everyItem(hasProperty("message", not(emptyString()))));
    }
}
```

## XML/JSON matchers

### JSON structure validation

#### JSON content assertions
```java
@SpringBootTest
@AutoConfigureMockMvc
public class JsonMatchersTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void testJsonResponseStructure() throws Exception {
        mockMvc.perform(get("/api/users/1"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.id").isNumber())
            .andExpect(jsonPath("$.email").isString())
            .andExpect(jsonPath("$.name").isString())
            .andDo(result -> {
                String json = result.getResponse().getContentAsString();

                // Hamcrest JSON assertions
                assertThat(json, containsString("\"id\""));
                assertThat(json, containsString("\"email\""));
                assertThat(json, containsString("\"name\""));
                assertThat(json, not(containsString("\"password\""))); // Security check
            });
    }

    @Test
    void testJsonArrayValidation() throws Exception {
        mockMvc.perform(get("/api/users"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$[0].id").exists())
            .andDo(result -> {
                String json = result.getResponse().getContentAsString();

                // Array structure validation
                assertThat(json, startsWith("["));
                assertThat(json, endsWith("]"));
                assertThat(json, containsString("\"id\""));
                assertThat(json, containsString("\"email\""));
            });
    }

    @Test
    void testComplexJsonValidation() throws Exception {
        mockMvc.perform(get("/api/orders/1"))
            .andDo(result -> {
                String json = result.getResponse().getContentAsString();

                // Complex JSON structure
                assertThat(json, allOf(
                    containsString("\"id\""),
                    containsString("\"customer\""),
                    containsString("\"items\""),
                    not(containsString("\"internalId\"")) // Internal field not exposed
                ));

                // Nested object validation
                assertThat(json, containsString("\"customer\":{"));
                assertThat(json, containsString("\"items\":["));
            });
    }
}
```

## Best practices

### 1. Choosing between Hamcrest and AssertJ

#### When to use Hamcrest:
- **Matcher composition** — сложные логические комбинации
- **Custom matchers** — reusable validation logic
- **Legacy code** — интеграция с существующими тестами
- **Framework agnostic** — не зависит от testing framework
- **Type safety** — compile-time проверка типов

#### When to use AssertJ:
- **Fluent API** — более читаемый синтаксис
- **Rich assertions** — больше built-in проверок
- **Soft assertions** — группировка проверок
- **Better IDE support** — автодополнение и refactoring
- **Modern Java** — лучше интегрируется с Java 8+

### 2. Writing maintainable matchers

#### Descriptive matcher naming
```java
@SpringBootTest
public class DescriptiveMatchersTest {

    @Autowired
    private OrderService orderService;

    @Test
    void testOrderBusinessRules() {
        Order order = orderService.createOrder(items);

        // Descriptive matcher names make tests self-documenting
        assertThat(order, isValidForProcessing());
        assertThat(order, hasSufficientCustomerCredit());
        assertThat(order, containsOnlyAvailableItems());
        assertThat(order, meetsMinimumOrderRequirements());
    }

    @Test
    void testUserSecurityConstraints() {
        User user = userService.getUserWithPermissions("admin");

        // Security validation through descriptive matchers
        assertThat(user, hasRequiredSecurityClearance());
        assertThat(user, isNotSuspendedOrDeactivated());
        assertThat(user, hasValidAuthenticationCredentials());
        assertThat(user, compliesWithPasswordPolicy());
    }

    // Custom descriptive matchers
    private Matcher<Order> isValidForProcessing() {
        return new TypeSafeMatcher<Order>() {
            @Override
            protected boolean matchesSafely(Order order) {
                return order.getStatus() == OrderStatus.PENDING &&
                       order.getTotalAmount().compareTo(BigDecimal.valueOf(10.0)) > 0 &&
                       order.getCustomer() != null &&
                       !order.getItems().isEmpty();
            }

            @Override
            public void describeTo(Description description) {
                description.appendText("an order valid for processing");
            }
        };
    }

    private Matcher<Order> hasSufficientCustomerCredit() {
        return hasProperty("customer",
            hasProperty("creditLimit",
                greaterThanOrEqualTo(hasProperty("orderTotal"))));
    }
}
```

### 3. Performance considerations

#### Efficient matcher usage
```java
@SpringBootTest
public class PerformanceMatchersTest {

    @Autowired
    private PerformanceService performanceService;

    @Test
    void testPerformanceRequirements() {
        PerformanceMetrics metrics = performanceService.getMetrics();

        // Performance assertions with clear thresholds
        assertThat(metrics.getResponseTime(), allOf(
            lessThan(1000L), // 1 second max
            greaterThan(0L)  // Must be positive
        ));

        assertThat(metrics.getThroughput(), greaterThan(50)); // 50 req/sec minimum
        assertThat(metrics.getErrorRate(), lessThan(0.05)); // 5% max error rate

        // Memory efficiency
        assertThat(metrics.getMemoryUsage(), allOf(
            greaterThan(0L),
            lessThan(512L * 1024L * 1024L) // 512MB max
        ));
    }

    @Test
    void testScalabilityValidation() {
        LoadTestResults results = performanceService.runLoadTest(100, Duration.ofMinutes(5));

        // Scalability assertions
        assertThat(results.getTotalRequests(), greaterThan(10000));
        assertThat(results.getAverageResponseTime(), lessThan(2000L)); // 2 seconds under load
        assertThat(results.getErrorRate(), lessThan(0.10)); // 10% max errors under load

        // Resource utilization
        assertThat(results.getPeakMemoryUsage(),
            lessThan(1024L * 1024L * 1024L)); // 1GB max

        assertThat(results.getCpuUtilization(), allOf(
            greaterThan(10.0), // At least 10% CPU usage
            lessThan(95.0)     // Less than 95% CPU usage
        ));
    }

    @Test
    void testDatabaseQueryPerformance() {
        QueryPerformanceResults results = databaseService.analyzeQueryPerformance();

        // Query performance validation
        assertThat(results.getAverageQueryTime(), lessThan(100L)); // 100ms max
        assertThat(results.getSlowQueriesCount(), lessThan(5)); // Max 5 slow queries
        assertThat(results.getQueryCount(), greaterThan(1000)); // Sufficient sample size

        // Index effectiveness
        assertThat(results.getIndexHitRatio(), greaterThan(0.95)); // 95% index usage
        assertThat(results.getFullTableScans(), lessThan(10)); // Minimal table scans
    }
}
```

### 4. Testing strategies with matchers

#### Data-driven testing
```java
@SpringBootTest
public class DataDrivenMatchersTest {

    @Autowired
    private ValidationService validationService;

    // Test data parameterized with matchers
    static Stream<Arguments> validationTestCases() {
        return Stream.of(
            arguments("valid@example.com", isValidEmail()),
            arguments("invalid-email", not(isValidEmail())),
            arguments("", not(isValidEmail())),
            arguments("test@subdomain.example.com", isValidEmail())
        );
    }

    @ParameterizedTest
    @MethodSource("validationTestCases")
    void testEmailValidation(String email, Matcher<Boolean> resultMatcher) {
        boolean isValid = validationService.isValidEmail(email);
        assertThat(isValid, resultMatcher);
    }

    @Test
    void testPasswordStrengthMatrix() {
        List<PasswordTestCase> testCases = List.of(
            new PasswordTestCase("weak", isWeakPassword()),
            new PasswordTestCase("MySecurePass123!", isStrongPassword()),
            new PasswordTestCase("MediumPass1", isMediumPassword()),
            new PasswordTestCase("short", isWeakPassword())
        );

        testCases.forEach(testCase -> {
            PasswordStrength strength = validationService.checkPasswordStrength(testCase.password);
            assertThat(strength, testCase.strengthMatcher);
        });
    }

    @Test
    void testUserRolePermissions() {
        Map<UserRole, Matcher<Set<String>>> rolePermissions = Map.of(
            UserRole.ADMIN, allOf(
                hasItem("READ_ALL"),
                hasItem("WRITE_ALL"),
                hasItem("DELETE_ALL"),
                hasSize(greaterThan(10))
            ),
            UserRole.USER, allOf(
                hasItem("READ_OWN"),
                hasItem("WRITE_OWN"),
                not(hasItem("DELETE_ALL")),
                hasSize(lessThan(10))
            ),
            UserRole.GUEST, allOf(
                hasItem("READ_PUBLIC"),
                not(hasItem("WRITE_ANY")),
                not(hasItem("DELETE_ANY")),
                hasSize(lessThan(5))
            )
        );

        rolePermissions.forEach((role, permissionMatcher) -> {
            Set<String> permissions = permissionService.getPermissionsForRole(role);
            assertThat(permissions, permissionMatcher);
        });
    }

    static class PasswordTestCase {
        final String password;
        final Matcher<PasswordStrength> strengthMatcher;

        PasswordTestCase(String password, Matcher<PasswordStrength> strengthMatcher) {
            this.password = password;
            this.strengthMatcher = strengthMatcher;
        }
    }
}
```

## Troubleshooting

### Распространенные проблемы

#### ClassNotFoundException for Hamcrest

**Symptoms:**
- Tests fail with ClassNotFoundException for Hamcrest classes

**Solutions:**
```xml
<!-- Ensure correct dependencies -->
<dependency>
    <groupId>org.hamcrest</groupId>
    <artifactId>hamcrest</artifactId>
    <version>2.2</version>
    <scope>test</scope>
</dependency>

<!-- For additional matchers -->
<dependency>
    <groupId>org.hamcrest</groupId>
    <artifactId>hamcrest-library</artifactId>
    <version>2.2</version>
    <scope>test</scope>
</dependency>
```

#### Static import issues

**Symptoms:**
- IDE shows errors for static imports of Hamcrest methods

**Solutions:**
```java
// Use explicit imports instead of static
import org.hamcrest.MatcherAssert.assertThat;
import org.hamcrest.Matchers.*;

// Or configure IDE to recognize Hamcrest static imports
// IntelliJ IDEA: Settings -> Editor -> General -> Auto Import
// Add: org.hamcrest.Matchers.*
```

#### Matcher type mismatch

**Symptoms:**
- Compile-time errors about matcher types

**Solutions:**
```java
// Wrong: mixing incompatible types
assertThat(user.getAge(), equalTo("25")); // int vs String

// Correct: proper type matching
assertThat(user.getAge(), equalTo(25));

// For complex types, use appropriate matchers
assertThat(user.getCreatedAt(), notNullValue());
assertThat(user.getEmail(), containsString("@"));
```

#### Custom matcher not working

**Symptoms:**
- Custom matchers don't behave as expected

**Solutions:**
```java
// Ensure proper inheritance
public class CustomMatcher extends TypeSafeMatcher<MyType> {

    @Override
    protected boolean matchesSafely(MyType item) {
        // Implementation
        return true;
    }

    @Override
    public void describeTo(Description description) {
        description.appendText("description of expected");
    }

    // For better error messages
    @Override
    protected void describeMismatchSafely(MyType item, Description mismatchDescription) {
        mismatchDescription.appendText("was ").appendValue(item);
    }
}
```

### Debug techniques

#### Verbose matcher output
```java
@SpringBootTest
public class DebugMatchersTest {

    @Autowired
    private ComplexService complexService;

    @Test
    void testWithVerboseMatcherOutput() {
        ComplexObject obj = complexService.createComplexObject();

        // Use descriptive matchers for better error messages
        assertThat("Object should be valid", obj, isValidComplexObject());

        // For debugging, add intermediate assertions
        assertThat("ID should not be null", obj.getId(), notNullValue());
        assertThat("Name should not be empty", obj.getName(), not(emptyString()));

        // Complex validation with detailed feedback
        assertThat("Object structure should be correct", obj, hasValidStructure());
    }

    private Matcher<ComplexObject> isValidComplexObject() {
        return new TypeSafeMatcher<ComplexObject>() {
            @Override
            protected boolean matchesSafely(ComplexObject obj) {
                List<String> errors = new ArrayList<>();

                if (obj.getId() == null) errors.add("ID is null");
                if (obj.getName() == null || obj.getName().isEmpty()) errors.add("Name is empty");
                if (obj.getItems() == null || obj.getItems().isEmpty()) errors.add("No items");
                // Add more validation logic

                return errors.isEmpty();
            }

            @Override
            public void describeTo(Description description) {
                description.appendText("a valid complex object");
            }

            @Override
            protected void describeMismatchSafely(ComplexObject obj, Description mismatchDescription) {
                List<String> errors = validateObject(obj);
                mismatchDescription.appendText("had errors: ").appendText(String.join(", ", errors));
            }
        };
    }

    @Test
    void testMatcherCompositionDebug() {
        User user = userService.createUser("debug@example.com", "Debug User");

        // Break down complex matcher for debugging
        Matcher<User> emailMatcher = hasProperty("email", containsString("@"));
        Matcher<User> nameMatcher = hasProperty("name", not(emptyString()));
        Matcher<User> statusMatcher = hasProperty("status", equalTo(UserStatus.ACTIVE));

        // Test individual matchers first
        assertThat("Email should be valid", user, emailMatcher);
        assertThat("Name should not be empty", user, nameMatcher);
        assertThat("Status should be active", user, statusMatcher);

        // Then combine them
        assertThat("User should be completely valid", user,
            allOf(emailMatcher, nameMatcher, statusMatcher));
    }
}
```

## Заключение

**Hamcrest** — это мощный framework для создания composable matcher objects, которые позволяют писать гибкие и читаемые assertions в тестах. Hamcrest отличается от AssertJ своим focus на composition и reusability.

### Ключевые возможности:

1. **Matcher Composition** — комбинирование matchers с allOf/anyOf/not
2. **Custom Matchers** — создание reusable validation logic
3. **Type Safety** — compile-time проверка типов
4. **Framework Agnostic** — работает с любыми testing frameworks
5. **Rich Diagnostics** — понятные сообщения при ошибках
6. **Extensible** — возможность создания новых matchers

### Архитектурные преимущества:

#### Composability:
- **Logical Operators** — allOf, anyOf, not для комбинации
- **Nested Matchers** — сложные вложенные проверки
- **Reusable Components** — custom matchers для common validations
- **Type-safe Composition** — compile-time проверка compatibility

#### Expressiveness:
- **Descriptive Matchers** — self-documenting validation rules
- **Domain-specific Language** — matchers как DSL для business rules
- **Flexible Validation** — различные стратегии проверки
- **Rich Error Messages** — детальная диагностика

### Когда использовать Hamcrest:

✅ **Matcher Reuse** — создание reusable validation components
✅ **Complex Logic** — nested и composable validation rules
✅ **Legacy Integration** — работа с существующими testing frameworks
✅ **Domain Modeling** — matchers как domain language
✅ **Type Safety** — strict compile-time проверка
✅ **Custom DSL** — создание domain-specific validation languages
✅ **Composition Heavy** — много логических комбинаций

### Когда НЕ использовать:

❌ **Simple Assertions** — для базовых equalTo/notNullValue
❌ **Fluent Style** — если нужен fluent API как AssertJ
❌ **IDE Heavy** — если важна IDE интеграция и автодополнение
❌ **Modern Java** — AssertJ лучше интегрируется с Java 8+
❌ **Soft Assertions** — Hamcrest не имеет built-in soft assertions
❌ **JSON Assertions** — AssertJ имеет лучше JSON support

### Best practices:

1. **Custom Matchers** — инкапсуляция complex validation logic
2. **Descriptive Names** — self-documenting matcher naming
3. **Composition** — использование allOf/anyOf для complex rules
4. **Type Safety** — использование TypeSafeMatcher
5. **Error Messages** — качественные describeTo/describeMismatch
6. **Reusability** — создание matchers для common validations
7. **Domain Focus** — matchers отражающие business rules

### Типы Matchers по назначению:

#### Core Types:
- **Equality** — equalTo, sameInstance, comparesEqualTo
- **Nullness** — nullValue, notNullValue
- **Collections** — hasSize, contains, everyItem, hasItem
- **Strings** — containsString, startsWith, endsWith, matchesPattern
- **Numbers** — greaterThan, lessThan, closeTo

#### Logical:
- **Combinators** — allOf, anyOf, not
- **Conditional** — based on predicates и conditions

#### Custom:
- **TypeSafeMatcher** — type-safe custom matchers
- **FeatureMatcher** — property-based matchers
- **BaseMatcher** — basic custom matcher foundation

#### Specialized:
- **Bean Matchers** — hasProperty для bean validation
- **Spring Integration** — context и MVC matchers
- **JSON/XML** — structure validation matchers

Hamcrest является отличным выбором для проектов, где важна composability и reusability validation logic. Его matcher-based подход позволяет создавать declarative и composable assertions, которые могут быть reused across different test scenarios. 🚀

**Далее: WireMock (API mocking и testing)**
