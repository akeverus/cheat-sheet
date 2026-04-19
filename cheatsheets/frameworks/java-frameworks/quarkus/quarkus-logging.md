---
title: "Quarkus: Logging - Логирование"
description: "Полное руководство по логированию в Quarkus: SLF4J, Logback, structured logging, MDC, log levels и best practices"
tags:
  - quarkus
  - logging
  - slf4j
  - logback
  - java
difficulty: "intermediate"
prerequisites: ["quarkus/quarkus-basics.md", "quarkus/quarkus-core.md"]
next: ["quarkus-core.md", "quarkus-actuator.md"]
updated: "2026-02-11"
related: ["quarkus-core.md", "quarkus-actuator.md"]
---

# Quarkus: Logging - Логирование



## Полезные ссылки

[Официальная документация Quarkus](https://quarkus.io/guides/)
[Quarkus GitHub](https://github.com/quarkusio/quarkus)

## Содержание

- [Введение](#введение)
  - [Основные возможности](#основные-возможности)
- [Basic Logging](#basic-logging)
  - [Simple Logging](#simple-logging)
  - [Log Levels](#log-levels)
- [application.properties](#applicationproperties)
- [Structured Logging](#structured-logging)
  - [JSON Logging](#json-logging)
  - [Structured Log Messages](#structured-log-messages)
- [MDC (Mapped Diagnostic Context)](#mdc-mapped-diagnostic-context)
  - [Using MDC](#using-mdc)
- [Лучшие практики](#лучшие-практики)
  - [1. Используйте правильные уровни логирования](#1-используйте-правильные-уровни-логирования)
  - [2. Используйте structured logging](#2-используйте-structured-logging)
  - [3. Используйте MDC для контекста](#3-используйте-mdc-для-контекста)
- [Advanced Logging Configuration](#advanced-logging-configuration)
  - [File Appenders](#file-appenders)
  - [Async Appenders](#async-appenders)
  - [Custom Formatters](#custom-formatters)
- [Logging Performance](#logging-performance)
  - [Log Level Optimization](#log-level-optimization)
- [Production - только важные логи](#production-только-важные-логи)
- [Development - подробные логи](#development-подробные-логи)
  - [Structured Logging Performance](#structured-logging-performance)
- [Logging Best Practices](#logging-best-practices)
  - [4. Проверяйте уровень перед логированием](#4-проверяйте-уровень-перед-логированием)
  - [5. Используйте правильные appenders](#5-используйте-правильные-appenders)
- [✅ Хорошо - для production](#хорошо-для-production)
- [Заключение](#заключение)
- [Дополнительные ресурсы](#дополнительные-ресурсы)

## Введение

**Quarkus** предоставляет мощную систему логирования через **SLF4J** и **Logback**. Это позволяет настраивать различные уровни логирования, форматы, **appenders** и другие аспекты логирования.

### Основные возможности

- **SLF4J**: Стандартный интерфейс логирования
- **Logback**: Реализация логирования
- **Structured Logging**: Структурированное логирование
- **MDC**: **Mapped Diagnostic Context**
- **Log Levels**: Уровни логирования

## Basic Logging

### **Simple Logging**

**Простое логирование:**

```java
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class LoggingService {
    
    private static final Logger logger = LoggerFactory.getLogger(LoggingService.class);
    
    public void logMessage() {
        logger.info("Info message");
        logger.debug("Debug message");
        logger.warn("Warning message");
        logger.error("Error message");
    }
}
```

### **Log Levels**

**Уровни логирования:**

```properties
# application.properties
quarkus.log.level=INFO
quarkus.log.category."org.example".level=DEBUG
quarkus.log.category."io.quarkus".level=WARN
```

## Structured Logging

### **JSON Logging**

**JSON** логирование:**

```properties
# application.properties
quarkus.log.console.json=true
quarkus.log.console.json.pretty-print=true
```

### **Structured Log Messages**

**Структурированные сообщения:**

```java
@ApplicationScoped
public class StructuredLoggingService {
    
    private static final Logger logger = LoggerFactory.getLogger(StructuredLoggingService.class);
    
    public void logStructured(User user) {
        logger.info("User created: userId={}, email={}", user.getId(), user.getEmail());
    }
}
```

## MDC** (`Mapped Diagnostic Context`)

### **Using MDC**

**Использование **MDC**:**

```java
import org.slf4j.MDC;

@ApplicationScoped
public class MDCLoggingService {
    
    private static final Logger logger = LoggerFactory.getLogger(MDCLoggingService.class);
    
    public void processRequest(String requestId, String userId) {
        MDC.put("requestId", requestId);
        MDC.put("userId", userId);
        
        try {
            logger.info("Processing request");
            // Обработка запроса
        } finally {
            MDC.clear();
        }
    }
}
```

## Лучшие практики

### 1. Используйте правильные уровни логирования

```java
// ✅ Хорошо
logger.debug("Debug information");
logger.info("Important information");
logger.warn("Warning message");
logger.error("Error message", exception);
```

### 2. Используйте **structured logging**

```java
// ✅ Хорошо
logger.info("User created: userId={}, email={}", userId, email);
```

### 3. Используйте **MDC** для контекста

```java
// ✅ Хорошо
MDC.put("requestId", requestId);
try {
    // Логирование с контекстом
} finally {
    MDC.clear();
}
```

## Advanced Logging Configuration

### **File Appenders**

**Настройка **file appenders**:**

```properties
quarkus.log.file.enable=true
quarkus.log.file.path=/var/log/myapp.log
quarkus.log.file.rotation.max-file-size=10M
quarkus.log.file.rotation.max-backup-index=10
```

### **Async Appenders**

**Асинхронные **appenders**:**

```properties
quarkus.log.async.enable=true
quarkus.log.async.queue-length=256
quarkus.log.async.overflow=discard
```

### **Custom Formatters**

**Кастомные форматтеры:**

```properties
quarkus.log.console.format=%d{yyyy-MM-dd HH:mm:ss} %-5p [%t] %c{1}: %m%n
```

## Logging Performance

### **Log Level Optimization**

**Оптимизация уровней логирования:**

```properties
# Production - только важные логи
%prod.quarkus.log.level=WARN
%prod.quarkus.log.category."org.example".level=INFO

# Development - подробные логи
%dev.quarkus.log.level=DEBUG
```

### **Structured Logging Performance**

**Производительность **structured logging**:**

```java
@ApplicationScoped
public class PerformanceLoggingService {
    
    private static final Logger logger = LoggerFactory.getLogger(PerformanceLoggingService.class);
    
    public void logWithPerformance(User user) {
        // Используйте параметризованные сообщения для лучшей производительности
        if (logger.isDebugEnabled()) {
            logger.debug("Processing user: userId={}, email={}", 
                user.getId(), user.getEmail());
        }
    }
}
```

## Logging Best Practices

### 1. Используйте правильные уровни логирования

```java
// ✅ Хорошо
logger.debug("Debug information");
logger.info("Important information");
logger.warn("Warning message");
logger.error("Error message", exception);
```

### 2. Используйте **structured logging**

```java
// ✅ Хорошо
logger.info("User created: userId={}, email={}", userId, email);
```

### 3. Используйте **MDC** для контекста

```java
// ✅ Хорошо
MDC.put("requestId", requestId);
try {
    // Логирование с контекстом
} finally {
    MDC.clear();
}
```

### 4. Проверяйте уровень перед логированием

```java
// ✅ Хорошо - для дорогих операций
if (logger.isDebugEnabled()) {
    logger.debug("Expensive operation: {}", expensiveOperation());
}
```

### 5. Используйте правильные **appenders**

```properties
# ✅ Хорошо - для production
quarkus.log.file.enable=true
quarkus.log.console.enable=false
```


## Заключение

**Quarkus Logging** предоставляет мощные инструменты для логирования. Поддержка **SLF4J**, **Logback**, **structured logging**, **MDC** и других возможностей позволяет создавать эффективные системы логирования.

## Дополнительные ресурсы

- [**Quarkus Logging** Guide](https://quarkus.io/guides/logging)
- [**SLF4J** Documentation](https://www.slf4j.org/documentation.html)
- [Logback Documentation](https://logback.qos.ch/documentation.html)
