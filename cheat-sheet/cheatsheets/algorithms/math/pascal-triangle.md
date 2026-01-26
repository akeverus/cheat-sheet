# Pascal Triangle

Кратко: треугольник Паскаля представляет собой расположение биномиальных коэффициентов в треугольной форме. Числа в треугольнике Паскаля расположены так, что каждое из них представляет собой сумму двух чисел непосредственно над ним.

**Дата последнего обновления:** 2025-01-15

## Полезные ссылки

### Официальная документация
- [GeeksforGeeks: Pascal's Triangle](https://www.geeksforgeeks.org/pascal-triangle/)

### См. также
- `./factorial-calculation.md` - вычисление факториала
- `./fibonacci-sequence.md` - ряд Фибоначчи

## Содержание

- [Описание алгоритма](#описание-алгоритма)
- [Java Implementation](#java-implementation)
- [Kotlin Implementation](#kotlin-implementation)
- [Сравнение подходов](#сравнение-подходов)
- [Сложность](#сложность)

## Описание алгоритма

Треугольник Паскаля представляет собой расположение биномиальных коэффициентов в треугольной форме. Числа в треугольнике Паскаля расположены так, что каждое из них представляет собой сумму двух чисел непосредственно над ним.

В этом уроке мы увидим, как напечатать треугольник Паскаля в Java.

### Структура треугольника

Первые несколько строк треугольника Паскаля:

```
        1
       1 1
      1 2 1
     1 3 3 1
    1 4 6 4 1
   1 5 10 10 5 1
```

Каждое число в треугольнике является суммой двух чисел непосредственно над ним.

### Биномиальные коэффициенты

Числа в треугольнике Паскаля также представляют биномиальные коэффициенты:

```
C(n, k) = n! / ((n-k)! * k!)
```

## Java Implementation

### Подход 1: Рекурсия с факториалом

Мы можем напечатать треугольник Паскаля, используя рекурсию по формуле nCr: n! / ((n-r)! * r!)

Во-первых, давайте создадим рекурсивную функцию:

```java
public int factorial(int i) {
    if (i == 0) {
        return 1;
    }
    return i * factorial(i - 1);
}
```

Затем мы можем напечатать треугольник, используя эту функцию:

```java
private void printUseRecursion(int n) {
    for (int i = 0; i <= n; i++) {
        for (int j = 0; j <= n - i; j++) {
            System.out.print(" ");
        }
        for (int k = 0; k <= i; k++) {
            System.out.print(" " + factorial(i) / (factorial(i - k) * factorial(k)));
        }
        System.out.println();
    }
}
```

Результат при n = 5 будет выглядеть так:

```
     1
    1 1
   1 2 1
  1 3 3 1
 1 4 6 4 1
1 5 10 10 5 1
```

### Недостатки

Этот подход неэффективен, так как вычисляет факториал многократно для одних и тех же значений.

## Подход 2: Биномиальное разложение

Другой способ напечатать треугольник Паскаля без рекурсии - использовать биномиальное разложение.

У нас всегда есть значение 1 в начале каждой строки, тогда значение k в строке (n) и позиции (i) будет рассчитываться как:

```
k = (k * (n - i) / i)
```

Давайте создадим нашу функцию, используя эту формулу:

```java
public void printUseBinomialExpansion(int n) {
    for (int line = 1; line <= n; line++) {
        for (int j = 0; j <= n - line; j++) {
            System.out.print(" ");
        }
        
        int k = 1;
        for (int i = 1; i <= line; i++) {
            System.out.print(k + " ");
            k = k * (line - i) / i;
        }
        System.out.println();
    }
}
```

### Преимущества

Этот подход более эффективен, так как вычисляет каждое значение только один раз.

## Подход 3: Динамическое программирование

Мы можем использовать динамическое программирование для построения треугольника Паскаля:

```java
public int[][] generatePascalTriangle(int n) {
    int[][] triangle = new int[n][];
    
    for (int i = 0; i < n; i++) {
        triangle[i] = new int[i + 1];
        triangle[i][0] = 1;
        triangle[i][i] = 1;
        
        for (int j = 1; j < i; j++) {
            triangle[i][j] = triangle[i-1][j-1] + triangle[i-1][j];
        }
    }
    
    return triangle;
}

public void printPascalTriangle(int n) {
    int[][] triangle = generatePascalTriangle(n);
    
    for (int i = 0; i < n; i++) {
        for (int j = 0; j < n - i; j++) {
            System.out.print(" ");
        }
        for (int j = 0; j <= i; j++) {
            System.out.print(triangle[i][j] + " ");
        }
        System.out.println();
    }
}
```

### Получение конкретного элемента

```java
public int getPascalValue(int row, int col) {
    if (col == 0 || col == row) {
        return 1;
    }
    return getPascalValue(row - 1, col - 1) + getPascalValue(row - 1, col);
}
```

### Оптимизированная версия с мемоизацией

```java
private Map<String, Integer> memo = new HashMap<>();

public int getPascalValueMemoized(int row, int col) {
    if (col == 0 || col == row) {
        return 1;
    }
    
    String key = row + "," + col;
    if (memo.containsKey(key)) {
        return memo.get(key);
    }
    
    int value = getPascalValueMemoized(row - 1, col - 1) + 
                getPascalValueMemoized(row - 1, col);
    memo.put(key, value);
    return value;
}
```

## Сравнение подходов

| Подход | Временная сложность | Пространственная сложность | Когда использовать |
|--------|---------------------|----------------------------|-------------------|
| Рекурсия с факториалом | O(n³) | O(n) | Не рекомендуется |
| Биномиальное разложение | O(n²) | O(1) | Для печати |
| Динамическое программирование | O(n²) | O(n²) | Для хранения |

## Сложность

### Временная сложность

- **Рекурсия с факториалом:** O(n³) - для каждой позиции вычисляется факториал
- **Биномиальное разложение:** O(n²) - один проход по всем позициям
- **Динамическое программирование:** O(n²) - заполнение таблицы

### Пространственная сложность

- **Рекурсия с факториалом:** O(n) - для стека вызовов
- **Биномиальное разложение:** O(1) - только константная память
- **Динамическое программирование:** O(n²) - для хранения треугольника

## Особенности

- **Эффективность:** Биномиальное разложение наиболее эффективно для печати
- **Хранение:** Динамическое программирование полезно, если нужен доступ к значениям
- **Простота:** Рекурсия проще всего понять, но неэффективна

## Применение

Треугольник Паскаля используется в:

- Комбинаторике
- Теории вероятностей
- Биномиальных разложениях
- Алгоритмах
- Математическом анализе

## Варианты задачи

### Вариант 1: Получение n-й строки

```java
public List<Integer> getRow(int rowIndex) {
    List<Integer> row = new ArrayList<>();
    row.add(1);
    
    for (int i = 1; i <= rowIndex; i++) {
        row.add((int)((long)row.get(i-1) * (rowIndex - i + 1) / i));
    }
    
    return row;
}
```

### Вариант 2: Сумма элементов в строке

```java
public int sumOfRow(int rowIndex) {
    return (int) Math.pow(2, rowIndex);
}
```

### Вариант 3: Проверка, является ли число числом Паскаля

```java
public boolean isPascalNumber(int num) {
    for (int n = 0; n <= num; n++) {
        for (int k = 0; k <= n; k++) {
            if (binomialCoefficient(n, k) == num) {
                return true;
            }
        }
    }
    return false;
}

private int binomialCoefficient(int n, int k) {
    if (k == 0 || k == n) {
        return 1;
    }
    return binomialCoefficient(n-1, k-1) + binomialCoefficient(n-1, k);
}
```

## Kotlin Implementation

### Подход 2: Биномиальное разложение

```kotlin
fun printUseBinomialExpansionK(n: Int) {
    for (line in 1..n) {
        repeat(n - line) { print(" ") }
        
        var k = 1
        for (i in 1..line) {
            print("$k ")
            k = k * (line - i) / i
        }
        println()
    }
}
```

### Подход 3: Динамическое программирование

```kotlin
fun generatePascalTriangleK(n: Int): Array<IntArray> {
    val triangle = Array(n) { IntArray(it + 1) }
    
    for (i in 0 until n) {
        triangle[i][0] = 1
        triangle[i][i] = 1
        
        for (j in 1 until i) {
            triangle[i][j] = triangle[i - 1][j - 1] + triangle[i - 1][j]
        }
    }
    
    return triangle
}

fun getRowK(rowIndex: Int): List<Int> {
    val row = mutableListOf(1)
    for (i in 1..rowIndex) {
        row.add((row[i - 1].toLong() * (rowIndex - i + 1) / i).toInt())
    }
    return row
}
```

### Пример использования

```kotlin
fun main() {
    printUseBinomialExpansionK(5)
    val triangle = generatePascalTriangleK(5)
    println(getRowK(5)) // [1, 5, 10, 10, 5, 1]
}
```

## Когда использовать

### Используйте биномиальное разложение, когда:

- Нужна только печать треугольника
- Важна эффективность
- Не нужно хранить значения

### Используйте динамическое программирование, когда:

- Нужен доступ к значениям
- Требуется многократное использование
- Важна производительность

## Заключение

В этом уроке мы рассмотрели различные способы генерации и печати треугольника Паскаля. Биномиальное разложение является наиболее эффективным для простой печати, в то время как динамическое программирование полезно, когда нужен доступ к значениям треугольника.
