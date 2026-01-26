# Perfect Square Check

Кратко: проверка, является ли целое число полным квадратом (идеальным квадратом). Рассматриваются различные подходы: использование Math.sqrt(), бинарный поиск, метод Ньютона и оптимизации.

**Дата последнего обновления:** 2025-01-15

## Полезные ссылки

### Официальная документация
- [GeeksforGeeks: Check if given number is perfect square](https://www.geeksforgeeks.org/check-if-given-number-is-perfect-square-in-cpp/)

### См. также
- `./greatest-common-divisor.md` - наибольший общий делитель
- `./factorial-calculation.md` - вычисление факториала

## Содержание

- [Описание алгоритма](#описание-алгоритма)
- [Java Implementation](#java-implementation)
- [Kotlin Implementation](#kotlin-implementation)
- [Сравнение подходов](#сравнение-подходов)
- [Сложность](#сложность)

## Описание алгоритма

Полный квадрат - это число, которое можно представить как произведение двух равных целых чисел.

В этой статье мы обнаружим несколько способов определить, является ли целое число идеальным квадратом в Java. Кроме того, мы обсудим преимущества и недостатки каждого метода, чтобы определить его эффективность и какой из них самый быстрый.

### Примеры

- 4 = 2 × 2 → полный квадрат
- 9 = 3 × 3 → полный квадрат
- 10 = ? → не полный квадрат
- 16 = 4 × 4 → полный квадрат

### Диапазон чисел

Как мы знаем, Java предоставляет нам два типа данных для определения целого числа. Первый - это int, который представляет число в 32 битах, а другой - long, который представляет число в 64 битах. В этой статье мы будем использовать тип данных long для обработки наихудшего случая (наибольшего возможного целого числа).

Поскольку Java представляет длинное число в 64 битах, диапазон длинного числа составляет от -9,223,372,036,854,775,808 до 9,223,372,036,854,775,807. А поскольку мы имеем дело с идеальными квадратами, нас интересует только набор положительных целых чисел, потому что умножение любого целого числа само на себя всегда дает положительное число.

Кроме того, поскольку наибольшее число равно примерно 2^63, это означает, что существует около 2^31.5 целых чисел, квадрат которых меньше 2^63. Кроме того, мы можем предположить, что наличие таблицы поиска этих чисел неэффективно.

## Java Implementation

### Подход 1: Использование Math.sqrt()

Самый простой и прямой способ проверить, является ли целое число полным квадратом, - использовать функцию sqrt. Как мы знаем, функция sqrt возвращает двойное значение. Итак, что нам нужно сделать, так это привести результат к типу int и умножить его сам на себя. Затем мы проверяем, равен ли результат целому числу, с которого мы начали:

```java
public static boolean isPerfectSquareByUsingSqrt(long n) {
    if (n <= 0) {
        return false;
    }
    
    double squareRoot = Math.sqrt(n);
    long tst = (long)(squareRoot + 0.5);
    return tst * tst == n;
}
```

Обратите внимание, что нам может понадобиться добавить 0.5 к результату из-за ошибок точности, с которыми мы можем столкнуться при работе с двойными значениями. Иногда целые числа могли быть представлены десятичной точкой при назначении двойной переменной.

Например, если мы присвоим переменной double число 3, то ее значение может быть 3.00000001 или 2.99999999. Итак, чтобы избежать такого представления, мы добавляем 0.5 перед приведением к типу long, чтобы убедиться, что мы получаем действительное значение.

Кроме того, если мы протестируем функцию sqrt с одним числом, мы заметим, что время выполнения быстрое. С другой стороны, если нам нужно вызывать функцию sqrt много раз, и мы пытаемся уменьшить количество операций, выполняемых функцией sqrt, такая микрооптимизация действительно может иметь значение.

### Альтернативная версия

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

Мы можем использовать бинарный поиск, чтобы найти квадратный корень числа без использования функции sqrt.

Так как диапазон числа от 1 до 2^63, корень находится между 1 и 2^31.5. Итак, алгоритму бинарного поиска требуется около 16 итераций, чтобы получить квадратный корень:

```java
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

Таким образом, мы можем построить таблицу поиска, чтобы указать диапазон квадратного корня на основе количества цифр числа, с которого мы начинаем. Это уменьшит диапазон бинарного поиска. Таким образом, для получения квадратного корня потребуется меньше итераций:

```java
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

В общем, мы можем использовать метод Ньютона, чтобы получить квадратный корень из любого числа, даже нецелого. Основная идея метода Ньютона состоит в том, чтобы предположить, что число X является квадратным корнем числа N. После этого мы можем запустить цикл и продолжить вычисление корня, который, несомненно, будет двигаться к правильному квадратному корню из N.

Однако с некоторыми изменениями в методе Ньютона мы можем использовать его для проверки того, является ли целое число полным квадратом:

```java
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

Один из фактов, который мы можем использовать, это «совершенные квадраты могут заканчиваться только на 0, 1, 4 или 9 по основанию 16». Таким образом, мы можем преобразовать целое число в основание 16 перед началом вычислений. После этого исключаем случаи, когда число считается несовершенным квадратным корнем:

```java
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

## Kotlin Implementation

### Подход 1: Использование Math.sqrt()

```kotlin
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
| Бинарный поиск | O(log n) | O(1) | Без Math.sqrt() |
| Метод Ньютона | O(log log n) | O(1) | Быстрая сходимость |
| С оптимизацией | O(1) | O(1) | Множественные проверки |

## Сложность

### Временная сложность

- **Math.sqrt():** O(1) - константное время (аппроксимация)
- **Бинарный поиск:** O(log n) - логарифмическое время
- **Метод Ньютона:** O(log log n) - очень быстрая сходимость

### Пространственная сложность

- **Все подходы:** O(1) - только константная память

## Особенности

- **Эффективность:** Math.sqrt() наиболее эффективен
- **Точность:** Все подходы точны для целых чисел
- **Оптимизация:** Быстрые проверки могут исключить большинство чисел

## Применение

Проверка полных квадратов используется в:

- Математических вычислениях
- Криптографии
- Алгоритмах
- Оптимизации
- Теории чисел

## Варианты задачи

### Вариант 1: Найти ближайший полный квадрат

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

### Используйте Math.sqrt(), когда:

- Нужна максимальная производительность
- Простота важна
- Рекомендуется для большинства случаев

### Используйте бинарный поиск, когда:

- Нельзя использовать Math.sqrt()
- Нужен контроль над алгоритмом
- Работаете с очень большими числами

### Используйте метод Ньютона, когда:

- Нужна быстрая сходимость
- Работаете с большими числами
- Важна производительность

## Заключение

В этой статье мы обсудили несколько способов определить, является ли целое число полным квадратом или нет. Как мы видели, мы всегда можем улучшить алгоритмы, используя некоторые приемы.

Эти приемы позволят исключить большое количество случаев до запуска основной работы алгоритма. Причина в том, что многие целые числа можно легко определить как несовершенные квадраты.
