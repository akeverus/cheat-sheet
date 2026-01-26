# Decorator (Декоратор) - Kotlin

Шаблон декоратора - это шаблон проектирования, который позволяет добавлять новые функции к существующему объекту, не изменяя его структуру и не влияя на поведение других объектов того же класса.

**Last Updated:** 2025-01-15

## Описание

Шаблон декоратора - это шаблон проектирования, который позволяет добавлять новые функции к существующему объекту, не изменяя его структуру и не влияя на поведение других объектов того же класса. В этом руководстве мы рассмотрим некоторые эффективные подходы к реализации этого шаблона в Kotlin.

Паттерн Decorator позволяет добавлять поведение статически или динамически, предоставляя расширенный интерфейс исходному объекту. Статического подхода можно достичь с помощью наследования, переопределяя все методы основного класса и добавляя дополнительные функции, которые нам нужны.

В качестве альтернативы наследованию и для уменьшения накладных расходов на создание подклассов мы можем использовать композицию и делегирование для динамического добавления дополнительного поведения. В этой статье мы будем следовать этим методам реализации этого шаблона.

## Useful Links

### Official Documentation
- [Design Patterns: Elements of Reusable Object-Oriented Software (GoF)](https://en.wikipedia.org/wiki/Design_Patterns)

### Baeldung
- [Decorator Pattern in Kotlin](https://www.baeldung.com/kotlin-decorator-pattern)

### See Also
- [Decorator Pattern (Java)](../structural/decorator.md)
- [Proxy Pattern](../structural/proxy.md)

## Table of Contents

- [Composition Approach](#composition-approach)
- [Delegation Approach](#delegation-approach)

## Composition Approach

Давайте рассмотрим следующий пример объекта «Рождественская елка», который мы хотим украсить. Украшение не меняет самого объекта; просто в дополнение к рождественской елке мы добавляем некоторые элементы декора, такие как гирлянды, пузыри или любой другой тип:

Во-первых, нам нужно создать общий интерфейс `ChristmasTree`:

```kotlin
interface ChristmasTree {
    fun decorate(): String
}
```

Теперь давайте определим реализацию для этого интерфейса:

```kotlin
class PineChristmasTree : ChristmasTree {
    override fun decorate() = "Christmas tree"
}
```

Далее мы рассмотрим две стратегии украшения объектов `ChristmasTree`.

При использовании композиции для реализации шаблона Decorator нам понадобится абстрактный класс, который будет действовать как композитор или декоратор для нашего целевого объекта:

```kotlin
abstract class TreeDecorator(private val tree: ChristmasTree) : ChristmasTree {
    override fun decorate(): String {
        return tree.decorate()
    }
}
```

Теперь мы создадим декоративный элемент. Этот декоратор расширит наш абстрактный класс `TreeDecorator` и изменит его метод `decorate()` в соответствии с нашими требованиями:

```kotlin
class BubbleLights(tree: ChristmasTree) : TreeDecorator(tree) {
    override fun decorate(): String {
        return super.decorate() + decorateWithBubbleLights()
    }

    private fun decorateWithBubbleLights(): String {
        return " with Bubble Lights"
    }
}
```

Теперь мы можем создать наш украшенный объект `ChristmasTree`:

```kotlin
fun christmasTreeWithBubbleLights() {
    val christmasTree = BubbleLights(PineChristmasTree())
    val decoratedChristmasTree = christmasTree.decorate()
    println(decoratedChristmasTree)
}
```

## Delegation Approach

Шаблон делегирования оказался хорошей альтернативой наследованию реализации, и Kotlin изначально поддерживает его, не требуя шаблонного кода. Эта функция позволяет легко создавать декораторы, используя делегирование класса с использованием ключевого слова `by`.

Теперь мы определим класс, который может реализовать интерфейс `ChristmasTree`, делегировав метод `decorate()` указанному объекту:

```kotlin
class Garlands(private val tree: ChristmasTree) : ChristmasTree by tree {
    override fun decorate(): String {
        return tree.decorate() + decorateWithGarlands()
    }

    private fun decorateWithGarlands(): String {
        return " with Garlands"
    }
}
```

И теперь мы можем создать наш украшенный объект `ChristmasTree`:

```kotlin
fun christmasTreeWithGarlands() {
    val christmasTree = Garlands(PineChristmasTree())
    val decoratedChristmasTree = christmasTree.decorate()
    println(decoratedChristmasTree)
}
```

