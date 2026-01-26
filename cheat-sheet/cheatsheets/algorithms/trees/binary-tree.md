# Binary Tree

Кратко: бинарное дерево - это рекурсивная структура данных, в которой каждый узел может иметь не более двух дочерних элементов. Рассматриваются операции вставки, поиска, удаления и обхода.

**Дата последнего обновления:** 2025-01-15

## Полезные ссылки

### Официальная документация
- [GeeksforGeeks: Binary Tree](https://www.geeksforgeeks.org/binary-tree-data-structure/)

### Визуализация
- [Visualgo: Binary Tree](https://visualgo.net/en/binarytree)

### См. также
- `./balanced-binary-tree-check.md` - проверка сбалансированности дерева
- `./binary-search-tree-traversal.md` - обходы бинарного дерева поиска
- `./avl-tree.md` - самобалансирующееся дерево AVL

## Содержание

- [Описание структуры данных](#описание-структуры-данных)
- [Java Implementation](#java-implementation)
- [Kotlin Implementation](#kotlin-implementation)
- [Обход дерева](#обход-дерева)
- [Сложность](#сложность)

## Описание структуры данных

В этом руководстве мы рассмотрим реализацию двоичного дерева в Java.

Для этого руководства мы будем использовать отсортированное двоичное дерево, содержащее значения int.

Бинарное дерево - это рекурсивная структура данных, в которой каждый узел может иметь не более двух дочерних элементов.

Распространенным типом бинарного дерева является бинарное дерево поиска, в котором каждый узел имеет значение, которое больше или равно значениям узлов в левом поддереве и меньше или равно значениям узлов в правом поддереве.

### Визуализация

```
        6
      /   \
     4     8
    / \   / \
   3   5 7   9
```

## Java Implementation

### Реализация узла

Для реализации мы будем использовать вспомогательный класс Node, который будет хранить значения int и сохранять ссылку на каждого дочернего элемента:

```java
class Node {
    int value;
    Node left;
    Node right;
    
    Node(int value) {
        this.value = value;
        right = null;
        left = null;
    }
}
```

Затем мы добавим начальный узел нашего дерева, обычно называемый корнем:

```java
public class BinaryTree {
    Node root;
}
```

## Операции с деревом

Теперь давайте посмотрим на наиболее распространенные операции, которые мы можем выполнять с бинарным деревом.

### Вставка узлов

Первая операция, которую мы рассмотрим, - это вставка новых узлов.

Во-первых, мы должны найти место, где мы хотим добавить новый узел, чтобы сохранить сортировку дерева. Мы будем следовать этим правилам, начиная с корневого узла:

1. если значение нового узла ниже, чем значение текущего узла, мы переходим к левому дочернему элементу
2. если значение нового узла больше, чем значение текущего узла, мы переходим к правому дочернему элементу
3. когда текущий узел равен null, мы достигли конечного узла и можем вставить новый узел в эту позицию

Затем мы создадим рекурсивный метод для вставки:

```java
private Node addRecursive(Node current, int value) {
    if (current == null) {
        return new Node(value);
    }
    
    if (value < current.value) {
        current.left = addRecursive(current.left, value);
    } else if (value > current.value) {
        current.right = addRecursive(current.right, value);
    } else {
        return current;
    }
    
    return current;
}
```

Далее мы создадим общедоступный метод, который запускает рекурсию с корневого узла:

```java
public void add(int value) {
    root = addRecursive(root, value);
}
```

Давайте посмотрим, как мы можем использовать этот метод для создания дерева из нашего примера:

```java
private BinaryTree createBinaryTree() {
    BinaryTree bt = new BinaryTree();
    
    bt.add(6);
    bt.add(4);
    bt.add(8);
    bt.add(3);
    bt.add(5);
    bt.add(7);
    bt.add(9);
    
    return bt;
}
```

### Поиск узла

Теперь добавим метод для проверки наличия в дереве определенного значения.

Как и раньше, мы сначала создадим рекурсивный метод, который проходит по дереву:

```java
private boolean containsNodeRecursive(Node current, int value) {
    if (current == null) {
        return false;
    }
    
    if (value == current.value) {
        return true;
    }
    
    return value < current.value
        ? containsNodeRecursive(current.left, value)
        : containsNodeRecursive(current.right, value);
}
```

Здесь мы ищем значение, сравнивая его со значением в текущем узле; затем мы продолжим в левом или правом дочернем элементе в зависимости от результата.

Далее мы создадим общедоступный метод, который начинается с корня:

```java
public boolean containsNode(int value) {
    return containsNodeRecursive(root, value);
}
```

Затем мы создадим простой тест, чтобы убедиться, что дерево действительно содержит вставленные элементы:

```java
@Test
public void givenABinaryTree_WhenAddingElements_ThenTreeContainsThoseElements() {
    BinaryTree bt = createBinaryTree();
    
    assertTrue(bt.containsNode(6));
    assertTrue(bt.containsNode(4));
    assertFalse(bt.containsNode(1));
}
```

Все добавленные узлы должны содержаться в дереве.

### Удаление узла

Другой распространенной операцией является удаление узла из дерева.

Во-первых, мы должны найти удаляемый узел так же, как и раньше:

```java
private Node deleteRecursive(Node current, int value) {
    if (current == null) {
        return null;
    }
    
    if (value == current.value) {
        // Узел для удаления найден
    }
    
    if (value < current.value) {
        current.left = deleteRecursive(current.left, value);
        return current;
    }
    
    current.right = deleteRecursive(current.right, value);
    return current;
}
```

Как только мы находим узел для удаления, есть 3 основных разных случая:

1. **узел не имеет потомков** - это самый простой случай; нам просто нужно заменить этот узел на null в его родительском узле
2. **у узла есть ровно один дочерний элемент** - в родительском узле мы заменяем этот узел его единственным дочерним элементом
3. **узел имеет двух детей** - это самый сложный случай, потому что он требует реорганизации дерева

Давайте посмотрим, как бы мы реализовали первый случай, когда узел является листовым узлом:

```java
if (current.left == null && current.right == null) {
    return null;
}
```

Теперь продолжим со случаем, когда у узла есть один дочерний элемент:

```java
if (current.right == null) {
    return current.left;
}

if (current.left == null) {
    return current.right;
}
```

Здесь мы возвращаем ненулевой дочерний элемент, чтобы его можно было назначить родительскому узлу.

Наконец, нам нужно обработать случай, когда у узла есть два потомка.

Во-первых, нам нужно найти узел, который заменит удаленный узел. Мы будем использовать наименьший узел правого поддерева узла, который скоро будет удален:

```java
private int findSmallestValue(Node root) {
    return root.left == null ? root.value : findSmallestValue(root.left);
}
```

Затем мы присваиваем наименьшее значение удаляемому узлу, после чего удаляем его из правого поддерева:

```java
int smallestValue = findSmallestValue(current.right);
current.value = smallestValue;
current.right = deleteRecursive(current.right, smallestValue);
return current;
```

Наконец, мы создадим общедоступный метод, который запускает удаление из корня:

```java
public void delete(int value) {
    root = deleteRecursive(root, value);
}
```

Теперь давайте проверим, что удаление сработало как положено:

```java
@Test
public void givenABinaryTree_WhenDeletingElements_ThenTreeDoesNotContainThoseElements() {
    BinaryTree bt = createBinaryTree();
    
    assertTrue(bt.containsNode(9));
    bt.delete(9);
    assertFalse(bt.containsNode(9));
}
```

## Kotlin Implementation

В Kotlin бинарное дерево может быть реализовано следующим образом:

```kotlin
data class Node(
    var value: Int,
    var left: Node? = null,
    var right: Node? = null
)

class BinaryTree {
    var root: Node? = null

    fun add(value: Int) {
        root = addRecursive(root, value)
    }

    private fun addRecursive(current: Node?, value: Int): Node {
        if (current == null) {
            return Node(value)
        }

        when {
            value < current.value -> current.left = addRecursive(current.left, value)
            value > current.value -> current.right = addRecursive(current.right, value)
        }

        return current
    }

    fun containsNode(value: Int): Boolean {
        return containsNodeRecursive(root, value)
    }

    private fun containsNodeRecursive(current: Node?, value: Int): Boolean {
        if (current == null) {
            return false
        }

        return when {
            value == current.value -> true
            value < current.value -> containsNodeRecursive(current.left, value)
            else -> containsNodeRecursive(current.right, value)
        }
    }

    fun delete(value: Int) {
        root = deleteRecursive(root, value)
    }

    private fun deleteRecursive(current: Node?, value: Int): Node? {
        if (current == null) {
            return null
        }

        when {
            value == current.value -> {
                when {
                    current.left == null && current.right == null -> return null
                    current.right == null -> return current.left
                    current.left == null -> return current.right
                    else -> {
                        val smallestValue = findSmallestValue(current.right!!)
                        current.value = smallestValue
                        current.right = deleteRecursive(current.right, smallestValue)
                        return current
                    }
                }
            }
            value < current.value -> {
                current.left = deleteRecursive(current.left, value)
                return current
            }
            else -> {
                current.right = deleteRecursive(current.right, value)
                return current
            }
        }
    }

    private fun findSmallestValue(root: Node): Int {
        return root.left?.let { findSmallestValue(it) } ?: root.value
    }
}
```

Использование:

```kotlin
fun main() {
    val bt = BinaryTree()
    bt.add(6)
    bt.add(4)
    bt.add(8)
    bt.add(3)
    bt.add(5)
    bt.add(7)
    bt.add(9)

    println(bt.containsNode(5)) // true
    bt.delete(5)
    println(bt.containsNode(5)) // false
}
```

### Обход дерева в Kotlin

```kotlin
fun traverseInOrder(node: Node?) {
    node?.let {
        traverseInOrder(it.left)
        print(" ${it.value}")
        traverseInOrder(it.right)
    }
}

fun traversePreOrder(node: Node?) {
    node?.let {
        print(" ${it.value}")
        traversePreOrder(it.left)
        traversePreOrder(it.right)
    }
}

fun traversePostOrder(node: Node?) {
    node?.let {
        traversePostOrder(it.left)
        traversePostOrder(it.right)
        print(" ${it.value}")
    }
}
```

## Обход дерева

В этом разделе мы рассмотрим различные способы обхода дерева, подробно рассмотрев поиск в глубину и в ширину.

### Inorder (Центрированный обход)

Inorder обход: левое поддерево → корень → правое поддерево

```java
public void traverseInOrder(Node node) {
    if (node != null) {
        traverseInOrder(node.left);
        System.out.print(" " + node.value);
        traverseInOrder(node.right);
    }
}
```

### Preorder (Прямой обход)

Preorder обход: корень → левое поддерево → правое поддерево

```java
public void traversePreOrder(Node node) {
    if (node != null) {
        System.out.print(" " + node.value);
        traversePreOrder(node.left);
        traversePreOrder(node.right);
    }
}
```

### Postorder (Обратный обход)

Postorder обход: левое поддерево → правое поддерево → корень

```java
public void traversePostOrder(Node node) {
    if (node != null) {
        traversePostOrder(node.left);
        traversePostOrder(node.right);
        System.out.print(" " + node.value);
    }
}
```

### Level Order (Обход по уровням)

Обход по уровням использует очередь:

```java
public void traverseLevelOrder() {
    if (root == null) {
        return;
    }
    
    Queue<Node> nodes = new LinkedList<>();
    nodes.add(root);
    
    while (!nodes.isEmpty()) {
        Node node = nodes.remove();
        System.out.print(" " + node.value);
        
        if (node.left != null) {
            nodes.add(node.left);
        }
        
        if (node.right != null) {
            nodes.add(node.right);
        }
    }
}
```

## Сложность

### Временная сложность

| Операция | Средний случай | Худший случай |
|----------|----------------|---------------|
| Поиск | O(log(n)) | O(n) |
| Вставка | O(log(n)) | O(n) |
| Удаление | O(log(n)) | O(n) |
| Обход | O(n) | O(n) |

### Пространственная сложность

- **Все операции:** O(h), где h - высота дерева
- **В худшем случае:** O(n) - для вырожденного дерева (список)
- **В лучшем случае:** O(log(n)) - для сбалансированного дерева

## Особенности

- **Рекурсивная структура:** Каждое поддерево само является бинарным деревом
- **Сортировка:** Бинарное дерево поиска автоматически поддерживает порядок
- **Гибкость:** Легко расширяется для различных операций

## Применение

Бинарные деревья используются в:

- Базах данных (индексы)
- Файловых системах
- Компиляторах (синтаксические деревья)
- Выражениях (деревья выражений)
- Алгоритмах сжатия (деревья Хаффмана)

## Когда использовать

### Используйте бинарное дерево, когда:

- Нужна быстрая вставка и поиск
- Данные должны быть отсортированы
- Нужна гибкая структура данных
- Важна производительность поиска

### Альтернативы:

- **HashSet/HashMap:** Для O(1) поиска без сортировки
- **SortedSet/SortedMap:** Для автоматической сортировки с балансировкой
- **Массивы:** Для простых случаев с фиксированным размером

## Заключение

В этом руководстве мы рассмотрели реализацию бинарного дерева в Java, включая основные операции вставки, поиска, удаления и различные способы обхода дерева. Бинарные деревья являются фундаментальной структурой данных, которая используется во многих алгоритмах и приложениях.
