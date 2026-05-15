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
>
> **Вопрос:** Что принципиально отличает Micronaut от других JVM-фреймворков и делает его подходящим для serverless и микросервисов?
>
> ---
>
> #### A) Compile-time DI/AOP без рефлексии + first-class GraalVM native image → cold start ~30ms в native — ✓ Верно
>
> **Развёрнутое объяснение:** Micronaut использует annotation processor (JSR-269), который **во время сборки** генерирует `$Definition`-классы для каждого `@Singleton`/`@Controller` и `$Intercepted`-классы для AOP. В runtime `BeanContext` просто загружает их через `ServiceLoader`, без сканирования classpath и без `java.lang.reflect`. Отсутствие рефлексии — это и есть причина быстрого старта (~30ms native, 1-2с JVM) и низкого memory footprint (~30-50 МБ native).
>
> **Пример:**
> ```java
> @Singleton
> public class OrderService {
>     private final OrderRepository repo;
>     public OrderService(OrderRepository repo) { this.repo = repo; }
> }
> // После javac в build/classes/.../OrderService$Definition.class:
> // public final class $OrderService$Definition extends AbstractInitializableBeanDefinition<OrderService> {
> //     public OrderService instantiate(BeanResolutionContext ctx, BeanContext beanContext) {
> //         return new OrderService((OrderRepository) super.getBeanForConstructorArgument(ctx, beanContext, 0, null));
> //     }
> // }
> ```
>
> **Когда применять:** AWS Lambda / GCP Functions, k8s scale-to-zero, memory-constrained окружения, любые сценарии где cold start критичен.
>
> **Подводные камни:** все DTO, попадающие в Jackson или validation, должны быть помечены `@Introspected` — иначе в native image будет `SerializationException`. Сторонние библиотеки, использующие рефлексию (старые версии Hibernate, некоторые JDBC-драйверы) требуют отдельной reflect-config для GraalVM.
>
> **Связанные вопросы:** [[Q4]], [[Q16]]
>
> ---
>
> #### B) Spring Boot тоже использует compile-time DI начиная с версии 3, разницы нет — ❌ Неверно
>
> **Что на самом деле:** Spring Boot 3 добавил `spring-aot` для native image (генерация hints в compile time), но **сам DI-граф по-прежнему строится в runtime через рефлексию** в `AnnotationConfigApplicationContext`. AOT-обработка нужна только для GraalVM, JVM-режим остался reflection-based. **Откуда путаница:** маркетинг Spring Boot 3 «cloud-native, native ready» создаёт впечатление архитектурного равенства с Micronaut, хотя речь идёт о компиляции hints, а не о смене модели DI. **Если бы это было правдой:** cold start Spring Boot на JVM был бы ~1-2с как у Micronaut, но в реальности это 3-15с.
>
> ---
>
> #### C) Micronaut — тонкий wrapper над Spring Framework, использует SpringContext под капотом — ❌ Неверно
>
> **Что на самом деле:** Micronaut — независимый фреймворк с собственным `io.micronaut.context.BeanContext`, никакой зависимости от `org.springframework.context.ApplicationContext`. Только аннотации (`@Controller`, `@Inject`) синтаксически похожи на Spring для облегчения миграции. **Откуда путаница:** создатели Micronaut — бывшие разработчики Grails/Spring, отсюда узнаваемые имена аннотаций. **Если бы это было правдой:** Micronaut тащил бы за собой 30+ МБ spring-core jars и наследовал бы reflection-based DI, а это противоречило бы заявленным cold start ~30ms native.
>
> ---
>
> #### D) Micronaut поддерживает только Kotlin, для Java нужен другой фреймворк — ❌ Неверно
>
> **Что на самом деле:** Micronaut официально поддерживает Java, Kotlin (через kapt/KSP) и Groovy. Большинство примеров в документации написаны именно на Java. **Откуда путаница:** в JetBrains-экосистеме Kotlin часто ассоциируется с современными JVM-фреймворками (Ktor, Spring + Kotlin DSL), отсюда ложное обобщение. **Если бы это было правдой:** Micronaut Launch (start.micronaut.io) не предлагал бы Java как опцию по умолчанию, а production-кейсы вроде Oracle Cloud Infrastructure не использовали бы его.

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
>
> **Вопрос:** Какова ключевая архитектурная разница между Micronaut и Spring Boot и как она проявляется на cold start и memory footprint в JVM-режиме?
>
> ---
>
> #### A) Spring Boot 3+ с native image идентичен Micronaut по производительности — разницы нет — ❌ Неверно
>
> **Что на самом деле:** в native image Spring Boot 3 действительно близок к Micronaut (~50-100ms старт), но в **JVM-режиме**, который доминирует в production, разница сохраняется: Spring Boot 3-15с, Micronaut 1-2с. Это потому что Spring AOT генерирует hints для GraalVM, но базовая DI-машина остаётся reflection-based. **Откуда путаница:** бенчмарки native vs native действительно показывают паритет; ошибка — экстраполировать это на JVM. **Если бы это было правдой:** не было бы смысла использовать Micronaut в JVM-режиме, но Oracle, Netflix используют его именно так.
>
> ---
>
> #### B) Micronaut — compile-time DI/AOP, cold start 1-2с JVM vs Spring Boot 3-15с, memory 80-120 МБ vs 150-300 МБ — ✓ Верно
>
> **Развёрнутое объяснение:** Micronaut генерирует DI-код во время компиляции через annotation processor, а Spring Boot строит `ApplicationContext` в runtime через сканирование classpath + рефлексию. Эта разница даёт два эффекта: (1) cold start — нет затрат на classpath scan, (2) memory — нет хранения metadata в `BeanFactory`, нет CGLIB-proxy объектов в heap. AOP в Micronaut реализован через `$Intercepted`-subclass, сгенерированный компилятором, тогда как Spring создаёт CGLIB-proxy в runtime.
>
> **Пример:**
> ```yaml
> # Real benchmark на одинаковом сервисе (5 controllers + JDBC + Kafka):
> # Spring Boot 3.2 JVM:    startup 4.8s, RSS 240 MB, first request p95 850ms
> # Micronaut 4.2 JVM:      startup 1.4s, RSS 95  MB, first request p95 180ms
> # Spring Boot 3.2 native: startup 95ms, RSS 110 MB, first request p95 35ms
> # Micronaut 4.2 native:   startup 35ms, RSS 65  MB, first request p95 28ms
> ```
> ```java
> // Micronaut: @Transactional → сгенерированный TransactionalInterceptor вызывается через прямой method call
> // Spring:    @Transactional → CGLIB proxy перехватывает вызов через MethodInterceptor + рефлексия
> ```
>
> **Когда применять:** k8s scale-to-zero (KEDA), AWS Lambda, batch-jobs которые часто рестартятся, memory-budget < 256MB на pod.
>
> **Подводные камни:** Spring-эcosystem огромна (Spring Security, Spring Data REST, Spring Cloud Gateway) — у Micronaut аналоги есть, но менее зрелые. Миграция большого Spring-проекта на Micronaut обычно занимает 3-6 месяцев из-за `@Conditional`, `@Profile`, Spring Cloud Config.
>
> **Связанные вопросы:** [[Q1]], [[Q25]]
>
> ---
>
> #### C) Оба используют runtime reflection для DI; Micronaut лишь добавляет синтаксический сахар — ❌ Неверно
>
> **Что на самом деле:** Micronaut вообще не использует `java.lang.reflect` для разрешения зависимостей. Все `BeanDefinition`-классы сгенерированы компилятором и регистрируются через `ServiceLoader` (`META-INF/services/io.micronaut.inject.BeanDefinitionReference`). **Откуда путаница:** похожий синтаксис аннотаций (`@Inject`, `@Singleton`) наводит на мысль о похожей реализации. **Если бы это было правдой:** GraalVM native image не работал бы из коробки без `reflect-config.json`, но Micronaut запускается в native без дополнительной конфигурации.
>
> ---
>
> #### D) Micronaut поддерживает только блокирующий HTTP, без Reactor/RxJava — ❌ Неверно
>
> **Что на самом деле:** Micronaut HTTP-сервер построен на Netty и нативно поддерживает `Publisher<T>` из Reactive Streams. Можно возвращать `Mono`, `Flux`, `Single`, `Maybe`, `CompletableFuture` — всё конвертируется через `ReactiveTypeConverter`. **Откуда путаница:** Spring WebFlux часто ассоциируется как «единственный reactive JVM-стек», поэтому новичкам кажется, что Micronaut — конкурент только Spring MVC. **Если бы это было правдой:** не было бы `micronaut-rxjava3`, `micronaut-reactor` модулей и встроенного R2DBC в Micronaut Data.

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
>
> **Вопрос:** Чем Micronaut принципиально отличается от Quarkus и как выбрать между ними для нового проекта?
>
> ---
>
> #### A) Quarkus работает только в GraalVM native, на JVM не запускается — ❌ Неверно
>
> **Что на самом деле:** Quarkus отлично работает в JVM-режиме, причём `quarkus:dev` mode даёт live reload — изменения в коде применяются без перезапуска. Native — опциональный профиль (`quarkus build -Dnative`). **Откуда путаница:** Red Hat активно продвигает связку Quarkus + GraalVM как USP, отсюда впечатление эксклюзивности. **Если бы это было правдой:** Quarkus был бы непригоден для команд без GraalVM-экспертизы, но он популярен именно как универсальный JVM-фреймворк.
>
> ---
>
> #### B) Quarkus значительно быстрее Micronaut в native, на порядок отличие cold start — ❌ Неверно
>
> **Что на самом деле:** реальные бенчмарки показывают паритет: Quarkus native ~10-30ms, Micronaut native ~30ms на типичных REST-сервисах. Разница в пределах погрешности и зависит от конкретного приложения (количество beans, размер графа классов). **Откуда путаница:** маркетинговые материалы Red Hat про «supersonic, subatomic Java» создают впечатление превосходства. **Если бы это было правдой:** все cloud-native проекты переехали бы на Quarkus, но Oracle Cloud, Target, Wells Fargo используют Micronaut в production.
>
> ---
>
> #### C) Quarkus от Red Hat придерживается MicroProfile/Jakarta EE, имеет более развитый Dev Services; Micronaut ближе к Spring-стилю и проще для команд из Spring-мира — ✓ Верно
>
> **Развёрнутое объяснение:** **Quarkus** построен поверх стандартов Jakarta EE (CDI вместо `@Inject` Micronaut-стиля, JAX-RS вместо `@Controller`, MicroProfile Config, Health, Metrics). Его **Dev Services** автоматически поднимают Testcontainers для PostgreSQL/Kafka/Redis при запуске `quarkus:dev` — это сильное преимущество для DX. **Micronaut** использует свои аннотации, синтаксически близкие к Spring (`@Controller`, `@Get`, `@Inject` из jakarta.inject), и Test Resources (аналог Dev Services, добавлен в 4.0). Выбор сводится к команде: Spring-команды быстрее освоят Micronaut, Java EE/JBoss-команды — Quarkus.
>
> **Пример:**
> ```java
> // Quarkus (Jakarta EE стиль)
> @Path("/users")
> @ApplicationScoped
> public class UserResource {
>     @Inject UserService service;          // jakarta.inject
>     @GET @Path("/{id}") @Produces(MediaType.APPLICATION_JSON)
>     public User get(@PathParam("id") Long id) { return service.find(id); }
> }
>
> // Micronaut (Spring-подобный стиль)
> @Controller("/users")
> public class UserController {
>     private final UserService service;
>     public UserController(UserService service) { this.service = service; }
>     @Get("/{id}") public User get(Long id) { return service.find(id); }
> }
> ```
>
> **Когда применять:** **Micronaut** — мигрируем с Spring Boot, нужна максимальная совместимость с привычным DI; команда уже знает Spring. **Quarkus** — greenfield проект на Jakarta EE, команда из JBoss/WildFly мира, нужен Dev Services из коробки, важна интеграция с OpenShift.
>
> **Подводные камни:** оба фреймворка хороши, выбор часто определяется не техникой, а экосистемой компании (Red Hat support vs Oracle support). Не пытайтесь смешивать Quarkus extensions с Micronaut beans — это разные `BeanContainer` API.
>
> **Связанные вопросы:** [[Q1]], [[Q23]]
>
> ---
>
> #### D) Micronaut — только для Google Cloud, Quarkus — только для Red Hat OpenShift — ❌ Неверно
>
> **Что на самом деле:** оба фреймворка cloud-agnostic. У Micronaut есть first-class модули для AWS, GCP, Azure, Oracle Cloud (Object Storage, Secret Manager, Functions). Quarkus аналогично работает на любых K8s-кластерах, не только OpenShift. **Откуда путаница:** Quarkus = Red Hat = OpenShift по ассоциации; Micronaut продвигался Oracle Cloud Infrastructure. **Если бы это было правдой:** не существовало бы `micronaut-aws`, `micronaut-azure`, `quarkus-amazon-lambda` модулей.

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
>
> **Вопрос:** Как Micronaut определяет, делать ли HTTP-вызов блокирующим или реактивным при использовании `@Client`?
>
> ---
>
> #### A) По возвращаемому типу метода интерфейса (`List<T>` → blocking, `Flux<T>`/`Mono<T>` → reactive, `CompletableFuture<T>` → async) — ✓ Верно
>
> **Развёрнутое объяснение:**
>
> Micronaut HTTP client использует тип возвращаемого значения метода как контракт. Annotation processor видит сигнатуру и генерирует соответствующий `$Intercepted` класс, который правильно адаптирует ответ Netty-канала. Под капотом сетевой слой ВСЕГДА реактивный (Netty + Reactor), но публичное API подстраивается:
> - `T` или `List<T>` → блокирующий вызов (`.block()` под капотом)
> - `Mono<T>` / `Flux<T>` → Reactor publisher без блокировки
> - `Single<T>` / `Observable<T>` → RxJava (если `micronaut-rxjava3` подключён)
> - `CompletableFuture<T>` → async через Java стандарт
>
> **Пример:**
> ```java
> @Client("https://api.example.com")
> public interface UserClient {
>     // blocking — поток ждёт ответ
>     @Get("/users/{id}")
>     User findById(Long id);
>
>     // reactive — non-blocking, возвращает publisher
>     @Get("/users/{id}")
>     Mono<User> findByIdReactive(Long id);
>
>     // streaming — каждый элемент по мере прихода (SSE/NDJSON)
>     @Get(value = "/users", processes = MediaType.APPLICATION_JSON_STREAM)
>     Flux<User> streamUsers();
>
>     // async через стандартный Java API
>     @Get("/users/{id}")
>     CompletableFuture<User> findByIdAsync(Long id);
> }
> ```
>
> **Когда применять:**
> - В реактивных контроллерах (возвращающих `Mono`/`Flux`) — использовать reactive-методы клиента, чтобы не блокировать event loop Netty.
> - В блокирующих контроллерах с `@ExecuteOn(TaskExecutors.IO)` — допустим blocking-метод, он будет выполнен в IO-пуле.
> - Для streaming-эндпоинтов (NDJSON, SSE) — только `Flux<T>` с `APPLICATION_JSON_STREAM`.
>
> **Подводные камни:**
> - Блокирующий метод `@Client` в реактивном контроллере на event-loop'е заблокирует Netty worker → throughput падает до 1 запроса/поток. Помечайте контроллер `@ExecuteOn(TaskExecutors.IO)` или используйте `Mono`.
> - `Flux<User> list()` БЕЗ `processes = JSON_STREAM` соберёт весь список перед эмиссией — не стриминг, а отложенный сбор.
> - `CompletableFuture` пробрасывает исключения через `CompletionException` — оборачивайте при unwrap.
>
> **Связанные вопросы:** [[Q10]] — declarative @Client и compile-time generation; [[Q22]] — Reactor/RxJava в Micronaut; [[Q8]] — REST-контроллеры и их типы возвратов.
>
> ---
>
> #### B) Через аннотацию `@Async` на методе клиента — без неё всегда блокирующий — ❌ Неверно
>
> **Что на самом деле:** в Micronaut нет аннотации `@Async` для declarative HTTP-клиентов. Стиль выполнения определяется исключительно типом возврата метода. Аннотация `@Async` в Micronaut существует только для бинов сервисов (`@ExecuteOn`/`@Async` в Micronaut 4 у методов сервисов) — это совсем другой механизм.
>
> **Откуда путаница:** в Spring Boot есть `@Async` для асинхронного выполнения методов через `TaskExecutor` (без HTTP-специфики). Разработчик переносит ментальную модель «нужна аннотация для async» на Micronaut.
>
> **Если бы это было правдой:** разработчик с реактивным контроллером добавил бы `Flux<User> list()` без `@Async` и ожидал блокирующего вызова — в реальности Micronaut вернёт честный `Flux` без блокировки, и тесты с моками типа `when(client.list()).thenReturn(...)` упали бы с `ClassCastException`.
>
> ---
>
> #### C) По строковому параметру `mode` в `@Client(mode = "reactive")` — ❌ Неверно
>
> **Что на самом деле:** аннотация `@Client` имеет параметры `value` (URL/service-id), `id`, `path`, `configuration`, `errorType`, `httpVersion`, но не `mode`. Реактивность не настраивается на уровне клиента целиком — она per-method через тип возврата.
>
> **Откуда путаница:** у некоторых HTTP-клиентов (например, AsyncHttpClient в Java) действительно есть глобальный mode. Также `WebClient` в Spring WebFlux всегда reactive, а `RestTemplate` всегда blocking — две разные сущности. Это создаёт впечатление, что выбор делается на уровне клиента, а не метода.
>
> **Если бы это было правдой:** нельзя было бы в одном клиенте смешивать blocking-методы (для админ-эндпоинтов с `@ExecuteOn(IO)`) и reactive (для high-throughput путей) — пришлось бы заводить два разных интерфейса под один сервис.
>
> ---
>
> #### D) Все методы `@Client` блокирующие; reactive нужно вручную через `Mono.fromCallable(client::call)` — ❌ Неверно
>
> **Что на самом деле:** declarative `@Client` поддерживает реактивные типы изначально и НЕ блокирует поток для `Mono`/`Flux` методов. Под капотом используется Netty, а ответ оборачивается в Reactor publisher без `.block()`. `Mono.fromCallable(client::call)` — антипаттерн: он завернул бы блокирующий вызов в обёртку, но реальное I/O всё равно блокирует поток scheduler'а.
>
> **Откуда путаница:** в Spring 4/5 с `RestTemplate` так и приходилось делать — `Mono.fromCallable(() -> restTemplate.getForObject(...))` на `Schedulers.boundedElastic()`. Привычка переносится на Micronaut, где это и не нужно, и вредно.
>
> **Если бы это было правдой:** не существовало бы преимущества Micronaut по throughput на реактивных путях — стек выглядел бы как Spring MVC с `RestTemplate`, что противоречит бенчмаркам ~100K req/s.

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
>
> **Как Micronaut читает и применяет конфигурацию из `application.yml`?**
>
> #### A) Конфиг загружается только через `@Value` — отдельных POJO для группы свойств в Micronaut нет
>
> Неверно. Micronaut поддерживает `@ConfigurationProperties("prefix")` — type-safe POJO, в который собирается ветка конфига (как в Spring).
>
> **Почему путают:** в учебных примерах часто показывают только `@Value("${some.value}")`, и кажется, будто это единственный способ. На самом деле для нескольких связанных свойств идиоматично использовать `@ConfigurationProperties`.
>
> #### B) `application.yml` — единственный поддерживаемый формат; properties/toml в Micronaut не работают
>
> Неверно. Micronaut поддерживает `properties`, `yml`, `groovy`, `toml`, а также environment variables и системные свойства. Все они мёржатся в единый `Environment`.
>
> **Откуда путаница:** YAML — самый частый формат в туториалах, и это создаёт впечатление эксклюзивности. На деле формат — вопрос предпочтения команды.
>
> #### C) Placeholder'ы `${VAR:default}` интерпретируются только в runtime, поэтому подставить env var в `application.yml` нельзя
>
> Неверно. `${DB_USER:admin}` — стандартный синтаксис Micronaut для подстановки env var с дефолтом. Это работает именно потому, что `Environment` объединяет файлы конфига и переменные окружения в одном property resolver'е.
>
> **Если бы это было правдой:** не получилось бы переопределять `datasources.default.username` через `DB_USER` без кода — а это базовый паттерн в контейнерных деплоях.
>
> #### C+) ✅ Micronaut собирает иерархию property source'ов (файл + env vars + system properties + cloud config), а свойства инжектятся через `@Value` или type-safe `@ConfigurationProperties("prefix")` POJO
>
> Это и есть правильная модель. `Environment` мёржит все источники по приоритету, поддерживает placeholder'ы `${VAR:default}`, а для группы связанных свойств идиоматично создавать `@ConfigurationProperties` — компайл-тайм-проверка имён, без runtime-рефлексии.
>
> **Почему именно так:** Micronaut делает property binding на этапе компиляции через annotation processor, поэтому конфиг проверяется типизированно и работает в GraalVM native image без reflection metadata.
>
> **Когда применять `@ConfigurationProperties` vs `@Value`:** для одиночного значения — `@Value`, для группы связанных свойств с одним префиксом — `@ConfigurationProperties` (читабельнее, тестируется, валидируется через `@Validated`).

