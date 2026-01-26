# Mockito Advanced для Java

Комплексное руководство по продвинутым техникам mocking с Mockito: advanced matchers, verification, spies, custom answers, BDDMockito, integration с Spring Boot и best practices для создания эффективных unit тестов.

**Дата последнего обновления:** 2026-01-21

## Полезные ссылки

### Официальная документация
- [Mockito Documentation](https://javadoc.io/doc/org.mockito/mockito-core/latest/org/mockito/Mockito.html) - Основная документация
- [Mockito GitHub](https://github.com/mockito/mockito) - Репозиторий и wiki
- [Mockito Best Practices](https://github.com/mockito/mockito/wiki/How-to-write-good-tests) - Лучшие практики

### Spring Boot интеграция
- [@MockBean vs @Mock](https://docs.spring.io/spring-boot/docs/current/reference/html/features.html#features.testing.spring-boot-applications.mocking-and-spying) - Spring Boot mocking
- [Mockito with Spring](https://www.baeldung.com/mockito-annotations) - Mockito аннотации

### Best practices
- [Testing on the Toilet](https://testing.googleblog.com/) - Google testing blog
- [Mockito Anti-patterns](https://github.com/mockito/mockito/wiki/Mockito-And-Design-Philosophy) - Антипаттерны
- [Unit Testing Best Practices](https://martinfowler.com/bliki/UnitTest.html) - Фаулер о unit testing

### См. также
- `testing/junit-advanced.md` - JUnit расширения
- `testing/assertj.md` - Fluent assertions
- `spring-testing.md` - Spring testing

## Содержание

- [Введение в Mockito](#введение-в-mockito)
- [Advanced matchers](#advanced-matchers)
- [Verification modes](#verification-modes)
- [Spies и partial mocking](#spies-и-partial-mocking)
- [Custom answers](#custom-answers)
- [BDDMockito](#bddmockito)
- [Argument captors](#argument-captors)
- [Mock injection](#mock-injection)
- [Spring Boot integration](#spring-boot-integration)
- [Testing void methods](#testing-void-methods)
- [Exception testing](#exception-testing)
- [Async mocking](#async-mocking)
- [Best practices](#best-practices)
- [Troubleshooting](#troubleshooting)
- [Заключение](#заключение)

## Введение в Mockito

**Mockito** — это популярный mocking framework для Java, который позволяет создавать mock объекты для изоляции unit тестов. Mockito предоставляет clean и intuitive API для создания, конфигурации и верификации mock объектов.

### Базовая настройка

#### Maven зависимости
```xml
<dependency>
    <groupId>org.mockito</groupId>
    <artifactId>mockito-core</artifactId>
    <version>5.5.0</version>
    <scope>test</scope>
</dependency>

<!-- Для JUnit 5 integration -->
<dependency>
    <groupId>org.mockito</groupId>
    <artifactId>mockito-junit-jupiter</artifactId>
    <version>5.5.0</version>
    <scope>test</scope>
</dependency>

<!-- Для Spring Boot -->
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-test</artifactId>
    <scope>test</scope>
</dependency>
```

#### Базовый пример
```java
@SpringBootTest
public class MockitoBasicTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private EmailService emailService;

    @InjectMocks
    private UserService userService;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testCreateUser() {
        // Given
        User user = new User("test@example.com", "Test User");
        when(userRepository.save(any(User.class))).thenReturn(user);

        // When
        User created = userService.createUser("test@example.com", "Test User");

        // Then
        assertNotNull(created);
        assertEquals("test@example.com", created.getEmail());
        verify(userRepository).save(any(User.class));
    }
}
```

## Advanced matchers

### Hamcrest matchers

#### Custom Hamcrest matchers
```java
@SpringBootTest
public class HamcrestMatchersTest {

    @Mock
    private PaymentService paymentService;

    @InjectMocks
    private OrderService orderService;

    @Test
    void testOrderProcessingWithHamcrest() {
        Order order = new Order(BigDecimal.valueOf(100.0));

        // Using Hamcrest matchers
        when(paymentService.processPayment(
            argThat(hasProperty("amount", equalTo(BigDecimal.valueOf(100.0)))),
            argThat(hasProperty("currency", equalTo("USD")))
        )).thenReturn(new PaymentResult(true, "SUCCESS"));

        // Test
        PaymentResult result = orderService.processOrderPayment(order);

        assertTrue(result.isSuccess());
        verify(paymentService).processPayment(
            argThat(hasProperty("amount", greaterThan(BigDecimal.valueOf(50.0)))),
            argThat(hasProperty("currency", notNullValue()))
        );
    }

    @Test
    void testUserValidationWithMatchers() {
        User user = new User("john@example.com", "John");

        // Complex matcher for user validation
        when(userRepository.save(argThat(allOf(
            hasProperty("email", containsString("@")),
            hasProperty("name", not(isEmptyOrNullString()))
        )))).thenReturn(user);

        User saved = userService.createUser("john@example.com", "John");

        assertNotNull(saved);
        verify(userRepository).save(argThat(hasProperty("email",
            equalToIgnoringCase("JOHN@EXAMPLE.COM"))));
    }
}
```

### Custom argument matchers

#### Создание custom matchers
```java
@SpringBootTest
public class CustomMatchersTest {

    @Mock
    private AuditService auditService;

    @InjectMocks
    private UserService userService;

    @Test
    void testAuditLoggingWithCustomMatcher() {
        User user = new User("audit@example.com", "Audit User");

        // Custom matcher for audit events
        when(auditService.logEvent(argThat(isValidAuditEvent()))).thenReturn(true);

        userService.createUser("audit@example.com", "Audit User");

        verify(auditService).logEvent(argThat(allOf(
            hasProperty("action", equalTo("USER_CREATED")),
            hasProperty("userId", notNullValue()),
            hasProperty("timestamp", notNullValue())
        )));
    }

    @Test
    void testEmailValidationWithCustomMatcher() {
        // Custom matcher for email validation
        when(emailService.sendWelcomeEmail(argThat(isValidEmail())))
            .thenReturn(true);

        userService.createUser("valid@example.com", "Valid User");

        verify(emailService).sendWelcomeEmail(argThat(matchesEmailPattern()));
    }

    // Custom ArgumentMatcher implementations
    private ArgumentMatcher<AuditEvent> isValidAuditEvent() {
        return event -> event != null &&
                       event.getAction() != null &&
                       event.getTimestamp() != null;
    }

    private ArgumentMatcher<String> isValidEmail() {
        return email -> email != null &&
                       email.contains("@") &&
                       email.length() >= 5;
    }

    private ArgumentMatcher<String> matchesEmailPattern() {
        return email -> Pattern.matches("^[\\w.-]+@[\\w.-]+\\.[a-zA-Z]{2,}$", email);
    }
}

// ArgumentMatcher for complex objects
public class OrderMatcher implements ArgumentMatcher<Order> {

    private final BigDecimal expectedAmount;
    private final String expectedCurrency;

    public OrderMatcher(BigDecimal amount, String currency) {
        this.expectedAmount = amount;
        this.expectedCurrency = currency;
    }

    @Override
    public boolean matches(Order order) {
        return order != null &&
               Objects.equals(order.getTotalAmount(), expectedAmount) &&
               Objects.equals(order.getCurrency(), expectedCurrency) &&
               order.getItems() != null &&
               !order.getItems().isEmpty();
    }

    public static Order orderWith(BigDecimal amount, String currency) {
        return argThat(new OrderMatcher(amount, currency));
    }
}
```

### Complex matchers

#### Combining matchers
```java
@SpringBootTest
public class ComplexMatchersTest {

    @Mock
    private NotificationService notificationService;

    @InjectMocks
    private OrderService orderService;

    @Test
    void testNotificationWithComplexMatchers() {
        Order order = new Order(BigDecimal.valueOf(150.0));
        order.setPriority(OrderPriority.HIGH);

        // Complex matcher combination
        when(notificationService.sendNotification(
            argThat(isHighPriorityOrder()),
            argThat(isValidRecipient()),
            argThat(containsOrderDetails())
        )).thenReturn(true);

        orderService.notifyOrderCreated(order);

        verify(notificationService).sendNotification(
            argThat(hasProperty("priority", equalTo(OrderPriority.HIGH))),
            argThat(anyOf(
                containsString("@company.com"),
                containsString("@partner.com")
            )),
            argThat(allOf(
                containsString("Order ID:"),
                containsString("$150.00")
            ))
        );
    }

    private ArgumentMatcher<Order> isHighPriorityOrder() {
        return order -> order != null &&
                       order.getPriority() == OrderPriority.HIGH &&
                       order.getTotalAmount().compareTo(BigDecimal.valueOf(100.0)) > 0;
    }

    private ArgumentMatcher<String> isValidRecipient() {
        return recipient -> recipient != null &&
                           (recipient.endsWith("@company.com") ||
                            recipient.endsWith("@partner.com"));
    }

    private ArgumentMatcher<String> containsOrderDetails() {
        return message -> message != null &&
                         message.contains("Order ID:") &&
                         message.contains("$");
    }
}
```

## Verification modes

### Advanced verification

#### Verification modes
```java
@SpringBootTest
public class VerificationModesTest {

    @Mock
    private CacheService cacheService;

    @InjectMocks
    private UserService userService;

    @Test
    void testCacheInteractionVerification() {
        // Test multiple calls
        userService.getUser(1L);
        userService.getUser(1L);
        userService.getUser(2L);

        // Verify exact number of calls
        verify(cacheService, times(2)).get("user:1");
        verify(cacheService, times(1)).get("user:2");

        // Verify at least once
        verify(cacheService, atLeastOnce()).get(anyString());

        // Verify at most
        verify(cacheService, atMost(3)).get(anyString());
    }

    @Test
    void testVerificationWithTimeout() {
        // For async operations
        userService.refreshCacheAsync();

        // Verify with timeout
        verify(cacheService, timeout(5000)).refresh();
        verify(cacheService, timeout(5000).times(1)).refresh();
    }

    @Test
    void testNoMoreInteractions() {
        userService.getUser(1L);

        verify(cacheService).get("user:1");

        // Verify no more interactions with cache service
        verifyNoMoreInteractions(cacheService);
    }

    @Test
    void testInOrderVerification() {
        userService.getUser(1L);

        InOrder inOrder = inOrder(cacheService);

        // Verify order of operations
        inOrder.verify(cacheService).get("user:1");
        inOrder.verify(cacheService, never()).put(anyString(), any());
    }
}
```

### Custom verification

#### Verification collectors
```java
@SpringBootTest
public class CustomVerificationTest {

    @Mock
    private MetricsService metricsService;

    @InjectMocks
    private ApiService apiService;

    @Test
    void testMetricsCollection() {
        // Perform operations
        apiService.handleRequest("GET", "/users");
        apiService.handleRequest("POST", "/users");
        apiService.handleRequest("GET", "/orders");

        // Custom verification logic
        verifyMetricsRecorded();
    }

    private void verifyMetricsRecorded() {
        // Verify request count metrics
        verify(metricsService, times(3)).recordRequest(anyString(), anyString());

        // Verify specific method calls
        verify(metricsService).recordRequest(eq("GET"), eq("/users"));
        verify(metricsService).recordRequest(eq("POST"), eq("/users"));
        verify(metricsService).recordRequest(eq("GET"), eq("/orders"));

        // Verify response time recording
        verify(metricsService, atLeast(3)).recordResponseTime(anyLong());
    }

    @Test
    void testErrorHandlingMetrics() {
        // Simulate error scenario
        doThrow(new RuntimeException("Database error"))
            .when(metricsService).recordRequest(anyString(), anyString());

        assertThrows(RuntimeException.class, () -> {
            apiService.handleRequest("GET", "/users");
        });

        // Verify error metrics
        verify(metricsService).recordError("Database error");
        verify(metricsService, never()).recordSuccess();
    }
}
```

## Spies и partial mocking

### Spies

#### Method-level spying
```java
@SpringBootTest
public class SpiesTest {

    @Spy
    private RealUserRepository realUserRepository = new RealUserRepository();

    @Mock
    private EmailService emailService;

    @InjectMocks
    private UserService userService;

    @Test
    void testPartialMockingWithSpy() {
        User user = new User("spy@example.com", "Spy User");

        // Spy on real method
        doReturn(user).when(realUserRepository).save(any(User.class));

        // Mock email service
        doNothing().when(emailService).sendWelcomeEmail(anyString());

        // Test
        User created = userService.createUser("spy@example.com", "Spy User");

        assertNotNull(created);
        assertEquals("spy@example.com", created.getEmail());

        // Verify spy interaction
        verify(realUserRepository).save(any(User.class));
        verify(emailService).sendWelcomeEmail("spy@example.com");
    }

    @Test
    void testSpyRealMethodCalls() {
        // Call real method but spy on it
        userService.getUserCount();

        // Verify the real method was called
        verify(realUserRepository).count();
    }

    @Test
    void testSpyWithCustomBehavior() {
        // Spy with custom behavior for specific calls
        doReturn(100L).when(realUserRepository).count();
        doCallRealMethod().when(realUserRepository).existsByEmail(anyString());

        // Test
        long count = userService.getUserCount();
        boolean exists = userService.userExists("test@example.com");

        assertEquals(100L, count);
        verify(realUserRepository).count();
        verify(realUserRepository).existsByEmail("test@example.com");
    }
}
```

### Partial mocking

#### Selective method mocking
```java
@SpringBootTest
public class PartialMockingTest {

    @Spy
    @InjectMocks
    private ComplexService complexService;

    @Mock
    private ExternalApiClient apiClient;

    @Test
    void testPartialMethodMocking() {
        // Mock only specific methods
        doReturn("mocked response").when(complexService).callExternalApi();
        doReturn(true).when(complexService).validateInput(anyString());

        // Call real methods for others
        doCallRealMethod().when(complexService).processData(anyString());

        String result = complexService.processRequest("test data");

        assertEquals("Processed: mocked response", result);

        verify(complexService).callExternalApi();
        verify(complexService).validateInput("test data");
        verify(complexService).processData("test data");
    }

    @Test
    void testConditionalMocking() {
        // Mock based on input parameters
        doReturn("special").when(complexService).processData(eq("special input"));
        doCallRealMethod().when(complexService).processData(anyString());

        assertEquals("SPECIAL", complexService.processRequest("special input"));
        assertEquals("Processed: real", complexService.processRequest("normal input"));
    }
}

// Example service with complex logic
@Service
public class ComplexService {

    @Autowired
    private ExternalApiClient apiClient;

    public String processRequest(String input) {
        if (!validateInput(input)) {
            throw new IllegalArgumentException("Invalid input");
        }

        String apiResponse = callExternalApi();
        String processed = processData(input);

        return processed + ": " + apiResponse;
    }

    public boolean validateInput(String input) {
        return input != null && !input.isEmpty();
    }

    public String callExternalApi() {
        // Real external API call
        return apiClient.callApi();
    }

    public String processData(String data) {
        // Real data processing
        return "Processed: " + data.toLowerCase();
    }
}
```

## Custom answers

### Answer implementations

#### Dynamic answers
```java
@SpringBootTest
public class CustomAnswersTest {

    @Mock
    private PaymentGateway paymentGateway;

    @InjectMocks
    private PaymentService paymentService;

    @Test
    void testDynamicPaymentResponse() {
        // Custom answer for payment processing
        when(paymentGateway.processPayment(any(PaymentRequest.class)))
            .thenAnswer(invocation -> {
                PaymentRequest request = invocation.getArgument(0);

                // Simulate payment processing logic
                if (request.getAmount().compareTo(BigDecimal.valueOf(100.0)) > 0) {
                    return new PaymentResponse("DECLINED", "Amount too high");
                } else if ("invalid".equals(request.getCardNumber())) {
                    return new PaymentResponse("ERROR", "Invalid card");
                } else {
                    return new PaymentResponse("APPROVED", "Payment successful",
                        "txn_" + System.currentTimeMillis());
                }
            });

        // Test different scenarios
        assertEquals("APPROVED", paymentService.processPayment(BigDecimal.valueOf(50.0), "valid").getStatus());
        assertEquals("DECLINED", paymentService.processPayment(BigDecimal.valueOf(150.0), "valid").getStatus());
        assertEquals("ERROR", paymentService.processPayment(BigDecimal.valueOf(50.0), "invalid").getStatus());
    }

    @Test
    void testStatefulMockBehavior() {
        List<String> callLog = new ArrayList<>();

        when(paymentGateway.getStatus(anyString()))
            .thenAnswer(invocation -> {
                String transactionId = invocation.getArgument(0);
                callLog.add("Status check for: " + transactionId);

                // Simulate stateful behavior
                if (transactionId.startsWith("success_")) {
                    return "COMPLETED";
                } else if (transactionId.startsWith("pending_")) {
                    return "PENDING";
                } else {
                    return "UNKNOWN";
                }
            });

        // Test stateful interactions
        assertEquals("COMPLETED", paymentGateway.getStatus("success_123"));
        assertEquals("PENDING", paymentGateway.getStatus("pending_456"));
        assertEquals("UNKNOWN", paymentGateway.getStatus("failed_789"));

        assertEquals(3, callLog.size());
        assertTrue(callLog.get(0).contains("success_123"));
    }

    @Test
    void testConsecutiveCalls() {
        // Different responses for consecutive calls
        when(paymentGateway.getNextId())
            .thenReturn("id1", "id2", "id3");

        assertEquals("id1", paymentGateway.getNextId());
        assertEquals("id2", paymentGateway.getNextId());
        assertEquals("id3", paymentGateway.getNextId());
        assertEquals("id3", paymentGateway.getNextId()); // Last value repeats
    }
}
```

### Callback answers
```java
@SpringBootTest
public class CallbackAnswersTest {

    @Mock
    private AuditService auditService;

    @InjectMocks
    private UserService userService;

    @Test
    void testAuditCallback() {
        List<AuditEvent> capturedEvents = new ArrayList<>();

        // Capture audit events using callback
        doAnswer(invocation -> {
            AuditEvent event = invocation.getArgument(0);
            capturedEvents.add(event);

            // Simulate async processing
            System.out.println("Auditing: " + event.getAction() +
                             " for user: " + event.getUserId());

            return true;
        }).when(auditService).logEvent(any(AuditEvent.class));

        // Perform operations
        userService.createUser("audit@example.com", "Audit User");
        userService.updateUser(new User(1L, "updated@example.com", "Updated User"));
        userService.deleteUser(1L);

        // Verify captured events
        assertEquals(3, capturedEvents.size());

        assertEquals("USER_CREATED", capturedEvents.get(0).getAction());
        assertEquals("USER_UPDATED", capturedEvents.get(1).getAction());
        assertEquals("USER_DELETED", capturedEvents.get(2).getAction());

        // Verify all events have user ID
        capturedEvents.forEach(event ->
            assertNotNull(event.getUserId()));
    }

    @Test
    void testValidationCallback() {
        List<String> validationErrors = new ArrayList<>();

        when(auditService.validateEvent(any(AuditEvent.class)))
            .thenAnswer(invocation -> {
                AuditEvent event = invocation.getArgument(0);

                if (event.getUserId() == null) {
                    validationErrors.add("User ID is required");
                    return false;
                }

                if (event.getAction() == null) {
                    validationErrors.add("Action is required");
                    return false;
                }

                return true;
            });

        // Test valid event
        AuditEvent validEvent = new AuditEvent("USER_CREATED", 1L);
        assertTrue(auditService.validateEvent(validEvent));
        assertTrue(validationErrors.isEmpty());

        // Test invalid event
        AuditEvent invalidEvent = new AuditEvent(null, null);
        assertFalse(auditService.validateEvent(invalidEvent));
        assertEquals(2, validationErrors.size());
    }
}
```

## BDDMockito

### BDD syntax

#### Given-When-Then pattern
```java
@SpringBootTest
public class BDDMockitoTest {

    @Mock
    private ProductRepository productRepository;

    @InjectMocks
    private ProductService productService;

    @Test
    void testProductCreation() {
        Product product = new Product("Test Product", BigDecimal.valueOf(29.99));

        // Given
        given(productRepository.save(any(Product.class))).willReturn(product);
        given(productRepository.existsByName("Test Product")).willReturn(false);

        // When
        Product created = productService.createProduct("Test Product", BigDecimal.valueOf(29.99));

        // Then
        then(productRepository).should().save(any(Product.class));
        then(productRepository).should().existsByName("Test Product");
        assertThat(created.getName()).isEqualTo("Test Product");
    }

    @Test
    void testProductUpdate() {
        Product existing = new Product(1L, "Old Name", BigDecimal.valueOf(19.99));
        Product updated = new Product(1L, "New Name", BigDecimal.valueOf(24.99));

        // Given
        given(productRepository.findById(1L)).willReturn(Optional.of(existing));
        given(productRepository.save(any(Product.class))).willReturn(updated);

        // When
        Product result = productService.updateProduct(1L, "New Name", BigDecimal.valueOf(24.99));

        // Then
        then(productRepository).should().findById(1L);
        then(productRepository).should().save(any(Product.class));
        assertThat(result.getName()).isEqualTo("New Name");
        assertThat(result.getPrice()).isEqualTo(BigDecimal.valueOf(24.99));
    }

    @Test
    void testProductDeletion() {
        // Given
        given(productRepository.existsById(1L)).willReturn(true);
        willDoNothing().given(productRepository).deleteById(1L);

        // When
        productService.deleteProduct(1L);

        // Then
        then(productRepository).should().existsById(1L);
        then(productRepository).should().deleteById(1L);
    }
}
```

### BDD verification

#### Readable assertions
```java
@SpringBootTest
public class BDDVerificationTest {

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private PaymentService paymentService;

    @InjectMocks
    private OrderService orderService;

    @Test
    void testOrderProcessingFlow() {
        Order order = new Order(BigDecimal.valueOf(100.0));
        PaymentResult paymentResult = new PaymentResult(true, "SUCCESS");

        // Given
        given(orderRepository.save(any(Order.class))).willReturn(order);
        given(paymentService.processPayment(any(BigDecimal.class))).willReturn(paymentResult);

        // When
        Order processedOrder = orderService.processOrder(order);

        // Then
        then(orderRepository).should().save(order);
        then(paymentService).should().processPayment(BigDecimal.valueOf(100.0));
        assertThat(processedOrder.getStatus()).isEqualTo(OrderStatus.PROCESSING);
    }

    @Test
    void testOrderFailureScenarios() {
        Order order = new Order(BigDecimal.valueOf(1000.0));

        // Given - payment will fail for high amounts
        given(paymentService.processPayment(BigDecimal.valueOf(1000.0)))
            .willReturn(new PaymentResult(false, "DECLINED"));

        // When & Then
        assertThatThrownBy(() -> orderService.processOrder(order))
            .isInstanceOf(OrderProcessingException.class)
            .hasMessageContaining("DECLINED");

        then(orderRepository).should(never()).save(any(Order.class));
        then(paymentService).should().processPayment(BigDecimal.valueOf(1000.0));
    }

    @Test
    void testOrderVerification() {
        Order order = new Order(BigDecimal.valueOf(50.0));

        // When
        orderService.validateOrder(order);

        // Then - verify interactions
        then(orderRepository).shouldHaveNoInteractions();
        then(paymentService).shouldHaveNoInteractions();
    }
}
```

## Argument captors

### Basic captors

#### Capturing method arguments
```java
@SpringBootTest
public class ArgumentCaptorsTest {

    @Mock
    private EmailService emailService;

    @InjectMocks
    private UserService userService;

    @Test
    void testEmailSendingWithCaptor() {
        ArgumentCaptor<String> emailCaptor = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<String> subjectCaptor = ArgumentCaptor.forClass(String.class);

        // Setup mock
        doNothing().when(emailService).sendEmail(anyString(), anyString(), anyString());

        // Execute
        userService.sendWelcomeEmail("user@example.com", "John Doe");

        // Verify and capture
        verify(emailService).sendEmail(emailCaptor.capture(), subjectCaptor.capture(), anyString());

        assertEquals("user@example.com", emailCaptor.getValue());
        assertEquals("Welcome John Doe!", subjectCaptor.getValue());
    }

    @Test
    void testMultipleCallsCapturing() {
        ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);

        // Setup mock for multiple calls
        doNothing().when(emailService).sendWelcomeEmail(any(User.class));

        // Execute multiple times
        userService.sendBulkWelcomeEmails(List.of(
            new User("user1@example.com", "User 1"),
            new User("user2@example.com", "User 2"),
            new User("user3@example.com", "User 3")
        ));

        // Verify all calls
        verify(emailService, times(3)).sendWelcomeEmail(userCaptor.capture());

        List<User> capturedUsers = userCaptor.getAllValues();
        assertEquals(3, capturedUsers.size());
        assertEquals("user1@example.com", capturedUsers.get(0).getEmail());
        assertEquals("user2@example.com", capturedUsers.get(1).getEmail());
        assertEquals("user3@example.com", capturedUsers.get(2).getEmail());
    }

    @Test
    void testComplexObjectCapturing() {
        ArgumentCaptor<Order> orderCaptor = ArgumentCaptor.forClass(Order.class);
        ArgumentCaptor<PaymentRequest> paymentCaptor = ArgumentCaptor.forClass(PaymentRequest.class);

        Order order = new Order(BigDecimal.valueOf(100.0));
        order.setItems(List.of(new OrderItem("item1", BigDecimal.valueOf(50.0), 2)));

        // Execute
        orderService.processOrderWithPayment(order);

        // Verify and capture complex objects
        verify(orderRepository).save(orderCaptor.capture());
        verify(paymentService).processPayment(paymentCaptor.capture());

        Order capturedOrder = orderCaptor.getValue();
        assertEquals(BigDecimal.valueOf(100.0), capturedOrder.getTotalAmount());
        assertEquals(1, capturedOrder.getItems().size());

        PaymentRequest capturedPayment = paymentCaptor.getValue();
        assertEquals(BigDecimal.valueOf(100.0), capturedPayment.getAmount());
        assertNotNull(capturedPayment.getOrderId());
    }
}
```

### Advanced captors

#### Generic captors
```java
@SpringBootTest
public class AdvancedCaptorsTest {

    @Mock
    private AuditService auditService;

    @InjectMocks
    private GenericService genericService;

    @Test
    void testGenericMethodCapturing() {
        // For generic methods, use ? extends Type
        ArgumentCaptor<? extends AuditEvent> eventCaptor = ArgumentCaptor.forClass(AuditEvent.class);

        genericService.performAction("test", 123);

        verify(auditService).logEvent(eventCaptor.capture());

        AuditEvent capturedEvent = eventCaptor.getValue();
        assertEquals("ACTION_PERFORMED", capturedEvent.getAction());
        assertEquals("test", capturedEvent.getEntityId());
    }

    @Test
    void testCollectionCapturing() {
        ArgumentCaptor<List<String>> listCaptor = ArgumentCaptor.forClass(List.class);

        genericService.processBatch(List.of("item1", "item2", "item3"));

        verify(auditService).logBatchOperation(listCaptor.capture(), anyString());

        List<String> capturedList = listCaptor.getValue();
        assertEquals(3, capturedList.size());
        assertTrue(capturedList.contains("item1"));
    }

    @Test
    void testMapCapturing() {
        ArgumentCaptor<Map<String, Object>> mapCaptor = ArgumentCaptor.forClass(Map.class);

        Map<String, Object> metadata = Map.of(
            "userId", 123L,
            "action", "UPDATE",
            "timestamp", Instant.now()
        );

        genericService.processWithMetadata("entity1", metadata);

        verify(auditService).logWithMetadata(anyString(), mapCaptor.capture());

        Map<String, Object> capturedMetadata = mapCaptor.getValue();
        assertEquals(123L, capturedMetadata.get("userId"));
        assertEquals("UPDATE", capturedMetadata.get("action"));
        assertNotNull(capturedMetadata.get("timestamp"));
    }
}
```

## Mock injection

### Constructor injection

#### @InjectMocks with constructors
```java
@SpringBootTest
public class ConstructorInjectionTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private EmailService emailService;

    @Mock
    private CacheService cacheService;

    @InjectMocks
    private UserService userService;

    @Test
    void testConstructorInjection() {
        User user = new User("constructor@example.com", "Constructor User");

        when(userRepository.save(any(User.class))).thenReturn(user);
        when(cacheService.get(anyString())).thenReturn(null);
        doNothing().when(cacheService).put(anyString(), any());

        User created = userService.createUser("constructor@example.com", "Constructor User");

        assertNotNull(created);
        verify(userRepository).save(any(User.class));
        verify(cacheService).put(anyString(), any(User.class));
    }

    @Test
    void testMultipleConstructorParameters() {
        // Test with service that has multiple dependencies
        when(userRepository.findAll()).thenReturn(List.of(
            new User("user1@example.com", "User 1"),
            new User("user2@example.com", "User 2")
        ));

        List<User> users = userService.getAllUsers();

        assertEquals(2, users.size());
        verify(userRepository).findAll();
        verify(cacheService).get("all_users"); // Cache check
    }
}
```

### Field injection

#### @InjectMocks with fields
```java
@SpringBootTest
public class FieldInjectionTest {

    @Mock
    private PaymentGateway paymentGateway;

    @Mock
    private FraudDetectionService fraudService;

    @InjectMocks
    private PaymentProcessingService paymentService;

    @Test
    void testFieldInjection() {
        PaymentRequest request = new PaymentRequest(BigDecimal.valueOf(100.0), "4111111111111111");

        when(fraudService.checkFraud(any(PaymentRequest.class))).thenReturn(FraudResult.CLEAN);
        when(paymentGateway.process(any(PaymentRequest.class)))
            .thenReturn(new PaymentResponse("APPROVED", "Success"));

        PaymentResult result = paymentService.processPayment(request);

        assertTrue(result.isApproved());
        verify(fraudService).checkFraud(request);
        verify(paymentGateway).process(request);
    }

    @Test
    void testFieldInjectionWithExceptions() {
        PaymentRequest request = new PaymentRequest(BigDecimal.valueOf(100.0), "invalid");

        when(fraudService.checkFraud(any(PaymentRequest.class))).thenReturn(FraudResult.SUSPICIOUS);

        assertThrows(FraudException.class, () -> {
            paymentService.processPayment(request);
        });

        verify(fraudService).checkFraud(request);
        verify(paymentGateway, never()).process(any(PaymentRequest.class));
    }
}
```

### Setter injection

#### @InjectMocks with setters
```java
@SpringBootTest
public class SetterInjectionTest {

    @Mock
    private MetricsCollector metricsCollector;

    @Mock
    private AlertService alertService;

    @InjectMocks
    private MonitoringService monitoringService;

    @Test
    void testSetterInjection() {
        SystemMetrics metrics = new SystemMetrics(80.0, 70.0, 1000);

        when(metricsCollector.collect()).thenReturn(metrics);
        doNothing().when(alertService).sendAlert(anyString(), anyString());

        monitoringService.checkSystemHealth();

        verify(metricsCollector).collect();
        verify(alertService, never()).sendAlert(anyString(), anyString()); // System is healthy
    }

    @Test
    void testSetterInjectionWithAlert() {
        SystemMetrics metrics = new SystemMetrics(95.0, 90.0, 1500); // High load

        when(metricsCollector.collect()).thenReturn(metrics);
        doNothing().when(alertService).sendAlert(anyString(), anyString());

        monitoringService.checkSystemHealth();

        verify(metricsCollector).collect();
        verify(alertService).sendAlert(eq("High CPU Usage"), anyString());
        verify(alertService).sendAlert(eq("High Memory Usage"), anyString());
    }
}
```

## Spring Boot integration

### @MockBean vs @Mock

#### @MockBean for Spring context
```java
@SpringBootTest
public class MockBeanTest {

    @MockBean
    private UserRepository userRepository; // Replaces bean in Spring context

    @Autowired
    private UserService userService; // Real service with mock dependency

    @Test
    void testWithMockBean() {
        User user = new User("mockbean@example.com", "MockBean User");

        when(userRepository.save(any(User.class))).thenReturn(user);

        User created = userService.createUser("mockbean@example.com", "MockBean User");

        assertNotNull(created);
        verify(userRepository).save(any(User.class));
    }

    @Test
    void testRepositoryInteraction() {
        List<User> users = List.of(
            new User("user1@example.com", "User 1"),
            new User("user2@example.com", "User 2")
        );

        when(userRepository.findAll()).thenReturn(users);

        List<User> result = userService.getAllUsers();

        assertEquals(2, result.size());
        verify(userRepository).findAll();
    }
}
```

### @SpyBean

#### Partial mocking in Spring context
```java
@SpringBootTest
public class SpyBeanTest {

    @SpyBean
    private UserRepository userRepository; // Spy on real bean

    @Autowired
    private UserService userService;

    @Test
    void testWithSpyBean() {
        User user = new User("spybean@example.com", "SpyBean User");

        // Spy on real method
        doReturn(user).when(userRepository).save(any(User.class));

        User created = userService.createUser("spybean@example.com", "SpyBean User");

        assertNotNull(created);

        // Verify real method was called
        verify(userRepository).save(any(User.class));
    }

    @Test
    void testPartialSpying() {
        // Spy on specific methods
        doReturn(42L).when(userRepository).count();

        long count = userService.getUserCount();

        assertEquals(42L, count);
        verify(userRepository).count();
    }
}
```

### Test slices with mocking

#### @WebMvcTest with mocks
```java
@WebMvcTest(UserController.class)
public class WebMvcMockTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService userService;

    @Test
    void testControllerWithMockedService() throws Exception {
        User user = new User(1L, "mock@example.com", "Mock User");

        when(userService.findById(1L)).thenReturn(user);

        mockMvc.perform(get("/api/users/1"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.email").value("mock@example.com"))
            .andExpect(jsonPath("$.name").value("Mock User"));

        verify(userService).findById(1L);
    }

    @Test
    void testControllerErrorHandling() throws Exception {
        when(userService.findById(999L)).thenThrow(new UserNotFoundException("User not found"));

        mockMvc.perform(get("/api/users/999"))
            .andExpect(status().isNotFound())
            .andExpect(jsonPath("$.error").value("User not found"));

        verify(userService).findById(999L);
    }
}
```

## Testing void methods

### Void method verification

#### Verifying void method calls
```java
@SpringBootTest
public class VoidMethodsTest {

    @Mock
    private AuditService auditService;

    @Mock
    private EmailService emailService;

    @InjectMocks
    private UserNotificationService notificationService;

    @Test
    void testVoidMethodCalls() {
        User user = new User("void@example.com", "Void User");

        // Setup mocks for void methods
        doNothing().when(auditService).logUserAction(anyString(), anyString());
        doNothing().when(emailService).sendWelcomeEmail(anyString());

        // Execute
        notificationService.notifyUserCreated(user);

        // Verify void method calls
        verify(auditService).logUserAction("USER_CREATED", "void@example.com");
        verify(emailService).sendWelcomeEmail("void@example.com");
    }

    @Test
    void testVoidMethodWithExceptions() {
        User user = new User("error@example.com", "Error User");

        // Setup mock to throw exception
        doThrow(new EmailException("SMTP error")).when(emailService).sendWelcomeEmail(anyString());

        // Execute and verify exception handling
        assertThrows(NotificationException.class, () -> {
            notificationService.notifyUserCreated(user);
        });

        // Verify calls still happened
        verify(auditService).logUserAction("USER_CREATED", "error@example.com");
        verify(emailService).sendWelcomeEmail("error@example.com");
    }

    @Test
    void testVoidMethodCallOrder() {
        User user = new User("order@example.com", "Order User");

        doNothing().when(auditService).logUserAction(anyString(), anyString());
        doNothing().when(emailService).sendWelcomeEmail(anyString());

        notificationService.notifyUserCreated(user);

        // Verify order of calls
        InOrder inOrder = inOrder(auditService, emailService);
        inOrder.verify(auditService).logUserAction("USER_CREATED", "order@example.com");
        inOrder.verify(emailService).sendWelcomeEmail("order@example.com");
    }
}
```

### Void methods with callbacks

#### Testing async void methods
```java
@SpringBootTest
public class AsyncVoidMethodsTest {

    @Mock
    private AsyncEmailService asyncEmailService;

    @InjectMocks
    private AsyncNotificationService notificationService;

    @Test
    void testAsyncVoidMethod() {
        User user = new User("async@example.com", "Async User");

        // Setup mock for async void method
        doNothing().when(asyncEmailService).sendWelcomeEmailAsync(anyString());

        // Execute
        notificationService.notifyUserCreatedAsync(user);

        // Verify async call was initiated
        verify(asyncEmailService).sendWelcomeEmailAsync("async@example.com");

        // Note: For real async testing, use Awaitility or similar
    }

    @Test
    void testAsyncVoidMethodWithAnswer() {
        User user = new User("callback@example.com", "Callback User");

        // Setup mock with callback verification
        doAnswer(invocation -> {
            String email = invocation.getArgument(0);
            System.out.println("Async email sent to: " + email);
            // Simulate async processing
            return null; // void method
        }).when(asyncEmailService).sendWelcomeEmailAsync(anyString());

        notificationService.notifyUserCreatedAsync(user);

        verify(asyncEmailService).sendWelcomeEmailAsync("callback@example.com");
    }
}
```

## Exception testing

### Expected exceptions

#### Testing for exceptions
```java
@SpringBootTest
public class ExceptionTestingTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserService userService;

    @Test
    void testUserCreationWithExistingEmail() {
        // Setup mock to simulate existing user
        when(userRepository.existsByEmail("existing@example.com")).thenReturn(true);

        // Test for exception
        UserCreationException exception = assertThrows(UserCreationException.class, () -> {
            userService.createUser("existing@example.com", "Test User");
        });

        assertEquals("Email already exists", exception.getMessage());
        verify(userRepository).existsByEmail("existing@example.com");
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void testPaymentProcessingFailure() {
        PaymentRequest request = new PaymentRequest(BigDecimal.valueOf(100.0), "invalid");

        when(paymentService.validatePayment(request)).thenReturn(false);

        PaymentException exception = assertThrows(PaymentException.class, () -> {
            paymentService.processPayment(request);
        });

        assertTrue(exception.getMessage().contains("validation failed"));
        verify(paymentService).validatePayment(request);
        verify(paymentGateway, never()).charge(any());
    }

    @Test
    void testExceptionWithCause() {
        when(databaseService.connect()).thenThrow(new SQLException("Connection timeout"));

        DatabaseException exception = assertThrows(DatabaseException.class, () -> {
            dataService.initializeConnection();
        });

        assertEquals("Database connection failed", exception.getMessage());
        assertTrue(exception.getCause() instanceof SQLException);
        assertEquals("Connection timeout", exception.getCause().getMessage());
    }
}
```

### Exception verification

#### Verifying exception details
```java
@SpringBootTest
public class ExceptionVerificationTest {

    @Mock
    private ExternalApiClient apiClient;

    @InjectMocks
    private ApiService apiService;

    @Test
    void testApiCallExceptionHandling() {
        // Setup mock to throw specific exception
        when(apiClient.callApi(anyString()))
            .thenThrow(new ApiException("Rate limit exceeded", 429));

        // Test exception handling
        ApiCallException exception = assertThrows(ApiCallException.class, () -> {
            apiService.callExternalApi("test-endpoint");
        });

        assertAll(
            () -> assertEquals("API call failed", exception.getMessage()),
            () -> assertEquals(429, exception.getStatusCode()),
            () -> assertTrue(exception.getCause() instanceof ApiException),
            () -> assertEquals("Rate limit exceeded", exception.getCause().getMessage())
        );

        verify(apiClient).callApi("test-endpoint");
    }

    @Test
    void testMultipleExceptionScenarios() {
        // Test different exception scenarios
        when(apiClient.callApi("timeout"))
            .thenThrow(new TimeoutException("Request timeout"));

        when(apiClient.callApi("server-error"))
            .thenThrow(new ServerException("Internal server error", 500));

        // Test timeout scenario
        assertThrows(TimeoutException.class, () -> {
            apiService.callExternalApi("timeout");
        });

        // Test server error scenario
        ServerException serverException = assertThrows(ServerException.class, () -> {
            apiService.callExternalApi("server-error");
        });

        assertEquals(500, serverException.getStatusCode());
    }

    @Test
    void testExceptionChaining() {
        // Setup chained exceptions
        RuntimeException rootCause = new RuntimeException("Root cause");
        ApiException apiException = new ApiException("API error", rootCause);
        ServiceException serviceException = new ServiceException("Service failed", apiException);

        when(apiClient.callApi(anyString())).thenThrow(serviceException);

        // Test exception chaining
        ServiceException caughtException = assertThrows(ServiceException.class, () -> {
            apiService.callExternalApi("test");
        });

        // Verify exception chain
        assertEquals("Service failed", caughtException.getMessage());
        assertTrue(caughtException.getCause() instanceof ApiException);

        ApiException apiCause = (ApiException) caughtException.getCause();
        assertEquals("API error", apiCause.getMessage());
        assertEquals("Root cause", apiCause.getCause().getMessage());
    }
}
```

## Async mocking

### CompletableFuture mocking

#### Testing async operations
```java
@SpringBootTest
public class AsyncMockingTest {

    @Mock
    private AsyncUserRepository asyncUserRepository;

    @InjectMocks
    private AsyncUserService asyncUserService;

    @Test
    void testAsyncUserCreation() {
        User user = new User("async@example.com", "Async User");
        CompletableFuture<User> future = CompletableFuture.completedFuture(user);

        when(asyncUserRepository.saveAsync(any(User.class))).thenReturn(future);

        CompletableFuture<User> result = asyncUserService.createUserAsync("async@example.com", "Async User");

        // Wait for completion and verify
        User created = result.join();

        assertNotNull(created);
        assertEquals("async@example.com", created.getEmail());
        verify(asyncUserRepository).saveAsync(any(User.class));
    }

    @Test
    void testAsyncOperationFailure() {
        CompletableFuture<User> failedFuture = CompletableFuture.failedFuture(
            new DataAccessException("Database error"));

        when(asyncUserRepository.saveAsync(any(User.class))).thenReturn(failedFuture);

        CompletableFuture<User> result = asyncUserService.createUserAsync("fail@example.com", "Fail User");

        // Verify exception handling
        assertThrows(CompletionException.class, result::join);

        try {
            result.join();
            fail("Expected exception");
        } catch (CompletionException e) {
            assertTrue(e.getCause() instanceof DataAccessException);
            assertEquals("Database error", e.getCause().getMessage());
        }
    }

    @Test
    void testAsyncChaining() {
        User savedUser = new User("chain@example.com", "Chain User");
        CompletableFuture<Void> emailFuture = CompletableFuture.completedFuture(null);

        when(asyncUserRepository.saveAsync(any(User.class)))
            .thenReturn(CompletableFuture.completedFuture(savedUser));

        when(asyncEmailService.sendWelcomeEmailAsync(anyString()))
            .thenReturn(emailFuture);

        CompletableFuture<User> result = asyncUserService.createUserWithEmailAsync(
            "chain@example.com", "Chain User");

        User created = result.join();

        assertNotNull(created);
        verify(asyncUserRepository).saveAsync(any(User.class));
        verify(asyncEmailService).sendWelcomeEmailAsync("chain@example.com");
    }
}
```

### Delayed responses

#### Testing timeouts and delays
```java
@SpringBootTest
public class DelayedMockingTest {

    @Mock
    private SlowExternalService slowService;

    @InjectMocks
    private TimeoutHandlingService timeoutService;

    @Test
    void testTimeoutHandling() {
        // Setup mock with delay
        when(slowService.callWithDelay(anyString()))
            .thenAnswer(invocation -> {
                // Simulate delay
                Thread.sleep(2000); // 2 seconds delay
                return "delayed response";
            });

        // Test timeout handling
        assertThrows(TimeoutException.class, () -> {
            timeoutService.callWithTimeout("test", 1000); // 1 second timeout
        });

        verify(slowService).callWithDelay("test");
    }

    @Test
    void testSuccessfulDelayedCall() {
        // Setup mock with acceptable delay
        when(slowService.callWithDelay(anyString()))
            .thenAnswer(invocation -> {
                Thread.sleep(500); // 0.5 seconds delay
                return "timely response";
            });

        // Test successful call
        String result = timeoutService.callWithTimeout("test", 2000); // 2 second timeout

        assertEquals("timely response", result);
        verify(slowService).callWithDelay("test");
    }

    @Test
    void testConcurrentDelayedCalls() throws InterruptedException {
        // Setup concurrent mock responses
        AtomicInteger callCount = new AtomicInteger(0);

        when(slowService.callWithDelay(anyString()))
            .thenAnswer(invocation -> {
                int count = callCount.incrementAndGet();
                Thread.sleep(100 * count); // Increasing delay
                return "response-" + count;
            });

        // Test concurrent calls
        ExecutorService executor = Executors.newFixedThreadPool(3);
        List<CompletableFuture<String>> futures = new ArrayList<>();

        for (int i = 0; i < 3; i++) {
            final int index = i;
            CompletableFuture<String> future = CompletableFuture.supplyAsync(() -> {
                try {
                    return timeoutService.callWithTimeout("call-" + index, 5000);
                } catch (Exception e) {
                    return "error";
                }
            }, executor);

            futures.add(future);
        }

        // Wait for all calls to complete
        List<String> results = futures.stream()
            .map(CompletableFuture::join)
            .collect(Collectors.toList());

        assertEquals(3, results.size());
        assertTrue(results.contains("response-1"));
        assertTrue(results.contains("response-2"));
        assertTrue(results.contains("response-3"));

        executor.shutdown();
    }
}
```

## Best practices

### 1. Test isolation

#### Independent tests
```java
@SpringBootTest
public class IsolationBestPracticesTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private EmailService emailService;

    @InjectMocks
    private UserService userService;

    @BeforeEach
    void setup() {
        // Fresh mocks for each test
        MockitoAnnotations.openMocks(this);

        // Reset mocks to clean state
        reset(userRepository, emailService);
    }

    @Test
    void testUserCreation_Success() {
        User user = new User("success@example.com", "Success User");

        when(userRepository.save(any(User.class))).thenReturn(user);
        doNothing().when(emailService).sendWelcomeEmail(anyString());

        User created = userService.createUser("success@example.com", "Success User");

        assertNotNull(created);
        verify(userRepository).save(any(User.class));
        verify(emailService).sendWelcomeEmail("success@example.com");
    }

    @Test
    void testUserCreation_DuplicateEmail() {
        when(userRepository.existsByEmail("duplicate@example.com")).thenReturn(true);

        assertThrows(DuplicateEmailException.class, () -> {
            userService.createUser("duplicate@example.com", "Duplicate User");
        });

        verify(userRepository).existsByEmail("duplicate@example.com");
        verify(userRepository, never()).save(any(User.class));
        verify(emailService, never()).sendWelcomeEmail(anyString());
    }

    @Test
    void testUserCreation_EmailFailure() {
        User user = new User("emailfail@example.com", "Email Fail User");

        when(userRepository.save(any(User.class))).thenReturn(user);
        doThrow(new EmailException("SMTP error")).when(emailService).sendWelcomeEmail(anyString());

        // Depending on implementation, might throw or handle gracefully
        try {
            userService.createUser("emailfail@example.com", "Email Fail User");
            // If email failure doesn't prevent user creation
            verify(userRepository).save(any(User.class));
        } catch (Exception e) {
            // If email failure prevents user creation
            verify(userRepository).save(any(User.class));
        }

        verify(emailService).sendWelcomeEmail("emailfail@example.com");
    }
}
```

### 2. Mock naming conventions

#### Clear mock naming
```java
@SpringBootTest
public class MockNamingTest {

    // Good naming - describes what is being mocked
    @Mock
    private UserRepository userRepository;

    @Mock
    private EmailService emailService;

    @Mock
    private PaymentGateway paymentGateway;

    @Mock
    private CacheService userCache;

    @Mock
    private MetricsCollector metricsCollector;

    @InjectMocks
    private UserService userService;

    @Test
    void testUserCreation_WithAllDependencies() {
        // Setup all mocks
        User user = new User("naming@example.com", "Naming User");

        when(userRepository.save(any(User.class))).thenReturn(user);
        doNothing().when(emailService).sendWelcomeEmail(anyString());
        when(paymentGateway.createAccount(anyString())).thenReturn("acc_123");
        when(userCache.get(anyString())).thenReturn(null);
        doNothing().when(userCache).put(anyString(), any());
        doNothing().when(metricsCollector).recordUserCreated();

        // Test
        User created = userService.createUser("naming@example.com", "Naming User");

        // Verify all interactions
        verify(userRepository).save(any(User.class));
        verify(emailService).sendWelcomeEmail("naming@example.com");
        verify(paymentGateway).createAccount("naming@example.com");
        verify(userCache).get(anyString());
        verify(userCache).put(anyString(), any());
        verify(metricsCollector).recordUserCreated();

        assertNotNull(created);
    }
}
```

### 3. Verification strategies

#### Strategic verification
```java
@SpringBootTest
public class VerificationStrategiesTest {

    @Mock
    private ComplexService complexService;

    @InjectMocks
    private FacadeService facadeService;

    @Test
    void testFacadeMethod_VerifyKeyInteractions() {
        // Setup
        when(complexService.validateInput(anyString())).thenReturn(true);
        when(complexService.processData(anyString())).thenReturn("processed");
        when(complexService.saveResult(anyString())).thenReturn(true);

        // Execute
        String result = facadeService.processAndSave("input data");

        // Verify key interactions (not all internal calls)
        verify(complexService).validateInput("input data");
        verify(complexService).processData("input data");
        verify(complexService).saveResult("processed");

        // Don't verify internal implementation details
        // verify(complexService, never()).logDebugInfo(anyString()); // Too granular

        assertEquals("processed", result);
    }

    @Test
    void testErrorHandling_VerifyExceptionHandling() {
        // Setup failure scenario
        when(complexService.validateInput(anyString()))
            .thenThrow(new ValidationException("Invalid input"));

        // Execute and verify exception
        assertThrows(BusinessException.class, () -> {
            facadeService.processAndSave("invalid data");
        });

        // Verify error handling interactions
        verify(complexService).validateInput("invalid data");
        verify(complexService, never()).processData(anyString());
        verify(complexService, never()).saveResult(anyString());
    }

    @Test
    void testPerformance_VerifyCallCounts() {
        // Setup for performance testing
        when(complexService.processData(anyString())).thenReturn("result");

        // Execute multiple times
        for (int i = 0; i < 5; i++) {
            facadeService.processAndSave("data" + i);
        }

        // Verify call counts are reasonable
        verify(complexService, times(5)).validateInput(anyString());
        verify(complexService, times(5)).processData(anyString());
        verify(complexService, times(5)).saveResult(anyString());
    }
}
```

### 4. Mock lifecycle management

#### Proper mock cleanup
```java
@SpringBootTest
public class MockLifecycleTest {

    // Static mocks for shared state (use carefully)
    @Mock
    private static SharedService sharedService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private EmailService emailService;

    @InjectMocks
    private UserService userService;

    @BeforeAll
    static void setupSharedMocks() {
        // Setup static mocks once
        MockitoAnnotations.openMocks(MockLifecycleTest.class);
        when(sharedService.getSystemConfig()).thenReturn(new SystemConfig(true));
    }

    @AfterAll
    static void cleanupSharedMocks() {
        // Clean up static mocks
        reset(sharedService);
    }

    @BeforeEach
    void setupInstanceMocks() {
        // Setup instance mocks for each test
        MockitoAnnotations.openMocks(this);

        // Default behavior
        when(userRepository.save(any(User.class)))
            .thenAnswer(invocation -> invocation.getArgument(0));
        doNothing().when(emailService).sendWelcomeEmail(anyString());
    }

    @AfterEach
    void cleanupInstanceMocks() {
        // Clean up instance mocks
        reset(userRepository, emailService);
    }

    @Test
    void testWithFreshMocks() {
        // Each test gets clean mock state
        User user = userService.createUser("fresh@example.com", "Fresh User");

        assertNotNull(user);
        verify(userRepository).save(any(User.class));
        verify(emailService).sendWelcomeEmail("fresh@example.com");
    }

    @Test
    void testWithDifferentMockBehavior() {
        // Can override default behavior for specific tests
        when(userRepository.save(any(User.class)))
            .thenThrow(new DataAccessException("Database error"));

        assertThrows(DataAccessException.class, () -> {
            userService.createUser("error@example.com", "Error User");
        });

        verify(userRepository).save(any(User.class));
        verify(emailService, never()).sendWelcomeEmail(anyString());
    }
}
```

### 5. Advanced mocking patterns

#### Mock chaining
```java
@SpringBootTest
public class AdvancedMockingPatternsTest {

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private PaymentService paymentService;

    @Mock
    private InventoryService inventoryService;

    @InjectMocks
    private OrderProcessingService orderProcessingService;

    @Test
    void testComplexOrderProcessing() {
        Order order = createComplexOrder();

        // Setup chained mock behavior
        when(orderRepository.save(any(Order.class))).thenReturn(order);
        when(inventoryService.checkAvailability(any(OrderItem.class))).thenReturn(true);
        when(inventoryService.reserveItems(anyList())).thenReturn(true);
        when(paymentService.authorize(any(BigDecimal.class))).thenReturn("auth_123");
        when(paymentService.capture(anyString(), any(BigDecimal.class))).thenReturn(true);

        // Execute complex workflow
        Order processedOrder = orderProcessingService.processOrder(order);

        // Verify complete workflow
        InOrder inOrder = inOrder(orderRepository, inventoryService, paymentService);

        inOrder.verify(inventoryService).checkAvailability(any(OrderItem.class));
        inOrder.verify(inventoryService).reserveItems(anyList());
        inOrder.verify(paymentService).authorize(order.getTotalAmount());
        inOrder.verify(paymentService).capture("auth_123", order.getTotalAmount());
        inOrder.verify(orderRepository).save(any(Order.class));

        assertEquals(OrderStatus.COMPLETED, processedOrder.getStatus());
    }

    @Test
    void testPartialFailureHandling() {
        Order order = createComplexOrder();

        // Setup partial failure scenario
        when(inventoryService.checkAvailability(any(OrderItem.class))).thenReturn(true);
        when(inventoryService.reserveItems(anyList())).thenReturn(true);
        when(paymentService.authorize(any(BigDecimal.class))).thenReturn("auth_123");
        when(paymentService.capture(anyString(), any(BigDecimal.class)))
            .thenThrow(new PaymentException("Card declined"));

        // Execute and verify failure handling
        assertThrows(OrderProcessingException.class, () -> {
            orderProcessingService.processOrder(order);
        });

        // Verify compensation actions
        verify(inventoryService).releaseReservation(anyList());
        verify(paymentService).voidAuthorization("auth_123");
        verify(orderRepository).save(argThat(orderArg ->
            orderArg.getStatus() == OrderStatus.FAILED));
    }

    @Test
    void testConcurrentOrderProcessing() {
        Order order1 = createOrder("order1");
        Order order2 = createOrder("order2");

        // Setup concurrent-safe mocks
        when(orderRepository.save(any(Order.class)))
            .thenAnswer(invocation -> {
                Order order = invocation.getArgument(0);
                order.setId(System.nanoTime()); // Simulate ID generation
                return order;
            });

        // Process orders concurrently (simplified)
        ExecutorService executor = Executors.newFixedThreadPool(2);
        CompletableFuture<Order> future1 = CompletableFuture.supplyAsync(() ->
            orderProcessingService.processOrder(order1), executor);
        CompletableFuture<Order> future2 = CompletableFuture.supplyAsync(() ->
            orderProcessingService.processOrder(order2), executor);

        // Wait for completion
        Order result1 = future1.join();
        Order result2 = future2.join();

        assertNotNull(result1.getId());
        assertNotNull(result2.getId());
        assertNotEquals(result1.getId(), result2.getId()); // Different IDs

        executor.shutdown();
    }

    private Order createComplexOrder() {
        Order order = new Order();
        order.setItems(List.of(
            new OrderItem("item1", BigDecimal.valueOf(50.0), 2),
            new OrderItem("item2", BigDecimal.valueOf(30.0), 1)
        ));
        order.setTotalAmount(BigDecimal.valueOf(130.0));
        return order;
    }

    private Order createOrder(String orderId) {
        Order order = new Order();
        order.setOrderId(orderId);
        order.setTotalAmount(BigDecimal.valueOf(100.0));
        return order;
    }
}
```

## Troubleshooting

### Распространенные проблемы

#### Mock injection failures

**Symptoms:**
- NullPointerException when using mocked dependencies
- @InjectMocks не работает

**Solutions:**
```java
@SpringBootTest
public class MockInjectionIssuesTest {

    // Problem: Missing @InjectMocks
    @Mock
    private UserRepository userRepository;

    // private UserService userService; // Null!

    @InjectMocks
    private UserService userService; // Fixed

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this); // Required for @InjectMocks
    }

    @Test
    void testInjectionWorks() {
        assertNotNull(userService); // Should pass now
        assertNotNull(userService.getUserRepository()); // Should have mock injected
    }
}

// Problem: Constructor with multiple parameters
@Service
public class MultiDependencyService {

    private final UserRepository userRepository;
    private final EmailService emailService;
    private final CacheService cacheService;

    // Mockito can't inject into this constructor automatically
    public MultiDependencyService(UserRepository userRepository,
                                EmailService emailService,
                                CacheService cacheService) {
        this.userRepository = userRepository;
        this.emailService = emailService;
        this.cacheService = cacheService;
    }
}

// Solution: Use setter injection or manual injection
@SpringBootTest
public class MultiDependencyTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private EmailService emailService;

    @Mock
    private CacheService cacheService;

    private MultiDependencyService service;

    @BeforeEach
    void setup() {
        service = new MultiDependencyService(userRepository, emailService, cacheService);
        // Or use @InjectMocks with setter/constructor
    }
}
```

#### Verification failures

**Symptoms:**
- Wanted but not invoked
- Too many actual invocations

**Solutions:**
```java
@SpringBootTest
public class VerificationIssuesTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserService userService;

    @Test
    void testVerificationOrder() {
        // Problem: Wrong verification order
        userService.createUser("test@example.com", "Test User");

        // This might fail if createUser calls other methods first
        // verify(userRepository).save(any(User.class)); // Might fail

        // Solution: Use inOrder verification
        InOrder inOrder = inOrder(userRepository);
        inOrder.verify(userRepository).existsByEmail("test@example.com");
        inOrder.verify(userRepository).save(any(User.class));
    }

    @Test
    void testExactInvocationCount() {
        // Problem: Unexpected multiple calls
        userService.getAllUsers(); // Calls repository.findAll()

        // This might fail if service calls repository multiple times
        // verify(userRepository, times(1)).findAll(); // Might fail

        // Solution: Check actual usage pattern
        verify(userRepository, atLeastOnce()).findAll();
        verify(userRepository, atMost(5)).findAll(); // Reasonable upper bound
    }

    @Test
    void testArgumentMatching() {
        User user = new User("exact@example.com", "Exact User");

        userService.createUser("exact@example.com", "Exact User");

        // Problem: Too strict matching
        // verify(userRepository).save(eq(new User("exact@example.com", "Exact User"))); // Fails

        // Solution: Use flexible matchers
        verify(userRepository).save(argThat(savedUser ->
            "exact@example.com".equals(savedUser.getEmail()) &&
            "Exact User".equals(savedUser.getName())
        ));
    }
}
```

#### Stubbing issues

**Symptoms:**
- UnnecessaryStubbingException
- UnfinishedStubbingException

**Solutions:**
```java
@SpringBootTest
public class StubbingIssuesTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserService userService;

    @Test
    void testUnnecessaryStubbing() {
        // Problem: Stubbing not used in test
        when(userRepository.count()).thenReturn(100L); // Not used

        userService.getUserCount(); // Only calls findAll()

        // Solution: Remove unused stubbing or use it
        // verify(userRepository).count(); // Use it
    }

    @Test
    void testUnfinishedStubbing() {
        // Problem: Incomplete stubbing
        // when(userRepository.save(any(User.class))); // Missing .thenReturn()

        // Solution: Complete the stubbing
        when(userRepository.save(any(User.class)))
            .thenReturn(new User("complete@example.com", "Complete User"));
    }

    @Test
    void testStrictMocking() {
        // Use @Mock(strictness = Strictness.LENIENT) for lenient mocking
        // or configure globally

        Mockito.mockitoSession()
            .strictness(Strictness.LENIENT)
            .startMocking();
    }
}
```

### Debug techniques

#### Mock debugging
```java
@SpringBootTest
public class MockDebuggingTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserService userService;

    @Test
    void testWithDebugging() {
        // Enable mockito debugging
        System.setProperty("org.mockito.debug", "true");

        // Setup mock with debug info
        when(userRepository.save(any(User.class)))
            .thenAnswer(invocation -> {
                User user = invocation.getArgument(0);
                System.out.println("Mock save called with: " + user);
                return user;
            });

        User result = userService.createUser("debug@example.com", "Debug User");

        // Debug verification
        try {
            verify(userRepository).save(any(User.class));
            System.out.println("Verification passed");
        } catch (AssertionError e) {
            System.out.println("Verification failed: " + e.getMessage());
            throw e;
        }
    }

    @Test
    void testMockStateInspection() {
        // Inspect mock state
        System.out.println("Mock interactions before test:");
        System.out.println(MockingDetails.of(userRepository).getInvocations());

        userService.getAllUsers();

        System.out.println("Mock interactions after test:");
        System.out.println(MockingDetails.of(userRepository).getInvocations());

        // Print stubbings
        System.out.println("Mock stubbings:");
        MockingDetails.of(userRepository).getStubbings().forEach(stubbing ->
            System.out.println("  " + stubbing));
    }

    @Test
    void testMockBehaviorValidation() {
        // Validate mock configuration
        MockingDetails details = MockingDetails.of(userRepository);

        assertTrue(details.isMock(), "Should be a mock");
        assertTrue(details.isSpy(), "Should be a spy"); // or false

        System.out.println("Mock type: " + details.getMockCreationSettings().getTypeToMock());
        System.out.println("Default answer: " + details.getMockCreationSettings().getDefaultAnswer());
        System.out.println("Strictness: " + details.getMockCreationSettings().getStrictness());
    }
}
```

#### Configuration validation
```java
@Configuration
public class MockitoConfigurationValidator implements InitializingBean {

    @Autowired(required = false)
    private List<Object> mockBeans;

    @Override
    public void afterPropertiesSet() {
        System.out.println("=== Mockito Configuration Validation ===");

        if (mockBeans != null) {
            System.out.println("Found " + mockBeans.size() + " mock beans");

            for (Object mockBean : mockBeans) {
                MockingDetails details = MockingDetails.of(mockBean);

                if (details.isMock()) {
                    System.out.println("✓ Mock bean: " + mockBean.getClass().getSimpleName());
                    System.out.println("  Type: " + details.getMockCreationSettings().getTypeToMock());
                    System.out.println("  Strictness: " + details.getMockCreationSettings().getStrictness());

                    // Check for unused stubbings
                    details.getStubbings().forEach(stubbing -> {
                        if (details.getInvocations().stream()
                            .noneMatch(invocation -> invocation.getMethod().equals(stubbing.getMethod()))) {
                            System.out.println("  ⚠️  Unused stubbing: " + stubbing.getMethod());
                        }
                    });
                }
            }
        } else {
            System.out.println("No mock beans found");
        }

        // Validate Mockito version
        System.out.println("Mockito version: " + Mockito.class.getPackage().getImplementationVersion());
    }
}
```

## Заключение

**Mockito Advanced** предоставляет мощные возможности для создания гибких и поддерживаемых unit тестов. От базового mocking до сложных сценариев с custom answers, spies и advanced verification, Mockito позволяет эффективно изолировать код для тестирования.

### Ключевые возможности:

1. **Advanced matchers** — Hamcrest и custom matchers для гибкой проверки аргументов
2. **Verification modes** — различные режимы верификации вызовов
3. **Spies и partial mocking** — частичное mocking реальных объектов
4. **Custom answers** — динамическое поведение mock объектов
5. **BDDMockito** — BDD-style синтаксис для тестов
6. **Argument captors** — захват и анализ аргументов вызовов
7. **Spring Boot integration** — @MockBean, @SpyBean, test slices
8. **Async mocking** — тестирование асинхронных операций

### Архитектурные преимущества:

#### Flexibility:
- **Multiple mocking styles** — от strict до lenient mocking
- **Custom behavior** — programmable mock responses
- **Integration options** — Spring, JUnit 5, Testcontainers

#### Test Quality:
- **Isolation** — полная изоляция unit тестов
- **Predictability** — контролируемое поведение зависимостей
- **Maintainability** — чистые и читаемые тесты

### Когда использовать Mockito Advanced:

✅ **Unit testing** — изоляция unit тестов от зависимостей
✅ **Complex interactions** — тестирование сложных взаимодействий
✅ **External dependencies** — mocking database, external APIs
✅ **Legacy code** — тестирование кода с множеством зависимостей
✅ **TDD/BDD** — test-first development подходы
✅ **Integration testing** — mocking внешних сервисов
✅ **Performance testing** — изоляция для performance тестов

### Когда НЕ использовать:

❌ **Simple integration tests** — когда нужны реальные компоненты
❌ **End-to-end testing** — для full system testing
❌ **Performance testing** — если нужен real system load
❌ **UI testing** — для user interface testing
❌ **Over-mocking** — когда mocks скрывают реальные проблемы
❌ **Complex mocking** — если тесты становятся сложнее кода

### Best practices:

1. **Test isolation** — independent и repeatable tests
2. **Mock naming** — clear and descriptive mock names
3. **Verification strategies** — focus on important interactions
4. **Mock lifecycle** — proper setup and cleanup
5. **Advanced patterns** — custom answers, spies, captors

### Testing Pyramid:

- **Unit Tests (Mockito)** — fast, isolated, focused
- **Integration Tests** — test component interactions  
- **End-to-End Tests** — full system validation

### Common Patterns:

- **Repository mocking** — isolate data access
- **Service mocking** — test business logic
- **External API mocking** — handle third-party dependencies
- **Async operation mocking** — test concurrent scenarios

Mockito Advanced является стандартом для unit testing в Java. Правильное использование его возможностей обеспечивает высокое качество, надежность и поддерживаемость тестового кода. 🚀

**Далее: AssertJ (fluent assertions)**
