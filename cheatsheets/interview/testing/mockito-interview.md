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
updated: "2026-05-08"
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
> - [ ] Mockito заменяет реальный JVM bytecode тестируемого класса на тестовую версию | Mockito не модифицирует исходный код, а создаёт прокси через ByteBuddy/CGLib. ❌ ПОСЛЕДСТВИЕ: ожидание что mock «патчит» класс приведёт к попытке мокать `private`-методы и потере 2 дней на отладку.
> - [ ] Mockito выполняет тесты в отдельной JVM с замоканными зависимостями | Mockito работает в одной JVM с тестом — никакого отдельного процесса нет. ❌ ПОСЛЕДСТВИЕ: непонимание scope мока ведёт к утечкам `MockedStatic` между тестами и flaky CI.
> - [x] Mockito создаёт тестовые двойники зависимостей, чтобы изолировать класс от БД/HTTP/времени | Заменяет коллабораторов прокси-объектами с заданным поведением, ускоряет тесты в 100× и даёт детерминизм. ✓ ПРИМЕНЯТЬ: Spring Boot unit-тесты сервисов с `@MockBean` для `PaymentGateway`/`OrderRepository`. 📋 ПРАВИЛО: «Mock — это пульт для зависимости». 🔗 См. Q2, Q9, Q14.
> - [ ] Mockito автоматически генерирует assertions по поведению production-кода | Mockito не верифицирует корректность бизнес-логики — только взаимодействия. ❌ ПОСЛЕДСТВИЕ: «зелёный» тест без `assertThat` пропускает регрессии — баг доходит до прода и ловится PagerDuty.

## Q2. Чем отличаются mock, stub, spy и fake?

| Тип | Описание | Пример |
|-----|----------|--------|
| **Mock** | Объект с проверяемым поведением — записывает вызовы, используется для `verify()` | `Mockito.mock(Service.class)` |
| **Stub** | Возвращает заранее заданные данные; поведение важно, проверка вызовов — нет | `when(repo.find(1)).thenReturn(entity)` |
| **Spy** | Обёртка над реальным объектом; часть методов реальная, часть — заглушки | `Mockito.spy(new MyService())` |
| **Fake** | Упрощённая рабочая реализация (в памяти, без Mockito) | `HashMap` вместо реального кэша |

В Mockito **mock по умолчанию является и стабом**: на него можно и настраивать ответы, и верифицировать вызовы.

---


> [!mcq]
> - [ ] Mock и stub — синонимы; spy и fake — тоже одно и то же | Это четыре разных категории по Meszaros (xUnit Test Patterns) с разной зоной ответственности. ❌ ПОСЛЕДСТВИЕ: проверять `verify()` на fake-объекте — упадёт `NotAMockException`, тест не запустится в CI.
> - [ ] Spy — это реализация в памяти (in-memory fake) | Spy оборачивает реальный объект, по умолчанию вызывает реальные методы; fake — это упрощённая рабочая реализация (например, `HashMap` вместо БД). ❌ ПОСЛЕДСТВИЕ: путаница ведёт к `@Spy` поверх настоящего HTTP-клиента и реальным запросам в payment-API из теста.
> - [ ] Stub проверяет порядок вызовов через `verify()` | Verification — это роль mock; stub только возвращает заранее заданные данные. ❌ ПОСЛЕДСТВИЕ: тест считает stub корректно вызванным, но реально проверка не происходит — silent test pass.
> - [x] Mock верифицирует взаимодействия, stub возвращает данные, spy оборачивает реальный объект, fake — рабочая упрощённая реализация | В Mockito mock по умолчанию совмещает роли mock и stub — настраивается `when().thenReturn()` и проверяется `verify()`. ✓ ПРИМЕНЯТЬ: Netflix unit-тесты — `@Mock` для Eureka-клиента, fake `InMemoryRepository` для агрегатов. 📋 ПРАВИЛО: «Mock — verify, stub — return, spy — wrap, fake — simple impl». 🔗 См. Q1, Q6, Q14.

## Q3. (!) Как создать mock-объект в Mockito?

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
> - [ ] Только через `new MockitoMock<>(Class)` напрямую | Такого конструктора в API не существует — Mockito использует фабричные методы. ❌ ПОСЛЕДСТВИЕ: ChatGPT/копипаста с такого «примера» — `compilation error`, час дебага в IDE.
> - [x] `Mockito.mock(Class)`, `@Mock` поле + `MockitoExtension`, или `mock()` через static import | Три эквивалентных способа: статический фабричный метод, аннотация (требует `MockitoExtension`/`openMocks`), краткий `mock()` без префикса. ✓ ПРИМЕНЯТЬ: Spring Boot тесты сервисов — `@Mock UserRepository repo` через `@ExtendWith(MockitoExtension.class)`. 📋 ПРАВИЛО: «Три двери в моки: mock(), @Mock, withSettings()». 🔗 См. Q4, Q8, Q23.
> - [ ] Только через `@MockBean` от Spring Boot | `@MockBean` подходит только для интеграционных тестов с `ApplicationContext`; в чистом unit-тесте сервиса он перезагружает контекст и тормозит. ❌ ПОСЛЕДСТВИЕ: каждый `@MockBean` с уникальной конфигурацией пересоздаёт `ApplicationContext`, build-time CI растёт с 5 до 25 минут.
> - [ ] Через рефлексию: `Class.forName(...).getDeclaredField("mock")` | Mockito не использует ручную рефлексию пользователем — всё инкапсулировано в `MockMaker` (ByteBuddy). ❌ ПОСЛЕДСТВИЕ: попытка обойти API через reflection ломается на каждом обновлении JDK (Java 17+ strong encapsulation).

## Q4. Что делает аннотация `@Mock`?

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
> - [ ] Создаёт mock самостоятельно без какой-либо инфраструктуры | `@Mock` обрабатывается только при наличии `MockitoExtension` или явного вызова `MockitoAnnotations.openMocks(this)`. ❌ ПОСЛЕДСТВИЕ: без `@ExtendWith(MockitoExtension.class)` поле остаётся `null`, первый же `when()` бросает NPE и тест падает на старте.
> - [ ] Внедряет реальный экземпляр класса вместо мока | Это работа `@InjectMocks` для тестируемого класса; `@Mock` создаёт именно тестовый двойник. ❌ ПОСЛЕДСТВИЕ: путаница даёт реальный HTTP-клиент в тесте, который стучится в payment.live.api и тратит prod-bandwidth.
> - [x] Создаёт mock-объект и присваивает его в помеченное поле — аналог `Mockito.mock(Class.class)` но декларативно | Краткая запись для `@Mock UserRepository repo` вместо `repo = mock(UserRepository.class)` в `@BeforeEach`. ✓ ПРИМЕНЯТЬ: Stripe Java SDK тесты — `@Mock ChargeApi chargeApi` для изоляции от Stripe network. 📋 ПРАВИЛО: «@Mock = decorative mock()». 🔗 См. Q3, Q8, Q23.
> - [ ] Превращает поле в spy реального объекта | Spy создаётся через `@Spy`, не `@Mock`; разница — в выполнении реальных методов по умолчанию. ❌ ПОСЛЕДСТВИЕ: ожидание реального поведения от `@Mock` ведёт к получению дефолтных `null`/`0` и `NullPointerException` в проверках бизнес-логики.

## Q5. Что делает аннотация `@InjectMocks`?

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
> - [x] Создаёт реальный экземпляр тестируемого класса и инжектит в него `@Mock`/`@Spy` поля через конструктор/сеттеры/поля | Mockito выбирает наибольший конструктор, который можно удовлетворить имеющимися моками; при провале молча создаёт без injection. ✓ ПРИМЕНЯТЬ: AWS SDK тесты сервисов — `@InjectMocks OrderService` с `@Mock DynamoDbClient`/`@Mock SnsClient`. 📋 ПРАВИЛО: «@InjectMocks = SUT-сборщик из @Mock-полей». 🔗 См. Q4, Q6, Q31.
> - [ ] Создаёт mock тестируемого класса как обычный `@Mock` | `@InjectMocks` создаёт **реальный** объект SUT, а не его двойник — иначе тестировать было бы нечего. ❌ ПОСЛЕДСТВИЕ: ожидание мока на SUT — `verify(service).method()` падает с `NotAMockException`, тест не работает.
> - [ ] Регистрирует SUT как Spring-бин в `ApplicationContext` | `@InjectMocks` к Spring не относится — это чистая Mockito-аннотация без DI-контейнера. ❌ ПОСЛЕДСТВИЕ: ожидание `@Autowired` injection вместо ручного wiring приводит к NPE на полях, не помеченных `@Mock`.
> - [ ] Бросает исключение если хоть один `@Mock` нельзя внедрить | Mockito **молча** создаёт SUT при провале injection — никакого предупреждения, поле остаётся `null`. ❌ ПОСЛЕДСТВИЕ: переименование зависимости в конструкторе — silent injection failure, NPE в runtime, диагностика занимает часы.

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

Spy требует реального экземпляра. `@Spy` без инициализации попробует создать объект через конструктор без аргументов.

---


> [!mcq]
> - [ ] `@Mock` оборачивает реальный объект, `@Spy` создаёт пустой двойник | Наоборот: `@Spy` оборачивает реальный объект, `@Mock` — пустой двойник без реальной логики. ❌ ПОСЛЕДСТВИЕ: путаница ведёт к попытке ставить `when()` на `@Mock` ожидая реального вызова — silent NPE в проверке `verify(...).getName()`.
> - [ ] У них одинаковая семантика, разница только в имени | Семантика принципиально разная: реальный метод по умолчанию выполняется только у spy. ❌ ПОСЛЕДСТВИЕ: подмена `@Mock` на `@Spy` без понимания запускает реальный код — например, реальный `EmailService.send()` отправляет письма из теста.
> - [ ] `@Spy` нельзя настраивать через `when()` | Spy настраивается, но рекомендуется `doReturn().when(spy).method()` чтобы избежать вызова реального метода во время stubbing. ❌ ПОСЛЕДСТВИЕ: `when(spyList.size()).thenReturn(100)` для spy `ArrayList` сначала вызывает реальный `size()` — на null/exception тесте получаем NPE.
> - [x] `@Mock` создаёт пустой двойник, `@Spy` оборачивает реальный объект и вызывает реальные методы по умолчанию | Spy = реальный объект + возможность точечно переопределить методы через `doReturn().when()`; `@Mock` — изоляция, `@Spy` — частичная заглушка. ✓ ПРИМЕНЯТЬ: Apache Kafka тесты — `@Spy` поверх `KafkaTemplate` с переопределённым `send()` для проверки сериализации. 📋 ПРАВИЛО: «@Mock — чистый лист, @Spy — реальный объект под наблюдением». 🔗 См. Q2, Q31, Q42.

