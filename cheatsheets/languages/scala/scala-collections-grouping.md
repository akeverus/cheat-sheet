---
title: "Scala Collections — Grouping and Aggregation"
description: "Полное руководство по группировке и агрегации коллекций в Scala: groupBy, агрегатные функции, продвинутые операции"
tags:
  - scala
  - collections
  - grouping
  - aggregation
  - functional-programming
difficulty: "intermediate"
prerequisites: ["scala/scala-collections.md"]
next:
  - scala-implicit
  - scala-http4s
  - scala-fp-advanced
updated: "2026-04-20"
related: ["scala/scala-collections.md", "scala/scala-collections-operations.md"]
---

# Scala Collections — Grouping and Aggregation

Кратко: руководство по группировке и агрегации коллекций в **Scala**: **groupBy**, агрегатные функции и продвинутые операции.

## Полезные ссылки

### Официальная документация
- [Scala Collections Documentation](https://docs.scala-lang.org/overviews/collections-2.13/overview.html)

### См. также
- [[scala-collections|Обзор коллекций]]
- [[scala-collections-operations|Операции над коллекциями]]

- [[scala-collections-list|Scala Collections — List]]
- [[scala-collections-vector|Scala Collections — Vector]]
- [[scala-collections-set|Scala Collections — Set]]
## Содержание

- [Группировка](#группировка)
  - [groupBy](#groupby)
  - [Группировка с трансформацией](#группировка-с-трансформацией)
- [Агрегация](#агрегация)
- [Подсчет](#подсчет)
- [Суммирование](#суммирование)
- [Минимум и максимум](#минимум-и-максимум)
- [Продвинутые операции](#продвинутые-операции)
  - [Многоуровневая группировка](#многоуровневая-группировка)
  - [Агрегация временных рядов](#агрегация-временных-рядов)
  - [groupMapReduce](#groupmapreduce)
  - [Группировка с множественными ключами](#группировка-с-множественными-ключами)
  - [Группировка с сортировкой](#группировка-с-сортировкой)
  - [Агрегация с различными функциями](#агрегация-с-различными-функциями)
  - [Практический пример: Анализ продаж](#практический-пример-анализ-продаж)
  - [Практический пример: Группировка пользователей](#практический-пример-группировка-пользователей)
  - [Практический пример: Агрегация временных рядов](#практический-пример-агрегация-временных-рядов)
  - [Группировка с фильтрацией](#группировка-с-фильтрацией)
  - [Группировка с трансформацией ключей](#группировка-с-трансформацией-ключей)
- [Лучшие практики](#лучшие-практики)
  - [Эффективная группировка](#эффективная-группировка)
  - [Избегание создания промежуточных коллекций](#избегание-создания-промежуточных-коллекций)
  - [Использование подходящих типов для ключей](#использование-подходящих-типов-для-ключей)
- [Продвинутые техники группировки и агрегации](#продвинутые-техники-группировки-и-агрегации)
  - [Многоуровневая группировка](#многоуровневая-группировка-1)
  - [Агрегация с условиями](#агрегация-с-условиями)
  - [Временные ряды и агрегация](#временные-ряды-и-агрегация)
  - [Практические примеры: Анализ данных продаж](#практические-примеры-анализ-данных-продаж)
  - [Практические примеры: Группировка пользователей](#практические-примеры-группировка-пользователей)
- [Заключение (расширенное)](#заключение-расширенное)
  - [Практические примеры: Группировка с агрегацией](#практические-примеры-группировка-с-агрегацией)
  - [Практические примеры: Многоуровневая группировка](#практические-примеры-многоуровневая-группировка)
  - [Использование с различными техниками для группировки](#использование-с-различными-техниками-для-группировки)
  - [Использование с различными техниками для многоуровневой группировки](#использование-с-различными-техниками-для-многоуровневой-группировки)
- [Дополнительные ресурсы](#дополнительные-ресурсы)

## Группировка

Группировка позволяет разделить коллекцию на группы на основе некоторого критерия. Это одна из самых мощных операций при работе с данными, особенно при анализе и агрегации. Группировка является основой для многих операций анализа данных, таких как подсчет, суммирование, вычисление средних значений и других статистических операций по группам.

Группировка особенно полезна для работы с реляционными данными, где нужно анализировать данные по категориям, временным периодам, регионам или другим критериям. Операция **groupBy** создает **Map**, где ключи — это значения критерия группировки, а значения — коллекции элементов, соответствующих этому критерию.

### groupBy

Функция `groupBy` группирует элементы коллекции по ключу, вычисляемому функцией для каждого элемента. Результатом является **Map**, где каждый ключ соответствует уникальному значению критерия группировки, а значение — коллекции всех элементов, соответствующих этому критерию. Это позволяет эффективно организовывать данные для последующего анализа и агрегации.

**Функция `groupBy` группирует элементы коллекции по ключу:**

```scala
val words = List("apple", "banana", "apricot", "blueberry")

// Группировка по первому символу
// _.head извлекает первый символ каждой строки
// Элементы с одинаковым первым символом группируются вместе
val grouped = words.groupBy(_.head)
// Map('a' -> List("apple", "apricot"), 'b' -> List("banana", "blueberry"))
// Все слова, начинающиеся с 'a', попадают в одну группу
// Все слова, начинающиеся с 'b', попадают в другую группу

// Группировка по длине
// _.length вычисляет длину каждой строки
// Элементы с одинаковой длиной группируются вместе
val byLength = words.groupBy(_.length)
// Map(5 -> List("apple"), 6 -> List("banana", "apricot"), 9 -> List("blueberry"))
// Слова длиной 5, 6 и 9 символов группируются отдельно
```

`groupBy` проходит по всем элементам коллекции, вычисляет ключ для каждого элемента и помещает элемент в соответствующую группу. Операция выполняется за один проход по коллекции, что делает ее эффективной даже для больших коллекций. Результирующая **Map** содержит все группы, даже если группа состоит из одного элемента.

### Группировка с трансформацией

```scala
val words = List("apple", "banana", "apricot", "blueberry")

// Группировка с трансформацией значений
val grouped = words.groupBy(_.head).mapValues(_.map(_.toUpperCase))
// Map('a' -> List("APPLE", "APRICOT"), 'b' -> List("BANANA", "BLUEBERRY"))
```

## Агрегация

Агрегация позволяет вычислять статистику по группам, комбинируя группировку с операциями агрегации. После группировки данных можно применить различные агрегатные функции к каждой группе, такие как суммирование, подсчет, вычисление среднего, минимума или максимума. Это позволяет анализировать данные на уровне групп, а не отдельных элементов.

Агрегация особенно полезна для бизнес-аналитики, где нужно вычислять метрики по категориям, временным периодам или другим измерениям. Комбинация **groupBy** и **mapValues** позволяет выразительно вычислять агрегаты для каждой группы.

**Агрегация позволяет вычислять статистику по группам:**

```scala
case class Sale(product: String, category: String, amount: Double)

val sales = List(
  Sale("Product1", "Electronics", 100.0),
  Sale("Product2", "Electronics", 200.0),
  Sale("Product3", "Clothing", 150.0)
)

// Группировка и агрегация
// groupBy группирует продажи по категориям
// mapValues применяет функцию агрегации к каждой группе
// sales.map(_.amount).sum вычисляет сумму продаж в каждой категории
val byCategory = sales.groupBy(_.category).mapValues { sales =>
  sales.map(_.amount).sum
}
// Map("Electronics" -> 300.0, "Clothing" -> 150.0)
// Electronics: 100.0 + 200.0 = 300.0
// Clothing: 150.0
```

Комбинация **groupBy** и **mapValues** позволяет выразительно вычислять агрегаты для каждой группы. Это особенно полезно для анализа данных, где нужно получить статистику по различным измерениям. Агрегация может включать не только суммирование, но и другие операции, такие как подсчет, вычисление среднего, минимума, максимума или более сложные вычисления.

## Подсчет

Подсчет элементов в группах является одной из самых распространенных операций агрегации. После группировки данных часто нужно узнать количество элементов в каждой группе. Это особенно полезно для анализа данных, где нужно понять распределение элементов по различным категориям.

Подсчет выполняется путем применения функции `length` к каждой группе после группировки. Это позволяет быстро получить статистику о количестве элементов в каждой категории без необходимости дополнительных проходов по данным.

```scala
val words = List("apple", "banana", "apricot", "blueberry")

// Подсчет элементов в группах
// groupBy группирует слова по первому символу
// mapValues(_.length) подсчитывает количество элементов в каждой группе
val counts = words.groupBy(_.head).mapValues(_.length)
// Map('a' -> 2, 'b' -> 2)
// Группа 'a' содержит 2 слова: "apple", "apricot"
// Группа 'b' содержит 2 слова: "banana", "blueberry"
```

Подсчет элементов в группах является основой для многих аналитических операций, таких как подсчет пользователей по регионам, подсчет заказов по статусам или подсчет событий по типам.

## Суммирование

Суммирование значений в группах является одной из самых важных операций агрегации при работе с числовыми данными. После группировки данных по определенному критерию часто нужно вычислить сумму значений в каждой группе. Это особенно полезно для финансовых данных, где нужно суммировать суммы заказов, продаж или транзакций по различным категориям.

Суммирование выполняется путем извлечения числовых значений из элементов группы и применения операции суммирования. Это позволяет быстро получить агрегированные значения для каждой категории.

```scala
case class Order(customerId: Long, amount: Double)

val orders = List(
  Order(1, 100.0),
  Order(1, 200.0),
  Order(2, 150.0)
)

// Сумма по клиенту
// groupBy группирует заказы по идентификатору клиента
// mapValues применяет функцию суммирования к каждой группе
// _.map(_.amount).sum извлекает суммы заказов и суммирует их
val totals = orders.groupBy(_.customerId).mapValues(_.map(_.amount).sum)
// Map(1 -> 300.0, 2 -> 150.0)
// Клиент 1: 100.0 + 200.0 = 300.0
// Клиент 2: 150.0
```

Суммирование значений в группах является основой для многих бизнес-аналитических операций, таких как вычисление общей выручки по категориям, суммирование расходов по проектам или вычисление общей стоимости заказов по клиентам.

## Минимум и максимум

```scala
case class Sale(category: String, amount: Double)

val sales = List(
  Sale("Electronics", 100.0),
  Sale("Electronics", 200.0),
  Sale("Clothing", 150.0)
)

// Максимальная продажа по категории
val maxByCategory = sales.groupBy(_.category).mapValues(_.map(_.amount).max)
// Map("Electronics" -> 200.0, "Clothing" -> 150.0)
```

## Продвинутые операции

### Многоуровневая группировка

```scala
case class Sale(product: String, category: String, region: String, amount: Double)

val sales = List(
  Sale("Product1", "Electronics", "North", 100.0),
  Sale("Product2", "Electronics", "South", 200.0),
  Sale("Product3", "Clothing", "North", 150.0)
)

// Группировка по категории и региону
val grouped = sales.groupBy(s => (s.category, s.region))
  .mapValues(_.map(_.amount).sum)
```

### Агрегация временных рядов

```scala
case class Event(timestamp: Long, value: Double)

def aggregateByTime(events: List[Event], interval: Long): Map[Long, Double] = {
  events.groupBy { event =>
    (event.timestamp / interval) * interval
  }.mapValues(_.map(_.value).sum)
}
```

### groupMapReduce

**`groupMapReduce` объединяет группировку, трансформацию и агрегацию:**

```scala
case class Sale(product: String, category: String, amount: Double)

val sales = List(
  Sale("Product1", "Electronics", 100.0),
  Sale("Product2", "Electronics", 200.0),
  Sale("Product3", "Clothing", 150.0)
)

// Группировка, трансформация и агрегация в одной операции
val totals = sales.groupMapReduce(_.category)(_.amount)(_ + _)
// Map("Electronics" -> 300.0, "Clothing" -> 150.0)

// Эквивалентно
val totals2 = sales.groupBy(_.category).mapValues(_.map(_.amount).sum)
```

`groupMapReduce` более эффективен, так как выполняет все операции за один проход.

### Группировка с множественными ключами

```scala
case class Sale(product: String, category: String, region: String, amount: Double)

val sales = List(
  Sale("Product1", "Electronics", "North", 100.0),
  Sale("Product2", "Electronics", "South", 200.0),
  Sale("Product3", "Clothing", "North", 150.0)
)

// Группировка по составному ключу
val byCategoryAndRegion = sales.groupBy(s => (s.category, s.region))
  .mapValues(_.map(_.amount).sum)
// Map(("Electronics","North") -> 100.0, ("Electronics","South") -> 200.0, ...)
```

### Группировка с сортировкой

```scala
val words = List("apple", "banana", "apricot", "blueberry", "avocado")

// Группировка с сортировкой значений
val grouped = words.groupBy(_.head).mapValues(_.sorted)
// Map('a' -> List("apple", "apricot", "avocado"), 'b' -> List("banana", "blueberry"))

// Группировка с сортировкой по количеству
val byCount = words.groupBy(_.head).toList.sortBy(_._2.length)
// List(('b', List("banana", "blueberry")), ('a', List("apple", "apricot", "avocado")))
```

### Агрегация с различными функциями

```scala
case class Sale(category: String, amount: Double)

val sales = List(
  Sale("Electronics", 100.0),
  Sale("Electronics", 200.0),
  Sale("Clothing", 150.0),
  Sale("Clothing", 50.0)
)

// Среднее значение
val averages = sales.groupBy(_.category).mapValues { sales =>
  sales.map(_.amount).sum / sales.length
}
// Map("Electronics" -> 150.0, "Clothing" -> 100.0)

// Минимум и максимум
val minMax = sales.groupBy(_.category).mapValues { sales =>
  val amounts = sales.map(_.amount)
  (amounts.min, amounts.max)
}
// Map("Electronics" -> (100.0, 200.0), "Clothing" -> (50.0, 150.0))

// Медиана
def median(numbers: List[Double]): Double = {
  val sorted = numbers.sorted
  val n = sorted.length
  if (n % 2 == 0) (sorted(n / 2 - 1) + sorted(n / 2)) / 2
  else sorted(n / 2)
}

val medians = sales.groupBy(_.category).mapValues { sales =>
  median(sales.map(_.amount))
}
```

### Практический пример: Анализ продаж

```scala
case class Sale(
  product: String,
  category: String,
  region: String,
  amount: Double,
  date: java.time.LocalDate
)

val sales = List(
  Sale("Product1", "Electronics", "North", 100.0, java.time.LocalDate.of(2023, 1, 15)),
  Sale("Product2", "Electronics", "South", 200.0, java.time.LocalDate.of(2023, 1, 20)),
  Sale("Product3", "Clothing", "North", 150.0, java.time.LocalDate.of(2023, 1, 25))
)

// Анализ по категориям
val byCategory = sales.groupBy(_.category).mapValues { sales =>
  Map(
    "total" -> sales.map(_.amount).sum,
    "count" -> sales.length,
    "average" -> sales.map(_.amount).sum / sales.length,
    "max" -> sales.map(_.amount).max,
    "min" -> sales.map(_.amount).min
  )
}

// Анализ по регионам
val byRegion = sales.groupBy(_.region).mapValues(_.map(_.amount).sum)

// Анализ по месяцам
val byMonth = sales.groupBy(_.date.getMonth).mapValues(_.map(_.amount).sum)
```

### Практический пример: Группировка пользователей

```scala
case class User(id: Long, name: String, age: Int, city: String)

val users = List(
  User(1, "Alice", 30, "New York"),
  User(2, "Bob", 25, "New York"),
  User(3, "Charlie", 35, "London"),
  User(4, "David", 28, "London")
)

// Группировка по городу
val byCity = users.groupBy(_.city)
// Map("New York" -> List(User(1,...), User(2,...)), "London" -> List(...))

// Группировка по возрастным группам
val byAgeGroup = users.groupBy { user =>
  if (user.age < 30) "Young"
  else if (user.age < 40) "Middle"
  else "Senior"
}

// Группировка с подсчетом
val countByCity = users.groupBy(_.city).mapValues(_.length)
// Map("New York" -> 2, "London" -> 2)
```

### Практический пример: Агрегация временных рядов

```scala
case class Event(timestamp: Long, value: Double, category: String)

val events = List(
  Event(1000, 10.0, "A"),
  Event(2000, 20.0, "A"),
  Event(3000, 15.0, "B"),
  Event(4000, 25.0, "B")
)

// Агрегация по временным интервалам
def aggregateByInterval(events: List[Event], interval: Long): Map[Long, Double] = {
  events.groupBy { event =>
    (event.timestamp / interval) * interval
  }.mapValues(_.map(_.value).sum)
}

val hourly = aggregateByInterval(events, 3600000)  // по часам

// Агрегация по категориям и времени
val byCategoryAndTime = events.groupBy { event =>
  (event.category, (event.timestamp / 1000) * 1000)
}.mapValues(_.map(_.value).sum)
```

### Группировка с фильтрацией

```scala
case class Order(customerId: Long, amount: Double, status: String)

val orders = List(
  Order(1, 100.0, "completed"),
  Order(1, 200.0, "pending"),
  Order(2, 150.0, "completed"),
  Order(2, 50.0, "cancelled")
)

// Группировка только завершенных заказов
val completedByCustomer = orders
  .filter(_.status == "completed")
  .groupBy(_.customerId)
  .mapValues(_.map(_.amount).sum)
// Map(1 -> 100.0, 2 -> 150.0)
```

### Группировка с трансформацией ключей

```scala
val words = List("apple", "banana", "apricot", "blueberry")

// Группировка по длине слова
val byLength = words.groupBy(_.length)
// Map(5 -> List("apple"), 6 -> List("banana", "apricot"), 9 -> List("blueberry"))

// Группировка по первой букве в верхнем регистре
val byFirstLetter = words.groupBy(_.head.toUpper)
// Map('A' -> List("apple", "apricot"), 'B' -> List("banana", "blueberry"))
```

## Лучшие практики

### Эффективная группировка

```scala
// Хорошо - группировка с последующей агрегацией
val result = sales.groupBy(_.category).mapValues(_.map(_.amount).sum)

// Еще лучше - использование groupMapReduce
val result2 = sales.groupMapReduce(_.category)(_.amount)(_ + _)

// Плохо - множественные проходы
val grouped = sales.groupBy(_.category)
val aggregated = grouped.mapValues { sales =>
  sales.map(_.amount).sum
}
```

### Избегание создания промежуточных коллекций

```scala
// Хорошо - использование groupMapReduce
val totals = sales.groupMapReduce(_.category)(_.amount)(_ + _)

// Плохо - создание промежуточных коллекций
val grouped = sales.groupBy(_.category)
val mapped = grouped.mapValues(_.map(_.amount))
val reduced = mapped.mapValues(_.sum)
```

### Использование подходящих типов для ключей

```scala
// Хорошо - использование case class для составных ключей
case class Key(category: String, region: String)
val grouped = sales.groupBy(s => Key(s.category, s.region))

// Плохо - использование кортежей для сложных ключей
val grouped2 = sales.groupBy(s => (s.category, s.region, s.date))  // сложно читать
```

## Продвинутые техники группировки и агрегации

### Многоуровневая группировка

Многоуровневая группировка позволяет создавать сложные структуры данных.

```scala
// Группировка по нескольким уровням
val sales = List(
  Sale("Electronics", "US", 100.0),
  Sale("Electronics", "US", 200.0),
  Sale("Electronics", "EU", 150.0),
  Sale("Clothing", "US", 50.0)
)

// Группировка по категории, затем по региону
val multiLevel = sales
  .groupBy(_.category)
  .mapValues(_.groupBy(_.region))

// Результат: Map("Electronics" -> Map("US" -> List(...), "EU" -> List(...)))
```

### Агрегация с условиями

Агрегация с условиями позволяет выполнять условные вычисления.

```scala
// Условная агрегация
val sales = List(
  Sale("Electronics", 100.0),
  Sale("Electronics", 200.0),
  Sale("Clothing", 50.0)
)

// Сумма только для Electronics
val electronicsTotal = sales
  .filter(_.category == "Electronics")
  .map(_.amount)
  .sum

// Агрегация с условием в одной операции
val conditionalTotal = sales
  .groupBy(_.category)
  .mapValues(_.filter(_.amount > 100).map(_.amount).sum)
```

### Временные ряды и агрегация

Временные ряды требуют специальных техник агрегации.

```scala
import java.time.LocalDate

case class TimeSeriesData(date: LocalDate, value: Double)

val data = List(
  TimeSeriesData(LocalDate.of(2023, 1, 1), 100.0),
  TimeSeriesData(LocalDate.of(2023, 1, 2), 150.0),
  TimeSeriesData(LocalDate.of(2023, 1, 3), 200.0)
)

// Группировка по месяцам
val monthly = data
  .groupBy(_.date.getMonth)
  .mapValues(_.map(_.value).sum)

// Скользящее среднее
val windowSize = 3
val movingAverage = data
  .sliding(windowSize)
  .map(window => window.map(_.value).sum / windowSize)
  .toList
```

### Практические примеры: Анализ данных продаж

```scala
case class Sale(
  product: String,
  category: String,
  amount: Double,
  date: java.time.LocalDate,
  region: String
)

class SalesAnalyzer(sales: List[Sale]) {
  // Группировка по категориям с суммарным объемом продаж
  def salesByCategory: Map[String, Double] = {
    sales.groupMapReduce(_.category)(_.amount)(_ + _)
  }

  // Группировка по регионам с топ-3 продуктами
  def topProductsByRegion(limit: Int = 3): Map[String, List[(String, Double)]] = {
    sales.groupBy(_.region)
      .view
      .mapValues(regionSales =>
        regionSales.groupMapReduce(_.product)(_.amount)(_ + _)
          .toList
          .sortBy(-_._2)
          .take(limit)
      )
      .toMap
  }

  // Продажи по месяцам
  def salesByMonth: Map[java.time.YearMonth, Double] = {
    sales.groupMapReduce(_.date.toYearMonth)(_.amount)(_ + _)
  }

  // Статистика по категориям
  def categoryStatistics: Map[String, CategoryStats] = {
    sales.groupBy(_.category)
      .view
      .mapValues(categorySales => {
        val amounts = categorySales.map(_.amount)
        CategoryStats(
          totalSales = amounts.sum,
          averageSale = amounts.sum / amounts.size,
          count = amounts.size,
          maxSale = amounts.max,
          minSale = amounts.min
        )
      })
      .toMap
  }
}

case class CategoryStats(
  totalSales: Double,
  averageSale: Double,
  count: Int,
  maxSale: Double,
  minSale: Double
)

implicit class LocalDateOps(date: java.time.LocalDate) {
  def toYearMonth: java.time.YearMonth =
    java.time.YearMonth.from(date)
}
```

### Практические примеры: Группировка пользователей

```scala
case class User(
  id: Long,
  name: String,
  age: Int,
  city: String,
  subscriptionType: String,
  registrationDate: java.time.LocalDate
)

class UserAnalyzer(users: List[User]) {
  // Группировка по городам с количеством пользователей
  def usersByCity: Map[String, Int] = {
    users.groupMapReduce(_.city)(_ => 1)(_ + _)
  }

  // Группировка по возрастным группам
  def usersByAgeGroup: Map[String, List[User]] = {
    users.groupBy(user =>
      user.age match {
        case age if age < 18 => "Minor"
        case age if age < 26 => "Young Adult"
        case age if age < 41 => "Adult"
        case age if age < 61 => "Middle Age"
        case _ => "Senior"
      }
    )
  }

  // Группировка по типу подписки с средним возрастом
  def averageAgeBySubscription: Map[String, Double] = {
    users.groupBy(_.subscriptionType)
      .view
      .mapValues(users => users.map(_.age).sum.toDouble / users.size)
      .toMap
  }

  // Новые пользователи по месяцам
  def newUsersByMonth: Map[java.time.YearMonth, Int] = {
    users.groupMapReduce(_.registrationDate.toYearMonth)(_ => 1)(_ + _)
  }
}
```

## Заключение (расширенное)

Группировка и агрегация коллекций являются важными операциями для анализа и обработки данных. Понимание различных техник группировки (groupBy, groupMapReduce), агрегации (sum, average, min, max, median), работы с составными ключами, временными рядами, многоуровневой группировки, агрегации с условиями, анализа продаж, группировки пользователей и практических применений позволяет эффективно обрабатывать большие объемы данных и извлекать полезную информацию. Использование **groupMapReduce** для эффективной группировки и агрегации, многоуровневой группировки для сложных структур данных, условной агрегации для фильтрованных вычислений, специальных техник для временных рядов, анализа продаж и пользователей критично для производительности и функциональности. Группировка и агрегация особенно важны для создания приложений, которые должны анализировать данные, создавать отчеты, обрабатывать временные ряды, и извлекать полезную информацию из больших объемов данных.

### Практические примеры: Группировка с агрегацией

```scala
// Группировка с множественными агрегациями
case class Sale(product: String, category: String, amount: Double, quantity: Int)

val sales = List(
  Sale("Product A", "Electronics", 100.0, 2),
  Sale("Product B", "Electronics", 150.0, 3),
  Sale("Product C", "Clothing", 50.0, 1)
)

// Группировка по категории с агрегацией
val byCategory = sales.groupBy(_.category).map { case (category, sales) =>
  category -> Map(
    "totalAmount" -> sales.map(_.amount).sum,
    "totalQuantity" -> sales.map(_.quantity).sum,
    "averageAmount" -> sales.map(_.amount).sum / sales.size
  )
}
```

### Практические примеры: Многоуровневая группировка

```scala
// Многоуровневая группировка
case class Order(customerId: Long, productId: Long, amount: Double, date: java.time.LocalDate)

val orders = List(
  Order(1L, 101L, 100.0, java.time.LocalDate.of(2024, 1, 1)),
  Order(1L, 102L, 200.0, java.time.LocalDate.of(2024, 1, 2)),
  Order(2L, 101L, 150.0, java.time.LocalDate.of(2024, 1, 1))
)

// Группировка по клиенту, затем по дате
val byCustomerAndDate = orders
  .groupBy(_.customerId)
  .map { case (customerId, customerOrders) =>
    customerId -> customerOrders.groupBy(_.date).map { case (date, dateOrders) =>
      date -> dateOrders.map(_.amount).sum
    }
  }
```

### Использование с различными техниками для группировки

```scala
// Группировка с множественными агрегациями
case class Sale(product: String, category: String, amount: Double, quantity: Int)

val sales = List(
  Sale("Product A", "Electronics", 100.0, 2),
  Sale("Product B", "Electronics", 150.0, 3),
  Sale("Product C", "Clothing", 50.0, 1)
)

// Группировка по категории с агрегацией
val byCategory = sales.groupBy(_.category).map { case (category, sales) =>
  category -> Map(
    "totalAmount" -> sales.map(_.amount).sum,
    "totalQuantity" -> sales.map(_.quantity).sum,
    "averageAmount" -> sales.map(_.amount).sum / sales.size
  )
}
```

### Использование с различными техниками для многоуровневой группировки

```scala
// Многоуровневая группировка
case class Order(customerId: Long, productId: Long, amount: Double, date: java.time.LocalDate)

val orders = List(
  Order(1L, 101L, 100.0, java.time.LocalDate.of(2024, 1, 1)),
  Order(1L, 102L, 200.0, java.time.LocalDate.of(2024, 1, 2)),
  Order(2L, 101L, 150.0, java.time.LocalDate.of(2024, 1, 1))
)

// Группировка по клиенту, затем по дате
val byCustomerAndDate = orders
  .groupBy(_.customerId)
  .map { case (customerId, customerOrders) =>
    customerId -> customerOrders.groupBy(_.date).map { case (date, dateOrders) =>
      date -> dateOrders.map(_.amount).sum
    }
  }
```

## Дополнительные ресурсы

**Для дальнейшего изучения группировки и агрегации коллекций в **Scala** рекомендуется:**

- [Scala Collections Documentation](https://docs.scala-lang.org/overviews/collections-2.13/overview.html)
- [Scala Collections API — groupBy](https://www.scala-lang.org/api/current/scala/collection/immutable/Map.html)
