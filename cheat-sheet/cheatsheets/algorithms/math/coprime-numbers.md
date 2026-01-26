# Coprime Numbers

Кратко: два целых числа являются взаимно простыми (coprime), если их наибольший общий делитель (НОД) равен 1. Рассматриваются различные реализации алгоритма Евклида для проверки взаимной простоты.

**Дата последнего обновления:** 2025-01-15

## Полезные ссылки

### Официальная документация
- [GeeksforGeeks: Coprime numbers](https://www.geeksforgeeks.org/check-two-numbers-co-prime-not/)

### См. также
- `./greatest-common-divisor.md` - наибольший общий делитель
- `./least-common-multiple.md` - наименьшее общее кратное

## Содержание

- [Описание алгоритма](#описание-алгоритма)
- [Java Implementation](#java-implementation)
- [Kotlin Implementation](#kotlin-implementation)
- [Сравнение подходов](#сравнение-подходов)
- [Сложность](#сложность)

## Описание алгоритма

Имея два целых числа, a и b, мы говорим, что они взаимно просты, если единственный множитель, на который они делятся, равен 1. Взаимно простые или взаимно простые числа являются синонимами относительно простых чисел.

В этом кратком руководстве мы рассмотрим решение этой проблемы с помощью Java.

### Математическое определение

Оказывается, если наибольший общий делитель (НОД) двух чисел a и b равен 1 (т. е. gcd(a, b) = 1), то a и b взаимно просты. В результате определение того, являются ли два числа взаимно простыми, состоит просто в том, чтобы выяснить, равен ли НОД 1.

### Примеры

- gcd(8, 15) = 1 → 8 и 15 взаимно просты
- gcd(12, 18) = 6 → 12 и 18 не взаимно просты
- gcd(17, 5) = 1 → 17 и 5 взаимно просты
- gcd(81, 35) = 1 → 81 и 35 взаимно просты

## Java Implementation

### Подход 1: Итеративный алгоритм Евклида

В этом разделе мы будем использовать алгоритм Евклида для вычисления НОД двух чисел.

Прежде чем мы покажем нашу реализацию, давайте подытожим алгоритм и рассмотрим краткий пример того, как его применять для понимания.

Итак, представьте, что у нас есть два целых числа, a и b. В итеративном подходе мы сначала делим a на b и получаем остаток. Затем мы присваиваем a значение b и присваиваем b оставшееся значение. Мы повторяем этот процесс до тех пор, пока b = 0. Наконец, когда мы достигаем этой точки, мы возвращаем значение a как результат НОД, и если a = 1, мы можем сказать, что a и b взаимно просты.

### Пример

Давайте попробуем это на двух целых числах, a = 81 и b = 35.

В этом случае остаток от 81 и 35 (81 % 35) равен 11. Итак, на первом шаге итерации мы заканчиваем с a = 35 и b = 11. Следовательно, мы сделаем еще одну итерацию.

Остаток от 35, деленного на 11, равен 2. В результате у нас теперь есть a = 11 (мы поменяли местами значения) и b = 2. Давайте продолжим.

Еще один шаг приведет к a = 2 и b = 1. Теперь мы приближаемся к концу.

Наконец, после еще одной итерации мы достигнем a = 1 и b = 0. Алгоритм возвращает 1, и мы можем заключить, что 81 и 35 действительно взаимно просты.

Во-первых, давайте реализуем императивную Java-версию алгоритма Евклида, как описано выше:

```java
int iterativeGCD(int a, int b) {
    int tmp;
    while (b != 0) {
        if (a < b) {
            tmp = a;
            a = b;
            b = tmp;
        }
        tmp = b;
        b = a % b;
        a = tmp;
    }
    return a;
}
```

Как мы можем заметить, в случае, когда a меньше, чем b, мы меняем значения местами, прежде чем продолжить. Алгоритм останавливается, когда b равно 0.

Теперь мы можем использовать этот метод для проверки взаимной простоты:

```java
boolean areCoprime(int a, int b) {
    return iterativeGCD(a, b) == 1;
}
```

### Улучшенная версия

```java
int iterativeGCDImproved(int a, int b) {
    while (b != 0) {
        int temp = b;
        b = a % b;
        a = temp;
    }
    return Math.abs(a);
}

boolean areCoprimeImproved(int a, int b) {
    return iterativeGCDImproved(a, b) == 1;
}
```

## Подход 2: Рекурсивный алгоритм Евклида

Далее рассмотрим рекурсивную реализацию. Это, вероятно, чище, поскольку позволяет избежать явного обмена значениями переменных:

```java
int recursiveGCD(int a, int b) {
    if (b == 0) {
        return a;
    }
    if (a < b) {
        return recursiveGCD(b, a);
    }
    return recursiveGCD(b, a % b);
}
```

Теперь мы можем использовать этот метод для проверки взаимной простоты:

```java
boolean areCoprimeRecursive(int a, int b) {
    return recursiveGCD(a, b) == 1;
}
```

### Упрощенная рекурсивная версия

```java
int recursiveGCDSimple(int a, int b) {
    if (b == 0) {
        return Math.abs(a);
    }
    return recursiveGCDSimple(b, a % b);
}
```

## Подход 3: Использование BigInteger

Но подождите - разве алгоритм gcd уже не реализован в Java? Да, это! Класс BigInteger предоставляет метод gcd, реализующий алгоритм Евклида для нахождения наибольшего общего делителя.

Используя этот метод, мы можем более легко набросать относительно простой алгоритм следующим образом:

```java
import java.math.BigInteger;

boolean bigIntegerRelativelyPrime(int a, int b) {
    return BigInteger.valueOf(a)
        .gcd(BigInteger.valueOf(b))
        .equals(BigInteger.ONE);
}
```

### Версия для больших чисел

```java
boolean areCoprimeBigInteger(BigInteger a, BigInteger b) {
    return a.gcd(b).equals(BigInteger.ONE);
}
```

## Kotlin Implementation

### Подход 1: Итеративный алгоритм Евклида

```kotlin
fun iterativeGCDK(a: Int, b: Int): Int {
    var num1 = a
    var num2 = b
    
    while (num2 != 0) {
        if (num1 < num2) {
            val temp = num1
            num1 = num2
            num2 = temp
        }
        val temp = num2
        num2 = num1 % num2
        num1 = temp
    }
    return num1
}

fun areCoprimeK(a: Int, b: Int): Boolean {
    return iterativeGCDK(a, b) == 1
}

fun iterativeGCDImprovedK(a: Int, b: Int): Int {
    var num1 = a
    var num2 = b
    while (num2 != 0) {
        val temp = num2
        num2 = num1 % num2
        num1 = temp
    }
    return Math.abs(num1)
}

fun areCoprimeImprovedK(a: Int, b: Int): Boolean {
    return iterativeGCDImprovedK(a, b) == 1
}
```

### Подход 2: Рекурсивный алгоритм Евклида

```kotlin
fun recursiveGCDK(a: Int, b: Int): Int {
    if (b == 0) {
        return a
    }
    if (a < b) {
        return recursiveGCDK(b, a)
    }
    return recursiveGCDK(b, a % b)
}

fun areCoprimeRecursiveK(a: Int, b: Int): Boolean {
    return recursiveGCDK(a, b) == 1
}

fun recursiveGCDSimpleK(a: Int, b: Int): Int {
    if (b == 0) {
        return Math.abs(a)
    }
    return recursiveGCDSimpleK(b, a % b)
}
```

### Подход 3: Использование BigInteger

```kotlin
import java.math.BigInteger

fun bigIntegerRelativelyPrimeK(a: Int, b: Int): Boolean {
    return BigInteger.valueOf(a.toLong())
        .gcd(BigInteger.valueOf(b.toLong()))
        .equals(BigInteger.ONE)
}

fun areCoprimeBigIntegerK(a: BigInteger, b: BigInteger): Boolean {
    return a.gcd(b).equals(BigInteger.ONE)
}
```

### Варианты задачи

```kotlin
fun countCoprimesK(n: Int): Int {
    var count = 0
    for (i in 1 until n) {
        if (areCoprimeK(i, n)) {
            count++
        }
    }
    return count
}

fun areAllCoprimeK(numbers: IntArray): Boolean {
    for (i in numbers.indices) {
        for (j in i + 1 until numbers.size) {
            if (!areCoprimeK(numbers[i], numbers[j])) {
                return false
            }
        }
    }
    return true
}

fun findAllCoprimePairsK(max: Int): List<Pair<Int, Int>> {
    val pairs = mutableListOf<Pair<Int, Int>>()
    for (i in 1..max) {
        for (j in i + 1..max) {
            if (areCoprimeK(i, j)) {
                pairs.add(Pair(i, j))
            }
        }
    }
    return pairs
}
```

### Пример использования

```kotlin
fun main() {
    println(areCoprimeK(8, 15)) // true
    println(areCoprimeK(12, 18)) // false
    println(areCoprimeRecursiveK(17, 5)) // true
}
```

## Сравнение подходов

| Подход | Простота | Производительность | Когда использовать |
|--------|----------|-------------------|-------------------|
| Итеративный | Высокая | Высокая | Рекомендуется |
| Рекурсивный | Высокая | Средняя | Для обучения |
| BigInteger | Высокая | Средняя | Большие числа |

## Сложность

### Временная сложность

- **Все подходы:** O(log(min(a, b))) - эффективность алгоритма Евклида

### Пространственная сложность

- **Итеративный:** O(1) - только константная память
- **Рекурсивный:** O(log(min(a, b))) - для стека вызовов
- **BigInteger:** O(1) - только константная память

## Особенности

- **Эффективность:** Алгоритм Евклида очень эффективен
- **Простота:** Проверка взаимной простоты сводится к проверке НОД
- **Точность:** Все подходы дают одинаковый результат

## Применение

Проверка взаимной простоты используется в:

- Криптографии (RSA)
- Теории чисел
- Алгоритмах
- Математических вычислениях
- Шифровании

## Варианты задачи

### Вариант 1: Количество взаимно простых чисел до N

```java
public int countCoprimes(int n) {
    int count = 0;
    for (int i = 1; i < n; i++) {
        if (areCoprime(i, n)) {
            count++;
        }
    }
    return count;
}
```

### Вариант 2: Проверка массива на взаимную простоту

```java
public boolean areAllCoprime(int[] numbers) {
    for (int i = 0; i < numbers.length; i++) {
        for (int j = i + 1; j < numbers.length; j++) {
            if (!areCoprime(numbers[i], numbers[j])) {
                return false;
            }
        }
    }
    return true;
}
```

### Вариант 3: Найти все пары взаимно простых чисел

```java
public List<int[]> findAllCoprimePairs(int max) {
    List<int[]> pairs = new ArrayList<>();
    for (int i = 1; i <= max; i++) {
        for (int j = i + 1; j <= max; j++) {
            if (areCoprime(i, j)) {
                pairs.add(new int[]{i, j});
            }
        }
    }
    return pairs;
}
```

## Когда использовать

### Используйте итеративный подход, когда:

- Нужна максимальная производительность
- Важна экономия памяти
- Рекомендуется для большинства случаев

### Используйте рекурсивный подход, когда:

- Нужна читаемость
- Обучаете алгоритмы
- Значения не очень большие

### Используйте BigInteger, когда:

- Работаете с очень большими числами
- Нужна надежность
- Числа могут превышать long

## Заключение

В этом кратком руководстве мы представили решение проблемы определения взаимной простоты двух чисел с помощью трех реализаций алгоритма gcd. Все подходы эффективны и дают одинаковый результат.