## Q7. Что такое `@Captor` и для чего используется?

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
> - [ ] Создаёт mock-объект, аналог `@Mock` | `@Captor` не создаёт mock — он создаёт `ArgumentCaptor<T>` для захвата аргумента, переданного в mock. ❌ ПОСЛЕДСТВИЕ: путаница приводит к попытке `verify(captor).save(...)` — `NotAMockException`, тест не запускается.
> - [x] Создаёт `ArgumentCaptor<T>` с правильным дженерик-типом для захвата аргумента из `verify()` | Альтернатива ручному `ArgumentCaptor.forClass(Order.class)` — компактнее и сохраняет дженерик параметризацию. ✓ ПРИМЕНЯТЬ: Booking.com тесты event-publisher'а — `@Captor ArgumentCaptor<BookingEvent>` для проверки payload отправляемого в Kafka. 📋 ПРАВИЛО: «@Captor — типизированный захватчик аргумента». 🔗 См. Q17, Q39, Q43.
> - [ ] Захватывает все исключения теста для последующего анализа | `@Captor` про аргументы методов мока, не про исключения; для исключений — `assertThatThrownBy()`. ❌ ПОСЛЕДСТВИЕ: ожидание поймать `RuntimeException` через captor — реальное исключение пробрасывается и тест падает на этапе `verify`.
> - [ ] Регистрирует callback для всех вызовов мока | Это работа `Answer`/`doAnswer()`; `@Captor` только сохраняет переданное значение для последующих assertion'ов. ❌ ПОСЛЕДСТВИЕ: ожидание реакции в момент захвата вместо `verify()` — тест проверяет до фактического вызова, проверки бессмысленны.

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

Предпочтительно использовать `MockitoExtension` в JUnit 5 — меньше бойлерплейта и строгая проверка заглушек.

---


> [!mcq]
> - [ ] Это синонимы — оба для JUnit 4 | `MockitoExtension` — это JUnit 5 Extension; для JUnit 4 был `MockitoJUnitRunner`. ❌ ПОСЛЕДСТВИЕ: использование `@RunWith(MockitoJUnitRunner.class)` в JUnit 5 не сработает — аннотации не обрабатываются, моки `null`, NPE.
> - [x] `MockitoExtension` — JUnit 5 Extension с автоматическим lifecycle (включая STRICT_STUBS), `openMocks(this)` — программный вызов в `@BeforeEach` для JUnit 4 или вручную | Extension декларативный и сам закрывает ресурсы; `openMocks` возвращает `Closeable`, нужно явно закрывать в `@AfterEach`. ✓ ПРИМЕНЯТЬ: Spring Boot 3 + JUnit 5 — везде `@ExtendWith(MockitoExtension.class)`, `openMocks` только для legacy JUnit 4 модулей. 📋 ПРАВИЛО: «Extension — для JUnit 5 декларативно, openMocks — для JUnit 4 программно». 🔗 См. Q4, Q23, Q28.
> - [ ] `openMocks` строже чем `MockitoExtension` | Наоборот: `MockitoExtension` по умолчанию включает `STRICT_STUBS`, а `openMocks` использует `LENIENT` если не настроить иначе. ❌ ПОСЛЕДСТВИЕ: ожидание strict проверок при `openMocks` — `UnnecessaryStubbingException` не ловится, мёртвые заглушки накапливаются.
> - [ ] `openMocks` устарел и не работает с Mockito 5+ | `openMocks` поддерживается и в Mockito 5; `initMocks` устарел в пользу `openMocks` начиная с Mockito 3.4 (возвращает `Closeable`). ❌ ПОСЛЕДСТВИЕ: миграция всех `openMocks` без причины ломает тесты в legacy-модулях, добавляет ненужный технический долг.

## Q9. Как работает `when().thenReturn()`?

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
> - [ ] `when()` модифицирует JVM bytecode метода во время теста | `when()` ничего не патчит — Mockito фиксирует последний вызов мока через thread-local state. ❌ ПОСЛЕДСТВИЕ: ожидание bytecode-магии ведёт к удивлению почему `when(spy.size()).thenReturn(100)` всё равно вызывает реальный `size()`.
> - [x] Mockito фиксирует последний вызов мока в `when(...)` и связывает его с `thenReturn`-значением через ongoing-stubbing | На каждый `mock.method(args)` Mockito проверяет соответствие аргументов и возвращает заглушенное значение, иначе — дефолт. ✓ ПРИМЕНЯТЬ: Hibernate тесты репозиториев — `when(em.find(User.class, 1L)).thenReturn(alice)` для изоляции от БД. 📋 ПРАВИЛО: «when() запоминает последний вызов, thenReturn() даёт ему значение». 🔗 См. Q3, Q10, Q30.
> - [ ] Можно вызывать `thenReturn` без `when()` | `thenReturn` обязательно следует за `when()` — иначе компилятор не примет синтаксис, а runtime бросит `MissingMethodInvocationException`. ❌ ПОСЛЕДСТВИЕ: попытка использовать «голый» `thenReturn` ломает компиляцию, новичок тратит час на поиск правильного синтаксиса.
> - [ ] Несколько `thenReturn` всегда возвращают первое значение бесконечно | Цепочка `thenReturn(a).thenReturn(b).thenReturn(c)` возвращает `a` на первый вызов, `b` на второй, `c` на третий и далее. ❌ ПОСЛЕДСТВИЕ: тест retry-логики получает один и тот же результат, retry-сценарий не верифицирован, баг утекает в прод.

## Q10. (!) Чем отличается `when().thenReturn()` от `doReturn().when()`?

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
> - [ ] Они полностью эквивалентны, разница только в синтаксисе | На spy и `void`-методах `when()` вызовет реальную реализацию во время stubbing — побочные эффекты или NPE. ❌ ПОСЛЕДСТВИЕ: `when(spyService.deleteAll()).thenThrow(...)` сначала реально удалит данные в test-DB, тесты ломают друг друга.
> - [ ] `doReturn()` нужен только для `void`-методов | `doReturn()` нужен и для spy с non-void методами, и для `void`-методов; `doNothing/doThrow` — специально для void. ❌ ПОСЛЕДСТВИЕ: использование `when()` на spy с побочным эффектом запускает реальный код — например, реальный `chargeCard()` через PaymentGateway spy.
> - [x] `when().thenReturn()` для `@Mock` non-void методов; `doReturn().when()` обязателен для `@Spy` (избегает реального вызова) и `void`-методов | На spy `when()` сначала вызывает реальный метод, потом стабит — для безопасности нужен `doReturn().when(spy).method()`. ✓ ПРИМЕНЯТЬ: тесты Spring `RestTemplate` spy — `doReturn(response).when(restTemplate).getForObject(...)` чтобы не делать реальный HTTP. 📋 ПРАВИЛО: «when() для @Mock, do*().when() для @Spy и void». 🔗 См. Q6, Q11, Q31.
> - [ ] `doReturn()` не работает с дженериками | `doReturn()` теряет проверку типов на этапе компиляции (возвращает `Object`), но runtime работает с дженериками — это компромисс. ❌ ПОСЛЕДСТВИЕ: ожидание compile-error с неверным типом — ошибка проявляется только в runtime через `ClassCastException` под нагрузкой.

## Q11. Как заставить mock выбросить исключение?

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
> - [ ] Только через `try { mock.method(); } catch (...) { throw e; }` | Это запуск исключения вручную в тесте, а не настройка мока на бросок при вызове. ❌ ПОСЛЕДСТВИЕ: ловить исключение, которое не вылетело из SUT, — тест зелёный без проверки error-handling, retry-логика не покрыта.
> - [ ] Через `assertThatThrownBy(() -> ...)` без настройки мока | `assertThatThrownBy` проверяет факт исключения после факта; его нужно настроить либо `when().thenThrow()`, либо `doThrow().when()` для void. ❌ ПОСЛЕДСТВИЕ: assert не сработает потому что мок ничего не бросает — false negative в тесте обработки timeout от внешнего API.
> - [x] `when(mock.method(args)).thenThrow(new Ex())` для non-void и `doThrow(new Ex()).when(mock).method(args)` для void | Для non-void можно передать `Class<? extends Throwable>` — Mockito создаст экземпляр сам через no-arg конструктор. ✓ ПРИМЕНЯТЬ: тесты Resilience4j circuit breaker — `doThrow(IOException.class).when(httpClient).send(...)` для проверки fallback-логики. 📋 ПРАВИЛО: «thenThrow() для return-методов, doThrow() для void». 🔗 См. Q9, Q13, Q40.
> - [ ] Mockito не умеет бросать checked exceptions | Mockito бросает любые `Throwable`, но для non-void метод должен объявлять checked exception в `throws`, иначе `MockitoException`. ❌ ПОСЛЕДСТВИЕ: попытка `thenThrow(SQLException.class)` на методе без `throws SQLException` — `Checked exception is invalid for this method`, билд красный.

## Q12. Что такое `thenAnswer()` и когда его применять?

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
> - [ ] Альтернативный синтаксис `thenReturn` без отличий | `thenAnswer` принимает `Answer<T>` с доступом к `InvocationOnMock` — динамический ответ, основанный на аргументах. ❌ ПОСЛЕДСТВИЕ: использование `thenAnswer` там, где достаточно `thenReturn`, перегружает тест и скрывает простое поведение.
> - [x] `thenAnswer(Answer<T>)` даёт callback с доступом к `invocation.getArgument(i)` для динамического ответа на основе входов | Применяется когда возврат зависит от аргумента, или нужно вызвать переданный callback (`Consumer`, `Function`). ✓ ПРИМЕНЯТЬ: тесты Spring `JdbcTemplate.queryForObject(sql, RowMapper)` — `thenAnswer` извлекает `RowMapper` и применяет к фейковому `ResultSet`. 📋 ПРАВИЛО: «thenAnswer — динамический ответ от аргументов». 🔗 См. Q9, Q40, Q38.
> - [ ] `thenAnswer` блокирует вызывающий поток до получения данных | `thenAnswer` синхронен — выполняется в потоке вызова мока, никакого ожидания. ❌ ПОСЛЕДСТВИЕ: ожидание async-семантики приводит к попытке настроить блокировку через `thenAnswer`, реально ничего не блокируется и race-condition не воспроизводится.
> - [ ] `thenAnswer` обязателен для всех void-методов | Для void используется `doAnswer().when(mock).method()`; `thenAnswer` идёт после `when()` для non-void. ❌ ПОСЛЕДСТВИЕ: попытка `when(voidMethod()).thenAnswer(...)` — компилятор не пропустит, час дебага синтаксиса.

