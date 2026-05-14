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
updated: "2026-05-14"
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
>
> **Вопрос:** Какие пять изменений в Spring Boot 3.0 действительно являются ключевыми breaking-баррьерами для миграции с 2.7?
>
> ---
>
> #### A) Spring Boot 3 = Java 11 baseline + Spring Framework 5.4 + опциональная поддержка `jakarta.*` через флаг — ❌ Неверно
>
> **Что на самом деле:** Spring Boot 3 требует **Java 17** как минимум (build и runtime). Spring Framework 6 — обязательная зависимость, не 5.x. Переход на `jakarta.*` — не опциональный флаг, а полная замена пакетов: код, использующий `javax.persistence.*`, `javax.servlet.*`, `javax.validation.*`, не скомпилируется на SB 3.
>
> **Откуда путаница:** в Spring Boot 2.x была возможность работать с Java 8/11/17 одновременно, и многие команды думают, что Spring сохранит подобную обратную совместимость и в 3.x.
>
> **Если бы это было правдой:** команды откладывали бы апгрейд JDK и продолжали использовать `javax.*`; в production миграция была бы тривиальной. На практике попытка собрать SB 3 проект на Java 11 даёт `UnsupportedClassVersionError` уже на этапе компиляции `spring-boot-starter` jar-ов.
>
> ---
>
> #### D) Java 17 baseline, Jakarta EE 9+ namespace migration, Spring Framework 6, GraalVM Native Image first-class support, Micrometer Observation API (metrics + tracing) — ✓ Верно
>
> **Развёрнутое объяснение:**
>
> Пять системных изменений Spring Boot 3.0 (ноябрь 2022):
>
> 1. **Java 17 baseline** — минимум для компиляции и runtime. Spring Framework 6 использует `sealed`, `records`, pattern matching.
> 2. **Jakarta EE 9+** — все Java EE пакеты переименованы из `javax.*` в `jakarta.*` (Eclipse Foundation не получила trademark от Oracle). Затрагивает JPA, Servlet, JAX-RS, Bean Validation, JMS, Mail.
> 3. **Spring Framework 6** — обновлённый Core, поддержка AOT, Problem Details (RFC 7807), HTTP Interface Clients.
> 4. **GraalVM Native Image** — поддержка из коробки через `spring-boot-starter-parent` AOT-processing (без Spring Native experimental).
> 5. **Observation API** — единый API для metrics (Micrometer) и tracing (`micrometer-tracing` через OpenTelemetry/Zipkin/Brave), вместо Spring Cloud Sleuth.
>
> **Пример:**
> ```xml
> <parent>
>     <groupId>org.springframework.boot</groupId>
>     <artifactId>spring-boot-starter-parent</artifactId>
>     <version>3.2.0</version>
> </parent>
> <properties>
>     <java.version>17</java.version>
> </properties>
> ```
>
> **Когда применять:** новые проекты с 2023 года — стартовать сразу на SB 3.x. Legacy SB 2.7 — план миграции с budget на тестирование (зависимости часто отстают на 6-12 месяцев).
>
> **Подводные камни:** многие third-party библиотеки задержались с jakarta-релизами (springdoc-openapi, swagger, custom internal libs). Проверить совместимость через `mvn dependency:tree` ДО старта миграции.
>
> **Связанные вопросы:** [[Q2]] — javax→jakarta детали; [[Q3]] — последовательность миграции 2.7→3.x; [[Q15]] — типичные проблемы.
>
> ---
>
> #### C) Главное изменение — переход на Spring Boot Native Mode (всегда GraalVM), JVM режим deprecated с 3.0 — ❌ Неверно
>
> **Что на самом деле:** Spring Boot 3 продолжает быть **в первую очередь JVM-приложением**. GraalVM Native Image — опциональный профиль сборки (`-Pnative`), не дефолтный режим. JVM mode полноценно поддерживается и остаётся основным сценарием для большинства проектов.
>
> **Откуда путаница:** Spring Native (experimental проект в 2021-2022) активно продвигал GraalVM, и его слияние с mainline Spring Boot 3 многие истолковали как «теперь всё native».
>
> **Если бы это было правдой:** все проекты на SB 3 страдали бы от ограничений native (reflection hints, отсутствие dynamic class loading, длительная сборка 5-15 минут). На практике JVM-приложение собирается за секунды и запускается за 2-5 секунд как обычно.
>
> ---
>
> #### B) Spring Boot 3 удалил Actuator, заменил его на отдельный starter `spring-boot-starter-observability` — ❌ Неверно
>
> **Что на самом деле:** Actuator **полностью сохранён** в Spring Boot 3 — это `spring-boot-starter-actuator`. Изменилось только то, что Micrometer Observation API заменил Spring Cloud Sleuth для tracing, и переименованы некоторые properties (`management.metrics.export.prometheus.*` → `management.prometheus.metrics.export.*`).
>
> **Откуда путаница:** новость о Micrometer Tracing вытеснении Sleuth многие интерпретировали как «Actuator переделан». Также `spring-boot-properties-migrator` подсвечивает переименования, что усиливает впечатление масштабного слома.
>
> **Если бы это было правдой:** все Kubernetes liveness/readiness probes, healthcheck-эндпоинты `/actuator/health`, метрики Prometheus в SB 3 не работали бы — но они работают штатно.
>

## Q2. Что такое миграция с javax на jakarta и почему она нужна?

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
>
> **Вопрос:** Почему Spring Boot 3 переименовал все пакеты `javax.*` (servlet, persistence, validation) в `jakarta.*`?
>
> ---
>
> #### A) Это маркетинговая инициатива Spring Team для брендирования своей версии Java EE — ❌ Неверно
>
> **Что на самом деле:** Spring Framework просто **следует** переходу Java EE → Jakarta EE. В 2017 Oracle передала Java EE в Eclipse Foundation, но НЕ передала право использовать trademark `javax.*`. Eclipse был вынужден переименовать все пакеты — Spring/Hibernate/Tomcat не имели выбора и должны были адаптироваться.
>
> **Откуда путаница:** в community часто звучит «Spring сломал совместимость» — но Spring лишь обновил импорты под Jakarta EE 9. Если бы Spring проигнорировал, он остался бы на устаревшем Java EE 8 без новых спецификаций.
>
> **Если бы это было правдой:** Spring Team могла бы выбрать любые имена пакетов (`org.springframework.persistence.*`?) — но тогда не работала бы интеграция с Hibernate 6, Tomcat 10, Jetty 11, которые тоже мигрировали на `jakarta.*`.
>
> ---
>
> #### B) `javax.*` пакеты были несовместимы с Java 17 модульной системой, и Eclipse решил их переписать — ❌ Неверно
>
> **Что на самом деле:** Java Module System (JPMS) с Java 9 — совершенно отдельная история. `javax.*` пакеты прекрасно работали в модульной системе. Причина переименования **исключительно юридическая** — Oracle сохранил trademark на `javax.*` после передачи Java EE Eclipse Foundation в 2017.
>
> **Откуда путаница:** Java 9 (модули) и javax→jakarta (Jakarta EE 9, 2019) хронологически близки, и многие путают эти изменения.
>
> **Если бы это было правдой:** изменения пошли бы постепенно с Java 9, а не одним большим релизом в 2019. Также Java SE-пакеты `javax.sql.DataSource`, `javax.crypto.*` остались бы — и они остались, потому что они часть JDK (Oracle), а не Java EE (Eclipse).
>
> ---
>
> #### C) Trademark `javax.*` принадлежит Oracle; Eclipse Foundation после передачи Java EE не получил права использовать namespace; Jakarta EE 9 (2019) переименовал все Java EE пакеты в `jakarta.*` — ✓ Верно
>
> **Развёрнутое объяснение:**
>
> Timeline миграции:
> 1. **2017** — Oracle объявляет передачу Java EE Eclipse Foundation.
> 2. **2018** — Eclipse Foundation запускает проект Jakarta EE (новое имя для Java EE).
> 3. **Переговоры о `javax.*` namespace** — Oracle оставляет trademark за собой; Eclipse не может развивать спецификации в этом namespace без согласия Oracle.
> 4. **Jakarta EE 9 (2019)** — полная замена `javax.*` → `jakarta.*` для всех Java EE API.
> 5. **2022** — Spring Boot 3 / Hibernate 6 / Tomcat 10 / Jetty 11 переходят на jakarta.
>
> **Пример:**
> ```java
> // ДО Spring Boot 3
> import javax.persistence.Entity;
> import javax.servlet.http.HttpServletRequest;
> import javax.validation.constraints.NotNull;
>
> // ПОСЛЕ Spring Boot 3
> import jakarta.persistence.Entity;
> import jakarta.servlet.http.HttpServletRequest;
> import jakarta.validation.constraints.NotNull;
> ```
>
> **Когда применять:** при миграции SB 2.7→3.x — обязательная замена всех Java EE импортов через OpenRewrite recipe `org.openrewrite.java.spring.boot3.UpgradeSpringBoot_3_0` или IntelliJ Migration Assistant.
>
> **Подводные камни:** Java SE пакеты (`javax.sql.DataSource`, `javax.crypto.*`, `javax.net.ssl.*`, `javax.management.*`, `javax.naming.*`) **остались javax** — это часть JDK от Oracle, не Java EE. Правило: если пакет из JDK rt.jar — javax; если из Java EE spec — jakarta.
>
> **Связанные вопросы:** [[Q1]] — Spring Boot 3 ключевые изменения; [[Q4]] — какие javax-пакеты НЕ мигрировали; [[Q15]] — типичные проблемы при миграции.
>
> ---
>
> #### D) Spring Team добровольно переименовала пакеты для удобной поддержки JDK 17 — ❌ Неверно
>
> **Что на самом деле:** переименование произошло не в Spring, а в **Jakarta EE 9 (2019, Eclipse Foundation)**. Spring был вынужден последовать, чтобы интегрироваться с обновлёнными Tomcat 10, Hibernate 6, Jetty 11. Spring Team не имела контроля над namespace — это решение Oracle/Eclipse.
>
> **Откуда путаница:** для разработчика, который видит SB 3 migration guide, кажется что это Spring breaking change. На самом деле Spring — реактивный потребитель: они мигрировали последними, не первыми.
>
> **Если бы это было правдой:** Spring мог бы оставить `javax.*` (как Java EE 8 stack) — но тогда не работали бы Hibernate 6 / Tomcat 10 / Jetty 11, и Spring замёрз бы на устаревшем стеке.
>

