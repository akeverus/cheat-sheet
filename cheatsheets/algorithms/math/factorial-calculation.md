---
title: "Вычисление факториала (Factorial Calculation)"
description: "Факториал n (n!) — произведение 1×2×…×n; 0! = 1. В документе описаны цикл, рекурсия, Stream API, Apache Commons Math, Guava и работа с BigInteger в Java и Kotlin."
tags:
  - algorithms
  - math
  - factorial-calculation
difficulty: "intermediate"
prerequisites: []
next: []
updated: "2026-02-11"
---
# Вычисление факториала (`Factorial Calculation`)

Факториал n (n!) — произведение 1×2×…×n; 0! = 1. В документе описаны цикл, рекурсия, Stream API, Apache Commons Math, Guava и работа с `BigInteger` в `Java` и Kotlin.

## Полезные ссылки

### Официальная документация
- [GeeksforGeeks: Program for factorial of a number](https://www.geeksforgeeks.org/program-for-factorial-of-a-number/)

### См. также
- [Ряд Фибоначчи](fibonacci-sequence.md) — ряд Фибоначчи
- [Наибольший общий делитель](greatest-common-divisor.md) — НОД

## Содержание

- [Описание алгоритма](#описание-алгоритма)
  - [Математическое определение](#математическое-определение)
  - [Примеры](#примеры)
- [Реализация на Java](#реализация-на-java)
- [Реализация на Kotlin](#реализация-на-kotlin)
- [Работа с большими числами](#работа-с-большими-числами)
- [Сравнение подходов](#сравнение-подходов)
- [Сложность](#сложность)
- [Особенности](#особенности)
- [Применение](#применение)
- [Варианты задачи](#варианты-задачи)
- [Когда использовать](#когда-использовать)
- [Лучшие практики](#лучшие-практики)
- [Решение проблем](#решение-проблем)
- [Частые вопросы](#частые-вопросы)
- [Заключение](#заключение)


## Описание алгоритма

Учитывая неотрицательное целое число n, факториал - это произведение всех положительных целых чисел, меньших или равных n.

Ниже — способы вычисления в `Java` и Kotlin.

### Математическое определение

n! = n × (n−1) × … × 1; 0! = 1. Примеры: 5! = 120, 3! = 6.

## Реализация на Java

### Подход 1: Цикл for

Итеративное умножение; для n > 20 результат не помещается в `long` — нужен `BigInteger`.

```java
// Факториал циклом; при n>20 — переполнение long
public long factorialUsingForLoop(int n) {
    long fact = 1;
    for (int i = 2; i <= n; i++) {
        fact = fact * i;
    }
    return fact;
}
```

### Обработка переполнения

Проверка n < 0 и n > 20 перед вычислением; при нарушении — исключение.

```java
// Валидация: отрицательные и n>20 для long
public long factorialUsingForLoopWithCheck(int n) {
    if (n < 0) {
        throw new IllegalArgumentException("Factorial is not defined for negative numbers");
    }
    
    if (n > 20) {
        throw new IllegalArgumentException("Result will overflow long type");
    }
    
    long fact = 1;
    for (int i = 2; i <= n; i++) {
        fact = fact * i;
    }
    return fact;
}
```

## Подход 2: Stream API

`LongStream.rangeClosed(1, n).reduce(1, (a, b) -> a * b)` — компактная запись; ограничение по long то же (n ≤ 20).

```java
// Через reduce; для n>20 — переполнение
public long factorialUsingStreams(int n) {
    return LongStream.rangeClosed(1, n)
        .reduce(1, (long x, long y) -> x * y);
}
```

В этой программе мы сначала используем **LongStream** для перебора чисел от 1 до n. Затем мы использовали метод `**reduce**()`, который использует значение идентичности и функцию-аккумулятор для шага сокращения.

Версия с проверкой и `Math::multiplyExact` (бросает исключение при переполнении).

```java
public long factorialUsingStreamsSafe(int n) {
    if (n < 0 || n > 20) {
        throw new IllegalArgumentException("n must be between 0 and 20");
    }
    
    return LongStream.rangeClosed(1, n)
        .reduce(1, Math::multiplyExact);
}
```

## Подход 3: Рекурсия

Базовый случай: n == 0 или 1 → 1; иначе n * factorial(n−1). Стек вызовов O(n); для больших n возможен StackOverflowError.

```java
// Рекурсия: базовый случай 0/1, иначе n * factorial(n-1)
public long factorialUsingRecursion(int n) {
    if (n <= 2) {
        return n;
    }
    return n * factorialUsingRecursion(n - 1);
}
```

### Улучшенная версия с базовым случаем

```java
public long factorialUsingRecursionImproved(int n) {
    if (n < 0) {
        throw new IllegalArgumentException("Factorial is not defined for negative numbers");
    }
    
    if (n == 0 || n == 1) {
        return 1;
    }
    
    return n * factorialUsingRecursionImproved(n - 1);
}
```

### Хвостовая рекурсия (для больших чисел)

Накопление результата в аргументе `acc`; при поддержке tail-call не растёт стек. Возврат `BigInteger` для произвольного n.

```java
public BigInteger factorialTailRecursive(int n) {
    return factorialTailRecursiveHelper(BigInteger.ONE, BigInteger.valueOf(n));
}

private BigInteger factorialTailRecursiveHelper(BigInteger acc, BigInteger n) {
    if (n.compareTo(BigInteger.ONE) <= 0) {
        return acc;
    }
    return factorialTailRecursiveHelper(acc.multiply(n), n.subtract(BigInteger.ONE));
}
```

## Подход 4: Apache Commons Math

Класс `CombinatoricsUtils.factorial(n)` возвращает long; при переполнении — `MathArithmeticException`. Зависимость `commons-math3`.

```xml
<dependency>
    <groupId>org.apache.commons</groupId>
    <artifactId>commons-math3</artifactId>
    <version>3.6.1</version>
</dependency>
```

```java
// CombinatoricsUtils.factorial(n) — long; при переполнении — MathArithmeticException
public long factorialUsingApacheCommons(int n) {
    return CombinatoricsUtils.factorial(n);
}
```

## Подход 5: Guava

`BigIntegerMath.factorial(n)` возвращает `BigInteger` — подходит для больших n. Зависимость `guava`.

```xml
<dependency>
    <groupId>com.google.guava</groupId>
    <artifactId>guava</artifactId>
    <version>31.0.1-jre</version>
</dependency>
```

```java
public BigInteger factorialUsingGuava(int n) {
    return BigIntegerMath.factorial(n);
}
```

## Реализация на Kotlin

```kotlin
// Цикл, fold, рекурсия, tailrec, BigInteger, мемоизация
fun factorialUsingForLoopK(n: Int): Long {
    if (n < 0) {
        throw IllegalArgumentException("Factorial is not defined for negative numbers")
    }
    if (n > 20) { // Max value for Long
        throw IllegalArgumentException("Result will overflow Long type")
    }
    var fact: Long = 1
    for (i in 2..n) {
        fact *= i
    }
    return fact
}
```

### Подход 2: Функциональный стиль

```kotlin
fun factorialUsingFoldK(n: Int): Long {
    if (n < 0 || n > 20) {
        throw IllegalArgumentException("n must be between 0 and 20")
    }
    return (1..n).fold(1L) { acc, i -> acc * i }
}
```

### Подход 3: Рекурсия

```kotlin
fun factorialUsingRecursionK(n: Int): Long {
    if (n < 0) {
        throw IllegalArgumentException("Factorial is not defined for negative numbers")
    }
    if (n == 0 || n == 1) {
        return 1
    }
    return n * factorialUsingRecursionK(n - 1)
}
```

### Подход 4: Хвостовая рекурсия

```kotlin
tailrec fun factorialTailRecursiveK(acc: Long = 1, n: Int): Long {
    return if (n <= 1) acc else factorialTailRecursiveK(acc * n, n - 1)
}

// Использование
fun factorialK(n: Int): Long = factorialTailRecursiveK(1, n)
```

### Подход 5: Использование BigInteger

```kotlin
import java.math.BigInteger

fun factorialUsingBigIntegerK(n: Int): BigInteger {
    if (n < 0) {
        throw IllegalArgumentException("Factorial is not defined for negative numbers")
    }
    if (n == 0 || n == 1) {
        return BigInteger.ONE
    }
    var result = BigInteger.ONE
    for (i in 2..n) {
        result = result.multiply(BigInteger.valueOf(i.toLong()))
    }
    return result
}
```

### Подход 6: Функциональный стиль с BigInteger

```kotlin
fun factorialUsingBigIntegerFoldK(n: Int): BigInteger {
    if (n < 0) {
        throw IllegalArgumentException("Factorial is not defined for negative numbers")
    }
    return (1..n).fold(BigInteger.ONE) { acc, i -> 
        acc.multiply(BigInteger.valueOf(i.toLong())) 
    }
}
```

### Подход 7: Мемоизация

```kotlin
val factorialCache = mutableMapOf<Int, BigInteger>()

fun factorialMemoizedK(n: Int): BigInteger {
    if (n < 0) {
        throw IllegalArgumentException("Factorial is not defined for negative numbers")
    }
    if (n == 0 || n == 1) {
        return BigInteger.ONE
    }
    return factorialCache.getOrPut(n) {
        BigInteger.valueOf(n.toLong()).multiply(factorialMemoizedK(n - 1))
    }
}
```

### Пример использования

```kotlin
fun main() {
    // Для небольших чисел
    println(factorialUsingForLoopK(5)) // 120
    println(factorialK(5)) // 120
    
    // Для больших чисел
    println(factorialUsingBigIntegerK(25)) // 15511210043330985984000000
    println(factorialMemoizedK(25)) // 15511210043330985984000000
}
```

## Работа с большими числами

Для n > 20 результат не помещается в `long`. Используйте `BigInteger`: умножение в цикле или Guava `BigIntegerMath.factorial(n)`.

```java
// BigInteger для n > 20
public BigInteger factorialHavingLargeResult(int n) {
    BigInteger result = BigInteger.ONE;
    for (int i = 2; i <= n; i++) {
        result = result.multiply(BigInteger.valueOf(i));
    }
    return result;
}
```

### Оптимизированная версия с BigInteger

С проверкой n < 0 и базового случая 0/1.

```java
public BigInteger factorialBigIntegerOptimized(int n) {
    if (n < 0) {
        throw new IllegalArgumentException("Factorial is not defined for negative numbers");
    }
    
    if (n == 0 || n == 1) {
        return BigInteger.ONE;
    }
    
    BigInteger result = BigInteger.ONE;
    for (int i = 2; i <= n; i++) {
        result = result.multiply(BigInteger.valueOf(i));
    }
    return result;
}
```

## Сравнение подходов

| Подход | Максимальное n (long) | С BigInteger | Простота | Производительность |
|--------|----------------------|--------------|----------|-------------------|
| Цикл for | 20 | да | Высокая | Высокая |
| Stream API | 20 | да | Средняя | Средняя |
| Рекурсия | 20 | ограничено стеком | Высокая | Низкая |
| Apache Commons | 20 | нет | Высокая | Высокая |
| Guava | — | неограничено | Высокая | Высокая |

## Сложность

Время O(n) — n умножений. Память: цикл и Stream O(1); рекурсия O(n) из-за стека; результат в `BigInteger` занимает O(n log n) бит.

## Особенности

В `long` помещается только n! для n ≤ 20. Для n > 20 нужен `BigInteger` или библиотека (Guava). Итеративный цикл быстрее рекурсии.

## Применение

Факториал используется в комбинаторике, теории вероятностей, при подсчёте перестановок и комбинаций и в научных расчётах.

## Варианты задачи

### Вариант 1: Двойной факториал

n!! = n × (n−2) × … (шаг 2); для чётных и нечётных n.

```java
public long doubleFactorial(int n) {
    if (n <= 1) {
        return 1;
    }
    return n * doubleFactorial(n - 2);
}
```

### Вариант 2: Факториал с мемоизацией

```java
private static final Map<Integer, BigInteger> FACTORIAL_CACHE = new ConcurrentHashMap<>();

public BigInteger factorialMemoized(int n) {
    if (n < 0) {
        throw new IllegalArgumentException("Factorial is not defined for negative numbers");
    }
    
    if (n == 0 || n == 1) {
        return BigInteger.ONE;
    }
    
    return FACTORIAL_CACHE.computeIfAbsent(n, k -> 
        BigInteger.valueOf(k).multiply(factorialMemoized(k - 1))
    );
}
```

### Вариант 3: Логарифм факториала

```java
public double logFactorial(int n) {
    double sum = 0.0;
    for (int i = 2; i <= n; i++) {
        sum += Math.log(i);
    }
    return sum;
}
```

## Когда использовать

Цикл или Stream — для n ≤ 20 и максимальной простоты. Для n > 20 используйте `BigInteger` (свой цикл или Guava `BigIntegerMath.factorial`). Apache Commons подходит для n ≤ 20 без переполнения long. Рекурсию лучше не использовать для больших n из-за стека.

## Лучшие практики

Для отрицательных n бросайте `IllegalArgumentException`. Для методов, возвращающих `long`, проверяйте n ≤ 20 или документируйте ограничение. При n > 20 используйте `BigInteger` или Guava. Итеративный цикл быстрее рекурсии. В тестах проверяйте 0, 1, типичные n, границу 20 и отрицательные (ожидание исключения).

## Решение проблем

| Симптом | Возможная причина | Решение |
|---------|-------------------|---------|
| Неверный или отрицательный результат при n > 20 | Переполнение long | Использовать BigInteger или проверку n ≤ 20 с исключением |
| StackOverflowError при рекурсии | Слишком большое n | Использовать итеративный цикл или tail-rec с BigInteger |
| MathArithmeticException в Commons | Результат > Long.MAX_VALUE | Проверять n ≤ 20 или использовать Guava/BigInteger |

## Частые вопросы

**Почему 0! = 1?** По определению для согласованности формул (например, число сочетаний C(n,0) = 1) и рекуррентного соотношения n! = n·(n−1)! при n = 1.

**До какого n помещается факториал в long?** 20! = 2 432 902 008 176 640 000 ≤ Long.MAX_VALUE; 21! уже больше.

**Когда использовать Guava вместо своего BigInteger-цикла?** Guava даёт оптимизированную и проверенную реализацию; свой цикл проще без зависимостей. Для продакшена часто выбирают Guava.

## Заключение

В документе описаны способы вычисления факториала в `Java` и Kotlin: цикл, Stream API, рекурсия, Apache Commons Math, Guava и работа с `BigInteger`. Для n ≤ 20 достаточно `long`; для больших n — `BigInteger` или Guava.
