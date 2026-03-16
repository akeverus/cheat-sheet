---
title: "Вопросы на собеседовании: Unit Testing"
description: "Краткие ответы по unit-тестированию: изолированные тесты, mocks/stubs, TDD, структура тестов и качество проверок."
tags: ["interview", "testing", "unit-testing-interview"]
difficulty: "intermediate"
prerequisites: []
next: []
updated: "2026-02-11"
---
# Вопросы на собеседовании: `Unit Testing`

Краткие ответы по `Unit Testing`: изолированные проверки бизнес-логики, работа с моками, `TDD` и поддерживаемая структура unit-тестов.

Дата последнего обновления: 2026-02-11

Краткое введение: фокус этого документа — быстрые и изолированные тесты без реальной инфраструктуры.

## Роль документа в связке testing

- Этот файл отвечает за **unit-уровень**: отдельные классы/методы, моки и контракт поведения компонента.
- За проверку интеграции с БД, брокерами и внешними API отвечают [`integration-testing-interview.md`](integration-testing-interview.md).
- За общую стратегию покрытия и приоритизацию тестов отвечает [`test-strategies-interview.md`](test-strategies-interview.md).
- За масштабирование автотестов в CI/CD и поддержку framework отвечает [`test-automation-interview.md`](test-automation-interview.md).

## Полезные ссылки

### Официальная документация

