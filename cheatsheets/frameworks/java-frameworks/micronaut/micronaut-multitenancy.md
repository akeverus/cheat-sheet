---
title: "Micronaut: Multitenancy — Multi-tenant Applications"
description: "Полное руководство по multitenancy в Micronaut: tenant resolution, data isolation, routing и best practices"
tags:
  - micronaut
  - multitenancy
  - multi-tenant
  - tenant
  - isolation
  - java
  - kotlin
difficulty: "advanced"
prerequisites: ["micronaut/micronaut-basics.md", "micronaut/micronaut-data.md"]
next: ["micronaut-data.md", "micronaut-security.md"]
updated: "2026-04-20"
related: ["micronaut-data.md", "micronaut-security.md"]
---

# Micronaut: Multitenancy — Multi-tenant Applications

## Полезные ссылки

[Официальная документация Micronaut](https://docs.micronaut.io/)
[Micronaut GitHub](https://github.com/micronaut-projects/micronaut-core)

## Содержание

- [Введение](#введение)
  - [Основные возможности](#основные-возможности)
- [Настройка Multitenancy](#настройка-multitenancy)
  - [Зависимости](#зависимости)
  - [Конфигурация](#конфигурация)
- [Tenant Resolution](#tenant-resolution)
  - [HTTP Header Resolver](#http-header-resolver)
  - [Subdomain Resolver](#subdomain-resolver)
- [Data Isolation](#data-isolation)
  - [Tenant-aware Repository](#tenant-aware-repository)
  - [Tenant Service](#tenant-service)
- [Configuration per Tenant](#configuration-per-tenant)
  - [Tenant-specific Configuration](#tenant-specific-configuration)
  - [Dynamic Configuration](#dynamic-configuration)
- [Лучшие практики](#лучшие-практики)
  - [1. Всегда проверяйте tenant ID](#1-всегда-проверяйте-tenant-id)
  - [2. Изолируйте данные на уровне БД](#2-изолируйте-данные-на-уровне-бд)
  - [3. Используйте connection pooling per tenant](#3-используйте-connection-pooling-per-tenant)
- [Tenant Context](#tenant-context)
  - [Tenant Context Provider](#tenant-context-provider)
- [Database per Tenant](#database-per-tenant)
  - [Dynamic DataSource](#dynamic-datasource)
- [Tenant Filtering](#tenant-filtering)
  - [Automatic Tenant Filtering](#automatic-tenant-filtering)
- [Tenant Validation](#tenant-validation)
  - [Tenant Validation Service](#tenant-validation-service)
- [Tenant Caching](#tenant-caching)
  - [Tenant-aware Cache](#tenant-aware-cache)
- [Tenant Security](#tenant-security)
  - [Tenant-based Security](#tenant-based-security)
- [Заключение](#заключение)
- [Дополнительные ресурсы](#дополнительные-ресурсы)
- [См. также](#см-также)

## Введение

**Micronaut** предоставляет поддержку **multitenancy** для создания **multi-tenant** приложений. Это позволяет изолировать данные и конфигурацию для разных **tenants**.

### Основные возможности

- **Tenant Resolution**: Определение **tenant** из запроса
- **Data Isolation**: Изоляция данных по **tenants**
- **Configuration per Tenant**: Конфигурация для каждого **tenant**
- **Routing**: Маршрутизация по **tenants**

## Настройка Multitenancy

### Зависимости

**build.gradle:**

```gradle
dependencies {
    implementation("io.micronaut:micronaut-multitenancy")
}
```

### Конфигурация

**application.yml:**

```yaml
micronaut:
  multitenancy:
    enabled: true
    tenant-resolver:
      http-header:
        enabled: true
        header-name: X-Tenant-Id
```

## Tenant Resolution

### HTTP Header Resolver

```java
import io.micronaut.multitenancy.tenantresolver.HttpHeaderTenantResolver;
import jakarta.inject.Singleton;

@Singleton
public class CustomTenantResolver implements HttpHeaderTenantResolver {

    @Override
    public String resolveTenantIdentifier(HttpRequest<?> request) {
        return request.getHeaders().get("X-Tenant-Id");
    }
}
```

### Subdomain Resolver

```java
import io.micronaut.multitenancy.tenantresolver.TenantResolver;
import jakarta.inject.Singleton;

@Singleton
public class SubdomainTenantResolver implements TenantResolver {

    @Override
    public String resolveTenantIdentifier(HttpRequest<?> request) {
        String host = request.getUri().getHost();
        String[] parts = host.split("\\.");
        if (parts.length > 0) {
            return parts[0];
        }
        return "default";
    }
}
```

## Data Isolation

### Tenant-aware Repository

```java
import io.micronaut.data.annotation.Repository;
import io.micronaut.data.jdbc.annotation.JdbcRepository;
import io.micronaut.multitenancy.tenantresolver.TenantResolver;

@JdbcRepository
public interface UserRepository extends CrudRepository<User, Long> {

    @Query("SELECT * FROM users WHERE tenant_id = :tenantId")
    List<User> findByTenantId(String tenantId);
}
```

### Tenant Service

```java
import io.micronaut.multitenancy.tenantresolver.TenantResolver;
import jakarta.inject.Singleton;

@Singleton
public class TenantAwareUserService {
    private final TenantResolver tenantResolver;
    private final UserRepository userRepository;

    public TenantAwareUserService(
            TenantResolver tenantResolver,
            UserRepository userRepository) {
        this.tenantResolver = tenantResolver;
        this.userRepository = userRepository;
    }

    public List<User> getUsers(HttpRequest<?> request) {
        String tenantId = tenantResolver.resolveTenantIdentifier(request);
        return userRepository.findByTenantId(tenantId);
    }
}
```

## Configuration per Tenant

### Tenant-specific Configuration

**application.yml:**

```yaml
tenants:
  tenant1:
    database:
      url: jdbc:postgresql://localhost:5432/tenant1
  tenant2:
    database:
      url: jdbc:postgresql://localhost:5432/tenant2
```

### Dynamic Configuration

```java
import io.micronaut.context.annotation.ConfigurationProperties;
import jakarta.inject.Singleton;

@Singleton
@ConfigurationProperties("tenants")
public class TenantConfiguration {
    private Map<String, TenantConfig> tenants = new HashMap<>();

    public TenantConfig getTenantConfig(String tenantId) {
        return tenants.get(tenantId);
    }

    // Getters and setters
}
```

## Лучшие практики

### 1. Всегда проверяйте tenant `ID`

```java
// ✅ Хорошо
String tenantId = tenantResolver.resolveTenantIdentifier(request);
if (tenantId == null) {
    throw new TenantNotFoundException();
}
```

### 2. Изолируйте данные на уровне БД

```java
// ✅ Хорошо
@Query("SELECT * FROM users WHERE tenant_id = :tenantId")
List<User> findByTenantId(String tenantId);
```

### 3. Используйте connection pooling per tenant

```yaml
# ✅ Хорошо
tenants:
  tenant1:
    datasource:
      pool:
        max-size: 10
```

## Tenant Context

### Tenant Context Provider

```java
import io.micronaut.multitenancy.tenantresolver.TenantResolver;
import jakarta.inject.Singleton;

@Singleton
public class TenantContextProvider {
    private final ThreadLocal<String> tenantContext = new ThreadLocal<>();
    private final TenantResolver tenantResolver;

    public TenantContextProvider(TenantResolver tenantResolver) {
        this.tenantResolver = tenantResolver;
    }

    public void setTenant(HttpRequest<?> request) {
        String tenantId = tenantResolver.resolveTenantIdentifier(request);
        tenantContext.set(tenantId);
    }

    public String getTenant() {
        return tenantContext.get();
    }

    public void clear() {
        tenantContext.remove();
    }
}
```

## Database per Tenant

### Dynamic DataSource

```java
import io.micronaut.context.annotation.Bean;
import io.micronaut.context.annotation.Factory;
import jakarta.inject.Singleton;

@Factory
public class TenantDataSourceFactory {

    @Bean
    @Singleton
    public DataSource dataSource(TenantContextProvider tenantContext) {
        String tenantId = tenantContext.getTenant();
        String url = "jdbc:postgresql://localhost:5432/" + tenantId;
        // Создание DataSource для tenant
        return createDataSource(url);
    }
}
```

## Tenant Filtering

### Automatic Tenant Filtering

```java
import io.micronaut.data.annotation.Repository;
import io.micronaut.data.jdbc.annotation.JdbcRepository;

@JdbcRepository
public interface TenantAwareRepository extends CrudRepository<User, Long> {

    @Query("SELECT * FROM users WHERE tenant_id = :tenantId")
    List<User> findAllByTenant(String tenantId);

    // Автоматическая фильтрация по tenant_id
    default List<User> findAllForCurrentTenant(String tenantId) {
        return findAllByTenant(tenantId);
    }
}
```

## Tenant Validation

### Tenant Validation Service

```java
import io.micronaut.multitenancy.tenantresolver.TenantResolver;
import jakarta.inject.Singleton;

@Singleton
public class TenantValidationService {
    private final TenantResolver tenantResolver;
    private final Set<String> validTenants;

    public TenantValidationService(TenantResolver tenantResolver) {
        this.tenantResolver = tenantResolver;
        this.validTenants = Set.of("tenant1", "tenant2", "tenant3");
    }

    public void validateTenant(HttpRequest<?> request) {
        String tenantId = tenantResolver.resolveTenantIdentifier(request);
        if (tenantId == null || !validTenants.contains(tenantId)) {
            throw new InvalidTenantException("Invalid tenant: " + tenantId);
        }
    }
}
```

## Tenant Caching

### Tenant-aware Cache

```java
import io.micronaut.cache.annotation.Cacheable;
import jakarta.inject.Singleton;

@Singleton
public class TenantCacheService {

    @Cacheable("tenant-config")
    public TenantConfig getTenantConfig(String tenantId) {
        return tenantConfiguration.getTenantConfig(tenantId);
    }
}
```

## Tenant Security

### Tenant-based Security

```java
import io.micronaut.security.annotation.Secured;
import io.micronaut.http.annotation.Controller;
import jakarta.inject.Singleton;

@Controller("/api/tenant")
@Secured("ROLE_TENANT_ADMIN")
public class TenantController {

    @Get("/{tenantId}/users")
    @Secured("ROLE_USER_MANAGER")
    public List<User> getUsers(String tenantId) {
        // Проверка доступа к tenant
        validateTenantAccess(tenantId);
        return userService.findByTenant(tenantId);
    }
}
```


## Заключение

**Micronaut Multitenancy** предоставляет мощные инструменты для создания **multi-tenant** приложений. Поддержка **tenant resolution**, **data isolation**, **configuration per tenant**, **routing**, **tenant context**, **database per tenant**, **tenant filtering**, **validation**, **caching**, **security** и других продвинутых возможностей позволяет создавать масштабируемые приложения с изоляцией данных.

## Дополнительные ресурсы

- [**Micronaut Multitenancy** Documentation](https://micronaut-projects.github.io/micronaut-data/latest/guide/#multitenancy)
- [**Multi-tenancy Patterns**](https://docs.microsoft.com/en-us/azure/architecture/patterns/multi-tenancy)
- [**Tenant Isolation** Strategies](https://www.baeldung.com/java-multitenancy-spring-boot)

## См. также

- [Micronaut: Actuator — Health Checks, Metrics и Endpoints](micronaut-actuator.md)
- [Micronaut: Основы](micronaut-basics.md)
- [Micronaut: Batch Processing — Job Processing и Scheduling](micronaut-batch.md)
- [Micronaut: Caching — Cache Abstraction и Redis Cache](micronaut-cache.md)
- [Micronaut: Cloud Native — Service Discovery, Configuration и Distributed Tracing](micronaut-cloud.md)
