---
title: "Micronaut: Views — Template Engines и View Rendering"
description: "Полное руководство по Views в Micronaut: template engines, view rendering, Thymeleaf, Freemarker, Velocity и best practices"
tags:
  - micronaut
  - views
  - templates
  - thymeleaf
  - freemarker
  - velocity
  - java
  - kotlin
difficulty: "intermediate"
prerequisites: ["micronaut/micronaut-basics.md", "micronaut/micronaut-http.md"]
next: ["micronaut-http.md", "micronaut-mail.md"]
updated: "2026-04-20"
related: ["micronaut-http.md", "micronaut-mail.md"]
---

# Micronaut: Views — Template Engines и View Rendering

## Полезные ссылки

[Официальная документация Micronaut](https://docs.micronaut.io/)
[Micronaut GitHub](https://github.com/micronaut-projects/micronaut-core)

## Содержание

- [Введение](#введение)
  - [Основные возможности](#основные-возможности)
- [Настройка Views](#настройка-views)
  - [Зависимости](#зависимости)
  - [Конфигурация](#конфигурация)
- [Thymeleaf](#thymeleaf)
  - [Thymeleaf Template](#thymeleaf-template)
  - [Controller with Thymeleaf](#controller-with-thymeleaf)
- [Freemarker](#freemarker)
  - [Freemarker Template](#freemarker-template)
  - [Controller with Freemarker](#controller-with-freemarker)
- [Velocity](#velocity)
  - [Velocity Template](#velocity-template)
- [View Models](#view-models)
  - [Custom View Models](#custom-view-models)
- [Лучшие практики](#лучшие-практики)
  - [1. Используйте view models](#1-используйте-view-models)
  - [2. Разделяйте логику и представление](#2-разделяйте-логику-и-представление)
  - [3. Используйте layout templates](#3-используйте-layout-templates)
- [Layout Templates](#layout-templates)
  - [Thymeleaf Layout](#thymeleaf-layout)
  - [Using Layouts](#using-layouts)
- [Internationalization](#internationalization)
  - [i18n Support](#i18n-support)
  - [Template with i18n](#template-with-i18n)
- [Template Caching](#template-caching)
  - [Cache Configuration](#cache-configuration)
- [Custom View Resolvers](#custom-view-resolvers)
  - [Custom Resolver](#custom-resolver)
- [Заключение](#заключение)
- [Дополнительные ресурсы](#дополнительные-ресурсы)
- [См. также](#см-также)

## Введение

**Micronaut** предоставляет поддержку различных **template engines** для рендеринга **views**. Это позволяет создавать динамические веб-страницы и **email** шаблоны с использованием популярных **template engines**.

### Основные возможности

- **Thymeleaf**: Поддержка **Thymeleaf template engine**
- **Freemarker**: Поддержка **Apache Freemarker**
- **Velocity**: Поддержка **Apache Velocity**
- **View Rendering**: Рендеринг **views** в контроллерах
- **Email Templates**: Использование шаблонов для **email**

## Настройка Views

### Зависимости

**build.gradle:**

```gradle
dependencies {
    // Thymeleaf
    implementation("io.micronaut.views:micronaut-views-thymeleaf")
    // или Freemarker
    implementation("io.micronaut.views:micronaut-views-freemarker")
    // или Velocity
    implementation("io.micronaut.views:micronaut-views-velocity")
}
```

### Конфигурация

**application.yml:**

```yaml
micronaut:
  views:
    enabled: true
    folder: views
    default-extension: html
```

## Thymeleaf

### Thymeleaf Template

**views/users/`list.html`:**

```html
<!DOCTYPE html>
<html xmlns:th="http://www.thymeleaf.org">
<head>
    <title>Users</title>
</head>
<body>
    <h1>User List</h1>
    <table>
        <tr th:each="user : ${users}">
            <td th:text="${user.name}">Name</td>
            <td th:text="${user.email}">Email</td>
        </tr>
    </table>
</body>
</html>
```

### Controller with Thymeleaf

```java
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Get;
import io.micronaut.views.View;
import jakarta.inject.Singleton;
import java.util.Map;

@Controller("/users")
public class UserController {

    @Get("/list")
    @View("users/list")
    public Map<String, Object> listUsers() {
        return Map.of("users", userService.findAll());
    }
}
```

## Freemarker

### Freemarker Template

**views/users/`list.ftl`:**

```ftl
<!DOCTYPE html>
<html>
<head>
    <title>Users</title>
</head>
<body>
    <h1>User List</h1>
    <table>
        <#list users as user>
        <tr>
            <td>${user.name}</td>
            <td>${user.email}</td>
        </tr>
        </#list>
    </table>
</body>
</html>
```

### Controller with Freemarker

```java
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Get;
import io.micronaut.views.View;

@Controller("/users")
public class UserController {

    @Get("/list")
    @View("users/list")
    public Map<String, Object> listUsers() {
        return Map.of("users", userService.findAll());
    }
}
```

## Velocity

### Velocity Template

**views/users/`list.vm`:**

```vm
<!DOCTYPE html>
<html>
<head>
    <title>Users</title>
</head>
<body>
    <h1>User List</h1>
    <table>
        #foreach($user in $users)
        <tr>
            <td>$user.name</td>
            <td>$user.email</td>
        </tr>
        #end
    </table>
</body>
</html>
```

## View Models

### Custom View Models

```java
import io.micronaut.views.View;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Get;
import jakarta.inject.Singleton;

@Controller("/users")
public class UserController {

    @Get("/{id}")
    @View("users/detail")
    public UserViewModel getUser(Long id) {
        User user = userService.findById(id);
        return new UserViewModel(user);
    }
}

public class UserViewModel {
    private final User user;

    public UserViewModel(User user) {
        this.user = user;
    }

    public String getName() {
        return user.getName();
    }

    public String getEmail() {
        return user.getEmail();
    }

    public boolean isAdult() {
        return user.getAge() >= 18;
    }
}
```

## Лучшие практики

### 1. Используйте view models

```java
// ✅ Хорошо
@View("users/list")
public UserViewModel getUser(Long id) {
    return new UserViewModel(user);
}
```

### 2. Разделяйте логику и представление

```java
// ✅ Хорошо
@View("users/list")
public Map<String, Object> listUsers() {
    return Map.of("users", userService.findAll());
}
```

### 3. Используйте layout templates

```html
<!-- ✅ Хорошо -->
<html th:replace="~{layout :: layout}">
```

## Layout Templates

### Thymeleaf Layout

**views/`layout.html`:**

```html
<!DOCTYPE html>
<html xmlns:th="http://www.thymeleaf.org">
<head>
    <title th:text="${title}">Default Title</title>
</head>
<body>
    <header>
        <nav>Navigation</nav>
    </header>
    <main th:replace="${content}">
        Content
    </main>
    <footer>
        Footer
    </footer>
</body>
</html>
```

### Using Layouts

```java
@Controller("/users")
public class UserController {

    @Get("/list")
    @View("users/list")
    public Map<String, Object> listUsers() {
        return Map.of(
            "title", "User List",
            "users", userService.findAll()
        );
    }
}
```

## Internationalization

### i18n Support

**application.yml:**

```yaml
micronaut:
  views:
    i18n:
      enabled: true
      default-locale: en
```

### Template with i18n

```html
<!DOCTYPE html>
<html xmlns:th="http://www.thymeleaf.org">
<head>
    <title th:text="#{page.title}">Title</title>
</head>
<body>
    <h1 th:text="#{users.title}">Users</h1>
</body>
</html>
```

## Template Caching

### Cache Configuration

**application.yml:**

```yaml
micronaut:
  views:
    cache:
      enabled: true
      max-size: 1000
```

## Custom View Resolvers

### Custom Resolver

```java
import io.micronaut.views.ViewResolver;
import jakarta.inject.Singleton;
import java.util.Optional;

@Singleton
public class CustomViewResolver implements ViewResolver<Map<String, Object>> {

    @Override
    public Optional<String> resolve(String viewName, Map<String, Object> model) {
        // Кастомная логика разрешения views
        String templatePath = "templates/" + viewName + ".html";
        return Optional.of(templatePath);
    }
}
```


## Заключение

**Micronaut Views** предоставляет мощные инструменты для рендеринга **views**. Поддержка **Thymeleaf**, **Freemarker**, **Velocity**, **view models**, **layout templates**, **internationalization**, **template caching**, **custom view resolvers** и других продвинутых возможностей позволяет создавать динамические веб-страницы и **email** шаблоны.

## Дополнительные ресурсы

- [**Micronaut Views** Documentation](https://micronaut-projects.github.io/micronaut-views/latest/guide/)
- [Thymeleaf Documentation](https://www.thymeleaf.org/documentation.html)
- [Freemarker Documentation](https://freemarker.apache.org/docs/)
- [Velocity Documentation](https://velocity.apache.org/engine/releases/velocity-1.7/)

## См. также

- [Micronaut: Actuator — Health Checks, Metrics и Endpoints](micronaut-actuator.md)
- [Micronaut: Основы](micronaut-basics.md)
- [Micronaut: Batch Processing — Job Processing и Scheduling](micronaut-batch.md)
- [Micronaut: Caching — Cache Abstraction и Redis Cache](micronaut-cache.md)
- [Micronaut: Cloud Native — Service Discovery, Configuration и Distributed Tracing](micronaut-cloud.md)
