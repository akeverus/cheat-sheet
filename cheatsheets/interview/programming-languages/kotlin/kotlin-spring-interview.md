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

`Kotlin + Spring Boot` — основная альтернатива Java для backend на JVM. Главные выгоды: null-safety на уровне типов (меньше NPE), `data class` для DTO без бойлерплейта и корутины вместо цепочек `Mono`/`Flux` в реактивном стеке. Цена за это — пара компиляторных плагинов (`kotlin-spring`, `kotlin-jpa`), без которых Spring и Hibernate просто не запустятся. Тема часто всплывает на Kotlin/JVM-собеседованиях в продуктовых компаниях.

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
    val kotlinVersion = "2.2.0"                          // версии здесь — пример,
    kotlin("jvm") version kotlinVersion                  // ветку сверяйте с актуальной
    kotlin("plugin.spring") version kotlinVersion        // open Spring-бинов
    kotlin("plugin.jpa") version kotlinVersion           // no-arg конструктор для @Entity
    kotlin("plugin.allopen") version kotlinVersion       // кастомные открытия
    id("org.springframework.boot") version "3.5.0"
    id("io.spring.dependency-management") version "1.1.7"
}
```

**Суть:** Spring проксирует бин либо JDK-прокси по интерфейсам, либо подклассом через CGLIB (в Spring Boot `proxyTargetClass = true` по умолчанию, поэтому обычно именно подкласс), Hibernate — через ByteBuddy (он стал дефолтным вместо Javassist в 5.3; cglib-провайдер там объявлен устаревшим в 4.x и удалён в 5.x), а для `@Entity` нужен ещё и no-arg конструктор. Но в Kotlin классы по умолчанию `final`, и no-arg конструктора у них нет. Плагины снимают эти ограничения автоматически — иначе пришлось бы вручную помечать каждый класс `open` и дописывать default-значения.

Что делает каждый плагин:
- **`kotlin-spring`** — делает `open` все классы со Spring-аннотациями (`@Service`, `@Configuration`, `@Transactional` и т.п.). Открытый класс сам по себе прокси не получает: подкласс Spring строит только там, где бин попадает под advice. Без него final-класс нельзя проксировать подклассом: `@Configuration` с дефолтным `proxyBeanMethods = true` роняет старт сообщением «@Configuration class ... may not be final», а бин под AOP (`@Transactional`, `@Async`, `@Cacheable`) — `AopConfigException: Could not generate CGLIB subclass`. Классу без advice прокси не нужен, и финальность ему не мешает. Final-метод в открытом классе CGLIB переопределить не может: старт при этом не падает, Spring лишь пишет сообщение на уровне INFO (если метод реализует интерфейс) или DEBUG, а advice на таком методе не срабатывает — при дефолтных уровнях логирования это вообще не видно.
- **`kotlin-jpa`** — генерирует синтетический no-arg конструктор для классов с `@Entity`, `@MappedSuperclass`, `@Embeddable`. Без него сущность придётся готовить вручную: либо задать значения по умолчанию всем параметрам конструктора, либо дописать пустой вторичный конструктор — иначе JPA не создаст экземпляр через рефлексию.
- **`kotlin-allopen`** — обобщение: делает классы `open` по любым аннотациям, которые вы перечислите сами. По сути `kotlin-spring` — это `allopen`, заранее настроенный на Spring-аннотации.

```kotlin
allOpen {
    annotation("jakarta.persistence.Entity")
    annotation("com.example.OpenForTesting")   // свои аннотации; Spring-овые уже покрыты kotlin-spring
}
```

## Q2. Чем отличается работа с Dependency Injection в Kotlin?

В Kotlin зависимости внедряют через первичный конструктор с `private val`. Это естественный для языка способ: `val` даёт immutable-зависимости, а единственный конструктор Spring выбирает автоматически — `@Autowired` над ним писать не нужно (начиная со Spring 4.3).

Field injection (`@Autowired` над свойством) в Kotlin работает плохо: non-null свойство нельзя оставить неинициализированным, поэтому обычно берут `lateinit var` (альтернативы — nullable-свойство или `@set:Autowired`, и обе проигрывают конструктору). Это убирает immutability, мешает тестам (нельзя подставить mock через конструктор) и прячет зависимости от глаз.

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

**Рекомендация:** первичный конструктор с `private val` — выбор по умолчанию. Зависимости immutable, легко подменяются в тестах (передаются прямо в конструктор), а сам список параметров служит явной документацией того, что классу нужно для работы.

## Q3. Почему `data class` не подходит для JPA @Entity?

Коротко: автогенерируемые `equals`/`hashCode`/`copy` у `data class` конфликтуют с жизненным циклом JPA-сущности, у которой идентичность определяется первичным ключом, а не значениями всех полей.

```kotlin
// ПРОБЛЕМА: data class нельзя для JPA
@Entity
data class Order(
    @Id @GeneratedValue val id: Long = 0,
    val customerId: String,
    val total: BigDecimal
)
```

Что именно ломается:
1. **`equals`/`hashCode` по всем полям.** JPA считает две сущности одной и той же, если совпадает их ID. Но `data class` сравнивает все свойства первичного конструктора — поэтому одна и та же строка БД до и после изменения `total` будет «не равна сама себе». А раз `hashCode` тоже зависит от изменяемых свойств, сущность ломается внутри `HashSet`/`HashMap`: объект кладут в набор с одним хэшем, потом правят `total` — хэш становится другим, и элемент в наборе больше не находится.
2. **`copy()` создаёт оторванный от БД объект.** Копия выглядит как сущность, но Hibernate её не отслеживает (она не в persistence context) — изменения такой копии молча не сохранятся.
3. **`final` мешает прокси и ленивой загрузке.** Hibernate подменяет сущность прокси-подклассом (ByteBuddy) и инициализирует `@ManyToOne`/lazy-поля по требованию. Kotlin-класс и его геттеры `final` по умолчанию, поэтому подкласс не построить — класс нужно открывать плагином `allopen`, настроенным на `jakarta.persistence.Entity`.

**Решение:** обычный `class` с `var`-полями и ручными `equals`/`hashCode`. Класс открывает плагин `allopen` (настроенный на `jakarta.persistence.Entity`), no-arg конструктор даёт `kotlin-jpa` — без них этот листинг Hibernate не запустит:

```kotlin
@Entity
class Order(
    @Id @GeneratedValue var id: Long = 0,
    var customerId: String,
    var total: BigDecimal,
    @OneToMany(cascade = [CascadeType.ALL])
    var items: MutableList<OrderItem> = mutableListOf()
) {
    override fun equals(other: Any?): Boolean =
        this === other || (other is Order && id != 0L && id == other.id)

    // хэш не должен зависеть от id: у новой сущности он 0, а после save меняется,
    // и объект теряется в HashSet, куда его положили до сохранения.
    // Hibernate.getClass, а не javaClass: у прокси класс — сгенерированный подкласс
    override fun hashCode(): Int = Hibernate.getClass(this).hashCode()

    override fun toString(): String = "Order(id=$id)"
}
```

## Q4. Как работает Kotlin с конструктором @Entity?

Проблема: Hibernate создаёт сущность через no-arg конструктор и рефлексию, а в Kotlin класс с параметрами первичного конструктора пустого конструктора не имеет. Штатное решение — плагин `kotlin-jpa`:

```kotlin
plugins {
    kotlin("plugin.jpa") version "2.2.0"
}
```

Плагин на этапе компиляции синтезирует no-arg конструктор для классов с `@Entity`, `@MappedSuperclass`, `@Embeddable`. Этот конструктор не виден из обычного кода (его нельзя вызвать вручную) — он нужен только Hibernate, который зовёт его через рефлексию, а затем заполняет поля.

Альтернатива без плагина — задать default-значение каждому параметру: тогда Kotlin дополнительно сгенерирует обычный публичный конструктор без аргументов. В отличие от плагинного, его можно вызвать и руками — `User()`. Минус — приходится придумывать «пустые» значения для всех полей, что засоряет модель.

```kotlin
// Без kotlin-jpa плагина: ручные default значения
@Entity
class User(
    @Id @GeneratedValue val id: Long = 0,    // default нужен
    var name: String = "",                    // default для no-arg
    var email: String = ""
)
```

## Q5. Как использовать Kotlin Coroutines с Spring WebFlux?

WebFlux понимает корутины «из коробки»: вместо реактивных типов в сигнатурах пишут идиоматичный Kotlin, а Spring сам конвертирует его в Reactor под капотом. Правило соответствия простое:

- одиночное значение `Mono<T>` → `suspend fun(): T?`;
- поток значений `Flux<T>` → `fun(): Flow<T>`.

Получается обычный последовательный код без `.flatMap`/`.map`-цепочек, но с той же неблокирующей семантикой — корутина приостанавливается, а не занимает поток event loop.

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

    fun streamOrders(): Flow<Order> =
        repo.findAllByOrderByCreatedAt().asFlow()
}
```

