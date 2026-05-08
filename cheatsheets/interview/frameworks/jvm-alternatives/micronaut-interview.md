---
title: "Вопросы на собеседовании: Micronaut"
description: "Micronaut — JVM-фреймворк с compile-time DI, AOP без рефлексии, native image, low memory. Сравнение со Spring Boot и Quarkus, Micronaut Data, HTTP-клиенты, GraalVM"
tags:
  - interview
  - frameworks
  - micronaut-interview
type: "interview"
difficulty: "intermediate"
aliases:
  - "Вопросы на собеседовании"
  - "Micronaut"
  - "Micronaut interview"
  - "Micronaut собеседование"
prerequisites:
  - "[[micronaut-basics]]"
next: []
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
> - [ ] Spring Boot тоже использует compile-time DI начиная с версии 3, разницы нет | ❌ ПОСЛЕДСТВИЕ: Spring Boot по-прежнему использует runtime reflection; выбор Spring для serverless = cold start 3-15с вместо 1-2с
> - [x] Compile-time DI/AOP без рефлексии + first-class GraalVM native image → cold start ~30ms native | ✓ ПРИМЕНЯТЬ: Lambda/autoscaling где критичен cold start 📋 ПРАВИЛО: "compile-time = no reflection = fast start" 🔗 См. Q4
> - [ ] Micronaut — тонкий wrapper над Spring Framework, использует SpringContext под капотом | ❌ ПОСЛЕДСТВИЕ: Micronaut — полностью независимый BeanContext без Spring; ложные ожидания Spring-поведения ломают миграцию
> - [ ] Micronaut поддерживает только Kotlin, для Java нужен другой фреймворк | ❌ ПОСЛЕДСТВИЕ: Micronaut поддерживает Java, Kotlin и Groovy; ошибочный отказ от фреймворка без реального ограничения

## Q2. (!) Чем Micronaut отличается от Spring Boot?

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
> - [ ] Spring Boot 3+ с native image идентичен Micronaut по производительности — разницы нет | ❌ ПОСЛЕДСТВИЕ: Spring Boot 3 native ускоряет старт, но DI по-прежнему reflection-based; при JVM-режиме разница cold start 3-10x
> - [ ] Оба используют runtime reflection для DI; Micronaut лишь добавляет синтаксический сахар | ❌ ПОСЛЕДСТВИЕ: Micronaut генерирует DI-код в compile time через annotation processor; неверная модель → неожиданные ошибки при GraalVM native
> - [x] Micronaut — compile-time DI/AOP, cold start 1-2с JVM vs Spring Boot 3-15с, memory 80-120 МБ vs 150-300 МБ | ✓ ПРИМЕНЯТЬ: Lambda, k8s scale-to-zero, memory-constrained deployments 📋 ПРАВИЛО: "no reflection = fast startup + low memory" 🔗 См. Q25
> - [ ] Micronaut поддерживает только блокирующий HTTP, без Reactor/RxJava | ❌ ПОСЛЕДСТВИЕ: Micronaut поддерживает Reactor, RxJava, CompletableFuture; ограничение reactive-архитектуры без основания

## Q3. (!) Чем Micronaut отличается от Quarkus?

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
> - [ ] Quarkus работает только в GraalVM native, на JVM не запускается | ❌ ПОСЛЕДСТВИЕ: Quarkus поддерживает JVM-режим с hot reload (dev mode); исключение Quarkus без оснований
> - [x] Quarkus от Red Hat придерживается MicroProfile/Jakarta EE, имеет более развитый Dev Services; Micronaut ближе к Spring-стилю | ✓ ПРИМЕНЯТЬ: Micronaut — когда команда знает Spring; Quarkus — когда нужны стандарты Jakarta EE 📋 ПРАВИЛО: "OCI=Spring-style, RedHat=Jakarta-style" 🔗 См. Q23
> - [ ] Micronaut — только для Google Cloud, Quarkus — только для Red Hat OpenShift | ❌ ПОСЛЕДСТВИЕ: оба фреймворка cloud-agnostic; неправильная привязка к провайдеру сужает архитектурные опции
> - [ ] Quarkus значительно быстрее Micronaut в native, на порядок отличие cold start | ❌ ПОСЛЕДСТВИЕ: native cold start Quarkus ~10-30ms vs Micronaut ~30ms — незначительная разница; не основание для выбора фреймворка

