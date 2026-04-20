---
title: "Log4j для Java"
description: "Руководство по Apache Log4j 2: конфигурация (XML, JSON, YAML), appenders, фильтры, ThreadContext, асинхронное логирование и безопасность."
tags:
  - monitoring
  - logging
  - log4j
difficulty: "intermediate"
prerequisites: []
next: []
updated: "2026-02-11"
---
# Log4j для Java

Руководство по Apache Log4j 2: конфигурация (XML, JSON, YAML), appenders, фильтры, ThreadContext, асинхронное логирование и безопасность.

## Полезные ссылки

### Официальная документация
- [Log4j 2 Documentation](https://logging.apache.org/log4j/2.x/)
- [Log4j 2 Manual](https://logging.apache.org/log4j/2.x/manual/)
- [Log4j 2 GitHub](https://github.com/apache/logging-log4j2)

### См. также
- [[logging-basics|Основы логирования]]
- [[logback|Logback]]
- [[slf4j|SLF4J]]

## Содержание

- [Введение в Log4j](#введение-в-log4j)
- [Архитектура](#архитектура-log4j)
- [Configuration](#configuration)
- [Appenders](#appenders)
- [Layouts и паттерны](#layouts)
- [Filters](#filters)
- [ThreadContext](#context-и-thread-context)
- [Async logging](#async-logging)
- [Security](#security)
- [Миграция с Log4j 1.x](#migration-from-log4j-1x)
- [Лучшие практики](#лучшие-практики)
- [Решение проблем](#решение-проблем)
- [Частые вопросы](#частые-вопросы)

## Введение в Log4j

Apache Log4j 2 — переработанный фреймворк логирования с высокой производительностью, гибкой конфигурацией (XML, JSON, YAML, Properties), plugin-архитектурой и встроенной защитой. Подходит для enterprise и облачных приложений.

## Архитектура Log4j

Основные компоненты: Logger (иерархия по имени), Appender (назначение вывода), Layout (формат), Filter. LoggerContext управляет конфигурацией и логгерами.

```java
Logger logger = LogManager.getLogger(MyClass.class);
```

## Configuration

Базовый пример XML (log4j2.xml в classpath):

```xml
<?xml version="1.0" encoding="UTF-8"?>
<Configuration status="WARN">
    <Properties>
        <Property name="LOG_HOME">logs</Property>
        <Property name="PATTERN">%d{yyyy-MM-dd HH:mm:ss.SSS} [%t] %-5level %logger{36} - %msg%n</Property>
    </Properties>
    <Appenders>
        <Console name="Console" target="SYSTEM_OUT">
            <PatternLayout pattern="${PATTERN}"/>
        </Console>
        <RollingFile name="RollingFile" fileName="${LOG_HOME}/app.log"
                     filePattern="${LOG_HOME}/app-%d{yyyy-MM-dd}-%i.log.gz">
            <PatternLayout pattern="${PATTERN}"/>
            <Policies>
                <SizeBasedTriggeringPolicy size="10MB"/>
                <TimeBasedTriggeringPolicy/>
            </Policies>
            <DefaultRolloverStrategy max="30"/>
        </RollingFile>
    </Appenders>
    <Loggers>
        <Logger name="com.example" level="DEBUG" additivity="false">
            <AppenderRef ref="Console"/>
            <AppenderRef ref="RollingFile"/>
        </Logger>
        <Root level="INFO">
            <AppenderRef ref="Console"/>
        </Root>
    </Loggers>
</Configuration>
```

Поддерживаются также JSON и YAML. Конфигурацию можно задавать программно через `ConfigurationBuilder` и `Configurator.initialize()`.

## Appenders

Типичные appenders: Console, File, RollingFile (по размеру/времени), Socket, Http, JDBC (в БД), Routing (по ThreadContext). RollingFile настраивается через Policies (SizeBasedTriggeringPolicy, TimeBasedTriggeringPolicy) и DefaultRolloverStrategy.

Кастомный appender: класс с аннотацией `@Plugin`, наследование от `AbstractAppender`, реализация `append(LogEvent)`.

## Layouts

PatternLayout — основной формат. Краткая справка по паттернам:

| Паттерн | Описание |
|--------|----------|
| `%d{pattern}` | Дата/время |
| `%t` | Имя потока |
| `%-5level` | Уровень (выравнивание) |
| `%logger{length}` | Имя логгера |
| `%msg` | Сообщение |
| `%n` | Перевод строки |
| `%X{key}` | Значение из ThreadContext |

Доступны JsonLayout, XmlLayout и кастомные Layout через `@Plugin`.

## Filters

ThresholdFilter — по минимальному уровню. LevelRangeFilter — диапазон уровней. RegexFilter — по тексту сообщения. Результаты: ACCEPT, DENY, NEUTRAL. Кастомный фильтр — класс с `@Plugin`, наследник AbstractFilter.

## Context и Thread Context

ThreadContext — аналог MDC в SLF4J: ключ-значение по потоку, доступ в паттерне как `%X{userId}`.

```java
import org.apache.logging.log4j.ThreadContext;

        ThreadContext.put("requestId", UUID.randomUUID().toString());
ThreadContext.put("userId", "12345");
logger.info("Processing request");
// ...
ThreadContext.clearAll(); // обязательно в finally или при завершении обработки
```

В асинхронных сценариях контекст нужно копировать в дочерний поток (например, через TaskDecorator) и очищать после выполнения.

## Async logging

AsyncRoot / AsyncLogger — асинхронная запись логов без блокировки потока приложения. AsyncAppender оборачивает другой appender в очередь. Для максимальной производительности используется LMAX Disruptor (настраивается в конфиге). Рекомендуется для высоконагруженных приложений.

## Security

Защита от log injection: санитизация ввода (удаление/экранирование `\r\n`, при необходимости `%`). Не логировать пароли, токены, полные номера карт; маскировать чувствительные заголовки (authorization, cookie). Для аудита — отдельный логгер и appender с ограниченным доступом на запись.

## Migration from Log4j 1.x

Подключить log4j-api и log4j-core 2.x. Для старого кода без изменений — log4j-1.2-api (bridge). Импорты меняются с `org.apache.log4j.Logger` на `org.apache.logging.log4j.LogManager` / `org.apache.logging.log4j.Logger`. Конфигурация переписывается с log4j.properties на log4j2.xml (см. официальный manual по миграции).

## Лучшие практики

1. **Конфигурация:** разная для dev/prod (уровни, appenders); проверка при старте приложения (наличие нужных логгеров и appenders).
2. **Производительность:** использовать AsyncLogger/AsyncAppender; параметризованные сообщения; не вызывать тяжёлые операции в аргументах без проверки уровня.
3. **Безопасность:** не логировать чувствительные данные; санитизация и маскирование.
4. **ThreadContext:** всегда очищать в finally или в декораторе задач, чтобы не «заражать» потоки из пула.

## Решение проблем

| Симптом | Причина | Решение |
|--------|---------|--------|
| Конфигурация не подхватывается, логи по умолчанию | log4j2.xml не в classpath или неверное имя/формат | Проверить имя файла (log4j2.xml), расположение (корень classpath); вывести `LoggerContext.getConfiguration().getConfigurationSource()` для отладки |
| Несколько SLF4J binding’ов, предупреждения в логах | В classpath и log4j-slf4j2-impl, и logback-classic (или другой binding) | Оставить один binding; исключить лишние зависимости в Maven |
| Рост памяти, «прилипание» контекста к потокам | ThreadContext не очищается в пуле потоков | Вызывать ThreadContext.clearAll() в finally после обработки запроса/задачи; в async — копировать контекст в поток и очищать в finally |

Для отладки конфигурации: `status="DEBUG"` в корне `<Configuration>`, либо программный обход `Configuration.getAppenders()` и `getLoggers()`.

## Частые вопросы

**Когда выбрать Log4j 2, а не Logback?**
Оба подходят для enterprise. Log4j 2 даёт больше форматов конфигурации (JSON, YAML), встроенную защиту от уязвимостей и облачные appenders; Logback проще интегрируется с Spring Boot «из коробки». Выбор часто делают по экосистеме (Spring Boot Logback) или по требованию к формату конфигурации.

**Как включить асинхронное логирование?**
Использовать `<AsyncRoot>` или `<AsyncLogger>` вместо `<Root>` / `<Logger>` и при необходимости настроить LMAX Disruptor (см. manual). Либо обернуть нужный appender в `<Async>` (AsyncAppender).

**Почему после обновления библиотеки логи пишутся не туда?**
Проверьте, не подтянулась ли другая реализация логирования (например, другой SLF4J binding или старый log4j 1.x). Приведите к одной реализации (только Log4j 2 или только Logback) и при необходимости используйте мосты (например, log4j-over-slf4j для библиотек на Log4j 1.x).
