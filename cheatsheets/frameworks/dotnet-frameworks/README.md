---
title: ".NET Frameworks"
description: "Точка входа в раздел экосистемы .NET: ASP.NET Core, EF Core, Blazor, MAUI, CLI, тесты и деплой."
tags:
  - meta
  - index
  - dotnet-frameworks
type: "index"
aliases:
  - ".NET Frameworks"
prerequisites: []
next: []
updated: "2026-04-20"
---
# .NET Frameworks

Раздел собирает ключевые фреймворки и инструменты платформы .NET для бэкенда, desktop и mobile: ASP.NET Core (веб/REST/gRPC), Entity Framework Core (ORM), Blazor (SPA/SSR), MAUI (cross-platform UI), NuGet и CLI. Материалы ориентированы на C# и F# разработчиков, которые приходят со стороны JVM и хотят сопоставить концепции.

Для кого: инженеры, поддерживающие полиглотные системы, и бэкенд-разработчики, оценивающие .NET как альтернативу Spring/Node/Go.

## Полезные ссылки

### Основные документы
- [.NET Frameworks — обзор](dotnet-frameworks-overview.md) — ASP.NET Core, EF Core, Blazor, MAUI, CLI

### Соседние разделы
- [Frameworks](../../basics/README.md) — корневой индекс фреймворков
- [Java Frameworks](../../basics/README.md) — для кросс-платформенных сравнений
- [Go Frameworks](../../basics/README.md)
- [Python Frameworks](../../basics/README.md)

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

- Если нужен Java-аналог — [Java Frameworks](../../basics/README.md)
- Общие практики backend — [development/web-backend](../../basics/README.md)
- Контейнеризация — [platform/containers](../../basics/README.md)
