# String Permutations

Кратко: перестановка - это перестановка элементов в множестве. В этом руководстве рассматриваются различные библиотеки для генерации всех возможных перестановок строки: Apache Commons, Guava, и CombinatoricsLib.

**Дата последнего обновления:** 2025-01-15

## Полезные ссылки

### Официальная документация
- [GeeksforGeeks: Write a program to print all permutations of a given string](https://www.geeksforgeeks.org/write-a-c-program-to-print-all-permutations-of-a-given-string/)

### См. также
- `./palindrome-check.md` - проверка палиндромов
- `./levenshtein-distance.md` - расстояние Левенштейна

## Содержание

- [Описание алгоритма](#описание-алгоритма)
- [Java Implementation](#java-implementation)
- [Kotlin Implementation](#kotlin-implementation)
- [Сравнение библиотек](#сравнение-библиотек)
- [Сложность](#сложность)

## Описание алгоритма

Перестановка - это перестановка элементов в множестве. Другими словами, это все возможные варианты порядка сбора.

В этом руководстве мы узнаем, как легко создавать перестановки в Java с использованием сторонних библиотек. В частности, мы будем работать с перестановкой в строке.

Иногда нам нужно проверить все возможные перестановки строкового значения. Часто для умопомрачительных онлайн-упражнений по кодированию и реже для повседневных рабочих задач. Например, строка «abc» будет иметь шесть различных способов расположения символов внутри: «abc», «acb», «cab», «bac», «bca», «cba».

Пара четко определенных алгоритмов может помочь нам создать все возможные перестановки для определенного значения String. Например, самым известным является алгоритм Хипа. Тем не менее, это довольно сложно и неинтуитивно. Кроме того, рекурсивный подход усугубляет ситуацию.

Реализация алгоритма генерации перестановок потребует написания пользовательской логики. Легко сделать ошибку в реализации и трудно проверить, правильно ли она работает с течением времени. Также нет смысла переписывать написанное ранее.

Кроме того, работая со строковыми значениями, можно переполнить пул строк, создав слишком много экземпляров, если делать это неаккуратно.

## Java Implementation

### Apache Commons

Вот библиотеки, которые в настоящее время предоставляют такие функции:

#### Зависимость Maven

Во-первых, давайте добавим в проект общие коллекции зависимости Maven:

```xml
<dependency>
    <groupId>org.apache.commons</groupId>
    <artifactId>commons-collections4</artifactId>
    <version>4.4</version>
</dependency>
```

### Eager перестановки

В целом Apache предоставляет простой API. CollectionUtils охотно создает перестановки, поэтому мы должны быть осторожны при работе с длинными строковыми значениями:

```java
public List<String> eagerPermutationWithRepetitions(final String string) {
    final List<Character> characters = Helper.toCharacterList(string);
    return CollectionUtils.permutations(characters)
        .stream()
        .map(Helper::toString)
        .collect(Collectors.toList());
}
```

### Lazy перестановки

В то же время, чтобы заставить его работать с ленивым подходом, мы должны использовать PermutationIterator:

```java
public List<String> lazyPermutationWithoutRepetitions(final String string) {
    final List<Character> characters = Helper.toCharacterList(string);
    final PermutationIterator<Character> permutationIterator = new PermutationIterator<>(characters);
    final List<String> result = new ArrayList<>();
    
    while (permutationIterator.hasNext()) {
        result.add(Helper.toString(permutationIterator.next()));
    }
    
    return result;
}
```

### Вспомогательные методы

Мы будем использовать метод Helper.toCharacterList в приведенных ниже примерах. Этот метод инкапсулирует сложность преобразования строки в список символов:

```java
static List<Character> toCharacterList(final String string) {
    return string.chars()
        .mapToObj(s -> ((char) s))
        .collect(Collectors.toList());
}
```

Кроме того, мы будем использовать вспомогательный метод для преобразования списка символов в строку:

```java
static String toString(Collection<Character> collection) {
    return collection.stream()
        .map(s -> s.toString())
        .collect(Collectors.joining());
}
```

### Особенности Apache Commons

Эта библиотека не обрабатывает дубликаты, поэтому строка «aaaaaa» будет производить 720 перестановок, что часто нежелательно. Кроме того, у PermutationIterator нет метода для получения количества перестановок. В этом случае мы должны рассчитать их отдельно в зависимости от размера ввода.

## Guava

Во-первых, давайте добавим в проект зависимость Maven для библиотеки Guava:

```xml
<dependency>
    <groupId>com.google.guava</groupId>
    <artifactId>guava</artifactId>
    <version>31.0.1-jre</version>
</dependency>
```

### Перестановки с повторениями

Guava позволяет создавать перестановки с помощью Collections2. API прост в использовании:

```java
public List<String> permutationWithRepetitions(final String string) {
    final List<Character> characters = Helper.toCharacterList(string);
    return Collections2.permutations(characters)
        .stream()
        .map(Helper::toString)
        .collect(Collectors.toList());
}
```

Результатом Collections2.permutations является коллекция PermutationCollection, которая обеспечивает легкий доступ к перестановкам. Все перестановки создаются лениво.

### Перестановки без повторений

Кроме того, этот класс предоставляет API для создания перестановок без повторений:

```java
public List<String> permutationWithoutRepetitions(final String string) {
    final List<Character> characters = Helper.toCharacterList(string);
    return Collections2.orderedPermutations(characters)
        .stream()
        .map(Helper::toString)
        .collect(Collectors.toList());
}
```

### Особенности Guava

Однако проблема с этими методами заключается в том, что они помечены аннотацией @Beta, что не гарантирует, что этот API не изменится в будущих выпусках.

## CombinatoricsLib

Чтобы использовать его в проекте, добавим Maven-зависимость combinatoricslib3:

```xml
<dependency>
    <groupId>com.github.dpaukov</groupId>
    <artifactId>combinatoricslib3</artifactId>
    <version>3.3.3</version>
</dependency>
```

### Перестановки без повторений

Хотя это небольшая библиотека, она предоставляет множество инструментов комбинаторики, включая перестановки. Сам API очень интуитивно понятен и использует потоки Java. Давайте создадим перестановки из определенной строки или списка символов:

```java
public List<String> permutationWithoutRepetitions(final String string) {
    List<Character> chars = Helper.toCharacterList(string);
    return Generator.permutation(chars)
        .simple()
        .stream()
        .map(Helper::toString)
        .collect(Collectors.toList());
}
```

Приведенный выше код создает генератор, который предоставит перестановки для String. Перестановка будет получена лениво. Таким образом, мы только создали генератор и подсчитали ожидаемое количество перестановок.

### Перестановки с повторениями

В то же время с помощью этой библиотеки мы можем определить стратегию для дубликатов. Если мы возьмем в качестве примера строку «аааааа», мы получим только одну вместо 720 одинаковых перестановок.

```java
public List<String> permutationWithRepetitions(final String string) {
    List<Character> chars = Helper.toCharacterList(string);
    return Generator.permutation(chars)
        .simple(TreatDuplicatesAs.IDENTICAL)
        .stream()
        .map(Helper::toString)
        .collect(Collectors.toList());
}
```

TreatDuplicatesAs позволяет нам определить, как мы хотели бы обрабатывать дубликаты.

### Другие опции

```java
// Перестановки с ограничением длины
public List<String> permutationWithLength(final String string, final int length) {
    List<Character> chars = Helper.toCharacterList(string);
    return Generator.permutation(chars)
        .simple(length)
        .stream()
        .map(Helper::toString)
        .collect(Collectors.toList());
}

// Перестановки с фильтром
public List<String> permutationWithFilter(final String string) {
    List<Character> chars = Helper.toCharacterList(string);
    return Generator.permutation(chars)
        .simple()
        .stream()
        .filter(perm -> perm.get(0) != 'a') // Фильтр: не начинается с 'a'
        .map(Helper::toString)
        .collect(Collectors.toList());
}
```

## Kotlin Implementation

В Kotlin генерация перестановок может быть реализована следующим образом:

### Рекурсивная реализация

```kotlin
fun generatePermutations(input: String): List<String> {
    if (input.length <= 1) {
        return listOf(input)
    }

    val result = mutableListOf<String>()
    for (i in input.indices) {
        val char = input[i]
        val remaining = input.substring(0, i) + input.substring(i + 1)
        val perms = generatePermutations(remaining)
        for (perm in perms) {
            result.add(char + perm)
        }
    }
    return result
}
```

### Итеративная реализация

```kotlin
fun generatePermutationsIterative(input: String): List<String> {
    val result = mutableListOf<String>()
    val queue = mutableListOf("")
    
    for (char in input) {
        val size = queue.size
        for (i in 0 until size) {
            val current = queue.removeAt(0)
            for (j in 0..current.length) {
                val newPerm = current.substring(0, j) + char + current.substring(j)
                queue.add(newPerm)
            }
        }
    }
    
    return queue
}
```

### Функциональный стиль

```kotlin
fun String.permutations(): List<String> {
    if (length <= 1) return listOf(this)
    
    return flatMapIndexed { index, char ->
        (substring(0, index) + substring(index + 1))
            .permutations()
            .map { char + it }
    }
}
```

Использование:

```kotlin
fun main() {
    val input = "abc"
    val perms = generatePermutations(input)
    println(perms) // [abc, acb, bac, bca, cab, cba]
    
    // Функциональный стиль
    val perms2 = input.permutations()
    println(perms2) // [abc, acb, bac, bca, cab, cba]
}
```

### Использование библиотек

Для Kotlin можно использовать те же Java библиотеки или Kotlin-специфичные решения:

```kotlin
// Использование Java библиотек через Kotlin
import org.apache.commons.collections4.CollectionUtils
import org.paukov.combinatorics3.Generator

fun generatePermutationsWithCombinatoricsLib(input: String): List<String> {
    val chars = input.toList()
    return Generator.permutation(chars)
        .simple()
        .map { it.joinToString("") }
        .toList()
}
```

## Сравнение библиотек

| Библиотека | Ленивая генерация | Обработка дубликатов | Простота API | Статус |
|------------|-------------------|----------------------|--------------|--------|
| Apache Commons | Да (PermutationIterator) | Нет | Средняя | Стабильная |
| Guava | Да | Частично | Простая | @Beta |
| CombinatoricsLib | Да | Да | Очень простая | Стабильная |

## Сложность

### Временная сложность

- **Генерация всех перестановок:** O(n! * n), где n - длина строки
- **Количество перестановок:** n! для строки без дубликатов
- **С дубликатами:** n! / (n1! * n2! * ... * nk!), где ni - количество повторений i-го символа

### Пространственная сложность

- **Eager подход:** O(n! * n) - хранит все перестановки
- **Lazy подход:** O(n) - только текущая перестановка

## Особенности

- **Ленивая генерация:** Все библиотеки поддерживают ленивую генерацию
- **Обработка дубликатов:** CombinatoricsLib лучше всего обрабатывает дубликаты
- **Производительность:** Lazy подход более эффективен по памяти

## Применение

Генерация перестановок используется в:

- Криптографии
- Тестировании (покрытие всех комбинаций)
- Играх (генерация уровней)
- Оптимизации
- Задачах на собеседованиях

## Ручная реализация

### Рекурсивный подход

Если нужно реализовать без библиотек:

```java
public List<String> generatePermutations(String str) {
    List<String> result = new ArrayList<>();
    generatePermutationsHelper("", str, result);
    return result;
}

private void generatePermutationsHelper(String prefix, String remaining, List<String> result) {
    int n = remaining.length();
    if (n == 0) {
        result.add(prefix);
    } else {
        for (int i = 0; i < n; i++) {
            generatePermutationsHelper(
                prefix + remaining.charAt(i),
                remaining.substring(0, i) + remaining.substring(i + 1, n),
                result
            );
        }
    }
}
```

### Итеративный подход (Heap's Algorithm)

```java
public List<String> generatePermutationsIterative(String str) {
    List<String> result = new ArrayList<>();
    char[] chars = str.toCharArray();
    int[] indexes = new int[chars.length];
    
    result.add(new String(chars));
    
    int i = 0;
    while (i < chars.length) {
        if (indexes[i] < i) {
            swap(chars, i % 2 == 0 ? 0 : indexes[i], i);
            result.add(new String(chars));
            indexes[i]++;
            i = 0;
        } else {
            indexes[i] = 0;
            i++;
        }
    }
    
    return result;
}

private void swap(char[] arr, int i, int j) {
    char temp = arr[i];
    arr[i] = arr[j];
    arr[j] = temp;
}
```

## Когда использовать

### Используйте библиотеки, когда:

- Нужна надежность
- Важна производительность
- Не хотите писать свой код
- Нужна обработка дубликатов

### Используйте ручную реализацию, когда:

- Нужен полный контроль
- Обучаете алгоритмы
- Нет внешних зависимостей

## Заключение

Есть много способов справиться с комбинаторикой и, в частности, с перестановками. Все эти библиотеки могут значительно помочь в этом. Стоит попробовать их все и решить, какой из них соответствует вашим потребностям. Хотя многие люди иногда призывают написать весь свой код, нет смысла тратить время на то, что уже есть и обеспечивает хорошую функциональность.
