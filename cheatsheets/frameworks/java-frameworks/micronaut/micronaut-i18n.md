---
title: "Micronaut: Internationalization - i18n и Localization"
description: "Полное руководство по интернационализации в Micronaut: i18n, localization, message bundles, locale resolution и best practices"
tags:
  - micronaut
  - i18n
  - internationalization
  - localization
  - locale
  - java
  - kotlin
difficulty: "intermediate"
prerequisites: ["micronaut/micronaut-basics.md", "micronaut/micronaut-http.md"]
next: ["micronaut-http.md", "micronaut-views.md"]
updated: "2026-02-11"
related: ["micronaut-http.md", "micronaut-views.md"]
---

# Micronaut: Internationalization — i18n и Localization

## Полезные ссылки

[Официальная документация Micronaut](https://docs.micronaut.io/)
[Micronaut GitHub](https://github.com/micronaut-projects/micronaut-core)

## Содержание

- [Micronaut: Internationalization — i18n и Localization](#micronaut-internationalization-i18n-и-localization)
- [Введение](#введение)
  - [Основные возможности](#основные-возможности)
- [Настройка i18n](#настройка-i18n)
  - [Зависимости](#зависимости)
  - [Конфигурация](#конфигурация)
- [Message Bundles](#message-bundles)
  - [Message Files](#message-files)
- [Locale Resolution](#locale-resolution)
  - [HTTP Header Resolver](#http-header-resolver)
  - [Query Parameter Resolver](#query-parameter-resolver)
- [Dynamic Messages](#dynamic-messages)
  - [Parameterized Messages](#parameterized-messages)
  - [Using Parameters](#using-parameters)
- [Лучшие практики](#лучшие-практики)
  - [1. Используйте ключи вместо текста](#1-используйте-ключи-вместо-текста)
  - [2. Группируйте сообщения по функциональности](#2-группируйте-сообщения-по-функциональности)
- [✅ Хорошо](#хорошо)
  - [3. Предоставляйте fallback для отсутствующих переводов](#3-предоставляйте-fallback-для-отсутствующих-переводов)
- [Locale-aware Formatting](#locale-aware-formatting)
  - [Number Formatting](#number-formatting)
- [Message Source Hierarchy](#message-source-hierarchy)
  - [Hierarchical Message Sources](#hierarchical-message-sources)
- [Custom Locale Resolver](#custom-locale-resolver)
  - [Custom Resolver Implementation](#custom-resolver-implementation)
- [Message Source Caching](#message-source-caching)
  - [Caching Configuration](#caching-configuration)
- [Resource Bundle Loading](#resource-bundle-loading)
  - [Custom Resource Bundle](#custom-resource-bundle)
- [Заключение](#заключение)
- [Дополнительные ресурсы](#дополнительные-ресурсы)

## Введение

**Micronaut** предоставляет поддержку интернационализации (**i18n**) для создания многоязычных приложений. Это позволяет локализовать сообщения и контент для различных языков и регионов.

### Основные возможности

- **Message Bundles**: Файлы с переводами
- **Locale Resolution**: Определение локали из запроса
- **Dynamic Messages**: Динамические сообщения
- **Locale-aware Services**: Сервисы с поддержкой локали

## Настройка i18n

### Зависимости

**build.gradle:**

```gradle
dependencies {
    implementation("io.micronaut:micronaut-i18n")
}
```

### Конфигурация

**application.yml:**

```yaml
micronaut:
  i18n:
    enabled: true
    default-locale: en
    message-bundles:
      - messages
```

## Message Bundles

### **Message Files**

**messages.properties:**

```properties
user.notfound=User not found
user.created=User created successfully
user.updated=User updated successfully
```

**messages_ru.properties:**

```properties
user.notfound=Пользователь не найден
user.created=Пользователь успешно создан
user.updated=Пользователь успешно обновлен
```

## Locale Resolution

### **HTTP Header Resolver**

```java
import io.micronaut.i18n.MessageSource;
import io.micronaut.http.HttpRequest;
import jakarta.inject.Singleton;

@Singleton
public class LocalizedService {
    private final MessageSource messageSource;

    public LocalizedService(MessageSource messageSource) {
        this.messageSource = messageSource;
    }

    public String getMessage(HttpRequest<?> request, String key) {
        Locale locale = request.getLocale().orElse(Locale.getDefault());
        return messageSource.getMessage(key, locale);
    }
}
```

### **Query Parameter Resolver**

```java
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Get;
import io.micronaut.http.annotation.QueryValue;

@Controller("/api/users")
public class LocalizedController {

    @Get("/{id}")
    public User getUser(Long id, @QueryValue(defaultValue = "en") String lang) {
        Locale locale = Locale.forLanguageTag(lang);
        // Использование локали
        return userService.findById(id);
    }
}
```

## Dynamic Messages

### **Parameterized Messages**

**messages.properties:**

```properties
user.welcome=Welcome, {0}!
user.age=You are {0} years old
```

### **Using Parameters**

```java
import io.micronaut.i18n.MessageSource;
import jakarta.inject.Singleton;

@Singleton
public class ParameterizedMessageService {
    private final MessageSource messageSource;

    public String getWelcomeMessage(String name, Locale locale) {
        return messageSource.getMessage("user.welcome", locale, name);
    }
}
```

## Лучшие практики

### 1. Используйте ключи вместо текста

```java
// ✅ Хорошо
messageSource.getMessage("user.notfound", locale);
```

### 2. Группируйте сообщения по функциональности

```properties
# ✅ Хорошо
user.notfound=User not found
user.created=User created
order.notfound=Order not found
order.created=Order created
```

### 3. Предоставляйте **fallback** для отсутствующих переводов

```java
// ✅ Хорошо
String message = messageSource.getMessage(key, locale)
    .orElse(messageSource.getMessage(key, Locale.ENGLISH).orElse(key));
```

## Locale-aware Formatting

### **Number Formatting**

```java
import java.text.NumberFormat;
import java.util.Locale;
import jakarta.inject.Singleton;

@Singleton
public class LocaleFormattingService {

    public String formatNumber(Double number, Locale locale) {
        NumberFormat formatter = NumberFormat.getNumberInstance(locale);
        return formatter.format(number);
    }

    public String formatCurrency(Double amount, Locale locale) {
        NumberFormat formatter = NumberFormat.getCurrencyInstance(locale);
        return formatter.format(amount);
    }

    public String formatDate(LocalDate date, Locale locale) {
        DateTimeFormatter formatter = DateTimeFormatter.ofLocalizedDate(FormatStyle.LONG)
            .withLocale(locale);
        return date.format(formatter);
    }
}
```

## Message Source Hierarchy

### **Hierarchical Message Sources**

**application.yml:**

```yaml
micronaut:
  i18n:
    message-bundles:
      - messages
      - validation
      - errors
```

## Custom Locale Resolver

### **Custom Resolver Implementation**

```java
import io.micronaut.i18n.LocaleResolver;
import io.micronaut.http.HttpRequest;
import jakarta.inject.Singleton;
import java.util.Locale;

@Singleton
public class CustomLocaleResolver implements LocaleResolver {

    @Override
    public Locale resolve(HttpRequest<?> request) {
        // Определение локали из cookie
        String localeStr = request.getCookies().get("locale")
            .map(c -> c.getValue())
            .orElse("en");
        return Locale.forLanguageTag(localeStr);
    }
}
```

## Message Source Caching

### **Caching Configuration**

**application.yml:**

```yaml
micronaut:
  i18n:
    cache:
      enabled: true
      max-size: 1000
      expire-after-write: 1h
```

## Resource Bundle Loading

### **Custom Resource Bundle**

```java
import io.micronaut.i18n.MessageSource;
import jakarta.inject.Singleton;
import java.util.ResourceBundle;

@Singleton
public class CustomMessageSource implements MessageSource {

    @Override
    public Optional<String> getMessage(String code, Locale locale, Object... args) {
        ResourceBundle bundle = ResourceBundle.getBundle("messages", locale);
        if (bundle.containsKey(code)) {
            String message = bundle.getString(code);
            return Optional.of(MessageFormat.format(message, args));
        }
        return Optional.empty();
    }
}
```


## Заключение

**Micronaut i18n** предоставляет мощные инструменты для интернационализации приложений. Поддержка **message bundles**, **locale resolution**, **dynamic messages**, **parameterized messages**, **locale-aware formatting**, **message source hierarchy**, **custom locale resolvers**, **caching**, **resource bundle loading** и других продвинутых возможностей позволяет создавать многоязычные приложения.

## Дополнительные ресурсы

- [**Micronaut** i18n Documentation](https://docs.micronaut.io/latest/guide/index.html#i18n)
- [**Java** Internationalization](https://docs.oracle.com/javase/tutorial/i18n/)
- [**Locale Best Practices**](https://www.baeldung.com/java-localization-messages)
- [**ResourceBundle** Documentation](https://docs.oracle.com/javase/8/docs/api/java/util/ResourceBundle.html)

## См. также

- [[micronaut-actuator|Micronaut: Actuator — Health Checks, Metrics и Endpoints]]
- [[micronaut-basics|Micronaut: Основы]]
- [[micronaut-batch|Micronaut: Batch Processing — Job Processing и Scheduling]]
- [[micronaut-cache|Micronaut: Caching — Cache Abstraction и Redis Cache]]
- [[micronaut-cloud|Micronaut: Cloud Native — Service Discovery, Configuration и Distributed Tracing]]
