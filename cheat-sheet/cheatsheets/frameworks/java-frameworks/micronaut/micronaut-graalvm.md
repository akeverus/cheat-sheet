---
title: "Micronaut: GraalVM Native Images - Compilation и Optimization"
description: "Полное руководство по созданию native images с GraalVM в Micronaut: настройка, компиляция, оптимизация и best practices"
tags: ["micronaut", "graalvm", "native-image", "compilation", "optimization", "java", "kotlin"]
difficulty: "advanced"
prerequisites: ["micronaut/micronaut-basics.md", "micronaut/micronaut-core.md"]
next: []
updated: "2025-01-16"
related: ["micronaut-cloud.md", "micronaut-performance.md"]
---

# Micronaut: GraalVM Native Images - Compilation и Optimization

## Введение

Micronaut изначально разработан для поддержки GraalVM Native Images благодаря compile-time dependency injection и минимальному использованию reflection. Это позволяет создавать быстрые, легковесные приложения с минимальным временем запуска и потреблением памяти.

### Преимущества Native Images

- **Быстрый старт**: Время запуска в миллисекундах вместо секунд
- **Низкое потребление памяти**: Потребление памяти в мегабайтах вместо сотен мегабайт
- **Меньший размер**: Бинарные файлы меньше, чем JAR архивы
- **Оптимизация**: AOT (Ahead-of-Time) компиляция для максимальной производительности

## Настройка GraalVM

### Установка GraalVM

```bash
# Используя SDKMAN
sdk install java 22.0.0-graal

# Или скачать с официального сайта
# https://www.graalvm.org/downloads/
```

### Установка Native Image

```bash
gu install native-image
```

### Проверка установки

```bash
native-image --version
java -version
```

## Настройка проекта

### Gradle Configuration

**build.gradle:**

```gradle
plugins {
    id("com.github.johnrengelman.shadow") version "8.1.1"
    id("io.micronaut.application") version "4.1.4"
    id("io.micronaut.aot") version "4.1.4"
}

dependencies {
    annotationProcessor("io.micronaut:micronaut-http-validation")
    implementation("io.micronaut:micronaut-http-server-netty")
    implementation("io.micronaut:micronaut-jackson-databind")
    runtimeOnly("ch.qos.logback:logback-classic")
}

graalvmNative {
    binaries {
        main {
            imageName = 'my-app'
            mainClass = 'com.example.Application'
            buildArgs.add('--verbose')
            buildArgs.add('--no-fallback')
            buildArgs.add('--install-exit-handlers')
        }
    }
}
```

### Maven Configuration

**pom.xml:**

```xml
<plugin>
    <groupId>org.graalvm.buildtools</groupId>
    <artifactId>native-maven-plugin</artifactId>
    <version>0.9.28</version>
    <executions>
        <execution>
            <id>build-native</id>
            <phase>package</phase>
            <goals>
                <goal>compile-no-fork</goal>
            </goals>
        </execution>
    </executions>
</plugin>
```

## Компиляция Native Image

### Компиляция с Gradle

```bash
./gradlew nativeCompile
```

### Компиляция с Maven

```bash
mvn native:compile
```

### Прямая компиляция

```bash
native-image \
    --no-fallback \
    --install-exit-handlers \
    -H:IncludeResources="application.yml|application.properties" \
    -H:Name=my-app \
    -cp build/libs/my-app-all.jar \
    com.example.Application
```

## Reflection Configuration

### Автоматическая генерация

Micronaut автоматически генерирует reflection configuration во время компиляции для большинства случаев.

### Ручная настройка

**META-INF/native-image/reflect-config.json:**

```json
[
  {
    "name": "com.example.User",
    "allDeclaredFields": true,
    "allDeclaredMethods": true,
    "allDeclaredConstructors": true
  }
]
```

### Программная настройка

```java
import io.micronaut.core.annotation.TypeHint;

@TypeHint(
    value = {User.class, Order.class},
    accessType = TypeHint.AccessType.ALL_PUBLIC
)
public class Application {
    // ...
}
```

## Resource Configuration

### Включение ресурсов

**application.yml:**

```yaml
graalvm:
  native-image:
    resources:
      includes:
        - "application.yml"
        - "application-*.yml"
        - "logback.xml"
      excludes:
        - "**/*.test.yml"
```

