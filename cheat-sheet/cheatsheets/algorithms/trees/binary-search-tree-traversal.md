# Binary Search Tree Traversal

Кратко: обход бинарного дерева поиска тремя способами: Inorder (слева-корень-справа), Preorder (корень-слева-справа), и Postorder (слева-справа-корень).

**Дата последнего обновления:** 2025-01-15

## Полезные ссылки

### Официальная документация
- [GeeksforGeeks: Tree Traversals (Inorder, Preorder and Postorder)](https://www.geeksforgeeks.org/tree-traversals-inorder-preorder-and-postorder/)

### Визуализация
- [Visualgo: Binary Tree Traversal](https://visualgo.net/en/bst)

### См. также
- `./binary-tree.md` - бинарное дерево
- `./balanced-binary-tree-check.md` - проверка сбалансированности
- `./avl-tree.md` - самобалансирующееся дерево AVL

## Содержание

- [Описание бинарного дерева поиска](#описание-бинарного-дерева-поиска)
- [Терминология](#терминология)
- [Типы обходов](#типы-обходов)
- [Java Implementation](#java-implementation)
- [Kotlin Implementation](#kotlin-implementation)
- [Сложность](#сложность)

## Описание бинарного дерева поиска

В этом руководстве вы узнаете, что такое бинарное дерево поиска, из каких частей состоит дерево, а также некоторые общие термины, которые мы используем при описании частей дерева.

Мы также увидим, как пройти по дереву, используя некоторые распространенные алгоритмы - все проиллюстрировано наглядными примерами.

### Что такое бинарное дерево поиска?

Бинарное дерево поиска - это бинарное дерево, состоящее из узлов. Каждый узел имеет ключ, обозначающий его значение.

Значение узлов в левом поддереве меньше значения корневого узла. И значение узлов в правом поддереве больше, чем значение корневого узла.

Корневой узел является родительским узлом обоих поддеревьев.

### Визуализация

```
         A
      /     \
     B       C
    / \     / \
   D   E   F   G
  / \
 H   I
```

## Терминология

Давайте посмотрим на отношения между узлами в примере выше:

- **A** является корневым узлом
- Левое поддерево начинается с **B**, а правое поддерево начинается с **C**
- У узла **A** есть два дочерних узла - **B** и **C**
- Узел **C** является родительским узлом для **F** и **G**. **F** и **G** являются братьями и сестрами
- Узлы **F** и **G** называются конечными узлами (листьями), потому что у них нет потомков
- Узел **B** является родительским узлом для **D** и **E**
- Узел **D** является родительским узлом для **H** и **I**
- **D** и **E** являются братьями и сестрами, а также **H** и **I**
- Узел **E** является листовым узлом

Итак, вот несколько важных терминов, которые мы только что использовали для описания дерева выше:

1. **Корень:** самый верхний узел в дереве
2. **Родитель:** узел с дочерним элементом или дочерними элементами
3. **Дочерний:** узел, расширенный из другого узла (родительского узла)
4. **Лист:** узел без дочернего элемента
5. **Братья и сестры:** узлы с общим родителем

## Для чего используется бинарное дерево поиска?

Деревья бинарного поиска помогают нам ускорить наш бинарный поиск, поскольку мы можем быстрее находить элементы.

Мы можем использовать бинарное дерево поиска для добавления и удаления элементов в дереве.

Мы также можем представить данные в ранжированном порядке, используя двоичное дерево. А в некоторых случаях его можно использовать как диаграмму для представления набора информации.

## Типы обходов

Далее мы рассмотрим некоторые приемы, используемые при обходе бинарного дерева.

### Что такое обход дерева?

Обход дерева означает посещение и вывод значения каждого узла в определенном порядке. В этом уроке мы будем использовать методы обхода дерева Inorder, Preorder и Postorder.

Основное значение обхода дерева заключается в том, что существует несколько способов выполнения операций обхода, в отличие от линейных структур данных, таких как массивы, растровые изображения, матрицы, где обход выполняется в линейном порядке.

Каждый из этих методов обхода дерева имеет определенный порядок:

1. **Inorder** - вы переходите от левого поддерева к корню, а затем к правому поддереву
2. **Preorder** - вы переходите от корня к левому поддереву, а затем к правому поддереву
3. **Postorder** - вы переходите от левого поддерева к правому поддереву, а затем к корню

Вот еще один способ представления приведенной выше информации:

1. **Inorder** => Слева, Корень, Справа
2. **Preorder** => Корень, Левый, Правый
3. **Postorder** => Влево, Вправо, Корень

## Java Implementation

### Структура узла

```java
class Node {
    int key;
    Node left;
    Node right;
    
    Node(int key) {
        this.key = key;
        left = right = null;
    }
}
```

### Inorder обход (Центрированный)

Мы собираемся создать дерево, похожее на то, что было в предыдущем разделе, но на этот раз ключами узлов будут числа, а не буквы.

Помните, что значения узлов левого поддерева всегда меньше значения корневого узла. Кроме того, значения узлов в правом поддереве больше, чем значение корневого узла.

Напомним, что порядок неупорядоченного обхода: влево, корень, вправо.

Вот результат, который мы получаем после использования неупорядоченного обхода:

**D, B, E, A, F, C, G**

```java
void inorderTraversal(Node node) {
    if (node != null) {
        inorderTraversal(node.left);
        System.out.print(node.key + " ");
        inorderTraversal(node.right);
    }
}
```

**Особенность:** Inorder обход бинарного дерева поиска дает элементы в отсортированном порядке.

### Preorder обход (Прямой)

Порядок здесь: корень, левый, правый.

Используя ту же диаграмму выше, мы имеем:

**A, B, D, E, C, F, G**

```java
void preorderTraversal(Node node) {
    if (node != null) {
        System.out.print(node.key + " ");
        preorderTraversal(node.left);
        preorderTraversal(node.right);
    }
}
```

**Особенность:** Preorder обход используется для копирования дерева или создания префиксного выражения.

### Postorder обход (Обратный)

Порядок обхода почтового заказа: левый, правый, корень.

Вот результат:

**D, E, B, F, G, C, A**

```java
void postorderTraversal(Node node) {
    if (node != null) {
        postorderTraversal(node.left);
        postorderTraversal(node.right);
        System.out.print(node.key + " ");
    }
}
```

**Особенность:** Postorder обход используется для удаления дерева или создания постфиксного выражения.

### Полная реализация

```java
public class BinaryTreeTraversal {
    Node root;
    
    BinaryTreeTraversal() {
        root = null;
    }
    
    void inorderTraversal(Node node) {
        if (node != null) {
            inorderTraversal(node.left);
            System.out.print(node.key + " ");
            inorderTraversal(node.right);
        }
    }
    
    void preorderTraversal(Node node) {
        if (node != null) {
            System.out.print(node.key + " ");
            preorderTraversal(node.left);
            preorderTraversal(node.right);
        }
    }
    
    void postorderTraversal(Node node) {
        if (node != null) {
            postorderTraversal(node.left);
            postorderTraversal(node.right);
            System.out.print(node.key + " ");
        }
    }
    
    // Обертки для вызова с корня
    void printInorder() {
        inorderTraversal(root);
    }
    
    void printPreorder() {
        preorderTraversal(root);
    }
    
    void printPostorder() {
        postorderTraversal(root);
    }
}
```

### Пример использования

```java
BinaryTreeTraversal tree = new BinaryTreeTraversal();
tree.root = new Node(1);
tree.root.left = new Node(2);
tree.root.right = new Node(3);
tree.root.left.left = new Node(4);
tree.root.left.right = new Node(5);

System.out.println("Inorder traversal:");
tree.printInorder();  // 4 2 5 1 3

System.out.println("\nPreorder traversal:");
tree.printPreorder();  // 1 2 4 5 3

System.out.println("\nPostorder traversal:");
tree.printPostorder();  // 4 5 2 3 1
```

### Итеративные версии

#### Inorder (с использованием стека)

```java
void inorderIterative(Node root) {
    Stack<Node> stack = new Stack<>();
    Node current = root;
    
    while (current != null || !stack.isEmpty()) {
        while (current != null) {
            stack.push(current);
            current = current.left;
        }
        current = stack.pop();
        System.out.print(current.key + " ");
        current = current.right;
    }
}
```

#### Preorder (с использованием стека)

```java
void preorderIterative(Node root) {
    if (root == null) return;
    
    Stack<Node> stack = new Stack<>();
    stack.push(root);
    
    while (!stack.isEmpty()) {
        Node node = stack.pop();
        System.out.print(node.key + " ");
        
        if (node.right != null) {
            stack.push(node.right);
        }
        if (node.left != null) {
            stack.push(node.left);
        }
    }
}
```

#### Postorder (с использованием двух стеков)

```java
void postorderIterative(Node root) {
    if (root == null) return;
    
    Stack<Node> stack1 = new Stack<>();
    Stack<Node> stack2 = new Stack<>();
    stack1.push(root);
    
    while (!stack1.isEmpty()) {
        Node node = stack1.pop();
        stack2.push(node);
        
        if (node.left != null) {
            stack1.push(node.left);
        }
        if (node.right != null) {
            stack1.push(node.right);
        }
    }
    
    while (!stack2.isEmpty()) {
        System.out.print(stack2.pop().key + " ");
    }
}
```

## Kotlin Implementation

### Структура узла

```kotlin
class NodeK(var key: Int) {
    var left: NodeK? = null
    var right: NodeK? = null
}
```

### Рекурсивные обходы

```kotlin
class BinaryTreeTraversalK {
    var root: NodeK? = null
    
    fun inorderTraversalK(node: NodeK?) {
        if (node != null) {
            inorderTraversalK(node.left)
            print("${node.key} ")
            inorderTraversalK(node.right)
        }
    }
    
    fun preorderTraversalK(node: NodeK?) {
        if (node != null) {
            print("${node.key} ")
            preorderTraversalK(node.left)
            preorderTraversalK(node.right)
        }
    }
    
    fun postorderTraversalK(node: NodeK?) {
        if (node != null) {
            postorderTraversalK(node.left)
            postorderTraversalK(node.right)
            print("${node.key} ")
        }
    }
    
    fun printInorderK() = inorderTraversalK(root)
    fun printPreorderK() = preorderTraversalK(root)
    fun printPostorderK() = postorderTraversalK(root)
}
```

### Итеративные версии

```kotlin
fun inorderIterativeK(root: NodeK?) {
    val stack = mutableListOf<NodeK>()
    var current = root
    
    while (current != null || stack.isNotEmpty()) {
        while (current != null) {
            stack.add(current)
            current = current.left
        }
        current = stack.removeAt(stack.size - 1)
        print("${current.key} ")
        current = current.right
    }
}

fun preorderIterativeK(root: NodeK?) {
    if (root == null) return
    
    val stack = mutableListOf(root)
    
    while (stack.isNotEmpty()) {
        val node = stack.removeAt(stack.size - 1)
        print("${node.key} ")
        
        node.right?.let { stack.add(it) }
        node.left?.let { stack.add(it) }
    }
}

fun postorderIterativeK(root: NodeK?) {
    if (root == null) return
    
    val stack1 = mutableListOf(root)
    val stack2 = mutableListOf<NodeK>()
    
    while (stack1.isNotEmpty()) {
        val node = stack1.removeAt(stack1.size - 1)
        stack2.add(node)
        
        node.left?.let { stack1.add(it) }
        node.right?.let { stack1.add(it) }
    }
    
    while (stack2.isNotEmpty()) {
        print("${stack2.removeAt(stack2.size - 1).key} ")
    }
}
```

### Пример использования

```kotlin
fun main() {
    val tree = BinaryTreeTraversalK()
    tree.root = NodeK(1).apply {
        left = NodeK(2).apply {
            left = NodeK(4)
            right = NodeK(5)
        }
        right = NodeK(3)
    }
    
    print("Inorder: ")
    tree.printInorderK() // 4 2 5 1 3
    
    print("\nPreorder: ")
    tree.printPreorderK() // 1 2 4 5 3
    
    print("\nPostorder: ")
    tree.printPostorderK() // 4 5 2 3 1
}
```

## Сложность

### Временная сложность

- **Все обходы:** O(n), где n - количество узлов в дереве

Каждый узел посещается ровно один раз.

### Пространственная сложность

- **Рекурсивные версии:** O(h), где h - высота дерева (для стека вызовов)
- **Итеративные версии:** O(h) для стека
- **В худшем случае:** O(n) - для вырожденного дерева
- **В лучшем случае:** O(log(n)) - для сбалансированного дерева

## Сравнение обходов

| Обход | Порядок | Применение |
|-------|---------|------------|
| Inorder | Лево → Корень → Право | Сортировка, получение элементов в порядке |
| Preorder | Корень → Лево → Право | Копирование дерева, префиксные выражения |
| Postorder | Лево → Право → Корень | Удаление дерева, постфиксные выражения |

## Особенности

- **Inorder:** Для BST дает элементы в отсортированном порядке
- **Preorder:** Первый элемент всегда корень
- **Postorder:** Последний элемент всегда корень
- **Универсальность:** Все три обхода работают с любым бинарным деревом

## Применение

Обходы дерева используются в:

- Вычислении выражений (префиксные, инфиксные, постфиксные)
- Копировании и сериализации деревьев
- Удалении деревьев
- Поиске элементов
- Валидации структуры дерева

## Когда использовать

### Используйте Inorder, когда:

- Нужны элементы в отсортированном порядке (для BST)
- Вычисляете инфиксные выражения
- Нужна валидация BST

### Используйте Preorder, когда:

- Копируете дерево
- Создаете префиксные выражения
- Нужно быстро получить структуру дерева

### Используйте Postorder, когда:

- Удаляете дерево
- Вычисляете постфиксные выражения
- Нужно обработать дочерние узлы перед родительским

## Заключение

В этом руководстве мы рассмотрели три основных способа обхода бинарного дерева поиска: Inorder, Preorder и Postorder. Каждый обход имеет свои особенности и применения. Понимание этих обходов важно для работы с деревьями и реализации различных алгоритмов.
