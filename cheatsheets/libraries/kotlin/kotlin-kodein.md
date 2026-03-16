---
title: "Kodein"
description: "Kodein - это dependency injection фреймворк для Kotlin, предоставляющий type-safe и компилируемый DI. Использует Kotlin-специфичные возможности для создания чистого и выразительного API."
tags: ["libraries", "kotlin", "kotlin-kodein"]
difficulty: "intermediate"
prerequisites: []
next: []
updated: "2026-02-11"
---
# Kodein

**Дата последнего обновления:** 2026-02-06

**Kodein** - это **dependency injection** фреймворк для **Kotlin**, предоставляющий **type-safe** и компилируемый `DI`. Использует **Kotlin**-специфичные возможности для создания чистого и выразительного **API**.

## Полезные ссылки
- [Официальная документация Kodein](https://kodein.org/Kodein-DI/)
- [Kodein GitHub](https://github.com/Kodein-Framework/Kodein-DI)
- [Kodein Samples](https://github.com/Kodein-Framework/Kodein-DI/tree/master/samples)
- [DI Principles](https://kodein.org/Kodein-DI/7.20/core/constructor-injection.html)

## Содержание

- [Основные возможности](#основные-возможности)
  - [Basic Dependency Injection](#basic-dependency-injection)
  - [Binding Types](#binding-types)
  - [Module Organization](#module-organization)
- [Продвинутые возможности](#продвинутые-возможности)
  - [Scopes](#scopes)
  - [Tagged Bindings](#tagged-bindings)
  - [Generics Support](#generics-support)
  - [Multiple Bindings](#multiple-bindings)
  - [Lateinit Bindings](#lateinit-bindings)
- [Интеграция с Kotlin](#интеграция-с-kotlin)
  - [Inline Functions и Reified Generics](#inline-functions-и-reified-generics)
  - [DSL для сложных конфигураций](#dsl-для-сложных-конфигураций)
  - [Operator Overloading](#operator-overloading)
- [Интеграция с Android](#интеграция-с-android)
  - [Application Level DI](#application-level-di)
  - [ViewModel Integration](#viewmodel-integration)
- [Интеграция с Spring Boot](#интеграция-с-spring-boot)
  - [Configuration Classes](#configuration-classes)
  - [Aspect-Oriented Programming](#aspect-oriented-programming)
- [Тестирование](#тестирование)
  - [Unit Testing с Kodein](#unit-testing-с-kodein)
  - [Integration Testing](#integration-testing)
  - [Property Testing](#property-testing)
- [Лучшие практики](#лучшие-практики)
  - [Error Handling](#error-handling)
  - [Performance Optimization](#performance-optimization)
- [Отладка и устранение неполадок](#отладка-и-устранение-неполадок)
  - [Debugging DI Issues](#debugging-di-issues)
  - [Common Issues](#common-issues)
- [Руководство по миграции](#руководство-по-миграции)
  - [From Dagger to Kodein](#from-dagger-to-kodein)
  - [From Koin to Kodein](#from-koin-to-kodein)
  - [From Guice to Kodein](#from-guice-to-kodein)
- [Экспериментальные возможности](#экспериментальные-возможности)
  - [Kodein 8.0+ **Features** (**Future**)](#kodein-80-features-future)
- [См. также](#см-также)

## Основные возможности

### **Basic Dependency Injection**

Базовый `DI`-контейнер **Kodein**: привязка типов (**bind**) и создание **singleton**/**provider**.

```kotlin
import org.kodein.di.*

/
 * Базовая dependency injection в Kodein
 * Kodein использует type-safe binding для регистрации зависимостей
 * DI { } - блок для создания DI контейнера
 */
// Создание DI контейнера
// DI { } - DSL блок для конфигурации dependency injection
val kodein = DI {
    // Регистрация зависимостей внутри блока DI
    
    // bind<T>() - привязка типа T к реализации
    // with singleton { } - создание singleton (один экземпляр на весь lifecycle)
    // RepositoryImpl() - реализация интерфейса Repository
    bind<Repository>() with singleton { RepositoryImpl() }
    // При каждом запросе Repository будет возвращаться один и тот же экземпляр
    
    // bind<Service>() - привязка типа Service
    // with provider { } - создание нового экземпляра при каждом запросе
    // get() - получение зависимости из контейнера (в данном случае Repository)
    bind<Service>() with provider { Service(get()) }
    // При каждом запросе Service будет создаваться новый экземпляр
    // get() автоматически разрешает зависимость Repository из контейнера
}

// Получение зависимостей из контейнера
// kodein.direct - прямой доступ к зависимостям (без ленивой инициализации)
// instance<T>() - получение экземпляра типа T из контейнера
val repository: Repository = kodein.direct.instance()
// Возвращает singleton экземпляр RepositoryImpl

val service: Service = kodein.direct.instance()
// Возвращает новый экземпляр Service с инжектированным Repository
```

### **Binding Types**
```kotlin
val kodein = DI {
    // Singleton - один экземпляр на весь application
    bind<Database>() with singleton { DatabaseImpl() }

    // Provider - новый экземпляр каждый раз
    bind<UserService>() with provider { UserService(get()) }

    // Factory - создание с параметрами
    bind<UserController>() with factory { userId: Int ->
        UserController(userId, get(), get())
    }

    // Multiton - один экземпляр на ключ
    bind<Cache<String>>() with multiton { key: String ->
        CacheImpl(key)
    }

    // Eager singleton - создается сразу
    bind<Config>() with eagerSingleton { loadConfig() }

    // Constant - константа
    constant("apiUrl") with "https://api.example.com"

    // Instance - существующий объект
    bind<Logger>() with instance(CustomLogger())
}
```

### **Module Organization**
```kotlin
// Разделение на модули
val databaseModule = DI.Module("database") {
    bind<Database>() with singleton { DatabaseImpl() }
    bind<UserDao>() with singleton { UserDaoImpl(get()) }
}

val serviceModule = DI.Module("service") {
    bind<UserService>() with singleton { UserServiceImpl(get(), get()) }
    bind<EmailService>() with provider { EmailServiceImpl() }
}

val controllerModule = DI.Module("controller") {
    bind<UserController>() with factory { userId: Int ->
        UserController(userId, get(), get())
    }
}

// Комбинирование модулей
val kodein = DI {
    import(databaseModule)
    import(serviceModule)
    import(controllerModule)
}
```

## Продвинутые возможности

### **Scopes**
```kotlin
import org.kodein.di.bindings.*

// Session scope
val sessionScope = WeakContextScope.of<Session>()

val kodein = DI {
    bind<UserPreferences>() with scoped(sessionScope).singleton {
        UserPreferencesImpl()
    }
}

// Использование
val session = Session()
kodein.direct.contextScopeRegistryContext = session

val prefs1 = kodein.direct.instance<UserPreferences>()
val prefs2 = kodein.direct.instance<UserPreferences>()

assert(prefs1 === prefs2) // Тот же экземпляр в рамках сессии

// Новая сессия
val newSession = Session()
kodein.direct.contextScopeRegistryContext = newSession
val prefs3 = kodein.direct.instance<UserPreferences>()

assert(prefs1 !== prefs3) // Разные экземпляры для разных сессий
```

### **Tagged Bindings**
```kotlin
// Теги для множественных реализаций одного интерфейса
val kodein = DI {
    // Основная база данных
    bind<Database>(tag = "main") with singleton { MainDatabase() }

    // Тестовая база данных
    bind<Database>(tag = "test") with singleton { TestDatabase() }

    // Кеш
    bind<Cache>(tag = "memory") with singleton { MemoryCache() }
    bind<Cache>(tag = "redis") with singleton { RedisCache() }
}

// Получение по тегу
val mainDb: Database = kodein.direct.instance(tag = "main")
val testDb: Database = kodein.direct.instance(tag = "test")
val memoryCache: Cache = kodein.direct.instance(tag = "memory")

// Кастомные теги
object Production : DIAware {
    override val di: DI get() = kodein
}

object Test : DIAware {
    override val di: DI get() = testKodein
}
```

### **Generics Support**
```kotlin
// Работа с generic типами
val kodein = DI {
    // Generic binding
    bind<Repository<User>>() with singleton { UserRepository() }
    bind<Repository<Product>>() with singleton { ProductRepository() }

    // Generic factory
    bind<Mapper<User, UserDto>>() with provider { UserMapper() }
    bind<Mapper<Product, ProductDto>>() with provider { ProductMapper() }

    // List of generics
    bind<List<Validator<User>>>() with singleton {
        listOf(EmailValidator(), NameValidator())
    }
}

// Получение generic зависимостей
val userRepository: Repository<User> = kodein.direct.instance()
val userMapper: Mapper<User, UserDto> = kodein.direct.instance()
val userValidators: List<Validator<User>> = kodein.direct.instance()
```

### **Multiple Bindings**
```kotlin
// Множественные реализации
interface NotificationService {
    fun sendNotification(message: String)
}

val kodein = DI {
    bind<NotificationService>() with singleton { EmailService() }
    bind<NotificationService>() with singleton { SmsService() }
    bind<NotificationService>() with singleton { PushService() }
}

// Получение всех реализаций
val services: Set<NotificationService> = kodein.direct.instance()

// Получение по индексу
val emailService: NotificationService = kodein.direct.instance(arg = 0)
val smsService: NotificationService = kodein.direct.instance(arg = 1)
```

### **Lateinit Bindings**
```kotlin
// Отложенная инициализация
val kodein = DI {
    bind<Config>() with singleton(ref = false) {
        // Эта функция будет вызвана только при первом обращении
        loadConfigFromFile()
    }
}

// Получение с отложенной инициализацией
val config: Config = kodein.direct.instance() // Config загружается здесь
```

## Интеграция с **Kotlin**

### **Inline Functions** и **Reified Generics**
```kotlin
// Type-safe instance retrieval
inline fun <reified T : Any> DI.instance(): T = direct.instance()

inline fun <reified T : Any> DI.instance(tag: Any?): T = direct.instance(tag = tag)

inline fun <reified T : Any> DI.instance(arg: Any?): T = direct.instance(arg = arg)

// Использование
val service: UserService = kodein.instance()
val database: Database = kodein.instance(tag = "main")
val controller: UserController = kodein.instance(arg = 123)
```

### **DSL** для сложных конфигураций
```kotlin
// Комплексная конфигурация с DSL
fun DI.Builder.configureApp() {
    // База данных
    database {
        bind<Database>() with singleton { PostgresDatabase() }
        bind<ConnectionPool>() with singleton { HikariPool() }
    }

    // Сервисы
    services {
        bind<UserService>() with singleton { UserServiceImpl(instance(), instance()) }
        bind<ProductService>() with provider { ProductServiceImpl(instance()) }
    }

    // Контроллеры
    controllers {
        bind<UserController>() with factory { id: Int -> UserController(id, instance(), instance()) }
        bind<ProductController>() with factory { id: Int -> ProductController(id, instance()) }
    }

    // Утилиты
    utils {
        bind<Logger>() with singleton { Slf4jLogger() }
        bind<Validator>() with provider { BeanValidator() }
        constant("app.version") with "1.0.0"
    }
}

// DSL функции
fun DI.Builder.database(block: DI.Builder.() -> Unit) {
    // Настройка database модуля
    block()
}

fun DI.Builder.services(block: DI.Builder.() -> Unit) {
    // Настройка services модуля
    block()
}

// Использование
val kodein = DI {
    configureApp()
}
```

### **Operator Overloading**
```kotlin
// Кастомные операторы для DI
operator fun <T : Any> DI.get(type: KClass<T>): T = direct.instance(type)

operator fun <T : Any> DI.get(tag: Pair<KClass<T>, Any?>): T =
    direct.instance(type = tag.first, tag = tag.second)

// Использование
val service: UserService = kodein[UserService::class]
val database: Database = kodein[Database::class to "main"]
```

## Интеграция с **Android**

### **Application Level** `DI`
```kotlin
class MyApplication : Application(), DIAware {

    override val di: DI = DI.lazy {
        // Application-scoped dependencies
        bind<Database>() with singleton { RoomDatabase.create(this@MyApplication) }
        bind<ApiService>() with singleton { RetrofitApiService() }
        bind<UserPreferences>() with singleton { SharedPreferencesHelper(this@MyApplication) }

        // Activity-scoped
        bind<LoginViewModel>() with provider { LoginViewModel(instance(), instance()) }
    }
}

// В Activity
class LoginActivity : AppCompatActivity(), DIAware {

    override val di: DI by di()

    private val viewModel: LoginViewModel by instance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // viewModel автоматически внедрен
        viewModel.login("user", "pass")
    }
}
```

### **ViewModel Integration**
```kotlin
// Kodein ViewModel
class UserViewModel(
    private val userService: UserService,
    private val logger: Logger
) : ViewModel() {

    private val _user = MutableLiveData<User>()
    val user: LiveData<User> = _user

    fun loadUser(userId: Int) {
        viewModelScope.launch {
            try {
                val user = userService.getUser(userId)
                _user.value = user
            } catch (e: Exception) {
                logger.error("Failed to load user", e)
            }
        }
    }
}

// В Application
val kodein = DI {
    bind<UserService>() with singleton { UserServiceImpl(instance()) }
    bind<Logger>() with singleton { AndroidLogger() }

    // ViewModel factory
    bind<ViewModelProvider.Factory>() with singleton {
        object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return when (modelClass) {
                    UserViewModel::class.java -> UserViewModel(instance(), instance()) as T
                    else -> throw IllegalArgumentException("Unknown ViewModel class")
                }
            }
        }
    }
}
```

## Интеграция с **Spring Boot**

### **Configuration Classes**
```kotlin
@Configuration
class KodeinConfig {

    @Bean
    fun kodeinContainer(
        @Autowired dataSource: DataSource,
        @Autowired objectMapper: ObjectMapper
    ): DI {
        return DI {
            // Database
            bind<DataSource>() with instance(dataSource)
            bind<Database>() with singleton { ExposedDatabase(dataSource) }

            // Serialization
            bind<ObjectMapper>() with instance(objectMapper)

            // Services
            bind<UserService>() with singleton { UserServiceImpl(instance(), instance()) }
            bind<ProductService>() with provider { ProductServiceImpl(instance()) }

            // Controllers
            bind<UserController>() with factory { userService: UserService ->
                UserController(userService)
            }
        }
    }
}

// Использование в контроллерах
@RestController
class UserRestController(
    private val kodein: DI
) {

    private val userService: UserService by kodein.instance()
    private val userController: UserController by kodein.factory(instance<UserService>())

    @GetMapping("/users/{id}")
    suspend fun getUser(@PathVariable id: Long): ResponseEntity<User> {
        return try {
            val user = userController.getUser(id)
            ResponseEntity.ok(user)
        } catch (e: UserNotFoundException) {
            ResponseEntity.notFound().build()
        }
    }
}
```

### **Aspect-Oriented Programming**
```kotlin
// AOP с Kodein
class TransactionalAspect {

    fun <T> transactional(block: () -> T): T {
        val transactionManager = kodein.direct.instance<TransactionManager>()

        return transactionManager.runInTransaction {
            try {
                val result = block()
                transactionManager.commit()
                result
            } catch (e: Exception) {
                transactionManager.rollback()
                throw e
            }
        }
    }
}

// Использование
@Service
class UserServiceImpl(
    private val userRepository: UserRepository,
    private val transactionalAspect: TransactionalAspect
) : UserService {

    override fun createUser(request: CreateUserRequest): User {
        return transactionalAspect.transactional {
            // Все операции в транзакции
            validateUser(request)
            val user = UserEntity.new {
                name = request.name
                email = request.email
            }
            userRepository.save(user)
        }
    }
}
```

## Тестирование

### **Unit Testing** с **Kodein**
```kotlin
class UserServiceTest {

    private lateinit var kodein: DI

    @BeforeEach
    fun setUp() {
        kodein = DI {
            // Моки для тестирования
            bind<UserRepository>() with singleton { mockk<UserRepository>() }
            bind<EmailService>() with singleton { mockk<EmailService>() }

            // Реальный сервис
            bind<UserService>() with singleton { UserServiceImpl(instance(), instance()) }
        }
    }

    @Test
    fun `should create user successfully`() {
        val userRepository: UserRepository by kodein.instance()
        val emailService: EmailService by kodein.instance()
        val userService: UserService by kodein.instance()

        val request = CreateUserRequest("John", "john@example.com")
        val expectedUser = User(1, "John", "john@example.com")

        // Настройка моков
        coEvery { userRepository.save(any()) } returns expectedUser
        coEvery { emailService.sendWelcomeEmail(any()) } just Runs

        // Тестирование
        runTest {
            val result = userService.createUser(request)

            assertEquals(expectedUser, result)
            coVerify { userRepository.save(any()) }
            coVerify { emailService.sendWelcomeEmail("john@example.com") }
        }
    }

    @Test
    fun `should throw exception for duplicate email`() {
        val userRepository: UserRepository by kodein.instance()
        val userService: UserService by kodein.instance()

        val request = CreateUserRequest("John", "existing@example.com")

        // Настройка мока для выброса исключения
        coEvery { userRepository.save(any()) } throws DuplicateEmailException()

        // Тестирование
        runTest {
            assertThrows<DuplicateEmailException> {
                userService.createUser(request)
            }
        }
    }
}
```

### **Integration Testing**
```kotlin
@SpringBootTest
class KodeinIntegrationTest {

    @Autowired
    private lateinit var kodein: DI

    @Autowired
    private lateinit var userService: UserService

    @Test
    fun `should inject dependencies correctly`() {
        // Проверка что зависимости правильно внедрены
        val repository: UserRepository by kodein.instance()
        val emailService: EmailService by kodein.instance()

        assertNotNull(repository)
        assertNotNull(emailService)
        assertNotNull(userService)
    }

    @Test
    fun `should create user in database`() {
        val request = CreateUserRequest("Integration", "integration@example.com")

        val user = userService.createUser(request)

        assertNotNull(user.id)
        assertEquals("Integration", user.name)

        // Проверка в базе данных
        val fromDb = userService.getUser(user.id!!)
        assertEquals(user, fromDb)
    }
}
```

### **Property Testing**
```kotlin
class KodeinPropertyTest {

    @Test
    fun `DI container should always return same singleton instance`() {
        val kodein = DI {
            bind<Database>() with singleton { TestDatabase() }
        }

        val instance1: Database by kodein.instance()
        val instance2: Database by kodein.instance()

        assertSame(instance1, instance2)
    }

    @Test
    fun `DI container should return different provider instances`() {
        val kodein = DI {
            bind<Config>() with provider { TestConfig() }
        }

        val instance1: Config by kodein.instance()
        val instance2: Config by kodein.instance()

        assertNotSame(instance1, instance2)
    }
}
```

## Лучшие практики

### **Module Organization**
```kotlin
// Рекомендуемая структура модулей
object DIModules {

    val dataModule = DI.Module("data") {
        // Database connections, DAOs, repositories
        bind<Database>() with singleton { Database.connect() }
        bind<UserRepository>() with singleton { UserRepositoryImpl(instance()) }
        bind<ProductRepository>() with singleton { ProductRepositoryImpl(instance()) }
    }

    val domainModule = DI.Module("domain") {
        // Business logic, services
        bind<UserService>() with singleton { UserServiceImpl(instance(), instance()) }
        bind<ProductService>() with singleton { ProductServiceImpl(instance()) }
        bind<Validator<User>>() with provider { UserValidator() }
    }

    val presentationModule = DI.Module("presentation") {
        // Controllers, view models
        bind<UserController>() with factory { userService: UserService ->
            UserController(userService)
        }
        bind<LoginViewModel>() with provider { LoginViewModel(instance(), instance()) }
    }

    val infrastructureModule = DI.Module("infrastructure") {
        // External services, utilities
        bind<EmailService>() with singleton { SmtpEmailService(instance()) }
        bind<Cache>() with singleton { RedisCache(instance()) }
        bind<Logger>() with singleton { Slf4jLogger() }
    }
}

// Основной контейнер
val kodein = DI {
    importAll(
        DIModules.dataModule,
        DIModules.domainModule,
        DIModules.presentationModule,
        DIModules.infrastructureModule
    )
}
```

### **Error Handling**
```kotlin
// Безопасное разрешение зависимостей
sealed class DIResult<out T> {
    data class Success<T>(val value: T) : DIResult<T>()
    data class Failure(val error: String) : DIResult<Nothing>()
}

inline fun <reified T : Any> DI.safeInstance(): DIResult<T> {
    return try {
        DIResult.Success(direct.instance<T>())
    } catch (e: DI.NotFoundException) {
        DIResult.Failure("Dependency not found: ${T::class.simpleName}")
    } catch (e: Exception) {
        DIResult.Failure("Failed to create instance: ${e.message}")
    }
}

// Использование
val result = kodein.safeInstance<UserService>()
when (result) {
    is DIResult.Success -> {
        val service = result.value
        // Использование сервиса
    }
    is DIResult.Failure -> {
        logger.error("DI Error: ${result.error}")
        // Обработка ошибки
    }
}
```

### **Performance Optimization**
```kotlin
// Ленивая инициализация для тяжелых зависимостей
val kodein = DI {
    bind<HeavyService>() with singleton(ref = false) {
        // Создается только при первом обращении
        HeavyServiceImpl()
    }
}

// Pooling для часто используемых объектов
class ObjectPool<T>(private val factory: () -> T, private val maxSize: Int = 10) {
    private val pool = ArrayDeque<T>()

    fun borrow(): T {
        return pool.removeFirstOrNull() ?: factory()
    }

    fun release(obj: T) {
        if (pool.size < maxSize) {
            pool.addLast(obj)
        }
    }
}

val connectionPool = ObjectPool(::createDatabaseConnection, maxSize = 20)

val kodein = DI {
    bind<ObjectPool<DatabaseConnection>>() with singleton { connectionPool }
}
```

## Отладка и устранение неполадок

### **Debugging** `DI` **Issues**
```kotlin
// Логирование создания зависимостей
object DILogger : KontextFactoryBuilder.Listener {
    override fun onInstanceCreated(kontext: Kontext, key: Kodein.Key<*, *, *>, instance: Any) {
        println("Created instance: $key -> $instance")
    }
}

val kodein = DI {
    // Регистрация листенера
    onReady {
        addContextTranslator(DILogger)
    }
}

// Проверка контейнера
fun DI.validateBindings() {
    val allBindings = container.bindings
    val missingDependencies = mutableListOf<String>()

    allBindings.forEach { (key, binding) ->
        try {
            direct.instanceOrNull(key.type, key.tag, key.arg)
        } catch (e: Exception) {
            missingDependencies.add("${key.type} (${key.tag})")
        }
    }

    if (missingDependencies.isNotEmpty()) {
        throw IllegalStateException("Missing dependencies: $missingDependencies")
    }
}
```

### **Common Issues**
```kotlin
object KodeinTroubleshooting {

    // Проблема: Circular dependencies
    // Решение: Использовать provider или factory вместо singleton
    val kodein = DI {
        // Плохо: circular dependency
        // bind<A>() with singleton { A(instance()) }
        // bind<B>() with singleton { B(instance()) }

        // Хорошо: использование provider
        bind<A>() with provider { A(instance()) }
        bind<B>() with provider { B(instance()) }
    }

    // Проблема: Thread safety
    // Решение: Использовать thread-safe scopes
    val kodein = DI {
        bind<SessionData>() with scoped(ThreadLocalScope).singleton {
            SessionDataImpl()
        }
    }

    // Проблема: Memory leaks
    // Решение: Правильное использование scopes
    val activityScope = WeakContextScope.of<Activity>()

    val kodein = DI {
        bind<ViewModel>() with scoped(activityScope).singleton {
            ViewModelImpl()
        }
    }

    // Проблема: Performance issues
    // Решение: Использовать appropriate binding types
    val kodein = DI {
        // Singleton для дорогих объектов
        bind<Database>() with singleton { createDatabase() }

        // Provider для легких объектов
        bind<Validator>() with provider { Validator() }

        // Factory для parameterized objects
        bind<Controller>() with factory { id: Int -> Controller(id, instance()) }
    }
}
```

## Руководство по миграции

### **From Dagger** to **Kodein**
```kotlin
// Dagger
@Component
interface AppComponent {
    fun userService(): UserService
    fun userController(): UserController
}

@Module
class AppModule {
    @Provides
    @Singleton
    fun provideUserService(repo: UserRepository): UserService {
        return UserServiceImpl(repo)
    }
}

// Kodein
val kodein = DI {
    bind<UserRepository>() with singleton { UserRepositoryImpl() }
    bind<UserService>() with singleton { UserServiceImpl(instance()) }
    bind<UserController>() with factory { userService: UserService ->
        UserController(userService)
    }
}

// Получение зависимостей
// Dagger: appComponent.userService()
// Kodein: kodein.direct.instance<UserService>()
```

### **From Koin** to **Kodein**
```kotlin
// Koin
val appModule = module {
    single { UserRepositoryImpl() }
    single { UserServiceImpl(get()) }
    factory { (userId: Int) -> UserController(userId, get()) }
}

val kodein = DI {
    bind<UserRepository>() with singleton { UserRepositoryImpl() }
    bind<UserService>() with singleton { UserServiceImpl(instance()) }
    bind<UserController>() with factory { userId: Int -> UserController(userId, instance()) }
}
```

### **From Guice** to **Kodein**
```kotlin
// Guice
class AppModule : AbstractModule() {
    override fun configure() {
        bind(UserRepository::class.java).to(UserRepositoryImpl::class.java).`in`(Singleton::class.java)
        bind(UserService::class.java).to(UserServiceImpl::class.java).`in`(Singleton::class.java)
    }
}

// Kodein
val kodein = DI {
    bind<UserRepository>() with singleton { UserRepositoryImpl() }
    bind<UserService>() with singleton { UserServiceImpl(instance()) }
}
```

## Экспериментальные возможности

### **Kodein** 8.0+ **Features** (**Future**)
```kotlin
// Предполагаемые возможности Kodein 8.0+

// Улучшенная поддержка Kotlin 1.8+
// Context receivers для scoped bindings
context(DIContext)
val scopedService: Service by instance()

// Inline value classes support
@JvmInline
value class UserId(val value: Long)

val kodein = DI {
    bind<UserService>() with factory { userId: UserId ->
        UserServiceImpl(userId, instance())
    }
}

// Compile-time DI generation
// Автоматическая генерация кода для DI во время компиляции
@KodeinGenerated
val kodein = DI {
    // Генерируется эффективный код без reflection
    bind<UserService>() with singleton { UserServiceImpl(instance()) }
}

// Reactive bindings
import kotlinx.coroutines.flow.*

val kodein = DI {
    bind<Flow<User>>() with singleton {
        userRepository.observeUsers()
    }
}

// Native support для sealed classes
sealed class Result<out T> {
    data class Success<T>(val value: T) : Result<T>()
    data class Error(val message: String) : Result<Nothing>()
}

val kodein = DI {
    bind<Result<User>>() with provider {
        try {
            Result.Success(userService.getCurrentUser())
        } catch (e: Exception) {
            Result.Error(e.message ?: "Unknown error")
        }
    }
}
```
## См. также
- [Обзор библиотек](../) — DI для **Java**/**Kotlin**
- [Spring](../../frameworks/java-frameworks/spring/spring-core.md) — DI в **Spring**

