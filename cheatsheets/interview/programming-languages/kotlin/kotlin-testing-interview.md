---
title: "Вопросы на собеседовании: Тестирование на Kotlin (MockK, Kotest)"
description: "Мокинг через MockK, спецификации и matchers Kotest, property-based и data-driven тесты, тестирование корутин и Flow, JUnit 5 и Testcontainers для Kotlin"
tags:
  - interview
  - programming-languages
  - kotlin-testing
type: "interview"
difficulty: "intermediate"
aliases:
  - "Тестирование на Kotlin (MockK, Kotest) interview"
  - "Тестирование на Kotlin (MockK, Kotest) собеседование"
  - "Тестирование на Kotlin (MockK, Kotest) вопросы"
related:
  - "[[kotlin-testing]]"
updated: "2026-06-25"
---

# Вопросы на собеседовании: `Тестирование на Kotlin (MockK, Kotest)`

Тестовый стек Kotlin отличается от Java: классы по умолчанию `final`, есть корутины, `suspend`-функции, top-level и extension-функции — всё это плохо ложится на Mockito и классический JUnit-стиль. Поэтому в экосистеме выросли Kotlin-native инструменты: `MockK` для мокинга и `Kotest` как фреймворк со спецификациями, matchers и property-based тестами. На собеседовании проверяют, понимаете ли вы, чем именно эти инструменты лучше Java-аналогов и как корректно тестировать асинхронный код.

Дата последнего обновления: 2026-06-25

## Полезные ссылки

### Официальная документация