## Q13. Environment-specific конфиги?

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
>
> **Как Micronaut выбирает environment-specific конфиги и переопределяет общие значения?**
>
> #### A) ✅ Базовый `application.yml` грузится всегда, а `application-{env}.yml` подключается по активным `micronaut.environments` (через `-D`, env var, profile); значения мёржатся, env-specific перекрывает общее
>
> Это и есть правильная модель. Активные environment'ы задаются через `-Dmicronaut.environments=prod` или `MICRONAUT_ENVIRONMENTS=prod`, можно перечислить несколько через запятую (`prod,k8s,aws`) — каждый следующий имеет более высокий приоритет. `application-test.yml` подключается автоматически в `@MicronautTest`.
>
> **Почему именно так:** это классический паттерн «base + override», знакомый по Spring profiles. Micronaut определяет порядок мёрджа предсказуемо: общий файл → env-specific → env vars → system properties → CLI args.
>
> **Когда применять:** разные эндпоинты БД/Kafka для dev/prod, выключение debug-логирования в prod, отдельная конфигурация для интеграционных тестов через `application-test.yml`.
>
> #### B) Имя файла должно совпадать с системным свойством `spring.profiles.active` — Micronaut читает Spring-конвенцию
>
> Неверно. У Micronaut собственное свойство — `micronaut.environments` (или env var `MICRONAUT_ENVIRONMENTS`). Spring-конвенция `spring.profiles.active` не используется.
>
> **Откуда путаница:** механизм идейно аналогичен Spring profiles, и разработчики, мигрирующие с Spring Boot, по инерции пытаются включать профили старым способом. В Micronaut переменная — другая.
>
> #### C) Можно активировать только один environment одновременно; список нескольких профилей не поддерживается
>
> Неверно. Micronaut поддерживает несколько environment'ов одновременно: `-Dmicronaut.environments=prod,k8s,aws`. Они применяются последовательно — каждый следующий перекрывает предыдущий.
>
> **Если бы это было правдой:** нельзя было бы накладывать конфиг slice'ы (например, `prod` + `k8s` + `aws-region-eu`), а это стандартный паттерн для облачных деплоев.
>
> #### D) Micronaut автоматически детектит окружение по hostname и применяет соответствующий `application-{env}.yml` без участия разработчика
>
> Неверно. Auto-detection работает в ограниченном виде (например, распознаются `kubernetes`, `cloud`, `test` через специальные триггеры), но универсального детекта по hostname нет — environment задаётся явно через property/env var или через специфические триггеры (`@MicronautTest` для `test`).
>
> **Откуда путаница:** Micronaut действительно имеет несколько встроенных environment-detector'ов (например, `K8S_ENV`), и это создаёт иллюзию полностью автоматической работы. На практике для прода всё равно нужно явно ставить `MICRONAUT_ENVIRONMENTS=prod`.

