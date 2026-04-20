---
title: ".NET Frameworks — обзор"
description: "Кратко: обзор экосистемы .NET: ASP.NET Core, Entity Framework Core, Blazor, MAUI, веб-API, CLI, инструменты и практики. Для разработчиков на C# и F#."
tags:
  - frameworks
  - dotnet-frameworks
  - dotnet-frameworks-overview
difficulty: "intermediate"
prerequisites: []
next: []
updated: "2026-04-20"
---
# .NET Frameworks — обзор

Кратко: обзор экосистемы **.NET**: **ASP.NET Core**, **Entity Framework Core**, **Blazor**, **MAUI**, веб-**API**, **CLI**, инструменты и практики. Для разработчиков на **C#** и **F#**.

## Полезные ссылки

### Официальная документация
- [.NET Documentation](https://learn.microsoft.com/en-us/dotnet/) — официальная документация
- [ASP.NET Core](https://learn.microsoft.com/en-us/aspnet/core/) — веб-фреймворк
- [Entity Framework Core](https://learn.microsoft.com/en-us/ef/core/) — **ORM**

### Ресурсы
- [.NET Blog](https://devblogs.microsoft.com/dotnet/) — анонсы и гайды
- [NuGet](https://www.nuget.org/) — пакеты

### См. также
- [[README|Frameworks README]] — раздел фреймворков
- [[README|Java Frameworks]] — **Spring**, **Quarkus**, **Micronaut**

- [[python-frameworks-overview|Python Frameworks — обзор]]
- [[kotlin-frameworks-overview|Kotlin Frameworks — обзор]]
- [[go-frameworks-overview|Go Frameworks — обзор (редирект)]]
- [[scala-frameworks-overview|Scala Frameworks — обзор]]
## Содержание

- [Введение](#введение)
- [Платформа .NET](#платформа-net)
- [ASP.NET Core](#aspnet-core)
- [Entity Framework Core](#entity-framework-core)
- [Blazor и MAUI](#blazor-и-maui)
- [CLI и инструменты](#cli-и-инструменты)
- [Тестирование](#тестирование)
- [Развёртывание и контейнеры](#развёртывание-и-контейнеры)
- [Лучшие практики](#лучшие-практики)
- [Решение проблем](#решение-проблем)
- [Частые вопросы](#частые-вопросы)
- [Глоссарий](#глоссарий)
- [Итоговые таблицы](#итоговые-таблицы)
- [Заключение](#заключение)

## Введение

**.NET** — кроссплатформенная экосистема от **Microsoft**: runtime (CLR), языки (C#, F#, VB.NET), библиотеки и фреймворки. **ASP.NET Core** — веб-фреймворк для **API** и **MVC**; **Entity Framework Core** — **ORM**; **Blazor** — **UI** на **C#** (WebAssembly или Server); **MAUI** — кроссплатформенные нативные приложения. Документ даёт обзор основных компонентов, установки, базового использования и лучших практик.

**Ключевые понятия:** **Middleware**, **Dependency Injection**, **Kestrel**, **DbContext**, **Razor**, **SignalR**, **LINQ**, **NuGet**, **MSBuild**, **dotnet CLI**.


## Платформа .NET

**.NET** (ранее .NET Core) — открытая, кроссплатформенная платформа. Нумерация с **.NET 5** унифицирована (нет .NET Core 4). Текущие **LTS**: **.NET 6**, **.NET 8**. **SDK** включает runtime, компиляторы, **CLI**, шаблоны проектов.

**Основные компоненты:**
- **CLR** (Common Language Runtime) — управляемый runtime, **GC**, **JIT**.
- **BCL** (Base Class Library) — коллекции, **IO**, рефлексия, **async/await**.
- **Языки:** **C#**, **F#**, **VB.NET** (в основном C# в современных проектах).

**Установка (пример на Ubuntu):**

```bash
# Установка .NET SDK на Ubuntu 22.04
wget https://packages.microsoft.com/config/ubuntu/22.04/packages-microsoft-prod.deb -O packages-microsoft-prod.deb
sudo dpkg -i packages-microsoft-prod.deb
sudo apt-get update && sudo apt-get install -y dotnet-sdk-8.0
dotnet --version
```

**Создание проекта:**

```bash
# Создание и запуск Web API проекта
dotnet new webapi -n MyApi -o MyApi
cd MyApi
dotnet run
```


## ASP.NET Core

**ASP.NET Core** — модульный веб-фреймворк: **Middleware** pipeline, **Dependency Injection**, конфигурация, **Kestrel** как веб-сервер. Поддержка **REST API**, **MVC**, **Razor Pages**, **SignalR**. Развёртывание на **Windows**, **Linux**, **macOS**; контейнеры **Docker**.

**Минимальный API (пример):**

```csharp
var builder = WebApplication.CreateBuilder(args);
var app = builder.Build();
app.MapGet("/", () => "Hello");
app.MapGet("/api/items/{id}", (int id) => new { Id = id, Name = $"Item {id}" });
app.Run();
```

**Middleware:** порядок важен. Типичная цепочка: **Exception Handling** **HTTPS Redirection** **Static Files** **Routing** **CORS** **Authentication** **Authorization** **Endpoints**.

**Регистрация сервисов:**

```csharp
builder.Services.AddControllers();
builder.Services.AddEndpointsApiExplorer();
builder.Services.AddSwaggerGen();
```

**Конфигурация:** из `appsettings.json`, переменных окружения, **User Secrets** (разработка). Доступ через `IConfiguration` и **Options** pattern (`IOptions<T>`).


## Entity Framework Core

**EF Core** — **ORM** для **.NET**: **Code First** / **Database First**, миграции, **LINQ** к сущностям, поддержка **SQL Server**, **PostgreSQL**, **SQLite**, **MySQL** и др. **DbContext**, **DbSet**, конфигурация моделей.

**Регистрация:**

```csharp
builder.Services.AddDbContext<AppDbContext>(options =>
    options.UseNpgsql(connectionString));
```

**Модель и миграции:**
```bash
dotnet ef migrations add InitialCreate
dotnet ef database update
```

**Запросы:** **LINQ** преобразуется в **SQL**; осторожно с **N+1** — использовать **Include** / **ThenInclude** или проекции.


## Blazor и MAUI

**Blazor** — **SPA** на **C#**: **Blazor Server** (SignalR, логика на сервере) или **Blazor WebAssembly** (выполнение в браузере). Компоненты **Razor** (`.razor`), привязка данных, события.

**.NET MAUI** (.NET Multi-platform App UI) — один код для **iOS**, **Android**, **Windows**, **macOS**. Замена **Xamarin.Forms**. **XAML** или **C#** для **UI**.

**Когда что выбирать:** **Blazor Server** — быстрый старт, меньше трафика; **Blazor WebAssembly** — офлайн, клиентская логика; **MAUI** — нативные приложения с общим кодом.


## CLI и инструменты

**dotnet CLI** — создание проектов, сборка, публикация, тесты, миграции **EF**.

| Команда | Описание |
|---------|----------|
| `dotnet new` | Шаблоны (webapi, mvc, blazor, console, xunit) |
| `dotnet build` | Сборка |
| `dotnet run` | Запуск |
| `dotnet test` | Запуск тестов |
| `dotnet publish` | Публикация для развёртывания |
| `dotnet add package` | Добавить пакет **NuGet** |
| `dotnet ef` | **Entity Framework Core** CLI (миграции, скрипты) |

**NuGet:** восстановление пакетов при `dotnet restore` (или автоматически при `build`). Источники: nuget.org, приватные feed.


## Тестирование

**xUnit**, **NUnit**, **MSTest** — юнит-тесты. **ASP.NET Core** — интеграционные тесты с `WebApplicationFactory`.

```csharp
public class UnitTest1
{
    [Fact]
    public void Test1() => Assert.True(1 + 1 == 2);
}
```

**Moq**, **NSubstitute** — моки. **FluentAssertions** — читаемые утверждения. **Testcontainers** — интеграция с БД в контейнерах.


## Развёртывание и контейнеры

**Публикация:**

```bash
dotnet publish -c Release -o ./publish
```

**Docker:** образы на базе `mcr.microsoft.com/dotnet/aspnet` (runtime) и `mcr.microsoft.com/dotnet/sdk` (сборка). Многоэтапная сборка: этап `build` с **SDK**, этап `publish` с **runtime**.

**Kubernetes**, **Azure App Service**, **AWS**, **Linux**-хосты — типичные цели развёртывания.


## Лучшие практики

- Использовать **DI** для сервисов; конфигурацию — из **IConfiguration** и переменных окружения.
- **EF Core**: миграции в **CI**; не хранить секреты в коде; учитывать **N+1**.
- **API**: версионирование, **Swagger**/ **OpenAPI**, валидация и единообразная обработка ошибок.
- **Health checks**: `AddHealthChecks()`, **UI** или endpoint для оркестраторов.
- Логирование: **ILogger**, структурированные логи; не логировать чувствительные данные.


## Решение проблем

| Проблема | Возможная причина | Действие |
|----------|-------------------|----------|
| Ошибка миграции **EF** | Неверная строка подключения, расхождение с БД | Проверить connection string; откатить миграцию при необходимости |
| **Kestrel** не слушает порт | Неверные **urls** или **binding** | Проверить `ASPNETCORE_URLS`, конфиг в production |
| 404 на **API** | Маршрутизация, порядок **Middleware** | Проверить `MapControllers`, атрибуты маршрутов |
| Высокое потребление памяти | Утечки, кэши без ограничений | Профилирование, ограничить размер кэшей |


## Частые вопросы

**Чем .NET Core отличается от .NET Framework?** **.NET Core** (теперь .NET 5+) — кроссплатформенный, открытый. **.NET Framework** — только **Windows**, legacy. Для новых проектов используют **.NET** (Core).

**ASP.NET Core vs ASP.NET?** **ASP.NET Core** — кроссплатформенный, модульный, один стек для **API** и **MVC**. **ASP.NET** (Framework) — только **Windows**, привязан к **IIS**.

**Когда использовать Blazor Server, когда WebAssembly?** **Blazor Server** — меньше загрузка на клиент, быстрый старт; задержка и нагрузка на сервер. **WebAssembly** — работа офлайн, тяжёлая логика на клиенте; первая загрузка больше.


## Глоссарий

| Термин | Описание |
|--------|----------|
| **CLR** | **Common Language Runtime** — runtime выполнения управляемого кода |
| **BCL** | **Base Class Library** — базовые классы .NET |
| **Kestrel** | Кроссплатформенный веб-сервер по умолчанию в **ASP.NET Core** |
| **Middleware** | Компоненты в конвейере обработки запроса |
| **DbContext** | Контекст **EF Core** для работы с БД |
| **LINQ** | **Language Integrated Query** — запросы к коллекциям и **IQueryable** |
| **NuGet** | Система пакетов для .NET |
| **Razor** | Синтаксис разметки + **C#** в **Blazor** / **MVC** |
| **SignalR** | Библиотека для real-time веб-функций |
| **MAUI** | .NET Multi-platform App UI — кроссплатформенный UI для мобильных и десктопов |


## Итоговые таблицы

| Компонент | Назначение |
|-----------|------------|
| **ASP.NET Core** | Веб-**API**, **MVC**, **Razor Pages**, **SignalR** |
| **EF Core** | **ORM**, миграции, **LINQ** к БД |
| **Blazor** | **SPA** на **C#** (Server или WebAssembly) |
| **MAUI** | Нативные приложения **iOS**/ **Android**/ **Windows**/ **macOS** |
| **dotnet CLI** | Сборка, тесты, публикация, **EF** миграции |


## Заключение

Экосистема **.NET** предоставляет **ASP.NET Core**, **EF Core**, **Blazor**, **MAUI** для веб-приложений, данных и кроссплатформенного **UI**. Используйте **DI**, конфигурацию из окружения, миграции в **CI** и единообразное оформление **API**. См. [[README|Frameworks README]].
