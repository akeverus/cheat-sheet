---
title: "Вопросы на собеседовании: Kotlin + Spring"
description: "Kotlin со Spring Boot: kotlin-spring plugin, open-классы, data class vs @Entity, CoroutineCrudRepository, WebFlux с suspend, DSL, extension functions"
tags:
  - interview
  - kotlin
  - kotlin-spring-interview
type: "interview"
difficulty: "intermediate"
aliases:
  - "Вопросы на собеседовании"
  - "Kotlin + Spring"
  - "Kotlin Spring interview"
  - "Kotlin backend interview"
prerequisites:
  - "[[kotlin-spring]]"
next: []
updated: "2026-05-15"
---
# Вопросы на собеседовании: `Kotlin + Spring`

`Kotlin + Spring Boot` — популярная альтернатива Java для backend-разработки. Kotlin даёт null-safety, data classes, corotines для реактивного программирования. Часто спрашивается на Kotlin/JVM интервью в современных продуктовых компаниях.

Дата последнего обновления: 2026-04-20

## Полезные ссылки

### Официальная документация

- [Spring + Kotlin Docs](https://docs.spring.io/spring-framework/reference/languages/kotlin.html) — официальная документация
- [Baeldung: Kotlin with Spring](https://www.baeldung.com/spring-boot-kotlin) — практическое введение
- [Spring Boot Kotlin Tutorial](https://spring.io/guides/tutorials/spring-boot-kotlin/) — официальный туториал

## Содержание

- [Полезные ссылки](#полезные-ссылки)
- [See also](#see-also)

## Q1. Какие плагины нужны для использования Kotlin со Spring?

```kotlin
// build.gradle.kts
plugins {
    kotlin("jvm") version "2.0.0"
    kotlin("plugin.spring") version "2.0.0"       // open Spring-бинов
    kotlin("plugin.jpa") version "2.0.0"           // no-arg конструктор для @Entity
    kotlin("plugin.allopen") version "2.0.0"       // кастомные открытия
    id("org.springframework.boot") version "3.2.0"
    id("io.spring.dependency-management") version "1.1.4"
}
```

Объяснение плагинов:
- **`kotlin-spring`** — делает все Spring-аннотированные классы (`@Service`, `@Configuration` и т.п.) открытыми (иначе нельзя создать CGLIB proxy для AOP).
- **`kotlin-jpa`** — генерирует no-arg конструктор для `@Entity` классов (JPA требует).
- **`kotlin-allopen`** — позволяет открывать классы по произвольным аннотациям:

```kotlin
allOpen {
    annotation("jakarta.persistence.Entity")
    annotation("org.springframework.stereotype.Component")
}
```


> [!mcq]
>
> **Вопрос:** Зачем в Kotlin+Spring проекте нужен плагин `kotlin-spring` (он же `all-open` для Spring-аннотаций)?
>
> ---
>
> #### A) Он автоматически добавляет `@Autowired` ко всем конструкторам, чтобы Spring видел зависимости — ❌ Неверно
>
> **Что на самом деле:** `@Autowired` на единственном конструкторе в Spring 4.3+ не требуется вообще — это работает и в Java, и в Kotlin без плагинов. `kotlin-spring` тут ни при чём, он занимается совсем другой проблемой — открытием классов.
> **Откуда путаница:** часть разработчиков считает, что плагины «магически делают DI», смешивая независимые механизмы.
> **Если бы это было правдой:** мы бы не могли явно ставить `@Autowired` на сеттер или поле — а это вполне работающие сценарии.
>
> ---
>
> #### B) Он подставляет no-arg конструктор в каждый `@Service`, чтобы Spring мог инстанцировать его рефлексией — ❌ Неверно
>
> **Что на самом деле:** no-arg конструктор генерирует другой плагин — `kotlin-jpa` (и только для `@Entity`/`@MappedSuperclass`/`@Embeddable`). Для Spring-бинов Spring сам вызывает primary конструктор с инжектированными аргументами, no-arg не нужен.
> **Откуда путаница:** оба плагина из семейства `kotlin-*` и часто включаются вместе, легко перепутать их роли.
> **Если бы это было правдой:** Spring инжектил бы зависимости через сеттеры/поля после no-arg вызова, теряя immutability `val`.
>
> ---
>
> #### C) Он делает классы с Spring-аннотациями (`@Component`, `@Service`, `@Configuration`...) `open`, чтобы CGLIB смог создать subclass-proxy для AOP, `@Transactional`, `@Async` — ✓ Верно
>
> **Развёрнутое объяснение:** В Kotlin классы по умолчанию `final`. CGLIB-proxy (которые Spring использует для `@Transactional`, `@Async`, `@Cacheable`, security-аспектов) создаются как subclass с переопределением методов — а наследовать `final` класс нельзя. Плагин `kotlin-spring` (под капотом — `all-open` с предустановленным списком Spring-аннотаций) автоматически снимает `final` с таких классов и их методов на этапе компиляции. Без плагина получите `Cannot subclass final class ... @Transactional method 'X' is final and cannot be proxied` при старте контекста или первом вызове proxied-метода.
> **Пример:**
> ```kotlin
> // build.gradle.kts
> plugins {
>     kotlin("plugin.spring") version "2.0.0"
> }
>
> // Без плагина это final class — CGLIB-proxy невозможен
> @Service
> class PaymentService {
>     @Transactional
>     fun charge(orderId: Long) { /* ... */ }   // proxied method
> }
> ```
> **Когда применять:** во всех Spring Boot проектах на Kotlin — это стандарт, добавляется в стартеры `spring-boot-starter` для Kotlin.
> **Подводные камни:** интерфейсные proxy (JDK dynamic) не требуют `open`, но если бин не реализует интерфейс — Spring падает на CGLIB; кастомные мета-аннотации, наследующие `@Component`, тоже нужно добавлять в `allOpen { annotation(...) }`.
> **Связанные вопросы:** [[Q4]] — `kotlin-jpa` плагин, [[Q14]] — типичные ошибки с `final`.
>
> ---
>
> #### D) Он включает Kotlin reflection для Spring, без него DI не находит beans — ❌ Неверно
>
> **Что на самом деле:** Spring сканирует beans через стандартный JVM-classpath и Java-рефлексию, ему не нужен `kotlin-reflect` для DI. `kotlin-reflect` нужен для `KClass`, `KFunction` и других Kotlin-specific reflection API — но это отдельная зависимость, а не задача `kotlin-spring`.
> **Откуда путаница:** «плагин Spring для Kotlin» звучит как «всё, что нужно для интеграции», и легко приписать ему любые функции.
> **Если бы это было правдой:** убрав плагин, мы бы получили ошибку `bean not found`, а не реальную `cannot subclass final class`.

## Q2. Чем отличается работа с Dependency Injection в Kotlin?

```kotlin
// В Kotlin используется constructor injection — идеально для immutable зависимостей
@Service
class OrderService(
    private val repository: OrderRepository,
    private val eventPublisher: ApplicationEventPublisher
) {
    fun createOrder(request: CreateOrderRequest): Order { ... }
}
```

```kotlin
// @Autowired больше не нужен (один конструктор = автовыбор)
// Альтернатива — field injection (НЕ рекомендуется)
@Service
class BadService {
    @Autowired
    private lateinit var repository: OrderRepository  // требует lateinit для non-null
}
```

**Правило**: используйте primary constructor с `private val` для DI. Это immutable, легко тестируется, явная документация зависимостей.


> [!mcq]
>
> **Вопрос:** Какой способ инжектирования зависимостей в Kotlin-сервис считается идиоматичным для Spring Boot и почему?
>
> ---
>
> #### A) Field injection через `@Autowired` с `lateinit var` — даёт максимум гибкости и работает «как в Java» — ❌ Неверно
>
> **Что на самом деле:** Field injection с `lateinit var` ломает immutability, скрывает зависимости от компилятора и затрудняет unit-тестирование без Spring-контекста — нужно либо подменять рефлексией, либо использовать `@InjectMocks`. Spring-команда официально не рекомендует field injection с 4.x.
> **Откуда путаница:** в старых туториалах поле с `@Autowired` — самый частый пример, поэтому привычка переносится из Java.
> **Если бы это было правдой:** unit-тесты можно было бы писать без боли, но на практике без рефлексии не выйдет даже подсунуть мок — это и есть симптом плохого дизайна.
>
> ---
>
> #### B) Constructor injection через primary constructor с `private val` — зависимости immutable, явные, легко тестируются — ✓ Верно
>
> **Развёрнутое объяснение:** Kotlin primary-конструктор естественно подходит под constructor injection: `class OrderService(private val repo: OrderRepository)` — это и есть конструктор и поле одновременно. `@Autowired` не нужен (Spring 4.3+ автовыбирает единственный конструктор). `val` гарантирует immutability — зависимость не подменишь после старта. В тестах достаточно `OrderService(mockRepo)`, без `@SpringBootTest`. Это базовый стиль в официальной Spring + Kotlin документации.
> **Пример:**
> ```kotlin
> @Service
> class OrderService(
>     private val repository: OrderRepository,
>     private val publisher: ApplicationEventPublisher
> ) {
>     fun create(req: CreateOrderRequest): Order { /* ... */ }
> }
>
> // Unit-тест без Spring context
> val service = OrderService(mockRepo, mockPublisher)
> ```
> **Когда применять:** в любом Spring Boot сервисе на Kotlin. Это стандарт.
> **Подводные камни:** циклические зависимости (A→B, B→A) ломают constructor injection — это намёк на проблему дизайна, а не повод вернуться к `@Autowired`. Решение — рефакторинг или ленивый прокси через `ObjectProvider<T>`.
> **Связанные вопросы:** [[Q14]] — антипаттерны с `lateinit`, [[Q7]] — тестирование.
>
> ---
>
> #### C) Setter injection через `@Autowired set` — позволяет менять зависимости в runtime — ❌ Неверно
>
> **Что на самом деле:** менять зависимости в runtime в Spring-приложении почти всегда плохая идея — это нарушает singleton-семантику, ломает кэширование proxy и делает поведение непредсказуемым. Setter injection оправдан только для опциональных зависимостей в legacy-сценариях.
> **Откуда путаница:** «гибкость» звучит привлекательно, но в DI-контейнере гибкость даёт `@Profile`, `@Conditional`, а не изменяемые сеттеры.
> **Если бы это было правдой:** Spring бы предлагал sсеттеры как default — но primary рекомендация в документации именно constructor injection.
>
> ---
>
> #### D) Inline-инжектирование через `applicationContext.getBean(...)` внутри методов сервиса — ❌ Неверно
>
> **Что на самом деле:** Прямое обращение к `ApplicationContext` — это Service Locator anti-pattern. Зависимости становятся невидимы извне, тесты требуют поднимать полный контекст, и DI теряет смысл. Так допустимо обращаться только к динамически выбираемым бинам по `qualifier` в редких случаях.
> **Откуда путаница:** иногда этот стиль встречается в legacy кодовых базах с большим числом условных зависимостей.
> **Если бы это было правдой:** тесты-моки писать было бы невозможно — пришлось бы каждый раз поднимать `ApplicationContext`.

## Q3. Почему `data class` не подходит для JPA @Entity?

```kotlin
// ПРОБЛЕМА: data class нельзя для JPA
@Entity
data class Order(
    @Id @GeneratedValue val id: Long = 0,
    val customerId: String,
    val total: BigDecimal
)
```

Проблемы:
1. **`equals/hashCode`** на основе всех полей → для Hibernate `Order(1, ...).equals(Order(1, ...))` может не совпадать с тем, что ожидает Hibernate.
2. **`copy()`** создаёт новую сущность без связи с БД.
3. **`final` поля** не могут быть заменены proxy Hibernate.

**Решение**: обычный `class` с ручным `equals/hashCode` по ID:

```kotlin
@Entity
class Order(
    @Id @GeneratedValue val id: Long = 0,
    var customerId: String,
    var total: BigDecimal,
    @OneToMany(cascade = [CascadeType.ALL])
    var items: MutableList<OrderItem> = mutableListOf()
) {
    override fun equals(other: Any?): Boolean =
        this === other || (other is Order && id != 0L && id == other.id)

    override fun hashCode(): Int = id.hashCode()

    override fun toString(): String = "Order(id=$id)"
}
```


> [!mcq]
>
> **Вопрос:** Почему использование Kotlin `data class` для JPA-`@Entity` создаёт скрытые проблемы в Hibernate?
>
> ---
>
> #### A) `data class` хранится в БД как одна строка с JSON-полем — нельзя сделать JPA-запросы по индивидуальным полям — ❌ Неверно
>
> **Что на самом деле:** Hibernate не сериализует `data class` как JSON. Маппинг полей в колонки работает обычным образом через `@Column`, и SQL-запросы по полям проходят корректно. Проблема в другом — в семантике `equals`/`hashCode` и в `final` полях.
> **Откуда путаница:** разработчики из NoSQL-мира иногда смешивают модели сериализации и реляционный маппинг.
> **Если бы это было правдой:** мы бы вообще не могли использовать `@OneToMany`, `@JoinColumn` — а они отлично работают.
>
> ---
>
> #### B) `equals`/`hashCode` сгенерированы по всем полям + поля `val` (`final`) делают невозможным lazy-proxy и dirty-checking — ✓ Верно
>
> **Развёрнутое объяснение:** У `data class` `equals`/`hashCode` сгенерированы автоматически по всем primary-constructor полям. Hibernate ожидает, что entity сравниваются по `@Id` — иначе в `Set<Order>` после загрузки lazy-коллекции одни и те же сущности будут давать разный `hashCode` (lazy-поля ещё `null`/proxy). Дальше: `val`-поля компилируются в `final`, а Hibernate создаёт subclass-proxy и должен иметь возможность перехватывать сеттеры для dirty-checking и lazy-load — `final` блокирует это. Третий минус — `copy()` создаёт detached-копию без связи с persistence context, такой объект «зависает» в managed/detached лимбе.
> **Пример:**
> ```kotlin
> // Антипаттерн
> @Entity
> data class Order(
>     @Id @GeneratedValue val id: Long = 0,
>     val total: BigDecimal
> )
>
> // Корректно — обычный class + equals по id
> @Entity
> class Order(
>     @Id @GeneratedValue val id: Long = 0,
>     var total: BigDecimal = BigDecimal.ZERO
> ) {
>     override fun equals(o: Any?) =
>         this === o || (o is Order && id != 0L && id == o.id)
>     override fun hashCode() = id.hashCode()
> }
> ```
> **Когда применять:** правило «обычный class для `@Entity`, data class для DTO» — стандарт в Spring + Kotlin.
> **Подводные камни:** в R2DBC и Spring Data JDBC (без Hibernate proxy) ограничения мягче — там `data class` допустим. Для Hibernate — нет.
> **Связанные вопросы:** [[Q4]] — `kotlin-jpa` плагин для no-arg, [[Q14]] — типичные ошибки.
>
> ---
>
> #### C) `data class` нельзя пометить `@Entity`, потому что компилятор Kotlin запрещает аннотации на data class — ❌ Неверно
>
> **Что на самом деле:** аннотации на `data class` ставятся свободно — `@Entity data class Foo(...)` отлично компилируется. Проблема не в компиляции, а в runtime-семантике Hibernate.
> **Откуда путаница:** некоторые думают, что плагины запрещают такие сочетания.
> **Если бы это было правдой:** проблема бы ловилась compile-time, а не приводила к загадочным багам в коллекциях lazy-загруженных entities.
>
> ---
>
> #### D) Hibernate не поддерживает `data class` вообще — приложение не стартует — ❌ Неверно
>
> **Что на самом деле:** приложение прекрасно стартует, базовые CRUD-операции проходят. Проблемы проявляются в специфичных сценариях: lazy-загрузка, `equals` в `Set`, `copy()`, дочерние proxy. Это и есть самое опасное — баги в production без явной ошибки.
> **Откуда путаница:** некоторые разработчики смешивают «не работает на тривиальном примере» с «работает, но скрытно ломается».
> **Если бы это было правдой:** проблема была бы найдена при первом же запуске — а реально она проявляется только под нагрузкой или с lazy-связями.

## Q4. Как работает Kotlin с конструктором @Entity?

По умолчанию JPA требует публичный no-arg конструктор, а в Kotlin классы `final` и без no-arg. Решение — `kotlin-jpa` plugin:

```kotlin
plugins {
    kotlin("plugin.jpa") version "2.0.0"
}
```

Плагин генерирует невидимый no-arg конструктор для классов с `@Entity`, `@MappedSuperclass`, `@Embeddable`. Конструктор нельзя вызвать из пользовательского кода — только через рефлексию.

```kotlin
// Без kotlin-jpa плагина: ручные default значения
@Entity
class User(
    @Id @GeneratedValue val id: Long = 0,    // default нужен
    var name: String = "",                    // default для no-arg
    var email: String = ""
)
```


> [!mcq]
>
> **Вопрос:** Какую конкретную задачу решает плагин `kotlin-jpa` в Spring Boot проекте на Kotlin?
>
> ---
>
> #### A) Генерирует синтетический no-arg конструктор для `@Entity`, `@MappedSuperclass`, `@Embeddable`, доступный только через reflection — ✓ Верно
>
> **Развёрнутое объяснение:** Спецификация JPA требует, чтобы у entity был public/protected no-arg конструктор — Hibernate использует его при гидратации (загрузке из БД через reflection). Kotlin primary-конструктор обычно требует все аргументы. Плагин `kotlin-jpa` (под капотом — `noarg`) во время компиляции добавляет в bytecode скрытый no-arg конструктор, но не открывает его для прямого вызова из Kotlin-кода — попытка `User()` не скомпилируется. Это аккуратно: API сохраняется, а Hibernate получает то, что ему нужно.
> **Пример:**
> ```kotlin
> // build.gradle.kts
> plugins {
>     kotlin("plugin.jpa") version "2.0.0"
> }
>
> @Entity
> class User(
>     @Id @GeneratedValue val id: Long = 0,
>     var name: String,    // нет default — но плагин всё равно сделает no-arg
>     var email: String
> )
>
> // val u = User() // не скомпилируется
> // Hibernate.newInstance(User::class.java) // работает через reflection
> ```
> **Когда применять:** в любом проекте с Hibernate/JPA на Kotlin — обязательная зависимость.
> **Подводные камни:** плагин включается только для перечисленных аннотаций по умолчанию; для кастомных мета-аннотаций нужно явно добавить `noArg { annotation("com.example.MyEntity") }`. Без плагина обходной путь — default-значения у всех полей (`var name: String = ""`), но это уродует доменную модель.
> **Связанные вопросы:** [[Q1]] — kotlin-spring плагин, [[Q3]] — почему обычный class, не data class.
>
> ---
>
> #### B) Открывает классы `@Entity` (делает их `open`), чтобы Hibernate мог создать lazy-proxy — ❌ Неверно
>
> **Что на самом деле:** Открытие классов — задача `kotlin-allopen` (с предконфигурацией под Spring — `kotlin-spring`). Чтобы lazy-загрузка работала, в Spring Boot starter Kotlin обычно прописывают `allOpen { annotation("jakarta.persistence.Entity") }`. Сам `kotlin-jpa` только генерирует no-arg.
> **Откуда путаница:** оба плагина часто включают вместе, и эффект для разработчика выглядит как «всё стало работать с JPA».
> **Если бы это было правдой:** мы бы не получали ошибку `Cannot subclass final class` без allOpen — но получаем.
>
> ---
>
> #### C) Регистрирует Kotlin-аналоги аннотаций `@Entity` и `@Id` в classpath Spring Data — ❌ Неверно
>
> **Что на самом деле:** Kotlin использует стандартные аннотации из `jakarta.persistence.*` (или `javax.persistence.*` в старых проектах), никаких отдельных Kotlin-аналогов нет. Плагин не трогает аннотации — он работает на уровне bytecode-трансформации.
> **Откуда путаница:** разработчики иногда думают, что под Kotlin нужны «свои» аннотации.
> **Если бы это было правдой:** мы бы импортировали `kotlin.persistence.Entity` — а импортируем стандартный `jakarta.persistence.Entity`.
>
> ---
>
> #### D) Конвертирует `data class` в обычный `class`, чтобы избежать проблем с `equals`/`hashCode` Hibernate — ❌ Неверно
>
> **Что на самом деле:** `data class` остаётся `data class`. Плагин никак не меняет сгенерированные `equals`/`hashCode`. Проблемы из Q3 решаются вручную — переписыванием на обычный class либо переопределением `equals`/`hashCode` по `id`.
> **Откуда путаница:** хотелось бы «магической» автоматической починки, но плагин делает только одну вещь — no-arg конструктор.
> **Если бы это было правдой:** мы бы могли свободно использовать `data class @Entity` без последствий — а на практике это плохо работает в продакшене.

## Q5. Как использовать Kotlin Coroutines с Spring WebFlux?

```kotlin
@RestController
@RequestMapping("/orders")
class OrderController(private val service: OrderService) {

    // suspend функция вместо Mono<Order>
    @GetMapping("/{id}")
    suspend fun getOrder(@PathVariable id: String): Order? =
        service.findById(id)

    // Flow вместо Flux<Order>
    @GetMapping
    fun allOrders(): Flow<Order> =
        service.findAll()

    // Server-Sent Events со streaming
    @GetMapping(produces = [TEXT_EVENT_STREAM_VALUE])
    fun stream(): Flow<Order> =
        service.streamOrders()
}
```

```kotlin
@Service
class OrderService(private val repo: OrderRepository) {

    suspend fun findById(id: String): Order? =
        repo.findById(id).awaitSingleOrNull()

    fun findAll(): Flow<Order> =
        repo.findAll().asFlow()
}
```

Spring WebFlux автоматически адаптирует `suspend` → `Mono`, `Flow` → `Flux`. Не нужно явно вызывать `.toFlux()`.


> [!mcq]
>
> **Вопрос:** Что произойдёт, если объявить `@GetMapping` метод как `suspend fun getOrder(id: String): Order` в Spring WebFlux контроллере?
>
> ---
>
> #### A) Spring выбросит ошибку «suspend functions are not supported in @RestController» при старте — ❌ Неверно
>
> **Что на самом деле:** Spring WebFlux официально поддерживает `suspend` функции с версии 5.2 (2019). Это первоклассная фича для Kotlin — её можно использовать без обёрток и адаптеров. Ошибка возникает только в `spring-webmvc` (синхронный стек), не в WebFlux.
> **Откуда путаница:** разработчики, не работавшие с WebFlux + Kotlin, переносят сюда ограничения старого Spring MVC.
> **Если бы это было правдой:** официальная документация Spring + Kotlin не рекомендовала бы этот стиль повсеместно.
>
> ---
>
> #### B) Метод выполнится в `runBlocking` на event loop потоке Netty и заблокирует обработку других запросов — ❌ Неверно
>
> **Что на самом деле:** Spring WebFlux НЕ оборачивает `suspend` в `runBlocking`. Вместо этого он создаёт `Mono` через `mono { suspendFun() }`-подобный механизм (`CoroutinesUtils`), и запускает корутину в `Dispatchers.Unconfined` с переключением на `Schedulers.parallel()` для блокирующих операций — никакого блокирования event loop.
> **Откуда путаница:** `runBlocking` действительно блокирует, и кажется логичным, что фреймворк «дёшево» использует его. Но это сделало бы WebFlux бесполезным.
> **Если бы это было правдой:** пропускная способность падала бы до уровня blocking-стека — но тесты показывают, что suspend + WebFlux держит десятки тысяч RPS на одном инстансе.
>
> ---
>
> #### C) Spring WebFlux адаптирует `suspend fun` к `Mono<T>` через `CoroutinesUtils`/`mono { }`, запуская корутину в реактивном контексте — ✓ Верно
>
> **Развёрнутое объяснение:** WebFlux distinguishes return types через `HandlerAdapter`. Для `suspend` функций используется специальный `InvocableHandlerMethod` с `CoroutinesUtils.invokeSuspendingFunction(...)` — он создаёт `Mono` из корутины, переходя в `Dispatchers.Unconfined` и поднимая reactor `ContextView` как coroutine context. `Flow<T>` аналогично адаптируется к `Flux<T>`. Это unified подход: для разработчика код выглядит как обычная Kotlin-функция, для рантайма — как `Mono`/`Flux`. Backpressure, cancellation, и context-propagation работают корректно.
> **Пример:**
> ```kotlin
> @RestController
> class OrderController(private val service: OrderService) {
>
>     @GetMapping("/{id}")
>     suspend fun getOrder(@PathVariable id: String): Order? =
>         service.findById(id)  // вернётся как Mono<Order>
>
>     @GetMapping
>     fun all(): Flow<Order> = service.findAll()  // вернётся как Flux<Order>
> }
> ```
> **Когда применять:** во всех новых WebFlux проектах на Kotlin — это рекомендованный стиль вместо ручных `Mono`/`Flux`.
> **Подводные камни:** WebMVC (`spring-webmvc`) не поддерживает `suspend` напрямую до Spring 6+ (а полная поддержка с виртуальными потоками — Spring 6.1+). Внутри `suspend` нельзя вызывать blocking I/O без `withContext(Dispatchers.IO)` — заблокирует worker.
> **Связанные вопросы:** [[Q6]] — CoroutineCrudRepository, [[Q14]] — антипаттерн с runBlocking.
>
> ---
>
> #### D) Метод компилируется, но Spring всегда отдаёт `200 OK` с пустым body — `suspend` несовместим с сериализацией — ❌ Неверно
>
> **Что на самом деле:** возвращаемое значение `suspend` функции сериализуется тем же `HttpMessageConverter`-механизмом — Jackson, Kotlinx Serialization, что угодно. Если `Order` возвращён, в body будет JSON. Если `null` для nullable `Order?` — `404 Not Found` (через `ResponseEntity`-handling).
> **Откуда путаница:** разработчики ожидают, что `Continuation` параметр (под капотом suspend) попадёт в сериализацию — но компилятор Kotlin его прячет в bytecode, а `CoroutinesUtils` снимает в момент адаптации.
> **Если бы это было правдой:** реальные production WebFlux-приложения на Kotlin отдавали бы пустые ответы — а они работают.

## Q6. Что такое CoroutineCrudRepository?

```kotlin
// Spring Data расширение для корутин
interface OrderRepository : CoroutineCrudRepository<Order, Long> {

    // suspend методы — возвращают Order? (nullable)
    suspend fun findByCustomerId(customerId: String): Order?

    // Flow для множественных результатов
    fun findByStatusOrderByCreatedAt(status: OrderStatus): Flow<Order>

    // @Query работает как обычно
    @Query("SELECT * FROM orders WHERE total > :min")
    fun findHighValueOrders(min: BigDecimal): Flow<Order>

    suspend fun countByStatus(status: OrderStatus): Long
}
```

Работает с R2DBC, MongoDB Reactive, Redis Reactive. Под капотом адаптирует `Mono`/`Flux` → suspend/Flow через `kotlinx-coroutines-reactor`.


> [!mcq]
>
> **Вопрос:** Что собой представляет `CoroutineCrudRepository<T, ID>` в Spring Data и поверх какого слоя он работает?
>
> ---
>
> #### A) Это блокирующий репозиторий с `suspend` обёрткой, под капотом вызывает JDBC через `runBlocking` — ❌ Неверно
>
> **Что на самом деле:** `CoroutineCrudRepository` работает поверх реактивных Spring Data модулей (R2DBC, MongoDB Reactive, Cassandra Reactive, Redis Reactive), а не поверх JDBC. Никакого `runBlocking` под капотом — это адаптация `Mono`/`Flux` из реактивного драйвера к `suspend`/`Flow`.
> **Откуда путаница:** JDBC более привычен, и кажется, что репозитории «должны» работать через него.
> **Если бы это было правдой:** под нагрузкой пул соединений быстро бы исчерпался, и реактивные плюсы исчезли.
>
> ---
>
> #### B) Это интерфейс Spring Data поверх реактивных драйверов (R2DBC/MongoDB Reactive), адаптирующий `Mono`/`Flux` к `suspend`/`Flow` через `kotlinx-coroutines-reactor` — ✓ Верно
>
> **Развёрнутое объяснение:** `CoroutineCrudRepository<T, ID>` — наследник `ReactiveCrudRepository<T, ID>`, в котором методы переписаны как `suspend` (для одиночных результатов) и `Flow<T>` (для коллекций). Spring Data при создании прокси-репозитория использует `ReactiveAdapterRegistry`, который через `kotlinx-coroutines-reactor` конвертирует `Mono.awaitSingleOrNull()` → `suspend fun ... : T?` и `Flux.asFlow()` → `fun ...: Flow<T>`. Запросы (производные методы, `@Query`) и транзакции (`@Transactional` с reactive transaction manager) работают идентично.
> **Пример:**
> ```kotlin
> interface OrderRepository : CoroutineCrudRepository<Order, Long> {
>     suspend fun findByCustomerId(customerId: String): Order?
>     fun findByStatus(status: OrderStatus): Flow<Order>
>
>     @Query("SELECT * FROM orders WHERE total > :min")
>     fun findHighValue(min: BigDecimal): Flow<Order>
> }
> ```
> **Когда применять:** в WebFlux + R2DBC проектах на Kotlin. Это default-выбор для нового реактивного стека.
> **Подводные камни:** транзакции требуют `R2dbcTransactionManager` или `ReactiveMongoTransactionManager` — обычный `JpaTransactionManager` не работает. `@Transactional` на suspend-методе работает с Spring 6+; до этого нужен manual `TransactionalOperator`.
> **Связанные вопросы:** [[Q5]] — WebFlux + suspend, [[Q13]] — security с suspend.
>
> ---
>
> #### C) Это альтернатива `JpaRepository` поверх Hibernate с поддержкой корутин — ❌ Неверно
>
> **Что на самом деле:** Hibernate синхронный, основан на JDBC, и не имеет нативной поддержки корутин. Hibernate Reactive (отдельный проект Quarkus/Reactive) использует Mutiny и Vert.x, а не Spring Data Coroutine. `CoroutineCrudRepository` живёт только в реактивных Spring Data модулях.
> **Откуда путаница:** «репозиторий» в большинстве проектов означает JPA, и легко предположить, что Coroutine-вариант тоже про JPA.
> **Если бы это было правдой:** у вас бы работал `@Entity` с `CoroutineCrudRepository` — но эта связка не существует в Spring Data JPA.
>
> ---
>
> #### D) Это маркер-интерфейс, который заставляет Spring Boot стартовать в реактивном режиме автоматически — ❌ Неверно
>
> **Что на самом деле:** Запуск в reactive-режиме определяется наличием `spring-boot-starter-webflux` и отсутствием `spring-boot-starter-web` в classpath, а не наличием репозитория. Spring Data сам по себе не управляет WebApplicationType.
> **Откуда путаница:** есть auto-configuration зависимости, но они идут в обратную сторону — стартеры тянут модули, а не модули определяют стартер.
> **Если бы это было правдой:** добавление одной репозиторной зависимости меняло бы тип всего приложения — это нарушало бы изоляцию модулей.

## Q7. Как тестировать Kotlin Spring приложения?

```kotlin
@SpringBootTest
class OrderServiceTest @Autowired constructor(
    private val service: OrderService,
    private val repository: OrderRepository
) {
    @Test
    fun `should create order`() {           // обратные кавычки для имён с пробелами
        val order = service.create(CreateOrderRequest("customer-1", BigDecimal.TEN))

        assertThat(order.id).isNotZero()
        assertThat(repository.findById(order.id)).isPresent
    }
}
```

```kotlin
// Mockito-Kotlin для удобных mocks
@ExtendWith(MockitoExtension::class)
class OrderControllerTest {
    @Mock lateinit var service: OrderService
    @InjectMocks lateinit var controller: OrderController

    @Test
    fun `should return order`() = runTest {
        val mockOrder = Order(id = 1L, customerId = "c-1", total = BigDecimal.TEN)
        whenever(service.findById("1")).thenReturn(mockOrder)  // mockito-kotlin

        val result = controller.getOrder("1")

        assertThat(result).isEqualTo(mockOrder)
    }
}
```

```kotlin
// Тестирование корутин
@Test
fun `should process order asynchronously`() = runTest {
    val result = service.processAsync(order)  // suspend функция
    assertThat(result).isNotNull
}
```


> [!mcq]
>
> **Вопрос:** Как корректно протестировать suspend-функцию `OrderService.findById(id)` в Kotlin без поднятия Spring-контекста?
>
> ---
>
> #### A) Обернуть вызов в `runBlocking { ... }` внутри `@Test fun` — это блокирует JUnit-поток, но даст результат — ❌ Неверно
>
> **Что на самом деле:** Технически `runBlocking` отработает, но это анти-паттерн в тестах: блокируется тестовый поток (включая `Dispatchers.Main` в Android), нельзя контролировать виртуальное время для `delay`, и тесты с `TestDispatcher` ломаются. В современных тестах для корутин используется `runTest { ... }` из `kotlinx-coroutines-test`.
> **Откуда путаница:** `runBlocking` исторически использовался в первых примерах корутин-тестов.
> **Если бы это было правдой:** тесты, проверяющие тайминги (`delay(1000)`), работали бы в реальном времени и были бы медленными — а `runTest` пропускает виртуальное время мгновенно.
>
> ---
>
> #### B) Запустить тест внутри `runTest { ... }` из `kotlinx-coroutines-test` и использовать `TestDispatcher` для контроля времени — ✓ Верно
>
> **Развёрнутое объяснение:** `runTest` — официальный builder для тестирования корутин: запускает тест в `TestScope`, подменяет `Dispatchers.Main` на `TestDispatcher`, поддерживает виртуальное время (мгновенно «пропускает» `delay`), и автоматически дожидается завершения всех дочерних корутин перед `return`. Внутри можно `assertThrows`, `coVerify` (MockK) для проверки suspend-вызовов, `advanceTimeBy(...)`/`runCurrent()` — для пошагового контроля. Это и есть стандарт для unit-тестов suspend-кода. С MockK suspend-моки делаются через `coEvery { repo.findById(any()) } returns mockOrder`.
> **Пример:**
> ```kotlin
> @Test
> fun `findById returns order from repo`() = runTest {
>     val repo = mockk<OrderRepository>()
>     coEvery { repo.findById("1") } returns Order(id = 1L)
>     val service = OrderService(repo)
>
>     val result = service.findById("1")
>
>     assertThat(result?.id).isEqualTo(1L)
>     coVerify(exactly = 1) { repo.findById("1") }
> }
> ```
> **Когда применять:** для каждого unit-теста suspend-функции, всех корутинных сценариев включая Flow.
> **Подводные камни:** `runTest` не подходит для Flow с горячими источниками (`StateFlow`, `SharedFlow`) — нужен `Turbine` или явный `collect` в `launch { }`. Для интеграционных тестов с реальной БД (`@SpringBootTest`) `runTest` тоже работает, но виртуальное время теряет смысл.
> **Связанные вопросы:** [[Q5]] — suspend контроллеры, [[Q6]] — CoroutineCrudRepository.
>
> ---
>
> #### C) Вызвать `.await()` на результате — он автоматически развернёт suspend в значение — ❌ Неверно
>
> **Что на самом деле:** `await()` — метод `Deferred<T>` (результата `async { }`), а не способ вызова suspend-функций. Вызов `service.findById("1").await()` не скомпилируется — `findById` сам по себе уже suspend, и его нельзя «развернуть» из обычной функции.
> **Откуда путаница:** разработчики из JS-мира переносят семантику `await` на Kotlin.
> **Если бы это было правдой:** suspend можно было бы вызывать из любой функции — и весь смысл `suspend`-маркировки исчез бы.
>
> ---
>
> #### D) Запустить через `GlobalScope.launch { ... }` и сразу проверить ассерт — ❌ Неверно
>
> **Что на самом деле:** `GlobalScope.launch` запускает корутину fire-and-forget на отдельном диспетчере. Тестовый поток продолжается параллельно — ассерт сработает до того, как корутина завершится, тест станет flaky или будет всегда «зелёным» без реальной проверки. К тому же IDE подсветит `GlobalScope` как `@DelicateCoroutinesApi`.
> **Откуда путаница:** `GlobalScope.launch` — самый «короткий» способ запустить корутину, и его легко принять за тестовый сценарий.
> **Если бы это было правдой:** все тестовые суиты с suspend-функциями были бы недетерминированными — но они стабильны при использовании `runTest`.

## Q8. Как работает null-safety в Kotlin + Spring?

Kotlin строго различает nullable и non-null типы. Это помогает, но создаёт сложности при интеграции с Java.

```kotlin
@Service
class UserService(private val repo: UserRepository) {

    // Возвращает nullable — соответствует Optional<User>
    fun findById(id: Long): User? = repo.findById(id).orElse(null)

    // Non-null — throws NoSuchElementException
    fun getById(id: Long): User = repo.findById(id).orElseThrow()

    // Из JPA-репозитория возвращает Optional — адаптируем:
    fun findByEmail(email: String): User? = repo.findByEmail(email).orElse(null)
}
```

```kotlin
// @NotNull / @Nullable в Java → автоматические non-null / nullable в Kotlin
public interface JavaService {
    @Nullable User findUser(String id);   // → Kotlin видит как User?
    @NotNull User getUser(String id);     // → Kotlin видит как User
    User unknown(String id);               // → Kotlin видит как User! (platform type)
}

// Platform types (User!) — опасны, компилятор не знает null-safety
val user: User = javaService.unknown("1")  // может упасть NPE в runtime
```


> [!mcq]
>
> **Вопрос:** Что такое «platform type» в Kotlin при интеграции с Spring/Java и почему это опасно?
>
> ---
>
> #### A) Это особый тип, который Kotlin создаёт только для Spring-бинов — для каждого `@Service` генерируется `User!` — ❌ Неверно
>
> **Что на самом деле:** Platform type — это вообще не «специальный для Spring» механизм. Он возникает каждый раз, когда Kotlin видит Java-метод без аннотаций `@Nullable`/`@NotNull` (или JSR-305 эквивалентов). Spring тут лишь массовый «поставщик» таких методов, потому что большая часть Spring API написана на Java.
> **Откуда путаница:** новички видят `User!` только в Spring-проектах и связывают это с фреймворком.
> **Если бы это было правдой:** platform types появлялись бы только в коде, использующем Spring — но они возникают при работе с любым Java API (Guava, Apache Commons, JDK).
>
> ---
>
> #### B) Это тип `T!` от Java-API без nullability-аннотаций — Kotlin не знает, может ли он быть null, и не делает compile-time проверок — ✓ Верно
>
> **Развёрнутое объяснение:** Когда Java-метод возвращает `String` без `@Nullable`/`@NotNull`, Kotlin видит этот тип как `String!` — это «не nullable и не non-nullable». Компилятор разрешает присвоить такой результат и в `String`, и в `String?`, но без runtime-проверки. Если метод реально вернёт `null`, и вы записали его в `val s: String = ...`, то NPE возникнет в момент первого dereference — иногда далеко от точки присвоения. Это «escape hatch» для совместимости с Java, но он перекладывает ответственность на программиста. Защита — JSR-305 (`@CheckForNull`), Jakarta `@Nullable`/`@NotNull`, или Kotlin-обёртки.
> **Пример:**
> ```kotlin
> // Java side
> public class UserRepo {
>     public User findOne(Long id) { return null; }  // нет аннотации
> }
>
> // Kotlin side
> val user: User = repo.findOne(1L)  // компилируется, но NPE в runtime
> val safe: User? = repo.findOne(1L)  // безопасно
> ```
> **Когда применять:** знать про platform types обязательно при работе с любым Java-API. Лучшая защита — явно объявлять `User?` при работе с Java-методами без аннотаций.
> **Подводные камни:** `JpaRepository.findById(...)` возвращает `Optional<User>` — это не platform type (Optional non-null), но `.get()` без проверки бросит `NoSuchElementException`. Spring добавил KNullness annotations с 6.x, но многие сторонние библиотеки до сих пор без них.
> **Связанные вопросы:** [[Q14]] — типичные ошибки, [[Q3]] — JPA entity nullable полей.
>
> ---
>
> #### C) Это runtime-исключение, которое Kotlin кидает при попытке прочитать nullable значение как non-nullable — ❌ Неверно
>
> **Что на самом деле:** «runtime исключение» — это `NullPointerException` или `KotlinNullPointerException`, но «platform type» — это явление времени компиляции, а не runtime. Это маркер «неизвестная nullability», который влияет на проверки компилятора, а не на runtime behavior.
> **Откуда путаница:** оба связаны с null, и легко смешать compile-time проверки с runtime ошибками.
> **Если бы это было правдой:** код с `User!` падал бы всегда, но он падает только когда реально `null` приходит.
>
> ---
>
> #### D) Это специальный generic-тип для коллекций из Java, чтобы корректно работали `List<T>` ↔ `MutableList<T>` — ❌ Неверно
>
> **Что на самом деле:** Маппинг Java `List<T>` ↔ Kotlin `(Mutable)List<T>` — отдельный механизм mapped types. Platform types работают на уровне nullability одиночных значений, не на уровне коллекций.
> **Откуда путаница:** оба механизма «помогают совместимости с Java» и легко смешиваются.
> **Если бы это было правдой:** platform types обсуждали бы в контексте generics, а не nullability — но в документации Kotlin это именно про nullability.

## Q9. Как использовать Kotlin DSL для конфигурации Spring?

```kotlin
// functional bean DSL (Spring 5+)
@Configuration
class AppConfig {

    @Bean
    fun beans() = beans {
        bean<OrderService>()
        bean { OrderController(ref()) }       // ref() — auto-wiring
        bean<OrderRepository>()

        profile("dev") {
            bean<InMemoryOrderRepository>(primary = true)
        }
    }
}
```

```kotlin
// Router DSL для WebFlux
@Configuration
class RouterConfig {

    @Bean
    fun routes(handler: OrderHandler) = coRouter {
        "/orders".nest {
            GET("", handler::getAll)
            GET("/{id}", handler::getById)
            POST("", handler::create)
            DELETE("/{id}", handler::delete)
        }

        filter { request, next ->
            logger.info("Request: ${request.path()}")
            next(request)
        }
    }
}

@Component
class OrderHandler(private val service: OrderService) {

    suspend fun getAll(request: ServerRequest): ServerResponse =
        ServerResponse.ok().bodyValueAndAwait(service.findAll().toList())

    suspend fun getById(request: ServerRequest): ServerResponse {
        val id = request.pathVariable("id")
        return service.findById(id)
            ?.let { ServerResponse.ok().bodyValueAndAwait(it) }
            ?: ServerResponse.notFound().buildAndAwait()
    }
}
```


> [!mcq]
>
> **Вопрос:** В чём принципиальная разница между `@Bean` методами в `@Configuration` классе и Kotlin Bean DSL (`beans { bean<X>() }`)?
>
> ---
>
> #### A) Bean DSL быстрее работает в runtime, потому что Spring компилирует его в bytecode — ❌ Неверно
>
> **Что на самом деле:** Runtime-производительность DSL и `@Configuration` практически одинакова: оба создают singleton-beans один раз при старте контекста. Bean DSL минимально быстрее на старте (нет CGLIB proxy на `@Configuration` класс), но это микро-оптимизация. Главная разница — в стиле и в model registration.
> **Откуда путаница:** «новый DSL должен быть быстрее старого» — частое предположение, не подкреплённое замерами.
> **Если бы это было правдой:** в production benchmark-ах был бы заметный выигрыш — а его нет.
>
> ---
>
> #### B) `@Configuration` использует annotation-based scanning с reflection, Bean DSL — это functional registration через `BeanDefinitionDsl` без CGLIB-proxy и аннотаций — ✓ Верно
>
> **Развёрнутое объяснение:** `@Configuration` с `@Bean` методами обрабатывается `ConfigurationClassPostProcessor` через reflection; вокруг класса создаётся CGLIB-subclass-proxy, чтобы внутренние вызовы `methodA()` из `methodB()` всё равно возвращали singleton (не создавали новый bean). Bean DSL — это `beans { bean<X>() }` builder, который возвращает `ApplicationContextInitializer<GenericApplicationContext>` и регистрирует beans программно через `context.registerBean(...)`. Никаких аннотаций, никакого component scan, никакого CGLIB — чистый Kotlin-код. Поэтому DSL дружелюбен к Spring Native/AOT (меньше reflection metadata) и легко версионируется как обычный код. Spring 5+ поддерживает оба стиля одновременно.
> **Пример:**
> ```kotlin
> fun beans() = beans {
>     bean<OrderService>()
>     bean { OrderController(ref()) }  // ref<T>() для DI
>     profile("dev") {
>         bean<InMemoryOrderRepo>(primary = true)
>     }
> }
>
> // В main:
> SpringApplication(MyApp::class.java).apply {
>     addInitializers(beans())
> }.run(*args)
> ```
> **Когда применять:** для GraalVM Native Image / AOT-сборок DSL предпочтительнее (меньше runtime-рефлексии), для обычных Spring Boot — на вкус команды.
> **Подводные камни:** DSL не работает напрямую с `@Component` сканированием — нужно либо одно, либо другое; смешивать допустимо, но требует аккуратности. `Profile`/`Conditional` через DSL имеют свой синтаксис.
> **Связанные вопросы:** [[Q1]] — kotlin-spring и open-классы, [[Q13]] — Security DSL.
>
> ---
>
> #### C) Bean DSL и `@Configuration` — это одно и то же, компилятор Kotlin их транслирует в идентичный bytecode — ❌ Неверно
>
> **Что на самом деле:** Это разные API с разными процессорами. `@Configuration` — это аннотация, обработка идёт через `BeanFactoryPostProcessor`. DSL — это вызов `BeanDefinitionDsl.register(...)`, регистрирующий beans напрямую в `GenericApplicationContext`. Компилятор не объединяет их.
> **Откуда путаница:** обе дают одинаковый результат (registered beans), и кажется, что под капотом одно и то же.
> **Если бы это было правдой:** не имело бы смысла иметь оба API в Spring 5.
>
> ---
>
> #### D) Bean DSL не поддерживает `@Profile` и `@Conditional`, нужно использовать `@Configuration` для этого — ❌ Неверно
>
> **Что на самом деле:** DSL поддерживает profiles напрямую: `profile("dev") { bean<X>() }`. Условная регистрация — через стандартные `Environment`-проверки в DSL-блоке (`if (env.activeProfiles.contains("dev")) bean<X>()`). Возможностей меньше не становится.
> **Откуда путаница:** документация по DSL короче, и не все возможности очевидны.
> **Если бы это было правдой:** разработчикам Spring пришлось бы выбирать между DSL и profiles — но в реальности оба работают.

## Q10. Как декларативный контроллер на Kotlin выглядит с validation?

```kotlin
@RestController
@RequestMapping("/api/users")
@Validated
class UserController(private val service: UserService) {

    @PostMapping
    fun createUser(@Valid @RequestBody request: CreateUserRequest): UserResponse =
        service.create(request).toResponse()

    @GetMapping("/{id}")
    fun getUser(@PathVariable @Min(1) id: Long): UserResponse =
        service.findById(id)?.toResponse()
            ?: throw UserNotFoundException(id)
}

// Data class как DTO с валидацией
data class CreateUserRequest(
    @field:NotBlank
    @field:Size(min = 3, max = 50)
    val name: String,

    @field:Email
    val email: String,

    @field:Min(18)
    val age: Int
)
```

**Важно**: `@field:` обязателен для валидационных аннотаций на полях data class, иначе аннотация попадает на конструктор/getter, а не поле.


> [!mcq]
>
> **Вопрос:** Зачем в Kotlin data class с Bean Validation нужен префикс `@field:NotBlank` вместо просто `@NotBlank`?
>
> ---
>
> #### A) Префикс `@field:` обязателен по спецификации JSR-380 для всех языков, не только Kotlin — ❌ Неверно
>
> **Что на самом деле:** JSR-380 (Bean Validation 2.0) ничего не знает про Kotlin use-site targets — это спецификация Java. В Java аннотации `@NotBlank` на поле работают как есть, потому что нет неоднозначности «на что повесить». Префикс — это Kotlin-специфичный механизм, нужный из-за множественности целей (field, getter, parameter, setter, property).
> **Откуда путаница:** разработчики думают, что Bean Validation спецификация диктует синтаксис.
> **Если бы это было правдой:** Java-разработчики тоже писали бы `@field:NotBlank` — но они пишут просто `@NotBlank`.
>
> ---
>
> #### B) В Kotlin data class у property есть несколько JVM-targets (field, getter, constructor parameter), и без явного `@field:` аннотация попадёт на конструктор-параметр, который Bean Validation не сканирует — ✓ Верно
>
> **Развёрнутое объяснение:** Kotlin property с primary-constructor параметром `val name: String` компилируется в три JVM-целях: приватное `field name`, public `getName()` getter, и параметр конструктора `(String name)`. Без явного use-site target Kotlin выбирает первый подходящий из стандартного порядка (`param` → `property` → `field`). Для конструктора-параметра это будет `param` — туда и попадёт `@NotBlank`. Hibernate Validator (`@Valid`) сканирует именно field и method annotations, и не видит аннотацию на параметре конструктора → валидация просто не срабатывает. Префикс `@field:` форсирует target на field — туда, где Validator её увидит. Альтернативно работает `@get:NotBlank` (на getter).
> **Пример:**
> ```kotlin
> // НЕ работает — аннотация на параметре конструктора
> data class CreateRequest(@NotBlank val name: String)
>
> // Работает — аннотация на field
> data class CreateRequest(@field:NotBlank val name: String)
>
> @PostMapping
> fun create(@Valid @RequestBody req: CreateRequest) { /* validation сработает */ }
> ```
> **Когда применять:** во всех Kotlin data class, используемых как Request DTO с `@Valid`. Это стандарт.
> **Подводные камни:** Spring 6 / Hibernate Validator 8 начинают поддерживать сканирование конструктор-параметров (`@ValidateOnExecution`), но в подавляющем большинстве проектов всё ещё нужен `@field:`. Для JSON-Schema/Springdoc OpenAPI может потребоваться дополнительно `@get:`.
> **Связанные вопросы:** [[Q14]] — список типичных ошибок, [[Q11]] — обработка `MethodArgumentNotValidException`.
>
> ---
>
> #### C) Префикс `@field:` нужен, чтобы Jackson правильно сериализовал поле в JSON — ❌ Неверно
>
> **Что на самом деле:** Jackson по умолчанию использует getter-based сериализацию (или field-based, если настроено) и ему не нужны use-site targets для своих аннотаций. `@JsonProperty` тоже работает без `@field:` в большинстве случаев. Это две разные подсистемы — сериализация и валидация.
> **Откуда путаница:** все эти аннотации часто стоят рядом в одном data class.
> **Если бы это было правдой:** Jackson был бы недоступен без use-site targets — а он работает «из коробки».
>
> ---
>
> #### D) Без `@field:` Kotlin компилятор выдаст ошибку «cannot resolve annotation target» — ❌ Неверно
>
> **Что на самом деле:** Код компилируется без ошибок. Компилятор молча выбирает default target и не предупреждает разработчика. Это и есть самое опасное — валидация «тихо» не срабатывает, баги доходят до production.
> **Откуда путаница:** разработчики ожидают, что критическая семантическая разница должна ловиться компилятором.
> **Если бы это было правдой:** ошибка с `@NotBlank val name` была бы compile-time — а её нет.

## Q11. Как обрабатывать исключения в Kotlin Spring приложении?

```kotlin
@RestControllerAdvice
class GlobalExceptionHandler {

    @ExceptionHandler(UserNotFoundException::class)
    fun handleNotFound(ex: UserNotFoundException): ResponseEntity<ErrorResponse> =
        ResponseEntity.status(NOT_FOUND).body(
            ErrorResponse("USER_NOT_FOUND", ex.message ?: "User not found")
        )

    @ExceptionHandler(MethodArgumentNotValidException::class)
    fun handleValidation(ex: MethodArgumentNotValidException): ResponseEntity<ErrorResponse> {
        val errors = ex.bindingResult.fieldErrors.associate { it.field to it.defaultMessage }
        return ResponseEntity.badRequest().body(
            ErrorResponse("VALIDATION_FAILED", "Invalid input", details = errors)
        )
    }
}

data class ErrorResponse(
    val code: String,
    val message: String,
    val details: Map<String, Any?>? = null,
    val timestamp: Instant = Instant.now()
)

class UserNotFoundException(id: Long) : RuntimeException("User $id not found")
```


> [!mcq]
>
> **Вопрос:** Какой механизм Spring наиболее подходит для централизованной обработки исключений в REST API на Kotlin, и как он работает?
>
> ---
>
> #### A) Try/catch внутри каждого `@RestController` метода с возвратом `ResponseEntity.status(...)` — самый явный подход — ❌ Неверно
>
> **Что на самом деле:** Try/catch в каждом методе нарушает DRY, размазывает обработку ошибок по контроллерам, и неминуемо приводит к рассогласованию формата ошибки в разных эндпоинтах. Это анти-паттерн в Spring экосистеме.
> **Откуда путаница:** «явное лучше неявного» — здравая идея, но в Spring централизованная обработка не «магическая», а вполне явная — она просто вынесена в отдельный класс.
> **Если бы это было правдой:** в Spring не существовало бы `@ControllerAdvice` — а он есть с версии 3.2 (2012).
>
> ---
>
> #### B) `@RestControllerAdvice` + `@ExceptionHandler(SomeException::class)` — централизованный перехват исключений всех контроллеров с возможностью маппинга на стандартный формат ошибки — ✓ Верно
>
> **Развёрнутое объяснение:** `@RestControllerAdvice` — комбинация `@ControllerAdvice` и `@ResponseBody`. Класс сканируется при старте, методы с `@ExceptionHandler` регистрируются в `HandlerExceptionResolver`. Когда контроллерный метод бросает исключение, Spring DispatcherServlet ищет подходящий handler по типу исключения (с учётом наследования), вызывает его, и результат сериализуется как обычный response body. Можно ограничить scope advice конкретным пакетом/аннотацией (`@RestControllerAdvice(basePackages = ...)`). Дополнительно, для consistent error model используют `ProblemDetail` (RFC 7807, Spring 6+) или собственный `ErrorResponse` data class.
> **Пример:**
> ```kotlin
> @RestControllerAdvice
> class GlobalExceptionHandler {
>     @ExceptionHandler(UserNotFoundException::class)
>     fun handleNotFound(ex: UserNotFoundException): ResponseEntity<ErrorResponse> =
>         ResponseEntity.status(NOT_FOUND).body(
>             ErrorResponse("USER_NOT_FOUND", ex.message ?: "User not found")
>         )
>
>     @ExceptionHandler(MethodArgumentNotValidException::class)
>     fun handleValidation(ex: MethodArgumentNotValidException) =
>         ResponseEntity.badRequest().body(
>             ErrorResponse("VALIDATION", ex.bindingResult.fieldErrors.joinToString())
>         )
> }
> ```
> **Когда применять:** во всех REST API на Spring (MVC и WebFlux). Это стандарт.
> **Подводные камни:** `@RestControllerAdvice` работает только для исключений из `@Controller`-слоя; ошибки в `Filter`-цепочке (security, CORS) обрабатываются раньше и должны ловиться отдельно (`AuthenticationEntryPoint`, `AccessDeniedHandler`). Для WebFlux + suspend исключение нужно бросать из `suspend` функции — `Mono.error(...)` в reactive-цепочке тоже работает.
> **Связанные вопросы:** [[Q10]] — валидация в контроллерах, [[Q13]] — Security ошибки.
>
> ---
>
> #### C) Глобальный `@PostConstruct` метод, который регистрирует error handlers через `WebMvcConfigurer.configureHandlerExceptionResolvers` — ❌ Неверно
>
> **Что на самом деле:** Технически `WebMvcConfigurer.configureHandlerExceptionResolvers` существует, но это низкоуровневый API для исключительных случаев — кастомных resolver'ов. Для бизнес-логики практически никогда не используется; `@ControllerAdvice` идиоматичнее и проще.
> **Откуда путаница:** в документации Spring упоминается оба пути, и кажется, что они равноправны.
> **Если бы это было правдой:** все туториалы рекомендовали бы `WebMvcConfigurer` — но рекомендуют `@ControllerAdvice`.
>
> ---
>
> #### D) Использование `ResponseStatusException`, бросая прямо из контроллера — Spring сам формирует JSON-ответ — ❌ Неверно
>
> **Что на самом деле:** `ResponseStatusException` действительно поддерживается и формирует базовый ответ, но не даёт контроля над форматом ошибки (нет `code`/`details`/`timestamp` полей), и нет места для cross-cutting логирования или метрик. Для прототипа подходит, для продакшена нужен `@RestControllerAdvice`.
> **Откуда путаница:** `ResponseStatusException` упоминают как «удобный shortcut», и легко решить, что этого достаточно.
> **Если бы это было правдой:** мы могли бы получить унифицированный formato ошибки без advice — но default ResponseStatusException JSON минимален и не настраивается.

## Q12. Что такое extension functions в контексте Spring?

```kotlin
// Extension function — добавляет методы существующим классам
fun ServerHttpRequest.authToken(): String? =
    headers["Authorization"]?.firstOrNull()?.removePrefix("Bearer ")

// Использование
@RestController
class SecureController {
    @GetMapping("/me")
    fun me(request: ServerHttpRequest): Mono<User> {
        val token = request.authToken() ?: throw UnauthorizedException()
        return userService.getByToken(token)
    }
}

// Extension на Spring типах
fun <T> Optional<T>.toKotlinNullable(): T? = orElse(null)
fun <T> Mono<T>.logOnError(tag: String): Mono<T> = doOnError { log.error("[$tag]", it) }
```

Extension functions — способ адаптировать Java-based Spring API к более идиоматичному Kotlin.


> [!mcq]
>
> **Вопрос:** Как реально работают Kotlin extension functions в Spring-контексте — на уровне JVM bytecode и как Spring их видит?
>
> ---
>
> #### A) Extension function динамически добавляет метод в class через reflection при первом вызове — ❌ Неверно
>
> **Что на самом деле:** Kotlin не модифицирует existing classes в runtime. Extension function — чисто статическая концепция: компилятор генерирует обычный `static`-метод с receiver-параметром, а на каждом call site подставляет вызов этого статического метода. Никакой динамической модификации классов и никакой reflection.
> **Откуда путаница:** синтаксис `obj.extensionFun()` выглядит как метод класса, и кажется, что класс «расширяется».
> **Если бы это было правдой:** Spring и AOP-proxy могли бы перехватывать extension calls — но они их не видят.
>
> ---
>
> #### B) Это статический метод в синтетическом классе (например, `MyExtensionsKt`) с первым параметром-receiver; на JVM вызов `req.authToken()` компилируется в `MyExtensionsKt.authToken(req)` — ✓ Верно
>
> **Развёрнутое объяснение:** Файл `MyExtensions.kt` с top-level extension `fun ServerHttpRequest.authToken(): String? = ...` компилируется в class `MyExtensionsKt` (имя файла + `Kt` суффикс) со статическим методом `public static String authToken(ServerHttpRequest $this) { ... }`. На call site `request.authToken()` Kotlin генерирует bytecode `INVOKESTATIC MyExtensionsKt.authToken(...)`. Spring и AOP видят только тот класс, на котором они умеют делать proxy — но extension не часть target-класса, поэтому `@Transactional` на extension не работает (метод статический и не в Spring-бине). Зато extension отлично подходит для адаптации Java-API под Kotlin-стиль (например, `Mono.awaitSingle()`).
> **Пример:**
> ```kotlin
> // file: WebExtensions.kt
> fun ServerHttpRequest.authToken(): String? =
>     headers["Authorization"]?.firstOrNull()?.removePrefix("Bearer ")
>
> // call site:
> val token = request.authToken()
> // bytecode: INVOKESTATIC com/example/WebExtensionsKt.authToken
> ```
> **Когда применять:** для адаптации сторонних API, добавления удобных хелперов на Spring-классы без наследования, конвертеров (`.toResponse()` на доменной модели). Идеально для cross-cutting concerns, не требующих DI.
> **Подводные камни:** extension не участвует в полиморфизме (resolved statically) — `parent.foo()` вызовет extension Parent, даже если у Child есть свой. `@Transactional`, `@Async`, `@Cacheable` на extension не работают — это статика вне Spring-бина. Extension не может иметь backing field.
> **Связанные вопросы:** [[Q1]] — open-classes для proxy, [[Q5]] — `awaitSingle()` extension на `Mono`.
>
> ---
>
> #### C) Это макрос Kotlin-компилятора, который inline-ит тело функции прямо на call site без вызова метода — ❌ Неверно
>
> **Что на самом деле:** Extension без `inline` модификатора компилируется в обычный вызов статического метода, никакого inlining. С `inline fun ServerHttpRequest.authToken()` — да, тело подставляется на call site, но это эффект `inline`, а не extension. Это разные ортогональные механизмы.
> **Откуда путаница:** оба генерируют «удобный» bytecode, и легко смешать.
> **Если бы это было правдой:** обычные extension не появлялись бы в stack trace — а они появляются как `MyExtensionsKt.authToken`.
>
> ---
>
> #### D) Extension — это псевдоним для interface method, который Kotlin генерирует через mixin — ❌ Неверно
>
> **Что на самом деле:** В Kotlin нет mixin'ов как в Ruby/Scala. Extension — отдельный механизм статических методов с syntactic sugar. Никаких интерфейсов компилятор не добавляет к receiver-класс.
> **Откуда путаница:** «расширение классов» звучит похоже на Ruby mixin или Scala implicit class.
> **Если бы это было правдой:** receiver class имел бы дополнительные методы в reflection — а `Class.getMethods()` для `ServerHttpRequest` не покажет extension.

## Q13. Как интегрировать Kotlin с Spring Security?

```kotlin
@Configuration
@EnableWebSecurity
class SecurityConfig {

    @Bean
    fun filterChain(http: HttpSecurity): SecurityFilterChain = http {
        // Kotlin DSL для HttpSecurity
        csrf { disable() }
        authorizeHttpRequests {
            authorize("/api/public/**", permitAll)
            authorize("/api/admin/**", hasRole("ADMIN"))
            authorize(anyRequest, authenticated)
        }
        oauth2ResourceServer {
            jwt { }
        }
    }
}

// Получение текущего пользователя в контроллере
@GetMapping("/me")
fun me(@AuthenticationPrincipal jwt: Jwt): Map<String, Any> = mapOf(
    "sub" to jwt.subject,
    "roles" to (jwt.getClaimAsStringList("roles") ?: emptyList<String>())
)

// В coroutine контексте
@GetMapping("/me-reactive")
suspend fun meReactive(): User {
    val auth = ReactiveSecurityContextHolder.getContext()
        .awaitSingle()
        .authentication
    return userService.findByUsername(auth.name)
}
```


> [!mcq]
>
> **Вопрос:** Как получить текущего authenticated user в `suspend` контроллере WebFlux + Spring Security?
>
> ---
>
> #### A) Использовать `SecurityContextHolder.getContext().authentication` напрямую — это standard Java способ — ❌ Неверно
>
> **Что на самом деле:** `SecurityContextHolder` использует `ThreadLocal` для хранения контекста. В корутинах поток меняется на каждом suspension point, и `ThreadLocal` теряет значение. Это базовая несовместимость blocking security model и suspend-кода. В WebFlux + Coroutines нужно использовать `ReactiveSecurityContextHolder`.
> **Откуда путаница:** разработчики переносят опыт из Spring MVC + Java.
> **Если бы это было правдой:** мы получали бы корректного пользователя на любом потоке — но при первом `delay` контекст теряется.
>
> ---
>
> #### B) Использовать `@AuthenticationPrincipal` параметр в контроллере или `ReactiveSecurityContextHolder.getContext().awaitSingle()` в suspend-коде — ✓ Верно
>
> **Развёрнутое объяснение:** Spring Security WebFlux хранит `SecurityContext` в reactor `ContextView` (не в `ThreadLocal`). Через `kotlinx-coroutines-reactor` этот контекст автоматически прокидывается в coroutine context при адаптации `suspend` ↔ `Mono`. Доступ из suspend-кода: `ReactiveSecurityContextHolder.getContext().awaitSingle()` (или `awaitSingleOrNull()` если может быть пустым). Ещё проще — параметр `@AuthenticationPrincipal jwt: Jwt` (или `OidcUser`, `UserDetails`) в сигнатуре контроллера, Spring сам извлечёт principal из reactive контекста и передаст. Это идиоматичный путь.
> **Пример:**
> ```kotlin
> @RestController
> class MeController(private val userService: UserService) {
>
>     @GetMapping("/me")
>     suspend fun me(@AuthenticationPrincipal jwt: Jwt): UserResponse =
>         userService.findByUsername(jwt.subject).toResponse()
>
>     // Альтернатива через ReactiveSecurityContextHolder
>     @GetMapping("/me-alt")
>     suspend fun meAlt(): UserResponse {
>         val auth = ReactiveSecurityContextHolder.getContext()
>             .awaitSingle().authentication
>         return userService.findByUsername(auth.name).toResponse()
>     }
> }
> ```
> **Когда применять:** во всех reactive/coroutine контроллерах с Spring Security.
> **Подводные камни:** Spring Security DSL для Kotlin (`http { authorizeHttpRequests { ... } }`) живёт в `org.springframework.security.config.annotation.web.invoke` — нужно `import` корректно. Для тестов нужны `@WithMockUser` или `WebTestClient.mutateWith(SecurityMockServerConfigurers.mockUser())`.
> **Связанные вопросы:** [[Q5]] — suspend + WebFlux, [[Q6]] — CoroutineCrudRepository.
>
> ---
>
> #### C) Передавать `Principal` через параметр контроллера из CoroutineContext через `currentCoroutineContext()` — ❌ Неверно
>
> **Что на самом деле:** `currentCoroutineContext()` возвращает coroutine context (Job, Dispatcher, etc.), но Spring Security элементы там лежат под специальным ключом `ReactorContext` и доступ через них требует именно `ReactiveSecurityContextHolder`, который инкапсулирует эту логику. Прямой доступ к `currentCoroutineContext()[ReactorContext]` теоретически возможен, но это низкоуровневый и хрупкий путь.
> **Откуда путаница:** разработчики ищут «coroutine-native» способ доступа.
> **Если бы это было правдой:** Spring Security предоставлял бы такой шорткат — но рекомендованное API именно `ReactiveSecurityContextHolder`.
>
> ---
>
> #### D) Делать `runBlocking { SecurityContextHolder.getContext() }` чтобы вынуть из старого `ThreadLocal` — ❌ Неверно
>
> **Что на самом деле:** В WebFlux `SecurityContextHolder` (blocking) попросту пуст — security-цепочка никогда не клала туда значения. `runBlocking` ничего не вернёт. Это путаница двух моделей security.
> **Откуда путаница:** новички пытаются «починить» проблему обёртками.
> **Если бы это было правдой:** Spring предлагал бы такие хаки в документации — но рекомендация ровно противоположная.

## Q14. Какие типичные ошибки при использовании Kotlin + Spring?

1. **`data class` для `@Entity`** — см. Q3. Используйте обычный класс.

2. **Забытый `kotlin-spring` plugin** — классы `final`, Spring не может создать proxy:

```
Error: @Transactional method 'X' is final and cannot be proxied
```

3. **Поля без `@field:` в data class-валидациях** — аннотация попадает не на поле:

```kotlin
// ПЛОХО — аннотация на конструкторе
data class Req(@NotBlank val name: String)

// ХОРОШО — аннотация на поле
data class Req(@field:NotBlank val name: String)
```

4. **Platform types (User!) из Java API** — не проверяются на null:

```kotlin
// repo.findById(id) из JpaRepository возвращает Optional<User>
val user: User = repo.findById(1L).get()  // если не найдено → NoSuchElementException
```

5. **Использование `lateinit` вместо конструктор-injection** — усложняет тестирование.

6. **`runBlocking` в реактивном контроллере** — блокирует event loop:

```kotlin
// ПЛОХО
@GetMapping
fun getOrders() = runBlocking { orderService.findAll() }

// ХОРОШО — suspend функция напрямую
@GetMapping
suspend fun getOrders(): List<Order> = orderService.findAll()
```


> [!mcq]
>
> **Вопрос:** Что произойдёт, если использовать `runBlocking { ... }` внутри `suspend`-метода WebFlux контроллера для обхода чисто реактивного API?
>
> ---
>
> #### A) Spring сам обнаружит `runBlocking` и переключит вызов на блокирующий пул `Schedulers.boundedElastic()` — ❌ Неверно
>
> **Что на самом деле:** Spring не делает никакой автоматической инспекции тела suspend-функции. Он адаптирует suspend ↔ Mono на уровне границы метода, не зная, что происходит внутри. `runBlocking` блокирует ровно тот поток, на котором был вызван — обычно это Netty event-loop поток (`reactor-http-nio-*`), которых всего по числу ядер.
> **Откуда путаница:** разработчики ожидают, что фреймворк защитит от ошибок.
> **Если бы это было правдой:** не было бы legendary post-mortem про `runBlocking` в WebFlux. Но они есть — это classic production-инцидент.
>
> ---
>
> #### B) Поток Netty event-loop заблокируется, остальные запросы на этом потоке встанут в очередь, throughput резко упадёт, в логах появятся `BlockHound` warnings (если включён) — ✓ Верно
>
> **Развёрнутое объяснение:** WebFlux работает на Netty event-loop, обычно `2 * Runtime.availableProcessors()` потоков. Каждый поток обслуживает тысячи параллельных запросов через non-blocking I/O. `runBlocking { ... }` создаёт новый scope и блокирует вызывающий поток до завершения тела — на этот период один из event-loop потоков выпадает. Если в `runBlocking` есть медленный I/O или `delay`, throughput падает в N раз (N — число потоков event-loop). В стрессовых сценариях это приводит к серверу, который держит 100 connections вместо 10000. Включение Reactor BlockHound в dev-режиме помогает ловить такие места: `BlockHoundError: Blocking call! ...` при попытке `Thread.sleep` или `IO.read` на reactor-потоке.
> **Пример:**
> ```kotlin
> // ПЛОХО — блокирует Netty thread
> @GetMapping
> suspend fun bad(): List<Order> =
>     runBlocking { orderRepo.findAll().toList() }
>
> // ХОРОШО — естественный suspend, никакого runBlocking
> @GetMapping
> suspend fun good(): List<Order> =
>     orderRepo.findAll().toList()
> ```
> **Когда применять:** **никогда** не использовать `runBlocking` в WebFlux/reactive контексте. Для адаптации blocking-кода в suspend — `withContext(Dispatchers.IO) { blockingCall() }`.
> **Подводные камни:** Suspend-функция уже coroutine context, ей просто не нужен `runBlocking`. Если приходится вызывать blocking JDBC из реактивного контроллера — `withContext(Dispatchers.IO)` или вынос в `@Async`-сервис, но лучшее решение — мигрировать на R2DBC.
> **Связанные вопросы:** [[Q5]] — suspend в WebFlux, [[Q6]] — CoroutineCrudRepository.
>
> ---
>
> #### C) Код просто не скомпилируется — Kotlin запрещает вложенные coroutine builders — ❌ Неверно
>
> **Что на самом деле:** Kotlin не запрещает `runBlocking` внутри `suspend` функции (хотя IDE даёт warning «runBlocking inside a coroutine»). Код прекрасно собирается и работает. Это runtime-проблема, не compile-time.
> **Откуда путаница:** надежда, что компилятор поймает плохие практики.
> **Если бы это было правдой:** не было бы статей и блогов о том, как искать `runBlocking` в production-коде — а они есть.
>
> ---
>
> #### D) Сработает CoroutineContext propagation, и блокировка перейдёт в фоновый поток без вреда — ❌ Неверно
>
> **Что на самом деле:** `runBlocking` создаёт **новый** event-loop в текущем потоке и блокирует его до завершения. Это не "переключение", это создание blocking scope поверх существующего. Никакая propagation тут не помогает.
> **Откуда путаница:** название `runBlocking` звучит как «запустить с блокированием в отдельном scope, не трогая текущий».
> **Если бы это было правдой:** `runBlocking` был бы безопасен везде — и не было бы предупреждений в документации.

## Q15. В чём преимущества и недостатки Kotlin для Spring Boot проектов?

**Преимущества**:
- **Null safety** — меньше NPE в runtime.
- **Data classes** — DTO без бойлерплейта.
- **Coroutines** — простота вместо `Mono`/`Flux` цепочек.
- **Extension functions** — удобная адаптация API.
- **Kotlin DSL** — чистая конфигурация (beans, routes, security).
- **100% совместимость с Java** — постепенная миграция.
- **Smart casts** — меньше явных приведений типов.

**Недостатки**:
- **Плагины для JPA/Spring** — без них классы не работают.
- **Platform types** — компилятор не знает nullability Java API.
- **Скорость компиляции** — обычно медленнее Java (улучшается с K2).
- **Меньше сообщество** — для Spring 80% материалов на Java.
- **Некоторые библиотеки** не идеально интегрируются (например, некоторые Mockito фичи).

**Практика**: современные Spring Boot проекты выигрывают от Kotlin, особенно с WebFlux + Coroutines. Для legacy Java проектов — постепенная миграция по модулям.


> [!mcq]
>
> **Вопрос:** Какое главное практическое преимущество Kotlin для Spring Boot проекта по сравнению с Java, и есть ли реальная цена?
>
> ---
>
> #### A) Kotlin полностью устраняет Spring boilerplate — больше не нужны `@Service`, `@Configuration`, `@Bean` — ❌ Неверно
>
> **Что на самом деле:** Все стандартные Spring-аннотации остаются. Kotlin сокращает boilerplate Java-уровня (геттеры/сеттеры/конструкторы через data class, `val`-поля как DI), но не Spring-аннотации — без них фреймворк не понимает, что регистрировать как bean. Bean DSL — альтернатива, но не замена.
> **Откуда путаница:** маркетинг иногда обещает «no-boilerplate», и легко решить, что Spring-аннотации тоже исчезнут.
> **Если бы это было правдой:** Spring Boot Kotlin проекты не имели бы `@Service`/`@Configuration` — а они везде.
>
> ---
>
> #### B) Главные преимущества — null-safety на уровне типов, data class для DTO, suspend/Flow для реактивного кода без чейнов Mono/Flux, extension functions, DSL для бинов и security; ценой идёт необходимость плагинов (kotlin-spring, kotlin-jpa) и осторожность с platform types и `final`-методами — ✓ Верно
>
> **Развёрнутое объяснение:** Kotlin даёт реальные технические выигрыши в Spring-проектах: (1) `User?` vs `User` устраняет целый класс багов NPE; (2) `data class CreateRequest(...)` заменяет 50 строк Java DTO; (3) `suspend fun get(id): User?` читается линейно, без `.flatMap(...).switchIfEmpty(...).onErrorResume(...)` цепочек; (4) extension functions удобно адаптируют Java-API; (5) DSL для beans/router/security — типобезопасная конфигурация без XML. Цена: обязательные плагины (`kotlin-spring`, `kotlin-jpa`) — без них `@Transactional` ломается с `Cannot subclass final class`; platform types от Java-API скрывают `null`; data class в JPA создаёт скрытые проблемы; некоторые Mockito возможности требуют MockK взамен; medium-sized проектам нужна команда, знакомая с обоими языками.
> **Пример:**
> ```kotlin
> // 1 строка вместо Java DTO с геттерами/equals/hashCode/toString
> data class CreateUserRequest(@field:NotBlank val name: String, @field:Email val email: String)
>
> // suspend читается линейно
> @PostMapping
> suspend fun create(@Valid @RequestBody req: CreateUserRequest): UserResponse =
>     userService.create(req).toResponse()
> ```
> **Когда применять:** новые Spring Boot проекты — Kotlin почти всегда выигрышен; legacy Java — постепенная миграция по модулям; embedded/нативные сборки требуют осторожности с reflection.
> **Подводные камни:** компиляция Kotlin медленнее Java (улучшается с K2 в 2.0+); Spring AOT/Native имеет тонкости с reflection в Kotlin metadata; командам без опыта корутин может быть тяжело отлаживать suspend-stack traces.
> **Связанные вопросы:** [[Q1]]-[[Q4]] — плагины и их роль, [[Q14]] — типичные ошибки.
>
> ---
>
> #### C) Kotlin делает Spring приложение в 2 раза быстрее за счёт нативной компиляции — ❌ Неверно
>
> **Что на самом деле:** Kotlin компилируется в обычный JVM bytecode и runtime-производительность сопоставима с Java (часто чуть медленнее на 1-3% из-за дополнительных null-проверок и synthetic bridges). Нативная компиляция — это отдельная фича GraalVM Native Image, доступная и для Java, и для Kotlin.
> **Откуда путаница:** Kotlin Multiplatform поддерживает native targets, но это не runtime для JVM-Spring.
> **Если бы это было правдой:** все Spring-проекты на Java мигрировали бы на Kotlin за неделю — но реальные benchmarks показывают схожие цифры.
>
> ---
>
> #### D) Kotlin несовместим со Spring Boot 3.x и работает только до Spring Boot 2.x — ❌ Неверно
>
> **Что на самом деле:** Spring Boot 3.x официально поддерживает Kotlin как first-class язык: kotlin-spring/kotlin-jpa плагины обновлены, suspend полностью интегрирован, Spring Native поддерживает Kotlin. На главной странице Spring.io есть туториалы именно для Spring Boot 3 + Kotlin.
> **Откуда путаница:** иногда путают версии Kotlin compiler и Spring.
> **Если бы это было правдой:** все Kotlin-Spring проекты были бы legacy — а они активно растут.

## See also

- [Kotlin](kotlin-interview.md) — основы языка, null safety, data classes
- [Kotlin Coroutines](kotlin-coroutines-interview.md) — suspend функции, CoroutineScope
- [Kotlin Flow](kotlin-flow-interview.md) — реактивные потоки, StateFlow/SharedFlow
- [Spring Boot](../../frameworks/spring/spring-boot-interview.md) — auto-configuration, Spring Boot Starter
- [Spring WebFlux](../../frameworks/spring/spring-webflux-interview.md) — реактивный стек с Kotlin Coroutines
- [Spring R2DBC](../../frameworks/spring/spring-r2dbc-interview.md) — CoroutineCrudRepository
- [Spring Security](../../frameworks/spring/spring-security-interview.md) — интеграция с Kotlin
- [Spring Data JPA](../../frameworks/spring/spring-data-jpa-interview.md) — data class vs @Entity проблема
- [Kotlin DSL](kotlin-dsl-interview.md) — Router Functions, Bean DSL
- [Kotlin/Java Interop](kotlin-interop-java-interview.md) — миграция с Java на Kotlin