## Q14. (!) Что такое Micronaut Data?

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
>
> **Чем Micronaut Data отличается от Spring Data JPA в плане работы с запросами?**
>
> #### A) Micronaut Data — это тонкая обёртка над Hibernate, она использует тот же runtime-парсер finder-методов, что и Spring Data
>
> Неверно. Micronaut Data **не использует runtime-парсинг** имён методов. Имена finder-методов разбираются на этапе компиляции annotation processor'ом, и в class-файл генерируется готовая реализация с SQL/JPQL.
>
> **Откуда путаница:** API внешне идентичный Spring Data (`findByName`, `CrudRepository`), что создаёт иллюзию одинакового механизма. На деле фундаментально разный pipeline: compile-time generation vs runtime proxy.
>
> #### B) ✅ Micronaut Data генерирует реализации репозиториев **в compile time**: finder-методы транслируются в SQL/JPQL без рефлексии, что даёт быстрый старт и работу в GraalVM native image
>
> Это и есть ключевое отличие. Annotation processor разбирает имена методов (`findByEmail`, `findByAuthorOrderByYearDesc`) и `@Query`-аннотации на этапе компиляции, после чего записывает готовую реализацию в byte code. В runtime нет ни рефлексии, ни прокси.
>
> **Почему именно так:** Micronaut в целом построен на compile-time DI и AOP, и Data следует той же идеологии. Это даёт три выигрыша: (1) меньше памяти (нет proxy-каскадов), (2) быстрее старт (нет сканирования classpath), (3) совместимость с GraalVM native image «из коробки».
>
> **Когда применять:** микросервисы с быстрым стартом, native-image деплои, low-memory среды; всё, где Spring Data JPA даёт ощутимый overhead на старт/heap.
>
> **Дополнительно:** поддерживаются backends JDBC (без Hibernate), JPA (через Hibernate), R2DBC (reactive), MongoDB — все через единый API `@Repository`.
>
> #### C) Micronaut Data не поддерживает `@Query` с произвольным JPQL/SQL — только derived queries из имени метода
>
> Неверно. `@Query("UPDATE User u SET u.active = false WHERE u.lastLogin < :date")` — стандартная фича Micronaut Data, аналогичная Spring Data. Поддерживаются и JPQL, и native SQL (`nativeQuery = true`).
>
> **Если бы это было правдой:** Micronaut Data не покрыл бы реальные сценарии (bulk update/delete, сложные JOIN'ы), и его нельзя было бы использовать как замену Spring Data JPA.
>
> #### D) Под капотом Micronaut Data всегда использует R2DBC, поэтому работа с обычным JDBC невозможна
>
> Неверно. Backends независимы: можно выбрать `@JdbcRepository` (sync JDBC без Hibernate), `@JpaRepository` (Hibernate), `@R2dbcRepository` (reactive), `@MongoRepository`. R2DBC — лишь один из вариантов, и далеко не дефолт.
>
> **Откуда путаница:** Micronaut активно продвигается как «реактивный фреймворк», и это создаёт впечатление, что и Data реактивная по умолчанию. На практике большинство приложений использует JDBC или JPA.

## Q15. JDBC, JPA, R2DBC репозитории?

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
>
> **Чем `@JdbcRepository` в Micronaut Data отличается от `@JpaRepository` и `@R2dbcRepository`, и когда какой выбирать?**
>
> #### A) `@JdbcRepository` — это просто JPA без `@Entity`-аннотации, под капотом всё равно работает Hibernate
>
> Неверно. `@JdbcRepository` **не использует Hibernate** вообще. Это самостоятельный backend, который генерирует SQL напрямую из имён методов и `@Query`, выполняет его через обычный JDBC и мапит результат на POJO через compile-time introspection.
>
> **Откуда путаница:** одинаковый API `CrudRepository` создаёт впечатление общего runtime. Но за `@JdbcRepository` нет ни `EntityManager`, ни persistence context, ни lazy loading — это «чистый» SQL access слой.
>
> #### B) Реактивный доступ к БД в Micronaut Data обеспечивает `@JpaRepository` через корутинный wrapper над Hibernate
>
> Неверно. Реактивный backend — это **отдельная аннотация `@R2dbcRepository`**, основанная на R2DBC-драйверах (`r2dbc-postgresql`, `r2dbc-mysql`), а не на Hibernate. JPA фундаментально блокирующая, и обернуть её в реактивный API без потери смысла нельзя.
>
> **Если бы это было правдой:** Hibernate-вызовы в reactive pipeline блокировали бы event loop, и весь смысл реактивного подхода (горизонтальное масштабирование на малом числе потоков) терялся бы.
>
> #### C) ✅ `@JdbcRepository` — синхронный SQL без Hibernate (лёгкий, быстрый старт), `@JpaRepository` — через Hibernate с lazy/dirty checking, `@R2dbcRepository` — non-blocking через R2DBC; выбор зависит от модели данных и стиля приложения
>
> Это и есть правильное разграничение. Три независимых backends с одним API:
>
> ```java
> // JDBC: lightweight, no Hibernate, native SQL/dialect
> @JdbcRepository(dialect = Dialect.POSTGRES)
> public interface BookRepository extends CrudRepository<Book, Long> {
>     List<Book> findByAuthor(String author);
> }
>
> // JPA: Hibernate, entity graph, lazy loading
> @Repository
> public interface UserRepository extends JpaRepository<User, Long> {
>     @EntityGraph(attributePaths = "roles")
>     Optional<User> findByEmail(String email);
> }
>
> // R2DBC: reactive, non-blocking
> @R2dbcRepository(dialect = Dialect.POSTGRES)
> public interface OrderRepository extends ReactorCrudRepository<Order, Long> {
>     Flux<Order> findByStatus(String status);
> }
> ```
>
> **Почему именно так:** Micronaut Data — это compile-time engine, который генерирует реализацию репозитория под каждый backend отдельно. Поэтому можно выбрать минимально необходимый stack без перетягивания Hibernate в микросервис, где он не нужен.
>
> **Когда применять:**
> - `@JdbcRepository` — микросервисы с простыми моделями, нужна максимальная скорость старта и низкое потребление памяти (native image-friendly).
> - `@JpaRepository` — сложные доменные модели с ассоциациями, нужны JPA-фичи (cascade, lazy, optimistic locking).
> - `@R2dbcRepository` — реактивные API с высоким concurrency и нагрузкой на I/O.
>
> **Дополнительно:** в одном приложении можно смешивать backends (часть репозиториев на JDBC, часть на JPA), они изолированы друг от друга через разные `@Repository`-стереотипы.
>
> #### D) Все три типа репозиториев совместимы между собой — можно объявить интерфейс с `@JdbcRepository`, а в runtime переключить его на R2DBC через property
>
> Неверно. Тип репозитория фиксируется на этапе компиляции — annotation processor генерирует под каждый бэкенд **разный код** (SQL execution через JDBC vs R2DBC connection vs JPA EntityManager). Поменять backend без правки исходников и пересборки нельзя.
>
> **Если бы это было правдой:** не было бы смысла в разных аннотациях — хватило бы одной `@DataRepository` с динамическим выбором. Но переход с blocking JDBC на non-blocking R2DBC требует изменения сигнатур методов (`List<T>` → `Flux<T>`), что чисто по типам несовместимо.

## Q16. (!) Поддержка GraalVM Native Image?

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
>
> **Почему Micronaut исторически считается «более native-friendly», чем Spring Boot до Spring Native/AOT?**
>
> #### A) Micronaut запускает JIT-компилятор GraalVM на старте приложения и поэтому стартует быстрее, чем Spring Boot
>
> Неверно. В **native image нет JIT** вообще — весь код AOT-скомпилирован в нативный бинарь. Это и есть причина мгновенного старта: не нужно прогревать JIT, не нужно загружать классы. Micronaut использует тот же `native-image` тулчейн GraalVM, что и Spring Native.
>
> **Откуда путаница:** GraalVM известен как JIT-компилятор (Graal Compiler), и легко спутать его с native-image (отдельная AOT-сборка). Это два разных продукта одного проекта.
>
> #### B) Micronaut требует меньше памяти, потому что не использует Java вообще — кодогенерация выдаёт чистый C
>
> Неверно. Micronaut генерирует **обычный Java bytecode** на этапе компиляции — никакого C нет. Меньшее потребление памяти достигается за счёт отсутствия рантайм-рефлексии и proxy-каскадов, а не за счёт другого языка.
>
> **Если бы это было правдой:** Micronaut нельзя было бы запускать на обычной JVM (без native image), но это штатный режим работы фреймворка.
>
> #### C) Spring Boot никогда не поддерживал native image; единственный способ собрать Spring-приложение в нативный бинарь — переписать его на Micronaut
>
> Неверно. Spring Native (отдельный проект, объединённый в Spring Boot 3.x как Spring AOT) даёт нативную сборку Spring-приложений. Просто Micronaut **исторически появился раньше** с встроенной поддержкой, а Spring дорабатывал её эволюционно через AOT-процессор.
>
> **Откуда путаница:** в 2018–2021 годах Spring действительно не имел production-ready native поддержки, и Micronaut позиционировался как замена. Сейчас оба фреймворка поддерживают native, но с разной зрелостью.
>
> #### D) ✅ Micronaut с самого старта построен на **compile-time DI/AOP без рефлексии**: annotation processor генерирует bean-метаданные и proxy в bytecode на этапе сборки, а в runtime нет ни classpath-сканирования, ни reflection — поэтому `native-image` собирается практически без дополнительной конфигурации
>
> Это и есть архитектурное отличие. Spring исторически опирался на runtime reflection (`@Autowired` через `BeanPostProcessor`, dynamic proxies для `@Transactional`), что плохо ложится на closed-world модель GraalVM. Micronaut с первого релиза проектировался иначе:
>
> ```java
> @Singleton
> public class OrderService {
>     private final OrderRepository repo;
>
>     public OrderService(OrderRepository repo) { this.repo = repo; }
>
>     @Transactional
>     public Order create(OrderRequest req) { ... }
> }
> ```
>
> На этапе компиляции annotation processor видит `@Singleton` и `@Transactional`, генерирует `OrderService$Definition` (метаданные для DI) и `OrderService$Intercepted` (compile-time proxy для transaction-advice). В runtime DI-контейнер просто читает готовые definitions без сканирования classpath.
>
> Для DTO, которые сериализуются в JSON, нужна реflection-metadata — её добавляет `@Introspected`:
>
> ```java
> @Introspected
> public class User {
>     private final Long id;
>     private final String name;
>     // конструктор + геттеры
> }
> ```
>
> Это даёт Jackson возможность работать без reflection в native image.
>
> **Почему именно так:** GraalVM `native-image` использует closed-world assumption — он должен знать ВСЕ классы, методы и reflection-вызовы на этапе сборки. Compile-time подход Micronaut естественно совместим с этим ограничением: что было сгенерировано на этапе сборки, то и существует в бинаре. Spring Native решает ту же задачу через AOT-процессор, но это надстройка над исторически reflection-heavy архитектурой.
>
> **Когда применять:** serverless (AWS Lambda, Cloud Run) с требованием <100ms старта, контейнеры с лимитом памяти 64–128 MB, edge deployments. Подвохи остаются: сборка native занимает 5–15 минут, peak throughput ниже на ~30% (нет JIT), стек-трейсы менее читаемы.
>
> **Дополнительно:** для third-party библиотек, которые используют reflection, всё равно нужны конфиги в `META-INF/native-image/` или GraalVM hints через `@ReflectiveAccess` / `@TypeHint`. «Из коробки» — только для собственного кода Micronaut и его экосистемы.

## Q17. Какие особенности и подводные камни?

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
>
> **При сборке Micronaut-приложения в GraalVM native image падает JSON-сериализация DTO в HTTP-ответе («No serializer found», `IllegalArgumentException` на reflection). Что нужно сделать в первую очередь?**
>
> #### A) ✅ Пометить DTO аннотацией `@Introspected` (или Jackson-классы `@SerdeImport`) — annotation processor сгенерирует compile-time метаданные, и Jackson/Serde смогут читать поля без рантайм-рефлексии
>
> Это и есть корректное решение для native-сборки. По умолчанию Jackson обходит поля через reflection (`Class.getDeclaredFields()`), но в native image весь reflection должен быть зарегистрирован на этапе сборки. `@Introspected` запускает Micronaut annotation processor, который генерирует `BeanIntrospection` для класса:
>
> ```java
> @Introspected
> public class UserDto {
>     private final Long id;
>     private final String name;
>     private final String email;
>
>     public UserDto(Long id, String name, String email) {
>         this.id = id;
>         this.name = name;
>         this.email = email;
>     }
>     // геттеры
> }
>
> @Controller("/users")
> public class UserController {
>     @Get("/{id}")
>     public UserDto get(@PathVariable Long id) {
>         return new UserDto(id, "Alice", "alice@example.com");
>     }
> }
> ```
>
> В compile time будет создан `UserDto$Introspection`, содержащий доступ к полям без reflection. Если приложение использует Micronaut Serialization (а не классический Jackson), `@Serdeable` делает то же самое и работает быстрее.
>
> **Почему именно так:** GraalVM `native-image` имеет closed-world модель — все reflection-вызовы должны быть известны на этапе сборки. `@Introspected` решает это идиоматично для Micronaut: метаданные кладутся в bytecode, и в runtime ничего отражать не нужно.
>
> **Когда применять:** все DTO, которые сериализуются/десериализуются через HTTP (request body, response body, query params binding), а также сущности, для которых нужна compile-time бин-интроспекция (валидация, `@Value` injection полей).
>
> **Дополнительно:** для DTO в подключённых третьесторонних библиотеках, которые нельзя пометить аннотацией, используется `@Introspected(classes = ExternalClass.class)` на любом классе вашего проекта — это сгенерирует метаданные для внешних типов.
>
> #### B) Добавить `-H:+ReportExceptionStackTraces` в опции `native-image` — это автоматически решит проблему с сериализацией
>
> Неверно. Этот флаг лишь улучшает диагностику (печатает стек-трейсы внутренних исключений сборки) и **не влияет** на reflection-метаданные. Сериализация продолжит падать.
>
> **Откуда путаница:** опция действительно полезна при дебаге native build, но это инструмент диагностики, а не фикс. Решение проблемы — `@Introspected` или конфиги в `META-INF/native-image/reflect-config.json`.
>
> #### C) Переключиться на JVM-режим (без native image), потому что native-сборка принципиально несовместима с JSON-сериализацией
>
> Неверно. JSON-сериализация прекрасно работает в native image при правильной конфигурации (`@Introspected` + Micronaut Serialization или `reflect-config.json` для Jackson). Сотни production-приложений на Micronaut + native подтверждают это.
>
> **Если бы это было правдой:** Micronaut не позиционировался бы как native-first фреймворк. На практике сериализация — одна из самых отработанных областей в native режиме.
>
> #### D) Использовать `@Reflective` вместо `@Introspected`, потому что Jackson требует именно runtime-рефлексии
>
> Неверно. Во-первых, в Micronaut нет аннотации `@Reflective` — есть `@ReflectiveAccess` для регистрации reflection-доступа к конкретному элементу. Во-вторых, в native image нужно **избегать** runtime-рефлексии, а не разрешать её — путь через compile-time интроспекцию (`@Introspected`) более эффективен и идиоматичен.
>
> **Откуда путаница:** в Spring Native действительно используются hints типа `@RegisterReflectionForBinding`, и разработчики переносят этот ментальный паттерн на Micronaut. Но философия Micronaut — генерировать метаданные на этапе компиляции, а не регистрировать рантайм-доступ.

## Q18. (!) @MicronautTest и его возможности?

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


> [!mcq] Что делает аннотация `@MicronautTest` в тестах Micronaut?
>
> - [x] **A.** Поднимает полноценный application context и поддерживает `@Inject`, `@Client`, `@MockBean`, по умолчанию оборачивает каждый тест в транзакцию с откатом
>
>     ```java
>     @MicronautTest
>     class UserControllerTest {
>         @Inject @Client("/") HttpClient client;
>
>         @MockBean(UserRepository.class)
>         UserRepository mockRepo() { return mock(UserRepository.class); }
>
>         @Test
>         void testList() {
>             var resp = client.toBlocking()
>                 .exchange(HttpRequest.GET("/users"),
>                           Argument.listOf(User.class));
>             assertEquals(HttpStatus.OK, resp.getStatus());
>         }
>     }
>     ```
>
>     **Почему правильно:** `@MicronautTest` — это полный аналог `@SpringBootTest`, но с поддержкой DI-инъекций в сам тестовый класс, `@MockBean` для подмены бинов и **transactional rollback по умолчанию** (через `@TransactionMode.SEPARATE_TRANSACTIONS` контроль). С Micronaut 4 также интегрируется с **Test Resources** — автоматически поднимает Postgres/Kafka/Redis контейнеры (аналог Quarkus Dev Services).
>
>     **Где применяется:** интеграционные тесты controller→service→repository с реальной БД из Testcontainers, без ручного управления жизненным циклом контекста.
>
> - [ ] **B.** Работает только с unit-тестами без application context, нужно вручную создавать `ApplicationContext.run()` для каждого теста
>
>     **Почему неправильно:** это описание **отсутствия** `@MicronautTest`. Аннотация как раз и нужна, чтобы избавиться от ручного `ApplicationContext context = ApplicationContext.run()` в каждом `@BeforeEach`. JUnit 5 extension `MicronautJunit5Extension` управляет жизненным циклом контекста автоматически.
>
>     **Последствие ошибки:** разработчик заводит boilerplate в `@BeforeEach`/`@AfterEach`, контекст не шарится между тестами одного класса → 10× медленнее прогон, плюс утечки ресурсов при забытом `context.close()`.
>
> - [ ] **C.** Это аналог `@WebMvcTest` из Spring — поднимает только web-слой без service/repository бинов
>
>     **Почему неправильно:** `@MicronautTest` по умолчанию поднимает **полный** application context. Срезы тестов (slice tests) в Micronaut решаются через `@MicronautTest(application = MyApp.class, environments = "test")` и `@Property` для override-ов, а не отдельной аннотацией. Web-only test делается через `EmbeddedServer` + `@Client` без специальной аннотации-среза.
>
>     **Последствие ошибки:** ожидаешь lightweight тест, а получаешь full context — но при этом mock-и service-слоя не подставлены через `@MockBean`, тесты ходят в реальную БД → flaky.
>
> - [ ] **D.** Требует обязательного запуска embedded server на случайном порту для каждого теста, даже если тестируется только service-слой
>
>     **Почему неправильно:** embedded server поднимается только если в зависимостях есть `micronaut-http-server-netty` **и** в тест инжектится `@Client` или `EmbeddedServer`. Для чистого service-теста с `@Inject UserService` сервер не стартует — Micronaut оптимизирует загрузку.
>
>     **Последствие ошибки:** разработчик добавляет `excludeFromTestClassPath` для netty, ломая работающие `@Client`-тесты в других классах.

## Q19. (!) Service discovery, config management?

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


> [!mcq] Как Micronaut поддерживает service discovery и distributed configuration?
>
> - [ ] **A.** Только через подключение `spring-cloud-starter-consul-discovery` — Micronaut использует те же стартеры, что и Spring Cloud
>
>     **Почему неправильно:** Micronaut **не использует** Spring Cloud стартеры — у него собственный модуль `micronaut-discovery-client` с native-friendly реализацией (без reflection-heavy auto-configuration). Подключение Spring-стартеров привнесёт лишний classpath, конфликты бинов и сломает AOT-компиляцию.
>
>     **Последствие ошибки:** GraalVM native image падает на старте из-за reflection-классов Spring Cloud, или JVM-сборка стартует 5+ секунд вместо 200ms.
>
> - [ ] **B.** Service discovery поддерживается только для Kubernetes через DNS, остальные системы (Consul, Eureka) требуют сторонних плагинов
>
>     **Почему неправильно:** Micronaut "из коробки" поддерживает **Consul, Eureka, Kubernetes, AWS Cloud Map, OCI Service Discovery** через официальные модули `io.micronaut.discovery:*`. Это не сторонние плагины, а часть платформы.
>
>     **Последствие ошибки:** команда ищет несуществующие "плагины", вместо `implementation("io.micronaut.discovery:micronaut-discovery-client")` пишут самописный `RestTemplate`-discovery, теряя health checks и автоматический re-fetch.
>
> - [ ] **C.** Для config management подходит только `application.yml` в classpath — внешние источники не поддерживаются
>
>     **Почему неправильно:** Micronaut поддерживает **distributed config** из: Consul KV, Spring Cloud Config Server, AWS Parameter Store / Secrets Manager, HashiCorp Vault, etcd, Kubernetes ConfigMap/Secret, OCI Vault. Подключается через `bootstrap.yml` (читается до `application.yml`).
>
>     **Последствие ошибки:** секреты лежат в git внутри `application.yml` → security incident; либо приходится самописно тянуть `aws-sdk` и парсить параметры в `EventListener`.
>
> - [x] **D.** Встроенные модули `micronaut-discovery-client` и `micronaut-config-client` дают native-поддержку Consul/Eureka/K8s/AWS Cloud Map для discovery и Consul/Vault/Parameter Store/etcd для конфигурации, а `@Client(id = "...")` автоматически резолвит сервис через configured registry
>
>     ```yaml
>     # bootstrap.yml — читается ДО application.yml
>     micronaut:
>       application:
>         name: order-service
>       config-client:
>         enabled: true
>     consul:
>       client:
>         registration: { enabled: true }
>         config: { enabled: true }
>     ```
>
>     ```java
>     @Client(id = "user-service")   // через discovery, не URL
>     public interface UserClient {
>         @Get("/users/{id}")
>         Mono<User> findById(@PathVariable Long id);
>     }
>     ```
>
>     **Почему правильно:** все модули discovery/config спроектированы под **compile-time DI** и AOT — никакого reflection при чтении конфига, метаданные индексов сервисов генерируются в build-time. Это даёт мгновенный старт в native image и предсказуемое поведение в K8s.
>
>     **Где применяется:** микросервисная архитектура с Consul/Vault, K8s-нативные приложения с автодискавери через Service API, AWS-deployments с Parameter Store.

## Q20. Serverless поддержка (AWS Lambda, GCP Functions)?

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


> [!mcq] Почему Micronaut особенно подходит для AWS Lambda и других serverless-платформ?
>
> - [ ] **A.** Micronaut единственный фреймворк, который умеет запускаться внутри Lambda — Spring Boot и Quarkus не имеют такой поддержки
>
>     **Почему неправильно:** на Lambda работают **все три**: Spring Cloud Function, Quarkus AWS Lambda extension и Micronaut. Преимущество Micronaut — не сам факт поддержки, а **характеристики**: cold start и memory footprint.
>
>     **Последствие ошибки:** ложный аргумент в архитектурном решении легко разбивается на ревью; выбор Micronaut должен обосновываться метриками, а не несуществующей эксклюзивностью.
>
> - [x] **B.** Compile-time DI без reflection даёт холодный старт ~30 ms в native image (vs ~5–10 сек у Spring Boot на JVM), что критично для pay-per-invocation модели; `MicronautRequestHandler` интегрирует DI прямо в Lambda handler
>
>     ```java
>     public class OrderHandler
>             extends MicronautRequestHandler<APIGatewayProxyRequestEvent,
>                                             APIGatewayProxyResponseEvent> {
>         @Inject OrderService service;        // DI работает в Lambda
>
>         @Override
>         public APIGatewayProxyResponseEvent execute(
>                 APIGatewayProxyRequestEvent input) {
>             var id = input.getPathParameters().get("id");
>             return new APIGatewayProxyResponseEvent()
>                 .withStatusCode(200)
>                 .withBody(service.toJson(service.find(id)));
>         }
>     }
>     ```
>
>     ```bash
>     # генерация функции с GraalVM native:
>     mn create-function-app order-fn --features=aws-lambda,graalvm
>     ./gradlew nativeCompile     # → bootstrap-binary для Lambda custom runtime
>     ```
>
>     **Почему правильно:** в serverless **каждый cold start оплачивается** (latency + billed duration). Spring Boot на JVM в Lambda — это 5–10 секунд init phase, что: (а) даёт п99 latency 6+ сек для пользователей, (б) попадает в `INIT_REPORT` биллинга. Micronaut native — 30 ms init, JVM-режим ~500 ms. Аналогично работают GCP Functions, Azure Functions, OCI Functions через свои handler-ы.
>
>     **Где применяется:** event-driven API (API Gateway → Lambda), потоковая обработка (SQS/Kinesis triggers), scheduled jobs (EventBridge → Lambda).
>
> - [ ] **C.** Micronaut требует обязательного использования GraalVM native — на стандартной JVM-runtime в Lambda работать не будет
>
>     **Почему неправильно:** Micronaut отлично работает на стандартной Lambda Java 17/21 runtime — старт ~500 мс (vs ~5–10 сек у Spring Boot JVM), что уже приемлемо для большинства сценариев. Native compilation — это **опциональная** оптимизация, не обязательное требование.
>
>     **Последствие ошибки:** команда отказывается от Micronaut, считая GraalVM-toolchain обязательным; теряют выигрыш в 10× даже без native-сборки.
>
> - [ ] **D.** Lambda-функции на Micronaut не поддерживают DI — `@Inject` внутри handler-а не работает, нужно вручную создавать `ApplicationContext` в каждом вызове
>
>     **Почему неправильно:** базовый класс `MicronautRequestHandler` сам управляет `ApplicationContext`: контекст создаётся один раз при init (вне billed time), а `@Inject` поля заполняются автоматически. Ручное создание контекста в `execute()` — антипаттерн, который убивает весь выигрыш Micronaut.
>
>     **Последствие ошибки:** разработчик пишет `ApplicationContext.run()` в `execute()` → cold start превращается в "warm start" (каждый invoke поднимает контекст 500мс) → биллинг растёт в 10×, latency деградирует.

## Q21. Distributed tracing, metrics?

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


> [!mcq] Что нужно сделать в Micronaut, чтобы получить distributed tracing запросов между микросервисами?
>
> - [ ] **A) Достаточно подключить `io.micronaut.tracing:micronaut-tracing-zipkin`, никакая конфигурация и аннотации не нужны — Micronaut сам обернёт все контроллеры в spans.**
>
>     **Почему неправильно:** depencency сама по себе только подключает классы; без `tracing.zipkin.enabled=true` и URL Zipkin-коллектора tracer не активируется и spans никуда не уходят. Аннотации `@NewSpan`/`@ContinueSpan` нужны там, где нужно явно создать или продолжить span внутри сервиса.
>
>     **Последствие ошибки:** dependency есть, но в Zipkin/Jaeger пусто → разработчик думает «не работает» и удаляет интеграцию, теряя возможность видеть traceId в логах распределённого вызова.
>
> - [ ] **B) Включить `micrometer.metrics.export.prometheus.enabled=true` — Prometheus метрики автоматически содержат distributed traces по HTTP-запросам.**
>
>     **Почему неправильно:** путаются два разных понятия — metrics (агрегированные числа: счётчики, гистограммы) и tracing (span-дерево конкретного запроса). Prometheus собирает метрики, но не хранит span context. Для tracing нужен отдельный backend (Zipkin/Jaeger/Tempo) и propagation заголовков (B3/W3C).
>
>     **Последствие ошибки:** команда видит `http_server_requests_seconds`, но не может найти, где именно тормозит цепочка `service-A → service-B → DB` — нет span-tree, нет latency по узлам.
>
> - [x] **C) Подключить `micronaut-tracing-zipkin` (или `jaeger`/`opentelemetry`), включить `tracing.zipkin.enabled=true` с URL коллектора, и Micronaut автоматически проинструментирует HTTP-сервер/клиент; для кастомных spans использовать `@NewSpan` / `@ContinueSpan` / `@SpanTag`.**
>
>     **Почему правильно:** Micronaut Tracing построен поверх OpenTracing/OpenTelemetry API. Дефолтный фильтр оборачивает каждый HTTP-запрос в span, declarative HTTP-клиенты пробрасывают B3 заголовки автоматически — получается end-to-end trace из коробки. Аннотации нужны только для интроспекции бизнес-логики (DB calls, async work).
>
>     **Механизм:** `HttpServerFilter` создаёт root span на входе, `HttpClientFilter` инжектит trace context в исходящие запросы. Метрики собираются параллельно через Micrometer (`micrometer-registry-prometheus`) — это ортогональные подсистемы, обе нужны.
>
>     **Production-нюанс:** ставь `tracing.zipkin.sampler.probability=0.1` (10%) в high-RPS сервисах — full sampling раздувает Zipkin storage и добавляет latency на сериализацию span.
>
> - [ ] **D) Установить `@Traced` на main-класс приложения — это включит автоматический tracing всех методов всех бинов в приложении.**
>
>     **Почему неправильно:** такой аннотации в Micronaut нет, и даже если бы была — трассировать ВСЕ методы всех бинов фатально для performance (span на каждый getter/utility-вызов). Tracing должен быть selective: HTTP-границы автоматически, бизнес-методы — по `@NewSpan` точечно.
>
>     **Последствие ошибки:** ожидание «всё проинструментируется само» → команда не настраивает `@NewSpan` на критичные методы (DB, external calls) → в Zipkin видно только HTTP-входы, а где тормозит внутри — неизвестно.

