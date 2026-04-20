---
title: "Kotlin Metaprogramming"
description: "Кратко: руководство по метапрограммированию в Kotlin: Annotations Processing, KAPT, KSP, Code Generation и другие техники метапрограммирования."
tags:
  - languages
  - kotlin
  - kotlin-metaprogramming
difficulty: "intermediate"
prerequisites: []
next: []
updated: "2026-04-20"
---
# Kotlin Metaprogramming

Кратко: руководство по метапрограммированию в **Kotlin**: **Annotations Processing**, **KAPT**, **KSP**, **Code Generation** и другие техники метапрограммирования.

## Полезные ссылки

### Официальная документация
- [Kotlin Annotations](https://kotlinlang.org/docs/annotations.html)
- [KSP Overview](https://kotlinlang.org/docs/ksp-overview.html)

### Обучающие материалы
- [Kotlin Annotation Processing](https://www.baeldung.com/kotlin/annotation-processing)

### См. также
- [[kotlin-basics|Основы Kotlin]]
- [[kotlin-dsl|DSL в Kotlin]]

- [[kotlin-performance|Kotlin Performance]]
- [[kotlin-fp-basics|Kotlin Functional Programming: Basics]]
- [[kotlin-sealed-classes|Sealed классы и интерфейсы в Kotlin]]
## Содержание

- [Введение в метапрограммирование](#введение-в-метапрограммирование)
  - [Типы метапрограммирования в Kotlin](#типы-метапрограммирования-в-kotlin)
  - [Преимущества](#преимущества)
- [Annotations Processing](#annotations-processing)
  - [Создание аннотаций](#создание-аннотаций)
  - [Использование аннотаций](#использование-аннотаций)
  - [Обработка аннотаций через Reflection](#обработка-аннотаций-через-reflection)
- [KAPT (Kotlin Annotation Processing Tool)](#kapt-kotlin-annotation-processing-tool)
  - [Настройка KAPT](#настройка-kapt)

## Введение в метапрограммирование

Метапрограммирование — это техника написания программ, которые генерируют или модифицируют другие программы.

### Типы метапрограммирования в Kotlin

1. **Compile-time** — обработка аннотаций (KAPT, KSP)
2. **Runtime** — рефлексия
3. **Code Generation** — генерация кода

### Преимущества

- **Меньше boilerplate кода**
- **Автоматическая генерация кода**
- **Безопасность типов**
- **Производительность** (compile-time обработка)

## Annotations Processing

### Создание аннотаций

```kotlin
// Простая аннотация
@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.RUNTIME)
annotation class MyAnnotation

// Аннотация с параметрами
@Target(AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.RUNTIME)
annotation class Route(val path: String, val method: String = "GET")

// Аннотация для свойств
@Target(AnnotationTarget.PROPERTY)
@Retention(AnnotationRetention.RUNTIME)
annotation class Column(val name: String, val nullable: Boolean = false)
```

### Использование аннотаций

```kotlin
@MyAnnotation
class MyClass

@Route("/users", "GET")
fun getUsers(): List<User> {
    // ...
}

data class User(
    @Column("id", nullable = false)
    val id: Int,

    @Column("name")
    val name: String
)
```

### Обработка аннотаций через Reflection

```kotlin
import kotlin.reflect.full.*

// Получение аннотаций
val annotations = MyClass::class.annotations
val myAnnotation = MyClass::class.findAnnotation<MyAnnotation>()

// Проверка наличия аннотации
val hasAnnotation = MyClass::class.hasAnnotation<MyAnnotation>()

// Получение аннотаций функций
fun processFunction(function: KFunction<*>) {
    val route = function.findAnnotation<Route>()
    route?.let {
        println("Route: ${it.method} ${it.path}")
    }
}
```

## KAPT (Kotlin `Annotation Processing` Tool)

**KAPT** (Kotlin `Annotation Processing` Tool) — это инструмент для обработки аннотаций в **Kotlin**, который обеспечивает совместимость с существующими **Java Annotation Processors**. **KAPT** работает путем генерации **Java stubs** из **Kotlin** кода, которые затем обрабатываются стандартными **Java** процессорами аннотаций.

**KAPT** был первым решением для обработки аннотаций в **Kotlin** и до сих пор широко используется, особенно в проектах, которые уже используют **Java** процессоры аннотаций, такие как **Dagger**, **Room**, или **Data Binding**. Однако **KAPT** имеет некоторые ограничения по производительности и не поддерживает все возможности **Kotlin**.

### Настройка KAPT

Настройка **KAPT** требует добавления плагина `kapt` в проект. Плагин автоматически генерирует **Java stubs** и интегрируется с системой сборки для запуска процессоров аннотаций.

```kotlin
// build.gradle.kts
plugins {
    kotlin("jvm") version "1.8.0"
    kotlin("kapt") version "1.8.0"
}
```

После добавления плагина зависимости для процессоров аннотаций указываются с префиксом `kapt` вместо `implementation`. Это позволяет **KAPT** обработать эти зависимости и запустить процессоры аннотаций на сгенерированных **Java stubs**.

**dependencies** {
    **kapt("`com.google.dagger`:`dagger-compiler`:2.44")
    **implementation("`com.google.dagger`:dagger:2.44")
}
```text

### Пример: Dagger с KAPT

```
// Аннотации `Dagger`
import dagger.`Component`
import dagger.`Module`
import dagger.`Provides`

`@Module`
class `AppModule` {
    `@Provides`
    fun `provideDatabase()`: `Database` {
        return `Database()`
    }
}

``@Component`(modules = [`AppModule::class`])`
interface `AppComponent` {
    fun inject(app: `MyApplication`)
}

// `KAPT` сгенерирует реализации компонентов
```text

### Пример: Room с KAPT

```
import `androidx.room`.*

``@Entity`(`tableName` = "users")`
data class `User`(
    `@PrimaryKey` val id: Int,
    ``@ColumnInfo`(name = "name")` val name: `String`
)

`@Dao`
interface `UserDao` {
    ``@Query`("`SELECT` * `FROM` users")`
    fun `getAll()`: `List`<`User`>

    `@Insert`
    fun insert(user: `User`)
}

``@Database`(entities = [`User::class`], version = 1)`
abstract class `AppDatabase` : `RoomDatabase()` {
    abstract fun `userDao()`: `UserDao`
}

// `KAPT` сгенерирует реализации `DAO`
```text

### Ограничения KAPT

- Медленнее чем KSP (использует Java Annotation Processing)
- Не поддерживает некоторые Kotlin-специфичные типы
- Требует генерации Java stubs

## KSP (Kotlin Symbol Processing)

KSP (Kotlin Symbol Processing) - это современная альтернатива KAPT, разработанная специально для Kotlin командой JetBrains. KSP работает напрямую с Kotlin компилятором, без необходимости генерации Java stubs, что делает его значительно быстрее и более интегрированным с экосистемой Kotlin.

KSP был создан для решения проблем производительности и ограничений KAPT. Он предоставляет API для работы с Kotlin символами напрямую, что позволяет процессорам понимать Kotlin-специфичные конструкции, такие как data классы, sealed классы, и другие особенности языка.

### Преимущества KSP

- Быстрее чем KAPT (в 2x раз): KSP работает напрямую с AST (Abstract Syntax Tree) Kotlin компилятора, избегая необходимости генерации и компиляции Java stubs. Это значительно ускоряет процесс обработки аннотаций, особенно для больших проектов.

- Нативная поддержка Kotlin типов: KSP понимает Kotlin типы напрямую, включая nullable типы, type aliases, и другие Kotlin-специфичные конструкции. Это позволяет создавать более точные и эффективные процессоры аннотаций.

- Лучшая интеграция с Kotlin компилятором: KSP интегрирован в процесс компиляции Kotlin, что обеспечивает лучшую синхронизацию и позволяет использовать информацию, доступную только компилятору.

- Не требует генерации Java stubs: отсутствие необходимости в генерации промежуточных Java файлов не только ускоряет процесс, но и упрощает отладку и уменьшает размер промежуточных артефактов.

### Настройка KSP

```
// `build.gradle.kts`
plugins {
    kotlin("jvm") version "1.8.0"
    id("`com.`google.devtools`.ksp`") version "1.8.0-1.0.9"
}

dependencies {
    ksp("`com.`google.devtools`.ksp`:`symbol-processing-api`:1.8.0-1.0.9")
    implementation("`com.`google.devtools`.ksp`:`symbol-processing`:1.8.0-1.0.9")
}
```text

### Создание KSP Processor

```
// `Processor` для обработки аннотаций
import `com.`google.devtools.ksp`.processing`.*
import `com.`google.devtools.ksp`.symbol`.*
import `com.`google.devtools.ksp`.validate`

class `MySymbolProcessor`(
    private val `codeGenerator`: `CodeGenerator`,
    private val logger: KSPLogger
) : `SymbolProcessor` {

    override fun process(resolver: `Resolver`): `List`<KSAnnotated> {
        // Получение всех символов с аннотацией
        val symbols = resolver
            .`getSymbolsWithAnnotation`("`com.example`.`MyAnnotation`")
            .`filterIsInstance`<KSClassDeclaration>()

        symbols.`forEach` { symbol ->
            // Генерация кода
            `generateCode`(symbol)
        }

        return symbols.`filterNot` { `it.validate`() }.`toList()`
    }

    private fun `generateCode`(symbol: KSClassDeclaration) {
        val `packageName` = symbol.`packageName`.`asString()`
        val `className` = symbol.`simpleName`.`asString()`

        val file = `codeGenerator`.`createNewFile`(
            `Dependencies`(`false`),
            `packageName`,
            "${`className`}`Generated`"
        )

        file.`appendText`("""
            package $`packageName`

            class ${`className`}`Generated` {
                fun hello() {
                    println("`Generated for` $`className`")
                }
            }
        """.`trimIndent()`)

        `file.close`()
    }
}

// `Provider` для регистрации processor
class `MySymbolProcessorProvider` : `SymbolProcessorProvider` {
    override fun create(
        environment: `SymbolProcessorEnvironment`
    ): `SymbolProcessor` {
        return `MySymbolProcessor`(
            environment.`codeGenerator`,
            `environment.logger`
        )
    }
}
```text

### Регистрация Processor

```
// resources/META-INF/services/com.google.devtools.ksp.processing.SymbolProcessorProvider
`com.example`.`MySymbolProcessorProvider`
```text

### Пример: Генерация Builder

```
// Аннотация
``@Target`(`AnnotationTarget`.`CLASS`)`
``@Retention`(`AnnotationRetention`.`SOURCE`)`
annotation class `Builder`

// Использование
`@Builder`
data class `User`(val name: `String`, val age: Int)

// `KSP Processor` генерирует
class `UserBuilder` {
    private var name: `String`? = `null`
    private var age: Int? = `null`

    fun name(name: `String`) = apply { `this.name` = name }
    fun age(age: Int) = apply { `this.age` = age }

    fun build(): `User` {
        `requireNotNull`(name) { "name is required" }
        `requireNotNull`(age) { "age is required" }
        return `User`(name!!, age!!)
    }
}
```text

## Code Generation

### Генерация кода во время компиляции

```
// Использование `KSP` для генерации кода
class `CodeGeneratorProcessor`(
    private val `codeGenerator`: `CodeGenerator`
) : `SymbolProcessor` {

    override fun process(resolver: `Resolver`): `List`<KSAnnotated> {
        val symbols = resolver
            .`getSymbolsWithAnnotation`("`com.example`.`Generate`")
            .`filterIsInstance`<KSClassDeclaration>()

        symbols.`forEach` { symbol ->
            `generateClass`(symbol)
        }

        return `emptyList()`
    }

    private fun `generateClass`(symbol: KSClassDeclaration) {
        val `packageName` = symbol.`packageName`.`asString()`
        val `className` = symbol.`simpleName`.`asString()`

        val file = `codeGenerator`.`createNewFile`(
            `Dependencies`(`false`),
            `packageName`,
            "${`className`}`Factory`"
        )

        file.`appendText`(`buildString` {
            `appendLine`("package $`packageName`")
            `appendLine()`
            `appendLine`("object ${`className`}`Factory` {")
            `appendLine`("    fun create(): $`className` {")
            `appendLine`("        return $`className()`")
            `appendLine`("    }")
            `appendLine`("}")
        })

        `file.close`()
    }
}
```text

### Генерация через Template

```
// Шаблон для генерации
class `TemplateGenerator` {
    fun generate(
        `packageName`: `String`,
        `className`: `String`,
        properties: `List`<`Property`>
    ): `String` {
        return """
            package $`packageName`

            data class ${`className`}`Generated`(
                ${properties.`joinToString`(",\n    ") { "${`it.name`}: ${`it.type`}" }}
            ) {
                companion object {
                    fun `builder()` = ${`className`}`Builder()`
                }
            }

            class ${`className`}`Builder` {
                ${properties.`joinToString`("\n    ") {
                    "private var ${`it.name`}: ${`it.type`}? = `null`"
                }}

                ${properties.`joinToString`("\n    ") {
                    "fun ${`it.name`}(value: ${`it.type`}) = apply { this.${`it.name`} = value }"
                }}

                fun build(): ${`className`}`Generated` {
                    return ${`className`}`Generated`(
                        ${properties.`joinToString`(",\n                        ") {
                            "${`it.name`} = ${`it.name`} ?: throw `IllegalArgumentException`(\"${`it.name`} is required\")"
                        }}
                    )
                }
            }
        """.`trimIndent()`
    }
}

data class `Property`(val name: `String`, val type: `String`)
```text

## Reflection-based метапрограммирование

### Runtime Code Generation

```
import `kotlin.reflect.full`.*

// Генерация `proxy` через reflection
fun <T : Any> `createProxy`(clazz: `Class`<T>, handler: `InvocationHandler`): T {
    return `java.lang.reflect`.`Proxy`.`newProxyInstance`(
        clazz.`classLoader`,
        `arrayOf`(clazz),
        handler
    ) as T
}

// Использование
interface `Service` {
    fun `doWork()`: `String`
}

val `proxy` = `createProxy`(`Service::class.java`) { _, method, args ->
    println("`Calling` ${`method.name`}")
    "`Result`"
}

`proxy`.`doWork()`
```text

### Dynamic Method Invocation

```
// Вызов методов динамически
class `DynamicInvoker` {
    fun invoke(obj: Any, `methodName`: `String`, vararg args: Any?): Any? {
        val method = obj::`class.members.find` { `it.name` == `methodName` }
        return method?.call(obj, args)
    }
}

// Использование
class `Calculator` {
    fun add(a: Int, b: Int) = a + b
    fun multiply(a: Int, b: Int) = a * b
}

val calculator = `Calculator()`
val invoker = `DynamicInvoker()`

val result1 = `invoker.invoke`(calculator, "add", 5, 3)  // 8
val result2 = `invoker.invoke`(calculator, "multiply", 4, 2)  // 8
```text

### Property Delegation для метапрограммирования

```
// Делегат для автоматической валидации
class `ValidatedProperty`<T>(
    private val validator: (T) -> `Boolean`,
    private val `errorMessage`: `String`
) {
    private var value: T? = `null`

    operator fun `getValue`(`thisRef`: Any?, property: KProperty<*>): T {
        return value ?: throw `IllegalStateException`("${`property.name`} not initialized")
    }

    operator fun `setValue`(`thisRef`: Any?, property: KProperty<*>, value: T) {
        require(validator(value)) { `errorMessage` }
        `this.value` = value
    }
}

// Использование
class `User` {
    var age: `Int by ValidatedProperty`(
        validator = { it >= 0 },
        `errorMessage` = "`Age must be non`-negative"
    )

    var email: `String by ValidatedProperty`(
        validator = { `it.contains`("@") },
        `errorMessage` = "`Email must contain` @"
    )
}
```text

## Лучшие практики

### Используйте KSP вместо KAPT

```
// Плохо (`KAPT`)
plugins {
    kotlin("kapt")
}

// Хорошо (`KSP`)
plugins {
    id("`com.`google.devtools`.ksp`")
}
```text

### Кэшируйте результаты обработки

```
// Кэширование для производительности
class `CachedProcessor` : `SymbolProcessor` {
    private val `cache` = `mutableMapOf`<`String`, `String`>()

    override fun process(resolver: `Resolver`): `List`<KSAnnotated> {
        // Использование кэша
        // ...
    }
}
```text

### Валидация входных данных

```
// Валидация в processor
class `ValidatingProcessor` : `SymbolProcessor` {
    override fun process(resolver: `Resolver`): `List`<KSAnnotated> {
        val symbols = resolver.`getSymbolsWithAnnotation`("`com.example`.`Annotation`")

        symbols.`forEach` { symbol ->
            if (!`symbol.validate`()) {
                `logger.error`("`Invalid symbol`: ${symbol.`simpleName`}")
            }
        }

        return symbols.`filterNot` { `it.validate`() }.`toList()`
    }
}
```text

### Логирование и отладка

```
// Использование logger для отладки
class `LoggingProcessor`(
    private val logger: KSPLogger
) : `SymbolProcessor` {
    override fun process(resolver: `Resolver`): `List`<KSAnnotated> {
        `logger.info`("`Processing symbols`...")

        // Обработка

        `logger.info`("`Processing complete`")
        return `emptyList()`
    }
}
```text

### Генерация читаемого кода

```
// Генерация хорошо отформатированного кода
fun `generateCode`(`className`: `String`, properties: `List`<`Property`>): `String` {
    return `buildString` {
        `appendLine`("package `com.example`")
        `appendLine()`
        `appendLine`("class $`className`(")
        properties.`forEachIndexed` { index, prop ->
            val comma = if (index < `properties.size` - 1) "," else ""
            `appendLine`("    val ${`prop.name`}: ${`prop.type`}$comma")
        }
        `appendLine`(")")
    }
}
```text

Этот файл содержит руководство по метапрограммированию в Kotlin, включая KAPT, KSP и различные техники генерации кода.

## Продвинутые техники метапрограммирования

### Генерация кода на основе аннотаций

Создание процессоров, которые генерируют код на основе аннотаций:

```
// Аннотация
``@Target`(`AnnotationTarget`.`CLASS`)`
annotation class `GenerateBuilder`

// Процессор `KSP`
class `BuilderProcessor` : `SymbolProcessor` {
    override fun process(resolver: `Resolver`): `List`<KSAnnotated> {
        val symbols = resolver.`getSymbolsWithAnnotation`("`GenerateBuilder`")

        symbols.`forEach` { symbol ->
            if (symbol is KSClassDeclaration) {
                `generateBuilder`(symbol)
            }
        }

        return `emptyList()`
    }

    private fun `generateBuilder`(`classDeclaration`: KSClassDeclaration) {
        val `className` = `classDeclaration`.`simpleName`.`asString()`
        val `builderName` = "${`className`}`Builder`"

        // Генерация кода `builder`
        val code = `buildString` {
            `appendLine`("class $`builderName` {")
            // Генерация методов `builder`
            `appendLine`("}")
        }

        // Запись сгенерированного кода
        // ...
    }
}
```text

Генерация кода на основе аннотаций позволяет автоматизировать создание boilerplate кода и уменьшить ручную работу.

### Рефлексия во время выполнения

Использование рефлексии для динамического доступа к типам и свойствам:

```
import `kotlin.reflect.full`.*

// Получение свойств класса
data class `User`(val name: `String`, val age: Int)

val properties = User::class.memberProperties
properties.`forEach` { prop ->
    println("${`prop.name`}: ${prop.`returnType`}")
}

// Вызов методов через рефлексию
class `Calculator` {
    fun add(a: Int, b: Int) = a + b
}

val calculator = `Calculator()`
val method = Calculator::class.memberFunctions.find { it.name == "add" }
val result = method?.call(calculator, 2, 3)  // 5

// Создание экземпляров через рефлексию
val constructor = User::class.primaryConstructor
val user = constructor?.call("`Alice`", 25)
```text

Рефлексия позволяет создавать гибкие системы, которые могут работать с типами, известными только во время выполнения.

### Динамическая генерация классов

Создание классов во время выполнения (требует специальных библиотек):

```
// Использование библиотеки для динамической генерации
// Пример концептуального подхода
interface `DynamicClass` {
    fun `getProperty`(name: `String`): Any?
    fun `setProperty`(name: `String`, value: Any?)
    fun `invokeMethod`(name: `String`, args: `Array`<Any?>): Any?
}

// Создание динамического класса
fun `createDynamicClass`(
    `className`: `String`,
    properties: Map<`String`, `Class`<*>>
): `DynamicClass` {
    // Генерация класса через bytecode manipulation
    // ...
}
```text

Динамическая генерация классов позволяет создавать типы во время выполнения, что полезно для систем, требующих максимальной гибкости.

## Инструменты и библиотеки

### KotlinPoet для генерации кода

KotlinPoet - библиотека для генерации Kotlin кода:

```
import `com.squareup.kotlinpoet`.*

// Генерация класса
val file = `FileSpec`.`builder`("`com.example`", "`GeneratedClass`")
    .`addType`(
        `TypeSpec`.`classBuilder`("`GeneratedClass`")
            .`primaryConstructor`(
                `FunSpec`.`constructorBuilder()`
                    .`addParameter`("name", `String::class`)
                    .build()
            )
            .`addProperty`(
                `PropertySpec`.`builder`("name", `String::class`)
                    .initializer("name")
                    .build()
            )
            .`addFunction`(
                `FunSpec`.`builder`("greet")
                    .`addStatement`("return \"`Hello`, \$name\"")
                    .returns(`String::class`)
                    .build()
            )
            .build()
    )
    .build()

file.`writeTo`(`File`("src/main/kotlin"))
```text

KotlinPoet предоставляет типобезопасный API для генерации Kotlin кода, что делает процесс более надежным и читаемым.

### Bytecode Manipulation

Манипуляция байт-кодом для изменения поведения классов:

```
// Использование `ASM` или `Javassist` для манипуляции байт-кодом
// Пример концептуального подхода
class `BytecodeModifier` {
    fun `addMethod`(
        `className`: `String`,
        `methodName`: `String`,
        `methodBody`: `String`
    ) {
        // Загрузка класса
        // Модификация байт-кода
        // Добавление метода
    }
}
```text

Манипуляция байт-кодом позволяет изменять поведение классов на низком уровне, что полезно для инструментов и фреймворков.

## Оптимизация производительности

### Кэширование результатов рефлексии

Рефлексия может быть медленной, поэтому важно кэшировать результаты:

```
class `ReflectionCache` {
    private val `propertyCache` = `mutableMapOf`<`Class`<*>, `List`<`PropertyDescriptor`>>()
    private val `methodCache` = `mutableMapOf`<`Pair`<`Class`<*>, `String`>, `Method`>()

    fun `getProperties`(clazz: `Class`<*>): `List`<`PropertyDescriptor`> {
        return `propertyCache`.`getOrPut`(clazz) {
            clazz.`declaredFields`.map { field ->
                `PropertyDescriptor`(`field.name`, `field.type`)
            }
        }
    }

    fun `getMethod`(clazz: `Class`<*>, name: `String`): `Method`? {
        return `methodCache`.`getOrPut`(clazz to name) {
            clazz.`declaredMethods`.find { `it.name` == name }
        }
    }
}
```text

Кэширование результатов рефлексии значительно улучшает производительность при повторных обращениях.

### Ленивая инициализация процессоров

Оптимизация инициализации процессоров аннотаций:

```
class `OptimizedProcessor` : `SymbolProcessor` {
    private val `processedSymbols` = `mutableSetOf`<`String`>()

    override fun process(resolver: `Resolver`): `List`<KSAnnotated> {
        val symbols = resolver.`getSymbolsWithAnnotation`("`MyAnnotation`")

        symbols.`forEach` { symbol ->
            val `qualifiedName` = symbol.`qualifiedName`?.`asString()`
            if (`qualifiedName` != `null` && !`processedSymbols`.contains(`qualifiedName`)) {
                `processSymbol`(symbol)
                `processedSymbols`.add(`qualifiedName`)
            }
        }

        return `emptyList()`
    }

    private fun `processSymbol`(symbol: KSAnnotated) {
        // Обработка символа
    }
}
```text

Избежание повторной обработки символов улучшает производительность процессоров.

## Лучшие практики

### Обработка ошибок

Правильная обработка ошибок в процессорах:

```
class `RobustProcessor`(
    private val logger: KSPLogger
) : `SymbolProcessor` {
    override fun process(resolver: `Resolver`): `List`<KSAnnotated> {
        return try {
            val symbols = resolver.`getSymbolsWithAnnotation`("`MyAnnotation`")
            val errors = `mutableListOf`<KSAnnotated>()

            symbols.`forEach` { symbol ->
                try {
                    `processSymbol`(symbol)
                } catch (e: `Exception`) {
                    `logger.error`("`Error processing` ${symbol.`qualifiedName`}: ${`e.message`}", symbol)
                    `errors.add`(symbol)
                }
            }

            errors
        } catch (e: `Exception`) {
            `logger.error`("`Fatal error in processor`: ${`e.message`}")
            `emptyList()`
        }
    }
}
```text

Правильная обработка ошибок предотвращает сбои компиляции и помогает разработчикам понять проблемы.

### Документирование сгенерированного кода

Добавление документации к сгенерированному коду:

```
fun `generateDocumentedClass`(`className`: `String`): `String` {
    return `buildString` {
        `appendLine`("/")
        `appendLine`(" * `Auto-generated` class. `Do not edit manually`.")
        `appendLine`(" * `Generated at`: ${`System`.`currentTimeMillis()`}")
        `appendLine`(" */")
        `appendLine`("class $`className` {")
        appendLine("Generated code")")
        `appendLine`("}")
    }
}
```text

Документация помогает разработчикам понимать происхождение и назначение сгенерированного кода.

Этот файл содержит полное руководство по метапрограммированию в Kotlin, покрывающее все основные аспекты от базовых техник до продвинутых подходов, оптимизации производительности и лучших практик.

## Интеграция с системами сборки

### Gradle плагины для генерации кода

Создание Gradle плагинов для автоматической генерации кода:

```
// buildSrc/CodeGeneratorPlugin.kt
class `CodeGeneratorPlugin` : `Plugin`<`Project`> {
    override fun apply(project: `Project`) {
        `project.tasks.register`("`generateCode`") {
            `doLast` {
                // Генерация кода
                `generateCode()`
            }
        }

        // Интеграция с процессом компиляции
        `project.tasks.named`("`compileKotlin`") {
            `dependsOn`("`generateCode`")
        }
    }

    private fun `generateCode()` {
        // Логика генерации кода
    }
}

// Использование в `build.gradle.kts`
plugins {
    id("`com.example`.`code-generator`")
}
```text

Gradle плагины позволяют автоматизировать генерацию кода и интегрировать ее в процесс сборки.

### Maven плагины для генерации кода

Создание Maven плагинов для генерации кода:

```
// `Maven` плагин для генерации кода
``@Mojo`(name = "`generate-code`")`
class `CodeGeneratorMojo` : `AbstractMojo()` {
    ``@Parameter`(required = `true`)`
    lateinit var `sourceDirectory`: `File`

    ``@Parameter`(required = `true`)`
    lateinit var `targetDirectory`: `File`

    override fun execute() {
        // Генерация кода
        `generateCode`(`sourceDirectory`, `targetDirectory`)
    }
}
```text

Maven плагины позволяют интегрировать генерацию кода в процесс сборки Maven проектов.

## Генерация кода для различных сценариев

### Генерация DTO классов

Автоматическая генерация DTO классов из схем данных:

```
data class `Schema`(
    val `className`: `String`,
    val properties: `List`<`Property`>
)

data class `Property`(
    val name: `String`,
    val type: `String`,
    val nullable: `Boolean` = `false`
)

fun `generateDTO`(schema: `Schema`): `String` {
    return `buildString` {
        `appendLine`("package `com.example.dto`")
        `appendLine()`
        `appendLine`("data class ${schema.`className`}(")
        `schema.properties`.`forEachIndexed` { index, prop ->
            val nullable = if (`prop.nullable`) "?" else ""
            val comma = if (index < `schema.properties.size` - 1) "," else ""
            `appendLine`("    val ${`prop.name`}: ${`prop.type`}$nullable$comma")
        }
        `appendLine`(")")
    }
}

// Использование
val schema = `Schema`(
    `className` = "`UserDTO`",
    properties = `listOf`(
        `Property`("id", "`Long`"),
        `Property`("name", "`String`"),
        `Property`("email", "`String`", nullable = `true`)
    )
)

val `dtoCode` = `generateDTO`(schema)
```text

Генерация DTO классов автоматизирует создание boilerplate кода и уменьшает ручную работу.

### Генерация API клиентов

Автоматическая генерация API клиентов из OpenAPI спецификаций:

```
data class `ApiEndpoint`(
    val method: `String`,
    val path: `String`,
    val `returnType`: `String`,
    val parameters: `List`<`Parameter`>
)

data class `Parameter`(
    val name: `String`,
    val type: `String`,
    val location: `String`  // "path", "query", "body"
)

fun `generateApiClient`(endpoints: `List`<`ApiEndpoint`>): `String` {
    return `buildString` {
        `appendLine`("package `com.example.api`")
        `appendLine()`
        `appendLine`("interface `ApiClient` {")
        endpoints.`forEach` { `endpoint` ->
            val `paramList` = `endpoint.parameters`.`joinToString`(", ") { "${`it.name`}: ${`it.type`}" }
            `appendLine`("    suspend fun ${`endpoint.method.lowercase`()}${`endpoint.path.capitalize`()}($`paramList`): ${`endpoint`.`returnType`}")
        }
        `appendLine`("}")
    }
}
```text

Генерация API клиентов автоматизирует создание клиентского кода для REST API и уменьшает вероятность ошибок.

## Продвинутые техники метапрограммирования

### Генерация тестовых классов

Автоматическая генерация тестовых классов:

```
// Генерация тестов для классов
fun `generateTestClass`(`sourceClass`: KClass<*>): `String` {
    val `className` = `sourceClass`.`simpleName` ?: "`Unknown`"
    val `testClassName` = "${`className`}`Test`"
    val `packageName` = `sourceClass`.java.`package`.name

    return `buildString` {
        `appendLine`("package $`packageName`")
        `appendLine()`
        `appendLine`("import `org.`junit.jupiter`.api`.`Test`")
        `appendLine`("import `org.`junit.jupiter`.api`.`Assertions`.*")
        `appendLine()`
        `appendLine`("class $`testClassName` {")
        `appendLine()`
        `sourceClass`.`memberFunctions`.`forEach` { func ->
            if (`func.parameters.size` == 1) {  // Методы без параметров
                val `methodName` = `func.name`
                `appendLine`("    `@Test`")
                `appendLine`("    fun `test ${`methodName`}`() {")
                `appendLine`("        val instance = ${`className`}()")
                `appendLine`("        val result = instance.$`methodName()`")
                `appendLine`("        `assertNotNull`(result)")
                `appendLine`("    }")
                `appendLine()`
            }
        }
        `appendLine`("}")
    }
}

// Генерация тестов для конструкторов
fun `generateConstructorTests`(`sourceClass`: KClass<*>): `String` {
    val `className` = `sourceClass`.`simpleName` ?: "`Unknown`"
    val `testClassName` = "${`className`}`ConstructorTest`"

    return `buildString` {
        `appendLine`("class $`testClassName` {")
        `sourceClass`.constructors.`forEach` { constructor ->
            val `paramCount` = `constructor.parameters.size`
            `appendLine`("    `@Test`")
            `appendLine`("    fun `test constructor with $`paramCount` parameters`() {")
            `appendLine`("        val instance = ${`className`}(")
            `constructor.parameters`.`forEachIndexed` { index, param ->
                val comma = if (index < `constructor.parameters.size` - 1) "," else ""
                val `defaultValue` = when (`param.type.classifier`) {
                    `String::class` -> "\"test\""
                    Int::class -> "0"
                    `Boolean::class` -> "`false`"
                    else -> "`null`"
                }
                `appendLine`("            $`defaultValue`$comma")
            }
            `appendLine`("        )")
            `appendLine`("        `assertNotNull`(instance)")
            `appendLine`("    }")
            `appendLine()`
        }
        `appendLine`("}")
    }
}
```text

Генерация тестовых классов автоматизирует создание базовых тестов и улучшает покрытие тестами.

### Генерация документации

Автоматическая генерация документации из кода:

```
// Генерация документации для классов
fun `generateDocumentation`(`sourceClass`: KClass<*>): `String` {
    val `className` = `sourceClass`.`simpleName` ?: "`Unknown`"
    val `packageName` = `sourceClass`.java.`package`.name

    return `buildString` {
        `appendLine`("# $`className`")
        `appendLine()`
        `appendLine`("`Package`: `$packageName`")
        `appendLine()`
        `appendLine`("## `Properties`")
        `appendLine()`
        `sourceClass`.`memberProperties`.`forEach` { prop ->
            val `propName` = `prop.name`
            val `propType` = prop.`returnType`.`toString()`
            `appendLine`("- $`propName`: `$propType`")
        }
        `appendLine()`
        `appendLine`("## `Functions`")
        `appendLine()`
        `sourceClass`.`memberFunctions`.`forEach` { func ->
            val `funcName` = `func.name`
            val `paramList` = `func.parameters`.`joinToString`(", ") { param ->
                "${`param.name`}: ${`param.type`}"
            }
            val `returnType` = func.`returnType`.`toString()`
            `appendLine`("### `$`funcName`($`paramList`): $returnType`")
            `appendLine()`
        }
    }
}

// Генерация `API` документации
fun `generateApiDocumentation`(`apiClass`: KClass<*>): `String` {
    val `className` = `apiClass`.`simpleName` ?: "`Unknown`"

    return `buildString` {
        `appendLine`("# `API`: $`className`")
        `appendLine()`
        `appendLine`("## `Endpoints`")
        `appendLine()`
        `apiClass`.`memberFunctions`.`forEach` { func ->
            val `funcName` = `func.name`
            `appendLine`("### $`funcName`")
            `appendLine()`
            `appendLine`("`Method`: `POST`")
            `appendLine()`
            `appendLine`("`Parameters`:")
            `func.parameters`.`forEach` { param ->
                `appendLine`("- `${`param.name`}`: ${`param.type`}")
            }
            `appendLine()`
            `appendLine`("`Returns`: ${func.`returnType`}")
            `appendLine()`
        }
    }
}
```text

Генерация документации автоматизирует создание документации из кода, что упрощает поддержку проекта.

## Инструменты для метапрограммирования

### KotlinPoet для генерации кода

Использование KotlinPoet для создания кода:

```
import `com.squareup.kotlinpoet`.*

// Генерация класса с использованием `KotlinPoet`
fun `generateClass`(`className`: `String`, properties: `List`<`Property`>): `FileSpec` {
    val `classBuilder` = `TypeSpec`.`classBuilder`(`className`)
        .`addModifiers`(KModifier.`DATA`)

    properties.`forEach` { prop ->
        `classBuilder`.`addProperty`(
            `PropertySpec`.`builder`(`prop.name`, `prop.type`)
                .initializer(prop.`defaultValue`)
                .build()
        )
    }

    return `FileSpec`.`builder`("`com.example`", `className`)
        .`addType`(`classBuilder`.build())
        .build()
}

// Генерация функции
fun `generateFunction`(`functionName`: `String`, parameters: `List`<`Parameter`>, `returnType`: `TypeName`): `FunSpec` {
    val `functionBuilder` = `FunSpec`.`builder`(`functionName`)
        .returns(`returnType`)

    parameters.`forEach` { param ->
        `functionBuilder`.`addParameter`(`param.name`, `param.type`)
    }

    `functionBuilder`.`addStatement`("return %L", "result")

    return `functionBuilder`.build()
}

// Использование
val `fileSpec` = `generateClass`(
    "`User`",
    `listOf`(
        Property("name", String::class.asTypeName(), "\"\""),
        `Property`("age", Int::class.`asTypeName()`, "0")
    )
)

`fileSpec`.`writeTo`(`System`.out)
```text

KotlinPoet предоставляет типобезопасный API для генерации Kotlin кода, что делает процесс генерации более надежным.

## Дополнительные техники метапрограммирования

### Генерация кода для различных сценариев

Создание генераторов кода для различных задач:

```
// Генерация `builder` классов
fun `generateBuilder`(`sourceClass`: KClass<*>): `String` {
    val `className` = `sourceClass`.`simpleName` ?: "`Unknown`"
    val `builderName` = "${`className`}`Builder`"

    val properties = `sourceClass`.`memberProperties`

    return `buildString` {
        `appendLine`("class $`builderName` {")
        properties.`forEach` { prop ->
            val `propName` = `prop.name`
            val `propType` = prop.`returnType`.`toString()`
            `appendLine`("    var $`propName`: $`propType`? = `null`")
        }
        `appendLine()`
        `appendLine`("    fun build(): $`className` {")
        `appendLine`("        return $`className`(")
        properties.`forEachIndexed` { index, prop ->
            val `propName` = `prop.name`
            val comma = if (index < `properties.size` - 1) "," else ""
            `appendLine`("            $`propName` = $`propName`!!$comma")
        }
        `appendLine`("        )")
        `appendLine`("    }")
        `appendLine`("}")
    }
}

// Генерация equals и `hashCode`
fun `generateEqualsAndHashCode`(`sourceClass`: KClass<*>): `String` {
    val `className` = `sourceClass`.`simpleName` ?: "`Unknown`"
    val properties = `sourceClass`.`memberProperties`

    return `buildString` {
        `appendLine`("override fun equals(other: Any?): `Boolean` {")
        `appendLine`("    if (this === other) return `true`")
        `appendLine`("    if (other !is $`className`) return `false`")
        `appendLine()`
        properties.`forEach` { prop ->
            val `propName` = `prop.name`
            `appendLine`("    if ($`propName` != other.$`propName`) return `false`")
        }
        `appendLine()`
        `appendLine`("    return `true`")
        `appendLine`("}")
        `appendLine()`
        `appendLine`("override fun `hashCode()`: Int {")
        `appendLine`("    var result = 0")
        properties.`forEach` { prop ->
            val `propName` = `prop.name`
            `appendLine`("    result = 31 * result + ($`propName`?.`hashCode()` ?: 0)")
        }
        `appendLine`("    return result")
        `appendLine`("}")
    }
}
```text

Генерация кода для различных сценариев автоматизирует создание boilerplate кода и улучшает продуктивность разработки.

Этот файл содержит полное руководство по метапрограммированию в Kotlin, покрывающее все основные аспекты от базовых техник до продвинутых подходов, оптимизации производительности, лучших практик, интеграции с системами сборки, генерации кода для различных сценариев, работы с KotlinPoet и создания генераторов кода.

## Дополнительные техники метапрограммирования

### Генерация кода для различных паттернов

Создание генераторов для распространенных паттернов:

```
// Генерация `Builder` для классов
fun `generateBuilder`(`sourceClass`: KClass<*>): `String` {
    val `className` = `sourceClass`.`simpleName` ?: "`Unknown`"
    val `builderName` = "${`className`}`Builder`"
    val properties = `sourceClass`.`memberProperties`

    return `buildString` {
        `appendLine`("class $`builderName` {")
        properties.`forEach` { prop ->
            val `propName` = `prop.name`
            val `propType` = prop.`returnType`.`toString()`
            `appendLine`("    private var $`propName`: $`propType`? = `null`")
        }
        `appendLine()`
        properties.`forEach` { prop ->
            val `propName` = `prop.name`
            `appendLine`("    fun $`propName`(value: ${prop.`returnType`}): $`builderName` {")
            `appendLine`("        this.$`propName` = value")
            `appendLine`("        return this")
            `appendLine`("    }")
        }
        `appendLine()`
        `appendLine`("    fun build(): $`className` {")
        `appendLine`("        return $`className`(")
        properties.`forEachIndexed` { index, prop ->
            val `propName` = `prop.name`
            val comma = if (index < `properties.size` - 1) "," else ""
            `appendLine`("            $`propName` = $`propName`!!$comma")
        }
        `appendLine`("        )")
        `appendLine`("    }")
        `appendLine`("}")
    }
}

// Генерация `toString`, equals, `hashCode`
fun `generateDataClassMethods`(`sourceClass`: KClass<*>): `String` {
    val `className` = `sourceClass`.`simpleName` ?: "`Unknown`"
    val properties = `sourceClass`.`memberProperties`

    return `buildString` {
        `appendLine`("override fun `toString()`: `String` {")
        `appendLine`("    return \"$`className`(\" +")
        properties.`forEachIndexed` { index, prop ->
            val `propName` = `prop.name`
            val comma = if (index < `properties.size` - 1) " + \", \" +" else ""
            `appendLine`("        \"$`propName`=\" + $`propName`$comma")
        }
        `appendLine`("    + \")\"")
        `appendLine`("}")
    }
}
```text

Генерация кода для паттернов автоматизирует создание boilerplate кода и улучшает продуктивность.

Этот файл содержит полное руководство по метапрограммированию в Kotlin, покрывающее все основные аспекты от базовых техник до продвинутых подходов, оптимизации производительности, лучших практик, интеграции с системами сборки, генерации кода для различных сценариев, работы с KotlinPoet, создания генераторов кода и генерации кода для различных паттернов.


## Решение проблем

Типичные проблемы и решения см. в официальной документации (блок «Полезные ссылки» в начале документа).

## Частые вопросы

Ответы на частые вопросы по теме см. в разделах «Введение» и «Лучшие практики» в документе.

## Заключение

Метапрограммирование в Kotlin позволяет автоматизировать создание кода и улучшать продуктивность разработки. Понимание техник метапрограммирования, включая рефлексию, аннотации, KAPT, KSP, KotlinPoet и генерацию кода, позволяет создавать мощные инструменты и библиотеки. Правильное использование метапрограммирования помогает уменьшить количество boilerplate кода, улучшить типобезопасность и упростить поддержку приложений.

Этот файл содержит полное руководство по метапрограммированию в Kotlin, покрывающее все основные аспекты от базовых техник до продвинутых подходов, оптимизации производительности, лучших практик, интеграции с системами сборки, генерации кода для различных сценариев, работы с KotlinPoet, создания генераторов кода, генерации кода для различных паттернов и заключение.

## Дополнительные ресурсы

Для дальнейшего изучения метапрограммирования в Kotlin рекомендуется:

- Kotlin Reflection: https://kotlinlang.org/docs/reflection.html
- KotlinPoet: https://github.com/square/kotlinpoet
- KAPT and KSP: https://kotlinlang.org/docs/ksp-overview.html

Этот файл содержит полное руководство по метапрограммированию в Kotlin, покрывающее все основные аспекты от базовых техник до продвинутых подходов, оптимизации производительности, лучших практик, интеграции с системами сборки, генерации кода для различных сценариев, работы с KotlinPoet, создания генераторов кода, генерации кода для различных паттернов, заключение и дополнительные ресурсы.

## Итоговые рекомендации

При работе с метапрограммированием рекомендуется:

1. Использовать KSP вместо KAPT для лучшей производительности
2. Применять KotlinPoet для типобезопасной генерации кода
3. Документировать генерируемый код для облегчения поддержки
4. Тестировать генераторы кода для проверки корректности
5. Оптимизировать производительность генерации кода

Этот файл содержит полное руководство по метапрограммированию в Kotlin, покрывающее все основные аспекты от базовых техник до продвинутых подходов, оптимизации производительности, лучших практик, интеграции с системами сборки, генерации кода для различных сценариев, работы с KotlinPoet, создания генераторов кода, генерации кода для различных паттернов, заключение, дополнительные ресурсы и итоговые рекомендации.

## Практические примеры использования

### Генерация DTO классов

Пример генерации DTO классов из domain моделей:

```
``@Target`(`AnnotationTarget`.`CLASS`)`
annotation class `GenerateDTO`

`@GenerateDTO`
data class `User`(
    val id: `Long`,
    val name: `String`,
    val email: `String`
)

// Генератор создаст:
// data class `UserDTO`(
//     val id: `Long`,
//     val name: `String`,
//     val email: `String`
// )
//
// fun `User`.`toDTO()`: `UserDTO` = `UserDTO`(id, name, email)
```text

Генерация DTO классов автоматизирует создание слоя передачи данных.

### Генерация валидаторов

Пример генерации валидаторов на основе аннотаций:

```
``@Target`(`AnnotationTarget`.`PROPERTY`)`
annotation class `NotEmpty`(val message: `String` = "`Field cannot be empty`")

``@Target`(`AnnotationTarget`.`PROPERTY`)`
annotation class Min(val value: Int, val message: `String` = "`Value too small`")

data class `RegistrationForm`(
    ``@NotEmpty`("`Username is required`")`
    val username: `String`,

    ``@NotEmpty`("`Email is required`")`
    val email: `String`,

    `@Min(8, "`Password must be at least 8` characters")`
    val password: `String`
)

// Генератор создаст валидатор:
// fun `RegistrationForm`.validate(): `ValidationResult` {
//     val errors = `mutableListOf`<`String`>()
//     if (username.`isEmpty()`) `errors.add`("`Username is required`")
//     if (email.`isEmpty()`) `errors.add`("`Email is required`")
//     if (`password.length` < 8) `errors.add`("`Password must be at least 8` characters")
//     return if (errors.`isEmpty()`) `ValidationResult`.`Success else ValidationResult`.`Failure`(errors)
// }
```text

Генерация валидаторов упрощает проверку данных и уменьшает boilerplate код.

### Генерация Builder классов

Пример генерации Builder классов:

```
``@Target`(`AnnotationTarget`.`CLASS`)`
annotation class `GenerateBuilder`

`@GenerateBuilder`
data class `User`(
    val id: `Long`,
    val name: `String`,
    val email: `String`,
    val age: Int
)

// Генератор создаст:
// class `UserBuilder` {
//     private var id: `Long`? = `null`
//     private var name: `String`? = `null`
//     private var email: `String`? = `null`
//     private var age: Int? = `null`
//
//     fun id(id: `Long`) = apply { `this.id` = id }
//     fun name(name: `String`) = apply { `this.name` = name }
//     fun email(email: `String`) = apply { `this.email` = email }
//     fun age(age: Int) = apply { `this.age` = age }
//
//     fun build(): `User` {
//         `requireNotNull`(id) { "id is required" }
//         `requireNotNull`(name) { "name is required" }
//         `requireNotNull`(email) { "email is required" }
//         `requireNotNull`(age) { "age is required" }
//         return `User`(id!!, name!!, email!!, age!!)
//     }
// }
```text

Генерация Builder классов упрощает создание сложных объектов.

### Генерация Mapper классов

Пример генерации mapper классов для преобразования между DTO и domain моделями:

```
``@Target`(`AnnotationTarget`.`CLASS`)`
annotation class `GenerateMapper`

`@GenerateMapper`
data class `UserDTO`(
    val id: `Long`,
    val name: `String`,
    val email: `String`
)

data class `User`(
    val id: `Long`,
    val name: `String`,
    val email: `String`
)

// Генератор создаст:
// object `UserMapper` {
//     fun `toDomain`(dto: `UserDTO`): `User` {
//         return `User`(
//             id = `dto.id`,
//             name = `dto.name`,
//             email = `dto.email`
//         )
//     }
//
//     fun `toDTO`(domain: `User`): `UserDTO` {
//         return `UserDTO`(
//             id = `domain.id`,
//             name = `domain.name`,
//             email = `domain.email`
//         )
//     }
// }
```text

Генерация mapper классов автоматизирует преобразование между слоями приложения.

### Практические примеры: Генерация DTO классов

```
// Аннотация для генерации `DTO`
``@Target`(`AnnotationTarget`.`CLASS`)`
annotation class `GenerateDTO`

`@GenerateDTO`
data class `User`(
    val id: `Long`,
    val name: `String`,
    val email: `String`
)

// Генератор создаст:
// data class `UserDTO`(
//     val id: `Long`,
//     val name: `String`,
//     val email: `String`
// ) {
//     companion object {
//         fun `fromDomain`(user: `User`): `UserDTO` {
//             return `UserDTO`(
//                 id = `user.id`,
//                 name = `user.name`,
//                 email = `user.email`
//             )
//         }
//
//         fun `toDomain`(dto: `UserDTO`): `User` {
//             return `User`(
//                 id = `dto.id`,
//                 name = `dto.name`,
//                 email = `dto.email`
//             )
//         }
//     }
// }
```text

### Практические примеры: Генерация Repository классов

```
``@Target`(`AnnotationTarget`.`CLASS`)`
annotation class `GenerateRepository`

`@GenerateRepository`
``@Table`(name = "users")`
data class `User`(
    `@Id` val id: `Long`,
    val name: `String`,
    val email: `String`
)

// Генератор создаст интерфейс репозитория с базовыми методами:
// interface `UserRepository` {
//     fun `findById`(id: `Long`): `User`?
//     fun `findAll()`: `List`<`User`>
//     fun save(user: `User`): `User`
//     fun delete(id: `Long`)
//     fun `existsById`(id: `Long`): `Boolean`
// }
```text

### Практические примеры: Генерация тестов

```
``@Target`(`AnnotationTarget`.`CLASS`)`
annotation class `GenerateTests`

`@GenerateTests`
class `UserService`(
    private val repository: `UserRepository`
) {
    fun `createUser`(name: `String`, email: `String`): `User` { /... / }
    fun `getUser`(id: `Long`): `User`? { /... / }
    fun `updateUser`(id: `Long`, name: `String`): `User` { /... / }
    fun `deleteUser`(id: `Long`) { /... / }
}

// Генератор создаст базовые тесты для всех методов
```text

### Практические примеры: Генерация API документации

```
``@Target`(`AnnotationTarget`.`CLASS`, `AnnotationTarget`.`FUNCTION`)`
annotation class `ApiDocumentation`(val description: `String`)

``@ApiDocumentation`("`Service for user management`")`
class `UserService` {
    ``@ApiDocumentation`("`Creates a new user`")`
    fun `createUser`(name: `String`, email: `String`): `User` { /... / }
}

// Генератор создаст OpenAPI/Swagger документацию
```text

### Практические примеры: Генерация валидаторов

```
``@Target`(`AnnotationTarget`.`PROPERTY`)`
annotation class `Validate`(
    val min: Int = Int.MIN_VALUE,
    val max: Int = Int.MAX_VALUE,
    val pattern: `String` = "",
    val required: `Boolean` = `true`
)

data class `User`(
    ``@Validate`(min = 1)`
    val id: `Long`,

    ``@Validate`(min = 1, max = `100`, required = `true`)`
    val name: `String`,

    ``@Validate`(pattern = "^[`A-`Za-z0`-9`+_.-]+@(.+)`$", required = `true`)
    val email: `String`
)

// Генератор создаст валидатор:
// object `UserValidator` {
//     fun validate(user: `User`): `ValidationResult` {
//         val errors = `mutableListOf`<`ValidationError`>()
//
//         if (`user.id` < 1) {
//             `errors.add`(`ValidationError`("id", "must be >= 1"))
//         }
//
//         if (`user.name`.`isEmpty()` || `user.name.length` > `100`) {
//             `errors.add`(`ValidationError`("name", "length must `be 1`-100"))
//         }
//
//         // ... больше проверок
//
//         return if (errors.`isEmpty()`) {
//             `ValidationResult`.`Success`
//         } else {
//             `ValidationResult`.`Failure`(errors)
//         }
//     }
// }
```text

Этот файл содержит полное руководство по метапрограммированию в Kotlin, покрывающее все основные аспекты от базовых техник до продвинутых подходов, оптимизации производительности, лучших практик, интеграции с системами сборки, генерации кода для различных сценариев, работы с KotlinPoet, создания генераторов кода, генерации кода для различных паттернов, генерации DTO, Repository, тестов, API документации, валидаторов, практические примеры использования, включая генерацию Builder и Mapper классов, заключение, дополнительные ресурсы и итоговые рекомендации.

```