---
title: "Spring Boot"
description: "Практическое руководство по Spring Boot: автоконфигурация, стартеры, свойства, встроенный сервер, Actuator, тестирование, упаковка и типичные ошибки."
tags:
  - spring
  - spring-boot
  - auto-configuration
  - actuator
  - java
difficulty: "intermediate"
prerequisites: ["spring-core.md"]
next: ["spring-data.md", "spring-security.md"]
updated: "2026-04-20"
---

# Spring Boot

Практическое руководство по Spring Boot: автоконфигурация, стартеры, конфигурация свойств, встроенный сервер, Actuator, DevTools, тестирование и упаковка.

## Полезные ссылки

- [Spring Boot Reference](https://docs.spring.io/spring-boot/reference/)
- [Spring Boot Auto-configuration](https://docs.spring.io/spring-boot/reference/using/auto-configuration.html)
- [Spring Boot Actuator](https://docs.spring.io/spring-boot/reference/actuator/)

## Содержание

- [Автоконфигурация](#автоконфигурация)
  - [Как работает под капотом](#как-работает-под-капотом)
  - [Условные аннотации](#условные-аннотации)
  - [Отладка автоконфигурации](#отладка-автоконфигурации)
- [Стартеры](#стартеры)
  - [Что такое стартер](#что-такое-стартер)
  - [Создание собственного стартера](#создание-собственного-стартера)
- [Конфигурация свойств](#конфигурация-свойств)
  - [Источники свойств и приоритет](#источники-свойств-и-приоритет)
  - [Type-safe ConfigurationProperties](#type-safe-configurationproperties)
  - [Relaxed Binding](#relaxed-binding)
  - [Профили](#профили)
- [Встроенный сервер](#встроенный-сервер)
- [Actuator](#actuator)
  - [Встроенные эндпоинты](#встроенные-эндпоинты)
  - [Собственный эндпоинт](#собственный-эндпоинт)
- [DevTools](#devtools)
- [Тестирование](#тестирование)
  - [SpringBootTest](#springboottest)
  - [Срезы тестов](#срезы-тестов)
- [Упаковка](#упаковка)
  - [Fat JAR](#fat-jar)
  - [Layered JAR для Docker](#layered-jar-для-docker)
- [Типичные ошибки](#типичные-ошибки)
- [См. также](#см-также)

## Автоконфигурация

### Как работает под капотом

`@SpringBootApplication` — это мета-аннотация, объединяющая три:

```java
@SpringBootConfiguration   // = @Configuration
@EnableAutoConfiguration   // запускает механизм автоконфигурации
@ComponentScan             // сканирует пакет приложения и дочерние
public class Application {
    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }
}
```

Механизм автоконфигурации пошагово:

```text
1. @EnableAutoConfiguration активирует AutoConfigurationImportSelector
2. Селектор читает файл META-INF/spring/org.springframework.boot.autoconfigure.AutoConfiguration.imports
   (до Boot 2.7 — META-INF/spring.factories)
3. Каждая строка — FQCN класса автоконфигурации
4. Для каждого класса проверяются @Conditional-аннотации
5. Если все условия выполнены — класс регистрируется как @Configuration
6. Бины из этого класса попадают в контейнер
```

Пример записи в `AutoConfiguration.imports`:

```text
org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration
org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration
org.springframework.boot.autoconfigure.web.servlet.WebMvcAutoConfiguration
```

Типичный класс автоконфигурации:

```java
@AutoConfiguration(after = DataSourceAutoConfiguration.class)
@ConditionalOnClass(DataSource.class)
@EnableConfigurationProperties(JpaProperties.class)
public class HibernateJpaAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean
    public LocalContainerEntityManagerFactoryBean entityManagerFactory(
            DataSource dataSource, JpaProperties properties) {
        // создание EntityManagerFactory с настройками из properties
    }
}
```

Ключевой принцип: **автоконфигурация отступает, если разработчик определил свой бин** (`@ConditionalOnMissingBean`).

### Условные аннотации

| Аннотация | Условие |
|-----------|---------|
| `@ConditionalOnClass` | Класс есть в classpath |
| `@ConditionalOnMissingClass` | Класса нет в classpath |
| `@ConditionalOnBean` | Бин зарегистрирован в контейнере |
| `@ConditionalOnMissingBean` | Бин НЕ зарегистрирован |
| `@ConditionalOnProperty` | Свойство имеет определённое значение |
| `@ConditionalOnResource` | Ресурс доступен в classpath |
| `@ConditionalOnWebApplication` | Приложение — веб-приложение |
| `@ConditionalOnExpression` | SpEL-выражение вернуло true |

```java
@Configuration
@ConditionalOnClass(RedisConnectionFactory.class)
@ConditionalOnProperty(name = "app.cache.type", havingValue = "redis")
public class RedisCacheConfig {

    @Bean
    @ConditionalOnMissingBean
    public CacheManager cacheManager(RedisConnectionFactory factory) {
        return RedisCacheManager.builder(factory).build();
    }
}
```

### Отладка автоконфигурации

```bash
# Полный отчёт об автоконфигурации
java -jar app.jar --debug

# Или в application.yml
# debug: true
```

Вывод содержит две секции:
- **Positive matches** — какие автоконфигурации применились и почему
- **Negative matches** — какие не применились и какое условие не выполнено

Также полезен Actuator-эндпоинт `/actuator/conditions`.

## Стартеры

### Что такое стартер

Стартер — это Maven/Gradle-зависимость, которая **не содержит кода**, а только подтягивает:
1. Нужные библиотеки (транзитивные зависимости)
2. Модуль автоконфигурации

```text
spring-boot-starter-web
├── spring-boot-starter (базовый: core, logging, autoconfigure)
├── spring-web
├── spring-webmvc
├── spring-boot-starter-tomcat (встроенный сервер)
└── spring-boot-starter-json (Jackson)
```

Основные стартеры:

| Стартер | Назначение |
|---------|-----------|
| `spring-boot-starter-web` | REST/MVC + Tomcat |
| `spring-boot-starter-data-jpa` | JPA + Hibernate + Spring Data |
| `spring-boot-starter-data-jdbc` | Spring Data JDBC |
| `spring-boot-starter-security` | Spring Security |
| `spring-boot-starter-actuator` | Мониторинг и метрики |
| `spring-boot-starter-test` | JUnit 5 + Mockito + AssertJ |
| `spring-boot-starter-validation` | Bean Validation (Hibernate Validator) |
| `spring-boot-starter-cache` | Кеширование |

### Создание собственного стартера

Конвенция: два модуля — автоконфигурация и стартер.

```text
acme-spring-boot-starter/           # стартер (пустой, только зависимости)
  └── pom.xml → зависит на acme-spring-boot-autoconfigure

acme-spring-boot-autoconfigure/     # логика автоконфигурации
  └── src/main/java/
      └── AcmeAutoConfiguration.java
  └── src/main/resources/
      └── META-INF/spring/
          └── org.springframework.boot.autoconfigure.AutoConfiguration.imports
```

Класс автоконфигурации:

```java
@AutoConfiguration
@ConditionalOnClass(AcmeService.class)
@EnableConfigurationProperties(AcmeProperties.class)
public class AcmeAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean
    public AcmeService acmeService(AcmeProperties properties) {
        return new AcmeService(properties.getApiKey(), properties.getTimeout());
    }
}
```

Properties-класс:

```java
@ConfigurationProperties(prefix = "acme")
public class AcmeProperties {
    private String apiKey;
    private Duration timeout = Duration.ofSeconds(30);
    // getters/setters
}
```

Регистрация — файл `META-INF/spring/org.springframework.boot.autoconfigure.AutoConfiguration.imports`:

```text
com.acme.autoconfigure.AcmeAutoConfiguration
```

## Конфигурация свойств

### Источники свойств и приоритет

Spring Boot читает свойства из множества источников. Приоритет (от высшего к низшему):

```text
1. Аргументы командной строки (--server.port=9090)
2. SPRING_APPLICATION_JSON (JSON в env-переменной)
3. Системные свойства JVM (-Dserver.port=9090)
4. Переменные окружения ОС (SERVER_PORT=9090)
5. application-{profile}.yml
6. application.yml
7. @PropertySource на @Configuration-классах
8. Значения по умолчанию (SpringApplication.setDefaultProperties)
```

Пример `application.yml`:

```yaml
server:
  port: 8080
  servlet:
    context-path: /api

spring:
  datasource:
    url: jdbc:postgresql://localhost:5432/mydb
    username: app
    password: ${DB_PASSWORD}  # берётся из переменной окружения

app:
  feature:
    enabled: true
    max-retries: 3
    timeout: 30s
```

### Type-safe ConfigurationProperties

Вместо россыпи `@Value` — один типобезопасный класс:

```java
@ConfigurationProperties(prefix = "app.feature")
public record FeatureProperties(
    boolean enabled,
    int maxRetries,
    Duration timeout
) {}
```

Регистрация:

```java
@Configuration
@EnableConfigurationProperties(FeatureProperties.class)
public class AppConfig {}

// Или через аннотацию на самом классе:
@ConfigurationProperties(prefix = "app.feature")
@Component  // Spring Boot 3.x рекомендует @EnableConfigurationProperties
public record FeatureProperties(boolean enabled, int maxRetries, Duration timeout) {}
```

Использование:

```java
@Service
@RequiredArgsConstructor
public class FeatureService {
    private final FeatureProperties properties;

    public void execute() {
        if (properties.enabled()) {
            // maxRetries = 3, timeout = 30s — всё типизировано
        }
    }
}
```

Валидация свойств:

```java
@ConfigurationProperties(prefix = "app.feature")
@Validated
public record FeatureProperties(
    boolean enabled,
    @Min(1) @Max(10) int maxRetries,
    @NotNull Duration timeout
) {}
```

### Relaxed Binding

Spring Boot автоматически маппит разные формы записи на одно свойство:

| Форма | Пример |
|-------|--------|
| Camel case | `app.maxRetries` |
| Kebab case (рекомендуется) | `app.max-retries` |
| Underscore | `app.max_retries` |
| Upper case (env) | `APP_MAX_RETRIES` |

Все четыре формы маппятся на поле `maxRetries`.

### Профили

```yaml
# application.yml — общая конфигурация
spring:
  profiles:
    active: dev

---
# application-dev.yml
server:
  port: 8080
logging:
  level:
    root: DEBUG

---
# application-prod.yml
server:
  port: 80
logging:
  level:
    root: WARN
```

Активация профиля:

```bash
# Командная строка
java -jar app.jar --spring.profiles.active=prod

# Переменная окружения
SPRING_PROFILES_ACTIVE=prod java -jar app.jar

# Программно
SpringApplication app = new SpringApplication(Application.class);
app.setAdditionalProfiles("prod");
app.run(args);
```

Группы профилей (Boot 2.4+):

```yaml
spring:
  profiles:
    group:
      prod: proddb, prodmetrics
      dev: devdb, devtools
```

## Встроенный сервер

Spring Boot включает встроенный servlet-контейнер — приложение запускается как обычный JAR.

| Сервер | Стартер | Особенности |
|--------|---------|-------------|
| **Tomcat** (по умолчанию) | `spring-boot-starter-web` | Стабильный, широко используется |
| **Jetty** | Замена Tomcat | Лёгкий, хорош для WebSocket |
| **Undertow** | Замена Tomcat | Высокая производительность, non-blocking |

Замена сервера:

```kotlin
// build.gradle.kts
implementation("org.springframework.boot:spring-boot-starter-web") {
    exclude(group = "org.springframework.boot", module = "spring-boot-starter-tomcat")
}
implementation("org.springframework.boot:spring-boot-starter-jetty")
```

Настройка через свойства:

```yaml
server:
  port: 8443
  ssl:
    key-store: classpath:keystore.p12
    key-store-password: ${SSL_PASSWORD}
    key-store-type: PKCS12
  tomcat:
    max-threads: 200
    accept-count: 100
    connection-timeout: 5s
  compression:
    enabled: true
    mime-types: application/json,text/html
    min-response-size: 1024
  shutdown: graceful  # Boot 2.3+: ожидание завершения текущих запросов

spring:
  lifecycle:
    timeout-per-shutdown-phase: 30s
```

Программная настройка:

```java
@Component
public class ServerCustomizer implements WebServerFactoryCustomizer<TomcatServletWebServerFactory> {
    @Override
    public void customize(TomcatServletWebServerFactory factory) {
        factory.addConnectorCustomizers(connector -> {
            connector.setProperty("maxKeepAliveRequests", "100");
            connector.setProperty("keepAliveTimeout", "60000");
        });
    }
}
```

## Actuator

Spring Boot Actuator предоставляет эндпоинты для мониторинга и управления приложением.

```kotlin
implementation("org.springframework.boot:spring-boot-starter-actuator")
```

### Встроенные эндпоинты

| Эндпоинт | Назначение |
|----------|-----------|
| `/actuator/health` | Статус здоровья (UP/DOWN) |
| `/actuator/info` | Информация о приложении |
| `/actuator/metrics` | Метрики (JVM, HTTP, DB) |
| `/actuator/env` | Свойства окружения |
| `/actuator/beans` | Все бины в контейнере |
| `/actuator/conditions` | Отчёт автоконфигурации |
| `/actuator/loggers` | Управление уровнями логирования |
| `/actuator/prometheus` | Метрики в формате Prometheus |

Настройка:

```yaml
management:
  endpoints:
    web:
      exposure:
        include: health, info, metrics, prometheus  # по умолчанию только health
  endpoint:
    health:
      show-details: when-authorized  # always, when-authorized, never
      probes:
        enabled: true  # /actuator/health/liveness, /actuator/health/readiness
  metrics:
    tags:
      application: ${spring.application.name}
```

Собственный health indicator:

```java
@Component
public class ExternalApiHealthIndicator implements HealthIndicator {
    private final RestClient restClient;

    @Override
    public Health health() {
        try {
            restClient.get().uri("/ping").retrieve().body(String.class);
            return Health.up().withDetail("api", "reachable").build();
        } catch (Exception e) {
            return Health.down().withException(e).build();
        }
    }
}
// Появится в /actuator/health как "externalApi": { "status": "UP" }
```

### Собственный эндпоинт

```java
@Component
@Endpoint(id = "features")
public class FeaturesEndpoint {

    @ReadOperation
    public Map<String, Boolean> features() {
        return Map.of(
            "newUI", true,
            "darkMode", false
        );
    }

    @WriteOperation
    public void toggle(@Selector String name, boolean enabled) {
        // Изменение фича-флага через POST /actuator/features/{name}
    }
}
```

## DevTools

```kotlin
developmentOnly("org.springframework.boot:spring-boot-devtools")
```

Возможности:
- **Автоматический рестарт** при изменении classpath (быстрее полного перезапуска — два classloader-а)
- **LiveReload** — браузер обновляется при изменении ресурсов
- **Отключение кеширования шаблонов** (Thymeleaf, FreeMarker)
- **Расширенная отладка** — показывает полный condition evaluation report

```yaml
spring:
  devtools:
    restart:
      enabled: true
      exclude: static/**,public/**   # не перезапускать при изменении статики
    livereload:
      enabled: true
```

DevTools автоматически отключаются в production (при запуске из JAR).

## Тестирование

### SpringBootTest

Полный интеграционный тест — поднимает весь контекст приложения:

```java
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class OrderControllerIntegrationTest {

    @Autowired
    private TestRestTemplate restTemplate;

    @Test
    void shouldCreateOrder() {
        OrderRequest request = new OrderRequest("item-1", 2);
        ResponseEntity<OrderResponse> response = restTemplate
                .postForEntity("/api/orders", request, OrderResponse.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(response.getBody().id()).isNotNull();
    }
}
```

### Срезы тестов

Срезы (slices) загружают только нужную часть контекста — тесты быстрее и изолированнее.

**@WebMvcTest** — только веб-слой (контроллеры, фильтры, конвертеры):

```java
@WebMvcTest(OrderController.class)
class OrderControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private OrderService orderService;

    @Test
    void shouldReturnOrder() throws Exception {
        when(orderService.findById(1L))
                .thenReturn(new Order(1L, "item-1", 2));

        mockMvc.perform(get("/api/orders/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1));
    }
}
```

**@DataJpaTest** — только JPA-слой (репозитории, EntityManager, встроенная БД):

```java
@DataJpaTest
class OrderRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private OrderRepository repository;

    @Test
    void shouldFindByStatus() {
        entityManager.persist(new Order("item-1", OrderStatus.PENDING));
        entityManager.persist(new Order("item-2", OrderStatus.COMPLETED));

        List<Order> pending = repository.findByStatus(OrderStatus.PENDING);

        assertThat(pending).hasSize(1);
        assertThat(pending.get(0).getItem()).isEqualTo("item-1");
    }
}
```

**@JsonTest** — сериализация/десериализация:

```java
@JsonTest
class OrderResponseTest {

    @Autowired
    private JacksonTester<OrderResponse> json;

    @Test
    void shouldSerialize() throws Exception {
        OrderResponse response = new OrderResponse(1L, "item-1", BigDecimal.TEN);

        assertThat(json.write(response))
                .extractingJsonPathNumberValue("$.id").isEqualTo(1);
        assertThat(json.write(response))
                .extractingJsonPathStringValue("$.item").isEqualTo("item-1");
    }
}
```

Другие полезные срезы:

| Аннотация | Что загружает |
|-----------|--------------|
| `@WebFluxTest` | WebFlux-контроллеры |
| `@DataJdbcTest` | Spring Data JDBC |
| `@RestClientTest` | REST-клиенты (MockRestServiceServer) |
| `@JdbcTest` | JdbcTemplate, DataSource |

## Упаковка

### Fat JAR

Spring Boot упаковывает приложение в исполняемый JAR, содержащий все зависимости:

```text
app.jar
├── BOOT-INF/
│   ├── classes/          # код приложения
│   ├── lib/              # все зависимости (*.jar)
│   └── classpath.idx     # порядок загрузки
├── META-INF/
│   └── MANIFEST.MF       # Main-Class: JarLauncher, Start-Class: Application
└── org/springframework/boot/loader/  # загрузчик Spring Boot
```

```bash
# Сборка
./gradlew bootJar

# Запуск
java -jar build/libs/app.jar --spring.profiles.active=prod
```

### Layered JAR для Docker

Boot 2.3+ поддерживает слоистые JAR — зависимости и код приложения в разных слоях Docker-образа:

```text
dependencies        # редко меняется → кешируется
spring-boot-loader  # редко меняется → кешируется
snapshot-dependencies
application         # часто меняется → пересобирается
```

```dockerfile
# Многоэтапная сборка
FROM eclipse-temurin:21-jdk AS builder
WORKDIR /app
COPY build/libs/app.jar app.jar
RUN java -Djarmode=layertools -jar app.jar extract

FROM eclipse-temurin:21-jre
WORKDIR /app
COPY --from=builder /app/dependencies/ ./
COPY --from=builder /app/spring-boot-loader/ ./
COPY --from=builder /app/snapshot-dependencies/ ./
COPY --from=builder /app/application/ ./
ENTRYPOINT ["java", "org.springframework.boot.loader.launch.JarLauncher"]
```

Преимущество: при изменении только кода приложения Docker пересобирает только последний слой. Сборка образа ускоряется в разы.

## Типичные ошибки

| Ошибка | Причина | Решение |
|--------|---------|---------|
| Автоконфигурация не срабатывает | Класс вне базового пакета `@SpringBootApplication` | Проверить структуру пакетов или добавить `scanBasePackages` |
| `@ConfigurationProperties` не связывается | Не зарегистрирован через `@EnableConfigurationProperties` | Добавить `@EnableConfigurationProperties` или `@ConfigurationPropertiesScan` |
| Свой бин перезатирается автоконфигурацией | Неправильный порядок загрузки | Убедиться, что ваш `@Configuration` загружается ДО автоконфигурации |
| `DataSource` не создаётся | Нет драйвера в classpath | Добавить зависимость `runtime("org.postgresql:postgresql")` |
| Профиль не активируется | Опечатка в имени или не тот файл | Проверить `--spring.profiles.active` и имя файла `application-{profile}.yml` |
| Actuator-эндпоинты недоступны | Не настроен exposure | `management.endpoints.web.exposure.include` по умолчанию — только `health` |
| `@SpringBootTest` медленный | Поднимается полный контекст | Использовать срезы (`@WebMvcTest`, `@DataJpaTest`) |
| Fat JAR не запускается | Конфликт зависимостей | `./gradlew dependencies` для анализа, `exclude` для конфликтующих |
| DevTools рестартует бесконечно | Генерируемые файлы в classpath | Настроить `spring.devtools.restart.exclude` |
| `@MockBean` / `@MockitoBean` ломает кеш контекста | Каждая комбинация моков — отдельный контекст | Группировать тесты с одинаковыми моками или использовать `@TestConfiguration` |

## См. также

- [[spring-core|Spring Framework: Core]]
- [[spring-data|Spring Data: JPA, JDBC и работа с данными]]
