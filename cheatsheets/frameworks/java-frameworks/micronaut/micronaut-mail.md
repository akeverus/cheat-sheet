---
title: "Micronaut: Mail - Email Sending и Templates"
description: "Полное руководство по отправке email в Micronaut: email sending, templates, attachments, HTML email и best practices"
tags: ["micronaut", "mail", "email", "smtp", "templates", "java", "kotlin"]
difficulty: "intermediate"
prerequisites: ["micronaut/micronaut-basics.md", "micronaut/micronaut-http.md"]
next: ["micronaut-http.md", "micronaut-reactive.md"]
updated: "2026-02-11"
related: ["micronaut-http.md", "micronaut-reactive.md"]
---

# Micronaut: Mail - Email Sending и Templates



## Полезные ссылки

[Официальная документация Micronaut](https://docs.micronaut.io/)
[Micronaut GitHub](https://github.com/micronaut-projects/micronaut-core)

## Содержание

- [Micronaut: Mail - Email Sending и Templates](#micronaut-mail-email-sending-и-templates)
- [Введение](#введение)
  - [Основные возможности](#основные-возможности)
- [Настройка Mail](#настройка-mail)
  - [Зависимости](#зависимости)
  - [Конфигурация](#конфигурация)
- [Basic Email Sending](#basic-email-sending)
  - [Simple Email](#simple-email)
  - [Email with Multiple Recipients](#email-with-multiple-recipients)
- [HTML Email](#html-email)
  - [HTML Content](#html-content)
  - [Email with Plain Text and HTML](#email-with-plain-text-and-html)
- [Email Templates](#email-templates)
  - [Thymeleaf Template](#thymeleaf-template)
  - [Using Templates](#using-templates)
- [Attachments](#attachments)
  - [Email with Attachments](#email-with-attachments)
- [Async Email Sending](#async-email-sending)
  - [Async Email Service](#async-email-service)
- [Лучшие практики](#лучшие-практики)
  - [1. Используйте шаблоны для HTML email](#1-используйте-шаблоны-для-html-email)
  - [2. Обрабатывайте ошибки отправки](#2-обрабатывайте-ошибки-отправки)
  - [3. Используйте async для больших объемов](#3-используйте-async-для-больших-объемов)
  - [4. Валидируйте email адреса](#4-валидируйте-email-адреса)
  - [5. Используйте конфигурацию из properties](#5-используйте-конфигурацию-из-properties)
- [✅ Хорошо](#хорошо)
- [Email Queue](#email-queue)
  - [Queue-based Email Sending](#queue-based-email-sending)
- [Email Tracking](#email-tracking)
  - [Email Delivery Tracking](#email-delivery-tracking)
- [Email Templates with Variables](#email-templates-with-variables)
  - [Template Variables](#template-variables)
- [Email Batching](#email-batching)
  - [Batch Email Sending](#batch-email-sending)
- [Email Templates with Conditions](#email-templates-with-conditions)
  - [Conditional Templates](#conditional-templates)
- [Email Delivery Reports](#email-delivery-reports)
  - [Delivery Status](#delivery-status)
- [Email Rate Limiting](#email-rate-limiting)
  - [Rate Limiting Configuration](#rate-limiting-configuration)
  - [Rate Limited Email Service](#rate-limited-email-service)
- [Email Bounce Handling](#email-bounce-handling)
  - [Bounce Detection](#bounce-detection)
- [Заключение](#заключение)
- [Дополнительные ресурсы](#дополнительные-ресурсы)

## Введение

**Micronaut** предоставляет поддержку отправки **email** через **Micronaut Email**. Это позволяет отправлять простые и **HTML email** сообщения с вложениями и шаблонами.

### Основные возможности

- **Email Sending**: Отправка **email** сообщений
- **HTML Email**: Поддержка **HTML** контента
- **Templates**: Использование шаблонов
- **Attachments**: Вложения в **email**
- **Async Sending**: Асинхронная отправка

## Настройка Mail

### Зависимости

**build.gradle:**

```gradle
dependencies {
    implementation("io.micronaut.email:micronaut-email-javamail")
    implementation("io.micronaut.views:micronaut-views-thymeleaf")
}
```

### Конфигурация

**application.yml:**

```yaml
micronaut:
  mail:
    default:
      from: noreply@example.com
      host: smtp.gmail.com
      port: 587
      username: ${MAIL_USERNAME}
      password: ${MAIL_PASSWORD}
      properties:
        mail:
          smtp:
            auth: true
            starttls:
              enable: true
```

## Basic Email Sending

### **Simple Email**

```java
import io.micronaut.email.Email;
import io.micronaut.email.EmailSender;
import jakarta.inject.Singleton;

@Singleton
public class EmailService {
    private final EmailSender<?, ?> emailSender;
    
    public EmailService(EmailSender<?, ?> emailSender) {
        this.emailSender = emailSender;
    }
    
    public void sendSimpleEmail(String to, String subject, String body) {
        Email email = Email.builder()
            .to(to)
            .subject(subject)
            .body(body)
            .build();
        
        emailSender.send(email);
    }
}
```

### **Email with Multiple Recipients**

```java
import io.micronaut.email.Email;
import io.micronaut.email.EmailSender;
import jakarta.inject.Singleton;

@Singleton
public class EmailService {
    private final EmailSender<?, ?> emailSender;
    
    public void sendToMultipleRecipients(
            List<String> to,
            List<String> cc,
            List<String> bcc,
            String subject,
            String body) {
        Email email = Email.builder()
            .to(to.toArray(new String[0]))
            .cc(cc.toArray(new String[0]))
            .bcc(bcc.toArray(new String[0]))
            .subject(subject)
            .body(body)
            .build();
        
        emailSender.send(email);
    }
}
```

## HTML Email

### **HTML Content**

```java
import io.micronaut.email.Email;
import io.micronaut.email.EmailSender;
import jakarta.inject.Singleton;

@Singleton
public class HtmlEmailService {
    private final EmailSender<?, ?> emailSender;
    
    public void sendHtmlEmail(String to, String subject, String htmlBody) {
        Email email = Email.builder()
            .to(to)
            .subject(subject)
            .body(htmlBody)
            .build();
        
        emailSender.send(email);
    }
}
```

### **Email with Plain Text and HTML**

```java
import io.micronaut.email.Email;
import io.micronaut.email.EmailSender;
import jakarta.inject.Singleton;

@Singleton
public class MultipartEmailService {
    private final EmailSender<?, ?> emailSender;
    
    public void sendMultipartEmail(
            String to, 
            String subject, 
            String plainText, 
            String htmlBody) {
        Email email = Email.builder()
            .to(to)
            .subject(subject)
            .body(plainText)
            .htmlBody(htmlBody)
            .build();
        
        emailSender.send(email);
    }
}
```

## Email Templates

### **Thymeleaf Template**

**templates/email/`welcome.html`:**

```html
<!DOCTYPE html>
<html xmlns:th="http://www.thymeleaf.org">
<head>
    <title>Welcome</title>
</head>
<body>
    <h1>Welcome, <span th:text="${name}">User</span>!</h1>
    <p>Thank you for joining us.</p>
</body>
</html>
```

### **Using Templates**

```java
import io.micronaut.email.Email;
import io.micronaut.email.EmailSender;
import io.micronaut.views.ViewsRenderer;
import jakarta.inject.Singleton;
import java.util.Map;

@Singleton
public class TemplateEmailService {
    private final EmailSender<?, ?> emailSender;
    private final ViewsRenderer<Map<String, Object>, ?> viewsRenderer;
    
    public TemplateEmailService(
            EmailSender<?, ?> emailSender,
            ViewsRenderer<Map<String, Object>, ?> viewsRenderer) {
        this.emailSender = emailSender;
        this.viewsRenderer = viewsRenderer;
    }
    
    public void sendWelcomeEmail(String to, String name) {
        Map<String, Object> model = Map.of("name", name);
        String htmlBody = viewsRenderer.render("email/welcome", model, false)
            .orElse("");
        
        Email email = Email.builder()
            .to(to)
            .subject("Welcome!")
            .htmlBody(htmlBody)
            .build();
        
        emailSender.send(email);
    }
}
```

## Attachments

### **Email with Attachments**

```java
import io.micronaut.email.Attachment;
import io.micronaut.email.Email;
import io.micronaut.email.EmailSender;
import jakarta.inject.Singleton;
import java.io.File;

@Singleton
public class AttachmentEmailService {
    private final EmailSender<?, ?> emailSender;
    
    public void sendEmailWithAttachment(
            String to, 
            String subject, 
            String body, 
            File attachment) {
        Attachment emailAttachment = Attachment.builder()
            .filename(attachment.getName())
            .contentType("application/pdf")
            .content(attachment)
            .build();
        
        Email email = Email.builder()
            .to(to)
            .subject(subject)
            .body(body)
            .attachment(emailAttachment)
            .build();
        
        emailSender.send(email);
    }
}
```

## Async Email Sending

### **Async Email Service**

```java
import io.micronaut.email.Email;
import io.micronaut.email.EmailSender;
import io.micronaut.scheduling.annotation.Async;
import jakarta.inject.Singleton;
import java.util.concurrent.CompletableFuture;

@Singleton
public class AsyncEmailService {
    private final EmailSender<?, ?> emailSender;
    
    @Async
    public CompletableFuture<Void> sendEmailAsync(
            String to, 
            String subject, 
            String body) {
        Email email = Email.builder()
            .to(to)
            .subject(subject)
            .body(body)
            .build();
        
        emailSender.send(email);
        return CompletableFuture.completedFuture(null);
    }
}
```

## Лучшие практики

### 1. Используйте шаблоны для **HTML email**

```java
// ✅ Хорошо
String htmlBody = viewsRenderer.render("email/template", model, false)
    .orElse("");
```

### 2. Обрабатывайте ошибки отправки

```java
// ✅ Хорошо
try {
    emailSender.send(email);
} catch (EmailException e) {
    log.error("Failed to send email", e);
}
```

### 3. Используйте **async** для больших объемов

```java
// ✅ Хорошо
@Async
public CompletableFuture<Void> sendEmailAsync(...) {
    // Асинхронная отправка
}
```

### 4. Валидируйте **email** адреса

```java
// ✅ Хорошо
@Email
private String email;
```

### 5. Используйте конфигурацию из **properties**

```yaml
# ✅ Хорошо
micronaut:
  mail:
    default:
      from: noreply@example.com
      host: smtp.gmail.com
```

## Email Queue

### **Queue-based Email Sending**

```java
import io.micronaut.email.Email;
import io.micronaut.email.EmailSender;
import jakarta.inject.Singleton;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

@Singleton
public class QueuedEmailService {
    private final EmailSender<?, ?> emailSender;
    private final BlockingQueue<Email> emailQueue = new LinkedBlockingQueue<>();
    
    public QueuedEmailService(EmailSender<?, ?> emailSender) {
        this.emailSender = emailSender;
        startEmailProcessor();
    }
    
    public void queueEmail(Email email) {
        emailQueue.offer(email);
    }
    
    private void startEmailProcessor() {
        new Thread(() -> {
            while (true) {
                try {
                    Email email = emailQueue.take();
                    emailSender.send(email);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    break;
                } catch (Exception e) {
                    log.error("Failed to send queued email", e);
                }
            }
        }).start();
    }
}
```

## Email Tracking

### **Email Delivery Tracking**

```java
import io.micronaut.email.Email;
import io.micronaut.email.EmailSender;
import jakarta.inject.Singleton;

@Singleton
public class TrackedEmailService {
    private final EmailSender<?, ?> emailSender;
    private final EmailTrackingRepository trackingRepository;
    
    public TrackedEmailService(
            EmailSender<?, ?> emailSender,
            EmailTrackingRepository trackingRepository) {
        this.emailSender = emailSender;
        this.trackingRepository = trackingRepository;
    }
    
    public void sendTrackedEmail(String to, String subject, String body) {
        EmailTracking tracking = new EmailTracking();
        tracking.setRecipient(to);
        tracking.setSubject(subject);
        tracking.setStatus("PENDING");
        tracking = trackingRepository.save(tracking);
        
        try {
            Email email = Email.builder()
                .to(to)
                .subject(subject)
                .body(body)
                .build();
            
            emailSender.send(email);
            
            tracking.setStatus("SENT");
            tracking.setSentAt(LocalDateTime.now());
        } catch (Exception e) {
            tracking.setStatus("FAILED");
            tracking.setError(e.getMessage());
        } finally {
            trackingRepository.update(tracking);
        }
    }
}
```

## Email Templates with Variables

### **Template Variables**

```java
import io.micronaut.email.Email;
import io.micronaut.email.EmailSender;
import io.micronaut.views.ViewsRenderer;
import jakarta.inject.Singleton;
import java.util.Map;

@Singleton
public class TemplateEmailService {
    private final EmailSender<?, ?> emailSender;
    private final ViewsRenderer<Map<String, Object>, ?> viewsRenderer;
    
    public void sendWelcomeEmail(String to, String name, String activationLink) {
        Map<String, Object> model = Map.of(
            "name", name,
            "activationLink", activationLink,
            "supportEmail", "support@example.com"
        );
        
        String htmlBody = viewsRenderer.render("email/welcome", model, false)
            .orElse("");
        
        Email email = Email.builder()
            .to(to)
            .subject("Welcome to our service!")
            .htmlBody(htmlBody)
            .build();
        
        emailSender.send(email);
    }
}
```

## Email Batching

### **Batch Email Sending**

```java
import io.micronaut.email.Email;
import io.micronaut.email.EmailSender;
import jakarta.inject.Singleton;
import java.util.List;

@Singleton
public class BatchEmailService {
    private final EmailSender<?, ?> emailSender;
    
    public void sendBatchEmails(List<String> recipients, String subject, String body) {
        List<Email> emails = recipients.stream()
            .map(to -> Email.builder()
                .to(to)
                .subject(subject)
                .body(body)
                .build())
            .collect(Collectors.toList());
        
        emails.forEach(emailSender::send);
    }
}
```

## Email Templates with Conditions

### **Conditional Templates**

```java
import io.micronaut.email.Email;
import io.micronaut.email.EmailSender;
import io.micronaut.views.ViewsRenderer;
import jakarta.inject.Singleton;
import java.util.Map;

@Singleton
public class ConditionalTemplateService {
    private final EmailSender<?, ?> emailSender;
    private final ViewsRenderer<Map<String, Object>, ?> viewsRenderer;
    
    public void sendWelcomeEmail(String to, String name, String locale) {
        String templateName = "email/welcome-" + locale;
        Map<String, Object> model = Map.of("name", name);
        
        String htmlBody = viewsRenderer.render(templateName, model, false)
            .orElse(viewsRenderer.render("email/welcome", model, false).orElse(""));
        
        Email email = Email.builder()
            .to(to)
            .subject("Welcome!")
            .htmlBody(htmlBody)
            .build();
        
        emailSender.send(email);
    }
}
```

## Email Delivery Reports

### **Delivery Status**

```java
import io.micronaut.email.Email;
import io.micronaut.email.EmailSender;
import jakarta.inject.Singleton;

@Singleton
public class DeliveryReportService {
    private final EmailSender<?, ?> emailSender;
    
    public void sendEmailWithReport(String to, String subject, String body) {
        Email email = Email.builder()
            .to(to)
            .subject(subject)
            .body(body)
            .header("X-Delivery-Report", "true")
            .build();
        
        emailSender.send(email);
        // Обработка delivery report
    }
}
```

## Email Rate Limiting

### **Rate Limiting Configuration**

**application.yml:**

```yaml
micronaut:
  mail:
    rate-limit:
      enabled: true
      max-emails-per-minute: 100
```

### **Rate Limited Email Service**

```java
import io.micronaut.email.Email;
import io.micronaut.email.EmailSender;
import io.micronaut.ratelimit.annotation.RateLimited;
import jakarta.inject.Singleton;

@Singleton
public class RateLimitedEmailService {
    private final EmailSender<?, ?> emailSender;
    
    @RateLimited(limit = 100, duration = "1m")
    public void sendEmail(String to, String subject, String body) {
        Email email = Email.builder()
            .to(to)
            .subject(subject)
            .body(body)
            .build();
        
        emailSender.send(email);
    }
}
```

## Email Bounce Handling

### **Bounce Detection**

```java
import io.micronaut.email.Email;
import io.micronaut.email.EmailSender;
import jakarta.inject.Singleton;

@Singleton
public class BounceHandlingService {
    private final EmailSender<?, ?> emailSender;
    
    public void sendEmailWithBounceHandler(String to, String subject, String body) {
        Email email = Email.builder()
            .to(to)
            .subject(subject)
            .body(body)
            .header("Return-Path", "bounce@example.com")
            .build();
        
        emailSender.send(email);
        // Обработка bounce сообщений
    }
}
```




## Заключение

**Micronaut Mail** предоставляет простой и мощный **API** для отправки **email** сообщений. Поддержка простых и **HTML email**, шаблонов, вложений, асинхронной отправки, очередей, отслеживания доставки, **template variables**, **batch sending**, **conditional templates**, **delivery reports**, **rate limiting**, **bounce handling** и других продвинутых возможностей позволяет создавать профессиональные **email** системы.

## Дополнительные ресурсы

- [**Micronaut Email** Documentation](https://micronaut-projects.github.io/micronaut-mail/latest/guide/)
- [JavaMail API](https://javaee.github.io/javamail/)
- [Thymeleaf Documentation](https://www.thymeleaf.org/documentation.html)
- [**Email Best Practices**](https://www.baeldung.com/spring-email)
