# Реверс Linked List

Руководство по реализации алгоритмов обращения связанных списков в Java: итеративный и рекурсивный подходы.

**Последнее обновление:** 2025-01-15

## Полезные ссылки

### Официальная документация
- [LinkedList JavaDoc](https://docs.oracle.com/javase/8/docs/api/java/util/LinkedList.html)
- [Java Collections Framework](https://docs.oracle.com/javase/tutorial/collections/)

### См. также
- [Поиск среднего элемента Linked List](./linked-list-middle.md)
- [Структуры данных](../problems/)

## Содержание

- [Обзор](#обзор)
- [Структура связанного списка](#структура-связанного-списка)
- [Java Implementation](#java-implementation)
- [Kotlin Implementation](#kotlin-implementation)
- [Сравнение подходов](#сравнение-подходов)
- [Полная реализация](#полная-реализация)

## Обзор

В этом руководстве мы реализуем два алгоритма обращения связанных списков на Java.

Связный список - это линейная структура данных, в которой указатель в каждом элементе определяет порядок. Каждый элемент связанного списка содержит поле данных для хранения данных списка и поле указателя для указания на следующий элемент в последовательности. Кроме того, мы можем использовать головной указатель, чтобы указать на начальный элемент связанного списка.

После того, как мы реверсируем связанный список, заголовок будет указывать на последний элемент исходного связанного списка, а указатель каждого элемента будет указывать на предыдущий элемент исходного связанного списка.

В Java у нас есть класс LinkedList для реализации двусвязного списка, реализующего интерфейсы List и Deque. Однако в этом руководстве мы будем использовать общую структуру данных односвязного списка.

## Структура связанного списка

Давайте сначала начнем с класса ListNode для представления элемента связанного списка:

```java
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

Класс ListNode имеет два поля:

1. Целочисленное значение для представления данных элемента
2. Указатель/ссылка на следующий элемент

Связанный список может содержать несколько объектов ListNode. Например, мы можем создать приведенный выше пример связанного списка с циклом:

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

## Java Implementation

### Итеративный алгоритм

Давайте реализуем итерационный алгоритм на Java:

```java
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
```

В этом итеративном алгоритме мы используем две переменные ListNode, предыдущую и текущую, для представления двух соседних элементов в связанном списке. Для каждой итерации мы меняем эти два элемента местами, а затем переходим к следующим двум элементам.

В конце концов, текущий указатель будет нулевым, а предыдущий указатель будет последним элементом старого связанного списка. Таким образом, previous также является новым указателем головы обратно связанного списка, и мы возвращаем его из метода.

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

В этом модульном тесте мы сначала создадим пример связанного списка с пятью узлами. Кроме того, мы проверяем, что каждый узел в связанном списке содержит правильное значение данных. Затем мы вызываем итеративную функцию для обращения связанного списка. Наконец, мы проверяем перевернутый связанный список, чтобы убедиться, что данные перевернуты, как ожидалось.

### Рекурсивный алгоритм

Теперь давайте реализуем рекурсивный алгоритм на Java:

```java
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
```

В функции reverseListRecursive мы рекурсивно посещаем каждый элемент в связанном списке, пока не достигнем последнего. Этот последний элемент станет новым заголовком обратно связанного списка. Кроме того, мы добавляем посещенный элемент в конец частично перевернутого связанного списка.

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
| Временная сложность | O(n) | O(n) |
| Пространственная сложность | O(1) | O(n) |
| Риск переполнения стека | Нет | Да (для больших списков) |
| Читаемость | Хорошая | Отличная |
| Производительность | Высокая | Средняя |

### Преимущества итеративного подхода

- ✅ Константная память O(1)
- ✅ Нет риска переполнения стека
- ✅ Обычно быстрее на практике
- ✅ Легче отлаживать

### Преимущества рекурсивного подхода

- ✅ Более элегантный и читаемый код
- ✅ Прямое отражение логики алгоритма
- ✅ Меньше переменных для отслеживания

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

```
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

```
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

## Резюме

В этом руководстве мы реализовали два алгоритма обращения связанных списков на Java.

Ключевые моменты:
- Итеративный алгоритм использует константную память и более эффективен
- Рекурсивный алгоритм более элегантен, но использует стек вызовов
- Оба алгоритма имеют временную сложность O(n)
- Для больших списков предпочтительнее итеративный подход
- Алгоритм можно адаптировать для обращения части списка или групп элементов

Выбор между итеративным и рекурсивным подходом зависит от конкретных требований: производительности, читаемости кода и размера данных.

## Kotlin Implementation

### Класс ListNode

```kotlin
class ListNodeK(var data: Int) {
    var next: ListNodeK? = null
}
```

### Итеративный алгоритм

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

### Рекурсивный алгоритм

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
