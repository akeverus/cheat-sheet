---
title: "Вопросы на собеседовании: Micronaut"
description: "Micronaut — JVM-фреймворк с compile-time DI, AOP без рефлексии, native image, low memory. Сравнение со Spring Boot и Quarkus, Micronaut Data, HTTP-клиенты, GraalVM"
tags:
  - interview
  - frameworks
  - micronaut-interview
aliases:
  - "Micronaut interview"
  - "Micronaut собеседование"
  - "Micronaut framework interview"
difficulty: "intermediate"
updated: "2026-04-25"
---
# Вопросы на собеседовании: `Micronaut`

`Micronaut` — JVM-фреймворк (с 2018, от OCI/Object Computing), оптимизированный для **микросервисов и serverless**. Главная фича: **compile-time DI и AOP** без рефлексии — отсюда быстрый старт, низкое потребление памяти, отличная поддержка GraalVM native image.

## Полезные ссылки

### Официальная документация и авторитетные источники

- [Micronaut Official Documentation](https://docs.micronaut.io/latest/guide/)
- [Micronaut GitHub](https://github.com/micronaut-projects)
- [Micronaut Guides](https://guides.micronaut.io/)
- [Micronaut vs Spring Boot — Baeldung](https://www.baeldung.com/micronaut-vs-spring-boot)
- [Micronaut Data — Baeldung](https://www.baeldung.com/micronaut-data)
- [GraalVM and Micronaut](https://docs.micronaut.io/latest/guide/#graal)

## Содержание

- [Полезные ссылки](#полезные-ссылки)
- [See also](#see-also)

**Базовые понятия**
- [Q1. (!) Что такое Micronaut?](#q1--что-такое-micronaut)
- [Q2. (!) Чем Micronaut отличается от Spring Boot?](#q2--чем-micronaut-отличается-от-spring-boot)
- [Q3. (!) Чем Micronaut отличается от Quarkus?](#q3--чем-micronaut-отличается-от-quarkus)
- [Q4. (!) Что такое compile-time DI в Micronaut?](#q4--что-такое-compile-time-di-в-micronaut)

**Архитектура**
- [Q5. (!) Как Micronaut обрабатывает аннотации в compile time?](#q5--как-micronaut-обрабатывает-аннотации-в-compile-time)
- [Q6. (!) AOP без рефлексии — как реализовано?](#q6--aop-без-рефлексии--как-реализовано)
- [Q7. Что такое BeanIntrospection?](#q7-что-такое-beanintrospection)

**REST**
- [Q8. (!) Как создать REST-эндпойнт?](#q8--как-создать-rest-эндпойнт)
- [Q9. (!) Validation, error handling?](#q9--validation-error-handling)

**HTTP клиенты**
- [Q10. (!) Declarative HTTP-клиенты?](#q10--declarative-http-клиенты)
- [Q11. Reactive vs blocking клиенты?](#q11-reactive-vs-blocking-клиенты)

**Конфигурация**
- [Q12. (!) Как организовать конфигурацию?](#q12--как-организовать-конфигурацию)
- [Q13. Environment-specific конфиги?](#q13-environment-specific-конфиги)

**Persistence**
- [Q14. (!) Что такое Micronaut Data?](#q14--что-такое-micronaut-data)
- [Q15. JDBC, JPA, R2DBC репозитории?](#q15-jdbc-jpa-r2dbc-репозитории)

**Native Image**
- [Q16. (!) Поддержка GraalVM Native Image?](#q16--поддержка-graalvm-native-image)
- [Q17. Какие особенности и подводные камни?](#q17-какие-особенности-и-подводные-камни)

**Тестирование**
- [Q18. (!) @MicronautTest и его возможности?](#q18--micronauttest-и-его-возможности)

**Cloud и микросервисы**
- [Q19. (!) Service discovery, config management?](#q19--service-discovery-config-management)
- [Q20. Serverless поддержка (AWS Lambda, GCP Functions)?](#q20-serverless-поддержка-aws-lambda-gcp-functions)
- [Q21. Distributed tracing, metrics?](#q21-distributed-tracing-metrics)

**Реактивный стек**
- [Q22. Reactive поддержка (Reactor, RxJava)?](#q22-reactive-поддержка-reactor-rxjava)

**Production и сравнения**
- [Q23. (!) Когда выбирать Micronaut?](#q23--когда-выбирать-micronaut)
- [Q24. (!) Какие минусы Micronaut?](#q24--какие-минусы-micronaut)
- [Q25. Производительность Micronaut vs Quarkus vs Spring Boot?](#q25-производительность-micronaut-vs-quarkus-vs-spring-boot)
- [Q26. (!) Какие компании используют Micronaut в production?](#q26--какие-компании-используют-micronaut-в-production)

## Q1. (!) Что такое Micronaut?

`Micronaut` — современный JVM-фреймворк (с 2018), создатели — **Грэм Рочер** и команда (бывшие Spring/Grails разработчики).

**Целевая аудитория:** микросервисы, serverless, cloud-native приложения.

**Ключевые отличия:**
- **Compile-time DI и AOP** — никакой рефлексии в runtime
- Низкий cold start (1-2 сек на JVM, ~30ms native)
- Низкое потребление памяти (~50-100 MB JVM, ~30-50 MB native)
- Built-in поддержка для service discovery, distributed config, tracing
- First-class GraalVM native image


> [!mcq]
> - [x] Правильный ответ | Объяснение концепции 2-3 предложения
> - [ ] Неправильный вариант 1 | Почему ошибка в этом подходе
> - [ ] Неправильный вариант 2 | Это смежное, но отличное понятие
> - [ ] Неправильный вариант 3 | Противоположное направление## Q2. (!) Чем Micronaut отличается от Spring Boot?

| Критерий | Micronaut | Spring Boot |
|----------|-----------|-------------|
| DI | **Compile-time** (через annotation processors) | Runtime (через рефлексию) |
| AOP | **Compile-time** (без рефлексии) | Runtime (CGLIB / JDK proxies) |
| Cold start | 1-2 сек | 3-15 сек |
| Memory | 50-100 MB | 150-300 MB |
| Native image | First-class | Через Spring Native (Boot 3+) |
| Standard | Свой (но похож на Spring) | Spring |
| HTTP-клиенты | Declarative + compile-time | RestTemplate / WebClient |
| Cloud features | Built-in (Consul, Eureka, K8s) | Через Spring Cloud |
| Lambda поддержка | Optimized | Slow без оптимизаций |


> [!mcq]
> - [x] Правильный ответ | Объяснение концепции 2-3 предложения
> - [ ] Неправильный вариант 1 | Почему ошибка в этом подходе
> - [ ] Неправильный вариант 2 | Это смежное, но отличное понятие
> - [ ] Неправильный вариант 3 | Противоположное направление## Q3. (!) Чем Micronaut отличается от Quarkus?

| Критерий | Micronaut | Quarkus |
|----------|-----------|---------|
| Создатель | OCI / Object Computing | Red Hat |
| Год | 2018 | 2019 |
| Стандарт | Свой (близко к Spring) | MicroProfile + Jakarta EE |
| Cold start (JVM) | 1-2 сек | 1-2 сек |
| Cold start (native) | ~30 ms | ~10-30 ms |
| Reactive | Reactor по умолчанию | Mutiny |
| ORM | Micronaut Data + Hibernate | Hibernate ORM Panache |
| Dev experience | Hot reload через `./mvnw mn:run` | Live reload в `dev` mode (более развит) |
| Dev Services | Test Resources (с 4.0) | Dev Services (developed first) |
| Cloud | Built-in | Через extensions |

Оба нацелены на похожую нишу. **Quarkus** более популярен и активно развивается, но **Micronaut** часто проще для людей, знакомых со Spring.


> [!mcq]
> - [x] Правильный ответ | Объяснение концепции 2-3 предложения
> - [ ] Неправильный вариант 1 | Почему ошибка в этом подходе
> - [ ] Неправильный вариант 2 | Это смежное, но отличное понятие
> - [ ] Неправильный вариант 3 | Противоположное направление## Q4. (!) Что такое compile-time DI в Micronaut?

В Micronaut **DI graph строится во время сборки** через annotation processors, не в runtime.

**Что делает annotation processor:**
1. Сканирует код с аннотациями (`@Singleton`, `@Inject`, ...)
2. Генерирует Java-классы с именами `<ClassName>$Definition`
3. В runtime эти классы регистрируются в `BeanContext` без рефлексии

```java
// Что мы пишем
@Singleton
public class UserService {
    private final UserRepository repo;

    public UserService(UserRepository repo) {
        this.repo = repo;
    }
}

// Сгенерированный класс (упрощённо)
class UserService$Definition implements BeanDefinition<UserService> {
    public UserService instantiate(BeanContext context) {
        UserRepository repo = context.getBean(UserRepository.class);
        return new UserService(repo);
    }
}
```

**Преимущества:**
- Faster startup
- Меньше памяти (нет метаданных в runtime)
- Native image поддерживается из коробки
- Ошибки DI ловятся при сборке


> [!mcq]
> - [x] Правильный ответ | Объяснение концепции 2-3 предложения
> - [ ] Неправильный вариант 1 | Почему ошибка в этом подходе
> - [ ] Неправильный вариант 2 | Это смежное, но отличное понятие
> - [ ] Неправильный вариант 3 | Противоположное направление## Q5. (!) Как Micronaut обрабатывает аннотации в compile time?

Через **JSR-269 Annotation Processing API** (стандарт Java).

```
1. javac запускает MicronautAnnotationProcessor
2. Для каждого @Controller, @Singleton, @Inject и т.д.:
   - генерируется $Definition класс
   - генерируется $Introspection класс (для bean introspection без рефлексии)
3. Эти классы попадают в JAR
4. В runtime BeanContext находит их и использует
```

В отличие от Spring, **classpath сканирования** в runtime нет. Все знания о beans уже в коде.


> [!mcq]
> - [x] Правильный ответ | Объяснение концепции 2-3 предложения
> - [ ] Неправильный вариант 1 | Почему ошибка в этом подходе
> - [ ] Неправильный вариант 2 | Это смежное, но отличное понятие
> - [ ] Неправильный вариант 3 | Противоположное направление## Q6. (!) AOP без рефлексии — как реализовано?

Spring AOP использует **CGLIB** или **JDK dynamic proxies** в runtime — generates proxy classes через рефлексию.

Micronaut генерирует proxy классы **в compile time** через annotation processor.

```java
// Что мы пишем
@Singleton
public class UserService {
    @Transactional
    public User createUser(User user) { ... }
}

// Сгенерированный proxy
class UserService$Intercepted extends UserService {
    @Override
    public User createUser(User user) {
        // Вызов interceptors (transactional, security, ...)
        return super.createUser(user);
    }
}
```

**Преимущества:**
- Нет рефлексии → работает в native image
- Быстрее (нет JIT warmup для proxy)
- Стектрейсы проще читать


> [!mcq]
> - [x] Правильный ответ | Объяснение концепции 2-3 предложения
> - [ ] Неправильный вариант 1 | Почему ошибка в этом подходе
> - [ ] Неправильный вариант 2 | Это смежное, но отличное понятие
> - [ ] Неправильный вариант 3 | Противоположное направление## Q7. Что такое BeanIntrospection?

**BeanIntrospection** — метаданные о бине (поля, методы, аннотации), сгенерированные в compile time. Заменяет Java Reflection.

```java
@Introspected
class Person {
    private String name;
    private int age;
    // getters/setters
}

// Использование
BeanIntrospection<Person> introspection = BeanIntrospection.getIntrospection(Person.class);
Person p = introspection.instantiate("Alice", 30);
BeanProperty<Person, String> nameProp = introspection.getRequiredProperty("name", String.class);
String name = nameProp.get(p);
```

Используется в Jackson serialization, validation, ORM mapping — везде где раньше нужна была reflection.


> [!mcq]
> - [x] Правильный ответ | Объяснение концепции 2-3 предложения
> - [ ] Неправильный вариант 1 | Почему ошибка в этом подходе
> - [ ] Неправильный вариант 2 | Это смежное, но отличное понятие
> - [ ] Неправильный вариант 3 | Противоположное направление## Q8. (!) Как создать REST-эндпойнт?

```java
@Controller("/users")
public class UserController {
    private final UserService service;

    public UserController(UserService service) {
        this.service = service;
    }

    @Get
    public List<User> list() {
        return service.findAll();
    }

    @Get("/{id}")
    public User getById(Long id) {
        return service.findById(id)
            .orElseThrow(() -> new NotFoundException("User not found"));
    }

    @Post
    @Status(HttpStatus.CREATED)
    public User create(@Body User user) {
        return service.save(user);
    }

    @Delete("/{id}")
    @Status(HttpStatus.NO_CONTENT)
    public void delete(Long id) {
        service.delete(id);
    }
}
```

Очень похоже на Spring MVC, но без `@RestController` (controllers по умолчанию `JSON`).


> [!mcq]
> - [x] Правильный ответ | Объяснение концепции 2-3 предложения
> - [ ] Неправильный вариант 1 | Почему ошибка в этом подходе
> - [ ] Неправильный вариант 2 | Это смежное, но отличное понятие
> - [ ] Неправильный вариант 3 | Противоположное направление## Q9. (!) Validation, error handling?

**Validation** — через `javax.validation` (Jakarta Bean Validation):

```java
@Introspected
public class CreateUserRequest {
    @NotBlank
    private String name;

    @Email
    private String email;

    @Min(0) @Max(150)
    private int age;
}

@Post
public User create(@Valid @Body CreateUserRequest req) { ... }
```

**Error handling** — через `@Error`:

```java
@Controller("/users")
public class UserController {
    @Error
    public HttpResponse<ErrorResponse> handleNotFound(NotFoundException e) {
        return HttpResponse.notFound(new ErrorResponse(e.getMessage()));
    }
}

// Глобальный
@Controller
public class GlobalErrorController {
    @Error(global = true, exception = ConstraintViolationException.class)
    public HttpResponse<?> handleValidation(ConstraintViolationException e) {
        return HttpResponse.badRequest(...);
    }
}
```


> [!mcq]
> - [x] Правильный ответ | Объяснение концепции 2-3 предложения
> - [ ] Неправильный вариант 1 | Почему ошибка в этом подходе
> - [ ] Неправильный вариант 2 | Это смежное, но отличное понятие
> - [ ] Неправильный вариант 3 | Противоположное направление## Q10. (!) Declarative HTTP-клиенты?

Главная фишка Micronaut — **compile-time клиенты** через интерфейсы:

```java
@Client("https://api.example.com")
public interface UserApiClient {
    @Get("/users/{id}")
    User findById(@PathVariable Long id);

    @Post("/users")
    User create(@Body User user);

    @Get("/users")
    Flux<User> stream();
}

// Использование — просто инжектим
@Singleton
class UserService {
    private final UserApiClient client;

    UserService(UserApiClient client) { this.client = client; }
}
```

Реализация генерируется в compile time. Нет proxy в runtime, всё type-safe.

Похоже на **Feign**/**OpenFeign** в Spring Cloud, но без рефлексии.


> [!mcq]
> - [x] Правильный ответ | Объяснение концепции 2-3 предложения
> - [ ] Неправильный вариант 1 | Почему ошибка в этом подходе
> - [ ] Неправильный вариант 2 | Это смежное, но отличное понятие
> - [ ] Неправильный вариант 3 | Противоположное направление## Q11. Reactive vs blocking клиенты?

```java
// Blocking
@Client("...")
public interface UserClient {
    @Get("/users")
    List<User> list();
}

// Reactive (Project Reactor)
@Client("...")
public interface UserClient {
    @Get("/users")
    Flux<User> list();
    @Get("/users/{id}")
    Mono<User> findById(Long id);
}

// CompletableFuture
@Client("...")
public interface UserClient {
    @Get("/users")
    CompletableFuture<List<User>> listAsync();
}
```

Возвращаемый тип определяет стиль. Micronaut автоматически адаптирует.


> [!mcq]
> - [x] Правильный ответ | Объяснение концепции 2-3 предложения
> - [ ] Неправильный вариант 1 | Почему ошибка в этом подходе
> - [ ] Неправильный вариант 2 | Это смежное, но отличное понятие
> - [ ] Неправильный вариант 3 | Противоположное направление## Q12. (!) Как организовать конфигурацию?

```yaml
# application.yml
micronaut:
  application:
    name: my-service
  server:
    port: 8080

datasources:
  default:
    url: jdbc:postgresql://localhost:5432/mydb
    driverClassName: org.postgresql.Driver
    username: ${DB_USER:admin}
    password: ${DB_PASSWORD}
```

```java
@ConfigurationProperties("my.service")
public class MyServiceConfig {
    private String greeting;
    private Duration timeout;
    // getters/setters
}

@Singleton
class MyService {
    @Inject MyServiceConfig config;
    @Value("${some.value}") String value;
}
```

Поддерживаются `properties`, `yml`, `groovy`, `toml`, environment variables.


> [!mcq]
> - [x] Правильный ответ | Объяснение концепции 2-3 предложения
> - [ ] Неправильный вариант 1 | Почему ошибка в этом подходе
> - [ ] Неправильный вариант 2 | Это смежное, но отличное понятие
> - [ ] Неправильный вариант 3 | Противоположное направление## Q13. Environment-specific конфиги?

`application-{env}.yml` для разных environments:

```
application.yml          ← общий
application-dev.yml      ← dev
application-prod.yml     ← prod
application-test.yml     ← test (автоматически в @MicronautTest)
```

Активация:
```bash
java -Dmicronaut.environments=prod -jar app.jar
# или
MICRONAUT_ENVIRONMENTS=prod java -jar app.jar
```

Можно несколько: `prod,k8s,aws`.


> [!mcq]
> - [x] Правильный ответ | Объяснение концепции 2-3 предложения
> - [ ] Неправильный вариант 1 | Почему ошибка в этом подходе
> - [ ] Неправильный вариант 2 | Это смежное, но отличное понятие
> - [ ] Неправильный вариант 3 | Противоположное направление## Q14. (!) Что такое Micronaut Data?

`Micronaut Data` — type-safe, compile-time data access (аналог Spring Data, но без рефлексии).

```java
@Repository
public interface UserRepository extends CrudRepository<User, Long> {
    List<User> findByName(String name);
    Optional<User> findByEmail(String email);

    @Query("UPDATE User u SET u.active = false WHERE u.lastLogin < :date")
    void deactivateInactive(LocalDate date);
}
```

В compile time генерируется реализация — finder methods транслируются в SQL/JPQL/MongoDB queries без runtime парсинга.


> [!mcq]
> - [x] Правильный ответ | Объяснение концепции 2-3 предложения
> - [ ] Неправильный вариант 1 | Почему ошибка в этом подходе
> - [ ] Неправильный вариант 2 | Это смежное, но отличное понятие
> - [ ] Неправильный вариант 3 | Противоположное направление## Q15. JDBC, JPA, R2DBC репозитории?

Micronaut Data поддерживает:

| Backend | Стиль |
|---------|-------|
| **JDBC** | Сами SQL queries, lightweight |
| **JPA** | Через Hibernate |
| **R2DBC** | Reactive JDBC |
| **MongoDB** | Sync и async |

```java
// JDBC repository — без Hibernate
@JdbcRepository(dialect = Dialect.POSTGRES)
public interface BookRepository extends CrudRepository<Book, Long> {
    List<Book> findByAuthor(String author);
}

// R2DBC reactive
@R2dbcRepository(dialect = Dialect.POSTGRES)
public interface BookRepository extends ReactorCrudRepository<Book, Long> {
    Flux<Book> findByAuthor(String author);
}
```

JDBC версия не нуждается в Hibernate → меньше памяти и быстрее старт. Хорошо для микросервисов с простыми моделями.


> [!mcq]
> - [x] Правильный ответ | Объяснение концепции 2-3 предложения
> - [ ] Неправильный вариант 1 | Почему ошибка в этом подходе
> - [ ] Неправильный вариант 2 | Это смежное, но отличное понятие
> - [ ] Неправильный вариант 3 | Противоположное направление## Q16. (!) Поддержка GraalVM Native Image?

Micronaut **первый** мейнстримный JVM-фреймворк с full GraalVM поддержкой:

```bash
./gradlew nativeCompile
# или Maven
./mvnw package -Dpackaging=native-image
```

В отличие от Spring Boot (где Spring Native — отдельный проект), Micronaut **с самого начала** проектировался под native image:
- Нет рефлексии в DI
- Нет рефлексии в AOP
- Compile-time bean introspection
- Готовые reflection metadata для известных libs

Результат: native binaries обычно меньше и быстрее, чем Spring Native.


> [!mcq]
> - [x] Правильный ответ | Объяснение концепции 2-3 предложения
> - [ ] Неправильный вариант 1 | Почему ошибка в этом подходе
> - [ ] Неправильный вариант 2 | Это смежное, но отличное понятие
> - [ ] Неправильный вариант 3 | Противоположное направление## Q17. Какие особенности и подводные камни?

1. **@Introspected** на DTO — без него Jackson не сможет сериализовать в native (нужна reflection metadata)
2. **Third-party libraries** могут требовать ручной конфигурации reflection
3. **Сборка native** долгая (5-15 минут)
4. **Меньше JIT** → peak throughput ниже на ~30%
5. **Stack traces** хуже читаются

```java
@Introspected
public class User { ... } // обязательно для DTO в native
```


> [!mcq]
> - [x] Правильный ответ | Объяснение концепции 2-3 предложения
> - [ ] Неправильный вариант 1 | Почему ошибка в этом подходе
> - [ ] Неправильный вариант 2 | Это смежное, но отличное понятие
> - [ ] Неправильный вариант 3 | Противоположное направление## Q18. (!) @MicronautTest и его возможности?

```java
@MicronautTest
class UserControllerTest {
    @Inject @Client("/")
    HttpClient client;

    @Test
    void testList() {
        HttpResponse<List<User>> response = client.toBlocking()
            .exchange(HttpRequest.GET("/users"), Argument.listOf(User.class));
        assertEquals(HttpStatus.OK, response.getStatus());
        assertFalse(response.body().isEmpty());
    }
}

// Mocking
@MicronautTest
class ServiceTest {
    @Inject UserService service;
    @MockBean(UserRepository.class)
    UserRepository mockRepo() { return mock(UserRepository.class); }
}
```

`@MicronautTest` поднимает application context для теста. По умолчанию **trans actional** — каждый тест в transaction, откатывается после.

С Micronaut 4 — **Test Resources** (аналог Quarkus Dev Services) автоматически поднимают Postgres, Kafka, и т.д. в контейнерах.


> [!mcq]
> - [x] Правильный ответ | Объяснение концепции 2-3 предложения
> - [ ] Неправильный вариант 1 | Почему ошибка в этом подходе
> - [ ] Неправильный вариант 2 | Это смежное, но отличное понятие
> - [ ] Неправильный вариант 3 | Противоположное направление## Q19. (!) Service discovery, config management?

Micronaut имеет **встроенную** поддержку для облачных сервисов (без отдельного `Spring Cloud`):

```yaml
consul:
  client:
    registration:
      enabled: true
    config:
      enabled: true
```

Поддержка:
- **Consul** (HashiCorp) — service registry + config
- **Eureka** (Netflix)
- **Kubernetes** native discovery
- **AWS Cloud Map**
- **Distributed config:** Consul, Spring Cloud Config server, AWS Parameter Store, Vault, etcd

```java
@Client(id = "user-service") // resolves через service discovery
interface UserClient { ... }
```


> [!mcq]
> - [x] Правильный ответ | Объяснение концепции 2-3 предложения
> - [ ] Неправильный вариант 1 | Почему ошибка в этом подходе
> - [ ] Неправильный вариант 2 | Это смежное, но отличное понятие
> - [ ] Неправильный вариант 3 | Противоположное направление## Q20. Serverless поддержка (AWS Lambda, GCP Functions)?

```bash
mn create-function-app my-function --features=aws-lambda,graalvm
```

Micronaut Lambda runtime:
- Native binaries — cold start ~30 ms
- JVM — ~500 ms (vs Spring Boot 5-10 секунд)

```java
public class MyHandler extends MicronautRequestHandler<APIGatewayProxyRequestEvent, APIGatewayProxyResponseEvent> {
    @Inject UserService service;

    @Override
    public APIGatewayProxyResponseEvent execute(APIGatewayProxyRequestEvent input) {
        // ...
    }
}
```

Аналогично — Google Cloud Functions, Azure Functions, OCI Functions.


> [!mcq]
> - [x] Правильный ответ | Объяснение концепции 2-3 предложения
> - [ ] Неправильный вариант 1 | Почему ошибка в этом подходе
> - [ ] Неправильный вариант 2 | Это смежное, но отличное понятие
> - [ ] Неправильный вариант 3 | Противоположное направление## Q21. Distributed tracing, metrics?

```yaml
tracing:
  zipkin:
    enabled: true
    http:
      url: http://zipkin:9411
  jaeger:
    enabled: true

micrometer:
  metrics:
    enabled: true
    export:
      prometheus:
        enabled: true
```

```java
@Controller("/users")
public class UserController {
    @Get
    @ContinueSpan
    public List<User> list(@SpanTag String filter) {
        return ...;
    }
}
```

**OpenTelemetry**, **Zipkin**, **Jaeger** поддерживаются. Метрики через **Micrometer**, как в Spring Boot.


> [!mcq]
> - [x] Правильный ответ | Объяснение концепции 2-3 предложения
> - [ ] Неправильный вариант 1 | Почему ошибка в этом подходе
> - [ ] Неправильный вариант 2 | Это смежное, но отличное понятие
> - [ ] Неправильный вариант 3 | Противоположное направление## Q22. Reactive поддержка (Reactor, RxJava)?

```java
@Controller("/users")
public class UserController {
    @Inject ReactorUserRepository repo;

    @Get
    public Flux<User> list() {
        return repo.findAll();
    }

    @Get("/{id}")
    public Mono<User> findById(Long id) {
        return repo.findById(id);
    }
}
```

Поддерживаются **Reactor** (по умолчанию) и **RxJava**. Под капотом — Netty + Reactor.

В отличие от Spring WebFlux, выбор reactive не блокирует возможность использовать blocking endpoints в том же приложении — можно микшировать.


> [!mcq]
> - [x] Правильный ответ | Объяснение концепции 2-3 предложения
> - [ ] Неправильный вариант 1 | Почему ошибка в этом подходе
> - [ ] Неправильный вариант 2 | Это смежное, но отличное понятие
> - [ ] Неправильный вариант 3 | Противоположное направление## Q23. (!) Когда выбирать Micronaut?

**Выбирай Micronaut когда:**
- Нужны микросервисы с **низким cold start** (Lambda, autoscaling)
- Хочется **чистого compile-time** подхода (DI, AOP, валидация)
- Команде близок Spring (синтаксис похож)
- Нужны **declarative HTTP-клиенты** (как Feign, но в compile time)
- Нужна **встроенная** поддержка cloud (Consul, Eureka, K8s) без Spring Cloud

**Не выбирай когда:**
- Уже большая Spring-кодовая база
- Нужны редкие интеграции (Spring имеет всё)
- Команда не готова к новому стеку


> [!mcq]
> - [x] Правильный ответ | Объяснение концепции 2-3 предложения
> - [ ] Неправильный вариант 1 | Почему ошибка в этом подходе
> - [ ] Неправильный вариант 2 | Это смежное, но отличное понятие
> - [ ] Неправильный вариант 3 | Противоположное направление## Q24. (!) Какие минусы Micronaut?

1. **Меньше community** по сравнению со Spring (но больше Quarkus в некоторых нишах)
2. **Меньше готовых интеграций** — для Kafka, RabbitMQ есть, но более экзотические — нет
3. **Документация** сильно отстаёт от Spring по объёму примеров
4. **Меньше JetBrains/IDEA support** — некоторые рефакторинги работают хуже
5. **Native image** требует `@Introspected` на всех DTO — easy to forget
6. **Третьесторонние** libraries часто требуют донастройки для native
7. **Compile time** — больше времени на сборку, медленнее editor feedback в больших проектах


> [!mcq]
> - [x] Правильный ответ | Объяснение концепции 2-3 предложения
> - [ ] Неправильный вариант 1 | Почему ошибка в этом подходе
> - [ ] Неправильный вариант 2 | Это смежное, но отличное понятие
> - [ ] Неправильный вариант 3 | Противоположное направление## Q25. Производительность Micronaut vs Quarkus vs Spring Boot?

| Метрика | Micronaut | Quarkus | Spring Boot |
|---------|-----------|---------|-------------|
| Cold start (JVM) | 1.0-1.5 сек | 1.0-1.5 сек | 3-15 сек |
| Cold start (native) | ~30 ms | ~10-30 ms | ~50-200 ms |
| Memory (JVM) | 80-120 MB | 80-150 MB | 150-300 MB |
| Memory (native) | 30-50 MB | 30-50 MB | 50-100 MB |
| Throughput (req/s) | ~100K | ~100K | ~80-100K |

**Микро-различия в throughput**, существенные — в startup и memory. Все три современные фреймворки сильно лучше старого Spring Boot (до 3.0).


> [!mcq]
> - [x] Правильный ответ | Объяснение концепции 2-3 предложения
> - [ ] Неправильный вариант 1 | Почему ошибка в этом подходе
> - [ ] Неправильный вариант 2 | Это смежное, но отличное понятие
> - [ ] Неправильный вариант 3 | Противоположное направление## Q26. (!) Какие компании используют Micronaut в production?

- **Oracle** — внутренние сервисы (logically — фреймворк родственный)
- **Boeing**
- **Walmart Labs**
- **Target**
- **Goldman Sachs** — некоторые сервисы
- **Various FinTech** (используется для микросервисов)

Меньше публичности, чем у Spring или Quarkus, но в enterprise сегменте присутствие хорошее.

---

## See also

- [Spring Boot](../spring/spring-boot-interview.md) — главный конкурент
- [Quarkus](quarkus-interview.md) — другой Cloud-native JVM фреймворк
- [Ktor](ktor-interview.md) — Kotlin-first альтернатива
- [Vert.x](vertx-interview.md) — event-driven JVM
- [Spring Framework](../spring/spring-framework-interview.md) — runtime DI vs compile-time
- [Spring Cloud](../spring/spring-cloud-interview.md) — vs встроенный cloud в Micronaut
- [Spring Data JPA](../spring/spring-data-jpa-interview.md) — vs Micronaut Data
- [Spring WebFlux](../spring/spring-webflux-interview.md) — reactive аналог
- [Микросервисы](../../architecture/microservices-interview.md) — основное применение
- [Kubernetes](../../devops/kubernetes-interview.md) — нативная интеграция
- [JVM](../../jvm/jvm-interview.md) — JIT vs AOT компиляция
- [Memory Management](../../performance/memory-management-interview.md) — почему меньше памяти


> [!mcq]
> - [x] Правильный ответ | Объяснение концепции 2-3 предложения
> - [ ] Неправильный вариант 1 | Почему ошибка в этом подходе
> - [ ] Неправильный вариант 2 | Это смежное, но отличное понятие
> - [ ] Неправильный вариант 3 | Противоположное направление- [Ktor](ktor-interview.md)
- [Quarkus](quarkus-interview.md)
- [Vert.x](vertx-interview.md)
- [Spring AOP](../spring/spring-aop-interview.md)
- [Spring Batch](../spring/spring-batch-interview.md)
- [Spring Boot Actuator](../spring/spring-boot-actuator-interview.md)
