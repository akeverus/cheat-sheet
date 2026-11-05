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

Это **роли тестовых двойников**, а не четыре несовместимых Java-типа. В этой шпаргалке spy означает частичную подмену через API Mockito; в общей терминологии Test Spy может также означать двойник, который лишь записывает сведения о вызовах.

| Роль | Зачем используется | Пример |
|---|---|---|
| **Mock** | Проверить требуемые взаимодействия: аргументы, число или порядок вызовов | `verify(repo).save(entity)` |
| **Stub** | Предоставить управляемый ответ для сценария | `when(repo.find(1)).thenReturn(entity)` |
| **Spy в Mockito** | Сохранить нужную реальную логику и подменить выбранное поведение | `Mockito.spy(new MyService())` |
| **Fake** | Дать упрощённую рабочую реализацию нужного контракта | Репозиторий на Map для тестового сценария |

Stub отвечает за данные, которые получает SUT; mock-роль — за проверку его взаимодействий. Проверку результата или состояния SUT и проверку вызовов можно сочетать. В обычном Mockito один объект, созданный `mock(...)`, позволяет и настроить ответы через `when(...)`, и проверить историю через `verify(...)`: отдельный класс «stub» для этого не требуется. Название фабричного метода не определяет, какую роль использует конкретный тест.

Spy существующего объекта в стандартной конфигурации копирует состояние вместо обязательного делегирования оригиналу. Поля-ссылки могут остаться общими, а отдельные значения полей — разойтись (Q6/Q31). Поэтому «обёртка над живым объектом» — ненадёжная модель его состояния.

Fake действительно выполняет упрощённую логику, но его возможности должны соответствовать проверяемому сценарию: Map-репозиторий не обещает транзакции, SQL-ограничения и изоляцию production-БД. Хранение в памяти — распространённый пример, а не обязательное определение fake. Выбор техники создания сам по себе не устанавливает роль двойника.

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

`@Mock` и `@Spy` задают разные **ответы по умолчанию**. В обычной конфигурации mock возвращает стандартные значения, а spy вызывает доступную реальную реализацию незаглушённого метода. Это не деление на «пустышку без Java-объекта» и «исходный объект без изменений».

| Аспект | Обычный `@Mock` | `@Spy` |
|---|---|---|
| Незаглушённый метод | Стандартный ответ: например, 0, null или пустое значение | Доступная реальная реализация; для абстрактного метода её нет |
| Подмена ответа | Например, `when(...).thenReturn(...)` | Семейство `doReturn`/`doThrow` позволяет избежать реального вызова при настройке |
| Область применения | Изоляция поведения зависимости | Частичная подмена с сохранением нужной реальной логики |

Полный пример для Mockito 5.11 и JUnit Jupiter с обработкой аннотаций:

```java
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class MockAndSpyExampleTest {
    static class Counter {
        int value = 1;
        int next() { return ++value; }
    }

    @Mock Counter mocked;
    @Spy Counter spied = new Counter();

    @Test
    void defaultAnswersAndSelectedStub() {
        assertEquals(0, mocked.next());
        assertEquals(2, spied.next());
        verify(spied).next();

        doReturn(99).when(spied).next();
        assertEquals(99, spied.next());
        assertEquals(2, spied.value);  // Stub не выполнил ++value.
    }

    @Test
    void factorySpyHasIndependentPrimitiveField() {
        Counter original = new Counter();
        Counter copy = spy(original);
        assertNotSame(original, copy);
        assertEquals(2, copy.next());
        assertEquals(1, original.value);
        original.value = 10;
        assertEquals(2, copy.value);
    }
}
```

Для существующего экземпляра стандартный spy копирует состояние вместо обязательного делегирования каждому вызову на `original`. Копирование поверхностное: отдельные поля, например `int`, независимы, но ссылочные поля могут указывать на один изменяемый объект. Поэтому нельзя считать spy и исходный объект ни одной ссылкой, ни полностью независимыми глубокими копиями.

При `when(spy.method()).thenReturn(...)` Java сначала вычисляет аргумент `when`: незаглушённый конкретный метод spy может реально выполниться и выбросить исключение или изменить состояние. `doReturn(...).when(spy).method()` этого вызова при настройке не требует. Это рекомендация для контроля побочных эффектов, а не запрет любого `when` на spy (Q10).

Граница зависит от настройки: обычному mock можно явно назначить вызов реального метода через `thenCallRealMethod`/`doCallRealMethod` или другой ответ по умолчанию (Q42). Для spy абстрактный метод не получает вымышленную реализацию — возможен стандартный ответ. Создание через готовый объект, тип или обработанное поле описано в Q31; требование «spy всегда оборачивает ранее созданный экземпляр» неверно.

---

## Q7. Что такое `@Captor` и для чего используется?

`@Captor` обозначает поле ArgumentCaptor и избавляет от части ручных generic-casts. Поле нужно инициализировать обработкой аннотаций — например, MockitoExtension или openMocks; сама Java-аннотация объект не создаёт.

Пример с generic-декларацией и настоящим вызовом зависимости:

```java
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GenericCaptorExampleTest {
    record Event(String name) {}
    interface Bus { void publish(List<Event> events); }
    static class Registration {
        private final Bus bus;
        Registration(Bus bus) { this.bus = bus; }
        void register(String name) { bus.publish(List.of(new Event(name))); }
    }
    @Mock Bus bus;
    @Captor ArgumentCaptor<List<Event>> events;

    @Test
    void inspectsPublishedBatch() {
        new Registration(bus).register("Alice");
        verify(bus).publish(events.capture());
        assertEquals("Alice", events.getValue().get(0).name());
    }

    @Test
    void createsTypedCaptorManually() {
        ArgumentCaptor<List<Event>> manual = ArgumentCaptor.captor();
        bus.publish(List.of(new Event("Bob")));
        verify(bus).publish(manual.capture());
        assertEquals("Bob", manual.getValue().get(0).name());
    }
}
```

