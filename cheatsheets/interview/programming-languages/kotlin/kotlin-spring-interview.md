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
updated: "2026-04-25"
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
> - [x] Правильный ответ | Корректное описание концепции с конкретным механизмом и use-case.
> - [ ] Альтернативное решение которое не подходит | Почему ошибка в этом подходе ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.
> - [ ] Другая альтернатива с критическим недостатком | Это смежное, но отличное понятие ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.
> - [ ] Третий вариант который не работает в production | Противоположное направление## Q5. Как использовать Kotlin Coroutines с Spring WebFlux? ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.

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
> - [x] Правильный ответ | Корректное описание концепции с конкретным механизмом и use-case.
> - [ ] Альтернативное решение которое не подходит | Почему ошибка в этом подходе ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.
> - [ ] Другая альтернатива с критическим недостатком | Это смежное, но отличное понятие ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.
> - [ ] Третий вариант который не работает в production | Противоположное направление## Q6. Что такое CoroutineCrudRepository? ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.

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
> - [x] Правильный ответ | Корректное описание концепции с конкретным механизмом и use-case.
> - [ ] Альтернативное решение которое не подходит | Почему ошибка в этом подходе ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.
> - [ ] Другая альтернатива с критическим недостатком | Это смежное, но отличное понятие ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.
> - [ ] Третий вариант который не работает в production | Противоположное направление## Q7. Как тестировать Kotlin Spring приложения? ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.

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
> - [x] Правильный ответ | Корректное описание концепции с конкретным механизмом и use-case.
> - [ ] Альтернативное решение которое не подходит | Почему ошибка в этом подходе ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.
> - [ ] Другая альтернатива с критическим недостатком | Это смежное, но отличное понятие ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.
> - [ ] Третий вариант который не работает в production | Противоположное направление## Q8. Как работает null-safety в Kotlin + Spring? ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.

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
> - [x] Правильный ответ | Корректное описание концепции с конкретным механизмом и use-case.
> - [ ] Альтернативное решение которое не подходит | Почему ошибка в этом подходе ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.
> - [ ] Другая альтернатива с критическим недостатком | Это смежное, но отличное понятие ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.
> - [ ] Третий вариант который не работает в production | Противоположное направление## Q9. Как использовать Kotlin DSL для конфигурации Spring? ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.

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
> - [x] Правильный ответ | Корректное описание концепции с конкретным механизмом и use-case.
> - [ ] Альтернативное решение которое не подходит | Почему ошибка в этом подходе ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.
> - [ ] Другая альтернатива с критическим недостатком | Это смежное, но отличное понятие ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.
> - [ ] Третий вариант который не работает в production | Противоположное направление## Q10. Как декларативный контроллер на Kotlin выглядит с validation? ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.

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
> - [x] Правильный ответ | Корректное описание концепции с конкретным механизмом и use-case.
> - [ ] Альтернативное решение которое не подходит | Почему ошибка в этом подходе ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.
> - [ ] Другая альтернатива с критическим недостатком | Это смежное, но отличное понятие ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.
> - [ ] Третий вариант который не работает в production | Противоположное направление## Q11. Как обрабатывать исключения в Kotlin Spring приложении? ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.

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
> - [x] Правильный ответ | Корректное описание концепции с конкретным механизмом и use-case.
> - [ ] Альтернативное решение которое не подходит | Почему ошибка в этом подходе ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.
> - [ ] Другая альтернатива с критическим недостатком | Это смежное, но отличное понятие ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.
> - [ ] Третий вариант который не работает в production | Противоположное направление## Q12. Что такое extension functions в контексте Spring? ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.

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
> - [x] Правильный ответ | Корректное описание концепции с конкретным механизмом и use-case.
> - [ ] Альтернативное решение которое не подходит | Почему ошибка в этом подходе ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.
> - [ ] Другая альтернатива с критическим недостатком | Это смежное, но отличное понятие ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.
> - [ ] Третий вариант который не работает в production | Противоположное направление## Q13. Как интегрировать Kotlin с Spring Security? ❌ ПОСЛЕДСТВИЕ: антипаттерн деградирует SLA при росте нагрузки или зависимостей.

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
> - [x] Правильный ответ | Корректное описание концепции с конкретным механизмом и use-case.
> - [ ] Альтернативное решение которое не подходит | Почему ошибка в этом подходе ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.
> - [ ] Другая альтернатива с критическим недостатком | Это смежное, но отличное понятие ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.
> - [ ] Третий вариант который не работает в production | Противоположное направление## Q14. Какие типичные ошибки при использовании Kotlin + Spring? ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.

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
> - [x] Правильный ответ | Корректное описание концепции с конкретным механизмом и use-case.
> - [ ] Альтернативное решение которое не подходит | Почему ошибка в этом подходе ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.
> - [ ] Другая альтернатива с критическим недостатком | Это смежное, но отличное понятие ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.
> - [ ] Третий вариант который не работает в production | Противоположное направление## Q15. В чём преимущества и недостатки Kotlin для Spring Boot проектов? ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.

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

## See also


> [!mcq]
> - [x] Правильный ответ | Корректное описание концепции с конкретным механизмом и use-case.
> - [ ] Альтернативное решение которое не подходит | Почему ошибка в этом подходе ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.
> - [ ] Другая альтернатива с критическим недостатком | Это смежное, но отличное понятие ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.
> - [ ] Третий вариант который не работает в production | Противоположное направление- [Kotlin](kotlin-interview.md) — основы языка, null safety, data classes ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.
- [Kotlin Coroutines](kotlin-coroutines-interview.md) — suspend функции, CoroutineScope
- [Kotlin Flow](kotlin-flow-interview.md) — реактивные потоки, StateFlow/SharedFlow
- [Spring Boot](../../frameworks/spring/spring-boot-interview.md) — auto-configuration, Spring Boot Starter
- [Spring WebFlux](../../frameworks/spring/spring-webflux-interview.md) — реактивный стек с Kotlin Coroutines
- [Spring R2DBC](../../frameworks/spring/spring-r2dbc-interview.md) — CoroutineCrudRepository
- [Spring Security](../../frameworks/spring/spring-security-interview.md) — интеграция с Kotlin
- [Spring Data JPA](../../frameworks/spring/spring-data-jpa-interview.md) — data class vs @Entity проблема
- [Kotlin DSL](kotlin-dsl-interview.md) — Router Functions, Bean DSL
- [Kotlin/Java Interop](kotlin-interop-java-interview.md) — миграция с Java на Kotlin
