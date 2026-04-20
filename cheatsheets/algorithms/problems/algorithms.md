---
title: "Хеширование и хеш-функции (Hashing and Hash Functions)"
description: "Преобразование объекта в числовой индекс для быстрого доступа: коллизии, требования к хеш-функции. Техники — складывание (folding), биннинг, середина квадрата; простая хеш для строк и умножение. Java и Kotlin."
tags:
  - algorithms
  - problems
difficulty: "intermediate"
prerequisites: []
next: []
updated: "2026-04-20"
---
# Хеширование и хеш-функции (Hashing and Hash Functions)

Преобразование объекта в числовой индекс для быстрого доступа: коллизии, требования к хеш-функции. Техники — складывание (folding), биннинг, середина квадрата; простая хеш для строк и умножение. Java и Kotlin.

## Полезные ссылки

### Официальная документация
- [Java Collections (Java SE 8)](https://docs.oracle.com/javase/8/docs/api/java/util/package-summary.html)
- [Object.hashCode() (Java SE 8)](https://docs.oracle.com/javase/8/docs/api/java/lang/Object.html#hashCode--)

### См. также
- [Структуры данных](../data-structures/) — обзор разделов
- [Задачи и алгоритмы](./) — обзор разделов

- [[a-star-pathfinding|Поиск пути A* (A* Pathfinding Algorithm)]]
- [[branch-prediction|Предсказание ветвления (Branch Prediction)]]
- [[calculator-implementation|Calculator Implementation]]
- [[circular-buffer|Circular Buffer]]
- [[combinatorial-problems-overview|Обзор комбинаторных задач (Combinatorial Problems Overview)]]

## Содержание

- [Обзор](#обзор)
- [Что такое хеширование?](#что-такое-хеширование)
- [Хеш-функции](#хеш-функции)
- [Реализация на Java](#реализация-на-java)
- [Реализация на Kotlin](#реализация-на-kotlin)
- [Примеры реализации](#примеры-реализации)
- [Сравнение методов](#сравнение-методов)
- [Лучшие практики](#лучшие-практики)
- [Решение проблем](#решение-проблем)
- [Частые вопросы](#частые-вопросы)
- [Резюме](#резюме)

## Обзор

Хеширование даёт структурам данных (HashMap, HashSet и др.) доступ к элементам за константное время: объект преобразуется в число — индекс в массиве. Ниже — техники построения хеш-функций и работа с коллизиями.

## Что такое хеширование?

Доступ по индексу в массиве — O(1), но индексы числовые. Хеш-функция сопоставляет объекту (например, строке) число в диапазоне [0, N], которое используется как индекс. Это значение называют хешем.

## Хеш-функции

Хеш-функция должна быть быстрой и по возможности равномерно распределять значения по диапазону, чтобы уменьшить коллизии. Коллизия — когда разным ключам соответствует один и тот же хеш; неизбежна при конечном N. Разрешение: цепочки (chaining) или открытая адресация.

Техника складывания (folding): символы строки в числа (например, ASCII), разбить на группы, сложить группы, взять остаток по модулю размера таблицы. Пример для "Java": J=74, a=97, v=118, a=97 группы (74,97), (118,97) 7497+11897=19394 19394 % 100000.

```text
J = 74
a = 97
v = 118
a = 97
```

## Реализация на Java

```java
// Реализация хеширования строки техникой складывания: преобразование символов в числа,
// группировка и суммирование по модулю размера массива.
public class FoldingHashFunction {

    public static int hash(String input, int arraySize) {
        // Преобразуем строку в ASCII значения
        StringBuilder asciiValues = new StringBuilder();
        for (char c : input.toCharArray()) {
            asciiValues.append((int) c);
        }

        String asciiString = asciiValues.toString();
        int groupSize = 2; // Размер группы
        int sum = 0;

        // Разбиваем на группы и суммируем
        for (int i = 0; i < asciiString.length(); i += groupSize) {
            int end = Math.min(i + groupSize, asciiString.length());
            String group = asciiString.substring(i, end);
            sum += Integer.parseInt(group);
        }

        return sum % arraySize;
    }

    public static void main(String[] args) {
        String input = "Java";
        int arraySize = 100000;
        int hash = hash(input, arraySize);
        System.out.println("Hash value for '" + input + "': " + hash);
    }
}
```

Биннинг: разбиение диапазона значений на интервалы (бины); хеш = value / numberOfBins. Быстро, но распределение неравномерное. Середина квадрата (mid-square): возвести число в квадрат, взять средние цифры — даёт псевдослучайное разбрасывание.

```java
public class BinningHashFunction {

    public static int hash(int value, int numberOfBins) {
        return value / numberOfBins;
    }

    public static void main(String[] args) {
        int[] values = {0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15};
        int numberOfBins = 10;

        for (int value : values) {
            int bin = hash(value, numberOfBins);
            System.out.println("Value " + value + " -> Bin " + bin);
        }
    }
}
```


```java
public class MidSquareHashFunction {

    public static int hash(int value, int digits) {
        long square = (long) value * value;
        String squareStr = String.valueOf(square);

        // Дополняем нулями слева, если нужно
        while (squareStr.length() < digits * 2) {
            squareStr = "0" + squareStr;
        }

        // Извлекаем средние цифры
        int start = (squareStr.length() - digits) / 2;
        String middle = squareStr.substring(start, start + digits);

        return Integer.parseInt(middle);
    }

    public static void main(String[] args) {
        int value = 1111;
        int digits = 4;
        int hash = hash(value, digits);
        System.out.println("Hash value for " + value + ": " + hash);
        // Output: Hash value for 1111: 2343
    }
}
```

## Реализация на Kotlin

```kotlin
object FoldingHashFunctionK {
    fun hash(input: String, arraySize: Int): Int {
        // Преобразуем строку в ASCII значения
        val asciiValues = StringBuilder()
        for (c in input.toCharArray()) {
            asciiValues.append(c.code)
        }

        val asciiString = asciiValues.toString()
        val groupSize = 2 // Размер группы
        var sum = 0

        // Разбиваем на группы и суммируем
        var i = 0
        while (i < asciiString.length) {
            val end = minOf(i + groupSize, asciiString.length)
            val group = asciiString.substring(i, end)
            sum += group.toInt()
            i += groupSize
        }

        return sum % arraySize
    }
}
```


```kotlin
object MidSquareHashFunctionK {
    fun hash(input: Int, arraySize: Int): Int {
        val squared = input.toLong() * input
        val squaredString = squared.toString()
        val mid = squaredString.length / 2
        val start = maxOf(0, mid - 2)
        val end = minOf(squaredString.length, mid + 2)
        val midDigits = squaredString.substring(start, end).toInt()
        return midDigits % arraySize
    }
}
```


```kotlin
object BinningHashFunctionK {
    fun hash(input: Int, arraySize: Int): Int {
        val binSize = 1000
        val bin = input / binSize
        return bin % arraySize
    }
}
```

```kotlin
fun main() {
    val input = "Java"
    val arraySize = 100000
    val hash = FoldingHashFunctionK.hash(input, arraySize)
    println("Hash value for '$input': $hash")

    val midSquareHash = MidSquareHashFunctionK.hash(1111, 10000)
    println("Mid-square hash for 1111: $midSquareHash")

    val binningHash = BinningHashFunctionK.hash(5000, 10)
    println("Binning hash for 5000: $binningHash")
}
```

## Примеры реализации

```java
public class SimpleStringHash {

    public static int hash(String key, int tableSize) {
        int hash = 0;
        for (int i = 0; i < key.length(); i++) {
            hash = (hash * 31 + key.charAt(i)) % tableSize;
        }
        return hash;
    }
}
```

Умножение (метод Кнута): дробная часть (key * A), где A = (√5−1)/2, затем умножение на размер таблицы — хорошее распределение.

```java
public class MultiplicationHash {

    private static final double A = (Math.sqrt(5) - 1) / 2; // Константа (√5 - 1) / 2

    public static int hash(int key, int tableSize) {
        double fractionalPart = (key * A) % 1;
        return (int) (tableSize * fractionalPart);
    }
}
```

## Сравнение методов

| Метод | Скорость | Равномерность распределения | Сложность реализации |
|-------|----------|----------------------------|---------------------|
| Складывание | Высокая | Средняя | Низкая |
| Биннинг | Очень высокая | Низкая | Очень низкая |
| Середина квадрата | Средняя | Высокая | Средняя |
| Умножение | Высокая | Высокая | Низкая |

## Лучшие практики

Размер таблицы — простое число или степень двойки для лучшего распределения при модуле. Коллизии разрешать цепочками или открытой адресацией в зависимости от нагрузки. В Java для ключей коллекций переопределять `hashCode` и `equals` согласованно; хеш должен быть детерминированным и по возможности равномерным. Не использовать неподконтрольные внешние данные как единственный источник хеша без ограничений (риск DoS при коллизиях).

## Решение проблем

| Симптом | Возможная причина | Решение |
|---------|-------------------|---------|
| Много коллизий | Плохое распределение или неудачный размер таблицы | Увеличить размер таблицы; использовать умножение или другую технику; проверить качество хеша |
| Разный хеш при одинаковом ключе | Недетерминированность (например, случайность в хеше) | Хеш-функция должна зависеть только от ключа и быть детерминированной |
| Медленный доступ | Тяжёлая хеш-функция или длинные цепочки | Упростить хеш; увеличить таблицу; проверить нагрузку (load factor) |

## Частые вопросы

**Почему в Java нужно переопределять equals вместе с hashCode?** Контракт: если equals возвращает true, hashCode должен совпадать; иначе объекты «равны», но попадают в разные корзины и теряются в HashSet/HashMap.

**Когда использовать цепочки, когда открытую адресацию?** Цепочки проще и устойчивы к высокой нагрузке; открытая адресация экономит память и кэш при умеренной нагрузке.

**Что такое load factor?** Отношение числа элементов к размеру таблицы; при превышении порога (например, 0.75) таблицу расширяют (rehash), чтобы уменьшить длину цепочек.

## Резюме

Хеш-функция должна быть быстрой, детерминированной и по возможности равномерно распределять ключи. Складывание, биннинг, середина квадрата и умножение — базовые техники; в практике Java чаще используют встроенный `hashCode` и хороший размер таблицы с разрешением коллизий.