## Q4. (!) Что такое compile-time DI в Micronaut?

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
> - [ ] DI graph строится при первом обращении к бину в runtime через CGLIB | ❌ ПОСЛЕДСТВИЕ: это поведение Spring Boot; в Micronaut ошибки DI ловятся на этапе сборки, не в runtime
> - [ ] Annotation processor запускается в runtime при старте приложения | ❌ ПОСЛЕДСТВИЕ: annotation processing — compile-time шаг javac; путаница ведёт к неправильным ожиданиям о нативном образе
> - [x] Annotation processor генерирует `$Definition` классы при сборке; BeanContext использует их в runtime без рефлексии | ✓ ПРИМЕНЯТЬ: когда нужны ошибки DI на этапе CI, а не в production 📋 ПРАВИЛО: "processor → $Definition → BeanContext без reflection" 🔗 См. Q5
> - [ ] Micronaut сканирует classpath в runtime через ClassLoader для нахождения бинов | ❌ ПОСЛЕДСТВИЕ: classpath-сканирование в runtime — это поведение Spring; Micronaut его не делает, поэтому быстрее стартует

## Q5. (!) Как Micronaut обрабатывает аннотации в compile time?

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
> - [ ] Annotation processor только валидирует аннотации, классы-реализации генерируются в runtime | ❌ ПОСЛЕДСТВИЕ: в Micronaut реализации ($Definition, $Intercepted) генерируются при сборке; ошибка в runtime = неправильные ожидания
> - [x] javac запускает MicronautAnnotationProcessor, который генерирует `$Definition` / `$Introspection` классы; в runtime BeanContext их использует без reflection | ✓ ПРИМЕНЯТЬ: для понимания почему Micronaut поддерживает native image из коробки 📋 ПРАВИЛО: "JSR-269 → $Definition at compile → no classpath scan at runtime" 🔗 См. Q4
> - [ ] Обработка аннотаций происходит через AspectJ compile-time weaving | ❌ ПОСЛЕДСТВИЕ: AspectJ weaving — отдельный инструмент; Micronaut использует стандартный JSR-269 annotation processing без AspectJ
> - [ ] Micronaut использует Kotlin kapt для обработки аннотаций в Java-проектах | ❌ ПОСЛЕДСТВИЕ: kapt — Kotlin-специфичная обёртка над JSR-269; в Java-проектах используется стандартный annotation processor без kapt

## Q6. (!) AOP без рефлексии — как реализовано?

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
> - [ ] AOP в Micronaut использует CGLIB proxy в runtime, как Spring Boot | ❌ ПОСЛЕДСТВИЕ: CGLIB требует рефлексии и не совместим с GraalVM native без конфигурации; Micronaut генерирует `$Intercepted` subclass в compile time
> - [ ] Micronaut использует JDK dynamic proxy для AOP через java.lang.reflect.Proxy | ❌ ПОСЛЕДСТВИЕ: JDK proxy работает только с интерфейсами и через рефлексию; Micronaut генерирует конкретный subclass в compile time
> - [x] Annotation processor генерирует `$Intercepted` subclass при сборке; interceptors вызываются через сгенерированный код без рефлексии | ✓ ПРИМЕНЯТЬ: когда нужен AOP в GraalVM native image 📋 ПРАВИЛО: "compile-time proxy = no CGLIB = native-friendly" 🔗 См. Q16
> - [ ] В native image AOP отключается — @Transactional и @CacheResult не работают | ❌ ПОСЛЕДСТВИЕ: в native image AOP работает, потому что proxy-классы сгенерированы при сборке; ограничение надуманное

