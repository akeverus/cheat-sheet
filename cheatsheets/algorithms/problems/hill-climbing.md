---
title: "Алгоритм восхождения на холм (Hill-Climbing)"
description: "Локальный поиск: из текущего состояния переходим к соседу с лучшим значением эвристики до достижения цели или тупика. Вариант Steepest-Ascent, проблемы локального максимума, плато и хребта. Реализация на Java и Kotlin."
tags: ["algorithms", "problems", "hill-climbing"]
difficulty: "intermediate"
prerequisites: []
next: []
updated: "2026-02-11"
---
# Алгоритм восхождения на холм (`Hill-Climbing`)

**Дата последнего обновления:** 2026-02-06

Локальный поиск: из текущего состояния переходим к соседу с лучшим значением эвристики до достижения цели или тупика. Вариант Steepest-Ascent, проблемы локального максимума, плато и хребта. Реализация на Java и Kotlin.

## Полезные ссылки

### Официальная документация
- [Hill Climbing Algorithm (Wikipedia)](https://en.wikipedia.org/wiki/Hill_climbing)
- [Heuristic Search (Wikipedia)](https://en.wikipedia.org/wiki/Heuristic_(computer_science))

### См. также
- [Multi-Swarm](multi-swarm.md)
- [Генетические алгоритмы](../ai-ml/genetic-algorithms.md) — генетические алгоритмы
- [Алгоритмы оптимизации](../algorithmic-paradigms/) — парадигмы

## Содержание

- [Обзор](#обзор)
- [Алгоритм восхождения на холм](#алгоритм-восхождения-на-холм)
- [Реализация на Java](#реализация-на-java)
- [Варианты алгоритма](#варианты-алгоритма)
- [Проблемы и решения](#проблемы-и-решения)
- [Преимущества и недостатки](#преимущества-и-недостатки)
- [Лучшие практики](#лучшие-практики)
- [Реализация на Kotlin](#реализация-на-kotlin)
- [Решение проблем](#решение-проблем)
- [Частые вопросы](#частые-вопросы)
- [Резюме](#резюме)


## Обзор

Hill-Climbing — локальный поиск с эвристикой: из текущего состояния переходим к соседу с лучшей оценкой, пока не достигнем цели или не останется улучшений. Простой, но может застревать в локальных оптимумах, плато и на хребтах.

## Алгоритм восхождения на холм

Начальное состояние задаётся. Цикл: применить оператор, получить соседей, выбрать соседа с лучшей эвристикой (или первый лучший в простом варианте), сделать его текущим; выйти при достижении цели или отсутствии улучшений. Эвристика оценивает близость к цели; от её выбора зависит качество решения.

Эвристика: например +1 за правильно расположенный блок опорной конструкции, −1 иначе; «правильно» — та же структура поддержки, что в целевом состоянии.

## Реализация на Java

Класс состояния: список стеков блоков и значение эвристики. Эвристика: +1 за правильно расположенный блок опорной конструкции, −1 иначе. Операторы: перенос блока в новый стек или в существующий; выбор преемника с лучшей эвристикой.

```java
// Состояние задачи: список стеков блоков и значение эвристики для направления поиска.
import java.util.*;

public class State {
    private List<Stack<String>> state;
    private int heuristics;
    
    public State(List<Stack<String>> state, int heuristics) {
        this.state = new ArrayList<>();
        for (Stack<String> stack : state) {
            Stack<String> newStack = new Stack<>();
            newStack.addAll(stack);
            this.state.add(newStack);
        }
        this.heuristics = heuristics;
    }
    
    public List<Stack<String>> getState() {
        return state;
    }
    
    public int getHeuristics() {
        return heuristics;
    }
}
```


```java
public class HillClimbingSolver {
    
    public int getHeuristicsValue(List<Stack<String>> currentState, 
                                  Stack<String> goalStateStack) {
        return currentState.stream()
            .mapToInt(stack -> getHeuristicsValueForStack(
                stack, currentState, goalStateStack))
            .sum();
    }
    
    public int getHeuristicsValueForStack(Stack<String> stack, 
                                         List<Stack<String>> currentState, 
                                         Stack<String> goalStateStack) {
        int stackHeuristics = 0;
        boolean isPositionCorrect = true;
        int goalStartIndex = 0;
        
        for (String currentBlock : stack) {
            if (isPositionCorrect && 
                goalStartIndex < goalStateStack.size() &&
                currentBlock.equals(goalStateStack.get(goalStartIndex))) {
                stackHeuristics += goalStartIndex;
            } else {
                stackHeuristics -= goalStartIndex;
                isPositionCorrect = false;
            }
            goalStartIndex++;
        }
        
        return stackHeuristics;
    }
}
```

Операторы: перенос блока в новый стек или в один из существующих; возвращаем состояние только если эвристика улучшилась.

```java
private State pushElementToNewStack(List<Stack<String>> currentStackList,
                                   String block,
                                   int currentStateHeuristics,
                                   Stack<String> goalStateStack) {
    State newState = null;
    Stack<String> newStack = new Stack<>();
    newStack.push(block);
    
    List<Stack<String>> tempList = new ArrayList<>(currentStackList);
    tempList.add(newStack);
    
    int newStateHeuristics = getHeuristicsValue(tempList, goalStateStack);
    
    if (newStateHeuristics > currentStateHeuristics) {
        newState = new State(tempList, newStateHeuristics);
    }
    
    return newState;
}

private State pushElementToExistingStacks(Stack<String> currentStack,
                                          List<Stack<String>> currentStackList,
                                          String block,
                                          int currentStateHeuristics,
                                          Stack<String> goalStateStack) {
    return currentStackList.stream()
        .filter(stack -> stack != currentStack)
        .map(stack -> pushElementToStack(stack, block, currentStackList,
            currentStateHeuristics, goalStateStack))
        .filter(Objects::nonNull)
        .findFirst()
        .orElse(null);
}

private State pushElementToStack(Stack<String> stack,
                                String block,
                                List<Stack<String>> currentStackList,
                                int currentStateHeuristics,
                                Stack<String> goalStateStack) {
    stack.push(block);
    int newStateHeuristics = getHeuristicsValue(currentStackList, goalStateStack);
    
    if (newStateHeuristics > currentStateHeuristics) {
        return new State(currentStackList, newStateHeuristics);
    }
    
    stack.pop();
    return null;
}
```


```java
public List<State> getRouteWithHillClimbing(Stack<String> initStateStack, 
                                            Stack<String> goalStateStack) 
    throws Exception {
    List<State> resultPath = new ArrayList<>();
    State currentState = new State(
        Collections.singletonList(initStateStack), 
        getHeuristicsValue(Collections.singletonList(initStateStack), goalStateStack)
    );
    resultPath.add(currentState);
    
    boolean noStateFound = false;
    
    while (!currentState.getState().get(0).equals(goalStateStack) && 
           !noStateFound) {
        noStateFound = true;
        State nextState = findNextState(currentState, goalStateStack);
        
        if (nextState != null) {
            noStateFound = false;
            currentState = nextState;
            resultPath.add(currentState);
        }
    }
    
    return resultPath;
}

public State findNextState(State currentState, Stack<String> goalStateStack) {
    List<Stack<String>> listOfStacks = currentState.getState();
    int currentStateHeuristics = currentState.getHeuristics();
    
    return listOfStacks.stream()
        .map(stack -> applyOperationsOnState(
            listOfStacks, stack, currentStateHeuristics, goalStateStack))
        .filter(Objects::nonNull)
        .findFirst()
        .orElse(null);
}

public State applyOperationsOnState(List<Stack<String>> listOfStacks,
                                   Stack<String> stack,
                                   int currentStateHeuristics,
                                   Stack<String> goalStateStack) {
    State tempState;
    List<Stack<String>> tempStackList = new ArrayList<>(listOfStacks);
    String block = stack.pop();
    
    if (stack.size() == 0) {
        tempStackList.remove(stack);
    }
    
    tempState = pushElementToNewStack(
        tempStackList, block, currentStateHeuristics, goalStateStack);
    
    if (tempState == null) {
        tempState = pushElementToExistingStacks(
            stack, tempStackList, block, currentStateHeuristics, goalStateStack);
    }
    
    stack.push(block);
    return tempState;
}
```

## Варианты алгоритма

Steepest-Ascent: из текущего состояния перебираем всех соседей и выбираем преемника с наилучшей эвристикой (а не первого улучшения). Даёт более стабильный прогресс, но дороже по вычислениям.

## Проблемы и решения

Локальный максимум: лучше всех соседей, но не глобальный оптимум — рестарты, откат к предыдущему состоянию или прыжок в новое направление. Плато: у всех соседей одна и та же оценка — случайный шаг или боковое движение. Хребет: оптимум «по диагонали», за один ход недостижим — исследовать несколько направлений или комбинировать операторы.

## Преимущества и недостатки

Плюсы: простая реализация, мало памяти, быстрый выход к локальному оптимуму. Минусы: застревание в локальном максимуме, плато или на хребте; глобальный оптимум не гарантируется; результат сильно зависит от эвристики.

## Лучшие практики

Выбирайте эвристику, монотонно приближающуюся к цели; избегайте плато (одинаковые значения у соседей). При застревании — случайные рестарты или лимит итераций. Для стабильности рассмотрите Steepest-Ascent. Проверяйте достижимость цели и корректность операторов.

## Реализация на Kotlin

```kotlin
data class StateK(
    val state: List<Stack<String>>,
    val heuristics: Int
) {
    constructor(state: List<Stack<String>>, heuristics: Int) : this(
        state.map { stack ->
            Stack<String>().apply { addAll(stack) }
        },
        heuristics
    )
}
```

```kotlin
class HillClimbingSolverK {
    fun getHeuristicsValue(
        currentState: List<Stack<String>>,
        goalStateStack: Stack<String>
    ): Int {
        return currentState.sumOf { stack ->
            getHeuristicsValueForStack(stack, currentState, goalStateStack)
        }
    }
    
    private fun getHeuristicsValueForStack(
        stack: Stack<String>,
        currentState: List<Stack<String>>,
        goalStateStack: Stack<String>
    ): Int {
        var stackHeuristics = 0
        var isPositionCorrect = true
        var goalStartIndex = 0
        
        for (currentBlock in stack) {
            if (isPositionCorrect &&
                goalStartIndex < goalStateStack.size &&
                currentBlock == goalStateStack[goalStartIndex]) {
                stackHeuristics += goalStartIndex
            } else {
                stackHeuristics -= goalStartIndex
                isPositionCorrect = false
            }
            goalStartIndex++
        }
        
        return stackHeuristics
    }
}
```

## Решение проблем

| Симптом | Возможная причина | Решение |
|---------|-------------------|---------|
| Застревание, нет улучшений | Локальный максимум, плато или хребет | Рестарты из случайного состояния; ограничение итераций; Steepest-Ascent |
| Плохое качество решения | Слабая или не монотонная эвристика | Пересмотреть эвристику; убедиться, что она отражает расстояние до цели |
| Циклы (возврат к одному состоянию) | Плато или боковые переходы с той же оценкой | Запретить повтор состояний; боковое движение с лимитом; рестарт |

## Частые вопросы

**Чем Steepest-Ascent лучше простого Hill-Climbing?** Выбор лучшего среди всех соседей уменьшает риск «первого попавшегося» плохого преемника и часто даёт более стабильную сходимость; минус — больше вычислений на шаг.

**Когда использовать Hill-Climbing?** Когда пространство состояний велико, есть разумная эвристика и приемлем локальный оптимум или есть механизм рестартов.

**Как бороться с плато?** Случайный шаг, ограниченное боковое движение (lateral movement) или рестарт из нового случайного состояния.

## Резюме

Hill-Climbing — итеративное улучшение текущего состояния по эвристике с риском застревания в локальном оптимуме, на плато или хребте. Подходит для больших пространств при наличии хорошей эвристики; для повышения качества — рестарты или Steepest-Ascent.
