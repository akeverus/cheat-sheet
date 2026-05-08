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
updated: "2026-04-25"
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

**Spring Boot 3.0 (ноябрь 2022)** — крупнейший релиз за последние годы:

1. **Java 17 baseline** — минимальная версия (до этого — Java 8).
2. **Jakarta EE 9+** — миграция с `javax.*` на `jakarta.*`.
3. **Spring Framework 6** — улучшения реактивности, AOT.
4. **GraalVM Native Image** — первоклассная поддержка.
5. **Observability** — Micrometer + OpenTelemetry по умолчанию.
6. **HTTP Interface Clients** — декларативный HTTP-клиент (`@HttpExchange`).
7. **Problem Details (RFC 7807)** — стандарт для REST error responses.

Последующие версии: 3.1 (май 2023), 3.2 (ноябрь 2023, Virtual Threads), 3.3, 3.4.


> [!mcq]
> - [x] Правильный ответ | Корректное описание концепции с конкретным механизмом и use-case.
> - [ ] Альтернативное решение которое не подходит | Почему ошибка в этом подходе ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.
> - [ ] Другая альтернатива с критическим недостатком | Это смежное, но отличное понятие ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.
> - [ ] Третий вариант который не работает в production | Противоположное направление## Q2. Что такое миграция с javax на jakarta и почему она нужна? ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.

В 2017 Oracle передала Java EE в Eclipse Foundation. Eclipse не смогла сохранить `javax.*` пакеты из-за trademark. Результат: вся платформа переименована в **Jakarta EE** с пакетами `jakarta.*`.

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

Затронутые библиотеки: JPA, Servlet API, JAX-RS, Bean Validation, JMS, Mail. Tomcat 10+, Jetty 11+, Hibernate 6+.


> [!mcq]
> - [x] Правильный ответ | Корректное описание концепции с конкретным механизмом и use-case.
> - [ ] Альтернативное решение которое не подходит | Почему ошибка в этом подходе ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.
> - [ ] Другая альтернатива с критическим недостатком | Это смежное, но отличное понятие ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.
> - [ ] Третий вариант который не работает в production | Противоположное направление## Q3. Как выполнить миграцию с Spring Boot 2.7 на 3.x? ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.

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


> [!mcq]
> - [x] Правильный ответ | Корректное описание концепции с конкретным механизмом и use-case.
> - [ ] Альтернативное решение которое не подходит | Почему ошибка в этом подходе ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.
> - [ ] Другая альтернатива с критическим недостатком | Это смежное, но отличное понятие ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.
> - [ ] Третий вариант который не работает в production | Противоположное направление## Q4. Какие javax-пакеты НЕ мигрировали на jakarta? ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.

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

Простое правило: **если пакет относится к Java SE (JDK) — остался javax; Java EE → jakarta**.


> [!mcq]
> - [x] Правильный ответ | Корректное описание концепции с конкретным механизмом и use-case.
> - [ ] Альтернативное решение которое не подходит | Почему ошибка в этом подходе ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.
> - [ ] Другая альтернатива с критическим недостатком | Это смежное, но отличное понятие ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.
> - [ ] Третий вариант который не работает в production | Противоположное направление## Q5. Что такое HTTP Interface Clients в Spring 6? ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.

Декларативный HTTP-клиент (аналог Feign), встроенный в Spring Framework 6:

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

Для блокирующего кода — `RestClientAdapter` (Spring 6.1+):

```java
RestClient client = RestClient.create("https://api.weather.com");
WeatherClient weatherClient = HttpServiceProxyFactory
    .builderFor(RestClientAdapter.create(client))
    .build()
    .createClient(WeatherClient.class);
```


> [!mcq]
> - [x] Правильный ответ | Корректное описание концепции с конкретным механизмом и use-case.
> - [ ] Альтернативное решение которое не подходит | Почему ошибка в этом подходе ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.
> - [ ] Другая альтернатива с критическим недостатком | Это смежное, но отличное понятие ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.
> - [ ] Третий вариант который не работает в production | Противоположное направление## Q6. Что такое Problem Details (RFC 7807) в Spring Boot 3? ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.

Стандарт для JSON error responses в REST API:

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

Content-Type: `application/problem+json`.


> [!mcq]
> - [x] Правильный ответ | Корректное описание концепции с конкретным механизмом и use-case.
> - [ ] Альтернативное решение которое не подходит | Почему ошибка в этом подходе ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.
> - [ ] Другая альтернатива с критическим недостатком | Это смежное, но отличное понятие ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.
> - [ ] Третий вариант который не работает в production | Противоположное направление## Q7. Что такое GraalVM Native Image и как Spring Boot 3 его поддерживает? ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.