## Q13. Как заглушить `void`-метод с помощью `doNothing()`?

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
> - [ ] `when(voidMethod()).thenReturn(null)` | `void`-метод нельзя обернуть в `when()` — компилятор не примет, нужно `doNothing()/doThrow()/doAnswer()`. ❌ ПОСЛЕДСТВИЕ: попытка стабить `void` через `when()` — `compilation error`, час потерян на разбор «почему не работает как с non-void».
> - [x] `doNothing().when(mock).voidMethod()` — на mock void и так ничего не делает, но на spy `doNothing` нужен чтобы перекрыть реальный метод | Для mock-объектов void-методы дефолтно no-op; для spy без `doNothing` будет вызван реальный метод. ✓ ПРИМЕНЯТЬ: тесты `EmailService` spy с реальным шаблонизатором, но `doNothing().when(spy).send(...)` чтобы не отправлять реальное письмо. 📋 ПРАВИЛО: «doNothing нужен только для @Spy void». 🔗 См. Q11, Q31, Q42.
> - [ ] `doNothing()` отключает все методы мока | `doNothing` стабит только конкретный void-метод, остальные продолжают работать. ❌ ПОСЛЕДСТВИЕ: ожидание тотального silence от мока ведёт к удивлению почему другие методы возвращают дефолты или выполняют реальную логику spy.
> - [ ] `doNothing()` нельзя комбинировать с `doThrow()` | Можно цепочкой: `doNothing().doThrow(Ex.class).when(mock).method()` — первый вызов no-op, второй бросает. ❌ ПОСЛЕДСТВИЕ: тест retry-on-failure без чередования поведения проверяет только success-path, реальная отказоустойчивость не покрыта.

## Q14. (!) Как проверить вызов метода с помощью `verify()`?

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
> - [ ] `verify(mock).method(any(), "literal")` — смешивая матчеры и литералы | Mockito запрещает смешивать матчеры и литералы — все или ни одного, иначе `InvalidUseOfMatchersException`. ❌ ПОСЛЕДСТВИЕ: тест компилируется, но runtime бросает `InvalidUseOfMatchersException` — флакающая ошибка в CI, локально иногда проходит.
> - [x] `verify(mock).method(args)` (1 раз по умолчанию) или `verify(mock, times(n)/never()/atLeast(n)).method(args)` | Без режима — `times(1)`; матчеры (`any()`, `eq()`) обязательны для всех аргументов либо ни одного. ✓ ПРИМЕНЯТЬ: тесты Stripe webhook handler — `verify(eventBus, times(1)).publish(any(PaymentSucceededEvent.class))` для проверки идемпотентности. 📋 ПРАВИЛО: «verify по умолчанию = times(1), либо все matchers, либо все литералы». 🔗 См. Q15, Q16, Q20.
> - [ ] `verify(mock)` проверяет что мок не использовался | `verify(mock)` без метода — недопустимый синтаксис; для проверки отсутствия вызовов — `verifyNoInteractions(mock)`. ❌ ПОСЛЕДСТВИЕ: попытка использовать «голый» verify даёт `MissingMethodInvocationException`, тест не компилируется.
> - [ ] `verify` обязательно надо вызывать ДО действия с SUT | `verify` после действия — иначе мок ещё не вызван и проверка падает. ❌ ПОСЛЕДСТВИЕ: расположение `verify` перед `service.action()` — тест всегда красный, паттерн AAA нарушен, диагностика занимает день.

## Q15. Какие режимы верификации поддерживает Mockito?

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
> - [ ] Только `times(n)` и `never()` | Mockito поддерживает шесть режимов: `times`, `never`, `atLeastOnce`, `atLeast`, `atMost`, `only`. ❌ ПОСЛЕДСТВИЕ: ограничение арсеналом из двух режимов ведёт к неточным проверкам — например, retry-сценарий без `atLeast(2)` пропускает single-call регрессию.
> - [x] `times(n)`, `never()`, `atLeastOnce()`, `atLeast(n)`, `atMost(n)`, `only()` | `only()` проверяет что вызывался ТОЛЬКО этот метод и ничего другого на моке; `atLeastOnce()` — дефолт verify. ✓ ПРИМЕНЯТЬ: тесты Spring `@Retryable` методов — `verify(client, atLeast(3)).call()` после network-failure для проверки retry-policy. 📋 ПРАВИЛО: «times — точно, atLeast/atMost — диапазон, only — единственный». 🔗 См. Q14, Q16, Q20.
> - [ ] `only()` означает «хотя бы один раз» | `only()` строже: ровно один вызов этого метода и ноль других на этом моке. ❌ ПОСЛЕДСТВИЕ: путаница `only()` с `atLeastOnce()` — добавление логирования в SUT неожиданно ломает тест, диагностика непонятна.
> - [ ] `atMost(0)` — то же самое что `never()` | По логике эквивалентны, но `never()` читаемее и принято в стиле; `atMost(0)` пишут только при формуле от параметра. ❌ ПОСЛЕДСТВИЕ: использование `atMost(0)` в обычных тестах путает ревьюера, замедляет code review и инкрементирует cognitive load.

## Q16. Что делают `verifyNoInteractions()` и `verifyNoMoreInteractions()`?

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
> - [ ] Это синонимы — оба проверяют что мок не использовался | Разные семантики: `verifyNoInteractions` — ноль вызовов вообще, `verifyNoMoreInteractions` — нет невёрифицированных вызовов после явных `verify()`. ❌ ПОСЛЕДСТВИЕ: путаница ведёт к неверной защите — использование `verifyNoMoreInteractions` без предварительных verify проверяет лишь дефолт.
> - [x] `verifyNoInteractions(mock)` — ни одного вызова на моке вообще; `verifyNoMoreInteractions(mock)` — после серии `verify()` нет других невёрифицированных вызовов | Первый пишут в начале теста, второй — в конце для строгой проверки. ✓ ПРИМЕНЯТЬ: тесты idempotency-логики — `verifyNoInteractions(emailService)` при повторной обработке processed-event. 📋 ПРАВИЛО: «NoInteractions — пусто; NoMore — после verify ничего лишнего». 🔗 См. Q14, Q15, Q24.
> - [ ] Их использование всегда полезно для строгих тестов | Злоупотребление `verifyNoMoreInteractions` — антипаттерн: тесты ломаются при добавлении любого нового вызова в SUT, даже логирования. ❌ ПОСЛЕДСТВИЕ: добавление `metrics.increment()` в production-код роняет 50 тестов с `verifyNoMoreInteractions`, разработчик тратит день на правку.
> - [ ] `verifyNoInteractions` нельзя комбинировать с другими проверками | Можно: `verify(mock).method(); verifyNoMoreInteractions(mock)` — стандартная схема для строгих тестов. ❌ ПОСЛЕДСТВИЕ: ограничение себя одним проверочным вызовом на тест — теряется покрытие side-effects, регрессии проходят незамеченными.

## Q17. (!) Как захватить аргумент с помощью `ArgumentCaptor`?

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
> - [ ] `ArgumentCaptor` нужен только когда mock возвращает значение через `thenReturn()` | Captor работает на стороне `verify()` для перехвата входящих аргументов, а не возвращаемых значений. ❌ ПОСЛЕДСТВИЕ: попытка `captor.getValue()` после `when().thenReturn()` без вызова `verify()` — пустой captor и `IndexOutOfBoundsException`.
> - [x] Объявить `@Captor ArgumentCaptor<Order>`, вызвать `verify(repo).save(captor.capture())`, затем `captor.getValue()` для assertions | `capture()` действует как matcher: запоминает аргумент, который потом достаётся через `getValue()`/`getAllValues()` для глубоких assertions. ✓ ПРИМЕНЯТЬ: тесты OrderService в Wolt — захват `Order` перед `save()` для проверки `status=PENDING` и `total`. 📋 ПРАВИЛО: «Captor — verify+capture+getValue». 🔗 См. Q14, Q19, Q39.
> - [ ] `ArgumentCaptor` создаётся через `new ArgumentCaptor<>()` без аннотации | Конструктор недоступен напрямую: используется фабрика `ArgumentCaptor.forClass(Class)` или `@Captor` + `MockitoExtension`. ❌ ПОСЛЕДСТВИЕ: `compilation error` в тесте, разработчик ищет «как создать captor» в Stack Overflow вместо чтения Javadoc.
> - [ ] Captor захватывает аргумент только из последнего вызова — для нескольких нужен `argThat()` | `getAllValues()` возвращает список всех захваченных значений за серию вызовов; `argThat` для другой задачи (предикаты в matcher). ❌ ПОСЛЕДСТВИЕ: проверка только последнего save() из batch-операции — пропускают баг с дубликатами в первых N-1 вызовах.

