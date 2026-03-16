---
title: "Mockito"
description: "Кратко: Mockito — фреймворк для создания моков в Java-тестах. Моки, стабы, верификация, матчеры аргументов, частичные моки (spy), аннотации."
tags: ["testing", "unit-testing", "mockito"]
difficulty: "intermediate"
prerequisites: []
next: []
updated: "2026-02-11"
---
# Mockito

Кратко: **Mockito** — фреймворк для создания моков в Java-тестах. Моки, стабы, верификация, матчеры аргументов, частичные моки (spy), аннотации.

**Дата последнего обновления:** 2026-02-06

## Полезные ссылки

- [Mockito Javadoc](https://javadoc.io/doc/org.mockito/mockito-core/latest/org/mockito/Mockito.html)
- [Mockito GitHub](https://github.com/mockito/mockito)
- [Baeldung — Mockito Tutorial](https://www.baeldung.com/mockito-series)

### См. также

- [JUnit](junit.md) — тест-раннер
- [Java Basics](../../../languages/java/java-basics.md) — основы Java
- [Testcontainers](../../../libraries/java/java-testcontainers.md) — интеграционные тесты
- [Unit Testing](../) — юнит-тестирование
- [Testing Tools Overview](../../testing-tools/testing-tools-overview.md) — обзор инструментов

---

## Содержание

- [Введение](#введение-в-mockito)
- [Установка](#установка-и-настройка)
- [Создание моков](#создание-моков)
- [Стабы (Stubbing)](#стабы-stubbing)
- [Верификация](#верификация-verification)
- [Аргументы (Argument Matchers)](#аргументы-argument-matchers)
- [Частичные моки (Spy)](#частичные-моки-partial-mocks)
- [Исключения](#исключения-и-обработка-ошибок)
- [Аннотации](#аннотации-mockito)
- [Интеграция с JUnit 5](#интеграция-с-junit-5)
- [Spy vs Mock](#spy-vs-mock)
- [Ответы по умолчанию](#ответы-по-умолчанию)
- [Лучшие практики](#лучшие-практики)
- [Примеры](#примеры-использования)
- [BDD, final/static, InOrder](#bdd-final-static-inorder)
- [Решение проблем](#решение-проблем)
- [Частые вопросы](#частые-вопросы)
- [Шпаргалка](#шпаргалка)
- [Заключение](#заключение)

---

## Введение в Mockito

Mockito — фреймворк для создания моков (mock objects) в Java-тестах. Моки изолируют тестируемый код от зависимостей, подменяя реальные объекты контролируемым поведением.

### Основные концепции

| Термин | Описание |
|--------|----------|
| **Mock** | Объект-заглушка с настраиваемым поведением |
| **Stub** | Настройка возвращаемых значений/исключений для метода мока |
| **Verify** | Проверка, что метод мока был вызван с ожидаемыми аргументами и числом вызовов |
| **Spy** | Частичный мок: реальная реализация + возможность переопределять отдельные методы |

---

## Установка и настройка

### Maven

```xml
<dependency>
    <groupId>org.mockito</groupId>
    <artifactId>mockito-core</artifactId>
    <version>5.7.0</version>
    <scope>test</scope>
</dependency>
<dependency>
    <groupId>org.mockito</groupId>
    <artifactId>mockito-junit-jupiter</artifactId>
    <version>5.7.0</version>
    <scope>test</scope>
</dependency>
```

### Gradle

```groovy
    testImplementation 'org.mockito:mockito-core:5.7.0'
    testImplementation 'org.mockito:mockito-junit-jupiter:5.7.0'
```

---

## Создание моков

- **Статический метод:** `mock(Class)`.
- **Аннотация:** `@Mock` + `@ExtendWith(MockitoExtension.class)`.
- **С настройками:** `mock(List.class, withSettings().defaultAnswer(RETURNS_SMART_NULLS))`.

```java
import static org.mockito.Mockito.*;

List<String> mockedList = mock(List.class);

// С JUnit 5
@ExtendWith(MockitoExtension.class)
class UserServiceTest {
    @Mock
    private UserRepository userRepository;
}
```

---

## Стабы (Stubbing)

Определяют поведение мока при вызове методов.

| Задача | Синтаксис |
|--------|-----------|
| Возврат значения | `when(mock.get(0)).thenReturn("first")` |
| Последовательные возвраты | `when(mock.get(anyInt())).thenReturn("a").thenReturn("b")` |
| Исключение | `when(mock.get(0)).thenThrow(new RuntimeException())` |
| Произвольная логика | `when(mock.get(anyInt())).thenAnswer(inv -> "element-" + inv.getArgument(0))` |
| Альтернатива для spy/void | `doReturn("x").when(mock).get(0)`; `doNothing().when(mock).clear()` |

```java
when(mockedList.get(0)).thenReturn("first");
when(mockedList.get(anyInt())).thenAnswer(inv -> "element-" + inv.getArgument(0));
doThrow(new RuntimeException()).when(mockedList).clear();
```

---

## Верификация (Verification)

- **Один вызов:** `verify(mock).add("one")`
- **Количество:** `times(n)`, `atLeast(n)`, `atMost(n)`, `never()`, `atLeastOnce()`
- **Порядок:** `InOrder inOrder = inOrder(mock1, mock2); inOrder.verify(mock1).m1(); inOrder.verify(mock2).m2();`
- **Никаких других вызовов:** `verifyNoMoreInteractions(mock)`
- **Мок не вызывался:** `verifyZeroInteractions(mock)` / `verifyNoInteractions(mock)`

```java
mockedList.add("one");
verify(mockedList).add("one");
verify(mockedList, times(3)).add("one");
verify(mockedList, never()).add("two");
```

---

## Аргументы (Argument Matchers)

Матчеры позволяют гибко задавать и проверять аргументы. **Важно:** если для одного аргумента используется матчер, все аргументы в вызове должны быть матчерами.

| Матчер | Описание |
|--------|----------|
| `any()`, `any(Class)` | Любой объект (включая null) / указанного типа |
| `anyInt()`, `anyLong()`, `anyString()` | Любое число / строка |
| `anyList()`, `anyMap()`, `anySet()` | Любая коллекция |
| `eq(value)` | Равенство по значению |
| `isNull()`, `isNotNull()` | null / не null |
| `nullable(Class)` | null или объект типа |
| `argThat(Predicate)` | Условие по аргументу |

```java
when(mockedList.get(anyInt())).thenReturn("element");
when(service.process(anyString(), eq(100))).thenReturn("result");
verify(repo).save(argThat(user -> user.getName().equals("John")));
```

---

## Частичные моки (Partial Mocks)

**Spy** — обёртка над реальным объектом; непереопределённые методы вызываются по-настоящему. Для переопределения на spy предпочтительно использовать `doReturn().when(spy).method()`, чтобы не вызывать реальный метод при стабировании.

```java
List<String> realList = new ArrayList<>();
List<String> spyList = spy(realList);
spyList.add("one");
doReturn(100).when(spyList).size();
assertEquals(100, spyList.size());
assertEquals("one", spyList.get(0)); // реальный метод
```

С JUnit 5: `@Spy` и при необходимости `doReturn().when(spy).method()`.

---

## Исключения и обработка ошибок

- Стаб исключения: `when(mock.get(0)).thenThrow(new RuntimeException())`
- Для void: `doThrow(new RuntimeException()).when(mock).clear()`
- Последовательно: `when(mock.get(0)).thenThrow(...).thenReturn("ok")`

---

## Аннотации Mockito

| Аннотация | Назначение |
|-----------|------------|
| `@Mock` | Создаёт мок поля |
| `@Spy` | Создаёт spy (нужен реальный экземпляр) |
| `@InjectMocks` | Внедряет моки/spy в тестируемый объект |
| `@Captor` | ArgumentCaptor для захвата аргументов |

```java
@ExtendWith(MockitoExtension.class)
class UserServiceTest {
    @Mock private UserRepository userRepository;
    @Mock private EmailService emailService;
    @InjectMocks private UserService userService;
    @Captor private ArgumentCaptor<User> userCaptor;
    
    @Test
    void testCreateUser() {
        when(userRepository.save(any(User.class))).thenReturn(new User());
        userService.createUser("John", "john@example.com");
        verify(userRepository).save(userCaptor.capture());
        assertEquals("John", userCaptor.getValue().getName());
    }
}
```

---

## Интеграция с JUnit 5

Подключите расширение и при необходимости настройте моки в `@BeforeEach`:

```java
@ExtendWith(MockitoExtension.class)
class UserServiceTest {
    @Mock private UserRepository userRepository;
    @InjectMocks private UserService userService;
    
    @BeforeEach
    void setUp() {
        when(userRepository.count()).thenReturn(10L);
    }
}
```

---

## Spy vs Mock

- **Mock** — полностью подменённый объект; зависимости изолированы; все методы по умолчанию возвращают null/0/false (или заданные ответы).
- **Spy** — реальный объект с возможностью переопределять отдельные методы; подходит, когда нужна часть реального поведения.

---

## Ответы по умолчанию

| Режим | Поведение |
|-------|-----------|
| `RETURNS_DEFAULTS` | null, 0, false |
| `RETURNS_SMART_NULLS` | SmartNull вместо null с подсказкой |
| `RETURNS_MOCKS` | Возвращает моки для сложных типов |
| `RETURNS_DEEP_STUBS` | Цепочки моков: `when(service.getUser().getAddress().getCity()).thenReturn("Moscow")` |

---

## Лучшие практики

1. Мокировать только зависимости, не тестируемый класс.
2. Использовать `@InjectMocks` для внедрения моков.
3. Верифицировать важные вызовы, не все подряд.
4. В `when()` при одном матчере — все аргументы матчеры.
5. Для spy предпочитать `doReturn().when(spy).method()`.
6. Сложные аргументы проверять через `ArgumentCaptor` или `argThat`.
7. Не мокировать value objects.
8. По возможности использовать `RETURNS_SMART_NULLS`.
9. Избегать глубоких стабов и лишних стабов; при необходимости — `lenient()` или `@MockitoSettings(strictness = LENIENT)`.
10. Не злоупотреблять `reset(mock)`; лучше новый мок в `@BeforeEach`.

---

## Примеры использования

### Сервис с зависимостями

```java
@ExtendWith(MockitoExtension.class)
class UserServiceTest {
    @Mock private UserRepository userRepository;
    @Mock private EmailService emailService;
    @InjectMocks private UserService userService;
    
    @Test
    void testCreateUser() {
        when(userRepository.save(any(User.class))).thenReturn(new User());
        User created = userService.createUser("John", "john@example.com");
        assertNotNull(created);
        verify(userRepository).save(any(User.class));
        verify(emailService).sendWelcomeEmail("john@example.com");
    }
    
    @Test
    void testDuplicateEmail() {
        when(userRepository.findByEmail("existing@example.com")).thenReturn(new User());
        assertThrows(DuplicateEmailException.class,
            () -> userService.createUser("John", "existing@example.com"));
        verify(userRepository, never()).save(any());
    }
}
```

### ArgumentCaptor — несколько вызовов

```java
ArgumentCaptor<Order> captor = ArgumentCaptor.forClass(Order.class);
verify(repository, times(2)).save(captor.capture());
List<Order> all = captor.getAllValues();
assertEquals(2, all.size());
```

---

## BDD, final/static, InOrder

- **BDD-стиль (BDDMockito):** `given(repo.save(any())).willReturn(entity);` … `then(repo).should().save(argThat(...));`
- **final-классы и static-методы:** зависимость `mockito-inline`; для static (Mockito 3.4+): `try (MockedStatic<Utils> mocked = mockStatic(Utils.class)) { mocked.when(Utils::now).thenReturn(fixed); }`
- **Порядок вызовов:** `InOrder inOrder = inOrder(repo, notifier); inOrder.verify(repo).save(any()); inOrder.verify(notifier).send(any());`

**lenient-стабы:** если стаб не используется в тесте, в строгом режиме будет `UnnecessaryStubbingException`. Разрешить: `lenient().when(mock.method()).thenReturn(x)` или `@MockitoSettings(strictness = Strictness.LENIENT)` на классе.

---

## Решение проблем

- **Почему UnnecessaryStubbingException?** Задан стаб для метода, который не вызывался. Удалите лишний `when()` или пометьте стаб как `lenient()`.
- **Как мокировать static-метод?** `mockito-inline` + `MockedStatic`: `try (MockedStatic<Utils> mocked = mockStatic(Utils.class)) { mocked.when(Utils::now).thenReturn(fixed); }`.
- **Spy vs Mock?** Mock — полностью подменённый объект. Spy — обёртка над реальным; не переопределённые методы вызываются по-настоящему.
- **Как проверить, что метод не вызывался?** `verify(mock, never()).method(args)` или `verifyNoInteractions(mock)`.
- **Как проверить вызов с null?** `verify(mock).method(isNull())` или `verify(mock).method(eq(null))` (с осторожностью с дженериками).
- **NullPointerException при вызове мока.** Проверьте инициализацию моков (`@ExtendWith(MockitoExtension.class)` или `MockitoAnnotations.openMocks(this)`). В `when()` все аргументы должны быть матчерами, если используется хотя бы один.
- **Stubbing same method with different arguments.** Повторный `when()` перезаписывает стаб. Используйте один стаб с матчерами или несколько `when` с разными матчерами.
- **NotAMockException.** В `verify()`/`when()` передан не мок. Убедитесь, что объект создан через `mock()` или помечен `@Mock`.
- **Verify не находит вызов.** Проверьте совпадение аргументов (например, через ArgumentCaptor), что метод действительно вызывается в коде и что в тестируемый объект передан тот же экземпляр мока.
- **Как мокировать private-метод?** Обычно не мокируют; тестируют через public API. При необходимости — рефакторинг (вынести в отдельный класс или сделать package-private).

Исключения верификации: **TooManyActualInvocations**, **TooFewActualInvocations**, **NoInteractionsWanted**, **NeverWantedButInvoked** — проверьте логику теста и при необходимости используйте `times(n)` / `atLeast(n)`.

---

## Частые вопросы

*(См. раздел «Решение проблем» выше — типичные вопросы и ответы объединены.)*

---

## Шпаргалка

| Задача | Код |
|--------|-----|
| Создать мок | `mock(Class)` или `@Mock` |
| Стаб возврата | `when(mock.method()).thenReturn(x)` |
| Стаб исключения | `when(mock.method()).thenThrow(Exception)` |
| Стаб для void | `doNothing().when(mock).voidMethod()` |
| Исключение для void | `doThrow(Exception).when(mock).voidMethod()` |
| Проверка вызова | `verify(mock).method(args)` |
| Количество вызовов | `verify(mock, times(n)).method(args)` |
| Не вызывался | `verify(mock, never()).method(args)` |
| Захват аргумента | `ArgumentCaptor.forClass(Class)` + `captor.capture()` + `captor.getValue()` |
| Порядок | `InOrder inOrder = inOrder(mock1, mock2); inOrder.verify(mock1).m1();` |
| Spy | `spy(realObject)` или `@Spy`; стаб: `doReturn(x).when(spy).method()` |
| Spring-бин | `@MockBean` / `@SpyBean` в Spring Boot-тестах |

---

## Заключение

Mockito — стандартный инструмент для моков в Java-тестах. Цикл: создать мок (`mock()` или `@Mock`), задать поведение (`when().thenReturn()` и др.), выполнить код, проверить вызовы (`verify()`). Используйте матчеры для аргументов, ArgumentCaptor для сложных объектов, Spy при необходимости частичной реальной реализации. Для final-классов и static-методов подключайте mockito-inline. В Kotlin часто используют mockito-kotlin или MockK; в Spring — `@MockBean`/`@SpyBean`.

**Дата последнего обновления:** 2026-02-06
