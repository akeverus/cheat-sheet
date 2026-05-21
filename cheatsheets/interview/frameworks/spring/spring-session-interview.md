---
title: "Вопросы на собеседовании: Spring Session"
description: "Spring Session для внешнего хранения HTTP-сессий: Redis/JDBC/MongoDB store, sticky sessions, кластеризация, интеграция со Spring Security, события"
tags:
  - interview
  - spring
  - spring-session-interview
type: "interview"
difficulty: "intermediate"
aliases:
  - "Вопросы на собеседовании"
  - "Spring Session"
  - "Spring Session interview"
  - "Spring Session собеседование"
prerequisites:
  - "[[spring-session]]"
next: []
updated: "2026-05-14"
---
# Вопросы на собеседовании: `Spring Session`

`Spring Session` — механизм хранения HTTP-сессий вне приложения (Redis, JDBC, MongoDB, Hazelcast). Позволяет масштабировать stateful приложения горизонтально без sticky sessions и обеспечивает session replication между инстансами. Спрашивается в контексте микросервисов и session clustering.

Дата последнего обновления: 2026-05-14

## Полезные ссылки

### Официальная документация

- [Spring Session Docs](https://docs.spring.io/spring-session/reference/) — официальная документация
- [Baeldung: Spring Session](https://www.baeldung.com/spring-session) — практическое введение

## Содержание

- [Полезные ссылки](#полезные-ссылки)
- [See also](#see-also)

## Q1. Что такое Spring Session и какую проблему он решает?

**Spring Session** — абстракция управления сессиями, отделяющая хранилище сессий от контейнера сервлетов (Tomcat/Jetty).

**Проблема без Spring Session**: HTTP-сессия хранится в памяти Tomcat. В кластере из N узлов браузер может попасть на другой сервер и потерять сессию (sticky sessions или Tomcat Session Replication как workarounds).

**Решение**: Spring Session выносит сессии во внешнее хранилище (Redis, JDBC, MongoDB) — любой узел кластера получает доступ к той же сессии.

```java
// Подключение
@EnableRedisIndexedHttpSession
@Configuration
public class SessionConfig { }
```

Поддерживаемые хранилища: Redis (`spring-session-data-redis`), JDBC (`spring-session-jdbc`), MongoDB (`spring-session-mongodb`), Hazelcast.


> [!mcq]
>
> **Вопрос:** Какую главную проблему решает Spring Session в типичном кластерном Spring Boot приложении?
>
> ---
>
> #### A) Spring Session шифрует содержимое HttpSession AES-256 ключом, защищая sensitive данные внутри сессии — ❌ Неверно
>
> **Что на самом деле:** Spring Session не занимается шифрованием полезной нагрузки. Он отделяет хранилище от контейнера сервлетов и предоставляет `SessionRepository` поверх Redis/JDBC/MongoDB. Защита sensitive-атрибутов — отдельная задача (TLS канала + шифрование на уровне хранилища Redis ACL/TLS или серверной БД).
>
> **Откуда путаница:** многие смешивают «session security» (httpOnly, Secure, SameSite cookie) с «session storage». Spring Session работает на втором уровне, конфиденциальность атрибутов — на третьем.
>
> **Если бы это было правдой:** разработчики надеялись бы на «встроенное шифрование» и складывали бы PAN-карт в `session.setAttribute()` — а реально в Redis лежит plain Java-serialized blob, видимый любому с `redis-cli`.
>
> ---
>
> #### B) Spring Session реализует sticky sessions на уровне application — направляет одного пользователя всегда на один и тот же узел через consistent hashing — ❌ Неверно
>
> **Что на самом деле:** Spring Session делает противоположное — устраняет необходимость в sticky sessions. Любой узел кластера читает сессию из внешнего store, поэтому LB может балансировать round-robin. Sticky routing — функция Layer 7 балансировщика (nginx `ip_hash`, AWS ALB stickiness cookie), а не Spring.
>
> **Откуда путаница:** sticky sessions исторически были workaround-ом для in-memory HttpSession в Tomcat. Когда команда впервые слышит «Spring Session решает кластерные сессии», легко предположить, что он автоматизирует sticky-маршрутизацию.
>
> **Если бы это было правдой:** при падении узла все его пользователи теряли бы сессии — именно тот сценарий, ради которого внешний store и создан. Облачные deploy с эфемерными подами были бы непригодны для stateful веб-приложений.
>
> ---
>
> #### C) Spring Session — обёртка над `HttpSessionListener`, которая логирует события create/destroy в Logback — ❌ Неверно
>
> **Что на самом деле:** Spring Session публикует события (`SessionCreatedEvent`, `SessionExpiredEvent`, `SessionDeletedEvent`) через `ApplicationEventPublisher`, но это побочная функция. Главное — реализация `SessionRepository` и `SessionRepositoryFilter`, который перехватывает запрос и подменяет нативный `HttpSession` на свой.
>
> **Откуда путаница:** один из первых туториалов Baeldung начинается с примера listener-а — у новичков создаётся впечатление, что вся библиотека про события.
>
> **Если бы это было правдой:** библиотека бы не давала шину между узлами, не было бы `FindByIndexNameSessionRepository`, не работала бы concurrency control в Spring Security кластерно.
>
> ---
>
> #### D) Spring Session выносит HTTP-сессии во внешнее хранилище (Redis/JDBC/MongoDB/Hazelcast), позволяя любому узлу кластера прозрачно работать с одной и той же сессией без sticky routing — ✓ Верно
>
> **Развёрнутое объяснение:**
>
> Spring Session состоит из трёх частей:
> 1. **`SessionRepositoryFilter`** — Servlet `Filter` с порядком выше всех, оборачивает `HttpServletRequest` так, что `request.getSession()` возвращает `Session` из репозитория, а не контейнерную.
> 2. **`SessionRepository<S>`** — абстракция чтения/записи. Реализации: `RedisIndexedSessionRepository`, `JdbcIndexedSessionRepository`, `MongoIndexedSessionRepository`, `Hazelcast*`.
> 3. **`CookieHttpSessionIdResolver` / `HeaderHttpSessionIdResolver`** — извлечение session id из cookie `SESSION` или HTTP-заголовка `X-Auth-Token`.
>
> Контейнерная HttpSession становится не нужна — `app.tomcat.session.persistent=false`, persistence уезжает в Redis.
>
> **Пример:**
> ```java
> @Configuration
> @EnableRedisIndexedHttpSession(maxInactiveIntervalInSeconds = 1800)
> public class SessionConfig {
>     @Bean
>     public LettuceConnectionFactory connectionFactory() {
>         return new LettuceConnectionFactory(
>             new RedisStandaloneConfiguration("redis.prod.internal", 6379));
>     }
> }
> // После этого session.setAttribute("cart", cart) автоматически летит в Redis HASH:
> //   spring:session:sessions:<id>  →  sessionAttr:cart = <bytes>
> ```
>
> **Когда применять:**
> - Любой horizontally scaled stateful Spring MVC сервис (e-commerce, admin-панели, личные кабинеты).
> - Kubernetes с эфемерными подами — pod может умереть, сессия выживает.
> - Blue-green / canary deployment без logout-ов пользователей.
> - LinkedIn, Booking.com и Netflix Edge gateway используют похожий подход (хоть и со своими store-ами).
>
> **Подводные камни:**
> - Каждый запрос — +1 RTT до Redis; на p99 это заметно, продумайте `flush-mode: on-save` и не пишите в сессию по мелочи.
> - Java Serialization по умолчанию — версионирование классов превращается в ад при canary deploy с разными версиями DTO в сессии.
> - При `@EnableRedisIndexedHttpSession` Redis обязан иметь `notify-keyspace-events Egx` иначе expiration-события не приходят.
>
> **Связанные вопросы:** [[spring-session-interview#Q2]] — настройка Redis store; [[spring-session-interview#Q10]] — работа в кластере; [[spring-session-interview#Q13]] — отличия от Tomcat Session Manager.

## Q2. Как настроить Redis Session Store?

```xml
<dependency>
    <groupId>org.springframework.session</groupId>
    <artifactId>spring-session-data-redis</artifactId>
</dependency>
```

```yaml
spring:
  session:
    store-type: redis
    timeout: 30m              # время жизни сессии
    redis:
      namespace: myapp:session
      flush-mode: on-save     # или immediate
  data:
    redis:
      host: localhost
      port: 6379
```

```java
// В Redis сессия хранится как hash:
// key: myapp:session:sessions:<sessionId>
// fields: lastAccessedTime, maxInactiveInterval, sessionAttr:<name>
```

При запросе Spring Session перехватывает `HttpServletRequest.getSession()` и загружает сессию из Redis вместо памяти контейнера.


> [!mcq]
>
> **Вопрос:** Какой минимально достаточный набор шагов корректно подключает Redis как Spring Session store в Spring Boot 3?
>
> ---
>
> #### A) Добавить `spring-session-data-redis`, прописать `spring.session.store-type=redis`, настроить `spring.data.redis.host/port`, и (опционально) `timeout` и `namespace` — ✓ Верно
>
> **Развёрнутое объяснение:**
>
> Spring Boot имеет auto-configuration `SessionAutoConfiguration`, которая активируется при наличии `spring-session-data-redis` в classpath. Если `store-type=redis` (или unset, но Redis единственный — будет выбран автоматически), запускается `RedisHttpSessionConfiguration` и регистрируется `SessionRepositoryFilter` с `RedisIndexedSessionRepository`.
>
> `spring.data.redis.*` (в Boot 3 — именно `data.redis`, не `redis`) даёт connection factory. `spring.session.timeout` маппится в `maxInactiveInterval`, `spring.session.redis.namespace` — префикс ключей.
>
> **Пример:**
> ```yaml
> spring:
>   session:
>     store-type: redis
>     timeout: 30m
>     redis:
>       namespace: shop:session
>       flush-mode: on-save
>       repository-type: indexed
>   data:
>     redis:
>       host: redis.prod.internal
>       port: 6379
>       password: ${REDIS_PASSWORD}
>       ssl:
>         enabled: true
> ```
>
> Никаких `@EnableRedisHttpSession` руками — он подтянется через auto-config. Достаточно собрать jar и запустить.
>
> **Когда применять:**
> - Любое современное Spring Boot 3 приложение, где нужен внешний store.
> - Stateless cluster в Kubernetes — каждый pod подключается к managed Redis (AWS ElastiCache, GCP Memorystore, Yandex Managed Redis).
> - Multi-region deploy: replicate Redis через `WAIT` команды или Redis Enterprise Active-Active.
>
> **Подводные камни:**
> - В Boot 3 префикс `spring.redis.*` стал `spring.data.redis.*` — старые туториалы вводят в заблуждение.
> - `flush-mode: immediate` пишет в Redis на каждый `setAttribute` — двойные RTT и потенциальный bottleneck.
> - `repository-type: indexed` требует keyspace notifications и индексных ZSET-ов — дороже памяти, но даёт `findByPrincipalName`.
>
> **Связанные вопросы:** [[spring-session-interview#Q1]] — что такое Spring Session; [[spring-session-interview#Q5]] — `FindByIndexNameSessionRepository`; [[spring-session-interview#Q6]] — события сессий.
>
> ---
>
> #### B) Достаточно добавить только `spring-boot-starter-data-redis` — Spring Session активируется сразу, потому что Redis transitively включён — ❌ Неверно
>
> **Что на самом деле:** `spring-boot-starter-data-redis` даёт `RedisTemplate` и `Lettuce`/`Jedis` connection factory, но не приносит `spring-session-data-redis`. Без отдельной зависимости `SessionAutoConfiguration` для Redis не активируется — будет использоваться обычная контейнерная HttpSession.
>
> **Откуда путаница:** имена стартеров похожи, оба про Redis. На IDE-autocomplete-е легко взять не тот.
>
> **Если бы это было правдой:** любой сервис с `RedisTemplate` (например, кэшем Redis для Caffeine fallback) внезапно получал бы внешние сессии — broken изоляция, неожиданные расходы памяти Redis, сломанные тесты.
>
> ---
>
> #### C) Нужно вручную добавить `@EnableRedisHttpSession` И отключить Boot auto-configuration через `@SpringBootApplication(exclude = SessionAutoConfiguration.class)`, иначе будет конфликт двух фильтров — ❌ Неверно
>
> **Что на самом деле:** auto-configuration спроектирован так, чтобы `@EnableRedisHttpSession` либо `application.yml`-конфиг работали изолированно. Двойных filter-ов не будет — `SessionRepositoryFilter` зарегистрирован один раз через `@Bean`. Отключать auto-config не нужно и вредно (потеряете metrics, actuator endpoint).
>
> **Откуда путаница:** в Spring Security исторически встречались дубли `springSecurityFilterChain` при ручной конфигурации — это формирует ложное правило «всегда отключай auto-config при ручной настройке».
>
> **Если бы это было правдой:** ни один production deploy не работал бы из коробки, и Spring Boot Quickstart-документация не показывала бы YAML-only вариант.
>
> ---
>
> #### D) Spring Session Redis требует deploy-а отдельного `spring-session-server` процесса (как Spring Cloud Config Server), который проксирует трафик в Redis — ❌ Неверно
>
> **Что на самом деле:** Spring Session — это embedded библиотека внутри приложения, без отдельного сервера. Каждый инстанс приложения напрямую общается с Redis через `LettuceConnectionFactory`. Никакого `spring-session-server` в экосистеме не существует.
>
> **Откуда путаница:** аналогия с `Spring Cloud Config Server`, `Eureka Server`, `Zuul` — там действительно есть отдельные процессы. Можно ошибочно перенести pattern на сессии.
>
> **Если бы это было правдой:** появилась бы лишняя точка отказа и +1 hop по сети для каждого запроса. Архитектурно это потеряло бы смысл — проще завести `Redis Cluster` напрямую.

## Q3. Как настроить JDBC Session Store?

```xml
<dependency>
    <groupId>org.springframework.session</groupId>
    <artifactId>spring-session-jdbc</artifactId>
</dependency>
```

```yaml
spring:
  session:
    store-type: jdbc
    jdbc:
      initialize-schema: always  # auto-create tables
      table-name: SPRING_SESSION
  datasource:
    url: jdbc:postgresql://localhost:5432/mydb
```

```sql
-- Автоматически создаваемые таблицы:
-- SPRING_SESSION (PRIMARY_ID, SESSION_ID, CREATION_TIME, LAST_ACCESS_TIME, MAX_INACTIVE_INTERVAL, EXPIRY_TIME, PRINCIPAL_NAME)
-- SPRING_SESSION_ATTRIBUTES (SESSION_PRIMARY_ID, ATTRIBUTE_NAME, ATTRIBUTE_BYTES)
```

JDBC Session требует периодической очистки истёкших сессий. Spring Boot автоматически создаёт задачу очистки (`spring.session.jdbc.cleanup-cron`).


> [!mcq]
>
> **Вопрос:** Какая особенность JDBC session store критична для production по сравнению с Redis store?
>
> ---
>
> #### A) JDBC store хранит атрибуты сессии как plain JSON в колонке `ATTRIBUTE_JSON` — можно делать SQL-запросы по содержимому сессии — ❌ Неверно
>
> **Что на самом деле:** атрибуты хранятся в `SPRING_SESSION_ATTRIBUTES.ATTRIBUTE_BYTES` как `BYTEA`/`BLOB`, сериализованные через Java Serialization (`SerializingConverter`). SQL-поиска по содержимому атрибутов нет, индексируется только `PRINCIPAL_NAME` отдельной колонкой.
>
> **Откуда путаница:** PostgreSQL имеет `JSONB`, и многие современные стартеры пишут JSON по умолчанию. Возникает ожидание, что Spring Session делает так же.
>
> **Если бы это было правдой:** появилась бы возможность `SELECT * WHERE attributes->>'role' = 'admin'` — но это нарушало бы изоляцию атрибутов и провоцировало бы команды лезть в сессии SQL-ом мимо приложения.
>
> ---
>
> #### B) JDBC store требует периодической очистки истёкших сессий через scheduled job, так как expiration не происходит автоматически как в Redis с TTL — ✓ Верно
>
> **Развёрнутое объяснение:**
>
> В Redis у каждого ключа есть TTL, и истёкшие записи удаляются сервером автоматически (active + passive expiration). В реляционной БД такого механизма нет — `EXPIRY_TIME` это просто колонка `BIGINT`. Spring Session запускает `@Scheduled` job (по умолчанию каждую минуту, cron `0 * * * * *`), который выполняет `DELETE FROM SPRING_SESSION WHERE EXPIRY_TIME < ?`.
>
> Если job-а нет (не подключили `@EnableScheduling` или фильтр SchedulingConfigurer), таблица будет неограниченно расти.
>
> **Пример:**
> ```java
> @Configuration
> @EnableScheduling
> @EnableJdbcHttpSession(
>     cleanupCron = "0 */5 * * * *",   // каждые 5 минут
>     tableName = "SPRING_SESSION",
>     maxInactiveIntervalInSeconds = 1800
> )
> public class JdbcSessionConfig {
>     // Flyway скрипт V1__spring_session.sql — обязателен в проде
>     // (initialize-schema=always небезопасен в kubernetes — race на старте)
> }
> ```
>
> SQL запроса cleanup-а в PostgreSQL:
> ```sql
> DELETE FROM SPRING_SESSION WHERE EXPIRY_TIME < EXTRACT(EPOCH FROM NOW()) * 1000;
> ```
>
> **Когда применять:**
> - Окружения, где уже есть PostgreSQL/MySQL и не хочется ставить Redis ради сессий.
> - Сценарии, где нужно ACID-гарантии для сессии вместе с бизнес-данными в одной транзакции (редко, но бывает).
> - Корпоративные системы с жёсткими требованиями к persistence (Redis считается «in-memory только» по compliance-причинам).
>
> **Подводные камни:**
> - Cleanup-job блокирует ряды — на высоконагруженных таблицах `DELETE` пачками + `VACUUM` обязательны, иначе bloat растёт.
> - `initialize-schema: always` в Kubernetes — race condition при одновременном старте подов, лучше Flyway/Liquibase.
> - Каждый запрос — два SQL: `SELECT FROM SPRING_SESSION` + `SELECT FROM SPRING_SESSION_ATTRIBUTES`, под нагрузкой создаёт connection pool pressure.
>
> **Связанные вопросы:** [[spring-session-interview#Q2]] — Redis store как альтернатива; [[spring-session-interview#Q10]] — кластеризация; [[spring-session-interview#Q14]] — мониторинг.
>
> ---
>
> #### C) JDBC store работает только с PostgreSQL — для MySQL/Oracle/SQL Server нужно писать свой `SessionRepository` — ❌ Неверно
>
> **Что на самом деле:** Spring Session JDBC поддерживает PostgreSQL, MySQL, MS SQL Server, Oracle, DB2, HSQLDB через DDL-скрипты в classpath (`org/springframework/session/jdbc/schema-<db>.sql`). Диалект определяется автоматически из `DataSource` metadata.
>
> **Откуда путаница:** в туториалах часто показывают только PostgreSQL — у читателя складывается впечатление эксклюзивности.
>
> **Если бы это было правдой:** библиотека была бы вендор-локнутой, и крупные enterprise-системы (где обычно Oracle/DB2) не могли бы её использовать.
>
> ---
>
> #### D) JDBC store автоматически реплицирует сессии между датацентрами через Postgres logical replication — ❌ Неверно
>
> **Что на самом деле:** Spring Session ничего не знает про репликацию — это ответственность БД (streaming replication, BDR, Citus). Spring просто пишет в одну БД, а cross-region durability обеспечивает инфраструктура.
>
> **Откуда путаница:** Postgres `pg_logical` действительно умеет replication, но это конфигурация на уровне БД, не библиотеки.
>
> **Если бы это было правдой:** Spring приходилось бы знать про топологию репликации, и при misconfiguration сессии бы дублировались/терялись на split-brain без видимых сигналов.

## Q4. Как Spring Session интегрируется с Spring Security?

```java
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .sessionManagement(session -> session
                .sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED)
                .maximumSessions(1)              // максимум 1 активная сессия на пользователя
                .maxSessionsPreventsLogin(false) // old сессия истекает при новом логине
                .expiredUrl("/session-expired")
            );
        return http.build();
    }
}
```

```java
// Получение информации о текущей сессии
@GetMapping("/me")
public SessionInfo getCurrentSession(HttpSession session) {
    return new SessionInfo(
        session.getId(),
        (String) session.getAttribute("username"),
        session.getCreationTime(),
        session.getLastAccessedTime()
    );
}
```

Spring Session + Spring Security: при аутентификации `SecurityContext` сохраняется как атрибут сессии в Redis/JDBC. `FindByIndexNameSessionRepository` позволяет найти все сессии пользователя.


> [!mcq]
>
> **Вопрос:** Как именно Spring Security хранит `SecurityContext` при включённом Spring Session с Redis-store?
>
> ---
>
> #### A) `SecurityContext` сериализуется как атрибут `SPRING_SECURITY_CONTEXT` внутри объекта `Session`, и через `SessionRepositoryFilter` улетает в Redis вместе с остальными атрибутами сессии — ✓ Верно
>
> **Развёрнутое объяснение:**
>
> Цепочка такая:
> 1. `HttpSessionSecurityContextRepository` (часть Spring Security) после успешной аутентификации вызывает `session.setAttribute("SPRING_SECURITY_CONTEXT", ctx)`.
> 2. Но `session` это не родная Tomcat HttpSession — `SessionRepositoryFilter` уже обернул запрос так, что `session` это `HttpSessionWrapper` поверх `RedisIndexedSession`.
> 3. На commit фазе (после обработки запроса) `SaveContextOnUpdateOrErrorResponseWrapper` форсит `session.save()`, который flushит `RedisSessionRepository.save(session)` — `HMSET spring:session:sessions:<id> sessionAttr:SPRING_SECURITY_CONTEXT <bytes>`.
> 4. На следующем запросе `SecurityContextPersistenceFilter` достаёт `SPRING_SECURITY_CONTEXT` из той же абстрактной HttpSession, фактически из Redis.
>
> **Пример:**
> ```java
> // Что лежит в Redis после login:
> // HGETALL spring:session:sessions:abc-123
> //   creationTime              → 1715800000000
> //   maxInactiveInterval       → 1800
> //   lastAccessedTime          → 1715800500000
> //   sessionAttr:SPRING_SECURITY_CONTEXT → <Java serialized SecurityContextImpl>
> //   sessionAttr:cart          → <Java serialized Cart>
> //
> // Индексы (при @EnableRedisIndexedHttpSession):
> //   spring:session:sessions:expires:abc-123     (TTL key)
> //   spring:session:sessions:index:principalName:alice (Set sessionId)
> ```
>
> **Когда применять:**
> - Любой Spring Security-проект, где требуется session-based auth (классический form-login, OAuth2 login с session cookie).
> - Multi-instance auth-сервер, где hot failover критичен — пользователь не выходит при падении node.
>
> **Подводные камни:**
> - `SecurityContextImpl` сериализуется Java Serialization → версионирование `Authentication`-имплементаций ломает rolling deploy.
> - В Spring Security 6 включён `SecurityContextHolderFilter` (delayed save) — атрибут пишется только при изменении, можно неожиданно потерять обновления Authentication, если код модифицировал контекст вручную без `SecurityContextHolder.getContext()`.
> - При `@EnableRedisIndexedHttpSession` имя principal-а попадает в индексный SET в plaintext — для compliance это PII, иногда требует hashing.
>
> **Связанные вопросы:** [[spring-session-interview#Q1]] — основы; [[spring-session-interview#Q5]] — `FindByIndexNameSessionRepository`; [[spring-session-interview#Q12]] — concurrency control.
>
> ---
>
> #### B) Spring Security при включённом Spring Session переходит на JWT-режим и не использует сессию вообще — токен хранится в Authorization header — ❌ Неверно
>
> **Что на самом деле:** Spring Session и JWT — ортогональные подходы. Подключение Spring Session не отключает session-based auth и не включает JWT. Если хотите JWT — это отдельная настройка через `SessionCreationPolicy.STATELESS` + OAuth2 Resource Server.
>
> **Откуда путаница:** в современных tutorials часто связывают «масштабируемая auth» = «JWT», и при упоминании Spring Session ожидают такого же перехода.
>
> **Если бы это было правдой:** не было бы смысла в `FindByIndexNameSessionRepository`, не работала бы concurrent-session control, и переход с monolith на кластер требовал бы переписывания всего auth-слоя.
>
> ---
>
> #### C) `SecurityContext` хранится отдельно в Redis под собственным префиксом `spring:security:context:<userId>`, не пересекаясь с session attributes — ❌ Неверно
>
> **Что на самом деле:** `SecurityContext` — обычный session attribute с ключом `SPRING_SECURITY_CONTEXT`, лежит внутри той же HASH-структуры в Redis (`spring:session:sessions:<sessionId>`). Отдельного хранилища нет.
>
> **Откуда путаница:** в OAuth2 Authorization Server бывают отдельные key-spaces для tokens (`oauth2:access_token`, `oauth2:refresh_token`). Можно по аналогии решить, что и SecurityContext отдельно.
>
> **Если бы это было правдой:** invalidation сессии не выкидывал бы пользователя — `SecurityContext` оставался бы валидным. Это сразу заметная security-дыра.
>
> ---
>
> #### D) Spring Session подменяет `SecurityContextHolder` стратегией `REDIS_BACKED`, и обращения к `SecurityContextHolder.getContext()` идут напрямую в Redis — ❌ Неверно
>
> **Что на самом деле:** `SecurityContextHolder` всегда использует `ThreadLocal`-based стратегию (или `InheritableThreadLocal`). Spring Session работает на уровне HttpSession и не трогает HolderStrategy. Каждый запрос — `SecurityContextPersistenceFilter` читает SecurityContext из session один раз, кладёт в ThreadLocal, и весь dispatch ходит уже к ThreadLocal.
>
> **Откуда путаница:** легко сделать ментальную модель «всё в Redis», но это сломало бы производительность — каждый `@PreAuthorize` лез бы в сеть.
>
> **Если бы это было правдой:** latency `@PreAuthorize` выросла бы с микросекунд до миллисекунд, и приложение под нагрузкой бы захлёбывалось network IO к Redis.

## Q5. Что такое FindByIndexNameSessionRepository и зачем он нужен?

```java
// Получение всех сессий конкретного пользователя
@Service
@RequiredArgsConstructor
public class SessionManagementService {
    private final FindByIndexNameSessionRepository<? extends Session> sessionRepository;

    public Map<String, ? extends Session> getUserSessions(String username) {
        return sessionRepository.findByPrincipalName(username);
    }

    public void invalidateAllUserSessions(String username) {
        getUserSessions(username)
            .forEach((id, session) -> sessionRepository.deleteById(id));
    }
}
```

```java
// @EnableRedisIndexedHttpSession — индексирование по имени пользователя
// @EnableRedisHttpSession — базовая поддержка без индексации
@EnableRedisIndexedHttpSession
@Configuration
public class SessionConfig { }
```

`FindByIndexNameSessionRepository` требует `@EnableRedisIndexedHttpSession` — только Redis поддерживает эту функцию natively.


> [!mcq]
>
> **Вопрос:** Зачем `FindByIndexNameSessionRepository` и какие операции он включает в инфраструктуре?
>
> ---
>
> #### A) Он индексирует session id по содержимому атрибутов через Redis SEARCH module, позволяя `SELECT WHERE cart.total > 1000` — ❌ Неверно
>
> **Что на самом деле:** `FindByIndexNameSessionRepository` не делает full-text/attribute поиск. Он индексирует только `PRINCIPAL_NAME` (имя пользователя) — единственный встроенный индекс. RediSearch для атрибутов потребовал бы отдельной интеграции.
>
> **Откуда путаница:** имя `FindByIndexName` звучит generic, как будто индекс можно задать любой. Реально index name захардкожен в `PRINCIPAL_NAME_INDEX_NAME`.
>
> **Если бы это было правдой:** Spring Session нёс бы зависимость от RediSearch (платный модуль в коммерческом Redis Enterprise), и не работал бы на чистом open-source Redis.
>
> ---
>
> #### B) Это admin-репозиторий, доступный только из Spring Boot Actuator endpoint `/actuator/sessions` — нельзя инжектить в свои бины — ❌ Неверно
>
> **Что на самом деле:** это обычный Spring bean, который Spring Session регистрирует автоматически. Его можно `@Autowired` в любой `@Service`/`@Component`. Actuator `/sessions` его использует под капотом, но не монопольно.
>
> **Откуда путаница:** многие первые встречаются с ним именно через actuator endpoint и считают, что это «admin-only».
>
> **Если бы это было правдой:** программный force-logout пользователя был бы невозможен, и admin-фичи в самих приложениях (kick session, view active sessions) требовали бы парсинга actuator JSON.
>
> ---
>
> #### C) Это репозиторий, дополнительно индексирующий сессии по principal name, что позволяет находить все сессии пользователя (`findByPrincipalName`) и реализовывать force-logout, concurrent-session-control, audit активных сессий — ✓ Верно
>
> **Развёрнутое объяснение:**
>
> Базовый `SessionRepository` умеет только `findById(sessionId)`. Чтобы найти «все сессии Alice», нужен обратный индекс `principalName → Set<sessionId>`. `FindByIndexNameSessionRepository` добавляет именно его. В Redis это реализовано через Set `spring:session:sessions:index:principalName:<name>` куда `SADD` на каждый login и `SREM` на logout/expiration.
>
> Активируется через `@EnableRedisIndexedHttpSession` (НЕ обычный `@EnableRedisHttpSession`). Для JDBC store индекс встроен в колонку `SPRING_SESSION.PRINCIPAL_NAME` с B-tree index.
>
> **Пример:**
> ```java
> @Service
> @RequiredArgsConstructor
> public class SessionAdminService {
>     private final FindByIndexNameSessionRepository<? extends Session> repo;
>
>     // Force logout: например, после смены пароля
>     public int revokeAllSessions(String username) {
>         Map<String, ? extends Session> sessions =
>             repo.findByPrincipalName(username);
>         sessions.keySet().forEach(repo::deleteById);
>         return sessions.size();
>     }
>
>     // View active devices в личном кабинете
>     public List<DeviceInfo> activeDevices(String username) {
>         return repo.findByPrincipalName(username).values().stream()
>             .map(s -> new DeviceInfo(
>                 s.getId(),
>                 (String) s.getAttribute("userAgent"),
>                 s.getLastAccessedTime()))
>             .toList();
>     }
> }
> ```
>
> **Когда применять:**
> - Личные кабинеты с «активные устройства» (GitHub, Google account, банковские).
> - Compliance-требования: force-logout всех сессий после security incident или смены пароля.
> - Spring Security concurrency control в кластере (`maximumSessions(1)` без него не работает между нодами).
>
> **Подводные камни:**
> - Индексный SET в Redis съедает дополнительную память — на 1M активных сессий ≈ 50-100 MB.
> - При `@EnableRedisHttpSession` (без `Indexed`) — `findByPrincipalName` бросает `UnsupportedOperationException`, ошибка только в рантайме.
> - Indexed repository требует keyspace notifications в Redis — иначе stale entries в принципал-индексе после TTL expiration.
>
> **Связанные вопросы:** [[spring-session-interview#Q4]] — интеграция с Security; [[spring-session-interview#Q12]] — concurrency control; [[spring-session-interview#Q14]] — actuator endpoint.
>
> ---
>
> #### D) Этот репозиторий заменяет `SessionRepository` полностью — после его подключения базовый `findById` больше не работает — ❌ Неверно
>
> **Что на самом деле:** `FindByIndexNameSessionRepository` расширяет `SessionRepository`. Все базовые операции (`findById`, `save`, `deleteById`) сохраняются. Это аддитивный интерфейс.
>
> **Откуда путаница:** в некоторых библиотеках decorator подменяет original bean, и пользователи привыкают к pattern замены.
>
> **Если бы это было правдой:** `SessionRepositoryFilter`, который зависит от `findById`, ломался бы — каждый запрос падал бы 500-кой при попытке загрузить сессию.

## Q6. Как обрабатывать события сессий?

```java
@Component
public class SessionEventListener {

    @EventListener
    public void onSessionCreated(SessionCreatedEvent event) {
        String sessionId = event.getSessionId();
        log.info("Session created: {}", sessionId);
    }

    @EventListener
    public void onSessionDeleted(SessionDeletedEvent event) {
        log.info("Session deleted: {}", event.getSessionId());
        // очистка ресурсов, связанных с сессией
    }

    @EventListener
    public void onSessionExpired(SessionExpiredEvent event) {
        log.info("Session expired: {}", event.getSessionId());
        // можно уведомить пользователя
    }
}
```

Redis Session использует keyspace notifications для получения событий об истечении/удалении сессий. Необходимо включить: `notify-keyspace-events Eg` в Redis.


> [!mcq]
>
> **Вопрос:** Какое требование Redis-конфигурации критично для корректной работы `SessionExpiredEvent` в Spring Session?
>
> ---
>
> #### A) Redis должен быть запущен в `cluster mode`, иначе keyspace notifications для expiration не работают — ❌ Неверно
>
> **Что на самом деле:** keyspace notifications работают одинаково в standalone, replica, sentinel и cluster режимах. Cluster mode добавляет распределение ключей по слотам, но не влияет на сам механизм нотификаций. Spring Session работает в любом режиме при правильной настройке `notify-keyspace-events`.
>
> **Откуда путаница:** многие enterprise-задачи начинаются сразу с cluster mode, и появляется ощущение, что «все features требуют cluster».
>
> **Если бы это было правдой:** dev-окружения на одном Redis-инстансе ловили бы тихий баг — expiration-события не приходят, но `SessionDeletedEvent` приходит (так как `del` явный). В CI/CD пропускались бы regression-тесты.
>
> ---
>
> #### B) В `redis.conf` обязательно `notify-keyspace-events Egx` (или `Egxe`), иначе Spring Session не получит уведомлений об истечении TTL и `SessionExpiredEvent` не будет публиковаться — ✓ Верно
>
> **Развёрнутое объяснение:**
>
> Redis по умолчанию `notify-keyspace-events ""` (выключено) для экономии CPU. Чтобы получить событие, нужно включить флаги:
> - `E` — Keyevent events (`__keyevent@0__:expired`)
> - `g` — Generic commands (DEL, EXPIRE) → даёт `del`
> - `x` — Expired events → даёт `expired` (это и есть наш SessionExpired)
> - `e` — Evicted events (если хочется ловить eviction при `maxmemory`)
>
> `Spring Session` подписывается на `__keyevent@*__:expired` через `RedisMessageListenerContainer`. Без `Ex` — подписка есть, но события не приходят, и сессия просто исчезает из Redis молча.
>
> **Пример:**
> ```bash
> # Включить динамически (без рестарта):
> redis-cli CONFIG SET notify-keyspace-events Egx
>
> # Проверить:
> redis-cli CONFIG GET notify-keyspace-events
> # 1) "notify-keyspace-events"
> # 2) "gxE"   ← порядок может отличаться, флаги те же
>
> # Постоянно — в redis.conf:
> # notify-keyspace-events "Egx"
> ```
> ```java
> @Component
> @Slf4j
> public class SessionLifecycleListener {
>     @EventListener
>     public void onExpire(SessionExpiredEvent e) {
>         // отправить websocket-уведомление в браузер,
>         // освободить bound resources, очистить локальный кэш
>         log.info("Expired: principal={}, sessionId={}",
>             e.getSessionId(), getPrincipal(e));
>     }
> }
> ```
>
> **Когда применять:**
> - Auto-logout с websocket-уведомлением «вас вышли по неактивности».
> - Освобождение распределённых блокировок, привязанных к сессии (booking-холдов, нумераторов).
> - Audit log — когда пользователь действительно «ушёл», а не залогинился.
>
> **Подводные камни:**
> - Managed Redis (AWS ElastiCache, GCP Memorystore) — `notify-keyspace-events` управляется через parameter group, не через `CONFIG SET`.
> - Redis Cluster — события генерируются на shard-owner, нужно подписываться на все ноды (Spring Session делает это автоматически через Lettuce).
> - `expired` событие приходит с задержкой до 100ms — для realtime-критичных сценариев лучше явный logout-flow.
>
> **Связанные вопросы:** [[spring-session-interview#Q2]] — Redis store; [[spring-session-interview#Q7]] — настройка timeout; [[spring-session-interview#Q11]] — миграция.
>
> ---
>
> #### C) Spring Session требует Redis ≥ 7 c Pub/Sub Sharded — раньше events не работали — ❌ Неверно
>
> **Что на самом деле:** keyspace notifications появились в Redis 2.8 (2013 год), и Spring Session работает с Redis 2.8+. Sharded Pub/Sub — фича Redis 7 для Cluster mode оптимизации, но базовые notifications работают везде.
>
> **Откуда путаница:** Redis 7 принёс много улучшений Pub/Sub, и можно ошибочно решить, что events требуют именно его.
>
> **Если бы это было правдой:** Spring Session не работал бы на legacy инсталляциях (Redis 5/6 — большинство production-ов до 2023).
>
> ---
>
> #### D) `SessionExpiredEvent` публикуется только при ручном вызове `sessionRepository.expire(id)` — он не привязан к TTL — ❌ Неверно
>
> **Что на самом деле:** в Spring Session нет метода `expire`. Event генерируется именно по TTL — Redis сам удаляет ключ при истечении `EXPIRE` и публикует `expired`-событие, которое Spring ловит и транслирует в `SessionExpiredEvent`. Программно можно вызвать `deleteById`, но это породит `SessionDeletedEvent`, а не Expired.
>
> **Откуда путаница:** в Hibernate Cache, JCache есть явные `expire(key)` методы — кажется, что Spring Session устроен так же.
>
> **Если бы это было правдой:** auto-logout по таймауту был бы невозможен — пришлось бы держать отдельный scheduler, перебирающий все сессии и проверяющий expiration вручную.

## Q7. Как настроить timeout сессии?

```yaml
# Глобальный timeout для всех сессий
spring:
  session:
    timeout: 30m   # 30 минут (ISO 8601 duration)
```

```java
// Программное управление timeout
@PostMapping("/keep-alive")
public void keepAlive(HttpSession session) {
    session.setMaxInactiveInterval(1800);  // 30 минут в секундах
}

// Разный timeout для разных ролей
@EventListener
public void onSessionCreated(SessionCreatedEvent event) {
    Session session = sessionRepository.findById(event.getSessionId());
    Authentication auth = (Authentication) session.getAttribute(
        HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY);

    if (auth != null && auth.getAuthorities().stream()
            .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"))) {
        session.setMaxInactiveInterval(Duration.ofHours(8));
        sessionRepository.save(session);
    }
}
```


> [!mcq]
>
> **Вопрос:** Где реально вычисляется session expiration в Spring Session при Redis store, и почему это важно для архитектуры?
>
> ---
>
> #### A) Expiration проверяет браузер на клиенте через JS-таймер, отсчитывающий `maxInactiveInterval` от document load — ❌ Неверно
>
> **Что на самом деле:** session expiration — строго серверный механизм. Браузер хранит только session cookie без TTL-логики. Логика «истекла или нет» вычисляется на стороне приложения и/или Redis (через `EXPIRE`). Никакого js-таймера для session timeout Spring не предоставляет.
>
> **Откуда путаница:** многие UI-фреймворки имеют клиентский idle-таймер для UX (показ модала «вы скоро выйдете»). Это совсем другое — UX-нотификация, не security-механизм.
>
> **Если бы это было правдой:** подделав js-таймер, любой пользователь мог бы продлить сессию бесконечно — это полностью ломает модель угроз. Аналог Knight Capital по тяжести — компрометация всей auth-системы.
>
> ---
>
> #### B) Истечение определяется на стороне сервера: Redis удаляет ключ по TTL (passive + active expiration), а Spring Session при `getSession` проверяет `lastAccessedTime + maxInactiveInterval`. Каждый запрос обновляет TTL и lastAccessedTime — ✓ Верно
>
> **Развёрнутое объяснение:**
>
> На сервере две сцепленные проверки:
> 1. **`RedisIndexedSessionRepository.findById`** — читает HASH, проверяет `lastAccessedTime + maxInactiveInterval < now` и если true — удаляет и возвращает null.
> 2. **Redis сам** — на каждый `save()` Spring устанавливает `EXPIRE spring:session:sessions:<id> <interval>`. Если сервер приложения упал, Redis всё равно вычистит запись в нужный момент.
>
> На каждом запросе `SessionRepositoryFilter` зовёт `session.setLastAccessedTime(now)` и в конце запроса делает `save()`, что переустанавливает TTL — это «sliding expiration».
>
> **Пример:**
> ```yaml
> spring:
>   session:
>     timeout: 30m   # маппится в session.maxInactiveInterval = 1800s
>     redis:
>       flush-mode: on-save  # сохранение в конце запроса (1 RTT)
>       # immediate — сохранение на каждый setAttribute (N RTT)
> ```
> ```java
> // Программно — разный timeout для admin / regular
> @EventListener
> public void onCreate(SessionCreatedEvent e) {
>     Session s = repo.findById(e.getSessionId());
>     if (isAdmin(s)) s.setMaxInactiveInterval(Duration.ofHours(8));
>     else s.setMaxInactiveInterval(Duration.ofMinutes(15));
>     repo.save(s);
> }
> // Sliding expiration: каждый запрос продлевает TTL на полные 30 минут
> ```
>
> **Когда применять:**
> - Разный timeout по ролям (admin длиннее, regular короче) — банки, госуслуги.
> - Sliding expiration для активных пользователей и fixed для service-аккаунтов.
> - Принудительный logout после security incident через сокращение `maxInactiveInterval` всех сессий.
>
> **Подводные камни:**
> - `flush-mode: on-save` означает: если приложение крашнется до конца запроса, изменения атрибутов потеряны (часто это OK).
> - `lastAccessedTime` обновляется только при попадании в `SessionRepositoryFilter` — websocket-соединения (после handshake) не продлевают сессию автоматически.
> - При установке очень короткого timeout (<30 секунд) keyspace notifications могут отставать на пару секунд — UX «логаут раньше времени».
>
> **Связанные вопросы:** [[spring-session-interview#Q2]] — Redis store; [[spring-session-interview#Q6]] — события; [[spring-session-interview#Q12]] — concurrent sessions.
>
> ---
>
> #### C) Истечение полагается только на cron-job `SessionCleanupTask`, запускающийся каждую минуту и удаляющий старые ключи в Redis через SCAN+TTL — ❌ Неверно
>
> **Что на самом деле:** для Redis store cleanup-job не нужен — TTL делает Redis нативно. SessionCleanupTask существует только в JDBC store, где expiration искусственный (колонка `EXPIRY_TIME`). Спутывание двух store-ов — частая ошибка.
>
> **Откуда путаница:** документация JDBC store упоминает cleanup-cron, и кажется, что это общий механизм.
>
> **Если бы это было правдой:** между job-runs (1 минута) истёкшие сессии оставались бы валидными — security hole в 60 секунд для force-logout сценариев.
>
> ---
>
> #### D) `maxInactiveInterval` — это absolute TTL: сессия истекает ровно через N секунд после создания, активность пользователя не учитывается — ❌ Неверно
>
> **Что на самом деле:** `maxInactiveInterval` — это INACTIVE interval (sliding expiration). Каждый запрос сбрасывает таймер. Absolute expiration в Spring Session нет из коробки — это нужно реализовать вручную (например, через `creationTime` + check в фильтре).
>
> **Откуда путаница:** в JWT часто бывает `exp` claim = absolute timestamp. Перенос модели на session-based auth даёт ложное ожидание.
>
> **Если бы это было правдой:** активного пользователя выкидывало бы посреди заполнения формы — типичная жалоба в багтрекере «сессия истекает во время работы», вызвавшая бы переход обратно на in-memory session.

## Q8. Как реализовать кастомную сериализацию сессии?

По умолчанию Spring Session использует Java serialization для Redis. В production лучше использовать JSON:

```java
@Bean
public RedisSerializer<Object> springSessionDefaultRedisSerializer() {
    return new GenericJackson2JsonRedisSerializer();
}
```

```java
// Или через конфигурацию
@Bean
public RedisSessionRepository redisSessionRepository(
        RedisOperations<String, Object> sessionRedisOperations) {
    RedisSessionRepository repository =
        new RedisSessionRepository(sessionRedisOperations);
    repository.setDefaultMaxInactiveInterval(Duration.ofMinutes(30));
    return repository;
}
```

**Важно**: при смене сериализации существующие сессии в Redis стали нечитаемы — нужна стратегия миграции (rolling deploy + версионирование ключей).


> [!mcq]
>
> **Вопрос:** Какая стратегия миграции с Java Serialization на JSON-сериализацию сессии безопасна для production без потери активных сессий?
>
> ---
>
> #### A) Просто заменить `springSessionDefaultRedisSerializer` на `GenericJackson2JsonRedisSerializer` и сделать rolling deploy — все активные сессии прозрачно конвертируются при первом обращении — ❌ Неверно
>
> **Что на самом деле:** конвертации «на лету» не происходит. Старый сериализатор писал JDK serialized bytes; новый Jackson попытается распарсить эти bytes как JSON и упадёт с `JsonParseException` или `SerializationException`. Все активные сессии становятся unreadable.
>
> **Откуда путаница:** в Spring Data Redis есть `OXM` адаптеры, и кажется, что библиотека умеет «понимать» формат на лету.
>
> **Если бы это было правдой:** прозрачная миграция была бы тривиальной — но в production rolling deploy с такой конфигурацией приводит к массовому force-logout всех пользователей одновременно (incident-grade event для e-commerce).
>
> ---
>
> #### B) Применить параллельное хранение: использовать namespace versioning — новые ноды пишут в `app:session:v2:*`, старые в `app:session:v1:*`; через 30 минут (TTL) старые истекают сами, миграция завершена — ✓ Верно
>
> **Развёрнутое объяснение:**
>
> Безопасный паттерн «expire-and-replace»:
> 1. Деплоим новую версию с новым serializer и `spring.session.redis.namespace=app:session:v2`.
> 2. Старые поды продолжают читать/писать в `app:session:v1`.
> 3. По мере rolling restart все новые сессии создаются в v2 namespace.
> 4. Через `maxInactiveInterval` (30 минут) старые сессии в v1 истекут естественно.
> 5. После 24 часов делаем cleanup: `redis-cli --scan --pattern "app:session:v1:*" | xargs redis-cli DEL`.
>
> Пользователи на старых сессиях получат либо silent re-login через OAuth refresh token, либо обычный login — за 30 минут это размазано и не выглядит как incident.
>
> **Пример:**
> ```yaml
> # старая версия — оставлена в коде, namespace v1
> # spring.session.redis.namespace=app:session:v1
>
> # новая версия — JSON serializer + v2
> spring:
>   session:
>     redis:
>       namespace: app:session:v2   # ← смена namespace
> ```
> ```java
> @Configuration
> public class SessionSerializerConfig {
>     @Bean("springSessionDefaultRedisSerializer")
>     public RedisSerializer<Object> serializer() {
>         ObjectMapper m = new ObjectMapper();
>         m.activateDefaultTyping(
>             BasicPolymorphicTypeValidator.builder()
>                 .allowIfBaseType(Object.class).build(),
>             ObjectMapper.DefaultTyping.NON_FINAL);
>         return new GenericJackson2JsonRedisSerializer(m);
>     }
> }
> ```
>
> **Когда применять:**
> - Любая смена сериализатора сессии в production.
> - Смена major-версии Spring Security (когда `SecurityContextImpl` несовместим).
> - Cross-region миграция сессий между Redis-кластерами.
>
> **Подводные камни:**
> - JSON requires `@JsonTypeInfo` или `activateDefaultTyping` для полиморфных Authentication-имплементаций — иначе при десериализации теряется конкретный тип.
> - JSON хранит больше байт, чем JDK serialization — Redis memory вырастает на 30-50% для тех же данных.
> - Если в сессии лежат transient-поля или non-serializable объекты, миграция вскроет их сразу — лучше прогнать через тесты заранее.
>
> **Связанные вопросы:** [[spring-session-interview#Q2]] — Redis store; [[spring-session-interview#Q11]] — миграция; [[spring-session-interview#Q4]] — SecurityContext.
>
> ---
>
> #### C) Установить `spring.session.serializer.fallback=java`: если JSON не парсится, использовать Java Serialization автоматически — ❌ Неверно
>
> **Что на самом деле:** такой настройки в Spring Session нет. Сериализатор один — fallback-логику пришлось бы писать самостоятельно через `RedisSerializer`-обёртку. Это нетривиально из-за `byte[]`-неоднозначности (JSON и JDK имеют разные magic-байты).
>
> **Откуда путаница:** в логах появляются `magic number not 0xACED` ошибки JDK serialization, и хочется поверить в auto-fallback.
>
> **Если бы это было правдой:** конфиг бы работал, но тогда нет mid-term incentive переходить на JSON — все продолжали бы держать legacy serialized data.
>
> ---
>
> #### D) Использовать команду `redis-cli MIGRATE` для конвертации всех ключей `spring:session:sessions:*` из JDK в JSON формат — ❌ Неверно
>
> **Что на самом деле:** `MIGRATE` — Redis команда для переноса ключей между серверами в исходном бинарном формате. Она не конвертирует формат данных. Конвертация JDK→JSON требует чтения, десериализации, повторной сериализации — это работа приложения, не Redis-команда.
>
> **Откуда путаница:** имя `MIGRATE` создаёт ложное ассоциативное «миграция данных = миграция формата».
>
> **Если бы это было правдой:** Redis должен был бы знать про JDK и Jackson форматы — но Redis вообще про blob-storage, без знания о сериализации клиентов.

## Q9. Как использовать Spring Session с WebFlux (реактивный стек)?

```xml
<dependency>
    <groupId>org.springframework.session</groupId>
    <artifactId>spring-session-data-redis</artifactId>
</dependency>
```

```java
// Реактивная Session конфигурация
@EnableRedisWebSession
@Configuration
public class ReactiveSessionConfig { }
```

```java
@RestController
@RequiredArgsConstructor
public class ReactiveController {

    @GetMapping("/session")
    public Mono<String> getSessionInfo(WebSession session) {
        return Mono.fromCallable(() ->
            "Session ID: " + session.getId() +
            ", Max Idle: " + session.getMaxIdleTime()
        );
    }
}
```

`WebSession` в WebFlux — реактивный аналог `HttpSession`. Spring Session автоматически интегрируется при наличии зависимости.


> [!mcq]
>
> **Вопрос:** В чём ключевая разница между Spring Session для Servlet и для WebFlux?
>
> ---
>
> #### A) В WebFlux Spring Session нельзя использовать вообще — реактивный стек требует stateless JWT auth — ❌ Неверно
>
> **Что на самом деле:** Spring Session отлично работает с WebFlux через `spring-session-data-redis` + `@EnableRedisWebSession`. Реализация `ReactiveSessionRepository<S>` отдаёт `Mono<Session>`, а `WebSession` приходит через `ServerWebExchange.getSession()`. JWT — отдельный паттерн, не обязательный.
>
> **Откуда путаница:** часто WebFlux связывают с микросервисами, а микросервисы — с JWT. Складывается ложная импликация.
>
> **Если бы это было правдой:** OAuth2 login flow (form-based с сессией) в WebFlux был бы невозможен — но это полностью поддерживаемый сценарий.
>
> ---
>
> #### B) В WebFlux используется `WebSession` (из `ServerWebExchange`) вместо `HttpSession`, репозиторий — `ReactiveSessionRepository<S>` возвращающий `Mono<S>`, активируется через `@EnableRedisWebSession` (не `@EnableRedisHttpSession`) — ✓ Верно
>
> **Развёрнутое объяснение:**
>
> В Servlet-мире фундамент — `HttpServletRequest.getSession()` (блокирующий). В WebFlux — `ServerWebExchange.getSession()` возвращает `Mono<WebSession>` (неблокирующий, реактивный). Spring Session адаптирует свою модель к обоим:
>
> - **Servlet:** `SessionRepository<S>` (sync) + `SessionRepositoryFilter` (Servlet Filter) + `@EnableRedisIndexedHttpSession`.
> - **WebFlux:** `ReactiveSessionRepository<S>` (returns `Mono`) + `WebSessionManager` + `@EnableRedisWebSession`.
>
> Перепутать аннотации легко — оба стартера лежат в одном артефакте `spring-session-data-redis`, но Servlet-вариант не работает с WebFlux и наоборот.
>
> **Пример:**
> ```java
> @Configuration
> @EnableRedisWebSession(maxInactiveIntervalInSeconds = 1800)
> public class ReactiveSessionConfig { }
>
> @RestController
> public class CartController {
>     @PostMapping("/cart/add")
>     public Mono<Void> add(ServerWebExchange exchange, @RequestBody Item item) {
>         return exchange.getSession()
>             .flatMap(session -> {
>                 Cart cart = (Cart) session.getAttributes()
>                     .computeIfAbsent("cart", k -> new Cart());
>                 cart.add(item);
>                 return Mono.empty();
>             });
>     }
> }
> ```
>
> **Когда применять:**
> - Spring WebFlux приложение на Netty с session-based auth (например, OAuth2 login).
> - Gateway pattern: Spring Cloud Gateway с session, шарящимся между upstream-сервисами.
> - Backpressure-friendly stateful API с Redis store.
>
> **Подводные камни:**
> - `@EnableRedisIndexedHttpSession` (Servlet) и `@EnableRedisWebSession` (Reactive) в одном приложении нельзя — приложение либо MVC, либо WebFlux.
> - `WebSession` НЕ имеет метода `setMaxInactiveInterval(int)` — только `setMaxIdleTime(Duration)`.
> - `findByPrincipalName` доступен через `ReactiveFindByIndexNameSessionRepository`, но требует именно индексированной конфигурации (поддерживается с Spring Session 3.0+).
>
> **Связанные вопросы:** [[spring-session-interview#Q1]] — основы; [[spring-session-interview#Q2]] — Servlet Redis; [[spring-session-interview#Q5]] — find by principal.
>
> ---
>
> #### C) В WebFlux нужно использовать ту же `@EnableRedisIndexedHttpSession` — реактивная аннотация не существует — ❌ Неверно
>
> **Что на самом деле:** существует именно `@EnableRedisWebSession` (для reactive) и `@EnableRedisIndexedHttpSession` (для Servlet). Использование Servlet-аннотации в WebFlux приведёт к Bean creation error: `SessionRepositoryFilter` не подключится к reactive web context.
>
> **Откуда путаница:** в Boot 2 был только один способ для Servlet; разработчики, поздно перешедшие на WebFlux, по инерции используют знакомое.
>
> **Если бы это было правдой:** Spring Session reactive не имел бы смысла как отдельный модуль — но он существует и активно развивается.
>
> ---
>
> #### D) WebFlux сессии хранятся только в памяти Netty — внешний store (Redis) не поддерживается — ❌ Неверно
>
> **Что на самом деле:** `ReactiveRedisIndexedSessionRepository` полностью поддерживает Redis store для WebFlux. Архитектурно WebFlux и stateful Redis совместимы — Lettuce клиент сам реактивный и nonblocking.
>
> **Откуда путаница:** WebFlux ассоциируется со «stateless», и легко предположить что внешний state-store не работает.
>
> **Если бы это было правдой:** WebFlux не имел бы Spring Session интеграции вообще, и команды на reactive-стеке писали бы свой session-management руками — что противоречит реальности.

## Q10. Как Spring Session работает в кластере?

```text
Browser ─── LB ─┬─ App1 ─┐
                │          ├── Redis Session Store
                └─ App2 ─┘
```

1. Запрос от браузера приходит с `SESSION` cookie.
2. Балансировщик маршрутизирует на любой узел (round-robin).
3. Spring Session Filter перехватывает запрос.
4. Загружает сессию из Redis по ID из cookie.
5. Делает сессию доступной через `HttpSession`.
6. После обработки сохраняет изменённую сессию обратно в Redis.

**Нет sticky sessions** — любой узел может обработать любой запрос.


> [!mcq]
>
> **Вопрос:** Что происходит при попадании двух одновременных запросов от одного пользователя на разные узлы кластера со Spring Session?
>
> ---
>
> #### A) Оба узла читают сессию из Redis независимо — последняя запись (`save()`) перезаписывает первую (last-write-wins), потенциально теряя изменения первого запроса — ✓ Верно
>
> **Развёрнутое объяснение:**
>
> Spring Session не реализует distributed locking для сессии. Поведение по умолчанию — оптимистичное: каждый запрос читает HASH, модифицирует локальную копию, в конце пишет `HMSET`. Если два запроса в параллели:
> 1. Req-A (node1): читает `{cart: [item1]}`.
> 2. Req-B (node2): читает `{cart: [item1]}`.
> 3. Req-A: добавляет item2, пишет `{cart: [item1, item2]}`.
> 4. Req-B: добавляет item3 (на основе своей копии), пишет `{cart: [item1, item3]}`.
> 5. Результат: item2 потерян.
>
> Это race condition, классический lost update. В Spring Session нет встроенного решения — нужен external locking (Redisson, Lettuce + `WATCH/MULTI/EXEC`) либо проектировать сессию так, чтобы конкуренция не возникала (только atomic attributes, не комплексные структуры).
>
> **Пример:**
> ```java
> // Проблемный код: race-prone
> @PostMapping("/cart/add")
> public void addToCart(HttpSession session, @RequestBody Item item) {
>     Cart cart = (Cart) session.getAttribute("cart");
>     cart.add(item);  // ← in-memory mutation
>     session.setAttribute("cart", cart);  // ← save лосит конкурентные обновления
> }
>
> // Safer: atomic операции через Redis-side scripts или
> // distributed lock через Redisson
> @PostMapping("/cart/add")
> public void addToCart(@AuthenticationPrincipal User user, @RequestBody Item item) {
>     RLock lock = redisson.getLock("session-lock:" + user.getId());
>     lock.lock(5, TimeUnit.SECONDS);
>     try {
>         Cart cart = ...;
>         cart.add(item);
>         ...
>     } finally {
>         lock.unlock();
>     }
> }
> ```
>
> **Когда применять:**
> - Понимать race scenarios при дизайне session-based корзин, wizards, multi-step форм.
> - Принимать осознанные решения о тradeoff: lock-free + simple model vs locked + correct under contention.
> - Multi-tab UX: пользователь открывает 2 вкладки, делает действия параллельно — типичный источник race.
>
> **Подводные камни:**
> - Distributed lock даёт latency (acquire + release: 2-5ms) — на CRUD-эндпоинтах с тысячами RPS это заметно.
> - WebSocket-соединения держат session ссылку долго — расщепление состояния между ws-message handlers и REST-controllers особенно опасно.
> - Browser prefetch + double-submit может создавать одинаковый race локально, без явной кластерности.
>
> **Связанные вопросы:** [[spring-session-interview#Q1]] — основы; [[spring-session-interview#Q4]] — Security context; [[spring-session-interview#Q12]] — concurrency control.
>
> ---
>
> #### B) Spring Session использует Redis `WATCH/MULTI/EXEC` транзакции автоматически — конфликт обнаруживается и второй запрос делает retry — ❌ Неверно
>
> **Что на самом деле:** Spring Session использует `HMSET` без `WATCH`. Это просто write через connection. Оптимистическое блокирование не встроено. Если оно нужно — пишется руками.
>
> **Откуда путаница:** в Spring Data JPA есть `@Version` для optimistic lock, и легко перенести ожидание на Spring Session.
>
> **Если бы это было правдой:** все CRUD на сессии в high-concurrency сценариях ловили бы `OptimisticLockingFailureException` и retry — это было бы заметно в логах любого e-commerce приложения.
>
> ---
>
> #### C) Один из узлов получает session-lease (через distributed lock), другие ноды получают `503 Locked` пока lease активен — ❌ Неверно
>
> **Что на самом деле:** ничего подобного нет. Все ноды равноправны, никакой `session-lease` концепции в Spring Session не существует. 503 не возвращается для конкурентных session-обращений.
>
> **Откуда путаница:** в Hazelcast и некоторых distributed cache-системах есть per-key lease, и можно ошибочно предположить такое же для Redis.
>
> **Если бы это было правдой:** multi-tab UX был бы сломан — открытие второй вкладки роняло бы первую с 503, пользователи бы массово жаловались.
>
> ---
>
> #### D) Конкурентные обращения сериализуются через Redis `SETNX session-id` — Redis сам гарантирует один запрос за раз — ❌ Неверно
>
> **Что на самом деле:** `SETNX` это команда установки ключа при отсутствии — не блокирующая. Spring Session ничего такого не делает. Конкурентные запросы идут параллельно без сериализации.
>
> **Откуда путаница:** `SETNX` — классический паттерн distributed lock, и многие ассоциируют любую Redis-based блокировку именно с ним.
>
> **Если бы это было правдой:** каждый запрос ждал бы освобождения, throughput сессионных эндпоинтов был бы ограничен latency Redis (1-2K RPS на сессию), и rapid clicking UI приводил бы к видимым задержкам.

## Q11. Как мигрировать с HTTP Session на Spring Session Redis?

```java
// 1. Добавить зависимость и конфигурацию (steps above)
// 2. Проблема: существующие пользователи потеряют сессии при деплое

// Решение 1: rolling restart — новые сессии сразу в Redis
// Старые in-memory сессии истекут при перезапуске каждого узла

// Решение 2: двойное хранилище во время миграции
@Bean
public SessionRepository<MapSession> migrationSessionRepository() {
    // временный in-memory для пользователей с старыми сессиями
    return new MapSessionRepository(new ConcurrentHashMap<>());
}
```

На практике: плановое обслуживание с принудительным logout всех пользователей или rolling restart с сообщением о повторном входе.


> [!mcq]
>
> **Вопрос:** Какой план миграции с in-memory HttpSession (Tomcat) на Spring Session Redis минимизирует логауты в production?
>
> ---
>
> #### A) Запустить rolling deploy новой версии Spring Boot с Redis-сессиями — старые HTTP-сессии будут автоматически прочитаны и переписаны в Redis — ❌ Неверно
>
> **Что на самом деле:** old in-memory сессии в Tomcat не имеют способа быть «прочитаны» новым приложением — они хранятся в JVM heap старого процесса. После рестарта процесс умирает вместе с сессиями. Никакой автоматической миграции state не существует.
>
> **Откуда путаница:** Tomcat Session Manager имеет опцию persistence-on-restart (`PersistentManager`), и можно предположить аналог для Spring Session. Но это про дисковую сериализацию Tomcat-а в файл, не про cross-process миграцию.
>
> **Если бы это было правдой:** rolling deploy на Spring Session был бы тривиально безопасным — но в реальности всегда сопровождается массовым логаутом текущей пользовательской базы.
>
> ---
>
> #### B) Принудительный logout всех пользователей в maintenance window — затем deploy новой версии. Это самый простой и предсказуемый путь, но требует downtime/неудобства для пользователей — ❌ Неверно
>
> **Что на самом деле:** force-logout всех — рабочий, но не «минимизирующий логауты» подход. Это maximum-logout стратегия. Вопрос специально про minimum.
>
> **Откуда путаница:** часто инженеры выбирают «простое и предсказуемое», особенно если SLA для веб-UI не строгий. Это валидно, но не оптимально.
>
> **Если бы это было правдой (что это минимально-disruptive)**: тогда e-commerce платформы делали бы плановые logout-окна каждый раз при обновлении — но Booking, Amazon, Yandex Lavka так не делают.
>
> ---
>
> #### C) Rolling deploy с включением Spring Session на новых подах: пользователи на старых подах продолжают работать со старыми сессиями (до их естественного истечения или ребалансировки), новые сессии создаются уже в Redis. Между двумя группами — `(re)login`-friendly UX flow — ✓ Верно
>
> **Развёрнутое объяснение:**
>
> Лучший подход — постепенная миграция:
> 1. **Pre-deploy:** включить session id resolver, который параллельно проверяет два формата (legacy Tomcat sessionid + новый Spring Session id).
> 2. **Rolling deploy:** на новых подах активирован `spring.session.store-type=redis`. На старых — пока in-memory.
> 3. **Sticky routing на legacy:** Layer 7 балансировщик пока маршрутизирует pre-existing `JSESSIONID` cookie на старые ноды (cookie-based stickiness).
> 4. **Естественная миграция:** новые пользователи получают `SESSION` cookie от новых подов → Redis. Старые пользователи доживают свои сессии до timeout.
> 5. **Cutover:** через 30 минут (typical session timeout) старые поды можно вывести.
>
> Альтернатива — sticky JSESSIONID на старые поды + параллельный Spring Session на новых → grace period 30 мин → drain.
>
> **Пример:**
> ```nginx
> # nginx — sticky pre-existing JSESSIONID на legacy pool
> upstream legacy { server pod-old-1; server pod-old-2; }
> upstream new    { server pod-new-1; server pod-new-2; }
>
> map $cookie_JSESSIONID $target_pool {
>     default new;
>     "~^.+$" legacy;   # есть JSESSIONID → старый pool
> }
> server {
>     location / {
>         proxy_pass http://$target_pool;
>     }
> }
> ```
> ```yaml
> # новые поды
> spring:
>   session:
>     store-type: redis
>     cookie-name: SESSION   # новый cookie, не JSESSIONID
> ```
>
> **Когда применять:**
> - Любая stateful миграция в production с активной пользовательской базой.
> - Multi-tier migration: feature flag + canary + gradual cutover — типичный pattern в Booking, Wolt, Авито.
> - Когда SLA на UX превышает простоту deploy-пайплайна.
>
> **Подводные камни:**
> - Необходим Layer 7 LB с cookie-based routing (nginx, HAProxy, AWS ALB — да; classic L4 — нет).
> - Smoke test перед cutover должен покрыть оба формата cookie — иначе race на drain.
> - Пользователи на старых сессиях не получат новые фичи новой версии — это temporary, но требует UX-коммуникации.
>
> **Связанные вопросы:** [[spring-session-interview#Q1]] — основы; [[spring-session-interview#Q8]] — миграция сериализатора; [[spring-session-interview#Q10]] — кластер.
>
> ---
>
> #### D) Использовать `JSESSIONID` cookie unchanged — Spring Session автоматически интерпретирует Tomcat session id и переносит данные в Redis — ❌ Неверно
>
> **Что на самом деле:** Spring Session по умолчанию использует cookie с именем `SESSION` (можно переименовать в `JSESSIONID` через `spring.session.cookie.name`). Но имя cookie ничего не меняет — данные внутри Tomcat memory не доступны новому процессу.
>
> **Откуда путаница:** одинаковое имя cookie создаёт иллюзию непрерывности, но это просто identifier, а не контейнер данных.
>
> **Если бы это было правдой:** Spring Session реализовывала бы какой-то Tomcat-specific введённый API, что нарушало бы container-agnostic архитектуру (Jetty, Undertow тоже поддерживаются).

## Q12. Что такое Session Concurrency Control?

**Concurrency Control** — ограничение числа одновременных сессий одного пользователя.

```java
@Bean
public SecurityFilterChain filterChain(HttpSecurity http,
                                        SessionRegistry sessionRegistry) throws Exception {
    http.sessionManagement(session -> session
        .maximumSessions(3)                    // максимум 3 сессии
        .maxSessionsPreventsLogin(true)        // блокировать новый логин если лимит исчерпан
        .sessionRegistry(sessionRegistry)      // необходимо для Redis
    );
    return http.build();
}

@Bean
public SessionRegistry sessionRegistry() {
    return new SpringSessionBackedSessionRegistry<>(
        (FindByIndexNameSessionRepository) sessionRepository);
}
```

`SpringSessionBackedSessionRegistry` — реализация `SessionRegistry` поверх Spring Session, необходимая для concurrency control в кластере.


> [!mcq]
>
> **Вопрос:** Почему стандартный `SessionRegistryImpl` Spring Security НЕ работает для concurrency control в кластере, и что используется вместо него?
>
> ---
>
> #### A) `SessionRegistryImpl` хранит активные сессии в локальной `ConcurrentMap` (in-memory per JVM) — каждый узел видит только свои сессии, лимит `maximumSessions(1)` работает только в рамках одного процесса. Решение — `SpringSessionBackedSessionRegistry`, использующий `FindByIndexNameSessionRepository` для кросс-кластерного учёта — ✓ Верно
>
> **Развёрнутое объяснение:**
>
> `SessionRegistryImpl` (default Spring Security) использует `ConcurrentMap<Object, Set<String>>` с principal → sessionIds. Эта map живёт в heap текущего JVM. На узлах node1 и node2 это разные maps. Если пользователь логинится на node1 (sessionId=A), затем на node2 (sessionId=B), то:
> - node1 видит только `{user → [A]}`, лимит не превышен.
> - node2 видит только `{user → [B]}`, лимит не превышен.
> - Глобально активных сессий 2, но `maximumSessions(1)` не сработает.
>
> `SpringSessionBackedSessionRegistry` (из `spring-session-core`) переопределяет реестр поверх `FindByIndexNameSessionRepository`. На запрос `getAllSessions(principal, false)` он делает `repo.findByPrincipalName(principal)` — это идёт в Redis SET `spring:session:sessions:index:principalName:<user>` и возвращает все сессии глобально.
>
> **Пример:**
> ```java
> @Configuration
> public class SessionRegistryConfig {
>     @Bean
>     public SpringSessionBackedSessionRegistry<? extends Session> sessionRegistry(
>             FindByIndexNameSessionRepository<? extends Session> repo) {
>         return new SpringSessionBackedSessionRegistry<>(repo);
>     }
> }
>
> @Configuration
> public class SecurityConfig {
>     @Bean
>     SecurityFilterChain chain(HttpSecurity http, SessionRegistry registry)
>             throws Exception {
>         return http.sessionManagement(s -> s
>             .maximumSessions(1)
>             .maxSessionsPreventsLogin(true)   // 2-й login откажет
>             .sessionRegistry(registry))
>             .build();
>     }
> }
> ```
>
> **Когда применять:**
> - Банковский кабинет: «один активный вход» по требованию compliance.
> - Платные подписки (Netflix, Spotify): N устройств одновременно.
> - Корпоративные системы с аудитом: знать «кто сейчас в системе».
> - Force-logout всех сессий пользователя при смене пароля.
>
> **Подводные камни:**
> - `@EnableRedisIndexedHttpSession` обязателен, иначе `findByPrincipalName` бросит `UnsupportedOperationException`.
> - `maxSessionsPreventsLogin(true)` блокирует второй вход — пользователь видит ошибку и должен сначала разлогиниться. UX-friendly альтернатива: `false` (старая сессия истекает, новая создаётся).
> - В моменте между login attempts возможна race: 2 параллельных login могут оба пройти проверку до save → 2 сессии вместо 1. Mitigation: distributed lock на principal.
>
> **Связанные вопросы:** [[spring-session-interview#Q4]] — Spring Security интеграция; [[spring-session-interview#Q5]] — find by principal; [[spring-session-interview#Q10]] — кластер.
>
> ---
>
> #### B) `SessionRegistryImpl` Spring Security работает в кластере если включить `@EnableSessionRegistryReplication` — Spring сам распространяет события через Redis Pub/Sub — ❌ Неверно
>
> **Что на самом деле:** аннотации `@EnableSessionRegistryReplication` не существует. Spring Security сам не делает distributed registry. Нужна явная интеграция со Spring Session через `SpringSessionBackedSessionRegistry`.
>
> **Откуда путаница:** есть `@EnableRedisHttpSession`, `@EnableJdbcHttpSession`, `@EnableRedisIndexedHttpSession` — кажется правдоподобной аннотация и для registry.
>
> **Если бы это было правдой:** не было бы смысла в `spring-session-core` — все жило бы внутри Spring Security. Но они архитектурно разделены.
>
> ---
>
> #### C) В кластере concurrency control не работает в принципе — для этого нужен JWT с jti revocation list — ❌ Неверно
>
> **Что на самом деле:** работает через `SpringSessionBackedSessionRegistry`. JWT — отдельный подход, и сам по себе jti revocation list требует точно такого же distributed store (Redis), что и Spring Session — никакого выигрыша.
>
> **Откуда путаница:** «масштабирование = JWT» — народный миф последних лет.
>
> **Если бы это было правдой:** все session-based приложения в банках, государственных системах должны были бы перейти на JWT — но они продолжают использовать session-based auth + concurrency control.
>
> ---
>
> #### D) Достаточно настроить `maximumSessions(1)` в `HttpSecurity` — Spring автоматически выбирает кластерную реализацию реестра, если есть Spring Session — ❌ Неверно
>
> **Что на самом деле:** auto-configuration `SessionRegistry` НЕ происходит. По умолчанию используется `SessionRegistryImpl` (in-memory). Чтобы получить кластерную работу, нужно явно зарегистрировать `SpringSessionBackedSessionRegistry` bean и передать его в `.sessionRegistry(registry)`.
>
> **Откуда путаница:** Spring Boot магическим образом подключает многое — кажется, что и здесь так.
>
> **Если бы это было правдой:** проблема концurrency в кластере не была бы FAQ-topic, и не существовало бы статей «почему `maximumSessions(1)` не работает в моём deploy».

## Q13. Чем Spring Session отличается от Tomcat Session Manager?

| Критерий | Tomcat Session Manager | Spring Session |
|----------|------------------------|----------------|
| Хранилище | Память Tomcat | Redis/JDBC/MongoDB |
| Кластер | Sticky sessions / Replication | Любой узел |
| Контейнер | Tomcat-зависимо | Любой (Tomcat/Jetty/Undertow) |
| Реактивный стек | Нет | Есть (WebSession) |
| Find-by-user | Нет | Через FindByIndexNameSessionRepository |
| Метрики | Ограничены | Spring Actuator metrics |

Spring Session предпочтительнее в облачных/контейнерных средах где инстансы эфемерны.


> [!mcq]
>
> **Вопрос:** Какое из утверждений корректно описывает архитектурное отличие Spring Session от Tomcat `DeltaManager`?
>
> ---
>
> #### A) Tomcat `DeltaManager` использует UDP multicast для репликации между нодами — Spring Session использует TCP unicast к Redis — ❌ Неверно
>
> **Что на самом деле:** Tomcat `DeltaManager` использует Apache Tribes для cluster communication, по умолчанию через UDP multicast, но это деталь. Главное архитектурное отличие не в транспорте — в самой модели (peer-to-peer replication vs centralized store). Spring Session это полная смена архитектуры, не просто другой транспорт.
>
> **Откуда путаница:** транспорт — самое поверхностное наблюдаемое отличие. На сетевом уровне разница видна сразу.
>
> **Если бы это было правдой (и это было главным отличием)**: достаточно было бы заменить UDP на TCP в Tribes-конфиге — но Spring Session принёс ДРУГУЮ модель шторма (centralized vs peer).
>
> ---
>
> #### B) Tomcat `DeltaManager` шлёт каждой ноде delta-обновления сессии (peer-to-peer replication) — O(N²) трафика для N нод. Spring Session использует centralized store (Redis/JDBC) — O(N) RTT, scalable, container-agnostic, поддерживает WebFlux — ✓ Верно
>
> **Развёрнутое объяснение:**
>
> Архитектурно это разные модели:
>
> **Tomcat `DeltaManager` (peer-to-peer):**
> - Каждая нода держит full copy всех сессий в heap.
> - На каждое изменение `setAttribute` шлёт delta всем остальным нодам через Apache Tribes.
> - Масштабируется плохо: при N=10 нодах каждая сессия дублируется 10 раз, трафик репликации растёт квадратично.
> - Привязан к Tomcat — не работает в Jetty, Undertow, Netty.
> - Не работает с WebFlux (там нет HttpSession).
>
> **Spring Session (centralized):**
> - Сессии — внешний store (Redis/JDBC/MongoDB), приложение stateless.
> - Каждый узел делает RTT в store при чтении/записи. Памяти приложение не тратит.
> - Линейно масштабируется до пропускной способности store (Redis Cluster — миллионы ops/s).
> - Container-agnostic: работает с Tomcat, Jetty, Undertow, Netty.
> - Поддерживает WebFlux через `WebSession`.
>
> **Пример:**
> ```text
> Tomcat DeltaManager (N=4 ноды, 1M сессий):
>   Память каждой ноды: 1M × avg_session_size (×4 копии)
>   Replication traffic: O(N²) на update — peer-to-peer mesh
>
> Spring Session + Redis Cluster (N=4 ноды, 1M сессий):
>   Память каждой ноды: ~constant (нет sessions in heap)
>   Replication traffic: O(1) RTT к Redis на update
>   Redis memory: 1M × avg_session_size (одна копия + replicas)
> ```
>
> **Когда применять:**
> - Spring Session: cloud-native, Kubernetes, эфемерные поды, microservices, WebFlux.
> - Tomcat DeltaManager: legacy on-premise, фиксированный malый кластер (2-4 nodes), все ноды в одном LAN с low-latency multicast.
>
> **Подводные камни:**
> - Spring Session — single point of failure store: Redis падает → весь auth ломается. Mitigation: Redis Cluster + Sentinel.
> - DeltaManager — split-brain при network partition: каждая половина мнит себя primary, после merge — конфликты сессий.
> - В Kubernetes DeltaManager практически не работает (multicast часто отключён в pod network).
>
> **Связанные вопросы:** [[spring-session-interview#Q1]] — основы; [[spring-session-interview#Q10]] — кластер; [[spring-session-interview#Q15]] — когда не использовать.
>
> ---
>
> #### C) Spring Session — это просто wrapper над Tomcat Session Manager: внутри он использует тот же `org.apache.catalina.session.StandardManager`, просто с другим store backend — ❌ Неверно
>
> **Что на самом деле:** Spring Session полностью независим от Tomcat-кода. `SessionRepositoryFilter` оборачивает запрос так, что `request.getSession()` возвращает свой `HttpSessionWrapper` поверх `Session`. Tomcat `StandardManager` при этом обходится — он не используется.
>
> **Откуда путаница:** в логах Spring Boot можно увидеть Tomcat session инициализацию (он создаётся по умолчанию), и кажется что Spring Session его расширяет.
>
> **Если бы это было правдой:** Spring Session не работал бы на Jetty/Undertow/Netty — но он работает.
>
> ---
>
> #### D) Tomcat `DeltaManager` и Spring Session не отличаются по производительности — оба дают O(N) трафика. Разница только в API — ❌ Неверно
>
> **Что на самом деле:** DeltaManager имеет O(N²) replication overhead, Spring Session — O(1) на узел при обращении к centralized store. Это принципиально разные scaling-характеристики, влияющие на максимальный размер кластера.
>
> **Откуда путаница:** на маленьких кластерах (2-3 ноды) разница неощутима, и можно решить что её нет в принципе.
>
> **Если бы это было правдой:** не было бы движения к centralized session stores в индустрии — но Netflix, Booking, LinkedIn, Wolt и большинство облачных приложений используют именно centralized.

## Q14. Как настроить мониторинг сессий через Actuator?

```yaml
management:
  endpoints:
    web:
      exposure:
        include: sessions, health, metrics
  endpoint:
    sessions:
      enabled: true
```

```bash
# Информация о сессии конкретного пользователя
GET /actuator/sessions?username=john.doe@example.com

# Удалить конкретную сессию
DELETE /actuator/sessions/{sessionId}
```

Метрики в Micrometer: `spring.session.sessions.open` — текущее число открытых сессий.


> [!mcq]
>
> **Вопрос:** Какие требования необходимо выполнить, чтобы actuator endpoint `/actuator/sessions` работал корректно в production?
>
> ---
>
> #### A) Endpoint автоматически доступен всем — никакой настройки безопасности не нужно, Spring Boot гарантирует, что только authorized users видят сессии — ❌ Неверно
>
> **Что на самом деле:** `/actuator/sessions` по умолчанию доступен только локально (`management.endpoint.sessions.enabled=true` + `management.endpoints.web.exposure.include`). Дополнительно ОБЯЗАТЕЛЬНО защитить Spring Security-конфигом — иначе любой attacker, имея доступ к /actuator, видит всех пользователей и их sessionId. Это критическая security дыра уровня Capital One 2019 (там был открыт `/env`).
>
> **Откуда путаница:** Spring Boot 2.1+ закрыл многие endpoints по умолчанию, и сложилось мнение «все secure by default».
>
> **Если бы это было правдой:** Equifax/Capital One сценарий: attacker делает `GET /actuator/sessions?username=admin@bank.com` → получает sessionId → подделывает `SESSION` cookie → берёт под контроль admin-сессию. Полная компрометация.
>
> ---
>
> #### B) Достаточно включить `management.endpoint.sessions.enabled=true` — все требования автоматически выполняются — ❌ Неверно
>
> **Что на самом деле:** `enabled=true` только делает endpoint доступным для exposure-механизма. Нужно также: (1) добавить `sessions` в `management.endpoints.web.exposure.include`; (2) обеспечить наличие `FindByIndexNameSessionRepository` (т.е. `@EnableRedisIndexedHttpSession`); (3) защитить путь через Spring Security; (4) endpoint работает только с indexed репозиторием.
>
> **Откуда путаница:** `enabled=true` звучит как «всё готово».
>
> **Если бы это было правдой:** endpoint работал бы и без `Indexed` репозитория — но реально на `@EnableRedisHttpSession` (без indexed) он вернёт `UnsupportedOperationException`.
>
> ---
>
> #### C) Нужно: (1) `FindByIndexNameSessionRepository` в context (т.е. `@EnableRedisIndexedHttpSession`); (2) `management.endpoints.web.exposure.include=sessions`; (3) `management.endpoint.sessions.enabled=true`; (4) Spring Security-конфиг, ограничивающий путь `/actuator/sessions/**` ролью ADMIN — ✓ Верно
>
> **Развёрнутое объяснение:**
>
> Endpoint `/actuator/sessions` реализован классом `SessionsEndpoint` (из `spring-boot-actuator`), который требует `FindByIndexNameSessionRepository<? extends Session>` в context. Без него autoconfig не создаёт bean, и endpoint просто отсутствует.
>
> Полная корректная настройка:
>
> **Пример:**
> ```yaml
> management:
>   endpoints:
>     web:
>       exposure:
>         include: health, info, sessions, prometheus
>   endpoint:
>     sessions:
>       enabled: true
>   server:
>     port: 9090   # отдельный port — изолировать от public traffic
>     address: 127.0.0.1   # доступен только из cluster network
> spring:
>   session:
>     store-type: redis
> ```
> ```java
> @Configuration
> @EnableRedisIndexedHttpSession   // нужен Indexed!
> public class SessionConfig {}
>
> @Configuration
> public class ActuatorSecurityConfig {
>     @Bean
>     SecurityFilterChain actuator(HttpSecurity http) throws Exception {
>         return http
>             .securityMatcher("/actuator/**")
>             .authorizeHttpRequests(a -> a
>                 .requestMatchers("/actuator/health").permitAll()
>                 .requestMatchers("/actuator/sessions/**").hasRole("ADMIN")
>                 .anyRequest().hasRole("OPS"))
>             .httpBasic(Customizer.withDefaults())
>             .build();
>     }
> }
> ```
>
> Использование:
> ```bash
> # admin кикает сессию пользователя
> curl -u admin:*** -X DELETE \
>   http://app:9090/actuator/sessions/abc-123-session-id
>
> # admin смотрит активные сессии user-а
> curl -u admin:*** \
>   "http://app:9090/actuator/sessions?username=alice"
> ```
>
> **Когда применять:**
> - Operations dashboard: видеть кто сейчас залогинен, force-logout incident response.
> - Compliance audit: список активных сессий за период.
> - Debugging customer support: «у меня не работает» → admin посмотрит активные устройства.
>
> **Подводные камни:**
> - Имя пользователя (principal) часто PII — exposing через actuator может нарушать GDPR без хеширования.
> - Endpoint возвращает session metadata, но не содержимое атрибутов — для full debug нужен прямой Redis access.
> - Без `Indexed` репозитория endpoint молча отсутствует, без error в логах — пользователи долго ищут «почему 404».
>
> **Связанные вопросы:** [[spring-session-interview#Q5]] — `FindByIndexNameSessionRepository`; [[spring-session-interview#Q12]] — concurrency control; [[spring-session-interview#Q15]] — когда не использовать.
>
> ---
>
> #### D) Actuator endpoint `/sessions` использует свой собственный Redis namespace `actuator:sessions:*`, не пересекающийся с реальными сессиями — нужен отдельный Redis instance — ❌ Неверно
>
> **Что на самом деле:** endpoint читает те же сессии через `FindByIndexNameSessionRepository`. Отдельного namespace или Redis instance не требуется — это та же централизованная инфраструктура.
>
> **Откуда путаница:** Boot Admin в некоторых конфигурациях использует отдельные key spaces — можно перенести аналогию.
>
> **Если бы это было правдой:** endpoint показывал бы не реальные сессии, а свой parallel state — бесполезно для diagnostics.

## Q15. Когда не стоит использовать Spring Session?

1. **Stateless JWT-аутентификация** — токены не требуют серверного хранения. Spring Session избыточен.

2. **Одноузловые приложения без HA** — стандартных HTTP сессий достаточно.

3. **Высоконагруженные read-heavy сценарии** — каждый запрос читает сессию из Redis; при большом числе RPM это дополнительная нагрузка.

4. **Микросервисы без общей сессии** — сессии API Gateway (OAuth2/OpenID Connect) лучше управлять через Authorization Server.

**Альтернатива**: Spring Security + JWT + stateless `SessionCreationPolicy.STATELESS`.


> [!mcq]
>
> **Вопрос:** В каком сценарии Spring Session — НЕОПРАВДАННАЯ архитектурная сложность, и какая альтернатива лучше?
>
> ---
>
> #### A) Любое микросервисное приложение в Kubernetes — всегда лучше JWT, Spring Session устарел — ❌ Неверно
>
> **Что на самом deле:** для микросервисов с session-based login (BFF pattern: gateway держит сессию, в downstream идут JWT) Spring Session — основной выбор. «Устарел» — это не аргумент: библиотека активно развивается, Spring Session 3 поддерживает Reactive, имеет integration с Spring Authorization Server.
>
> **Откуда путаница:** «cloud-native = stateless = JWT» — мантра последних лет, не всегда применимая.
>
> **Если бы это было правдой:** Booking, Wolt, банковские BFF никогда бы не использовали session-store — но они используют, для UX (instant logout, force-revoke).
>
> ---
>
> #### B) Высоконагруженный публичный API на 100K RPS со stateless JWT-auth и без UI-сессии — здесь Spring Session добавит +1 RTT к Redis на каждый запрос без бизнес-выгоды. Альтернатива: `SessionCreationPolicy.STATELESS` + JWT через OAuth2 Resource Server — ✓ Верно
>
> **Развёрнутое объяснение:**
>
> Spring Session оправдан, когда есть **server-side session state**: корзина, multi-step wizard, OAuth2 login flow с PKCE. Если этого нет — мы платим overhead зря.
>
> Конкретные показатели для stateless публичного API:
> - 100K RPS × 1 Redis RTT (~1ms p50, 5ms p99) → дополнительная latency и нагрузка на Redis Cluster.
> - JWT даёт полную аутентификацию из самого токена: подпись валидируется локально (Nimbus JOSE+JWT), без сетевых вызовов.
> - Революция сессии не нужна, если токен живёт коротко (5-15 мин) и есть refresh-flow.
>
> **Пример:**
> ```java
> // НЕТ Spring Session
> @Configuration
> @EnableWebSecurity
> public class StatelessApiSecurity {
>     @Bean
>     SecurityFilterChain api(HttpSecurity http) throws Exception {
>         return http
>             .csrf(AbstractHttpConfigurer::disable)
>             .sessionManagement(s -> s.sessionCreationPolicy(STATELESS))
>             .oauth2ResourceServer(rs -> rs.jwt(Customizer.withDefaults()))
>             .authorizeHttpRequests(a -> a.anyRequest().authenticated())
>             .build();
>     }
> }
> ```
> ```yaml
> spring:
>   security:
>     oauth2:
>       resourceserver:
>         jwt:
>           issuer-uri: https://auth.example.com
>   session:
>     # отключено явно
>     store-type: none
> ```
>
> **Когда применять (т.е. не использовать Spring Session):**
> - Публичные REST/GraphQL API для мобильных приложений (stateless по дизайну).
> - Service-to-service auth через client credentials grant.
> - High-volume integrations (B2B partners, payment gateways) с 100K+ RPS.
> - Edge каталог-сервисы (read-heavy, без personalization в сессии).
>
> **Подводные камни:**
> - JWT нельзя инвалидировать мгновенно — пока живёт `exp`, токен валиден. Если нужна моментальная revocation, всё равно нужен server-side store (но тогда возвращаемся к sessions).
> - JWT distinguish content больше — `Authorization: Bearer` header может быть 1-2 KB при rich claims. Это сетевой overhead на каждом запросе.
> - JWT не подходит для form-login UX (классические UI с cookie-based session) — попытки натянуть JWT на browser-приложения порождают XSS-уязвимости при хранении в localStorage.
>
> **Связанные вопросы:** [[spring-session-interview#Q1]] — что такое Spring Session; [[spring-session-interview#Q4]] — Security integration; [[spring-session-interview#Q13]] — vs Tomcat Manager.
>
> ---
>
> #### C) Если приложение деплоится в один pod без HA — Spring Session бесполезен, и можно использовать in-memory HttpSession даже в Kubernetes — ❌ Неверно
>
> **Что на самом деле:** в Kubernetes один pod может рестартануть в любой момент (rolling deploy, node drain, OOMKill, eviction). Даже без явного HA pod ephemeral. Spring Session оправдан даже для single-instance deploy просто ради переживания рестартов.
>
> **Откуда путаница:** «single-instance = одиночный сервер» — ментальная модель из эпохи monolith on bare-metal, где сервер мог стоять годами.
>
> **Если бы это было правдой:** ни одно single-pod Spring приложение в Kubernetes не использовало бы Spring Session — но любая kubectl rollout-операция выкидывала бы всех пользователей в логин.
>
> ---
>
> #### D) Spring Session нельзя использовать для админ-панелей внутренних систем — Redis избыточен для маленькой пользовательской базы — ❌ Неверно
>
> **Что на самом деле:** для админ-панелей с 10-100 пользователями Spring Session JDBC (поверх существующей БД) — отличный выбор без необходимости в Redis. Argument «маленькая база» не оправдывает отказ — пользы от force-logout, audit, concurrent control столько же.
>
> **Откуда путаница:** Redis ассоциируется с high-scale, и кажется что для small-scale он overkill.
>
> **Если бы это было правдой:** все админ-системы Spring-стека шли бы на in-memory session — но реально команды осознанно выбирают JDBC store ради операционной зрелости.

## See also

- [Spring Security](spring-security-interview.md) — аутентификация и авторизация, интеграция с сессиями
- [Redis](../../databases/redis-interview.md) — Redis как наиболее популярное хранилище сессий
- [Spring Boot](spring-boot-interview.md) — auto-configuration для Spring Session
- [JWT](../../security/jwt-interview.md) — stateless альтернатива session-based auth
- [OAuth2](../../security/oauth2-interview.md) — OAuth2 вместо сессий в микросервисах
- [Load Balancing](../../architecture/load-balancing-interview.md) — sticky sessions vs external session store
- [Scalability Patterns](../../architecture/scalability-patterns-interview.md) — horizontal scaling с shared сессиями
- [Microservices](../../architecture/microservices-interview.md) — session sharing между инстансами
- [Auth Patterns](../../security/authentication-authorization-patterns-interview.md) — session vs token-based аутентификация
- [MongoDB](../../databases/mongodb-interview.md) — MongoDB как альтернативное хранилище сессий