Адаптацию делает сам фреймворк: `suspend` → `Mono`, `Flow` → `Flux`. Вручную `.toFlux()`/`.toMono()` вызывать не нужно — мост между корутинами и Reactor обеспечивает библиотека `kotlinx-coroutines-reactor`. На стороне сервиса для перехода из реактивного мира в корутины используют `awaitSingle()`/`awaitSingleOrNull()` (для `Mono`) и `asFlow()` (для `Flux`), как в примере выше.

## Q6. Что такое CoroutineCrudRepository?

`CoroutineCrudRepository` — это корутинная версия `CrudRepository` из Spring Data для реактивных хранилищ. Её методы возвращают не `Mono`/`Flux`, а обычные `suspend`-функции для одиночных результатов и `Flow<T>` для множественных. Nullable там, где результата может не быть (`findById`), а `save`, `count`, `existsById` возвращают non-null. То есть с репозиторием работаешь идиоматичным Kotlin без реактивных типов в сигнатурах.

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

**Где работает:** R2DBC, MongoDB Reactive, Cassandra Reactive — то есть в модулях Spring Data с реактивными репозиториями. У Spring Data Redis реактивных репозиториев нет: там только `ReactiveRedisTemplate`. Для классического блокирующего JDBC/JPA эта абстракция не подходит (там обычный `CrudRepository`). Под капотом запросы выполняет реактивный движок, а мост `kotlinx-coroutines-reactor` превращает его `Mono`/`Flux` в `suspend`/`Flow`.

