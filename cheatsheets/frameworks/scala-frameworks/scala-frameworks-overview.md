---
title: "Scala Frameworks — обзор"
description: "Кратко: обзор фреймворков и экосистемы Scala: Play, Akka, ZIO, http4s, Cats Effect, Tapir, веб-API, реактивные и функциональные практики."
tags:
  - frameworks
  - scala-frameworks
  - scala-frameworks-overview
difficulty: "intermediate"
prerequisites: []
next: []
updated: "2026-02-11"
---
# Scala Frameworks — обзор

Кратко: обзор фреймворков и экосистемы **Scala**: **Play**, **Akka**, **ZIO**, **http4s**, **Cats Effect**, **Tapir**, веб-**API**, реактивные и функциональные практики.

## Полезные ссылки

### Официальная документация
- [Play Framework](https://www.playframework.com/)
- [Akka](https://akka.io/)
- [ZIO](https://zio.dev/)
- [http4s](https://http4s.org/)

### См. также
- [Frameworks README](../README.md) — раздел фреймворков
- [Java Frameworks](../java-frameworks/README.md) — **Spring**, **Quarkus**
- [Scala (languages)](../../languages/scala/README.md) — язык **Scala**

## Содержание

- [Введение](#введение)
- [Play Framework](#play-framework)
- [Akka и Akka HTTP](#akka-и-akka-http)
- [ZIO и ZIO HTTP](#zio-и-zio-http)
- [http4s и Cats Effect](#http4s-и-cats-effect)
- [Tapir](#tapir)
- [Сравнение и выбор](#сравнение-и-выбор)
- [Лучшие практики](#лучшие-практики)
- [Решение проблем](#решение-проблем)
- [Частые вопросы](#частые-вопросы)
- [Глоссарий](#глоссарий)
- [Заключение](#заключение)

---

## Введение

**Scala** предлагает несколько путей для веб и **API**: **Play** — полноценный фреймворк с **MVC**, **Akka** — акторная модель и **Akka HTTP**, **ZIO** — функциональный runtime и **ZIO HTTP**, **http4s** — функциональный **HTTP** на **Cats Effect**. **Tapir** — описание **API** и генерация документации. Документ даёт обзор и рекомендации.

**Ключевые понятия:** **Play**, **Akka**, **ZIO**, **http4s**, **Cats Effect**, **Tapir**, **effect**, **actor**.

---

## Play Framework

**Play** — веб-фреймворк: **MVC**, маршрутизация по конфигу, **Twirl** шаблоны, встроенная поддержка **JSON**, **WS** (HTTP-клиент), **Akka** под капотом для асинхронности.

**Зависимость (sbt):**
```scala
// Зависимость Play Framework для sbt
libraryDependencies += "com.typesafe.play" %% "play" % "2.9.0"
```

**Контроллер (пример):**
```scala
// Контроллер Play с Dependency Injection
class HomeController @Inject()(val controllerComponents: ControllerComponents)
  extends BaseController {
  def index() = Action { implicit request =>
    Ok(Json.obj("message" -> "Hello"))
  }
  def item(id: Long) = Action { Ok(Json.obj("id" -> id)) }
}
```

**Маршруты** задаются в `conf/routes`. См. [Play](../../libraries/scala/scala-play.md).

---

## Akka и Akka HTTP

**Akka** — toolkit для реактивных распределённых приложений: акторы, потоки (**Streams**), кластеризация. **Akka HTTP** — сервер и клиент **HTTP** на базе **Akka Streams**.

**Пример маршрута:**
```scala
// Маршрут Akka HTTP: GET /api/items/:id
val route = path("api" / "items" / LongNumber) { id =>
  get { complete(Item(id, "Item")) }
}
Http().newServerAt("0.0.0.0", 8080).bind(route)
```

**Особенности:** полностью асинхронный, интеграция с **Akka** акторами и потоками. См. [Akka](../../libraries/scala/scala-akka.md).

---

## ZIO и ZIO HTTP

**ZIO** — библиотека для асинхронного и конкурентного кода на основе **effect** типа **ZIO[R, E, A]**. **ZIO HTTP** (zio-http) — **HTTP** сервер и клиент поверх **ZIO**.

**Пример:**
```scala
// Маршруты ZIO HTTP с handler
val app = Routes(
  Method.GET / "api" / "items" / long -> handler { (_, id: Long) =>
    Response.json(s"""{"id":$id}""")
  }
)
Server.serve(app).provide(Server.defaultWithPort(8080))
```

**Особенности:** типобезопасные зависимости, тестируемость, интеграция с **ZIO** экосистемой. См. [ZIO](../../libraries/scala/scala-zio.md).

---

## http4s и Cats Effect

**http4s** — чисто функциональный **HTTP** стек на **Cats** и **Cats Effect**: **IO**, **Resource**, **Blaze** или **Ember** сервер.

**Пример:**
```scala
// HttpRoutes http4s на Cats Effect IO
val routes = HttpRoutes.of[IO] {
  case GET -> Root / "api" / "items" / LongVar(id) =>
    Ok(Json.obj("id" -> id.asJson))
}
BlazeServerBuilder[IO].bindHttp(8080, "0.0.0.0").withHttpApp(routes.orNotFound).resource
```

**Особенности:** tagless final, **IO**-ориентированность, совместимость с **Cats** экосистемой. См. [http4s](../../languages/scala/scala-http4s.md).

---

## Tapir

**Tapir** — описание endpoint (путь, метод, вход/выход) как значений **Scala**; генерация **OpenAPI**, маршрутов для **Akka HTTP**, **http4s**, **ZIO HTTP**, **Play**. Удобен для контрактного **API** и документации.

---

## Сравнение и выбор

| Фреймворк | Стиль | Когда использовать |
|-----------|-------|--------------------|
| **Play** | **MVC**, императивный/смешанный | Полноценный веб, быстрый старт, знакомый **MVC** |
| **Akka HTTP** | Реактивный, **Streams** | Высокая нагрузка, интеграция с **Akka** |
| **ZIO HTTP** | Функциональный, **ZIO** | **ZIO**-стек, типобезопасность, тестируемость |
| **http4s** | Функциональный, **Cats** | **Cats Effect**-стек, tagless final |

---

## Лучшие практики

- Выбирать стек по команде: **Play** — привычный **MVC**; **ZIO**/ **http4s** — функциональный стиль.
- **Tapir** для единого описания **API** и генерации документации и клиентов.
- Управление ресурсами через **ZIO** **Resource** или **Cats** **Resource**; graceful shutdown.

---

## Решение проблем

| Проблема | Действие |
|----------|----------|
| **Play** не компилирует маршруты | Проверить `conf/routes` и инжекцию контроллеров |
| **Akka HTTP** таймауты | Настроить `idle-timeout`, `request-timeout` |
| **ZIO**/ **http4s** зависимости | Проверить слои и `provide` |

---

## Частые вопросы

**Play или Akka HTTP?** **Play** — выше уровень, встроенная поддержка форм, шаблонов, **WS**. **Akka HTTP** — низкоуровневый, максимальный контроль, **Streams**.

**ZIO или Cats Effect?** Оба — функциональные runtimes. **ZIO** — всё в одном (включая **ZIO HTTP**). **Cats Effect** + **http4s** — модульный стек. Выбор часто по предпочтениям команды.

---

## Глоссарий

| Термин | Описание |
|--------|----------|
| **Play** | Веб-фреймворк для **Scala** и **Java** |
| **Akka** | Toolkit акторов и потоков для **JVM** |
| **Akka HTTP** | **HTTP** сервер/клиент на **Akka** |
| **ZIO** | Библиотека и runtime для функциональных эффектов в **Scala** |
| **http4s** | Функциональный **HTTP** стек на **Cats Effect** |
| **Cats Effect** | Библиотека для описания и выполнения эффектов |
| **Tapir** | Библиотека описания **API** и генерации **OpenAPI**/маршрутов |

---

## Заключение

**Scala**-экосистема предлагает **Play** для классического веб, **Akka HTTP** для реактивных сценариев, **ZIO** и **http4s** для функционального стека. **Tapir** объединяет описание **API** для разных бэкендов. См. [Frameworks README](../README.md) и [Scala (languages)](../../languages/scala/README.md).

---