## Q22. Reactive поддержка (Reactor, RxJava)?

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


> [!mcq] Что верно о reactive-поддержке в Micronaut по сравнению со Spring WebFlux?
>
> - [ ] **A) Micronaut поддерживает только Reactor — RxJava нельзя использовать, потому что под капотом фреймворк жёстко завязан на Project Reactor через Spring WebFlux.**
>
>     **Почему неправильно:** Micronaut НЕ использует Spring WebFlux — его HTTP-стек собственный (Netty + Micronaut HTTP). Reactor — дефолт, но RxJava 2/3 подключается отдельной зависимостью (`micronaut-rxjava2`/`rxjava3`) и используется как тип возврата контроллера (`Single<User>`, `Flowable<User>`) без дополнительных конвертеров.
>
>     **Последствие ошибки:** команда мигрирует с RxJava-кодовой базы и думает, что придётся переписывать всё на Reactor → недели лишней работы вместо подключения одного `runtime` модуля.
>
> - [ ] **B) В Micronaut, как и в Spring WebFlux, нельзя смешивать blocking и reactive endpoints в одном приложении — выбор делается на уровне приложения через `spring.main.web-application-type`.**
>
>     **Почему неправильно:** в Micronaut каждый endpoint независим — на одном контроллере может быть `@Get public User get()` (blocking, executed on IO pool) и рядом `@Get public Mono<User> getReactive()`. Фреймворк сам определяет по типу возврата, на каком executor запустить метод (event loop vs IO thread pool через `@ExecuteOn`).
>
>     **Последствие ошибки:** разработчик создаёт два отдельных микросервиса «реактивный» и «блокирующий» вместо одного приложения → лишние деплои, дублирование инфраструктуры, сложнее общая авторизация.
>
> - [ ] **C) Reactive в Micronaut работает поверх Tomcat NIO; для работы достаточно вернуть `CompletableFuture` из контроллера — `Mono`/`Flux` не нативны и конвертируются через адаптер.**
>
>     **Почему неправильно:** Micronaut HTTP-сервер построен на **Netty**, не на Tomcat. `Mono`/`Flux` — first-class возвращаемые типы (через `ReactiveTypeConverter`), никакие адаптеры подключать не нужно. `CompletableFuture` тоже поддерживается, но это не главный механизм reactive в Micronaut.
>
>     **Последствие ошибки:** разработчик думает, что Micronaut «не настоящий reactive» и выбирает Spring WebFlux на новом проекте ради «настоящего» Netty — теряет ahead-of-time DI и быстрый startup без выгоды.
>
> - [x] **D) Micronaut поддерживает и Reactor (по умолчанию), и RxJava (через отдельный модуль); HTTP-стек на Netty+Reactor, и в одном контроллере можно микшировать blocking endpoints с `Mono`/`Flux`-эндпоинтами — фреймворк сам выбирает thread pool по типу возврата.**
>
>     **Почему правильно:** Micronaut реализует HTTP-сервер на Netty с reactive event loop. Тип возврата (`User`, `Mono<User>`, `Flux<User>`, `Single<User>`, `CompletableFuture<User>`) автоматически интерпретируется reactive-streams адаптером. Blocking методы маршрутизируются на IO thread pool, reactive — выполняются на event loop без переключения потоков.
>
>     **Механизм:** `RouteExecutor` смотрит на возвращаемый тип через `ConversionService` + `ReactiveTypeConverter`; если это reactive publisher — подписывается напрямую на event loop; если обычный объект — отправляет выполнение в IO pool (`@ExecuteOn(TaskExecutors.IO)` переопределяет это явно).
>
>     **Сравнение со Spring:** Spring разделяет MVC (Tomcat/blocking) и WebFlux (Netty/reactive) на уровне приложения. Micronaut объединяет это — миграция blocking → reactive идёт endpoint by endpoint, без переписывания инфраструктуры. Это огромный плюс при постепенном переходе на reactive.
>
>     **Production-нюанс:** даже в Micronaut НЕ блокируй event loop (`Thread.sleep`, JDBC без R2DBC, file I/O) — это убьёт throughput. Для blocking операций в reactive-контроллере используй `Schedulers.boundedElastic()` или вынеси на `@ExecuteOn(IO)`.

