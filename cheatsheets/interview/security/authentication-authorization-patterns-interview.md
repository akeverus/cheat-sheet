---
title: "Вопросы на собеседовании: Authentication and Authorization Patterns"
description: "Комплексное руководство по паттернам аутентификации и авторизации: OAuth 2.0, JWT, RBAC, ABAC, OIDC, PKCE, mTLS, Zero Trust"
tags:
  - interview
  - security
  - authentication-authorization-patterns-interview
type: "interview"
difficulty: "intermediate"
aliases:
  - "Вопросы на собеседовании"
  - "Auth patterns interview"
  - "JWT RBAC ABAC interview"
  - "OAuth2 OIDC паттерны"
prerequisites: []
next: []
updated: "2026-05-08"
---
# Вопросы на собеседовании: `Authentication` and `Authorization Patterns`

Комплексное руководство по вопросам собеседования на тему паттернов аутентификации и авторизации
для `Senior Java Developer`. Включает детальные объяснения концепций, практические примеры на `Java` + `Spring Security`,
пошаговые разборы потоков, best practices и типичные ошибки.

## Полезные ссылки

### Официальная документация

- [OAuth 2.0 Authorization Framework](https://oauth.net/2/)
- [JWT (RFC 7519)](https://datatracker.ietf.org/doc/html/rfc7519)
- [PKCE (RFC 7636)](https://datatracker.ietf.org/doc/html/rfc7636)
- [Spring Security Documentation](https://docs.spring.io/spring-security/reference/)
- [Keycloak Documentation](https://www.keycloak.org/documentation)
- [Spring Security — Roles and Privileges (Baeldung)](https://www.baeldung.com/role-and-privilege-for-spring-security-registration)
- [Spring Security Method Security (Baeldung)](https://www.baeldung.com/spring-security-method-security)
- [OAuth 2.0 Resource Server (Baeldung)](https://www.baeldung.com/spring-security-oauth-resource-server)
- [Access Control Models (Baeldung)](https://www.baeldung.com/java-access-control-models)

## Содержание

- [Полезные ссылки](#полезные-ссылки)
- [See also](#see-also)

**Основы аутентификации**
- [Q1. (!) Какие основные паттерны аутентификации существуют?](#q1--какие-основные-паттерны-аутентификации-существуют)
- [Q2. (!) Что такое `OAuth 2.0` и как он работает?](#q2--что-такое-oauth-20-и-как-он-работает)
- [Q3. (!) Как работает `JWT` аутентификация?](#q3--как-работает-jwt-аутентификация)
- [Q4. (!) Что такое `RBAC` и `ABAC`?](#q4--что-такое-rbac-и-abac)
- [Q5. В чём разница между аутентификацией и авторизацией?](#q5-в-чём-разница-между-аутентификацией-и-авторизацией)

**Безопасность в распределённых системах**
- [Q6. (!) Как реализовать безопасность в микросервисах?](#q6--как-реализовать-безопасность-в-микросервисах)
- [Q7. Что такое `SAML` и когда его использовать?](#q7-что-такое-saml-и-когда-его-использовать)
- [Q8. (!) Как реализовать `Multi-Factor Authentication`?](#q8--как-реализовать-multi-factor-authentication)
- [Q9. Какие паттерны сессионного управления существуют?](#q9-какие-паттерны-сессионного-управления-существуют)
- [Q10. Как защититься от распространённых атак на аутентификацию?](#q10-как-защититься-от-распространённых-атак-на-аутентификацию)
- [Q11. Как реализовать `API Gateway Security`?](#q11-как-реализовать-api-gateway-security)

**SSO и Identity Federation**
- [Q12. (!) Что такое `OpenID Connect` и как он расширяет `OAuth 2.0`?](#q12--что-такое-openid-connect-и-как-он-расширяет-oauth-20)
- [Q13. Как реализовать `Single Sign-On` (`SSO`)?](#q13-как-реализовать-single-sign-on-sso)
- [Q14. Что такое `Claims-based` аутентификация?](#q14-что-такое-claims-based-аутентификация)
- [Q15. (!) Как обеспечить безопасность токенов (`JWT` refresh, rotation)?](#q15--как-обеспечить-безопасность-токенов-jwt-refresh-rotation)
- [Q16. Что такое `Zero Trust Security Model`?](#q16-что-такое-zero-trust-security-model)

**Защита API и Rate Limiting**
- [Q17. Как реализовать `Rate Limiting` для защиты `API`?](#q17-как-реализовать-rate-limiting-для-защиты-api)
- [Q18. (!) Что такое `mTLS` и когда его использовать?](#q18--что-такое-mtls-и-когда-его-использовать)
- [Q19. Как реализовать аудит и логирование событий безопасности?](#q19-как-реализовать-аудит-и-логирование-событий-безопасности)
- [Q20. Что такое `Context-based Access Control`?](#q20-что-такое-context-based-access-control)
- [Q21. Как обеспечить безопасность в `Service Mesh`?](#q21-как-обеспечить-безопасность-в-service-mesh)

**Продвинутые паттерны авторизации**
- [Q22. Что такое `Identity Federation`?](#q22-что-такое-identity-federation)
- [Q23. Как реализовать `Step-Up Authentication`?](#q23-как-реализовать-step-up-authentication)
- [Q24. Что такое `Passwordless Authentication`?](#q24-что-такое-passwordless-authentication)
- [Q25. Как защитить `GraphQL API`?](#q25-как-защитить-graphql-api)
- [Q26. Что такое `Delegated Authorization`?](#q26-что-такое-delegated-authorization)
- [Q27. (!) Как реализовать `Fine-Grained Authorization`?](#q27--как-реализовать-fine-grained-authorization)
- [Q28. Что такое `Token Binding` и зачем он нужен?](#q28-что-такое-token-binding-и-зачем-он-нужен)
- [Q29. Как обеспечить безопасность `WebSocket` соединений?](#q29-как-обеспечить-безопасность-websocket-соединений)
- [Q30. (!) Что такое `Proof Key for Code Exchange` (`PKCE`)?](#q30--что-такое-proof-key-for-code-exchange-pkce)
- [Q31. Как реализовать `Dynamic Authorization`?](#q31-как-реализовать-dynamic-authorization)

**Spring Security и практические паттерны**
- [Q32. (!) Как настроить `Spring Security` как `OAuth2 Resource Server`?](#q32--как-настроить-spring-security-как-oauth2-resource-server)
- [Q33. Как реализовать кастомный `PermissionEvaluator` в `Spring Security`?](#q33-как-реализовать-кастомный-permissionevaluator-в-spring-security)
- [Q34. Как работает `SecurityFilterChain` в `Spring Security 6`?](#q34-как-работает-securityfilterchain-в-spring-security-6)
- [Q35. Как реализовать иерархию ролей в `Spring Security`?](#q35-как-реализовать-иерархию-ролей-в-spring-security)
- [Q36. Как хранить пароли безопасно в `Java`?](#q36-как-хранить-пароли-безопасно-в-java)
- [Q37. (!) Какие типичные ошибки при реализации `JWT`?](#q37--какие-типичные-ошибки-при-реализации-jwt)
- [Q38. Как реализовать `OAuth2 Backend for Frontend` (`BFF`) паттерн?](#q38-как-реализовать-oauth2-backend-for-frontend-bff-паттерн)
- [Q39. Как интегрировать `Keycloak` со `Spring Boot`?](#q39-как-интегрировать-keycloak-со-spring-boot)
- [Q40. Как тестировать безопасность в `Spring`-приложении?](#q40-как-тестировать-безопасность-в-spring-приложении)

**Продвинутые паттерны**
- [Q41. (!) Чем `JWT` отличается от `Session`-based аутентификации: когда что выбирать?](#q41--чем-jwt-отличается-от-session-based-аутентификации-когда-что-выбирать)
- [Q42. Как реализовать `API Key` аутентификацию в `Spring Security`?](#q42-как-реализовать-api-key-аутентификацию-в-spring-security)
- [Q43. (!) Что такое `Zero Trust` и как реализовать его принципы в Java-приложении?](#q43--что-такое-zero-trust-и-как-реализовать-его-принципы-в-java-приложении)
- [Q44. Как реализовать `RBAC` и `ABAC` совместно в `Spring Security`?](#q44-как-реализовать-rbac-и-abac-совместно-в-spring-security)
- [Q45. (!) Как реализовать `mTLS` аутентификацию между микросервисами?](#q45--как-реализовать-mtls-аутентификацию-между-микросервисами)

## Q1. (!) Какие основные паттерны аутентификации существуют?

Четыре базовых паттерна — `Basic`, `Session-Based`, `Token-Based` и `Certificate-Based`. Они отличаются тем, **где живёт состояние сессии** (на сервере или у клиента) и **что предъявляет клиент** (пароль, cookie, токен или сертификат). Это и определяет выбор: монолиту с браузером подходит session, микросервисам и `SPA` — токены, межсервисному трафику — сертификаты.

Четыре паттерна и что именно предъявляет клиент:

- **Basic Auth** → `HTTP`-заголовок `username:password`
- **Session-Based** → серверная сессия + cookie
- **Token-Based** → `JWT` / `OAuth`, stateless
- **Certificate-Based** → `X.509`-сертификаты, `mTLS`

### 1. `Basic Authentication`

Клиент отправляет `username:password` в заголовке `Authorization` в виде `Base64`. Важно: `Base64` — это **кодирование, а не шифрование**, поэтому без `HTTPS` пароль уходит фактически открытым текстом в каждом запросе.

```java
@Configuration
public class BasicAuthConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .authorizeHttpRequests(auth -> auth.anyRequest().authenticated())
            .httpBasic(Customizer.withDefaults());
        return http.build();
    }

    @Bean
    public UserDetailsService userDetailsService() {
        UserDetails user = User.withDefaultPasswordEncoder()
            .username("user")
            .password("password")
            .roles("USER")
            .build();
        return new InMemoryUserDetailsManager(user);
    }
}
```

**Плюсы:** простота, встроенная поддержка в `HTTP` и любом клиенте.
**Минусы:** небезопасно без `HTTPS`, нет logout (нечего «забыть»), пароль передаётся в каждом запросе.

### 2. `Session-Based Authentication`

Состояние хранится на сервере. После успешного логина сервер создаёт сессию и отдаёт клиенту только её идентификатор — `JSESSIONID` в cookie. В каждом следующем запросе браузер автоматически шлёт эту cookie, а сервер по ней находит сессию.

```java
@Controller
public class LoginController {

    @PostMapping("/login")
    public String login(@ModelAttribute LoginForm form,
                        HttpSession session,
                        RedirectAttributes redirectAttrs) {
        User user = userService.authenticate(form.getUsername(), form.getPassword());
        if (user != null) {
            session.setAttribute("user", user);
            session.setMaxInactiveInterval(30 * 60); // 30 минут
            return "redirect:/dashboard";
        }
        redirectAttrs.addFlashAttribute("error", "Invalid credentials");
        return "redirect:/login";
    }

    @PostMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/login";
    }
}
```

**Плюсы:** сервер полностью контролирует сессию, поэтому отзыв (logout) мгновенный; чувствительные данные не покидают сервер.
**Минусы:** состояние на сервере мешает горизонтальному масштабированию — нужны `sticky sessions` или общее хранилище (`Redis`); поскольку аутентификация держится на cookie, появляется уязвимость к `CSRF`.

### 3. `Token-Based Authentication`

`stateless`-подход: после логина сервер выдаёт подписанный токен (`JWT`, `OAuth`), и клиент сам носит его в каждом запросе. Сервер ничего не хранит — он лишь проверяет подпись токена, поэтому любой инстанс обработает запрос без общего хранилища сессий. Это и делает подход идеальным для [микросервисов](../architecture/microservices-interview.md) и `SPA`.

```java
@Service
public class TokenAuthenticationService {

    private final JwtService jwtService;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public String authenticateAndGenerateToken(String username, String password) {
        User user = userRepository.findByUsername(username)
            .orElseThrow(() -> new BadCredentialsException("User not found"));

        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new BadCredentialsException("Invalid password");
        }
        return jwtService.generateToken(user);
    }
}
```

**Плюсы:** `stateless` и легко масштабируется, отлично ложится на микросервисы.
**Минусы:** обратная сторона stateless — токен нельзя отозвать до истечения `exp` (нужен blacklist или короткий TTL); запросы тяжелее, чем cookie.

### 4. `Certificate-Based Authentication`

Стороны аутентифицируют друг друга по цифровым `X.509`-сертификатам, а не по паролям. Это основа `mTLS` между сервисами: клиент тоже предъявляет сертификат, и сервер проверяет его по доверенному `CA` (подробнее в [Spring Security](../frameworks/spring/spring-security-interview.md)).

```java
@Configuration
public class CertificateAuthConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .authorizeHttpRequests(auth -> auth.anyRequest().authenticated())
            .x509(x509 -> x509
                .subjectPrincipalRegex("CN=(.*?)(?:,|$)")
                .userDetailsService(x509UserDetailsService()));
        return http.build();
    }
}
```

**Плюсы:** высокая безопасность, взаимная (двусторонняя) аутентификация, паролей нет вовсе — нечего фишить или перебирать.
**Минусы:** требуется полноценная `PKI` (выпуск, ротация, отзыв сертификатов); неудобно для мобильных устройств и браузеров конечных пользователей.

## Q2. (!) Что такое `OAuth 2.0` и как он работает?

`OAuth 2.0` — протокол **делегированной авторизации**: приложение получает ограниченный доступ к ресурсам пользователя, не видя его пароль. Классический пример — «войти через Google»: вы вводите пароль только на стороне Google, а стороннее приложение получает лишь токен с нужными правами. Ключевая идея — отделить того, кто владеет данными, от того, кто их запрашивает. Детальный разбор — в [вопросах по OAuth 2.0](oauth2-interview.md).

### Роли в `OAuth 2.0`

| Роль | Описание |
|------|----------|
| `Resource Owner` | Пользователь, владелец данных |
| `Client` | Приложение, запрашивающее доступ |
| `Authorization Server` | Сервер, выдающий токены |
| `Resource Server` | Сервер, хранящий защищённые ресурсы |

### `Authorization Code Grant` — основной поток

Это самый безопасный поток для веб-приложений с backend. Суть: пользователь логинится **напрямую на Authorization Server**, а приложение получает сначала одноразовый `code` (через redirect в браузере), и только потом обменивает его на токены — уже по защищённому server-to-server запросу со своим `client_secret`. За счёт этого `access token` никогда не проходит через адресную строку браузера, где его мог бы перехватить.

Участники: `User (Browser)`, `Client App`, `Authorization Server` (`AS`), `Resource Server` (`RS`). По шагам:

1. `User → Client App`: запрос защищённого ресурса.
2. `Client App → AS`: redirect на `/authorize`.
3. `AS → User`: страница входа + consent.
4. `User → AS`: логин + согласие.
5. `AS → Client App`: `Authorization Code` (через redirect).
6. `Client App → AS`: `POST /token` (`code` + `client_secret`).
7. `AS → Client App`: `Access Token` + `Refresh Token`.
8. `Client App → RS`: `GET /resource` (`Bearer token`).
9. `RS → Client App`: данные.

### Реализация в `Spring Boot`

```java
@RestController
@RequestMapping("/oauth")
public class OAuthController {

    @GetMapping("/login")
    public String login() {
        return "redirect:/oauth2/authorization/google";
    }

    @GetMapping("/user")
    public Map<String, Object> user(@AuthenticationPrincipal OAuth2User principal) {
        return principal.getAttributes();
    }
}
```

### `Grant Types`

| Тип | Сценарий | Статус |
|-----|----------|--------|
| `Authorization Code` | Веб-приложения с backend | Рекомендуется |
| `Authorization Code + PKCE` | `SPA`, мобильные | Рекомендуется |
| `Client Credentials` | Сервис-сервис | Рекомендуется |
| `Implicit` | `SPA` (устарел) | Deprecated |
| `Resource Owner Password` | Доверенные клиенты | Не рекомендуется |

### `Client Credentials Grant` — для межсервисного взаимодействия

Здесь нет пользователя вообще: сервис аутентифицируется сам по `client_id` + `client_secret` и получает токен от своего имени. Применяется для backend-to-backend вызовов (cron-задачи, межсервисные запросы), где некому показывать `consent screen`.

```java
@Service
public class ServiceAuthenticationService {

    private final RestClient restClient;

    public String getServiceToken() {
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("grant_type", "client_credentials");
        params.add("scope", "service");

        Map<String, Object> response = restClient.post()
            .uri("http://auth-server/oauth2/token")
            .headers(h -> h.setBasicAuth("service-client", "service-secret"))
            .contentType(MediaType.APPLICATION_FORM_URLENCODED)
            .body(params)
            .retrieve()
            .body(new ParameterizedTypeReference<>() {});

        return (String) response.get("access_token");
    }
}
```

## Q3. (!) Как работает `JWT` аутентификация?

`JWT` (`JSON Web Token`) — компактный URL-safe токен, который несёт **claims** (утверждения о пользователе) и **подписан** выпускающей стороной (`RFC 7519`). Главная ценность: получатель проверяет подпись локально и доверяет содержимому без обращения к БД или auth-серверу. Это и делает `JWT` stateless — за что приходится платить тем, что отозвать уже выпущенный токен до истечения `exp` нельзя.

### Структура `JWT`

Три части, разделённые точками: `header.payload.signature`. Первые две — это просто `Base64url` от JSON (их **может прочитать кто угодно**, шифрования нет), а третья — подпись от `header.payload` секретом. Подпись гарантирует **целостность**: изменить payload, не зная ключа, нельзя.

Из чего складывается токен `header.payload.signature` (части разделены точками `.`):

- **Header** (`alg`, `typ`) → `Base64url` → `eyJhbGci...`
- **Payload** (`sub`, `exp`, `roles`) → `Base64url` → `eyJzdWIi...`
- **Signature** = `HMAC(header.payload, secret)` → `SflKxwRJ...`

**Header:**
```json
{ "alg": "RS256", "typ": "JWT" }
```

**Payload:**
```json
{
  "sub": "user123",
  "name": "John Doe",
  "roles": ["USER", "ADMIN"],
  "iat": 1516239022,
  "exp": 1516242622
}
```

### Реализация `JWT` в `Java`

```java
@Component
public class JwtUtil {

    private final SecretKey secretKey;

    public JwtUtil(@Value("${jwt.secret}") String secret) {
        this.secretKey = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
    }

    public String generateToken(User user) {
        return Jwts.builder()
            .subject(user.getUsername())
            .claim("roles", user.getRoles())
            .issuedAt(new Date())
            .expiration(new Date(System.currentTimeMillis() + 10 * 60 * 60 * 1000))
            .signWith(secretKey)
            .compact();
    }

    public boolean validateToken(String token, UserDetails userDetails) {
        String username = extractUsername(token);
        return username.equals(userDetails.getUsername()) && !isTokenExpired(token);
    }

    public String extractUsername(String token) {
        return Jwts.parser()
            .verifyWith(secretKey)
            .build()
            .parseSignedClaims(token)
            .getPayload()
            .getSubject();
    }

    private boolean isTokenExpired(String token) {
        Date expiration = Jwts.parser()
            .verifyWith(secretKey)
            .build()
            .parseSignedClaims(token)
            .getPayload()
            .getExpiration();
        return expiration.before(new Date());
    }
}
```

### `JWT Filter` для `Spring Security`

```java
@Component
public class JwtRequestFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;
    private final UserDetailsService userDetailsService;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain chain) throws ServletException, IOException {
        String authHeader = request.getHeader("Authorization");

        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String jwt = authHeader.substring(7);
            String username = jwtUtil.extractUsername(jwt);

            if (username != null
                    && SecurityContextHolder.getContext().getAuthentication() == null) {
                UserDetails userDetails = userDetailsService.loadUserByUsername(username);

                if (jwtUtil.validateToken(jwt, userDetails)) {
                    var authToken = new UsernamePasswordAuthenticationToken(
                        userDetails, null, userDetails.getAuthorities());
                    authToken.setDetails(
                        new WebAuthenticationDetailsSource().buildDetails(request));
                    SecurityContextHolder.getContext().setAuthentication(authToken);
                }
            }
        }
        chain.doFilter(request, response);
    }
}
```

### `Refresh Token` с ротацией

`Access token` живёт коротко (минуты), чтобы украденный токен быстро протух. Чтобы пользователю не приходилось логиниться каждые 15 минут, выдают долгоживущий `refresh token`: по нему клиент получает новую пару токенов. **Ротация** означает, что при каждом обмене старый refresh-токен удаляется и выпускается новый — если кто-то использует уже потраченный refresh повторно, это сигнал кражи.

```java
@Service
public class TokenService {

    private final JwtUtil jwtUtil;
    private final RefreshTokenRepository refreshTokenRepository;

    public TokenPair generateTokens(User user) {
        String accessToken = jwtUtil.generateToken(user);
        String refreshToken = UUID.randomUUID().toString();

        refreshTokenRepository.save(new RefreshToken(
            refreshToken, user.getUsername(),
            Date.from(Instant.now().plus(30, ChronoUnit.DAYS))));

        return new TokenPair(accessToken, refreshToken);
    }

    public TokenPair refreshTokens(String refreshToken) {
        RefreshToken entity = refreshTokenRepository.findByToken(refreshToken)
            .orElseThrow(() -> new InvalidTokenException("Invalid refresh token"));

        if (entity.getExpiryDate().before(new Date())) {
            refreshTokenRepository.delete(entity);
            throw new TokenExpiredException("Refresh token expired");
        }

        User user = userService.findByUsername(entity.getUsername());
        refreshTokenRepository.delete(entity); // одноразовое использование
        return generateTokens(user);
    }
}
```

## Q4. (!) Что такое `RBAC` и `ABAC`?

`RBAC` отвечает на вопрос «**какая у тебя роль?**», а `ABAC` — «**какие атрибуты сошлись в этом конкретном запросе?**». `RBAC` проще и быстрее, `ABAC` гибче и контекстнее. На практике их часто комбинируют: `RBAC` для грубой фильтрации, `ABAC` — для тонкой (см. Q44).

### `Role-Based Access Control` (`RBAC`)

`RBAC` — доступ на основе ролей. Пользователю назначают роли, роли содержат разрешения (permissions), а проверка прав сводится к «есть ли у пользователя нужное разрешение». Промежуточный слой ролей — главное преимущество: права меняют на уровне роли, а не у каждого пользователя по отдельности.

Связи модели: пользователю (`User`) **назначена** роль (`Role`), роль **содержит** разрешения (`Permission`); нескольким пользователям может быть назначена одна и та же роль. Пример распределения разрешений по ролям:

- `Admin Role` **содержит** `READ`, `WRITE`, `DELETE`
- `User Role` **содержит** `READ`

#### Реализация `RBAC` — доменная модель

```java
@Entity
public class User {
    @Id
    private Long id;
    private String username;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "user_roles")
    private Set<Role> roles = new HashSet<>();
}

@Entity
public class Role {
    @Id
    private Long id;
    private String name; // "ADMIN", "USER", "MODERATOR"

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "role_permissions")
    private Set<Permission> permissions = new HashSet<>();
}

@Entity
public class Permission {
    @Id
    private Long id;
    private String name; // "READ_POST", "CREATE_POST", "DELETE_POST"
}
```

#### `RBAC` в `Spring Security` с `@PreAuthorize`

```java
@Configuration
@EnableMethodSecurity // Spring Security 6+
public class SecurityConfig {
}

@RestController
@RequestMapping("/api/posts")
public class PostController {

    @PreAuthorize("hasAuthority('READ_POST')")
    @GetMapping("/{id}")
    public Post getPost(@PathVariable Long id) {
        return postService.findById(id);
    }

    @PreAuthorize("hasAuthority('CREATE_POST')")
    @PostMapping
    public Post createPost(@RequestBody Post post) {
        return postService.create(post);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public void deletePost(@PathVariable Long id) {
        postService.delete(id);
    }
}
```

### `Attribute-Based Access Control` (`ABAC`)

`ABAC` принимает решение, вычисляя политики на основе атрибутов четырёх видов: **субъекта** (роль, отдел, уровень), **объекта** (владелец, категория), **действия** (read/write/delete) и **окружения** (время, IP, локация). Это позволяет выразить правила, которые `RBAC` не покрывает, например «удалять можно только в рабочее время и только владельцу». Плата за гибкость — каждое правило надо вычислять при запросе, и аудит «кому что доступно» становится сложнее.

В `Policy Engine` сходятся `Access Request` и атрибуты:

- **Subject Attributes** — роль, отдел, уровень
- **Object Attributes** — владелец, категория
- **Environment Attributes** — время, `IP`, локация

`Policy Engine` вычисляет (`Evaluate`) решение: **Allow** → доступ разрешён, либо **Deny** → доступ отклонён.

#### Реализация `ABAC`

```java
@Service
public class AbacService {

    private final List<AccessPolicy> policies;

    public boolean isAllowed(AccessRequest request) {
        return policies.stream()
            .allMatch(policy -> policy.evaluate(request));
    }
}

public interface AccessPolicy {
    boolean evaluate(AccessRequest request);
}

@Component
public class WorkingHoursPolicy implements AccessPolicy {

    @Override
    public boolean evaluate(AccessRequest request) {
        if ("DELETE".equals(request.getAction())) {
            int hour = LocalDateTime.now().getHour();
            return hour >= 9 && hour <= 17; // удаление только в рабочее время
        }
        return true;
    }
}

@Component
public class OwnershipPolicy implements AccessPolicy {

    @Override
    public boolean evaluate(AccessRequest request) {
        if (request.getResource() instanceof OwnedResource owned) {
            return owned.getOwnerId().equals(request.getSubject().getId())
                || request.getSubject().hasRole("ADMIN");
        }
        return true;
    }
}
```

### Сравнение `RBAC` и `ABAC`

| Аспект | `RBAC` | `ABAC` |
|--------|--------|--------|
| Гибкость | Средняя | Высокая |
| Сложность внедрения | Низкая | Высокая |
| Масштабируемость политик | Хорошая | Отличная |
| Производительность | Высокая (lookup по роли) | Средняя (вычисление политик) |
| Аудит | Простой | Сложный |
| Когда использовать | Фиксированные роли, простые правила | Контекстные правила, сложные политики |

## Q5. В чём разница между аутентификацией и авторизацией?

Кратко: **аутентификация (AuthN) подтверждает, кто ты, авторизация (AuthZ) решает, что тебе можно**. Сначала всегда идёт AuthN, потом AuthZ — нельзя проверить права, пока не установлена личность. Запомнить помогают HTTP-коды: `401 Unauthorized` (несмотря на название) — это провал аутентификации («ты не представился»), а `403 Forbidden` — провал авторизации («представился, но прав нет»).

| Аспект | Аутентификация (AuthN) | Авторизация (AuthZ) |
|--------|------------------------|---------------------|
| Вопрос | **Кто ты?** | **Что тебе можно?** |
| Цель | Подтверждение личности | Проверка прав доступа |
| Порядок | Первая | Вторая (после AuthN) |
| Протоколы | `OIDC`, `SAML`, `LDAP` | `OAuth 2.0`, `RBAC`, `ABAC` |
| Данные | Credentials (пароль, сертификат) | Permissions, roles, scopes |
| HTTP-коды | `401 Unauthorized` | `403 Forbidden` |

Порядок проверки запроса:

1. **Аутентификация** («Кто ты?»): не пройдена → `401 Unauthorized`; пройдена → переход к авторизации.
2. **Авторизация** («Что можно?»): нет прав → `403 Forbidden`; есть права → `200 OK` / ресурс.

В `Spring Security` аутентификация обрабатывается `AuthenticationManager`, а авторизация — `AccessDecisionManager` / `AuthorizationManager` (Spring Security 6+). Подробнее — в [Spring Security](../frameworks/spring/spring-security-interview.md).

## Q6. (!) Как реализовать безопасность в микросервисах?

Безопасность в [микросервисах](../architecture/microservices-interview.md) выстраивается слоями, потому что одной точки контроля недостаточно: **edge** (`API Gateway` валидирует токен на входе), **межсервисный уровень** (сервисы аутентифицируют друг друга через `mTLS` / `JWT`, не доверяя «внутренней» сети) и **централизованная авторизация** (единый источник правил доступа). Ключевой принцип — gateway проверяет токен один раз и пробрасывает идентичность пользователя downstream-сервисам через заголовки.

Топология безопасности:

- `Client` → `API Gateway` (выполняет `JWT`-валидацию).
- `API Gateway` → `Service A` и `Service B`.
- `Service A` ↔ `Service B`: межсервисный трафик по `mTLS` + `JWT`.
- `Service A` и `Service B` → `Authorization Service` (централизованная авторизация).
- `API Gateway` → `Identity Provider` (`Keycloak` / `Auth0`).

### 1. `API Gateway` — единая точка безопасности

```java
@Component
public class GatewayAuthFilter implements GlobalFilter, Ordered {

    private final JwtUtil jwtUtil;

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        String path = exchange.getRequest().getPath().toString();

        if (path.startsWith("/auth/") || path.startsWith("/api/public/")) {
            return chain.filter(exchange);
        }

        String token = extractToken(exchange.getRequest());
        if (token == null || !jwtUtil.validateToken(token)) {
            exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
            return exchange.getResponse().setComplete();
        }

        // Пробрасываем данные пользователя downstream-сервисам
        String username = jwtUtil.extractUsername(token);
        ServerHttpRequest mutatedRequest = exchange.getRequest().mutate()
            .header("X-User-Id", username)
            .header("X-User-Roles", String.join(",", jwtUtil.extractRoles(token)))
            .build();

        return chain.filter(exchange.mutate().request(mutatedRequest).build());
    }

    @Override
    public int getOrder() { return -100; }
}
```

### 2. `Service-to-Service` аутентификация через `JWT`

```java
@Component
public class ServiceAuthInterceptor implements ClientHttpRequestInterceptor {

    private final ServiceTokenProvider tokenProvider;

    @Override
    public ClientHttpResponse intercept(HttpRequest request, byte[] body,
                                        ClientHttpRequestExecution execution)
                                        throws IOException {
        String serviceToken = tokenProvider.generateServiceToken();
        request.getHeaders().setBearerAuth(serviceToken);
        request.getHeaders().set("X-Service-Name", "order-service");
        return execution.execute(request, body);
    }
}
```

### 3. Централизованная авторизация

```java
@RestController
@RequestMapping("/authz")
public class AuthorizationController {

    private final AuthorizationService authorizationService;

    @PostMapping("/check")
    public AuthorizationDecision checkPermission(@RequestBody AuthzRequest request) {
        boolean allowed = authorizationService.hasPermission(
            request.getUserId(), request.getResource(),
            request.getAction(), request.getContext());
        return new AuthorizationDecision(allowed);
    }
}
```

### 4. Распределённые сессии через `Redis`

```java
@Configuration
@EnableRedisHttpSession(redisNamespace = "myapp:session")
public class SessionConfig {

    @Bean
    public LettuceConnectionFactory connectionFactory() {
        return new LettuceConnectionFactory("redis-server", 6379);
    }
}
```

## Q7. Что такое `SAML` и когда его использовать?

`SAML` (`Security Assertion Markup Language`) — `XML`-стандарт обмена данными об аутентификации между `Identity Provider` (`IdP`, тот, кто проверяет пользователя) и `Service Provider` (`SP`, приложение). Пользователь логинится один раз у `IdP`, а тот выдаёт подписанный `XML`-документ (`assertion`), которому доверяет `SP`. Это «старший» протокол enterprise-мира: проверенный временем, но громоздкий из-за `XML`.

Участники: `User`, `Service Provider` (`SP`), `Identity Provider` (`IdP`). По шагам:

1. `User → SP`: запрос доступа.
2. `SP → IdP`: `SAML AuthnRequest` (через redirect).
3. `IdP → User`: страница входа.
4. `User → IdP`: логин/пароль.
5. `IdP → SP`: `SAML Response` (assertion).
6. `SP → User`: доступ к ресурсу.

### Конфигурация `SAML` в `Spring Security`

```java
@Configuration
@EnableWebSecurity
public class SamlSecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .authorizeHttpRequests(auth -> auth.anyRequest().authenticated())
            .saml2Login(Customizer.withDefaults());
        return http.build();
    }

    @Bean
    public RelyingPartyRegistrationRepository relyingPartyRegistrationRepository() {
        RelyingPartyRegistration registration = RelyingPartyRegistrations
            .fromMetadataLocation("https://idp.example.com/metadata")
            .registrationId("okta")
            .build();
        return new InMemoryRelyingPartyRegistrationRepository(registration);
    }
}
```

**Когда `SAML`:** корпоративные приложения, интеграция с `Active Directory / ADFS`, enterprise SSO — там, где он уже исторически развёрнут.
**Когда `OIDC` вместо `SAML`:** новые приложения, мобильные клиенты, `SPA`, `REST API`. `OIDC` основан на `JSON`/`JWT` и легче ложится на API, тогда как `XML` `SAML` плохо подходит для мобильных и stateless-сценариев.

## Q8. (!) Как реализовать `Multi-Factor Authentication`?

`MFA` требует подтвердить личность **несколькими независимыми факторами** разных категорий, чтобы кражи одного (например, пароля) было недостаточно. Три категории: **знание** — пароль или PIN (something you know); **владение** — телефон или аппаратный ключ (something you have); **биометрия** — отпечаток, лицо (something you are). Сила MFA именно в независимости факторов: украсть пароль и одновременно завладеть телефоном жертвы намного труднее.

Участники: `User`, `Server`, `Authenticator App`. По шагам:

1. `User → Server`: `username` + `password`.
2. `Server`: проверка credentials.
3. `Server → User`: запрос `2FA`-кода.
4. `User → Authenticator App`: открывает приложение.
5. `Authenticator App → User`: `TOTP`-код (6 цифр).
6. `User → Server`: ввод `TOTP`-кода.
7. `Server`: верификация `TOTP`.
8. `Server → User`: `JWT`-токен (аутентификация завершена).

### Реализация `TOTP` (`Time-based One-Time Password`)

`TOTP` — самый распространённый второй фактор (Google Authenticator и аналоги). Сервер и приложение хранят общий секрет; код вычисляется из секрета и текущего времени (окно 30 секунд), поэтому совпадает на обеих сторонах без передачи по сети. Из-за рассинхронизации часов проверяют код в окне ±1 шаг (clock skew).

```java
@Service
public class TotpService {

    public String generateSecret() {
        return new Base32().encodeAsString(new SecureRandom().generateSeed(20));
    }

    public boolean verifyTotp(String secret, String code) {
        // Проверка с учётом clock skew (±30 секунд)
        byte[] decodedKey = new Base32().decode(secret);
        long timeStep = System.currentTimeMillis() / 30_000;

        for (int i = -1; i <= 1; i++) {
            String computed = computeTotp(decodedKey, timeStep + i);
            if (computed.equals(code)) return true;
        }
        return false;
    }

    public String generateQrCodeUrl(String secret, String username, String issuer) {
        return String.format("otpauth://totp/%s:%s?secret=%s&issuer=%s",
            issuer, username, secret, issuer);
    }
}
```

### `MFA Flow` — контроллер

```java
@RestController
@RequestMapping("/auth")
public class MfaController {

    private final TotpService totpService;
    private final JwtService jwtService;
    private final UserService userService;

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest request) {
        User user = userService.authenticate(request.getUsername(), request.getPassword());

        if (user.isMfaEnabled()) {
            String sessionId = createMfaSession(user.getId());
            return ResponseEntity.ok(new MfaRequiredResponse(sessionId));
        }
        return ResponseEntity.ok(new LoginResponse(jwtService.generateToken(user)));
    }

    @PostMapping("/mfa/verify")
    public ResponseEntity<?> verifyMfa(@RequestBody MfaVerificationRequest request) {
        MfaSession session = getMfaSession(request.getSessionId());
        User user = userService.findById(session.getUserId());

        if (totpService.verifyTotp(user.getMfaSecret(), request.getCode())) {
            deleteMfaSession(request.getSessionId());
            return ResponseEntity.ok(new LoginResponse(jwtService.generateToken(user)));
        }
        return ResponseEntity.badRequest().body("Invalid MFA code");
    }
}
```

## Q9. Какие паттерны сессионного управления существуют?

Главный вопрос при выборе — **где хранится состояние сессии**, и от этого зависят масштабирование и отзыв. Спектр такой: от полностью серверного состояния (server-side sessions) до полностью клиентского (stateless JWT), с гибридом посередине. Чем больше состояния на сервере — тем проще отзыв, но тяжелее масштабирование; чем больше у клиента — наоборот.

### Сравнение подходов

| Подход | State | Масштабирование | Logout | Использование |
|--------|-------|-----------------|--------|---------------|
| Server-Side Sessions | Stateful | Sticky sessions / Redis | Простой | Монолиты |
| Distributed Sessions (Redis) | Stateful (shared) | Горизонтальное | Простой | Кластеры |
| JWT-based (stateless) | Stateless | Отличное | Сложный* | Микросервисы |
| Hybrid (JWT + Redis blacklist) | Mixed | Хорошее | Средний | Компромисс |

*Для logout `JWT` нужен blacklist или короткий срок + refresh token.

### `Server-Side Sessions` — конфигурация

```java
@Configuration
public class SessionConfig {

    @Bean
    public ServletContextInitializer servletContextInitializer() {
        return ctx -> {
            ctx.getSessionCookieConfig().setHttpOnly(true);
            ctx.getSessionCookieConfig().setSecure(true);
            ctx.getSessionCookieConfig().setMaxAge(1800); // 30 минут
            ctx.getSessionCookieConfig().setAttribute("SameSite", "Strict");
        };
    }
}
```

### Распределённые сессии через `Redis`

```java
@Configuration
@EnableRedisHttpSession(redisNamespace = "myapp:session")
public class RedisSessionConfig {

    @Bean
    public LettuceConnectionFactory connectionFactory() {
        RedisStandaloneConfiguration config = new RedisStandaloneConfiguration();
        config.setHostName("redis-server");
        config.setPort(6379);
        return new LettuceConnectionFactory(config);
    }

    @Bean
    public RedisSerializer<Object> springSessionDefaultRedisSerializer() {
        return new GenericJackson2JsonRedisSerializer();
    }
}
```

### `JWT-based Sessions` с ротацией refresh-токенов

```java
@Service
public class JwtSessionService {

    private final JwtUtil jwtUtil;
    private final RefreshTokenRepository refreshTokenRepository;

    public SessionTokens createSession(User user) {
        String accessToken = jwtUtil.generateToken(user);    // 15 минут
        String refreshToken = UUID.randomUUID().toString();   // 30 дней

        refreshTokenRepository.save(new RefreshToken(
            refreshToken, user.getUsername(),
            Date.from(Instant.now().plus(30, ChronoUnit.DAYS))));

        return new SessionTokens(accessToken, refreshToken);
    }

    public SessionTokens refreshSession(String refreshToken) {
        RefreshToken entity = refreshTokenRepository.findByToken(refreshToken)
            .orElseThrow(() -> new InvalidTokenException("Invalid refresh token"));

        if (entity.getExpiryDate().before(new Date())) {
            refreshTokenRepository.delete(entity);
            throw new TokenExpiredException("Refresh token expired");
        }

        User user = userService.findByUsername(entity.getUsername());
        refreshTokenRepository.delete(entity); // ротация: старый удаляется
        return createSession(user);
    }
}
```

## Q10. Как защититься от распространённых атак на аутентификацию?

Четыре частых вектора и их контрмеры: `Brute Force` (перебор паролей), `CSRF` (запрос от имени жертвы), `Session Fixation` (навязывание известного злоумышленнику session ID) и `Timing Attack` (вывод информации по времени ответа). Ниже — как закрыть каждый.

### 1. Защита от `Brute Force`

Идея — ограничить число попыток входа по ключу (`username` + `IP`) и временно блокировать после порога. Это превращает перебор миллионов паролей в практически невозможный по времени.

```java
@Service
public class LoginProtectionService {

    private final Cache<String, LoginAttempts> attemptsCache;

    public boolean isAllowedToLogin(String username, String ip) {
        String key = username + ":" + ip;
        LoginAttempts attempts = attemptsCache.get(key, LoginAttempts::new);

        if (attempts.getCount() >= 5) {
            return attempts.getBlockedUntil().isBefore(Instant.now());
        }
        return true;
    }

    public void recordFailedLogin(String username, String ip) {
        String key = username + ":" + ip;
        LoginAttempts attempts = attemptsCache.get(key, LoginAttempts::new);
        attempts.increment();

        if (attempts.getCount() >= 5) {
            attempts.setBlockedUntil(Instant.now().plus(15, ChronoUnit.MINUTES));
        }
        attemptsCache.put(key, attempts);
    }
}
```

### 2. Защита от `CSRF`

```java
@Bean
public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
    http.csrf(csrf -> csrf
        .csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse())
        .csrfTokenRequestHandler(new CsrfTokenRequestAttributeHandler()));
    return http.build();
}
```

Для `REST API` с `JWT` (stateless) `CSRF`-защита не нужна. Причина: `CSRF` эксплуатирует то, что браузер **автоматически** прикладывает cookie к запросу. Токен же лежит в заголовке `Authorization`, который браузер сам не добавляет — JavaScript злоумышленника на чужом сайте его поставить не сможет.

### 3. Защита от `Session Fixation`

```java
http.sessionManagement(session -> session
    .sessionFixation().changeSessionId() // новый session ID после логина
    .maximumSessions(1)                  // одна активная сессия
    .maxSessionsPreventsLogin(true));     // блокировка нового входа
```

### 4. Защита от `Timing Attacks`

Если для несуществующего пользователя ответ приходит заметно быстрее (не дойдя до проверки пароля), атакующий по времени ответа узнаёт, какие логины существуют. Защита — отвечать за **одинаковое время** независимо от того, найден пользователь или нет. `Spring Security` делает это из коробки: даже для неизвестного пользователя вызывается `passwordEncoder.matches()` против фиктивного хэша.

```java
// Плохо — время ответа зависит от существования пользователя
if (userRepository.findByUsername(username).isEmpty()) {
    throw new BadCredentialsException("User not found");
}

// Хорошо — одинаковое время ответа (Spring Security делает это из коробки)
// UserDetailsService всегда вызывает passwordEncoder.matches()
```

Подробнее о защите от уязвимостей — в [OWASP Top 10](owasp-top10-interview.md) и [Application Security](application-security-interview.md).

## Q11. Как реализовать `API Gateway Security`?

`API Gateway` — единая точка входа для всех клиентских запросов, и поэтому удобное место для централизованной безопасности: вместо того чтобы каждый сервис валидировал токен и считал лимиты, это делается один раз на входе. В `Spring Cloud Gateway` это реализуется цепочкой фильтров — `GlobalFilter` проверяет токен и пробрасывает идентичность пользователя downstream, а `RequestRateLimiter` ограничивает частоту запросов.

```java
@Component
public class AuthenticationGatewayFilter implements GlobalFilter, Ordered {

    private final JwtUtil jwtUtil;
    private final RouteValidator routeValidator;

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        if (routeValidator.isPublicRoute(exchange.getRequest().getPath().toString())) {
            return chain.filter(exchange);
        }

        String token = extractToken(exchange.getRequest());
        if (token == null || !jwtUtil.validateToken(token)) {
            exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
            return exchange.getResponse().setComplete();
        }

        String username = jwtUtil.extractUsername(token);
        ServerHttpRequest mutatedRequest = exchange.getRequest().mutate()
            .header("X-User-Id", username)
            .header("X-User-Roles",
                String.join(",", jwtUtil.extractRoles(token)))
            .build();

        return chain.filter(exchange.mutate().request(mutatedRequest).build());
    }

    @Override
    public int getOrder() { return -100; }
}
```

Для `Rate Limiting` в gateway используется `Redis`:

```java
@Bean
public RouteLocator routeLocator(RouteLocatorBuilder builder) {
    return builder.routes()
        .route("api_route", r -> r
            .path("/api/**")
            .filters(f -> f
                .requestRateLimiter(rl -> rl
                    .setRateLimiter(redisRateLimiter())
                    .setKeyResolver(userKeyResolver())))
            .uri("lb://api-service"))
        .build();
}
```

## Q12. (!) Что такое `OpenID Connect` и как он расширяет `OAuth 2.0`?

`OpenID Connect` (`OIDC`) — тонкий слой аутентификации поверх `OAuth 2.0`. Зачем он нужен: `OAuth 2.0` сам по себе решает только **авторизацию** (дать приложению доступ к ресурсам), но не говорит **кто** пользователь — `access token` непрозрачен для клиента. `OIDC` закрывает этот пробел, добавляя `ID Token` — подписанный `JWT` со стандартными claims о пользователе (`sub`, `name`, `email`). Именно `OIDC`, а не «голый» OAuth, стоит за кнопками «Войти через Google/GitHub».

Что даёт каждый из них:

- **OAuth 2.0** — только `Access Token` (доступ к ресурсам).
- **OIDC = OAuth 2.0 + Identity** — добавляет поверх `Access Token`: `ID Token` (`JWT` с claims `sub`, `name`, `email`, `aud`) и `UserInfo Endpoint` (`/userinfo`).

### Ключевые дополнения `OIDC` к `OAuth 2.0`

| Компонент | `OAuth 2.0` | `OIDC` |
|-----------|-------------|--------|
| Назначение | Авторизация | Аутентификация + Авторизация |
| Токен идентификации | Нет | `ID Token` (`JWT`) |
| Информация о пользователе | Нет стандарта | `/userinfo` endpoint |
| Discovery | Нет | `/.well-known/openid-configuration` |
| Scopes | Произвольные | `openid`, `profile`, `email` |

### Реализация в `Spring Security`

```java
@Configuration
@EnableWebSecurity
public class OidcConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .authorizeHttpRequests(auth -> auth.anyRequest().authenticated())
            .oauth2Login(Customizer.withDefaults()); // OIDC через oauth2Login
        return http.build();
    }
}

@RestController
public class UserController {

    @GetMapping("/user")
    public Map<String, Object> user(@AuthenticationPrincipal OidcUser oidcUser) {
        return Map.of(
            "name", oidcUser.getFullName(),
            "email", oidcUser.getEmail(),
            "claims", oidcUser.getClaims()
        );
    }
}
```

`application.yml`:
```yaml
spring:
  security:
    oauth2:
      client:
        registration:
          google:
            client-id: ${GOOGLE_CLIENT_ID}
            client-secret: ${GOOGLE_CLIENT_SECRET}
            scope: openid, profile, email
```

## Q13. Как реализовать `Single Sign-On` (`SSO`)?

`SSO` (`Single Sign-On`) — один вход для нескольких приложений. Механизм такой: пользователь логинится в `Identity Provider` (`IdP`) один раз, `IdP` заводит свою сессию, и при заходе в любое следующее приложение тот молча получает токен от `IdP` без повторного ввода пароля. Ключевая деталь — сессию держит именно `IdP`, а не каждое приложение по отдельности; приложения лишь доверяют его токенам.

Участники: `User`, `App 1`, `App 2`, `Identity Provider` (`IdP`). По шагам:

1. `User → App 1`: запрос.
2. `App 1 → IdP`: redirect для аутентификации.
3. `IdP → User`: логин.
4. `User → IdP`: credentials.
5. `IdP → App 1`: токен / assertion. *(Пометка: на этом шаге создаётся сессия `IdP`.)*
6. `User → App 2`: запрос.
7. `App 2 → IdP`: redirect.
8. `IdP → App 2`: токен — без повторного логина!

**Подходы к реализации:**

| Протокол | Формат | Когда использовать |
|----------|--------|--------------------|
| `SAML 2.0` | `XML` | Enterprise, `Active Directory` |
| `OIDC` | `JSON` / `JWT` | Современные API, мобильные, `SPA` |
| `CAS` | Ticket-based | Академические учреждения |

В `Spring` — используется `spring-security-oauth2-client` для `OIDC` или `spring-security-saml2-service-provider` для `SAML`.

## Q14. Что такое `Claims-based` аутентификация?

`Claims-based` аутентификация — это подход, при котором всё, что нужно для авторизации (роли, отдел, email), приходит как **claims** прямо в подписанном токене (`JWT` или `SAML assertion`). Приложение доверяет подписи и принимает решения по claims, **не обращаясь к БД** на каждый запрос. Это устраняет лишний round-trip к auth-серверу или базе — но взамен данные в токене «застывают» до его перевыпуска (например, отозванная роль будет действовать до истечения `exp`).

```java
// Извлечение claims из JWT в Spring Security
@RestController
public class ClaimsController {

    @GetMapping("/profile")
    public Map<String, Object> profile(@AuthenticationPrincipal Jwt jwt) {
        return Map.of(
            "userId", jwt.getSubject(),
            "email", jwt.getClaimAsString("email"),
            "roles", jwt.getClaimAsStringList("roles"),
            "department", jwt.getClaimAsString("department")
        );
    }

    @PreAuthorize("@jwt.getClaim('department') == 'engineering'")
    @GetMapping("/internal")
    public String internalResource() {
        return "Engineering-only resource";
    }
}
```

Поток такой: `IdP` зашивает claims в токен при аутентификации → приложение проверяет подпись → использует claims для `RBAC` / `ABAC`. Чувствительные данные в claims класть нельзя — payload `JWT` читается любым (это `Base64`, не шифрование).

## Q15. (!) Как обеспечить безопасность токенов (`JWT` refresh, rotation)?

Главная проблема `JWT` — его нельзя отозвать до истечения `exp`. Стратегия безопасности строится на том, чтобы **минимизировать окно ущерба** от украденного токена: короткий TTL у access-токена (минуты), долгий refresh для удобства, ротация refresh при каждом обмене и детекция повторного использования. Если потраченный refresh применяют второй раз — это почти наверняка кража, и тогда отзывается вся цепочка токенов пользователя.

### Жизненный цикл токенов

Состояния и переходы `Access Token`:

- *(старт)* → `AccessToken`: по аутентификации.
- `AccessToken` → `Expired`: по истечении `TTL` (15 мин).
- `Expired` → `AccessToken`: обновление по `Refresh Token`.
- `AccessToken` → `Revoked`: при logout / компрометации.
- `Revoked` → *(конец)*.

Вложенная машина состояний `Refresh Token`:

- *(старт)* → `Active`.
- `Active` → `Rotated`: токен использован.
- `Rotated` → `NewRefreshToken`: выпуск нового.
- `Active` → `Compromised`: повторное использование.
- `Compromised` → `AllRevoked`: отзыв всей цепочки.

### Ключевые практики

| Практика | Описание |
|----------|----------|
| Короткий TTL access token | 5-15 минут |
| Длинный TTL refresh token | 7-30 дней |
| Ротация refresh token | Новый refresh при каждом обмене, старый инвалидируется |
| Детекция повторного использования | Если старый refresh используется повторно — отзыв всей цепочки |
| Хранение refresh token | `httpOnly` cookie или серверная БД (хэш), **никогда** в `localStorage` |
| Blacklist для access token | `Redis` с TTL равным оставшемуся сроку токена |

### Детекция компрометации refresh token

```java
@Service
public class SecureTokenService {

    private final RefreshTokenRepository repository;

    public TokenPair refresh(String refreshToken) {
        RefreshToken entity = repository.findByToken(refreshToken)
            .orElseThrow(() -> new InvalidTokenException("Unknown token"));

        if (entity.isUsed()) {
            // Повторное использование — компрометация!
            // Отзываем ВСЮ цепочку токенов пользователя
            repository.revokeAllByUserId(entity.getUserId());
            throw new SecurityException("Refresh token reuse detected");
        }

        entity.setUsed(true);
        repository.save(entity);

        return generateTokens(entity.getUserId());
    }
}
```

## Q16. Что такое `Zero Trust Security Model`?

`Zero Trust` — модель «никогда не доверяй, всегда проверяй»: проверяется каждый запрос, даже изнутри сети. Это отказ от старой периметровой модели (castle-and-moat), где попавший за firewall автоматически считался «своим». Проблема периметра в том, что один взломанный сервис открывает злоумышленнику свободное горизонтальное перемещение по всей внутренней сети; `Zero Trust` это перекрывает, требуя аутентификацию и авторизацию на каждом шаге.

**Традиционная модель:** `Firewall` → доверенная зона → `Service A` и `Service B`; между `Service A` и `Service B` связь **без проверки**.

**Zero Trust:** `API Gateway` → `Service A` и `Service B`; между `Service A` и `Service B` связь по `mTLS` + `JWT` + проверка; оба сервиса обращаются к `Policy Decision Point` (`PDP`).

**Принципы:**
1. **Verify explicitly** — проверять каждый запрос (identity, device, location)
2. **Least privilege** — минимальные необходимые права
3. **Assume breach** — проектировать как если бы сеть уже скомпрометирована

**Реализация в микросервисах:**
- `mTLS` между всеми сервисами (`Istio`, `Linkerd`)
- Короткоживущие токены (5-15 минут)
- Авторизация на каждом сервисе
- Continuous verification (не только при входе)
- Network segmentation + monitoring

## Q17. Как реализовать `Rate Limiting` для защиты `API`?

`Rate Limiting` — ограничение числа запросов по ключу (`IP`, `userId`, `API key`) за период времени. Защищает от перебора паролей, DoS и злоупотребления API. Выбор алгоритма — это компромисс между точностью и стоимостью: `Fixed Window` дешёвый, но на границе окон пропускает всплеск до 2× лимита; `Sliding Window` точный, но требует больше памяти; `Token Bucket` допускает контролируемые всплески (burst).

| Алгоритм | Описание | Плюсы | Минусы |
|----------|----------|-------|--------|
| `Token Bucket` | Токены добавляются с фиксированной скоростью | Допускает burst | Сложнее реализовать |
| `Fixed Window` | Счётчик с `TTL` на окно | Простой | Граничный всплеск (2x) |
| `Sliding Window` | Скользящее окно (`Redis ZADD`) | Точный | Больше памяти |

```java
@Component
public class RateLimitFilter extends OncePerRequestFilter {

    private final RedisTemplate<String, String> redis;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain chain)
                                    throws ServletException, IOException {
        String clientIp = request.getRemoteAddr();
        String key = "rate_limit:" + clientIp;

        Long count = redis.opsForValue().increment(key);
        if (count == 1) {
            redis.expire(key, Duration.ofMinutes(1));
        }

        if (count > 100) { // 100 запросов/минуту
            response.setStatus(HttpStatus.TOO_MANY_REQUESTS.value());
            response.getWriter().write("Rate limit exceeded");
            return;
        }

        response.setHeader("X-RateLimit-Remaining", String.valueOf(100 - count));
        chain.doFilter(request, response);
    }
}
```

## Q18. (!) Что такое `mTLS` и когда его использовать?

`mTLS` (`mutual TLS`) — двусторонняя аутентификация по сертификатам. Ключевое отличие от обычного `TLS`: там сервер доказывает свою подлинность клиенту (так работает HTTPS в браузере), но клиент остаётся анонимным. При `mTLS` сервер дополнительно посылает `Certificate Request`, и **клиент тоже предъявляет сертификат** — в итоге обе стороны криптографически уверены, с кем разговаривают, без всяких паролей.

Участники: `Client`, `Server`. По шагам:

1. `Client → Server`: `ClientHello`.
2. `Server → Client`: `ServerHello` + `Server Certificate`.
3. `Server → Client`: `Certificate Request` — отличие от обычного `TLS`!
4. `Client → Server`: `Client Certificate`.
5. `Client → Server`: `CertificateVerify` (подпись).
6. *(Здесь обе стороны аутентифицированы.)* `Client → Server`: `Encrypted communication`.

### Конфигурация `mTLS` в `Spring Boot`

```yaml
server:
  ssl:
    enabled: true
    client-auth: need   # require client certificate
    key-store: classpath:keystore.p12
    key-store-password: ${KEYSTORE_PASSWORD}
    trust-store: classpath:truststore.p12
    trust-store-password: ${TRUSTSTORE_PASSWORD}
```

### Когда использовать

- **Микросервисы** — взаимная аутентификация без паролей (автоматизация через `cert-manager` в Kubernetes)
- **B2B API** — партнёрские интеграции
- **Zero Trust** — сетевой уровень аутентификации
- **IoT** — устройства с сертификатами

**Управление сертификатами** — основная сложность: выпуск, ротация (обычно 90 дней), отзыв (`CRL` / `OCSP`). В `Service Mesh` (`Istio`) — автоматически.

## Q19. Как реализовать аудит и логирование событий безопасности?

Аудит событий безопасности нужен для двух целей: **compliance** (`SOC 2`, `PCI DSS` прямо требуют такие логи) и **расследование инцидентов** — без записи «кто, когда, что и с какого IP» восстановить картину взлома постфактум невозможно. В `Spring` это удобно решается через события: `AuthenticationSuccessEvent` / `AbstractAuthenticationFailureEvent` ловятся `@EventListener`-ом, без засорения бизнес-кода.

### Что логировать

| Событие | Данные | Уровень |
|---------|--------|---------|
| Успешная аутентификация | userId, IP, timestamp | INFO |
| Неудачная аутентификация | username, IP, причина | WARN |
| Изменение прав | userId, oldRole, newRole | INFO |
| Доступ к чувствительным данным | userId, resource, action | INFO |
| Подозрительная активность | IP, pattern, count | WARN |

### Реализация через Spring Events

```java
@Component
public class SecurityAuditListener {

    private static final Logger auditLog = LoggerFactory.getLogger("SECURITY_AUDIT");

    @EventListener
    public void onAuthSuccess(AuthenticationSuccessEvent event) {
        Authentication auth = event.getAuthentication();
        auditLog.info("AUTH_SUCCESS user={} authorities={}",
            auth.getName(), auth.getAuthorities());
    }

    @EventListener
    public void onAuthFailure(AbstractAuthenticationFailureEvent event) {
        auditLog.warn("AUTH_FAILURE user={} reason={}",
            event.getAuthentication().getName(),
            event.getException().getMessage());
    }
}
```

Централизованное хранение в `Elasticsearch` / `SIEM`; алерты по аномалиям (множественные неудачи, необычная геолокация). Подробнее — в [Observability](../monitoring/observability-interview.md).

## Q20. Что такое `Context-based Access Control`?

`Context-based Access Control` — авторизация, учитывающая **контекст самого запроса**: время, геолокацию, устройство, уровень риска. По сути это разновидность `ABAC`, где упор сделан на динамические атрибуты окружения, а не на статичные роли. Смысл — одно и то же право может быть разрешено или запрещено в зависимости от обстоятельств: вход из офиса в рабочее время безопаснее, чем вход с нового устройства из подозрительной страны ночью.

**Примеры правил:**
- Доступ к админке только из корпоративной сети
- Повышенная аутентификация при входе с нового устройства
- Блокировка транзакций из blacklisted-стран
- Ограничение действий в нерабочее время

```java
@Service
public class ContextBasedAuthService {

    public boolean isAllowed(User user, String action, RequestContext ctx) {
        // Правило 1: VPN-only для admin-действий
        if (action.startsWith("ADMIN_") && !ctx.isVpnConnection()) {
            return false;
        }

        // Правило 2: Проверка геолокации
        if (ctx.getCountry() != null
                && bannedCountries.contains(ctx.getCountry())) {
            return false;
        }

        // Правило 3: Новое устройство → step-up auth
        if (!deviceRegistry.isKnownDevice(user.getId(), ctx.getDeviceFingerprint())) {
            throw new StepUpAuthRequired("Unknown device");
        }

        return true;
    }
}
```

Реализация: policy engine (`OPA` — Open Policy Agent, `AWS Verified Permissions`); проверка контекста при каждом запросе; кэширование решений для производительности.

## Q21. Как обеспечить безопасность в `Service Mesh`?

`Service Mesh` (`Istio`, `Linkerd`) выносит безопасность на инфраструктурный уровень **без изменения кода приложений**. Достигается это через sidecar-прокси: рядом с каждым сервисом крутится прокси (Envoy), который перехватывает весь трафик и сам устанавливает `mTLS`, применяет политики доступа и собирает аудит. Сервису не нужно знать про сертификаты и токены — это огромный плюс для `Zero Trust`, где `mTLS` нужен повсеместно, но прошивать его в код десятков сервисов непрактично.

**Что обеспечивает Service Mesh:**
- `mTLS` между sidecar-прокси автоматически
- `AuthorizationPolicy` — правила доступа между сервисами
- Rate limiting на уровне mesh
- Трейсинг и аудит трафика
- Автоматическая ротация сертификатов

```yaml
# Istio AuthorizationPolicy — разрешить только определённым сервисам
apiVersion: security.istio.io/v1
kind: AuthorizationPolicy
metadata:
  name: order-service-policy
spec:
  selector:
    matchLabels:
      app: order-service
  rules:
    - from:
        - source:
            principals: ["cluster.local/ns/default/sa/payment-service"]
      to:
        - operation:
            methods: ["POST"]
            paths: ["/api/orders/*/pay"]
```

Подробнее о `Kubernetes` и инфраструктуре — в [Kubernetes](../devops/kubernetes-interview.md).

## Q22. Что такое `Identity Federation`?

`Identity Federation` — доверительные отношения между несколькими `Identity Provider` (`IdP`): пользователь аутентифицируется в **своём** `IdP`, а ресурсы **чужой** организации принимают этот результат, потому что доверяют его `IdP`. Это отличается от SSO внутри одной компании — федерация связывает разные домены доверия (например, две компании-партнёра), не заставляя заводить пользователям отдельные учётки.

**Протоколы:** `SAML`, `WS-Federation`, `OIDC`.
**Сценарии:** доступ партнёров к корпоративным ресурсам, мультиоблачная среда (`Azure AD` + `Google Workspace`), B2B-интеграции.

Топология федерации:

- `Employee` → `Corporate IdP`; `Partner` → `Partner IdP`.
- `Corporate IdP` и `Partner IdP` связаны доверием (**Trust**) с `Federation Hub`.
- `Federation Hub` → `App 1`, `App 2`, `App 3`.

Ключевая задача — **маппинг атрибутов**: роли и groups из одного `IdP` могут не совпадать с другим. Нужна таблица маппинга claims.

## Q23. Как реализовать `Step-Up Authentication`?

`Step-Up Authentication` — динамическое повышение уровня аутентификации перед чувствительной операцией. Идея в балансе UX и безопасности: не мучить пользователя `2FA` на каждом шаге, а требовать дополнительное подтверждение только там, где цена ошибки высока. Например, просмотр профиля доступен по обычному входу, а перевод крупной суммы — лишь после повторного `2FA`.

```java
@RestController
@RequestMapping("/api/transfers")
public class TransferController {

    @PreAuthorize("hasAuthority('TRANSFER')")
    @PostMapping
    public ResponseEntity<?> transfer(@RequestBody TransferRequest request,
                                      @AuthenticationPrincipal Jwt jwt) {
        // Проверяем уровень аутентификации (ACR claim)
        String acr = jwt.getClaimAsString("acr");

        if (request.getAmount().compareTo(new BigDecimal("1000")) > 0
                && !"urn:mfa".equals(acr)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                .body(new StepUpRequired("MFA required for transfers > $1000"));
        }

        return ResponseEntity.ok(transferService.execute(request));
    }
}
```

Claim `acr` (`Authentication Context Class Reference`) в `JWT` указывает уровень аутентификации: `password-only`, `mfa`, `hardware-key`. `IdP` (`Keycloak`, `Auth0`) устанавливает этот claim при аутентификации.

## Q24. Что такое `Passwordless Authentication`?

`Passwordless Authentication` убирает пароль как фактор — а значит и весь класс атак, связанных с ним (фишинг, перебор, утечки баз паролей, переиспользование). Вместо пароля доказательством личности служит владение устройством или биометрия. Основные варианты:

- **`WebAuthn` / `FIDO2`** — биометрия и аппаратные ключи (YubiKey); самый стойкий к фишингу, так как ключ привязан к домену
- **`Magic Link`** — одноразовая ссылка на email
- **`OTP`** — одноразовый код через SMS / push

```java
@RestController
@RequestMapping("/auth/magic-link")
public class MagicLinkController {

    private final MagicLinkService magicLinkService;

    @PostMapping("/request")
    public ResponseEntity<?> requestLink(@RequestBody EmailRequest request) {
        String token = UUID.randomUUID().toString();
        magicLinkService.saveToken(token, request.getEmail(), Duration.ofMinutes(10));
        emailService.send(request.getEmail(),
            "Login link: https://app.example.com/auth/verify?token=" + token);
        return ResponseEntity.ok("Check your email");
    }

    @GetMapping("/verify")
    public ResponseEntity<?> verify(@RequestParam String token) {
        String email = magicLinkService.validateAndConsume(token);
        User user = userService.findByEmail(email);
        String jwt = jwtService.generateToken(user);
        return ResponseEntity.ok(new LoginResponse(jwt));
    }
}
```

**Преимущества:** нет фишинга паролей, удобство UX.
**Недостатки:** зависимость от email/телефона, `Magic Link` уязвим к перехвату email.

## Q25. Как защитить `GraphQL API`?

Сама гибкость `GraphQL` — источник его специфических угроз. В отличие от REST с фиксированными эндпоинтами, клиент сам конструирует запрос: может уйти в глубокую вложенность или запросить дорогие поля и положить сервер (DoS), а единый эндпоинт делает привычную авторизацию «по URL» бесполезной — проверять права надо **на уровне отдельных полей**.

**Ключевые аспекты:**

| Угроза | Защита |
|--------|--------|
| Слишком глубокие запросы | Ограничение глубины (`maxDepth`) |
| Сложные запросы (DoS) | Ограничение complexity score |
| Неавторизованный доступ к полям | Field-level авторизация |
| Introspection в production | Отключение `__schema` |
| N+1 проблема | `DataLoader` |

```java
@Component
public class GraphQLSecurityInstrumentation extends SimplePerformantInstrumentation {

    @Override
    public DataFetcher<?> instrumentDataFetcher(DataFetcher<?> dataFetcher,
                                                 InstrumentationFieldFetchParameters params) {
        String fieldName = params.getEnvironment().getField().getName();

        return environment -> {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();

            // Field-level авторизация
            if ("salary".equals(fieldName) && !hasRole(auth, "HR")) {
                throw new AccessDeniedException("No access to salary field");
            }

            return dataFetcher.get(environment);
        };
    }
}
```

## Q26. Что такое `Delegated Authorization`?

`Delegated Authorization` — пользователь делегирует приложению **часть** своих прав, не отдавая пароль: «приложение X может читать мои фото, но не удалять». Это и есть основная идея `OAuth 2.0`.

Механика: пользователь подтверждает доступ на `consent screen` → приложение получает `access token` с ограниченным набором `scopes` (`read`, `write`, `delete`) → `Resource Server` пускает приложение только туда, куда позволяют scopes. Важное свойство — пользователь может отозвать доступ в любой момент, не меняя пароль.

```java
// Resource Server проверяет scopes
@RestController
@RequestMapping("/api/photos")
public class PhotoController {

    @PreAuthorize("hasAuthority('SCOPE_photos:read')")
    @GetMapping
    public List<Photo> getPhotos() {
        return photoService.getAll();
    }

    @PreAuthorize("hasAuthority('SCOPE_photos:write')")
    @PostMapping
    public Photo upload(@RequestBody Photo photo) {
        return photoService.save(photo);
    }

    @PreAuthorize("hasAuthority('SCOPE_photos:delete')")
    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        photoService.delete(id);
    }
}
```

## Q27. (!) Как реализовать `Fine-Grained Authorization`?

`Fine-Grained Authorization` — авторизация на уровне **конкретного экземпляра** объекта, а не типа: «пользователь может редактировать только свои заказы». Роли (`RBAC`) тут не помогают — роль `USER` есть у всех, а право зависит от связи между этим пользователем и этим объектом (например, владение). Поэтому проверку нельзя сделать только по токену: нужно подгрузить объект и сравнить его атрибуты с идентичностью запрашивающего.

### Подходы

| Подход | Сложность | Когда использовать |
|--------|-----------|--------------------|
| Проверка в коде | Низкая | Простые правила (owner check) |
| `@PreAuthorize` + SpEL | Средняя | Декларативные правила |
| Custom `PermissionEvaluator` | Средняя | Проверка прав на объект |
| Внешний policy engine (`OPA`) | Высокая | Сложные бизнес-правила |

### Проверка через `@PostAuthorize`

Разница принципиальна: `@PreAuthorize` проверяет права **до** вызова метода (когда объекта ещё нет — права выводят из аргументов через отдельный запрос), а `@PostAuthorize` — **после**, имея на руках `returnObject`. `@PostAuthorize` удобен для owner-check по уже загруженному объекту, но помните: метод **уже отработал**, поэтому его нельзя применять там, где сам факт чтения имеет побочные эффекты.

```java
@RestController
@RequestMapping("/api/orders")
public class OrderController {

    @PostAuthorize("returnObject.userId == authentication.name or hasRole('ADMIN')")
    @GetMapping("/{id}")
    public Order getOrder(@PathVariable Long id) {
        return orderService.findById(id);
    }

    @PreAuthorize("@orderSecurity.isOwner(#id, authentication)")
    @PutMapping("/{id}")
    public Order updateOrder(@PathVariable Long id, @RequestBody Order order) {
        return orderService.update(id, order);
    }
}

@Component("orderSecurity")
public class OrderSecurityService {

    private final OrderRepository orderRepository;

    public boolean isOwner(Long orderId, Authentication auth) {
        return orderRepository.findById(orderId)
            .map(order -> order.getUserId().equals(auth.getName()))
            .orElse(false);
    }
}
```

## Q28. Что такое `Token Binding` и зачем он нужен?

`Token Binding` (`RFC 8471`) — привязка токена к конкретному `TLS`-соединению клиента криптографическим доказательством. Решает фундаментальную слабость bearer-токенов: обычный `JWT` или cookie работает у **любого**, кто его предъявит, поэтому украденный токен сразу пригоден к использованию. С привязкой токен бесполезен на чужом соединении — нет нужного ключа.

**Как работает:**
1. Клиент и сервер обмениваются ключами при `TLS handshake`
2. Токен включает `binding ID`, привязанный к соединению
3. При предъявлении токена с другого соединения — отказ

**Практический статус:** поддержка ограничена (отменено в браузерах). Альтернативы:
- **DPoP** (`Demonstrating Proof-of-Possession`, `RFC 9449`) — proof привязки токена к клиенту
- Короткий TTL access token + refresh token rotation
- `Sender-Constrained Tokens` (`mTLS` certificate-bound tokens)

## Q29. Как обеспечить безопасность `WebSocket` соединений?

Особенность `WebSocket` в том, что это **долгоживущее соединение**: классическая проверка «токен в заголовке каждого HTTP-запроса» здесь не работает, потому что HTTP-запрос один — на момент handshake. Поэтому аутентификацию делают при установке соединения (`CONNECT`-фрейм STOMP с токеном), а дальше авторизуют уже отдельные действия — подписку на topic и отправку сообщений. И обязательно `wss://` (TLS), иначе токен при handshake уйдёт открытым.

```java
@Configuration
@EnableWebSocketMessageBroker
public class WebSocketSecurityConfig implements WebSocketMessageBrokerConfigurer {

    @Override
    public void configureClientInboundChannel(ChannelRegistration registration) {
        registration.interceptors(new ChannelInterceptor() {
            @Override
            public Message<?> preSend(Message<?> message, MessageChannel channel) {
                StompHeaderAccessor accessor = StompHeaderAccessor.wrap(message);

                if (StompCommand.CONNECT.equals(accessor.getCommand())) {
                    String token = accessor.getFirstNativeHeader("Authorization");
                    if (token != null && token.startsWith("Bearer ")) {
                        Authentication auth = jwtUtil.getAuthentication(
                            token.substring(7));
                        accessor.setUser(auth);
                    }
                }
                return message;
            }
        });
    }
}
```

**Ключевые практики:** аутентификация при `CONNECT` (токен в заголовке), `TLS` (`wss://`), проверка прав на подписку к topic, rate limiting сообщений.

## Q30. (!) Что такое `Proof Key for Code Exchange` (`PKCE`)?

`PKCE` (`RFC 7636`, читается «пикси») — расширение `OAuth 2.0`, защищающее `Authorization Code Flow` у **публичных клиентов** (`SPA`, мобильные приложения). Проблема, которую он решает: такие клиенты не могут безопасно хранить `client_secret` (код на устройстве можно вскрыть), поэтому перехваченный `authorization code` злоумышленник обменял бы на токен. `PKCE` добавляет одноразовый секрет (`code_verifier`), сгенерированный самим клиентом и **никогда не покидающий** его, — без него код бесполезен.

Участники: `Client (SPA)`, `Authorization Server` (`AS`). По шагам:

1. `Client`: генерация `code_verifier` (random).
2. `Client`: `code_challenge = SHA256(code_verifier)`.
3. `Client → AS`: `/authorize` + `code_challenge` + `method=S256`.
4. `AS → Client`: `Authorization Code`.
5. `Client → AS`: `/token` + `code` + `code_verifier`.
6. `AS`: проверка `SHA256(code_verifier) == code_challenge`?
7. `AS → Client`: `Access Token`.

### Реализация клиентской части

```java
public class PkceUtil {

    public static String generateCodeVerifier() {
        byte[] bytes = new byte[32];
        new SecureRandom().nextBytes(bytes);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
    }

    public static String generateCodeChallenge(String codeVerifier) {
        try {
            byte[] digest = MessageDigest.getInstance("SHA-256")
                .digest(codeVerifier.getBytes(StandardCharsets.US_ASCII));
            return Base64.getUrlEncoder().withoutPadding().encodeToString(digest);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }
}
```

### Конфигурация Spring Authorization Server

С `Spring Security 6.3+` `PKCE` поддерживается для `confidential clients` (не только для public).

```yaml
spring:
  security:
    oauth2:
      client:
        registration:
          my-client:
            client-id: my-spa
            client-authentication-method: none  # public client
            authorization-grant-type: authorization_code
            redirect-uri: http://localhost:3000/callback
            scope: openid, profile
```

**Эмпирическое правило:** сегодня `PKCE` рекомендуют **для всех** клиентов `Authorization Code Flow`, включая confidential (`Spring Security 6.3+` это поддерживает) — он не мешает `client_secret` и добавляет защиту от подмены кода практически бесплатно. `Implicit Flow`, который раньше использовали для `SPA`, объявлен устаревшим именно в пользу `Authorization Code + PKCE`.

## Q31. Как реализовать `Dynamic Authorization`?

`Dynamic Authorization` — решения, зависящие от **runtime-данных**: текущего состояния объекта, бизнес-правил, внешних факторов. Отличие от `RBAC` в том, что права нельзя «зашить» заранее — они вычисляются по контексту в момент запроса (классика: «менеджер одобряет заказы до $1000, выше — только директор»). Чтобы такие правила не растекались по коду контроллеров, их выносят в **policy engine** и описывают декларативно.

```java
@Service
public class DynamicAuthorizationService {

    private final PolicyEngine policyEngine;

    public AuthorizationDecision evaluate(String userId, String action,
                                          String resource, Map<String, Object> context) {
        // Загрузка политик из БД или policy engine (OPA, Casbin)
        PolicyResult result = policyEngine.evaluate(
            new PolicyRequest(userId, action, resource, context));

        return new AuthorizationDecision(
            result.isAllowed(), result.getReason());
    }
}

// Пример: менеджер одобряет заказы до $1000, выше — директор
@Component
public class OrderApprovalPolicy implements Policy {

    @Override
    public boolean evaluate(PolicyRequest request) {
        if (!"APPROVE".equals(request.getAction())) return true;

        BigDecimal amount = (BigDecimal) request.getContext().get("amount");
        String role = (String) request.getContext().get("role");

        if (amount.compareTo(new BigDecimal("1000")) > 0) {
            return "DIRECTOR".equals(role);
        }
        return "MANAGER".equals(role) || "DIRECTOR".equals(role);
    }
}
```

Для сложных правил используют **`OPA` (Open Policy Agent)** — policy engine с языком `Rego`, или **`AWS Verified Permissions`** — managed-сервис для fine-grained авторизации.

## Q32. (!) Как настроить `Spring Security` как `OAuth2 Resource Server`?

`Resource Server` — это сервис, который защищает API и при каждом запросе валидирует пришедший `access token`. В отличие от `Authorization Server`, он токены **не выдаёт**, а только проверяет: подпись (по `jwk-set-uri` от auth-сервера), срок и claims. В `Spring` достаточно `oauth2ResourceServer`-конфигурации — вся обвязка фильтров создаётся автоматически. Подробнее о `Spring Security` — в [Spring Security](../frameworks/spring/spring-security-interview.md).

### Конфигурация с `JWT`

```java
@Configuration
@EnableWebSecurity
public class ResourceServerConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/api/public/**").permitAll()
                .requestMatchers("/api/admin/**").hasRole("ADMIN")
                .anyRequest().authenticated())
            .oauth2ResourceServer(oauth2 -> oauth2
                .jwt(jwt -> jwt
                    .jwtAuthenticationConverter(jwtAuthenticationConverter())));
        return http.build();
    }

    @Bean
    public JwtAuthenticationConverter jwtAuthenticationConverter() {
        JwtGrantedAuthoritiesConverter grantedAuthorities =
            new JwtGrantedAuthoritiesConverter();
        grantedAuthorities.setAuthorityPrefix("ROLE_");
        grantedAuthorities.setAuthoritiesClaimName("roles");

        JwtAuthenticationConverter converter = new JwtAuthenticationConverter();
        converter.setJwtGrantedAuthoritiesConverter(grantedAuthorities);
        return converter;
    }
}
```

`application.yml`:
```yaml
spring:
  security:
    oauth2:
      resourceserver:
        jwt:
          issuer-uri: https://auth.example.com/realms/my-realm
          # или jwk-set-uri: https://auth.example.com/.well-known/jwks.json
```

### Маппинг authorities из `JWT`

По умолчанию `Spring Security` берёт authorities из claim `scope`. Для кастомных claims (например, `roles` из `Keycloak`) нужен `JwtGrantedAuthoritiesConverter`.

## Q33. Как реализовать кастомный `PermissionEvaluator` в `Spring Security`?

`PermissionEvaluator` — расширение `Spring Security`, которое подключает выражение `hasPermission()` в `@PreAuthorize`/`@PostAuthorize`. Зачем он нужен: `hasRole()`/`hasAuthority()` проверяют только статичные роли, а `hasPermission()` позволяет вынести **доменную логику доступа к объекту** (владелец, статус заказа) в один переиспользуемый компонент вместо повторения SpEL-выражений по всем контроллерам. Есть две формы: по уже загруженному объекту и по `(id, type)` — вторая сама подгрузит объект из репозитория.

```java
@Component
public class CustomPermissionEvaluator implements PermissionEvaluator {

    private final OrderRepository orderRepository;
    private final DocumentRepository documentRepository;

    @Override
    public boolean hasPermission(Authentication auth, Object targetDomainObject,
                                  Object permission) {
        if (targetDomainObject instanceof Order order) {
            return evaluateOrderPermission(auth, order, (String) permission);
        }
        return false;
    }

    @Override
    public boolean hasPermission(Authentication auth, Serializable targetId,
                                  String targetType, Object permission) {
        if ("Order".equals(targetType)) {
            Order order = orderRepository.findById((Long) targetId).orElse(null);
            return order != null
                && evaluateOrderPermission(auth, order, (String) permission);
        }
        return false;
    }

    private boolean evaluateOrderPermission(Authentication auth, Order order,
                                             String permission) {
        String userId = auth.getName();
        return switch (permission) {
            case "READ" -> order.getUserId().equals(userId)
                || hasRole(auth, "ADMIN");
            case "WRITE" -> order.getUserId().equals(userId)
                && "DRAFT".equals(order.getStatus());
            case "DELETE" -> hasRole(auth, "ADMIN");
            default -> false;
        };
    }
}

// Использование
@PreAuthorize("hasPermission(#id, 'Order', 'WRITE')")
@PutMapping("/orders/{id}")
public Order update(@PathVariable Long id, @RequestBody Order order) {
    return orderService.update(id, order);
}
```

Регистрация:
```java
@Configuration
@EnableMethodSecurity
public class MethodSecurityConfig {

    @Bean
    static MethodSecurityExpressionHandler methodSecurityExpressionHandler(
            CustomPermissionEvaluator evaluator) {
        DefaultMethodSecurityExpressionHandler handler =
            new DefaultMethodSecurityExpressionHandler();
        handler.setPermissionEvaluator(evaluator);
        return handler;
    }
}
```

## Q34. Как работает `SecurityFilterChain` в `Spring Security 6`?

В `Spring Security 6` безопасность настраивается через bean `SecurityFilterChain` — компонентный стиль вместо удалённого `WebSecurityConfigurerAdapter`. Под капотом это **цепочка фильтров**, через которую проходит каждый запрос: аутентификация, авторизация, CSRF и т.д. — каждый фильтр отвечает за свой аспект. Полезный приём — несколько `SecurityFilterChain` с `@Order` и `securityMatcher`: например, отдельная stateless-цепочка для `/api/**` (JWT) и form-login цепочка для UI. Запрос попадает в первую подходящую по matcher цепочку.

Путь запроса по цепочке (по порядку):

`HTTP Request` → `DelegatingFilterProxy` → `FilterChainProxy` → `SecurityContextPersistenceFilter` → `CsrfFilter` → `UsernamePasswordAuthenticationFilter` → `BearerTokenAuthenticationFilter` → `AuthorizationFilter` → `Controller`.

```java
@Configuration
@EnableWebSecurity
public class MultiSecurityConfig {

    // Цепочка для API — JWT, stateless
    @Bean
    @Order(1)
    public SecurityFilterChain apiFilterChain(HttpSecurity http) throws Exception {
        http
            .securityMatcher("/api/**")
            .authorizeHttpRequests(auth -> auth.anyRequest().authenticated())
            .oauth2ResourceServer(oauth2 -> oauth2.jwt(Customizer.withDefaults()))
            .sessionManagement(s -> s.sessionCreationPolicy(STATELESS))
            .csrf(AbstractHttpConfigurer::disable);
        return http.build();
    }

    // Цепочка для UI — form login, sessions
    @Bean
    @Order(2)
    public SecurityFilterChain webFilterChain(HttpSecurity http) throws Exception {
        http
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/login", "/css/**").permitAll()
                .anyRequest().authenticated())
            .formLogin(form -> form.loginPage("/login"))
            .logout(logout -> logout.logoutSuccessUrl("/login?logout"));
        return http.build();
    }
}
```

Ключевое отличие `Spring Security 6`: `@EnableMethodSecurity` вместо `@EnableGlobalMethodSecurity`, lambda-DSL обязателен, `authorizeHttpRequests` вместо `authorizeRequests`.

## Q35. Как реализовать иерархию ролей в `Spring Security`?

Иерархия ролей даёт **наследование привилегий**: `ADMIN` автоматически получает всё, что доступно `MODERATOR` и `USER`. Без неё пришлось бы в каждой проверке перечислять все роли (`hasAnyRole('USER','MODERATOR','ADMIN')`), что легко забыть и ошибиться. С `RoleHierarchy` достаточно написать `hasRole('USER')`, и старшие роли пройдут проверку автоматически.

```java
@Configuration
@EnableMethodSecurity
public class RoleHierarchyConfig {

    @Bean
    public RoleHierarchy roleHierarchy() {
        return RoleHierarchyImpl.fromHierarchy("""
            ROLE_ADMIN > ROLE_MODERATOR
            ROLE_MODERATOR > ROLE_USER
            ROLE_USER > ROLE_GUEST
            """);
    }

    @Bean
    static MethodSecurityExpressionHandler methodSecurityExpressionHandler(
            RoleHierarchy roleHierarchy) {
        DefaultMethodSecurityExpressionHandler handler =
            new DefaultMethodSecurityExpressionHandler();
        handler.setRoleHierarchy(roleHierarchy);
        return handler;
    }
}
```

С этой конфигурацией `@PreAuthorize("hasRole('USER')")` будет пропускать и `ADMIN`, и `MODERATOR`.

## Q36. Как хранить пароли безопасно в `Java`?

Пароли **никогда** не хранятся в открытом виде — только хэш с солью, причём специальным **медленным** алгоритмом. Ключевой момент: обычные `SHA-256`/`MD5` для этого не годятся именно потому, что они быстрые — на GPU перебираются миллиарды хэшей в секунду. Нужны адаптивные функции (`bcrypt`, `Argon2`, `scrypt`, `PBKDF2`), которые специально сделаны вычислительно дорогими и настраиваемыми по сложности, чтобы перебор стал нерентабельным. Соль добавляется, чтобы одинаковые пароли давали разные хэши и не работали радужные таблицы.

| Алгоритм | Рекомендация | Описание |
|----------|--------------|----------|
| `bcrypt` | Рекомендуется (по умолчанию в Spring) | Адаптивный, настраиваемая сложность |
| `Argon2` | Рекомендуется | Победитель Password Hashing Competition |
| `scrypt` | Допустимо | Memory-hard |
| `PBKDF2` | Допустимо (NIST) | HMAC-based |
| `SHA-256` / `MD5` | **Нет!** | Слишком быстрые, уязвимы к brute force |

```java
@Configuration
public class PasswordConfig {

    @Bean
    public PasswordEncoder passwordEncoder() {
        // bcrypt — default, strength 10 (2^10 итераций)
        return new BCryptPasswordEncoder(12); // увеличиваем до 12
    }

    // Или DelegatingPasswordEncoder для миграции
    @Bean
    public PasswordEncoder delegatingPasswordEncoder() {
        return PasswordEncoderFactories.createDelegatingPasswordEncoder();
        // Хранит формат: {bcrypt}$2a$12$...
        // Позволяет мигрировать с одного алгоритма на другой
    }
}
```

**Важно:** использовать `char[]` вместо `String` для паролей в памяти (можно обнулить после использования); `String` остаётся в пуле строк JVM.

## Q37. (!) Какие типичные ошибки при реализации `JWT`?

Большинство уязвимостей `JWT` сводятся к двум корневым причинам: **наивная вера в payload без полной проверки** (`alg: none`, отсутствие проверки `iss`/`aud`) и **неправильное хранение/жизненный цикл токена** (секрет в коде, токен в `localStorage`, бесконечный TTL). Самая опасная — атака `alg: none`: если код доверяет полю `alg` из самого токена, злоумышленник ставит `none` и подаёт неподписанный токен. Поэтому алгоритм всегда задаёт сервер, а не токен.

| Ошибка | Последствие | Как правильно |
|--------|-------------|---------------|
| Хранение секрета в коде | Компрометация всех токенов | Vault / env variables |
| `alg: none` без валидации | Подделка токенов | Всегда проверять алгоритм |
| Слишком длинный TTL | Неотзываемость скомпрометированных | 5-15 минут + refresh |
| Хранение в `localStorage` | XSS → кража токена | `httpOnly` cookie или memory |
| Чувствительные данные в payload | Утечка при decode (Base64, не шифрование) | Только несекретные claims |
| Отсутствие проверки `aud` / `iss` | Подмена токена из другого сервиса | Всегда проверять audience и issuer |
| Симметричный ключ для нескольких сервисов | Любой сервис может выпускать токены | `RS256` / `ES256` (asymmetric) |

```java
// Правильная валидация JWT с проверкой issuer и audience
@Bean
public JwtDecoder jwtDecoder() {
    NimbusJwtDecoder decoder = NimbusJwtDecoder
        .withJwkSetUri("https://auth.example.com/.well-known/jwks.json")
        .build();

    decoder.setJwtValidator(new DelegatingOAuth2TokenValidator<>(
        JwtValidators.createDefaultWithIssuer("https://auth.example.com"),
        new JwtClaimValidator<>("aud",
            aud -> aud != null && ((List<?>) aud).contains("my-api")),
        new JwtTimestampValidator(Duration.ofSeconds(30)) // clock skew
    ));

    return decoder;
}
```

## Q38. Как реализовать `OAuth2 Backend for Frontend` (`BFF`) паттерн?

`BFF` (`Backend for Frontend`) решает главную головную боль `SPA` — **где хранить токены в браузере**. `localStorage` уязвим к `XSS`, поэтому BFF просто не отдаёт токены фронтенду вовсе: `Spring Cloud Gateway` (или отдельный backend) сам выступает `OAuth2 Client`, держит токены на сервере, а `SPA` общается с ним через `httpOnly` cookie, недоступную JavaScript. По сути проблему хранения токена в браузере убирают, перенося токен на сервер.

Потоки данных:

- `SPA / Browser` → `BFF / Gateway`: через `httpOnly` cookie.
- `BFF / Gateway` → `Resource Server`: с `Access Token`.
- `BFF / Gateway` → `Authorization Server`: `Code` + `PKCE`.

```java
// Spring Cloud Gateway как BFF
@Configuration
public class BffGatewayConfig {

    @Bean
    public SecurityWebFilterChain filterChain(ServerHttpSecurity http) {
        http
            .authorizeExchange(auth -> auth
                .pathMatchers("/", "/login/**").permitAll()
                .anyExchange().authenticated())
            .oauth2Login(Customizer.withDefaults()) // Gateway = OAuth2 client
            .csrf(csrf -> csrf
                .csrfTokenRepository(CookieServerCsrfTokenRepository.withHttpOnlyFalse()));
        return http.build();
    }
}
```

```yaml
spring:
  cloud:
    gateway:
      routes:
        - id: api
          uri: http://resource-server:8081
          predicates:
            - Path=/api/**
          filters:
            - TokenRelay  # пробрасывает access token downstream
```

**Преимущества:** токены не доступны JavaScript (защита от `XSS`), `CSRF`-защита через cookies, централизованное управление токенами.

## Q39. Как интегрировать `Keycloak` со `Spring Boot`?

`Keycloak` — open-source `Identity Provider`, который берёт на себя аутентификацию, выпуск токенов, `RBAC` и `MFA`, поддерживая `OIDC` и `SAML` из коробки. Со `Spring Boot` он интегрируется по стандарту `OIDC`, поэтому достаточно настроить приложение как `Resource Server` / OAuth2-клиент через `issuer-uri`. Единственная неочевидность — роли `Keycloak` кладёт в нестандартный claim `realm_access.roles`, поэтому нужен кастомный `JwtAuthenticationConverter`, чтобы превратить их в `GrantedAuthority`.

```yaml
# application.yml
spring:
  security:
    oauth2:
      resourceserver:
        jwt:
          issuer-uri: http://keycloak:8080/realms/my-realm
      client:
        registration:
          keycloak:
            client-id: my-app
            client-secret: ${KEYCLOAK_SECRET}
            scope: openid, profile, email
            authorization-grant-type: authorization_code
        provider:
          keycloak:
            issuer-uri: http://keycloak:8080/realms/my-realm
```

### Маппинг ролей из `Keycloak`

`Keycloak` хранит роли в `realm_access.roles` — нестандартный claim, нужен кастомный converter:

```java
@Bean
public JwtAuthenticationConverter keycloakJwtConverter() {
    JwtAuthenticationConverter converter = new JwtAuthenticationConverter();
    converter.setJwtGrantedAuthoritiesConverter(jwt -> {
        Map<String, Object> realmAccess = jwt.getClaimAsMap("realm_access");
        if (realmAccess == null) return Set.of();

        @SuppressWarnings("unchecked")
        List<String> roles = (List<String>) realmAccess.get("roles");

        return roles.stream()
            .map(role -> new SimpleGrantedAuthority("ROLE_" + role.toUpperCase()))
            .collect(Collectors.toSet());
    });
    return converter;
}
```

## Q40. Как тестировать безопасность в `Spring`-приложении?

`spring-security-test` даёт инструменты, чтобы тестировать авторизацию **без реального логина** — вы подставляете нужную идентичность прямо в тест. Главное правило: проверять не только «разрешённый» путь, но и **отказы** — что `USER` получает `403` там, где можно только `ADMIN`, а аноним — `401`. Под разные механизмы есть свои хелперы: `@WithMockUser` для роль-based проверок, `jwt()` для Resource Server, `oidcLogin()` для OAuth2-логина.

### Unit-тесты с `@WithMockUser`

```java
@WebMvcTest(PostController.class)
class PostControllerSecurityTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    @WithMockUser(roles = "ADMIN")
    void adminCanDeletePost() throws Exception {
        mockMvc.perform(delete("/api/posts/1"))
            .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles = "USER")
    void userCannotDeletePost() throws Exception {
        mockMvc.perform(delete("/api/posts/1"))
            .andExpect(status().isForbidden());
    }

    @Test
    void unauthenticatedGetsForbidden() throws Exception {
        mockMvc.perform(get("/api/posts"))
            .andExpect(status().isUnauthorized());
    }
}
```

### Тестирование `JWT Resource Server`

```java
@SpringBootTest
@AutoConfigureMockMvc
class ResourceServerTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void validJwtAllowsAccess() throws Exception {
        mockMvc.perform(get("/api/data")
            .with(jwt()
                .jwt(j -> j
                    .subject("user1")
                    .claim("roles", List.of("USER")))
                .authorities(new SimpleGrantedAuthority("ROLE_USER"))))
            .andExpect(status().isOk());
    }

    @Test
    void invalidScopeReturnsForbidden() throws Exception {
        mockMvc.perform(get("/api/admin")
            .with(jwt().jwt(j -> j.claim("scope", "read"))))
            .andExpect(status().isForbidden());
    }
}
```

### Тестирование `OAuth2` логина

```java
@Test
void oauth2LoginRedirects() throws Exception {
    mockMvc.perform(get("/api/profile")
        .with(oidcLogin()
            .idToken(token -> token
                .claim("name", "Test User")
                .claim("email", "test@example.com"))))
        .andExpect(status().isOk());
}
```

## Q41. (!) Чем `JWT` отличается от `Session`-based аутентификации: когда что выбирать?

Это один из самых частых вопросов на собеседованиях, и сильный ответ не «JWT лучше», а **формулировка компромисса**. Корень различия один: session хранит состояние на сервере, JWT — у клиента. Отсюда зеркальный размен: session легко отзывается, но плохо масштабируется без общего хранилища; JWT масштабируется без shared state, но его почти нельзя отозвать до истечения `exp`. Поэтому выбор диктуется архитектурой: монолиту с браузером и требованием мгновенного отзыва (банки) — session, микросервисам и mobile/SPA — JWT.

### Сравнение подходов

| Критерий | Session-based | JWT (Stateless) |
|---------|--------------|-----------------|
| Хранение состояния | На сервере (in-memory, Redis) | В токене (у клиента) |
| Масштабирование | Требует sticky sessions или shared store | Горизонтальное без shared state |
| Отзыв | Мгновенный (удалить из store) | Сложный (до истечения `exp`) |
| Размер | Cookie ~50 байт | JWT ~500-2000 байт |
| Производительность | Чтение из store на каждый запрос | Верификация подписи (CPU) |
| Поддержка микросервисов | Сложно (нужен shared session store) | Нативно (stateless) |
| Invalidation при logout | Немедленный | Только через blacklist |
| Подходит для | Monolith, SPA с BFF | Микросервисы, API, mobile |

### Session-based: реализация в Spring Security

```java
@Bean
SecurityFilterChain sessionBasedChain(HttpSecurity http,
                                       SessionRegistry sessionRegistry) throws Exception {
    http
        .sessionManagement(session -> session
            .sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED)
            .maximumSessions(1)                    // один активный сеанс на пользователя
            .maxSessionsPreventsLogin(false)        // при новом логине инвалидировать старый
            .sessionRegistry(sessionRegistry())
            .and()
            .sessionFixation().newSession()         // новая сессия после логина (защита от fixation)
            .invalidSessionUrl("/login?expired"))
        .rememberMe(me -> me
            .tokenValiditySeconds(86400 * 7)        // 7 дней
            .key("${app.remember-me-key}"));
    return http.build();
}

// Принудительный logout из всех сессий пользователя (при компрометации)
public void invalidateAllSessions(String username) {
    List<SessionInformation> sessions =
        sessionRegistry.getAllSessions(username, false);
    sessions.forEach(SessionInformation::expireNow);
}
```

### JWT: реализация с blacklist для logout

```java
@Service
public class JwtBlacklistService {

    // Redis для быстрого lookup; TTL = remaining token lifetime
    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    public void revokeToken(String jti, Instant expiry) {
        long ttl = Duration.between(Instant.now(), expiry).getSeconds();
        if (ttl > 0) {
            redisTemplate.opsForValue().set(
                "jwt:blacklist:" + jti, "revoked",
                ttl, TimeUnit.SECONDS);
        }
    }

    public boolean isRevoked(String jti) {
        return Boolean.TRUE.equals(
            redisTemplate.hasKey("jwt:blacklist:" + jti));
    }
}

// JWT валидатор с проверкой blacklist
@Component
public class BlacklistJwtDecoder implements JwtDecoder {

    private final NimbusJwtDecoder delegate;
    private final JwtBlacklistService blacklistService;

    @Override
    public Jwt decode(String token) throws JwtException {
        Jwt jwt = delegate.decode(token);
        String jti = jwt.getId();
        if (jti != null && blacklistService.isRevoked(jti)) {
            throw new JwtException("Token has been revoked");
        }
        return jwt;
    }
}
```

### Когда что выбирать

```
Выбирайте Session-based, если:
✓ Monolithic приложение
✓ Нужен немедленный отзыв (банки, медицина)
✓ Веб-браузер — основной клиент
✓ Нет требований горизонтального масштабирования без shared state

Выбирайте JWT, если:
✓ Микросервисная архитектура
✓ Mobile/SPA клиенты
✓ API, используемые третьими сторонами
✓ Горизонтальное масштабирование без sticky sessions
✓ Cross-domain/cross-origin сценарии
```

## Q42. Как реализовать `API Key` аутентификацию в `Spring Security`?

`API Key` — простейший механизм machine-to-machine аутентификации: клиент шлёт долгоживущий ключ в заголовке (например, `X-API-Key`). Применяется там, где нет интерактивного пользователя и не нужна вся машинерия OAuth — публичные API, внутренние интеграции, webhooks. Два правила, которые отличают грамотную реализацию от наивной: ключ хранится в БД **только как хэш** (как пароль, не в открытом виде), и сравнение делается безопасно. По стойкости `API Key` слабее OAuth-токенов — у него нет срока жизни и scope-ов «из коробки», поэтому их добавляют вручную (`expiresAt`, `scopes`).

### Кастомный фильтр API Key

```java
@Component
public class ApiKeyAuthFilter extends OncePerRequestFilter {

    private static final String API_KEY_HEADER = "X-API-Key";
    private final ApiKeyService apiKeyService;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                     HttpServletResponse response,
                                     FilterChain chain)
            throws ServletException, IOException {

        String apiKey = request.getHeader(API_KEY_HEADER);

        if (apiKey == null || apiKey.isBlank()) {
            chain.doFilter(request, response);
            return;
        }

        // Валидация ключа (безопасное сравнение)
        Optional<ApiKeyPrincipal> principal = apiKeyService.validate(apiKey);

        if (principal.isPresent()) {
            ApiKeyAuthenticationToken auth = new ApiKeyAuthenticationToken(
                principal.get(),
                principal.get().getAuthorities());
            auth.setDetails(new WebAuthenticationDetailsSource()
                .buildDetails(request));
            SecurityContextHolder.getContext().setAuthentication(auth);
        }

        chain.doFilter(request, response);
    }
}

// Кастомный Authentication Token
public class ApiKeyAuthenticationToken extends AbstractAuthenticationToken {
    private final ApiKeyPrincipal principal;

    public ApiKeyAuthenticationToken(ApiKeyPrincipal principal,
                                      Collection<? extends GrantedAuthority> authorities) {
        super(authorities);
        this.principal = principal;
        setAuthenticated(true);
    }

    @Override public Object getPrincipal() { return principal; }
    @Override public Object getCredentials() { return null; }
}
```

### Хранение и валидация API ключей

```java
@Entity
@Table(name = "api_keys")
public class ApiKeyEntity {
    @Id private Long id;
    @Column(unique = true)
    private String keyHash;          // SHA-256 хэш ключа (не сам ключ!)
    private String clientName;
    private boolean active;
    private Instant expiresAt;
    @ElementCollection
    private Set<String> scopes;      // read, write, admin
    private Instant lastUsedAt;
    private long requestCount;
}

@Service
public class ApiKeyService {

    @Cacheable(value = "apiKeys", key = "#apiKey.substring(0, 8)")
    public Optional<ApiKeyPrincipal> validate(String apiKey) {
        // Никогда не храним ключ в открытом виде
        String keyHash = DigestUtils.sha256Hex(apiKey);

        return apiKeyRepo.findByKeyHash(keyHash)
            .filter(ApiKeyEntity::isActive)
            .filter(key -> key.getExpiresAt() == null ||
                           key.getExpiresAt().isAfter(Instant.now()))
            .map(key -> {
                // Обновляем статистику использования
                key.setLastUsedAt(Instant.now());
                key.setRequestCount(key.getRequestCount() + 1);
                apiKeyRepo.save(key);
                return new ApiKeyPrincipal(key.getClientName(), key.getScopes());
            });
    }

    public String generateApiKey() {
        // Генерация: prefix для идентификации + random bytes
        byte[] bytes = new byte[32];
        new SecureRandom().nextBytes(bytes);
        String key = "sk_" + Base64.getUrlEncoder().withoutPadding()
            .encodeToString(bytes);
        // Сохраняем только хэш
        String hash = DigestUtils.sha256Hex(key);
        apiKeyRepo.save(new ApiKeyEntity(hash, ...));
        return key; // возвращаем только при создании
    }
}
```

### Интеграция в SecurityFilterChain

```java
@Bean
SecurityFilterChain apiSecurityChain(HttpSecurity http,
                                      ApiKeyAuthFilter apiKeyFilter) throws Exception {
    http
        .securityMatcher("/api/**")
        .csrf(csrf -> csrf.disable())
        .sessionManagement(s -> s.sessionCreationPolicy(STATELESS))
        .addFilterBefore(apiKeyFilter, UsernamePasswordAuthenticationFilter.class)
        .authorizeHttpRequests(authz -> authz
            .requestMatchers("/api/public/**").permitAll()
            .requestMatchers("/api/admin/**").hasRole("API_ADMIN")
            .anyRequest().authenticated());
    return http.build();
}
```

## Q43. (!) Что такое `Zero Trust` и как реализовать его принципы в Java-приложении?

**Zero Trust** — модель «никогда не доверяй, всегда проверяй», противоположность периметровой безопасности (castle-and-moat). Если Q16 вводит концепцию, то здесь важно показать, как она ложится на конкретный Java-стек: «внутренней доверенной сети» нет, поэтому **каждый сервис сам валидирует токен** (Resource Server), сервисы аутентифицируют друг друга по `mTLS`, права выдаются по минимуму, а каждый доступ логируется. Ниже — три кита и их реализация на Spring.

### Три кита Zero Trust

```
1. Verify Explicitly
   - Аутентификация каждого запроса (нет "внутренней" сети)
   - Контекстная аутентификация: IP, device, время, поведение
   - MFA для критичных операций

2. Least Privilege Access
   - Минимальные права для каждого субъекта
   - JIT (Just-in-Time) доступ: права выдаются на время задачи
   - Регулярный review и отзыв неиспользуемых прав

3. Assume Breach
   - Проектирование с учётом того, что нарушение произойдёт
   - Микросегментация: каждый сервис изолирован
   - Непрерывный мониторинг и обнаружение аномалий
```

### Реализация в микросервисах на Spring

```java
// 1. Каждый сервис проверяет токен самостоятельно (нет доверенной внутренней сети)
@Configuration
public class ZeroTrustResourceServerConfig {

    @Bean
    SecurityFilterChain chain(HttpSecurity http) throws Exception {
        http
            // Нет white-list по IP — проверяем каждый запрос
            .oauth2ResourceServer(oauth2 -> oauth2
                .jwt(jwt -> jwt
                    .decoder(jwtDecoder())
                    .jwtAuthenticationConverter(jwtConverter())))
            // Запрет всего, что явно не разрешено
            .authorizeHttpRequests(authz -> authz
                .anyRequest().authenticated());
        return http.build();
    }
}

// 2. Propagation токена между сервисами
@Bean
WebClient serviceToServiceClient(OAuth2AuthorizedClientManager manager) {
    // Автоматически добавляет Bearer token в исходящие запросы
    var oauth2 = new ServletOAuth2AuthorizedClientExchangeFilterFunction(manager);
    oauth2.setDefaultClientRegistrationId("internal-service");
    return WebClient.builder()
        .apply(oauth2.oauth2Configuration())
        .build();
}

// 3. Контекстная авторизация (учитываем дополнительные факторы)
@Component
public class ZeroTrustAuthorizationManager
        implements AuthorizationManager<RequestAuthorizationContext> {

    @Override
    public AuthorizationDecision check(
            Supplier<Authentication> authentication,
            RequestAuthorizationContext context) {

        Authentication auth = authentication.get();
        HttpServletRequest request = context.getRequest();

        // Проверяем не только роль, но и контекст запроса
        boolean isAuthenticated = auth != null && auth.isAuthenticated();
        boolean hasRequiredScope = hasScope(auth, "api:read");
        boolean isKnownDevice = deviceTrustService.isTrusted(
            request.getHeader("X-Device-ID"), auth.getName());
        boolean withinRateLimit = rateLimitService.isAllowed(auth.getName());

        return new AuthorizationDecision(
            isAuthenticated && hasRequiredScope &&
            isKnownDevice && withinRateLimit);
    }
}
```

### Zero Trust checklist для Java-разработчика

| Принцип | Мера | Инструмент |
|---------|------|-----------|
| Verify explicitly | JWT на каждом сервисе | Spring Security Resource Server |
| Service identity | mTLS между сервисами | Istio, Spring + X.509 |
| Least privilege | RBAC/ABAC с минимальными правами | Spring `@PreAuthorize` |
| Network segmentation | Network Policies | Kubernetes Calico/Cilium |
| Continuous monitoring | Audit log каждого доступа | Micrometer + SIEM |
| Assume breach | Шифрование данных в покое | Vault, k8s etcd encryption |

## Q44. Как реализовать `RBAC` и `ABAC` совместно в `Spring Security`?

На практике `RBAC` и `ABAC` не конкурируют, а дополняют друг друга по принципу «грубо, потом точно»: `RBAC` дешёвой проверкой по роли отсекает основную массу запросов (есть ли вообще роль `USER`?), а `ABAC` уже на оставшихся применяет тонкое правило к конкретному объекту (а это **его** документ? тот же отдел?). Порядок важен и для производительности: незачем грузить объект из БД и считать атрибуты, если у пользователя нет даже базовой роли. В `Spring` это удобно собирается в одном `@PreAuthorize`: `hasAnyRole(...)` (RBAC) `and hasPermission(...)` (ABAC через `PermissionEvaluator`).

### Архитектура комбинированной авторизации

```
Запрос → [RBAC: есть ли роль USER?] → Да → [ABAC: принадлежит ли ресурс пользователю?]
                                    → Нет → 403
                                                       → Да → 200
                                                       → Нет → 403
```

### Реализация

```java
// Доменная модель — ресурс с владельцем и тегами
@Entity
public class Document {
    @Id private Long id;
    private String ownerId;
    private String department;       // ABAC-атрибут: отдел
    private Classification classification;  // PUBLIC, INTERNAL, SECRET
}

// ABAC политики как Spring beans
@Component
public class DocumentAuthorizationPolicy {

    // Правило: пользователь читает документ если:
    // - он владелец, ИЛИ
    // - документ PUBLIC, ИЛИ
    // - документ INTERNAL и пользователь в том же отделе
    public boolean canRead(UserDetails user, Document document) {
        String userId = user.getUsername();
        UserProfile profile = userProfileService.getProfile(userId);

        return document.getOwnerId().equals(userId)
            || document.getClassification() == Classification.PUBLIC
            || (document.getClassification() == Classification.INTERNAL
                && document.getDepartment().equals(profile.getDepartment()));
    }

    public boolean canWrite(UserDetails user, Document document) {
        return document.getOwnerId().equals(user.getUsername())
            || user.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
    }
}

// PermissionEvaluator — интегрируем ABAC в Spring Security
@Component
public class DocumentPermissionEvaluator implements PermissionEvaluator {

    private final DocumentAuthorizationPolicy policy;
    private final DocumentRepository documentRepo;

    @Override
    public boolean hasPermission(Authentication auth, Object targetDomainObject,
                                  Object permission) {
        if (!(targetDomainObject instanceof Document doc)) return false;
        UserDetails user = (UserDetails) auth.getPrincipal();
        return switch (permission.toString()) {
            case "READ"  -> policy.canRead(user, doc);
            case "WRITE" -> policy.canWrite(user, doc);
            default -> false;
        };
    }

    @Override
    public boolean hasPermission(Authentication auth, Serializable targetId,
                                  String targetType, Object permission) {
        if ("Document".equals(targetType)) {
            Document doc = documentRepo.findById((Long) targetId).orElse(null);
            return doc != null && hasPermission(auth, doc, permission);
        }
        return false;
    }
}

// Использование: RBAC (hasRole) + ABAC (hasPermission) вместе
@RestController
@RequestMapping("/api/documents")
public class DocumentController {

    // RBAC: только USER или ADMIN, ABAC: только если есть право READ
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN') and hasPermission(#id, 'Document', 'READ')")
    public Document getDocument(@PathVariable Long id) {
        return documentService.findById(id);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN') and hasPermission(#id, 'Document', 'WRITE')")
    public Document updateDocument(@PathVariable Long id,
                                    @RequestBody DocumentRequest request) {
        return documentService.update(id, request);
    }

    // Только ADMIN может создавать SECRET документы
    @PostMapping
    @PreAuthorize("hasRole('USER') and " +
        "(#request.classification != 'SECRET' or hasRole('ADMIN'))")
    public Document createDocument(@RequestBody DocumentRequest request) {
        return documentService.create(request);
    }
}
```

### Конфигурация MethodSecurity

```java
@Configuration
@EnableMethodSecurity(prePostEnabled = true)
public class MethodSecurityConfig {

    @Bean
    MethodSecurityExpressionHandler methodSecurityExpressionHandler(
            DocumentPermissionEvaluator permissionEvaluator) {
        DefaultMethodSecurityExpressionHandler handler =
            new DefaultMethodSecurityExpressionHandler();
        handler.setPermissionEvaluator(permissionEvaluator);
        return handler;
    }
}
```

## Q45. (!) Как реализовать `mTLS` аутентификацию между микросервисами?

`mTLS` (mutual TLS) — стандартный способ аутентификации сервиса сервисом в `Zero Trust`-архитектуре. Каждый сервис носит собственный `X.509`-сертификат, и при соединении **обе стороны** проверяют сертификат друг друга по доверенному `CA` — так сервис B точно знает, что к нему пришёл именно сервис A, а не самозванец из той же сети. Главный вопрос на собеседовании — не «как настроить SSL», а **кто управляет жизненным циклом сертификатов**: вручную это не масштабируется, поэтому в проде используют автоматику (`cert-manager`, Istio/SPIRE, Vault PKI) с регулярной ротацией. Ниже — два подхода: вручную на голом Spring и декларативно через Istio.

### Как работает mTLS

Участники: `Service A`, `Service B`. По шагам:

1. `Service A → Service B`: `TLS ClientHello`.
2. `Service B → Service A`: `Server Certificate` (`CN=service-b`).
3. `Service A`: верифицирует сертификат `B` по truststore.
4. `Service A → Service B`: `Client Certificate` (`CN=service-a`).
5. `Service B`: верифицирует сертификат `A` по truststore.
6. `Service A → Service B`: `Encrypted Request`; `Service B → Service A`: `Encrypted Response`.

### Без Service Mesh: Spring Boot + X.509

```yaml
# service-b/application.yml — сервер требует клиентский сертификат
server:
  ssl:
    enabled: true
    key-store: classpath:service-b-keystore.p12
    key-store-password: ${KS_PASSWORD}
    key-store-type: PKCS12
    trust-store: classpath:internal-ca-truststore.p12
    trust-store-password: ${TS_PASSWORD}
    client-auth: REQUIRE
```

```java
// Service B: аутентификация по CN сертификата Service A
@Configuration
public class ServiceBSecurityConfig {

    @Bean
    SecurityFilterChain chain(HttpSecurity http) throws Exception {
        http
            .x509(x509 -> x509
                // CN=service-a → принципал "service-a"
                .subjectPrincipalRegex("CN=(.*?)(?:,|$)")
                .userDetailsService(serviceUserDetailsService()))
            .authorizeHttpRequests(authz -> authz
                // Только service-a может вызывать internal endpoints
                .requestMatchers("/internal/**")
                    .hasAuthority("SERVICE_SERVICE_A")
                .anyRequest().denyAll());
        return http.build();
    }

    @Bean
    UserDetailsService serviceUserDetailsService() {
        // Маппинг CN сертификата → роли/полномочия
        Map<String, UserDetails> services = Map.of(
            "service-a", User.withUsername("service-a")
                .password("")
                .authorities("SERVICE_SERVICE_A")
                .build(),
            "service-b", User.withUsername("service-b")
                .password("")
                .authorities("SERVICE_SERVICE_B")
                .build()
        );
        return username -> Optional.ofNullable(services.get(username))
            .orElseThrow(() -> new UsernameNotFoundException(
                "Unknown service: " + username));
    }
}
```

```java
// Service A: клиент с сертификатом
@Configuration
public class ServiceAClientConfig {

    @Bean
    WebClient serviceBClient() throws Exception {
        KeyStore keyStore = KeyStore.getInstance("PKCS12");
        try (var is = new ClassPathResource("service-a-keystore.p12").getInputStream()) {
            keyStore.load(is, System.getenv("KS_PASSWORD").toCharArray());
        }

        KeyStore trustStore = KeyStore.getInstance("PKCS12");
        try (var is = new ClassPathResource("internal-ca-truststore.p12").getInputStream()) {
            trustStore.load(is, System.getenv("TS_PASSWORD").toCharArray());
        }

        SslContext sslContext = SslContextBuilder.forClient()
            .keyManager(
                KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm())
                    .also(f -> f.init(keyStore, System.getenv("KEY_PASSWORD").toCharArray())))
            .trustManager(
                TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm())
                    .also(f -> f.init(trustStore)))
            .protocols("TLSv1.3", "TLSv1.2")
            .build();

        return WebClient.builder()
            .clientConnector(new ReactorClientHttpConnector(
                HttpClient.create().secure(spec -> spec.sslContext(sslContext))))
            .baseUrl("https://service-b:8443")
            .build();
    }
}
```

### С Istio Service Mesh (декларативный подход)

```yaml
# PeerAuthentication: STRICT mTLS в production namespace
apiVersion: security.istio.io/v1beta1
kind: PeerAuthentication
metadata:
  name: default
  namespace: production
spec:
  mtls:
    mode: STRICT
---
# AuthorizationPolicy: service-a может вызывать только /internal/** service-b
apiVersion: security.istio.io/v1beta1
kind: AuthorizationPolicy
metadata:
  name: service-b-policy
  namespace: production
spec:
  selector:
    matchLabels:
      app: service-b
  rules:
    - from:
        - source:
            principals:
              - "cluster.local/ns/production/sa/service-a"
      to:
        - operation:
            paths: ["/internal/*"]
            methods: ["GET", "POST"]
```

### Управление сертификатами

| Подход | Когда | Инструмент |
|--------|-------|-----------|
| Ручной keystore | Dev/test | `keytool`, `openssl` |
| cert-manager (k8s) | Staging/prod без mesh | `cert-manager` + Let's Encrypt / internal CA |
| Istio SPIFFE/SPIRE | Production с service mesh | Автоматически, ротация каждые 24ч |
| Vault PKI Engine | Enterprise | HashiCorp Vault, TTL-based rotation |

Подробнее о стратегиях тестирования — в [Integration Testing](../testing/integration-testing-interview.md) и [Unit Testing](../testing/unit-testing-interview.md).

---

## See also

- [Application Security](application-security-interview.md) — общая безопасность приложений, Defense in Depth
- [Spring Security](../frameworks/spring/spring-security-interview.md) — конфигурация и фильтры Spring Security
- [OAuth 2.0](oauth2-interview.md) — детальный разбор протокола OAuth 2.0, flows, токены
- [OWASP Top 10](owasp-top10-interview.md) — A07 Auth Failures, Broken Access Control
- [Микросервисы](../architecture/microservices-interview.md) — безопасность в микросервисной архитектуре, service mesh
- [Распределённые системы](../architecture/distributed-systems-interview.md) — безопасность на уровне инфраструктуры
- [Kubernetes](../devops/kubernetes-interview.md) — ServiceAccount, RBAC, Workload Identity
- [Архитектура баз данных](../databases/database-architecture-interview.md) — Row-Level Security, шифрование данных
- [HTTP и REST](../api/http-rest-interview.md) — TLS, HTTPS, заголовки безопасности

- [Application Security](application-security-interview.md)
- [JWT](jwt-interview.md)
- [mTLS (Mutual TLS)](mtls-interview.md)
- [OAuth2](oauth2-interview.md)
- [OWASP Top 10](owasp-top10-interview.md)
- [Secrets Management](secrets-management-interview.md)
