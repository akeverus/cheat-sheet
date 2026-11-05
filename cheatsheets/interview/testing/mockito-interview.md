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
updated: "2026-11-05"
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
- [Q8. (!) Чем отличается `MockitoExtension` от `MockitoAnnotations.openMocks()`?](#q8--чем-отличается-mockitoextension-от-mockitoannotationsopenmocks)
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
- [Q21. Что такое BDDMockito и как использовать стиль `given/when/then`?](#q21-что-такое-bddmockito-и-как-использовать-стиль-givenwhenthen)
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

**Mockito** — Java-библиотека для создания тестовых двойников (test doubles). Её задача — изолировать тестируемый класс от зависимостей: вместо реального репозитория или HTTP-клиента вы подставляете управляемый объект, который возвращает заданные значения и записывает свои вызовы.

Почему без неё в unit-тестах не обойтись:
- **Изоляция** — тест проверяет одну единицу кода, а не весь граф зависимостей. Упал тест — сразу понятно, где баг.
- **Скорость** — нет реальных HTTP-вызовов, обращений к БД и файловой системе, поэтому тесты идут миллисекунды, а не секунды.
- **Контроль** — можно воспроизвести любой сценарий: исключение, пустой ответ, конкретное значение — даже те, что сложно вызвать на реальной зависимости.
- **Верификация** — можно проверить не только результат, но и сам факт взаимодействия: что метод вызвали, с нужными аргументами и нужное число раз.

```xml
<dependency>
    <groupId>org.mockito</groupId>
    <artifactId>mockito-core</artifactId>
    <version>5.x.x</version>
    <scope>test</scope>
</dependency>
```

---

## Q2. Чем отличаются mock, stub, spy и fake?

| Тип | Описание | Пример |
|-----|----------|--------|
| **Mock** | Объект с проверяемым поведением — записывает вызовы, используется для `verify()` | `Mockito.mock(Service.class)` |
| **Stub** | Возвращает заранее заданные данные; поведение важно, проверка вызовов — нет | `when(repo.find(1)).thenReturn(entity)` |
| **Spy** | Обёртка над реальным объектом; часть методов реальная, часть — заглушки | `Mockito.spy(new MyService())` |
| **Fake** | Упрощённая рабочая реализация (в памяти, без Mockito) | `HashMap` вместо реального кэша |

Ключевое деление — по тому, что вы проверяете. **Stub** отвечает на вопрос «что вернётся» (state verification), **mock** — на вопрос «как с ним взаимодействовали» (behavior verification). Spy и fake — это про степень реальности: spy оборачивает живой объект, fake даёт честную, но упрощённую реализацию.

На практике в Mockito граница между mock и stub размыта: **mock по умолчанию является и стабом**. Один и тот же объект можно и настраивать через `when()`, и проверять через `verify()` — отдельный тип «stub» в коде не заводят.

---

## Q3. (!) Как создать mock-объект в Mockito?

Mock создают фабричным методом либо через аннотацию, которую обрабатывает расширение/инициализатор. Записи `Mockito.mock(Type.class)` и `mock(Type.class)` вызывают один и тот же фабричный метод: короткое имя требует, например, `import static org.mockito.Mockito.mock`.

```java
import org.mockito.Mockito;
import static org.mockito.Mockito.mock;

// UserRepository — интерфейс зависимости проекта
UserRepository explicit = Mockito.mock(UserRepository.class);
UserRepository imported = mock(UserRepository.class);
UserRepository inferred = Mockito.mock(); // тип выводится из присваивания
```

Последний вариант доступен с Mockito 4.10.0; он не отменяет правила импорта Java. Для него нужен ожидаемый тип: `var repo = Mockito.mock()` не задаёт тип UserRepository. В этом материале API проверен на Mockito 5.11.0 / Java 21, но сама форма не требует именно Java 21.

```java
@Mock
UserRepository repository; // поле теста; нужен MockitoExtension или openMocks(this)
```

При стандартном `RETURNS_DEFAULTS` незастабленный mock не исполняет обычное тело метода. Результат зависит от возвращаемого типа: нули/false, поддерживаемые пустые контейнеры (`List`, `Optional` и др.) либо null для многих остальных ссылок. Это не гарантия null для любого reference type; custom/default Answer может задавать другое поведение.

---

## Q4. Что делает аннотация `@Mock`?

`@Mock` создаёт mock-объект и присваивает его полю, помеченному аннотацией. Это декларативный аналог `Mockito.mock(ClassName.class)` — короче и нагляднее, особенно когда моков несколько.

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

Сама по себе аннотация инертна: чтобы Mockito обработал поле и подставил мок, тест должен быть подключён к обработчику — через `@ExtendWith(MockitoExtension.class)` (JUnit 5) или вызов `MockitoAnnotations.openMocks(this)` в `@BeforeEach`. Без этого поле останется `null`.

---

## Q5. Что делает аннотация `@InjectMocks`?

`@InjectMocks` просит Mockito подставить объявленные `@Mock`/`@Spy` в объект под тестом. Сам по себе этот маркер не превращает SUT в mock и не заменяет полноценный DI-контейнер.

Для Mockito 5.11.0 важны границы стратегий:

1. При создании объекта выбирается самый большой конструктор. Ссылочный аргумент без подходящего двойника может получить null: это не поиск самого большого **полностью удовлетворимого** конструктора.
2. Если создание через конструктор успешно, Mockito не обязан затем внедрять остальные моки через сеттеры и поля.
3. Если constructor injection не применился, возможны setter/field injection. Невнедрённую зависимость иногда придётся задать самому, но исключение, брошенное самим конструктором, не игнорируется: инициализация теста завершится ошибкой.

```java
@ExtendWith(MockitoExtension.class)
class OrderServiceTest {
    @Mock OrderRepository orderRepository;
    @Mock PaymentGateway paymentGateway;
    @InjectMocks OrderService orderService;
}
```

Здесь предполагается совместимый конструктор `OrderService(OrderRepository, PaymentGateway)`. Аннотация не обещает внедрения **всех** объявленных моков, не строит рекурсивно произвольный граф реальных объектов и не проверяет за вас инварианты конструктора. Если создание SUT требует точного контроля, обычный `new OrderService(repo, gateway)` делает зависимости явными. [Контракт InjectMocks 5.11.0](https://github.com/mockito/mockito/blob/v5.11.0/src/main/java/org/mockito/InjectMocks.java).

---

## Q6. (!) Чем отличается `@Spy` от `@Mock`?

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

Главное отличие в одной фразе: **mock — это пустышка**, у которой по умолчанию заглушены все методы; **spy — это настоящий объект**, у которого по умолчанию работают все методы, а заглушаются только выбранные.

Из этого следствие: spy всегда оборачивает реальный экземпляр. `@Spy` без явной инициализации поля попытается создать объект через конструктор без аргументов — если такого конструктора нет, тест упадёт.

---

## Q7. Что такое `@Captor` и для чего используется?

`@Captor` декларативно создаёт `ArgumentCaptor` с правильным дженерик-типом. Захватчик ловит аргумент, который тестируемый код передал в мок, — чтобы потом сделать assertions по содержимому этого аргумента, а не только по факту вызова.

Главная польза аннотации перед ручным созданием — она обходит стирание дженериков: `@Captor ArgumentCaptor<List<User>>` сохраняет тип, тогда как `ArgumentCaptor.forClass(List.class)` не даст уточнить параметр.

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

## Q8. (!) Чем отличается `MockitoExtension` от `MockitoAnnotations.openMocks()`?

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

**Когда что:** в JUnit 5 по умолчанию берите `MockitoExtension` — он короче и из коробки включает strict stubbing. `openMocks()` нужен там, где расширение недоступно: JUnit 4, кастомный раннер, наследование от чужого базового класса, или когда моки нужно создать в нестандартный момент жизненного цикла. Если используете `openMocks()`, обязательно закрывайте `Closeable` в `@AfterEach` — иначе моки утекут между тестами.

---

## Q9. Как работает `when().thenReturn()`?

`when(mock.method(args)).thenReturn(value)` задаёт stubbing: подходящий вызов получает указанное значение. Аргументы по умолчанию сопоставляются через `equals`; для более общего условия применяют matchers (Q18).

```java
when(userRepository.findById(1L)).thenReturn(Optional.of(new User(1L, "Alice")));

Optional<User> result = userRepository.findById(1L);
assertThat(result).isPresent();
```

Если подходящего стаба нет, mock обычно использует свой default Answer. Однако это не безусловное правило «другой аргумент всегда вернёт null»: в режиме `STRICT_STUBS` потенциальное несовпадение со stubbing может вызвать `PotentialStubbingProblem`. В Mockito 5.11.0 диагностика учитывает дополнительные условия, в том числе расположение вызова и stubbing в исходных файлах; не каждый новый аргумент обязательно приводит к этой ошибке. `WARN` и `STRICT_STUBS` — разные режимы (Q28), а `ArgumentMismatchException` не является названием этой диагностики Mockito.

`thenReturn(first).thenReturn(second)` возвращает first один раз, затем повторяет second. Это описание постоянного последнего значения; если последним настроен динамический Answer, при последующих вызовах выполняется его логика (Q30).

---

## Q10. (!) Чем отличается `when().thenReturn()` от `doReturn().when()`?

Java вычисляет аргумент `mock.foo()` **до** входа в `when(...)`. Поэтому на обычном mock со стандартным Answer получается обычный default, на spy может выполниться реальный метод, а уже настроенный throwing/custom Answer тоже может повлиять на настройку. `doReturn(value).when(mock).foo()` задаёт ответ без такого вызова реального тела выбранного метода при stubbing.

`thenReturn` типизирован результатом выбранного вызова, поэтому обычную несовместимость возвращаемого типа обнаруживает компилятор. `doReturn` принимает Object и проверяет совместимость во время настройки: например, строка для метода с результатом int компилируется, но даёт `WrongTypeOfReturnValue`. Это не отменяет ограничений type erasure для содержимого generic-контейнеров.

| Аспект | `when().thenReturn()` | `doReturn().when()` |
|--------|----------------------|---------------------|
| Non-void метод обычного mock | Обычно предпочтительнее | Поддерживается |
| Non-void метод spy | При настройке может выполниться реальное тело | Позволяет избежать его исполнения |
| Void-метод | `when(voidCall())` не компилируется | Выражение компилируется, но Mockito отклоняет возвращаемое значение |

```java
List<String> spyList = spy(new ArrayList<String>());

// get(0) выполнится до when и бросит IndexOutOfBoundsException:
// when(spyList.get(0)).thenReturn("Alice");

doReturn("Alice").when(spyList).get(0);
assertEquals("Alice", spyList.get(0));
```

Для void используют `doNothing()`, `doThrow()` или `doAnswer()`, а не `doReturn(value)`. На обычном mock void-вызов по умолчанию и так ничего не делает. `doReturn(value).when(mock).voidMethod()` компилируется, но при настройке даёт `CannotStubVoidMethodWithReturnValue`; это ошибка Mockito при исполнении, а не ошибка Java-компилятора.

Для spy важна цель: если требуется заглушить non-void метод без выполнения его тела при настройке, подходит doReturn; если реальный вызов намерен, when тоже допустим. Ни одна форма не отменяет обычного вычисления Java-аргументов, передаваемых в метод.

---

## Q11. Как заставить mock выбросить исключение?

Способ зависит от того, возвращает метод значение или нет.

Для методов с возвращаемым значением — `thenThrow()`:
```java
when(paymentGateway.charge(any()))
    .thenThrow(new PaymentException("Insufficient funds"));
```

Для `void`-методов синтаксис `when(...).thenThrow()` не компилируется, нужен `doThrow()`:
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

## Q12. Что такое `thenAnswer()` и когда его применять?

`thenAnswer()` задаёт **динамический** ответ: вместо фиксированного значения вы передаёте функцию, которая вычисляет результат прямо в момент вызова — на основе переданных аргументов. Нужен, когда статического `thenReturn()` не хватает.

```java
when(userService.getUserName(anyLong()))
    .thenAnswer(invocation -> {
        Long id = invocation.getArgument(0);
        return "User_" + id;
    });

assertThat(userService.getUserName(42L)).isEqualTo("User_42");
assertThat(userService.getUserName(99L)).isEqualTo("User_99");
```

Когда применять:
- Ответ зависит от аргумента (как в примере: `id` → `"User_" + id`).
- Нужно вызвать переданный в мок callback — `Consumer`, `Function` — синхронно прямо в тесте.
- Требуется сложная логика или побочный эффект, который не выразить через `thenReturn()`.

---

## Q13. Как заглушить `void`-метод с помощью `doNothing()`?

На обычном mock `void`-методы и так ничего не делают по умолчанию, поэтому `doNothing()` там нужен редко — в основном для читаемости. По-настоящему он полезен в двух случаях: чтобы **заглушить void-метод на spy** (иначе вызовется реальная реализация) и чтобы **чередовать поведение** void-метода между вызовами.

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

## Q14. (!) Как проверить вызов метода с помощью `verify()`?

`verify()` проверяет факт взаимодействия с моком: что нужный метод был вызван, с нужными аргументами и нужное число раз. Без второго аргумента `verify()` означает «ровно один раз».

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

`verify()` понимает матчеры `ArgumentMatchers`. **Важное правило:** если хотя бы один аргумент задан матчером, все остальные тоже должны быть матчерами — смешивать матчеры и литералы нельзя. Чтобы передать конкретное значение рядом с матчером, оберните его в `eq(value)` (см. ошибку №1 в Q32).

---

## Q15. Какие режимы верификации поддерживает Mockito?

Режим — это второй аргумент `verify(mock, mode)`. По умолчанию (без режима) подразумевается `times(1)`. Доступные режимы:

| Метод | Описание |
|-------|----------|
| `times(n)` | Ровно `n` вызовов |
| `never()` | Ноль вызовов (синоним `times(0)`) |
| `atLeastOnce()` | Минимум 1 вызов |
| `atLeast(n)` | Минимум `n` вызовов |
| `atMost(n)` | Максимум `n` вызовов |
| `only()` | Ровно 1 вызов этого метода и никаких других на данном mock |

```java
verify(service, times(2)).process(any());
verify(service, atLeastOnce()).init();
verify(service, only()).start();  // только этот метод и больше ничего
```

---

## Q16. Что делают `verifyNoInteractions()` и `verifyNoMoreInteractions()`?

Оба проверяют отсутствие вызовов, но в разные моменты.

**`verifyNoInteractions(mock)`** — мок вообще не трогали ни разу за тест. Подходит, когда нужно доказать, что некий побочный эффект не произошёл вовсе (например, при невалидном вводе письмо не отправлялось):

```java
verifyNoInteractions(auditService, notificationService);
```

**`verifyNoMoreInteractions(mock)`** — после уже выполненных `verify()` на моке не осталось **непроверенных** вызовов. То есть «всё, что мок делал, я перечислил, и больше ничего не было»:

```java
verify(repo).save(user);
verifyNoMoreInteractions(repo);  // убедиться, что delete/update не вызывались
```

Короче: `verifyNoInteractions` — «ничего не было вообще», `verifyNoMoreInteractions` — «всё уже проверено выше, лишнего нет».

**Подводный камень:** `verifyNoMoreInteractions()` легко превращается в антипаттерн. Он делает тест хрупким — любой новый, пусть и безобидный, вызов на моке ломает тест. Применяйте точечно, а не «на всякий случай» в конце каждого теста.

---

## Q17. (!) Как захватить аргумент с помощью `ArgumentCaptor`?

`ArgumentCaptor` перехватывает объект, который тестируемый код передал в мок, и сохраняет его — чтобы вы могли проверить не сам факт вызова, а **состояние** аргумента: поля, статус, размер коллекции. Это удобно, когда аргумент собирается внутри тестируемого кода и его не с чем сравнить заранее.

Порядок работы — три шага: вызвать тестируемый код → передать `captor.capture()` внутрь `verify()` → достать пойманное значение через `getValue()`.

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

## Q18. Какие матчеры аргументов предоставляет `ArgumentMatchers`?

Матчеры из `org.mockito.ArgumentMatchers` описывают, какие аргументы должна принять заглушка или верификация, не привязываясь к конкретному значению. Самые ходовые:

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

**Тонкость с `any()`:** `any()` пропускает и `null`, а `any(Foo.class)` и `anyString()` — нет, поэтому именно типизированные матчеры стоит брать, когда `null` в этом месте быть не должно.

---

## Q19. Что такое `argThat()` и когда он нужен?

`argThat(ArgumentMatcher<T>)` задаёт произвольное условие для аргумента в виде предиката — когда готовых матчеров (`eq`, `contains`, `anyString`) не хватает и нужна своя логика проверки.

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

**Отличие от `ArgumentCaptor`:** `argThat` встраивает проверку прямо в `verify()` или `when()` и хорош для коротких условий; `ArgumentCaptor` отдаёт пойманный объект наружу и удобнее для длинных, детальных assertions (подробный разбор выбора — в Q39).

---

## Q20. (!) Как проверить порядок вызовов с помощью `InOrder`?

Обычный `verify()` проверяет факт вызова, но не его очерёдность. Когда порядок важен (открыть транзакцию → сохранить → закоммитить), используют `InOrder`: вы создаёте его для нужных моков и затем верифицируете вызовы в той последовательности, в какой они должны были произойти.

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

`InOrder` проверяет только **относительный** порядок перечисленных вызовов: между ними могут быть любые другие вызовы, это не считается нарушением. То есть `InOrder` отвечает на вопрос «A произошло раньше B?», а не «A и B шли подряд?».

---

## Q21. Что такое BDDMockito и как использовать стиль `given/when/then`?

`BDDMockito` — тонкая обёртка над тем же Mockito, которая переименовывает методы под нотацию Given/When/Then. Функционально ничего нового: `given().willReturn()` делает ровно то же, что `when().thenReturn()`, но читается естественнее, когда тест структурирован по BDD. Удобно, чтобы и настройка моков, и сам тест говорили на одном языке.

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

## Q22. (!) Что такое `@MockBean` и `@SpyBean` в Spring Boot?

`@MockBean` и `@SpyBean` — аннотации Spring Boot Test, которые подменяют **бин** в `ApplicationContext` на mock/spy. В отличие от обычного `@Mock`, мок попадает прямо в Spring-контекст, поэтому его получат все бины, которым он внедряется через `@Autowired`. `@MockBean` заменяет бин полностью, `@SpyBean` оборачивает реальный бин, сохраняя его поведение по умолчанию.

**Spring Boot 3.4+ (важно для senior):** `@MockBean` и `@SpyBean` помечены `@Deprecated` начиная со Spring Boot 3.4 (ноябрь 2024). Рекомендуемая замена — `@MockitoBean` и `@MockitoSpyBean` из пакета `org.springframework.test.context.bean.override.mockito` (общий механизм bean override в Spring Framework 6.2). Семантика подмены бина и инвалидации кеша контекста та же; в новом коде используйте `@MockitoBean`/`@MockitoSpyBean`.

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

**Подводный камень — производительность.** Каждое уникальное сочетание `@MockBean` / `@SpyBean` ломает кеш контекста: Spring пересоздаёт `ApplicationContext` под этот набор подмен, и на больших приложениях это заметно замедляет прогон. Поэтому одни и те же моки выносят в общий базовый тестовый класс — так контекст переиспользуется между тестами.

---

## Q23. Как работает Mockito с `@ExtendWith(MockitoExtension.class)` в JUnit 5?

`MockitoExtension` интегрирует Mockito с Jupiter. В версии 5.11.0 его `BeforeEachCallback` начинает сессию и обрабатывает аннотированные поля; `AfterEachCallback` завершает сессию, закрывает управляемые scoped mocks и проверяет strict stubbing. Это не обещание вызвать `reset()` на каждом объекте, ссылку на который сохранил ваш код. `ParameterResolver` поддерживает параметры `@Mock` и `@Captor`.

```java
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PaymentServiceLifecycleTest {
    interface Gateway { boolean charge(int amount); }
    static class Service {
        private final Gateway gateway;
        Service(Gateway gateway) { this.gateway = gateway; }
        boolean pay(int amount) { return gateway.charge(amount); }
    }

    @Test
    void pays(@Mock Gateway gateway) {
        when(gateway.charge(100)).thenReturn(true);
        Service service = new Service(gateway);
        assertTrue(service.pay(100)); // stub действительно используется
    }
}
```

По умолчанию действует `STRICT_STUBS`: неиспользованный обычный stub может привести к `UnnecessaryStubbingException` при завершении теста; lenient-настройки и уже произошедшая ошибка теста влияют на диагностику.

**Сохранённый граф объектов:** при `PER_CLASS` поле `@Mock` может получить новый mock перед следующим тестом, но уже созданный `@InjectMocks` SUT с final constructor dependency способен сохранить **старый** mock и его stub. Новое значение поля теста не переписывает все ссылки в графе. Для обычных независимых тестов пересоздавайте SUT с текущими зависимостями в `@BeforeEach` или используйте подходящий lifecycle; не полагайтесь на глобальный сброс со стороны расширения.

---

## Q24. Что делает `Mockito.reset()`?

`reset(mock)` полностью обнуляет mock: стирает все заглушки и историю вызовов, возвращая объект в состояние «как только что созданный».

```java
@AfterEach
void tearDown() {
    reset(userRepository, emailService);
}
```

**Но в обычных тестах `reset()` — антипаттерн.** Потребность сбросить мок посреди теста почти всегда означает, что тест делает слишком много или переиспользует общее состояние. Правильный путь — независимые тесты: новый mock создаётся заново в `@BeforeEach`, а не очищается старый.

**Когда оправдан:** Spring-тесты с `@MockBean`, где бин-мок живёт в кешируемом контексте и пересоздавать контекст ради чистого мока слишком дорого. Тогда `reset()` в `@BeforeEach` — меньшее зло.

---

## Q25. (!) Как мокировать статические методы с помощью `mockStatic()`?

`mockStatic(Class)` возвращает `MockedStatic`, управляющий статическими методами выбранного класса в потоке создания. Control нужно закрыть; `try-with-resources` — удобный способ гарантировать это, также возможен явный lifecycle через `close()`/расширение. Закрытие scope восстанавливает обычное поведение в этом потоке.

Для static mocking нужен поддерживающий его inline maker: API появился в Mockito 3.4.0; в обычной конфигурации Mockito 5.11.0 inline выбран по умолчанию. Переопределённый maker и возможности JVM нужно учитывать отдельно. Пример использует собственный тип:

```java
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

class StaticIdTest {
    static class IdSource {
        static String next() { return "real-id"; }
    }
    static class OrderService {
        String generateId() { return IdSource.next(); }
    }

    @Test
    void usesControlledId() {
        OrderService service = new OrderService();
        try (MockedStatic<IdSource> ids = mockStatic(IdSource.class)) {
            ids.when(IdSource::next).thenReturn("fixed-id");
            assertEquals("fixed-id", service.generateId());
            ids.verify(IdSource::next, times(1));
        }
        assertEquals("real-id", service.generateId());
    }
}
```

При стандартном Answer незастабленные статические методы этого класса получают defaults. Поэтому в прежнем примере `UUID.fromString(...)` **внутри** активного `MockedStatic<UUID>` возвращал null: значение следовало создать до открытия scope. Подмена JDK-классов требует особого внимания к ограничениям maker; собственный тип делает пример проще. Область потока и повторная регистрация разобраны в Q41. Control следует использовать и закрывать в его потоке, не передавая его для работы другому потоку.

---

## Q26. Как мокировать `final`-классы и методы?

Subclass maker опирается на наследование и не переопределяет final-методы. Inline maker поддерживает final-классы и методы через instrumentation. В обычной конфигурации Mockito 5.11.0 inline — default; это не гарантия для любой платформы или проекта с переопределённым `MockMaker`.

Для старых линий Mockito 3–4 inline можно выбрать провайдером:

```text
# src/test/resources/mockito-extensions/org.mockito.plugins.MockMaker
mock-maker-inline
```

Пример собственного final-типа при активном inline maker:

```java
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

class FinalMockTest {
    static final class PriceCalculator {
        final int total(int quantity) { return quantity * 100; }
    }

    @Test
    void mocksFinalTypeAndMethod() {
        PriceCalculator calculator = mock(PriceCalculator.class);
        when(calculator.total(2)).thenReturn(7);
        assertEquals(7, calculator.total(2));
    }
}
```

**Ограничения Mockito 5.11.0:** даже inline не разрешает `mock(String.class)`, Class и wrapper types. Обычный enum при inline может мокироваться; дополнительные interfaces/serialization и особенности типа могут изменить доступность. Примитив не является объектом для mock; обычный публичный stubbing API не открывает доступ к private-методу. Inline не инструментирует native-методы, у которых нет подходящего Java-байткода.

Универсальное утверждение «inline всегда медленнее» не следует из его устройства: стоимость зависит от сценария, JVM и прогрева. Совместимость с coverage/agent-инструментами нужно проверять для конкретного набора версий; здесь производительность и все комбинации агентов не измерялись.

---

## Q27. Что такое `RETURNS_DEEP_STUBS` и когда его применять?

`RETURNS_DEEP_STUBS` — стратегия ответов по умолчанию (`Answer`), при которой каждый промежуточный вызов в цепочке автоматически возвращает новый мок. Это позволяет настроить цепочку `a.getB().getC()` одной строкой, не создавая и не стабя вручную каждый уровень:

```java
// Без RETURNS_DEEP_STUBS пришлось бы настраивать каждый уровень
Order order = mock(Order.class, RETURNS_DEEP_STUBS);

when(order.getCustomer().getAddress().getCity()).thenReturn("Moscow");

// Теперь работает без NPE
String city = order.getCustomer().getAddress().getCity();
assertThat(city).isEqualTo("Moscow");
```

Когда уместно:
- Работа с чужим API, возвращающим fluent builder, который не переделать.
- Настройка громоздких конфигурационных объектов в тестах.

**Подводный камень:** если deep stubs понадобились для **вашего** доменного кода, это обычно симптом нарушения Закона Деметры (цепочки `a.getB().getC()`). Лечится не моком, а пересмотром дизайна — например, передачей нужного объекта напрямую вместо длинной навигации.

---

## Q28. (!) Что такое strict stubbing и зачем он нужен?

**Strict stubbing** — режим, который не даёт заглушкам «протухать» и ловит несоответствия между тем, как мок настроили, и как его реально вызвали. Введён в Mockito 2, в `MockitoExtension` включён по умолчанию. Конкретно он добавляет две проверки:

1. **`UnnecessaryStubbingException`** — тест падает, если настроили заглушку, но так её и не вызвали (мёртвая настройка).
2. **`StubbingArgumentMismatchException`** — сигнал, если stub настроен на одни аргументы, а вызов пришёл с другими: чаще всего это и есть причина «почему мок вернул null».

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

**Зачем это нужно:** strict stubbing держит тесты в чистоте — мёртвые заглушки не накапливаются, опечатки в аргументах всплывают сразу, а при рефакторинге сразу видно, какие настройки больше не используются. По сути это статический контроль качества самих тестов.

---

## Q29. Как сделать отдельный stub ненастойчивым с помощью `lenient()`?

`lenient()` точечно снимает strict-проверки с одной конкретной заглушки — она перестаёт быть «обязательной к использованию» и не вызовет `UnnecessaryStubbingException`. Типичный случай: общая настройка в `@BeforeEach`, которая нужна большинству тестов класса, но не всем.

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

**Почему точечный `lenient()` лучше, чем `@MockitoSettings(strictness = Strictness.LENIENT)`:** аннотация отключает strict-режим на весь класс, и тогда мёртвые заглушки в остальных тестах перестают ловиться. `lenient()` ослабляет только одну выбранную настройку, оставляя строгий контроль для всех прочих.

---

## Q30. Можно ли вернуть разные значения при последовательных вызовах?

Да — это нужно, например, чтобы смоделировать счётчик, retry или восстановление после сбоя. Есть несколько способов:

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

**Запомнить:** цепочка не зацикливается — последний `.thenReturn()` «залипает» и возвращается на всех вызовах после исчерпания цепочки.

---

## Q31. Чем отличается `Mockito.spy()` от `@Spy`?

Оба создают одинаковый spy — разница лишь в том, кто отвечает за создание реального объекта-основы: вы сами (`Mockito.spy()`) или Mockito (`@Spy`).

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

Итог: разница чисто синтаксическая, поведение идентично. Оба выполняют реальные методы, пока конкретный метод не заглушён через `doReturn`/`doThrow` (на spy именно `do*`-форма, а не `when().thenReturn()` — см. Q10).

---

## Q32. (!) Каковы типичные ошибки при использовании Mockito?

Большинство ошибок Mockito сводятся к двум причинам: нарушен порядок «настройка → вызов → проверка» либо неверно использованы матчеры. Самые частые:

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

## Q33. Что такое `UnnecessaryStubbingException` и как её избежать?

`UnnecessaryStubbingException` — исключение строгого режима (strict stubbing): тест падает, потому что заглушку настроили, но тестируемый код её так и не вызвал. По сути Mockito говорит: «эта настройка — мёртвый код, убери или поправь».

Откуда берётся:
- Общая заглушка в `@BeforeEach` не нужна конкретному тесту.
- Тест изменили, а ставшую лишней заглушку забыли удалить.
- Заглушка настроена на ветку кода, которая в этом тесте не выполняется (часто из-за несовпадения аргументов).

Как чинить (от лучшего к худшему):

```java
// 1. Убрать неиспользуемую заглушку (предпочтительно)

// 2. Использовать lenient() для заглушек в @BeforeEach
lenient().when(repo.findAll()).thenReturn(Collections.emptyList());

// 3. Аннотация на уровне класса (крайний случай)
@MockitoSettings(strictness = Strictness.LENIENT)
class MyTest { ... }
```

---

## Q34. Когда не стоит использовать Mockito?

Mockito хорош для изоляции зависимостей с поведением, но не универсален. Чрезмерное мокирование часто маскирует проблемы дизайна или подменяет интеграционные тесты. Признаки, что инструмент выбран неверно:

**1. Мокирование value objects** (`String`, `LocalDate`, DTO) — у них нет поведения, только данные; создавайте реальные экземпляры, мок здесь лишь усложняет тест.

**2. Мокирование всего подряд** — если в тесте 5+ замоканных зависимостей, скорее всего класс нарушает SRP и его стоит разбить.

**3. Подмена реальной БД** — для интеграционных тестов берите Testcontainers, H2 или встроенный Redis: мок не проверит реальные запросы и схему.

**4. Массовое мокирование статики и конструкторов** — сигнал нарушения DIP: код сам создаёт зависимости вместо внедрения. Лечится дизайном, а не моками.

**5. Проверка взаимодействия реальных объектов друг с другом** — это задача интеграционных тестов, а не unit-теста с моками.

Альтернативы:
- **Фейки** (fake) — для репозиториев, кэшей: `HashMap`-based реализации
- **Testcontainers** — для реальных сервисов (PostgreSQL, Redis, Kafka)
- **WireMock** — для HTTP-сервисов

---

## Q35. Как мокировать конструкторы с помощью `mockConstruction()`?

`mockConstruction()` перехватывает оператор `new`: пока открыт `try-with-resources`, каждый `new TargetClass()` внутри тестируемого кода возвращает не реальный объект, а мок. Это спасает, когда класс создаёт зависимость сам и её нельзя внедрить через конструктор. Доступно с Mockito 3.5+.

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

Как и `mockStatic()`, работает только в `try-with-resources` — за пределами блока `new` снова создаёт настоящие объекты.

**Когда видите этот приём — насторожитесь:** потребность мокировать конструктор почти всегда означает, что класс создаёт зависимости напрямую вместо внедрения через DI. Чище — передать зависимость в конструктор и замокировать её обычным `@Mock`. `mockConstruction()` оправдан в основном для legacy-кода, который нельзя переписать.

---

## Q36. (!) В чём разница между `mockito-core` и `mockito-inline`?

Разница между этими артефактами — в `MockMaker`, движке создания моков. Исторически Mockito поставлялся в двух вариантах:

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

**За что платим** (компромиссы inline mock maker):
- Медленнее subclass-версии — инструментирует классы через Java Agent в рантайме.
- Может конфликтовать с Jacoco, JRebel и другими байткод-инструментами.
- Примитивы и `private`-методы по-прежнему недоступны.
- Нужна JVM с поддержкой Instrumentation API.

**Итог для собеседования:** на Mockito 5+ отдельный `mockito-inline` не нужен — всё уже в `mockito-core`. Знать про `mockito-inline` стоит для проектов на Mockito 3–4.

---

## Q37. Что такое `MockSettings` и какие опции он предоставляет?

`MockSettings` — интерфейс для тонкой настройки мока в момент его создания, через `Mockito.mock(Class, MockSettings)` или билдер `withSettings()`. Нужен в нечастых, но конкретных ситуациях: задать стратегию ответов по умолчанию, сделать мок `Serializable`, навесить дополнительные интерфейсы или включить отладочный лог вызовов.

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

На практике `withSettings()` нужен редко: в 99% тестов хватает обычного `mock()`. Держите его в уме для отладки (`verboseLogging()`, `RETURNS_SMART_NULLS`) и для legacy-кода, требующего `Serializable`-зависимостей.

---

## Q38. Какие встроенные `Answer` доступны в Mockito?

`Answer` определяет, что мок возвращает на **незастабленные** вызовы. Mockito предоставляет готовый набор стратегий в enum `org.mockito.Answers` — их передают вторым аргументом в `mock()`:

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

## Q39. (!) Когда выбирать `ArgumentCaptor`, а когда `argThat()`?

Оба про аргумент, переданный в мок, но решают разные задачи. Короткое правило: **`argThat()` — для условия в момент вызова** (особенно в стаббинге), **`ArgumentCaptor` — для assertions после вызова** (особенно сложных).

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

## Q40. Как тестировать код с callback-функциями через `doAnswer()`?

Частая задача: тестируемый метод принимает callback (`Consumer`, `Callback`, `Runnable`) и где-то его вызывает. Мок сам callback не дёрнет — но через `doAnswer()` вы можете достать переданный callback из аргументов и выполнить его прямо в тесте, синхронно и предсказуемо:

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

Альтернатива через `ArgumentCaptor` — когда хочется контролировать **момент** вызова callback, а не делать его сразу. Сначала ловим callback, потом вызываем его, когда нужно по сценарию:
```java
ArgumentCaptor<Consumer<String>> cbCaptor = ArgumentCaptor.forClass(Consumer.class);
handler.handle("query");

verify(asyncService).fetchAsync(eq("query"), cbCaptor.capture());
cbCaptor.getValue().accept("result-42");  // симулируем ответ здесь

assertThat(handler.getState()).isEqualTo(State.READY);
```

Ключевой принцип: **не используйте `Thread.sleep()`** для тестирования асинхронного кода. Либо делайте callback синхронным через `doAnswer`, либо `Awaitility.await()` для реальной асинхронности.

---

## Q41. (!) В чём ограничение области видимости `MockedStatic` по потокам?

`MockedStatic` действует в потоке, где создан control. Это не наследуемый контекст: отправка задачи в другой поток или pool сама по себе не переносит туда подмену.

Два разных потока могут одновременно держать отдельные static scopes одного класса. Ошибка `static mocking is already registered` возникает при второй активной регистрации **того же класса в том же потоке**. В другом потоке без своего scope будет обычный метод; со своим scope — его собственная настройка.

```java
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.mockito.exceptions.base.MockitoException;
import java.util.List;
import java.util.concurrent.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class StaticThreadScopeTest {
    static class Signal {
        static String value() { return "real"; }
    }

    @Test
    void separatesThreadScopes() throws Exception {
        ExecutorService pool = Executors.newSingleThreadExecutor();
        try (MockedStatic<Signal> main = mockStatic(Signal.class)) {
            main.when(Signal::value).thenReturn("main");
            Future<List<String>> worker = pool.submit(() -> {
                String before = Signal.value();
                String during;
                try (MockedStatic<Signal> local = mockStatic(Signal.class)) {
                    local.when(Signal::value).thenReturn("worker");
                    during = Signal.value();
                }
                return List.of(before, during, Signal.value());
            });
            assertEquals(List.of("real", "worker", "real"),
                worker.get(5, TimeUnit.SECONDS));
            assertEquals("main", Signal.value());
            assertThrows(MockitoException.class, () -> mockStatic(Signal.class));
        } finally {
            pool.shutdownNow();
        }
        assertEquals("real", Signal.value());
    }
}
```

Не используйте один control из другого потока: его API не потокобезопасен. Thread-local scope не заменяет закрытие control и не изолирует произвольные общие данные теста. Если код должен иметь управляемые часы или генератор в нескольких потоках, передача `Clock`/`Supplier` через зависимости часто проще, чем настройка static scope на каждом исполнителе. Этот пример проверяет семантику scopes, а не всё поведение асинхронного приложения.

---

## Q42. Что делает `thenCallRealMethod()` и чем отличается от `@Spy`?

`thenCallRealMethod()` настраивает вызов реальной реализации выбранного метода **на mock-объекте**. Mock уже является Java-объектом; stubbing не создаёт его заново и не вызывает конструктор. При обычном создании mock конструктор часто обходится, поэтому его instance fields могут иметь defaults. Но `withSettings().useConstructor(...)` позволяет создать mock с выполнением конструктора:

```java
import org.junit.jupiter.api.Test;
import java.util.Locale;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

class RealMethodStateTest {
    static class Greeter {
        private final String prefix;
        Greeter(String prefix) { this.prefix = prefix; }
        String greet(String name) { return prefix.toUpperCase(Locale.ROOT) + name; }
    }

    @Test
    void usesConstructorStateForRealMethod() {
        Greeter greeter = mock(Greeter.class,
            withSettings().useConstructor("hi ").defaultAnswer(RETURNS_DEFAULTS));
        when(greeter.greet("Alice")).thenCallRealMethod();
        assertEquals("HI Alice", greeter.greet("Alice"));
    }
}
```

| Аспект | Mock + thenCallRealMethod | Spy существующего объекта |
|--------|---------------------------|---------------------------|
| Объект | Есть; выбранный метод исполняет реальный код | Есть; обычный spy копирует состояние исходного объекта |
| Конструктор | Зависит от способа создания mock; stubbing его не запускает | Исходный объект создаёт пользователь; spy не исправляет его инварианты |
| Остальные методы | Текущий default Answer или отдельные stubs | Обычно реальные, если не переопределены stubbing |
| Состояние и зависимости | Могут быть defaults либо заданы при создании | Могут включать null и общие изменяемые ссылки |

`@Spy` на поле может использовать явно заданный экземпляр; при незаданном значении Mockito пытается создать его сам, обычно через конструктор без аргументов. Это не проверяет корректность бизнес-инвариантов.

У `spy(existing)` нет гарантии делегирования всех вызовов исходному экземпляру; обычный spy копирует его состояние, при этом ссылки на вложенные объекты могут оставаться общими. Поэтому ни spy, ни real-method stub сами по себе не гарантируют отсутствие NPE или корректность зависимостей.

Для default-метода интерфейса (без instance fields), stateless-метода или явно сконструированного объекта real-method stubbing может быть уместен. Для stateful partial mock выбирайте способ создания и зависимости по реальному поведению класса, а не по обещанию общей «безопасности» spy.

---

## Q43. Как инжектировать `@Mock` и `@Captor` в параметры метода JUnit 5?

`MockitoExtension` реализует JUnit-интерфейс `ParameterResolver`, поэтому моки и захватчики можно объявлять **прямо в параметрах** тестового метода — не заводя поля класса. Mockito создаст их перед запуском метода и подставит по аннотации:

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

Чем хорош подход с параметрами:
- **Изоляция** — каждый тест видит только те моки, что объявил сам.
- Нет разделяемого между тестами состояния (моки не «протекают» из теста в тест).
- Меньше полей в классе, тест читается компактнее.
- Зависимости теста описаны явно, прямо в сигнатуре.

Когда всё же оставлять поля класса:
- Один и тот же мок нужен в нескольких тестах — выносим в `@Mock`-поле + общую настройку в `@BeforeEach`.
- Используется `@InjectMocks` — он умеет внедрять только в поля, не в параметры.

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

## Q44. (!) Какие особенности у Mockito в Kotlin и зачем `mockito-kotlin`?

Классический Mockito писался под Java, а Kotlin строже к синтаксису и null-безопасности — отсюда несколько трений:

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

**Библиотека `mockito-kotlin`** — обёртка, которая закрывает все эти трения: даёт `whenever` вместо `` `when` ``, reified-версии `mock<T>()` и `any<T>()`, корректно работающие с non-null типами, и DSL для настройки моков:

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

**Альтернатива:** библиотека **MockK**, написанная с нуля под Kotlin. Она нативно понимает корутины и `suspend`-функции, поддерживает relaxed-моки (возвращают разумные значения без явной настройки) и идиоматичный DSL. Эмпирическое правило: на смешанных Java/Kotlin-кодовых базах берут `mockito-kotlin`, на чисто Kotlin-проектах MockK обычно удобнее.

---

## Q45. Что такое `@MockitoSettings` и как управлять strictness на уровне класса?

`@MockitoSettings` — аннотация уровня класса, которая задаёт настройки `MockitoExtension` сразу для всех тестов в нём. Чаще всего ею управляют strictness — например, разом переводят весь класс в `LENIENT`, не помечая каждую заглушку через `lenient()`:

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
- [Chaos Engineering](chaos-engineering-interview.md)
- [Contract Testing](contract-testing-interview.md)
- [Load Testing](load-testing-interview.md)
- [Mutation Testing](mutation-testing-interview.md)
- [Property-based Testing](property-based-testing-interview.md)
- [Шпаргалка: Mockito](../../testing/unit-testing/junit/mockito.md) — теория
- [MockK и Kotest](../programming-languages/kotlin/kotlin-testing-interview.md) — мокинг для Kotlin: final-классы, `suspend`-функции, top-level