## Q23. (!) Когда выбирать Micronaut?

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


> [!mcq] Какой сценарий лучше всего подходит для выбора Micronaut вместо Spring Boot для нового сервиса?
>
> - [x] **A) Микросервис, который будет деплоиться в AWS Lambda (или autoscaling Kubernetes с частыми ребалансами), активно использует declarative HTTP-клиенты к другим сервисам и должен быть готов к GraalVM native image в перспективе.**
>
>     **Почему правильно:** это сценарий, где сильные стороны Micronaut напрямую решают боль:
>     - **Cold start** Lambda: Micronaut за счёт compile-time DI стартует за ~80мс на JVM (vs 2–4с Spring Boot) и ~30мс на GraalVM native — Lambda оплачивается по времени, выигрыш реальный денежный.
>     - **Autoscaling K8s** с частыми ребалансами: каждый новый pod выходит из «cold» состояния за секунды, а не минуты — readiness probe срабатывает быстро, HPA реагирует адекватно.
>     - **Declarative HTTP-клиенты** (`@Client("user-service") interface UserClient`) — генерируются в compile time, без runtime proxy и Feign-overhead. В микросервисной архитектуре это сразу +N сервис-вызовов «бесплатно».
>     - **GraalVM native** — Micronaut проектировался с native в уме (нет рантайм-рефлексии в DI), миграция дешёвая. Spring Boot 3 тоже умеет, но требует больше ручной конфигурации reflection hints.
>
>     **Когда это решающий фактор:** serverless-нативные системы (Lambda/Cloud Functions), event-driven с горизонтальным масштабированием, k8s-окружение с агрессивным autoscaling и стоимостью pod-минут.
>
> - [ ] **B) Большое существующее монолитное приложение на Spring Boot с богатой бизнес-логикой, которое нужно постепенно модернизировать — Micronaut можно подключить как замену Spring внутри того же модуля.**
>
>     **Почему неправильно:** Micronaut и Spring — это **разные DI/AOP-фреймворки**, их нельзя «подключить параллельно» в одном модуле. Миграция большого Spring-монолита на Micronaut = переписывание DI, AOP, конфигурации, тестов. Стоимость огромная, выигрыш в startup не компенсирует.
>
>     **Последствие ошибки:** команда тратит квартал на «модернизацию» переписыванием, ломает работающий код, теряет редкие Spring-интеграции (Spring Security/OAuth filter chain, Spring Data специфика, Spring Batch) — все они в Micronaut либо отсутствуют, либо беднее.
>
> - [ ] **C) Сервис, где критичны редкие интеграции и максимальная функциональная полнота — поддержка любого мессенджера (Kafka/RabbitMQ/Pulsar/ActiveMQ/IBM MQ), любых БД, всех cloud providers «из коробки».**
>
>     **Почему неправильно:** это сильная сторона **Spring Boot**, а не Micronaut. Spring имеет на порядок больше готовых стартеров и community-интеграций. У Micronaut Kafka/RabbitMQ есть, но Pulsar/IBM MQ — либо беднее, либо вообще нет, придётся писать руками поверх raw драйвера.
>
>     **Последствие ошибки:** команда выбирает Micronaut «потому что быстрый», натыкается на отсутствие нужной интеграции, либо мигрирует обратно (потерянное время), либо пишет интеграцию сама (баги, отсутствие поддержки).
>
> - [ ] **D) Высоконагруженный CRUD-сервис на Spring Data JPA с большим количеством сложных запросов и нужно сохранить весь существующий код Hibernate-репозиториев — Micronaut использует тот же Spring Data JPA под капотом.**
>
>     **Почему неправильно:** Micronaut **не использует** Spring Data JPA. У него своя `micronaut-data-jpa` с похожим API, но не идентичным: нет `@Query` SpEL-выражений Spring, специфики Spring Data Projections, кастомных repository базовых классов в Spring-стиле. Если код активно опирается на Spring Data API — миграция = переписывание репозиториев.
>
>     **Последствие ошибки:** команда ожидает «копи-пейст репозиториев», на деле получает компиляционные ошибки на каждой нетривиальной аннотации/запросе → срыв сроков миграции, отказ от Micronaut посередине процесса.

