---
title: "Вопросы на собеседовании: Authentication and Authorization Patterns"
description: "Комплексное руководство по вопросам собеседования на тему Authentication and Authorization Patterns для Senior Java"
tags: ["interview", "security", "authentication-authorization-patterns-interview"]
difficulty: "intermediate"
prerequisites: []
next: []
updated: "2026-02-11"
---
# Вопросы на собеседовании: `Authentication` and `Authorization Patterns`

Комплексное руководство по вопросам собеседования на тему `Authentication` and `Authorization Patterns` для `Senior Java`
`Developer`. Включает детальные объяснения концепций, практические примеры на `Java` + `Spring`, best practices и
troubleshooting.

Дата последнего обновления: 2026-02-04

Краткое введение: Authentication and Authorization Patterns для Senior Java Developer.

## Полезные ссылки

### Официальная документация

- [OAuth 2.0 Authorization Framework](https://oauth.net/2/)
- [JWT (RFC 7519)](https://datatracker.ietf.org/doc/html/rfc7519)
- [Spring Security Documentation](https://docs.spring.io/spring-security/reference/)
- [Auth0 Patterns](https://auth0.com/docs)
- [Okta Developer Guides](https://developer.okta.com/docs/)
- [Keycloak Documentation](https://www.keycloak.org/documentation)

### См. также

- [`application-security-interview.md`](application-security-interview.md) — общая безопасность приложений
- [`../frameworks/spring/spring-security-interview.md`](../frameworks/spring/spring-security-interview.md) — Spring Security

## Содержание

- [Полезные ссылки](#полезные-ссылки)

**Основы аутентификации**
- [Q1. Какие основные паттерны аутентификации существуют?](#q1-какие-основные-паттерны-аутентификации-существуют)
- [Q2. Что такое OAuth 2.0 и как он работает?](#q2-что-такое-oauth-20-и-как-он-работает)
- [Q3. Как работает JWT аутентификация?](#q3-как-работает-jwt-аутентификация)
- [Q4. Что такое RBAC и ABAC?](#q4-что-такое-rbac-и-abac)

**Безопасность в распределённых системах**
- [Q5. Как реализовать безопасность в микросервисах?](#q5-как-реализовать-безопасность-в-микросервисах)
- [Q6. Что такое SAML и когда его использовать?](#q6-что-такое-saml-и-когда-его-использовать)
- [Q7. Как реализовать Multi-Factor Authentication?](#q7-как-реализовать-multi-factor-authentication)
- [Q8. Какие паттерны сессионного управления существуют?](#q8-какие-паттерны-сессионного-управления-существуют)
- [Q9. Как защититься от распространенных атак?](#q9-как-защититься-от-распространенных-атак)
- [Q10. Как реализовать API Gateway Security?](#q10-как-реализовать-api-gateway-security)

**SSO и Identity Federation**
- [Q11. Что такое OpenID Connect и как он расширяет OAuth 2.0?](#q11-что-такое-openid-connect-и-как-он-расширяет-oauth-20)
- [Q12. Как реализовать Single Sign-On (SSO)?](#q12-как-реализовать-single-sign-on-sso)
- [Q13. Что такое Claims-based аутентификация?](#q13-что-такое-claims-based-аутентификация)
- [Q14. Как обеспечить безопасность токенов (JWT refresh, rotation)?](#q14-как-обеспечить-безопасность-токенов-jwt-refresh-rotation)
- [Q15. Что такое Zero Trust Security Model?](#q15-что-такое-zero-trust-security-model)

**Защита API и Rate Limiting**
- [Q16. Как реализовать Rate Limiting для защиты API?](#q16-как-реализовать-rate-limiting-для-защиты-api)
- [Q17. Что такое mTLS и когда его использовать?](#q17-что-такое-mtls-и-когда-его-использовать)
- [Q18. Как реализовать аудит и логирование событий безопасности?](#q18-как-реализовать-аудит-и-логирование-событий-безопасности)
- [Q19. Что такое Context-based Access Control?](#q19-что-такое-context-based-access-control)
- [Q20. Как обеспечить безопасность в Service Mesh?](#q20-как-обеспечить-безопасность-в-service-mesh)

**Продвинутые паттерны авторизации**
- [Q21. Что такое Identity Federation?](#q21-что-такое-identity-federation)
- [Q22. Как реализовать Step-Up Authentication?](#q22-как-реализовать-step-up-authentication)
- [Q23. Что такое Passwordless Authentication?](#q23-что-такое-passwordless-authentication)
- [Q24. Как защитить GraphQL API?](#q24-как-защитить-graphql-api)
- [Q25. Что такое Delegated Authorization?](#q25-что-такое-delegated-authorization)
- [Q26. Как реализовать Fine-Grained Authorization?](#q26-как-реализовать-fine-grained-authorization)
- [Q27. Что такое Token Binding и зачем он нужен?](#q27-что-такое-token-binding-и-зачем-он-нужен)
- [Q28. Как обеспечить безопасность WebSocket соединений?](#q28-как-обеспечить-безопасность-websocket-соединений)
- [Q29. Что такое Proof Key for Code Exchange (PKCE)?](#q29-что-такое-proof-key-for-code-exchange-pkce)
- [Q30. Как реализовать Dynamic Authorization?](#q30-как-реализовать-dynamic-authorization)

## Q1. Какие основные паттерны аутентификации существуют?

### 1. `Basic Authentication`

Описание: Простейший метод, использующий username:password в `HTTP` заголовке. Как работает:

```java
// Spring Security конфигурация
@Configuration
public class BasicAuthConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.authorizeRequests().anyRequest().authenticated().and().httpBasic(); // Включение Basic Authentication

        return http.build();
    }

    @Bean
    public UserDetailsService userDetailsService() {
        UserDetails user = User.withDefaultPasswordEncoder().username("user").password("password").roles("USER").build();

        return new InMemoryUserDetailsManager(user);
    }
}
```

Плюсы:

- Простота реализации
- Встроенная поддержка в `HTTP`

Минусы:

- Небезопасно без `HTTPS`
- Нет возможности logout
- Пароль передается при каждом запросе

### 2. `Session-Based Authentication`

Описание: Использование серверных сессий для отслеживания аутентифицированных пользователей. Как работает:

```java

@Controller
public class LoginController {

    @PostMapping("/login")
    public String login(@ModelAttribute LoginForm form,
                        HttpSession session,
                        RedirectAttributes redirectAttrs) {

        User user = userService.authenticate(form.getUsername(), form.getPassword());
        if (user != null) {
            // Сохранение пользователя в сессии
            session.setAttribute("user", user);
            session.setMaxInactiveInterval(30 * 60); // 30 минут

            return "redirect:/dashboard";
        } else {
            redirectAttrs.addFlashAttribute("error", "Invalid credentials");
            return "redirect:/login";
        }
    }

    @PostMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/login";
    }
}
```

Плюсы:

- Безопасность (сессия на сервере)
- Возможность logout
- Поддержка сложных сценариев

Минусы:

- `State` на сервере
- Проблемы масштабирования
- `CSRF` уязвимости

### 3. `Token-Based Authentication`

Описание: Использование токенов (`JWT`, OAuth) для аутентификации.Как работает:

```java

@Service
public class TokenAuthenticationService {

    private final JwtService jwtService;
    private final UserRepository userRepository;

    public String authenticateAndGenerateToken(String username, String password) {
        User user = userRepository.findByUsername(username).orElseThrow(() -> new BadCredentialsException("User not found"));

        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new BadCredentialsException("Invalid password");
        }

        // Генерация токена
        return jwtService.generateToken(user);
    }

    public User validateTokenAndGetUser(String token) {
        String username = jwtService.extractUsername(token);
        User user = userRepository.findByUsername(username).orElseThrow(() -> new UsernameNotFoundException("User not found"));

        if (!jwtService.isTokenValid(token, user)) {
            throw new InvalidTokenException("Invalid token");
        }

        return user;
    }
}
```

Плюсы:

- `Stateless` (нет состояния на сервере)
- Масштабируемость
- Подходит для микросервисов

Минусы:

- Неотзываемость токенов (до истечения срока)
- Больший размер запросов
- Сложность реализации

### 4. `Certificate-Based Authentication`

Описание: Аутентификация с использованием цифровых сертификатов.Как работает:

```java

@Configuration
public class CertificateAuthConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.authorizeRequests().anyRequest().authenticated().and().x509().subjectPrincipalRegex("CN=(.*?)(?:,|$)").userDetailsService(userDetailsService());

        return http.build();
    }

    @Bean
    public UserDetailsService userDetailsService() {
        return new X509UserDetailsService();
    }
}

@Service
public class X509UserDetailsService implements UserDetailsService {

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        // Поиск пользователя по CN из сертификата
        User user = userRepository.findByCertificateCommonName(username);

        return User.builder().username(user.getUsername()).password("") // Пароль не нужен для сертификатов.authorities(user.getRoles().stream().map(role -> new SimpleGrantedAuthority("ROLE_" + role)).collect(Collectors.toList())).build();
    }
}
```

Плюсы:

- Высокая безопасность
- Двусторонняя аутентификация
- Не требует хранения паролей

Минусы:

- Сложность развертывания
- Требует `PKI` инфраструктуры
- Проблемы с мобильными устройствами

## Q2. Что такое `OAuth 2.0` и как он работает?

`OAuth 2.0` — это протокол авторизации, который позволяет приложениям получать ограниченный доступ к ресурсам пользователя
без передачи учетных данных. ### Роли в `OAuth 2.0`

#### 1. `Resource Owner` (Владелец ресурса)

Пользователь, который авторизует доступ к своим данным. #### 2. `Client` (Клиент)
Приложение, запрашивающее доступ к ресурсам. #### 3. `Authorization Server` (Сервер авторизации)
Сервер, который аутентифицирует пользователя и выдает токены. #### 4. `Resource Server` (Сервер ресурсов)
Сервер, который хранит защищенные ресурсы. ### `Grant Types` (Типы грантов)

#### 1. `Authorization Code Grant`

Описание: Наиболее безопасный тип гранта для веб-приложений.Поток:

```text
1. Client -> Authorization Server: Запрос авторизации
 GET /authorize?response_type=code&client_id=CLIENT_ID&redirect_uri=REDIRECT_URI&scope=SCOPE

2. Authorization Server -> Resource Owner: Запрос согласия

3. Resource Owner -> Authorization Server: Согласие

4. Authorization Server -> Client: Authorization Code
 REDIRECT_URI?code=AUTHORIZATION_CODE

5. Client -> Authorization Server: Обмен кода на токен
 POST /token
 grant_type=authorization_code&code=AUTH_CODE&redirect_uri=REDIRECT_URI&client_id=CLIENT_ID&client_secret=CLIENT_SECRET

6. Authorization Server -> Client: Access Token + Refresh Token
```

Реализация:

```java

@RestController
@RequestMapping("/oauth")
public class OAuthController {

    private final OAuth2AuthorizedClientService authorizedClientService;

    @GetMapping("/login")
    public String login() {
        return "redirect:/oauth2/authorization/google";
    }

    @GetMapping("/callback")
    public String callback(@RegisteredOAuth2AuthorizedClient OAuth2AuthorizedClient client) {
        // Токен доступен через client.getAccessToken()
        return "redirect:/dashboard";
    }

    @GetMapping("/user")
    public Map<String, Object> user(@AuthenticationPrincipal OAuth2User principal) {
        return principal.getAttributes();
    }
}
```

#### 2. `Implicit Grant` (`Legacy`)

Описание: Упрощенный поток для `SPA` приложений (устарел, используйте `Authorization Code` with `PKCE`).

#### 3. `Resource Owner Password Credentials Grant`

Описание: Прямой обмен учетных данных на токен.Использование:

```java

@Service
public class OAuthService {

    private final RestTemplate restTemplate;

    public OAuthToken getToken(String username, String password) {
        HttpHeaders headers = new HttpHeaders();
        headers.setBasicAuth("client_id", "client_secret");
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("grant_type", "password");
        params.add("username", username);
        params.add("password", password);
        params.add("scope", "read write");

        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(params, headers);

        ResponseEntity<OAuthToken> response = restTemplate.postForEntity(
                "http://auth-server/oauth/token", request, OAuthToken.class);

        return response.getBody();
    }
}
```

#### 4. `Client Credentials Grant`

Описание: Аутентификация между сервисами без участия пользователя.Использование:

```java

@Service
public class ServiceAuthenticationService {

    public String getServiceToken() {
        // Получение токена для service-to-service коммуникации
        HttpHeaders headers = new HttpHeaders();
        headers.setBasicAuth("service-client", "service-secret");
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("grant_type", "client_credentials");
        params.add("scope", "service");

        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(params, headers);

        ResponseEntity<Map> response = restTemplate.postForEntity(
                "http://auth-server/oauth/token", request, Map.class);

        return (String) response.getBody().get("access_token");
    }
}
```

### `Refresh Tokens`

```java

@Service
public class TokenRefreshService {

    private final RestTemplate restTemplate;

    public OAuthToken refreshToken(String refreshToken) {
        HttpHeaders headers = new HttpHeaders();
        headers.setBasicAuth("client_id", "client_secret");
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("grant_type", "refresh_token");
        params.add("refresh_token", refreshToken);

        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(params, headers);

        ResponseEntity<OAuthToken> response = restTemplate.postForEntity(
                "http://auth-server/oauth/token", request, OAuthToken.class);

        return response.getBody();
    }
}
```

## Q3. Как работает `JWT` аутентификация?

`JWT` (JSON Web Token) — это компактный, URL-safe способ представления claims между двумя сторонами. ### Структура `JWT`

`JWT` состоит из трех частей: `Header`, `Payload`, `Signature`. #### 1. `Header` (Заголовок)

```json
{
  "alg": "HS256",
  "typ": "JWT"
}
```

#### 2. `Payload` (Полезная нагрузка)

```json
{
  "sub": "1234567890",
  "name": "John Doe",
  "iat": 1516239022,
  "exp": 1516242622,
  "roles": [
    "USER",
    "ADMIN"
  ]
}
```

#### 3. `Signature` (Подпись)

```text
HMACSHA256(
 base64UrlEncode(header) + "." +
 base64UrlEncode(payload),
 secret
)
```

### Реализация `JWT` в `Java`

```java

@Component
public class JwtUtil {

    private final String SECRET_KEY = "mySecretKey";

    public String generateToken(User user) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("roles", user.getRoles());

        return createToken(claims, user.getUsername());
    }

    private String createToken(Map<String, Object> claims, String subject) {
        return Jwts.builder().setClaims(claims).setSubject(subject).setIssuedAt(new Date(System.currentTimeMillis())).setExpiration(new Date(System.currentTimeMillis() + 1000 * 60 * 60 * 10)) // 10 часов.signWith(SignatureAlgorithm.HS256, SECRET_KEY).compact();
    }

    public Boolean validateToken(String token, UserDetails userDetails) {
        final String username = extractUsername(token);
        return (username.equals(userDetails.getUsername()) && !isTokenExpired(token));
    }

    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    public Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    private Claims extractAllClaims(String token) {
        return Jwts.parser().setSigningKey(SECRET_KEY).parseClaimsJws(token).getBody();
    }

    private Boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }
}
```

### `JWT Filter` для `Spring Security`

```java

@Component
public class JwtRequestFilter extends OncePerRequestFilter {

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private UserDetailsService userDetailsService;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain chain) throws ServletException, IOException {

        final String authorizationHeader = request.getHeader("Authorization");

        String username = null;
        String jwt = null;

        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            jwt = authorizationHeader.substring(7);
            username = jwtUtil.extractUsername(jwt);
        }

        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            UserDetails userDetails = this.userDetailsService.loadUserByUsername(username);

            if (jwtUtil.validateToken(jwt, userDetails)) {
                UsernamePasswordAuthenticationToken authenticationToken =
                        new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(authenticationToken);
            }
        }

        chain.doFilter(request, response);
    }
}
```

### `Refresh Tokens` с `JWT`

```java

@Service
public class TokenService {

    private final JwtUtil jwtUtil;
    private final RefreshTokenRepository refreshTokenRepository;

    public TokenPair generateTokens(User user) {
        String accessToken = jwtUtil.generateToken(user);
        String refreshToken = generateRefreshToken(user);

        // Сохранение refresh token
        RefreshToken refreshTokenEntity = new RefreshToken();
        refreshTokenEntity.setToken(refreshToken);
        refreshTokenEntity.setUsername(user.getUsername());
        refreshTokenEntity.setExpiryDate(calculateExpiryDate());
        refreshTokenRepository.save(refreshTokenEntity);

        return new TokenPair(accessToken, refreshToken);
    }

    public TokenPair refreshTokens(String refreshToken) {
        RefreshToken tokenEntity = refreshTokenRepository.findByToken(refreshToken).orElseThrow(() -> new InvalidTokenException("Invalid refresh token"));

        if (tokenEntity.getExpiryDate().before(new Date())) {
            refreshTokenRepository.delete(tokenEntity);
            throw new TokenExpiredException("Refresh token expired");
        }

        User user = userService.findByUsername(tokenEntity.getUsername());
        refreshTokenRepository.delete(tokenEntity); // One-time use

        return generateTokens(user);
    }

    private String generateRefreshToken(User user) {
        return UUID.randomUUID().toString();
    }

    private Date calculateExpiryDate() {
        return new Date(System.currentTimeMillis() + 30 * 24 * 60 * 60 * 1000L); // 30 дней
    }
}
```

## Q4. Что такое `RBAC` и `ABAC`?

### `Role-Based Access Control` (`RBAC`)

`RBAC` — это модель управления доступом, основанная на ролях пользователей. #### Основные компоненты

1. `Users` (Пользователи)
2. `Roles` (Роли) — наборы разрешений
3. `Permissions` (Разрешения) — действия над ресурсами
4. `Sessions` (Сессии) — активные роли пользователя

#### Реализация `RBAC`

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

@Service
public class RbacService {

    public boolean hasPermission(User user, String permission) {
        return user.getRoles().stream().flatMap(role -> role.getPermissions().stream()).anyMatch(p -> p.getName().equals(permission));
    }

    public boolean hasRole(User user, String roleName) {
        return user.getRoles().stream().anyMatch(role -> role.getName().equals(roleName));
    }
}
```

#### `RBAC` в `Spring Security`

```java

@Configuration
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class RbacConfig {

    @Bean
    public UserDetailsService userDetailsService() {
        return new CustomUserDetailsService();
    }
}

@Service
public class CustomUserDetailsService implements UserDetailsService {

    @Autowired
    private UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByUsername(username).orElseThrow(() -> new UsernameNotFoundException("User not found"));

        return User.builder().username(user.getUsername()).password(user.getPassword()).authorities(user.getRoles().stream().flatMap(role -> role.getPermissions().stream()).map(permission -> new SimpleGrantedAuthority(permission.getName())).collect(Collectors.toList())).build();
    }
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

    @PreAuthorize("hasAuthority('DELETE_POST')")
    @DeleteMapping("/{id}")
    public void deletePost(@PathVariable Long id) {
        postService.delete(id);
    }
}
```

### `Attribute-Based Access Control` (`ABAC`)

`ABAC` — это модель управления доступом, основанная на атрибутах субъекта, объекта, действия и окружения. #### Основные
компоненты

1. `Subject Attributes` — атрибуты пользователя (роль, отдел, уровень доступа)
2. `Object Attributes` — атрибуты ресурса (владелец, чувствительность, категория)
3. `Action Attributes` — атрибуты действия (чтение, запись, удаление)
4. `Environment Attributes` — атрибуты окружения (время, локация, `IP`)

#### Реализация `ABAC`

```java
public class AccessRequest {
    private User subject;
    private Object object;
    private String action;
    private EnvironmentContext environment;
}

public class EnvironmentContext {
    private LocalDateTime timestamp;
    private String ipAddress;
    private String userAgent;
    private String location;
}

@Service
public class AbacService {

    public boolean isAllowed(AccessRequest request) {
        // Проверка атрибутов субъекта
        if (!checkSubjectAttributes(request.getSubject())) {
            return false;
        }

        // Проверка атрибутов объекта
        if (!checkObjectAttributes(request.getObject(), request.getSubject())) {
            return false;
        }

        // Проверка действия
        if (!checkActionAttributes(request.getAction())) {
            return false;
        }

        // Проверка окружения
        if (!checkEnvironmentAttributes(request.getEnvironment())) {
            return false;
        }

        return true;
    }

    private boolean checkSubjectAttributes(User subject) {
        // Проверка статуса аккаунта
        return subject.isActive() && !subject.isLocked();
    }

    private boolean checkObjectAttributes(Object object, User subject) {
        if (object instanceof Document) {
            Document doc = (Document) object;

            // Пользователь может видеть документ если:
            // - он владелец
            // - документ публичный
            // - у него есть роль с доступом к этому типу документов
            return doc.getOwnerId().equals(subject.getId()) ||
                    doc.isPublic() ||
                    hasAccessToDocumentType(subject, doc.getType());
        }

        return true;
    }

    private boolean checkActionAttributes(String action) {
        // Некоторые действия могут быть запрещены в определенное время
        LocalDateTime now = LocalDateTime.now();
        if (action.equals("DELETE") && (now.getHour() < 9 || now.getHour() > 17)) {
            return false; // Удаление разрешено только в рабочее время
        }

        return true;
    }

    private boolean checkEnvironmentAttributes(EnvironmentContext env) {
        // Проверка IP адреса
        if (isBlacklistedIp(env.getIpAddress())) {
            return false;
        }

        // Проверка локации
        if (!isAllowedLocation(env.getLocation())) {
            return false;
        }

        return true;
    }
}
```

### Сравнение `RBAC` и `ABAC`

| Аспект             | `RBAC`             | `ABAC`             |
|--------------------|------------------|------------------|
| Гибкость           | Средняя          | Высокая          |
| Сложность          | Низкая           | Высокая          |
| Масштабируемость   | Хорошая          | Отличная         |
| Производительность | Высокая          | Средняя          |
| Использование      | Простые сценарии | Сложные политики |

## Q5. Как реализовать безопасность в микросервисах?

### 1. `API Gateway Security`

```java

@Configuration
@EnableWebFluxSecurity
public class GatewaySecurityConfig {

    @Bean
    public SecurityWebFilterChain springSecurityFilterChain(ServerHttpSecurity http) {
        return http.authorizeExchange().pathMatchers("/auth/").permitAll().pathMatchers("/api/public/").permitAll().anyExchange().authenticated().and().oauth2ResourceServer().jwt().and().build();
    }
}

@Service
public class GatewayAuthFilter implements GlobalFilter, Ordered {

    private final JwtUtil jwtUtil;

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        String path = exchange.getRequest().getPath().toString();

        // Пропуск публичных эндпоинтов
        if (path.startsWith("/auth/") || path.startsWith("/api/public/")) {
            return chain.filter(exchange);
        }

        // Валидация JWT токена
        String token = extractToken(exchange.getRequest());
        if (token == null || !jwtUtil.validateToken(token)) {
            exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
            return exchange.getResponse().setComplete();
        }

        // Добавление информации о пользователе в заголовки
        String username = jwtUtil.extractUsername(token);
        ServerHttpRequest mutatedRequest = exchange.getRequest().mutate().header("X-User-Id", username).build();

        return chain.filter(exchange.mutate().request(mutatedRequest).build());
    }

    private String extractToken(ServerHttpRequest request) {
        String authHeader = request.getHeaders().getFirst("Authorization");
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            return authHeader.substring(7);
        }
        return null;
    }

    @Override
    public int getOrder() {
        return -100; // Выполняется перед другими фильтрами
    }
}
```

### 2. `Service-to-Service Authentication`

#### `JWT` между сервисами

```java

@Service
public class ServiceTokenProvider {

    private final String serviceName;
    private final String sharedSecret;
    private final JwtUtil jwtUtil;

    public String generateServiceToken() {
        Map<String, Object> claims = new HashMap<>();
        claims.put("service", serviceName);
        claims.put("type", "service");

        return jwtUtil.createToken(claims, serviceName, 5 * 60 * 1000); // 5 минут
    }
}

@Component
public class ServiceAuthInterceptor implements ClientHttpRequestInterceptor {

    @Autowired
    private ServiceTokenProvider tokenProvider;

    @Override
    public ClientHttpResponse intercept(HttpRequest request, byte[] body,
                                        ClientHttpRequestExecution execution) throws IOException {

        String serviceToken = tokenProvider.generateServiceToken();
        request.getHeaders().set("Authorization", "Bearer " + serviceToken);
        request.getHeaders().set("X-Service-Name", "order-service");

        return execution.execute(request, body);
    }
}

@Configuration
public class RestTemplateConfig {

    @Bean
    public RestTemplate restTemplate() {
        RestTemplate restTemplate = new RestTemplate();
        restTemplate.getInterceptors().add(new ServiceAuthInterceptor());
        return restTemplate;
    }
}
```

#### `mTLS` (`Mutual TLS`)

```yaml
# application.yml
server:
  ssl:
  enabled: true
  client-auth: need # Требуется клиентский сертификат
  trust-store: classpath:truststore.jks
  trust-store-password: changeit
  key-store: classpath:keystore.jks
  key-store-password: changeit

# WebClient конфигурация для mTLS
  @Configuration
  public class WebClientConfig {

  @Bean
  public WebClient webClient() {
  SslContext sslContext = SslContextBuilder.forClient().trustManager(InsecureTrustManagerFactory.INSTANCE).keyManager(getKeyManagerFactory()).build();

  HttpClient httpClient = HttpClient.create().secure(sslContextSpec -> sslContextSpec.sslContext(sslContext));

  return WebClient.builder().clientConnector(new ReactorClientHttpConnector(httpClient)).build();
}

  private KeyManagerFactory getKeyManagerFactory() {
  // Загрузка клиентского сертификата
  try {
  KeyStore keyStore = KeyStore.getInstance("PKCS12");
  keyStore.load(new FileInputStream("client.p12"), "password".toCharArray());

  KeyManagerFactory keyManagerFactory = KeyManagerFactory.getInstance("SunX509");
  keyManagerFactory.init(keyStore, "password".toCharArray());

  return keyManagerFactory;
} catch (Exception e) {
  throw new RuntimeException("Failed to load client certificate", e);
}
}
}
```

### 3. `Centralized Authorization`

```java
// Authorization Service
@RestController
@RequestMapping("/authz")
public class AuthorizationController {

    @PostMapping("/check")
    public AuthorizationDecision checkPermission(@RequestBody AuthzRequest request) {
        // Проверка прав в централизованном сервисе
        boolean allowed = authorizationService.hasPermission(
                request.getUserId(),
                request.getResource(),
                request.getAction(),
                request.getContext()
        );

        return new AuthorizationDecision(allowed);
    }
}

public class AuthzRequest {
    private String userId;
    private String resource;
    private String action;
    private Map<String, Object> context;
}

// Client-side проверка
@Service
public class RemoteAuthorizationService {

    private final WebClient webClient;

    public boolean hasPermission(String userId, String resource, String action, Map<String, Object> context) {
        AuthzRequest request = new AuthzRequest(userId, resource, action, context);

        try {
            AuthorizationDecision decision = webClient.post().uri("/authz/check").bodyValue(request).retrieve().bodyToMono(AuthorizationDecision.class).block();

            return decision.isAllowed();
        } catch (Exception e) {
            // Fail-safe: отказ в доступе при ошибке
            return false;
        }
    }
}
```

### 4. `Distributed Session Management`

```java

@Configuration
@EnableRedisHttpSession
public class SessionConfig {

    @Bean
    public LettuceConnectionFactory connectionFactory() {
        return new LettuceConnectionFactory();
    }
}

// Использование Redis для хранения сессий
#application.yml
spring:
session:
store-type:redis
timeout:30m
redis:
host:localhost
port:6379
```

## Q6. Что такое `SAML` и когда его использовать?

`SAML` (`Security Assertion Markup Language`) — это открытый стандарт для обмена аутентификационной и авторизационной
информацией между системами. ### Компоненты `SAML`

#### 1. `Identity Provider` (`IdP`)

Система, которая аутентифицирует пользователей и выдает `SAML` assertions. #### 2. `Service Provider` (`SP`)
Приложение, которое полагается на `IdP` для аутентификации. #### 3. `SAML Assertion`
`XML` документ, содержащий информацию о пользователе и его правах. ### Типы `SAML Assertions`

#### 1. `Authentication Assertion`

Подтверждает аутентификацию пользователя. #### 2. `Attribute Assertion`
Содержит атрибуты пользователя (роли, email, etc.). #### 3. `Authorization Decision Assertion`
Определяет права доступа пользователя. ### `SAML Flow`

```text
1. User -> SP: Запрос доступа к ресурсу
2. SP -> IdP: SAML AuthnRequest
3. IdP -> User: Страница входа
4. User -> IdP: Учетные данные
5. IdP -> SP: SAML Response с assertion
6. SP -> User: Доступ к ресурсу
```

### Реализация `SAML` в `Spring`

```xml
<!-- pom.xml -->
<dependency>
    <groupId>org.springframework.security.extensions</groupId>
    <artifactId>spring-security-saml2-service-provider</artifactId>
</dependency>
```

```java

@Configuration
@EnableWebSecurity
public class SamlSecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.authorizeRequests().anyRequest().authenticated().and().saml2Login().successHandler(samlAuthenticationSuccessHandler());

        return http.build();
    }

    @Bean
    public Saml2AuthenticationSuccessHandler samlAuthenticationSuccessHandler() {
        return new Saml2AuthenticationSuccessHandler();
    }

    @Bean
    public RelyingPartyRegistrationRepository relyingPartyRegistrationRepository() {
        // Конфигурация SAML IdP
        Saml2RelyingPartyRegistration registration =
                Saml2RelyingPartyRegistration.withRegistrationId("okta").entityId("my-app").assertionConsumerServiceUrl("http://localhost:8080/login/saml2/sso/okta").signingX509Credentials(credentials ->
                        credentials.add(getSigningCredential())).singleLogoutServiceUrl("http://localhost:8080/logout/saml2/sso/okta").assertingPartyDetails(party -> party.entityId("okta-entity-id").singleSignOnServiceUrl("https://okta-sso-url").wantAuthnRequestsSigned(true)).build();

        return new InMemoryRelyingPartyRegistrationRepository(registration);
    }
}
```

### Когда использовать `SAML`

#### Преимущества `SAML`:

- `Enterprise Integration`: Отличная поддержка корпоративных систем
- `Federation`: `SSO` между множеством приложений
- `Standards-Based`: Широкая поддержка
- `Secure`: Цифровые подписи и шифрование

#### Недостатки `SAML`:

- `Complexity`: Сложная настройка
- `XML Overhead`: Большие сообщения
- `Browser Dependency`: Требует браузера для `SSO`

#### Когда использовать `SAML`:

- Корпоративные приложения
- `SSO` между множеством систем
- Интеграция с `Active Directory / ADFS`
- Когда нужен высокий уровень безопасности

## Q7. Как реализовать `Multi-Factor Authentication`?

`MFA` добавляет дополнительные уровни проверки подлинности пользователя. ### Типы факторов

#### 1. `Something` you know (Пароль)

#### 2. `Something` you have (Телефон, токен, приложение)

#### 3. `Something` you are (Биометрия)

### Реализация `TOTP` (`Time-based One-Time Password`)

```java

@Service
public class TotpService {

    private final TOTP totp = new TOTP();

    public String generateSecret() {
        // Генерация 32-символьного base32 секрета
        return new Base32().encodeAsString(
                new SecureRandom().generateSeed(20));
    }

    public String generateTotp(String secret) {
        return totp.generateTOTP(secret);
    }

    public boolean verifyTotp(String secret, String code) {
        // Проверка с учетом clock skew (±30 секунд)
        return totp.verifyTOTP(secret, code);
    }

    public String generateQrCodeUrl(String secret, String username, String issuer) {
        return String.format(
                "otpauth://totp/%s:%s?secret=%s&issuer=%s",
                issuer, username, secret, issuer);
    }
}
```

### `MFA Flow`

```java

@RestController
@RequestMapping("/auth")
public class MfaController {

    @Autowired
    private TotpService totpService;
    @Autowired
    private UserService userService;

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest request) {
        User user = userService.authenticate(request.getUsername(), request.getPassword());

        if (user == null) {
            return ResponseEntity.badRequest().body("Invalid credentials");
        }

        if (user.isMfaEnabled()) {
            // Первый фактор пройден, требуется второй фактор
            String sessionId = createMfaSession(user.getId());
            return ResponseEntity.ok(new MfaRequiredResponse(sessionId));
        } else {
            // MFA не требуется, вход успешен
            String token = jwtService.generateToken(user);
            return ResponseEntity.ok(new LoginResponse(token));
        }
    }

    @PostMapping("/mfa/verify")
    public ResponseEntity<?> verifyMfa(@RequestBody MfaVerificationRequest request) {
        MfaSession session = getMfaSession(request.getSessionId());

        User user = userService.findById(session.getUserId());
        boolean validCode = totpService.verifyTotp(user.getMfaSecret(), request.getCode());

        if (validCode) {
            String token = jwtService.generateToken(user);
            deleteMfaSession(request.getSessionId());
            return ResponseEntity.ok(new LoginResponse(token));
        } else {
            return ResponseEntity.badRequest().body("Invalid MFA code");
        }
    }

    @PostMapping("/mfa/setup")
    public ResponseEntity<?> setupMfa(@AuthenticationPrincipal User user) {
        if (user.getMfaSecret() != null) {
            return ResponseEntity.badRequest().body("MFA already enabled");
        }

        String secret = totpService.generateSecret();
        String qrCodeUrl = totpService.generateQrCodeUrl(secret, user.getUsername(), "MyApp");

        // Сохраняем секрет временно (до верификации)
        user.setTempMfaSecret(secret);
        userService.save(user);

        return ResponseEntity.ok(new MfaSetupResponse(qrCodeUrl));
    }

    @PostMapping("/mfa/enable")
    public ResponseEntity<?> enableMfa(@AuthenticationPrincipal User user,
                                       @RequestBody MfaEnableRequest request) {

        boolean validCode = totpService.verifyTotp(user.getTempMfaSecret(), request.getCode());

        if (validCode) {
            user.setMfaSecret(user.getTempMfaSecret());
            user.setMfaEnabled(true);
            user.setTempMfaSecret(null);
            userService.save(user);

            return ResponseEntity.ok("MFA enabled successfully");
        } else {
            return ResponseEntity.badRequest().body("Invalid verification code");
        }
    }
}
```

### `SMS-Based MFA`

```java

@Service
public class SmsMfaService {

    private final TwilioService twilioService;
    private final Cache<String, String> mfaCodes;

    public void sendMfaCode(String phoneNumber) {
        String code = generateCode();
        String message = "Your verification code is: " + code;

        twilioService.sendSms(phoneNumber, message);

        // Сохраняем код в кэше на 5 минут
        mfaCodes.put(phoneNumber, code);
    }

    public boolean verifyMfaCode(String phoneNumber, String code) {
        String storedCode = mfaCodes.getIfPresent(phoneNumber);
        if (storedCode != null && storedCode.equals(code)) {
            mfaCodes.invalidate(phoneNumber);
            return true;
        }
        return false;
    }

    private String generateCode() {
        return String.format("%06d", new SecureRandom().nextInt(999999));
    }
}
```

## Q8. Какие паттерны сессионного управления существуют?

### 1. `Server-Side Sessions`

Описание: Хранение состояния сессии на сервере.Реализация:

```java

@Configuration
public class SessionConfig {

    @Bean
    public HttpSessionIdResolver httpSessionIdResolver() {
        return HeaderHttpSessionIdResolver.xAuthToken();
    }

    @Bean
    public ServletContextInitializer servletContextInitializer() {
        return servletContext -> {
            // Настройка сессий
            servletContext.getSessionCookieConfig().setHttpOnly(true);
            servletContext.getSessionCookieConfig().setSecure(true);
            servletContext.getSessionCookieConfig().setMaxAge(1800); // 30 минут
        };
    }
}

@Controller
@RequestMapping("/session")
public class SessionController {

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest request, HttpSession session) {
        User user = authenticateUser(request.getUsername(), request.getPassword());

        if (user != null) {
            // Сохранение пользователя в сессии
            session.setAttribute("user", user);
            session.setMaxInactiveInterval(30 * 60); // 30 минут

            return ResponseEntity.ok("Login successful");
        }

        return ResponseEntity.badRequest().body("Invalid credentials");
    }

    @GetMapping("/user")
    public ResponseEntity<?> getCurrentUser(HttpSession session) {
        User user = (User) session.getAttribute("user");

        if (user != null) {
            return ResponseEntity.ok(user);
        }

        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout(HttpSession session) {
        session.invalidate();
        return ResponseEntity.ok("Logout successful");
    }
}
```

### 2. `Distributed Sessions`

Описание: Хранение сессий в распределенном хранилище. **Redis-based Sessions:**

```java

@Configuration
@EnableRedisHttpSession(redisNamespace = "myapp:session")
public class RedisSessionConfig {

    @Bean
    public LettuceConnectionFactory connectionFactory() {
        RedisStandaloneConfiguration config = new RedisStandaloneConfiguration();
        config.setHostName("redis-server");
        config.setPort(6379);
        config.setPassword("password");

        return new LettuceConnectionFactory(config);
    }

    @Bean
    public RedisSerializer<Object> springSessionDefaultRedisSerializer() {
        return new GenericJackson2JsonRedisSerializer();
    }
}
```

### 3. `JWT-based Sessions`

Описание: `Stateless` сессии с использованием `JWT`.Реализация:

```java

@Service
public class JwtSessionService {

    private final JwtUtil jwtUtil;
    private final RefreshTokenRepository refreshTokenRepository;

    public SessionTokens createSession(User user) {
        // Создание access token (короткоживущий)
        String accessToken = jwtUtil.generateToken(user);

        // Создание refresh token (долгоживущий)
        String refreshToken = UUID.randomUUID().toString();

        // Сохранение refresh token
        RefreshToken tokenEntity = new RefreshToken();
        tokenEntity.setToken(refreshToken);
        tokenEntity.setUsername(user.getUsername());
        tokenEntity.setExpiryDate(Date.from(Instant.now().plus(30, ChronoUnit.DAYS)));
        refreshTokenRepository.save(tokenEntity);

        return new SessionTokens(accessToken, refreshToken);
    }

    public SessionTokens refreshSession(String refreshToken) {
        RefreshToken tokenEntity = refreshTokenRepository.findByToken(refreshToken).orElseThrow(() -> new InvalidTokenException("Invalid refresh token"));

        if (tokenEntity.getExpiryDate().before(new Date())) {
            refreshTokenRepository.delete(tokenEntity);
            throw new TokenExpiredException("Refresh token expired");
        }

        User user = userService.findByUsername(tokenEntity.getUsername());

        // Удаление использованного refresh token
        refreshTokenRepository.delete(tokenEntity);

        return createSession(user);
    }

    public void invalidateSession(String refreshToken) {
        refreshTokenRepository.findByToken(refreshToken).ifPresent(refreshTokenRepository::delete);
    }
}
```

### 4. Session Security Best practices

```java

@Configuration
public class SessionSecurityConfig {

    @Bean
    public HttpSessionListener sessionListener() {
        return new HttpSessionListener() {

            @Override
            public void sessionCreated(HttpSessionEvent se) {
                HttpSession session = se.getSession();

                // Установка безопасных параметров сессии
                session.setMaxInactiveInterval(30 * 60); // 30 минут
                session.setAttribute("created", System.currentTimeMillis());
                session.setAttribute("ip", getClientIp()); // Отслеживание IP

                logger.info("Session created: {}", session.getId());
            }

            @Override
            public void sessionDestroyed(HttpSessionEvent se) {
                HttpSession session = se.getSession();

                // Логирование завершения сессии
                Long created = (Long) session.getAttribute("created");
                Long duration = System.currentTimeMillis() - created;

                logger.info("Session destroyed: {}, duration: {}ms",
                        session.getId(), duration);
            }
        };
    }

    @Bean
    public Filter sessionSecurityFilter() {
        return new OncePerRequestFilter() {

            @Override
            protected void doFilterInternal(HttpServletRequest request,
                                            HttpServletResponse response,
                                            FilterChain filterChain) throws ServletException, IOException {

                HttpSession session = request.getSession(false);
                if (session != null) {
                    // Проверка IP адреса (защита от session hijacking)
                    String sessionIp = (String) session.getAttribute("ip");
                    String currentIp = getClientIp(request);

                    if (!Objects.equals(sessionIp, currentIp)) {
                        logger.warn("IP address changed for session: {}", session.getId());
                        session.invalidate();
                        response.sendRedirect("/login?error=session_hijacking");
                        return;
                    }

                    // Проверка времени жизни сессии
                    Long created = (Long) session.getAttribute("created");
                    if (created != null) {
                        long age = System.currentTimeMillis() - created;
                        if (age > 8 * 60 * 60 * 1000) { // 8 часов
                            logger.info("Session expired due to age: {}", session.getId());
                            session.invalidate();
                            response.sendRedirect("/login?expired=true");
                            return;
                        }
                    }
                }

                filterChain.doFilter(request, response);
            }
        };
    }
}
```

## Q9. Как защититься от распространенных атак?

### 1. Защита от `Brute Force`

```java

@Service
public class LoginProtectionService {

    private final Cache<String, LoginAttempts> attemptsCache;

    public boolean isAllowedToLogin(String username, String ip) {
        String key = username + ":" + ip;
        LoginAttempts attempts = attemptsCache.get(key, LoginAttempts::new);

        if (attempts.getCount() >= 5) {
            if (attempts.getBlockedUntil().isAfter(Instant.now())) {
                return false; // Блокировка активна
            } else {
                // Сброс счетчика после истечения блокировки
                attempts.reset();
            }
        }

        return true;
    }

    public void recordFailedLogin(String username, String ip) {
        String key = username + ":" + ip;
        LoginAttempts attempts = attemptsCache.get(key, LoginAttempts::new);
        attempts.increment();

        if (attempts.getCount() >= 5) {
            // Блокировка на 15 минут
            attempts.setBlockedUntil(Instant.now().plus(15, ChronoUnit.MINUTES));
        }

        attemptsCache.put(key, attempts);
    }

    public void recordSuccessfulLogin(String username, String ip) {
        String key = username + ":" + ip;
        attemptsCache.invalidate(key);
    }

    public static class LoginAttempts {
        private int count = 0;
        private Instant blockedUntil = Instant.MIN;

        public void increment() {
            count++;
        }

        public void reset() {
            count = 0;
            blockedUntil = Instant.MIN;
        }
        // getters/setters
    }
}
```

### 2. Защита от `CSRF`

```java

@Configuration
@EnableWebSecurity
public class CsrfConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.csrf().csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse()).and().authorizeRequests().anyRequest().authenticated();

        return http.build();
    }
}

// В шаблоне Thymeleaf
<form th:action="@{/transfer}"method="post">
 <input type="hidden"th:name="${_csrf.parameterName}"th:value="${_csrf.token}"/>
 <!--
остальные поля
формы -->
</form>
```

### 3. `Rate Limiting`

```java

@Configuration
public class RateLimitConfig {

    @Bean
    public RateLimiterRegistry rateLimiterRegistry() {
        return RateLimiterRegistry.ofDefaults();
    }
}

@RestController
@RequestMapping("/api")
public class ApiController {

    @Autowired
    private RateLimiterRegistry rateLimiterRegistry;

    @PostMapping("/login")
    @RateLimited
    public ResponseEntity<?> login(@RequestBody LoginRequest request) {
        RateLimiter limiter = rateLimiterRegistry.rateLimiter("login");

        if (!limiter.acquirePermission()) {
            return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS).body("Too many requests. Please try again later.");
        }

        // Логика входа
        return ResponseEntity.ok("Login successful");
    }
}

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface RateLimited {
    String value() default "default";
}

@Aspect
@Component
public class RateLimitAspect {

    @Autowired
    private RateLimiterRegistry rateLimiterRegistry;

    @Around("@annotation(rateLimited)")
    public Object enforceRateLimit(ProceedingJoinPoint joinPoint, RateLimited rateLimited) throws Throwable {
        RateLimiter limiter = rateLimiterRegistry.rateLimiter(rateLimited.value());

        if (!limiter.acquirePermission()) {
            throw new RateLimitExceededException("Rate limit exceeded");
        }

        return joinPoint.proceed();
    }
}
```

### 4. `Input Validation` и `Sanitization`

```java
public class InputValidator {

    private static final Pattern EMAIL_PATTERN =
            Pattern.compile("^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$");

    private static final Pattern SAFE_TEXT_PATTERN =
            Pattern.compile("^[a-zA-Z0-9\\s.,!?-]*$");

    public static ValidationResult validateEmail(String email) {
        if (email == null || email.trim().isEmpty()) {
            return ValidationResult.invalid("Email is required");
        }

        if (email.length() > 254) {
            return ValidationResult.invalid("Email is too long");
        }

        if (!EMAIL_PATTERN.matcher(email).matches()) {
            return ValidationResult.invalid("Invalid email format");
        }

        return ValidationResult.valid();
    }

    public static ValidationResult validateText(String text, int maxLength) {
        if (text == null) {
            return ValidationResult.valid();
        }

        if (text.length() > maxLength) {
            return ValidationResult.invalid("Text is too long");
        }

        // Проверка на потенциально опасные символы
        if (!SAFE_TEXT_PATTERN.matcher(text).matches()) {
            return ValidationResult.invalid("Text contains invalid characters");
        }

        return ValidationResult.valid();
    }

    public static String sanitizeHtml(String html) {
        if (html == null) return null;

        // Удаление script тегов и других опасных элементов
        return html.replaceAll("<script[^>]*>.*?</script>", "").replaceAll("<[^>]+>", "").trim();
    }

    public static class ValidationResult {
        private final boolean valid;
        private final String errorMessage;

        private ValidationResult(boolean valid, String errorMessage) {
            this.valid = valid;
            this.errorMessage = errorMessage;
        }

        public static ValidationResult valid() {
            return new ValidationResult(true, null);
        }

        public static ValidationResult invalid(String message) {
            return new ValidationResult(false, message);
        }

        // getters
    }
}
```

## Q10. Как реализовать `API Gateway Security`?

### 1. `Authentication Gateway`

```java

@Component
public class AuthenticationGatewayFilter implements GlobalFilter, Ordered {

    private final JwtUtil jwtUtil;
    private final RouteValidator routeValidator;

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();

        // Пропуск публичных маршрутов
        if (routeValidator.isPublicRoute(request.getPath().toString())) {
            return chain.filter(exchange);
        }

        // Валидация JWT токена
        String token = extractToken(request);
        if (token == null || !jwtUtil.validateToken(token)) {
            return unauthorizedResponse(exchange);
        }

        // Добавление информации о пользователе в заголовки
        String username = jwtUtil.extractUsername(token);
        ServerHttpRequest mutatedRequest = request.mutate().header("X-User-Id", username).header("X-User-Roles", String.join(",", jwtUtil.extractRoles(token))).build();

        return chain.filter(exchange.mutate().request(mutatedRequest).build());
    }

    private String extractToken(ServerHttpRequest request) {
        String authHeader = request.getHeaders().getFirst("Authorization");
        return authHeader != null && authHeader.startsWith("Bearer ") ? authHeader.substring(7) : null;
    }

    private Mono<Void> unauthorizedResponse(ServerWebExchange exchange) {
        exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
        return exchange.getResponse().setComplete();
    }

    @Override
    public int getOrder() {
        return -100;
    }
}
```

### 2. `Rate Limiting Gateway`

```java

@Configuration
public class RateLimitGatewayConfig {

    @Bean
    public RouteLocator routeLocator(RouteLocatorBuilder builder,
                                     RateLimitGatewayFilter rateLimitFilter) {
        return builder.routes().route("api_route", r -> r.path("/api/").filters(f -> f.filter(rateLimitFilter)).uri("lb://api-service")).build();
    }
}

@Component
public class RateLimitGatewayFilter implements GatewayFilter {

    private final RedisRateLimiter rateLimiter;

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        String clientId = getClientId(exchange.getRequest());

        return rateLimiter.isAllowed(clientId).flatMap(allowed -> {
            if (allowed) {
                return chain.filter(exchange);
            } else {
                exchange.getResponse().setStatusCode(HttpStatus.TOO_MANY_REQUESTS);
                return exchange.getResponse().setComplete();
            }
        });
    }

    private String getClientId(ServerHttpRequest request) {
        // Получение client ID из заголовка или IP
        String clientId = request.getHeaders().getFirst("X-Client-Id");
        if (clientId == null) {
            clientId = request.getRemoteAddress().getAddress().getHostAddress();
        }
        return clientId;
    }
}
```

### 3. `Authorization Gateway`

```java

@Component
public class AuthorizationGatewayFilter implements GlobalFilter, Ordered {

    private final AuthorizationService authorizationService;

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();
        String path = request.getPath().toString();
        String method = request.getMethod().toString();

        // Получение информации о пользователе из заголовков
        String userId = request.getHeaders().getFirst("X-User-Id");
        String roles = request.getHeaders().getFirst("X-User-Roles");

        if (userId == null) {
            return chain.filter(exchange); // Публичный маршрут
        }

        // Проверка авторизации
        return authorizationService.isAuthorized(userId, path, method, Arrays.asList(roles.split(","))).flatMap(authorized -> {
            if (authorized) {
                return chain.filter(exchange);
            } else {
                exchange.getResponse().setStatusCode(HttpStatus.FORBIDDEN);
                return exchange.getResponse().setComplete();
            }
        });
    }

    @Override
    public int getOrder() {
        return -90; // После AuthenticationFilter
    }
}

@Service
public class AuthorizationService {

    private final RoutePermissionRepository permissionRepository;

    public Mono<Boolean> isAuthorized(String userId, String path, String method, List<String> roles) {
        return permissionRepository.findByPathAndMethod(path, method).map(permission -> hasRequiredRole(roles, permission.getRequiredRoles())).defaultIfEmpty(true); // Если нет специальных требований, разрешить
    }

    private boolean hasRequiredRole(List<String> userRoles, List<String> requiredRoles) {
        return requiredRoles.stream().anyMatch(userRoles::contains);
    }
}
```

### 4. `Logging` и `Monitoring Gateway`

```java

@Component
public class LoggingGatewayFilter implements GlobalFilter, Ordered {

    private final Logger logger = LoggerFactory.getLogger("GATEWAY");

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();
        long startTime = System.currentTimeMillis();

        return chain.filter(exchange).doOnSuccess(aVoid -> {
            long duration = System.currentTimeMillis() - startTime;
            ServerHttpResponse response = exchange.getResponse();

            logger.info("Request: {} {} -> {} ({}ms)",
                    request.getMethod(),
                    request.getPath(),
                    response.getStatusCode(),
                    duration);
        }).doOnError(throwable -> {
            long duration = System.currentTimeMillis() - startTime;

            logger.error("Request failed: {} {} -> {} ({}ms)",
                    request.getMethod(),
                    request.getPath(),
                    throwable.getMessage(),
                    duration,
                    throwable);
        });
    }

    @Override
    public int getOrder() {
        return Ordered.LOWEST_PRECEDENCE; // Выполняется последним
    }
}
```

## Q11. Что такое `OpenID Connect` и как он расширяет `OAuth 2.0`?

`OpenID Connect` (`OIDC`) — протокол аутентификации поверх `OAuth 2.0`; добавляет `ID Token` (`JWT` с информацией о пользователе: sub, name, email). `OAuth 2.0` — авторизация (доступ к ресурсам); `OIDC / SSO`; провайдеры: `Google`, `Azure AD`, `Keycloak`.

Для production-систем дополнительно стоит описать модель угроз, контроль доступа по принципу least privilege и процесс реагирования на инциденты. На собеседовании обычно ожидают, что вы свяжете техническую меру с риском для бизнеса и с проверяемыми контрольными точками в CI/CD.

## Q12. Как реализовать `Single Sign-On` (`SSO`)?

`SSO` — один вход для нескольких приложений. Подходы: (1) `SAML` (`XML`, enterprise); (2) `OAuth 2.0` + `OIDC` (`JWT`, современные `API`); (3) `CAS` (`Central Authentication Service`). Архитектура: `Identity Provider` (`IdP`) хранит сессию; приложения перенаправляют на `IdP` для аутентификации; после успеха `IdP` выдаёт токен или assertion. В `Spring` — `spring-security-saml2` или `spring-security-oauth2-client`.

Практическая ценность ответа обычно повышается, если дополнить определение операционным контекстом: как решение ведёт себя под нагрузкой, при сбоях и в процессе сопровождения. На интервью ожидают, что вы назовёте критерии выбора и способ валидации решения через метрики и проверяемый сценарий.

## Q13. Что такое `Claims-based` аутентификация?

`Claims` — утверждения о пользователе (роль, email, группа); передаются в токене (`JWT`, `SAML` assertion). Приложение принимает решения по авторизации на основе claims без обращения к БД. Провайдер (`IdP`) включает claims в токен при аутентификации. Приложение доверяет подписи токена и использует claims для `RBAC / ABAC`.

Практическая ценность ответа обычно повышается, если дополнить определение операционным контекстом: как решение ведёт себя под нагрузкой, при сбоях и в процессе сопровождения. На интервью ожидают, что вы назовёте критерии выбора и способ валидации решения через метрики и проверяемый сценарий.

## Q14. Как обеспечить безопасность токенов (`JWT` refresh, rotation)?

`Access / Rotation`: при обмене refresh выдаётся новый refresh (старый инвалидируется). Защита: refresh token — `httpOnly` cookie; хранить хэш refresh в БД; детектировать повторное использование (атака).

Для production-систем дополнительно стоит описать модель угроз, контроль доступа по принципу least privilege и процесс реагирования на инциденты. На собеседовании обычно ожидают, что вы свяжете техническую меру с риском для бизнеса и с проверяемыми контрольными точками в CI/CD.

## Q15. Что такое `Zero Trust Security Model`?

`Zero Trust` — «не доверять никому по умолчанию»; проверять каждый запрос (даже внутри сети). Микросервисы: `mTLS` между сервисами; токены с коротким сроком; проверка авторизации на каждом сервисе. Нет «доверенной зоны»; сеть считается враждебной. Реализация: `Service Mesh` (`Istio`), `API Gateway` с проверкой токенов, мониторинг аномалий.

Для production-систем дополнительно стоит описать модель угроз, контроль доступа по принципу least privilege и процесс реагирования на инциденты. На собеседовании обычно ожидают, что вы свяжете техническую меру с риском для бизнеса и с проверяемыми контрольными точками в CI/CD.

## Q16. Как реализовать `Rate Limiting` для защиты `API`?

`Rate` limiting — ограничение числа запросов по ключу (`IP`, `userId`, `API` key) за период. Подходы: (1) `Token Bucket` (`Bucket4j`, `Redis`); (2) `Sliding Window` (`Redis ZADD` по времени); (3) `Fixed Window` (счётчик с `TTL`). В `Spring`: фильтр или `HandlerInterceptor`; проверка лимита до обработки запроса; возврат `429` Too `Many Requests` при превышении. Защита от DDoS и злоупотреблений.

Практическая ценность ответа обычно повышается, если дополнить определение операционным контекстом: как решение ведёт себя под нагрузкой, при сбоях и в процессе сопровождения. На интервью ожидают, что вы назовёте критерии выбора и способ валидации решения через метрики и проверяемый сценарий.

## Q17. Что такое `mTLS` и когда его использовать?

`mTLS` (mutual `TLS`) — двусторонняя аутентификация по сертификатам: клиент проверяет сертификат сервера и сервер проверяет сертификат клиента. Используется между микросервисами (`Service Mesh`), для `B2B API`, `IoT`. Обеспечивает сильную аутентификацию без паролей. Управление сертификатами (выпуск, ротация, отзыв) — сложность; автоматизация через `cert-manager` (`Kubernetes`).

Практическая ценность ответа обычно повышается, если дополнить определение операционным контекстом: как решение ведёт себя под нагрузкой, при сбоях и в процессе сопровождения. На интервью ожидают, что вы назовёте критерии выбора и способ валидации решения через метрики и проверяемый сценарий.

## Q18. Как реализовать аудит и логирование событий безопасности?

Логировать: успешные и неудачные аутентификации, изменения прав, доступ к чувствительным ресурсам. Поля: `userId`, `IP`, timestamp, действие, результат. Централизованное хранение (`Elasticsearch`, `SIEM`). Алерты по аномалиям (множественные неудачи, доступ из необычной локации). В `Spring`: `ApplicationListener` на `AuthenticationSuccessEvent / AuthenticationFailureEvent`; кастомный фильтр для аудита запросов.

Для production-систем дополнительно стоит описать модель угроз, контроль доступа по принципу least privilege и процесс реагирования на инциденты. На собеседовании обычно ожидают, что вы свяжете техническую меру с риском для бизнеса и с проверяемыми контрольными точками в CI/CD.

## Q19. Что такое `Context-based Access Control`?

Авторизация на основе контекста запроса: время, локация, устройство, уровень риска. Пример: доступ к админке только из офисной сети; повышенная аутентификация при доступе из нового устройства. Реализация: политики в авторизационном сервисе (`OPA`, `AWS IAM`); проверка контекста при каждом запросе. Динамическая авторизация вместо статических ролей.

Практическая ценность ответа обычно повышается, если дополнить определение операционным контекстом: как решение ведёт себя под нагрузкой, при сбоях и в процессе сопровождения. На интервью ожидают, что вы назовёте критерии выбора и способ валидации решения через метрики и проверяемый сценарий.

## Q20. Как обеспечить безопасность в `Service Mesh`?

`Service Mesh` (`Istio`, `Linkerd`) обеспечивает: `mTLS` между sidecar автоматически; политики авторизации (какой сервис может вызывать какой); rate limiting; трейсинг и аудит. Конфигурация через манифесты (`AuthorizationPolicy` в `Istio`). Централизованное управление безопасностью без изменения кода приложений. Сертификаты ротируются автоматически.

Для production-систем дополнительно стоит описать модель угроз, контроль доступа по принципу least privilege и процесс реагирования на инциденты. На собеседовании обычно ожидают, что вы свяжете техническую меру с риском для бизнеса и с проверяемыми контрольными точками в CI/CD.

## Q21. Что такое `Identity Federation`?

`Identity Federation` — доверие между несколькими `IdP`; пользователь аутентифицируется в одном `IdP`, получает доступ к ресурсам других. Протоколы: `SAML`, `WS-Federation`, `OIDC`. Используется в корпоративных сценариях (доступ партнёров) и в облаках (федерация с `Azure AD`, `Google Workspace`). Маппинг атрибутов и claims между `IdP`.

Практическая ценность ответа обычно повышается, если дополнить определение операционным контекстом: как решение ведёт себя под нагрузкой, при сбоях и в процессе сопровождения. На интервью ожидают, что вы назовёте критерии выбора и способ валидации решения через метрики и проверяемый сценарий.

## Q22. Как реализовать `Step-Up Authentication`?

`Step-Up` — повышение уровня аутентификации для чувствительных операций (например, перевод денег требует повторного ввода пароля или 2FA). Проверка: при запросе к защищённому ресурсу проверить уровень аутентификации в токене; при недостаточном — запросить дополнительную аутентификацию. `Claim` в токене (например, acr — `Authentication Context Class Reference`) указывает уровень.

Для production-систем дополнительно стоит описать модель угроз, контроль доступа по принципу least privilege и процесс реагирования на инциденты. На собеседовании обычно ожидают, что вы свяжете техническую меру с риском для бизнеса и с проверяемыми контрольными точками в CI/CD.

## Q23. Что такое `Passwordless Authentication`?

Аутентификация без пароля: `WebAuthn` (биометрия, аппаратные ключи), `Magic Link` (ссылка на email), `OTP` (одноразовый код). Преимущества: нет фишинга паролей, удобство. Реализация: библиотеки `WebAuthn` (`Yubico`, Duo); при логине генерируется challenge, устройство подписывает, сервер проверяет подпись. `Fallback` на пароль при необходимости.

Для production-систем дополнительно стоит описать модель угроз, контроль доступа по принципу least privilege и процесс реагирования на инциденты. На собеседовании обычно ожидают, что вы свяжете техническую меру с риском для бизнеса и с проверяемыми контрольными точками в CI/CD.

## Q24. Как защитить `GraphQL API`?

Авторизация на уровне полей (`field-level`): проверка прав при резолве каждого поля. Ограничение глубины и сложности запросов (защита от `DoS`). Аутентификация: `JWT` в заголовке; контекст запроса с `userId`. `DataLoader` для батчинга запросов к БД; избегать N+1. Валидация входных данных; rate limiting по пользователю.

Практическая ценность ответа обычно повышается, если дополнить определение операционным контекстом: как решение ведёт себя под нагрузкой, при сбоях и в процессе сопровождения. На интервью ожидают, что вы назовёте критерии выбора и способ валидации решения через метрики и проверяемый сценарий.

## Q25. Что такое `Delegated Authorization`?

Пользователь делегирует права приложению (например, «приложение X может читать мои фото»). `OAuth 2.0` — стандартный протокол: пользователь авторизует приложение через consent screen; приложение получает access token с ограниченными правами (scope). Отзыв токена — в любой момент. Используется для интеграций (`Google API`, `GitHub API`).

Для production-систем дополнительно стоит описать модель угроз, контроль доступа по принципу least privilege и процесс реагирования на инциденты. На собеседовании обычно ожидают, что вы свяжете техническую меру с риском для бизнеса и с проверяемыми контрольными точками в CI/CD.

## Q26. Как реализовать `Fine-Grained Authorization`?

Авторизация на уровне объектов или атрибутов: «пользователь может редактировать только свои заказы». Подходы: (1) проверка в коде (if `userId` == order.`userId`); (2) `ABAC` (`Attribute-Based Access Control`) с политиками; (3) внешний авторизационный сервис (`OPA`, `AWS Verified Permissions`). Для сложных правил — вынести в политики; для простых — в код с аннотациями (`@PreAuthorize`).

Для production-систем дополнительно стоит описать модель угроз, контроль доступа по принципу least privilege и процесс реагирования на инциденты. На собеседовании обычно ожидают, что вы свяжете техническую меру с риском для бизнеса и с проверяемыми контрольными точками в CI/CD.

## Q27. Что такое `Token Binding` и зачем он нужен?

`Token Binding` — привязка токена к `TLS`-соединению через криптографический proof; предотвращает кражу и повторное использование токена. Клиент и сервер обмениваются proof при установке `TLS`; токен включает `binding ID`. При повторном использовании токена с другого соединения — отказ. Стандарт `RFC 8471`; поддержка ограничена; альтернатива — короткий срок токена и refresh.

Практическая ценность ответа обычно повышается, если дополнить определение операционным контекстом: как решение ведёт себя под нагрузкой, при сбоях и в процессе сопровождения. На интервью ожидают, что вы назовёте критерии выбора и способ валидации решения через метрики и проверяемый сценарий.

## Q28. Как обеспечить безопасность `WebSocket` соединений?

Аутентификация при handshake: токен в `query`-параметре или заголовке (при upgrade). После установки соединения проверять авторизацию для каждого сообщения (`userId` из контекста). `TLS / Spring WebSocket`: `HandshakeInterceptor` для токена; `ChannelInterceptor` для проверки сообщений.

Для production-систем дополнительно стоит описать модель угроз, контроль доступа по принципу least privilege и процесс реагирования на инциденты. На собеседовании обычно ожидают, что вы свяжете техническую меру с риском для бизнеса и с проверяемыми контрольными точками в CI/CD.

## Q29. Что такое `Proof Key for Code Exchange` (`PKCE`)?

`PKCE` — расширение `OAuth 2.0` для защиты authorization code flow в публичных клиентах (`SPA`, мобильные). Клиент генерирует `code_verifier` (случайная строка) и `code_challenge` (хэш); передаёт challenge при запросе code; при обмене code на token передаёт verifier. Сервер проверяет соответствие. Защита от перехвата authorization code. Обязателен для `SPA` и мобильных (`RFC 7636`).

Практическая ценность ответа обычно повышается, если дополнить определение операционным контекстом: как решение ведёт себя под нагрузкой, при сбоях и в процессе сопровождения. На интервью ожидают, что вы назовёте критерии выбора и способ валидации решения через метрики и проверяемый сценарий.

## Q30. Как реализовать `Dynamic Authorization`?

Авторизация на основе runtime-данных (состояние объекта, время, внешние факторы). Политики хранятся в БД или в авторизационном сервисе (`OPA`, `Casbin`); при запросе вычисляется решение. Пример: «менеджер может одобрить заказ до $1000; выше — требуется директор». Реализация: вызов авторизационного сервиса с контекстом (`userId`, action, resource, attributes); кэширование решений при необходимости.

### 5. `Circuit Breaker Gateway`

```java

@Configuration
public class CircuitBreakerGatewayConfig {

    @Bean
    public RouteLocator routeLocator(RouteLocatorBuilder builder) {
        return builder.routes().route("api_route", r -> r.path("/api/").filters(f -> f.circuitBreaker(c -> c.setName("apiCircuitBreaker").setFallbackUri("forward:/fallback/api")).retry(retry -> retry.setRetries(3).setStatuses(HttpStatus.INTERNAL_SERVER_ERROR))).uri("lb://api-service")).build();
    }
}

@RestController
public class FallbackController {

    @GetMapping("/fallback/api")
    public ResponseEntity<?> apiFallback() {
        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body("Service is temporarily unavailable. Please try again later.");
    }
}
```