## Q3. Как выполнить миграцию с Spring Boot 2.7 на 3.x?

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
>
> **Вопрос:** В каком порядке безопасно мигрировать legacy проект со Spring Boot 2.7 на Spring Boot 3.x?
>
> ---
>
> #### A) Сначала обновить JDK до 17 на 2.7.x → обновить до latest 2.7.x (последний minor) → запустить OpenRewrite recipe → обновить parent на 3.x → исправить оставшиеся ошибки и протестировать — ✓ Верно
>
> **Развёрнутое объяснение:**
>
> Поэтапный план миграции (рекомендация Spring Team):
>
> 1. **Java 17 на текущем Spring Boot 2.7** — приложение должно работать на JDK 17 в production. Это снимает риск «JDK + Spring сразу».
> 2. **Spring Boot 2.7.x latest** (например 2.7.18) — последний minor; убирает большую часть deprecation warnings, готовит к 3.x.
> 3. **OpenRewrite migration recipe** — автоматический рефакторинг javax→jakarta:
>    ```bash
>    mvn org.openrewrite.maven:rewrite-maven-plugin:run \
>      -Drewrite.activeRecipes=org.openrewrite.java.spring.boot3.UpgradeSpringBoot_3_0
>    ```
> 4. **`spring-boot-starter-parent` 3.x** — обновить parent, разрулить compile errors (которые остались после OpenRewrite).
> 5. **`spring-boot-properties-migrator` (runtime)** — добавить временно, чтобы получать warnings о переименованных properties.
> 6. **Тестирование** — unit, integration, e2e; особое внимание Hibernate 6 SQL диалектам и Spring Security 6.
>
> **Пример:**
> ```xml
> <parent>
>     <groupId>org.springframework.boot</groupId>
>     <artifactId>spring-boot-starter-parent</artifactId>
>     <version>3.2.0</version>
> </parent>
> <properties>
>     <java.version>17</java.version>
> </properties>
>
> <dependencies>
>   <dependency>
>     <groupId>org.springframework.boot</groupId>
>     <artifactId>spring-boot-properties-migrator</artifactId>
>     <scope>runtime</scope>
>   </dependency>
> </dependencies>
> ```
>
> **Когда применять:** для любого production-проекта с возрастом >1 года. Маленькие greenfield-проекты можно мигрировать одним прыжком.
>
> **Подводные камни:** OpenRewrite не покрывает всё — Spring Security 6 lambda DSL, Hibernate 6 SQL generation, custom `WebSecurityConfigurerAdapter` нужно править вручную. Third-party библиотеки могут не иметь jakarta-версий — проверить через `mvn dependency:tree | grep javax`.
>
> **Связанные вопросы:** [[Q11]] — Spring Security 6 breaking changes; [[Q14]] — properties migrator; [[Q15]] — типичные проблемы при миграции.
>
> ---
>
> #### B) Сразу `spring-boot-starter-parent` 3.2.0, потом фиксить compile errors одну за другой, тесты переписывать в конце — ❌ Неверно
>
> **Что на самом деле:** прямой прыжок 2.7 → 3.2 обычно даёт **сотни compile errors** одновременно: javax→jakarta, Spring Security 6 breaking, Hibernate 6 HQL изменения, Sleuth удалён, Actuator endpoint переименования. Распутывать это всё разом — путь к багам, особенно когда тесты ещё не работают (нечем проверить корректность правок).
>
> **Откуда путаница:** для маленьких проектов прыжок 2.7→3.2 действительно проходит за час. Для production-приложения с 50+ зависимостями — это недели работы и регресс-баги.
>
> **Если бы это было правдой:** команда правит код «до зелёных тестов», но без поэтапной верификации не понимает, какой именно шаг ввёл регрессию. Типичный сценарий — обнаружение Hibernate 6 HQL bug через 2 недели после релиза в production.
>
> ---
>
> #### C) Сразу мигрировать на SB 3.x, потом дотягивать JDK 17 при следующем спринте — ❌ Неверно
>
> **Что на самом деле:** Spring Boot 3 **требует** Java 17 для компиляции и runtime. Без JDK 17 даже Maven не сможет загрузить `spring-boot-starter-parent` 3.x — будет `UnsupportedClassVersionError` уже на этапе resolution.
>
> **Откуда путаница:** Spring Boot 2.x работал на Java 8 и 11 одновременно, и многие думают что переход на 3 можно отложить апгрейд JDK на потом.
>
> **Если бы это было правдой:** можно было бы постепенно вводить SB 3, оставаясь на Java 11. На практике CI-сборка ломается на самом первом шаге.
>
> ---
>
> #### D) Откатить все custom configurations, перейти на vanilla SB 3 архитектуру, постепенно добавить кастомизации обратно — ❌ Неверно
>
> **Что на самом деле:** «откатить кастомизации» означает rewrite from scratch — это не миграция, а переписывание проекта. Для production-системы с бизнес-логикой это месяцы работы. Правильный подход — **инкрементальный**: править зависимости и API, сохраняя бизнес-логику нетронутой.
>
> **Откуда путаница:** в некоторых блогах «greenfield rewrite» предлагается как способ обхода технического долга. Это валидно для маленьких прототипов, но не для production.
>
> **Если бы это было правдой:** разработчики бы переписывали бизнес-логику и теряли edge case коды, накопленные годами. Регресс-баги становятся гарантированными.
>

## Q4. Какие javax-пакеты НЕ мигрировали на jakarta?

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
>
> **Вопрос:** Какие `javax.*` пакеты ОСТАЛИСЬ в Spring Boot 3 (НЕ переименованы в `jakarta.*`)?
>
> ---
>
> #### A) Все `javax.*` пакеты переименованы в `jakarta.*` без исключений — ❌ Неверно
>
> **Что на самом деле:** **JDK-пакеты остались** — `javax.sql.DataSource`, `javax.crypto.*`, `javax.net.ssl.*`, `javax.management.*` (JMX), `javax.naming.*` (JNDI). Эти пакеты — часть Java SE (rt.jar / java.base module), а не Java EE; Oracle их контролирует и не передавал в Eclipse Foundation.
>
> **Откуда путаница:** при OpenRewrite migration recipe и автоматическом «find-replace javax→jakarta» все импорты заменяются скопом. Без точного понимания границы Java SE / Java EE разработчики ломают рабочий код.
>
> **Если бы это было правдой:** `javax.sql.DataSource` стал бы `jakarta.sql.DataSource`, и любой код с `@Autowired DataSource ds` не компилировался бы. Hikari, Tomcat JDBC pool тоже сломались бы.
>
> ---
>
> #### B) Остался только `javax.sql.DataSource` (JDBC API в JDK), всё остальное мигрировано — ❌ Неверно
>
> **Что на самом деле:** не только DataSource — целый набор JDK-пакетов остался: `javax.crypto.*` (Cipher, KeyGenerator), `javax.net.ssl.*` (SSLContext), `javax.management.*` (MBean, MXBean), `javax.naming.*` (JNDI), `javax.security.auth.*` (JAAS).
>
> **Откуда путаница:** DataSource — самый известный пример «javax, который остался», поэтому многие запоминают только его. Но если копнуть глубже — JDK содержит десятки `javax.*` пакетов.
>
> **Если бы это было правдой:** код, использующий `Cipher.getInstance("AES")` из `javax.crypto`, ломался бы. На практике крипто-операции работают штатно в SB 3.
>
> ---
>
> #### C) Пакеты из JDK остались `javax.*` (sql, crypto, net.ssl, management, naming, security); Java EE пакеты мигрировали на `jakarta.*` (persistence, servlet, validation, jms, mail, annotation, ws.rs) — ✓ Верно
>
> **Развёрнутое объяснение:**
>
> Граница простая: **если пакет входит в JDK (rt.jar / java.base / java.xml и т.д.) — javax. Если из Java EE / Jakarta EE spec — jakarta**.
>
> **Остались javax (Java SE / JDK):**
> - `javax.sql.*` — JDBC API
> - `javax.crypto.*` — JCE (Java Cryptography Extension)
> - `javax.net.*`, `javax.net.ssl.*` — SSL/networking
> - `javax.management.*` — JMX (MBean)
> - `javax.naming.*` — JNDI
> - `javax.security.auth.*` — JAAS
> - `javax.xml.*` — JAXP, JAX-WS (зависит от модуля)
>
> **Мигрировали на jakarta (Java EE → Jakarta EE):**
> - `jakarta.persistence.*` — JPA
> - `jakarta.servlet.*` — Servlet API
> - `jakarta.validation.*` — Bean Validation
> - `jakarta.jms.*` — JMS
> - `jakarta.mail.*` — Mail
> - `jakarta.annotation.*` — `@PostConstruct`, `@PreDestroy`, `@Resource`
> - `jakarta.ws.rs.*` — JAX-RS
> - `jakarta.transaction.*` — JTA
>
> **Пример:**
> ```java
> import javax.sql.DataSource;            // JDK — остался javax
> import javax.crypto.Cipher;              // JDK — остался javax
> import javax.naming.InitialContext;      // JDK — остался javax
>
> import jakarta.persistence.Entity;       // Java EE → jakarta
> import jakarta.servlet.http.HttpServlet; // Java EE → jakarta
> import jakarta.validation.Valid;         // Java EE → jakarta
> ```
>
> **Когда применять:** при ручной правке импортов после OpenRewrite. Тест: если класс есть в `java.base`/`java.sql`/`java.xml`/`java.management` модулях JDK 17 — оставлять javax.
>
> **Подводные камни:** `javax.annotation.Resource` (CDI) мигрировал в `jakarta.annotation.Resource`, но `javax.annotation.processing.*` (часть JDK для annotation processors) остался javax. Также `@PostConstruct`/`@PreDestroy` теперь в `jakarta.annotation.*`, но раньше jaxws-api тащил их через `javax.annotation`.
>
> **Связанные вопросы:** [[Q2]] — причины миграции javax→jakarta; [[Q3]] — порядок миграции SB 2.7→3.x; [[Q15]] — типичные проблемы.
>
> ---
>
> #### D) `javax.transaction.*` (JTA) остался — потому что входит в JDK через `java.transaction.xa` модуль — ❌ Неверно
>
> **Что на самом деле:** `javax.transaction.xa.*` (XAResource, Xid) действительно остался в JDK (`java.transaction.xa` module). Но **сам `javax.transaction.*` (UserTransaction, TransactionManager)** — это Java EE / JTA spec, и он **мигрировал** в `jakarta.transaction.*`. Только подпакет `xa` остался.
>
> **Откуда путаница:** имя похожее (`javax.transaction`), и разница в подпакете легко проходит мимо. Запутаться особенно легко если код использует только XAResource без UserTransaction.
>
> **Если бы это было правдой:** в SB 3 можно было бы использовать `@Inject UserTransaction` из `javax.transaction` — но нет, нужен `jakarta.transaction.UserTransaction`.
>

