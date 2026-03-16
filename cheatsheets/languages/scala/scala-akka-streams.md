---
title: "Akka Streams в Scala"
description: "Краткое руководство по Akka Streams - реактивные потоки данных для обработки данных в Scala."
tags: ["languages", "scala", "scala-akka-streams"]
difficulty: "intermediate"
prerequisites: []
next: []
updated: "2026-02-11"
---
# **Akka Streams** в **Scala**

Краткое руководство по **Akka Streams** - реактивные потоки данных для обработки данных в **Scala**.

**Последнее обновление**: 2024-01-`XX`

## Полезные ссылки

[Scala Documentation](https://docs.scala-lang.org/)
[Scala GitHub](https://github.com/scala/scala)

## Содержание

- [Введение](#введение)
- [Основы **Akka Streams**](#основы-akka-streams)
  - [Базовый пример](#базовый-пример)
- [**Source**, **Flow**, **Sink**](#source-flow-sink)
  - [**Source** - источник данных](#source-источник-данных)
  - [**Flow** - трансформация данных](#flow-трансформация-данных)
  - [**Sink** - приемник данных](#sink-приемник-данных)
- [Практические примеры](#практические-примеры)
  - [Обработка файлов](#обработка-файлов)
  - [Обработка с **backpressure**](#обработка-с-backpressure)
  - [Обработка ошибок](#обработка-ошибок)
  - [Параллельная обработка](#параллельная-обработка)
  - [**Fan-out** и **Fan-in**](#fan-out-и-fan-in)
- [**Best practices**](#best-practices)
  - [1. Используйте **throttling** для контроля скорости](#1-используйте-throttling-для-контроля-скорости)
  - [2. Используйте **buffer** для буферизации](#2-используйте-buffer-для-буферизации)
  - [3. Используйте **mapAsync** для асинхронной обработки](#3-используйте-mapasync-для-асинхронной-обработки)
  - [Обработка больших файлов с батчингом](#обработка-больших-файлов-с-батчингом)
  - [Обработка с использованием **GraphDSL**](#обработка-с-использованием-graphdsl)
  - [Обработка с разделением и объединением](#обработка-с-разделением-и-объединением)
  - [Обработка с использованием **Materializer**](#обработка-с-использованием-materializer)
  - [Обработка **HTTP** запросов](#обработка-http-запросов)
  - [Обработка с использованием **State**](#обработка-с-использованием-state)
  - [Обработка с использованием аккумулятора](#обработка-с-использованием-аккумулятора)
  - [Обработка с использованием динамических потоков](#обработка-с-использованием-динамических-потоков)
  - [Обработка с использованием тестирования](#обработка-с-использованием-тестирования)
  - [Обработка с использованием **Rate Limiting**](#обработка-с-использованием-rate-limiting)
  - [Обработка с использованием **Windowing**](#обработка-с-использованием-windowing)
  - [Обработка с использованием **Keep**](#обработка-с-использованием-keep)
- [Дополнительные техники](#дополнительные-техники)
  - [Обработка с использованием **Substreams**](#обработка-с-использованием-substreams)
  - [Обработка с использованием **KillSwitch**](#обработка-с-использованием-killswitch)
  - [Обработка с использованием **Timers**](#обработка-с-использованием-timers)
- [Troubleshooting](#troubleshooting)
- [FAQ](#faq)
- [Заключение](#заключение)
  - [Обработка с использованием **MergeHub** и **BroadcastHub**](#обработка-с-использованием-mergehub-и-broadcasthub)
  - [Обработка с использованием **Partition** для разделения потока](#обработка-с-использованием-partition-для-разделения-потока)
  - [Обработка с использованием **Balance** для балансировки нагрузки](#обработка-с-использованием-balance-для-балансировки-нагрузки)
  - [Обработка с использованием **Zip** для объединения потоков](#обработка-с-использованием-zip-для-объединения-потоков)
  - [Обработка с использованием **ZipWith** для объединения с функцией](#обработка-с-использованием-zipwith-для-объединения-с-функцией)
  - [Обработка с использованием **Concat** для последовательного объединения](#обработка-с-использованием-concat-для-последовательного-объединения)
  - [Обработка с использованием **Interleave** для чередования](#обработка-с-использованием-interleave-для-чередования)
  - [Обработка с использованием **Merge** для параллельного объединения](#обработка-с-использованием-merge-для-параллельного-объединения)
  - [Обработка с использованием **MergePreferred** для приоритетного объединения](#обработка-с-использованием-mergepreferred-для-приоритетного-объединения)
  - [Обработка с использованием **MergeSorted** для сортированного объединения](#обработка-с-использованием-mergesorted-для-сортированного-объединения)
  - [Обработка с использованием **OrElse** для альтернативных источников](#обработка-с-использованием-orelse-для-альтернативных-источников)
  - [Обработка с использованием **Conflate** для агрегации](#обработка-с-использованием-conflate-для-агрегации)
  - [Обработка с использованием **Batch** для батчинга](#обработка-с-использованием-batch-для-батчинга)
  - [Обработка с использованием **Expand** для расширения потока](#обработка-с-использованием-expand-для-расширения-потока)
- [Дополнительные ресурсы](#дополнительные-ресурсы)

## Введение

**Akka Streams** - это библиотека для обработки потоков данных в **Scala** на основе **Reactive Streams**. Она предоставляет типобезопасный **DSL** для создания пайплайнов обработки данных с автоматическим управлением **backpressure**.

**Akka Streams** особенно полезен для обработки больших объемов данных, создания реактивных приложений, обработки файлов, работы с сетью и создания микросервисов.

## Основы **Akka Streams**

### Базовый пример

```scala
import akka.stream.scaladsl.{Source, Flow, Sink}
import akka.NotUsed

// Создание источника данных
val source: Source[Int, NotUsed] = Source(1 to 100)

// Трансформация данных
val flow: Flow[Int, Int, NotUsed] = Flow[Int]
  .map(_ * 2)
  .filter(_ > 10)

// Приемник данных
val sink: Sink[Int, Future[Done]] = Sink.foreach(println)

// Соединение компонентов
val graph = source.via(flow).to(sink)
val result = graph.run()
```

## **Source**, **Flow**, **Sink**

### **Source** - источник данных

```scala
import akka.stream.scaladsl.Source

// Создание Source из коллекции
val numbers = Source(1 to 100)

// Создание Source из одного элемента
val single = Source.single(42)

// Создание Source из Future
import scala.concurrent.Future
val futureSource = Source.future(Future.successful("result"))

// Создание Source из повторяющихся элементов
val repeated = Source.repeat("hello")

// Создание Source из Iterator
val iteratorSource = Source.fromIterator(() => Iterator.continually(1))

// Создание Source из файла
import akka.stream.scaladsl.FileIO
import java.nio.file.Paths
val fileSource = FileIO.fromPath(Paths.get("file.txt"))
```

### **Flow** - трансформация данных

```scala
import akka.stream.scaladsl.Flow

// Базовые трансформации
val multiplyFlow = Flow[Int].map(_ * 2)
val filterFlow = Flow[Int].filter(_ % 2 == 0)
val takeFlow = Flow[Int].take(10)

// Композиция Flow
val composedFlow = Flow[Int]
  .map(_ * 2)
  .filter(_ > 10)
  .take(100)

// Агрегация
val sumFlow = Flow[Int].fold(0)(_ + _)
val collectFlow = Flow[Int].collect {
  case x if x > 0 => x * 2
}

// Группировка
val groupFlow = Flow[Int].grouped(10)
val slidingFlow = Flow[Int].sliding(5, 1)
```

### **Sink** - приемник данных

```scala
import akka.stream.scaladsl.Sink
import scala.concurrent.Future

// Простой Sink
val printSink = Sink.foreach[Int](println)

// Sink для сбора результатов
val collectSink: Sink[Int, Future[List[Int]]] = Sink.collection[Int, List[Int]]

// Sink для файла
import akka.stream.scaladsl.FileIO
import java.nio.file.Paths
val fileSink = FileIO.toPath(Paths.get("output.txt"))

// Sink для подсчета
val countSink: Sink[Int, Future[Int]] = Sink.fold(0)((acc, _) => acc + 1)

// Sink для первого элемента
val headSink: Sink[Int, Future[Option[Int]]] = Sink.headOption
```

## Практические примеры

### Обработка файлов

```scala
import akka.stream.scaladsl.{Source, Flow, Sink, FileIO}
import akka.util.ByteString
import akka.stream.scaladsl.Framing
import java.nio.file.Paths

// Обработка большого файла построчно
val fileSource = FileIO.fromPath(Paths.get("large-file.txt"))
  .via(Framing.delimiter(ByteString("\n"), maximumFrameLength = 1000))

val processingFlow = Flow[ByteString]
  .map(_.utf8String)
  .filter(_.nonEmpty)
  .map(_.trim.toUpperCase)
  .map(_.length)

val sink = Sink.fold[Int, Int](0)(_ + _)

val totalLength = fileSource
  .via(processingFlow)
  .runWith(sink)
```

### Обработка с **backpressure**

```scala
import akka.stream.scaladsl.{Source, Flow, Sink}
import scala.concurrent.duration._

// Обработка с контролем backpressure
val source = Source(1 to 1000000)

val slowFlow = Flow[Int]
  .throttle(1000, 1.second)  // Ограничение скорости
  .map { value =>
    Thread.sleep(10)  // Медленная операция
    value * 2
  }

val sink = Sink.foreach[Int](println)

// Автоматическая обработка backpressure
val graph = source.via(slowFlow).to(sink)
```

### Обработка ошибок

```scala
import akka.stream.scaladsl.{Source, Flow, Sink}
import akka.stream.Supervision

// Стратегия обработки ошибок
val decider: Supervision.Decider = {
  case _: ArithmeticException => Supervision.Resume
  case _: IllegalArgumentException => Supervision.Stop
  case _ => Supervision.Restart
}

val flow = Flow[Int]
  .map(100 / _)  // Может вызвать ArithmeticException
  .withAttributes(ActorAttributes.supervisionStrategy(decider))

val source = Source(List(1, 2, 0, 3, 4))
val result = source.via(flow).runWith(Sink.seq)
```

### Параллельная обработка

```scala
import akka.stream.scaladsl.{Source, Flow, Sink}

val source = Source(1 to 1000)

// Параллельная обработка с mapAsync
val parallelFlow = Flow[Int]
  .mapAsync(10) { value =>
    Future {
      // Асинхронная обработка
      processValue(value)
    }
  }

val result = source.via(parallelFlow).runWith(Sink.seq)
```

### **Fan-out** и **Fan-in**

```scala
import akka.stream.scaladsl.{Source, Flow, Sink, Broadcast, Merge}

val source = Source(1 to 100)

// Fan-out - разветвление потока
val broadcast = Broadcast[Int](3)

val flow1 = Flow[Int].map(_ * 2)
val flow2 = Flow[Int].map(_ * 3)
val flow3 = Flow[Int].map(_ * 4)

val graph = Source.fromGraph(
  GraphDSL.create() { implicit builder =>
    import GraphDSL.Implicits._
    
    val broadcast = builder.add(Broadcast[Int](3))
    
    source ~> broadcast.in
    broadcast.out(0) ~> flow1 ~> Sink.foreach(println)
    broadcast.out(1) ~> flow2 ~> Sink.foreach(println)
    broadcast.out(2) ~> flow3 ~> Sink.foreach(println)
    
    ClosedShape
  }
)
```

## **Best practices**

### 1. Используйте **throttling** для контроля скорости

```scala
val flow = Flow[Int]
  .throttle(100, 1.second)  // Ограничение до 100 элементов в секунду
  .map(process)
```

### 2. Используйте **buffer** для буферизации

```scala
val flow = Flow[Int]
  .buffer(100, OverflowStrategy.backpressure)
  .map(process)
```

### 3. Используйте **mapAsync** для асинхронной обработки

```scala
val flow = Flow[Int]
  .mapAsync(10) { value =>
    Future {
      asyncProcess(value)
    }
  }
```

### Обработка больших файлов с батчингом

```scala
import akka.stream.scaladsl.{Source, Flow, Sink, FileIO}
import akka.util.ByteString
import akka.stream.scaladsl.Framing
import java.nio.file.Paths
import scala.concurrent.Future

// Обработка большого файла батчами
val fileSource = FileIO.fromPath(Paths.get("large-file.txt"))
  .via(Framing.delimiter(ByteString("\n"), maximumFrameLength = 10000))

val batchFlow = Flow[ByteString]
  .map(_.utf8String)
  .filter(_.nonEmpty)
  .grouped(100)  // Группировка по 100 строк
  .mapAsync(5) { batch =>  // Параллельная обработка батчей
    Future {
      batch.map(processLine)
    }
  }
  .mapConcat(identity)  // Разворачивание батчей обратно в элементы

val sink = Sink.fold[List[String], String](Nil)(_ :+ _)

val result = fileSource
  .via(batchFlow)
  .runWith(sink)
```

### Обработка с использованием **GraphDSL**

```scala
import akka.stream.scaladsl.{Source, Flow, Sink, Broadcast, Merge, GraphDSL}
import akka.stream.{ClosedShape, UniformFanOutShape, UniformFanInShape}

// Создание сложного графа обработки
val complexGraph = GraphDSL.create() { implicit builder =>
  import GraphDSL.Implicits._
  
  val source = builder.add(Source(1 to 100))
  val broadcast = builder.add(Broadcast[Int](2))
  val merge = builder.add(Merge[Int](2))
  
  val flow1 = Flow[Int].map(_ * 2)
  val flow2 = Flow[Int].map(_ * 3)
  val sink = builder.add(Sink.foreach(println))
  
  source ~> broadcast.in
  broadcast.out(0) ~> flow1 ~> merge.in(0)
  broadcast.out(1) ~> flow2 ~> merge.in(1)
  merge.out ~> sink.in
  
  ClosedShape
}

// Запуск графа
Source.fromGraph(complexGraph).run()
```

### Обработка с разделением и объединением

```scala
import akka.stream.scaladsl.{Source, Flow, Sink, Partition}

// Разделение потока по условию
val partitionFlow = Flow[Int].via(
  Partition(
    outputPorts = 2,
    partitioner = value => if (value % 2 == 0) 0 else 1
  )
)

val source = Source(1 to 100)
val sinkEven = Sink.foreach[Int](value => println(s"Even: $value"))
val sinkOdd = Sink.foreach[Int](value => println(s"Odd: $value"))

// Подключение к разным приемникам
source.via(partitionFlow.outlet(0)).to(sinkEven).run()
source.via(partitionFlow.outlet(1)).to(sinkOdd).run()
```

### Обработка с использованием **Materializer**

```scala
import akka.actor.ActorSystem
import akka.stream.Materializer
import akka.stream.scaladsl.{Source, Flow, Sink}

implicit val system = ActorSystem("StreamSystem")
implicit val materializer = Materializer(system)

// Создание потоков с явным Materializer
val source = Source(1 to 1000)
val flow = Flow[Int].map(_ * 2)
val sink = Sink.foreach(println)

val graph = source.via(flow).to(sink)

// Запуск с использованием Materializer
val result = graph.run()(materializer)
```

### Обработка **HTTP** запросов

```scala
import akka.http.scaladsl.Http
import akka.http.scaladsl.model.HttpRequest
import akka.stream.scaladsl.{Source, Flow, Sink}

// Обработка HTTP запросов через потоки
val httpRequests = Source(List(
  HttpRequest(uri = "http://example.com/1"),
  HttpRequest(uri = "http://example.com/2"),
  HttpRequest(uri = "http://example.com/3")
))

val httpFlow = Http().superPool[Int]()

val processingFlow = Flow[(HttpRequest, Int)]
  .via(httpFlow)
  .mapAsync(10) { case (response, context) =>
    response.entity.toStrict(5.seconds).map(entity => (entity, context))
  }
  .map { case (entity, context) =>
    processHttpResponse(entity, context)
  }

val result = httpRequests
  .zipWithIndex
  .via(processingFlow)
  .runWith(Sink.seq)
```

### Обработка с использованием **State**

```scala
import akka.stream.scaladsl.{Source, Flow, Sink}

// Обработка с сохранением состояния
val statefulFlow = Flow[Int]
  .scan(0) { (state, value) =>
    val newState = state + value
    println(s"State: $state, Value: $value, New State: $newState")
    newState
  }

val source = Source(1 to 10)
val result = source.via(statefulFlow).runWith(Sink.seq)
```

### Обработка с использованием аккумулятора

```scala
import akka.stream.scaladsl.{Source, Flow, Sink}

// Аккумуляция значений
val accumulatorFlow = Flow[Int]
  .fold(0) { (acc, value) =>
    val newAcc = acc + value
    println(s"Accumulated: $newAcc")
    newAcc
  }

val source = Source(1 to 10)
val result = source.via(accumulatorFlow).runWith(Sink.head)
```

### Обработка с использованием динамических потоков

```scala
import akka.stream.scaladsl.{Source, Flow, Sink, MergeHub, BroadcastHub}

// Создание динамических потоков
val (mergeSink, mergeSource) = MergeHub.source[Int](perProducerBufferSize = 16)
  .toMat(BroadcastHub.sink[Int](bufferSize = 256))(Keep.both)
  .run()

// Добавление источников в runtime
Source(1 to 100).runWith(mergeSink)
Source(101 to 200).runWith(mergeSink)

// Подключение приемников
mergeSource.to(Sink.foreach(println)).run()
```

### Обработка с использованием тестирования

```scala
import akka.stream.testkit.scaladsl.{TestSource, TestSink}
import akka.stream.scaladsl.Flow

// Тестирование потоков
val flow = Flow[Int].map(_ * 2)

val (source, sink) = TestSource.probe[Int]
  .via(flow)
  .toMat(TestSink.probe[Int])(Keep.both)
  .run()

source.sendNext(1)
source.sendNext(2)
source.sendNext(3)

sink.request(3)
sink.expectNext(2, 4, 6)

source.sendComplete()
sink.expectComplete()
```

### Обработка с использованием **Rate Limiting**

```scala
import akka.stream.scaladsl.{Source, Flow, Sink}
import scala.concurrent.duration._

// Ограничение скорости обработки
val rateLimitedFlow = Flow[Int]
  .throttle(
    elements = 100,
    per = 1.second,
    maximumBurst = 200,
    mode = ThrottleMode.shaping
  )
  .map(processValue)

val source = Source(1 to 10000)
val result = source.via(rateLimitedFlow).runWith(Sink.ignore)
```

### Обработка с использованием **Windowing**

```scala
import akka.stream.scaladsl.{Source, Flow, Sink}
import scala.concurrent.duration._

// Окно обработки по времени
val windowedFlow = Flow[Int]
  .groupedWithin(100, 5.seconds)  // Группировка по времени или размеру
  .map { window =>
    println(s"Processing window of size: ${window.size}")
    window.sum
  }

val source = Source(1 to 1000)
val result = source.via(windowedFlow).runWith(Sink.seq)
```

### Обработка с использованием **Keep**

```scala
import akka.stream.scaladsl.{Source, Flow, Sink, Keep}

// Сохранние различных значений из графа
val source = Source(1 to 100)
val flow = Flow[Int].map(_ * 2)
val sink = Sink.fold[Int, Int](0)(_ + _)

// Сохранение значения от Source
val sourceMat = source.viaMat(flow)(Keep.left).toMat(sink)(Keep.left).run()

// Сохранение значения от Sink
val sinkMat = source.via(flow).toMat(sink)(Keep.right).run()

// Сохранение обоих значений
val (sourceMat, sinkMat) = source.viaMat(flow)(Keep.left).toMat(sink)(Keep.both).run()
```

## Дополнительные техники

### Обработка с использованием **Substreams**

```scala
import akka.stream.scaladsl.{Source, Flow, Sink}

// Работа с подпотоками
val substreamFlow = Flow[Int]
  .groupBy(1000, _ % 10)  // Разделение на подпотоки
  .fold(0)(_ + _)  // Обработка каждого подпотока
  .mergeSubstreams  // Объединение подпотоков

val source = Source(1 to 10000)
val result = source.via(substreamFlow).runWith(Sink.seq)
```

### Обработка с использованием **KillSwitch**

```scala
import akka.stream.KillSwitches
import akka.stream.scaladsl.{Source, Flow, Sink}

// Управление потоком через KillSwitch
val killSwitch = KillSwitches.shared("my-kill-switch")

val source = Source(1 to 10000)
val flow = Flow[Int].map(_ * 2)
val sink = Sink.foreach(println)

val graph = source
  .via(killSwitch.flow)
  .via(flow)
  .to(sink)
  .run()

// Остановка потока
Thread.sleep(5000)
killSwitch.shutdown()
```

### Обработка с использованием **Timers**

```scala
import akka.stream.scaladsl.{Source, Flow, Sink}
import scala.concurrent.duration._

// Использование таймеров в потоках
val timerFlow = Flow[Int]
  .statefulMapConcat { () =>
    var lastEmit = System.currentTimeMillis()
    
    { value =>
      val now = System.currentTimeMillis()
      if (now - lastEmit > 1000) {
        lastEmit = now
        List(value)
      } else {
        Nil
      }
    }
  }

val source = Source(1 to 100)
val result = source.via(timerFlow).runWith(Sink.seq)
```


## Решение проблем

Типичные проблемы и решения см. в официальной документации (блок «Полезные ссылки» в начале документа).

## Частые вопросы

Ответы на частые вопросы по теме см. в разделах «Введение» и «Лучшие практики» в документе.

## Заключение

**Akka Streams** - это мощная библиотека для обработки потоков данных в **Scala**. Понимание **Source**, **Flow**, **Sink**, обработки файлов, управления **backpressure**, обработки ошибок, параллельной обработки, **Fan-out**/**Fan-in** и практических применений позволяет создавать эффективные, масштабируемые реактивные приложения.

Использование **Akka Streams** для обработки больших файлов, управления **backpressure**, параллельной обработки, создания реактивных пайплайнов и практических применений критично для создания высокопроизводительных, отказоустойчивых приложений.

**Akka Streams** предоставляет мощные инструменты для работы с потоками данных, включая обработку файлов, **HTTP** запросов, динамических потоков, тестирования, **rate limiting**, **windowing**, **substreams**, **kill switches** и таймеров. Понимание этих техник позволяет создавать сложные, масштабируемые системы обработки данных.

### Обработка с использованием **MergeHub** и **BroadcastHub**

```scala
import akka.stream.scaladsl.{Source, Sink, MergeHub, BroadcastHub}
import akka.stream.Keep

// Создание динамических потоков
val (mergeSink, mergeSource) = MergeHub.source[Int](perProducerBufferSize = 16)
  .toMat(BroadcastHub.sink[Int](bufferSize = 256))(Keep.both)
  .run()

// Добавление источников в runtime
Source(1 to 100).runWith(mergeSink)
Source(101 to 200).runWith(mergeSink)

// Подключение приемников
mergeSource.to(Sink.foreach(println)).run()
mergeSource.to(Sink.foreach(x => println(s"Doubled: ${x * 2}"))).run()
```

### Обработка с использованием **Partition** для разделения потока

```scala
import akka.stream.scaladsl.{Source, Flow, Sink, Partition}

// Разделение потока по условию
val partitionFlow = Partition[Int](
  outputPorts = 2,
  partitioner = value => if (value % 2 == 0) 0 else 1
)

val source = Source(1 to 100)
val sinkEven = Sink.foreach[Int](value => println(s"Even: $value"))
val sinkOdd = Sink.foreach[Int](value => println(s"Odd: $value"))

// Подключение к разным приемникам
source.via(partitionFlow).out(0).to(sinkEven).run()
source.via(partitionFlow).out(1).to(sinkOdd).run()
```

### Обработка с использованием **Balance** для балансировки нагрузки

```scala
import akka.stream.scaladsl.{Source, Flow, Sink, Balance}

// Балансировка нагрузки между несколькими обработчиками
val balanceFlow = Balance[Int](outputPorts = 3)

val source = Source(1 to 1000)
val sink1 = Sink.foreach[Int](value => println(s"Sink 1: $value"))
val sink2 = Sink.foreach[Int](value => println(s"Sink 2: $value"))
val sink3 = Sink.foreach[Int](value => println(s"Sink 3: $value"))

// Подключение к разным приемникам
source.via(balanceFlow).out(0).to(sink1).run()
source.via(balanceFlow).out(1).to(sink2).run()
source.via(balanceFlow).out(2).to(sink3).run()
```

### Обработка с использованием **Zip** для объединения потоков

```scala
import akka.stream.scaladsl.{Source, Sink, Zip}

// Объединение двух потоков
val source1 = Source(1 to 10)
val source2 = Source(11 to 20)

val zipped = source1.zip(source2)
val result = zipped.runWith(Sink.seq)
// Seq((1, 11), (2, 12), ..., (10, 20))
```

### Обработка с использованием **ZipWith** для объединения с функцией

```scala
import akka.stream.scaladsl.{Source, Sink, ZipWith}

// Объединение потоков с функцией
val source1 = Source(1 to 10)
val source2 = Source(11 to 20)

val zipped = source1.zipWith(source2)(_ + _)
val result = zipped.runWith(Sink.seq)
// Seq(12, 14, 16, ..., 30)
```

### Обработка с использованием **Concat** для последовательного объединения

```scala
import akka.stream.scaladsl.{Source, Sink, Concat}

// Последовательное объединение потоков
val source1 = Source(1 to 5)
val source2 = Source(6 to 10)

val concatenated = source1.concat(source2)
val result = concatenated.runWith(Sink.seq)
// Seq(1, 2, 3, 4, 5, 6, 7, 8, 9, 10)
```

### Обработка с использованием **Interleave** для чередования

```scala
import akka.stream.scaladsl.{Source, Sink, Interleave}

// Чередование элементов из двух потоков
val source1 = Source(1 to 5)
val source2 = Source(6 to 10)

val interleaved = source1.interleave(source2, segmentSize = 1)
val result = interleaved.runWith(Sink.seq)
// Seq(1, 6, 2, 7, 3, 8, 4, 9, 5, 10)
```

### Обработка с использованием **Merge** для параллельного объединения

```scala
import akka.stream.scaladsl.{Source, Sink, Merge}

// Параллельное объединение потоков
val source1 = Source(1 to 5)
val source2 = Source(6 to 10)

val merged = source1.merge(source2)
val result = merged.runWith(Sink.seq)
// Порядок не гарантирован
```

### Обработка с использованием **MergePreferred** для приоритетного объединения

```scala
import akka.stream.scaladsl.{Source, Sink, MergePreferred}

// Объединение с приоритетом
val preferredSource = Source(1 to 5)
val secondarySource = Source(6 to 10)

val mergePreferred = MergePreferred[Int](secondaryInputPorts = 1)
val merged = preferredSource
  .via(mergePreferred.preferred)
  .merge(secondarySource.via(mergePreferred.in(0)))
  .runWith(Sink.seq)
```

### Обработка с использованием **MergeSorted** для сортированного объединения

```scala
import akka.stream.scaladsl.{Source, Sink, MergeSorted}

// Сортированное объединение потоков
val source1 = Source(List(1, 3, 5, 7, 9))
val source2 = Source(List(2, 4, 6, 8, 10))

val merged = source1.mergeSorted(source2)
val result = merged.runWith(Sink.seq)
// Seq(1, 2, 3, 4, 5, 6, 7, 8, 9, 10)
```

### Обработка с использованием **OrElse** для альтернативных источников

```scala
import akka.stream.scaladsl.{Source, Sink}

// Альтернативный источник, если первый пуст
val primarySource = Source.empty[Int]
val fallbackSource = Source(1 to 10)

val result = primarySource.orElse(fallbackSource).runWith(Sink.seq)
// Seq(1, 2, 3, 4, 5, 6, 7, 8, 9, 10)
```

### Обработка с использованием **Conflate** для агрегации

```scala
import akka.stream.scaladsl.{Source, Flow, Sink}

// Агрегация элементов при backpressure
val source = Source(1 to 1000)
val conflateFlow = Flow[Int].conflate((acc, elem) => acc + elem)

val result = source.via(conflateFlow).runWith(Sink.seq)
```

### Обработка с использованием **Batch** для батчинга

```scala
import akka.stream.scaladsl.{Source, Flow, Sink}

// Батчинг элементов
val source = Source(1 to 100)
val batchFlow = Flow[Int].batch(max = 10, seed = List.empty[Int])(_ :+ _)

val result = source.via(batchFlow).runWith(Sink.seq)
// Seq(List(1, 2, ..., 10), List(11, 12, ..., 20), ...)
```

### Обработка с использованием **Expand** для расширения потока

```scala
import akka.stream.scaladsl.{Source, Flow, Sink}

// Расширение потока при отсутствии backpressure
val source = Source(1 to 10)
val expandFlow = Flow[Int].expand(Iterator.continually(_))

val result = source.via(expandFlow).take(100).runWith(Sink.seq)
```

## Дополнительные ресурсы

- [Akka Streams Documentation](https://doc.akka.io/docs/akka/current/stream/index.html)
- [Reactive Streams](https://www.reactive-streams.org/)
- [Akka Streams Cookbook](https://doc.akka.io/docs/akka/current/stream/stream-cookbook.html)

