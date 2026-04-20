---
title: "xUnit.net"
description: "Кратко: фреймворк для тестирования приложений на .NET (C#, F#, VB.NET). Модель «один экземпляр класса на тест», атрибуты [Fact] и [Theory], параметризация через [InlineData] и др., фикстуры через IClassFixture<T>, встроенные ассерты, параллельный запуск по умолчанию. Поддержка .N"
tags:
  - testing
  - unit-testing
  - xunit
difficulty: "intermediate"
prerequisites: []
next: []
updated: "2026-04-20"
---
# xUnit.net

**Кратко:** фреймворк для тестирования приложений на **.NET** (C#, F#, VB.NET). Модель «один экземпляр класса на тест», атрибуты `[Fact]` и `[Theory]`, параметризация через `[InlineData]` и др., фикстуры через `IClassFixture<T>`, встроенные ассерты, параллельный запуск по умолчанию. Поддержка .NET Core / .NET 5+, интеграция с Visual Studio и `dotnet test`.

**Дата:** 2026-02-06

## Полезные ссылки

| Тип | Ссылка |
|-----|--------|
| Документация | [xUnit.net — Getting Started](https://xunit.net/docs/getting-started/netcore/cmdline), [Writing Tests](https://xunit.net/docs/writing-tests), [Running Tests](https://xunit.net/docs/running-tests-in-visual-studio) |
| Сравнение | [xUnit vs NUnit vs MSTest](https://xunit.net/docs/comparisons) |
| Ассерты | [FluentAssertions](https://fluentassertions.com/) |
| См. также | [Unit Testing](../), [junit](../junit/junit.md), [Testing Tools](../../testing-tools/testing-tools-overview.md) |

## Содержание

- [Введение](#введение)
- [Установка и настройка](#установка-и-настройка)
- [Базовое использование](#базовое-использование)
  - [Простой тест ([Fact])](#простой-тест-fact)
- [Факты и теории](#факты-и-теории)
  - [[Fact] — один сценарий](#fact-один-сценарий)
  - [[Theory] и [InlineData] — параметризация](#theory-и-inlinedata-параметризация)
- [Фикстуры](#фикстуры)
  - [IClassFixture<T>](#iclassfixturet)
  - [Общая фикстура для нескольких классов (ICollectionFixture)](#общая-фикстура-для-нескольких-классов-icollectionfixture)
- [Ассерты](#ассерты)
- [Параллельность и изоляция](#параллельность-и-изоляция)
- [Расширенные сценарии](#расширенные-сценарии)
  - [ASP.NET Core (WebApplicationFactory)](#aspnet-core-webapplicationfactory)
  - [Моки (Moq)](#моки-moq)
  - [FluentAssertions](#fluentassertions)
- [Интеграция с CI/CD](#интеграция-с-cicd)
- [Лучшие практики](#лучшие-практики)
- [FAQ и решение проблем](#faq-и-решение-проблем)
  - [Частые вопросы](#частые-вопросы)
  - [Таблица: симптомы и действия](#таблица-симптомы-и-действия)
- [Справочник](#справочник)
  - [Атрибуты](#атрибуты)
  - [Частые ассерты](#частые-ассерты)
  - [xunit.runner.json](#xunitrunnerjson)
  - [Глоссарий](#глоссарий)
- [Заключение](#заключение)

## Введение

**xUnit.net** — фреймворк модульного и интеграционного тестирования для .NET. Отличия от NUnit/MSTest:

- **Изоляция** — новый экземпляр тестового класса на каждый тест (нет общего состояния через поля).
- **Параллельность** — тесты по умолчанию выполняются параллельно.
- **Минимум атрибутов** — `[Fact]`, `[Theory]`, `[InlineData]`, фикстуры через `IClassFixture<T>`.

Основные концепции:

| Концепция | Описание |
|-----------|----------|
| **Fact** | Один сценарий, метод без параметров, атрибут `[Fact]`. |
| **Theory** | Параметризованный тест: один метод, несколько наборов данных (`[InlineData]`, `[MemberData]`, `[ClassData]`). |
| **Fixture** | Общий ресурс для класса: `IClassFixture<T>`, внедрение через конструктор. |
| **Assert** | Проверки: `Assert.Equal`, `Assert.True`, `Assert.Throws` и др. |


## Установка и настройка

**Требования:** .NET SDK 6.0+ (рекомендуется .NET 8).

Создание проекта:

```bash
dotnet new xunit -n MyProject.Tests -o tests/MyProject.Tests
cd tests/MyProject.Tests
```

В существующем проекте:

```bash
dotnet add package xunit
dotnet add package xunit.runner.visualstudio
dotnet add package Microsoft.NET.Test.Sdk
```

Проверка: `dotnet test` — должен запуститься шаблонный тест.

Минимальный `.csproj` для тестов:

```xml
<PropertyGroup>
  <TargetFramework>net8.0</TargetFramework>
  <IsTestProject>true</IsTestProject>
</PropertyGroup>
<ItemGroup>
  <PackageReference Include="Microsoft.NET.Test.Sdk" Version="17.8.0" />
  <PackageReference Include="xunit" Version="2.6.2" />
  <PackageReference Include="xunit.runner.visualstudio" Version="2.5.4" />
</ItemGroup>
```


## Базовое использование

### Простой тест ([Fact])

```csharp
using Xunit;

public class CalculatorTests
{
    [Fact]
    public void Add_TwoNumbers_ReturnsSum()
    {
        var calculator = new Calculator();
        var result = calculator.Add(2, 3);
        Assert.Equal(5, result);
    }
}
```

Именование: **Method_Scenario_ExpectedResult** (или Given_When_Then). Для отчётов: `[Fact(DisplayName = "Сложение двух чисел возвращает сумму")]`.

Запуск:

```bash
dotnet test
dotnet test --filter "FullyQualifiedName~CalculatorTests"
dotnet test --logger "console;verbosity=detailed"
```


## Факты и теории

### [Fact] — один сценарий

Метод без параметров. Пример проверки исключения:

```csharp
[Fact]
public void Divide_ByZero_Throws()
{
    var calculator = new Calculator();
    Assert.Throws<DivideByZeroException>(() => calculator.Divide(1, 0));
}
```

### [Theory] и [InlineData] — параметризация

Один метод, несколько наборов данных:

```csharp
[Theory]
[InlineData(1, 2, 3)]
[InlineData(0, 0, 0)]
[InlineData(-1, 1, 0)]
public void Add_TwoNumbers_ReturnsSum(int a, int b, int expected)
{
    var calculator = new Calculator();
    Assert.Equal(expected, calculator.Add(a, b));
}
```

Данные из кода:

- **MemberData** — метод или свойство возвращают `IEnumerable<object[]>`; указать `[MemberData(nameof(GetData))]`.
- **ClassData** — отдельный класс, реализующий `IEnumerable<object[]>`; указать `[ClassData(typeof(TestDataClass))]`.

Пропуск и категории:

```csharp
[Fact(Skip = "Временно отключён")]
public void SkippedTest() { }

[Fact(Trait = "Category", "Integration")]
public void IntegrationTest() { }
```

Запуск по категории: `dotnet test --filter "Category=Integration"`.


## Фикстуры

### IClassFixture<T>

Общий ресурс для всех тестов в классе; создаётся один раз, передаётся в конструктор. Для очистки фикстура может реализовывать `IDisposable`.

```csharp
public class DatabaseFixture : IDisposable
{
    public DatabaseFixture() { /* подключение к БД */ }
    public void Dispose() => GC.SuppressFinalize(this);
}

public class MyTests : IClassFixture<DatabaseFixture>
{
    private readonly DatabaseFixture _fixture;

    public MyTests(DatabaseFixture fixture) => _fixture = fixture;

    [Fact]
    public void Test1() { /* использование _fixture */ }
}
```

### Общая фикстура для нескольких классов (ICollectionFixture)

Определить коллекцию и пометить классы одной коллекцией:

```csharp
[CollectionDefinition("Database")]
public class DatabaseCollection : ICollectionFixture<DatabaseFixture> { }

[Collection("Database")]
public class TestsA : IClassFixture<DatabaseFixture>
{
    public TestsA(DatabaseFixture fixture) { }
    [Fact] public void Test1() { }
}
```

Фикстура создаётся один раз на коллекцию. В тестовом классе можно реализовать `IDisposable` — `Dispose` вызывается после каждого теста (очистка ресурсов экземпляра).


## Ассерты

Базовые:

```csharp
Assert.Equal(expected, actual);
Assert.NotEqual(notExpected, actual);
Assert.True(condition);
Assert.False(condition);
Assert.Null(obj);
Assert.NotNull(obj);
Assert.Same(expected, actual);
Assert.Empty(collection);
Assert.NotEmpty(collection);
Assert.Contains(item, collection);
Assert.DoesNotContain(item, collection);
Assert.InRange(value, low, high);
```

Исключения:

```csharp
var ex = Assert.Throws<ArgumentException>(() => service.DoBad());
Assert.Equal("expected message", ex.Message);
Assert.ThrowsAny<Exception>(() => service.Do());
```

Коллекции и строки:

```csharp
Assert.All(collection, item => Assert.True(item > 0));
Assert.Collection(collection, item => Assert.Equal(1, item), ...);
Assert.Contains("sub", "hello world");
Assert.Matches(@"\d+", "123");
```

Сообщение при падении: `Assert.True(condition, "Причина: {0}", reason);`


## Параллельность и изоляция

- **Параллельность:** по умолчанию тесты выполняются параллельно (на уровне коллекций). Отключение — через конфигурацию.
- **Изоляция:** для каждого тестового метода создаётся новый экземпляр класса; общее состояние только через фикстуры.

Отключение параллелизма: создать `xunit.runner.json` в корне тестового проекта:

```json
{
  "parallelizeAssembly": false,
  "parallelizeTestCollections": false,
  "maxParallelThreads": 1
}
```

В `.csproj`:

```xml
<ItemGroup>
  <None Update="xunit.runner.json">
    <CopyToOutputDirectory>PreserveNewest</CopyToOutputDirectory>
  </None>
</ItemGroup>
```

Для последовательного запуска одного класса — поместить его в отдельную коллекцию (без фикстуры) и при необходимости ограничить параллелизм в конфиге.


## Расширенные сценарии

### ASP.NET Core (WebApplicationFactory)

```csharp
public class ApiTests : IClassFixture<WebApplicationFactory<Program>>
{
    private readonly WebApplicationFactory<Program> _factory;

    public ApiTests(WebApplicationFactory<Program> factory) => _factory = factory;

    [Fact]
    public async Task Get_ReturnsOk()
    {
        var client = _factory.CreateClient();
        var response = await client.GetAsync("/api/values");
        response.EnsureSuccessStatusCode();
    }
}
```

### Моки (Moq)

```bash
dotnet add package Moq
```

```csharp
[Fact]
public void TestWithMock()
{
    var mock = new Mock<IService>();
    mock.Setup(s => s.GetValue()).Returns(42);
    var sut = new Consumer(mock.Object);
    Assert.Equal(42, sut.Compute());
}
```

### FluentAssertions

```csharp
result.Should().Be(5);
list.Should().HaveCount(3).And.Contain(1);
ex.Should().BeOfType<ArgumentException>().Which.Message.Should().Contain("invalid");
```

Асинхронные тесты: метод с `async Task` и `await` — поддерживается без дополнительных атрибутов.


## Интеграция с CI/CD

```bash
dotnet test --configuration Release --logger "trx;LogFileName=results.trx" --collect:"XPlat Code Coverage"
```

Результаты TRX и отчёты покрытия публикуются в Azure DevOps, GitHub Actions, Jenkins. В GitHub Actions — задача `dotnet test` и шаг публикации TRX (например, `dorny/test-reporter` с `reporter: dotnet-trx`). В Azure DevOps — задача .NET Core `test` и «Publish Test Results» по TRX.


## Лучшие практики

- **Именование:** Method_Scenario_ExpectedResult или Given_When_Then.
- **Один assertion на тест** — где возможно, для ясности.
- **Theory + InlineData** — несколько сценариев без дублирования кода.
- **Тяжёлые ресурсы** — в `IClassFixture` (БД, HTTP-клиент), не создавать в каждом тесте.
- **Избегать общего изменяемого состояния** — при фикстурах учитывать потокобезопасность при параллельном запуске.
- **IDisposable** — в фикстуре или тестовом классе для очистки ресурсов.
- **Трейты** — `[Trait("Category", "Integration")]` для выборочного запуска в CI.
- **FluentAssertions** — для сложных и читаемых проверок.


## FAQ и решение проблем

### Частые вопросы

**Чем xUnit отличается от NUnit и MSTest?**
Новый экземпляр класса на каждый тест (изоляция); нет [SetUp]/[TearDown] — подготовка в конструкторе, очистка через `IDisposable`; параллельный запуск по умолчанию.

**IClassFixture vs ICollectionFixture?**
`IClassFixture` — ресурс для одного тестового класса. `ICollectionFixture` — общий ресурс для нескольких классов с одной коллекцией `[Collection("Name")]` (например, одна БД на несколько классов).

**Как параметры из файла?**
`[MemberData]` или `[ClassData]`, где метод/класс читает CSV/JSON и возвращает `IEnumerable<object[]>`.

**Поддержка .NET Framework?**
Да (например, `net48`); пакеты xunit и xunit.runner.visualstudio поддерживают .NET Framework.

**Запуск одного теста:**
`dotnet test --filter "FullyQualifiedName~TestMethodName"` или Run Test из IDE.

**Отключить параллелизм для одного класса:**
Поместить класс в отдельную коллекцию; в `xunit.runner.json` задать `parallelizeTestCollections: false` или ограничить `maxParallelThreads`.

### Таблица: симптомы и действия

| Симптом | Возможная причина | Действие |
|---------|-------------------|----------|
| Тесты не обнаруживаются | Не тестовый проект или нет пакетов | Добавить `Microsoft.NET.Test.Sdk`, `xunit`, `xunit.runner.visualstudio`; `IsTestProject=true` |
| «Объект не создан» в тесте | Фикстура не внедрена | Проверить `IClassFixture<T>` на классе и параметр конструктора типа `T` |
| Нестабильные падения при параллельном запуске | Общее состояние в фикстуре | Сделать фикстуру потокобезопасной или отключить параллелизм для коллекции |
| [Theory] не получает данные | Ошибка в [MemberData]/[ClassData] | Имя — `nameof(MethodOrProperty)`; метод возвращает `IEnumerable<object[]>` |
| TRX не создаётся | Не указан logger | Запуск с `--logger "trx;LogFileName=results.trx"` |
| Нет отчёта покрытия | Нет коллектора | Добавить `coverlet.collector`, запуск с `--collect:"XPlat Code Coverage"` |
| Тест пропускается без явного Skip | Условие или исключение до Assert | Проверить логи; для условного пропуска — `[Fact(Skip = "reason")]` |


## Справочник

### Атрибуты

| Атрибут | Назначение |
|---------|------------|
| `[Fact]` | Один тест без параметров |
| `[Theory]` | Параметризованный тест |
| `[InlineData(...)]` | Данные для [Theory] |
| `[MemberData(nameof(...))]` | Данные из метода/свойства |
| `[ClassData(typeof(...))]` | Данные из класса |
| `[Skip = "reason"]` | Пропуск теста |
| `[Trait("key", "value")]` | Категория для фильтрации |
| `[Collection("name")]` | Коллекция для фикстур |

### Частые ассерты

| Метод | Описание |
|-------|----------|
| `Assert.Equal(expected, actual)` | Равенство |
| `Assert.True(condition)` | Условие истинно |
| `Assert.Throws<T>(action)` | Ожидаемое исключение |
| `Assert.NotNull(obj)` | Объект не null |
| `Assert.Contains(item, collection)` | Элемент в коллекции |
| `Assert.All(collection, action)` | Проверка каждого элемента |

### xunit.runner.json

| Параметр | Описание | По умолчанию |
|----------|----------|--------------|
| parallelizeAssembly | Параллельный запуск сборок | true |
| parallelizeTestCollections | Параллельный запуск коллекций | true |
| maxParallelThreads | Макс. потоков | не ограничено |

### Глоссарий

| Термин | Описание |
|--------|----------|
| **Fact** | Тест без параметров |
| **Theory** | Параметризованный тест |
| **Fixture** | Общий ресурс; `IClassFixture<T>`, `ICollectionFixture<T>` |
| **Collection** | Группа классов с одной фикстурой |
| **Assert** | Проверка результата |
| **Trait** | Метаданные для фильтрации |
| **TRX** | Формат отчёта результатов (Visual Studio / dotnet test) |


## Заключение

xUnit.net даёт изоляцию «один экземпляр на тест», параллельный запуск и простую модель фактов и теорий. Используйте `[Theory]` и `[InlineData]` для параметризации, `IClassFixture<T>` для общих ресурсов, `IDisposable` для очистки. Документация: [xUnit.net](https://xunit.net/docs/getting-started/netcore/cmdline), [Unit Testing](../), [junit](../junit/junit.md).

*Дата: 2026-02-06*
