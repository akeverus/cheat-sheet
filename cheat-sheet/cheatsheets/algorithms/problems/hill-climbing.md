# Hill-Climbing

Руководство по алгоритму Hill-Climbing и его реализации в Java для решения задач оптимизации.

**Последнее обновление:** 2025-01-15

## Полезные ссылки

### Официальная документация
- [Hill Climbing Algorithm - Wikipedia](https://en.wikipedia.org/wiki/Hill_climbing)
- [Heuristic Search Algorithms](https://www.geeksforgeeks.org/hill-climbing-algorithm-in-ai/)

### См. также
- [Multi-Swarm](./multi-swarm.md)
- [Генетические алгоритмы](../ai-collections/genetic-algorithms.md)
- [Алгоритмы оптимизации](./greedy-algorithms.md)

## Содержание

- [Обзор](#обзор)
- [Алгоритм генерации и тестирования](#алгоритм-генерации-и-тестирования)
- [Что такое Hill-Climbing?](#что-такое-hill-climbing)
- [Алгоритм восхождения на холм](#алгоритм-восхождения-на-холм)
- [Выбор эвристической функции](#выбор-эвристической-функции)
- [Java Implementation](#java-implementation)
- [Kotlin Implementation](#kotlin-implementation)
- [Варианты алгоритма](#варианты-алгоритма)
- [Проблемы и решения](#проблемы-и-решения)

## Обзор

В этом уроке мы покажем алгоритм Hill-Climbing и его реализацию. Мы также рассмотрим его преимущества и недостатки. Прежде чем перейти непосредственно к этому, давайте кратко обсудим алгоритмы генерации и тестирования.

## Алгоритм генерации и тестирования

Это очень простая техника, которая позволяет нам алгоритмизировать поиск решений:

1. Определить текущее состояние как начальное состояние
2. Примените любую возможную операцию к текущему состоянию и сгенерируйте возможное решение
3. Сравните вновь созданное решение с целевым состоянием
4. Если цель достигнута или новые состояния не могут быть созданы, выйдите. В противном случае вернитесь к шагу 2

Очень хорошо работает с простыми задачами. Поскольку это исчерпывающий поиск, его невозможно рассматривать при работе с большими проблемными пространствами. Он также известен как алгоритм Британского музея (попытка найти артефакт в Британском музее, исследуя его случайным образом).

Это также основная идея Hill-Climbing Attack в мире биометрии. Этот подход может быть использован для генерации синтетических биометрических данных.

## Что такое Hill-Climbing?

В технике альпинизма, начиная с подножия холма, мы идем вверх, пока не достигнем вершины холма. Другими словами, мы начинаем с начального состояния и продолжаем улучшать решение до тех пор, пока оно не станет оптимальным.

Это вариант алгоритма генерации и тестирования, который отбрасывает все состояния, которые не выглядят многообещающими или вряд ли приведут нас к целевому состоянию. Для принятия таких решений он использует эвристику (функцию оценки), которая показывает, насколько близко текущее состояние к целевому состоянию.

**Проще говоря:** Hill-Climbing = генерация-и-тестирование + эвристика

## Алгоритм восхождения на холм

Давайте посмотрим на алгоритм восхождения на простой холм:

1. Определите текущее состояние как начальное состояние
2. Цикл до тех пор, пока не будет достигнуто целевое состояние или к текущему состоянию нельзя будет применить больше операторов:
3. Применить операцию к текущему состоянию и получить новое состояние
4. Сравните новое состояние с целью
5. Выйти, если целевое состояние достигнуто
6. Оцените новое состояние с помощью эвристической функции и сравните его с текущим состоянием
7. Если новое состояние ближе к цели по сравнению с текущим состоянием, обновите текущее состояние

Как мы видим, он достигает целевого состояния с итеративными улучшениями. В алгоритме Hill-Climbing поиск цели эквивалентен достижению вершины холма.

Алгоритм восхождения на холм можно отнести к категории информированного поиска. Таким образом, мы можем реализовать любой поиск на основе узлов или задачи, такие как проблема n-королев, используя его.

## Выбор эвристической функции

Ключевым моментом при решении любой задачи о восхождении на холм является выбор подходящей эвристической функции.

Определим такую функцию h:

```
h(x) = +1 для всех блоков опорной конструкции, если блок расположен правильно, 
       в противном случае -1 для всех блоков опорной конструкции.
```

Здесь мы будем называть любой блок правильно расположенным, если он имеет ту же структуру поддержки, что и целевое состояние.

## Java Implementation

### Реализация

Теперь давайте реализуем пример, используя алгоритм Hill-Climbing.

Прежде всего, нам нужен класс State, в котором будет храниться список стеков, представляющих позиции блоков в каждом состоянии. Он также будет хранить эвристики для этого конкретного состояния:

```java
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

Нам также нужен метод, который будет вычислять эвристическое значение состояния:

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

Кроме того, нам нужно определить операторные методы, которые дадут нам новое состояние. Для нашего примера мы определим два из этих методов:

1. Извлеките блок из стека и поместите его в новый стек
2. Извлеките блок из стека и поместите его в один из других стеков

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

Теперь, когда у нас есть вспомогательные методы, давайте напишем метод для реализации техники подъема в гору:

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

### Steepest-Ascent Hill-Climbing

Алгоритм Steepest-Ascent Hill-Climbing (поиск градиента) является вариантом алгоритма Hill Climbing. Мы можем реализовать это с небольшими изменениями в нашем простом алгоритме. В этом алгоритме мы рассматриваем все возможные состояния из текущего состояния, а затем выбираем лучшее из них в качестве преемника, в отличие от простого метода восхождения на холм.

Другими словами, в случае метода восхождения на холм мы выбрали в качестве преемника любое состояние, которое было ближе к цели, чем текущее состояние, тогда как в алгоритме восхождения на вершину с самым крутым подъемом мы выбираем наилучшего преемника из всех возможных преемников, а затем обновляем текущее состояние.

## Проблемы и решения

Hill Climbing - недальновидный метод, поскольку он оценивает только ближайшие возможности. Таким образом, он может оказаться в нескольких ситуациях, из которых он не сможет выбрать какие-либо дальнейшие состояния. Давайте посмотрим на эти состояния и некоторые решения для них:

### 1. Локальный максимум

Это состояние лучше всех соседей, но существует лучшее состояние, которое далеко от текущего состояния; если локальный максимум находится в пределах видимости решения, он известен как «предгорье».

**Решения:**
- Вернуться к одному из предыдущих состояний и исследовать другие направления
- Пропустить несколько состояний и сделать прыжок в новых направлениях
- Использовать случайные рестарты

### 2. Плато

В этом состоянии все соседние состояния имеют одинаковые эвристические значения, поэтому неясно выбрать следующее состояние путем локальных сравнений.

**Решения:**
- Сделать большой шаг в случайном направлении
- Использовать боковое движение (lateral movement)

### 3. Хребет

Это область, которая выше окружающих штатов, но до нее нельзя добраться за один ход; например, у нас есть четыре возможных направления для исследования (N, E, W, S), и существует область в северо-восточном направлении.

**Решения:**
- Исследовать несколько направлений одновременно
- Использовать комбинацию нескольких операторов

## Преимущества и недостатки

### Преимущества

- Простота реализации
- Эффективен для задач с хорошими эвристиками
- Требует мало памяти
- Быстро находит локальные оптимумы

### Недостатки

- Может застрять в локальных максимумах
- Не гарантирует нахождение глобального оптимума
- Зависит от качества эвристической функции
- Может зациклиться на плато

## Kotlin Implementation

### Класс State

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

### Hill Climbing Solver

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

## Резюме

В этом уроке мы рассмотрели алгоритм Hill-Climbing и его реализацию в Java.

Ключевые моменты:
- Hill-Climbing использует эвристику для направления поиска
- Алгоритм итеративно улучшает решение
- Может застрять в локальных оптимумах
- Требует хорошей эвристической функции
- Есть несколько вариантов для преодоления ограничений

Алгоритм особенно полезен для задач, где:
- Пространство состояний велико
- Есть хорошая эвристическая функция
- Локальный оптимум приемлем
- Требуется быстрое решение
