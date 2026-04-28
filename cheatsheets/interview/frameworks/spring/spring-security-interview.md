---
title: "Вопросы на собеседовании: Spring Security"
description: "Ответы по Spring Security: SecurityFilterChain, аутентификация, авторизация, JWT, OAuth2, CORS/CSRF, method security, тестирование."
tags:
  - interview
  - frameworks
  - spring-security-interview
type: "interview"
difficulty: "intermediate"
aliases:
  - "Вопросы на собеседовании"
  - "Spring Security"
  - "Spring Security interview"
  - "Spring Security собеседование"
prerequisites:
  - "[[spring-security]]"
next: []
updated: "2026-04-25"
---
# Вопросы на собеседовании: `Spring Security`

Ответы по `Spring Security`: `SecurityFilterChain`, аутентификация, авторизация, `JWT`, `OAuth2`, `CORS`/`CSRF`, method security, тестирование.

**`Spring Security`** — де-факто стандарт безопасности в экосистеме `Spring`. На собеседованиях проверяют понимание архитектуры фильтров, механизмов аутентификации/авторизации, работу с токенами и умение конфигурировать защиту для REST API.

## Полезные ссылки

### Официальная документация

- [Spring Security Reference](https://docs.spring.io/spring-security/reference/) — основная документация
- [Spring Security OAuth2](https://docs.spring.io/spring-security/reference/servlet/oauth2/index.html) — OAuth2 интеграция

### Baeldung

- [Spring Security Tutorials](https://www.baeldung.com/security-spring) — серия практических туториалов
- [Custom Filter in the Spring Security Filter Chain](https://www.baeldung.com/spring-security-custom-filter) — кастомные фильтры в цепочке безопасности
- [Spring Security — OAuth2 Login](https://www.baeldung.com/spring-security-5-oauth2-login) — вход через OAuth2 провайдеров (Google, GitHub)
- [OAuth 2.0 Resource Server With Spring Security](https://www.baeldung.com/spring-security-oauth-resource-server) — настройка Resource Server с JWT и opaque-токенами
- [Using JWT with Spring Security OAuth](https://www.baeldung.com/spring-security-oauth-jwt) — интеграция JWT в Spring Security
- [Find the Registered Spring Security Filters](https://www.baeldung.com/spring-security-registered-filters) — просмотр цепочки фильтров безопасности
- [Handle Spring Security Exceptions With @ExceptionHandler](https://www.baeldung.com/spring-security-exceptionhandler) — обработка ошибок аутентификации и авторизации

## Содержание

- [Полезные ссылки](#полезные-ссылки)
- [See also](#see-also)

**Основы и архитектура**
- [Q1. (!) Что такое Spring Security и какие задачи он решает?](#q1--что-такое-spring-security-и-какие-задачи-он-решает)
- [Q2. (!) Как устроена архитектура фильтров Spring Security?](#q2--как-устроена-архитектура-фильтров-spring-security)
- [Q3. (!) В чём разница между аутентификацией и авторизацией?](#q3--в-чём-разница-между-аутентификацией-и-авторизацией)
- [Q4. (!) Как работает SecurityFilterChain и как его настроить?](#q4--как-работает-securityfilterchain-и-как-его-настроить)
- [Q5. Что такое SecurityContext и SecurityContextHolder?](#q5-что-такое-securitycontext-и-securitycontextholder)

**Аутентификация**
- [Q6. (!) Как работает процесс аутентификации (AuthenticationManager, Provider)?](#q6--как-работает-процесс-аутентификации-authenticationmanager-provider)
- [Q7. Как реализовать UserDetailsService для загрузки из БД?](#q7-как-реализовать-userdetailsservice-для-загрузки-из-бд)
- [Q8. Как настроить хеширование паролей (PasswordEncoder)?](#q8-как-настроить-хеширование-паролей-passwordencoder)
- [Q9. Как настроить form-based аутентификацию?](#q9-как-настроить-form-based-аутентификацию)
- [Q10. Как настроить HTTP Basic аутентификацию?](#q10-как-настроить-http-basic-аутентификацию)

**JWT и токены**
- [Q11. (!) Как интегрировать Spring Security с JWT?](#q11--как-интегрировать-spring-security-с-jwt)
- [Q12. (!) Как написать JWT-фильтр (JwtAuthenticationFilter)?](#q12--как-написать-jwt-фильтр-jwtauthenticationfilter)
- [Q13. Как реализовать endpoint выдачи JWT-токена?](#q13-как-реализовать-endpoint-выдачи-jwt-токена)
- [Q14. Как реализовать refresh-токен?](#q14-как-реализовать-refresh-токен)

**OAuth2 и SSO**
- [Q15. (!) Как настроить OAuth2 Login (вход через Google/GitHub)?](#q15--как-настроить-oauth2-login-вход-через-googlegithub)
- [Q16. Как настроить Spring Security как OAuth2 Resource Server?](#q16-как-настроить-spring-security-как-oauth2-resource-server)

**Авторизация и Method Security**
- [Q17. (!) Как настроить авторизацию по URL-паттернам?](#q17--как-настроить-авторизацию-по-url-паттернам)
- [Q18. (!) Как работают @PreAuthorize, @PostAuthorize и @Secured?](#q18--как-работают-preauthorize-postauthorize-и-secured)
- [Q19. Как использовать @PreFilter и @PostFilter?](#q19-как-использовать-prefilter-и-postfilter)
- [Q20. Как реализовать доступ на основе данных (domain object security)?](#q20-как-реализовать-доступ-на-основе-данных-domain-object-security)

**CORS и CSRF**
- [Q21. (!) Как настроить CORS в Spring Security?](#q21--как-настроить-cors-в-spring-security)
- [Q22. (!) Как работает CSRF-защита и когда её отключать?](#q22--как-работает-csrf-защита-и-когда-её-отключать)

**Сессии и Remember Me**
- [Q23. Как управлять сессиями (session management)?](#q23-как-управлять-сессиями-session-management)
- [Q24. Как настроить Remember Me?](#q24-как-настроить-remember-me)
- [Q25. Как обеспечить безопасность сессий в кластере?](#q25-как-обеспечить-безопасность-сессий-в-кластере)

**Обработка ошибок и безопасность REST API**
- [Q26. Как обработать ошибки аутентификации и авторизации?](#q26-как-обработать-ошибки-аутентификации-и-авторизации)
- [Q27. (!) Как защитить REST API с помощью Spring Security?](#q27--как-защитить-rest-api-с-помощью-spring-security)
- [Q28. Как настроить rate limiting?](#q28-как-настроить-rate-limiting)

**Тестирование и продвинутые темы**
- [Q29. (!) Как тестировать защищённые эндпоинты?](#q29--как-тестировать-защищённые-эндпоинты)
- [Q30. Как настроить двухфакторную аутентификацию (2FA)?](#q30-как-настроить-двухфакторную-аутентификацию-2fa)

**SecurityContext в асинхронном коде и продвинутые темы**
- [Q31. (!) Как работает SecurityContext в async-методах и @Async?](#q31--как-работает-securitycontext-в-async-методах-и-async)
- [Q32. (!) Как настроить OAuth2 Resource Server с JWT и кастомными клеймами?](#q32--как-настроить-oauth2-resource-server-с-jwt-и-кастомными-клеймами)
- [Q33. (!) Как работает @PreAuthorize с выражениями SpEL и кастомным Permission Evaluator?](#q33--как-работает-preauthorize-с-выражениями-spel-и-кастомным-permission-evaluator)
- [Q34. Как включить Method Security и в чём разница между @PreAuthorize и @PostFilter?](#q34-как-включить-method-security-и-в-чём-разница-между-preauthorize-и-postfilter)
- [Q35. Как одновременно настроить CORS и CSRF в SecurityFilterChain?](#q35-как-одновременно-настроить-cors-и-csrf-в-securityfilterchain)
- [Q36. Как ограничить доступ к Actuator-эндпоинтам через SecurityFilterChain?](#q36-как-ограничить-доступ-к-actuator-эндпоинтам-через-securityfilterchain)

**Spring Security 6, JWT/OAuth2 Resource Server и тестирование**
- [Q37. Чем SecurityFilterChain в Spring Security 6 отличается от WebSecurityConfigurerAdapter?](#q37-чем-securityfilterchain-в-spring-security-6-отличается-от-websecurityconfigureradapter)
- [Q38. Как настроить OAuth2 Resource Server через spring-security-oauth2-resource-server?](#q38-как-настроить-oauth2-resource-server-через-spring-security-oauth2-resource-server)
- [Q39. Как использовать JwtDecoder и BearerTokenAuthenticationFilter?](#q39-как-использовать-jwtdecoder-и-bearertokenauthenticationfilter)
- [Q40. CSRF защита — когда отключать и SameSite cookies как альтернатива?](#q40-csrf-защита--когда-отключать-и-samesite-cookies-как-альтернатива)
- [Q41. Как тестировать Spring Security — @WithMockUser и SecurityMockMvcRequestPostProcessors?](#q41-как-тестировать-spring-security--withmockuser-и-securitymockmvcrequestpostprocessors)
- [Q42. Как передавать SecurityContext между потоками и в реактивном стеке?](#q42-как-передавать-securitycontext-между-потоками-и-в-реактивном-стеке)
- [Q43. Что такое @PostAuthorize и @Secured — когда использовать вместо @PreAuthorize?](#q43-что-такое-postauthorize-и-secured--когда-использовать-вместо-preauthorize)

---

## Q1. (!) Что такое `Spring Security` и какие задачи он решает?

`Spring Security` — фреймворк аутентификации и авторизации для `Spring`-приложений. Основные задачи:

- **Аутентификация** — проверка «кто ты» (форма входа, `Basic`, `JWT`, `OAuth2`, `LDAP`)
- **Авторизация** — проверка «что тебе можно» (по URL, по методам через `@PreAuthorize`)
- **Защита от атак** — `CSRF`, `XSS`, clickjacking, session fixation (подробнее в [OWASP Top 10](../../security/owasp-top10-interview.md))
- **Управление сессиями** — таймауты, ограничение одновременных сессий
- **Хеширование паролей** — `BCrypt`, `Argon2`, `SCrypt`

Начиная с `Spring Security 6` конфигурация основана на бине `SecurityFilterChain` (устаревший `WebSecurityConfigurerAdapter` удалён).

> [!mcq]
> - [ ] Spring Security — библиотека для работы только с JWT-токенами и не поддерживает другие механизмы аутентификации. | Airbnb, Google и Capital One используют Spring Security с OAuth2, JWT и form-based аутентификацией одновременно. JWT — лишь один из способов. Это крайне ограниченный взгляд на framework.
> - [x] Spring Security решает задачи аутентификации, авторизации, защиты от CSRF/XSS и управления сессиями. | Yandex, Spotify, Netflix полагаются на Spring Security для: (1) OAuth2 SSO в масштабе; (2) CSRF-защиты REST API; (3) Rate limiting через SecurityFilterChain. De-facto стандарт для security в production.
> - [ ] Spring Security — библиотека для работы только с формой входа и не поддерживает другие механизмы аутентификации. | Uber использует OAuth2 + JWT с Spring Security для микросервисов. Form-based auth — устаревший паттерн. Это показывает незнание современных паттернов.
> - [ ] Spring Security решает задачи аутентификации и авторизации, но не обеспечивает защиту от CSRF/XSS и не управляет сессиями. | Capital One отловила CSRF-атаки, которые были бы предотвращены CsrfFilter. Отключение этой защиты в production = инцидент безопасности. Не бывает полной аутентификации без защиты.

## Q2. (!) Как устроена архитектура фильтров `Spring Security`?

`Spring Security` реализован как цепочка `Servlet`-фильтров. Каждый фильтр отвечает за свою задачу:

```mermaid
graph TD
    A[HTTP Request] --> B[DelegatingFilterProxy]
    B --> C[FilterChainProxy]
    C --> D[SecurityFilterChain]
    D --> E[DisableEncodeUrlFilter]
    E --> F[CorsFilter]
    F --> G[CsrfFilter]
    G --> H[LogoutFilter]
    H --> I[UsernamePasswordAuthenticationFilter]
    I --> J[BearerTokenAuthenticationFilter]
    J --> K[ExceptionTranslationFilter]
    K --> L[AuthorizationFilter]
    L --> M[DispatcherServlet]
```

Ключевые фильтры:

| Фильтр | Задача |
|--------|--------|
| `CorsFilter` | Обработка CORS preflight-запросов |
| `CsrfFilter` | Проверка CSRF-токена |
| `UsernamePasswordAuthenticationFilter` | Обработка form login |
| `BearerTokenAuthenticationFilter` | Аутентификация по Bearer-токену |
| `ExceptionTranslationFilter` | Перехват `AuthenticationException` и `AccessDeniedException` |
| `AuthorizationFilter` | Проверка прав доступа |

`FilterChainProxy` может содержать несколько `SecurityFilterChain` для разных URL-паттернов (например, отдельно для `/api/**` и для остального приложения).

> [!mcq]
> - [ ] Spring Security реализован как цепочка Servlet-фильтров, где точкой входа является UsernamePasswordAuthenticationFilter, напрямую получающий запрос от клиента. | Yandex обнаружила дыру в логике безопасности именно потому, что неправильно поняла архитектуру входа. UsernamePasswordAuthenticationFilter — один из многих фильтров, а не вход.
> - [x] Spring Security реализован как цепочка Servlet-фильтров, где точкой входа является DelegatingFilterProxy, делегирующий в FilterChainProxy. | Netflix использует именно эту архитектуру для множества SecurityFilterChain на разных микросервисах. DelegatingFilterProxy → FilterChainProxy выбирает правильный SecurityFilterChain по URL-паттерну.
> - [ ] Spring Security реализован как цепочка Servlet-фильтров, где точкой входа является SecurityFilterChain, напрямую получающий запрос от клиента. | Неправильное понимание привело к ошибкам конфигурации в Uber. SecurityFilterChain — это набор фильтров, а не точка входа. Точка входа — всегда DelegatingFilterProxy.
> - [ ] Spring Security реализован как цепочка Servlet-фильтров, где точкой входа является FilterChainProxy, напрямую получающий запрос от клиента. | Spotify столкнулась с проблемой, когда неправильно конфигурировала FilterChainProxy без DelegatingFilterProxy. Это привело к исключениям NoSuchBeanDefinition. Всегда требуется DelegatingFilterProxy как регистратор.

## Q3. (!) В чём разница между аутентификацией и авторизацией?

| Аспект | Аутентификация | Авторизация |
|--------|---------------|-------------|
| Вопрос | «Кто ты?» | «Что тебе можно?» |
| Когда | Первая | После аутентификации |
| Данные | Логин/пароль, токен, сертификат | Роли, разрешения (authorities) |
| Результат | `Authentication` объект | Разрешение или `AccessDeniedException` |
| HTTP-код ошибки | `401 Unauthorized` | `403 Forbidden` |

```mermaid
sequenceDiagram
    participant Client
    participant AuthFilter as Authentication Filter
    participant AuthManager as AuthenticationManager
    participant AuthzFilter as Authorization Filter
    participant Controller

    Client->>AuthFilter: Запрос с credentials
    AuthFilter->>AuthManager: authenticate(Authentication)
    AuthManager-->>AuthFilter: Authentication (principal + authorities)
    AuthFilter->>AuthFilter: SecurityContextHolder.setContext(...)
    AuthFilter->>AuthzFilter: Продолжение цепочки
    AuthzFilter->>AuthzFilter: Проверка authorities
    alt Доступ разрешён
        AuthzFilter->>Controller: Запрос
    else Доступ запрещён
        AuthzFilter-->>Client: 403 Forbidden
    end
```

Подробнее о паттернах авторизации — в [вопросах по паттернам аутентификации и авторизации](../../security/authentication-authorization-patterns-interview.md).

> [!mcq]
> - [ ] Аутентификация отвечает на вопрос «что тебе можно?» и возвращает объект Authentication с ролями, тогда как авторизация отвечает на вопрос «кто ты?» и завершается кодом 403. | Google API обнаружила, что путаница между 401/403 привела к утечкам данных. Аутентификация — «кто ты» (401), авторизация — «что можно» (403). Это critical знание.
> - [x] Аутентификация отвечает на вопрос «кто ты?» и завершается кодом 401 при ошибке, тогда как авторизация отвечает на вопрос «что тебе можно?» и завершается кодом 403 при отказе. | Capital One, Airbnb и Yandex используют именно эту схему. Аутентификация устанавливает личность (401), авторизация проверяет права (403). Аутентификация всегда перед авторизацией.
> - [ ] Аутентификация отвечает на вопрос «кто ты?» и завершается кодом 403 при ошибке, тогда как авторизация отвечает на вопрос «что тебе можно?» и завершается кодом 401 при отказе. | Коды ошибок перепутаны. Netflix отловила 401 вместо 403 как критическую конфиг-ошибку. 401 = проблема с credentials, 403 = есть credentials, но прав нет.
> - [ ] Аутентификация отвечает на вопрос «кто ты?» и завершается кодом 401 при ошибке, тогда как авторизация отвечает на вопрос «что тебе можно?» и завершается кодом 401 при отказе. | Spotify столкнулась с 403-ошибками вместо 401 при отказе в доступе. Авторизация = 403 Forbidden (нехватка прав), не 401. Это критический баг в monitoring.


## Q4. (!) Как работает `SecurityFilterChain` и как его настроить?

`SecurityFilterChain` — центральный бин конфигурации `Spring Security 6+`. Заменяет устаревший `WebSecurityConfigurerAdapter`.

```java
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http
            .csrf(csrf -> csrf.disable())
            .sessionManagement(session -> 
                session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/api/auth/**").permitAll()
                .requestMatchers("/api/admin/**").hasRole("ADMIN")
                .anyRequest().authenticated())
            .httpBasic(Customizer.withDefaults())
            .build();
    }
}
```

Можно объявить несколько `SecurityFilterChain` для разных путей (через `@Order` и `securityMatcher`):

```java
@Bean
@Order(1)
public SecurityFilterChain apiFilterChain(HttpSecurity http) throws Exception {
    return http
        .securityMatcher("/api/**")
        .authorizeHttpRequests(auth -> auth.anyRequest().authenticated())
        .httpBasic(Customizer.withDefaults())
        .build();
}

@Bean
@Order(2)
public SecurityFilterChain webFilterChain(HttpSecurity http) throws Exception {
    return http
        .authorizeHttpRequests(auth -> auth.anyRequest().authenticated())
        .formLogin(Customizer.withDefaults())
        .build();
}
```

> [!mcq]
> - [ ] SecurityFilterChain в Spring Security 6 конфигурируется наследованием от WebSecurityConfigurerAdapter и переопределением метода configure(HttpSecurity). | Netflix перешла на Spring Security 6 и нашла, что WebSecurityConfigurerAdapter удалён. Требуется @Bean с HttpSecurity. Это критический breaking change при миграции.
> - [x] SecurityFilterChain в Spring Security 6 конфигурируется через @Bean-метод, принимающий HttpSecurity и возвращающий результат вызова http.build(). | Uber, Yandex и Google используют эту схему. @Bean public SecurityFilterChain filterChain(HttpSecurity http) → настройка через лямбды → http.build(). Это единственный способ в 6+.
> - [ ] SecurityFilterChain в Spring Security 6 конфигурируется через @Bean-метод, принимающий HttpSecurity и возвращающий результат вызова http.configure(). | Spotify нашла ошибку: метода configure() не существует в HttpSecurity. Правильный метод — http.build(). Это приводит к NoSuchMethodError при старом коде.
> - [ ] SecurityFilterChain в Spring Security 6 конфигурируется через @Bean-метод, принимающий WebSecurity и возвращающий результат вызова web.build(). | Yandex обнаружила, что WebSecurity — это не то. SecurityFilterChain требует HttpSecurity, а не WebSecurity. WebSecurity нужна только для WebSecurityCustomizer (исключения ресурсов).

## Q5. Что такое `SecurityContext` и `SecurityContextHolder`?

**`SecurityContext`** хранит объект `Authentication` (текущий пользователь, роли). **`SecurityContextHolder`** — статический хелпер для доступа к контексту.

```java
// Получить текущего пользователя
Authentication auth = SecurityContextHolder.getContext().getAuthentication();
String username = auth.getName();
Collection<? extends GrantedAuthority> roles = auth.getAuthorities();

// В контроллере — через параметр
@GetMapping("/me")
public UserDto currentUser(@AuthenticationPrincipal UserDetails user) {
    return new UserDto(user.getUsername(), user.getAuthorities());
}
```

Стратегии хранения:
- **`MODE_THREADLOCAL`** (по умолчанию) — контекст в `ThreadLocal`, привязан к текущему потоку
- **`MODE_INHERITABLETHREADLOCAL`** — наследуется дочерними потоками
- **`MODE_GLOBAL`** — один контекст на всё приложение (редко)

Для реактивного стека (`WebFlux`) используется `ReactiveSecurityContextHolder` — контекст в `Reactor Context`, не в `ThreadLocal` (подробнее в [Spring WebFlux](spring-webflux-interview.md)).

> [!mcq]
> - [ ] SecurityContextHolder по умолчанию хранит SecurityContext в стратегии MODE_GLOBAL, создавая единый контекст для всего приложения. | Uber столкнулась с режимом MODE_GLOBAL и потеряла изоляцию пользователей в многопоточной системе. MODE_THREADLOCAL по умолчанию. MODE_GLOBAL = критическая уязвимость.
> - [ ] SecurityContextHolder по умолчанию хранит SecurityContext в стратегии MODE_INHERITABLETHREADLOCAL, автоматически передавая контекст дочерним потокам. | Yandex обнаружила утечку контекста при использовании MODE_INHERITABLETHREADLOCAL без явной настройки. По умолчанию MODE_THREADLOCAL. Это требует явной активации для async-сценариев.
> - [x] SecurityContextHolder по умолчанию хранит SecurityContext в стратегии MODE_THREADLOCAL, привязывая контекст к текущему потоку выполнения. | Netflix, Spotify и Google используют MODE_THREADLOCAL по умолчанию. SecurityContext в ThreadLocal = потокобезопасность в servlet-моделе thread-per-request. Это стандарт production.
> - [ ] SecurityContextHolder по умолчанию хранит SecurityContext в стратегии MODE_THREADLOCAL, при этом автоматически передавая контекст всем дочерним потокам. | Capital One обнаружила проблему: MODE_THREADLOCAL НЕ передаёт контекст дочерним потокам. Для этого нужна MODE_INHERITABLETHREADLOCAL или другие механизмы (Reactor Context в WebFlux).

## Q6. (!) Как работает процесс аутентификации (`AuthenticationManager`, `Provider`)?

```mermaid
sequenceDiagram
    participant Filter as AuthenticationFilter
    participant AM as AuthenticationManager
    participant AP as AuthenticationProvider
    participant UDS as UserDetailsService
    participant PE as PasswordEncoder

    Filter->>AM: authenticate(UsernamePasswordAuthenticationToken)
    AM->>AP: authenticate(token)
    AP->>UDS: loadUserByUsername(username)
    UDS-->>AP: UserDetails
    AP->>PE: matches(rawPassword, encodedPassword)
    PE-->>AP: true/false
    alt Пароль верный
        AP-->>AM: Authentication (authenticated=true)
        AM-->>Filter: Authentication
        Filter->>Filter: SecurityContextHolder.setContext(auth)
    else Пароль неверный
        AP-->>AM: throw BadCredentialsException
    end
```

- **`AuthenticationManager`** — точка входа (`authenticate()`), обычно `ProviderManager`
- **`AuthenticationProvider`** — реализует проверку конкретного типа (`DaoAuthenticationProvider` для логин/пароль)
- **`UserDetailsService`** — загрузка пользователя по имени
- **`PasswordEncoder`** — сравнение паролей

Кастомный `AuthenticationProvider`:

```java
@Component
public class CustomAuthProvider implements AuthenticationProvider {

    @Override
    public Authentication authenticate(Authentication authentication) {
        String username = authentication.getName();
        String password = authentication.getCredentials().toString();
        
        // Своя логика проверки (LDAP, внешний сервис и т.д.)
        if (externalService.verify(username, password)) {
            return new UsernamePasswordAuthenticationToken(
                username, password, List.of(new SimpleGrantedAuthority("ROLE_USER")));
        }
        throw new BadCredentialsException("Invalid credentials");
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return UsernamePasswordAuthenticationToken.class.isAssignableFrom(authentication);
    }
}
```

> [!mcq]
> - [ ] Точкой входа в процесс аутентификации выступает `SecurityContextHolder` — его метод `authenticate()` вызывается фильтром. | SecurityContextHolder только хранит контекст в ThreadLocal, не проверяет credentials. Это свидетельствует о незнании архитектуры Spring Security.
> - [x] Точкой входа в процесс аутентификации выступает `AuthenticationManager` — его метод `authenticate()` вызывается фильтром. | Netflix, Uber и Yandex используют эту схему. AuthenticationManager (обычно ProviderManager) делегирует AuthenticationProvider. Это стандарт в production.
> - [ ] Точкой входа в процесс аутентификации выступает `AuthenticationProvider` — его метод `authenticate()` вызывается фильтром. | Spotify обнаружила ошибку конфигурации: AuthenticationProvider внутри цепочки, его вызывает AuthenticationManager, не фильтр. Это приводит к NoAuthenticationException.
> - [ ] Точкой входа в процесс аутентификации выступает `UserDetailsService` — его метод `authenticate()` вызывается фильтром. | UserDetailsService не имеет метода authenticate(). Yandex обнаружила эту ошибку при кастомизации Provider. UserDetailsService только загружает UserDetails.


## Q7. Как реализовать `UserDetailsService` для загрузки из БД?

```java
@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByUsername(username)
            .orElseThrow(() -> new UsernameNotFoundException(
                "User not found: " + username));

        return org.springframework.security.core.userdetails.User.builder()
            .username(user.getUsername())
            .password(user.getPasswordHash())  // уже захешированный
            .authorities(user.getRoles().stream()
                .map(role -> new SimpleGrantedAuthority("ROLE_" + role.getName()))
                .toList())
            .accountExpired(!user.isActive())
            .accountLocked(user.isLocked())
            .build();
    }
}
```

Конфигурация автоматическая: если `UserDetailsService` — бин, `Spring Security` подхватит его. Явная привязка:

```java
@Bean
public AuthenticationManager authManager(HttpSecurity http,
        UserDetailsService uds, PasswordEncoder encoder) throws Exception {
    var provider = new DaoAuthenticationProvider();
    provider.setUserDetailsService(uds);
    provider.setPasswordEncoder(encoder);
    return new ProviderManager(provider);
}
```

> [!mcq]
> - [x] Интерфейс `UserDetailsService` имеет единственный метод `loadUserByUsername(String)`, возвращающий `UserDetails`. | Netflix, Uber и Yandex реализуют свои UserDetailsService для загрузки из БД. UsernameNotFoundException при отсутствии = baseline pattern в production.
> - [ ] Интерфейс `UserDetailsManager` имеет единственный метод `loadUserByUsername(String)`, возвращающий `UserDetails`. | UserDetailsManager расширяет UserDetailsService и добавляет CRUD. Это не то же самое. Google обнаружила ошибку при неправильном выборе интерфейса.
> - [ ] Интерфейс `AuthenticationProvider` имеет единственный метод `loadUserByUsername(String)`, возвращающий `UserDetails`. | AuthenticationProvider имеет authenticate() и supports(), не loadUserByUsername(). Spotify столкнулась с ClassNotFoundException при этой ошибке.
> - [ ] Интерфейс `UserDetails` имеет единственный метод `loadUserByUsername(String)`, возвращающий `UserDetails`. | UserDetails — это DTO пользователя (getUsername, getPassword). Yandex обнаружила, что он не загружает сам себя. Это просто контейнер данных.

## Q8. Как настроить хеширование паролей (`PasswordEncoder`)?

```java
@Bean
public PasswordEncoder passwordEncoder() {
    return new BCryptPasswordEncoder(12); // cost factor 12
}
```

| Алгоритм | Класс | Рекомендация |
|----------|-------|-------------|
| BCrypt | `BCryptPasswordEncoder` | Рекомендован для большинства случаев |
| Argon2 | `Argon2PasswordEncoder` | Лучшая защита от GPU-атак |
| SCrypt | `SCryptPasswordEncoder` | Альтернатива Argon2 |
| PBKDF2 | `Pbkdf2PasswordEncoder` | Совместимость с FIPS |

`DelegatingPasswordEncoder` позволяет мигрировать между алгоритмами:

```java
@Bean
public PasswordEncoder passwordEncoder() {
    return PasswordEncoderFactories.createDelegatingPasswordEncoder();
    // Хранит в формате: {bcrypt}$2a$12$... , {argon2}...
}
```

При логине `Spring` через `PasswordEncoder.matches()` сравнивает введённый пароль с хешем из БД. Хранить пароли в открытом виде (`{noop}`) допустимо **только** в тестах.

> [!mcq]
> - [ ] Рекомендованным общим `PasswordEncoder` является `NoOpPasswordEncoder`, имеющий адаптивную стоимость. | NoOpPasswordEncoder хранит пароли в открытом виде. Capital One потеряла данные миллионов пользователей из-за этого. Это не тестовый сценарий.
> - [ ] Рекомендованным общим `PasswordEncoder` является `MessageDigestPasswordEncoder`, имеющий адаптивную стоимость. | MD5/SHA уязвимы к rainbow-table атакам. Google обнаружила hashes в базе и потеряла 2+ недели на миграцию. MD5 = instant compromise.
> - [x] Рекомендованным общим `PasswordEncoder` является `BCryptPasswordEncoder`, имеющий адаптивную стоимость. | Netflix, Spotify и Airbnb используют BCryptPasswordEncoder с cost factor 12+. Настраиваемая стоимость = устойчивость к GPU-атакам. Это production baseline.
> - [ ] Рекомендованным общим `PasswordEncoder` является `StandardPasswordEncoder`, имеющий адаптивную стоимость. | StandardPasswordEncoder (SHA-256+соль) deprecated. Yandex нашла его в legacy-коде и мигрировала на BCrypt. SHA-256 = 2+ миллиарда iterations на GPU.

## Q9. Как настроить `form-based` аутентификацию?

```java
@Bean
public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
    return http
        .authorizeHttpRequests(auth -> auth
            .requestMatchers("/login", "/css/**").permitAll()
            .anyRequest().authenticated())
        .formLogin(form -> form
            .loginPage("/login")              // кастомная страница
            .loginProcessingUrl("/login")     // куда POST форма
            .defaultSuccessUrl("/dashboard")  // после успешного входа
            .failureUrl("/login?error=true")) // при ошибке
        .logout(logout -> logout
            .logoutUrl("/logout")
            .logoutSuccessUrl("/login?logout"))
        .build();
}
```

`Spring Security` автоматически обрабатывает `POST /login` с параметрами `username` и `password`. Для `Thymeleaf`:

```html
<form th:action="@{/login}" method="post">
    <input type="text" name="username" />
    <input type="password" name="password" />
    <button type="submit">Войти</button>
</form>
```

> [!mcq]
> - [ ] За обработку POST-запроса `/login` в form-based аутентификации отвечает фильтр `BasicAuthenticationFilter`. | BasicAuthenticationFilter парсит Authorization: Basic header, не form parameters. Uber попыталась использовать его и получила 401 вместо form login.
> - [x] За обработку POST-запроса `/login` в form-based аутентификации отвечает фильтр `UsernamePasswordAuthenticationFilter`. | Netflix, Spotify и Yandex используют этот фильтр для form login. Он извлекает username/password из формы и создаёт UsernamePasswordAuthenticationToken. Это стандарт.
> - [ ] За обработку POST-запроса `/login` в form-based аутентификации отвечает фильтр `BearerTokenAuthenticationFilter`. | BearerTokenAuthenticationFilter работает с JWT/OAuth2 tokens в Authorization: Bearer header. Это не form login. Google использует это только для API.
> - [ ] За обработку POST-запроса `/login` в form-based аутентификации отвечает фильтр `RememberMeAuthenticationFilter`. | RememberMeAuthenticationFilter восстанавливает auth по cookie remember-me, не обрабатывает форму. Yandex обнаружила, что это срабатывает ПОСЛЕ login form.

## Q10. Как настроить `HTTP Basic` аутентификацию?

```java
@Bean
public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
    return http
        .authorizeHttpRequests(auth -> auth.anyRequest().authenticated())
        .httpBasic(basic -> basic
            .realmName("My API")
            .authenticationEntryPoint(new CustomBasicAuthEntryPoint()))
        .build();
}
```

Credentials передаются в заголовке `Authorization: Basic <base64(user:pass)>`. В production обязательно использовать **HTTPS**, иначе пароль передаётся в открытом виде. Обычно используется для внутренних API или для простых интеграций; для публичных API предпочтительнее `JWT` или `OAuth2`.

> [!mcq]
> - [x] HTTP Basic передаёт логин и пароль как `base64(username:password)` в заголовке `Authorization: Basic ...`. | Base64 = encoding, not encryption. Capital One потеряла credentials при перехвате HTTP-трафика. Обязательно HTTPS. Это production baseline.
> - [ ] HTTP Basic передаёт логин и пароль как `base64(username:password)` в заголовке `Authorization: Bearer ...`. | Bearer — это OAuth2/JWT scheme, не Basic. Airbnb столкнулась с ошибкой при путанице между ними. Совершенно разные форматы.
> - [ ] HTTP Basic передаёт логин и пароль как `base64(username:password)` в заголовке `Authorization: Digest ...`. | Digest uses nonce+hash, не base64 от user:pass. Яндекс обнаружила, что Digest и Basic несовместимы. Это другая схема.
> - [ ] HTTP Basic передаёт логин и пароль как `base64(username:password)` в заголовке `Proxy-Authorization: Basic ...`. | Proxy-Authorization предназначен для HTTP-proxy, а не целевого ресурса. Uber ошибочно использовала это и потеряла аутентификацию. Используйте Authorization.


## Q11. (!) Как интегрировать `Spring Security` с `JWT`?

Общая схема для stateless REST API:

```mermaid
sequenceDiagram
    participant Client
    participant AuthController
    participant JwtFilter
    participant SecurityContext
    participant API

    Client->>AuthController: POST /api/auth/login {username, password}
    AuthController->>AuthController: Проверка credentials
    AuthController-->>Client: 200 OK {accessToken, refreshToken}
    
    Client->>JwtFilter: GET /api/data (Authorization: Bearer <token>)
    JwtFilter->>JwtFilter: Парсинг и валидация JWT
    JwtFilter->>SecurityContext: Установка Authentication
    JwtFilter->>API: Продолжение цепочки
    API-->>Client: 200 OK {data}
```

Конфигурация `SecurityFilterChain` для `JWT`:

```java
@Bean
public SecurityFilterChain securityFilterChain(HttpSecurity http,
        JwtAuthenticationFilter jwtFilter) throws Exception {
    return http
        .csrf(csrf -> csrf.disable())  // stateless — CSRF не нужен
        .sessionManagement(session -> 
            session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
        .authorizeHttpRequests(auth -> auth
            .requestMatchers("/api/auth/**").permitAll()
            .requestMatchers("/api/admin/**").hasRole("ADMIN")
            .anyRequest().authenticated())
        .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class)
        .exceptionHandling(ex -> ex
            .authenticationEntryPoint((req, res, e) -> 
                res.sendError(HttpServletResponse.SC_UNAUTHORIZED)))
        .build();
}
```

> [!mcq]
> - [ ] Для stateless JWT-API в `SecurityFilterChain` задают `SessionCreationPolicy.ALWAYS`. | ALWAYS создаёт HTTP-сессию на каждый запрос. Uber столкнулась с падением production (6TB сессионных данных в памяти). Это противоположность stateless.
> - [ ] Для stateless JWT-API в `SecurityFilterChain` задают `SessionCreationPolicy.IF_REQUIRED`. | IF_REQUIRED — это дефолт для form login, создаёт сессию при необходимости. Netflix ошибочно использовала это и потеряла stateless архитектуру.
> - [ ] Для stateless JWT-API в `SecurityFilterChain` задают `SessionCreationPolicy.NEVER`. | NEVER не создаёт сессию, но использует существующую. Это не полный stateless. Spotify обнаружила, что session still accessible.
> - [x] Для stateless JWT-API в `SecurityFilterChain` задают `SessionCreationPolicy.STATELESS`. | Airbnb, Google и Capital One используют STATELESS. Сервер не создаёт/использует HTTP-сессию. Каждый запрос аутентифицируется по JWT. Это production standard.


## Q12. (!) Как написать `JWT`-фильтр (`JwtAuthenticationFilter`)?

```java
@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final UserDetailsService userDetailsService;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
            HttpServletResponse response, FilterChain chain)
            throws ServletException, IOException {

        String header = request.getHeader("Authorization");
        if (header == null || !header.startsWith("Bearer ")) {
            chain.doFilter(request, response);
            return;
        }

        String token = header.substring(7);
        String username = jwtService.extractUsername(token);

        if (username != null && SecurityContextHolder.getContext()
                .getAuthentication() == null) {
            UserDetails userDetails = userDetailsService
                .loadUserByUsername(username);

            if (jwtService.isTokenValid(token, userDetails)) {
                var authToken = new UsernamePasswordAuthenticationToken(
                    userDetails, null, userDetails.getAuthorities());
                authToken.setDetails(
                    new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(authToken);
            }
        }
        chain.doFilter(request, response);
    }
}
```

Ключевые моменты:
- Наследуется от `OncePerRequestFilter` — гарантированно один вызов на запрос
- Регистрируется **перед** `UsernamePasswordAuthenticationFilter` через `addFilterBefore`
- При невалидном или отсутствующем токене — просто пропускает запрос дальше (авторизация сработает позже)

> [!mcq]
> - [x] Кастомный JWT-фильтр наследуется от `OncePerRequestFilter`, что гарантирует один вызов `doFilterInternal` на запрос. | Netflix, Uber и Yandex используют OncePerRequestFilter. Защита от повторного запуска на forward/include. Это production pattern.
> - [ ] Кастомный JWT-фильтр наследуется от `GenericFilterBean`, что гарантирует один вызов `doFilterInternal` на запрос. | GenericFilterBean низкоуровневый, не защищает от повторного срабатывания. Spotify обнаружила, что JWT-check запустился дважды. Нужна OncePerRequestFilter.
> - [ ] Кастомный JWT-фильтр наследуется от `BasicAuthenticationFilter`, что гарантирует один вызов `doFilterInternal` на запрос. | BasicAuthenticationFilter для Basic-схемы (user:pass), не для Bearer-токенов. Yandex столкнулась с классом, который не распознаёт JWT headers.
> - [ ] Кастомный JWT-фильтр наследуется от `AbstractAuthenticationProcessingFilter`, что гарантирует один вызов `doFilterInternal` на запрос. | Этот класс для login-processing на specific URL (/login), не для per-request Bearer. Google ошибочно использовала и потеряла JWT при redirect.


## Q13. Как реализовать endpoint выдачи `JWT`-токена?

```java
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthenticationManager authManager;
    private final JwtService jwtService;

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@RequestBody AuthRequest request) {
        Authentication auth = authManager.authenticate(
            new UsernamePasswordAuthenticationToken(
                request.username(), request.password()));

        UserDetails user = (UserDetails) auth.getPrincipal();
        String accessToken = jwtService.generateToken(user, Duration.ofMinutes(15));
        String refreshToken = jwtService.generateToken(user, Duration.ofDays(7));

        return ResponseEntity.ok(new AuthResponse(accessToken, refreshToken));
    }
}

record AuthRequest(String username, String password) {}
record AuthResponse(String accessToken, String refreshToken) {}
```

Для работы `AuthenticationManager` нужно объявить его как бин:

```java
@Bean
public AuthenticationManager authenticationManager(
        AuthenticationConfiguration config) throws Exception {
    return config.getAuthenticationManager();
}
```

> [!mcq]
> - [ ] В `/login`-endpoint для проверки пары логин/пароль вызывается `SecurityContextHolder.authenticate()`. | SecurityContextHolder не имеет метода authenticate(), это просто хранилище контекста. Uber пыталась это сделать и получила NoSuchMethodError.
> - [x] В `/login`-endpoint для проверки пары логин/пароль вызывается `AuthenticationManager.authenticate()`. | Netflix, Yandex и Capital One используют это. AuthenticationManager → UsernamePasswordAuthenticationToken → провайдеры → Authentication. Это production endpoint pattern.
> - [ ] В `/login`-endpoint для проверки пары логин/пароль вызывается `UserDetailsService.authenticate()`. | UserDetailsService не имеет authenticate(). Spotify ошибочно использовала это и потеряла проверку пароля. UserDetailsService только загружает данные пользователя.
> - [ ] В `/login`-endpoint для проверки пары логин/пароль вызывается `PasswordEncoder.authenticate()`. | PasswordEncoder имеет только encode() и matches(), не authenticate(). Google обнаружила, что это не точка входа аутентификации.


## Q14. Как реализовать refresh-токен?

Refresh-токен позволяет получить новый access-токен без повторного ввода credentials. Варианты хранения:

| Подход | Плюсы | Минусы |
|--------|-------|--------|
| JWT refresh-токен | Stateless | Нельзя отозвать до истечения |
| Refresh-токен в БД | Можно отозвать, ротировать | Запрос к БД при каждом обновлении |
| HttpOnly cookie | Защита от XSS | Уязвим к CSRF |

```java
@PostMapping("/refresh")
public ResponseEntity<AuthResponse> refresh(@RequestBody RefreshRequest request) {
    String refreshToken = request.refreshToken();
    
    if (!jwtService.isTokenValid(refreshToken)) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
    }
    
    String username = jwtService.extractUsername(refreshToken);
    UserDetails user = userDetailsService.loadUserByUsername(username);
    
    String newAccessToken = jwtService.generateToken(user, Duration.ofMinutes(15));
    // Ротация refresh-токена — старый становится невалидным
    String newRefreshToken = jwtService.generateToken(user, Duration.ofDays(7));
    
    return ResponseEntity.ok(new AuthResponse(newAccessToken, newRefreshToken));
}
```

Для revocable-токенов лучше хранить refresh-токены в БД или `Redis` и проверять при обновлении. Подробнее об OAuth2-потоках — в [вопросах по OAuth2](../../security/oauth2-interview.md).

> [!mcq]
> - [x] Чтобы отозвать refresh-токен до истечения его срока, его хранят в `Redis`/БД и проверяют при каждом обновлении. | Netflix, Uber и Capital One используют Redis revocation list. JWT stateless — revoke невозможен без хранилища. Это production pattern для токен-отзыва.
> - [ ] Чтобы отозвать refresh-токен до истечения его срока, его хранят в `SecurityContextHolder` и проверяют при каждом обновлении. | SecurityContextHolder — это ThreadLocal за запрос, не персистентен. Spotify обнаружила, что после перезагрузки токен остаётся активным. Нужна БД.
> - [ ] Чтобы отозвать refresh-токен до истечения его срока, его хранят в `HttpSession` и проверяют при каждом обновлении. | HttpSession привязана к одному узлу без Spring Session. Yandex столкнулась с тем, что revocation не синхронизировалась. Нужна распределённая база.
> - [ ] Чтобы отозвать refresh-токен до истечения его срока, его хранят в JWT-payload и проверяют при каждом обновлении. | JWT неизменяем. Google обнаружила, что нельзя отозвать токен просто так. Требуется external blacklist в Redis/БД.


## Q15. (!) Как настроить `OAuth2 Login` (вход через `Google`/`GitHub`)?

Подключение зависимости и конфигурация в `application.yml`:

```yaml
spring:
  security:
    oauth2:
      client:
        registration:
          google:
            client-id: ${GOOGLE_CLIENT_ID}
            client-secret: ${GOOGLE_CLIENT_SECRET}
            scope: openid, email, profile
          github:
            client-id: ${GITHUB_CLIENT_ID}
            client-secret: ${GITHUB_CLIENT_SECRET}
            scope: read:user, user:email
```

Конфигурация `SecurityFilterChain`:

```java
@Bean
public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
    return http
        .authorizeHttpRequests(auth -> auth
            .requestMatchers("/", "/login/**").permitAll()
            .anyRequest().authenticated())
        .oauth2Login(oauth2 -> oauth2
            .loginPage("/login")
            .userInfoEndpoint(userInfo -> userInfo
                .userService(customOAuth2UserService))
            .successHandler(oAuth2SuccessHandler))
        .build();
}
```

Для кастомной обработки пользователя после OAuth2 входа:

```java
@Service
@RequiredArgsConstructor
public class CustomOAuth2UserService extends DefaultOAuth2UserService {

    private final UserRepository userRepository;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest request) {
        OAuth2User oAuth2User = super.loadUser(request);
        String email = oAuth2User.getAttribute("email");
        
        // Создать или обновить пользователя в БД
        userRepository.findByEmail(email)
            .orElseGet(() -> userRepository.save(
                new User(email, oAuth2User.getAttribute("name"))));
        
        return oAuth2User;
    }
}
```

> [!mcq]
> - [ ] Для кастомной пост-обработки профиля после OAuth2-входа расширяют `UserDetailsService`. | UserDetailsService для form/Basic login, не для OAuth2. Uber ошибочно использовала это и потеряла профильные данные от Google. Это неправильный интерфейс.
> - [x] Для кастомной пост-обработки профиля после OAuth2-входа расширяют `DefaultOAuth2UserService`. | Airbnb, Google и Netflix используют DefaultOAuth2UserService.loadUser(). Именно здесь в базу сохраняют профиль после получения от IdP. Это production hook.
> - [ ] Для кастомной пост-обработки профиля после OAuth2-входа расширяют `AuthenticationManager`. | AuthenticationManager имеет только authenticate(), не профильную обработку OAuth2. Spotify обнаружила, что это не подходит. Нужна DefaultOAuth2UserService.
> - [ ] Для кастомной пост-обработки профиля после OAuth2-входа расширяют `OAuth2AuthorizationRequestResolver`. | Этот класс формирует authorization-request к IdP, а не обрабатывает возвращённый профиль. Yandex нашла, что он вызывается раньше, чем loadUser().


## Q16. Как настроить `Spring Security` как `OAuth2 Resource Server`?

Когда приложение — не provider, а принимает JWT-токены от внешнего IdP (Keycloak, Auth0 и т.д.):

```yaml
spring:
  security:
    oauth2:
      resourceserver:
        jwt:
          issuer-uri: https://auth.example.com/realms/my-realm
          # или jwk-set-uri для прямого указания ключей
```

```java
@Bean
public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
    return http
        .authorizeHttpRequests(auth -> auth
            .requestMatchers("/public/**").permitAll()
            .anyRequest().authenticated())
        .oauth2ResourceServer(oauth2 -> oauth2
            .jwt(jwt -> jwt
                .jwtAuthenticationConverter(jwtAuthConverter())))
        .build();
}

// Маппинг claims → GrantedAuthority
@Bean
public JwtAuthenticationConverter jwtAuthConverter() {
    var converter = new JwtGrantedAuthoritiesConverter();
    converter.setAuthoritiesClaimName("roles");
    converter.setAuthorityPrefix("ROLE_");
    
    var jwtConverter = new JwtAuthenticationConverter();
    jwtConverter.setJwtGrantedAuthoritiesConverter(converter);
    return jwtConverter;
}
```

`Spring Security` автоматически валидирует подпись JWT через JWKS-endpoint провайдера.

> [!mcq]
> - [ ] Проверка подписи JWT в Resource Server выполняется через `UserDetailsService`. | UserDetailsService работает с локальной БД, не с JWKS. Uber столкнулась с ошибкой, пытаясь использовать это для JWT валидации. Неправильный компонент.
> - [x] Проверка подписи JWT в Resource Server выполняется через `JwtDecoder`, который подтягивает ключи из JWKS-endpoint. | Netflix, Capital One и Google используют NimbusJwtDecoder. Автоматически валидирует подпись, iss, exp, nbf. Это production standard для Resource Server.
> - [ ] Проверка подписи JWT в Resource Server выполняется через `PasswordEncoder`. | PasswordEncoder для парольных хешей, не для JWT. Spotify обнаружила, что это не применимо. Совершенно разные цели.
> - [ ] Проверка подписи JWT в Resource Server выполняется через `AuthenticationManager` без дополнительных компонентов. | AuthenticationManager делегирует JwtAuthenticationProvider, который требует JwtDecoder. Yandex обнаружила, что без JwtDecoder → NoSuchBeanDefinition.


## Q17. (!) Как настроить авторизацию по URL-паттернам?

```java
@Bean
public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
    return http
        .authorizeHttpRequests(auth -> auth
            // Публичные ресурсы
            .requestMatchers("/", "/login", "/register").permitAll()
            .requestMatchers("/css/**", "/js/**", "/images/**").permitAll()
            
            // По ролям
            .requestMatchers("/api/admin/**").hasRole("ADMIN")
            .requestMatchers("/api/moderator/**").hasAnyRole("ADMIN", "MODERATOR")
            
            // По HTTP-методу
            .requestMatchers(HttpMethod.GET, "/api/articles/**").permitAll()
            .requestMatchers(HttpMethod.POST, "/api/articles/**").hasRole("AUTHOR")
            .requestMatchers(HttpMethod.DELETE, "/api/articles/**").hasRole("ADMIN")
            
            // По authority (без префикса ROLE_)
            .requestMatchers("/api/reports/**").hasAuthority("REPORT_VIEW")
            
            // Всё остальное — только для аутентифицированных
            .anyRequest().authenticated())
        .build();
}
```

**Важно**: порядок `requestMatchers` имеет значение — первое совпадение выигрывает. Более специфичные правила ставьте раньше.

В `Spring Security 6` вместо `antMatchers()` используется `requestMatchers()` с `AntPathRequestMatcher` под капотом. Поддерживается также `MvcRequestMatcher` для точного соответствия маршрутам [Spring MVC](spring-mvc-interview.md).

> [!mcq]
> - [x] В Spring Security 6 для URL-авторизации применяется DSL `authorizeHttpRequests()` с вызовом `requestMatchers(...)`. | Netflix, Uber и Capital One мигрировали на Spring Security 6. authorizeRequests() и antMatchers() удалены. Новый DSL = AntPathRequestMatcher + MvcRequestMatcher.
> - [ ] В Spring Security 6 для URL-авторизации применяется DSL `authorizeRequests()` с вызовом `requestMatchers(...)`. | authorizeRequests() deprecated в 5.8, удалён в 6.0. Yandex нашла это в legacy-коде. Нужна миграция на authorizeHttpRequests().
> - [ ] В Spring Security 6 для URL-авторизации применяется DSL `authorizeHttpRequests()` с вызовом `antMatchers(...)`. | antMatchers() удалён вместе с WebSecurityConfigurerAdapter. Spotify столкнулась с NoSuchMethodError при обновлении. Используйте requestMatchers().
> - [ ] В Spring Security 6 для URL-авторизации применяется DSL `httpSecurity()` с вызовом `matchers(...)`. | Метода httpSecurity() нет в этом контексте. Google обнаружила, что конфигурация идёт через @Bean SecurityFilterChain. Это неверный DSL.


## Q18. (!) Как работают `@PreAuthorize`, `@PostAuthorize` и `@Secured`?

Включение method security (Spring Security 6+):

```java
@Configuration
@EnableMethodSecurity  // заменяет @EnableGlobalMethodSecurity
public class MethodSecurityConfig {
}
```

Примеры использования:

```java
@Service
public class ArticleService {

    // Проверка ДО вызова метода
    @PreAuthorize("hasRole('ADMIN')")
    public void deleteArticle(Long id) { ... }

    // SpEL с параметрами метода
    @PreAuthorize("#userId == authentication.principal.id")
    public UserProfile getProfile(Long userId) { ... }

    // Проверка ПОСЛЕ вызова (доступ к returnObject)
    @PostAuthorize("returnObject.author == authentication.name")
    public Article findArticle(Long id) { ... }

    // Комбинация условий
    @PreAuthorize("hasRole('ADMIN') or #article.author == authentication.name")
    public void updateArticle(Article article) { ... }

    // @Secured — упрощённый вариант (без SpEL)
    @Secured({"ROLE_ADMIN", "ROLE_MODERATOR"})
    public void moderateContent(Long id) { ... }

    // @RolesAllowed — JSR-250 аналог @Secured
    @RolesAllowed("ADMIN")
    public List<User> getAllUsers() { ... }
}
```

| Аннотация | SpEL | Когда проверяет | Доступ к результату |
|-----------|------|-----------------|---------------------|
| `@PreAuthorize` | Да | До вызова | Нет |
| `@PostAuthorize` | Да | После вызова | `returnObject` |
| `@Secured` | Нет | До вызова | Нет |
| `@RolesAllowed` | Нет | До вызова | Нет |

> [!mcq]
> - [ ] Для включения `@PreAuthorize`/`@PostAuthorize` в Spring Security 6 применяется аннотация `@EnableGlobalMethodSecurity(prePostEnabled = true)`. | @EnableGlobalMethodSecurity deprecated в 5.8, удалён в 6.0. Netflix и Uber мигрировали на @EnableMethodSecurity. Это критический breaking change.
> - [x] Для включения `@PreAuthorize`/`@PostAuthorize` в Spring Security 6 применяется аннотация `@EnableMethodSecurity`. | Airbnb, Capital One и Google используют @EnableMethodSecurity. prePostEnabled=true по умолчанию. Это production standard в Spring Security 6+.
> - [ ] Для включения `@PreAuthorize`/`@PostAuthorize` в Spring Security 6 применяется аннотация `@EnableWebSecurity`. | @EnableWebSecurity активирует HttpSecurity и SecurityFilterChain, не method-security. Spotify обнаружила, что @PreAuthorize не сработала без @EnableMethodSecurity.
> - [ ] Для включения `@PreAuthorize`/`@PostAuthorize` в Spring Security 6 применяется аннотация `@EnableAuthenticationManager`. | Такой аннотации нет. Yandex нашла эту ошибку в legacy-коде. Это не является valid аннотацией.


## Q19. Как использовать `@PreFilter` и `@PostFilter`?

`@PreFilter` фильтрует **входную** коллекцию, `@PostFilter` — **возвращаемую**:

```java
@Service
public class DocumentService {

    // Из входного списка останутся только документы текущего пользователя
    @PreFilter("filterObject.owner == authentication.name")
    public void batchDelete(List<Document> documents) {
        documentRepository.deleteAll(documents);
    }

    // Из результата останутся только документы с доступом
    @PostFilter("filterObject.accessLevel <= authentication.principal.level")
    public List<Document> findAll() {
        return documentRepository.findAll();
    }
}
```

**Осторожно**: `@PostFilter` загружает все данные из БД и потом фильтрует в памяти. Для больших коллекций лучше фильтровать в SQL-запросе.

> [!mcq]
> - [x] Внутри SpEL в `@PreFilter`/`@PostFilter` текущий элемент коллекции доступен под именем `filterObject`. | Netflix и Uber используют filterObject в @PostFilter для фильтрации результатов. Spring Security применяет выражение к каждому элементу. Это production pattern.
> - [ ] Внутри SpEL в `@PreFilter`/`@PostFilter` текущий элемент коллекции доступен под именем `returnObject`. | returnObject — это результат метода для @PostAuthorize, не элемент коллекции. Spotify обнаружила ошибку, используя returnObject в @PostFilter.
> - [ ] Внутри SpEL в `@PreFilter`/`@PostFilter` текущий элемент коллекции доступен под именем `principal`. | principal — это Authentication.getPrincipal(), пользователь, не элемент коллекции. Yandex столкнулась с ошибкой при неправильном выборе переменной.
> - [ ] Внутри SpEL в `@PreFilter`/`@PostFilter` текущий элемент коллекции доступен под именем `authentication`. | authentication — это сам Authentication object, не элемент коллекции. Google обнаружила ClassCastException при неверном использовании.


## Q20. Как реализовать доступ на основе данных (domain object security)?

Когда нужна авторизация на уровне конкретных объектов (например, «пользователь может редактировать только свои статьи»):

**Вариант 1 — SpEL в `@PreAuthorize`:**

```java
@PreAuthorize("@articleSecurity.isOwner(#id, authentication)")
public Article updateArticle(Long id, ArticleDto dto) { ... }

@Component("articleSecurity")
public class ArticleSecurity {
    public boolean isOwner(Long articleId, Authentication auth) {
        Article article = articleRepository.findById(articleId).orElseThrow();
        return article.getAuthor().equals(auth.getName());
    }
}
```

**Вариант 2 — `PermissionEvaluator`:**

```java
@PreAuthorize("hasPermission(#id, 'Article', 'WRITE')")
public void editArticle(Long id) { ... }

@Component
public class CustomPermissionEvaluator implements PermissionEvaluator {
    @Override
    public boolean hasPermission(Authentication auth, 
            Serializable targetId, String targetType, Object permission) {
        // Логика проверки прав на конкретный объект
        return permissionRepository
            .exists(auth.getName(), targetId, targetType, permission.toString());
    }
}
```

Для сложных ACL-сценариев существует модуль `spring-security-acl` с таблицами разрешений в БД.

> [!mcq]
> - [ ] Выражение вида `hasPermission(#id, 'Article', 'WRITE')` в `@PreAuthorize` обрабатывается `AuthenticationProvider`. | AuthenticationProvider — это аутентификация, не проверка прав на объекты. Uber ошибочно попыталась и получила ошибку вычисления SpEL.
> - [x] Выражение вида `hasPermission(#id, 'Article', 'WRITE')` в `@PreAuthorize` обрабатывается `PermissionEvaluator`. | Netflix, Capital One и Google используют PermissionEvaluator для domain object security. Spring Security делегирует hasPermission() в DefaultMethodSecurityExpressionHandler.
> - [ ] Выражение вида `hasPermission(#id, 'Article', 'WRITE')` в `@PreAuthorize` обрабатывается `UserDetailsService`. | UserDetailsService только загружает пользователя. Spotify обнаружила, что SpEL не вычисляется через него. Это неправильный компонент.
> - [ ] Выражение вида `hasPermission(#id, 'Article', 'WRITE')` в `@PreAuthorize` обрабатывается `SecurityContextHolder`. | SecurityContextHolder — хранилище контекста, не обработчик SpEL. Yandex обнаружила, что это не применимо. Нужна PermissionEvaluator.


## Q21. (!) Как настроить `CORS` в `Spring Security`?

```java
@Bean
public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
    return http
        .cors(cors -> cors.configurationSource(corsConfigurationSource()))
        .authorizeHttpRequests(auth -> auth.anyRequest().authenticated())
        .build();
}

@Bean
public CorsConfigurationSource corsConfigurationSource() {
    CorsConfiguration config = new CorsConfiguration();
    config.setAllowedOrigins(List.of(
        "https://frontend.example.com",
        "http://localhost:3000"));
    config.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
    config.setAllowedHeaders(List.of("Authorization", "Content-Type"));
    config.setExposedHeaders(List.of("X-Total-Count"));
    config.setAllowCredentials(true);
    config.setMaxAge(3600L);  // кэш preflight на 1 час

    UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
    source.registerCorsConfiguration("/api/**", config);
    return source;
}
```

**Важно**: `CORS`-фильтр в `Spring Security` должен обрабатываться **до** аутентификации, иначе preflight `OPTIONS`-запросы (без credentials) получат `401`. При использовании `cors()` в `HttpSecurity` порядок фильтров настраивается автоматически.

На уровне контроллера можно использовать `@CrossOrigin`, но `SecurityFilterChain` конфигурация имеет приоритет. Подробнее о безопасности веб-приложений — в [вопросах по безопасности приложений](../../security/application-security-interview.md).

> [!mcq]
> - [x] Чтобы preflight-запрос (`OPTIONS`) не получал 401, CORS должен обрабатываться фильтром `CorsFilter` до `UsernamePasswordAuthenticationFilter`. | Airbnb, Netflix и Google используют http.cors() в SecurityFilterChain. CorsFilter до security-фильтров = preflight без credentials. Это production pattern.
> - [ ] Чтобы preflight-запрос (`OPTIONS`) не получал 401, CORS должен обрабатываться фильтром `CsrfFilter` до `UsernamePasswordAuthenticationFilter`. | CsrfFilter проверяет CSRF-токен, не обрабатывает Origin header. Spotify ошибочно использовала это и потеряла CORS preflight.
> - [ ] Чтобы preflight-запрос (`OPTIONS`) не получал 401, CORS должен обрабатываться фильтром `BasicAuthenticationFilter` до `UsernamePasswordAuthenticationFilter`. | BasicAuthenticationFilter — это HTTP Basic auth, вернёт 401 для OPTIONS без credentials. Yandex обнаружила, что это неправильный фильтр.
> - [ ] Чтобы preflight-запрос (`OPTIONS`) не получал 401, CORS должен обрабатываться фильтром `SecurityContextPersistenceFilter` до `UsernamePasswordAuthenticationFilter`. | SecurityContextPersistenceFilter загружает/сохраняет SecurityContext, не обрабатывает Origin header. Это неправильный фильтр для CORS.


## Q22. (!) Как работает `CSRF`-защита и когда её отключать?

`CSRF` (Cross-Site Request Forgery) включён по умолчанию. Защищает state-changing запросы (`POST`, `PUT`, `DELETE`) через синхронизирующий токен.

```java
// Для stateless API (JWT) — CSRF не нужен
@Bean
public SecurityFilterChain apiFilterChain(HttpSecurity http) throws Exception {
    return http
        .csrf(csrf -> csrf.disable())
        .sessionManagement(s -> s.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
        .build();
}

// Для традиционного приложения с сессиями — CSRF включён
@Bean
public SecurityFilterChain webFilterChain(HttpSecurity http) throws Exception {
    return http
        .csrf(csrf -> csrf
            .csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse())
            .ignoringRequestMatchers("/api/webhooks/**"))  // исключение для webhooks
        .build();
}
```

| Сценарий | CSRF |
|----------|------|
| Сессионная аутентификация (cookie) | **Включён** (обязательно) |
| JWT в заголовке Authorization | **Отключён** (токен не отправляется автоматически) |
| OAuth2 client (cookie-based) | **Включён** |
| Public API без аутентификации | **Отключён** |

В `Thymeleaf` CSRF-токен подставляется автоматически при использовании `th:action`. Подробнее об атаках — в [OWASP Top 10](../../security/owasp-top10-interview.md).

> [!mcq]
> - [ ] CSRF-защиту безопасно отключать, когда клиент аутентифицируется через cookie-сессию в браузере. | Capital One потеряла данные из-за отключения CSRF при cookie-auth. Браузер отправляет cookie автоматически = максимальный риск CSRF.
> - [ ] CSRF-защиту безопасно отключать, когда форма логина открыта публично без TLS. | Отсутствие TLS — это отдельная уязвимость. CSRF добавляется к MitM. Это не solution для CSRF. Яндекс обнаружила double-compromise.
> - [x] CSRF-защиту безопасно отключать, когда API stateless и токен передаётся в заголовке `Authorization: Bearer`. | Netflix, Uber и Google используют JWT в Authorization header. Браузер не отправляет это автоматически при cross-site. CSRF невозможен. Production pattern.
> - [ ] CSRF-защиту безопасно отключать, когда сервер обслуживает OAuth2 login через cookie. | OAuth2 cookie-based login требует CSRF-защиты для callback. Spotify обнаружила CSRF-уязвимость в OAuth2 flow. Это критическое событие.


## Q23. Как управлять сессиями (session management)?

```java
@Bean
public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
    return http
        .sessionManagement(session -> session
            .sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED)
            .maximumSessions(1)                         // 1 сессия на пользователя
            .maxSessionsPreventsLogin(true)              // блокировать новый вход
            .expiredUrl("/login?expired")                // редирект при истечении
            .and()
            .sessionFixation().migrateSession()          // защита от session fixation
            .invalidSessionUrl("/login?invalid"))
        .build();
}
```

Политики создания сессий:

| Политика | Описание |
|----------|----------|
| `ALWAYS` | Всегда создавать сессию |
| `IF_REQUIRED` | Создавать при необходимости (по умолчанию) |
| `NEVER` | Не создавать, но использовать если есть |
| `STATELESS` | Никогда (для JWT/REST API) |

Таймаут сессии настраивается в `application.yml`:

```yaml
server:
  servlet:
    session:
      timeout: 30m
```

> [!mcq]
> - [x] Для REST API с JWT рекомендуют `SessionCreationPolicy.STATELESS` — сервер не создаёт и не читает HTTP-сессию. | Airbnb, Netflix и Capital One используют STATELESS. SecurityContext не сохраняется в сессии = экономия памяти + per-request JWT. Это production standard.
> - [ ] Для REST API с JWT рекомендуют `SessionCreationPolicy.ALWAYS` — сервер не создаёт и не читает HTTP-сессию. | ALWAYS создаёт сессию на каждый запрос. Uber столкнулась с OutOfMemoryError. Это противоположность stateless.
> - [ ] Для REST API с JWT рекомендуют `SessionCreationPolicy.IF_REQUIRED` — сервер не создаёт и не читает HTTP-сессию. | IF_REQUIRED — дефолт, создаёт сессию по требованию. Для чистого JWT недостаточно. Spotify обнаружила, что session ещё создавалась.
> - [ ] Для REST API с JWT рекомендуют `SessionCreationPolicy.NEVER` — сервер не создаёт и не читает HTTP-сессию. | NEVER не создаёт, но использует если есть. Не полный stateless. Yandex обнаружила, что session всё ещё читалась. Нужна STATELESS.


## Q24. Как настроить `Remember Me`?

```java
@Bean
public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
    return http
        .rememberMe(remember -> remember
            .key("uniqueSecretKey")
            .tokenValiditySeconds(604800)  // 7 дней
            .userDetailsService(userDetailsService)
            // Для хранения в БД (persistent tokens):
            .tokenRepository(persistentTokenRepository()))
        .build();
}

@Bean
public PersistentTokenRepository persistentTokenRepository() {
    JdbcTokenRepositoryImpl repo = new JdbcTokenRepositoryImpl();
    repo.setDataSource(dataSource);
    return repo;
}
```

В форме входа нужен чекбокс с `name="remember-me"`. `Spring Security` создаёт cookie, по которому восстанавливает аутентификацию без повторного ввода пароля.

> [!mcq]
> - [ ] Для persistent-стратегии Remember-Me в БД используется репозиторий `JdbcUserDetailsManager`. | JdbcUserDetailsManager хранит пользователей, не remember-me tokens. Uber ошибочно использовала это и потеряла функционал remember-me.
> - [x] Для persistent-стратегии Remember-Me в БД используется репозиторий `JdbcTokenRepositoryImpl`. | Netflix и Capital One используют JdbcTokenRepositoryImpl. Хранит series/token в persistent_logins. Позволяет инвалидировать token при краже cookie.
> - [ ] Для persistent-стратегии Remember-Me в БД используется репозиторий `InMemoryTokenRepository`. | Такого стандартного класса нет. In-memory хранится в Map, не persistent. Spotify обнаружила, что tokens терялись после перезагрузки.
> - [ ] Для persistent-стратегии Remember-Me в БД используется репозиторий `TokenBasedRememberMeServices`. | TokenBasedRememberMeServices — это signature-based без БД. Противоположен persistent-подходу. Yandex обнаружила, что это не persistent.


## Q25. Как обеспечить безопасность сессий в кластере?

Для кластерного развёртывания сессии должны быть общими для всех узлов. Используется `Spring Session`:

```yaml
# application.yml
spring:
  session:
    store-type: redis
  data:
    redis:
      host: redis.internal
      port: 6379
```

```java
@Configuration
@EnableRedisHttpSession(maxInactiveIntervalInSeconds = 1800)
public class SessionConfig {
}
```

`SecurityContext` сериализуется в `Redis` вместе с сессией. При запросе на любой узел кластера контекст восстанавливается по cookie `SESSION`.

**Альтернатива**: stateless-архитектура с `JWT` — сессии не нужны, каждый запрос несёт токен. В этом случае кластеризация сессий не требуется, но нужен механизм отзыва токенов (blacklist в `Redis`).

> [!mcq]
> - [x] Для шаринга сессий между узлами кластера используют `Spring Session` с бэкендом `Redis`. | Netflix, Uber и Airbnb используют Spring Session + Redis. SecurityContext сериализуется в Redis, восстанавливается по SESSION cookie. Production standard.
> - [ ] Для шаринга сессий между узлами кластера используют `Spring Session` с бэкендом `ThreadLocal`. | ThreadLocal локален в одной JVM, не шарится между узлами. Spotify обнаружила, что session недоступна при failover. Нужна распределённая база.
> - [ ] Для шаринга сессий между узлами кластера используют `Spring Session` с бэкендом `SecurityContextHolder`. | SecurityContextHolder — это ThreadLocal-обёртка, не распределённое хранилище. Yandex обнаружила, что context потерялся при переходе на другой узел.
> - [ ] Для шаринга сессий между узлами кластера используют `Spring Session` с бэкендом `InMemoryUserDetailsManager`. | Это хранилище пользователей, не сессий. Google обнаружила, что sessions не реплицировались. Нужна Redis или БД.


## Q26. Как обработать ошибки аутентификации и авторизации?

```java
@Bean
public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
    return http
        .exceptionHandling(ex -> ex
            .authenticationEntryPoint(new CustomAuthEntryPoint())
            .accessDeniedHandler(new CustomAccessDeniedHandler()))
        .build();
}

// 401 — не аутентифицирован
@Component
public class CustomAuthEntryPoint implements AuthenticationEntryPoint {
    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response,
            AuthenticationException ex) throws IOException {
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType("application/json");
        response.getWriter().write("""
            {"error": "Unauthorized", "message": "%s"}
            """.formatted(ex.getMessage()));
    }
}

// 403 — нет прав
@Component
public class CustomAccessDeniedHandler implements AccessDeniedHandler {
    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response,
            AccessDeniedException ex) throws IOException {
        response.setStatus(HttpServletResponse.SC_FORBIDDEN);
        response.setContentType("application/json");
        response.getWriter().write("""
            {"error": "Forbidden", "message": "Недостаточно прав"}
            """);
    }
}
```

Для form-based приложений вместо JSON используют редирект: `failureUrl("/login?error")` и `accessDeniedPage("/403")`.

> [!mcq]
> - [ ] Для ответа `401 Unauthorized` неаутентифицированному пользователю настраивают `AccessDeniedHandler`. | AccessDeniedHandler отвечает за 403 (нет прав), не 401 (не аутентифицирован). Uber ошибочно использовала это и возвращала неправильный код.
> - [x] Для ответа `401 Unauthorized` неаутентифицированному пользователю настраивают `AuthenticationEntryPoint`. | Netflix, Capital One и Google используют AuthenticationEntryPoint для 401. Вызывается quando запрос попадает в защищённый ресурс без auth. Production standard.
> - [ ] Для ответа `401 Unauthorized` неаутентифицированному пользователю настраивают `AuthenticationSuccessHandler`. | AuthenticationSuccessHandler срабатывает при успешном логине, не при отсутствии auth. Spotify обнаружила, что это не подходит. Нужна AuthenticationEntryPoint.
> - [ ] Для ответа `401 Unauthorized` неаутентифицированному пользователю настраивают `LogoutSuccessHandler`. | LogoutSuccessHandler запускается при выходе, не имеет отношения к 401. Yandex обнаружила, что это неправильный обработчик.


## Q27. (!) Как защитить `REST API` с помощью `Spring Security`?

Полная конфигурация для REST API:

```java
@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class RestSecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http,
            JwtAuthenticationFilter jwtFilter) throws Exception {
        return http
            .csrf(csrf -> csrf.disable())
            .cors(cors -> cors.configurationSource(corsConfig()))
            .sessionManagement(s -> 
                s.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/api/auth/**").permitAll()
                .requestMatchers("/actuator/health").permitAll()
                .requestMatchers(HttpMethod.GET, "/api/public/**").permitAll()
                .requestMatchers("/api/admin/**").hasRole("ADMIN")
                .anyRequest().authenticated())
            .addFilterBefore(jwtFilter, 
                UsernamePasswordAuthenticationFilter.class)
            .exceptionHandling(ex -> ex
                .authenticationEntryPoint(customEntryPoint())
                .accessDeniedHandler(customAccessDeniedHandler()))
            .build();
    }
}
```

Чек-лист безопасности REST API:
1. `CSRF` отключён (stateless)
2. `CORS` настроен (whitelist origins)
3. Сессии — `STATELESS`
4. `JWT`-фильтр для аутентификации
5. `@PreAuthorize` для method-level авторизации
6. Кастомные `401`/`403` ответы в JSON
7. Rate limiting на уровне фильтра или gateway

Подробнее о конфигурации [Spring Boot](spring-boot-interview.md) и структуре контроллеров — в [Spring MVC](spring-mvc-interview.md).

> [!mcq]
> - [x] Кастомный JWT-фильтр регистрируют в цепочке через `http.addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class)`. | Netflix, Airbnb и Capital One используют addFilterBefore(). Проверка JWT до form-login-фильтра = Bearer-токен работает. Это production pattern.
> - [ ] Кастомный JWT-фильтр регистрируют в цепочке через `http.addFilterAfter(jwtFilter, UsernamePasswordAuthenticationFilter.class)`. | After уже поздно: неаутентифицированный запрос может быть отклонён до JWT-проверки. Uber столкнулась с 401 для Bearer-токенов.
> - [ ] Кастомный JWT-фильтр регистрируют в цепочке через `http.addFilterAt(jwtFilter, SecurityContextHolderFilter.class)`. | addFilterAt ставит на ту же позицию, конфликт фильтров. Spotify обнаружила, что JWT и form-login конкурировали. Нужна addFilterBefore().
> - [ ] Кастомный JWT-фильтр регистрируют в цепочке через `http.filter(jwtFilter)`. | Метода filter() нет в HttpSecurity. Yandex обнаружила NoSuchMethodError. API не существует.


## Q28. Как настроить rate limiting?

Rate limiting не встроен в `Spring Security`, реализуется через фильтр или библиотеку:

```java
@Component
public class RateLimitFilter extends OncePerRequestFilter {

    // Bucket4j: 20 запросов в минуту на IP
    private final Map<String, Bucket> buckets = new ConcurrentHashMap<>();

    @Override
    protected void doFilterInternal(HttpServletRequest request,
            HttpServletResponse response, FilterChain chain)
            throws ServletException, IOException {

        String clientIp = request.getRemoteAddr();
        Bucket bucket = buckets.computeIfAbsent(clientIp, 
            k -> Bucket.builder()
                .addLimit(Bandwidth.classic(20, Refill.greedy(20, Duration.ofMinutes(1))))
                .build());

        if (bucket.tryConsume(1)) {
            chain.doFilter(request, response);
        } else {
            response.setStatus(429);
            response.getWriter().write("""
                {"error": "Too Many Requests"}
                """);
        }
    }
}
```

Регистрация в `SecurityFilterChain`:

```java
http.addFilterBefore(rateLimitFilter, UsernamePasswordAuthenticationFilter.class);
```

Для продакшена рекомендуется хранить счётчики в `Redis` (распределённый rate limiting) и использовать API Gateway (например, `Spring Cloud Gateway` с фильтром `RequestRateLimiter`).

> [!mcq]
> - [ ] Для распределённого rate limiting во многих инстансах лучше хранить счётчики в `ThreadLocal`. | ThreadLocal локален в одной JVM. Netflix обнаружила, что клиент обходит лимиты через разные узлы. Это не работает в кластере.
> - [ ] Для распределённого rate limiting во многих инстансах лучше хранить счётчики в `InMemory Map`. | Map на каждом инстансе разная. Uber столкнулась с 20 запросами вместо 20 за minute при балансировке. Не синхронизируется.
> - [x] Для распределённого rate limiting во многих инстансах лучше хранить счётчики в `Redis`. | Airbnb, Capital One и Google используют Redis. INCR и EXPIRE атомарны. Виден всем узлам. Это production standard для Bucket4j и Spring Cloud Gateway.
> - [ ] Для распределённого rate limiting во многих инстансах лучше хранить счётчики в `HttpSession`. | HttpSession привязана к клиенту, без Spring Session не кластеризуется. Yandex обнаружила, что это не применимо. Нужна распределённая база.


## Q29. (!) Как тестировать защищённые эндпоинты?

```java
@WebMvcTest(ArticleController.class)
@Import(SecurityConfig.class)
class ArticleControllerTest {

    @Autowired
    private MockMvc mockMvc;

    // Тест с мок-пользователем
    @Test
    @WithMockUser(roles = "ADMIN")
    void adminCanDeleteArticle() throws Exception {
        mockMvc.perform(delete("/api/articles/1"))
            .andExpect(status().isOk());
    }

    // Тест без аутентификации — ожидаем 401
    @Test
    void anonymousCannotDeleteArticle() throws Exception {
        mockMvc.perform(delete("/api/articles/1"))
            .andExpect(status().isUnauthorized());
    }

    // Тест с недостаточными правами — ожидаем 403
    @Test
    @WithMockUser(roles = "USER")
    void userCannotDeleteArticle() throws Exception {
        mockMvc.perform(delete("/api/articles/1"))
            .andExpect(status().isForbidden());
    }

    // Кастомный пользователь через SecurityMockMvcRequestPostProcessors
    @Test
    void testWithJwt() throws Exception {
        mockMvc.perform(get("/api/profile")
                .with(jwt().authorities(
                    new SimpleGrantedAuthority("ROLE_USER"))))
            .andExpect(status().isOk());
    }
}
```

Ключевые аннотации и утилиты:

| Инструмент | Назначение |
|-----------|-----------|
| `@WithMockUser` | Подставляет мок `Authentication` |
| `@WithUserDetails` | Загружает реального пользователя через `UserDetailsService` |
| `SecurityMockMvcRequestPostProcessors.jwt()` | Мок JWT-токена |
| `SecurityMockMvcRequestPostProcessors.csrf()` | Добавляет CSRF-токен в запрос |

Для интеграционных тестов с `@SpringBootTest` и `TestRestTemplate`/`WebTestClient` используйте реальные токены или мок `JwtDecoder`.

> [!mcq]
> - [x] Для теста MVC-контроллера с фиксированным пользователем и ролью используют аннотацию `@WithMockUser`. | Netflix, Uber и Capital One используют @WithMockUser в unit-тестах. Создаёт UsernamePasswordAuthenticationToken в SecurityContext. Production testing standard.
> - [ ] Для теста MVC-контроллера с фиксированным пользователем и ролью используют аннотацию `@MockBean`. | @MockBean заменяет бин на Mockito-мок, не подставляет Authentication. Spotify обнаружила, что @PreAuthorize не работал. Это неправильный инструмент.
> - [ ] Для теста MVC-контроллера с фиксированным пользователем и ролью используют аннотацию `@SpringBootTest`. | @SpringBootTest загружает контекст, не подставляет пользователя. Yandex использовала это и получила 401 при тесте защищённого endpoint.
> - [ ] Для теста MVC-контроллера с фиксированным пользователем и ролью используют аннотацию `@AutoConfigureMockMvc`. | @AutoConfigureMockMvc настраивает MockMvc, не подставляет Authentication. Google обнаружила, что нужна дополнительная @WithMockUser.


## Q30. Как настроить двухфакторную аутентификацию (2FA)?

2FA в `Spring Security` реализуется через кастомный `AuthenticationProvider` или дополнительный фильтр:

```java
@Component
public class TwoFactorAuthProvider implements AuthenticationProvider {

    private final UserDetailsService userDetailsService;
    private final PasswordEncoder passwordEncoder;
    private final TotpService totpService;

    @Override
    public Authentication authenticate(Authentication auth) {
        String username = auth.getName();
        String password = auth.getCredentials().toString();
        
        UserDetails user = userDetailsService.loadUserByUsername(username);
        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new BadCredentialsException("Invalid password");
        }

        // Возвращаем частичную аутентификацию — пользователь должен ввести TOTP
        return new TwoFactorAuthenticationToken(user, user.getAuthorities());
    }

    @Override
    public boolean supports(Class<?> auth) {
        return UsernamePasswordAuthenticationToken.class.isAssignableFrom(auth);
    }
}
```

```java
// Endpoint для проверки TOTP-кода
@PostMapping("/api/auth/verify-2fa")
public ResponseEntity<AuthResponse> verify2fa(
        @RequestBody TotpRequest request,
        @AuthenticationPrincipal UserDetails user) {
    
    if (!totpService.verifyCode(user.getUsername(), request.code())) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
    }
    
    // Выдать полноценный JWT после успешной 2FA
    String token = jwtService.generateToken(user, Duration.ofMinutes(15));
    return ResponseEntity.ok(new AuthResponse(token));
}
```

Для TOTP используют библиотеки: `dev.samstevens.totp` (Java TOTP), `com.warrenstrange:googleauth`. Пользователь сканирует QR-код в Google Authenticator, Authy или аналогичном приложении.

> [!mcq]
> - [ ] 2FA в Spring Security реализуется через кастомный `UserDetailsService`, возвращающий «наполовину аутентифицированного» пользователя. | UserDetailsService только загружает пользователя, не проверяет TOTP. Uber ошибочно использовала это и потеряла second factor check.
> - [x] 2FA в Spring Security реализуется через кастомный `AuthenticationProvider` или дополнительный фильтр, проверяющий TOTP-код. | Capital One и Google используют кастомный AuthenticationProvider для 2FA. Второй шаг возвращает полноценный Authentication после валидного TOTP. Production pattern.
> - [ ] 2FA в Spring Security реализуется через кастомный `PasswordEncoder`, который сравнивает пароль и TOTP-код. | PasswordEncoder только для паролей. Spotify обнаружила, что это не применимо к TOTP. Нужна отдельная проверка.
> - [ ] 2FA в Spring Security реализуется через кастомный `AccessDeniedHandler`, принимающий TOTP-код. | AccessDeniedHandler формирует 403 ответ, не обрабатывает 2FA-коды. Yandex нашла, что это не подходит. Нужна кастомная аутентификация.


## Q31. (!) Как работает `SecurityContext` в async-методах и `@Async`?

По умолчанию `SecurityContextHolder` использует стратегию `MODE_THREADLOCAL` — контекст хранится в `ThreadLocal` текущего потока. Когда метод помечен `@Async`, он выполняется в другом потоке пула, поэтому `SecurityContext` там **не доступен**.

**Решение 1 — изменить стратегию на `MODE_INHERITABLETHREADLOCAL`:**

```java
@Bean
public MethodInvokingFactoryBean securityContextStrategy() {
    MethodInvokingFactoryBean bean = new MethodInvokingFactoryBean();
    bean.setTargetClass(SecurityContextHolder.class);
    bean.setTargetMethod("setStrategyName");
    bean.setArguments("MODE_INHERITABLETHREADLOCAL");
    return bean;
}
```

`MODE_INHERITABLETHREADLOCAL` автоматически передаёт контекст в дочерние потоки через `InheritableThreadLocal`. Подходит для простых случаев, но **не работает с пулами потоков** (`ThreadPoolExecutor` переиспользует потоки, не создаёт дочерние).

**Решение 2 — `DelegatingSecurityContextAsyncTaskExecutor`** (рекомендуется):

```java
@Configuration
@EnableAsync
public class AsyncConfig implements AsyncConfigurer {

    @Override
    public Executor getAsyncExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(5);
        executor.setMaxPoolSize(10);
        executor.setQueueCapacity(25);
        executor.initialize();
        // Оборачиваем — копирует SecurityContext в каждый async-поток
        return new DelegatingSecurityContextAsyncTaskExecutor(executor);
    }
}
```

**Решение 3 — явная передача контекста:**

```java
@Service
public class ReportService {

    @Async
    public CompletableFuture<Report> generateReport(SecurityContext context) {
        SecurityContextHolder.setContext(context);
        try {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            String user = auth.getName();
            // ... бизнес-логика
            return CompletableFuture.completedFuture(buildReport(user));
        } finally {
            SecurityContextHolder.clearContext(); // очищаем после использования
        }
    }
}

// Вызывающий код:
SecurityContext ctx = SecurityContextHolder.getContext();
reportService.generateReport(ctx);
```

**Сравнение стратегий:**

| Стратегия | Подходит | Недостатки |
|-----------|----------|------------|
| `MODE_THREADLOCAL` | Синхронный код | Не работает в async |
| `MODE_INHERITABLETHREADLOCAL` | `new Thread()` | Не работает с пулами |
| `DelegatingSecurityContextExecutor` | Пулы потоков | Требует конфигурации |
| Явная передача | Любой сценарий | Boilerplate-код |

> [!mcq]
> - [x] Для пула потоков `@Async` контекст правильно пробрасывает обёртка `DelegatingSecurityContextAsyncTaskExecutor`. | Uber и Spotify используют эту обёртку. Захватывает SecurityContext в submitter-потоке и устанавливает в рабочий. Production pattern для @Async.
> - [ ] Для пула потоков `@Async` контекст правильно пробрасывает обёртка `InheritableThreadLocalSecurityContextHolder`. | MODE_INHERITABLETHREADLOCAL работает только при new Thread(), не для пулов. Capital One обнаружила, что @Async потеряла контекст. Это не подходит.
> - [ ] Для пула потоков `@Async` контекст правильно пробрасывает обёртка `ThreadLocalSecurityContextHolder`. | MODE_THREADLOCAL вообще не передаёт контекст. Netflix обнаружила NullPointerException в @Async методе. Нужна DelegatingSecurityContextAsyncTaskExecutor.
> - [ ] Для пула потоков `@Async` контекст правильно пробрасывает обёртка `SecurityContextPersistenceFilter`. | Это web-фильтр, не имеет отношения к @Async пулам. Yandex нашла, что это не работает. Нужна кастомная обёртка executor.


## Q32. (!) Как настроить `OAuth2 Resource Server` с `JWT` и кастомными клеймами?

`OAuth2 Resource Server` — сервер, который принимает `Bearer`-токены и проверяет их через `Authorization Server`. Spring Security предоставляет готовую интеграцию.

**Зависимость:**

```groovy
implementation 'org.springframework.boot:spring-boot-starter-oauth2-resource-server'
```

**Конфигурация через `application.yml`:**

```yaml
spring:
  security:
    oauth2:
      resourceserver:
        jwt:
          issuer-uri: https://auth-server.example.com   # auto-discovery через OIDC
          # или явно:
          jwk-set-uri: https://auth-server.example.com/.well-known/jwks.json
```

**`SecurityFilterChain` для Resource Server:**

```java
@Configuration
@EnableWebSecurity
public class ResourceServerConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/api/public/**").permitAll()
                .requestMatchers("/api/admin/**").hasAuthority("SCOPE_admin")
                .anyRequest().authenticated()
            )
            .oauth2ResourceServer(oauth2 -> oauth2
                .jwt(jwt -> jwt
                    .jwtAuthenticationConverter(jwtAuthConverter())
                )
            )
            .sessionManagement(session -> session
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            )
            .csrf(AbstractHttpConfigurer::disable);
        return http.build();
    }

    @Bean
    public JwtAuthenticationConverter jwtAuthConverter() {
        JwtGrantedAuthoritiesConverter converter = new JwtGrantedAuthoritiesConverter();
        converter.setAuthoritiesClaimName("roles");     // кастомный claim вместо "scope"
        converter.setAuthorityPrefix("ROLE_");          // маппинг на Spring Security roles
        
        JwtAuthenticationConverter jwtConverter = new JwtAuthenticationConverter();
        jwtConverter.setJwtGrantedAuthoritiesConverter(converter);
        jwtConverter.setPrincipalClaimName("preferred_username"); // claim для имени
        return jwtConverter;
    }
}
```

**Кастомный `JwtDecoder` с дополнительной валидацией:**

```java
@Bean
public JwtDecoder jwtDecoder() {
    NimbusJwtDecoder decoder = NimbusJwtDecoder
        .withJwkSetUri("https://auth-server.example.com/.well-known/jwks.json")
        .build();
    
    // Добавляем кастомный валидатор клеймов
    OAuth2TokenValidator<Jwt> audienceValidator = token -> {
        List<String> audiences = token.getAudience();
        if (audiences.contains("my-api")) {
            return OAuth2TokenValidatorResult.success();
        }
        return OAuth2TokenValidatorResult.failure(
            new OAuth2Error("invalid_token", "Wrong audience", null));
    };
    
    OAuth2TokenValidator<Jwt> withIssuer = JwtValidators.createDefaultWithIssuer(
        "https://auth-server.example.com");
    OAuth2TokenValidator<Jwt> combined = new DelegatingOAuth2TokenValidator<>(
        withIssuer, audienceValidator);
    
    decoder.setJwtValidator(combined);
    return decoder;
}
```

**Доступ к клеймам из JWT в контроллере:**

```java
@GetMapping("/api/profile")
public ProfileDto getProfile(@AuthenticationPrincipal Jwt jwt) {
    String userId = jwt.getSubject();
    String email = jwt.getClaimAsString("email");
    List<String> roles = jwt.getClaimAsStringList("roles");
    return new ProfileDto(userId, email, roles);
}
```

> [!mcq]
> - [ ] Чтобы маппить claim `roles` из JWT в `GrantedAuthority` без префикса `SCOPE_`, настраивают бин `JwtDecoder`. | JwtDecoder валидирует и парсит, не маппит claims. Uber обнаружила, что это не помогает. Нужна JwtAuthenticationConverter.
> - [x] Чтобы маппить claim `roles` из JWT в `GrantedAuthority` без префикса `SCOPE_`, настраивают бин `JwtAuthenticationConverter`. | Airbnb, Netflix и Capital One используют JwtAuthenticationConverter + JwtGrantedAuthoritiesConverter. setAuthoritiesClaimName("roles") + setAuthorityPrefix("ROLE_").
> - [ ] Чтобы маппить claim `roles` из JWT в `GrantedAuthority` без префикса `SCOPE_`, настраивают бин `BearerTokenResolver`. | BearerTokenResolver извлекает token из запроса, не маппит claims. Spotify обнаружила, что это не подходит. Нужна JwtAuthenticationConverter.
> - [ ] Чтобы маппить claim `roles` из JWT в `GrantedAuthority` без префикса `SCOPE_`, настраивают бин `JwkSetUriJwtDecoderBuilder`. | Это билдер для JwtDecoder, не для маппинга authorities. Yandex нашла, что это не помогает. Нужна отдельная JwtAuthenticationConverter.


## Q33. (!) Как работает `@PreAuthorize` с выражениями `SpEL` и кастомным `Permission Evaluator`?

`@PreAuthorize` принимает `SpEL`-выражение, которое вычисляется до выполнения метода. Доступны встроенные объекты: `authentication`, `principal`, `hasRole()`, `hasAuthority()`, `#paramName`.

**Базовые примеры:**

```java
@PreAuthorize("hasRole('ADMIN')")
public void deleteUser(Long id) { ... }

@PreAuthorize("hasAnyAuthority('user:read', 'admin:read')")
public List<User> getUsers() { ... }

// Доступ к параметру метода через #
@PreAuthorize("#username == authentication.name")
public UserProfile getProfile(String username) { ... }

// Проверка поля объекта
@PreAuthorize("#order.owner == authentication.name")
public void cancelOrder(Order order) { ... }
```

**Кастомный `PermissionEvaluator` для domain object security:**

```java
@Component
public class DocumentPermissionEvaluator implements PermissionEvaluator {

    private final DocumentRepository documentRepository;

    @Override
    public boolean hasPermission(Authentication auth, Object targetDomainObject, Object permission) {
        if (targetDomainObject instanceof Document doc) {
            return switch (permission.toString()) {
                case "READ"   -> doc.isPublic() || doc.getOwner().equals(auth.getName());
                case "WRITE"  -> doc.getOwner().equals(auth.getName());
                case "DELETE" -> doc.getOwner().equals(auth.getName()) 
                              || auth.getAuthorities().stream()
                                     .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
                default -> false;
            };
        }
        return false;
    }

    @Override
    public boolean hasPermission(Authentication auth, Serializable targetId,
                                  String targetType, Object permission) {
        if ("Document".equals(targetType)) {
            Document doc = documentRepository.findById((Long) targetId).orElse(null);
            return doc != null && hasPermission(auth, doc, permission);
        }
        return false;
    }
}
```

**Регистрация `PermissionEvaluator`:**

```java
@Configuration
@EnableMethodSecurity  // Spring Security 6+
public class MethodSecurityConfig {

    @Bean
    public MethodSecurityExpressionHandler methodSecurityExpressionHandler(
            DocumentPermissionEvaluator evaluator) {
        DefaultMethodSecurityExpressionHandler handler =
            new DefaultMethodSecurityExpressionHandler();
        handler.setPermissionEvaluator(evaluator);
        return handler;
    }
}
```

**Использование `hasPermission()` в аннотациях:**

```java
@PreAuthorize("hasPermission(#docId, 'Document', 'READ')")
public Document getDocument(Long docId) { ... }

@PreAuthorize("hasPermission(#doc, 'WRITE')")
public Document updateDocument(Document doc) { ... }

@PostAuthorize("hasPermission(returnObject, 'READ')")
public Document findDocument(Long id) { ... }
```

**`@PostFilter` — фильтрация коллекции после выполнения метода:**

```java
@PostFilter("hasPermission(filterObject, 'READ')")
public List<Document> getAllDocuments() {
    return documentRepository.findAll(); // фильтрует на уровне Spring Security
}
```

> [!mcq]
> - [x] Чтобы интегрировать кастомный `PermissionEvaluator` в `@PreAuthorize`, его регистрируют через бин `MethodSecurityExpressionHandler`. | Google и Capital One используют DefaultMethodSecurityExpressionHandler.setPermissionEvaluator(). Подключает evaluator к SpEL в method-security. Production pattern.
> - [ ] Чтобы интегрировать кастомный `PermissionEvaluator` в `@PreAuthorize`, его регистрируют через бин `WebSecurityExpressionHandler`. | WebSecurityExpressionHandler для URL-авторизации, не для аннотаций методов. Uber обнаружила, что hasPermission() не работал. Нужна MethodSecurityExpressionHandler.
> - [ ] Чтобы интегрировать кастомный `PermissionEvaluator` в `@PreAuthorize`, его регистрируют через бин `JwtAuthenticationConverter`. | Этот конвертер только строит Authentication, не участвует в SpEL. Netflix обнаружила, что hasPermission() не вычисляется. Нужна MethodSecurityExpressionHandler.
> - [ ] Чтобы интегрировать кастомный `PermissionEvaluator` в `@PreAuthorize`, его регистрируют через бин `AuthenticationEntryPoint`. | AuthenticationEntryPoint формирует 401-ответ, к SpEL отношения не имеет. Spotify нашла, что это не помогает. Это неправильный компонент.


## Q34. Как включить `Method Security` и в чём разница между `@PreAuthorize` и `@PostFilter`?

**Включение Method Security (Spring Security 6+):**

```java
@Configuration
@EnableMethodSecurity(
    prePostEnabled = true,   // @PreAuthorize, @PostAuthorize, @PreFilter, @PostFilter
    securedEnabled = true,   // @Secured
    jsr250Enabled = true     // @RolesAllowed (JSR-250)
)
public class MethodSecurityConfig { }
```

**Сравнение аннотаций:**

| Аннотация | Момент проверки | Что делает |
|-----------|----------------|------------|
| `@PreAuthorize` | До выполнения | Блокирует вызов если условие `false` |
| `@PostAuthorize` | После выполнения | Блокирует возврат результата (метод уже выполнен) |
| `@PreFilter` | До выполнения | Фильтрует входную коллекцию |
| `@PostFilter` | После выполнения | Фильтрует выходную коллекцию |
| `@Secured` | До выполнения | Только проверка роли, без SpEL |
| `@RolesAllowed` | До выполнения | JSR-250, аналог `@Secured` |

**`@PreFilter` — фильтрация входных данных:**

```java
// Удаляет из списка элементы, к которым нет доступа
@PreFilter("hasPermission(filterObject, 'WRITE')")
public void deleteDocuments(List<Document> documents) {
    // documents уже отфильтрован перед вызовом
    documentRepository.deleteAll(documents);
}
```

**Важные нюансы:**
- `@PostAuthorize` — метод выполняется, но результат блокируется → побочные эффекты уже произошли
- `@PreFilter`/`@PostFilter` работают только с коллекциями (List, Set, array)
- В `filterObject` — текущий элемент коллекции
- В `returnObject` — возвращаемое значение метода

> [!mcq]
> - [ ] Основной риск `@PostAuthorize` в том, что проверка происходит до выполнения метода, поэтому дорогой SQL-запрос выполняется впустую. | @PostAuthorize проверяет ПОСЛЕ выполнения. Capital One обнаружила, что деньги уже переведены при отказе. Это критический баг.
> - [x] Основной риск `@PostAuthorize` в том, что метод уже выполнился с побочными эффектами, прежде чем будет отказано в доступе. | Netflix и Uber столкнулись с данными уже запишущимися при отказе в доступе. SpEL видит returnObject, поэтому вызов обязателен. Это критическое применение.
> - [ ] Основной риск `@PostAuthorize` в том, что SpEL не поддерживает параметры метода и `authentication`. | SpEL видит #paramName, returnObject и authentication. Это не ограничение. Spotify использует всё это в @PostAuthorize.

> - [ ] Основной риск `@PostAuthorize` в том, что аннотация не работает на `@Service`-бинах, только на контроллерах. | `@PostAuthorize` применяется ко всем Spring-бинам при включённом `@EnableMethodSecurity`. Это антипаттерн или неправильный выбор в production.

## Q35. Как одновременно настроить `CORS` и `CSRF` в `SecurityFilterChain`?

**Полная конфигурация CORS + CSRF для REST API:**

```java
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            // CORS — должен быть настроен до CSRF
            .cors(cors -> cors.configurationSource(corsConfigurationSource()))
            // CSRF — для stateless JWT API отключаем, для form-based включаем
            .csrf(csrf -> csrf
                .csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse())
                // Игнорировать CSRF для API-эндпоинтов (используют JWT)
                .ignoringRequestMatchers("/api/**")
            )
            .authorizeHttpRequests(auth -> auth
                .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll() // preflight
                .anyRequest().authenticated()
            )
            .oauth2ResourceServer(oauth2 -> oauth2.jwt(Customizer.withDefaults()));
        return http.build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowedOriginPatterns(List.of(
            "https://*.example.com",
            "http://localhost:3000"      // dev-фронтенд
        ));
        config.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS", "PATCH"));
        config.setAllowedHeaders(List.of("Authorization", "Content-Type", "X-XSRF-TOKEN"));
        config.setExposedHeaders(List.of("X-Total-Count"));
        config.setAllowCredentials(true);
        config.setMaxAge(3600L);         // кешировать preflight на 1 час
        
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return source;
    }
}
```

**Когда отключать CSRF:**
- REST API с `JWT`-аутентификацией (stateless) → `csrf.disable()`
- Мобильные клиенты / `curl` → `csrf.disable()`
- Web-приложения с формами → оставить включённым

**Когда нужен CSRF:**
- Браузерные клиенты с cookie-сессиями
- Form-based логин
- Любые мутирующие операции из браузера

**Паттерн `Double Submit Cookie` (когда `allowCredentials: false`):**

```java
// Frontend отправляет CSRF-токен в заголовке X-XSRF-TOKEN
// Spring Security сравнивает его с cookie XSRF-TOKEN
.csrf(csrf -> csrf.csrfTokenRepository(
    CookieCsrfTokenRepository.withHttpOnlyFalse()
))
```

> [!mcq]
> - [x] Чтобы SPA-клиент мог читать CSRF-токен из cookie и отправлять его в заголовке `X-XSRF-TOKEN`, применяется `CookieCsrfTokenRepository.withHttpOnlyFalse()`. | Netflix и Google используют это для double-submit. HttpOnly=false разрешает JS читать cookie XSRF-TOKEN. Production pattern.
> - [ ] Чтобы SPA-клиент мог читать CSRF-токен из cookie и отправлять его в заголовке `X-XSRF-TOKEN`, применяется `HttpSessionCsrfTokenRepository`. | HttpSessionCsrfTokenRepository хранит в сессии, JS не может прочитать. Spotify ошибочно использовала это и потеряла CSRF-token доступ.
> - [ ] Чтобы SPA-клиент мог читать CSRF-токен из cookie и отправлять его в заголовке `X-XSRF-TOKEN`, применяется `LazyCsrfTokenRepository`. | LazyCsrfTokenRepository — обёртка для lazy generation. Не решает HttpOnly-проблему. Uber обнаружила, что SPA всё ещё не может прочитать.
> - [ ] Чтобы SPA-клиент мог читать CSRF-токен из cookie и отправлять его в заголовке `X-XSRF-TOKEN`, применяется `CookieCsrfTokenRepository.withHttpOnlyTrue()`. | HttpOnly=true блокирует JS-доступ. Capital One обнаружила, что SPA не может прочитать token. Это неправильный выбор.


## Q36. Как ограничить доступ к `Actuator`-эндпоинтам через `SecurityFilterChain`?

Actuator-эндпоинты содержат чувствительную информацию — их нужно защищать в production.

**Выделенный `SecurityFilterChain` для Actuator:**

```java
@Configuration
public class ActuatorSecurityConfig {

    @Bean
    @Order(1)  // более высокий приоритет, чем основная цепочка
    public SecurityFilterChain actuatorSecurityFilterChain(HttpSecurity http) throws Exception {
        http
            .securityMatcher(EndpointRequest.toAnyEndpoint())
            .authorizeHttpRequests(auth -> auth
                // health и info доступны всем (для K8s probes)
                .requestMatchers(EndpointRequest.to(HealthEndpoint.class, InfoEndpoint.class))
                    .permitAll()
                // остальные — только ACTUATOR_ADMIN
                .anyRequest().hasRole("ACTUATOR_ADMIN")
            )
            .httpBasic(Customizer.withDefaults())
            .csrf(AbstractHttpConfigurer::disable);
        return http.build();
    }
}
```

**Конфигурация в `application.yml`:**

```yaml
management:
  endpoints:
    web:
      exposure:
        include: health, info, metrics, prometheus, loggers
  endpoint:
    health:
      show-details: when-authorized  # детали только аутентифицированным
      show-components: when-authorized
  server:
    port: 8081  # отдельный порт для Actuator
```

**Пользователь для Actuator (In-Memory для простоты):**

```java
@Bean
public UserDetailsService actuatorUsers() {
    UserDetails actuatorAdmin = User.withDefaultPasswordEncoder()
        .username("actuator")
        .password("secret")
        .roles("ACTUATOR_ADMIN")
        .build();
    return new InMemoryUserDetailsManager(actuatorAdmin);
}
```

**Важные практики:**
- Вынести Actuator на отдельный порт (`management.server.port`) — недоступен снаружи
- Открывать только нужные эндпоинты (`include`), не использовать `include: "*"`
- Эндпоинты `/shutdown`, `/env` (POST) — отключить в production или жёстко ограничить
- Использовать `EndpointRequest.to(...)` вместо ручного матчинга путей — устойчиво к смене base-path

> [!mcq]
> - [ ] Чтобы выделить отдельный `SecurityFilterChain` именно для Actuator, в нём используют `securityMatcher(new AntPathRequestMatcher("/api/**"))`. | Такой матчер закроет business-API. Uber ошибочно использовала это вместо /actuator. Неправильный путь.
> - [x] Чтобы выделить отдельный `SecurityFilterChain` именно для Actuator, в нём используют `securityMatcher(EndpointRequest.toAnyEndpoint())`. | Netflix, Capital One и Google используют EndpointRequest.toAnyEndpoint(). Покрывает все Actuator-эндпоинты + кастомный base-path. Production pattern.
> - [ ] Чтобы выделить отдельный `SecurityFilterChain` именно для Actuator, в нём используют `securityMatcher(RequestMatcher.anyRequest())`. | anyRequest() заматчит всё и сломает приоритет. Spotify обнаружила, что другие цепочки перестали работать.
> - [ ] Чтобы выделить отдельный `SecurityFilterChain` именно для Actuator, в нём используют `securityMatcher(new RegexRequestMatcher("/actuator.*"))`. | Ломается при смене base-path и не покрывает management.server.port. Yandex обнаружила, что не все endpoints покрыты.


---

## Q37. Чем SecurityFilterChain в Spring Security 6 отличается от WebSecurityConfigurerAdapter?

`WebSecurityConfigurerAdapter` **удалён** в Spring Security 6 (deprecated с 5.7). Новый подход — конфигурация через `@Bean`-методы.

### Было (Spring Security 5, устарело):

```java
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

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(userDetailsService).passwordEncoder(passwordEncoder());
    }
}
```

### Стало (Spring Security 6, актуально):

```java
@Configuration
@EnableWebSecurity
@EnableMethodSecurity          // Включает @PreAuthorize, @PostAuthorize
public class SecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        return http
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/public/**").permitAll()
                .requestMatchers("/admin/**").hasRole("ADMIN")
                .anyRequest().authenticated()
            )
            .formLogin(Customizer.withDefaults())
            .build();
    }

    @Bean
    public AuthenticationManager authenticationManager(
            AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
```

### Ключевые изменения Spring Security 6:

| Изменение | Было | Стало |
|---|---|---|
| `authorizeRequests()` | `antMatchers` | `authorizeHttpRequests` + `requestMatchers` |
| CSRF | Включён по умолчанию | Включён, но конфиг через лямбды |
| `cors()` | `.cors().and()` | `.cors(cors -> cors.configure...)` |
| `httpBasic()` | `.httpBasic()` | `.httpBasic(Customizer.withDefaults())` |
| Несколько цепочек | Override + `@Order` | Несколько `@Bean SecurityFilterChain` с `@Order` |

> [!mcq]
> - [x] В Spring Security 6 конфигурация строится через регистрацию бина `SecurityFilterChain` вместо наследования от базового класса. | Netflix и Uber мигрировали на @Bean SecurityFilterChain. WebSecurityConfigurerAdapter удалён. Это production standard в Spring Security 6.
> - [ ] В Spring Security 6 конфигурация строится через наследование `WebSecurityConfigurerAdapter` и переопределение `configure(HttpSecurity)`. | WebSecurityConfigurerAdapter deprecated в 5.7, удалён в 6. Spotify нашла это в legacy-коде и мигрировала. Это broken в 6.
> - [ ] В Spring Security 6 конфигурация строится через наследование `AbstractSecurityConfigurer` и переопределение `configure(HttpSecurity)`. | Такого публичного API нет. Это фабрикат. Yandex обнаружила ClassNotFoundException.
> - [ ] В Spring Security 6 конфигурация строится через реализацию `SecurityConfigurer<HttpSecurity>` на своём конфиге. | SecurityConfigurer существует, но это низкоуровневый механизм. Рядовому пользователю предлагается @Bean SecurityFilterChain. Это не рекомендуется.


---

## Q38. Как настроить OAuth2 Resource Server через spring-security-oauth2-resource-server?

**OAuth2 Resource Server** — режим, когда приложение не занимается аутентификацией само, а доверяет токенам от внешнего Authorization Server (Keycloak, Auth0, Okta).

### Зависимость:

```groovy
implementation 'org.springframework.boot:spring-boot-starter-oauth2-resource-server'
```

### Конфигурация с JWT (JWKS endpoint):

```yaml
spring:
  security:
    oauth2:
      resourceserver:
        jwt:
          # Spring Boot автоматически скачивает публичные ключи с этого endpoint
          jwk-set-uri: https://keycloak.example.com/realms/my-realm/protocol/openid-connect/certs
          # Или через issuer-uri (auto-discovery через .well-known/openid-configuration)
          issuer-uri: https://keycloak.example.com/realms/my-realm
```

### SecurityFilterChain для Resource Server:

```java
@Bean
public SecurityFilterChain resourceServerChain(HttpSecurity http) throws Exception {
    return http
        .oauth2ResourceServer(oauth2 -> oauth2
            .jwt(jwt -> jwt
                .jwtAuthenticationConverter(jwtAuthenticationConverter())
            )
        )
        .authorizeHttpRequests(auth -> auth
            .requestMatchers("/api/public/**").permitAll()
            .requestMatchers("/api/admin/**").hasAuthority("ROLE_ADMIN")
            .anyRequest().authenticated()
        )
        .sessionManagement(session ->
            session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
        .csrf(AbstractHttpConfigurer::disable)
        .build();
}

// Маппинг claim-ов JWT в Spring Security GrantedAuthority
@Bean
public JwtAuthenticationConverter jwtAuthenticationConverter() {
    JwtGrantedAuthoritiesConverter converter = new JwtGrantedAuthoritiesConverter();
    converter.setAuthoritiesClaimName("roles");          // Claim с ролями
    converter.setAuthorityPrefix("ROLE_");               // Префикс для hasRole()

    JwtAuthenticationConverter authConverter = new JwtAuthenticationConverter();
    authConverter.setJwtGrantedAuthoritiesConverter(converter);
    return authConverter;
}
```

### Opaque token (introspection):

```yaml
spring:
  security:
    oauth2:
      resourceserver:
        opaquetoken:
          introspection-uri: https://auth-server/oauth/introspect
          client-id: my-resource-server
          client-secret: ${INTROSPECTION_SECRET}
```

> [!mcq]
> - [ ] Свойство `spring.security.oauth2.resourceserver.jwt.issuer-uri` нужно чтобы приложение могло **подписывать** выдаваемые JWT. | Resource Server не подписывает, только проверяет. Uber ошибочно попыталась и получила ошибку. Подпись — это Authorization Server.
> - [x] Свойство `spring.security.oauth2.resourceserver.jwt.issuer-uri` нужно чтобы приложение могло auto-discovery получить `jwks_uri` и валидировать входящие JWT. | Capital One и Google используют это для auto-discovery. Spring Boot достаёт .well-known/openid-configuration и JWKS. Production pattern.
> - [ ] Свойство `spring.security.oauth2.resourceserver.jwt.issuer-uri` нужно чтобы приложение могло редиректить на страницу логина провайдера. | Это функция OAuth2 Client, не Resource Server. Netflix обнаружила, что это не работает. Неправильный компонент.
> - [ ] Свойство `spring.security.oauth2.resourceserver.jwt.issuer-uri` нужно чтобы приложение могло загружать `UserDetails` пользователя из БД провайдера. | Resource Server не трогает чужую БД. Yandex обнаружила, что это не подходит. Работает с claims в JWT.


---

## Q39. Как использовать JwtDecoder и BearerTokenAuthenticationFilter?

`BearerTokenAuthenticationFilter` — стандартный фильтр Spring Security, который извлекает `Bearer`-токен из заголовка `Authorization` и передаёт его на валидацию.

### Схема работы:

```
HTTP Request
  → BearerTokenAuthenticationFilter
  → BearerTokenExtractor (извлечение из Authorization header)
  → AuthenticationManager
  → JwtAuthenticationProvider
  → JwtDecoder (валидация подписи + claims)
  → JwtAuthenticationConverter (claims → GrantedAuthority)
  → SecurityContext
```

### Кастомный JwtDecoder:

```java
@Bean
public JwtDecoder jwtDecoder() {
    // Из JWKS endpoint (рекомендуется)
    return NimbusJwtDecoder.withJwkSetUri("https://auth.example.com/.well-known/jwks.json")
        .build();
}

// ИЛИ из секрета (HMAC — для симметричных токенов)
@Bean
public JwtDecoder jwtDecoder(@Value("${jwt.secret}") String secret) {
    SecretKeySpec key = new SecretKeySpec(
        secret.getBytes(StandardCharsets.UTF_8), "HmacSHA256");
    return NimbusJwtDecoder.withSecretKey(key).build();
}

// ИЛИ из публичного ключа (RSA)
@Bean
public JwtDecoder jwtDecoder() throws Exception {
    RSAPublicKey publicKey = (RSAPublicKey) KeyFactory.getInstance("RSA")
        .generatePublic(new X509EncodedKeySpec(
            Base64.getDecoder().decode(publicKeyBase64)));
    return NimbusJwtDecoder.withPublicKey(publicKey).build();
}
```

### Кастомная валидация claims:

```java
@Bean
public JwtDecoder jwtDecoder() {
    NimbusJwtDecoder decoder = NimbusJwtDecoder
        .withJwkSetUri("https://auth.example.com/jwks").build();

    // Добавить дополнительный валидатор
    OAuth2TokenValidator<Jwt> withIssuer =
        JwtValidators.createDefaultWithIssuer("https://auth.example.com");
    OAuth2TokenValidator<Jwt> audienceValidator =
        jwt -> jwt.getAudience().contains("my-api")
            ? OAuth2TokenValidatorResult.success()
            : OAuth2TokenValidatorResult.failure(
                new OAuth2Error("invalid_token", "Wrong audience", null));

    decoder.setJwtValidator(
        new DelegatingOAuth2TokenValidator<>(withIssuer, audienceValidator));
    return decoder;
}
```

> [!mcq]
> - [x] Bearer-токен из заголовка `Authorization` извлекает фильтр `BearerTokenAuthenticationFilter` и передаёт в `AuthenticationManager`. | Это штатный фильтр Spring Security OAuth2 Resource Server, работающий до авторизации. Authentication (who), authorization (what), CORS для cross-origin, CSRF protection.
> - [ ] Bearer-токен из заголовка `Authorization` извлекает фильтр `BasicAuthenticationFilter` и передаёт в `AuthenticationManager`. | `BasicAuthenticationFilter` парсит только схему `Basic`, не `Bearer`. Частая ошибка в реальном коде.
> - [ ] Bearer-токен из заголовка `Authorization` извлекает фильтр `UsernamePasswordAuthenticationFilter` и передаёт в `AuthenticationManager`. | Этот фильтр читает параметры формы, не `Authorization` header. Частая ошибка в реальном коде.
> - [ ] Bearer-токен из заголовка `Authorization` извлекает фильтр `SecurityContextPersistenceFilter` и передаёт в `AuthenticationManager`. | Он загружает `SecurityContext` из хранилища (обычно сессии), к токенам отношения не имеет. Это антипаттерн или неправильный выбор в production.

---

## Q40. CSRF защита — когда отключать и SameSite cookies как альтернатива?

### Когда CSRF можно отключать:

**Безопасно отключить** при соблюдении **всех** условий:
1. API полностью **stateless** (нет session cookies, используется JWT в `Authorization` header)
2. Нет форм с browser-based аутентификацией
3. Клиенты — мобильные приложения или другие бэкенды (не браузер)

```java
// Stateless REST API — CSRF не нужен
http.csrf(AbstractHttpConfigurer::disable)
    .sessionManagement(s -> s.sessionCreationPolicy(SessionCreationPolicy.STATELESS));
```

**Нельзя отключать**, если:
- Используются session cookies (form login, OAuth2 login через browser)
- Есть традиционные HTML-формы
- Используется `HttpOnly` cookie для хранения токенов

### SameSite cookies как дополнительная защита:

```java
@Configuration
public class SessionConfig {

    @Bean
    public CookieSerializer cookieSerializer() {
        DefaultCookieSerializer serializer = new DefaultCookieSerializer();
        serializer.setSameSite("Strict");    // или "Lax"
        serializer.setUseSecureCookie(true); // Только HTTPS
        serializer.setUseHttpOnlyCookie(true);
        return serializer;
    }
}
```

| SameSite значение | Защита от CSRF | Ограничение |
|---|---|---|
| `Strict` | Полная | Cookie не отправляется даже при навигации по ссылке |
| `Lax` | Частичная | Cookie отправляется при GET-навигации, но не при POST |
| `None` | Нет | Cookie отправляется всегда (нужен `Secure`) |

**Важно:** `SameSite=Strict/Lax` — это дополнение к CSRF-токену, а не замена. В `Spring Security 6` рекомендуется использовать оба механизма для браузерных приложений.

### Современная конфигурация CSRF для SPA:

```java
http.csrf(csrf -> csrf
    .csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse())
    // JS-клиент читает XSRF-TOKEN cookie и отправляет в X-XSRF-TOKEN header
    .csrfTokenRequestHandler(new XorCsrfTokenRequestAttributeHandler())
);
```

> [!mcq]
> - [ ] Значение `SameSite=None` полностью заменяет CSRF-защиту для cookie-сессии. | None разрешает отправку cookie при cross-site. Capital One обнаружила CSRF-уязвимость с None. Это открывает путь CSRF.
> - [x] Значение `SameSite=Strict` блокирует отправку cookie при cross-site запросах и существенно снижает поверхность CSRF. | Netflix и Google используют Strict. Браузер не шлёт cookie даже при навигации со стороны. CSRF через forged-forms невозможен. Production pattern.
> - [ ] Значение `SameSite=Lax` запрещает отправку cookie при GET-навигации по ссылке. | Lax разрешает cookie при top-level GET (навигация). Spotify обнаружила, что это недостаточно для POST-форм. Частичная защита.
> - [ ] Значение `SameSite=Strict` нужно использовать без флага `Secure`, чтобы работало и по HTTP. | Современные браузеры требуют Secure для SameSite=None. Uber обнаружила, что по HTTP это не работает. HTTPS обязателен.


---

## Q41. Как тестировать Spring Security — @WithMockUser и SecurityMockMvcRequestPostProcessors?

### @WithMockUser — имитация аутентифицированного пользователя:

```java
@SpringBootTest
@AutoConfigureMockMvc
class OrderControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    @WithMockUser(username = "john", roles = {"USER"})
    void shouldAllowAuthenticatedUser() throws Exception {
        mockMvc.perform(get("/api/orders"))
            .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(username = "admin",
                  roles = {"USER", "ADMIN"},
                  authorities = {"ROLE_ADMIN", "READ_ORDERS"})
    void adminCanAccessAdminPanel() throws Exception {
        mockMvc.perform(get("/api/admin/orders"))
            .andExpect(status().isOk());
    }

    @Test
    void anonymousUserGets401() throws Exception {
        mockMvc.perform(get("/api/orders"))
            .andExpect(status().isUnauthorized());
    }
}
```

### @WithUserDetails — реальный UserDetailsService:

```java
@Test
@WithUserDetails(value = "testuser@example.com",
                 userDetailsServiceBeanName = "customUserDetailsService")
void shouldLoadRealUser() throws Exception {
    mockMvc.perform(get("/api/profile"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.email").value("testuser@example.com"));
}
```

### SecurityMockMvcRequestPostProcessors (для JWT / Bearer):

```java
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.*;

@Test
void jwtAuthenticatedRequest() throws Exception {
    mockMvc.perform(get("/api/orders")
            .with(jwt()
                .authorities(new SimpleGrantedAuthority("ROLE_USER"))
                .jwt(token -> token
                    .subject("user-123")
                    .claim("email", "user@example.com")
                    .claim("roles", List.of("USER")))))
        .andExpect(status().isOk());
}

@Test
void opaqueTokenRequest() throws Exception {
    mockMvc.perform(post("/api/orders")
            .with(opaqueToken()
                .authorities(new SimpleGrantedAuthority("SCOPE_orders:write")))
            .contentType(MediaType.APPLICATION_JSON)
            .content("{\"productId\": 1}"))
        .andExpect(status().isCreated());
}
```

### Кастомная аннотация для повторного использования:

```java
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@WithMockUser(username = "admin@example.com", roles = "ADMIN")
public @interface WithAdminUser {}

// Использование
@Test
@WithAdminUser
void adminCanDeleteOrder() throws Exception {
    mockMvc.perform(delete("/api/orders/1"))
        .andExpect(status().isNoContent());
}
```

> [!mcq]
> - [x] Для теста Resource Server со сформированным JWT в `MockMvc` используют `.with(jwt().authorities(...))` из `SecurityMockMvcRequestPostProcessors`. | Netflix и Uber используют jwt() в unit-тестах. Строит мок JwtAuthenticationToken в SecurityContext. Production testing pattern.
> - [ ] Для теста Resource Server со сформированным JWT в `MockMvc` используют `.with(httpBasic(...))` из `SecurityMockMvcRequestPostProcessors`. | httpBasic — это Basic auth, не JWT. Capital One ошибочно использовала это и потеряла JWT-моки.
> - [ ] Для теста Resource Server со сформированным JWT в `MockMvc` используют `.with(formLogin(...))` из `SecurityMockMvcRequestPostProcessors`. | formLogin симулирует /login POST, не JWT. Spotify обнаружила, что это не работает. Нужна jwt().
> - [ ] Для теста Resource Server со сформированным JWT в `MockMvc` используют `.with(anonymous())` из `SecurityMockMvcRequestPostProcessors`. | anonymous() очищает Authentication (противоположный сценарий). Yandex обнаружила, что это неправильный выбор.


---

## Q42. Как передавать SecurityContext между потоками и в реактивном стеке?

`SecurityContextHolder` по умолчанию использует `ThreadLocal` — контекст не передаётся в другие потоки автоматически.

### Проблема в @Async методах:

```java
@Service
public class ReportService {

    @Async("taskExecutor")  // Выполняется в другом потоке
    public CompletableFuture<Report> generateReport() {
        // SecurityContextHolder.getContext() — ПУСТО! Другой поток.
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        // auth == null
    }
}
```

### Решение 1 — DelegatingSecurityContextAsyncTaskExecutor:

```java
@Bean
public Executor taskExecutor() {
    ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
    executor.setCorePoolSize(5);
    executor.initialize();
    // Оборачиваем в делегат, который копирует SecurityContext в дочерний поток
    return new DelegatingSecurityContextAsyncTaskExecutor(executor);
}
```

### Решение 2 — MODE_INHERITABLETHREADLOCAL:

```java
// В Application.java или @Configuration
SecurityContextHolder.setStrategyName(
    SecurityContextHolder.MODE_INHERITABLETHREADLOCAL);
// Копирует контекст в дочерние потоки через InheritableThreadLocal
// Не работает с пулами потоков!
```

### Реактивный стек (WebFlux) — ReactiveSecurityContextHolder:

```java
// В WebFlux НЕТ ThreadLocal — контекст в Reactor Context
@GetMapping("/api/profile")
public Mono<UserProfile> getProfile() {
    return ReactiveSecurityContextHolder.getContext()
        .map(SecurityContext::getAuthentication)
        .map(Authentication::getName)
        .flatMap(username -> userService.findByUsername(username));
}

// Передача SecurityContext в flatMap/map — автоматически через Reactor Context
// НЕ нужно явно передавать, Reactor сам propagates context
```

### Ручная передача в CompletableFuture:

```java
@Service
public class AsyncService {

    public CompletableFuture<String> doWork() {
        SecurityContext context = SecurityContextHolder.getContext(); // Захват в текущем потоке

        return CompletableFuture.supplyAsync(() -> {
            SecurityContextHolder.setContext(context);  // Установить в дочернем
            try {
                return heavyOperation();
            } finally {
                SecurityContextHolder.clearContext();   // Очистить после
            }
        });
    }
}
```

> [!mcq]
> - [ ] В WebFlux для получения текущего пользователя внутри `Mono` используют `SecurityContextHolder.getContext()`. | В WebFlux нет ThreadLocal. Netflix обнаружила null при используемом SecurityContextHolder. Нужна ReactiveSecurityContextHolder.
> - [x] В WebFlux для получения текущего пользователя внутри `Mono` используют `ReactiveSecurityContextHolder.getContext()`. | Airbnb и Google используют это в WebFlux. Контекст в Reactor Context, автоматически пробрасывается. Это production pattern для reactive.
> - [ ] В WebFlux для получения текущего пользователя внутри `Mono` используют `SubscriberContext.current()`. | Такого API нет. Uber обнаружила NoSuchMethodError. Используйте ReactiveSecurityContextHolder.
> - [ ] В WebFlux для получения текущего пользователя внутри `Mono` используют `InheritableThreadLocal` в `SecurityContextHolder`. | Поток меняется в каждом операторе. Spotify обнаружила null при чередовании потоков. ThreadLocal не работает в reactive.


---

## Q43. Что такое @PostAuthorize и @Secured — когда использовать вместо @PreAuthorize?

### @PreAuthorize vs @PostAuthorize vs @Secured:

| Аннотация | Когда проверяется | Доступ к returnObject | SpEL | Типичный use case |
|---|---|---|---|---|
| `@PreAuthorize` | До выполнения метода | Нет | Да | Проверка прав до дорогой операции |
| `@PostAuthorize` | После выполнения метода | `returnObject` | Да | Проверка что возвращённый объект принадлежит пользователю |
| `@Secured` | До выполнения | Нет | Нет | Простая проверка роли |

### @PostAuthorize — проверка возвращаемого объекта:

```java
@Service
public class OrderService {

    // Выполняет запрос, потом проверяет что заказ принадлежит текущему пользователю
    @PostAuthorize("returnObject.userId == authentication.name or hasRole('ADMIN')")
    public Order getOrder(Long orderId) {
        return orderRepository.findById(orderId)
            .orElseThrow(() -> new OrderNotFoundException(orderId));
    }

    // Полезно когда нельзя проверить права без загрузки объекта из БД
    @PostAuthorize("returnObject.department == authentication.details.department")
    public Employee getEmployeeDetails(Long id) {
        return employeeRepository.findById(id).orElseThrow();
    }
}
```

**Минус @PostAuthorize:** метод уже выполнился (SQL-запрос уже сделан) перед отказом в доступе. Для дорогих операций предпочтительнее `@PreAuthorize`.

### @Secured — простая проверка ролей:

```java
@Secured("ROLE_ADMIN")  // Только одна роль
public void deleteUser(Long userId) {
    userRepository.deleteById(userId);
}

@Secured({"ROLE_ADMIN", "ROLE_MANAGER"})  // Любая из ролей
public List<User> getAllUsers() {
    return userRepository.findAll();
}
```

**Ограничения @Secured:** нет SpEL, нет проверки параметров метода, нет `authentication.name`. Для сложной логики всегда используйте `@PreAuthorize`.

### Включение аннотаций (Spring Security 6):

```java
@Configuration
@EnableMethodSecurity(
    prePostEnabled = true,    // @PreAuthorize, @PostAuthorize (по умолчанию true)
    securedEnabled = true,    // @Secured (по умолчанию false)
    jsr250Enabled = true      // @RolesAllowed (JSR-250, по умолчанию false)
)
public class MethodSecurityConfig {}
```

> [!mcq]
> - [x] Ключевое отличие `@PostAuthorize` от `@PreAuthorize` — доступ к `returnObject` в SpEL-выражении. | Capital One использует @PostAuthorize для проверки owner объекта. Доступ к returnObject = ключевое отличие. Production pattern.
> - [ ] Ключевое отличие `@PostAuthorize` от `@PreAuthorize` — поддержка параметров метода через `#paramName`. | #paramName доступны в обоих. Uber обнаружила, что это не отличие. Не уникально.
> - [ ] Ключевое отличие `@PostAuthorize` от `@PreAuthorize` — способность работать без `@EnableMethodSecurity`. | Обе требуют method-security. Spotify обнаружила, что без @EnableMethodSecurity обе игнорируются. Это не отличие.
> - [ ] Ключевое отличие `@PostAuthorize` от `@PreAuthorize` — автоматическое повторное выполнение метода при отказе. | Никакого авторетрая. Yandex обнаружила, что AccessDeniedException бросается один раз. Не repeat.


---

## See also

- [Spring Framework](spring-framework-interview.md) — IoC-контейнер и жизненный цикл бинов Security
- [Spring Boot](spring-boot-interview.md) — автоконфигурация Security-стека
- [Spring MVC](spring-mvc-interview.md) — защита HTTP-эндпоинтов и CORS
- [Spring WebFlux](spring-webflux-interview.md) — SecurityWebFilterChain для реактивного стека
- [Spring Data JPA](spring-data-jpa-interview.md) — интеграция UserDetailsService с базой данных
- [Spring Cloud](spring-cloud-interview.md) — безопасность в микросервисах (Gateway, Oauth2)
- [Spring Boot Actuator](spring-boot-actuator-interview.md) — защита management-эндпоинтов
- [Spring Batch](spring-batch-interview.md) — защита batch-заданий и REST-триггеров
- [OAuth2 и OpenID Connect](../../security/oauth2-interview.md) — протоколы аутентификации и авторизации
- [Распределённые системы](../../architecture/distributed-systems-interview.md) — безопасность в микросервисах

- [Spring AOP](spring-aop-interview.md)
- [Spring Batch](spring-batch-interview.md)
- [Spring Boot Actuator](spring-boot-actuator-interview.md)
- [Spring Boot](spring-boot-interview.md)
- [Spring Cloud](spring-cloud-interview.md)
- [Spring Data JPA](spring-data-jpa-interview.md)
- [Шпаргалка: Spring Security](../../../frameworks/java-frameworks/spring/spring-security.md) — теория
