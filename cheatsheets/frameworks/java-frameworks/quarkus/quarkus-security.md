---
title: "Quarkus: Security - Authentication, Authorization и OAuth2"
description: "Полное руководство по безопасности в Quarkus: authentication, authorization, OAuth2, JWT, Keycloak и best practices"
tags:
  - quarkus
  - security
  - oauth2
  - jwt
  - keycloak
  - authentication
  - java
difficulty: "intermediate"
prerequisites: ["quarkus/quarkus-basics.md", "quarkus/quarkus-rest.md"]
next: ["quarkus-rest.md", "quarkus-reactive.md"]
updated: "2026-04-20"
related: ["quarkus-rest.md", "quarkus-reactive.md"]
---

# Quarkus: Security - Authentication, Authorization и OAuth2

## Полезные ссылки

[Официальная документация Quarkus](https://quarkus.io/guides/)
[Quarkus GitHub](https://github.com/quarkusio/quarkus)

## Содержание

- [Введение](#введение)
  - [Основные возможности](#основные-возможности)
- [Basic Security](#basic-security)
  - [Secured Endpoint](#secured-endpoint)
  - [Security Configuration](#security-configuration)
- [JWT Authentication](#jwt-authentication)
  - [JWT Configuration](#jwt-configuration)
  - [JWT Resource](#jwt-resource)
- [OAuth2](#oauth2)
  - [OAuth2 Configuration](#oauth2-configuration)
  - [OAuth2 Resource](#oauth2-resource)
- [Keycloak Integration](#keycloak-integration)
  - [Keycloak Configuration](#keycloak-configuration)
- [Лучшие практики](#лучшие-практики)
  - [1. Используйте JWT для stateless authentication](#1-используйте-jwt-для-stateless-authentication)
  - [2. Валидируйте все входные данные](#2-валидируйте-все-входные-данные)
  - [3. Используйте HTTPS в production](#3-используйте-https-в-production)
- [Custom Authentication](#custom-authentication)
  - [Custom Identity Provider](#custom-identity-provider)
- [Security Annotations](#security-annotations)
  - [Permissions](#permissions)
- [Security Context](#security-context)
  - [Accessing Security Context](#accessing-security-context)
- [OAuth2 Resource Server](#oauth2-resource-server)
  - [Resource Server Configuration](#resource-server-configuration)
  - [Resource Server Endpoint](#resource-server-endpoint)
- [OAuth2 Client](#oauth2-client)
  - [OAuth2 Client Configuration](#oauth2-client-configuration)
  - [OAuth2 Client Resource](#oauth2-client-resource)
- [Security Context Propagation](#security-context-propagation)
  - [Async Security Context](#async-security-context)
- [Role-Based Access Control](#role-based-access-control)
  - [Dynamic Role Assignment](#dynamic-role-assignment)
  - [Custom Role Provider](#custom-role-provider)
- [Password Hashing](#password-hashing)
  - [BCrypt Password Hashing](#bcrypt-password-hashing)
  - [Argon2 Password Hashing](#argon2-password-hashing)
- [Security Headers](#security-headers)
  - [Security Headers Filter](#security-headers-filter)
- [Rate Limiting](#rate-limiting)
  - [Rate Limiting Filter](#rate-limiting-filter)
- [Advanced Security Patterns](#advanced-security-patterns)
  - [Multi-Factor Authentication](#multi-factor-authentication)
  - [Security Audit Logging](#security-audit-logging)
  - [Token Refresh Strategy](#token-refresh-strategy)
- [Security Monitoring and Auditing](#security-monitoring-and-auditing)
  - [Security Event Logging](#security-event-logging)
  - [Failed Authentication Tracking](#failed-authentication-tracking)
- [Advanced Authorization Patterns](#advanced-authorization-patterns)
  - [Dynamic Role Assignment](#dynamic-role-assignment-1)
  - [Resource-Based Authorization](#resource-based-authorization)
- [Заключение](#заключение)
- [Дополнительные ресурсы](#дополнительные-ресурсы)
- [См. также](#см-также)

## Введение

**Quarkus** предоставляет мощную систему безопасности с поддержкой различных механизмов аутентификации и авторизации. Поддержка **OAuth2**, **JWT**, **Keycloak** и других стандартов позволяет создавать безопасные приложения.

### Основные возможности

- **Authentication**: Различные механизмы аутентификации
- **Authorization**: Контроль доступа
- **OAuth2**: Поддержка **OAuth2**
- **JWT**: **JSON Web Tokens**
- **Keycloak Integration**: Интеграция с **Keycloak**

## Basic Security

### Secured Endpoint

```java
import jakarta.annotation.security.RolesAllowed;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;

@Path("/api/users")
public class UserResource {

    @GET
    @RolesAllowed("user")
    public List<User> getUsers() {
        return userService.findAll();
    }

    @GET
    @Path("/admin")
    @RolesAllowed("admin")
    public List<User> getAdminUsers() {
        return userService.findAdminUsers();
    }
}
```

### Security Configuration

**application.properties:**

```properties
quarkus.security.users.embedded.enabled=true
quarkus.security.users.embedded.plain-text=true
quarkus.security.users.embedded.users.admin=admin
quarkus.security.users.embedded.roles.admin=admin,user
```

## JWT Authentication

### JWT Configuration

**application.properties:**

```properties
quarkus.security.jwt.enabled=true
quarkus.security.jwt.issuer=https://example.com/issuer
quarkus.security.jwt.audience=https://example.com/audience
mp.jwt.verify.publickey.location=https://example.com/public-key
```

### JWT Resource

```java
import jakarta.annotation.security.RolesAllowed;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import org.eclipse.microprofile.jwt.JsonWebToken;

@Path("/api/protected")
public class ProtectedResource {

    @Inject
    JsonWebToken jwt;

    @GET
    @RolesAllowed("user")
    public String getProtectedData() {
        String username = jwt.getClaim("preferred_username");
        return "Hello, " + username;
    }
}
```

## OAuth2

### OAuth2 Configuration

**application.properties:**

```properties
quarkus.oidc.auth-server-url=https://keycloak.example.com/realms/myrealm
quarkus.oidc.client-id=my-client
quarkus.oidc.credentials.secret=my-secret
```

### OAuth2 Resource

```java
import jakarta.annotation.security.RolesAllowed;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;

@Path("/api/oauth2")
public class OAuth2Resource {

    @GET
    @RolesAllowed("user")
    public String getOAuth2Data() {
        return "OAuth2 protected data";
    }
}
```

## Keycloak Integration

### Keycloak Configuration

**application.properties:**

```properties
quarkus.oidc.auth-server-url=http://localhost:8080/realms/quarkus
quarkus.oidc.client-id=quarkus-app
quarkus.oidc.credentials.secret=secret
quarkus.oidc.application-type=web-app
```

## Лучшие практики

### 1. Используйте JWT для stateless authentication

```java
// ✅ Хорошо
@Path("/api")
@RolesAllowed("user")
public class Resource {
    // JWT authentication
}
```

### 2. Валидируйте все входные данные

```java
// ✅ Хорошо
@POST
@Valid
public Response createUser(@Valid User user) {
    // Валидация
}
```

### 3. Используйте HTTPS в production

```properties
# ✅ Хорошо
quarkus.http.ssl-port=8443
quarkus.http.ssl.certificate.file=server.crt
```

## Custom Authentication

### Custom Identity Provider

```java
import io.quarkus.security.identity.SecurityIdentity;
import io.quarkus.security.identity.request.AuthenticationRequest;
import io.quarkus.security.identity.request.UsernamePasswordAuthenticationRequest;
import io.quarkus.security.runtime.QuarkusSecurityIdentity;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class CustomIdentityProvider
        implements io.quarkus.security.identity.IdentityProvider<UsernamePasswordAuthenticationRequest> {

    @Override
    public Class<UsernamePasswordAuthenticationRequest> getRequestType() {
        return UsernamePasswordAuthenticationRequest.class;
    }

    @Override
    public Uni<SecurityIdentity> authenticate(
            UsernamePasswordAuthenticationRequest request,
            AuthenticationRequestContext context) {
        String username = request.getUsername();
        String password = new String(request.getPassword().getPassword());

        // Проверка учетных данных
        if (validateCredentials(username, password)) {
            SecurityIdentity identity = QuarkusSecurityIdentity.builder()
                .setPrincipal(new SimplePrincipal(username))
                .addRole("user")
                .build();
            return Uni.createFrom().item(identity);
        }

        return Uni.createFrom().failure(new AuthenticationFailedException());
    }
}
```

## Security Annotations

### Permissions

```java
import jakarta.annotation.security.PermitAll;
import jakarta.annotation.security.DenyAll;
import jakarta.annotation.security.RolesAllowed;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;

@Path("/api")
public class SecurityResource {

    @GET
    @Path("/public")
    @PermitAll
    public String publicEndpoint() {
        return "Public data";
    }

    @GET
    @Path("/private")
    @RolesAllowed("user")
    public String privateEndpoint() {
        return "Private data";
    }

    @GET
    @Path("/admin")
    @RolesAllowed("admin")
    public String adminEndpoint() {
        return "Admin data";
    }

    @GET
    @Path("/denied")
    @DenyAll
    public String deniedEndpoint() {
        return "This should never be accessible";
    }
}
```

## Security Context

### Accessing Security Context

```java
import io.quarkus.security.identity.SecurityIdentity;
import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;

@Path("/api/user")
public class UserInfoResource {

    @Inject
    SecurityIdentity securityIdentity;

    @GET
    @Path("/info")
    @RolesAllowed("user")
    public Map<String, Object> getUserInfo() {
        return Map.of(
            "username", securityIdentity.getPrincipal().getName(),
            "roles", securityIdentity.getRoles()
        );
    }
}
```

## OAuth2 Resource Server

### Resource Server Configuration

**application.properties:**

```properties
quarkus.oidc.auth-server-url=https://keycloak.example.com/realms/myrealm
quarkus.oidc.client-id=my-resource-server
quarkus.oidc.application-type=service
```

### Resource Server Endpoint

```java
import jakarta.annotation.security.RolesAllowed;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import org.eclipse.microprofile.jwt.JsonWebToken;

@Path("/api/resource")
public class ResourceServerEndpoint {

    @Inject
    JsonWebToken jwt;

    @GET
    @RolesAllowed("user")
    public String getResource() {
        String username = jwt.getClaim("preferred_username");
        return "Resource for: " + username;
    }
}
```

## OAuth2 Client

### OAuth2 Client Configuration

**Настройка **OAuth2** клиента:**

```properties
# application.properties
quarkus.oidc.auth-server-url=https://keycloak.example.com/realms/myrealm
quarkus.oidc.client-id=my-client
quarkus.oidc.credentials.secret=my-secret
quarkus.oidc.application-type=web-app
quarkus.oidc.authentication.redirect-path=/callback
```

### OAuth2 Client Resource

**Использование **OAuth2** клиента:**

```java
import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import org.eclipse.microprofile.rest.client.inject.RestClient;

@Path("/oauth2")
public class OAuth2ClientResource {

    @Inject
    @RestClient
    OAuth2Service oAuth2Service;

    @GET
    @Path("/userinfo")
    @RolesAllowed("user")
    public UserInfo getUserInfo() {
        return oAuth2Service.getUserInfo();
    }
}
```

## Security Context Propagation

### Async Security Context

**Передача **security context** в асинхронных операциях:**

```java
import io.quarkus.security.identity.SecurityIdentity;
import io.smallrye.mutiny.Uni;
import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;

@Path("/async")
public class AsyncSecurityResource {

    @Inject
    SecurityIdentity securityIdentity;

    @GET
    @RolesAllowed("user")
    public Uni<String> getAsyncData() {
        String username = securityIdentity.getPrincipal().getName();
        return Uni.createFrom().item(() -> processData(username))
            .runSubscriptionOn(Infrastructure.getDefaultWorkerPool());
    }
}
```

## Role-Based Access Control

### Dynamic Role Assignment

**Динамическое назначение ролей:**

```java
import io.quarkus.security.identity.SecurityIdentity;
import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;

@Path("/admin")
public class AdminResource {

    @Inject
    SecurityIdentity securityIdentity;

    @GET
    @RolesAllowed("admin")
    public String adminOnly() {
        if (securityIdentity.hasRole("super-admin")) {
            return "Super admin access";
        }
        return "Admin access";
    }
}
```

### Custom Role Provider

**Создание кастомного провайдера ролей:**

```java
import io.quarkus.security.runtime.QuarkusSecurityIdentity;
import io.quarkus.security.identity.SecurityIdentity;
import io.quarkus.security.identity.request.AuthenticationRequest;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class CustomRoleProvider {

    public SecurityIdentity enhanceSecurityIdentity(
            SecurityIdentity identity,
            AuthenticationRequest request) {

        Set<String> roles = new HashSet<>(identity.getRoles());

        // Добавление дополнительных ролей на основе контекста
        if (isAdmin(identity.getPrincipal().getName())) {
            roles.add("admin");
        }

        return QuarkusSecurityIdentity.builder()
            .setPrincipal(identity.getPrincipal())
            .addRoles(roles)
            .build();
    }
}
```

## Password Hashing

### BCrypt Password Hashing

**Хеширование паролей:**

```java
import org.mindrot.jbcrypt.BCrypt;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class PasswordService {

    public String hashPassword(String plainPassword) {
        return BCrypt.hashpw(plainPassword, BCrypt.gensalt());
    }

    public boolean verifyPassword(String plainPassword, String hashedPassword) {
        return BCrypt.checkpw(plainPassword, hashedPassword);
    }
}
```

### Argon2 Password Hashing

**Использование **Argon2**:**

```java
import de.mkammerer.argon2.Argon2;
import de.mkammerer.argon2.Argon2Factory;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class Argon2PasswordService {

    private final Argon2 argon2 = Argon2Factory.create();

    public String hashPassword(String plainPassword) {
        return argon2.hash(10, 65536, 1, plainPassword.toCharArray());
    }

    public boolean verifyPassword(String plainPassword, String hashedPassword) {
        return argon2.verify(hashedPassword, plainPassword.toCharArray());
    }
}
```

## Security Headers

### Security Headers Filter

**Добавление **security headers**:**

```java
import jakarta.ws.rs.container.ContainerResponseFilter;
import jakarta.ws.rs.container.ContainerResponseContext;
import jakarta.ws.rs.ext.Provider;

@Provider
public class SecurityHeadersFilter implements ContainerResponseFilter {

    @Override
    public void filter(ContainerRequestContext requestContext,
                      ContainerResponseContext responseContext) {
        responseContext.getHeaders().add("X-Content-Type-Options", "nosniff");
        responseContext.getHeaders().add("X-Frame-Options", "DENY");
        responseContext.getHeaders().add("X-XSS-Protection", "1; mode=block");
        responseContext.getHeaders().add("Strict-Transport-Security",
            "max-age=31536000; includeSubDomains");
        responseContext.getHeaders().add("Content-Security-Policy",
            "default-src 'self'");
    }
}
```

## Rate Limiting

### Rate Limiting Filter

**Ограничение частоты запросов:**

```java
import jakarta.ws.rs.container.ContainerRequestFilter;
import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.ext.Provider;
import jakarta.ws.rs.core.Response;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

@Provider
public class RateLimitingFilter implements ContainerRequestFilter {

    private final Map<String, AtomicInteger> requestCounts = new ConcurrentHashMap<>();
    private final int maxRequests = 100;
    private final long timeWindow = 60000; // 1 minute

    @Override
    public void filter(ContainerRequestContext requestContext) {
        String clientId = getClientId(requestContext);
        AtomicInteger count = requestCounts.computeIfAbsent(
            clientId, k -> new AtomicInteger(0));

        if (count.incrementAndGet() > maxRequests) {
            requestContext.abortWith(
                Response.status(429)
                    .entity("Rate limit exceeded")
                    .build());
        }
    }
}
```

## Advanced Security Patterns

### Multi-Factor Authentication

**Многофакторная аутентификация:**

```java
@ApplicationScoped
public class MFAService {

    public Uni<AuthResult> authenticateWithMFA(String username, String password, String code) {
        return validateCredentials(username, password)
            .chain(() -> validateMFACode(username, code))
            .map(isValid -> new AuthResult(isValid));
    }
}
```

### Security Audit Logging

**Аудит безопасности:**

```java
@ApplicationScoped
public class SecurityAuditService {

    @Inject
    SecurityIdentity identity;

    public void auditSecurityEvent(String event, String details) {
        auditLog.log(new SecurityEvent(
            identity.getPrincipal().getName(),
            event,
            details,
            LocalDateTime.now()
        ));
    }
}
```

### Token Refresh Strategy

**Стратегия обновления токенов:**

```java
@ApplicationScoped
public class TokenRefreshService {

    public Uni<String> refreshToken(String refreshToken) {
        return validateRefreshToken(refreshToken)
            .chain(valid -> generateNewAccessToken(valid.getUserId()))
            .onFailure().recoverWithItem(() -> null);
    }
}
```

## Security Monitoring and Auditing

### Security Event Logging

**Логирование событий безопасности:**

```java
@ApplicationScoped
public class SecurityEventLogger {

    @Inject
    SecurityIdentity identity;

    public void logSecurityEvent(String eventType, String details) {
        SecurityEvent event = new SecurityEvent(
            identity.getPrincipal().getName(),
            eventType,
            details,
            LocalDateTime.now(),
            getClientIP()
        );
        securityLogService.log(event);
    }
}
```

### Failed Authentication Tracking

**Отслеживание неудачных попыток аутентификации:**

```java
@ApplicationScoped
public class AuthenticationTracker {

    private final Map<String, Integer> failedAttempts = new ConcurrentHashMap<>();
    private static final int MAX_ATTEMPTS = 5;

    public void recordFailedAttempt(String username) {
        int attempts = failedAttempts.merge(username, 1, Integer::sum);
        if (attempts >= MAX_ATTEMPTS) {
            lockAccount(username);
        }
    }

    public void recordSuccessfulAttempt(String username) {
        failedAttempts.remove(username);
    }
}
```

## Advanced Authorization Patterns

### Dynamic Role Assignment

**Динамическое назначение ролей:**

```java
@ApplicationScoped
public class DynamicRoleService {

    @Inject
    SecurityIdentity identity;

    public boolean hasPermission(String resource, String action) {
        // Динамическая проверка прав на основе контекста
        return checkPermission(identity, resource, action);
    }
}
```

### Resource-Based Authorization

**Авторизация на основе ресурсов:**

```java
@Path("/documents")
public class DocumentResource {

    @GET
    @Path("/{id}")
    @RolesAllowed("user")
    public Document getDocument(@PathParam("id") Long id) {
        Document doc = documentService.findById(id);
        // Проверка прав на конкретный документ
        if (!hasAccess(doc)) {
            throw new ForbiddenException();
        }
        return doc;
    }
}
```


## Заключение

**Quarkus Security** предоставляет мощные инструменты для обеспечения безопасности приложений. Поддержка различных механизмов аутентификации, **OAuth2**, **JWT**, **Keycloak**, **custom authentication**, **security context**, **resource server**, **password hashing**, **security headers**, **rate limiting** и других продвинутых возможностей позволяет создавать безопасные приложения. Правильное использование **security** паттернов, аутентификация, авторизация и защита данных являются ключевыми аспектами создания безопасных приложений.

## Дополнительные ресурсы

- [**Quarkus Security** Guide](https://quarkus.io/guides/security)
- [**Quarkus OIDC** Guide](https://quarkus.io/guides/security-openid-connect-web-authentication)
- [Keycloak Documentation](https://www.keycloak.org/documentation)
- [**OAuth2** Specification](https://oauth.net/2/)
- [**JWT** Specification](https://datatracker.ietf.org/doc/html/rfc7519)
- [**OWASP Top** 10](https://owasp.org/www-project-top-ten/)

## См. также

- [[quarkus-actuator|Quarkus: Actuator — Health Checks и Metrics]]
- [[quarkus-basics|Quarkus: Основы]]
- [[quarkus-cache|Quarkus: Cache — Кеширование данных]]
- [[quarkus-cloud|Quarkus: Cloud Native — Kubernetes, OpenShift и Service Mesh]]
- [[quarkus-core|Quarkus: Core — CDI, Bean Scopes и Configuration]]