### Программная настройка

```java
import io.micronaut.core.annotation.TypeHint;

@TypeHint(
    value = {Application.class},
    accessType = TypeHint.AccessType.ALL_PUBLIC,
    typeNames = {
        "application.yml",
        "logback.xml"
    }
)
public class Application {
    // ...
}
```

## Serialization Configuration

### Jackson Serialization

Для Jackson автоматически генерируется конфигурация, но можно настроить вручную:

**META-INF/native-image/serialization-config.json:**

```json
[
  "com.example.User",
  "com.example.Order"
]
```

### Программная настройка

```java
import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.micronaut.context.annotation.Bean;
import io.micronaut.context.annotation.Factory;

@Factory
public class JacksonFactory {
    
    @Bean
    public ObjectMapper objectMapper() {
        ObjectMapper mapper = new ObjectMapper();
        mapper.setVisibility(
            mapper.getSerializationConfig()
                .getDefaultVisibilityChecker()
                .withFieldVisibility(JsonAutoDetect.Visibility.ANY)
        );
        return mapper;
    }
}
```

## Proxy Configuration

### Настройка прокси

**META-INF/native-image/proxy-config.json:**

```json
[
  ["com.example.UserService"],
  ["com.example.OrderService", "com.example.PaymentService"]
]
```

### Программная настройка

```java
import io.micronaut.core.annotation.TypeHint;

@TypeHint(
    value = {UserService.class},
    accessType = TypeHint.AccessType.ALL_PUBLIC
)
public class Application {
    // ...
}
```

## JNI Configuration

### Настройка JNI

**META-INF/native-image/jni-config.json:**

```json
[
  {
    "name": "com.example.NativeLibrary",
    "methods": [
      {
        "name": "nativeMethod",
        "parameterTypes": ["java.lang.String"]
      }
    ]
  }
]
```

## Оптимизация

### Оптимизация размера

```bash
native-image \
    --no-fallback \
    -H:IncludeResources="application.yml" \
    -H:+RemoveSaturatedTypeFlows \
    -H:+ReportExceptionStackTraces \
    -H:+PrintClassInitialization \
    -H:ClassInitialization=build-time \
    -H:ReflectionConfigurationFiles=reflect-config.json \
    -cp build/libs/my-app-all.jar \
    com.example.Application
```

### Оптимизация производительности

```bash
native-image \
    --no-fallback \
    -O2 \
    -H:+InlineBeforeAnalysis \
    -H:+ReportExceptionStackTraces \
    -cp build/libs/my-app-all.jar \
    com.example.Application
```

## Troubleshooting

### Проблемы с Reflection

Если получаете ошибки связанные с reflection:

1. Проверьте `reflect-config.json`
2. Используйте `@TypeHint` аннотации
3. Проверьте логи компиляции

### Проблемы с Resources

Если ресурсы не находятся:

1. Проверьте `application.yml` конфигурацию
2. Убедитесь, что ресурсы включены в `resources-config.json`
3. Проверьте пути к ресурсам

### Проблемы с Serialization

Если сериализация не работает:

1. Проверьте `serialization-config.json`
2. Убедитесь, что классы доступны для reflection
3. Проверьте настройки Jackson

## Best Practices

### 1. Используйте Compile-time DI

```java
// ✅ Хорошо - compile-time DI
@Singleton
public class UserService {
    private final UserRepository repository;
    
    public UserService(UserRepository repository) {
        this.repository = repository;
    }
}

// ❌ Плохо - runtime reflection
@Singleton
public class UserService {
    @Inject
    private UserRepository repository;
}
```

### 2. Минимизируйте Reflection

```java
// ✅ Хорошо - используйте @TypeHint
@TypeHint(value = {User.class})
public class Application {
    // ...
}

// ❌ Плохо - избегайте динамического reflection
Class<?> clazz = Class.forName("com.example.User");
```

### 3. Используйте AOT оптимизации

```yaml
# ✅ Хорошо
graalvm:
  native-image:
    optimization:
      enabled: true
```

### 4. Тестируйте Native Images

```bash
# ✅ Хорошо - тестируйте native image
./gradlew nativeTest
```

### 5. Мониторьте размер и производительность

