---
title: "Вопросы на собеседовании: Spring Boot"
description: "Глубокие ответы по Spring Boot: автоконфигурация, стартеры, Actuator, встроенные серверы, профили, externalized config, Docker, мониторинг."
tags:
  - interview
  - frameworks
  - spring-boot-interview
aliases:
  - "Spring Boot"
  - "Spring Boot interview"
  - "Spring Boot собеседование"
  - "Spring Boot автоконфигурация"
  - "Spring Boot стартеры"
difficulty: "intermediate"
updated: "2026-04-25"
---
# Вопросы на собеседовании: `Spring Boot`

Глубокие ответы по `Spring Boot`: автоконфигурация, стартеры, `Actuator`, встроенные серверы, профили, externalized config, `Docker`, мониторинг.

**`Spring Boot`** — стандарт де-факто для быстрого создания production-ready `Java`-приложений на базе `Spring`. Вопросы по автоконфигурации, стартерам, `Actuator`, профилям и развёртыванию — обязательная часть собеседований на позиции `Java / Senior Developer`. Файл охватывает как базовые, так и продвинутые темы, включая архитектуру встроенных серверов, иерархию конфигурации и интеграцию с [Docker](../../devops/docker-interview.md) и [Kubernetes](../../devops/kubernetes-interview.md).

## Полезные ссылки

### Официальная документация

