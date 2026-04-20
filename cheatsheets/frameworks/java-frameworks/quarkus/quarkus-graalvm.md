---
title: "Quarkus: GraalVM - Native Image и Compilation"
description: "Полное руководство по GraalVM Native Image в Quarkus: native compilation, reflection configuration, optimization и best practices"
tags:
  - quarkus
  - graalvm
  - native-image
  - compilation
  - optimization
  - java
difficulty: "intermediate"
prerequisites: ["quarkus/quarkus-basics.md", "quarkus/quarkus-core.md"]
next: ["quarkus-core.md", "quarkus-cloud.md"]
updated: "2026-02-11"
related: ["quarkus-core.md", "quarkus-cloud.md"]
---

# Quarkus: GraalVM — Native Image и Compilation

## Полезные ссылки

[Официальная документация Quarkus](https://quarkus.io/guides/)
[Quarkus GitHub](https://github.com/quarkusio/quarkus)

## Содержание

- [Введение](#введение)
  - [Основные возможности](#основные-возможности)
- [Native Compilation](#native-compilation)
  - [Building Native Image](#building-native-image)
- [Сборка native image](#сборка-native-image)
- [Или с Docker](#или-с-docker)
  - [Native Image Configuration](#native-image-configuration)
- [Reflection Configuration](#reflection-configuration)
  - [Reflection Config](#reflection-config)
  - [Using Reflection](#using-reflection)
- [Лучшие практики](#лучшие-практики)
  - [1. Используйте @RegisterForReflection](#1-используйте-registerforreflection)
  - [2. Минимизируйте использование reflection](#2-минимизируйте-использование-reflection)
  - [3. Тестируйте native image](#3-тестируйте-native-image)
- [✅ Хорошо](#хорошо)
- [Native Image Build Options](#native-image-build-options)
  - [Build Configuration](#build-configuration)
- [application.properties](#applicationproperties)
  - [Build Arguments](#build-arguments)
  - [Automatic Reflection Registration](#automatic-reflection-registration)
  - [Selective Reflection](#selective-reflection)
  - [Manual Reflection Config](#manual-reflection-config)
- [Resource Configuration](#resource-configuration)
  - [Native Image Resources](#native-image-resources)
  - [Resource Bundles](#resource-bundles)
- [Proxy Configuration](#proxy-configuration)
  - [Dynamic Proxy Registration](#dynamic-proxy-registration)
- [Serialization Configuration](#serialization-configuration)
  - [Java Serialization](#java-serialization)
  - [JSON Serialization](#json-serialization)
- [Build-Time vs Runtime](#build-time-vs-runtime)
  - [Build-Time Initialization](#build-time-initialization)
  - [Runtime Initialization](#runtime-initialization)
- [Native Image Optimization](#native-image-optimization)
  - [Memory Configuration](#memory-configuration)
  - [Garbage Collector](#garbage-collector)
- [или](#или)
  - [Optimization Levels](#optimization-levels)
- [Debug mode](#debug-mode)
- [Release mode (default)](#release-mode-default)
- [Testing Native Images](#testing-native-images)
  - [Native Image Tests](#native-image-tests)
  - [Build Configuration для тестов](#build-configuration-для-тестов)
- [Container Build](#container-build)
  - [Docker Build](#docker-build)
  - [Custom Builder Image](#custom-builder-image)
  - [Dockerfile для Native](#dockerfile-для-native)
- [Performance Tuning](#performance-tuning)
  - [Startup Time Optimization](#startup-time-optimization)
  - [Memory Footprint](#memory-footprint)
  - [Runtime Performance](#runtime-performance)
- [Common Issues и Solutions](#common-issues-и-solutions)
  - [Reflection Issues](#reflection-issues)
  - [Class Loading Issues](#class-loading-issues)
  - [Resource Access Issues](#resource-access-issues)
  - [1. Всегда регистрируйте классы для reflection](#1-всегда-регистрируйте-классы-для-reflection)
  - [4. Используйте build-time initialization где возможно](#4-используйте-build-time-initialization-где-возможно)
  - [5. Настраивайте ресурсы явно](#5-настраивайте-ресурсы-явно)
- [Native Image Debugging](#native-image-debugging)
  - [Debug Configuration](#debug-configuration)
  - [Native Image Logging](#native-image-logging)
- [Build Optimization](#build-optimization)
  - [Incremental Builds](#incremental-builds)
  - [Build Caching](#build-caching)
- [Использование кеша](#использование-кеша)
  - [6. Оптимизируйте сборку](#6-оптимизируйте-сборку)
- [Advanced Native Image Patterns](#advanced-native-image-patterns)
  - [Conditional Native Compilation](#conditional-native-compilation)
- [Native Image Build Optimization](#native-image-build-optimization)
  - [Build Time Analysis](#build-time-analysis)
- [Сборка с анализом времени](#сборка-с-анализом-времени)
- [Native Image Runtime Optimization](#native-image-runtime-optimization)
  - [Garbage Collection](#garbage-collection)
  - [Class Initialization](#class-initialization)
- [Решение проблем: нативные образы](#решение-проблем-нативные-образы)
  - [Common Build Errors](#common-build-errors)
  - [Debugging Native Images](#debugging-native-images)
- [Заключение](#заключение)
- [Дополнительные ресурсы](#дополнительные-ресурсы)

## Введение

**Quarkus** оптимизирован для **GraalVM Native Image**, что позволяет создавать нативные исполняемые файлы с минимальным временем запуска и потреблением памяти.

### Основные возможности

- **Native Image**: Компиляция в нативный бинарник
- **Fast Startup**: **Sub-second startup time**
- **Low Memory**: Минимальное потребление памяти
- **Reflection Configuration**: Настройка **reflection**

## Native Compilation

### Building Native Image

```bash
# Сборка native image
./mvnw package -Pnative

# Или с Docker
./mvnw package -Pnative -Dquarkus.native.container-build=true
```

### Native Image Configuration

**application.properties:**

```properties
quarkus.native.enabled=true
quarkus.native.additional-build-args=--enable-all-security-services
quarkus.native.native-image-xmx=4g
```

## Reflection Configuration

### Reflection Config

**reflection-`config.json`:**

```json
{
  "name": "com.example.User",
  "allDeclaredFields": true,
  "allDeclaredMethods": true,
  "allDeclaredConstructors": true
}
```

### Using Reflection

```java
import io.quarkus.runtime.annotations.RegisterForReflection;

@RegisterForReflection
public class User {
    private String name;
    private String email;

    // Reflection будет работать в native image
}
```

## Лучшие практики

### 1. Используйте @RegisterForReflection

```java
// ✅ Хорошо
@RegisterForReflection
public class MyClass {
    // Автоматическая регистрация для reflection
}
```

### 2. Минимизируйте использование reflection

```java
// ✅ Хорошо
// Избегайте reflection где возможно
```

### 3. Тестируйте native image

```bash
# ✅ Хорошо
./mvnw verify -Pnative
```

## Native Image Build Options

### Build Configuration

**Детальная настройка **native image build**:**

```properties
# application.properties
quarkus.native.enabled=true
quarkus.native.additional-build-args=--enable-all-security-services,--gc=G1
quarkus.native.native-image-xmx=4g
quarkus.native.container-build=true
quarkus.native.builder-image=quay.io/quarkus/ubi-quarkus-native-image:22.3-java17
```

### Build Arguments

**Дополнительные **build arguments**:**

```properties
quarkus.native.additional-build-args=\
  --initialize-at-build-time,\
  --report-unsupported-elements-at-runtime,\
  --allow-incomplete-classpath,\
  --enable-url-protocols=http,https
```

## Reflection Configuration

### Automatic Reflection Registration

**Автоматическая регистрация для **reflection**:**

```java
import io.quarkus.runtime.annotations.RegisterForReflection;

@RegisterForReflection
public class User {
    private String name;
    private String email;

    // Все поля и методы будут доступны через reflection
}
```

### Selective Reflection

**Селективная регистрация:**

```java
@RegisterForReflection(
    targets = {User.class, Order.class},
    methods = true,
    fields = true
)
public class ReflectionConfig {
    // Конфигурация reflection
}
```

### Manual Reflection Config

**Ручная настройка **reflection**:**

```json
{
  "name": "com.example.User",
  "allDeclaredFields": true,
  "allDeclaredMethods": true,
  "allDeclaredConstructors": true,
  "allPublicFields": true,
  "allPublicMethods": true,
  "allPublicConstructors": true
}
```

## Resource Configuration

### Native Image Resources

**Настройка ресурсов для **native image**:**

```properties
quarkus.native.resources.includes=/*.properties,/*.xml
quarkus.native.resources.excludes=/*.test.properties
```

### Resource Bundles

**Регистрация **resource bundles**:**

```java
import io.quarkus.runtime.annotations.RegisterForReflection;

@RegisterForReflection(targets = {
    java.util.ResourceBundle.class
})
public class ResourceConfig {
    // Регистрация resource bundles
}
```

## Proxy Configuration

### Dynamic Proxy Registration

**Регистрация **dynamic proxies**:**

```json
{
  "interfaces": [
    "com.example.ServiceInterface"
  ]
}
```

**Или через аннотации:**

```java
import io.quarkus.runtime.annotations.RegisterForReflection;

@RegisterForReflection(
    targets = ServiceInterface.class,
    registerFullHierarchy = true
)
public class ProxyConfig {
    // Регистрация proxy
}
```

## Serialization Configuration

### Java Serialization

**Настройка **Java serialization**:**

```properties
quarkus.native.enable-all-security-services=true
```

**Регистрация **serializable** классов:**

```java
import io.quarkus.runtime.annotations.RegisterForReflection;

@RegisterForReflection
public class SerializableUser implements Serializable {
    private static final long serialVersionUID = 1L;
    private String name;
    private String email;
}
```

### JSON Serialization

**Настройка **JSON serialization** для **native**:**

```java
import com.fasterxml.jackson.annotation.JsonProperty;
import io.quarkus.runtime.annotations.RegisterForReflection;

@RegisterForReflection
public class JsonUser {
    @JsonProperty("name")
    private String name;

    @JsonProperty("email")
    private String email;
}
```

## Build-Time vs Runtime

### Build-Time Initialization

**Инициализация на этапе сборки:**

```java
import io.quarkus.runtime.annotations.Recorder;

@Recorder
public class BuildTimeRecorder {

    public void initializeAtBuildTime() {
        // Код выполняется на этапе сборки
        System.setProperty("build.time", String.valueOf(System.currentTimeMillis()));
    }
}
```

### Runtime Initialization

**Инициализация в **runtime**:**

```java
import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class RuntimeInitializer {

    @PostConstruct
    void init() {
        // Код выполняется в runtime
    }
}
```

## Native Image Optimization

### Memory Configuration

**Настройка памяти для **native image**:**

```properties
quarkus.native.native-image-xmx=4g
quarkus.native.enable-all-security-services=true
```

### Garbage Collector

**Выбор **garbage collector**:**

```properties
quarkus.native.additional-build-args=--gc=G1
# или
quarkus.native.additional-build-args=--gc=serial
```

### Optimization Levels

**Уровни оптимизации:**

```properties
# Debug mode
quarkus.native.additional-build-args=-H:-OmitInlinedExceptionDetails

# Release mode (default)
quarkus.native.additional-build-args=-H:+OmitInlinedExceptionDetails
```

## Testing Native Images

### Native Image Tests

**Тестирование **native images**:**

```java
import io.quarkus.test.junit.QuarkusIntegrationTest;

@QuarkusIntegrationTest
public class NativeUserResourceIT {

    @Test
    void testNativeEndpoint() {
        given()
            .when().get("/api/users/1")
            .then()
            .statusCode(200);
    }
}
```

### Build Configuration для тестов

```properties
# application.properties
quarkus.native.enabled=true
quarkus.test.native-image-profile=test
```

## Container Build

### Docker Build

**Сборка **native image** в **Docker**:**

```bash
./mvnw package -Pnative -Dquarkus.native.container-build=true
```

### Custom Builder Image

**Использование кастомного **builder image**:**

```properties
quarkus.native.builder-image=quay.io/quarkus/ubi-quarkus-native-image:22.3-java17
quarkus.native.container-build=true
```

### Dockerfile для Native

```dockerfile
FROM quay.io/quarkus/ubi-quarkus-native-image:22.3-java17 AS build
COPY --chown=quarkus:quarkus mvnw /code/mvnw
COPY --chown=quarkus:quarkus .mvn /code/.mvn
COPY --chown=quarkus:quarkus pom.xml /code/
USER quarkus
WORKDIR /code
RUN ./mvnw -B org.apache.maven.plugins:maven-dependency-plugin:3.2.0:go-offline
COPY src /code/src
RUN ./mvnw package -Pnative

FROM registry.access.redhat.com/ubi8/ubi-minimal:8.6
WORKDIR /work/
COPY --from=build /code/target/*-runner /work/application
RUN chmod 775 /work/application
EXPOSE 8080
USER 185
ENTRYPOINT ["./application", "-Dquarkus.http.host=0.0.0.0"]
```

## Performance Tuning

### Startup Time Optimization

**Оптимизация времени запуска:**

```properties
quarkus.native.additional-build-args=\
  --initialize-at-build-time,\
  --no-fallback,\
  -H:+ReportExceptionStackTraces
```

### Memory Footprint

**Уменьшение потребления памяти:**

```properties
quarkus.native.additional-build-args=\
  --gc=serial,\
  -H:InitialCollectionPolicy=com.oracle.svm.core.genscavenge.CollectionPolicy$BySpaceAndTime
```

### Runtime Performance

**Оптимизация **runtime** производительности:**

```properties
quarkus.native.additional-build-args=\
  -H:+InlineBeforeAnalysis,\
  -H:MaxInliningDepth=10
```

## Common Issues и Solutions

### Reflection Issues

**Решение проблем с **reflection**:**

```java
// ✅ Хорошо - явная регистрация
@RegisterForReflection
public class ProblematicClass {
    // Класс будет доступен через reflection
}

// ❌ Плохо - отсутствие регистрации
public class ProblematicClass {
    // Может не работать в native image
}
```

### Class Loading Issues

**Решение проблем с **class loading**:**

```properties
quarkus.native.additional-build-args=\
  --allow-incomplete-classpath,\
  --report-unsupported-elements-at-runtime
```

### Resource Access Issues

**Решение проблем с доступом к ресурсам:**

```properties
quarkus.native.resources.includes=/*.properties,/*.xml,/*.json
```

## Native Image Debugging

### Debug Configuration

**Настройка отладки:**

```properties
# application.properties
quarkus.native.additional-build-args=\
  -H:+AllowVMInspection,\
  -H:+ReportExceptionStackTraces,\
  -H:+PrintAnalysisCallTree
```

### Native Image Logging

**Логирование в **native image**:**

```properties
quarkus.log.level=DEBUG
quarkus.log.category."io.quarkus".level=DEBUG
```

## Build Optimization

### Incremental Builds

**Инкрементальная сборка:**

```properties
quarkus.native.enable-all-security-services=true
quarkus.native.additional-build-args=-H:+InlineBeforeAnalysis
```

### Build Caching

**Кеширование сборки:**

```bash
# Использование кеша
./mvnw package -Pnative -Dquarkus.native.container-build=true
```

## Лучшие практики

### 1. Всегда регистрируйте классы для reflection

```java
// ✅ Хорошо
@RegisterForReflection
public class MyClass {
    // Автоматическая регистрация
}
```

### 2. Минимизируйте использование reflection

```java
// ✅ Хорошо - избегайте reflection где возможно
public class DirectAccess {
    public void useDirectly() {
        // Прямой доступ вместо reflection
    }
}

// ❌ Плохо - избыточное использование reflection
public class ReflectionAccess {
    public void useReflection() {
        Class<?> clazz = Class.forName("com.example.MyClass");
        // Reflection может не работать в native image
    }
}
```

### 3. Тестируйте native image

```java
// ✅ Хорошо
@QuarkusIntegrationTest
public class NativeTest {
    // Тест native image
}
```

### 4. Используйте build-time initialization где возможно

```java
// ✅ Хорошо
@Recorder
public class BuildTimeInit {
    // Инициализация на этапе сборки
}
```

### 5. Настраивайте ресурсы явно

```properties
# ✅ Хорошо
quarkus.native.resources.includes=/*.properties,/*.xml
```

### 6. Оптимизируйте сборку

```properties
# ✅ Хорошо
quarkus.native.additional-build-args=\
  --initialize-at-build-time,\
  -H:+InlineBeforeAnalysis
```

## Advanced Native Image Patterns

### Build-Time Initialization

**Инициализация на этапе сборки:**

```java
@Recorder
public class BuildTimeRecorder {

    @Record(ExecutionTime.STATIC_INIT)
    public void initializeAtBuildTime(RuntimeValue<String> config) {
        // Инициализация на этапе сборки
        System.setProperty("build.time.config", config.getValue());
    }
}
```

### Conditional Native Compilation

**Условная компиляция:**

```java
@ApplicationScoped
public class ConditionalService {

    @BuildTimeProperty(name = "feature.enabled")
    boolean featureEnabled;

    public void process() {
        if (featureEnabled) {
            // Код только для native
        }
    }
}
```

### Native Image Optimization

**Оптимизация **native image**:**

```properties
quarkus.native.additional-build-args=\
  --gc=G1,\
  -H:+ReportExceptionStackTraces,\
  -H:IncludeResources=.*properties,\
  -H:ReflectionConfigurationFiles=reflection-config.json
```

## Native Image Build Optimization

### Build Time Analysis

**Анализ времени сборки:**

```bash
# Сборка с анализом времени
./mvnw package -Pnative -Dquarkus.native.additional-build-args=-H:+PrintAnalysisCallTree
```

### Memory Configuration

**Конфигурация памяти:**

```properties
quarkus.native.native-image-xmx=4g
```

### Build Caching

**Кеширование сборки:**

```properties
quarkus.native.enable-all-security-services=true
quarkus.native.enable-all-charsets=true
```

## Native Image Runtime Optimization

### Garbage Collection

**Настройка сборки мусора:**

```properties
quarkus.native.additional-build-args=-H:+UseG1GC
```

### Class Initialization

**Инициализация классов:**

```properties
quarkus.native.additional-build-args=\
  --initialize-at-build-time=com.example.MyClass,\
  --initialize-at-run-time=com.example.OtherClass
```

## Troubleshooting Native Images

### Common Build Errors

**Типичные ошибки сборки:**

1. **Reflection errors**: Добавьте **reflection configuration**
2. **Resource not found**: Настройте ресурсы в **application.properties**
3. **Class initialization errors**: Настройте инициализацию классов

### Debugging Native Images

**Отладка **native images**:**

```properties
quarkus.native.debug.enabled=true
quarkus.native.debug.build-process=true
quarkus.native.additional-build-args=-H:+ReportExceptionStackTraces
```


## Заключение

**Quarkus GraalVM** предоставляет мощные инструменты для создания **native images**. Поддержка **native compilation**, **reflection configuration**, **optimization**, **debugging**, **build optimization** и других продвинутых возможностей позволяет создавать быстрые, легковесные приложения. Правильная настройка **reflection**, ресурсов, **proxy**, оптимизация сборки и отладка являются ключевыми аспектами создания эффективных **native images**.

## Дополнительные ресурсы

- [**Quarkus Native Image** Guide](https://quarkus.io/guides/building-native-image)
- [**GraalVM** Documentation](https://www.graalvm.org/latest/docs/)
- [**Native Image Best Practices**](https://www.graalvm.org/latest/reference-manual/native-image/optimization-and-performance/)
- [**GraalVM Native Image** Configuration](https://www.graalvm.org/latest/reference-manual/native-image/metadata/AutomaticMetadataCollection/)
- [**GraalVM Native Image** Debugging](https://www.graalvm.org/latest/reference-manual/native-image/debugging/)

## См. также

- [[quarkus-actuator|Quarkus: Actuator — Health Checks и Metrics]]
- [[quarkus-basics|Quarkus: Основы]]
- [[quarkus-cache|Quarkus: Cache — Кеширование данных]]
- [[quarkus-cloud|Quarkus: Cloud Native — Kubernetes, OpenShift и Service Mesh]]
- [[quarkus-core|Quarkus: Core — CDI, Bean Scopes и Configuration]]