**GraalVM Native Image** — AOT (ahead-of-time) компиляция JVM приложения в нативный executable:
- Startup ~100ms (vs 2-5s для JVM)
- Memory ~30% JVM
- Размер ~80MB (smaller with upx)
- Ограничения: reflection, динамическая загрузка классов, unsafe

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


> [!mcq]
> - [x] Правильный ответ | Корректное описание концепции с конкретным механизмом и use-case.
> - [ ] Альтернативное решение которое не подходит | Почему ошибка в этом подходе ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.
> - [ ] Другая альтернатива с критическим недостатком | Это смежное, но отличное понятие ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.
> - [ ] Третий вариант который не работает в production | Противоположное направление## Q8. Как работает AOT processing в Spring Boot 3? ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.

**AOT (Ahead-of-Time)** — анализ приложения на этапе сборки и генерация дополнительного кода для ускорения старта (особенно для GraalVM).

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

Преимущество: запуск на 30-50% быстрее даже без GraalVM native image.


> [!mcq]
> - [x] Правильный ответ | Корректное описание концепции с конкретным механизмом и use-case.
> - [ ] Альтернативное решение которое не подходит | Почему ошибка в этом подходе ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.
> - [ ] Другая альтернатива с критическим недостатком | Это смежное, но отличное понятие ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.
> - [ ] Третий вариант который не работает в production | Противоположное направление## Q9. Какие изменения в Observability? ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.

Spring Boot 3 предоставляет единый API для metrics + tracing через Micrometer:

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

Старый Sleuth удалён — мигрировать на `micrometer-tracing`.


> [!mcq]
> - [x] Правильный ответ | Корректное описание концепции с конкретным механизмом и use-case.
> - [ ] Альтернативное решение которое не подходит | Почему ошибка в этом подходе ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.
> - [ ] Другая альтернатива с критическим недостатком | Это смежное, но отличное понятие ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.
> - [ ] Третий вариант который не работает в production | Противоположное направление## Q10. Что нужно знать о поддержке Virtual Threads в Spring Boot 3.2+? ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.

Spring Boot 3.2 добавил первоклассную поддержку Java 21 Virtual Threads:

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

**Важно**: Virtual Threads помогают для I/O-bound workloads. CPU-bound — не даёт выигрыша.


> [!mcq]
> - [x] Правильный ответ | Корректное описание концепции с конкретным механизмом и use-case.
> - [ ] Альтернативное решение которое не подходит | Почему ошибка в этом подходе ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.
> - [ ] Другая альтернатива с критическим недостатком | Это смежное, но отличное понятие ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.
> - [ ] Третий вариант который не работает в production | Противоположное направление## Q11. Какие breaking changes в Spring Security 6? ❌ ПОСЛЕДСТВИЕ: антипаттерн деградирует SLA при росте нагрузки или зависимостей.

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
- `WebSecurityConfigurerAdapter` удалён → `SecurityFilterChain` Bean
- `authorizeRequests` → `authorizeHttpRequests`
- `antMatchers` → `requestMatchers`
- Все настройки через лямбду Customizer


> [!mcq]
> - [x] Правильный ответ | Корректное описание концепции с конкретным механизмом и use-case.
> - [ ] Альтернативное решение которое не подходит | Почему ошибка в этом подходе ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.
> - [ ] Другая альтернатива с критическим недостатком | Это смежное, но отличное понятие ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.
> - [ ] Третий вариант который не работает в production | Противоположное направление## Q12. Что такое декларативный RestClient? ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.

Spring Boot 3.2 принёс `RestClient` — новый блокирующий HTTP-клиент с fluent API (замена `RestTemplate`):

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

**RestClient vs WebClient**: `RestClient` — блокирующий, проще; `WebClient` — реактивный, сложнее.


> [!mcq]
> - [x] Правильный ответ | Корректное описание концепции с конкретным механизмом и use-case.
> - [ ] Альтернативное решение которое не подходит | Почему ошибка в этом подходе ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.
> - [ ] Другая альтернатива с критическим недостатком | Это смежное, но отличное понятие ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.
> - [ ] Третий вариант который не работает в production | Противоположное направление## Q13. Какие изменения в auto-configuration? ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.

