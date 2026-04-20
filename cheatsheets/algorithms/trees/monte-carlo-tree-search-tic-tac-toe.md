---
title: "Поиск по дереву Монте-Карло (Monte Carlo Tree Search, MCTS)"
description: "Поиск по дереву Монте-Карло (MCTS) — это алгоритм вероятностного поиска для принятия решений в играх и других задачах с большим пространством состояний. Состоит из четырёх фаз: Selection (выбор), Expansion (расширение), Simulation (симуляция), Backpropagation (обратное распростра"
tags:
  - algorithms
  - trees
  - monte-carlo-tree-search-tic-tac-toe
difficulty: "intermediate"
prerequisites: []
next: []
updated: "2026-02-11"
---
# Поиск по дереву Монте-Карло (`Monte Carlo Tree Search`, `MCTS`)

Поиск по дереву Монте-Карло (`MCTS`) — это алгоритм вероятностного поиска для принятия решений в играх и других задачах с большим пространством состояний. Состоит из четырёх фаз: `Selection` (выбор), `Expansion` (расширение), `Simulation` (симуляция), `Backpropagation` (обратное распространение).

## Полезные ссылки

### Официальная документация
- [GeeksforGeeks: Monte Carlo Tree Search (MCTS)](https://www.geeksforgeeks.org/monte-carlo-tree-search-mcts/)

### Визуализация
- [MCTS Visualization](https://www.geeksforgeeks.org/monte-carlo-tree-search-mcts/) — визуализация

### См. также
- [[binary-tree|`binary-tree.md`]] — бинарное дерево (`binary tree`)
- [[binary-search-tree-traversal|`binary-search-tree-traversal.md`]] — обходы бинарного дерева поиска (`traversals`)

## Содержание

- [Описание алгоритма](#описание-алгоритма)
  - [Историческая справка](#историческая-справка)
- [Четыре фазы MCTS](#четыре-фазы-mcts)
  - [Selection (Выбор)](#1-selection-выбор)
  - [Expansion (Расширение)](#2-expansion-расширение)
  - [Simulation (Симуляция)](#3-simulation-симуляция)
  - [Backpropagation (Обратное распространение)](#4-backpropagation-обратное-распространение)
- [UCT формула](#uct-формула)
- [Реализация на Java](#реализация-на-java)
  - [Реализация для крестиков-ноликов](#реализация-для-крестиков-ноликов)
  - [Классы Tree и Node](#классы-tree-и-node)
  - [Класс State (Java)](#класс-state-java)
  - [Класс MonteCarloTreeSearch](#класс-montecarlotreesearch)
  - [Реализация фаз](#реализация-фаз)
    - [Selection с UCT](#selection-с-uct)
    - [Expansion](#expansion)
    - [Simulation](#simulation)
    - [Backpropagation](#backpropagation)
  - [Класс Board](#класс-board)
  - [Тестирование](#тестирование)
- [Реализация на Kotlin](#реализация-на-kotlin)
  - [Класс Node](#класс-node)
  - [Класс MCTS](#класс-mcts)
  - [Пример использования](#пример-использования)
- [Сложность](#сложность)
  - [Временная сложность](#временная-сложность)
  - [Пространственная сложность](#пространственная-сложность)
- [Преимущества MCTS](#преимущества-mcts)
- [Недостатки и улучшения](#недостатки-и-улучшения)
- [Применение](#применение)
- [Когда использовать](#когда-использовать)
  - [Используйте MCTS, когда:](#используйте-mcts-когда)
  - [Альтернативы:](#альтернативы)
- [Лучшие практики](#лучшие-практики)
- [Решение проблем](#решение-проблем)
- [Частые вопросы](#частые-вопросы)
- [Заключение](#заключение)


## Описание алгоритма

В этой статье мы собираемся изучить алгоритм поиска по дереву Монте-Карло (`MCTS`) и его приложения. Мы подробно рассмотрим его этапы, реализовав игру «Крестики-нолики» на `Java`. Мы разработаем общее решение, которое можно будет использовать во многих других практических приложениях с минимальными изменениями. Проще говоря, поиск по дереву Монте-Карло — это алгоритм вероятностного поиска. Это уникальный алгоритм принятия решений из‑за его эффективности в открытых средах с огромным количеством возможностей.

Если вы уже знакомы с алгоритмами теории игр, такими как `Minimax`, вам потребуется функция для оценки текущего состояния, и она должна вычислить множество уровней в дереве игры, чтобы найти оптимальный ход. К сожалению, это невозможно сделать в такой игре, как го, в которой есть высокий коэффициент ветвления, что приводит к миллионам возможностей по мере увеличения высоты дерева, и трудно написать хорошую функцию оценки, чтобы вычислить, насколько хороша ветвь текущего состояния.

Поиск по дереву Монте-Карло применяет метод Монте-Карло к поиску по дереву игры. Поскольку он основан на случайной выборке игровых состояний, ему не нужно перебирать каждую возможность методом грубой силы. Кроме того, это не обязательно требует от нас написания оценки или хороших эвристических функций.

### Историческая справка

И короткое замечание: он произвёл революцию в мире компьютерного го. С марта `2016` года он стал широко распространённой темой исследований, поскольку `Google AlphaGO` (построенный с помощью `MCTS` и нейронной сети) победил Ли Седоля (чемпион мира по го).

## Четыре фазы MCTS

Теперь давайте рассмотрим, как работает алгоритм. Сначала мы построим предварительное дерево (игровое дерево) с корневым узлом, а затем будем расширять его случайными развёртываниями. В процессе мы будем поддерживать количество посещений и количество побед для каждого узла. В конце мы собираемся выбрать узел с наиболее многообещающей статистикой. Алгоритм состоит из четырёх этапов; давайте подробно рассмотрим их все.

### 1. Selection (Выбор)

На этом начальном этапе алгоритм начинает с корневого узла и выбирает дочерний узел таким образом, чтобы выбрать узел с максимальным коэффициентом выигрыша. Мы также хотим убедиться, что каждому узлу предоставлен равный шанс. Идея состоит в том, чтобы продолжать выбирать оптимальные дочерние узлы, пока мы не достигнем конечного узла дерева. Хороший способ выбрать такой дочерний узел — использовать формулу `UCT` (верхняя доверительная граница, применяемая к деревьям).

### 2. Expansion (Расширение)

Когда он больше не может применять `UCT` для поиска узла‑преемника, он расширяет игровое дерево, добавляя все возможные состояния из конечного узла. Это позволяет алгоритму исследовать новые области пространства состояний и находить потенциально лучшие ходы.

### 3. Simulation (Симуляция)

После расширения алгоритм произвольно выбирает дочерний узел и моделирует рандомизированную игру из выбранного узла, пока не достигнет результирующего состояния игры. Если узлы выбираются случайным или полуслучайным образом во время воспроизведения, это называется лёгким воспроизведением (`light play`). Вы также можете выбрать тяжёлую игру (`heavy play`), написав эвристики качества или функции оценки, что даст более точные результаты, но потребует больше времени на выполнение.

### 4. Backpropagation (Обратное распространение)

Это также известно как этап обновления. Как только алгоритм достигает конца игры, он оценивает состояние, чтобы выяснить, кто из игроков выиграл. Он проходит вверх к корню и увеличивает оценку посещения для всех посещённых узлов. Он также обновляет счёт выигрыша для каждого узла, если игрок на этой позиции выиграл игру. `MCTS` продолжает повторять эти четыре фазы до определённого фиксированного количества итераций или определённого фиксированного периода времени.

В этом подходе мы оцениваем выигрышный счёт для каждого узла на основе случайных ходов. Таким образом, чем выше количество итераций, тем надёжнее становится оценка. Оценки алгоритма будут менее точными в начале поиска и продолжат улучшаться по прошествии достаточного количества времени. Опять же, это зависит исключительно от типа проблемы.

## UCT формула

Формула `UCT` (Upper Confidence Bound applied to Trees) используется для выбора оптимального дочернего узла на этапе выбора. Формула выглядит следующим образом: `UCT = (w^i / n^i) + c * sqrt(ln(t) / n^i)`. В этой формуле `w^i` — это количество побед после `i`‑го хода, `n^i` — количество симуляций после `i`‑го хода, `c` — параметр разведки (теоретически равен `√2`), а `t` — общее количество симуляций для родительского узла. Формула гарантирует, что ни одно состояние не станет жертвой голода, а также чаще, чем их коллеги, играет в многообещающие отрасли, балансируя между исследованием новых узлов и использованием уже известных хороших узлов.

## Реализация на Java

### Реализация для крестиков-ноликов

Теперь давайте реализуем игру в крестики-нолики, используя алгоритм поиска по дереву Монте-Карло. Мы разработаем универсальное решение для `MCTS`, которое можно будет использовать и для многих других настольных игр. Мы рассмотрим большую часть кода в самой статье. Хотя, чтобы сделать объяснение более чётким, нам, возможно, придётся пропустить некоторые незначительные детали (не особенно связанные с `MCTS`), но вы всегда можете найти полную реализацию на `GitHub`.

### Классы Tree и Node

Прежде всего, нам нужна базовая реализация для классов `Tree` и `Node`, чтобы иметь функциональность поиска по дереву:

```java
// Узел игрового дерева MCTS: состояние, родитель и список дочерних узлов
public class Node {
    State state;
    Node parent;
    List<Node> childArray;
    
    public Node() {
        this.state = new State();
        this.childArray = new ArrayList<>();
    }
    
    public Node(State state) {
        this.state = state;
        this.childArray = new ArrayList<>();
    }
    
    public Node(Node node) {
        this.childArray = new ArrayList<>();
        this.state = new State(node.getState());
        if (node.getParent() != null) {
            this.parent = node.getParent();
        }
        List<Node> childArray = node.getChildArray();
        for (Node child : childArray) {
            this.childArray.add(new Node(child));
        }
    }
    
    // Геттеры и сеттеры
    public State getState() {
        return state;
    }
    
    public void setState(State state) {
        this.state = state;
    }
    
    public Node getParent() {
        return parent;
    }
    
    public void setParent(Node parent) {
        this.parent = parent;
    }
    
    public List<Node> getChildArray() {
        return childArray;
    }
    
    public void setChildArray(List<Node> childArray) {
        this.childArray = childArray;
    }
    
    public Node getRandomChildNode() {
        int noOfPossibleMoves = this.childArray.size();
        int selectRandom = (int) (Math.random() * noOfPossibleMoves);
        return this.childArray.get(selectRandom);
    }
    
    public Node getChildWithMaxScore() {
        return Collections.max(this.childArray, 
            Comparator.comparing(c -> c.getState().getVisitCount()));
    }
}

public class Tree {
    Node root;
    
    public Tree() {
        root = new Node();
    }
    
    public Node getRoot() {
        return root;
    }
    
    public void setRoot(Node root) {
        this.root = root;
    }
}
```

### Класс State (Java)

Поскольку каждый узел будет иметь определённое состояние проблемы, давайте также реализуем класс `State`:

```java
public class State {
    Board board;
    int playerNo;
    int visitCount;
    double winScore;
    
    public State() {
        this.board = new Board();
    }
    
    public State(State state) {
        this.board = new Board(state.getBoard());
        this.playerNo = state.getPlayerNo();
        this.visitCount = state.getVisitCount();
        this.winScore = state.getWinScore();
    }
    
    public State(Board board) {
        this.board = new Board(board);
    }
    
    public List<State> getAllPossibleStates() {
        List<State> possibleStates = new ArrayList<>();
        List<Position> availablePositions = this.board.getEmptyPositions();
        
        availablePositions.forEach(p -> {
            State newState = new State(this.board);
            newState.setPlayerNo(3 - this.playerNo);
            newState.getBoard().performMove(newState.getPlayerNo(), p);
            possibleStates.add(newState);
        });
        
        return possibleStates;
    }
    
    public void randomPlay() {
        List<Position> availablePositions = this.board.getEmptyPositions();
        int totalAvailableMoves = availablePositions.size();
        int selectRandom = (int) (Math.random() * totalAvailableMoves);
        this.board.performMove(this.playerNo, availablePositions.get(selectRandom));
    }
    
    public void togglePlayer() {
        this.playerNo = 3 - this.playerNo;
    }
    
    public int getOpponent() {
        return 3 - this.playerNo;
    }
    
    // Геттеры и сеттеры
    public Board getBoard() {
        return board;
    }
    
    public void setBoard(Board board) {
        this.board = board;
    }
    
    public int getPlayerNo() {
        return playerNo;
    }
    
    public void setPlayerNo(int playerNo) {
        this.playerNo = playerNo;
    }
    
    public int getVisitCount() {
        return visitCount;
    }
    
    public void setVisitCount(int visitCount) {
        this.visitCount = visitCount;
    }
    
    public double getWinScore() {
        return winScore;
    }
    
    public void setWinScore(double winScore) {
        this.winScore = winScore;
    }
    
    public void incrementVisit() {
        this.visitCount++;
    }
    
    public void addScore(double score) {
        if (this.winScore != Integer.MIN_VALUE) {
            this.winScore += score;
        }
    }
}
```

### Класс MonteCarloTreeSearch

Теперь давайте реализуем класс `MonteCarloTreeSearch`, который будет отвечать за поиск следующего лучшего хода из заданной игровой позиции:

```java
public class MonteCarloTreeSearch {
    static final int WIN_SCORE = 10;
    int level;
    int opponent;
    
    public Board findNextMove(Board board, int playerNo) {
        opponent = 3 - playerNo;
        Tree tree = new Tree();
        Node rootNode = tree.getRoot();
        rootNode.getState().setBoard(board);
        rootNode.getState().setPlayerNo(opponent);
        
        long end = System.currentTimeMillis() + 5000; // 5 секунд
        
        while (System.currentTimeMillis() < end) {
            // Selection
            Node promisingNode = selectPromisingNode(rootNode);
            
            // Expansion
            if (promisingNode.getState().getBoard().checkStatus() == Board.IN_PROGRESS) {
                expandNode(promisingNode);
            }
            
            // Simulation
            Node nodeToExplore = promisingNode;
            if (promisingNode.getChildArray().size() > 0) {
                nodeToExplore = promisingNode.getRandomChildNode();
            }
            int playoutResult = simulateRandomPlayout(nodeToExplore);
            
            // Backpropagation
            backPropogation(nodeToExplore, playoutResult);
        }
        
        Node winnerNode = rootNode.getChildWithMaxScore();
        tree.setRoot(winnerNode);
        return winnerNode.getState().getBoard();
    }
}
```

Здесь мы продолжаем повторять все четыре фазы до заранее определенного времени, и в конце мы получаем дерево с надежной статистикой для принятия разумного решения.

### Реализация фаз

Теперь давайте реализуем методы для всех фаз.

#### Selection с UCT

Мы начнём с этапа выбора, который также требует внедрения `UCT`:

```java
private Node selectPromisingNode(Node rootNode) {
    Node node = rootNode;
    
    while (node.getChildArray().size() != 0) {
        node = UCT.findBestNodeWithUCT(node);
    }
    
    return node;
}

public class UCT {
    public static double uctValue(int totalVisit, double nodeWinScore, int nodeVisit) {
        if (nodeVisit == 0) {
            return Integer.MAX_VALUE;
        }
        
        return ((double) nodeWinScore / (double) nodeVisit)
            + 1.41 * Math.sqrt(Math.log(totalVisit) / (double) nodeVisit);
    }
    
    public static Node findBestNodeWithUCT(Node node) {
        int parentVisit = node.getState().getVisitCount();
        return Collections.max(
            node.getChildArray(),
            Comparator.comparing(c -> uctValue(parentVisit,
                c.getState().getWinScore(), c.getState().getVisitCount())));
    }
}
```

На этом этапе рекомендуется конечный узел, который следует расширить на этапе расширения.

#### Expansion

```java
private void expandNode(Node node) {
    List<State> possibleStates = node.getState().getAllPossibleStates();
    possibleStates.forEach(state -> {
        Node newNode = new Node(state);
        newNode.setParent(node);
        newNode.getState().setPlayerNo(node.getState().getOpponent());
        node.getChildArray().add(newNode);
    });
}
```

#### Simulation

Затем мы пишем код для выбора случайного узла и имитации случайного воспроизведения из него:

```java
private int simulateRandomPlayout(Node node) {
    Node tempNode = new Node(node);
    State tempState = tempNode.getState();
    int boardStatus = tempState.getBoard().checkStatus();
    
    if (boardStatus == opponent) {
        tempNode.getParent().getState().setWinScore(Integer.MIN_VALUE);
        return boardStatus;
    }
    
    while (boardStatus == Board.IN_PROGRESS) {
        tempState.togglePlayer();
        tempState.randomPlay();
        boardStatus = tempState.getBoard().checkStatus();
    }
    
    return boardStatus;
}
```

#### Backpropagation

Кроме того, у нас будет функция обновления для распространения оценки и количества посещений, начиная с листа к корню:

```java
private void backPropogation(Node nodeToExplore, int playerNo) {
    Node tempNode = nodeToExplore;
    
    while (tempNode != null) {
        tempNode.getState().incrementVisit();
        if (tempNode.getState().getPlayerNo() == playerNo) {
            tempNode.getState().addScore(WIN_SCORE);
        }
        tempNode = tempNode.getParent();
    }
}
```

### Класс Board

Теперь мы закончили с реализацией `MCTS`. Всё, что нам нужно, — это конкретная реализация класса `Board` в крестиках-ноликах. Обратите внимание, что для того, чтобы играть в другие игры с нашей реализацией, нам просто нужно изменить класс `Board`.

```java
public class Board {
    int[][] boardValues;
    int totalMoves;
    
    public static final int DEFAULT_BOARD_SIZE = 3;
    public static final int IN_PROGRESS = -1;
    public static final int DRAW = 0;
    public static final int P1 = 1;
    public static final int P2 = 2;
    
    public Board() {
        boardValues = new int[DEFAULT_BOARD_SIZE][DEFAULT_BOARD_SIZE];
        totalMoves = 0;
    }
    
    public Board(Board board) {
        this.boardValues = new int[DEFAULT_BOARD_SIZE][DEFAULT_BOARD_SIZE];
        for (int i = 0; i < DEFAULT_BOARD_SIZE; i++) {
            for (int j = 0; j < DEFAULT_BOARD_SIZE; j++) {
                this.boardValues[i][j] = board.boardValues[i][j];
            }
        }
        this.totalMoves = board.totalMoves;
    }
    
    public void performMove(int player, Position p) {
        this.totalMoves++;
        boardValues[p.getX()][p.getY()] = player;
    }
    
    public int checkStatus() {
        // Проверка строк
        for (int i = 0; i < DEFAULT_BOARD_SIZE; i++) {
            if (boardValues[i][0] == boardValues[i][1] 
                && boardValues[i][1] == boardValues[i][2] 
                && boardValues[i][0] != 0) {
                return boardValues[i][0];
            }
        }
        
        // Проверка столбцов
        for (int j = 0; j < DEFAULT_BOARD_SIZE; j++) {
            if (boardValues[0][j] == boardValues[1][j] 
                && boardValues[1][j] == boardValues[2][j] 
                && boardValues[0][j] != 0) {
                return boardValues[0][j];
            }
        }
        
        // Проверка диагоналей
        if (boardValues[0][0] == boardValues[1][1] 
            && boardValues[1][1] == boardValues[2][2] 
            && boardValues[0][0] != 0) {
            return boardValues[0][0];
        }
        
        if (boardValues[0][2] == boardValues[1][1] 
            && boardValues[1][1] == boardValues[2][0] 
            && boardValues[0][2] != 0) {
            return boardValues[0][2];
        }
        
        if (totalMoves == DEFAULT_BOARD_SIZE * DEFAULT_BOARD_SIZE) {
            return DRAW;
        }
        
        return IN_PROGRESS;
    }
    
    public List<Position> getEmptyPositions() {
        int size = this.boardValues.length;
        List<Position> emptyPositions = new ArrayList<>();
        
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                if (boardValues[i][j] == 0) {
                    emptyPositions.add(new Position(i, j));
                }
            }
        }
        
        return emptyPositions;
    }
}
```

### Тестирование

Мы только что внедрили `ИИ`, который невозможно победить в крестиках-ноликах. Давайте напишем единичный случай, демонстрирующий, что `ИИ` против `ИИ` всегда приводит к ничьей:

```java
@Test
void givenEmptyBoard_whenSimulateInterAIPlay_thenGameDraw() {
    Board board = new Board();
    int player = Board.P1;
    int totalMoves = Board.DEFAULT_BOARD_SIZE * Board.DEFAULT_BOARD_SIZE;
    
    for (int i = 0; i < totalMoves; i++) {
        board = mcts.findNextMove(board, player);
        if (board.checkStatus() != -1) {
            break;
        }
        player = 3 - player;
    }
    
    int winStatus = board.checkStatus();
    assertEquals(winStatus, Board.DRAW);
}
```

## Реализация на Kotlin

### Класс Node

```kotlin
class NodeK(var state: StateK) {
    var parent: NodeK? = null
    val childArray = mutableListOf<NodeK>()
    var visitCount = 0
    var winScore = 0.0
    
    fun getRandomChildNode(): NodeK? {
        return if (childArray.isNotEmpty()) {
            childArray.random()
        } else null
    }
    
    fun getChildWithMaxScore(): NodeK? {
        return childArray.maxByOrNull { it.winScore }
    }
}
```

### Класс State (Kotlin)

```kotlin
class StateK {
    var board: Array<IntArray> = Array(3) { IntArray(3) { 0 } }
    var playerNo: Int = 1
    var visitCount: Int = 0
    var winScore: Double = 0.0
    
    fun getAllPossibleStates(): List<StateK> {
        val possibleStates = mutableListOf<StateK>()
        
        for (i in 0 until 3) {
            for (j in 0 until 3) {
                if (board[i][j] == 0) {
                    val newState = StateK()
                    newState.board = board.map { it.clone() }.toTypedArray()
                    newState.playerNo = 3 - playerNo
                    newState.board[i][j] = playerNo
                    possibleStates.add(newState)
                }
            }
        }
        
        return possibleStates
    }
    
    fun randomPlay() {
        val availablePositions = mutableListOf<Pair<Int, Int>>()
        for (i in 0 until 3) {
            for (j in 0 until 3) {
                if (board[i][j] == 0) {
                    availablePositions.add(Pair(i, j))
                }
            }
        }
        
        if (availablePositions.isNotEmpty()) {
            val (i, j) = availablePositions.random()
            board[i][j] = playerNo
            playerNo = 3 - playerNo
        }
    }
    
    fun checkStatus(): Int {
        // Проверка строк
        for (i in 0 until 3) {
            if (board[i][0] != 0 && board[i][0] == board[i][1] && board[i][1] == board[i][2]) {
                return board[i][0]
            }
        }
        
        // Проверка столбцов
        for (j in 0 until 3) {
            if (board[0][j] != 0 && board[0][j] == board[1][j] && board[1][j] == board[2][j]) {
                return board[0][j]
            }
        }
        
        // Проверка диагоналей
        if (board[0][0] != 0 && board[0][0] == board[1][1] && board[1][1] == board[2][2]) {
            return board[0][0]
        }
        if (board[0][2] != 0 && board[0][2] == board[1][1] && board[1][1] == board[2][0]) {
            return board[0][2]
        }
        
        // Проверка на ничью
        for (i in 0 until 3) {
            for (j in 0 until 3) {
                if (board[i][j] == 0) {
                    return -1 // Игра продолжается
                }
            }
        }
        
        return 0 // Ничья
    }
}
```

### Класс MCTS

```kotlin
class MCTSK {
    private val explorationConstant = Math.sqrt(2.0)
    
    fun findNextMove(board: StateK, playerNo: Int): StateK {
        val root = NodeK(StateK().apply {
            this.board = board.board.map { it.clone() }.toTypedArray()
            this.playerNo = playerNo
        })
        
        for (i in 0 until 1000) {
            val promisingNode = selectPromisingNode(root)
            if (promisingNode.state.checkStatus() == -1) {
                expandNode(promisingNode)
            }
            
            val nodeToExplore = promisingNode
            if (promisingNode.childArray.isNotEmpty()) {
                nodeToExplore = promisingNode.getRandomChildNode()!!
            }
            
            val playoutResult = simulateRandomPlayout(nodeToExplore)
            backPropagation(nodeToExplore, playoutResult)
        }
        
        val winnerNode = root.getChildWithMaxScore()
        return winnerNode?.state ?: root.state
    }
    
    private fun selectPromisingNode(rootNode: NodeK): NodeK {
        var node = rootNode
        while (node.childArray.isNotEmpty()) {
            node = findBestNodeWithUCT(node) ?: node
        }
        return node
    }
    
    private fun findBestNodeWithUCT(node: NodeK): NodeK? {
        val parentVisit = node.visitCount.toDouble()
        
        return node.childArray.maxByOrNull { child ->
            val winScore = child.winScore
            val visitCount = child.visitCount.toDouble()
            (winScore / visitCount) + explorationConstant * Math.sqrt(Math.log(parentVisit) / visitCount)
        }
    }
    
    private fun expandNode(node: NodeK) {
        val possibleStates = node.state.getAllPossibleStates()
        for (state in possibleStates) {
            val newNode = NodeK(state)
            newNode.parent = node
            newNode.state.playerNo = node.state.playerNo
            node.childArray.add(newNode)
        }
    }
    
    private fun simulateRandomPlayout(node: NodeK): Int {
        val tempState = StateK().apply {
            board = node.state.board.map { it.clone() }.toTypedArray()
            playerNo = node.state.playerNo
        }
        
        var boardStatus = tempState.checkStatus()
        
        if (boardStatus == node.state.playerNo) {
            node.parent?.let { it.winScore = Int.MIN_VALUE.toDouble() }
            return boardStatus
        }
        
        while (boardStatus == -1) {
            tempState.randomPlay()
            boardStatus = tempState.checkStatus()
        }
        
        return boardStatus
    }
    
    private fun backPropagation(nodeToExplore: NodeK, playerNo: Int) {
        var tempNode = nodeToExplore
        while (tempNode != null) {
            tempNode.visitCount++
            if (tempNode.state.playerNo == playerNo) {
                tempNode.winScore += 1.0
            }
            tempNode = tempNode.parent
        }
    }
}
```

### Пример использования

```kotlin
fun main() {
    val board = StateK()
    val mcts = MCTSK()
    
    var player = 1
    while (board.checkStatus() == -1) {
        val nextMove = mcts.findNextMove(board, player)
        board.board = nextMove.board
        board.playerNo = nextMove.playerNo
        player = 3 - player
    }
    
    println("Game status: ${board.checkStatus()}")
}
```

## Сложность

### Временная сложность

Временная сложность `MCTS` зависит от количества итераций и составляет `O(k·d)`, где `k` — количество итераций, а `d` — средняя глубина симуляции. В худшем случае временная сложность может быть экспоненциальной, но на практике она ограничивается временем выполнения, так как алгоритм может быть прерван в любой момент и всё равно даст разумный результат.

### Пространственная сложность

Пространственная сложность `MCTS` во всех случаях составляет `O(b·d)`, где `b` — коэффициент ветвления (количество возможных ходов из каждого состояния), а `d` — глубина дерева. Это означает, что память, необходимая для хранения дерева, растёт линейно с глубиной и коэффициентом ветвления.

## Преимущества MCTS

`MCTS` имеет несколько преимуществ перед другими алгоритмами поиска в играх. Алгоритм не требует тактических знаний об игре, что делает его универсальным и применимым к различным типам игр без необходимости написания сложных эвристических функций. Обычная реализация `MCTS` может быть повторно использована для любого количества игр с небольшими изменениями, что значительно упрощает разработку игровых `ИИ`. Алгоритм сосредоточен на узлах с более высокими шансами на победу в игре, что делает его эффективным в использовании вычислительных ресурсов. `MCTS` подходит для задач с высоким коэффициентом ветвления, поскольку не тратит вычисления на все возможные ветвления, а фокусируется на наиболее перспективных. Алгоритм очень прост в реализации по сравнению с другими алгоритмами теории игр, такими как `Minimax` с альфа-бета отсечением. Выполнение может быть остановлено в любой момент времени, и оно по‑прежнему будет предлагать следующее лучшее состояние, вычисленное на данный момент, что делает алгоритм гибким и адаптивным к различным ограничениям по времени.

## Недостатки и улучшения

Если `MCTS` используется в своей базовой форме без каких‑либо улучшений, он может не предложить разумных действий. Это может произойти, если узлы не посещаются должным образом, что приводит к неточным оценкам. Однако `MCTS` можно улучшить с помощью некоторых методов. Он включает в себя методы, специфичные для предметной области, а также независимые от предметной области методы. В методах, специфичных для предметной области, этап моделирования обеспечивает более реалистичное воспроизведение, чем стохастическое моделирование. Хотя это требует знания конкретных игровых техник и правил, что может усложнить реализацию, но значительно улучшает качество оценок и точность алгоритма.

## Применение

`MCTS` используется в различных областях. В играх алгоритм применяется для создания сильных игровых `ИИ` в таких играх, как `Go`, `Chess`, `Shogi` и других играх с большим пространством состояний. В игровых `ИИ` алгоритм используется для принятия решений в реальном времени, где требуется баланс между качеством решения и временем на его вычисление. В планировании маршрутов `MCTS` применяется для поиска оптимальных путей в сложных графах с множеством возможных вариантов. В оптимизации алгоритм используется для поиска оптимальных решений в задачах с большим пространством поиска. В робототехнике `MCTS` применяется для планирования движений и принятия решений в неопределённых средах. В машинном обучении алгоритм используется для обучения с подкреплением и принятия решений в сложных средах.

## Когда использовать

Выбор между `MCTS` и другими алгоритмами зависит от конкретных требований задачи. `MCTS` следует использовать, когда пространство состояний большое, сложно написать функцию оценки, нужен адаптивный алгоритм, который может работать с различными типами игр, и важна гибкость реализации. Алгоритм особенно эффективен в играх с высоким коэффициентом ветвления, где другие алгоритмы могут быть неэффективными.

Альтернативы `MCTS` включают `Minimax` для игр с небольшим пространством состояний, где можно перебрать все возможные варианты. `Alpha-Beta Pruning` подходит для детерминированных игр, где можно точно оценить состояние игры. Нейронные сети используются для сложных задач с большими данными, где требуется обучение на исторических данных для принятия решений.

## Лучшие практики

Количество итераций критически важно для качества результатов `MCTS`. Чем больше итераций или времени выделено на выполнение алгоритма, тем точнее становится оценка. Необходимо выбирать бюджет по времени или по числу симуляций в зависимости от задачи и доступных вычислительных ресурсов. Для игр с ограничением по времени на ход необходимо балансировать между количеством итераций и качеством оценки.

`UCT` и константа `C` играют важную роль в балансировке между исследованием новых узлов и использованием уже известных хороших узлов. Параметр `C` в `UCT` балансирует исследование и использование; типичные значения находятся в диапазоне от `1.4` до `√2`. Необходимо подбирать значение `C` под конкретную игру, так как оптимальное значение может различаться в зависимости от характера игры и пространства состояний.

Симуляция может быть реализована двумя способами. `Light play` (случайные ходы) проще и быстрее, что позволяет выполнить больше итераций за то же время. `Heavy play` с эвристиками даёт более точные оценки при больших затратах времени, что может быть полезно, когда качество оценки важнее скорости.

Кэширование состояний важно при работе с большим деревом. Необходимо кэшировать хэши состояний и избегать дублирования узлов с одинаковым состоянием, что может значительно улучшить производительность и качество результатов алгоритма.

Адаптация под игру требует минимальных изменений в коде. Для новой игры достаточно реализовать генерацию допустимых ходов, проверку терминального состояния и результат игры. Остальная логика `MCTS` универсальна и может быть использована без изменений для различных типов игр.


## Решение проблем

| Симптом | Возможная причина | Решение |
|--------|-------------------|---------|
| Алгоритм делает очевидно плохие ходы | Мало итераций MCTS | Увеличить число итераций; для крестиков-ноликов минимум 1000 итераций на ход |
| Высокий разброс качества ходов | Параметр `C` (exploration) слишком большой или маленький | Подобрать `C` экспериментально: типичное значение `√2 ≈ 1.41`; для детерминированных игр можно уменьшить |
| Алгоритм не находит выигрышный ход | Rollout (симуляция) даёт нерепрезентативные результаты | Заменить случайную симуляцию на эвристическую; использовать domain-specific знания |

## Частые вопросы

**Чем MCTS отличается от Minimax?** Minimax перебирает всё дерево игры и гарантирует оптимальную стратегию, но экспоненциально растёт с глубиной. MCTS семплирует дерево выборочно через случайные симуляции, что позволяет работать с играми с огромным пространством состояний (Go, шахматы).

**Зачем фаза backpropagation?** После симуляции результат (победа/поражение) передаётся вверх по дереву, обновляя статистику каждого пройденного узла. Это позволяет алгоритму постепенно узнавать, какие ветви более перспективны, и концентрировать поиск на них.

**Можно ли использовать MCTS для не-игровых задач?** Да, MCTS применяется в оптимизации, планировании маршрутов, генерации расписаний — везде, где можно определить состояния, действия и функцию оценки. Ключевое требование — возможность симуляции исхода из любого состояния.


## Заключение

В этой статье мы рассмотрели алгоритм поиска по дереву Монте-Карло и его реализацию для игры в крестики-нолики. `MCTS` — это мощный алгоритм, который не требует глубоких знаний о предметной области и может быть адаптирован для различных задач принятия решений. Его успех в `AlphaGo` демонстрирует его эффективность в сложных задачах с большим пространством состояний. Алгоритм продолжает развиваться и находить новые применения в различных областях, от игровых `ИИ` до робототехники и машинного обучения.
