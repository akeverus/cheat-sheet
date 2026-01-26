# Поиск среднего элемента Linked List

Руководство по поиску среднего элемента связанного списка в Java различными способами: с использованием размера списка, двух указателей и рекурсии.

**Последнее обновление:** 2025-01-15

## Полезные ссылки

### Официальная документация
- [LinkedList JavaDoc](https://docs.oracle.com/javase/8/docs/api/java/util/LinkedList.html)
- [Optional JavaDoc](https://docs.oracle.com/javase/8/docs/api/java/util/Optional.html)

### См. также
- [Реверс Linked List](./linked-list-reverse.md)
- [Структуры данных](../problems/)

## Содержание

- [Обзор](#обзор)
- [Java Implementation](#java-implementation)
- [Kotlin Implementation](#kotlin-implementation)
- [Сравнение подходов](#сравнение-подходов)

## Обзор

В этом руководстве мы объясним, как найти средний элемент связанного списка в Java.

В следующих разделах мы представим основные проблемы и покажем различные подходы к их решению.

Эту проблему можно легко решить, просто отслеживая размер при добавлении новых элементов в список. Если мы знаем размер, мы также знаем, где находится средний элемент, поэтому решение тривиально.

## Java Implementation

### Подход 1: Использование размера списка

Давайте посмотрим на пример, использующий реализацию LinkedList на Java:

```java
import java.util.LinkedList;
import java.util.Optional;

public class MiddleElementFinder {
    
    public static Optional<String> findMiddleElementLinkedList(
            LinkedList<String> linkedList) {
        if (linkedList == null || linkedList.isEmpty()) {
            return Optional.empty();
        }
        return Optional.of(linkedList.get((linkedList.size() - 1) / 2));
    }
}
```

Если мы проверим внутренний код класса LinkedList, то увидим, что в этом примере мы просто просматриваем список, пока не достигнем среднего элемента:

```java
Node<E> node(int index) {
    if (index < (size >> 1)) {
        Node<E> x = first;
        for (int i = 0; i < index; i++) {
            x = x.next;
        }
        return x;
    } else {
        Node<E> x = last;
        for (int i = size - 1; i > index; i--) {
            x = x.prev;
        }
        return x;
    }
}
```

Очень часто мы сталкиваемся с проблемами, когда у нас есть только головной узел связанного списка, и нам нужно найти средний элемент. В этом случае мы не знаем размер списка, что усложняет решение этой проблемы.

### Подход 2: Два указателя (один проход)

В следующих разделах мы покажем несколько подходов к решению этой проблемы, но сначала нам нужно создать класс для представления узла списка.

Давайте создадим класс Node, в котором хранятся значения String:

```java
public static class Node {
    private Node next;
    private String data;
    
    public Node(String data) {
        this.data = data;
        this.next = null;
    }
    
    public boolean hasNext() {
        return next != null;
    }
    
    public Node next() {
        return next;
    }
    
    public void setNext(Node next) {
        this.next = next;
    }
    
    public String data() {
        return data;
    }
    
    @Override
    public String toString() {
        return this.data;
    }
}
```

Кроме того, мы будем использовать этот вспомогательный метод в наших тестовых примерах для создания односвязного списка, используя только наши узлы:

```java
private static Node createNodesList(int n) {
    Node head = new Node("1");
    Node current = head;
    
    for (int i = 2; i <= n; i++) {
        Node newNode = new Node(String.valueOf(i));
        current.setNext(newNode);
        current = newNode;
    }
    
    return head;
}
```

Самый простой подход к решению этой проблемы - сначала найти размер списка, а затем следовать тому же подходу, который мы использовали раньше - итерации до среднего элемента.

Давайте посмотрим на это решение в действии:

```java
public static Optional<String> findMiddleElementFromHead(Node head) {
    if (head == null) {
        return Optional.empty();
    }
    
    Node current = head;
    int size = 1;
    
    while (current.hasNext()) {
        current = current.next();
        size++;
    }
    
    current = head;
    for (int i = 0; i < (size - 1) / 2; i++) {
        current = current.next();
    }
    
    return Optional.of(current.data());
}
```

Как мы видим, этот код перебирает список дважды. Поэтому это решение имеет низкую производительность и не рекомендуется.

Теперь мы собираемся улучшить предыдущее решение, найдя средний элемент всего за одну итерацию по списку.

Чтобы сделать это итеративно, нам нужны два указателя для одновременного прохода по списку. Один указатель будет продвигать 2 узла за каждую итерацию, а другой указатель будет продвигать только один узел за итерацию.

Когда более быстрый указатель достигнет конца списка, более медленный указатель окажется в середине:

```java
public static Optional<String> findMiddleElementFromHead1PassIteratively(Node head) {
    if (head == null) {
        return Optional.empty();
    }
    
    Node slowPointer = head;
    Node fastPointer = head;
    
    while (fastPointer.hasNext() && fastPointer.next().hasNext()) {
        fastPointer = fastPointer.next().next();
        slowPointer = slowPointer.next();
    }
    
    return Optional.ofNullable(slowPointer.data());
}
```

Мы можем протестировать это решение с помощью простого модульного теста, используя списки как с нечетным, так и с четным количеством элементов:

```java
@Test
void whenFindingMiddleFromHead1PassIteratively_thenMiddleFound() {
    assertEquals("3", MiddleElementLookup
        .findMiddleElementFromHead1PassIteratively(createNodesList(5)).get());
    
    assertEquals("2", MiddleElementLookup
        .findMiddleElementFromHead1PassIteratively(createNodesList(4)).get());
}
```

### Подход 3: Рекурсивный метод

Другой способ решить эту проблему за один проход - использовать рекурсию. Мы можем выполнить итерацию до конца списка, чтобы узнать размер, и в обратных вызовах мы просто считаем до половины размера.

Для этого в Java мы создадим вспомогательный класс, чтобы сохранять ссылки на размер списка и средний элемент во время выполнения всех рекурсивных вызовов:

```java
private static class MiddleAuxRecursion {
    Node middle;
    int length = 0;
}
```

Теперь давайте реализуем рекурсивный метод:

```java
private static void findMiddleRecursively(Node node, MiddleAuxRecursion middleAux) {
    if (node == null) {
        middleAux.length = middleAux.length / 2;
        return;
    }
    
    middleAux.length++;
    findMiddleRecursively(node.next(), middleAux);
    
    if (middleAux.length == 0) {
        middleAux.middle = node;
    }
    
    middleAux.length--;
}
```

И, наконец, давайте создадим метод, который вызывает рекурсивный:

```java
public static Optional<String> findMiddleElementFromHead1PassRecursively(Node head) {
    if (head == null) {
        return Optional.empty();
    }
    
    MiddleAuxRecursion middleAux = new MiddleAuxRecursion();
    findMiddleRecursively(head, middleAux);
    
    return Optional.of(middleAux.middle.data());
}
```

Опять же, мы можем проверить это так же, как и раньше:

```java
@Test
void whenFindingMiddleFromHead1PassRecursively_thenMiddleFound() {
    assertEquals("3", MiddleElementLookup
        .findMiddleElementFromHead1PassRecursively(createNodesList(5)).get());
    
    assertEquals("2", MiddleElementLookup
        .findMiddleElementFromHead1PassRecursively(createNodesList(4)).get());
}
```

## Полная реализация

```java
import java.util.Optional;

public class MiddleElementLookup {
    
    public static class Node {
        private Node next;
        private String data;
        
        public Node(String data) {
            this.data = data;
            this.next = null;
        }
        
        public boolean hasNext() {
            return next != null;
        }
        
        public Node next() {
            return next;
        }
        
        public void setNext(Node next) {
            this.next = next;
        }
        
        public String data() {
            return data;
        }
    }
    
    // Метод с двумя проходами
    public static Optional<String> findMiddleElementFromHead(Node head) {
        if (head == null) {
            return Optional.empty();
        }
        
        Node current = head;
        int size = 1;
        
        while (current.hasNext()) {
            current = current.next();
            size++;
        }
        
        current = head;
        for (int i = 0; i < (size - 1) / 2; i++) {
            current = current.next();
        }
        
        return Optional.of(current.data());
    }
    
    // Метод с одним проходом (итеративный)
    public static Optional<String> findMiddleElementFromHead1PassIteratively(Node head) {
        if (head == null) {
            return Optional.empty();
        }
        
        Node slowPointer = head;
        Node fastPointer = head;
        
        while (fastPointer.hasNext() && fastPointer.next().hasNext()) {
            fastPointer = fastPointer.next().next();
            slowPointer = slowPointer.next();
        }
        
        return Optional.ofNullable(slowPointer.data());
    }
    
    // Метод с одним проходом (рекурсивный)
    private static class MiddleAuxRecursion {
        Node middle;
        int length = 0;
    }
    
    public static Optional<String> findMiddleElementFromHead1PassRecursively(Node head) {
        if (head == null) {
            return Optional.empty();
        }
        
        MiddleAuxRecursion middleAux = new MiddleAuxRecursion();
        findMiddleRecursively(head, middleAux);
        
        return Optional.of(middleAux.middle.data());
    }
    
    private static void findMiddleRecursively(Node node, MiddleAuxRecursion middleAux) {
        if (node == null) {
            middleAux.length = middleAux.length / 2;
            return;
        }
        
        middleAux.length++;
        findMiddleRecursively(node.next(), middleAux);
        
        if (middleAux.length == 0) {
            middleAux.middle = node;
        }
        
        middleAux.length--;
    }
    
    private static Node createNodesList(int n) {
        Node head = new Node("1");
        Node current = head;
        
        for (int i = 2; i <= n; i++) {
            Node newNode = new Node(String.valueOf(i));
            current.setNext(newNode);
            current = newNode;
        }
        
        return head;
    }
}
```

## Сравнение подходов

| Подход | Временная сложность | Пространственная сложность | Проходов по списку |
|--------|---------------------|----------------------------|-------------------|
| С размером списка | O(n) | O(1) | 2 |
| Два указателя (итеративный) | O(n) | O(1) | 1 |
| Рекурсивный | O(n) | O(n) | 1 |

### Преимущества и недостатки

**Подход с размером списка:**
- ✅ Простой для понимания
- ❌ Требует два прохода по списку
- ❌ Нужен доступ к размеру списка

**Подход с двумя указателями:**
- ✅ Один проход по списку
- ✅ Константная память
- ✅ Эффективный алгоритм
- ⚠️ Немного сложнее для понимания

**Рекурсивный подход:**
- ✅ Один проход по списку
- ✅ Элегантное решение
- ❌ Использует стек вызовов (O(n) памяти)
- ❌ Может вызвать StackOverflowError для больших списков

## Рекомендации

Для большинства случаев рекомендуется использовать подход с двумя указателями (итеративный), так как он:
- Эффективен по времени (один проход)
- Эффективен по памяти (константная память)
- Не имеет риска переполнения стека

## Резюме

В этой статье мы представили проблему поиска среднего элемента связанного списка в Java и показали различные способы ее решения.

Ключевые моменты:
- Простейший подход использует размер списка, но требует два прохода
- Подход с двумя указателями эффективен и требует только один проход
- Рекурсивный подход элегантен, но использует дополнительную память
- Для больших списков предпочтительнее итеративный подход с двумя указателями

## Kotlin Implementation

### Класс Node

```kotlin
class NodeK(val data: String) {
    var next: NodeK? = null
    
    fun hasNext(): Boolean = next != null
}
```

### Подход 1: Использование размера списка

```kotlin
import java.util.Optional

fun findMiddleElementLinkedListK(linkedList: List<String>): Optional<String> {
    if (linkedList.isEmpty()) {
        return Optional.empty()
    }
    return Optional.of(linkedList[(linkedList.size - 1) / 2])
}
```

### Подход 2: Два указателя (один проход)

```kotlin
fun findMiddleElementFromHead1PassIterativelyK(head: NodeK?): String? {
    if (head == null) {
        return null
    }
    
    var slowPointer: NodeK? = head
    var fastPointer: NodeK? = head
    
    while (fastPointer?.hasNext() == true && fastPointer.next?.hasNext() == true) {
        fastPointer = fastPointer.next?.next
        slowPointer = slowPointer?.next
    }
    
    return slowPointer?.data
}
```

### Подход 3: Рекурсивный метод

```kotlin
data class MiddleAuxRecursionK(
    var middle: NodeK? = null,
    var length: Int = 0
)

fun findMiddleElementFromHead1PassRecursivelyK(head: NodeK?): String? {
    if (head == null) {
        return null
    }
    
    val middleAux = MiddleAuxRecursionK()
    findMiddleRecursivelyK(head, middleAux)
    
    return middleAux.middle?.data
}

private fun findMiddleRecursivelyK(node: NodeK?, middleAux: MiddleAuxRecursionK) {
    if (node == null) {
        middleAux.length = middleAux.length / 2
        return
    }
    
    middleAux.length++
    findMiddleRecursivelyK(node.next, middleAux)
    
    if (middleAux.length == 0) {
        middleAux.middle = node
    }
    
    middleAux.length--
}
```

### Вспомогательная функция для создания списка

```kotlin
fun createNodesListK(n: Int): NodeK? {
    if (n < 1) return null
    
    val head = NodeK("1")
    var current = head
    
    for (i in 2..n) {
        val newNode = NodeK(i.toString())
        current.next = newNode
        current = newNode
    }
    
    return head
}
```
