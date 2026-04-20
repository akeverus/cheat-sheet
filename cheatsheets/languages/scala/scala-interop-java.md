---
title: "Scala Java Interoperability"
description: "Полное руководство по взаимодействию Scala и Java: использование Java библиотек, вызов Scala из Java, миграция"
tags:
  - scala
  - java
  - interop
  - interoperability
  - migration
difficulty: "intermediate"
prerequisites: ["scala/scala-basics.md"]
next: []
updated: "2026-02-06"
related: ["scala/scala-basics.md", "../java/java-basics.md"]
---

# **Scala Java Interoperability**

Кратко: полное руководство по взаимодействию **Scala** и **Java**: использование **Java** библиотек, вызов **Scala** из **Java**, миграция.

## Полезные ссылки

### Официальная документация
- [Scala Java Interop](https://docs.scala-lang.org/scala3/book/scala-for-java-devs.html)

### См. также
- [[scala-basics|Основы Scala]]
- [[java-basics|Основы Java]]

## Содержание

- [**Scala Java Interoperability**](#scala-java-interoperability)
- [Введение в **Interop**](#введение-в-interop)
  - [Основные принципы](#основные-принципы)
- [Использование **Java** библиотек](#использование-java-библиотек)
  - [Прямое использование](#прямое-использование)
  - [Использование **Java Streams**](#использование-java-streams)
- [Вызов **Scala** из **Java**](#вызов-scala-из-java)
- [Коллекции](#коллекции)
- [Миграция](#миграция)
- [Лучшие практики](#лучшие-практики)
  - [Использование **Option** вместо **null**](#использование-option-вместо-null)
  - [Преобразование коллекций](#преобразование-коллекций)
- [Обработка **null**](#обработка-null)
- [Аннотации для **Java**](#аннотации-для-java)
- [Использование **Java Generics**](#использование-java-generics)
- [Продвинутые техники **interop**](#продвинутые-техники-interop)
  - [Использование **Java Streams** из **Scala**](#использование-java-streams-из-scala)
  - [Использование **Scala Futures** с **Java CompletableFuture**](#использование-scala-futures-с-java-completablefuture)
  - [Использование **Java Optional** с **Scala Option**](#использование-java-optional-с-scala-option)
- [Troubleshooting](#troubleshooting)
- [FAQ](#faq)
- [Заключение](#заключение)
- [Расширенные техники **Interop**](#расширенные-техники-interop)
  - [Использование **Java Reflection** из **Scala**](#использование-java-reflection-из-scala)
  - [Работа с **Java Annotations**](#работа-с-java-annotations)
  - [Миграция **Java** кода в **Scala**](#миграция-java-кода-в-scala)
  - [Практические примеры: Использование **Java** библиотек в **Scala**](#практические-примеры-использование-java-библиотек-в-scala)
  - [Практические примеры: Миграция **Java** кода в **Scala**](#практические-примеры-миграция-java-кода-в-scala)
  - [Практические примеры: Работа с **Java Optional**](#практические-примеры-работа-с-java-optional)
  - [Практические примеры: Использование **Java NIO**](#практические-примеры-использование-java-nio)
  - [Практические примеры: Использование **Java Concurrency**](#практические-примеры-использование-java-concurrency)
  - [Практические примеры: Использование **Java Time API**](#практические-примеры-использование-java-time-api)
  - [Практические примеры: Использование **Java Collections**](#практические-примеры-использование-java-collections)
  - [Практические примеры: Использование **Java Streams**](#практические-примеры-использование-java-streams)
  - [Практические примеры: Использование **Java Reflection**](#практические-примеры-использование-java-reflection)
  - [Практические примеры: Использование **Java Annotations**](#практические-примеры-использование-java-annotations)
  - [Практические примеры: Использование **Java NIO** для асинхронного I/O](#практические-примеры-использование-java-nio-для-асинхронного-io)
  - [Использование с различными **Java** библиотеками](#использование-с-различными-java-библиотеками)
  - [Использование с **Java NIO** для работы с файлами](#использование-с-java-nio-для-работы-с-файлами)
  - [Использование с **Java Streams** для обработки данных](#использование-с-java-streams-для-обработки-данных)
  - [Использование с **Java Concurrency** для параллельных операций](#использование-с-java-concurrency-для-параллельных-операций)
- [Дополнительные ресурсы](#дополнительные-ресурсы)

## Введение в **Interop**

**Scala** полностью совместим с **Java** и может использовать **Java** библиотеки напрямую, так как оба языка компилируются в байт-код **JVM**. Это означает, что **Scala** может использовать огромную экосистему **Java** библиотек без дополнительных оберток или адаптеров. Оба языка работают на одной и той же виртуальной машине и используют одинаковые примитивные типы и объекты на уровне байт-кода.

Совместимость **Scala** и **Java** позволяет постепенно мигрировать существующие **Java** проекты на **Scala**, используя **Scala** для новых компонентов, пока старые остаются на **Java**. Это снижает риски миграции и позволяет командам постепенно изучать **Scala**, не переписывая весь код сразу.

### Основные принципы

- **Scala** код может вызывать **Java** код напрямую. **Java** классы, методы и библиотеки доступны в **Scala** без дополнительных преобразований. Это позволяет использовать любую **Java** библиотеку в **Scala** проектах, что значительно расширяет доступные инструменты и решения.

- **Java** код может вызывать **Scala** код. **Scala** классы компилируются в стандартный байт-код **JVM**, который может быть использован из **Java**. Однако некоторые особенности **Scala** (**например, default параметры, implicit параметры**) требуют специальных аннотаций для удобного использования из **Java**.

- Оба языка используют одинаковые типы данных на уровне **JVM**. Примитивные типы (**Int, `Long`, `Double` и т.д.**) и ссылочные типы (**String, `Object` и т.д.**) соответствуют друг другу. Это обеспечивает прозрачную передачу данных между **Java** и **Scala** кодом.

- Коллекции требуют преобразования. **Scala** и **Java** имеют разные коллекции, поэтому при передаче коллекций между языками необходимо использовать преобразователи из пакета `**scala.jdk.CollectionConverters**`. Это позволяет конвертировать коллекции в обе стороны без потери данных.

## Использование **Java** библиотек

### Прямое использование

```scala
// Использование Java классов
import java.util.ArrayList
import java.util.HashMap

val list = new ArrayList[String]()
list.add("Hello")
list.add("World")

val map = new HashMap[String, Int]()
map.put("one", 1)
map.put("two", 2)
```

### Использование **Java Streams**

```scala
import java.util.stream.Collectors

val javaList = java.util.Arrays.asList(1, 2, 3, 4, 5)
val result = javaList.stream()
  .filter(_ > 2)
  .map(_ * 2)
  .collect(Collectors.toList())
```

## Вызов **Scala** из **Java**

**Scala** код может быть вызван из **Java**:**

```scala
// Scala класс
class Calculator {
  def add(a: Int, b: Int): Int = a + b
  def multiply(a: Int, b: Int): Int = a * b
}
```

```java
// Java код
Calculator calc = new Calculator();
int sum = calc.add(5, 3);
int product = calc.multiply(4, 5);
```

## Коллекции

**Коллекции **Scala** и **Java** требуют преобразования:**

```scala
import scala.jdk.CollectionConverters._

// Scala List в Java List
val scalaList = List(1, 2, 3)
val javaList = scalaList.asJava

// Java List в Scala List
val javaList2 = new java.util.ArrayList[Int]()
javaList2.add(1)
javaList2.add(2)
val scalaList2 = javaList2.asScala.toList
```

## Миграция

**Миграция **Java** кода в **Scala** может быть постепенной:**

```java
// Java код
public class UserService {
    private final UserRepository repository;

    public UserService(UserRepository repository) {
        this.repository = repository;
    }

    public User findUser(Long id) {
        return repository.findById(id);
    }
}
```

```scala
// Scala код
class UserService(repository: UserRepository) {
  def findUser(id: Long): Option[User] = {
    Option(repository.findById(id))
  }
}
```

## Лучшие практики

### Использование **Option** вместо **null**

```scala
// Хорошо - использование Option
def findUser(id: Long): Option[User] = {
  val user = javaRepository.findById(id)
  Option(user)  // преобразует null в None
}

// Плохо - возврат null
def findUserBad(id: Long): User = {
  javaRepository.findById(id)  // может вернуть null
}
```

### Преобразование коллекций

```scala
// Хорошо - явное преобразование
import scala.jdk.CollectionConverters._

val javaList = new java.util.ArrayList[String]()
val scalaList = javaList.asScala.toList

// Плохо - смешивание типов
val mixed = javaList  // может вызвать проблемы
```

## Обработка **null**

**Scala** использует **Option** для безопасной работы с возможными **null** значениями:**

```scala
// Преобразование Java методов, возвращающих null
def findUserJava(id: Long): User = {
  // Java метод может вернуть null
  javaRepository.findById(id)
}

// Безопасная обертка
def findUserSafe(id: Long): Option[User] = {
  Option(findUserJava(id))  // null преобразуется в None
}

// Использование
findUserSafe(1L) match {
  case Some(user) => println(s"Found: ${user.name}")
  case None => println("User not found")
}
```

Использование **Option** вместо **null** делает код более безопасным и выразительным.

## Аннотации для **Java**

**Scala** предоставляет аннотации для улучшения совместимости с **Java**:**

```scala
// @BeanProperty для JavaBean совместимости
import scala.beans.BeanProperty

class User(@BeanProperty var name: String, @BeanProperty var age: Int)

// Теперь доступны методы getName(), setName(), getAge(), setAge()
```

Аннотации помогают создавать **Scala** код, который легко используется из **Java**.

## Использование **Java Generics**

**Scala generics** совместимы с **Java generics**:**

```scala
// Scala класс с generics
class Box[T](val value: T)

// Использование из Java
// Box<String> box = new Box<>("Hello");
```

**Generics** работают одинаково в обоих языках на уровне **JVM**.

## Продвинутые техники **interop**

### Использование **Java Streams** из **Scala**

**Scala** может использовать **Java Streams API**.

```scala
import java.util.stream.Collectors
import scala.jdk.CollectionConverters._

// Использование Java Streams
val javaList = List(1, 2, 3, 4, 5).asJava
val result = javaList.stream()
  .filter(_ > 2)
  .map(_ * 2)
  .collect(Collectors.toList())
  .asScala
```

### Использование **Scala Futures** с **Java CompletableFuture**

**Scala Futures** могут работать с **Java CompletableFuture**.

```scala
import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global
import java.util.concurrent.CompletableFuture
import scala.compat.java8.FutureConverters._

// Преобразование между Future и CompletableFuture
val scalaFuture: Future[Int] = Future(42)
val javaFuture: CompletableFuture[Int] = scalaFuture.toJava.toCompletableFuture

val javaCompletableFuture: CompletableFuture[Int] = CompletableFuture.supplyAsync(() => 42)
val scalaFutureFromJava: Future[Int] = javaCompletableFuture.toScala
```

### Использование **Java Optional** с **Scala Option**

**Scala Option** может работать с **Java Optional**.

```scala
import scala.jdk.OptionConverters._

// Преобразование между Option и Optional
val scalaOption: Option[String] = Some("Hello")
val javaOptional: java.util.Optional[String] = scalaOption.toJava

val javaOptionalValue: java.util.Optional[String] = java.util.Optional.of("Hello")
val scalaOptionFromJava: Option[String] = javaOptionalValue.toScala
```


## Решение проблем

Типичные проблемы и решения см. в официальной документации (блок «Полезные ссылки» в начале документа).

## Частые вопросы

Ответы на частые вопросы по теме см. в разделах «Введение» и «Лучшие практики» в документе.

## Заключение

## Расширенные техники **Interop**

### Использование **Java Reflection** из **Scala**

**Java Reflection** может использоваться из **Scala** для динамической работы с классами.

```scala
import java.lang.reflect.Method

// Получение метода через Reflection
val method: Method = classOf[String].getMethod("substring", classOf[Int])
val result = method.invoke("Hello", 2)  // "llo"
```

### Работа с **Java Annotations**

**Java** аннотации могут использоваться в **Scala** коде.

```scala
import javax.annotation.PostConstruct
import javax.annotation.PreDestroy

class Service {
  @PostConstruct
  def init(): Unit = {
    // инициализация
  }

  @PreDestroy
  def cleanup(): Unit = {
    // очистка
  }
}
```

### Миграция **Java** кода в **Scala**

Постепенная миграция **Java** кода в **Scala** позволяет сохранить работоспособность приложения.

```scala
// Java класс
// public class User {
//   private String name;
//   public User(String name) { this.name = name; }
//   public String getName() { return name; }
// }

// Scala эквивалент
case class User(name: String)

// Использование Java класса из Scala
val javaUser = new com.example.User("Alice")
val name = javaUser.getName()
```

**Scala** и **Java** полностью совместимы на уровне **JVM**, что позволяет использовать существующие **Java** библиотеки и постепенно мигрировать код. Понимание особенностей **interop**, преобразования коллекций, обработки **null**, использования **Java Streams**, работы с **Java CompletableFuture**, работы с **Java Optional**, использования **Java Reflection** из **Scala**, работы с **Java Annotations**, и миграции **Java** кода в **Scala** критично для успешной интеграции. Использование **Option**, аннотаций, правильное преобразование коллекций, использование библиотек для преобразования между типами, использование **Java Reflection** для динамической работы с классами, работа с **Java Annotations**, и постепенная миграция **Java** кода в **Scala** обеспечивает плавную интеграцию между двумя языками. **Interop** особенно важен для создания приложений, которые должны использовать существующие **Java** библиотеки, постепенно мигрировать код, интегрироваться с **Java** экосистемой, и использовать **Java Reflection** и **Annotations**.

### Практические примеры: Использование **Java** библиотек в **Scala**

```scala
import java.util.concurrent.{CompletableFuture, Executors}
import scala.concurrent.{Future, Promise}
import scala.util.Try

// Конвертация Java CompletableFuture в Scala Future
implicit class CompletableFutureOps[T](val cf: CompletableFuture[T]) extends AnyVal {
  def toScalaFuture: Future[T] = {
    val promise = Promise[T]()
    cf.whenComplete((result, throwable) => {
      if (throwable != null) promise.failure(throwable)
      else promise.success(result)
    })
    promise.future
  }
}

// Использование
val javaFuture = CompletableFuture.supplyAsync(() => "result")
val scalaFuture = javaFuture.toScalaFuture
```

### Практические примеры: Миграция **Java** кода в **Scala**

```scala
// Java код
// public class UserService {
//     private final UserRepository repository;
//
//     public UserService(UserRepository repository) {
//         this.repository = repository;
//     }
//
//     public User createUser(String name) {
//         User user = new User(name);
//         repository.save(user);
//         return user;
//     }
// }

// Scala версия
class UserService(repository: UserRepository) {
  def createUser(name: String): User = {
    val user = User(name)
    repository.save(user)
    user
  }
}
```

### Практические примеры: Работа с **Java Optional**

```scala
import java.util.Optional

object JavaOptionalUtils {
  // Конвертация между Scala Option и Java Optional
  implicit class OptionOps[T](val option: Option[T]) extends AnyVal {
    def toJavaOptional: Optional[T] = option match {
      case Some(value) => Optional.of(value)
      case None => Optional.empty()
    }
  }

  implicit class OptionalOps[T](val optional: Optional[T]) extends AnyVal {
    def toScalaOption: Option[T] =
      if (optional.isPresent) Some(optional.get) else None
  }
}

// Использование
val scalaOption: Option[String] = Some("value")
val javaOptional: Optional[String] = scalaOption.toJavaOptional

val javaOpt: Optional[String] = Optional.of("value")
val scalaOpt: Option[String] = javaOpt.toScalaOption
```

### Практические примеры: Использование **Java NIO**

```scala
import java.nio.file.{Files, Paths, StandardOpenOption}
import java.nio.charset.StandardCharsets

// Чтение файла с использованием Java NIO
def readFile(path: String): String = {
  val bytes = Files.readAllBytes(Paths.get(path))
  new String(bytes, StandardCharsets.UTF_8)
}

// Запись файла с использованием Java NIO
def writeFile(path: String, content: String): Unit = {
  Files.write(
    Paths.get(path),
    content.getBytes(StandardCharsets.UTF_8),
    StandardOpenOption.CREATE,
    StandardOpenOption.TRUNCATE_EXISTING
  )
}

// Копирование файла
def copyFile(source: String, dest: String): Unit = {
  Files.copy(Paths.get(source), Paths.get(dest), java.nio.file.StandardCopyOption.REPLACE_EXISTING)
}
```

### Практические примеры: Использование **Java Concurrency**

```scala
import java.util.concurrent.{ExecutorService, Executors, Future, Callable}
import scala.concurrent.{ExecutionContext, Future => ScalaFuture}
import scala.concurrent.ExecutionContext.Implicits.global

// Использование Java ExecutorService
val executor: ExecutorService = Executors.newFixedThreadPool(10)

def executeJavaTask[A](task: Callable[A]): Future[A] = {
  executor.submit(task)
}

// Конвертация Java Future в Scala Future
def javaFutureToScala[A](javaFuture: java.util.concurrent.Future[A]): ScalaFuture[A] = {
  ScalaFuture {
    javaFuture.get()
  }
}

// Использование
val javaTask = new Callable[Int] {
  def call(): Int = {
    Thread.sleep(1000)
    42
  }
}

val javaFuture = executeJavaTask(javaTask)
val scalaFuture = javaFutureToScala(javaFuture)
```

### Практические примеры: Использование **Java Time API**

```scala
import java.time.{LocalDate, LocalDateTime, LocalTime, ZonedDateTime}
import java.time.format.DateTimeFormatter

// Использование Java Time API
val now = LocalDateTime.now()
val date = LocalDate.of(2024, 1, 1)
val time = LocalTime.of(12, 30, 0)

// Форматирование даты
val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
val formatted = now.format(formatter)

// Парсинг даты
val parsed = LocalDateTime.parse("2024-01-01 12:00:00", formatter)

// Работа с часовыми поясами
val zonedDateTime = ZonedDateTime.now(java.time.ZoneId.of("UTC"))
```

### Практические примеры: Использование **Java Collections**

```scala
import java.util.{List => JList, Map => JMap, Set => JSet}
import scala.collection.JavaConverters._

// Конвертация между Java и Scala коллекциями
val scalaList = List(1, 2, 3, 4, 5)
val javaList: JList[Int] = scalaList.asJava
val backToScala: List[Int] = javaList.asScala.toList

val scalaMap = Map("a" -> 1, "b" -> 2, "c" -> 3)
val javaMap: JMap[String, Int] = scalaMap.asJava
val backToScalaMap: Map[String, Int] = javaMap.asScala.toMap

val scalaSet = Set(1, 2, 3, 4, 5)
val javaSet: JSet[Int] = scalaSet.asJava
val backToScalaSet: Set[Int] = javaSet.asScala.toSet
```

### Практические примеры: Использование **Java Streams**

```scala
import java.util.stream.{Stream => JStream, Collectors}
import scala.collection.JavaConverters._

// Использование Java Streams API
val javaStream: JStream[Int] = JStream.of(1, 2, 3, 4, 5)

// Преобразование Java Stream в Scala List
val scalaList = javaStream.collect(Collectors.toList()).asScala.toList

// Использование Java Stream для обработки данных
val result = JStream.of(1, 2, 3, 4, 5)
  .filter(_ % 2 == 0)
  .map(_ * 2)
  .collect(Collectors.toList())
  .asScala
  .toList
// List(4, 8)
```

### Практические примеры: Использование **Java Reflection**

```scala
import java.lang.reflect.{Method, Field, Constructor}

// Получение класса
val clazz = classOf[String]

// Получение методов
val methods: Array[Method] = clazz.getDeclaredMethods()

// Получение полей
val fields: Array[Field] = clazz.getDeclaredFields()

// Получение конструкторов
val constructors: Array[Constructor[_]] = clazz.getDeclaredConstructors()

// Вызов метода через reflection
val method = clazz.getMethod("toUpperCase")
val result = method.invoke("hello")
// "HELLO"
```

### Практические примеры: Использование **Java Annotations**

```scala
import java.lang.annotation.{Annotation, Retention, RetentionPolicy}

// Использование Java аннотаций
@Retention(RetentionPolicy.RUNTIME)
trait MyAnnotation extends Annotation

@MyAnnotation
class MyClass

// Получение аннотаций
val annotations: Array[Annotation] = classOf[MyClass].getAnnotations()
val hasAnnotation = classOf[MyClass].isAnnotationPresent(classOf[MyAnnotation])
```

### Практические примеры: Использование **Java NIO** для асинхронного I/O

```scala
import java.nio.channels.{AsynchronousFileChannel, CompletionHandler}
import java.nio.file.{Paths, StandardOpenOption}
import java.nio.ByteBuffer

// Асинхронное чтение файла
def readFileAsync(path: String): scala.concurrent.Future[String] = {
  val promise = scala.concurrent.Promise[String]
  val channel = AsynchronousFileChannel.open(
    Paths.get(path),
    StandardOpenOption.READ
  )

  val buffer = ByteBuffer.allocate(1024)
  channel.read(buffer, 0, buffer, new CompletionHandler[Integer, ByteBuffer] {
    def completed(bytesRead: Integer, attachment: ByteBuffer): Unit = {
      attachment.flip()
      val bytes = new Array[Byte](attachment.remaining())
      attachment.get(bytes)
      promise.success(new String(bytes))
      channel.close()
    }

    def failed(ex: Throwable, attachment: ByteBuffer): Unit = {
      promise.failure(ex)
      channel.close()
    }
  })

  promise.future
}
```

## Заключение

**Interop** между **Scala** и **Java** позволяет использовать богатую экосистему **Java** библиотек в **Scala** приложениях. Понимание конвертации типов, работы с **null**, использования **Java** коллекций, **Java Streams**, **Java Time API**, **Java NIO**, **Java Concurrency**, **Java Reflection**, **Java Annotations** и их практических применений позволяет эффективно интегрировать **Java** код в **Scala** приложения.

Использование **Java** библиотек в **Scala** приложениях, обработка **null**, конвертация между **Java** и **Scala** типами, использование **Java** коллекций, **Java Streams**, **Java Time API**, **Java NIO**, **Java Concurrency**, **Java Reflection** и **Java Annotations** критично для создания полнофункциональных, совместимых приложений.

### Использование с различными **Java** библиотеками

```scala
import java.util.{Properties, Properties => JProperties}
import scala.jdk.CollectionConverters._

// Работа с Java Properties
val props = new Properties()
props.setProperty("key1", "value1")
props.setProperty("key2", "value2")

// Конвертация в Scala Map
val scalaMap = props.asScala.toMap
// Map("key1" -> "value1", "key2" -> "value2")
```

### Использование с **Java NIO** для работы с файлами

```scala
import java.nio.file.{Paths, Files, StandardOpenOption}
import scala.jdk.CollectionConverters._

// Чтение файла
val path = Paths.get("file.txt")
val lines = Files.readAllLines(path).asScala.toList

// Запись в файл
val content = List("line1", "line2", "line3")
Files.write(path, content.asJava, StandardOpenOption.CREATE)
```

### Использование с **Java Streams** для обработки данных

```scala
import java.util.stream.{Stream, Collectors}
import scala.jdk.CollectionConverters._

// Создание Java Stream
val javaStream = Stream.of(1, 2, 3, 4, 5)

// Обработка и конвертация в Scala List
val scalaList = javaStream
  .filter(_ % 2 == 0)
  .map(_ * 2)
  .collect(Collectors.toList())
  .asScala
  .toList
// List(4, 8)
```

### Использование с **Java Concurrency** для параллельных операций

```scala
import java.util.concurrent.{ExecutorService, Executors, Future => JFuture}
import scala.concurrent.{Future, ExecutionContext}
import scala.jdk.FutureConverters._

// Создание ExecutorService
val executor: ExecutorService = Executors.newFixedThreadPool(10)
implicit val ec: ExecutionContext = ExecutionContext.fromExecutor(executor)

// Конвертация Java Future в Scala Future
val javaFuture: JFuture[String] = executor.submit(() => "result")
val scalaFuture: Future[String] = javaFuture.asScala
```

## Дополнительные ресурсы

**Для дальнейшего изучения **interop** между **Scala** и **Java** рекомендуется:**

- [Scala Java Interop Documentation](https://docs.scala-lang.org/overviews/scala-book/scala-for-java-devs.html)
- [Scala Collection JavaConverters](https://www.scala-lang.org/api/current/scala/jdk/CollectionConverters$.html)