## Q7. Как тестировать Kotlin Spring приложения?

Тесты пишутся теми же `@SpringBootTest`/`@WebMvcTest`, что и в Java, плюс несколько Kotlin-специфичных приёмов:

- **Имена тестов в обратных кавычках** — `fun \`should create order\`()` читаются как предложение, без `camelCase`.
- **`@Autowired constructor`** — зависимости в тест приходят через конструктор, как и в обычные бины (вместо `@Autowired` над полями).
- **`mockito-kotlin`** — обёртка над Mockito с null-safe API: `whenever(...)`/`verify(...)` без проблем с `any()` на non-null параметрах (стандартный Mockito возвращает `null`, что ломает Kotlin-типы).
- **`runTest`** из `kotlinx-coroutines-test` — запускает `suspend`-функции в тесте и виртуально проматывает время задержек.

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
        assertThat(repository.findById(order.id)).isPresent()
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
    assertThat(result).isNotNull()
}
```

## Q8. Как работает null-safety в Kotlin + Spring?

В системе типов Kotlin `User` (никогда не null) и `User?` (может быть null) — разные типы, и компилятор заставляет обрабатывать null до выполнения. В связке со Spring это убирает целый класс NPE, но создаёт стык на границе с Java-кодом самого фреймворка.

**На своей стороне** проще всего привести Java-контракты к Kotlin-типам в одном месте — в сервисе. Типичный паттерн: `Optional<User>` из JPA-репозитория превращают в `User?` через `.orElse(null)`, а «обязательное» получение — в non-null через `.orElseThrow()` (бросит исключение, если записи нет).

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

**На границе с Java** Kotlin читает аннотации nullability и переносит их в типы. Если аннотаций нет — тип становится *platform type* (`User!`): компилятор не знает, может ли значение быть null, и не требует обработки на этапе компиляции. Проверка при этом не исчезает, а переезжает в рантайм: при присваивании в non-null компилятор вставляет `Intrinsics.checkNotNullExpressionValue`, и она бросает NPE ровно в точке присваивания, если Java вернула null. Spring API частично размечен (`@Nullable`/`@NonNull`), но не везде, поэтому platform types стоит явно сужать до `User?` и обрабатывать.

```java
// @NotNull / @Nullable в Java → автоматические non-null / nullable в Kotlin
public interface JavaService {
    @Nullable User findUser(String id);   // → Kotlin видит как User?
    @NotNull User getUser(String id);     // → Kotlin видит как User
    User unknown(String id);              // → Kotlin видит как User! (platform type)
}
```

```kotlin
// Platform type (User!) — компилятор не требует проверки, но вставляет её в рантайм
val user: User = javaService.unknown("1")  // NPE прямо здесь, если Java вернула null
```

## Q9. Как использовать Kotlin DSL для конфигурации Spring?

Spring предоставляет типобезопасные Kotlin-DSL для конфигурации — функции с лямбдой-приёмником, где IDE подсказывает доступные опции, а ошибки видны на компиляции (в отличие от строковых XML/properties). Самые ходовые:

- **functional bean DSL** (`beans { ... }`) — регистрация бинов кодом вместо аннотаций; `ref()` подставляет уже зарегистрированную зависимость, а `profile("dev") { ... }` ограничивает бины профилем.
- **router DSL** (`coRouter { ... }`) — функциональная маршрутизация WebFlux: вместо `@RequestMapping` маршруты собираются деревом с `nest`, `GET`/`POST` и общими `filter`. `coRouter` — корутинный вариант, обработчики в нём `suspend`.

```kotlin
// functional bean DSL (Spring 5+)
// beans { } возвращает ApplicationContextInitializer, поэтому его подключают
// до refresh контекста, а не объявляют как @Bean
val appBeans = beans {
    bean<OrderService>()
    bean { OrderController(ref()) }       // ref() — auto-wiring
    bean<OrderRepository>()

    profile("dev") {
        bean<InMemoryOrderRepository>(isPrimary = true)
    }
}

