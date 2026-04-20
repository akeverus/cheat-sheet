---
title: "Вопросы на собеседовании: MongoDB"
description: "Вопросы и ответы по MongoDB: документная модель, BSON, индексы, агрегации, репликация, шардирование, транзакции, Spring Data MongoDB, оптимизация."
tags:
  - interview
  - databases
  - mongodb-interview
aliases:
  - "MongoDB"
  - "MongoDB interview"
  - "MongoDB собеседование"
  - "Mongo DB"
  - "NoSQL MongoDB"
  - "Spring Data MongoDB"
difficulty: "intermediate"
updated: "2026-04-13"
---
# Вопросы на собеседовании: `MongoDB`

Вопросы и ответы по `MongoDB`: документная модель, `BSON`, индексы, агрегации, репликация, шардирование, транзакции, `Spring Data MongoDB`, оптимизация.

**MongoDB** — документоориентированная `NoSQL` БД, хранящая данные в `BSON`-документах. На собеседовании спрашивают про модель данных, индексы, агрегационный `pipeline`, репликацию, шардирование, транзакции и интеграцию с `Java`/`Spring`. Подробнее о выборе типа БД — в [архитектуре баз данных](database-architecture-interview.md).

## Полезные ссылки

### Официальная документация

### Официальная документация

