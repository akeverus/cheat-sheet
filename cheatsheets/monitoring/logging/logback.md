---
title: "Logback для Java"
description: "Руководство по Logback: реализация SLF4J с конфигурацией (XML, Spring), appenders, фильтрами, MDC, Markers и настройкой под production."
tags:
  - monitoring
  - logging
  - logback
difficulty: "intermediate"
prerequisites: []
next: []
updated: "2026-02-11"
---
# Logback для Java

Руководство по Logback: реализация SLF4J с конфигурацией (XML, Spring), appenders, фильтрами, MDC, Markers и настройкой под production.

## Полезные ссылки

### Официальная документация
- [Logback Documentation](https://logback.qos.ch/)
- [Logback Manual](https://logback.qos.ch/manual/)
- [Logback GitHub](https://github.com/qos-ch/logback)

### См. также
- [Основы логирования](logging-basics.md)
- [SLF4J](slf4j.md)
- [Структурированное логирование](structured-logging.md)

## Содержание

- [Введение в Logback](#введение-в-logback)
- [Архитектура](#архитектура)
- [Configuration](#configuration)
- [Appenders](#appenders)
- [Encoders и Layouts](#encoders-и-layouts)
- [Filters](#filters)
- [MDC и Markers](#mdc-и-markers)
- [Переменные и условия](#variable-substitution)
- [JMX](#jmx-configuration)
- [Производительность](#performance-tuning)
- [Лучшие практики](#лучшие-практики)
- [Решение проблем](#решение-проблем)
- [Частые вопросы](#частые-вопросы)

## Введение в Logback

Logback — современная реализация SLF4J, преемник Log4j. Высокая производительность, конфигурация через XML или Groovy, MDC, Markers, фильтры, TurboFilters. По умолчанию используется в Spring Boot.

## Архитектура Logback

Компоненты: Logger (иерархия по имени), Appender (вывод), Encoder (форматирование; в Logback обычно Encoder вместо отдельного Layout для файла/консоли). LoggerContext управляет логгерами и конфигурацией.

```java
Logger logger = LoggerFactory.getLogger(MyClass.class);
```

## Configuration

Базовый logback.xml в classpath:

```xml
<?xml version="1.0" encoding="UTF-8"?>
<configuration>
    <property name="LOG_HOME" value="/app/logs"/>
    <property name="LOG_PATTERN" value="%d{yyyy-MM-dd HH:mm:ss.SSS} [%thread] %-5level %logger{36} - %msg%n"/>
    
    <appender name="CONSOLE" class="ch.qos.logback.core.ConsoleAppender">
        <encoder><pattern>${LOG_PATTERN}</pattern></encoder>
    </appender>
    
    <appender name="FILE" class="ch.qos.logback.core.rolling.RollingFileAppender">
        <file>${LOG_HOME}/application.log</file>
        <rollingPolicy class="ch.qos.logback.core.rolling.TimeBasedRollingPolicy">
            <fileNamePattern>${LOG_HOME}/application.%d{yyyy-MM-dd}.%i.log</fileNamePattern>
            <maxFileSize>10MB</maxFileSize>
            <maxHistory>30</maxHistory>
        </rollingPolicy>
        <encoder><pattern>${LOG_PATTERN}</pattern></encoder>
    </appender>
    
    <logger name="com.example" level="DEBUG" additivity="false">
        <appender-ref ref="CONSOLE"/>
        <appender-ref ref="FILE"/>
    </logger>
    <root level="INFO">
        <appender-ref ref="CONSOLE"/>
    </root>
</configuration>
```

Конфигурацию можно задавать программно: получить LoggerContext, сбросить конфиг, создать и запустить Appender и Encoder, привязать к root/логгерам.

### Spring Boot

В application.yml задаются уровни и путь к файлу; при необходимости используется logback-spring.xml (поддержка `<springProfile>`, переменных Spring). Имя файла для Spring — logback-spring.xml в classpath.

## Appenders

ConsoleAppender — вывод в консоль. FileAppender — запись в один файл. RollingFileAppender — ротация по времени (TimeBasedRollingPolicy) или по размеру и времени (SizeAndTimeBasedRollingPolicy). AsyncAppender — буферизация и асинхронная запись в другой appender (рекомендуется для снижения нагрузки на поток приложения).

Кастомный appender: наследование от `AppenderBase<ILoggingEvent>`, реализация `append(ILoggingEvent)`.

## Encoders и Layouts

PatternLayoutEncoder — основной способ задать формат. Справка по паттернам:

| Паттерн | Описание | Пример |
|--------|----------|--------|
| `%d{pattern}` | Дата/время | 2023-12-01 10:30:45.123 |
| `%thread` | Имя потока | main |
| `%-5level` | Уровень | INFO |
| `%logger{length}` | Имя логгера | com.example.MyClass |
| `%msg` | Сообщение | User logged in |
| `%n` | Перевод строки | |
| `%X{key}` | Значение MDC | userId=123 |

В pattern можно включать MDC: `[%X{userId:-}]` (дефолт после `:-`). Кастомный layout — наследник LayoutBase<ILoggingEvent>, метод doLayout().

## Filters

ThresholdFilter — минимальный уровень. LevelFilter — точный уровень с onMatch/onMismatch (ACCEPT/DENY/NEUTRAL). EvaluatorFilter — по выражению. TurboFilter выполняется до создания события (удобно для быстрой отсечки по уровню или имени логгера). Кастомный фильтр — реализация Filter<ILoggingEvent>, возврат FilterReply.

## MDC и Markers

MDC — контекст по потоку; в pattern выводится как `%X{key}`. Обязательно очищать MDC в конце обработки запроса/задачи (в пуле потоков), иначе контекст «прилипнет» к потоку.

```java
        MDC.put("requestId", UUID.randomUUID().toString());
logger.info("Processing request");
// ...
        MDC.clear();
```

Markers — метки для категоризации (AUDIT, SECURITY). Использование: `logger.info(MarkerFactory.getMarker("AUDIT"), "Event: {}", data)`. Фильтрация по маркерам — в кастомном Filter или TurboFilter.

## Variable substitution

В конфиге можно использовать свойства: `${name}` или `${name:-default}`. Источники: `<property>`, системные свойства, переменные окружения. Для кастомного источника — реализация PropertyDefiner. Условия: `<if condition='...'>` (требуется Janino), `<springProfile name="dev">` в logback-spring.xml.

## JMX configuration

Элемент `<jmxConfigurator/>` регистрирует MBean для смены уровней и просмотра конфигурации в runtime. Подробности — в Logback Manual.

## Performance tuning

Использовать AsyncAppender для файловых appenders; при необходимости увеличить queueSize, уменьшить discardingThreshold. Не включать includeCallerData без необходимости (дорого). Упрощать pattern и не использовать тяжёлые преобразования в pattern. TurboFilter позволяет отбрасывать события до создания полного LoggingEvent.

## Лучшие практики

1. **Конфигурация:** отдельные настройки для dev/prod (уровни, appenders); проверка при старте (наличие нужных логгеров и appenders, права на запись в файлы).
2. **Производительность:** AsyncAppender для файла; параметризованное логирование; не вызывать тяжёлые операции в аргументах без проверки уровня.
3. **Безопасность:** не логировать пароли, токены, полные номера карт; маскировать чувствительные заголовки; для аудита — отдельный логгер и appender.
4. **MDC:** задавать в начале запроса, очищать в finally; в async — копировать контекст в поток и очищать после выполнения.

## Решение проблем

| Симптом | Причина | Решение |
|--------|---------|--------|
| logback.xml не подхватывается, логи по умолчанию | Файл не в classpath или имя не logback.xml / logback-spring.xml | Положить конфиг в корень classpath; проверить StatusManager контекста (status="DEBUG" в configuration) на ошибки разбора |
| Рост памяти, утечки в пуле потоков | MDC не очищается после обработки запроса/задачи | Вызывать MDC.clear() в finally в фильтре/интерцепторе или в конце async-задачи; в async копировать контекст в поток и очищать в finally |
| Логи не пишутся в файл, ошибки при старте appender | Нет прав на запись, диск заполнен, неверный путь | Проверить права и место записи; смотреть StatusManager и логи при старте контекста; при необходимости fallback на консоль |

Для отладки: атрибут `debug="true"` в `<configuration>`, либо программный обход LoggerContext (getLoggerList(), статусы).

## Частые вопросы

**Почему в Spring Boot используется Logback, а не Log4j 2?**  
Spring Boot по умолчанию подключает spring-boot-starter-logging, который тянет Logback как реализацию SLF4J. Это упрощает конфигурацию и интеграцию (logback-spring.xml, springProfile). При необходимости можно переключиться на Log4j 2 через spring-boot-starter-log4j2 и отключить starter-logging.

**Как включить разные уровни для dev и prod без двух конфигов?**  
Использовать logback-spring.xml и `<springProfile name="dev">` / `<springProfile name="prod">` с разными уровнями и appenders. Либо задавать уровень через переменную/системное свойство и подставлять в `<root level="${LOG_LEVEL:-INFO}">`.

**Логи в файл пишутся с задержкой или теряются при остановке приложения?**  
При использовании AsyncAppender при shutdown очередь может не успеть сброситься. Можно уменьшить queueSize или не использовать neverBlock; для критичных логов рассмотреть синхронный appender или отдельный канал. Настройка maxFlushTime и корректное завершение приложения (flush при остановке) снижают риск потери.
