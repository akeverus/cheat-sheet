---
title: "Scala Reflection"
description: "Полное руководство по рефлексии в Scala: runtime reflection, type tags, class tags, интроспекция типов"
tags: ["scala", "reflection", "runtime", "type-tags", "introspection"]
difficulty: "advanced"
prerequisites: ["scala/scala-basics.md"]
next: []
updated: "2025-01-16"
related: ["scala/scala-basics.md", "scala/scala-type-system.md"]
---

# Scala Reflection

Кратко: полное руководство по рефлексии в Scala: runtime reflection, type tags, class tags, интроспекция типов.

**Дата последнего обновления:** 2025-01-16

## Полезные ссылки

### Официальная документация
- [Scala Reflection](https://docs.scala-lang.org/overviews/reflection/overview.html)

### См. также
- `./scala-basics.md` - основы Scala
- `./scala-type-system.md` - система типов

## Содержание

- [Введение в Reflection](#введение-в-reflection)
- [Type Tags](#type-tags)
- [Class Tags](#class-tags)
- [Runtime Reflection](#runtime-reflection)
- [Лучшие практики](#лучшие-практики)

## Введение в Reflection

Reflection позволяет получать информацию о типах и структурах во время выполнения. Scala предоставляет мощные инструменты для рефлексии.

### Основные применения

- **Интроспекция типов**: получение информации о типах во время выполнения
- **Динамическое создание объектов**: создание экземпляров классов по имени
- **Генерация кода**: создание кода на основе метаданных
- **Сериализация**: автоматическая сериализация на основе типов

## Type Tags

Type Tags сохраняют информацию о типах во время выполнения:

```scala
import scala.reflect.runtime.universe._

def getType[T: TypeTag](obj: T): Type = {
  typeOf[T]
}

// Использование
val intType = getType(42)  // Type для Int
val stringType = getType("hello")  // Type для String
```

Type Tags позволяют получать полную информацию о типах, включая generics.

## Class Tags

Class Tags предоставляют информацию о классах:

```scala
import scala.reflect.ClassTag

def createArray[T: ClassTag](size: Int): Array[T] = {
  Array.ofDim[T](size)
}

// Использование
val intArray = createArray[Int](10)
val stringArray = createArray[String](5)
```

Class Tags необходимы для создания массивов и других операций, требующих информации о классе во время выполнения.

## Runtime Reflection

Runtime Reflection позволяет интроспектировать классы и объекты:

```scala
import scala.reflect.runtime.universe._

// Получение типа класса
val userType = typeOf[User]

// Получение методов
val methods = userType.members.filter(_.isMethod)

// Получение полей
val fields = userType.members.filter(_.isTerm)
```

Runtime Reflection предоставляет полный доступ к метаданным типов во время выполнения.

### Получение информации о типах

Type Tags позволяют получать детальную информацию о типах:

```scala
import scala.reflect.runtime.universe._

def analyzeType[T: TypeTag]: Unit = {
  val tpe = typeOf[T]
  
  println(s"Type: ${tpe}")
  println(s"Type constructor: ${tpe.typeConstructor}")
  println(s"Type args: ${tpe.typeArgs}")
  println(s"Is case class: ${tpe.typeSymbol.isClass && tpe.typeSymbol.asClass.isCaseClass}")
}

// Использование
case class User(name: String, age: Int)
analyzeType[User]
// Type: User
// Type constructor: User
// Type args: List()
// Is case class: true
```

### Работа с generic типами

Type Tags сохраняют информацию о generic типах:

```scala
import scala.reflect.runtime.universe._

def getTypeArgs[T: TypeTag]: List[Type] = {
  typeOf[T].typeArgs
}

// Использование
val listType = getTypeArgs[List[Int]]  // List(Int)
val mapType = getTypeArgs[Map[String, Int]]  // List(String, Int)
```

### Создание экземпляров классов динамически

Runtime Reflection позволяет создавать экземпляры классов по имени:

```scala
import scala.reflect.runtime.universe._

def createInstance[T: TypeTag](args: Any*): T = {
  val mirror = runtimeMirror(getClass.getClassLoader)
  val tpe = typeOf[T]
  val classSymbol = tpe.typeSymbol.asClass
  val classMirror = mirror.reflectClass(classSymbol)
  val constructor = tpe.decl(termNames.CONSTRUCTOR).asMethod
  val constructorMirror = classMirror.reflectConstructor(constructor)
  constructorMirror(args: _*).asInstanceOf[T]
}

// Использование
case class Person(name: String, age: Int)
val person = createInstance[Person]("Alice", 30)
```

### Вызов методов динамически

Runtime Reflection позволяет вызывать методы по имени:

```scala
import scala.reflect.runtime.universe._

def callMethod[T: TypeTag](obj: T, methodName: String, args: Any*): Any = {
  val mirror = runtimeMirror(getClass.getClassLoader)
  val instanceMirror = mirror.reflect(obj)
  val methodSymbol = typeOf[T].member(TermName(methodName)).asMethod
  val methodMirror = instanceMirror.reflectMethod(methodSymbol)
  methodMirror(args: _*)
}

// Использование
case class Calculator() {
  def add(x: Int, y: Int): Int = x + y
  def multiply(x: Int, y: Int): Int = x * y
}

val calc = Calculator()
val sum = callMethod(calc, "add", 5, 3)  // 8
val product = callMethod(calc, "multiply", 4, 7)  // 28
```

### Получение полей класса

Runtime Reflection позволяет получать информацию о полях класса:

```scala
import scala.reflect.runtime.universe._

def getFields[T: TypeTag]: List[String] = {
  typeOf[T].members.collect {
    case m: MethodSymbol if m.isCaseAccessor => m.name.toString
  }.toList
}

// Использование
case class User(name: String, age: Int, email: String)
val fields = getFields[User]  // List(name, age, email)
```

### Чтение и запись полей

Runtime Reflection позволяет читать и изменять поля объектов:

```scala
import scala.reflect.runtime.universe._

def getFieldValue[T: TypeTag](obj: T, fieldName: String): Any = {
  val mirror = runtimeMirror(getClass.getClassLoader)
  val instanceMirror = mirror.reflect(obj)
  val fieldSymbol = typeOf[T].member(TermName(fieldName)).asTerm
  val fieldMirror = instanceMirror.reflectField(fieldSymbol)
  fieldMirror.get
}

def setFieldValue[T: TypeTag](obj: T, fieldName: String, value: Any): Unit = {
  val mirror = runtimeMirror(getClass.getClassLoader)
  val instanceMirror = mirror.reflect(obj)
  val fieldSymbol = typeOf[T].member(TermName(fieldName)).asTerm
  val fieldMirror = instanceMirror.reflectField(fieldSymbol)
  fieldMirror.set(value)
}

// Использование
class Person(var name: String, var age: Int)
val person = new Person("Alice", 30)
val name = getFieldValue(person, "name")  // "Alice"
setFieldValue(person, "age", 31)
```

### Работа с аннотациями

Runtime Reflection позволяет получать информацию об аннотациях:

```scala
import scala.reflect.runtime.universe._

def getAnnotations[T: TypeTag]: List[Annotation] = {
  typeOf[T].typeSymbol.annotations
}

// Использование
@deprecated("Use NewClass instead", "2.0")
class OldClass

val annotations = getAnnotations[OldClass]
annotations.foreach { ann =>
  println(s"Annotation: ${ann.tree}")
}
```

### Сравнение типов

Runtime Reflection позволяет сравнивать типы:

```scala
import scala.reflect.runtime.universe._

def isSubtype[T: TypeTag, U: TypeTag]: Boolean = {
  typeOf[T] <:< typeOf[U]
}

// Использование
val isStringSubtypeOfAny = isSubtype[String, Any]  // true
val isIntSubtypeOfString = isSubtype[Int, String]  // false
```

### Генерация кода на основе типов

Runtime Reflection может использоваться для генерации кода:

```scala
import scala.reflect.runtime.universe._

def generateToString[T: TypeTag]: String = {
  val tpe = typeOf[T]
  val className = tpe.typeSymbol.name.toString
  val fields = tpe.members.collect {
    case m: MethodSymbol if m.isCaseAccessor =>
      s"${m.name} = $${${m.name}}"
  }
  s"$className(${fields.mkString(", ")})"
}

// Использование
case class User(name: String, age: Int)
val toStringCode = generateToString[User]
// User(name = ${name}, age = ${age})
```

### Практический пример: Сериализация

Runtime Reflection может использоваться для автоматической сериализации:

```scala
import scala.reflect.runtime.universe._

def serialize[T: TypeTag](obj: T): Map[String, Any] = {
  val mirror = runtimeMirror(getClass.getClassLoader)
  val instanceMirror = mirror.reflect(obj)
  
  typeOf[T].members.collect {
    case m: MethodSymbol if m.isCaseAccessor =>
      val fieldMirror = instanceMirror.reflectMethod(m)
      m.name.toString -> fieldMirror()
  }.toMap
}

// Использование
case class User(name: String, age: Int, email: String)
val user = User("Alice", 30, "alice@example.com")
val serialized = serialize(user)
// Map(name -> "Alice", age -> 30, email -> "alice@example.com")
```

### Практический пример: Валидация

Runtime Reflection может использоваться для автоматической валидации:

```scala
import scala.reflect.runtime.universe._

def validate[T: TypeTag](obj: T): List[String] = {
  val mirror = runtimeMirror(getClass.getClassLoader)
  val instanceMirror = mirror.reflect(obj)
  
  typeOf[T].members.collect {
    case m: MethodSymbol if m.isCaseAccessor =>
      val fieldMirror = instanceMirror.reflectMethod(m)
      val value = fieldMirror()
      if (value == null) {
        Some(s"${m.name} cannot be null")
      } else {
        None
      }
  }.flatten.toList
}

// Использование
case class User(name: String, age: Int)
val user = User(null, 30)
val errors = validate(user)  // List("name cannot be null")
```

## Лучшие практики

### Использование Type Tags для generics

```scala
// Хорошо - использование Type Tags для сохранения информации о типах
def process[T: TypeTag](value: T): String = {
  val tpe = typeOf[T]
  s"Processing ${tpe.toString}: $value"
}

// Плохо - потеря информации о типах
def processBad[T](value: T): String = {
  s"Processing: $value"  // информация о типе потеряна
}
```

### Кэширование Reflection операций

Reflection операции могут быть дорогими, поэтому их стоит кэшировать:

```scala
import scala.reflect.runtime.universe._
import scala.collection.mutable

object ReflectionCache {
  private val cache = mutable.Map[String, MethodSymbol]()
  
  def getMethod[T: TypeTag](methodName: String): MethodSymbol = {
    val key = s"${typeOf[T]}.$methodName"
    cache.getOrElseUpdate(key, {
      typeOf[T].member(TermName(methodName)).asMethod
    })
  }
}
```

### Обработка ошибок Reflection

Всегда обрабатывайте возможные ошибки при использовании Reflection:

```scala
import scala.reflect.runtime.universe._
import scala.util.{Try, Success, Failure}

def safeCallMethod[T: TypeTag](obj: T, methodName: String, args: Any*): Try[Any] = {
  Try {
    val mirror = runtimeMirror(getClass.getClassLoader)
    val instanceMirror = mirror.reflect(obj)
    val methodSymbol = typeOf[T].member(TermName(methodName)).asMethod
    val methodMirror = instanceMirror.reflectMethod(methodSymbol)
    methodMirror(args: _*)
  }
}

// Использование
case class Calculator() {
  def add(x: Int, y: Int): Int = x + y
}

val calc = Calculator()
safeCallMethod(calc, "add", 5, 3) match {
  case Success(result) => println(s"Result: $result")
  case Failure(e) => println(s"Error: ${e.getMessage}")
}
```

## Продвинутые возможности Reflection

### Работа с аннотациями

Reflection позволяет работать с аннотациями во время выполнения.

```scala
import scala.reflect.runtime.universe._

// Получение аннотаций класса
def getAnnotations[T: TypeTag]: List[Annotation] = {
  typeOf[T].typeSymbol.annotations
}

// Проверка наличия аннотации
def hasAnnotation[T: TypeTag](annotationType: Type): Boolean = {
  typeOf[T].typeSymbol.annotations.exists(_.tree.tpe =:= annotationType)
}

// Извлечение значений аннотаций
def getAnnotationValue[T: TypeTag](annotationType: Type): Option[Any] = {
  typeOf[T].typeSymbol.annotations
    .find(_.tree.tpe =:= annotationType)
    .flatMap(_.tree.children.tail.headOption)
    .map(_.toString)
}
```

### Генерация кода

Reflection может использоваться для генерации кода во время выполнения.

```scala
import scala.reflect.runtime.universe._
import scala.tools.reflect.ToolBox

val toolbox = runtimeMirror(getClass.getClassLoader).mkToolBox()

// Генерация и выполнение кода
val code = "val x = 5; val y = 3; x + y"
val tree = toolbox.parse(code)
val result = toolbox.eval(tree)  // 8
```

### Работа с generic типами

Reflection позволяет работать с generic типами во время выполнения.

```scala
import scala.reflect.runtime.universe._

// Получение type arguments
def getTypeArgs[T: TypeTag]: List[Type] = {
  typeOf[T] match {
    case TypeRef(_, _, args) => args
    case _ => Nil
  }
}

// Проверка соответствия типов
def isSubtype[T: TypeTag, U: TypeTag]: Boolean = {
  typeOf[T] <:< typeOf[U]
}
```

## Заключение

## Продвинутые техники работы с Reflection

### Runtime Type Information

Runtime Type Information позволяет получать информацию о типах во время выполнения.

```scala
import scala.reflect.runtime.universe._

// Получение информации о типе
def getTypeInfo[T: TypeTag]: TypeInfo = {
  val tpe = typeOf[T]
  TypeInfo(
    typeName = tpe.typeSymbol.name.toString,
    typeArgs = tpe.typeArgs.map(_.typeSymbol.name.toString),
    isCaseClass = tpe.typeSymbol.isCaseClass
  )
}

// Использование
case class User(name: String, age: Int)
val info = getTypeInfo[User]
// TypeInfo("User", List(), true)
```

### Dynamic Method Invocation

Dynamic Method Invocation позволяет вызывать методы динамически.

```scala
import scala.reflect.runtime.universe._

// Вызов метода динамически
def invokeMethod[T](obj: T, methodName: String, args: Any*): Any = {
  val mirror = runtimeMirror(obj.getClass.getClassLoader)
  val instanceMirror = mirror.reflect(obj)
  val methodSymbol = typeOf[T].member(TermName(methodName)).asMethod
  val methodMirror = instanceMirror.reflectMethod(methodSymbol)
  methodMirror(args: _*)
}

// Использование
case class Calculator() {
  def add(x: Int, y: Int): Int = x + y
}

val calc = Calculator()
val result = invokeMethod(calc, "add", 5, 3)  // 8
```

### Type-safe Reflection

Type-safe Reflection позволяет работать с типами безопасно.

```scala
import scala.reflect.runtime.universe._

// Type-safe создание экземпляров
def createInstance[T: TypeTag](args: Any*): T = {
  val tpe = typeOf[T]
  val mirror = runtimeMirror(getClass.getClassLoader)
  val classSymbol = tpe.typeSymbol.asClass
  val classMirror = mirror.reflectClass(classSymbol)
  val constructorSymbol = tpe.decl(termNames.CONSTRUCTOR).asMethod
  val constructorMirror = classMirror.reflectConstructor(constructorSymbol)
  constructorMirror(args: _*).asInstanceOf[T]
}

// Использование
case class Person(name: String, age: Int)
val person = createInstance[Person]("Alice", 30)
```

### Практические примеры: Динамическое создание объектов

```scala
import scala.reflect.runtime.universe._
import scala.reflect.runtime.currentMirror

case class User(name: String, age: Int)

def createInstance[A](args: Any*)(implicit tag: TypeTag[A]): A = {
  val tpe = typeOf[A]
  val constructorSymbol = tpe.decl(termNames.CONSTRUCTOR).asMethod
  val classSymbol = tpe.typeSymbol.asClass
  val classMirror = currentMirror.reflectClass(classSymbol)
  val constructorMirror = classMirror.reflectConstructor(constructorSymbol)
  
  constructorMirror(args: _*).asInstanceOf[A]
}

// Использование
val user = createInstance[User]("Alice", 30)
// User("Alice", 30)
```

### Практические примеры: Интроспекция типов

```scala
import scala.reflect.runtime.universe._

def inspectType[A](implicit tag: TypeTag[A]): Unit = {
  val tpe = typeOf[A]
  println(s"Type: $tpe")
  println(s"Members: ${tpe.members}")
  println(s"Declarations: ${tpe.decls}")
  println(s"Type parameters: ${tpe.typeParams}")
}

// Использование
inspectType[User]
```

### Практические примеры: Работа с аннотациями

```scala
import scala.reflect.runtime.universe._

// Определение аннотации
class Author(val name: String) extends scala.annotation.StaticAnnotation

@Author("John Doe")
class MyClass

def getAnnotation[A](implicit tag: TypeTag[A]): Option[String] = {
  val tpe = typeOf[A]
  val annotations = tpe.typeSymbol.annotations
  annotations.collectFirst {
    case annotation if annotation.tree.tpe.typeSymbol.name.toString == "Author" =>
      annotation.tree.children.tail.head match {
        case Literal(Constant(name: String)) => name
      }
  }
}

val author = getAnnotation[MyClass]  // Some("John Doe")
```

Reflection предоставляет мощные возможности для работы с типами во время выполнения. Понимание Type Tags, Class Tags, Runtime Reflection, работы с аннотациями, генерации кода, работы с generic типами, Runtime Type Information, Dynamic Method Invocation, Type-safe Reflection, динамического создания объектов, интроспекции типов, работы с аннотациями и их практических применений позволяет создавать гибкие и динамические решения. Reflection особенно полезен для сериализации, валидации, создания экземпляров классов динамически, генерации кода, работы с метаданными, создания динамических API, интроспекции типов и работы с аннотациями. Однако важно помнить о производительности и кэшировать результаты Reflection операций там, где это возможно. Reflection особенно полезен для создания фреймворков и библиотек, которые требуют работы с типами во время выполнения, динамического создания объектов, и создания гибких API.

### Практические примеры: Динамическое создание объектов

```scala
import scala.reflect.runtime.universe._

// Динамическое создание объектов через reflection
def createInstance[A](className: String)(implicit tag: TypeTag[A]): Option[A] = {
  val mirror = runtimeMirror(getClass.getClassLoader)
  try {
    val classSymbol = mirror.staticClass(className)
    val classMirror = mirror.reflectClass(classSymbol)
    val constructor = classSymbol.primaryConstructor.asMethod
    val constructorMirror = classMirror.reflectConstructor(constructor)
    Some(constructorMirror().asInstanceOf[A])
  } catch {
    case _: Exception => None
  }
}
```

### Практические примеры: Интроспекция типов

```scala
import scala.reflect.runtime.universe._

// Получение информации о типе
def getTypeInfo[A](implicit tag: TypeTag[A]): String = {
  val tpe = typeOf[A]
  s"Type: ${tpe}, Members: ${tpe.members.map(_.name).mkString(", ")}"
}

// Использование
case class Person(name: String, age: Int)
val info = getTypeInfo[Person]
```

### Использование с различными техниками для работы с типами

```scala
import scala.reflect.runtime.universe._

// Работа с generic типами
def getTypeParameters[A](implicit tag: TypeTag[A]): List[String] = {
  val tpe = typeOf[A]
  tpe.typeArgs.map(_.toString)
}

// Использование
case class Container[T](value: T)
val params = getTypeParameters[Container[Int]]  // List("Int")
```

### Использование с различными техниками для работы с методами

```scala
import scala.reflect.runtime.universe._

// Динамический вызов методов
def invokeMethod[A](obj: A, methodName: String, args: Any*): Option[Any] = {
  val mirror = runtimeMirror(obj.getClass.getClassLoader)
  val instanceMirror = mirror.reflect(obj)
  try {
    val method = obj.getClass.getMethod(methodName, args.map(_.getClass): _*)
    val methodSymbol = mirror.classSymbol(obj.getClass).toType.member(TermName(methodName)).asMethod
    val methodMirror = instanceMirror.reflectMethod(methodSymbol)
    Some(methodMirror(args: _*))
  } catch {
    case _: Exception => None
  }
}
```

## Дополнительные ресурсы

Для дальнейшего изучения Reflection в Scala рекомендуется:

- [Scala Reflection Documentation](https://docs.scala-lang.org/overviews/reflection/overview.html)
- [Scala Reflection API](https://www.scala-lang.org/api/current/scala-reflect/index.html)

