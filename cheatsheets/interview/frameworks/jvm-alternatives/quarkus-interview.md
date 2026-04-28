---
title: "Вопросы на собеседовании: Quarkus"
description: "Quarkus — Kubernetes-native Java-фреймворк от Red Hat. GraalVM native image, build-time DI, supersonic startup, Reactive с Mutiny, MicroProfile, Hibernate ORM with Panache"
tags:
  - interview
  - frameworks
  - quarkus-interview
type: "interview"
difficulty: "intermediate"
aliases:
  - "Вопросы на собеседовании"
  - "Quarkus"
  - "Quarkus interview"
  - "Quarkus собеседование"
prerequisites:
  - "[[quarkus-basics]]"
next: []
updated: "2026-04-25"
---
# Вопросы на собеседовании: `Quarkus`

`Quarkus` — Java-фреймворк от Red Hat, ориентированный на **Kubernetes-native приложения**. Главные фичи: **build-time DI**, **GraalVM native image** (запуск за миллисекунды, потребление ~30MB RAM), **MicroProfile** API, реактивный стек на Mutiny, hot-reload в dev-mode.

## Полезные ссылки

### Официальная документация и авторитетные источники

- [Quarkus Official Documentation](https://quarkus.io/guides/)
- [Quarkus GitHub](https://github.com/quarkusio/quarkus)
- [Quarkus Performance — Red Hat](https://www.redhat.com/en/blog/why-quarkus-best-cloud-native-java-framework)
- [Quarkus vs Spring Boot — Baeldung](https://www.baeldung.com/spring-boot-vs-quarkus)
- [GraalVM Native Image](https://www.graalvm.org/native-image/)
- [Mutiny Reactive Programming](https://smallrye.io/smallrye-mutiny/)
- [MicroProfile Specification](https://microprofile.io/)

## Содержание

- [Полезные ссылки](#полезные-ссылки)
- [See also](#see-also)

**Базовые понятия**
- [Q1. (!) Что такое Quarkus и зачем он нужен?](#q1--что-такое-quarkus-и-зачем-он-нужен)
- [Q2. (!) Чем Quarkus отличается от Spring Boot?](#q2--чем-quarkus-отличается-от-spring-boot)
- [Q3. (!) Что такое supersonic subatomic Java?](#q3--что-такое-supersonic-subatomic-java)
- [Q4. Что такое extensions в Quarkus?](#q4-что-такое-extensions-в-quarkus)

**Build-time архитектура**
- [Q5. (!) Что такое build-time DI и почему это важно?](#q5--что-такое-build-time-di-и-почему-это-важно)
- [Q6. (!) Как Quarkus генерирует код во время сборки?](#q6--как-quarkus-генерирует-код-во-время-сборки)
- [Q7. Что такое recorder и BuildItem?](#q7-что-такое-recorder-и-builditem)

**GraalVM Native Image**
- [Q8. (!) Что такое GraalVM Native Image?](#q8--что-такое-graalvm-native-image)
- [Q9. (!) Как Quarkus собирает native binary?](#q9--как-quarkus-собирает-native-binary)
- [Q10. (!) Какие преимущества и недостатки native image?](#q10--какие-преимущества-и-недостатки-native-image)
- [Q11. Что такое closed-world assumption?](#q11-что-такое-closed-world-assumption)
- [Q12. Как работает reflection в native image?](#q12-как-работает-reflection-в-native-image)

**REST и MicroProfile**
- [Q13. (!) RESTEasy Reactive vs RESTEasy Classic?](#q13--resteasy-reactive-vs-resteasy-classic)
- [Q14. (!) Какие части MicroProfile поддерживает Quarkus?](#q14--какие-части-microprofile-поддерживает-quarkus)
- [Q15. Config через MicroProfile Config?](#q15-config-через-microprofile-config)
- [Q16. Health, Metrics, OpenAPI — встроенные?](#q16-health-metrics-openapi--встроенные)

**Реактивный стек**
- [Q17. (!) Что такое Mutiny?](#q17--что-такое-mutiny)
- [Q18. Чем Mutiny отличается от Reactor и RxJava?](#q18-чем-mutiny-отличается-от-reactor-и-rxjava)
- [Q19. Uni и Multi в Mutiny?](#q19-uni-и-multi-в-mutiny)
- [Q20. (!) Vert.x под капотом?](#q20--vertx-под-капотом)

**Persistence**
- [Q21. (!) Hibernate ORM with Panache?](#q21--hibernate-orm-with-panache)
- [Q22. Active Record vs Repository pattern?](#q22-active-record-vs-repository-pattern)
- [Q23. Hibernate Reactive — что это?](#q23-hibernate-reactive--что-это)

**Dev experience**
- [Q24. (!) Что такое dev mode и live reload?](#q24--что-такое-dev-mode-и-live-reload)
- [Q25. Continuous testing?](#q25-continuous-testing)
- [Q26. Dev Services?](#q26-dev-services)

**Тестирование**
- [Q27. (!) @QuarkusTest и его особенности?](#q27--quarkustest-и-его-особенности)
- [Q28. RestAssured интеграция?](#q28-restassured-интеграция)

**Production**
- [Q29. (!) Как Quarkus интегрируется с Kubernetes?](#q29--как-quarkus-интегрируется-с-kubernetes)
- [Q30. (!) Когда выбирать Quarkus вместо Spring Boot?](#q30--когда-выбирать-quarkus-вместо-spring-boot)
- [Q31. (!) Какие минусы Quarkus?](#q31--какие-минусы-quarkus)

## Q1. (!) Что такое Quarkus и зачем он нужен?

`Quarkus` — Java-фреймворк от **Red Hat** (с 2019), оптимизированный для **Kubernetes-native** приложений. Главная цель — **сделать Java конкурентоспособной с Go и Node.js** в облачной среде.

**Ключевые особенности:**
- **Supersonic startup** — запуск за десятки миллисекунд (native image)
- **Subatomic memory** — потребление от ~30MB RAM
- **Build-time оптимизации** — DI, конфигурация, аннотации обрабатываются при сборке
- **Live reload** в dev-mode без рестарта JVM
- **MicroProfile** API + Quarkus-specific extensions
- **Reactive первого класса** — через Mutiny и Vert.x


> [!mcq]
> - [x] Правильный ответ | Объяснение концепции 2-3 предложения Ключевое отличие и best practice в production.
> - [ ] Неправильный вариант 1 | Почему ошибка в этом подходе Частая ошибка в реальном коде.
> - [ ] Неправильный вариант 2 | Это смежное, но отличное понятие Частая ошибка в реальном коде.
> - [ ] Неправильный вариант 3 | Противоположное направление## Q2. (!) Чем Quarkus отличается от Spring Boot? Частая ошибка в реальном коде.

| Критерий | Quarkus | Spring Boot |
|----------|---------|-------------|
| DI | **Build-time** (CDI + ArC) | Runtime (Spring Container) |
| Cold start (JVM) | 1-2 сек | 3-15 сек |
| Cold start (native) | 0.01-0.05 сек | 0.05-0.2 сек (Spring Native) |
| Memory (JVM) | ~80-150 MB | ~150-300 MB |
| Memory (native) | ~30-50 MB | ~50-100 MB |
| Standard | MicroProfile + Jakarta EE + Quarkus | Spring (свой стандарт) |
| Reactive | Mutiny + Vert.x | Reactor (WebFlux) |
| ORM | Hibernate + Panache | Spring Data JPA |
| Native image | First-class | Через Spring Native (стало частью Boot 3.x) |
| Dev experience | Live reload, Dev Services | DevTools |
| Маркетинг | Cloud-native, Kubernetes | General-purpose |


> [!mcq]
> - [x] Правильный ответ | Объяснение концепции 2-3 предложения Ключевое отличие и best practice в production.
> - [ ] Неправильный вариант 1 | Почему ошибка в этом подходе Частая ошибка в реальном коде.
> - [ ] Неправильный вариант 2 | Это смежное, но отличное понятие Частая ошибка в реальном коде.
> - [ ] Неправильный вариант 3 | Противоположное направление## Q3. (!) Что такое supersonic subatomic Java? Частая ошибка в реальном коде.

Маркетинговый слоган Quarkus:

- **Supersonic** (сверхзвуковой) — startup за десятки миллисекунд (native), 1-2 сек (JVM)
- **Subatomic** (субатомный) — память от 30 MB

Достигается за счёт:
1. **Build-time обработки** — annotations процессятся при компиляции
2. **Tree-shaking** — убираются неиспользуемые классы
3. **GraalVM native image** — компиляция в нативный бинарник
4. **Минимизация runtime reflection** — известные паттерны заменяются на сгенерированный код


> [!mcq]
> - [x] Правильный ответ | Объяснение концепции 2-3 предложения Ключевое отличие и best practice в production.
> - [ ] Неправильный вариант 1 | Почему ошибка в этом подходе Частая ошибка в реальном коде.
> - [ ] Неправильный вариант 2 | Это смежное, но отличное понятие Частая ошибка в реальном коде.
> - [ ] Неправильный вариант 3 | Противоположное направление## Q4. Что такое extensions в Quarkus? Частая ошибка в реальном коде.

`Extension` — модульный компонент, добавляющий функциональность. Аналог Spring Boot starters, но с **build-time оптимизациями**.

```xml
<dependency>
    <groupId>io.quarkus</groupId>
    <artifactId>quarkus-resteasy-reactive</artifactId>
</dependency>
<dependency>
    <groupId>io.quarkus</groupId>
    <artifactId>quarkus-hibernate-orm-panache</artifactId>
</dependency>
<dependency>
    <groupId>io.quarkus</groupId>
    <artifactId>quarkus-jdbc-postgresql</artifactId>
</dependency>
```

Каждое extension содержит:
- **Runtime module** — код, исполняющийся в приложении
- **Deployment module** — код, исполняющийся при сборке (генерация bean, конфигурация)

Поэтому extensions знают про native image и могут регистрировать reflection metadata автоматически.


> [!mcq]
> - [x] Правильный ответ | Объяснение концепции 2-3 предложения Ключевое отличие и best practice в production.
> - [ ] Неправильный вариант 1 | Почему ошибка в этом подходе Частая ошибка в реальном коде.
> - [ ] Неправильный вариант 2 | Это смежное, но отличное понятие Частая ошибка в реальном коде.
> - [ ] Неправильный вариант 3 | Противоположное направление## Q5. (!) Что такое build-time DI и почему это важно? Частая ошибка в реальном коде.

В Spring DI происходит при **запуске** — сканируются classpath, создаются BeanFactory, обрабатываются annotations.

В Quarkus всё это делается **во время сборки** через **ArC** (Quarkus CDI implementation):

```
Spring (runtime):
  Compile → Start → Scan classpath → Reflection → Build BeanFactory → Run

Quarkus (build-time):
  Compile → Process annotations → Generate bean code → Bake into JAR
  Start → Run (всё уже готово)
```

**Преимущества:**
- Faster startup (нет работы при старте)
- Меньше runtime reflection → возможен native image
- Меньше памяти (не нужен метаданные о классах в RAM)
- Ошибки конфигурации видны на этапе сборки


> [!mcq]
> - [x] Правильный ответ | Объяснение концепции 2-3 предложения Ключевое отличие и best practice в production.
> - [ ] Неправильный вариант 1 | Почему ошибка в этом подходе Частая ошибка в реальном коде.
> - [ ] Неправильный вариант 2 | Это смежное, но отличное понятие Частая ошибка в реальном коде.
> - [ ] Неправильный вариант 3 | Противоположное направление## Q6. (!) Как Quarkus генерирует код во время сборки? Частая ошибка в реальном коде.

Через **Maven/Gradle plugin** + **deployment modules** extensions:

1. **Augmentation phase** — анализ classpath, обработка аннотаций, генерация bean'ов
2. **Static init** — код, исполняемый один раз при сборке (вычисление конфигурации)
3. **Runtime init** — код, исполняемый при старте

Сгенерированный код добавляется в JAR. При запуске — нет сканирования, нет рефлексии.

```java
// Что мы пишем
@ApplicationScoped
public class UserService {
    @Inject UserRepository repo;
    public User findById(Long id) { return repo.findById(id); }
}

// Что Quarkus генерирует во время сборки
public class UserService_Bean implements InjectableBean<UserService> {
    @Override
    public UserService create(CreationalContext context) {
        UserService instance = new UserService();
        instance.repo = container.instance(UserRepository.class);
        return instance;
    }
}
```


> [!mcq]
> - [x] Правильный ответ | Объяснение концепции 2-3 предложения Ключевое отличие и best practice в production.
> - [ ] Неправильный вариант 1 | Почему ошибка в этом подходе Частая ошибка в реальном коде.
> - [ ] Неправильный вариант 2 | Это смежное, но отличное понятие Частая ошибка в реальном коде.
> - [ ] Неправильный вариант 3 | Противоположное направление## Q7. Что такое recorder и BuildItem? Частая ошибка в реальном коде.

`BuildItem` — единица данных, передаваемая между **build steps** (методы, обрабатывающие какие-то аспекты сборки).

```java
@BuildStep
void processConfigurations(BuildProducer<MyConfigBuildItem> producer) {
    producer.produce(new MyConfigBuildItem("value"));
}

@BuildStep
void useConfigurations(MyConfigBuildItem config, ...) {
    // потребляем
}
```

`Recorder` — механизм для **записи** runtime-действий во время сборки, которые исполняются при старте.

Это ядро Quarkus — позволяет переносить работу со старта в build time.


> [!mcq]
> - [x] Правильный ответ | Объяснение концепции 2-3 предложения Ключевое отличие и best practice в production.
> - [ ] Неправильный вариант 1 | Почему ошибка в этом подходе Частая ошибка в реальном коде.
> - [ ] Неправильный вариант 2 | Это смежное, но отличное понятие Частая ошибка в реальном коде.
> - [ ] Неправильный вариант 3 | Противоположное направление## Q8. (!) Что такое GraalVM Native Image? Частая ошибка в реальном коде.

**GraalVM Native Image** — компилятор от Oracle, превращающий JVM-байткод в **нативный исполняемый файл** (без JVM).

```bash
native-image -jar myapp.jar  # → myapp (linux/macOS executable)
```

**Особенности:**
- AOT-компиляция (Ahead-Of-Time)
- Закрытый мир (closed-world): все классы должны быть известны при сборке
- Нет dynamic class loading (по умолчанию)
- Reflection и dynamic proxy нужно регистрировать

**Преимущества:**
- Старт за миллисекунды (нет JIT warmup)
- Память — от ~30 MB
- Не нужен JVM в runtime


> [!mcq]
> - [x] Правильный ответ | Объяснение концепции 2-3 предложения Ключевое отличие и best practice в production.
> - [ ] Неправильный вариант 1 | Почему ошибка в этом подходе Частая ошибка в реальном коде.
> - [ ] Неправильный вариант 2 | Это смежное, но отличное понятие Частая ошибка в реальном коде.
> - [ ] Неправильный вариант 3 | Противоположное направление## Q9. (!) Как Quarkus собирает native binary? Частая ошибка в реальном коде.

```bash
# Maven
./mvnw package -Pnative

# Gradle
./gradlew build -Dquarkus.package.type=native

# Без локального GraalVM — в Docker контейнере
./mvnw package -Pnative -Dquarkus.native.container-build=true
```

Сборка занимает **2-10 минут** (медленнее обычной). Результат — `target/myapp-runner` (linux executable, ~30-100 MB).

```bash
docker build -f src/main/docker/Dockerfile.native -t myapp .
docker run -p 8080:8080 myapp
```


> [!mcq]
> - [x] Правильный ответ | Объяснение концепции 2-3 предложения Ключевое отличие и best practice в production.
> - [ ] Неправильный вариант 1 | Почему ошибка в этом подходе Частая ошибка в реальном коде.
> - [ ] Неправильный вариант 2 | Это смежное, но отличное понятие Частая ошибка в реальном коде.
> - [ ] Неправильный вариант 3 | Противоположное направление## Q10. (!) Какие преимущества и недостатки native image? Частая ошибка в реальном коде.

**Преимущества:**
- Startup за **миллисекунды** (vs 1-15 сек на JVM)
- Памяти **в 5-10 раз меньше** (30-50 MB vs 150-500 MB)
- Не нужен JVM в runtime → меньше Docker image
- Идеально для serverless (Lambda, Cloud Run)
- Идеально для horizontal scaling в Kubernetes

**Недостатки:**
- **Долгая сборка** (2-10 минут)
- **Сложности с reflection** — нужно регистрировать классы
- **Нет JIT** — peak throughput ниже на ~30-50%
- **Нет dynamic class loading** (без специальной настройки)
- **Сложнее отладка** (gdb, не jdb)
- **Профилировщики** ограничены


> [!mcq]
> - [x] Правильный ответ | Объяснение концепции 2-3 предложения Ключевое отличие и best practice в production.
> - [ ] Неправильный вариант 1 | Почему ошибка в этом подходе Частая ошибка в реальном коде.
> - [ ] Неправильный вариант 2 | Это смежное, но отличное понятие Частая ошибка в реальном коде.
> - [ ] Неправильный вариант 3 | Противоположное направление## Q11. Что такое closed-world assumption? Частая ошибка в реальном коде.

**Closed-world assumption (CWA)** — Native Image предполагает, что **все** классы, методы, поля, ресурсы известны на этапе компиляции.

**Что нельзя в native image (по умолчанию):**
- Reflection с unknown classes
- Dynamic proxy
- Class.forName() с динамическими именами
- Resource loading с динамическими путями
- Динамическая загрузка JAR

**Решение:** **reachability metadata** — JSON конфигурация, объявляющая reflection-используемые классы:

```json
[
  {
    "name": "com.example.MyClass",
    "allDeclaredFields": true,
    "allDeclaredMethods": true
  }
]
```

Quarkus extensions делают это **автоматически** для known frameworks (Hibernate, Jackson, ...).


> [!mcq]
> - [x] Правильный ответ | Объяснение концепции 2-3 предложения Ключевое отличие и best practice в production.
> - [ ] Неправильный вариант 1 | Почему ошибка в этом подходе Частая ошибка в реальном коде.
> - [ ] Неправильный вариант 2 | Это смежное, но отличное понятие Частая ошибка в реальном коде.
> - [ ] Неправильный вариант 3 | Противоположное направление## Q12. Как работает reflection в native image? Частая ошибка в реальном коде.

Reflection работает только если класс зарегистрирован в **reachability metadata**.

```java
// Quarkus автоматически регистрирует классы с @RegisterForReflection
@RegisterForReflection
public class MyDto { ... }

// Или programmatically:
@BuildStep
ReflectiveClassBuildItem registerForReflection() {
    return new ReflectiveClassBuildItem(true, true, "com.example.MyClass");
}
```

Без регистрации — `ClassNotFoundException` или `NoSuchMethodException` в runtime.

Извлечение metadata можно автоматизировать через **GraalVM Native Image Tracing Agent**:

```bash
java -agentlib:native-image-agent=config-output-dir=meta-conf -jar app.jar
```

Запускаем приложение с тестами — agent записывает все reflection-вызовы.


> [!mcq]
> - [x] Правильный ответ | Объяснение концепции 2-3 предложения Ключевое отличие и best practice в production.
> - [ ] Неправильный вариант 1 | Почему ошибка в этом подходе Частая ошибка в реальном коде.
> - [ ] Неправильный вариант 2 | Это смежное, но отличное понятие Частая ошибка в реальном коде.
> - [ ] Неправильный вариант 3 | Противоположное направление## Q13. (!) RESTEasy Reactive vs RESTEasy Classic? Частая ошибка в реальном коде.

В Quarkus 2.0+ есть две реализации REST:

**RESTEasy Reactive** (рекомендуется):
- Async по умолчанию
- Построен на Vert.x
- Высокий throughput
- Поддержка Mutiny `Uni`/`Multi`

**RESTEasy Classic** (legacy):
- Synchronous (blocking)
- Servlet-based
- Совместим с JAX-RS API

```java
@Path("/users")
public class UserResource {
    @Inject UserRepository repo;

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Uni<List<User>> list() { // async с Mutiny
        return repo.listAll();
    }

    @GET
    @Path("{id}")
    public Uni<User> get(@PathParam("id") Long id) {
        return repo.findById(id);
    }
}
```

JAX-RS API — стандарт Java EE / Jakarta. Похож на Spring MVC аннотации.


> [!mcq]
> - [x] Правильный ответ | Объяснение концепции 2-3 предложения Ключевое отличие и best practice в production.
> - [ ] Неправильный вариант 1 | Почему ошибка в этом подходе Частая ошибка в реальном коде.
> - [ ] Неправильный вариант 2 | Это смежное, но отличное понятие Частая ошибка в реальном коде.
> - [ ] Неправильный вариант 3 | Противоположное направление## Q14. (!) Какие части MicroProfile поддерживает Quarkus? Частая ошибка в реальном коде.

**MicroProfile** — набор стандартов для облачных Java-приложений (от Eclipse Foundation).

| MicroProfile API | Quarkus поддержка |
|------------------|-------------------|
| Config | `quarkus-arc`, через `@ConfigProperty` |
| Health | `quarkus-smallrye-health` |
| Metrics | `quarkus-micrometer-registry-prometheus` |
| OpenAPI | `quarkus-smallrye-openapi` |
| Fault Tolerance | `quarkus-smallrye-fault-tolerance` (retry, circuit breaker) |
| JWT | `quarkus-smallrye-jwt` |
| REST Client | `quarkus-rest-client-reactive` |
| OpenTracing | `quarkus-opentelemetry` |

```java
@RestClient
@RegisterRestClient(configKey = "user-api")
public interface UserApi {
    @GET @Path("/{id}")
    Uni<User> findById(@PathParam("id") Long id);
}
```


> [!mcq]
> - [x] Правильный ответ | Объяснение концепции 2-3 предложения Ключевое отличие и best practice в production.
> - [ ] Неправильный вариант 1 | Почему ошибка в этом подходе Частая ошибка в реальном коде.
> - [ ] Неправильный вариант 2 | Это смежное, но отличное понятие Частая ошибка в реальном коде.
> - [ ] Неправильный вариант 3 | Противоположное направление## Q15. Config через MicroProfile Config? Частая ошибка в реальном коде.

```properties
# application.properties
my.greeting=Hello, %s!
my.timeout=PT30S
quarkus.datasource.jdbc.url=jdbc:postgresql://localhost:5432/mydb
```

```java
@ConfigProperty(name = "my.greeting", defaultValue = "Hi, %s!")
String greeting;

@ConfigProperty(name = "my.timeout")
Duration timeout;

// Через record (Quarkus 3+)
@ConfigMapping(prefix = "my")
public interface MyConfig {
    String greeting();
    Duration timeout();
}
```

Поддерживаются типы: `String`, `int`, `boolean`, `Duration`, `Optional<T>`, `List<T>`, custom через converters.


> [!mcq]
> - [x] Правильный ответ | Объяснение концепции 2-3 предложения Ключевое отличие и best practice в production.
> - [ ] Неправильный вариант 1 | Почему ошибка в этом подходе Частая ошибка в реальном коде.
> - [ ] Неправильный вариант 2 | Это смежное, но отличное понятие Частая ошибка в реальном коде.
> - [ ] Неправильный вариант 3 | Противоположное направление## Q16. Health, Metrics, OpenAPI — встроенные? Частая ошибка в реальном коде.

```xml
<!-- Достаточно подключить extensions -->
<dependency>
    <groupId>io.quarkus</groupId>
    <artifactId>quarkus-smallrye-health</artifactId>
</dependency>
<dependency>
    <groupId>io.quarkus</groupId>
    <artifactId>quarkus-micrometer-registry-prometheus</artifactId>
</dependency>
<dependency>
    <groupId>io.quarkus</groupId>
    <artifactId>quarkus-smallrye-openapi</artifactId>
</dependency>
```

Endpoints:
- `/q/health` — overall
- `/q/health/live` — liveness
- `/q/health/ready` — readiness
- `/q/metrics` — Prometheus метрики
- `/q/openapi` — OpenAPI spec
- `/q/swagger-ui` — Swagger UI
- `/q/dev` — dev console (только в dev mode)

```java
@Liveness
public class DatabaseHealthCheck implements HealthCheck {
    @Override
    public HealthCheckResponse call() {
        return HealthCheckResponse.named("db").up().build();
    }
}
```


> [!mcq]
> - [x] Правильный ответ | Объяснение концепции 2-3 предложения Ключевое отличие и best practice в production.
> - [ ] Неправильный вариант 1 | Почему ошибка в этом подходе Частая ошибка в реальном коде.
> - [ ] Неправильный вариант 2 | Это смежное, но отличное понятие Частая ошибка в реальном коде.
> - [ ] Неправильный вариант 3 | Противоположное направление## Q17. (!) Что такое Mutiny? Частая ошибка в реальном коде.

`Mutiny` — реактивная библиотека от SmallRye/Red Hat, рекомендуемая в Quarkus.

**Два основных типа:**
- `Uni<T>` — асинхронное вычисление одного значения (как `Mono<T>` в Reactor)
- `Multi<T>` — поток значений (как `Flux<T>` в Reactor)

```java
Uni<User> user = userRepo.findById(1L);

user.onItem().transform(u -> u.name)
    .onFailure().recoverWithItem("default")
    .subscribe().with(name -> log.info(name));

Multi<User> users = userRepo.streamAll();
users.onItem().invoke(u -> log.info(u.name)).subscribe();
```


> [!mcq]
> - [x] Правильный ответ | Объяснение концепции 2-3 предложения Ключевое отличие и best practice в production.
> - [ ] Неправильный вариант 1 | Почему ошибка в этом подходе Частая ошибка в реальном коде.
> - [ ] Неправильный вариант 2 | Это смежное, но отличное понятие Частая ошибка в реальном коде.
> - [ ] Неправильный вариант 3 | Противоположное направление## Q18. Чем Mutiny отличается от Reactor и RxJava? Частая ошибка в реальном коде.

| Критерий | Mutiny | Reactor | RxJava |
|----------|--------|---------|--------|
| Однообъектный тип | `Uni<T>` | `Mono<T>` | `Single<T>`, `Maybe<T>` |
| Многообъектный | `Multi<T>` | `Flux<T>` | `Observable<T>`, `Flowable<T>` |
| API style | Verbose (явные группы) | Compact (chained) | Compact |
| Onboarding | Проще | Сложнее | Сложнее |
| Backpressure | Из Multi | Built-in | Из Flowable |
| Backed by | SmallRye | Pivotal | ReactiveX |

**Verbose API в Mutiny:**

```java
uni.onItem().transform(...)
   .onFailure().recoverWithItem(...)
   .onCompletion().invoke(...)
```

vs Reactor:

```java
mono.map(...)
    .onErrorResume(...)
    .doOnSuccess(...)
```

Mutiny утверждает, что verbose API проще для junior-разработчиков (явно видно группу — что отслеживаем). На вкус.


> [!mcq]
> - [x] Правильный ответ | Объяснение концепции 2-3 предложения Ключевое отличие и best practice в production.
> - [ ] Неправильный вариант 1 | Почему ошибка в этом подходе Частая ошибка в реальном коде.
> - [ ] Неправильный вариант 2 | Это смежное, но отличное понятие Частая ошибка в реальном коде.
> - [ ] Неправильный вариант 3 | Противоположное направление## Q19. Uni и Multi в Mutiny? Частая ошибка в реальном коде.

```java
// Uni — одно значение или ошибка
Uni<String> uni = Uni.createFrom().item("hello");
Uni<String> fromCallable = Uni.createFrom().item(() -> heavyComputation());
Uni<String> fromFuture = Uni.createFrom().completionStage(future);

uni.onItem().transform(s -> s.toUpperCase())
   .onFailure().recoverWithItem("error")
   .subscribe().with(System.out::println);

// Multi — поток
Multi<Integer> multi = Multi.createFrom().items(1, 2, 3, 4, 5);
multi.onItem().transform(i -> i * 2)
     .filter(i -> i > 4)
     .subscribe().with(System.out::println);

// Combining
Uni<List<User>> users = userRepo.listAll();
Uni<List<Product>> products = productRepo.listAll();
Uni<Dashboard> dashboard = Uni.combine().all().unis(users, products)
    .asTuple()
    .onItem().transform(t -> new Dashboard(t.getItem1(), t.getItem2()));
```


> [!mcq]
> - [x] Правильный ответ | Объяснение концепции 2-3 предложения Ключевое отличие и best practice в production.
> - [ ] Неправильный вариант 1 | Почему ошибка в этом подходе Частая ошибка в реальном коде.
> - [ ] Неправильный вариант 2 | Это смежное, но отличное понятие Частая ошибка в реальном коде.
> - [ ] Неправильный вариант 3 | Противоположное направление## Q20. (!) Vert.x под капотом? Частая ошибка в реальном коде.

Quarkus reactive стек построен поверх **Eclipse Vert.x** — event-driven фреймворка для JVM.

- HTTP server — Vert.x WebServer
- DB drivers — Vert.x SQL Client (postgres, mysql, etc.)
- Mailer, Mailbox — Vert.x integrations
- Event Bus — Vert.x EventBus

```java
@Inject
Vertx vertx;

vertx.setTimer(1000, id -> log.info("Timer fired"));

@Inject
EventBus eventBus;

eventBus.publish("address", "message");
```

Quarkus делает Vert.x доступным напрямую, или оборачивает его в Mutiny API.


> [!mcq]
> - [x] Правильный ответ | Объяснение концепции 2-3 предложения Ключевое отличие и best practice в production.
> - [ ] Неправильный вариант 1 | Почему ошибка в этом подходе Частая ошибка в реальном коде.
> - [ ] Неправильный вариант 2 | Это смежное, но отличное понятие Частая ошибка в реальном коде.
> - [ ] Неправильный вариант 3 | Противоположное направление## Q21. (!) Hibernate ORM with Panache? Частая ошибка в реальном коде.

`Panache` — слой над Hibernate, упрощающий работу с entities.

```java
// Active Record style
@Entity
public class Person extends PanacheEntity {
    public String name;
    public int age;

    public static List<Person> findByName(String name) {
        return list("name", name);
    }
}

// Использование
Person p = new Person();
p.name = "Alice";
p.age = 30;
p.persist();

List<Person> people = Person.findByName("Alice");
List<Person> adults = Person.list("age >= ?1", 18);
long count = Person.count();
Person.deleteById(1L);
```

Panache добавляет статические методы и убирает boilerplate Repositories.


> [!mcq]
> - [x] Правильный ответ | Объяснение концепции 2-3 предложения Ключевое отличие и best practice в production.
> - [ ] Неправильный вариант 1 | Почему ошибка в этом подходе Частая ошибка в реальном коде.
> - [ ] Неправильный вариант 2 | Это смежное, но отличное понятие Частая ошибка в реальном коде.
> - [ ] Неправильный вариант 3 | Противоположное направление## Q22. Active Record vs Repository pattern? Частая ошибка в реальном коде.

| Подход | Active Record | Repository |
|--------|---------------|------------|
| Стиль | `Person.findById(1)` | `personRepo.findById(1)` |
| Класс | `Person extends PanacheEntity` | `PersonRepository implements PanacheRepository<Person>` |
| Тестируемость | Сложнее (статика) | Лучше (mock repo) |
| Знакомство | Rails, Django | Spring Data |

```java
// Repository pattern в Panache
@ApplicationScoped
public class PersonRepository implements PanacheRepository<Person> {
    public List<Person> findByName(String name) {
        return list("name", name);
    }
}

@Inject PersonRepository repo;
List<Person> p = repo.listAll();
```

В большинстве enterprise-проектов выбирают **Repository** — лучше для unit-тестов и DDD.


> [!mcq]
> - [x] Правильный ответ | Объяснение концепции 2-3 предложения Ключевое отличие и best practice в production.
> - [ ] Неправильный вариант 1 | Почему ошибка в этом подходе Частая ошибка в реальном коде.
> - [ ] Неправильный вариант 2 | Это смежное, но отличное понятие Частая ошибка в реальном коде.
> - [ ] Неправильный вариант 3 | Противоположное направление## Q23. Hibernate Reactive — что это? Частая ошибка в реальном коде.

`Hibernate Reactive` — реактивная версия Hibernate. Использует **non-blocking** drivers (Vert.x SQL Client) вместо JDBC.

```java
@Entity
public class Person extends PanacheEntityBase {
    @Id @GeneratedValue Long id;
    public String name;
}

@GET
@Path("/people")
public Uni<List<Person>> list() {
    return Person.listAll(); // возвращает Uni<List<Person>>
}
```

Применение: high-throughput системы, где blocking JDBC становится bottleneck. Не для всех — обычный Hibernate проще и достаточен.

Подробнее — в [Hibernate](../../databases/hibernate-interview.md).


> [!mcq]
> - [x] Правильный ответ | Объяснение концепции 2-3 предложения Ключевое отличие и best practice в production.
> - [ ] Неправильный вариант 1 | Почему ошибка в этом подходе Частая ошибка в реальном коде.
> - [ ] Неправильный вариант 2 | Это смежное, но отличное понятие Частая ошибка в реальном коде.
> - [ ] Неправильный вариант 3 | Противоположное направление## Q24. (!) Что такое dev mode и live reload? Частая ошибка в реальном коде.

```bash
./mvnw quarkus:dev
```

В dev mode:
- **Live reload** — изменения в `.java`/`.properties` подхватываются без рестарта
- **Continuous testing** — тесты запускаются автоматически
- **Dev UI** — на `/q/dev` (web интерфейс с конфигурацией, БД, kafka topics)
- **Dev Services** — автозапуск зависимостей в Docker

Смысл — `developer experience` сравнимый с Node.js/Python: меняешь код, F5 в браузере, изменения видно.


> [!mcq]
> - [x] Правильный ответ | Объяснение концепции 2-3 предложения Ключевое отличие и best practice в production.
> - [ ] Неправильный вариант 1 | Почему ошибка в этом подходе Частая ошибка в реальном коде.
> - [ ] Неправильный вариант 2 | Это смежное, но отличное понятие Частая ошибка в реальном коде.
> - [ ] Неправильный вариант 3 | Противоположное направление## Q25. Continuous testing? Частая ошибка в реальном коде.

В dev mode тесты запускаются автоматически при сохранении файла:

```
[r] - re-run all
[f] - re-run failed
[v] - toggle verbose
[p] - pause
[h] - help
```

Полезно для TDD — пишешь тест, видишь сразу прохождение.


> [!mcq]
> - [x] Правильный ответ | Объяснение концепции 2-3 предложения Ключевое отличие и best practice в production.
> - [ ] Неправильный вариант 1 | Почему ошибка в этом подходе Частая ошибка в реальном коде.
> - [ ] Неправильный вариант 2 | Это смежное, но отличное понятие Частая ошибка в реальном коде.
> - [ ] Неправильный вариант 3 | Противоположное направление## Q26. Dev Services? Частая ошибка в реальном коде.

Автоматический запуск зависимостей в Docker-контейнерах при `./mvnw quarkus:dev`:

```properties
# application.properties — конфигурация можно НЕ указывать!
quarkus.datasource.db-kind=postgresql
# Quarkus сам поднимет Postgres в Docker и подключится
```

Поддержка: PostgreSQL, MySQL, MongoDB, Kafka, Redis, Keycloak, Elasticsearch, RabbitMQ, ...

В production используется реальный сервис из конфига. Dev Services — только когда `dev mode` или `test mode`.

Сильно ускоряет onboarding нового разработчика.


> [!mcq]
> - [x] Правильный ответ | Объяснение концепции 2-3 предложения Ключевое отличие и best practice в production.
> - [ ] Неправильный вариант 1 | Почему ошибка в этом подходе Частая ошибка в реальном коде.
> - [ ] Неправильный вариант 2 | Это смежное, но отличное понятие Частая ошибка в реальном коде.
> - [ ] Неправильный вариант 3 | Противоположное направление## Q27. (!) @QuarkusTest и его особенности? Частая ошибка в реальном коде.

```java
@QuarkusTest
class PersonResourceTest {
    @Test
    void testList() {
        given()
            .when().get("/people")
            .then()
                .statusCode(200)
                .body("size()", greaterThan(0));
    }
}
```

`@QuarkusTest` запускает приложение в-процессе. Использует **RestAssured** для HTTP-тестирования.

**Mocking:**

```java
@QuarkusTest
class UserServiceTest {
    @InjectMock UserRepository repo;
    @Inject UserService service;

    @Test
    void test() {
        Mockito.when(repo.findById(1L)).thenReturn(Optional.of(new User()));
        assertNotNull(service.findById(1L));
    }
}
```

**Native test** (медленный, но проверяет работу в native image):

```java
@QuarkusIntegrationTest
class NativeTest extends BaseTest { }
```


> [!mcq]
> - [x] Правильный ответ | Объяснение концепции 2-3 предложения Ключевое отличие и best practice в production.
> - [ ] Неправильный вариант 1 | Почему ошибка в этом подходе Частая ошибка в реальном коде.
> - [ ] Неправильный вариант 2 | Это смежное, но отличное понятие Частая ошибка в реальном коде.
> - [ ] Неправильный вариант 3 | Противоположное направление## Q28. RestAssured интеграция? Частая ошибка в реальном коде.

`RestAssured` — DSL для HTTP-тестов. Quarkus идёт с готовой настройкой (порт, base URL):

```java
given()
    .contentType(ContentType.JSON)
    .body(new Person("Alice", 30))
.when()
    .post("/people")
.then()
    .statusCode(201)
    .body("id", notNullValue())
    .body("name", equalTo("Alice"));
```

Подробнее — в [Integration Testing](../../testing/integration-testing-interview.md).


> [!mcq]
> - [x] Правильный ответ | Объяснение концепции 2-3 предложения Ключевое отличие и best practice в production.
> - [ ] Неправильный вариант 1 | Почему ошибка в этом подходе Частая ошибка в реальном коде.
> - [ ] Неправильный вариант 2 | Это смежное, но отличное понятие Частая ошибка в реальном коде.
> - [ ] Неправильный вариант 3 | Противоположное направление## Q29. (!) Как Quarkus интегрируется с Kubernetes? Частая ошибка в реальном коде.

```xml
<dependency>
    <groupId>io.quarkus</groupId>
    <artifactId>quarkus-kubernetes</artifactId>
</dependency>
```

При сборке генерируются `kubernetes.yaml`, `kubernetes.json`:

```bash
./mvnw package
# Manifests в target/kubernetes/kubernetes.yaml
```

Конфигурация:

```properties
quarkus.kubernetes.namespace=production
quarkus.kubernetes.replicas=3
quarkus.kubernetes.labels.app=myapp
quarkus.kubernetes.resources.requests.memory=64Mi
quarkus.kubernetes.resources.requests.cpu=100m
quarkus.kubernetes.resources.limits.memory=128Mi
```

Также есть extensions для **Helm chart**, **Knative**, **OpenShift** генерации.


> [!mcq]
> - [x] Правильный ответ | Объяснение концепции 2-3 предложения Ключевое отличие и best practice в production.
> - [ ] Неправильный вариант 1 | Почему ошибка в этом подходе Частая ошибка в реальном коде.
> - [ ] Неправильный вариант 2 | Это смежное, но отличное понятие Частая ошибка в реальном коде.
> - [ ] Неправильный вариант 3 | Противоположное направление## Q30. (!) Когда выбирать Quarkus вместо Spring Boot? Частая ошибка в реальном коде.

**Выбирай Quarkus когда:**
- Serverless / FaaS (AWS Lambda, Knative) — критичен cold start
- Kubernetes + auto-scaling — экономия памяти важна (можно держать больше реплик)
- Edge computing, IoT — ограниченная память
- Команда любит MicroProfile, Jakarta EE
- Хочется быстрого dev-цикла (live reload без рестарта)

**Выбирай Spring Boot когда:**
- Уже есть Spring-команда, инфраструктура
- Нужны редкие интеграции (Spring имеет всё)
- Enterprise: батчи, stream processing, integration
- Проект не cloud-native (классический сервис в VM/bare metal)

В **2024** Spring Boot 3.x с Spring Native сильно сократил отставание по cold start. Quarkus всё ещё быстрее в большинстве случаев, но разрыв меньше.


> [!mcq]
> - [x] Правильный ответ | Объяснение концепции 2-3 предложения Ключевое отличие и best practice в production.
> - [ ] Неправильный вариант 1 | Почему ошибка в этом подходе Частая ошибка в реальном коде.
> - [ ] Неправильный вариант 2 | Это смежное, но отличное понятие Частая ошибка в реальном коде.
> - [ ] Неправильный вариант 3 | Противоположное направление## Q31. (!) Какие минусы Quarkus? Частая ошибка в реальном коде.

1. **Меньше community** по сравнению со Spring (документации, статей, StackOverflow меньше)
2. **MicroProfile/Jakarta EE** API менее знакомы Java-разработчикам, которые работали со Spring
3. **Native image** — сложности с reflection, third-party libraries
4. **Долгая native сборка** (2-10 минут)
5. **Меньше готовых интеграций** (хотя основные покрыты)
6. **Live reload** — иногда плохо работает при глубоких изменениях, нужно `./mvnw quarkus:dev` рестартовать
7. **Тесты в native** — медленные, нужен GraalVM на CI
8. **Обучение Mutiny** — отличается от привычных Reactor/RxJava
9. **Vendor lock-in** — экосистема Red Hat (хотя open-source)

Quarkus — отличный выбор для **новых** cloud-native проектов, но миграция со Spring редко окупается.

---

## See also

- [Spring Boot](../spring/spring-boot-interview.md) — главный конкурент
- [Ktor](ktor-interview.md) — другой lightweight JVM фреймворк
- [Micronaut](micronaut-interview.md) — ещё один build-time DI фреймворк
- [Vert.x](vertx-interview.md) — основа реактивного стека Quarkus
- [Spring WebFlux](../spring/spring-webflux-interview.md) — реактивный аналог
- [Hibernate](../../databases/hibernate-interview.md) — основа Panache
- [Spring Data JPA](../spring/spring-data-jpa-interview.md) — аналог Repository в Spring
- [Микросервисы](../../architecture/microservices-interview.md) — основное применение Quarkus
- [Kubernetes](../../devops/kubernetes-interview.md) — нативная интеграция
- [Docker](../../devops/docker-interview.md) — для native image
- [JVM](../../jvm/jvm-interview.md) — JIT vs AOT компиляция
- [JVM Performance Tuning](../../performance/jvm-performance-tuning-interview.md) — startup и memory
- [Memory Management](../../performance/memory-management-interview.md) — почему native жрёт меньше
- [Integration Testing](../../testing/integration-testing-interview.md) — RestAssured


> [!mcq]
> - [x] Правильный ответ | Объяснение концепции 2-3 предложения Ключевое отличие и best practice в production.
> - [ ] Неправильный вариант 1 | Почему ошибка в этом подходе Частая ошибка в реальном коде.
> - [ ] Неправильный вариант 2 | Это смежное, но отличное понятие Частая ошибка в реальном коде.
> - [ ] Неправильный вариант 3 | Противоположное направление- [Ktor](ktor-interview.md) Частая ошибка в реальном коде.
- [Micronaut](micronaut-interview.md)
- [Vert.x](vertx-interview.md)
- [Spring AOP](../spring/spring-aop-interview.md)
- [Spring Batch](../spring/spring-batch-interview.md)
- [Spring Boot Actuator](../spring/spring-boot-actuator-interview.md)
