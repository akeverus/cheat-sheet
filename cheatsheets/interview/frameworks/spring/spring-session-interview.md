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
updated: 2026-05-31
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

**Spring Session** — абстракция управления HTTP-сессиями, которая отделяет хранилище сессии от контейнера сервлетов (Tomcat/Jetty) и выносит её во внешнее хранилище.

**Какую проблему решает.** По умолчанию HTTP-сессия живёт в памяти Tomcat — то есть привязана к одному конкретному узлу. В кластере из N узлов запрос пользователя через балансировщик может попасть на другой сервер, где этой сессии нет, и пользователь её «теряет» (например, его разлогинивает). Исторические обходные пути — sticky sessions (балансировщик закрепляет клиента за узлом) или Tomcat Session Replication (узлы реплицируют сессии между собой) — либо ограничивают балансировку, либо плохо масштабируются.

**Как решает.** Spring Session перехватывает работу с сессией и хранит её во внешнем сторе (Redis, JDBC, MongoDB), общем для всех узлов. Любой узел кластера видит одну и ту же сессию по её ID из cookie — узлы становятся stateless и взаимозаменяемыми.

```java
// Подключение
@EnableRedisIndexedHttpSession
@Configuration
public class SessionConfig { }
```

**Поддерживаемые хранилища:** Redis (`spring-session-data-redis`), JDBC (`spring-session-jdbc`), MongoDB (`spring-session-mongodb`), Hazelcast.

## Q2. Как настроить Redis Session Store?

Достаточно добавить стартер `spring-session-data-redis` — в Spring Boot 3.x стор определяется автоматически по зависимости на classpath, а свойство `spring.session.store-type` из Boot 2.x **удалено**. Boot автоконфигурирует фильтр и репозиторий, дальше работа с сессией идёт прозрачно. Если на classpath случайно оказалось несколько сторов, выбор фиксируют явной аннотацией (`@EnableRedisHttpSession`, `@EnableJdbcHttpSession` и т.п.).

**Зависимость:**

```xml
<dependency>
    <groupId>org.springframework.session</groupId>
    <artifactId>spring-session-data-redis</artifactId>
</dependency>
```

**Конфигурация:**

```yaml
spring:
  session:
    timeout: 30m              # время жизни сессии
    redis:
      namespace: myapp:session
      flush-mode: on-save     # или immediate
  data:
    redis:
      host: localhost
      port: 6379
```

Ключевые параметры: `namespace` задаёт префикс ключей (чтобы несколько приложений не пересекались в одном Redis), а `flush-mode` определяет, когда изменения пишутся в Redis — `on-save` (по умолчанию, запись в конце запроса, эффективнее) или `immediate` (сразу при изменении атрибута, нужно для редких сценариев с межзапросной видимостью).

**Транспорт session ID настраивается отдельно.** По умолчанию ID ездит в cookie с именем `SESSION`; бин `DefaultCookieSerializer` (реализация `CookieSerializer`) позволяет поменять имя cookie, домен и path, а главное — атрибут `SameSite` через `setSameSite(...)` (по умолчанию `Lax`; для cross-site сценариев вроде iframe нужен `None` вместе с `Secure`). Для REST API без cookie регистрируют бин `HttpSessionIdResolver` через `HeaderHttpSessionIdResolver.xAuthToken()` — тогда session ID передаётся в заголовке `X-Auth-Token`: сервер возвращает его в ответе на логин, клиент шлёт в каждом следующем запросе.

**Как сессия хранится в Redis** — в виде hash:

```java
// В Redis сессия хранится как hash:
// key: myapp:session:sessions:<sessionId>
// fields: lastAccessedTime, maxInactiveInterval, sessionAttr:<name>
```

**Что происходит под капотом.** `SessionRepositoryFilter` перехватывает вызов `HttpServletRequest.getSession()` и подменяет стандартную сессию контейнера на загруженную из Redis. Для кода приложения API не меняется — это всё тот же `HttpSession`.

## Q3. Как настроить JDBC Session Store?

JDBC-стор хранит сессии в реляционной БД — подходит, когда Redis нет, а отдельную инфраструктуру под сессии заводить не хочется. Подключается стартером `spring-session-jdbc`: как и с Redis, Spring Boot 3.x выбирает стор по classpath — указывать `store-type: jdbc` не нужно, это свойство удалено в Boot 3.0.

**Зависимость:**

```xml
<dependency>
    <groupId>org.springframework.session</groupId>
    <artifactId>spring-session-jdbc</artifactId>
</dependency>
```

**Конфигурация:**

