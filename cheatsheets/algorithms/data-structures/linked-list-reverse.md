---
title: "Реверс связанного списка (Linked List)"
description: "Руководство по реализации алгоритмов обращения связанных списков в Java: итеративный и рекурсивный подходы."
tags:
  - algorithms
  - data-structures
  - linked-list-reverse
difficulty: "intermediate"
prerequisites: []
next: []
updated: "2026-04-20"
---
# Реверс связанного списка (Linked List)

Руководство по реализации алгоритмов обращения связанных списков в `Java`: итеративный и рекурсивный подходы.

## Полезные ссылки

### Официальная документация
- [LinkedList (Java SE 8)](https://docs.oracle.com/javase/8/docs/api/java/util/LinkedList.html)
- [Java Collections (Java SE 8)](https://docs.oracle.com/javase/8/docs/api/java/util/package-summary.html)

### См. также
- [`linked-list-middle.md`](linked-list-middle.md) — поиск среднего элемента связанного списка (`Linked List`)
- [README](../../basics/README.md) — задачи и алгоритмы, обзор разделов

- [Временная сложность коллекций Java](collections-complexity.md)
- [Практические примеры нотации Big O](collections-big-o.md)
- [Введение в структуры данных без блокировок](collections-lock-free.md)
## Содержание

- [Обзор](#обзор)
- [Структура связанного списка](#структура-связанного-списка)
- [Реализация на Java](#реализация-на-java)
  - [Итеративный алгоритм (Java)](#итеративный-алгоритм-java)
  - [Тестирование итеративного алгоритма](#тестирование-итеративного-алгоритма)
  - [Рекурсивный алгоритм (Java)](#рекурсивный-алгоритм-java)
  - [Тестирование рекурсивного алгоритма](#тестирование-рекурсивного-алгоритма)
- [Сравнение подходов](#сравнение-подходов)
  - [Преимущества итеративного подхода](#преимущества-итеративного-подхода)
  - [Преимущества рекурсивного подхода](#преимущества-рекурсивного-подхода)
- [Полная реализация](#полная-реализация)
- [Визуализация процесса](#визуализация-процесса)
  - [Итеративный алгоритм](#итеративный-алгоритм)
  - [Рекурсивный алгоритм](#рекурсивный-алгоритм)
- [Варианты задачи](#варианты-задачи)
  - [Обращение части списка](#обращение-части-списка)
  - [Обращение каждых k элементов](#обращение-каждых-k-элементов)
- [Лучшие практики](#лучшие-практики)
- [Резюме](#резюме)
- [Реализация на Kotlin](#реализация-на-kotlin)
  - [Класс ListNode](#класс-listnode)
  - [Итеративный алгоритм (Kotlin)](#итеративный-алгоритм-kotlin)
  - [Рекурсивный алгоритм (Kotlin)](#рекурсивный-алгоритм-kotlin)
  - [Вспомогательные функции](#вспомогательные-функции)
  - [Пример использования](#пример-использования)

## Обзор

В этом руководстве мы реализуем два алгоритма обращения связанных списков на **Java**.

Связный список — это линейная структура данных, в которой указатель в каждом элементе определяет порядок. Каждый элемент связанного списка содержит поле данных для хранения данных списка и поле указателя для указания на следующий элемент в последовательности. Кроме того, мы можем использовать головной указатель, чтобы указать на начальный элемент связанного списка.

После того, как мы реверсируем связанный список, заголовок будет указывать на последний элемент исходного связанного списка, а указатель каждого элемента будет указывать на предыдущий элемент исходного связанного списка.

В **Java** у нас есть класс **LinkedList** для реализации двусвязного списка, реализующего интерфейсы **List** и **Deque**. Однако в этом руководстве мы будем использовать общую структуру данных односвязного списка.

## Структура связанного списка

Давайте сначала начнём с класса `ListNode` для представления элемента связанного списка:

```java
// Узел односвязного списка: данные и ссылка на следующий узел; реверс — итеративно или рекурсивно.
public class ListNode {
    private int data;
    private ListNode next;

    public ListNode(int data) {
        this.data = data;
        this.next = null;
    }

    public int getData() {
        return data;
    }

    public void setData(int data) {
        this.data = data;
    }

    public ListNode getNext() {
        return next;
    }

    public void setNext(ListNode next) {
        this.next = next;
    }
}
```

**Класс **ListNode** имеет два поля:**

1. Целочисленное значение для представления данных элемента
2. Указатель/ссылка на следующий элемент

**Связанный список может содержать несколько объектов **ListNode**. Например, мы можем создать приведенный выше пример связанного списка с циклом:**

```java
public ListNode constructLinkedList() {
    ListNode head = null;
    ListNode tail = null;

    for (int i = 1; i <= 5; i++) {
        ListNode node = new ListNode(i);
        if (head == null) {
            head = node;
        } else {
            tail.setNext(node);
        }
        tail = node;
    }

    return head;
}
```

## Реализация на Java

### Итеративный алгоритм (Java)

Давайте реализуем итерационный алгоритм на `Java`:

```java
public ListNode reverseList(ListNode head) {
    // Итеративный разворот списка: двигаем указатели previous/current и перенаправляем ссылки next
    ListNode previous = null;
    ListNode current = head;

    while (current != null) {
        ListNode nextElement = current.getNext();
        current.setNext(previous);
        previous = current;
        current = nextElement;
    }

    return previous;
}
```

В этом итеративном алгоритме мы используем две переменные `ListNode`, предыдущую и текущую, для представления двух соседних элементов в связанном списке. Для каждой итерации мы меняем эти два элемента местами, а затем переходим к следующим двум элементам. В конце концов, текущий указатель будет нулевым, а предыдущий указатель будет последним элементом старого связанного списка. Таким образом, `previous` также является новым указателем головы обратно связанного списка, и мы возвращаем его из метода.

### Тестирование итеративного алгоритма

Мы можем проверить эту итеративную реализацию с помощью простого модульного теста:

```java
@Test
void givenLinkedList_whenIterativeReverse_thenOutputCorrectResult() {
    ListNode head = constructLinkedList();
    ListNode node = head;

    // Проверяем исходный список
    for (int i = 1; i <= 5; i++) {
        assertNotNull(node);
        assertEquals(i, node.getData());
        node = node.getNext();
    }

    LinkedListReversal reversal = new LinkedListReversal();
    node = reversal.reverseList(head);

    // Проверяем перевернутый список
    for (int i = 5; i >= 1; i--) {
        assertNotNull(node);
        assertEquals(i, node.getData());
        node = node.getNext();
    }
}
```

В этом модульном тесте мы сначала создаём пример связанного списка с пятью узлами. Кроме того, мы проверяем, что каждый узел в связанном списке содержит правильное значение данных. Затем мы вызываем итеративную функцию для обращения связанного списка. Наконец, мы проверяем перевёрнутый связанный список, чтобы убедиться, что данные перевёрнуты, как ожидалось.

### Рекурсивный алгоритм (Java)

Теперь давайте реализуем рекурсивный алгоритм на `Java`:

```java
public ListNode reverseListRecursive(ListNode head) {
    // Рекурсивный разворот: доходим до хвоста и по пути назад перекидываем ссылки next от следующего к текущему узлу
    if (head == null) {
        return null;
    }

    if (head.getNext() == null) {
        return head;
    }

    ListNode node = reverseListRecursive(head.getNext());
    head.getNext().setNext(head);
    head.setNext(null);

    return node;
}
```

В функции `reverseListRecursive` мы рекурсивно посещаем каждый элемент в связанном списке, пока не достигнем последнего. Этот последний элемент станет новым заголовком обратно связанного списка. Кроме того, мы добавляем посещённый элемент в конец частично перевёрнутого связанного списка.

### Тестирование рекурсивного алгоритма

Точно так же мы можем проверить эту рекурсивную реализацию с помощью простого модульного теста:

```java
@Test
void givenLinkedList_whenRecursiveReverse_thenOutputCorrectResult() {
    ListNode head = constructLinkedList();
    ListNode node = head;

    // Проверяем исходный список
    for (int i = 1; i <= 5; i++) {
        assertNotNull(node);
        assertEquals(i, node.getData());
        node = node.getNext();
    }

    LinkedListReversal reversal = new LinkedListReversal();
    node = reversal.reverseListRecursive(head);

    // Проверяем перевернутый список
    for (int i = 5; i >= 1; i--) {
        assertNotNull(node);
        assertEquals(i, node.getData());
        node = node.getNext();
    }
}
```

## Сравнение подходов

| Критерий | Итеративный | Рекурсивный |
|----------|-------------|-------------|
| Временная сложность | `O(n)` | `O(n)` |
| Пространственная сложность | `O(1)` | `O(n)` |
| Риск переполнения стека | Нет | Да (для больших списков) |
| Читаемость | Хорошая | Отличная |
| Производительность | Высокая | Средняя |

### Преимущества итеративного подхода

Итеративный подход имеет несколько преимуществ. Он использует константную память `O(1)`, что делает его более эффективным по памяти, чем рекурсивный подход. Итеративный подход не имеет риска переполнения стека, что делает его безопасным для больших списков. На практике итеративный подход обычно быстрее, так как не требует создания новых кадров стека для каждого рекурсивного вызова. Итеративный подход также легче отлаживать, так как все переменные находятся в одной области видимости.

### Преимущества рекурсивного подхода

Рекурсивный подход имеет свои преимущества. Он более элегантен и читаем, так как код более компактен и ближе к математическому определению алгоритма. Рекурсивный подход прямо отражает логику алгоритма, что делает его более понятным для некоторых разработчиков. Рекурсивный подход требует меньше переменных для отслеживания, так как состояние передаётся через параметры функции и стек вызовов.

## Полная реализация

```java
public class LinkedListReversal {

    public ListNode reverseList(ListNode head) {
        ListNode previous = null;
        ListNode current = head;

        while (current != null) {
            ListNode nextElement = current.getNext();
            current.setNext(previous);
            previous = current;
            current = nextElement;
        }

        return previous;
    }

    public ListNode reverseListRecursive(ListNode head) {
        if (head == null) {
            return null;
        }

        if (head.getNext() == null) {
            return head;
        }

        ListNode node = reverseListRecursive(head.getNext());
        head.getNext().setNext(head);
        head.setNext(null);

        return node;
    }

    public ListNode constructLinkedList() {
        ListNode head = null;
        ListNode tail = null;

        for (int i = 1; i <= 5; i++) {
            ListNode node = new ListNode(i);
            if (head == null) {
                head = node;
            } else {
                tail.setNext(node);
            }
            tail = node;
        }

        return head;
    }

    public void printList(ListNode head) {
        ListNode current = head;
        while (current != null) {
            System.out.print(current.getData() + " -> ");
            current = current.getNext();
        }
        System.out.println("null");
    }

    public static void main(String[] args) {
        LinkedListReversal reversal = new LinkedListReversal();

        // Создаем список
        ListNode head = reversal.constructLinkedList();
        System.out.println("Исходный список:");
        reversal.printList(head);

        // Итеративное обращение
        ListNode reversed = reversal.reverseList(head);
        System.out.println("Перевернутый список (итеративно):");
        reversal.printList(reversed);

        // Создаем новый список для рекурсивного метода
        head = reversal.constructLinkedList();
        reversed = reversal.reverseListRecursive(head);
        System.out.println("Перевернутый список (рекурсивно):");
        reversal.printList(reversed);
    }
}
```

## Визуализация процесса

### Итеративный алгоритм

```text
Исходный список: 1 -> 2 -> 3 -> 4 -> 5 -> null

Шаг 1: previous = null, current = 1
       1 -> null, previous = 1, current = 2

Шаг 2: 2 -> 1 -> null, previous = 2, current = 3

Шаг 3: 3 -> 2 -> 1 -> null, previous = 3, current = 4

Шаг 4: 4 -> 3 -> 2 -> 1 -> null, previous = 4, current = 5

Шаг 5: 5 -> 4 -> 3 -> 2 -> 1 -> null, previous = 5, current = null

Результат: 5 -> 4 -> 3 -> 2 -> 1 -> null
```

### Рекурсивный алгоритм

```text
reverseListRecursive(1 -> 2 -> 3 -> 4 -> 5)
  reverseListRecursive(2 -> 3 -> 4 -> 5)
    reverseListRecursive(3 -> 4 -> 5)
      reverseListRecursive(4 -> 5)
        reverseListRecursive(5)
          return 5
        return 5, 4 -> 5 -> 4, 4 -> null
      return 5, 3 -> 4 -> 3, 3 -> null
    return 5, 2 -> 3 -> 2, 2 -> null
  return 5, 1 -> 2 -> 1, 1 -> null
return 5 -> 4 -> 3 -> 2 -> 1 -> null
```

## Варианты задачи

### Обращение части списка

```java
public ListNode reverseBetween(ListNode head, int left, int right) {
    if (head == null || left == right) {
        return head;
    }

    ListNode dummy = new ListNode(0);
    dummy.setNext(head);
    ListNode prev = dummy;

    for (int i = 0; i < left - 1; i++) {
        prev = prev.getNext();
    }

    ListNode current = prev.getNext();
    for (int i = 0; i < right - left; i++) {
        ListNode next = current.getNext();
        current.setNext(next.getNext());
        next.setNext(prev.getNext());
        prev.setNext(next);
    }

    return dummy.getNext();
}
```

### Обращение каждых k элементов

```java
public ListNode reverseKGroup(ListNode head, int k) {
    if (head == null || k == 1) {
        return head;
    }

    ListNode current = head;
    int count = 0;

    while (current != null && count < k) {
        current = current.getNext();
        count++;
    }

    if (count == k) {
        current = reverseKGroup(current, k);

        while (count > 0) {
            ListNode next = head.getNext();
            head.setNext(current);
            current = head;
            head = next;
            count--;
        }
        head = current;
    }

    return head;
}
```

## Лучшие практики

Итеративный реверс имеет временную сложность `O(n)` и пространственную сложность `O(1)`. Он использует три указателя (`previous`, `current`, `next`) и предпочтителен для продакшена из‑за своей эффективности и отсутствия риска переполнения стека. Итеративный подход особенно эффективен для больших списков, где рекурсивный подход может вызвать проблемы с памятью.

Рекурсивный реверс имеет временную сложность `O(n)` и пространственную сложность `O(n)` из‑за стека вызовов. Он элегантен и читаем, но имеет риск переполнения стека на больших списках. Рекурсивный подход следует использовать только для небольших списков или когда читаемость кода важнее производительности.

Граничные случаи требуют особого внимания. Необходимо проверять `null head` и список из одного элемента, так как эти случаи могут привести к ошибкам, если не обработаны правильно. Важно не разрывать ссылки до переназначения, чтобы избежать потери данных и утечек памяти.

Для двусвязного списка при наличии указателя `prev` необходимо обновлять оба указателя при реверсе. Это включает обновление как `next`, так и `prev` для каждого узла, чтобы сохранить целостность структуры данных.

## Резюме

В этом руководстве мы реализовали два алгоритма обращения связанных списков на `Java`. Ключевые моменты включают то, что итеративный алгоритм использует константную память и более эффективен, рекурсивный алгоритм более элегантен, но использует стек вызовов, оба алгоритма имеют временную сложность `O(n)`, для больших списков предпочтительнее итеративный подход, и алгоритм можно адаптировать для обращения части списка или групп элементов. Выбор между итеративным и рекурсивным подходом зависит от конкретных требований: производительности, читаемости кода и размера данных.

## Реализация на Kotlin

### Класс ListNode

```kotlin
class ListNodeK(var data: Int) {
    var next: ListNodeK? = null
}
```

### Итеративный алгоритм (Kotlin)

```kotlin
fun reverseListK(head: ListNodeK?): ListNodeK? {
    var previous: ListNodeK? = null
    var current: ListNodeK? = head

    while (current != null) {
        val nextElement = current.next
        current.next = previous
        previous = current
        current = nextElement
    }

    return previous
}
```

### Рекурсивный алгоритм (Kotlin)

```kotlin
fun reverseListRecursiveK(head: ListNodeK?): ListNodeK? {
    if (head == null) {
        return null
    }

    if (head.next == null) {
        return head
    }

    val node = reverseListRecursiveK(head.next)
    head.next?.next = head
    head.next = null

    return node
}
```

### Вспомогательные функции

```kotlin
fun constructLinkedListK(): ListNodeK? {
    var head: ListNodeK? = null
    var tail: ListNodeK? = null

    for (i in 1..5) {
        val node = ListNodeK(i)
        if (head == null) {
            head = node
        } else {
            tail?.next = node
        }
        tail = node
    }

    return head
}

fun printListK(head: ListNodeK?) {
    var current = head
    while (current != null) {
        print("${current.data} -> ")
        current = current.next
    }
    println("null")
}
```

### Пример использования

```kotlin
fun main() {
    // Создаем список
    var head = constructLinkedListK()
    println("Исходный список:")
    printListK(head)

    // Итеративное обращение
    var reversed = reverseListK(head)
    println("Перевернутый список (итеративно):")
    printListK(reversed)

    // Создаем новый список для рекурсивного метода
    head = constructLinkedListK()
    reversed = reverseListRecursiveK(head)
    println("Перевернутый список (рекурсивно):")
    printListK(reversed)
}
```