## Q24. (!) Какие минусы Micronaut?

1. **Меньше community** по сравнению со Spring (но больше Quarkus в некоторых нишах)
2. **Меньше готовых интеграций** — для Kafka, RabbitMQ есть, но более экзотические — нет
3. **Документация** сильно отстаёт от Spring по объёму примеров
4. **Меньше JetBrains/IDEA support** — некоторые рефакторинги работают хуже
5. **Native image** требует `@Introspected` на всех DTO — easy to forget
6. **Третьесторонние** libraries часто требуют донастройки для native
7. **Compile time** — больше времени на сборку, медленнее editor feedback в больших проектах


> [!mcq] Какой минус Micronaut критичен именно для native image и команды без AOT-опыта?
>
> - [ ] A) Меньше community и медленнее ответы на StackOverflow по сравнению со Spring.
>
>     **Почему неправильно:** размер community — общая проблема любой не-Spring экосистемы, но она не блокирует разработку. Можно компенсировать чтением исходников, GitHub Issues, Gitter-каналом Micronaut. Это **дискомфорт, а не stopper**.
>
>     **Последствие ошибки:** команда фокусируется на «социальном» риске и упускает технические — например, ловит surprise на проде, когда DTO без `@Introspected` падают в native image.
>
> - [x] B) Native image требует `@Introspected` на всех DTO/POJO, участвующих в сериализации — забыли аннотацию → `ClassNotFoundException` или пустые JSON-поля в runtime.
>
>     **Почему правильно:** GraalVM native image не делает классический reflection — Micronaut **на этапе компиляции** генерирует introspection-метаданные только для классов с `@Introspected` (или попадающих под `@Serdeable`, `@Entity` и т.д.). Если разработчик добавил новый DTO и забыл аннотацию — на JVM-режиме всё работает (Jackson через reflection), а в native image поле молча станет null или class не найдётся при десериализации.
>
>     **Механизм:** annotation processor `micronaut-inject-java` сканирует `@Introspected` → генерирует `*$Introspection.class` с явными getters/setters. Native image берёт **только эти** сгенерированные классы, обычный reflection отключён.
>
>     **Use-case:** при ревью кода в Micronaut-проектах с native — обязательная проверка «есть ли `@Introspected` на новых DTO» должна быть в чек-листе или ArchUnit-тесте. Альтернатива — `@Introspected` на package-info.java для всего пакета.
>
>     **Best practice:** включить `micronaut.application.fail-fast=true` + интеграционный smoke-test в native режиме на CI, чтобы ловить пропущенные аннотации до production.
>
> - [ ] C) Документация Micronaut объёмнее, чем Spring, что замедляет онбординг.
>
>     **Почему неправильно:** прямо противоположно реальности — документация Micronaut **меньше** Spring (в разы по объёму примеров), и это действительно минус, но не «объёмнее». Это инверсия факта.
>
>     **Последствие ошибки:** в техническом обсуждении такой ответ сразу маркирует кандидата как не работавшего с Micronaut — он не видел разницу в guides.
>
> - [ ] D) Compile time меньше, чем у Spring Boot, из-за compile-time DI.
>
>     **Почему неправильно:** compile-time DI **увеличивает** время сборки (annotation processor генерирует код для каждого `@Singleton`/`@Inject`), а не уменьшает. Runtime startup быстрее — но это другая метрика. Путаница compile time vs startup time.
>
>     **Последствие ошибки:** команда выбирает Micronaut, ожидая быструю IDE-сборку в больших проектах, и сталкивается с медленным incremental build → падает developer productivity.

