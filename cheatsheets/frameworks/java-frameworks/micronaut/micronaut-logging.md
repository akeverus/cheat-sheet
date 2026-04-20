---
title: "Micronaut: Logging - Logback, SLF4J и Structured Logging"
description: "Полное руководство по логированию в Micronaut: Logback, SLF4J, structured logging, MDC и best practices"
tags:
  - micronaut
  - logging
  - logback
  - slf4j
  - structured-logging
  - java
  - kotlin
difficulty: "intermediate"
prerequisites: ["micronaut/micronaut-basics.md", "micronaut/micronaut-core.md"]
next: ["micronaut-core.md", "micronaut-actuator.md"]
updated: "2026-02-11"
related: ["micronaut-core.md", "micronaut-actuator.md"]
---

# Micronaut: Logging — Logback, SLF4J и Structured Logging

## Полезные ссылки

[Официальная документация Micronaut](https://docs.micronaut.io/)
[Micronaut GitHub](https://github.com/micronaut-projects/micronaut-core)

## Содержание

- [Micronaut: Logging — Logback, SLF4J и Structured Logging](#micronaut-logging-logback-slf4j-и-structured-logging)
- [Введение](#введение)
  - [Основные возможности](#основные-возможности)
- [Настройка Logging](#настройка-logging)
  - [Зависимости](#зависимости)
  - [Конфигурация](#конфигурация)
- [Basic Logging](#basic-logging)
  - [Logger Usage](#logger-usage)
- [Structured Logging](#structured-logging)
  - [JSON Logging](#json-logging)
  - [Structured Log Messages](#structured-log-messages)
- [MDC (Mapped Diagnostic Context)](#mdc-mapped-diagnostic-context)
  - [MDC Usage](#mdc-usage)
- [Custom Appenders](#custom-appenders)
  - [Custom Appender](#custom-appender)
- [Log Levels](#log-levels)
  - [Dynamic Log Level](#dynamic-log-level)
- [Лучшие практики](#лучшие-практики)
  - [1. Используйте правильные уровни логирования](#1-используйте-правильные-уровни-логирования)
  - [2. Используйте MDC для контекста](#2-используйте-mdc-для-контекста)
  - [3. Избегайте конкатенации строк](#3-избегайте-конкатенации-строк)
- [File Appenders](#file-appenders)
  - [File Logging](#file-logging)
- [Async Appenders](#async-appenders)
  - [Async Logging](#async-logging)
- [Log Filtering](#log-filtering)
  - [Custom Filter](#custom-filter)
  - [Filter Configuration](#filter-configuration)
- [Log Aggregation](#log-aggregation)
  - [Log Aggregation Setup](#log-aggregation-setup)
- [Заключение](#заключение)
- [Дополнительные ресурсы](#дополнительные-ресурсы)

## Введение

**Micronaut** предоставляет отличную поддержку логирования через **SLF4J** и **Logback**. Это позволяет создавать структурированные логи с поддержкой различных **appenders** и форматов.

### Основные возможности

- **SLF4J**: Стандартный интерфейс логирования
- **Logback**: Реализация логирования
- **Structured Logging**: Структурированное логирование
- **MDC**: **Mapped Diagnostic Context**
- **Custom Appenders**: Пользовательские **appenders**

## Настройка Logging

### Зависимости

**build.gradle:**

```gradle
dependencies {
    implementation("ch.qos.logback:logback-classic")
    // Structured logging
    implementation("net.logstash.logback:logstash-logback-encoder")
}
```

### Конфигурация

**logback.xml:**

```xml
<configuration>
    <appender name="STDOUT" class="ch.qos.logback.core.ConsoleAppender">
        <encoder>
            <pattern>%d{HH:mm:ss.SSS} [%thread] %-5level %logger{36} - %msg%n</pattern>
        </encoder>
    </appender>

    <root level="INFO">
        <appender-ref ref="STDOUT" />
    </root>
</configuration>
```

## Basic Logging

### Logger Usage

```java
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import jakarta.inject.Singleton;

@Singleton
public class UserService {
    private static final Logger log = LoggerFactory.getLogger(UserService.class);

    public User createUser(User user) {
        log.info("Creating user: {}", user.getName());
        try {
            User created = userRepository.save(user);
            log.info("User created successfully: {}", created.getId());
            return created;
        } catch (Exception e) {
            log.error("Failed to create user: {}", user.getName(), e);
            throw e;
        }
    }
}
```

## Structured Logging

### JSON Logging

**logback.xml:**

```xml
<configuration>
    <appender name="STDOUT" class="ch.qos.logback.core.ConsoleAppender">
        <encoder class="net.logstash.logback.encoder.LogstashEncoder">
            <customFields>{"service":"my-app"}</customFields>
        </encoder>
    </appender>

    <root level="INFO">
        <appender-ref ref="STDOUT" />
    </root>
</configuration>
```

### Structured Log Messages

```java
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;

@Singleton
public class StructuredLoggingService {
    private static final Logger log = LoggerFactory.getLogger(StructuredLoggingService.class);

    public void processUser(User user) {
        MDC.put("userId", user.getId().toString());
        MDC.put("userName", user.getName());

        log.info("Processing user");

        // Логика обработки

        MDC.clear();
    }
}
```

## MDC (`Mapped Diagnostic Context`)

### MDC Usage

```java
import org.slf4j.MDC;
import jakarta.inject.Singleton;

@Singleton
public class MDCService {

    public void processRequest(String requestId, String userId) {
        MDC.put("requestId", requestId);
        MDC.put("userId", userId);

        try {
            // Обработка запроса
            log.info("Processing request");
        } finally {
            MDC.clear();
        }
    }
}
```

## Custom Appenders

### Custom Appender

```java
import ch.qos.logback.core.AppenderBase;
import ch.qos.logback.classic.spi.ILoggingEvent;

public class CustomAppender extends AppenderBase<ILoggingEvent> {

    @Override
    protected void append(ILoggingEvent event) {
        // Кастомная логика обработки логов
        System.out.println("Custom: " + event.getFormattedMessage());
    }
}
```

## Log Levels

### Dynamic Log Level

**application.yml:**

```yaml
logger:
  levels:
    com.example: DEBUG
    io.micronaut: INFO
    org.hibernate: WARN
```

## Лучшие практики

### 1. Используйте правильные уровни логирования

```java
// ✅ Хорошо
log.debug("Detailed debug information");
log.info("Important business event");
log.warn("Warning condition");
log.error("Error condition", exception);
```

### 2. Используйте MDC для контекста

```java
// ✅ Хорошо
MDC.put("requestId", requestId);
try {
    // Логика
} finally {
    MDC.clear();
}
```

### 3. Избегайте конкатенации строк

```java
// ❌ Плохо
log.info("User: " + user.getName());

// ✅ Хорошо
log.info("User: {}", user.getName());
```

## File Appenders

### File Logging

**logback.xml:**

```xml
<configuration>
    <appender name="FILE" class="ch.qos.logback.core.rolling.RollingFileAppender">
        <file>logs/application.log</file>
        <rollingPolicy class="ch.qos.logback.core.rolling.TimeBasedRollingPolicy">
            <fileNamePattern>logs/application.%d{yyyy-MM-dd}.log</fileNamePattern>
            <maxHistory>30</maxHistory>
        </rollingPolicy>
        <encoder>
            <pattern>%d{HH:mm:ss.SSS} [%thread] %-5level %logger{36} - %msg%n</pattern>
        </encoder>
    </appender>

    <root level="INFO">
        <appender-ref ref="FILE" />
    </root>
</configuration>
```

## Async Appenders

### Async Logging

**logback.xml:**

```xml
<configuration>
    <appender name="ASYNC" class="ch.qos.logback.classic.AsyncAppender">
        <queueSize>512</queueSize>
        <discardingThreshold>0</discardingThreshold>
        <appender-ref ref="STDOUT" />
    </appender>

    <root level="INFO">
        <appender-ref ref="ASYNC" />
    </root>
</configuration>
```

## Log Filtering

### Custom Filter

```java
import ch.qos.logback.core.filter.Filter;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.spi.FilterReply;

public class CustomLogFilter extends Filter<ILoggingEvent> {

    @Override
    public FilterReply decide(ILoggingEvent event) {
        if (event.getMessage().contains("SENSITIVE")) {
            return FilterReply.DENY;
        }
        return FilterReply.ACCEPT;
    }
}
```

### Filter Configuration

**logback.xml:**

```xml
<configuration>
    <appender name="STDOUT" class="ch.qos.logback.core.ConsoleAppender">
        <filter class="com.example.CustomLogFilter"/>
        <encoder>
            <pattern>%d{HH:mm:ss.SSS} [%thread] %-5level %logger{36} - %msg%n</pattern>
        </encoder>
    </appender>
</configuration>
```

## Log Aggregation

### Log Aggregation Setup

**logback.xml:**

```xml
<configuration>
    <appender name="LOGSTASH" class="net.logstash.logback.appender.LogstashTcpSocketAppender">
        <destination>localhost:5000</destination>
        <encoder class="net.logstash.logback.encoder.LogstashEncoder"/>
    </appender>

    <root level="INFO">
        <appender-ref ref="LOGSTASH" />
    </root>
</configuration>
```


## Заключение

**Micronaut Logging** предоставляет мощные инструменты для логирования. Поддержка **SLF4J**, **Logback**, **structured logging**, **MDC**, **custom appenders**, **dynamic log levels**, **file appenders**, **async appenders**, **log filtering**, **aggregation** и других продвинутых возможностей позволяет создавать эффективные системы логирования.

## Дополнительные ресурсы

- [**SLF4J** Documentation](https://www.slf4j.org/documentation.html)
- [Logback Documentation](https://logback.qos.ch/documentation.html)
- [Logstash **Logback** Encoder](https://github.com/logfellow/logstash-logback-encoder)
- [**ELK Stack**](https://www.elastic.co/elastic-stack)

## См. также

- [[micronaut-actuator|Micronaut: Actuator — Health Checks, Metrics и Endpoints]]
- [[micronaut-basics|Micronaut: Основы]]
- [[micronaut-batch|Micronaut: Batch Processing — Job Processing и Scheduling]]
- [[micronaut-cache|Micronaut: Caching — Cache Abstraction и Redis Cache]]
- [[micronaut-cloud|Micronaut: Cloud Native — Service Discovery, Configuration и Distributed Tracing]]
