# Balanced Binary Tree Check

Кратко: проверка, является ли бинарное дерево сбалансированным. Сбалансированное дерево - это дерево, в котором для каждого поддерева разница высот левого и правого поддеревьев не превышает 1.

**Дата последнего обновления:** 2025-01-15

## Полезные ссылки

### Официальная документация
- [GeeksforGeeks: Check if a binary tree is balanced](https://www.geeksforgeeks.org/how-to-determine-if-a-binary-tree-is-balanced/)

### См. также
- `./binary-tree.md` - бинарное дерево
- `./avl-tree.md` - самобалансирующееся дерево AVL
- `./binary-search-tree-traversal.md` - обходы бинарного дерева поиска

## Содержание

- [Описание алгоритма](#описание-алгоритма)
- [Определения](#определения)
- [Java Implementation](#java-implementation)
- [Kotlin Implementation](#kotlin-implementation)
- [Сложность](#сложность)

## Описание алгоритма

Деревья - одна из самых важных структур данных в информатике. Обычно нас интересует сбалансированное дерево из-за его ценных свойств. Их структура позволяет выполнять такие операции, как запросы, вставки, удаления за логарифмическое время.

В этом уроке мы узнаем, как определить, сбалансировано ли бинарное дерево.

## Определения

Во-первых, давайте введем несколько определений, чтобы убедиться, что мы находимся на одной странице:

1. **Бинарное дерево** - вид дерева, в котором каждый узел имеет ноль, одного или двух дочерних элементов
2. **Высота дерева** - максимальное расстояние от корня до листа (такое же, как глубина самого глубокого листа)
3. **Сбалансированное дерево** - вид дерева, в котором для каждого поддерева максимальное расстояние от корня до любого листа не более чем на единицу больше, чем минимальное расстояние от корня до любого листа

### Пример сбалансированного дерева

```
        1
      /   \
     2     3
    / \   / \
   4   5 6   7
```

В этом дереве для каждого узла разница высот левого и правого поддеревьев не превышает 1.

### Пример несбалансированного дерева

```
        1
      /
     2
    /
   3
  /
 4
```

В этом дереве разница высот значительно превышает 1.

## Java Implementation

### Реализация

Итак, начнем с класса для нашего дерева:

```java
public class Tree {
    private int value;
    private Tree left;
    private Tree right;
    
    public Tree(int value, Tree left, Tree right) {
        this.value = value;
        this.left = left;
        this.right = right;
    }
    
    public Tree left() {
        return left;
    }
    
    public Tree right() {
        return right;
    }
}
```

Для простоты предположим, что каждый узел имеет целочисленное значение. Обратите внимание, что если левое и правое деревья равны null, это означает, что наш узел является листом.

### Класс Result

Прежде чем мы представим наш основной метод, давайте посмотрим, что он должен возвращать:

```java
private class Result {
    private boolean isBalanced;
    private int height;
    
    private Result(boolean isBalanced, int height) {
        this.isBalanced = isBalanced;
        this.height = height;
    }
}
```

Таким образом, для каждого вызова у нас будет информация о высоте и балансе.

### Рекурсивный метод

Имея определение сбалансированного дерева, мы можем придумать алгоритм. Что нам нужно сделать, так это проверить желаемое свойство для каждого узла. Это может быть легко достигнуто с помощью рекурсивного обхода поиска в глубину.

Теперь наш рекурсивный метод будет вызываться для каждого узла. Кроме того, он будет отслеживать текущую глубину. Каждый вызов будет возвращать информацию о высоте и балансе.

Теперь давайте посмотрим на наш метод поиска в глубину:

```java
private Result isBalancedRecursive(Tree tree, int depth) {
    if (tree == null) {
        return new Result(true, -1);
    }
    
    Result leftSubtreeResult = isBalancedRecursive(tree.left(), depth + 1);
    Result rightSubtreeResult = isBalancedRecursive(tree.right(), depth + 1);
    
    boolean isBalanced = Math.abs(leftSubtreeResult.height - rightSubtreeResult.height) <= 1;
    boolean subtreesAreBalanced = leftSubtreeResult.isBalanced && rightSubtreeResult.isBalanced;
    
    int height = Math.max(leftSubtreeResult.height, rightSubtreeResult.height) + 1;
    
    return new Result(isBalanced && subtreesAreBalanced, height);
}
```

Во-первых, нам нужно рассмотреть случай, если наш узел равен null: мы вернем true (что означает, что дерево сбалансировано) и -1 в качестве высоты.

Затем мы делаем два рекурсивных вызова для левого и правого поддеревьев, сохраняя актуальность глубины.

На данный момент у нас есть вычисления, выполненные для дочерних элементов текущего узла. Теперь у нас есть все необходимые данные для проверки баланса:

1. переменная `isBalanced` проверяет разницу высот детей
2. `subtreesAreBalanced` указывает, сбалансированы ли поддеревья

Наконец, мы можем вернуть информацию о балансе и высоте. Также было бы неплохо упростить первый рекурсивный вызов методом фасада:

```java
public boolean isBalanced(Tree tree) {
    return isBalancedRecursive(tree, -1).isBalanced;
}
```

### Упрощенная версия (без класса Result)

Можно также использовать более простой подход с возвратом -1 для несбалансированного дерева:

```java
public int checkBalance(Tree tree) {
    if (tree == null) {
        return 0;
    }
    
    int leftHeight = checkBalance(tree.left());
    if (leftHeight == -1) {
        return -1;
    }
    
    int rightHeight = checkBalance(tree.right());
    if (rightHeight == -1) {
        return -1;
    }
    
    if (Math.abs(leftHeight - rightHeight) > 1) {
        return -1;
    }
    
    return Math.max(leftHeight, rightHeight) + 1;
}

public boolean isBalancedSimple(Tree tree) {
    return checkBalance(tree) != -1;
}
```

### Пример использования

```java
// Сбалансированное дерево
Tree balanced = new Tree(1,
    new Tree(2, new Tree(4, null, null), new Tree(5, null, null)),
    new Tree(3, new Tree(6, null, null), new Tree(7, null, null))
);
assertTrue(isBalanced(balanced));

// Несбалансированное дерево
Tree unbalanced = new Tree(1,
    new Tree(2,
        new Tree(3,
            new Tree(4, null, null),
            null),
        null),
    null
);
assertFalse(isBalanced(unbalanced));
```

## Kotlin Implementation

### Класс Tree

```kotlin
data class TreeK(
    val value: Int,
    val left: TreeK? = null,
    val right: TreeK? = null
)
```

### Класс Result

```kotlin
private data class ResultK(
    val isBalanced: Boolean,
    val height: Int
)
```

### Рекурсивный метод

```kotlin
private fun isBalancedRecursiveK(tree: TreeK?, depth: Int): ResultK {
    if (tree == null) {
        return ResultK(true, -1)
    }
    
    val leftSubtreeResult = isBalancedRecursiveK(tree.left, depth + 1)
    val rightSubtreeResult = isBalancedRecursiveK(tree.right, depth + 1)
    
    val isBalanced = Math.abs(leftSubtreeResult.height - rightSubtreeResult.height) <= 1
    val subtreesAreBalanced = leftSubtreeResult.isBalanced && rightSubtreeResult.isBalanced
    
    val height = Math.max(leftSubtreeResult.height, rightSubtreeResult.height) + 1
    
    return ResultK(isBalanced && subtreesAreBalanced, height)
}

fun isBalancedK(tree: TreeK?): Boolean {
    return isBalancedRecursiveK(tree, -1).isBalanced
}
```

### Упрощенная версия

```kotlin
fun checkBalanceK(tree: TreeK?): Int {
    if (tree == null) {
        return 0
    }
    
    val leftHeight = checkBalanceK(tree.left)
    if (leftHeight == -1) {
        return -1
    }
    
    val rightHeight = checkBalanceK(tree.right)
    if (rightHeight == -1) {
        return -1
    }
    
    if (Math.abs(leftHeight - rightHeight) > 1) {
        return -1
    }
    
    return Math.max(leftHeight, rightHeight) + 1
}

fun isBalancedSimpleK(tree: TreeK?): Boolean {
    return checkBalanceK(tree) != -1
}
```

### Пример использования

```kotlin
fun main() {
    // Сбалансированное дерево
    val balanced = TreeK(1,
        TreeK(2, TreeK(4), TreeK(5)),
        TreeK(3, TreeK(6), TreeK(7))
    )
    println(isBalancedK(balanced)) // true
    
    // Несбалансированное дерево
    val unbalanced = TreeK(1,
        TreeK(2, TreeK(3, TreeK(4), null), null),
        null
    )
    println(isBalancedK(unbalanced)) // false
}
```

## Сложность

### Временная сложность

- **Все случаи:** O(n), где n - количество узлов в дереве

Каждый узел посещается ровно один раз.

### Пространственная сложность

- **Все случаи:** O(h), где h - высота дерева

Пространственная сложность определяется глубиной рекурсивного стека вызовов, которая равна высоте дерева.

- **В худшем случае:** O(n) - для вырожденного дерева (список)
- **В лучшем случае:** O(log(n)) - для полностью сбалансированного дерева

## Особенности

- **Эффективность:** Алгоритм проверяет баланс за один проход
- **Оптимизация:** Можно остановить проверку раньше, если найдено несбалансированное поддерево
- **Рекурсивность:** Естественно рекурсивная структура дерева

## Применение

Проверка баланса дерева используется в:

- Оптимизации производительности операций с деревом
- Валидации структуры данных
- Реализации самобалансирующихся деревьев (AVL, Red-Black)
- Анализе производительности алгоритмов

## Сравнение с другими структурами

| Структура | Балансировка | Временная сложность поиска |
|-----------|--------------|----------------------------|
| Обычное BST | Нет | O(n) в худшем случае |
| Сбалансированное BST | Да | O(log(n)) |
| AVL Tree | Автоматическая | O(log(n)) |
| Red-Black Tree | Автоматическая | O(log(n)) |

## Когда использовать

### Проверяйте баланс дерева, когда:

- Нужна гарантированная производительность операций
- Дерево часто модифицируется
- Важна стабильность времени выполнения
- Реализуете самобалансирующееся дерево

### Альтернативы:

- **Использовать самобалансирующиеся деревья:** AVL, Red-Black автоматически поддерживают баланс
- **Периодическая перебалансировка:** Если дерево редко изменяется

## Заключение

В этой статье мы обсудили, как определить, сбалансировано ли бинарное дерево. Мы объяснили подход поиска в глубину, который эффективно проверяет баланс дерева за время O(n) и пространство O(h).