В **Mockito 5.11.0** доступны явный forClass и type-inferred captor(). Удобная декларация `ArgumentCaptor<List<Event>>` не отменяет стирание типов: обработчик вложенного generic получает raw List и не проверяет глубоко тип каждого элемента. Runtime-проверку контейнера и assertions над содержимым не следует смешивать.

Captor не является mock захваченного объекта, не задаёт default Answer и сам не доказывает нужный вызов. Обычно его используют при verify, затем отдельно проверяют getValue/getAllValues. Он сохраняет ссылки, а не глубокие снимки (Q17/Q39). В параметрах Jupiter Captor также поддерживается исследованной версией при зарегистрированной интеграции — см. Q43.

---


## Q8. (!) Чем отличается `MockitoExtension` от `MockitoAnnotations.openMocks()`?

MockitoExtension интегрирует Mockito с **JUnit Jupiter**: обрабатывает полевые аннотации, разрешает поддерживаемые параметры, начинает/завершает MockitoSession. В исследованной **5.11.0** default strictness — STRICT_STUBS, если настройки не переопределены.

MockitoAnnotations.openMocks(this) программно обрабатывает поля и возвращает **AutoCloseable**. Его можно использовать и в Jupiter, не только в JUnit 4. Но момент инициализации и закрытие ресурса выбирает ваш код; сам вызов не планирует teardown и не создаёт эквивалент строгой сессии расширения.

Полный пример ручного lifecycle без MockitoExtension:

```java
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ManualAnnotationsTest {
    interface Mailer { boolean send(String recipient); }
    @Mock Mailer mailer;
    @Captor ArgumentCaptor<String> recipient;
    private AutoCloseable resources;

    @BeforeEach
    void initialize() {
        resources = MockitoAnnotations.openMocks(this);
    }
    @AfterEach
    void release() throws Exception {
        if (resources != null) resources.close();
    }
    @Test
    void sendsToExpectedRecipient() {
        when(mailer.send("alice@example.com")).thenReturn(true);
        assertTrue(mailer.send("alice@example.com"));
        verify(mailer).send(recipient.capture());
        assertEquals("alice@example.com", recipient.getValue());
    }
}
```

Расширение обычно сокращает ручной lifecycle-код. OpenMocks полезен, когда нужен собственный момент обработки полей; наличие чужого базового класса само по себе не запрещает Jupiter extensions. Не выполняйте оба способа инициализации одних полей без цели: можно получить новые объекты вместо уже переданных в SUT.

Close ресурса, strictness, очистка истории и повторное создание SUT — разные действия. Закрытие не обещает reset всех сохранённых Java-объектов или отсутствие разделяемого состояния. Для параметров метода нужен ParameterResolver расширения, а не openMocks над this (Q23/Q43).

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

`ArgumentCaptor` сохраняет аргументы совпавших вызовов, чтобы тест выполнил assertions над ними. Сам captor не сравнивает бизнес-поля с ожидаемыми значениями и не делает глубокий снимок объекта.

В обычной verification-последовательности сначала вызывают SUT, затем `verify(...).method(captor.capture())`, после чего читают `getValue()` или `getAllValues()`. Capture регистрирует специальный matcher; значение появляется после состоявшегося захвата. GetValue без захватов сообщает об отсутствии аргумента.

```java
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.ArgumentCaptor;
import org.mockito.junit.jupiter.MockitoExtension;
import java.math.BigDecimal;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CaptorExampleTest {
    record Order(String status, BigDecimal total) {}
    interface Repository { void save(Order order); }
    static class Service {
        private final Repository repository;
        Service(Repository repository) { this.repository = repository; }
        void placeOrder() { repository.save(new Order("PENDING", BigDecimal.TEN)); }
    }

    @Mock Repository repository;
    @Captor ArgumentCaptor<Order> captor;

    @Test
    void inspectsArgumentCreatedByService() {
        new Service(repository).placeOrder();
        verify(repository).save(captor.capture());
        Order saved = captor.getValue();
        assertEquals("PENDING", saved.status());
        assertEquals(BigDecimal.TEN, saved.total());
    }

    @Test
    void readsHistoryAndLastCapturedValue() {
        Order first = new Order("PENDING", BigDecimal.ONE);
        Order second = new Order("PAID", BigDecimal.TEN);
        repository.save(first);
        repository.save(second);
        verify(repository, times(2)).save(captor.capture());
        assertEquals(List.of(first, second), captor.getAllValues());
        assertSame(second, captor.getValue());
    }
}
```

Здесь поля Mock/Captor инициализирует **MockitoExtension 5.11.0**. Альтернативы — явный `ArgumentCaptor.forClass(...)` или `MockitoAnnotations.openMocks(this)` с корректным закрытием возвращённого ресурса. Одна аннотация Captor без обработки не инициализирует Java-поле.

GetValue возвращает последнюю захваченную ссылку, GetAllValues — список захваченных значений. Если один изменяемый объект передали несколько раз и затем изменили, captor не восстановит прежние поля: нужны неизменяемые аргументы или явно сохранённые снимки. Captor технически работает и в stubbing, но документация рекомендует его для verification по читаемости и локализации ошибок; это отличие разобрано в Q39.

---


## Q18. Какие матчеры аргументов предоставляет `ArgumentMatchers`?

`ArgumentMatchers` задаёт условия для аргументов в stubbing и verification. Если хотя бы один аргумент вызова задан matcher, **все аргументы этого вызова** должны быть заданы matchers: например, `find(anyLong(), eq("ACTIVE"))`. Смешение `anyLong()` и обычной строки может скомпилироваться, но даст runtime InvalidUseOfMatchersException.

