---
title: "Scala Additional Topics"
description: "Дополнительные темы Scala: расширения, полезные паттерны, best practices, продвинутые техники"
tags:
  - scala
  - patterns
  - best-practices
  - advanced
  - extensions
difficulty: "advanced"
prerequisites: ["scala/scala-basics.md", "scala/scala-fp-advanced.md"]
next: []
updated: "2026-02-06"
related: ["scala/scala-basics.md", "scala/scala-fp-advanced.md", "scala/scala-type-system.md"]
---

# Scala Additional Topics

Кратко: дополнительные темы **Scala**: расширения, полезные паттерны, **best practices**, продвинутые техники программирования.

## Полезные ссылки

### Официальная документация
- [Scala Documentation](https://docs.scala-lang.org/)
- [Scala Style Guide](https://docs.scala-lang.org/style/)

### См. также
- [[scala-basics|Основы Scala]]
- [[scala-fp-advanced|Продвинутое функциональное программирование]]
- [[scala-type-system|Система типов]]

## Содержание

- [**Scala Additional Topics**](#scala-additional-topics)
- [Паттерны проектирования](#паттерны-проектирования)
  - [**Strategy Pattern**](#strategy-pattern)
  - [**Observer Pattern**](#observer-pattern)
  - [**Factory Pattern**](#factory-pattern)
- [Лучшие практики](#лучшие-практики)
  - [Именование](#именование)
  - [Иммутабельность](#иммутабельность)
  - [Обработка ошибок](#обработка-ошибок)
- [Продвинутые техники](#продвинутые-техники)
  - [**Tagless Final**](#tagless-final)
  - [**Free Monad**](#free-monad)
- [Оптимизация кода](#оптимизация-кода)
  - [Ленивые вычисления](#ленивые-вычисления)
  - [Мемоизация](#мемоизация)
- [Архитектурные паттерны](#архитектурные-паттерны)
  - [**Layered Architecture**](#layered-architecture)
  - [**Hexagonal Architecture**](#hexagonal-architecture)
- [Дополнительные паттерны](#дополнительные-паттерны)
  - [**Builder Pattern**](#builder-pattern)
  - [**Adapter Pattern**](#adapter-pattern)
  - [**Decorator Pattern**](#decorator-pattern)
- [Продвинутые техники (расширенные)](#продвинутые-техники-расширенные)
  - [**Type-level Programming**](#type-level-programming)
  - [**Dependent Types**](#dependent-types)
  - [**Phantom Types**](#phantom-types)
- [Оптимизация кода (расширенная)](#оптимизация-кода-расширенная)
  - [Специализация](#специализация)
  - [**Inline** методы](#inline-методы)
  - [Оптимизация коллекций](#оптимизация-коллекций)
- [Архитектурные паттерны (расширенные)](#архитектурные-паттерны-расширенные)
  - [**Event-Driven Architecture**](#event-driven-architecture)
  - [**CQRS** (Command Query Responsibility Segregation)](#cqrs-command-query-responsibility-segregation)
- [Troubleshooting](#troubleshooting)
- [FAQ](#faq)
- [Заключение](#заключение)
- [Дополнительные ресурсы](#дополнительные-ресурсы)
- [Дополнительные паттерны и техники](#дополнительные-паттерны-и-техники)
  - [**Template Method Pattern**](#template-method-pattern)
  - [**Visitor Pattern**](#visitor-pattern)
  - [**Chain of Responsibility Pattern**](#chain-of-responsibility-pattern)
  - [**Command Pattern**](#command-pattern)
  - [**Memento Pattern**](#memento-pattern)
- [Заключение (финальное расширенное)](#заключение-финальное-расширенное)
  - [Практические примеры: Архитектурные паттерны](#практические-примеры-архитектурные-паттерны)
  - [Практические примеры: **Event-Driven Architecture**](#практические-примеры-event-driven-architecture)

## Паттерны проектирования

### Strategy Pattern

**Strategy Pattern** позволяет выбирать алгоритм во время выполнения.

```scala
// Определение стратегии
trait SortingStrategy {
  def sort[T](list: List[T])(implicit ord: Ordering[T]): List[T]
}

// Реализации стратегий
object QuickSort extends SortingStrategy {
  def sort[T](list: List[T])(implicit ord: Ordering[T]): List[T] = {
    if (list.length <= 1) list
    else {
      val pivot = list(list.length / 2)
      val (less, equal, greater) = list.partition { x =>
        val cmp = ord.compare(x, pivot)
        if (cmp < 0) true
        else if (cmp > 0) false
        else x == pivot
      }
      sort(less) ::: equal ::: sort(greater)
    }
  }
}

object MergeSort extends SortingStrategy {
  def sort[T](list: List[T])(implicit ord: Ordering[T]): List[T] = {
    def merge(left: List[T], right: List[T]): List[T] = {
      (left, right) match {
        case (Nil, right) => right
        case (left, Nil) => left
        case (l :: ls, r :: rs) =>
          if (ord.lt(l, r)) l :: merge(ls, right)
          else r :: merge(left, rs)
      }
    }

    if (list.length <= 1) list
    else {
      val (left, right) = list.splitAt(list.length / 2)
      merge(sort(left), sort(right))
    }
  }
}

// Использование
class Sorter(strategy: SortingStrategy) {
  def sort[T](list: List[T])(implicit ord: Ordering[T]): List[T] =
    strategy.sort(list)
}

val quickSorter = new Sorter(QuickSort)
val mergeSorter = new Sorter(MergeSort)
```

**Strategy Pattern** особенно полезен, когда нужно выбирать алгоритм во время выполнения или когда есть несколько способов выполнения одной задачи. В функциональном программировании стратегии часто представлены как функции высшего порядка, что делает код более гибким и композируемым.

### Observer Pattern

**Observer Pattern** позволяет объектам подписываться на события и получать уведомления.

```scala
// Определение наблюдателя
trait Observer[T] {
  def onNext(value: T): Unit
  def onError(error: Throwable): Unit
  def onComplete(): Unit
}

// Определение наблюдаемого
trait Observable[T] {
  def subscribe(observer: Observer[T]): Unit
}

// Реализация
class EventEmitter[T] extends Observable[T] {
  private var observers: List[Observer[T]] = Nil

  def subscribe(observer: Observer[T]): Unit = {
    observers = observer :: observers
  }

  def emit(value: T): Unit = {
    observers.foreach(_.onNext(value))
  }

  def error(error: Throwable): Unit = {
    observers.foreach(_.onError(error))
  }

  def complete(): Unit = {
    observers.foreach(_.onComplete())
  }
}
```

**Observer Pattern** в **Scala** часто реализуется через функциональные подходы, такие как функции обратного вызова или реактивные потоки, что делает код более декларативным и композируемым.

### Factory Pattern

**Factory Pattern** позволяет создавать объекты без указания их конкретных классов.

```scala
// Определение продукта
trait Product {
  def operation(): String
}

// Конкретные продукты
class ConcreteProductA extends Product {
  def operation(): String = "Product A"
}

class ConcreteProductB extends Product {
  def operation(): String = "Product B"
}

// Фабрика
object ProductFactory {
  def create(productType: String): Product = productType match {
    case "A" => new ConcreteProductA
    case "B" => new ConcreteProductB
    case _ => throw new IllegalArgumentException(s"Unknown product type: $productType")
  }
}

// Использование
val product = ProductFactory.create("A")
```

**Factory Pattern** в **Scala** часто реализуется через **companion objects** или функции, что делает код более функциональным и типобезопасным.

## Лучшие практики

### Именование

Правильное именование критично для читаемости кода.

```scala
// Хорошо - описательные имена
def calculateTotalPrice(items: List[Item]): BigDecimal = {
  items.map(_.price).sum
}

// Плохо - неясные имена
def calc(ls: List[I]): B = {
  ls.map(_.p).sum
}

// Хорошо - имена отражают намерение
val activeUsers = users.filter(_.isActive)
val userCount = users.length

// Плохо - имена не отражают намерение
val filtered = users.filter(_.isActive)
val count = users.length
```

### Иммутабельность

Предпочтение иммутабельных структур данных улучшает безопасность и предсказуемость кода.

```scala
// Хорошо - иммутабельные структуры
val users = List(User(1, "Alice"), User(2, "Bob"))
val updatedUsers = users.map(_.copy(name = _.name.toUpperCase))

// Плохо - мутабельные структуры
var users = mutable.ListBuffer(User(1, "Alice"), User(2, "Bob"))
users.foreach(_.name = _.name.toUpperCase)
```

### Обработка ошибок

Использование функциональных подходов к обработке ошибок улучшает надежность кода.

```scala
// Хорошо - использование Either
def divide(a: Int, b: Int): Either[String, Int] = {
  if (b == 0) Left("Division by zero")
  else Right(a / b)
}

// Плохо - использование исключений для контроля потока
def divideBad(a: Int, b: Int): Int = {
  if (b == 0) throw new IllegalArgumentException("Division by zero")
  else a / b
}
```

## Продвинутые техники

### Tagless Final

**Tagless Final** позволяет создавать интерпретируемые **DSL** без фиксации на конкретном эффекте.

```scala
// Определение алгебры
trait UserAlgebra[F[_]] {
  def findById(id: Long): F[Option[User]]
  def create(user: User): F[User]
  def update(id: Long, user: User): F[Option[User]]
}

// Интерпретация для IO
class IOUserAlgebra extends UserAlgebra[IO] {
  def findById(id: Long): IO[Option[User]] = IO(users.find(_.id == id))
  def create(user: User): IO[User] = IO {
    val newUser = user.copy(id = nextId())
    users = newUser :: users
    newUser
  }
  def update(id: Long, user: User): IO[Option[User]] = IO {
    users.find(_.id == id).map { existing =>
      val updated = user.copy(id = id)
      users = updated :: users.filterNot(_.id == id)
      updated
    }
  }
}

// Интерпретация для Future
class FutureUserAlgebra extends UserAlgebra[Future] {
  def findById(id: Long): Future[Option[User]] = Future(users.find(_.id == id))
  def create(user: User): Future[User] = Future {
    val newUser = user.copy(id = nextId())
    users = newUser :: users
    newUser
  }
  def update(id: Long, user: User): Future[Option[User]] = Future {
    users.find(_.id == id).map { existing =>
      val updated = user.copy(id = id)
      users = updated :: users.filterNot(_.id == id)
      updated
    }
  }
}
```

**Tagless Final** особенно полезен для создания тестируемого и переиспользуемого кода, который не зависит от конкретной реализации эффектов.

### Free Monad

**Free Monad** позволяет создавать **DSL** и интерпретировать их различными способами.

```scala
import cats.free.Free
import cats.free.Free.liftF

// Определение алгебры
sealed trait UserAlgebra[A]
case class FindUser(id: Long) extends UserAlgebra[Option[User]]
case class CreateUser(user: User) extends UserAlgebra[User]
case class UpdateUser(id: Long, user: User) extends UserAlgebra[Option[User]]

type UserProgram[A] = Free[UserAlgebra, A]

// Создание программ
def findUser(id: Long): UserProgram[Option[User]] =
  liftF(FindUser(id))

def createUser(user: User): UserProgram[User] =
  liftF(CreateUser(user))

// Композиция программ
val program: UserProgram[User] = for {
  maybeUser <- findUser(1L)
  user <- maybeUser match {
    case Some(u) => Free.pure(u)
    case None => createUser(User(0, "Alice", "alice@example.com"))
  }
} yield user
```

**Free Monad** позволяет создавать чистые программы, которые можно интерпретировать различными способами, что делает код более тестируемым и гибким.

## Оптимизация кода

### Ленивые вычисления

Использование ленивых вычислений может улучшить производительность.

```scala
// Ленивое вычисление
lazy val expensiveValue: Int = {
  println("Computing...")
  Thread.sleep(1000)
  42
}

// View для ленивых коллекций
val largeList = (1 to 1000000).toList
val lazyResult = largeList.view
  .filter(_ % 2 == 0)
  .map(_ * 2)
  .take(100)
  .toList
```

### Мемоизация

Мемоизация позволяет кэшировать результаты вычислений.

```scala
import scala.collection.mutable

def memoize[K, V](f: K => V): K => V = {
  val cache = mutable.Map.empty[K, V]
  key => cache.getOrElseUpdate(key, f(key))
}

val memoizedFactorial = memoize { (n: Int) =>
  if (n <= 1) 1
  else n * memoizedFactorial(n - 1)
}
```

## Архитектурные паттерны

### Layered Architecture

Многослойная архитектура разделяет код на логические слои.

```scala
// Domain layer
case class User(id: Long, name: String, email: String)

// Repository layer
trait UserRepository {
  def findById(id: Long): Option[User]
  def create(user: User): User
}

// Service layer
class UserService(repository: UserRepository) {
  def getUser(id: Long): Option[User] = repository.findById(id)
  def createUser(name: String, email: String): User = {
    val user = User(0, name, email)
    repository.create(user)
  }
}

// Presentation layer
class UserController(service: UserService) {
  def handleRequest(id: Long): String = {
    service.getUser(id).map(_.name).getOrElse("User not found")
  }
}
```

### Hexagonal Architecture

**Hexagonal Architecture** разделяет бизнес-логику от внешних зависимостей.

```scala
// Domain
case class User(id: Long, name: String, email: String)

// Ports (интерфейсы)
trait UserRepository {
  def findById(id: Long): Option[User]
}

trait EmailService {
  def sendEmail(to: String, subject: String, body: String): Unit
}

// Adapters (реализации)
class DatabaseUserRepository extends UserRepository {
  def findById(id: Long): Option[User] = {
    // Реализация работы с базой данных
    None
  }
}

class SMTPEmailService extends EmailService {
  def sendEmail(to: String, subject: String, body: String): Unit = {
    // Реализация отправки email
  }
}

// Application (бизнес-логика)
class UserApplication(
  userRepository: UserRepository,
  emailService: EmailService
) {
  def registerUser(name: String, email: String): User = {
    val user = User(0, name, email)
    val created = userRepository.findById(0) // Упрощенная логика
    emailService.sendEmail(email, "Welcome", "Welcome to our service")
    created.get
  }
}
```

## Дополнительные паттерны

### Builder Pattern

**Builder Pattern** позволяет создавать сложные объекты пошагово.

```scala
case class User private (
  id: Long,
  name: String,
  email: String,
  age: Option[Int],
  address: Option[String]
)

class UserBuilder {
  private var id: Long = 0
  private var name: String = ""
  private var email: String = ""
  private var age: Option[Int] = None
  private var address: Option[String] = None

  def withId(id: Long): UserBuilder = {
    this.id = id
    this
  }

  def withName(name: String): UserBuilder = {
    this.name = name
    this
  }

  def withEmail(email: String): UserBuilder = {
    this.email = email
    this
  }

  def withAge(age: Int): UserBuilder = {
    this.age = Some(age)
    this
  }

  def withAddress(address: String): UserBuilder = {
    this.address = Some(address)
    this
  }

  def build(): User = {
    if (name.isEmpty || email.isEmpty) {
      throw new IllegalArgumentException("Name and email are required")
    }
    User(id, name, email, age, address)
  }
}

object UserBuilder {
  def apply(): UserBuilder = new UserBuilder()
}

// Использование
val user = UserBuilder()
  .withId(1L)
  .withName("Alice")
  .withEmail("alice@example.com")
  .withAge(30)
  .withAddress("123 Main St")
  .build()
```

**Builder Pattern** в **Scala** часто реализуется через **fluent interface** или **case class** с методами **copy**, что делает код более функциональным и типобезопасным.

### Adapter Pattern

**Adapter Pattern** позволяет объектам работать вместе, несмотря на несовместимые интерфейсы.

```scala
// Старый интерфейс
trait OldService {
  def getData(): String
}

// Новый интерфейс
trait NewService {
  def fetchData(): Future[String]
}

// Адаптер
class ServiceAdapter(oldService: OldService) extends NewService {
  def fetchData(): Future[String] = {
    Future.successful(oldService.getData())
  }
}
```

### Decorator Pattern

**Decorator Pattern** позволяет добавлять новое поведение к объектам динамически.

```scala
// Базовый компонент
trait Coffee {
  def cost(): Double
  def description(): String
}

// Конкретная реализация
class SimpleCoffee extends Coffee {
  def cost(): Double = 1.0
  def description(): String = "Simple Coffee"
}

// Декоратор
abstract class CoffeeDecorator(coffee: Coffee) extends Coffee {
  def cost(): Double = coffee.cost()
  def description(): String = coffee.description()
}

// Конкретные декораторы
class MilkDecorator(coffee: Coffee) extends CoffeeDecorator(coffee) {
  override def cost(): Double = super.cost() + 0.5
  override def description(): String = super.description() + ", Milk"
}

class SugarDecorator(coffee: Coffee) extends CoffeeDecorator(coffee) {
  override def cost(): Double = super.cost() + 0.2
  override def description(): String = super.description() + ", Sugar"
}

// Использование
val coffee = new SugarDecorator(new MilkDecorator(new SimpleCoffee()))
```

## Продвинутые техники (расширенные)

### Type-level Programming

**Type-level** программирование позволяет выполнять вычисления на уровне типов.

```scala
// Представление чисел на уровне типов
sealed trait Nat
case object Zero extends Nat
case class Succ[N <: Nat](n: N) extends Nat

// Сложение на уровне типов
type Add[A <: Nat, B <: Nat] <: Nat = A match {
  case Zero => B
  case Succ[n] => Succ[Add[n, B]]
}

// Использование
type Two = Succ[Succ[Zero]]
type Three = Succ[Succ[Succ[Zero]]]
type Five = Add[Two, Three]
```

### Dependent Types

**Dependent Types** позволяют типам зависеть от значений.

```scala
// Зависимый тип
trait Vector[N <: Nat] {
  def get(index: Int): Double
  def length: Int
}

// Функция с зависимым типом
def dotProduct[N <: Nat](v1: Vector[N], v2: Vector[N]): Double = {
  (0 until v1.length).map(i => v1.get(i) * v2.get(i)).sum
}
```

### Phantom Types

**Phantom Types** используются для обеспечения безопасности типов без накладных расходов.

```scala
// Phantom type для единиц измерения
sealed trait Unit
sealed trait Meter extends Unit
sealed trait Second extends Unit

case class Quantity[A <: Unit](value: Double)

// Типобезопасные операции
def add[A <: Unit](q1: Quantity[A], q2: Quantity[A]): Quantity[A] = {
  Quantity[A](q1.value + q2.value)
}

// Компилятор предотвращает смешивание единиц
val distance1 = Quantity[Meter](10.0)
val distance2 = Quantity[Meter](5.0)
val time = Quantity[Second](2.0)

val totalDistance = add(distance1, distance2)  // OK
// val invalid = add(distance1, time)  // Ошибка компиляции
```

## Оптимизация кода (расширенная)

### Специализация

Специализация позволяет создавать оптимизированные версии функций для примитивных типов.

```scala
// Специализированная функция
def process[@specialized(Int, Long, Double) T](value: T): T = {
  // Обработка
  value
}
```

### Inline методы

**Inline** методы позволяют встраивать код для улучшения производительности.

```scala
@inline
def fastOperation(x: Int, y: Int): Int = x + y

// Компилятор встроит вызов метода
val result = fastOperation(5, 3)
```

### Оптимизация коллекций

Правильный выбор коллекций критичен для производительности.

```scala
// Vector для произвольного доступа
val vector = Vector(1, 2, 3, 4, 5)
val element = vector(2)  // O(log32(n))

// List для последовательного доступа
val list = List(1, 2, 3, 4, 5)
val head = list.head  // O(1)

// Set для быстрого поиска
val set = Set(1, 2, 3, 4, 5)
val contains = set.contains(3)  // O(1) в среднем
```

## Архитектурные паттерны (расширенные)

### Event-Driven Architecture

**Event-Driven Architecture** позволяет создавать системы, реагирующие на события.

```scala
// Определение события
sealed trait Event
case class UserCreated(user: User) extends Event
case class UserUpdated(user: User) extends Event
case class UserDeleted(userId: Long) extends Event

// Обработчик событий
trait EventHandler {
  def handle(event: Event): Unit
}

// Event Bus
class EventBus {
  private var handlers: List[EventHandler] = Nil

  def subscribe(handler: EventHandler): Unit = {
    handlers = handler :: handlers
  }

  def publish(event: Event): Unit = {
    handlers.foreach(_.handle(event))
  }
}

// Использование
val eventBus = new EventBus()
eventBus.subscribe(new UserEventHandler())
eventBus.publish(UserCreated(User(1, "Alice", "alice@example.com")))
```

### CQRS (Command `Query Responsibility` Segregation)

**CQRS** разделяет операции чтения и записи.

```scala
// Command
sealed trait Command
case class CreateUser(name: String, email: String) extends Command
case class UpdateUser(id: Long, name: String) extends Command

// Query
sealed trait Query
case class GetUser(id: Long) extends Query
case class GetAllUsers() extends Query

// Command Handler
trait CommandHandler {
  def handle(command: Command): Unit
}

// Query Handler
trait QueryHandler {
  def handle(query: Query): Option[User]
}
```


## Решение проблем

Типичные проблемы и решения см. в официальной документации (блок «Полезные ссылки» в начале документа).

## Частые вопросы

Ответы на частые вопросы по теме см. в разделах «Введение» и «Лучшие практики» в документе.

## Заключение

**Scala** предоставляет множество продвинутых техник и паттернов для создания эффективного и поддерживаемого кода. Понимание паттернов проектирования (Strategy, `Observer`, `Factory`, `Builder`, `Adapter`, Decorator), **best practices**, продвинутых техник (Tagless `Final`, `Free Monad`, `Type-level Programming`, `Dependent Types`, `Phantom` Types), оптимизации кода (ленивые вычисления, мемоизация, специализация, inline методы, оптимизация коллекций), и архитектурных паттернов (Layered `Architecture`, `Hexagonal Architecture`, `Event`-`Driven Architecture`, CQRS) позволяет создавать масштабируемые, надежные и высокопроизводительные приложения. **Scala** особенно полезен для создания приложений, которые требуют типобезопасности, функционального подхода, высокой производительности, и сложной архитектуры.

## Дополнительные ресурсы

**Для дальнейшего изучения дополнительных тем **Scala** рекомендуется:**

- [Scala Documentation](https://docs.scala-lang.org/)
- [Scala Style Guide](https://docs.scala-lang.org/style/)
- [Effective Scala](https://twitter.github.io/effectivescala/)

## Дополнительные паттерны и техники

### Template Method Pattern

**Template Method Pattern** определяет скелет алгоритма, позволяя подклассам переопределять некоторые шаги.

```scala
// Абстрактный класс с шаблонным методом
abstract class DataProcessor {
  // Шаблонный метод
  def process(data: String): String = {
    val validated = validate(data)
    val transformed = transform(validated)
    val saved = save(transformed)
    saved
  }

  // Шаги, которые должны быть реализованы подклассами
  def validate(data: String): String
  def transform(data: String): String
  def save(data: String): String
}

// Конкретные реализации
class CSVProcessor extends DataProcessor {
  def validate(data: String): String = {
    if (data.contains(",")) data
    else throw new IllegalArgumentException("Invalid CSV")
  }

  def transform(data: String): String = {
    data.split(",").map(_.trim).mkString("|")
  }

  def save(data: String): String = {
    s"Saved CSV: $data"
  }
}

class JSONProcessor extends DataProcessor {
  def validate(data: String): String = {
    if (data.startsWith("{") && data.endsWith("}")) data
    else throw new IllegalArgumentException("Invalid JSON")
  }

  def transform(data: String): String = {
    data.replace("\"", "'")
  }

  def save(data: String): String = {
    s"Saved JSON: $data"
  }
}
```

### Visitor Pattern

**Visitor Pattern** позволяет добавлять новые операции к объектам без изменения их классов.

```scala
// Определение элементов
sealed trait Element {
  def accept(visitor: Visitor): String
}

case class TextElement(text: String) extends Element {
  def accept(visitor: Visitor): String = visitor.visitText(this)
}

case class ImageElement(src: String) extends Element {
  def accept(visitor: Visitor): String = visitor.visitImage(this)
}

// Определение посетителя
trait Visitor {
  def visitText(element: TextElement): String
  def visitImage(element: ImageElement): String
}

// Конкретные посетители
class HTMLVisitor extends Visitor {
  def visitText(element: TextElement): String = s"<p>${element.text}</p>"
  def visitImage(element: ImageElement): String = s"<img src=\"${element.src}\" />"
}

class MarkdownVisitor extends Visitor {
  def visitText(element: TextElement): String = element.text
  def visitImage(element: ImageElement): String = s"![image](${element.src})"
}
```

### Chain of Responsibility Pattern

**Chain of Responsibility Pattern** позволяет передавать запросы по цепочке обработчиков.

```scala
// Определение обработчика
trait Handler {
  def handle(request: String): Option[String]
  def setNext(handler: Handler): Handler
}

// Базовый обработчик
abstract class BaseHandler extends Handler {
  private var next: Option[Handler] = None

  def setNext(handler: Handler): Handler = {
    next = Some(handler)
    handler
  }

  def handle(request: String): Option[String] = {
    if (canHandle(request)) {
      process(request)
    } else {
      next.flatMap(_.handle(request))
    }
  }

  protected def canHandle(request: String): Boolean
  protected def process(request: String): Option[String]
}

// Конкретные обработчики
class AuthenticationHandler extends BaseHandler {
  protected def canHandle(request: String): Boolean = {
    request.startsWith("AUTH:")
  }

  protected def process(request: String): Option[String] = {
    Some(s"Authenticated: ${request.substring(5)}")
  }
}

class AuthorizationHandler extends BaseHandler {
  protected def canHandle(request: String): Boolean = {
    request.startsWith("AUTHZ:")
  }

  protected def process(request: String): Option[String] = {
    Some(s"Authorized: ${request.substring(6)}")
  }
}

class ValidationHandler extends BaseHandler {
  protected def canHandle(request: String): Boolean = {
    request.startsWith("VALID:")
  }

  protected def process(request: String): Option[String] = {
    Some(s"Validated: ${request.substring(6)}")
  }
}

// Использование
val chain = new AuthenticationHandler()
  .setNext(new AuthorizationHandler())
  .setNext(new ValidationHandler())
```

### Command Pattern

**Command Pattern** инкапсулирует запросы как объекты.

```scala
// Определение команды
trait Command {
  def execute(): Unit
  def undo(): Unit
}

// Конкретные команды
case class CreateUserCommand(user: User, repository: UserRepository) extends Command {
  private var createdId: Option[Long] = None

  def execute(): Unit = {
    createdId = Some(repository.create(user))
  }

  def undo(): Unit = {
    createdId.foreach(repository.delete)
  }
}

case class UpdateUserCommand(id: Long, oldUser: User, newUser: User, repository: UserRepository) extends Command {
  def execute(): Unit = {
    repository.update(id, newUser)
  }

  def undo(): Unit = {
    repository.update(id, oldUser)
  }
}

// Invoker
class CommandInvoker {
  private var history: List[Command] = Nil

  def execute(command: Command): Unit = {
    command.execute()
    history = command :: history
  }

  def undo(): Unit = {
    history match {
      case head :: tail =>
        head.undo()
        history = tail
      case Nil =>
        println("No commands to undo")
    }
  }
}
```

### Memento Pattern

**Memento Pattern** позволяет сохранять и восстанавливать состояние объекта.

```scala
// Memento
case class Memento(state: String)

// Originator
class Originator {
  private var state: String = ""

  def setState(state: String): Unit = {
    this.state = state
  }

  def getState(): String = state

  def save(): Memento = Memento(state)

  def restore(memento: Memento): Unit = {
    state = memento.state
  }
}

// Caretaker
class Caretaker {
  private var mementos: List[Memento] = Nil

  def save(memento: Memento): Unit = {
    mementos = memento :: mementos
  }

  def restore(): Option[Memento] = {
    mementos match {
      case head :: tail =>
        mementos = tail
        Some(head)
      case Nil => None
    }
  }
}
```

## Заключение (финальное расширенное)

**Scala** предоставляет множество продвинутых техник и паттернов для создания эффективного и поддерживаемого кода. Понимание паттернов проектирования (Strategy, `Observer`, `Factory`, `Builder`, `Adapter`, `Decorator`, `Template Method`, `Visitor`, `Chain of Responsibility`, `Command`, Memento), **best practices**, продвинутых техник (Tagless `Final`, `Free Monad`, `Type-level Programming`, `Dependent Types`, `Phantom` Types), оптимизации кода (ленивые вычисления, мемоизация, специализация, inline методы, оптимизация коллекций), и архитектурных паттернов (Layered `Architecture`, `Hexagonal Architecture`, `Event`-`Driven Architecture`, CQRS) позволяет создавать масштабируемые, надежные и высокопроизводительные приложения. **Scala** особенно полезен для создания приложений, которые требуют типобезопасности, функционального подхода, высокой производительности, и сложной архитектуры.

### Практические примеры: Архитектурные паттерны

```scala
// Hexagonal Architecture (Ports and Adapters)
trait UserRepository {
  def findById(id: Long): Option[User]
  def save(user: User): User
}

// Adapter для базы данных
class DatabaseUserRepository extends UserRepository {
  def findById(id: Long): Option[User] = {
    // Реализация с базой данных
    None
  }

  def save(user: User): User = {
    // Реализация сохранения
    user
  }
}

// Adapter для in-memory хранилища (для тестов)
class InMemoryUserRepository extends UserRepository {
  private var users: Map[Long, User] = Map.empty

  def findById(id: Long): Option[User] = users.get(id)

  def save(user: User): User = {
    val saved = user.copy(id = System.currentTimeMillis())
    users = users + (saved.id -> saved)
    saved
  }
}
```

### Практические примеры: Event-Driven Architecture

```scala
// Event-Driven Architecture
sealed trait Event
case class UserCreated(userId: Long, name: String) extends Event
case class UserUpdated(userId: Long, name: String) extends Event
case class UserDeleted(userId: Long) extends Event

trait EventHandler {
  def handle(event: Event): Unit
}

class UserEventHandler extends EventHandler {
  def handle(event: Event): Unit = event match {
    case UserCreated(id, name) => println(s"User created: $id - $name")
    case UserUpdated(id, name) => println(s"User updated: $id - $name")
    case UserDeleted(id) => println(s"User deleted: $id")
  }
}
```

Ключевые преимущества **Scala** включают мощную систему типов, функциональное программирование, объектно-ориентированное программирование, композируемость, типобезопасность, выразительность, производительность, и богатую экосистему библиотек и фреймворков. Эти преимущества делают **Scala** идеальным выбором для создания современных приложений, которые требуют высокой производительности, типобезопасности, функционального подхода, и сложной архитектуры.
