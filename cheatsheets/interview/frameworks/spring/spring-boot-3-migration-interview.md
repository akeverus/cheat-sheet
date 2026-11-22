---
title: "Вопросы на собеседовании: Миграция на Spring Boot 3"
description: "Миграция на Spring Boot 3: Java 17 baseline, javax→jakarta, GraalVM Native, AOT, HTTP Interface Clients, Problem Details RFC 9457, Virtual Threads, breaking changes"
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
# Вопросы на собеседовании: Миграция на Spring Boot 3

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
2. **Jakarta EE 10** — Boot 3.0 обновил управляемые им Jakarta EE спецификации до уровня EE 10. Затронутые API используют `jakarta.*`. Версию отдельной спецификации сверяют с выбранным контейнером и версией Boot (Q2).
3. **Spring Framework 6** — основа Spring Boot 3; добавил поддержку AOT, улучшил реактивность и Observability.
4. **GraalVM Native Image** — Spring AOT готовит код, ресурсы и hints, а GraalVM отдельно компилирует приложение в нативный образ. Сборка требует подходящих инструментов (Q7–Q8).
5. **Observability** — Micrometer Observation передаёт события наблюдения настроенным обработчикам метрик и трассировки. OpenTelemetry — один из вариантов tracing bridge (Q9).
6. **HTTP Interface Clients** — декларативный HTTP-клиент через `@HttpExchange` (альтернатива Feign).
7. **Problem Details (RFC 9457, бывший 7807)** — стандартный формат JSON-ответа для ошибок REST.

Последующие версии добавляли возможности постепенно: 3.1 (май 2023), 3.2 (ноябрь 2023, поддержка Virtual Threads и `RestClient`), 3.3, 3.4.

## Q2. Что такое миграция с javax на jakarta и почему она нужна?

