---
title: "Решатель судоку (Sudoku Solver)"
description: "Кратко: как решить судоку через backtracking и Dancing Links (Algorithm X), когда выбирать каждый подход, как валидировать доску и профилировать производительность. Даем готовые Java/Kotlin примеры с русскими комментариями, тестами и рекомендациями для продакшена."
tags:
  - algorithms
  - problems
  - sudoku-solver
difficulty: "intermediate"
prerequisites: []
next: []
updated: "2026-02-11"
---
# Решатель судоку (Sudoku Solver)

Кратко: как решить судоку через `backtracking` и `Dancing Links` (`Algorithm X`), когда выбирать каждый подход, как валидировать доску и профилировать производительность. Даем готовые Java/Kotlin примеры с русскими комментариями, тестами и рекомендациями для продакшена.

## Полезные ссылки

### Официальная документация
- [Baeldung: Sudoku Solver](https://www.baeldung.com/)

### См. также
- [[traveling-salesman-problem|Задача коммивояжера]] — **TSP**
- [[knapsack-problem|Задача о рюкзаке]] — **knapsack**
- [Бинарное дерево](../trees/) — бинарное дерево

## Содержание

- [Описание алгоритма](#описание-алгоритма)
- [Правила судоку](#правила-судоку)
  - [Пример головоломки](#пример-головоломки)
- [Реализация на Java](#реализация-на-java)
  - [Подход 1: Поиск с возвратом](#подход-1-поиск-с-возвратом)
  - [Реализация](#реализация)
  - [Пример использования (Java)](#пример-использования-java)
- [Подход 2: Dancing Links (Algorithm X)](#подход-2-dancing-links-algorithm-x)
  - [Основные концепции](#основные-концепции)
  - [Реализация Dancing Links](#реализация-dancing-links)
- [Сравнение подходов](#сравнение-подходов)
- [Реализация на Kotlin](#реализация-на-kotlin)
- [Сложность](#сложность)
  - [Временная сложность](#временная-сложность)
  - [Пространственная сложность](#пространственная-сложность)
- [Особенности](#особенности)
- [Применение](#применение)
- [Варианты задачи](#варианты-задачи)
  - [Вариант 1: Проверка валидности доски](#вариант-1-проверка-валидности-доски)
  - [Вариант 2: Генерация судоку](#вариант-2-генерация-судоку)
- [Когда использовать](#когда-использовать)
- [Лучшие практики](#лучшие-практики)
- [Тестирование](#тестирование)
- [Решение проблем](#решение-проблем)
- [Частые вопросы](#частые-вопросы)
- [Глоссарий](#глоссарий)
- [Дополнительные примеры использования](#дополнительные-примеры-использования)
  - [Пример 1: Валидация ввода](#пример-1-валидация-ввода)
  - [Пример 2: Мини-доска 4x4](#пример-2-мини-доска-4x4)
  - [Пример 3: Быстрый рантайм-тест](#пример-3-быстрый-рантайм-тест)
  - [Пример 4: CLI/логирование](#пример-4-cli-логирование)
- [Чек-лист перед продакшеном](#чек-лист-перед-продакшеном)
- [Заключение](#заключение)


## Описание алгоритма

В этой статье мы рассмотрим головоломку судоку и алгоритмы, используемые для ее решения.

Далее мы реализуем решения на **Java**. Первым решением будет простая атака грубой силы. Второй будет использовать технику **Dancing Links**.

Имейте в виду, что основное внимание мы сосредоточим на алгоритмах, а не на дизайне ООП.

## Правила судоку

Проще говоря, судоку представляет собой комбинаторную головоломку с размещением чисел с сеткой 9 `x 9` ячеек, частично заполненную числами от 1 до 9. Цель состоит в том, чтобы заполнить оставшиеся пустые поля остальными числами, чтобы в каждой строке и столбце было только одно число каждого вида.

Более того, в каждом подразделе сетки размером 3 `x 3` не может быть дублированного числа. Уровень сложности естественным образом повышается с увеличением количества пустых полей на каждой доске.

### Пример головоломки

**Чтобы сделать наше решение более интересным и проверить алгоритм, мы собираемся использовать «самую сложную в мире доску судоку»:**

```text
8 0 0 0 0 0 0 0 0
0 0 3 6 0 0 0 0 0
0 7 0 0 9 0 2 0 0
0 5 0 0 0 7 0 0 0
0 0 0 0 4 5 7 0 0
0 0 0 1 0 0 0 3 0
0 0 1 0 0 0 0 6 8
0 0 8 5 0 0 0 1 0
0 9 0 0 0 0 4 0 0
```

**Правильно решенная головоломка:**

```text
8 1 2 7 5 3 6 4 9
9 4 3 6 8 2 1 7 5
6 7 5 4 9 1 2 8 3
1 5 4 2 3 7 8 9 6
3 6 9 8 4 5 7 2 1
2 8 7 1 6 9 5 3 4
5 2 1 9 7 4 3 6 8
4 3 8 5 2 6 9 1 7
7 9 6 3 1 8 4 5 2
```

## Реализация на Java

### Подход 1: Поиск с возвратом

Поиск с возвратом перебирает пустые клетки, пытаясь проставить значения 1–9 и откатываясь при нарушении ограничений строки, столбца или блока 3×3. Это простой, но экспоненциальный перебор; его сила в понятности и легкости отладки.

### Реализация

**Прежде всего, давайте определим нашу доску как двумерный массив целых чисел. Мы будем использовать 0 в качестве нашей пустой ячейки:**

```java
// Решатель судоку: backtracking с проверкой ограничений и откатом
public class SudokuSolver {
    private static final int BOARD_SIZE = 9;
    private static final int SUBSECTION_SIZE = 3;
    private static final int NO_VALUE = 0;
    private static final int MIN_VALUE = 1;
    private static final int MAX_VALUE = 9;
    private static final int BOARD_START_INDEX = 0;

    private final int[][] board;

    public SudokuSolver(int[][] board) {
        this.board = board;
    }

    // Рекурсивно пытаемся заполнить пустые клетки; откатываемся при нарушении
    public boolean solve() {
        for (int row = BOARD_START_INDEX; row < BOARD_SIZE; row++) {
            for (int column = BOARD_START_INDEX; column < BOARD_SIZE; column++) {
                if (board[row][column] == NO_VALUE) {        // нашли пустую клетку
                    for (int value = MIN_VALUE; value <= MAX_VALUE; value++) {
                        board[row][column] = value;         // ставим кандидат
                        if (isValid(board, row, column) && solve()) {
                            return true;                    // нашли решение глубже
                        }
                        board[row][column] = NO_VALUE;      // откат, если не подошло
                    }
                    return false;                            // нет валидных значений
                }
            }
        }
        return true;                                         // все клетки заполнены
    }
}
```

**Другой метод, который нам нужен, — это метод `isValid()`, который будет проверять ограничения судоку, т.е. проверять, допустимы ли строка, столбец и сетка 3 `x 3`:**

```java
// Проверяем ограничения строки, столбца и блока 3×3 для текущей клетки
private boolean isValid(int[][] board, int row, int column) {
    return rowConstraint(board, row)
        && columnConstraint(board, column)
        && subsectionConstraint(board, row, column);
}
```

**Эти три проверки относительно похожи. Во-первых, давайте начнем с проверки строк:**

```java
// Нет ли дубликатов в строке
private boolean rowConstraint(int[][] board, int row) {
    boolean[] constraint = new boolean[BOARD_SIZE];
    return IntStream.range(BOARD_START_INDEX, BOARD_SIZE)
        .allMatch(column -> checkConstraint(board, row, constraint, column));
}
```

**Далее мы используем почти идентичный код для проверки столбца:**

```java
// Нет ли дубликатов в столбце
private boolean columnConstraint(int[][] board, int column) {
    boolean[] constraint = new boolean[BOARD_SIZE];
    return IntStream.range(BOARD_START_INDEX, BOARD_SIZE)
        .allMatch(row -> checkConstraint(board, row, constraint, column));
}
```

**Кроме того, нам нужно проверить подраздел 3 `x 3`:**

```java
// Проверяем блок 3×3 на дубликаты
private boolean subsectionConstraint(int[][] board, int row, int column) {
    boolean[] constraint = new boolean[BOARD_SIZE];
    int subsectionRowStart = (row / SUBSECTION_SIZE) * SUBSECTION_SIZE;
    int subsectionRowEnd = subsectionRowStart + SUBSECTION_SIZE;
    int subsectionColumnStart = (column / SUBSECTION_SIZE) * SUBSECTION_SIZE;
    int subsectionColumnEnd = subsectionColumnStart + SUBSECTION_SIZE;

    for (int r = subsectionRowStart; r < subsectionRowEnd; r++) {
        for (int c = subsectionColumnStart; c < subsectionColumnEnd; c++) {
            if (!checkConstraint(board, r, constraint, c)) {
                return false; // найден повтор
            }
        }
    }
    return true;
}
```

**Наконец, нам нужен метод `checkConstraint()`:**

```java
// Помечаем значение как встреченное; если оно повторяется — нарушение
private boolean checkConstraint(int[][] board, int row, boolean[] constraint, int column) {
    if (board[row][column] != NO_VALUE) {
        int valueIndex = board[row][column] - 1;
        if (!constraint[valueIndex]) {
            constraint[valueIndex] = true;
        } else {
            return false; // значение уже было в строке/столбце/блоке
        }
    }
    return true;
}
```

### Пример использования (Java)

```java
int[][] board = {
    { 8, 0, 0, 0, 0, 0, 0, 0, 0 },
    { 0, 0, 3, 6, 0, 0, 0, 0, 0 },
    { 0, 7, 0, 0, 9, 0, 2, 0, 0 },
    { 0, 5, 0, 0, 0, 7, 0, 0, 0 },
    { 0, 0, 0, 0, 4, 5, 7, 0, 0 },
    { 0, 0, 0, 1, 0, 0, 0, 3, 0 },
    { 0, 0, 1, 0, 0, 0, 0, 6, 8 },
    { 0, 0, 8, 5, 0, 0, 0, 1, 0 },
    { 0, 9, 0, 0, 0, 0, 4, 0, 0 }
};

SudokuSolver solver = new SudokuSolver(board);
if (solver.solve()) {
    solver.printBoard();
}
```

## Подход 2: Dancing Links (Algorithm X)

Судоку можно описать как задачу точного покрытия, которая может быть представлена матрицей инцидентности, показывающей отношения между двумя объектами.

Алгоритм X представляет собой «метод проб и ошибок» для поиска всех решений проблемы точного покрытия.

Эффективной реализацией алгоритма X является алгоритм **Dancing Links** (сокращенно DLX), предложенный доктором Дональдом Кнутом.

### Основные концепции

**Алгоритм **Dancing Links** работает на основе базового наблюдения, что следующая операция над двусвязными списками узлов:**

```java
node.prev.next = node.next
node.next.prev = node.prev
```

**удаляет узел, а:**

```java
node.prev = node
node.next = node
```

восстанавливает узел.

Каждый узел в **DLX** связан с узлом слева, справа, вверху и внизу.

### Реализация Dancing Links

**Класс `DancingNode` будет иметь все операции, необходимые для добавления или удаления узлов:**

```java
// Узел танцующих связей: хранит ссылки в четырёх направлениях и на столбец
class DancingNode {
    DancingNode L, R, U, D;
    ColumnNode C;

    // Подвешиваем узел вниз в столбце
    DancingNode hookDown(DancingNode node) {
        assert (this.C == node.C);
        node.D = this.D;
        node.D.U = node;
        node.U = this;
        this.D = node;
        return node;
    }

    // Подвешиваем узел вправо в строке
    DancingNode hookRight(DancingNode node) {
        node.R = this.R;
        node.R.L = node;
        node.L = this;
        this.R = node;
        return node;
    }

    // Удаляем из горизонтального списка
    void unlinkLR() {
        this.L.R = this.R;
        this.R.L = this.L;
    }

    // Восстанавливаем в горизонтальном списке
    void relinkLR() {
        this.L.R = this.R.L = this;
    }

    // Удаляем из вертикального списка
    void unlinkUD() {
        this.U.D = this.D;
        this.D.U = this.U;
    }

    // Восстанавливаем в вертикальном списке
    void relinkUD() {
        this.U.D = this.D.U = this;
    }

    DancingNode() {
        L = R = U = D = this; // изначально зациклен на себе
    }

    DancingNode(ColumnNode c) {
        this();
        C = c;
    }
}
```

**Класс `ColumnNode` свяжет столбцы вместе:**

```java
// Столбец матрицы точного покрытия: умеет скрывать/раскрывать себя
class ColumnNode extends DancingNode {
    int size;      // количество единиц в столбце
    String name;   // метка столбца

    ColumnNode(String n) {
        super();
        size = 0;
        name = n;
        C = this;   // ссылка на себя как на столбец
    }

    // Удаляем столбец и связанные строки (cover в Algorithm X)
    void cover() {
        unlinkLR();
        for (DancingNode i = this.D; i != this; i = i.D) {
            for (DancingNode j = i.R; j != i; j = j.R) {
                j.unlinkUD();
                j.C.size--;
            }
        }
    }

    // Восстанавливаем столбец и строки (uncover)
    void uncover() {
        for (DancingNode i = this.U; i != this; i = i.U) {
            for (DancingNode j = i.L; j != i; j = j.L) {
                j.C.size++;
                j.relinkUD();
            }
        }
        relinkLR();
    }
}
```

## Сравнение подходов

| Подход | Время выполнения | Сложность реализации | Когда использовать |
|--------|------------------|---------------------|-------------------|
| **Backtracking** | ~250 мс | Простая | Стандартные головоломки |
| **Dancing Links** | ~50 мс | Сложная | Сложные головоломки, оптимизация |

Алгоритм возврата занимает около `250` мс, чтобы решить доску.

Если мы сравним это с **Dancing Links**, который занимает около 50 мс, мы увидим явного победителя. **Dancing Links** примерно в пять раз быстрее при решении этого конкретного примера.

## Реализация на Kotlin

### Класс SudokuSolver

```kotlin
// Kotlin-решатель судоку: backtracking с проверкой ограничений
class SudokuSolverK(private var board: Array<IntArray>) {
    companion object {
        const val BOARD_SIZE = 9
        const val SUBSECTION_SIZE = 3
        const val NO_VALUE = 0
        const val MIN_VALUE = 1
        const val MAX_VALUE = 9
    }

    // Рекурсивно заполняем пустые клетки, откатываясь при нарушениях
    fun solve(): Boolean {
        for (row in 0 until BOARD_SIZE) {
            for (column in 0 until BOARD_SIZE) {
                if (board[row][column] == NO_VALUE) {
                    for (k in MIN_VALUE..MAX_VALUE) {
                        board[row][column] = k
                        if (isValid(board, row, column) && solve()) {
                            return true
                        }
                        board[row][column] = NO_VALUE
                    }
                    return false
                }
            }
        }
        return true
    }

    // Проверка строки, столбца и блока 3×3
    private fun isValid(board: Array<IntArray>, row: Int, column: Int): Boolean {
        return rowConstraint(board, row) &&
               columnConstraint(board, column) &&
               subsectionConstraint(board, row, column)
    }

    // Проверяем отсутствие дубликатов в строке
    private fun rowConstraint(board: Array<IntArray>, row: Int): Boolean {
        val constraint = BooleanArray(BOARD_SIZE)
        return (0 until BOARD_SIZE).all { column ->
            checkConstraint(board, row, constraint, column)
        }
    }

    // Проверяем отсутствие дубликатов в столбце
    private fun columnConstraint(board: Array<IntArray>, column: Int): Boolean {
        val constraint = BooleanArray(BOARD_SIZE)
        return (0 until BOARD_SIZE).all { row ->
            checkConstraint(board, row, constraint, column)
        }
    }

    // Проверяем блок 3×3
    private fun subsectionConstraint(board: Array<IntArray>, row: Int, column: Int): Boolean {
        val constraint = BooleanArray(BOARD_SIZE)
        val subsectionRowStart = (row / SUBSECTION_SIZE) * SUBSECTION_SIZE
        val subsectionRowEnd = subsectionRowStart + SUBSECTION_SIZE
        val subsectionColumnStart = (column / SUBSECTION_SIZE) * SUBSECTION_SIZE
        val subsectionColumnEnd = subsectionColumnStart + SUBSECTION_SIZE

        for (r in subsectionRowStart until subsectionRowEnd) {
            for (c in subsectionColumnStart until subsectionColumnEnd) {
                if (!checkConstraint(board, r, constraint, c)) {
                    return false
                }
            }
        }
        return true
    }

    // Помечаем значение; при повторе возвращаем false
    private fun checkConstraint(
        board: Array<IntArray>,
        row: Int,
        constraint: BooleanArray,
        column: Int
    ): Boolean {
        if (board[row][column] != NO_VALUE) {
            if (!constraint[board[row][column] - 1]) {
                constraint[board[row][column] - 1] = true
            } else {
                return false
            }
        }
        return true
    }

    fun getBoard(): Array<IntArray> = board // возвращаем итоговую доску
}
```

### Пример использования (Kotlin)

```kotlin
fun main() {
    val board = arrayOf(
        intArrayOf(8, 0, 0, 0, 0, 0, 0, 0, 0),
        intArrayOf(0, 0, 3, 6, 0, 0, 0, 0, 0),
        intArrayOf(0, 7, 0, 0, 9, 0, 2, 0, 0),
        intArrayOf(0, 5, 0, 0, 0, 7, 0, 0, 0),
        intArrayOf(0, 0, 0, 0, 4, 5, 7, 0, 0),
        intArrayOf(0, 0, 0, 1, 0, 0, 0, 3, 0),
        intArrayOf(0, 0, 1, 0, 0, 0, 0, 6, 8),
        intArrayOf(0, 0, 8, 5, 0, 0, 0, 1, 0),
        intArrayOf(0, 9, 0, 0, 0, 0, 4, 0, 0)
    )

    val solver = SudokuSolverK(board)
    if (solver.solve()) {
        println("Sudoku solved!")
        solver.getBoard().forEach { row ->
            println(row.joinToString(" "))
        }
    } else {
        println("No solution found")
    }
}
```

## Сложность

### Временная сложность

У `backtracking` худший случай — перебор `9^m`, где `m` пустых ячеек, поэтому на разреженных досках время растёт экспоненциально. `Dancing Links` остаётся в том же порядке, но выигрывает на константах и раннем отсечении благодаря выбору столбца с минимальным числом единиц.

### Пространственная сложность

`Backtracking` хранит только доску и стек рекурсии (`O(1)` сверх данных). `Dancing Links` строит матрицу точного покрытия размером порядка `9^3` элементов (для стандартной 9×9) плюс ссылки между узлами, поэтому потребление памяти выше, но управляемо для классической задачи.

## Особенности

`Backtracking` выигрывает в простоте и понятности, его легко написать и отладить даже на интервью. `Dancing Links` требует больше кода, но даёт резкий прирост скорости на сложных досках, потому что быстро исключает невозможные варианты. Оба подхода работают для классической сетки 9×9, но DLX легче переносить на вариации точного покрытия.

## Применение

Решатели судоку используются в играх и мобильных приложениях, в обучении алгоритмам поиска и точного покрытия, в тестах для движков ограничений, а также как лабораторный пример для `Algorithm X` и структуры `Dancing Links`. В аналитике они служат учебным примером для оценки переборов и эвристик.

## Варианты задачи

### Вариант 1: Проверка валидности доски

```java
public boolean isValidBoard(int[][] board) {
    // Проверяем каждую строку и столбец на дубликаты
    for (int i = 0; i < BOARD_SIZE; i++) {
        if (!rowConstraint(board, i) || !columnConstraint(board, i)) {
            return false;
        }
    }
    // Проверяем все блоки 3×3
    for (int i = 0; i < BOARD_SIZE; i += SUBSECTION_SIZE) {
        for (int j = 0; j < BOARD_SIZE; j += SUBSECTION_SIZE) {
            if (!subsectionConstraint(board, i, j)) {
                return false;
            }
        }
    }
    return true;
}
```

### Вариант 2: Генерация судоку

```java
public int[][] generateSudoku(int difficulty) {
    int[][] board = new int[BOARD_SIZE][BOARD_SIZE];
    solve(); // Заполняем полную доску (предполагается отдельный solve() для генерации)

    // Удаляем числа в зависимости от сложности — чем больше, тем труднее пазл
    int cellsToRemove = difficulty * 10;
    Random random = new Random();

    while (cellsToRemove > 0) {
        int row = random.nextInt(BOARD_SIZE);
        int col = random.nextInt(BOARD_SIZE);
        if (board[row][col] != NO_VALUE) { // вырезаем только заполненные клетки
            board[row][col] = NO_VALUE;
            cellsToRemove--;
        }
    }
    return board;
}
```

## Когда использовать

`Backtracking` выбирайте, когда нужен понятный базовый решатель, важна читабельность и простота поддержки, а головоломки стандартные. `Dancing Links` имеет смысл для сложных задач или больших батчей, где критична скорость: он дороже в реализации, но окупается при массовом решении или при генерации/валидации множества досок.

## Лучшие практики

Начинайте с валидации входа: если в строках, столбцах или блоках уже есть дубликаты, перебор бессмысленен. В `backtracking` отсекайте ветку сразу при нарушении, не продолжая перебор всех цифр. Перед запуском попробуйте простое предзаполнение клеток с единственным вариантом — это резко снижает глубину поиска. `Dancing Links` подключайте, когда решаете много досок или сложные экземпляры, а для единичных задач обычно достаточно классического перебора. В тестах держите кейсы для неразрешимой доски, уже решённой, пустой и уменьшенных размеров (например 4×4, если поддерживаете).

## Тестирование

### JUnit: решаемость и неразрешимость

```java
@Test
void solver_solvesKnownBoard() {
    int[][] board = { /* ... классическая задача ... */ };
    SudokuSolver solver = new SudokuSolver(board);
    assertTrue(solver.solve()); // ожидаем решение для валидной доски
}

@Test
void solver_rejectsInvalidBoard() {
    int[][] invalid = { /* ... доска с дубликатами ... */ };
    SudokuSolver solver = new SudokuSolver(invalid);
    assertFalse(solver.isValidBoard(invalid)); // ранний отказ на некорректном входе
}
```

### Kotlin: проверка результата

```kotlin
@Test
fun `solver returns solved board`() {
    val board = arrayOf(/* ... 9x9 ... */)
    val solver = SudokuSolverK(board)
    assertTrue(solver.solve())
    assertTrue(solver.getBoard().all { row -> row.none { it == 0 } }) // нет пустых клеток
}
```

### Property-based (kotest)

```kotlin
@Property
fun `solution respects constraints`(@ForAll board: Array<IntArray>) {
    assumeTrue(board.size == 9 && board.all { it.size == 9 })
    val solver = SudokuSolverK(board.map { it.clone() }.toTypedArray())
    if (solver.solve()) {
        solver.getBoard().forEachIndexed { r, row ->
            row.forEachIndexed { c, _ ->
                assertTrue(solver.isValidBoard(solver.getBoard()), "constraints hold at $r,$c")
            }
        }
    }
}
```

## Решение проблем

- Решение не находится: проверьте валидность входа; для DLX убедитесь, что матрица точного покрытия построена без пропущенных столбцов.
- Время слишком велико: включите предзаполнение одиночных кандидатов, снизьте глубину поиска эвристикой «наименьшего количества кандидатов».
- Память растёт: для DLX избегайте сохранения всех решений; храните только текущее и лучшее.
- Ответ неверный: добавьте логирование шагов или сохранение снимков доски при откате; чаще ошибка в `checkConstraint` или индексах блока.

## Частые вопросы

**Нужен ли DLX для обычных пазлов?** Обычно нет; `backtracking` справляется, DLX выгоден на сложных или массовых задачах.
**Как выбирать порядок заполнения?** Берите клетку с минимальным числом допустимых значений — это уменьшает ветвление.
**Поддерживает ли код нестандартные размеры?** Принципы подходят, но константы `BOARD_SIZE` и `SUBSECTION_SIZE` нужно менять согласованно.
**Можно ли искать несколько решений?** Да, достаточно не выходить при первом решении и собирать варианты, но следите за временем.
**Как профилировать?** Используйте JMH для Java и measureTimeMillis для Kotlin, фиксируйте seed и входные доски.

## Глоссарий

`Backtracking` — перебор с откатом, рекурсивный поиск по пустым клеткам.
`Dancing Links` — структура связей для эффективного скрытия/раскрытия столбцов в `Algorithm X`.
`Algorithm X` — нерекурсивная формулировка поиска по задаче точного покрытия.
`Exact Cover` — задача выбора строк матрицы, чтобы каждый столбец был покрыт ровно один раз.
`Constraint propagation` — сокращение поиска путём удаления невозможных значений до перебора.

## Дополнительные примеры использования

### Пример 1: Валидация ввода

```java
// Проверяем, что числа в диапазоне 0..9 и нет дубликатов в строке
public boolean validateInput(int[][] board) {
    for (int r = 0; r < BOARD_SIZE; r++) {
        for (int c = 0; c < BOARD_SIZE; c++) {
            int v = board[r][c];
            if (v < 0 || v > 9) return false;
        }
        if (!rowConstraint(board, r)) return false;
    }
    return true;
}
```

### Пример 2: Мини-доска 4x4

```java
// Поддержка 4×4: достаточно изменить константы и логику блока 2×2
static final int MINI_SIZE = 4;
static final int MINI_BLOCK = 2;

boolean isValid4x4(int[][] board, int row, int col) {
    // Аналогичные проверки для 4×4
    return true;
}
```

### Пример 3: Быстрый рантайм-тест

```java
@State(Scope.Benchmark)
public class SudokuBench {
    int[][] board;
    @Setup
    public void setup() { board = /* загрузка тестовой доски */; }
    @Benchmark
    public boolean solveBench() { return new SudokuSolver(board).solve(); }
}
```

### Пример 4: CLI/логирование

```java
// Печать доски для отладки CLI
void printBoard(int[][] board) {
    for (int r = 0; r < BOARD_SIZE; r++) {
        System.out.println(Arrays.toString(board[r]));
    }
}
```

## Заключение

`Backtracking` даёт быстрый старт и читабельность, `Dancing Links` — ускорение на сложных досках ценой большей сложности кода. Оба метода решают классическую задачу 9×9, а добавленные тесты, валидаторы и чек-лист помогают держать качество кода и документации. Чтобы улучшить решатель, попробуйте эвристику выбора клетки с минимумом кандидатов, интегрируйте простую пропагацию ограничений и добавьте метрики времени для разных наборов пазлов.