## Q7. Что такое BeanIntrospection?

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
> - [ ] BeanIntrospection — это runtime reflection API, аналог `Class.getDeclaredFields()` | ❌ ПОСЛЕДСТВИЕ: BeanIntrospection — compile-time метаданные, не reflection; использование reflection в native image → ClassNotFoundException
> - [x] Compile-time метаданные о бине (поля, методы, аннотации) через `@Introspected`; заменяет reflection в Jackson, validation, ORM mapping | ✓ ПРИМЕНЯТЬ: на всех DTO/entity для работы в GraalVM native 📋 ПРАВИЛО: "@Introspected = compile-time reflection metadata" 🔗 См. Q16
> - [ ] BeanIntrospection используется только для unit-тестов, в production коде не нужен | ❌ ПОСЛЕДСТВИЕ: без @Introspected на DTO Jackson не может сериализовать объект в native image → SerializationException в production
> - [ ] @Introspected нужен только для Kotlin data classes, не для Java POJO | ❌ ПОСЛЕДСТВИЕ: @Introspected нужен для любых DTO в native image независимо от языка; Java POJO без аннотации → ClassNotFoundException при сериализации

## Q8. (!) Как создать REST-эндпойнт?

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
> - [ ] Нужна аннотация `@RestController` как в Spring MVC | ❌ ПОСЛЕДСТВИЕ: в Micronaut `@Controller` по умолчанию возвращает JSON без дополнительных аннотаций; добавление несуществующей @RestController — compile error
> - [ ] `@Get`, `@Post` без указания пути матчат только корень `/` | ❌ ПОСЛЕДСТВИЕ: без пути endpoint матчит корень контроллера; для `/users/{id}` нужен `@Get("/{id}")`, иначе 404
> - [ ] Возвращаемый тип `void` автоматически даёт статус 200, нельзя вернуть 201 Created | ❌ ПОСЛЕДСТВИЕ: `@Status(HttpStatus.CREATED)` меняет код ответа; без него POST-endpoint возвращает 200 вместо стандартного 201
> - [x] `@Controller("/users")` + `@Get/@Post/@Delete` + DI через конструктор; JSON по умолчанию, статус через `@Status` | ✓ ПРИМЕНЯТЬ: REST API без `@RestController`, синтаксис похож на Spring MVC 📋 ПРАВИЛО: "@Controller = @RestController в Spring" 🔗 См. Q9

## Q9. (!) Validation, error handling?

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
> - [ ] Глобальный `@Error` задаётся через `@ControllerAdvice` как в Spring | ❌ ПОСЛЕДСТВИЕ: в Micronaut глобальный error handler — `@Error(global = true)` на методе `@Controller`; `@ControllerAdvice` не существует в Micronaut
> - [x] `@Valid @Body` запускает Bean Validation; `@Error` на методе контроллера перехватывает исключение; для глобального — `@Error(global = true)` | ✓ ПРИМЕНЯТЬ: для структурированных 400/404 ответов без try-catch в каждом методе 📋 ПРАВИЛО: "@Error(global=true) = @ControllerAdvice аналог" 🔗 См. Q8
> - [ ] Validation работает только с Kotlin data classes, для Java — нужен отдельный validator | ❌ ПОСЛЕДСТВИЕ: @NotBlank/@Email работают на любых Java/Kotlin объектах с @Introspected; ограничение надуманное
> - [ ] Без `@Introspected` на DTO аннотации @NotBlank/@Email игнорируются | ❌ ПОСЛЕДСТВИЕ: @Introspected нужен для native image сериализации; validation работает и без него на JVM (через reflection)

## Q10. (!) Declarative HTTP-клиенты?

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
> - [ ] Declarative HTTP-клиент в Micronaut работает через CGLIB proxy в runtime, как OpenFeign | ❌ ПОСЛЕДСТВИЕ: Micronaut генерирует реализацию клиента в compile time; ошибки конфигурации → compile error, не runtime exception
> - [ ] `@Client` интерфейс без реализации → `UnsatisfiedDependencyException` при старте | ❌ ПОСЛЕДСТВИЕ: реализация генерируется annotation processor при сборке; если пропустить добавление зависимости mn-http-client → compile error, не UnsatisfiedDependency
> - [x] `@Client("url")` на интерфейсе + `@Get/@Post` методы → compile-time реализация; нет рефлексии, type-safe, как Feign но без runtime proxy | ✓ ПРИМЕНЯТЬ: межсервисное взаимодействие в Micronaut-based microservices 📋 ПРАВИЛО: "@Client interface = Feign без runtime proxy" 🔗 См. Q11
> - [ ] Для reactive методов нужно отдельно подключить reactor-core dependency | ❌ ПОСЛЕДСТВИЕ: Reactor включён в micronaut-reactor из коробки; возвращаемый тип Flux/Mono достаточен для автоматической адаптации