## Q25. Производительность Micronaut vs Quarkus vs Spring Boot?

| Метрика | Micronaut | Quarkus | Spring Boot |
|---------|-----------|---------|-------------|
| Cold start (JVM) | 1.0-1.5 сек | 1.0-1.5 сек | 3-15 сек |
| Cold start (native) | ~30 ms | ~10-30 ms | ~50-200 ms |
| Memory (JVM) | 80-120 MB | 80-150 MB | 150-300 MB |
| Memory (native) | 30-50 MB | 30-50 MB | 50-100 MB |
| Throughput (req/s) | ~100K | ~100K | ~80-100K |

**Микро-различия в throughput**, существенные — в startup и memory. Все три современные фреймворки сильно лучше старого Spring Boot (до 3.0).


> [!mcq] Где Micronaut и Quarkus реально обгоняют Spring Boot, а где разница незначительна?
>
> - [ ] A) Throughput (req/s) у Micronaut в 5-10 раз выше Spring Boot — это главное преимущество.
>
>     **Почему неправильно:** по бенчмаркам TechEmpower и собственным замерам разница в throughput **в пределах 10-25%**, иногда Spring Boot 3.x даже выигрывает на одном инстансе с прогретым JIT. Это **микро-различия**, не порядки. Слишком сильное заявление = не понимаешь природу преимуществ AOT-фреймворков.
>
>     **Последствие ошибки:** команда выбирает Micronaut **ради throughput** под нагрузку 100K RPS, тратит месяцы на миграцию — и получает те же цифры, что были на Spring. Перенос был необоснован, ROI отрицательный.
>
> - [ ] B) Memory footprint Spring Boot одинаков с Micronaut и Quarkus в любом режиме.
>
>     **Почему неправильно:** прямо противоположно фактам. Spring Boot держит 150-300 MB heap на старте (рефлексивный DI, BeanFactory, прокси), Micronaut/Quarkus — 80-150 MB. В native image разница ещё больше (30-50 MB vs 50-100 MB). Это **измеримое 2-3x преимущество** для Kubernetes/serverless.
>
>     **Последствие ошибки:** sizing подов рассчитан по Spring-меркам — pods за-OOM-нутся под нагрузкой или, наоборот, переплата за memory limits в облаке.
>
> - [x] C) Главное преимущество — startup time и memory footprint, особенно в native image (~30 ms vs 50-200 ms cold start). Throughput у всех трёх сопоставим.
>
>     **Почему правильно:** ключевая выгода AOT-фреймворков (Micronaut, Quarkus) — **холодный старт и память**, а не пропускная способность установившегося трафика. Spring Boot прогревается JIT-ом за секунды и догоняет по throughput. Но если pod рестартует, scale-to-zero в Knative или Lambda — каждая секунда старта = деньги и SLA.
>
>     **Конкретные цифры:**
>     - JVM cold start: Micronaut/Quarkus ~1-1.5 сек, Spring Boot 3-15 сек.
>     - Native cold start: Micronaut/Quarkus ~10-30 ms, Spring Boot AOT ~50-200 ms.
>     - Memory: AOT-фреймворки экономят 2-3x heap.
>     - Throughput: разница в пределах погрешности (~10-20%).
>
>     **Use-case выбора:** serverless (AWS Lambda, Cloud Run), частые scale-up под спайки, CI с тысячами интеграционных тестов (быстрее старт контекста), edge/IoT с ограниченной памятью.
>
>     **Best practice:** при обсуждении производительности **всегда уточнять метрику** — throughput, latency p99, cold start, memory. Без этого сравнение бессмысленно.
>
> - [ ] D) Cold start Spring Boot 3.x в native режиме всегда быстрее Micronaut.
>
>     **Почему неправильно:** Spring Boot 3 действительно получил поддержку native (Spring AOT), но стартует **медленнее** Micronaut/Quarkus (50-200 ms vs 10-30 ms) — потому что Spring AOT — это «прибитый поверх» reflection-ориентированной архитектуры, а Micronaut был спроектирован под AOT изначально.
>
>     **Последствие ошибки:** команда выбирает Spring Native, ожидая лидера по cold start, и не получает целевых SLA в Lambda с p99 cold start < 100 ms.

