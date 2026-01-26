---
title: "Scala Logging"
description: "Полное руководство по логированию в Scala: SLF4J, Logback, структурированное логирование, MDC"
tags: ["scala", "logging", "slf4j", "logback", "mdc"]
difficulty: "intermediate"
prerequisites: ["scala/scala-basics.md"]
next: []
updated: "2025-01-16"
related: ["scala/scala-basics.md"]
---

# Scala Logging

Кратко: полное руководство по логированию в Scala: SLF4J, Logback, структурированное логирование, MDC.

**Дата последнего обновления:** 2025-01-16

## Полезные ссылки

### Официальная документация
- [SLF4J Documentation](http://www.slf4j.org/)
- [Logback Documentation](http://logback.qos.ch/)

### См. также
- `./scala-basics.md` - основы Scala

## Содержание

- [Введение в логирование](#введение-в-логирование)
- [SLF4J](#slf4j)
- [Logback](#logback)
- [Структурированное логирование](#структурированное-логирование)
- [MDC (Mapped Diagnostic Context)](#mdc-mapped-diagnostic-context)
- [Лучшие практики](#лучшие-практики)

## Введение в логирование

Логирование является важной частью разработки приложений. Scala использует стандартные Java библиотеки логирования, такие как SLF4J и Logback.

### Основные библиотеки

- **SLF4J**: фасад для логирования, абстрагирует от конкретной реализации
- **Logback**: популярная реализация SLF4J
- **Log4j2**: альтернативная реализация

## SLF4J

SLF4J предоставляет простой API для логирования:

```scala
import org.slf4j.LoggerFactory

class MyService {
  private val logger = LoggerFactory.getLogger(getClass)
  
  def processData(data: String): Unit = {
    logger.debug(s"Processing data: $data")
    logger.info(s"Data processed successfully")
    logger.warn(s"Warning: data is empty")
    logger.error(s"Error processing data", exception)
  }
}
```

SLF4J обеспечивает единый интерфейс для логирования, независимо от конкретной реализации.

## Logback

Logback - это реализация SLF4J с мощными возможностями:

```scala
import ch.qos.logback.classic.LoggerContext
import ch.qos.logback.classic.Level

// Настройка уровня логирования программно
val loggerContext = LoggerFactory.getILoggerFactory.asInstanceOf[LoggerContext]
val rootLogger = loggerContext.getLogger(org.slf4j.Logger.ROOT_LOGGER_NAME)
rootLogger.setLevel(Level.DEBUG)
```

Logback предоставляет гибкую конфигурацию через XML или программно.

## Структурированное логирование

Структурированное логирование использует JSON или другие структурированные форматы:

```scala
import net.logstash.logback.marker.Markers

logger.info(
  Markers.append("userId", 123L)
    .and(Markers.append("action", "login")),
  "User logged in"
)
```

Структурированное логирование упрощает анализ и обработку логов.

## MDC (Mapped Diagnostic Context)

MDC позволяет добавлять контекстную информацию к логам:

```scala
import org.slf4j.MDC

// Установка контекста
MDC.put("requestId", "12345")
MDC.put("userId", "67890")

logger.info("Processing request")

// Очистка контекста
MDC.clear()
```

MDC позволяет отслеживать контекст выполнения через все уровни приложения.

### Scala Logging библиотека

Scala Logging предоставляет удобный wrapper над SLF4J:

```scala
import com.typesafe.scalalogging.Logger
import com.typesafe.scalalogging.LazyLogging

// Использование LazyLogging trait
class MyService extends LazyLogging {
  def processData(data: String): Unit = {
    logger.debug(s"Processing data: $data")
    logger.info(s"Data processed successfully")
    logger.warn(s"Warning: data is empty")
    logger.error(s"Error processing data", exception)
  }
}

// Или создание Logger напрямую
class MyService2 {
  private val logger = Logger[MyService2]
  
  def processData(data: String): Unit = {
    logger.info(s"Processing: $data")
  }
}
```

Scala Logging предоставляет более удобный синтаксис и автоматическое форматирование.

### Конфигурация Logback

Logback конфигурируется через `logback.xml`:

```xml
<configuration>
  <appender name="STDOUT" class="ch.qos.logback.core.ConsoleAppender">
    <encoder>
      <pattern>%d{HH:mm:ss.SSS} [%thread] %-5level %logger{36} - %msg%n</pattern>
    </encoder>
  </appender>

  <appender name="FILE" class="ch.qos.logback.core.rolling.RollingFileAppender">
    <file>logs/application.log</file>
    <rollingPolicy class="ch.qos.logback.core.rolling.TimeBasedRollingPolicy">
      <fileNamePattern>logs/application.%d{yyyy-MM-dd}.log</fileNamePattern>
      <maxHistory>30</maxHistory>
    </rollingPolicy>
    <encoder>
      <pattern>%d{yyyy-MM-dd HH:mm:ss.SSS} [%thread] %-5level %logger{36} - %msg%n</pattern>
    </encoder>
  </appender>

  <root level="INFO">
    <appender-ref ref="STDOUT" />
    <appender-ref ref="FILE" />
  </root>

  <logger name="com.myapp" level="DEBUG" />
</configuration>
```

### Уровни логирования

```scala
import org.slf4j.LoggerFactory
import ch.qos.logback.classic.Level

val logger = LoggerFactory.getLogger(getClass).asInstanceOf[ch.qos.logback.classic.Logger]

// Установка уровня логирования
logger.setLevel(Level.DEBUG)

// Проверка уровня
if (logger.isDebugEnabled) {
  logger.debug("Debug message")
}

// Условное логирование
logger.debug("Expensive computation: {}", expensiveOperation())
```

### Структурированное логирование - расширенное использование

```scala
import net.logstash.logback.marker.Markers
import org.slf4j.LoggerFactory

val logger = LoggerFactory.getLogger(getClass)

// Логирование с множественными полями
logger.info(
  Markers.append("userId", 123L)
    .and(Markers.append("action", "login"))
    .and(Markers.append("ip", "192.168.1.1"))
    .and(Markers.append("timestamp", System.currentTimeMillis())),
  "User logged in"
)

// Логирование с объектом
case class UserEvent(userId: Long, action: String, timestamp: Long)
val event = UserEvent(123L, "login", System.currentTimeMillis())

logger.info(
  Markers.appendEntries(event),
  "User event occurred"
)
```

### MDC - расширенное использование

MDC позволяет добавлять контекстную информацию, которая автоматически включается в логи:

```scala
import org.slf4j.LoggerFactory
import org.slf4j.MDC

val logger = LoggerFactory.getLogger(getClass)

// Установка контекста для запроса
def processRequest(requestId: String, userId: String): Unit = {
  MDC.put("requestId", requestId)
  MDC.put("userId", userId)
  
  try {
    logger.info("Processing request")
    // обработка запроса
    logger.info("Request processed successfully")
  } finally {
    MDC.clear()  // очистка контекста
  }
}

// Контекст автоматически включается в логи
// [requestId=12345] [userId=67890] Processing request
```

### Асинхронное логирование

Logback поддерживает асинхронное логирование для улучшения производительности:

```xml
<configuration>
  <appender name="ASYNC" class="ch.qos.logback.classic.AsyncAppender">
    <queueSize>512</queueSize>
    <discardingThreshold>0</discardingThreshold>
    <appender-ref ref="FILE" />
  </appender>

  <root level="INFO">
    <appender-ref ref="ASYNC" />
  </root>
</configuration>
```

Асинхронное логирование уменьшает влияние логирования на производительность приложения.

### Логирование исключений

```scala
import org.slf4j.LoggerFactory

val logger = LoggerFactory.getLogger(getClass)

// Логирование исключения с сообщением
try {
  riskyOperation()
} catch {
  case e: Exception =>
    logger.error("Error during risky operation", e)
}

// Логирование исключения с контекстом
try {
  processUser(userId)
} catch {
  case e: UserNotFoundException =>
    logger.warn(s"User not found: $userId", e)
  case e: Exception =>
    logger.error(s"Unexpected error processing user $userId", e)
}
```

### Производительность логирования

Для улучшения производительности используйте ленивую оценку:

```scala
import org.slf4j.LoggerFactory

val logger = LoggerFactory.getLogger(getClass)

// Плохо - строка создается всегда
logger.debug(s"Expensive computation: ${expensiveOperation()}")

// Хорошо - строка создается только если уровень DEBUG включен
if (logger.isDebugEnabled) {
  logger.debug(s"Expensive computation: ${expensiveOperation()}")
}

// Или с использованием ленивой оценки
logger.debug("Expensive computation: {}", () => expensiveOperation())
```

### Кастомные Appenders

Можно создавать кастомные appenders для специальных целей:

```scala
import ch.qos.logback.core.AppenderBase
import ch.qos.logback.classic.spi.ILoggingEvent

class CustomAppender extends AppenderBase[ILoggingEvent] {
  override def append(event: ILoggingEvent): Unit = {
    // кастомная логика обработки логов
    val message = event.getFormattedMessage
    val level = event.getLevel
    // отправка в кастомную систему
    sendToCustomSystem(level, message)
  }
  
  def sendToCustomSystem(level: ch.qos.logback.classic.Level, message: String): Unit = {
    // реализация
  }
}
```

### Логирование в Akka

Akka предоставляет свой logger:

```scala
import akka.event.Logging
import akka.actor.{Actor, ActorSystem}

class MyActor extends Actor {
  val log = Logging(context.system, this)
  
  def receive = {
    case msg =>
      log.info(s"Received message: $msg")
      // обработка сообщения
  }
}
```

### Логирование в Play Framework

Play Framework интегрирован с логированием:

```scala
import play.api.Logger

class MyController {
  private val logger = Logger(this.getClass)
  
  def action = Action { request =>
    logger.info(s"Processing request: ${request.path}")
    Ok("Response")
  }
}
```

### Практический пример: Структурированное логирование для мониторинга

```scala
import net.logstash.logback.marker.Markers
import org.slf4j.LoggerFactory

class MonitoringService {
  private val logger = LoggerFactory.getLogger(getClass)
  
  def logMetrics(operation: String, duration: Long, success: Boolean): Unit = {
    logger.info(
      Markers.append("operation", operation)
        .and(Markers.append("duration", duration))
        .and(Markers.append("success", success))
        .and(Markers.append("timestamp", System.currentTimeMillis())),
      s"Operation $operation completed"
    )
  }
  
  def logError(operation: String, error: Throwable, context: Map[String, String]): Unit = {
    val markers = context.foldLeft(Markers.append("operation", operation)) { (acc, (k, v)) =>
      acc.and(Markers.append(k, v))
    }
    logger.error(markers, s"Error in operation $operation", error)
  }
}
```

## Лучшие практики

### Использование правильных уровней логирования

```scala
// DEBUG - детальная информация для отладки
logger.debug(s"Variable value: $value")

// INFO - общая информация о работе приложения
logger.info(s"User ${userId} logged in")

// WARN - предупреждения о потенциальных проблемах
logger.warn(s"Rate limit approaching for user ${userId}")

// ERROR - ошибки, требующие внимания
logger.error(s"Failed to process request", exception)
```

### Избегание логирования чувствительных данных

```scala
// Плохо - логирование паролей
logger.info(s"User logged in with password: $password")

// Хорошо - логирование без чувствительных данных
logger.info(s"User ${userId} logged in")
```

### Использование MDC для контекста

```scala
// Хорошо - использование MDC для контекста запроса
MDC.put("requestId", requestId)
logger.info("Processing request")
MDC.clear()

// Плохо - передача контекста в каждом вызове
logger.info(s"[requestId=$requestId] Processing request")
```

### Ленивая оценка для дорогих операций

```scala
// Хорошо - ленивая оценка
if (logger.isDebugEnabled) {
  logger.debug(s"Expensive: ${expensiveOperation()}")
}

// Плохо - всегда выполняется
logger.debug(s"Expensive: ${expensiveOperation()}")
```

## Продвинутые техники логирования

### Структурированное логирование

Структурированное логирование позволяет создавать логи в формате JSON для лучшей обработки.

```scala
import net.logstash.logback.encoder.LogstashEncoder
import ch.qos.logback.classic.LoggerContext

// Конфигурация структурированного логирования
val context = new LoggerContext()
val encoder = new LogstashEncoder()
encoder.setContext(context)
encoder.start()

// Использование
logger.info("User action", Map(
  "userId" -> "123",
  "action" -> "login",
  "timestamp" -> System.currentTimeMillis()
))
```

### Асинхронное логирование

Асинхронное логирование улучшает производительность приложения.

```scala
import ch.qos.logback.classic.AsyncAppender

// Конфигурация асинхронного логирования
val asyncAppender = new AsyncAppender()
asyncAppender.setQueueSize(512)
asyncAppender.setDiscardingThreshold(0)
asyncAppender.setIncludeCallerData(true)
```

### Кастомные аппендеры

Создание кастомных аппендеров для специальных случаев.

```scala
import ch.qos.logback.core.AppenderBase
import ch.qos.logback.classic.spi.ILoggingEvent

class CustomAppender extends AppenderBase[ILoggingEvent] {
  override def append(event: ILoggingEvent): Unit = {
    // Кастомная обработка логов
    sendToExternalSystem(event)
  }
}
```

## Заключение

## Дополнительные техники логирования

### Контекстное логирование с MDC

MDC (Mapped Diagnostic Context) позволяет добавлять контекстную информацию к логам.

```scala
import org.slf4j.MDC

// Добавление контекста к логам
MDC.put("userId", "12345")
MDC.put("requestId", "req-abc-123")
logger.info("Processing request")

// Удаление контекста
MDC.clear()
```

### Структурированное логирование

Структурированное логирование упрощает анализ логов.

```scala
import com.typesafe.scalalogging.Logger

// Структурированное логирование с JSON
logger.info(
  s"""{"event": "user_login", "userId": "$userId", "timestamp": "${System.currentTimeMillis()}"}"""
)
```

### Асинхронное логирование

Асинхронное логирование улучшает производительность приложений.

```scala
import ch.qos.logback.classic.AsyncAppender

// Настройка асинхронного логирования в logback.xml
// <appender name="ASYNC" class="ch.qos.logback.classic.AsyncAppender">
//   <appender-ref ref="FILE" />
// </appender>
```

### Практические примеры: Структурированное логирование

```scala
import com.typesafe.scalalogging.Logger
import net.logstash.logback.marker.Markers

val logger = Logger("Application")

// Структурированное логирование с JSON
def logUserAction(userId: String, action: String, details: Map[String, Any]): Unit = {
  logger.info(
    Markers.appendEntries(details.asJava),
    s"User $userId performed action: $action"
  )
}

// Использование
logUserAction(
  "user123",
  "purchase",
  Map(
    "productId" -> "prod456",
    "amount" -> 99.99,
    "currency" -> "USD"
  )
)
```

### Практические примеры: Контекстное логирование с MDC

```scala
import org.slf4j.MDC
import com.typesafe.scalalogging.Logger

val logger = Logger("Application")

def processRequest(requestId: String, userId: String)(body: => Unit): Unit = {
  MDC.put("requestId", requestId)
  MDC.put("userId", userId)
  
  try {
    logger.info("Processing request")
    body
    logger.info("Request completed successfully")
  } catch {
    case e: Exception =>
      logger.error("Request failed", e)
      throw e
  } finally {
    MDC.clear()
  }
}

// Использование
processRequest("req-123", "user-456") {
  // Вся логика автоматически получит requestId и userId из MDC
  logger.debug("Processing step 1")
  logger.debug("Processing step 2")
}
```

Логирование является критичной частью разработки приложений. Понимание SLF4J, Logback, Scala Logging, структурированного логирования, MDC, асинхронного логирования, кастомных аппендеров, контекстного логирования с MDC, структурированного логирования с JSON, асинхронного логирования и их практических применений позволяет создавать эффективные системы логирования для мониторинга и отладки приложений. Правильное использование уровней логирования, MDC для контекста, ленивой оценки для дорогих операций, структурированного логирования, асинхронного логирования, добавление контекстной информации с MDC, структурированное логирование с JSON, настройка асинхронного логирования, и использование MDC для трейсинга запросов критично для production-ready приложений. Логирование особенно важно для создания наблюдаемых систем, которые позволяют отслеживать поведение приложений, диагностировать проблемы, анализировать производительность, добавлять контекстную информацию к логам, использовать структурированное логирование для упрощения анализа, и трейсить запросы через несколько сервисов.

### Практические примеры: Структурированное логирование

```scala
import com.typesafe.scalalogging.Logger
import org.slf4j.LoggerFactory

val logger = Logger(LoggerFactory.getLogger("MyApp"))

// Структурированное логирование с JSON
case class LogEvent(level: String, message: String, timestamp: Long, metadata: Map[String, String])

def logStructured(event: LogEvent): Unit = {
  val json = s"""{"level":"${event.level}","message":"${event.message}","timestamp":${event.timestamp},"metadata":${event.metadata.mkString(",")}}"""
  logger.info(json)
}

// Использование
logStructured(LogEvent(
  level = "INFO",
  message = "User logged in",
  timestamp = System.currentTimeMillis(),
  metadata = Map("userId" -> "123", "ip" -> "192.168.1.1")
))
```

### Практические примеры: Асинхронное логирование

```scala
import ch.qos.logback.classic.AsyncAppender
import ch.qos.logback.classic.LoggerContext

// Настройка асинхронного логирования
val context = LoggerFactory.getILoggerFactory.asInstanceOf[LoggerContext]
val asyncAppender = new AsyncAppender()
asyncAppender.setContext(context)
asyncAppender.setQueueSize(512)
asyncAppender.setDiscardingThreshold(0)
asyncAppender.start()
```

### Использование с различными техниками для структурированного логирования

```scala
import com.typesafe.scalalogging.Logger
import org.slf4j.LoggerFactory

val logger = Logger(LoggerFactory.getLogger("MyApp"))

// Структурированное логирование с JSON
case class LogEvent(level: String, message: String, timestamp: Long, metadata: Map[String, String])

def logStructured(event: LogEvent): Unit = {
  val json = s"""{"level":"${event.level}","message":"${event.message}","timestamp":${event.timestamp},"metadata":${event.metadata.mkString(",")}}"""
  logger.info(json)
}

// Использование
logStructured(LogEvent(
  level = "INFO",
  message = "User logged in",
  timestamp = System.currentTimeMillis(),
  metadata = Map("userId" -> "123", "ip" -> "192.168.1.1")
))
```

### Использование с различными техниками для асинхронного логирования

```scala
import ch.qos.logback.classic.AsyncAppender
import ch.qos.logback.classic.LoggerContext

// Настройка асинхронного логирования
val context = LoggerFactory.getILoggerFactory.asInstanceOf[LoggerContext]
val asyncAppender = new AsyncAppender()
asyncAppender.setContext(context)
asyncAppender.setQueueSize(512)
asyncAppender.setDiscardingThreshold(0)
asyncAppender.start()
```

## Дополнительные ресурсы

Для дальнейшего изучения логирования в Scala рекомендуется:

- [SLF4J Documentation](http://www.slf4j.org/)
- [Logback Documentation](http://logback.qos.ch/)
- [Scala Logging](https://github.com/lightbend/scala-logging)
- [Logstash Logback Encoder](https://github.com/logfellow/logstash-logback-encoder)