## Q11. Reactive vs blocking клиенты?

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
> - [x] Правильный ответ | Корректное описание концепции с конкретным механизмом и use-case.
> - [ ] Альтернативное решение которое не подходит | Почему ошибка в этом подходе ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.
> - [ ] Другая альтернатива с критическим недостатком | Это смежное, но отличное понятие ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.
> - [ ] Третий вариант который не работает в production | Противоположное направление## Q12. (!) Как организовать конфигурацию? ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.

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
> - [x] Правильный ответ | Корректное описание концепции с конкретным механизмом и use-case.
> - [ ] Альтернативное решение которое не подходит | Почему ошибка в этом подходе ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.
> - [ ] Другая альтернатива с критическим недостатком | Это смежное, но отличное понятие ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.
> - [ ] Третий вариант который не работает в production | Противоположное направление## Q13. Environment-specific конфиги? ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.

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
> - [x] Правильный ответ | Корректное описание концепции с конкретным механизмом и use-case.
> - [ ] Альтернативное решение которое не подходит | Почему ошибка в этом подходе ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.
> - [ ] Другая альтернатива с критическим недостатком | Это смежное, но отличное понятие ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.
> - [ ] Третий вариант который не работает в production | Противоположное направление## Q14. (!) Что такое Micronaut Data? ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.

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
> - [x] Правильный ответ | Корректное описание концепции с конкретным механизмом и use-case.
> - [ ] Альтернативное решение которое не подходит | Почему ошибка в этом подходе ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.
> - [ ] Другая альтернатива с критическим недостатком | Это смежное, но отличное понятие ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.
> - [ ] Третий вариант который не работает в production | Противоположное направление## Q15. JDBC, JPA, R2DBC репозитории? ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.

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
> - [x] Правильный ответ | Корректное описание концепции с конкретным механизмом и use-case.
> - [ ] Альтернативное решение которое не подходит | Почему ошибка в этом подходе ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.
> - [ ] Другая альтернатива с критическим недостатком | Это смежное, но отличное понятие ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.
> - [ ] Третий вариант который не работает в production | Противоположное направление## Q16. (!) Поддержка GraalVM Native Image? ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.

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
> - [x] Правильный ответ | Корректное описание концепции с конкретным механизмом и use-case.
> - [ ] Альтернативное решение которое не подходит | Почему ошибка в этом подходе ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.
> - [ ] Другая альтернатива с критическим недостатком | Это смежное, но отличное понятие ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.
> - [ ] Третий вариант который не работает в production | Противоположное направление## Q17. Какие особенности и подводные камни? ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.

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
> - [x] Правильный ответ | Корректное описание концепции с конкретным механизмом и use-case.
> - [ ] Альтернативное решение которое не подходит | Почему ошибка в этом подходе ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.
> - [ ] Другая альтернатива с критическим недостатком | Это смежное, но отличное понятие ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.
> - [ ] Третий вариант который не работает в production | Противоположное направление## Q18. (!) @MicronautTest и его возможности? ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.

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
> - [x] Правильный ответ | Корректное описание концепции с конкретным механизмом и use-case.
> - [ ] Альтернативное решение которое не подходит | Почему ошибка в этом подходе ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.
> - [ ] Другая альтернатива с критическим недостатком | Это смежное, но отличное понятие ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.
> - [ ] Третий вариант который не работает в production | Противоположное направление## Q19. (!) Service discovery, config management? ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.

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
> - [x] Правильный ответ | Корректное описание концепции с конкретным механизмом и use-case.
> - [ ] Альтернативное решение которое не подходит | Почему ошибка в этом подходе ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.
> - [ ] Другая альтернатива с критическим недостатком | Это смежное, но отличное понятие ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.
> - [ ] Третий вариант который не работает в production | Противоположное направление## Q20. Serverless поддержка (AWS Lambda, GCP Functions)? ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.

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
> - [x] Правильный ответ | Корректное описание концепции с конкретным механизмом и use-case.
> - [ ] Альтернативное решение которое не подходит | Почему ошибка в этом подходе ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.
> - [ ] Другая альтернатива с критическим недостатком | Это смежное, но отличное понятие ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.
> - [ ] Третий вариант который не работает в production | Противоположное направление## Q21. Distributed tracing, metrics? ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.

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
> - [x] Правильный ответ | Корректное описание концепции с конкретным механизмом и use-case.
> - [ ] Альтернативное решение которое не подходит | Почему ошибка в этом подходе ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.
> - [ ] Другая альтернатива с критическим недостатком | Это смежное, но отличное понятие ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.
> - [ ] Третий вариант который не работает в production | Противоположное направление## Q22. Reactive поддержка (Reactor, RxJava)? ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.

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
> - [x] Правильный ответ | Корректное описание концепции с конкретным механизмом и use-case.
> - [ ] Альтернативное решение которое не подходит | Почему ошибка в этом подходе ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.
> - [ ] Другая альтернатива с критическим недостатком | Это смежное, но отличное понятие ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.
> - [ ] Третий вариант который не работает в production | Противоположное направление## Q23. (!) Когда выбирать Micronaut? ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.

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
> - [x] Правильный ответ | Корректное описание концепции с конкретным механизмом и use-case.
> - [ ] Альтернативное решение которое не подходит | Почему ошибка в этом подходе ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.
> - [ ] Другая альтернатива с критическим недостатком | Это смежное, но отличное понятие ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.
> - [ ] Третий вариант который не работает в production | Противоположное направление## Q24. (!) Какие минусы Micronaut? ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.

