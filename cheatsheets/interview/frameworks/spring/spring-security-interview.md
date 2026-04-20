---
title: "Вопросы на собеседовании: Spring Security"
description: "Ответы по Spring Security: SecurityFilterChain, аутентификация, авторизация, JWT, OAuth2, CORS/CSRF, method security, тестирование."
tags:
  - interview
  - frameworks
  - spring-security-interview
aliases:
  - "Spring Security"
  - "Spring Security interview"
  - "Spring Security собеседование"
  - "SecurityFilterChain"
  - "Spring OAuth2"
difficulty: "intermediate"
updated: "2026-04-13"
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
- **Защита от атак** — `CSRF`, `XSS`, clickjacking, session fixation (подробнее в [[owasp-top10-interview|OWASP Top 10]])
- **Управление сессиями** — таймауты, ограничение одновременных сессий
- **Хеширование паролей** — `BCrypt`, `Argon2`, `SCrypt`

Начиная с `Spring Security 6` конфигурация основана на бине `SecurityFilterChain` (устаревший `WebSecurityConfigurerAdapter` удалён).

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

Подробнее о паттернах авторизации — в [[authentication-authorization-patterns-interview|вопросах по паттернам аутентификации и авторизации]].

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

Для реактивного стека (`WebFlux`) используется `ReactiveSecurityContextHolder` — контекст в `Reactor Context`, не в `ThreadLocal` (подробнее в [[spring-webflux-interview|Spring WebFlux]]).

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

Для revocable-токенов лучше хранить refresh-токены в БД или `Redis` и проверять при обновлении. Подробнее об OAuth2-потоках — в [[oauth2-interview|вопросах по OAuth2]].

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

В `Spring Security 6` вместо `antMatchers()` используется `requestMatchers()` с `AntPathRequestMatcher` под капотом. Поддерживается также `MvcRequestMatcher` для точного соответствия маршрутам [[spring-mvc-interview|Spring MVC]].

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

На уровне контроллера можно использовать `@CrossOrigin`, но `SecurityFilterChain` конфигурация имеет приоритет. Подробнее о безопасности веб-приложений — в [[application-security-interview|вопросах по безопасности приложений]].

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

В `Thymeleaf` CSRF-токен подставляется автоматически при использовании `th:action`. Подробнее об атаках — в [[owasp-top10-interview|OWASP Top 10]].

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

Подробнее о конфигурации [[spring-boot-interview|Spring Boot]] и структуре контроллеров — в [[spring-mvc-interview|Spring MVC]].

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

---

## See also

- [[spring-framework-interview|Spring Framework]] — IoC-контейнер и жизненный цикл бинов Security
- [[spring-boot-interview|Spring Boot]] — автоконфигурация Security-стека
- [[spring-mvc-interview|Spring MVC]] — защита HTTP-эндпоинтов и CORS
- [[spring-webflux-interview|Spring WebFlux]] — SecurityWebFilterChain для реактивного стека
- [[spring-data-jpa-interview|Spring Data JPA]] — интеграция UserDetailsService с базой данных
- [[spring-cloud-interview|Spring Cloud]] — безопасность в микросервисах (Gateway, Oauth2)
- [[spring-boot-actuator-interview|Spring Boot Actuator]] — защита management-эндпоинтов
- [[spring-batch-interview|Spring Batch]] — защита batch-заданий и REST-триггеров
- [[oauth2-interview|OAuth2 и OpenID Connect]] — протоколы аутентификации и авторизации
- [[distributed-systems-interview|Распределённые системы]] — безопасность в микросервисах

- [[spring-aop-interview|Spring AOP]]
- [[spring-batch-interview|Spring Batch]]
- [[spring-boot-actuator-interview|Spring Boot Actuator]]
- [[spring-boot-interview|Spring Boot]]
- [[spring-cloud-interview|Spring Cloud]]
- [[spring-data-jpa-interview|Spring Data JPA]]
