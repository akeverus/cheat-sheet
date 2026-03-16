---
title: "Вопросы на собеседовании: сериализация в Kotlin"
description: "Краткое введение: ответы по сериализации в Kotlin — kotlinx.serialization, @Serializable, JSON, полиморфизм, кастомные сериализаторы. На собеседованиях часто спрашивают про аннотацию @Serializable, настройку Json, полиморфную сериализацию и кастомные сериализаторы."
tags: ["interview", "programming-languages", "kotlin-serialization-interview"]
difficulty: "intermediate"
prerequisites: []
next: []
updated: "2026-02-11"
---
# Вопросы на собеседовании: сериализация в `Kotlin`

Краткое введение: ответы по сериализации в `Kotlin` — `kotlinx.serialization`, `@Serializable`, `JSON`, полиморфизм, кастомные сериализаторы. На собеседованиях часто спрашивают про аннотацию `@Serializable`, настройку `Json`, полиморфную сериализацию и кастомные сериализаторы.

Дата последнего обновления: 2026-02-11

## Полезные ссылки

### Официальная документация

- [kotlinx.serialization](https://github.com/Kotlin/kotlinx.serialization)
- [Serialization guide](https://kotlinlang.org/docs/serialization.html)

### См. также

- [`kotlin-interview.md`](kotlin-interview.md) — основы Kotlin
- [`kotlin-interop-java-interview.md`](kotlin-interop-java-interview.md) — интероп с Java
- [`../java/java-serialization-interview.md`](../java/java-serialization-interview.md) — сериализация в Java

## Содержание

- [Полезные ссылки](#полезные-ссылки)

**Основы и объявление**
- [Q1. (!) Что такое kotlinx.serialization и чем отличается от Jackson/Gson?](#q1-важно-что-такое-kotlinxserialization-и-чем-отличается-от-jacksongson)
- [Q2. Как объявить класс для сериализации в JSON?](#q2-как-объявить-класс-для-сериализации-в-json)

**Настройка и аннотации полей**
- [Q3. Как настроить Json (prettyPrint, ignoreUnknownKeys, encodeDefaults)?](#q3-как-настроить-json-prettyprint-ignoreunknownkeys-encodedefaults)
- [Q4. Как переименовать поле в JSON (@SerialName)?](#q4-как-переименовать-поле-в-json-serialname)
- [Q5. Как исключить поле из сериализации (@Transient)?](#q5-как-исключить-поле-из-сериализации-transient)

**Полиморфизм и кастомные сериализаторы**
- [Q6. Как сериализовать sealed class и полиморфные иерархии?](#q6-как-сериализовать-sealed-class-и-полиморфные-иерархии)
- [Q7. Когда нужен кастомный сериализатор (KSerializer) и как его написать?](#q7-когда-нужен-кастомный-сериализатор-kserializer-и-как-его-написать)

**Форматы и типы**
- [Q8. Какие форматы поддерживает kotlinx.serialization кроме JSON?](#q8-какие-форматы-поддерживает-kotlinxserialization-кроме-json)
- [Q9. Как сериализовать даты и кастомные типы (LocalDate, UUID)?](#q9-как-сериализовать-даты-и-кастомные-типы-localdate-uuid)
- [Q10. В чём разница между Json.encodeToString и Json.decodeFromString и когда ловить исключения?](#q10-в-чём-разница-между-jsonencodetostring-и-jsondecodefromstring-и-когда-ловить-исключения)
- [Q11. Как сериализовать nullable-поля и optional-значения?](#q11-как-сериализовать-nullable-поля-и-optional-значения)
- [Q12. Как работает JsonElement и когда использовать encodeToJsonElement?](#q12-как-работает-jsonelement-и-когда-использовать-encodetojsonelement)
- [Q13. Как сериализовать generic-классы (List<T>, Map<K,V>)?](#q13-как-сериализовать-generic-классы-listt-mapkv)
- [Q14. В чём разница между kotlinx.serialization и Java Serialization?](#q14-в-чём-разница-между-kotlinxserialization-и-java-serialization)
- [Q15. Как использовать @SerialName на уровне класса и для обратной совместимости?](#q15-как-использовать-serialname-на-уровне-класса-и-для-обратной-совместимости)

## Q1. (!) Что такое kotlinx.serialization и чем отличается от Jackson/Gson?

**kotlinx.serialization** — официальная библиотека Kotlin для сериализации и десериализации в различные форматы (JSON, Protocol Buffers, CBOR и др.). Сериализаторы генерируются на этапе компиляции через плагин компилятора Kotlin или KSP, поэтому типы проверяются компилятором и в рантайме рефлексия не требуется. Это даёт типобезопасность и предсказуемую производительность.

Jackson и Gson работают через рефлексию и из коробки не учитывают особенности Kotlin: nullable-типы, default-значения в конструкторе, `data class`. Для Jackson часто подключают `jackson-module-kotlin`, чтобы корректно вызывать конструкторы с default-параметрами и работать с nullability. kotlinx.serialization нативно поддерживает `data class`, nullable-поля, default-значения, sealed-классы и генерирует код под выбранный формат; один и тот же класс можно сериализовать в JSON, protobuf и т.д. без дублирования логики.

## Q2. Как объявить класс для сериализации в JSON?

Класс помечают аннотацией **`@Serializable`**. Обычно используют **data class** с полями, типы которых имеют встроенный или зарегистрированный сериализатор (примитивы, строки, другие `@Serializable` классы, списки, мапы). Пример: `@Serializable data class User(val id: Int, val name: String)`. В проекте должен быть подключён плагин `org.jetbrains.kotlin.plugin.serialization` и зависимость `kotlinx-serialization-json`.

Сериализация: `Json.encodeToString(user)` или `Json.encodeToJsonElement(user)`. Десериализация: `Json.decodeFromString<User>(json)` или `Json.decodeFromJsonElement<User>(element)`. Все вложенные объекты тоже должны быть помечены `@Serializable` (или для них зарегистрирован кастомный сериализатор). Для полей с нестандартным именем в JSON используют `@SerialName("key")`.

```kotlin
@Serializable
data class User(val id: Int, val name: String)

val user = User(1, "Alice")
val json = Json.encodeToString(user)           // {"id":1,"name":"Alice"}
val back = Json.decodeFromString<User>(json)
```

## Q3. Как настроить Json (prettyPrint, ignoreUnknownKeys, encodeDefaults)?

Экземпляр конфигурации создают через `Json { ... }` с блоком настроек. **`prettyPrint = true`** — вывод с переносами и отступами, удобно для логов и отладки. **`ignoreUnknownKeys = true`** — при десериализации неизвестные ключи в JSON игнорируются, а не вызывают ошибку; полезно при обратной совместимости и при приёме данных от внешних API. **`encodeDefaults = true`** — поля со значениями по умолчанию тоже записываются в JSON; по умолчанию они опускаются для экономии места.

**`coerceInputValues = true`** — при несовпадении типа или недопустимом значении (например, неизвестный enum) подставлять default вместо исключения. **`isLenient = true`** — разрешать нестрогий JSON (например, необязательные кавычки в определённых режимах). Конфиг обычно создают один раз (например, в `object` или через DI) и переиспользуют.

```kotlin
val json = Json {
    prettyPrint = true
    ignoreUnknownKeys = true
    encodeDefaults = true
    coerceInputValues = true
}
val str = json.encodeToString(obj)
val obj = json.decodeFromString<Dto>(str)
```

## Q4. Как переименовать поле в JSON (@SerialName)?

Аннотация **`@SerialName("name")`** на свойстве задаёт имя ключа в сериализованном виде (JSON, XML и т.д.). Пример: `@Serializable data class Dto(@SerialName("user_name") val userName: String)` — в JSON будет ключ `"user_name"`, а не `"userName"`. Удобно для соответствия внешнему API (snake_case, специфичные имена полей) или для обратной совместимости со старыми контрактами.

Тот же механизм используется для enum: `@SerialName("val")` на константе перечисления задаёт её представление в JSON. Без `@SerialName` по умолчанию используется имя свойства (в Kotlin) или имя константы enum.

```kotlin
@Serializable
data class Dto(@SerialName("user_name") val userName: String)
// JSON: {"user_name": "Alice"}
```

## Q5. Как исключить поле из сериализации (@Transient)?

Свойство, которое не должно попадать в сериализованный вывод, помечают **`@Transient`**. Оно не будет записано при сериализации. При десериализации такому полю присваивается значение по умолчанию: для примитивов — 0, false; для ссылочных типов — null; для типов без default нужно задать значение в самом объявлении поля или в конструкторе (default-аргумент).

Подходит для вычисляемых полей, кэшей, служебных данных. Если поле должно читаться из JSON, но не записываться (или наоборот), потребуется кастомный сериализатор или обход через `@Serializable` с переопределением сериализатора для класса.

## Q6. Как сериализовать sealed class и полиморфные иерархии?

Для полиморфной сериализации нужен **SerializersModule**: в нём регистрируют подтипы базового класса через `polymorphic(BaseClass::class) { subclass(SubClass1::class); subclass(SubClass2::class) }`. Этот модуль передают в конфиг: `Json { serializersModule = myModule }`. В JSON при сериализации добавляется **дискриминатор** — ключ (например, `type`), по значению которого при десериализации выбирается нужный подтип. Имя ключа задаётся через `classDiscriminator` в конфиге.

Для **sealed class** все подтипы известны компилятору, поэтому поддержка тесная: подтипы помечают `@Serializable`, в модуле перечисляют их в `polymorphic(SealedClass::class) { ... }`, при необходимости задают `@SerialName` для каждого подтипа, чтобы в JSON был читаемый дискриминатор. Тогда сериализация и десериализация полиморфных иерархий работают типобезопасно.

## Q7. Когда нужен кастомный сериализатор (KSerializer) и как его написать?

Кастомный сериализатор нужен, когда тип не поддерживается по умолчанию (собственные типы дат, `UUID`, сложные обёртки) или требуется нестандартное представление в JSON (одно поле в объект, формат даты, enum в виде числа). Реализуют интерфейс **`KSerializer<T>`** с методами **`serialize(encoder, value)`** и **`deserialize(decoder)`**: в них используют методы `encoder`/`decoder` для записи и чтения примитивов, строк, вложенных структур.

Сериализатор подключают через **`@Serializable(with = MySerializer::class)`** на типе или регистрируют в **SerializersModule** и передают модуль в конфиг `Json`. Для enum часто достаточно `@SerialName` на константах; при сложной логике (например, парсинг из числа или составной строки) пишут свой `KSerializer<MyEnum>`.

```kotlin
@Serializable(with = LocalDateSerializer::class)
data class Event(val date: LocalDate, val name: String)

object LocalDateSerializer : KSerializer<LocalDate> {
    override fun serialize(encoder: Encoder, value: LocalDate) {
        encoder.encodeString(value.toString())
    }

    override fun deserialize(decoder: Decoder): LocalDate =
        LocalDate.parse(decoder.decodeString())
}
```

## Q8. Какие форматы поддерживает kotlinx.serialization кроме JSON?

В экосистеме kotlinx.serialization есть форматы: **JSON** (`kotlinx-serialization-json`), **Protocol Buffers** (`kotlinx-serialization-protobuf`), **CBOR**, **Properties**. Для XML и других форматов существуют отдельные артефакты или их реализуют через кастомные сериализаторы и форматы.

Один и тот же класс с `@Serializable` можно сериализовать в разные форматы: подключают нужную зависимость, создают экземпляр формата (`Json`, `ProtoBuf` и т.д.) с общими или разными настройками и вызывают `encodeToString`/`decodeFromString` (или бинарные аналоги). Логика модели общая; меняется только формат вывода и конфигурация (например, имена полей в protobuf задаются в схеме).

## Q9. Как сериализовать даты и кастомные типы (LocalDate, UUID)?

Типы из стандартной библиотеки Kotlin и примитивы JVM поддерживаются из коробки; **`java.time`** (например, `LocalDate`, `Instant`) и **`UUID`** — нет. Для них есть два пути: (1) использовать готовые плагины/сериализаторы из экосистемы (например, для `java.time` и `UUID` существуют сторонние или официальные сериализаторы, которые регистрируют в `SerializersModule`); (2) написать свой **KSerializer**.

Для даты обычно сериализуют в строку (ISO-8601) или в число (timestamp). В кастомном сериализаторе в `serialize` пишут в encoder строку или число, в `deserialize` читают и парсят. Для `UUID` — аналогично, чаще всего строка. Сериализатор подключают через `@Serializable(with = ...)` на типе или через регистрацию в модуле, чтобы не помечать каждый класс с полем типа `LocalDate`/`UUID` отдельно.

## Q10. В чём разница между Json.encodeToString и Json.decodeFromString и когда ловить исключения?

**`Json.encodeToString(serializer, value)`** (или перегрузка с reified типом) сериализует объект в строку JSON. Исключения при сериализации возможны, если сериализатор или формат не поддерживают значение (редко при корректной модели). **`Json.decodeFromString<T>(string)`** десериализует строку в объект; здесь исключения типичны: невалидный JSON, отсутствующие обязательные поля, несовпадение типов, неизвестный дискриминатор в полиморфной иерархии.

Рекомендуется оборачивать **decodeFromString** в `try/catch` (или использовать `runCatching`) и обрабатывать **SerializationException** и подтипы: логировать, возвращать default или передавать ошибку выше. Для входящих данных из сети или от пользователя валидация и обработка ошибок десериализации обязательны. При **encodeToString** ошибки обычно указывают на ошибку в модели или конфиге и обрабатываются на этапе разработки/тестов.

## Q11. Как сериализовать nullable-поля и optional-значения?

**Nullable-поля** (`String?`, `Int?`) сериализуются нативно: при значении `null` в JSON записывается `null` (или ключ опускается, если `encodeDefaults = false` и настроено исключение null). При десериализации отсутствующий ключ или `null` даёт `null` в Kotlin. Это естественно для JSON и не требует дополнительной настройки. Для `Optional` (Java) или собственных optional-типов нужен кастомный сериализатор или обёртка.

**Default-значения** в конструкторе: при десериализации, если ключ отсутствует, используется default. С `encodeDefaults = true` default-значения тоже пишутся в JSON; с `false` — опускаются (экономия места). Для «опционального поля, которое может отсутствовать в старых версиях API» сочетание nullable + default работает: `val tags: List<String>? = null` — отсутствующий ключ даст `null`, старый API может не присылать поле.

## Q12. Как работает JsonElement и когда использовать encodeToJsonElement?

**JsonElement** — дерево представления JSON (аналог DOM): `JsonObject`, `JsonArray`, `JsonPrimitive`, `JsonNull`. **`Json.encodeToJsonElement(value)`** сериализует объект в это дерево без преобразования в строку. Полезно, когда нужно модифицировать JSON (добавить/удалить поля, изменить значение), объединить несколько структур или передать в API, работающее с деревом.

**`Json.decodeFromJsonElement<T>(element)`** — обратная операция: десериализация из дерева. Сценарии: парсинг JSON в `JsonElement` через `Json.parseToJsonElement(string)`, затем ручная валидация или модификация, и только потом десериализация в тип. Или получение поддерева: `element.jsonObject["user"]` и `decodeFromJsonElement<User>(subtree)`. Для простой сериализации в строку и обратно `encodeToString`/`decodeFromString` достаточно; `JsonElement` — когда нужна работа с самой структурой JSON.

## Q13. Как сериализовать generic-классы (List<T>, Map<K,V>)?

**Стандартные generic-коллекции** (`List<T>`, `Map<K, V>`, `Set<T>`) поддерживаются из коробки, если `T`, `K`, `V` — сериализуемые типы. Пример: `@Serializable data class Response(val items: List<User>)` — `User` должен быть `@Serializable`. Для `Map<String, Any>` тип `Any` не сериализуем напрямую; нужен кастомный сериализатор или использование `JsonElement` как значения.

**Собственные generic-классы** требуют, чтобы параметр типа имел сериализатор. Для `@Serializable data class Wrapper<T>(val value: T)` компилятор сгенерирует код, но при вызове `decodeFromString<Wrapper<User>>` нужен reified `User` или явная передача сериализатора. Для `T` без ограничений (`T : Any`) сериализатор `T` неизвестен — используют `KSerializer` в параметре или `@Serializable(with = ...)` с сериализатором, принимающим `KType`.

## Q14. В чём разница между kotlinx.serialization и Java Serialization?

**Java Serialization** (`Serializable`, `ObjectOutputStream`/`ObjectInputStream`) — бинарный формат, привязанный к JVM, нечитаемый, хрупкий при изменении классов (версии, имена полей). Используется для сохранения состояния объектов в память, RMI, частично в сессиях. Требует `serialVersionUID`, поддерживает `transient`, но не учитывает nullability и default-значения Kotlin.

**kotlinx.serialization** — кросс-платформенная библиотека (Kotlin/JVM, JS, Native), формат настраивается (JSON, Protobuf, CBOR). Генерирует код на этапе компиляции, типобезопасна, поддерживает nullability, default-значения, sealed-классы. Для обмена данными по сети, API, конфигурации — предпочтительна. Java Serialization — для legacy-кода и сценариев, где нужна именно JVM-бинарная сериализация; для новых проектов и JSON/API — kotlinx.serialization.

## Q15. Как использовать @SerialName на уровне класса и для обратной совместимости?

**@SerialName на уровне класса** задаёт имя дискриминатора для полиморфной сериализации. В `polymorphic` блоке для каждого подтипа можно указать `@SerialName("subtype_name")` — в JSON в поле-дискриминаторе будет это значение. Это позволяет читаемо различать подтипы: `{"type": "success", "data": ...}` вместо `{"type": "Success"}`.

**Обратная совместимость**: при переименовании поля в Kotlin старое имя в JSON сохраняют через `@SerialName("old_name")` на новом свойстве. Для переименования класса в полиморфной иерархии можно зарегистрировать алиас: `serializersModule.polymorphic(Base::class) { subclass(NewClass::class, OldClassSerializer) }` или использовать `@SerialName` со старым значением. Для эволюции API рекомендуют стратегию: новые поля — nullable с default; удаление полей — через `@Transient` и игнорирование; переименование — `@SerialName` со старым именем.