1. **Меньше community** по сравнению со Spring (но больше Quarkus в некоторых нишах)
2. **Меньше готовых интеграций** — для Kafka, RabbitMQ есть, но более экзотические — нет
3. **Документация** сильно отстаёт от Spring по объёму примеров
4. **Меньше JetBrains/IDEA support** — некоторые рефакторинги работают хуже
5. **Native image** требует `@Introspected` на всех DTO — easy to forget
6. **Третьесторонние** libraries часто требуют донастройки для native
7. **Compile time** — больше времени на сборку, медленнее editor feedback в больших проектах


> [!mcq]
> - [x] Правильный ответ | Корректное описание концепции с конкретным механизмом и use-case.
> - [ ] Альтернативное решение которое не подходит | Почему ошибка в этом подходе ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.
> - [ ] Другая альтернатива с критическим недостатком | Это смежное, но отличное понятие ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.
> - [ ] Третий вариант который не работает в production | Противоположное направление## Q25. Производительность Micronaut vs Quarkus vs Spring Boot? ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.

| Метрика | Micronaut | Quarkus | Spring Boot |
|---------|-----------|---------|-------------|
| Cold start (JVM) | 1.0-1.5 сек | 1.0-1.5 сек | 3-15 сек |
| Cold start (native) | ~30 ms | ~10-30 ms | ~50-200 ms |
| Memory (JVM) | 80-120 MB | 80-150 MB | 150-300 MB |
| Memory (native) | 30-50 MB | 30-50 MB | 50-100 MB |
| Throughput (req/s) | ~100K | ~100K | ~80-100K |

**Микро-различия в throughput**, существенные — в startup и memory. Все три современные фреймворки сильно лучше старого Spring Boot (до 3.0).


> [!mcq]
> - [x] Правильный ответ | Корректное описание концепции с конкретным механизмом и use-case.
> - [ ] Альтернативное решение которое не подходит | Почему ошибка в этом подходе ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.
> - [ ] Другая альтернатива с критическим недостатком | Это смежное, но отличное понятие ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.
> - [ ] Третий вариант который не работает в production | Противоположное направление## Q26. (!) Какие компании используют Micronaut в production? ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.

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
> - [x] Правильный ответ | Корректное описание концепции с конкретным механизмом и use-case.
> - [ ] Альтернативное решение которое не подходит | Почему ошибка в этом подходе ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.
> - [ ] Другая альтернатива с критическим недостатком | Это смежное, но отличное понятие ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.
> - [ ] Третий вариант который не работает в production | Противоположное направление- [Ktor](ktor-interview.md) ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.
- [Quarkus](quarkus-interview.md)
- [Vert.x](vertx-interview.md)
- [Spring AOP](../spring/spring-aop-interview.md)
- [Spring Batch](../spring/spring-batch-interview.md)
- [Spring Boot Actuator](../spring/spring-boot-actuator-interview.md)