## Q26. (!) Какие компании используют Micronaut в production?

- **Oracle** — внутренние сервисы (logically — фреймворк родственный)
- **Boeing**
- **Walmart Labs**
- **Target**
- **Goldman Sachs** — некоторые сервисы
- **Various FinTech** (используется для микросервисов)

Меньше публичности, чем у Spring или Quarkus, но в enterprise сегменте присутствие хорошее.


> [!mcq] Что говорит публичный adoption Micronaut о его зрелости для enterprise?
>
> - [ ] A) Micronaut используется только в стартапах, никаких корпораций — поэтому брать в enterprise рискованно.
>
>     **Почему неправильно:** факт обратный — Micronaut активно используют **крупные enterprise**: Oracle (Micronaut Foundation), Boeing, Walmart Labs, Target, Goldman Sachs (частично), ряд FinTech. Это не «startup-only фреймворк». Заявление противоречит публичной информации.
>
>     **Последствие ошибки:** архитектурный комитет отвергает Micronaut на основании ложного факта → команда уходит на Spring Boot с худшим cold start, проигрывая в SLA для serverless-нагрузки.
>
> - [ ] B) Micronaut поддерживается только Oracle и больше никем, как закрытый внутренний инструмент.
>
>     **Почему неправильно:** Micronaut — **open source** (Apache 2.0), управляется **Micronaut Foundation** (некоммерческая организация, отделена от Oracle). Внешняя экосистема: Object Computing (создатели), Sonatype, contributors из Boeing, Walmart и др. Это не «Oracle-only».
>
>     **Последствие ошибки:** менеджмент боится vendor lock-in на Oracle → необоснованно блокирует выбор Micronaut, теряя его технические преимущества.
>
> - [ ] C) Только Goldman Sachs использует Micronaut, и только в одной legacy-системе.
>
>     **Почему неправильно:** даже сам Goldman Sachs не «только в legacy» — у них Micronaut в новых микросервисах. Плюс есть Boeing, Walmart, Target, Oracle, разные FinTech. Заявление сужает реальность до одной компании.
>
>     **Последствие ошибки:** недооценка adoption → отказ от Micronaut → выбор более медленного стека под cloud-native нагрузку.
>
> - [x] D) Micronaut в production используют Oracle, Boeing, Walmart Labs, Target, Goldman Sachs и FinTech-компании — adoption меньше Spring, но в enterprise присутствие весомое, особенно где важны cold start и память.
>
>     **Почему правильно:** публичные case studies, GitHub testimonials, конференционные доклады (Micronaut Connect, Devoxx) подтверждают этот список. **Меньше публичности**, чем у Spring, — но в enterprise-сегменте Micronaut представлен достаточно, чтобы не считать его «экспериментальным».
>
>     **Use-case adoption:** микросервисы под Kubernetes (Walmart Labs), serverless на AWS Lambda (FinTech), внутренние Oracle Cloud-сервисы, edge-приложения (Boeing). Везде, где **cold start и memory важнее, чем максимальный community size**.
>
>     **Как использовать на собеседовании:** при выборе между Spring Boot и Micronaut аргументировать adoption — «не bleeding edge, есть production-проверка в Tier-1 компаниях, есть Foundation и поддержка LTS-веток». Это снижает архитектурный риск в глазах менеджмента.
>
>     **Best practice:** перед принятием решения смотреть **свежие** case studies (последние 1-2 года) на micronaut.io/case-studies и в GitHub Issues — оценить активность maintainers и реальное использование, а не маркетинговые упоминания.

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

