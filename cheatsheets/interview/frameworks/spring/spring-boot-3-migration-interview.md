---
title: "Вопросы на собеседовании: Spring Boot 3 Migration"
description: "Миграция на Spring Boot 3: Java 17 baseline, javax→jakarta, GraalVM Native, AOT, HTTP Interface Clients, Problem Details RFC 7807, Virtual Threads, breaking changes"
tags:
  - interview
  - spring
  - spring-boot-3-migration-interview
type: "interview"
difficulty: "intermediate"
aliases:
  - "Вопросы на собеседовании"
  - "Spring Boot 3 Migration"
  - "Spring Boot 3 собеседование"
  - "Spring Boot migration вопросы"
prerequisites:
  - "[[spring-boot-3-migration]]"
next: []
updated: 2026-05-31
---
# Вопросы на собеседовании: `Spring Boot 3 Migration`

`Spring Boot 3.0` (ноябрь 2022) — крупнейший релиз Spring Boot: Java 17 baseline, переход с `javax.*` на `jakarta.*`, поддержка GraalVM Native Image из коробки, новая Observability API. Часто спрашивается в интервью по актуальным Spring проектам.

Дата последнего обновления: 2026-04-20

## Полезные ссылки

### Официальная документация

- [Spring Boot 3 Migration Guide](https://github.com/spring-projects/spring-boot/wiki/Spring-Boot-3.0-Migration-Guide) — официальный гайд
- [Spring Framework 6 Migration](https://github.com/spring-projects/spring-framework/wiki/Upgrading-to-Spring-Framework-6.x) — гайд Spring Framework 6
- [Baeldung: Spring Boot 3](https://www.baeldung.com/spring-boot-3-migration) — практическое введение

## Содержание

- [Полезные ссылки](#полезные-ссылки)
- [See also](#see-also)

## Q1. Какие ключевые изменения в Spring Boot 3?

Spring Boot 3.0 (ноябрь 2022) — крупнейший релиз за всю историю: он поднял минимальную планку JDK и сменил всю пакетную базу, поэтому миграция на него редко бывает «обновил версию — и поехали». Главные изменения:

1. **Java 17 baseline** — минимально требуемая версия JDK теперь 17 (до этого — Java 8). Это позволило использовать records, sealed-классы и текстовые блоки внутри самого фреймворка.
2. **Jakarta EE 9+** — все enterprise-API переименованы с `javax.*` на `jakarta.*`. Самое массовое и болезненное изменение для прикладного кода (см. Q2).
3. **Spring Framework 6** — основа Spring Boot 3; добавил поддержку AOT, улучшил реактивность и Observability.
4. **GraalVM Native Image** — встроенная поддержка компиляции в нативный образ без сторонних плагинов.
5. **Observability** — единый API для метрик и трассировки на Micrometer + OpenTelemetry вместо Spring Cloud Sleuth.
6. **HTTP Interface Clients** — декларативный HTTP-клиент через `@HttpExchange` (альтернатива Feign).
7. **Problem Details (RFC 7807)** — стандартный формат JSON-ответа для ошибок REST.

Последующие версии добавляли возможности постепенно: 3.1 (май 2023), 3.2 (ноябрь 2023, поддержка Virtual Threads и `RestClient`), 3.3, 3.4.

## Q2. Что такое миграция с javax на jakarta и почему она нужна?

Это переименование всех enterprise-API из пространства имён `javax.*` в `jakarta.*` — не косметика, а вынужденный шаг из-за прав на торговую марку. В 2017 Oracle передала Java EE в Eclipse Foundation, но не отдала права на имя `javax`. Eclipse не имела права выпускать новые версии под старыми пакетами, поэтому всю платформу переименовала в **Jakarta EE**, а пакеты — в `jakarta.*`.

Для прикладного кода это означает массовую замену импортов:

```java
// ДО Spring Boot 3
import javax.persistence.Entity;
import javax.servlet.http.HttpServletRequest;
import javax.validation.constraints.NotNull;

// ПОСЛЕ Spring Boot 3
import jakarta.persistence.Entity;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.constraints.NotNull;
```

**Что затронуто:** JPA, Servlet API, JAX-RS, Bean Validation, JMS, Mail. Из-за этого подтягиваются и новые версии контейнеров и ORM, поддерживающих `jakarta.*`: Tomcat 10+, Jetty 11+, Hibernate 6+.

**Подводный камень:** старая библиотека, скомпилированная под `javax.servlet`, не запустится на Tomcat 10 — нужны версии с поддержкой Jakarta либо замена зависимости.

## Q3. Как выполнить миграцию с Spring Boot 2.7 на 3.x?

Ключевая идея — мигрировать поэтапно, а не одним прыжком: сначала подготовить почву (Java 17 и обновление до последней 2.7.x), потом автоматизировать рутину через OpenRewrite и только затем поднимать сам Spring Boot. Так каждый шаг можно прогнать тестами по отдельности и локализовать поломку.

```text
Последовательность:
1. Обновить JDK → Java 17+
2. Обновить зависимости до последних 2.7.x
3. Запустить OpenRewrite для автоматической миграции:
   mvn org.openrewrite.maven:rewrite-maven-plugin:run \
     -Drewrite.activeRecipes=org.openrewrite.java.spring.boot3.UpgradeSpringBoot_3_0
4. Обновить spring-boot-starter-parent до 3.x
5. Заменить javax.* → jakarta.* (кроме javax.sql.DataSource, javax.crypto)
6. Обновить библиотеки третьих сторон
7. Протестировать
```

```xml
<!-- pom.xml -->
<parent>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-parent</artifactId>
    <version>3.2.0</version>
</parent>

<properties>
    <java.version>17</java.version>
</properties>
```

**Почему сначала до последней 2.7.x:** в поздних 2.7.x уже есть deprecation-предупреждения о том, что меняется в 3.0. Сняв их на стабильной версии, вы заходите в 3.0 с меньшим числом сюрпризов.

## Q4. Какие javax-пакеты НЕ мигрировали на jakarta?

Не всё `javax.*` переехало в `jakarta.*` — мигрировали только API из Java EE (Enterprise Edition). Пакеты, которые входят в сам JDK (Java SE), остались под прежними именами, потому что их Oracle не передавала в Eclipse.

```text
ОСТАЛИСЬ javax.*:
- javax.sql.DataSource  (JDBC API — часть JDK)
- javax.crypto.*        (crypto — часть JDK)
- javax.net.*           (SSL/networking — часть JDK)
- javax.management.*    (JMX — часть JDK)
- javax.naming.*        (JNDI — часть JDK)

МИГРИРОВАЛИ на jakarta.*:
- jakarta.persistence.* (JPA)
- jakarta.servlet.*     (Servlet API)
- jakarta.ws.rs.*       (JAX-RS)
- jakarta.validation.*  (Bean Validation)
- jakarta.jms.*         (JMS)
- jakarta.mail.*        (Mail)
- jakarta.annotation.*  (@PostConstruct, @PreDestroy и др.)
```

**Эмпирическое правило:** если пакет относится к Java SE (JDK) — он остался `javax`; если к Java EE — стал `jakarta`. Поэтому при автозамене импортов нельзя слепо менять все `javax.*` на `jakarta.*` — `javax.sql.DataSource` сломается. OpenRewrite-рецепт это учитывает.

## Q5. Что такое HTTP Interface Clients в Spring 6?

Это встроенный в Spring Framework 6 способ описать вызов внешнего HTTP-API через обычный Java-интерфейс с аннотациями — Spring сам генерирует реализацию. По духу это аналог Feign, но без отдельной библиотеки: достаточно интерфейса и одной фабрики `HttpServiceProxyFactory`.

Метод объявляется аннотацией `@GetExchange`/`@PostExchange`, а параметры размечаются привычными `@PathVariable`, `@RequestBody`, `@RequestParam`. Поддерживаются как блокирующие (`Weather`), так и реактивные (`Flux<Forecast>`) типы возврата:

```java
// Интерфейс описывает API
interface WeatherClient {

    @GetExchange("/api/weather/{city}")
    Weather getWeather(@PathVariable String city);

    @PostExchange("/api/weather")
    WeatherReport reportWeather(@RequestBody WeatherData data);

    @GetExchange("/api/forecast")
    Flux<Forecast> getForecast(@RequestParam String city);  // реактивный
}

// Конфигурация через proxy
@Configuration
class HttpClientsConfig {

    @Bean
    WeatherClient weatherClient(WebClient.Builder builder) {
        WebClient client = builder.baseUrl("https://api.weather.com").build();
        HttpServiceProxyFactory factory = HttpServiceProxyFactory
            .builderFor(WebClientAdapter.create(client))
            .build();
        return factory.createClient(WeatherClient.class);
    }
}
```

Адаптер определяет транспорт: `WebClientAdapter` оборачивает реактивный `WebClient`, а для чисто блокирующего кода есть `RestClientAdapter` (Spring 6.1+) поверх `RestClient` — без затягивания реактивного стека:

```java
RestClient client = RestClient.create("https://api.weather.com");
WeatherClient weatherClient = HttpServiceProxyFactory
    .builderFor(RestClientAdapter.create(client))
    .build()
    .createClient(WeatherClient.class);
```

## Q6. Что такое Problem Details (RFC 7807) в Spring Boot 3?

Это стандартизированный формат тела ответа об ошибке для REST API (RFC 7807). Вместо самописной структуры JSON у каждого сервиса — единый набор полей, понятный клиентам и инструментам. Spring Boot 3 поддерживает его из коробки через тип `ProblemDetail`.

**Поля стандарта:** `type` (URI-идентификатор типа ошибки), `title` (краткое человекочитаемое название), `status` (HTTP-код), `detail` (подробности конкретного случая), `instance` (URI этого экземпляра ошибки).

```json
{
  "type": "https://example.com/errors/insufficient-funds",
  "title": "Insufficient Funds",
  "status": 402,
  "detail": "Your account balance is $10, but the purchase requires $100",
  "instance": "/orders/12345/purchases/789"
}
```

```java
// Включение Problem Details
@Configuration
public class WebConfig {
    @Bean
    public ErrorHandler errorHandler() {
        // По умолчанию включено в Spring Boot 3
    }
}

// Кастомизация ответа
@RestControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler(InsufficientFundsException.class)
    public ProblemDetail handleInsufficientFunds(InsufficientFundsException ex) {
        ProblemDetail problem = ProblemDetail.forStatusAndDetail(
            HttpStatus.PAYMENT_REQUIRED,
            ex.getMessage()
        );
        problem.setType(URI.create("https://example.com/errors/insufficient-funds"));
        problem.setTitle("Insufficient Funds");
        problem.setProperty("accountBalance", ex.getBalance());
        return problem;
    }
}
```

Кастомный обработчик строит `ProblemDetail` через `forStatusAndDetail`, а в `setProperty` можно положить любые доменные поля (`accountBalance`) сверх стандартных. Ответ отдаётся с Content-Type `application/problem+json` — по нему клиент понимает, что перед ним именно Problem Details, а не обычный JSON.

## Q7. Что такое GraalVM Native Image и как Spring Boot 3 его поддерживает?

**GraalVM Native Image** компилирует приложение в самостоятельный нативный исполняемый файл ещё до запуска (AOT, ahead-of-time), без JVM в рантайме. На выходе — бинарник, который стартует почти мгновенно и потребляет в разы меньше памяти, что особенно ценно для serverless и быстрого масштабирования.

**Что даёт:**
- Startup ~100ms (против 2-5 с у JVM) — нет прогрева JIT.
- Memory ~30% от потребления на JVM.
- Размер образа ~80MB (меньше при сжатии upx).

**Чем платим (ограничения AOT):** всё, что нельзя проанализировать статически на этапе сборки, ломается — reflection, динамическая загрузка классов, `Unsafe`. Поэтому такие места нужно явно описывать через hints (см. ниже).

Spring Boot 3 берёт на себя бóльшую часть подсказок автоматически через AOT-обработку (см. Q8), а сборка делается одним Maven-плагином:

```xml
<!-- Native Image plugin -->
<plugin>
    <groupId>org.graalvm.buildtools</groupId>
    <artifactId>native-maven-plugin</artifactId>
</plugin>
```

```bash
# Сборка native image
./mvnw -Pnative native:compile

# Запуск
./target/myapp
```

Если ваш код использует reflection, который Spring не видит сам (например, своя сериализация DTO), компилятору нужно дать **hints** — иначе класс не попадёт в нативный образ и в рантайме упадёт. Делается это аннотациями `@RegisterReflection*` либо программно через `RuntimeHintsRegistrar`:

```java
// Подсказки компилятору для reflection
@RegisterReflectionForBinding(MyDto.class)
@RegisterReflection(classes = MyDto.class)
public class MyConfig { }

// Или через @ImportRuntimeHints
@Component
@ImportRuntimeHints(MyHints.class)
public class MyService { ... }

public class MyHints implements RuntimeHintsRegistrar {
    @Override
    public void registerHints(RuntimeHints hints, ClassLoader loader) {
        hints.reflection().registerType(MyDto.class,
            MemberCategory.INVOKE_DECLARED_CONSTRUCTORS,
            MemberCategory.DECLARED_FIELDS);
    }
}
```

## Q8. Как работает AOT processing в Spring Boot 3?

**AOT (Ahead-of-Time) processing** — это анализ контекста приложения на этапе сборки с генерацией готового кода вместо рантайм-рефлексии. Обычно Spring при старте сканирует classpath, разбирает аннотации и строит определения бинов «на лету». AOT выполняет эту работу заранее и записывает результат в сгенерированный код, поэтому при старте остаётся только его выполнить.

Это фундамент для GraalVM (где рефлексия ограничена), но даёт ускорение и на обычной JVM. Что именно генерирует AOT-процессор:

```java
// AOT процессор генерирует:
// 1. Pre-computed bean definitions (без рефлексии)
// 2. Optimized configuration classes
// 3. Reflection hints
// 4. Resource hints (application.yml)
// 5. Proxy hints
```

```bash
# Включить AOT processing в обычном JAR (без native)
./mvnw spring-boot:process-aot

# Использовать сгенерированные артефакты
java -Dspring.aot.enabled=true -jar app.jar
```

**Преимущество:** запуск на 30-50% быстрее даже без GraalVM native image — за счёт того, что определения бинов уже посчитаны, а рефлексия на старте почти не используется.

**Подводный камень:** AOT фиксирует структуру контекста на этапе сборки, поэтому конфигурация, зависящая от рантайм-условий (например, профили, выбираемые при запуске), может вести себя иначе — тестировать сборку нужно в том же режиме, что и прод.

## Q9. Какие изменения в Observability?

Главное изменение — единый API для метрик и трассировки на базе Micrometer, который заменил разрозненные решения Spring Boot 2 (Micrometer для метрик + Spring Cloud Sleuth для трассировки). Теперь одно наблюдение (`Observation`) одновременно создаёт и span для трейсинга, и таймер-метрику — описывать дважды не нужно.

Подключение трассировки — это bridge на конкретный бэкенд (здесь OpenTelemetry) плюс экспортёр (здесь Zipkin):

```xml
<dependency>
    <groupId>io.micrometer</groupId>
    <artifactId>micrometer-tracing-bridge-otel</artifactId>
</dependency>
<dependency>
    <groupId>io.opentelemetry</groupId>
    <artifactId>opentelemetry-exporter-zipkin</artifactId>
</dependency>
```

```java
// Observation API вместо Spring Cloud Sleuth
@Service
class OrderService {

    private final ObservationRegistry registry;

    public Order process(OrderCommand cmd) {
        return Observation.createNotStarted("order.process", registry)
            .contextualName("process-order")
            .lowCardinalityKeyValue("type", cmd.type())
            .observe(() -> {
                // этот блок трассируется и замеряется
                return doProcess(cmd);
            });
    }
}
```

```yaml
management:
  tracing:
    sampling:
      probability: 1.0   # 100% sampling в dev
  zipkin:
    tracing:
      endpoint: http://localhost:9411/api/v2/spans
```

В коде `Observation.observe(...)` оборачивает блок: внутри него автоматически создаётся span и замеряется длительность, а `lowCardinalityKeyValue` добавляет теги с малым числом значений (они безопасны для метрик, в отличие от high-cardinality вроде userId).

**Важно при миграции:** старый Spring Cloud Sleuth удалён, его нужно заменить на `micrometer-tracing`.

## Q10. Что нужно знать о поддержке Virtual Threads в Spring Boot 3.2+?

Spring Boot 3.2 добавил встроенную поддержку Java 21 Virtual Threads — лёгких потоков, которыми JVM управляет сама, не привязывая каждый к ОС-потоку. Это позволяет держать тысячи одновременных блокирующих запросов на горстке реальных потоков, сохраняя простой императивный стиль кода (без перехода на реактивный стек).

Включается одним флагом, после чего virtual threads используют Tomcat (по потоку на запрос), `@Async` и `@Scheduled`:

```yaml
spring:
  threads:
    virtual:
      enabled: true   # включить VT для Tomcat, @Async, @Scheduled
```

```java
// @Async теперь использует Virtual Thread
@Service
class OrderService {
    @Async
    public CompletableFuture<Order> processAsync(Order o) { ... }
}

// Tomcat executor
@Bean
public TomcatProtocolHandlerCustomizer<?> protocolHandlerVirtualThreadExecutorCustomizer() {
    return protocolHandler -> protocolHandler.setExecutor(
        Executors.newVirtualThreadPerTaskExecutor()
    );
}
```

**Когда применять:** Virtual Threads дают выигрыш на I/O-bound нагрузке (много времени тратится на ожидание БД, сети, диска) — пока поток ждёт, JVM освобождает несущий ОС-поток под другую работу. На CPU-bound задачах выигрыша нет: реальные ядра всё равно ограничены, лёгкие потоки не добавляют вычислительной мощности.

## Q11. Какие breaking changes в Spring Security 6?

Главный breaking change — удалён `WebSecurityConfigurerAdapter`: теперь конфигурация задаётся не наследованием от базового класса, а регистрацией бина `SecurityFilterChain`. Заодно переименованы методы DSL и весь стиль настройки переведён на лямбды.

Сравнение старого и нового подхода:

```java
// ДО (Spring Security 5, SB 2.x)
@EnableWebSecurity
class SecurityConfig extends WebSecurityConfigurerAdapter {
    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.authorizeRequests()
            .antMatchers("/public").permitAll()
            .anyRequest().authenticated()
            .and()
            .formLogin();
    }
}

// ПОСЛЕ (Spring Security 6, SB 3.x) — WebSecurityConfigurerAdapter удалён
@Configuration
@EnableWebSecurity
class SecurityConfig {

    @Bean
    SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .authorizeHttpRequests(auth -> auth        // authorizeHttpRequests вместо authorizeRequests
                .requestMatchers("/public").permitAll()  // requestMatchers вместо antMatchers
                .anyRequest().authenticated())
            .formLogin(Customizer.withDefaults());
        return http.build();
    }
}
```

Ключевые изменения:
- `WebSecurityConfigurerAdapter` удалён → конфигурация через бин `SecurityFilterChain`.
- `authorizeRequests` → `authorizeHttpRequests` (новый, более производительный механизм авторизации).
- `antMatchers`/`mvcMatchers` → единый `requestMatchers`.
- Все настройки задаются лямбдой `Customizer`, цепочки через `.and()` больше не нужны.

Почему так: переход с наследования на бин делает конфигурацию композируемой — можно объявить несколько `SecurityFilterChain` для разных URL и управлять их порядком, что с единственным `configure()`-методом было неудобно.

## Q12. Что такое декларативный RestClient?

`RestClient` — новый синхронный HTTP-клиент из Spring Boot 3.2 с цепочечным (fluent) API. Он пришёл на смену устаревающему `RestTemplate`: даёт тот же блокирующий стиль, но с современным API, как у `WebClient`, без необходимости тянуть реактивный стек.

Типичные операции читаются как одна цепочка вызовов — выбор метода, URI, тела и извлечение результата:

```java
// Создание
RestClient client = RestClient.create("https://api.example.com");

// GET
Order order = client.get()
    .uri("/orders/{id}", 123)
    .retrieve()
    .body(Order.class);

// POST
OrderResponse response = client.post()
    .uri("/orders")
    .contentType(MediaType.APPLICATION_JSON)
    .body(new CreateOrderRequest(...))
    .retrieve()
    .body(OrderResponse.class);

// Обработка ошибок
try {
    client.get().uri("/notfound").retrieve().body(Order.class);
} catch (HttpClientErrorException.NotFound ex) {
    log.error("Not found", ex);
}

// Reactive bridge
Mono<Order> orderMono = client.get()
    .uri("/orders/{id}", 123)
    .exchange((request, response) -> Mono.just(response.bodyTo(Order.class)));
```

**RestClient vs WebClient (Компромисс):** `RestClient` — блокирующий и проще в отладке, подходит для обычных синхронных сервисов; `WebClient` — реактивный и сложнее, оправдан там, где уже используется WebFlux или нужен неблокирующий стек. Обработка ошибок у `RestClient` — через привычные исключения (`HttpClientErrorException.NotFound`).

## Q13. Какие изменения в auto-configuration?

Изменился способ регистрации авто-конфигураций: вместо ключа в `META-INF/spring.factories` они объявляются в отдельном файле `AutoConfiguration.imports`, где каждый класс пишется на своей строке. Это убрало парсинг тяжёлого общего `spring.factories` ради одной записи и ускорило старт.

```java
// ДО Spring Boot 2.x — META-INF/spring.factories
org.springframework.boot.autoconfigure.EnableAutoConfiguration=\
    com.example.MyAutoConfiguration

// ПОСЛЕ Spring Boot 3.x — META-INF/spring/org.springframework.boot.autoconfigure.AutoConfiguration.imports
com.example.MyAutoConfiguration
```

Старый `spring.factories` всё ещё поддерживается для обратной совместимости, но для новых стартеров используют новый файл. Дополнительно появилась аннотация-маркер `@AutoConfiguration` — она заменяет связку `@Configuration` + порядковых аннотаций и явно помечает класс именно как авто-конфигурацию:

```java
// Также добавлено @AutoConfiguration — marker аннотация
@AutoConfiguration
@ConditionalOnClass(DataSource.class)
public class MyDataSourceAutoConfiguration { ... }
```

## Q14. Что такое Configuration Properties Migrator?

Это вспомогательная зависимость, которая на старте находит в вашей конфигурации устаревшие или переименованные `application.properties`/`yml` и пишет в лог, на что их заменить. Многие свойства в Spring Boot 3 переехали (особенно в Actuator), и без миграции они просто молча игнорируются — приложение запустится, но настройка не применится. Migrator делает эти случаи видимыми.

Подключается с `runtime`-областью:

```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-properties-migrator</artifactId>
    <scope>runtime</scope>
</dependency>
```

При запуске приложения в логах появятся предупреждения об устаревших или переименованных properties с указанием нового имени:

```text
The following properties have been renamed:
  management.metrics.export.prometheus.enabled → 
  management.prometheus.metrics.export.enabled
```

**Важно:** это инструмент только на время миграции — после правки всех свойств зависимость нужно удалить, чтобы не тащить её в прод.

## Q15. Какие проблемы часто возникают при миграции?

Большинство граблей при переходе на Spring Boot 3 — это не сам Spring, а его окружение: сторонние библиотеки под `javax`, устаревшие версии ORM и переименованные настройки. Типичный список:

1. **Несовместимость библиотек с Jakarta EE.** Старая зависимость скомпилирована под `javax.servlet` и падает в рантайме на новом контейнере:
```text
Error: NoClassDefFoundError: javax/servlet/http/HttpServletRequest
→ Решение: обновить до версии с Jakarta-поддержкой или заменить зависимость
```

2. **Устаревшие версии библиотек.** Многие требуют апгрейда вместе со Spring Boot, иногда с собственными breaking changes:
```text
- Hibernate 5.x → 6.x (breaking changes в HQL, SQL dialects)
- Spring Cloud Sleuth → Micrometer Tracing
- springfox-swagger → springdoc-openapi
- Lombok до 1.18.24+ (для Java 17 compatibility)
```

3. **Breaking changes в Spring Security 6** — удалён `WebSecurityConfigurerAdapter` (см. Q11).

4. **Переименование Actuator endpoints** — настройки экспорта метрик переехали:
```text
management.metrics.export.*  → management.prometheus.*
и т.п.
```

5. **Обработка маршрутов WebMVC / WebFlux** — `antMatchers` стал `requestMatchers`, а `PathPatternParser` теперь применяется по умолчанию:
```java
// antMatchers → requestMatchers
// pathPatternsParser теперь default
```

6. **Jackson 2.14+** — более строгая обработка типов; код, полагавшийся на мягкую десериализацию, может начать падать.

7. **Изменились дефолты HikariCP** — таймауты стали короче, из-за чего ранее «проходившие» медленные соединения могут отваливаться.

**Рекомендация:** разбить миграцию на этапы — сначала Java 17, потом зависимости до последних 2.x, затем 3.0, затем 3.x++. Каждый этап отдельно прогоняется тестами, и поломку видно сразу.

## See also

- [Spring Boot](spring-boot-interview.md) — основы Spring Boot, auto-configuration
- [Spring Framework](spring-framework-interview.md) — Spring Framework 6 changes
- [Spring Security](spring-security-interview.md) — breaking changes в Spring Security 6
- [Java Virtual Threads](../../programming-languages/java/java-virtual-threads-interview.md) — поддержка в Spring Boot 3.2+
- [Java 17-21](../../programming-languages/java/java-17-21-interview.md) — Java 17 baseline, records, sealed classes
- [GraalVM Native Image](../../jvm/graalvm-native-interview.md) — first-class native support в Spring Boot 3
- [Spring REST Client](spring-rest-client-interview.md) — RestClient — новый декларативный API
- [Micrometer](../../monitoring/micrometer-interview.md) — новая Observation API в Spring 6
- [Spring Boot Actuator](spring-boot-actuator-interview.md) — изменения в Actuator endpoints
- [Spring WebFlux](spring-webflux-interview.md) — изменения в WebFlux 6