| Matcher | Сопоставление в Mockito 5.11.0 |
|---------|------------------------------|
| `any()` | Любое одиночное значение, включая null; не обозначает весь varargs-массив |
| `any(Type.class)` | Ненулевое значение подходящего типа |
| `anyString()` | Ненулевой String |
| `anyInt()`, `anyLong()` | Соответствующий primitive или ненулевой wrapper |
| `eq(value)` | Равенство значений; для массивов учитываются элементы |
| `same(object)` | Идентичность ссылки |
| `isNull()`, `isNotNull()` | Условие на null |
| `contains(text)`, `startsWith(text)` | Условие на строку |
| `argThat(matcher)` | Пользовательское условие; см. Q19 |

```java
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class MatcherExampleTest {
    interface Service { String find(long id, String status); }
    interface Labels { String read(String key); }
    interface Events { boolean publish(String... values); }

    @Test
    void usesMatchersForBothArguments() {
        Service service = mock(Service.class);
        when(service.find(anyLong(), eq("ACTIVE"))).thenReturn("found");
        assertEquals("found", service.find(7L, "ACTIVE"));
        verify(service).find(eq(7L), eq("ACTIVE"));
        assertNull(service.find(7L, "DISABLED"));
    }

    @Test
    void distinguishesUntypedAnyFromTypedAny() {
        Labels untyped = mock(Labels.class);
        when(untyped.read(any())).thenReturn("any");
        assertEquals("any", untyped.read(null));

        Labels typed = mock(Labels.class);
        when(typed.read(any(String.class))).thenReturn("typed");
        assertNull(typed.read(null));
        assertEquals("typed", typed.read("key"));
        when(typed.read(isNull())).thenReturn("null-key");
        assertEquals("null-key", typed.read(null));
    }

    @Test
    void distinguishesVarargElementFromWholeArray() {
        Events one = mock(Events.class);
        when(one.publish(any())).thenReturn(true);
        assertFalse(one.publish());
        assertTrue(one.publish("a"));
        assertFalse(one.publish("a", "b"));

        Events array = mock(Events.class);
        when(array.publish(any(String[].class))).thenReturn(true);
        assertTrue(array.publish());
        assertTrue(array.publish("a"));
        assertTrue(array.publish("a", "b"));
        assertFalse(array.publish((String[]) null));
    }
}
```

Методы any/eq регистрируют matcher во внутреннем состоянии Mockito и возвращают **placeholder**, например null, 0 или пустую строку. Это не объект matcher и не полезное значение для обычного production-вызова; не сохраняйте такой результат для последующего использования вне stubbing/verification.

Для varargs в исследованной версии важно различать matcher одного элемента и matcher массива целиком: `any(String[].class)` принимает ненулевой массив, включая пустой; null-массив требует другого условия. Поведение других перегрузок и версий не следует выводить только из названия any. Для пользовательских условий проверяйте null и избегайте primitive null-unboxing (Q19).

---


## Q19. Что такое `argThat()` и когда он нужен?

`argThat(ArgumentMatcher<T>)` регистрирует пользовательское условие сопоставления. Он работает в stubbing и verification, когда готовых eq/any* недостаточно. При несовпадении matcher должен вернуть **false**, а не бросить assertion: verification может перебирать несколько фактических вызовов, и первый из них не обязан подходить.

```java
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ArgThatExampleTest {
    record Order(String status, List<String> items) {}
    interface Repository { boolean save(Order order); }

    @Test
    void skipsNonMatchingArgumentsAndHandlesNull() {
        Repository repository = mock(Repository.class);
        when(repository.save(argThat(order -> order != null
            && "CONFIRMED".equals(order.status())
            && order.items() != null && order.items().size() == 2))).thenReturn(true);

        assertFalse(repository.save(null));
        assertFalse(repository.save(new Order("PENDING", List.of("a"))));
        assertTrue(repository.save(new Order("CONFIRMED", List.of("a", "b"))));
        verify(repository).save(argThat(order -> order != null
            && "CONFIRMED".equals(order.status())
            && order.items() != null && order.items().size() == 2));
    }

    @Test
    void performsDetailedAssertionsAfterCapture() {
        Repository repository = mock(Repository.class);
        repository.save(new Order("PENDING", List.of("a")));
        repository.save(new Order("CONFIRMED", List.of("a", "b")));

        ArgumentCaptor<Order> captor = ArgumentCaptor.forClass(Order.class);
        verify(repository, times(2)).save(captor.capture());
        Order confirmed = captor.getAllValues().get(1);
        assertEquals("CONFIRMED", confirmed.status());
        assertEquals(List.of("a", "b"), confirmed.items());
    }
}
```

Исключение из тела matcher не превращается в false: null-unsafe условие может пробросить NPE, а assertions — прервать поиск до подходящего вызова. Делайте matcher чистым и явно обрабатывайте допустимый null. Не рассчитывайте на единственный вызов предиката; он может выполняться при поиске стаба и verification.

Для primitive-параметров выбирайте соответствующий matcher, например intThat, чтобы не получить null-unboxing от placeholder ссылочного argThat. Matcher не предоставляет getValue или историю захвата; для анализа аргументов и подробных assertions используйте captor после verification (Q17/Q39). Диагностику именованного matcher можно пояснить через toString.

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

`reset(mock)` удаляет stubbing и историю взаимодействий указанного мока. Сам Java-объект и параметры его создания сохраняются: это не новый экземпляр, не повтор конструктора и не очистка настоящих полей spy. После reset действует сохранённый default Answer, который не обязан возвращать null.

`clearInvocations(mock)` очищает только историю, сохраняя stubs. Это полезное различие, но оба механизма стоит применять по цели теста: в обычном unit-тесте новый mock и заново созданный SUT часто проще общего изменяемого состояния.

```java
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ResetExampleTest {
    interface Repository { String name(); }

    @Test
    void distinguishesHistoryFromStubbing() {
        Repository repository = mock(Repository.class);
        when(repository.name()).thenReturn("Alice");
        assertEquals("Alice", repository.name());

        clearInvocations(repository);
        verifyNoInteractions(repository);
        assertEquals("Alice", repository.name()); // stub сохранился

        reset(repository);
        verifyNoInteractions(repository);
        assertNull(repository.name()); // стандартный Answer для String
    }
}
```