```java
// ДО Spring Boot 2.x — META-INF/spring.factories
org.springframework.boot.autoconfigure.EnableAutoConfiguration=\
    com.example.MyAutoConfiguration

// ПОСЛЕ Spring Boot 3.x — META-INF/spring/org.springframework.boot.autoconfigure.AutoConfiguration.imports
com.example.MyAutoConfiguration
```

Теперь auto-configuration декларируется в отдельном файле с именем класса. Старый `spring.factories` всё ещё поддерживается для обратной совместимости.

```java
// Также добавлено @AutoConfiguration — marker аннотация
@AutoConfiguration
@ConditionalOnClass(DataSource.class)
public class MyDataSourceAutoConfiguration { ... }
```


> [!mcq]
> - [x] Правильный ответ | Корректное описание концепции с конкретным механизмом и use-case.
> - [ ] Альтернативное решение которое не подходит | Почему ошибка в этом подходе ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.
> - [ ] Другая альтернатива с критическим недостатком | Это смежное, но отличное понятие ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.
> - [ ] Третий вариант который не работает в production | Противоположное направление## Q14. Что такое Configuration Properties Migrator? ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.

```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-properties-migrator</artifactId>
    <scope>runtime</scope>
</dependency>
```

При запуске приложения в логах появятся предупреждения о устаревших или переименованных properties:

```text
The following properties have been renamed:
  management.metrics.export.prometheus.enabled → 
  management.prometheus.metrics.export.enabled
```

После исправления properties удалите этот dependency.


> [!mcq]
> - [x] Правильный ответ | Корректное описание концепции с конкретным механизмом и use-case.
> - [ ] Альтернативное решение которое не подходит | Почему ошибка в этом подходе ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.
> - [ ] Другая альтернатива с критическим недостатком | Это смежное, но отличное понятие ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.
> - [ ] Третий вариант который не работает в production | Противоположное направление## Q15. Какие проблемы часто возникают при миграции? ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.

1. **Jakarta EE несовместимость библиотек**:
```text
Error: NoClassDefFoundError: javax/servlet/http/HttpServletRequest
→ Решение: обновить до версии с Jakarta-поддержкой или заменить зависимость
```

2. **Устаревшие library versions**:
```text
- Hibernate 5.x → 6.x (breaking changes в HQL, SQL dialects)
- Spring Cloud Sleuth → Micrometer Tracing
- springfox-swagger → springdoc-openapi
- Lombok до 1.18.24+ (для Java 17 compatibility)
```

3. **Security 6 breaking changes** (см. Q11).

4. **Actuator endpoints переименования**:
```text
management.metrics.export.*  → management.prometheus.*
и т.п.
```

5. **WebMVC / WebFlux route handling**:
```java
// antMatchers → requestMatchers
// pathPatternsParser теперь default
```

6. **Jackson 2.14+** — более строгая обработка типов.

7. **HikariCP defaults изменились** — timeouts короче.

**Best practice**: разбить миграцию на этапы — сначала Java 17, потом зависимости до последних 2.x, затем 3.0, затем 3.x++.

## See also


> [!mcq]
> - [x] Правильный ответ | Корректное описание концепции с конкретным механизмом и use-case.
> - [ ] Альтернативное решение которое не подходит | Почему ошибка в этом подходе ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.
> - [ ] Другая альтернатива с критическим недостатком | Это смежное, но отличное понятие ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.
> - [ ] Третий вариант который не работает в production | Противоположное направление- [Spring Boot](spring-boot-interview.md) — основы Spring Boot, auto-configuration ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.
- [Spring Framework](spring-framework-interview.md) — Spring Framework 6 changes
- [Spring Security](spring-security-interview.md) — breaking changes в Spring Security 6
- [Java Virtual Threads](../../programming-languages/java/java-virtual-threads-interview.md) — поддержка в Spring Boot 3.2+
- [Java 17-21](../../programming-languages/java/java-17-21-interview.md) — Java 17 baseline, records, sealed classes
- [GraalVM Native Image](../../jvm/graalvm-native-interview.md) — first-class native support в Spring Boot 3
- [Spring REST Client](spring-rest-client-interview.md) — RestClient — новый декларативный API
- [Micrometer](../../monitoring/micrometer-interview.md) — новая Observation API в Spring 6
- [Spring Boot Actuator](spring-boot-actuator-interview.md) — изменения в Actuator endpoints
- [Spring WebFlux](spring-webflux-interview.md) — изменения в WebFlux 6
