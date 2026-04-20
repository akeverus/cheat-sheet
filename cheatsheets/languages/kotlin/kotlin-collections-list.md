---
title: "Kotlin Collections: List"
description: "Кратко: руководство по работе со списками в Kotlin: List, MutableList, ArrayList, LinkedList, операции и лучшие практики."
tags:
  - languages
  - kotlin
  - kotlin-collections-list
difficulty: "intermediate"
prerequisites: []
next: []
updated: "2026-02-11"
---
# Kotlin Collections: List

Кратко: руководство по работе со списками в Kotlin: List, MutableList, ArrayList, LinkedList, операции и лучшие практики.

## Полезные ссылки

### Официальная документация
- [Kotlin Collections Overview](https://kotlinlang.org/docs/collections-overview.html)
- [Kotlin List API](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.collections/-list/index.html)

### Обучающие материалы
- [Kotlin Collections Guide](https://www.baeldung.com/kotlin/collections-api)

### См. также
- [[kotlin-basics|Основы Kotlin]]
- [[kotlin-collections-set|Множества (Set)]]
- [[kotlin-collections-map|Словари (Map)]]
- [[kotlin-collections-operations|Операции над коллекциями]]
- [[java-collections-list|Java Collections List]]

## Содержание

- [Введение в List](#введение-в-list)
  - [Основные характеристики](#основные-характеристики)
  - [Интерфейсы](#интерфейсы)
- [Создание списков](#создание-списков)
  - [Пустые списки](#пустые-списки)
  - [Создание с элементами](#создание-с-элементами)
  - [Создание из других коллекций](#создание-из-других-коллекций)
  - [Создание с помощью builder](#создание-с-помощью-builder)
  - [Генерация списков](#генерация-списков)
- [Неизменяемые и изменяемые списки](#неизменяемые-и-изменяемые-списки)
  - [Неизменяемые списки (List)](#неизменяемые-списки-list)
  - [Изменяемые списки (MutableList)](#изменяемые-списки-mutablelist)
  - [Преобразование между типами](#преобразование-между-типами)
- [ArrayList](#arraylist)
  - [Характеристики](#характеристики)
  - [Создание ArrayList](#создание-arraylist)
  - [Операции с ArrayList](#операции-с-arraylist)
  - [Оптимизация емкости](#оптимизация-емкости)
- [LinkedList](#linkedlist)
  - [Когда использовать LinkedList](#когда-использовать-linkedlist)
- [Основные операции](#основные-операции)
  - [Доступ к элементам](#доступ-к-элементам)
  - [Проверка содержимого](#проверка-содержимого)
  - [Итерация](#итерация)
- [Поиск и фильтрация](#поиск-и-фильтрация)
  - [Поиск элементов](#поиск-элементов)
  - [Фильтрация](#фильтрация)
- [Преобразование списков](#преобразование-списков)
  - [Map (трансформация)](#map-трансформация)
  - [Zip (объединение)](#zip-объединение)
  - [Windowed (скользящее окно)](#windowed-скользящее-окно)
- [Сортировка](#сортировка)
- [Группировка и агрегация](#группировка-и-агрегация)
  - [Группировка](#группировка)
  - [Агрегация](#агрегация)
  - [Подсчет](#подсчет)
- [Работа с индексами](#работа-с-индексами)
- [Многомерные списки](#многомерные-списки)
- [Сравнение списков](#сравнение-списков)
- [Производительность](#производительность)
  - [Сложность операций](#сложность-операций)
  - [Оптимизация](#оптимизация)
- [Лучшие практики](#лучшие-практики)
  - [Выбор типа списка](#выбор-типа-списка)
  - [Null safety](#null-safety)
  - [Функциональный стиль](#функциональный-стиль)
  - [Идиоматичный Kotlin](#идиоматичный-kotlin)
- [Продвинутые техники работы со списками](#продвинутые-техники-работы-со-списками)
  - [Работа с подсписками](#работа-с-подсписками)
  - [Операции с индексами](#операции-с-индексами)
  - [Модификация списков](#модификация-списков)
- [Производительность List](#производительность-list)
  - [Выбор правильной реализации](#выбор-правильной-реализации)
  - [Оптимизация операций List](#оптимизация-операций-list)
- [Реальные примеры использования List](#реальные-примеры-использования-list)
  - [List для обработки данных](#list-для-обработки-данных)
  - [List для реализации стека и очереди](#list-для-реализации-стека-и-очереди)
- [Продвинутые операции List](#продвинутые-операции-list)
  - [Windowed операции](#windowed-операции)
  - [Batch обработка](#batch-обработка)
  - [Работа с индексами и срезами](#работа-с-индексами-и-срезами)
  - [Операции с несколькими списками](#операции-с-несколькими-списками)
- [Дополнительные техники работы со списками](#дополнительные-техники-работы-со-списками)
  - [Работа с перестановками](#работа-с-перестановками)
- [Дополнительные техники](#дополнительные-техники)
  - [Работа с циклическими списками](#работа-с-циклическими-списками)
- [Troubleshooting](#troubleshooting)
- [FAQ](#faq)
- [Заключение](#заключение)
- [Дополнительные ресурсы](#дополнительные-ресурсы)
- [Практические примеры использования](#практические-примеры-использования)
  - [Реализация стека и очереди](#реализация-стека-и-очереди)
  - [Обработка больших списков](#обработка-больших-списков)
  - [Работа с индексами и элементами](#работа-с-индексами-и-элементами)

## Введение в List

**List** в **Kotlin** — это упорядоченная коллекция элементов с доступом по индексу. В отличие от **Java**, **Kotlin** различает изменяемые (mutable) и неизменяемые (immutable) коллекции на уровне системы типов, что обеспечивает дополнительную безопасность и ясность кода.

### Основные характеристики

- **Упорядоченность**: элементы сохраняют порядок вставки. Это означает, что при итерации элементы будут появляться в том же порядке, в котором они были добавлены.

- **Индексированный доступ**: доступ к элементам по индексу. Время доступа зависит от реализации: `O(1)` для **ArrayList** (произвольный доступ), `O(n)` для **LinkedList** (требуется проход от начала или конца до нужного элемента).

- **Дубликаты**: разрешены повторяющиеся элементы. В отличие от **Set**, **List** может содержать несколько экземпляров одного и того же значения.

- **Null safety**: может содержать **null** элементы, если тип объявлен как **nullable** (например, `List<`String`?>`). Это позволяет явно контролировать возможность наличия **null** значений в коллекции.

### Интерфейсы

```kotlin
// Неизменяемый список
interface List<out E> : Collection<E>

// Изменяемый список
interface MutableList<E> : List<E>, MutableCollection<E>
```

## Создание списков

### Пустые списки

```kotlin
// Неизменяемый пустой список
val emptyList: List<String> = emptyList()
val emptyList2 = listOf<String>()

// Изменяемый пустой список
val mutableList = mutableListOf<String>()
val arrayList = ArrayList<String>()
```

### Создание с элементами

```kotlin
// Неизменяемый список (listOf)
val numbers = listOf(1, 2, 3, 4, 5)
val names = listOf("Alice", "Bob", "Charlie")

// Изменяемый список (mutableListOf)
val mutableNumbers = mutableListOf(1, 2, 3)
mutableNumbers.add(4)

// ArrayList напрямую
val arrayList = ArrayList<Int>()
arrayList.add(1)
arrayList.add(2)

// Инициализация с начальной емкостью
val listWithCapacity = ArrayList<String>(20)
```

### Создание из других коллекций

```kotlin
// Преобразование Set в List (порядок элементов не гарантирован)
val set = setOf(1, 2, 3)
val listFromSet = set.toList()

// Массив в список
val array = arrayOf(1, 2, 3)
val listFromArray = array.toList()

// Ключи и значения Map в отдельные списки
val map = mapOf("a" to 1, "b" to 2)
val keysList = map.keys.toList()
val valuesList = map.values.toList()
```

### Создание с помощью builder

```kotlin
// buildList — создание неизменяемого списка через builder
val list = buildList {
    add(1)
    add(2)
    add(3)
}
```

### Генерация списков

```kotlin
// С помощью range
val rangeList = (1..10).toList()

// С помощью generateSequence
val generatedList = generateSequence(1) { it + 1 }
    .take(10)
    .toList()

// Повторение элементов
val repeated = List(5) { "Hello" } // ["Hello", "Hello", "Hello", "Hello", "Hello"]
```

## Неизменяемые и изменяемые списки

### Неизменяемые списки (List)

```kotlin
val immutableList = listOf(1, 2, 3)

// Эти операции НЕ изменяют исходный список, а возвращают новый
val newList = immutableList + 4
val anotherList = immutableList - 2

// immutableList остается [1, 2, 3]
```

### Изменяемые списки (MutableList)

```kotlin
val mutableList = mutableListOf(1, 2, 3)

// Эти операции изменяют исходный список
mutableList.add(4)
mutableList.remove(2)
mutableList[0] = 10

// mutableList теперь [10, 3, 4]
```

### Преобразование между типами

```kotlin
val mutable = mutableListOf(1, 2, 3)
val immutable: List<Int> = mutable.toList()

// Создание копии для безопасного изменения
val copy = mutable.toMutableList()
copy.add(4) // не влияет на mutable
```

## ArrayList

**ArrayList** — это реализация **MutableList** на основе массива. Это наиболее распространенная реализация списка в **Kotlin**, так как она обеспечивает оптимальный баланс между производительностью и простотой использования для большинства сценариев.

### Характеристики

- **Произвольный доступ**: `O(1)` - доступ к элементу по индексу выполняется за постоянное время, так как используется прямое обращение к элементу массива по индексу.

- **Добавление в конец**: `O(1)` амортизированное — в большинстве случаев добавление элемента в конец списка выполняется за постоянное время. При заполнении внутреннего массива происходит его расширение (обычно в 1.5-2 раза), что требует `O(n)` времени, но это происходит редко, поэтому средняя сложность остается `O(1)`.

- **Вставка/удаление**: `O(n)` - при вставке или удалении элемента в середине списка необходимо сдвинуть все последующие элементы, что требует линейного времени.

- **Поиск**: `O(n)` - для поиска элемента по значению необходимо проверить все элементы списка в худшем случае.

### Создание ArrayList

```kotlin
// Пустой ArrayList
val list1 = ArrayList<Int>()

// С начальной емкостью
val list2 = ArrayList<String>(20)

// Из другой коллекции
val list3 = ArrayList(listOf(1, 2, 3))

// С помощью arrayListOf
val list4 = arrayListOf(1, 2, 3, 4, 5)
```

### Операции с ArrayList

```kotlin
val list = arrayListOf("a", "b", "c")

// Добавление элементов
list.add("d")                    // в конец
list.add(0, "start")            // по индексу
list.addAll(listOf("e", "f"))   // несколько элементов

// Удаление элементов
list.remove("b")                 // по значению
list.removeAt(0)                 // по индексу
list.removeAll(listOf("c", "d")) // несколько элементов
list.clear()                     // все элементы

// Изменение элементов
list[0] = "new value"            // по индексу
list.set(1, "another")          // через метод set
```

### Оптимизация емкости

```kotlin
// Установка минимальной емкости
val list = ArrayList<Int>(100)
list.ensureCapacity(200)

// Уменьшение емкости до размера
list.trimToSize()
```

## LinkedList

В **Kotlin** нет встроенного **LinkedList** в стандартной библиотеке, но можно использовать **Java LinkedList** через **interop**. **LinkedList** реализует двусвязный список, где каждый элемент содержит ссылки на предыдущий и следующий элементы.

### Когда использовать LinkedList

**LinkedList** полезен в следующих случаях:**
- Частые вставки и удаления в середине списка (**O(1) после нахождения позиции)
- Необходимость использования операций из интерфейса **Deque** (добавление/удаление с обоих концов)
- Когда порядок элементов важен, но произвольный доступ по индексу не требуется

**Однако в большинстве случаев **ArrayList** предпочтительнее, так как:**
- Произвольный доступ по индексу быстрее (**O(1) vs `O(n)`)
- Меньше потребление памяти (нет дополнительных ссылок на узлы)
- Лучшая локальность данных в памяти (массив хранится непрерывно)

```kotlin
// LinkedList из Java — вставка/удаление в начале/конце O(1)
import java.util.LinkedList

val linkedList = LinkedList<Int>()
linkedList.add(1)
linkedList.add(2)
linkedList.addFirst(0)  // Добавление в начало - O(1)
linkedList.addLast(3)  // Добавление в конец - O(1)

// Доступ по индексу - O(n), так как требуется проход от начала или конца
val element = linkedList[1]  // Медленнее, чем в ArrayList
```

## Основные операции

Операции со списками в **Kotlin** предоставляют множество способов работы с элементами. Понимание различных методов доступа и модификации элементов критично для эффективной работы с коллекциями.

### Доступ к элементам

Доступ к элементам списка может осуществляться различными способами. **Kotlin** предоставляет как безопасные, так и небезопасные методы доступа, каждый из которых подходит для разных сценариев.

```kotlin
val list = listOf("a", "b", "c", "d", "e")

// По индексу
val first = list[0]              // "a"
val second = list.get(1)         // "b"
```

Оператор `[]` является синтаксическим сахаром для метода `get()` и предоставляет удобный способ доступа к элементам. Однако оба метода выбросят `IndexOutOfBoundsException`, если индекс выходит за границы списка. Для безопасного доступа используются методы `getOrNull()` или `getOrElse()`.

// Безопасный доступ
**val safe** = **list.getOrNull**(10)    // **null**
**val withDefault** = **list.getOrElse**(10) { "**default**" } // "**default**"

// Первый и последний
**val first** = **list.first**()         // "a"
**val last** = **list.last**()           // "e"
**val firstOrNull** = **list.firstOrNull**()
**val lastOrNull** = **list.lastOrNull**()
```text

### Проверка содержимого

```
val list = `listOf`(1, 2, 3, 4, 5)

// Проверка наличия
`list.contains`(`true`)
3 in list                        // `true`
list.`containsAll`(`listOf`(`true`)

// Проверка пустоты
list.`isEmpty()`  // `false`
list.`isNotEmpty()`  // `true`

// Размер
`list.size`                        // 5
`list.count`()                     // 5
```text

### Итерация

```
val list = `listOf`("a", "b", "c")

// `For loop`
for (item in list) {
    println(item)
}

// С индексом
for ((index, value) in list.`withIndex()`) {
    println("$index: $value")
}

// `ForEach`
list.`forEach` { println(it) }
list.`forEachIndexed` { index, value ->
    println("$index: $value")
}

// Итератор
val `iterator` = `list.iterator`()
while (`iterator`.`hasNext()`) {
    println(`iterator.next`())
}
```text

## Поиск и фильтрация

Поиск и фильтрация - это одни из самых распространенных операций при работе со списками. Kotlin предоставляет богатый набор функций для поиска элементов по различным критериям и фильтрации коллекций.

### Поиск элементов

Поиск элементов в списке может выполняться по различным критериям. Kotlin предоставляет несколько методов поиска, каждый из которых подходит для разных сценариев. Важно понимать разницу между методами, которые выбрасывают исключения, и методами, которые возвращают null.

```
val list = `listOf`(1, 2, 3, 4, 5, 6)

// Найти первый элемент
`list.find` { it > 3 }             // 4
`list.first` { it > 3 }            // 4
list.`firstOrNull()`  // `null`
```text

Метод `find` возвращает первый элемент, удовлетворяющий условию, или null, если такого элемента нет. Метод `first` работает аналогично, но выбрасывает `NoSuchElementException`, если элемент не найден. `firstOrNull` является безопасной версией `first`, которая возвращает null вместо исключения. Выбор метода зависит от того, является ли отсутствие элемента ожидаемой ситуацией или ошибкой.

// Найти последний элемент
list.findLast { it < 5 }         // 4
list.last { it < 5 }             // 4

// Найти индекс
list.indexOf(3)                  // 2
list.indexOfFirst { it > 3 }     // 3
list.indexOfLast { it < 5 }      // 3

// Проверка условий
list.any { it > 5 }              // true
list.all { it > 0 }              // true
list.none { it > 10 }            // true
```

### Фильтрация

```kotlin
val list = listOf(1, 2, 3, 4, 5, 6)

// Фильтр
val evens = list.filter { it % 2 == 0 }        // [2, 4, 6]
val odds = list.filterNot { it % 2 == 0 }      // [1, 3, 5]

// Фильтр с индексом
val filtered = list.filterIndexed { index, value ->
    index % 2 == 0 && value > 2
}

// Фильтр по типу
val mixed = listOf(1, "a", 2, "b", 3)
val numbers = mixed.filterIsInstance<Int>()    // [1, 2, 3]

// Фильтр null
val nullable = listOf(1, null, 2, null, 3)
val nonNull = nullable.filterNotNull()         // [1, 2, 3]

// Разделение
val (evens, odds) = list.partition { it % 2 == 0 }
// evens = [2, 4, 6], odds = [1, 3, 5]
```

## Преобразование списков

Преобразование списков позволяет трансформировать элементы коллекции в другие значения или типы. Это одна из основных операций функционального программирования, которая позволяет обрабатывать данные без изменения исходной коллекции.

### Map (трансформация)

Операция `map` применяет функцию преобразования к каждому элементу списка и возвращает новый список с преобразованными элементами. Исходный список остается неизменным, что соответствует принципам функционального программирования.

```kotlin
val list = listOf(1, 2, 3, 4, 5)

// Преобразование каждого элемента
val doubled = list.map { it * 2 }              // [2, 4, 6, 8, 10]
val strings = list.map { it.toString() }       // ["1", "2", "3", "4", "5"]
```

Операция `map` создает новую коллекцию того же размера, где каждый элемент является результатом применения функции к соответствующему элементу исходной коллекции. Это делает `map` идеальным инструментом для преобразования типов данных или вычисления производных значений. Важно помнить, что `map` всегда создает новую коллекцию, даже если преобразование тривиально, что может влиять на производительность для очень больших коллекций.

// С индексом
**val indexed** = **list.mapIndexed** { **index**, **value** ->
    "$**index**: $**value**"
}

// **Flatten**
**val nested** = **listOf(**listOf(1, 2), **listOf**(3, 4))
**val flat** = **nested.flatten**()                    // [1, 2, 3, 4]

// **FlatMap**
**val flatMapped** = **list.flatMap** {
    **listOf**(it, it * 2)
}                                               // [1, 2, 2, 4, 3, 6, 4, 8, 5, 10]
```text

### Zip (объединение)

```
val list1 = `listOf`(1, 2, 3)
val list2 = `listOf`("a", "b", "c")

// Zip в пары
val zipped = `list1.zip`(list2)                  // [(1, "a"), (2, "b"), (3, "c")]

// Zip с трансформацией
val zipped = `list1.zip`(list2) { a, b ->
    "$a$b"
}                                               // ["1a", "2b", "3c"]

// `Unzip`
val pairs = `listOf`(1 to "a", 2 to "b", 3 to "c")
val (numbers, letters) = `pairs.unzip`()
```text

### Windowed (скользящее окно)

```
val list = `listOf`(1, 2, 3, 4, 5)

// Скользящее окно
`list.windowed`(3)                                // [[1, 2, 3], [2, 3, 4], [3, 4, 5]]
`list.windowed`(3, step = 2)                     // [[1, 2, 3], [3, 4, 5]]

// `Chunked` (разбиение на части)
`list.chunked`(2)                                 // [[1, 2], [3, 4], [5]]
```text

## Сортировка

Сортировка списков - это операция упорядочивания элементов по определенному критерию. Kotlin предоставляет несколько методов сортировки, которые различаются по способу упорядочивания и по тому, изменяют ли они исходную коллекцию.

Сортировка является операцией с временной сложностью O(n log n) для большинства алгоритмов, что делает ее относительно дорогой операцией для больших коллекций. Поэтому важно понимать, когда сортировка действительно необходима, и использовать ее осознанно.

```
val list = `listOf`(3, 1, 4, 1, 5, 9, 2, 6)

// Сортировка (создает новый список)
val sorted = `list.sorted`()                      // [1, 1, 2, 3, 4, 5, 6, 9]
val `sortedDesc` = list.`sortedDescending()`       // [9, 6, 5, 4, 3, 2, 1, 1]
```text

Метод `sorted()` создает новый отсортированный список, не изменяя исходный. Это соответствует принципам неизменяемости в функциональном программировании. Метод `sortedDescending()` работает аналогично, но сортирует в обратном порядке. Для изменяемых списков доступны методы `sort()` и `sortDescending()`, которые изменяют исходный список in-place, что может быть более эффективно по памяти.

// Сортировка по ключу
val names = listOf("Alice", "Bob", "Charlie")
val byLength = names.sortedBy { it.length }     // ["Bob", "Alice", "Charlie"]
val byLengthDesc = names.sortedByDescending { it.length }

// Сортировка изменяемого списка (in-place)
val mutable = mutableListOf(3, 1, 4, 1, 5)
mutable.sort()                                  // изменяет исходный список
mutable.sortBy { it }
mutable.sortDescending()
```

## Группировка и агрегация

### Группировка

```kotlin
val list = listOf("apple", "banana", "apricot", "blueberry")

// Группировка по первому символу
val grouped = list.groupBy { it[0] }
// {'a': ["apple", "apricot"], 'b': ["banana", "blueberry"]}

// Группировка с трансформацией ключа
val byLength = list.groupBy { it.length }
// {5: ["apple"], 6: ["banana", "apricot"], 9: ["blueberry"]}

// Группировка с трансформацией значения
val grouped = list.groupBy(
    keySelector = { it[0] },
    valueTransform = { it.uppercase() }
)
```

### Агрегация

```kotlin
val list = listOf(1, 2, 3, 4, 5)

// Сумма
val sum = list.sum()                            // 15
val sumBy = listOf("a", "ab", "abc").sumOf { it.length } // 6

// Среднее
val average = list.average()                    // 3.0

// Минимум и максимум
val min = list.minOrNull()                      // 1
val max = list.maxOrNull()                      // 5
val minBy = listOf("a", "ab", "abc").minByOrNull { it.length } // "a"

// Fold и Reduce
val sum = list.reduce { acc, value -> acc + value } // 15
val product = list.fold(1) { acc, value -> acc * value } // 120
```

### Подсчет

```kotlin
val list = listOf(1, 2, 2, 3, 3, 3, 4)

// Подсчет вхождений
val counts = list.groupingBy { it }.eachCount()
// {1: 1, 2: 2, 3: 3, 4: 1}

// Количество элементов по условию
val count = list.count { it > 2 }               // 4
```

## Работа с индексами

```kotlin
val list = listOf("a", "b", "c", "d", "e")

// Получение элемента по индексу
val element = list[2]                           // "c"
val safe = list.getOrNull(10)                  // null
val withDefault = list.getOrElse(10) { "default" }

// Поиск индекса
val index = list.indexOf("c")                   // 2
val lastIndex = list.lastIndexOf("c")          // 2
val firstIndex = list.indexOfFirst { it == "c" }
val lastIndex = list.indexOfLast { it == "c" }

// Срезы
val sublist = list.subList(1, 3)               // ["b", "c"]
val slice = list.slice(0..2)                   // ["a", "b", "c"]
val take = list.take(3)                         // ["a", "b", "c"]
val drop = list.drop(2)                        // ["c", "d", "e"]
```

## Многомерные списки

```kotlin
// Создание двумерного списка
val matrix = listOf(
    listOf(1, 2, 3),
    listOf(4, 5, 6),
    listOf(7, 8, 9)
)

// Доступ к элементам
val element = matrix[1][2]                     // 6

// Итерация
for (row in matrix) {
    for (element in row) {
        print("$element ")
    }
    println()
}

// Создание с помощью функций
val matrix2 = List(3) { row ->
    List(3) { col -> row * 3 + col + 1 }
}
```

## Сравнение списков

```kotlin
val list1 = listOf(1, 2, 3)
val list2 = listOf(1, 2, 3)
val list3 = listOf(3, 2, 1)

// Равенство (порядок важен)
list1 == list2                                 // true
list1 == list3                                 // false

// Сравнение содержимого (без учета порядка)
list1.toSet() == list3.toSet()                // true

// Сравнение с помощью contentEquals
val arr1 = arrayOf(1, 2, 3)
val arr2 = arrayOf(1, 2, 3)
arr1.contentEquals(arr2)                       // true
```

## Производительность

### Сложность операций

| Операция | **ArrayList** | **LinkedList** (Java) |
|----------|-----------|-------------------|
| Доступ по индексу | `O(1)` | `O(n)` |
| Поиск элемента | `O(n)` | `O(n)` |
| Вставка в начало | `O(n)` | `O(1)` |
| Вставка в конец | `O(1)` | `O(1)` |
| Вставка в середину | `O(n)` | `O(n)` |
| Удаление по индексу | `O(n)` | `O(n)` |
| Удаление по значению | `O(n)` | `O(n)` |

### Оптимизация

```kotlin
// Используйте начальную емкость для ArrayList
val list = ArrayList<String>(1000)

// Используйте immutable списки когда возможно
val immutable = listOf(1, 2, 3) // более эффективно

// Избегайте частых операций вставки/удаления в середине
// Для этого лучше использовать LinkedList (через Java interop)

// Используйте sequences для ленивых вычислений
val result = (1..1_000_000)
    .asSequence()
    .filter { it % 2 == 0 }
    .map { it * 2 }
    .take(10)
    .toList()
```

## Лучшие практики

### Выбор типа списка

```kotlin
// Используйте listOf для неизменяемых списков
val config = listOf("option1", "option2", "option3")

// Используйте mutableListOf когда нужны изменения
val items = mutableListOf<String>()

// Используйте ArrayList с начальной емкостью для больших коллекций
val largeList = ArrayList<Data>(10_000)
```

### Null safety

```kotlin
// Список может содержать null
val nullableList: List<String?> = listOf("a", null, "b")

// Фильтрация null
val nonNull = nullableList.filterNotNull()

// Безопасный доступ
val first = nullableList.firstOrNull()
```

### Функциональный стиль

```kotlin
// Предпочитайте функциональные операции
val result = list
    .filter { it > 0 }
    .map { it * 2 }
    .take(10)
    .toList()

// Вместо императивного стиля
val result = mutableListOf<Int>()
for (item in list) {
    if (item > 0) {
        result.add(item * 2)
        if (result.size >= 10) break
    }
}
```

### Производительность

```kotlin
// Используйте sequences для больших коллекций
val largeResult = (1..1_000_000)
    .asSequence()
    .filter { it % 2 == 0 }
    .map { it * 2 }
    .toList()

// Избегайте множественных проходов
// Плохо:
val filtered = list.filter { it > 0 }
val doubled = filtered.map { it * 2 }

// Хорошо:
val result = list.filter { it > 0 }.map { it * 2 }
```

### Идиоматичный Kotlin

```kotlin
// Используйте деструктуризацию
val (first, second, third) = listOf(1, 2, 3)

// Используйте when для работы со списками
when {
    list.isEmpty() -> println("Empty")
    list.size == 1 -> println("Single: ${list.first()}")
    else -> println("Multiple: ${list.size}")
}

// Используйте расширения
fun <T> List<T>.secondOrNull(): T? = getOrNull(1)
val second = listOf(1, 2, 3).secondOrNull() // 2
```

## Продвинутые техники работы со списками

### Работа с подсписками

**Эффективная работа с частями списков:**

```kotlin
val list = listOf(1, 2, 3, 4, 5, 6, 7, 8, 9, 10)

// Разделение на части
val chunks = list.chunked(3)  // [[1, 2, 3], [4, 5, 6], [7, 8, 9], [10]]

// Разделение с перекрытием
val windowed = list.windowed(3, step = 1)  // [[1, 2, 3], [2, 3, 4], ...]
val sliding = list.windowed(3, step = 1, partialWindows = true)

// Разделение по предикату
val partitioned = list.partition { it % 2 == 0 }
val (evens, odds) = partitioned

// Разделение на группы
val grouped = list.groupBy { it % 3 }
```

Работа с подсписками позволяет эффективно обрабатывать данные порциями и создавать сложные трансформации.

### Операции с индексами

**Работа с индексами элементов в списке:**

```kotlin
val list = listOf("a", "b", "c", "d", "e")

// Поиск индекса элемента
val index = list.indexOf("c")  // 2
val lastIndex = list.lastIndexOf("c")

// Поиск с условием
val indexFirst = list.indexOfFirst { it.length > 1 }
val indexLast = list.indexOfLast { it.length > 1 }

// Итерация с индексами
list.forEachIndexed { index, value ->
    println("$index: $value")
}

// Фильтрация с индексами
val filtered = list.filterIndexed { index, value ->
    index % 2 == 0  // Четные индексы
}

// Трансформация с индексами
val mapped = list.mapIndexed { index, value ->
    "$index: $value"
}
```

Операции с индексами позволяют создавать более гибкие и мощные трансформации данных.

### Модификация списков

**Эффективная работа с изменяемыми списками:**

```kotlin
val mutableList = mutableListOf(1, 2, 3, 4, 5)

// Добавление элементов
mutableList.add(6)
mutableList.addAll(listOf(7, 8, 9))
mutableList += 10

// Вставка элементов
mutableList.add(0, 0)  // В начало
mutableList.addAll(1, listOf(1, 2))

// Удаление элементов
mutableList.remove(5)
mutableList.removeAt(0)
mutableList.removeAll { it > 7 }
mutableList -= 3

// Модификация элементов
mutableList.replaceAll { it * 2 }
mutableList[0] = 100

// Сортировка in-place
mutableList.sort()
mutableList.sortBy { it }
mutableList.sortWith(compareBy { it })
```

Изменяемые списки позволяют эффективно модифицировать данные без создания новых коллекций, что важно для производительности.

## Производительность List

### Выбор правильной реализации

**Различные реализации **List** имеют разные характеристики производительности:**

```kotlin
// ArrayList - O(1) доступ по индексу, O(n) вставка/удаление в середине
val arrayList = ArrayList<Int>(1000)  // Начальная емкость
arrayList[0] = 1  // O(1)
arrayList.add(0, 0)  // O(n) - сдвигает элементы

// LinkedList - O(n) доступ по индексу, O(1) вставка/удаление в начале/конце
val linkedList = LinkedList<Int>()
linkedList[0]  // O(n) - нужно пройти до индекса
linkedList.addFirst(0)  // O(1)
linkedList.removeFirst()  // O(1)
```

Выбор правильной реализации **List** зависит от паттернов доступа и модификации данных.

### Оптимизация операций List

**Оптимизация операций со списками для лучшей производительности:**

```kotlin
// Использование начальной емкости
val largeList = ArrayList<Int>(10000)  // Избегает перераспределения

// Batch операции
fun <T> MutableList<T>.addAllSafely(elements: Collection<T>) {
    if (elements.size > 1000) {
        // Для больших коллекций используем более эффективный способ
        ensureCapacity(size + elements.size)
    }
    addAll(elements)
}

// Оптимизация итерации
val list = listOf(1, 2, 3, 4, 5)
// Хорошо - прямое использование элементов
for (item in list) {
    println(item)
}

// Плохо - доступ по индексу в цикле
for (i in list.indices) {
    println(list[i])  // Множественный доступ
}
```

Оптимизация операций **List** улучшает производительность, особенно при работе с большими списками или частыми операциями.

Этот файл содержит полное руководство по **List** в **Kotlin**, покрывающее все основные аспекты от базовых операций до продвинутых техник и оптимизации производительности.

## Реальные примеры использования List

### List для обработки данных

**Использование **List** для обработки данных в реальных приложениях:**

```kotlin
// Обработка CSV данных
fun parseCSV(csv: String): List<Map<String, String>> {
    val lines = csv.lines()
    if (lines.isEmpty()) return emptyList()

    val headers = lines.first().split(",").map { it.trim() }

    return lines.drop(1)
        .map { line ->
            line.split(",")
                .map { it.trim() }
                .mapIndexed { index, value ->
                    headers.getOrNull(index) to value
                }
                .filter { it.first != null }
                .associate { it.first!! to it.second }
        }
}

// Использование
val csv = """
    name,age,email
    Alice,25,alice@example.com
    Bob,30,bob@example.com
""".trimIndent()

val data = parseCSV(csv)
data.forEach { row ->
    println("${row["name"]}: ${row["email"]}")
}
```

**List** идеально подходит для обработки последовательных данных, таких как **CSV** файлы или лог-файлы.

### List для реализации стека и очереди

**Использование **List** для реализации структур данных:**

```kotlin
// Реализация стека
class Stack<T> {
    private val elements = mutableListOf<T>()

    fun push(item: T) {
        elements.add(item)
    }

    fun pop(): T? {
        return elements.removeLastOrNull()
    }

    fun peek(): T? {
        return elements.lastOrNull()
    }

    fun isEmpty(): Boolean {
        return elements.isEmpty()
    }

    fun size(): Int {
        return elements.size
    }
}

// Реализация очереди
class Queue<T> {
    private val elements = mutableListOf<T>()

    fun enqueue(item: T) {
        elements.add(item)
    }

    fun dequeue(): T? {
        return elements.removeFirstOrNull()
    }

    fun peek(): T? {
        return elements.firstOrNull()
    }

    fun isEmpty(): Boolean {
        return elements.isEmpty()
    }

    fun size(): Int {
        return elements.size
    }
}

// Использование
val stack = Stack<Int>()
stack.push(1)
stack.push(2)
stack.push(3)
val top = stack.pop()  // 3

val queue = Queue<String>()
queue.enqueue("first")
queue.enqueue("second")
queue.enqueue("third")
val first = queue.dequeue()  // "first"
```

**List** может использоваться для реализации различных структур данных, таких как стек и очередь.

## Продвинутые операции List

### Windowed операции

**Использование **windowed** операций для анализа последовательностей:**

```kotlin
// Скользящее окно
val list = listOf(1, 2, 3, 4, 5, 6, 7, 8, 9, 10)

// Скользящее окно размером 3
val windows = list.windowed(3)  // [[1, 2, 3], [2, 3, 4], [3, 4, 5], ...]

// Скользящее среднее
val movingAverage = list.windowed(3) { it.average() }  // [2.0, 3.0, 4.0, ...]

// Скользящее окно с шагом
val stepped = list.windowed(3, step = 2)  // [[1, 2, 3], [3, 4, 5], [5, 6, 7], ...]

// Скользящее окно с частичными окнами
val partial = list.windowed(3, partialWindows = true)
// [[1, 2, 3], [2, 3, 4], ..., [10]]

// Использование для анализа данных
fun analyzeTrends(prices: List<Double>, windowSize: Int = 5): List<Trend> {
    return prices.windowed(windowSize) { window ->
        val average = window.average()
        val trend = when {
            window.last() > window.first() -> Trend.UP
            window.last() < window.first() -> Trend.DOWN
            else -> Trend.STABLE
        }
        TrendPoint(average, trend)
    }
}

enum class Trend { UP, DOWN, STABLE }
data class TrendPoint(val average: Double, val trend: Trend)
```

**Windowed** операции позволяют анализировать последовательности данных через скользящие окна, что полезно для анализа трендов и паттернов.

### Batch обработка

**Обработка больших списков порциями:**

```kotlin
// Разделение списка на батчи
fun <T> List<T>.batch(size: Int): List<List<T>> {
    return chunked(size)
}

// Обработка батчей
fun <T, R> List<T>.batchProcess(
    batchSize: Int,
    process: (List<T>) -> R
): List<R> {
    return chunked(batchSize).map { batch ->
        process(batch)
    }
}

// Асинхронная обработка батчей
suspend fun <T, R> List<T>.batchProcessAsync(
    batchSize: Int,
    process: suspend (List<T>) -> R
): List<R> = coroutineScope {
    chunked(batchSize).map { batch ->
        async {
            process(batch)
        }
    }.awaitAll()
}

// Использование
val largeList = (1..1000000).toList()

// Синхронная обработка
val results = largeList.batchProcess(1000) { batch ->
    batch.sum()
}

// Асинхронная обработка
val asyncResults = largeList.batchProcessAsync(1000) { batch ->
    delay(10)  // Имитация асинхронной операции
    batch.sum()
}
```

**Batch** обработка позволяет эффективно обрабатывать большие списки, уменьшая использование памяти и улучшая производительность.

## Продвинутые техники работы со списками

### Работа с индексами и срезами

**Продвинутые операции с индексами и срезами списков:**

```kotlin
// Работа с индексами
val list = listOf(1, 2, 3, 4, 5, 6, 7, 8, 9, 10)

// Получение элементов по индексам
val first = list.first()  // 1
val last = list.last()    // 10
val element = list[5]     // 6
val elementOrNull = list.getOrNull(20)  // null

// Получение срезов
val slice = list.slice(2..5)  // [3, 4, 5, 6]
val subList = list.subList(2, 6)  // [3, 4, 5, 6]
val take = list.take(5)  // [1, 2, 3, 4, 5]
val drop = list.drop(5)  // [6, 7, 8, 9, 10]
val takeLast = list.takeLast(3)  // [8, 9, 10]
val dropLast = list.dropLast(3)  // [1, 2, 3, 4, 5, 6, 7]

// Работа с индексами в циклах
list.forEachIndexed { index, value ->
    println("Index $index: $value")
}

// Поиск индексов
val indexOf = list.indexOf(5)  // 4
val lastIndexOf = list.lastIndexOf(5)  // 4
val indexOfFirst = list.indexOfFirst { it > 5 }  // 5
val indexOfLast = list.indexOfLast { it < 5 }  // 3

// Модификация по индексам
val mutableList = list.toMutableList()
mutableList[0] = 100
mutableList.add(0, 0)
mutableList.removeAt(0)
mutableList.set(0, 1)
```

Работа с индексами и срезами позволяет эффективно обрабатывать части списков без создания копий.

### Операции с несколькими списками

**Работа с несколькими списками одновременно:**

```kotlin
// Объединение списков
val list1 = listOf(1, 2, 3)
val list2 = listOf(4, 5, 6)
val combined = list1 + list2  // [1, 2, 3, 4, 5, 6]
val merged = list1.plus(list2)  // [1, 2, 3, 4, 5, 6]

// Zip для объединения элементов
val names = listOf("Alice", "Bob", "Charlie")
val ages = listOf(25, 30, 35)
val zipped = names.zip(ages)  // [(Alice, 25), (Bob, 30), (Charlie, 35)]
val zippedWithTransform = names.zip(ages) { name, age ->
    "$name is $age years old"
}  // ["Alice is 25 years old", "Bob is 30 years old", ...]

// Пересечение и разность
val listA = listOf(1, 2, 3, 4, 5)
val listB = listOf(4, 5, 6, 7, 8)
val intersection = listA.intersect(listB.toSet())  // [4, 5]
val difference = listA.subtract(listB.toSet())  // [1, 2, 3]

// Сравнение списков
val areEqual = list1 == list2  // false
val areSame = list1 === list2  // false (ссылочное сравнение)

// Сортировка нескольких списков вместе
data class Person(val name: String, val age: Int)
val people = listOf(Person("Alice", 25), Person("Bob", 30))
val sortedByName = people.sortedBy { it.name }
val sortedByAge = people.sortedByDescending { it.age }
```

Работа с несколькими списками позволяет комбинировать данные из различных источников и выполнять сложные операции.

Этот файл содержит полное руководство по **List** в **Kotlin**, покрывающее все основные аспекты от базовых операций до продвинутых техник, оптимизации производительности, реальных примеров использования, работы со стеками и очередями, **windowed** операций, **batch** обработки, работы с индексами и срезами, и операций с несколькими списками.

## Дополнительные техники работы со списками

### Работа с подсписками

**Продвинутые операции с подсписками:**

```kotlin
// Разделение списка на части
fun <T> List<T>.split(size: Int): List<List<T>> {
    return this.chunked(size)
}

// Разделение по предикату
fun <T> List<T>.splitBy(predicate: (T) -> Boolean): List<List<T>> {
    val result = mutableListOf<List<T>>()
    var current = mutableListOf<T>()

    for (element in this) {
        if (predicate(element)) {
            if (current.isNotEmpty()) {
                result.add(current.toList())
                current = mutableListOf()
            }
        } else {
            current.add(element)
        }
    }

    if (current.isNotEmpty()) {
        result.add(current)
    }

    return result
}

// Использование
val list = listOf(1, 2, 0, 3, 4, 0, 5, 6)
val split = list.splitBy { it == 0 }
// [[1, 2], [3, 4], [5, 6]]

// Разделение на два списка по предикату
fun <T> List<T>.partitionBy(predicate: (T) -> Boolean): Pair<List<T>, List<T>> {
    val trueList = mutableListOf<T>()
    val falseList = mutableListOf<T>()

    for (element in this) {
        if (predicate(element)) {
            trueList.add(element)
        } else {
            falseList.add(element)
        }
    }

    return trueList to falseList
}
```

Работа с подсписками позволяет эффективно разделять и обрабатывать данные.

### Работа с перестановками

**Генерация и работа с перестановками:**

```kotlin
// Генерация всех перестановок
fun <T> List<T>.permutations(): List<List<T>> {
    if (size <= 1) return listOf(this)

    val result = mutableListOf<List<T>>()
    for (i in indices) {
        val element = this[i]
        val rest = this.subList(0, i) + this.subList(i + 1, size)
        for (perm in rest.permutations()) {
            result.add(listOf(element) + perm)
        }
    }
    return result
}

// Использование
val list = listOf(1, 2, 3)
val perms = list.permutations()
// [[1, 2, 3], [1, 3, 2], [2, 1, 3], [2, 3, 1], [3, 1, 2], [3, 2, 1]]

// Перемешивание списка
fun <T> List<T>.shuffled(): List<T> {
    val mutable = this.toMutableList()
    mutable.shuffle()
    return mutable.toList()
}
```

Работа с перестановками позволяет генерировать различные комбинации элементов.

Этот файл содержит полное руководство по **List** в **Kotlin**, покрывающее все основные аспекты от базовых операций до продвинутых техник, оптимизации производительности, реальных примеров использования, работы со стеками и очередями, **windowed** операций, **batch** обработки, работы с индексами и срезами, операций с несколькими списками, работы с подсписками и перестановками.

## Дополнительные техники

### Работа с циклическими списками

**Создание и работа с циклическими списками:**

```kotlin
// Циклический доступ к списку
fun <T> List<T>.cycle(): Sequence<T> = sequence {
    while (true) {
        yieldAll(this@cycle)
    }
}

// Использование
val list = listOf(1, 2, 3)
val cycled = list.cycle().take(10).toList()
// [1, 2, 3, 1, 2, 3, 1, 2, 3, 1]

// Циклический доступ с индексами
fun <T> List<T>.getCyclic(index: Int): T {
    val normalizedIndex = index % size
    return this[if (normalizedIndex < 0) normalizedIndex + size else normalizedIndex]
}

// Использование
val colors = listOf("red", "green", "blue")
val color1 = colors.getCyclic(5)  // "green" (5 % 3 = 2)
val color2 = colors.getCyclic(-1)  // "blue" (последний элемент)
```

Циклические списки полезны для создания повторяющихся паттернов.

Этот файл содержит полное руководство по **List** в **Kotlin**, покрывающее все основные аспекты от базовых операций до продвинутых техник, оптимизации производительности, реальных примеров использования, работы со стеками и очередями, **windowed** операций, **batch** обработки, работы с индексами и срезами, операций с несколькими списками, работы с подсписками, перестановками и циклическими списками.


## Решение проблем

Типичные проблемы и решения см. в официальной документации (блок «Полезные ссылки» в начале документа).

## Частые вопросы

Ответы на частые вопросы по теме см. в разделах «Введение» и «Лучшие практики» в документе.

## Заключение

**List** является одной из наиболее часто используемых коллекций в **Kotlin**. Понимание различных операций над списками, от базовых добавления и удаления элементов до продвинутых техник работы с индексами, срезами, подсписками, перестановками и циклическими списками, позволяет эффективно работать с данными. Правильный выбор операций и структур данных помогает создавать эффективный и читаемый код.

Этот файл содержит полное руководство по **List** в **Kotlin**, покрывающее все основные аспекты от базовых операций до продвинутых техник, оптимизации производительности, реальных примеров использования, работы со стеками и очередями, **windowed** операций, **batch** обработки, работы с индексами и срезами, операций с несколькими списками, работы с подсписками, перестановками, циклическими списками, заключение и дополнительные ресурсы.

## Дополнительные ресурсы

**Для дальнейшего изучения **List** в **Kotlin** рекомендуется:**

- **Kotlin List Documentation**: **https**://**kotlinlang.org**/**api**/**latest**/**jvm**/**stdlib**/**kotlin.collections**/-**list**/
- **List Operations**: **https**://**kotlinlang.org**/**docs**/**collection-operations.html**

## Практические примеры использования

### Реализация стека и очереди

**Использование **List** для реализации стека и очереди:**

```kotlin
// Стек на основе List
class Stack<T> {
    private val items = mutableListOf<T>()

    fun push(item: T) {
        items.add(item)
    }

    fun pop(): T? {
        return if (items.isNotEmpty()) {
            items.removeAt(items.size - 1)
        } else {
            null
        }
    }

    fun peek(): T? = items.lastOrNull()
    fun isEmpty(): Boolean = items.isEmpty()
}

// Очередь на основе List
class Queue<T> {
    private val items = mutableListOf<T>()

    fun enqueue(item: T) {
        items.add(item)
    }

    fun dequeue(): T? {
        return if (items.isNotEmpty()) {
            items.removeAt(0)
        } else {
            null
        }
    }

    fun peek(): T? = items.firstOrNull()
    fun isEmpty(): Boolean = items.isEmpty()
}
```

**List** может использоваться для реализации различных структур данных.

### Обработка больших списков

**Эффективная обработка больших списков:**

```kotlin
// Batch обработка больших списков
fun <T, R> List<T>.processInBatches(
    batchSize: Int,
    processor: (List<T>) -> R
): List<R> {
    return this.chunked(batchSize)
        .map { batch ->
            processor(batch)
        }
}

// Использование
val largeList = (1..1_000_000).toList()
val results = largeList.processInBatches(1000) { batch ->
    batch.sum()
}
```

**Batch** обработка позволяет эффективно работать с большими объемами данных.

### Работа с индексами и элементами

**Пример эффективной работы с индексами:**

```kotlin
// Поиск индекса элемента
fun <T> List<T>.findIndex(predicate: (T) -> Boolean): Int? {
    return this.indexOfFirst(predicate).takeIf { it >= 0 }
}

// Получение элемента по индексу с fallback
fun <T> List<T>.getOrElse(index: Int, defaultValue: T): T {
    return this.getOrNull(index) ?: defaultValue
}

// Обновление элемента по индексу
fun <T> List<T>.updateAt(index: Int, transform: (T) -> T): List<T> {
    return this.mapIndexed { i, item ->
        if (i == index) transform(item) else item
    }
}

// Использование
val numbers = listOf(1, 2, 3, 4, 5)
val index = numbers.findIndex { it > 3 }  // 3
val updated = numbers.updateAt(2) { it * 10 }  // [1, 2, 30, 4, 5]
```

Работа с индексами позволяет эффективно манипулировать элементами списка.

### Операции с несколькими списками

**Пример работы с несколькими списками:**

```kotlin
// Zip с трансформацией
fun <A, B, R> List<A>.zipWith(other: List<B>, transform: (A, B) -> R): List<R> {
    return this.zip(other, transform)
}

// Пересечение списков
fun <T> List<T>.intersect(other: List<T>): List<T> {
    return this.filter { it in other }
}

// Объединение с сохранением порядка
fun <T> List<T>.union(other: List<T>): List<T> {
    val seen = mutableSetOf<T>()
    return (this + other).filter { seen.add(it) }
}

// Использование
val list1 = listOf(1, 2, 3, 4)
val list2 = listOf(3, 4, 5, 6)
val zipped = list1.zipWith(list2) { a, b -> a + b }  // [4, 6, 8, 10]
val intersection = list1.intersect(list2)  // [3, 4]
val union = list1.union(list2)  // [1, 2, 3, 4, 5, 6]
```

Операции с несколькими списками позволяют комбинировать данные из различных источников.

Этот файл содержит полное руководство по **List** в **Kotlin**, покрывающее все основные аспекты от базовых операций до продвинутых техник, оптимизации производительности, реальных примеров использования, работы со стеками и очередями, **windowed** операций, **batch** обработки, работы с индексами и срезами, операций с несколькими списками, работы с подсписками, перестановками, циклическими списками, практические примеры использования, включая работу с индексами и операции с несколькими списками, заключение, дополнительные ресурсы и итоговые рекомендации.