## Q18. Какие матчеры аргументов предоставляет `ArgumentMatchers`?

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
> - [ ] Достаточно передавать сырые значения: `when(svc.find(1L, "ACTIVE")).thenReturn(list)` без матчеров | Mockito требует, чтобы при использовании ЛЮБОГО матчера ВСЕ аргументы были матчерами — иначе `InvalidUseOfMatchersException`. ❌ ПОСЛЕДСТВИЕ: смешение `eq()` и сырых значений в одном вызове — тест падает на старте, разработчик ищет 30 минут где «потерянный» матчер.
> - [ ] `any()` всегда совпадает только с не-null значениями любого типа | Базовый `any()` (без аргумента) пропускает `null`; для исключения null нужно `any(SomeClass.class)` или `notNull()`. ❌ ПОСЛЕДСТВИЕ: тест зелёный при null-аргументе, в проде `NullPointerException` в SUT — баг ускользает в релиз.
> - [ ] `eq()` сравнивает по ссылке (`==`) | `eq()` использует `equals()`; для ссылочного сравнения существует отдельный `same()`. ❌ ПОСЛЕДСТВИЕ: тест с `eq(new User("Alice"))` не находит match для другого инстанса с теми же полями — false negative в проверке save().
> - [x] `any()`, `anyString()`, `eq(value)`, `argThat(predicate)`, `same(obj)`, `isNull()`/`isNotNull()`, `contains(str)` — для разных стратегий совпадения | Матчеры покрывают: «любое значение», «по equals», «по ссылке», «по предикату», «по null»; смешивать с raw-значениями нельзя. ✓ ПРИМЕНЯТЬ: тесты Spring Data репозиториев — `when(repo.findByStatus(eq("ACTIVE"), anyLong()))` для гибкой настройки stubs. 📋 ПРАВИЛО: «Один матчер — все аргументы матчеры». 🔗 См. Q9, Q19, Q39.

## Q19. Что такое `argThat()` и когда он нужен?

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
> - [ ] `argThat()` заменяет `verify()` — это сокращённая запись проверки вызова | `argThat` — это matcher, передаётся внутрь `verify(mock).method(argThat(...))`, не отменяет сам `verify`. ❌ ПОСЛЕДСТВИЕ: попытка «verify через argThat без verify» — компилируется, но ничего не проверяет, тест silent-pass.
> - [ ] Возврат `false` из лямбды `argThat` валит тест с явным сообщением | `false` означает «не совпало» — Mockito ищет другой stub/verify, может тихо пропустить, особенно с lenient. ❌ ПОСЛЕДСТВИЕ: разработчик пишет `argThat(x -> { /* sysout */ return false; })` для дебага — тест зелёный, проверка не сработала.
> - [ ] `argThat` нельзя использовать в `when().thenReturn()` — только в `verify()` | Можно: `when(repo.find(argThat(id -> id > 0))).thenReturn(...)` — стандартный способ stub для предиката. ❌ ПОСЛЕДСТВИЕ: разработчик дублирует stub-ы под каждый id вместо одного предиката — 30 строк boilerplate в тесте.
> - [x] `argThat(predicate)` задаёт произвольное условие на аргумент через предикат — нужен когда `eq()` недостаточно для глубокой проверки полей | Удобен в `verify()` для inline-проверок без захвата объекта; для сложных assertions предпочесть `ArgumentCaptor`. ✓ ПРИМЕНЯТЬ: тесты EmailService в Booking.com — `verify(svc).send(argThat(e -> e.getTo().contains("@booking.com")))`. 📋 ПРАВИЛО: «argThat — предикат in-place, captor — объект для assertions». 🔗 См. Q17, Q18, Q39.

## Q20. (!) Как проверить порядок вызовов с помощью `InOrder`?

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
> - [ ] Обычный `verify(mock1); verify(mock2)` гарантирует порядок вызовов между разными моками | Без `InOrder` Mockito не проверяет хронологию: `verify` лишь констатирует факт вызова, не порядок. ❌ ПОСЛЕДСТВИЕ: тест зелёный когда `commit()` вызван до `save()` — нарушение транзакционной семантики попадает в прод и портит данные.
> - [ ] `InOrder` работает только для одного mock-объекта — для нескольких нужны отдельные `InOrder` | `inOrder(m1, m2, m3)` принимает несколько моков и проверяет порядок между ними. ❌ ПОСЛЕДСТВИЕ: дубль `InOrder` для каждого мока скрывает межмоковые нарушения порядка — тест проходит с broken sequence.
> - [x] `InOrder inOrder = inOrder(txMgr, repo, audit); inOrder.verify(txMgr).begin(); inOrder.verify(repo).save(...); inOrder.verify(txMgr).commit()` — относительный порядок между указанными моками | `InOrder` верифицирует относительный порядок указанных вызовов, разрешая другие незаявленные вызовы между ними. ✓ ПРИМЕНЯТЬ: тесты Saga-координатора в LinkedIn — порядок `prepare → commit → notify` критичен для consistency. 📋 ПРАВИЛО: «InOrder — относительный порядок указанных шагов». 🔗 См. Q14, Q15, Q21.
> - [ ] `InOrder` падает если между указанными `verify()` есть другие вызовы на тех же моках | Семантика «relative order» — другие вызовы разрешены, проверяется только указанная подпоследовательность. ❌ ПОСЛЕДСТВИЕ: разработчик добавляет `verifyNoMoreInteractions()` думая что InOrder уже это проверил — ложное чувство safety и пропуск audit-вызовов.

## Q21. Что такое BDDMockito и как использовать стиль `given/when/then`?

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
> - [ ] `BDDMockito` — это отдельная библиотека с другой реализацией mock-engine | `BDDMockito` — это статический класс внутри `mockito-core`, всего лишь синтаксический сахар над `when/verify`. ❌ ПОСЛЕДСТВИЕ: разработчик добавляет лишнюю зависимость в `build.gradle`, конфликт версий и `NoSuchMethodError` в CI.
> - [ ] `BDDMockito` обязателен для интеграции с Cucumber и JBehave | Cucumber не требует `BDDMockito` — можно использовать стандартный `when/verify`; BDDMockito лишь улучшает читаемость. ❌ ПОСЛЕДСТВИЕ: переписывание 300 тестов под BDDMockito ради «совместимости» с Cucumber — потерянная неделя без бизнес-эффекта.
> - [ ] `then(mock).should().method()` запускает реальный метод и сохраняет stub | `then().should()` — это только верификация (как `verify`), реальные методы не вызывает. ❌ ПОСЛЕДСТВИЕ: ожидание side-effects от `then().should()` — запись в БД не происходит, тест integration-логики проходит, регрессия в проде.
> - [x] `given(mock.method()).willReturn(...)` ↔ `when().thenReturn()`, `then(mock).should()` ↔ `verify(mock)` — семантический сахар для BDD-стиля Given/When/Then | Полностью эквивалентны по поведению; различаются читаемостью и согласованием с naming Given/When/Then в тесте. ✓ ПРИМЕНЯТЬ: тесты бизнес-сценариев в банковских BDD-сьютах с Cucumber — нативно ложится на feature-файлы. 📋 ПРАВИЛО: «BDD = given/willReturn + then/should». 🔗 См. Q9, Q10, Q14.

## Q22. (!) Что такое `@MockBean` и `@SpyBean` в Spring Boot?

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
> - [ ] `@MockBean` создаёт mock в Spring-контексте без перезагрузки — это lightweight-аннотация | Каждое уникальное сочетание `@MockBean` пересоздаёт `ApplicationContext` (cache-key меняется), значительно замедляя CI. ❌ ПОСЛЕДСТВИЕ: добавление `@MockBean` в каждый из 200 тестов растягивает CI-pipeline с 5 до 30 минут — feedback loop ломается.
> - [ ] `@SpyBean` оборачивает реальный бин но при этом отключает все его реальные методы | `@SpyBean` по умолчанию вызывает РЕАЛЬНЫЕ методы; чтобы заглушить — `doReturn().when(spyBean).method()`. ❌ ПОСЛЕДСТВИЕ: ожидание мокового поведения от `@SpyBean` запускает реальный HTTP-запрос к payment-API из теста — реальные деньги списываются.
> - [x] `@MockBean` заменяет реальный бин в Spring-контексте mock-ом, `@SpyBean` оборачивает реальный бин в spy с возможностью частичного override | Регистрируются Spring TestContextManager-ом до `Autowired`-инжекции; влияют на cache-key контекста. ✓ ПРИМЕНЯТЬ: `@SpringBootTest` в Avito — `@MockBean PaymentGateway` для изоляции от внешнего платёжного API. 📋 ПРАВИЛО: «MockBean — заменяет, SpyBean — оборачивает». 🔗 См. Q3, Q6, Q31.
> - [ ] `@MockBean` и `@SpyBean` работают только в `@WebMvcTest`, в полном `@SpringBootTest` нужно `@Mock` | Обе аннотации работают везде где есть `SpringExtension`/`SpringRunner` — в любом slice или full integration test. ❌ ПОСЛЕДСТВИЕ: использование `@Mock` в `@SpringBootTest` — поле инициализируется, но в контексте остаётся реальный бин, тест проверяет не то что думает.

## Q23. Как работает Mockito с `@ExtendWith(MockitoExtension.class)` в JUnit 5?

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
> - [ ] `MockitoExtension` равен старому `MockitoJUnitRunner` — никаких отличий | `MockitoExtension` использует JUnit 5 ExtensionAPI и поддерживает параметрические моки в методах теста; Runner работал только в JUnit 4. ❌ ПОСЛЕДСТВИЕ: попытка комбинировать `@RunWith(MockitoJUnitRunner)` и `@ExtendWith` — конфликт extension-моделей, тесты не запускаются.
> - [ ] Extension по умолчанию использует `Strictness.LENIENT` | По умолчанию включён `STRICT_STUBS` — неиспользованные stubs валят тест с `UnnecessaryStubbingException`. ❌ ПОСЛЕДСТВИЕ: разработчик не замечает «мёртвый» stub, который стал ненужным после рефакторинга — тесты гниют, покрытие иллюзорно.
> - [ ] Параметрические моки (`@Test void test(@Mock Foo f)`) не поддерживаются | Реализован `ParameterResolver` — моки можно передавать как параметры test/`@BeforeEach` методов. ❌ ПОСЛЕДСТВИЕ: разработчик объявляет 10 полей `@Mock` ради одного теста — class-level state протекает между тестами.
> - [x] Реализует `BeforeEachCallback` (init `@Mock`/`@Spy`/`@Captor`/`@InjectMocks`), `AfterEachCallback` (strict-validation), `ParameterResolver` (моки как параметры) — нативная JUnit 5 интеграция со `STRICT_STUBS` по умолчанию | Заменяет `MockitoAnnotations.openMocks(this)` и валидирует unused stubs автоматически. ✓ ПРИМЕНЯТЬ: все JUnit 5 unit-тесты сервисов в Spring Boot 3.x проектах. 📋 ПРАВИЛО: «MockitoExtension = init + validate + parameter». 🔗 См. Q4, Q8, Q28.

