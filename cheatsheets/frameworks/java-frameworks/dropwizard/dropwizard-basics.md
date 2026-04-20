---
title: "Dropwizard: Основы"
description: "Java фреймворк для создания RESTful веб-приложений с production-ready метриками, логированием и health checks"
tags:
  - dropwizard
  - java
  - rest
  - microservices
  - metrics
  - logging
difficulty: "intermediate"
prerequisites: ["java/java-basics.md", "spring/spring-boot.md"]
next: ["dropwizard-core.md", "dropwizard-jersey.md"]
updated: "2026-02-11"
related: ["spring/spring-boot.md", "micronaut/micronaut-basics.md"]
---

# Dropwizard: Основы

**Дата обновления:** 2026-02-11

## Полезные ссылки

- [Dropwizard — официальный сайт](https://www.dropwizard.io)
- [Dropwizard — документация](https://www.dropwizard.io/en/latest/)
- [Dropwizard — GitHub](https://github.com/dropwizard/dropwizard)
- [Getting Started Guide](https://www.dropwizard.io/en/latest/getting-started.html)

## Содержание

- [Введение в Dropwizard](#введение-в-dropwizard)
  - [Основные особенности](#основные-особенности)
  - [Компоненты Dropwizard](#компоненты-dropwizard)
- [Установка и настройка](#установка-и-настройка)
  - [Maven зависимость](#maven-зависимость)
  - [Gradle зависимость](#gradle-зависимость)
  - [Структура проекта](#структура-проекта)
- [Базовая архитектура](#базовая-архитектура)
  - [Application класс](#application-класс)
  - [Configuration класс](#configuration-класс)
  - [YAML конфигурация](#yaml-конфигурация)
  - [REST Resource](#rest-resource)
  - [Модель данных](#модель-данных)
- [Запуск приложения](#запуск-приложения)
  - [Команда server](#команда-server)
  - [CLI команды](#cli-команды)
- [Health Checks](#health-checks)
  - [Кастомный Health Check](#кастомный-health-check)
  - [Регистрация health check](#регистрация-health-check)
- [Метрики](#метрики)
  - [Счётчики и таймеры](#счётчики-и-таймеры)
  - [Endpoint metrics](#endpoint-metrics)
- [Логирование](#логирование)
  - [Настройка Logger](#настройка-logger)
  - [Конфигурация логирования](#конфигурация-логирования)
- [Работа с БД](#работа-с-бд)
  - [JDBI Integration](#jdbi-integration)
  - [Hibernate Integration](#hibernate-integration)
- [REST API расширенные возможности](#rest-api-расширенные-возможности)
  - [Path и Query параметры](#path-и-query-параметры)
  - [Валидация](#валидация)
  - [Exception Handling](#exception-handling)
- [Фильтры и Interceptors](#фильтры-и-interceptors)
- [Тестирование](#тестирование)
  - [Integration Testing](#integration-testing)
  - [Application Testing](#application-testing)
- [Аутентификация](#аутентификация)
  - [Basic Authentication](#basic-authentication)
  - [JWT Authentication](#jwt-authentication)
- [Развёртывание](#развёртывание)
  - [Docker](#docker)
  - [Kubernetes](#kubernetes)
- [Production Deployment](#production-deployment)
  - [JVM Tuning](#jvm-tuning)
  - [Graceful Shutdown](#graceful-shutdown)
- [Лучшие практики](#лучшие-практики)
- [Решение проблем](#решение-проблем)
- [Заключение](#заключение)

## Введение в Dropwizard

**Dropwizard** — это **Java** фреймворк для создания production-ready **RESTful** веб-приложений. Dropwizard объединяет проверенные Java библиотеки в единый стек для быстрой разработки production-ready сервисов.

### Основные особенности

- **Production-ready**: встроенные метрики, логирование, health checks
- **Lightweight**: минимальный набор зависимостей
- **Jersey**: RESTful веб-сервисы на **JAX-RS**
- **Metrics**: встроенные метрики (Metrics library)
- **Logging**: структурированное логирование через **Logback** и **SLF4J**
- **Configuration**: type-safe конфигурация через **YAML**

### Компоненты Dropwizard

- **Jersey**: RESTful веб-сервисы
- **Jetty**: HTTP сервер
- **Jackson**: JSON сериализация
- **Metrics**: счётчики и метрики
- **Logback**: логирование
- **Hibernate Validator**: валидация


## Установка и настройка

### Maven зависимость

```xml
<dependency>
    <groupId>io.dropwizard</groupId>
    <artifactId>dropwizard-core</artifactId>
    <version>4.0.0</version>
</dependency>
```

### Gradle зависимость

```gradle
dependencies {
    implementation 'io.dropwizard:dropwizard-core:4.0.0'
}
```

### Структура проекта

```text
project/
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── com/example/
│   │   │       ├── HelloWorldApplication.java
│   │   │       ├── HelloWorldConfiguration.java
│   │   │       └── resources/
│   │   │           └── HelloWorldResource.java
│   │   └── resources/
│   │       └── config.yml
└── pom.xml
```


## Базовая архитектура

### Application класс

```java
import io.dropwizard.Application;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;

public class HelloWorldApplication extends Application<HelloWorldConfiguration> {

    public static void main(String[] args) throws Exception {
        new HelloWorldApplication().run(args);
    }

    @Override
    public void initialize(Bootstrap<HelloWorldConfiguration> bootstrap) {
        // Регистрация bundles
    }

    @Override
    public void run(HelloWorldConfiguration configuration, Environment environment) {
        final HelloWorldResource resource = new HelloWorldResource(
            configuration.getTemplate(),
            configuration.getDefaultName()
        );
        environment.jersey().register(resource);
    }
}
```

### Configuration класс

```java
import io.dropwizard.Configuration;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.hibernate.validator.constraints.NotEmpty;

public class HelloWorldConfiguration extends Configuration {

    @NotEmpty
    private String template;

    @NotEmpty
    private String defaultName = "Stranger";

    @JsonProperty
    public String getTemplate() {
        return template;
    }

    @JsonProperty
    public void setTemplate(String template) {
        this.template = template;
    }

    @JsonProperty
    public String getDefaultName() {
        return defaultName;
    }

    @JsonProperty
    public void setDefaultName(String defaultName) {
        this.defaultName = defaultName;
    }
}
```

### YAML конфигурация

```yaml
template: Hello, %s!
defaultName: Dropwizard

server:
  type: simple
  connector:
    type: http
    port: 8080

logging:
  level: INFO
  loggers:
    com.example: DEBUG
```

### REST Resource

```java
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicLong;

@Path("/hello-world")
@Produces(MediaType.APPLICATION_JSON)
public class HelloWorldResource {
    private final String template;
    private final String defaultName;
    private final AtomicLong counter;

    public HelloWorldResource(String template, String defaultName) {
        this.template = template;
        this.defaultName = defaultName;
        this.counter = new AtomicLong();
    }

    @GET
    public Saying sayHello(@QueryParam("name") Optional<String> name) {
        final String value = String.format(template, name.orElse(defaultName));
        return new Saying(counter.incrementAndGet(), value);
    }
}
```

### Модель данных

```java
import com.fasterxml.jackson.annotation.JsonProperty;

public class Saying {
    private long id;
    private String content;

    public Saying() {}

    public Saying(long id, String content) {
        this.id = id;
        this.content = content;
    }

    @JsonProperty
    public long getId() {
        return id;
    }

    @JsonProperty
    public String getContent() {
        return content;
    }
}
```


## Запуск приложения

### Команда server

```bash
java -jar target/hello-world-1.0.0.jar server config.yml
```

### CLI команды

- `server`: запуск сервера
- `check`: проверка конфигурации
- `db migrate`: миграция БД (если подключена)


## Health Checks

### Кастомный Health Check

```java
import com.codahale.metrics.health.HealthCheck;

public class DatabaseHealthCheck extends HealthCheck {
    private final Database database;

    public DatabaseHealthCheck(Database database) {
        this.database = database;
    }

    @Override
    protected Result check() throws Exception {
        if (database.isConnected()) {
            return Result.healthy();
        } else {
            return Result.unhealthy("Cannot connect to database");
        }
    }
}
```

### Регистрация health check

```java
@Override
public void run(HelloWorldConfiguration configuration, Environment environment) {
    final DatabaseHealthCheck healthCheck = new DatabaseHealthCheck(database);
    environment.healthChecks().register("database", healthCheck);
}
```

Проверка health:

```bash
curl http://localhost:8080/healthcheck
```


## Метрики

### Счётчики и таймеры

```java
import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.Counter;
import com.codahale.metrics.Timer;

public class MetricsExample {
    private final Counter requests;
    private final Timer responses;

    public MetricsExample(MetricRegistry metrics) {
        this.requests = metrics.counter("requests");
        this.responses = metrics.timer("responses");
    }

    public void handleRequest() {
        requests.inc();
        Timer.Context context = responses.time();
        try {
            // Обработка запроса
        } finally {
            context.stop();
        }
    }
}
```

### Endpoint metrics

```bash
curl http://localhost:8080/metrics
```


## Логирование

### Настройка Logger

```java
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LoggingExample {
    private static final Logger logger = LoggerFactory.getLogger(LoggingExample.class);

    public void example() {
        logger.info("Info message");
        logger.debug("Debug message");
        logger.warn("Warning message");
        logger.error("Error message", new Exception("Error"));
    }
}
```

### Конфигурация логирования

```yaml
logging:
  level: INFO
  loggers:
    com.example: DEBUG
    org.hibernate: WARN
  appenders:
    - type: console
    - type: file
      currentLogFilename: ./logs/application.log
      archivedLogFilenamePattern: ./logs/application-%d.log.gz
      archivedFileCount: 5
```


## Работа с БД

### JDBI Integration

```java
import org.jdbi.v3.core.Jdbi;
import org.jdbi.v3.jdbi3.JdbiFactory;

public class DatabaseExample extends Application<HelloWorldConfiguration> {

    @Override
    public void run(HelloWorldConfiguration config, Environment env) {
        JdbiFactory factory = new JdbiFactory();
        Jdbi jdbi = factory.build(env, config.getDataSourceFactory(), "postgresql");

        UserDAO userDAO = jdbi.onDemand(UserDAO.class);
        env.jersey().register(new UserResource(userDAO));
    }
}

public interface UserDAO {
    @SqlQuery("SELECT * FROM users WHERE id = :id")
    User findById(@Bind("id") long id);

    @SqlUpdate("INSERT INTO users (name, email) VALUES (:name, :email)")
    void insert(@BindBean User user);
}
```

### Hibernate Integration

```java
import io.dropwizard.hibernate.HibernateBundle;
import org.hibernate.SessionFactory;

public class HibernateExample extends Application<HelloWorldConfiguration> {

    private final HibernateBundle<HelloWorldConfiguration> hibernate =
        new HibernateBundle<HelloWorldConfiguration>(User.class) {
            @Override
            public DataSourceFactory getDataSourceFactory(HelloWorldConfiguration config) {
                return config.getDataSourceFactory();
            }
        };

    @Override
    public void initialize(Bootstrap<HelloWorldConfiguration> bootstrap) {
        bootstrap.addBundle(hibernate);
    }

    @Override
    public void run(HelloWorldConfiguration config, Environment env) {
        SessionFactory sessionFactory = hibernate.getSessionFactory();
        UserDAO userDAO = new UserDAO(sessionFactory);
        env.jersey().register(new UserResource(userDAO));
    }
}
```


## REST API расширенные возможности

### Path и Query параметры

```java
@Path("/api/users")
public class QueryParamsResource {

    @GET
    public List<User> getUsers(
            @QueryParam("page") @DefaultValue("0") int page,
            @QueryParam("size") @DefaultValue("20") int size,
            @QueryParam("sort") @DefaultValue("id") String sort) {
        return getUsersPaginated(page, size, sort);
    }

    @GET
    @Path("/{id}")
    public User getUser(@PathParam("id") long id) {
        return findUserById(id);
    }
}
```

### Валидация

```java
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Email;

public class User {
    @NotNull
    private Long id;
    @NotNull
    @Size(min = 1, max = 100)
    private String name;
    @NotNull
    @Email
    private String email;
}

@Path("/api/users")
public class ValidationResource {

    @POST
    public Response createUser(@Valid User user) {
        return Response.status(201).entity(user).build();
    }
}
```

### Exception Handling

```java
import javax.ws.rs.ext.ExceptionMapper;

public class UserNotFoundExceptionMapper implements ExceptionMapper<UserNotFoundException> {

    @Override
    public Response toResponse(UserNotFoundException exception) {
        ErrorMessage error = new ErrorMessage(404, exception.getMessage());
        return Response.status(404).entity(error).build();
    }
}

// Регистрация:
env.jersey().register(new UserNotFoundExceptionMapper());
```


## Фильтры и Interceptors

```java
import javax.ws.rs.container.ContainerRequestFilter;

@Provider
public class AuthenticationFilter implements ContainerRequestFilter {

    @Override
    public void filter(ContainerRequestContext requestContext) {
        String token = requestContext.getHeaderString("Authorization");
        if (token == null || !isValidToken(token)) {
            requestContext.abortWith(
                Response.status(401).entity("Unauthorized").build());
        }
    }
}
```


## Тестирование

### Integration Testing

```java
import io.dropwizard.testing.junit5.DropwizardExtensionsSupport;
import io.dropwizard.testing.junit5.ResourceExtension;

@ExtendWith(DropwizardExtensionsSupport.class)
public class UserResourceTest {

    private static final ResourceExtension EXT = ResourceExtension.builder()
        .addResource(new UserResource(userDAO))
        .build();

    @Test
    void testGetUser() {
        User user = EXT.target("/users/1").request().get(User.class);
        assertNotNull(user);
        assertEquals(1L, user.getId());
    }
}
```

### Application Testing

```java
import io.dropwizard.testing.junit5.DropwizardAppExtension;

@ExtendWith(DropwizardExtensionsSupport.class)
public class IntegrationTest {

    private static final DropwizardAppExtension<HelloWorldConfiguration> EXT =
        new DropwizardAppExtension<>(HelloWorldApplication.class, "config.yml");

    @Test
    void testApplication() {
        Client client = EXT.client();
        Response response = client.target("http://localhost:" + EXT.getLocalPort() + "/hello-world")
            .request()
            .get();
        assertEquals(200, response.getStatus());
    }
}
```


## Аутентификация

### Basic Authentication

```java
import io.dropwizard.auth.Auth;
import io.dropwizard.auth.basic.BasicCredentialAuthFilter;

@Path("/api")
public class SecureResource {

    @GET
    @Path("/protected")
    public String getProtected(@Auth User user) {
        return "Hello " + user.getName();
    }
}

// В Application:
env.jersey().register(new AuthDynamicFeature(
    new BasicCredentialAuthFilter.Builder<User>()
        .setAuthenticator(new BasicAuthenticator())
        .setAuthorizer(new SimpleAuthorizer())
        .buildAuthFilter()));
```

### JWT Authentication

```java
env.jersey().register(new AuthDynamicFeature(
    new JwtAuthFilter.Builder<User>()
        .setTokenExtractor(new BearerTokenExtractor())
        .setAuthenticator(new JwtAuthenticator())
        .buildAuthFilter()));
```


## Развёртывание

### Docker

```dockerfile
FROM eclipse-temurin:11-jre
COPY target/myapp-*.jar /app/app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "/app/app.jar", "server", "/app/config.yml"]
```

### Kubernetes

```yaml
apiVersion: apps/v1
kind: Deployment
metadata:
  name: dropwizard-app
spec:
  replicas: 3
  template:
    spec:
      containers:
      - name: app
        image: myapp:latest
        ports:
        - containerPort: 8080
        env:
        - name: DB_HOST
          valueFrom:
            configMapKeyRef:
              name: app-config
              key: db-host
```


## Production Deployment

### JVM Tuning

```bash
java -Xms512m -Xmx2g \
     -XX:+UseG1GC \
     -XX:MaxGCPauseMillis=200 \
     -jar myapp.jar server config.yml
```

### Graceful Shutdown

```java
Runtime.getRuntime().addShutdownHook(new Thread(() -> {
    System.out.println("Shutting down...");
    // Cleanup resources
}));
```


## Лучшие практики

1. **Структура проекта**: разделяйте ресурсы, сервисы, DAO и модели по пакетам
2. **Dependency Injection**: используйте Guice или встроенный DI для слабосвязанного кода
3. **Error Handling**: регистрируйте ExceptionMapper для всех кастомных исключений
4. **Resource Management**: используйте `@PostConstruct` и `@PreDestroy` для жизненного цикла
5. **Connection Pool**: настраивайте `minSize` и `maxSize` в DataSourceFactory под нагрузку
6. **Валидация**: применяйте Bean Validation на входных DTO
7. **Метрики**: добавляйте таймеры для критичных операций


## Решение проблем

### Out of Memory

**Симптомы**: `java.lang.OutOfMemoryError: Java heap space`, падение приложения под нагрузкой.

**Решение**:
- Увеличьте heap: `-Xmx2g` или больше в зависимости от нагрузки
- Проанализируйте heap dump: `-XX:+HeapDumpOnOutOfMemoryError -XX:HeapDumpPath=/var/log/heapdump.hprof`
- Проверьте утечки: долгоживущие кеши, незакрытые потоки

### Connection Pool Exhaustion

**Симптомы**: `java.sql.SQLException: Timeout: Pool empty`, запросы зависают при обращении к БД.

**Решение**:
```yaml
database:
  maxSize: 32
  minSize: 8
  maxWaitForConnection: 1s
  validationQuery: "SELECT 1"
  evictionInterval: 10s
```

### Медленные запросы

**Симптомы**: высокий latency, рост метрик `response-time`.

**Решение**:
- Включите логирование SQL: `org.hibernate.SQL: DEBUG`
- Добавьте индексы на frequently queried колонки
- Используйте prepared statements (JDBI делает это по умолчанию)
- Включите connection pooling и мониторинг пула

### Порт занят при запуске

**Симптомы**: `java.net.BindException: Address already in use`.

**Решение**:
- Проверьте, что предыдущий процесс завершён: `lsof -i :8080` (macOS/Linux)
- Используйте другой порт в `config.yml` или переменной окружения
- В тестах используйте `SimpleServerFactory` с `port: 0` для динамического порта

### Jersey exception mapper не срабатывает

**Симптомы**: возвращается стандартный HTML вместо JSON при ошибке.

**Решение**:
- Убедитесь, что mapper зарегистрирован: `environment.jersey().register(new MyExceptionMapper())`
- Проверьте приоритет: `@Priority(1)` для кастомных мапперов
- Для исключений Jetty используйте `ErrorHandler` в `Environment`


## Заключение

**Dropwizard** — зрелый фреймворк для создания production-ready **RESTful** веб-сервисов. Он объединяет проверенные Java библиотеки (Jersey, Jetty, Metrics, Logback) в единый стек с минимальной конфигурацией. Подходит для микросервисов, где важны метрики, health checks и структурированное логирование из коробки.

### Ключевые преимущества

- **Production-ready**: метрики, health checks, логирование по умолчанию
- **Type-safe конфигурация**: YAML POJO с валидацией
- **JAX-RS**: стандартный подход к REST API
- **Легковесность**: без лишних абстракций

### Когда использовать Dropwizard

- Создание RESTful API
- Микросервисная архитектура
- Требования к метрикам и observability
- Production-ready развёртывание

## См. также

- [[javalin-basics|Javalin: Основы]]
- [[micronaut-actuator|Micronaut: Actuator — Health Checks, Metrics и Endpoints]]
- [[micronaut-basics|Micronaut: Основы]]
- [[micronaut-batch|Micronaut: Batch Processing — Job Processing и Scheduling]]
- [[micronaut-cache|Micronaut: Caching — Cache Abstraction и Redis Cache]]
