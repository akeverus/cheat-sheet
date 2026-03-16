---
title: "SLF4J для Java"
description: "Руководство по Simple Logging Facade for Java (SLF4J): фасад для логирования, bridging, MDC, Markers и интеграция с Logback, Log4j, java.util.logging."
tags: ["monitoring", "logging", "slf4j"]
difficulty: "intermediate"
prerequisites: []
next: []
updated: "2026-02-11"
---
# SLF4J для Java

Руководство по Simple Logging Facade for Java (SLF4J): фасад для логирования, bridging, MDC, Markers и интеграция с Logback, Log4j, java.util.logging.

**Дата последнего обновления:** 2026-02-06

## Полезные ссылки

### Официальная документация
- [SLF4J Documentation](https://www.slf4j.org/)
- [SLF4J Manual](https://www.slf4j.org/manual.html)
- [SLF4J GitHub](https://github.com/qos-ch/slf4j)

### QOS.ch
- [SLF4J API](https://www.slf4j.org/apidocs/)
- [SLF4J Cookbook](https://www.slf4j.org/cookbook.html)

### См. также
- [Основы логирования](logging-basics.md)
- [Logback](logback.md) — реализация SLF4J
- [Log4j](log4j.md) — реализация Log4j

## Содержание

- [Введение в SLF4J](#введение-в-slf4j)
- [Logger API](#logger-api)
- [Parameterized logging](#parameterized-logging)
- [MDC (Mapped Diagnostic Context)](#mdc-mapped-diagnostic-context)
- [Markers](#markers)
- [Binding с реализациями](#binding-с-реализациями)
- [Bridging](#bridging)
- [Лучшие практики](#лучшие-практики)
- [Решение проблем](#решение-проблем)
- [Частые вопросы](#частые-вопросы)

## Введение в SLF4J

SLF4J (Simple Logging Facade for Java) — фасад для логирования в Java. Даёт единый API для Logback, Log4j, java.util.logging и позволяет менять реализацию без изменения кода приложения.

Преимущества: единый API (facade), выбор реализации в runtime, bridging для миграции, оптимизированная обработка параметров, MDC и Markers, отсутствие лишних зависимостей.

Архитектура: слой приложения → SLF4J API → binding (logback-classic, log4j-slf4j2-impl, slf4j-jul) → конкретная реализация (Logback, Log4j, JUL).

## Logger API

### Получение Logger

```java
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LoggerExample {
    private static final Logger logger = LoggerFactory.getLogger(LoggerExample.class);
    
    public void demo() {
        logger.info("Message from class logger");
    }
}
```

Рекомендуется объявлять логгер как `private static final` по имени класса. Иерархия логгеров наследует настройки от родителя (например, `com.example.service` наследует от `com.example`).

### Уровни логирования

TRACE, DEBUG, INFO, WARN, ERROR. Для дорогих операций проверяйте уровень перед вызовом или используйте параметризованное логирование (параметры не вычисляются, если уровень отключён).

```java
        if (logger.isDebugEnabled()) {
    logger.debug("Expensive: {}", computeExpensiveData());
}
logger.debug("User details: {}", user.toDetailedString()); // OK — один аргумент
```

## Parameterized logging

Не используйте конкатенацию строк — она выполняется всегда. Используйте плейсхолдеры `{}`.

```java
// Плохо
logger.debug("User " + userId + " action: " + action);

// Хорошо
        logger.debug("User {} performed action: {}", userId, action);
logger.error("DB error for user {}: {}", userId, errorMessage, exception);
```

В Java 8+ можно передавать лямбды для отложенного вычисления: `logger.debug("Data: {}", () -> computeExpensiveData());`

## MDC (Mapped Diagnostic Context)

MDC хранит контекст по потоку (userId, requestId и т.д.) и автоматически попадает в формат логов при настройке реализации (например, `%X{requestId}` в Logback).

```java
import org.slf4j.MDC;

        MDC.put("requestId", UUID.randomUUID().toString());
            MDC.put("userId", "12345");
            logger.info("Processing request");
// ...
MDC.clear(); // обязательно очищать, иначе утечка контекста в пуле потоков
```

В веб-приложениях задавайте MDC в фильтре/интерцепторе в начале запроса и очищайте в `finally` или в `afterCompletion`. Для асинхронных задач копируйте контекст в дочерний поток: `Map<String, String> ctx = MDC.getCopyOfContextMap()` перед запуском и `MDC.setContextMap(ctx)` в начале задачи, затем `MDC.clear()` в `finally`.

## Markers

Маркеры позволяют помечать события (AUDIT, SECURITY, PERFORMANCE) и фильтровать их в конфигурации реализации.

```java
import org.slf4j.Marker;
import org.slf4j.MarkerFactory;

private static final Marker AUDIT = MarkerFactory.getMarker("AUDIT");

logger.info(AUDIT, "User {} performed {}", userId, action);
```

Поддерживается иерархия маркеров (дочерний маркер может быть связан с родительским через `add()`).

## Binding с реализациями

В classpath должна быть ровно одна реализация SLF4J (binding). Типичные зависимости Maven:

```xml
<dependency>
    <groupId>org.slf4j</groupId>
    <artifactId>slf4j-api</artifactId>
    <version>2.0.9</version>
</dependency>
<!-- Один из: -->
<dependency>
    <groupId>ch.qos.logback</groupId>
    <artifactId>logback-classic</artifactId>
    <version>1.4.14</version>
</dependency>
```

Если в classpath несколько binding’ов, SLF4J выводит предупреждение и выбирает один. Уберите лишние (например, slf4j-simple, второй slf4j-log4j12 и т.д.). Проверить текущую реализацию: `LoggerFactory.getILoggerFactory().getClass().getName()`.

## Bridging

Чтобы старые библиотеки, использующие Log4j 1.x, JCL или java.util.logging, писали в SLF4J, добавьте соответствующий мост и исключите оригинальную библиотеку логирования:

- Log4j 1.x → SLF4J: `log4j-over-slf4j` (и убрать `log4j`).
- Commons Logging → SLF4J: `jcl-over-slf4j` (и убрать `commons-logging`).
- JUL → SLF4J: `jul-to-slf4j` + вызов `SLF4JBridgeHandler.install()` при старте.

После этого весь вывод идёт через SLF4J и вашу реализацию (например, Logback).

## Лучшие практики

1. **Объявление логгера:** `private static final Logger logger = LoggerFactory.getLogger(MyClass.class);`
2. **Формат сообщений:** параметризованные сообщения с `{}`, единый стиль и контекст (userId, operation).
3. **MDC:** задавать в начале обработки запроса, очищать в `finally` или try-with-resources; в async — копировать контекст в поток и очищать после выполнения.
4. **Исключения:** передавать последним аргументом: `logger.error("Failed: {}", id, exception)`; не логировать и повторно пробрасывать без необходимости (избегать дублирования в логах).
5. **Безопасность:** не логировать пароли, токены, полные номера карт; маскировать чувствительные заголовки и поля.
6. **Производительность:** не вызывать тяжёлые операции в аргументах без проверки уровня или без лямбды; использовать статический логгер, а не получать его на каждый вызов.

## Решение проблем

| Симптом | Причина | Решение |
|--------|---------|--------|
| Логи не появляются, в логах ничего нет | Нет binding или используется NOPLogger | Добавить ровно один binding (например, logback-classic); проверить `LoggerFactory.getLogger("test").getClass().getName()` — не должен быть NOPLogger |
| В логах предупреждение о нескольких binding’ах | В classpath несколько slf4j-*-impl / logback-classic и т.п. | Оставить один binding, исключить лишние через `<exclusion>` в Maven или убрать JAR |
| Рост памяти, «прилипание» данных к потокам | MDC не очищается в пуле потоков (сервлеты, async) | Всегда вызывать `MDC.clear()` в finally после обработки запроса/задачи; в async восстанавливать контекст в потоке и очищать в finally |

Для отладки: включить внутренний статус реализации (например, для Logback — `status="DEBUG"` в конфиге); проверить иерархию логгеров и уровень корневого логгера.

## Частые вопросы

**Нужно ли в приложении использовать SLF4J или можно только Logback/Log4j?**  
Для приложений предпочтительно писать код против SLF4J API, а в classpath подключать одну реализацию (Logback или Log4j 2). Так проще менять реализацию и подключать библиотеки с разным логированием через мосты.

**Почему после добавления библиотеки логи «пропали» или изменился формат?**  
Скорее всего, в classpath попал второй binding (например, через транзитивную зависимость). Проверьте `mvn dependency:tree`, оставьте один binding и при необходимости исключите лишние артефакты.

**Как передать контекст запроса (requestId, userId) в асинхронные задачи?**  
Перед запуском задачи скопируйте MDC: `Map<String, String> ctx = MDC.getCopyOfContextMap()`. В начале run() дочернего потока вызовите `if (ctx != null) MDC.setContextMap(ctx);` и в `finally` обязательно `MDC.clear()`.
