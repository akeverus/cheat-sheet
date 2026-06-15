---
title: "Вопросы на собеседовании: Apache Spark"
description: "Apache Spark: RDD, DataFrame, Dataset, Catalyst optimizer, Tungsten, lazy evaluation, partitioning, shuffle, caching, Spark Streaming, Structured Streaming, joins, broadcast, скос (skew)"
tags:
  - interview
  - data-engineering
  - apache-spark-interview
type: "interview"
difficulty: "intermediate"
aliases:
  - "Вопросы на собеседовании"
  - "Apache Spark"
  - "Apache Spark interview"
  - "Spark interview"
prerequisites: []
next: []
updated: "2026-04-25"
---
# Вопросы на собеседовании: `Apache Spark`

`Apache Spark` — самый популярный движок для распределённой обработки больших данных. Написан на Scala, использует JVM. Главные API: **RDD** (низкоуровневый), **DataFrame/Dataset** (high-level через Catalyst optimizer), **Spark Streaming**, **Structured Streaming**. На интервью спрашивают: lazy evaluation, shuffle, partitioning, joins (broadcast vs shuffle), skew handling, caching стратегии.

## Полезные ссылки

### Официальная документация и авторитетные источники

- [Apache Spark Documentation](https://spark.apache.org/docs/latest/)
- [Spark Programming Guide](https://spark.apache.org/docs/latest/rdd-programming-guide.html)
- [Spark SQL Guide](https://spark.apache.org/docs/latest/sql-programming-guide.html)
- [Structured Streaming Guide](https://spark.apache.org/docs/latest/structured-streaming-programming-guide.html)
- [Apache Spark — Baeldung](https://www.baeldung.com/apache-spark)
- [Spark Performance Tuning](https://spark.apache.org/docs/latest/sql-performance-tuning.html)
- [Tungsten Project](https://databricks.com/glossary/tungsten)

## Содержание

- [Полезные ссылки](#полезные-ссылки)
- [See also](#see-also)

**Базовые понятия**
- [Q1. (!) Что такое Apache Spark?](#q1--что-такое-apache-spark)
- [Q2. (!) Чем Spark отличается от Hadoop MapReduce?](#q2--чем-spark-отличается-от-hadoop-mapreduce)
- [Q3. (!) Архитектура Spark — Driver, Executor, Cluster Manager?](#q3--архитектура-spark--driver-executor-cluster-manager)
- [Q4. Cluster managers — какие?](#q4-cluster-managers--какие)

**RDD**
- [Q5. (!) Что такое RDD?](#q5--что-такое-rdd)
- [Q6. (!) Чем отличаются Transformations и Actions?](#q6--чем-отличаются-transformations-и-actions)
- [Q7. (!) Lazy evaluation в Spark?](#q7--lazy-evaluation-в-spark)
- [Q8. (!) Lineage — что это и зачем?](#q8--lineage--что-это-и-зачем)
- [Q9. Чем отличаются Wide и Narrow transformations?](#q9-чем-отличаются-wide-и-narrow-transformations)

**DataFrame / Dataset**
- [Q10. (!) Чем отличаются DataFrame, Dataset и RDD?](#q10--чем-отличаются-dataframe-dataset-и-rdd)
- [Q11. (!) Как работает Catalyst Optimizer?](#q11--как-работает-catalyst-optimizer)
- [Q12. Tungsten — что это?](#q12-tungsten--что-это)
- [Q13. Как работают Spark SQL queries?](#q13-как-работают-spark-sql-queries)

**Partitioning и Shuffle**
- [Q14. (!) Что такое partition в Spark?](#q14--что-такое-partition-в-spark)
- [Q15. (!) Что такое shuffle?](#q15--что-такое-shuffle)
- [Q16. (!) Как минимизировать shuffle?](#q16--как-минимизировать-shuffle)
- [Q17. Чем отличаются coalesce и repartition?](#q17-чем-отличаются-coalesce-и-repartition)
- [Q18. (!) Data skew — как решать?](#q18--data-skew--как-решать)

**Joins**
- [Q19. (!) Какие типы joins в Spark?](#q19--какие-типы-joins-в-spark)
- [Q20. (!) Broadcast join — когда применяется?](#q20--broadcast-join--когда-применяется)
- [Q21. Чем отличается sort-merge join от hash join?](#q21-чем-отличается-sort-merge-join-от-hash-join)

**Caching и Persistence**
- [Q22. (!) Чем отличается cache() от persist()?](#q22--чем-отличается-cache-от-persist)
- [Q23. Какие бывают storage levels?](#q23-какие-бывают-storage-levels)
- [Q24. (!) Когда стоит кэшировать?](#q24--когда-стоит-кэшировать)

**Spark SQL и DataFrame API**
- [Q25. (!) Window functions в Spark?](#q25--window-functions-в-spark)
- [Q26. (!) Что такое UDF (User Defined Functions)?](#q26--что-такое-udf-user-defined-functions)
- [Q27. Spark SQL vs DataFrame API — производительность?](#q27-spark-sql-vs-dataframe-api--производительность)

**Streaming**
- [Q28. (!) Чем отличаются Spark Streaming и Structured Streaming?](#q28--чем-отличаются-spark-streaming-и-structured-streaming)
- [Q29. (!) Что такое micro-batch?](#q29--что-такое-micro-batch)
- [Q30. (!) Watermark и late data?](#q30--watermark-и-late-data)
- [Q31. Что такое Continuous Processing mode?](#q31-что-такое-continuous-processing-mode)

**Performance**
- [Q32. (!) Spark UI — что смотреть?](#q32--spark-ui--что-смотреть)
- [Q33. Что такое AQE (Adaptive Query Execution)?](#q33-что-такое-aqe-adaptive-query-execution)
- [Q34. (!) Какие частые проблемы в production Spark?](#q34--какие-частые-проблемы-в-production-spark)
- [Q35. PySpark vs Spark Scala — производительность?](#q35-pyspark-vs-spark-scala--производительность)

## Q1. (!) Что такое Apache Spark?

`Apache Spark` — это движок для распределённой обработки больших данных: вы пишете один код, а Spark разбивает работу на параллельные задачи и раскладывает их по кластеру. Создан в **UC Berkeley AMPLab** (2009), стал Apache top-level проектом в 2014. Написан на **Scala**, работает на JVM.

Главная идея, которая выделила Spark на фоне предшественников, — держать промежуточные данные в памяти, а не сбрасывать их на диск после каждого шага. Отсюда выигрыш в скорости на итеративных задачах.

**Ключевые особенности:**
- **In-memory processing** — промежуточные данные живут в RAM кластера (у Hadoop MR — каждый шаг через диск)
- **Lazy evaluation** — Spark сначала строит план всех операций и оптимизирует его, и только потом выполняет
- **Единый набор API** для разных языков: Scala, Java, Python (PySpark), R, SQL
- **Разные типы нагрузок** в одном движке: batch, streaming, ML (MLlib), графы (GraphX)
- До **100x быстрее** Hadoop MapReduce на in-memory задачах

**Где применяют:** ETL, аналитические хранилища (Spark SQL), near-real-time аналитика, ML-пайплайны.

## Q2. (!) Чем Spark отличается от Hadoop MapReduce?

Главное отличие — в обращении с промежуточными данными. MapReduce после каждого шага пишет результат на диск (HDFS) и читает обратно на следующем; Spark держит данные в памяти и переиспользует между шагами. Поэтому на многошаговых и итеративных задачах Spark кратно быстрее — он не платит за дисковый round-trip на каждой итерации.

| Критерий | Spark | Hadoop MapReduce |
|----------|-------|------------------|
| Данные | In-memory (если помещаются) | На диске (HDFS) |
| API | Богатый (RDD, DataFrame, SQL) | Map + Reduce |
| Скорость | До 100x быстрее (in-memory) | Медленнее (disk I/O) |
| Языки | Scala, Java, Python, R, SQL | Java, Pig, Hive |
| Streaming | Да (Structured Streaming) | Нет (отдельные инструменты) |
| ML | MLlib | Mahout (отдельно) |
| Итеративные вычисления | Эффективно (cache) | Очень медленно |
| Latency | секунды — минуты | минуты — часы |

Hadoop MR ещё встречается в legacy-проектах, но все новые big-data пайплайны строят на Spark. Важная оговорка: преимущество Spark в скорости проявляется, пока данные помещаются в память; если их приходится сбрасывать на диск (spill), разрыв с MapReduce сокращается.

## Q3. (!) Архитектура Spark — Driver, Executor, Cluster Manager?

Топология компонентов:

- **Driver Program** (`SparkContext` / `SparkSession`) → запрашивает ресурсы у Cluster Manager
- **Cluster Manager** (YARN / K8s / Standalone) → выделяет executor'ы на worker-узлах:
  - **Executor 1** (Worker Node) → выполняет несколько Task параллельно (Task, Task)
  - **Executor 2** (Worker Node) → выполняет Task
  - **Executor 3** (Worker Node)

Spark-приложение — это один **Driver** и несколько **Executor**'ов, а **Cluster Manager** связывает их, выделяя ресурсы. Driver — «мозг», который решает, что делать; executor'ы — «руки», которые считают.

**Driver** — главный процесс приложения:
- Содержит `SparkContext` / `SparkSession` — точку входа
- Строит DAG из ваших трансформаций и планирует выполнение
- Разбивает работу на задачи и координирует executor'ы, собирает результаты

**Executor** — рабочий процесс на узле кластера:
- Выполняет назначенные ему задачи (tasks)
- Хранит закэшированные данные в своей памяти
- Возвращает результаты driver'у

**Cluster Manager** — распределяет ресурсы (CPU, память) между приложениями: YARN, Kubernetes или Standalone. Сам не считает — только выдаёт executor'ам слоты.

Связь: один driver управляет многими executor'ами; внутри executor'а несколько задач выполняются параллельно (по числу ядер). Если падает executor — Spark пересчитает потерянные данные по lineage; если падает driver — приложение целиком завершается.

## Q4. Cluster managers — какие?

Cluster manager отвечает за выделение ресурсов под executor'ы. Spark умеет работать с несколькими — выбор обычно диктуется тем, что уже развёрнуто в инфраструктуре.

| Manager | Когда выбирают |
|---------|----------|
| **Standalone** | Встроенный, простой; ставится без сторонних компонентов |
| **YARN** | Уже есть Hadoop-кластер — традиционный выбор |
| **Kubernetes** | Cloud-native окружение; популярность растёт |
| **Mesos** | Очень большой масштаб (Apache Mesos), сейчас редко |
| **Local** | Один JVM на машине — для разработки и тестов |

```bash
spark-submit \
  --master yarn \
  --deploy-mode cluster \
  --num-executors 10 \
  --executor-memory 4g \
  --executor-cores 2 \
  myapp.jar
```

На сегодня Kubernetes — самый частый выбор для новых развёртываний; YARN остаётся там, где живёт legacy-Hadoop.

## Q5. (!) Что такое RDD?

**RDD (Resilient Distributed Dataset)** — базовая, самая низкоуровневая абстракция Spark: неизменяемая коллекция элементов, разбитая на партиции и распределённая по узлам кластера. Всё остальное (DataFrame, Dataset) под капотом опирается на RDD.

Имя расшифровывает суть по буквам:
- **Resilient** — отказоустойчивый: потерянную партицию Spark пересчитает по lineage, не нужна репликация
- **Distributed** — данные физически разложены по узлам
- **Dataset** — коллекция неизменяемых элементов (каждая трансформация даёт новый RDD)
- **Lazy** — операции откладываются до первого action
- **In-memory или disk** — где хранить, задаётся через `persist`

```scala
val rdd = sc.parallelize(List(1, 2, 3, 4, 5))
val doubled = rdd.map(_ * 2)
val sum = doubled.reduce(_ + _) // action — запускает вычисление
```

**Когда применять.** В современном коде RDD напрямую трогают редко — почти всё делают через DataFrame/Dataset, потому что у них есть Catalyst и Tungsten, а у голого RDD оптимизатора нет. RDD оправдан только когда нужен низкоуровневый контроль: кастомный partitioner, нетипизированные данные без схемы, тонкая работа с lineage.

## Q6. (!) Чем отличаются Transformations и Actions?

Все операции Spark делятся на два класса, и это деление — основа модели выполнения. **Transformation** описывает новый RDD/DataFrame, но ничего не считает (ленивая). **Action** запускает реальное вычисление и возвращает результат — в driver или в хранилище.

**Transformations** — строят новый RDD/DataFrame, выполнение откладывается:

```scala
val rdd = sc.textFile("data.txt")
val filtered = rdd.filter(_.contains("error"))   // transformation
val mapped = filtered.map(_.toUpperCase)          // transformation
// ничего ещё не выполнилось!
```

**Actions** — отдают результат driver'у и запускают вычисление всей накопленной цепочки:

```scala
val count = mapped.count()              // action — запускает execution
val first = mapped.first()              // action
mapped.saveAsTextFile("output/")         // action
val collected = mapped.collect()         // action — bring all to driver (опасно!)
```

**Типичные transformations:** map, filter, flatMap, groupBy, reduceByKey, join, distinct, sort, ...
**Типичные actions:** count, collect, take, first, reduce, foreach, save*

Почему это важно: пока вы цепляете трансформации, Spark копит план и не делает лишней работы — action даёт ему шанс оптимизировать всю цепочку разом.

**Подводный камень:** `collect()` тянет все данные в driver. На большом датасете это гарантированный OOM драйвера — используйте `take(n)` или пишите результат в хранилище.

## Q7. (!) Lazy evaluation в Spark?

Lazy evaluation — это «отложенное вычисление»: трансформации не выполняются в момент вызова, а накапливаются в виде **DAG** (Directed Acyclic Graph) операций. Реальная работа стартует только когда вызван action — тогда Spark прогоняет весь накопленный граф.

```scala
val df = spark.read.parquet("huge_data/")  // ничего не происходит
  .filter($"age" > 18)                      // ничего
  .select("id", "name")                     // ничего
  .repartition(10)                          // ничего

df.show(20) // ВСЁ выполняется здесь
```

**Зачем так сделано.** Видя весь план целиком, а не по одной операции, Spark может его оптимизировать:
- **Catalyst** переставляет операции — например, протаскивает фильтр поближе к чтению (predicate pushdown), чтобы лишние строки вообще не грузились
- Сливает несколько узких операций в один stage (без промежуточных материализаций)
- Выкидывает то, что не влияет на результат (dead code elimination)

Обратная сторона: при ошибке стек-трейс часто указывает на action, а не на трансформацию, где реально кроется баг.

## Q8. (!) Lineage — что это и зачем?

**Lineage** (граф происхождения) — это записанная история того, как получился каждый RDD: из какого родителя и через какую операцию. По сути Spark хранит не сами данные, а рецепт их приготовления.

```scala
val rdd1 = sc.parallelize(...)          // lineage: source
val rdd2 = rdd1.map(...)                 // lineage: rdd1 → map
val rdd3 = rdd2.filter(...)              // lineage: rdd2 → filter
```

**Зачем нужен:**
- **Отказоустойчивость** — если узел упал и партиция потеряна, Spark по рецепту пересчитает именно её, не трогая остальные
- **Оптимизация** — Catalyst анализирует граф и перестраивает план

Главная мысль для интервью: lineage — это **альтернатива репликации**. HDFS хранит несколько копий данных ради надёжности; Spark вместо копий хранит рецепт и при потере просто пересчитывает. Дёшево по памяти, но если lineage очень длинный, восстановление дорогое — поэтому в итеративных задачах ставят `checkpoint`, обрывающий цепочку.

## Q9. Чем отличаются Wide и Narrow transformations?

Разница в том, сколько входных партиций нужно, чтобы посчитать одну выходную. От этого зависит, потребуется ли shuffle — самая дорогая операция в Spark.

**Narrow** — каждая выходная партиция зависит от **одной** (или малого числа) входной:
- `map`, `filter`, `flatMap`
- Shuffle не нужен: данные обрабатываются на месте, без передачи по сети
- Несколько narrow-операций подряд сливаются в один stage (pipelining)

**Wide** — выходная партиция собирается из **многих** входных:
- `groupByKey`, `reduceByKey`, `join`, `repartition`, `distinct`
- **Требуют shuffle** — данные перетасовываются по узлам через сеть и диск

Схема зависимостей партиций:

- **Narrow (`map`)** — один-к-одному, каждая входная партиция даёт ровно одну выходную:
  - `Part 1` → `Part 1'`
  - `Part 2` → `Part 2'`
  - `Part 3` → `Part 3'`
- **Wide (`groupByKey`)** — многие-ко-многим, каждая входная партиция вносит вклад в каждую выходную (это и есть shuffle):
  - `Part 1` → `Part 1'`, `Part 2'`
  - `Part 2` → `Part 1'`, `Part 2'`
  - `Part 3` → `Part 1'`, `Part 2'`

Ключевой вывод: именно wide-операции создают границу stage (stage boundary) и запускают shuffle. Поэтому минимизация wide-операций — одна из главных оптимизаций Spark-джобы.

## Q10. (!) Чем отличаются DataFrame, Dataset и RDD?

Три API — это три уровня абстракции над одними и теми же данными. Грубо: RDD даёт максимум контроля и минимум оптимизаций; DataFrame — схема и Catalyst, но ошибки в именах колонок вылезают только в рантайме; Dataset добавляет к DataFrame типобезопасность на этапе компиляции (но только в Scala/Java).

| API | Типобезопасность | Catalyst | Schema | Когда |
|-----|-----------------|----------|--------|-------|
| **RDD** | Типы на компиляции | Нет | Нет | Низкий уровень, кастомный partitioning |
| **DataFrame** | Только в рантайме | Да | Да (`StructType`) | Большинство задач |
| **Dataset** | Типы на компиляции | Да | Да | Типобезопасность + perf (только Scala/Java) |

**По умолчанию выбирайте DataFrame/Dataset, а не RDD.** Причина не в синтаксисе, а в том, что у них есть Catalyst (оптимизирует план) и Tungsten (компилирует операции в эффективный байткод). У голого RDD оптимизатора нет — Spark выполняет ваши шаги «как написано».

```scala
// DataFrame — generic Row
val df: DataFrame = spark.read.parquet("...")
df.filter($"age" > 18).select("name").show()

// Dataset — type-safe
case class User(name: String, age: Int)
val ds: Dataset[User] = df.as[User]
ds.filter(_.age > 18).map(_.name).show() // type-safe map

// RDD — низкоуровневый
val rdd = ds.rdd
```

**Нюанс:** в PySpark Dataset нет — в Python нет статической типизации, проверять типы на компиляции нечем. Остаётся только DataFrame.

## Q11. (!) Как работает Catalyst Optimizer?

`Catalyst` — оптимизатор запросов в Spark SQL / DataFrame. Его задача — превратить ваш декларативный запрос (что вы хотите получить) в эффективный физический план (как это посчитать), переписав его так, чтобы посчитать дешевле.

Работает по конвейеру, постепенно уточняя план:

1. **Parsing** — SQL → Unresolved Logical Plan (синтаксическое дерево, имена ещё не сопоставлены)
2. **Analysis** — резолвит колонки и таблицы по каталогу → Logical Plan
3. **Logical Optimization** — переписывает план: проталкивает фильтры (predicate pushdown), сворачивает константы (constant folding), отсекает ненужные колонки (column pruning)
4. **Physical Planning** — выбирает конкретные алгоритмы (broadcast vs sort-merge join) и оценивает их по стоимости
5. **Code Generation (Tungsten)** — генерирует байткод для горячих путей вместо интерпретации

```scala
df.filter($"age" > 18).filter($"country" === "US").select("name")
// Catalyst объединит в один filter и сделает column pruning
```

Главный вывод: благодаря Catalyst **тот же запрос на DataFrame обычно быстрее, чем на RDD**, — оптимизатор сам приведёт его к эффективной форме, а у RDD такого слоя нет.

## Q12. Tungsten — что это?

`Project Tungsten` (с Spark 1.4) — набор низкоуровневых оптимизаций исполнения, нацеленных на «железо»: память и CPU. Идея в том, что узким местом Spark был не диск, а накладные расходы JVM — сборка мусора, неэффективное представление данных, интерпретация выражений. Tungsten бьёт именно по ним.

1. **Off-heap memory** — данные хранятся вне кучи JVM с явным управлением через `sun.misc.Unsafe`, минуя сборщик мусора и его паузы
2. **Cache-aware computation** — алгоритмы написаны с учётом устройства CPU-кэша, чтобы меньше промахиваться мимо него
3. **Whole-stage code generation** — для всего stage генерируется компактный JIT-дружественный код вместо цепочки виртуальных вызовов-итераторов

Итог: **в 5–10 раз быстрее** аналогичных RDD-операций. Catalyst решает *что* считать, Tungsten — *как* это исполнить максимально быстро на конкретной машине.

## Q13. Как работают Spark SQL queries?

Spark SQL — это не отдельный движок, а ещё один фасад над DataFrame. SQL-строка и эквивалентный код на DataFrame API проходят через один и тот же Catalyst и дают идентичный физический план — разница чисто синтаксическая.

```scala
spark.sql("SELECT name, age FROM users WHERE age > 18")

// Эквивалентно DataFrame API
spark.table("users").filter($"age" > 18).select("name", "age")
```

Чтобы обращаться к данным по имени в SQL, их регистрируют как view или таблицу:

```scala
// Регистрация temp view
df.createOrReplaceTempView("users")
spark.sql("SELECT COUNT(*) FROM users").show()

// Permanent table
df.write.saveAsTable("users")
```

## Q14. (!) Что такое partition в Spark?

**Partition** — это кусок данных и одновременно единица параллелизма: один partition обрабатывается ровно одним task на одном ядре. Поэтому число партиций напрямую задаёт, насколько работа распараллелится по кластеру.

```scala
val rdd = sc.parallelize(1 to 1000, 10) // 10 partitions
rdd.getNumPartitions // 10

val df = spark.read.parquet("data/")
df.rdd.getNumPartitions // зависит от файлов
```

**Как выбрать число партиций — баланс:**
- **Слишком мало** → задач меньше, чем ядер: часть кластера простаивает, а партиции большие и могут не влезть в память
- **Слишком много** → накладные расходы на планирование и запуск множества мелких задач съедают выигрыш

**Эмпирическое правило:** примерно 2–4 партиции на ядро кластера — так есть запас на балансировку, и при этом overhead невелик.

```scala
df.repartition(100) // shuffle на 100 partitions
df.coalesce(50)      // объединить (без shuffle, только уменьшение)
```

## Q15. (!) Что такое shuffle?

**Shuffle** — это полное перераспределение данных между партициями: записи перекидываются с одних узлов на другие через диск и сеть. Возникает, когда для результата нужно собрать вместе записи с одинаковым ключом, которые разбросаны по разным партициям — то есть при любой wide-операции (`groupByKey`, `join`, `repartition`, `distinct`).

**Как происходит:**
1. **Map side** — каждый task раскладывает свои записи по выходным партициям (по hash от ключа)
2. **Запись на локальный диск** — результат сохраняется в shuffle-файлы
3. **Reduce side fetch** — каждый reducer стягивает по сети свои партиции со всех map-узлов
4. **Reduce side process** — собранные записи обрабатываются

**Почему это дорого:**
- Дисковый I/O (запись и чтение shuffle-файлов)
- Сетевой I/O (передача между узлами)
- Сериализация/десериализация каждой записи

Суммарно shuffle в 100–1000 раз медленнее narrow-операций и потому — **главная** причина медленных Spark-джоб. Большинство оптимизаций сводится к тому, чтобы либо убрать shuffle, либо уменьшить объём перетасовываемых данных.

## Q16. (!) Как минимизировать shuffle?

Поскольку shuffle перетасовывает данные по сети, стратегия одна: либо не запускать его вовсе, либо уменьшить объём данных, которые он переносит. Конкретные приёмы:

1. **`reduceByKey` вместо `groupByKey`** — частичная агрегация на map-стороне (combiner), чтобы по сети ушли уже свёрнутые значения, а не все исходные:
```scala
// BAD — shuffle всех значений
rdd.groupByKey().mapValues(_.sum)

// GOOD — pre-aggregation на map стороне
rdd.reduceByKey(_ + _)
```

2. **Broadcast join** для маленьких таблиц — рассылаем маленькую сторону на все узлы и совсем избегаем shuffle большой
3. **Pre-partitioning** по ключу join — если обе стороны уже разложены одинаково, при join shuffle не нужен
4. **Фильтруй раньше** — чем меньше строк дойдёт до wide-операции, тем дешевле shuffle (Catalyst делает predicate pushdown сам)
5. **Column pruning** — отбрасывай ненужные колонки до join, чтобы перетасовывать меньше байтов
6. **`repartition` только по необходимости** — это явный shuffle, не злоупотребляйте

## Q17. Чем отличаются coalesce и repartition?

Оба метода меняют число партиций, но принципиально по-разному. `coalesce` только **объединяет** существующие партиции, не двигая данные между узлами, — без shuffle, но и без гарантии равномерности. `repartition` делает полный **shuffle** и раскладывает данные равномерно (или по hash от колонки). Отсюда правило: уменьшаешь число партиций и равномерность не критична — `coalesce`; нужна балансировка, увеличение или разбивка по ключу — `repartition`.

```scala
df.coalesce(10)     // объединяет partitions, БЕЗ shuffle
df.repartition(10)   // полный shuffle, равномерное распределение
df.repartition($"country") // shuffle по hash от country
```

| Метод | Shuffle | Равные партиции | Когда |
|-------|---------|-----------------|-------|
| `coalesce(N)` | Нет | Нет (могут быть неравные) | Уменьшить число партиций перед записью |
| `repartition(N)` | Да | Да | Балансировка, увеличение числа партиций |
| `repartition(col)` | Да | По hash | Перед join по этой колонке |

**Рекомендация:** перед `write` используй `coalesce`, чтобы сократить число выходных файлов, не платя за лишний shuffle.

## Q18. (!) Data skew — как решать?

**Data skew** (перекос данных) — это когда одна-две партиции получают непропорционально много данных. Поскольку партиция = задача, эти задачи тянутся в разы дольше, а остальной кластер уже всё досчитал и простаивает. Stage не может завершиться, пока не доедет самая медленная задача, — поэтому весь джоб упирается в один перегруженный узел.

**Симптомы (видно в Spark UI):**
- Один task обрабатывается в 10 раз дольше прочих
- OOM на конкретном executor (на него легла гигантская партиция)
- Stage подолгу «висит» почти готовым

**Откуда берётся:**
- Hot keys в `groupBy`/`join` — например, все `NULL` или одно популярное значение попадают в одну партицию
- Неравномерное распределение данных по ключу партиционирования

**Как лечить:**

1. **Salting** — «солим» горячий ключ случайным суффиксом, чтобы разбить его на несколько партиций, агрегируем, потом убираем соль и доагрегируем:

```scala
val saltedDf = df.withColumn("salted_key",
  concat($"key", lit("_"), (rand() * 100).cast("int"))
)
saltedDf.groupBy("salted_key").agg(...)
  .withColumn("key", split($"salted_key", "_").getItem(0))
  .groupBy("key").agg(...)
```

2. **AQE (Adaptive Query Execution)** — в Spark 3+ автоматически замечает перекошенные партиции и дробит их; часто решает проблему без ручного salting

3. **Broadcast join** — если одна из таблиц маленькая, рассылаем её и убираем shuffle, а с ним и перекос на стороне join

4. **Custom partitioner** — точечно для специфичных распределений ключей

## Q19. (!) Какие типы joins в Spark?

Здесь важно различать две вещи: **тип join** (какие строки войдут в результат — это семантика SQL) и **стратегию join** (каким физическим алгоритмом Spark его выполнит — это выбирает Catalyst по размеру таблиц). На интервью часто путают.

**Типы join** (что попадёт в результат):

```scala
df1.join(df2, "id")              // inner
df1.join(df2, Seq("id"), "left")  // left outer
df1.join(df2, Seq("id"), "right") // right outer
df1.join(df2, Seq("id"), "outer") // full outer
df1.join(df2, Seq("id"), "left_semi")  // вернёт строки df1 где есть match
df1.join(df2, Seq("id"), "left_anti")  // строки df1 где НЕТ match
df1.crossJoin(df2)                // cartesian
```

**Стратегии join** (как Catalyst его исполнит):
- **Broadcast Hash Join** — одна сторона меньше `spark.sql.autoBroadcastJoinThreshold` (по умолчанию 10 MB): её рассылают на все узлы, shuffle не нужен — самый быстрый вариант
- **Sort-Merge Join** — обе таблицы большие: их сортируют и сливают; дефолт для крупных join, требует shuffle
- **Shuffle Hash Join** — строит hash-таблицу из меньшей стороны после shuffle (применяется реже)
- **Cartesian** — когда условия соединения нет: декартово произведение, обычно признак ошибки

## Q20. (!) Broadcast join — когда применяется?

**Broadcast join** применяют, когда одна из таблиц достаточно маленькая, чтобы целиком уместиться в памяти каждого executor'а. Тогда Spark рассылает её копию на все узлы, и большую таблицу можно соединять локально — без перетасовки по сети. Это убирает самый дорогой шаг обычного join: shuffle большой стороны.

```scala
import org.apache.spark.sql.functions.broadcast

val joined = bigDf.join(broadcast(smallDf), "id")
```

**Условие применимости:** `smallDf` должна помещаться в память каждого executor'а (порог по умолчанию — `spark.sql.autoBroadcastJoinThreshold`, 10 MB). Если broadcast'ить слишком большую таблицу — получите OOM на executor'ах.

**Зачем это нужно:**
- **Нет shuffle** для большой таблицы — основной выигрыш
- В **5–100 раз быстрее** sort-merge join на подходящих данных

Подсказку можно дать и в SQL через хинт:

```sql
SELECT /*+ BROADCAST(small_table) */ ...
```

В Spark 3+ AQE умеет сам переключить sort-merge на broadcast, если по фактической статистике видно, что одна сторона мала, — даже когда вы не указали хинт.

## Q21. Чем отличается sort-merge join от hash join?

Это два алгоритма соединения с разным компромиссом «память против сортировки». Sort-merge сортирует обе стороны и идёт по ним слиянием — память тратит экономно, но платит за сортировку. Hash join строит hash-таблицу из меньшей стороны и быстро ищет по ней — без сортировки, зато таблица должна влезть в память.

**Sort-merge join:**
1. Сортируем обе стороны по ключу join
2. Идём двумя указателями и сливаем (merge)

Сложность `O((n + m) log n)` — основной вклад даёт сортировка.

**Hash join:**
1. Build phase — строим hash-таблицу из меньшей стороны
2. Probe phase — для каждой строки большей стороны ищем совпадение в hash-таблице

Сложность `O(n + m)` — линейно, но нужна память под hash-таблицу.

**Что выбирает Spark:** по умолчанию **sort-merge join** — он устойчивее по памяти и не боится больших сторон. **Broadcast hash join** включается для маленьких таблиц, когда одна сторона помещается в память.

## Q22. (!) Чем отличается cache() от persist()?

По сути это один и тот же механизм. `cache()` — просто удобный сокращённый вызов `persist()` с уровнем по умолчанию. `persist()` же позволяет явно указать storage level — где и как хранить данные (память, диск, сериализовать, реплицировать).

```scala
df.cache()                                    // = persist(MEMORY_AND_DISK)
df.persist(StorageLevel.MEMORY_ONLY)          // RAM
df.persist(StorageLevel.MEMORY_AND_DISK)      // RAM + spill to disk
df.persist(StorageLevel.DISK_ONLY)            // только диск
df.persist(StorageLevel.MEMORY_ONLY_SER)      // RAM, сериализованно (компактнее)
df.unpersist()                                // освободить
```

Тонкость, которую любят спросить: уровень по умолчанию у `cache()` зависит от API. Для DataFrame/Dataset это `MEMORY_AND_DISK`, для RDD — `MEMORY_ONLY`. То есть закэшированный RDD, не влезший в память, просто пересчитается, а DataFrame сольёт лишнее на диск.

## Q23. Какие бывают storage levels?

Storage level описывает, где и как хранить закэшированные данные. По сути это три независимых выбора: память или диск, хранить сырыми или сериализованными, реплицировать или нет. Из их комбинаций и складывается список уровней.

| Level | Memory | Disk | Serialized | Replication |
|-------|--------|------|------------|-------------|
| `MEMORY_ONLY` | Да | Нет | Нет | 1 |
| `MEMORY_AND_DISK` | Да, spill | Да | Нет | 1 |
| `MEMORY_ONLY_SER` | Да | Нет | Да | 1 |
| `MEMORY_AND_DISK_SER` | Да, spill | Да | Да | 1 |
| `DISK_ONLY` | Нет | Да | — | 1 |
| `MEMORY_ONLY_2` | Да | Нет | Нет | 2 |

Как читать суффиксы:
- **`SER`** — данные хранятся сериализованными: занимают меньше памяти, но тратят CPU на сериализацию/десериализацию при доступе. Компромисс «память против CPU».
- **`_2`** — копия на 2 узлах ради отказоустойчивости: при потере узла кэш не придётся пересчитывать.

**По умолчанию для DataFrame** — `MEMORY_AND_DISK`: держит в памяти, что помещается, остальное сбрасывает на диск. Подходит для большинства случаев.

## Q24. (!) Когда стоит кэшировать?

Простое правило: кэш окупается, только если результат используется больше одного раза. Из-за lazy evaluation Spark по умолчанию пересчитывает DataFrame заново при каждом action, проходя весь lineage. Кэш разрывает этот повтор — но сам занимает память, поэтому кэшировать «на всякий случай» вредно.

**Кэшируй, когда:**
- DataFrame участвует в **нескольких** action — иначе пересчёт каждый раз
- За ним стоит дорогая трансформация (длинный lineage), которую жалко повторять
- Итеративные алгоритмы (ML), где один и тот же набор гоняется в цикле

**Не кэшируй, когда:**
- DataFrame используется ровно один раз — кэш не даст ничего, кроме лишней памяти
- Он совсем маленький или дешёвый в пересчёте
- Памяти не хватает — кэш всё равно будет вытесняться (evict), а накладные расходы останутся

```scala
val expensive = df.groupBy(...).agg(...).cache()
expensive.count()  // первое action — build cache
expensive.show()   // используем cache
expensive.write.parquet(...)  // ещё раз
expensive.unpersist()
```

**Подвох:** `cache()` тоже ленивый — он лишь помечает DataFrame для кэширования, а реально кэш наполняется только при первом action. Поэтому часто вызывают `.count()` сразу после, чтобы «прогреть» кэш до основной работы.

## Q25. (!) Window functions в Spark?

Оконные функции считают агрегат «в пределах группы», но, в отличие от `groupBy`, не схлопывают строки — каждая исходная строка остаётся, и к ней добавляется значение по её окну. Окно задаётся через `Window`: `partitionBy` определяет группы, `orderBy` — порядок внутри группы (нужен для ранжирования и lag/lead).

```scala
import org.apache.spark.sql.expressions.Window
import org.apache.spark.sql.functions._

val window = Window.partitionBy("department").orderBy($"salary".desc)

df.withColumn("rank", rank().over(window))
  .withColumn("prev_salary", lag("salary", 1).over(window))
  .withColumn("running_total", sum("salary").over(window))
```

**Где применяют:**
- Топ-N внутри групп (через `rank`/`row_number`)
- Накопительные суммы (running totals)
- `lag`/`lead` для сравнения с предыдущей/следующей строкой во временных рядах
- Перцентильный ранг

## Q26. (!) Что такое UDF (User Defined Functions)?

UDF (User Defined Function) — это ваша собственная функция, которую можно применить к колонке там, где не хватает встроенных. Удобно, но за гибкость приходится платить производительностью.

```scala
val toUpper = udf((s: String) => s.toUpperCase)
df.withColumn("name_upper", toUpper($"name"))

// SQL
spark.udf.register("to_upper", (s: String) => s.toUpperCase)
spark.sql("SELECT to_upper(name) FROM users")
```

**Подводный камень:** для Catalyst UDF — чёрный ящик. Оптимизатор не видит, что внутри, поэтому не может протолкнуть через неё фильтры или применить codegen — оптимизации теряются.

**Поэтому: где есть встроенная функция — используйте её, а не UDF.** Встроенные функции Catalyst понимает и оптимизирует.

```scala
// SLOW — UDF
df.withColumn("upper", udf((s: String) => s.toUpperCase).apply($"name"))

// FAST — встроенная функция
df.withColumn("upper", upper($"name"))
```

В PySpark UDF медленнее вдвойне: каждая строка гоняется между JVM и Python-процессом с сериализацией туда-обратно. С Spark 3+ есть **Pandas UDF** на базе Arrow — они передают данные пачками без построчной сериализации и потому намного быстрее обычных Python-UDF.

## Q27. Spark SQL vs DataFrame API — производительность?

Производительность **одинаковая** — это самый частый ожидаемый ответ. Оба варианта проходят через Catalyst и компилируются в один и тот же физический план, так что строка SQL и цепочка вызовов DataFrame после оптимизатора неотличимы.

```scala
// SQL
spark.sql("SELECT name FROM users WHERE age > 18")

// DataFrame
df.filter($"age" > 18).select("name")
```

Раз скорость не зависит от выбора, выбирают по удобству:
- **SQL** — привычнее аналитикам, лаконичнее для сложных запросов
- **DataFrame API** — удобнее разработчикам; в связке с Dataset даёт типобезопасность на компиляции

## Q28. (!) Чем отличаются Spark Streaming и Structured Streaming?

Это два поколения стримингового API. Старое — **Spark Streaming (DStream)** — построено на RDD: без Catalyst, со слабой поддержкой event time и поздних данных. Новое — **Structured Streaming** — это тот же DataFrame/SQL, только над бесконечным потоком: те же оптимизации, watermark, богатые оконные операции. По сути вы пишете обычный батч-запрос, а Spark исполняет его инкрементально по мере поступления данных.

| Критерий | Spark Streaming (DStream) | Structured Streaming |
|----------|---------------------------|----------------------|
| API | На RDD | На DataFrame |
| Оптимизация | Нет (как у RDD) | Catalyst |
| Режимы | Только micro-batch | Micro-batch + Continuous |
| Оконные операции | Базовые | Богатые (event time, watermark) |
| Поздние данные | Сложно | Watermark |
| Статус | Legacy (API из Spark 2) | Рекомендуемый (Spark 2+) |

**Вывод для интервью:** в новых проектах берите Structured Streaming, DStream-API считается устаревшим.

## Q29. (!) Что такое micro-batch?

**Micro-batch** — модель, в которой Spark обрабатывает поток не «по одному событию», а маленькими порциями: копит поступающие данные за короткий интервал и запускает на каждой порции обычный batch-джоб. Это позволяет переиспользовать весь зрелый батч-движок (Catalyst, Tungsten) для стриминга, но накладывает «пол» по задержке — минимум один интервал.

```
[ data ] → [ data ] → [ data ]
   ↓          ↓          ↓
 batch 1   batch 2    batch 3   (каждые ~1 сек)
   ↓          ↓          ↓
 process   process    process
```

**Trigger interval** — как часто запускать очередной batch. По умолчанию — сразу как закончился предыдущий; можно задать фиксированный интервал.

**Задержка:** обычно от 100 мс до 5 секунд. Если нужна латентность ниже 100 мс, micro-batch не подходит — там берут настоящий потоковый движок (Flink, Apache Storm).

## Q30. (!) Watermark и late data?

В реальном потоке события приходят не по порядку: сетевые задержки, ретраи, оффлайн-устройства — и запись с временем 12:00 может прийти в 12:09. Возникает дилемма: сколько ждать опоздавшие данные, прежде чем считать окно закрытым? **Watermark** и есть этот порог: «события старше такого-то времени мы уже не ждём».

```scala
streamDf.withWatermark("timestamp", "10 minutes")
  .groupBy(window($"timestamp", "5 minutes"), $"user_id")
  .count()
```

**Как это работает:**
- Watermark = (максимальный увиденный timestamp) − 10 минут; он сдвигается вперёд по мере поступления данных
- Запись с timestamp меньше watermark считается «опоздавшей» (late) и отбрасывается (или включается в out-of-order — зависит от output mode)
- Главное: состояние агрегатов старше watermark можно безопасно освободить. Без watermark Spark был бы обязан вечно держать состояние всех окон — и память бы неограниченно росла.

## Q31. Что такое Continuous Processing mode?

Continuous mode (с Spark 2.3, экспериментальный) — попытка обойти задержку micro-batch: вместо обработки порциями данные обрабатываются по мере поступления, по одной записи. Это снижает латентность до миллисекунд, но в обмен на серьёзные ограничения.

```scala
streamDf.writeStream
  .trigger(Trigger.Continuous("1 second"))
  .start()
```

Задержка **~1 мс** против 100+ мс у micro-batch. Цена — узкая применимость:
- Поддерживаются только **map-подобные** операции (без агрегаций и join)
- Лишь часть source/sink
- Гарантия only at-least-once, а не exactly-once

Continuous mode так и остался экспериментальным и широкого применения не получил. Если действительно нужна низкая латентность — берут **Apache Flink** (см. [Flink](apache-flink-interview.md)), у которого потоковая модель родная, а не надстройка над батчем.

## Q32. (!) Spark UI — что смотреть?

Spark UI (на `http://driver:4040`, либо в history server после завершения) — главный инструмент диагностики джобы. Логика осмотра: сверху вниз — от джобы к самому долгому stage, затем к конкретным задачам, и наконец к причине (skew, spill, shuffle).

Где что искать:

1. **Jobs** — какие job'ы запускались и сколько шли
2. **Stages** — какой stage самый долгий (с него и начинают копать)
3. **SQL** — план запроса (DAG и физический план), видно, какую стратегию join выбрал Catalyst
4. **Executors** — память, CPU и распределение задач по executor'ам
5. **Storage** — что закэшировано и сколько занимает

**Сигналы проблем:**
- **Skew** — несколько задач идут непропорционально дольше остальных (см. Q18)
- **Spill** — данные не влезли в RAM и сброшены на диск; верный признак нехватки памяти или слишком крупных партиций
- **Shuffle read/write** — большие объёмы указывают на дорогой shuffle как узкое место
- **Failed tasks** — упавшие задачи: смотрите причину (часто OOM)

## Q33. Что такое AQE (Adaptive Query Execution)?

AQE (с Spark 3.0) решает фундаментальную проблему статической оптимизации: Catalyst строит план *до* выполнения, опираясь на оценки размеров, а они часто врут. AQE корректирует план **прямо во время выполнения** — по фактической статистике, собранной после каждого shuffle. То есть видит реальные размеры партиций, а не догадки.

```properties
spark.sql.adaptive.enabled=true
spark.sql.adaptive.coalescePartitions.enabled=true
spark.sql.adaptive.skewJoin.enabled=true
```

**Что именно делает:**
- **Объединяет мелкие партиции** после shuffle, если их получилось слишком много мелких
- **Меняет стратегию join** на лету: sort-merge → broadcast, если оказалось, что одна сторона мала
- **Лечит skew в join** — автоматически дробит перекошенные партиции (см. Q18)

На Spark 3+ AQE даёт заметный прирост производительности «из коробки», почти без настройки. Рекомендуется держать включённым.

## Q34. (!) Какие частые проблемы в production Spark?

Почти все боли Spark сводятся к трём корням: **память** (OOM, GC), **shuffle/перекос данных** и **неудачное число партиций**. Список ниже — частые симптомы и их фиксы.

| # | Проблема | Что делать |
|---|----------|------------|
| 1 | **OOM на executor** | Партиции слишком крупные — увеличить память или число партиций |
| 2 | **Долгие stage из-за skew** | Salting, AQE skew join (см. Q18) |
| 3 | **Медленный shuffle** | Поднять `spark.sql.shuffle.partitions`, оптимизировать join'ы |
| 4 | **Медленный PySpark UDF** | Заменить на встроенную функцию или Pandas UDF |
| 5 | **OOM драйвера** | `collect()` слишком большого df — не тянуть всё в driver |
| 6 | **Медленное чтение** | Мало партиций в файлах; перейти на колоночные форматы (Parquet, ORC) |
| 7 | **Паузы GC** | Поднять `spark.executor.memory`, переключиться на G1GC |
| 8 | **Кластер недогружен** | Мало партиций — увеличить параллелизм |
| 9 | **Кластер перегружен** | Слишком много executor'ов под объём данных |
| 10 | **`groupByKey` на больших данных** | Заменить на `reduceByKey` (агрегация на map-стороне, см. Q16) |

## Q35. PySpark vs Spark Scala — производительность?

Ключ к ответу — понять, где Python вообще участвует в исполнении. Если вы пишете на DataFrame API, Python только строит план, а считает всё JVM-движок — производительность идентична Scala. Разница появляется лишь там, где данные реально проходят через Python-процесс: в операциях над RDD и в обычных UDF, где каждая строка сериализуется между JVM и Python.

| Критерий | PySpark | Scala Spark |
|----------|---------|-------------|
| DataFrame API | Та же производительность | Та же производительность |
| Операции над RDD | Медленно (сериализация Python ↔ JVM) | Быстро |
| UDF | Медленно (интерпретатор Python) | Быстро (JVM) |
| Pandas UDF | Быстро (Arrow) | — |
| Дистрибуция | PyPI, проще ставить | Maven/SBT |
| ML-библиотеки | Интеграция с Pandas, scikit-learn | Ограниченно |

**Главное:** на DataFrame-операциях разницы нет, она проявляется только в RDD и UDF.

Pandas UDF (на базе Arrow) почти закрывают этот разрыв даже для пользовательских функций. На практике PySpark доминирует в data science (рядом экосистема Python), Scala — в data engineering (производительность и типобезопасность).

## See also

- [Apache Flink](apache-flink-interview.md) — настоящий streaming с low latency
- [Kafka Streams](kafka-streams-interview.md) — JVM streaming на Kafka
- [Apache Airflow](apache-airflow-interview.md) — orchestration Spark jobs
- [Scala](../programming-languages/scala/scala-interview.md) — основной язык Spark
- [Stream Processing](stream-processing-interview.md) — концепции
- [Data Warehousing](data-warehousing-interview.md) — где Spark часто ETL
- [Data Lake / Lakehouse](data-lake-lakehouse-interview.md) — Delta Lake, Iceberg, Hudi
- [PostgreSQL](../databases/postgresql-interview.md) — частый source/destination
- [Apache Kafka](../messaging/kafka-interview.md) — Spark читает/пишет
- [Микросервисы](../architecture/microservices-interview.md) — vs аналитика на Spark
- [Performance Testing](../performance/performance-testing-interview.md) — Spark UI и benchmarking
- [JVM](../jvm/jvm-interview.md) — Spark на JVM, GC tuning
- [Memory Management](../performance/memory-management-interview.md) — Tungsten off-heap

- [Apache Flink](apache-flink-interview.md)
- [Data Lake и Lakehouse](data-lake-lakehouse-interview.md)
- [Data Warehousing](data-warehousing-interview.md)
- [dbt](dbt-interview.md)
- [Kafka Streams](kafka-streams-interview.md)
