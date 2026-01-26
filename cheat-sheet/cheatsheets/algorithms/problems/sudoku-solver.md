# Sudoku Solver

Кратко: решение головоломки судоку с использованием алгоритма поиска с возвратом (backtracking) и техники Dancing Links. Рассматриваются два подхода: простой backtracking и алгоритм X с Dancing Links.

**Дата последнего обновления:** 2025-01-15

## Полезные ссылки

### Официальная документация
- [Baeldung: Sudoku Solver](https://www.baeldung.com/java-sudoku)

### См. также
- `./traveling-salesman-problem.md` - задача коммивояжера
- `./knapsack-problem.md` - задача о рюкзаке
- `../trees/binary-tree.md` - бинарное дерево

## Содержание

- [Описание алгоритма](#описание-алгоритма)
- [Правила судоку](#правила-судоку)
- [Java Implementation](#java-implementation)
- [Kotlin Implementation](#kotlin-implementation)
- [Сравнение подходов](#сравнение-подходов)
- [Сложность](#сложность)

## Описание алгоритма

В этой статье мы рассмотрим головоломку судоку и алгоритмы, используемые для ее решения.

Далее мы реализуем решения на Java. Первым решением будет простая атака грубой силы. Второй будет использовать технику Dancing Links.

Имейте в виду, что основное внимание мы сосредоточим на алгоритмах, а не на дизайне ООП.

## Правила судоку

Проще говоря, судоку представляет собой комбинаторную головоломку с размещением чисел с сеткой 9 x 9 ячеек, частично заполненную числами от 1 до 9. Цель состоит в том, чтобы заполнить оставшиеся пустые поля остальными числами, чтобы в каждой строке и столбце было только одно число каждого вида.

Более того, в каждом подразделе сетки размером 3 x 3 не может быть дублированного числа. Уровень сложности естественным образом повышается с увеличением количества пустых полей на каждой доске.

### Пример головоломки

Чтобы сделать наше решение более интересным и проверить алгоритм, мы собираемся использовать «самую сложную в мире доску судоку»:

```
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

Правильно решенная головоломка:

```
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

## Java Implementation

### Подход 1: Поиск с возвратом

Алгоритм поиска с возвратом пытается решить головоломку, проверяя каждую ячейку на наличие правильного решения.

Если нарушения ограничений нет, алгоритм переходит к следующей ячейке, заполняет все возможные решения и повторяет все проверки.

Если есть нарушение, то увеличивается значение ячейки. Как только значение ячейки достигает 9, и все еще есть нарушение, алгоритм возвращается к предыдущей ячейке и увеличивает значение этой ячейки.

Он пробует все возможные решения.

### Реализация

Прежде всего, давайте определим нашу доску как двумерный массив целых чисел. Мы будем использовать 0 в качестве нашей пустой ячейки:

```java
public class SudokuSolver {
    private static final int BOARD_SIZE = 9;
    private static final int SUBSECTION_SIZE = 3;
    private static final int NO_VALUE = 0;
    private static final int MIN_VALUE = 1;
    private static final int MAX_VALUE = 9;
    private static final int BOARD_START_INDEX = 0;
    
    private int[][] board;
    
    public SudokuSolver(int[][] board) {
        this.board = board;
    }
    
    public boolean solve() {
        for (int row = BOARD_START_INDEX; row < BOARD_SIZE; row++) {
            for (int column = BOARD_START_INDEX; column < BOARD_SIZE; column++) {
                if (board[row][column] == NO_VALUE) {
                    for (int k = MIN_VALUE; k <= MAX_VALUE; k++) {
                        board[row][column] = k;
                        if (isValid(board, row, column) && solve()) {
                            return true;
                        }
                        board[row][column] = NO_VALUE;
                    }
                    return false;
                }
            }
        }
        return true;
    }
}
```

Другой метод, который нам нужен, - это метод `isValid()`, который будет проверять ограничения судоку, т.е. проверять, допустимы ли строка, столбец и сетка 3 x 3:

```java
private boolean isValid(int[][] board, int row, int column) {
    return (rowConstraint(board, row)
        && columnConstraint(board, column)
        && subsectionConstraint(board, row, column));
}
```

Эти три проверки относительно похожи. Во-первых, давайте начнем с проверки строк:

```java
private boolean rowConstraint(int[][] board, int row) {
    boolean[] constraint = new boolean[BOARD_SIZE];
    return IntStream.range(BOARD_START_INDEX, BOARD_SIZE)
        .allMatch(column -> checkConstraint(board, row, constraint, column));
}
```

Далее мы используем почти идентичный код для проверки столбца:

```java
private boolean columnConstraint(int[][] board, int column) {
    boolean[] constraint = new boolean[BOARD_SIZE];
    return IntStream.range(BOARD_START_INDEX, BOARD_SIZE)
        .allMatch(row -> checkConstraint(board, row, constraint, column));
}
```

Кроме того, нам нужно проверить подраздел 3 x 3:

```java
private boolean subsectionConstraint(int[][] board, int row, int column) {
    boolean[] constraint = new boolean[BOARD_SIZE];
    int subsectionRowStart = (row / SUBSECTION_SIZE) * SUBSECTION_SIZE;
    int subsectionRowEnd = subsectionRowStart + SUBSECTION_SIZE;
    int subsectionColumnStart = (column / SUBSECTION_SIZE) * SUBSECTION_SIZE;
    int subsectionColumnEnd = subsectionColumnStart + SUBSECTION_SIZE;
    
    for (int r = subsectionRowStart; r < subsectionRowEnd; r++) {
        for (int c = subsectionColumnStart; c < subsectionColumnEnd; c++) {
            if (!checkConstraint(board, r, constraint, c)) {
                return false;
            }
        }
    }
    return true;
}
```

Наконец, нам нужен метод `checkConstraint()`:

```java
private boolean checkConstraint(int[][] board, int row, boolean[] constraint, int column) {
    if (board[row][column] != NO_VALUE) {
        if (!constraint[board[row][column] - 1]) {
            constraint[board[row][column] - 1] = true;
        } else {
            return false;
        }
    }
    return true;
}
```

### Пример использования

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

## Подход 2: Dancing Links (Алгоритм X)

Судоку можно описать как задачу точного покрытия, которая может быть представлена матрицей инцидентности, показывающей отношения между двумя объектами.

Алгоритм X представляет собой «метод проб и ошибок» для поиска всех решений проблемы точного покрытия.

Эффективной реализацией алгоритма X является алгоритм Dancing Links (сокращенно DLX), предложенный доктором Дональдом Кнутом.

### Основные концепции

Алгоритм Dancing Links работает на основе базового наблюдения, что следующая операция над двусвязными списками узлов:

```java
node.prev.next = node.next
node.next.prev = node.prev
```

удаляет узел, а:

```java
node.prev = node
node.next = node
```

восстанавливает узел.

Каждый узел в DLX связан с узлом слева, справа, вверху и внизу.

### Реализация Dancing Links

Класс `DancingNode` будет иметь все операции, необходимые для добавления или удаления узлов:

```java
class DancingNode {
    DancingNode L, R, U, D;
    ColumnNode C;
    
    DancingNode hookDown(DancingNode node) {
        assert (this.C == node.C);
        node.D = this.D;
        node.D.U = node;
        node.U = this;
        this.D = node;
        return node;
    }
    
    DancingNode hookRight(DancingNode node) {
        node.R = this.R;
        node.R.L = node;
        node.L = this;
        this.R = node;
        return node;
    }
    
    void unlinkLR() {
        this.L.R = this.R;
        this.R.L = this.L;
    }
    
    void relinkLR() {
        this.L.R = this.R.L = this;
    }
    
    void unlinkUD() {
        this.U.D = this.D;
        this.D.U = this.U;
    }
    
    void relinkUD() {
        this.U.D = this.D.U = this;
    }
    
    DancingNode() {
        L = R = U = D = this;
    }
    
    DancingNode(ColumnNode c) {
        this();
        C = c;
    }
}
```

Класс `ColumnNode` свяжет столбцы вместе:

```java
class ColumnNode extends DancingNode {
    int size;
    String name;
    
    ColumnNode(String n) {
        super();
        size = 0;
        name = n;
        C = this;
    }
    
    void cover() {
        unlinkLR();
        for (DancingNode i = this.D; i != this; i = i.D) {
            for (DancingNode j = i.R; j != i; j = j.R) {
                j.unlinkUD();
                j.C.size--;
            }
        }
    }
    
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
| Backtracking | ~250 мс | Простая | Стандартные головоломки |
| Dancing Links | ~50 мс | Сложная | Сложные головоломки, оптимизация |

Алгоритм возврата занимает около 250 мс, чтобы решить доску.

Если мы сравним это с Dancing Links, который занимает около 50 мс, мы увидим явного победителя. Dancing Links примерно в пять раз быстрее при решении этого конкретного примера.

## Kotlin Implementation

### Класс SudokuSolver

```kotlin
class SudokuSolverK(private var board: Array<IntArray>) {
    companion object {
        const val BOARD_SIZE = 9
        const val SUBSECTION_SIZE = 3
        const val NO_VALUE = 0
        const val MIN_VALUE = 1
        const val MAX_VALUE = 9
    }
    
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
    
    private fun isValid(board: Array<IntArray>, row: Int, column: Int): Boolean {
        return rowConstraint(board, row) &&
               columnConstraint(board, column) &&
               subsectionConstraint(board, row, column)
    }
    
    private fun rowConstraint(board: Array<IntArray>, row: Int): Boolean {
        val constraint = BooleanArray(BOARD_SIZE)
        return (0 until BOARD_SIZE).all { column ->
            checkConstraint(board, row, constraint, column)
        }
    }
    
    private fun columnConstraint(board: Array<IntArray>, column: Int): Boolean {
        val constraint = BooleanArray(BOARD_SIZE)
        return (0 until BOARD_SIZE).all { row ->
            checkConstraint(board, row, constraint, column)
        }
    }
    
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
    
    fun getBoard(): Array<IntArray> = board
}
```

### Пример использования

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

- **Backtracking:** O(9^m) - где m - количество пустых ячеек (в худшем случае)
- **Dancing Links:** O(9^m) - но с лучшими константами и эвристиками

### Пространственная сложность

- **Backtracking:** O(1) - только рекурсивный стек
- **Dancing Links:** O(9^3) - для матрицы точного покрытия

## Особенности

- **Простота:** Backtracking проще в реализации
- **Производительность:** Dancing Links быстрее для сложных головоломок
- **Гибкость:** Оба подхода решают стандартные головоломки 9×9

## Применение

Решение судоку используется в:

- Играх и головоломках
- Обучении алгоритмам
- Тестировании алгоритмов поиска
- Исследованиях в области ИИ

## Варианты задачи

### Вариант 1: Проверка валидности доски

```java
public boolean isValidBoard(int[][] board) {
    for (int i = 0; i < BOARD_SIZE; i++) {
        if (!rowConstraint(board, i) || !columnConstraint(board, i)) {
            return false;
        }
    }
    
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
    solve(); // Заполняем полную доску
    
    // Удаляем числа в зависимости от сложности
    int cellsToRemove = difficulty * 10;
    Random random = new Random();
    
    while (cellsToRemove > 0) {
        int row = random.nextInt(BOARD_SIZE);
        int col = random.nextInt(BOARD_SIZE);
        
        if (board[row][col] != NO_VALUE) {
            board[row][col] = NO_VALUE;
            cellsToRemove--;
        }
    }
    
    return board;
}
```

## Когда использовать

### Используйте Backtracking, когда:

- Нужна простота реализации
- Работаете со стандартными головоломками
- Важна читаемость кода

### Используйте Dancing Links, когда:

- Нужна максимальная производительность
- Работаете со сложными головоломками
- Готовы к более сложной реализации

## Заключение

В этом уроке мы обсудили два решения головоломки судоку с ядром Java. Алгоритм поиска с возвратом, который представляет собой алгоритм грубой силы, может легко решить стандартную головоломку 9×9.

Также обсуждался немного более сложный алгоритм Dancing Links. Оба решают самые сложные головоломки за считанные секунды.
