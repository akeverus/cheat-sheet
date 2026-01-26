# Maze Solver

Кратко: решение лабиринтов с использованием алгоритмов поиска в глубину (DFS) и поиска в ширину (BFS). Рассматривается нахождение пути от входа к выходу и поиск кратчайшего пути.

**Дата последнего обновления:** 2025-01-15

## Полезные ссылки

### Официальная документация
- [Baeldung: Maze Solver](https://www.baeldung.com/java-solve-maze)

### См. также
- `./a-star-pathfinding.md` - поиск пути A*
- `../graphs/dijkstra-algorithm.md` - алгоритм Дейкстры
- `../graphs/breadth-first-search.md` - поиск в ширину

## Содержание

- [Описание алгоритма](#описание-алгоритма)
- [Представление лабиринта](#представление-лабиринта)
- [Java Implementation](#java-implementation)
- [Kotlin Implementation](#kotlin-implementation)
- [Сравнение подходов](#сравнение-подходов)
- [Сложность](#сложность)

## Описание алгоритма

В этой статье мы рассмотрим возможные способы навигации по лабиринту с помощью Java.

Считайте лабиринт черно-белым изображением, где черные пиксели представляют собой стены, а белые пиксели представляют путь. Два белых пикселя являются особыми, один - это вход в лабиринт, а другой - выход.

Учитывая такой лабиринт, мы хотим найти путь от входа к выходу.

## Представление лабиринта

Мы будем рассматривать лабиринт как двумерный целочисленный массив. Значение числовых значений в массиве будет соответствовать следующему соглашению:

1. **0** - Дорога
2. **1** - Стена
3. **2** - Вход в лабиринт
4. **3** - Выход из лабиринта
5. **4** - Ячейка части пути от входа до выхода

Мы смоделируем лабиринт в виде графа. Вход и выход - это два специальных узла, между которыми необходимо определить путь.

Типичный граф имеет два свойства: узлы и ребра. Ребро определяет связность графа и связывает один узел с другим.

Следовательно, мы предполагаем четыре неявных ребра из каждого узла, связывающих данный узел с его левым, правым, верхним и нижним узлами.

### Класс Maze

```java
public class Maze {
    private static final int ROAD = 0;
    private static final int WALL = 1;
    private static final int ENTRANCE = 2;
    private static final int EXIT = 3;
    private static final int PATH = 4;
    
    private int[][] maze;
    private boolean[][] visited;
    private Coordinate entrance;
    private Coordinate exit;
    
    public Maze(int[][] maze) {
        this.maze = maze;
        this.visited = new boolean[maze.length][maze[0].length];
        findEntranceAndExit();
    }
    
    public boolean isValidLocation(int row, int col) {
        return row >= 0 && row < maze.length &&
               col >= 0 && col < maze[0].length;
    }
    
    public boolean isWall(int row, int col) {
        return maze[row][col] == WALL;
    }
    
    public boolean isExit(int row, int col) {
        return maze[row][col] == EXIT;
    }
    
    public boolean isExplored(int row, int col) {
        return visited[row][col];
    }
    
    public void setVisited(int row, int col, boolean visited) {
        this.visited[row][col] = visited;
    }
    
    public Coordinate getEntry() {
        return entrance;
    }
    
    private void findEntranceAndExit() {
        for (int i = 0; i < maze.length; i++) {
            for (int j = 0; j < maze[0].length; j++) {
                if (maze[i][j] == ENTRANCE) {
                    entrance = new Coordinate(i, j);
                }
                if (maze[i][j] == EXIT) {
                    exit = new Coordinate(i, j);
                }
            }
        }
    }
}
```

### Класс Coordinate

```java
public class Coordinate {
    private int x;
    private int y;
    public Coordinate parent;
    
    public Coordinate(int x, int y) {
        this.x = x;
        this.y = y;
    }
    
    public Coordinate(int x, int y, Coordinate parent) {
        this.x = x;
        this.y = y;
        this.parent = parent;
    }
    
    public int getX() {
        return x;
    }
    
    public int getY() {
        return y;
    }
}
```

## Java Implementation

### Подход 1: Поиск в глубину (DFS)

Один довольно очевидный подход состоит в том, чтобы исследовать все возможные пути, которые в конечном итоге найдут путь, если он существует. Но такой подход будет иметь экспоненциальную сложность и не будет хорошо масштабироваться.

Тем не менее, можно настроить упомянутое выше решение грубой силы, отслеживая и отмечая посещенные узлы, чтобы получить путь за разумное время. Этот алгоритм также известен как поиск в глубину.

### Алгоритм DFS

Этот алгоритм можно описать так:

1. Если мы у стены или уже посещенного узла, вернуть отказ
2. В противном случае, если мы являемся выходным узлом, верните успех
3. В противном случае добавьте узел в список путей и рекурсивно перемещайтесь во всех четырех направлениях. Если возвращается ошибка, удалите узел из пути и верните ошибку. Список путей будет содержать уникальный путь, когда выход будет найден

### Реализация DFS

Во-первых, нам нужно определить четыре направления. Мы можем определить это в терминах координат:

```java
public class MazeSolverDFS {
    private static final int[][] DIRECTIONS = {
        {0, 1},   // вправо
        {1, 0},   // вниз
        {0, -1},  // влево
        {-1, 0}   // вверх
    };
    
    private Coordinate getNextCoordinate(int row, int col, int i, int j) {
        return new Coordinate(row + i, col + j);
    }
    
    public List<Coordinate> solve(Maze maze) {
        List<Coordinate> path = new ArrayList<>();
        
        if (explore(maze, maze.getEntry().getX(), maze.getEntry().getY(), path)) {
            return path;
        }
        
        return Collections.emptyList();
    }
    
    private boolean explore(Maze maze, int row, int col, List<Coordinate> path) {
        if (!maze.isValidLocation(row, col) || 
            maze.isWall(row, col) || 
            maze.isExplored(row, col)) {
            return false;
        }
        
        path.add(new Coordinate(row, col));
        maze.setVisited(row, col, true);
        
        if (maze.isExit(row, col)) {
            return true;
        }
        
        for (int[] direction : DIRECTIONS) {
            Coordinate coordinate = getNextCoordinate(
                row, col, direction[0], direction[1]
            );
            
            if (explore(maze, coordinate.getX(), coordinate.getY(), path)) {
                return true;
            }
        }
        
        path.remove(path.size() - 1);
        return false;
    }
}
```

Это решение использует размер стека до размера лабиринта.

## Подход 2: Поиск в ширину (BFS)

Описанный выше рекурсивный алгоритм находит путь, но это не обязательно кратчайший путь. Чтобы найти кратчайший путь, мы можем использовать другой подход обхода графа, известный как поиск в ширину.

В DFS сначала исследовались один дочерний элемент и все его внуки, а затем переходили к другому дочернему элементу. В то время как в BFS мы исследуем всех непосредственных потомков, прежде чем переходить к внукам. Это гарантирует, что все узлы на определенном расстоянии от родительского узла будут исследованы одновременно.

### Алгоритм BFS

Алгоритм можно изложить следующим образом:

1. Добавить начальный узел в очередь
2. Пока очередь не пуста, извлеките узел, сделайте следующее:
3. Если мы достигаем стены или узел уже посещен, переходим к следующей итерации
4. Если выходной узел достигнут, вернуться от текущего узла к начальному узлу, чтобы найти кратчайший путь
5. В противном случае добавьте в очередь всех непосредственных соседей в четырех направлениях

Здесь важно то, что узлы должны отслеживать своего родителя, т.е. откуда они были добавлены в очередь. Это важно, чтобы найти путь, когда встретится выходной узел.

### Реализация BFS

Давайте сначала определим служебный метод для возврата от заданного узла к его корню:

```java
public class MazeSolverBFS {
    private static final int[][] DIRECTIONS = {
        {0, 1}, {1, 0}, {0, -1}, {-1, 0}
    };
    
    private List<Coordinate> backtrackPath(Coordinate cur) {
        List<Coordinate> path = new ArrayList<>();
        Coordinate iter = cur;
        
        while (iter != null) {
            path.add(iter);
            iter = iter.parent;
        }
        
        Collections.reverse(path);
        return path;
    }
    
    public List<Coordinate> solve(Maze maze) {
        LinkedList<Coordinate> nextToVisit = new LinkedList<>();
        Coordinate start = maze.getEntry();
        nextToVisit.add(start);
        
        while (!nextToVisit.isEmpty()) {
            Coordinate cur = nextToVisit.remove();
            
            if (!maze.isValidLocation(cur.getX(), cur.getY()) || 
                maze.isExplored(cur.getX(), cur.getY())) {
                continue;
            }
            
            if (maze.isWall(cur.getX(), cur.getY())) {
                maze.setVisited(cur.getX(), cur.getY(), true);
                continue;
            }
            
            if (maze.isExit(cur.getX(), cur.getY())) {
                return backtrackPath(cur);
            }
            
            for (int[] direction : DIRECTIONS) {
                Coordinate coordinate = new Coordinate(
                    cur.getX() + direction[0],
                    cur.getY() + direction[1],
                    cur
                );
                nextToVisit.add(coordinate);
                maze.setVisited(cur.getX(), cur.getY(), true);
            }
        }
        
        return Collections.emptyList();
    }
}
```

## Сравнение подходов

| Подход | Время выполнения | Находит кратчайший путь | Использование памяти |
|--------|------------------|------------------------|---------------------|
| DFS | Зависит от лабиринта | Нет | O(h) - где h - глубина |
| BFS | O(V + E) | Да | O(V) - где V - количество узлов |

### Когда использовать DFS

- Когда нужно найти любой путь
- Когда важна экономия памяти
- Когда лабиринт очень большой

### Когда использовать BFS

- Когда нужен кратчайший путь
- Когда важна гарантия оптимальности
- Когда лабиринт среднего размера

## Kotlin Implementation

### Класс Maze

```kotlin
data class CoordinateK(val x: Int, val y: Int, var parent: CoordinateK? = null)

class MazeK(private val maze: Array<IntArray>) {
    companion object {
        const val ROAD = 0
        const val WALL = 1
        const val ENTRANCE = 2
        const val EXIT = 3
        const val PATH = 4
    }
    
    private val visited = Array(maze.size) { BooleanArray(maze[0].size) }
    private var entrance: CoordinateK? = null
    private var exit: CoordinateK? = null
    
    init {
        findEntranceAndExit()
    }
    
    fun isValidLocation(row: Int, col: Int): Boolean {
        return row >= 0 && row < maze.size && col >= 0 && col < maze[0].size
    }
    
    fun isWall(row: Int, col: Int): Boolean = maze[row][col] == WALL
    fun isExit(row: Int, col: Int): Boolean = maze[row][col] == EXIT
    fun isExplored(row: Int, col: Int): Boolean = visited[row][col]
    fun setVisited(row: Int, col: Int, visited: Boolean) {
        this.visited[row][col] = visited
    }
    fun getEntry(): CoordinateK? = entrance
    
    private fun findEntranceAndExit() {
        for (i in maze.indices) {
            for (j in maze[0].indices) {
                when (maze[i][j]) {
                    ENTRANCE -> entrance = CoordinateK(i, j)
                    EXIT -> exit = CoordinateK(i, j)
                }
            }
        }
    }
}
```

### DFS решение

```kotlin
class MazeSolverDFSK {
    private val DIRECTIONS = arrayOf(
        intArrayOf(0, 1),   // вправо
        intArrayOf(1, 0),   // вниз
        intArrayOf(0, -1),  // влево
        intArrayOf(-1, 0)   // вверх
    )
    
    fun solve(maze: MazeK): List<CoordinateK> {
        val path = mutableListOf<CoordinateK>()
        val entry = maze.getEntry() ?: return emptyList()
        
        if (explore(maze, entry.x, entry.y, path)) {
            return path
        }
        
        return emptyList()
    }
    
    private fun explore(maze: MazeK, row: Int, col: Int, path: MutableList<CoordinateK>): Boolean {
        if (!maze.isValidLocation(row, col) || 
            maze.isWall(row, col) || 
            maze.isExplored(row, col)) {
            return false
        }
        
        path.add(CoordinateK(row, col))
        maze.setVisited(row, col, true)
        
        if (maze.isExit(row, col)) {
            return true
        }
        
        for (direction in DIRECTIONS) {
            val next = CoordinateK(row + direction[0], col + direction[1])
            if (explore(maze, next.x, next.y, path)) {
                return true
            }
        }
        
        path.removeAt(path.size - 1)
        return false
    }
}
```

### BFS решение

```kotlin
import java.util.*

class MazeSolverBFSK {
    private val DIRECTIONS = arrayOf(
        intArrayOf(0, 1), intArrayOf(1, 0), intArrayOf(0, -1), intArrayOf(-1, 0)
    )
    
    fun solve(maze: MazeK): List<CoordinateK> {
        val entry = maze.getEntry() ?: return emptyList()
        val queue: Queue<CoordinateK> = LinkedList()
        queue.add(entry)
        maze.setVisited(entry.x, entry.y, true)
        
        while (queue.isNotEmpty()) {
            val current = queue.poll()
            
            if (maze.isExit(current.x, current.y)) {
                return reconstructPath(current)
            }
            
            for (direction in DIRECTIONS) {
                val next = CoordinateK(
                    current.x + direction[0],
                    current.y + direction[1],
                    current
                )
                
                if (maze.isValidLocation(next.x, next.y) &&
                    !maze.isWall(next.x, next.y) &&
                    !maze.isExplored(next.x, next.y)) {
                    queue.add(next)
                    maze.setVisited(next.x, next.y, true)
                }
            }
        }
        
        return emptyList()
    }
    
    private fun reconstructPath(end: CoordinateK): List<CoordinateK> {
        val path = mutableListOf<CoordinateK>()
        var current: CoordinateK? = end
        
        while (current != null) {
            path.add(current)
            current = current.parent
        }
        
        return path.reversed()
    }
}
```

### Пример использования

```kotlin
fun main() {
    val mazeArray = arrayOf(
        intArrayOf(2, 0, 0, 1, 0),
        intArrayOf(0, 1, 0, 1, 0),
        intArrayOf(0, 0, 0, 0, 0),
        intArrayOf(1, 1, 0, 1, 0),
        intArrayOf(0, 0, 0, 0, 3)
    )
    
    val maze = MazeK(mazeArray)
    
    val dfsSolver = MazeSolverDFSK()
    val dfsPath = dfsSolver.solve(maze)
    println("DFS path length: ${dfsPath.size}")
    
    val maze2 = MazeK(mazeArray)
    val bfsSolver = MazeSolverBFSK()
    val bfsPath = bfsSolver.solve(maze2)
    println("BFS path length: ${bfsPath.size}")
}
```

## Сложность

### Временная сложность

- **DFS:** O(V + E) - где V - количество узлов, E - количество ребер
- **BFS:** O(V + E) - где V - количество узлов, E - количество ребер

### Пространственная сложность

- **DFS:** O(h) - где h - максимальная глубина рекурсии
- **BFS:** O(V) - где V - количество узлов в худшем случае

## Особенности

- **Простота:** Оба алгоритма относительно просты в реализации
- **Оптимальность:** BFS гарантирует кратчайший путь
- **Гибкость:** Легко адаптировать для различных типов лабиринтов

## Применение

Решение лабиринтов используется в:

- Играх (NPC навигация)
- Робототехнике
- Сетевых алгоритмах
- ИИ и планировании
- Обучении алгоритмам

## Варианты задачи

### Вариант 1: Поиск всех путей

```java
public List<List<Coordinate>> findAllPaths(Maze maze) {
    List<List<Coordinate>> allPaths = new ArrayList<>();
    List<Coordinate> currentPath = new ArrayList<>();
    
    findAllPathsDFS(maze, maze.getEntry().getX(), 
                    maze.getEntry().getY(), 
                    currentPath, allPaths);
    
    return allPaths;
}

private void findAllPathsDFS(Maze maze, int row, int col, 
                            List<Coordinate> currentPath,
                            List<List<Coordinate>> allPaths) {
    if (!maze.isValidLocation(row, col) || 
        maze.isWall(row, col) || 
        maze.isExplored(row, col)) {
        return;
    }
    
    currentPath.add(new Coordinate(row, col));
    maze.setVisited(row, col, true);
    
    if (maze.isExit(row, col)) {
        allPaths.add(new ArrayList<>(currentPath));
    } else {
        for (int[] direction : DIRECTIONS) {
            Coordinate next = getNextCoordinate(row, col, 
                                               direction[0], direction[1]);
            findAllPathsDFS(maze, next.getX(), next.getY(), 
                          currentPath, allPaths);
        }
    }
    
    currentPath.remove(currentPath.size() - 1);
    maze.setVisited(row, col, false);
}
```

### Вариант 2: Поиск пути с препятствиями

```java
public List<Coordinate> solveWithObstacles(Maze maze, 
                                          Set<Coordinate> obstacles) {
    // Модифицировать isValidLocation для проверки препятствий
    // Остальная логика остается той же
}
```

## Когда использовать

### Используйте DFS, когда:

- Нужен любой путь
- Важна экономия памяти
- Лабиринт очень большой

### Используйте BFS, когда:

- Нужен кратчайший путь
- Важна гарантия оптимальности
- Лабиринт среднего размера

## Заключение

В этом руководстве мы описали два основных графовых алгоритма: поиск в глубину и поиск в ширину для решения лабиринта. Мы также коснулись того, как BFS определяет кратчайший путь от входа до выхода.

Для дальнейшего чтения поищите другие методы решения лабиринта, такие как A* и алгоритм Дейкстры.
