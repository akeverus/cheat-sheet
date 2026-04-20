---
title: "Поиск среднего элемента связного списка (Linked List)"
description: "Поиск среднего элемента односвязного списка в Java: по известному размеру (get((size-1)/2)), два указателя (один проход, O(1) памяти), рекурсия. Сравнение подходов, реализация в Kotlin."
tags:
  - algorithms
  - data-structures
  - linked-list-middle
difficulty: "intermediate"
prerequisites: []
next: []
updated: "2026-02-11"
---
# Поиск среднего элемента связного списка (`Linked List`)

Поиск среднего элемента односвязного списка в Java: по известному размеру (get((size-1)/2)), два указателя (один проход, O(1) памяти), рекурсия. Сравнение подходов, реализация в Kotlin.

## Полезные ссылки

### Официальная документация
- [`LinkedList` (Java SE 8)](https://docs.oracle.com/javase/8/docs/api/java/util/LinkedList.html)
- [`Optional` (Java SE 8)](https://docs.oracle.com/javase/8/docs/api/java/util/Optional.html)

### См. также
- [[linked-list-reverse|Реверс Linked List]]
- [Задачи и алгоритмы](../problems/README.md) — обзор разделов

## Содержание

- [Обзор](#обзор)
- [Реализация на Java](#реализация-на-java)
- [Полная реализация](#полная-реализация)
- [Сравнение подходов](#сравнение-подходов)
- [Рекомендации](#рекомендации)
- [Лучшие практики](#лучшие-практики)
- [Реализация на Kotlin](#реализация-на-kotlin)
- [Решение проблем](#решение-проблем)
- [Частые вопросы](#частые-вопросы)
- [Резюме](#резюме)


## Обзор

Если известен размер списка, средний элемент — `get((size-1)/2)` (для `java.util.LinkedList` учтите, что get(i) у LinkedList — O(n)). Если есть только голова односвязного списка — нужен один из подходов: два прохода (подсчёт размера, затем переход к середине), два указателя (один проход) или рекурсия.

## Реализация на Java

### Подход 1: Использование размера списка (Java)

```java
// Поиск среднего элемента: при известном размере — get((size-1)/2); при одном проходе — два указателя (быстрый/медленный).
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

Внутренний метод `node(index)` в `LinkedList` обходит от `first` или `last` до нужного индекса (до середины — от начала).

```java
// Фрагмент java.util.LinkedList: обход до index от начала или конца
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

### Подход 2: Два указателя (один проход)

При наличии только головы без размера: класс узла (data, next) и два указателя — быстрый шагает по два узла, медленный по одному; когда быстрый достигнет конца, медленный в середине.

```java
// Узел односвязного списка: data и next
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

```java
// Создание списка из n узлов "1", "2", ... для тестов
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

Два прохода: сначала подсчёт размера, затем итерация до (size-1)/2. Ниже — один проход с двумя указателями.

```java
// Два прохода: подсчёт size, затем переход к индексу (size-1)/2
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

```java
// Один проход: fast — два узла за шаг, slow — один; при достижении fast конца slow в середине
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

```java
@Test
void whenFindingMiddleFromHead1PassIteratively_thenMiddleFound() {
    assertEquals("3", MiddleElementLookup
        .findMiddleElementFromHead1PassIteratively(createNodesList(5)).get());
    
    assertEquals("2", MiddleElementLookup
        .findMiddleElementFromHead1PassIteratively(createNodesList(4)).get());
}
```

### Подход 3: Рекурсивный метод (Java)

Рекурсия до конца списка считает длину; при возврате из рекурсии счётчик уменьшается; когда он становится 0 — текущий узел и есть середина. Вспомогательный класс хранит длину и ссылку на средний узел.

```java
// Вспомогательный объект: длина и найденный средний узел
private static class MiddleAuxRecursion {
    Node middle;
    int length = 0;
}
```

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
| С размером списка | `O(n)` | `O(1)` | 2 |
| Два указателя (итеративный) | `O(n)` | `O(1)` | 1 |
| Рекурсивный | `O(n)` | `O(n)` | 1 |

Подход с размером: прост, но два прохода и нужен доступ к размеру. Два указателя: один проход, O(1) памяти, предпочтителен. Рекурсия: один проход, O(n) по стеку, риск StackOverflowError на больших списках.

## Рекомендации

Для одного прохода и константной памяти без риска переполнения стека используйте итеративный подход с двумя указателями.

## Лучшие практики

При известном размере — `get((size-1)/2)` для `java.util.LinkedList` (учтите: get(i) у LinkedList — O(n)). При одном проходе — два указателя (быстрый по два узла, медленный по одному): O(n) время, O(1) память. Рекурсия даёт O(n) по стеку; для больших списков предпочтите итеративный вариант. В начале проверяйте null и пустой список; возвращайте `Optional` или null по конвенции проекта.

## Реализация на Kotlin

```kotlin
// Узел: data и next
class NodeK(val data: String) {
    var next: NodeK? = null
    
    fun hasNext(): Boolean = next != null
}
```

### Подход 1: Использование размера списка (Kotlin)

```kotlin
import java.util.Optional

fun findMiddleElementLinkedListK(linkedList: List<String>): Optional<String> {
    if (linkedList.isEmpty()) {
        return Optional.empty()
    }
    return Optional.of(linkedList[(linkedList.size - 1) / 2])
}
```

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

### Подход 3: Рекурсивный метод (Kotlin)

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

## Решение проблем

| Симптом | Возможная причина | Решение |
|---------|-------------------|---------|
| NullPointerException при обходе | head == null или next у последнего | Проверять head в начале; у быстрого указателя проверять hasNext() и next().hasNext() |
| StackOverflowError при рекурсии | Очень длинный список | Использовать итеративный подход с двумя указателями |
| Неверный «средний» для чётного размера | Разные определения середины | (size-1)/2 даёт «левый» средний (например для 4 элементов — индекс 1); при необходимости договориться о конвенции |

## Частые вопросы

**Почему два указателя дают середину?** Быстрый проходит в два раза больше узлов; когда он в конце, медленный прошёл половину пути — то есть находится в середине.

**Какой средний при чётном числе элементов?** Обычно берут элемент с индексом (size-1)/2 (для 4 элементов — индекс 1, т.е. второй элемент). Для «правого» среднего можно использовать size/2.

**Когда использовать рекурсию?** Только для коротких списков или когда важна краткость кода; для продакшена предпочтительнее итеративный вариант с двумя указателями.

## Резюме

Поиск среднего элемента: при известном размере — get((size-1)/2); при одной голове — два указателя (один проход, O(1) памяти) или рекурсия (один проход, O(n) памяти). Для больших списков предпочтите итеративный подход с двумя указателями.