fun main(args: Array<String>) {
    runApplication<Application>(*args) {
        addInitializers(appBeans)
    }
}
```

```kotlin
// Router DSL для WebFlux
@Configuration
class RouterConfig {

    private val logger = LoggerFactory.getLogger(RouterConfig::class.java)

    @Bean
    fun routes(handler: OrderHandler) = coRouter {
        "/orders".nest {
            GET("", handler::getAll)
            GET("/{id}", handler::getById)
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

## Q10. Как декларативный контроллер на Kotlin выглядит с validation?

Контроллер на Kotlin почти не отличается от Java: те же `@RestController`, `@Valid`, `@RequestBody`, а DTO удобно описывать `data class`. Идиоматичные детали — тело метода через выражение (`fun ... = ...`) и обработка отсутствия через elvis (`?: throw ...`) вместо `if (x == null)`.

Главная Kotlin-специфика — явная цель (`@field:` или `@get:`) у валидационных аннотаций (см. примечание ниже).

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

**Важно про `@field:`.** Свойство `data class` порождает сразу несколько Java-элементов: параметр конструктора, поле и getter. Без указания цели Kotlin вешает аннотацию на параметр конструктора, а валидатор Bean Validation читает ограничения с полей и геттеров — параметры конструктора он проверяет только при валидации самого вызова конструктора, поэтому проверка молча не срабатывает. Явная цель нужна обязательно, но целей две: `@field:` адресует аннотацию полю, `@get:` — геттеру, и обе работают.

## Q11. Как обрабатывать исключения в Kotlin Spring приложении?

Подход тот же, что в Java: централизованный обработчик `@RestControllerAdvice` с методами `@ExceptionHandler`, каждый из которых ловит свой тип исключения и собирает единый формат ответа. Kotlin добавляет удобства: тело-выражение для коротких хендлеров, elvis (`ex.message ?: "..."`) для запасного сообщения и `data class` с default-значениями для модели ошибки.

В примере ниже два хендлера: один для доменного `UserNotFoundException` (отдаёт 404), второй разбирает результат Bean Validation (`MethodArgumentNotValidException`) и складывает ошибки полей в `Map` через `associate`.

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

## Q12. Что такое extension functions в контексте Spring?

Extension function позволяет добавить метод к существующему типу, не наследуясь от него и не меняя его исходники. Технически это статический метод, которому объект передаётся как получатель (`this`), но вызывается он как обычный метод — `request.authToken()`. В Spring это главный инструмент адаптации Java-API фреймворка под идиоматичный Kotlin: можно навесить удобные хелперы прямо на `ServerHttpRequest`, `Optional`, `Mono` и т.п.

```kotlin
// Extension function — добавляет методы существующим классам
fun ServerHttpRequest.authToken(): String? =
    headers["Authorization"]?.firstOrNull()?.removePrefix("Bearer ")

// Использование
@RestController
class SecureController(private val userService: UserService) {
    @GetMapping("/me")
    fun me(request: ServerHttpRequest): Mono<User> {
        val token = request.authToken() ?: throw UnauthorizedException()
        return userService.getByToken(token)
    }
}

// Extension на типах из JDK и Reactor, с которыми Spring работает постоянно
private val log = LoggerFactory.getLogger("extensions")

fun <T> Optional<T>.toKotlinNullable(): T? = orElse(null)
fun <T> Mono<T>.logOnError(tag: String): Mono<T> = doOnError { log.error("[$tag]", it) }
```

**Итог:** extension functions держат адаптеры к Java-API Spring рядом с местом использования, не плодя классы-утилиты. Важно помнить: расширения резолвятся статически (по объявленному типу, без полиморфизма) — это синтаксический сахар, а не настоящее добавление методов в класс. Доступа к `private`-членам у расширения, объявленного снаружи, нет; исключение — member extension, объявленный внутри самого класса или его companion object.

## Q13. Как интегрировать Kotlin с Spring Security?

Spring Security даёт Kotlin-DSL для `SecurityFilterChain`: вместо цепочки `http.csrf().disable().authorizeHttpRequests()...` пишут вложенные блоки `http { csrf { disable() }; authorizeHttpRequests { ... } }`. Читается декларативнее, правила доступа группируются явно.

Текущего пользователя в обоих стеках проще всего внедрить параметром метода — `@AuthenticationPrincipal Jwt` (или `UserDetails`) работает и в MVC, и в WebFlux. Разница в том, откуда контекст берут вне контроллера:
- **MVC / синхронный** — `SecurityContextHolder`, контекст лежит в `ThreadLocal`.
- **Реактивный / корутины** — `ThreadLocal` не годится, контекст живёт в Reactor Context, поэтому его берут из `ReactiveSecurityContextHolder` и разворачивают через `awaitSingle()`.

```kotlin
@Configuration
@EnableWebSecurity
class SecurityConfig {

    @Bean
    fun filterChain(http: HttpSecurity): SecurityFilterChain {
        http {                                  // Kotlin DSL для HttpSecurity: возвращает Unit,
            csrf { disable() }                  // поэтому цепочку собираем и билдим отдельно
            authorizeHttpRequests {
                authorize("/api/public/**", permitAll)
                authorize("/api/admin/**", hasRole("ADMIN"))
                authorize(anyRequest, authenticated)
            }
            oauth2ResourceServer {
                jwt { }
            }
        }
        return http.build()
    }
}

// Получение текущего пользователя в контроллере
@GetMapping("/me")
fun me(@AuthenticationPrincipal jwt: Jwt): Map<String, Any> = mapOf(
    "sub" to jwt.subject,
    "roles" to (jwt.getClaimAsStringList("roles") ?: emptyList<String>())
)

// Реактивное приложение — другая конфигурация: @EnableWebFluxSecurity и
// fun springSecurityFilterChain(http: ServerHttpSecurity): SecurityWebFilterChain.
// Внутри suspend-обработчика контекст достают так:
@GetMapping("/me-reactive")
suspend fun meReactive(): User {
    val auth = ReactiveSecurityContextHolder.getContext()
        .awaitSingle()
        .authentication
    return userService.findByUsername(auth.name)
}
```

## Q14. Какие типичные ошибки при использовании Kotlin + Spring?

Почти все грабли растут из двух особенностей Kotlin — `final`-классы по умолчанию и строгая null-safety — и из того, как `data class` раскладывается в Java-аннотации. Чек-лист самых частых:

1. **`data class` для `@Entity`** — см. Q3. Используйте обычный класс.

2. **Забытый `kotlin-spring` plugin** — классы `final`, Spring не может создать подкласс-прокси:

```
AopConfigException: Could not generate CGLIB subclass of class com.example.OrderService:
Common causes of this problem include using a final class
```

3. **Поля без `@field:` в data class-валидациях** — аннотация попадает не на поле:

```kotlin
// ПЛОХО — аннотация уходит на параметр конструктора и валидатору бина не видна
data class Req(@NotBlank val name: String)

// ХОРОШО — цель задана явно: поле (или геттер через @get:)
data class Req(@field:NotBlank val name: String)
```

4. **Platform types (`User!`) из Java API** — компилятор не требует проверки, и NPE прилетает в рантайме:

```kotlin
// getName() в неразмеченном Java-классе виден как String!
val name: String = javaBean.getName()   // вернул null → NPE прямо на этой строке
```

5. **`Optional.get()` вместо безопасного разворачивания** — исключение вместо null-обработки:

```kotlin
// repo.findById(id) из JpaRepository возвращает Optional<User>
val user: User = repo.findById(1L).get()  // если не найдено → NoSuchElementException
```

6. **Использование `lateinit` вместо конструктор-injection** — усложняет тестирование.

7. **`runBlocking` в реактивном контроллере** — блокирует event loop:

```kotlin
// ПЛОХО
@GetMapping
fun getOrders() = runBlocking { orderService.findAll() }

// ХОРОШО — suspend функция напрямую
@GetMapping
suspend fun getOrders(): List<Order> = orderService.findAll()
```

## Q15. В чём преимущества и недостатки Kotlin для Spring Boot проектов?

Коротко: Kotlin даёт более безопасный и компактный код, расплачиваясь компиляторными плагинами и стыками nullability на границе с Java-экосистемой Spring.

**Плюсы:**
- **Null-safety** — null обрабатывается на компиляции, в рантайме NPE заметно меньше.
- **`data class`** — DTO без ручного `equals`/`hashCode`/`toString`.
- **Корутины** — последовательный код вместо цепочек `Mono`/`Flux`.
- **Extension functions** — адаптация Java-API Spring без классов-утилит.
- **Kotlin DSL** — типобезопасная конфигурация (beans, routes, security).
- **Совместимость с Java** — общий байт-код, миграция по одному классу.
- **Smart casts** — после `if (x is T)` компилятор сам кастует, явных приведений меньше.

**Минусы:**
- **Зависимость от плагинов** — без `kotlin-spring` падают места, где нужен подкласс-прокси (`@Configuration` с `proxyBeanMethods = true`, class-based `@Transactional` и AOP), без `kotlin-jpa` Hibernate не создаст сущность. Приложение без прокси и без JPA стартует и так, но в типовом проекте эти плагины нужны сразу.
- **Platform types** — компилятор не видит nullability неразмеченного Java-API, отсюда скрытые NPE.
- **Скорость компиляции** — обычно медленнее Java (компилятор K2 это подтягивает).
- **Меньше материалов** — большинство Spring-гайдов и Stack Overflow-ответов на Java.
- **Шероховатости библиотек** — часть инструментов (например, отдельные возможности Mockito) требует Kotlin-обёрток.

**Вывод:** для новых проектов, особенно на WebFlux + корутинах, Kotlin почти всегда выигрывает. Legacy на Java мигрируют постепенно — по модулям, пользуясь взаимной совместимостью: Kotlin вызывает Java почти без ограничений, а вот обратный вызов требует `@JvmStatic`/`@JvmOverloads`/`@JvmName` и не покрывает suspend-функции и inline с reified.

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