Для container-managed mock reset иногда оправдан, но не добавляйте ручной сброс только потому, что это Spring-тест. В **Spring Boot 3.3.2** у `@MockBean` default — `MockReset.AFTER`: стандартная интеграция уже сбрасывает mock после тестового метода. Учитывайте выбранный reset mode и listener configuration. Сброс mock не очищает БД, другие бины или произвольный граф ссылок.

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

`RETURNS_DEEP_STUBS` — default Answer, создающий подходящие промежуточные моки и **переиспользующий их для совместимых вызовов**. Это не новый объект при каждом обращении и не выполнение настоящих методов цепочки.

```java
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class DeepStubExampleTest {
    interface Order { Customer getCustomer(); }
    interface Customer { Address getAddress(); String name(); }
    interface Address { String getCity(); }

    @Test
    void reusesIntermediateMocks() {
        Order order = mock(Order.class, RETURNS_DEEP_STUBS);
        when(order.getCustomer().getAddress().getCity()).thenReturn("Moscow");

        Customer customer = order.getCustomer();
        Address address = customer.getAddress();
        assertSame(customer, order.getCustomer());
        assertSame(address, order.getCustomer().getAddress());
        assertEquals("Moscow", order.getCustomer().getAddress().getCity());
        verify(address).getCity(); // проверяем конечный вызов
        assertNull(customer.name()); // String не превращается в промежуточный mock
    }
}
```

Deep stubs не обещают ненулевой результат любого типа. В **Mockito 5.11.0** учитываются возвращаемый тип, доступная generic-информация и MockMaker; возможны стандартные empty/default значения. В частности, String исключён из inline-моков, поэтому `customer.name().trim()` без явного строкового стаба может дать NPE. Разные аргументы могут вести к разным промежуточным мокам; это не один объект на весь граф.

Приём бывает полезен для чужого цепочного API, когда тесту важен конечный результат. Для своего кода длинная навигация — повод проверить связанность и границу зависимости, а не доказательство ошибки дизайна. Настройка цепочки сама вызывает промежуточные методы; при verification учитывайте, какое звено и какие взаимодействия вы проверяете.

---


## Q28. (!) Что такое strict stubbing и зачем он нужен?

Strict stubbing — проверки, выполняемые **во время тестового прогона**, а не Java-компилятором. В `MockitoExtension` **5.11.0** default — `STRICT_STUBS`; обычный вызов `mock()` сам по себе не означает такую же сессию расширения.

| Режим | Дополнительное поведение |
|-------|-------------------------|
| `LENIENT` | Не добавляет unused/mismatch диагностику strict stubbing; обычная валидация API и verify сохраняются |
| `WARN` | Выводит предупреждения через Mockito logger; для этого режима важно читать сообщения |
| `STRICT_STUBS` | Может завершить тест с UnnecessaryStubbingException из-за лишнего стаба либо PotentialStubbingProblem при потенциальном mismatch |

Диагностика mismatch не означает, что **любой** незастабленный вызов падает: важны действующая строгость, stubbing, версия и условия обнаружения, включая source-file heuristic 5.11.0 (см. Q9). Strictness и default Answer — разные настройки. Совпавший использованный stub в STRICT_STUBS помечается verified автоматически:

```java
import org.junit.jupiter.api.Test;
import org.mockito.MockitoSession;
import org.mockito.quality.Strictness;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

class StrictSessionExampleTest {
    interface Repository { String find(long id); }

    @Test
    void usesStrictSession() {
        MockitoSession session = mockitoSession()
            .strictness(Strictness.STRICT_STUBS).startMocking();
        try {
            Repository repository = mock(Repository.class);
            when(repository.find(1L)).thenReturn("Alice");
            assertEquals("Alice", repository.find(1L));
            verifyNoMoreInteractions(repository); // использованный stub уже verified
        } finally {
            session.finishMocking();
        }
    }
}
```

Неиспользованный обычный stub может дать UnnecessaryStubbingException при завершении сессии. Если сам тест уже завершился ошибкой, вторичная диагностика может отличаться. Более локальная строгость стаба/мока имеет значение; для настройки расширения используют `@MockitoSettings`, а не выдуманное имя исключения или обещание компиляторной проверки.

---

## Q29. Как сделать отдельный stub ненастойчивым с помощью `lenient()`?

`lenient().when(...)` исключает **выбранный stub** из unused-stubbing и argument-mismatch проверок strict stubbing. Оно не отключает `verify`, проверку совместимости возвращаемого типа и остальные правила API. Другие stubs сохраняют свою effective strictness.

Пример общей подготовки: один тест читает feature flag, другой проходит по ветке без обращения к нему.

```java
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class LenientFeatureTest {
    interface Flags { boolean enabled(String name); }
    static class View {
        private final Flags flags;
        View(Flags flags) { this.flags = flags; }
        String label(boolean newView) {
            return newView && flags.enabled("NEW_UI") ? "new" : "classic";
        }
    }
    @Mock Flags flags;

    @BeforeEach
    void setUp() {
        lenient().when(flags.enabled("NEW_UI")).thenReturn(true);
    }
    @Test
    void classicViewDoesNotReadFlag() {
        assertEquals("classic", new View(flags).label(false));
        verifyNoInteractions(flags);
    }
    @Test
    void newViewReadsFlag() {
        assertEquals("new", new View(flags).label(true));
        verify(flags).enabled("NEW_UI");
    }
}
```

Сначала проверьте, нельзя ли перенести лишнюю общую настройку в нужный тест. Если общий stub обоснован, per-stub lenient сохраняет контроль остальных настроек. `@MockitoSettings(strictness = Strictness.LENIENT)` задаёт default сессии расширения, а не делает любой более локальный mock/stub безусловно lenient: effective strictness учитывает настройки стаба, мока и сессии.

---

## Q30. Можно ли вернуть разные значения при последовательных вызовах?

