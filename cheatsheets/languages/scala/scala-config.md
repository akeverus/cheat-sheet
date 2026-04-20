---
title: "Scala Configuration"
description: "Полное руководство по конфигурации в Scala: Typesafe Config, Play Configuration, переменные окружения"
tags:
  - scala
  - configuration
  - typesafe-config
  - play-config
difficulty: "intermediate"
prerequisites: ["scala/scala-basics.md"]
next: []
updated: "2026-04-20"
related: ["scala/scala-basics.md", "scala/scala-play.md"]
---

# Scala Configuration

Кратко: полное руководство по конфигурации в **Scala**: **Typesafe Config**, **Play Configuration**, переменные окружения.

## Полезные ссылки

### Официальная документация
- [Typesafe Config](https://github.com/lightbend/config)

### См. также
- [[scala-basics|Основы Scala]]
- [[scala-play|Play Framework]]

## Содержание

- [Введение в конфигурацию](#введение-в-конфигурацию)
  - [Основные подходы](#основные-подходы)
- [Typesafe Config](#typesafe-config)
- [Play Configuration](#play-configuration)
- [Переменные окружения](#переменные-окружения)
  - [Чтение вложенных конфигураций](#чтение-вложенных-конфигураций)
  - [Чтение списков и массивов](#чтение-списков-и-массивов)
  - [Значения по умолчанию](#значения-по-умолчанию)
  - [Переменные подстановки](#переменные-подстановки)
  - [Загрузка конфигурации из разных источников](#загрузка-конфигурации-из-разных-источников)
  - [Валидация конфигурации](#валидация-конфигурации)
  - [Play Configuration — расширенные возможности](#play-configuration-расширенные-возможности)
  - [PureConfig для типобезопасной конфигурации](#pureconfig-для-типобезопасной-конфигурации)
  - [Конфигурация для разных окружений](#конфигурация-для-разных-окружений)
  - [Переменные окружения — расширенное использование](#переменные-окружения-расширенное-использование)
  - [Конфигурация с секретами](#конфигурация-с-секретами)
- [Лучшие практики](#лучшие-практики)
  - [Использование типизированных конфигураций](#использование-типизированных-конфигураций)
  - [Валидация при загрузке](#валидация-при-загрузке)
  - [Использование разных конфигураций для разных окружений](#использование-разных-конфигураций-для-разных-окружений)
- [Продвинутые техники работы с конфигурацией](#продвинутые-техники-работы-с-конфигурацией)
  - [Динамическая конфигурация](#динамическая-конфигурация)
  - [Валидация конфигурации](#валидация-конфигурации-1)
  - [Безопасное хранение секретов](#безопасное-хранение-секретов)
- [Решение проблем](#решение-проблем)
- [Частые вопросы](#частые-вопросы)
- [Заключение](#заключение)
- [Дополнительные техники работы с конфигурацией](#дополнительные-техники-работы-с-конфигурацией)
  - [Динамическое обновление конфигурации](#динамическое-обновление-конфигурации)
  - [Валидация конфигурации](#валидация-конфигурации-2)
  - [Безопасное хранение секретов](#безопасное-хранение-секретов-1)
  - [Практические примеры: Hot reload конфигурации](#практические-примеры-hot-reload-конфигурации)
  - [Практические примеры: Конфигурация с приоритетами](#практические-примеры-конфигурация-с-приоритетами)
  - [Практические примеры: Конфигурация с валидацией](#практические-примеры-конфигурация-с-валидацией)
  - [Практические примеры: Работа с переменными окружения](#практические-примеры-работа-с-переменными-окружения)
  - [Практические примеры: Валидация конфигурации](#практические-примеры-валидация-конфигурации)
  - [Использование с различными техниками для динамической конфигурации](#использование-с-различными-техниками-для-динамической-конфигурации)
  - [Использование с различными техниками для безопасного хранения секретов](#использование-с-различными-техниками-для-безопасного-хранения-секретов)
- [Дополнительные ресурсы](#дополнительные-ресурсы)

## Введение в конфигурацию

Конфигурация позволяет настраивать приложение без изменения кода. **Scala** использует **Typesafe Config** и **Play Configuration** для управления конфигурацией.

### Основные подходы

- **Typesafe Config**: универсальная библиотека для конфигурации
- **Play Configuration**: конфигурация в **Play Framework**
- **Переменные окружения**: для чувствительных данных

## Typesafe Config

**Typesafe Config** предоставляет мощный **API** для работы с конфигурацией:**

```scala
import com.typesafe.config.{Config, ConfigFactory}

// Загрузка конфигурации
val config: Config = ConfigFactory.load()

// Чтение значений
val host: String = config.getString("app.host")
val port: Int = config.getInt("app.port")
val timeout: Long = config.getDuration("app.timeout").toMillis

// Чтение с значениями по умолчанию
val maxRetries: Int = config.getInt("app.maxRetries")
```

**Typesafe Config** поддерживает **HOCON** формат, который более выразителен, чем **JSON**.

## Play Configuration

**Play Framework** предоставляет свой **API** для конфигурации:**

```scala
import play.api.Configuration

class MyService @Inject()(config: Configuration) {
  val host: String = config.get[String]("app.host")
  val port: Int = config.get[Int]("app.port")
  val timeout: Option[Long] = config.get[Option[Long]]("app.timeout")
}
```

**Play Configuration** интегрирован с **dependency injection** и предоставляет типобезопасный доступ к конфигурации.

## Переменные окружения

**Переменные окружения используются для чувствительных данных:**

```scala
val dbUrl = sys.env.getOrElse("DATABASE_URL", "localhost")
val apiKey = sys.env.get("API_KEY").getOrElse(throw new Exception("API_KEY not set"))
```

Переменные окружения обеспечивают безопасное хранение секретов.

### Чтение вложенных конфигураций

**Typesafe Config** поддерживает вложенные структуры:**

```scala
import com.typesafe.config.{Config, ConfigFactory}

val config = ConfigFactory.load()

// Вложенная конфигурация
val dbConfig = config.getConfig("database")
val dbHost = dbConfig.getString("host")
val dbPort = dbConfig.getInt("port")
val dbName = dbConfig.getString("name")

// Или напрямую
val dbHost2 = config.getString("database.host")
```

### Чтение списков и массивов

```scala
val config = ConfigFactory.load()

// Список строк
val servers: List[String] = config.getStringList("app.servers").asScala.toList

// Список чисел
val ports: List[Int] = config.getIntList("app.ports").asScala.map(_.toInt).toList

// Массив конфигураций
val users: List[Config] = config.getConfigList("app.users").asScala.toList
users.foreach { userConfig =>
  val name = userConfig.getString("name")
  val age = userConfig.getInt("age")
}
```

### Значения по умолчанию

```scala
val config = ConfigFactory.load()

// С использованием hasPath
val maxRetries = if (config.hasPath("app.maxRetries")) {
  config.getInt("app.maxRetries")
} else {
  3  // значение по умолчанию
}

// С использованием getOrElse (через Option)
val timeout = Option(config.getString("app.timeout")).getOrElse("30s")

// С использованием withFallback
val defaultConfig = ConfigFactory.parseString("""
  app {
    maxRetries = 3
    timeout = 30s
  }
""")
val finalConfig = config.withFallback(defaultConfig)
```

### Переменные подстановки

**Typesafe Config** поддерживает переменные подстановки:**

```scala
// application.conf
// app {
//   host = "localhost"
//   port = 8080
//   url = "http://${app.host}:${app.port}"
// }

val config = ConfigFactory.load()
val url = config.getString("app.url")  // "http://localhost:8080"
```

### Загрузка конфигурации из разных источников

```scala
import com.typesafe.config.{Config, ConfigFactory}

// Загрузка из файла
val fileConfig = ConfigFactory.parseFile(new File("custom.conf"))

// Загрузка из строки
val stringConfig = ConfigFactory.parseString("""
  app {
    name = "MyApp"
    version = "1.0"
  }
""")

// Загрузка из ресурсов
val resourceConfig = ConfigFactory.parseResources("application.conf")

// Объединение конфигураций
val finalConfig = ConfigFactory.load()
  .withFallback(fileConfig)
  .withFallback(stringConfig)
  .withFallback(resourceConfig)
  .resolve()  // разрешение переменных
```

### Валидация конфигурации

```scala
import com.typesafe.config.{Config, ConfigException}

def validateConfig(config: Config): Either[String, AppConfig] = {
  try {
    Right(AppConfig.fromConfig(config))
  } catch {
    case e: ConfigException.Missing =>
      Left(s"Missing configuration: ${e.getMessage}")
    case e: ConfigException.WrongType =>
      Left(s"Wrong type in configuration: ${e.getMessage}")
    case e: Exception =>
      Left(s"Configuration error: ${e.getMessage}")
  }
}
```

### Play Configuration — расширенные возможности

```scala
import play.api.Configuration
import play.api.inject.Injector

class MyService @Inject()(config: Configuration) {
  // Чтение с типом
  val host: String = config.get[String]("app.host")

  // Чтение с опциональным значением
  val port: Option[Int] = config.get[Option[Int]]("app.port")

  // Чтение с значением по умолчанию
  val timeout: Long = config.get[Long]("app.timeout")

  // Чтение вложенной конфигурации
  val dbConfig: Configuration = config.get[Configuration]("database")
  val dbHost: String = dbConfig.get[String]("host")

  // Чтение списков
  val servers: Seq[String] = config.get[Seq[String]]("app.servers")

  // Чтение с валидацией
  val maxRetries: Int = config.get[Int]("app.maxRetries") match {
    case n if n > 0 && n <= 10 => n
    case _ => throw new IllegalArgumentException("maxRetries must be between 1 and 10")
  }
}
```

### PureConfig для типобезопасной конфигурации

**PureConfig** предоставляет автоматическое преобразование конфигурации в **case** классы:**

```scala
import pureconfig._
import pureconfig.generic.auto._

case class DatabaseConfig(
  host: String,
  port: Int,
  name: String,
  user: String,
  password: String
)

case class AppConfig(
  name: String,
  version: String,
  database: DatabaseConfig,
  maxRetries: Int = 3
)

// Автоматическое чтение конфигурации
val appConfig: Either[ConfigReaderFailures, AppConfig] =
  ConfigSource.default.load[AppConfig]

appConfig match {
  case Right(config) =>
    println(s"App: ${config.name}, DB: ${config.database.host}")
  case Left(failures) =>
    failures.toList.foreach(println)
}
```

### Конфигурация для разных окружений

```scala
import com.typesafe.config.{Config, ConfigFactory}

object ConfigLoader {
  def load(environment: String = "development"): Config = {
    val baseConfig = ConfigFactory.load("application.conf")
    val envConfig = ConfigFactory.load(s"application-$environment.conf")

    envConfig
      .withFallback(baseConfig)
      .resolve()
  }
}

// Использование
val devConfig = ConfigLoader.load("development")
val prodConfig = ConfigLoader.load("production")
```

### Переменные окружения — расширенное использование

```scala
import scala.util.Try

object EnvConfig {
  def getString(key: String, default: String): String = {
    sys.env.getOrElse(key, default)
  }

  def getInt(key: String, default: Int): Int = {
    sys.env.get(key).flatMap(s => Try(s.toInt).toOption).getOrElse(default)
  }

  def getBoolean(key: String, default: Boolean): Boolean = {
    sys.env.get(key).flatMap(s => Try(s.toBoolean).toOption).getOrElse(default)
  }

  def require(key: String): String = {
    sys.env.getOrElse(key, throw new IllegalArgumentException(s"Environment variable $key is required"))
  }
}

// Использование
val dbUrl = EnvConfig.getString("DATABASE_URL", "localhost")
val maxConnections = EnvConfig.getInt("MAX_CONNECTIONS", 10)
val apiKey = EnvConfig.require("API_KEY")
```

### Конфигурация с секретами

**Для работы с секретами рекомендуется использовать специализированные библиотеки:**

```scala
import com.typesafe.config.Config

case class SecretConfig(
  apiKey: String,
  dbPassword: String
)

object SecretConfig {
  def fromConfig(config: Config): SecretConfig = {
    // В production секреты должны загружаться из безопасного хранилища
    val env = sys.env.getOrElse("ENVIRONMENT", "development")

    if (env == "production") {
      SecretConfig(
        apiKey = sys.env.getOrElse("API_KEY", throw new Exception("API_KEY not set")),
        dbPassword = sys.env.getOrElse("DB_PASSWORD", throw new Exception("DB_PASSWORD not set"))
      )
    } else {
      SecretConfig(
        apiKey = config.getString("secrets.apiKey"),
        dbPassword = config.getString("secrets.dbPassword")
      )
    }
  }
}
```

## Лучшие практики

### Использование типизированных конфигураций

```scala
case class AppConfig(host: String, port: Int, timeout: Long)

object AppConfig {
  def fromConfig(config: Config): AppConfig = {
    AppConfig(
      host = config.getString("app.host"),
      port = config.getInt("app.port"),
      timeout = config.getDuration("app.timeout").toMillis
    )
  }
}
```

### Валидация при загрузке

**Всегда валидируйте конфигурацию при загрузке приложения:**

```scala
def loadAndValidateConfig(): Either[String, AppConfig] = {
  val config = ConfigFactory.load()

  // Проверка обязательных полей
  val requiredFields = List("app.host", "app.port", "database.host")
  val missing = requiredFields.filterNot(config.hasPath)

  if (missing.nonEmpty) {
    Left(s"Missing required configuration fields: ${missing.mkString(", ")}")
  } else {
    Right(AppConfig.fromConfig(config))
  }
}
```

### Использование разных конфигураций для разных окружений

```scala
// application.conf - базовая конфигурация
// application-dev.conf - для development
// application-prod.conf - для production

val environment = sys.env.getOrElse("ENVIRONMENT", "development")
val config = ConfigFactory.load(s"application-$environment.conf")
  .withFallback(ConfigFactory.load("application.conf"))
  .resolve()
```

## Продвинутые техники работы с конфигурацией

### Динамическая конфигурация

Динамическая конфигурация позволяет изменять настройки во время выполнения.

```scala
import com.typesafe.config.ConfigFactory
import scala.concurrent.duration._

// Загрузка конфигурации
val config = ConfigFactory.load()

// Создание наблюдателя за изменениями
class ConfigWatcher(config: Config) {
  private var currentConfig = config

  def watch(path: String)(callback: Config => Unit): Unit = {
    // Реализация наблюдения за изменениями
    // В реальном приложении можно использовать файловые watchers или другие механизмы
  }

  def reload(): Unit = {
    currentConfig = ConfigFactory.load()
  }

  def get(path: String): Option[String] = {
    if (currentConfig.hasPath(path)) Some(currentConfig.getString(path))
    else None
  }
}
```

### Валидация конфигурации

Валидация конфигурации позволяет проверять корректность настроек при загрузке.

```scala
import com.typesafe.config.ConfigFactory
import pureconfig._
import pureconfig.generic.auto._

case class DatabaseConfig(
  host: String,
  port: Int,
  name: String,
  username: String,
  password: String
) {
  require(port > 0 && port < 65536, "Port must be between 1 and 65535")
  require(host.nonEmpty, "Host cannot be empty")
  require(name.nonEmpty, "Database name cannot be empty")
}

case class AppConfig(
  database: DatabaseConfig,
  server: ServerConfig
)

// Загрузка с валидацией
def loadConfig(): Either[String, AppConfig] = {
  ConfigSource.default.load[AppConfig].left.map(_.toString)
}
```

### Безопасное хранение секретов

Безопасное хранение секретов критично для **production** приложений.

```scala
import com.typesafe.config.ConfigFactory

// Загрузка секретов из переменных окружения
val config = ConfigFactory.load()
  .withFallback(ConfigFactory.systemEnvironment())
  .withFallback(ConfigFactory.systemProperties())
  .resolve()

// Использование секретов
val dbPassword = config.getString("database.password")
val apiKey = config.getString("api.key")
```


## Решение проблем

Типичные проблемы и решения см. в официальной документации (блок «Полезные ссылки» в начале документа).

## Частые вопросы

Ответы на частые вопросы по теме см. в разделах «Введение» и «Лучшие практики» в документе.

## Заключение

## Дополнительные техники работы с конфигурацией

### Динамическое обновление конфигурации

Динамическое обновление конфигурации позволяет изменять настройки без перезапуска приложения.

```scala
import com.typesafe.config.ConfigFactory

// Загрузка конфигурации
val config = ConfigFactory.load()

// Обновление конфигурации
val updatedConfig = config.withValue("app.timeout", ConfigValueFactory.fromAnyRef(5000))
```

### Валидация конфигурации

Валидация конфигурации гарантирует корректность настроек.

```scala
import pureconfig._
import pureconfig.generic.auto._

// Валидация конфигурации с использованием PureConfig
case class AppConfig(
  host: String,
  port: Int,
  timeout: Int
)

val config = ConfigSource.default.loadOrThrow[AppConfig]
```

### Безопасное хранение секретов

Безопасное хранение секретов критично для безопасности приложений.

```scala
import com.typesafe.config.ConfigFactory

// Загрузка секретов из переменных окружения
val secretKey = sys.env.getOrElse("SECRET_KEY", "default-key")

// Использование конфигурации для секретов
val config = ConfigFactory.load()
val dbPassword = config.getString("database.password")
```

### Практические примеры: Hot reload конфигурации

```scala
import com.typesafe.config.{Config, ConfigFactory}
import scala.concurrent.duration._
import java.io.File

class ConfigWatcher(configPath: String) {
  private var config: Config = loadConfig()

  def loadConfig(): Config = {
    ConfigFactory.parseFile(new File(configPath))
      .resolve()
  }

  def reload(): Unit = {
    config = loadConfig()
    onConfigChanged(config)
  }

  def getConfig: Config = config

  def onConfigChanged(newConfig: Config): Unit = {
    // Уведомление об изменении конфигурации
    println("Configuration reloaded")
  }
}

// Использование с таймером для проверки изменений
val watcher = new ConfigWatcher("application.conf")
val scheduler = akka.actor.ActorSystem("scheduler")
import scheduler.dispatcher

scheduler.scheduler.schedule(5.seconds, 5.seconds) {
  watcher.reload()
}
```

### Практические примеры: Конфигурация с приоритетами

```scala
import com.typesafe.config.ConfigFactory
import scala.util.Try

case class AppConfig(
  database: DatabaseConfig,
  server: ServerConfig,
  features: FeaturesConfig
)

case class DatabaseConfig(url: String, username: String, password: String)
case class ServerConfig(host: String, port: Int, timeout: Int)
case class FeaturesConfig(enableCache: Boolean, enableMetrics: Boolean)

object AppConfig {
  def load(): AppConfig = {
    // 1. Значения по умолчанию
    val defaultConfig = ConfigFactory.parseString("""
      server {
        host = "localhost"
        port = 8080
        timeout = 30000
      }
      features {
        enableCache = true
        enableMetrics = true
      }
    """)

    // 2. Загрузка из файла
    val fileConfig = Try(ConfigFactory.parseFile(new File("application.conf")))
      .getOrElse(ConfigFactory.empty())

    // 3. Переменные окружения (высший приоритет)
    val envConfig = ConfigFactory.systemEnvironment()

    // Объединение с приоритетами
    val config = envConfig
      .withFallback(fileConfig)
      .withFallback(defaultConfig)
      .resolve()

    AppConfig(
      database = DatabaseConfig(
        url = config.getString("database.url"),
        username = config.getString("database.username"),
        password = config.getString("database.password")
      ),
      server = ServerConfig(
        host = config.getString("server.host"),
        port = config.getInt("server.port"),
        timeout = config.getInt("server.timeout")
      ),
      features = FeaturesConfig(
        enableCache = config.getBoolean("features.enableCache"),
        enableMetrics = config.getBoolean("features.enableMetrics")
      )
    )
  }
}
```

### Практические примеры: Конфигурация с валидацией

```scala
import pureconfig._
import pureconfig.generic.auto._

case class Config(
  database: DatabaseConfig,
  server: ServerConfig
) {
  def validate: Either[List[String], Config] = {
    val errors = scala.collection.mutable.ListBuffer[String]()

    if (database.url.isEmpty) errors += "Database URL is required"
    if (server.port < 1 || server.port > 65535) {
      errors += "Server port must be between 1 and 65535"
    }
    if (server.timeout < 1000) {
      errors += "Server timeout must be at least 1000ms"
    }

    if (errors.isEmpty) Right(this) else Left(errors.toList)
  }
}

def loadAndValidate(): Either[List[String], Config] = {
  ConfigSource.default.load[Config].flatMap(_.validate)
}
```

Конфигурация является важной частью разработки приложений. Понимание **Typesafe Config**, **Play Configuration**, **PureConfig**, работы с переменными окружения, динамической конфигурации, валидации конфигурации, безопасного хранения секретов, динамического обновления конфигурации, **hot reload**, приоритетов конфигурации, валидации конфигурации и практических применений позволяет создавать гибкие, безопасные и надежные системы конфигурации. Правильная организация конфигурации для разных окружений, валидация при загрузке, безопасное хранение секретов, динамическое обновление конфигурации, использование **ConfigFactory** для обновления конфигурации, валидация конфигурации с использованием **PureConfig**, безопасное хранение секретов из переменных окружения, **hot reload** конфигурации и использование приоритетов критичны для **production-ready** приложений. Конфигурация особенно важна для создания приложений, которые должны работать в различных окружениях, адаптироваться к изменениям, обеспечивать безопасность чувствительных данных, динамически обновлять настройки без перезапуска, и валидировать конфигурацию для гарантии корректности.

### Практические примеры: Работа с переменными окружения

```scala
import com.typesafe.config.ConfigFactory

// Загрузка конфигурации с переменными окружения
val config = ConfigFactory.load()

// Переменные окружения имеют приоритет
val dbUrl = sys.env.getOrElse("DATABASE_URL", config.getString("database.url"))
val apiKey = sys.env.getOrElse("API_KEY", config.getString("api.key"))
```

### Практические примеры: Валидация конфигурации

```scala
import pureconfig._
import pureconfig.generic.auto._

case class AppConfig(
  database: DatabaseConfig,
  api: ApiConfig
)

case class DatabaseConfig(
  url: String,
  user: String,
  password: String
)

case class ApiConfig(
  host: String,
  port: Int
)

// Валидация при загрузке
def loadConfig(): Either[ConfigReaderFailures, AppConfig] = {
  ConfigSource.default.load[AppConfig]
}
```

### Использование с различными техниками для динамической конфигурации

```scala
import com.typesafe.config.ConfigFactory

// Динамическое обновление конфигурации
class ConfigManager {
  private var config = ConfigFactory.load()

  def reload(): Unit = {
    config = ConfigFactory.load()
  }

  def getString(path: String): String = {
    config.getString(path)
  }

  def getInt(path: String): Int = {
    config.getInt(path)
  }
}

// Использование
val manager = new ConfigManager
val value = manager.getString("database.url")
manager.reload()  // Обновление конфигурации
```

### Использование с различными техниками для безопасного хранения секретов

```scala
import com.typesafe.config.ConfigFactory

// Безопасное хранение секретов
object SecretManager {
  def getSecret(key: String): String = {
    // Приоритет: переменные окружения > конфигурационный файл
    sys.env.getOrElse(
      key.toUpperCase,
      ConfigFactory.load().getString(s"secrets.$key")
    )
  }
}

// Использование
val dbPassword = SecretManager.getSecret("database.password")
```

## Дополнительные ресурсы

**Для дальнейшего изучения конфигурации в **Scala** рекомендуется:**

- [Typesafe Config Documentation](https://github.com/lightbend/config)
- [PureConfig Documentation](https://pureconfig.github.io/)
- [Play Configuration Documentation](https://www.playframework.com/documentation/latest/Configuration)
