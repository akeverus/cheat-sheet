---
title: "Вопросы на собеседовании: Mockito"
description: "45 вопросов по Mockito для подготовки к техническому собеседованию: mock/stub/spy, аннотации, verify, ArgumentCaptor, BDDMockito, MockedStatic/Construction, MockSettings, Answer, Spring Boot интеграция, Kotlin и антипаттерны."
tags:
  - interview
  - testing
  - mockito-interview
type: "interview"
difficulty: "intermediate"
aliases:
  - "Вопросы на собеседовании"
  - "Mockito"
  - "Mockito interview"
  - "Mockito собеседование"
prerequisites:
  - "[[mockito]]"
next: []
updated: "2026-04-25"
---

# Вопросы на собеседовании: Mockito

## Содержание

- [Q1. Что такое Mockito и зачем он нужен?](#q1-что-такое-mockito-и-зачем-он-нужен)
- [Q2. Чем отличаются mock, stub, spy и fake?](#q2-чем-отличаются-mock-stub-spy-и-fake)
- [Q3. (!) Как создать mock-объект в Mockito?](#q3--как-создать-mock-объект-в-mockito)
- [Q4. Что делает аннотация `@Mock`?](#q4-что-делает-аннотация-mock)
- [Q5. Что делает аннотация `@InjectMocks`?](#q5-что-делает-аннотация-injectmocks)
- [Q6. (!) Чем отличается `@Spy` от `@Mock`?](#q6--чем-отличается-spy-от-mock)
- [Q7. Что такое `@Captor` и для чего используется?](#q7-что-такое-captor-и-для-чего-используется)
- [Q8. (!) Чем отличается `MockitoExtension` от `MockitoAnnotations.openMocks()`?](#q8--чем-отличается-mockitoextension-от-mockitoannotationsopenMocks)
- [Q9. Как работает `when().thenReturn()`?](#q9-как-работает-whenthenreturn)
- [Q10. (!) Чем отличается `when().thenReturn()` от `doReturn().when()`?](#q10--чем-отличается-whenthenreturn-от-doreturnwhen)
- [Q11. Как заставить mock выбросить исключение?](#q11-как-заставить-mock-выбросить-исключение)
- [Q12. Что такое `thenAnswer()` и когда его применять?](#q12-что-такое-thenanswer-и-когда-его-применять)
- [Q13. Как заглушить `void`-метод с помощью `doNothing()`?](#q13-как-заглушить-void-метод-с-помощью-donothing)
- [Q14. (!) Как проверить вызов метода с помощью `verify()`?](#q14--как-проверить-вызов-метода-с-помощью-verify)
- [Q15. Какие режимы верификации поддерживает Mockito?](#q15-какие-режимы-верификации-поддерживает-mockito)
- [Q16. Что делают `verifyNoInteractions()` и `verifyNoMoreInteractions()`?](#q16-что-делают-verifynointeractions-и-verifynomoreinteractions)
- [Q17. (!) Как захватить аргумент с помощью `ArgumentCaptor`?](#q17--как-захватить-аргумент-с-помощью-argumentcaptor)
- [Q18. Какие матчеры аргументов предоставляет `ArgumentMatchers`?](#q18-какие-матчеры-аргументов-предоставляет-argumentmatchers)
- [Q19. Что такое `argThat()` и когда он нужен?](#q19-что-такое-argthat-и-когда-он-нужен)
- [Q20. (!) Как проверить порядок вызовов с помощью `InOrder`?](#q20--как-проверить-порядок-вызовов-с-помощью-inorder)
- [Q21. Что такое BDDMockito и как использовать стиль `given/when/then`?](#q21-что-такое-bddmockito-и-как-использовать-стиль-givenwhenThen)
- [Q22. (!) Что такое `@MockBean` и `@SpyBean` в Spring Boot?](#q22--что-такое-mockbean-и-spybean-в-spring-boot)
- [Q23. Как работает Mockito с `@ExtendWith(MockitoExtension.class)` в JUnit 5?](#q23-как-работает-mockito-с-extendwithmockitoextensionclass-в-junit-5)
- [Q24. Что делает `Mockito.reset()`?](#q24-что-делает-mockitoreset)
- [Q25. (!) Как мокировать статические методы с помощью `mockStatic()`?](#q25--как-мокировать-статические-методы-с-помощью-mockstatic)
- [Q26. Как мокировать `final`-классы и методы?](#q26-как-мокировать-final-классы-и-методы)
- [Q27. Что такое `RETURNS_DEEP_STUBS` и когда его применять?](#q27-что-такое-returns_deep_stubs-и-когда-его-применять)
- [Q28. (!) Что такое strict stubbing и зачем он нужен?](#q28--что-такое-strict-stubbing-и-зачем-он-нужен)
- [Q29. Как сделать отдельный stub ненастойчивым с помощью `lenient()`?](#q29-как-сделать-отдельный-stub-ненастойчивым-с-помощью-lenient)
- [Q30. Можно ли вернуть разные значения при последовательных вызовах?](#q30-можно-ли-вернуть-разные-значения-при-последовательных-вызовах)
- [Q31. Чем отличается `Mockito.spy()` от `@Spy`?](#q31-чем-отличается-mockitospy-от-spy)
- [Q32. (!) Каковы типичные ошибки при использовании Mockito?](#q32--каковы-типичные-ошибки-при-использовании-mockito)
- [Q33. Что такое `UnnecessaryStubbingException` и как её избежать?](#q33-что-такое-unnecessarystubbingexception-и-как-её-избежать)
- [Q34. Когда не стоит использовать Mockito?](#q34-когда-не-стоит-использовать-mockito)
- [Q35. Как мокировать конструкторы с помощью `mockConstruction()`?](#q35-как-мокировать-конструкторы-с-помощью-mockconstruction)
- [Q36. (!) В чём разница между `mockito-core` и `mockito-inline`?](#q36--в-чём-разница-между-mockito-core-и-mockito-inline)
- [Q37. Что такое `MockSettings` и какие опции он предоставляет?](#q37-что-такое-mocksettings-и-какие-опции-он-предоставляет)
- [Q38. Какие встроенные `Answer` доступны в Mockito?](#q38-какие-встроенные-answer-доступны-в-mockito)
- [Q39. (!) Когда выбирать `ArgumentCaptor`, а когда `argThat()`?](#q39--когда-выбирать-argumentcaptor-а-когда-argthat)
- [Q40. Как тестировать код с callback-функциями через `doAnswer()`?](#q40-как-тестировать-код-с-callback-функциями-через-doanswer)
- [Q41. (!) В чём ограничение области видимости `MockedStatic` по потокам?](#q41--в-чём-ограничение-области-видимости-mockedstatic-по-потокам)
- [Q42. Что делает `thenCallRealMethod()` и чем отличается от `@Spy`?](#q42-что-делает-thencallrealmethod-и-чем-отличается-от-spy)
- [Q43. Как инжектировать `@Mock` и `@Captor` в параметры метода JUnit 5?](#q43-как-инжектировать-mock-и-captor-в-параметры-метода-junit-5)
- [Q44. (!) Какие особенности у Mockito в Kotlin и зачем `mockito-kotlin`?](#q44--какие-особенности-у-mockito-в-kotlin-и-зачем-mockito-kotlin)
- [Q45. Что такое `@MockitoSettings` и как управлять strictness на уровне класса?](#q45-что-такое-mockitosettings-и-как-управлять-strictness-на-уровне-класса)
- [See also](#see-also)

---

## Q1. Что такое Mockito и зачем он нужен?

**Mockito** — популярная Java-библиотека для создания тестовых двойников (test doubles). Позволяет изолировать тестируемый класс от его зависимостей, подменяя их на управляемые объекты.

Зачем нужен:
- **Изоляция** — тест проверяет только одну единицу кода
- **Скорость** — нет реальных HTTP-вызовов, баз данных, файловой системы
- **Контроль** — можно симулировать любой сценарий (исключение, медленный ответ, конкретное значение)
- **Верификация** — можно проверить, что метод был вызван с нужными аргументами

```xml
<dependency>
    <groupId>org.mockito</groupId>
    <artifactId>mockito-core</artifactId>
    <version>5.x.x</version>
    <scope>test</scope>
</dependency>
```

---


> [!mcq]
> - [x] Правильный ответ | Объяснение концепции 2-3 предложения Ключевое отличие и best practice в production.
> - [ ] Неправильный вариант 1 | Почему ошибка в этом подходе Частая ошибка в реальном коде.
> - [ ] Неправильный вариант 2 | Это смежное, но отличное понятие Частая ошибка в реальном коде.
> - [ ] Неправильный вариант 3 | Противоположное направление## Q2. Чем отличаются mock, stub, spy и fake? Это антипаттерн или неправильный выбор в production.

| Тип | Описание | Пример |
|-----|----------|--------|
| **Mock** | Объект с проверяемым поведением — записывает вызовы, используется для `verify()` | `Mockito.mock(Service.class)` |
| **Stub** | Возвращает заранее заданные данные; поведение важно, проверка вызовов — нет | `when(repo.find(1)).thenReturn(entity)` |
| **Spy** | Обёртка над реальным объектом; часть методов реальная, часть — заглушки | `Mockito.spy(new MyService())` |
| **Fake** | Упрощённая рабочая реализация (в памяти, без Mockito) | `HashMap` вместо реального кэша |

В Mockito **mock по умолчанию является и стабом**: на него можно и настраивать ответы, и верифицировать вызовы.

---


> [!mcq]
> - [x] Правильный ответ | Объяснение концепции 2-3 предложения Ключевое отличие и best practice в production.
> - [ ] Неправильный вариант 1 | Почему ошибка в этом подходе Частая ошибка в реальном коде.
> - [ ] Неправильный вариант 2 | Это смежное, но отличное понятие Частая ошибка в реальном коде.
> - [ ] Неправильный вариант 3 | Противоположное направление## Q3. (!) Как создать mock-объект в Mockito? Это антипаттерн или неправильный выбор в production.

Три способа:

```java
// 1. Статический фабричный метод
UserRepository repo = Mockito.mock(UserRepository.class);

// 2. Аннотация (требует MockitoExtension или openMocks)
@Mock
UserRepository repo;

// 3. Начиная с Mockito 4.10+ — mock() без статического импорта (Java 21+)
var repo = mock(UserRepository.class);
```

По умолчанию mock:
- возвращает `null` для ссылочных типов
- возвращает `0` / `false` / пустые коллекции для примитивов и `Optional`
- не выполняет реальную логику

---


> [!mcq]
> - [x] Правильный ответ | Объяснение концепции 2-3 предложения Ключевое отличие и best practice в production.
> - [ ] Неправильный вариант 1 | Почему ошибка в этом подходе Частая ошибка в реальном коде.
> - [ ] Неправильный вариант 2 | Это смежное, но отличное понятие Частая ошибка в реальном коде.
> - [ ] Неправильный вариант 3 | Противоположное направление## Q4. Что делает аннотация `@Mock`? Это антипаттерн или неправильный выбор в production.

`@Mock` создаёт mock-объект и внедряет его в поле, помеченное аннотацией. Аналог `Mockito.mock(ClassName.class)`, но компактнее.

```java
@ExtendWith(MockitoExtension.class)
class OrderServiceTest {

    @Mock
    PaymentGateway paymentGateway;  // mock создаётся автоматически

    @Test
    void test() {
        when(paymentGateway.charge(any())).thenReturn(true);
        // ...
    }
}
```

Аннотация работает только если включена через `@ExtendWith(MockitoExtension.class)` или `MockitoAnnotations.openMocks(this)`.

---


> [!mcq]
> - [x] Правильный ответ | Объяснение концепции 2-3 предложения Ключевое отличие и best practice в production.
> - [ ] Неправильный вариант 1 | Почему ошибка в этом подходе Частая ошибка в реальном коде.
> - [ ] Неправильный вариант 2 | Это смежное, но отличное понятие Частая ошибка в реальном коде.
> - [ ] Неправильный вариант 3 | Противоположное направление## Q5. Что делает аннотация `@InjectMocks`? Это антипаттерн или неправильный выбор в production.

`@InjectMocks` создаёт реальный экземпляр тестируемого класса и внедряет в него все объявленные `@Mock` / `@Spy` поля — через конструктор, сеттеры или поля (в этом порядке).

```java
@ExtendWith(MockitoExtension.class)
class OrderServiceTest {

    @Mock
    OrderRepository orderRepository;

    @Mock
    PaymentGateway paymentGateway;

    @InjectMocks
    OrderService orderService;  // получит оба мока в конструктор или поля
}
```

Важно: Mockito выбирает самый большой конструктор, который можно удовлетворить имеющимися моками. Если внедрение не удаётся — Mockito молча создаёт объект без внедрения; ошибки не будет.

---


> [!mcq]
> - [x] Правильный ответ | Объяснение концепции 2-3 предложения Ключевое отличие и best practice в production.
> - [ ] Неправильный вариант 1 | Почему ошибка в этом подходе Частая ошибка в реальном коде.
> - [ ] Неправильный вариант 2 | Это смежное, но отличное понятие Частая ошибка в реальном коде.
> - [ ] Неправильный вариант 3 | Противоположное направление## Q6. (!) Чем отличается `@Spy` от `@Mock`? Это антипаттерн или неправильный выбор в production.

| Характеристика | `@Mock` | `@Spy` |
|----------------|---------|--------|
| Реальная логика | Не выполняется | Выполняется по умолчанию |
| Настройка ответов | `when().thenReturn()` | `doReturn().when()` (рекомендуется) |
| Назначение | Полная изоляция | Частичная заглушка реального объекта |

```java
@Spy
List<String> list = new ArrayList<>();

@Test
void test() {
    // реальный метод add() будет вызван
    list.add("hello");
    verify(list).add("hello");

    // заглушаем только size()
    doReturn(100).when(list).size();
}
```

Spy требует реального экземпляра. `@Spy` без инициализации попробует создать объект через конструктор без аргументов.

---


> [!mcq]
> - [x] Правильный ответ | Объяснение концепции 2-3 предложения Ключевое отличие и best practice в production.
> - [ ] Неправильный вариант 1 | Почему ошибка в этом подходе Частая ошибка в реальном коде.
> - [ ] Неправильный вариант 2 | Это смежное, но отличное понятие Частая ошибка в реальном коде.
> - [ ] Неправильный вариант 3 | Противоположное направление## Q7. Что такое `@Captor` и для чего используется? Частая ошибка в реальном коде.

`@Captor` создаёт `ArgumentCaptor` с нужным дженерик-типом. Используется для захвата аргумента, переданного в mock, чтобы потом проверить его содержимое.

```java
@Captor
ArgumentCaptor<UserEvent> eventCaptor;

@Test
void shouldPublishCorrectEvent() {
    service.createUser("Alice");

    verify(eventBus).publish(eventCaptor.capture());
    UserEvent event = eventCaptor.getValue();
    assertThat(event.getName()).isEqualTo("Alice");
}
```

Без `@Captor` можно создать вручную:
```java
ArgumentCaptor<UserEvent> captor = ArgumentCaptor.forClass(UserEvent.class);
```

---


> [!mcq]
> - [x] Правильный ответ | Объяснение концепции 2-3 предложения Ключевое отличие и best practice в production.
> - [ ] Неправильный вариант 1 | Почему ошибка в этом подходе Частая ошибка в реальном коде.
> - [ ] Неправильный вариант 2 | Это смежное, но отличное понятие Частая ошибка в реальном коде.
> - [ ] Неправильный вариант 3 | Противоположное направление## Q8. (!) Чем отличается `MockitoExtension` от `MockitoAnnotations.openMocks()`? Это антипаттерн или неправильный выбор в production.

**`@ExtendWith(MockitoExtension.class)`** — JUnit 5 Extension, декларативный подход:
- аннотации `@Mock`, `@Spy`, `@Captor`, `@InjectMocks` обрабатываются автоматически
- включён strict stubbing по умолчанию
- ресурсы освобождаются автоматически после теста

```java
@ExtendWith(MockitoExtension.class)
class MyTest { ... }
```

**`MockitoAnnotations.openMocks(this)`** — программный подход, используется в JUnit 4 или когда нельзя использовать расширение:

```java
@BeforeEach
void setUp() {
    closeable = MockitoAnnotations.openMocks(this);
}

@AfterEach
void tearDown() throws Exception {
    closeable.close();  // важно закрывать, чтобы освободить ресурсы
}
```

Предпочтительно использовать `MockitoExtension` в JUnit 5 — меньше бойлерплейта и строгая проверка заглушек.

---


> [!mcq]
> - [x] Правильный ответ | Объяснение концепции 2-3 предложения Ключевое отличие и best practice в production.
> - [ ] Неправильный вариант 1 | Почему ошибка в этом подходе Частая ошибка в реальном коде.
> - [ ] Неправильный вариант 2 | Это смежное, но отличное понятие Частая ошибка в реальном коде.
> - [ ] Неправильный вариант 3 | Противоположное направление## Q9. Как работает `when().thenReturn()`? Частая ошибка в реальном коде.

`when(mock.method(args)).thenReturn(value)` задаёт заглушку: при вызове `method` с указанными аргументами mock вернёт `value`.

```java
when(userRepository.findById(1L)).thenReturn(Optional.of(new User(1L, "Alice")));

Optional<User> result = userRepository.findById(1L);
assertThat(result).isPresent();
```

Особенности:
- Если аргумент не совпадает — возвращается дефолтное значение (`null`, `0`, и т.д.)
- Можно цеплять: `when(...).thenReturn(first).thenReturn(second)` — первый вызов → `first`, второй и далее → `second`
- Последний `thenReturn` применяется для всех последующих вызовов

---


> [!mcq]
> - [x] Правильный ответ | Объяснение концепции 2-3 предложения Ключевое отличие и best practice в production.
> - [ ] Неправильный вариант 1 | Почему ошибка в этом подходе Частая ошибка в реальном коде.
> - [ ] Неправильный вариант 2 | Это смежное, но отличное понятие Частая ошибка в реальном коде.
> - [ ] Неправильный вариант 3 | Противоположное направление## Q10. (!) Чем отличается `when().thenReturn()` от `doReturn().when()`? Частая ошибка в реальном коде.

| Аспект | `when().thenReturn()` | `doReturn().when()` |
|--------|----------------------|---------------------|
| Работает с `@Mock` | Да | Да |
| Работает с `@Spy` | Частично (реальный метод вызывается при настройке!) | Да (реальный метод НЕ вызывается) |
| Работает с `void`-методами | Нет | Да |
| Читаемость | Выше | Ниже |

```java
// Опасно для spy! Реальный метод size() будет вызван при настройке
when(spyList.size()).thenReturn(100);  // может выбросить NPE или иметь побочный эффект

// Безопасно для spy
doReturn(100).when(spyList).size();
```

**Правило**: для `@Mock` — используй `when().thenReturn()`. Для `@Spy` и `void`-методов — используй `doReturn/doThrow/doNothing/doAnswer`.

---


> [!mcq]
> - [x] Правильный ответ | Объяснение концепции 2-3 предложения Ключевое отличие и best practice в production.
> - [ ] Неправильный вариант 1 | Почему ошибка в этом подходе Частая ошибка в реальном коде.
> - [ ] Неправильный вариант 2 | Это смежное, но отличное понятие Частая ошибка в реальном коде.
> - [ ] Неправильный вариант 3 | Противоположное направление## Q11. Как заставить mock выбросить исключение? Это антипаттерн или неправильный выбор в production.

Для non-void методов:
```java
when(paymentGateway.charge(any()))
    .thenThrow(new PaymentException("Insufficient funds"));
```

Для `void`-методов:
```java
doThrow(new RuntimeException("Network error"))
    .when(emailService).send(any());
```

Можно указать класс исключения (Mockito создаст его сам):
```java
when(repo.findById(anyLong())).thenThrow(IllegalStateException.class);
```

В тесте проверяем:
```java
assertThatThrownBy(() -> service.processPayment(order))
    .isInstanceOf(PaymentException.class)
    .hasMessage("Insufficient funds");
```

---


> [!mcq]
> - [x] Правильный ответ | Объяснение концепции 2-3 предложения Ключевое отличие и best practice в production.
> - [ ] Неправильный вариант 1 | Почему ошибка в этом подходе Частая ошибка в реальном коде.
> - [ ] Неправильный вариант 2 | Это смежное, но отличное понятие Частая ошибка в реальном коде.
> - [ ] Неправильный вариант 3 | Противоположное направление## Q12. Что такое `thenAnswer()` и когда его применять? Частая ошибка в реальном коде.

`thenAnswer()` позволяет задать динамическое поведение на основе переданных аргументов. Используется, когда `thenReturn()` недостаточно.

```java
when(userService.getUserName(anyLong()))
    .thenAnswer(invocation -> {
        Long id = invocation.getArgument(0);
        return "User_" + id;
    });

assertThat(userService.getUserName(42L)).isEqualTo("User_42");
assertThat(userService.getUserName(99L)).isEqualTo("User_99");
```

Типичные случаи:
- Возврат значения на основе аргумента
- Эмуляция `Consumer` или `Function` — вызов переданного callback-а
- Симуляция сложной логики с побочными эффектами

---


> [!mcq]
> - [x] Правильный ответ | Объяснение концепции 2-3 предложения Ключевое отличие и best practice в production.
> - [ ] Неправильный вариант 1 | Почему ошибка в этом подходе Частая ошибка в реальном коде.
> - [ ] Неправильный вариант 2 | Это смежное, но отличное понятие Частая ошибка в реальном коде.
> - [ ] Неправильный вариант 3 | Противоположное направление## Q13. Как заглушить `void`-метод с помощью `doNothing()`? Частая ошибка в реальном коде.

По умолчанию `void`-методы на mock-объектах уже ничего не делают. `doNothing()` явно указывает это, либо переопределяет `doThrow` на spy:

```java
// Явное указание для ясности кода
doNothing().when(emailService).send(any());

// Полезно для spy — перекрыть реальный метод
@Spy
NotificationService notificationService;

doNothing().when(notificationService).sendPushNotification(any());
```

Также можно чередовать поведение:
```java
doNothing()
    .doThrow(new RuntimeException("Second call fails"))
    .when(service).process(any());
```

---


> [!mcq]
> - [x] Правильный ответ | Объяснение концепции 2-3 предложения Ключевое отличие и best practice в production.
> - [ ] Неправильный вариант 1 | Почему ошибка в этом подходе Частая ошибка в реальном коде.
> - [ ] Неправильный вариант 2 | Это смежное, но отличное понятие Частая ошибка в реальном коде.
> - [ ] Неправильный вариант 3 | Противоположное направление## Q14. (!) Как проверить вызов метода с помощью `verify()`? Частая ошибка в реальном коде.

```java
// Проверить, что метод был вызван ровно один раз
verify(userRepository).save(any(User.class));

// С конкретным аргументом
verify(emailService).send(eq("alice@example.com"));

// С точным числом вызовов
verify(cache, times(3)).put(anyString(), any());

// Не вызывался
verify(auditLog, never()).log(any());

// Минимум 2 раза
verify(retryHandler, atLeast(2)).handle(any());

// Максимум 5 раз
verify(limiter, atMost(5)).check(anyString());
```

`verify()` работает с матчерами `ArgumentMatchers`. Если хотя бы один аргумент — матчер, все остальные тоже должны быть матчерами (нельзя смешивать с литералами).

---


> [!mcq]
> - [x] Правильный ответ | Объяснение концепции 2-3 предложения Ключевое отличие и best practice в production.
> - [ ] Неправильный вариант 1 | Почему ошибка в этом подходе Частая ошибка в реальном коде.
> - [ ] Неправильный вариант 2 | Это смежное, но отличное понятие Частая ошибка в реальном коде.
> - [ ] Неправильный вариант 3 | Противоположное направление## Q15. Какие режимы верификации поддерживает Mockito? Это антипаттерн или неправильный выбор в production.

| Метод | Описание |
|-------|----------|
| `times(n)` | Ровно `n` вызовов |
| `never()` | Ноль вызовов |
| `atLeastOnce()` | Минимум 1 вызов (значение по умолчанию для `verify()`) |
| `atLeast(n)` | Минимум `n` вызовов |
| `atMost(n)` | Максимум `n` вызовов |
| `only()` | Ровно 1 вызов этого метода и никаких других на данном mock |

```java
verify(service, times(2)).process(any());
verify(service, atLeastOnce()).init();
verify(service, only()).start();  // только этот метод и больше ничего
```

---


> [!mcq]
> - [x] Правильный ответ | Объяснение концепции 2-3 предложения Ключевое отличие и best practice в production.
> - [ ] Неправильный вариант 1 | Почему ошибка в этом подходе Частая ошибка в реальном коде.
> - [ ] Неправильный вариант 2 | Это смежное, но отличное понятие Частая ошибка в реальном коде.
> - [ ] Неправильный вариант 3 | Противоположное направление## Q16. Что делают `verifyNoInteractions()` и `verifyNoMoreInteractions()`? Частая ошибка в реальном коде.

**`verifyNoInteractions(mock)`** — проверяет, что ни один метод mock-объекта не был вызван:

```java
verifyNoInteractions(auditService, notificationService);
```

**`verifyNoMoreInteractions(mock)`** — проверяет, что после всех `verify()` больше никаких невёрифицированных вызовов не было:

```java
verify(repo).save(user);
verifyNoMoreInteractions(repo);  // убедиться, что delete/update не вызывались
```

Различие: `verifyNoInteractions` — для всего теста, `verifyNoMoreInteractions` — для проверки "всё уже проверено".

Злоупотребление `verifyNoMoreInteractions()` — антипаттерн: делает тесты хрупкими при добавлении новой логики.

---


> [!mcq]
> - [x] Правильный ответ | Объяснение концепции 2-3 предложения Ключевое отличие и best practice в production.
> - [ ] Неправильный вариант 1 | Почему ошибка в этом подходе Частая ошибка в реальном коде.
> - [ ] Неправильный вариант 2 | Это смежное, но отличное понятие Частая ошибка в реальном коде.
> - [ ] Неправильный вариант 3 | Противоположное направление## Q17. (!) Как захватить аргумент с помощью `ArgumentCaptor`? Частая ошибка в реальном коде.

`ArgumentCaptor` позволяет захватить объект, переданный в mock, и сделать assertions по его состоянию:

```java
@Captor
ArgumentCaptor<Order> orderCaptor;

@Test
void shouldSaveOrderWithCorrectStatus() {
    service.placeOrder(orderRequest);

    verify(orderRepository).save(orderCaptor.capture());
    Order savedOrder = orderCaptor.getValue();

    assertThat(savedOrder.getStatus()).isEqualTo(OrderStatus.PENDING);
    assertThat(savedOrder.getTotal()).isEqualByComparingTo(BigDecimal.TEN);
}
```

Для нескольких вызовов:
```java
verify(repo, times(3)).save(captor.capture());
List<Order> allOrders = captor.getAllValues();
assertThat(allOrders).hasSize(3);
```

---


> [!mcq]
> - [x] Правильный ответ | Объяснение концепции 2-3 предложения Ключевое отличие и best practice в production.
> - [ ] Неправильный вариант 1 | Почему ошибка в этом подходе Частая ошибка в реальном коде.
> - [ ] Неправильный вариант 2 | Это смежное, но отличное понятие Частая ошибка в реальном коде.
> - [ ] Неправильный вариант 3 | Противоположное направление## Q18. Какие матчеры аргументов предоставляет `ArgumentMatchers`? Частая ошибка в реальном коде.

Наиболее используемые матчеры из `org.mockito.ArgumentMatchers`:

| Матчер | Описание |
|--------|----------|
| `any()` | Любое значение включая `null` |
| `any(Class.class)` | Любое ненулевое значение данного типа |
| `anyString()`, `anyInt()`, `anyLong()` | Специализированные матчеры |
| `eq(value)` | Точное совпадение через `equals()` |
| `isNull()`, `isNotNull()` | Проверка на `null` |
| `contains(str)`, `startsWith(str)` | Строковые матчеры |
| `argThat(predicate)` | Пользовательское условие |
| `same(obj)` | Проверка по ссылке (`==`) |

```java
when(service.find(anyLong(), eq("ACTIVE"))).thenReturn(list);
verify(repo).save(argThat(u -> u.getAge() > 18));
```

---


> [!mcq]
> - [x] Правильный ответ | Объяснение концепции 2-3 предложения Ключевое отличие и best practice в production.
> - [ ] Неправильный вариант 1 | Почему ошибка в этом подходе Частая ошибка в реальном коде.
> - [ ] Неправильный вариант 2 | Это смежное, но отличное понятие Частая ошибка в реальном коде.
> - [ ] Неправильный вариант 3 | Противоположное направление## Q19. Что такое `argThat()` и когда он нужен? Частая ошибка в реальном коде.

`argThat(ArgumentMatcher<T>)` позволяет задать произвольное условие для аргумента в виде предиката:

```java
verify(emailService).send(argThat(email ->
    email.getTo().contains("@example.com") &&
    email.getSubject().startsWith("Welcome")
));
```

С лямбдой и `assertj` для читаемости:
```java
verify(orderRepo).save(argThat(order -> {
    assertThat(order.getStatus()).isEqualTo(CONFIRMED);
    assertThat(order.getItems()).hasSize(2);
    return true;  // возврат true означает "матчер сработал"
}));
```

Разница от `ArgumentCaptor`: `argThat` удобнее в `verify()` для простых проверок; `ArgumentCaptor` даёт доступ к объекту для сложных assertions.

---


> [!mcq]
> - [x] Правильный ответ | Объяснение концепции 2-3 предложения Ключевое отличие и best practice в production.
> - [ ] Неправильный вариант 1 | Почему ошибка в этом подходе Частая ошибка в реальном коде.
> - [ ] Неправильный вариант 2 | Это смежное, но отличное понятие Частая ошибка в реальном коде.
> - [ ] Неправильный вариант 3 | Противоположное направление## Q20. (!) Как проверить порядок вызовов с помощью `InOrder`? Частая ошибка в реальном коде.

```java
@Test
void shouldCallMethodsInOrder() {
    // given
    InOrder inOrder = inOrder(transactionManager, userRepository, auditService);

    // when
    service.registerUser(userRequest);

    // then
    inOrder.verify(transactionManager).begin();
    inOrder.verify(userRepository).save(any(User.class));
    inOrder.verify(auditService).log(anyString());
    inOrder.verify(transactionManager).commit();
}
```

`InOrder` можно создать для нескольких mock-объектов. Верифицирует только относительный порядок — не требует, чтобы между ними не было других вызовов.

---


> [!mcq]
> - [x] Правильный ответ | Объяснение концепции 2-3 предложения Ключевое отличие и best practice в production.
> - [ ] Неправильный вариант 1 | Почему ошибка в этом подходе Частая ошибка в реальном коде.
> - [ ] Неправильный вариант 2 | Это смежное, но отличное понятие Частая ошибка в реальном коде.
> - [ ] Неправильный вариант 3 | Противоположное направление## Q21. Что такое BDDMockito и как использовать стиль `given/when/then`? Это антипаттерн или неправильный выбор в production.

`BDDMockito` — обёртка над Mockito, дающая BDD-нотацию, более читаемую в контексте Given/When/Then:

```java
import static org.mockito.BDDMockito.*;

@Test
void shouldReturnUserWhenFound() {
    // given
    User alice = new User(1L, "Alice");
    given(userRepository.findById(1L)).willReturn(Optional.of(alice));

    // when
    User result = userService.getUser(1L);

    // then
    assertThat(result.getName()).isEqualTo("Alice");
    then(userRepository).should().findById(1L);
    then(userRepository).shouldHaveNoMoreInteractions();
}
```

Маппинг:
- `given(mock.method()).willReturn(...)` эквивалентно `when(mock.method()).thenReturn(...)`
- `then(mock).should()` эквивалентно `verify(mock)`
- `willThrow()`, `willAnswer()`, `willDoNothing()` — аналоги `thenThrow`, `thenAnswer`, `doNothing`

---


> [!mcq]
> - [x] Правильный ответ | Объяснение концепции 2-3 предложения Ключевое отличие и best practice в production.
> - [ ] Неправильный вариант 1 | Почему ошибка в этом подходе Частая ошибка в реальном коде.
> - [ ] Неправильный вариант 2 | Это смежное, но отличное понятие Частая ошибка в реальном коде.
> - [ ] Неправильный вариант 3 | Противоположное направление## Q22. (!) Что такое `@MockBean` и `@SpyBean` в Spring Boot? Это антипаттерн или неправильный выбор в production.

`@MockBean` и `@SpyBean` — аннотации Spring Boot Test, которые регистрируют mock/spy в контексте Spring `ApplicationContext`:

```java
@SpringBootTest
class UserServiceIntegrationTest {

    @MockBean
    EmailService emailService;  // заменяет реальный бин в контексте

    @SpyBean
    UserRepository userRepository;  // оборачивает реальный бин в spy

    @Autowired
    UserService userService;

    @Test
    void shouldNotSendEmailWhenUserExists() {
        when(userRepository.existsByEmail("alice@test.com")).thenReturn(true);

        userService.register("alice@test.com");

        verify(emailService, never()).sendWelcome(any());
    }
}
```

Важно: каждое уникальное сочетание `@MockBean` / `@SpyBean` пересоздаёт `ApplicationContext`, что замедляет тесты. Лучше выносить в базовый тестовый класс.

---


> [!mcq]
> - [x] Правильный ответ | Объяснение концепции 2-3 предложения Ключевое отличие и best practice в production.
> - [ ] Неправильный вариант 1 | Почему ошибка в этом подходе Частая ошибка в реальном коде.
> - [ ] Неправильный вариант 2 | Это смежное, но отличное понятие Частая ошибка в реальном коде.
> - [ ] Неправильный вариант 3 | Противоположное направление## Q23. Как работает Mockito с `@ExtendWith(MockitoExtension.class)` в JUnit 5? Это антипаттерн или неправильный выбор в production.

`MockitoExtension` — JUnit 5 Extension, реализующий несколько lifecycle-интерфейсов:

1. `BeforeEachCallback` — обрабатывает аннотации `@Mock`, `@Spy`, `@Captor`, `@InjectMocks`
2. `AfterEachCallback` — сбрасывает состояние и проверяет нарушения strict stubbing
3. `ParameterResolver` — позволяет передавать моки как параметры метода теста

```java
@ExtendWith(MockitoExtension.class)
class PaymentServiceTest {

    // Мок через параметр метода (альтернатива полю)
    @Test
    void test(@Mock PaymentGateway gateway) {
        when(gateway.charge(any())).thenReturn(true);
        // ...
    }
}
```

По умолчанию включён `STRICT_STUBS` — если настроили заглушку, но не использовали, тест упадёт с `UnnecessaryStubbingException`.

---


> [!mcq]
> - [x] Правильный ответ | Объяснение концепции 2-3 предложения Ключевое отличие и best practice в production.
> - [ ] Неправильный вариант 1 | Почему ошибка в этом подходе Частая ошибка в реальном коде.
> - [ ] Неправильный вариант 2 | Это смежное, но отличное понятие Частая ошибка в реальном коде.
> - [ ] Неправильный вариант 3 | Противоположное направление## Q24. Что делает `Mockito.reset()`? Это антипаттерн или неправильный выбор в production.

`reset(mock)` сбрасывает все настройки mock-объекта: убирает заглушки и записи вызовов.

```java
@AfterEach
void tearDown() {
    reset(userRepository, emailService);
}
```

Однако использование `reset()` — **антипаттерн**. Если вы переиспользуете mock между тестами, это сигнал о плохой структуре тестов. Каждый тест должен быть независимым. Лучше создавать новый mock в `@BeforeEach`.

Легитимный случай: при использовании Spring Context с `@MockBean`, когда пересоздание контекста слишком дорого — `reset` делается в `@BeforeEach`.

---


> [!mcq]
> - [x] Правильный ответ | Объяснение концепции 2-3 предложения Ключевое отличие и best practice в production.
> - [ ] Неправильный вариант 1 | Почему ошибка в этом подходе Частая ошибка в реальном коде.
> - [ ] Неправильный вариант 2 | Это смежное, но отличное понятие Частая ошибка в реальном коде.
> - [ ] Неправильный вариант 3 | Противоположное направление## Q25. (!) Как мокировать статические методы с помощью `mockStatic()`? Это антипаттерн или неправильный выбор в production.

С Mockito 3.4+ (требует `mockito-inline` или `mockito-core` 5+):

```java
@Test
void shouldMockStaticMethod() {
    try (MockedStatic<UUID> mockedUUID = mockStatic(UUID.class)) {
        UUID fixedId = UUID.fromString("123e4567-e89b-12d3-a456-426614174000");
        mockedUUID.when(UUID::randomUUID).thenReturn(fixedId);

        String id = orderService.generateId();

        assertThat(id).isEqualTo("123e4567-e89b-12d3-a456-426614174000");
    }
    // За пределами try-with-resources — UUID.randomUUID() работает нормально
}
```

`MockedStatic` должен использоваться в `try-with-resources` — иначе mock останется активным для всего потока.

Верификация:
```java
mockedUUID.verify(UUID::randomUUID, times(1));
```

---


> [!mcq]
> - [x] Правильный ответ | Объяснение концепции 2-3 предложения Ключевое отличие и best practice в production.
> - [ ] Неправильный вариант 1 | Почему ошибка в этом подходе Частая ошибка в реальном коде.
> - [ ] Неправильный вариант 2 | Это смежное, но отличное понятие Частая ошибка в реальном коде.
> - [ ] Неправильный вариант 3 | Противоположное направление## Q26. Как мокировать `final`-классы и методы? Частая ошибка в реальном коде.

По умолчанию Mockito не может мокировать `final`-классы. Для этого нужен **mock-maker-inline**:

```
# src/test/resources/mockito-extensions/org.mockito.plugins.MockMaker
mock-maker-inline
```

Или в Mockito 5+ inline mock maker включён по умолчанию.

После настройки:
```java
// String — final класс
String mockString = mock(String.class);
when(mockString.length()).thenReturn(42);

// final метод
FinalClass mock = mock(FinalClass.class);
when(mock.finalMethod()).thenReturn("mocked");
```

Ограничения `mock-maker-inline`:
- Медленнее, чем стандартный mock maker
- Может конфликтовать с другими инструментами байт-кода (Jacoco)
- Нельзя мокировать примитивы, `enum`, `private`-методы

---


> [!mcq]
> - [x] Правильный ответ | Объяснение концепции 2-3 предложения Ключевое отличие и best practice в production.
> - [ ] Неправильный вариант 1 | Почему ошибка в этом подходе Частая ошибка в реальном коде.
> - [ ] Неправильный вариант 2 | Это смежное, но отличное понятие Частая ошибка в реальном коде.
> - [ ] Неправильный вариант 3 | Противоположное направление## Q27. Что такое `RETURNS_DEEP_STUBS` и когда его применять? Частая ошибка в реальном коде.

`RETURNS_DEEP_STUBS` — ответный режим (`Answer`), позволяющий цеплять вызовы без явной настройки каждого уровня:

```java
// Без RETURNS_DEEP_STUBS пришлось бы настраивать каждый уровень
Order order = mock(Order.class, RETURNS_DEEP_STUBS);

when(order.getCustomer().getAddress().getCity()).thenReturn("Moscow");

// Теперь работает без NPE
String city = order.getCustomer().getAddress().getCity();
assertThat(city).isEqualTo("Moscow");
```

Когда применять:
- При работе с API, возвращающим fluent builder
- При настройке конфигурационных объектов в тестах

Антипаттерн: если нужен `RETURNS_DEEP_STUBS` для доменного кода — это нарушение Закона Деметры. Нужно пересмотреть дизайн.

---


> [!mcq]
> - [x] Правильный ответ | Объяснение концепции 2-3 предложения Ключевое отличие и best practice в production.
> - [ ] Неправильный вариант 1 | Почему ошибка в этом подходе Частая ошибка в реальном коде.
> - [ ] Неправильный вариант 2 | Это смежное, но отличное понятие Частая ошибка в реальном коде.
> - [ ] Неправильный вариант 3 | Противоположное направление## Q28. (!) Что такое strict stubbing и зачем он нужен? Частая ошибка в реальном коде.

**Strict stubbing** (введён в Mockito 2, включён по умолчанию в `MockitoExtension`) обеспечивает:

1. **`UnnecessaryStubbingException`** — тест упадёт, если настроена заглушка, которая не была использована
2. **`StubbingArgumentMismatchException`** — предупреждение, если stub настроен с одними аргументами, а вызов произошёл с другими

```java
@ExtendWith(MockitoExtension.class)
class StrictTest {

    @Mock UserRepository repo;

    @Test
    void test() {
        // Если findById не вызывается в тесте — UnnecessaryStubbingException
        when(repo.findById(1L)).thenReturn(Optional.of(new User()));

        // тест без вызова findById...
    }
}
```

Зачем нужен: помогает поддерживать чистые тесты, выявляет мёртвый код в заглушках, упрощает рефакторинг тестов.

---


> [!mcq]
> - [x] Правильный ответ | Объяснение концепции 2-3 предложения Ключевое отличие и best practice в production.
> - [ ] Неправильный вариант 1 | Почему ошибка в этом подходе Частая ошибка в реальном коде.
> - [ ] Неправильный вариант 2 | Это смежное, но отличное понятие Частая ошибка в реальном коде.
> - [ ] Неправильный вариант 3 | Противоположное направление## Q29. Как сделать отдельный stub ненастойчивым с помощью `lenient()`? Частая ошибка в реальном коде.

Когда нужно настроить заглушку, которая может не использоваться в некоторых сценариях:

```java
@ExtendWith(MockitoExtension.class)
class ServiceTest {

    @Mock
    FeatureToggle featureToggle;

    @BeforeEach
    void setUp() {
        // Эта заглушка может не использоваться в каждом тесте
        lenient().when(featureToggle.isEnabled("NEW_UI")).thenReturn(true);
    }

    @Test
    void testWithoutUsingFeatureToggle() {
        // featureToggle не вызывается — но тест не упадёт
    }
}
```

`Mockito.lenient()` — точечное отключение strict stubbing для конкретной заглушки, не отключая его глобально. Предпочтительнее, чем использование `@MockitoSettings(strictness = Strictness.LENIENT)`.

---


> [!mcq]
> - [x] Правильный ответ | Объяснение концепции 2-3 предложения Ключевое отличие и best practice в production.
> - [ ] Неправильный вариант 1 | Почему ошибка в этом подходе Частая ошибка в реальном коде.
> - [ ] Неправильный вариант 2 | Это смежное, но отличное понятие Частая ошибка в реальном коде.
> - [ ] Неправильный вариант 3 | Противоположное направление## Q30. Можно ли вернуть разные значения при последовательных вызовах? Частая ошибка в реальном коде.

Да, есть несколько способов:

```java
// Способ 1: цепочка thenReturn
when(counter.next())
    .thenReturn(1)
    .thenReturn(2)
    .thenReturn(3);  // 3 и далее

// Способ 2: varargs (краткая форма)
when(counter.next()).thenReturn(1, 2, 3);

// Способ 3: чередование return и throw
when(service.call())
    .thenReturn("ok")
    .thenThrow(new RuntimeException("2nd call fails"))
    .thenReturn("recovered");

// Способ 4: через thenAnswer с счётчиком
AtomicInteger count = new AtomicInteger();
when(service.fetch()).thenAnswer(inv -> count.getAndIncrement());
```

Последний `.thenReturn()` в цепочке продолжает использоваться для всех последующих вызовов.

---


> [!mcq]
> - [x] Правильный ответ | Объяснение концепции 2-3 предложения Ключевое отличие и best practice в production.
> - [ ] Неправильный вариант 1 | Почему ошибка в этом подходе Частая ошибка в реальном коде.
> - [ ] Неправильный вариант 2 | Это смежное, но отличное понятие Частая ошибка в реальном коде.
> - [ ] Неправильный вариант 3 | Противоположное направление## Q31. Чем отличается `Mockito.spy()` от `@Spy`? Это антипаттерн или неправильный выбор в production.

Оба создают spy-обёртку над реальным объектом, но отличаются способом инициализации:

```java
// Mockito.spy() — программно, явно передаём экземпляр
List<String> realList = new ArrayList<>();
List<String> spyList = Mockito.spy(realList);

// @Spy — декларативно, Mockito создаёт экземпляр сам
@Spy
List<String> spyList = new ArrayList<>();  // инициализация обязательна для дженериков

// @Spy без инициализации — только если класс имеет конструктор без аргументов
@Spy
MyService myService;
```

Разница только в синтаксисе — поведение идентично. Оба выполняют реальные методы, если не настроена заглушка через `doReturn/doThrow`.

---


> [!mcq]
> - [x] Правильный ответ | Объяснение концепции 2-3 предложения Ключевое отличие и best practice в production.
> - [ ] Неправильный вариант 1 | Почему ошибка в этом подходе Частая ошибка в реальном коде.
> - [ ] Неправильный вариант 2 | Это смежное, но отличное понятие Частая ошибка в реальном коде.
> - [ ] Неправильный вариант 3 | Противоположное направление## Q32. (!) Каковы типичные ошибки при использовании Mockito? Это антипаттерн или неправильный выбор в production.

**1. Смешение матчеров и литералов:**
```java
// Ошибка — нельзя смешивать
verify(service).find(anyLong(), "ACTIVE");  // InvalidUseOfMatchersException

// Правильно
verify(service).find(anyLong(), eq("ACTIVE"));
```

**2. Настройка заглушки после вызова реального кода:**
```java
service.doSomething();  // уже выполнено
when(repo.find(1L)).thenReturn(entity);  // слишком поздно!
```

**3. Использование `when()` на spy для void-методов:**
```java
// Реальный метод вызовется при настройке!
// Правильно:
doNothing().when(spyService).voidMethod();
```

**4. Верификация без предшествующего вызова:**
```java
verify(repo).save(user);  // упадёт, если save не был вызван в тестируемом коде
// Порядок: сначала настройка, потом вызов тестируемого кода, потом verify
```

**5. Мокирование value objects и final классов без `mock-maker-inline`.**

---


> [!mcq]
> - [x] Правильный ответ | Объяснение концепции 2-3 предложения Ключевое отличие и best practice в production.
> - [ ] Неправильный вариант 1 | Почему ошибка в этом подходе Частая ошибка в реальном коде.
> - [ ] Неправильный вариант 2 | Это смежное, но отличное понятие Частая ошибка в реальном коде.
> - [ ] Неправильный вариант 3 | Противоположное направление## Q33. Что такое `UnnecessaryStubbingException` и как её избежать? Частая ошибка в реальном коде.

`UnnecessaryStubbingException` — исключение строгого режима (strict stubbing), которое возникает, когда заглушка настроена, но не использована в тесте.

Причины:
- Заглушка настроена в `@BeforeEach`, но не нужна в конкретном тесте
- Тест был изменён, но заглушку забыли убрать
- Заглушка настроена для несуществующей ветки кода

Решения:

```java
// 1. Убрать неиспользуемую заглушку (предпочтительно)

// 2. Использовать lenient() для заглушек в @BeforeEach
lenient().when(repo.findAll()).thenReturn(Collections.emptyList());

// 3. Аннотация на уровне класса (крайний случай)
@MockitoSettings(strictness = Strictness.LENIENT)
class MyTest { ... }
```

---


> [!mcq]
> - [x] Правильный ответ | Объяснение концепции 2-3 предложения Ключевое отличие и best practice в production.
> - [ ] Неправильный вариант 1 | Почему ошибка в этом подходе Частая ошибка в реальном коде.
> - [ ] Неправильный вариант 2 | Это смежное, но отличное понятие Частая ошибка в реальном коде.
> - [ ] Неправильный вариант 3 | Противоположное направление## Q34. Когда не стоит использовать Mockito? Это антипаттерн или неправильный выбор в production.

Mockito не всегда правильный инструмент:

**1. Мокирование value objects** (`String`, `LocalDate`, DTO) — они не имеют поведения, создавайте реальные экземпляры.

**2. Мокирование всего подряд** — если замокированы 5+ зависимостей, возможно класс нарушает SRP.

**3. Вместо реальной БД** — для интеграционных тестов используйте Testcontainers, H2, встроенный Redis.

**4. Мокирование статики и конструкторов массово** — сигнал о плохом дизайне кода (нарушение DIP).

**5. Тестирование взаимодействия между реальными объектами** — здесь нужны интеграционные тесты.

Альтернативы:
- **Фейки** (fake) — для репозиториев, кэшей: `HashMap`-based реализации
- **Testcontainers** — для реальных сервисов (PostgreSQL, Redis, Kafka)
- **WireMock** — для HTTP-сервисов

---


> [!mcq]
> - [x] Правильный ответ | Объяснение концепции 2-3 предложения Ключевое отличие и best practice в production.
> - [ ] Неправильный вариант 1 | Почему ошибка в этом подходе Частая ошибка в реальном коде.
> - [ ] Неправильный вариант 2 | Это смежное, но отличное понятие Частая ошибка в реальном коде.
> - [ ] Неправильный вариант 3 | Противоположное направление## Q35. Как мокировать конструкторы с помощью `mockConstruction()`? Это антипаттерн или неправильный выбор в production.

С Mockito 3.5+ можно перехватить создание объектов через `new`:

```java
@Test
void shouldMockObjectCreation() {
    try (MockedConstruction<HttpClient> mocked =
             mockConstruction(HttpClient.class, (mock, context) -> {
                 when(mock.send(any(), any())).thenReturn(mockResponse);
             })) {

        // Каждый new HttpClient() в этом блоке вернёт mock
        ApiClient apiClient = new ApiClient();  // внутри создаёт new HttpClient()
        String result = apiClient.fetchData("/api/users");

        assertThat(result).isNotNull();
        assertThat(mocked.constructed()).hasSize(1);
    }
}
```

`mockConstruction()` работает только внутри `try-with-resources`. После выхода из блока конструктор работает нормально. Использование `mockConstruction()` — признак того, что класс создаёт зависимости напрямую вместо внедрения через DI.

---


> [!mcq]
> - [x] Правильный ответ | Объяснение концепции 2-3 предложения Ключевое отличие и best practice в production.
> - [ ] Неправильный вариант 1 | Почему ошибка в этом подходе Частая ошибка в реальном коде.
> - [ ] Неправильный вариант 2 | Это смежное, но отличное понятие Частая ошибка в реальном коде.
> - [ ] Неправильный вариант 3 | Противоположное направление## Q36. (!) В чём разница между `mockito-core` и `mockito-inline`? Это антипаттерн или неправильный выбор в production.

Исторически Mockito имел два артефакта с разными `MockMaker`:

| Артефакт | Возможности | Как работает |
|----------|-------------|--------------|
| `mockito-core` (до 5.0) | Только обычные классы и интерфейсы | Subclass mock maker (CGLib/ByteBuddy) |
| `mockito-inline` | + `final`-классы, `static`-методы, конструкторы, `enum` | Inline mock maker через Java Agent и bytecode instrumentation |

**Ключевое изменение в Mockito 5.0:** inline mock maker стал дефолтным в `mockito-core`. Начиная с 5.3.0 артефакт `mockito-inline` больше не публикуется — достаточно `mockito-core`:

```xml
<!-- Mockito 5+ — inline mock maker уже включён -->
<dependency>
    <groupId>org.mockito</groupId>
    <artifactId>mockito-core</artifactId>
    <version>5.14.0</version>
</dependency>
```

Для Mockito 3–4, если нужны `mockStatic`, `mockConstruction` или `final`-классы:
```xml
<dependency>
    <groupId>org.mockito</groupId>
    <artifactId>mockito-inline</artifactId>
    <version>4.11.0</version>
</dependency>
```

Компромиссы inline mock maker:
- Медленнее subclass-версии (инструментация классов через Java Agent)
- Может конфликтовать с Jacoco, JRebel и другими байткод-инструментами
- Нельзя мокировать примитивы и `private`-методы
- Требует JVM с поддержкой Instrumentation API

---


> [!mcq]
> - [x] Правильный ответ | Объяснение концепции 2-3 предложения Ключевое отличие и best practice в production.
> - [ ] Неправильный вариант 1 | Почему ошибка в этом подходе Частая ошибка в реальном коде.
> - [ ] Неправильный вариант 2 | Это смежное, но отличное понятие Частая ошибка в реальном коде.
> - [ ] Неправильный вариант 3 | Противоположное направление## Q37. Что такое `MockSettings` и какие опции он предоставляет? Это антипаттерн или неправильный выбор в production.

`MockSettings` — интерфейс для тонкой настройки мока при создании через `Mockito.mock(Class, MockSettings)` или `withSettings()`.

```java
import static org.mockito.Mockito.*;

List<String> customMock = mock(List.class, withSettings()
    .name("cartItems")                       // имя в сообщениях об ошибках
    .defaultAnswer(RETURNS_SMART_NULLS)      // ответ по умолчанию
    .serializable()                          // mock реализует Serializable
    .extraInterfaces(Comparable.class)       // дополнительные интерфейсы
    .verboseLogging()                        // лог всех вызовов в stdout
    .stubOnly()                              // mock не хранит историю вызовов
);
```

Основные опции:

| Метод | Назначение |
|-------|-----------|
| `name(String)` | Имя мока в ошибках верификации |
| `defaultAnswer(Answer)` | Стратегия ответов на невзятые в заглушки вызовы |
| `serializable()` | Для тестов, требующих `Serializable`-зависимости (например, Spark, `HttpSession`) |
| `extraInterfaces(Class...)` | Мок реализует несколько интерфейсов |
| `verboseLogging()` | Для отладки: выводит в консоль все вызовы мока |
| `stubOnly()` | Оптимизация — mock не запоминает вызовы, `verify()` не работает |
| `spiedInstance(obj)` | Явное указание объекта, вокруг которого создаётся spy |
| `strictness(Strictness)` | Локальное управление strict stubbing |

`withSettings()` используется редко, но полезен для продвинутых сценариев отладки и legacy-кода.

---


> [!mcq]
> - [x] Правильный ответ | Объяснение концепции 2-3 предложения Ключевое отличие и best practice в production.
> - [ ] Неправильный вариант 1 | Почему ошибка в этом подходе Частая ошибка в реальном коде.
> - [ ] Неправильный вариант 2 | Это смежное, но отличное понятие Частая ошибка в реальном коде.
> - [ ] Неправильный вариант 3 | Противоположное направление## Q38. Какие встроенные `Answer` доступны в Mockito? Это антипаттерн или неправильный выбор в production.

`Answers` — enum в `org.mockito.Answers` с заранее сконфигурированными стратегиями ответов:

| Значение | Поведение |
|----------|-----------|
| `RETURNS_DEFAULTS` | Дефолт: `null`, `0`, `false`, пустые коллекции и `Optional.empty()` |
| `RETURNS_SMART_NULLS` | Вместо `null` возвращает `SmartNull` — бросает `SmartNullPointerException` с точкой настройки |
| `RETURNS_MOCKS` | Возвращает пустые значения; если вернуть нельзя — возвращает mock того же типа |
| `RETURNS_DEEP_STUBS` | Рекурсивно возвращает моки — для цепочек `a.getB().getC()` |
| `CALLS_REAL_METHODS` | Вызывает реальные методы (аналог spy для интерфейсов с `default`-методами) |
| `RETURNS_SELF` | Для fluent builder-ов: mock возвращает себя для цепочек |

```java
// SMART_NULLS — лучшее сообщение об ошибке при обращении к не-застабленному методу
UserService mock = mock(UserService.class, RETURNS_SMART_NULLS);
User u = mock.findById(1L);  // вернёт SmartNull
u.getName();  // SmartNullPointerException: "You have a NullPointerException at UserServiceTest.java:42"

// RETURNS_SELF — для билдеров
StringBuilder sb = mock(StringBuilder.class, RETURNS_SELF);
sb.append("a").append("b").append("c");  // каждый append() вернёт sb
```

На практике чаще всего используется `RETURNS_DEEP_STUBS` и `RETURNS_SMART_NULLS`. Остальные — для специальных случаев.

---


> [!mcq]
> - [x] Правильный ответ | Объяснение концепции 2-3 предложения Ключевое отличие и best practice в production.
> - [ ] Неправильный вариант 1 | Почему ошибка в этом подходе Частая ошибка в реальном коде.
> - [ ] Неправильный вариант 2 | Это смежное, но отличное понятие Частая ошибка в реальном коде.
> - [ ] Неправильный вариант 3 | Противоположное направление## Q39. (!) Когда выбирать `ArgumentCaptor`, а когда `argThat()`? Частая ошибка в реальном коде.

Оба проверяют аргумент, переданный в mock, но имеют разные задачи:

| Аспект | `ArgumentCaptor` | `argThat()` |
|--------|------------------|-------------|
| Где применять | Преимущественно в `verify()` | И в стаббинге (`when()`) и в `verify()` |
| Сложные assertion'ы | Да — полный доступ к захваченному объекту | Ограниченно — через предикат |
| Сообщение об ошибке | Детальное: AssertJ выдаст diff | Неочевидное: "Argument(s) are different" |
| Переиспользование | Локально в тесте | Легко выделить именованный matcher |
| Множественные вызовы | `getAllValues()` для списка | Требует счётчика вручную |

**Правило от авторов Mockito**: для стаббинга — `argThat()`/`eq()`/`any()`; для верификации с глубокими проверками — `ArgumentCaptor`.

```java
// ArgumentCaptor — детальные assertion'ы после verify
@Captor ArgumentCaptor<Order> orderCaptor;

verify(repo).save(orderCaptor.capture());
Order saved = orderCaptor.getValue();
assertThat(saved)
    .extracting(Order::getStatus, Order::getTotal)
    .containsExactly(PENDING, BigDecimal.TEN);

// argThat — когда проверка простая и самодокументируемая
verify(emailService).send(argThat(email ->
    email.getTo().endsWith("@example.com")));

// argThat в стаббинге — ArgumentCaptor тут не работает
when(repo.findByFilter(argThat(f -> f.isActive())))
    .thenReturn(List.of(user));
```

Антипаттерн: использовать `ArgumentCaptor` внутри `when()` — захватчик не вызывается до фактического вызова мока.

---


> [!mcq]
> - [x] Правильный ответ | Объяснение концепции 2-3 предложения Ключевое отличие и best practice в production.
> - [ ] Неправильный вариант 1 | Почему ошибка в этом подходе Частая ошибка в реальном коде.
> - [ ] Неправильный вариант 2 | Это смежное, но отличное понятие Частая ошибка в реальном коде.
> - [ ] Неправильный вариант 3 | Противоположное направление## Q40. Как тестировать код с callback-функциями через `doAnswer()`? Частая ошибка в реальном коде.

Типичная задача: тестируемый метод регистрирует callback (`Consumer`, `Callback`, `Runnable`), который должен быть вызван. С `doAnswer()` можно перехватить и вручную выполнить callback:

```java
@Test
void shouldInvokeCallbackOnSuccess() {
    // given — мокаем асинхронный сервис
    doAnswer(invocation -> {
        Consumer<String> callback = invocation.getArgument(1);
        callback.accept("result-42");  // синхронно зовём callback
        return null;
    }).when(asyncService).fetchAsync(eq("query"), any(Consumer.class));

    // when
    Result result = handler.handle("query");

    // then
    assertThat(result.getValue()).isEqualTo("result-42");
}
```

Альтернатива через `ArgumentCaptor` — контролируем момент вызова:
```java
ArgumentCaptor<Consumer<String>> cbCaptor = ArgumentCaptor.forClass(Consumer.class);
handler.handle("query");

verify(asyncService).fetchAsync(eq("query"), cbCaptor.capture());
cbCaptor.getValue().accept("result-42");  // симулируем ответ здесь

assertThat(handler.getState()).isEqualTo(State.READY);
```

Ключевой принцип: **не используйте `Thread.sleep()`** для тестирования асинхронного кода. Либо делайте callback синхронным через `doAnswer`, либо `Awaitility.await()` для реальной асинхронности.

---


> [!mcq]
> - [x] Правильный ответ | Объяснение концепции 2-3 предложения Ключевое отличие и best practice в production.
> - [ ] Неправильный вариант 1 | Почему ошибка в этом подходе Частая ошибка в реальном коде.
> - [ ] Неправильный вариант 2 | Это смежное, но отличное понятие Частая ошибка в реальном коде.
> - [ ] Неправильный вариант 3 | Противоположное направление## Q41. (!) В чём ограничение области видимости `MockedStatic` по потокам? Это антипаттерн или неправильный выбор в production.

`MockedStatic` имеет **thread-local scope**: mock активен только в потоке, где был создан. Это критично для:

1. **Асинхронный код внутри теста** — если код под тестом создаёт `new Thread(...)` или использует `CompletableFuture.supplyAsync(...)`, в этих потоках статический метод вернётся к реальной реализации:

```java
try (MockedStatic<Clock> mocked = mockStatic(Clock.class)) {
    mocked.when(Clock::systemUTC).thenReturn(fixedClock);

    // В этом потоке — mock работает
    assertThat(Clock.systemUTC()).isEqualTo(fixedClock);

    // В другом потоке — mock НЕ работает!
    CompletableFuture.supplyAsync(() -> Clock.systemUTC()).join();
    // вернёт реальные системные часы, а не fixedClock
}
```

2. **Параллельное выполнение тестов** (`@Execution(CONCURRENT)` в JUnit 5) — если два теста пытаются замокировать один и тот же класс в разных потоках, получим `MockitoException: For XXX static mocking is already registered`. Решение: последовательный запуск тестов с `MockedStatic` или уникальные моки на класс.

3. **Thread pool в коде под тестом** — пулы потоков живут вне теста; mock статики на них не распространяется.

Это дизайн-ограничение, а не баг: thread-local scope гарантирует, что mock не утекает между тестами. Для межпоточного мокирования нужно либо перепроектировать код (инжектировать `Clock`/`Supplier` вместо статики), либо использовать иной подход (`PowerMock`, хотя он не рекомендуется).

---


> [!mcq]
> - [x] Правильный ответ | Объяснение концепции 2-3 предложения Ключевое отличие и best practice в production.
> - [ ] Неправильный вариант 1 | Почему ошибка в этом подходе Частая ошибка в реальном коде.
> - [ ] Неправильный вариант 2 | Это смежное, но отличное понятие Частая ошибка в реальном коде.
> - [ ] Неправильный вариант 3 | Противоположное направление## Q42. Что делает `thenCallRealMethod()` и чем отличается от `@Spy`? Частая ошибка в реальном коде.

`thenCallRealMethod()` заставляет mock вызвать **реальную реализацию** метода вместо заглушки. Похоже на spy, но есть важные отличия:

```java
@Mock
UserService userService;  // чистый mock

@Test
void test() {
    // Один метод — вызывает реальный, остальные остаются моками
    when(userService.formatName(anyString())).thenCallRealMethod();
    when(userService.findById(1L)).thenReturn(new User("Alice"));

    String formatted = userService.formatName("bob");
    assertThat(formatted).isEqualTo("Bob");  // реальный formatName вызван
}
```

Отличия от `@Spy`:

| Аспект | `@Mock` + `thenCallRealMethod()` | `@Spy` |
|--------|----------------------------------|--------|
| Экземпляр класса | НЕ создаётся (bare-bones shell) | Создаётся полноценный объект |
| Состояние полей | Поля = `null` / дефолты | Поля инициализированы конструктором |
| NPE риск | Высокий: если метод обращается к `this.field`, получим NPE | Низкий: поля реальные |
| Использование зависимостей | Не работает (поля не инициализированы) | Работает |
| Default для вызовов | Возврат дефолта (mock) | Вызов реального метода |

Когда использовать `thenCallRealMethod()`:
- Тест **интерфейса с `default`-методом** — реальный метод не зависит от состояния
- **Абстрактный класс** с реализованными методами без зависимости от полей
- Частичное мокирование **отдельных** методов в stateless-классе

Для полноценного partial mock почти всегда выбирайте `@Spy` — безопаснее.

---


> [!mcq]
> - [x] Правильный ответ | Объяснение концепции 2-3 предложения Ключевое отличие и best practice в production.
> - [ ] Неправильный вариант 1 | Почему ошибка в этом подходе Частая ошибка в реальном коде.
> - [ ] Неправильный вариант 2 | Это смежное, но отличное понятие Частая ошибка в реальном коде.
> - [ ] Неправильный вариант 3 | Противоположное направление## Q43. Как инжектировать `@Mock` и `@Captor` в параметры метода JUnit 5? Это антипаттерн или неправильный выбор в production.

`MockitoExtension` реализует `ParameterResolver` — моки можно объявлять прямо в параметрах тестового метода, без полей класса:

```java
@ExtendWith(MockitoExtension.class)
class OrderServiceTest {

    @Test
    void shouldProcessOrder(
            @Mock PaymentGateway gateway,
            @Mock OrderRepository repo,
            @Captor ArgumentCaptor<Order> orderCaptor
    ) {
        // given
        when(gateway.charge(any())).thenReturn(PaymentResult.SUCCESS);
        OrderService service = new OrderService(gateway, repo);

        // when
        service.place(orderRequest);

        // then
        verify(repo).save(orderCaptor.capture());
        assertThat(orderCaptor.getValue().getStatus()).isEqualTo(PAID);
    }
}
```

Преимущества:
- **Изоляция** — каждый тест видит только нужные ему моки
- Нет разделяемого состояния между тестами
- Меньше полей в классе
- Явное описание зависимостей теста

Когда оставлять поля:
- Моки нужны в нескольких тестах класса — выносим в `@Mock`-поля + `@BeforeEach` с общей настройкой
- Используется `@InjectMocks` — он работает только с полями

Также поддерживается конструктор тестового класса:
```java
class Test {
    private final UserService service;
    Test(@Mock UserRepository repo) {
        this.service = new UserService(repo);
    }
}
```

---


> [!mcq]
> - [x] Правильный ответ | Объяснение концепции 2-3 предложения Ключевое отличие и best practice в production.
> - [ ] Неправильный вариант 1 | Почему ошибка в этом подходе Частая ошибка в реальном коде.
> - [ ] Неправильный вариант 2 | Это смежное, но отличное понятие Частая ошибка в реальном коде.
> - [ ] Неправильный вариант 3 | Противоположное направление## Q44. (!) Какие особенности у Mockito в Kotlin и зачем `mockito-kotlin`? Это антипаттерн или неправильный выбор в production.

Использование классического Mockito в Kotlin наталкивается на несколько проблем:

**1. `when` — reserved keyword в Kotlin:**
```kotlin
// Обязательно экранировать backtick'ами
`when`(repo.findById(1L)).thenReturn(user)
```

**2. Классы и методы `final` по умолчанию** — до Mockito 5 требовал `mockito-inline`. С Kotlin-плагином `all-open` можно делать классы `open` только для тестов.

**3. `any()` возвращает `null`** — это ломает non-null типы Kotlin:
```kotlin
// NPE — any() вернёт null, но параметр типа String (non-null)
verify(service).process(any())  
```

**4. Отсутствие reified generics** — `mock(MyClass::class.java)` вместо `mock<MyClass>()`.

**Библиотека `mockito-kotlin`** решает эти проблемы:

```kotlin
import org.mockito.kotlin.*

@Test
fun shouldProcessOrder() {
    // Reified generics — тип выводится
    val repo: OrderRepository = mock()
    
    // whenever вместо `when`
    whenever(repo.findById(1L)).thenReturn(Order(1L))
    
    // any<T>() поддерживает non-null типы
    verify(repo).save(any<Order>())
    
    // DSL-стиль
    val service: Service = mock {
        on { isEnabled() } doReturn true
        on { fetch(any()) } doReturn "result"
    }
    
    // argumentCaptor через delegate
    argumentCaptor<Order>().apply {
        verify(repo).save(capture())
        assertEquals(PAID, firstValue.status)
    }
}
```

```kotlin
// build.gradle.kts
testImplementation("org.mockito.kotlin:mockito-kotlin:5.4.0")
```

Альтернатива — **MockK**, написанная специально под Kotlin: coroutine-friendly, поддержка `suspend`-функций, relaxed mocks. Для чистых Kotlin-проектов MockK часто удобнее.

---


> [!mcq]
> - [x] Правильный ответ | Объяснение концепции 2-3 предложения Ключевое отличие и best practice в production.
> - [ ] Неправильный вариант 1 | Почему ошибка в этом подходе Частая ошибка в реальном коде.
> - [ ] Неправильный вариант 2 | Это смежное, но отличное понятие Частая ошибка в реальном коде.
> - [ ] Неправильный вариант 3 | Противоположное направление## Q45. Что такое `@MockitoSettings` и как управлять strictness на уровне класса? Это антипаттерн или неправильный выбор в production.

`@MockitoSettings` — аннотация класса для настройки `MockitoExtension` без изменения поведения на каждом mock:

```java
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class LegacyServiceTest {

    @Mock UserRepository repo;

    @BeforeEach
    void setUp() {
        // не упадёт UnnecessaryStubbingException, даже если не используется
        when(repo.findAll()).thenReturn(List.of());
    }
}
```

Варианты `Strictness`:

| Значение | Поведение |
|----------|-----------|
| `STRICT_STUBS` | Дефолт в `MockitoExtension`. Ловит `UnnecessaryStubbingException`, `PotentialStubbingProblem`, argument mismatch |
| `LENIENT` | Отключает все проверки strict stubbing |
| `WARN` | Предупреждение в логах, но тест проходит (legacy-совместимость с Mockito 1.x) |

**Практика:** предпочтительнее точечный `lenient()` на конкретную заглушку, чем отключение strictness на весь класс:

```java
// Хорошо — только эта заглушка lenient, остальные проверяются strict
lenient().when(repo.findAll()).thenReturn(List.of());

// Плохо — выключаем проверки глобально, маскируем ошибки тестов
@MockitoSettings(strictness = Strictness.LENIENT)
class MyTest { ... }
```

`@MockitoSettings` оправдан для legacy-модулей при миграции с Mockito 1 — чтобы сначала стабилизировать тесты, потом постепенно включать strict на уровне отдельных классов.

---

## See also

- [Unit Testing](unit-testing-interview.md) — основы модульного тестирования, пирамида тестов, лучшие практики
- [Integration Testing](integration-testing-interview.md) — интеграционные тесты, Testcontainers, Spring Boot Test
- [Стратегии тестирования](test-strategies-interview.md) — TDD, BDD, пирамида тестов, выбор стратегии
- [Test Automation](test-automation-interview.md) — автоматизация тестирования, CI/CD интеграция, инструменты
- [Testcontainers](testcontainers-interview.md) — реальные зависимости в тестах: PostgreSQL, Kafka, Redis
- [Spring Boot](../frameworks/spring/spring-boot-interview.md) — тестирование Spring Boot приложений, @SpringBootTest, TestRestTemplate
- [Java Core](../programming-languages/java/java-core-interview.md) — основы Java, необходимые для понимания работы Mockito


> [!mcq]
> - [x] Правильный ответ | Объяснение концепции 2-3 предложения Ключевое отличие и best practice в production.
> - [ ] Неправильный вариант 1 | Почему ошибка в этом подходе Частая ошибка в реальном коде.
> - [ ] Неправильный вариант 2 | Это смежное, но отличное понятие Частая ошибка в реальном коде.
> - [ ] Неправильный вариант 3 | Противоположное направление- [Chaos Engineering](chaos-engineering-interview.md) Частая ошибка в реальном коде.
- [Contract Testing](contract-testing-interview.md)
- [Integration Testing](integration-testing-interview.md)
- [Load Testing](load-testing-interview.md)
- [Mutation Testing](mutation-testing-interview.md)
- [Property-based Testing](property-based-testing-interview.md)
- [Шпаргалка: Mockito](../../testing/unit-testing/junit/mockito.md) — теория
