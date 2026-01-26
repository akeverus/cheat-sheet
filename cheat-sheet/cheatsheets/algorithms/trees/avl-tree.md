# AVL Tree

Кратко: дерево AVL - это самобалансирующееся двоичное дерево поиска (BST), названное в честь его изобретателей Адельсона-Вельского и Лэндиса. Поддерживает баланс через вращения, гарантируя высоту O(log(n)).

**Дата последнего обновления:** 2025-01-15

## Полезные ссылки

### Официальная документация
- [GeeksforGeeks: AVL Tree](https://www.geeksforgeeks.org/avl-tree-set-1-insertion/)

### Визуализация
- [Visualgo: AVL Tree](https://visualgo.net/en/bst)

### См. также
- `./binary-tree.md` - бинарное дерево
- `./balanced-binary-tree-check.md` - проверка сбалансированности
- `./binary-search-tree-traversal.md` - обходы бинарного дерева поиска

## Содержание

- [Описание структуры данных](#описание-структуры-данных)
- [Коэффициент баланса](#коэффициент-баланса)
- [Операции вращения](#операции-вращения)
- [Java Implementation](#java-implementation)
- [Kotlin Implementation](#kotlin-implementation)
- [Сложность](#сложность)

## Описание структуры данных

В этом руководстве мы познакомимся с деревом AVL и рассмотрим алгоритмы вставки, удаления и поиска значений. Дерево AVL, названное в честь его изобретателей Адельсона-Вельского и Лэндиса, представляет собой самобалансирующееся двоичное дерево поиска (BST).

Самобалансирующееся дерево - это бинарное дерево поиска, которое уравновешивает высоту после вставки и удаления в соответствии с некоторыми правилами балансировки.

Временная сложность BST в наихудшем случае зависит от высоты дерева. В частности, самый длинный путь от корня дерева к узлу. Предположим, что для BST с N узлами у каждого узла есть только ноль или один дочерний узел. Следовательно, его высота равна N, а время поиска в худшем случае равно O(N). Таким образом, наша главная цель в BST - сохранить максимальную высоту близкой к log(N).

## Коэффициент баланса

Коэффициент баланса узла N равен `height(right(N)) - height(left(N))`. В дереве AVL коэффициент баланса узла может принимать только одно из значений 1, 0 или -1.

## Java Implementation

### Определение узла

Давайте определим объект Node для нашего дерева:

```java
public class Node {
    int key;
    int height;
    Node left;
    Node right;
    
    Node(int key) {
        this.key = key;
        this.height = 0;
        this.left = null;
        this.right = null;
    }
}
```

### Определение AVLTree

Далее давайте определим AVLTree:

```java
public class AVLTree {
    private Node root;
    
    void updateHeight(Node n) {
        n.height = 1 + Math.max(height(n.left), height(n.right));
    }
    
    int height(Node n) {
        return n == null ? -1 : n.height;
    }
    
    int getBalance(Node n) {
        return (n == null) ? 0 : height(n.right) - height(n.left);
    }
}
```

Дерево AVL проверяет коэффициент баланса своих узлов после вставки или удаления узла. Если коэффициент баланса узла больше единицы или меньше -1, дерево перебалансируется.

## Операции вращения

Есть две операции по перебалансировке дерева:

1. правое вращение
2. левое вращение

### Правое вращение

Начнем с правого вращения.

Предположим, у нас есть BST с именем T1, с Y в качестве корневого узла, X в качестве левого потомка Y и Z в качестве правого потомка X. Учитывая характеристики BST, мы знаем, что X < Z < Y.

После правого поворота Y у нас есть дерево с именем T2 с X в качестве корня и Y в качестве правого дочернего элемента X и Z в качестве левого дочернего элемента Y. T2 все еще является BST, потому что он сохраняет порядок X < Z < Y.

Давайте посмотрим на правильную операцию вращения для нашего AVLTree:

```java
Node rotateRight(Node y) {
    Node x = y.left;
    Node z = x.right;
    
    x.right = y;
    y.left = z;
    
    updateHeight(y);
    updateHeight(x);
    
    return x;
}
```

### Левое вращение

У нас также есть операция левого вращения.

Предположим, что BST называется T1, с Y в качестве корневого узла, X в качестве правого потомка Y и Z в качестве левого потомка X. Учитывая это, мы знаем, что Y < Z < X.

После поворота Y влево у нас есть дерево с именем T2, где X является корнем, Y - левым дочерним элементом X, а Z - правым дочерним элементом Y. T2 по-прежнему является BST, поскольку сохраняет порядок Y < Z < X.

Давайте посмотрим на операцию поворота влево для нашего AVLTree:

```java
Node rotateLeft(Node y) {
    Node x = y.right;
    Node z = x.left;
    
    x.left = y;
    y.right = z;
    
    updateHeight(y);
    updateHeight(x);
    
    return x;
}
```

### Комбинированные вращения

Мы можем использовать операции вращения вправо и влево в более сложных комбинациях, чтобы сохранить баланс дерева AVL после любых изменений в его узлах. В несбалансированной структуре хотя бы один узел имеет коэффициент баланса, равный 2 или -2.

#### Случай 1: Баланс = 2 (правое поддерево выше)

Когда коэффициент баланса узла Z равен 2, поддерево с Z в качестве корня находится в одном из этих двух состояний, считая Y правым дочерним элементом Z.

В первом случае высота правого потомка Y(X) больше высоты левого потомка (T2). Мы можем легко сбалансировать дерево, повернув Z влево.

Во втором случае высота правого потомка Y(T4) меньше высоты левого потомка (X). В этой ситуации требуется комбинация операций вращения.

В этом случае мы сначала поворачиваем Y вправо, чтобы дерево приняло ту же форму, что и в предыдущем случае. Затем мы можем сбалансировать дерево, повернув Z влево.

#### Случай 2: Баланс = -2 (левое поддерево выше)

Кроме того, когда коэффициент баланса узла Z равен -2, его поддерево находится в одном из этих двух состояний, поэтому мы рассматриваем Z как корень, а Y как его левый дочерний элемент.

Высота левого дочернего элемента Y больше высоты его правого дочернего элемента, поэтому мы уравновешиваем дерево правым вращением Z.

Или, во втором случае, правый потомок Y имеет большую высоту, чем его левый потомок.

Итак, прежде всего, мы преобразуем его в прежнюю форму с левым вращением Y, затем балансируем дерево с правым вращением Z.

### Операция перебалансировки

Давайте посмотрим на операцию перебалансировки для нашего AVLTree:

```java
Node rebalance(Node z) {
    updateHeight(z);
    int balance = getBalance(z);
    
    if (balance > 1) {
        if (height(z.right.right) > height(z.right.left)) {
            z = rotateLeft(z);
        } else {
            z.right = rotateRight(z.right);
            z = rotateLeft(z);
        }
    } else if (balance < -1) {
        if (height(z.left.left) > height(z.left.right)) {
            z = rotateRight(z);
        } else {
            z.left = rotateLeft(z.left);
            z = rotateRight(z);
        }
    }
    
    return z;
}
```

Мы будем использовать перебалансировку после вставки или удаления узла для всех узлов на пути от измененного узла к корню.

## Вставка узла

Когда мы собираемся вставить ключ в дерево, мы должны найти его правильное положение, чтобы пройти правила BST. Итак, мы начинаем с корня и сравниваем его значение с новым ключом. Если ключ больше, продолжаем вправо - иначе идем к левому потомку.

Как только мы находим правильный родительский узел, мы добавляем новый ключ в качестве узла слева или справа, в зависимости от значения.

После вставки узла у нас есть BST, но это может быть не дерево AVL. Поэтому мы проверяем факторы баланса и перебалансируем BST для всех узлов на пути от нового узла к корню.

Рассмотрим операцию вставки:

```java
Node insert(Node node, int key) {
    if (node == null) {
        return new Node(key);
    } else if (node.key > key) {
        node.left = insert(node.left, key);
    } else if (node.key < key) {
        node.right = insert(node.right, key);
    } else {
        throw new RuntimeException("duplicate Key!");
    }
    
    return rebalance(node);
}

public void insert(int key) {
    root = insert(root, key);
}
```

Важно помнить, что ключ уникален в дереве - никакие два узла не имеют одного и того же ключа.

Временная сложность алгоритма вставки зависит от высоты. Поскольку наше дерево сбалансировано, мы можем предположить, что временная сложность в худшем случае равна O(log(N)).

## Удаление узла

Чтобы удалить ключ из дерева, мы сначала должны найти его в BST.

После того, как мы найдем узел (называемый Z), мы должны ввести нового кандидата, который будет его заменой в дереве. Если Z - лист, кандидат пуст. Если у Z есть только один ребенок, этот ребенок является кандидатом, но если у Z есть два ребенка, процесс немного сложнее.

Предположим, правый потомок Z называется Y. Сначала мы находим крайний левый узел Y и называем его X. Затем мы устанавливаем новое значение Z равным значению X и продолжаем удалять X из Y.

Наконец, мы вызываем метод перебалансировки в конце, чтобы сохранить BST в виде дерева AVL.

Вот наш метод удаления:

```java
Node mostLeftChild(Node node) {
    Node current = node;
    while (current.left != null) {
        current = current.left;
    }
    return current;
}

Node delete(Node node, int key) {
    if (node == null) {
        return node;
    } else if (node.key > key) {
        node.left = delete(node.left, key);
    } else if (node.key < key) {
        node.right = delete(node.right, key);
    } else {
        if (node.left == null || node.right == null) {
            node = (node.left == null) ? node.right : node.left;
        } else {
            Node mostLeftChild = mostLeftChild(node.right);
            node.key = mostLeftChild.key;
            node.right = delete(node.right, node.key);
        }
    }
    
    if (node != null) {
        node = rebalance(node);
    }
    
    return node;
}

public void delete(int key) {
    root = delete(root, key);
}
```

Временная сложность алгоритма удаления зависит от высоты дерева. Подобно методу вставки, мы можем предположить, что временная сложность в худшем случае равна O(log(N)).

## Поиск узла

Поиск узла в дереве AVL такой же, как и в любом BST.

Начните с корня дерева и сравните ключ со значением узла. Если ключ равен значению, вернуть узел. Если ключ больше, ищите от правого дочернего элемента, в противном случае продолжайте поиск от левого дочернего элемента.

Временная сложность поиска зависит от высоты. Можно предположить, что временная сложность в худшем случае равна O(log(N)).

Давайте посмотрим пример кода:

```java
Node find(int key) {
    Node current = root;
    
    while (current != null) {
        if (current.key == key) {
            break;
        }
        current = current.key < key ? current.right : current.left;
    }
    
    return current;
}

public boolean contains(int key) {
    return find(key) != null;
}
```

## Kotlin Implementation

### Класс Node

```kotlin
class NodeK(var key: Int) {
    var height: Int = 0
    var left: NodeK? = null
    var right: NodeK? = null
}
```

### Класс AVLTree

```kotlin
class AVLTreeK {
    private var root: NodeK? = null
    
    private fun updateHeight(n: NodeK?) {
        if (n != null) {
            n.height = 1 + Math.max(height(n.left), height(n.right))
        }
    }
    
    private fun height(n: NodeK?): Int = n?.height ?: -1
    
    private fun getBalance(n: NodeK?): Int {
        return if (n == null) 0 else height(n.right) - height(n.left)
    }
    
    private fun rotateRight(y: NodeK): NodeK {
        val x = y.left!!
        val z = x.right
        
        x.right = y
        y.left = z
        
        updateHeight(y)
        updateHeight(x)
        
        return x
    }
    
    private fun rotateLeft(y: NodeK): NodeK {
        val x = y.right!!
        val z = x.left
        
        x.left = y
        y.right = z
        
        updateHeight(y)
        updateHeight(x)
        
        return x
    }
    
    private fun rebalance(z: NodeK): NodeK {
        updateHeight(z)
        val balance = getBalance(z)
        
        if (balance > 1) {
            if (height(z.right?.right) > height(z.right?.left)) {
                return rotateLeft(z)
            } else {
                z.right = rotateRight(z.right!!)
                return rotateLeft(z)
            }
        } else if (balance < -1) {
            if (height(z.left?.left) > height(z.left?.right)) {
                return rotateRight(z)
            } else {
                z.left = rotateLeft(z.left!!)
                return rotateRight(z)
            }
        }
        
        return z
    }
    
    fun insert(key: Int) {
        root = insertRecursive(root, key)
    }
    
    private fun insertRecursive(node: NodeK?, key: Int): NodeK {
        if (node == null) {
            return NodeK(key)
        }
        
        when {
            key < node.key -> node.left = insertRecursive(node.left, key)
            key > node.key -> node.right = insertRecursive(node.right, key)
            else -> return node
        }
        
        return rebalance(node)
    }
    
    fun find(key: Int): NodeK? {
        var current = root
        while (current != null) {
            when {
                current.key == key -> break
                current.key < key -> current = current.right
                else -> current = current.left
            }
        }
        return current
    }
    
    fun contains(key: Int): Boolean = find(key) != null
}
```

### Пример использования

```kotlin
fun main() {
    val tree = AVLTreeK()
    tree.insert(10)
    tree.insert(20)
    tree.insert(30)
    tree.insert(40)
    tree.insert(50)
    
    println(tree.contains(30)) // true
    println(tree.contains(60)) // false
}
```

## Сложность

### Временная сложность

| Операция | Средний случай | Худший случай |
|----------|----------------|---------------|
| Поиск | O(log(n)) | O(log(n)) |
| Вставка | O(log(n)) | O(log(n)) |
| Удаление | O(log(n)) | O(log(n)) |

### Пространственная сложность

- **Все операции:** O(n) - для хранения всех узлов
- **Рекурсивные операции:** O(log(n)) - для стека вызовов

## Особенности

- **Самобалансировка:** Автоматически поддерживает баланс после вставки/удаления
- **Гарантированная высота:** Высота всегда O(log(n))
- **Эффективность:** Все операции выполняются за O(log(n))

## Применение

AVL деревья используются в:

- Базах данных (индексы)
- Компиляторах (символьные таблицы)
- Файловых системах
- Кэшировании
- Структурах данных стандартных библиотек

## Сравнение с другими структурами

| Структура | Балансировка | Временная сложность поиска |
|-----------|--------------|----------------------------|
| Обычное BST | Нет | O(n) в худшем случае |
| AVL Tree | Автоматическая | O(log(n)) |
| Red-Black Tree | Автоматическая | O(log(n)) |
| B-Tree | Автоматическая | O(log(n)) |

## Когда использовать

### Используйте AVL дерево, когда:

- Нужна гарантированная производительность
- Часто выполняются операции поиска
- Важна стабильность времени выполнения
- Нужна самобалансировка

### Альтернативы:

- **Red-Black Tree:** Меньше вращений, но менее строгий баланс
- **B-Tree:** Для внешней памяти
- **Обычное BST:** Когда баланс не критичен

## Заключение

В этом руководстве мы познакомились с деревом AVL и рассмотрели алгоритмы вставки, удаления и поиска значений. AVL дерево - это самобалансирующееся двоичное дерево поиска, которое гарантирует высоту O(log(n)) через операции вращения, обеспечивая эффективные операции поиска, вставки и удаления.
