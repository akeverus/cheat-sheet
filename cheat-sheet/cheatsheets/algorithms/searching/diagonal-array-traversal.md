# Diagonal Array Traversal

Кратко: перебор двумерного массива по диагонали - это техника обхода элементов массива по диагональным линиям, которая используется в различных алгоритмах обработки матриц.

**Дата последнего обновления:** 2025-01-15

## Полезные ссылки

### Официальная документация
- [GeeksforGeeks: Diagonal Traversal of Matrix](https://www.geeksforgeeks.org/print-matrix-diagonal-pattern/)

### См. также
- `./pairs-with-given-sum.md` - поиск пар с заданной суммой
- `./maximum-subarray.md` - проблема максимального подмассива

## Содержание

- [Описание алгоритма](#описание-алгоритма)
- [Принцип работы](#принцип-работы)
- [Java Implementation](#java-implementation)
- [Kotlin Implementation](#kotlin-implementation)
- [Сложность](#сложность)

## Описание алгоритма

В этом уроке мы увидим, как перебирать двумерный массив по диагонали. Предлагаемое нами решение можно использовать для квадратного двумерного массива любого размера.

Ключом к работе с элементами массива является знание того, как получить определенный элемент из этого массива. Для двумерного массива мы используем индексы строк и столбцов для получения элементов массива.

## Принцип работы

Далее нам нужно понять, сколько диагональных линий у нас есть в нашем массиве. Мы делаем это, сначала получая длину одного измерения массива, а затем используя его для получения количества диагональных линий (diagonLines).

Затем мы используем количество диагональных линий, чтобы получить среднюю точку, которая поможет в поиске индексов строк и столбцов.

В этом примере средняя точка равна трем:

```java
int length = twoDArray.length;
int diagonalLines = (length + length) - 1;
int midPoint = (diagonalLines / 2) + 1;
```

### Визуализация

Для массива 3x3:
```
a b c
d e f
g h i

Диагонали:
1: a
2: b, d
3: c, e, g
4: f, h
5: i
```

## Java Implementation

### Реализация

Чтобы перебрать весь массив, мы начинаем цикл с 1 до тех пор, пока переменная цикла не станет меньше или равна переменной диагональных линий.

```java
for (int i = 1; i <= diagonalLines; i++) {
    // обработка диагонали i
}
```

Давайте также представим идею количества элементов в диагональной линии, назвав ее `itemsInDiagonal`. Например, в строке 3 на диаграмме выше есть 3 элемента (g, e, c), а в строке 4 - 2 (h, f). Эта переменная увеличивается на 1 в цикле, когда переменная цикла i меньше или равна midPoint. В противном случае он уменьшается на 1.

После увеличения или уменьшения `itemsInDiagonal` у нас появляется новый цикл с переменной цикла j. Переменная j увеличивается с 0 до тех пор, пока она не станет меньше, чем `itemsInDiagonal`.

Затем мы используем переменные цикла i и j для получения индексов строк и столбцов. Логика этого вычисления зависит от того, больше ли переменная цикла i, чем midPoint, или нет. Когда i больше, чем midPoint, мы также используем переменную длины для определения индексов строки и столбца:

```java
int rowIndex;
int columnIndex;

if (i <= midPoint) {
    itemsInDiagonal++;
    
    for (int j = 0; j < itemsInDiagonal; j++) {
        rowIndex = (i - j) - 1;
        columnIndex = j;
        items.append(twoDArray[rowIndex][columnIndex]);
    }
} else {
    itemsInDiagonal--;
    
    for (int j = 0; j < itemsInDiagonal; j++) {
        rowIndex = (length - 1) - j;
        columnIndex = (i - length) + j;
        items.append(twoDArray[rowIndex][columnIndex]);
    }
}
```

### Полная реализация

```java
public List<Integer> traverseDiagonally(int[][] twoDArray) {
    int length = twoDArray.length;
    int diagonalLines = (length + length) - 1;
    int midPoint = (diagonalLines / 2) + 1;
    List<Integer> items = new ArrayList<>();
    int itemsInDiagonal = 0;
    
    for (int i = 1; i <= diagonalLines; i++) {
        int rowIndex;
        int columnIndex;
        
        if (i <= midPoint) {
            itemsInDiagonal++;
            
            for (int j = 0; j < itemsInDiagonal; j++) {
                rowIndex = (i - j) - 1;
                columnIndex = j;
                items.add(twoDArray[rowIndex][columnIndex]);
            }
        } else {
            itemsInDiagonal--;
            
            for (int j = 0; j < itemsInDiagonal; j++) {
                rowIndex = (length - 1) - j;
                columnIndex = (i - length) + j;
                items.add(twoDArray[rowIndex][columnIndex]);
            }
        }
    }
    
    return items;
}
```

### Пример использования

```java
int[][] matrix = {
    {1, 2, 3},
    {4, 5, 6},
    {7, 8, 9}
};

List<Integer> result = traverseDiagonally(matrix);
// Результат: [1, 2, 4, 3, 5, 7, 6, 8, 9]
```

## Kotlin Implementation

```kotlin
fun traverseDiagonallyK(twoDArray: Array<IntArray>): List<Int> {
    val length = twoDArray.size
    val diagonalLines = (length + length) - 1
    val midPoint = (diagonalLines / 2) + 1
    val items = mutableListOf<Int>()
    var itemsInDiagonal = 0
    
    for (i in 1..diagonalLines) {
        if (i <= midPoint) {
            itemsInDiagonal++
            for (j in 0 until itemsInDiagonal) {
                items.add(twoDArray[(i - j) - 1][j])
            }
        } else {
            itemsInDiagonal--
            for (j in 0 until itemsInDiagonal) {
                items.add(twoDArray[(length - 1) - j][(i - length) + j])
            }
        }
    }
    return items
}
```

## Сложность

### Временная сложность

- **Все случаи:** O(n²), где n - размер одной стороны квадратного массива

Каждый элемент массива посещается ровно один раз.

### Пространственная сложность

- **Все случаи:** O(n²) - для хранения результата (если сохраняем все элементы)

Если мы просто обрабатываем элементы без сохранения, пространственная сложность будет O(1).

## Особенности

- **Работает только с квадратными массивами:** Алгоритм оптимизирован для квадратных матриц
- **Диагональный обход:** Элементы обрабатываются по диагоналям
- **Два направления:** Сначала диагонали растут, затем уменьшаются

## Применение

Диагональный обход используется в:

- Обработке изображений
- Матричных операциях
- Алгоритмах динамического программирования
- Обработке данных в таблицах
- Визуализации данных

## Варианты обхода

### Обход сверху-вниз, слева-направо

```java
// Начинаем с верхнего левого угла
for (int i = 0; i < n; i++) {
    for (int j = 0; j <= i; j++) {
        // Обработка элемента matrix[i - j][j]
    }
}
```

### Обход снизу-вверх, справа-налево

```java
// Начинаем с нижнего правого угла
for (int i = n - 1; i >= 0; i--) {
    for (int j = n - 1; j >= i; j--) {
        // Обработка элемента matrix[j][n - 1 - (j - i)]
    }
}
```

## Заключение

В этом руководстве мы показали, как перебирать квадратный двумерный массив по диагонали, используя метод, который помогает получить индексы строк и столбцов. Этот подход полезен для различных задач обработки матриц и алгоритмов.
