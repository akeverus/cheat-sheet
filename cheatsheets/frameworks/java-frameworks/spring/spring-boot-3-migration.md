---
title: "Миграция с Spring Boot 2.x на 3.x"
description: "Практическое руководство по миграции с Spring Boot 2.x на 3.x: Jakarta EE 10, Java 17, Security 6, GraalVM Native, AOT, Micrometer Observation API — пошаговый план и типичные ошибки"
tags:
  - spring-boot
  - migration
  - jakarta-ee
  - java-17
  - spring-security
  - graalvm
  - aot
  - spring-data
  - micrometer
difficulty: advanced
updated: "2026-04-20"
---

# Миграция с Spring Boot 2.x на 3.x

## Полезные ссылки

- [Официальный Migration Guide Spring Boot 3.0](https://github.com/spring-projects/spring-boot/wiki/Spring-Boot-3.0-Migration-Guide)
- [Spring Framework 6.0 What's New](https://github.com/spring-projects/spring-framework/wiki/What%27s-New-in-Spring-Framework-6.x)

## Содержание

- [Ключевые изменения](#ключевые-изменения)
- [Пошаговый план миграции](#пошаговый-план-миграции)
  - [Шаг 1 — Обновить JDK](#шаг-1-обновить-jdk)
  - [Шаг 2 — Обновить Spring Boot](#шаг-2-обновить-spring-boot)
  - [Шаг 3 — Заменить javax → jakarta](#шаг-3-заменить-javax-jakarta)
  - [Шаг 4 — Пересмотреть Security конфигурацию](#шаг-4-пересмотреть-security-конфигурацию)
  - [Шаг 5 — Проверить Hibernate / Spring Data](#шаг-5-проверить-hibernate-spring-data)
  - [Шаг 6 — Запустить полный набор тестов](#шаг-6-запустить-полный-набор-тестов)
- [javax → jakarta: таблица замен](#javax-jakarta-таблица-замен)
  - [Пример замены импортов](#пример-замены-импортов)
- [Spring Security 6.x](#spring-security-6x)
  - [Главное изменение — удалён WebSecurityConfigurerAdapter](#главное-изменение-удалён-websecurityconfigureradapter)
  - [Изменения DSL](#изменения-dsl)
  - [Пример JWT-конфигурации в Security 6](#пример-jwt-конфигурации-в-security-6)
- [Spring Data JPA 3.x](#spring-data-jpa-3x)
  - [Hibernate 6 — изменения в типах](#hibernate-6-изменения-в-типах)
  - [Query Hints](#query-hints)
- [AOT Processing](#aot-processing)
- [GraalVM Native Image](#graalvm-native-image)
  - [Базовый пример сборки](#базовый-пример-сборки)
  - [reflect-config.json (ручные подсказки)](#reflect-configjson-ручные-подсказки)
  - [Ограничения Native Image](#ограничения-native-image)
- [Actuator и Observability](#actuator-и-observability)
  - [Новый формат /actuator/info](#новый-формат-actuatorinfo)
  - [Micrometer Observation API](#micrometer-observation-api)
  - [@Observed на методах](#observed-на-методах)
- [Типичные ошибки при миграции](#типичные-ошибки-при-миграции)
- [Чек-лист миграции](#чек-лист-миграции)
- [См. также](#см-также)

## Ключевые изменения

Spring Boot 3.x — major-релиз с несколькими breaking changes:

- **Java 17 minimum** — поддержка Java 8/11 прекращена. Используется record, sealed classes, text blocks.
- **Jakarta EE 10** — все `javax.*` пакеты переименованы в `jakarta.*` (Servlet API, JPA, Validation, Mail, etc.)
- **Spring Framework 6.x** — основа Boot 3. Новый AOT-движок, поддержка GraalVM Native.
- **Spring Security 6.x** — удалён `WebSecurityConfigurerAdapter`, новый Lambda DSL обязателен.
- **Hibernate 6.x** — смена поставщика JPA, изменения в маппинге типов.
- **Micrometer Observation API** — замена `Metrics` + `Tracing` на единый `ObservationRegistry`.


## Пошаговый план миграции

### Шаг 1 — Обновить JDK

```bash
# Проверить текущую версию
java -version

# Установить JDK 17+ (через SDKMAN)
sdk install java 17.0.11-tem
sdk use java 17.0.11-tem
```

В `build.gradle` / `pom.xml` обновить target:

```groovy
// build.gradle
java {
    sourceCompatibility = JavaVersion.VERSION_17
    targetCompatibility = JavaVersion.VERSION_17
}
```

```xml
<!-- pom.xml -->
<properties>
    <java.version>17</java.version>
</properties>
```

### Шаг 2 — Обновить Spring Boot

```groovy
// build.gradle
plugins {
    id 'org.springframework.boot' version '3.2.5'
    id 'io.spring.dependency-management' version '1.1.5'
}
```

```xml
<!-- pom.xml -->
<parent>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-parent</artifactId>
    <version>3.2.5</version>
</parent>
```

### Шаг 3 — Заменить javax → jakarta

Через IntelliJ IDEA: **Edit → Find → Replace in Files** (regex mode) или CLI:

```bash
# macOS/Linux — массовая замена
find src -name "*.java" -exec sed -i '' \
  's/import javax\.persistence\./import jakarta.persistence./g;
   s/import javax\.validation\./import jakarta.validation./g;
   s/import javax\.servlet\./import jakarta.servlet./g' {} +
```

### Шаг 4 — Пересмотреть Security конфигурацию

Убрать `WebSecurityConfigurerAdapter`, перейти на `SecurityFilterChain` (см. раздел ниже).

### Шаг 5 — Проверить Hibernate / Spring Data

Hibernate 6 изменил ряд маппингов типов. Запустить тесты и проверить SQL-логи:

```yaml
logging:
  level:
    org.hibernate.SQL: DEBUG
    org.hibernate.type.descriptor.sql: TRACE
```

### Шаг 6 — Запустить полный набор тестов

```bash
./gradlew test
./mvnw test
```


## javax → jakarta: таблица замен

| javax (Boot 2.x)                   | jakarta (Boot 3.x)                   | Область                |
|------------------------------------|--------------------------------------|------------------------|
| `javax.persistence.*`              | `jakarta.persistence.*`              | JPA / Hibernate        |
| `javax.validation.*`               | `jakarta.validation.*`               | Bean Validation        |
| `javax.servlet.*`                  | `jakarta.servlet.*`                  | Servlet API            |
| `javax.annotation.*`               | `jakarta.annotation.*`               | Annotations (JSR-250)  |
| `javax.mail.*`                     | `jakarta.mail.*`                     | JavaMail               |
| `javax.transaction.*`              | `jakarta.transaction.*`              | JTA                    |
| `javax.ws.rs.*`                    | `jakarta.ws.rs.*`                    | JAX-RS                 |
| `javax.xml.bind.*`                 | `jakarta.xml.bind.*`                 | JAXB                   |

### Пример замены импортов

```java
// БЫЛО                                    // СТАЛО
import javax.persistence.Entity;     →     import jakarta.persistence.Entity;
import javax.persistence.Id;         →     import jakarta.persistence.Id;
import javax.validation.constraints.*;→     import jakarta.validation.constraints.*;
import javax.servlet.http.HttpServletRequest; → import jakarta.servlet.http.HttpServletRequest;
```


## Spring Security 6.x

### Главное изменение — удалён WebSecurityConfigurerAdapter

```java
// БЫЛО (Spring Boot 2.x) — больше не работает
@Configuration
@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {
    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
            .authorizeRequests()
                .antMatchers("/public/**").permitAll()
                .anyRequest().authenticated()
            .and()
            .formLogin();
    }
}
```

```java
// СТАЛО (Spring Boot 3.x / Security 6)
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/public/**").permitAll()
                .anyRequest().authenticated()
            )
            .formLogin(Customizer.withDefaults());
        return http.build();
    }
}
```

### Изменения DSL

| Boot 2.x                     | Boot 3.x                                        |
|------------------------------|-------------------------------------------------|
| `.authorizeRequests()`       | `.authorizeHttpRequests()`                      |
| `.antMatchers("/path")`      | `.requestMatchers("/path")`                     |
| `.and()`                     | Lambda DSL (`.and()` не нужен)                  |
| `http.csrf().disable()`      | `http.csrf(csrf -> csrf.disable())`             |
| `@EnableGlobalMethodSecurity`| `@EnableMethodSecurity`                         |

### Пример JWT-конфигурации в Security 6

```java
@Bean
public SecurityFilterChain jwtFilterChain(HttpSecurity http) throws Exception {
    http
        .csrf(csrf -> csrf.disable())
        .sessionManagement(sm -> sm
            .sessionCreationPolicy(SessionCreationPolicy.STATELESS))
        .authorizeHttpRequests(auth -> auth
            .requestMatchers("/api/auth/**").permitAll()
            .anyRequest().authenticated())
        .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);
    return http.build();
}
```


## Spring Data JPA 3.x

### Hibernate 6 — изменения в типах

- `@Type(type = "...")` из `org.hibernate.annotations` требует нового синтаксиса
- UUID-поля теперь маппятся по-другому на разных базах данных
- `@Column(columnDefinition = "TEXT")` может потребовать явного указания

```java
// Boot 2.x — использовался Hibernate 5
@Type(type = "org.hibernate.type.TextType")
private String longText;

// Boot 3.x — Hibernate 6, новый синтаксис
@JdbcTypeCode(SqlTypes.LONGVARCHAR)
private String longText;
```

### Query Hints

```java
@QueryHints(@QueryHint(name = "jakarta.persistence.query.timeout", value = "5000"))
List<Order> findByStatus(String status);
```


## AOT Processing

AOT (Ahead-of-Time) — новый движок в Spring Framework 6, включён по умолчанию в Spring Boot 3.

- Генерирует исходный код и метаданные во время сборки
- Устраняет runtime reflection для BeanDefinition
- JVM-режим: startup быстрее на ~15–30%; Native: startup от <100ms до ~500ms (vs 2–5s в JVM)
- Обязателен для GraalVM Native Image

```bash
# Gradle
./gradlew processAot && ./gradlew bootRun
# Maven
./mvnw spring-boot:process-aot && ./mvnw spring-boot:run
```

**AOT Hints для своих классов:**

```java
@Configuration
@ImportRuntimeHints(MyRuntimeHints.class)
public class AppConfig { }

public class MyRuntimeHints implements RuntimeHintsRegistrar {
    @Override
    public void registerHints(RuntimeHints hints, ClassLoader classLoader) {
        hints.reflection()
            .registerType(MyDynamicClass.class, MemberCategory.INVOKE_DECLARED_METHODS);
        hints.resources()
            .registerPattern("templates/*.html");
    }
}
```


## GraalVM Native Image

### Базовый пример сборки

```groovy
plugins {
    id 'org.graalvm.buildtools.native' version '0.9.28'
}
graalvmNative.binaries.main.imageName = 'my-app'
```

```bash
./gradlew nativeCompile          # требует GraalVM JDK
./build/native/nativeCompile/my-app
```

### reflect-config.json (ручные подсказки)

Если AOT не «видит» динамически создаваемый класс, добавить вручную:

```json
[
  {
    "name": "com.example.MyClass",
    "allDeclaredConstructors": true,
    "allDeclaredMethods": true,
    "allDeclaredFields": true
  }
]
```

Путь: `src/main/resources/META-INF/native-image/<group>/<artifact>/reflect-config.json`

### Ограничения Native Image

- `Class.forName` без hints — не работает
- Reflection требует явной регистрации (AOT hints или `reflect-config.json`)
- CGLIB-прокси → interface-прокси или AOT-сгенерированные
- Ряд библиотек не поддерживают Native (Groovy, некоторые AOP-сценарии)
- Время сборки: 2–10 минут


## Actuator и Observability

### Новый формат /actuator/info

```yaml
# application.yml
management:
  info:
    env:
      enabled: true
    build:
      enabled: true
    git:
      enabled: true
      mode: full
  endpoints:
    web:
      exposure:
        include: health,info,metrics,prometheus
```

В Boot 3.x `/actuator/info` не показывает env-переменные по умолчанию — нужно явно включить `management.info.env.enabled=true`.

### Micrometer Observation API

Boot 3.x вводит единый `ObservationRegistry` (вместо `MeterRegistry` + Tracing):

```java
// БЫЛО (Boot 2.x) — Micrometer напрямую
@Autowired
MeterRegistry meterRegistry;

Counter counter = meterRegistry.counter("orders.processed");
counter.increment();
```

```java
// СТАЛО (Boot 3.x) — Observation API
@Autowired
ObservationRegistry observationRegistry;

Observation.createNotStarted("orders.processed", observationRegistry)
    .observe(() -> processOrder(order));
```

### @Observed на методах

```java
@Component
public class OrderService {

    @Observed(name = "order.create", contextualName = "creating-order")
    public Order createOrder(OrderRequest request) {
        // автоматически создаст span + metric
        return repository.save(new Order(request));
    }
}
```

Для работы `@Observed` нужен бин `ObservedAspect`:

```java
@Bean
ObservedAspect observedAspect(ObservationRegistry registry) {
    return new ObservedAspect(registry);
}
```


## Типичные ошибки при миграции

| Симптом                                                              | Причина                                                     | Решение                                                                      |
|----------------------------------------------------------------------|-------------------------------------------------------------|------------------------------------------------------------------------------|
| `ClassNotFoundException: javax.servlet.Filter`                       | Зависимость использует javax, а не jakarta                  | Обновить зависимость или добавить jakarta bridge                             |
| `WebSecurityConfigurerAdapter cannot be resolved`                    | Класс удалён в Security 6                                   | Переписать конфиг на `SecurityFilterChain` bean                             |
| `antMatchers is undefined`                                           | Метод удалён из Security 6                                  | Заменить на `requestMatchers`                                                |
| Hibernate `Could not determine recommended JdbcType`                 | Hibernate 6 изменил маппинг типов                           | Добавить `@JdbcTypeCode` или явно указать `@Column(columnDefinition)`        |
| `No property X found for type Y` в Spring Data                       | Hibernate 6 строже к именам колонок                         | Проверить `@Column(name = "...")` и Naming Strategy                          |
| Native build fails: `Class not found at runtime`                     | Рефлексия без AOT hints                                     | Добавить `RuntimeHintsRegistrar` или `reflect-config.json`                   |
| `management.info.contributors.enabled` не работает                  | В Boot 3.x переименованы свойства                           | Использовать `management.info.env.enabled=true`                              |
| Тесты падают с `NullPointerException` в SecurityContext              | Изменился порядок фильтров                                  | Обновить `@WithMockUser` / `SecurityMockMvcConfigurers`                      |


## Чек-лист миграции

1. Убедиться, что JDK 17+ установлен и `JAVA_HOME` указывает на него
2. Обновить `spring-boot-starter-parent` (или BOM) до версии 3.x
3. Обновить `io.spring.dependency-management` плагин до 1.1.x
4. Массово заменить `javax.*` → `jakarta.*` во всех `.java` файлах
5. Проверить сторонние зависимости: все должны поддерживать Jakarta EE 9+ (особенно MapStruct, Lombok, QueryDSL)
6. Переписать `WebSecurityConfigurerAdapter` → `SecurityFilterChain`
7. Заменить `.antMatchers()` → `.requestMatchers()`, `.authorizeRequests()` → `.authorizeHttpRequests()`
8. Заменить `@EnableGlobalMethodSecurity` → `@EnableMethodSecurity`
9. Проверить Hibernate маппинги: новые типы, UUID, LOB-поля
10. Обновить `application.yml`: проверить переименованные `management.*` свойства
11. Добавить `management.info.env.enabled=true` если нужен `/actuator/info` с env
12. Запустить полный тест-suite, исправить падающие тесты
13. Если используется GraalVM Native — добавить AOT hints для динамических классов
14. Проверить логи запуска на `WARN` и `DEPRECATED` сообщения
15. Обновить CI/CD pipeline: указать JDK 17+, обновить Docker base image


## См. также

- [[spring-boot|Spring Boot]]
- [[spring-security|Spring Security]]
- [[spring-data-jpa|Spring Data JPA]]
- [[spring-core|Spring Core]]
- [[spring-core|Spring Core]]
