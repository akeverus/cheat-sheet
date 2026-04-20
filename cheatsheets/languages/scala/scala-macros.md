---
title: "Scala Macros"
description: "Полное руководство по макросам в Scala: code generation, compile-time вычисления, метапрограммирование"
tags:
  - scala
  - macros
  - metaprogramming
  - code-generation
difficulty: "advanced"
prerequisites: ["scala/scala-basics.md"]
next: []
updated: "2026-04-20"
related: ["scala/scala-basics.md", "scala/scala-metaprogramming.md"]
---

# Scala Macros

Кратко: полное руководство по макросам в **Scala**: **code generation**, **compile-time** вычисления, метапрограммирование.

## Полезные ссылки

### Официальная документация
- [Scala Macros](https://docs.scala-lang.org/overviews/macros/overview.html)

### См. также
- [[scala-basics|Основы Scala]]

## Содержание

- [Введение в Macros](#введение-в-macros)
  - [Основные применения](#основные-применения)
- [Def Macros](#def-macros)
- [Type Macros](#type-macros)
  - [Пример: Генерация валидаторов](#пример-генерация-валидаторов)
  - [Пример: Оптимизация строковых операций](#пример-оптимизация-строковых-операций)
- [Macro Annotations](#macro-annotations)
- [Quasiquotes](#quasiquotes)
- [Compile-time вычисления](#compile-time-вычисления)
- [Type-level программирование с макросами](#type-level-программирование-с-макросами)
- [Практические примеры использования макросов](#практические-примеры-использования-макросов)
  - [Генерация toString, equals, hashCode](#генерация-tostring-equals-hashcode)
  - [Генерация сериализаторов](#генерация-сериализаторов)
- [Ограничения макросов](#ограничения-макросов)
- [Лучшие практики](#лучшие-практики)
  - [Использование макросов для оптимизации](#использование-макросов-для-оптимизации)
  - [Документирование макросов](#документирование-макросов)
  - [Тестирование макросов](#тестирование-макросов)
- [Продвинутые техники работы с макросами](#продвинутые-техники-работы-с-макросами)
  - [Quasiquotes](#quasiquotes-1)
  - [Macro Annotations](#macro-annotations-1)
  - [Type Macros](#type-macros-1)
- [Заключение (расширенное)](#заключение-расширенное)
- [Дополнительные техники работы с макросами](#дополнительные-техники-работы-с-макросами)
  - [Использование Macro Paradise](#использование-macro-paradise)
  - [Генерация кода с использованием макросов](#генерация-кода-с-использованием-макросов)
  - [Оптимизация производительности с макросами](#оптимизация-производительности-с-макросами)
  - [Практические примеры: Генерация toString с макросами](#практические-примеры-генерация-tostring-с-макросами)
  - [Практические примеры: Генерация equals с макросами](#практические-примеры-генерация-equals-с-макросами)
  - [Практические примеры: Генерация копирующих методов](#практические-примеры-генерация-копирующих-методов)
  - [Практические примеры: Генерация Builder паттерна](#практические-примеры-генерация-builder-паттерна)
  - [Практические примеры: Генерация сериализаторов](#практические-примеры-генерация-сериализаторов)
  - [Практические примеры: Генерация валидаторов](#практические-примеры-генерация-валидаторов)
  - [Практические примеры: Генерация тестов](#практические-примеры-генерация-тестов)
  - [Практические примеры: Генерация mock объектов](#практические-примеры-генерация-mock-объектов)
- [Решение проблем](#решение-проблем)
- [Частые вопросы](#частые-вопросы)
- [Заключение](#заключение)
  - [Использование с различными техниками для генерации кода](#использование-с-различными-техниками-для-генерации-кода)
  - [Использование с различными техниками для оптимизации](#использование-с-различными-техниками-для-оптимизации)
- [Дополнительные ресурсы](#дополнительные-ресурсы)

## Введение в Macros

Макросы в **Scala** позволяют выполнять вычисления и генерировать код на этапе компиляции. Макросы предоставляют мощные возможности метапрограммирования.

### Основные применения

- **Code Generation**: автоматическая генерация кода
- **Compile-time вычисления**: вычисления на этапе компиляции
- **Оптимизация**: оптимизация кода на этапе компиляции
- **DSL**: создание **domain-specific languages**

## Def Macros

**Def macros** позволяют генерировать код на этапе компиляции:**

```scala
import scala.language.experimental.macros
import scala.reflect.macros.blackbox.Context

def assert(condition: Boolean): Unit = macro assertImpl

def assertImpl(c: Context)(condition: c.Expr[Boolean]): c.Expr[Unit] = {
  import c.universe._
  val q"$expr" = condition
  c.Expr(q"""
    if (!$condition) {
      throw new AssertionError(s"Assertion failed: ${$expr}")
    }
  """)
}
```

**Def macros** позволяют создавать оптимизированный код на этапе компиляции.

## Type Macros

**Type macros** позволяют генерировать типы на этапе компиляции:**

```scala
import scala.language.experimental.macros
import scala.reflect.macros.blackbox.Context

type MyType = macro MyTypeMacro

def MyTypeMacro(c: Context): c.Tree = {
  import c.universe._
  // Генерация типа
  tq"List[String]"
}
```

**Type macros** позволяют создавать типы динамически на этапе компиляции.

### Пример: Генерация валидаторов

**Макросы можно использовать для автоматической генерации валидаторов на основе типов:**

```scala
import scala.language.experimental.macros
import scala.reflect.macros.blackbox.Context

def validate[T](value: T): List[String] = macro validateImpl[T]

def validateImpl[T: c.WeakTypeTag](c: Context)(value: c.Expr[T]): c.Expr[List[String]] = {
  import c.universe._
  val tpe = weakTypeOf[T]
  val errors = tpe.members.collect {
    case m: MethodSymbol if m.isCaseAccessor =>
      q"if (${value}.${m.name} == null) List(${m.name.toString} + \" cannot be null\") else Nil"
  }.toList

  c.Expr[List[String]](q"List(..$errors).flatten")
}
```

Этот макрос автоматически генерирует валидатор, проверяющий все поля типа на **null**.

### Пример: Оптимизация строковых операций

**Макросы могут оптимизировать строковые операции на этапе компиляции:**

```scala
def log(message: String): Unit = macro logImpl

def logImpl(c: Context)(message: c.Expr[String]): c.Expr[Unit] = {
  import c.universe._
  val level = sys.props.get("log.level").getOrElse("INFO")
  if (level == "DEBUG") {
    c.Expr(q"println($message)")
  } else {
    c.Expr(q"()")
  }
}
```

Этот макрос полностью удаляет логирование на этапе компиляции, если уровень логирования не **DEBUG**.

## Macro Annotations

**Macro annotations** позволяют добавлять функциональность к классам и методам:**

```scala
import scala.annotation.StaticAnnotation
import scala.language.experimental.macros
import scala.reflect.macros.blackbox.Context

class cached extends StaticAnnotation {
  def macroTransform(annottees: Any*): Any = macro CachedMacro.impl
}

object CachedMacro {
  def impl(c: Context)(annottees: c.Expr[Any]*): c.Expr[Any] = {
    import c.universe._
    // Генерация кода с кэшированием
    // ...
  }
}

@cached
def expensiveComputation(x: Int): Int = {
  // дорогие вычисления
  x * x
}
```

**Macro annotations** позволяют добавлять функциональность к коду без изменения исходного кода.

## Quasiquotes

**Quasiquotes** предоставляют удобный синтаксис для работы с **AST**:**

```scala
import scala.reflect.macros.blackbox.Context
import scala.language.experimental.macros

def debug(expr: Any): Unit = macro debugImpl

def debugImpl(c: Context)(expr: c.Expr[Any]): c.Expr[Unit] = {
  import c.universe._
  val tree = q"""
    println(${expr.toString} + " = " + $expr)
  """
  c.Expr[Unit](tree)
}
```

**Quasiquotes** делают работу с **AST** более читаемой и менее подверженной ошибкам.

## Compile-time вычисления

**Макросы могут выполнять вычисления на этапе компиляции:**

```scala
import scala.language.experimental.macros
import scala.reflect.macros.blackbox.Context

def power(base: Int, exp: Int): Int = macro powerImpl

def powerImpl(c: Context)(base: c.Expr[Int], exp: c.Expr[Int]): c.Expr[Int] = {
  import c.universe._
  val baseValue = c.eval(c.Expr[Int](c.untypecheck(base.tree)))
  val expValue = c.eval(c.Expr[Int](c.untypecheck(exp.tree)))
  val result = Math.pow(baseValue, expValue).toInt
  c.Expr[Int](q"$result")
}
```

Этот макрос вычисляет степень на этапе компиляции, если оба аргумента известны.

## Type-level программирование с макросами

**Макросы могут использоваться для **type-level** программирования:**

```scala
import scala.language.experimental.macros
import scala.reflect.macros.blackbox.Context

def typeName[T]: String = macro typeNameImpl[T]

def typeNameImpl[T: c.WeakTypeTag](c: Context): c.Expr[String] = {
  import c.universe._
  val name = weakTypeOf[T].toString
  c.Expr[String](q"$name")
}

val name = typeName[Int]  // "Int" на этапе компиляции
```

Этот макрос возвращает имя типа как строку, вычисляемую на этапе компиляции.

## Практические примеры использования макросов

### Генерация toString, equals, hashCode

**Макросы могут автоматически генерировать стандартные методы:**

```scala
import scala.language.experimental.macros
import scala.reflect.macros.blackbox.Context

def autoToString[T]: String = macro autoToStringImpl[T]

def autoToStringImpl[T: c.WeakTypeTag](c: Context): c.Expr[String] = {
  import c.universe._
  val tpe = weakTypeOf[T]
  val fields = tpe.members.collect {
    case m: MethodSymbol if m.isCaseAccessor =>
      q"${m.name.toString} = " + ${c.Expr(q"obj.${m.name}")}
  }
  c.Expr[String](q"""${tpe.toString}(" + List(..$fields).mkString(", ") + ")""")
}
```

### Генерация сериализаторов

**Макросы могут генерировать эффективные сериализаторы:**

```scala
import scala.language.experimental.macros
import scala.reflect.macros.blackbox.Context

def serialize[T](obj: T): String = macro serializeImpl[T]

def serializeImpl[T: c.WeakTypeTag](c: Context)(obj: c.Expr[T]): c.Expr[String] = {
  import c.universe._
  val tpe = weakTypeOf[T]
  val fields = tpe.members.collect {
    case m: MethodSymbol if m.isCaseAccessor =>
      q"${m.name.toString}: " + ${c.Expr(q"$obj.${m.name}")}
  }
  c.Expr[String](q"""{ " + List(..$fields).mkString(", ") + " }""")
}
```

## Ограничения макросов

**Макросы имеют некоторые ограничения:**

1. **Сложность отладки**: ошибки в макросах сложнее отлаживать
2. **Время компиляции**: макросы увеличивают время компиляции
3. **IDE поддержка**: не все **IDE** полностью поддерживают макросы
4. **Портативность**: макросы могут быть специфичны для версии компилятора

## Лучшие практики

### Использование макросов для оптимизации

```scala
// Хорошо - использование макросов для compile-time оптимизации
def optimizedMethod: Int = macro optimizeImpl

// Плохо - runtime вычисления там, где можно использовать макросы
def slowMethod: Int = {
  // вычисления во время выполнения
}
```

### Документирование макросов

**Всегда документируйте макросы, объясняя их поведение и ограничения:**

```scala
/
 * Макрос для генерации валидаторов.
 * Генерирует код, проверяющий все поля типа на null.
 *
 * @param value значение для валидации
 * @return список ошибок валидации
 */
def validate[T](value: T): List[String] = macro validateImpl[T]
```

### Тестирование макросов

**Тестируйте макросы так же, как и обычный код:**

```scala
class MacroTest extends AnyFlatSpec {
  "validate macro" should "return errors for null fields" in {
    case class User(name: String, age: Int)
    val user = User(null, 25)
    val errors = validate(user)
    assert(errors.contains("name cannot be null"))
  }
}
```

## Продвинутые техники работы с макросами

### Quasiquotes

**Quasiquotes** предоставляют удобный способ создания **AST**.

```scala
import scala.reflect.macros.blackbox.Context
import scala.language.experimental.macros

def debug(param: Any): Unit = macro debugImpl

def debugImpl(c: Context)(param: c.Expr[Any]): c.Expr[Unit] = {
  import c.universe._
  val paramRep = show(param.tree)
  val paramExpr = c.Expr[Any](param.tree)
  reify {
    println(s"$paramRep = $paramExpr")
  }
}
```

### Macro Annotations

**Macro Annotations** позволяют создавать аннотации, которые генерируют код.

```scala
import scala.annotation.StaticAnnotation
import scala.macros.annotation._

class Builder extends StaticAnnotation {
  def macroTransform(annottees: Any*): Any = macro BuilderMacro.impl
}
```

### Type Macros

**Type Macros** позволяют генерировать типы на этапе компиляции.

```scala
import scala.language.experimental.macros
import scala.reflect.macros.blackbox.Context

def materialize[T]: T = macro MaterializeMacro.materialize[T]

class MaterializeMacro(val c: Context) {
  def materialize[T: c.WeakTypeTag]: c.Expr[T] = {
    // Генерация кода для типа T
    c.Expr[T](q"new ${c.weakTypeOf[T]}()")
  }
}
```

## Заключение (расширенное)

## Дополнительные техники работы с макросами

### Использование Macro Paradise

**Macro Paradise** предоставляет расширенные возможности для работы с макросами.

```scala
import scala.language.experimental.macros
import scala.reflect.macros.blackbox.Context

// Macro Paradise для расширенных возможностей
def debugMacro[T](expr: T): T = macro debugMacroImpl

def debugMacroImpl[T: c.WeakTypeTag](c: Context)(expr: c.Expr[T]): c.Expr[T] = {
  import c.universe._
  val exprRep = show(expr.tree)
  c.Expr[T](q"""
    {
      val result = $expr
      println(s"Expression: $exprRep = " + result)
      result
    }
  """)
}
```

### Генерация кода с использованием макросов

Генерация кода позволяет автоматизировать создание **boilerplate** кода.

```scala
// Генерация case class из конфигурации
def generateCaseClass(className: String, fields: Map[String, String]): String = macro generateCaseClassImpl

def generateCaseClassImpl(c: Context)(className: c.Expr[String], fields: c.Expr[Map[String, String]]): c.Expr[String] = {
  import c.universe._
  // Генерация кода для case class
  c.Expr[String](q""" "case class $className(...)" """)
}
```

### Оптимизация производительности с макросами

Макросы могут использоваться для оптимизации производительности.

```scala
// Inline макрос для оптимизации
def inlineOptimized[T](expr: T): T = macro inlineOptimizedImpl

def inlineOptimizedImpl[T: c.WeakTypeTag](c: Context)(expr: c.Expr[T]): c.Expr[T] = {
  import c.universe._
  // Inline оптимизация выражения
  expr
}
```

### Практические примеры: Генерация toString с макросами

```scala
import scala.language.experimental.macros
import scala.reflect.macros.blackbox

def toStringMacro[A](a: A): String = macro toStringMacroImpl[A]

def toStringMacroImpl[A: c.WeakTypeTag](c: blackbox.Context)(a: c.Expr[A]): c.Expr[String] = {
  import c.universe._
  val tpe = weakTypeOf[A]
  val fields = tpe.decls.collect {
    case m: MethodSymbol if m.isCaseAccessor => m
  }

  val fieldStrings = fields.map { field =>
    val fieldName = field.name.toString
    q"$fieldName + \"=\" + $a.$field"
  }

  val className = tpe.typeSymbol.name.toString
  val result = q"$className + \"(\" + ${fieldStrings.mkString(" + \", \" + ")} + \")\""
  c.Expr[String](result)
}

// Использование
case class Person(name: String, age: Int)
val person = Person("Alice", 30)
val str = toStringMacro(person)  // "Person(name=Alice, age=30)"
```

### Практические примеры: Генерация equals с макросами

```scala
import scala.language.experimental.macros
import scala.reflect.macros.blackbox

def equalsMacro[A](a: A, b: A): Boolean = macro equalsMacroImpl[A]

def equalsMacroImpl[A: c.WeakTypeTag](
  c: blackbox.Context
)(a: c.Expr[A], b: c.Expr[A]): c.Expr[Boolean] = {
  import c.universe._
  val tpe = weakTypeOf[A]
  val fields = tpe.decls.collect {
    case m: MethodSymbol if m.isCaseAccessor => m
  }

  val comparisons = fields.map { field =>
    q"$a.$field == $b.$field"
  }

  val result = comparisons.foldLeft(q"true": c.Tree) { (acc, comp) =>
    q"$acc && $comp"
  }
  c.Expr[Boolean](result)
}
```

### Практические примеры: Генерация копирующих методов

```scala
import scala.language.experimental.macros
import scala.reflect.macros.blackbox.Context

// Макрос для генерации copy методов с обновлениями
def copyWith[A: c.WeakTypeTag](c: Context)(updates: c.Expr[Map[String, Any]]): c.Expr[A] = {
  import c.universe._

  val A = weakTypeOf[A]
  val companion = A.typeSymbol.companion

  // Получение полей case class
  val fields = A.decls.collect {
    case m: MethodSymbol if m.isCaseAccessor => m
  }

  // Генерация аргументов для конструктора
  val args = fields.map { field =>
    val fieldName = field.name.toString
    q"""$updates.getOrElse($fieldName, ${TermName(fieldName)})"""
  }

  c.Expr[A](q"$companion(..$args)")
}
```

### Практические примеры: Генерация Builder паттерна

```scala
import scala.language.experimental.macros
import scala.reflect.macros.blackbox.Context

// Макрос для генерации Builder классов
def builder[A: c.WeakTypeTag](c: Context): c.Expr[Builder[A]] = {
  import c.universe._

  val A = weakTypeOf[A]
  val fields = A.decls.collect {
    case m: MethodSymbol if m.isCaseAccessor => m
  }

  // Генерация методов set для каждого поля
  val setters = fields.map { field =>
    val fieldName = field.name
    val fieldType = field.returnType
    q"""
      def set$fieldName(value: $fieldType): Builder = {
        this.copy($fieldName = Some(value))
      }
    """
  }

  // Генерация метода build
  val buildArgs = fields.map { field =>
    val fieldName = field.name
    q"${TermName(s"${fieldName}Option")}.get"
  }

  q"""
    new Builder[A] {
      ..$setters

      def build: A = {
        $companion(..$buildArgs)
      }
    }
  """
}
```

### Практические примеры: Генерация сериализаторов

```scala
import scala.language.experimental.macros
import scala.reflect.macros.blackbox.Context

// Макрос для генерации JSON сериализаторов
def jsonEncoder[A: c.WeakTypeTag](c: Context): c.Expr[JsonEncoder[A]] = {
  import c.universe._

  val A = weakTypeOf[A]
  val fields = A.decls.collect {
    case m: MethodSymbol if m.isCaseAccessor => m
  }

  // Генерация кода для сериализации полей
  val fieldEncodings = fields.map { field =>
    val fieldName = field.name.toString
    val fieldAccess = q"obj.${field.name}"
    q"""
      "\"$fieldName\": " + encode($fieldAccess)
    """
  }

  val jsonBody = fieldEncodings.reduceLeft { (acc, field) =>
    q"$acc + \", \" + $field"
  }

  q"""
    new JsonEncoder[$A] {
      def encode(obj: $A): String = {
        "{" + $jsonBody + "}"
      }
    }
  """
}
```

### Практические примеры: Генерация валидаторов

```scala
import scala.language.experimental.macros
import scala.reflect.macros.blackbox.Context

// Макрос для генерации валидаторов на основе аннотаций
def validator[A: c.WeakTypeTag](c: Context): c.Expr[Validator[A]] = {
  import c.universe._

  val A = weakTypeOf[A]
  val fields = A.decls.collect {
    case m: MethodSymbol if m.isCaseAccessor => m
  }

  // Генерация правил валидации для каждого поля
  val validations = fields.map { field =>
    val fieldName = field.name.toString
    val fieldAccess = q"obj.${field.name}"

    // Получение аннотаций поля
    val annotations = field.annotations

    // Генерация проверок на основе аннотаций
    q"""
      val fieldErrors = List.empty[String]
      if ($fieldAccess == null) {
        fieldErrors :+ "$fieldName is required"
      }
      fieldErrors
    """
  }

  q"""
    new Validator[$A] {
      def validate(obj: $A): List[String] = {
        List(..$validations).flatten
      }
    }
  """
}
```

### Практические примеры: Генерация тестов

```scala
import scala.language.experimental.macros
import scala.reflect.macros.blackbox.Context

// Макрос для генерации тестовых кейсов
def generateTests[A: c.WeakTypeTag](c: Context): c.Expr[Unit] = {
  import c.universe._

  val A = weakTypeOf[A]
  val methods = A.decls.collect {
    case m: MethodSymbol if m.isPublic && !m.isConstructor => m
  }

  // Генерация тестов для каждого метода
  val tests = methods.map { method =>
    val methodName = method.name.toString
    q"""
      test("test $methodName") {
        val instance = new $A
        val result = instance.$method()
        assert(result != null)
      }
    """
  }

  q"""
    ..$tests
  """
}
```

### Практические примеры: Генерация mock объектов

```scala
import scala.language.experimental.macros
import scala.reflect.macros.blackbox.Context

// Макрос для генерации mock объектов
def mock[A: c.WeakTypeTag](c: Context): c.Expr[Mock[A]] = {
  import c.universe._

  val A = weakTypeOf[A]
  val methods = A.decls.collect {
    case m: MethodSymbol if m.isAbstract => m
  }

  // Генерация реализаций методов
  val methodImpls = methods.map { method =>
    val methodName = method.name
    val returnType = method.returnType
    q"""
      override def $methodName(..${method.paramLists.flatten.map(_.asTerm)}) = {
        mockCalls.getOrElse("$methodName", () => defaultReturn[$returnType])
      }
    """
  }

  q"""
    new Mock[$A] {
      private val mockCalls = scala.collection.mutable.Map[String, () => Any]()

      ..$methodImpls

      def when(methodName: String)(fn: () => Any): Unit = {
        mockCalls(methodName) = fn
      }
    }
  """
}
```


## Решение проблем

Типичные проблемы и решения см. в официальной документации (блок «Полезные ссылки» в начале документа).

## Частые вопросы

Ответы на частые вопросы по теме см. в разделах «Введение» и «Лучшие практики» в документе.

## Заключение

Макросы предоставляют мощные возможности метапрограммирования в **Scala**. Понимание **def macros**, **type macros**, **macro annotations**, **quasiquotes**, использования **Macro Paradise**, генерации кода с использованием макросов, оптимизации производительности с макросами, генерации **toString**, **equals** и других методов и их практических применений позволяет создавать оптимизированный и генерируемый код на этапе компиляции. Макросы особенно полезны для генерации **boilerplate** кода, оптимизации производительности, создания **DSL**, работы с типами на этапе компиляции, использования **Macro Paradise** для расширенных возможностей, генерации кода для автоматизации создания **boilerplate** кода, оптимизации производительности с макросами, генерации методов класса и автоматизации создания **boilerplate** кода. Однако важно использовать их с осторожностью, учитывая сложность отладки и влияние на время компиляции. Макросы особенно важны для создания библиотек и фреймворков, которые требуют генерации кода, оптимизации производительности, и работы с типами на этапе компиляции.

Макросы предоставляют мощные инструменты для генерации **copy** методов, **Builder** паттерна, сериализаторов, валидаторов, тестов и **mock** объектов. Понимание этих техник позволяет создавать сложные, оптимизированные программы с минимальным **boilerplate** кодом.

### Использование с различными техниками для генерации кода

```scala
import scala.language.experimental.macros
import scala.reflect.macros.blackbox.Context

// Генерация case class из обычного класса
def generateCaseClass[A: c.WeakTypeTag](c: Context): c.Expr[Any] = {
  import c.universe._

  val A = weakTypeOf[A]
  val fields = A.decls.collect {
    case m: MethodSymbol if m.isCaseAccessor => m
  }

  // Генерация case class
  val caseClassFields = fields.map { field =>
    val fieldName = field.name
    val fieldType = field.returnType
    q"$fieldName: $fieldType"
  }

  q"""
    case class GeneratedCaseClass(..$caseClassFields)
  """
}
```

### Использование с различными техниками для оптимизации

```scala
// Использование макросов для compile-time оптимизации
def optimizedMap[A, B](list: List[A], f: A => B): List[B] = macro optimizedMapImpl

def optimizedMapImpl[A: c.WeakTypeTag, B: c.WeakTypeTag](
  c: Context
)(list: c.Expr[List[A]], f: c.Expr[A => B]): c.Expr[List[B]] = {
  import c.universe._

  // Оптимизация на этапе компиляции
  q"""
    $list.map($f)
  """
}
```

## Дополнительные ресурсы

**Для дальнейшего изучения макросов в **Scala** рекомендуется:**

- [Scala Macros Documentation](https://docs.scala-lang.org/overviews/macros/overview.html)
- [Macro Paradise](https://docs.scala-lang.org/overviews/macros/paradise.html)
- [Scala Macros Tutorial](https://docs.scala-lang.org/overviews/macros/implicits.html)
- [Scala 3 Macros](https://docs.scala-lang.org/scala3/guides/macros/)