## Q5. Что такое HTTP Interface Clients в Spring 6?

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
>
> **Вопрос:** Что такое HTTP Interface Clients (`@HttpExchange`) в Spring Framework 6 и чем они отличаются от Feign?
>
> ---
>
> #### B) HTTP Interface Clients — обёртка над OpenFeign внутри Spring Cloud, требует `@EnableFeignClients` — ❌ Неверно
>
> **Что на самом деле:** HTTP Interface Clients — **встроенная** в Spring Framework 6 фича (не Spring Cloud, не Feign). Создаются через `HttpServiceProxyFactory` поверх `WebClient` или `RestClient`. `@EnableFeignClients` — отдельная аннотация Spring Cloud OpenFeign, никак не связана с `@HttpExchange`.
>
> **Откуда путаница:** оба механизма используют декларативные интерфейсы с HTTP-аннотациями (`@GetMapping`-подобные). Внешне выглядят похоже, поэтому многие думают что Spring встроил Feign в core.
>
> **Если бы это было правдой:** для работы `@HttpExchange` нужна была бы зависимость на `spring-cloud-starter-openfeign` — но это работает чисто на `spring-web` без Spring Cloud.
>
> ---
>
> #### A) Декларативный HTTP-клиент в Spring Framework 6, использует `@HttpExchange` / `@GetExchange` / `@PostExchange`; создаётся через `HttpServiceProxyFactory` поверх WebClient или RestClient; не требует Spring Cloud — ✓ Верно
>
> **Развёрнутое объяснение:**
>
> HTTP Interface Clients — встроенный в Spring Framework 6 механизм генерации HTTP-клиентов из аннотированных интерфейсов:
>
> 1. **Аннотации**: `@HttpExchange` (универсальная), `@GetExchange`, `@PostExchange`, `@PutExchange`, `@DeleteExchange`, `@PatchExchange`.
> 2. **Параметры**: `@PathVariable`, `@RequestParam`, `@RequestBody`, `@RequestHeader`, `@CookieValue` — те же что в Spring MVC.
> 3. **Адаптеры**: `WebClientAdapter` (реактивный), `RestClientAdapter` (блокирующий, SB 3.2+), `RestTemplateAdapter` (deprecated).
> 4. **Возвращаемые типы**: блокирующие (`User`), реактивные (`Mono<User>`, `Flux<User>`), `CompletableFuture`.
>
> **Пример:**
> ```java
> interface WeatherClient {
>     @GetExchange("/api/weather/{city}")
>     Weather getWeather(@PathVariable String city);
>
>     @PostExchange("/api/weather")
>     WeatherReport reportWeather(@RequestBody WeatherData data);
>
>     @GetExchange("/api/forecast")
>     Flux<Forecast> getForecast(@RequestParam String city);
> }
>
> @Bean
> WeatherClient weatherClient(WebClient.Builder builder) {
>     WebClient client = builder.baseUrl("https://api.weather.com").build();
>     return HttpServiceProxyFactory
>         .builderFor(WebClientAdapter.create(client))
>         .build()
>         .createClient(WeatherClient.class);
> }
> ```
>
> **Когда применять:** замена `RestTemplate` или Feign в новых сервисах. Особенно удобно для микросервисов, где хочется типизированных контрактов API. Не нужен Spring Cloud — `spring-web` достаточно.
>
> **Подводные камни:** нет встроенной поддержки service discovery (как у Feign+Eureka) — нужно вручную задавать baseUrl. Нет circuit breaker — интегрируется через Resilience4j вручную. Конфигурация ProxyFactory verbose — можно обернуть в свой `@Configuration`.
>
> **Связанные вопросы:** [[Q12]] — RestClient (Spring Boot 3.2+); [[Q1]] — ключевые изменения Spring Boot 3.
>
> ---
>
> #### C) `@HttpExchange` — это просто алиас для `@RequestMapping`, работает только внутри `@RestController` — ❌ Неверно
>
> **Что на самом деле:** `@HttpExchange` создан для **клиентских** интерфейсов (вызов remote API), а не для server-side контроллеров. Объявляется на интерфейсе, и Spring через `HttpServiceProxyFactory` генерирует реализацию — proxy, которая делает HTTP-запросы наружу. `@RequestMapping` — server-side, обрабатывает входящие запросы.
>
> **Откуда путаница:** аннотации действительно похожи (HTTP method + path + параметры), и неопытному может показаться что это server-side рефакторинг.
>
> **Если бы это было правдой:** не было бы смысла в `HttpServiceProxyFactory` и `WebClientAdapter` — но они нужны именно потому, что это клиентская абстракция.
>
> ---
>
> #### D) HTTP Interface Clients генерируют классы при компиляции через APT (annotation processor), как MapStruct — ❌ Неверно
>
> **Что на самом деле:** HTTP Interface Clients **не используют APT** — они работают **в runtime через JDK Dynamic Proxy** (`Proxy.newProxyInstance`). `HttpServiceProxyFactory.createClient()` создаёт прокси-объект, который при вызове методов формирует HTTP-запрос. Нет генерации .class файлов на этапе компиляции.
>
> **Откуда путаница:** MapStruct, Lombok, и некоторые другие Spring-связанные тулзы используют APT. Аналогия может ввести в заблуждение.
>
> **Если бы это было правдой:** в `target/generated-sources/` появлялись бы `WeatherClientImpl.java` — но их нет; интерфейс остаётся интерфейсом, и runtime создаёт proxy.
>

## Q6. Что такое Problem Details (RFC 7807) в Spring Boot 3?

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
>
> **Вопрос:** Что такое Problem Details (RFC 7807) и как Spring Boot 3 их реализует?
>
> ---
>
> #### A) Problem Details — Spring-only стандарт, используется только в `@RestControllerAdvice` через `@ExceptionHandler` — ❌ Неверно
>
> **Что на самом деле:** Problem Details — это **IETF RFC 7807**, открытый стандарт описания ошибок в HTTP API. Не Spring-специфичен — поддерживается Quarkus, Micronaut, ASP.NET, FastAPI. Content-Type `application/problem+json` стандартизирован.
>
> **Откуда путаница:** в Spring Boot 3 Problem Details впервые появился из коробки, и многие думают что это Spring-фича. На самом деле Spring 3 встроил поддержку существующего стандарта.
>
> **Если бы это было правдой:** микросервисы Spring и Quarkus в одной системе не могли бы общаться через стандартные error responses — но они могут, потому что стандарт интероперабельный.
>
> ---
>
> #### D) Стандарт IETF RFC 7807; JSON-объект с полями `type`, `title`, `status`, `detail`, `instance` + любые custom; Content-Type `application/problem+json`; Spring Boot 3 включает по умолчанию через property `spring.mvc.problemdetails.enabled` — ✓ Верно
>
> **Развёрнутое объяснение:**
>
> Problem Details (RFC 7807) определяет стандарт JSON-структуры для error response:
>
> | Поле | Описание |
> |---|---|
> | `type` | URI описания типа ошибки (документация) |
> | `title` | короткое человекочитаемое название |
> | `status` | HTTP status code (дублирует header) |
> | `detail` | подробное описание для этого случая |
> | `instance` | URI запроса (опционально) |
> | `*custom*` | extension members (account_balance, retry_after_seconds) |
>
> Включение в Spring Boot 3:
> ```yaml
> spring:
>   mvc:
>     problemdetails:
>       enabled: true
>   webflux:
>     problemdetails:
>       enabled: true   # для WebFlux отдельно
> ```
>
> **Пример:**
> ```java
> @RestControllerAdvice
> public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {
>     @ExceptionHandler(InsufficientFundsException.class)
>     public ProblemDetail handleInsufficientFunds(InsufficientFundsException ex) {
>         ProblemDetail problem = ProblemDetail.forStatusAndDetail(
>             HttpStatus.PAYMENT_REQUIRED, ex.getMessage());
>         problem.setType(URI.create("https://example.com/errors/insufficient-funds"));
>         problem.setTitle("Insufficient Funds");
>         problem.setProperty("accountBalance", ex.getBalance());
>         return problem;
>     }
> }
> ```
>
> Response:
> ```json
> {
>   "type": "https://example.com/errors/insufficient-funds",
>   "title": "Insufficient Funds",
>   "status": 402,
>   "detail": "Balance $10, purchase requires $100",
>   "instance": "/orders/12345/purchases/789",
>   "accountBalance": 10
> }
> ```
>
> **Когда применять:** все публичные REST API — особенно multi-language / multi-client (web + iOS + Android). Дает клиентам типизированный способ разбора ошибок (по `type` URI можно генерировать локализованные сообщения).
>
> **Подводные камни:** `Spring 6.0` не включал Problem Details по умолчанию — нужно явно ставить property. `Spring Boot 3.2+` упростил, но для `@ControllerAdvice` всё равно нужен `extends ResponseEntityExceptionHandler`. Кастомные fields добавляются через `setProperty`, не как поля POJO.
>
> **Связанные вопросы:** [[Q1]] — ключевые изменения Spring Boot 3; [[Q5]] — HTTP Interface Clients (тоже Spring 6 фича).
>
> ---
>
> #### C) Problem Details — это XML-стандарт W3C для SOAP error envelopes — ❌ Неверно
>
> **Что на самом деле:** RFC 7807 — это IETF (не W3C), и формат — JSON (не XML). Существует XML-вариант `application/problem+xml`, но он редко используется. SOAP имеет свой стандарт SOAP Fault, который никак не связан с Problem Details.
>
> **Откуда путаница:** SOAP-эпоха и обилие XML-стандартов могут наводить на мысль, что любой error standard — XML. На самом деле Problem Details рождён для REST/JSON-эры (2016).
>
> **Если бы это было правдой:** `ProblemDetail` класс в Spring 6 возвращал бы XML по умолчанию — но он сериализуется через Jackson в JSON.
>
> ---
>
> #### B) Problem Details доступен только в Spring WebFlux (reactive), Spring MVC не поддерживает — ❌ Неверно
>
> **Что на самом деле:** Spring Boot 3 поддерживает Problem Details **в обоих стеках** — Spring MVC и Spring WebFlux. Каждый стек имеет свой property:
> - `spring.mvc.problemdetails.enabled` — для Spring MVC
> - `spring.webflux.problemdetails.enabled` — для WebFlux
>
> **Откуда путаница:** WebFlux часто ассоциируется с современными фичами (modern reactive stack), и можно решить, что Problem Details — тоже WebFlux-only.
>
> **Если бы это было правдой:** все приложения на Spring MVC (большинство production-сервисов) не могли бы использовать стандарт — но они могут, и это основной use-case.
>