Да: цепочка Answers или varargs thenReturn задают последовательность на одном моке. Когда остаётся последний **Answer**, Mockito продолжает применять его. Постоянный thenReturn повторяет значение; динамический thenAnswer вычисляет новый результат при каждом вызове; последний бросающий Answer продолжает бросать.

```java
import org.junit.jupiter.api.Test;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

class AnswerSequenceTest {
    interface Counter { int next(); }

    @Test
    void repeatsLastConstant() {
        Counter counter = mock(Counter.class);
        when(counter.next()).thenReturn(1, 2, 3);
        assertEquals(List.of(1, 2, 3, 3),
            List.of(counter.next(), counter.next(), counter.next(), counter.next()));
        verify(counter, times(4)).next();
    }
    @Test
    void evaluatesLastDynamicAnswerAgain() {
        Counter counter = mock(Counter.class);
        AtomicInteger count = new AtomicInteger(10);
        when(counter.next()).thenReturn(1).thenAnswer(inv -> count.getAndIncrement());
        assertEquals(List.of(1, 10, 11, 12),
            List.of(counter.next(), counter.next(), counter.next(), counter.next()));
        verify(counter, times(4)).next();
    }
}
```

Цепочка `thenReturn(first).thenReturn(second)` и varargs `thenReturn(first, second)` задают одинаковую последовательность постоянных ответов. Отдельные повторные `when(...)` на тот же вызов не образуют очередь автоматически: новый stubbing перекрывает предыдущий. При перенастройке when может вызвать уже существующий Answer, поэтому для бросающего стаба пригодится doReturn (Q10).

Последовательность не ограничивает число обращений по контракту — это проверяют отдельно через `verify`. При совместном использовании мока несколькими потоками порядок потребления ответов зависит от порядка фактических вызовов; сам пример не задаёт порядок исполнения рабочих потоков.

---

## Q31. Чем отличается `Mockito.spy()` от `@Spy`?

Оба способа дают spy с реальными ответами по умолчанию, но различаются **моментом создания, источником состояния и обработкой аннотаций**:

- `Mockito.spy(instance)` вызывается явно и использует состояние переданного объекта. В стандартной конфигурации это копирование, а не обязательная оболочка с делегированием оригиналу.
- `Mockito.spy(Type.class)` создаёт spy по типу с конструкторной инициализацией. Готовый экземпляр передавать не обязательно; поддерживаемость типа и конструктора нужно проверять.
- Поле `@Spy` должно быть обработано, например `MockitoExtension` или `MockitoAnnotations.openMocks`. При готовом значении обработчик использует его состояние; при null пытается создать подходящий spy. Само наличие аннотации ничего не инициализирует.

**Параметр обобщённого типа не требует инициализатора.** `@Spy ArrayList<String> values;` может быть создан через конструктор без аргументов. А `@Spy List<String> values = new ArrayList<>();` явно выбирает реализацию. Различие здесь в типе/состоянии, а не в наличии `<String>`.

```java
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class SpyCreationExampleTest {
    static class Initialized {
        int number = 7;
        int value() { return number; }
    }
    interface View {
        default String name() { return "view"; }
        int count();
    }

    @Spy ArrayList<String> created;
    @Spy List<String> supplied = new ArrayList<>();
    @Spy View view;

    @Test
    void concreteGenericFieldCanBeCreated() {
        created.add("created");
        supplied.add("supplied");
        assertEquals(List.of("created"), created);
        assertEquals(List.of("supplied"), supplied);
    }

    @Test
    void factoryCanTakeInstanceOrType() {
        Initialized original = new Initialized();
        Initialized fromInstance = spy(original);
        Initialized fromType = spy(Initialized.class);
        assertNotSame(original, fromInstance);
        assertEquals(7, fromInstance.value());
        assertEquals(7, fromType.value());
    }

    @Test
    void interfaceFieldUsesAvailableDefaultMethod() {
        assertTrue(mockingDetails(view).isSpy());
        assertEquals("view", view.name());
        assertEquals(0, view.count());  // У абстрактного метода нет тела.
        doReturn(4).when(view).count();
        assertEquals(4, view.count());
    }
}
```

В Mockito 5.11 обработчик поддерживает и `@Spy` поля-интерфейсы без готового объекта: default-метод может выполняться, абстрактный — получать стандартный ответ. Это **не создаёт скрытую бизнес-реализацию**: `@Spy List<String> values;` не превращается в ArrayList с хранилищем. Чтобы `add` реально сохранял элементы, задай реализацию явно. Также доступны создаваемые абстрактные классы: их конкретные методы могут выполняться, абстрактные требуют отдельного поведения.

Для обычного поля класса без инициализатора нужен подходящий способ конструкторного создания; например, класс только с аргументным конструктором не создаётся обычным `@Spy` автоматически. Готовое значение можно задать самостоятельно. Сочетание `@Spy @InjectMocks` обрабатывается отдельным механизмом внедрения зависимостей и не сводится к этому правилу (Q5).

Реальные методы и stubbing одного вида spy работают по общей модели, но «разница чисто синтаксическая» скрывает важную настройку теста: до обработки поле с инициализатором остаётся обычным объектом, поле без инициализатора — null. Обработку и завершение её ресурсов выбирают явно (Q8). Поверхностно разделяемое состояние и `doReturn` при настройке рассматриваются в Q6/Q10.

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

`mockConstruction(Target.class, initializer)` создаёт control для construction mocking. В **Mockito 5.11.0** с поддерживающим inline maker подходящие создания конкретного типа перехватываются в **потоке регистрации**, пока control активен. Initializer настраивает полученные моки, `constructed()` предоставляет их список, а context — аргументы и сведения о вызванном конструкторе.

