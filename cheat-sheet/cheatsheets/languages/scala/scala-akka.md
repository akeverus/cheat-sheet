---
title: "Scala Akka"
description: "Полное руководство по Akka в Scala: Actors, ActorSystem, сообщения, supervision, routing, persistence"
tags: ["scala", "akka", "actors", "concurrency", "distributed-systems"]
difficulty: "advanced"
prerequisites: ["scala/scala-concurrency.md"]
next: []
updated: "2025-01-16"
related: ["scala/scala-concurrency.md", "scala/scala-basics.md"]
---

# Scala Akka

Кратко: полное руководство по Akka в Scala: Actors, ActorSystem, сообщения, supervision, routing, persistence.

**Дата последнего обновления:** 2025-01-16

## Полезные ссылки

### Официальная документация
- [Akka Documentation](https://doc.akka.io/)
- [Akka Actors Guide](https://doc.akka.io/docs/akka/current/typed/actors.html)

### См. также
- `./scala-concurrency.md` - конкурентность в Scala
- `./scala-basics.md` - основы Scala

## Содержание

- [Введение в Akka](#введение-в-akka)
- [Actors](#actors)
- [ActorSystem](#actorsystem)
- [Сообщения](#сообщения)
- [Supervision](#supervision)
- [Routing](#routing)
- [Лучшие практики](#лучшие-практики)

## Введение в Akka

Akka - это toolkit и runtime для построения высокодоступных, распределенных систем на основе акторной модели программирования. Акторная модель обеспечивает изоляцию состояния и асинхронную обработку сообщений, что делает системы более устойчивыми к ошибкам и легче масштабируемыми. Akka абстрагирует низкоуровневые детали многопоточности, предоставляя высокоуровневую модель для конкурентного программирования.

Акторная модель основана на принципе, что акторы общаются только через обмен неизменяемыми сообщениями. Это устраняет необходимость в явной синхронизации и блокировках, так как каждый актор обрабатывает сообщения последовательно. Акторы могут создавать другие акторы и отправлять им сообщения, образуя иерархию супервизоров для обработки ошибок.

### Основные концепции

- **Actors** - легковесные сущности, обрабатывающие сообщения. Каждый актор имеет изолированное состояние и обрабатывает сообщения последовательно, что устраняет проблемы с гонками данных. Акторы могут создавать другие акторы, образуя иерархию.

- **ActorSystem** - контейнер для акторов. ActorSystem управляет жизненным циклом акторов, пулом потоков для их выполнения и предоставляет инфраструктуру для обмена сообщениями. Обычно в приложении создается один ActorSystem.

- **Messages** - неизменяемые сообщения между акторами. Сообщения должны быть неизменяемыми (case classes или case objects), чтобы избежать проблем с разделяемым состоянием. Сообщения отправляются асинхронно и обрабатываются получателем в порядке получения.

- **Supervision** - стратегия обработки ошибок. Каждый актор может быть супервизором для других акторов и определять стратегию обработки ошибок (Resume, Restart, Stop, Escalate). Это обеспечивает отказоустойчивость системы.

## Actors

Actor - это сущность, которая обрабатывает сообщения асинхронно. Каждый актор имеет изолированное состояние и обрабатывает сообщения последовательно, один за другим. Это обеспечивает потокобезопасность без явных блокировок, так как только один поток может обрабатывать сообщения актора в любой момент времени.

Акторы общаются только через обмен сообщениями, что делает систему более декомпозируемой и тестируемой. Состояние актора инкапсулировано и недоступно напрямую извне, что предотвращает гонки данных и проблемы с конкурентным доступом.

```scala
import akka.actor.{Actor, ActorRef, ActorSystem, Props}

// Определение актора
// Метод receive определяет, как актор обрабатывает различные типы сообщений
// Pattern matching позволяет элегантно обрабатывать разные сообщения
class Greeter extends Actor {
  def receive: Receive = {
    case "hello" => println("Hello!")
    case "goodbye" => println("Goodbye!")
    case _ => println("Unknown message")
  }
}

// Создание актора
// ActorSystem управляет жизненным циклом акторов и пулом потоков
// Props описывает, как создать актор
// ActorRef - это ссылка на актор, используемая для отправки сообщений
val system = ActorSystem("MySystem")
val greeter: ActorRef = system.actorOf(Props[Greeter], "greeter")

// Отправка сообщений
// Оператор ! (tell) отправляет сообщение асинхронно
// Сообщения обрабатываются в порядке получения, но отправка не блокирует отправителя
greeter ! "hello"     // асинхронная отправка
greeter ! "goodbye"
```

Акторы обрабатывают сообщения последовательно, что гарантирует потокобезопасность доступа к состоянию актора. Однако это означает, что длительные операции могут блокировать обработку других сообщений, поэтому важно избегать блокирующих операций в обработчиках сообщений.

### Typed Actors (Akka Typed)

```scala
import akka.actor.typed.{ActorRef, Behavior}
import akka.actor.typed.scaladsl.{Behaviors, ActorContext}

sealed trait Command
case class Greet(name: String, replyTo: ActorRef[String]) extends Command

object Greeter {
  def apply(): Behavior[Command] = Behaviors.receive { (context, message) =>
    message match {
      case Greet(name, replyTo) =>
        replyTo ! s"Hello, $name!"
        Behaviors.same
    }
  }
}
```

## ActorSystem

ActorSystem - это контейнер для акторов:

```scala
import akka.actor.ActorSystem

// Создание ActorSystem
val system = ActorSystem("MySystem")

// Создание акторов
val actor1 = system.actorOf(Props[MyActor], "actor1")
val actor2 = system.actorOf(Props[MyActor], "actor2")

// Завершение системы
system.terminate()
```

## Сообщения

Сообщения в Akka должны быть неизменяемыми:

```scala
// Хорошо - case class (неизменяемый)
case class Greeting(message: String)

// Хорошо - case object
case object Start

// Плохо - изменяемый класс
class BadMessage(var value: Int)  // не рекомендуется
```

### Отправка сообщений

```scala
// Fire-and-forget
actor ! message

// Request-Reply
val future = actor ? message  // возвращает Future

// Tell pattern
actor.tell(message, sender)
```

## Supervision

Supervision определяет, как обрабатывать ошибки в акторах:

```scala
import akka.actor.SupervisorStrategy
import akka.actor.OneForOneStrategy
import akka.actor.SupervisorStrategy._

class Supervisor extends Actor {
  override val supervisorStrategy = OneForOneStrategy() {
    case _: ArithmeticException => Resume
    case _: NullPointerException => Restart
    case _: IllegalArgumentException => Stop
    case _: Exception => Escalate
  }
  
  def receive: Receive = {
    case props: Props => sender() ! context.actorOf(props)
  }
}
```

## Routing

Routing позволяет распределять сообщения между несколькими акторами:

```scala
import akka.routing.RoundRobinPool

val router = system.actorOf(
  RoundRobinPool(5).props(Props[Worker]),
  "router"
)

// Сообщения распределяются между 5 воркерами
router ! "message1"
router ! "message2"
```

## Лучшие практики

### Использование неизменяемых сообщений

```scala
// Хорошо - case class
case class ProcessData(data: String)

// Плохо - изменяемый класс
class ProcessDataBad(var data: String)
```

### Избегание блокирующих операций

```scala
// Хорошо - асинхронная операция
def receive: Receive = {
  case data =>
    val future = processAsync(data)
    future.onComplete { result =>
      // обработка результата
    }
}

// Плохо - блокирующая операция
def receive: Receive = {
  case data =>
    val result = processBlocking(data)  // блокирует актор
}
```

## Akka Streams

Akka Streams предоставляет реактивную модель для обработки потоков данных, основанную на Reactive Streams спецификации. Akka Streams позволяет обрабатывать потоки данных с автоматической обработкой backpressure, что обеспечивает эффективное использование ресурсов и предотвращает переполнение памяти при обработке больших объемов данных. Akka Streams особенно полезен для обработки потоков событий, файлов, и данных из различных источников.

Akka Streams состоит из трех основных компонентов: Source (источник данных), Flow (преобразование данных), и Sink (получатель данных). Эти компоненты можно комбинировать для создания сложных графов обработки данных. Akka Streams также поддерживает материализацию графов, что позволяет выполнять графы на различных execution context.

Akka Streams предоставляет реактивную модель для обработки потоков данных:

```scala
import akka.actor.ActorSystem
import akka.stream.scaladsl.{Source, Sink, Flow}
import akka.stream.Materializer

implicit val system = ActorSystem("StreamSystem")
implicit val materializer = Materializer(system)

// Создание источника
// Source представляет источник данных, который может быть конечным или бесконечным
// Source(1 to 10) создает источник из диапазона чисел
val source = Source(1 to 10)

// Создание потока обработки
// Flow представляет преобразование данных от источника к стоку
// map(_ * 2) умножает каждый элемент на 2
// filter(_ > 5) оставляет только элементы больше 5
val flow = Flow[Int].map(_ * 2).filter(_ > 5)

// Создание стока
// Sink представляет получатель данных, который обрабатывает элементы потока
// Sink.foreach выполняет функцию для каждого элемента
val sink = Sink.foreach(println)

// Композиция
// via соединяет источник с потоком обработки
// to соединяет поток обработки со стоком
// run() материализует граф и начинает выполнение
val graph = source.via(flow).to(sink)
graph.run()
// Выводит: 12, 14, 16, 18, 20
```

Akka Streams позволяет обрабатывать потоки данных реактивным способом, обеспечивая backpressure и эффективное использование ресурсов. Backpressure автоматически замедляет источник данных, если сток не успевает обрабатывать элементы, что предотвращает переполнение памяти и обеспечивает стабильную работу системы при обработке больших объемов данных.

## Akka Persistence

Akka Persistence позволяет сохранять состояние акторов через Event Sourcing, где все изменения состояния сохраняются как последовательность событий. Это обеспечивает полную историю изменений и возможность восстановления состояния актора на любой момент времени. Akka Persistence особенно полезен для создания отказоустойчивых систем, где важно сохранять состояние акторов между перезапусками.

Akka Persistence поддерживает различные плагины для хранения событий, включая in-memory, файловую систему, и базы данных. Это позволяет выбирать подходящий способ хранения в зависимости от требований приложения. Akka Persistence также поддерживает snapshots, которые позволяют сохранять снимки состояния для быстрого восстановления без необходимости воспроизведения всех событий.

Akka Persistence позволяет сохранять состояние акторов:

```scala
import akka.persistence.PersistentActor

// События для Event Sourcing
sealed trait CounterEvent
case object Incremented extends CounterEvent
case object Decremented extends CounterEvent

// Команды для актора
sealed trait CounterCommand
case object Increment extends CounterCommand
case object Decrement extends CounterCommand
case object GetCount extends CounterCommand

class PersistentCounter extends PersistentActor {
  // Уникальный идентификатор для персистентного актора
  // Используется для хранения и восстановления состояния
  override def persistenceId: String = "counter-1"
  
  // Состояние актора
  // Восстанавливается из событий при старте актора
  var count = 0
  
  // Обработка команд
  // Команды преобразуются в события, которые сохраняются
  def receiveCommand: Receive = {
    case Increment =>
      // persist сохраняет событие и затем обрабатывает его
      // Событие сохраняется перед обновлением состояния
      persist(Incremented) { event =>
        count += 1
        // После сохранения события можно отправить подтверждение
      }
    case Decrement =>
      persist(Decremented) { event =>
        count -= 1
      }
    case GetCount =>
      sender() ! count
  }
  
  // Восстановление состояния из событий
  // Вызывается при старте актора для восстановления состояния
  def receiveRecover: Receive = {
    case Incremented =>
      count += 1
    case Decremented =>
      count -= 1
  }
}
```

Akka Persistence обеспечивает восстановление состояния акторов после перезапуска. При перезапуске актора все сохраненные события воспроизводятся в порядке их возникновения, что позволяет восстановить точное состояние актора. Это критично для создания отказоустойчивых систем, где важно сохранять состояние между перезапусками приложения.

## Распределенные системы

Akka позволяет создавать распределенные системы:

```scala
import akka.cluster.Cluster
import akka.cluster.ClusterEvent._

class ClusterListener extends Actor {
  val cluster = Cluster(context.system)
  
  override def preStart(): Unit = {
    cluster.subscribe(self, classOf[MemberEvent])
  }
  
  def receive: Receive = {
    case MemberUp(member) =>
      println(s"Member is Up: ${member.address}")
    case MemberRemoved(member, previousStatus) =>
      println(s"Member is Removed: ${member.address}")
  }
}
```

Akka Cluster позволяет создавать распределенные системы с автоматическим обнаружением узлов и балансировкой нагрузки.

## Детальное изучение Akka Typed

Akka Typed предоставляет типобезопасную модель акторов, где сообщения и поведение акторов строго типизированы. Это обеспечивает безопасность типов на этапе компиляции и предотвращает ошибки, связанные с неправильными типами сообщений.

### Базовые концепции Akka Typed

```scala
import akka.actor.typed.{ActorRef, Behavior}
import akka.actor.typed.scaladsl.{Behaviors, ActorContext}

// Определение протокола сообщений
sealed trait Command
case class Greet(name: String, replyTo: ActorRef[String]) extends Command
case object Stop extends Command

// Определение поведения актора
object Greeter {
  def apply(): Behavior[Command] = Behaviors.receive { (context, message) =>
    message match {
      case Greet(name, replyTo) =>
        context.log.info(s"Greeting $name")
        replyTo ! s"Hello, $name!"
        Behaviors.same
      case Stop =>
        context.log.info("Stopping")
        Behaviors.stopped
    }
  }
}

// Создание актора
val system = ActorSystem(Greeter(), "greeter-system")
val greeter = system
```

### Supervision в Akka Typed

Supervision в Akka Typed позволяет определять стратегии обработки ошибок для дочерних акторов.

```scala
import akka.actor.typed.SupervisorStrategy

// Restart стратегия
val supervisedBehavior = Behaviors.supervise(childBehavior)
  .onFailure[Exception](SupervisorStrategy.restart)

// Resume стратегия
val resumeBehavior = Behaviors.supervise(childBehavior)
  .onFailure[IllegalArgumentException](SupervisorStrategy.resume)

// Stop стратегия
val stopBehavior = Behaviors.supervise(childBehavior)
  .onFailure[Error](SupervisorStrategy.stop)
```

## Расширенные возможности Akka

### Akka Cluster

Akka Cluster позволяет создавать распределенные системы с автоматическим обнаружением узлов и балансировкой нагрузки.

```scala
import akka.cluster.Cluster
import akka.cluster.ClusterEvent._

class ClusterListener extends Actor {
  val cluster = Cluster(context.system)
  
  override def preStart(): Unit = {
    cluster.subscribe(self, classOf[MemberEvent], classOf[UnreachableMember])
  }
  
  override def postStop(): Unit = {
    cluster.unsubscribe(self)
  }
  
  def receive: Receive = {
    case MemberUp(member) =>
      println(s"Member is Up: ${member.address}")
    case UnreachableMember(member) =>
      println(s"Member detected as unreachable: ${member.address}")
    case MemberRemoved(member, previousStatus) =>
      println(s"Member is Removed: ${member.address} after ${previousStatus}")
  }
}
```

### Akka Cluster Sharding

Akka Cluster Sharding позволяет распределять акторы по узлам кластера автоматически.

```scala
import akka.cluster.sharding.{ClusterSharding, ClusterShardingSettings, ShardRegion}

// Определение экстрактора сущности
val extractEntityId: ShardRegion.ExtractEntityId = {
  case msg @ ProcessMessage(id, _) => (id.toString, msg)
}

// Определение экстрактора шарда
val extractShardId: ShardRegion.ExtractShardId = {
  case ProcessMessage(id, _) => (id % 10).toString
}

// Создание шардированного региона
val shardRegion: ActorRef[ShardRegion.Command] = ClusterSharding(system).init(
  Entity(EntityTypeKey[ProcessMessage]("Processor")) { entityContext =>
    Processor(entityContext.entityId)
  },
  ClusterShardingSettings(system),
  extractEntityId,
  extractShardId
)
```

### Akka Persistence (расширенное)

Akka Persistence позволяет сохранять состояние акторов через Event Sourcing.

```scala
import akka.persistence.PersistentActor

sealed trait Event
case class Incremented(delta: Int) extends Event
case class Decremented(delta: Int) extends Event

sealed trait Command
case class Increment(delta: Int) extends Command
case class Decrement(delta: Int) extends Command
case object GetValue extends Command

class PersistentCounter extends PersistentActor {
  override def persistenceId: String = "counter-1"
  
  var count = 0
  
  def receiveCommand: Receive = {
    case Increment(delta) =>
      persist(Incremented(delta)) { event =>
        count += event.delta
        sender() ! count
      }
    case Decrement(delta) =>
      persist(Decremented(delta)) { event =>
        count -= event.delta
        sender() ! count
      }
    case GetValue =>
      sender() ! count
  }
  
  def receiveRecover: Receive = {
    case Incremented(delta) =>
      count += delta
    case Decremented(delta) =>
      count -= delta
  }
}
```

## Продвинутые возможности Akka

### Akka Streams (расширенное)

Akka Streams предоставляет расширенные возможности для работы с потоками данных.

```scala
import akka.stream._
import akka.stream.scaladsl._

// Создание потока
val source = Source(1 to 100)

// Трансформация потока
val transformed = source
  .filter(_ % 2 == 0)
  .map(_ * 2)
  .take(10)

// Объединение потоков
val source1 = Source(1 to 10)
val source2 = Source(10 to 20)
val merged = Source.combine(source1, source2)(Merge(_))

// Группировка потока
val grouped = source.groupBy(3, _ % 3)

// Обработка ошибок в потоке
val errorHandled = source.recover {
  case e: Exception => 0
}
```

### Akka HTTP

Akka HTTP предоставляет функциональный API для создания HTTP серверов и клиентов.

```scala
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport._
import spray.json.DefaultJsonProtocol._

// Определение маршрутов
val routes = path("users" / LongNumber) { userId =>
  get {
    complete(getUser(userId))
  } ~
  put {
    entity(as[User]) { user =>
      complete(updateUser(userId, user))
    }
  } ~
  delete {
    complete(deleteUser(userId))
  }
}
```

### Akka Persistence Query

Akka Persistence Query предоставляет способ запроса событий из журнала событий.

```scala
import akka.persistence.query.PersistenceQuery
import akka.persistence.query.journal.leveldb.scaladsl.LeveldbReadJournal

val readJournal = PersistenceQuery(system)
  .readJournalFor[LeveldbReadJournal](LeveldbReadJournal.Identifier)

// Запрос всех событий
val events = readJournal.eventsByPersistenceId("counter", 0, Long.MaxValue)

// Запрос событий по тегам
val taggedEvents = readJournal.eventsByTag("user-events", Offset.noOffset)
```

### Akka Cluster (расширенное)

Akka Cluster предоставляет расширенные возможности для создания распределенных систем.

```scala
import akka.cluster.Cluster
import akka.cluster.ClusterEvent._

// Подписка на события кластера
Cluster(system).subscribe(
  self,
  classOf[MemberEvent],
  classOf[UnreachableMember]
)

// Получение информации о кластере
val cluster = Cluster(system)
val members = cluster.state.members
val leader = cluster.state.leader
```

### Akka Cluster Sharding (расширенное)

Akka Cluster Sharding предоставляет расширенные возможности для распределения акторов по кластеру.

```scala
import akka.cluster.sharding.ClusterSharding
import akka.cluster.sharding.ClusterShardingSettings

// Регистрация шардированных акторов
val shardRegion = ClusterSharding(system).start(
  typeName = "Counter",
  entityProps = Props[CounterActor],
  settings = ClusterShardingSettings(system),
  extractEntityId = {
    case msg @ Increment(id, _) => (id.toString, msg)
  },
  extractShardId = {
    case Increment(id, _) => (id % 10).toString
  }
)
```

## Заключение (расширенное)

Akka предоставляет мощную модель для построения распределенных и конкурентных систем. Понимание акторов, сообщений, supervision, routing, Akka Typed, Akka Cluster, Akka Cluster Sharding, Akka Persistence, Akka Streams, Akka HTTP, Akka Persistence Query, и их практических применений позволяет создавать масштабируемые и отказоустойчивые приложения. Akka Streams и Akka Persistence расширяют возможности фреймворка для работы с потоками данных и сохранением состояния. Akka HTTP предоставляет функциональный API для создания HTTP серверов и клиентов. Akka особенно полезен для создания распределенных систем, которые должны обрабатывать множество одновременных запросов, масштабироваться горизонтально, обеспечивать высокую доступность, работать с потоками данных, создавать HTTP API, и запрашивать события из журнала событий.

## Практические примеры использования Akka

### Создание акторной системы с использованием Akka Typed

Akka Typed предоставляет типобезопасный API для создания акторных систем.

```scala
import akka.actor.typed.{ActorRef, Behavior}
import akka.actor.typed.scaladsl.Behaviors

// Определение протокола актора
sealed trait UserCommand
case class CreateUser(name: String, replyTo: ActorRef[UserCreated]) extends UserCommand
case class GetUser(id: Int, replyTo: ActorRef[Option[User]]) extends UserCommand

sealed trait UserResponse
case class UserCreated(id: Int) extends UserResponse

// Реализация актора
object UserActor {
  def apply(): Behavior[UserCommand] = Behaviors.receive { (context, message) =>
    message match {
      case CreateUser(name, replyTo) =>
        val id = generateId()
        replyTo ! UserCreated(id)
        Behaviors.same
      case GetUser(id, replyTo) =>
        val user = findUser(id)
        replyTo ! user
        Behaviors.same
    }
  }
}
```

### Использование Akka Streams для обработки потоков данных

Akka Streams позволяет обрабатывать потоки данных эффективно.

```scala
import akka.stream.scaladsl.{Sink, Source}

// Обработка потока данных
val source = Source(1 to 1000)
val flow = source
  .filter(_ % 2 == 0)
  .map(_ * 2)
  .take(100)
val sink = Sink.foreach(println)

val graph = source.via(flow).to(sink)
```

### Практические примеры: Akka Typed Actors

```scala
import akka.actor.typed.{ActorRef, Behavior}
import akka.actor.typed.scaladsl.{ActorContext, Behaviors}

sealed trait Command
case class Greet(name: String, replyTo: ActorRef[String]) extends Command
case class Stop() extends Command

object Greeter {
  def apply(): Behavior[Command] = Behaviors.receive { (context, message) =>
    message match {
      case Greet(name, replyTo) =>
        replyTo ! s"Hello, $name!"
        Behaviors.same
      case Stop() =>
        Behaviors.stopped
    }
  }
}

// Использование
val system = ActorSystem(Greeter(), "greeter")
val greeterRef = system.ref
```

### Практические примеры: Akka Streams для обработки данных

```scala
import akka.stream.scaladsl.{Source, Flow, Sink}
import akka.NotUsed

// Создание источника данных
val source: Source[Int, NotUsed] = Source(1 to 1000)

// Обработка данных
val flow: Flow[Int, Int, NotUsed] = Flow[Int]
  .filter(_ % 2 == 0)
  .map(_ * 2)
  .take(100)

// Приемник данных
val sink: Sink[Int, Future[Done]] = Sink.foreach(println)

// Соединение компонентов
val graph = source.via(flow).to(sink)
val result = graph.run()
```

### Практические примеры: Supervision стратегия

```scala
import akka.actor.SupervisorStrategy._
import akka.actor.{Actor, OneForOneStrategy}

class SupervisorActor extends Actor {
  override val supervisorStrategy = OneForOneStrategy() {
    case _: ArithmeticException => Resume
    case _: NullPointerException => Restart
    case _: IllegalArgumentException => Stop
    case _: Exception => Escalate
  }
  
  def receive = {
    case msg => // Обработка сообщений
  }
}
```

### Практические примеры: Работа с Akka Cluster

```scala
import akka.actor.typed.{ActorSystem, Behavior}
import akka.actor.typed.scaladsl.Behaviors
import akka.cluster.Cluster

// Создание кластера
val system = ActorSystem(Behaviors.empty, "ClusterSystem")

// Получение информации о кластере
val cluster = Cluster(system)
val members = cluster.state.members
val leader = cluster.state.leader
```

### Практические примеры: Работа с Akka Persistence

```scala
import akka.persistence.PersistentActor

// Persistent Actor для сохранения состояния
class PersistentCounter extends PersistentActor {
  override def persistenceId: String = "counter-1"
  
  var count = 0
  
  override def receiveCommand: Receive = {
    case "increment" => persist("incremented") { event =>
      count += 1
    }
    case "get" => sender() ! count
  }
  
  override def receiveRecover: Receive = {
    case "incremented" => count += 1
  }
}
```

## Дополнительные ресурсы

Для дальнейшего изучения Akka рекомендуется:

- [Akka Documentation](https://doc.akka.io/)
- [Akka Typed Guide](https://doc.akka.io/docs/akka/current/typed/actors.html)

