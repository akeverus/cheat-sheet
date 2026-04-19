---
title: "Micronaut: Security - Authentication и Authorization"
description: "Полное руководство по безопасности в Micronaut: JWT, OAuth2, authentication, authorization и security best practices"
tags:
  - micronaut
  - security
  - jwt
  - oauth2
  - authentication
  - authorization
  - java
  - kotlin
difficulty: "intermediate"
prerequisites: ["micronaut/micronaut-basics.md", "micronaut/micronaut-http.md"]
next: ["micronaut-reactive.md", "micronaut-testing.md"]
updated: "2026-02-11"
related: ["micronaut-core.md", "micronaut-http.md"]
---

# Micronaut: Security - Authentication и Authorization



## Полезные ссылки

[Официальная документация Micronaut](https://docs.micronaut.io/)
[Micronaut GitHub](https://github.com/micronaut-projects/micronaut-core)

## Содержание

- [Micronaut: Security - Authentication и Authorization](#micronaut-security-authentication-и-authorization)
- [Введение](#введение)
  - [Основные возможности](#основные-возможности)
- [Настройка Security](#настройка-security)
  - [Зависимости](#зависимости)
  - [Конфигурация](#конфигурация)
- [JWT Authentication](#jwt-authentication)
  - [Генерация JWT Tokens](#генерация-jwt-tokens)
  - [JWT Login Endpoint](#jwt-login-endpoint)
  - [Custom Authentication Provider](#custom-authentication-provider)
  - [Защита Endpoints](#защита-endpoints)
- [OAuth2](#oauth2)
  - [Настройка OAuth2](#настройка-oauth2)
  - [OAuth2 Login Controller](#oauth2-login-controller)
- [Method Security](#method-security)
  - [Защита методов](#защита-методов)
  - [Custom Security Rules](#custom-security-rules)
- [Password Encoding](#password-encoding)
- [Лучшие практики](#лучшие-практики)
  - [1. Используйте JWT для Stateless Applications](#1-используйте-jwt-для-stateless-applications)
  - [2. Валидируйте входные данные](#2-валидируйте-входные-данные)
  - [3. Используйте HTTPS в Production](#3-используйте-https-в-production)
- [✅ Хорошо](#хорошо)
  - [4. Ограничивайте время жизни токенов](#4-ограничивайте-время-жизни-токенов)
  - [5. Используйте Role-based Access Control](#5-используйте-role-based-access-control)
- [LDAP Authentication](#ldap-authentication)
  - [Настройка LDAP](#настройка-ldap)
  - [LDAP Authentication Provider](#ldap-authentication-provider)
- [Session-based Authentication](#session-based-authentication)
  - [Настройка Session](#настройка-session)
  - [Session Controller](#session-controller)
- [Token Refresh](#token-refresh)
  - [Refresh Token Endpoint](#refresh-token-endpoint)
- [Rate Limiting](#rate-limiting)
  - [Настройка Rate Limiting](#настройка-rate-limiting)
  - [Custom Rate Limiter](#custom-rate-limiter)
- [CORS Configuration](#cors-configuration)
  - [Настройка CORS](#настройка-cors)
- [Security Headers](#security-headers)
  - [Настройка Security Headers](#настройка-security-headers)
- [Token Validation](#token-validation)
  - [JWT Token Validation](#jwt-token-validation)
- [Security Events](#security-events)
  - [Security Event Listeners](#security-event-listeners)
- [Password Policies](#password-policies)
  - [Password Validation](#password-validation)
  - [Security Headers Configuration](#security-headers-configuration)
- [Role-based Access Control](#role-based-access-control)
  - [RBAC Implementation](#rbac-implementation)
- [Заключение](#заключение)
- [Дополнительные ресурсы](#дополнительные-ресурсы)

## Введение

**Micronaut Security** предоставляет комплексное решение для аутентификации и авторизации в приложениях. Поддерживаются различные механизмы: **JWT**, **OAuth2**, **Basic Auth**, **Session-based authentication** и другие.

### Основные возможности

- **JWT Authentication**: **Stateless authentication** с **JSON Web Tokens**
- **OAuth2**: Поддержка **OAuth2** и **OpenID Connect**
- **Session-based**: Традиционная **session-based authentication**
- **LDAP**: Интеграция с **LDAP**
- **Custom Authentication**: Создание собственных механизмов аутентификации
- **Role-based Access Control**: управление доступом на основе ролей
- **Method Security**: Защита на уровне методов

## Настройка Security

### Зависимости

**build.gradle:**

```gradle
dependencies {
    implementation("io.micronaut.security:micronaut-security")
    implementation("io.micronaut.security:micronaut-security-jwt")
    implementation("io.micronaut.security:micronaut-security-oauth2")
}
```

### Конфигурация

**application.yml:**

```yaml
micronaut:
  security:
    enabled: true
    authentication: bearer
    token:
      jwt:
        enabled: true
        generator:
          access-token:
            expiration: 3600
    endpoints:
      login:
        enabled: true
        path: /login
      logout:
        enabled: true
        path: /logout
    intercept-url-map:
      - pattern: /api/public/
        http-method: GET
        access:
          - isAnonymous()
      - pattern: /api/admin/
        access:
          - ROLE_ADMIN
      - pattern: /api/user/
        access:
          - ROLE_USER
          - ROLE_ADMIN
```

## JWT Authentication

### Генерация **JWT Tokens**

```java
// Логин и генерация JWT (UsernamePasswordCredentials → BearerAccessRefreshToken)
import io.micronaut.security.authentication.UsernamePasswordCredentials;
import io.micronaut.security.token.jwt.generator.JwtTokenGenerator;
import io.micronaut.security.token.jwt.render.BearerAccessRefreshToken;
import jakarta.inject.Singleton;

@Singleton
public class AuthenticationService {
    private final JwtTokenGenerator tokenGenerator;
    private final UserRepository userRepository;
    
    public AuthenticationService(
            JwtTokenGenerator tokenGenerator,
            UserRepository userRepository) {
        this.tokenGenerator = tokenGenerator;
        this.userRepository = userRepository;
    }
    
    public Optional<BearerAccessRefreshToken> authenticate(
            String username, String password) {
        return userRepository.findByUsername(username)
            .filter(user -> passwordEncoder.matches(password, user.getPassword()))
            .map(user -> {
                Map<String, Object> claims = new HashMap<>();
                claims.put("sub", user.getUsername());
                claims.put("roles", user.getRoles());
                
                String accessToken = tokenGenerator.generateToken(claims)
                    .orElseThrow(() -> new RuntimeException("Token generation failed"));
                
                return new BearerAccessRefreshToken(
                    accessToken,
                    accessToken, // refresh token (same for simplicity)
                    "Bearer",
                    3600L
                );
            });
    }
}
```

### **JWT Login Endpoint**

```java
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Post;
import io.micronaut.http.annotation.Body;
import io.micronaut.security.authentication.UsernamePasswordCredentials;
import io.micronaut.security.token.jwt.render.BearerAccessRefreshToken;

@Controller("/login")
public class LoginController {
    private final AuthenticationService authenticationService;
    
    public LoginController(AuthenticationService authenticationService) {
        this.authenticationService = authenticationService;
    }
    
    @Post
    public HttpResponse<BearerAccessRefreshToken> login(
            @Body UsernamePasswordCredentials credentials) {
        return authenticationService.authenticate(
            credentials.getUsername(),
            credentials.getPassword()
        )
        .map(HttpResponse::ok)
        .orElse(HttpResponse.unauthorized());
    }
}
```

### **Custom Authentication Provider**

```java
// Провайдер аутентификации (проверка учётных данных)
import io.micronaut.security.authentication.AuthenticationProvider;
import io.micronaut.security.authentication.AuthenticationRequest;
import io.micronaut.security.authentication.AuthenticationResponse;
import jakarta.inject.Singleton;
import org.reactivestreams.Publisher;
import reactor.core.publisher.Flux;
import reactor.core.publisher.FluxSink;

@Singleton
public class CustomAuthenticationProvider 
        implements AuthenticationProvider {
    
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    
    public CustomAuthenticationProvider(
            UserRepository userRepository,
            PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }
    
    @Override
    public Publisher<AuthenticationResponse> authenticate(
            HttpRequest<?> request,
            AuthenticationRequest<?, ?> authRequest) {
        
        return Flux.create(emitter -> {
            String username = authRequest.getIdentity().toString();
            String password = authRequest.getSecret().toString();
            
            userRepository.findByUsername(username)
                .ifPresentOrElse(
                    user -> {
                        if (passwordEncoder.matches(password, user.getPassword())) {
                            List<String> roles = user.getRoles();
                            emitter.next(AuthenticationResponse.success(
                                username,
                                roles
                            ));
                        } else {
                            emitter.error(AuthenticationResponse.exception());
                        }
                    },
                    () -> emitter.error(AuthenticationResponse.exception())
                );
            
            emitter.complete();
        }, FluxSink.OverflowStrategy.ERROR);
    }
}
```

### Защита **Endpoints**

```java
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Get;
import io.micronaut.security.annotation.Secured;
import io.micronaut.security.rules.SecurityRule;

@Controller("/api/users")
public class UserController {
    
    @Get("/public")
    @Secured(SecurityRule.IS_ANONYMOUS)
    public String publicEndpoint() {
        return "This is public";
    }
    
    @Get("/authenticated")
    @Secured(SecurityRule.IS_AUTHENTICATED)
    public String authenticatedEndpoint() {
        return "This requires authentication";
    }
    
    @Get("/admin")
    @Secured("ROLE_ADMIN")
    public String adminEndpoint() {
        return "This requires ADMIN role";
    }
    
    @Get("/user")
    @Secured({"ROLE_USER", "ROLE_ADMIN"})
    public String userEndpoint() {
        return "This requires USER or ADMIN role";
    }
}
```

## OAuth2

### Настройка **OAuth2**

**application.yml:**

```yaml
micronaut:
  security:
    oauth2:
      clients:
        google:
          client-id: ${GOOGLE_CLIENT_ID}
          client-secret: ${GOOGLE_CLIENT_SECRET}
          openid:
            issuer: https://accounts.google.com
        github:
          client-id: ${GITHUB_CLIENT_ID}
          client-secret: ${GITHUB_CLIENT_SECRET}
          authorization:
            url: https://github.com/login/oauth/authorize
          token:
            url: https://github.com/login/oauth/access_token
```

### **OAuth2 Login Controller**

```java
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Get;
import io.micronaut.security.annotation.Secured;
import io.micronaut.security.oauth2.endpoint.authorization.state.State;
import io.micronaut.security.oauth2.endpoint.token.response.OauthAuthenticationMapper;
import io.micronaut.security.oauth2.endpoint.token.response.OauthUserDetailsMapper;

@Controller("/oauth")
public class OAuthController {
    
    @Get("/login/google")
    @Secured(SecurityRule.IS_ANONYMOUS)
    public HttpResponse<?> loginGoogle() {
        return HttpResponse.seeOther(
            URI.create("/oauth/callback/google")
        );
    }
    
    @Get("/callback/google")
    @Secured(SecurityRule.IS_ANONYMOUS)
    public HttpResponse<?> callbackGoogle(
            @QueryValue String code,
            State state) {
        // Обработка OAuth callback
        return HttpResponse.ok();
    }
}
```

## Method Security

### Защита методов

```java
import io.micronaut.security.annotation.Secured;
import jakarta.inject.Singleton;

@Singleton
public class UserService {
    
    @Secured("ROLE_ADMIN")
    public User createUser(User user) {
        return userRepository.save(user);
    }
    
    @Secured({"ROLE_USER", "ROLE_ADMIN"})
    public Optional<User> getUser(Long id) {
        return userRepository.findById(id);
    }
    
    @Secured(SecurityRule.IS_AUTHENTICATED)
    public User updateUser(Long id, User user) {
        return userRepository.update(id, user);
    }
    
    @Secured("ROLE_ADMIN")
    public void deleteUser(Long id) {
        userRepository.deleteById(id);
    }
}
```

### **Custom Security Rules**

```java
import io.micronaut.security.rules.SecurityRule;
import io.micronaut.security.rules.SecuredAnnotationRule;
import jakarta.inject.Singleton;

@Singleton
public class CustomSecurityRule implements SecurityRule {
    
    @Override
    public SecurityRuleResult check(HttpRequest<?> request, 
                                   RouteMatch<?> routeMatch, 
                                   Map<String, Object> claims) {
        // Custom security logic
        if (request.getPath().startsWith("/api/internal")) {
            List<String> roles = (List<String>) claims.get("roles");
            if (roles != null && roles.contains("ROLE_INTERNAL")) {
                return SecurityRuleResult.ALLOWED;
            }
            return SecurityRuleResult.REJECTED;
        }
        return SecurityRuleResult.UNKNOWN;
    }
}
```

## Password Encoding

```java
import io.micronaut.security.authentication.providers.PasswordEncoder;
import jakarta.inject.Singleton;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@Singleton
public class BCryptPasswordEncoder implements PasswordEncoder {
    private final org.springframework.security.crypto.password.PasswordEncoder encoder;
    
    public BCryptPasswordEncoder() {
        this.encoder = new BCryptPasswordEncoder();
    }
    
    @Override
    public String encode(String rawPassword) {
        return encoder.encode(rawPassword);
    }
    
    @Override
    public boolean matches(String rawPassword, String encodedPassword) {
        return encoder.matches(rawPassword, encodedPassword);
    }
}
```

## Лучшие практики

### 1. Используйте **JWT** для **Stateless Applications**

```java
// ✅ Хорошо - JWT для микросервисов
micronaut:
  security:
    authentication: bearer
    token:
      jwt:
        enabled: true
```

### 2. Валидируйте входные данные

```java
// ✅ Хорошо
@Post("/login")
public HttpResponse<?> login(@Valid @Body LoginRequest request) {
    // ...
}
```

### 3. Используйте **HTTPS** в **Production**

```yaml
# ✅ Хорошо
micronaut:
  server:
    ssl:
      enabled: true
      port: 8443
```

### 4. Ограничивайте время жизни токенов

```yaml
# ✅ Хорошо
micronaut:
  security:
    token:
      jwt:
        generator:
          access-token:
            expiration: 3600  # 1 hour
```

### 5. Используйте **Role-based Access Control**

```java
// ✅ Хорошо
@Secured("ROLE_ADMIN")
public void deleteUser(Long id) {
    // ...
}
```

## LDAP Authentication

### Настройка **LDAP**

**application.yml:**

```yaml
micronaut:
  security:
    ldap:
      default:
        enabled: true
        server: ldap://localhost:389
        search:
          base: dc=example,dc=com
          filter: (uid={0})
```

### **LDAP Authentication Provider**

```java
import io.micronaut.security.authentication.providers.LdapAuthenticationProvider;
import jakarta.inject.Singleton;

@Singleton
public class CustomLdapAuthenticationProvider 
        extends LdapAuthenticationProvider {
    
    public CustomLdapAuthenticationProvider(
            LdapContextFactory ldapContextFactory) {
        super(ldapContextFactory);
    }
    
    @Override
    protected List<String> getRoles(String username) {
        // Получение ролей из LDAP
        return ldapService.getUserRoles(username);
    }
}
```

## Session-based Authentication

### Настройка **Session**

**application.yml:**

```yaml
micronaut:
  security:
    authentication: session
    session:
      enabled: true
      cookie:
        enabled: true
        cookie-max-age: PT1H
        cookie-http-only: true
        cookie-secure: true
```

### **Session Controller**

```java
@Controller("/session")
public class SessionController {
    
    @Post("/login")
    @Secured(SecurityRule.IS_ANONYMOUS)
    public HttpResponse<?> login(
            @Body UsernamePasswordCredentials credentials,
            HttpSession session) {
        return authenticationService.authenticate(
            credentials.getUsername(),
            credentials.getPassword()
        )
        .map(user -> {
            session.put("user", user);
            return HttpResponse.ok();
        })
        .orElse(HttpResponse.unauthorized());
    }
    
    @Post("/logout")
    @Secured(SecurityRule.IS_AUTHENTICATED)
    public HttpResponse<?> logout(HttpSession session) {
        session.invalidate();
        return HttpResponse.ok();
    }
}
```

## Token Refresh

### **Refresh Token Endpoint**

```java
@Controller("/token")
public class TokenController {
    
    private final JwtTokenGenerator tokenGenerator;
    
    @Post("/refresh")
    @Secured(SecurityRule.IS_ANONYMOUS)
    public HttpResponse<BearerAccessRefreshToken> refresh(
            @Body RefreshTokenRequest request) {
        return tokenGenerator.refreshToken(request.getRefreshToken())
            .map(HttpResponse::ok)
            .orElse(HttpResponse.unauthorized());
    }
}
```

## Rate Limiting

### Настройка **Rate Limiting**

**application.yml:**

```yaml
micronaut:
  security:
    rate-limit:
      enabled: true
      login:
        enabled: true
        max-attempts: 5
        window: PT15M
```

### **Custom Rate Limiter**

```java
@Singleton
public class CustomRateLimiter implements RateLimiter {
    
    private final Map<String, AtomicInteger> attempts = new ConcurrentHashMap<>();
    
    @Override
    public boolean checkLimit(String identifier) {
        AtomicInteger count = attempts.computeIfAbsent(
            identifier, k -> new AtomicInteger(0));
        return count.incrementAndGet() <= 5;
    }
    
    @Scheduled(fixedDelay = "15m")
    public void resetAttempts() {
        attempts.clear();
    }
}
```

## CORS Configuration

### Настройка **CORS**

**application.yml:**

```yaml
micronaut:
  security:
    cors:
      enabled: true
      configurations:
        web:
          allowed-origins:
            - http://localhost:3000
            - https://example.com
          allowed-methods:
            - GET
            - POST
            - PUT
            - DELETE
          allowed-headers:
            - Authorization
            - Content-Type
          exposed-headers:
            - X-Total-Count
          allow-credentials: true
          max-age: 3600
```

## Security Headers

### Настройка **Security Headers**

**application.yml:**

```yaml
micronaut:
  security:
    headers:
      x-frame-options: DENY
      x-content-type-options: nosniff
      x-xss-protection: "1; mode=block"
      strict-transport-security: "max-age=31536000; includeSubDomains"
      content-security-policy: "default-src 'self'"
```

## Token Validation

### **JWT Token Validation**

```java
import io.micronaut.security.token.Claims;
import io.micronaut.security.token.jwt.validator.JwtTokenValidator;
import jakarta.inject.Singleton;

@Singleton
public class CustomTokenValidator implements JwtTokenValidator {
    
    @Override
    public Optional<Claims> validateToken(String token, HttpRequest<?> request) {
        // Кастомная валидация токена
        if (isTokenExpired(token)) {
            return Optional.empty();
        }
        
        if (isTokenRevoked(token)) {
            return Optional.empty();
        }
        
        return Optional.of(extractClaims(token));
    }
    
    private boolean isTokenExpired(String token) {
        // Проверка истечения токена
        return false;
    }
    
    private boolean isTokenRevoked(String token) {
        // Проверка отзыва токена
        return false;
    }
    
    private Claims extractClaims(String token) {
        // Извлечение claims из токена
        return null;
    }
}
```

## Security Events

### **Security Event Listeners**

```java
import io.micronaut.security.event.LoginSuccessfulEvent;
import io.micronaut.security.event.LoginFailedEvent;
import io.micronaut.context.event.ApplicationEventPublisher;
import jakarta.inject.Singleton;

@Singleton
public class SecurityEventListener {
    
    @EventListener
    public void onLoginSuccess(LoginSuccessfulEvent event) {
        log.info("User {} logged in successfully", event.getSource());
        // Логирование успешного входа
    }
    
    @EventListener
    public void onLoginFailure(LoginFailedEvent event) {
        log.warn("Login failed for user: {}", event.getSource());
        // Обработка неудачного входа
    }
}
```

## Password Policies

### **Password Validation**

```java
import jakarta.validation.Constraint;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = StrongPassword.Validator.class)
public @interface StrongPassword {
    String message() default "Password must be strong";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
    
    class Validator implements ConstraintValidator<StrongPassword, String> {
        @Override
        public boolean isValid(String password, ConstraintValidatorContext context) {
            if (password == null) {
                return true;
            }
            // Минимум 8 символов, хотя бы одна цифра, одна буква, один спецсимвол
            return password.length() >= 8 &&
                   password.matches(".*[0-9].*") &&
                   password.matches(".*[a-zA-Z].*") &&
                   password.matches(".*[!@#$%^&*].*");
        }
    }
}
```

## Security Headers

### **Security Headers Configuration**

**application.yml:**

```yaml
micronaut:
  server:
    cors:
      enabled: true
      configurations:
        web:
          allowedOrigins:
            - http://localhost:3000
          allowedMethods:
            - GET
            - POST
            - PUT
            - DELETE
  security:
    headers:
      x-frame-options: DENY
      x-content-type-options: nosniff
      x-xss-protection: 1; mode=block
      strict-transport-security: max-age=31536000
```

## Role-based Access Control

### **RBAC Implementation**

```java
import io.micronaut.security.annotation.Secured;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Get;

@Controller("/admin")
@Secured("ROLE_ADMIN")
public class AdminController {
    
    @Get("/users")
    @Secured({"ROLE_ADMIN", "ROLE_USER_MANAGER"})
    public List<User> listUsers() {
        return userService.findAll();
    }
    
    @Get("/settings")
    @Secured("ROLE_ADMIN")
    public Settings getSettings() {
        return settingsService.getSettings();
    }
}
```


## Заключение

**Micronaut Security** предоставляет мощные инструменты для реализации аутентификации и авторизации в приложениях. Поддержка **JWT**, **OAuth2**, **LDAP**, **session-based authentication**, **token refresh**, **rate limiting**, **CORS**, **security headers**, **token validation**, **security events**, **password policies**, **security headers configuration**, **RBAC** и других продвинутых возможностей обеспечивает гибкость и безопасность при выборе подхода к безопасности.

## Дополнительные ресурсы

- [**Micronaut Security** Documentation](https://micronaut-projects.github.io/micronaut-security/latest/guide/)
- [**Micronaut Security JWT**](https://micronaut-projects.github.io/micronaut-security/latest/guide/#jwt)
- [**Micronaut Security OAuth2**](https://micronaut-projects.github.io/micronaut-security/latest/guide/#oauth2)
- [**OWASP Security** Guidelines](https://owasp.org/www-project-web-security-testing-guide/)
- [**NIST Password** Guidelines](https://pages.nist.gov/800-63-3/sp800-63b.html)
