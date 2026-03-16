---
title: "Mockito Advanced для Java"
description: "Продвинутые приёмы с Mockito: матчеры, верификация, spies, custom answers, BDDMockito, интеграция со Spring Boot."
tags: ["testing", "unit-testing", "mockito-advanced"]
difficulty: "intermediate"
prerequisites: []
next: []
updated: "2026-02-11"
---
# Mockito Advanced для Java

Продвинутые приёмы с Mockito: матчеры, верификация, spies, custom answers, BDDMockito, интеграция со Spring Boot.

**Дата:** 2026-02-06

## Полезные ссылки

| Тип | Ссылки |
|-----|--------|
| Документация | [Mockito Documentation](https://javadoc.io/doc/org.mockito/mockito-core/latest/org/mockito/Mockito.html), [Mockito GitHub](https://github.com/mockito/mockito) |
| Spring Boot | [@MockBean vs @Mock](https://docs.spring.io/spring-boot/docs/current/reference/html/features.html#features.testing), [Mockito with Spring](https://spring.io/guides/gs/testing-web/) |
| Практики | [Mockito Best Practices](https://github.com/mockito/mockito/wiki/How-to-write-good-tests), [Testing on the Toilet](https://testing.googleblog.com/) |
| См. также | [JUnit Advanced](junit-advanced.md), [AssertJ](assertj.md), [Spring Testing](../../../frameworks/java-frameworks/spring/spring-testing.md) |

## Содержание

- [Advanced matchers](#advanced-matchers)
- [Verification modes](#verification-modes)
- [Spies и partial mocking](#spies-и-partial-mocking)
- [Custom answers](#custom-answers)
- [BDDMockito](#bddmockito)
- [Argument captors](#argument-captors)
- [Mock injection](#mock-injection)
- [Spring Boot integration](#spring-boot-integration)
- [Тестирование void-методов](#testing-void-methods)
- [Тестирование исключений](#exception-testing)
- [Асинхронный mocking](#async-mocking)
- [Решение проблем и FAQ](#решение-проблем-и-faq)
- [Заключение](#заключение)

---

## Advanced matchers

### Hamcrest и кастомные матчеры

- **Hamcrest**: `argThat(hasProperty("amount", equalTo(...)))`, `allOf`, `anyOf`, `not`, `containsString`.
- **Свой матчер**: реализовать `ArgumentMatcher<T>` и использовать через `argThat(матчер)`.

Пример: кастомный матчер и комбинация матчеров.

```java
// Кастомный ArgumentMatcher
    private ArgumentMatcher<AuditEvent> isValidAuditEvent() {
        return event -> event != null &&
                       event.getAction() != null &&
                       event.getTimestamp() != null;
    }

// Использование
when(auditService.logEvent(argThat(isValidAuditEvent()))).thenReturn(true);
verify(auditService).logEvent(argThat(allOf(
    hasProperty("action", equalTo("USER_CREATED")),
    hasProperty("userId", notNullValue())
)));
```

---

## Verification modes

- **times(n)**, **atLeastOnce()**, **atLeast(n)**, **atMost(n)** — количество вызовов.
- **timeout(ms)** — для асинхронных вызовов.
- **never()** — метод не вызывался.
- **verifyNoMoreInteractions(mock)** — больше не было обращений к mock.
- **InOrder** — проверка порядка вызовов.

```java
        verify(cacheService, times(2)).get("user:1");
        verify(cacheService, atLeastOnce()).get(anyString());
        verifyNoMoreInteractions(cacheService);

        InOrder inOrder = inOrder(cacheService);
        inOrder.verify(cacheService).get("user:1");
        inOrder.verify(cacheService, never()).put(anyString(), any());
```

---

## Spies и partial mocking

- **@Spy** — обёртка над реальным объектом; можно подменять отдельные методы.
- **doReturn(x).when(spy).method(...)** — для spy предпочтительнее, чем `when(...).thenReturn(...)`.
- **doCallRealMethod().when(spy).method(...)** — вызвать реальную реализацию.

```java
    @Spy
    private RealUserRepository realUserRepository = new RealUserRepository();

    @Spy
    @InjectMocks
    private ComplexService complexService;

// Подмена только одного метода
        doReturn("mocked response").when(complexService).callExternalApi();
        doCallRealMethod().when(complexService).processData(anyString());
        String result = complexService.processRequest("test data");
```

---

## Custom answers

- **thenAnswer(invocation -> ...)** — ответ по аргументам вызова.
- Удобно для условной логики, счётчиков вызовов, имитации состояния.

```java
        when(paymentGateway.processPayment(any(PaymentRequest.class)))
            .thenAnswer(invocation -> {
        PaymentRequest req = invocation.getArgument(0);
        if (req.getAmount().compareTo(BigDecimal.valueOf(100.0)) > 0)
                    return new PaymentResponse("DECLINED", "Amount too high");
        return new PaymentResponse("APPROVED", "OK", "txn_" + System.currentTimeMillis());
    });
```

Для void-методов: **doAnswer(invocation -> { ... return null; }).when(mock).voidMethod(...)**.

---

## BDDMockito

- **given(mock.method()).willReturn(x)** вместо `when(...).thenReturn(...)`.
- **then(mock).should().method()** вместо `verify(mock).method()`.
- **willDoNothing().given(mock).voidMethod()** для void.

```java
        given(productRepository.save(any(Product.class))).willReturn(product);
        given(productRepository.existsByName("Test Product")).willReturn(false);

        Product created = productService.createProduct("Test Product", BigDecimal.valueOf(29.99));

        then(productRepository).should().save(any(Product.class));
        assertThat(created.getName()).isEqualTo("Test Product");
```

---

## Argument captors

- Захват аргументов для проверки: **ArgumentCaptor.forClass(Class)**.
- **captor.getValue()** — последний вызов; **captor.getAllValues()** — все вызовы.

```java
        ArgumentCaptor<String> emailCaptor = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<String> subjectCaptor = ArgumentCaptor.forClass(String.class);

        doNothing().when(emailService).sendEmail(anyString(), anyString(), anyString());
        userService.sendWelcomeEmail("user@example.com", "John Doe");

        verify(emailService).sendEmail(emailCaptor.capture(), subjectCaptor.capture(), anyString());
        assertEquals("user@example.com", emailCaptor.getValue());
        assertEquals("Welcome John Doe!", subjectCaptor.getValue());
```

---

## Mock injection

- **@InjectMocks** — создаёт экземпляр класса и подставляет в него **@Mock** (по конструктору, полям или сеттерам).
- **MockitoAnnotations.openMocks(this)** в `@BeforeEach` (или расширение JUnit 5) обязательно для работы аннотаций.

| Способ | Когда используется |
|--------|---------------------|
| Constructor | Предпочтительно: один конструктор, все зависимости в нём |
| Field | Поля с подходящими типами |
| Setter | Сеттеры с именами вида `setXxx` |

При нескольких конструкторах или сложной инициализации проще создавать тестируемый объект вручную в `@BeforeEach`, передавая моки.

---

## Spring Boot integration

| Аннотация | Назначение |
|-----------|------------|
| **@MockBean** | Подменяет бин в контексте Spring; тест получает реальный контекст с подставленным mock |
| **@SpyBean** | Spy на реальном бине; часть методов можно замокать |
| **@Mock** | Только mock, без подстановки в контекст (нужен @InjectMocks или ручная инъекция) |

**@WebMvcTest(Controller.class)** — только слой MVC; сервисы подменять через **@MockBean**.

```java
@WebMvcTest(UserController.class)
class WebMvcMockTest {
    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private UserService userService;

    @Test
    void testController() throws Exception {
        when(userService.findById(1L)).thenReturn(new User(1L, "a@b.com", "Name"));
        mockMvc.perform(get("/api/users/1"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.email").value("a@b.com"));
        verify(userService).findById(1L);
    }
}
```

---

## Testing void methods

- **doNothing().when(mock).voidMethod(...)** — явно задать «ничего не делать» (по умолчанию void так и ведёт себя).
- **doThrow(exception).when(mock).voidMethod(...)** — выброс исключения.
- Верификация как обычно: **verify(mock).voidMethod(args)**.

```java
        doNothing().when(auditService).logUserAction(anyString(), anyString());
doThrow(new EmailException("SMTP error")).when(emailService).sendWelcomeEmail(anyString());

        notificationService.notifyUserCreated(user);

        verify(auditService).logUserAction("USER_CREATED", "void@example.com");
        verify(emailService).sendWelcomeEmail("void@example.com");
```

---

## Exception testing

- **assertThrows(ExceptionClass.class, () -> sut.method())** — ожидаемое исключение.
- Проверка сообщения и причины: **exception.getMessage()**, **exception.getCause()**.

```java
        when(userRepository.existsByEmail("existing@example.com")).thenReturn(true);

UserCreationException ex = assertThrows(UserCreationException.class, () ->
    userService.createUser("existing@example.com", "Test User"));

assertEquals("Email already exists", ex.getMessage());
        verify(userRepository, never()).save(any(User.class));
```

---

## Async mocking

- **CompletableFuture**: замокать метод, возвращающий `CompletableFuture<T>`: **thenReturn(CompletableFuture.completedFuture(value))** или **thenReturn(CompletableFuture.failedFuture(exception))**.
- Проверка результата: **result.join()** или **assertThrows(CompletionException.class, result::join)**.

```java
        User user = new User("async@example.com", "Async User");
when(asyncUserRepository.saveAsync(any(User.class)))
    .thenReturn(CompletableFuture.completedFuture(user));

        CompletableFuture<User> result = asyncUserService.createUserAsync("async@example.com", "Async User");
        User created = result.join();

        assertNotNull(created);
        verify(asyncUserRepository).saveAsync(any(User.class));
```

---

## Решение проблем и FAQ

### Частые ошибки

| Симптом | Причина | Что сделать |
|--------|---------|-------------|
| NullPointerException при вызове методов у зависимостей | Не инициализированы моки или нет @InjectMocks | Вызвать `MockitoAnnotations.openMocks(this)` в @BeforeEach; убедиться, что у тестируемого класса есть @InjectMocks |
| Wanted but not invoked / Too many invocations | Неверные аргументы или порядок вызовов | Использовать `argThat(...)`, `eq()`, или **InOrder** для проверки порядка |
| UnnecessaryStubbingException | Застаблен метод, который тест не вызывает | Удалить лишний stubbing или изменить тест; при необходимости использовать lenient: `@Mock(lenient = true)` или `lenient().when(...)` |
| UnfinishedStubbingException | Оставлен незавершённый вызов `when(...)` без `thenReturn`/`thenThrow`/`thenAnswer` | Дописать цепочку: `when(...).thenReturn(...)` |

### Краткие советы

- **Изоляция**: в каждом тесте свой сценарий; при необходимости **reset(mock)** в @BeforeEach.
- **Имена моков**: называть по роли (`userRepository`, `emailService`), а не `mock1`.
- **Верификация**: проверять только значимые вызовы; не дублировать проверками всю внутреннюю реализацию.
- **Spy**: для spy использовать **doReturn().when(spy).method()**, иначе реальный метод может вызваться при настройке.

**Когда использовать Mockito Advanced?** При юнит-тестах с зависимостями, сложными сценариями, внешними API/БД, legacy-кодом. Не заменяет интеграционные и E2E-тесты.

**Где документация?** См. блок «Полезные ссылки» в начале документа.

---

## Заключение

Mockito Advanced даёт:

- **Матчеры** — Hamcrest и свои для гибкой проверки аргументов.
- **Режимы верификации** — количество вызовов, порядок, таймауты.
- **Spies и partial mocking** — подмена только части методов.
- **Custom answers** — поведение в зависимости от аргументов.
- **BDDMockito** — стиль Given-When-Then.
- **ArgumentCaptor** — проверка переданных аргументов.
- **Интеграция со Spring Boot** — @MockBean, @SpyBean, срезы тестов.
- **Асинхронность** — моки для CompletableFuture и void-колбэков.

Имеет смысл использовать для изоляции юнит-тестов и тестирования сложных взаимодействий; не злоупотреблять моками, чтобы тесты не усложнялись сильнее, чем сам код.