```java
import org.junit.jupiter.api.Test;
import org.mockito.MockedConstruction;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

class ConstructionScopeTest {
    static class Gateway {
        static int constructorCalls;
        private final String endpoint;
        Gateway(String endpoint) {
            constructorCalls++;
            this.endpoint = endpoint;
        }
        String fetch() { return "real:" + endpoint; }
    }
    static class Service {
        String load() { return new Gateway("orders").fetch(); }
    }

    @Test
    void interceptsHelperAndRestoresConstructor() {
        Gateway.constructorCalls = 0;
        try (MockedConstruction<Gateway> control = mockConstruction(
                Gateway.class, (mock, context) ->
                    when(mock.fetch()).thenReturn("stub:" + context.arguments().get(0)))) {
            assertEquals("stub:orders", new Service().load());
            assertEquals(1, control.constructed().size());
            verify(control.constructed().get(0)).fetch();
            assertEquals(0, Gateway.constructorCalls);
        }
        assertEquals("real:orders", new Service().load());
        assertEquals(1, Gateway.constructorCalls);
    }
}
```

`new Gateway` расположен в helper-коде Service, но выполняется в том же потоке при активной регистрации и потому перехватывается. В примере тело конструктора Gateway не выполняется; после close снова работает настоящее создание. Существовавшие ранее объекты не заменяются.

Scope определяется временем жизни control и потоком, а не текстовыми границами `try`. Try-with-resources удобно гарантирует close; гарантированное закрытие в teardown тоже допустимо. Регистрация не переносится автоматически в worker thread. Нужны поддерживающий maker и допустимый тип: например, inline maker 5.11.0 отклоняет construction mocking abstract-типа, primitive и Object.

Приём полезен для legacy-зависимости, которую код создаёт сам. Если границу можно изменить, внедрение зависимости часто упрощает тест; это проектное решение, а не запрет Mockito.

---


## Q36. (!) В чём разница между `mockito-core` и `mockito-inline`?

Различайте **артефакт зависимости**, default MockMaker версии и фактически выбранный provider. В **Mockito 5.0** default основного mockito-core сменился с subclass на inline. Исторический mockito-inline помогал выбрать inline provider; имя артефакта само по себе не описывает все допустимые конфигурации core.

В исследованном **Mockito 5.11.0** для обычного inline-сценария достаточно core. Это воспроизведённая версия примера, не указание на последний релиз:

```xml
<dependency>
    <groupId>org.mockito</groupId>
    <artifactId>mockito-core</artifactId>
    <version>5.11.0</version>
</dependency>
```

Inline использует instrumentation и может сочетать её с subclassing; subclass maker создаёт наследника и не снимает ограничения final-переопределения. Default можно переопределить настройкой provider или конкретного мока. Пример локального выбора без изменения глобальных plugin-файлов:

```java
import org.junit.jupiter.api.Test;
import org.mockito.MockMakers;
import org.mockito.exceptions.base.MockitoException;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class MakerSelectionTest {
    static final class Token { String value() { return "real"; } }

    @Test
    void defaultInlineCanMockOwnFinalType() {
        Token token = mock(Token.class);
        when(token.value()).thenReturn("stub");
        assertEquals("stub", token.value());
    }

    @Test
    void explicitSubclassHasDifferentLimits() {
        assertThrows(MockitoException.class, () ->
            mock(Token.class, withSettings().mockMaker(MockMakers.SUBCLASS)));
    }
}
```

Этот тест предполагает default inline в указанной конфигурации. Возможности зависят от maker, типов, сочетания settings и JVM. Inline требует подходящей instrumentation-среды; отдельный старый артефакт не отменяет ограничений. Совместимость с другими agents и performance нужно проверять в своём окружении: универсальное «inline медленнее» здесь не измерялось. Для старых версий сверяйте поддерживаемый способ выбора provider с их документацией, а не переносите default Mockito 5 назад.

---


## Q37. Что такое `MockSettings` и какие опции он предоставляет?

`MockSettings` задаёт параметры **создания** через mock(Type.class, withSettings()...). Это не string-based API настройки метода по имени. Обычные ответы задают stubbing; defaultAnswer — обработчик незастабленного invocation, а не объект, автоматически возвращаемый вызывающему коду.

```java
import org.junit.jupiter.api.Test;
import org.mockito.exceptions.base.MockitoException;
import java.io.Serializable;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ConfiguredMockTest {
    interface Service { String lookup(String key); }
    interface Marker {}

    @Test
    void configuresNameAndExtraInterfaces() {
        Service service = mock(Service.class, withSettings()
            .name("catalog").defaultAnswer(RETURNS_DEFAULTS)
            .extraInterfaces(Marker.class).serializable());
        assertTrue(service instanceof Marker);
        assertTrue(service instanceof Serializable);
        assertEquals("catalog", mockingDetails(service).getMockCreationSettings()
            .getMockName().toString());
        when(service.lookup("known")).thenReturn("value");
        assertEquals("value", service.lookup("known"));
        verify(service).lookup("known");
    }

    @Test
    void stubOnlyKeepsStubbingButNotVerificationHistory() {
        Service service = mock(Service.class, withSettings().stubOnly());
        when(service.lookup("known")).thenReturn("value");
        assertEquals("value", service.lookup("known"));
        assertTrue(mockingDetails(service).getInvocations().isEmpty());
        assertThrows(MockitoException.class, () -> verify(service).lookup("known"));
    }

    @Test
    void executesCustomDefaultAnswerUnlessStubMatches() {
        Service service = mock(Service.class, withSettings().defaultAnswer(invocation -> {
            if (invocation.getMethod().getName().equals("lookup")) {
                return "missing:" + invocation.getArgument(0);
            }
            return RETURNS_DEFAULTS.answer(invocation);
        }));
        assertEquals("missing:other", service.lookup("other"));
        when(service.lookup("known")).thenReturn("value");
        assertEquals("value", service.lookup("known"));
    }
}
```

| Опция | Что задаёт |
|-------|------------|
| name | Имя для диагностики |
| defaultAnswer | Общую политику незастабленных вызовов |
| extraInterfaces / serializable | Дополнительные интерфейсы / поддержку сериализуемого мока в допустимой конфигурации |
| verboseLogging | Логирование invocations для отладки |
| stubOnly | Отказ от записи истории; обычный verify недоступен |
| spiedInstance / useConstructor | Исходный экземпляр для spy / попытку использовать конструктор; см. Q42 |
| strictness / mockMaker | Локальный выбор строгости / maker; см. Q28/Q36 |