```bash
# ✅ Хорошо - отслеживайте размер
ls -lh build/native/nativeCompile/my-app

# ✅ Хорошо - профилируйте производительность
./my-app --profile
```

## AOT (Ahead-of-Time) Оптимизации

### Micronaut AOT Plugin

**build.gradle:**

```gradle
plugins {
    id("io.micronaut.aot") version "4.1.4"
}

micronaut {
    aot {
        version = "1.0.0"
        optimizeServiceLoading = true
        optimizeClassLoading = true
        convertYamlToJava = true
        precomputeOperations = true
        cacheEnvironment = true
        optimizeNetty = true
    }
}
```

### Оптимизация Service Loading

```java
@Configuration
public class ServiceLoadingOptimization {
    // Micronaut автоматически оптимизирует ServiceLoader
    // для native images
}
```

### Оптимизация Class Loading

```yaml
micronaut:
  aot:
    optimize-class-loading: true
```

## Docker и Native Images

### Multi-stage Dockerfile

```dockerfile
# Stage 1: Build
FROM ghcr.io/graalvm/graalvm-ce:java17 AS build
WORKDIR /app
COPY . .
RUN ./gradlew nativeCompile

# Stage 2: Runtime
FROM gcr.io/distroless/cc-debian12
COPY --from=build /app/build/native/nativeCompile/my-app /app/my-app
EXPOSE 8080
ENTRYPOINT ["/app/my-app"]
```

### Docker Compose

```yaml
version: '3.8'
services:
  app:
    build:
      context: .
      dockerfile: Dockerfile
    ports:
      - "8080:8080"
    environment:
      - JAVA_OPTS=-Xmx64m
```

## CI/CD Integration

### GitHub Actions

```yaml
name: Build Native Image

on:
  push:
    branches: [ main ]

jobs:
  build:
    runs-on: ubuntu-latest
    steps:
      - uses: actions/checkout@v3
      - name: Set up GraalVM
        uses: graalvm/setup-graalvm@v1
        with:
          java-version: '17'
          distribution: 'graalvm'
          components: 'native-image'
      - name: Build native image
        run: ./gradlew nativeCompile
      - name: Upload artifact
        uses: actions/upload-artifact@v3
        with:
          name: native-image
          path: build/native/nativeCompile/my-app
```

### GitLab CI

```yaml
build-native:
  image: ghcr.io/graalvm/graalvm-ce:java17
  script:
    - gu install native-image
    - ./gradlew nativeCompile
  artifacts:
    paths:
      - build/native/nativeCompile/my-app
```

## Профилирование Native Images

### Memory Profiling

```bash
native-image \
    --enable-monitoring=heapdump \
    -H:+AllowVMInspection \
    -cp build/libs/my-app-all.jar \
    com.example.Application
```

### CPU Profiling

```bash
native-image \
    --enable-monitoring=jfr \
    -H:+AllowVMInspection \
    -cp build/libs/my-app-all.jar \
    com.example.Application
```

## Оптимизация для Production

### Build-time Initialization

```java
@TypeHint(
    value = {Application.class},
    accessType = TypeHint.AccessType.ALL_PUBLIC,
    initialization = TypeHint.Initialization.BUILD_TIME
)
public class Application {
    // Класс инициализируется во время сборки
}
```

### Runtime Initialization

```java
@TypeHint(
    value = {UserService.class},
    accessType = TypeHint.AccessType.ALL_PUBLIC,
    initialization = TypeHint.Initialization.RUNTIME
)
public class Application {
    // Класс инициализируется во время выполнения
}
```

## Оптимизация размера

### Уменьшение размера Native Image

```bash
native-image \
    --no-fallback \
    -H:+RemoveSaturatedTypeFlows \
    -H:+ReportExceptionStackTraces \
    -H:+PrintClassInitialization \
    -H:ClassInitialization=build-time \
    -H:ReflectionConfigurationFiles=reflect-config.json \
    -H:IncludeResources="application.yml" \
    -H:ExcludeResources="**/*.test.yml" \
    -cp build/libs/my-app-all.jar \
    com.example.Application
```

### Оптимизация для Production

