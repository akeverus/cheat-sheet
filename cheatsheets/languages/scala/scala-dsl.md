---
title: "Scala DSL"
description: "Полное руководство по созданию DSL в Scala: type-safe builders, implicit conversions, operator overloading, fluent interfaces"
tags:
  - scala
  - dsl
  - domain-specific-language
  - type-safe-builders
difficulty: "advanced"
prerequisites: ["scala/scala-basics.md", "scala/scala-implicit.md"]
next: []
updated: "2026-04-20"
related: ["scala/scala-basics.md", "scala/scala-implicit.md"]
---

# Scala DSL

Кратко: полное руководство по созданию **DSL** в **Scala**: **type-safe builders**, **implicit conversions**, **operator overloading**, **fluent interfaces**.

## Полезные ссылки

### Официальная документация
- [Scala DSL](https://docs.scala-lang.org/overviews/core/string-interpolation.html)

### См. также
- [[scala-basics|Основы Scala]]
- [[scala-implicit|Implicit]]

## Содержание

- [Введение в DSL](#введение-в-dsl)
  - [Основные техники](#основные-техники)
- [Type-Safe Builders](#type-safe-builders)
- [Implicit Conversions для DSL](#implicit-conversions-для-dsl)
- [Operator Overloading](#operator-overloading)
- [Fluent Interfaces](#fluent-interfaces)
  - [Практический пример: SQL DSL](#практический-пример-sql-dsl)
  - [Практический пример: HTML DSL](#практический-пример-html-dsl)
  - [Практический пример: Тестовый DSL](#практический-пример-тестовый-dsl)
  - [Практический пример: Конфигурационный DSL](#практический-пример-конфигурационный-dsl)
  - [Расширенные операторы](#расширенные-операторы)
- [Лучшие практики](#лучшие-практики)
  - [Использование type-safe builders](#использование-type-safe-builders)
  - [Избегание излишних implicit conversions](#избегание-излишних-implicit-conversions)
  - [Документирование DSL](#документирование-dsl)
- [Продвинутые техники создания DSL](#продвинутые-техники-создания-dsl)
  - [Комбинаторные парсеры](#комбинаторные-парсеры)
  - [Embedded DSL](#embedded-dsl)
  - [External DSL](#external-dsl)
- [Решение проблем](#решение-проблем)
- [Частые вопросы](#частые-вопросы)
- [Заключение](#заключение)
- [Продвинутые техники создания DSL](#продвинутые-техники-создания-dsl-1)
  - [Internal DSL с использованием макросов](#internal-dsl-с-использованием-макросов)
  - [External DSL с парсерами](#external-dsl-с-парсерами)
  - [Fluent Interfaces (расширенные)](#fluent-interfaces-расширенные)
  - [Практические примеры: DSL для SQL запросов](#практические-примеры-dsl-для-sql-запросов)
  - [Практические примеры: DSL для HTML](#практические-примеры-dsl-для-html)
  - [Практические примеры: DSL для тестирования](#практические-примеры-dsl-для-тестирования)
  - [Практические примеры: SQL DSL](#практические-примеры-sql-dsl)
  - [Практические примеры: HTML DSL](#практические-примеры-html-dsl)
  - [Использование с различными техниками для создания DSL](#использование-с-различными-техниками-для-создания-dsl)
  - [Использование с различными техниками для комбинаторных парсеров](#использование-с-различными-техниками-для-комбинаторных-парсеров)
- [Дополнительные ресурсы](#дополнительные-ресурсы)

## Введение в DSL

**DSL** (Domain-`Specific` Language) — это специализированный язык для решения задач в определенной области. **Scala** предоставляет мощные инструменты для создания внутренних **DSL**.

### Основные техники

- **Type-`Safe` Builders**: построение объектов с проверкой типов
- **Implicit Conversions**: автоматические преобразования для удобного синтаксиса
- **Operator Overloading**: перегрузка операторов для выразительности
- **Fluent Interfaces**: цепочки методов для читаемости

## Type-Safe Builders

**Type-safe builders** обеспечивают безопасное построение объектов:**

```scala
class QueryBuilder {
  private var selectClause: Option[String] = None
  private var fromClause: Option[String] = None

  def select(columns: String): QueryBuilder = {
    selectClause = Some(columns)
    this
  }

  def from(table: String): QueryBuilder = {
    fromClause = Some(table)
    this
  }

  def build(): String = {
    s"SELECT ${selectClause.getOrElse("*")} FROM ${fromClause.getOrElse("")}"
  }
}

// Использование
val query = new QueryBuilder()
  .select("name, age")
  .from("users")
  .build()
```

**Type-safe builders** обеспечивают проверку корректности построения объектов на этапе компиляции.

## Implicit Conversions для DSL

**Implicit conversions** позволяют создавать удобный синтаксис:**

```scala
implicit class IntOps(val n: Int) extends AnyVal {
  def days: Duration = Duration(n, TimeUnit.DAYS)
  def hours: Duration = Duration(n, TimeUnit.HOURS)
  def minutes: Duration = Duration(n, TimeUnit.MINUTES)
}

// Использование
val timeout = 5.minutes
val delay = 2.hours
```

**Implicit conversions** делают **DSL** более выразительным и естественным.

## Operator Overloading

**Перегрузка операторов позволяет использовать знакомые символы:**

```scala
case class Money(amount: Double, currency: String) {
  def +(other: Money): Money = {
    require(currency == other.currency, "Currencies must match")
    Money(amount + other.amount, currency)
  }

  def *(factor: Double): Money = {
    Money(amount * factor, currency)
  }
}

// Использование
val total = Money(100, "USD") + Money(50, "USD")  // Money(150, "USD")
val doubled = Money(100, "USD") * 2  // Money(200, "USD")
```

Перегрузка операторов делает **DSL** более интуитивным.

## Fluent Interfaces

**Fluent interfaces** позволяют создавать цепочки методов:**

```scala
class FluentBuilder {
  private var conditions: List[String] = Nil

  def where(condition: String): FluentBuilder = {
    conditions = condition :: conditions
    this
  }

  def and(condition: String): FluentBuilder = {
    where(condition)
  }

  def build(): String = {
    if (conditions.isEmpty) ""
    else s"WHERE ${conditions.reverse.mkString(" AND ")}"
  }
}

// Использование
val query = new FluentBuilder()
  .where("age > 18")
  .and("status = 'active'")
  .build()
```

**Fluent interfaces** делают код более читаемым и выразительным.

### Практический пример: SQL DSL

```scala
// Type-safe SQL builder
sealed trait QueryState
case object Empty extends QueryState
case class WithSelect(columns: String) extends QueryState
case class WithFrom(table: String) extends QueryState
case class WithWhere(condition: String) extends QueryState

class QueryBuilder[S <: QueryState] private(val query: String) {
  def select(columns: String)(implicit ev: S =:= Empty): QueryBuilder[WithSelect] = {
    new QueryBuilder[WithSelect](s"SELECT $columns")
  }

  def from(table: String)(implicit ev: S =:= WithSelect): QueryBuilder[WithFrom] = {
    new QueryBuilder[WithFrom](s"$query FROM $table")
  }

  def where(condition: String)(implicit ev: S =:= WithFrom): QueryBuilder[WithWhere] = {
    new QueryBuilder[WithWhere](s"$query WHERE $condition")
  }

  def build()(implicit ev: S =:= WithWhere): String = query
}

object QueryBuilder {
  def apply(): QueryBuilder[Empty] = new QueryBuilder[Empty]("")
}

// Использование
val query = QueryBuilder()
  .select("name, age")
  .from("users")
  .where("age > 18")
  .build()
// "SELECT name, age FROM users WHERE age > 18"
```

### Практический пример: HTML DSL

```scala
trait HtmlElement {
  def render: String
}

case class HtmlTag(name: String, attributes: Map[String, String] = Map.empty, children: List[HtmlElement] = Nil) extends HtmlElement {
  def render: String = {
    val attrs = if (attributes.isEmpty) "" else attributes.map { case (k, v) => s"""$k="$v"""" }.mkString(" ", " ", "")
    val childrenStr = children.map(_.render).mkString
    s"<$name$attrs>$childrenStr</$name>"
  }
}

case class HtmlText(text: String) extends HtmlElement {
  def render: String = text
}

class HtmlBuilder {
  def div(attributes: Map[String, String] = Map.empty)(children: HtmlElement*): HtmlTag = {
    HtmlTag("div", attributes, children.toList)
  }

  def span(text: String): HtmlTag = {
    HtmlTag("span", children = List(HtmlText(text)))
  }

  def p(text: String): HtmlTag = {
    HtmlTag("p", children = List(HtmlText(text)))
  }
}

// Использование
val html = new HtmlBuilder()
val page = html.div(Map("class" -> "container"))(
  html.p("Hello, World!"),
  html.span("Scala DSL")
)
page.render
// <div class="container"><p>Hello, World!</p><span>Scala DSL</span></div>
```

### Практический пример: Тестовый DSL

```scala
class TestScope {
  private var tests: List[Test] = Nil

  case class Test(name: String, body: () => Unit)

  def test(name: String)(body: => Unit): Unit = {
    tests = Test(name, () => body) :: tests
  }

  def run(): Unit = {
    tests.reverse.foreach { test =>
      try {
        test.body()
        println(s"✓ ${test.name}")
      } catch {
        case e: Throwable => println(s"✗ ${test.name}: ${e.getMessage}")
      }
    }
  }
}

def test(block: TestScope => Unit): Unit = {
  val scope = new TestScope()
  block(scope)
  scope.run()
}

// Использование
test { t =>
  t.test("should add numbers") {
    assert(2 + 2 == 4)
  }

  t.test("should multiply numbers") {
    assert(3 * 3 == 9)
  }
}
```

### Практический пример: Конфигурационный DSL

```scala
class ConfigBuilder {
  private var config: Map[String, Any] = Map.empty

  def set(key: String, value: Any): ConfigBuilder = {
    config = config + (key -> value)
    this
  }

  def build(): Map[String, Any] = config
}

implicit class ConfigOps(val builder: ConfigBuilder) extends AnyVal {
  def database(block: DatabaseConfig => Unit): ConfigBuilder = {
    val dbConfig = new DatabaseConfig()
    block(dbConfig)
    builder.set("database", dbConfig.build())
  }

  def server(block: ServerConfig => Unit): ConfigBuilder = {
    val serverConfig = new ServerConfig()
    block(serverConfig)
    builder.set("server", serverConfig.build())
  }
}

class DatabaseConfig {
  private var config: Map[String, Any] = Map.empty
  def host(h: String): DatabaseConfig = { config = config + ("host" -> h); this }
  def port(p: Int): DatabaseConfig = { config = config + ("port" -> p); this }
  def name(n: String): DatabaseConfig = { config = config + ("name" -> n); this }
  def build(): Map[String, Any] = config
}

class ServerConfig {
  private var config: Map[String, Any] = Map.empty
  def port(p: Int): ServerConfig = { config = config + ("port" -> p); this }
  def host(h: String): ServerConfig = { config = config + ("host" -> h); this }
  def build(): Map[String, Any] = config
}

// Использование
val config = new ConfigBuilder()
  .database { db =>
    db.host("localhost").port(5432).name("mydb")
  }
  .server { server =>
    server.host("0.0.0.0").port(8080)
  }
  .build()
```

### Расширенные операторы

```scala
case class Vector2D(x: Double, y: Double) {
  def +(other: Vector2D): Vector2D = Vector2D(x + other.x, y + other.y)
  def -(other: Vector2D): Vector2D = Vector2D(x - other.x, y - other.y)
  def *(scalar: Double): Vector2D = Vector2D(x * scalar, y * scalar)
  def /(scalar: Double): Vector2D = Vector2D(x / scalar, y / scalar)
  def dot(other: Vector2D): Double = x * other.x + y * other.y
  def magnitude: Double = math.sqrt(x * x + y * y)

  def unary_- : Vector2D = Vector2D(-x, -y)
  def unary_+ : Vector2D = this
}

// Использование
val v1 = Vector2D(1, 2)
val v2 = Vector2D(3, 4)
val sum = v1 + v2  // Vector2D(4, 6)
val scaled = v1 * 2  // Vector2D(2, 4)
val negated = -v1  // Vector2D(-1, -2)
```

## Лучшие практики

### Использование type-safe builders

```scala
// Хорошо - type-safe builder с проверкой на этапе компиляции
class SafeBuilder {
  def step1(): Step2Builder = new Step2Builder()
}

class Step2Builder {
  def step2(): Step3Builder = new Step3Builder()
}

class Step3Builder {
  def build(): Result = new Result()
}

// Использование
val result = new SafeBuilder().step1().step2().build()
// Невозможно пропустить шаги
```

### Избегание излишних implicit conversions

```scala
// Хорошо - явные преобразования там, где это необходимо
implicit class RichInt(val n: Int) extends AnyVal {
  def minutes: Duration = Duration(n, TimeUnit.MINUTES)
}

// Плохо - слишком много неявных преобразований, которые могут скрыть ошибки
implicit def intToString(x: Int): String = x.toString
implicit def stringToInt(s: String): Int = s.toInt
```

### Документирование DSL

**Всегда документируйте **DSL**, объясняя синтаксис и использование:**

```scala
/
 * SQL Query Builder DSL
 *
 * Использование:
 * {{{
 *   val query = QueryBuilder()
 *     .select("name, age")
 *     .from("users")
 *     .where("age > 18")
 *     .build()
 * }}}
 */
class QueryBuilder[S <: QueryState] private(val query: String) {
  // ...
}
```

## Продвинутые техники создания DSL

### Комбинаторные парсеры

Комбинаторные парсеры позволяют создавать **DSL** для парсинга.

```scala
import scala.util.parsing.combinator._

class MyParser extends RegexParsers {
  def number: Parser[Int] = """\d+""".r ^^ (_.toInt)
  def plus: Parser[String] = "+"
  def minus: Parser[String] = "-"
  def expr: Parser[Int] = number ~ (plus | minus) ~ number ^^ {
    case n1 ~ "+" ~ n2 => n1 + n2
    case n1 ~ "-" ~ n2 => n1 - n2
  }
}

val parser = new MyParser()
val result = parser.parse(parser.expr, "5+3")  // Success(8)
```

### Embedded DSL

**Embedded DSL** встроены в язык и используют его синтаксис.

```scala
// SQL-like DSL
object SQL {
  def select(columns: String*): SelectBuilder = new SelectBuilder(columns.toList)
}

class SelectBuilder(columns: List[String]) {
  def from(table: String): FromBuilder = new FromBuilder(columns, table)
}

class FromBuilder(columns: List[String], table: String) {
  def where(condition: String): Query = Query(columns, table, Some(condition))
  def build(): Query = Query(columns, table, None)
}

case class Query(columns: List[String], table: String, where: Option[String])

// Использование
val query = SQL.select("name", "age").from("users").where("age > 18").build()
```

### External DSL

**External DSL** используют собственный синтаксис и требуют парсинга.

```scala
// Пример конфигурационного DSL
val config = """
  database {
    host = "localhost"
    port = 5432
    name = "mydb"
  }
  server {
    port = 8080
    timeout = 30s
  }
"""

// Парсер для конфигурации
class ConfigParser extends RegexParsers {
  def config: Parser[Config] = rep(section) ^^ Config

  def section: Parser[Section] =
    identifier ~ "{" ~ rep(setting) ~ "}" ^^ {
      case name ~ _ ~ settings ~ _ => Section(name, settings)
    }

  def setting: Parser[Setting] =
    identifier ~ "=" ~ value ^^ {
      case key ~ _ ~ value => Setting(key, value)
    }
}
```


## Решение проблем

Типичные проблемы и решения см. в официальной документации (блок «Полезные ссылки» в начале документа).

## Частые вопросы

Ответы на частые вопросы по теме см. в разделах «Введение» и «Лучшие практики» в документе.

## Заключение

## Продвинутые техники создания DSL

### Internal DSL с использованием макросов

Макросы позволяют создавать более выразительные **DSL**.

```scala
import scala.language.experimental.macros
import scala.reflect.macros.blackbox.Context

// Макрос для создания DSL
def sql(query: String): Query = macro sqlImpl

def sqlImpl(c: Context)(query: c.Expr[String]): c.Expr[Query] = {
  import c.universe._
  // Генерация кода для SQL запроса
  c.Expr[Query](q"Query($query)")
}
```

### External DSL с парсерами

**External DSL** требуют создания парсеров для собственного синтаксиса.

```scala
import scala.util.parsing.combinator._

// Парсер для конфигурационного DSL
class ConfigParser extends RegexParsers {
  def config: Parser[Config] = rep(section) ^^ Config

  def section: Parser[Section] =
    identifier ~ "{" ~ rep(setting) ~ "}" ^^ {
      case name ~ _ ~ settings ~ _ => Section(name, settings)
    }

  def setting: Parser[Setting] =
    identifier ~ "=" ~ value ^^ {
      case key ~ _ ~ value => Setting(key, value)
    }

  def identifier: Parser[String] = """[a-zA-Z_][a-zA-Z0-9_]*""".r
  def value: Parser[String] = """[^}]+""".r
}
```

### Fluent Interfaces (расширенные)

**Fluent Interfaces** позволяют создавать цепочки вызовов методов.

```scala
// Fluent interface для построения запросов
class QueryBuilder {
  private var selectClause: String = ""
  private var fromClause: String = ""
  private var whereClause: String = ""

  def select(columns: String*): QueryBuilder = {
    selectClause = columns.mkString(", ")
    this
  }

  def from(table: String): QueryBuilder = {
    fromClause = table
    this
  }

  def where(condition: String): QueryBuilder = {
    whereClause = condition
    this
  }

  def build(): String = {
    s"SELECT $selectClause FROM $fromClause WHERE $whereClause"
  }
}

// Использование
val query = new QueryBuilder()
  .select("name", "age")
  .from("users")
  .where("age > 18")
  .build()
```

### Практические примеры: DSL для SQL запросов

```scala
case class QueryBuilder(table: String) {
  private var whereClauses: List[String] = Nil
  private var selectFields: List[String] = Nil

  def select(fields: String*): QueryBuilder = {
    selectFields = fields.toList
    this
  }

  def where(condition: String): QueryBuilder = {
    whereClauses = whereClauses :+ condition
    this
  }

  def build: String = {
    val select = if (selectFields.isEmpty) "*" else selectFields.mkString(", ")
    val where = if (whereClauses.isEmpty) "" else s" WHERE ${whereClauses.mkString(" AND ")}"
    s"SELECT $select FROM $table$where"
  }
}

// Использование
val query = QueryBuilder("users")
  .select("id", "name", "email")
  .where("age > 18")
  .where("active = true")
  .build
// "SELECT id, name, email FROM users WHERE age > 18 AND active = true"
```

### Практические примеры: DSL для HTML

```scala
trait HtmlElement {
  def render: String
}

case class Div(children: List[HtmlElement], attrs: Map[String, String] = Map.empty) extends HtmlElement {
  def render: String = {
    val attrStr = if (attrs.isEmpty) "" else attrs.map { case (k, v) => s"""$k="$v"""" }.mkString(" ", " ", "")
    s"<div$attrStr>${children.map(_.render).mkString}</div>"
  }
}

case class Text(content: String) extends HtmlElement {
  def render: String = content
}

case class H1(content: String) extends HtmlElement {
  def render: String = s"<h1>$content</h1>"
}

case class P(content: String) extends HtmlElement {
  def render: String = s"<p>$content</p>"
}

// Использование
val html = Div(
  List(
    H1("Welcome"),
    P("This is a Scala DSL example"),
    Div(List(Text("Nested content")))
  ),
  Map("class" -> "container")
)

println(html.render)
```

### Практические примеры: DSL для тестирования

```scala
class TestDSL {
  private var tests: List[Test] = Nil

  case class Test(name: String, body: () => Unit)

  def test(name: String)(body: => Unit): Unit = {
    tests = tests :+ Test(name, () => body)
  }

  def run(): Unit = {
    tests.foreach { t =>
      try {
        t.body()
        println(s"✓ ${t.name}")
      } catch {
        case e: Exception =>
          println(s"✗ ${t.name}: ${e.getMessage}")
      }
    }
  }
}

// Использование
val suite = new TestDSL
suite.test("addition works") {
  assert(1 + 1 == 2)
}

suite.test("division works") {
  assert(10 / 2 == 5)
}

suite.run()
```

**DSL** в **Scala** позволяют создавать выразительный и типобезопасный код для специфических областей. Понимание **type-safe builders**, **implicit conversions**, **operator overloading**, **fluent interfaces**, комбинаторных парсеров, **embedded DSL**, **external DSL**, **internal DSL** с использованием макросов, **external DSL** с парсерами, расширенных **Fluent Interfaces**, **SQL DSL**, **HTML DSL**, тестового **DSL** и их практических применений позволяет создавать мощные и удобные **DSL**. Правильное использование этих техник, документирование **DSL**, создание **SQL DSL** для запросов, **HTML DSL** для генерации **HTML**, тестового **DSL** для написания тестов критично для создания поддерживаемого, читаемого и выразительного кода. **DSL** особенно полезны для создания специализированных языков для конкретных доменов, которые делают код более читаемым, выразительным, и типобезопасным.

### Практические примеры: SQL DSL

```scala
// Type-safe SQL DSL
case class QueryBuilder(table: String, conditions: List[String] = Nil) {
  def where(condition: String): QueryBuilder = {
    copy(conditions = conditions :+ condition)
  }

  def build: String = {
    val whereClause = if (conditions.nonEmpty) {
      s" WHERE ${conditions.mkString(" AND ")}"
    } else ""
    s"SELECT * FROM $table$whereClause"
  }
}

// Использование
val query = QueryBuilder("users")
  .where("age > 18")
  .where("active = true")
  .build
// "SELECT * FROM users WHERE age > 18 AND active = true"
```

### Практические примеры: HTML DSL

```scala
// HTML DSL для генерации HTML
sealed trait HtmlNode
case class HtmlElement(tag: String, attributes: Map[String, String], children: List[HtmlNode]) extends HtmlNode
case class HtmlText(content: String) extends HtmlNode

object Html {
  def div(attributes: Map[String, String] = Map.empty)(children: HtmlNode*): HtmlNode = {
    HtmlElement("div", attributes, children.toList)
  }

  def span(text: String): HtmlNode = HtmlText(text)
}

// Использование
val html = Html.div(Map("class" -> "container"))(
  Html.span("Hello, World!")
)
```

### Использование с различными техниками для создания DSL

```scala
// Fluent Interface для создания DSL
class ConfigBuilder {
  private var host: Option[String] = None
  private var port: Option[Int] = None
  private var timeout: Option[Int] = None

  def withHost(host: String): ConfigBuilder = {
    this.host = Some(host)
    this
  }

  def withPort(port: Int): ConfigBuilder = {
    this.port = Some(port)
    this
  }

  def withTimeout(timeout: Int): ConfigBuilder = {
    this.timeout = Some(timeout)
    this
  }

  def build: Config = {
    Config(
      host.getOrElse("localhost"),
      port.getOrElse(8080),
      timeout.getOrElse(30)
    )
  }
}

case class Config(host: String, port: Int, timeout: Int)

// Использование
val config = new ConfigBuilder()
  .withHost("example.com")
  .withPort(9090)
  .withTimeout(60)
  .build
```

### Использование с различными техниками для комбинаторных парсеров

```scala
// Комбинаторный парсер для создания DSL
import scala.util.parsing.combinator._

object SimpleParser extends JavaTokenParsers {
  def expr: Parser[Int] = term ~ rep("+" ~ term | "-" ~ term) ^^ {
    case number ~ list => list.foldLeft(number) {
      case (x, "+" ~ y) => x + y
      case (x, "-" ~ y) => x - y
    }
  }

  def term: Parser[Int] = factor ~ rep("*" ~ factor | "/" ~ factor) ^^ {
    case number ~ list => list.foldLeft(number) {
      case (x, "*" ~ y) => x * y
      case (x, "/" ~ y) => x / y
    }
  }

  def factor: Parser[Int] = wholeNumber ^^ (_.toInt) | "(" ~ expr ~ ")" ^^ {
    case _ ~ e ~ _ => e
  }
}

// Использование
val result = SimpleParser.parseAll(SimpleParser.expr, "2 + 3 * 4")
```

## Дополнительные ресурсы

**Для дальнейшего изучения **DSL** в **Scala** рекомендуется:**

- [Scala DSL Documentation](https://docs.scala-lang.org/overviews/core/string-interpolation.html)
- [Type-Safe Builders in Scala](https://docs.scala-lang.org/overviews/scala-book/case-classes.html)
- [Scala DSL Examples](https://github.com/scala/scala)