- [MockK](https://mockk.io/) — официальный сайт и справочник по API мокинга
- [Kotest](https://kotest.io/) — документация фреймворка: стили, matchers, property testing
- [kotlinx-coroutines-test](https://kotlinlang.org/api/kotlinx.coroutines/kotlinx-coroutines-test/) — `runTest`, `TestDispatcher`, виртуальное время
- [Turbine](https://github.com/cashapp/turbine) — библиотека для тестирования `Flow`
- [JUnit 5 User Guide](https://junit.org/junit5/docs/current/user-guide/) — `@TestInstance`, жизненный цикл, расширения
- [Baeldung: Тестирование на Kotlin (MockK, Kotest)](https://www.baeldung.com/kotlin/mockk) — туториал

## Содержание

- [Полезные ссылки](#полезные-ссылки)
- [See also](#see-also)

**MockK: основы мокинга**
- [Q1. (!) Что такое MockK и почему для Kotlin предпочитают его, а не Mockito](#q1--что-такое-mockk-и-почему-для-kotlin-предпочитают-его-а-не-mockito)
- [Q2. Как работают every / returns / verify в MockK](#q2-как-работают-every--returns--verify-в-mockk)
- [Q3. Что такое relaxed mock и когда он нужен](#q3-что-такое-relaxed-mock-и-когда-он-нужен)
- [Q4. Что делает spyk и чем спай отличается от мока](#q4-что-делает-spyk-и-чем-спай-отличается-от-мока)
- [Q5. (!) Как захватывать аргументы через slot и capture](#q5--как-захватывать-аргументы-через-slot-и-capture)

**MockK: продвинутое**
- [Q6. (!) Как мокать suspend-функции через coEvery и coVerify](#q6--как-мокать-suspend-функции-через-coevery-и-coverify)
- [Q7. Как подключить MockKExtension в JUnit 5](#q7-как-подключить-mockkextension-в-junit-5)
- [Q8. (!) Почему MockK мокает final-классы, а Mockito буксует](#q8--почему-mockk-мокает-final-классы-а-mockito-буксует)
- [Q9. Как мокать top-level, extension- и static-функции](#q9-как-мокать-top-level-extension--и-static-функции)
- [Q10. В чём разница clearAllMocks, unmockkAll и confirmVerified](#q10-в-чём-разница-clearallmocks-unmockkall-и-confirmverified)

**Kotest: спецификации и matchers**
- [Q11. (!) Какие стили спецификаций есть в Kotest и чем они различаются](#q11--какие-стили-спецификаций-есть-в-kotest-и-чем-они-различаются)
- [Q12. Как устроены matchers Kotest (shouldBe, shouldThrow)](#q12-как-устроены-matchers-kotest-shouldbe-shouldthrow)
- [Q13. (!) Что такое property-based testing и как его делать через checkAll и Arb](#q13--что-такое-property-based-testing-и-как-его-делать-через-checkall-и-arb)
- [Q14. Как писать data-driven тесты через withData](#q14-как-писать-data-driven-тесты-через-withdata)
- [Q15. Какие lifecycle hooks есть в Kotest](#q15-какие-lifecycle-hooks-есть-в-kotest)
- [Q16. Чем Kotest отличается от JUnit 5](#q16-чем-kotest-отличается-от-junit-5)

**JUnit 5 и тестирование асинхронного кода**
- [Q17. Что даёт kotlin.test и assertFailsWith](#q17-что-даёт-kotlintest-и-assertfailswith)
- [Q18. (!) Как тестировать корутины через runTest и TestDispatcher](#q18--как-тестировать-корутины-через-runtest-и-testdispatcher)
- [Q19. (!) Как тестировать Flow с помощью Turbine](#q19--как-тестировать-flow-с-помощью-turbine)
- [Q20. Как использовать Testcontainers в Kotlin-тестах](#q20-как-использовать-testcontainers-в-kotlin-тестах)

## Q1. (!) Что такое MockK и почему для Kotlin предпочитают его, а не Mockito

`MockK` — это библиотека мокинга, написанная на Kotlin и под Kotlin. Mockito создавался для Java, и при работе с Kotlin он спотыкается о несколько особенностей языка.

Ключевые проблемы Mockito на Kotlin:

- Классы в Kotlin по умолчанию `final`. Mockito без `mock-maker-inline` их вообще не мокает.
- Нет нативной поддержки `suspend`-функций — корутины приходится обходить вручную.
- Top-level и extension-функции компилируются в статические методы, которые Mockito не умеет мокать из коробки.
- DSL Mockito (`when(...).thenReturn(...)`) выглядит чужеродно: `when` — зарезервированное слово Kotlin, его приходится экранировать бэктиками.

MockK решает это нативным DSL и встроенной поддержкой Kotlin-фич:

```kotlin
val repo = mockk<UserRepository>()
every { repo.findById(1) } returns User(1, "Алиса")

val service = UserService(repo)
assertEquals("Алиса", service.getName(1))

verify { repo.findById(1) }
```

| Критерий | MockK | Mockito |
|----------|-------|---------|
| `final`-классы | из коробки | нужен `mockito-inline` |
| `suspend`-функции | `coEvery` / `coVerify` | вручную, костыли |
| top-level / extension | `mockkStatic` | нет нативной поддержки |
| `object` (синглтоны) | `mockkObject` | нет |
| DSL | Kotlin-native | Java-стиль, бэктики на `when` |

**Итог:** MockK выбирают, потому что он покрывает `final`-классы, корутины, статические и extension-функции «из коробки», тогда как Mockito требует дополнительных расширений и обходных путей.

## Q2. Как работают every / returns / verify в MockK

`MockK` строит работу вокруг двух фаз: настройка заглушек (stubbing) и проверка вызовов (verification).

- `every { ... }` открывает блок настройки поведения. Внутри лямбды вызывается мокаемый метод.
- `returns` / `returnsMany` / `throws` / `answers` задают, что вернуть. `returnsMany` отдаёт значения по очереди, `answers` вычисляет ответ динамически.
- `verify { ... }` проверяет, что метод действительно вызывался. Можно ограничить кратность.

```kotlin
val calc = mockk<Calculator>()

every { calc.add(2, 3) } returns 5
every { calc.divide(any(), 0) } throws ArithmeticException()
every { calc.next() } returnsMany listOf(1, 2, 3)

// проверки
verify { calc.add(2, 3) }                 // хотя бы раз
verify(exactly = 1) { calc.add(2, 3) }    // ровно один раз
verify(exactly = 0) { calc.reset() }      // не вызывался
verify(atLeast = 2) { calc.next() }       // минимум два раза
verifyOrder { calc.add(2, 3); calc.next() } // в этом порядке
```

Матчеры аргументов: `any()`, `eq(value)`, `range(...)`, `match { it > 0 }`. Если хотя бы один аргумент задан матчером, все остальные тоже должны быть матчерами — нельзя смешивать «сырое» значение и `any()` в одном вызове.

**Подвох:** для не-relaxed мока вызов метода, у которого нет `every`, бросит исключение `MockKException: no answer found`. Это намеренно: тест падает, если код пошёл неожиданным путём.

## Q3. Что такое relaxed mock и когда он нужен

Relaxed mock — это мок, который не требует описывать поведение каждого метода. На незастабленные вызовы он возвращает «разумные пустышки»: `0` для чисел, `false` для `Boolean`, пустую строку, а для объектов — вложенный relaxed-мок (chained mocks).

Создание:

```kotlin
val repo = mockk<UserRepository>(relaxed = true)
// можно сразу использовать, не описывая каждый метод
val users = repo.findAll() // вернёт пустой список / дефолт, не упадёт

// relaxUnitFun — мягкий вариант: дефолты только для Unit-функций
val logger = mockk<Logger>(relaxUnitFun = true)
logger.info("сообщение") // ок, метод возвращает Unit
```

Когда применять:

- Когда у зависимости много методов, а вас интересует поведение лишь пары из них — лишний `every` зашумляет тест.
- `relaxUnitFun = true` хорош для логгеров и прочих `Unit`-сайд-эффектов: их не надо стабить, но возвращающие значения методы по-прежнему требуют явного `every`.

**Trade-off:** полный `relaxed = true` снижает строгость теста. Если код вызовет «забытый» метод, тест не упадёт, а молча получит дефолт — баг можно проглядеть. Поэтому строгие моки (без `relaxed`) предпочтительнее, а `relaxUnitFun` — разумный компромисс.

## Q4. Что делает spyk и чем спай отличается от мока

`spyk` создаёт спай (частичный мок): реальный объект, у которого можно подменить поведение только отдельных методов, а остальные работают «по-настоящему».

```kotlin
val service = spyk(RealService()) // оборачиваем настоящий объект

// большинство методов работают реально
service.compute(5) // выполнит настоящую логику

// а этот метод подменяем
every { service.getFromDb(any()) } returns "Замоканный результат"

verify { service.getFromDb(1) } // проверки работают, как у мока
```

Разница «мок vs спай»:

| | `mockk()` | `spyk()` |
|--|-----------|----------|
| Базовое поведение | всё застаблено / падает | реальные методы вызываются |
| Объект | пустышка | обёртка над настоящим |
| Назначение | полная изоляция зависимости | подмена 1-2 методов в реальном классе |

**Подвох:** при стабинге спая внутри `every { spy.foo() }` метод `foo()` реально вызовется один раз во время записи заглушки (record phase). Если у метода есть тяжёлые сайд-эффекты, используйте форму `every { spy.foo() } answers { callOriginal() }` осознанно. Спаи легко превращаются в «запах» — частая нужда в спае намекает, что класс делает слишком много и его стоит разбить.

## Q5. (!) Как захватывать аргументы через slot и capture

`slot` и `capture` нужны, когда надо проверить не сам факт вызова, а именно те аргументы, с которыми метод вызвали — особенно если аргумент собирается внутри тестируемого кода.

```kotlin
val repo = mockk<UserRepository>(relaxed = true)
val service = UserService(repo)

val slot = slot<User>() // слот для одного захваченного значения
every { repo.save(capture(slot)) } returns Unit

service.register("Алиса", "alice@example.com")

// проверяем, что собрал тестируемый код
assertEquals("Алиса", slot.captured.name)
assertTrue(slot.captured.email.contains("@"))
```

Для нескольких вызовов используют `mutableListOf` и `capture(list)`:

```kotlin
val events = mutableListOf<Event>()
every { publisher.publish(capture(events)) } returns Unit

service.process()

assertEquals(3, events.size)
assertEquals("created", events.first().type)
```

Нюансы:

- `slot.captured` хранит значение последнего захваченного вызова; для истории берите список.
- `capture` работает и внутри `coEvery` для `suspend`-функций.
- Альтернатива захвату — матчер `match { it.name == "Алиса" }` прямо в `verify`. Слот удобнее, когда нужно несколько ассертов по одному аргументу.

**Итог:** слот превращает мок из «проверки факта вызова» в «проверку содержимого аргумента» — это мост между mock-стилем и assertion-стилем.

## Q6. (!) Как мокать suspend-функции через coEvery и coVerify

`suspend`-функции нельзя вызывать вне корутины, поэтому обычные `every` / `verify` для них не годятся. MockK даёт co-префиксные аналоги, которые открывают корутинный контекст внутри блока.

```kotlin
val repo = mockk<UserRepository>() // методы могут быть suspend

coEvery { repo.fetchUser(1) } returns User(1, "Алиса")
coEvery { repo.fetchAll() } throws IOException("сеть недоступна")

runTest {
    val service = UserService(repo)
    val name = service.loadName(1) // внутри вызывает suspend fetchUser

    assertEquals("Алиса", name)
}

coVerify { repo.fetchUser(1) }
coVerify(exactly = 1) { repo.fetchAll() }
```

Соответствие функций:

| Обычная | Для `suspend` |
|---------|---------------|
| `every` | `coEvery` |
| `verify` | `coVerify` |
| `verifyOrder` | `coVerifyOrder` |
| `answers` | `coAnswers` |

**Подвох:** если по ошибке написать `every` (без `co`) для `suspend`-метода, код не скомпилируется или поведёт себя некорректно — внутри лямбды `every` нет корутинного контекста. Сам тест при этом должен запускаться в `runTest`, чтобы было где исполнять `suspend`-вызовы тестируемого кода.

## Q7. Как подключить MockKExtension в JUnit 5

`MockKExtension` интегрирует MockK с жизненным циклом JUnit 5: инициализирует моки по аннотациям и сам чистит их после тестов.

```kotlin
@ExtendWith(MockKExtension::class)
class UserServiceTest {

    @MockK
    lateinit var repo: UserRepository       // строгий мок

    @RelaxedMockK
    lateinit var logger: Logger             // relaxed-мок

    @MockK(relaxUnitFun = true)
    lateinit var metrics: Metrics           // дефолты только для Unit-функций

    @InjectMockKs
    lateinit var service: UserService       // моки внедряются в SUT

    @Test
    fun `возвращает имя пользователя`() {
        every { repo.findById(1) } returns User(1, "Алиса")
        assertEquals("Алиса", service.getName(1))
    }
}
```

Что даёт расширение:

- Аннотации `@MockK`, `@RelaxedMockK`, `@SpyK`, `@InjectMockKs` вместо ручного `mockk<>()` в `@BeforeEach`.
- Моки можно объявлять и как параметры тест-функций.
- В `@AfterAll` расширение само вызывает `unmockkAll` и `clearAllMocks`, оставляя окружение чистым.

Альтернатива без расширения — `MockKAnnotations.init(this)` в `@BeforeEach`. Расширение чище: меньше boilerplate и гарантированная очистка.

## Q8. (!) Почему MockK мокает final-классы, а Mockito буксует

В Kotlin классы и методы по умолчанию `final` — чтобы сделать класс наследуемым, нужно явно писать `open`. Это сознательное решение языка (предпочтение композиции наследованию), но оно ломает классический мокинг.

Mockito исторически мокал через генерацию подкласса в рантайме (CGLIB/ByteBuddy-прокси). Подкласс нельзя создать от `final`-класса — значит, «голый» Mockito такие классы не мокает. Лечится подключением `mockito-inline` (mock-maker-inline), который через инструментацию байткода умеет подменять и `final`.

`MockK` изначально построен на инструментации байткода (агент/инлайн-mock-maker), поэтому `final` для него не препятствие — не нужны ни `open`, ни дополнительные зависимости.

```kotlin
class PaymentGateway {            // final по умолчанию
    fun charge(amount: Int): Boolean = TODO()
}

val gateway = mockk<PaymentGateway>()        // MockK — ок
every { gateway.charge(100) } returns true   // работает без open
```

```kotlin
// Mockito без mockito-inline на таком классе упадёт:
// "Cannot mock/spy class ... it is a final class"
```

**Итог:** в Java мок-фреймворки рассчитаны на нефинальные по умолчанию классы; в Kotlin всё наоборот. MockK спроектирован под эту реальность, поэтому работает без настроек, а Mockito требует inline-mock-maker.

## Q9. Как мокать top-level, extension- и static-функции

Top-level и extension-функции в Kotlin компилируются в статические методы JVM в классе-обёртке (`<ИмяФайла>Kt`). Обычный `mockk<>()` мокает только инстанс-методы объекта, а статику — нет. Для неё есть `mockkStatic`, для `object` — `mockkObject`.

Top-level функция (например, в файле `Utils.kt`):

```kotlin
// Utils.kt
fun currentTimeMillis(): Long = System.currentTimeMillis()
```

```kotlin
mockkStatic("com.example.UtilsKt") // имя файла + суффикс Kt
every { currentTimeMillis() } returns 1_700_000_000_000L
```

Extension-функция мокается так же — по классу-обёртке файла, где она объявлена.

`object` (синглтон) и его методы:

```kotlin
object FeatureFlags {
    fun isEnabled(name: String): Boolean = TODO()
}

mockkObject(FeatureFlags)
every { FeatureFlags.isEnabled("new-ui") } returns true
```

Java-статика:

```kotlin
mockkStatic(UUID::class)
every { UUID.randomUUID() } returns UUID.fromString("...")
```

**Подвох:** статические и object-моки глобальны — после теста обязательно снимайте их (`unmockkStatic`, `unmockkObject` или `unmockkAll`), иначе мок «протечёт» в соседние тесты. Mockito этого почти не умеет (нужен PowerMock), что и делает MockK предпочтительным для Kotlin-кода с top-level/extension-функциями.

## Q10. В чём разница clearAllMocks, unmockkAll и confirmVerified

Три функции с похожими именами решают разные задачи — на собеседовании любят про это спросить.

| Функция | Что делает |
|---------|-----------|
| `clearAllMocks()` | сбрасывает у моков застабленное поведение и историю вызовов, но объект остаётся моком |
| `unmockkAll()` | снимает глобальные моки (`mockkStatic`, `mockkObject`), возвращая настоящие реализации |
| `confirmVerified(mock)` | проверяет, что все вызовы мока были покрыты `verify` (нет «забытых») |

```kotlin
@AfterEach
fun tearDown() {
    clearAllMocks()  // чистим состояние между тестами
}

// в конце теста — строгая проверка, что ничего не упустили
verify { repo.save(any()) }
confirmVerified(repo) // упадёт, если был ещё какой-то непроверенный вызов
```

Ключевое различие: `clearAllMocks` работает с *состоянием* моков (поведение/история), а `unmockkAll` — с *самим фактом* подмены глобальных функций и объектов. После `mockkStatic`/`mockkObject` нужен именно `unmockkAll` (или точечный `unmockk*`), потому что `clearAllMocks` глобальную подмену не снимет.

`confirmVerified` усиливает строгость: вместе с `verify` он гарантирует, что тест описал *все* взаимодействия с моком, а не только удобные.

## Q11. (!) Какие стили спецификаций есть в Kotest и чем они различаются

`Kotest` уникален тем, что предлагает несколько стилей написания тестов (spec styles). Функциональной разницы между ними нет — это вопрос вкуса и читаемости; все поддерживают одинаковую конфигурацию (теги, lifecycle и т.д.).

```kotlin
// StringSpec — минимум синтаксиса: строка + лямбда
class CalcStringSpec : StringSpec({
    "1 + 2 должно быть 3" { (1 + 2) shouldBe 3 }
})

// FunSpec — тесты через функцию test("...")
class CalcFunSpec : FunSpec({
    test("сложение") { (1 + 2) shouldBe 3 }
    context("группа") { test("ещё один") { true shouldBe true } }
})

// ShouldSpec — как FunSpec, но ключевое слово should
class CalcShouldSpec : ShouldSpec({
    should("складывать числа") { (1 + 2) shouldBe 3 }
})

// BehaviorSpec — BDD: given / when / then
class CalcBehaviorSpec : BehaviorSpec({
    given("калькулятор") {
        `when`("складываем 1 и 2") {
            then("получаем 3") { (1 + 2) shouldBe 3 }
        }
    }
})

// DescribeSpec — BDD в стиле describe / it / context (как Jest/RSpec)
class CalcDescribeSpec : DescribeSpec({
    describe("сложение") {
        it("даёт 3 для 1 и 2") { (1 + 2) shouldBe 3 }
    }
})
```

Краткий ориентир по выбору:

| Стиль | Когда удобен |
|-------|--------------|
| `StringSpec` | простые unit-тесты, минимум церемоний |
| `FunSpec` | привычно тем, кто шёл от JUnit/ScalaTest |
| `ShouldSpec` | читаемые «should …» формулировки |
| `BehaviorSpec` | BDD given/when/then, общение с аналитиками |
| `DescribeSpec` | команды с опытом Jest/RSpec/Mocha |

**Итог:** стиль выбирают под культуру команды; внутри проекта лучше держаться одного, чтобы тесты выглядели единообразно.

## Q12. Как устроены matchers Kotest (shouldBe, shouldThrow)

`Kotest` поставляет богатую библиотеку matchers — это инфикс-функции и расширения, читаемые как естественный язык. Их можно использовать даже без фреймворка Kotest, отдельно от JUnit.

```kotlin
// равенство и базовые проверки
result shouldBe 42
result shouldNotBe null
name.shouldStartWith("Ал")
list shouldHaveSize 3
list shouldContain "Алиса"
value.shouldBeInstanceOf<User>()

// числа, диапазоны
score shouldBeGreaterThan 0
ratio.shouldBeBetween(0.0, 1.0, tolerance = 0.001)

// исключения — shouldThrow возвращает само исключение
val ex = shouldThrow<IllegalArgumentException> {
    parseAge("-5")
}
ex.message shouldBe "Возраст не может быть отрицательным"

// shouldThrowAny — любое исключение; shouldNotThrowAny — что код не падает
shouldNotThrowAny { service.healthCheck() }
```

Особенности:

- Matchers составляются цепочками и группируются через `assertSoftly { ... }` — тогда тест собирает все провалившиеся проверки сразу, а не падает на первой.
- Сообщения об ошибках информативны: показывают expected/actual в человекочитаемом виде.
- Есть тематические модули matchers: для коллекций, строк, дат, JSON и т.д.

`shouldThrow<T>` лучше try/catch: он одновременно проверяет тип исключения и возвращает объект для дальнейших ассертов по сообщению или полям.

## Q13. (!) Что такое property-based testing и как его делать через checkAll и Arb

Property-based testing (PBT) проверяет не отдельные примеры, а *свойства*, которые должны выполняться для множества случайно сгенерированных входов. Вместо «для 2 и 3 функция вернёт 5» формулируется «для любых a и b сложение коммутативно».

В Kotest этим занимаются `forAll` (свойство возвращает `Boolean`) и `checkAll` (внутри обычные ассерты), а данные поставляют генераторы `Arb` (arbitrary).

```kotlin
class AdditionSpec : StringSpec({

    "сложение коммутативно" {
        checkAll(Arb.int(), Arb.int()) { a, b ->
            (a + b) shouldBe (b + a)
        }
    }

    "длина конкатенации = сумме длин" {
        checkAll(Arb.string(), Arb.string()) { s1, s2 ->
            (s1 + s2).length shouldBe (s1.length + s2.length)
        }
    }

    // forAll возвращает Boolean
    "абсолют неотрицателен" {
        forAll(Arb.int()) { n -> abs(n.toLong()) >= 0 }
    }
})
```

Что важно знать:

- По умолчанию каждый property-тест прогоняется на 1000 сгенерированных примеров.
- `Arb` имеет фабрики: `Arb.int(2..10)`, `Arb.string()`, `Arb.list(Arb.int())`, `Arb.enum<Color>()`, плюс комбинаторы `map`, `filter`, `bind`.
- При провале Kotest делает **shrinking** — сжимает упавший вход до минимального контрпримера (например, не «случайное 982347», а «0» или «-1»), что резко упрощает диагностику.

**Trade-off:** PBT хорош для алгоритмов, парсеров, сериализации (round-trip), инвариантов. Он не заменяет example-based тесты для конкретных бизнес-кейсов и сложнее в отладке флака из-за случайности (для воспроизводимости фиксируют seed).

## Q14. Как писать data-driven тесты через withData

Data-driven (table-driven) тесты прогоняют один и тот же сценарий на наборе фиксированных входных данных. В Kotest это `withData` — каждый элемент превращается в отдельный тест с собственным результатом в отчёте.

```kotlin
class IsEvenSpec : FunSpec({

    context("чётность чисел") {
        withData(2, 4, 6, 100) { n ->
            (n % 2 == 0) shouldBe true
        }
    }

    // с явными именами кейсов через map
    context("валидация email") {
        withData(
            mapOf(
                "обычный"        to ("a@b.com" to true),
                "без собаки"     to ("ab.com" to false),
                "пустой"         to ("" to false),
            )
        ) { (email, expected) ->
            isValidEmail(email) shouldBe expected
        }
    }
})
```

Отличия от обычного цикла `forEach` внутри одного теста:

- Каждый кейс — отдельный узел в отчёте: видно, какой именно вход упал, остальные продолжают выполняться.
- Имена кейсов берутся из ключей `map` или из data-класса (можно настроить через `withData(nameFn = ...)`).
- Аналог JUnit 5 `@ParameterizedTest`, но без аннотаций и провайдеров аргументов — всё в коде теста.

**Итог:** `withData` устраняет копипасту похожих тестов и даёт гранулярную отчётность, в отличие от `forEach`, который падает на первом же неверном кейсе и скрывает остальные.

## Q15. Какие lifecycle hooks есть в Kotest

`Kotest` даёт развитую систему хуков жизненного цикла на уровне спецификации и отдельных тестов — аналог `@BeforeEach`/`@AfterAll` из JUnit, но с большей гранулярностью.

```kotlin
class RepoSpec : FunSpec({

    beforeSpec { /* один раз перед всеми тестами спеки */ }
    afterSpec  { /* один раз после всех тестов спеки */ }

    beforeTest { testCase -> /* перед каждым тестом */ }
    afterTest  { (testCase, result) -> /* после каждого, есть результат */ }

    beforeEach { /* перед каждым leaf-тестом */ }
    afterEach  { /* после каждого leaf-теста */ }

    test("пример") { 1 shouldBe 1 }
})
```

Тонкости, о которых спрашивают:

- `beforeTest` / `beforeAny` срабатывают только перед `TestType.Test` (листовыми тестами), а `beforeEach` — перед контейнерами и тестами. Для динамических тестов (property/data) это различие особенно важно.
- Хуки можно выносить в `TestListener` / `Extension` и переиспользовать между спеками — например, общая очистка БД.
- Для property-тестов есть отдельные `beforeProperty` / `afterProperty`.

Размещать общую инициализацию (поднятие контейнера, очистка состояния) лучше в `beforeSpec`/`afterSpec`, чтобы не платить за неё на каждом тесте, а сброс изменяемого состояния — в `beforeTest`/`beforeEach`.

## Q16. Чем Kotest отличается от JUnit 5

Это частый vs-вопрос. Оба — фреймворки запуска тестов, но Kotest предлагает заметно больше «из коробки» и Kotlin-идиоматичный синтаксис.

| Критерий | Kotest | JUnit 5 |
|----------|--------|---------|
| Стили тестов | 10+ спецификаций (String/Fun/Behavior/Describe…) | один: классы с `@Test` |
| Ассерты | встроенные matchers (`shouldBe`, `shouldThrow`) | `Assertions.assertEquals` или внешний AssertJ |
| Property-based | встроено (`checkAll`, `Arb`, shrinking) | нет, нужен jqwik |
| Data-driven | `withData` | `@ParameterizedTest` + провайдеры |
| Корутины | нативно: тело теста — `suspend`-лямбда | нужен `runTest` вручную |
| Экосистема | моложе, меньше интеграций | стандарт индустрии, max поддержки IDE/CI |
| Многоплатформенность | Kotlin Multiplatform | только JVM |

Когда что выбирать:

- **Kotest** — чистый Kotlin-проект, нужны property-тесты и BDD-стили, KMP-таргеты.
- **JUnit 5** — смешанный Java/Kotlin-код, упор на стабильность, максимум IDE/CI-интеграций и знакомство команды.

Важно: они не взаимоисключающие. MockK работает и с Kotest, и с JUnit 5; Kotest умеет запускаться через JUnit Platform. Многие проекты берут JUnit 5 как раннер, а matchers и property-тесты подключают из Kotest-assertions.

## Q17. Что даёт kotlin.test и assertFailsWith

`kotlin.test` — это тонкая стандартная библиотека ассертов от JetBrains. Её плюс — мультиплатформенность: одни и те же `assertEquals`/`assertTrue` работают на JVM, JS и Native, делегируя на нижележащий движок (JUnit, TestNG и т.д.).

```kotlin
import kotlin.test.*

@Test
fun testAdd() {
    assertEquals(5, add(2, 3))
    assertTrue(isValid("a@b.com"))
    assertNotNull(repo.findById(1))
    assertContains(listOf(1, 2, 3), 2)
}
```

`assertFailsWith` — идиоматичный способ проверить исключение: он утверждает, что блок бросит указанный тип, и возвращает само исключение для дальнейших проверок.

```kotlin
@Test
fun testNegativeAge() {
    val ex = assertFailsWith<IllegalArgumentException> {
        parseAge("-5")
    }
    assertEquals("Возраст не может быть отрицательным", ex.message)
}
```

Это Kotlin-аналог JUnit 5 `assertThrows` и Kotest `shouldThrow`. Преимущество над try/catch очевидно: тест не пройдёт молча, если исключение *не* бросилось, и читается линейно.

**Когда брать:** `kotlin.test` уместен для лёгких или KMP-проектов, где не нужен богатый DSL Kotest, но хочется кроссплатформенные ассерты поверх любого раннера.

## Q18. (!) Как тестировать корутины через runTest и TestDispatcher

Тестировать `suspend`-код напрямую нельзя — нужен корутинный контекст. Библиотека `kotlinx-coroutines-test` даёт `runTest`, который запускает тело в `TestScope` и управляет *виртуальным временем*: задержки не ждут по-настоящему.

```kotlin
@Test
fun testLoad() = runTest {
    val service = DataService()
    val result = service.loadWithDelay() // внутри delay(10_000) — виртуально
    assertEquals("данные", result)       // тест мгновенный, не ждёт 10 секунд
}
```

Два диспетчера определяют, как планируются дочерние корутины:

| Диспетчер | Поведение |
|-----------|-----------|
| `StandardTestDispatcher` | дочерние корутины НЕ стартуют сразу; нужно явно продвинуть время |
| `UnconfinedTestDispatcher` | дочерние стартуют немедленно (eager), удобно для простых тестов |

По умолчанию `runTest` использует `StandardTestDispatcher`, поэтому фоновые корутины нужно «прокрутить»:

```kotlin
@Test
fun testFireAndForget() = runTest {
    val vm = MyViewModel()
    vm.startBackgroundWork() // launch внутри — ещё не выполнилось

    advanceUntilIdle()       // прогоняет все корутины до завершения

    assertTrue(vm.isDone)
}
```

Важные правила:

- В тесте должен быть **один** `TestCoroutineScheduler`, общий для всех `TestDispatcher`. Внутри `runTest` его берут из `testScheduler` и передают в дополнительно создаваемые диспетчеры — иначе виртуальное время не синхронизируется.
- Продакшен-код не должен хардкодить `Dispatchers.IO`/`Default`. Внедряйте диспетчер (через конструктор), чтобы в тесте подставить `TestDispatcher` — иначе виртуальным временем не поуправляешь.
- `advanceUntilIdle()` — выполнить всё; `advanceTimeBy(ms)` — продвинуть на конкретный интервал; `runCurrent()` — выполнить уже запланированное на текущий момент.

**Подвох:** старый API (`runBlockingTest`, `TestCoroutineDispatcher`) устарел — на собеседовании ждут `runTest` + `TestScope` (актуально с coroutines 1.6+).

## Q19. (!) Как тестировать Flow с помощью Turbine

`Flow` — холодный поток, и тестировать его через `toList()` неудобно: бесконечные или зависящие от времени потоки так не проверишь. `Turbine` (`app.cash.turbine`) превращает push-модель Flow в pull-модель suspend-функций.

```kotlin
// зависимость: testImplementation("app.cash.turbine:turbine:...")

@Test
fun testFlow() = runTest {
    flowOf("один", "два").test {
        assertEquals("один", awaitItem())
        assertEquals("два", awaitItem())
        awaitComplete()
    }
}
```

Ключевые функции внутри `test { }`:

- `awaitItem()` — дождаться следующего значения и вернуть его.
- `awaitComplete()` — дождаться нормального завершения потока.
- `awaitError()` — дождаться и вернуть исключение.
- `cancelAndIgnoreRemainingEvents()` — оборвать подписку, не проверяя хвост (для бесконечных потоков).
- `expectNoEvents()` — убедиться, что эмиссий пока не было.

```kotlin
@Test
fun testStateFlow() = runTest {
    val vm = CounterViewModel()
    vm.state.test {
        assertEquals(0, awaitItem())     // начальное состояние
        vm.increment()
        assertEquals(1, awaitItem())     // после события
        cancelAndIgnoreRemainingEvents() // StateFlow бесконечен
    }
}
```

Почему Turbine, а не `toList()`:

- Жёсткая модель: каждый ожидаемый `awaitItem`/`awaitComplete` строго проверяется. Если придёт не то событие или истечёт таймаут (по умолчанию 3 секунды) — тест падает с понятным `AssertionError`.
- Работает с горячими и бесконечными потоками (`StateFlow`, `SharedFlow`), где `toList()` просто завис бы.

**Итог:** Turbine делает тестирование Flow детерминированным и читаемым, особенно в связке с `runTest`, который контролирует виртуальное время.

## Q20. Как использовать Testcontainers в Kotlin-тестах

`Testcontainers` поднимает реальные зависимости (PostgreSQL, Kafka, Redis) в Docker-контейнерах на время тестов — это интеграционные тесты против настоящей БД, а не моков. В Kotlin есть пара идиоматичных приёмов.

Через JUnit 5 и `companion object`:

```kotlin
@Testcontainers
class UserRepositoryIT {

    companion object {
        @Container
        @JvmStatic // важно: @Container на static-поле — один контейнер на класс
        val postgres = PostgreSQLContainer("postgres:16-alpine")
    }

    @Test
    fun `сохраняет и читает пользователя`() {
        val ds = dataSourceFor(postgres.jdbcUrl, postgres.username, postgres.password)
        val repo = UserRepository(ds)
        repo.save(User(1, "Алиса"))
        assertEquals("Алиса", repo.findById(1)?.name)
    }
}
```

Kotlin-нюансы:

- `@Container` на статическом поле = **один контейнер на класс** (запускается раз, переиспользуется тестами). В Kotlin статика живёт в `companion object`, поэтому нужны `@JvmStatic` и `@Container` на нём. На уровне instance-поля контейнер пересоздаётся для каждого теста — медленнее.
- Удобный трюк со Spring Boot: специальный JDBC-URL `jdbc:tc:postgresql:16-alpine:///dbname` поднимает контейнер автоматически без явного объявления `@Container` — этот подход используется в данном проекте (`application-test.yml`).
- Часто заводят базовый абстрактный класс (`AbstractPostgresRepositoryTest`) с одним контейнером на JVM и `TRUNCATE` между тестами — это убирает накладные расходы на повторный старт.

**Trade-off:** Testcontainers требуют запущенного Docker-демона и стартуют дольше unit-тестов (секунды на поднятие). Зато ловят то, что моки не видят: реальные SQL-диалекты, миграции, транзакции, ограничения БД. Это сдвиг по «пирамиде тестирования» вверх — таких тестов должно быть меньше, чем unit.

---

## See also

- [Kotlin: вопросы на собеседовании](kotlin-interview.md) — базовый язык, null-safety, классы, функции
- [Корутины Kotlin](kotlin-coroutines-interview.md) — `suspend`, `launch`/`async`, диспетчеры, structured concurrency
- [Flow в Kotlin](kotlin-flow-interview.md) — холодные/горячие потоки, операторы, `StateFlow`/`SharedFlow`
- [JUnit: вопросы на собеседовании](../../testing/junit-interview.md) — жизненный цикл, аннотации, параметризованные тесты
- [Mockito: вопросы на собеседовании](../../testing/mockito-interview.md) — мокинг в Java, спаи, `ArgumentCaptor`
- [Testcontainers: вопросы на собеседовании](../../testing/testcontainers-interview.md) — контейнеры для интеграционных тестов
- [Unit-тестирование](../../testing/unit-testing-interview.md) — принципы, пирамида тестирования, изоляция