## Q7. Что такое GraalVM Native Image и как Spring Boot 3 его поддерживает?

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
>
> **Вопрос:** Что такое GraalVM Native Image, какие выгоды и ограничения, как Spring Boot 3 это поддерживает?
>
> ---
>
> #### A) Native Image — это just JIT compilation, ускоренный за счёт HotSpot tiered compilation — ❌ Неверно
>
> **Что на самом деле:** Native Image — **AOT (Ahead-Of-Time)** компиляция, не JIT. Компиляция происходит на этапе сборки (`native:compile`), создаётся single executable файл без JVM внутри. JIT и tiered compilation — runtime-механизмы HotSpot, которые работают в обычном JVM, но не в native image.
>
> **Откуда путаница:** оба механизма «оптимизируют производительность через компиляцию в нативный код», поэтому легко смешать. Но JIT работает в runtime, AOT — в build time.
>
> **Если бы это было правдой:** native image требовался бы JVM для запуска — но executable запускается без JDK на машине (нужны только libc и system libs).
>
> ---
>
> #### C) AOT (Ahead-Of-Time) компиляция через GraalVM в single native executable; startup ~50-100ms, memory ~30% от JVM; ограничения — reflection, dynamic class loading, JNI требуют hints; Spring Boot 3 поддерживает через `spring-boot-starter-parent` AOT processing — ✓ Верно
>
> **Развёрнутое объяснение:**
>
> **GraalVM Native Image** — компилятор от Oracle Labs, который анализирует точку входа приложения, определяет все достижимые классы (closed-world assumption) и генерирует один статический executable.
>
> Выгоды:
> - Startup time: 50-100ms (vs 2-5 секунд для JVM)
> - Memory footprint: ~30% от JVM (нет metaspace, нет JIT compiler в памяти)
> - Размер: 50-80 MB (можно уменьшить через `upx` до 20 MB)
> - Нет JVM dependency на runtime — деплой single binary
>
> Ограничения (closed-world assumption):
> - **Reflection** — требует hints (`@RegisterReflection` или `RuntimeHints`)
> - **Dynamic class loading** (`Class.forName` с runtime-именами) — невозможно
> - **JNI**, **proxy** (`Proxy.newProxyInstance`), **resources** — требуют hints
> - **Unsafe** ограничено
>
> **Пример:**
> ```xml
> <plugin>
>     <groupId>org.graalvm.buildtools</groupId>
>     <artifactId>native-maven-plugin</artifactId>
> </plugin>
> ```
> ```bash
> ./mvnw -Pnative native:compile     # 5-15 минут
> ./target/myapp                      # запуск 50ms
> ```
> ```java
> @ImportRuntimeHints(MyHints.class)
> @Component
> class MyService { }
>
> class MyHints implements RuntimeHintsRegistrar {
>     public void registerHints(RuntimeHints hints, ClassLoader loader) {
>         hints.reflection().registerType(MyDto.class,
>             MemberCategory.INVOKE_DECLARED_CONSTRUCTORS,
>             MemberCategory.DECLARED_FIELDS);
>     }
> }
> ```
>
> **Когда применять:** serverless (AWS Lambda, Cloud Run, Knative) — где cold start критичен. Edge computing, CLI tools на JVM. Микросервисы с высоким количеством инстансов и низким уровнем нагрузки на инстанс.
>
> **Подводные камни:** время сборки 5-15 минут vs секунды для JVM — медленнее dev loop. Некоторые библиотеки не имеют GraalVM-hints (особенно специфичные internal). Peak throughput может быть ниже чем у JVM с разогретым JIT для long-running.
>
> **Связанные вопросы:** [[Q8]] — AOT processing для JVM mode; [[Q1]] — ключевые изменения SB 3; [[Q10]] — Virtual Threads (другой подход к производительности).
>
> ---
>
> #### B) Native Image работает на CRaC (Coordinated Restore at Checkpoint) — снимок памяти JVM — ❌ Неверно
>
> **Что на самом деле:** CRaC и GraalVM Native Image — **разные технологии**. CRaC — это снимок состояния запущенной JVM (как hibernate ноутбука), запуск восстанавливает heap и stack. Native Image — настоящая AOT компиляция в native машинный код.
>
> **Откуда путаница:** обе технологии решают «cold start», и обе появились в эту же эпоху (2022-2023). Spring Boot 3 поддерживает CRaC отдельно (через Project CRaC), но это не Native Image.
>
> **Если бы это было правдой:** native image требовал бы checkpoint-файл рядом с executable — но он самодостаточен, один binary, без снапшотов.
>
> ---
>
> #### D) GraalVM Native Image интерпретирует Java bytecode без компиляции, ускорение через предзагрузку классов — ❌ Неверно
>
> **Что на самом деле:** Native Image полностью **компилирует** bytecode в нативный машинный код через GraalVM compiler. Это **не интерпретация** — после сборки JVM bytecode внутри executable вообще нет.
>
> **Откуда путаница:** в JVM существует interpreter (для not-yet-JITed методов). Кто-то может предположить, что Native Image — это просто interpreter, ускоренный AOT-preload.
>
> **Если бы это было правдой:** размер executable был бы равен размеру JVM + bytecode (200+ MB), и были бы те же ограничения по startup, что и в обычной JVM. На практике native image — это компилированный код, без bytecode runtime.
>

## Q8. Как работает AOT processing в Spring Boot 3?

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
>
> **Вопрос:** Что такое AOT processing в Spring Boot 3 и какое отличие от GraalVM Native Image?
>
> ---
>
> #### B) AOT processing = GraalVM Native Image; разные имена одной фичи — ❌ Неверно
>
> **Что на самом деле:** **AOT processing — это шаг ПОДГОТОВКИ к Native Image, но ОН ЖЕ работает и для JVM**. Spring AOT engine анализирует приложение и генерирует дополнительные классы (BeanDefinitionRegistrar, ReflectionHints), которые ускоряют startup. Native Image — следующий опциональный шаг, который компилирует всё в native executable.
>
> **Откуда путаница:** AOT processing появился в Spring Boot 3 вместе с GraalVM поддержкой, и упоминания часто идут парой. Но AOT работает и в обычном JAR, без Native Image.
>
> **Если бы это было правдой:** не было бы смысла в `spring.aot.enabled=true` для обычного JVM запуска. На практике это property даёт 30-50% ускорение startup даже без GraalVM.
>
> ---
>
> #### A) AOT (Ahead-Of-Time) processing в Spring Boot 3 — генерация pre-computed bean definitions, reflection hints, resource hints на этапе сборки; работает как для GraalVM Native, так и для обычного JVM (через `spring.aot.enabled=true`); ускоряет startup на 30-50% даже в JVM mode — ✓ Верно
>
> **Развёрнутое объяснение:**
>
> Spring AOT engine на этапе сборки:
> 1. **Сканирует приложение** — все `@Component`, `@Configuration`, `@Bean`
> 2. **Генерирует Java-классы**:
>    - Pre-computed bean definitions (без reflection на classpath сканирование)
>    - Optimized `@Configuration` processing
>    - Reflection hints (`reflect-config.json` для GraalVM)
>    - Resource hints (`resource-config.json` — какие `.yml`, `.properties` нужны)
>    - Proxy hints (для CGLIB/JDK proxies)
> 3. **Замещает runtime reflection** статическим кодом
>
> Два режима использования:
> - **JVM mode**: `mvn spring-boot:process-aot` + `java -Dspring.aot.enabled=true -jar app.jar` → 30-50% faster startup
> - **Native mode**: `mvn -Pnative native:compile` → AOT generation + GraalVM compile → 95% faster startup
>
> **Пример:**
> ```bash
> # JVM mode с AOT
> ./mvnw spring-boot:process-aot
> java -Dspring.aot.enabled=true -jar target/myapp.jar
> # Startup: 1.2s вместо 2.5s
>
> # Native mode
> ./mvnw -Pnative native:compile
> ./target/myapp
> # Startup: 80ms
> ```
>
> AOT processing генерирует код в `target/spring-aot/main/sources/`:
> ```java
> // Сгенерированный AOT класс
> public class ApplicationContextInitializer__BeanDefinitions {
>     public static void registerBeanDefinitions(BeanDefinitionRegistry registry) {
>         // pre-computed bean registrations
>     }
> }
> ```
>
> **Когда применять:** AOT в JVM mode — для приложений, чувствительных к startup time, но не желающих переходить на Native (CI/CD, integration tests). Native — для serverless/edge.
>
> **Подводные камни:** при AOT processing динамические профили (`@Profile` с runtime-условиями) могут вести себя иначе — bean registration финализируется на этапе сборки. Тестировать AOT-сборку отдельно в pipeline.
>
> **Связанные вопросы:** [[Q7]] — GraalVM Native Image; [[Q1]] — ключевые изменения SB 3; [[Q10]] — Virtual Threads.
>
> ---
>
> #### C) AOT processing — это просто кеширование результатов рефлексии в локальном файле `.aot-cache` — ❌ Неверно
>
> **Что на самом деле:** AOT processing — не кеширование, а **генерация настоящих Java-классов** на этапе сборки. Эти классы компилируются в .class файлы и попадают в jar. Runtime их использует напрямую без reflection.
>
> **Откуда путаница:** «pre-computation» звучит как «кеш», но фактически это generation of source code, который компилируется в bytecode.
>
> **Если бы это было правдой:** не нужно было бы пересобирать jar — достаточно было бы первого запуска для построения кеша. На практике AOT — часть Maven/Gradle build pipeline.
>
> ---
>
> #### D) AOT processing — это часть JVM HotSpot JIT, активирующаяся через `-XX:+UseAOTCompilation` — ❌ Неверно
>
> **Что на самом деле:** Spring AOT processing — это **Spring-фреймворк-уровень** (build-time generation of Java classes). Это не JVM flag и не JIT-фича. JDK 9-16 имел JVM-level AOT (`jaotc`), но он deprecated в JDK 17. Spring AOT работает на уровне Java code generation, не bytecode.
>
> **Откуда путаница:** оба слова содержат «AOT». JDK AOT (jaotc) был ранним экспериментом Oracle. Spring AOT — независимая фича.
>
> **Если бы это было правдой:** AOT работало бы для любого Java-приложения через JVM flag — но `-Dspring.aot.enabled=true` это Spring-specific property.
>

