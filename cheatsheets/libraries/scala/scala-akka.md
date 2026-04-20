---
title: "Akka"
description: "Akka - это мощный фреймворк для создания высококонкурентных, распределенных и отказоустойчивых приложений на JVM. Основан на акторной модели, предоставляет инструменты для реактивного программирования и распределенных вычислений."
tags:
  - libraries
  - scala
  - scala-akka
difficulty: "intermediate"
prerequisites: []
next: []
updated: "2026-02-11"
---
# Akka

**Akka** - это мощный фреймворк для создания высококонкурентных, распределенных и отказоустойчивых приложений на **JVM**. Основан на акторной модели, предоставляет инструменты для реактивного программирования и распределенных вычислений.

## Полезные ссылки
- [Официальная документация Akka](https://akka.io/docs/)
- [Akka GitHub](https://github.com/akka/akka)
- [Akka Typed Guide](https://doc.akka.io/docs/akka/current/typed/guide.html)
- [Akka Streams](https://doc.akka.io/docs/akka/current/stream/index.html)
- [Akka HTTP](https://doc.akka.io/docs/akka-http/current/index.html)
- [Akka Cluster](https://doc.akka.io/docs/akka/current/cluster-usage.html)
- [Akka Persistence](https://doc.akka.io/docs/akka/current/persistence.html)

## Содержание

- [Основные концепции Akka](#основные-концепции-akka)
  - [Actor System](#actor-system)
  - [Акторы (Actors)](#акторы-actors)
  - [Иерархия акторов](#иерархия-акторов)
  - [Обработка ошибок и супервизия](#обработка-ошибок-и-супервизия)
- [Акка Typed](#акка-typed)
  - [Typed Actors](#typed-actors)
  - [Behaviors API](#behaviors-api)
- [Потоки Akka](#потоки-akka)
  - [Основы Streams](#основы-streams)
  - [Graph DSL](#graph-dsl)
  - [Reactive Streams](#reactive-streams)
  - [Backpressure](#backpressure)
  - [Error Handling](#error-handling)
- [Akka HTTP](#akka-http)
  - [HTTP Server](#http-server)
  - [HTTP Client](#http-client)
  - [WebSocket](#websocket)
- [Кластер Akka](#кластер-akka)
  - [Настройка кластера](#настройка-кластера)
  - [Cluster Actors](#cluster-actors)
  - [Cluster Sharding](#cluster-sharding)
  - [Cluster Singleton](#cluster-singleton)
- [Персистентность Akka](#персистентность-akka)
  - [Persistent Actors](#persistent-actors)
  - [Snapshots](#snapshots)
- [TestKit Akka](#testkit-akka)
  - [Testing Actors](#testing-actors)
  - [Testing Streams](#testing-streams)
- [Интеграция с Spring Boot](#интеграция-с-spring-boot)
  - [Akka в **Spring** приложении](#akka-в-spring-приложении)
  - [Configuration](#configuration)
- [Лучшие практики](#лучшие-практики)
  - [Actor Design](#actor-design)
  - [Performance Optimization](#performance-optimization)
- [Устранение неполадок](#устранение-неполадок)
  - [Common Issues](#common-issues)
  - [Monitoring and Debugging](#monitoring-and-debugging)
- [Руководство по миграции](#руководство-по-миграции)
  - [From Akka Classic to Typed](#from-akka-classic-to-typed)
  - [From Java to Scala](#from-java-to-scala)
- [См. также](#см-также)

## Основные концепции **Akka**

### **Actor System**

Создание **Actor System** и завершение работы (**terminate**).

```scala
import akka.actor.{ActorSystem, Props}

/
 * Actor System - корневой контейнер для всех акторов
 * Управляет жизненным циклом акторов, диспетчерами и конфигурацией
 */
// Создание Actor System
// "MyActorSystem" - имя системы (используется для логирования и идентификации)
val system = ActorSystem("MyActorSystem")
// Actor System создает иерархию акторов, начиная с корневого актора "/user"
// Все пользовательские акторы создаются под "/user"

// Остановка системы
// terminate() - асинхронно останавливает все акторы и освобождает ресурсы
// Возвращает Future[Terminated] для ожидания полной остановки
system.terminate()
```

### Акторы (**Actors**)
```scala
import akka.actor.{Actor, ActorLogging, Props}

/
 * Определение актора в Akka
 * Актор - изолированная единица вычислений с собственным состоянием
 * Обрабатывает сообщения последовательно (один за раз)
 */
class SimpleActor extends Actor with ActorLogging {
  // Состояние актора - инкапсулировано внутри актора
  // Доступно только этому актору, защищено от гонок данных
  private var counter = 0

  // receive - частичная функция для обработки сообщений
  // Вызывается когда актор получает сообщение
  def receive = {
    // Паттерн-матчинг для обработки различных типов сообщений
    case "increment" =>
      // Обработка сообщения "increment" - увеличение счетчика
      counter += 1
      // log - встроенное логирование через ActorLogging trait
      log.info(s"Counter incremented to $counter")

    case "get" =>
      // sender() - ссылка на актор, отправивший текущее сообщение
      // ! - оператор "tell" для отправки сообщения (fire-and-forget)
      sender() ! counter  // Отправка текущего значения счетчика отправителю

    case Reset =>
      // Обработка case object Reset
      counter = 0
      log.info("Counter reset")

    case msg =>
      // Обработка неизвестных сообщений
      log.warning(s"Unknown message: $msg")
      // unhandled() - пометка сообщения как необработанного
      unhandled(msg)
  }
}

/
 * Сопроводные объекты для сообщений
 * Рекомендуется использовать case классы/объекты для типобезопасности
 */
object SimpleActor {
  case object Reset  // Case object для сообщения без параметров
  case class SetValue(value: Int)  // Case class для сообщения с параметрами
}

// Создание актора в Actor System
// Props[SimpleActor]() - фабрика для создания экземпляра актора
// "simpleActor" - имя актора (путь будет "/user/simpleActor")
val simpleActor = system.actorOf(Props[SimpleActor](), "simpleActor")

// Отправка сообщений актору
// ! - оператор "tell" (асинхронная отправка без ожидания ответа)
simpleActor ! "increment"  // Отправка строкового сообщения
simpleActor ! "increment"  // Еще одно сообщение
simpleActor ! SimpleActor.Reset  // Отправка case object

// Запрос с ожиданием ответа (ask pattern)
import scala.concurrent.duration._
import akka.pattern.ask  // Импорт оператора "?"
import akka.util.Timeout

// Timeout - максимальное время ожидания ответа
implicit val timeout = Timeout(5.seconds)
// ? - оператор "ask" (отправка сообщения с ожиданием ответа)
// Возвращает Future[Any] - результат будет доступен в будущем
val futureResult = (simpleActor ? "get").mapTo[Int]
// mapTo[Int] - преобразование Future[Any] в Future[Int] для типобезопасности

// Остановка актора
// PoisonPill - специальное сообщение для остановки актора
// Актор обработает все сообщения до PoisonPill, затем остановится
simpleActor ! akka.actor.PoisonPill
```

### Иерархия акторов
```scala
import akka.actor.{Actor, Props, ActorLogging}

// Родительский актор
class ParentActor extends Actor with ActorLogging {

  // Создание дочерних акторов
  val child1 = context.actorOf(Props[ChildActor](), "child1")
  val child2 = context.actorOf(Props[ChildActor](), "child2")

  def receive = {
    case CreateChild(name) =>
      val child = context.actorOf(Props[ChildActor](), name)
      sender() ! ChildCreated(child)

    case StopChild(name) =>
      val child = context.child(name)
      child.foreach(_ ! "stop")
      sender() ! ChildStopped(name)

    case GetChildren =>
      sender() ! context.children.map(_.path.name).toList
  }

  // Обработка завершения дочерних акторов
  override def supervisorStrategy = OneForOneStrategy() {
    case _: ArithmeticException => Resume
    case _: NullPointerException => Restart
    case _: IllegalArgumentException => Stop
    case _: Exception => Escalate
  }
}

// Дочерний актор
class ChildActor extends Actor with ActorLogging {
  def receive = {
    case "work" =>
      log.info(s"Working for ${self.path}")
      // Выполнение работы

    case "stop" =>
      log.info("Stopping child actor")
      context.stop(self)
  }
}

// Сопроводные объекты
case class CreateChild(name: String)
case class ChildCreated(ref: ActorRef)
case class StopChild(name: String)
case class ChildStopped(name: String)
case object GetChildren
```

### Обработка ошибок и супервизия
```scala
import akka.actor.{Actor, SupervisorStrategy, OneForOneStrategy, AllForOneStrategy}
import scala.concurrent.duration._

// Стратегии супервизии
class Supervisor extends Actor {
  val worker = context.actorOf(Props[Worker](), "worker")

  override val supervisorStrategy = OneForOneStrategy(
    maxNrOfRetries = 10,
    withinTimeRange = 1.minute
  ) {
    case _: ArithmeticException =>
      println("Resuming worker")
      SupervisorStrategy.Resume

    case _: NullPointerException =>
      println("Restarting worker")
      SupervisorStrategy.Restart

    case _: IllegalArgumentException =>
      println("Stopping worker")
      SupervisorStrategy.Stop

    case _: Exception =>
      println("Escalating to parent")
      SupervisorStrategy.Escalate
  }

  def receive = {
    case msg => worker.forward(msg)
  }

  // Обработка завершения дочерних акторов
  override def preRestart(reason: Throwable, message: Option[Any]): Unit = {
    println(s"Supervisor restarting due to: $reason")
    super.preRestart(reason, message)
  }

  override def postRestart(reason: Throwable): Unit = {
    println("Supervisor restarted")
    super.postRestart(reason)
  }
}

class Worker extends Actor {
  var state = 0

  def receive = {
    case "work" =>
      state += 1
      println(s"Working, state = $state")

    case "error1" =>
      throw new ArithmeticException("Division by zero")

    case "error2" =>
      throw new NullPointerException("Null pointer")

    case "error3" =>
      throw new IllegalArgumentException("Invalid argument")
  }

  // Lifecycle hooks
  override def preStart(): Unit = {
    println("Worker starting")
  }

  override def postStop(): Unit = {
    println("Worker stopped")
  }

  override def preRestart(reason: Throwable, message: Option[Any]): Unit = {
    println(s"Worker restarting, state was $state")
    state = 0 // Сброс состояния при перезапуске
  }
}

// Использование
val supervisor = system.actorOf(Props[Supervisor](), "supervisor")
supervisor ! "work"
supervisor ! "error1" // Resume
supervisor ! "error2" // Restart
supervisor ! "error3" // Stop
```

## Акка **Typed**

### **Typed Actors**
```scala
import akka.actor.typed.{ActorSystem, ActorRef, Behavior}
import akka.actor.typed.scaladsl.{Behaviors, ActorContext}

// Определение протокола сообщений
sealed trait Command
case class AddItem(item: String) extends Command
case class RemoveItem(item: String) extends Command
case class GetItems(replyTo: ActorRef[List[String]]) extends Command
case object Clear extends Command

// Typed актор
object ShoppingCart {
  def apply(): Behavior[Command] = cartBehavior(List.empty)

  private def cartBehavior(items: List[String]): Behavior[Command] = {
    Behaviors.receive { (context, message) =>
      message match {
        case AddItem(item) =>
          val newItems = item :: items
          context.log.info(s"Added $item, cart now has ${newItems.size} items")
          cartBehavior(newItems)

        case RemoveItem(item) =>
          val newItems = items.filterNot(_ == item)
          context.log.info(s"Removed $item, cart now has ${newItems.size} items")
          cartBehavior(newItems)

        case GetItems(replyTo) =>
          replyTo ! items
          Behaviors.same

        case Clear =>
          context.log.info("Cart cleared")
          cartBehavior(List.empty)
      }
    }
  }
}

// Создание и использование
val system = ActorSystem(ShoppingCart(), "shopping-cart")

// Отправка сообщений
system ! AddItem("apple")
system ! AddItem("banana")
system ! RemoveItem("apple")

// Запрос состояния
import akka.actor.typed.scaladsl.AskPattern._
import scala.concurrent.duration._

implicit val timeout = Timeout(3.seconds)
implicit val scheduler = system.scheduler

val futureItems = system.ask[Command](ref => GetItems(ref))
futureItems.foreach(items => println(s"Cart contains: $items"))

// Остановка
system.terminate()
```

### **Behaviors API**
```scala
import akka.actor.typed.scaladsl.Behaviors
import scala.concurrent.duration._

// Setup behavior
object DeviceManager {
  sealed trait Command
  case class RequestTrackDevice(groupId: String, deviceId: String, replyTo: ActorRef[DeviceRegistered]) extends Command
  case class DeviceRegistered(device: ActorRef[Device.Command])

  def apply(): Behavior[Command] = {
    Behaviors.setup { context =>
      context.log.info("DeviceManager started")

      Behaviors.receiveMessage {
        case RequestTrackDevice(groupId, deviceId, replyTo) =>
          // Создание группы устройств, если не существует
          val groupActor = context.child(groupId).getOrElse {
            context.spawn(DeviceGroup(groupId), groupId)
          }

          // Пересылка запроса группе
          groupActor ! DeviceGroup.RequestTrackDevice(deviceId, replyTo)
          Behaviors.same
      }
    }
  }
}

// Timer behavior
object TimerActor {
  case object Tick
  case object Stop

  def apply(): Behavior[Tick.type] = {
    Behaviors.withTimers { timers =>
      timers.startTimerWithFixedDelay(Tick, 1.second)

      Behaviors.receiveMessage {
        case Tick =>
          println("Timer ticked")
          Behaviors.same

        case Stop =>
          timers.cancelAll()
          Behaviors.stopped
      }
    }
  }
}

// Stash behavior для буферизации сообщений
object StashExample {
  sealed trait Command
  case class Open(dbUrl: String) extends Command
  case class Query(sql: String, replyTo: ActorRef[QueryResult]) extends Command
  case object Close extends Command

  def apply(): Behavior[Command] = {
    Behaviors.withStash(100) { buffer =>
      Behaviors.receive { (context, message) =>
        message match {
          case Open(dbUrl) =>
            context.log.info(s"Opening database: $dbUrl")
            opened(dbUrl)

          case other =>
            buffer.stash(other)
            Behaviors.same
        }
      }
    }
  }

  def opened(dbUrl: String): Behavior[Command] = {
    Behaviors.receive { (context, message) =>
      message match {
        case Query(sql, replyTo) =>
          // Выполнение запроса
          val result = executeQuery(sql)
          replyTo ! result
          Behaviors.same

        case Close =>
          context.log.info("Closing database")
          closed

        case other =>
          context.log.warning(s"Unexpected message in opened state: $other")
          Behaviors.unhandled
      }
    }
  }

  def closed: Behavior[Command] = Behaviors.ignore
}
```

## Потоки **Akka**

### Основы **Streams**
```scala
import akka.stream._
import akka.stream.scaladsl._
import scala.concurrent.Future

implicit val system = ActorSystem("StreamSystem")
implicit val materializer = Materializer(system)
implicit val ec = system.dispatcher

// Простой Source
val source: Source[Int, NotUsed] = Source(1 to 10)

// Sink
val sink: Sink[Int, Future[Int]] = Sink.fold[Int, Int](0)(_ + _)

// Flow
val flow: Flow[Int, String, NotUsed] = Flow[Int].map(_.toString)

// Соединение компонентов
val graph = source.via(flow).toMat(sink)(Keep.right)

// Запуск
val result: Future[Int] = graph.run()

// Более короткая форма
val result2: Future[Int] = Source(1 to 10)
  .map(_.toString)
  .map(_.length)
  .runFold(0)(_ + _)

// Работа с коллекциями
val listResult = Source(List("hello", "world"))
  .map(_.toUpperCase)
  .runWith(Sink.seq)

// Infinite streams
val infiniteSource = Source.tick(0.seconds, 1.second, ()).zipWithIndex.map(_._2)

// Throttling
val throttledSource = Source(1 to 100)
  .throttle(10, 1.second) // 10 элементов в секунду
```

### **Graph DSL**
```scala
import akka.stream.scaladsl.GraphDSL
import akka.stream.scaladsl.MergePreferred
import akka.stream.scaladsl.Balance

// Пользовательский граф
val graph = GraphDSL.create() { implicit builder =>
  import GraphDSL.Implicits._

  // Определение компонентов
  val source1 = Source(1 to 10)
  val source2 = Source(11 to 20)
  val merge = builder.add(Merge[Int](2))
  val balance = builder.add(Balance[Int](2))
  val sink1 = Sink.foreach[Int](x => println(s"Sink1: $x"))
  val sink2 = Sink.foreach[Int](x => println(s"Sink2: $x"))

  // Соединение
  source1 ~> merge ~> balance
  source2 ~> merge
  balance ~> sink1
  balance ~> sink2

  ClosedShape
}

// Запуск
RunnableGraph.fromGraph(graph).run()
```

### **Reactive Streams**
```scala
import org.reactivestreams.{Publisher, Subscriber, Subscription}
import java.util.concurrent.Flow

// Publisher
val publisher: Publisher[Int] = Source(1 to 100).runWith(Sink.asPublisher(fanout = false))

// Custom Subscriber
class LoggingSubscriber[T] extends Subscriber[T] {
  private var subscription: Subscription = _

  override def onSubscribe(s: Subscription): Unit = {
    subscription = s
    subscription.request(10) // Запрос первых 10 элементов
  }

  override def onNext(t: T): Unit = {
    println(s"Received: $t")
    subscription.request(1) // Запрос следующего элемента
  }

  override def onError(t: Throwable): Unit = {
    println(s"Error: $t")
  }

  override def onComplete(): Unit = {
    println("Completed")
  }
}

// Использование
publisher.subscribe(new LoggingSubscriber[Int])
```

### **Backpressure**
```scala
// Fast producer, slow consumer
val fastSource = Source.tick(0.millis, 10.millis, 1).scan(0)(_ + _)
val slowSink = Sink.foreach[Int] { x =>
  Thread.sleep(100) // Медленная обработка
  println(s"Processed: $x")
}

// Автоматическая backpressure
fastSource.to(slowSink).run()

// Buffer для сглаживания
val buffered = fastSource.buffer(100, OverflowStrategy.dropHead).to(slowSink).run()

// Контроль backpressure
val controlled = Source(1 to 1000)
  .throttle(100, 1.second, 1, ThrottleMode.shaping)
  .to(Sink.foreach(println))
  .run()
```

### **Error Handling**
```scala
import akka.stream.RestartSettings
import scala.concurrent.duration._

// Обработка ошибок в streams
val errorProneSource = Source(1 to 10).map { x =>
  if (x == 5) throw new RuntimeException("Error at 5")
  else x
}

// Recover
val recovered = errorProneSource.recover {
  case _: RuntimeException => -1
}

// Supervision
val supervised = errorProneSource
  .withAttributes(ActorAttributes.supervisionStrategy {
    case _: RuntimeException => Supervision.Resume
    case _ => Supervision.Stop
  })

// Restart source
val restartSettings = RestartSettings(
  minBackoff = 1.second,
  maxBackoff = 30.seconds,
  randomFactor = 0.2
)

val restartableSource = RestartSource.withBackoff(restartSettings) { () =>
  Source.tick(0.seconds, 1.second, "tick")
    .map(_ => throw new RuntimeException("Simulated failure"))
}
```

## **Akka HTTP**

### **HTTP Server**
```scala
import akka.http.scaladsl.Http
import akka.http.scaladsl.model._
import akka.http.scaladsl.server.Directives._
import scala.concurrent.Future

// Определение маршрутов
val route =
  path("hello") {
    get {
      complete(HttpEntity(ContentTypes.`text/html(UTF-8)`, "<h1>Hello, Akka HTTP!</h1>"))
    }
  } ~
  path("users" / LongNumber) { id =>
    get {
      complete(s"User with id: $id")
    }
  } ~
  path("api" / "users") {
    post {
      entity(as[User]) { user =>
        complete(StatusCodes.Created, s"User created: ${user.name}")
      }
    }
  }

// Запуск сервера
val bindingFuture = Http().bindAndHandle(route, "localhost", 8080)

// Остановка
// bindingFuture.flatMap(_.unbind())
```

### **HTTP Client**
```scala
import akka.http.scaladsl.Http
import akka.http.scaladsl.model._
import akka.http.scaladsl.unmarshalling.Unmarshal

// Простой GET запрос
val responseFuture: Future[HttpResponse] = Http().singleRequest(
  HttpRequest(uri = "http://httpbin.org/get")
)

// Обработка ответа
val result = responseFuture.flatMap { response =>
  response.status match {
    case StatusCodes.OK =>
      Unmarshal(response.entity).to[String]
    case _ =>
      Future.failed(new RuntimeException(s"HTTP error: ${response.status}"))
  }
}

// POST запрос с JSON
import spray.json._
import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport._

case class CreateUser(name: String, age: Int)
implicit val userFormat = jsonFormat2(CreateUser)

val createUserRequest = HttpRequest(
  method = HttpMethods.POST,
  uri = "http://api.example.com/users",
  entity = HttpEntity(
    ContentTypes.`application/json`,
    CreateUser("John", 30).toJson.toString()
  )
)

val postResponse = Http().singleRequest(createUserRequest)
```

### **WebSocket**
```scala
import akka.http.scaladsl.model.ws.{Message, TextMessage}
import akka.stream.scaladsl.Flow

// WebSocket flow
val wsFlow: Flow[Message, Message, NotUsed] = Flow[Message].map {
  case TextMessage.Strict(text) =>
    TextMessage(s"Echo: $text")

  case TextMessage.Streamed(textStream) =>
    TextMessage.Streamed(textStream.map(chunk => s"Echo: $chunk"))

  case other =>
    throw new RuntimeException(s"Unsupported message type: $other")
}

// WebSocket маршрут
val websocketRoute =
  path("ws") {
    get {
      handleWebSocketMessages(wsFlow)
    }
  }

// Actor-based WebSocket
class WebSocketActor(outgoing: ActorRef[Message]) extends Actor {
  def receive = {
    case TextMessage.Strict(text) =>
      outgoing ! TextMessage(s"Actor echo: $text")

    case msg =>
      println(s"Received: $msg")
  }
}

val actorFlow = Flow.fromSinkAndSource(
  Sink.foreach[Message](println),
  Source.actorRef[Message](
    completionMatcher = { case akka.actor.Status.Success(_) => CompletionStrategy.immediately },
    failureMatcher = { case akka.actor.Status.Failure(cause) => cause },
    bufferSize = 16,
    overflowStrategy = OverflowStrategy.fail
  )
)
```

## Кластер **Akka**

### Настройка кластера
```scala
// application.conf
akka {
  actor {
    provider = cluster
  }
  cluster {
    seed-nodes = [
      "akka://ClusterSystem@127.0.0.1:2551",
      "akka://ClusterSystem@127.0.0.1:2552"
    ]
    roles = ["worker", "master"]
  }
  remote {
    artery {
      canonical.hostname = "127.0.0.1"
      canonical.port = 2551
    }
  }
}
```

### **Cluster Actors**
```scala
import akka.cluster.Cluster
import akka.cluster.ClusterEvent._

class ClusterListener extends Actor with ActorLogging {
  val cluster = Cluster(context.system)

  // Подписка на события кластера
  override def preStart(): Unit = {
    cluster.subscribe(self, initialStateMode = InitialStateAsEvents,
      classOf[MemberEvent], classOf[UnreachableMember])
  }

  override def postStop(): Unit = {
    cluster.unsubscribe(self)
  }

  def receive = {
    case MemberUp(member) =>
      log.info("Member is Up: {}", member.address)

    case UnreachableMember(member) =>
      log.info("Member detected as unreachable: {}", member)

    case MemberRemoved(member, previousStatus) =>
      log.info("Member is Removed: {} after {}", member.address, previousStatus)

    case _: MemberEvent => // ignore
  }
}

// Создание кластерного актора
val clusterListener = system.actorOf(Props[ClusterListener](), "clusterListener")
```

### **Cluster Sharding**
```scala
import akka.cluster.sharding.{ClusterSharding, ClusterShardingSettings}
import akka.cluster.sharding.ShardRegion._

// Определение shard entity
class Counter extends Actor with ActorLogging {
  var count = 0

  def receive = {
    case Increment =>
      count += 1
      log.info(s"Counter incremented to $count")

    case Get(replyTo) =>
      replyTo ! count
  }
}

object Counter {
  case object Increment
  case class Get(replyTo: ActorRef[Int])

  // Функция извлечения entity id
  val extractEntityId: ExtractEntityId = {
    case Increment => ("counter", Increment)
    case Get(replyTo) => ("counter", Get(replyTo))
  }

  // Функция извлечения shard id
  val extractShardId: ExtractShardId = {
    case Increment => "counter"
    case Get(_) => "counter"
  }
}

// Настройка sharding
val clusterSharding = ClusterSharding(system)

val counterRegion = clusterSharding.start(
  typeName = "Counter",
  entityProps = Props[Counter](),
  settings = ClusterShardingSettings(system),
  extractEntityId = Counter.extractEntityId,
  extractShardId = Counter.extractShardId
)

// Использование
counterRegion ! Increment
counterRegion ! Increment

implicit val timeout = Timeout(5.seconds)
val futureCount = (counterRegion ? Get(self)).mapTo[Int]
```

### **Cluster Singleton**
```scala
import akka.cluster.singleton.{ClusterSingletonManager, ClusterSingletonManagerSettings}
import akka.cluster.singleton.{ClusterSingletonProxy, ClusterSingletonProxySettings}

// Singleton актор
class MasterActor extends Actor with ActorLogging {
  def receive = {
    case WorkRequest(work) =>
      log.info("Processing work: {}", work)
      // Обработка работы
  }
}

// Настройка singleton
val singletonManager = system.actorOf(
  ClusterSingletonManager.props(
    singletonProps = Props[MasterActor](),
    terminationMessage = PoisonPill,
    settings = ClusterSingletonManagerSettings(system)
  ),
  "master"
)

// Прокси для доступа к singleton
val singletonProxy = system.actorOf(
  ClusterSingletonProxy.props(
    singletonManagerPath = "/user/master",
    settings = ClusterSingletonProxySettings(system)
  ),
  "masterProxy"
)

// Использование
singletonProxy ! WorkRequest("some work")
```

## Персистентность **Akka**

### **Persistent Actors**
```scala
import akka.persistence._

// События и команды
case class UserCreated(name: String)
case class UserNameChanged(newName: String)
case object UserDeleted

case class CreateUser(name: String)
case class ChangeName(newName: String)
case object DeleteUser
case object GetUser

case class UserState(name: String, deleted: Boolean = false) {
  def updated(event: Any): UserState = event match {
    case UserCreated(name) => copy(name = name)
    case UserNameChanged(newName) => copy(name = newName)
    case UserDeleted => copy(deleted = true)
  }
}

// Persistent актор
class UserActor(userId: String) extends PersistentActor with ActorLogging {

  override def persistenceId = s"user-$userId"

  var state = UserState("")

  def updateState(event: Any): Unit = {
    state = state.updated(event)
  }

  val receiveCommand: Receive = {
    case CreateUser(name) if state.name.isEmpty =>
      persist(UserCreated(name)) { event =>
        updateState(event)
        log.info("User created: {}", name)
        sender() ! state
      }

    case ChangeName(newName) if !state.deleted =>
      persist(UserNameChanged(newName)) { event =>
        updateState(event)
        log.info("User name changed to: {}", newName)
        sender() ! state
      }

    case DeleteUser if !state.deleted =>
      persist(UserDeleted) { event =>
        updateState(event)
        log.info("User deleted")
        sender() ! state
      }

    case GetUser =>
      sender() ! state

    case _ => sender() ! s"Invalid command for user $userId"
  }

  val receiveRecover: Receive = {
    case event: UserCreated => updateState(event)
    case event: UserNameChanged => updateState(event)
    case event: UserDeleted => updateState(event)
  }
}
```

### **Snapshots**
```scala
class UserActorWithSnapshots(userId: String) extends PersistentActor with ActorLogging {

  override def persistenceId = s"user-$userId"

  var state = UserState("")
  var eventsSinceLastSnapshot = 0

  override def receiveCommand: Receive = {
    case cmd @ CreateUser(name) =>
      persist(UserCreated(name)) { event =>
        updateState(event)
        eventsSinceLastSnapshot += 1
        maybeSnapshot()
        sender() ! state
      }

    // ... другие команды

    case "snapshot" =>
      saveSnapshot(state)
  }

  override def receiveRecover: Receive = {
    case SnapshotOffer(metadata, snapshot: UserState) =>
      log.info("Recovered from snapshot: {}", metadata)
      state = snapshot

    case event =>
      updateState(event)
      eventsSinceLastSnapshot += 1
  }

  def maybeSnapshot(): Unit = {
    if (eventsSinceLastSnapshot >= 100) {
      saveSnapshot(state)
      eventsSinceLastSnapshot = 0
    }
  }

  def updateState(event: Any): Unit = {
    state = state.updated(event)
  }
}
```

## **TestKit Akka**

### **Testing Actors**
```scala
import akka.testkit.{TestKit, TestActorRef, TestProbe}
import org.scalatest.BeforeAndAfterAll
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpecLike

class SimpleActorSpec extends TestKit(ActorSystem("test"))
  with AnyWordSpecLike with Matchers with BeforeAndAfterAll {

  override def afterAll(): Unit = {
    TestKit.shutdownActorSystem(system)
  }

  "SimpleActor" should {
    "increment counter" in {
      val actor = system.actorOf(Props[SimpleActor]())

      actor ! "increment"
      actor ! "increment"

      val probe = TestProbe()
      actor.tell("get", probe.ref)
      probe.expectMsg(2)
    }

    "reset counter" in {
      val actor = TestActorRef[SimpleActor]

      actor ! "increment"
      actor.underlyingActor.counter shouldBe 1

      actor ! SimpleActor.Reset
      actor.underlyingActor.counter shouldBe 0
    }
  }
}
```

### **Testing Streams**
```scala
import akka.stream.testkit.scaladsl.{TestSource, TestSink}
import scala.concurrent.duration._

class StreamSpec extends AnyWordSpecLike with Matchers {

  "A simple stream" should {
    "transform elements" in {
      val (source, sink) = TestSource.probe[Int]
        .via(Flow[Int].map(_ * 2))
        .toMat(TestSink.probe[Int])(Keep.both)
        .run()

      source.sendNext(1)
      source.sendNext(2)
      source.sendNext(3)
      source.sendComplete()

      sink.request(3)
      sink.expectNext(2, 4, 6)
      sink.expectComplete()
    }

    "handle errors" in {
      val (source, sink) = TestSource.probe[Int]
        .via(Flow[Int].map(x => if (x == 3) throw new RuntimeException("error") else x))
        .recover { case _: RuntimeException => -1 }
        .toMat(TestSink.probe[Int])(Keep.both)
        .run()

      source.sendNext(1)
      source.sendNext(2)
      source.sendNext(3)
      source.sendComplete()

      sink.request(3)
      sink.expectNext(1, 2, -1)
      sink.expectComplete()
    }
  }
}
```

## Интеграция с **Spring Boot**

### **Akka** в **Spring** приложении
```scala
@Configuration
class AkkaConfig {

  @Bean
  def actorSystem(): ActorSystem = {
    val system = ActorSystem("SpringAkkaSystem")
    // Настройка системы
    system
  }

  @Bean
  def userActor(actorSystem: ActorSystem): ActorRef = {
    actorSystem.actorOf(Props[UserActor](), "userActor")
  }

  @Bean
  def materializer(actorSystem: ActorSystem): Materializer = {
    Materializer(actorSystem)
  }
}

@Service
class UserService @Autowired()(
  userActor: ActorRef,
  actorSystem: ActorSystem
) {

  implicit val timeout = Timeout(5.seconds)
  implicit val ec = actorSystem.dispatcher

  def createUser(name: String): Future[User] = {
    (userActor ? CreateUser(name)).mapTo[User]
  }

  def getUser(id: Long): Future[Option[User]] = {
    (userActor ? GetUser(id)).mapTo[Option[User]]
  }
}

@RestController
@RequestMapping(Array("/api/users"))
class UserController @Autowired()(
  userService: UserService
) {

  @PostMapping
  def createUser(@RequestBody request: CreateUserRequest): CompletableFuture[User] = {
    userService.createUser(request.name).toCompletableFuture
  }

  @GetMapping(Array("/{id}"))
  def getUser(@PathVariable id: Long): CompletableFuture[ResponseEntity[User]] = {
    userService.getUser(id).map {
      case Some(user) => ResponseEntity.ok(user)
      case None => ResponseEntity.notFound().build()
    }.toCompletableFuture
  }
}
```

### **Configuration**
```scala
// application.conf
akka {
  loglevel = "INFO"
  actor {
    default-dispatcher {
      type = Dispatcher
      executor = "fork-join-executor"
      fork-join-executor {
        parallelism-min = 2
        parallelism-factor = 2.0
        parallelism-max = 10
      }
    }
  }
  remote {
    artery {
      enabled = on
      transport = tcp
      hostname = "127.0.0.1"
      port = 2552
    }
  }
  cluster {
    seed-nodes = [
      "akka://ClusterSystem@127.0.0.1:2551",
      "akka://ClusterSystem@127.0.0.1:2552"
    ]
  }
}
```

## Лучшие практики

### **Actor Design**
```scala
// Правильное проектирование акторов
object ActorDesign {

  // 1. Immutable messages
  sealed trait Message
  case class ProcessData(data: List[String]) extends Message
  case class DataProcessed(result: Map[String, Int]) extends Message

  // 2. Actor state management
  class DataProcessor extends Actor with ActorLogging {
    // Mutable state
    private var processedData = Map.empty[String, Int]

    def receive = {
      case ProcessData(data) =>
        val result = processData(data)
        processedData = processedData ++ result
        sender() ! DataProcessed(result)

      case GetStats(replyTo) =>
        replyTo ! processedData
    }

    private def processData(data: List[String]): Map[String, Int] = {
      data.groupBy(identity).mapValues(_.size)
    }
  }

  // 3. Ask pattern with timeout
  def askWithTimeout(actor: ActorRef, message: Any)
                    (implicit timeout: Timeout, ec: ExecutionContext): Future[Any] = {
    val askFuture = actor ? message
    val timeoutFuture = after(timeout.duration, using = context.system.scheduler) {
      Future.failed(new TimeoutException("Actor response timeout"))
    }
    Future.firstCompletedOf(Seq(askFuture, timeoutFuture))
  }

  // 4. Circuit breaker pattern
  class CircuitBreakerActor(target: ActorRef) extends Actor with ActorLogging {
    import context.dispatcher
    import scala.concurrent.duration._

    case object CallTimeout
    case object ResetTimeout

    var failures = 0
    val maxFailures = 3
    var state: CircuitState = Closed

    def receive = {
      case msg if state == Open =>
        sender() ! CircuitOpen

      case msg =>
        val originalSender = sender()
        val timeout = context.system.scheduler.scheduleOnce(5.seconds, self, CallTimeout)

        (target ? msg).onComplete {
          case Success(result) =>
            timeout.cancel()
            self ! ResetTimeout
            originalSender ! result

          case Failure(_) =>
            timeout.cancel()
            self ! FailureOccurred
        }
    }

    def failureHandler: Receive = {
      case FailureOccurred =>
        failures += 1
        if (failures >= maxFailures) {
          state = Open
          context.system.scheduler.scheduleOnce(30.seconds, self, ResetCircuit)
        }

      case ResetTimeout =>
        failures = 0

      case ResetCircuit =>
        state = Closed
        failures = 0
    }
  }
}
```

### **Performance Optimization**
```scala
object PerformanceOptimization {

  // 1. Message batching
  case class BatchProcess(messages: List[Any])

  class BatchProcessor extends Actor with ActorLogging {
    var buffer = List.empty[Any]
    val batchSize = 100

    def receive = {
      case msg =>
        buffer = msg :: buffer
        if (buffer.size >= batchSize) {
          processBatch()
        }
    }

    def processBatch(): Unit = {
      log.info(s"Processing batch of ${buffer.size} messages")
      // Обработка пакета
      buffer = List.empty
    }
  }

  // 2. Router pattern
  class Router extends Actor {
    val workers = (1 to 10).map(i => context.actorOf(Props[Worker](), s"worker-$i"))

    def receive = {
      case msg =>
        // Round-robin routing
        val workerIndex = (msg.hashCode % workers.size).abs
        workers(workerIndex) forward msg
    }
  }

  // 3. Backpressure with streams
  val backpressuredFlow = Flow[Int]
    .buffer(1000, OverflowStrategy.backpressure)
    .throttle(100, 1.second, 10, ThrottleMode.shaping)
    .mapAsync(4)(expensiveOperation)

  // 4. Connection pooling
  class ConnectionPool extends Actor {
    val connections = (1 to 5).map(_ => createConnection())

    def receive = {
      case Query(sql, replyTo) =>
        val connection = getAvailableConnection()
        connection ! ExecuteQuery(sql, replyTo)
    }

    def getAvailableConnection(): ActorRef = {
      // Round-robin или другой алгоритм выбора
      connections.head
    }
  }
}
```

## Устранение неполадок

### **Common Issues**
```scala
object AkkaTroubleshooting {

  // Проблема: Actor mailbox overflow
  // Решение: Использовать bounded mailbox или контролировать rate
  val boundedMailbox = system.actorOf(
    Props[Worker].withMailbox("akka.actor.mailbox.bounded-mailbox"),
    "boundedWorker"
  )

  // Проблема: Dead letters
  // Решение: Настроить dead letter logging и обработку
  class DeadLetterMonitor extends Actor with ActorLogging {
    override def preStart(): Unit = {
      context.system.eventStream.subscribe(self, classOf[DeadLetter])
    }

    def receive = {
      case deadLetter: DeadLetter =>
        log.warning(s"Dead letter: ${deadLetter.message} from ${deadLetter.sender} to ${deadLetter.recipient}")
    }
  }

  // Проблема: Memory leaks
  // Решение: Избегать хранения больших объектов в actor state
  class MemorySafeActor extends Actor {
    // Плохо: хранение больших данных в состоянии
    // var largeData: Map[String, LargeObject] = Map.empty

    // Хорошо: хранение только ключей или ссылок
    var dataKeys: Set[String] = Set.empty

    def receive = {
      case StoreData(key, data) =>
        // Сохранить в external storage
        externalStorage.put(key, data)
        dataKeys += key

      case GetData(key) =>
        externalStorage.get(key).foreach(sender() ! _)
    }
  }

  // Проблема: Blocking operations
  // Решение: Использовать dedicated dispatcher для blocking operations
  val blockingActor = system.actorOf(
    Props[BlockingActor].withDispatcher("blocking-dispatcher"),
    "blockingActor"
  )

  // Проблема: Race conditions in tests
  // Решение: Использовать TestProbe для deterministic testing
  "Actor interaction" should {
    "handle concurrent messages" in {
      val probe = TestProbe()
      val actor = system.actorOf(Props[TestActor]())

      // Отправка сообщений из разных потоков
      Future { actor.tell("msg1", probe.ref) }
      Future { actor.tell("msg2", probe.ref) }

      // Ожидание в определенном порядке
      probe.expectMsgAllOf("response1", "response2")
    }
  }
}
```

### **Monitoring and Debugging**
```scala
// Actor monitoring
class ActorMonitor extends Actor with ActorLogging {
  import context.dispatcher

  val monitoredActors = mutable.Set.empty[ActorRef]

  def receive = {
    case Monitor(actor) =>
      context.watch(actor)
      monitoredActors += actor

    case Unmonitor(actor) =>
      context.unwatch(actor)
      monitoredActors -= actor

    case Terminated(actor) =>
      log.warning(s"Actor terminated: $actor")
      monitoredActors -= actor
      // Перезапуск или другие действия
  }
}

// Metrics collection
class MetricsCollector extends Actor with ActorLogging {
  import context.dispatcher
  import scala.concurrent.duration._

  val metrics = mutable.Map.empty[String, Long]

  // Периодическая отправка метрик
  context.system.scheduler.scheduleWithFixedDelay(1.minute, 1.minute, self, CollectMetrics)

  def receive = {
    case IncrementMetric(name) =>
      metrics.update(name, metrics.getOrElse(name, 0L) + 1)

    case CollectMetrics =>
      metrics.foreach { case (name, value) =>
        // Отправка в monitoring систему
        log.info(s"Metric $name: $value")
      }
      metrics.clear()

    case GetMetrics(replyTo) =>
      replyTo ! metrics.toMap
  }
}

// JMX monitoring
class JmxMonitor extends Actor {
  import javax.management._

  val mbeanServer = ManagementFactory.getPlatformMBeanServer()
  val objectName = new ObjectName("akka:type=ActorSystem")

  def receive = {
    case RegisterMBean =>
      val actorCount = context.system.asInstanceOf[ExtendedActorSystem].provider.numberOfActors
      // Регистрация MBean
  }
}
```

## Руководство по миграции

### **From Akka Classic** to **Typed**
```scala
// Classic actor
class ClassicActor extends Actor {
  def receive = {
    case "hello" => sender() ! "world"
  }
}

// Typed equivalent
object TypedActor {
  def apply(): Behavior[String] = Behaviors.receive { (context, message) =>
    message match {
      case "hello" =>
        context.self ! "respond"
        Behaviors.same

      case "respond" =>
        // Отправка ответа не так прямолинейна как в classic
        Behaviors.same
    }
  }
}

// Migration helper
object MigrationHelper {
  def adaptClassicToTyped(classicActor: Actor): Behavior[Any] = {
    Behaviors.setup { context =>
      // Адаптация classic actor к typed API
      Behaviors.receiveMessage { message =>
        classicActor.receive.applyOrElse(message, (_: Any) => ())
        Behaviors.same
      }
    }
  }
}
```

### **From Java** to **Scala**
```scala
// Java-style actors in Scala
class JavaStyleActor extends Actor {
  private var state: Map[String, Any] = Map.empty

  override def receive: Receive = {
    case msg: java.util.Map[String, Any] @unchecked =>
      val scalaMap = msg.asScala.toMap
      state = state ++ scalaMap
      sender() ! state.asJava

    case "get" =>
      sender() ! state.asJava
  }
}

// Scala idiomatic approach
class ScalaStyleActor extends Actor {
  case class UpdateData(data: Map[String, String])
  case object GetData

  private var state = Map.empty[String, String]

  def receive = {
    case UpdateData(data) =>
      state = state ++ data
      sender() ! state

    case GetData =>
      sender() ! state
  }
}
```
## См. также
- [[scala-zio|ZIO]] — Альтернативная библиотека для функционального программирования
- [[scala-cats|Cats]] — Функциональная библиотека для **Scala**
- [Паттерны](../../patterns/README.md) — Функциональные паттерны

