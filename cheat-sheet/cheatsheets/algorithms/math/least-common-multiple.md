# Least Common Multiple

Кратко: наименьшее общее кратное (НОК) двух ненулевых целых чисел - это наименьшее положительное целое число, которое идеально делится как на a, так и на b. Рассматриваются три подхода: итеративный, простая факторизация и использование НОД.

**Дата последнего обновления:** 2025-01-15

## Полезные ссылки

### Официальная документация
- [GeeksforGeeks: Program to find LCM of two numbers](https://www.geeksforgeeks.org/program-to-find-lcm-of-two-numbers/)

### См. также
- `./greatest-common-divisor.md` - наибольший общий делитель
- `./factorial-calculation.md` - вычисление факториала

## Содержание

- [Описание алгоритма](#описание-алгоритма)
- [Java Implementation](#java-implementation)
- [Kotlin Implementation](#kotlin-implementation)
- [Работа с большими числами](#работа-с-большими-числами)
- [Сравнение подходов](#сравнение-подходов)
- [Сложность](#сложность)

## Описание алгоритма

Наименьшее общее кратное (НОК) двух ненулевых целых чисел (a, b) - это наименьшее положительное целое число, которое идеально делится как на a, так и на b.

В этом уроке мы узнаем о различных подходах к нахождению LCM двух или более чисел. Мы должны отметить, что отрицательные целые числа и ноль не являются кандидатами для LCM.

### Математическое определение

НОК двух чисел a и b можно вычислить через их НОД:

```
НОК(a, b) = |a × b| / НОД(a, b)
```

### Примеры

- НОК(12, 18) = 36
- НОК(4, 6) = 12
- НОК(5, 7) = 35

## Java Implementation

### Подход 1: Итеративный метод

Мы можем найти НОК двух чисел, используя тот простой факт, что умножение есть многократное сложение.

Простой алгоритм нахождения НОК представляет собой итеративный подход, использующий несколько фундаментальных свойств НОК двух чисел.

Во-первых, мы знаем, что НОК любого числа с нулем сам по себе равен нулю. Таким образом, мы можем досрочно выйти из процедуры, когда любое из заданных целых чисел равно 0.

Во-вторых, мы также можем использовать тот факт, что нижняя граница НОК двух ненулевых целых чисел равна большему из абсолютных значений двух чисел.

Более того, как объяснялось ранее, НОК никогда не может быть отрицательным целым числом. Итак, мы будем использовать только абсолютные значения целых чисел для нахождения возможных кратных, пока не найдем общее кратное.

### Алгоритм

Давайте посмотрим точную процедуру, которой нам нужно следовать для определения lcm(a, b):

1. Если a = 0 или b = 0, вернитесь с lcm(a, b) = 0, иначе перейдите к шагу 2
2. Вычислите абсолютные значения двух чисел
3. Инициализируйте lcm как большее из двух значений, вычисленных на шаге 2
4. Если lcm делится на меньшее абсолютное значение, возврат
5. Увеличьте lcm на большее абсолютное значение из двух и перейдите к шагу 4

### Пример

Прежде чем мы начнем реализацию этого простого подхода, давайте проверим, чтобы найти lcm(12, 18).

Поскольку и 12, и 18 положительны, давайте перейдем к шагу 3, инициализируя lcm = max(12, 18) = 18, и продолжим.

В нашей первой итерации lcm = 18, что не делится на 12 в точности. Итак, мы увеличиваем его на 18 и продолжаем.

На второй итерации мы видим, что lcm = 36 и теперь полностью делится на 12. Итак, мы можем вернуться к алгоритму и сделать вывод, что lcm(12, 18) равно 36.

### Реализация

Давайте реализуем алгоритм на Java. Наш метод lcm() должен принимать два целочисленных аргумента и возвращать их LCM в качестве возвращаемого значения.

Мы можем заметить, что приведенный выше алгоритм включает в себя выполнение нескольких математических операций над числами, таких как нахождение абсолютных, минимальных и максимальных значений. Для этой цели мы можем использовать соответствующие статические методы класса Math, такие как abs(), min() и max() соответственно.

Давайте реализуем наш метод lcm():

```java
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

Далее, давайте также проверим этот метод:

```java
@Test
public void testLCM() {
    Assert.assertEquals(36, lcm(12, 18));
}
```

Приведенный выше тестовый пример проверяет правильность метода lcm(), утверждая, что lcm(12, 18) равно 36.

### Подход 2: Простая факторизация

Фундаментальная теорема арифметики утверждает, что можно однозначно выразить каждое целое число больше единицы как произведение степеней простых чисел.

Итак, для любого целого числа N > 1 имеем N = (2^k1) * (3^k2) * (5^k3) * ...

Используя результат этой теоремы, мы теперь поймем подход простой факторизации для нахождения НОК двух чисел.

Подход с простой факторизацией вычисляет LCM из простого разложения двух чисел. Мы можем использовать простые множители и показатели из простой факторизации для вычисления НОК двух чисел:

### Пример

Давайте посмотрим, как рассчитать LCM 12 и 18, используя этот подход:

Во-первых, нам нужно представить абсолютные значения двух чисел в виде произведений простых множителей:

- 12 = 2 * 2 * 3 = 2² * 3¹
- 18 = 2 * 3 * 3 = 2¹ * 3²

Здесь мы можем заметить, что простые множители в приведенных выше представлениях равны 2 и 3.

Далее давайте определим показатель степени каждого простого множителя для LCM. Мы делаем это, беря его высшую силу из двух представлений.

Используя эту стратегию, степень числа 2 в НОК будет равна max(2, 1) = 2, а степень числа 3 в НОК будет равна max(1, 2) = 2.

Наконец, мы можем вычислить НОК, умножив простые множители на соответствующую степень, полученную на предыдущем шаге. Следовательно, имеем lcm(12, 18) = 2² * 3² = 36.

### Реализация

Наша реализация на Java использует представление двух чисел в виде простой факторизации для нахождения LCM.

Для этого наш метод getPrimeFactors() должен принимать целочисленный аргумент и давать нам его представление простой факторизации. В Java мы можем представить простую факторизацию числа с помощью HashMap, где каждый ключ обозначает простой множитель, а значение, связанное с ключом, означает показатель степени соответствующего множителя.

Давайте посмотрим на итеративную реализацию метода getPrimeFactors():

```java
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

Наш метод lcm() сначала использует метод getPrimeFactors(), чтобы найти карту простой факторизации для каждого числа. Затем он использует карту простой факторизации обоих чисел, чтобы найти их LCM. Давайте посмотрим на итеративную реализацию этого метода:

```java
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

Между НОК и НОД (наибольшим общим делителем) двух чисел существует интересная связь, которая гласит, что абсолютное значение произведения двух чисел равно произведению их НОД и НОК.

Как сказано, gcd(a, b) * lcm(a, b) = |a * b|.

Следовательно, lcm(a, b) = |a * b| / gcd(a, b).

Используя эту формулу, наша первоначальная задача нахождения lcm(a, b) теперь сводится к простому нахождению gcd(a, b).

Конечно, есть несколько способов найти НОД двух чисел. Однако известно, что алгоритм Евклида является одним из самых эффективных из всех.

### Реализация алгоритма Евклида

По этой причине давайте кратко разберемся в сути этого алгоритма, который можно суммировать в двух соотношениях:

- НОД(a, b) = НОД(|a%b|, |a|); где |а| >= |б|
- НОД(р, 0) = НОД(0, р) = |р|

Давайте посмотрим, как мы можем найти lcm(12, 18), используя приведенные выше соотношения:

У нас есть НОД(12, 18) = НОД(18%12, 12) = НОД(6, 12) = НОД(12%6, 6) = НОД(0, 6) = 6

Следовательно, lcm(12, 18) = |12 x 18| / НОД(12, 18) = (12 × 18) / 6 = 36

Теперь мы увидим рекурсивную реализацию алгоритма Евклида:

```java
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

В приведенной выше реализации используются абсолютные значения чисел - поскольку НОД - это наибольшее положительное целое число, которое точно делит два числа, нас не интересуют отрицательные делители.

Используя предыдущий метод для нахождения НОД, теперь мы можем легко вычислить НОК. Опять же, наш метод lcm() должен принимать два целых числа в качестве входных данных, чтобы вернуть их LCM. Давайте посмотрим, как мы можем реализовать этот метод в Java:

```java
public static int lcm(int number1, int number2) {
    if (number1 == 0 || number2 == 0) {
        return 0;
    } else {
        int gcd = gcd(number1, number2);
        return Math.abs(number1 * number2) / gcd;
    }
}
```

## Kotlin Implementation

### Подход 1: Итеративный метод

```kotlin
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

### Подход 2: Простая факторизация

```kotlin
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

Чтобы вычислить LCM больших чисел, мы можем использовать класс BigInteger.

Внутри метод gcd() класса BigInteger использует гибридный алгоритм для оптимизации производительности вычислений. Более того, поскольку объекты BigInteger являются неизменяемыми, реализация использует изменяемые экземпляры класса MutableBigInteger, чтобы избежать частых перераспределений памяти.

Начнем с того, что он использует обычный алгоритм Евклида для многократной замены большего целого числа по его модулю на меньшее целое число.

В результате пара не только становится все меньше и меньше, но и сближается после последовательных делений. В конце концов, разница в количестве int, необходимых для хранения величины двух объектов MutableBigInteger в их соответствующих массивах значений int[], достигает либо 1, либо 0.

На этом этапе стратегия переключается на алгоритм Binary GCD для получения еще более быстрых результатов вычислений.

В этом случае мы также вычислим НОК, разделив абсолютное значение произведения чисел на их НОД. Как и в наших предыдущих примерах, наш метод lcm() принимает два значения BigInteger в качестве входных данных и возвращает LCM для двух чисел в виде BigInteger. Давайте посмотрим на это в действии:

```java
public static BigInteger lcm(BigInteger number1, BigInteger number2) {
    BigInteger gcd = number1.gcd(number2);
    BigInteger absProduct = number1.multiply(number2).abs();
    return absProduct.divide(gcd);
}
```

## Сравнение подходов

| Подход | Временная сложность | Пространственная сложность | Когда использовать |
|--------|---------------------|----------------------------|-------------------|
| Итеративный | O(a*b) | O(1) | Малые числа |
| Простая факторизация | O(n log n) | O(log n) | Средние числа |
| Использование НОД | O(log(min(a,b))) | O(1) | Рекомендуется |

## Сложность

### Временная сложность

- **Итеративный:** O(a*b) в худшем случае
- **Простая факторизация:** O(n log n) - зависит от факторизации
- **Использование НОД:** O(log(min(a,b))) - эффективность алгоритма Евклида

### Пространственная сложность

- **Итеративный:** O(1) - только константная память
- **Простая факторизация:** O(log n) - для хранения простых множителей
- **Использование НОД:** O(1) - только константная память

## Особенности

- **Эффективность:** Использование НОД наиболее эффективно
- **Простота:** Итеративный метод проще всего понять
- **Практичность:** Использование НОД рекомендуется для большинства случаев

## Применение

НОК используется в:

- Упрощении дробей
- Синхронизации событий
- Криптографии
- Теории чисел
- Алгоритмах

## Варианты задачи

### Вариант 1: НОК для массива чисел

```java
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
public static BigInteger lcmBigInteger(BigInteger a, BigInteger b) {
    return a.multiply(b).abs().divide(a.gcd(b));
}
```

## Когда использовать

### Используйте итеративный метод, когда:

- Числа очень маленькие
- Нужна простота
- Производительность не критична

### Используйте простую факторизацию, когда:

- Нужны простые множители
- Работаете со средними числами
- Важна детализация

### Используйте НОД метод, когда:

- Общий случай
- Нужна максимальная эффективность
- Работаете с любыми числами

## Заключение

В этом руководстве мы обсудили различные методы поиска наименьшего общего кратного двух чисел в Java.

Более того, мы также узнали о связи произведения чисел с их НОК и НОД. Имея алгоритмы, которые могут эффективно вычислять НОД двух чисел, мы также свели задачу вычисления НОК к задаче вычисления НОД.