## Q9. Какие изменения в Observability?

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
>
> **Вопрос:** Какие ключевые изменения в Observability в Spring Boot 3 и почему Spring Cloud Sleuth был удалён?
>
> ---
>
> #### A) Spring Cloud Sleuth обновлён до версии 4.x, но API сохранён обратно совместимым — ❌ Неверно
>
> **Что на самом деле:** Spring Cloud Sleuth **прекращён** в эпоху Spring Boot 3. Команда Sleuth (Marcin Grzejszczak) перешла в Micrometer и создала **Micrometer Tracing** — преемник, основанный на единой Observation API. Sleuth-овый код был перенесён в `micrometer-tracing` и `micrometer-tracing-bridge-*`.
>
> **Откуда путаница:** в Spring Cloud 2022.0.x ещё были артефакты `spring-cloud-sleuth-*`, но они помечены deprecated и не работают с Boot 3 без миграции на Micrometer.
>
> **Если бы это было правдой:** проекты могли бы добавить `spring-cloud-starter-sleuth` в SB 3 — но это даёт `NoClassDefFoundError` из-за javax/jakarta несовместимости.
>
> ---
>
> #### D) Micrometer Observation API заменил Spring Cloud Sleuth; единый API для metrics + tracing; интеграция через `micrometer-tracing-bridge-otel` / `-brave`; экспорт в Zipkin/OTLP/Wavefront; AOP `@Observed` для автоинструментации — ✓ Верно
>
> **Развёрнутое объяснение:**
>
> Архитектура Observability в Spring Boot 3:
>
> 1. **Micrometer Observation API** — core abstraction, единый API для записи measurement (метрики + трейсы из одного места).
> 2. **Micrometer Tracing** — преемник Sleuth, через bridge подключается к backend (Brave/OpenZipkin или OpenTelemetry).
> 3. **`@Observed`** — AOP-аннотация для автоматического создания observation из метода.
> 4. **Auto-instrumentation** — Spring MVC, WebFlux, RestTemplate, WebClient, RestClient, Kafka, JDBC автоматически создают observations.
>
> **Пример:**
> ```xml
> <dependency>
>     <groupId>io.micrometer</groupId>
>     <artifactId>micrometer-tracing-bridge-otel</artifactId>
> </dependency>
> <dependency>
>     <groupId>io.opentelemetry</groupId>
>     <artifactId>opentelemetry-exporter-zipkin</artifactId>
> </dependency>
> ```
> ```java
> @Service
> class OrderService {
>     private final ObservationRegistry registry;
>
>     public Order process(OrderCommand cmd) {
>         return Observation.createNotStarted("order.process", registry)
>             .contextualName("process-order")
>             .lowCardinalityKeyValue("type", cmd.type())
>             .highCardinalityKeyValue("orderId", cmd.id())
>             .observe(() -> doProcess(cmd));
>     }
> }
>
> // AOP вариант
> @Observed(name = "order.process")
> public Order process(OrderCommand cmd) { ... }
> ```
> ```yaml
> management:
>   tracing:
>     sampling:
>       probability: 1.0      # 100% в dev, 0.1 в prod
>   zipkin:
>     tracing:
>       endpoint: http://localhost:9411/api/v2/spans
> ```
>
> **Когда применять:** все микросервисы Spring Boot 3+ — Sleuth больше не вариант. OpenTelemetry-стек для cross-platform трейсинга (Java + Go + Python). Zipkin/Brave если уже инвестировано в Zipkin UI.
>
> **Подводные камни:** `Observation` имеет low-cardinality (для метрик, ограниченный набор значений) и high-cardinality (для трейсов, любые значения). Использовать `highCardinalityKeyValue` для metric labels — приведёт к cardinality explosion в Prometheus.
>
> **Связанные вопросы:** [[Q1]] — ключевые изменения SB 3; [[Q15]] — типичные проблемы при миграции.
>
> ---
>
> #### C) Spring Boot 3 использует OpenTelemetry напрямую, без Micrometer; Micrometer удалён — ❌ Неверно
>
> **Что на самом деле:** **Micrometer не удалён** — наоборот, его роль усилена. Micrometer остаётся основным API для metrics, и Micrometer Tracing добавлен как преемник Sleuth. OpenTelemetry — один из backend-ов, к которому Micrometer подключается через bridge (`micrometer-tracing-bridge-otel`). Прямой OpenTelemetry SDK тоже работает, но Spring рекомендует Micrometer.
>
> **Откуда путаница:** OpenTelemetry — модный термин в 2023-2024, и многие думают, что Spring отказался от Micrometer в пользу OTel.
>
> **Если бы это было правдой:** `MeterRegistry` API не работал бы в SB 3 — но он работает, и большинство приложений всё ещё используют `meterRegistry.counter(...)`.
>
> ---
>
> #### B) Tracing в Spring Boot 3 настраивается только через JFR (Java Flight Recorder), не через Micrometer — ❌ Неверно
>
> **Что на самом деле:** **JFR — это JVM-level профайлер для дампов, не для distributed tracing**. Tracing спанов между микросервисами идёт через Micrometer Tracing + Zipkin/OTel. JFR используется для производительности и stack profiling, никак не пересекается с distributed tracing.
>
> **Откуда путаница:** JFR недавно стал open-source (JDK 11+), и его расширенная функциональность может казаться достаточной для трейсинга. Но между процессами JFR не передаёт trace_id.
>
> **Если бы это было правдой:** не нужны были бы Zipkin/Jaeger UI — но они стандарт для multi-service tracing в production.
>

