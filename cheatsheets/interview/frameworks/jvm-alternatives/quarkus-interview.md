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
- [Q13. (!) Чем отличаются RESTEasy Reactive и RESTEasy Classic?](#q13--чем-отличаются-resteasy-reactive-и-resteasy-classic)
- [Q14. (!) Какие части MicroProfile поддерживает Quarkus?](#q14--какие-части-microprofile-поддерживает-quarkus)
- [Q15. Config через MicroProfile Config?](#q15-config-через-microprofile-config)
- [Q16. Health, Metrics, OpenAPI — встроенные?](#q16-health-metrics-openapi--встроенные)

**Реактивный стек**
- [Q17. (!) Что такое Mutiny?](#q17--что-такое-mutiny)
- [Q18. Чем Mutiny отличается от Reactor и RxJava?](#q18-чем-mutiny-отличается-от-reactor-и-rxjava)
- [Q19. Uni и Multi в Mutiny?](#q19-uni-и-multi-в-mutiny)
- [Q20. (!) Vert.x под капотом?](#q20--vertx-под-капотом)

**Persistence**
- [Q21. (!) Что такое Hibernate ORM with Panache?](#q21--что-такое-hibernate-orm-with-panache)
- [Q22. Чем различаются Active Record и Repository в Panache?](#q22-чем-различаются-active-record-и-repository-в-panache)
- [Q23. Hibernate Reactive — что это?](#q23-hibernate-reactive--что-это)

**Dev experience**
- [Q24. (!) Что такое dev mode и live reload?](#q24--что-такое-dev-mode-и-live-reload)
- [Q25. Что такое continuous testing?](#q25-что-такое-continuous-testing)
- [Q26. Что такое Dev Services в Quarkus?](#q26-что-такое-dev-services-в-quarkus)

**Тестирование**
- [Q27. (!) @QuarkusTest и его особенности?](#q27--quarkustest-и-его-особенности)
- [Q28. RestAssured интеграция?](#q28-restassured-интеграция)

**Production**
- [Q29. (!) Как Quarkus интегрируется с Kubernetes?](#q29--как-quarkus-интегрируется-с-kubernetes)
- [Q30. (!) Когда выбирать Quarkus вместо Spring Boot?](#q30--когда-выбирать-quarkus-вместо-spring-boot)
- [Q31. (!) Какие минусы Quarkus?](#q31--какие-минусы-quarkus)

## Q1. (!) Что такое Quarkus и зачем он нужен?

`Quarkus` — Java-фреймворк от **Red Hat** (с 2019), заточенный под **Kubernetes-native** приложения. Его главная задача — сделать Java конкурентоспособной с Go и Node.js в облаке, где традиционный минус Java (долгий старт и большое потребление памяти) превращается в реальные деньги: при auto-scaling и serverless вы платите за каждую секунду старта и каждый мегабайт RAM на реплику.

Решение Quarkus — **перенести как можно больше работы со старта приложения на этап сборки**. То, что Spring делает при запуске (сканирование classpath, рефлексия, построение контейнера), Quarkus вычисляет при компиляции и «запекает» в артефакт.

**Ключевые особенности:**
- **Supersonic startup** — запуск за десятки миллисекунд (в native image)
- **Subatomic memory** — потребление от ~30 MB RAM
- **Build-time оптимизации** — DI, конфигурация, аннотации обрабатываются при сборке, а не при старте
- **Live reload** в dev-mode без рестарта JVM
- **MicroProfile** API + специфичные для Quarkus extensions
- **Reactive первого класса** — через Mutiny и Vert.x

**Зачем это нужно:** на типовом сервисе в VM разница незаметна, но на десятках реплик в Kubernetes или в FaaS экономия памяти и быстрый cold start напрямую снижают счёт за инфраструктуру.

## Q2. (!) Чем Quarkus отличается от Spring Boot?

Коротко: **главное различие — момент сборки DI-контейнера**. Quarkus строит граф зависимостей при компиляции (build-time DI на ArC), Spring — при старте приложения (runtime DI). Из этого следствием вытекает почти всё остальное в таблице: быстрый старт, меньшее потребление памяти и first-class поддержка native image у Quarkus.

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

**Вывод:** Spring Boot — универсальный фреймворк с огромной экосистемой; Quarkus специализирован под облако и платит за это меньшим community и менее знакомым стеком (MicroProfile/Jakarta EE вместо Spring API).

## Q3. (!) Что такое supersonic subatomic Java?

Это маркетинговый слоган Quarkus, который описывает две его цели:

- **Supersonic** (сверхзвуковой) — про скорость старта: десятки миллисекунд в native, 1-2 сек на JVM
- **Subatomic** (субатомный) — про потребление памяти: от 30 MB

Слоган не пустой — за ним стоят конкретные технические приёмы, которые убирают работу из рантайма:

1. **Build-time обработка** — аннотации разбираются при компиляции, а не при старте
2. **Tree-shaking** — из артефакта выкидываются неиспользуемые классы, остаётся только то, что реально вызывается
3. **GraalVM native image** — компиляция в нативный бинарник без JVM
4. **Минимизация runtime reflection** — известные паттерны заменяются на заранее сгенерированный код, поэтому при старте не надо ничего «угадывать» рефлексией

**Логика связки:** меньше работы при старте → быстрее запуск; меньше рефлексии и метаданных → меньше памяти и возможен native image.

## Q4. Что такое extensions в Quarkus?

`Extension` — модульный компонент, который добавляет в приложение функциональность (REST, ORM, Kafka и т.д.). Концептуально это аналог Spring Boot starters, но с принципиальным отличием: extension умеет работать на этапе сборки и заранее знает про native image.

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

Каждое extension состоит из двух модулей:
- **Runtime module** — код, который работает в самом приложении
- **Deployment module** — код, который работает при сборке (генерация bean'ов, обработка конфигурации)

Именно наличие deployment-модуля и отличает extension от обычной зависимости: благодаря ему extension знает, какие классы понадобятся в native image, и **сам регистрирует reflection-metadata**. Поэтому Hibernate, Jackson и прочие «дружат» с native image из коробки — вам не приходится вручную перечислять классы для рефлексии.

## Q5. (!) Что такое build-time DI и почему это важно?

**Build-time DI** — это построение графа зависимостей (DI-контейнера) на этапе сборки, а не при старте приложения. Это центральная идея Quarkus, из которой растут все его преимущества.

Сравним с Spring: там DI происходит при **запуске** — сканируется classpath, создаётся BeanFactory, обрабатываются аннотации через рефлексию. В Quarkus всё это делается **во время сборки** через **ArC** (реализацию CDI от Quarkus):

```
Spring (runtime):
  Compile → Start → Scan classpath → Reflection → Build BeanFactory → Run

Quarkus (build-time):
  Compile → Process annotations → Generate bean code → Bake into JAR
  Start → Run (всё уже готово)
```

**Почему это важно (каждый пункт — прямое следствие переноса работы на сборку):**
- **Быстрый старт** — при запуске уже нечего делать, граф bean'ов готов
- **Меньше runtime reflection** → возможен native image (рефлексию GraalVM плохо переваривает)
- **Меньше памяти** — не нужно держать метаданные о классах в RAM
- **Ошибки конфигурации ловятся при сборке**, а не падают в проде на старте (например, неразрешённая инъекция bean'а упадёт компиляцией)

## Q6. (!) Как Quarkus генерирует код во время сборки?

Кодогенерацию запускает **Maven/Gradle-плагин Quarkus**, который дёргает **deployment-модули** подключённых extensions. Процесс делится на три фазы:

1. **Augmentation phase** — анализ classpath, обработка аннотаций, генерация bean'ов (основная фаза кодогенерации)
2. **Static init** — код, исполняемый один раз при сборке (например, вычисление конфигурации)
3. **Runtime init** — то немногое, что всё же откладывается на старт (то, что нельзя посчитать заранее, — например, чтение секретов из окружения)

Сгенерированный код добавляется прямо в JAR. Поэтому при запуске нет ни сканирования classpath, ни построения контейнера, ни рефлексии — приложение просто исполняет уже готовый код.

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

## Q7. Что такое recorder и BuildItem?

Это два кирпичика, на которых стоит build-time-кодогенерация Quarkus.

`BuildItem` — единица данных, которой обмениваются **build steps** (методы, обрабатывающие отдельные аспекты сборки). Это типобезопасный способ передать результат одного шага другому: один шаг «производит» BuildItem, другой его «потребляет» как параметр.

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

`Recorder` — механизм, который во время сборки **записывает** будущие runtime-действия (как «байткод-шаблон»), а исполняются они уже при старте. Так часть логики, которую нельзя полностью вычислить на сборке, всё равно описывается на этапе augmentation, а не пишется руками в стартовом коде.

Связка `BuildStep` + `BuildItem` + `Recorder` — это и есть ядро Quarkus, позволяющее переносить работу со старта в build time.

## Q8. (!) Что такое GraalVM Native Image?

**GraalVM Native Image** — это технология от Oracle, которая компилирует JVM-байткод в **самодостаточный нативный исполняемый файл**, не требующий установленной JVM. Внутрь бинарника зашивается минимальный рантайм (Substrate VM), поэтому файл запускается как обычная нативная программа.

```bash
native-image -jar myapp.jar  # → myapp (linux/macOS executable)
```

**Ключевые особенности (и их обратная сторона):**
- **AOT-компиляция** (Ahead-Of-Time) — весь код компилируется заранее, а не JIT'ом в рантайме
- **Закрытый мир** (closed-world): все классы должны быть известны при сборке
- **Нет dynamic class loading** по умолчанию
- **Reflection и dynamic proxy** нужно регистрировать вручную (или это делает Quarkus за вас)

**Что это даёт:**
- Старт за миллисекунды — нет JIT warmup, код уже скомпилирован
- Память от ~30 MB — нет накладных расходов JVM
- Не нужен JVM в runtime → меньше базовый Docker-образ

Цена за это — ограничения closed-world (см. Q11) и потеря JIT-оптимизаций (см. Q10).

## Q9. (!) Как Quarkus собирает native binary?

Native-сборка запускается одной командой — отдельной от обычной упаковки. Для неё нужен установленный GraalVM либо Docker (тогда GraalVM не требуется локально — сборка идёт в контейнере):

```bash
# Maven
./mvnw package -Pnative

# Gradle
./gradlew build -Dquarkus.package.type=native

# Без локального GraalVM — в Docker контейнере
./mvnw package -Pnative -Dquarkus.native.container-build=true
```

Под капотом Quarkus сначала собирает обычный JAR (фаза augmentation уже отработала кодогенерацию), а затем отдаёт его в GraalVM `native-image`. Сборка занимает **2-10 минут** — заметно дольше обычной, потому что AOT-компилятор анализирует весь граф достижимого кода. Результат — `target/myapp-runner` (linux executable, ~30-100 MB).

```bash
docker build -f src/main/docker/Dockerfile.native -t myapp .
docker run -p 8080:8080 myapp
```

## Q10. (!) Какие преимущества и недостатки native image?

Native image — это **компромисс**: вы выигрываете на старте и памяти, но проигрываете на пиковой пропускной способности и удобстве разработки. Где этот компромисс выгоден, а где нет — и есть суть вопроса.

**Плюсы (за что берут native):**
- Startup за **миллисекунды** (против 1-15 сек на JVM)
- Памяти **в 5-10 раз меньше** (30-50 MB против 150-500 MB)
- Не нужен JVM в runtime → меньше Docker-образ
- Идеально для serverless (Lambda, Cloud Run), где платят за cold start
- Идеально для horizontal scaling в Kubernetes, где важна память на реплику

**Минусы (чем платят):**
- **Долгая сборка** (2-10 минут) — бьёт по скорости CI
- **Сложности с reflection** — динамические классы надо регистрировать
- **Нет JIT** — пиковый throughput ниже на ~30-50%, т.к. код не оптимизируется под реальную нагрузку
- **Нет dynamic class loading** без специальной настройки
- **Сложнее отладка** — gdb вместо jdb
- **Профилировщики** ограничены

**Эмпирическое правило:** native выгоден для коротко-живущих или часто масштабируемых сервисов (FaaS, scale-to-zero); для долгоживущего сервиса под постоянной высокой нагрузкой JVM с JIT нередко даёт больший throughput.

## Q11. Что такое closed-world assumption?

**Closed-world assumption (CWA, гипотеза закрытого мира)** — это фундаментальное допущение Native Image: на этапе компиляции AOT-компилятор должен видеть **весь** граф достижимого кода (все классы, методы, поля, ресурсы). То, что компилятор не «увидел», в бинарник не попадёт. Именно это допущение и позволяет делать tree-shaking и заранее всё скомпилировать — но оно же ломает любую динамику.

**Что поэтому нельзя в native image по умолчанию** (компилятор не может вычислить это статически):
- Reflection по неизвестным заранее классам
- Dynamic proxy
- `Class.forName()` с именем, вычисляемым в рантайме
- Загрузка ресурсов по динамическим путям
- Динамическая загрузка JAR

**Решение — reachability metadata:** JSON-конфигурация, в которой вы явно объявляете классы, нужные для рефлексии. Это как бы «подсказка» компилятору: «эти классы используются динамически, не выкидывай их».

```json
[
  {
    "name": "com.example.MyClass",
    "allDeclaredFields": true,
    "allDeclaredMethods": true
  }
]
```

Главный практический плюс Quarkus: для известных фреймворков (Hibernate, Jackson и т.д.) extensions генерируют эту metadata **автоматически** — вручную писать JSON приходится в основном для своих DTO и сторонних библиотек без Quarkus-extension.

## Q12. Как работает reflection в native image?

Reflection работает только для классов, заранее зарегистрированных в **reachability metadata** — иначе из-за closed-world (см. Q11) они просто не попадут в бинарник. В Quarkus есть три способа их зарегистрировать.

**Способ 1 — аннотация `@RegisterForReflection`** (самый частый для своих DTO). **Способ 2 — programmatically** через build step (для extension-разработчиков):

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

Без регистрации в рантайме вы получите `ClassNotFoundException` или `NoSuchMethodException` — и, что коварно, только в native-сборке, а на JVM всё работало.

**Способ 3 (для незнакомых библиотек) — автоматический сбор metadata** через GraalVM Native Image Tracing Agent. Agent перехватывает реальные reflection-вызовы и записывает их в конфиг:

```bash
java -agentlib:native-image-agent=config-output-dir=meta-conf -jar app.jar
```

Запускаем приложение под нагрузкой или прогоняем тесты — agent фиксирует все обращения к рефлексии и сам формирует metadata. **Подводный камень:** покрытие зависит от того, какие пути кода вы реально прошли, — непокрытые ветки в native всё равно упадут.

## Q13. (!) Чем отличаются RESTEasy Reactive и RESTEasy Classic?

В Quarkus есть две реализации JAX-RS, и выбор между ними — это выбор между двумя моделями потоков. **RESTEasy Reactive** строится на event-loop Vert.x (неблокирующая модель), **RESTEasy Classic** — на классической servlet-модели «поток на запрос». По умолчанию в Quarkus 2.0+ рекомендуется Reactive.

**RESTEasy Reactive** (рекомендуется):
- Async по умолчанию, работает на event-loop'е
- Построен на Vert.x
- Высокий throughput при I/O-bound нагрузке (один поток обслуживает много запросов)
- Нативная поддержка Mutiny `Uni`/`Multi`

**RESTEasy Classic** (legacy):
- Синхронный, блокирующий (поток занят на всё время обработки запроса)
- Servlet-based
- Тоже совместим с JAX-RS API

Важно: Reactive не означает «обязательно писать реактивный код» — он отлично работает и с обычными блокирующими методами (Quarkus сам уведёт их в worker-пул), но раскрывается именно на async-эндпоинтах.

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

Аннотации JAX-RS (`@Path`, `@GET`, `@PathParam`) — это стандарт Java EE / Jakarta, концептуально близкий к аннотациям Spring MVC: разница в основном в именах и в том, что это вендоронезависимая спецификация.

## Q14. (!) Какие части MicroProfile поддерживает Quarkus?

**MicroProfile** — набор спецификаций от Eclipse Foundation для облачных Java-микросервисов: единый стандартный API для конфигурации, health-checks, метрик, отказоустойчивости и т.д. Quarkus реализует его через библиотеки SmallRye, так что вы программируете против стандарта, а не против проприетарного API.

Основные части и реализующие их extensions:

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

## Q15. Config через MicroProfile Config?

Конфигурация в Quarkus задаётся через стандарт MicroProfile Config: значения берутся из `application.properties` (а также из переменных окружения, system properties и других источников по приоритету) и инжектятся в код типизированно. Есть два стиля — отдельные поля через `@ConfigProperty` и целые группы свойств через `@ConfigMapping`.

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

Из коробки конвертируются типы `String`, `int`, `boolean`, `Duration`, `Optional<T>`, `List<T>`; для своих типов пишется custom-converter. `@ConfigMapping` предпочтительнее для связанных групп настроек — он даёт типобезопасный интерфейс и валидируется при сборке.

## Q16. Health, Metrics, OpenAPI — встроенные?

Да, observability в Quarkus — это подключаемые extensions, а не ручная обвязка: добавили зависимость — и endpoints появляются автоматически. Это прямое следствие MicroProfile-стандартов (Health, Metrics, OpenAPI).

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

Готовые endpoints (все под общим префиксом `/q/`):
- `/q/health` — общий статус
- `/q/health/live` — liveness (приложение живо, нужен ли рестарт)
- `/q/health/ready` — readiness (готово принимать трафик)
- `/q/metrics` — метрики в формате Prometheus
- `/q/openapi` — OpenAPI-спецификация
- `/q/swagger-ui` — Swagger UI
- `/q/dev` — dev console (только в dev mode)

Разделение liveness/readiness — не формальность: Kubernetes по liveness решает, перезапускать ли под, а по readiness — слать ли в него трафик. Свои проверки добавляются классом-`HealthCheck` с аннотацией `@Liveness` или `@Readiness`:

```java
@Liveness
public class DatabaseHealthCheck implements HealthCheck {
    @Override
    public HealthCheckResponse call() {
        return HealthCheckResponse.named("db").up().build();
    }
}
```

## Q17. (!) Что такое Mutiny?

`Mutiny` — реактивная библиотека от SmallRye/Red Hat, это «родной» реактивный API Quarkus (аналог роли, которую Reactor играет в Spring WebFlux). Её отличительная черта — навигационный, читаемый стиль API через явные группы операторов (`onItem()`, `onFailure()`), задуманный как более понятная альтернатива «плоским» цепочкам Reactor/RxJava.

**Два основных типа** (вся библиотека крутится вокруг них):
- `Uni<T>` — асинхронное вычисление **одного** значения (аналог `Mono<T>` в Reactor)
- `Multi<T>` — асинхронный **поток** значений (аналог `Flux<T>` в Reactor)

```java
Uni<User> user = userRepo.findById(1L);

user.onItem().transform(u -> u.name)
    .onFailure().recoverWithItem("default")
    .subscribe().with(name -> log.info(name));

Multi<User> users = userRepo.streamAll();
users.onItem().invoke(u -> log.info(u.name)).subscribe();
```

## Q18. Чем Mutiny отличается от Reactor и RxJava?

Все три — реактивные библиотеки с одной моделью (издатель-подписчик, backpressure по Reactive Streams), и различаются они в основном **стилем API и происхождением**, а не возможностями. Главное отличие Mutiny — явные группы операторов вместо плоских цепочек.

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

Аргумент авторов Mutiny: явные группы (`onItem`, `onFailure`) читаются легче, потому что сразу видно, какое событие мы обрабатываем, — это снижает порог входа для тех, кто не привык к Reactor. Это вопрос вкуса и привычки: опытным «реакторщикам» компактность Reactor может казаться удобнее.

## Q19. Uni и Multi в Mutiny?

На практике почти весь код на Mutiny — это три шага: **создать** (`createFrom()`), **преобразовать** (через группы `onItem()`/`onFailure()`) и **подписаться** (`subscribe()`). Без подписки ничего не выполнится — Mutiny ленив, как и другие реактивные библиотеки.

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

## Q20. (!) Vert.x под капотом?

Да: весь реактивный стек Quarkus стоит на **Eclipse Vert.x** — event-driven, неблокирующем toolkit'е для JVM (модель event-loop в духе Node.js). Mutiny при этом — лишь удобная обёртка над Vert.x; именно Vert.x обеспечивает реальную неблокирующую работу с I/O.

Что в Quarkus реализовано через Vert.x:
- HTTP-сервер — Vert.x Web
- Драйверы БД — Vert.x SQL Client (postgres, mysql и др.), неблокирующие
- Mailer — интеграции Vert.x
- Event Bus — Vert.x EventBus для внутренней коммуникации

```java
@Inject
Vertx vertx;

vertx.setTimer(1000, id -> log.info("Timer fired"));

@Inject
EventBus eventBus;

eventBus.publish("address", "message");
```

Quarkus отдаёт Vert.x на двух уровнях: можно инжектить «голый» `Vertx`/`EventBus` для низкоуровневой работы, а можно использовать Mutiny-обёртки для удобного реактивного API.

## Q21. (!) Что такое Hibernate ORM with Panache?

`Panache` — это надстройка над Hibernate ORM, которая убирает boilerplate: типовые операции (`persist`, `findById`, запросы) становятся короткими, а простые запросы пишутся в упрощённом HQL-синтаксисе. Под капотом это всё тот же Hibernate — Panache лишь добавляет удобный фасад.

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

В примере выше использован стиль **Active Record**: сущность наследует `PanacheEntity` и сама несёт методы доступа к данным (`persist`, `findByName`). Альтернатива — паттерн Repository (см. Q22). Поля делаются `public` намеренно: Panache на этапе сборки заменяет прямой доступ к полю на вызов геттера/сеттера, поэтому boilerplate-аксессоры не нужны.

## Q22. Чем различаются Active Record и Repository в Panache?

Panache предлагает **два стиля** работы с данными, и это типовой вопрос «что выбрать». Active Record — методы на самой сущности (короче кода), Repository — отдельный класс-репозиторий (привычнее по Spring Data и удобнее для тестов). Функционально они эквивалентны, разница — в дизайне и тестируемости.

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

**Рекомендация:** в большинстве enterprise-проектов берут **Repository** — статические методы Active Record сложнее мокать в unit-тестах, а отдельный репозиторий легко подменить и он лучше ложится на DDD. Active Record хорош для прототипов и небольших сервисов, где важна скорость написания.

## Q23. Hibernate Reactive — что это?

`Hibernate Reactive` — это реактивная (неблокирующая) версия Hibernate. Ключевое отличие от обычного: вместо блокирующего JDBC она работает через неблокирующие драйверы Vert.x SQL Client, а методы возвращают `Uni`/`Multi` вместо обычных значений. Так обращение к БД перестаёт блокировать поток event-loop.

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

**Сценарий применения:** high-throughput системы, где блокирующий JDBC становится узким местом (потоки висят в ожидании БД). **Когда не нужен:** в большинстве сервисов обычный Hibernate проще, привычнее и его производительности достаточно — реактивность добавляет сложности (весь стек должен стать неблокирующим), поэтому брать её стоит осознанно, а не по умолчанию.

Подробнее — в [Hibernate](../../databases/hibernate-interview.md).

## Q24. (!) Что такое dev mode и live reload?

**Dev mode** (`quarkus:dev`) — специальный режим запуска для разработки, главная фича которого — **live reload**: вы меняете код, и при следующем HTTP-запросе Quarkus незаметно пересобирает и перезагружает приложение без ручного рестарта JVM.

```bash
./mvnw quarkus:dev
```

Что включается в dev mode:
- **Live reload** — изменения в `.java`/`.properties` подхватываются без рестарта (пересборка происходит лениво, по первому входящему запросу)
- **Continuous testing** — тесты запускаются автоматически (см. Q25)
- **Dev UI** — веб-интерфейс на `/q/dev` (конфигурация, состояние БД, Kafka-топики)
- **Dev Services** — автозапуск зависимостей в Docker (см. Q26)

**Смысл:** дать Java тот же быстрый цикл «поправил → обновил вкладку → увидел результат», к которому привыкли в Node.js/Python, — традиционно слабое место Java из-за долгого рестарта.

## Q25. Что такое continuous testing?

Continuous testing — это режим, в котором в dev mode тесты автоматически перезапускаются при каждом сохранении файла, причём **только затронутые изменением тесты**, а не весь набор. Управление идёт горячими клавишами прямо в консоли:

```
[r] - re-run all
[f] - re-run failed
[v] - toggle verbose
[p] - pause
[h] - help
```

**Сценарий применения:** идеально ложится на TDD — пишете тест, видите красный/зелёный мгновенно, без ручного запуска и переключения в IDE.

## Q26. Что такое Dev Services в Quarkus?

**Dev Services** — это автоматический подъём инфраструктурных зависимостей (БД, брокеры) в Docker-контейнерах, когда вы запускаете dev mode или тесты. Главная ценность: вам не нужно ни поднимать Postgres вручную, ни прописывать URL/логин/пароль — Quarkus сам стартует контейнер и подставляет настройки подключения.

```properties
# application.properties — конфигурация можно НЕ указывать!
quarkus.datasource.db-kind=postgresql
# Quarkus сам поднимет Postgres в Docker и подключится
```

Поддерживаются: PostgreSQL, MySQL, MongoDB, Kafka, Redis, Keycloak, Elasticsearch, RabbitMQ и др.

**Важно:** Dev Services активны только в dev mode и test mode — в production Quarkus берёт реальный сервис из конфигурации и ничего сам не поднимает. Триггер прост: если явной конфигурации подключения нет, Quarkus считает, что нужен Dev Service, и стартует контейнер.

**Эффект:** резко ускоряет onboarding — новый разработчик клонирует репозиторий и сразу запускает `quarkus:dev`, не настраивая локально БД и брокеры.

## Q27. (!) @QuarkusTest и его особенности?

`@QuarkusTest` — основная аннотация для интеграционных тестов: она поднимает **реальное приложение Quarkus в том же процессе**, что и тесты, со всем DI-контейнером и эндпоинтами. То есть тест бьёт по живым HTTP-роутам (через RestAssured), а не дёргает методы в изоляции — это полноценный in-process интеграционный тест.

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

**Подмена зависимостей (mocking):** чтобы изолировать тестируемый сервис от настоящей БД или внешнего API, реальный bean заменяют моком через `@InjectMock` — Quarkus подменит его в контейнере, а в тесте вы программируете поведение через Mockito:

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

**Тест против native image** через `@QuarkusIntegrationTest`: он запускает уже собранный артефакт (в т.ч. native binary) как чёрный ящик. Это медленно и требует GraalVM на CI, зато ловит проблемы, которые видны только в native (см. Q12 — незарегистрированная рефлексия), и которых не было на JVM:

```java
@QuarkusIntegrationTest
class NativeTest extends BaseTest { }
```

## Q28. RestAssured интеграция?

`RestAssured` — это fluent-DSL для HTTP-тестов в стиле `given().when().then()`. В Quarkus он интегрирован из коробки: порт и base URL уже настроены под тестовое приложение, поэтому в `@QuarkusTest` можно сразу слать запросы, не конфигурируя клиент:

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

## Q29. (!) Как Quarkus интегрируется с Kubernetes?

Главная фишка — **генерация Kubernetes-манифестов прямо из сборки**: вы описываете деплой в `application.properties`, а Quarkus при `package` сам выпускает `kubernetes.yaml`/`kubernetes.json`. Не нужно вручную писать и синхронизировать YAML с кодом — это согласуется с общей идеей Quarkus делать работу на этапе сборки.

```xml
<dependency>
    <groupId>io.quarkus</groupId>
    <artifactId>quarkus-kubernetes</artifactId>
</dependency>
```

При сборке генерируются `kubernetes.yaml` и `kubernetes.json`:

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

Помимо «ванильного» Kubernetes есть extensions для генерации **Helm chart**, **Knative** (serverless поверх k8s) и манифестов **OpenShift**.

## Q30. (!) Когда выбирать Quarkus вместо Spring Boot?

Короткий ответ: **Quarkus — когда критичны cold start и память; Spring Boot — когда важнее зрелость экосистемы и наличие Spring-команды.** Решение почти всегда сводится к этому компромиссу.

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

## Q31. (!) Какие минусы Quarkus?

Главный честный ответ: минусы Quarkus — это в основном **обратная сторона его сильных сторон** (build-time и native) и **молодости экосистемы** относительно Spring.

**Экосистема и кадры:**
1. **Меньше community** по сравнению со Spring — меньше документации, статей, ответов на StackOverflow
2. **MicroProfile/Jakarta EE** API незнакомы тем, кто всю карьеру писал на Spring → порог входа для команды
3. **Меньше готовых интеграций** (хотя основные покрыты) — под редкую задачу может не оказаться extension

**Цена native image (расплата за быстрый старт):**
4. **Сложности с reflection** и сторонними библиотеками, не знающими про native
5. **Долгая native-сборка** (2-10 минут) — тормозит CI
6. **Тесты в native медленные** и требуют GraalVM на CI

**Прочее:**
7. **Live reload** иногда не справляется с глубокими изменениями — приходится перезапускать `quarkus:dev`
8. **Кривая обучения Mutiny** — другой стиль API, чем у привычных Reactor/RxJava
9. **Привязка к экосистеме Red Hat** (хотя проект open-source)

**Вывод:** Quarkus — сильный выбор для **новых** cloud-native проектов, но миграция уже работающего Spring-сервиса редко окупается — выигрыш по cold start обычно не перевешивает стоимость переписывания и переучивания.

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

- [Micronaut](micronaut-interview.md)
- [Vert.x](vertx-interview.md)
- [Spring AOP](../spring/spring-aop-interview.md)
- [Spring Batch](../spring/spring-batch-interview.md)
- [Spring Boot Actuator](../spring/spring-boot-actuator-interview.md)