```bash
native-image \
    --no-fallback \
    -O2 \
    -H:+InlineBeforeAnalysis \
    -H:+ReportExceptionStackTraces \
    -H:+RemoveSaturatedTypeFlows \
    -H:+PrintClassInitialization \
    -H:ClassInitialization=build-time \
    -H:ReflectionConfigurationFiles=reflect-config.json \
    -H:IncludeResources="application.yml" \
    -H:ExcludeResources="**/*.test.yml" \
    -H:+ReportUnsupportedElementsAtRuntime \
    -H:+AllowVMInspection \
    -cp build/libs/my-app-all.jar \
    com.example.Application
```

## Мониторинг Native Images

### Memory Profiling

```bash
native-image \
    --enable-monitoring=heapdump \
    -H:+AllowVMInspection \
    -cp build/libs/my-app-all.jar \
    com.example.Application
```

### CPU Profiling

```bash
native-image \
    --enable-monitoring=jfr \
    -H:+AllowVMInspection \
    -cp build/libs/my-app-all.jar \
    com.example.Application
```

## Troubleshooting Native Images

### Common Issues

1. **ClassNotFoundException**: Добавьте класс в reflect-config.json
2. **ResourceNotFoundException**: Включите ресурс в resources-config.json
3. **MethodNotFoundException**: Используйте @TypeHint аннотации
4. **Initialization Errors**: Настройте class initialization

### Debugging Tips

```bash
# Включите verbose output
native-image --verbose ...

# Проверьте reflection configuration
native-image -H:+PrintClassInitialization ...

# Проверьте resource inclusion
native-image -H:+TraceClassInitialization ...
```

## Performance Comparison

### Startup Time

```
JVM:  2-5 seconds
Native: 0.1-0.5 seconds
```

### Memory Usage

```
JVM:  500MB+
Native: 50-100MB
```

### Throughput

```
JVM:  Baseline
Native: Similar or better
```

## Build Optimization

### Incremental Builds

```bash
# Первая сборка
./gradlew nativeCompile

# Инкрементальная сборка (быстрее)
./gradlew nativeCompile
```

### Build Caching

```gradle
// build.gradle
graalvmNative {
    binaries {
        main {
            buildArgs.add('--no-fallback')
            buildArgs.add('--verbose')
        }
    }
}
```

## Runtime Configuration

### Runtime Options

```bash
# Запуск с дополнительными опциями
./my-app --enable-monitoring=heapdump
./my-app -H:+AllowVMInspection
```

### Environment Variables

```bash
# Настройка через переменные окружения
export GRAALVM_NATIVE_IMAGE_OPTS="-H:+ReportExceptionStackTraces"
./my-app
```

## Native Image Build Profiles

### Build Profile Configuration

**application.yml:**

```yaml
micronaut:
  application:
    name: my-app
  aot:
    enabled: true
    package-name: com.example
```

### Build Profile Generation

```bash
# Генерация build profile
./gradlew generateAotClass

# Компиляция native image
./gradlew nativeCompile
```

## Native Image Testing

### Testing Native Images

```java
import io.micronaut.test.extensions.junit5.annotation.MicronautTest;
import org.junit.jupiter.api.Test;

@MicronautTest
public class NativeImageTest {
    
    @Test
    void testNativeImage() {
        // Тесты для native image
    }
}
```

## Заключение

Micronaut предоставляет отличную поддержку GraalVM Native Images благодаря compile-time dependency injection. Правильное использование AOT оптимизаций, Docker интеграции, CI/CD, профилирования, оптимизации размера, мониторинга, troubleshooting, build optimization, runtime configuration, build profiles, native image testing и других продвинутых возможностей позволяет создавать быстрые, легковесные приложения с минимальным временем запуска и потреблением памяти.

## Дополнительные ресурсы

- [GraalVM Native Image Documentation](https://www.graalvm.org/latest/reference-manual/native-image/)
- [Micronaut Native Image Guide](https://docs.micronaut.io/latest/guide/index.html#aot)
- [GraalVM Native Image Build Configuration](https://www.graalvm.org/latest/reference-manual/native-image/overview/BuildConfiguration/)
- [Micronaut AOT](https://micronaut-projects.github.io/micronaut-aot/latest/guide/)
- [GraalVM Performance](https://www.graalvm.org/latest/reference-manual/native-image/guides/optimize-native-executable/)

