---
title: "Основы логирования в Java"
description: "Краткое руководство по основам логирования в Java: уровни, фреймворки (JUL, SLF4J, Logback), конфигурация и базовые паттерны."
tags:
  - monitoring
  - logging
  - logging-basics
difficulty: "intermediate"
prerequisites: []
next: []
updated: "2026-02-11"
---
# Основы логирования в Java

Краткое руководство по основам логирования в Java: уровни, фреймворки (JUL, SLF4J, Logback), конфигурация и базовые паттерны.

## Полезные ссылки

### Официальная документация
- [Java Logging Overview](https://docs.oracle.com/javase/tutorial/essential/logging/)
- [SLF4J](https://www.slf4j.org/)
- [Logback](https://logback.qos.ch/)
- [Log4j](https://logging.apache.org/log4j/2.x/)

### Спецификации
- [JSR 47: Logging](https://jcp.org/en/jsr/detail?id=47)
- [JUL: java.util.logging](https://docs.oracle.com/javase/8/docs/api/java/util/logging/package-summary.html)

### См. также
- [[logback|Logback]] — конфигурация Logback
- [[slf4j|SLF4J]] — SLF4J facade
- [[structured-logging|Структурированное логирование]]
- [[distributed-tracing|Distributed Tracing]]

## Содержание

- [Введение в логирование](#введение-в-логирование)
- [Уровни логирования](#уровни-логирования)
- [Java Util Logging (JUL)](#java-util-logging-jul)
- [SLF4J и Logback](#slf4j-и-logback)
- [Логирование в Spring Boot](#логирование-в-spring-boot)
- [Конфигурация и форматы](#конфигурация-и-форматы)
- [Производительность](#производительность)
- [Лучшие практики](#лучшие-практики)
- [Решение проблем](#решение-проблем)
- [Частые вопросы](#частые-вопросы)

## Введение в логирование

Логирование — запись информации о работе приложения для отладки, мониторинга и аудита. В Java это ключевой инструмент для понимания поведения в production.

### Почему важно логирование

- Отладка — анализ ошибок и состояний
- Мониторинг — состояние приложения
- Аудит — важные бизнес-события
- Безопасность — подозрительная активность

### Основные концепции

- **Logger** — объект, записывающий сообщения в лог
- **Log Level** — уровень важности (TRACE, DEBUG, INFO, WARN, ERROR)
- **Appender/Layout** — куда и в каком формате писать
- **Log Context** — дополнительная информация (например, MDC)

Архитектура: слой API (SLF4J, JUL), реализация (Logback, Log4j, JUL), назначения (консоль, файл, сеть).

## Уровни логирования

### Стандартные уровни

| Уровень | Назначение | Примеры |
|---------|------------|---------|
| TRACE | Детальная отладка | SQL, параметры методов |
| DEBUG | Отладка | Состояние переменных, cache hits |
| INFO | Важные события | Запуск, бизнес-операции |
| WARN | Потенциальные проблемы | Deprecated API, высокая нагрузка |
| ERROR | Критические ошибки | Исключения, сбои |

Примеры:

```java
logger.trace("Entering processOrder with orderId: {}", orderId);
logger.debug("Cache hit ratio: {}%", calculateHitRatio());
logger.info("User {} created order {}", userId, orderId);
logger.warn("Slow query: {} took {}ms", query, duration);
logger.error("Payment failed for order {}", orderId, exception);
```

## Java Util Logging (JUL)

Базовое использование:

```java
import java.util.logging.Logger;
import java.util.logging.Level;

public class JulExample {
    private static final Logger logger = Logger.getLogger(JulExample.class.getName());

    public void basicLogging() {
        logger.fine("Fine message");
        logger.info("Info message");
        logger.warning("Warning message");
        logger.log(Level.SEVERE, "Operation failed", exception);
    }
}
```

Конфигурация через `logging.properties`:

```properties
handlers=java.util.logging.ConsoleHandler
.level=INFO
com.example.level=FINE
java.util.logging.ConsoleHandler.level=ALL
java.util.logging.FileHandler.pattern=%h/java%u.log
java.util.logging.FileHandler.limit=50000
```

Ограничения JUL: ниже производительность, сложнее конфигурация, ограниченные возможности. В современных приложениях рекомендуется SLF4J + Logback.

## SLF4J и Logback

### SLF4J API

```java
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Slf4jExample {
    private static final Logger logger = LoggerFactory.getLogger(Slf4jExample.class);

    public void basicSlf4j() {
        logger.debug("Processing user {} with id {}", username, userId);
        logger.info("Application started on port {}", port);
            logger.error("Failed to process data", e);
    }

    public void conditionalLogging() {
        if (logger.isDebugEnabled()) {
            logger.debug("Expensive: {}", computeExpensiveDebugInfo());
        }
    }
}
```

Иерархия логгеров: root пакет класс; настройки наследуются от родителя.

### Logback: базовый logback.xml

```xml
<?xml version="1.0" encoding="UTF-8"?>
<configuration>
    <appender name="CONSOLE" class="ch.qos.logback.core.ConsoleAppender">
        <encoder>
            <pattern>%d{yyyy-MM-dd HH:mm:ss.SSS} [%thread] %-5level %logger{36} - %msg%n</pattern>
        </encoder>
    </appender>

    <appender name="FILE" class="ch.qos.logback.core.rolling.RollingFileAppender">
        <file>logs/application.log</file>
        <rollingPolicy class="ch.qos.logback.core.rolling.TimeBasedRollingPolicy">
            <fileNamePattern>logs/application.%d{yyyy-MM-dd}.%i.log</fileNamePattern>
            <maxFileSize>10MB</maxFileSize>
            <maxHistory>30</maxHistory>
        </rollingPolicy>
        <encoder>
            <pattern>%d{yyyy-MM-dd HH:mm:ss.SSS} [%thread] %-5level %logger{36} - %msg%n</pattern>
        </encoder>
    </appender>

    <logger name="com.example" level="DEBUG" additivity="false">
        <appender-ref ref="CONSOLE"/>
        <appender-ref ref="FILE"/>
    </logger>
    <logger name="org.springframework" level="INFO"/>
    <root level="INFO">
        <appender-ref ref="CONSOLE"/>
    </root>
</configuration>
```

Асинхронный appender для производительности:

```xml
    <appender name="ASYNC" class="ch.qos.logback.classic.AsyncAppender">
        <discardingThreshold>20</discardingThreshold>
        <queueSize>512</queueSize>
        <appender-ref ref="FILE"/>
    </appender>
```

## Логирование в Spring Boot

Автоконфигурация через `application.yml`:

```yaml
logging:
  level:
    root: INFO
    com.example: DEBUG
    org.springframework: WARN
  pattern:
    console: "%d{yyyy-MM-dd HH:mm:ss.SSS} [%thread] %-5level %logger{36} - %msg%n"
  file:
    name: logs/application.log
    max-size: 10MB
      max-history: 30
```

Профили (dev/prod): в `application-dev.yml` — DEBUG для пакетов приложения; в `application-prod.yml` — INFO, отдельный путь к файлу. Кастомная конфигурация: `logback-spring.xml` с `<springProfile name="dev">` / `prod`.

## Конфигурация и форматы

### Динамическая смена уровня

```java
LoggerContext ctx = (LoggerContext) LoggerFactory.getILoggerFactory();
Logger logger = ctx.getLogger("com.example");
logger.setLevel(Level.DEBUG);
```

В Spring Boot можно задавать уровни через переменные окружения: `LOGGING_LEVEL_ROOT`, `LOGGING_LEVEL_COM_EXAMPLE` и т.д.

### Pattern и MDC

Типичный pattern с MDC:

```xml
<pattern>%d{yyyy-MM-dd HH:mm:ss.SSS} [%thread] %-5level %logger{36} [%X{userId:-}] [%X{requestId:-}] - %msg%n</pattern>
```

Использование MDC:

```java
        MDC.put("userId", "12345");
        MDC.put("requestId", UUID.randomUUID().toString());
        logger.info("User logged in");
// ...
        MDC.clear();
```

Структурированное логирование: JSON-encoder (например, logstash-logback-encoder) с полями timestamp, level, logger, message, mdc, stackTrace.

## Производительность

- Избегать тяжёлых операций в аргументах лога: использовать параметризованные вызовы `logger.debug("Data: {}", expensive())` — SLF4J не вычисляет аргументы, если уровень отключён.
- При необходимости явно: `if (logger.isDebugEnabled()) { logger.debug("...", expensive()); }`
- Не конкатенировать строки в логе: не `"User: " + user`, а `"User: {}", user`.
- Async appender снижает блокировку потока приложения.

## Лучшие практики

1. **Именование логгеров:** по классу — `LoggerFactory.getLogger(MyClass.class)`.
2. **Формат сообщений:** единый стиль, контекст (userId, orderId, requestId).
3. **Исключения:** `logger.error("Message", exception)` — передавать throwable последним аргументом; для ожидаемых исключений — WARN без stack trace при необходимости.
4. **Безопасность:** не логировать пароли, токены, полные номера карт; маскировать чувствительные поля.
5. **Мониторинг:** выделять отдельные логгеры/уровни для security и performance; использовать MDC для корреляции.

## Решение проблем

| Проблема | Причина | Решение |
|----------|---------|---------|
| Логи не появляются | Уровень логгера выше, чем у сообщения; нет appender у root/пакета | Проверить effective level: `((ch.qos.logback.classic.Logger)logger).getEffectiveLevel()`; убедиться, что у root или нужного логгера есть appender |
| Утечки памяти в thread-pool | MDC не очищается после запроса | Всегда вызывать `MDC.clear()` в finally или использовать фильтр/интерцептор, очищающий MDC после обработки запроса |
| Логирование тормозит приложение | Синхронный appender, тяжёлые операции в сообщениях | Включить AsyncAppender; убрать конкатенацию и дорогие вызовы из аргументов лога; проверять уровень перед тяжёлым форматированием |

## Частые вопросы

**Как выбрать уровень логирования для своего сообщения?** ERROR — сбой, требующий внимания; WARN — потенциальная проблема (retry, fallback, deprecated); INFO — важные бизнес-события и смена состояния; DEBUG/TRACE — только для отладки, в prod обычно отключены.

**Почему в production предпочтительнее SLF4J + Logback, а не JUL?** Logback даёт лучшую производительность, гибкую конфигурацию (включая XML), ротацию, фильтры и async appenders; SLF4J предоставляет единый API и параметризованные сообщения без лишних затрат при отключённом уровне.

**Как безопасно логировать запросы с чувствительными данными?** Не помещать пароли, токены и полные платёжные данные в сообщения; использовать маскирование (например, последние 4 цифры карты); убирать чувствительные заголовки (Authorization, Cookie) перед логированием; при необходимости санитизировать пользовательский ввод для защиты от log injection (удаление переводов строк и управляющих символов).
