---
title: "Наименьшее общее кратное (Least Common Multiple)"
description: "Наименьшее общее кратное (НОК) двух ненулевых целых чисел — наименьшее положительное целое, делящееся на оба. В документе описаны три подхода: итеративный перебор кратных, факторизация на простые и формула через НОД (рекомендуется)."
tags:
  - algorithms
  - math
  - least-common-multiple
difficulty: "intermediate"
prerequisites: []
next: []
updated: "2026-04-20"
---
# Наименьшее общее кратное (Least Common Multiple)

Наименьшее общее кратное (НОК) двух ненулевых целых чисел — наименьшее положительное целое, делящееся на оба. В документе описаны три подхода: итеративный перебор кратных, факторизация на простые и формула через НОД (рекомендуется).

## Полезные ссылки

### Официальная документация
- [GeeksforGeeks: Program to find LCM](https://www.geeksforgeeks.org/program-to-find-lcm-of-two-numbers/)

### См. также
- [[greatest-common-divisor|Наибольший общий делитель]] — НОД
- [[factorial-calculation|Вычисление факториала]] — факториал

- [[line-intersection|Пересечение прямых (Line Intersection)]]
- [[circle-area-calculation|Вычисление площади круга (Circle Area Calculation)]]
- [[coprime-numbers|Взаимно простые числа (Coprime Numbers)]]
## Содержание

- [Описание алгоритма](#описание-алгоритма)
  - [Математическое определение](#математическое-определение)
  - [Примеры](#примеры)
- [Реализация на Java](#реализация-на-java)
  - [Подход 1: Итеративный метод (Java)](#подход-1-итеративный-метод-java)
  - [Алгоритм](#алгоритм)
  - [Пример](#пример)
  - [Реализация](#реализация)
  - [Подход 2: Простая факторизация (Java)](#подход-2-простая-факторизация-java)
  - [Реализация](#реализация-1)
  - [Подход 3: Использование НОД](#подход-3-использование-нод)
  - [Реализация алгоритма Евклида](#реализация-алгоритма-евклида)
- [Реализация на Kotlin](#реализация-на-kotlin)
  - [Подход 1: Итеративный метод (Kotlin)](#подход-1-итеративный-метод-kotlin)
  - [Подход 2: Простая факторизация (Kotlin)](#подход-2-простая-факторизация-kotlin)
  - [Подход 3: Использование НОД (рекомендуется)](#подход-3-использование-нод-рекомендуется)
  - [Хвостовая рекурсия для НОД](#хвостовая-рекурсия-для-нод)
  - [НОК для массива чисел](#нок-для-массива-чисел)
  - [Работа с BigInteger](#работа-с-biginteger)
  - [Пример использования](#пример-использования)
- [Работа с большими числами](#работа-с-большими-числами)
- [Сравнение подходов](#сравнение-подходов)
- [Сложность](#сложность)
- [Особенности](#особенности)
- [Применение](#применение)
- [Варианты задачи](#варианты-задачи)
  - [Вариант 1: НОК для массива чисел](#вариант-1-нок-для-массива-чисел)
  - [Вариант 2: НОК с использованием BigInteger](#вариант-2-нок-с-использованием-biginteger)
- [Когда использовать](#когда-использовать)
- [Лучшие практики](#лучшие-практики)
- [Решение проблем](#решение-проблем)
- [Частые вопросы](#частые-вопросы)
- [Заключение](#заключение)

## Описание алгоритма

Наименьшее общее кратное (НОК) двух ненулевых целых чисел a, b — наименьшее положительное целое, делящееся на оба. Отрицательные числа и ноль не рассматриваются как кандидаты для НОК; для них часто полагают НОК = 0 по соглашению.

### Математическое определение

НОК двух чисел a и b выражается через их НОД:

```text
НОК(a, b) = |a × b| / НОД(a, b)
```

### Примеры

НОК(12, 18) = 36, НОК(4, 6) = 12, НОК(5, 7) = 35.

## Реализация на Java

### Подход 1: Итеративный метод (Java)

Мы можем найти НОК двух чисел, используя тот простой факт, что умножение есть многократное сложение.

Простой алгоритм нахождения НОК представляет собой итеративный подход, использующий несколько фундаментальных свойств НОК двух чисел.

Во-первых, мы знаем, что НОК любого числа с нулем сам по себе равен нулю. Таким образом, мы можем досрочно выйти из процедуры, когда любое из заданных целых чисел равно 0.

Во-вторых, мы также можем использовать тот факт, что нижняя граница НОК двух ненулевых целых чисел равна большему из абсолютных значений двух чисел.

Более того, как объяснялось ранее, НОК никогда не может быть отрицательным целым числом. Итак, мы будем использовать только абсолютные значения целых чисел для нахождения возможных кратных, пока не найдем общее кратное.

### Алгоритм

Алгоритм: если a = 0 или b = 0, вернуть 0. Иначе вычислить абсолютные значения, принять lcm = max(|a|, |b|) и в цикле увеличивать lcm на это большее значение, пока lcm не станет делиться на меньшее — тогда вернуть lcm.

### Пример

Для lcm(12, 18): lcm = 18, не делится на 12 36, делится ответ 36.

### Реализация

Реализация на `Java`: используем `Math.abs`, `Math.max`, `Math.min`; перебираем кратные большего числа.

```java
// Итеративный НОК: перебор кратных большего числа до первого делящегося на меньшее
public static int lcm(int number1, int number2) {
    if (number1 == 0 || number2 == 0) {
        return 0;
    }

    int absNumber1 = Math.abs(number1);
    int absNumber2 = Math.abs(number2);
    int absHigherNumber = Math.max(absNumber1, absNumber2);
    int absLowerNumber = Math.min(absNumber1, absNumber2);
    int lcm = absHigherNumber;

    while (lcm % absLowerNumber != 0) {
        lcm += absHigherNumber;
    }

    return lcm;
}
```

Тест: `lcm(12, 18)` должен быть 36.

```java
@Test
public void testLCM() {
    Assert.assertEquals(36, lcm(12, 18));
}
```

### Подход 2: Простая факторизация (Java)

Фундаментальная теорема арифметики утверждает, что можно однозначно выразить каждое целое число больше единицы как произведение степеней простых чисел.

Для N > 1: N = 2^k1 · 3^k2 · 5^k3 · … НОК получается из объединения простых разложений: для каждого простого берётся максимальная степень. Пример: 12 = 2²·3¹, 18 = 2¹·3² НОК = 2²·3² = 36.

### Реализация

Метод `getPrimeFactors` возвращает отображение «простой множитель степень»; НОК считается по максимальным степеням для каждого простого.

```java
// Разложение на простые; результат — Map<множитель, степень>
public static Map<Integer, Integer> getPrimeFactors(int number) {
    int absNumber = Math.abs(number);
    Map<Integer, Integer> primeFactorsMap = new HashMap<Integer, Integer>();

    for (int factor = 2; factor <= absNumber; factor++) {
        while (absNumber % factor == 0) {
            Integer power = primeFactorsMap.get(factor);
            if (power == null) {
                power = 0;
            }
            primeFactorsMap.put(factor, power + 1);
            absNumber /= factor;
        }
    }

    return primeFactorsMap;
}
```

НОК по факторизациям: объединение ключей, для каждого простого берём максимум степеней, перемножаем.

```java
// НОК через простые множители: max степеней по каждому простому
public static int lcm(int number1, int number2) {
    if (number1 == 0 || number2 == 0) {
        return 0;
    }

    Map<Integer, Integer> primeFactorsForNum1 = getPrimeFactors(number1);
    Map<Integer, Integer> primeFactorsForNum2 = getPrimeFactors(number2);
    Set<Integer> primeFactorsUnionSet = new HashSet<>(primeFactorsForNum1.keySet());
    primeFactorsUnionSet.addAll(primeFactorsForNum2.keySet());

    int lcm = 1;
    for (Integer primeFactor : primeFactorsUnionSet) {
        lcm *= Math.pow(primeFactor,
            Math.max(primeFactorsForNum1.getOrDefault(primeFactor, 0),
                     primeFactorsForNum2.getOrDefault(primeFactor, 0)));
    }

    return lcm;
}
```

### Подход 3: Использование НОД

Связь НОК и НОД: |a·b| = НОД(a,b)·НОК(a,b), поэтому НОК(a,b) = |a·b|/НОД(a,b). Задача сводится к вычислению НОД алгоритмом Евклида. Пример: НОД(12,18)=6 НОК(12,18)=12·18/6=36.

### Реализация алгоритма Евклида

Рекурсия: НОД(a,b)=НОД(b, a mod b), базовый случай — ноль.

```java
// Рекурсивный НОД для использования в формуле НОК
public static int gcd(int number1, int number2) {
    if (number1 == 0 || number2 == 0) {
        return number1 + number2;
    } else {
        int absNumber1 = Math.abs(number1);
        int absNumber2 = Math.abs(number2);
        int biggerValue = Math.max(absNumber1, absNumber2);
        int smallerValue = Math.min(absNumber1, absNumber2);
        return gcd(biggerValue % smallerValue, smallerValue);
    }
}
```

НОК по формуле: |number1·number2|/НОД.

```java
// НОК через НОД — предпочтительный способ
public static int lcm(int number1, int number2) {
    if (number1 == 0 || number2 == 0) {
        return 0;
    } else {
        int gcd = gcd(number1, number2);
        return Math.abs(number1 * number2) / gcd;
    }
}
```

## Реализация на Kotlin

### Подход 1: Итеративный метод (Kotlin)

```kotlin
// Итеративный НОК (аналог Java-версии)
fun lcmIterativeK(number1: Int, number2: Int): Int {
    if (number1 == 0 || number2 == 0) {
        return 0
    }

    val absNumber1 = Math.abs(number1)
    val absNumber2 = Math.abs(number2)
    val absHigherNumber = maxOf(absNumber1, absNumber2)
    val absLowerNumber = minOf(absNumber1, absNumber2)
    var lcm = absHigherNumber

    while (lcm % absLowerNumber != 0) {
        lcm += absHigherNumber
    }

    return lcm
}
```

### Подход 2: Простая факторизация (Kotlin)

```kotlin
// Разложение на простые и НОК по ним
fun getPrimeFactorsK(number: Int): Map<Int, Int> {
    var absNumber = Math.abs(number)
    val primeFactorsMap = mutableMapOf<Int, Int>()

    var factor = 2
    while (factor <= absNumber) {
        while (absNumber % factor == 0) {
            primeFactorsMap[factor] = primeFactorsMap.getOrDefault(factor, 0) + 1
            absNumber /= factor
        }
        factor++
    }

    return primeFactorsMap
}

fun lcmByPrimeFactorsK(number1: Int, number2: Int): Int {
    if (number1 == 0 || number2 == 0) {
        return 0
    }

    val primeFactorsForNum1 = getPrimeFactorsK(number1)
    val primeFactorsForNum2 = getPrimeFactorsK(number2)
    val primeFactorsUnionSet = (primeFactorsForNum1.keys + primeFactorsForNum2.keys).toSet()

    var lcm = 1
    for (primeFactor in primeFactorsUnionSet) {
        val power = maxOf(
            primeFactorsForNum1.getOrDefault(primeFactor, 0),
            primeFactorsForNum2.getOrDefault(primeFactor, 0)
        )
        lcm *= Math.pow(primeFactor.toDouble(), power.toDouble()).toInt()
    }

    return lcm
}
```

### Подход 3: Использование НОД (рекомендуется)

```kotlin
// НОД и НОК через формулу |a·b|/НОД
fun gcdK(number1: Int, number2: Int): Int {
    if (number1 == 0 || number2 == 0) {
        return number1 + number2
    } else {
        val absNumber1 = Math.abs(number1)
        val absNumber2 = Math.abs(number2)
        val biggerValue = maxOf(absNumber1, absNumber2)
        val smallerValue = minOf(absNumber1, absNumber2)
        return gcdK(biggerValue % smallerValue, smallerValue)
    }
}

fun lcmByGcdK(number1: Int, number2: Int): Int {
    if (number1 == 0 || number2 == 0) {
        return 0
    } else {
        val gcd = gcdK(number1, number2)
        return Math.abs(number1 * number2) / gcd
    }
}
```

### Хвостовая рекурсия для НОД

```kotlin
tailrec fun gcdTailRecursiveK(a: Int, b: Int): Int {
    if (b == 0) return Math.abs(a)
    return gcdTailRecursiveK(b, a % b)
}

fun lcmByGcdTailRecursiveK(number1: Int, number2: Int): Int {
    if (number1 == 0 || number2 == 0) {
        return 0
    }
    val gcd = gcdTailRecursiveK(number1, number2)
    return Math.abs(number1 * number2) / gcd
}
```

### НОК для массива чисел

```kotlin
fun lcmArrayK(numbers: IntArray): Int {
    var result = numbers[0]
    for (i in 1 until numbers.size) {
        result = lcmByGcdK(result, numbers[i])
    }
    return result
}
```

### Работа с BigInteger

```kotlin
// НОК для произвольно больших чисел
import java.math.BigInteger

fun lcmBigIntegerK(a: BigInteger, b: BigInteger): BigInteger {
    val gcd = a.gcd(b)
    return a.multiply(b).abs().divide(gcd)
}
```

### Пример использования

```kotlin
fun main() {
    // Итеративный метод
    println(lcmIterativeK(12, 18)) // 36

    // Использование НОД (рекомендуется)
    println(lcmByGcdK(12, 18)) // 36
    println(lcmByGcdTailRecursiveK(4, 6)) // 12

    // Для массива
    println(lcmArrayK(intArrayOf(12, 18, 24))) // 72

    // BigInteger
    val a = BigInteger("12345678901234567890")
    val b = BigInteger("98765432109876543210")
    println(lcmBigIntegerK(a, b))
}
```

## Работа с большими числами

Для чисел вне диапазона `long` используйте `BigInteger`. У него есть метод `gcd()` (внутри — Евклид и Binary GCD). НОК по той же формуле: |a·b|/НОД.

```java
// НОК для BigInteger
public static BigInteger lcm(BigInteger number1, BigInteger number2) {
    BigInteger gcd = number1.gcd(number2);
    BigInteger absProduct = number1.multiply(number2).abs();
    return absProduct.divide(gcd);
}
```

## Сравнение подходов

| Подход | Временная сложность | Пространственная сложность | Когда использовать |
|--------|---------------------|----------------------------|-------------------|
| Итеративный | O(a·b) в худшем случае | O(1) | Малые числа |
| Простая факторизация | O(n log n) | O(log n) | Средние числа, нужны множители |
| Использование НОД | O(log(min(a,b))) | O(1) | Рекомендуется |

## Сложность

Итеративный метод в худшем случае O(a·b) по времени при O(1) памяти. Факторизация — O(n log n) по времени и O(log n) по памяти. Метод через НОД даёт O(log(min(a,b))) по времени и O(1) по памяти благодаря алгоритму Евклида.

## Особенности

Наиболее эффективен расчёт через НОД; итеративный способ самый наглядный. На практике почти всегда используют формулу с НОД.

## Применение

НОК применяется при приведении дробей к общему знаменателю, в задачах синхронизации (периоды событий), в теории чисел и в ряде алгоритмов.

## Варианты задачи

### Вариант 1: НОК для массива чисел

НОК нескольких чисел: последовательно НОК(результат, next).

```java
// НОК массива: lcm(lcm(...lcm(n0,n1), n2), ...)
public static int lcmArray(int[] numbers) {
    int result = numbers[0];
    for (int i = 1; i < numbers.length; i++) {
        result = lcm(result, numbers[i]);
    }
    return result;
}
```

### Вариант 2: НОК с использованием BigInteger

```java
// Для чисел вне диапазона long
public static BigInteger lcmBigInteger(BigInteger a, BigInteger b) {
    return a.multiply(b).abs().divide(a.gcd(b));
}
```

## Когда использовать

Итеративный метод уместен только для очень маленьких чисел и когда важна максимальная простота. Факторизация полезна, если нужны сами простые множители. В остальных случаях предпочтителен расчёт через НОД — он быстрый и без переполнения при использовании формулы до деления (сначала НОД, затем деление произведения).

## Лучшие практики

При a=0 или b=0 считаем НОК равным 0; для отрицательных используйте абсолютные значения в формуле. Формула НОК(a,b)=|a·b|/НОД(a,b) предпочтительнее итеративного перебора. При риске переполнения `int`/`long` используйте `BigInteger`. В тестах проверяйте пары (0, n), два простых числа, случай когда одно число кратно другому.

## Решение проблем

| Симптом | Возможная причина | Решение |
|---------|-------------------|---------|
| Переполнение при умножении a*b | Произведение не помещается в int/long | Считать в BigInteger или использовать (a/gcd)*b |
| НОК получился 0 при ненулевых вводах | Один из аргументов 0 или деление на 0 | Явно обрабатывать a=0 или b=0 return 0 |
| Медленная работа на больших числах | Использован итеративный метод | Перейти на метод через НОД |

## Частые вопросы

**Почему НОК(0, b) часто считают 0?** По определению НОК — наименьшее положительное общее кратное; для нуля общепринято возвращать 0, чтобы формулы оставались согласованными.

**Как избежать переполнения при вычислении |a·b|/НОД?** Можно вычислять a*(b/НОД) или использовать `BigInteger`; при целочисленной арифметике сначала делить на НОД, затем умножать.

**Когда нужна факторизация вместо формулы с НОД?** Когда нужны сами простые множители чисел или их степени (например, для подсчёта делителей или других задач теории чисел).

## Заключение

В документе рассмотрены три способа вычисления НОК в `Java` и Kotlin: итеративный перебор кратных, факторизация на простые и формула через НОД. Рекомендуется метод с НОД как самый эффективный; для чисел вне диапазона `long` используется `BigInteger`.
