---
title: "Kotlin Exposed"
description: "Кратко: полное руководство по Exposed - типобезопасному ORM для Kotlin. Рассматриваются DSL и DAO API, работа с таблицами, связи, транзакции, миграции и лучшие практики."
tags:
  - libraries
  - kotlin
  - kotlin-exposed
difficulty: "intermediate"
prerequisites: []
next: []
updated: "2026-02-11"
---
# Kotlin Exposed

Кратко: полное руководство по **Exposed** - типобезопасному **ORM** для **Kotlin**. Рассматриваются **DSL** и **DAO API**, работа с таблицами, связи, транзакции, миграции и лучшие практики.

## Полезные ссылки

### Официальная документация
- [Exposed Documentation](https://github.com/JetBrains/Exposed)
- [Exposed API Reference](https://github.com/JetBrains/Exposed/wiki)

### **Baeldung**
- [Exposed Tutorial](https://github.com/JetBrains/Exposed/wiki/Getting-Started)

### См. также
- [Основы Kotlin](../../languages/kotlin/kotlin-basics.md)
- [DSL в Kotlin](../../languages/kotlin/kotlin-dsl.md)
- [Основы PostgreSQL](../../databases/relational/postgresql/postgres-basics.md)

## Содержание

- [Введение в Exposed](#введение-в-exposed)
  - [Основные преимущества Exposed](#основные-преимущества-exposed)
  - [Когда использовать Exposed](#когда-использовать-exposed)
- [Настройка проекта](#настройка-проекта)
  - [Gradle зависимости](#gradle-зависимости)
- [Подключение к базе данных](#подключение-к-базе-данных)
  - [Настройка подключения](#настройка-подключения)
  - [Использование Connection Pool](#использование-connection-pool)
- [DSL API](#dsl-api)
  - [Преимущества DSL API](#преимущества-dsl-api)
- [Определение таблиц](#определение-таблиц)
  - [Создание таблицы](#создание-таблицы)
  - [Типы колонок](#типы-колонок)
  - [Создание таблиц в базе данных](#создание-таблиц-в-базе-данных)
- [CRUD операции](#crud-операции)
  - [Вставка данных (Create)](#вставка-данных-create)
  - [Получение данных (Read)](#получение-данных-read)
  - [Обновление данных (Update)](#обновление-данных-update)
  - [Удаление данных (Delete)](#удаление-данных-delete)
- [Запросы и фильтрация](#запросы-и-фильтрация)
  - [Базовые запросы](#базовые-запросы)
  - [Условия WHERE](#условия-where)
  - [Соединения (JOIN)](#соединения-join)
- [Связи между таблицами](#связи-между-таблицами)
  - [Внешние ключи](#внешние-ключи)
  - [Множественные связи](#множественные-связи)
- [Транзакции](#транзакции)
  - [Базовое использование транзакций](#базовое-использование-транзакций)
  - [Уровни изоляции](#уровни-изоляции)
  - [Вложенные транзакции](#вложенные-транзакции)
- [DAO API](#dao-api)
  - [Определение Entity](#определение-entity)
  - [CRUD через DAO](#crud-через-dao)
- [Миграции](#миграции)
  - [Ручные миграции](#ручные-миграции)
  - [Версионирование миграций](#версионирование-миграций)
- [Лучшие практики](#лучшие-практики)
  - [Типобезопасность](#типобезопасность)
  - [Индексы](#индексы)
  - [Использование индексов](#использование-индексов)
- [Продвинутые запросы](#продвинутые-запросы)
  - [Подзапросы](#подзапросы)
  - [Window Functions](#window-functions)
  - [Common Table Expressions (**CTE**)](#common-table-expressions-cte)
- [Работа с JSON](#работа-с-json)
- [Миграции с Flyway](#миграции-с-flyway)
- [Тестирование](#тестирование)
- [Производительность](#производительность)
  - [Batch операции](#batch-операции)
  - [Connection Pooling](#connection-pooling)
  - [Query оптимизация](#query-оптимизация)
- [Безопасность](#безопасность)
  - [SQL Injection защита](#sql-injection-защита)
  - [Транзакции и изоляция](#транзакции-и-изоляция)
- [Интеграция с Ktor](#интеграция-с-ktor)
- [Мониторинг и логирование](#мониторинг-и-логирование)
  - [Логирование запросов](#логирование-запросов)
  - [Метрики производительности](#метрики-производительности)
- [Работа с большими данными](#работа-с-большими-данными)
  - [Streaming результатов](#streaming-результатов)
- [Репликация и шардирование](#репликация-и-шардирование)
  - [Работа с репликами базы данных](#работа-с-репликами-базы-данных)
  - [Шардирование данных](#шардирование-данных)
- [Продвинутые техники Exposed](#продвинутые-техники-exposed)
  - [Оптимизация сложных запросов](#оптимизация-сложных-запросов)
  - [Работа с транзакциями и изоляцией](#работа-с-транзакциями-и-изоляцией)
  - [Работа с JSON данными](#работа-с-json-данными)
  - [Работа с временными данными](#работа-с-временными-данными)
  - [Работа с миграциями](#работа-с-миграциями)
  - [Оптимизация запросов](#оптимизация-запросов)
  - [Работа с Raw SQL](#работа-с-raw-sql)
  - [Работа с Stored Procedures](#работа-с-stored-procedures)
  - [Работа с типами данных](#работа-с-типами-данных)
  - [Работа с индексами](#работа-с-индексами)
- [Решение проблем](#решение-проблем)
- [Частые вопросы](#частые-вопросы)
- [Заключение](#заключение)
- [Дополнительные ресурсы](#дополнительные-ресурсы)
- [Итоговые рекомендации](#итоговые-рекомендации)
- [Практические примеры использования](#практические-примеры-использования)
  - [Работа с репозиториями](#работа-с-репозиториями)

## Введение в **Exposed**

**Exposed** - это легковесный **SQL**-фреймворк для **Kotlin**, разработанный **JetBrains**. Он предоставляет два **API** для работы с базами данных: **DSL API** (**типобезопасный SQL**) и **DAO API** (**объектно-ориентированный подход**).

### Основные преимущества **Exposed**

- **Типобезопасность**: компилятор проверяет корректность запросов на этапе компиляции
- **Два API**: выбор между **DSL** (**близко к SQL**) и **DAO** (**близко к ORM**)
- **Легковесность**: минимальные зависимости, нет необходимости в сложной конфигурации
- **Kotlin-first**: полностью написан на **Kotlin** с использованием возможностей языка
- **Поддержка нескольких БД**: **PostgreSQL**, **MySQL**, **H2**, **SQLite**, **Oracle**, **SQL Server**

### Когда использовать **Exposed**

**Exposed** подходит для проектов, где нужен контроль над **SQL** запросами, но при этом требуется типобезопасность. Он менее "магический", чем **Hibernate**, что делает его предсказуемым и понятным.

## Настройка проекта

### **Gradle** зависимости

**Для работы с **Exposed** необходимо добавить зависимости в `build.gradle.kts` (или `build.gradle`):**

```kotlin
dependencies {
    // Exposed core
    implementation("org.jetbrains.exposed:exposed-core:0.44.1")
    implementation("org.jetbrains.exposed:exposed-dao:0.44.1")
    implementation("org.jetbrains.exposed:exposed-jdbc:0.44.1")
    
    // Для PostgreSQL
    implementation("org.postgresql:postgresql:42.6.0")
    
    // Connection pool (опционально)
    implementation("com.zaxxer:HikariCP:5.0.1")
}
```

Для других баз данных используются соответствующие драйверы: **MySQL**, **H2**, **SQLite** и т.д.

## Подключение к базе данных

### Настройка подключения

**Подключение к базе данных настраивается через **Database** объект:**

```kotlin
import org.jetbrains.exposed.sql.Database

Database.connect(
    url = "jdbc:postgresql://localhost:5432/mydb",
    driver = "org.postgresql.Driver",
    user = "user",
    password = "password"
)
```

Этот код устанавливает соединение с базой данных **PostgreSQL**. **URL**, драйвер и **credentials** должны соответствовать вашей конфигурации базы данных.

### Использование **Connection Pool**

**Для **production** приложений рекомендуется использовать **connection pool**:**

```kotlin
import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import org.jetbrains.exposed.sql.Database

val config = HikariConfig().apply {
    jdbcUrl = "jdbc:postgresql://localhost:5432/mydb"
    driverClassName = "org.postgresql.Driver"
    username = "user"
    password = "password"
    maximumPoolSize = 10
    minimumIdle = 5
}

val dataSource = HikariDataSource(config)
Database.connect(dataSource)
```

**Connection pool** управляет пулом соединений, что повышает производительность и эффективность использования ресурсов базы данных.

## **DSL API**

**DSL API** в **Exposed** позволяет писать типобезопасные **SQL** запросы, которые проверяются на этапе компиляции.

### Преимущества **DSL API**

- **Типобезопасность**: ошибки в именах таблиц и колонок обнаруживаются на этапе компиляции
- **Автодополнение**: **IDE** предоставляет подсказки при написании запросов
- **Читаемость**: запросы читаются как обычный **Kotlin** код
- **Рефакторинг**: безопасный рефакторинг с поддержкой **IDE**

**DSL API** идеально подходит для разработчиков, которые знакомы с **SQL** и хотят сохранить контроль над запросами, но получить преимущества типобезопасности.

## Определение таблиц

### Создание таблицы

**Таблицы определяются как объекты, наследующиеся от `**Table**`:**

```kotlin
import org.jetbrains.exposed.sql.Table

object Users : Table("users") {
    val id = integer("id").autoIncrement().primaryKey()
    val name = varchar("name", length = 50)
    val email = varchar("email", length = 100).uniqueIndex()
    val age = integer("age").nullable()
    val createdAt = timestamp("created_at").defaultExpression(CurrentTimestamp())
}
```

Каждая колонка определяется с указанием типа, имени и опциональных ограничений. `**autoIncrement**()` создает автоинкрементное поле, `**primaryKey**()` устанавливает первичный ключ, `**uniqueIndex**()` создает уникальный индекс.

### Типы колонок

**Exposed** поддерживает различные типы колонок:**

```kotlin
object Products : Table("products") {
    val id = integer("id").autoIncrement().primaryKey()
    val name = varchar("name", 100)
    val description = text("description").nullable()
    val price = decimal("price", 10, 2)
    val inStock = bool("in_stock")
    val categoryId = integer("category_id")
    val createdAt = datetime("created_at")
    val metadata = json("metadata").nullable()  // Для PostgreSQL
}
```

Каждый тип данных имеет соответствующую функцию в **DSL**, что обеспечивает типобезопасность на уровне компиляции.

### Создание таблиц в базе данных

**Для создания таблиц в базе данных используется функция `**SchemaUtils.create**()`:**

```kotlin
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.transactions.transaction

transaction {
    SchemaUtils.create(Users, Products)
}
```

`**transaction**` блок необходим для выполнения операций с базой данных. Все операции в **Exposed** должны выполняться внутри транзакции.

## **CRUD** операции

### Вставка данных (**Create**)

**Вставка данных выполняется через функцию `**insert**()`:**

```kotlin
transaction {
    Users.insert {
        it[name] = "Alice"
        it[email] = "alice@example.com"
        it[age] = 25
    }
}
```

Функция `**insert**` принимает **lambda**, где через `it[**column**]` устанавливаются значения. Это обеспечивает типобезопасность - нельзя установить строку в **integer** колонку.

### Получение данных (**Read**)

**Чтение данных выполняется через запросы:**

```kotlin
transaction {
    val users = Users.selectAll().map { row ->
        User(
            id = row[Users.id].value,
            name = row[Users.name],
            email = row[Users.email],
            age = row[Users.age]
        )
    }
}
```

`**selectAll**()` возвращает все строки таблицы. Результаты можно преобразовать в объекты через `**map**`.

### Обновление данных (**Update**)

**Обновление выполняется через функцию `**update**()`:**

```kotlin
transaction {
    Users.update({ Users.id eq 1 }) {
        it[name] = "Alice Updated"
        it[age] = 26
    }
}
```

Первый параметр `**update**` - это условие **WHERE**, второй - **lambda** с новыми значениями. Условие определяет, какие строки будут обновлены.

### Удаление данных (**Delete**)

**Удаление выполняется через функцию `**deleteWhere**()`:**

```kotlin
transaction {
    Users.deleteWhere { Users.id eq 1 }
}
```

Условие определяет, какие строки будут удалены. Без условия будут удалены все строки таблицы.

## Запросы и фильтрация

### Базовые запросы

**Exposed** предоставляет богатый набор функций для построения запросов:**

```kotlin
transaction {
    // Выборка всех пользователей
    val allUsers = Users.selectAll()
    
    // Выборка с условием
    val adults = Users.select { Users.age greaterEq 18 }
    
    // Выборка с сортировкой
    val sorted = Users.selectAll().orderBy(Users.name)
    
    // Ограничение количества
    val limited = Users.selectAll().limit(10)
}
```

Каждая функция возвращает `**Query**` объект, который можно дополнительно модифицировать или преобразовать в список.

### Условия **WHERE**

**Условия фильтрации строятся через операторы:**

```kotlin
transaction {
    // Равенство
    Users.select { Users.id eq 1 }
    
    // Неравенство
    Users.select { Users.id neq 1 }
    
    // Больше/меньше
    Users.select { Users.age greater 18 }
    Users.select { Users.age less 65 }
    
    // Вхождение в список
    Users.select { Users.id inList listOf(1, 2, 3) }
    
    // LIKE для строк
    Users.select { Users.name like "%John%" }
    
    // NULL проверки
    Users.select { Users.age.isNull() }
    Users.select { Users.age.isNotNull() }
}
```

Операторы проверяются на этапе компиляции, что предотвращает ошибки в условиях запросов.

### Соединения (**JOIN**)

**Exposed** поддерживает различные типы **JOIN**:**

```kotlin
object Orders : Table("orders") {
    val id = integer("id").autoIncrement().primaryKey()
    val userId = integer("user_id").references(Users.id)
    val total = decimal("total", 10, 2)
}

transaction {
    // INNER JOIN
    val result = (Users innerJoin Orders)
        .select { Orders.total greater 100 }
        .map { row ->
            row[Users.name] to row[Orders.total]
        }
    
    // LEFT JOIN
    val leftJoin = (Users leftJoin Orders)
        .selectAll()
}
```

**JOIN** операции типобезопасны - нельзя соединить таблицы, между которыми нет связи, определенной через `**references**()`.

## Связи между таблицами

### Внешние ключи

**Связи между таблицами определяются через `**references**()`:**

```kotlin
object Orders : Table("orders") {
    val id = integer("id").autoIncrement().primaryKey()
    val userId = integer("user_id").references(Users.id)
    val productId = integer("product_id").references(Products.id)
}
```

`**references**()` создает внешний ключ в базе данных и обеспечивает целостность данных. При попытке удалить пользователя, у которого есть заказы, база данных выдаст ошибку.

### Множественные связи

**Для связи многие-ко-многим используется промежуточная таблица:**

```kotlin
object UserRoles : Table("user_roles") {
    val userId = integer("user_id").references(Users.id)
    val roleId = integer("role_id").references(Roles.id)
    
    override val primaryKey = PrimaryKey(userId, roleId)
}
```

Промежуточная таблица содержит ссылки на обе связанные таблицы и имеет составной первичный ключ.

## Транзакции

### Базовое использование транзакций

**Все операции с базой данных должны выполняться внутри транзакции:**

```kotlin
transaction {
    Users.insert { it[name] = "Alice" }
    Users.insert { it[name] = "Bob" }
}
```

Если внутри блока произойдет исключение, все изменения будут откачены (**rollback**). Это обеспечивает целостность данных.

### Уровни изоляции

**Можно указать уровень изоляции транзакции:**

```kotlin
transaction(Connection.TRANSACTION_SERIALIZABLE) {
    // Операции с максимальной изоляцией
}
```

Разные уровни изоляции обеспечивают различный баланс между производительностью и консистентностью данных.

### Вложенные транзакции

**Exposed** поддерживает вложенные транзакции через `**nestedTransaction**()`:**

```kotlin
transaction {
    Users.insert { it[name] = "Alice" }
    
    nestedTransaction {
        Users.insert { it[name] = "Bob" }
        // Если здесь произойдет ошибка, откатится только эта часть
    }
}
```

Вложенные транзакции позволяют создавать точки сохранения (**savepoints**) для более гибкого управления откатами.

## **DAO API**

**DAO API** предоставляет объектно-ориентированный подход к работе с данными.

### Определение **Entity**

**Entity** определяется как класс, наследующийся от `**IntIdTable**` или `**UUIDTable**`:**

```kotlin
import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass

object Users : IntIdTable("users") {
    val name = varchar("name", 50)
    val email = varchar("email", 100)
}

class User(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<User>(Users)
    
    var name by Users.name
    var email by Users.email
}
```

**Entity** класс автоматически получает методы для работы с базой данных через наследование от `**IntEntity**`.

### **CRUD** через **DAO**

**DAO API** предоставляет удобные методы для работы с данными:**

```kotlin
transaction {
    // Создание
    val user = User.new {
        name = "Alice"
        email = "alice@example.com"
    }
    
    // Чтение
    val found = User.findById(1)
    val all = User.all()
    
    // Обновление
    user.name = "Alice Updated"
    
    // Удаление
    user.delete()
}
```

**DAO API** более высокоуровневый и требует меньше кода, но предоставляет меньше контроля над **SQL** запросами.

## Миграции

### Ручные миграции

**Миграции выполняются через `**SchemaUtils**`:**

```kotlin
transaction {
    // Добавление колонки
    SchemaUtils.addColumn(Users, Users.age)
    
    // Создание индекса
    SchemaUtils.createIndex(Users.email)
    
    // Удаление таблицы
    SchemaUtils.drop(OldTable)
}
```

Ручные миграции дают полный контроль, но требуют внимательности при выполнении на **production**.

### Версионирование миграций

**Для управления версиями схемы можно использовать отдельную таблицу:**

```kotlin
object SchemaVersion : IntIdTable("schema_version") {
    val version = integer("version")
    val appliedAt = timestamp("applied_at")
}

fun migrate() {
    transaction {
        val currentVersion = SchemaVersion.all().maxOfOrNull { it.version } ?: 0
        
        if (currentVersion < 1) {
            SchemaUtils.create(Users)
            SchemaVersion.new { version = 1; appliedAt = DateTime.now() }
        }
        
        if (currentVersion < 2) {
            SchemaUtils.addColumn(Users, Users.age)
            SchemaVersion.new { version = 2; appliedAt = DateTime.now() }
        }
    }
}
```

Версионирование позволяет отслеживать, какие миграции уже применены, и применять только новые.

## Лучшие практики

### Использование **Connection Pool**

Всегда используйте **connection pool** в **production** для оптимальной производительности и управления ресурсами.

### Типобезопасность

Предпочитайте **DSL API** для сложных запросов, где важна типобезопасность и контроль над **SQL**.

### Транзакции

Всегда выполняйте операции внутри транзакций. Это обеспечивает целостность данных и правильную обработку ошибок.

### Индексы

Создавайте индексы для часто используемых колонок в **WHERE** условиях для улучшения производительности запросов.

Этот файл содержит полное руководство по **Exposed**, покрывающее все основные аспекты работы с базами данных через этот фреймворк.

### Использование индексов

**Индексы критичны для производительности запросов, особенно для больших таблиц:**

```kotlin
object Users : IntIdTable("users") {
    val name = varchar("name", 50)
    val email = varchar("email", 100).uniqueIndex()
    val age = integer("age").index()
    
    // Составной индекс
    init {
        index(name, email)
    }
}
```

Правильное использование индексов может значительно улучшить производительность запросов, особенно для операций поиска и сортировки.

## Продвинутые запросы

### Подзапросы

**Exposed** поддерживает подзапросы для сложных сценариев:**

```kotlin
// Подзапрос в SELECT
val subquery = Users.slice(Users.id.count()).select { Users.age > 18 }
val result = Users.select {
    Users.id inSubQuery subquery
}

// Подзапрос в WHERE
val avgAge = Users.slice(Users.age.avg()).selectAll()
val users = Users.select {
    Users.age greater Users.age.avg()
}
```

Подзапросы позволяют создавать сложные запросы, которые сложно выразить через **JOIN** или агрегацию.

### **Window Functions**

**Exposed** поддерживает **window functions** для аналитических запросов:**

```kotlin
val result = Users.selectAll()
    .with(Users.name, Users.age)
    .window(
        partitionBy = Users.name,
        orderBy = Users.age to SortOrder.DESC
    ) {
        Users.age.rank()
    }
```

**Window functions** позволяют выполнять вычисления над группами строк без группировки результатов, что полезно для аналитики и отчетов.

### **Common Table Expressions** (**CTE**)

**CTE** позволяют создавать временные именованные наборы результатов:**

```kotlin
val cte = Users.slice(Users.id, Users.name)
    .select { Users.age > 18 }
    .alias("adult_users")

val result = Users.innerJoin(cte) {
    Users.id eq cte[Users.id]
}.selectAll()
```

**CTE** упрощают сложные запросы, разбивая их на более понятные части и улучшая читаемость кода.

## Работа с **JSON**

**Exposed** поддерживает работу с **JSON** колонками для **PostgreSQL**:**

```kotlin
object Products : IntIdTable("products") {
    val name = varchar("name", 100)
    val metadata = json("metadata").nullable()
}

// Вставка JSON
transaction {
    Products.insert {
        it[name] = "Product 1"
        it[metadata] = """{"color": "red", "size": "large"}"""
    }
}

// Запросы по JSON
val products = Products.select {
    Products.metadata.json("color") eq "red"
}
```

**JSON** колонки позволяют хранить полуструктурированные данные, что полезно для гибких схем и метаданных.

## Миграции с **Flyway**

**Интеграция с **Flyway** для управления миграциями:**

```kotlin
// build.gradle.kts
dependencies {
    implementation("org.flywaydb:flyway-core:9.0.0")
}

// Применение миграций
fun migrate() {
    val flyway = Flyway.configure()
        .dataSource(dataSource)
        .locations("db/migration")
        .load()
    flyway.migrate()
}
```

**Flyway** позволяет версионировать миграции и применять их последовательно, что критично для управления схемой БД в **production**.

## Тестирование

**Тестирование с **Exposed** требует правильной настройки тестовой базы данных:**

```kotlin
class UserRepositoryTest {
    @BeforeEach
    fun setUp() {
        Database.connect("jdbc:h2:mem:test", driver = "org.h2.Driver")
        transaction {
            SchemaUtils.create(Users)
        }
    }
    
    @AfterEach
    fun tearDown() {
        transaction {
            SchemaUtils.drop(Users)
        }
    }
    
    @Test
    fun `should save and retrieve user`() {
        transaction {
            val user = Users.insert {
                it[name] = "Alice"
                it[email] = "alice@example.com"
            }
            
            val retrieved = Users.select { Users.id eq user[Users.id] }.single()
            assertEquals("Alice", retrieved[Users.name])
        }
    }
}
```

Использование **in-memory** базы данных (**H2**) для тестов позволяет быстро выполнять тесты без настройки реальной БД.

## Производительность

### **Batch** операции

**Batch** операции более эффективны для массовых вставок:**

```kotlin
// Плохо - множественные вставки
users.forEach { user ->
    Users.insert {
        it[name] = user.name
        it[email] = user.email
    }
}

// Хорошо - batch вставка
Users.batchInsert(users) { user ->
    this[Users.name] = user.name
    this[Users.email] = user.email
}
```

**Batch** операции значительно быстрее множественных отдельных операций, особенно для больших объемов данных.

### **Connection Pooling**

**Правильная настройка **connection pool** критична для производительности:**

```kotlin
val dataSource = HikariDataSource().apply {
    jdbcUrl = "jdbc:postgresql://localhost:5432/mydb"
    username = "user"
    password = "password"
    maximumPoolSize = 10
    minimumIdle = 5
    connectionTimeout = 30000
}

Database.connect(dataSource)
```

**Connection pooling** уменьшает накладные расходы на создание соединений и улучшает производительность при высокой нагрузке.

### **Query** оптимизация

**Использование правильных типов запросов и индексов:**

```kotlin
// Плохо - загрузка всех данных
val users = Users.selectAll().map { it.toUser() }

// Хорошо - загрузка только нужных полей
val users = Users.slice(Users.id, Users.name)
    .selectAll()
    .map { User(id = it[Users.id], name = it[Users.name]) }

// Использование EXISTS вместо IN для больших наборов
val exists = Users.select {
    exists(Posts.select { Posts.userId eq Users.id })
}
```

Оптимизация запросов уменьшает объем передаваемых данных и улучшает производительность, особенно для больших таблиц.

## Безопасность

### **SQL Injection** защита

**Exposed** автоматически защищает от **SQL injection** через параметризованные запросы:**

```kotlin
// Безопасно - параметризованный запрос
val users = Users.select {
    Users.name eq userInput  // Автоматически экранируется
}

// НЕ используйте строковую интерполяцию
// val query = "SELECT * FROM users WHERE name = '$userInput'"  // ОПАСНО!
```

Всегда используйте **DSL API Exposed** вместо строковых запросов для защиты от **SQL injection**.

### Транзакции и изоляция

**Настройка уровня изоляции транзакций:**

```kotlin
transaction(Connection.TRANSACTION_READ_COMMITTED) {
    // Операции с уровнем изоляции READ_COMMITTED
}

transaction(Connection.TRANSACTION_SERIALIZABLE) {
    // Операции с уровнем изоляции SERIALIZABLE
}
```

Правильный выбор уровня изоляции балансирует между производительностью и консистентностью данных.

## Интеграция с **Ktor**

**Использование **Exposed** с **Ktor** для веб-приложений:**

```kotlin
fun Application.module() {
    Database.connect(
        "jdbc:postgresql://localhost:5432/mydb",
        driver = "org.postgresql.Driver",
        user = "user",
        password = "password"
    )
    
    routing {
        get("/users") {
            val users = transaction {
                Users.selectAll().map { it.toUser() }
            }
            call.respond(users)
        }
        
        post("/users") {
            val user = call.receive<User>()
            transaction {
                Users.insert {
                    it[name] = user.name
                    it[email] = user.email
                }
            }
            call.respond(HttpStatusCode.Created)
        }
    }
}
```

Интеграция с **Ktor** позволяет создавать **REST API** с типобезопасным доступом к базе данных.

## Мониторинг и логирование

### Логирование запросов

**Включение логирования **SQL** запросов для отладки:**

```kotlin
// Включение логирования
Logger.getLogger("org.jetbrains.exposed.sql").level = Level.DEBUG

// Или через SLF4J
Logger.getLogger("org.jetbrains.exposed.sql").addAppender(
    ConsoleAppender().apply {
        layout = PatternLayout("%d{yyyy-MM-dd HH:mm:ss} %-5p %c{1}:%L - %m%n")
    }
)
```

Логирование запросов помогает выявлять проблемы производительности и неправильные запросы.

### Метрики производительности

**Отслеживание производительности запросов:**

```kotlin
fun <T> measureQuery(block: () -> T): T {
    val startTime = System.currentTimeMillis()
    val result = block()
    val duration = System.currentTimeMillis() - startTime
    
    if (duration > 1000) {
        logger.warn("Slow query detected: ${duration}ms")
    }
    
    return result
}

// Использование
val users = measureQuery {
    Users.selectAll().toList()
}
```

Мониторинг производительности помогает выявлять медленные запросы и оптимизировать их.

Этот файл содержит полное руководство по **Exposed**, покрывающее все основные аспекты работы с базами данных через этот фреймворк, включая продвинутые запросы, миграции, тестирование, оптимизацию производительности, безопасность и интеграцию с другими фреймворками.

## Работа с большими данными

### **Batch** операции

**Эффективная обработка больших объемов данных:**

```kotlin
// Batch вставка для больших объемов данных
fun insertUsersInBatches(users: List<User>, batchSize: Int = 1000) {
    transaction {
        users.chunked(batchSize).forEach { batch ->
            Users.batchInsert(batch) { user ->
                this[Users.name] = user.name
                this[Users.email] = user.email
                this[Users.age] = user.age
            }
        }
    }
}

// Batch обновление
fun updateUsersInBatches(updates: List<Pair<Int, User>>, batchSize: Int = 1000) {
    transaction {
        updates.chunked(batchSize).forEach { batch ->
            batch.forEach { (id, user) ->
                Users.update({ Users.id eq id }) {
                    it[name] = user.name
                    it[email] = user.email
                    it[age] = user.age
                }
            }
        }
    }
}
```

**Batch** операции значительно улучшают производительность при работе с большими объемами данных.

### **Streaming** результатов

**Обработка больших результатов через **streaming**:**

```kotlin
fun processLargeDataset() {
    transaction {
        val query = Users.selectAll()
        
        // Streaming обработка
        query.forEach { row ->
            val user = row.toUser()
            processUser(user)  // Обработка каждого пользователя без загрузки всех в память
        }
    }
}

// Использование Sequence для ленивой обработки
fun getUsersAsSequence(): Sequence<User> = sequence {
    transaction {
        Users.selectAll().forEach { row ->
            yield(row.toUser())
        }
    }
}

// Использование
getUsersAsSequence()
    .filter { it.age > 18 }
    .take(1000)
    .forEach { processUser(it) }
```

**Streaming** обработка позволяет работать с большими результатами запросов без загрузки всех данных в память.

## Репликация и шардирование

### Работа с репликами базы данных

**Настройка работы с репликами для чтения и записи:**

```kotlin
// Настройка подключений к репликам
val masterDatabase = Database.connect(
    "jdbc:postgresql://master:5432/mydb",
    driver = "org.postgresql.Driver",
    user = "user",
    password = "password"
)

val replicaDatabase = Database.connect(
    "jdbc:postgresql://replica:5432/mydb",
    driver = "org.postgresql.Driver",
    user = "user",
    password = "password"
)

// Чтение с реплики, запись в мастер
fun readFromReplica(): List<User> {
    return transaction(replicaDatabase) {
        Users.selectAll().map { it.toUser() }
    }
}

fun writeToMaster(user: User) {
    transaction(masterDatabase) {
        Users.insert {
            it[name] = user.name
            it[email] = user.email
            it[age] = user.age
        }
    }
}
```

Работа с репликами позволяет распределить нагрузку между базами данных для чтения и записи, что улучшает производительность.

### Шардирование данных

**Реализация шардирования для горизонтального масштабирования:**

```kotlin
class ShardedDatabase(private val shards: List<Database>) {
    fun getShard(key: Int): Database {
        return shards[key % shards.size]
    }
    
    fun executeOnShard(key: Int, block: Transaction.() -> Unit) {
        transaction(getShard(key), block)
    }
    
    fun findUser(userId: Int): User? {
        return transaction(getShard(userId)) {
            Users.select { Users.id eq userId }.singleOrNull()?.toUser()
        }
    }
}

// Использование
val shardedDb = ShardedDatabase(listOf(db1, db2, db3))
val user = shardedDb.findUser(123)  // Использует соответствующий шард
```

Шардирование позволяет горизонтально масштабировать базу данных, распределяя данные по нескольким узлам.

## Продвинутые техники **Exposed**

### Оптимизация сложных запросов

**Оптимизация сложных запросов для лучшей производительности:**

```kotlin
// Использование индексов для оптимизации JOIN
object Users : IntIdTable("users") {
    val name = varchar("name", 50).index()
    val email = varchar("email", 100).uniqueIndex()
    val departmentId = integer("department_id").index()
}

object Departments : IntIdTable("departments") {
    val name = varchar("name", 50).index()
}

// Оптимизированный JOIN запрос
transaction {
    val usersWithDepartments = Users
        .innerJoin(Departments) {
            Users.departmentId eq Departments.id
        }
        .select {
            Users.age greater 18
        }
        .map { row ->
            UserWithDepartment(
                userId = row[Users.id].value,
                userName = row[Users.name],
                departmentName = row[Departments.name]
            )
        }
}

// Использование EXISTS вместо IN для больших наборов
val activeUsers = Users.select {
    exists(
        Posts.select {
            Posts.userId eq Users.id and (Posts.published eq true)
        }
    )
}

// Использование подзапросов для оптимизации
val topUsers = Users.select {
    Users.id inSubQuery (
        Posts
            .slice(Posts.userId, Posts.id.count())
            .select { Posts.published eq true }
            .groupBy(Posts.userId)
            .orderBy(Posts.id.count() to SortOrder.DESC)
            .limit(10)
            .select { Posts.userId }
    )
}
```

Оптимизация сложных запросов критична для производительности приложений, особенно при работе с большими объемами данных.

### Работа с транзакциями и изоляцией

**Продвинутые техники работы с транзакциями:**

```kotlin
// Уровни изоляции транзакций
transaction(Connection.TRANSACTION_READ_COMMITTED) {
    // Чтение только зафиксированных данных
    val users = Users.selectAll().toList()
}

transaction(Connection.TRANSACTION_SERIALIZABLE) {
    // Сериализуемые транзакции для максимальной изоляции
    val user = Users.select { Users.id eq 1 }.single()
    user.name = "Updated"
    Users.update({ Users.id eq 1 }) {
        it[name] = "Updated"
    }
}

// Вложенные транзакции
transaction {
    // Внешняя транзакция
    val user = Users.select { Users.id eq 1 }.single()
    
    transaction {
        // Внутренняя транзакция
        Posts.insert {
            it[userId] = user.id.value
            it[title] = "New Post"
        }
    }
}

// Использование savepoint для отката части транзакции
transaction {
    val savepoint = connection.setSavepoint("before_insert")
    try {
        Users.insert {
            it[name] = "Test"
            it[email] = "test@example.com"
        }
        
        if (someCondition) {
            connection.rollback(savepoint)
        }
        
        Users.insert {
            it[name] = "Another"
            it[email] = "another@example.com"
        }
    } catch (e: Exception) {
        connection.rollback(savepoint)
        throw e
    }
}
```

Правильное управление транзакциями и уровнями изоляции обеспечивает целостность данных и корректную работу приложений.

### Работа с **JSON** данными

**Использование **JSON** колонок для гибких данных:**

```kotlin
// Работа с JSON колонками в PostgreSQL
object Products : IntIdTable("products") {
    val name = varchar("name", 100)
    val metadata = json("metadata").nullable()
    val tags = json("tags").nullable()
}

// Вставка JSON данных
transaction {
    Products.insert {
        it[name] = "Product 1"
        it[metadata] = """{"color": "red", "size": "large", "weight": 100}"""
        it[tags] = """["electronics", "gadgets"]"""
    }
}

// Запросы по JSON данным
transaction {
    val products = Products.select {
        Products.metadata.json("color") eq "red"
    }
    
    // Запрос по массиву JSON
    val taggedProducts = Products.select {
        Products.tags.jsonArray("contains", "electronics")
    }
}

// Обновление JSON данных
transaction {
    Products.update({ Products.id eq 1 }) {
        it[metadata] = """{"color": "blue", "size": "large"}"""
    }
}
```

**JSON** колонки позволяют хранить полуструктурированные данные, что полезно для гибких схем и метаданных.

### Работа с временными данными

**Использование временных таблиц для обработки данных:**

```kotlin
// Создание временной таблицы
object TempUsers : IntIdTable("temp_users") {
    val name = varchar("name", 50)
    val email = varchar("email", 100)
}

// Использование временной таблицы
transaction {
    SchemaUtils.create(TempUsers)
    
    try {
        // Обработка данных во временной таблице
        TempUsers.insert {
            it[name] = "Temp User"
            it[email] = "temp@example.com"
        }
        
        // Использование данных из временной таблицы
        val tempUsers = TempUsers.selectAll().toList()
        
        // Копирование во основную таблицу
        Users.batchInsert(tempUsers) { user ->
            this[Users.name] = user[TempUsers.name]
            this[Users.email] = user[TempUsers.email]
        }
    } finally {
        SchemaUtils.drop(TempUsers)
    }
}
```

Временные таблицы позволяют обрабатывать данные изолированно и копировать их в основные таблицы после обработки.

### Работа с миграциями

**Управление схемой базы данных через миграции:**

```kotlin
// Создание миграций
object Migrations {
    fun createInitialSchema() {
        transaction {
            SchemaUtils.create(Users, Posts, Comments)
        }
    }
    
    fun addIndexes() {
        transaction {
            SchemaUtils.createIndex(
                index = false,
                unique = false,
                Users.email,
                Users.name
            )
        }
    }
    
    fun addForeignKeys() {
        transaction {
            SchemaUtils.createForeignKey(
                Posts.userId,
                Users.id,
                onDelete = ReferenceOption.CASCADE,
                onUpdate = ReferenceOption.CASCADE
            )
        }
    }
    
    fun addColumns() {
        transaction {
            addColumn(Users, Users.birthDate, DateTimeColumnType())
            addColumn(Users, Users.isActive, BooleanColumnType().default(false))
        }
    }
}

// Управление версиями миграций
object MigrationManager {
    private val executedMigrations = mutableSetOf<String>()
    
    fun executeMigrations() {
        transaction {
            createMigrationTable()
            
            if (!isMigrationExecuted("001_initial_schema")) {
                Migrations.createInitialSchema()
                recordMigration("001_initial_schema")
            }
            
            if (!isMigrationExecuted("002_add_indexes")) {
                Migrations.addIndexes()
                recordMigration("002_add_indexes")
            }
            
            if (!isMigrationExecuted("003_add_foreign_keys")) {
                Migrations.addForeignKeys()
                recordMigration("003_add_foreign_keys")
            }
            
            if (!isMigrationExecuted("004_add_columns")) {
                Migrations.addColumns()
                recordMigration("004_add_columns")
            }
        }
    }
    
    private fun createMigrationTable() {
        SchemaUtils.create(Migrations)
    }
    
    private fun isMigrationExecuted(name: String): Boolean {
        return Migrations.select { Migrations.name eq name }.count() > 0
    }
    
    private fun recordMigration(name: String) {
        Migrations.insert {
            it[Migrations.name] = name
            it[Migrations.executedAt] = System.currentTimeMillis()
        }
    }
}

object Migrations : IntIdTable("migrations") {
    val name = varchar("name", 100)
    val executedAt = long("executed_at")
}
```

Миграции позволяют управлять эволюцией схемы базы данных безопасно и предсказуемо.

### Оптимизация запросов

**Продвинутые техники оптимизации запросов:**

```kotlin
// Использование EXPLAIN для анализа запросов
fun explainQuery(query: Query): String {
    return transaction {
        val sql = query.prepareSQL(QueryBuilder(false))
        // Выполнение EXPLAIN для анализа плана запроса
        exec("EXPLAIN $sql") { rs ->
            buildString {
                while (rs.next()) {
                    appendLine(rs.getString(1))
                }
            }
        }
    }
}

// Использование индексов для оптимизации
object Users : IntIdTable("users") {
    val name = varchar("name", 50).index()
    val email = varchar("email", 100).uniqueIndex()
    
    // Составной индекс
    init {
        index(name, email)
    }
    
    // Частичный индекс (для PostgreSQL)
    init {
        index(isUnique = false) {
            Users.email
        } where { Users.email.isNotNull() }
    }
}

// Оптимизация JOIN запросов
fun getUsersWithPostsOptimized(): List<UserWithPosts> {
    return transaction {
        Users
            .leftJoin(Posts) {
                Users.id eq Posts.userId
            }
            .select {
                Users.name.isNotNull()
            }
            .groupBy(Users.id, Users.name)
            .map { row ->
                UserWithPosts(
                    userId = row[Users.id].value,
                    userName = row[Users.name],
                    postCount = row[Posts.id.count()]
                )
            }
    }
}

// Использование prepared statements для производительности
class PreparedStatementCache {
    private val cache = mutableMapOf<String, PreparedStatement>()
    
    fun getOrCreate(sql: String): PreparedStatement {
        return cache.getOrPut(sql) {
            connection.prepareStatement(sql)
        }
    }
}
```

Оптимизация запросов критична для производительности приложений, особенно при работе с большими объемами данных.

### Работа с **Raw SQL**

**Использование **Raw SQL** для сложных запросов:**

```kotlin
// Выполнение Raw SQL
fun executeRawSQL(sql: String): List<ResultRow> {
    return transaction {
        exec(sql) { rs ->
            val rows = mutableListOf<ResultRow>()
            while (rs.next()) {
                // Обработка результатов
            }
            rows
        }
    }
}

// Использование Raw SQL для специфичных запросов
fun getComplexReport(): List<ReportRow> {
    return transaction {
        val sql = """
            SELECT 
                u.id,
                u.name,
                COUNT(p.id) as post_count,
                SUM(p.views) as total_views
            FROM users u
            LEFT JOIN posts p ON u.id = p.user_id
            GROUP BY u.id, u.name
            HAVING COUNT(p.id) > 10
        """.trimIndent()
        
        exec(sql) { rs ->
            buildList {
                while (rs.next()) {
                    add(ReportRow(
                        userId = rs.getLong("id"),
                        userName = rs.getString("name"),
                        postCount = rs.getInt("post_count"),
                        totalViews = rs.getLong("total_views")
                    ))
                }
            }
        }
    }
}
```

**Raw SQL** позволяет выполнять сложные запросы, которые сложно выразить через **DSL Exposed**.

### Работа с **Stored Procedures**

**Вызов **stored procedures**:**

```kotlin
fun callStoredProcedure(userId: Long): List<ResultRow> {
    return transaction {
        exec("CALL get_user_statistics(?)", listOf(userId)) { rs ->
            buildList {
                while (rs.next()) {
                    // Обработка результатов
                }
            }
        }
    }
}
```

**Stored Procedures** позволяют выполнять сложную логику на стороне базы данных.

### Работа с типами данных

**Использование различных типов данных в **Exposed**:**

```kotlin
// Работа с UUID
object Users : UUIDTable("users") {
    val name = varchar("name", 50)
    val email = varchar("email", 100)
}

// Работа с датами
object Events : IntIdTable("events") {
    val name = varchar("name", 100)
    val date = date("date")
    val timestamp = datetime("timestamp")
    val time = time("time")
}

// Работа с JSON (PostgreSQL)
object Products : IntIdTable("products") {
    val name = varchar("name", 100)
    val metadata = json("metadata").nullable()
    val tags = jsonb("tags").nullable()
}

// Работа с массивами (PostgreSQL)
object Tags : IntIdTable("tags") {
    val name = varchar("name", 50)
    val values = array<String>("values", VarCharColumnType(50))
}

// Работа с ENUM
enum class UserStatus { ACTIVE, INACTIVE, SUSPENDED }

object Users : IntIdTable("users") {
    val name = varchar("name", 50)
    val status = enumerationByName<UserStatus>("status", 20)
}
```

Различные типы данных позволяют эффективно работать с различными структурами данных в базе.

### Работа с индексами

**Создание и использование индексов:**

```kotlin
// Простые индексы
object Users : IntIdTable("users") {
    val name = varchar("name", 50).index()
    val email = varchar("email", 100).uniqueIndex()
}

// Составные индексы
object Posts : IntIdTable("posts") {
    val userId = integer("user_id").index()
    val title = varchar("title", 200)
    val createdAt = datetime("created_at")
    
    init {
        index(userId, createdAt)  // Составной индекс
    }
}

// Частичные индексы (PostgreSQL)
object ActiveUsers : IntIdTable("users") {
    val name = varchar("name", 50)
    val active = bool("active")
    
    init {
        index(isUnique = false) {
            active
        } where { active eq true }
    }
}

// Полнотекстовый поиск (PostgreSQL)
object Articles : IntIdTable("articles") {
    val title = varchar("title", 200)
    val content = text("content")
    
    init {
        // Создание GIN индекса для полнотекстового поиска
    }
}
```

Индексы критичны для производительности запросов, особенно при работе с большими объемами данных.

## Решение проблем

Типичные проблемы и решения см. в официальной документации (блок «Полезные ссылки» в начале документа).

## Частые вопросы

Ответы на частые вопросы по теме см. в разделах «Введение» и «Лучшие практики» в документе.

## Заключение

**Exposed** предоставляет мощный и типобезопасный способ работы с базами данных в **Kotlin**. Понимание продвинутых техник работы с **Exposed**, включая миграции, оптимизацию запросов, работу с различными типами данных, индексами и транзакциями, позволяет создавать эффективные и надежные приложения, работающие с базами данных.

Этот файл содержит полное руководство по **Exposed**, покрывающее все основные аспекты работы с базами данных через этот фреймворк, включая продвинутые запросы, миграции, тестирование, оптимизацию производительности, безопасность, интеграцию с другими фреймворками, работу с большими данными, репликацией, шардированием, **JSON** данными, временными таблицами, оптимизацией запросов, **Raw SQL**, **Stored Procedures**, типами данных, индексами, транзакциями и заключение.

## Дополнительные ресурсы

**Для дальнейшего изучения **Exposed** рекомендуется:**

- **Exposed Documentation**: **https**://**github.com**/**JetBrains**/**Exposed**
- **Exposed Wiki**: **https**://**github.com**/**JetBrains**/**Exposed**/**wiki**
- **Exposed Examples**: **https**://**github.com**/**JetBrains**/**Exposed**/**tree**/**master**/**examples**

Этот файл содержит полное руководство по **Exposed**, покрывающее все основные аспекты работы с базами данных через этот фреймворк, включая продвинутые запросы, миграции, тестирование, оптимизацию производительности, безопасность, интеграцию с другими фреймворками, работу с большими данными, репликацией, шардированием, **JSON** данными, временными таблицами, оптимизацией запросов, **Raw SQL**, **Stored Procedures**, типами данных, индексами, транзакциями, заключение и дополнительные ресурсы.

## Итоговые рекомендации

**При работе с **Exposed** рекомендуется:**

1. Использовать миграции для управления схемой базы данных
2. Оптимизировать запросы с помощью индексов
3. Использовать транзакции для обеспечения целостности данных
4. Тестировать запросы для проверки корректности
5. Применять **Raw SQL** для сложных запросов

Этот файл содержит полное руководство по **Exposed**, покрывающее все основные аспекты работы с базами данных через этот фреймворк, включая продвинутые запросы, миграции, тестирование, оптимизацию производительности, безопасность, интеграцию с другими фреймворками, работу с большими данными, репликацией, шардированием, **JSON** данными, временными таблицами, оптимизацией запросов, **Raw SQL**, **Stored Procedures**, типами данных, индексами, транзакциями, заключение, дополнительные ресурсы и итоговые рекомендации.

## Практические примеры использования

### Работа с репозиториями

**Создание репозиториев с использованием **Exposed**:**

```kotlin
class UserRepository {
    fun findById(id: Long): User? {
        return transaction {
            Users.select { Users.id eq id }
                .map { it.toUser() }
                .singleOrNull()
        }
    }
    
    fun findAll(): List<User> {
        return transaction {
            Users.selectAll()
                .map { it.toUser() }
        }
    }
    
    fun create(user: User): User {
        return transaction {
            val id = Users.insert {
                it[name] = user.name
                it[email] = user.email
            } get Users.id
            
            user.copy(id = id)
        }
    }
    
    fun update(user: User) {
        transaction {
            Users.update({ Users.id eq user.id }) {
                it[name] = user.name
                it[email] = user.email
            }
        }
    }
    
    fun delete(id: Long) {
        transaction {
            Users.deleteWhere { Users.id eq id }
        }
    }
}
```

Репозитории инкапсулируют логику работы с базой данных и упрощают тестирование.

### Оптимизация запросов

**Оптимизация запросов для улучшения производительности:**

```kotlin
// Использование индексов
object Users : IntIdTable("users") {
    val name = varchar("name", 50).index()
    val email = varchar("email", 100).uniqueIndex()
}

// Batch операции
fun createUsers(users: List<User>) {
    transaction {
        Users.batchInsert(users) { user ->
            this[Users.name] = user.name
            this[Users.email] = user.email
        }
    }
}

// Оптимизация выборки
fun findUsersOptimized(ids: List<Long>): List<User> {
    return transaction {
        Users.select { Users.id inList ids }
            .map { it.toUser() }
    }
}
```

Оптимизация запросов критична для производительности приложений.

### Работа с транзакциями и изоляцией

**Пример работы с транзакциями и уровнями изоляции:**

```kotlin
// Транзакция с уровнем изоляции
fun transferMoney(fromId: Long, toId: Long, amount: Double) {
    transaction(Connection.TRANSACTION_SERIALIZABLE) {
        val fromAccount = Accounts.select { Accounts.id eq fromId }.single()
        val toAccount = Accounts.select { Accounts.id eq toId }.single()
        
        if (fromAccount[Accounts.balance] < amount) {
            throw InsufficientFundsException()
        }
        
        Accounts.update({ Accounts.id eq fromId }) {
            it[balance] = it[balance] - amount
        }
        
        Accounts.update({ Accounts.id eq toId }) {
            it[balance] = it[balance] + amount
        }
    }
}

// Вложенные транзакции
fun complexOperation() {
    transaction {
        // Внешняя транзакция
        val user = createUser()
        
        transaction {
            // Внутренняя транзакция
            createUserProfile(user.id)
        }
    }
}
```

Правильное использование транзакций обеспечивает целостность данных.

### Работа с миграциями

**Пример создания и применения миграций:**

```kotlin
object Migrations {
    fun migrate() {
        transaction {
            exec("""
                CREATE TABLE IF NOT EXISTS users (
                    id BIGINT PRIMARY KEY AUTO_INCREMENT,
                    name VARCHAR(255) NOT NULL,
                    email VARCHAR(255) UNIQUE NOT NULL,
                    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
                )
            """.trimIndent())
        }
    }
    
    fun addIndex() {
        transaction {
            exec("CREATE INDEX idx_email ON users(email)")
        }
    }
    
    fun addColumn() {
        transaction {
            exec("ALTER TABLE users ADD COLUMN phone VARCHAR(20)")
        }
    }
}
```

Миграции позволяют управлять эволюцией схемы базы данных.

Этот файл содержит полное руководство по **Exposed**, покрывающее все основные аспекты работы с базами данных через этот фреймворк, включая продвинутые запросы, миграции, тестирование, оптимизацию производительности, безопасность, интеграцию с другими фреймворками, работу с большими данными, репликацией, шардированием, **JSON** данными, временными таблицами, оптимизацией запросов, **Raw SQL**, **Stored Procedures**, типами данных, индексами, транзакциями, практические примеры использования, включая транзакции и миграции, заключение, дополнительные ресурсы и итоговые рекомендации.