```yaml
spring:
  session:
    jdbc:
      initialize-schema: always  # auto-create tables
      table-name: SPRING_SESSION
  datasource:
    url: jdbc:postgresql://localhost:5432/mydb
```

**Схема.** Spring Session хранит данные в двух таблицах: сама сессия и её атрибуты (последние — сериализованными байтами в `ATTRIBUTE_BYTES`):

```sql
-- Автоматически создаваемые таблицы:
-- SPRING_SESSION (PRIMARY_ID, SESSION_ID, CREATION_TIME, LAST_ACCESS_TIME, MAX_INACTIVE_INTERVAL, EXPIRY_TIME, PRINCIPAL_NAME)
-- SPRING_SESSION_ATTRIBUTES (SESSION_PRIMARY_ID, ATTRIBUTE_NAME, ATTRIBUTE_BYTES)
```

**Важный нюанс — очистка.** В отличие от Redis, у БД нет встроенного TTL, поэтому истёкшие сессии не удаляются сами. Spring Boot заводит периодическую задачу-уборщик, удаляющую просроченные строки; её расписание настраивается через `spring.session.jdbc.cleanup-cron`.

**Компромисс:** JDBC проще в эксплуатации (используется уже имеющаяся БД), но запись/чтение сессии на каждый запрос даёт лишнюю нагрузку на основную базу — для высокой нагрузки Redis предпочтительнее.

## Q4. Как Spring Session интегрируется с Spring Security?

Интеграция бесшовная: Spring Security хранит контекст аутентификации в HTTP-сессии, а Spring Session подменяет эту сессию внешним стором — отдельной настройки «связать их» не требуется. Поверх этого Spring Security даёт управление сессиями: политику создания, лимит одновременных сессий, страницу истечения.

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

**Как они работают вместе.** Spring Security кладёт `SecurityContext` (с `Authentication` пользователя) в сессию как обычный атрибут. Раз сессия теперь во внешнем сторе, авторизованное состояние автоматически становится общим для всего кластера — пользователь остаётся залогинен на любом узле. А благодаря индексу по `PRINCIPAL_NAME` репозиторий `FindByIndexNameSessionRepository` умеет найти все сессии конкретного пользователя (см. Q5) — это основа для «выйти со всех устройств» и concurrency control.

## Q5. Что такое FindByIndexNameSessionRepository и зачем он нужен?

`FindByIndexNameSessionRepository` — расширение базового `SessionRepository`, которое умеет искать сессии не только по ID, но и по индексу (чаще всего — по имени пользователя). Это даёт возможность найти **все** сессии конкретного человека.

**Зачем это нужно.** Базовый репозиторий знает сессию только по её `sessionId`, а это бесполезно для задач уровня пользователя: «показать список активных устройств», «принудительно завершить все сессии при смене пароля», ограничить число одновременных входов. Индекс по `PRINCIPAL_NAME` решает именно эти кейсы.

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

**Подводный камень.** Поиск по индексу работает только при `@EnableRedisIndexedHttpSession` — обычный `@EnableRedisHttpSession` индекс не строит, и `findByPrincipalName` вернёт пусто. Нативно эту индексацию поддерживает Redis (через дополнительные индекс-ключи); это плата за функцию — больше записей в стор на каждую сессию.

## Q6. Как обрабатывать события сессий?

Spring Session публикует события жизненного цикла сессии как обычные Spring-события, поэтому ловить их можно через `@EventListener`. Три ключевых события: `SessionCreatedEvent` (создана), `SessionDeletedEvent` (явно удалена, например при logout) и `SessionExpiredEvent` (истекла по таймауту). Это удобные точки для аудита, освобождения связанных ресурсов или уведомления пользователя.

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

**Важный нюанс для Redis.** Истёкший ключ в Redis удаляется молча — само по себе приложение об этом не узнает. Чтобы получать `SessionExpiredEvent` и `SessionDeletedEvent`, нужно включить keyspace notifications: `notify-keyspace-events Eg` (события по истечению `E` и обобщённые `g`). Без этой настройки эти события просто не придут.

**Второй нюанс — тип репозитория.** События публикует только indexed-репозиторий: с `@EnableRedisIndexedHttpSession` (`RedisIndexedSessionRepository`) listener получает все три события, а с обычным `@EnableRedisHttpSession` (`RedisSessionRepository`) события не публикуются вообще — listener молчит без какой-либо ошибки. Если события нужны, включайте indexed-вариант.

## Q7. Как настроить timeout сессии?

Timeout — это период бездействия (max inactive interval), после которого сессия истекает. Настраивается на трёх уровнях: глобально в конфиге, программно для отдельной сессии и динамически по условию (например, по роли пользователя).