## Q24. Что делает `Mockito.reset()`?

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
> - [ ] `reset()` — рекомендуемый способ изоляции тестов внутри одного класса | Использование `reset()` — антипаттерн: сигнал плохой структуры (mock переиспользуется); правильно создавать новый mock в `@BeforeEach`. ❌ ПОСЛЕДСТВИЕ: `reset` в `@BeforeEach` маскирует утечку state между тестами — flaky CI с разным порядком прогона.
> - [ ] `reset()` сбрасывает только stub-конфигурацию, но сохраняет историю вызовов | Сбрасывает И stubs, И записи вызовов — `verify()` после `reset` уже не видит предыдущие вызовы. ❌ ПОСЛЕДСТВИЕ: `verify()` после `reset` всегда зелёный (никаких вызовов нет), регрессии вызовов уходят в прод.
> - [x] Сбрасывает stubs И верификации mock-а; легитимный нишевый случай — `@MockBean` в дорогом Spring-контексте, иначе создавать новый mock на каждый тест | Использование вне Spring-контекста — антипаттерн, признак переиспользования mock-ов между тестами. ✓ ПРИМЕНЯТЬ: дорогие `@SpringBootTest` где пересоздание контекста замедлит CI на 10× — `reset` в `@BeforeEach` для `@MockBean`. 📋 ПРАВИЛО: «reset — last resort, не привычка». 🔗 См. Q22, Q28, Q32.
> - [ ] `reset()` нужен после каждого `verify()` чтобы освободить память | `verify()` не накапливает память — Mockito хранит invocations в weak-ref-структуре; `reset` к памяти не относится. ❌ ПОСЛЕДСТВИЕ: культ-карго `reset` после verify добавляет 50 строк boilerplate в каждый тест и провоцирует hide bugs.

## Q25. (!) Как мокировать статические методы с помощью `mockStatic()`?

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
> - [ ] `mockStatic(Util.class)` без `try-with-resources` — нормальная практика, mock автоматически снимается после теста | `MockedStatic` thread-local: без явного `close()` остаётся активным для всех последующих тестов в потоке. ❌ ПОСЛЕДСТВИЕ: тест-1 mock-ает `UUID.randomUUID()` без close — тест-42 в том же CI-worker получает фиксированный UUID, дубликаты в БД, flaky CI.
> - [ ] `mockStatic` работает с `mockito-core` без дополнительных зависимостей в Mockito 3.x | Для Mockito 3.x требуется `mockito-inline` (или mock-maker-inline plugin); только в Mockito 5+ inline по умолчанию. ❌ ПОСЛЕДСТВИЕ: `MockitoException: cannot mock static` в CI с Mockito 3.4 — час дебага вместо 5 секунд чтения release notes.
> - [ ] `MockedStatic` распространяется на все потоки JVM сразу после `mockStatic()` | Scope ограничен текущим потоком — другие потоки видят реальный метод; критично для многопоточного кода. ❌ ПОСЛЕДСТВИЕ: тест с `CompletableFuture.runAsync()` в проверяемом коде получает реальный `UUID.randomUUID()` в forkJoin-потоке — flaky, маскирует race-условие.
> - [x] `try (MockedStatic<UUID> m = mockStatic(UUID.class)) { m.when(UUID::randomUUID).thenReturn(fixed); }` — обязательный try-with-resources, mock thread-local | Без auto-close mock «протекает» в другие тесты потока; верификация через `m.verify(UUID::randomUUID)`. ✓ ПРИМЕНЯТЬ: тесты id-генерации в платёжных системах — фиксация `UUID.randomUUID()` для воспроизводимости. 📋 ПРАВИЛО: «mockStatic — только в try-with-resources». 🔗 См. Q26, Q35, Q41.

## Q26. Как мокировать `final`-классы и методы?

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
> - [x] Подключить `mockito-inline` или включить mock-maker-inline через `org.mockito.plugins.MockMaker`; Mockito 5+ — by default | Inline mock maker использует Java Instrumentation API для byte-buddy redefine, поддерживает final-классы и методы. ✓ ПРИМЕНЯТЬ: тесты в Kotlin-проектах (все классы final by default) — обязательный inline mock maker для Mockito до 5. 📋 ПРАВИЛО: «Final → inline mock maker». 🔗 См. Q3, Q35, Q44.
> - [ ] Final-классы мокируются стандартным mock maker без дополнительных настроек | По умолчанию (CGLib/SubclassByteBuddy) final невозможны — нужен mock-maker-inline (через resource-файл или Mockito 5+). ❌ ПОСЛЕДСТВИЕ: `MockitoException: Cannot mock final class String` в CI — спешная замена `String` на `CharSequence` ломает контракт production-кода.
> - [ ] Mock-maker-inline бесплатен и не имеет ограничений | Inline медленнее, конфликтует с Jacoco/coverage-агентами, не мокирует примитивы/enum/private. ❌ ПОСЛЕДСТВИЕ: переход проекта на inline без оценки — CI время растёт на 30%, метрики coverage Jacoco ломаются на random-классах.
> - [ ] Можно мокировать private-методы final-класса через `mockito-inline` | Private методы не мокируются ни в одном режиме — нужен PowerMock либо рефакторинг (вынос в protected/класс). ❌ ПОСЛЕДСТВИЕ: попытка `when(mock.privateMethod())` тихо проходит, но заглушка не работает — реальный private выполняется, тест проверяет не то.

## Q27. Что такое `RETURNS_DEEP_STUBS` и когда его применять?

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
> - [x] `mock(Order.class, RETURNS_DEEP_STUBS)` создаёт авто-stubs для цепочек `a.getB().getC()` без NPE; разумно для fluent-API (builders, AWS SDK), антипаттерн для доменных объектов | Каждый промежуточный объект — отдельный mock; полезно для тестирования fluent-builder сценариев. ✓ ПРИМЕНЯТЬ: тесты конфигурации AWS SDK builders в Lambda — `S3Client.builder().region().endpoint()` без 5 mock-полей. 📋 ПРАВИЛО: «DEEP_STUBS — fluent API only, не доменка». 🔗 См. Q4, Q9, Q34.
> - [ ] `RETURNS_DEEP_STUBS` — best practice для всех моков сложных бизнес-объектов | Это сигнал нарушения Закона Деметры (`a.getB().getC().getD()`); для доменного кода — антипаттерн, нужен рефакторинг. ❌ ПОСЛЕДСТВИЕ: тесты с deep stubs ломаются при любом рефакторинге доменной модели — 100 тестов red после переименования метода в третьей цепочке.
> - [ ] `RETURNS_DEEP_STUBS` тестирует реальные методы цепочки, защищая от регрессий | Ничего не тестирует — только защищает от NPE, возвращая нули/пустые объекты на каждом уровне; реальная логика не проверяется. ❌ ПОСЛЕДСТВИЕ: «зелёный» тест с deep stubs пропускает изменение логики в `getCustomer()` — баг с null-customer уходит в payment-flow.
> - [ ] Deep stubs автоматически верифицируются через `verify(mock).getB().getC()` | Verify не работает на промежуточных вызовах deep-stub цепочки — нужен отдельный mock на каждом уровне или `Captor`. ❌ ПОСЛЕДСТВИЕ: разработчик уверен что verify-цепочка проверяет вызов — на самом деле проверяет ничего, бизнес-flow не покрыт.

## Q28. (!) Что такое strict stubbing и зачем он нужен?

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
> - [ ] Strict stubbing замедляет тесты — рекомендуется отключать через `Strictness.LENIENT` | Strict почти не влияет на производительность, отключение скрывает мёртвые stubs и refactoring rot. ❌ ПОСЛЕДСТВИЕ: глобальный `LENIENT` — 30% «зелёных» stubs становятся неактуальными после рефакторинга, тесты лгут о покрытии.
> - [x] Strict валит тест с `UnnecessaryStubbingException` если stub настроен но не использован, и предупреждает при `StubbingArgumentMismatchException` — выявляет мёртвые stubs и опечатки в аргументах | Включён по умолчанию в `MockitoExtension`; помогает поддерживать чистоту тестов и быстрый рефакторинг. ✓ ПРИМЕНЯТЬ: все unit-тесты в Mockito 3+/JUnit 5 — strict как defense-in-depth против test rot. 📋 ПРАВИЛО: «Strict — мёртвые stubs валят тест немедленно». 🔗 См. Q23, Q29, Q33.
> - [ ] `UnnecessaryStubbingException` бросается во время `when().thenReturn()` если stub дублируется | Бросается в `AfterEachCallback` — Mockito подсчитывает использование stub-а и валит тест ПОСЛЕ выполнения. ❌ ПОСЛЕДСТВИЕ: разработчик ищет ошибку «при настройке stub» вместо чтения report после теста — теряет час на отладку.
> - [ ] Strict stubbing проверяет, что mock покрывает 100% методов класса | Strict проверяет только использованность настроенных stubs, не coverage класса; для coverage есть Jacoco. ❌ ПОСЛЕДСТВИЕ: разработчик настраивает заглушки на ВСЕ методы «чтобы strict не ругался» — boilerplate взрывается, тесты нечитаемы.

## Q29. Как сделать отдельный stub ненастойчивым с помощью `lenient()`?

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
> - [ ] `lenient()` отключает strict stubbing для всего теста | `lenient()` точечный: применяется ТОЛЬКО к одному конкретному stub, остальные остаются strict. ❌ ПОСЛЕДСТВИЕ: разработчик думает что lenient «отключил strict глобально» — другие неиспользованные stubs всё равно валят тест после рефакторинга.
> - [ ] `@MockitoSettings(strictness = LENIENT)` — рекомендуемая альтернатива `lenient()` | Класс-уровневое отключение скрывает мёртвые stubs во ВСЁМ классе теста; точечный `lenient()` предпочтительнее. ❌ ПОСЛЕДСТВИЕ: refactor-rot накапливается — через год тесты содержат 40% неиспользуемых stubs, рефакторинг невозможен без аудита.
> - [x] `lenient().when(mock.method()).thenReturn(value)` — точечно отключает strict-проверку именно этого stub-а; оставляет strict для всех остальных | Применяется в `@BeforeEach` для общих stubs, которые могут не использоваться в части тестов класса. ✓ ПРИМЕНЯТЬ: тесты с feature-toggle — `lenient().when(toggle.isEnabled("X"))` в `@BeforeEach` для тестов, не использующих toggle. 📋 ПРАВИЛО: «lenient — на конкретный stub, не на класс». 🔗 См. Q23, Q28, Q45.
> - [ ] `lenient()` нужен после каждого `when()` для предотвращения exceptions | Большинство stubs должны оставаться strict — `lenient` это исключение, не правило. ❌ ПОСЛЕДСТВИЕ: автоматическая обёртка всех stubs в lenient — потеря защиты от мёртвых заглушек, тесты гниют незаметно.

