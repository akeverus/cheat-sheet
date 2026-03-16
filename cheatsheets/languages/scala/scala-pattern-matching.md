---
title: "Scala Pattern Matching"
description: "Полное руководство по Pattern Matching в Scala: базовые паттерны, продвинутые техники, извлечение данных"
tags: ["scala", "pattern-matching", "functional-programming", "case-classes"]
difficulty: "intermediate"
prerequisites: ["scala/scala-basics.md"]
next: []
updated: "2026-02-06"
related: ["scala/scala-basics.md", "scala/scala-fp-basics.md"]
---

# **Scala Pattern Matching**

Кратко: полное руководство по **Pattern Matching** в **Scala**: базовые паттерны, продвинутые техники, извлечение данных.

**Дата последнего обновления:** 2026-02-06

## Полезные ссылки

### Официальная документация
- [Scala Pattern Matching](https://docs.scala-lang.org/tour/pattern-matching.html)

### См. также
- [Основы Scala](scala-basics.md)
- [Функциональное программирование](scala-fp-basics.md)

## Содержание

- [**Scala Pattern Matching**](#scala-pattern-matching)
- [Введение в **Pattern Matching**](#введение-в-pattern-matching)
  - [Основные преимущества](#основные-преимущества)
- [Базовые паттерны](#базовые-паттерны)
  - [Сопоставление с константами](#сопоставление-с-константами)
  - [Сопоставление с переменными](#сопоставление-с-переменными)
- [**Pattern Matching** с **Case Classes**](#pattern-matching-с-case-classes)
- [**Pattern Matching** с коллекциями](#pattern-matching-с-коллекциями)
  - [Списки](#списки)
  - [Векторы](#векторы)
- [**Guards** (**охранники**)](#guards-охранники)
- [**Type Patterns**](#type-patterns)
- [**Sealed Classes** и **Exhaustive Matching**](#sealed-classes-и-exhaustive-matching)
- [Лучшие практики](#лучшие-практики)
  - [Использование **sealed traits** для безопасности](#использование-sealed-traits-для-безопасности)
  - [Избегание **catch-all** паттернов](#избегание-catch-all-паттернов)
  - [**Pattern Matching** с **Tuples**](#pattern-matching-с-tuples)
  - [**Pattern Matching** с **Option**](#pattern-matching-с-option)
  - [**Pattern Matching** с **Try**](#pattern-matching-с-try)
  - [**Pattern Matching** с **Either**](#pattern-matching-с-either)
  - [Вложенный **Pattern Matching**](#вложенный-pattern-matching)
  - [**Pattern Matching** в функциях](#pattern-matching-в-функциях)
  - [**Pattern Matching** с регулярными выражениями](#pattern-matching-с-регулярными-выражениями)
  - [**Pattern Matching** с типами и значениями](#pattern-matching-с-типами-и-значениями)
  - [Практический пример: Парсинг **JSON**](#практический-пример-парсинг-json)
  - [Практический пример: Обработка **AST**](#практический-пример-обработка-ast)
  - [**Pattern Matching** в циклах](#pattern-matching-в-циклах)
  - [@ (**as-pattern**)](#as-pattern)
  - [Использование **guards** для сложных условий](#использование-guards-для-сложных-условий)
- [Troubleshooting](#troubleshooting)
- [FAQ](#faq)
- [Заключение](#заключение)
- [Дополнительные техники **Pattern Matching**](#дополнительные-техники-pattern-matching)
  - [Использование **Extractors**](#использование-extractors)
  - [Исчерпывающий **Pattern Matching**](#исчерпывающий-pattern-matching)
  - [Практические примеры: **Pattern Matching** для парсинга **JSON**](#практические-примеры-pattern-matching-для-парсинга-json)
  - [Практические примеры: **Pattern Matching** для обработки **AST**](#практические-примеры-pattern-matching-для-обработки-ast)
  - [Практические примеры: **Pattern Matching** для обработки сообщений](#практические-примеры-pattern-matching-для-обработки-сообщений)
  - [Практические примеры: **Pattern Matching** с регулярными выражениями](#практические-примеры-pattern-matching-с-регулярными-выражениями)
  - [Использование с различными типами для обработки данных](#использование-с-различными-типами-для-обработки-данных)
  - [Использование с различными типами для обработки коллекций](#использование-с-различными-типами-для-обработки-коллекций)
- [Дополнительные ресурсы](#дополнительные-ресурсы)

## Введение в **Pattern Matching**

**Pattern Matching** - это мощный механизм **Scala** для декомпозиции данных и сопоставления с образцами. **Pattern Matching** позволяет элегантно обрабатывать различные случаи и извлекать данные из структур, заменяя множественные **if-else** конструкции более выразительным и безопасным синтаксисом. **Pattern Matching** является одной из ключевых особенностей функционального программирования в **Scala** и позволяет создавать декларативный код, который явно показывает все возможные случаи обработки.

**Pattern Matching** в **Scala** более мощный, чем **switch**-конструкции в других языках, так как поддерживает декомпозицию сложных структур данных, **guards** (**условия**), извлечение значений и проверку типов. Компилятор **Scala** может проверить полноту **pattern matching**, что предотвращает ошибки, связанные с необработанными случаями.

**Pattern Matching** - это мощный механизм **Scala** для декомпозиции данных и сопоставления с образцами. **Pattern Matching** позволяет элегантно обрабатывать различные случаи и извлекать данные из структур.

### Основные преимущества

- **Декомпозиция данных**: извлечение значений из структур. **Pattern Matching** позволяет декомпозировать **case** классы, кортежи, списки и другие структуры данных, извлекая значения полей напрямую в образце. Это делает код более читаемым, чем использование геттеров и явных проверок.

- **Безопасность**: компилятор проверяет полноту сопоставления. При работе с **sealed** классами и **traits** компилятор может проверить, что все возможные случаи обработаны. Это предотвращает ошибки времени выполнения, связанные с необработанными случаями, и делает код более надежным.

- **Выразительность**: код становится более читаемым и понятным. **Pattern Matching** явно показывает все возможные случаи обработки данных, что делает код более декларативным и легким для понимания. Это особенно полезно при работе с алгебраическими типами данных и различными вариантами обработки.

- **Исчерпывающий анализ**: компилятор предупреждает о необработанных случаях. При использовании **sealed** классов компилятор может гарантировать, что все подтипы обработаны в **pattern matching**. Это обеспечивает безопасность типов и предотвращает ошибки, связанные с неполной обработкой данных.

## Базовые паттерны

### Сопоставление с константами

```scala
// Сопоставление с константами: case 0, case 1, ...
def matchNumber(x: Int): String = x match {
  case 0 => "zero"
  case 1 => "one"
  case 2 => "two"
  case _ => "many"
}
```

### Сопоставление с переменными

```scala
def matchValue(x: Any): String = x match {
  case 42 => "answer"
  case "hello" => "greeting"
  case value => s"other: $value"
}
```

## **Pattern Matching** с **Case Classes**

**Case classes** идеально подходят для **pattern matching**:**

```scala
// Case-классы для паттерн-матчинга с извлечением полей
case class Person(name: String, age: Int)
case class Employee(name: String, age: Int, salary: Double)

def describe(p: Person): String = p match {
  case Person("Alice", age) => s"Alice is $age years old"
  case Person(name, age) if age < 18 => s"$name is a minor"
  case Person(name, age) => s"$name is $age years old"
}
```

**Pattern matching** с **case classes** позволяет декомпозировать структуры данных и извлекать значения полей.

## **Pattern Matching** с коллекциями

### Списки

```scala
// Сопоставление со списком: Nil и head :: tail
def describeList(list: List[Int]): String = list match {
  case Nil => "empty list"
  case head :: Nil => s"single element: $head"
  case head :: tail => s"head: $head, tail: ${tail.mkString(", ")}"
  case List(a, b, c) => s"three elements: $a, $b, $c"
}
```

### Векторы

```scala
def describeVector(vec: Vector[Int]): String = vec match {
  case Vector() => "empty"
  case Vector(x) => s"single: $x"
  case Vector(x, y, _*) => s"starts with: $x, $y"
}
```

## **Guards** (**охранники**)

**Guards** позволяют добавлять условия к паттернам, расширяя возможности **pattern matching** за пределы простого сопоставления структур. **Guards** используют ключевое слово `if` для проверки дополнительных условий после сопоставления паттерна. Это позволяет обрабатывать более сложные случаи, где одного сопоставления структуры недостаточно, и нужно проверить значения или выполнить вычисления.

**Guards** особенно полезны для проверки диапазонов значений, валидации данных и обработки случаев, которые зависят от вычисляемых условий, а не только от структуры данных. **Guards** выполняются только после успешного сопоставления паттерна, что делает их эффективными для фильтрации совпадений.

**Guards** позволяют добавлять условия к паттернам:**

```scala
// Guards проверяют дополнительные условия после сопоставления паттерна
// Переменная n извлекается из паттерна и используется в условии
def matchWithGuard(x: Int): String = x match {
  // Паттерн n сопоставляется с любым Int, затем проверяется условие n < 0
  case n if n < 0 => "negative"
  
  // Проверка на равенство нулю
  case n if n == 0 => "zero"
  
  // Проверка диапазона значений
  // Условие проверяет, что число положительное и меньше 10
  case n if n > 0 && n < 10 => "small positive"
  
  // Обработка всех остальных случаев (большие положительные числа)
  case _ => "large positive"
}

// Guards позволяют создавать более выразительные условия
// Вместо множественных if-else можно использовать pattern matching с guards
```

**Guards** расширяют возможности **pattern matching**, позволяя проверять дополнительные условия. Это делает **pattern matching** более гибким и выразительным, позволяя обрабатывать сложные случаи, где нужно проверить не только структуру данных, но и значения или выполнить вычисления. **Guards** особенно полезны для валидации данных, проверки диапазонов и обработки бизнес-логики.

## **Type Patterns**

**Type patterns** позволяют сопоставлять типы, проверяя тип значения во время выполнения. Это особенно полезно при работе с полиморфными данными, где тип значения неизвестен на этапе компиляции, или при интеграции с **Java** кодом, где типы могут быть стерты. **Type patterns** используют синтаксис `**value**: **Type**` для проверки типа значения.

**Type patterns** следует использовать осторожно, так как они нарушают безопасность типов и могут привести к ошибкам времени выполнения. Предпочтительнее использовать **sealed** классы и **case** классы для типобезопасного **pattern matching**, но **type patterns** полезны для работы с внешними **API** или **legacy** кодом.

**Type patterns** позволяют сопоставлять типы:**

```scala
// Type patterns проверяют тип значения во время выполнения
// Это полезно для работы с полиморфными данными или интеграции с Java
def matchType(x: Any): String = x match {
  // Проверка типа String
  // Если x является String, переменная s получает значение x
  case s: String => s"String: $s"
  
  // Проверка типа Int
  // Если x является Int, переменная i получает значение x
  case i: Int => s"Int: $i"
  
  // Проверка типа Double
  // Если x является Double, переменная d получает значение x
  case d: Double => s"Double: $d"
  
  // Обработка всех остальных типов
  case _ => "unknown type"
}

// Type patterns особенно полезны при работе с Any или при интеграции с Java
// Однако предпочтительнее использовать sealed классы для типобезопасного pattern matching
```

**Type patterns** полезны для работы с полиморфными данными и обработки различных типов, но их следует использовать только когда типобезопасные альтернативы недоступны. **Type patterns** нарушают безопасность типов, так как проверка типа происходит во время выполнения, а не на этапе компиляции. Для типобезопасного **pattern matching** лучше использовать **sealed** классы и **case** классы, которые обеспечивают проверку типов на этапе компиляции.

## **Sealed Classes** и **Exhaustive Matching**

**Sealed classes** обеспечивают исчерпывающий анализ:**

```scala
sealed trait Result[+A]
case class Success[A](value: A) extends Result[A]
case class Failure(error: String) extends Result[Nothing]

def handleResult[A](result: Result[A]): String = result match {
  case Success(value) => s"Success: $value"
  case Failure(error) => s"Failure: $error"
  // Компилятор предупредит, если не все случаи обработаны
}
```

**Sealed classes** гарантируют, что все возможные случаи будут обработаны, что повышает безопасность кода.

## Лучшие практики

### Использование **sealed traits** для безопасности

```scala
// Хорошо - sealed trait обеспечивает исчерпывающий анализ
sealed trait Status
case object Active extends Status
case object Inactive extends Status

def handleStatus(status: Status): String = status match {
  case Active => "active"
  case Inactive => "inactive"
  // Компилятор проверит полноту
}
```

### Избегание **catch-all** паттернов

```scala
// Хорошо - явная обработка всех случаев
def matchValue(x: Int): String = x match {
  case 0 => "zero"
  case 1 => "one"
  case 2 => "two"
  case n => s"other: $n"  // явный паттерн вместо _
}

// Плохо - использование _ без необходимости
def matchValueBad(x: Int): String = x match {
  case 0 => "zero"
  case _ => "other"  // теряется информация
}
```

### **Pattern Matching** с **Tuples**

**Tuples** можно декомпозировать через **pattern matching**:**

```scala
def processTuple(tuple: (String, Int, Boolean)): String = tuple match {
  case ("admin", id, true) => s"Admin $id is active"
  case (name, id, true) => s"User $name ($id) is active"
  case (name, id, false) => s"User $name ($id) is inactive"
}

// Вложенные tuples
def processNested(tuple: ((String, Int), Boolean)): String = tuple match {
  case ((name, age), true) => s"$name ($age) is active"
  case ((name, age), false) => s"$name ($age) is inactive"
}
```

### **Pattern Matching** с **Option**

**Option** часто используется с **pattern matching**:**

```scala
def processOption(opt: Option[Int]): String = opt match {
  case Some(value) if value > 0 => s"Positive value: $value"
  case Some(value) => s"Non-positive value: $value"
  case None => "No value"
}

// В for-comprehension
val result = for {
  a <- Some(5)
  b <- Some(3)
} yield a + b  // Some(8)
```

### **Pattern Matching** с **Try**

**Try** можно обрабатывать через **pattern matching**:**

```scala
import scala.util.{Try, Success, Failure}

def processTry(t: Try[Int]): String = t match {
  case Success(value) => s"Success: $value"
  case Failure(e: ArithmeticException) => s"Arithmetic error: ${e.getMessage}"
  case Failure(e) => s"Error: ${e.getMessage}"
}
```

### **Pattern Matching** с **Either**

**Either** обрабатывается через **pattern matching**:**

```scala
def processEither(either: Either[String, Int]): String = either match {
  case Right(value) => s"Success: $value"
  case Left(error) => s"Error: $error"
}
```

### Вложенный **Pattern Matching**

**Можно вкладывать **pattern matching**:**

```scala
case class Address(street: String, city: String)
case class Person(name: String, age: Int, address: Address)

def processPerson(person: Person): String = person match {
  case Person("Alice", age, Address("Main St", city)) => 
    s"Alice, $age, lives on Main St in $city"
  case Person(name, age, Address(street, "New York")) => 
    s"$name, $age, lives on $street in New York"
  case Person(name, age, address) => 
    s"$name, $age, lives at ${address.street}, ${address.city}"
}
```

### **Pattern Matching** в функциях

**Pattern matching** можно использовать прямо в определении функции:**

```scala
// Pattern matching в параметрах функции
def processList(list: List[Int]): String = list match {
  case Nil => "empty"
  case head :: Nil => s"single: $head"
  case head :: tail => s"head: $head, tail: ${tail.mkString(", ")}"
}

// Pattern matching с частичными функциями
val processNumber: PartialFunction[Int, String] = {
  case 0 => "zero"
  case 1 => "one"
  case 2 => "two"
  case n if n > 0 => "positive"
}

processNumber(0)  // "zero"
processNumber(5)  // "positive"
```

### **Pattern Matching** с регулярными выражениями

```scala
val EmailPattern = """(\w+)@(\w+)\.(\w+)""".r

def extractEmail(email: String): Option[(String, String, String)] = email match {
  case EmailPattern(user, domain, tld) => Some((user, domain, tld))
  case _ => None
}

extractEmail("alice@example.com")  // Some(("alice", "example", "com"))
extractEmail("invalid")  // None
```

### **Pattern Matching** с типами и значениями

**Можно комбинировать проверку типов и значений:**

```scala
def processValue(value: Any): String = value match {
  case s: String if s.length > 10 => s"Long string: ${s.take(10)}..."
  case s: String => s"String: $s"
  case i: Int if i > 0 => s"Positive int: $i"
  case i: Int => s"Int: $i"
  case d: Double => s"Double: $d"
  case _ => "Unknown"
}
```

### Практический пример: Парсинг **JSON**

```scala
import play.api.libs.json._

def parseJson(json: JsValue): String = json match {
  case JsObject(fields) => 
    fields.map { case (key, value) => s"$key: ${parseJson(value)}" }.mkString(", ")
  case JsArray(elements) => 
    elements.map(parseJson).mkString("[", ", ", "]")
  case JsString(value) => s""""$value""""
  case JsNumber(value) => value.toString
  case JsBoolean(value) => value.toString
  case JsNull => "null"
}
```

### Практический пример: Обработка **AST**

```scala
sealed trait Expr
case class Number(n: Int) extends Expr
case class Add(left: Expr, right: Expr) extends Expr
case class Multiply(left: Expr, right: Expr) extends Expr
case class Variable(name: String) extends Expr

def evaluate(expr: Expr, vars: Map[String, Int]): Int = expr match {
  case Number(n) => n
  case Add(left, right) => evaluate(left, vars) + evaluate(right, vars)
  case Multiply(left, right) => evaluate(left, vars) * evaluate(right, vars)
  case Variable(name) => vars.getOrElse(name, 0)
}

// Использование
val expr = Add(Multiply(Number(2), Number(3)), Variable("x"))
evaluate(expr, Map("x" -> 5))  // 11
```

### **Pattern Matching** в циклах

**Pattern matching** можно использовать в циклах:**

```scala
val list = List(Some(1), None, Some(2), Some(3), None)

// Извлечение значений из Option
for (Some(value) <- list) {
  println(value)
}
// 1
// 2
// 3

// Pattern matching в map
val extracted = list.collect {
  case Some(value) => value
}
// List(1, 2, 3)
```

### @ (**as-pattern**)

**Оператор @ позволяет сохранить весь паттерн:**

```scala
val list = List(1, 2, 3, 4, 5)

list match {
  case list @ List(1, 2, _*) => 
    println(s"List starts with 1, 2: $list")
  case _ => 
    println("Other list")
}
```

## Лучшие практики

### Использование **sealed traits** для безопасности

```scala
// Хорошо - sealed trait обеспечивает исчерпывающий анализ
sealed trait Status
case object Active extends Status
case object Inactive extends Status

def handleStatus(status: Status): String = status match {
  case Active => "active"
  case Inactive => "inactive"
  // Компилятор проверит полноту
}
```

### Избегание **catch-all** паттернов

```scala
// Хорошо - явная обработка всех случаев
def matchValue(x: Int): String = x match {
  case 0 => "zero"
  case 1 => "one"
  case 2 => "two"
  case n => s"other: $n"  // явный паттерн вместо _
}

// Плохо - использование _ без необходимости
def matchValueBad(x: Int): String = x match {
  case 0 => "zero"
  case _ => "other"  // теряется информация
}
```

### Использование **guards** для сложных условий

```scala
// Хорошо - использование guards для сложных условий
def processNumber(n: Int): String = n match {
  case x if x < 0 => "negative"
  case x if x == 0 => "zero"
  case x if x > 0 && x < 10 => "small positive"
  case x if x >= 10 && x < 100 => "medium positive"
  case _ => "large positive"
}

// Плохо - множественные вложенные if
def processNumberBad(n: Int): String = {
  if (n < 0) "negative"
  else if (n == 0) "zero"
  else if (n > 0 && n < 10) "small positive"
  else "large positive"
}
```


## Решение проблем

Типичные проблемы и решения см. в официальной документации (блок «Полезные ссылки» в начале документа).

## Частые вопросы

Ответы на частые вопросы по теме см. в разделах «Введение» и «Лучшие практики» в документе.

## Заключение

## Дополнительные техники **Pattern Matching**

### Использование **Extractors**

**Extractors** позволяют создавать кастомные паттерны для **pattern matching**.

```scala
// Создание extractor
object Email {
  def unapply(email: String): Option[(String, String)] = {
    val parts = email.split("@")
    if (parts.length == 2) Some(parts(0), parts(1)) else None
  }
}

// Использование extractor
val email = "user@example.com"
email match {
  case Email(user, domain) => println(s"User: $user, Domain: $domain")
  case _ => println("Invalid email")
}
```

### **Pattern Matching** с регулярными выражениями

**Pattern Matching** может использоваться с регулярными выражениями.

```scala
import scala.util.matching.Regex

// Pattern matching с регулярными выражениями
val pattern = new Regex("(\\d+)-(\\d+)")
"123-456" match {
  case pattern(first, second) => println(s"First: $first, Second: $second")
  case _ => println("No match")
}
```

### Исчерпывающий **Pattern Matching**

Исчерпывающий **Pattern Matching** гарантирует обработку всех случаев.

```scala
sealed trait Status
case object Active extends Status
case object Inactive extends Status
case object Pending extends Status

// Исчерпывающий pattern matching
def processStatus(status: Status): String = status match {
  case Active => "Processing"
  case Inactive => "Stopped"
  case Pending => "Waiting"
  // Компилятор предупредит, если не все случаи обработаны
}
```

### Практические примеры: **Pattern Matching** для парсинга **JSON**

```scala
import play.api.libs.json._

def parseJson(json: JsValue): Option[String] = json match {
  case JsString(value) => Some(value)
  case JsNumber(value) => Some(value.toString)
  case JsBoolean(value) => Some(value.toString)
  case JsNull => None
  case JsArray(elements) => 
    Some(elements.map(parseJson).collect { case Some(v) => v }.mkString(", "))
  case JsObject(fields) => 
    Some(fields.map { case (k, v) => s"$k: ${parseJson(v).getOrElse("null")}" }.mkString(", "))
  case _ => None
}
```

### Практические примеры: **Pattern Matching** для обработки **AST**

```scala
sealed trait Expr
case class Number(value: Int) extends Expr
case class Variable(name: String) extends Expr
case class Add(left: Expr, right: Expr) extends Expr
case class Multiply(left: Expr, right: Expr) extends Expr
case class FunctionCall(name: String, args: List[Expr]) extends Expr

class ExpressionEvaluator(variables: Map[String, Int]) {
  def evaluate(expr: Expr): Int = expr match {
    case Number(value) => value
    case Variable(name) => variables.getOrElse(name, 0)
    case Add(left, right) => evaluate(left) + evaluate(right)
    case Multiply(left, right) => evaluate(left) * evaluate(right)
    case FunctionCall(name, args) => 
      name match {
        case "max" => args.map(evaluate).max
        case "min" => args.map(evaluate).min
        case "sum" => args.map(evaluate).sum
        case _ => throw new IllegalArgumentException(s"Unknown function: $name")
      }
  }
}
```

### Практические примеры: **Pattern Matching** для обработки сообщений

```scala
sealed trait Message
case class TextMessage(sender: String, content: String) extends Message
case class ImageMessage(sender: String, url: String, caption: Option[String]) extends Message
case class SystemMessage(content: String) extends Message

def processMessage(message: Message): String = message match {
  case TextMessage(sender, content) => 
    s"[$sender]: $content"
  case ImageMessage(sender, url, Some(caption)) => 
    s"[$sender] sent image ($url) with caption: $caption"
  case ImageMessage(sender, url, None) => 
    s"[$sender] sent image ($url)"
  case SystemMessage(content) => 
    s"[System]: $content"
}
```

**Pattern Matching** является одним из самых мощных инструментов **Scala** для работы с данными. Понимание различных паттернов (**базовые, с case classes, коллекциями, tuples, `Option`, Try, Either**), **guards**, **type patterns**, вложенного **pattern matching**, использования **sealed classes** для исчерпывающего анализа, использования **Extractors**, **Pattern Matching** с регулярными выражениями, исчерпывающего **Pattern Matching**, парсинга **JSON**, обработки **AST**, обработки сообщений и практических применений позволяет создавать безопасный и выразительный код. **Pattern Matching** особенно полезен для декомпозиции данных, обработки различных случаев, создания читаемого кода, создания кастомных паттернов с использованием **Extractors**, работы с регулярными выражениями, гарантии обработки всех случаев с исчерпывающим **Pattern Matching**, парсинга структурированных данных и обработки различных типов сообщений.

### Практические примеры: **Pattern Matching** с регулярными выражениями

```scala
import scala.util.matching.Regex

val emailPattern: Regex = """(\w+)@(\w+\.\w+)""".r

def extractEmail(email: String): Option[(String, String)] = email match {
  case emailPattern(user, domain) => Some((user, domain))
  case _ => None
}

val result = extractEmail("user@example.com")  // Some(("user", "example.com"))
```

### Практические примеры: **Pattern Matching** для обработки **AST**

```scala
sealed trait Expr
case class Number(n: Int) extends Expr
case class Add(left: Expr, right: Expr) extends Expr
case class Multiply(left: Expr, right: Expr) extends Expr

def evaluate(expr: Expr): Int = expr match {
  case Number(n) => n
  case Add(left, right) => evaluate(left) + evaluate(right)
  case Multiply(left, right) => evaluate(left) * evaluate(right)
}

val expr = Add(Number(2), Multiply(Number(3), Number(4)))
val result = evaluate(expr)  // 14
```

### Использование с различными типами для обработки данных

```scala
// Pattern Matching с различными типами
def process(value: Any): String = value match {
  case i: Int => s"Integer: $i"
  case s: String => s"String: $s"
  case d: Double => s"Double: $d"
  case _ => "Unknown type"
}

// Использование
val result1 = process(42)  // "Integer: 42"
val result2 = process("hello")  // "String: hello"
```

### Использование с различными типами для обработки коллекций

```scala
// Pattern Matching с коллекциями
def processList(list: List[Int]): String = list match {
  case Nil => "Empty list"
  case head :: Nil => s"Single element: $head"
  case head :: tail => s"Head: $head, Tail: ${tail.mkString(", ")}"
}

// Использование
val result1 = processList(List())  // "Empty list"
val result2 = processList(List(1))  // "Single element: 1"
val result3 = processList(List(1, 2, 3))  // "Head: 1, Tail: 2, 3"
```

## Дополнительные ресурсы

**Для дальнейшего изучения **Pattern Matching** в **Scala** рекомендуется:**

- [Scala Pattern Matching Documentation](https://docs.scala-lang.org/tour/pattern-matching.html)
- [Scala School — Pattern Matching](https://twitter.github.io/scala_school/pattern-matching.html)
- [Pattern Matching in Scala](https://www.baeldung.com/scala/pattern-matching)