В Jakarta EE 9 затронутые API перешли с `javax.*` на `jakarta.*`. После передачи Java EE фонду Eclipse соглашение отдельно ограничивало изменения в `javax.*` для развиваемых спецификаций и использование торговых марок Java. Пакеты Java SE, например `javax.sql`, сохраняют имена. [Eclipse Foundation: The Jakarta EE Future](https://www.eclipse.org/community/eclipse_newsletter/2019/may/jakartafuture.php).

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

Переход затронул, среди прочего, Persistence, Servlet, RESTful Web Services, Validation, Messaging и Mail. Boot 3.0 управлял соответствующими спецификациями уровня Jakarta EE 10, но поддержка отдельных версий зависит от контейнера. Например, Tomcat 10.1 реализует Servlet 6.0, а Jetty 11 — Servlet 5.0. [Boot 3.0 Migration Guide](https://github.com/spring-projects/spring-boot/wiki/Spring-Boot-3.0-Migration-Guide).

**Подводный камень:** старая библиотека, скомпилированная под `javax.servlet`, не запустится на Tomcat 10 — нужны версии с поддержкой Jakarta либо замена зависимости.

## Q3. Как выполнить миграцию с Spring Boot 2.7 на 3.x?

Сначала перейдите на Java 17 и последнюю доступную версию 2.7.x, устраните предупреждения и проверьте тесты. Затем обновите Boot и совместимые зависимости вручную либо примените рецепт OpenRewrite и проверьте его diff.

```text
Последовательность:
1. Обновить JDK → Java 17+
2. Обновить зависимости до последних 2.7.x
3. Перейти на Boot 3.0 вручную или запустить настроенный рецепт
   org.openrewrite.java.spring.boot3.UpgradeSpringBoot_3_0
4. Проверить parent или BOM, версии зависимостей и импорты затронутых API Jakarta EE
5. Обновить несовместимые библиотеки и прогнать тесты
6. Отдельно перейти на выбранную более позднюю ветку 3.x
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

Рецепт `UpgradeSpringBoot_3_0` уже обновляет версии Boot и может изменить Maven parent. Если он это сделал, повторять вручную тот же шаг не нужно. [OpenRewrite UpgradeSpringBoot_3_0](https://docs.openrewrite.org/recipes/java/spring/boot3/upgradespringboot_3_0-community-edition/).

## Q4. Какие javax-пакеты НЕ мигрировали на jakarta?

Переименовывают импорты затронутых API Jakarta EE. Пакеты Java SE из JDK, например JDBC и криптография, сохраняют прежние имена.

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
interface BlockingWeatherClient {
    @GetExchange("/api/weather/{city}")
    Weather getWeather(@PathVariable String city);

    @GetExchange("/api/forecast")
    List<Forecast> getForecast(@RequestParam String city);
}

RestClient client = RestClient.create("https://api.weather.com");
BlockingWeatherClient weatherClient = HttpServiceProxyFactory
    .builderFor(RestClientAdapter.create(client))
    .build()
    .createClient(BlockingWeatherClient.class);
```

## Q6. Что такое Problem Details (RFC 7807) в Spring Boot 3?

Это стандартизированный формат тела ответа об ошибке для REST API: изначально RFC 7807, с 2023 года — заменивший его RFC 9457 с тем же набором полей. Вместо самописной структуры JSON у каждого сервиса — единый набор полей, понятный клиентам и инструментам. Spring Boot 3 поддерживает его из коробки через тип `ProblemDetail`.

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

```yaml
# Включение Problem Details для MVC-ошибок — по умолчанию ВЫключено
spring:
  mvc:
    problemdetails:
      enabled: true   # для WebFlux — spring.webflux.problemdetails.enabled
```

```java
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

GraalVM Native Image создаёт бинарник для целевой платформы. Изменения времени старта, RSS и размера артефакта зависят от приложения и измеряются отдельно.

Spring AOT подготавливает код, ресурсы и hints. GraalVM затем компилирует образ в модели закрытого мира. Hints описывают динамические операции, например доступ к рефлексии и ресурсам. Они не снимают ограничения на динамическую загрузку классов.

Для Maven-примера нужны профиль `native`, Native Build Tools и настроенный GraalVM toolchain для целевой платформы. [Spring Boot 3.5 Native Image Build](https://docs.spring.io/spring-boot/3.5/how-to/native-image/developing-your-first-application.html).

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

Для динамических операций, которые Spring не может вывести сам, зарегистрируйте нужные hints. Например, укажите операции рефлексии для DTO через `@RegisterReflectionForBinding` или `RuntimeHintsRegistrar`:

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

Spring AOT анализирует определения бинов при сборке и генерирует код инициализации, ресурсы и runtime hints. Эти артефакты используются при сборке Native Image или в отдельном JVM-режиме. Эффект на время запуска зависит от приложения.

```bash
# Maven со spring-boot-starter-parent и spring-boot-maven-plugin:
# профиль native запускает process-aot, package включает результат в JAR
./mvnw -Pnative package
java -Dspring.aot.enabled=true -jar target/myapp.jar
```

[Spring AOT](https://docs.spring.io/spring-framework/reference/core/aot.html) · [Spring Boot Native Image Build](https://docs.spring.io/spring-boot/3.5/how-to/native-image/developing-your-first-application.html).

**Подводный камень:** AOT фиксирует структуру контекста на этапе сборки, поэтому конфигурация, зависящая от рантайм-условий (например, профили, выбираемые при запуске), может вести себя иначе — тестировать сборку нужно в том же режиме, что и прод.

## Q9. Какие изменения в Observability?

Micrometer Observation описывает одно наблюдение, которое могут обработать зарегистрированные `ObservationHandler`. Обработчик метрик может записать таймер, а обработчик трассировки — создать span. Для трассировки можно выбрать мост на OpenTelemetry или Brave.

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

`observe(...)` задаёт границы наблюдения. Метрики и spans зависят от подключённых handlers, а отправка span — от настроек трассировки и sampling. Теги метрик ограничивают по кардинальности. [Spring Boot Tracing](https://docs.spring.io/spring-boot/3.5/reference/actuator/tracing.html).

Spring Cloud Sleuth не поддерживает Boot 3.x. При миграции используйте Micrometer Tracing, если приложению нужна распределённая трассировка.

## Q10. Что нужно знать о поддержке Virtual Threads в Spring Boot 3.2+?

Spring Boot 3.2 на Java 21+ включает виртуальные потоки для поддерживаемых автоконфигураций по настройке:

```yaml
spring:
  threads:
    virtual:
      enabled: true
  main:
    keep-alive: true
```

При стандартной автоконфигурации Boot 3.2 использует виртуальные потоки для подходящего встроенного сервера и создаёт `SimpleAsyncTaskExecutor` и `SimpleAsyncTaskScheduler` с виртуальными потоками. Собственный executor, scheduler или `AsyncConfigurer` может изменить выбор. Сам флаг не включает обработку аннотаций. Для `@Async` и `@Scheduled` обычно нужны `@EnableAsync` и `@EnableScheduling` соответственно.

Виртуальные потоки — daemon-потоки. `spring.main.keep-alive=true` полезно, если иначе не останется обычного потока, удерживающего JVM, например в приложении только с планировщиком. При блокирующем I/O оценивайте пропускную способность на своей нагрузке. Pinning зависит от JDK и операции. Например, блокировка внутри `synchronized` могла привязать виртуальный поток к несущему в Java 21, а JDK 24 устранил этот источник pinning. [Boot 3.2 Virtual Threads](https://docs.spring.io/spring-boot/docs/3.2.3/reference/html/features.html#features.spring-application.virtual-threads) · [Boot 3.2 Task Execution and Scheduling](https://docs.spring.io/spring-boot/docs/3.2.7/reference/html/features.html) · [JEP 491](https://openjdk.org/jeps/491).

## Q11. Какие breaking changes в Spring Security 6?

В Spring Security 6 удалены `WebSecurityConfigurerAdapter` и прежние методы выбора matcher, включая `antMatchers` и `mvcMatchers`. Современный вариант задаёт `SecurityFilterChain` и использует `requestMatchers` с Lambda DSL.

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
- В Security 6 используйте `authorizeHttpRequests` для миграции с `authorizeRequests`.
- `antMatchers`/`mvcMatchers` → единый `requestMatchers`.
- Lambda DSL рекомендуется в Security 6 и обязателен в Security 7. В 7 удалены `.and()` и `authorizeRequests`. [Security 7.0 Changes](https://docs.spring.io/spring-security/reference/7.0/whats-new.html).

Почему так: переход с наследования на бин делает конфигурацию композируемой — можно объявить несколько `SecurityFilterChain` для разных URL и управлять их порядком, что с единственным `configure()`-методом было неудобно.

## Q12. Что такое декларативный RestClient?

`RestClient` — синхронный HTTP-клиент с fluent API из Spring Framework 6.1. Spring Boot 3.2 добавил интеграцию, включая автоконфигурируемый builder. `@HttpExchange` описывает декларативный интерфейс, который может работать поверх `RestClient` через адаптер (Q5). `RestTemplate` — более ранний блокирующий API ветки Framework 6.x.

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

```

`RestClient` выполняет блокирующие вызовы. Обёртка результата `exchange` в `Mono.just(...)` не переносит работу на другой поток и не делает I/O неблокирующим. Для неблокирующего HTTP-клиента используйте `WebClient`. [Spring Framework 6.2 REST Clients](https://docs.spring.io/spring-framework/reference/6.2/integration/rest-clients.html).

## Q13. Какие изменения в auto-configuration?

Boot 2.7 добавил `@AutoConfiguration` и регистрацию в `AutoConfiguration.imports`. Формат перечисляет по одному классу на строку.

```java
// Формат до Spring Boot 2.7 — META-INF/spring.factories
org.springframework.boot.autoconfigure.EnableAutoConfiguration=\
    com.example.MyAutoConfiguration

// ПОСЛЕ Spring Boot 3.x — META-INF/spring/org.springframework.boot.autoconfigure.AutoConfiguration.imports
com.example.MyAutoConfiguration
```

Регистрация авто-конфигураций через `spring.factories` в Spring Boot 3.0 больше **не поддерживается** — работает только новый файл (переходный период был в 2.7, где принимались оба). Сам `spring.factories` жив для других ключей: listeners, initializers, environment post-processors. `@AutoConfiguration` помечает класс как автоконфигурацию и позволяет задать её порядок:

```java
// Также добавлено @AutoConfiguration — marker аннотация
@AutoConfiguration
@ConditionalOnClass(DataSource.class)
public class MyDataSourceAutoConfiguration { ... }
```

## Q14. Что такое Configuration Properties Migrator?

`spring-boot-properties-migrator` анализирует свойства при запуске, сообщает об устаревших именах и временно подставляет новые имена для поддерживаемых переименований. [Boot 3.0 Migration Guide](https://github.com/spring-projects/spring-boot/wiki/Spring-Boot-3.0-Migration-Guide).

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

4. **Переименование свойств экспорта метрик.** Например, `management.metrics.export.prometheus.enabled` стало `management.prometheus.metrics.export.enabled` (Q14).
5. **MVC-маршрутизация.** Отдельно проверьте правила сопоставления путей. В Framework 6 по умолчанию отключено необязательное совпадение завершающего слэша. Правила выбора matcher Spring Security мигрируют отдельно (Q11). [Boot 3.0 Migration Guide](https://github.com/spring-projects/spring-boot/wiki/Spring-Boot-3.0-Migration-Guide).

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