- [JUnit 5 Documentation](https://junit.org/junit5/docs/current/user-guide/)
- [Mockito Documentation](https://javadoc.io/doc/org.mockito/mockito-core/latest/org/mockito/Mockito.html)
- [AssertJ Documentation](https://assertj.github.io/doc/)
- [Test-Driven Development](https://martinfowler.com/bliki/TestDrivenDevelopment.html)
- [Testing Pyramid](https://martinfowler.com/articles/practical-test-pyramid.html)

### См. также

- [`integration-testing-interview.md`](integration-testing-interview.md) — интеграционное тестирование
- [`test-strategies-interview.md`](test-strategies-interview.md) — стратегии тестирования
- [`test-automation-interview.md`](test-automation-interview.md) — автоматизация тестирования

## Содержание

- [Полезные ссылки](#полезные-ссылки)

**Основы Unit Testing**
- [Q1. Что такое unit testing и зачем он нужен?](#q1-что-такое-unit-testing-и-зачем-он-нужен)
- [Q2. Какие основные принципы unit testing?](#q2-какие-основные-принципы-unit-testing)
- [Q3. Как работает JUnit 5?](#q3-как-работает-junit-5)

**Mocking и TDD**
- [Q4. Что такое mocking и зачем он нужен?](#q4-что-такое-mocking-и-зачем-он-нужен)
- [Q5. Как использовать Mockito?](#q5-как-использовать-mockito)
- [Q6. Что такое TDD (Test-Driven Development)?](#q6-что-такое-tdd-test-driven-development)
- [Q7. Как тестировать исключения?](#q7-как-тестировать-исключения)
- [Q8. Как тестировать приватные методы?](#q8-как-тестировать-приватные-методы)

**Parameterized и структура тестов**
- [Q9. Что такое parameterized tests?](#q9-что-такое-parameterized-tests)
- [Q10. Как организовать структуру тестов?](#q10-как-организовать-структуру-тестов)
- [Q11. Что такое AssertJ и чем он лучше стандартных assertions?](#q11-что-такое-assertj-и-чем-он-лучше-стандартных-assertions)
- [Q12. Как тестировать статические методы?](#q12-как-тестировать-статические-методы)
- [Q13. Что такое test doubles (mock, stub, spy, fake)?](#q13-что-такое-test-doubles-mock-stub-spy-fake)

**Асинхронность и специальные сценарии**
- [Q14. Как тестировать асинхронный код (CompletableFuture)?](#q14-как-тестировать-асинхронный-код-completablefuture)
- [Q15. Что такое @TempDir и зачем он нужен?](#q15-что-такое-tempdir-и-зачем-он-нужен)
- [Q16. Как тестировать логирование?](#q16-как-тестировать-логирование)
- [Q17. Что такое @RepeatedTest и @Timeout?](#q17-что-такое-repeatedtest-и-timeout)
- [Q18. Как тестировать Stream API и Optional?](#q18-как-тестировать-stream-api-и-optional)
- [Q19. Что такое test fixtures и test data builders?](#q19-что-такое-test-fixtures-и-test-data-builders)

**Покрытие и группировка**
- [Q20. Как измерить покрытие кода (JaCoCo)?](#q20-как-измерить-покрытие-кода-jacoco)
- [Q21. Как тестировать equals/hashCode/toString?](#q21-как-тестировать-equalshashcodetostring)
- [Q22. Что такое @Nested и зачем группировать тесты?](#q22-что-такое-nested-и-зачем-группировать-тесты)
- [Q23. Как тестировать конструкторы и билдеры?](#q23-как-тестировать-конструкторы-и-билдеры)
- [Q24. Что такое @DynamicTest и когда использовать?](#q24-что-такое-dynamictest-и-когда-использовать)

**Продвинутые темы**
- [Q25. Как тестировать код с зависимостью от времени (Clock)?](#q25-как-тестировать-код-с-зависимостью-от-времени-clock)
- [Q26. Что такое @Tag и как фильтровать тесты?](#q26-что-такое-tag-и-как-фильтровать-тесты)
- [Q27. Как тестировать рефлексию и аннотации?](#q27-как-тестировать-рефлексию-и-аннотации)
- [Q28. Что такое AssertJ soft assertions?](#q28-что-такое-assertj-soft-assertions)
- [Q29. Как тестировать многопоточный код?](#q29-как-тестировать-многопоточный-код)
- [Q30. Best practices для unit-тестов?](#q30-best-practices-для-unit-тестов)

## Q1. Что такое `unit testing` и зачем он нужен?

`Unit testing` — это метод тестирования программного обеспечения, при котором отдельные модули (units) кода тестируются изолированно от остальной системы.

### Что такое "`Unit`"?

`Unit` — это наименьший тестируемый компонент программы, обычно:
- Отдельный метод
- Класс с одним методом
- Группа связанных функций

### Зачем нужен `unit testing`?

#### 1. Раннее обнаружение ошибок

```java
// Класс для тестирования
public class Calculator {
 public int divide(int a, int b) {
 return a / b; // Может бросить ArithmeticException
 }
}

// Unit тест
@Test
void shouldThrowExceptionWhenDividingByZero() {
 Calculator calculator = new Calculator();

 assertThrows(ArithmeticException.class, () -> {
 calculator.divide(10, 0);
 });
}
```

#### 2. Упрощение рефакторинга

```java
// Исходный код
public class StringUtils {
 public static String capitalize(String input) {
 if (input == null || input.isEmpty()) {
 return input;
 }
 return input.substring(0, 1).toUpperCase() + input.substring(1).toLowerCase();
 }
}

// Тест, который защищает от регрессии
@Test
void shouldCapitalizeString() {
 assertEquals("Hello", StringUtils.capitalize("hello"));
 assertEquals("Hello", StringUtils.capitalize("HELLO"));
 assertEquals("", StringUtils.capitalize(""));
 assertNull(StringUtils.capitalize(null));
}

// Безопасный рефакторинг
public class StringUtils {
 public static String capitalize(String input) {
 if (input == null || input.isEmpty()) {
 return input;
 }
 // Более эффективная реализация
 char[] chars = input.toCharArray();
 chars[0] = Character.toUpperCase(chars[0]);
 for (int i = 1; i < chars.length; i++) {
 chars[i] = Character.toLowerCase(chars[i]);
 }
 return new String(chars);
 }
}
```

#### 3. Документация поведения

```java
@Test
void shouldReturnEmptyListWhenNoItemsFound() {
 // Given
 ItemRepository repository = mock(ItemRepository.class);
 when(repository.findByCategory("books")).thenReturn(Collections.emptyList());

 ItemService service = new ItemService(repository);

 // When
 List<Item> items = service.findItemsByCategory("books");

 // Then
 assertTrue(items.isEmpty());
}
```

#### 4. Улучшение дизайна кода

`Unit` тестирование способствует написанию более модульного и loosely coupled кода.

## Q2. Какие основные принципы `unit testing`?

### 1. F.I.R.S.T `Principles`

#### `Fast` (Быстрые)

```java
@Test
void shouldExecuteInMilliseconds() {
 // Тест должен выполняться быстро
 long startTime = System.nanoTime();

 Calculator calc = new Calculator();
 int result = calc.add(2, 3);

 long endTime = System.nanoTime();
 long duration = (endTime - startTime) / 1_000_000; // в миллисекундах

 assertTrue(duration < 10, "Test should execute in less than 10ms");
 assertEquals(5, result);
}
```

#### `Independent` (Независимые)

```java
public class UserServiceTest {

 private UserRepository userRepository;
 private UserService userService;

 @BeforeEach
 void setUp() {
 // Каждый тест получает свежие экземпляры
 userRepository = mock(UserRepository.class);
 userService = new UserService(userRepository);
 }

 @Test
 void shouldCreateUser() {
 // Тест не зависит от состояния других тестов
 User user = new User("john@example.com");
 when(userRepository.save(any(User.class))).thenReturn(user);

 User created = userService.createUser("john@example.com");
 assertNotNull(created);
 }

 @Test
 void shouldFindUserById() {
 // Этот тест тоже независим
 User user = new User("jane@example.com");
 when(userRepository.findById(1L)).thenReturn(Optional.of(user));

 Optional<User> found = userService.findById(1L);
 assertTrue(found.isPresent());
 assertEquals("jane@example.com", found.get().getEmail());
 }
}
```

#### `Repeatable` (Повторяемые)

```java
@Test
void shouldAlwaysReturnSameResult() {
 Calculator calc = new Calculator();

 // Тест всегда должен давать один и тот же результат
 assertEquals(8, calc.add(3, 5));
 assertEquals(8, calc.add(3, 5));
 assertEquals(8, calc.add(3, 5));
}
```

#### `Self-Validating` (Самопроверяемые)

```java
@Test
void shouldValidateUserEmail() {
 UserValidator validator = new UserValidator();

 // Тест сам проверяет результат - не нужно вручную проверять логи
 assertTrue(validator.isValidEmail("user@example.com"));
 assertFalse(validator.isValidEmail("invalid-email"));
 assertFalse(validator.isValidEmail(""));
 assertFalse(validator.isValidEmail(null));
}
```

#### `Thorough` (Тщательные)

```java
@Test
void shouldHandleAllEdgeCases() {
 StringProcessor processor = new StringProcessor();

 // Тестируем все граничные случаи
 assertEquals("", processor.reverse(""));
 assertEquals("a", processor.reverse("a"));
 assertEquals("ba", processor.reverse("ab"));
 assertNull(processor.reverse(null));

 // Тестируем специальные символы
 assertEquals("!dlroW olleH", processor.reverse("Hello World!"));

 // Тестируем unicode
 assertEquals("🌟⭐", processor.reverse("⭐🌟"));
}
```

### 2. `AAA Pattern` (`Arrange`, Act, `Assert`)

```java
@Test
void shouldCalculateDiscountForPremiumUser() {
 // Arrange (Подготовка)
 User premiumUser = new User("premium@example.com", UserType.PREMIUM);
 Product product = new Product("Laptop", 1000.0);
 DiscountService discountService = new DiscountService();

 // Act (Действие)
 double finalPrice = discountService.applyDiscount(product, premiumUser);

 // Assert (Проверка)
 assertEquals(850.0, finalPrice, 0.01); // 15% скидка для premium
}
```

### 3. `Right BICEP`

- `Right`: Результат правильный?
- B: Граничные условия корректны?
- I: Обратное отношение корректно?
- C: Другой способ дает тот же результат?
- E: Ошибки обрабатываются правильно?
- P: Производительность приемлема?

## Q3. Как работает `JUnit 5`?

`JUnit 5` — это фреймворк для тестирования `Java` приложений с новой архитектурой и улучшенными возможностями.

### Основные компоненты `JUnit 5`

#### 1. JUnit `Platform`

Запускает тесты на различных платформах (`IDE`, build tools, `CI`/`CD`).

#### 2. JUnit `Jupiter`
Содержит новые аннотации и assertions для написания тестов.

#### 3. JUnit `Vintage`
Обеспечивает совместимость с `JUnit 3` и 4.

### Основные аннотации

```java
import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;

public class CalculatorTest {

 private Calculator calculator;

 @BeforeAll
 static void initAll() {
 // Выполняется один раз перед всеми тестами класса
 System.out.println("Starting Calculator tests");
 }

 @BeforeEach
 void init() {
 // Выполняется перед каждым тестом
 calculator = new Calculator();
 }

 @Test
 void shouldAddTwoNumbers() {
 // Простой тест
 assertEquals(5, calculator.add(2, 3));
 }

 @Test
 @DisplayName("Should throw exception when dividing by zero")
 void shouldThrowWhenDividingByZero() {
 // Тест с понятным названием
 assertThrows(ArithmeticException.class, () -> {
 calculator.divide(10, 0);
 });
 }

 @Test
 @Disabled("Feature not implemented yet")
 void shouldHandleLargeNumbers() {
 // Отключенный тест
 fail("Not implemented");
 }

 @AfterEach
 void tearDown() {
 // Выполняется после каждого теста
 calculator = null;
 }

 @AfterAll
 static void tearDownAll() {
 // Выполняется один раз после всех тестов класса
 System.out.println("Calculator tests completed");
 }
}
```

### `Assertions`

```java
import static org.junit.jupiter.api.Assertions.*;

@Test
void shouldValidateAssertions() {
 Calculator calc = new Calculator();

 // Базовые assertions
 assertEquals(5, calc.add(2, 3));
 assertNotEquals(6, calc.add(2, 3));

 assertTrue(calc.isPositive(5));
 assertFalse(calc.isPositive(-1));

 assertNull(calc.findUser(null));
 assertNotNull(calc.findUser("valid"));

 // Assertions с сообщениями
 assertEquals(8, calc.multiply(2, 4), "2 * 4 should equal 8");

 // Assertions с дельтой для floating point
 assertEquals(3.14, calc.getPi(), 0.01);

 // Assertions для коллекций
 List<String> result = calc.getItems();
 assertAll("List assertions",
 () -> assertTrue(result.contains("item1")),
 () -> assertEquals(3, result.size()),
 () -> assertFalse(result.isEmpty())
 );

 // Assertions для исключений
 Exception exception = assertThrows(IllegalArgumentException.class, () -> {
 calc.process(null);
 });
 assertEquals("Input cannot be null", exception.getMessage());
}
```

### `Nested Tests`

```java
@DisplayName("Calculator operations")
public class CalculatorNestedTest {

 private Calculator calculator;

 @BeforeEach
 void setUp() {
 calculator = new Calculator();
 }

 @Nested
 @DisplayName("Addition")
 class AdditionTests {

 @Test
 @DisplayName("Should add positive numbers")
 void shouldAddPositiveNumbers() {
 assertEquals(5, calculator.add(2, 3));
 }

 @Test
 @DisplayName("Should add negative numbers")
 void shouldAddNegativeNumbers() {
 assertEquals(-5, calculator.add(-2, -3));
 }

 @Nested
 @DisplayName("With zero")
 class WithZero {

 @Test
 void shouldReturnSameNumberWhenAddingZero() {
 assertEquals(5, calculator.add(5, 0));
 assertEquals(0, calculator.add(0, 0));
 }
 }
 }

 @Nested
 @DisplayName("Division")
 class DivisionTests {

 @Test
 void shouldDivideNumbers() {
 assertEquals(2.5, calculator.divide(5, 2));
 }

 @Test
 void shouldThrowWhenDividingByZero() {
 assertThrows(ArithmeticException.class, () -> {
 calculator.divide(5, 0);
 });
 }
 }
}
```

### `Parameterized Tests`

```java
@ParameterizedTest
@ValueSource(ints = {1, 2, 3, 4, 5})
void shouldReturnTrueForPositiveNumbers(int number) {
 Calculator calc = new Calculator();
 assertTrue(calc.isPositive(number));
}

@ParameterizedTest
@CsvSource({
 "2, 3, 5",
 "-1, 1, 0",
 "0, 0, 0"
})
void shouldAddNumbers(int a, int b, int expected) {
 Calculator calc = new Calculator();
 assertEquals(expected, calc.add(a, b));
}

@ParameterizedTest
@MethodSource("provideTestData")
void shouldProcessData(String input, String expected) {
 DataProcessor processor = new DataProcessor();
 assertEquals(expected, processor.process(input));
}

static Stream<Arguments> provideTestData() {
 return Stream.of(
 Arguments.of("hello", "HELLO"),
 Arguments.of("world", "WORLD"),
 Arguments.of("", "")
 );
}
```

## Q4. Что такое mocking и зачем он нужен?

`Mocking` — это техника создания объектов-заглушек, которые имитируют поведение реальных объектов для тестирования.

### Зачем нужен mocking?

#### 1. Изоляция тестируемого кода

```java
// Без mocking - тест зависит от внешних систем
@Test
void shouldSendEmail() {
 EmailService service = new EmailService();
 service.sendEmail("user@example.com", "Subject", "Body");

 // Как проверить, что email отправлен?
 // Нужно проверить логи, базу данных, или использовать реальный SMTP сервер
}

// С mocking - тест изолирован
@Test
void shouldSendEmail() {
 // Arrange
 EmailProvider emailProvider = mock(EmailProvider.class);
 EmailService service = new EmailService(emailProvider);

 // Act
 service.sendEmail("user@example.com", "Subject", "Body");

 // Assert
 verify(emailProvider).sendEmail("user@example.com", "Subject", "Body");
}
```

#### 2. Контроль зависимостей

```java
@Test
void shouldReturnCachedData() {
 // Arrange
 Cache cache = mock(Cache.class);
 DataService service = new DataService(cache);

 String key = "user:123";
 User expectedUser = new User("John");

 when(cache.get(key)).thenReturn(expectedUser);

 // Act
 User user = service.getUser(123L);

 // Assert
 assertEquals(expectedUser, user);
 verify(cache).get(key);
 verify(cache, never()).put(anyString(), any());
}
```

#### 3. Тестирование исключительных ситуаций

```java
@Test
void shouldHandleDatabaseConnectionFailure() {
 // Arrange
 UserRepository repository = mock(UserRepository.class);
 UserService service = new UserService(repository);

 when(repository.findById(1L)).thenThrow(new DataAccessException("Connection failed"));

 // Act & Assert
 assertThrows(DataAccessException.class, () -> {
 service.getUserById(1L);
 });
}
```

## Q5. Как использовать `Mockito`?

`Mockito` — это популярный фреймворк для создания `mock` объектов в `Java`.

### Основные возможности `Mockito`

#### 1. Создание `mock` объектов

```java
// Создание mock через annotation
@ExtendWith(MockitoExtension.class)
public class UserServiceTest {

 @Mock
 private UserRepository userRepository;

 @Mock
 private EmailService emailService;

 @InjectMocks
 private UserService userService;

 // Альтернативный способ создания mock
 @Test
 void alternativeMockCreation() {
 UserRepository repository = mock(UserRepository.class);
 EmailService emailService = mock(EmailService.class);
 UserService service = new UserService(repository, emailService);
 }
}
```

#### 2. Настройка поведения (`Stubbing`)

```java
@Test
void shouldReturnUserWhenFound() {
 // Arrange
 User expectedUser = new User("john@example.com");
 when(userRepository.findById(1L)).thenReturn(Optional.of(expectedUser));

 // Act
 Optional<User> user = userService.findById(1L);

 // Assert
 assertTrue(user.isPresent());
 assertEquals("john@example.com", user.get().getEmail());
}

@Test
void shouldReturnEmptyWhenUserNotFound() {
 // Arrange
 when(userRepository.findById(999L)).thenReturn(Optional.empty());

 // Act
 Optional<User> user = userService.findById(999L);

 // Assert
 assertFalse(user.isPresent());
}
```

#### 3. Проверка вызовов (`Verification`)

```java
@Test
void shouldSaveUserAndSendEmail() {
 // Arrange
 User user = new User("john@example.com");
 when(userRepository.save(any(User.class))).thenReturn(user);

 // Act
 userService.registerUser("john@example.com", "password");

 // Assert - проверка вызовов
 verify(userRepository).save(any(User.class));
 verify(emailService).sendWelcomeEmail("john@example.com");

 // Проверка количества вызовов
 verify(emailService, times(1)).sendWelcomeEmail(anyString());
 verify(emailService, atLeastOnce()).sendWelcomeEmail(anyString());
 verify(emailService, never()).sendPasswordResetEmail(anyString());
}
```

#### 4. Работа с void методами

```java
@Test
void shouldHandleVoidMethod() {
 // Настройка void метода
 doNothing().when(emailService).sendWelcomeEmail(anyString());

 // Или настройка исключения
 doThrow(new RuntimeException("SMTP server down")).when(emailService).sendWelcomeEmail("problem@example.com");

 // Act
 userService.registerUser("john@example.com", "password");

 // Verify
 verify(emailService).sendWelcomeEmail("john@example.com");
}
```

#### 5. `Argument Matchers`

```java
@Test
void shouldUseArgumentMatchers() {
 // Matchers для различных типов аргументов
 when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(new User()));
 when(userRepository.findById(eq(1L))).thenReturn(Optional.of(new User()));
 when(userRepository.findByStatus(isNotNull())).thenReturn(List.of(new User()));

 // Act
 userService.findUserByEmail("test@example.com");
 userService.findById(1L);
 userService.findActiveUsers();

 // Verify with matchers
 verify(userRepository).findByEmail(anyString());
 verify(userRepository).findById(eq(1L));
 verify(userRepository).findByStatus(isNotNull());
}
```

#### 6. Spy (`Partial Mocking`)

```java
@Test
void shouldUseSpyForPartialMocking() {
 // Создание spy - реальный объект с возможностью mocking
 List<String> list = spy(new ArrayList<>());

 // Реальные методы работают нормально
 list.add("one");
 list.add("two");

 assertEquals(2, list.size());

 // Можно замокать конкретные методы
 when(list.size()).thenReturn(100);

 assertEquals(100, list.size()); // Замоканный результат

 // Проверка вызовов
 verify(list).add("one");
 verify(list).add("two");
 verify(list, times(2)).size();
}
```

#### 7. `Capturing Arguments`

```java
@Test
void shouldCaptureArguments() {
 // Arrange
 ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);

 // Act
 userService.createUser("john@example.com", "password");

 // Assert - захват переданных аргументов
 verify(userRepository).save(userCaptor.capture());

 User capturedUser = userCaptor.getValue();
 assertEquals("john@example.com", capturedUser.getEmail());
 assertNotNull(capturedUser.getCreatedAt());
}
```

#### 8. `BDD Style Testing`

```java
@Test
void shouldCreateUserSuccessfully() {
 // Given
 User user = new User("john@example.com");
 given(userRepository.save(any(User.class))).willReturn(user);
 given(emailService.sendWelcomeEmail(anyString())).willReturn(true);

 // When
 User created = userService.createUser("john@example.com", "password");

 // Then
 assertThat(created).isNotNull();
 assertThat(created.getEmail()).isEqualTo("john@example.com");

 then(userRepository).should().save(any(User.class));
 then(emailService).should().sendWelcomeEmail("john@example.com");
}
```

## Q6. Что такое `TDD` (Test-Driven Development)?

`TDD` (Test-Driven Development) — методология разработки, при которой тесты пишутся перед кодом. Цикл **Red–Green–Refactor**: написать падающий тест, сделать минимальную реализацию, рефакторить.

#### 1. Red: Написать failing тест

```java
// Тест для еще не реализованной функциональности
@Test
void shouldCalculateFactorial() {
 Calculator calc = new Calculator();

 assertEquals(1, calc.factorial(0));
 assertEquals(1, calc.factorial(1));
 assertEquals(2, calc.factorial(2));
 assertEquals(6, calc.factorial(3));
 assertEquals(24, calc.factorial(4));
}
```

#### 2. `Green`: Написать минимальный код для прохождения теста

```java
public class Calculator {
 public int factorial(int n) {
 if (n == 0 || n == 1) {
 return 1;
 }
 return n * factorial(n - 1);
 }
}
```

#### 3. `Refactor`: Улучшить код, сохраняя проходные тесты

```java
public class Calculator {
 public int factorial(int n) {
 if (n < 0) {
 throw new IllegalArgumentException("Factorial is not defined for negative numbers");
 }
 if (n == 0 || n == 1) {
 return 1;
 }
 int result = 1;
 for (int i = 2; i <= n; i++) {
 result *= i;
 }
 return result;
 }
}
```

### Преимущества `TDD`

#### 1. Лучший дизайн кода

`TDD` заставляет думать о интерфейсе и поведении до реализации.

#### 2. Высокое покрытие тестами
Все функциональные требования покрыты тестами.

#### 3. Уверенность в рефакторинге
Тесты защищают от регрессий при изменении кода.

#### 4. Живая документация
Тесты показывают, как использовать код.

### Пример полного цикла `TDD`

```java
// 1. RED: Написать тест для StringStack
@Test
void shouldCreateEmptyStack() {
 StringStack stack = new StringStack();
 assertTrue(stack.isEmpty());
 assertEquals(0, stack.size());
}

@Test
void shouldPushAndPopElements() {
 StringStack stack = new StringStack();

 stack.push("first");
 assertFalse(stack.isEmpty());
 assertEquals(1, stack.size());

 stack.push("second");
 assertEquals(2, stack.size());

 assertEquals("second", stack.pop());
 assertEquals(1, stack.size());
 assertEquals("first", stack.pop());
 assertTrue(stack.isEmpty());
}

// 2. GREEN: Минимальная реализация
public class StringStack {
 private List<String> elements = new ArrayList<>();

 public void push(String element) {
 elements.add(element);
 }

 public String pop() {
 if (elements.isEmpty()) {
 throw new EmptyStackException();
 }
 return elements.remove(elements.size() - 1);
 }

 public boolean isEmpty() {
 return elements.isEmpty();
 }

 public int size() {
 return elements.size();
 }
}

// 3. REFACTOR: Улучшения
public class StringStack {
 private final List<String> elements;
 private final int maxSize;

 public StringStack() {
 this(Integer.MAX_VALUE);
 }

 public StringStack(int maxSize) {
 this.maxSize = maxSize;
 this.elements = new ArrayList<>();
 }

 public void push(String element) {
 if (element == null) {
 throw new IllegalArgumentException("Element cannot be null");
 }
 if (elements.size() >= maxSize) {
 throw new StackOverflowError("Stack is full");
 }
 elements.add(element);
 }

 public String pop() {
 if (elements.isEmpty()) {
 throw new EmptyStackException();
 }
 return elements.remove(elements.size() - 1);
 }

 public String peek() {
 if (elements.isEmpty()) {
 throw new EmptyStackException();
 }
 return elements.get(elements.size() - 1);
 }

 public boolean isEmpty() {
 return elements.isEmpty();
 }

 public int size() {
 return elements.size();
 }

 public void clear() {
 elements.clear();
 }
}
```

## Q7. Как тестировать исключения?

### 1. Использование `assertThrows`

```java
@Test
void shouldThrowExceptionWhenDividingByZero() {
 Calculator calculator = new Calculator();

 // JUnit 5
 assertThrows(ArithmeticException.class, () -> {
 calculator.divide(10, 0);
 });
}

@Test
void shouldThrowExceptionWithCorrectMessage() {
 UserService userService = new UserService();

 Exception exception = assertThrows(IllegalArgumentException.class, () -> {
 userService.createUser("", "password");
 });

 assertEquals("Username cannot be empty", exception.getMessage());
}
```

### 2. Тестирование checked exceptions

```java
@Test
void shouldHandleIOException() throws IOException {
 FileProcessor processor = new FileProcessor();

 // Использование @Test(expected =...) в JUnit 4
 // В JUnit 5:
 assertThrows(IOException.class, () -> {
 processor.readFile("/nonexistent/file.txt");
 });
}

@Test
void shouldHandleCheckedExceptionInLambda() {
 FileProcessor processor = new FileProcessor();

 // Для checked exceptions в lambda можно использовать Executable
 Executable executable = () -> processor.readFile("/nonexistent/file.txt");
 assertThrows(IOException.class, executable);
}
```

### 3. Тестирование исключений с `Mockito`

```java
@Test
void shouldHandleRepositoryException() {
 // Arrange
 UserRepository repository = mock(UserRepository.class);
 UserService service = new UserService(repository);

 when(repository.findById(1L)).thenThrow(new DataAccessException("Database connection failed"));

 // Act & Assert
 assertThrows(ServiceException.class, () -> {
 service.getUserById(1L);
 });
}

@Test
void shouldRetryOnTransientException() {
 // Arrange
 ExternalService externalService = mock(ExternalService.class);
 RetryService retryService = new RetryService(externalService);

 when(externalService.call()).thenThrow(new TimeoutException("Temporary failure")).thenReturn("success");

 // Act
 String result = retryService.callWithRetry();

 // Assert
 assertEquals("success", result);
 verify(externalService, times(2)).call(); // Первый вызов + повтор
}
```

### 4. Тестирование кастомных исключений

```java
@Test
void shouldThrowCustomValidationException() {
 UserValidator validator = new UserValidator();

 ValidationException exception = assertThrows(ValidationException.class, () -> {
 validator.validateEmail("invalid-email");
 });

 // Проверка полей кастомного исключения
 assertEquals("INVALID_EMAIL", exception.getErrorCode());
 assertTrue(exception.getErrors().contains("Email format is invalid"));
 assertEquals("email", exception.getField());
}
```

### 5. Тестирование исключений в асинхронном коде

```java
@Test
void shouldHandleAsyncException() {
 AsyncService asyncService = new AsyncService();

 // Для CompletableFuture
 CompletableFuture<String> future = asyncService.processAsync("invalid");

 ExecutionException exception = assertThrows(ExecutionException.class, () -> {
 future.get(1, TimeUnit.SECONDS);
 });

 assertTrue(exception.getCause() instanceof ValidationException);
}
```

## Q8. Как тестировать приватные методы?

### 1. Зачем тестировать приватные методы?

Обычно приватные методы тестируются косвенно через публичные методы. Но иногда прямое тестирование необходимо:

- Комплексная бизнес-логика в приватных методах
- Приватные методы с побочными эффектами
- Унаследованный код с плохо структурированными приватными методами

### 2. Прямой доступ через `Reflection` (не рекомендуется)

```java
public class CalculatorTest {

 @Test
 void shouldTestPrivateMethod() throws Exception {
 Calculator calculator = new Calculator();
 Class<?> clazz = calculator.getClass();

 // Получение приватного метода
 Method method = clazz.getDeclaredMethod("calculateTax", BigDecimal.class);
 method.setAccessible(true);

 // Вызов метода
 BigDecimal result = (BigDecimal) method.invoke(calculator, new BigDecimal("100"));

 assertEquals(new BigDecimal("20.00"), result);
 }
}
```

### 3. Изменение видимости метода (рекомендуется)

```java
// Изменить модификатор доступа для тестирования
class Calculator {

 // Изменено с private на package-private для тестирования
 BigDecimal calculateTax(BigDecimal amount) {
 return amount.multiply(new BigDecimal("0.20"));
 }

 public BigDecimal calculateTotal(BigDecimal amount) {
 BigDecimal tax = calculateTax(amount);
 return amount.add(tax);
 }
}

@Test
void shouldCalculateTaxCorrectly() {
 Calculator calculator = new Calculator();

 // Тестирование через reflection или в том же пакете
 BigDecimal tax = calculator.calculateTax(new BigDecimal("100"));
 assertEquals(new BigDecimal("20.00"), tax);
}
```

### 4. Выделение приватной логики в отдельный класс

```java
// Лучший подход: выделить логику в отдельный класс
public class TaxCalculator {

 public BigDecimal calculateTax(BigDecimal amount) {
 if (amount == null || amount.compareTo(BigDecimal.ZERO) < 0) {
 throw new IllegalArgumentException("Invalid amount");
 }
 return amount.multiply(new BigDecimal("0.20"));
 }
}

public class Calculator {

 private final TaxCalculator taxCalculator = new TaxCalculator();

 public BigDecimal calculateTotal(BigDecimal amount) {
 BigDecimal tax = taxCalculator.calculateTax(amount);
 return amount.add(tax);
 }
}

// Теперь TaxCalculator можно тестировать независимо
public class TaxCalculatorTest {

 private TaxCalculator taxCalculator = new TaxCalculator();

 @Test
 void shouldCalculateTax() {
 BigDecimal tax = taxCalculator.calculateTax(new BigDecimal("100"));
 assertEquals(new BigDecimal("20.00"), tax);
 }

 @Test
 void shouldThrowExceptionForInvalidAmount() {
 assertThrows(IllegalArgumentException.class, () -> {
 taxCalculator.calculateTax(null);
 });

 assertThrows(IllegalArgumentException.class, () -> {
 taxCalculator.calculateTax(new BigDecimal("-10"));
 });
 }
}
```

### 5. Тестирование через публичные методы

```java
@Test
void shouldCalculateTotalIncludingTax() {
 Calculator calculator = new Calculator();

 // Тестируем приватный метод calculateTax косвенно через публичный calculateTotal
 BigDecimal total = calculator.calculateTotal(new BigDecimal("100"));

 // Ожидаем: 100 + 20% tax = 120
 assertEquals(new BigDecimal("120.00"), total);
}

@Test
void shouldCalculateTaxThroughTotal() {
 Calculator calculator = new Calculator();

 // Тестируем разные сценарии через публичный API
 assertEquals(new BigDecimal("120.00"), calculator.calculateTotal(new BigDecimal("100")));
 assertEquals(new BigDecimal("240.00"), calculator.calculateTotal(new BigDecimal("200")));
 assertEquals(new BigDecimal("0.00"), calculator.calculateTotal(new BigDecimal("0")));
}
```

### 6. Использование `PowerMock` для тестирования приватных методов (не рекомендуется)

```java
@RunWith(PowerMockRunner.class)
@PrepareForTest(Calculator.class)
public class CalculatorPowerMockTest {

 @Test
 public void shouldTestPrivateMethod() throws Exception {
 Calculator calculator = new Calculator();

 // Получение приватного метода
 Method method = PowerMockito.method(Calculator.class, "calculateTax", BigDecimal.class);

 // Вызов приватного метода
 BigDecimal result = (BigDecimal) method.invoke(calculator, new BigDecimal("100"));

 assertEquals(new BigDecimal("20.00"), result);
 }
}
```

## Q9. Что такое `parameterized tests`?

`Parameterized` tests позволяют запускать один и тот же тест с разными наборами данных.

### 1. `@ValueSource`

```java
@ParameterizedTest
@ValueSource(ints = {1, 2, 3, 4, 5})
void shouldReturnTrueForPositiveNumbers(int number) {
 Calculator calc = new Calculator();
 assertTrue(calc.isPositive(number));
}

@ParameterizedTest
@ValueSource(strings = {"", " ", "\t", "\n"})
void shouldReturnTrueForBlankStrings(String input) {
 assertTrue(StringUtils.isBlank(input));
}
```

### 2. `@EnumSource`

```java
enum UserStatus {
 ACTIVE, INACTIVE, SUSPENDED, DELETED
}

@ParameterizedTest
@EnumSource(UserStatus.class)
void shouldHandleAllUserStatuses(UserStatus status) {
 User user = new User();
 user.setStatus(status);

 UserService service = new UserService();
 assertDoesNotThrow(() -> service.processUserStatus(user));
}
```

### 3. `@MethodSource`

```java
@ParameterizedTest
@MethodSource("provideCalculatorTestData")
void shouldCalculateCorrectly(int a, int b, int expected) {
 Calculator calc = new Calculator();
 assertEquals(expected, calc.add(a, b));
}

static Stream<Arguments> provideCalculatorTestData() {
 return Stream.of(
 Arguments.of(1, 2, 3),
 Arguments.of(-1, 1, 0),
 Arguments.of(0, 0, 0),
 Arguments.of(100, 200, 300),
 Arguments.of(Integer.MAX_VALUE, 1, Integer.MIN_VALUE) // overflow
 );
}

@ParameterizedTest
@MethodSource("provideUserValidationData")
void shouldValidateUserData(String email, String password, boolean expectedValid) {
 UserValidator validator = new UserValidator();
 assertEquals(expectedValid, validator.isValid(email, password));
}

static Stream<Arguments> provideUserValidationData() {
 return Stream.of(
 Arguments.of("user@example.com", "password123", true),
 Arguments.of("invalid-email", "password123", false),
 Arguments.of("user@example.com", "short", false),
 Arguments.of("", "password123", false),
 Arguments.of(null, "password123", false)
 );
}
```

### 4. `@CsvSource`

```java
@ParameterizedTest
@CsvSource({
 "apple, 5, appleappleappleappleapple",
 "hello, 3, hellohellohello",
 "'', 5, ''",
 "a, 1, a"
})
void shouldRepeatString(String input, int times, String expected) {
 StringUtils utils = new StringUtils();
 assertEquals(expected, utils.repeat(input, times));
}

@ParameterizedTest
@CsvSource(delimiter = '|', textBlock = """
 apple | 5 | appleappleappleappleapple
 hello | 3 | hellohellohello
 '' | 5 | ''
 a | 1 | a
 """)
void shouldRepeatStringWithDelimiter(String input, int times, String expected) {
 StringUtils utils = new StringUtils();
 assertEquals(expected, utils.repeat(input, times));
}
```

### 5. `@CsvFileSource`

```java
// test-data.csv
// email,password,expectedValid
// user@example.com,password123,true
// invalid-email,password123,false
// user@example.com,short,false

@ParameterizedTest
@CsvFileSource(resources = "/test-data.csv", numLinesToSkip = 1)
void shouldValidateUserFromCsv(String email, String password, boolean expectedValid) {
 UserValidator validator = new UserValidator();
 assertEquals(expectedValid, validator.isValid(email, password));
}
```

### 6. `Custom Parameter Providers`

```java
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@ParameterizedTest
@ArgumentsSource(RandomDateProvider.class)
void shouldHandleRandomDates(@AggregateWith(PersonAggregator.class) Person person) {
 // Тест с рандомными данными
 assertNotNull(person.getName());
 assertNotNull(person.getBirthDate());
}

static class RandomDateProvider implements ArgumentsProvider {

 @Override
 public Stream<? extends Arguments> provideArguments(ExtensionContext context) {
 Random random = new Random();
 return Stream.generate(() -> {
 String name = "Person" + random.nextInt(1000);
 LocalDate birthDate = LocalDate.of(
 1950 + random.nextInt(50),
 1 + random.nextInt(12),
 1 + random.nextInt(28)
 );
 return Arguments.of(new Person(name, birthDate));
 }).limit(10);
 }
}

static class PersonAggregator implements ArgumentsAggregator {

 @Override
 public Object aggregateArguments(ArgumentsAccessor accessor, ParameterContext context) {
 return new Person(accessor.getString(0), accessor.get(1, LocalDate.class));
 }
}
```

### 7. `Parameterized Tests` с `Lifecycle`

```java
@ParameterizedTest
@MethodSource("provideDatabaseConfigurations")
void shouldConnectToDatabase(String url, String username, String password) {
 DatabaseConnection connection = new DatabaseConnection(url, username, password);

 assertDoesNotThrow(() -> {
 connection.open();
 assertTrue(connection.isConnected());
 connection.close();
 });
}

static Stream<Arguments> provideDatabaseConfigurations() {
 return Stream.of(
 Arguments.of("jdbc:h2:mem:test1", "sa", ""),
 Arguments.of("jdbc:h2:mem:test2", "sa", ""),
 Arguments.of("jdbc:postgresql://localhost:5432/test", "postgres", "password")
 );
}

// Использование @BeforeEach с параметрами
@BeforeEach
void setUpDatabase(@ArgumentsAccessor ArgumentsAccessor accessor) {
 String url = accessor.getString(0);
 String username = accessor.getString(1);
 String password = accessor.getString(2);

 // Настройка базы данных для каждого параметризованного теста
 databaseManager.setupDatabase(url, username, password);
}
```

## Q10. Как организовать структуру тестов?

### 1. Структура проекта

```text
src/
├── main/java/
│ └── com/example/
│ ├── domain/
│ │ ├── User.java
│ │ └── Order.java
│ ├── service/
│ │ ├── UserService.java
│ │ └── OrderService.java
│ └── repository/
│ ├── UserRepository.java
│ └── OrderRepository.java
└── test/java/
 └── com/example/
 ├── domain/
 │ ├── UserTest.java
 │ └── OrderTest.java
 ├── service/
 │ ├── UserServiceTest.java
 │ └── OrderServiceTest.java
 └── repository/
 ├── UserRepositoryTest.java
 └── OrderRepositoryTest.java
```

### 2. `Test Fixtures` и `Setup`

```java
// Test fixture для повторного использования
public abstract class AbstractServiceTest {

 @Autowired
 protected UserRepository userRepository;

 @Autowired
 protected OrderRepository orderRepository;

 protected User testUser;
 protected Order testOrder;

 @BeforeEach
 void setUp() {
 // Очистка данных
 orderRepository.deleteAll();
 userRepository.deleteAll();

 // Создание тестовых данных
 testUser = createTestUser();
 testOrder = createTestOrder(testUser);
 }

 protected User createTestUser() {
 User user = new User("test@example.com", "password");
 return userRepository.save(user);
 }

 protected Order createTestOrder(User user) {
 Order order = new Order(user, BigDecimal.valueOf(100));
 return orderRepository.save(order);
 }
}

// Использование fixture
public class OrderServiceTest extends AbstractServiceTest {

 @Autowired
 private OrderService orderService;

 @Test
 void shouldCreateOrder() {
 // testUser и testOrder уже созданы в setUp()
 assertNotNull(testOrder);
 assertEquals(testUser, testOrder.getUser());
 }
}
```

### 3. `Test Data Builders`

```java
// Builder pattern для тестовых данных
public class UserBuilder {

 private String email = "user@example.com";
 private String password = "password";
 private UserStatus status = UserStatus.ACTIVE;
 private LocalDateTime createdAt = LocalDateTime.now();

 public UserBuilder email(String email) {
 this.email = email;
 return this;
 }

 public UserBuilder password(String password) {
 this.password = password;
 return this;
 }

 public UserBuilder status(UserStatus status) {
 this.status = status;
 return this;
 }

 public UserBuilder createdAt(LocalDateTime createdAt) {
 this.createdAt = createdAt;
 return this;
 }

 public User build() {
 User user = new User(email, password);
 user.setStatus(status);
 user.setCreatedAt(createdAt);
 return user;
 }
}

// Использование builder'а
public class UserServiceTest {

 @Test
 void shouldCreateActiveUser() {
 User user = new UserBuilder().email("john@example.com").status(UserStatus.ACTIVE).build();

 User saved = userService.createUser(user);
 assertEquals(UserStatus.ACTIVE, saved.getStatus());
 }

 @Test
 void shouldCreateInactiveUser() {
 User user = new UserBuilder().email("inactive@example.com").status(UserStatus.INACTIVE).build();

 User saved = userService.createUser(user);
 assertEquals(UserStatus.INACTIVE, saved.getStatus());
 }
}
```

### 4. `Test Categories` и `Grouping`

```java
// Тестовые категории
public interface UnitTest {
}

public interface IntegrationTest {
}

public interface SlowTest {
}

// Применение категорий
@Tag("unit")
public class CalculatorTest {

 @Test
 void shouldAddNumbers() {
 // Быстрый unit тест
 }
}

@Tag("integration")
public class UserRepositoryTest {

 @Test
 void shouldSaveUserToDatabase() {
 // Интеграционный тест с БД
 }
}

@Tag("slow")
public class PerformanceTest {

 @Test
 void shouldHandleHighLoad() {
 // Медленный тест производительности
 }
}
```

### 5. `Test Execution` и `Reporting`

```xml
<!-- pom.xml - настройка тестов -->
<plugin>
 <groupId>org.apache.maven.plugins</groupId>
 <artifactId>maven-surefire-plugin</artifactId>
 <version>3.0.0</version>
 <configuration>
 <!-- Запуск только unit тестов -->
 <groups>unit</groups>
 <excludedGroups>slow</excludedGroups>

 <!-- Отчеты -->
 <reportFormat>html</reportFormat>
 <useFile>true</useFile>

 <!-- Параллельное выполнение -->
 <parallel>classes</parallel>
 <threadCount>4</threadCount>

 <!-- Таймауты -->
 <forkedProcessTimeoutInSeconds>300</forkedProcessTimeoutInSeconds>
 </configuration>
</plugin>
```

### 6. `Test Naming Conventions`

```java
public class UserServiceTest {

 // MethodName_Condition_ExpectedResult
 @Test
 void createUser_ValidData_ReturnsCreatedUser() {
 // Тест создания пользователя с валидными данными
 }

 @Test
 void createUser_DuplicateEmail_ThrowsException() {
 // Тест создания пользователя с дублирующим email
 }

 @Test
 void getUserById_ExistingId_ReturnsUser() {
 // Тест получения существующего пользователя
 }

 @Test
 void getUserById_NonExistingId_ReturnsNull() {
 // Тест получения несуществующего пользователя
 }

 // Given_When_Then (BDD style)
 @Test
 void givenValidUser_whenCreating_thenReturnsCreatedUser() {
 // BDD naming
 }

 @Test
 void whenUserIsInactive_thenCannotLogin() {
 // When_Then naming
 }
}
```

### 7. `Test Configuration Management`

```java
// Конфигурация для разных сред тестирования
@Configuration
@Profile("test")
public class TestConfiguration {

 @Bean
 @Primary
 public DataSource dataSource() {
 // In-memory H2 база для unit тестов
 return new EmbeddedDatabaseBuilder().setType(EmbeddedDatabaseType.H2).addScript("schema.sql").addScript("test-data.sql").build();
 }

 @Bean
 public EmailService emailService() {
 // Mock email service для тестов
 return mock(EmailService.class);
 }
}

@Configuration
@Profile("integration-test")
public class IntegrationTestConfiguration {

 @Bean
 @Primary
 public DataSource dataSource() {
 // Реальная тестовая база данных
 return DataSourceBuilder.create().url("jdbc:postgresql://localhost:5432/testdb").username("test").password("test").build();
 }
}
```

### 8. `Test Lifecycle Hooks`

```java
@ExtendWith(TestExecutionListener.class)
public class LifecycleTest {

 @BeforeAll
 static void beforeAllTests() {
 // Настройка перед всеми тестами класса
 System.setProperty("test.mode", "true");
 }

 @BeforeEach
 void beforeEachTest(TestInfo testInfo) {
 // Настройка перед каждым тестом
 System.out.println("Running test: " + testInfo.getDisplayName());
 }

 @AfterEach
 void afterEachTest(TestInfo testInfo, TestReporter testReporter) {
 // Очистка после каждого теста
 testReporter.publishEntry("test.completed", testInfo.getDisplayName());
 }

 @AfterAll
 static void afterAllTests() {
 // Очистка после всех тестов класса
 System.clearProperty("test.mode");
 }
}
```

## Q11. Что такое `AssertJ` и чем он лучше стандартных assertions?

`AssertJ` — fluent assertions библиотека с читаемым синтаксисом: `assertThat`(list).`hasSize`(3).contains("a", "b"). Лучше стандартных `assertEquals`: цепочка проверок, лучшие сообщения об ошибках, поддержка коллекций и `Optional`. Для `Java` 8+ — `assertThat`(optional).`isPresent()`.contains(value). Интеграция с `JUnit 5`.

Практическая ценность ответа обычно повышается, если дополнить определение операционным контекстом: как решение ведёт себя под нагрузкой, при сбоях и в процессе сопровождения. На интервью ожидают, что вы назовёте критерии выбора и способ валидации решения через метрики и проверяемый сценарий.

## Q12. Как тестировать статические методы?

Статические методы сложно мокировать; варианты: (1) Не мокировать (если метод простой и детерминированный); (2) `Mockito.mockStatic()` (`JUnit 5`, с `try-with-resources`); (3) Рефакторинг: вынести статический метод в инстанс-класс и мокировать его. Предпочтительнее избегать статических методов с зависимостями.

В production-процессе это обычно закрепляют автоматизированными проверками и чёткими quality gates, чтобы правило не зависело от ручного контроля. На собеседовании полезно назвать минимальный набор тестов/чеков и как вы избегаете ложных срабатываний.

## Q13. Что такое `test doubles` (`mock`, `stub`, `spy`, `fake`)?

`Mock` — объект с запрограммированным поведением и проверкой вызовов (`Mockito`). `Stub` — объект с фиксированными ответами без проверки. Spy — обёртка реального объекта с частичным мокированием. `Fake` — упрощённая реализация (например, `in-memory` БД вместо реальной). Выбор: `mock` для проверки взаимодействий; `stub` для возврата данных; `fake` для сложной логики.

В production-процессе это обычно закрепляют автоматизированными проверками и чёткими quality gates, чтобы правило не зависело от ручного контроля. На собеседовании полезно назвать минимальный набор тестов/чеков и как вы избегаете ложных срабатываний.

## Q14. Как тестировать асинхронный код (`CompletableFuture`)?

`CompletableFuture.get()` — блокирующее ожидание результата в тесте. join() — то же без checked exception. `Awaitility` — библиотека для ожидания условий: await().atMost(5, SECONDS).until(() -> condition). Для реактивных потоков (`Mono`/`Flux`) — `StepVerifier`. Не использовать `Thread.sleep` без timeout.

В production-процессе это обычно закрепляют автоматизированными проверками и чёткими quality gates, чтобы правило не зависело от ручного контроля. На собеседовании полезно назвать минимальный набор тестов/чеков и как вы избегаете ложных срабатываний.

## Q15. Что такое `@TempDir` и зачем он нужен?

`@TempDir` (`JUnit 5`) — автоматическое создание временной директории для теста; удаляется после теста. `@TempDir Path tempDir` — параметр метода или поле. Используется для тестов с файловыми операциями без ручной очистки.

Практическая ценность ответа обычно повышается, если дополнить определение операционным контекстом: как решение ведёт себя под нагрузкой, при сбоях и в процессе сопровождения. На интервью ожидают, что вы назовёте критерии выбора и способ валидации решения через метрики и проверяемый сценарий.

## Q16. Как тестировать логирование?

Варианты: (1) Не тестировать (логирование — не бизнес-логика); (2) Мокировать `Logger` (`Mockito`) и проверять вызовы; (3) `Logback` test appender (`ListAppender`) для захвата сообщений. Проверять только критичное логирование (ошибки, аудит); не проверять каждый `log.debug`.

В production-процессе это обычно закрепляют автоматизированными проверками и чёткими quality gates, чтобы правило не зависело от ручного контроля. На собеседовании полезно назвать минимальный набор тестов/чеков и как вы избегаете ложных срабатываний.

## Q17. Что такое `@RepeatedTest` и `@Timeout`?

`@RepeatedTest`(10) — запуск теста 10 раз; для проверки стабильности или случайных данных. `@Timeout`(5) — тест должен завершиться за 5 секунд; иначе падает. `@Timeout` на классе — для всех методов. Используется для предотвращения зависаний.

В production-процессе это обычно закрепляют автоматизированными проверками и чёткими quality gates, чтобы правило не зависело от ручного контроля. На собеседовании полезно назвать минимальный набор тестов/чеков и как вы избегаете ложных срабатываний.

## Q18. Как тестировать `Stream API` и `Optional`?

`Stream`: собрать в коллекцию и проверить: `assertThat`(`stream.collect`(`toList()`)).`containsExactly`(...). `Optional`: `assertThat`(optional).`isPresent()`.contains(value) (`AssertJ`) или `assertTrue`(optional.`isPresent()`); `assertEquals`(value, `optional.get`()). Не вызывать get() без проверки `isPresent()` в тестах.

Практический акцент в таких вопросах — показать не только синтаксис, но и эксплуатационные последствия: читаемость, совместимость и профиль производительности. На интервью это обычно усиливают примером типичной ошибки и способом её предотвращения в код-ревью или тестах.

## Q19. Что такое test fixtures и test data builders?

`Test fixture` — подготовка данных и состояния для теста (`@BeforeEach`). `Test data builder` — паттерн для создания тестовых объектов: `UserBuilder().withName("John").withEmail("j@example.com").build()`. Упрощает создание сложных объектов; читаемость; переиспользование. Альтернатива — фабричные методы или библиотеки (`Instancio`, `EasyRandom`).

В production-процессе это обычно закрепляют автоматизированными проверками и чёткими quality gates, чтобы правило не зависело от ручного контроля. На собеседовании полезно назвать минимальный набор тестов/чеков и как вы избегаете ложных срабатываний.

## Q20. Как измерить покрытие кода (`JaCoCo`)?

`JaCoCo` — плагин для `Maven`/`Gradle`; генерирует отчёт покрытия (строки, ветки, методы). Интеграция: `jacoco-maven-plugin`; после mvn test отчёт в target/site/jacoco. Не гнаться за 100%; фокус на критичной логике. Покрытие — не гарантия качества тестов; проверять осмысленность assertions.

Практическая ценность ответа обычно повышается, если дополнить определение операционным контекстом: как решение ведёт себя под нагрузкой, при сбоях и в процессе сопровождения. На интервью ожидают, что вы назовёте критерии выбора и способ валидации решения через метрики и проверяемый сценарий.

## Q21. Как тестировать `equals`/`hashCode`/`toString`?

`EqualsVerifier`/`hashCode`: `EqualsVerifier.forClass(MyClass.class)`.verify(). Проверяет рефлексивность, симметричность, транзитивность, консистентность, `null`. Для `toString` — проверить, что не бросает исключение и содержит ключевые поля. Не обязательно тестировать для простых `POJO`; для сложных — да.

В production-процессе это обычно закрепляют автоматизированными проверками и чёткими quality gates, чтобы правило не зависело от ручного контроля. На собеседовании полезно назвать минимальный набор тестов/чеков и как вы избегаете ложных срабатываний.

## Q22. Что такое `@Nested` и зачем группировать тесты?

`@Nested` — вложенный тестовый класс в `JUnit 5`; группировка связанных тестов. Каждый `@Nested` класс может иметь свои `@BeforeEach`/`@AfterEach`. Удобно для группировки по сценариям (например, «валидные входные данные», «невалидные данные»). Улучшает читаемость и организацию.

В production-процессе это обычно закрепляют автоматизированными проверками и чёткими quality gates, чтобы правило не зависело от ручного контроля. На собеседовании полезно назвать минимальный набор тестов/чеков и как вы избегаете ложных срабатываний.

## Q23. Как тестировать конструкторы и билдеры?

Конструктор: создать объект и проверить поля; для валидации — проверить исключение при невалидных данных. Билдер: проверить, что build() создаёт корректный объект; что default значения применяются; что валидация работает. Не тестировать тривиальные геттеры/сеттеры; фокус на логике.

В production-процессе это обычно закрепляют автоматизированными проверками и чёткими quality gates, чтобы правило не зависело от ручного контроля. На собеседовании полезно назвать минимальный набор тестов/чеков и как вы избегаете ложных срабатываний.

## Q24. Что такое `@DynamicTest` и когда использовать?

`@TestFactory` метод возвращает `Stream`&lt;`DynamicTest`&gt; или коллекцию; каждый `DynamicTest` — тест, сгенерированный в runtime. Используется для `data-driven` тестов с динамическими данными (например, из файла или БД). `DynamicTest.dynamicTest("name", () -> { assertions })`.

В production-процессе это обычно закрепляют автоматизированными проверками и чёткими quality gates, чтобы правило не зависело от ручного контроля. На собеседовании полезно назвать минимальный набор тестов/чеков и как вы избегаете ложных срабатываний.

## Q25. Как тестировать код с зависимостью от времени (`Clock`)?

Инъекция `Clock` в код: `Clock.systemUTC()` в production, `Clock.fixed()` в тестах. В `Spring`: `Clock Bean`; в тестах подменить через `@MockBean` или `@TestConfiguration`. Избегать `LocalDateTime.now()` напрямую; использовать `LocalDateTime.now(clock)`. Альтернатива — библиотеки (например, `time-machine` для подмены времени).

В production-процессе это обычно закрепляют автоматизированными проверками и чёткими quality gates, чтобы правило не зависело от ручного контроля. На собеседовании полезно назвать минимальный набор тестов/чеков и как вы избегаете ложных срабатываний.

## Q26. Что такое `@Tag` и как фильтровать тесты?

`@Tag`("slow") — метка теста или класса; запуск по тегу: mvn test -`Dgroups`=slow или в `IDE`. Используется для разделения быстрых и медленных тестов, интеграционных и `unit`. В `CI`: быстрые тесты на каждый коммит; медленные — по расписанию. Несколько тегов: `@Tag`("integration") `@Tag`("database").

В production-процессе это обычно закрепляют автоматизированными проверками и чёткими quality gates, чтобы правило не зависело от ручного контроля. На собеседовании полезно назвать минимальный набор тестов/чеков и как вы избегаете ложных срабатываний.

## Q27. Как тестировать рефлексию и аннотации?

Создать объект с аннотацией; прочитать через `getAnnotation()`; проверить значения полей аннотации. Для рефлексии: вызвать метод через `Method.invoke()`; проверить результат. Тестировать логику, использующую рефлексию (например, маппинг); не тестировать саму рефлексию `JVM`.

В production-процессе это обычно закрепляют автоматизированными проверками и чёткими quality gates, чтобы правило не зависело от ручного контроля. На собеседовании полезно назвать минимальный набор тестов/чеков и как вы избегаете ложных срабатываний.

## Q28. Что такое `AssertJ soft assertions`?

`SoftAssertions` — накопление ошибок; все проверки выполняются, затем все ошибки выводятся разом. `SoftAssertions` softly = new `SoftAssertions()`; softly.`assertThat`(x).`isEqualTo`(1); softly.`assertThat`(y).`isEqualTo`(2); softly.`assertAll()`;. Полезно для проверки нескольких полей объекта; видно все несоответствия, а не только первое.

Практическая ценность ответа обычно повышается, если дополнить определение операционным контекстом: как решение ведёт себя под нагрузкой, при сбоях и в процессе сопровождения. На интервью ожидают, что вы назовёте критерии выбора и способ валидации решения через метрики и проверяемый сценарий.

## Q29. Как тестировать многопоточный код?

Сложно; предпочтительнее тестировать логику без многопоточности (выделить в методы). Для проверки race conditions: `ConcurrentUnit`, `Awaitility`, `CountDownLatch` для синхронизации потоков. Запускать тест многократно (flaky test detection). Для `ExecutorService` — использовать тестовый executor с контролируемым выполнением.

В production-процессе это обычно закрепляют автоматизированными проверками и чёткими quality gates, чтобы правило не зависело от ручного контроля. На собеседовании полезно назвать минимальный набор тестов/чеков и как вы избегаете ложных срабатываний.

## Q30. Best practices для unit-тестов?

(1) Один тест — одна проверка (или связанная группа). (2) Тесты независимы (порядок не важен). (3) Быстрые (миллисекунды). (4) Читаемые (`Given-When-Then`, ясные имена). (5) Не тестировать чужой код (фреймворки, библиотеки). (6) Мокировать зависимости. (7) Не дублировать production-код в тестах. (8) Рефакторить тесты как и код.

В production-процессе это обычно закрепляют автоматизированными проверками и чёткими quality gates, чтобы правило не зависело от ручного контроля. На собеседовании полезно назвать минимальный набор тестов/чеков и как вы избегаете ложных срабатываний.
