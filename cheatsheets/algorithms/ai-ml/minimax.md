---
title: "Руководство по алгоритму Minimax"
description: "Руководство по алгоритму Minimax и его применению в ИИ для игр с нулевой суммой, включая реализацию простой игры."
tags:
  - algorithms
  - ai-ml
  - minimax
difficulty: "intermediate"
prerequisites: []
next: []
updated: "2026-02-11"
---
# Руководство по алгоритму Minimax

Руководство по алгоритму **Minimax** и его применению в ИИ для игр с нулевой суммой, включая реализацию простой игры.

## Полезные ссылки

### Официальная документация
- [Minimax (Wikipedia)](https://en.wikipedia.org/wiki/Minimax)
- [Game Theory (Stanford/Coursera)](https://www.coursera.org/learn/game-theory-1)

### См. также
- [[monte-carlo-tree-search-tic-tac-toe|Monte Carlo Tree Search]]
- [[README|Задачи и алгоритмы]] — обзор разделов

## Содержание

- [Обзор](#обзор)
- [Алгоритм Minimax](#алгоритм-minimax)
- [Реализация на Java](#реализация-на-java)
- [Улучшения (Alpha-Beta)](#улучшения-алгоритма)
- [Сложность и применение](#временная-сложность)
- [Лучшие практики](#лучшие-практики)
- [Решение проблем](#решение-проблем)
- [Частые вопросы](#частые-вопросы)
- [Резюме](#резюме)
- [Реализация на Kotlin](#реализация-на-kotlin)


## Обзор

В этой статье мы обсудим алгоритм Минимакс и его применение в ИИ. Поскольку это алгоритм теории игр, мы реализуем простую игру, используя его.

Мы также обсудим преимущества использования алгоритма и посмотрим, как его можно улучшить.

Минимакс — это алгоритм принятия решений, обычно используемый в пошаговых играх для двух игроков. Цель алгоритма — найти оптимальный следующий ход.

## Что такое Minimax?

В алгоритме один игрок называется максимизатором, а другой игрок — минимизатором. Если мы присвоим игровому полю оценочный балл, то один игрок попытается выбрать состояние игры с максимальным количеством очков, а другой — состояние с минимальным счетом.

Другими словами, максимизатор работает, чтобы получить наибольшее количество очков, в то время как минимизатор пытается получить наименьшее количество очков, пытаясь противодействовать ходам.

## Игры с нулевой суммой

Он основан на концепции игры с нулевой суммой. В игре с нулевой суммой общая оценка полезности делится между игроками. Увеличение счета одного игрока приводит к уменьшению счета другого игрока. Таким образом, общий балл всегда равен нулю. Чтобы один игрок выиграл, другой должен проиграть. Примерами таких игр являются шахматы, покер, шашки, крестики-нолики.

Интересный факт — в `1997` году шахматный компьютер **Deep Blue** от **IBM** (созданный на базе Minimax) победил Гарри Каспарова (чемпиона мира по шахматам).

## Алгоритм Minimax

Наша цель — найти лучший ход для игрока. Для этого мы можем просто выбрать узел с наилучшей оценочной оценкой. Чтобы сделать процесс более умным, мы также можем смотреть вперед и оценивать ходы потенциального противника.

Для каждого хода мы можем заглянуть вперед на столько ходов, сколько позволяют наши вычислительные мощности. Алгоритм предполагает, что противник играет оптимально.

Технически мы начинаем с корневого узла и выбираем наилучший возможный узел. Мы оцениваем узлы на основе их оценочных баллов. В нашем случае функция оценки может присваивать баллы только узлам результата (листьям). Таким образом, мы рекурсивно достигаем листьев с оценками и распространяем оценки обратно.

### Шаги алгоритма

1. Построить полное игровое дерево
2. Оцените баллы для листьев, используя функцию оценки
3. **Резервное копирование очков от листьев к корню с учетом типа игрока:**
   - Для максимального игрока выберите ребенка с максимальным количеством очков
   - Для минимального игрока выберите ребенка с минимальным счетом
4. В корневом узле выберите узел с максимальным значением и выполните соответствующий ход

## Реализация на Java

### Реализация игры

Теперь давайте реализуем игру.

В игре у нас есть куча с n количеством костей. Оба игрока должны поднять 1, 2 или 3 кости в свой ход. Игрок, который не может взять кости, проигрывает. Каждый игрок играет оптимально. Учитывая значение n, давайте напишем ИИ.

### Класс GameOfBones (Java)

**Чтобы определить правила игры, мы реализуем класс **GameOfBones**:**

```java
// Игра «куча костей»: возможные ходы — забрать 1, 2 или 3 кости; Minimax для оптимальной стратегии.
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

class GameOfBones {
    static List<Integer> getPossibleStates(int noOfBonesInHeap) {
        return IntStream.rangeClosed(1, 3)
            .boxed()
            .map(i -> noOfBonesInHeap - i)
            .filter(newHeapCount -> newHeapCount >= 0)
            .collect(Collectors.toList());
    }
}
```

### Классы Node и Tree

**Кроме того, нам также нужна реализация для классов **Node** и **Tree**:**

```java
import java.util.ArrayList;
import java.util.List;

public class Node {
    private int noOfBones;
    private boolean isMaxPlayer;
    private int score;
    private List<Node> children;

    public Node(int noOfBones, boolean isMaxPlayer) {
        this.noOfBones = noOfBones;
        this.isMaxPlayer = isMaxPlayer;
        this.children = new ArrayList<>();
    }

    public void addChild(Node child) {
        children.add(child);
    }

    public List<Node> getChildren() {
        return children;
    }

    public int getNoOfBones() {
        return noOfBones;
    }

    public boolean isMaxPlayer() {
        return isMaxPlayer;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }
}

public class Tree {
    private Node root;

    public void setRoot(Node root) {
        this.root = root;
    }

    public Node getRoot() {
        return root;
    }
}
```

### Класс MiniMax (Java)

**Теперь реализуем алгоритм. Требуется игровое дерево, чтобы заглянуть вперед и найти лучший ход. Давайте реализуем это:**

```java
import java.util.Comparator;
import java.util.List;
import java.util.NoSuchElementException;

public class MiniMax {
    private Tree tree;

    public void constructTree(int noOfBones) {
        tree = new Tree();
        Node root = new Node(noOfBones, true);
        tree.setRoot(root);
        constructTree(root);
    }

    private void constructTree(Node parentNode) {
        List<Integer> listofPossibleHeaps = GameOfBones.getPossibleStates(
            parentNode.getNoOfBones()
        );
        boolean isChildMaxPlayer = !parentNode.isMaxPlayer();

        listofPossibleHeaps.forEach(n -> {
            Node newNode = new Node(n, isChildMaxPlayer);
            parentNode.addChild(newNode);
            if (newNode.getNoOfBones() > 0) {
                constructTree(newNode);
            }
        });
    }

    public boolean checkWin() {
        Node root = tree.getRoot();
        checkWin(root);
        return root.getScore() == 1;
    }

    private void checkWin(Node node) {
        List<Node> children = node.getChildren();
        boolean isMaxPlayer = node.isMaxPlayer();

        children.forEach(child -> {
            if (child.getNoOfBones() == 0) {
                child.setScore(isMaxPlayer ? 1 : -1);
            } else {
                checkWin(child);
            }
        });

        Node bestChild = findBestChild(isMaxPlayer, children);
        node.setScore(bestChild.getScore());
    }

    private Node findBestChild(boolean isMaxPlayer, List<Node> children) {
        Comparator<Node> byScoreComparator = Comparator.comparing(Node::getScore);
        return children.stream()
            .max(isMaxPlayer ? byScoreComparator : byScoreComparator.reversed())
            .orElseThrow(NoSuchElementException::new);
    }
}
```

**Теперь мы реализуем метод **checkWin**, который будет имитировать розыгрыш, выбирая оптимальные ходы для обоих игроков. Он устанавливает оценку:**

1. +1, если выигрывает максимизатор
2. -1, если минимизатор выигрывает

**CheckWin** вернет **true**, если выиграет первый игрок (в нашем случае — максимизатор).

### Тестирование

**Наконец, давайте реализуем тестовый пример с некоторыми значениями n (количество костей в куче):**

```java
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class MiniMaxTest {

    @Test
    public void givenMiniMax_whenCheckWin_thenComputeOptimal() {
        MiniMax miniMax = new MiniMax();

        miniMax.constructTree(6);
        boolean result = miniMax.checkWin();
        assertTrue(result);

        miniMax.constructTree(8);
        result = miniMax.checkWin();
        assertFalse(result);
    }
}
```

## Улучшения алгоритма

Для большинства задач невозможно построить все игровое дерево. На практике мы можем разработать частичное дерево (построить дерево только до заранее определенного количества уровней).

Затем нам нужно будет реализовать функцию оценки, которая должна решить, насколько хорошее текущее состояние для игрока.

Даже если мы не строим полные игровые деревья, вычисление ходов для игр с высоким коэффициентом ветвления может занять много времени.

### Альфа-бета отсечение

К счастью, есть возможность найти оптимальный ход, не исследуя каждый узел дерева игры. Мы можем пропустить некоторые ветки, следуя некоторым правилам, и это не повлияет на конечный результат. Этот процесс называется обрезкой. Альфа-бета-обрезка является распространенным вариантом минимаксного алгоритма.

```java
public class AlphaBetaMiniMax {
    private int minimax(Node node, int depth, boolean isMaxPlayer,
                       int alpha, int beta) {
        if (depth == 0 || node.getChildren().isEmpty()) {
            return evaluate(node);
        }

        if (isMaxPlayer) {
            int maxEval = Integer.MIN_VALUE;
            for (Node child : node.getChildren()) {
                int eval = minimax(child, depth - 1, false, alpha, beta);
                maxEval = Math.max(maxEval, eval);
                alpha = Math.max(alpha, eval);
                if (beta <= alpha) {
                    break; // Alpha-beta pruning
                }
            }
            return maxEval;
        } else {
            int minEval = Integer.MAX_VALUE;
            for (Node child : node.getChildren()) {
                int eval = minimax(child, depth - 1, true, alpha, beta);
                minEval = Math.min(minEval, eval);
                beta = Math.min(beta, eval);
                if (beta <= alpha) {
                    break; // Alpha-beta pruning
                }
            }
            return minEval;
        }
    }

    private int evaluate(Node node) {
        // Функция оценки состояния игры
        return node.getScore();
    }
}
```

## Временная сложность

- **Без обрезки**: `O(b^d)`, где b — коэффициент ветвления, d — глубина дерева
- **С `alpha-beta` обрезкой**: `O(b^(d/2))` в лучшем случае

## Пространственная сложность

- **Рекурсивный подход**: `O(d)` для стека вызовов
- **Итеративный подход**: `O(b^d)` для хранения всего дерева

## Применение

**Minimax** используется в:**
- Шахматах (Deep Blue)
- Шашках
- Крестиках-ноликах
- Покере
- Других играх с нулевой суммой

## Лучшие практики

Используйте alpha-beta отсечение для сокращения дерева поиска без потери оптимальности; порядок рассмотрения ходов влияет на эффективность отсечения. При большом коэффициенте ветвления ограничивайте глубину и вводите эвристическую оценку неконечных позиций. Учитывайте симметричные позиции и кэшируйте оценки (transposition table) для ускорения. Minimax применим к играм с нулевой суммой и полной информацией; для игр с неполной информацией (покер и т.п.) нужны другие методы.

## Решение проблем

| Симптом | Возможная причина | Решение |
|--------|-------------------|---------|
| Слишком долгий расчёт хода | Большая глубина или коэффициент ветвления | Ограничить глубину; alpha-beta; упорядочить ходы для раннего отсечения |
| ИИ играет слабо при ограничении глубины | Плохая оценочная функция неконечных позиций | Улучшить эвристику (материал, позиция, мобильность); калибровать веса |
| Дублирование оценки одинаковых позиций | Одна и та же позиция достигается разными путями | Ввести transposition table (кэш позиция → оценка) |

## Частые вопросы

**Чем alpha-beta отличается от полного Minimax?** Alpha-beta даёт тот же оптимальный ход, что и Minimax, но отсекает ветви, которые не могут улучшить текущий лучший результат; при удачном порядке ходов резко сокращает число узлов.

**Когда Minimax неприменим?** В играх с неполной информацией (скрытые карты, случайность без полного дерева) и при слишком большом дереве без возможности ограничить глубину и ввести эвристику.

**Что такое transposition table?** Кэш, сопоставляющий позицию на доске (хэш) и её оценку, чтобы не вычислять одну и ту же позицию повторно при разных порядках ходов.

## Резюме

В этой статье мы обсудили алгоритм Минимакс и его применение в ИИ.

**Ключевые моменты:**
- **Minimax** находит оптимальный ход для игрока
- Используется в играх с нулевой суммой
- Может быть улучшен с помощью **alpha-beta** обрезки
- Требует построения игрового дерева
- Эффективен для игр с небольшим коэффициентом ветвления

**Minimax** — это фундаментальный алгоритм в теории игр, который позволяет создавать сильных игровых ИИ.

## Реализация на Kotlin

### Класс GameOfBones (Kotlin)

```kotlin
object GameOfBonesK {
    fun getPossibleStates(noOfBonesInHeap: Int): List<Int> {
        return (1..3)
            .map { noOfBonesInHeap - it }
            .filter { it >= 0 }
    }
}
```

### Класс Node

```kotlin
class NodeK(
    val noOfBones: Int,
    val isMaxPlayer: Boolean
) {
    var score: Int = 0
    val children: MutableList<NodeK> = mutableListOf()

    fun addChild(child: NodeK) {
        children.add(child)
    }
}
```

### Класс Tree

```kotlin
class TreeK {
    var root: NodeK? = null
}
```

### Класс MiniMax (Kotlin)

```kotlin
class MiniMaxK {
    private var tree: TreeK? = null

    fun constructTree(noOfBones: Int) {
        tree = TreeK()
        val root = NodeK(noOfBones, true)
        tree?.root = root
        constructTree(root)
    }

    private fun constructTree(parentNode: NodeK) {
        val listOfPossibleHeaps = GameOfBonesK.getPossibleStates(parentNode.noOfBones)
        val isChildMaxPlayer = !parentNode.isMaxPlayer

        listOfPossibleHeaps.forEach { n ->
            val newNode = NodeK(n, isChildMaxPlayer)
            parentNode.addChild(newNode)
            if (newNode.noOfBones > 0) {
                constructTree(newNode)
            }
        }
    }

    fun checkWin(): Boolean {
        val root = tree?.root ?: return false
        checkWin(root)
        return root.score == 1
    }

    private fun checkWin(node: NodeK) {
        val children = node.children
        val isMaxPlayer = node.isMaxPlayer

        children.forEach { child ->
            if (child.noOfBones == 0) {
                child.score = if (isMaxPlayer) 1 else -1
            } else {
                checkWin(child)
            }
        }

        val bestChild = findBestChild(isMaxPlayer, children)
        node.score = bestChild?.score ?: 0
    }

    private fun findBestChild(isMaxPlayer: Boolean, children: List<NodeK>): NodeK? {
        return if (isMaxPlayer) {
            children.maxByOrNull { it.score }
        } else {
            children.minByOrNull { it.score }
        }
    }
}
```