## Q30. Можно ли вернуть разные значения при последовательных вызовах?

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
> - [x] `thenReturn(a, b, c)` или цепочка `thenReturn(a).thenReturn(b)`; можно чередовать `thenReturn`/`thenThrow`; последний элемент повторяется для всех последующих вызовов | Полезно для тестов retry-логики (1-й вызов throw, 2-й success) и пагинации (page1, page2, empty). ✓ ПРИМЕНЯТЬ: тесты Spring Retry в Yandex Lavka — `thenThrow(IOException).thenReturn(orderResponse)` для проверки backoff. 📋 ПРАВИЛО: «thenReturn — varargs или chain, последний — sticky». 🔗 См. Q9, Q11, Q12.
> - [ ] Можно только перечислить значения через `thenReturn(1).thenReturn(2)` — varargs не поддерживается | Поддерживается varargs: `thenReturn(1, 2, 3)` — эквивалент цепочке. ❌ ПОСЛЕДСТВИЕ: разработчик пишет 20 строк цепочки `thenReturn` вместо одного varargs — код раздут без причины.
> - [ ] Цепочка `thenReturn` бросает исключение если вызовов меньше, чем заданных значений | Использует только нужное количество, остаток — недосягаем; никаких exceptions. ❌ ПОСЛЕДСТВИЕ: разработчик ждёт `IndexOutOfBoundsException` на «лишних» значениях, но получает silent test pass — настройка stub неверна, не замечает.
> - [ ] Цепочка `thenReturn` нельзя комбинировать с `thenThrow` | Можно: `thenReturn(ok).thenThrow(IOException).thenReturn(recovered)` — стандартный pattern для retry-тестов. ❌ ПОСЛЕДСТВИЕ: тестировщик пишет два отдельных stub-а вместо одной цепочки — race-condition в порядке настройки, флакающий тест.

## Q31. Чем отличается `Mockito.spy()` от `@Spy`?

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
> - [ ] `@Spy` всегда работает на immutable-объектах без публичного конструктора | `@Spy` требует публичный no-arg конструктор ИЛИ явную инициализацию полем (`@Spy List<String> l = new ArrayList<>()`). ❌ ПОСЛЕДСТВИЕ: `@Spy` на классе без default-конструктора падает с MockitoException — час дебага вместо чтения Javadoc.
> - [x] Поведение идентично — оба создают spy над реальным объектом; разница в синтаксисе: `Mockito.spy(instance)` программно, `@Spy` декларативно через `MockitoExtension`/`openMocks` | Реальные методы по умолчанию вызываются; для override — `doReturn().when(spy).method()`. ✓ ПРИМЕНЯТЬ: тесты UserService где 90% методов реальные, 10% мокаются — `@Spy UserService svc`. 📋 ПРАВИЛО: «spy() — программно, @Spy — декларативно, поведение one-and-the-same». 🔗 См. Q2, Q6, Q42.
> - [ ] `Mockito.spy(realObject)` не вызывает реальные методы — это полный mock | Вызывает реальные методы по умолчанию (это и есть смысл spy: частичная подмена); `mock()` без `CALLS_REAL_METHODS` — не вызывает. ❌ ПОСЛЕДСТВИЕ: разработчик путает spy и mock — реальные методы запускают side-effects (запись в БД), тест пишет в реальный postgres.
> - [ ] `@Spy` обязательно требует `@InjectMocks` для работы | Не требует: `@Spy` инжектит spy в собственное поле; `@InjectMocks` — отдельная аннотация для тестируемого класса. ❌ ПОСЛЕДСТВИЕ: лишняя `@InjectMocks` оборачивает spy в другую spy-структуру — vary verify-поведение, тесты ломаются.

## Q32. (!) Каковы типичные ошибки при использовании Mockito?

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
> - [ ] `verify(svc).find(anyLong(), "ACTIVE")` — корректный mix матчеров и литералов | `InvalidUseOfMatchersException`: либо все аргументы матчеры, либо никакие. ❌ ПОСЛЕДСТВИЕ: тест падает на старте, разработчик переписывает 50 строк на `eq()` вместо чтения 1 строки в docs.
> - [ ] `when(spyService.voidMethod())` для void-spy — стандартный способ заглушки | Запускает РЕАЛЬНЫЙ метод (side-effect!); правильный способ — `doNothing().when(spy).voidMethod()`. ❌ ПОСЛЕДСТВИЕ: spy на email-сервисе с `when(spy.send())` отправляет реальный email из теста — клиенты получают спам «test message».
> - [ ] Стабинг ПОСЛЕ вызова реального кода работает корректно — Mockito применяет stub задним числом | Mockito stub-ы работают только на ПОСЛЕДУЮЩИЕ вызовы; настройка после execute не влияет на уже произошедший вызов. ❌ ПОСЛЕДСТВИЕ: тест зелёный по причине того что stub не применился, реальная логика вернула null — пропущен баг с null-handling.
> - [x] Mix матчеров+литералов, stub после реального вызова, `when()` на void-spy (запускает реальный метод), мокирование final без `mockito-inline`, забытый `verify` после `when()` | Эти 5 категорий покрывают 90% issues на StackOverflow про Mockito; первые два валят тест с понятным exception, третий — silent corruption. ✓ ПРИМЕНЯТЬ: чек-лист code-review для тестов в Spring Boot проектах. 📋 ПРАВИЛО: «Mockito 5 antipatterns: mix, late-stub, void-spy, final, missing-verify». 🔗 См. Q9, Q10, Q33.

## Q33. Что такое `UnnecessaryStubbingException` и как её избежать?

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
> - [ ] `UnnecessaryStubbingException` бросается на этапе `when()` если stub дублируется | Бросается ПОСЛЕ выполнения теста (`AfterEachCallback`) — strict-валидация неиспользованных stubs. ❌ ПОСЛЕДСТВИЕ: разработчик ищет «дубликат stub в when()» вместо «неиспользованный stub в данном тесте» — теряет 30 минут на неверный диагноз.
> - [ ] Решение — глобально перейти на `@MockitoSettings(LENIENT)` для всего модуля | Это маскирование симптома: мёртвые stubs остаются, тест rot копится; `lenient()` точечный — правильное решение. ❌ ПОСЛЕДСТВИЕ: модуль с 500 тестами и `LENIENT` — через год 40% stubs мёртвые, рефакторинг блокирован страхом сломать «зелёные» тесты.
> - [ ] `UnnecessaryStubbingException` запускается только в JUnit 4 с `MockitoJUnitRunner.Strict.class` | Активна и в JUnit 5 через `MockitoExtension` (по умолчанию STRICT_STUBS), и в JUnit 4 со strict runner. ❌ ПОСЛЕДСТВИЕ: уверенность что миграция на JUnit 5 «отключила strict» — тесты падают неожиданно при добавлении extension.
> - [x] Возникает в strict-режиме когда stub настроен но не вызван — лечится удалением мёртвого stub-а или точечным `lenient().when(...)` для общих stubs в `@BeforeEach` | Сигнализирует test rot после рефакторинга; `@MockitoSettings(LENIENT)` — крайняя мера. ✓ ПРИМЕНЯТЬ: чек-лист после рефакторинга сервиса в Avito — exception показывает мёртвые stubs для удаления. 📋 ПРАВИЛО: «UnnecessaryStubbing — удалить или lenient точечно». 🔗 См. Q23, Q28, Q29.

## Q34. Когда не стоит использовать Mockito?

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
> - [x] Не использовать для value objects, реальной БД (Testcontainers лучше), массового мокирования (5+ зависимостей = SRP violation), interaction-тестов между реальными объектами | Mockito — для изоляции от внешних коллабораторов; для реальной интеграции — Testcontainers/WireMock/H2. ✓ ПРИМЕНЯТЬ: репозитории Spring Data в проектах Сбера — `@DataJpaTest + Testcontainers Postgres` вместо `@MockBean Repository`. 📋 ПРАВИЛО: «Mock — внешние коллабораторы, не value/БД/all». 🔗 См. Q22, Q31, Q32.
> - [ ] Mockito — универсальный инструмент: мокировать всё (включая `String`, `LocalDate`, DTO) — best practice | Мокирование value objects бесполезно (нет поведения) и хрупко; правильно — реальные экземпляры. ❌ ПОСЛЕДСТВИЕ: `mock(LocalDate.class)` ради `now()` ломается после Mockito update — final-методы JDK классов конфликтуют с агентами.
> - [ ] Если нужно замокить 10+ зависимостей — это сигнал использовать `RETURNS_DEEP_STUBS` для всех | Это сигнал нарушения SRP: класс делает слишком много; решение — рефакторинг, а не deep stubs. ❌ ПОСЛЕДСТВИЕ: класс на 1500 строк с 12 моками в тесте — любое изменение ломает 50 тестов, рефакторинг невозможен.
> - [ ] Testcontainers — медленная альтернатива Mockito; всегда предпочитать mock | Testcontainers медленнее (5-10 секунд старт), но даёт реальный postgres/redis вместо моков; критичен для интеграции. ❌ ПОСЛЕДСТВИЕ: тесты репозиториев на моках пропускают баг в SQL-запросе с native query — баг доходит до прода и падает на специфичных данных.