Не собирайте все опции в один мок без цели. Serializability marker не доказывает, что произвольный граф stubs, Answers и аргументов успешно сериализуется. Возможность extraInterfaces и других сочетаний зависит от типа и maker. SpiedInstance не означает автоматическую делегацию тому же Java-объекту; состояние и default Answer нужно учитывать отдельно (Q42). Доля тестов, которым нужны settings, здесь не измеряется.

---


## Q38. Какие встроенные `Answer` доступны в Mockito?

`Answer` — обработчик invocation: он может использоваться как defaultAnswer или в thenAnswer. Встроенный enum Answers даёт готовые стратегии. Для default поведения незастабленных вызовов в **Mockito 5.11.0** важны следующие границы:

| Стратегия | Поведение |
|-----------|-----------|
| RETURNS_DEFAULTS | Подходящие defaults: 0/false, пустые коллекции/Optional, null для ряда ссылочных результатов |
| RETURNS_SMART_NULLS | Сначала обычные empty-ответы, затем SmartNull для подходящего ссылочного типа; не каждый результат становится SmartNull |
| RETURNS_MOCKS | Сначала empty-ответы, иначе при возможности mock возвращаемого типа; переиспользование всей цепочки не гарантируется |
| RETURNS_DEEP_STUBS | Создаёт/переиспользует подходящие промежуточные моки; ограничения типов сохраняются (Q27) |
| CALLS_REAL_METHODS | Для доступного конкретного тела вызывает реальный метод; abstract-метод получает стандартный ответ |
| RETURNS_SELF | Возвращает mock для подходящего совместимого типа, но в 5.11.0 отдельно исключён Object; иначе default |

```java
import org.junit.jupiter.api.Test;
import org.mockito.exceptions.verification.SmartNullPointerException;
import java.util.List;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class AnswersExampleTest {
    interface Child { String name(); }
    interface Data {
        int count(); String text(); List<String> items();
        Optional<String> maybe(); Child child();
    }
    interface Builder { Builder name(String value); String build(); Object objectResult(); }
    interface Greeter {
        default String greeting() { return "real"; }
        String abstractValue();
    }

    @Test
    void returnsStandardDefaults() {
        Data data = mock(Data.class, RETURNS_DEFAULTS);
        assertEquals(0, data.count());
        assertNull(data.text());
        assertTrue(data.items().isEmpty());
        assertEquals(Optional.empty(), data.maybe());
        assertNull(data.child());
    }

    @Test
    void smartNullIsDifferentFromOrdinaryEmptyValue() {
        Data data = mock(Data.class, RETURNS_SMART_NULLS);
        assertTrue(data.items().isEmpty());
        assertEquals("", data.text());
        Child child = data.child();
        assertTrue(mockingDetails(child).isMock());
        assertTrue(child.toString().contains("SmartNull"));
        assertThrows(SmartNullPointerException.class, child::name);
    }

    @Test
    void distinguishesMocksFromDeepStubReuse() {
        Data ordinary = mock(Data.class, RETURNS_MOCKS);
        Child first = ordinary.child();
        assertTrue(mockingDetails(first).isMock());
        assertNotSame(first, ordinary.child());
        Data deep = mock(Data.class, RETURNS_DEEP_STUBS);
        assertSame(deep.child(), deep.child());
    }

    @Test
    void selfAppliesOnlyToSelectedReturnTypes() {
        Builder builder = mock(Builder.class, RETURNS_SELF);
        assertSame(builder, builder.name("a").name("b"));
        assertNull(builder.build());
        assertNull(builder.objectResult()); // Object отдельно исключён в 5.11.0
    }

    @Test
    void realMethodNeedsAnAvailableBody() {
        Greeter greeter = mock(Greeter.class, CALLS_REAL_METHODS);
        assertEquals("real", greeter.greeting());
        assertNull(greeter.abstractValue());
    }
}
```

SmartNull помогает диагностировать обращение к незастабленной зависимости, но не является обычным заполненным объектом; его диагностический toString отличается от вызова бизнес-метода. Для неподдерживаемого возвращаемого типа по-прежнему возможен null.

CALLS_REAL_METHODS не инициализирует поля автоматически и сам по себе не запускает конструктор мока: реальный код может упасть на незаполненном состоянии (Q42). Выбирайте стратегию по контракту теста; использование deep stubs или SmartNull не объявляется самым частым либо универсально лучшим.

---


## Q39. (!) Когда выбирать `ArgumentCaptor`, а когда `argThat()`?

`argThat` задаёт условие сопоставления аргумента. `ArgumentCaptor` собирает переданные аргументы для последующих assertions. Для обычного равенства часто достаточно самого значения или `eq`; matcher и captor нужны, когда это упрощает проверку.

| Задача | Подход |
|--------|--------|
| Выбрать ответ по условию аргумента | Matcher в stubbing, например `argThat` |
| Проверить вызовы, подходящие под условие | `verify(..., times(n))` с matcher; ручной счётчик не обязателен |
| Проверить несколько полей или историю аргументов | Captor в verification, затем assertions над `getValue()` / `getAllValues()` |