## Q10. Что нужно знать о поддержке Virtual Threads в Spring Boot 3.2+?

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
>
> **Вопрос:** Что нужно знать о поддержке Virtual Threads в Spring Boot 3.2+ и для каких задач они дают выигрыш?
>
> ---
>
> #### A) Virtual Threads ускоряют CPU-bound вычисления через лёгкие потоки и parallel computation — ❌ Неверно
>
> **Что на самом деле:** Virtual Threads (JEP 444, Java 21) — это **lightweight threads для I/O-bound нагрузок**, не для CPU-bound. Их преимущество — миллионы виртуальных потоков на JVM с минимальной памятью (~1KB на VT vs ~1MB на platform thread). При CPU-bound работе они **не дают выигрыша** — bottleneck остаётся в количестве CPU-ядер.
>
> **Откуда путаница:** «virtual» звучит как магия, и можно подумать, что VT решают все проблемы concurrency. На деле они решают только проблему «много блокирующих I/O вызовов».
>
> **Если бы это было правдой:** CPU-bound задачи (hash computation, ML inference) ускорялись бы — но на 8-ядерном CPU больше 8 параллельных CPU задач не запустятся быстрее, независимо от типа потока.
>
> ---
>
> #### B) Lightweight threads (Project Loom, JEP 444 в Java 21); ~1KB на VT, миллионы потоков на JVM; ускоряют I/O-bound (DB, HTTP, file); Spring Boot 3.2+ включает через `spring.threads.virtual.enabled=true` для Tomcat/Jetty/@Async/@Scheduled — ✓ Верно
>
> **Развёрнутое объяснение:**
>
> Virtual Threads — Project Loom финализирован в Java 21 (JEP 444):
> - Управляются JVM (не OS), pinned к platform thread только во время выполнения
> - При `Thread.sleep()`, blocking I/O, `LockSupport.park()` — VT откладывается, platform thread освобождается
> - Запоминается стек, потом продолжается на любом platform thread
>
> Spring Boot 3.2+ интеграция:
> ```yaml
> spring:
>   threads:
>     virtual:
>       enabled: true   # Tomcat worker, @Async, @Scheduled, WebFlux
> ```
>
> Что переключается:
> - **Tomcat request handling** — каждый HTTP request на свой VT
> - **`@Async` методы** — VT executor вместо обычного ThreadPoolTaskExecutor
> - **`@Scheduled` tasks** — VT scheduler
> - **WebFlux** опционально может использовать VT
>
> **Пример:**
> ```java
> // С virtual threads request обрабатывается на VT
> @RestController
> class OrderController {
>     @GetMapping("/orders/{id}")
>     public Order get(@PathVariable Long id) {
>         // Этот thread — virtual; blocking call безопасен
>         return orderService.find(id);  // JDBC blocking call
>     }
> }
>
> // Кастомный VT executor
> @Bean
> TomcatProtocolHandlerCustomizer<?> protocolHandlerVirtualThreadExecutorCustomizer() {
>     return protocolHandler -> protocolHandler.setExecutor(
>         Executors.newVirtualThreadPerTaskExecutor()
>     );
> }
> ```
>
> **Когда применять:** I/O-bound сервисы — HTTP API с большим числом параллельных запросов, JDBC-heavy сервисы. Хорошо работает там, где раньше использовался WebFlux только ради concurrency (но команда не хочет реактивности).
>
> **Подводные камни:**
> - **Pinning**: `synchronized` блоки пиннят VT к platform thread (нельзя освободить). Использовать `ReentrantLock` вместо.
> - **ThreadLocal**: миллион VT × миллион ThreadLocal = OOM. Использовать `ScopedValue` (preview API).
> - **Не помогает для CPU-bound** — bottleneck в количестве cores.
> - **JNI/native код**: блокирует platform thread (не VT-aware).
>
> **Связанные вопросы:** [[Q1]] — ключевые изменения SB 3; [[Q7]] — GraalVM Native (альтернатива для cold start); [[Q9]] — Observability.
>
> ---
>
> #### C) Virtual Threads — это просто новое имя для `ForkJoinPool.commonPool()`; никаких runtime-изменений в JVM — ❌ Неверно
>
> **Что на самом деле:** Virtual Threads — фундаментально новая JVM-фича в Java 19 (preview) → Java 21 (GA). Это не псевдоним для ForkJoinPool. ForkJoinPool управляет workers (platform threads), а VT — это объекты, выполняемые НА carrier threads (которые могут быть из ForkJoinPool, но это разные уровни абстракции).
>
> **Откуда путаница:** carrier thread pool для VT использует ForkJoinPool под капотом, и поверхностно может казаться что это одно и то же. На самом деле VT — отдельная сущность с unmount/mount семантикой.
>
> **Если бы это было правдой:** проблема blocking I/O в ForkJoinPool была бы решена ещё в Java 8 — но она не решена, и поэтому Project Loom разрабатывался 5+ лет.
>
> ---
>
> #### D) Virtual Threads автоматически работают только в Spring WebFlux (reactive), для Spring MVC нужен ручной adapter — ❌ Неверно
>
> **Что на самом деле:** **Spring MVC** — основной сценарий применения VT. Tomcat/Jetty в Spring Boot 3.2 при `spring.threads.virtual.enabled=true` использует VT для request handling — это эквивалент «дешёвой реактивности» для blocking MVC кода. **WebFlux** уже non-blocking и в VT не нуждается принципиально.
>
> **Откуда путаница:** все недавние performance-фичи (reactive, VT) могут казаться «современным WebFlux-стеком». На деле VT — это **способ сделать Spring MVC масштабируемым без переписывания на WebFlux**.
>
> **Если бы это было правдой:** VT не приносили бы выгоды большинству Spring-приложений (они MVC) — но именно для MVC они самые полезные.
>

## Q11. Какие breaking changes в Spring Security 6?

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
>
> **Вопрос:** Какие главные breaking changes в Spring Security 6 (Spring Boot 3)?
>
> ---
>
> #### B) Spring Security 6 удалил `SecurityFilterChain`, заменил его на `HttpSecurity` напрямую как Bean — ❌ Неверно
>
> **Что на самом деле:** Наоборот, **`SecurityFilterChain` — НОВЫЙ рекомендованный способ** конфигурации в Spring Security 6. `HttpSecurity` — это builder API, а `SecurityFilterChain` — финальный результат, который регистрируется как Bean. До 6.x был `WebSecurityConfigurerAdapter` (deprecated), теперь его удалили.
>
> **Откуда путаница:** в SS 5.7+ оба способа были доступны параллельно, и легко запутаться кто заменил кого.
>
> **Если бы это было правдой:** `@Bean HttpSecurity` не имел бы смысла — `HttpSecurity` это builder с состоянием, нельзя его регистрировать как Bean.
>
> ---
>
> #### A) `WebSecurityConfigurerAdapter` удалён → `SecurityFilterChain` Bean; `authorizeRequests` → `authorizeHttpRequests`; `antMatchers` → `requestMatchers`; Customizer-лямбды обязательны вместо `.and()`; defaults более строгие (CSRF включён, formLogin не дефолтный) — ✓ Верно
>
> **Развёрнутое объяснение:**
>
> Главные breaking changes Spring Security 6:
>
> 1. **`WebSecurityConfigurerAdapter` УДАЛЁН** — нельзя `extends WebSecurityConfigurerAdapter`. Вместо этого `@Bean SecurityFilterChain`.
> 2. **`authorizeRequests()` deprecated → `authorizeHttpRequests()`** — новый API с улучшенной типизацией.
> 3. **`antMatchers()` → `requestMatchers()`** — единый API для path matching (раньше были `antMatchers`, `mvcMatchers`, `regexMatchers`).
> 4. **Customizer-лямбды**: `.authorizeHttpRequests(auth -> ...)` вместо `.authorizeHttpRequests().and()`.
> 5. **CSRF включён по умолчанию** — для stateless API нужен explicit `.csrf(csrf -> csrf.disable())`.
> 6. **OAuth2 Resource Server** — упрощён, JWT decoders настраиваются через property `spring.security.oauth2.resourceserver.jwt.issuer-uri`.
>
> **Пример:**
> ```java
> // ДО (Spring Security 5, SB 2.x) — DEPRECATED
> @EnableWebSecurity
> class SecurityConfig extends WebSecurityConfigurerAdapter {
>     @Override
>     protected void configure(HttpSecurity http) throws Exception {
>         http.authorizeRequests()
>             .antMatchers("/public").permitAll()
>             .anyRequest().authenticated()
>             .and().formLogin();
>     }
> }
>
> // ПОСЛЕ (Spring Security 6, SB 3.x)
> @Configuration
> @EnableWebSecurity
> class SecurityConfig {
>     @Bean
>     SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
>         http
>             .authorizeHttpRequests(auth -> auth
>                 .requestMatchers("/public").permitAll()
>                 .requestMatchers("/admin/**").hasRole("ADMIN")
>                 .anyRequest().authenticated())
>             .formLogin(Customizer.withDefaults())
>             .csrf(csrf -> csrf.disable());   // если stateless API
>         return http.build();
>     }
> }
> ```
>
> **Когда применять:** обязательно при миграции на Spring Boot 3. OpenRewrite recipe `org.openrewrite.java.spring.security6.UpgradeSpringSecurity_6_0` частично автоматизирует.
>
> **Подводные камни:** многие `@EnableMethodSecurity` defaults изменились (`prePostEnabled=true` дефолт вместо false). `RoleHierarchy` Bean больше не автоматически подхватывается — нужно явная регистрация. `H2 Console` блокируется CSRF — для dev `.requestMatchers(toH2Console()).permitAll()` + disable CSRF на этом пути.
>
> **Связанные вопросы:** [[Q1]] — ключевые изменения SB 3; [[Q3]] — порядок миграции; [[Q15]] — типичные проблемы.
>
> ---
>
> #### C) Spring Security 6 полностью отказался от Servlet Filter архитектуры в пользу Reactive Streams — ❌ Неверно
>
> **Что на самом деле:** Spring Security **продолжает использовать Servlet Filter** для Spring MVC (`spring-security-web`). Для WebFlux отдельный `spring-security-webflux` с реактивными filter (`WebFilter`). Оба стека сохранены, ничего не объединено.
>
> **Откуда путаница:** Spring 6 / Spring Boot 3 ассоциируется с reactive, и кто-то может предположить unified reactive Security.
>
> **Если бы это было правдой:** MVC-приложения сломались бы при апгрейде — но они работают, и `SecurityFilterChain` остаётся servlet filter chain.
>
> ---
>
> #### D) Spring Security 6 перевёл всю аутентификацию на JWT по умолчанию; form login удалён — ❌ Неверно
>
> **Что на самом деле:** **Form login полноценно поддерживается** в Spring Security 6 (`http.formLogin(Customizer.withDefaults())`). JWT — это просто один из вариантов (через `oauth2ResourceServer().jwt()`), не дефолт. Basic Auth, OAuth2 Login, SAML2, LDAP — все типы аутентификации сохранены.
>
> **Откуда путаница:** API-first архитектуры массово переходят на JWT, и можно предположить что Spring следует тренду по умолчанию.
>
> **Если бы это было правдой:** все web-приложения с HTML формой логина ломались бы — но они работают штатно.
>

