---
title: "Quarkus: Core - CDI, Bean Scopes и Configuration"
description: "Полное руководство по Quarkus Core: CDI, bean scopes, configuration, profiles, dependency injection и best practices"
tags:
  - quarkus
  - cdi
  - dependency-injection
  - beans
  - configuration
  - java
difficulty: "intermediate"
prerequisites: ["quarkus/quarkus-basics.md"]
next: ["quarkus-basics.md", "quarkus-rest.md"]
updated: "2026-02-11"
related: ["quarkus-basics.md", "quarkus-rest.md"]
---

# Quarkus: Core — CDI, Bean Scopes и Configuration

## Полезные ссылки

[Официальная документация Quarkus](https://quarkus.io/guides/)
[Quarkus GitHub](https://github.com/quarkusio/quarkus)

## Содержание

- [Quarkus: Core — CDI, Bean Scopes и Configuration](#quarkus-core-cdi-bean-scopes-и-configuration)
- [Введение](#введение)
  - [Основные возможности](#основные-возможности)
- [CDI Basics](#cdi-basics)
  - [Bean Definition](#bean-definition)
  - [Constructor Injection](#constructor-injection)
- [Bean Scopes](#bean-scopes)
  - [ApplicationScoped](#applicationscoped)
  - [RequestScoped](#requestscoped)
  - [Singleton](#singleton)
  - [Dependent](#dependent)
- [Configuration](#configuration)
  - [Type-safe Configuration](#type-safe-configuration)
  - [Using Configuration](#using-configuration)
- [Profiles](#profiles)
  - [Profile Configuration](#profile-configuration)
- [Default profile](#default-profile)
- [Development profile](#development-profile)
- [Production profile](#production-profile)
  - [Conditional Beans](#conditional-beans)
- [Лучшие практики](#лучшие-практики)
  - [1. Используйте constructor injection](#1-используйте-constructor-injection)
  - [2. Выбирайте правильный scope](#2-выбирайте-правильный-scope)
  - [3. Используйте type-safe configuration](#3-используйте-type-safe-configuration)
- [Bean Producers](#bean-producers)
  - [Producer Methods](#producer-methods)
  - [Producer Fields](#producer-fields)
- [Qualifiers](#qualifiers)
  - [Custom Qualifiers](#custom-qualifiers)
  - [Using Qualifiers](#using-qualifiers)
- [Interceptors](#interceptors)
  - [Interceptor Definition](#interceptor-definition)
  - [Interceptor Binding](#interceptor-binding)
- [Events](#events)
  - [Event Producer](#event-producer)
  - [Event Observer](#event-observer)
- [Configuration Properties](#configuration-properties)
  - [Nested Configuration](#nested-configuration)
  - [Configuration Mapping](#configuration-mapping)
- [Conditional Configuration](#conditional-configuration)
  - [Profile-based Configuration](#profile-based-configuration)
- [Bean Lifecycle](#bean-lifecycle)
  - [PostConstruct и PreDestroy](#postconstruct-и-predestroy)
- [Bean Validation](#bean-validation)
  - [Validation Integration](#validation-integration)
- [Advanced CDI Features](#advanced-cdi-features)
  - [Stereotypes](#stereotypes)
  - [Decorators](#decorators)
  - [Alternatives](#alternatives)
- [application.properties](#applicationproperties)
- [Configuration Sources](#configuration-sources)
  - [Multiple Configuration Files](#multiple-configuration-files)
- [application.properties (основной)](#applicationproperties-основной)
- [application-dev.properties](#application-devproperties)
- [application-prod.properties](#application-prodproperties)
  - [Environment Variables](#environment-variables)
  - [System Properties](#system-properties)
- [Build-Time vs Runtime](#build-time-vs-runtime)
  - [Build-Time Configuration](#build-time-configuration)
  - [Runtime Configuration](#runtime-configuration)
- [Observers и Async Events](#observers-и-async-events)
  - [Async Event Observers](#async-event-observers)
  - [Conditional Observers](#conditional-observers)
- [Bean Disposal](#bean-disposal)
  - [Disposal Methods](#disposal-methods)
- [Interceptor Ordering](#interceptor-ordering)
  - [Interceptor Priority](#interceptor-priority)
- [Bean Validation Integration](#bean-validation-integration)
  - [Method Validation](#method-validation)
- [Configuration Validation](#configuration-validation)
  - [4. Минимизируйте использование @Produces](#4-минимизируйте-использование-produces)
  - [5. Используйте events для слабой связанности](#5-используйте-events-для-слабой-связанности)
- [Build-Time vs Runtime Optimization](#build-time-vs-runtime-optimization)
  - [Build-Time Processing](#build-time-processing)
  - [Runtime Processing](#runtime-processing)
  - [Method Parameter Validation](#method-parameter-validation)
  - [Return Value Validation](#return-value-validation)
  - [6. Используйте build-time processing где возможно](#6-используйте-build-time-processing-где-возможно)
- [Advanced CDI Patterns](#advanced-cdi-patterns)
  - [Decorator Pattern](#decorator-pattern)
  - [Observer Pattern](#observer-pattern)
  - [Strategy Pattern with CDI](#strategy-pattern-with-cdi)
- [Configuration Management](#configuration-management)
  - [Dynamic Configuration](#dynamic-configuration)
- [Advanced Bean Lifecycle](#advanced-bean-lifecycle)
  - [Application Startup Events](#application-startup-events)
  - [Bean Initialization Order](#bean-initialization-order)
- [Configuration Patterns](#configuration-patterns)
  - [Environment-Specific Configuration](#environment-specific-configuration)
  - [Configuration Reload](#configuration-reload)
- [Build-Time Optimization](#build-time-optimization)
  - [Build Steps](#build-steps)
  - [Native Image Configuration](#native-image-configuration)
- [Заключение](#заключение)
- [Дополнительные ресурсы](#дополнительные-ресурсы)

## Введение

**Quarkus** использует **CDI** (`Contexts and Dependency Injection`) как основу для **dependency injection**. **Quarkus Arc** — это легковесная реализация **CDI**, оптимизированная для быстрого запуска и низкого потребления памяти.

### Основные возможности

- **CDI Integration**: Полная поддержка **CDI** `2.0`
- **Arc**: Легковесная реализация **CDI** от **Quarkus**
- **Bean Scopes**: **ApplicationScoped**, **RequestScoped**, **Singleton**, **Dependent**
- **Configuration**: **Type-safe configuration**
- **Profiles**: Различные профили для разных окружений

## CDI Basics

### Bean Definition

```java
// Внедрение зависимостей: @ApplicationScoped и @Inject
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

@ApplicationScoped
public class UserService {
    private final UserRepository userRepository;

    @Inject
    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public User createUser(User user) {
        return userRepository.save(user);
    }
}
```

### Constructor Injection

```java
// Бин в контексте приложения (singleton на приложение)
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class OrderService {
    private final UserService userService;
    private final PaymentService paymentService;

    // Constructor injection (рекомендуется)
    public OrderService(UserService userService, PaymentService paymentService) {
        this.userService = userService;
        this.paymentService = paymentService;
    }
}
```

## Bean Scopes

### ApplicationScoped

```java
// Бин в контексте приложения (singleton на приложение)
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class ApplicationScopedService {
    // Один экземпляр на все приложение
    private int counter = 0;

    public int increment() {
        return ++counter;
    }
}
```

### RequestScoped

```java
import jakarta.enterprise.context.RequestScoped;

@RequestScoped
public class RequestScopedService {
    // Новый экземпляр для каждого HTTP запроса
    private String requestId;

    public void setRequestId(String requestId) {
        this.requestId = requestId;
    }

    public String getRequestId() {
        return requestId;
    }
}
```

### Singleton

```java
import jakarta.inject.Singleton;

@Singleton
public class SingletonService {
    // Один экземпляр (аналог ApplicationScoped, но без прокси)
    public void doSomething() {
        // ...
    }
}
```

### Dependent

```java
import jakarta.enterprise.context.Dependent;

@Dependent
public class DependentService {
    // Новый экземпляр каждый раз при инъекции
    public void doSomething() {
        // ...
    }
}
```

## Configuration

### Type-safe Configuration

```java
import io.quarkus.arc.config.ConfigProperties;

@ConfigProperties(prefix = "app")
public class AppConfiguration {
    public String name;
    public Integer port;
    public DatabaseConfig database;

    public static class DatabaseConfig {
        public String url;
        public String username;
        public String password;
    }
}
```

### Using Configuration

```java
import jakarta.inject.Inject;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class ConfigService {
    @Inject
    AppConfiguration appConfig;

    public void printConfig() {
        System.out.println("App name: " + appConfig.name);
        System.out.println("Port: " + appConfig.port);
    }
}
```

## Profiles

### Profile Configuration

**application.properties:**

```properties
# Default profile
app.name=My App
app.port=8080

# Development profile
%dev.app.name=My App (Dev)
%dev.app.port=8081

# Production profile
%prod.app.name=My App (Prod)
%prod.app.port=8080
```

### Conditional Beans

```java
import jakarta.enterprise.inject.Produces;
import jakarta.enterprise.inject.Default;
import io.quarkus.arc.DefaultBean;

@ApplicationScoped
public class BeanProducer {

    @Produces
    @DefaultBean
    public DataSource defaultDataSource() {
        return new HikariDataSource();
    }

    @Produces
    @IfBuildProfile("prod")
    public DataSource productionDataSource() {
        return new ProductionDataSource();
    }
}
```

## Лучшие практики

### 1. Используйте constructor injection

```java
// ✅ Хорошо
@ApplicationScoped
public class Service {
    private final Dependency dependency;

    public Service(Dependency dependency) {
        this.dependency = dependency;
    }
}
```

### 2. Выбирайте правильный scope

```java
// ✅ Хорошо
@ApplicationScoped  // Для stateless сервисов
@RequestScoped      // Для request-specific данных
```

### 3. Используйте type-safe configuration

```java
// ✅ Хорошо
@ConfigProperties(prefix = "app")
public class AppConfig {
    // Type-safe доступ к конфигурации
}
```

## Bean Producers

### Producer Methods

```java
import jakarta.enterprise.inject.Produces;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Named;

@ApplicationScoped
public class DataSourceProducer {

    @Produces
    @ApplicationScoped
    @Named("primary")
    public DataSource primaryDataSource() {
        HikariConfig config = new HikariConfig();
        config.setJdbcUrl("jdbc:postgresql://localhost:5432/primary");
        return new HikariDataSource(config);
    }

    @Produces
    @ApplicationScoped
    @Named("secondary")
    public DataSource secondaryDataSource() {
        HikariConfig config = new HikariConfig();
        config.setJdbcUrl("jdbc:postgresql://localhost:5432/secondary");
        return new HikariDataSource(config);
    }
}
```

### Producer Fields

```java
import jakarta.enterprise.inject.Produces;
import jakarta.inject.Singleton;

@Singleton
public class ConfigurationProducer {

    @Produces
    @ApplicationScoped
    public String applicationName = "My Application";

    @Produces
    @ApplicationScoped
    public Integer maxConnections = 100;
}
```

## Qualifiers

### Custom Qualifiers

```java
import jakarta.inject.Qualifier;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Qualifier
@Retention(RetentionPolicy.RUNTIME)
public @interface Database {
    String value();
}
```

### Using Qualifiers

```java
import jakarta.inject.Inject;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class DatabaseService {

    @Inject
    @Database("primary")
    DataSource primaryDataSource;

    @Inject
    @Database("secondary")
    DataSource secondaryDataSource;
}
```

## Interceptors

### Interceptor Definition

```java
import jakarta.interceptor.Interceptor;
import jakarta.interceptor.AroundInvoke;
import jakarta.interceptor.InvocationContext;

@Interceptor
@Loggable
public class LoggingInterceptor {

    @AroundInvoke
    public Object log(InvocationContext context) throws Exception {
        System.out.println("Entering method: " + context.getMethod().getName());
        try {
            Object result = context.proceed();
            System.out.println("Exiting method: " + context.getMethod().getName());
            return result;
        } catch (Exception e) {
            System.out.println("Exception in method: " + context.getMethod().getName());
            throw e;
        }
    }
}
```

### Interceptor Binding

```java
import jakarta.interceptor.InterceptorBinding;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@InterceptorBinding
@Retention(RetentionPolicy.RUNTIME)
public @interface Loggable {
}
```

## Events

### Event Producer

```java
import jakarta.enterprise.event.Event;
import jakarta.inject.Inject;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class UserService {

    @Inject
    Event<UserCreated> userCreatedEvent;

    public User createUser(User user) {
        User created = userRepository.save(user);
        userCreatedEvent.fire(new UserCreated(created));
        return created;
    }
}
```

### Event Observer

```java
import jakarta.enterprise.event.Observes;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class UserEventListener {

    public void onUserCreated(@Observes UserCreated event) {
        System.out.println("User created: " + event.getUser().getName());
        // Отправка email, логирование и т.д.
    }
}
```

## Configuration Properties

### Nested Configuration

```java
import io.quarkus.arc.config.ConfigProperties;

@ConfigProperties(prefix = "app")
public class AppConfiguration {
    public String name;
    public ServerConfig server;
    public DatabaseConfig database;

    public static class ServerConfig {
        public Integer port;
        public String host;
    }

    public static class DatabaseConfig {
        public String url;
        public String username;
        public String password;
        public PoolConfig pool;

        public static class PoolConfig {
            public Integer minSize;
            public Integer maxSize;
        }
    }
}
```

### Configuration Mapping

**application.properties:**

```properties
app.name=My Application
app.server.port=8080
app.server.host=localhost
app.database.url=jdbc:postgresql://localhost:5432/mydb
app.database.username=user
app.database.password=password
app.database.pool.min-size=5
app.database.pool.max-size=20
```

## Conditional Configuration

### Profile-based Configuration

```java
import io.quarkus.arc.DefaultBean;
import io.quarkus.arc.profile.IfBuildProfile;
import jakarta.enterprise.inject.Produces;

@ApplicationScoped
public class ConditionalProducer {

    @Produces
    @DefaultBean
    public DataSource defaultDataSource() {
        return createDefaultDataSource();
    }

    @Produces
    @IfBuildProfile("prod")
    public DataSource productionDataSource() {
        return createProductionDataSource();
    }

    @Produces
    @IfBuildProfile("dev")
    public DataSource developmentDataSource() {
        return createDevelopmentDataSource();
    }
}
```

## Bean Lifecycle

### PostConstruct и PreDestroy

```java
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class LifecycleService {

    @PostConstruct
    public void init() {
        System.out.println("Service initialized");
        // Инициализация ресурсов
    }

    @PreDestroy
    public void cleanup() {
        System.out.println("Service destroyed");
        // Очистка ресурсов
    }
}
```

## Bean Validation

### Validation Integration

```java
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class ValidationService {

    public void processUser(@Valid @NotNull User user) {
        // Валидация выполняется автоматически
        userRepository.save(user);
    }
}
```

## Advanced CDI Features

### Stereotypes

**Создание и использование **stereotypes**:**

```java
import jakarta.enterprise.inject.Stereotype;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Stereotype
@ApplicationScoped
@Transactional
@Retention(RetentionPolicy.RUNTIME)
public @interface Service {
}

// Использование
@Service
public class UserService {
    // Автоматически ApplicationScoped и Transactional
}
```

### Decorators

**Создание **decorators** для расширения функциональности:**

```java
import jakarta.decorator.Decorator;
import jakarta.decorator.Delegate;
import jakarta.inject.Inject;

@Decorator
public abstract class CachingUserService implements UserService {

    @Inject
    @Delegate
    UserService delegate;

    @Override
    public User findById(Long id) {
        // Кеширование перед вызовом делегата
        User cached = cache.get(id);
        if (cached != null) {
            return cached;
        }
        User user = delegate.findById(id);
        cache.put(id, user);
        return user;
    }
}
```

### Alternatives

**Использование **alternatives** для замены реализации:**

```java
import jakarta.enterprise.inject.Alternative;
import jakarta.enterprise.inject.Default;
import jakarta.enterprise.context.ApplicationScoped;

@Alternative
@ApplicationScoped
public class MockUserService implements UserService {
    // Mock реализация для тестирования
}
```

**Активация через конфигурацию:**

```properties
# application.properties
quarkus.arc.selected-alternatives=com.example.MockUserService
```

## Configuration Sources

### Multiple Configuration Files

**Использование нескольких файлов конфигурации:**

```properties
# application.properties (основной)
app.name=My Application

# application-dev.properties
%dev.app.name=My Application (Dev)

# application-prod.properties
%prod.app.name=My Application (Prod)
```

### Environment Variables

**Использование переменных окружения:**

```properties
# application.properties
app.database.url=${DB_URL:jdbc:postgresql://localhost:5432/mydb}
app.database.username=${DB_USERNAME:user}
app.database.password=${DB_PASSWORD:password}
```

### System Properties

**Использование **system properties**:**

```bash
java -Dapp.name=MyApp -jar app.jar
```

## Build-Time vs Runtime

### Build-Time Configuration

**Конфигурация на этапе сборки:**

```java
import io.quarkus.runtime.annotations.Recorder;

@Recorder
public class BuildTimeRecorder {

    public void configureAtBuildTime(String config) {
        // Конфигурация выполняется на этапе сборки
        System.setProperty("build.time.config", config);
    }
}
```

### Runtime Configuration

**Конфигурация в **runtime**:**

```java
import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class RuntimeConfig {

    @PostConstruct
    void init() {
        // Конфигурация выполняется в runtime
    }
}
```

## Observers и Async Events

### Async Event Observers

**Асинхронная обработка событий:**

```java
import jakarta.enterprise.event.ObservesAsync;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class AsyncEventObserver {

    public void onUserCreatedAsync(@ObservesAsync UserCreated event) {
        // Асинхронная обработка события
        sendEmail(event.getUser());
        logEvent(event);
    }
}
```

### Conditional Observers

**Условные **observers**:**

```java
import jakarta.enterprise.event.Observes;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class ConditionalObserver {

    public void onUserCreated(
            @Observes(during = TransactionPhase.AFTER_SUCCESS)
            UserCreated event) {
        // Обработка только после успешной транзакции
    }
}
```

## Bean Disposal

### Disposal Methods

**Методы для очистки ресурсов:**

```java
import jakarta.enterprise.inject.Disposes;
import jakarta.enterprise.inject.Produces;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class ResourceProducer {

    @Produces
    @ApplicationScoped
    public Connection createConnection() {
        return new Connection();
    }

    public void closeConnection(@Disposes Connection connection) {
        connection.close();
    }
}
```

## Interceptor Ordering

### Interceptor Priority

**Управление порядком выполнения **interceptors**:**

```java
import jakarta.annotation.Priority;
import jakarta.interceptor.Interceptor;
import jakarta.interceptor.AroundInvoke;

@Priority(1000)
@Interceptor
@Loggable
public class LoggingInterceptor {
    // Выполняется первым
}

@Priority(2000)
@Interceptor
@Transactional
public class TransactionInterceptor {
    // Выполняется вторым
}
```

## Bean Validation Integration

### Method Validation

**Валидация параметров и возвращаемых значений:**

```java
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Min;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class ValidationService {

    @Valid
    public User createUser(
            @NotNull @Valid User user,
            @Min(1) Long organizationId) {
        // Валидация параметров и возвращаемого значения
        return userRepository.save(user);
    }
}
```

## Configuration Validation

### Configuration Validation

**Валидация конфигурации при старте:**

```java
import io.quarkus.arc.config.ConfigProperties;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Min;

@ConfigProperties(prefix = "app")
public class ValidatedConfiguration {

    @NotNull
    public String name;

    @Min(1)
    public Integer port;

    @NotNull
    public DatabaseConfig database;
}
```

## Build-Time vs Runtime Optimization

### Build-Time Processing

**Обработка на этапе сборки:**

```java
import io.quarkus.runtime.annotations.Recorder;
import io.quarkus.runtime.annotations.BuildStep;

public class BuildTimeProcessor {

    @BuildStep
    public void processAtBuildTime() {
        // Код выполняется на этапе сборки
        // Оптимизация, генерация кода и т.д.
    }
}
```

### Runtime Processing

**Обработка в **runtime**:**

```java
import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class RuntimeProcessor {

    @PostConstruct
    void processAtRuntime() {
        // Код выполняется в runtime
        // Динамическая конфигурация и т.д.
    }
}
```

## Bean Validation Integration

### Method Parameter Validation

**Валидация параметров методов:**

```java
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Min;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class ValidatedService {

    public User createUser(
            @NotNull @Valid User user,
            @Min(1) Long organizationId) {
        // Валидация параметров выполняется автоматически
        return userRepository.save(user);
    }
}
```

### Return Value Validation

**Валидация возвращаемых значений:**

```java
@ApplicationScoped
public class ReturnValueValidation {

    @Valid
    public User getUser(@NotNull Long id) {
        // Возвращаемое значение также валидируется
        return userRepository.findById(id);
    }
}
```

## Advanced CDI Patterns

### Decorator Pattern

**Реализация **Decorator pattern**:**

```java
@Decorator
@Priority(1)
public abstract class UserServiceDecorator implements UserService {

    @Inject
    @Delegate
    UserService delegate;

    @Override
    public User createUser(User user) {
        // Дополнительная логика перед вызовом
        logUserCreation(user);
        User created = delegate.createUser(user);
        // Дополнительная логика после вызова
        notifyUserCreation(created);
        return created;
    }
}
```

### Observer Pattern

**Реализация **Observer pattern**:**

```java
@ApplicationScoped
public class EventObserver {

    @Observes
    void onUserCreated(@ObservesAsync UserCreatedEvent event) {
        // Асинхронная обработка события
        processUserCreation(event.getUser());
    }

    @Observes
    @Priority(100)
    void onUserCreatedPriority(UserCreatedEvent event) {
        // Обработка с приоритетом
        validateUser(event.getUser());
    }
}
```

### Strategy Pattern with CDI

**Реализация **Strategy pattern**:**

```java
@Qualifier
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE, ElementType.METHOD, ElementType.FIELD, ElementType.PARAMETER})
public @interface PaymentStrategy {
    PaymentType value();
}

@ApplicationScoped
public class PaymentService {

    @Inject
    @Any
    Instance<PaymentProcessor> processors;

    public void processPayment(Payment payment) {
        PaymentProcessor processor = processors.stream()
            .filter(p -> p.supports(payment.getType()))
            .findFirst()
            .orElseThrow();
        processor.process(payment);
    }
}
```

## Configuration Management

### Dynamic Configuration

**Динамическая конфигурация:**

```java
@ApplicationScoped
public class DynamicConfigService {

    @ConfigProperty(name = "app.feature.enabled")
    boolean featureEnabled;

    @Inject
    Config config;

    public boolean isFeatureEnabled() {
        return config.getOptionalValue("app.feature.enabled", Boolean.class)
            .orElse(false);
    }
}
```

### Configuration Validation

**Валидация конфигурации:**

```java
@ConfigMapping(prefix = "app")
public interface AppConfig {

    @NotNull
    @Size(min = 1, max = 100)
    String name();

    @Min(1)
    @Max(100)
    int maxConnections();
}
```

## Advanced Bean Lifecycle

### Application Startup Events

**События запуска приложения:**

```java
@ApplicationScoped
public class StartupListener {

    @Observes
    void onStart(@Observes StartupEvent event) {
        // Инициализация при старте
        initializeApplication();
    }

    @Observes
    void onShutdown(@Observes ShutdownEvent event) {
        // Очистка при остановке
        cleanupApplication();
    }
}
```

### Bean Initialization Order

**Порядок инициализации бинов:**

```java
@ApplicationScoped
@Priority(1)
public class HighPriorityService {
    // Инициализируется первым
}

@ApplicationScoped
@Priority(100)
public class LowPriorityService {
    // Инициализируется позже
}
```

## Configuration Patterns

### Environment-Specific Configuration

**Конфигурация для разных окружений:**

```properties
# application.properties
app.name=My App

# application-dev.properties
app.name=My App (Dev)
app.debug=true

# application-prod.properties
app.name=My App (Production)
app.debug=false
```

### Configuration Reload

**Перезагрузка конфигурации:**

```java
@ApplicationScoped
public class ConfigReloadService {

    @ConfigProperty(name = "app.setting")
    String setting;

    @Inject
    Config config;

    public String getSetting() {
        // Получение актуального значения
        return config.getOptionalValue("app.setting", String.class)
            .orElse("default");
    }
}
```

## Bean Validation Integration

### Method Parameter Validation

**Валидация параметров методов:**

```java
@ApplicationScoped
public class ValidatedService {

    @Valid
    public User createUser(@NotNull @Valid User user) {
        // Валидация выполняется автоматически
        return userRepository.save(user);
    }
}
```

### Return Value Validation

**Валидация возвращаемых значений:**

```java
@ApplicationScoped
public class ValidatedReturnService {

    @Valid
    public @NotNull User getUser(@Min(1) Long id) {
        return userRepository.findById(id);
    }
}
```

## Build-Time Optimization

### Build Steps

**Шаги сборки:**

```java
@BuildStep
public void buildStep(BuildProducer<AdditionalBeanBuildItem> producer) {
    // Оптимизация на этапе сборки
    producer.produce(new AdditionalBeanBuildItem(MyService.class));
}
```

### Native Image Configuration

**Конфигурация для **native image**:**

```java
@BuildStep
public void nativeImageConfig(BuildProducer<NativeImageResourceBuildItem> producer) {
    producer.produce(new NativeImageResourceBuildItem("META-INF/resources/index.html"));
}
```


## Заключение

**Quarkus Core** предоставляет мощную систему **dependency injection** через **CDI** и **Arc**. Поддержка различных **bean scopes**, **type-safe configuration**, **profiles**, **conditional beans**, **producers**, **qualifiers**, **interceptors**, **events**, **lifecycle hooks**, **validation**, **build-time optimization** и других продвинутых возможностей позволяет создавать гибкие и эффективные приложения. Правильное использование **CDI** паттернов, конфигурации, **lifecycle management**, **validation** и **build-time** оптимизации являются ключевыми аспектами создания качественных приложений.

## Дополнительные ресурсы

- [**Quarkus CDI** Documentation](https://quarkus.io/guides/cdi-reference)
- [**Quarkus Configuration** Guide](https://quarkus.io/guides/config-reference)
- [**CDI** Specification](https://jakarta.ee/specifications/cdi/)
- [**Quarkus** Arc Documentation](https://quarkus.io/guides/cdi-reference#quarkus-arc)
- [**Bean Validation** Specification](https://beanvalidation.org/2.0/)

## См. также

- [[quarkus-actuator|Quarkus: Actuator — Health Checks и Metrics]]
- [[quarkus-basics|Quarkus: Основы]]
- [[quarkus-cache|Quarkus: Cache — Кеширование данных]]
- [[quarkus-cloud|Quarkus: Cloud Native — Kubernetes, OpenShift и Service Mesh]]
- [[quarkus-data|Quarkus: Data Access — Hibernate ORM, Panache и Repositories]]
