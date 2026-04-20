---
title: "Quarkus: Mail — Отправка email"
description: "Полное руководство по отправке email в Quarkus: SMTP, HTML emails, attachments, templates и best practices"
tags:
  - quarkus
  - mail
  - smtp
  - email
  - messaging
  - java
difficulty: "intermediate"
prerequisites: ["quarkus/quarkus-basics.md", "quarkus/quarkus-core.md"]
next: ["quarkus-core.md", "quarkus-reactive.md"]
updated: "2026-04-20"
related: ["quarkus-core.md", "quarkus-reactive.md"]
---

# Quarkus: Mail — Отправка email

## Полезные ссылки

[Официальная документация Quarkus](https://quarkus.io/guides/)
[Quarkus GitHub](https://github.com/quarkusio/quarkus)

## Содержание

- [Введение](#введение)
  - [Основные возможности](#основные-возможности)
- [Configuration](#configuration)
  - [SMTP Configuration](#smtp-configuration)
  - [Advanced Configuration](#advanced-configuration)
- [Basic Email Sending](#basic-email-sending)
  - [Simple Text Email](#simple-text-email)
  - [HTML Email](#html-email)
- [Advanced Email Features](#advanced-email-features)
  - [Email with Attachments](#email-with-attachments)
  - [Multiple Recipients](#multiple-recipients)
- [Async Email Sending](#async-email-sending)
  - [Reactive Email](#reactive-email)
- [Email Templates](#email-templates)
  - [Qute Templates](#qute-templates)
- [Лучшие практики](#лучшие-практики)
  - [1. Используйте async для отправки](#1-используйте-async-для-отправки)
  - [2. Используйте шаблоны для HTML](#2-используйте-шаблоны-для-html)
  - [3. Обрабатывайте ошибки](#3-обрабатывайте-ошибки)
- [Email Headers](#email-headers)
  - [Custom Headers](#custom-headers)
  - [Reply-To Header](#reply-to-header)
- [Email Validation](#email-validation)
  - [Email Address Validation](#email-address-validation)
- [Batch Email Sending](#batch-email-sending)
  - [Bulk Email](#bulk-email)
- [Email Queue](#email-queue)
  - [Queued Email](#queued-email)
- [Email Templates with Qute](#email-templates-with-qute)
  - [Template-based Emails](#template-based-emails)
  - [Template Example](#template-example)
- [Email Queue Management](#email-queue-management)
  - [Queue-based Email Sending](#queue-based-email-sending)
  - [Email Delivery Tracking](#email-delivery-tracking)
- [Email Delivery Optimization](#email-delivery-optimization)
  - [Batch Email Sending](#batch-email-sending-1)
  - [Email Rate Limiting](#email-rate-limiting)
  - [Email Template Caching](#email-template-caching)
- [Заключение](#заключение)
- [Дополнительные ресурсы](#дополнительные-ресурсы)
- [См. также](#см-также)

## Введение

**Quarkus** предоставляет простую и мощную интеграцию для отправки **email** через **Jakarta Mail**. Это позволяет отправлять текстовые и **HTML** письма, с вложениями и использованием шаблонов.

### Основные возможности

- **SMTP Integration**: Отправка через **SMTP**
- **HTML Emails**: Поддержка **HTML** писем
- **Attachments**: Вложения в письма
- **Templates**: Использование шаблонов
- **Async Sending**: Асинхронная отправка

## Configuration

### SMTP Configuration

**application.properties:**

```properties
quarkus.mailer.host=smtp.example.com
quarkus.mailer.port=587
quarkus.mailer.username=user@example.com
quarkus.mailer.password=password
quarkus.mailer.from=no-reply@example.com
quarkus.mailer.ssl=false
quarkus.mailer.start-tls=true
```

### Advanced Configuration

```properties
quarkus.mailer.host=smtp.gmail.com
quarkus.mailer.port=465
quarkus.mailer.ssl=true
quarkus.mailer.auth-methods=DEFAULT
quarkus.mailer.trust-all=false
quarkus.mailer.max-pool-size=10
```

## Basic Email Sending

### Simple Text Email

**Отправка простого текстового письма:**

```java
import io.quarkus.mailer.Mail;
import io.quarkus.mailer.Mailer;
import jakarta.inject.Inject;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class EmailService {

    @Inject
    Mailer mailer;

    public void sendSimpleEmail(String to, String subject, String body) {
        mailer.send(Mail.withText(to, subject, body));
    }
}
```

### HTML Email

**Отправка **HTML** письма:**

```java
@ApplicationScoped
public class HtmlEmailService {

    @Inject
    Mailer mailer;

    public void sendHtmlEmail(String to, String subject, String htmlBody) {
        mailer.send(Mail.withHtml(to, subject, htmlBody));
    }
}
```

## Advanced Email Features

### Email with Attachments

**Письмо с вложениями:**

```java
import java.io.File;
import java.nio.file.Paths;

@ApplicationScoped
public class AttachmentEmailService {

    @Inject
    Mailer mailer;

    public void sendEmailWithAttachment(String to, String subject, String body) {
        Mail mail = Mail.withText(to, subject, body);
        mail.addAttachment("document.pdf",
            Paths.get("/path/to/document.pdf").toFile(),
            "application/pdf");
        mailer.send(mail);
    }
}
```

### Multiple Recipients

**Несколько получателей:**

```java
@ApplicationScoped
public class MultiRecipientService {

    @Inject
    Mailer mailer;

    public void sendToMultiple(String subject, String body) {
        Mail mail = Mail.withText("", subject, body);
        mail.addTo("user1@example.com");
        mail.addTo("user2@example.com");
        mail.addCc("cc@example.com");
        mail.addBcc("bcc@example.com");
        mailer.send(mail);
    }
}
```

## Async Email Sending

### Reactive Email

**Асинхронная отправка:**

```java
import io.smallrye.mutiny.Uni;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class AsyncEmailService {

    @Inject
    ReactiveMailer mailer;

    public Uni<Void> sendEmailAsync(String to, String subject, String body) {
        return mailer.send(Mail.withText(to, subject, body));
    }
}
```

## Email Templates

### Qute Templates

**Использование **Qute** шаблонов:**

```java
import io.quarkus.qute.Template;
import jakarta.inject.Inject;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class TemplateEmailService {

    @Inject
    Template welcome;

    @Inject
    Mailer mailer;

    public void sendWelcomeEmail(String to, String name) {
        String htmlBody = welcome.data("name", name).render();
        mailer.send(Mail.withHtml(to, "Welcome", htmlBody));
    }
}
```

**templates/`welcome.html`:**

```html
<html>
<body>
    <h1>Welcome, {name}!</h1>
    <p>Thank you for joining us.</p>
</body>
</html>
```

## Лучшие практики

### 1. Используйте async для отправки

```java
// ✅ Хорошо
public Uni<Void> sendEmailAsync(String to, String subject, String body) {
    return mailer.send(Mail.withText(to, subject, body));
}
```

### 2. Используйте шаблоны для HTML

```java
// ✅ Хорошо
String htmlBody = template.data("name", name).render();
mailer.send(Mail.withHtml(to, subject, htmlBody));
```

### 3. Обрабатывайте ошибки

```java
// ✅ Хорошо
mailer.send(mail)
    .onFailure().invoke(this::logError)
    .onFailure().recoverWithItem(() -> null);
```

## Email Headers

### Custom Headers

**Добавление кастомных заголовков:**

```java
@ApplicationScoped
public class HeaderEmailService {

    @Inject
    Mailer mailer;

    public void sendEmailWithHeaders(String to, String subject, String body) {
        Mail mail = Mail.withText(to, subject, body);
        mail.addHeader("X-Custom-Header", "value");
        mail.addHeader("X-Priority", "1");
        mailer.send(mail);
    }
}
```

### Reply-To Header

**Настройка **Reply-To**:**

```java
@ApplicationScoped
public class ReplyToService {

    @Inject
    Mailer mailer;

    public void sendWithReplyTo(String to, String subject, String body) {
        Mail mail = Mail.withText(to, subject, body);
        mail.setReplyTo("support@example.com");
        mailer.send(mail);
    }
}
```

## Email Validation

### Email Address Validation

**Валидация **email** адресов:**

```java
import jakarta.validation.constraints.Email;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class ValidatedEmailService {

    @Inject
    Mailer mailer;

    public void sendValidatedEmail(
            @Email String to,
            String subject,
            String body) {
        mailer.send(Mail.withText(to, subject, body));
    }
}
```

## Batch Email Sending

### Bulk Email

**Массовая отправка:**

```java
import io.smallrye.mutiny.Uni;
import java.util.List;

@ApplicationScoped
public class BulkEmailService {

    @Inject
    ReactiveMailer mailer;

    public Uni<Void> sendBulkEmails(List<String> recipients, String subject, String body) {
        return Uni.combine().all().unis(
            recipients.stream()
                .map(to -> mailer.send(Mail.withText(to, subject, body)))
                .toList()
        ).discardItems();
    }
}
```

## Email Queue

### Queued Email

**Очередь для отправки:**

```java
import jakarta.enterprise.event.Event;
import jakarta.inject.Inject;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class QueuedEmailService {

    @Inject
    Event<EmailEvent> emailEvent;

    public void queueEmail(String to, String subject, String body) {
        emailEvent.fireAsync(new EmailEvent(to, subject, body));
    }

    @ApplicationScoped
    public static class EmailProcessor {

        @Inject
        Mailer mailer;

        public void processEmail(@ObservesAsync EmailEvent event) {
            mailer.send(Mail.withText(event.getTo(), event.getSubject(), event.getBody()));
        }
    }
}
```

## Email Templates with Qute

### Template-based Emails

**Использование **Qute** шаблонов:**

```java
@ApplicationScoped
public class TemplateEmailService {

    @Inject
    Mailer mailer;

    @Inject
    Template welcome;

    public Uni<Void> sendWelcomeEmail(User user) {
        String htmlBody = welcome.data("user", user).render();
        return mailer.send(Mail.withHtml(user.getEmail(), "Welcome", htmlBody));
    }
}
```

### Template Example

**Пример шаблона:**

```html
<!-- templates/welcome.html -->
<!DOCTYPE html>
<html>
<head>
    <title>Welcome</title>
</head>
<body>
    <h1>Welcome {user.name}!</h1>
    <p>Your email: {user.email}</p>
</body>
</html>
```

## Email Queue Management

### Queue-based Email Sending

**Отправка через очередь:**

```java
@ApplicationScoped
public class EmailQueueService {

    private final Queue<EmailTask> emailQueue = new ConcurrentLinkedQueue<>();

    @Scheduled(every = "5s")
    void processEmailQueue() {
        EmailTask task = emailQueue.poll();
        if (task != null) {
            mailer.send(task.getMail())
                .onFailure().invoke(e -> {
                    // Retry logic
                    retryTask(task);
                });
        }
    }

    public void enqueueEmail(EmailTask task) {
        emailQueue.offer(task);
    }
}
```

### Email Delivery Tracking

**Отслеживание доставки:**

```java
@ApplicationScoped
public class EmailTrackingService {

    @Inject
    Mailer mailer;

    public Uni<String> sendTrackedEmail(String to, String subject, String body) {
        String trackingId = UUID.randomUUID().toString();

        Mail mail = Mail.withText(to, subject, body)
            .addHeader("X-Tracking-ID", trackingId);

        return mailer.send(mail)
            .onItem().transform(v -> trackingId)
            .onFailure().recoverWithItem(() -> null);
    }
}
```

## Email Delivery Optimization

### Batch Email Sending

**Массовая отправка **email**:**

```java
@ApplicationScoped
public class BatchEmailService {

    @Inject
    Mailer mailer;

    public Uni<Void> sendBatchEmails(List<String> recipients, String subject, String body) {
        List<Uni<Void>> emailTasks = recipients.stream()
            .map(to -> mailer.send(Mail.withText(to, subject, body)))
            .collect(Collectors.toList());

        return Uni.combine().all().unis(emailTasks)
            .combinedWith(results -> null);
    }
}
```

### Email Rate Limiting

**Ограничение частоты отправки:**

```java
@ApplicationScoped
public class RateLimitedEmailService {

    private final RateLimiter rateLimiter = RateLimiter.create(10.0); // 10 emails per second

    public Uni<Void> sendEmail(String to, String subject, String body) {
        rateLimiter.acquire();
        return mailer.send(Mail.withText(to, subject, body));
    }
}
```

### Email Template Caching

**Кеширование шаблонов:**

```java
@ApplicationScoped
public class CachedTemplateService {

    @Inject
    Template welcomeTemplate;

    private final Cache<String, String> templateCache =
        Caffeine.newBuilder()
            .expireAfterWrite(1, TimeUnit.HOURS)
            .build();

    public String getRenderedTemplate(User user) {
        String key = "welcome:" + user.getId();
        return templateCache.get(key, k ->
            welcomeTemplate.data("user", user).render()
        );
    }
}
```


## Заключение

**Quarkus Mail** предоставляет мощные инструменты для отправки **email**. Поддержка **SMTP**, **HTML** писем, вложений, шаблонов, асинхронной отправки, кастомных заголовков, валидации, массовой отправки и других продвинутых возможностей позволяет создавать эффективные системы отправки **email**. Правильное использование **email** паттернов, обработка ошибок, валидация и оптимизация производительности являются ключевыми аспектами создания надежных систем отправки **email**.

## Дополнительные ресурсы

- [**Quarkus Mail** Guide](https://quarkus.io/guides/mailer)
- [**Jakarta Mail** Specification](https://jakarta.ee/specifications/mail/)
- [**SMTP** Protocol (RFC 5321)](https://datatracker.ietf.org/doc/html/rfc5321)

## См. также

- [Quarkus: Actuator — Health Checks и Metrics](quarkus-actuator.md)
- [Quarkus: Основы](quarkus-basics.md)
- [Quarkus: Cache — Кеширование данных](quarkus-cache.md)
- [Quarkus: Cloud Native — Kubernetes, OpenShift и Service Mesh](quarkus-cloud.md)
- [Quarkus: Core — CDI, Bean Scopes и Configuration](quarkus-core.md)
