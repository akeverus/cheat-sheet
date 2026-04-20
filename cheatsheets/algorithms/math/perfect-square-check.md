---
title: "Проверка полного квадрата (Perfect Square Check)"
description: "Проверка, является ли целое число полным квадратом (n = k² для целого k). В документе описаны подходы: Math.sqrt(), бинарный поиск, метод Ньютона и быстрые отсевы по последним цифрам/битам."
tags:
  - algorithms
  - math
  - perfect-square-check
difficulty: "intermediate"
prerequisites: []
next: []
updated: "2026-04-20"
---
# Проверка полного квадрата (Perfect Square Check)

Проверка, является ли целое число полным квадратом (n = k² для целого k). В документе описаны подходы: `Math.sqrt()`, бинарный поиск, метод Ньютона и быстрые отсевы по последним цифрам/битам.

## Полезные ссылки

### Официальная документация
- [GeeksforGeeks: Check if given number is perfect square](https://www.geeksforgeeks.org/check-if-given-number-is-perfect-square-in-cpp/)

### См. также
- [[greatest-common-divisor|Наибольший общий делитель]] — НОД
- [[factorial-calculation|Вычисление факториала]] — факториал

- [[line-intersection|Пересечение прямых (Line Intersection)]]
- [[circle-area-calculation|Вычисление площади круга (Circle Area Calculation)]]
- [[coprime-numbers|Взаимно простые числа (Coprime Numbers)]]
## Содержание

- [Описание алгоритма](#описание-алгоритма)
  - [Примеры](#примеры)
  - [Диапазон чисел](#диапазон-чисел)
- [Реализация на Java](#реализация-на-java)
  - [Подход 1: Использование Math.sqrt()](#подход-1-использование-mathsqrt)
  - [Альтернативная версия](#альтернативная-версия)
- [Подход 2: Бинарный поиск](#подход-2-бинарный-поиск)
  - [Итеративная версия](#итеративная-версия)
  - [Оптимизированный бинарный поиск](#оптимизированный-бинарный-поиск)
- [Подход 3: Метод Ньютона](#подход-3-метод-ньютона)
  - [Улучшенная версия](#улучшенная-версия)
- [Оптимизации](#оптимизации)
  - [Дополнительные оптимизации](#дополнительные-оптимизации)
- [Реализация на Kotlin](#реализация-на-kotlin)
  - [Подход 2: Бинарный поиск](#подход-2-бинарный-поиск-1)
  - [Подход 3: Метод Ньютона](#подход-3-метод-ньютона-1)
  - [Пример использования](#пример-использования)
- [Сравнение подходов](#сравнение-подходов)
- [Сложность](#сложность)
- [Особенности](#особенности)
- [Применение](#применение)
- [Варианты задачи](#варианты-задачи)
  - [Вариант 1: Найти ближайший полный квадрат](#вариант-1-найти-ближайший-полный-квадрат)
  - [Вариант 2: Список полных квадратов до N](#вариант-2-список-полных-квадратов-до-n)
- [Когда использовать](#когда-использовать)
- [Лучшие практики](#лучшие-практики)
- [Решение проблем](#решение-проблем)
- [Частые вопросы](#частые-вопросы)
- [Заключение](#заключение)

## Описание алгоритма

Полный квадрат — это число, которое можно представить как произведение двух равных целых чисел.

Ниже — несколько способов проверки в `Java`; для больших чисел используется тип `long`.

### Примеры

4 = 2×2 и 16 = 4×4 — полные квадраты; 10 — нет.

### Диапазон чисел

Используем `long` (64 бита); для полных квадратов рассматриваем неотрицательные числа. Квадратный корень из n лежит в диапазоне до ~2^31.5.

## Реализация на Java

### Подход 1: Использование Math.sqrt()

Вычисляем sqrt(n), округляем до long и проверяем, что квадрат совпадает с n. Для n ≤ 0 возвращаем false. Из-за точности double иногда добавляют 0.5 перед приведением.

```java
// sqrt(n) → long; проверка tst*tst == n; для n<=0 — false
public static boolean isPerfectSquareByUsingSqrt(long n) {
    if (n <= 0) {
        return false;
    }

    double squareRoot = Math.sqrt(n);
    long tst = (long)(squareRoot + 0.5);
    return tst * tst == n;
}
```

### Альтернативная версия

Без добавления 0.5: приведение к long и сравнение sqrt*sqrt == n.

```java
public static boolean isPerfectSquareSqrt(long n) {
    if (n < 0) {
        return false;
    }
    long sqrt = (long) Math.sqrt(n);
    return sqrt * sqrt == n;
}
```

## Подход 2: Бинарный поиск

Корень из n в диапазоне [1, n]; бинарный поиск за O(log n) итераций без вызова `Math.sqrt`.

```java
// Рекурсивный бинарный поиск: mid*mid == n
public boolean isPerfectSquareByUsingBinarySearch(long low, long high, long number) {
    long check = (low + high) / 2L;

    if (high < low) {
        return false;
    }

    if (number == check * check) {
        return true;
    } else if (number < check * check) {
        high = check - 1L;
        return isPerfectSquareByUsingBinarySearch(low, high, number);
    } else {
        low = check + 1L;
        return isPerfectSquareByUsingBinarySearch(low, high, number);
    }
}
```

### Итеративная версия

```java
// Итеративный бинарный поиск по [1, number]
public boolean isPerfectSquareBinarySearchIterative(long number) {
    if (number < 0) {
        return false;
    }
    if (number == 0 || number == 1) {
        return true;
    }

    long low = 1;
    long high = number;

    while (low <= high) {
        long mid = (low + high) / 2;
        long square = mid * mid;

        if (square == number) {
            return true;
        } else if (square < number) {
            low = mid + 1;
        } else {
            high = mid - 1;
        }
    }

    return false;
}
```

### Оптимизированный бинарный поиск

Чтобы улучшить бинарный поиск, мы можем заметить, что если мы определяем количество цифр основного числа, это дает нам диапазон корня.

Например, если число состоит только из одной цифры, то диапазон квадратного корня находится между 1 и 4. Причина в том, что максимальное целое число из одной цифры равно 9, а его корень равен 3. Кроме того, если число состоит из двух цифр, диапазон от 4 до 10 и так далее.

Таблица диапазонов по числу цифр уменьшает границы бинарного поиска.

```java
// Диапазон [low, high] по количеству цифр числа
public class BinarySearchRange {
    private long low;
    private long high;

    public BinarySearchRange(long low, long high) {
        this.low = low;
        this.high = high;
    }

    // Getters
    public long getLow() { return low; }
    public long getHigh() { return high; }
}

private static List<BinarySearchRange> lookupTable = new ArrayList<>();

private static void initiateOptimizedBinarySearchLookupTable() {
    lookupTable.add(new BinarySearchRange(0, 0));
    lookupTable.add(new BinarySearchRange(1L, 4L));
    lookupTable.add(new BinarySearchRange(3L, 10L));

    for (int i = 3; i < 20; i++) {
        lookupTable.add(new BinarySearchRange(
            lookupTable.get(i - 2).getLow() * 10,
            lookupTable.get(i - 2).getHigh() * 10
        ));
    }
}

public boolean isPerfectSquareByUsingOptimizedBinarySearch(long number) {
    int numberOfDigits = Long.toString(number).length();
    return isPerfectSquareByUsingBinarySearch(
        lookupTable.get(numberOfDigits).getLow(),
        lookupTable.get(numberOfDigits).getHigh(),
        number
    );
}
```

## Подход 3: Метод Ньютона

Итерация x := (x + n/x)/2 сходится к √n. Останавливаемся, когда x² ≤ n; проверяем x*x == n.

```java
// Итерация Ньютона для целочисленного корня
public static boolean isPerfectSquareByUsingNewtonMethod(long n) {
    long x1 = n;
    long x2 = 1L;

    while (x1 > x2) {
        x1 = (x1 + x2) / 2L;
        x2 = n / x1;
    }

    return x1 == x2 && n % x1 == 0L;
}
```

### Улучшенная версия

```java
public static boolean isPerfectSquareNewton(long n) {
    if (n < 0) {
        return false;
    }
    if (n == 0 || n == 1) {
        return true;
    }

    long x = n;
    while (x * x > n) {
        x = (x + n / x) / 2;
    }

    return x * x == n;
}
```

## Оптимизации

Как мы уже говорили, существует несколько алгоритмов проверки квадратных корней целого числа. Тем не менее, мы всегда можем оптимизировать любой алгоритм, используя некоторые приемы.

Хитрости должны избегать выполнения основных операций, которые будут определять квадратный корень. Например, мы можем исключить отрицательные числа напрямую.

В системе с основанием 16 полный квадрат может заканчиваться только на 0, 1, 4 или 9. Быстрая проверка (n & 0xF) отсекает остальные случаи.

```java
// Быстрый отсев по (n & 0xF); затем sqrt
public static boolean isPerfectSquareWithOptimization(long n) {
    if (n < 0) {
        return false;
    }

    switch((int)(n & 0xF)) {
        case 0: case 1: case 4: case 9:
            long tst = (long)Math.sqrt(n);
            return tst * tst == n;
        default:
            return false;
    }
}
```

### Дополнительные оптимизации

```java
public static boolean isPerfectSquareOptimized(long n) {
    if (n < 0) {
        return false;
    }

    // Быстрая проверка последней цифры в десятичной системе
    int lastDigit = (int)(n % 10);
    if (lastDigit != 0 && lastDigit != 1 && lastDigit != 4 &&
        lastDigit != 5 && lastDigit != 6 && lastDigit != 9) {
        return false;
    }

    // Проверка в шестнадцатеричной системе
    if ((n & 0xF) > 9) {
        return false;
    }

    long sqrt = (long) Math.sqrt(n);
    return sqrt * sqrt == n;
}
```

## Реализация на Kotlin

```kotlin
// sqrt, бинарный поиск и Ньютон в Kotlin
fun isPerfectSquareByUsingSqrtK(n: Long): Boolean {
    if (n <= 0) {
        return false
    }
    val squareRoot = Math.sqrt(n.toDouble())
    val tst = (squareRoot + 0.5).toLong()
    return tst * tst == n
}

fun isPerfectSquareSqrtK(n: Long): Boolean {
    if (n < 0) {
        return false
    }
    val sqrt = Math.sqrt(n.toDouble()).toLong()
    return sqrt * sqrt == n
}
```

### Подход 2: Бинарный поиск

```kotlin
fun isPerfectSquareBinarySearchIterativeK(number: Long): Boolean {
    if (number < 0) {
        return false
    }
    if (number == 0L || number == 1L) {
        return true
    }

    var low = 1L
    var high = number

    while (low <= high) {
        val mid = (low + high) / 2
        val square = mid * mid

        when {
            square == number -> return true
            square < number -> low = mid + 1
            else -> high = mid - 1
        }
    }

    return false
}
```

### Подход 3: Метод Ньютона

```kotlin
fun isPerfectSquareByUsingNewtonMethodK(n: Long): Boolean {
    if (n < 0) {
        return false
    }
    var x = n
    while (x * x > n) {
        x = (x + n / x) / 2
    }
    return x * x == n
}
```

### Пример использования

```kotlin
fun main() {
    println(isPerfectSquareSqrtK(16)) // true
    println(isPerfectSquareSqrtK(10)) // false
    println(isPerfectSquareBinarySearchIterativeK(25)) // true
}
```

## Сравнение подходов

| Подход | Временная сложность | Пространственная сложность | Когда использовать |
|--------|---------------------|----------------------------|-------------------|
| Math.sqrt() | O(1) | O(1) | Рекомендуется |
| Бинарный поиск | O(log n) | O(1) | Без Math.sqrt |
| Метод Ньютона | O(log log n) | O(1) | Быстрая сходимость |
| С оптимизацией | O(1) | O(1) | Множественные проверки |

## Сложность

`Math.sqrt` — O(1) по времени (аппроксимация). Бинарный поиск — O(log n). Метод Ньютона сходится за O(log log n). Память O(1) для всех вариантов.

## Особенности

`Math.sqrt` обычно самый быстрый на практике. Все подходы дают точный результат для целых чисел. Быстрые отсевы (последняя цифра, n & 0xF) отбрасывают многие не-квадраты до основного расчёта.

## Применение

Проверка полного квадрата используется в математике, криптографии, теории чисел и при оптимизации (например, размер буфера, размер сетки).

## Варианты задачи

### Вариант 1: Найти ближайший полный квадрат

Ближайший квадрат: floor(√n)² и (floor(√n)+1)²; выбираем тот, что ближе к n.

```java
public long findNearestPerfectSquare(long n) {
    if (n < 0) {
        return 0;
    }

    long sqrt = (long) Math.sqrt(n);
    long lower = sqrt * sqrt;
    long upper = (sqrt + 1) * (sqrt + 1);

    return (n - lower < upper - n) ? lower : upper;
}
```

### Вариант 2: Список полных квадратов до N

```java
public List<Long> listPerfectSquares(long n) {
    List<Long> squares = new ArrayList<>();
    for (long i = 1; i * i <= n; i++) {
        squares.add(i * i);
    }
    return squares;
}
```

## Когда использовать

Используйте `Math.sqrt()` в большинстве случаев — просто и быстро. Бинарный поиск или метод Ньютона — когда нельзя вызывать `sqrt` или нужна целочисленная арифметика без `double`. Оптимизации с отсевом по последней цифре или (n & 0xF) полезны при массовой проверке.

## Лучшие практики

Для n ≤ 0 возвращайте false. При использовании `Math.sqrt` учитывайте точность double: округление + проверка sqrt*sqrt == n надёжнее прямого сравнения. Быстрая проверка (n & 0xF) или по последней цифре отсекает многие не-квадраты. В тестах проверяйте 0, 1, 4, 9, 16, не-квадраты и границы long.

## Решение проблем

| Симптом | Возможная причина | Решение |
|---------|-------------------|---------|
| true для не-квадрата | Ошибка округления double при приведении sqrt к long | Добавить 0.5 перед (long) или использовать целочисленный метод (бинарный поиск, Ньютон) |
| false для полного квадрата | Переполнение при mid*mid или sqrt*sqrt | Использовать long для всех промежуточных произведений; для очень больших n — BigInteger |
| Медленно при массовой проверке | Вызов sqrt для каждого числа | Добавить быстрый отсев (n & 0xF) или по последней цифре в десятичной системе |

## Частые вопросы

**Почему полный квадрат по модулю 16 может быть только 0, 1, 4, 9?** Квадраты по mod 16 исчерпываются этими остатками; остальные значения не могут быть квадратами целого числа.

**Когда Math.sqrt даёт неверный результат для целого n?** При очень больших n точность double может быть недостаточной; тогда предпочтительны целочисленный бинарный поиск или метод Ньютона.

**Что возвращать для n = 0?** По определению 0 = 0² — полный квадрат; часто возвращают true. Для отрицательных — false.

## Заключение

В документе рассмотрены способы проверки полного квадрата в `Java` и Kotlin: через `Math.sqrt`, бинарный поиск и метод Ньютона. Для ускорения при массовых проверках приведены отсевы по последней цифре и по (n & 0xF).
