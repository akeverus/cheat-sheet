---
title: "Scala SBT"
description: "Полное руководство по SBT (Scala Build Tool): настройка проектов, зависимости, плагины, задачи"
tags:
  - scala
  - sbt
  - build-tool
  - dependency-management
difficulty: "intermediate"
prerequisites: ["scala/scala-basics.md"]
next: []
updated: "2026-02-06"
related: ["scala/scala-basics.md"]
---

# Scala SBT

Кратко: полное руководство по **SBT** (Scala `Build` Tool): настройка проектов, зависимости, плагины, задачи.

## Полезные ссылки

### Официальная документация
- [SBT Documentation](https://www.scala-sbt.org/documentation.html)

### См. также
- [[scala-basics|Основы Scala]]

## Содержание

- [**Scala SBT**](#scala-sbt)
- [Введение в **SBT**](#введение-в-sbt)
  - [Основные возможности](#основные-возможности)
- [Структура проекта](#структура-проекта)
- [**build.sbt**](#buildsbt)
- [Зависимости](#зависимости)
  - [Добавление зависимостей](#добавление-зависимостей)
  - [Зависимости для тестирования](#зависимости-для-тестирования)
- [Задачи](#задачи)
- [Компиляция](#компиляция)
- [Запуск тестов](#запуск-тестов)
- [Очистка](#очистка)
- [Запуск приложения](#запуск-приложения)
- [REPL](#repl)
- [Плагины](#плагины)
  - [Добавление плагинов](#добавление-плагинов)
  - [Разрешение конфликтов зависимостей](#разрешение-конфликтов-зависимостей)
  - [Многомодульные проекты](#многомодульные-проекты)
  - [Кастомные задачи](#кастомные-задачи)
  - [Кастомные настройки](#кастомные-настройки)
  - [Плагины — расширенное использование](#плагины-расширенное-использование)
  - [Настройка компилятора](#настройка-компилятора)
  - [Настройка для разных окружений](#настройка-для-разных-окружений)
  - [Работа с ресурсами](#работа-с-ресурсами)
  - [Тестирование](#тестирование)
  - [Публикация артефактов](#публикация-артефактов)
  - [Инкрементальная компиляция](#инкрементальная-компиляция)
  - [**REPL** (Read-Eval-Print Loop)](#repl-read-eval-print-loop)
- [Запуск REPL](#запуск-repl)
- [В REPL можно тестировать код](#в-repl-можно-тестировать-код)
  - [Практический пример: Полная конфигурация проекта](#практический-пример-полная-конфигурация-проекта)
- [Лучшие практики](#лучшие-практики)
  - [Использование версий в отдельном файле](#использование-версий-в-отдельном-файле)
  - [Организация многомодульных проектов](#организация-многомодульных-проектов)
  - [Использование плагинов для автоматизации](#использование-плагинов-для-автоматизации)
- [Продвинутые возможности **SBT**](#продвинутые-возможности-sbt)
  - [Мультимодульные проекты](#мультимодульные-проекты)
  - [**SBT Shell** и интерактивный режим](#sbt-shell-и-интерактивный-режим)
  - [**SBT Plugins** (расширенные)](#sbt-plugins-расширенные)
  - [**SBT Multi-Project Builds** (расширенные)](#sbt-multi-project-builds-расширенные)
  - [**SBT Tasks** (расширенные)](#sbt-tasks-расширенные)
  - [Практические примеры: Кастомные задачи **SBT**](#практические-примеры-кастомные-задачи-sbt)
  - [Практические примеры: **SBT** для мультимодульных проектов](#практические-примеры-sbt-для-мультимодульных-проектов)
- [Заключение (расширенное)](#заключение-расширенное)
  - [Практические примеры: Работа с настройками для разных окружений](#практические-примеры-работа-с-настройками-для-разных-окружений)
  - [Использование с различными плагинами для автоматизации](#использование-с-различными-плагинами-для-автоматизации)
  - [Использование с различными настройками для CI/CD](#использование-с-различными-настройками-для-cicd)
- [Дополнительные ресурсы](#дополнительные-ресурсы)

## Введение в SBT

**SBT** (Scala `Build` Tool) — это инструмент сборки для **Scala** проектов, предоставляющий управление зависимостями, компиляцию, тестирование и другие задачи.

### Основные возможности

- **Управление зависимостями**: автоматическое разрешение и загрузка зависимостей
- **Инкрементальная компиляция**: компиляция только измененных файлов
- **REPL**: интерактивная консоль для тестирования кода
- **Плагины**: расширяемость через систему плагинов

## Структура проекта

**Типичная структура **SBT** проекта:**

```text
project/
  build.properties
  plugins.sbt
build.sbt
src/
  main/
    scala/
  test/
    scala/
```

## build.sbt

**Основной файл конфигурации проекта:**

```scala
name := "my-project"
version := "1.0.0"
scalaVersion := "2.13.10"

libraryDependencies ++= Seq(
  "org.scalatest" %% "scalatest" % "3.2.15" % Test
)
```

## Зависимости

### Добавление зависимостей

```scala
libraryDependencies ++= Seq(
  "org.typelevel" %% "cats-core" % "2.9.0",
  "com.typesafe.akka" %% "akka-actor" % "2.6.20"
)
```

### Зависимости для тестирования

```scala
libraryDependencies ++= Seq(
  "org.scalatest" %% "scalatest" % "3.2.15" % Test,
  "org.mockito" % "mockito-core" % "5.3.1" % Test
)
```

## Задачи

**Основные **SBT** задачи:**

```bash
# Компиляция
sbt compile

# Запуск тестов
sbt test

# Очистка
sbt clean

# Запуск приложения
sbt run

# REPL
sbt console
```

## Плагины

### Добавление плагинов

```scala
// project/plugins.sbt
addSbtPlugin("com.typesafe.sbt" % "sbt-native-packager" % "1.9.11")
```

### Разрешение конфликтов зависимостей

**SBT** автоматически разрешает конфликты зависимостей:**

```scala
// SBT выберет последнюю версию при конфликте
libraryDependencies ++= Seq(
  "org.typelevel" %% "cats-core" % "2.9.0",
  "org.typelevel" %% "cats-effect" % "3.5.0"  // может использовать cats-core 2.9.0
)

// Принудительное использование версии
dependencyOverrides += "org.typelevel" %% "cats-core" % "2.9.0"
```

### Многомодульные проекты

**SBT** поддерживает многомодульные проекты:**

```scala
// build.sbt
lazy val root = (project in file("."))
  .aggregate(core, api, web)

lazy val core = (project in file("core"))
  .settings(
    name := "my-project-core",
    libraryDependencies ++= Seq(
      "org.typelevel" %% "cats-core" % "2.9.0"
    )
  )

lazy val api = (project in file("api"))
  .dependsOn(core)
  .settings(
    name := "my-project-api"
  )

lazy val web = (project in file("web"))
  .dependsOn(core, api)
  .settings(
    name := "my-project-web"
  )
```

### Кастомные задачи

**Можно создавать кастомные задачи:**

```scala
// Определение задачи
lazy val hello = taskKey[Unit]("Prints hello")

hello := {
  println("Hello from SBT!")
}

// Задача с зависимостями
lazy val buildInfo = taskKey[String]("Build information")

buildInfo := {
  s"Version: ${version.value}, Scala: ${scalaVersion.value}"
}

// Задача, зависящая от другой задачи
lazy val printBuildInfo = taskKey[Unit]("Prints build information")

printBuildInfo := {
  println(buildInfo.value)
}

// Задача, зависящая от компиляции
printBuildInfo := {
  (Compile / compile).value
  println(buildInfo.value)
}
```

### Кастомные настройки

**Можно определять кастомные настройки:**

```scala
// Определение настройки
lazy val customSetting = settingKey[String]("Custom setting")

customSetting := "default value"

// Использование настройки
lazy val useCustom = taskKey[Unit]("Uses custom setting")

useCustom := {
  println(s"Custom setting: ${customSetting.value}")
}
```

### Плагины — расширенное использование

**Плагины расширяют функциональность **SBT**:**

```scala
// project/plugins.sbt

// Плагин для упаковки
addSbtPlugin("com.typesafe.sbt" % "sbt-native-packager" % "1.9.11")

// Плагин для покрытия кода
addSbtPlugin("org.scoverage" % "sbt-scoverage" % "2.0.6")

// Плагин для форматирования
addSbtPlugin("org.scalameta" % "sbt-scalafmt" % "2.5.0")

// Использование плагинов в build.sbt
enablePlugins(JavaAppPackaging)  // для native-packager
coverageEnabled := true  // для scoverage
```

### Настройка компилятора

**Можно настраивать опции компилятора:**

```scala
scalacOptions ++= Seq(
  "-deprecation",           // предупреждения об устаревших конструкциях
  "-feature",               // предупреждения о новых возможностях
  "-unchecked",             // предупреждения о неполной проверке типов
  "-Xfatal-warnings",       // превращать предупреждения в ошибки
  "-Ywarn-unused:imports"   // предупреждения о неиспользуемых импортах
)
```

### Настройка для разных окружений

**Можно настраивать проект для разных окружений:**

```scala
lazy val commonSettings = Seq(
  organization := "com.example",
  version := "1.0.0",
  scalaVersion := "2.13.10"
)

lazy val devSettings = Seq(
  scalacOptions ++= Seq("-Xfatal-warnings")
)

lazy val prodSettings = Seq(
  scalacOptions ++= Seq("-optimize")
)

lazy val root = (project in file("."))
  .settings(commonSettings)
  .settings(
    name := "my-project"
  )

// Для разработки
lazy val dev = (project in file("."))
  .settings(commonSettings ++ devSettings)

// Для production
lazy val prod = (project in file("."))
  .settings(commonSettings ++ prodSettings)
```

### Работа с ресурсами

**SBT** управляет ресурсами проекта:**

```scala
// Структура:
// src/main/resources/
// src/test/resources/

// Доступ к ресурсам в коде
val resource = scala.io.Source.fromResource("config.properties")
val content = resource.mkString
resource.close()
```

### Тестирование

**SBT** интегрирован с тестовыми фреймворками:**

```scala
libraryDependencies ++= Seq(
  "org.scalatest" %% "scalatest" % "3.2.15" % Test,
  "org.scalacheck" %% "scalacheck" % "1.17.0" % Test
)

// Запуск тестов
// sbt test

// Запуск только определенных тестов
// sbt "testOnly *MyTest"

// Запуск тестов с покрытием
// sbt clean coverage test coverageReport
```

### Публикация артефактов

**SBT** может публиковать артефакты в репозитории:**

```scala
publishTo := Some(
  if (isSnapshot.value)
    "snapshots" at "https://oss.sonatype.org/content/repositories/snapshots"
  else
    "releases" at "https://oss.sonatype.org/service/local/staging/deploy/maven2"
)

credentials += Credentials(
  "Sonatype Nexus Repository Manager",
  "oss.sonatype.org",
  "username",
  "password"
)

// Публикация
// sbt publish
// sbt publishLocal  // в локальный репозиторий
```

### Инкрементальная компиляция

**SBT** использует инкрементальную компиляцию:**

```scala
// SBT отслеживает изменения и компилирует только измененные файлы
// Это значительно ускоряет сборку больших проектов

// Очистка кэша компиляции
// sbt clean

// Принудительная полная перекомпиляция
// sbt clean compile
```

### REPL (Read-`Eval-Print` Loop)

**SBT** предоставляет интерактивную консоль:**

```bash
# Запуск REPL
sbt console

# В REPL можно тестировать код
scala> val x = 42
x: Int = 42

scala> val list = List(1, 2, 3)
list: List[Int] = List(1, 2, 3)

scala> list.map(_ * 2)
res1: List[Int] = List(2, 4, 6)
```

### Практический пример: Полная конфигурация проекта

```scala
// build.sbt
ThisBuild / organization := "com.example"
ThisBuild / version := "1.0.0"
ThisBuild / scalaVersion := "2.13.10"

lazy val root = (project in file("."))
  .settings(
    name := "my-project",
    libraryDependencies ++= Seq(
      "org.typelevel" %% "cats-core" % "2.9.0",
      "org.typelevel" %% "cats-effect" % "3.5.0",
      "org.scalatest" %% "scalatest" % "3.2.15" % Test
    ),
    scalacOptions ++= Seq(
      "-deprecation",
      "-feature",
      "-unchecked"
    )
  )

// project/Dependencies.scala
object Dependencies {
  val catsVersion = "2.9.0"
  val catsEffectVersion = "3.5.0"
  val scalaTestVersion = "3.2.15"
}

// project/plugins.sbt
addSbtPlugin("com.typesafe.sbt" % "sbt-native-packager" % "1.9.11")
addSbtPlugin("org.scoverage" % "sbt-scoverage" % "2.0.6")
```

## Лучшие практики

### Использование версий в отдельном файле

```scala
// project/Dependencies.scala
object Dependencies {
  val scalaVersion = "2.13.10"
  val catsVersion = "2.9.0"
  val akkaVersion = "2.6.20"
}

// build.sbt
import Dependencies._

libraryDependencies ++= Seq(
  "org.typelevel" %% "cats-core" % catsVersion
)
```

### Организация многомодульных проектов

```scala
// Хорошо - четкое разделение модулей
lazy val core = (project in file("core"))
lazy val api = (project in file("api")).dependsOn(core)
lazy val web = (project in file("web")).dependsOn(core, api)

// Плохо - все в одном модуле без разделения
lazy val root = (project in file("."))
```

### Использование плагинов для автоматизации

```scala
// Хорошо - использование плагинов для стандартных задач
enablePlugins(JavaAppPackaging)
enablePlugins(ScoverageSbtPlugin)

// Плохо - ручное выполнение задач, которые можно автоматизировать
```

## Продвинутые возможности SBT

### Мультимодульные проекты

**SBT** поддерживает создание мультимодульных проектов, где каждый модуль может иметь свои зависимости и настройки.

```scala
// build.sbt
lazy val root = (project in file("."))
  .aggregate(core, api, web)
  .settings(
    name := "my-project",
    version := "1.0.0"
  )

lazy val core = (project in file("core"))
  .settings(
    name := "my-project-core",
    libraryDependencies += "org.scalatest" %% "scalatest" % "3.2.15" % Test
  )

lazy val api = (project in file("api"))
  .dependsOn(core)
  .settings(
    name := "my-project-api",
    libraryDependencies += "com.typesafe.akka" %% "akka-http" % "10.5.0"
  )

lazy val web = (project in file("web"))
  .dependsOn(api, core)
  .settings(
    name := "my-project-web",
    libraryDependencies += "com.typesafe.play" %% "play" % "2.9.0"
  )
```

### Кастомные задачи

**SBT** позволяет создавать кастомные задачи для автоматизации различных операций.

```scala
// Создание кастомной задачи
lazy val generateCode = taskKey[Unit]("Generate code from templates")

generateCode := {
  val templates = (sourceDirectory.value / "templates").listFiles()
  val outputDir = sourceManaged.value / "generated"
  outputDir.mkdirs()

  templates.foreach { template =>
    val content = IO.read(template)
    val generated = content.replace("{{VERSION}}", version.value)
    IO.write(outputDir / template.getName, generated)
  }
}

// Зависимость задачи от компиляции
(compile in Compile) := ((compile in Compile) dependsOn generateCode).value
```

## Продвинутые возможности SBT

### SBT Shell и интерактивный режим

**SBT** предоставляет интерактивный **shell** для работы с проектом.

```scala
// Запуск SBT shell
// sbt

// Выполнение команд в shell
// > compile
// > test
// > run
// > reload
// > exit

// Выполнение команд из командной строки
// sbt compile
// sbt test
// sbt "project subproject" compile
```

### SBT Plugins (расширенные)

**SBT** поддерживает множество плагинов для расширения функциональности.

```scala
// Добавление плагинов в project/plugins.sbt
addSbtPlugin("com.typesafe.sbt" % "sbt-native-packager" % "1.8.1")
addSbtPlugin("org.scalastyle" %% "scalastyle-sbt-plugin" % "1.0.0")
addSbtPlugin("com.github.sbt" % "sbt-release" % "1.1.0")

// Использование плагинов
enablePlugins(JavaAppPackaging)
enablePlugins(ScalastylePlugin)
```

### SBT Multi-Project Builds (расширенные)

**SBT** поддерживает сложные мультимодульные проекты.

```scala
// Определение корневого проекта
lazy val root = (project in file("."))
  .aggregate(core, api, web)
  .dependsOn(core, api, web)
  .settings(
    name := "root-project"
  )

// Определение подпроектов
lazy val core = (project in file("core"))
  .settings(
    name := "core",
    libraryDependencies += "org.scalatest" %% "scalatest" % "3.2.15" % Test
  )

lazy val api = (project in file("api"))
  .dependsOn(core)
  .settings(
    name := "api"
  )

lazy val web = (project in file("web"))
  .dependsOn(core, api)
  .settings(
    name := "web"
  )
```

### SBT Tasks (расширенные)

**SBT** позволяет создавать сложные задачи с зависимостями.

```scala
// Задача с зависимостями
lazy val buildAll = taskKey[Unit]("Build all modules")

buildAll := {
  (compile in Compile).value
  (test in Test).value
  (packageBin in Compile).value
}

// Задача с параметрами
lazy val deploy = inputKey[Unit]("Deploy to environment")

deploy := {
  val env = (parsedArgs).headOption.getOrElse("dev")
  val project = (parsedArgs).tail.headOption.getOrElse("root")
  println(s"Deploying $project to $env")
}
```

### Практические примеры: Кастомные задачи SBT

```scala
// build.sbt
lazy val hello = taskKey[Unit]("Prints hello")
hello := {
  println("Hello from SBT!")
}

lazy val countLines = taskKey[Int]("Counts lines in source files")
countLines := {
  val sourceFiles = (Compile / sources).value
  sourceFiles.map(IO.readLines(_).size).sum
}

// Запуск
// sbt hello
// sbt countLines
```

### Практические примеры: SBT для мультимодульных проектов

```scala
// build.sbt
lazy val root = (project in file("."))
  .aggregate(core, api, web)
  .settings(
    name := "my-project"
  )

lazy val core = (project in file("core"))
  .settings(
    name := "core",
    libraryDependencies += "org.typelevel" %% "cats-core" % "2.9.0"
  )

lazy val api = (project in file("api"))
  .dependsOn(core)
  .settings(
    name := "api"
  )

lazy val web = (project in file("web"))
  .dependsOn(core, api)
  .settings(
    name := "web",
    libraryDependencies += "com.typesafe.play" %% "play" % "2.8.19"
  )
```

## Заключение (расширенное)

**SBT** является мощным инструментом для управления **Scala** проектами. Понимание структуры проекта, конфигурации, зависимостей, задач, плагинов, многомодульных проектов, публикации артефактов, мультимодульных проектов, кастомных задач, интеграции с CI/CD, **SBT Shell**, расширенных плагинов, сложных мультимодульных проектов, расширенных задач, создания кастомных задач, организации мультимодульных проектов и практических применений позволяет эффективно работать с **SBT**. Правильная организация проекта, использование плагинов, настройка для разных окружений, использование **SBT Shell**, расширенных плагинов, сложных мультимодульных проектов, создание кастомных задач для автоматизации и организация мультимодульных проектов критичны для **production-ready** приложений. **SBT** особенно полезен для создания сложных проектов с множественными модулями, зависимостями, настройками, автоматизацией процессов сборки и развертывания, интеграцией с различными инструментами и системами, создания кастомных задач и организации больших мультимодульных проектов.

### Практические примеры: Кастомные задачи SBT

```scala
// Создание кастомной задачи
lazy val countLines = taskKey[Int]("Count lines in source files")

countLines := {
  val sourceFiles = (Compile / sources).value
  sourceFiles.map(IO.readLines(_).size).sum
}

// Задача, зависящая от другой задачи
lazy val printLineCount = taskKey[Unit]("Print line count")

printLineCount := {
  val count = countLines.value
  println(s"Total lines: $count")
}
```

### Практические примеры: Работа с настройками для разных окружений

```scala
// Настройки для разных окружений
lazy val devSettings = Seq(
  scalacOptions += "-Xfatal-warnings"
)

lazy val prodSettings = Seq(
  scalacOptions += "-opt:l:inline"
)

// Применение настроек
ThisBuild / scalacOptions ++= {
  if (sys.props.get("env").contains("prod")) prodSettings
  else devSettings
}
```

### Использование с различными плагинами для автоматизации

```scala
// build.sbt
addSbtPlugin("com.typesafe.sbt" % "sbt-native-packager" % "1.9.16")
addSbtPlugin("com.github.sbt" % "sbt-release" % "1.1.0")

// Настройка плагинов
enablePlugins(JavaAppPackaging)
enablePlugins(ReleasePlugin)

releaseVersion := { _ => "1.0.0" }
```

### Использование с различными настройками для CI/CD

```scala
// build.sbt
// Настройки для CI/CD
val ciSettings = Seq(
  scalacOptions ++= Seq("-Xfatal-warnings", "-Xlint"),
  testOptions in Test += Tests.Argument("-oD")
)

// Применение настроек
ThisBuild / scalacOptions ++= {
  if (sys.env.contains("CI")) ciSettings
  else Seq.empty
}
```

## Дополнительные ресурсы

**Для дальнейшего изучения **SBT** рекомендуется:**

- [SBT Documentation](https://www.scala-sbt.org/documentation.html)
- [SBT Reference Manual](https://www.scala-sbt.org/1.x/docs/)
- [SBT Plugins](https://www.scala-sbt.org/1.x/docs/Plugins.html)
