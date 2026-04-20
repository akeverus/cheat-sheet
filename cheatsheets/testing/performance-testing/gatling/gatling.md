---
title: "Gatling"
description: "Кратко: Gatling — инструмент нагрузочного тестирования на Scala (DSL) и Java. Simulation, Scenario, HTTP, inject (atOnceUsers, rampUsers и др.), HTML-отчёты, интеграция с Grafana/InfluxDB, CI/CD."
tags:
  - testing
  - performance-testing
  - gatling
difficulty: "intermediate"
prerequisites: []
next: []
updated: "2026-02-11"
---
# Gatling

Кратко: **Gatling** — инструмент нагрузочного тестирования на **Scala** (DSL) и **Java**. **Simulation**, **Scenario**, **HTTP**, **inject** (atOnceUsers, rampUsers и др.), HTML-отчёты, интеграция с **Grafana**/InfluxDB, **CI/CD**.

**Дата:** 2026-02-06


## Полезные ссылки

| Тип | Ссылка |
|-----|--------|
| Официальный сайт | [gatling.io](https://gatling.io/) |
| Документация | [Gatling Documentation](https://gatling.io/docs/gatling/) |
| Simulation | [Simulation Reference](https://gatling.io/docs/gatling/reference/current/core/simulation/) |
| Quick Start | [Quick Start](https://gatling.io/docs/gatling/quickstart/) |
| Cheat Sheet | [Gatling Cheat Sheet](https://gatling.io/docs/gatling/reference/current/cheat-sheet/) |

**См. также:** [[jmeter]], [[k6]], [[artillery]], [[testing-tools-overview|Обзор инструментов тестирования]].


## Содержание

- [Введение](#введение)
- [Установка и настройка](#установка-и-настройка)
- [Структура Simulation](#структура-simulation)
- [Базовое использование](#базовое-использование)
- [Отчёты и метрики](#отчёты-и-метрики)
- [Параметризация и переменные](#параметризация-и-переменные)
- [Контроллеры, циклы и условия](#контроллеры-циклы-и-условия)
- [Пример: авторизация и API](#пример-авторизация-и-api)
- [CI/CD](#cicd)
- [Конфигурация](#конфигурация)
- [Лучшие практики](#лучшие-практики)
- [Сравнение с JMeter и k6](#сравнение-с-jmeter-и-k6)
- [FAQ и решение проблем](#faq-и-решение-проблем)
- [Глоссарий и таблицы](#глоссарий-и-таблицы)
- [Заключение](#заключение)


## Введение

**Gatling** — открытый инструмент нагрузочного тестирования на **Scala** с **DSL** для описания сценариев. Сценарии задаются в коде (Simulation, Scenario, exec, http); запуск — из командной строки или через **sbt**/Gradle/Maven. Отчёты — HTML по умолчанию; поддержка **InfluxDB** и **Grafana** для дашбордов в реальном времени.

### Зачем Gatling

- **Код вместо GUI** — сценарии в Scala/Java; версионирование, ревью, переиспользование.
- **Низкие накладные расходы** — асинхронная модель; меньше ресурсов на поток, чем у JMeter.
- **Удобные отчёты** — HTML с графиками из коробки; интеграция с Grafana через InfluxDB.
- **Протоколы** — HTTP, WebSocket; gRPC, JMS, MQTT через плагины.
- **CI/CD** — запуск через sbt, Gradle, Maven, Docker.

### Основные концепции

| Концепция | Описание |
|-----------|----------|
| **Simulation** | Класс сценария нагрузки; в конструкторе вызывается **setUp(...)** с сценариями и профилем нагрузки (inject). |
| **Scenario** | Последовательность действий пользователя (exec). Задаётся через **scenario("name")**. |
| **exec** | Выполнение одного или нескольких действий (например, **http("request").get(...)**). |
| **inject** | Профиль нагрузки: **atOnceUsers(n)**, **rampUsers(n).during(d)** и др. |
| **httpProtocol** | Настройки HTTP по умолчанию: **baseUrl**, заголовки. |


## Установка и настройка

### Требования

- **Java** 8+ (рекомендуется 11 или 17). Scala подтягивается через sbt.
- **sbt** или **Gradle** / **Maven** для сборки и запуска.

### Установка через sbt

**build.sbt** (фрагмент):

```scala
enablePlugins(GatlingPlugin)
libraryDependencies += "io.gatling.highcharts" % "gatling-charts-highcharts" % "3.9.5" % "test"
libraryDependencies += "io.gatling" % "gatling-test-framework" % "3.9.5" % "test"
```

- **src/test/scala** — классы Simulation.
- **src/test/resources** — конфигурация, CSV и др.

Запуск одной симуляции:

```bash
sbt "Gatling/testOnly com.example.BasicSimulation"
```

Все симуляции:

```bash
sbt Gatling/test
```

### Установка через Maven

В **pom.xml** добавить зависимости **gatling-charts-highcharts** и **gatling-test-framework** (scope test) и плагин **gatling-maven-plugin**. Запуск:

```bash
mvn gatling:test -Dgatling.simulationClass=com.example.BasicSimulation
```

### Проверка

После запуска в **target/gatling** (или аналог) появляется каталог с HTML-отчётом. Открыть **index.html** в браузере.


## Структура Simulation

### Scenario и exec

**Scenario** — последовательность действий через **exec(...)**. **pause(1)** — пауза 1 с (think time). **atOnceUsers(10)** — 10 пользователей стартуют сразу.

Пример минимальной симуляции:

```scala
import io.gatling.core.Predef._
import io.gatling.http.Predef._
import scala.concurrent.duration._

class BasicSimulation extends Simulation {

  val httpProtocol = http
    .baseUrl("https://example.com")
    .acceptHeader("application/json")

  val scn = scenario("Basic Scenario")
    .exec(http("Get Home").get("/"))
    .exec(http("Get API").get("/api/status"))
    .pause(1)

  setUp(scn.inject(atOnceUsers(10))).protocols(httpProtocol)
}
```

### HTTP-запросы

| Элемент | Описание |
|---------|----------|
| **get(url)** / **post(url)** | Метод и путь (относительно baseUrl или абсолютный). |
| **header(name, value)** | Заголовок запроса. |
| **body(StringBody("...")).asJson** | Тело запроса (JSON). |
| **queryParam(name, value)** | Query-параметр. |
| **check** | Проверка ответа: **status**.is(200), **jsonPath("$.id").saveAs("id")** и т.д. |

Пример POST с JSON и проверкой:

```scala
.exec(
  http("Create User")
    .post("/api/users")
    .header("Content-Type", "application/json")
    .body(StringBody("""{"name":"${name}","email":"${email}"}""")).asJson
    .check(status.is(201))
    .check(jsonPath("$.id").saveAs("userId"))
)
```

Переменные **${name}**, **${email}** задаются через **feed** или **session**.

### inject (профиль нагрузки)

| Метод | Описание |
|-------|----------|
| **atOnceUsers(n)** | n пользователей сразу. |
| **rampUsers(n).during(d)** | Линейный выход на n пользователей за время d. |
| **constantUsersPerSec(rate).during(d)** | Постоянная скорость (пользователей/сек) в течение d. |
| **rampUsersPerSec(r1).to(r2).during(d)** | Скорость от r1 до r2 за время d. |
| **nothingFor(d)** | Пауза d перед следующим шагом. |
| **heavisideUsers(n).during(d)** | Плавный выход на n пользователей. |

Комбинирование: **rampUsers(10).during(10).andThen(atOnceUsers(5))** — сначала ramp, затем ещё 5 пользователей.


## Базовое использование

1. Создать класс, расширяющий **Simulation**.
2. Задать **httpProtocol** (baseUrl, заголовки по умолчанию).
3. Задать **scenario** с **exec** и **http**-запросами.
4. Вызвать **setUp(scenario.inject(...)).protocols(httpProtocol)**.

Запуск: **sbt "Gatling/testOnly com.example.BasicSimulation"** или **mvn gatling:test -Dgatling.simulationClass=...**.

После прогона в **target/gatling/<simulation-id>** создаётся каталог с **index.html** — графики времени отклика, пропускной способности, активных пользователей, ошибок.


## Отчёты и метрики

- **Время отклика** — mean, min, max, перцентили.
- **Пропускная способность** — запросов в секунду.
- **Активные пользователи** — число одновременно работающих виртуальных пользователей.
- **Неуспешные запросы** — по **check** или коду ответа.

HTML-отчёт генерируется по умолчанию. Для метрик в реальном времени настраивают **InfluxDB** и дашборд **Grafana** (через gatling.conf / logback).


## Параметризация и переменные

- **feed(csv("file.csv").circular)** — данные из CSV; каждая итерация берёт следующую строку. Поля CSV становятся переменными в **session**.
- **session("key")** — доступ к переменной; в **check** сохраняют через **saveAs("key")**.
- В строках подстановка: **"${variableName}"**.
- **repeat(n)** — повторить блок **exec** n раз.
- **during(d)** — выполнять блок в течение времени d.
- **foreach(sequence, "varName")** — итерация по последовательности.

Пример с CSV:

```scala
val feeder = csv("users.csv").circular
val scn = scenario("With CSV")
  .feed(feeder)
  .exec(http("Get User").get("/api/users/${userId}"))
```

**users.csv** с колонкой **userId**; **circular** — по кругу, **queue** — одна строка на пользователя.


## Контроллеры, циклы и условия

| Конструкция | Описание |
|-------------|----------|
| **repeat(n)** | Повторить блок n раз. |
| **during(d)** | Выполнять блок в течение d. |
| **doIf(condition)** / **doIfEquals(key, value)** | Выполнить блок при условии (по session). |
| **randomSwitch(percent1 -> chain1, ...)** | Случайное ветвление по процентам. |
| **tryMax(n)** | Повторить блок до n раз до успешного **check**. |

Удобно для сценариев с разными ролями (админ/пользователь) или разными путями в зависимости от данных.


## Пример: авторизация и API

Логин извлечь токен через **check** использовать в заголовке в последующих запросах:

```scala
val scn = scenario("Auth and API")
  .feed(csv("users.csv"))
  .exec(
    http("Login")
      .post("/api/login")
      .body(StringBody("""{"username":"${user}","password":"${pass}"}""")).asJson
      .check(status.is(200))
      .check(jsonPath("$.access_token").saveAs("token"))
  )
  .pause(1)
  .exec(
    http("Get Profile")
      .get("/api/me")
      .header("Authorization", "Bearer ${token}")
      .check(status.is(200))
  )
```

**group("Name") { ... }** группирует запросы в отчёте (метрики по группе).


## CI/CD

- **Jenkins:** шаг с **sbt "Gatling/testOnly ..."**; публикация **target/gatling/** через HTML Publisher.
- **GitHub Actions:** `run: sbt "Gatling/testOnly ..."` и **upload-artifact** для **target/gatling/**.
- Параметры (baseUrl, число пользователей) переопределять через **System.getProperty("baseUrl")** или **sys.env.get("BASE_URL")**; запуск: **sbt -DbaseUrl=https://staging.example.com "Gatling/testOnly ..."**.


## Конфигурация

Основное в **gatling.conf** (src/test/resources):

- **gatling.core.outputDirectoryBase** — каталог отчётов (по умолчанию target/gatling).
- **gatling.core.runDescription** — описание прогона в отчёте.
- **gatling.http.enableGA** — отключить анонимную статистику (в CI лучше false).

Переопределение: **-Dgatling.core.outputDirectoryBase=/path/to/reports**.


## Лучшие практики

1. **Данные отдельно от логики** — URL, пользователи в CSV или конфиге.
2. **baseUrl** в **httpProtocol** — в get/post только путь.
3. **check** для критичных ответов — status, jsonPath saveAs для цепочки запросов.
4. **pause** для think time — **pause(1)** или **pause(1, 5)** для реалистичности.
5. **Ramp-up** — **rampUsers(n).during(d)** вместо старта всех сразу.
6. Именовать запросы: **http("Get Home")** для читаемого отчёта.
7. Фиксировать версию Gatling в build.sbt/pom.xml.

**Чек-лист перед прогоном:** baseUrl и заголовки заданы; check на критичных ответах; pause и inject с ramp-up; имена запросов; параметры переопределяются в CI.


## Сравнение с JMeter и k6

| Критерий | Gatling | JMeter | k6 |
|----------|---------|--------|-----|
| Язык/интерфейс | Scala (DSL), Java DSL | GUI + CLI | JavaScript |
| Протоколы | HTTP, WebSocket, gRPC (плагин) | HTTP, JDBC, FTP, JMS, TCP, ... | HTTP, WebSocket, gRPC |
| Порог входа | Средний (код) | Низкий (GUI) | Низкий (JS) |
| Ресурсы на поток | Ниже | Выше | Ниже |
| Отчёты | HTML, InfluxDB, Grafana | Summary, HTML, Backend Listener | Встроенные, Grafana/InfluxDB |
| CI/CD | sbt, Maven, Gradle, Docker | Запуск .jmx в CLI | Скрипт, Docker |


## FAQ и решение проблем

### Таблица типичных проблем

| Проблема | Возможная причина | Решение |
|----------|-------------------|---------|
| Simulation не найден | Неверное имя класса или пакет | Проверить -Dgatling.simulationClass или путь в sbt |
| Ошибка компиляции Scala | Синтаксис, версия Scala | Проверить импорты (Predef._), версию Scala в sbt |
| Высокий % ошибок | Падают check (status, jsonPath) | Проверить коды и структуру ответа; скорректировать check |
| OutOfMemoryError | Слишком много пользователей или длительный прогон | Увеличить heap JVM; снизить нагрузку |
| Нет отчёта | Ошибка до конца прогона, неверный каталог | Логи; gatling.resultsDirectory в конфигурации |
| Переменная не подставляется | Переменная не в session или опечатка | Проверить feed/check saveAs; имя в "${var}" |

### Вопросы и ответы

**Gatling или JMeter?**
Gatling — сценарии в коде, меньше ресурсов на поток, удобные отчёты. JMeter — GUI, много протоколов из коробки. Выбор по команде (Scala/Java) и протоколам.

**Нужно ли знать Scala?**
Базовые сценарии можно писать по примерам; для сложной логики пригодится Scala. Есть **Gatling Java DSL** для Java.

**Как тестировать API с авторизацией?**
Логин в `check` извлечь токен (`jsonPath("$.access_token").saveAs("token")`) в `httpProtocol` или в запросах `header("Authorization", "Bearer ${token}")`.

**Тело запроса из файла?**
**ElFileBody("path/to/body.json")** или **RawFileBody(...)** в **body(...)**; в файле с ElFileBody можно использовать **${variable}**.

**gRPC?**
Плагин **gatling-grpc**. Также часто используют k6 для gRPC.


## Глоссарий и таблицы

### Глоссарий

| Термин | Описание |
|--------|----------|
| **Simulation** | Класс сценария нагрузки; расширяет **Simulation** |
| **Scenario** | Последовательность действий пользователя (exec) |
| **exec** | Выполнение действия (например, HTTP-запрос) |
| **inject** | Профиль нагрузки (atOnceUsers, rampUsers и т.д.) |
| **httpProtocol** | Настройки HTTP по умолчанию (baseUrl, заголовки) |
| **check** | Проверка ответа (status, jsonPath, saveAs) |
| **feed** | Источник данных (CSV и т.д.) для параметризации |
| **session** | Контекст пользователя (переменные между запросами) |
| **pause** | Think time (пауза между действиями) |

### Основные методы inject

| Метод | Описание |
|-------|----------|
| **atOnceUsers(n)** | n пользователей сразу |
| **rampUsers(n).during(d)** | Линейный выход на n пользователей за время d |
| **constantUsersPerSec(r).during(d)** | Постоянная скорость r (пользователей/сек) в течение d |
| **rampUsersPerSec(r1).to(r2).during(d)** | Скорость от r1 до r2 за время d |
| **nothingFor(d)** | Пауза d |
| **heavisideUsers(n).during(d)** | Плавный выход на n пользователей за время d |

### Основные check

| Проверка | Описание |
|----------|----------|
| **status.is(200)** | Код ответа 200 |
| **status.in(200, 201)** | Код 200 или 201 |
| **jsonPath("$.id").saveAs("id")** | Извлечь по JSONPath и сохранить в переменную |
| **bodyString.exists** | Тело ответа не пустое |
| **responseTimeInMillis.lt(500)** | Время отклика &lt; 500 мс |


## Заключение

**Gatling** — инструмент нагрузочного тестирования с описанием сценариев в коде (Scala/Java). Низкие накладные расходы и удобные HTML-отчёты делают его удобным выбором для HTTP/API и CI/CD. Используйте **baseUrl**, **check** и **feed** для параметризации; задавайте реалистичный **inject** (ramp-up, think time). Дальше: [Gatling Documentation](https://gatling.io/docs/gatling/), [[jmeter]], [[k6]], [[artillery]], [[testing-tools-overview|Обзор инструментов тестирования]].