## Q35. Как мокировать конструкторы с помощью `mockConstruction()`?

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
> - [x] `try (MockedConstruction<HttpClient> mc = mockConstruction(HttpClient.class, (mock, ctx) -> {...})) { /* new HttpClient() возвращает mock */ }` — перехват `new` в scope блока | Использование — сигнал отсутствия DI: класс создаёт зависимости напрямую через `new`. ✓ ПРИМЕНЯТЬ: тесты legacy-кода без DI (Apache Commons HttpClient в утилите) — `mockConstruction` как переходный мост к рефакторингу. 📋 ПРАВИЛО: «mockConstruction — try-with-resources + сигнал к рефакторингу на DI». 🔗 См. Q25, Q34, Q41.
> - [ ] `mockConstruction()` глобально перехватывает `new` для класса до `Mockito.reset()` | Scope ограничен `try-with-resources` блоком; вне блока конструктор работает нормально. ❌ ПОСЛЕДСТВИЕ: без try-with-resources — все последующие тесты в потоке создают mock вместо реального HttpClient — сетевые запросы заглушены везде.
> - [ ] `mockConstruction()` доступен с Mockito 1.x — стандартная фича | Появился с Mockito 3.5; использует тот же inline mock maker что и `mockStatic`. ❌ ПОСЛЕДСТВИЕ: попытка использовать в Mockito 3.4 — `MockitoException`, час дебага зависимостей.
> - [ ] Возвращаемое `MockedConstruction.constructed()` хранит ссылки на ВСЕ инстансы класса в JVM | Хранит только инстансы созданные ВНУТРИ try-блока; вне блока — нормальные объекты. ❌ ПОСЛЕДСТВИЕ: разработчик уверен что видит «все HttpClient в JVM» — пропускает инстансы созданные до try-блока, неполная проверка.

## Q36. (!) В чём разница между `mockito-core` и `mockito-inline`?

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
> - [x] До Mockito 5: `core` — обычные классы (subclass mock maker), `inline` — final/static/конструкторы (Java Agent + bytecode); с Mockito 5.0 inline стал дефолтом в `core`, `inline` deprecated с 5.3 | Inline медленнее на ~10-20%, может конфликтовать с Jacoco/JRebel. ✓ ПРИМЕНЯТЬ: апгрейд Spring Boot 3.x проектов на Mockito 5+ — удалить `mockito-inline` из `build.gradle`. 📋 ПРАВИЛО: «Mockito 5+ — только core, inline уходит в прошлое». 🔗 См. Q3, Q25, Q26.
> - [ ] В Mockito 5+ нужно отдельно подключать `mockito-inline` для `mockStatic`/`mockConstruction` | С Mockito 5.0 inline mock maker — дефолт в `mockito-core`; артефакт `mockito-inline` deprecated с 5.3. ❌ ПОСЛЕДСТВИЕ: добавление лишней зависимости `mockito-inline` 5.x в Maven — конфликт версий, `NoClassDefFoundError` в CI.
> - [ ] `mockito-core` без inline может мокировать `private`-методы | Никакой mock maker не мокирует private — это техническое ограничение JVM bytecode access; нужен PowerMock или рефакторинг. ❌ ПОСЛЕДСТВИЕ: команда переходит на `mockito-inline` ради «private методов» — оказывается impossible, потерянный спринт.
> - [ ] Inline mock maker не имеет недостатков — всегда лучше subclass | Inline медленнее, конфликтует с byte-code agents (Jacoco, JRebel, NewRelic), требует JDK с Instrumentation API. ❌ ПОСЛЕДСТВИЕ: проект с NewRelic agent + inline — random-падения тестов в CI с `IllegalStateException`, расследование 2 дня.

## Q37. Что такое `MockSettings` и какие опции он предоставляет?

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
> - [ ] `MockSettings` обязателен для каждого `mock()` — иначе мок неполноценный | Большинство моков создаются без `MockSettings`; настройки нужны только в продвинутых сценариях (serializable, extraInterfaces, verboseLogging). ❌ ПОСЛЕДСТВИЕ: обязательный `withSettings()` в каждом тесте — boilerplate взрывается, никакой пользы.
> - [x] `mock(Class, withSettings().name("..").defaultAnswer(..).serializable().extraInterfaces(..).verboseLogging().stubOnly().strictness(..))` — точечная настройка для отладки и legacy-сценариев | `stubOnly()` для производительности (не хранит invocations), `serializable()` для Spark/HttpSession, `verboseLogging` для дебага. ✓ ПРИМЕНЯТЬ: тесты Spark-jobs в Booking.com — `mock(Config.class, withSettings().serializable())` для broadcast variables. 📋 ПРАВИЛО: «MockSettings — для serializable/extra/verbose, не для each test». 🔗 См. Q3, Q38, Q44.
> - [ ] `stubOnly()` ускоряет тесты в 100×, нужно использовать всегда | Ускорение незначительное, но `stubOnly()` ОТКЛЮЧАЕТ `verify()` — теряется проверка взаимодействий. ❌ ПОСЛЕДСТВИЕ: глобальный `stubOnly()` — все `verify()` тихо падают/не работают, регрессии вызовов в проде.
> - [ ] `withSettings().extraInterfaces()` позволяет мокать private-методы | `extraInterfaces` добавляет реализуемые мок-интерфейсы; private-методы не мокируются никогда. ❌ ПОСЛЕДСТВИЕ: попытка `withSettings().extraInterfaces(SomePrivate)` — компиляция падает или silent ignore, разработчик теряет день.

## Q38. Какие встроенные `Answer` доступны в Mockito?

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
> - [ ] `RETURNS_DEFAULTS` бросает `NullPointerException` на любых не-застабленных вызовах | Возвращает безопасные дефолты: `null`/`0`/`false`/empty collections/`Optional.empty()` — никаких NPE. ❌ ПОСЛЕДСТВИЕ: уверенность что mock «защищает» от NPE автоматически — реальный код использует null от мока и падает в SUT, тест мисдиагностирован.
> - [x] `RETURNS_DEFAULTS` (дефолт), `RETURNS_SMART_NULLS` (SmartNull с указанием места), `RETURNS_DEEP_STUBS` (цепочки), `RETURNS_MOCKS` (моки вместо null), `CALLS_REAL_METHODS` (для default-методов интерфейсов), `RETURNS_SELF` (fluent builders) | Каждый Answer покрывает конкретный паттерн; на практике 80% — DEFAULTS, 15% — SMART_NULLS, 5% — DEEP_STUBS/SELF. ✓ ПРИМЕНЯТЬ: `RETURNS_SELF` для тестов AWS SDK builders в Lambda — `S3Client.builder().region().endpoint()`. 📋 ПРАВИЛО: «Answer — стратегия default-возврата». 🔗 См. Q12, Q27, Q42.
> - [ ] `RETURNS_SMART_NULLS` всегда лучше DEFAULTS — нужно использовать везде | SMART_NULLS даёт лучшие сообщения об ошибках, но добавляет overhead; используется выборочно для дебага flaky-тестов. ❌ ПОСЛЕДСТВИЕ: глобальный SMART_NULLS на проекте — CI замедляется на 15%, без явной пользы для большинства тестов.
> - [ ] `CALLS_REAL_METHODS` — синоним `Mockito.spy()`, разницы нет | Schemа похожа, но `mock(C, CALLS_REAL_METHODS)` не имеет реального state объекта; `spy(instance)` — оборачивает реальный экземпляр со state. ❌ ПОСЛЕДСТВИЕ: путаница ведёт к `mock(MyClass, CALLS_REAL_METHODS)` для класса с обязательным конструктором — `NullPointerException` на полях, тест падает на init.

## Q39. (!) Когда выбирать `ArgumentCaptor`, а когда `argThat()`?

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
> - [ ] `ArgumentCaptor` — единственный способ проверить аргументы; `argThat()` устарел | Оба активны: captor для глубоких assertions после verify, argThat для inline-предикатов в when()/verify(). ❌ ПОСЛЕДСТВИЕ: разработчик не знает про argThat, пишет captor для каждой проверки в `when()` — boilerplate растёт, тесты нечитаемы.
> - [x] `argThat()` — для стаббинга `when()` и простых самодокументируемых предикатов в verify; `ArgumentCaptor` — для verify с глубокими assertions через AssertJ или работы с множественными вызовами через `getAllValues()` | Captor в `when()` — антипаттерн (захватчик не вызывается до факта вызова); рекомендация авторов Mockito. ✓ ПРИМЕНЯТЬ: тесты OrderService — captor для проверки `Order.status` после save(); argThat для stub `findActive(argThat(f -> f.priority > 0))`. 📋 ПРАВИЛО: «Captor — глубокий assert, argThat — простой предикат». 🔗 См. Q17, Q18, Q19.
> - [ ] `ArgumentCaptor` можно использовать в `when()` для условного стаббинга | Captor захватывает аргумент только во время `verify()`; в `when()` не функционирует как matcher. ❌ ПОСЛЕДСТВИЕ: stub с captor.capture() в `when()` не срабатывает — реальный метод запускается или mock возвращает default, тест проверяет не то.
> - [ ] `argThat` даёт детальные сообщения об ошибках с diff'ом по полям | Сообщения от argThat — generic «Argument(s) are different»; для diff нужен Captor + AssertJ. ❌ ПОСЛЕДСТВИЕ: тест падает с "Argument(s) are different", разработчик не знает какое поле — час на ручное логирование вместо использования Captor.

## Q40. Как тестировать код с callback-функциями через `doAnswer()`?

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
> - [ ] Для async-кода с callback нужен `Thread.sleep(1000)` чтобы дождаться вызова | `sleep` — flaky pattern; правильно — `doAnswer` для синхронного вызова callback или `Awaitility.await()` для реальной асинхронности. ❌ ПОСЛЕДСТВИЕ: `Thread.sleep(1000)` в тесте — CI замедляется на минуты, при медленном runner-е тест зелёный, в норме — красный, flaky.
> - [ ] `doAnswer` нельзя использовать для void-методов — только `thenAnswer` | `thenAnswer` НЕ работает с void; правильно — `doAnswer().when()` для void и spy-методов. ❌ ПОСЛЕДСТВИЕ: попытка `when(spy.voidMethod()).thenAnswer(...)` — реальный метод запускается, side-effect неконтролируем.
> - [ ] `invocation.getArgument()` возвращает только String — для callbacks нужен cast | Возвращает generic тип `<T>`, IDE/javac выводит тип; cast не нужен в большинстве случаев. ❌ ПОСЛЕДСТВИЕ: ручной cast `(Consumer)inv.getArgument(1)` — `unchecked warning` спам в коде, плохой стиль.
> - [x] `doAnswer(inv -> { Consumer<String> cb = inv.getArgument(1); cb.accept("result"); return null; }).when(svc).fetchAsync(eq("q"), any())` — синхронный вызов callback из теста; альтернатива — `ArgumentCaptor<Consumer>` с явным контролем момента | Без sleep, детерминированно; для реальной асинхронности — Awaitility. ✓ ПРИМЕНЯТЬ: тесты EventBus в Yandex Lavka — `doAnswer` для эмуляции webhook-callbacks без Thread.sleep. 📋 ПРАВИЛО: «Async callback — doAnswer или Captor, никогда sleep». 🔗 См. Q12, Q17, Q41.