- [MongoDB Documentation](https://www.mongodb.com/docs/) — основная документация
- [MongoDB Java Driver](https://www.mongodb.com/docs/drivers/java/) — драйвер для Java
- [Spring Data MongoDB Reference](https://docs.spring.io/spring-data/mongodb/reference/) — справочник Spring Data MongoDB
- [MongoDB Manual: Aggregation](https://www.mongodb.com/docs/manual/aggregation/) — агрегационный pipeline

### Статьи Baeldung

- [Introduction to Spring Data MongoDB](https://www.baeldung.com/spring-data-mongodb-tutorial) — вводный туториал
- [Spring Data MongoDB: Projections and Aggregations](https://www.baeldung.com/spring-data-mongodb-projections-aggregations) — проекции и агрегации
- [Spring Data MongoDB Transactions](https://www.baeldung.com/spring-data-mongodb-transactions) — транзакции в Spring
- [MongoDB Aggregations Using Java](https://www.baeldung.com/java-mongodb-aggregations) — агрегации через Java Driver
- [A Guide to Queries in Spring Data MongoDB](https://www.baeldung.com/queries-in-spring-data-mongodb) — типы запросов
- [Spring Data MongoDB — Indexes, Annotations and Converters](https://www.baeldung.com/spring-data-mongodb-index-annotations-converter) — индексы и аннотации
- [MongoDB Atlas Search Using the Java Driver and Spring Data](https://www.baeldung.com/mongodb-spring-data-atlas-search) — полнотекстовый поиск Atlas

## Содержание

- [Полезные ссылки](#полезные-ссылки)
- [See also](#see-also)

**Основы MongoDB и документная модель**
- [Q1. (!) Что такое MongoDB и когда её выбирать?](#q1--что-такое-mongodb-и-когда-её-выбирать)
- [Q2. В чём отличие MongoDB от реляционных баз данных?](#q2-в-чём-отличие-mongodb-от-реляционных-баз-данных)
- [Q3. (!) Что такое BSON и чем он отличается от JSON?](#q3--что-такое-bson-и-чем-он-отличается-от-json)
- [Q4. Что такое коллекция и документ?](#q4-что-такое-коллекция-и-документ)
- [Q5. Какие типы данных поддерживает MongoDB?](#q5-какие-типы-данных-поддерживает-mongodb)
- [Q6. (!) Как моделировать связи: embedding vs referencing?](#q6--как-моделировать-связи-embedding-vs-referencing)
- [Q7. Какие ограничения есть в MongoDB?](#q7-какие-ограничения-есть-в-mongodb)

**CRUD и язык запросов MQL**
- [Q8. Какие основные CRUD-операции есть в MongoDB?](#q8-какие-основные-crud-операции-есть-в-mongodb)
- [Q9. Как работают операторы сравнения и логические операторы?](#q9-как-работают-операторы-сравнения-и-логические-операторы)
- [Q10. (!) Как обновлять документы: операторы $set, $inc, $push, $pull?](#q10--как-обновлять-документы-операторы-set-inc-push-pull)
- [Q11. В чём разница между find и aggregate?](#q11-в-чём-разница-между-find-и-aggregate)

**Индексы**
- [Q12. (!) Что такое индексы в MongoDB и какие типы существуют?](#q12--что-такое-индексы-в-mongodb-и-какие-типы-существуют)
- [Q13. (!) Как работает составной индекс и правило ESR?](#q13--как-работает-составной-индекс-и-правило-esr)
- [Q14. Что такое covered query?](#q14-что-такое-covered-query)
- [Q15. (!) Как реализовать полнотекстовый поиск?](#q15--как-реализовать-полнотекстовый-поиск)
- [Q16. Что такое TTL-индекс?](#q16-что-такое-ttl-индекс)

**Агрегационный pipeline**
- [Q17. (!) Как устроен агрегационный pipeline?](#q17--как-устроен-агрегационный-pipeline)
- [Q18. Как работает $lookup (аналог JOIN)?](#q18-как-работает-lookup-аналог-join)
- [Q19. Как использовать $group, $unwind, $project?](#q19-как-использовать-group-unwind-project)

**Репликация**
- [Q20. (!) Что такое Replica Set и как происходит выбор primary?](#q20--что-такое-replica-set-и-как-происходит-выбор-primary)
- [Q21. (!) Что такое Write Concern и Read Concern?](#q21--что-такое-write-concern-и-read-concern)
- [Q22. Что такое Read Preference и когда читать из реплик?](#q22-что-такое-read-preference-и-когда-читать-из-реплик)
- [Q23. Что такое Oplog?](#q23-что-такое-oplog)

**Шардирование**
- [Q24. (!) Как работает шардирование в MongoDB?](#q24--как-работает-шардирование-в-mongodb)
- [Q25. (!) Как выбрать shard key?](#q25--как-выбрать-shard-key)
- [Q26. В чём разница между range-based и hash-based sharding?](#q26-в-чём-разница-между-range-based-и-hash-based-sharding)

**Транзакции и целостность данных**
- [Q27. (!) Как работают транзакции в MongoDB?](#q27--как-работают-транзакции-в-mongodb)
- [Q28. Как работает оптимистическая блокировка?](#q28-как-работает-оптимистическая-блокировка)
- [Q29. Что такое Schema Validation?](#q29-что-такое-schema-validation)

**Spring Data MongoDB**
- [Q30. (!) Как использовать Spring Data MongoDB: MongoRepository и MongoTemplate?](#q30--как-использовать-spring-data-mongodb-mongorepository-и-mongotemplate)
- [Q31. Какие аннотации используются в Spring Data MongoDB?](#q31-какие-аннотации-используются-в-spring-data-mongodb)
- [Q32. (!) Как выполнять агрегации в Spring Data MongoDB?](#q32--как-выполнять-агрегации-в-spring-data-mongodb)
- [Q33. Как использовать транзакции в Spring Data MongoDB?](#q33-как-использовать-транзакции-в-spring-data-mongodb)
- [Q34. Как настроить подключение к MongoDB в Spring Boot?](#q34-как-настроить-подключение-к-mongodb-в-spring-boot)

**Оптимизация и эксплуатация**
- [Q35. (!) Как оптимизировать запросы в MongoDB?](#q35--как-оптимизировать-запросы-в-mongodb)
- [Q36. Что такое Change Streams?](#q36-что-такое-change-streams)
- [Q37. Как мониторить производительность MongoDB?](#q37-как-мониторить-производительность-mongodb)
- [Q38. Как выполнять миграции данных?](#q38-как-выполнять-миграции-данных)
- [Q39. Как обеспечить безопасность MongoDB?](#q39-как-обеспечить-безопасность-mongodb)
- [Q40. Когда MongoDB — плохой выбор?](#q40-когда-mongodb--плохой-выбор)

**Продвинутые возможности**
- [Q41. (!) Как работает $lookup с вложенным pipeline и $unwind?](#q41--как-работает-lookup-с-вложенным-pipeline-и-unwind)
- [Q42. (!) Что такое Change Streams и как использовать в Spring?](#q42--что-такое-change-streams-и-как-использовать-в-spring)
- [Q43. (!) Как работают транзакции на нескольких документах и коллекциях?](#q43--как-работают-транзакции-на-нескольких-документах-и-коллекциях)
- [Q44. Что такое Time Series Collections в MongoDB?](#q44-что-такое-time-series-collections-в-mongodb)
- [Q45. Как выбрать стратегию шардирования: зональное vs хэш-шардирование?](#q45-как-выбрать-стратегию-шардирования-зональное-vs-хэш-шардирование)
- [Q46. Что такое Atlas Search и как он отличается от встроенного text-индекса?](#q46-что-такое-atlas-search-и-как-он-отличается-от-встроенного-text-индекса)

---

## Q1. (!) Что такое `MongoDB` и когда её выбирать?

`MongoDB` — документоориентированная `NoSQL` БД с открытым исходным кодом. Данные хранятся в `BSON`-документах (бинарный `JSON`) внутри коллекций. Основные преимущества:

- **Гибкая схема** — документы в одной коллекции могут иметь разную структуру
- **Горизонтальное масштабирование** — встроенный `sharding`
- **Высокая доступность** — `Replica Set` с автоматическим `failover`
- **Мощные запросы** — `MQL`, агрегационный `pipeline`, полнотекстовый поиск

**Когда выбирать `MongoDB`:**
- Полуструктурированные данные с изменяющейся схемой (каталоги, CMS, профили)
- Высокая нагрузка на запись с горизонтальным масштабированием
- Прототипирование — быстрый старт без миграций
- Иерархические / вложенные данные (JSON-документы)

**Когда НЕ выбирать:** сложные связи между сущностями (лучше РСУБД), строгие `ACID`-транзакции на множестве коллекций, аналитические запросы по колонкам. Подробнее о выборе типа БД — в [архитектуре баз данных](database-architecture-interview.md) и [CAP-теореме](../architecture/cap-theorem-interview.md).

## Q2. В чём отличие `MongoDB` от реляционных баз данных?

| Критерий | `MongoDB` | РСУБД (`PostgreSQL`, `MySQL`) |
|----------|----------|-------------------------------|
| Модель данных | Документы (`BSON`) | Таблицы, строки, столбцы |
| Схема | Гибкая (schema-less) | Фиксированная (`DDL`) |
| Связи | Embedding / `$lookup` | `JOIN`, внешние ключи |
| Масштабирование | Горизонтальное (`sharding`) | Вертикальное (+ read replicas) |
| Транзакции | Multi-document (с 4.0) | Полный `ACID` |
| Язык запросов | `MQL` (JSON-like) | `SQL` |
| Нормализация | Денормализация (embedding) | Нормализация (3NF) |

Подробнее о `SQL` — в [вопросах по SQL](sql-interview.md).

## Q3. (!) Что такое `BSON` и чем он отличается от `JSON`?

`BSON` (`Binary JSON`) — бинарный формат сериализации, который `MongoDB` использует для хранения документов и передачи данных по сети.

**Отличия от `JSON`:**
- **Типизация** — `BSON` поддерживает дополнительные типы: `Date`, `ObjectId`, `Decimal128`, `BinData`, `Int32/Int64`, `Regex`
- **Бинарный формат** — быстрее парсинг и обход, чем текстовый `JSON`
- **Размер** — `BSON` может быть больше за счёт метаданных типов и длин
- **Обход** — `BSON` хранит длины строк и поддокументов, что позволяет пропускать ненужные поля

```javascript
// JSON
{ "name": "John", "age": 30 }

// BSON (концептуально) — добавлены типы и длины
\x16\x00\x00\x00           // размер документа
\x02 name\x00              // тип string
\x05\x00\x00\x00 John\x00  // длина + значение
\x10 age\x00               // тип int32
\x1e\x00\x00\x00           // значение 30
\x00                       // конец документа
```

`ObjectId` — 12-байтовый уникальный идентификатор: 4 байта timestamp + 5 байт случайное значение + 3 байта инкрементирующий счётчик. Позволяет генерировать `_id` на стороне клиента без координации.

## Q4. Что такое коллекция и документ?

**Документ** — основная единица данных в `MongoDB`. Это `BSON`-объект с парами ключ-значение. Каждый документ имеет уникальный `_id` (по умолчанию `ObjectId`). Максимальный размер — **16 МБ**.

**Коллекция** — набор документов; аналог таблицы в РСУБД. Коллекция не требует единой схемы — документы могут иметь разные поля.

```javascript
// Создание коллекции и вставка документа
db.createCollection("users")

db.users.insertOne({
    _id: ObjectId("507f1f77bcf86cd799439011"),
    name: "Иван",
    age: 28,
    email: "ivan@example.com",
    address: {                     // вложенный документ
        city: "Москва",
        zip: "101000"
    },
    interests: ["Java", "MongoDB"] // массив
})
```

**Capped Collection** — коллекция фиксированного размера с FIFO-порядком. Используется для логов и буферов:

```javascript
db.createCollection("logs", { capped: true, size: 10485760, max: 5000 })
```

## Q5. Какие типы данных поддерживает `MongoDB`?

Основные `BSON`-типы:

| Тип | Описание | Пример |
|-----|----------|--------|
| `String` | UTF-8 строка | `"hello"` |
| `Int32` / `Int64` | Целые числа | `42`, `NumberLong(123)` |
| `Double` | 64-bit float | `3.14` |
| `Decimal128` | 128-bit decimal (финансы) | `NumberDecimal("19.99")` |
| `Boolean` | true/false | `true` |
| `ObjectId` | 12-байтовый уникальный ID | `ObjectId("...")` |
| `Date` | Дата/время (ms от epoch) | `ISODate("2026-01-01")` |
| `Array` | Массив значений | `[1, 2, 3]` |
| `Object` | Вложенный документ | `{ a: 1 }` |
| `Binary` | Бинарные данные | Файлы, хэши |
| `Null` | Пустое значение | `null` |
| `Regex` | Регулярное выражение | `/pattern/i` |
| `Timestamp` | Для внутренних нужд (Oplog) | `Timestamp(1, 1)` |

## Q6. (!) Как моделировать связи: `embedding` vs `referencing`?

Два основных подхода к моделированию связей:

**Embedding (встраивание)** — связанные данные внутри документа:

```javascript
// Заказ со встроенными позициями
{
    _id: ObjectId("..."),
    customer: "Иван",
    items: [
        { product: "Ноутбук", price: 85000, qty: 1 },
        { product: "Мышь", price: 2500, qty: 2 }
    ],
    total: 90000
}
```

**Referencing (ссылки)** — хранение `_id` связанного документа:

```javascript
// Заказ со ссылкой на пользователя
{
    _id: ObjectId("..."),
    userId: ObjectId("507f1f77bcf86cd799439011"),
    items: [...]
}
// Пользователь в отдельной коллекции
{
    _id: ObjectId("507f1f77bcf86cd799439011"),
    name: "Иван"
}
```

**Критерии выбора:**

| Критерий | Embedding | Referencing |
|----------|-----------|-------------|
| Паттерн чтения | Данные всегда нужны вместе | Данные нужны по отдельности |
| Размер | Вложенные данные небольшие | Вложенные данные большие |
| Обновления | Редко меняются | Часто обновляются независимо |
| Кардинальность | 1:few, 1:many (до сотен) | 1:very-many (тысячи+) |
| Атомарность | Атомарное обновление | Требуется транзакция |

**Антипаттерн:** неограниченные массивы (unbounded arrays) — массив, растущий без ограничения, приведёт к превышению лимита 16 МБ и деградации производительности.

## Q7. Какие ограничения есть в `MongoDB`?

- **Размер документа:** максимум **16 МБ** (для больших файлов — `GridFS`)
- **Вложенность:** максимум **100 уровней** вложенности
- **Имена полей:** не могут начинаться с `$` или содержать `.`
- **Индексы:** максимум **64 индекса** на коллекцию
- **Namespace:** длина имени БД + коллекции до **120 байт**
- **Составной индекс:** максимум **32 поля**
- **Сортировка в памяти:** ограничение **100 МБ** (без `allowDiskUse`)
- **Транзакции:** по умолчанию таймаут **60 секунд**; требуют `Replica Set`

## Q8. Какие основные `CRUD`-операции есть в `MongoDB`?

```javascript
// CREATE
db.users.insertOne({ name: "Иван", age: 28 })
db.users.insertMany([
    { name: "Мария", age: 25 },
    { name: "Алексей", age: 32 }
])

// READ
db.users.find({ age: { $gte: 25 } })            // все с age >= 25
db.users.findOne({ name: "Иван" })               // один документ
db.users.find({ age: { $gte: 25 } }, { name: 1 }) // проекция: только name

// UPDATE
db.users.updateOne(
    { name: "Иван" },
    { $set: { age: 29 }, $push: { tags: "senior" } }
)
db.users.updateMany(
    { age: { $lt: 30 } },
    { $inc: { age: 1 } }
)
db.users.replaceOne(
    { name: "Иван" },
    { name: "Иван", age: 30, role: "developer" }
)

// DELETE
db.users.deleteOne({ name: "Алексей" })
db.users.deleteMany({ age: { $lt: 20 } })
```

`findOneAndUpdate` / `findOneAndDelete` — атомарно находят и изменяют документ, возвращая оригинал или обновлённый документ. Полезно для реализации очередей и счётчиков.

## Q9. Как работают операторы сравнения и логические операторы?

**Операторы сравнения:**
- `$eq`, `$ne` — равно / не равно
- `$gt`, `$gte`, `$lt`, `$lte` — больше / больше-равно / меньше / меньше-равно
- `$in`, `$nin` — в списке / не в списке
- `$exists` — поле существует
- `$type` — тип поля

**Логические операторы:**
- `$and`, `$or`, `$not`, `$nor`

```javascript
// Пользователи из Москвы или Питера старше 25
db.users.find({
    $and: [
        { city: { $in: ["Москва", "Санкт-Петербург"] } },
        { age: { $gt: 25 } }
    ]
})

// Поиск по вложенному полю (dot notation)
db.users.find({ "address.city": "Москва" })

// Поиск по элементу массива
db.users.find({ interests: "Java" })

// $elemMatch — элемент массива удовлетворяет нескольким условиям
db.orders.find({
    items: { $elemMatch: { product: "Ноутбук", qty: { $gte: 2 } } }
})
```

## Q10. (!) Как обновлять документы: операторы `$set`, `$inc`, `$push`, `$pull`?

Основные операторы обновления:

```javascript
// $set — установить значение поля
db.users.updateOne({ _id: id }, { $set: { status: "active" } })

// $unset — удалить поле
db.users.updateOne({ _id: id }, { $unset: { tempField: "" } })

// $inc — инкремент числового поля
db.users.updateOne({ _id: id }, { $inc: { loginCount: 1, score: -5 } })

// $push — добавить элемент в массив
db.users.updateOne({ _id: id }, { $push: { tags: "mongodb" } })

// $addToSet — добавить, если ещё нет в массиве
db.users.updateOne({ _id: id }, { $addToSet: { tags: "mongodb" } })

// $pull — удалить элемент из массива
db.users.updateOne({ _id: id }, { $pull: { tags: "deprecated" } })

// $push с $each и $sort — добавить несколько, отсортировать
db.users.updateOne({ _id: id }, {
    $push: {
        scores: { $each: [85, 92], $sort: -1, $slice: 10 }
    }
})

// Upsert — вставить, если не найден
db.users.updateOne(
    { email: "new@example.com" },
    { $set: { name: "Новый" }, $setOnInsert: { createdAt: new Date() } },
    { upsert: true }
)
```

**Атомарность:** каждая операция обновления одного документа атомарна. Для обновления нескольких документов атомарно — используйте транзакции.

## Q11. В чём разница между `find` и `aggregate`?

| Аспект | `find` | `aggregate` |
|--------|--------|-------------|
| Назначение | Простая выборка | Сложная обработка данных |
| Возможности | Фильтр, проекция, сортировка, лимит | `pipeline` из произвольных этапов |
| `JOIN` | Нет | `$lookup` |
| Группировка | Нет | `$group` |
| Вычисляемые поля | Ограниченно | `$addFields`, `$project` |
| Производительность | Быстрее для простых запросов | Может использовать индексы на `$match` |

**Правило:** если хватает `find` — используйте `find`; `aggregate` — для аналитики, группировок, `JOIN` и преобразований.

## Q12. (!) Что такое индексы в `MongoDB` и какие типы существуют?

Индексы ускоряют выборку данных, используя `B-tree` структуру. Без индекса `MongoDB` выполняет `collection scan` — просмотр всех документов.

**Типы индексов:**

```javascript
// 1. Single field — одно поле
db.users.createIndex({ email: 1 })

// 2. Compound — составной (несколько полей)
db.orders.createIndex({ status: 1, createdAt: -1 })

// 3. Unique — уникальный
db.users.createIndex({ email: 1 }, { unique: true })

// 4. Text — полнотекстовый
db.articles.createIndex({ title: "text", body: "text" })

// 5. Hashed — хэш (для шардирования)
db.users.createIndex({ userId: "hashed" })

// 6. TTL — автоудаление по времени
db.sessions.createIndex({ createdAt: 1 }, { expireAfterSeconds: 3600 })

// 7. Geospatial — геоиндекс
db.places.createIndex({ location: "2dsphere" })

// 8. Partial — индекс с условием (экономия места)
db.orders.createIndex(
    { status: 1 },
    { partialFilterExpression: { status: "ACTIVE" } }
)

// 9. Wildcard — для документов с динамической структурой
db.products.createIndex({ "attributes.$**": 1 })

// 10. Multikey — автоматически для массивов
db.posts.createIndex({ tags: 1 })
```

**Важно:** каждый индекс замедляет запись. Не создавайте лишних индексов — анализируйте паттерны запросов. Подробнее об индексах реляционных БД — в [вопросах по SQL](sql-interview.md).

## Q13. (!) Как работает составной индекс и правило `ESR`?

**Составной индекс** содержит несколько полей. Порядок полей критичен — индекс поддерживает запросы по **префиксу** полей.

```javascript
// Индекс { a: 1, b: 1, c: 1 } поддерживает запросы:
// { a: X }           — да (префикс)
// { a: X, b: Y }     — да (префикс)
// { a: X, b: Y, c: Z } — да (полный)
// { b: Y }           — НЕТ (не префикс)
// { a: X, c: Z }     — частично (a используется, c — нет)
```

**Правило ESR (Equality, Sort, Range)** — оптимальный порядок полей в составном индексе:

1. **Equality** — поля с точным совпадением (`=`)
2. **Sort** — поля сортировки
3. **Range** — поля с диапазонными условиями (`$gt`, `$lt`, `$in`)

```javascript
// Запрос: status = "ACTIVE", sort by createdAt, price > 1000
// Оптимальный индекс:
db.products.createIndex({ status: 1, createdAt: -1, price: 1 })
//                        Equality     Sort           Range
```

## Q14. Что такое `covered query`?

**Covered query** — запрос, все поля которого (фильтр, проекция, сортировка) есть в индексе. `MongoDB` возвращает результат **только из индекса**, без обращения к документам — максимальная скорость.

```javascript
// Индекс
db.users.createIndex({ email: 1, name: 1 })

// Covered query — все поля в индексе, _id: 0 обязателен
db.users.find(
    { email: "ivan@example.com" },
    { email: 1, name: 1, _id: 0 }
)
```

Проверка через `explain()`:

```javascript
db.users.find(...).explain("executionStats")
// totalDocsExamined: 0  — документы не читались
// totalKeysExamined: 1  — только индекс
```

## Q15. (!) Как реализовать полнотекстовый поиск?

Для полнотекстового поиска используется **текстовый индекс** и оператор `$text`:

```javascript
// Создание текстового индекса (один на коллекцию)
db.articles.createIndex(
    { title: "text", body: "text" },
    { weights: { title: 10, body: 5 }, default_language: "russian" }
)

// Поиск
db.articles.find({ $text: { $search: "MongoDB индексы" } })

// С ранжированием по релевантности
db.articles.find(
    { $text: { $search: "MongoDB индексы" } },
    { score: { $meta: "textScore" } }
).sort({ score: { $meta: "textScore" } })

// Фразовый поиск (точное совпадение фразы)
db.articles.find({ $text: { $search: "\"replica set\"" } })

// Исключение слова
db.articles.find({ $text: { $search: "MongoDB -deprecated" } })
```

**Ограничения:** один текстовый индекс на коллекцию; не поддерживает fuzzy-поиск, нет синонимов (есть в `Atlas Search`). Для продвинутого полнотекстового поиска рекомендуется [Elasticsearch](elasticsearch-interview.md).

## Q16. Что такое `TTL`-индекс?

**TTL (Time To Live) index** — индекс по полю с типом `Date`, при котором `MongoDB` автоматически удаляет документы после заданного срока.

```javascript
// Удалять сессии через 1 час после создания
db.sessions.createIndex({ createdAt: 1 }, { expireAfterSeconds: 3600 })

// Удалять в заданное время (поле expireAt хранит точную дату удаления)
db.notifications.createIndex({ expireAt: 1 }, { expireAfterSeconds: 0 })
```

**Особенности:**
- Фоновый процесс проверяет каждые **60 секунд** — удаление не мгновенное
- Только один `TTL`-индекс на коллекцию
- В `Replica Set` удаление выполняется только на `primary`
- Поле должно быть типа `Date` или массив дат (берётся минимальная)

Применение: сессии, временные токены, логи, кэш.

## Q17. (!) Как устроен агрегационный `pipeline`?

**Aggregation pipeline** — цепочка этапов (stages), каждый из которых трансформирует поток документов. Результат одного этапа подаётся на вход следующего.

```mermaid
graph LR
    A[Коллекция] --> B["$match"]
    B --> C["$group"]
    C --> D["$sort"]
    D --> E["$limit"]
    E --> F[Результат]
```

**Основные этапы:**

```javascript
db.orders.aggregate([
    // 1. Фильтрация (использует индексы!)
    { $match: { status: "COMPLETED", createdAt: { $gte: ISODate("2026-01-01") } } },

    // 2. Группировка
    { $group: {
        _id: "$category",
        totalRevenue: { $sum: "$amount" },
        avgAmount: { $avg: "$amount" },
        count: { $sum: 1 }
    }},

    // 3. Сортировка
    { $sort: { totalRevenue: -1 } },

    // 4. Ограничение
    { $limit: 10 },

    // 5. Преобразование полей
    { $project: {
        category: "$_id",
        totalRevenue: 1,
        avgAmount: { $round: ["$avgAmount", 2] },
        _id: 0
    }}
])
```

**Оптимизация:** ставьте `$match` первым — он использует индексы и сокращает объём данных. `$project` перед `$group` убирает ненужные поля. Для больших данных — `allowDiskUse: true`.

## Q18. Как работает `$lookup` (аналог `JOIN`)?

`$lookup` — этап агрегации для объединения документов из разных коллекций (аналог `LEFT JOIN` в SQL):

```javascript
// Простой $lookup
db.orders.aggregate([
    { $lookup: {
        from: "users",           // коллекция для join
        localField: "userId",    // поле в orders
        foreignField: "_id",     // поле в users
        as: "user"               // имя результата (массив)
    }},
    { $unwind: "$user" },        // превратить массив в объект
    { $project: {
        orderNumber: 1,
        "user.name": 1,
        amount: 1
    }}
])

// Correlated $lookup с pipeline (гибкий вариант)
db.orders.aggregate([
    { $lookup: {
        from: "products",
        let: { orderItems: "$itemIds" },
        pipeline: [
            { $match: { $expr: { $in: ["$_id", "$$orderItems"] } } },
            { $project: { name: 1, price: 1 } }
        ],
        as: "products"
    }}
])
```

**Важно:** `$lookup` не использует шардирование для `from`-коллекции (до `MongoDB 5.1`). В шардированном кластере рассмотрите денормализацию вместо частых `$lookup`.

## Q19. Как использовать `$group`, `$unwind`, `$project`?

```javascript
// $unwind — разворачивает массив в отдельные документы
// Документ { tags: ["java", "mongo"] } → два документа
db.posts.aggregate([
    { $unwind: "$tags" },
    { $group: { _id: "$tags", count: { $sum: 1 } } },
    { $sort: { count: -1 } }
])

// $project — выбор и преобразование полей
db.users.aggregate([
    { $project: {
        fullName: { $concat: ["$firstName", " ", "$lastName"] },
        ageGroup: {
            $switch: {
                branches: [
                    { case: { $lt: ["$age", 18] }, then: "junior" },
                    { case: { $lt: ["$age", 30] }, then: "middle" },
                ],
                default: "senior"
            }
        },
        year: { $year: "$createdAt" }
    }}
])

// $addFields — добавить поля, не убирая существующие
db.orders.aggregate([
    { $addFields: {
        totalWithTax: { $multiply: ["$total", 1.2] }
    }}
])

// $facet — несколько pipeline параллельно
db.products.aggregate([
    { $facet: {
        byCategory: [{ $group: { _id: "$category", count: { $sum: 1 } } }],
        priceStats: [{ $group: { _id: null, avg: { $avg: "$price" }, max: { $max: "$price" } } }]
    }}
])
```

## Q20. (!) Что такое `Replica Set` и как происходит выбор `primary`?

**Replica Set** — группа узлов `MongoDB`, поддерживающих одинаковую копию данных для высокой доступности и отказоустойчивости.

```mermaid
graph TB
    Client[Клиент] -->|"write"| Primary
    Client -->|"read (optional)"| Secondary1
    Primary -->|"Oplog replication"| Secondary1[Secondary 1]
    Primary -->|"Oplog replication"| Secondary2[Secondary 2]
    
    subgraph "Replica Set (3 узла)"
        Primary
        Secondary1
        Secondary2
    end
```

**Компоненты:**
- **Primary** — единственный узел, принимающий записи; реплицирует изменения через `Oplog`
- **Secondary** — реплики; могут обслуживать чтение (при настройке `Read Preference`)
- **Arbiter** — узел без данных, только для голосования (кворум)

**Выбор primary (election):**
- Происходит при старте или падении текущего `primary`
- Узлы голосуют; нужно **большинство** (majority) голосов
- Нечётное число узлов (3, 5, 7) — для кворума
- `Priority` узла влияет на шансы стать `primary` (0 = никогда не станет)
- `Election` занимает обычно **1-2 секунды** (до 12 в худшем случае)

**Рекомендация для production:** минимум 3 узла; для geo-distributed — 5 узлов (2+2+1 по дата-центрам). Подробнее о распределённых системах — в [вопросах по распределённым системам](../architecture/distributed-systems-interview.md).

## Q21. (!) Что такое `Write Concern` и `Read Concern`?

**Write Concern** — уровень подтверждения записи:

| Write Concern | Гарантия | Скорость |
|---------------|----------|----------|
| `w: 0` | Без подтверждения (fire and forget) | Максимальная |
| `w: 1` | Подтверждение от primary | Быстрая |
| `w: "majority"` | Подтверждение от большинства узлов | Средняя |
| `j: true` | Запись в journal (на диск) | Медленнее |
| `w: "majority", j: true` | Максимальная надёжность | Самая медленная |

**Read Concern** — уровень согласованности чтения:

| Read Concern | Гарантия |
|-------------|----------|
| `"local"` | Чтение последних данных с узла (может откатиться) |
| `"majority"` | Данные подтверждены большинством |
| `"linearizable"` | Строгая линеаризуемость (только primary) |
| `"snapshot"` | Для транзакций; согласованный снимок |

**Практика:** для критичных данных (платежи) — `w: "majority", j: true, readConcern: "majority"`. Для логов и аналитики — `w: 1` достаточно.

## Q22. Что такое `Read Preference` и когда читать из реплик?

**Read Preference** определяет, с какого узла `Replica Set` читать:

| Режим | Описание | Когда использовать |
|-------|----------|-------------------|
| `primary` | Только primary | Строгая консистентность |
| `primaryPreferred` | Primary; при недоступности — secondary | Основной режим |
| `secondary` | Только secondary | Отчёты, аналитика |
| `secondaryPreferred` | Secondary; при недоступности — primary | Снижение нагрузки |
| `nearest` | Ближайший по latency | Geo-distributed |

**Риски чтения из реплик:** `eventual consistency` — реплика может отставать (`replication lag`). Для сценария "прочитай после записи" используйте `primary`.

```java
// Spring Data MongoDB — настройка Read Preference
@Configuration
public class MongoConfig {
    @Bean
    public MongoTemplate mongoTemplate(MongoDatabaseFactory factory) {
        MongoTemplate template = new MongoTemplate(factory);
        template.setReadPreference(ReadPreference.secondaryPreferred());
        return template;
    }
}
```

## Q23. Что такое `Oplog`?

**Oplog (Operations Log)** — capped-коллекция `local.oplog.rs`, в которую `primary` записывает все операции изменения данных. `Secondary`-узлы читают `Oplog` и воспроизводят операции для синхронизации.

**Свойства:**
- Фиксированный размер (по умолчанию 5% диска, минимум 990 МБ)
- Каждая запись — идемпотентная операция
- `Change Streams` работают поверх `Oplog`
- При отставании реплики больше, чем размер `Oplog` — необходим полный `initial sync`

```javascript
// Просмотр Oplog
use local
db.oplog.rs.find().sort({ $natural: -1 }).limit(5)
```

## Q24. (!) Как работает шардирование в `MongoDB`?

**Sharding** — горизонтальное распределение данных коллекции по нескольким серверам (шардам).

```mermaid
graph TB
    App[Приложение] --> Mongos1["mongos (роутер)"]
    App --> Mongos2["mongos (роутер)"]
    
    Mongos1 --> Shard1["Shard 1<br/>(Replica Set)"]
    Mongos1 --> Shard2["Shard 2<br/>(Replica Set)"]
    Mongos1 --> Shard3["Shard 3<br/>(Replica Set)"]
    
    Mongos2 --> Shard1
    Mongos2 --> Shard2
    Mongos2 --> Shard3
    
    ConfigSrv["Config Servers<br/>(Replica Set)"] -.->|"метаданные"| Mongos1
    ConfigSrv -.->|"метаданные"| Mongos2
```

**Компоненты:**
- **Shard** — каждый шард является `Replica Set`; хранит подмножество данных
- **mongos** — маршрутизатор запросов; определяет, на какой шард направить запрос
- **Config Servers** — хранят метаданные: диапазоны чанков, маппинг шард-данные
- **Chunk** — единица данных (по умолчанию 128 МБ); балансировщик перемещает чанки между шардами

**Targeted vs Scatter-Gather:**
- Запрос с `shard key` — **targeted** (на один шард) — быстро
- Запрос без `shard key` — **scatter-gather** (на все шарды) — дорого

## Q25. (!) Как выбрать `shard key`?

Выбор `shard key` — одно из самых важных архитектурных решений; **изменить его после создания коллекции сложно** (с MongoDB 5.0 можно через `resharding`, но это дорогая операция).

**Критерии хорошего shard key:**
1. **Высокая кардинальность** — много уникальных значений (не boolean, не enum)
2. **Равномерное распределение** — данные распределяются по шардам без hot spots
3. **Совпадение с запросами** — чтобы запросы были targeted, а не scatter-gather
4. **Не монотонный** — `ObjectId` или `timestamp` создают hot spot на последнем шарде

**Примеры:**

| Shard key | Оценка | Проблема |
|-----------|--------|----------|
| `{ _id: "hashed" }` | Хорошо для записи | Range-запросы по `_id` — scatter-gather |
| `{ userId: 1 }` | Хорошо | Если один userId генерирует слишком много данных — jumbo chunk |
| `{ createdAt: 1 }` | Плохо | Монотонный — все записи идут в один шард |
| `{ userId: 1, createdAt: 1 }` | Отлично | Высокая кардинальность + targeted запросы |

## Q26. В чём разница между `range-based` и `hash-based` sharding?

| Аспект | Range-based | Hash-based |
|--------|------------|------------|
| Распределение | По диапазонам значений | По хэшу ключа |
| Range-запросы | Targeted (эффективно) | Scatter-gather |
| Равномерность | Зависит от данных | Равномерная |
| Hot spots | Возможны (монотонный ключ) | Маловероятны |
| Пример ключа | `{ zipCode: 1 }` | `{ userId: "hashed" }` |

**Компромиссный вариант** — составной shard key: `{ country: 1, orderId: "hashed" }` — targeted по `country` и равномерное распределение внутри страны.

## Q27. (!) Как работают транзакции в `MongoDB`?

С **MongoDB 4.0** поддерживаются multi-document `ACID`-транзакции. С **4.2** — транзакции в шардированном кластере.

```javascript
// JavaScript (mongosh)
const session = db.getMongo().startSession();
session.startTransaction({
    readConcern: { level: "snapshot" },
    writeConcern: { w: "majority" }
});

try {
    const accounts = session.getDatabase("bank").accounts;
    accounts.updateOne({ _id: "from" }, { $inc: { balance: -100 } });
    accounts.updateOne({ _id: "to" }, { $inc: { balance: 100 } });
    session.commitTransaction();
} catch (error) {
    session.abortTransaction();
    throw error;
} finally {
    session.endSession();
}
```

**Ограничения транзакций:**
- Требуют `Replica Set` (или шардированный кластер)
- Таймаут по умолчанию **60 секунд** (`transactionLifetimeLimitSeconds`)
- Не могут создавать коллекции или индексы
- Значительный overhead по производительности
- Размер транзакции — до **16 МБ** `Oplog`-записей

**Правило:** если можно обойтись атомарным обновлением одного документа (через embedding) — это предпочтительнее транзакции. Транзакции — для случаев, когда атомарность нужна между несколькими коллекциями/документами.

## Q28. Как работает оптимистическая блокировка?

В `MongoDB` нет встроенной оптимистической блокировки — она реализуется на уровне приложения через **поле версии**:

```javascript
// 1. Прочитать документ с версией
const doc = db.products.findOne({ _id: productId });

// 2. Обновить с проверкой версии
const result = db.products.updateOne(
    { _id: productId, version: doc.version },
    { $set: { price: newPrice }, $inc: { version: 1 } }
);

// 3. Проверить, было ли обновление
if (result.modifiedCount === 0) {
    throw new Error("Concurrent modification detected");
}
```

В `Spring Data MongoDB` — аннотация `@Version`:

```java
@Document(collection = "products")
public class Product {
    @Id private String id;
    @Version private Long version; // автоматическая оптимистическая блокировка
    private String name;
    private BigDecimal price;
}
```

`Spring Data` автоматически добавит `version` в условие `updateOne` и выбросит `OptimisticLockingFailureException` при конфликте.

## Q29. Что такое `Schema Validation`?

**Schema Validation** — правила валидации документов при вставке и обновлении на уровне БД:

```javascript
db.createCollection("users", {
    validator: {
        $jsonSchema: {
            bsonType: "object",
            required: ["email", "name", "role"],
            properties: {
                email: {
                    bsonType: "string",
                    pattern: "^.+@.+\\..+$",
                    description: "должен быть валидный email"
                },
                name: { bsonType: "string", minLength: 1 },
                age: { bsonType: "int", minimum: 0, maximum: 150 },
                role: { enum: ["admin", "user", "moderator"] }
            }
        }
    },
    validationLevel: "strict",       // strict | moderate
    validationAction: "error"        // error | warn
})
```

- **`strict`** — проверяет все документы при insert/update
- **`moderate`** — проверяет только документы, которые уже соответствовали схеме
- **`error`** — отклоняет невалидный документ (`WriteError` с кодом 121)
- **`warn`** — записывает предупреждение в лог, но сохраняет документ

Не заменяет валидацию в приложении — дополняет целостность на уровне БД.

## Q30. (!) Как использовать `Spring Data MongoDB`: `MongoRepository` и `MongoTemplate`?

**`MongoRepository`** — декларативный подход, аналогичный `Spring Data JPA`:

```java
@Document(collection = "users")
public class User {
    @Id private String id;
    @Indexed(unique = true) private String email;
    private String name;
    private int age;
    private Address address; // вложенный объект
}

public interface UserRepository extends MongoRepository<User, String> {
    // Автогенерация запроса по имени метода
    List<User> findByName(String name);
    List<User> findByAgeBetween(int min, int max);
    List<User> findByAddressCity(String city); // dot notation

    // Кастомный запрос MQL
    @Query("{ 'age': { $gte: ?0 }, 'address.city': ?1 }")
    List<User> findActiveInCity(int minAge, String city);

    // Проекция — возвращаем только нужные поля
    @Query(value = "{ 'status': 'ACTIVE' }", fields = "{ 'name': 1, 'email': 1 }")
    List<User> findActiveUsersProjected();

    // Пагинация
    Page<User> findByStatus(String status, Pageable pageable);
}
```

**`MongoTemplate`** — программный подход для сложных запросов:

```java
@Service
@RequiredArgsConstructor
public class UserService {
    private final MongoTemplate mongoTemplate;

    public List<User> findByComplexCriteria(String city, int minAge) {
        Query query = new Query();
        query.addCriteria(Criteria.where("address.city").is(city)
                .and("age").gte(minAge));
        query.with(Sort.by(Sort.Direction.DESC, "createdAt"));
        query.limit(20);
        query.fields().include("name").include("email");
        return mongoTemplate.find(query, User.class);
    }

    public UpdateResult incrementLoginCount(String userId) {
        Query query = Query.query(Criteria.where("id").is(userId));
        Update update = new Update()
                .inc("loginCount", 1)
                .set("lastLogin", Instant.now());
        return mongoTemplate.updateFirst(query, update, User.class);
    }
}
```

**Когда что использовать:**
- `MongoRepository` — `CRUD`, простые запросы, пагинация
- `MongoTemplate` — агрегации, `bulkOps`, сложные update, `upsert`, программная логика

Подробнее о `Spring Data` — в [вопросах по Spring Data JPA](../frameworks/spring/spring-data-jpa-interview.md) (аналогичный подход для РСУБД).

## Q31. Какие аннотации используются в `Spring Data MongoDB`?

| Аннотация | Назначение | Пример |
|-----------|-----------|--------|
| `@Document` | Маркер документа, привязка к коллекции | `@Document(collection = "orders")` |
| `@Id` | Поле `_id` | `@Id private String id;` |
| `@Field` | Маппинг на имя поля в `BSON` | `@Field("user_name") private String name;` |
| `@Indexed` | Создание индекса | `@Indexed(unique = true)` |
| `@CompoundIndex` | Составной индекс | `@CompoundIndex(def = "{'status': 1, 'createdAt': -1}")` |
| `@TextIndexed` | Текстовый индекс | `@TextIndexed(weight = 2)` |
| `@DBRef` | Ссылка на документ в другой коллекции | `@DBRef private User author;` |
| `@Version` | Оптимистическая блокировка | `@Version private Long version;` |
| `@Transient` | Не сохранять в БД | `@Transient private String computed;` |
| `@CreatedDate` | Дата создания (auditing) | Требует `@EnableMongoAuditing` |
| `@LastModifiedDate` | Дата последнего обновления | Требует `@EnableMongoAuditing` |

```java
@Document(collection = "orders")
@CompoundIndex(def = "{'status': 1, 'createdAt': -1}", name = "status_date_idx")
public class Order {
    @Id private String id;
    @Field("user_id") private String userId;
    @DBRef private User user;
    @Indexed private String status;
    @Version private Long version;
    @CreatedDate private Instant createdAt;
    @LastModifiedDate private Instant updatedAt;
    private List<OrderItem> items; // embedded
    private BigDecimal total;
}
```

## Q32. (!) Как выполнять агрегации в `Spring Data MongoDB`?

```java
@Service
@RequiredArgsConstructor
public class OrderAnalyticsService {
    private final MongoTemplate mongoTemplate;

    // Подсчёт заказов и выручки по категориям
    public List<CategoryStats> getCategoryStats(LocalDate from) {
        Aggregation agg = Aggregation.newAggregation(
            // $match — фильтрация (использует индекс)
            match(Criteria.where("status").is("COMPLETED")
                    .and("createdAt").gte(from)),
            // $unwind — развернуть массив items
            unwind("items"),
            // $group — группировка
            group("items.category")
                .sum("items.price").as("totalRevenue")
                .count().as("orderCount")
                .avg("items.price").as("avgPrice"),
            // $sort
            sort(Sort.Direction.DESC, "totalRevenue"),
            // $project
            project()
                .and("_id").as("category")
                .andInclude("totalRevenue", "orderCount")
                .and("avgPrice").applyExpression("round", 2)
        );

        AggregationResults<CategoryStats> results =
            mongoTemplate.aggregate(agg, "orders", CategoryStats.class);
        return results.getMappedResults();
    }

    // $lookup — join с другой коллекцией
    public List<Document> getOrdersWithUsers() {
        Aggregation agg = Aggregation.newAggregation(
            lookup("users", "userId", "_id", "user"),
            unwind("user"),
            project("orderNumber", "amount")
                .and("user.name").as("customerName")
        );
        return mongoTemplate.aggregate(agg, "orders", Document.class)
                .getMappedResults();
    }
}
```

Также можно использовать `@Aggregation` в репозитории:

```java
public interface OrderRepository extends MongoRepository<Order, String> {
    @Aggregation(pipeline = {
        "{ $match: { status: ?0 } }",
        "{ $group: { _id: '$category', total: { $sum: '$amount' } } }",
        "{ $sort: { total: -1 } }"
    })
    List<Document> aggregateByCategory(String status);
}
```

## Q33. Как использовать транзакции в `Spring Data MongoDB`?

Для транзакций необходим `Replica Set` и `MongoTransactionManager`:

```java
@Configuration
public class MongoTransactionConfig {
    @Bean
    MongoTransactionManager transactionManager(MongoDatabaseFactory dbFactory) {
        return new MongoTransactionManager(dbFactory);
    }
}
```

Использование через `@Transactional`:

```java
@Service
@RequiredArgsConstructor
public class TransferService {
    private final MongoTemplate mongoTemplate;

    @Transactional
    public void transfer(String fromId, String toId, BigDecimal amount) {
        // Обе операции выполняются в одной транзакции
        mongoTemplate.updateFirst(
            Query.query(Criteria.where("id").is(fromId)),
            new Update().inc("balance", amount.negate().doubleValue()),
            Account.class
        );
        mongoTemplate.updateFirst(
            Query.query(Criteria.where("id").is(toId)),
            new Update().inc("balance", amount.doubleValue()),
            Account.class
        );
        // При исключении — автоматический rollback
    }
}
```

Программный подход через `TransactionTemplate`:

```java
@Service
@RequiredArgsConstructor
public class TransferService {
    private final MongoTemplate mongoTemplate;
    private final TransactionTemplate txTemplate;

    public void transfer(String fromId, String toId, BigDecimal amount) {
        txTemplate.execute(status -> {
            // операции в транзакции
            mongoTemplate.updateFirst(...);
            mongoTemplate.updateFirst(...);
            return null;
        });
    }
}
```

**Важно:** `@Transactional` требует `Replica Set`; на standalone-сервере транзакции **не работают**. Для тестирования используйте `Testcontainers` с `Replica Set`.

## Q34. Как настроить подключение к `MongoDB` в `Spring Boot`?

**application.yml:**

```yaml
spring:
  data:
    mongodb:
      # Простой URI
      uri: mongodb://user:password@host1:27017,host2:27017/mydb?replicaSet=rs0&readPreference=secondaryPreferred

      # Или отдельные свойства
      host: localhost
      port: 27017
      database: mydb
      username: admin
      password: secret
      authentication-database: admin
```

**Программная конфигурация:**

```java
@Configuration
public class MongoConfig extends AbstractMongoClientConfiguration {

    @Override
    protected String getDatabaseName() {
        return "mydb";
    }

    @Override
    public MongoClient mongoClient() {
        return MongoClients.create(
            MongoClientSettings.builder()
                .applyConnectionString(new ConnectionString(
                    "mongodb://host1:27017,host2:27017/?replicaSet=rs0"))
                .writeConcern(WriteConcern.MAJORITY)
                .readPreference(ReadPreference.secondaryPreferred())
                .applyToConnectionPoolSettings(pool ->
                    pool.maxSize(50)
                        .minSize(5)
                        .maxWaitTime(5, TimeUnit.SECONDS))
                .applyToSocketSettings(socket ->
                    socket.connectTimeout(3, TimeUnit.SECONDS)
                        .readTimeout(10, TimeUnit.SECONDS))
                .build()
        );
    }
}
```

Для подключения к нескольким базам данных потребуется несколько `MongoTemplate` с разными `MongoDatabaseFactory`. Подробнее — в [вопросах по Spring Boot](../frameworks/spring/spring-boot-interview.md).

## Q35. (!) Как оптимизировать запросы в `MongoDB`?

**1. Анализ через `explain()`:**

```javascript
db.orders.find({ status: "ACTIVE" }).explain("executionStats")
// Ключевые метрики:
// - totalDocsExamined vs nReturned (чем ближе, тем лучше)
// - executionTimeMillis
// - stage: IXSCAN (индекс) vs COLLSCAN (полный скан)
```

**2. Правильные индексы:**
- Составной индекс по правилу ESR (Equality, Sort, Range)
- `Covered queries` — все поля в индексе
- `Partial index` — индексировать только нужное подмножество

**3. Проекция — возвращать только нужные поля:**

```javascript
db.users.find({ status: "ACTIVE" }, { name: 1, email: 1, _id: 0 })
```

**4. Пагинация:**

```javascript
// Плохо для больших offset (skip сканирует все пропущенные документы)
db.orders.find().sort({ _id: 1 }).skip(100000).limit(20)

// Хорошо — keyset pagination
db.orders.find({ _id: { $gt: lastSeenId } }).sort({ _id: 1 }).limit(20)
```

**5. Оптимизация агрегаций:**
- `$match` первым (использует индексы)
- `$project` для удаления ненужных полей до `$group`
- `allowDiskUse: true` для больших данных

**6. Мониторинг медленных запросов:**

```javascript
// Включить profiler для запросов медленнее 100ms
db.setProfilingLevel(1, { slowms: 100 })

// Просмотр медленных запросов
db.system.profile.find().sort({ ts: -1 }).limit(10)
```

В `Spring Data MongoDB` пагинация через `Pageable`, а для бесконечной ленты — запрос по `_id` с курсором. Индексы создаются через `@Indexed`, `@CompoundIndex` или `MongoTemplate.indexOps()`.

## Q36. Что такое `Change Streams`?

**Change Streams** — подписка на изменения данных в реальном времени (аналог `CDC` — `Change Data Capture`). Работают поверх `Oplog`.

```java
// Java — подписка на изменения коллекции
MongoCollection<Document> collection = database.getCollection("orders");

// Фильтр: только insert и update
List<Bson> pipeline = List.of(
    Aggregates.match(Filters.in("operationType", "insert", "update"))
);

ChangeStreamIterable<Document> changeStream = collection.watch(pipeline);
changeStream.forEach(event -> {
    System.out.println("Operation: " + event.getOperationType());
    System.out.println("Document: " + event.getFullDocument());
    // Сохранить resumeToken для возобновления после сбоя
    BsonDocument resumeToken = event.getResumeToken();
});

// Возобновление с токена
collection.watch().resumeAfter(savedResumeToken);
```

**Применение:**
- Инвалидация кэша ([Redis](redis-interview.md))
- Синхронизация с поисковым движком ([Elasticsearch](elasticsearch-interview.md))
- Event-driven архитектура (публикация событий в [Kafka](../messaging/kafka-interview.md))
- Аудит изменений

**Требования:** `Replica Set` или шардированный кластер. В шардированном кластере порядок между шардами **не гарантируется**.

## Q37. Как мониторить производительность `MongoDB`?

**Ключевые метрики:**

| Метрика | Что показывает | Алерт при |
|---------|---------------|-----------|
| `opcounters` | insert/query/update/delete в секунду | Резкий рост |
| `connections.current` | Активные соединения | > 80% пула |
| `wiredTiger.cache` | Утилизация кэша | Eviction rate > 0 |
| `repl.lag` | Отставание реплик (секунды) | > 10 сек |
| `globalLock.currentQueue` | Очередь на блокировку | > 0 (устойчиво) |

**Инструменты:**
- `mongostat` — realtime метрики (inserts/queries/updates)
- `mongotop` — время на чтение/запись по коллекциям
- `db.serverStatus()` — полный снимок метрик
- `Prometheus` + `mongodb-exporter` — дашборды в `Grafana`
- `MongoDB Atlas` — встроенный мониторинг (облако)

**Profiler для медленных запросов:**

```javascript
db.setProfilingLevel(1, { slowms: 100 })
db.system.profile.find({ millis: { $gt: 500 } }).sort({ ts: -1 })
```

В `Spring Boot` — мониторинг через `Micrometer`: метрики пула соединений и латентности экспортируются в `Prometheus` при наличии `micrometer-registry-prometheus`. Подробнее — в [вопросах по Observability](../monitoring/observability-interview.md) и [метриках и трейсинге](../monitoring/metrics-tracing-interview.md).

## Q38. Как выполнять миграции данных?

**Подходы к миграциям:**

1. **`Mongock`** (рекомендуется для `Spring`) — фреймворк миграций, аналог `Flyway`/`Liquibase`:

```java
@ChangeUnit(id = "add-status-field", order = "001", author = "dev")
public class AddStatusFieldMigration {

    @Execution
    public void execution(MongoTemplate mongoTemplate) {
        mongoTemplate.updateMulti(
            new Query(Criteria.where("status").exists(false)),
            new Update().set("status", "ACTIVE"),
            "users"
        );
    }

    @RollbackExecution
    public void rollback(MongoTemplate mongoTemplate) {
        mongoTemplate.updateMulti(
            new Query(),
            new Update().unset("status"),
            "users"
        );
    }
}
```

2. **`migrate-mongo`** (для Node.js / скрипты)
3. **Кастомные скрипты** в `mongosh` с версионированием

**Лучшие практики:**
- Обратная совместимость: новые поля — опциональные; удаление полей — в несколько шагов
- Постепенная миграция больших коллекций фоновой задачей (`bulkWrite` батчами)
- Тестирование миграций на копии данных
- Откат: обратные скрипты или `point-in-time recovery`

## Q39. Как обеспечить безопасность `MongoDB`?

**Аутентификация:**
- `SCRAM-SHA-256` — стандартный механизм (логин/пароль)
- `x.509` сертификаты — для production
- `LDAP` / `Kerberos` — интеграция с корпоративной инфраструктурой

**Авторизация (роли):**

| Роль | Описание |
|------|----------|
| `read` | Только чтение |
| `readWrite` | Чтение и запись |
| `dbAdmin` | Администрирование БД (индексы, статистика) |
| `userAdmin` | Управление пользователями |
| `clusterAdmin` | Управление кластером |
| Кастомные роли | Гранулярные привилегии на ресурсы |

```javascript
db.createUser({
    user: "appUser",
    pwd: "securePassword",
    roles: [{ role: "readWrite", db: "myapp" }]
})
```

**Дополнительные меры:**
- **TLS/SSL** для шифрования трафика
- **Сетевая изоляция** — `MongoDB` не должна быть доступна из интернета
- **`bindIp`** — привязка к конкретным IP
- **Audit log** — журнал действий для compliance
- Регулярное обновление версии

Подробнее о безопасности — в [вопросах по безопасности приложений](../security/application-security-interview.md).

## Q40. Когда `MongoDB` -- плохой выбор?

**Сценарии, где `MongoDB` не оптимальна:**

1. **Сложные связи между сущностями** — множество `JOIN`-ов и связей many-to-many эффективнее в РСУБД (см. [SQL](sql-interview.md))
2. **Строгие `ACID`-транзакции** — хотя транзакции есть, overhead велик; для финансовых систем РСУБД надёжнее
3. **Аналитические запросы по колонкам** — колоночные БД (`ClickHouse`, `Cassandra`) эффективнее для OLAP
4. **Небольшие данные с фиксированной схемой** — нет преимуществ перед `PostgreSQL`
5. **Сильная нормализация** — `MongoDB` оптимизирована для денормализации
6. **Полнотекстовый поиск** — встроенный поиск ограничен; [Elasticsearch](elasticsearch-interview.md) мощнее

**Вопрос на собеседовании:** "Расскажите о проекте, где вы выбрали `MongoDB`, и как обосновали выбор?" — ожидают конкретные критерии: паттерны доступа, требования к масштабируемости, структура данных, [CAP](../architecture/cap-theorem-interview.md)-компромиссы и альтернативы, которые рассматривали.

## Q41. (!) Как работает `$lookup` с вложенным `pipeline` и `$unwind`?

`$lookup` — агрегационный оператор для `JOIN`-подобных операций между коллекциями. С версии MongoDB 3.6 поддерживает вложенный `pipeline` для сложных сценариев.

**Простой $lookup:**

```javascript
// orders → products (по product_id)
db.orders.aggregate([
  {
    $lookup: {
      from: "products",
      localField: "product_id",
      foreignField: "_id",
      as: "product_info"
    }
  }
])
// product_info — массив найденных документов
```

**$lookup с вложенным pipeline (фильтрация на стороне joined-коллекции):**

```javascript
db.orders.aggregate([
  {
    $lookup: {
      from: "products",
      let: { pid: "$product_id", minPrice: "$min_price" },
      pipeline: [
        { $match: { $expr: { $and: [
          { $eq: ["$$pid", "$_id"] },
          { $gte: ["$price", "$$minPrice"] }
        ]}}},
        { $project: { name: 1, price: 1 } }
      ],
      as: "product_info"
    }
  }
])
```

**$unwind — разворачивание массивов:**

`$unwind` превращает документ с массивом в несколько документов — по одному на каждый элемент массива. Необходим перед `$group` по полям массива.

```javascript
// Заказ с массивом items → отдельные строки на каждый item
db.orders.aggregate([
  { $unwind: "$items" },               // разворачиваем массив items
  {
    $group: {
      _id: "$items.product_id",
      totalQty: { $sum: "$items.quantity" },
      revenue: { $sum: { $multiply: ["$items.price", "$items.quantity"] } }
    }
  },
  { $sort: { revenue: -1 } },
  { $limit: 10 }
])
```

```mermaid
graph LR
    D1["Order {items:[A,B,C]}"] -->|"$unwind"| R1["Order + item A"]
    D1 -->|"$unwind"| R2["Order + item B"]
    D1 -->|"$unwind"| R3["Order + item C"]
    R1 & R2 & R3 -->|"$group"| G["Aggregated result"]
```

**preserveNullAndEmptyArrays:** по умолчанию `$unwind` отфильтровывает документы с null/пустым массивом. Флаг `preserveNullAndEmptyArrays: true` сохраняет их.

**Производительность:** `$lookup` выполняется на mongos/primary, не поддерживает индексы joined-коллекции в старых версиях. В MongoDB 5.0+ появились оптимизации. Для high-load сценариев предпочитайте `embedding` или денормализацию.

## Q42. (!) Что такое `Change Streams` и как использовать в `Spring`?

`Change Streams` — механизм подписки на изменения данных в MongoDB в реальном времени. Основан на `oplog` (журнале операций `Replica Set`). Требует работы в режиме `Replica Set` (или `Sharded Cluster`).

**Что можно отслеживать:**
- Изменения в конкретной коллекции
- Изменения в базе данных
- Изменения во всём кластере

**Типы событий:** `insert`, `update`, `replace`, `delete`, `invalidate`, `drop`, `rename`

**Пример с Java Driver:**

```java
MongoCollection<Document> collection = database.getCollection("orders");

// Открыть поток изменений
MongoCursor<ChangeStreamDocument<Document>> cursor =
    collection.watch().iterator();

while (cursor.hasNext()) {
    ChangeStreamDocument<Document> change = cursor.next();
    System.out.println("Operation: " + change.getOperationType());
    System.out.println("Document: " + change.getFullDocument());
    System.out.println("Resume token: " + change.getResumeToken());
}
```

**Resume Token** — позволяет возобновить прослушивание с места остановки после перезапуска приложения:

```java
BsonDocument resumeToken = lastProcessedToken; // сохранить в Redis/БД
collection.watch().resumeAfter(resumeToken).iterator();
```

**Spring Data MongoDB — реактивный подход:**

```java
@Component
public class OrderChangeListener {

    @Autowired
    private ReactiveMongoTemplate mongoTemplate;

    @PostConstruct
    public void listen() {
        mongoTemplate.changeStream(
            "orders",
            ChangeStreamOptions.builder()
                .filter(Aggregation.newAggregation(
                    Aggregation.match(Criteria.where("operationType").is("insert"))
                ))
                .build(),
            Order.class
        )
        .doOnNext(event -> processNewOrder(event.getBody()))
        .subscribe();
    }
}
```

**Типичные применения:**
- Event-driven синхронизация кэша при изменении данных
- Аудит-лог изменений
- CDC (Change Data Capture) для репликации в другие хранилища
- Триггеры без хранимых процедур

## Q43. (!) Как работают транзакции на нескольких документах и коллекциях?

`MongoDB` поддерживает многодокументные `ACID`-транзакции начиная с версии 4.0 (Replica Set) и 4.2 (Sharded Cluster). По умолчанию операции на одном документе всегда атомарны.

**Синтаксис транзакции:**

```java
// Java Driver
ClientSession session = mongoClient.startSession();
TransactionOptions opts = TransactionOptions.builder()
    .readPreference(ReadPreference.primary())
    .readConcern(ReadConcern.SNAPSHOT)
    .writeConcern(WriteConcern.MAJORITY)
    .build();

try {
    session.withTransaction(() -> {
        // Все операции в рамках транзакции
        ordersCollection.insertOne(session, newOrder);
        inventoryCollection.updateOne(session,
            eq("product_id", productId),
            inc("quantity", -1)
        );
        return null;
    }, opts);
} catch (MongoException e) {
    // Транзакция автоматически откатывается
}
```

**Spring Data MongoDB с @Transactional:**

```java
@Configuration
public class MongoConfig {
    @Bean
    public MongoTransactionManager transactionManager(MongoDatabaseFactory dbFactory) {
        return new MongoTransactionManager(dbFactory);
    }
}

@Service
public class OrderService {
    @Transactional  // требует MongoTransactionManager + Replica Set
    public void placeOrder(Order order) {
        orderRepository.save(order);
        inventoryRepository.decrementStock(order.getProductId(), order.getQuantity());
        // При исключении — автоматический rollback
    }
}
```

**Ограничения и overhead:**

| Аспект | Детали |
|--------|--------|
| Время жизни | По умолчанию 60 сек, затем автоматический abort |
| Размер | Лимит 16 MB на операцию записи в транзакции |
| Производительность | Overhead ~3–5x vs обычных операций |
| DDL | Нельзя создавать коллекции внутри транзакции (до 4.4) |
| Шардинг | Требует `mongos` и inter-shard транзакции — более высокий overhead |

**Рекомендации:** используйте транзакции только при необходимости. Для большинства случаев — денормализация (`embedding`) устраняет необходимость транзакций между коллекциями.

## Q44. Что такое `Time Series Collections` в `MongoDB`?

`Time Series Collections` — специальный тип коллекции в MongoDB 5.0+, оптимизированный для хранения временных рядов (метрики, IoT-данные, логи с временными метками).

**Создание:**

```javascript
db.createCollection("sensor_readings", {
  timeseries: {
    timeField: "timestamp",     // обязательное поле с временной меткой
    metaField: "metadata",      // поле для группировки (sensor_id, region)
    granularity: "seconds"      // seconds | minutes | hours
  },
  expireAfterSeconds: 2592000   // TTL = 30 дней
})
```

**Запись данных:**

```javascript
db.sensor_readings.insertMany([
  {
    timestamp: new Date("2026-04-13T10:00:00Z"),
    metadata: { sensor_id: "sensor_01", region: "Moscow" },
    temperature: 22.5,
    humidity: 65
  },
  {
    timestamp: new Date("2026-04-13T10:01:00Z"),
    metadata: { sensor_id: "sensor_01", region: "Moscow" },
    temperature: 22.7,
    humidity: 64
  }
])
```

**Агрегация по времени:**

```javascript
db.sensor_readings.aggregate([
  {
    $match: {
      "metadata.sensor_id": "sensor_01",
      timestamp: { $gte: ISODate("2026-04-13T00:00:00Z") }
    }
  },
  {
    $group: {
      _id: { $dateTrunc: { date: "$timestamp", unit: "hour" } },
      avgTemp: { $avg: "$temperature" },
      maxTemp: { $max: "$temperature" }
    }
  },
  { $sort: { "_id": 1 } }
])
```

**Преимущества vs обычные коллекции:**
- Данные автоматически сжимаются и группируются по временным "бакетам" внутри движка
- Экономия памяти до 70–90% для однородных метрик
- Оптимизированные range-запросы по временному полю
- TTL работает по `timeField` автоматически

**Ограничения:** нельзя обновлять или удалять отдельные документы напрямую (только через `deleteOne`/`deleteMany` с фильтром). Схема плоская — нет сложных вложенных операций обновления.

## Q45. Как выбрать стратегию шардирования: зональное vs хэш-шардирование?

Выбор стратегии шардирования критичен для производительности и балансировки нагрузки.

**Хэш-шардирование (`hashed`):**

```javascript
sh.shardCollection("mydb.orders", { customer_id: "hashed" })
```

- Равномерное распределение данных по шардам
- Не поддерживает range-запросы по ключу шардирования
- Горячие точки исключены
- Используется при высокой нагрузке на запись с монотонно растущими ID

**Range-шардирование (`range`):**

```javascript
sh.shardCollection("mydb.sensors", { region: 1, timestamp: 1 })
```

- Поддерживает эффективные range-запросы (`$gte`, `$lte`)
- Риск "горячего шарда" при монотонном ключе (например, `ObjectId` по умолчанию)
- Оптимально для временных рядов + регионального фильтра

**Зональное шардирование (`Zone Sharding`):**

Позволяет привязать диапазоны shard key к конкретным шардам (географическая локализация данных):

```javascript
// Тег зоны к шарду
sh.addShardToZone("shard0001", "EU")
sh.addShardToZone("shard0002", "US")

// Диапазон ключей → зона
sh.updateZoneKeyRange("mydb.users",
  { country: "DE" }, { country: "DZ" },
  "EU"
)
sh.updateZoneKeyRange("mydb.users",
  { country: "US" }, { country: "UU" },
  "US"
)
```

**Выбор стратегии:**

```mermaid
graph TD
    A["Нужны range-запросы по shard key?"] -->|Да| B["Range sharding"]
    A -->|Нет| C["Есть требования к локализации данных?"]
    C -->|Да| D["Zone sharding"]
    C -->|Нет| E["Hashed sharding"]
    B --> F{"Монотонный ключ?"}
    F -->|Да| G["Риск горячего шарда\n→ добавить рандомизацию\nили composite key"]
    F -->|Нет| H["Оптимально"]
```

## Q46. Что такое `Atlas Search` и как он отличается от встроенного `text`-индекса?

`Atlas Search` — полнотекстовый поиск на базе `Apache Lucene`, встроенный в `MongoDB Atlas`. Значительно мощнее стандартного `text`-индекса `MongoDB`.

**Сравнение:**

| Возможность | `text`-индекс | `Atlas Search` |
|-------------|--------------|----------------|
| Движок | Собственный MongoDB | Apache Lucene |
| Нечёткий поиск (fuzzy) | Нет | Да |
| Автодополнение | Нет | Да (`autocomplete`) |
| Фасетный поиск | Нет | Да (`facet`) |
| Подсветка результатов | Нет | Да (`highlight`) |
| Синонимы | Нет | Да |
| Языковые анализаторы | Ограничено | Полный набор Lucene |
| Развёртывание | Self-hosted | Atlas (облако) |

**Создание Search Index:**

```javascript
// В Atlas через UI или API
{
  "name": "products_search",
  "definition": {
    "mappings": {
      "dynamic": false,
      "fields": {
        "name": { "type": "string", "analyzer": "lucene.russian" },
        "description": { "type": "string" },
        "price": { "type": "number" },
        "category": { "type": "stringFacet" }
      }
    }
  }
}
```

**Запрос через $search:**

```javascript
db.products.aggregate([
  {
    $search: {
      index: "products_search",
      compound: {
        must: [{
          text: {
            query: "детское кресло",
            path: ["name", "description"],
            fuzzy: { maxEdits: 1 },  // нечёткий поиск
            score: { boost: { value: 2 } }
          }
        }],
        filter: [{
          range: { path: "price", gte: 1000, lte: 10000 }
        }]
      }
    }
  },
  {
    $facet: {
      results: [{ $limit: 10 }],
      categories: [{
        $searchMeta: {
          facet: { operator: { exists: { path: "category" } },
            facets: { categoryFacet: { type: "string", path: "category" } }
          }
        }
      }]
    }
  }
])
```

**Self-hosted альтернатива:** для on-premise MongoDB используйте интеграцию с `Elasticsearch` через `MongoDB Connector for BI` или `Kafka Connect MongoDB Source Connector` → `Elasticsearch Sink Connector`.

---

## See also

- [SQL](sql-interview.md) — реляционные запросы и сравнение с NoSQL
- [Архитектура баз данных](database-architecture-interview.md) — выбор типа БД, CAP-теорема
- [Elasticsearch](elasticsearch-interview.md) — полнотекстовый поиск и аналитика
- [Redis](redis-interview.md) — кэширование и in-memory хранилище
- [Cassandra](cassandra-interview.md) — другая NoSQL БД (колоночная модель)
- [CAP-теорема](../architecture/cap-theorem-interview.md) — компромиссы распределённых систем
- [Hibernate / JPA](hibernate-interview.md) — ORM для реляционных БД

- [Apache Cassandra](cassandra-interview.md)
- [ClickHouse](clickhouse-interview.md)
- [CockroachDB](cockroachdb-interview.md)
- [Database Architecture](database-architecture-interview.md)
- [Транзакции и уровни изоляции](database-transactions-interview.md)
- [DynamoDB](dynamodb-interview.md)
- [Шпаргалка: MongoDB: Полное руководство по документо](../../databases/nosql/mongodb/mongodb.md) — теория
