---
title: "Решение лабиринта (Maze Solver)"
description: "Поиск пути от входа до выхода в лабиринте (сетка с стенами): DFS — любой путь, экономия памяти; BFS — кратчайший путь при единичном шаге. Представление лабиринта, классы Maze и Coordinate, Java и Kotlin."
tags:
  - algorithms
  - problems
  - maze-solver
difficulty: "intermediate"
prerequisites: []
next: []
updated: "2026-02-11"
---
# Решение лабиринта (`Maze Solver`)

Поиск пути от входа до выхода в лабиринте (сетка с стенами): DFS — любой путь, экономия памяти; BFS — кратчайший путь при единичном шаге. Представление лабиринта, классы Maze и Coordinate, Java и Kotlin.

## Полезные ссылки

### Официальная документация
- [`Baeldung`: `Maze Solver`](https://www.baeldung.com/)

### См. также
- [[a-star-pathfinding|Поиск пути A*]] — A*
- [[dijkstra|Алгоритм Дейкстры]] — Dijkstra
- [[bfs|Поиск в ширину (BFS)]]

## Содержание

- [Описание алгоритма](#описание-алгоритма)
- [Представление лабиринта](#представление-лабиринта)
- [Реализация на Java](#реализация-на-java)
- [Подход 2: BFS](#подход-2-поиск-в-ширину-bfs)
- [Сравнение подходов](#сравнение-подходов)
- [Реализация на Kotlin](#реализация-на-kotlin)
- [Сложность](#сложность)
- [Особенности](#особенности)
- [Применение](#применение)
- [Варианты задачи](#варианты-задачи)
- [Когда использовать](#когда-использовать)
- [Лучшие практики](#лучшие-практики)
- [Решение проблем](#решение-проблем)
- [Частые вопросы](#частые-вопросы)
- [Заключение](#заключение)


## Описание алгоритма

Лабиринт — сетка (например, изображение или матрица): стены, дороги, одна ячейка — вход, одна — выход. Нужно найти путь от входа до выхода; при единичной стоимости шага кратчайший путь даёт BFS, любой путь — DFS.

## Представление лабиринта

Двумерный массив: 0 — дорога, 1 — стена, 2 — вход, 3 — выход, 4 — помеченная часть пути. Граф неявный: из каждой ячейки четыре соседа (вверх, вниз, влево, вправо). Класс `Maze` хранит сетку, массив посещённых ячеек и координаты входа/выхода; `Coordinate` — пара (x, y) и ссылка на родителя для восстановления пути (BFS).

```java
// Лабиринт: сетка (ROAD/WALL/ENTRANCE/EXIT), посещённые ячейки и координаты входа/выхода
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

```java
// Координата ячейки и родитель для восстановления пути в BFS
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

## Реализация на Java

DFS: рекурсивный обход из входа; если текущая ячейка — стена или уже посещена — возврат false; если выход — true; иначе помечаем посещённой, добавляем в путь, рекурсивно пробуем четыре направления, при неудаче откатываем путь и пометку. Четыре направления задаются смещениями (0,1), (1,0), (0,-1), (-1,0).

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

BFS: очередь узлов; из начала добавляем вход. Пока очередь не пуста: извлекаем узел; если стена или уже посещён — пропускаем; если выход — восстанавливаем путь по полю parent до старта; иначе помечаем посещённым и добавляем в очередь всех допустимых соседей (каждый с ссылкой parent на текущий узел). Кратчайший путь восстанавливается обходом parent от выхода к входу.

```java
// BFS: очередь, восстановление кратчайшего пути по parent
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

| Подход | Время | Кратчайший путь | Память |
|--------|--------|------------------|--------|
| DFS | O(V+E) | Нет | O(h) — глубина стека |
| BFS | O(V+E) | Да | O(V) |

## Реализация на Kotlin

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

Время DFS и BFS: O(V+E). Память DFS — O(h) по глубине стека; BFS — O(V) по очереди.

## Особенности

Оба алгоритма просты в реализации. BFS при единичном шаге даёт кратчайший путь; DFS экономит память. Легко адаптировать под другие типы сеток и правил перехода.

## Применение

Игры (NPC), робототехника, сетевые алгоритмы, ИИ и планирование, обучение алгоритмам.

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

DFS — когда достаточно любого пути и важна экономия памяти или лабиринт очень большой. BFS — когда нужен кратчайший путь при единичном шаге.

## Лучшие практики

Помечайте посещённые ячейки до рекурсии (DFS) или при добавлении в очередь (BFS); в DFS снимайте пометку при откате. Проверяйте границы и тип ячейки (стена/дорога); константы — в именованные константы. При больших сетках: итеративный DFS со стеком или учёт глубины/размера очереди. Тесты: нет пути, вход = выход, один путь, тупики.

## Решение проблем

| Симптом | Возможная причина | Решение |
|---------|------------------|---------|
| StackOverflowError в DFS | Слишком глубокая рекурсия | Перейти на итеративный DFS со стеком или увеличить лимит стека |
| BFS не находит путь при его наличии | Не сбрасывается visited между запусками или ошибка границ | Убедиться, что visited инициализирован под текущий лабиринт; проверять isValidLocation до доступа к ячейке |
| Путь не кратчайший при BFS | Пометили посещённым после извлечения из очереди или добавляем дубликаты | Помечать при добавлении в очередь; не добавлять в очередь уже посещённые ячейки |

## Частые вопросы

**Почему BFS даёт кратчайший путь?** При единичной стоимости шага первый достигший выхода обход в ширину гарантированно прошёл минимальное число рёбер; восстановление по parent даёт этот путь.

**Когда использовать A* вместо BFS?** Когда шаги имеют разную стоимость или нужна эвристика (например, движение по диагонали дороже); для простой сетки с шагом 1 BFS проще и достаточен.

**Нужно ли снимать пометку visited в DFS при откате?** Да, если ищем один путь и откатываем путь — иначе другие ветки не смогут пройти через эту ячейку. Если ищем все пути — снимать обязательно.

## Заключение

DFS и BFS — базовые способы обхода лабиринта: DFS даёт любой путь и экономит память, BFS — кратчайший путь. Для взвешенных графов или эвристики см. A* и Дейкстру.