## Q41. (!) В чём ограничение области видимости `MockedStatic` по потокам?

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
> - [ ] `MockedStatic` глобален в JVM — мок виден всем потокам | Scope thread-local: другие потоки видят реальный метод; при `@Execution(CONCURRENT)` параллельные тесты конфликтуют. ❌ ПОСЛЕДСТВИЕ: тест мокает `Clock.systemUTC()` — async-код в `CompletableFuture.supplyAsync()` получает реальное время, тест зелёный, баг в проде с timezone.
> - [x] Thread-local: mock работает только в потоке где создан; `CompletableFuture.supplyAsync()`, новые `Thread`, thread-pool в SUT — не видят mock; параллельные тесты с тем же классом → `MockitoException: already registered` | Дизайн-ограничение, гарантирует non-leak между тестами; для async-кода рефакторить на DI-инжекцию (`Clock` бин) или Awaitility. ✓ ПРИМЕНЯТЬ: рефакторинг legacy с `System.currentTimeMillis()` — заменить на `@Bean Clock` инжекцию вместо `mockStatic`. 📋 ПРАВИЛО: «MockedStatic — thread-local, async-код требует DI». 🔗 См. Q25, Q26, Q35.
> - [ ] Решение проблемы межпоточного скоупа — глобальный `Mockito.mockStaticGlobal()` | Такого API не существует; thread-local — это by design Mockito, не bug. ❌ ПОСЛЕДСТВИЕ: разработчик пишет SO-вопрос про «как сделать static mock global» — теряет неделю до осознания что нужна DI-абстракция.
> - [ ] Параллельные тесты JUnit 5 (`@Execution(CONCURRENT)`) могут безопасно мокать один static-класс | Получают `MockitoException: For XXX static mocking is already registered`; нужно `SAME_THREAD` исполнение или уникальные классы. ❌ ПОСЛЕДСТВИЕ: разработчик включает CONCURRENT для ускорения CI — half of static-mocking tests random-fail, диагностика 2 дня.

## Q42. Что делает `thenCallRealMethod()` и чем отличается от `@Spy`?

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
> - [x] `thenCallRealMethod()` на mock запускает реальную логику метода без инициализированных полей (NPE-риск); `@Spy` оборачивает РЕАЛЬНЫЙ инстанс с полями — почти всегда выбирать spy для partial mock | `thenCallRealMethod` подходит только для stateless default-методов интерфейсов или абстрактных классов без зависимостей. ✓ ПРИМЕНЯТЬ: тесты `default`-методов Spring Data Repository интерфейсов — `thenCallRealMethod` без spy. 📋 ПРАВИЛО: «Partial mock — @Spy; default interface — thenCallRealMethod». 🔗 См. Q2, Q6, Q31.
> - [ ] `thenCallRealMethod()` идентичен `@Spy` — выбирайте по личным предпочтениям | Существенно отличаются: spy создаёт ПОЛНОЦЕННЫЙ объект (поля инициализированы), `thenCallRealMethod` на mock — bare-bones shell (поля = null). ❌ ПОСЛЕДСТВИЕ: `thenCallRealMethod` для метода с `this.repository.find()` — NPE на null-репозитории, час дебага вместо использования @Spy.
> - [ ] `thenCallRealMethod()` инициализирует поля mock через рефлексию | Не инициализирует — поля остаются null/default, в отличие от spy с реальным инстансом. ❌ ПОСЛЕДСТВИЕ: ожидание что поля заполнятся — NPE на любом обращении `this.field.method()`, тест валится без понимания причины.
> - [ ] `@Spy` нельзя использовать с `thenCallRealMethod()` — это конфликт | По умолчанию spy уже вызывает реальные методы; `thenCallRealMethod` на spy легитимен (re-enable real после `doReturn`). ❌ ПОСЛЕДСТВИЕ: разработчик дублирует spy-настройки `doReturn` и `thenCallRealMethod` — лишний код без пользы.

## Q43. Как инжектировать `@Mock` и `@Captor` в параметры метода JUnit 5?

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
> - [ ] `@Mock` в параметрах метода поддерживается только в JUnit 4 с MockitoJUnitRunner | Реализован `ParameterResolver` в `MockitoExtension` — параметрические моки доступны в JUnit 5. ❌ ПОСЛЕДСТВИЕ: разработчик не использует параметрические моки в JUnit 5 проекте, объявляет 30 полей `@Mock` ради одного теста — class-level state протекает.
> - [x] `@Test void shouldProcess(@Mock Gateway g, @Captor ArgumentCaptor<Order> c)` — `MockitoExtension`'s `ParameterResolver` инжектирует моки в параметры, изолируя их per-test; работает и с конструктором тестового класса | Уменьшает class-level state, явно описывает зависимости каждого теста. ✓ ПРИМЕНЯТЬ: тесты OrderService с разными моками для разных сценариев — параметры вместо 10 полей. 📋 ПРАВИЛО: «Параметры — изоляция моков per-test». 🔗 См. Q4, Q7, Q23.
> - [ ] Параметрические моки нельзя использовать с `@InjectMocks` | Верно само по себе (`@InjectMocks` работает только с полями), но это ограничение, а не невозможность параметрических моков в принципе. ❌ ПОСЛЕДСТВИЕ: разработчик отказывается от параметрических моков из-за InjectMocks — теряет benefit per-test изоляции.
> - [ ] Конструктор тестового класса не поддерживает `@Mock`-параметры | Поддерживается: `Test(@Mock UserRepository repo) { this.svc = new UserService(repo); }` — final-поля и DI в тесте. ❌ ПОСЛЕДСТВИЕ: разработчик пишет mutable-поле + `@BeforeEach` ради инжекции — лишние строки и риск forget-init.

## Q44. (!) Какие особенности у Mockito в Kotlin и зачем `mockito-kotlin`?

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
> - [x] `mockito-kotlin` решает 4 Java-Mockito issues в Kotlin: `whenever` (вместо `when`-keyword), `any<T>()` поддерживает non-null, reified `mock<T>()`, DSL `mock { on { } doReturn ... }`; альтернатива — MockK (coroutine/suspend support) | Для проектов с suspend-функциями MockK чаще удобнее; mockito-kotlin для смешанных Java+Kotlin проектов. ✓ ПРИМЕНЯТЬ: тесты Kotlin Spring сервисов в Avito — `mockito-kotlin` для совместимости с существующими Java-тестами. 📋 ПРАВИЛО: «Kotlin → mockito-kotlin или MockK, не raw Mockito». 🔗 См. Q3, Q9, Q26.
> - [ ] Mockito работает в Kotlin без специфики — синтаксис идентичен Java | Минимум 4 проблемы: `when` — Kotlin keyword (нужно `\`when\``), all classes final (нужен inline до Mockito 5), `any()` возвращает null (ломает non-null), нет reified. ❌ ПОСЛЕДСТВИЕ: попытка `when(...)` в Kotlin — compilation error; разработчик ищет 30 минут вместо backtick-эскейпа.
> - [ ] `any()` в Kotlin безопасен для non-null типов — Mockito делает auto-cast | `any()` Java-Mockito возвращает null → NPE на non-null Kotlin-параметрах; нужен `mockito-kotlin`'s `any<T>()` или `argThat`. ❌ ПОСЛЕДСТВИЕ: тест с `any()` в Kotlin падает с NPE при инициализации stub — час дебага вместо чтения migration guide.
> - [ ] MockK и mockito-kotlin — одна и та же библиотека под разными именами | Это РАЗНЫЕ библиотеки: MockK — нативная Kotlin (suspend, coroutines, relaxed); mockito-kotlin — обёртка над Java Mockito. ❌ ПОСЛЕДСТВИЕ: добавление обоих в `build.gradle` — конфликт API, `NoSuchMethodError` в CI.

## Q45. Что такое `@MockitoSettings` и как управлять strictness на уровне класса?

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

> [!mcq]
> - [ ] `@MockitoSettings(strictness = LENIENT)` — рекомендованная настройка для большинства тестов | Глобальный LENIENT маскирует мёртвые stubs во всём классе; точечный `lenient()` в `@BeforeEach` предпочтительнее. ❌ ПОСЛЕДСТВИЕ: legacy-модуль с `LENIENT` накапливает 40% неактуальных stubs за год — рефакторинг невозможен без тотального аудита тестов.
> - [ ] `@MockitoSettings` влияет только на новые моки, созданные после аннотации | Применяется ко ВСЕМ мокам в пределах класса теста (через `MockitoExtension`); ретроактивно не работает только в смысле «after creation» — настройка считывается при инициализации. ❌ ПОСЛЕДСТВИЕ: разработчик ставит аннотацию посреди класса для «конкретных тестов» — настройка не работает на отдельные методы, нужен `lenient()` точечный.
> - [x] Класс-уровневая настройка `MockitoExtension`: `STRICT_STUBS` (дефолт), `LENIENT` (отключает strict), `WARN` (legacy Mockito 1.x); предпочитать точечный `lenient()` для отдельных stubs вместо `LENIENT` на класс | `@MockitoSettings(LENIENT)` оправдан только для legacy-миграции с Mockito 1; в новых тестах — STRICT_STUBS + точечные `lenient()`. ✓ ПРИМЕНЯТЬ: миграция legacy-модулей Сбера с Mockito 1 на 5 — временный `@MockitoSettings(LENIENT)` для постепенного аудита. 📋 ПРАВИЛО: «Strictness: класс — STRICT, исключения — точечный lenient». 🔗 См. Q23, Q28, Q29.
> - [ ] `Strictness.WARN` бросает exception при обнаружении мёртвых stubs | `WARN` только пишет предупреждение в лог — тест проходит; `STRICT_STUBS` бросает `UnnecessaryStubbingException`. ❌ ПОСЛЕДСТВИЕ: команда уверена что `WARN` защищает от мёртвых stubs — log-сообщения теряются в CI output, tests rot накапливается.

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