## Q12. Что такое декларативный RestClient?

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
>
> **Вопрос:** Что такое `RestClient` в Spring Boot 3.2+, чем он отличается от `RestTemplate` и `WebClient`?
>
> ---
>
> #### A) `RestClient` — это reactive replacement для `WebClient`, использующий Project Reactor — ❌ Неверно
>
> **Что на самом деле:** `RestClient` — **БЛОКИРУЮЩИЙ** HTTP-клиент. Это рекомендованная замена `RestTemplate` с fluent API похожим на WebClient, но без Mono/Flux. `WebClient` остаётся reactive (Mono/Flux), и они нужны для разных сценариев.
>
> **Откуда путаница:** имя «RestClient» похоже на «WebClient», и многие думают, что это reactive-эволюция WebClient. На самом деле это блокирующая альтернатива для не-reactive контекста.
>
> **Если бы это было правдой:** `RestClient.create()` возвращал бы реактивные типы — но `restClient.get().retrieve().body(User.class)` возвращает `User` напрямую, blocking.
>
> ---
>
> #### B) Блокирующий HTTP-клиент в Spring Boot 3.2+; fluent API похожий на WebClient; рекомендованная замена `RestTemplate`; синхронные `body(Class)` вызовы; `WebClient` остаётся для reactive (WebFlux) — ✓ Верно
>
> **Развёрнутое объяснение:**
>
> Эволюция HTTP-клиентов в Spring:
> - **`RestTemplate`** — старый блокирующий клиент. Не deprecated, но рекомендуется RestClient.
> - **`WebClient`** — reactive non-blocking (Mono/Flux) для WebFlux или async вызовов в MVC.
> - **`RestClient`** (Spring 6.1, Spring Boot 3.2+) — блокирующий с современным fluent API.
> - **`@HttpExchange`** — декларативные интерфейсы (поверх RestClient или WebClient).
>
> **Пример:**
> ```java
> // Создание (auto-configured RestClient.Builder доступен в SB 3.2+)
> RestClient client = RestClient.create("https://api.example.com");
>
> // GET
> Order order = client.get()
>     .uri("/orders/{id}", 123)
>     .retrieve()
>     .body(Order.class);
>
> // POST с body
> OrderResponse response = client.post()
>     .uri("/orders")
>     .contentType(MediaType.APPLICATION_JSON)
>     .body(new CreateOrderRequest(...))
>     .retrieve()
>     .body(OrderResponse.class);
>
> // Обработка ошибок
> ResponseEntity<Order> entity = client.get()
>     .uri("/orders/{id}", 999)
>     .retrieve()
>     .onStatus(HttpStatusCode::is4xxClientError,
>         (req, res) -> { throw new NotFoundException(); })
>     .toEntity(Order.class);
>
> // Exchange handler (полный контроль)
> Order o = client.get()
>     .uri("/orders/{id}", 123)
>     .exchange((req, res) -> objectMapper.readValue(res.getBody(), Order.class));
> ```
>
> **Когда применять:** новые сервисы на Spring MVC — сразу RestClient (не RestTemplate). При миграции с RestTemplate — постепенный refactor, RestTemplate не deprecated и продолжает работать.
>
> **Подводные камни:** `RestClient.Builder` auto-configured в SB 3.2+ как `restClientBuilder()` Bean. `RestClient` thread-safe, можно создать один на приложение. Для timeout — настраивать через `ClientHttpRequestFactory` (например `JdkClientHttpRequestFactory.setReadTimeout()`).
>
> **Связанные вопросы:** [[Q5]] — HTTP Interface Clients (поверх RestClient); [[Q1]] — изменения SB 3; [[Q10]] — Virtual Threads (с RestClient блокирующий код становится дешёвым).
>
> ---
>
> #### C) `RestClient` — это просто алиас для `RestTemplate`; класс переименован для согласованности — ❌ Неверно
>
> **Что на самом деле:** `RestClient` — **полностью новый класс** в Spring Framework 6.1 с современным fluent API. `RestTemplate` остался с императивным API (`exchange()`, `getForObject()`). Это два разных класса, не алиасы.
>
> **Откуда путаница:** оба клиента блокирующие и решают похожие задачи, поэтому может казаться что один заменил имя другого.
>
> **Если бы это было правдой:** не было бы смысла иметь два класса. На практике `RestTemplate` остаётся для legacy code, `RestClient` для нового.
>
> ---
>
> #### D) `RestClient` интегрирован только через Spring Cloud Stream; для standalone Spring Boot 3 нужен отдельный starter — ❌ Неверно
>
> **Что на самом деле:** `RestClient` — часть `spring-web` (core Spring Framework 6.1+). Доступен в любом Spring Boot 3.2+ приложении автоматически. Не нужно Spring Cloud, не нужен отдельный starter.
>
> **Откуда путаница:** Spring Cloud OpenFeign — отдельный starter, и аналогия может ввести в заблуждение.
>
> **Если бы это было правдой:** для использования RestClient пришлось бы добавлять `spring-cloud-starter-*` зависимость — но достаточно `spring-boot-starter-web`.
>

