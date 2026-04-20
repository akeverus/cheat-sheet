---
title: "Mockito"
description: "Mockito - это популярная mocking библиотека для Java, которая позволяет создавать mock объекты для тестирования. Обеспечивает чистый и простой API для создания заглушек и mock объектов."
tags:
  - libraries
  - testing-libraries
  - java-mockito
difficulty: "intermediate"
prerequisites: []
next: []
updated: "2026-02-11"
---
# Mockito

**Mockito** - это популярная **mocking** библиотека для **Java**, которая позволяет создавать **mock** объекты для тестирования. Обеспечивает чистый и простой **API** для создания заглушек и **mock** объектов.

## Полезные ссылки

### Официальная документация
- [Mockito](https://site.mockito.org/) — официальный сайт
- [Mockito GitHub](https://github.com/mockito/mockito) — репозиторий проекта
- [Mockito Documentation](https://javadoc.io/doc/org.mockito/mockito-core/latest/org/mockito/Mockito.html) — **API** документация

### См. также
- [[junit|JUnit 5]] — **JUnit** 5 для тестирования
- [MockK](https://mockk.io/) — **MockK** для **Kotlin**
- [Unit-тестирование](../../testing/unit-testing/README.md) и [Mockito Advanced](../../testing/unit-testing/junit/mockito-advanced.md)

## Содержание

- [Основные возможности](#основные-возможности)
  - [Создание Mock объектов](#создание-mock-объектов)
  - [Stubbing методов](#stubbing-методов)
  - [Матчеры аргументов](#argument-matchers)
  - [Verification (проверка вызовов)](#verification-проверка-вызовов)
- [Продвинутые возможности](#продвинутые-возможности)
  - [Spy объекты](#spy-объекты)
  - [BDD стиль тестирования](#bdd-стиль-тестирования)
  - [@Mock и @InjectMocks аннотации](#mock-и-injectmocks-аннотации)
  - [Захват аргументов](#capturing-arguments)
  - [Пользовательский Answer](#custom-answer)
- [Интеграция с Spring Boot](#интеграция-с-spring-boot)
  - [MockMvc для Controller тестирования](#mockmvc-для-controller-тестирования)
  - [Testing Service Layer](#testing-service-layer)
  - [Data JPA Repository Testing](#data-jpa-repository-testing)
- [Паттерны тестирования](#паттерны-тестирования)
  - [Test Data Builders](#test-data-builders)
  - [Behavior Verification Patterns](#behavior-verification-patterns)
- [Продвинутые возможности](#продвинутые-возможности)
  - [Deep Stubbing](#deep-stubbing)
  - [Strict vs Lenient Mocks](#strict-vs-lenient-mocks)
  - [Mock Settings и Options](#mock-settings-и-options)
  - [Mockito Plugins](#mockito-plugins)
- [Performance Testing с Mockito](#performance-testing-с-mockito)
  - [Performance Verification](#performance-verification)
  - [Load Testing с Mocks](#load-testing-с-mocks)
- [Integration с другими инструментами](#integration-с-другими-инструментами)
  - [Mockito + AssertJ](#mockito-assertj)
  - [Mockito + JUnit 5](#mockito-junit-5)
  - [Mockito + Testcontainers](#mockito-testcontainers)
- [Лучшие практики](#лучшие-практики)
  - [Структура тестов](#test-structure)
  - [Рекомендации Mock vs Spy](#mock-vs-spy-guidelines)
- [Решение проблем](#решение-проблем)
  - [Типичные проблемы](#common-issues)
  - [Отладка тестов Mockito](#debugging-mockito-tests)
- [Руководство по миграции](#руководство-по-миграции)
  - [From EasyMock to Mockito](#from-easymock-to-mockito)
  - [From JMock to Mockito](#from-jmock-to-mockito)
  - [Mockito 1.x to 2.x Migration](#mockito-1x-to-2x-migration)
  - [Mockito 3.x to 4.x Migration](#mockito-3x-to-4x-migration)
- [Экспериментальные возможности](#экспериментальные-возможности)
  - [Mockito 5.x Features (Future)](#mockito-5x-features-future)

## Основные возможности

### Создание **Mock** объектов

Создание **mock**-объекта списка и проверка взаимодействий через `verify()`.

```java
import static org.mockito.Mockito.*;
import java.util.List;

// Создание mock объекта
List<String> mockedList = mock(List.class);

// Использование mock объекта
mockedList.add("one");
mockedList.clear();

// Проверка взаимодействий
verify(mockedList).add("one");
verify(mockedList).clear();
```

### **Stubbing** методов
```java
/
 * Демонстрация настройки поведения mock объектов через stubbing
 * Stubbing позволяет определить что должен возвращать mock объект при вызове методов
 */
// Настройка поведения mock объекта - определяем поведение для различных сценариев
List<String> mockedList = mock(List.class);

// Stubbing: когда вызывается get(0), вернуть "first"
// when().thenReturn() определяет возвращаемое значение для конкретного вызова
when(mockedList.get(0)).thenReturn("first");
// Теперь при вызове mockedList.get(0) вернется "first" вместо null

// Stubbing с исключением: когда вызывается get(1), бросить RuntimeException
// when().thenThrow() определяет исключение которое должно быть выброшено
when(mockedList.get(1)).thenThrow(new RuntimeException());
// Теперь при вызове mockedList.get(1) будет выброшено RuntimeException

// Stubbing для метода size(): когда вызывается size(), вернуть 100
when(mockedList.size()).thenReturn(100);
// Теперь mockedList.size() всегда вернет 100 независимо от реального содержимого

// Stubbing с matchers: несколько вызовов возвращают одинаковое значение
// anyInt() - matcher который соответствует любому целому числу
when(mockedList.get(anyInt())).thenReturn("element");
// Теперь любой вызов get() с любым индексом вернет "element"

// Цепочка возвращаемых значений: первый вызов возвращает "first", второй - "second"
// Можно указать несколько thenReturn() для последовательных вызовов
when(mockedList.get(0))
    .thenReturn("first")   // Первый вызов get(0) вернет "first"
    .thenReturn("second"); // Второй вызов get(0) вернет "second"
// Последующие вызовы будут возвращать последнее указанное значение ("second")
```

### Argument Matchers
```java
// Использование матчеров аргументов
`List`<`String`> `mockedList` = `mock`(`List`.class);

// any() - любой объект
when(`mockedList`.get(`anyInt`())).`thenReturn`("element");

// eq() - точное совпадение
when(`mockedList`.add(eq("test"))).`thenReturn`(`true`);

// `anyString`() - любая строка
when(`mockedList`.contains(`anyString`())).`thenReturn`(`true`);

// `anyList`() - любой `List`
when(`mockedList`.`addAll`(`anyList`())).`thenReturn`(`true`);

// matches() - регулярное выражение
when(`mockedList`.remove(matches("test.*"))).`thenReturn`(`true`);

// Кастомный матчер
when(`mockedList`.add(`argThat`(s -> `s.length`() > 3))).`thenReturn`(`true`);
```

### Verification (проверка вызовов)
```java
// Базовая проверка
`List`<`String`> `mockedList` = `mock`(`List`.class);
`mockedList`.add("one");

// Проверка, что метод был вызван
verify(`mockedList`).add("one");

// Проверка количества вызовов
verify(`mockedList`, times(1)).add("one");
verify(`mockedList`, never()).clear();

// Проверка, что другие методы не вызывались
`verifyNoMoreInteractions`(`mockedList`);

// Проверка порядка вызовов
`InOrder inOrder` = `inOrder`(`mockedList`);
`inOrder`.verify(`mockedList`).add("first");
`inOrder`.verify(`mockedList`).add("second");
```

## Продвинутые возможности

### Spy объекты
```java
// Spy объекты вызывают реальные методы, но позволяют их переопределять
`List`<`String`> `realList` = new `ArrayList`<>();
`List`<`String`> `spiedList` = spy(`realList`);

// Реальный метод вызывается
`spiedList`.add("one");
`assertEquals`(1, `spiedList`.size());

// Переопределение поведения
when(`spiedList`.size()).`thenReturn`(`100`);
`assertEquals`(`100`, `spiedList`.get(0)); // 100, а не 1

// Проверка взаимодействий
verify(`spiedList`).add("one");
```

### BDD стиль тестирования
```java
import static `org.mockito`.BDDMockito.*;

// **BDD** (`Behavior Driven Development`) синтаксис
`List`<`String`> list = `mock`(`List`.class);

// `Given`
given(`list.get`(`anyInt`())).`willReturn`("element");

// When
`String result` = `list.get`(0);

// `Then`
then(list).should().get(0);
`assertEquals`("element", result);
```

### @Mock и @InjectMocks аннотации
```java
import `org.mockito`.`Mock`;
import `org.mockito`.`InjectMocks`;
import `org.`mockito.junit`.jupiter`.`MockitoExtension`;
import `org.`junit.jupiter.api`.extension`.`ExtendWith`;

``@ExtendWith`(`MockitoExtension`.class)`
public class `UserServiceTest` {

    `@Mock`
    private `UserRepository userRepository`;

    `@Mock`
    private `EmailService emailService`;

    `@InjectMocks`
    private `UserService userService`;

    `@Test`
    void `testCreateUser`() {
        // `Mocks` автоматически создаются и внедряются
        `User user` = new `User`("`John`", "john`@example`.com");

        when(`userRepository`.save(any(`User`.class))).`thenReturn`(user);

        `User result` = `userService`.`createUser`("`John`", "john`@example`.com");

        `assertEquals`("`John`", result.`getName`());
        verify(`emailService`).`sendWelcomeEmail`(user);
    }
}
```

### Capturing Arguments
```java
/
 - Демонстрация использования `ArgumentCaptor` для захвата аргументов методов
 - `ArgumentCaptor` позволяет проверить аргументы переданные в методы `mock` объектов
 */
// Захват аргументов для последующей проверки
// `ArgumentCaptor` создается для конкретного типа (`User` в данном случае)
`ArgumentCaptor`<`User`> `userCaptor` = `ArgumentCaptor`.`forClass`(`User`.class);
// `ArgumentCaptor` будет захватывать все `User` объекты переданные в методы

// Проверка вызова метода с захватом аргумента
// capture() захватывает аргумент переданный в метод save()
verify(`userRepository`).save(`userCaptor`.capture());
// После verify() аргумент сохранен в `ArgumentCaptor`

// Получение захваченного аргумента для проверки
`User capturedUser` = `userCaptor`.getValue(); // захваченный User
// Проверяем что захваченный объект имеет правильные значения
`assertEquals`("`John`", `capturedUser`.`getName`());              // Проверяем имя
`assertEquals`("john`@example`.com", `capturedUser`.`getEmail`()); // Проверяем email

// Захват нескольких вызовов - когда метод вызывается несколько раз
// times(2) указывает что метод должен быть вызван ровно 2 раза
verify(`emailService`, times(2)).`sendEmail`(`userCaptor`.capture());
// capture() захватывает аргументы из всех вызовов метода

// Получение всех захваченных значений
`List`<`User`> `capturedUsers` = `userCaptor`.getAllValues();
`assertEquals`(2, `capturedUsers`.size());  // Проверяем что было захвачено 2 объекта
// `capturedUsers`.get(0) - первый вызов, `capturedUsers`.get(1) - второй вызов
```

### Custom Answer
```java
/
 - Демонстрация использования `Custom Answer` для кастомной логики в `mock` объектах
 - `Answer` позволяет определить сложную логику возврата значений на основе аргументов
 */
// Кастомная логика для `mock` методов - определяем что должен возвращать метод
// `Answer` - функциональный интерфейс для определения поведения `mock` метода
`Answer`<`String`> `customAnswer` = invocation -> {
    // invocation содержит информацию о вызове метода
    `String argument` = invocation.`getArgument`(0);  // Получаем первый аргумент метода
    // Выполняем кастомную логику на основе аргумента
    return "`Processed`: " + argument.`toUpperCase`();  // Возвращаем обработанную строку
};

`List`<`String`> `mockedList` = `mock`(`List`.class);
// Используем `thenAnswer`() для установки кастомной логики
when(`mockedList`.get(`anyInt`())).`thenAnswer`(`customAnswer`);
// Теперь при вызове get() будет выполняться кастомная логика из `Answer`

// Более сложная логика - создание объекта на основе аргументов
`Answer`<`User`> `userAnswer` = invocation -> {
    // Получаем аргументы метода
    `String name` = invocation.`getArgument`(0);   // Первый аргумент - имя
    `String email` = invocation.`getArgument`(1);  // Второй аргумент - email

    // Создаем новый `User` объект на основе аргументов
    `User user` = new `User`(name, email);
    user.`setId`(`System`.currentTimeMillis()); // ID на основе времени
    // Это полезно когда нужно создать объект с динамическими значениями

    return user;  // Возвращаем созданный объект
};

// Применяем кастомный `Answer` к методу `createUser`
when(`userRepository`.`createUser`(`anyString`(), `anyString`())).`thenAnswer`(`userAnswer`);
// Теперь при вызове `createUser`() будет создан новый `User` с `ID` на основе текущего времени
```

## Интеграция с Spring Boot

### MockMvc для Controller тестирования
```java
`@SpringBootTest`
`@AutoConfigureMockMvc`
public class `UserControllerTest` {

    `@Autowired`
    private `MockMvc mockMvc`;

    `@MockBean`
    private `UserService userService`;

    `@Test`
    void `testGetUser`() throws `Exception` {
        `User user` = new `User`("`John`", "john`@example`.com");
        when(`userService`.`getUser`(1L)).`thenReturn`(user);

        mockMvc.perform(get("/users/1"))
            .`andExpect`(status().`isOk`())
            .`andExpect`(`jsonPath`("$.name").value("`John`"))
            .`andExpect`(`jsonPath`("$.email").value("john`@example`.com"));
    }
}
```

### Testing Service Layer
```java
``@ExtendWith`(`MockitoExtension`.class)`
public class `UserServiceTest` {

    `@Mock`
    private `UserRepository userRepository`;

    `@Mock`
    private `NotificationService notificationService`;

    `@InjectMocks`
    private `UserService userService`;

    `@Test`
    void testCreateUser_Success() {
        // `Arrange`
        `User user` = new `User`("`John`", "john`@example`.com");
        when(`userRepository`.save(any(`User`.class))).`thenReturn`(user);

        // Act
        `User result` = `userService`.`createUser`("`John`", "john`@example`.com");

        // `Assert`
        `assertNotNull`(result);
        `assertEquals`("`John`", result.`getName`());
        verify(`userRepository`).save(any(`User`.class));
        verify(`notificationService`).`sendWelcomeNotification`(user);
    }

    `@Test`
    void testCreateUser_RepositoryThrowsException() {
        // `Arrange`
        when(`userRepository`.save(any(`User`.class)))
            .`thenThrow`(new `DataIntegrityViolationException`("`Duplicate email`"));

        // Act & `Assert`
        `assertThrows`(`UserCreationException`.class, () ->
            `userService`.`createUser`("`John`", "existing`@example`.com"));

        verify(`notificationService`, never()).`sendWelcomeNotification`(any());
    }
}
```

### Data JPA Repository Testing
```java
``@ExtendWith`(`MockitoExtension`.class)`
public class `UserRepositoryTest` {

    `@Mock`
    private `EntityManager entityManager`;

    `@InjectMocks`
    private `UserRepositoryImpl userRepository`;

    `@Test`
    void `testFindByEmail`() {
        // Создание `TypedQuery mock`
        `TypedQuery`<`User`> query = `mock`(`TypedQuery`.class);
        when(`entityManager`.`createQuery`(`anyString`(), eq(`User`.class))).`thenReturn`(query);
        when(query.`setParameter`(`anyString`(), any())).`thenReturn`(query);
        when(query.`getSingleResult`()).`thenReturn`(new `User`("`John`", "john`@example`.com"));

        // Выполнение теста
        `Optional`<`User`> result = `userRepository`.`findByEmail`("john`@example`.com");

        `assertTrue`(result.`isPresent`());
        `assertEquals`("`John`", `result.get`().`getName`());
    }
}
```

## Паттерны тестирования

### Test Data Builders
```java
public class `UserTestBuilder` {
    private `String name` = "`Default Name`";
    private `String email` = "default`@example`.com";
    private int age = 25;
    private boolean active = `true`;

    public `UserTestBuilder` name(`String name`) {
        `this.name` = name;
        return this;
    }

    public `UserTestBuilder` email(`String email`) {
        `this.email` = email;
        return this;
    }

    public `UserTestBuilder` age(int age) {
        `this.age` = age;
        return this;
    }

    public `UserTestBuilder` inactive() {
        `this.active` = `false`;
        return this;
    }

    public `User build`() {
        `User user` = new `User`(name, email);
        user.`setAge`(age);
        user.`setActive`(active);
        return user;
    }

    public static `UserTestBuilder validUser`() {
        return new `UserTestBuilder`();
    }

    public static `UserTestBuilder invalidUser`() {
        return new `UserTestBuilder`().email("`invalid-email`");
    }
}

// Использование в тестах
``@ExtendWith`(`MockitoExtension`.class)`
public class `UserServiceTest` {

    `@Mock`
    private `UserRepository userRepository`;

    `@InjectMocks`
    private `UserService userService`;

    `@Test`
    void `testCreateValidUser`() {
        `User user` = `UserTestBuilder`.`validUser`()
            .name("`John Doe`")
            .email("john`@example`.com")
            .build();

        when(`userRepository`.save(any(`User`.class))).`thenReturn`(user);

        `User result` = `userService`.`createUser`(user);

        `assertNotNull`(result);
        verify(`userRepository`).save(user);
    }
}
```

### Behavior Verification Patterns
```java
public class `BehaviorVerificationTest` {

    `@Mock`
    private `OrderRepository orderRepository`;

    `@Mock`
    private `PaymentService paymentService`;

    `@Mock`
    private `EmailService emailService`;

    `@InjectMocks`
    private `OrderService orderService`;

    `@Test`
    void `testSuccessfulOrderProcessing`() {
        // `Given`
        `Order order` = `createTestOrder`();
        `PaymentResult paymentResult` = new `PaymentResult`(`true`, "txn123");

        when(`paymentService`.`processPayment`(order)).`thenReturn`(`paymentResult`);
        when(`orderRepository`.save(any(`Order`.class))).`thenReturn`(order);

        // When
        `Order processedOrder` = `orderService`.`processOrder`(order);

        // `Then`
        `assertEquals`(`OrderStatus`.`CONFIRMED`, `processedOrder`.`getStatus`());

        // `Verify interactions in correct` order
        `InOrder inOrder` = `inOrder`(`paymentService`, `orderRepository`, `emailService`);
        `inOrder`.verify(`paymentService`).`processPayment`(order);
        `inOrder`.verify(`orderRepository`).save(order);
        `inOrder`.verify(`emailService`).`sendOrderConfirmation`(order);
    }

    `@Test`
    void `testFailedPaymentHandling`() {
        // `Given`
        `Order order` = `createTestOrder`();
        `PaymentResult paymentResult` = new `PaymentResult`(`false`, "`Payment failed`");

        when(`paymentService`.`processPayment`(order)).`thenReturn`(`paymentResult`);

        // When & `Then`
        `assertThrows`(`PaymentException`.class, () -> `orderService`.`processOrder`(order));

        // `Verify that order was` not saved and no email was sent
        verify(`orderRepository`, never()).save(any(`Order`.class));
        verify(`emailService`, never()).`sendOrderConfirmation`(any(`Order`.class));
    }
}
```

## Продвинутые возможности

### Deep Stubbing
```java
// Автоматическое создание `mock` для цепочки вызовов
`@Mock`
private `OrderService orderService`;

// Включаем deep stubbing
// (в `Mockito 3.0`+ это поведение по умолчанию для lenient mocks)

`@Test`
void `testDeepStubbing`() {
    // Этот код работает без предварительной настройки
    when(`orderService`.`getOrder`(1L).`getItems`().get(0).`getProduct`().`getName`())
        .`thenReturn`("`Test Product`");

    // Использование
    `String productName` = `orderService`.`getOrder`(1L).`getItems`().get(0).`getProduct`().`getName`();
    `assertEquals`("`Test Product`", `productName`);
}
```

### Strict vs Lenient Mocks
```java
// `Strict mocks` (по умолчанию) - бросают исключение при неожиданных взаимодействиях
`@Mock`
private `List`<`String`> `strictMock`; // Не настроен

`@Test`
void `testWithStrictMock`() {
    // Этот вызов бросит исключение, так как метод не настроен
    // `strictMock` вызовет `UnnecessaryStubbingException` в Mockito 3+

    // Правильный способ
    lenient().when(`strictMock`.get(0)).`thenReturn`("value");
}

// `Lenient mocks` - игнорируют неожиданные взаимодействия
``@Mock`(lenient = `true`)`
private `List`<`String`> `lenientMock`;

`@Test`
void `testWithLenientMock`() {
    // Эти вызовы не настроены, но не бросают исключения
    `lenientMock`.get(0); // OK
    `lenientMock`.get(1); // OK
}
```

### Mock Settings и Options
```java
// Кастомные настройки для `mock`
`List`<`String`> `mockWithSettings` = `mock`(`List`.class, `withSettings`()
    .name("`MyMockList`") // Имя для отладки
    .`verboseLogging`()   // Детальное логирование
    .`stubOnly`()         // Только для stubbing, без verification
);

// `Mock` с ответом по умолчанию
`List`<`String`> `mockWithDefaultAnswer` = `mock`(`List`.class, RETURNS_DEEP_STUBS);

// `Mock` с кастомным ответом по умолчанию
`List`<`String`> `mockWithCustomAnswer` = `mock`(`List`.class, invocation -> {
    // Кастомная логика для необработанных вызовов
    return "`Default response`";
});

// `Serializable mock`
`List`<`String`> `serializableMock` = `mock`(`List`.class, `withSettings`().serializable());
```

### Mockito Plugins
```java
// Использование плагинов
// `META-INF/org.mockito.plugins.MockMaker`
public class `CustomMockMaker` implements `MockMaker` {

    `@Override`
    public <T> T `createMock`(`MockCreationSettings`<T> settings, `MockHandler` handler) {
        // Кастомная логика создания `mock` объектов
        return `null`;
    }

    `@Override`
    public `MockHandler getHandler`(`Object mock`) {
        return `null`;
    }

    `@Override`
    public void `resetMock`(`Object mock`, `MockHandler newHandler`, `MockCreationSettings` settings) {
        // Логика сброса `mock`
    }

    `@Override`
    public `TypeMockability isTypeMockable`(`Class`<?> type) {
        return `null`;
    }
}
```

## Performance Testing с Mockito

### Performance Verification
```java
`@Test`
void `testServicePerformance`() {
    // Настройка `mock` для быстрого ответа
    when(`userRepository`.`findById`(1L)).`thenReturn`(`Optional`.of(`createTestUser`()));

    // Измерение времени
    long `startTime` = `System`.`nanoTime`();

    `User user` = `userService`.`getUser`(1L);

    long `endTime` = `System`.`nanoTime`();
    long `durationMs` = (`endTime` - `startTime`) / 1_000_000;

    // Проверка производительности
    `assertTrue`(`durationMs` < `100`, "`Service too slow`: " + `durationMs` + "ms");
    `assertNotNull`(user);
}
```

### Load Testing с Mocks
```java
`@Test`
void `testConcurrentAccess`() throws `InterruptedException` {
    // Настройка `mock` для конкурентного доступа
    when(`userRepository`.save(any(`User`.class))).then(invocation -> {
        `User user` = invocation.`getArgument`(0);
        user.`setId`(`Thread`.currentThread().getId()); // ID для разных потоков
        return user;
    });

    // Конкурентное выполнение
    `ExecutorService` executor = `Executors`.`newFixedThreadPool`(10);
    `List`<`CompletableFuture`<`User`>> futures = new `ArrayList`<>();

    for (int i = 0; i < `100`; i++) {
        `CompletableFuture`<`User`> future = `CompletableFuture`.`supplyAsync`(() -> {
            `User user` = new `User`("`User`" + `Thread`.`currentThread`().`getId`(), "user`@example`.com");
            return `userService`.`createUser`(user);
        }, executor);

        `futures.add`(future);
    }

    // Ожидание завершения всех задач
    `CompletableFuture`<`Void`> `allFutures` = `CompletableFuture`.`allOf`(
        futures.`toArray`(new `CompletableFuture`[0]));

    `allFutures`.get(10, `TimeUnit`.`SECONDS`);

    // Проверка результатов
    for (`CompletableFuture`<`User`> future : futures) {
        `User user` = `future.get`();
        `assertNotNull`(user.`getId`());
    }

    `executor.shutdown`();
}
```

## Integration с другими инструментами

### Mockito + AssertJ
```java
``@ExtendWith`(`MockitoExtension`.class)`
public class `MockitoAssertJTest` {

    `@Mock`
    private `UserRepository userRepository`;

    `@InjectMocks`
    private `UserService userService`;

    `@Test`
    void `testCreateUser`() {
        `User user` = new `User`("`John`", "john`@example`.com");

        when(`userRepository`.save(any(`User`.class))).`thenReturn`(user);

        // Act
        `User result` = `userService`.`createUser`("`John`", "john`@example`.com");

        // `Assert with AssertJ`
        `assertThat`(result).`isNotNull`();
        `assertThat`(result.`getName`()).`isEqualTo`("`John`");
        `assertThat`(result.`getEmail`()).`isEqualTo`("john`@example`.com");

        // `Verify interactions with AssertJ`
        verify(`userRepository`).save(`argThat`(`savedUser` ->
            `savedUser`.`getName`().equals("`John`") &&
            `savedUser`.`getEmail`().equals("john`@example`.com")
        ));
    }
}
```

### Mockito + JUnit 5
```java
``@ExtendWith`(`MockitoExtension`.class)`
``@DisplayName`("`User `Service` Tests`")`
public class `UserServiceJUnit5Test` {

    `@Mock`
    private `UserRepository userRepository`;

    `@InjectMocks`
    private `UserService userService`;

    `@Nested`
    ``@DisplayName`("`User Creation`")`
    class `UserCreationTests` {

        `@Test`
        ``@DisplayName`("`Should create user successfully`")`
        void `shouldCreateUserSuccessfully`() {
            // `Test implementation`
        }

        `@ParameterizedTest`
        ``@ValueSource`(strings = {"valid@`email.com`", "test@`example.com`"})`
        ``@DisplayName`("`Should create user with` different emails")`
        void `shouldCreateUserWithDifferentEmails`(`String email`) {
            `User user` = new `User`("`Test`", email);
            when(`userRepository`.save(any(`User`.class))).`thenReturn`(user);

            `User result` = `userService`.`createUser`("`Test`", email);

            `assertEquals`(email, result.`getEmail`());
        }
    }
}
```

### Mockito + Testcontainers
```java
`@SpringBootTest`
`@Testcontainers`
``@ExtendWith`(`MockitoExtension`.class)`
public class `IntegrationTest` {

    `@Container`
    private static `PostgreSQLContainer`<?> postgres = new `PostgreSQLContainer`<>("postgres:13");

    `@MockBean`
    private `ExternalApiClient externalApiClient`; // Mock внешнего API

    `@Autowired`
    private `UserService userService`;

    `@Test`
    void `testWithRealDatabaseAndMockedExternalApi`() {
        // Настройка `mock` для внешнего `API`
        when(`externalApiClient`.`validateEmail`(`anyString`())).`thenReturn`(`true`);

        // Тест использует реальную базу данных из `Testcontainers`
        // и замоканный внешний `API`
        `User user` = `userService`.`createUser`("`John`", "john`@example`.com");

        `assertNotNull`(user.`getId`());
        verify(`externalApiClient`).`validateEmail`("john`@example`.com");
    }
}
```

## Лучшие практики

### Test Structure
```java
``@ExtendWith`(`MockitoExtension`.class)`
public class `UserServiceTest` {

    // `Constants`
    private static final `String` VALID_EMAIL = "test`@example`.com";
    private static final `String` INVALID_EMAIL = "invalid";

    // `Test fixtures`
    `@Mock` private `UserRepository userRepository`;
    `@Mock` private `EmailService emailService`;
    `@InjectMocks` private `UserService userService`;

    `@Test`
    void `shouldCreateUserSuccessfully`() {
        // `Arrange`
        `User expectedUser` = `createTestUser`();
        when(`userRepository`.save(any(`User`.class))).`thenReturn`(`expectedUser`);

        // Act
        `User actualUser` = `userService`.`createUser`("`John`", VALID_EMAIL);

        // `Assert`
        `assertNotNull`(`actualUser`);
        `assertEquals`("`John`", `actualUser`.`getName`());
        `assertEquals`(VALID_EMAIL, `actualUser`.`getEmail`());

        // `Verify`
        verify(`userRepository`).save(any(`User`.class));
        verify(`emailService`).`sendWelcomeEmail`(`expectedUser`);
    }

    `@Test`
    void `shouldThrowExceptionForInvalidEmail`() {
        // `Arrange`
        when(`userRepository`.save(any(`User`.class)))
            .`thenThrow`(new `IllegalArgumentException`("`Invalid email`"));

        // Act & `Assert`
        `assertThrows`(`IllegalArgumentException`.class, () ->
            `userService`.`createUser`("`John`", INVALID_EMAIL));

        // `Verify no email was` sent
        verify(`emailService`, never()).`sendWelcomeEmail`(any());
    }

    `@Test`
    void `shouldHandleRepositoryException`() {
        // `Arrange`
        when(`userRepository`.save(any(`User`.class)))
            .`thenThrow`(new `DataAccessException`("`Database error`") {});

        // Act & `Assert`
        `assertThrows`(`ServiceException`.class, () ->
            `userService`.`createUser`("`John`", VALID_EMAIL));
    }

    private `User createTestUser`() {
        `User user` = new `User`("`John`", VALID_EMAIL);
        user.`setId`(1L);
        return user;
    }
}
```

### Mock vs Spy Guidelines
```java
public class `MockSpyGuidelines` {

    `@Test`
    void `useMockWhen`() {
        // Используй `mock` когда:
        // - Тестируешь взаимодействие с зависимостью
        // - Зависимость сложная или медленная (база данных, внешние `API`)
        // - Хочешь полный контроль над поведением

        `List`<`String`> `mockList` = `mock`(`List`.class);
        when(`mockList`.size()).`thenReturn`(5);

        `assertEquals`(5, `mockList`.size());
    }

    `@Test`
    void `useSpyWhen`() {
        // Используй spy когда:
        // - Хочешь протестировать реальное поведение с некоторыми изменениями
        // - Большая часть методов должна работать как обычно
        // - Тестируешь legacy код

        `List`<`String`> `realList` = new `ArrayList`<>();
        `List`<`String`> `spiedList` = spy(`realList`);

        // Реальное поведение
        `spiedList`.add("test");
        `assertEquals`(1, `spiedList`.size());

        // Переопределенное поведение
        when(`spiedList`.`isEmpty`()).`thenReturn`(`true`);
        `assertTrue`(`spiedList`.isEmpty()); // true, хотя элемент добавлен
    }

    `@Test`
    void `avoidSpyAntiPatterns`() {
        `List`<`String`> `spiedList` = spy(new `ArrayList`<>());

        // Плохо: spy с полным переопределением
        when(`spiedList`.add(`anyString`())).`thenReturn`(`true`);
        // Теперь реальный add() никогда не вызывается

        // Лучше: использовать `mock`
        `List`<`String`> `mockList` = `mock`(`List`.class);
        when(`mockList`.add(`anyString`())).`thenReturn`(`true`);
    }
}
```

## Решение проблем

### Common Issues
```java
public class `MockitoTroubleshooting` {

    // Проблема: `UnnecessaryStubbingException`
    `@Test`
    void `fixUnnecessaryStubbing`() {
        `List`<`String`> `mockList` = `mock`(`List`.class);

        // Неправильно: настройка без использования
        // when(`mockList`.get(0)).`thenReturn`("test");

        // Правильно: использовать настройку
        when(`mockList`.get(0)).`thenReturn`("test");
        `String result` = `mockList`.get(0); // Используем настройку

        // Или сделать lenient
        // lenient().when(`mockList`.get(0)).`thenReturn`("test");
    }

    // Проблема: `WrongTypeOfReturnValue`
    `@Test`
    void `fixWrongReturnType`() {
        `List`<`String`> `mockList` = `mock`(`List`.class);

        // Неправильно: возвращаем `Integer` вместо boolean
        // when(`mockList`.add("test")).`thenReturn`(1);

        // Правильно: возвращаем правильный тип
        when(`mockList`.add("test")).`thenReturn`(`true`);
    }

    // Проблема: `NullPointerException` при verify
    `@Test`
    void `fixNullPointerInVerify`() {
        `List`<`String`> `mockList` = `mock`(`List`.class);

        // Неправильно: verify для `null` объекта
        // `List`<`String`> `nullList` = `null`;
        // verify(`nullList`).add("test");

        // Правильно: verify для `mock` объекта
        verify(`mockList`).add("test");
    }

    // Проблема: `TooManyActualInvocations`
    `@Test`
    void `fixTooManyInvocations`() {
        `List`<`String`> `mockList` = `mock`(`List`.class);

        // Настройка ожидания одного вызова
        `mockList`.add("test");

        // Неправильно: verify(times(1)) когда было 1 вызов
        // verify(`mockList`, times(2)) приведёт к TooManyActualInvocations

        // Правильно: правильное количество
        verify(`mockList`, times(1)).add("test");
    }
}
```

### Debugging Mockito Tests
```java
public class `MockitoDebugging` {

    `@Test`
    void `debugWithDetailedVerification`() {
        `List`<`String`> `mockList` = `mock`(`List`.class);

        // Добавление элементов
        `mockList`.add("first");
        `mockList`.add("second");
        `mockList`.add("first"); // Добавляем еще раз

        // Детальная проверка вызовов
        verify(`mockList`, times(2)).add("first");
        verify(`mockList`, times(1)).add("second");
        verify(`mockList`, times(3)).add(`anyString`());

        // Проверка отсутствия других взаимодействий
        `verifyNoMoreInteractions`(`mockList`);
    }

    `@Test`
    void `debugWithArgumentCaptors`() {
        `List`<`String`> `mockList` = `mock`(`List`.class);
        `ArgumentCaptor`<`String`> captor = `ArgumentCaptor`.`forClass`(`String`.class);

        `mockList`.add("test1");
        `mockList`.add("test2");

        verify(`mockList`, times(2)).add(`captor.capture`());

        `List`<`String`> `capturedValues` = captor.`getAllValues`();
        `System`.`out.println`("`Captured values`: " + `capturedValues`);

        `assertEquals`(`Arrays`.`asList`("test1", "test2"), `capturedValues`);
    }

    `@Test`
    void `debugWithInOrderVerification`() {
        `List`<`String`> `mockList` = `mock`(`List`.class);

        // Вызовы в определенном порядке
        `mockList`.clear();
        `mockList`.add("first");
        `mockList`.add("second");

        `InOrder inOrder` = `inOrder`(`mockList`);

        // Проверка порядка
        `inOrder`.verify(`mockList`).clear();
        `inOrder`.verify(`mockList`).add("first");
        `inOrder`.verify(`mockList`).add("second");
    }
}
```

## Руководство по миграции

### From EasyMock to Mockito
```java
// `EasyMock`
`List`<`String`> `mockList` = `EasyMock`.`createMock`(`List`.class);
`EasyMock`.expect(`mockList`.get(0)).`andReturn`("test");
`EasyMock`.replay(`mockList`);

// `Mockito`
`List`<`String`> `mockList` = `mock`(`List`.class);
when(`mockList`.get(0)).`thenReturn`("test");
// Нет необходимости в replay
```

### From JMock to Mockito
```java
// JMock
`Mockery context` = new `Mockery`();
final `List`<`String`> `mockList` = `context.mock`(`List`.class);

`context.checking`(new `Expectations`() {{
    `oneOf`(`mockList`).get(0); will(`returnValue`("test"));
}});

// `Mockito`
`List`<`String`> `mockList` = `mock`(`List`.class);
when(`mockList`.get(0)).`thenReturn`("test");
// Нет необходимости в контексте
```

### Mockito 1.x to 2.x Migration
```java
// `Mockito 1`.x
`List`<`String`> `mockList` = `Mockito`.`mock`(`List`.class);
`Mockito`.when(`mockList`.get(0)).`thenReturn`("test");

// `Mockito 2`.x+ (совместимо)
`List`<`String`> `mockList` = `mock`(`List`.class);
when(`mockList`.get(0)).`thenReturn`("test");
```

### Mockito 3.x to 4.x Migration
```java
// `Mockito 3`.x
`List`<`String`> `mock` = `mock`(`List`.class, RETURNS_DEFAULTS);

// `Mockito 4`.x
// RETURNS_DEFAULTS стал default поведением
`List`<`String`> `mock` = `mock`(`List`.class);
```

## Экспериментальные возможности

### Mockito 5.x Features (Future)
```java
// Предполагаемые возможности `Mockito 5`.x
// (основанные на roadmap и текущих разработках)

// `Inline mocking` (без `MockMaker`)
// Улучшенная поддержка sealed classes
// Лучшая поддержка records
// Улучшенная интеграция с `JDK`

// Пример возможного `API` для records
`@Mock`
private `PersonRecord personRecord`;

`@Test`
void `testWithRecords`() {
    when(`personRecord`.name()).`thenReturn`("`John`");
    when(`personRecord`.age()).`thenReturn`(30);

    `assertEquals`("`John`", `personRecord`.name());
    `assertEquals`(30, `personRecord`.age());
}
```

## Полезные ссылки
- [Официальная документация Mockito](https://javadoc.io/doc/org.mockito/mockito-core/latest/org/mockito/Mockito.html)
- [Mockito GitHub](https://github.com/mockito/mockito)
- [Mockito Samples](https://github.com/mockito/mockito/tree/main/src/test/java/org/mockito)
- [JUnit 5 Integration](https://junit.org/junit5/docs/current/user-guide/#writing-tests-dependency-injection)

## См. также
- [[java-junit5|JUnit 5]] — тестирование с **JUnit 5**
- [[assertj|AssertJ]] — fluent assertions
- [[java-testcontainers|Testcontainers]] — интеграционные тесты

