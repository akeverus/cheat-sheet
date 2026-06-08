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
- [Q6. (!) Как Micronaut реализует AOP без рефлексии?](#q6--как-micronaut-реализует-aop-без-рефлексии)
- [Q7. Что такое BeanIntrospection?](#q7-что-такое-beanintrospection)

**REST**
- [Q8. (!) Как создать REST-эндпойнт?](#q8--как-создать-rest-эндпойнт)
- [Q9. (!) Как устроены валидация и обработка ошибок?](#q9--как-устроены-валидация-и-обработка-ошибок)

**HTTP клиенты**
- [Q10. (!) Что такое декларативные HTTP-клиенты?](#q10--что-такое-декларативные-http-клиенты)
- [Q11. Чем отличаются реактивные и блокирующие клиенты?](#q11-чем-отличаются-реактивные-и-блокирующие-клиенты)

**Конфигурация**
- [Q12. (!) Как организовать конфигурацию?](#q12--как-организовать-конфигурацию)
- [Q13. Как делать конфиги под разные окружения (dev/prod/test)?](#q13-как-делать-конфиги-под-разные-окружения-devprodtest)

**Persistence**
- [Q14. (!) Что такое Micronaut Data?](#q14--что-такое-micronaut-data)
- [Q15. Чем различаются JDBC-, JPA- и R2DBC-репозитории?](#q15-чем-различаются-jdbc--jpa--и-r2dbc-репозитории)

**Native Image**
- [Q16. (!) Как Micronaut поддерживает GraalVM Native Image?](#q16--как-micronaut-поддерживает-graalvm-native-image)
- [Q17. Какие подводные камни у native image?](#q17-какие-подводные-камни-у-native-image)

**Тестирование**
- [Q18. (!) Что умеет @MicronautTest?](#q18--что-умеет-micronauttest)

**Cloud и микросервисы**
- [Q19. (!) Как устроены service discovery и управление конфигурацией?](#q19--как-устроены-service-discovery-и-управление-конфигурацией)
- [Q20. Как Micronaut поддерживает serverless (AWS Lambda, GCP Functions)?](#q20-как-micronaut-поддерживает-serverless-aws-lambda-gcp-functions)
- [Q21. Как настроить distributed tracing и метрики?](#q21-как-настроить-distributed-tracing-и-метрики)

**Реактивный стек**
- [Q22. Как устроена реактивная поддержка (Reactor, RxJava)?](#q22-как-устроена-реактивная-поддержка-reactor-rxjava)

**Production и сравнения**
- [Q23. (!) Когда стоит выбирать Micronaut?](#q23--когда-стоит-выбирать-micronaut)
- [Q24. (!) Какие минусы у Micronaut?](#q24--какие-минусы-у-micronaut)
- [Q25. Как Micronaut, Quarkus и Spring Boot соотносятся по производительности?](#q25-как-micronaut-quarkus-и-spring-boot-соотносятся-по-производительности)
- [Q26. (!) Какие компании используют Micronaut в production?](#q26--какие-компании-используют-micronaut-в-production)

## Q1. (!) Что такое Micronaut?

`Micronaut` — JVM-фреймворк для микросервисов и serverless, в котором весь DI и AOP делается на этапе компиляции, а не в runtime через рефлексию. Создан в 2018 году командой **Грэма Рочера** (бывшие разработчики Spring и Grails) и заточен под cloud-native сценарии.

Это и есть главная идея: пока Spring собирает граф зависимостей при старте, сканируя classpath и читая аннотации рефлексией, Micronaut уже сгенерировал всю эту информацию во время сборки. Отсюда вытекают все его преимущества.

**Что даёт compile-time подход:**
- **Быстрый старт.** Нет classpath-сканирования и разбора аннотаций на запуске — 1–2 сек на JVM против 3–15 сек у Spring Boot; в native image ~30 ms.
- **Низкая память.** Нет метаданных рефлексии в heap — ~50–100 MB на JVM, ~30–50 MB в native.
- **First-class GraalVM native image.** Раз нет рефлексии — native-компиляция работает из коробки, а не требует отдельного проекта.
- **Встроенный cloud-стек.** Service discovery, distributed config и tracing идут в комплекте, без отдельного Spring Cloud.

**Сценарий применения:** AWS Lambda, GCP Functions, Kubernetes scale-to-zero — всё, где критичен холодный старт и каждый мегабайт памяти на счету.

## Q2. (!) Чем Micronaut отличается от Spring Boot?

Ключевое различие — **когда строится DI/AOP**: Micronaut делает это на компиляции без рефлексии, Spring Boot — в runtime через рефлексию. Всё остальное (старт, память, native, cloud) — следствие этого выбора.

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

**Как читать таблицу на собеседовании:** не зазубривайте цифры — назовите причину. Compile-time DI убирает classpath-сканирование на старте (отсюда быстрый cold start) и метаданные рефлексии из heap (отсюда меньше памяти), а заодно делает native image тривиальным. Синтаксис аннотаций намеренно похож на Spring, чтобы упростить переход, но контейнер и экосистема — свои.

## Q3. (!) Чем Micronaut отличается от Quarkus?

Оба решают одну задачу — быстрый, лёгкий cloud-native фреймворк с native image — и оба двигают работу на компиляцию. Главное различие в **происхождении и стандартах**: Micronaut от OCI с собственным API, близким к Spring; Quarkus от Red Hat поверх MicroProfile + Jakarta EE.

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

**Как выбрать:** по производительности (cold start, память) они практически равны. Quarkus популярнее, активнее развивается и сильнее в dev-эргономике (live reload, Dev Services появились раньше). Micronaut обычно легче заходит командам со Spring-бэкграундом — синтаксис почти тот же. То есть выбор чаще про экосистему и привычки команды, чем про числа.

## Q4. (!) Что такое compile-time DI в Micronaut?

Compile-time DI — это построение графа зависимостей во время сборки через annotation processor, а не при старте через рефлексию. Компилятор заранее «знает», какие бины есть и как их связать, и зашивает это в сгенерированный код.

**Как это работает по шагам:**
1. Annotation processor сканирует код с аннотациями (`@Singleton`, `@Inject`, ...) во время `javac`.
2. Для каждого бина генерирует Java-класс `<ClassName>$Definition` с явным кодом создания и резолва зависимостей.
3. В runtime `BeanContext` просто загружает эти классы и вызывает их — без рефлексии и без сканирования classpath.

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

**Что это даёт:**
- **Быстрый старт** — нет работы по сборке графа на запуске.
- **Меньше памяти** — метаданные рефлексии не висят в heap.
- **Native image из коробки** — нет рефлексии, которую пришлось бы вручную регистрировать.
- **Ошибки DI ловятся при сборке** — несуществующий или неоднозначный бин ломает компиляцию, а не падает в проде при старте.

## Q5. (!) Как Micronaut обрабатывает аннотации в compile time?

Через стандартный механизм Java — **JSR-269 Annotation Processing API**. Это тот же API, что используют Lombok или MapStruct: компилятор отдаёт процессору AST с аннотациями, а процессор генерирует дополнительные классы, которые тут же компилируются.

```
1. javac запускает MicronautAnnotationProcessor
2. Для каждого @Controller, @Singleton, @Inject и т.д.:
   - генерируется $Definition класс
   - генерируется $Introspection класс (для bean introspection без рефлексии)
3. Эти классы попадают в JAR
4. В runtime BeanContext находит их и использует
```

**Ключевое следствие:** в отличие от Spring, classpath-сканирования в runtime нет — всё знание о бинах уже зашито в сгенерированный код. Поэтому старт быстрый, а в native image нечего «терять»: GraalVM не выкинет нужный класс, потому что обращение к нему статически видно компилятору.

## Q6. (!) Как Micronaut реализует AOP без рефлексии?

Micronaut генерирует прокси-классы **на этапе компиляции** через annotation processor, а не создаёт их в runtime, как Spring.

Для сравнения: Spring AOP в runtime генерирует прокси через **CGLIB** (наследование) или **JDK dynamic proxies** (интерфейсы) и обращается к перехватываемым методам рефлексией. Micronaut делает то же самое заранее — на выходе обычный сгенерированный Java-класс, который вызывает методы напрямую.

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

**Что это даёт:**
- **Работает в native image** — нет рефлексии и динамической генерации классов, которые GraalVM не любит.
- **Быстрее** — прокси готов на старте, не нужен JIT-прогрев динамически сгенерированного класса.
- **Читаемые стектрейсы** — в трейсе видно обычный сгенерированный метод, а не безымянный CGLIB-прокси.

## Q7. Что такое BeanIntrospection?

**BeanIntrospection** — это сгенерированные на компиляции метаданные о классе (поля, методы, аннотации, конструктор), которые заменяют Java Reflection. По сути, статический «слепок» структуры класса: создать объект, прочитать и записать свойство, узнать аннотации — всё без `java.lang.reflect`.

Чтобы класс получил такой слепок, его помечают `@Introspected`.

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

**Где применяется:** Jackson-сериализация, Bean Validation, ORM-маппинг — везде, где раньше нужна была рефлексия. Именно благодаря `BeanIntrospection` эти механизмы работают в native image: вся метаинформация уже в коде, GraalVM ничего не приходится регистрировать вручную. Отсюда и правило «помечай DTO `@Introspected`» — без слепка Jackson в native не сможет сериализовать объект.

## Q8. (!) Как создать REST-эндпойнт?

Контроллер помечается `@Controller("/path")`, а методы — `@Get`/`@Post`/`@Delete` и т.д. Зависимости инжектятся через конструктор, путь и тело запроса — через `@Get("/{id}")` и `@Body`, статус ответа — через `@Status`.

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

**Чем отличается от Spring MVC:** синтаксис почти тот же, но отдельной аннотации `@RestController` нет — любой `@Controller` по умолчанию отдаёт `JSON`. Аргумент метода `Long id` сам подхватывается из `@Get("/{id}")` по имени, а тело запроса берётся через `@Body`. Под капотом — Netty, а не сервлет-контейнер.

## Q9. (!) Как устроены валидация и обработка ошибок?

Валидация — это стандартный Jakarta Bean Validation (`@NotBlank`, `@Email`, `@Min`/`@Max`), а обработка ошибок строится на аннотации `@Error`: локальной (внутри контроллера) или глобальной (`global = true`).

**Валидация** — через `javax.validation` (Jakarta Bean Validation). Класс с ограничениями помечается `@Introspected` (чтобы валидатор читал поля без рефлексии), а в методе ставится `@Valid`:

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

**Обработка ошибок** — через `@Error`. Метод с `@Error` ловит исключение и превращает его в HTTP-ответ; локальный обработчик действует в пределах контроллера, `@Error(global = true, ...)` — на всё приложение (типично для `ConstraintViolationException` из валидации):

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

## Q10. (!) Что такое декларативные HTTP-клиенты?

Декларативный клиент — это просто интерфейс, помеченный `@Client`, по которому Micronaut **в compile time** генерирует рабочую реализацию. Вы описываете контракт (методы с `@Get`/`@Post`), а код запросов писать не нужно — его собирает annotation processor.

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

Реализация генерируется в compile time — никакого runtime-прокси, всё type-safe, и работает в native image.

**Аналогия:** это как **Feign**/**OpenFeign** из Spring Cloud, но без рефлексии и без отдельной runtime-генерации прокси — клиент готов уже на этапе сборки.

## Q11. Чем отличаются реактивные и блокирующие клиенты?

Разница — только в **возвращаемом типе метода**: `List<User>` → блокирующий вызов, `Flux<User>`/`Mono<User>` → реактивный (Project Reactor), `CompletableFuture<...>` → асинхронный. Один и тот же `@Client` обслуживает все стили; Micronaut сам подбирает реализацию под тип.

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

Итог: меняя возвращаемый тип, вы переключаете стиль клиента, не трогая остальной код, — Micronaut адаптирует исполнение автоматически.

## Q12. (!) Как организовать конфигурацию?

Конфиг лежит в `application.yml` (поддерживаются также `properties`, `groovy`, `toml` и переменные окружения), а типобезопасно читается двумя способами: `@ConfigurationProperties("prefix")` маппит целый блок на класс, `@Value("${...}")` подставляет одно значение. В YAML работают плейсхолдеры с дефолтами вида `${DB_USER:admin}`.

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

Форматы конфигурации: `properties`, `yml`, `groovy`, `toml`, плюс переменные окружения.

## Q13. Как делать конфиги под разные окружения (dev/prod/test)?

Принцип тот же, что в Spring: файл `application-{env}.yml` на каждое окружение плюс общий `application.yml`. Активное окружение выбирается системным свойством `micronaut.environments` или переменной `MICRONAUT_ENVIRONMENTS`; можно перечислить несколько через запятую — они накладываются друг на друга.

Файлы под разные окружения:

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

Несколько окружений сразу — `prod,k8s,aws`: каждое следующее переопределяет значения предыдущего. `test` подхватывается автоматически в `@MicronautTest`.

## Q14. (!) Что такое Micronaut Data?

`Micronaut Data` — это слой доступа к данным с генерацией реализации репозиториев на этапе компиляции: аналог Spring Data, но без рефлексии и без разбора имён методов в runtime. Вы объявляете интерфейс с finder-методами, а компилятор сразу транслирует их в SQL/JPQL/запросы к MongoDB.

```java
@Repository
public interface UserRepository extends CrudRepository<User, Long> {
    List<User> findByName(String name);
    Optional<User> findByEmail(String email);

    @Query("UPDATE User u SET u.active = false WHERE u.lastLogin < :date")
    void deactivateInactive(LocalDate date);
}
```

**Главное отличие от Spring Data:** там `findByName` разбирается в строку запроса при старте приложения через рефлексию; здесь — на компиляции. Если в имени метода ошибка или нет такого поля, проект просто не соберётся, а не упадёт в рантайме. Плюс — это работает в native image.

## Q15. Чем различаются JDBC-, JPA- и R2DBC-репозитории?

Micronaut Data — это не один движок, а несколько бэкендов под единый API репозиториев. Выбор задаётся аннотацией репозитория и определяет компромисс «вес/возможности»:

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

**Эмпирическое правило:** JDBC-вариант не тащит Hibernate (нет lazy-loading, кэшей первого уровня, dirty checking) → меньше памяти и быстрее старт; подходит микросервисам с простыми моделями. JPA берут, когда нужны фичи Hibernate; R2DBC — когда нужен полностью неблокирующий доступ к БД (методы возвращают `Flux`/`Mono`).

## Q16. (!) Как Micronaut поддерживает GraalVM Native Image?

Native image поддерживается из коробки одной командой сборки — это был один из проектных принципов фреймворка, а не более поздняя надстройка. Micronaut стал **первым** мейнстримным JVM-фреймворком с полноценной поддержкой GraalVM.

```bash
./gradlew nativeCompile
# или Maven
./mvnw package -Dpackaging=native-image
```

**Почему это «бесплатно».** Главный враг native image — рефлексия: GraalVM при closed-world анализе не видит динамические обращения и выкидывает классы. У Micronaut этой проблемы почти нет, потому что всё статично уже на компиляции:
- Нет рефлексии в DI
- Нет рефлексии в AOP
- Compile-time bean introspection
- Готовые reflection metadata для известных libs

В отличие от Spring Boot, где Spring Native долго был отдельным проектом, Micronaut проектировался под native с самого начала. **Результат:** native-бинари обычно меньше и стартуют быстрее, чем у Spring Native.

## Q17. Какие подводные камни у native image?

Native image убирает рефлексию, но взамен заставляет следить за тем, что раньше «просто работало». Основные грабли:

**Подводные камни:**
1. **`@Introspected` на DTO обязателен** — без него Jackson не получит reflection metadata и не сможет сериализовать объект в native. Легко забыть, и упадёт только в native-сборке, не на JVM.
2. **Сторонние библиотеки** могут требовать ручной reflect-config, если внутри используют рефлексию.
3. **Долгая сборка** — native-компиляция занимает 5–15 минут (на JVM сборка секунды).
4. **Ниже пиковый throughput** — без полноценного JIT он примерно на ~30% меньше, чем у прогретой JVM; native выигрывает в старте и памяти, но не в максимальной пропускной способности.
5. **Хуже читаются стектрейсы** в native-бинаре.

```java
@Introspected
public class User { ... } // обязательно для DTO в native
```

## Q18. (!) Что умеет @MicronautTest?

`@MicronautTest` поднимает полноценный application context для теста и позволяет инжектить бины и `@Client` прямо в тестовый класс — это интеграционный тест «как в проде», но в рамках JUnit. Мокать бины можно через `@MockBean`. По умолчанию каждый тест выполняется в транзакции с откатом после, а с Micronaut 4 поднимать инфраструктуру (Postgres, Kafka) помогают Test Resources.

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

**Транзакционность по умолчанию:** каждый тест оборачивается в транзакцию, которая откатывается после завершения, — база остаётся чистой между тестами без ручного `cleanup`.

**Test Resources (с Micronaut 4)** — аналог Quarkus Dev Services: автоматически поднимают Postgres, Kafka и т.п. в контейнерах на время тестов, чтобы не настраивать инфраструктуру вручную.

## Q19. (!) Как устроены service discovery и управление конфигурацией?

Это встроено в ядро — отдельный `Spring Cloud` не нужен. Service discovery и распределённый config включаются конфигом, после чего сервис сам регистрируется в реестре и тянет настройки из централизованного хранилища.

```yaml
consul:
  client:
    registration:
      enabled: true
    config:
      enabled: true
```

**Поддерживаемые реестры:**
- **Consul** (HashiCorp) — service registry + config
- **Eureka** (Netflix)
- **Kubernetes** native discovery
- **AWS Cloud Map**
- **Distributed config:** Consul, Spring Cloud Config server, AWS Parameter Store, Vault, etcd

На практике это значит, что декларативному клиенту можно дать логическое имя сервиса вместо хардкода URL — адрес резолвится через реестр в момент вызова:

```java
@Client(id = "user-service") // resolves через service discovery
interface UserClient { ... }
```

## Q20. Как Micronaut поддерживает serverless (AWS Lambda, GCP Functions)?

Это «домашний» сценарий Micronaut: быстрый старт и низкая память — ровно то, за что в serverless платят и где штрафует cold start. Функцию генерируют CLI-командой, а handler наследуют от `MicronautRequestHandler`.

```bash
mn create-function-app my-function --features=aws-lambda,graalvm
```

**Почему это выгодно для Lambda** — холодный старт радикально ниже:
- Native-бинарь — cold start ~30 ms
- JVM — ~500 ms (против 5–10 секунд у Spring Boot)

В serverless вы платите за время работы и страдаете от холодных стартов на каждом масштабировании, поэтому эти цифры напрямую бьют по latency и счёту.

```java
public class MyHandler extends MicronautRequestHandler<APIGatewayProxyRequestEvent, APIGatewayProxyResponseEvent> {
    @Inject UserService service;

    @Override
    public APIGatewayProxyResponseEvent execute(APIGatewayProxyRequestEvent input) {
        // ...
    }
}
```

По той же схеме работают Google Cloud Functions, Azure Functions и OCI Functions.

## Q21. Как настроить distributed tracing и метрики?

Tracing и метрики включаются конфигом, без своего кода. Трейсинг шлётся в Zipkin/Jaeger (или через OpenTelemetry), метрики собираются Micrometer и экспортируются в Prometheus — ровно как в Spring Boot. В коде остаётся лишь точечно размечать спаны аннотациями `@ContinueSpan`/`@SpanTag`.

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

Итого: трассировка — через **OpenTelemetry**, **Zipkin** или **Jaeger**; метрики — через **Micrometer**, как в Spring Boot.

## Q22. Как устроена реактивная поддержка (Reactor, RxJava)?

Реактивность встроена: контроллер просто возвращает `Flux`/`Mono` (Project Reactor по умолчанию) или типы RxJava — и эндпоинт становится неблокирующим. Под капотом Netty + Reactor, поэтому реактивный стек тут «родной», а не надстройка.

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

**Поддерживаемые библиотеки:** Reactor (по умолчанию) и RxJava.

**Важное отличие от Spring WebFlux:** там переход на реактивный стек обычно «всё или ничего». В Micronaut реактивные и блокирующие эндпоинты спокойно живут в одном приложении — можно мигрировать постепенно и держать оба стиля рядом.

## Q23. (!) Когда стоит выбирать Micronaut?

Короткий критерий: Micronaut оправдан там, где важны быстрый старт и низкая память, а экзотических интеграций не требуется. Если же есть большая Spring-кодовая база или нужны редкие интеграции — выгода тает.

**Выбирайте Micronaut, когда:**
- Нужны микросервисы с **низким cold start** (Lambda, autoscaling) — это его ключевое преимущество.
- Хочется **чистого compile-time** подхода (DI, AOP, валидация) с ранней проверкой ошибок.
- Команде близок Spring — синтаксис похож, порог входа низкий.
- Нужны **декларативные HTTP-клиенты** (как Feign, но в compile time).
- Нужна **встроенная** поддержка cloud (Consul, Eureka, K8s) без Spring Cloud.

**Не выбирайте, когда:**
- Уже есть большая Spring-кодовая база — переписывать дороже, чем выгода.
- Нужны редкие интеграции — у Spring экосистема шире.
- Команда не готова осваивать новый стек.

## Q24. (!) Какие минусы у Micronaut?

Большинство минусов — про **зрелость экосистемы и инструментов**, а не про саму архитектуру. Compile-time подход даёт скорость, но платой идёт более долгая сборка и обязательная аккуратность с native.

**Минусы:**
1. **Меньше community**, чем у Spring (хотя в отдельных нишах — больше Quarkus).
2. **Меньше готовых интеграций** — Kafka, RabbitMQ есть, но экзотику придётся писать руками.
3. **Документация** уступает Spring по объёму примеров.
4. **Слабее поддержка в IDEA/JetBrains** — часть рефакторингов работает хуже.
5. **Native требует `@Introspected` на всех DTO** — легко забыть, и упадёт только в native-сборке.
6. **Сторонние библиотеки** часто требуют донастройки для native.
7. **Дольше сборка и медленнее editor feedback** в больших проектах — плата за генерацию кода на компиляции.

## Q25. Как Micronaut, Quarkus и Spring Boot соотносятся по производительности?

| Метрика | Micronaut | Quarkus | Spring Boot |
|---------|-----------|---------|-------------|
| Cold start (JVM) | 1.0-1.5 сек | 1.0-1.5 сек | 3-15 сек |
| Cold start (native) | ~30 ms | ~10-30 ms | ~50-200 ms |
| Memory (JVM) | 80-120 MB | 80-150 MB | 150-300 MB |
| Memory (native) | 30-50 MB | 30-50 MB | 50-100 MB |
| Throughput (req/s) | ~100K | ~100K | ~80-100K |

**Вывод:** по throughput разница между всеми тремя в пределах погрешности — выбор сводится не к ней. Реальный разрыв — в **старте и памяти**, и здесь Micronaut с Quarkus практически вровень, а оба заметно опережают старый Spring Boot (до 3.0). Современный Spring Boot 3+ подтянулся, но в native всё ещё уступает по cold start и размеру бинаря.

## Q26. (!) Какие компании используют Micronaut в production?

Micronaut закрепился в enterprise-сегменте, особенно там, где важны быстрый старт и низкая память (микросервисы, FinTech).

**Кто использует:**
- **Oracle** — внутренние сервисы (фреймворк родственный — вышел из OCI).
- **Boeing**
- **Walmart Labs**
- **Target**
- **Goldman Sachs** — отдельные сервисы.
- **Различные FinTech** — для микросервисов.

Публичности меньше, чем у Spring или Quarkus, но реальное присутствие в enterprise хорошее.

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

