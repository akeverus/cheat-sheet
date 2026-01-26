---
title: "Quarkus: Qute - Templating Engine"
description: "Полное руководство по Qute в Quarkus: шаблонизация, теги, инклюды, fragments, type-safe templates и best practices"
tags: ["quarkus", "qute", "templating", "templates", "html", "java"]
difficulty: "intermediate"
prerequisites: ["quarkus/quarkus-basics.md", "quarkus/quarkus-core.md"]
next: ["quarkus-core.md", "quarkus-rest.md"]
updated: "2025-01-16"
related: ["quarkus-core.md", "quarkus-rest.md"]
---

# Quarkus: Qute - Templating Engine

## Введение

Qute - это современный и типобезопасный движок шаблонов для Quarkus. Он обеспечивает высокую производительность и безопасность при работе с шаблонами.

### Основные возможности

- **Type-safe Templates**: Типобезопасные шаблоны
- **Server-side Rendering**: Рендеринг на сервере
- **Reactive Support**: Поддержка reactive streams
- **Fragments**: Переиспользуемые фрагменты

## Basic Templates

### Simple Template

Простой шаблон:

```html
<!-- templates/hello.html -->
<!DOCTYPE html>
<html>
<head>
    <title>Hello {name}</title>
</head>
<body>
    <h1>Hello {name}!</h1>
</body>
</html>
```

### Using Template

Использование шаблона:

```java
import io.quarkus.qute.Template;
import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;

@Path("/hello")
public class HelloResource {
    
    @Inject
    Template hello;
    
    @GET
    public String getHello() {
        return hello.data("name", "World").render();
    }
}
```

## Type-safe Templates

### Type-safe Template

Типобезопасный шаблон:

```java
@CheckedTemplate
public class Templates {
    
    public static native TemplateInstance hello(String name);
}
```

### Using Type-safe Template

Использование типобезопасного шаблона:

```java
@Path("/hello")
public class HelloResource {
    
    @GET
    public String getHello() {
        return Templates.hello("World").render();
    }
}
```

## Template Features

### Conditionals

Условные выражения:

```html
{#if user.isActive}
    <p>User is active</p>
{#else}
    <p>User is inactive</p>
{/if}
```

### Loops

Циклы:

```html
<ul>
{#for item in items}
    <li>{item.name}</li>
{/for}
</ul>
```

### Includes

Инклюды:

```html
{@include("header.html")}
<main>Content</main>
{@include("footer.html")}
```

## Best Practices

### 1. Используйте type-safe templates

```java
// ✅ Хорошо
@CheckedTemplate
public class Templates {
    public static native TemplateInstance user(User user);
}
```

### 2. Валидируйте данные

```java
// ✅ Хорошо
template.data("user", user).render();
```

### 3. Используйте fragments для переиспользования

```html
<!-- ✅ Хорошо -->
{#fragment header}
    <header>Header</header>
{/fragment}
```

## Advanced Qute Features

### Fragments and Sections

Фрагменты и секции:

```html
<!-- templates/base.html -->
<!DOCTYPE html>
<html>
<head>
    <title>{title}</title>
</head>
<body>
    {#include header /}
    <main>
        {#insert content}Default content{/insert}
    </main>
    {#include footer /}
</body>
</html>

<!-- templates/page.html -->
{#include base.html}
    {#title}My Page{/title}
    {#content}
        <h1>Page Content</h1>
    {/content}
{/include}
```

### User-defined Tags

Пользовательские теги:

```java
@ApplicationScoped
public class UserTags {
    
    public static Template.Fragment formatDate(LocalDateTime date) {
        return Template.Fragment.of(
            date.format(DateTimeFormatter.ofPattern("dd.MM.yyyy"))
        );
    }
}
```

Использование:

```html
{user:formatDate(user.createdAt)}
```

### Reactive Templates

Реактивные шаблоны:

```java
@Path("/users")
public class ReactiveUserResource {
    
    @Inject
    Template users;
    
    @GET
    @Produces(MediaType.TEXT_HTML)
    public Uni<String> getUsers() {
        return userService.findAllAsync()
            .map(usersList -> users.data("users", usersList).render());
    }
}
```

### Template Inheritance

Наследование шаблонов:

```html
<!-- templates/layout.html -->
<!DOCTYPE html>
<html>
<head>
    <title>{title}</title>
</head>
<body>
    {#insert body}Default body{/insert}
</body>
</html>

<!-- templates/page.html -->
{#include layout.html}
    {#title}My Page{/title}
    {#body}
        <h1>Page Content</h1>
    {/body}
{/include}
```

## Qute Best Practices

### 1. Используйте type-safe templates

```java
// ✅ Хорошо
@CheckedTemplate
public class Templates {
    public static native TemplateInstance user(User user);
}
```

### 2. Валидируйте данные

```java
// ✅ Хорошо
template.data("user", user).render();
```

### 3. Используйте fragments для переиспользования

```html
<!-- ✅ Хорошо -->
{#fragment header}
    <header>Header</header>
{/fragment}
```

### 4. Используйте includes для компонентов

```html
<!-- ✅ Хорошо -->
{@include("components/header.html")}
```

### 5. Оптимизируйте для production

```properties
quarkus.qute.optimize-templates=true
```

## Заключение

Qute предоставляет мощные инструменты для шаблонизации. Поддержка type-safe templates, server-side rendering, reactive streams, fragments и других возможностей позволяет создавать эффективные системы рендеринга.

## Дополнительные ресурсы

- [Quarkus Qute Guide](https://quarkus.io/guides/qute)
- [Qute Documentation](https://quarkus.io/guides/qute-reference)