## Q13. Какие изменения в auto-configuration?

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
>
> **Вопрос:** Какое главное изменение в механизме auto-configuration в Spring Boot 3?
>
> ---
>
> #### A) Auto-configuration теперь работает через annotation processor — все `@AutoConfiguration` классы генерируются как компилируемые .class — ❌ Неверно
>
> **Что на самом деле:** Auto-configuration **по-прежнему** работает через runtime classpath scanning, не APT. Изменился только **способ объявления** auto-configuration классов: вместо `META-INF/spring.factories` теперь файл `META-INF/spring/org.springframework.boot.autoconfigure.AutoConfiguration.imports` с просто списком class FQN, один на строку.
>
> **Откуда путаница:** появилась новая аннотация `@AutoConfiguration` (раньше использовался `@Configuration`), и может казаться, что появилась compile-time генерация.
>
> **Если бы это было правдой:** в `target/generated-sources/` появлялись бы файлы — но их нет. Auto-configuration discovery остаётся runtime механизмом.
>
> ---
>
> #### D) `spring.factories` заменён на `META-INF/spring/org.springframework.boot.autoconfigure.AutoConfiguration.imports` (plain text file, один class FQN на строку); `@AutoConfiguration` — новая marker-аннотация вместо `@Configuration`; старый формат deprecated, но всё ещё работает — ✓ Верно
>
> **Развёрнутое объяснение:**
>
> Изменения в declaration auto-configuration:
>
> 1. **Старый формат (Spring Boot 2.x)** — `META-INF/spring.factories`:
>    ```properties
>    org.springframework.boot.autoconfigure.EnableAutoConfiguration=\
>        com.example.MyAutoConfiguration,\
>        com.example.AnotherAutoConfiguration
>    ```
>    Многострочный, через `\` continuation, сложно мерджить в git.
>
> 2. **Новый формат (Spring Boot 3.x)** — `META-INF/spring/org.springframework.boot.autoconfigure.AutoConfiguration.imports`:
>    ```
>    com.example.MyAutoConfiguration
>    com.example.AnotherAutoConfiguration
>    ```
>    Один класс на строку, без `=`, без `\` — git-friendly.
>
> 3. **`@AutoConfiguration`** — новая аннотация. Раньше использовался `@Configuration` + объявление в `spring.factories`. Теперь:
>    ```java
>    @AutoConfiguration
>    @ConditionalOnClass(DataSource.class)
>    public class MyDataSourceAutoConfiguration {
>        @Bean
>        @ConditionalOnMissingBean
>        public DataSource dataSource() { ... }
>    }
>    ```
>    `@AutoConfiguration` имеет `before`/`after` атрибуты для ordering:
>    ```java
>    @AutoConfiguration(after = DataSourceAutoConfiguration.class)
>    public class MyOrmAutoConfiguration { ... }
>    ```
>
> **Пример migration:**
> ```diff
> - // src/main/resources/META-INF/spring.factories
> - org.springframework.boot.autoconfigure.EnableAutoConfiguration=\
> -   com.example.MyAutoConfiguration
> +
> + // src/main/resources/META-INF/spring/
> + //     org.springframework.boot.autoconfigure.AutoConfiguration.imports
> + com.example.MyAutoConfiguration
> ```
>
> **Когда применять:** при создании Spring Boot starter в SB 3+. При миграции существующих starters — заменить spring.factories на .imports файл. Spring Boot 3 поддерживает оба, но новые проекты должны использовать .imports.
>
> **Подводные камни:** `spring.factories` всё ещё используется для других целей (ApplicationListener, EnvironmentPostProcessor) — он не удалён полностью, только EnableAutoConfiguration перенесён. Не путать с `META-INF/spring.factories` который ещё может содержать `org.springframework.context.ApplicationListener=...`.
>
> **Связанные вопросы:** [[Q8]] — AOT processing (тоже изменения в инфраструктуре); [[Q1]] — ключевые изменения SB 3.
>
> ---
>
> #### C) Auto-configuration полностью удалена; все Spring Boot starter должны явно объявлять `@Configuration` классы в `META-INF/services` — ❌ Неверно
>
> **Что на самом деле:** Auto-configuration **сохранена** и остаётся ключевой фичей Spring Boot 3. Файл `META-INF/services` — это JDK ServiceLoader механизм, никак не связан со Spring auto-configuration. Spring использует свой собственный mechanism для discovery.
>
> **Откуда путаница:** JDK ServiceLoader использует похожий по идее формат (один class на строку в `META-INF/services/<interface>`).
>
> **Если бы это было правдой:** Spring Boot 3 starters не работали бы автоматически — но `spring-boot-starter-data-jpa` подключается и auto-configures DataSource как обычно.
>
> ---
>
> #### B) `@AutoConfiguration` — это deprecated alias для `@Configuration`, рекомендуется не использовать — ❌ Неверно
>
> **Что на самом деле:** Наоборот, `@AutoConfiguration` — **рекомендованная** аннотация для auto-configuration классов в Spring Boot 3+. Она расширяет `@Configuration` плюс добавляет специфические для auto-config возможности (`before`/`after` ordering). `@Configuration` сам по себе всё ещё работает, но `@AutoConfiguration` несёт явный intent.
>
> **Откуда путаница:** «add new annotation, keep old one» часто означает deprecation. Здесь наоборот — старый способ устаревает, новый рекомендуется.
>
> **Если бы это было правдой:** Spring Boot 3 internal starters не использовали бы `@AutoConfiguration` — но они используют.
>

## Q14. Что такое Configuration Properties Migrator?

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
>
> **Вопрос:** Что такое `spring-boot-properties-migrator` и как он помогает при миграции на Spring Boot 3?
>
> ---
>
> #### A) Это автоматический rewrite-инструмент, который переименовывает properties в исходных файлах `application.yml` — ❌ Неверно
>
> **Что на самом деле:** `spring-boot-properties-migrator` **не модифицирует** файлы исходного кода или конфигурации. Он работает в runtime: при запуске приложения логирует WARNING для устаревших или переименованных properties и **временно мапит** старые имена на новые. Изменения в YAML/properties файлах нужно делать вручную (или OpenRewrite recipe).
>
> **Откуда путаница:** название «migrator» наводит на мысль о автоматическом рефакторинге исходников. Реально это runtime diagnostic tool.
>
> **Если бы это было правдой:** после запуска приложения `application.yml` менялся бы on disk — но Spring не пишет в config-файлы пользователя.
>
> ---
>
> #### C) Runtime-инструмент с `scope: runtime`; логирует WARNING про устаревшие/переименованные properties; временно мапит старые имена на новые для поддержания работы приложения; удалить после миграции — ✓ Верно
>
> **Развёрнутое объяснение:**
>
> `spring-boot-properties-migrator` — диагностический модуль:
>
> 1. **Подключение** (только на время миграции):
>    ```xml
>    <dependency>
>        <groupId>org.springframework.boot</groupId>
>        <artifactId>spring-boot-properties-migrator</artifactId>
>        <scope>runtime</scope>
>    </dependency>
>    ```
>
> 2. **Что делает**:
>    - При старте Spring Boot сканирует `application.yml`/`.properties`
>    - Сравнивает с метаданными SB 3 (где описаны renames из 2.x → 3.x)
>    - Логирует WARNING для каждой устаревшей property
>    - **Временно** мапит старые имена на новые, чтобы приложение не упало
>
> 3. **Пример вывода**:
>    ```text
>    The use of configuration keys that have been renamed was found
>    in the environment:
>      Property source: applicationConfig: [classpath:/application.yml]
>        Key: management.metrics.export.prometheus.enabled
>          Replacement: management.prometheus.metrics.export.enabled
>      Key: spring.kafka.consumer.properties.specific.avro.reader
>          Replacement: spring.kafka.consumer.properties.specific\.avro\.reader
>    ```
>
> 4. **Действие**: вручную переименовать в YAML на новые ключи.
>
> 5. **Удалить** dependency после миграции (избежать перфоманс-оверхеда на runtime scanning).
>
> **Пример migration:**
> ```yaml
> # ДО (Spring Boot 2.7)
> management:
>   metrics:
>     export:
>       prometheus:
>         enabled: true
>
> # ПОСЛЕ (Spring Boot 3.0+)
> management:
>   prometheus:
>     metrics:
>       export:
>         enabled: true
> ```
>
> **Когда применять:** временно во время миграции SB 2.7 → 3.x. Подключить → запустить → собрать WARNINGs → исправить YAML → удалить dependency.
>
> **Подводные камни:** не покрывает 100% renames — Spring Boot 3.1, 3.2 добавляют новые renames. Каждый minor upgrade — снова подключить migrator. Не работает для third-party библиотек (только Spring Boot core). Может слегка замедлить startup (~50-100ms) — не оставлять в production.
>
> **Связанные вопросы:** [[Q3]] — последовательность миграции; [[Q9]] — Observability properties renames; [[Q15]] — типичные проблемы.
>
> ---
>
> #### B) Это maven plugin, запускается через `mvn spring-boot:migrate-properties` и переписывает application.yml — ❌ Неверно
>
> **Что на самом деле:** properties-migrator — **runtime dependency**, не Maven plugin. Подключается через `<dependency>` с `<scope>runtime</scope>`, не через `<plugin>`. Maven goal `migrate-properties` не существует. Для рефакторинга на этапе сборки используется OpenRewrite recipe.
>
> **Откуда путаница:** Spring Boot Maven plugin (`spring-boot-maven-plugin`) предоставляет goals (`run`, `repackage`, `build-image`, `process-aot`). Аналогия может ввести в заблуждение.
>
> **Если бы это было правдой:** в логах сборки появлялись бы WARN — но WARN появляются только при `bootRun`/runtime запуске.
>
> ---
>
> #### D) Properties Migrator поддерживает только Spring Cloud Config Server, для локальных application.yml не работает — ❌ Неверно
>
> **Что на самом деле:** properties-migrator работает с **любыми** property sources — `application.yml`, `application.properties`, env vars, command line args, Spring Cloud Config Server. Это universal механизм через Spring `Environment` API.
>
> **Откуда путаница:** Spring Cloud Config Server имеет свои механизмы для конфигурации, и можно предположить специализацию.
>
> **Если бы это было правдой:** standalone приложения (без Cloud Config) не получали бы WARN — но они получают.
>

## Q15. Какие проблемы часто возникают при миграции?

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


> [!mcq]
>
> **Вопрос:** Какие самые частые проблемы возникают при миграции Spring Boot 2.7 → 3.x в production-проектах?
>
> ---
>
> #### A) Главная проблема — необходимость переписать всё на Kotlin, потому что SB 3 не поддерживает Java — ❌ Неверно
>
> **Что на самом деле:** Spring Boot 3 **полноценно поддерживает Java** (требует Java 17+, но это не Kotlin). Java остаётся основным языком для Spring. Kotlin тоже поддерживается, но это альтернатива, не требование.
>
> **Откуда путаница:** в community много hype вокруг Kotlin + Spring, и можно ошибочно подумать что SB 3 forced миграцию.
>
> **Если бы это было правдой:** все existing Spring projects на Java были бы вынуждены переписаться — это огромная стоимость, не реальная.
>
> ---
>
> #### B) Third-party библиотеки без jakarta-поддержки + Hibernate 6 HQL/SQL changes + Spring Security 6 breaking + Sleuth → Micrometer Tracing + Actuator properties renames + Jackson 2.14 stricter parsing — ✓ Верно
>
> **Развёрнутое объяснение:**
>
> Топ-7 типичных проблем при миграции 2.7 → 3.x:
>
> 1. **Jakarta EE несовместимость третьих библиотек**:
>    ```text
>    Error: NoClassDefFoundError: javax/servlet/http/HttpServletRequest
>    → Решение: обновить library (springdoc-openapi-starter-webmvc-ui 2.x вместо springfox)
>    или заменить на jakarta-compatible alternative
>    ```
>
> 2. **Hibernate 6 breaking changes**:
>    - HQL парсер переписан (некоторые запросы перестают парситься)
>    - SQL diaeplct generation отличается (например `nvarchar` vs `varchar` для MSSQL)
>    - `@OneToMany(fetch=LAZY)` дефолт изменился
>    - Удалены некоторые сидераторы (`SequenceStyleGenerator` теперь дефолт для PostgreSQL)
>
> 3. **Spring Security 6**: `WebSecurityConfigurerAdapter` удалён (см. Q11).
>
> 4. **Spring Cloud Sleuth удалён** → переходить на `micrometer-tracing-bridge-otel` или `-brave`.
>
> 5. **Actuator properties переименованы**:
>    ```yaml
>    # старые
>    management.metrics.export.prometheus.enabled
>    management.metrics.export.wavefront.*
>    # новые
>    management.prometheus.metrics.export.enabled
>    management.wavefront.metrics.export.*
>    ```
>
> 6. **WebMVC/WebFlux route changes**:
>    - `antMatchers` → `requestMatchers`
>    - `PathPatternParser` дефолт (более строгий чем `AntPathMatcher`)
>
> 7. **Jackson 2.14+** — strict number parsing; ранее терпимый к "123" в Integer стал throw. HikariCP defaults timeouts сокращены.
>
> **Пример проблем и решений:**
> ```text
> springfox-swagger2 → springdoc-openapi-starter-webmvc-ui 2.x
> spring-cloud-starter-sleuth → micrometer-tracing-bridge-otel
> Hibernate 5.x → 6.x (новый jakarta.persistence + новый HQL parser)
> Lombok < 1.18.24 → 1.18.30+ (Java 17 compatibility)
> Mockito 4.x → 5.x (Java 17 на runtime)
> ```
>
> **Когда применять:** план миграции production-сервиса — выделить минимум 1-2 недели на тестирование. Использовать feature flags для постепенного rollout. Мониторить latency, error rate, GC после deploy.
>
> **Подводные камни:** Hibernate 6 generated SQL может отличаться → integration tests с реальной БД (Testcontainers) обязательны. Свои custom `WebSecurityConfigurerAdapter`-наследники переписывать вручную (OpenRewrite не покрывает 100%).
>
> **Связанные вопросы:** [[Q3]] — порядок миграции; [[Q11]] — Spring Security 6 breaking; [[Q9]] — Observability migration; [[Q14]] — properties migrator.
>
> ---
>
> #### C) Главная проблема — Tomcat 10 потерял HTTP/2 support; нужно перейти на Jetty или Netty — ❌ Неверно
>
> **Что на самом деле:** Tomcat 10 **полноценно поддерживает HTTP/2** (как и Tomcat 9). Главное изменение Tomcat 10 — это переход на jakarta namespace (servlet 5.0). HTTP/2 продолжает работать через `<UpgradeProtocol className="org.apache.coyote.http2.Http2Protocol"/>` или Spring Boot property `server.http2.enabled=true`.
>
> **Откуда путаница:** Tomcat 10 — major version с breaking changes (servlet 5.0), и можно ожидать потерь features. Но HTTP/2 не среди них.
>
> **Если бы это было правдой:** все Spring Boot 3 приложения теряли бы HTTP/2 — но они работают штатно.
>
> ---
>
> #### D) Spring Boot 3 удалил поддержку SQL баз; работает только с NoSQL — ❌ Неверно
>
> **Что на самом деле:** Spring Boot 3 **полноценно поддерживает SQL** — JDBC, JPA (через Hibernate 6), R2DBC, Spring Data JDBC, Spring Data JPA. NoSQL (MongoDB, Redis, Cassandra) тоже поддерживается. Никаких удалений баз.
>
> **Откуда путаница:** возможно от внимания к Hibernate 6 и Jakarta Persistence — кажется что-то «полностью переделано» и могут возникнуть необычные ассоциации.
>
> **Если бы это было правдой:** `spring-boot-starter-data-jpa` не существовал бы — но он есть и активно используется.

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
