# Scala Cheatsheets

Шпаргалки по Scala: основы языка, коллекции, функциональное программирование.

## Структура

### Основы
- [`scala-basics.md`](scala-basics.md) - основы Scala (установка, создание проектов, переменные, функции, классы, case классы, pattern matching, generics, implicit, type classes)

### Коллекции
- [`scala-collections.md`](scala-collections.md) - обзор коллекций Scala (immutable/mutable коллекции, операции, for-comprehensions, параллельные коллекции, view, stream)
- [`scala-collections-list.md`](scala-collections-list.md) - списки (List, ListBuffer, операции, pattern matching)
- [`scala-collections-set.md`](scala-collections-set.md) - множества (Set, HashSet, SortedSet, операции над множествами)
- [`scala-collections-map.md`](scala-collections-map.md) - словари (Map, HashMap, SortedMap, операции)
- [`scala-collections-vector.md`](scala-collections-vector.md) - Vector (эффективная структура для произвольного доступа)
- [`scala-collections-array.md`](scala-collections-array.md) - Array (массивы, производительность, Java совместимость)
- [`scala-collections-operations.md`](scala-collections-operations.md) - операции над коллекциями (трансформации, фильтрация, агрегация)
- [`scala-collections-grouping.md`](scala-collections-grouping.md) - группировка и агрегация коллекций

### Функциональное программирование
- [`scala-fp-basics.md`](scala-fp-basics.md) - основы функционального программирования (Higher-Order Functions, Currying, Pure Functions)
- [`scala-fp-advanced.md`](scala-fp-advanced.md) - продвинутое ФП (Monads, Functors, Cats, Scalaz)
- [`scala-monads.md`](scala-monads.md) - Monads в Scala (Option, Either, Try, Future, List, State, Writer, Reader)
- [`scala-type-classes.md`](scala-type-classes.md) - Type Classes в Scala (Semigroup, Monoid, Functor, Applicative)
- [`scala-shapeless.md`](scala-shapeless.md) - Shapeless (HList, Generic, Lens, type-level вычисления)
- [`scala-scalaz.md`](scala-scalaz.md) - Scalaz (Type Classes, Monads, Validation, Lens)
- [`scala-tagless-final.md`](scala-tagless-final.md) - Tagless Final (Algebras, интерпретаторы, композиция эффектов)

### Конкурентность
- [`scala-concurrency.md`](scala-concurrency.md) - конкурентность (Futures, Promises, параллельные коллекции)
- [`scala-futures.md`](scala-futures.md) - Futures (асинхронное программирование, композиция, обработка ошибок)
- [`scala-promises.md`](scala-promises.md) - Promises (создание и управление Futures, адаптация callback API)
- [`scala-akka.md`](scala-akka.md) - Akka Actors (акторная модель, сообщения, supervision)
- [`scala-reactive.md`](scala-reactive.md) - реактивное программирование (RxScala, Akka Streams, Reactive Streams, backpressure)
- [`scala-akka-streams.md`](scala-akka-streams.md) - Akka Streams (Source, Flow, Sink, обработка файлов, backpressure)

### Фреймворки и библиотеки
- [`scala-play.md`](scala-play.md) - Play Framework (веб-приложения, маршрутизация, контроллеры, JSON, аутентификация)
- [`scala-slick.md`](scala-slick.md) - Slick (типобезопасный доступ к БД, запросы, миграции, репозитории)
- [`scala-zio.md`](scala-zio.md) - ZIO (функциональное программирование с эффектами, обработка ошибок)
- [`scala-cats.md`](scala-cats.md) - Cats (функциональные абстракции, type classes, Monads)
- [`scala-cats-effect.md`](scala-cats-effect.md) - Cats Effect (IO Monad, управление ресурсами, асинхронные операции)
- [`scala-sbt.md`](scala-sbt.md) - SBT (инструмент сборки, зависимости, плагины)
- [`scala-http4s.md`](scala-http4s.md) - http4s (функциональный HTTP клиент и сервер, маршрутизация, middleware)
- [`scala-doobie.md`](scala-doobie.md) - Doobie (функциональный JDBC слой, типобезопасные запросы, транзакции)

### Дополнительные темы
- [`scala-pattern-matching.md`](scala-pattern-matching.md) - Pattern Matching (базовые паттерны, case classes, guards, type patterns)
- [`scala-for-comprehensions.md`](scala-for-comprehensions.md) - For-comprehensions (синтаксический сахар для flatMap, фильтрация, генераторы)
- [`scala-implicit.md`](scala-implicit.md) - Implicit (параметры, conversions, type classes, Scala 3 given/using)
- [`scala-type-system.md`](scala-type-system.md) - система типов (generics, variance, bounds, type inference, path-dependent types)
- [`scala-macros.md`](scala-macros.md) - макросы (code generation, compile-time вычисления)
- [`scala-dsl.md`](scala-dsl.md) - DSL (Domain-Specific Languages, type-safe builders, комбинаторные парсеры)
- [`scala-reflection.md`](scala-reflection.md) - Reflection (Type Tags, Class Tags, Runtime Reflection, работа с аннотациями)
- [`scala-json.md`](scala-json.md) - JSON (Play JSON, Circe, сериализация, десериализация, валидация)
- [`scala-error-handling.md`](scala-error-handling.md) - обработка ошибок (Option, Either, Try, error accumulation, recovery)
- [`scala-serialization.md`](scala-serialization.md) - сериализация (Java Serialization, JSON, Protocol Buffers, Avro, версионирование)
- [`scala-logging.md`](scala-logging.md) - логирование (SLF4J, Logback, структурированное логирование, MDC)
- [`scala-config.md`](scala-config.md) - конфигурация (Typesafe Config, PureConfig, переменные окружения, валидация)
- [`scala-another.md`](scala-another.md) - дополнительные темы (паттерны проектирования, best practices, продвинутые техники)

### Тестирование и производительность
- [`scala-testing.md`](scala-testing.md) - тестирование (ScalaTest, Specs2, Mockito, Actors, Testcontainers)
- [`scala-performance.md`](scala-performance.md) - оптимизация производительности (профилирование, JVM настройки, оптимизация памяти)
- [`scala-interop-java.md`](scala-interop-java.md) - взаимодействие с Java (использование Java библиотек, миграция, обработка null)

## Связанные разделы

- [`../java/`](../java/) - Java (Scala работает на JVM)

## Полезные ссылки

- [Scala Documentation](https://docs.scala-lang.org/)
- [Scala School](https://twitter.github.io/scala_school/)
- [Baeldung Scala Tutorial](https://www.baeldung.com/scala)