**Глобально** — одно свойство для всех сессий:

```yaml
# Глобальный timeout для всех сессий
spring:
  session:
    timeout: 30m   # 30 минут (ISO 8601 duration)
```

**Программно и по условию** — через `setMaxInactiveInterval`. Внизу пример: при создании сессии админам выдаётся увеличенный таймаут. Обратите внимание — после изменения сессию нужно явно сохранить (`save`), иначе новое значение не попадёт в стор:

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
    SecurityContext context = (SecurityContext) session.getAttribute(
        HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY);
    Authentication auth = context != null ? context.getAuthentication() : null;

    if (auth != null && auth.getAuthorities().stream()
            .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"))) {
        session.setMaxInactiveInterval(Duration.ofHours(8));
        sessionRepository.save(session);
    }
}
```

## Q8. Как реализовать кастомную сериализацию сессии?

Атрибуты сессии нужно сериализовать, чтобы положить в Redis. По умолчанию используется Java serialization, но в production обычно переходят на JSON.

**Почему не Java serialization:** она хрупкая (несовместимости при смене `serialVersionUID`/структуры классов), непрозрачная при отладке (бинарь не прочитать глазами) и потенциально небезопасная при десериализации недоверенных данных. JSON через `GenericJackson2JsonRedisSerializer` читаем, переносим между версиями и языками.

Переопределить сериализатор можно бином с именем `springSessionDefaultRedisSerializer`:

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

**Подводный камень.** Старые сессии в Redis были записаны прежним сериализатором — после переключения новый код не сможет их прочитать, и активные пользователи разлогинятся. Нужна стратегия миграции: rolling deploy в сочетании с версионированием ключей (новый namespace/префикс), либо плановое обслуживание с принудительным повторным входом.

## Q9. Как использовать Spring Session с WebFlux (реактивный стек)?

В реактивном стеке нет блокирующего `HttpSession` — его роль играет `WebSession`, а конфигурация включается аннотацией `@EnableRedisWebSession` (вместо `@EnableRedisHttpSession` из MVC). Зависимость та же — `spring-session-data-redis`.

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

Работа с `WebSession` тоже неблокирующая: методы возвращают `Mono`/`Publisher`, поэтому доступ к сессии встраивается в реактивную цепочку, а не блокирует поток.

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

**Итог:** `WebSession` — реактивный аналог `HttpSession`, а интеграция со стором подхватывается автоматически при наличии зависимости и аннотации. Прикладной код не вызывает блокирующих методов и остаётся полностью реактивным.

## Q10. Как Spring Session работает в кластере?

Суть: состояние сессии вынесено в общий стор, поэтому каждый узел при запросе подгружает её по ID из cookie и пишет обратно — узлы остаются stateless и взаимозаменяемыми.

```text
Browser ─── LB ─┬─ App1 ─┐
                │          ├── Redis Session Store
                └─ App2 ─┘
