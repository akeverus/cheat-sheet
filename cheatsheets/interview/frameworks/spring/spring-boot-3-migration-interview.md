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
> #### B) Java 17 baseline, Jakarta EE 9+ namespace migration, Spring Framework 6, GraalVM Native Image first-class support, Micrometer Observation API (metrics + tracing) — ✓ Верно
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
> #### D) Spring Boot 3 удалил Actuator, заменил его на отдельный starter `spring-boot-starter-observability` — ❌ Неверно
>
> **Что на самом деле:** Actuator **полностью сохранён** в Spring Boot 3 — это `spring-boot-starter-actuator`. Изменилось только то, что Micrometer Observation API заменил Spring Cloud Sleuth для tracing, и переименованы некоторые properties (`management.metrics.export.prometheus.*` → `management.prometheus.metrics.export.*`).
>
> **Откуда путаница:** новость о Micrometer Tracing вытеснении Sleuth многие интерпретировали как «Actuator переделан». Также `spring-boot-properties-migrator` подсвечивает переименования, что усиливает впечатление масштабного слома.
>
> **Если бы это было правдой:** все Kubernetes liveness/readiness probes, healthcheck-эндпоинты `/actuator/health`, метрики Prometheus в SB 3 не работали бы — но они работают штатно.
>
> ---
>
> ## Q2. Что такое миграция с javax на jakarta и почему она нужна?

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
> ---
>
> ## Q3. Как выполнить миграцию с Spring Boot 2.7 на 3.x?

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
> #### A) Сразу `spring-boot-starter-parent` 3.2.0, потом фиксить compile errors одну за другой, тесты переписывать в конце — ❌ Неверно
>
> **Что на самом деле:** прямой прыжок 2.7 → 3.2 обычно даёт **сотни compile errors** одновременно: javax→jakarta, Spring Security 6 breaking, Hibernate 6 HQL изменения, Sleuth удалён, Actuator endpoint переименования. Распутывать это всё разом — путь к багам, особенно когда тесты ещё не работают (нечем проверить корректность правок).
>
> **Откуда путаница:** для маленьких проектов прыжок 2.7→3.2 действительно проходит за час. Для production-приложения с 50+ зависимостями — это недели работы и регресс-баги.
>
> **Если бы это было правдой:** команда правит код «до зелёных тестов», но без поэтапной верификации не понимает, какой именно шаг ввёл регрессию. Типичный сценарий — обнаружение Hibernate 6 HQL bug через 2 недели после релиза в production.
>
> ---
>
> #### B) Сначала обновить JDK до 17 на 2.7.x → обновить до latest 2.7.x (последний minor) → запустить OpenRewrite recipe → обновить parent на 3.x → исправить оставшиеся ошибки и протестировать — ✓ Верно
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
> ---
>
> ## Q4. Какие javax-пакеты НЕ мигрировали на jakarta?

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
> ---
>
> ## Q5. Что такое HTTP Interface Clients в Spring 6?

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
> #### A) HTTP Interface Clients — обёртка над OpenFeign внутри Spring Cloud, требует `@EnableFeignClients` — ❌ Неверно
>
> **Что на самом деле:** HTTP Interface Clients — **встроенная** в Spring Framework 6 фича (не Spring Cloud, не Feign). Создаются через `HttpServiceProxyFactory` поверх `WebClient` или `RestClient`. `@EnableFeignClients` — отдельная аннотация Spring Cloud OpenFeign, никак не связана с `@HttpExchange`.
>
> **Откуда путаница:** оба механизма используют декларативные интерфейсы с HTTP-аннотациями (`@GetMapping`-подобные). Внешне выглядят похоже, поэтому многие думают что Spring встроил Feign в core.
>
> **Если бы это было правдой:** для работы `@HttpExchange` нужна была бы зависимость на `spring-cloud-starter-openfeign` — но это работает чисто на `spring-web` без Spring Cloud.
>
> ---
>
> #### B) Декларативный HTTP-клиент в Spring Framework 6, использует `@HttpExchange` / `@GetExchange` / `@PostExchange`; создаётся через `HttpServiceProxyFactory` поверх WebClient или RestClient; не требует Spring Cloud — ✓ Верно
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
> ---
>
> ## Q6. Что такое Problem Details (RFC 7807) в Spring Boot 3?

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
> #### B) Стандарт IETF RFC 7807; JSON-объект с полями `type`, `title`, `status`, `detail`, `instance` + любые custom; Content-Type `application/problem+json`; Spring Boot 3 включает по умолчанию через property `spring.mvc.problemdetails.enabled` — ✓ Верно
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
> #### D) Problem Details доступен только в Spring WebFlux (reactive), Spring MVC не поддерживает — ❌ Неверно
>
> **Что на самом деле:** Spring Boot 3 поддерживает Problem Details **в обоих стеках** — Spring MVC и Spring WebFlux. Каждый стек имеет свой property:
> - `spring.mvc.problemdetails.enabled` — для Spring MVC
> - `spring.webflux.problemdetails.enabled` — для WebFlux
>
> **Откуда путаница:** WebFlux часто ассоциируется с современными фичами (modern reactive stack), и можно решить, что Problem Details — тоже WebFlux-only.
>
> **Если бы это было правдой:** все приложения на Spring MVC (большинство production-сервисов) не могли бы использовать стандарт — но они могут, и это основной use-case.
>
> ---
>
> ## Q7. Что такое GraalVM Native Image и как Spring Boot 3 его поддерживает?

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
