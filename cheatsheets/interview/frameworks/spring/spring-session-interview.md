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

## Q15. Когда не стоит использовать Spring Session?

1. **Stateless JWT-аутентификация** — токены не требуют серверного хранения. Spring Session избыточен.

2. **Одноузловые приложения без HA** — стандартных HTTP сессий достаточно.

3. **Высоконагруженные read-heavy сценарии** — каждый запрос читает сессию из Redis; при большом числе RPM это дополнительная нагрузка.

4. **Микросервисы без общей сессии** — сессии API Gateway (OAuth2/OpenID Connect) лучше управлять через Authorization Server.

**Альтернатива**: Spring Security + JWT + stateless `SessionCreationPolicy.STATELESS`.

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