```

**Путь запроса:**

1. Запрос от браузера приходит с `SESSION` cookie.
2. Балансировщик маршрутизирует на любой узел (round-robin).
3. Spring Session Filter перехватывает запрос.
4. Загружает сессию из Redis по ID из cookie.
5. Делает сессию доступной через `HttpSession`.
6. После обработки сохраняет изменённую сессию обратно в Redis.

**Главное следствие — нет sticky sessions.** Балансировщик не обязан закреплять клиента за узлом: любой узел обработает любой запрос. Это упрощает масштабирование (узлы можно добавлять/убирать свободно) и отказоустойчивость (падение узла не теряет сессии — они в Redis).

## Q11. Как мигрировать с HTTP Session на Spring Session Redis?

Технически миграция тривиальна — добавить зависимость и конфигурацию. Вся сложность в одном: старые in-memory сессии в Redis не попадут, поэтому при деплое активные пользователи рискуют потерять сессию. Стратегия миграции — это в первую очередь план, как этого избежать.

**Варианты подхода** (в коде ниже — комментариями; «решение 2» — лишь набросок, см. оговорку после кода):

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

**Честная оговорка про «двойное хранилище».** Объявленный выше бин `MapSessionRepository` сам по себе ничего не мигрирует: это просто второй стор без какой-либо маршрутизации между старым и новым — Spring не станет автоматически читать «сначала тут, потом там». Рабочий миграционный паттерн выглядит иначе: либо **дренаж старого стора** (новые сессии сразу пишутся в Redis, старый источник живёт read-only с коротким TTL, пока активные сессии в нём естественно не истекут), либо **dual-write на уровне кастомного `SessionRepository`** — обёртки, которая читает из Redis с фоллбэком в старый стор и пишет в оба. Второй путь заметно дороже в реализации и оправдан только при жёстком требовании «никого не разлогинивать».

**На практике** выбирают один из двух сценариев: плановое обслуживание с принудительным logout всех пользователей (просто и предсказуемо) либо rolling restart с заметным сообщением о необходимости повторного входа (без простоя, но часть пользователей разлогинится в момент перезапуска их узла).

## Q12. Что такое Session Concurrency Control?

**Concurrency Control** — ограничение числа одновременных активных сессий одного пользователя (например, «не больше 3 устройств»). Типичные применения: защита аккаунта, лицензионные ограничения, запрет на одновременный вход с разных мест.

При срабатывании лимита есть два поведения, задаваемых `maxSessionsPreventsLogin`: либо завершить самую старую сессию и пустить новый вход (`false`), либо заблокировать новый логин до закрытия одной из существующих сессий (`true`).

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

**Ключевой момент для кластера.** Стандартный `SessionRegistry` хранит данные о сессиях в памяти одного узла, поэтому в кластере лимит «протекает»: разные узлы не видят сессий друг друга. `SpringSessionBackedSessionRegistry` решает это — он реализует `SessionRegistry` поверх Spring Session, считая активные сессии прямо из общего стора через `FindByIndexNameSessionRepository`. Поэтому в кластере concurrency control работает корректно только с ним.

## Q13. Чем Spring Session отличается от Tomcat Session Manager?

Коротко: Tomcat Session Manager привязывает сессию к памяти конкретного узла и контейнера, а Spring Session выносит её в общий внешний стор и не зависит от контейнера. Отсюда вытекают и остальные различия:

| Критерий | Tomcat Session Manager | Spring Session |
|----------|------------------------|----------------|
| Хранилище | Память Tomcat | Redis/JDBC/MongoDB |
| Кластер | Sticky sessions / Replication | Любой узел |
| Контейнер | Tomcat-зависимо | Любой (Tomcat/Jetty/Undertow) |
| Реактивный стек | Нет | Есть (WebSession) |
| Find-by-user | Нет | Через FindByIndexNameSessionRepository |
| Наблюдаемость | Ограничена | Actuator `/sessions` endpoint |

**Вывод:** Spring Session предпочтительнее в облачных и контейнерных средах, где инстансы эфемерны — их в любой момент пересоздают, масштабируют и перезапускают, а сессии в общем сторе это переживают без потерь.

## Q14. Как настроить мониторинг сессий через Actuator?

Главный инструмент — endpoint `/actuator/sessions` для просмотра и удаления сессий конкретного пользователя. Он требует, чтобы под капотом был `FindByIndexNameSessionRepository` (поиск сессий по пользователю), и его нужно явно открыть в `exposure.include`.

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

**Метрики.** Готовых Micrometer-метрик у Spring Session out-of-the-box **нет** — а привычные `tomcat.sessions.*` при включённом Spring Session как раз перестают что-либо показывать (сессии контейнера больше не используются). Число активных сессий мониторят самостоятельно: кастомный gauge по количеству ключей в namespace Redis (подсчёт по префиксу через `SCAN`) или `COUNT(*)` по таблице `SPRING_SESSION` для JDBC. На такой gauge вешают алерт: резкий рост может сигналить об утечке сессий или о DDoS, резкое падение — о проблеме со стором.

## Q15. Когда не стоит использовать Spring Session?

Spring Session решает проблему общего серверного состояния в кластере — если этой проблемы нет или вы сознательно делаете систему stateless, он добавляет лишнюю сложность и сетевые накладные расходы без выгоды.

**Когда он избыточен:**

1. **Stateless JWT-аутентификация** — всё состояние пользователя несёт сам токен, серверное хранилище сессий не нужно. Spring Session здесь лишний.

2. **Одноузловые приложения без HA** — раз узел один, «потерять сессию на другом узле» невозможно: стандартных in-memory HTTP-сессий достаточно.

3. **Высоконагруженные read-heavy сценарии** — каждый запрос читает (и часто пишет) сессию из стора; при большом RPM это лишний сетевой round-trip и нагрузка на Redis/БД на горячем пути.

4. **Микросервисы без общей сессии** — на API Gateway состояние логина (OAuth2/OpenID Connect) уместнее держать на Authorization Server, а сами сервисы делать stateless.

**Альтернатива:** Spring Security + JWT + `SessionCreationPolicy.STATELESS` — сервер не хранит сессию вообще, состояние едет в токене.

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