- [Spring Boot Reference](https://docs.spring.io/spring-boot/reference/) — актуальная документация
- [Spring Boot Guides](https://spring.io/guides) — пошаговые руководства
- [Spring Boot Auto-configuration](https://docs.spring.io/spring-boot/reference/using/auto-configuration.html) — механизм автоконфигурации
- [Spring Boot Actuator](https://docs.spring.io/spring-boot/reference/actuator/) — мониторинг и управление

### Baeldung

- [Spring Boot Tutorial](https://www.baeldung.com/spring-boot) — обзорный туториал по Spring Boot
- [A Custom Auto-Configuration with Spring Boot](https://www.baeldung.com/spring-boot-custom-auto-configuration) — создание собственной автоконфигурации
- [Creating a Custom Starter with Spring Boot](https://www.baeldung.com/spring-boot-custom-starter) — разработка кастомного стартера
- [Display Auto-Configuration Report in Spring Boot](https://www.baeldung.com/spring-boot-auto-configuration-report) — анализ отчёта автоконфигурации
- [Difference Between @ComponentScan and @EnableAutoConfiguration](https://www.baeldung.com/spring-componentscan-vs-enableautoconfiguration) — сравнение аннотаций
- [Spring Boot Security Auto-Configuration](https://www.baeldung.com/spring-boot-security-autoconfiguration) — автоконфигурация безопасности
- [Order of Configuration in Spring Boot](https://www.baeldung.com/spring-boot-configuration-order) — порядок применения конфигурации

## Содержание

- [Полезные ссылки](#полезные-ссылки)
- [See also](#see-also)

**Основы Spring Boot**
- [Q1. (!) Что такое Spring Boot и какие проблемы он решает?](#q1-что-такое-spring-boot-и-какие-проблемы-он-решает)
- [Q2. (!) В чём разница между Spring и Spring Boot?](#q2-в-чём-разница-между-spring-и-spring-boot)
- [Q3. Что делает аннотация @SpringBootApplication?](#q3-что-делает-аннотация-springbootapplication)
- [Q4. Как настроить Spring Boot с помощью Maven / Gradle?](#q4-как-настроить-spring-boot-с-помощью-maven--gradle)
- [Q5. Что такое Spring Initializr?](#q5-что-такое-spring-initializr)

**Starters и механизм стартеров**
- [Q6. (!) Что такое Starters и как они устроены?](#q6-что-такое-starters-и-как-они-устроены)
- [Q7. (!) Какие наиболее популярные Starters?](#q7-какие-наиболее-популярные-starters)
- [Q8. (!) Как создать свой Starter?](#q8-как-создать-свой-starter)

**Автоконфигурация**
- [Q9. (!) Как работает auto-configuration — полный поток?](#q9-как-работает-auto-configuration--полный-поток)
- [Q10. (!) Какие @Conditional-аннотации существуют?](#q10-какие-conditional-аннотации-существуют)
- [Q11. Как отключить конкретную автоконфигурацию?](#q11-как-отключить-конкретную-автоконфигурацию)
- [Q12. (!) Как зарегистрировать пользовательскую автоконфигурацию?](#q12-как-зарегистрировать-пользовательскую-автоконфигурацию)

**Встроенные серверы**
- [Q13. (!) Какие встроенные серверы поддерживает Spring Boot?](#q13-какие-встроенные-серверы-поддерживает-spring-boot)
- [Q14. (!) Архитектура встроенного сервера — как Spring Boot запускает Tomcat?](#q14-архитектура-встроенного-сервера--как-spring-boot-запускает-tomcat)

**Конфигурация и профили**
- [Q15. (!) Иерархия внешней конфигурации (Externalized Configuration)](#q15-иерархия-внешней-конфигурации-externalized-configuration)
- [Q16. Что такое application.properties и application.yml?](#q16-что-такое-applicationproperties-и-applicationyml)
- [Q17. (!) Как использовать профили (profiles)?](#q17-как-использовать-профили-profiles)
- [Q18. (!) Как использовать @ConfigurationProperties?](#q18-как-использовать-configurationproperties)
- [Q19. Как изменить порт по умолчанию?](#q19-как-изменить-порт-по-умолчанию)

**Actuator и мониторинг**
- [Q20. (!) Что такое Spring Boot Actuator?](#q20-что-такое-spring-boot-actuator)
- [Q21. (!) Полный список Actuator endpoints и их категории](#q21-полный-список-actuator-endpoints-и-их-категории)
- [Q22. Как создать custom Actuator endpoint?](#q22-как-создать-custom-actuator-endpoint)
- [Q23. (!) Как настроить мониторинг и метрики (Micrometer)?](#q23-как-настроить-мониторинг-и-метрики-micrometer)
- [Q24. Как настроить health check для Kubernetes?](#q24-как-настроить-health-check-для-kubernetes)

**Развёртывание и DevTools**
- [Q25. (!) Что такое executable JAR и как он устроен?](#q25-что-такое-executable-jar-и-как-он-устроен)
- [Q26. Как развернуть Spring Boot в виде JAR и WAR?](#q26-как-развернуть-spring-boot-в-виде-jar-и-war)
- [Q27. Как упаковать Spring Boot в Docker образ?](#q27-как-упаковать-spring-boot-в-docker-образ)
- [Q28. Что такое Spring Boot DevTools?](#q28-что-такое-spring-boot-devtools)

**Тестирование**
- [Q29. (!) Как отключить автоконфигурацию для тестов?](#q29-как-отключить-автоконфигурацию-для-тестов)
- [Q30. Какие основные аннотации Spring Boot предлагает?](#q30-какие-основные-аннотации-spring-boot-предлагает)
- [Q36. (!) Что такое тестовые срезы (@WebMvcTest, @DataJpaTest)?](#q36-что-такое-тестовые-срезы-webmvctest-datajpatest)
- [Q37. (!) Как работает @SpringBootTest и какие режимы запуска контекста существуют?](#q37-как-работает-springboottest-и-какие-режимы-запуска-контекста-существуют)

**Продвинутые темы**
- [Q31. Как использовать Spring Boot как приложение командной строки?](#q31-как-использовать-spring-boot-как-приложение-командной-строки)
- [Q32. Как настроить логирование?](#q32-как-настроить-логирование)
- [Q33. Как мигрировать с Spring на Spring Boot?](#q33-как-мигрировать-с-spring-на-spring-boot)
- [Q34. (!) Что такое GraalVM Native Image в Spring Boot?](#q34-что-такое-graalvm-native-image-в-spring-boot)
- [Q35. (!) Жизненный цикл Spring Boot приложения](#q35-жизненный-цикл-spring-boot-приложения)
- [Q38. (!) Как устроены @ConditionalOnProperty, @ConditionalOnMissingBean и другие условные аннотации?](#q38-как-устроены-conditionalonproperty-conditionalonmissingbean-и-другие-условные-аннотации)
- [Q39. (!) Что такое AOT-обработка в Spring Boot 3.x?](#q39-что-такое-aot-обработка-в-spring-boot-3x)
- [Q40. (!) Как настроить кастомный HealthIndicator для Actuator?](#q40-как-настроить-кастомный-healthindicator-для-actuator)
- [Q41. (!) Как работает Spring Boot с несколькими профилями одновременно?](#q41-как-работает-spring-boot-с-несколькими-профилями-одновременно)
- [Q42. Как ограничить экспозицию Actuator endpoints в production?](#q42-как-ограничить-экспозицию-actuator-endpoints-в-production)

---

## Q1. (!) Что такое `Spring Boot` и какие проблемы он решает?

`Spring Boot` — фреймворк поверх `Spring Framework`, который устраняет три ключевые проблемы классического `Spring`:

1. **Boilerplate-конфигурация** — вместо десятков XML-файлов или `@Configuration`-классов, `Spring Boot` автоматически конфигурирует бины на основе classpath (автоконфигурация).
2. **Управление зависимостями** — стартеры (`spring-boot-starter-*`) подтягивают совместимые версии библиотек.
3. **Развёртывание** — встроенный сервер (`Tomcat` / `Jetty` / `Undertow`) позволяет запускать приложение как обычный `JAR` без внешнего сервера приложений.

Точка входа — класс с `@SpringBootApplication` и `SpringApplication.run()`:

```java
@SpringBootApplication
public class MyApplication {
    public static void main(String[] args) {
        SpringApplication.run(MyApplication.class, args);
    }
}
```

Ключевые возможности: умные значения по умолчанию (opinionated defaults), встроенные серверы, `Actuator` для мониторинга, удобное тестирование с `@SpringBootTest` и тестовыми срезами.

> **На собеседовании:** важно подчеркнуть, что `Spring Boot` **не заменяет** `Spring`, а упрощает его использование. Под капотом — тот же `Spring Context`, `DI`, `AOP`.

> [!mcq]
> - [ ] Spring Boot заменяет Spring Framework и предоставляет собственный IoC-контейнер. | Spring Boot не заменяет Spring Framework — он строится поверх него. Под капотом работает тот же ApplicationContext, DI и AOP из Spring. Это антипаттерн или неправильный выбор в production.
> - [x] Spring Boot решает три проблемы классического Spring: boilerplate-конфигурация, управление зависимостями и развёртывание. | Именно эти три проблемы устраняет Spring Boot: автоконфигурация убирает XML, стартеры управляют зависимостями, встроенный сервер избавляет от внешнего контейнера.
> - [ ] Spring Boot решает три проблемы классического Spring: производительность, безопасность и масштабируемость. | Производительность, безопасность и масштабируемость — не те проблемы, которые решает Spring Boot. Он фокусируется на упрощении конфигурации, зависимостей и развёртывания.
> - [ ] Spring Boot решает три проблемы классического Spring: мониторинг, логирование и тестирование. | Мониторинг, логирование и тестирование — возможности, которые Spring Boot улучшает, но не являются теми тремя ключевыми проблемами, для решения которых он создан.

## Q2. (!) В чём разница между `Spring` и `Spring Boot`?

| Аспект | `Spring Framework` | `Spring Boot` |
|--------|-------------------|---------------|
| Конфигурация | Явная (XML, `@Configuration`) | Автоконфигурация по classpath |
| Зависимости | Ручной подбор версий | Стартеры с BOM |
| Сервер | Внешний (`Tomcat`, `WildFly`) | Встроенный (`Tomcat` / `Jetty` / `Undertow`) |
| Запуск | WAR на сервер | `java -jar app.jar` |
| Мониторинг | Ручная настройка | `Actuator` из коробки |
| Профили | `@Profile` + XML | `application-{profile}.yml` |

`Spring Boot` использует `Spring Framework` как основу и добавляет три слоя: **автоконфигурацию**, **стартеры** и **встроенный сервер**. Проект `Spring` остаётся ядром — `DI`, `AOP`, `@Transactional`, `Spring MVC` работают одинаково в обоих случаях.

> [!mcq]
> - [ ] Spring Boot добавляет к Spring Framework три слоя: авторизацию, стартеры и встроенный сервер. | Авторизация — не один из трёх слоёв Spring Boot. Три добавленных слоя: автоконфигурация, стартеры и встроенный сервер. Частая ошибка в реальном коде.
> - [ ] Spring Boot добавляет к Spring Framework три слоя: автоконфигурацию, планировщик задач и встроенный сервер. | Планировщик задач — не один из трёх ключевых слоёв Spring Boot. Правильная тройка: автоконфигурация, стартеры и встроенный сервер. Частая ошибка в реальном коде.
> - [x] Spring Boot добавляет к Spring Framework три слоя: автоконфигурацию, стартеры и встроенный сервер. | Это точное описание добавленных слоёв. Автоконфигурация убирает ручную настройку бинов, стартеры управляют зависимостями, встроенный сервер позволяет запускаться через `java -jar`.
> - [ ] Spring Boot добавляет к Spring Framework три слоя: автоконфигурацию, стартеры и внешний сервер. | Spring Boot добавляет именно встроенный сервер, а не внешний. Возможность запускать приложение без внешнего сервера — одно из ключевых преимуществ Spring Boot.

## Q3. Что делает аннотация `@SpringBootApplication`?

`@SpringBootApplication` — мета-аннотация, объединяющая три:

```java
@SpringBootConfiguration   // == @Configuration — класс является источником бинов
@EnableAutoConfiguration    // запускает механизм автоконфигурации
@ComponentScan              // сканирует текущий пакет и вложенные
public @interface SpringBootApplication { }
```

Поведение `@ComponentScan` — сканирование начинается с пакета, в котором находится класс. Поэтому главный класс приложения рекомендуют размещать в корневом пакете.

Атрибуты:
- `exclude` — исключить автоконфигурации: `@SpringBootApplication(exclude = DataSourceAutoConfiguration.class)`
- `scanBasePackages` — переопределить корень сканирования

> [!mcq]
> - [ ] @SpringBootApplication объединяет: @SpringBootConfiguration, @EnableAutoConfiguration и @Repository. | @Repository — стереотипная аннотация для слоя данных, она не входит в состав @SpringBootApplication. Правильная тройка: @SpringBootConfiguration, @EnableAutoConfiguration и @ComponentScan.
> - [x] @SpringBootApplication объединяет: @SpringBootConfiguration, @EnableAutoConfiguration и @ComponentScan. | Именно эти три аннотации составляют @SpringBootApplication. @SpringBootConfiguration регистрирует бины, @EnableAutoConfiguration запускает автоконфигурацию, @ComponentScan сканирует пакеты начиная с класса-владельца.
> - [ ] @SpringBootApplication объединяет: @Configuration, @EnableAutoConfiguration и @ComponentScan. | Хотя @SpringBootConfiguration является специализацией @Configuration, в составе @SpringBootApplication используется именно @SpringBootConfiguration, а не просто @Configuration.
> - [ ] @SpringBootApplication объединяет: @SpringBootConfiguration, @EnableWebMvc и @ComponentScan. | @EnableWebMvc — аннотация для настройки Spring MVC, она не входит в состав @SpringBootApplication. Для автоконфигурации используется @EnableAutoConfiguration.

## Q4. Как настроить `Spring Boot` с помощью `Maven` / `Gradle`?

**Maven** — наследование от `spring-boot-starter-parent`:

```xml
<parent>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-parent</artifactId>
    <version>3.3.0</version>
</parent>
```

Если наследование невозможно (корпоративный parent POM), используют BOM через `dependencyManagement`:

```xml
<dependencyManagement>
    <dependencies>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-dependencies</artifactId>
            <version>3.3.0</version>
            <type>pom</type>
            <scope>import</scope>
        </dependency>
    </dependencies>
</dependencyManagement>
```

**Gradle**:

```groovy
plugins {
    id 'org.springframework.boot' version '3.3.0'
    id 'io.spring.dependency-management' version '1.1.5'
    id 'java'
}
```

> [!mcq]
> - [ ] Если наследование от `spring-boot-starter-parent` невозможно, версии зависимостей нужно прописывать вручную в каждом `<dependency>`. | Ручное прописывание версий — плохой подход, ведёт к конфликтам. Spring Boot предоставляет альтернативу — импорт BOM `spring-boot-dependencies` через `dependencyManagement` со `scope=import`.
> - [ ] Если наследование от `spring-boot-starter-parent` невозможно, нужно использовать `spring-boot-starter-core` через `dependencyManagement`. | Артефакта `spring-boot-starter-core` не существует. BOM с версиями зависимостей называется `spring-boot-dependencies`. Частая ошибка в реальном коде.
> - [x] Если наследование от `spring-boot-starter-parent` невозможно, используют `spring-boot-dependencies` BOM через `dependencyManagement` со `scope=import`. | Это штатный способ подключить управляемые версии без смены parent POM. BOM импортирует весь список `<dependencyManagement>` из `spring-boot-dependencies`.
> - [ ] Если наследование от `spring-boot-starter-parent` невозможно, нужно использовать `spring-boot-bom` через `dependencyManagement`. | Артефакт называется `spring-boot-dependencies`, а не `spring-boot-bom`. Несмотря на то что он играет роль BOM, имя другое. Частая ошибка в реальном коде.

## Q5. Что такое `Spring Initializr`?

[Spring Initializr](https://start.spring.io) — веб-инструмент для генерации каркаса проекта `Spring Boot`. Выбор: система сборки (`Maven` / `Gradle`), язык (`Java` / `Kotlin` / `Groovy`), версия `Spring Boot`, зависимости (стартеры). Результат — готовый `ZIP`-архив.

IDE (`IntelliJ IDEA`, `VS Code` с расширением Spring) используют `Initializr` API под капотом. Также доступен CLI: `spring init --dependencies=web,data-jpa my-project`.

> [!mcq]
> - [x] Spring Initializr — это веб-инструмент и API для генерации каркаса Spring Boot проекта с выбором сборки, языка и зависимостей. | Именно так: start.spring.io отдаёт готовый ZIP с проектом, тот же API используют IDE и CLI `spring init`. Ключевое отличие и best practice в production.
> - [ ] Spring Initializr — это JVM-агент для анализа зависимостей запущенного Spring Boot приложения. | Spring Initializr не работает в runtime и не подключается к запущенному приложению. Это инструмент генерации исходного каркаса проекта на этапе создания.
> - [ ] Spring Initializr — это Gradle-плагин для автоматического обновления версий Spring Boot в существующем проекте. | Spring Initializr не обновляет версии в существующих проектах. Для этого используется Spring Boot Migrator или ручное обновление. Частая ошибка в реальном коде.
> - [ ] Spring Initializr — это Maven-плагин для инициализации `application.yml` по конвенциям Spring Boot. | Spring Initializr — это не Maven-плагин, а отдельный сервис/CLI для генерации каркаса проекта, включая выбор системы сборки. Частая ошибка в реальном коде.

---

## Q6. (!) Что такое `Starters` и как они устроены?

**Starter** — это `Maven`/`Gradle`-артефакт без собственного кода, который служит агрегатором зависимостей. Каждый starter:

1. **Подтягивает совместимые библиотеки** через транзитивные зависимости
2. **Включает автоконфигурацию** — при появлении классов в classpath `Spring Boot` создаёт бины по условиям

```mermaid
graph LR
    A["spring-boot-starter-web"] --> B["spring-web<br/>spring-webmvc"]
    A --> C["spring-boot-starter-tomcat"]
    A --> D["spring-boot-starter-json"]
    C --> E["tomcat-embed-core"]
    D --> F["jackson-databind"]
    A -.->|"auto-config"| G["WebMvcAutoConfiguration<br/>ServletWebServerFactoryAutoConfiguration"]
```

Соглашение по именованию:
- **Официальные:** `spring-boot-starter-{name}` (web, data-jpa, security)
- **Сторонние:** `{name}-spring-boot-starter` (mybatis-spring-boot-starter)

> **На собеседовании:** starter — это **не** библиотека с кодом, а POM-агрегатор зависимостей + автоконфигурация. Без автоконфигурации starter был бы обычным BOM.

> [!mcq]
> - [ ] Starter — это JAR с кодом, который реализует бизнес-логику конкретной функциональности. | Starter не содержит собственного кода с бизнес-логикой — это POM-агрегатор без кода. Именно поэтому официальные стартеры называются `spring-boot-starter-{name}`, а не `spring-boot-{name}`.
> - [x] Starter — это POM-агрегатор зависимостей без собственного кода, который при наличии нужных классов в classpath автоматически создаёт бины через автоконфигурацию. | Это точное определение стартера. Он только подтягивает совместимые библиотеки и регистрирует автоконфигурацию — код приложения остаётся в отдельных модулях.
> - [ ] Starter — это POM-агрегатор зависимостей без собственного кода, который требует явного указания `@EnableXxx` для активации. | Стартеры не требуют явного `@EnableXxx` — это было бы возвратом к ручной конфигурации. Активация происходит автоматически через механизм `AutoConfigurationImportSelector`.
> - [ ] Starter — это POM-агрегатор зависимостей без собственного кода, который регистрируется в `META-INF/services/java.util.ServiceLoader`. | Стартеры используют не ServiceLoader, а собственный механизм Spring Boot: файл `META-INF/spring/...AutoConfiguration.imports` (в 3.x) или `spring.factories` (в 2.x).

> [!mcq]
> - [ ] Официальные стартеры именуются `{name}-spring-boot-starter`, сторонние — `spring-boot-starter-{name}`. | Это перепутанное соглашение. Официальные стартеры Spring называются `spring-boot-starter-{name}`, а сторонние — `{name}-spring-boot-starter`. Частая ошибка в реальном коде.
> - [x] Официальные стартеры именуются `spring-boot-starter-{name}`, сторонние — `{name}-spring-boot-starter`. | Это правильное соглашение об именовании. Например, `spring-boot-starter-web` — официальный, `mybatis-spring-boot-starter` — сторонний от MyBatis. Ключевое отличие и best practice в production.
> - [ ] Официальные стартеры именуются `spring-boot-starter-{name}`, сторонние — `spring-boot-{name}-starter`. | Сторонние стартеры используют форму `{name}-spring-boot-starter`, чтобы избежать конфликта с официальным пространством имён `org.springframework.boot`.
> - [ ] Официальные стартеры именуются `spring-{name}-starter`, сторонние — `{name}-spring-boot-starter`. | Официальные стартеры используют полный префикс `spring-boot-starter-`, а не `spring-`. Например, `spring-boot-starter-data-jpa`, а не `spring-data-jpa-starter`.

## Q7. (!) Какие наиболее популярные `Starters`?

| Starter | Что включает |
|---------|-------------|
| `spring-boot-starter-web` | `Spring MVC`, встроенный `Tomcat`, `Jackson` |
| `spring-boot-starter-data-jpa` | `Spring Data JPA`, `Hibernate`, пул соединений |
| `spring-boot-starter-security` | `Spring Security`, фильтры аутентификации |
| `spring-boot-starter-test` | `JUnit 5`, `Mockito`, `MockMvc`, `AssertJ` |
| `spring-boot-starter-actuator` | Эндпоинты мониторинга, `Micrometer` |
| `spring-boot-starter-validation` | `Hibernate Validator`, `Jakarta Validation` |
| `spring-boot-starter-webflux` | Реактивный стек, `Netty` |
| `spring-boot-starter-data-redis` | `Lettuce`, `Spring Data Redis` |
| `spring-boot-starter-cache` | Абстракция кэширования |
| `spring-boot-starter-amqp` | `RabbitMQ`, `Spring AMQP` |

Подробнее о реактивном стеке — в [Spring WebFlux](spring-webflux-interview.md), о работе с данными — в [Spring Data JPA](spring-data-jpa-interview.md).

> [!mcq]
> - [ ] Starter `spring-boot-starter-web` по умолчанию подтягивает встроенный `Jetty` как сервлет-контейнер. | По умолчанию `spring-boot-starter-web` транзитивно подтягивает `spring-boot-starter-tomcat`, а не Jetty. Чтобы использовать Jetty — нужно исключить Tomcat и добавить `spring-boot-starter-jetty`.
> - [x] Starter `spring-boot-starter-web` по умолчанию подтягивает встроенный `Tomcat` как сервлет-контейнер. | Именно так: `spring-boot-starter-tomcat` является транзитивной зависимостью `spring-boot-starter-web`. Для смены сервера его нужно исключить и добавить Jetty или Undertow.
> - [ ] Starter `spring-boot-starter-web` по умолчанию подтягивает встроенный `Undertow` как сервлет-контейнер. | Undertow не используется по умолчанию. Он подключается отдельным стартером `spring-boot-starter-undertow` с исключением Tomcat. Частая ошибка в реальном коде.
> - [ ] Starter `spring-boot-starter-web` по умолчанию подтягивает встроенный `Netty` как сервлет-контейнер. | Netty — неблокирующий сервер, используется в `spring-boot-starter-webflux`, а не в `spring-boot-starter-web`. В сервлет-стеке по умолчанию Tomcat. Частая ошибка в реальном коде.

## Q8. (!) Как создать свой `Starter`?

Создание starter состоит из двух модулей:

**1. Модуль автоконфигурации** (`my-spring-boot-autoconfigure`):

```java
@AutoConfiguration
@ConditionalOnClass(MyService.class)
@EnableConfigurationProperties(MyProperties.class)
public class MyAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean
    public MyService myService(MyProperties props) {
        return new MyService(props.getUrl(), props.getTimeout());
    }
}
```

```java
@ConfigurationProperties(prefix = "my.service")
public class MyProperties {
    private String url = "http://localhost:8080";
    private Duration timeout = Duration.ofSeconds(30);
    // getters/setters
}
```

**2. Регистрация** — файл `META-INF/spring/org.springframework.boot.autoconfigure.AutoConfiguration.imports`:

```
com.example.MyAutoConfiguration
```

> В `Spring Boot` 2.x использовался `META-INF/spring.factories` с ключом `EnableAutoConfiguration`. С `Spring Boot` 3.x — файл `.imports`.

**3. Модуль starter** (`my-spring-boot-starter`) — пустой POM, подтягивающий модуль автоконфигурации и нужные библиотеки.

```mermaid
graph TD
    A["my-spring-boot-starter<br/>(пустой POM)"] --> B["my-spring-boot-autoconfigure"]
    A --> C["my-library"]
    B --> D["spring-boot-autoconfigure"]
    B -.->|"@ConditionalOnClass"| C
```

> [!mcq]
> - [ ] Собственный стартер состоит из двух модулей: модуль с кодом бизнес-логики и модуль с тестами. | Стартер не содержит отдельного модуля с тестами как часть архитектуры. Классическая структура — модуль автоконфигурации + модуль-агрегатор (пустой POM).
> - [x] Собственный стартер состоит из двух модулей: `*-autoconfigure` с автоконфигурацией и `*-starter` как пустого POM-агрегатора. | Это стандартная структура: autoconfigure содержит `@AutoConfiguration` + `@ConditionalOnClass`, а starter-POM только подтягивает autoconfigure и нужные библиотеки. @ConditionalOnProperty позволяет feature flags; используйте для A/B тестирования.
> - [ ] Собственный стартер состоит из двух модулей: `*-api` с интерфейсами и `*-impl` с реализациями. | Структура `api/impl` относится к дизайну обычных библиотек. Stater-конвенция требует именно пары autoconfigure + starter-POM. Частая ошибка в реальном коде.
> - [ ] Собственный стартер состоит из двух модулей: `*-core` с моделями и `*-spring` с конфигурацией. | Структура core/spring не является конвенцией Spring Boot starter-ов. Правильная пара — `*-autoconfigure` и `*-starter`. Частая ошибка в реальном коде.

---

## Q9. (!) Как работает auto-configuration — полный поток?

Автоконфигурация — ключевой механизм `Spring Boot`. Полный поток:

```mermaid
graph TD
    A["SpringApplication.run()"] --> B["Создание ApplicationContext"]
    B --> C["@EnableAutoConfiguration<br/>через @SpringBootApplication"]
    C --> D["AutoConfigurationImportSelector"]
    D --> E["Загрузка кандидатов из<br/>META-INF/spring/...AutoConfiguration.imports<br/>и spring.factories"]
    E --> F["Фильтрация по @Conditional"]
    F --> G{"@ConditionalOnClass<br/>есть в classpath?"}
    G -->|Да| H{"@ConditionalOnMissingBean<br/>бин не создан вручную?"}
    G -->|Нет| I["Пропуск конфигурации"]
    H -->|Да| J["Регистрация бинов"]
    H -->|Нет| K["Пропуск — пользователь<br/>определил свой бин"]
    J --> L["ApplicationContext готов"]
    K --> L
    I --> L
```

Ключевые этапы:

1. **Сбор кандидатов** — `AutoConfigurationImportSelector` читает файлы `.imports` (Spring Boot 3.x) или `spring.factories` (2.x). В `Spring Boot 3.3` — около **150** автоконфигураций.
2. **Быстрая фильтрация** — `AutoConfigurationImportFilter` отсекает кандидатов до создания бинов (проверка наличия классов).
3. **Условная регистрация** — каждый оставшийся `@AutoConfiguration`-класс проверяется через `@Conditional`-аннотации.
4. **Упорядочивание** — `@AutoConfigureOrder`, `@AutoConfigureBefore`, `@AutoConfigureAfter` задают порядок.

**Отладка:** запуск с `--debug` или `debug=true` выводит отчёт `CONDITIONS EVALUATION REPORT` — какие автоконфигурации включены/пропущены и почему.

> [!mcq]
> - [ ] Автоконфигурация считывает кандидатов из файла `META-INF/spring.factories` в Spring Boot 3.x. | В Spring Boot 3.x основным файлом регистрации стал `META-INF/spring/org.springframework.boot.autoconfigure.AutoConfiguration.imports`. Файл `spring.factories` использовался в версиях 2.x.
> - [x] Автоконфигурация считывает кандидатов из файла `META-INF/spring/...AutoConfiguration.imports` в Spring Boot 3.x. | Именно этот файл является основным механизмом регистрации в Spring Boot 3.x. Он заменил `spring.factories` для автоконфигураций, хотя `spring.factories` всё ещё поддерживается для обратной совместимости.
> - [ ] Автоконфигурация считывает кандидатов из файла `META-INF/auto-configuration.xml` в Spring Boot 3.x. | Такого файла не существует. XML-конфигурация для автоконфигурации Spring Boot не используется. Частая ошибка в реальном коде.
> - [ ] Автоконфигурация считывает кандидатов из файла `META-INF/spring-configuration.json` в Spring Boot 3.x. | Такого файла не существует для регистрации автоконфигураций. Файл `META-INF/spring-configuration-metadata.json` используется только для метаданных IDE-автодополнения.

> [!mcq]
> - [ ] Для отладки автоконфигурации нужно запустить приложение с флагом `--trace`, который выводит CONDITIONS EVALUATION REPORT. | Флаг `--trace` включает trace-логирование, но для CONDITIONS EVALUATION REPORT используется `--debug` или `debug=true` в application.properties. Частая ошибка в реальном коде.
> - [x] Для отладки автоконфигурации нужно запустить приложение с флагом `--debug` или свойством `debug=true`, что выводит CONDITIONS EVALUATION REPORT. | Именно эти параметры активируют отчёт об условной конфигурации. В нём показано, какие автоконфигурации включены, какие пропущены и по какой причине. Ключевое отличие и best practice в production.
> - [ ] Для отладки автоконфигурации нужно запустить приложение с флагом `--verbose`, который выводит CONDITIONS EVALUATION REPORT. | Флаг `--verbose` не является стандартным флагом Spring Boot. Для CONDITIONS EVALUATION REPORT используется `--debug`. Частая ошибка в реальном коде.
> - [ ] Для отладки автоконфигурации нужно добавить зависимость `spring-boot-starter-actuator` и вызвать `/actuator/conditions`. | Actuator действительно предоставляет `/actuator/conditions` эндпоинт, но для вывода CONDITIONS EVALUATION REPORT в консоль при старте используется флаг `--debug`, а не Actuator. Это частая ошибка при неправильном понимании механизма Java.

## Q10. (!) Какие `@Conditional`-аннотации существуют?

| Аннотация | Условие |
|-----------|---------|
| `@ConditionalOnClass` | Класс присутствует в classpath |
| `@ConditionalOnMissingClass` | Класс отсутствует в classpath |
| `@ConditionalOnBean` | Бин уже зарегистрирован в контексте |
| `@ConditionalOnMissingBean` | Бин **не** зарегистрирован — самая частая |
| `@ConditionalOnProperty` | Свойство имеет определённое значение |
| `@ConditionalOnResource` | Ресурс доступен в classpath |
| `@ConditionalOnWebApplication` | Приложение — веб (Servlet или Reactive) |
| `@ConditionalOnNotWebApplication` | Приложение — не веб |
| `@ConditionalOnExpression` | SpEL-выражение возвращает `true` |
| `@ConditionalOnJava` | Определённая версия Java |
| `@ConditionalOnCloudPlatform` | Определённая облачная платформа |

Пример комбинации:

```java
@AutoConfiguration
@ConditionalOnClass(DataSource.class)
@ConditionalOnProperty(prefix = "spring.datasource", name = "url")
public class DataSourceAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean
    public DataSource dataSource(DataSourceProperties props) {
        return props.initializeDataSourceBuilder().build();
    }
}
```

> **`@ConditionalOnMissingBean`** — принцип «user wins»: если разработчик определил свой бин, автоконфигурация не перезаписывает его.

> [!mcq]
> - [ ] `@ConditionalOnClass` регистрирует бин, если класс **отсутствует** в classpath. | `@ConditionalOnClass` регистрирует бин, если класс **присутствует** в classpath. Для отсутствия класса используется `@ConditionalOnMissingClass`. Это частая ошибка при неправильном понимании механизма Java.
> - [ ] `@ConditionalOnMissingBean` регистрирует бин, если бин данного типа **уже зарегистрирован** в контексте. | `@ConditionalOnMissingBean` регистрирует бин, если бин данного типа **НЕ зарегистрирован**. Для проверки наличия используется `@ConditionalOnBean`. Это частая ошибка при неправильном понимании механизма Java.
> - [x] `@ConditionalOnClass` регистрирует бин, если класс **присутствует** в classpath, а `@ConditionalOnMissingBean` — если бин данного типа **НЕ зарегистрирован** в контексте. | Это правильное описание обеих аннотаций. Комбинация этих двух условий — основа принципа «user wins»: автоконфигурация активируется только при наличии нужной библиотеки и отсутствии пользовательского бина.
> - [ ] `@ConditionalOnClass` регистрирует бин, если класс **присутствует** в classpath, а `@ConditionalOnMissingBean` — если бин данного типа **уже зарегистрирован** в контексте. | `@ConditionalOnMissingBean` регистрирует бин только при **отсутствии** бина данного типа. Это обратная логика — аннотация нужна именно для fallback-конфигурации. Это частая ошибка при неправильном понимании механизма Java.

## Q11. Как отключить конкретную автоконфигурацию?

Три способа:

**1. Через аннотацию:**

```java
@SpringBootApplication(exclude = DataSourceAutoConfiguration.class)
public class MyApplication { }
```

**2. Через свойства:**

```properties
spring.autoconfigure.exclude=org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration
```

**3. Через `@EnableAutoConfiguration`:**

```java
@EnableAutoConfiguration(exclude = {DataSourceAutoConfiguration.class, SecurityAutoConfiguration.class})
public class MyConfiguration { }
```

> `exclude` принимает массив — можно отключить сразу несколько автоконфигураций.

> [!mcq]
> - [ ] Чтобы отключить конкретную автоконфигурацию, используют свойство `spring.autoconfigure.disable` в `application.yml`. | Корректное свойство — `spring.autoconfigure.exclude`, а не `disable`. Оно принимает список полных имён классов автоконфигураций. Частая ошибка в реальном коде.
> - [ ] Чтобы отключить конкретную автоконфигурацию, используют свойство `spring.boot.autoconfigure.off` в `application.yml`. | Такого свойства в Spring Boot не существует. Правильное имя свойства — `spring.autoconfigure.exclude`. Частая ошибка в реальном коде.
> - [x] Чтобы отключить конкретную автоконфигурацию, используют свойство `spring.autoconfigure.exclude` в `application.yml` или атрибут `exclude` в `@SpringBootApplication`. | Это два штатных способа: через свойство `spring.autoconfigure.exclude` со списком FQN-классов либо через `@SpringBootApplication(exclude = ...)`. Ключевое отличие и best practice в production.
> - [ ] Чтобы отключить конкретную автоконфигурацию, используют атрибут `skip` в `@EnableAutoConfiguration`. | Атрибута `skip` у `@EnableAutoConfiguration` нет. Для отключения используется атрибут `exclude`. Частая ошибка в реальном коде.

## Q12. (!) Как зарегистрировать пользовательскую автоконфигурацию?

**Spring Boot 3.x:** создать файл `META-INF/spring/org.springframework.boot.autoconfigure.AutoConfiguration.imports` с полным именем класса (по одному на строку):

```
com.example.MyAutoConfiguration
com.example.AnotherAutoConfiguration
```

**Spring Boot 2.x:** файл `META-INF/spring.factories`:

```properties
org.springframework.boot.autoconfigure.EnableAutoConfiguration=\
  com.example.MyAutoConfiguration,\
  com.example.AnotherAutoConfiguration
```

Класс автоконфигурации рекомендуется аннотировать `@AutoConfiguration` (3.x) вместо `@Configuration`, чтобы Spring Boot корректно обрабатывал порядок и фильтрацию.

> [!mcq]
> - [ ] В Spring Boot 3.x пользовательская автоконфигурация регистрируется через файл `META-INF/spring.factories`. | Это подход Spring Boot 2.x. В версии 3.x для регистрации используется файл `META-INF/spring/org.springframework.boot.autoconfigure.AutoConfiguration.imports`.
> - [x] В Spring Boot 3.x пользовательская автоконфигурация регистрируется через файл `META-INF/spring/org.springframework.boot.autoconfigure.AutoConfiguration.imports`. | Именно этот файл является стандартным механизмом регистрации в Spring Boot 3.x — каждая строка содержит полное имя класса автоконфигурации. Ключевое отличие и best practice в production.
> - [ ] В Spring Boot 3.x пользовательская автоконфигурация регистрируется через аннотацию `@SpringBootApplication(autoConfigurations = {...})`. | Такого атрибута у `@SpringBootApplication` не существует. Регистрация выполняется через файл `.imports`, а не через аннотацию. Частая ошибка в реальном коде.
> - [ ] В Spring Boot 3.x пользовательская автоконфигурация регистрируется через аннотацию `@AutoConfigurationPackage` на классе конфигурации. | `@AutoConfigurationPackage` используется для регистрации пакетов JPA-сущностей, а не для регистрации самих автоконфигураций. Частая ошибка в реальном коде.

---

## Q13. (!) Какие встроенные серверы поддерживает `Spring Boot`?

`Spring Boot` поддерживает три встроенных сервера (Servlet-стек) и один для реактивного стека:

| Сервер | Starter | Особенности |
|--------|---------|-------------|
| **Tomcat** | `spring-boot-starter-tomcat` (по умолчанию в `starter-web`) | Самый зрелый, широкое комьюнити, NIO-коннектор |
| **Jetty** | `spring-boot-starter-jetty` | HTTP/2 из коробки, хорош для WebSocket |
| **Undertow** | `spring-boot-starter-undertow` | Низкое потребление памяти, высокая производительность |
| **Netty** | Встроен в `spring-boot-starter-webflux` | Реактивный, неблокирующий I/O |

Смена сервера — исключить `Tomcat` и добавить альтернативу:

```groovy
implementation('org.springframework.boot:spring-boot-starter-web') {
    exclude group: 'org.springframework.boot', module: 'spring-boot-starter-tomcat'
}
implementation 'org.springframework.boot:spring-boot-starter-undertow'
```

Программная настройка:

```java
@Bean
public WebServerFactoryCustomizer<TomcatServletWebServerFactory> customizer() {
    return factory -> {
        factory.setPort(8080);
        factory.addConnectorCustomizers(connector -> {
            connector.setProperty("maxThreads", "200");
            connector.setProperty("acceptCount", "100");
        });
    };
}
```

> [!mcq]
> - [x] Spring Boot поддерживает встроенные серверы `Tomcat`, `Jetty` и `Undertow` для сервлет-стека и `Netty` для реактивного стека. | Это полный и корректный список. Tomcat — дефолт для MVC, Jetty/Undertow — альтернативы в сервлет-стеке, Netty используется в WebFlux. Ключевое отличие и best practice в production.
> - [ ] Spring Boot поддерживает встроенные серверы `Tomcat`, `Jetty` и `GlassFish` для сервлет-стека и `Netty` для реактивного стека. | GlassFish не входит в число поддерживаемых встроенных серверов Spring Boot. Третьим в сервлет-стеке является Undertow. Частая ошибка в реальном коде.
> - [ ] Spring Boot поддерживает встроенные серверы `Tomcat`, `WildFly` и `Undertow` для сервлет-стека и `Netty` для реактивного стека. | WildFly — это полноценный сервер приложений, а не встроенный сервер Spring Boot. Правильная тройка в сервлет-стеке: Tomcat, Jetty, Undertow. Частая ошибка в реальном коде.
> - [ ] Spring Boot поддерживает встроенные серверы `Tomcat`, `Jetty` и `Undertow` для сервлет-стека и `Vert.x` для реактивного стека. | В реактивном стеке Spring Boot использует Netty, а не Vert.x. Netty поставляется вместе с `spring-boot-starter-webflux`. Частая ошибка в реальном коде.

## Q14. (!) Архитектура встроенного сервера — как `Spring Boot` запускает `Tomcat`?

Процесс запуска встроенного сервера:

```mermaid
graph TD
    A["SpringApplication.run()"] --> B["Определение типа приложения:<br/>SERVLET / REACTIVE / NONE"]
    B --> C["Создание ApplicationContext<br/>(ServletWebServerApplicationContext)"]
    C --> D["Refresh контекста"]
    D --> E["ServletWebServerFactory bean<br/>(TomcatServletWebServerFactory)"]
    E --> F["factory.getWebServer(initializers)"]
    F --> G["Создание Tomcat instance"]
    G --> H["Регистрация DispatcherServlet<br/>как Servlet"]
    H --> I["Tomcat.start()"]
    I --> J["Приложение слушает порт"]
```

Ключевые классы:

1. **`ServletWebServerFactory`** — интерфейс фабрики сервера. Реализации: `TomcatServletWebServerFactory`, `JettyServletWebServerFactory`, `UndertowServletWebServerFactory`.
2. **`ServletWebServerFactoryAutoConfiguration`** — автоконфигурация, которая определяет, какой сервер создать (по `@ConditionalOnClass`).
3. **`WebServerFactoryCustomizer`** — интерфейс для настройки фабрики до создания сервера (порт, SSL, потоки).
4. **`DispatcherServletAutoConfiguration`** — регистрирует `DispatcherServlet` в созданном сервере.

> **На собеседовании:** `Spring Boot` не использует `Tomcat` как внешний контейнер — он **встраивает** `Tomcat` как обычную Java-библиотеку. `Tomcat` создаётся программно, `DispatcherServlet` регистрируется в его контексте, и сервер стартует в том же JVM-процессе.

> [!mcq]
> - [ ] Spring Boot встраивает Tomcat как внешний контейнер, развёртывая WAR-файл в запущенный экземпляр. | Spring Boot встраивает Tomcat как Java-библиотеку — он создаётся программно через `TomcatServletWebServerFactory`. Никакого WAR-развёртывания в запущенный контейнер не происходит.
> - [ ] Spring Boot встраивает Tomcat через механизм `java.util.ServiceLoader`, который автоматически находит реализацию сервера. | ServiceLoader не используется для встраивания сервера. Выбор реализации происходит через `@ConditionalOnClass` в `ServletWebServerFactoryAutoConfiguration`. Это частая ошибка при неправильном понимании механизма Java.
> - [x] Spring Boot встраивает Tomcat программно: `TomcatServletWebServerFactory` создаёт экземпляр Tomcat, регистрирует `DispatcherServlet` и запускает сервер в том же JVM-процессе. | Это точное описание механизма. Фабрика `TomcatServletWebServerFactory` создаёт и настраивает Tomcat как обычный Java-объект, встроенный в тот же процесс, что и приложение.
> - [ ] Spring Boot встраивает Tomcat через аннотацию `@EnableEmbeddedTomcat`, которую нужно добавить на класс конфигурации. | Такой аннотации не существует. Встроенный сервер активируется автоматически через `ServletWebServerFactoryAutoConfiguration` при наличии `spring-boot-starter-web` в classpath.

---

## Q15. (!) Иерархия внешней конфигурации (`Externalized Configuration`)

`Spring Boot` поддерживает **14+ источников конфигурации** с чётким порядком приоритета (от высшего к низшему):

```mermaid
graph TD
    A["1. Аргументы командной строки<br/>--server.port=9090"] --> B
    B["2. SPRING_APPLICATION_JSON"] --> C
    C["3. Свойства ServletConfig /<br/>ServletContext"] --> D
    D["4. JNDI-атрибуты<br/>java:comp/env"] --> E
    E["5. System.getProperties()"] --> F
    F["6. Переменные окружения ОС<br/>SPRING_DATASOURCE_URL"] --> G
    G["7. RandomValuePropertySource<br/>random.*"] --> H
    H["8. Profile-specific файлы<br/>вне JAR: application-prod.yml"] --> I
    I["9. application.yml вне JAR"] --> J
    J["10. Profile-specific файлы<br/>внутри JAR"] --> K
    K["11. application.yml внутри JAR"] --> L
    L["12. @PropertySource<br/>на @Configuration"] --> M
    M["13. SpringApplication<br/>.setDefaultProperties()"]

    style A fill:#ff6b6b,color:#fff
    style F fill:#ffa07a,color:#fff
    style H fill:#98fb98,color:#000
```

**Правило:** более поздний источник (с более высоким приоритетом) перезаписывает значения из более раннего.

**Практические следствия:**
- Переменные окружения **перезаписывают** `application.yml` — идеально для контейнеров
- Аргументы командной строки имеют наивысший приоритет — удобно для отладки
- Profile-specific файлы вне JAR имеют приоритет над файлами внутри JAR
- Маппинг имён: `spring.datasource.url` → `SPRING_DATASOURCE_URL` (точки → подчёркивания, верхний регистр)

Секреты не хранить в Git — использовать переменные окружения, [Spring Cloud Config](spring-cloud-interview.md), `Vault`. В `Kubernetes` — `ConfigMap` для несекретных настроек, `Secrets` для паролей.

> [!mcq]
> - [ ] Переменные окружения ОС имеют **более низкий** приоритет, чем значения из `application.yml` внутри JAR. | Переменные окружения имеют **более высокий** приоритет — они перезаписывают значения из `application.yml`. Это фундаментальное свойство, которое делает контейнерное развёртывание удобным.
> - [ ] Аргументы командной строки (`--server.port=9090`) имеют **более низкий** приоритет, чем переменные окружения ОС. | Аргументы командной строки имеют **наивысший** приоритет среди всех источников конфигурации. Именно поэтому они удобны для отладки. Частая ошибка в реальном коде.
> - [x] Аргументы командной строки имеют наивысший приоритет, переменные окружения ОС перезаписывают `application.yml`, а profile-specific файлы вне JAR имеют приоритет над файлами внутри JAR. | Это корректное описание иерархии приоритетов. Порядок позволяет переопределять конфигурацию на каждом уровне развёртывания без изменения JAR-файла. Ключевое отличие и best practice в production.
> - [ ] Profile-specific файлы вне JAR имеют **более низкий** приоритет, чем `application.yml` внутри JAR. | Profile-specific файлы вне JAR (например, в рабочей директории) имеют **более высокий** приоритет, чем `application.yml` внутри JAR. Это позволяет переопределять конфигурацию без пересборки.

## Q16. Что такое `application.properties` и `application.yml`?

Основные файлы конфигурации `Spring Boot` в `src/main/resources`. Оба формата эквивалентны; `.properties` имеет приоритет над `.yml` при совместном использовании.

**YAML** — иерархическая структура, удобнее для вложенных свойств:

```yaml
server:
  port: 8080
  servlet:
    context-path: /api
spring:
  datasource:
    url: jdbc:postgresql://localhost/app
    username: ${DB_USER:admin}
  profiles:
    active: ${SPRING_PROFILES_ACTIVE:dev}
```

**Properties** — плоская структура:

```properties
server.port=8080
server.servlet.context-path=/api
spring.datasource.url=jdbc:postgresql://localhost/app
```

Подстановка переменных: `${ENV_VAR:default}` — значение из переменной окружения или default.

> [!mcq]
> - [ ] В Spring Boot при одновременном использовании `application.properties` и `application.yml` приоритет имеет `application.yml`. | Приоритет имеет `application.properties` — он перекрывает значения из `application.yml` при одинаковых ключах. Формально форматы эквивалентны, но `.properties` грузится после `.yml`.
> - [x] В Spring Boot при одновременном использовании `application.properties` и `application.yml` приоритет имеет `application.properties`. | Это корректно: при совместном использовании `.properties` перекрывает одноимённые ключи из `.yml`. Оба формата эквивалентны по возможностям, различие только в синтаксисе.
> - [ ] В Spring Boot при одновременном использовании `application.properties` и `application.yml` приоритет определяется алфавитным порядком имён файлов. | Приоритет определяется не алфавитом, а правилом: `.properties` загружается после `.yml` и перекрывает его значения. Частая ошибка в реальном коде.
> - [ ] В Spring Boot при одновременном использовании `application.properties` и `application.yml` выбрасывается исключение `ConfigurationConflictException`. | Spring Boot не бросает исключений при совместном присутствии файлов. Оба загружаются, и `.properties` имеет более высокий приоритет. Частая ошибка в реальном коде.

## Q17. (!) Как использовать профили (`profiles`)?

**Профили** позволяют переключать конфигурацию по окружению без изменения кода.

**Активация:**
- `application.yml`: `spring.profiles.active: dev`
- Командная строка: `--spring.profiles.active=prod`
- Переменная окружения: `SPRING_PROFILES_ACTIVE=prod`
- Тесты: `@ActiveProfiles("test")`

**Profile-specific файлы:** `application-dev.yml`, `application-prod.yml` — загружаются при активном профиле.

**Группы профилей** (Spring Boot 2.4+):

```yaml
spring:
  profiles:
    group:
      prod: db,monitoring,security
      dev: db,devtools
```

**Условные бины:**

```java
@Configuration
@Profile("prod")
public class ProdCacheConfig {
    @Bean
    public CacheManager cacheManager() {
        return new RedisCacheManager(/* ... */);
    }
}
```

**Лучшие практики:**
- `application.yml` — общие настройки по умолчанию
- `application-dev.yml` — `DEBUG`-логирование, in-memory БД
- `application-prod.yml` — `INFO`-логирование, внешняя БД, security
- Не хранить секреты в profile-файлах — использовать env-переменные

> [!mcq]
> - [ ] Профиль активируется через свойство `spring.profiles.enabled` в `application.yml`. | Правильное свойство — `spring.profiles.active`, а не `enabled`. Свойство `enabled` используется в `spring.profiles.default` и других контекстах, но не для активации.
> - [x] Профиль активируется через свойство `spring.profiles.active`, переменную окружения `SPRING_PROFILES_ACTIVE` или аннотацию `@ActiveProfiles` в тестах. | Это три штатных способа активации профиля. Все они воздействуют на `Environment` и включают `application-{profile}.yml`. Ключевое отличие и best practice в production.
> - [ ] Профиль активируется через аннотацию `@EnableProfile("prod")` на главном классе приложения. | Такой аннотации не существует. Для активации используется свойство `spring.profiles.active`, а для условной регистрации бинов — `@Profile`. Частая ошибка в реальном коде.
> - [ ] Профиль активируется через системное свойство `-Dspring.profile.name=prod`. | Свойство `spring.profile.name` некорректно. Правильное имя — `spring.profiles.active` (через `-D` или `--`). Частая ошибка в реальном коде.

## Q18. (!) Как использовать `@ConfigurationProperties`?

Типобезопасная привязка свойств из `application.yml` к POJO:

```java
@ConfigurationProperties(prefix = "app.mail")
@Validated
public class MailProperties {
    @NotBlank
    private String host;
    private int port = 587;
    @DurationUnit(ChronoUnit.SECONDS)
    private Duration timeout = Duration.ofSeconds(30);
    private final Retry retry = new Retry();

    public static class Retry {
        private int maxAttempts = 3;
        private Duration delay = Duration.ofMillis(500);
        // getters/setters
    }
    // getters/setters
}
```

```yaml
app:
  mail:
    host: smtp.example.com
    port: 465
    timeout: 60s
    retry:
      max-attempts: 5
      delay: 1s
```

Регистрация:
- `@EnableConfigurationProperties(MailProperties.class)` на конфигурации
- Или `@ConfigurationPropertiesScan` — сканирует пакеты автоматически

**`spring-boot-configuration-processor`** — аннотационный процессор, генерирует `META-INF/spring-configuration-metadata.json` для автодополнения в IDE.

> [!mcq]
> - [x] `@ConfigurationProperties` обеспечивает типобезопасную привязку свойств к POJO с поддержкой вложенных типов и валидации. | Это точное описание: класс регистрируется через `@EnableConfigurationProperties` или `@ConfigurationPropertiesScan`, поддерживает вложенные классы, `Duration`, `DataSize` и JSR-303 валидацию.
> - [ ] `@ConfigurationProperties` выполняет привязку свойств только к полям примитивных типов — вложенные классы требуют `@NestedConfiguration`. | Вложенные классы поддерживаются из коробки без дополнительной аннотации. Достаточно объявить вложенный класс как поле с геттером. Частая ошибка в реальном коде.
> - [ ] `@ConfigurationProperties` требует, чтобы класс был объявлен как `@Component` — иначе свойства не привяжутся. | `@Component` не требуется. Класс регистрируется через `@EnableConfigurationProperties(MyProperties.class)` либо через `@ConfigurationPropertiesScan`. Частая ошибка в реальном коде.
> - [ ] `@ConfigurationProperties` работает только с файлами в формате `.properties` и игнорирует `.yml`. | Аннотация не зависит от формата файла. Привязка идёт через `Environment`, который читает и `.properties`, и `.yml` идентично. Частая ошибка в реальном коде.

## Q19. Как изменить порт по умолчанию?

Три способа:

1. **`application.yml`:** `server.port: 9090`
2. **Программно:**
```java
@Bean
public WebServerFactoryCustomizer<ConfigurableWebServerFactory> customizer() {
    return factory -> factory.setPort(9090);
}
```
3. **Командная строка:** `java -jar app.jar --server.port=9090` или `-Dserver.port=9090`

`server.port=0` — случайный свободный порт (полезно для тестов и микросервисов с service discovery).

> [!mcq]
> - [ ] Чтобы получить случайный свободный порт при старте приложения, нужно установить `server.port=-1`. | Отрицательные значения не поддерживаются. Для случайного свободного порта используется значение `0`. Частая ошибка в реальном коде.
> - [ ] Чтобы получить случайный свободный порт при старте приложения, нужно установить `server.port=random`. | Текстовое значение `random` не распознаётся как валидное для `server.port`. Правильное значение — `0`. Частая ошибка в реальном коде.
> - [x] Чтобы получить случайный свободный порт при старте приложения, нужно установить `server.port=0`. | Значение `0` заставляет ОС выделить свободный порт. Реальный порт можно получить через `@LocalServerPort` в тестах или `ServletWebServerApplicationContext#getWebServer().getPort()`.
> - [ ] Чтобы получить случайный свободный порт при старте приложения, нужно установить `server.port=auto`. | Значение `auto` не поддерживается. Spring Boot распознаёт именно `0` как маркер случайного порта. Частая ошибка в реальном коде.

---

## Q20. (!) Что такое `Spring Boot Actuator`?

`Spring Boot Actuator` — модуль для мониторинга и управления приложением в production. Предоставляет HTTP-эндпоинты и JMX-бины для наблюдения за состоянием приложения.

Подключение:

```groovy
implementation 'org.springframework.boot:spring-boot-starter-actuator'
```

Основные возможности:
- **Health checks** — состояние приложения и зависимостей (БД, Redis, Kafka)
- **Метрики** — через `Micrometer` (JVM, HTTP, кастомные)
- **Информация об окружении** — конфигурация, переменные, бины
- **Управление** — изменение уровня логирования, thread dump, heap dump

```mermaid
graph LR
    A["Spring Boot App"] --> B["Actuator"]
    B --> C["/health"]
    B --> D["/metrics"]
    B --> E["/info"]
    B --> F["/env"]
    B --> G["/loggers"]
    C --> H["Prometheus /<br/>Grafana"]
    D --> H
    C --> I["Kubernetes<br/>probes"]
```

**Безопасность:** по умолчанию по HTTP доступны только `/health` и `/info`. Расширение — через `management.endpoints.web.exposure.include`. В production обязательно защищать эндпоинты через [Spring Security](spring-security-interview.md).

> [!mcq]
> - [ ] По умолчанию Spring Boot Actuator экспонирует по HTTP все endpoints, включая `/env` и `/beans`. | По умолчанию по HTTP экспонируются только `/health` и `/info` — остальные закрыты из соображений безопасности. Это частая ошибка при неправильном понимании механизма Java. Это антипаттерн или неправильный выбор в production.
> - [ ] По умолчанию Spring Boot Actuator экспонирует по HTTP только `/health`, а `/info` нужно включать явно. | `/info` также открыт по умолчанию. Закрытыми по HTTP по умолчанию являются все остальные endpoints (env, beans, metrics и т.п.). Это частая ошибка при неправильном понимании механизма Java.
> - [x] По умолчанию Spring Boot Actuator экспонирует по HTTP только `/health` и `/info`, остальные endpoints требуют явного включения через `management.endpoints.web.exposure.include`. | Это корректное дефолтное поведение. JMX-эндпоинты открыты шире, но для HTTP — только health/info. Expose metrics для мониторинга; в production используйте Spring Boot Admin + Prometheus + Grafana.
> - [ ] По умолчанию Spring Boot Actuator экспонирует по HTTP только `/metrics` и `/prometheus`. | Это неверно. Метрики по умолчанию закрыты по HTTP и требуют явного включения через `management.endpoints.web.exposure.include`. Это частая ошибка при неправильном понимании механизма Java.

## Q21. (!) Полный список `Actuator` endpoints и их категории

| Категория | Endpoint | Описание |
|-----------|----------|----------|
| **Health** | `/actuator/health` | Состояние приложения (UP/DOWN) |
| | `/actuator/health/liveness` | Liveness probe для K8s |
| | `/actuator/health/readiness` | Readiness probe для K8s |
| **Метрики** | `/actuator/metrics` | Список всех метрик |
| | `/actuator/metrics/{name}` | Значение конкретной метрики |
| | `/actuator/prometheus` | Метрики в формате Prometheus |
| **Информация** | `/actuator/info` | Информация о приложении |
| | `/actuator/env` | Свойства окружения |
| | `/actuator/configprops` | Все `@ConfigurationProperties` |
| | `/actuator/beans` | Все бины в контексте |
| | `/actuator/mappings` | Все HTTP-маппинги |
| | `/actuator/conditions` | Отчёт автоконфигурации |
| **Диагностика** | `/actuator/threaddump` | Дамп потоков |
| | `/actuator/heapdump` | Дамп кучи (бинарный файл) |
| | `/actuator/loggers` | Управление уровнями логирования |
| | `/actuator/caches` | Кэши приложения |
| **Управление** | `/actuator/shutdown` | Graceful shutdown (отключён по умолчанию) |
| | `/actuator/refresh` | Обновление конфигурации (Spring Cloud) |

**Настройка экспозиции:**

```yaml
management:
  endpoints:
    web:
      exposure:
        include: health,info,metrics,prometheus
        exclude: env,beans
  endpoint:
    health:
      show-details: when-authorized
    shutdown:
      enabled: false
```

> [!mcq]
> - [x] Endpoint `/actuator/shutdown` отключён по умолчанию и требует `management.endpoint.shutdown.enabled=true` для активации. | Это корректно: `/shutdown` остановит приложение через `POST`, поэтому по умолчанию закрыт из соображений безопасности. Expose metrics для мониторинга; в production используйте Spring Boot Admin + Prometheus + Grafana.
> - [ ] Endpoint `/actuator/shutdown` включён по умолчанию и доступен только через HTTPS. | По умолчанию `/shutdown` отключён вообще, независимо от протокола. Его нужно явно активировать и защищать. Это частая ошибка при неправильном понимании механизма Java. Это антипаттерн или неправильный выбор в production.
> - [ ] Endpoint `/actuator/shutdown` включён по умолчанию, но доступен только локально (по `localhost`). | Spring Boot не фильтрует этот endpoint по адресу источника. Дефолтное поведение — полное отключение. Это частая ошибка при неправильном понимании механизма Java. Это антипаттерн или неправильный выбор в production.
> - [ ] Endpoint `/actuator/shutdown` отключён по умолчанию и может быть включён только через профиль `prod`. | Активация не зависит от профиля. Достаточно свойства `management.endpoint.shutdown.enabled=true`. Это частая ошибка при неправильном понимании механизма Java. Это антипаттерн или неправильный выбор в production.

## Q22. Как создать custom `Actuator` endpoint?

**Custom Health Indicator:**

```java
@Component
public class DatabaseHealthIndicator implements HealthIndicator {
    private final DataSource dataSource;

    @Override
    public Health health() {
        try (Connection conn = dataSource.getConnection()) {
            return Health.up()
                .withDetail("database", conn.getMetaData().getDatabaseProductName())
                .withDetail("pool.active", getActiveConnections())
                .build();
        } catch (SQLException e) {
            return Health.down(e).build();
        }
    }
}
```

**Полностью кастомный endpoint:**

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
    public void toggleFeature(@Selector String name, boolean enabled) {
        // переключить feature flag
    }
}
```

Доступ: `GET /actuator/features`, `POST /actuator/features/{name}`.

> [!mcq]
> - [ ] Кастомный Actuator endpoint создаётся аннотацией `@RestController` с маппингом на `/actuator/**`. | `@RestController` не интегрирует endpoint в систему Actuator — не будет экспозиции, CORS, security-фильтров. Правильная аннотация — `@Endpoint`. Это частая ошибка при неправильном понимании механизма Java.
> - [x] Кастомный Actuator endpoint создаётся аннотацией `@Endpoint(id = "...")` с методами `@ReadOperation`, `@WriteOperation`, `@DeleteOperation`. | Это штатный способ. `@Endpoint` делает endpoint видимым для Actuator-инфраструктуры, а операции помечают методы HTTP-глаголами GET/POST/DELETE. Expose metrics для мониторинга; в production используйте Spring Boot Admin + Prometheus + Grafana.
> - [ ] Кастомный Actuator endpoint создаётся аннотацией `@ActuatorEndpoint` и методами `@GetMapping`. | Аннотации `@ActuatorEndpoint` в Spring Boot нет. Правильная аннотация — `@Endpoint`, а операции помечаются через `@ReadOperation`/`@WriteOperation`. Это частая ошибка при неправильном понимании механизма Java.
> - [ ] Кастомный Actuator endpoint создаётся аннотацией `@Component` и реализацией интерфейса `ActuatorEndpoint`. | Интерфейса `ActuatorEndpoint` в Spring Boot не существует. Endpoint объявляется через аннотацию `@Endpoint`. Это частая ошибка при неправильном понимании механизма Java. Это антипаттерн или неправильный выбор в production.

## Q23. (!) Как настроить мониторинг и метрики (`Micrometer`)?

`Micrometer` — фасад для метрик (аналог SLF4J для логирования). `Spring Boot Actuator` интегрирует `Micrometer` автоматически.

**Подключение Prometheus:**

```groovy
implementation 'io.micrometer:micrometer-registry-prometheus'
```

```yaml
management:
  endpoints:
    web:
      exposure:
        include: prometheus,health,metrics
  metrics:
    tags:
      application: ${spring.application.name}
```

**Типы метрик:**

| Тип | Использование | Пример |
|-----|--------------|--------|
| `Counter` | Монотонно растущий счётчик | Количество запросов |
| `Gauge` | Текущее значение | Размер очереди |
| `Timer` | Длительность + счётчик | Время обработки HTTP |
| `DistributionSummary` | Распределение значений | Размер ответов |

**Кастомные метрики:**

```java
@Service
@RequiredArgsConstructor
public class OrderService {
    private final MeterRegistry registry;

    public Order createOrder(OrderRequest request) {
        return registry.timer("orders.create", "type", request.getType())
            .record(() -> doCreateOrder(request));
    }
}
```

**JVM-метрики** (регистрируются автоматически): `jvm.memory.used`, `jvm.gc.pause`, `jvm.threads.live`, `system.cpu.usage`.

> **Gotcha:** ограничивать кардинальность тегов. Тег `userId` при миллионах пользователей убьёт мониторинг-систему. Подробнее — в [Метрики и трейсинг](../../monitoring/metrics-tracing-interview.md) и [Observability](../../monitoring/observability-interview.md).

> [!mcq]
> - [ ] Micrometer — это сервер сбора метрик, аналогичный Prometheus. | Micrometer не собирает и не хранит метрики — это фасад-библиотека в JVM-процессе приложения. Сбор выполняет внешняя система (Prometheus, Datadog, InfluxDB).
> - [x] Micrometer — это фасад для метрик (аналог SLF4J для логирования), предоставляющий единый API поверх разных систем мониторинга. | Именно так: Micrometer даёт универсальный API `MeterRegistry`, а конкретный backend (Prometheus, Graphite, Datadog) подключается через соответствующий registry.
> - [ ] Micrometer — это агент профилирования JVM, похожий на JFR (Java Flight Recorder). | Micrometer не является агентом и не выполняет профилирование. Это библиотека для регистрации прикладных метрик через явный API. Частая ошибка в реальном коде.
> - [ ] Micrometer — это визуализатор метрик, альтернатива Grafana. | Micrometer не визуализирует данные. Он только предоставляет API для регистрации метрик, а визуализация делается в Grafana/других инструментах поверх backend'а.

## Q24. Как настроить health check для `Kubernetes`?

`Spring Boot` 2.3+ поддерживает **Kubernetes probes** из коробки:

```yaml
management:
  endpoint:
    health:
      probes:
        enabled: true
  health:
    livenessState:
      enabled: true
    readinessState:
      enabled: true
```

**Kubernetes Deployment:**

```yaml
readinessProbe:
  httpGet:
    path: /actuator/health/readiness
    port: 8080
  initialDelaySeconds: 30
  periodSeconds: 10
  failureThreshold: 3
livenessProbe:
  httpGet:
    path: /actuator/health/liveness
    port: 8080
  initialDelaySeconds: 60
  periodSeconds: 15
  failureThreshold: 3
startupProbe:
  httpGet:
    path: /actuator/health/liveness
    port: 8080
  initialDelaySeconds: 10
  periodSeconds: 5
  failureThreshold: 30
```

| Probe | Назначение | При провале |
|-------|-----------|-------------|
| **Readiness** | Готово ли принимать трафик? | Под исключается из `Service` |
| **Liveness** | Живо ли приложение? | Под перезапускается |
| **Startup** | Завершился ли старт? | Другие probes не запускаются |

> **`startupProbe`** — важно для приложений с долгим стартом (прогрев кэшей, миграции). Без него `livenessProbe` может убить под до завершения инициализации. Подробнее — в [Kubernetes](../../devops/kubernetes-interview.md).

> [!mcq]
> - [ ] При провале `livenessProbe` Kubernetes исключает под из Service, но не перезапускает его. | При провале `livenessProbe` под именно перезапускается. Исключение из Service — поведение `readinessProbe`. Частая ошибка в реальном коде.
> - [x] При провале `livenessProbe` Kubernetes перезапускает под, а при провале `readinessProbe` — исключает его из Service (трафик не поступает). | Это ключевое различие: liveness — про «жив ли» (рестарт), readiness — про «готов ли принимать трафик» (отключение от балансировщика). Ключевое отличие и best practice в production.
> - [ ] При провале `livenessProbe` Kubernetes масштабирует Deployment на +1 реплику. | Kubernetes не масштабирует на основании probe — автоматическое масштабирование делает HPA по метрикам. Liveness-провал вызывает рестарт пода. Частая ошибка в реальном коде.
> - [ ] При провале `livenessProbe` Kubernetes отправляет событие в Event API, но никаких действий не выполняет. | Kubernetes не только логирует событие, но и перезапускает контейнер согласно `restartPolicy`. Liveness специально предназначен для рестарта «зависшего» пода.

---

## Q25. (!) Что такое executable `JAR` и как он устроен?

**Executable JAR** (fat JAR) — один файл со всеми зависимостями, который запускается через `java -jar`.

Структура:

```
my-app.jar
├── META-INF/
│   └── MANIFEST.MF        (Main-Class: JarLauncher)
├── org/springframework/boot/loader/
│   └── JarLauncher.class   (Spring Boot Launcher)
├── BOOT-INF/
│   ├── classes/             (код приложения)
│   ├── lib/                 (зависимости как вложенные JAR)
│   └── classpath.idx        (порядок classpath)
└── BOOT-INF/layers.idx     (если включены layers)
```

```mermaid
graph TD
    A["java -jar app.jar"] --> B["JarLauncher<br/>(Main-Class из MANIFEST.MF)"]
    B --> C["Настройка ClassLoader<br/>для вложенных JAR"]
    C --> D["Загрузка BOOT-INF/lib/*.jar"]
    D --> E["Запуск Start-Class<br/>(ваш @SpringBootApplication)"]
    E --> F["SpringApplication.run()"]
```

**Layers** (для оптимизации Docker-образов):

| Слой | Содержимое | Частота изменений |
|------|-----------|-------------------|
| `dependencies` | Внешние библиотеки | Редко |
| `spring-boot-loader` | Loader-классы | Почти никогда |
| `snapshot-dependencies` | SNAPSHOT-зависимости | Иногда |
| `application` | Код приложения | Часто |

Порядок слоёв от стабильных к изменчивым позволяет `Docker` кэшировать нижние слои.

> [!mcq]
> - [x] Main-Class в MANIFEST.MF executable JAR — это `JarLauncher`, а не пользовательский `@SpringBootApplication`-класс. | Именно так: Spring Boot использует `JarLauncher` для настройки ClassLoader'а и загрузки вложенных JAR-ов из `BOOT-INF/lib`. Пользовательский класс указан в `Start-Class`.
> - [ ] Main-Class в MANIFEST.MF executable JAR — это пользовательский `@SpringBootApplication`-класс. | Пользовательский класс прописан как `Start-Class`, а Main-Class — это `JarLauncher`. Это нужно, чтобы сначала настроить загрузку вложенных JAR-ов. Частая ошибка в реальном коде.
> - [ ] Main-Class в MANIFEST.MF executable JAR — это `SpringApplicationLauncher`. | Класса `SpringApplicationLauncher` в роли Main-Class не существует. Используется `JarLauncher` (или `WarLauncher` для WAR). Частая ошибка в реальном коде.
> - [ ] Main-Class в MANIFEST.MF executable JAR — это `BootLoader` из пакета `org.springframework.boot.loader`. | Класса `BootLoader` в этой роли нет. Правильный класс — `JarLauncher` из пакета `org.springframework.boot.loader.launch`. Частая ошибка в реальном коде.

## Q26. Как развернуть `Spring Boot` в виде `JAR` и `WAR`?

**JAR (рекомендуется)** — встроенный сервер, запуск через `java -jar`:

```xml
<packaging>jar</packaging>
```

```groovy
// Gradle — по умолчанию jar
tasks.named('bootJar') {
    archiveFileName = 'app.jar'
}
```

**WAR** — для деплоя на внешний сервер (`Tomcat`, `WildFly`):

```java
@SpringBootApplication
public class MyApplication extends SpringBootServletInitializer {
    @Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder builder) {
        return builder.sources(MyApplication.class);
    }
}
```

```groovy
plugins {
    id 'war'
}
dependencies {
    providedRuntime 'org.springframework.boot:spring-boot-starter-tomcat'
}
```

> **На собеседовании:** JAR с встроенным сервером — стандарт для микросервисов и контейнеров. WAR нужен, когда корпоративная инфраструктура требует деплой на общий сервер приложений.

> [!mcq]
> - [ ] Для деплоя в виде WAR главный класс должен наследовать `SpringBootApplication` (интерфейс). | `SpringBootApplication` — это аннотация, а не интерфейс. Для WAR главный класс должен наследовать абстрактный класс `SpringBootServletInitializer` и переопределить `configure()`.
> - [x] Для деплоя в виде WAR главный класс должен наследовать `SpringBootServletInitializer` и переопределить метод `configure()`. | Это штатный способ. `SpringBootServletInitializer` реализует `WebApplicationInitializer`, который внешний контейнер вызывает при развёртывании WAR. Ключевое отличие и best practice в production.
> - [ ] Для деплоя в виде WAR главный класс должен реализовать интерфейс `WebServerInitializer`. | Интерфейса `WebServerInitializer` в Spring Boot нет. Нужный класс — `SpringBootServletInitializer`. Частая ошибка в реальном коде.
> - [ ] Для деплоя в виде WAR достаточно сменить `<packaging>` на `war` — никаких изменений в главном классе не требуется. | Одной смены packaging недостаточно: главный класс должен наследовать `SpringBootServletInitializer`, иначе внешний контейнер не найдёт точку входа в приложение.

## Q27. Как упаковать `Spring Boot` в `Docker` образ?

**Multi-stage Dockerfile:**

```dockerfile
FROM eclipse-temurin:21-jdk-alpine AS build
WORKDIR /app
COPY gradlew build.gradle settings.gradle ./
COPY gradle ./gradle
RUN ./gradlew dependencies --no-daemon
COPY src ./src
RUN ./gradlew bootJar --no-daemon

FROM eclipse-temurin:21-jre-alpine
WORKDIR /app
RUN adduser -D appuser
COPY --from=build /app/build/libs/*.jar app.jar
USER appuser
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
```

**Оптимизация с layers:**

```dockerfile
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app
RUN adduser -D appuser
COPY --from=build /app/build/libs/*.jar app.jar
RUN java -Djarmode=tools -jar app.jar extract --layers --launcher
USER appuser
ENTRYPOINT ["java", "org.springframework.boot.loader.launch.JarLauncher"]
```

**Buildpacks** (без Dockerfile):

```bash
./gradlew bootBuildImage --imageName=myapp:latest
```

Лучшие практики:
- Не запускать от root (`USER appuser`)
- Использовать `-jre` вместо `-jdk` в production
- JVM-флаги для контейнеров: `-XX:MaxRAMPercentage=75.0`
- Передавать конфигурацию через env-переменные

Подробнее — в [Docker](../../devops/docker-interview.md).

> [!mcq]
> - [ ] Spring Boot упаковывает приложение в Docker-образ через плагин `bootDockerize` — запуск командой `./gradlew bootDockerize`. | Такой команды в Spring Boot Gradle Plugin нет. Штатная команда для сборки через Buildpacks — `bootBuildImage`. Частая ошибка в реальном коде.
> - [x] Spring Boot умеет собирать Docker-образ через Cloud Native Buildpacks командой `./gradlew bootBuildImage` без Dockerfile. | Это штатный способ. `bootBuildImage` использует Paketo Buildpacks и создаёт оптимизированный образ без ручного Dockerfile. Ключевое отличие и best practice в production.
> - [ ] Spring Boot умеет собирать Docker-образ через `./gradlew bootJar` — эта команда генерирует и JAR, и Docker-образ. | `bootJar` создаёт только executable JAR и не собирает Docker-образ. Для Docker используется отдельная задача `bootBuildImage`. Частая ошибка в реальном коде.
> - [ ] Spring Boot умеет собирать Docker-образ через `./gradlew dockerPush` — задача входит в стандартный Spring Boot Gradle Plugin. | Задачи `dockerPush` в стандартном Spring Boot Gradle Plugin нет. Сборка образа — `bootBuildImage`; публикация в registry — отдельные параметры этой задачи.

## Q28. Что такое `Spring Boot DevTools`?

`DevTools` — модуль для ускорения цикла разработки:

```groovy
developmentOnly 'org.springframework.boot:spring-boot-devtools'
```

Возможности:

| Функция | Описание |
|---------|----------|
| **Automatic Restart** | Перезапуск при изменении классов (два ClassLoader: base + restart) |
| **LiveReload** | Автоматическое обновление браузера при изменении ресурсов |
| **Property Defaults** | Отключение кэширования шаблонов, `DEBUG`-логирование |
| **Remote DevTools** | Удалённая отладка (не для production!) |

Два ClassLoader: `base` загружает зависимости (не меняются), `restart` — код приложения. При изменении перезагружается только `restart` ClassLoader — быстрее полного рестарта.

**Отключение:**
```properties
spring.devtools.restart.enabled=false
```

> `DevTools` автоматически отключается в production (при запуске через `java -jar` или из специального ClassLoader).

> [!mcq]
> - [x] Spring Boot DevTools использует два ClassLoader: `base` (зависимости, не меняются) и `restart` (код приложения, перезагружается при изменении классов). | Это корректное описание: перезагрузка только `restart`-ClassLoader'а сильно быстрее полного рестарта JVM. Ключевое отличие и best practice в production.
> - [ ] Spring Boot DevTools использует один ClassLoader и перекомпилирует классы через механизм HotSwap JVM. | DevTools не использует HotSwap JVM. Он использует двухуровневый ClassLoader: base + restart — это и есть его главное отличие от HotSwap. Частая ошибка в реальном коде.
> - [ ] Spring Boot DevTools использует агент инструментирования JVM (`-javaagent`) для замены байт-кода в runtime. | DevTools не использует javaagent и не меняет байт-код в runtime. Работает через пересоздание `restart`-ClassLoader'а. Частая ошибка в реальном коде.
> - [ ] Spring Boot DevTools использует OSGi-контейнер для изолированной загрузки модулей приложения. | OSGi не задействуется. DevTools реализован через два обычных Java ClassLoader-а (base + restart). Частая ошибка в реальном коде.

---

## Q29. (!) Как отключить автоконфигурацию для тестов?

**Тестовые срезы** — загружают только нужный слой:

| Аннотация | Что загружает |
|-----------|--------------|
| `@WebMvcTest` | Только MVC-контроллеры, без сервиса и БД |
| `@DataJpaTest` | Только JPA-репозитории, встроенная БД |
| `@WebFluxTest` | Только WebFlux-контроллеры |
| `@JsonTest` | Только JSON-сериализация |
| `@RestClientTest` | Только REST-клиенты |

**Исключение автоконфигурации в `@SpringBootTest`:**

```java
@SpringBootTest
@EnableAutoConfiguration(exclude = {
    DataSourceAutoConfiguration.class,
    SecurityAutoConfiguration.class
})
class MyIntegrationTest { }
```

**`@MockBean`** — заменяет бин моком в контексте. **`@TestPropertySource`** — переопределяет свойства для теста.

Подробнее о тестировании — в [Интеграционное тестирование](../../testing/integration-testing-interview.md) и [Юнит-тестирование](../../testing/unit-testing-interview.md).

> [!mcq]
> - [ ] `@WebMvcTest` загружает полный контекст приложения, включая сервисы и репозитории. | `@WebMvcTest` загружает только MVC-слой (контроллеры, фильтры, `DispatcherServlet`). Сервисы нужно подменять через `@MockBean`, репозитории не грузятся. Это антипаттерн или неправильный выбор в production.
> - [x] `@WebMvcTest` загружает только MVC-слой (контроллеры, фильтры, `DispatcherServlet`) и требует мокать сервисы через `@MockBean`. | Это корректно: тестовый срез отключает автоконфигурации, не относящиеся к MVC, что ускоряет тесты и снижает связанность. Singleton по умолчанию, lazy vs eager initialization, scope lifecycle важен.
> - [ ] `@WebMvcTest` загружает MVC-слой и `DataSource`, чтобы репозитории могли быть использованы в контроллерах. | `DataSource` не создаётся в `@WebMvcTest` — репозитории не входят в этот срез. Для JPA используется `@DataJpaTest`. Частая ошибка в реальном коде.
> - [ ] `@WebMvcTest` загружает MVC-слой и `EntityManager`, но без репозиториев. | `EntityManager` не создаётся в `@WebMvcTest`. Этот срез ограничен веб-слоем: контроллеры, фильтры, `DispatcherServlet`. Частая ошибка в реальном коде.

## Q30. Какие основные аннотации `Spring Boot` предлагает?

| Аннотация | Назначение |
|-----------|-----------|
| `@SpringBootApplication` | Точка входа = `@Configuration` + `@EnableAutoConfiguration` + `@ComponentScan` |
| `@EnableAutoConfiguration` | Включить автоконфигурацию |
| `@ConfigurationProperties` | Типобезопасная привязка свойств |
| `@ConditionalOnClass` | Условная регистрация бина |
| `@ConditionalOnMissingBean` | Бин только если не определён вручную |
| `@ConditionalOnProperty` | Бин по значению свойства |
| `@AutoConfiguration` | Класс автоконфигурации (Spring Boot 3.x) |
| `@SpringBootTest` | Интеграционный тест с полным контекстом |
| `@WebMvcTest` | Тестовый срез для MVC |
| `@DataJpaTest` | Тестовый срез для JPA |

Подробнее об аннотациях `Spring` — в [Spring Framework](spring-framework-interview.md).

> [!mcq]
> - [x] В Spring Boot 3.x класс автоконфигурации рекомендуется аннотировать `@AutoConfiguration` вместо `@Configuration`. | Это корректно: `@AutoConfiguration` гарантирует правильную обработку порядка (`@AutoConfigureBefore`/`@AutoConfigureAfter`) и фильтрацию. Ключевое отличие и best practice в production.
> - [ ] В Spring Boot 3.x класс автоконфигурации рекомендуется аннотировать `@SpringBootConfiguration` вместо `@Configuration`. | `@SpringBootConfiguration` используется для класса-точки входа приложения, а не для автоконфигураций. Правильная аннотация — `@AutoConfiguration`. Частая ошибка в реальном коде.
> - [ ] В Spring Boot 3.x класс автоконфигурации рекомендуется аннотировать `@EnableAutoConfiguration` вместо `@Configuration`. | `@EnableAutoConfiguration` используется на приложении (через `@SpringBootApplication`), а не на самих классах автоконфигурации. Для них применяется `@AutoConfiguration`.
> - [ ] В Spring Boot 3.x класс автоконфигурации рекомендуется аннотировать `@Bean` вместо `@Configuration`. | `@Bean` применяется на методах, а не на классах. Для декларации класса автоконфигурации в 3.x используется `@AutoConfiguration`. Это антипаттерн или неправильный выбор в production.

---

## Q31. Как использовать `Spring Boot` как приложение командной строки?

Реализовать `CommandLineRunner` или `ApplicationRunner`:

```java
@SpringBootApplication
public class BatchApp implements CommandLineRunner {

    @Override
    public void run(String... args) throws Exception {
        System.out.println("Аргументы: " + Arrays.toString(args));
        // бизнес-логика
    }

    public static void main(String[] args) {
        SpringApplication.run(BatchApp.class, args);
    }
}
```

**`ApplicationRunner`** — получает `ApplicationArguments` вместо `String[]`, поддерживает `--key=value` парсинг.

Для отключения встроенного сервера: `spring.main.web-application-type=none` или `WebApplicationType.NONE` в `SpringApplication`.

> [!mcq]
> - [ ] Для запуска Spring Boot как CLI-приложения нужно реализовать интерфейс `Runnable` в главном классе. | `Runnable` не вызывается Spring Boot после старта контекста. Для CLI используется `CommandLineRunner` или `ApplicationRunner` — они вызываются автоматически после `ApplicationReadyEvent`.
> - [x] Для запуска Spring Boot как CLI-приложения нужно реализовать интерфейс `CommandLineRunner` или `ApplicationRunner` — Spring вызовет `run(args)` после старта контекста. | Это штатный механизм. `ApplicationRunner` получает `ApplicationArguments` с поддержкой парсинга `--key=value`, `CommandLineRunner` — сырой `String[]`.
> - [ ] Для запуска Spring Boot как CLI-приложения нужно реализовать интерфейс `Callable<Integer>` и аннотацию `@CliRunner`. | Аннотации `@CliRunner` не существует, а `Callable` не является частью жизненного цикла Spring Boot. Правильные интерфейсы — `CommandLineRunner`/`ApplicationRunner`.
> - [ ] Для запуска Spring Boot как CLI-приложения нужно вызвать `SpringApplication.runCli()` вместо `SpringApplication.run()`. | Метода `runCli()` у `SpringApplication` нет. Используется обычный `run()` с любой реализацией `CommandLineRunner` или `ApplicationRunner` в контексте.

## Q32. Как настроить логирование?

По умолчанию — `Logback`. Конфигурация через `application.yml`:

```yaml
logging:
  level:
    root: INFO
    com.example: DEBUG
    org.springframework.web: WARN
    org.hibernate.SQL: DEBUG
  file:
    name: logs/app.log
  pattern:
    console: "%d{ISO8601} [%thread] %-5level %logger{36} - %msg%n"
  logback:
    rollingpolicy:
      max-file-size: 100MB
      max-history: 30
```

**Замена на Log4j2:**
```groovy
implementation('org.springframework.boot:spring-boot-starter-web') {
    exclude group: 'org.springframework.boot', module: 'spring-boot-starter-logging'
}
implementation 'org.springframework.boot:spring-boot-starter-log4j2'
```

**Изменение уровня в рантайме** через `Actuator`:
```bash
curl -X POST http://localhost:8080/actuator/loggers/com.example \
  -H 'Content-Type: application/json' \
  -d '{"configuredLevel": "DEBUG"}'
```

В `Kubernetes` — логи в stdout, сбор через `DaemonSet` (`Fluentd`, `Filebeat`). Подробнее — в [Логирование](../../logging/logging-interview.md).

> [!mcq]
> - [x] В Spring Boot по умолчанию используется фреймворк логирования `Logback`. | Это корректно: `spring-boot-starter-logging` транзитивно подтягивает Logback через `spring-boot-starter`. SLF4J служит фасадом поверх него. Ключевое отличие и best practice в production.
> - [ ] В Spring Boot по умолчанию используется фреймворк логирования `Log4j2`. | Log4j2 — альтернатива, которая подключается через `spring-boot-starter-log4j2` с исключением `spring-boot-starter-logging`. По умолчанию используется Logback.
> - [ ] В Spring Boot по умолчанию используется фреймворк логирования `java.util.logging`. | JUL не является дефолтным логгером Spring Boot. По умолчанию используется Logback, а JUL-логи мостятся в SLF4J. Частая ошибка в реальном коде.
> - [ ] В Spring Boot по умолчанию используется фреймворк логирования `Log4j1`. | Log4j 1.x давно устарел и не входит в зависимости Spring Boot. По умолчанию используется Logback. Частая ошибка в реальном коде.

## Q33. Как мигрировать с `Spring` на `Spring Boot`?

Пошаговый план:

1. **BOM/Parent** — подключить `spring-boot-dependencies` или `spring-boot-starter-parent`
2. **Зависимости** — заменить ручные зависимости на `spring-boot-starter-*`
3. **Конфигурация** — перенести XML/Java-config в `application.yml`
4. **Главный класс** — создать `@SpringBootApplication` с `SpringApplication.run()`
5. **Встроенный сервер** — убрать зависимость от внешнего сервера, использовать embedded `Tomcat`
6. **Тесты** — мигрировать на `@SpringBootTest`
7. **Конфликты** — отключить лишнюю автоконфигурацию через `exclude`

Миграция по модулям; тесты и регрессии на каждом шаге. Типичные проблемы: конфликты версий библиотек, дублирование конфигурации бинов (авто + ручная).

> [!mcq]
> - [ ] При миграции с Spring на Spring Boot главный шаг — переписать весь код на Kotlin для совместимости с автоконфигурацией. | Язык не связан с миграцией. Ключевой шаг — подключить `spring-boot-dependencies` (BOM), заменить зависимости на стартеры и добавить `@SpringBootApplication`-класс.
> - [x] При миграции с Spring на Spring Boot ключевые шаги: подключить BOM/parent, заменить зависимости на стартеры, создать класс с `@SpringBootApplication` и `SpringApplication.run()`, отключить конфликтующие автоконфигурации. | Это корректный план миграции. Наибольшие проблемы дают конфликты версий и дублирование конфигурации — поэтому рекомендуется миграция по модулям. Ключевое отличие и best practice в production.
> - [ ] При миграции с Spring на Spring Boot главный шаг — переписать все XML-конфигурации в аннотации на этапе 1 до добавления каких-либо стартеров. | Порядок избыточно жёсткий: Spring Boot поддерживает XML-конфигурации через `@ImportResource`, поэтому их можно мигрировать постепенно. Сначала добавляются BOM и стартеры.
> - [ ] При миграции с Spring на Spring Boot нужно обязательно перейти на Gradle — Maven не поддерживается Spring Boot 3.x. | Maven полностью поддерживается через `spring-boot-starter-parent` или BOM. Spring Boot не диктует систему сборки. Частая ошибка в реальном коде.

## Q34. (!) Что такое `GraalVM Native Image` в `Spring Boot`?

`Spring Boot` 3.x поддерживает компиляцию в нативный образ через `GraalVM`:

```bash
./gradlew nativeCompile
```

**Преимущества:**
- Старт за **50-100 мс** (вместо секунд)
- Потребление памяти **в 3-5 раз меньше**
- Идеально для serverless и short-lived контейнеров

**Ограничения:**
- Рефлексия требует явной конфигурации (GraalVM reachability metadata)
- Длительная компиляция (минуты)
- Не все библиотеки поддерживаются
- Нет динамической загрузки классов

**AOT-обработка** (`Ahead-of-Time`):

```mermaid
graph LR
    A["Исходный код"] --> B["AOT Processing<br/>(compile time)"]
    B --> C["Сгенерированный код<br/>+ metadata"]
    C --> D["GraalVM Native<br/>Image Compiler"]
    D --> E["Нативный бинарник<br/>(~50ms startup)"]
```

`Spring Boot` 3.x выполняет AOT-обработку: генерирует код для создания бинов без рефлексии, анализирует `@Conditional` на этапе компиляции. Результат — нативный бинарник, не требующий JVM.

> [!mcq]
> - [x] Главное преимущество GraalVM Native Image — старт за 50-100 мс и в 3-5 раз меньше потребление памяти, ценой длительной компиляции и ограничений рефлексии. | Это корректный trade-off: быстрый старт и низкое потребление RAM идеальны для serverless; минус — долгая сборка и обязательная `reachability-metadata` для библиотек с рефлексией.
> - [ ] Главное преимущество GraalVM Native Image — в десятки раз большая пропускная способность (throughput) под нагрузкой по сравнению с JVM. | На sustained throughput (tiered JIT) обычная JVM как правило оказывается быстрее. Native Image выигрывает на старте и потреблении памяти, а не на пиковой пропускной способности.
> - [ ] Главное преимущество GraalVM Native Image — автоматическая поддержка всех существующих Java-библиотек без какой-либо конфигурации. | Native Image требует reachability metadata для рефлексии/прокси. Не все библиотеки поддерживаются из коробки — часть требует явных hints или `@RegisterReflectionForBinding`.
> - [ ] Главное преимущество GraalVM Native Image — возможность динамической загрузки классов в runtime через Class.forName. | Динамическая загрузка классов в Native Image как раз ограничена. Это одно из главных отличий от обычной JVM, требующее явной регистрации классов. Частая ошибка в реальном коде.

## Q35. (!) Жизненный цикл `Spring Boot` приложения

Полный жизненный цикл от запуска до остановки:

```mermaid
graph TD
    A["main() → SpringApplication.run()"] --> B["Подготовка Environment<br/>(загрузка property sources)"]
    B --> C["Публикация<br/>ApplicationEnvironmentPreparedEvent"]
    C --> D["Создание ApplicationContext"]
    D --> E["Загрузка @Configuration,<br/>Auto-configuration"]
    E --> F["Refresh контекста<br/>(создание бинов)"]
    F --> G["Запуск встроенного сервера"]
    G --> H["Вызов CommandLineRunner /<br/>ApplicationRunner"]
    H --> I["Публикация<br/>ApplicationReadyEvent"]
    I --> J["Приложение работает"]
    J --> K["Получение SIGTERM"]
    K --> L["Graceful Shutdown<br/>(завершение запросов)"]
    L --> M["Закрытие ApplicationContext<br/>(вызов @PreDestroy)"]
    M --> N["Остановка"]
```

**Ключевые события (listeners):**

| Событие | Когда |
|---------|-------|
| `ApplicationStartingEvent` | До всего, сразу после `run()` |
| `ApplicationEnvironmentPreparedEvent` | Environment готов, контекст не создан |
| `ApplicationContextInitializedEvent` | Контекст создан, бины не загружены |
| `ApplicationPreparedEvent` | Бины загружены, контекст не обновлён |
| `ApplicationStartedEvent` | Контекст обновлён, runners не вызваны |
| `ApplicationReadyEvent` | Всё готово, приложение принимает трафик |
| `ApplicationFailedEvent` | Ошибка при запуске |

**Graceful Shutdown** (Spring Boot 2.3+):

```yaml
server:
  shutdown: graceful
spring:
  lifecycle:
    timeout-per-shutdown-phase: 30s
```

При `SIGTERM` сервер прекращает принимать новые соединения, дожидается завершения текущих запросов (до timeout), затем закрывает контекст.

> [!mcq]
> - [ ] Событие `ApplicationReadyEvent` публикуется до вызова `CommandLineRunner` и `ApplicationRunner`. | `ApplicationReadyEvent` публикуется **после** вызова всех runners — это сигнал, что приложение полностью готово принимать трафик. Частая ошибка в реальном коде.
> - [x] Событие `ApplicationReadyEvent` публикуется после вызова всех `CommandLineRunner` и `ApplicationRunner` — сигнал, что приложение готово принимать трафик. | Это корректная позиция события в жизненном цикле. Между `ApplicationStartedEvent` и `ApplicationReadyEvent` исполняются runners. Ключевое отличие и best practice в production.
> - [ ] Событие `ApplicationReadyEvent` публикуется до refresh контекста, когда бины ещё не созданы. | До refresh контекста публикуется `ApplicationContextInitializedEvent`. `ApplicationReadyEvent` — финальное событие, когда все бины созданы и runners отработали.
> - [ ] Событие `ApplicationReadyEvent` публикуется только при успешном прохождении `/actuator/health` всеми HealthIndicator-ами. | Actuator не влияет на публикацию события. `ApplicationReadyEvent` эмитится после завершения runners независимо от состояния health-индикаторов. Это частая ошибка при неправильном понимании механизма Java.

## Q36. (!) Что такое тестовые срезы (`@WebMvcTest`, `@DataJpaTest`)?

Тестовые срезы — специализированные аннотации Spring Boot Test, которые загружают **только часть** контекста приложения, необходимую для тестирования конкретного слоя. Это ускоряет тесты и снижает связанность.

| Аннотация | Что загружает | Что мокируется |
|-----------|---------------|----------------|
| `@WebMvcTest` | MVC-слой: контроллеры, фильтры, `DispatcherServlet` | Сервисы (`@MockBean`) |
| `@DataJpaTest` | JPA: репозитории, `EntityManager`, H2 in-memory | Сервисный слой |
| `@DataMongoTest` | MongoDB репозитории | — |
| `@WebFluxTest` | WebFlux: контроллеры на реактивном стеке | Сервисы |
| `@JsonTest` | JSON-сериализация (`@JsonComponent`, Jackson) | — |
| `@RestClientTest` | `RestTemplate` / `RestClient` + mock server | — |

**Пример `@WebMvcTest`:**

```java
@WebMvcTest(UserController.class)
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService userService;          // мокируем зависимость

    @Test
    void getUser_returnsOk() throws Exception {
        given(userService.findById(1L))
            .willReturn(new UserDto(1L, "Alice"));

        mockMvc.perform(get("/users/1"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.name").value("Alice"));
    }
}
```

**Пример `@DataJpaTest`:**

```java
@DataJpaTest
// По умолчанию заменяет DataSource на H2 in-memory
// и применяет @Transactional — каждый тест откатывается
class UserRepositoryTest {

    @Autowired
    private TestEntityManager em;

    @Autowired
    private UserRepository userRepository;

    @Test
    void findByEmail_returnsUser() {
        User user = em.persistFlushFind(new User("alice@example.com", "Alice"));

        Optional<User> found = userRepository.findByEmail("alice@example.com");
        assertThat(found).isPresent();
        assertThat(found.get().getName()).isEqualTo("Alice");
    }
}
```

Для тестирования с реальной БД используется `@AutoConfigureTestDatabase(replace = NONE)` в сочетании с `Testcontainers`.

> [!mcq]
> - [x] `@DataJpaTest` по умолчанию заменяет настоящий `DataSource` на встроенный H2 и оборачивает каждый тест в транзакцию с откатом. | Это корректное поведение: поведение контролируется `@AutoConfigureTestDatabase(replace = ANY)` по умолчанию и транзакционностью через `@Transactional`. Propagation (REQUIRED, REQUIRES_NEW, NESTED) и isolation (READ_UNCOMMITTED до SERIALIZABLE) критичны.
> - [ ] `@DataJpaTest` по умолчанию использует настоящий `DataSource` из `application.yml` без каких-либо замен. | По умолчанию `@DataJpaTest` заменяет `DataSource` на встроенный H2. Для работы с реальной БД нужно явно указать `@AutoConfigureTestDatabase(replace = NONE)`.
> - [ ] `@DataJpaTest` по умолчанию заменяет настоящий `DataSource` на встроенный H2, но не управляет транзакциями — коммиты нужно делать вручную. | Транзакционное поведение с rollback включено по умолчанию. Каждый тест откатывается после выполнения, обеспечивая изоляцию. Частая ошибка в реальном коде.
> - [ ] `@DataJpaTest` по умолчанию поднимает Testcontainers-PostgreSQL без какой-либо дополнительной настройки. | Testcontainers не используется автоматически в `@DataJpaTest`. По умолчанию — H2 in-memory. Для Testcontainers нужна отдельная настройка. Частая ошибка в реальном коде.

## Q37. (!) Как работает `@SpringBootTest` и какие режимы запуска контекста существуют?

`@SpringBootTest` загружает **полный** контекст приложения. Управляется параметром `webEnvironment`:

| Режим | Описание |
|-------|----------|
| `MOCK` (по умолчанию) | Загружает `WebApplicationContext` с mock-сервлет-окружением |
| `RANDOM_PORT` | Запускает реальный встроенный сервер на случайном порту |
| `DEFINED_PORT` | Запускает на порту из `application.properties` |
| `NONE` | Загружает `ApplicationContext` без веб-окружения |

**Пример интеграционного теста с реальным портом:**

```java
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class OrderApiIntegrationTest {

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    @Test
    void createOrder_returns201() {
        var response = restTemplate.postForEntity(
            "http://localhost:" + port + "/orders",
            new CreateOrderRequest("ITEM-1", 2),
            OrderDto.class
        );
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
    }
}
```

**Оптимизация скорости тестов** — кэширование контекста. Spring кэширует контекст между тестами, если совпадает набор конфигурации. `@MockBean` / `@SpyBean` инвалидируют кэш — используйте их минимально или выносите в общий базовый класс.

```java
// Базовый класс для переиспользования контекста
@SpringBootTest(webEnvironment = RANDOM_PORT)
@ActiveProfiles("test")
abstract class BaseIntegrationTest {
    // общие @MockBean здесь
}
```

> [!mcq]
> - [ ] В `@SpringBootTest` режим `MOCK` (по умолчанию) запускает реальный встроенный сервер на случайном порту. | `MOCK` использует mock-сервлет-окружение без поднятия сервера. Реальный сервер на случайном порту запускает `RANDOM_PORT`. Частая ошибка в реальном коде.
> - [ ] В `@SpringBootTest` режим `RANDOM_PORT` загружает mock-сервлет-окружение, не запуская реальный сервер. | Наоборот: `RANDOM_PORT` поднимает реальный встроенный сервер. Mock-окружение соответствует режиму `MOCK`. Частая ошибка в реальном коде.
> - [x] В `@SpringBootTest` режим `RANDOM_PORT` запускает реальный встроенный сервер на случайном свободном порту, доступный через `@LocalServerPort`. | Это корректно: режим полезен для end-to-end тестов через `TestRestTemplate` или `WebTestClient`. Порт получается через `@LocalServerPort`. Ключевое отличие и best practice в production.
> - [ ] В `@SpringBootTest` режим `DEFINED_PORT` использует случайный порт, определяемый Spring при старте. | `DEFINED_PORT` использует порт из `application.properties` (или 8080 по умолчанию), а не случайный. Случайный порт — это `RANDOM_PORT`. Частая ошибка в реальном коде.

## Q38. (!) Как устроены `@ConditionalOnProperty`, `@ConditionalOnMissingBean` и другие условные аннотации?

Условные аннотации — механизм тонкой настройки автоконфигурации. Каждая реализует `Condition` и вычисляется во время загрузки контекста.

| Аннотация | Условие регистрации бина |
|-----------|------------------------|
| `@ConditionalOnProperty` | Свойство задано и/или имеет нужное значение |
| `@ConditionalOnMissingBean` | Бин данного типа ещё не зарегистрирован |
| `@ConditionalOnBean` | Бин данного типа уже зарегистрирован |
| `@ConditionalOnClass` | Класс присутствует в classpath |
| `@ConditionalOnMissingClass` | Класс отсутствует в classpath |
| `@ConditionalOnWebApplication` | Контекст является веб-приложением |
| `@ConditionalOnExpression` | SpEL-выражение возвращает `true` |
| `@ConditionalOnResource` | Ресурс (файл) присутствует |
| `@ConditionalOnJava` | Версия JVM соответствует условию |

**Пример кастомной автоконфигурации:**

```java
@AutoConfiguration
@ConditionalOnClass(DataSource.class)            // только если JDBC в classpath
@ConditionalOnProperty(
    prefix = "app.cache",
    name = "enabled",
    havingValue = "true",
    matchIfMissing = false                        // по умолчанию НЕ активен
)
public class RedisCacheAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean(CacheManager.class) // не перекрывает пользовательский бин
    public CacheManager cacheManager(RedisConnectionFactory factory) {
        return RedisCacheManager.builder(factory).build();
    }
}
```

**Порядок вычисления:** `@ConditionalOnClass` / `@ConditionalOnMissingClass` → `@ConditionalOnBean` / `@ConditionalOnMissingBean` → остальные. Ошибочный порядок в классах автоконфигурации ведёт к `NoSuchBeanDefinitionException` — поэтому Spring Boot вычисляет условия на classpath-уровне раньше, чем на bean-уровне.

> [!mcq]
> - [x] Атрибут `matchIfMissing = true` у `@ConditionalOnProperty` делает условие истинным, если свойство вообще не задано в конфигурации. | Это корректно: `matchIfMissing=true` включает бин по умолчанию, если свойство отсутствует. Полезно для fallback-конфигураций. @ConditionalOnProperty позволяет feature flags; используйте для A/B тестирования.
> - [ ] Атрибут `matchIfMissing = true` у `@ConditionalOnProperty` делает условие истинным только если свойство явно равно пустой строке. | Пустая строка и отсутствие свойства — разные состояния. `matchIfMissing` относится именно к **отсутствию** свойства, а не к пустой строке. Это частая ошибка при неправильном понимании механизма Java.
> - [ ] Атрибут `matchIfMissing = true` у `@ConditionalOnProperty` бросает исключение при отсутствии свойства, чтобы явно указать проблему. | Этот атрибут не вызывает исключений. Наоборот, он позволяет активировать бин при отсутствии свойства. Это частая ошибка при неправильном понимании механизма Java.
> - [ ] Атрибут `matchIfMissing = true` у `@ConditionalOnProperty` ищет свойство в переменных окружения, игнорируя `application.yml`. | Источник свойства не меняется: Spring использует стандартный `Environment` со всей цепочкой источников. `matchIfMissing` только определяет поведение при отсутствии свойства. Это частая ошибка при неправильном понимании механизма Java.

## Q39. (!) Что такое AOT-обработка в Spring Boot 3.x?

**AOT (Ahead-Of-Time Processing)** — этап сборки, на котором Spring Boot 3.x анализирует приложение и генерирует исходный код для создания бинов без использования рефлексии и динамических прокси в runtime.

**Зачем нужен AOT:**
- Обязателен для компиляции в **GraalVM Native Image** (рефлексия ограничена)
- Ускоряет старт даже на обычной JVM (меньше работы при инициализации контекста)
- Позволяет выявлять ошибки конфигурации на этапе сборки

**Что генерирует AOT:**

```
build/generated/aotSources/      # Java-код для создания бинов
build/generated/aotResources/    # reflect-config.json, proxy-config.json
build/generated/aotClasses/      # скомпилированные AOT-классы
```

**Типичный пример генерируемого кода:**

```java
// Вместо рефлексии Spring генерирует прямые вызовы:
@Generated
public class MyServiceBeanDefinitions implements BeanDefinitionRegistrar {
    @Override
    public void registerBeanDefinitions(BeanDefinitionRegistry registry) {
        // прямое создание BeanDefinition без рефлексии
        RootBeanDefinition def = new RootBeanDefinition(MyService.class);
        def.setInstanceSupplier(MyService::new);
        registry.registerBeanDefinition("myService", def);
    }
}
```

**Запуск AOT-обработки:**

```bash
# Gradle: генерирует AOT-источники
./gradlew processAot

# Сборка нативного образа
./gradlew nativeCompile

# Запуск нативного бинарника
./build/native/nativeCompile/my-app
```

**Ограничения:** динамические `@Bean`-методы с рефлексией, `BeanDefinitionRegistryPostProcessor` с условной логикой требуют явных hints через `@RegisterReflectionForBinding` или `RuntimeHintsRegistrar`.

> [!mcq]
> - [ ] AOT-обработка в Spring Boot 3.x выполняется в runtime при первом старте приложения. | AOT выполняется **до** запуска — на этапе сборки. Команда `./gradlew processAot` генерирует Java-исходники и метаданные заранее. Частая ошибка в реальном коде.
> - [x] AOT-обработка в Spring Boot 3.x выполняется на этапе сборки: генерирует Java-исходники, reflect-config и proxy-config для создания бинов без рефлексии в runtime. | Это корректно: AOT-артефакты попадают в `build/generated/aotSources/` и компилируются вместе с приложением, сильно ускоряя старт. Ключевое отличие и best practice в production.
> - [ ] AOT-обработка в Spring Boot 3.x выполняется только для тестов и никак не влияет на production-билд. | AOT применима именно к production-билду, особенно для сборки в GraalVM Native Image. Тесты тоже могут участвовать (через `processTestAot`), но это не основной сценарий.
> - [ ] AOT-обработка в Spring Boot 3.x автоматически заменяет все Java Proxy на CGLIB Proxy. | AOT не меняет стратегию проксирования. Он генерирует метаданные (`proxy-config.json`) для Native Image, чтобы прокси работали без рефлексии. Частая ошибка в реальном коде.

## Q40. (!) Как настроить кастомный `HealthIndicator` для Actuator?

`HealthIndicator` — интерфейс для добавления собственных проверок здоровья в `/actuator/health`. Spring Boot автоматически обнаруживает все бины, реализующие этот интерфейс.

**Простой HealthIndicator:**

```java
@Component
public class ExternalApiHealthIndicator implements HealthIndicator {

    private final ExternalApiClient client;

    public ExternalApiHealthIndicator(ExternalApiClient client) {
        this.client = client;
    }

    @Override
    public Health health() {
        try {
            boolean reachable = client.ping();
            if (reachable) {
                return Health.up()
                    .withDetail("url", client.getBaseUrl())
                    .withDetail("responseTime", client.getLastResponseTimeMs() + "ms")
                    .build();
            }
            return Health.down()
                .withDetail("reason", "API не отвечает")
                .build();
        } catch (Exception e) {
            return Health.down(e)
                .withDetail("error", e.getMessage())
                .build();
        }
    }
}
```

**Результат в `/actuator/health`:**

```json
{
  "status": "UP",
  "components": {
    "externalApi": {
      "status": "UP",
      "details": {
        "url": "https://api.example.com",
        "responseTime": "42ms"
      }
    }
  }
}
```

**Асинхронный / реактивный вариант (WebFlux):**

```java
@Component
public class ReactiveDbHealthIndicator implements ReactiveHealthIndicator {

    private final R2dbcConnectionFactory factory;

    @Override
    public Mono<Health> health() {
        return Mono.fromCallable(() -> factory.create())
            .map(conn -> Health.up().withDetail("r2dbc", "connected").build())
            .onErrorReturn(Health.down().withDetail("r2dbc", "unavailable").build());
    }
}
```

Группировка индикаторов настраивается через `management.endpoint.health.group.*`.

> [!mcq]
> - [x] Кастомный `HealthIndicator` создаётся через бин, реализующий интерфейс `HealthIndicator` с методом `Health health()` — Spring Boot автоматически его обнаруживает. | Это корректно: любой бин, реализующий `HealthIndicator`, попадает в `/actuator/health`. Имя компонента в JSON берётся из имени бина с удалением суффикса "HealthIndicator". Expose metrics для мониторинга; в production используйте Spring Boot Admin + Prometheus + Grafana.
> - [ ] Кастомный `HealthIndicator` создаётся через аннотацию `@HealthCheck` на любом методе — Spring Boot сам интегрирует его в `/actuator/health`. | Аннотации `@HealthCheck` нет. Проверка оформляется именно как бин, реализующий интерфейс `HealthIndicator`. Это частая ошибка при неправильном понимании механизма Java. Это антипаттерн или неправильный выбор в production.
> - [ ] Кастомный `HealthIndicator` создаётся через наследование `AbstractEndpoint<Health>` с переопределением метода `invoke()`. | Класс `AbstractEndpoint<Health>` — это инфраструктура endpoint-ов, не health. Правильный путь — интерфейс `HealthIndicator` с методом `health()`. Частая ошибка в реальном коде.
> - [ ] Кастомный `HealthIndicator` создаётся через регистрацию лямбды `MeterRegistry::registerHealth` в конфигурации. | Такого метода у `MeterRegistry` нет — это API метрик Micrometer, не Actuator. Health-индикаторы регистрируются как бины, реализующие `HealthIndicator`. Это частая ошибка при неправильном понимании механизма Java.

## Q41. (!) Как работает Spring Boot с несколькими профилями одновременно?

Spring Boot поддерживает **активацию нескольких профилей** одновременно. Каждый профиль добавляет/переопределяет конфигурацию поверх базовой.

**Активация нескольких профилей:**

```yaml
# application.yml (базовая конфигурация)
spring:
  profiles:
    active: dev,metrics   # несколько через запятую
```

```bash
# Через системное свойство
java -Dspring.profiles.active=prod,metrics -jar app.jar

# Через переменную окружения
SPRING_PROFILES_ACTIVE=prod,metrics java -jar app.jar
```

**Profile Groups (Spring Boot 2.4+):**

```yaml
# application.yml
spring:
  profiles:
    group:
      production:          # псевдоним для набора профилей
        - prod-db
        - prod-metrics
        - prod-security
      development:
        - dev-db
        - dev-logging
```

При активации `production` автоматически включаются `prod-db`, `prod-metrics`, `prod-security`.

**Порядок применения конфигурации:**

```
application.yml                      # базовая
application-{profile1}.yml           # первый профиль
application-{profile2}.yml           # второй (перекрывает первый)
```

**`@Profile` на бинах:**

```java
@Configuration
@Profile("!prod")          // активен на всех профилях, кроме prod
public class MockEmailConfig {
    @Bean
    public EmailService emailService() {
        return new MockEmailService();
    }
}

@Configuration
@Profile("prod & metrics") // AND-условие (Spring 5.1+)
public class ProdMetricsConfig { ... }
```

**`@ActiveProfiles` в тестах:**

```java
@SpringBootTest
@ActiveProfiles({"test", "h2"})
class ServiceTest { ... }
```

> [!mcq]
> - [ ] При активации нескольких профилей через запятую Spring Boot применяет только первый из них, игнорируя остальные. | Spring Boot применяет все указанные профили — их значения накладываются друг на друга. Последующие профили перекрывают совпадающие ключи предыдущих. Частая ошибка в реальном коде.
> - [x] При активации нескольких профилей через запятую Spring Boot применяет их все: более поздний в списке перекрывает совпадающие ключи более раннего. | Это корректно: `application-{profile1}.yml`, затем `application-{profile2}.yml` — каждый последующий перекрывает предыдущий. Ключевое отличие и best practice в production.
> - [ ] При активации нескольких профилей через запятую Spring Boot бросает `ProfileConflictException` при совпадающих ключах. | Таких исключений Spring Boot не бросает. Коллизии разрешаются правилом «последний выигрывает». Частая ошибка в реальном коде.
> - [ ] При активации нескольких профилей через запятую Spring Boot объединяет только секции верхнего уровня, оставляя остальные неизменными. | Spring Boot не делает выборочного слияния по уровням. Применение профилей идёт по цепочке полного наложения значений. Частая ошибка в реальном коде.

## Q42. Как ограничить экспозицию Actuator endpoints в production?

По умолчанию Actuator открывает только `/health` и `/info` по HTTP. В production важно явно контролировать, какие endpoint-ы доступны и защищены.

**Конфигурация экспозиции:**

```yaml
management:
  endpoints:
    web:
      exposure:
        include: health,info,metrics,prometheus  # белый список
        # exclude: env,beans,heapdump           # или чёрный список
  endpoint:
    health:
      show-details: when-authorized              # детали только авторизованным
      show-components: when-authorized
    # Защита отдельного endpoint
    shutdown:
      enabled: false                             # отключить полностью
  server:
    port: 9090                                   # отдельный порт для management
```

**Защита через Spring Security:**

```java
@Configuration
@EnableWebSecurity
public class ActuatorSecurityConfig {

    @Bean
    public SecurityFilterChain actuatorSecurity(HttpSecurity http) throws Exception {
        return http
            .securityMatcher(EndpointRequest.toAnyEndpoint())
            .authorizeHttpRequests(auth -> auth
                .requestMatchers(EndpointRequest.to(HealthEndpoint.class, InfoEndpoint.class))
                    .permitAll()
                .requestMatchers(EndpointRequest.toAnyEndpoint())
                    .hasRole("ACTUATOR_ADMIN")
                .anyRequest().authenticated()
            )
            .httpBasic(Customizer.withDefaults())
            .build();
    }
}
```

**Вынесение на отдельный порт** (рекомендуется для production):

```yaml
management:
  server:
    port: 9090
    # Этот порт закрыт в Ingress/LoadBalancer — доступен только внутри кластера
```

> **На собеседовании:** упомяните, что `/actuator/heapdump` и `/actuator/env` особенно чувствительны — первый даёт доступ к памяти процесса, второй раскрывает переменные окружения включая секреты.

> [!mcq]
> - [x] В production Actuator endpoints обычно выносят на отдельный порт через `management.server.port`, закрытый в Ingress/LoadBalancer, и защищают через Spring Security. | Это корректная практика: отдельный порт изолирует management-трафик от публичного, а Security с `EndpointRequest.toAnyEndpoint()` ограничивает доступ ролью `ACTUATOR_ADMIN`. Expose metrics для мониторинга; в production используйте Spring Boot Admin + Prometheus + Grafana.
> - [ ] В production Actuator endpoints рекомендуется открывать через `management.endpoints.web.exposure.include=*` без какой-либо защиты. | Открытие `*` без Security — прямая дорога к утечке секретов через `/env` и `/heapdump`. В production так делать нельзя. Это частая ошибка при неправильном понимании механизма Java.
> - [ ] В production Actuator endpoints рекомендуется закрывать полностью через `management.endpoints.enabled-by-default=false`. | Полное отключение лишает команду инструментов мониторинга. Правильный подход — выборочная экспозиция и защита через Security, а не глобальное отключение. Это частая ошибка при неправильном понимании механизма Java.
> - [ ] В production Actuator endpoints рекомендуется оставлять на том же порту, что и приложение, и защищать их через CORS. | CORS защищает только браузерные клиенты и не мешает curl/взлому. Штатная защита — Spring Security с ограничением ролей плюс отдельный management-порт. Это частая ошибка при неправильном понимании механизма Java.

---

## See also

- [Spring Framework](spring-framework-interview.md) — IoC-контейнер и DI, лежащие в основе Boot
- [Spring MVC](spring-mvc-interview.md) — веб-слой, автоконфигурируемый Boot-ом
- [Spring WebFlux](spring-webflux-interview.md) — реактивный стек, поддерживаемый Boot-ом
- [Spring Security](spring-security-interview.md) — автоконфигурация Security в Spring Boot
- [Spring Data JPA](spring-data-jpa-interview.md) — автоконфигурация datasource и репозиториев
- [Spring Cloud](spring-cloud-interview.md) — экосистема микросервисов поверх Spring Boot
- [Spring Boot Actuator](spring-boot-actuator-interview.md) — production-ready мониторинг и management
- [Spring Batch](spring-batch-interview.md) — пакетная обработка, интегрированная в Boot
- [Микросервисы](../../architecture/microservices-interview.md) — Spring Boot как основа для микросервисов
- [Docker](../../devops/docker-interview.md) — контейнеризация Spring Boot приложений

- [Spring AOP](spring-aop-interview.md)
- [Spring Batch](spring-batch-interview.md)
- [Spring Boot Actuator](spring-boot-actuator-interview.md)
- [Spring Cloud](spring-cloud-interview.md)
- [Spring Data JPA](spring-data-jpa-interview.md)
- [Spring Framework](spring-framework-interview.md)
- [Шпаргалка: Spring Boot — Полное руководство](../../../frameworks/spring/spring-boot.md) — теория
