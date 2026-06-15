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
updated: "2026-05-05"
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
- [Q1. (!) Что такое `Spring Security` и какие задачи он решает?](#q1--что-такое-spring-security-и-какие-задачи-он-решает)
- [Q2. (!) Как устроена архитектура фильтров `Spring Security`?](#q2--как-устроена-архитектура-фильтров-spring-security)
- [Q3. (!) В чём разница между аутентификацией и авторизацией?](#q3--в-чём-разница-между-аутентификацией-и-авторизацией)
- [Q4. (!) Как работает `SecurityFilterChain` и как его настроить?](#q4--как-работает-securityfilterchain-и-как-его-настроить)
- [Q5. Что такое `SecurityContext` и `SecurityContextHolder`?](#q5-что-такое-securitycontext-и-securitycontextholder)

**Аутентификация**
- [Q6. (!) Как работает процесс аутентификации (`AuthenticationManager`, `Provider`)?](#q6--как-работает-процесс-аутентификации-authenticationmanager-provider)
- [Q7. Как реализовать `UserDetailsService` для загрузки из БД?](#q7-как-реализовать-userdetailsservice-для-загрузки-из-бд)
- [Q8. Как настроить хеширование паролей (`PasswordEncoder`)?](#q8-как-настроить-хеширование-паролей-passwordencoder)
- [Q9. Как настроить `form-based` аутентификацию?](#q9-как-настроить-form-based-аутентификацию)
- [Q10. Как настроить `HTTP Basic` аутентификацию?](#q10-как-настроить-http-basic-аутентификацию)

**JWT и токены**
- [Q11. (!) Как интегрировать `Spring Security` с `JWT`?](#q11--как-интегрировать-spring-security-с-jwt)
- [Q12. (!) Как написать `JWT`-фильтр (`JwtAuthenticationFilter`)?](#q12--как-написать-jwt-фильтр-jwtauthenticationfilter)
- [Q13. Как реализовать endpoint выдачи `JWT`-токена?](#q13-как-реализовать-endpoint-выдачи-jwt-токена)
- [Q14. Как реализовать refresh-токен?](#q14-как-реализовать-refresh-токен)

**OAuth2 и SSO**
- [Q15. (!) Как настроить `OAuth2 Login` (вход через `Google`/`GitHub`)?](#q15--как-настроить-oauth2-login-вход-через-googlegithub)
- [Q16. Как настроить `Spring Security` как `OAuth2 Resource Server`?](#q16-как-настроить-spring-security-как-oauth2-resource-server)

**Авторизация и Method Security**
- [Q17. (!) Как настроить авторизацию по URL-паттернам?](#q17--как-настроить-авторизацию-по-url-паттернам)
- [Q18. (!) Как работают `@PreAuthorize`, `@PostAuthorize` и `@Secured`?](#q18--как-работают-preauthorize-postauthorize-и-secured)
- [Q19. Как использовать `@PreFilter` и `@PostFilter`?](#q19-как-использовать-prefilter-и-postfilter)
- [Q20. Как реализовать доступ на основе данных (domain object security)?](#q20-как-реализовать-доступ-на-основе-данных-domain-object-security)

**CORS и CSRF**
- [Q21. (!) Как настроить `CORS` в `Spring Security`?](#q21--как-настроить-cors-в-spring-security)
- [Q22. (!) Как работает `CSRF`-защита и когда её отключать?](#q22--как-работает-csrf-защита-и-когда-её-отключать)

**Сессии и Remember Me**
- [Q23. Как управлять сессиями (session management)?](#q23-как-управлять-сессиями-session-management)
- [Q24. Как настроить `Remember Me`?](#q24-как-настроить-remember-me)
- [Q25. Как обеспечить безопасность сессий в кластере?](#q25-как-обеспечить-безопасность-сессий-в-кластере)

**Обработка ошибок и безопасность REST API**
- [Q26. Как обработать ошибки аутентификации и авторизации?](#q26-как-обработать-ошибки-аутентификации-и-авторизации)
- [Q27. (!) Как защитить `REST API` с помощью `Spring Security`?](#q27--как-защитить-rest-api-с-помощью-spring-security)
- [Q28. Как настроить rate limiting?](#q28-как-настроить-rate-limiting)

**Тестирование и продвинутые темы**
- [Q29. (!) Как тестировать защищённые эндпоинты?](#q29--как-тестировать-защищённые-эндпоинты)
- [Q30. Как настроить двухфакторную аутентификацию (2FA)?](#q30-как-настроить-двухфакторную-аутентификацию-2fa)

**SecurityContext в асинхронном коде и продвинутые темы**
- [Q31. (!) Как работает `SecurityContext` в async-методах и `@Async`?](#q31--как-работает-securitycontext-в-async-методах-и-async)
- [Q32. (!) Как настроить `OAuth2 Resource Server` с `JWT` и кастомными клеймами?](#q32--как-настроить-oauth2-resource-server-с-jwt-и-кастомными-клеймами)
- [Q33. (!) Как работает `@PreAuthorize` с выражениями `SpEL` и кастомным `Permission Evaluator`?](#q33--как-работает-preauthorize-с-выражениями-spel-и-кастомным-permission-evaluator)
- [Q34. Как включить `Method Security` и в чём разница между `@PreAuthorize` и `@PostFilter`?](#q34-как-включить-method-security-и-в-чём-разница-между-preauthorize-и-postfilter)
- [Q35. Как одновременно настроить `CORS` и `CSRF` в `SecurityFilterChain`?](#q35-как-одновременно-настроить-cors-и-csrf-в-securityfilterchain)
- [Q36. Как ограничить доступ к `Actuator`-эндпоинтам через `SecurityFilterChain`?](#q36-как-ограничить-доступ-к-actuator-эндпоинтам-через-securityfilterchain)

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

`Spring Security` — фреймворк аутентификации и авторизации для `Spring`-приложений, де-факто стандарт безопасности в экосистеме. Его сила в том, что вся защита вешается декларативно — отдельным слоем фильтров поверх приложения, не размазывая проверки прав по бизнес-коду.

Что он закрывает:

- **Аутентификация** («кто ты») — форма входа, `Basic`, `JWT`, `OAuth2`, `LDAP`
- **Авторизация** («что тебе можно») — по URL-паттернам и по методам через `@PreAuthorize`
- **Защита от атак** — `CSRF`, `XSS`, clickjacking, session fixation (подробнее в [OWASP Top 10](../../security/owasp-top10-interview.md))
- **Управление сессиями** — таймауты, ограничение одновременных сессий
- **Хеширование паролей** — `BCrypt`, `Argon2`, `SCrypt`

Начиная с `Spring Security 6` конфигурация строится вокруг бина `SecurityFilterChain`; устаревший `WebSecurityConfigurerAdapter` удалён.

## Q2. (!) Как устроена архитектура фильтров `Spring Security`?

`Spring Security` встраивается в запрос как **цепочка `Servlet`-фильтров**, а не как магия внутри контроллеров. Один-единственный `Servlet`-фильтр (`DelegatingFilterProxy`) делегирует управление в `FilterChainProxy`, а тот прогоняет запрос через нужный `SecurityFilterChain`. Каждый фильтр в цепочке отвечает строго за свою задачу и передаёт запрос дальше:

Путь запроса по цепочке (каждая стрелка — передача дальше):

- `HTTP Request` → `DelegatingFilterProxy` → `FilterChainProxy` → `SecurityFilterChain`
- внутри `SecurityFilterChain` фильтры идут по порядку: `DisableEncodeUrlFilter` → `CorsFilter` → `CsrfFilter` → `LogoutFilter` → `UsernamePasswordAuthenticationFilter` → `BearerTokenAuthenticationFilter` → `ExceptionTranslationFilter` → `AuthorizationFilter`
- после прохождения цепочки запрос попадает в `DispatcherServlet`

Ключевые фильтры:

| Фильтр | Задача |
|--------|--------|
| `CorsFilter` | Обработка CORS preflight-запросов |
| `CsrfFilter` | Проверка CSRF-токена |
| `UsernamePasswordAuthenticationFilter` | Обработка form login |
| `BearerTokenAuthenticationFilter` | Аутентификация по Bearer-токену |
| `ExceptionTranslationFilter` | Перехват `AuthenticationException` и `AccessDeniedException` |
| `AuthorizationFilter` | Проверка прав доступа |

**Порядок важен:** фильтры выполняются строго по списку, поэтому, например, `CorsFilter` стоит раньше аутентификации (иначе preflight `OPTIONS` получит `401`), а `ExceptionTranslationFilter` обёрнут вокруг `AuthorizationFilter`, чтобы перехватить отказ в доступе и превратить его в `401`/`403`.

`FilterChainProxy` может содержать **несколько** `SecurityFilterChain` для разных URL-паттернов — например, stateless-цепочку для `/api/**` и сессионную для остального приложения. Подходящая цепочка выбирается по первому совпавшему `securityMatcher`.

## Q3. (!) В чём разница между аутентификацией и авторизацией?

Коротко: **аутентификация отвечает на вопрос «кто ты», авторизация — «что тебе можно».** Сначала система устанавливает личность (по логину/паролю, токену, сертификату), и только потом, зная личность и её роли, решает, пускать ли к ресурсу. Порядок всегда такой: нельзя проверить права раньше, чем установлена личность.

В `Spring Security` это два разных места в цепочке фильтров: аутентификацией занимается `AuthenticationManager`, а проверкой прав — `AuthorizationFilter`. Отсюда и разные HTTP-коды ошибок: `401 Unauthorized` — «не знаю, кто ты», `403 Forbidden` — «знаю, но тебе сюда нельзя».

| Аспект | Аутентификация | Авторизация |
|--------|---------------|-------------|
| Вопрос | «Кто ты?» | «Что тебе можно?» |
| Когда | Первая | После аутентификации |
| Данные | Логин/пароль, токен, сертификат | Роли, разрешения (authorities) |
| Результат | `Authentication` объект | Разрешение или `AccessDeniedException` |
| HTTP-код ошибки | `401 Unauthorized` | `403 Forbidden` |

По шагам, как это проходит через `Authentication Filter`, `AuthenticationManager`, `Authorization Filter` и контроллер:

1. `Client` → `Authentication Filter`: запрос с credentials.
2. `Authentication Filter` → `AuthenticationManager`: `authenticate(Authentication)`.
3. `AuthenticationManager` → `Authentication Filter`: возвращает `Authentication` (principal + authorities).
4. `Authentication Filter`: кладёт результат в контекст — `SecurityContextHolder.setContext(...)`.
5. `Authentication Filter` → `Authorization Filter`: продолжение цепочки.
6. `Authorization Filter`: проверка authorities. Дальше — ветвление:
   - **доступ разрешён** → `Authorization Filter` → `Controller`: запрос проходит к контроллеру;
   - **доступ запрещён** → `Authorization Filter` → `Client`: `403 Forbidden`.

Подробнее о паттернах авторизации — в [вопросах по паттернам аутентификации и авторизации](../../security/authentication-authorization-patterns-interview.md).

## Q4. (!) Как работает `SecurityFilterChain` и как его настроить?

`SecurityFilterChain` — центральный бин конфигурации `Spring Security 6+`, заменивший устаревший `WebSecurityConfigurerAdapter`. Вы описываете правила через `HttpSecurity` (DSL с лямбдами) и возвращаете готовую цепочку как `@Bean` — Spring сам соберёт из неё нужный набор фильтров.

Минимальная конфигурация для REST API:

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

Каждый вызов DSL включает или настраивает группу фильтров: `csrf` — `CsrfFilter`, `authorizeHttpRequests` — `AuthorizationFilter`, `httpBasic` — `BasicAuthenticationFilter` и так далее.

Когда у приложения две разные модели доступа (например, stateless API и сессионный web-UI), объявляют **несколько** `SecurityFilterChain`. Их порядок задаётся через `@Order`, а зона ответственности каждой — через `securityMatcher`. Цепочка с меньшим `@Order` проверяется первой:

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

Это пара объектов, через которую любой код узнаёт, кто выполняет текущий запрос. **`SecurityContext`** — контейнер, хранящий объект `Authentication` (текущий пользователь и его роли). **`SecurityContextHolder`** — статический хелпер, который отдаёт этот контекст из любой точки кода без проброса его через параметры.

После успешной аутентификации фильтр кладёт `Authentication` в контекст, и дальше до конца запроса любой сервис может его прочитать:

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

По умолчанию контекст живёт в `ThreadLocal`, то есть привязан к потоку, обрабатывающему запрос. Из этого вытекают стратегии хранения и их ограничения:

- **`MODE_THREADLOCAL`** (по умолчанию) — контекст в `ThreadLocal`, виден только в текущем потоке
- **`MODE_INHERITABLETHREADLOCAL`** — наследуется дочерними потоками (но не пулами, см. Q31)
- **`MODE_GLOBAL`** — один контекст на всё приложение (применяется редко, в основном в desktop-сценариях)

Привязка к `ThreadLocal` — причина, по которой контекст «теряется» в `@Async` и пулах потоков (см. Q31). Для реактивного стека (`WebFlux`) `ThreadLocal` не подходит вовсе — там используется `ReactiveSecurityContextHolder`, который держит контекст в `Reactor Context` (подробнее в [Spring WebFlux](spring-webflux-interview.md)).

## Q6. (!) Как работает процесс аутентификации (`AuthenticationManager`, `Provider`)?

Аутентификация в `Spring Security` — это конвейер с чётким разделением ответственности: фильтр собирает данные, `AuthenticationManager` оркеструет проверку, `AuthenticationProvider` проверяет конкретный тип учётных данных. Такое разделение позволяет добавить новый способ входа (например, LDAP или одноразовый код), не трогая существующие.

Поток для входа по логину/паролю:

По шагам, как это проходит через `AuthenticationFilter`, `AuthenticationManager`, `AuthenticationProvider`, `UserDetailsService` и `PasswordEncoder`:

1. `AuthenticationFilter` → `AuthenticationManager`: `authenticate(UsernamePasswordAuthenticationToken)`.
2. `AuthenticationManager` → `AuthenticationProvider`: `authenticate(token)`.
3. `AuthenticationProvider` → `UserDetailsService`: `loadUserByUsername(username)`.
4. `UserDetailsService` → `AuthenticationProvider`: возвращает `UserDetails`.
5. `AuthenticationProvider` → `PasswordEncoder`: `matches(rawPassword, encodedPassword)`.
6. `PasswordEncoder` → `AuthenticationProvider`: `true`/`false`. Дальше — ветвление:
   - **пароль верный**: `AuthenticationProvider` → `AuthenticationManager` возвращает `Authentication` (`authenticated=true`); `AuthenticationManager` → `AuthenticationFilter` отдаёт `Authentication`; `AuthenticationFilter` кладёт результат в контекст — `SecurityContextHolder.setContext(auth)`;
   - **пароль неверный**: `AuthenticationProvider` → `AuthenticationManager` бросает `BadCredentialsException`.

Роли участников:

- **`AuthenticationManager`** — точка входа (`authenticate()`); стандартная реализация `ProviderManager` перебирает провайдеры, пока один не справится
- **`AuthenticationProvider`** — проверяет конкретный тип учётных данных; `DaoAuthenticationProvider` отвечает за пару логин/пароль
- **`UserDetailsService`** — загружает пользователя по имени (обычно из БД)
- **`PasswordEncoder`** — сравнивает введённый пароль с хешем

`ProviderManager` выбирает провайдер по методу `supports()`: каждый провайдер сообщает, какой тип `Authentication` он умеет обрабатывать. Это и есть механизм расширения — свой провайдер встраивается в цепочку рядом со стандартными.

Кастомный `AuthenticationProvider` (например, проверка через внешний сервис):

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

`UserDetailsService` — это единственный метод `loadUserByUsername`, который превращает запись пользователя из вашей БД в объект `UserDetails`, понятный `Spring Security`. Реализация сводится к трём шагам: найти пользователя, смапить его роли в `GrantedAuthority`, вернуть `UserDetails` (с хешем пароля, а не с открытым паролем). Если пользователя нет — кидаем `UsernameNotFoundException`.

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

Подключение почти бесплатное: если `UserDetailsService` объявлен бином, `Spring Security` автоматически отдаст его в `DaoAuthenticationProvider`. Явная привязка нужна, только когда вы сами собираете `AuthenticationManager` (например, чтобы задать конкретный `PasswordEncoder`):

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

Пароли никогда не хранят в открытом виде — только их **необратимый хеш**, посчитанный медленным адаптивным алгоритмом. `PasswordEncoder` отвечает за два действия: захешировать пароль при регистрации (`encode`) и сравнить введённый пароль с хешем при входе (`matches`). Медленность здесь — фича: чем дороже один хеш, тем тяжелее перебор по украденной базе.

Базовая настройка — объявить бин:

```java
@Bean
public PasswordEncoder passwordEncoder() {
    return new BCryptPasswordEncoder(12); // cost factor 12
}
```

`cost factor` (work factor) задаёт число итераций: каждое увеличение на единицу удваивает время хеширования. 12 — разумный баланс между защитой и скоростью входа.

| Алгоритм | Класс | Рекомендация |
|----------|-------|-------------|
| BCrypt | `BCryptPasswordEncoder` | Рекомендован для большинства случаев |
| Argon2 | `Argon2PasswordEncoder` | Лучшая защита от GPU-атак |
| SCrypt | `SCryptPasswordEncoder` | Альтернатива Argon2 |
| PBKDF2 | `Pbkdf2PasswordEncoder` | Совместимость с FIPS |

`DelegatingPasswordEncoder` решает проблему миграции: он хранит в самом хеше префикс с именем алгоритма (`{bcrypt}…`, `{argon2}…`) и при проверке сам выбирает нужный энкодер. Это позволяет постепенно перевести базу на новый алгоритм, не ломая старые хеши:

```java
@Bean
public PasswordEncoder passwordEncoder() {
    return PasswordEncoderFactories.createDelegatingPasswordEncoder();
    // Хранит в формате: {bcrypt}$2a$12$... , {argon2}...
}
```

**Подводный камень:** хранить пароли в открытом виде (префикс `{noop}`) допустимо **только** в тестах и демках. В production это прямая утечка всех паролей при компрометации БД.

## Q9. Как настроить `form-based` аутентификацию?

Form-based вход — классическая схема для серверного web-приложения с сессиями: пользователь видит HTML-форму, отправляет логин и пароль POST-запросом, `Spring Security` создаёт сессию и кладёт в неё аутентификацию. Всё, что нужно настроить, — это блок `formLogin`, где указываются URL страницы входа, URL обработки и куда редиректить при успехе/ошибке.

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

`UsernamePasswordAuthenticationFilter` автоматически перехватывает `POST /login` и читает параметры `username` и `password` — поэтому именно так должны называться поля формы. Пример формы для `Thymeleaf`:

```html
<form th:action="@{/login}" method="post">
    <input type="text" name="username" />
    <input type="password" name="password" />
    <button type="submit">Войти</button>
</form>
```

## Q10. Как настроить `HTTP Basic` аутентификацию?

`HTTP Basic` — простейшая схема аутентификации: клиент передаёт логин и пароль в заголовке каждого запроса, без формы и без сессии. Сервер на закрытый ресурс отвечает `401` с заголовком `WWW-Authenticate`, и браузер показывает системное окно ввода. Включается одной строкой `httpBasic`.

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

Учётные данные передаются в заголовке `Authorization: Basic <base64(user:pass)>`. Ключевой момент: `base64` — это **не шифрование**, а просто кодирование, поэтому без HTTPS пароль фактически летит в открытом виде. Отсюда правила применения:

- **Когда подходит:** внутренние API, machine-to-machine, простые интеграции — только поверх HTTPS.
- **Когда не стоит:** публичные API и браузерные клиенты — там предпочтительнее `JWT` или `OAuth2`, поскольку Basic шлёт пароль с каждым запросом и не поддерживает отзыв доступа.

## Q11. (!) Как интегрировать `Spring Security` с `JWT`?

Идея JWT-аутентификации в том, чтобы сделать API **stateless**: вместо серверной сессии каждый запрос несёт самодостаточный подписанный токен, по которому сервер восстанавливает личность, ничего не храня у себя. Это упрощает горизонтальное масштабирование — любой узел кластера проверит токен сам.

Схема состоит из двух частей: эндпоинт логина выдаёт токен, а фильтр на каждом последующем запросе его проверяет и наполняет `SecurityContext`.

По шагам, как это проходит через `Client`, `AuthController`, `JwtFilter`, `SecurityContext` и `API`.

Сначала логин (выдача токена):

1. `Client` → `AuthController`: `POST /api/auth/login {username, password}`.
2. `AuthController`: проверка credentials.
3. `AuthController` → `Client`: `200 OK {accessToken, refreshToken}`.

Затем каждый последующий запрос:

1. `Client` → `JwtFilter`: `GET /api/data` (`Authorization: Bearer <token>`).
2. `JwtFilter`: парсинг и валидация JWT.
3. `JwtFilter` → `SecurityContext`: установка `Authentication`.
4. `JwtFilter` → `API`: продолжение цепочки.
5. `API` → `Client`: `200 OK {data}`.

Конфигурация `SecurityFilterChain` под JWT отличается от сессионной тремя вещами: `csrf` отключён (токен не отправляется браузером автоматически, значит CSRF не грозит), сессии — `STATELESS`, и в цепочку добавлен собственный JWT-фильтр перед стандартным `UsernamePasswordAuthenticationFilter`:

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

Задача фильтра — на каждом запросе достать токен из заголовка `Authorization`, проверить его и, если он валиден, положить аутентификацию в `SecurityContext`. Дальше по цепочке уже `AuthorizationFilter` решит, хватает ли прав. Алгоритм всегда один: нет токена — пропускаем запрос дальше как анонимный; токен есть и валиден — наполняем контекст; токен битый — тоже пропускаем (отказ выдаст authorization-слой ниже).

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

Почему именно так:

- Наследование от `OncePerRequestFilter` гарантирует ровно один вызов на запрос — иначе при forward/include фильтр сработал бы повторно.
- Регистрация **перед** `UsernamePasswordAuthenticationFilter` (через `addFilterBefore`) нужна, чтобы контекст был готов до того, как до запроса доберётся проверка прав.
- Проверка `getAuthentication() == null` не даёт перезаписать уже установленную аутентификацию.
- При невалидном или отсутствующем токене фильтр **не бросает исключение**, а просто пропускает запрос — отказать должен authorization-слой, чтобы публичные эндпоинты остались доступны анонимам.

## Q13. Как реализовать endpoint выдачи `JWT`-токена?

Эндпоинт логина делает три вещи: проверяет логин и пароль через `AuthenticationManager`, и если они верны — генерирует пару токенов и отдаёт клиенту. Короткоживущий access-токен идёт в каждый запрос, долгоживущий refresh-токен — только для обновления (см. Q14). Саму проверку учётных данных не пишут руками — её делегируют `AuthenticationManager`, который под капотом задействует `UserDetailsService` и `PasswordEncoder`.

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

Чтобы инжектить `AuthenticationManager` в контроллер, в `Spring Security 6` его нужно явно достать из `AuthenticationConfiguration` и объявить бином:

```java
@Bean
public AuthenticationManager authenticationManager(
        AuthenticationConfiguration config) throws Exception {
    return config.getAuthenticationManager();
}
```

## Q14. Как реализовать refresh-токен?

Refresh-токен решает дилемму JWT: access-токен делают короткоживущим (15 минут), чтобы ограничить ущерб от кражи, но заставлять пользователя логиниться каждые 15 минут — неприемлемо. Долгоживущий refresh-токен позволяет тихо получить новый access-токен без повторного ввода логина и пароля.

Главный компромисс — отзываемость. Выбор подхода — это выбор между простотой и контролем:

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

**Ротация** (как в примере) — важная практика: при каждом обновлении старый refresh-токен инвалидируется, и выдаётся новый. Если злоумышленник украл refresh-токен и успел им воспользоваться, при следующей легитимной попытке обновления всплывёт повторное использование — это сигнал атаки.

**Рекомендация:** для отзываемых токенов храните refresh-токены в БД или `Redis` и проверяйте их при каждом обновлении — чисто stateless JWT-refresh нельзя отозвать до истечения срока. Подробнее об OAuth2-потоках — в [вопросах по OAuth2](../../security/oauth2-interview.md).

## Q15. (!) Как настроить `OAuth2 Login` (вход через `Google`/`GitHub`)?

`OAuth2 Login` делает приложение **OAuth2-клиентом**: пользователь логинится не паролем у вас, а через внешнего провайдера (Google, GitHub), который после согласия возвращает данные о пользователе. Вы не храните и не видите его пароль — только токен и профиль. `Spring Security` реализует весь Authorization Code flow за вас: редирект к провайдеру, обмен кода на токен, загрузку профиля. Остаётся прописать `client-id`/`client-secret` и при желании — что делать с пользователем после входа.

Конфигурация регистраций провайдеров в `application.yml`:

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

Чаще всего после входа нужно завести или обновить пользователя в своей БД — для этого расширяют `DefaultOAuth2UserService`: вызывают `super.loadUser()` (он сходит к провайдеру за профилем), а затем синхронизируют локальную запись:

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

Resource Server — это роль приложения, которое **само не логинит** пользователей, а только принимает уже выданные кем-то `Bearer`-токены и проверяет их. Типичный случай: микросервис за внешним IdP (Keycloak, Auth0). Приложению достаточно знать, где взять публичные ключи провайдера, — подпись токена оно проверит локально, без обращения к IdP на каждый запрос.

Указываем `issuer-uri` (или напрямую `jwk-set-uri`) в `application.yml`:

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

`JwtAuthenticationConverter` здесь решает частую проблему: провайдер кладёт роли в свой claim (например, `roles`), а `Spring Security` по умолчанию ждёт `scope`. Конвертер переопределяет, из какого claim брать права и какой префикс добавлять (`ROLE_`), чтобы заработали `hasRole(...)`. Саму подпись токена `Spring Security` валидирует автоматически по ключам с JWKS-endpoint провайдера.

## Q17. (!) Как настроить авторизацию по URL-паттернам?

URL-авторизация — это правила вида «такой путь доступен таким ролям», задаваемые в `authorizeHttpRequests`. Это самый грубый, но самый быстрый уровень защиты: проверка идёт в фильтре, до контроллера. Правила можно различать по пути, по HTTP-методу и по конкретному authority.

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

**Главный подводный камень — порядок.** `requestMatchers` проверяются сверху вниз, и **первое совпадение выигрывает**. Если поставить `anyRequest()` или широкий паттерн раньше специфичного правила, специфичное никогда не сработает. Правило простое: от частного к общему, `anyRequest()` — всегда последним.

Различия по типу проверки:

- `hasRole("ADMIN")` ожидает authority с префиксом `ROLE_` (`ROLE_ADMIN`) и добавляет его сам.
- `hasAuthority("REPORT_VIEW")` сверяет authority дословно, без префикса.

В `Spring Security 6` вместо `antMatchers()` используется `requestMatchers()` (`AntPathRequestMatcher` под капотом). Есть также `MvcRequestMatcher` — для точного соответствия маршрутам [Spring MVC](spring-mvc-interview.md), устойчивого к нюансам вроде trailing slash.

## Q18. (!) Как работают `@PreAuthorize`, `@PostAuthorize` и `@Secured`?

Method security переносит проверку прав с уровня URL на уровень **методов сервиса** — там, где есть доступ к параметрам и результату вызова. Работает через AOP-прокси: перед (или после) вызовом метода `Spring` вычисляет условие, и если оно ложно — бросает `AccessDeniedException`. Это даёт более тонкий контроль, чем URL-правила, и защищает метод независимо от того, откуда его вызвали.

Сначала method security надо включить (`@EnableMethodSecurity` в `Spring Security 6+` заменил старый `@EnableGlobalMethodSecurity`):

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

**Как выбрать:** `@PreAuthorize` — рабочая лошадка, его SpEL покрывает почти всё (роли, параметры, поля объектов). `@PostAuthorize` берут, только когда права зависят от результата, которого до вызова ещё нет. `@Secured`/`@RolesAllowed` — для простейшей проверки роли без SpEL.

**Подводный камень AOP-прокси:** аннотация не сработает при вызове метода **изнутри того же бина** (self-invocation) — вызов идёт мимо прокси. Защищаемый метод должен вызываться извне, через бин.

## Q19. Как использовать `@PreFilter` и `@PostFilter`?

Если `@PreAuthorize`/`@PostAuthorize` принимают решение «всё или ничего» для всего вызова, то `@PreFilter` и `@PostFilter` работают тоньше — **отсеивают отдельные элементы коллекции** по доступу. `@PreFilter` чистит входную коллекцию перед вызовом метода, `@PostFilter` — возвращаемую после. Внутри выражения текущий элемент доступен как `filterObject`.

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

**Подводный камень `@PostFilter`:** метод сначала достаёт **все** записи из БД, и только потом `Spring` отсеивает лишние в памяти. На большой выборке это и лишняя нагрузка на БД, и зря потраченная память — для таких случаев фильтруйте прямо в SQL-запросе (по `WHERE owner = :user`), а не аннотацией.

## Q20. Как реализовать доступ на основе данных (domain object security)?

Domain object security — это авторизация на уровне **конкретного экземпляра**, а не типа: «пользователь может редактировать только *свои* статьи». Ролей здесь недостаточно — решение зависит от данных самого объекта (кто владелец). В `Spring Security` это решают двумя способами в порядке роста сложности.

**Вариант 1 — вызов своего бина в SpEL `@PreAuthorize`.** Самый прямой путь: выносим проверку в метод бина и зовём его из выражения через `@beanName`.

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

**Вариант 2 — `PermissionEvaluator`.** Более стандартизированный путь: реализуем единый `PermissionEvaluator` и вызываем встроенную функцию `hasPermission(...)`. Удобно, когда проверок прав на объекты много и хочется единую точку логики.

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

**Когда что брать:** вариант 1 — для точечных проверок «свой/чужой»; вариант 2 — когда таких проверок много и нужна единая точка. Для полноценных ACL-сценариев (наследование прав, разрешения на иерархии объектов) существует отдельный модуль `spring-security-acl` с таблицами разрешений в БД — но он тяжёлый, и без реальной потребности его обычно не берут.

## Q21. (!) Как настроить `CORS` в `Spring Security`?

CORS (Cross-Origin Resource Sharing) — браузерный механизм, который по умолчанию запрещает JS-коду с одного origin (`https://frontend.example.com`) делать запросы к API на другом (`https://api.example.com`). Чтобы разрешить такие запросы, сервер должен явно перечислить доверенные origin, методы и заголовки. В `Spring Security` это делается через `CorsConfigurationSource`, который подключается вызовом `cors(...)`.

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

**Ключевой нюанс с preflight.** Перед «сложным» кросс-доменным запросом браузер шлёт предварительный `OPTIONS`-запрос **без** учётных данных. Если бы он проходил аутентификацию, то получал бы `401` и реальный запрос вообще не отправлялся. Поэтому `CorsFilter` обязан стоять **до** аутентификации — при вызове `cors(...)` внутри `HttpSecurity` `Spring` расставляет фильтры в правильном порядке сам.

**Тонкости настройки:**

- `allowCredentials(true)` несовместим с `allowedOrigins("*")` — браузер отклонит ответ; для wildcard используйте `allowedOriginPatterns`.
- `exposedHeaders` нужен, чтобы JS мог прочитать нестандартные заголовки ответа (например, `X-Total-Count`).
- `maxAge` кэширует результат preflight, снижая число `OPTIONS`-запросов.

Аннотацию `@CrossOrigin` на контроллере тоже можно использовать, но конфигурация `SecurityFilterChain` имеет приоритет. Подробнее о безопасности веб-приложений — в [вопросах по безопасности приложений](../../security/application-security-interview.md).

## Q22. (!) Как работает `CSRF`-защита и когда её отключать?

CSRF (Cross-Site Request Forgery) — атака, при которой чужой сайт заставляет браузер жертвы отправить запрос к вашему приложению, используя её сессионную cookie (которую браузер прикрепляет автоматически). Защита `Spring Security` (включена по умолчанию) добавляет к state-changing запросам (`POST`, `PUT`, `DELETE`) секретный токен, которого чужой сайт знать не может — сервер сверяет его и отклоняет подделку.

**Ключевая мысль про отключение:** CSRF опасен только там, где аутентификация привязана к запросу **автоматически** — то есть к cookie. Если же клиент сам кладёт токен в заголовок `Authorization` (как с JWT), чужой сайт его подставить не сможет, и CSRF-защита становится лишней.

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

Отсюда правило: **отключаете CSRF — убедитесь, что нет cookie-based аутентификации.** В `Thymeleaf` CSRF-токен подставляется в форму автоматически при использовании `th:action`, поэтому для серверных приложений защита почти бесплатна. Подробнее об атаках — в [OWASP Top 10](../../security/owasp-top10-interview.md).

## Q23. Как управлять сессиями (session management)?

Блок `sessionManagement` управляет тем, когда создаётся HTTP-сессия и как защищаются её жизненный цикл. Три практически важных вещи: политика создания (нужна ли сессия вообще), ограничение числа одновременных сессий на пользователя и защита от **session fixation** — атаки, когда злоумышленник навязывает жертве заранее известный ID сессии.

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

`migrateSession()` при входе выдаёт новый ID сессии и переносит в неё атрибуты — старый навязанный ID становится бесполезным. Это и есть защита от session fixation.

Политики создания сессий:

| Политика | Описание |
|----------|----------|
| `ALWAYS` | Всегда создавать сессию |
| `IF_REQUIRED` | Создавать при необходимости (по умолчанию) |
| `NEVER` | Не создавать, но использовать если есть |
| `STATELESS` | Никогда (для JWT/REST API) |

Для JWT-API ставят `STATELESS` — тогда `Spring Security` не создаёт и не читает сессию вовсе, что и нужно для масштабируемого stateless-сервиса. Таймаут сессии (для сессионных приложений) настраивается в `application.yml`:

```yaml
server:
  servlet:
    session:
      timeout: 30m
```

## Q24. Как настроить `Remember Me`?

`Remember Me` оставляет пользователя залогиненным после закрытия браузера и истечения сессии — за счёт отдельной долгоживущей cookie. При следующем визите `Spring Security` восстанавливает аутентификацию по этой cookie, не спрашивая пароль. Включается блоком `rememberMe`.

Есть два механизма хранения. По умолчанию — **hash-based**: cookie содержит подписанный хеш из имени, срока и `key`. Надёжнее — **persistent tokens**: токены хранятся в БД (`JdbcTokenRepositoryImpl`), что позволяет их отзывать и ротировать.

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

Чтобы пользователь мог включить эту опцию, в форме входа нужен чекбокс с `name="remember-me"` — `Spring Security` ищет именно этот параметр. **Подводный камень безопасности:** Remember-Me снижает защиту (украденная cookie = доступ без пароля), поэтому критичные операции стоит требовать переаутентификации, даже если пользователь «запомнен».

## Q25. Как обеспечить безопасность сессий в кластере?

Проблема кластера: по умолчанию сессия живёт в памяти одного узла. Если балансировщик отправит следующий запрос на другой узел, тот о сессии ничего не знает — пользователя «разлогинит». Два решения: либо вынести сессии в общее внешнее хранилище, либо вовсе отказаться от сессий.

**Решение 1 — общее хранилище сессий через `Spring Session`** (обычно `Redis`). Все узлы читают и пишут сессии в один `Redis`, поэтому любой из них обслужит запрос:

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

`SecurityContext` сериализуется в `Redis` вместе с сессией, и при запросе на любой узел восстанавливается по cookie `SESSION`. Плюс — привычная сессионная модель работает в кластере прозрачно; минус — внешняя зависимость и сериализация на каждый запрос.

**Решение 2 — stateless-архитектура с `JWT`.** Сессий нет вообще: каждый запрос несёт самодостаточный токен, проверяемый локально на любом узле. Кластеризация сессий не нужна, но появляется своя цена — отозвать ещё не истёкший токен нельзя, поэтому для этого заводят blacklist (например, в `Redis`).

## Q26. Как обработать ошибки аутентификации и авторизации?

По умолчанию `Spring Security` отвечает на отказы HTML-страницами или редиректом на форму входа — для REST API это неудобно. Нужно подменить две точки обработки: `AuthenticationEntryPoint` срабатывает, когда пользователь **не аутентифицирован** (`401`), а `AccessDeniedHandler` — когда аутентифицирован, но **прав не хватает** (`403`). Оба подключаются через блок `exceptionHandling` и для API возвращают JSON вместо HTML.

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

**Когда что использовать:** JSON-обработчики — для REST API и SPA; для классических form-based приложений вместо них берут редирект — `failureUrl("/login?error")` при неудачном входе и `accessDeniedPage("/403")` при нехватке прав. Важно различать: ошибки на этапе фильтров перехватывает именно `ExceptionTranslationFilter`, а не `@ExceptionHandler` контроллера — до контроллера запрос с отказом просто не доходит.

## Q27. (!) Как защитить `REST API` с помощью `Spring Security`?

Защита REST API — это сборка из уже разобранных кирпичиков в одну stateless-конфигурацию. Логика такая: REST не использует cookie-сессии, поэтому CSRF отключаем, а сессии переводим в `STATELESS`; аутентификацию берёт на себя JWT-фильтр; ошибки отдаём в JSON. Ниже — типовой каркас, который дальше дополняется деталями под проект.

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

1. `CSRF` отключён — оправдано только потому, что аутентификация stateless (не cookie-based)
2. `CORS` настроен по whitelist origins, а не `*`
3. Сессии — `STATELESS`, сервер ничего не хранит между запросами
4. `JWT`-фильтр выполняет аутентификацию на каждом запросе
5. `@PreAuthorize` добавляет авторизацию на уровне методов поверх URL-правил
6. Кастомные `401`/`403` отдаются в JSON, а не HTML
7. Rate limiting — на уровне фильтра или API Gateway (см. Q28)

Подробнее о конфигурации [Spring Boot](spring-boot-interview.md) и структуре контроллеров — в [Spring MVC](spring-mvc-interview.md).

## Q28. Как настроить rate limiting?

Rate limiting ограничивает число запросов от одного клиента за интервал — защита от перебора паролей, скрейпинга и DoS. В `Spring Security` его **нет из коробки**, поэтому реализуют через собственный фильтр (обычно поверх библиотеки `Bucket4j` с алгоритмом token bucket) или выносят на уровень API Gateway.

Пример фильтра «20 запросов в минуту на IP»:

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

**Подводный камень in-memory варианта:** `ConcurrentHashMap` локален для одного узла — в кластере каждый инстанс считает запросы по отдельности, и реальный лимит умножается на число узлов. Плюс карта по IP растёт без очистки (риск утечки памяти). Поэтому для production счётчики выносят в `Redis` (распределённый лимит на весь кластер) либо используют API Gateway — например, `Spring Cloud Gateway` с фильтром `RequestRateLimiter`.

## Q29. (!) Как тестировать защищённые эндпоинты?

Тестировать безопасность нужно так же, как бизнес-логику: с моком текущего пользователя, без реального логина. Модуль `spring-security-test` даёт два подхода — аннотации (`@WithMockUser` ставит фиктивную аутентификацию в контекст до выполнения теста) и request-post-processors (`.with(jwt())`, `.with(csrf())` — добавляют аутентификацию или CSRF-токен прямо в конкретный `MockMvc`-запрос). Хороший набор тестов покрывает три исхода: успех с нужной ролью, `403` с недостаточной и `401` для анонима.

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

**Частый промах:** для POST/PUT/DELETE в form-based приложении тест без `.with(csrf())` получит `403`, потому что CSRF-токен включён по умолчанию. Для интеграционных тестов с `@SpringBootTest` и `TestRestTemplate`/`WebTestClient` мок-аутентификация не работает — там нужны реальные токены или мок `JwtDecoder`.

## Q30. Как настроить двухфакторную аутентификацию (2FA)?

2FA добавляет второй фактор поверх пароля — обычно одноразовый код TOTP из приложения-аутентификатора. Встроенного «2FA из коробки» в `Spring Security` нет, поэтому вход разбивают на два шага: первый проверяет логин/пароль и выдаёт **частичную** аутентификацию (полного доступа ещё нет), второй проверяет TOTP-код и только тогда выдаёт полноценный токен. Реализуют это кастомным `AuthenticationProvider` плюс отдельным эндпоинтом проверки кода.

Шаг 1 — проверка пароля, возврат частичной аутентификации:

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

Шаг 2 — проверка TOTP-кода и выдача полноценного токена:

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

Сам TOTP не пишут руками — берут библиотеку (`dev.samstevens.totp` или `com.warrenstrange:googleauth`). При подключении 2FA пользователь сканирует QR-код в Google Authenticator, Authy или аналоге; дальше приложение и сервер независимо считают один и тот же код от общего секрета и текущего времени. Дополнительно стоит выдавать backup-коды — на случай потери устройства.

## Q31. (!) Как работает `SecurityContext` в async-методах и `@Async`?

Корень проблемы: `SecurityContextHolder` по умолчанию хранит контекст в `ThreadLocal`, то есть привязывает его к потоку, обрабатывающему запрос. Метод с `@Async` выполняется в **другом** потоке из пула, у которого свой пустой `ThreadLocal` — поэтому `SecurityContextHolder.getContext()` там вернёт анонимный контекст, и `auth` будет `null`. Решений три, с разными компромиссами.

**Решение 1 — стратегия `MODE_INHERITABLETHREADLOCAL`.** Контекст автоматически копируется в дочерние потоки:

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

Подвох: `InheritableThreadLocal` копирует контекст лишь в **момент создания** дочернего потока. А пул потоков (`ThreadPoolExecutor`) потоки переиспользует, а не создаёт заново на каждую задачу, — поэтому с пулами эта стратегия фактически не работает. Годится только для «сырого» `new Thread()`.

**Решение 2 — `DelegatingSecurityContextAsyncTaskExecutor` (рекомендуется).** Обёртка над executor'ом, которая захватывает контекст в вызывающем потоке и переустанавливает его в рабочем перед каждой задачей — поэтому корректно работает с пулами:

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

**Решение 3 — явная передача контекста.** Самый ручной способ: захватить контекст в вызывающем потоке, передать параметром и установить в рабочем (обязательно очистив в `finally`, чтобы не «протёк» в переиспользуемый поток пула). Работает всегда, но добавляет boilerplate:

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

`OAuth2 Resource Server` — приложение, которое само не логинит пользователей, а принимает уже выданные `Bearer`-токены и проверяет их по ключам Authorization Server. `Spring Security` даёт готовую интеграцию: вы указываете, где брать ключи, и опционально — как маппить claim'ы токена в права. Этот вопрос углубляет тему кастомизацией: свой `JwtAuthenticationConverter` (откуда брать роли) и кастомный `JwtDecoder` (дополнительная валидация claim'ов вроде `audience`).

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

**Кастомный `JwtDecoder` с дополнительной валидацией.** По умолчанию `Spring` проверяет подпись, срок и issuer. Но часто нужно убедиться, что токен предназначен именно вашему API — это claim `audience`. Свой валидатор отклонит чужой токен, даже если он валидно подписан тем же IdP:

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

**Доступ к claim'ам JWT в контроллере.** После валидации сам токен доступен через `@AuthenticationPrincipal Jwt` — из него можно читать любые claim'ы (subject, email, кастомные поля):

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

`@PreAuthorize` принимает `SpEL`-выражение, которое вычисляется **до** вызова метода: вернёт `false` — метод не выполнится, полетит `AccessDeniedException`. Сила в том, что выражению доступны и контекст безопасности, и параметры метода. Встроенные обращения: `authentication` и `principal` (текущий пользователь), `hasRole()`/`hasAuthority()` (проверка прав), `#paramName` (значение аргумента метода), `hasPermission()` (вызов вашего `PermissionEvaluator`).

**Базовые примеры** — от простой проверки роли до сверки параметра с текущим пользователем:

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

**Кастомный `PermissionEvaluator` для domain object security.** Когда логика прав сложнее, чем «свой/чужой», её выносят в `PermissionEvaluator` — единую точку, которую вызывает `hasPermission()`. Он реализует две перегрузки: по уже загруженному объекту и по его id с типом (чтобы достать объект самому):

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

**Регистрация `PermissionEvaluator`.** Чтобы `hasPermission()` в аннотациях заработал, evaluator нужно подсунуть в `MethodSecurityExpressionHandler`:

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

Method security включается одной аннотацией `@EnableMethodSecurity`, у которой есть флаги под разные семейства аннотаций. Ключевое различие между четырьмя аннотациями — **момент** (до или после вызова метода) и **что они делают** с вызовом: `@PreAuthorize`/`@PostAuthorize` решают «пускать или нет», а `@PreFilter`/`@PostFilter` отсеивают элементы коллекции.

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

CORS и CSRF часто путают, но решают они разные задачи: CORS **разрешает** легитимные кросс-доменные запросы фронтенда, CSRF — **запрещает** поддельные запросы с чужих сайтов. В смешанном приложении (REST API + браузерные формы) их настраивают вместе, и здесь важны два правила: CORS должен быть сконфигурирован до CSRF, а preflight `OPTIONS` — всегда `permitAll`, иначе фронтенд не достучится.

**Полная конфигурация CORS + CSRF:**

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

**Паттерн `Double Submit Cookie`** — стандартный способ отдать CSRF-токен в SPA. `Spring` кладёт токен в cookie `XSRF-TOKEN` (с `withHttpOnlyFalse`, чтобы JS мог его прочитать), фронтенд читает его и дублирует в заголовок `X-XSRF-TOKEN`. Сервер сверяет cookie и заголовок: чужой сайт не сможет прочитать cookie из-за same-origin policy, значит и подставить верный заголовок не сможет.

```java
// Frontend отправляет CSRF-токен в заголовке X-XSRF-TOKEN
// Spring Security сравнивает его с cookie XSRF-TOKEN
.csrf(csrf -> csrf.csrfTokenRepository(
    CookieCsrfTokenRepository.withHttpOnlyFalse()
))
```

## Q36. Как ограничить доступ к `Actuator`-эндпоинтам через `SecurityFilterChain`?

Actuator-эндпоинты (`/env`, `/heapdump`, `/loggers`, `/shutdown`) раскрывают конфигурацию, секреты и дают управление приложением — оставить их открытыми в production равносильно дыре в безопасности. Правильный подход: вынести Actuator в **отдельный** `SecurityFilterChain` с более высоким `@Order`, открыть анонимам только `health`/`info` (для k8s-проб), а остальное закрыть ролью. Матчинг — через `EndpointRequest`, а не вручную по путям, чтобы не зависеть от base-path.

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

Главное изменение: `WebSecurityConfigurerAdapter` **удалён** в Spring Security 6 (deprecated с 5.7). Раньше конфигурацию писали, **наследуясь** от адаптера и переопределяя методы `configure(...)`; теперь — **объявляя бины** (`SecurityFilterChain`, `AuthenticationManager`, `PasswordEncoder`). Это переход от наследования к композиции: вместо одного класса-наследника вы собираете конфигурацию из независимых бинов, которые проще тестировать и комбинировать.

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

**OAuth2 Resource Server** — режим, когда приложение не логинит пользователей само, а доверяет токенам от внешнего Authorization Server (Keycloak, Auth0, Okta). Стартер делает почти всё за вас: достаточно указать, где брать публичные ключи, и `Spring` сам подключит `BearerTokenAuthenticationFilter`, скачает ключи и будет валидировать подпись токенов. Поддерживаются два типа токенов — самодостаточный JWT (валидация локально по ключам) и opaque-токен (валидация через introspection-запрос к серверу).

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

Opaque-токен — это непрозрачная строка без полезной нагрузки внутри. Проверить его локально нельзя, поэтому Resource Server на каждый запрос обращается к introspection-эндпоинту Authorization Server, который и сообщает, валиден ли токен и какие у него права. Плюс — токен можно отозвать мгновенно; минус — сетевой вызов на каждый запрос:

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

Это два звена одной цепочки на стороне Resource Server. `BearerTokenAuthenticationFilter` — точка входа: он достаёт `Bearer`-токен из заголовка `Authorization` и запускает аутентификацию. `JwtDecoder` — рабочая лошадка проверки: он парсит токен, проверяет подпись и claim'ы, превращая строку в объект `Jwt`. Вы редко трогаете фильтр напрямую, но часто настраиваете `JwtDecoder` — выбираете источник ключей (JWKS, секрет, публичный ключ) и добавляете свои валидаторы.

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

Источник ключей зависит от того, как подписан токен. **JWKS-endpoint** (асимметричный RSA, ключи раздаёт IdP) — рекомендуемый вариант: ротация ключей подхватывается автоматически. **Секрет** (симметричный HMAC) — когда вы сами и выпускаете, и проверяете токены. **Публичный ключ** (RSA напрямую) — когда JWKS недоступен, а ключ зашит в конфигурацию:

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

Короткое правило: **CSRF нужен ровно там, где аутентификация привязана к запросу автоматически — то есть к cookie.** Если же токен кладётся в заголовок вручную (JWT), подделать запрос с чужого сайта нельзя, и защиту можно отключить. `SameSite`-cookie — это не замена CSRF-токену, а второй рубеж: атрибут говорит браузеру не отправлять cookie в кросс-доменных запросах, что само по себе срезает большинство CSRF-векторов.

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

Модуль `spring-security-test` даёт два способа подменить текущего пользователя без реального логина, и они дополняют друг друга:

- **Аннотации** (`@WithMockUser`, `@WithUserDetails`) — ставят аутентификацию в `SecurityContext` на весь тестовый метод. `@WithMockUser` создаёт фиктивного пользователя из переданных роли/authorities, `@WithUserDetails` грузит реального через ваш `UserDetailsService`.
- **Request-post-processors** (`.with(jwt())`, `.with(opaqueToken())`, `.with(csrf())`) — добавляют аутентификацию или CSRF-токен в конкретный `MockMvc`-запрос. Незаменимы для тестов Resource Server (JWT/opaque-токены), где контекст задаётся самим токеном.

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

Чтобы не повторять один и тот же `@WithMockUser(...)` в десятках тестов, его оборачивают в собственную мета-аннотацию — получается читаемый и переиспользуемый `@WithAdminUser`:

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

Корень всех проблем — `ThreadLocal`: `SecurityContextHolder` привязывает контекст к потоку, поэтому он **не передаётся** в другие потоки автоматически. Это проявляется в трёх ситуациях с разными решениями: `@Async`-методы (другой поток пула), ручные `CompletableFuture` (тоже чужой поток) и реактивный `WebFlux`, где `ThreadLocal` неприменим в принципе и используется `Reactor Context`.

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

`@PreAuthorize` — выбор по умолчанию, остальные две аннотации берут в конкретных случаях. `@PostAuthorize` нужен, когда права зависят от **результата**, которого до вызова ещё нет (например, проверить владельца загруженного из БД заказа). `@Secured` — для простейшей проверки роли без SpEL, ради лаконичности. Главный минус `@PostAuthorize`: метод **уже выполнился** (включая запрос к БД) к моменту отказа, поэтому для дорогих операций он хуже `@PreAuthorize`.

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
