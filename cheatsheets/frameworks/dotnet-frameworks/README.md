---
title: ".NET Frameworks"
description: "Точка входа в раздел экосистемы .NET: ASP.NET Core, EF Core, Blazor, MAUI, CLI, тесты и деплой."
tags:
  - meta
  - index
  - dotnet-frameworks
type: "index"
updated: "2026-04-17"
---
# .NET Frameworks

Раздел собирает ключевые фреймворки и инструменты платформы .NET для бэкенда, desktop и mobile: ASP.NET Core (веб/REST/gRPC), Entity Framework Core (ORM), Blazor (SPA/SSR), MAUI (cross-platform UI), NuGet и CLI. Материалы ориентированы на C# и F# разработчиков, которые приходят со стороны JVM и хотят сопоставить концепции.

Для кого: инженеры, поддерживающие полиглотные системы, и бэкенд-разработчики, оценивающие .NET как альтернативу Spring/Node/Go.

## Полезные ссылки

### Основные документы
- [[dotnet-frameworks-overview|.NET Frameworks — обзор]] — ASP.NET Core, EF Core, Blazor, MAUI, CLI

### Соседние разделы
- [[README|Frameworks]] — корневой индекс фреймворков
- [[README|Java Frameworks]] — для кросс-платформенных сравнений
- [[README|Go Frameworks]]
- [[README|Python Frameworks]]

### Внешние ресурсы
- [.NET Documentation](https://learn.microsoft.com/en-us/dotnet/)
- [ASP.NET Core](https://learn.microsoft.com/en-us/aspnet/core/)
- [Entity Framework Core](https://learn.microsoft.com/en-us/ef/core/)
- [.NET Blog](https://devblogs.microsoft.com/dotnet/)
- [NuGet Gallery](https://www.nuget.org/)

## Содержание

- [Что внутри](#что-внутри)
- [Когда выбирать .NET](#когда-выбирать-net)
- [Сравнение с JVM-стеком](#сравнение-с-jvm-стеком)
- [Маршруты чтения](#маршруты-чтения)
- [Куда идти дальше](#куда-идти-дальше)

## Что внутри

- Обзор платформы .NET (runtime, BCL, CLI)
- ASP.NET Core: MVC, Minimal API, gRPC
- Entity Framework Core: Code-First, миграции, LINQ
- Blazor: Server vs WebAssembly
- MAUI: cross-platform UI
- Тестирование (xUnit, NUnit) и деплой (Docker, Kubernetes)

## Когда выбирать .NET

- Команда сильна в C#/F# или уже работает с Windows-инфраструктурой.
- Нужен предсказуемый AOT-компилятор (Native AOT в .NET 8+) с низким cold start.
- Интеграция с Azure, Microsoft Graph, Office/Exchange.
- Альтернативы: Spring Boot (JVM), FastAPI (Python), Go (gin/echo) — если экосистема Microsoft не обязательна.

## Сравнение с JVM-стеком

| Область | .NET | JVM |
|---------|------|-----|
| Web framework | ASP.NET Core | Spring Boot, Quarkus |
| ORM | EF Core | Hibernate, jOOQ |
| DI | Встроенный (Microsoft.Extensions.DependencyInjection) | Spring IoC, CDI |
| SPA/UI | Blazor | Vaadin, Thymeleaf |
| AOT | Native AOT | GraalVM (Quarkus/Micronaut) |
| Сборка | dotnet CLI | Gradle, Maven |

## Маршруты чтения

- **Быстрый обзор (30 мин):** `dotnet-frameworks-overview.md` — целиком.
- **Backend-инженер, сравнивающий со Spring:** раздел ASP.NET Core + EF Core + секция сравнения.
- **Архитектор:** блоки «Развёртывание и контейнеры», «Тестирование» в overview.

## Куда идти дальше

- Если нужен Java-аналог — [[README|Java Frameworks]]
- Общие практики backend — [[README|development/web-backend]]
- Контейнеризация — [[README|platform/containers]]