```java
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ArgumentChoiceTest {
    record Order(long id, String status) {}
    interface Repository { boolean save(Order order); }

    @Test
    void capturesArgumentsForLaterAssertions() {
        Repository repository = mock(Repository.class);
        Order first = new Order(1L, "PENDING");
        Order second = new Order(2L, "PAID");
        repository.save(first);
        repository.save(second);

        ArgumentCaptor<Order> captor = ArgumentCaptor.forClass(Order.class);
        verify(repository, times(2)).save(captor.capture());
        assertEquals(List.of(first, second), captor.getAllValues());
        assertEquals(second, captor.getValue()); // последнее захваченное значение
    }

    @Test
    void matchesSeveralCallsWithoutManualCounter() {
        Repository repository = mock(Repository.class);
        when(repository.save(argThat(order ->
            order != null && order.status().equals("PENDING")))).thenReturn(true);

        assertTrue(repository.save(new Order(1L, "PENDING")));
        assertTrue(repository.save(new Order(2L, "PENDING")));
        assertFalse(repository.save(new Order(3L, "CANCELLED")));
        verify(repository, times(2)).save(argThat(order ->
            order != null && order.status().equals("PENDING")));
    }
}
```

В **Mockito 5.11.0** captor технически может работать и внутри stubbing: аргумент захватывается при подходящем последующем вызове. Авторы рекомендуют его для verification, поскольку stubbing с captor может ухудшить читаемость и локализацию ошибки: если вызова не было, захватывать нечего. Это рекомендация, а не запрет API.

`getValue()` возвращает последнее захваченное значение, `getAllValues()` — список захваченных значений. Для изменяемых объектов captor сохраняет ссылки, а не глубокие снимки: последующая мутация видна при assertion. Matcher также не является снимком состояния на момент вызова; он может выполняться при stubbing lookup и при verification. Подробный diff зависит от выбранных assertions и диагностики matcher, а не гарантируется именем API.

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

В **MockitoExtension 5.11.0** отдельные resolvers поддерживают параметры **Mock и Captor**. Зарегистрируйте интеграцию: одна аннотация параметра без подходящего Jupiter ParameterResolver недостаточна. Пример показывает Mock в конструкторе/BeforeEach/тесте и Captor в тестовом методе:

```java
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ParameterInjectionExampleTest {
    interface Clock { long now(); }
    interface Gateway { boolean charge(long amount); }
    interface Repository { void save(Order order); }
    record Order(String status) {}
    static class Service {
        private final Gateway gateway;
        private final Repository repository;
        Service(Gateway gateway, Repository repository) {
            this.gateway = gateway; this.repository = repository;
        }
        void place(long amount) {
            if (gateway.charge(amount)) repository.save(new Order("PAID"));
        }
    }
    private final Clock constructorClock;
    private Clock setupClock;

    ParameterInjectionExampleTest(@Mock Clock clock) {
        constructorClock = clock;
    }
    @BeforeEach
    void prepare(@Mock Clock clock) {
        setupClock = clock;
    }
    @Test
    void processesOrder(@Mock Clock clock, @Mock Gateway gateway,
                        @Mock Repository repository, @Captor ArgumentCaptor<Order> captor) {
        assertNotSame(constructorClock, clock);
        assertNotSame(setupClock, clock);
        when(gateway.charge(10L)).thenReturn(true);
        new Service(gateway, repository).place(10L);
        verify(repository).save(captor.capture());
        assertEquals("PAID", captor.getValue().status());
    }
}
```

Одинаковый тип или имя параметра не означает один общий mock: передавайте в SUT именно тот экземпляр, которым управляет данный тест. Параметры уменьшают число полей, но сами по себе не изолируют всё состояние приложения или теста, особенно при другом lifecycle.

InjectMocks на поле использует поддерживаемые полевые кандидаты и не подключает автоматически любой mock параметра метода к уже созданному SUT. При параметрическом подходе явное создание SUT часто понятнее. Конструктор теста тоже требует зарегистрированного расширения. Support Captor подтверждён именно для 5.11.0; переносить ограничения других версий без проверки нельзя.

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

`@MockitoSettings` задаёт настройки MockitoExtension, включая **session default strictness**. В **5.11.0** аннотация сама мета-регистрирует расширение через ExtendWith; дополнительный ExtendWith той же интеграции не обязателен. Настройка может находиться на классе; поиск в test context также учитывает более близкую аннотацию метода.

Пример намеренно показывает разные области настройки, а не рекомендует оставлять лишние stubs:

```java
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@MockitoSettings(strictness = Strictness.LENIENT)
class StrictnessSettingsExampleTest {
    interface Api { String read(); }
    @Mock Api api;

    @Test
    void classDefaultAllowsUnusedStub() {
        when(api.read()).thenReturn("unused");
        assertNotNull(api); // MockitoSettings уже зарегистрировала extension
    }

    @Test
    @MockitoSettings(strictness = Strictness.STRICT_STUBS)
    void methodOverridesClassDefault() {
        when(api.read()).thenReturn("used");
        assertEquals("used", api.read());
        verifyNoMoreInteractions(api); // strict stub invocation уже verified
    }

    @Test
    void lenientDoesNotDisableExplicitVerification() {
        assertThrows(AssertionError.class, () -> verify(api).read());
    }

    @Test
    void localMockStrictnessStillMattersForLookup() {
        Api local = mock(Api.class, withSettings().strictness(Strictness.STRICT_STUBS));
        when(local.read()).thenReturn("local");
        assertEquals("local", local.read());
        verifyNoMoreInteractions(local);
    }
}
```

| Режим | Дополнительная strict-stubbing политика |
|-------|-----------------------------------------|
| STRICT_STUBS | Диагностика unused/potential mismatch с учётом условий и lifecycle; использованный strict stub может быть auto-verified |
| LENIENT | Мягкий session default; verify и валидация API остаются |
| WARN | Диагностика через logger вместо такого же строгого отказа; другие ошибки теста не исчезают |

Более локальные настройки mock/stub имеют значение; это не общий выключатель всех проверок класса. Завершение сессии и lookup-диагностика имеют свои условия (Q28/Q29). Предпочитайте удалить неуместный общий stub или ослабить обоснованный конкретный stub через lenient, а не автоматически переводить любой класс в LENIENT.

Стандартный MockitoExtension 5.11.0 не читает выдуманный platform parameter mockito.strictness. Произвольное имя в junit-platform.properties/Launcher не становится настройкой Mockito без кода, который его обрабатывает. Настройки Jupiter и Mockito различаются.

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
