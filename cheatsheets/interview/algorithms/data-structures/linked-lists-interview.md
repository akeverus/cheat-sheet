---
title: "Вопросы на собеседовании: Связные списки"
description: "Алгоритмы и приёмы работы со связными списками: реверс, обнаружение цикла (Floyd), middle node, merge two sorted, удаление N-ого с конца, LRU cache, doubly-linked list"
tags:
  - interview
  - algorithms
  - linked-lists-interview
type: "interview"
difficulty: "intermediate"
aliases:
  - "Вопросы на собеседовании"
  - "Связные списки"
  - "Linked lists interview"
  - "Связные списки собеседование"
prerequisites: []
next: []
updated: "2026-05-19"
---
# Вопросы на собеседовании: `Связные списки`

Связные списки — основа классических задач: реверс, обнаружение цикла (Флойд), middle node, слияние, LRU cache. На собеседовании их любят, потому что они проверяют умение работать с указателями, dummy node и edge cases (пустой список, один элемент, цикл).

## Полезные ссылки

### Официальная документация и авторитетные источники

- [Java LinkedList — Baeldung](https://www.baeldung.com/java-linkedlist)
- [Java ArrayList vs LinkedList — Baeldung](https://www.baeldung.com/java-arraylist-linkedlist)
- [Reverse Linked List — Baeldung](https://www.baeldung.com/java-reverse-linked-list)
- [Floyd's Cycle Finding — Wikipedia](https://en.wikipedia.org/wiki/Cycle_detection#Floyd's_tortoise_and_hare)
- [LRU Cache via LinkedHashMap — Baeldung](https://www.baeldung.com/java-lru-cache)

## Содержание

- [Полезные ссылки](#полезные-ссылки)
- [See also](#see-also)

**Базовые структуры**
- [Q1. (!) Чем singly-linked отличается от doubly-linked?](#q1--чем-singly-linked-отличается-от-doubly-linked)
- [Q2. (!) Чем LinkedList отличается от ArrayList в Java?](#q2--чем-linkedlist-отличается-от-arraylist-в-java)
- [Q3. Что такое sentinel/dummy node и зачем он нужен?](#q3-что-такое-sentineldummy-node-и-зачем-он-нужен)
- [Q4. Как реализовать circular linked list?](#q4-как-реализовать-circular-linked-list)

**Базовые операции**
- [Q5. (!) Как добавить узел в начало/конец/середину?](#q5--как-добавить-узел-в-началоконецсередину)
- [Q6. Как удалить узел по значению?](#q6-как-удалить-узел-по-значению)
- [Q7. (!) Как удалить узел, имея только ссылку на него (без head)?](#q7--как-удалить-узел-имея-только-ссылку-на-него-без-head)

**Реверс и перестановка**
- [Q8. (!) Реверс связного списка — итеративный и рекурсивный?](#q8--реверс-связного-списка--итеративный-и-рекурсивный)
- [Q9. (!) Реверс между позициями m и n?](#q9--реверс-между-позициями-m-и-n)
- [Q10. (!) Реверс группами по K элементов?](#q10--реверс-группами-по-k-элементов)
- [Q11. Swap pairs — обмен смежных пар?](#q11-swap-pairs--обмен-смежных-пар)

**Two pointers**
- [Q12. (!) Floyd's cycle detection — есть ли цикл?](#q12--floyds-cycle-detection--есть-ли-цикл)
- [Q13. (!) Где начинается цикл?](#q13--где-начинается-цикл)
- [Q14. (!) Найти middle node?](#q14--найти-middle-node)
- [Q15. (!) Удалить N-ый узел с конца?](#q15--удалить-n-ый-узел-с-конца)
- [Q16. (!) Найти точку пересечения двух списков?](#q16--найти-точку-пересечения-двух-списков)

**Слияние и сортировка**
- [Q17. (!) Слияние двух отсортированных списков?](#q17--слияние-двух-отсортированных-списков)
- [Q18. (!) Слияние K отсортированных списков?](#q18--слияние-k-отсортированных-списков)
- [Q19. (!) Merge Sort на связном списке?](#q19--merge-sort-на-связном-списке)
- [Q20. Сложить два числа, представленных списками?](#q20-сложить-два-числа-представленных-списками)

**Проверки и преобразования**
- [Q21. (!) Проверить, что список — палиндром?](#q21--проверить-что-список--палиндром)
- [Q22. Удалить дубликаты из отсортированного списка?](#q22-удалить-дубликаты-из-отсортированного-списка)
- [Q23. Разделить список вокруг значения X (partition)?](#q23-разделить-список-вокруг-значения-x-partition)
- [Q24. Копирование списка с random pointer?](#q24-копирование-списка-с-random-pointer)

**Применения**
- [Q25. (!) LRU Cache на основе doubly-linked list?](#q25--lru-cache-на-основе-doubly-linked-list)
- [Q26. (!) Реализовать стек на linked list?](#q26--реализовать-стек-на-linked-list)
- [Q27. (!) Реализовать очередь на linked list?](#q27--реализовать-очередь-на-linked-list)
- [Q28. Skip list — что это и зачем?](#q28-skip-list--что-это-и-зачем)

**Java-специфика и подвохи**
- [Q29. (!) Можно ли использовать Binary Search для LinkedList?](#q29--можно-ли-использовать-binary-search-для-linkedlist)
- [Q30. Почему LinkedList ест больше памяти, чем ArrayList?](#q30-почему-linkedlist-ест-больше-памяти-чем-arraylist)
- [Q31. (!) Когда стоит использовать LinkedList в Java?](#q31--когда-стоит-использовать-linkedlist-в-java)
- [Q32. Что такое ConcurrentLinkedQueue?](#q32-что-такое-concurrentlinkedqueue)

## Q1. (!) Чем singly-linked отличается от doubly-linked?

Разница в одной ссылке: у doubly-узла есть и `next`, и `prev`, а у singly — только `next`. Это «лишнее» поле меняет асимптотику ключевых операций.

| Тип | Ссылки в узле | Память | Двунаправленный обход |
|-----|---------------|--------|----------------------|
| **Singly** | `next` | Меньше (1 ссылка) | Нет — только вперёд |
| **Doubly** | `prev` + `next` | Больше (2 ссылки) | Да |

```java
class SinglyNode<T> {
    T value;
    SinglyNode<T> next;
}

class DoublyNode<T> {
    T value;
    DoublyNode<T> prev, next;
}
```

**Почему это важно.** Чтобы удалить узел `x`, нужно поправить ссылку его предшественника. В singly предшественника не видно — приходится идти от `head` и искать его за `O(n)`. В doubly он доступен мгновенно через `x.prev`, поэтому удаление по ссылке — `O(1)`. Плата за это — лишнее поле `prev` (≈ +8 байт на узел на 64-битной JVM).

**Когда нужен doubly:**
- Удаление узла за `O(1)` при наличии ссылки на него (LRU cache, `LinkedHashMap` с `accessOrder=true`)
- Двунаправленный обход (именно поэтому Java `LinkedList` — doubly: он реализует `Deque` и `descendingIterator()`)
- Browser history (переходы back/forward)

**Когда хватает singly:** обход всегда односторонний и важен каждый байт:
- Стек/очередь
- Сложение чисел в порядке цифр
- Цепочки в хеш-таблице (chaining)


## Q2. (!) Чем LinkedList отличается от ArrayList в Java?

`ArrayList` — это массив с непрерывной памятью и `O(1)`-доступом по индексу; `LinkedList` — цепочка отдельных узлов с `O(n)`-доступом, но `O(1)`-вставкой в начало. Главное на практике — не асимптотика, а cache locality: массив выигрывает почти всегда.

| Операция | ArrayList | LinkedList |
|----------|-----------|------------|
| `get(i)` | `O(1)` | `O(n)` (или `O(min(i, n-i))` с оптимизацией) |
| `add(e)` (в конец) | `O(1)` амортиз. | `O(1)` |
| `add(0, e)` (в начало) | `O(n)` | `O(1)` |
| `remove(i)` | `O(n)` | `O(n)` (поиск) + `O(1)` (удаление) |
| Память на элемент | 1 ссылка | 3 ссылки (`prev`, `next`, `value`) + объект Node |
| Cache locality | Отличная | Плохая |

```java
// LinkedList в Java реализует Deque — отличный выбор для FIFO/LIFO
LinkedList<Integer> queue = new LinkedList<>();
queue.offer(1); queue.offer(2);
queue.poll(); // 1

// Но как очередь чаще предпочитают ArrayDeque — быстрее, меньше памяти
Deque<Integer> deque = new ArrayDeque<>();
```

**Подводный камень.** У `LinkedList` `get(i)` асимптотически `O(n)`, но цена ещё выше из-за cache miss: каждый переход по `next` — прыжок на случайный адрес в памяти. Поэтому линейный обход `LinkedList` на бенчмарках в разы медленнее `ArrayList`, даже при равной сложности.

**Рекомендация:** даже для частых вставок в начало `ArrayDeque` обычно быстрее `LinkedList` благодаря лучшей локальности. `LinkedList` оправдан только когда нужны итераторы с `remove()` посередине без сдвига массива.

Подробнее — в [Java Collections](../../programming-languages/java/java-collections-interview.md).


## Q3. Что такое sentinel/dummy node и зачем он нужен?

**Dummy/sentinel node** — фиктивный узел перед `head`. Упрощает код, убирая edge case «пустой список или удаление head».

```java
ListNode removeElements(ListNode head, int val) {
    ListNode dummy = new ListNode(0);
    dummy.next = head;
    ListNode curr = dummy;

    while (curr.next != null) {
        if (curr.next.val == val) {
            curr.next = curr.next.next;
        } else {
            curr = curr.next;
        }
    }
    return dummy.next; // возвращаем настоящий head
}
```

**В чём польза.** Удаление обычного узла единообразно: `prev.next = curr.next`. Но у `head` нет предшественника, поэтому без dummy его пришлось бы обрабатывать отдельной веткой `if`. Dummy даёт `head` фиктивного предшественника — и весь цикл становится одинаковым для всех узлов, включая первый.

**Эмпирическое правило:** как только в задаче может меняться сам `head` (удаление, вставка в начало, реверс участка), заводи dummy node — это убирает большинство граничных случаев и багов.


## Q4. Как реализовать circular linked list?

В **circular** списке последний узел замыкается на первый: `tail.next = head` вместо `null`. Обход никогда не упирается в конец — список зациклен. Применяется там, где элементы перебираются по кругу: round-robin шедулеры, циклическая смена игроков, плееры с repeat.

```java
class CircularList<T> {
    private Node<T> tail; // храним только tail для O(1) добавления

    void add(T value) {
        Node<T> node = new Node<>(value);
        if (tail == null) {
            node.next = node;
        } else {
            node.next = tail.next; // новый указывает на head
            tail.next = node;
        }
        tail = node;
    }

    void traverse() {
        if (tail == null) return;
        Node<T> curr = tail.next; // head
        do {
            System.out.println(curr.value);
            curr = curr.next;
        } while (curr != tail.next);
    }
}
```

**Почему храним `tail`, а не `head`.** В circular-списке `head` всегда доступен как `tail.next`, а вот `tail` через `head` пришлось бы искать за `O(n)`. Поэтому, держа единственный указатель на `tail`, мы получаем `O(1)` и для `addFirst`, и для `addLast` — обе операции вставляют узел в одно и то же место (между `tail` и `head`), отличаясь лишь тем, сдвигаем ли мы `tail`.


## Q5. (!) Как добавить узел в начало/конец/середину?

Стоимость вставки зависит от того, есть ли уже ссылка на нужное место. В начало — всегда `O(1)`. В конец — `O(1)`, если храним `tail`, иначе `O(n)`. По индексу — `O(n)`, потому что до позиции надо дойти пешком.

```java
class SinglyLinkedList<T> {
    Node<T> head, tail;

    // Добавление в начало — O(1)
    void addFirst(T value) {
        Node<T> node = new Node<>(value);
        node.next = head;
        head = node;
        if (tail == null) tail = node;
    }

    // Добавление в конец — O(1) при наличии tail
    void addLast(T value) {
        Node<T> node = new Node<>(value);
        if (tail == null) {
            head = tail = node;
        } else {
            tail.next = node;
            tail = node;
        }
    }

    // Добавление по позиции — O(n)
    void addAt(int index, T value) {
        if (index == 0) { addFirst(value); return; }
        Node<T> curr = head;
        for (int i = 0; i < index - 1 && curr != null; i++) {
            curr = curr.next;
        }
        if (curr == null) throw new IndexOutOfBoundsException();
        Node<T> node = new Node<>(value);
        node.next = curr.next;
        curr.next = node;
        if (node.next == null) tail = node;
    }
}
```

**Подводный камень.** Без указателя `tail` каждый `addLast` дегенерирует в `O(n)` — приходится каждый раз пробегать список до конца. Поэтому реальные реализации (включая Java `LinkedList`) хранят оба конца: `head` для вставок в начало, `tail` для вставок в конец.


## Q6. Как удалить узел по значению?

Идём по списку и, найдя узел с нужным значением, перекидываем ссылку через него: `prev.next = curr.next`. Dummy node избавляет от отдельной обработки удаления `head`. Ниже два варианта — удалить только первое вхождение и удалить все.

```java
ListNode removeFirst(ListNode head, int val) {
    ListNode dummy = new ListNode(0, head);
    ListNode curr = dummy;
    while (curr.next != null) {
        if (curr.next.val == val) {
            curr.next = curr.next.next;
            return dummy.next; // удалили первое вхождение
        }
        curr = curr.next;
    }
    return dummy.next;
}

// Удалить ВСЕ вхождения
ListNode removeAll(ListNode head, int val) {
    ListNode dummy = new ListNode(0, head);
    ListNode curr = dummy;
    while (curr.next != null) {
        if (curr.next.val == val) curr.next = curr.next.next;
        else                       curr = curr.next;
    }
    return dummy.next;
}
```

`O(n)` время, `O(1)` память. Ключевая деталь в `removeAll`: при удалении узла мы **не** двигаем `curr` дальше, иначе пропустим подряд идущие дубликаты.


## Q7. (!) Как удалить узел, имея только ссылку на него (без head)?

Прямого предшественника здесь нет, а значит честно «выкинуть» узел нельзя. Трюк: **скопировать в него значение следующего узла и удалить уже следующий** — снаружи это выглядит так, будто исчез именно нужный узел.

```java
void deleteNode(ListNode node) {
    if (node == null || node.next == null) return; // не работает для tail
    node.val = node.next.val;
    node.next = node.next.next;
}
```

**Почему так.** Удалить узел = выкинуть его из цепочки, но для этого нужен `prev`, до которого без `head` не добраться. Вместо удаления самого узла мы делаем его «клоном» соседа и удаляем соседа — данные сдвигаются на одну позицию, и нужное значение пропадает из списка.

**Ограничение:** не работает для последнего узла — у него нет `next`, значение которого можно скопировать. Поэтому в таких задачах обычно явно гарантируется, что удаляемый узел не последний.

`O(1)` время. Без этого трюка пришлось бы искать предшественника с начала — `O(n)`.


## Q8. (!) Реверс связного списка — итеративный и рекурсивный?

Реверс — это разворот всех ссылок `next` в обратную сторону за один проход. Идём по списку, держа три указателя: `prev` (уже развёрнутая часть), `curr` (текущий узел) и `next` (чтобы не потерять хвост, пока перевешиваем ссылку). На каждом шаге `curr.next` указываем на `prev`.

```java
// Итеративный — O(n) время, O(1) память
ListNode reverse(ListNode head) {
    ListNode prev = null, curr = head;
    while (curr != null) {
        ListNode next = curr.next;
        curr.next = prev;
        prev = curr;
        curr = next;
    }
    return prev; // новый head
}

// Рекурсивный — O(n) время и стек
ListNode reverseRecursive(ListNode head) {
    if (head == null || head.next == null) return head;
    ListNode newHead = reverseRecursive(head.next);
    head.next.next = head;  // следующий теперь указывает на нас
    head.next = null;       // обнуляем нашу старую ссылку
    return newHead;
}
```

Как меняются ссылки на примере списка `1 → 2 → 3`:

- **До:** `1 → 2 → 3 → null` (head — это `1`, последний узел `3` указывает на `null`).
- **После:** `3 → 2 → 1 → null` (новый head — это `3`; бывший head `1` стал хвостом, его `next` теперь `null`).

**Компромисс.** Рекурсивный вариант короче и нагляднее, но тратит `O(n)` стека: на длинном списке (десятки-сотни тысяч узлов) он упадёт со `StackOverflowError`. Итеративный — стандарт для production: `O(1)` памяти и без риска переполнения стека.


## Q9. (!) Реверс между позициями m и n?

Разворачиваем только участок `[m, n]`, не трогая остальное. Сначала доходим до узла перед началом участка (`prev`), затем `n - m` раз применяем приём «головной вставки»: каждый следующий узел вынимаем и подсовываем сразу после `prev`. Так участок разворачивается за один проход, без отдельного реверса и сшивания.

```java
ListNode reverseBetween(ListNode head, int m, int n) {
    ListNode dummy = new ListNode(0, head);
    ListNode prev = dummy;

    // Шаг 1: дошли до позиции m-1
    for (int i = 0; i < m - 1; i++) prev = prev.next;

    // Шаг 2: реверсим n-m+1 узлов между prev и after
    ListNode curr = prev.next;
    for (int i = 0; i < n - m; i++) {
        ListNode next = curr.next;
        curr.next = next.next;
        next.next = prev.next;
        prev.next = next;
    }
    return dummy.next;
}
```

Один проход, `O(n)` время, `O(1)` память. Dummy node нужен на случай `m = 1` — когда разворачиваемый участок включает сам `head`.


## Q10. (!) Реверс группами по K элементов?

Разбиваем список на группы по `k` узлов и разворачиваем каждую целиком; неполный «хвост» (меньше `k` узлов) оставляем как есть. Сначала проверяем, что впереди есть `k` узлов, потом разворачиваем их обычным итеративным реверсом, а оставшуюся часть обрабатываем рекурсивно и пришиваем.

```java
ListNode reverseKGroup(ListNode head, int k) {
    // Проверяем, есть ли k элементов
    ListNode curr = head;
    for (int i = 0; i < k; i++) {
        if (curr == null) return head; // меньше k — оставляем как есть
        curr = curr.next;
    }

    // Реверсим первые k
    ListNode prev = null, c = head;
    for (int i = 0; i < k; i++) {
        ListNode next = c.next;
        c.next = prev;
        prev = c;
        c = next;
    }

    // head теперь хвост первой группы — рекурсивно соединяем со следующей
    head.next = reverseKGroup(c, k);
    return prev;
}
```

**Компромисс.** `O(n)` время; рекурсия добавляет `O(n/k)` стека (по одному фрейму на группу). Полностью итеративная версия даёт `O(1)` памяти, но заметно сложнее — на собеседовании рекурсивная читается легче, и её достаточно, если не оговорено ограничение по памяти.


## Q11. Swap pairs — обмен смежных пар?

Меняем местами соседние узлы: `1→2→3→4` превращается в `2→1→4→3`. Это частный случай `reverseKGroup` при `k = 2`, поэтому достаточно простого итеративного прохода с dummy node, без рекурсии.

```java
ListNode swapPairs(ListNode head) {
    ListNode dummy = new ListNode(0, head);
    ListNode prev = dummy;

    while (prev.next != null && prev.next.next != null) {
        ListNode first = prev.next;
        ListNode second = first.next;
        // меняем местами
        first.next = second.next;
        second.next = first;
        prev.next = second;
        // двигаемся вперёд
        prev = first;
    }
    return dummy.next;
}
```

`O(n)` время, `O(1)` память. На каждой итерации перевешиваем три ссылки и сдвигаем `prev` через оба обменянных узла.


## Q12. (!) Floyd's cycle detection — есть ли цикл?

**Алгоритм Флойда (tortoise and hare):** два указателя, slow двигается на 1, fast на 2. Если есть цикл — они встретятся.

```java
boolean hasCycle(ListNode head) {
    ListNode slow = head, fast = head;
    while (fast != null && fast.next != null) {
        slow = slow.next;
        fast = fast.next.next;
        if (slow == fast) return true;
    }
    return false;
}
```

**Почему работает.** Когда оба указателя зайдут в цикл, fast приближается к slow на одну позицию за шаг (он движется на 2, slow — на 1, относительная скорость = 1). Поэтому в цикле длины `L` он гарантированно догонит slow максимум за `L` шагов — выскочить «перепрыгнув» нельзя. Если цикла нет, fast просто упрётся в `null`.

**Компромисс.** Альтернатива — складывать пройденные узлы в `HashSet<ListNode>` и ловить повтор: тоже `O(n)` время, но `O(n)` память. Преимущество Floyd — `O(1)` память при той же скорости.

`O(n)` время, `O(1)` память.


## Q13. (!) Где начинается цикл?

Сначала находим точку встречи алгоритмом Флойда (Q12). Затем один указатель возвращаем в `head`, второй оставляем в точке встречи и двигаем оба со скоростью 1 — они встретятся ровно в начале цикла.

```java
ListNode detectCycle(ListNode head) {
    ListNode slow = head, fast = head;

    // Фаза 1: проверка цикла
    while (fast != null && fast.next != null) {
        slow = slow.next;
        fast = fast.next.next;
        if (slow == fast) {
            // Фаза 2: ищем начало
            ListNode start = head;
            while (start != slow) {
                start = start.next;
                slow = slow.next;
            }
            return start;
        }
    }
    return null;
}
```

**Почему работает:** пусть `μ` — длина «хвоста» до цикла, `λ` — длина цикла. Когда они встречаются, slow прошёл `μ + k` (где `k < λ`), fast — `2(μ + k)`. Разница `μ + k` кратна `λ`. От места встречи до начала цикла — `λ - k` шагов; от head до начала цикла — `μ` шагов. Поскольку `μ + k = nλ`, то `μ = nλ - k`, что mod `λ` равно `λ - k`.

`O(n)` время, `O(1)` память.


## Q14. (!) Найти middle node?

Тот же приём «черепаха и заяц», но без цикла: fast идёт вдвое быстрее slow. Когда fast достигнет конца, slow окажется ровно посередине — он прошёл половину пути. Главное преимущество — один проход без предварительного подсчёта длины списка.

```java
ListNode middleNode(ListNode head) {
    ListNode slow = head, fast = head;
    while (fast != null && fast.next != null) {
        slow = slow.next;
        fast = fast.next.next;
    }
    return slow;
}
```

**Граничный случай.** При **чётном** числе узлов есть два центральных. Эта версия вернёт второй из них. Чтобы получить первый (что нужно, например, для разбиения в Merge Sort), меняем условие на `while (fast.next != null && fast.next.next != null)`.

`O(n)` время, `O(1)` память. Тот же приём — основа Merge Sort на linked list (Q19).


## Q15. (!) Удалить N-ый узел с конца?

**One-pass через two pointers:** fast уходит на N шагов вперёд, потом оба двигаются вместе.

```java
ListNode removeNthFromEnd(ListNode head, int n) {
    ListNode dummy = new ListNode(0, head);
    ListNode slow = dummy, fast = dummy;

    // fast уходит на n+1 шагов вперёд
    for (int i = 0; i <= n; i++) fast = fast.next;

    // Двигаем оба, пока fast не дойдёт до конца
    while (fast != null) {
        slow = slow.next;
        fast = fast.next;
    }

    // slow указывает на узел перед удаляемым
    slow.next = slow.next.next;
    return dummy.next;
}
```

**Почему `n+1` шагов.** Между fast и slow держим зазор в `n+1` узлов. Когда fast дойдёт до `null` (за концом), slow остановится на узле **перед** удаляемым — именно его предшественник нужен, чтобы перекинуть ссылку `slow.next = slow.next.next`. Из-за dummy зазор отсчитывается от него, поэтому удаление самого `head` (когда `n` равно длине списка) обрабатывается без отдельной ветки.

Один проход, `O(n)` время, `O(1)` память.


## Q16. (!) Найти точку пересечения двух списков?

Два указателя идут по своим спискам, а дойдя до конца — переходят на чужой. Так оба проходят суммарно `m + n` узлов и выравниваются: если списки пересекаются, они встретятся ровно в узле пересечения. Это убирает необходимость заранее считать длины и сдвигать более длинный список.

```java
ListNode getIntersection(ListNode a, ListNode b) {
    if (a == null || b == null) return null;
    ListNode pA = a, pB = b;

    // Каждый указатель проходит свой список + чужой
    // Если есть пересечение — встретятся в нём (через 2 итерации макс)
    while (pA != pB) {
        pA = (pA == null) ? b : pA.next;
        pB = (pB == null) ? a : pB.next;
    }
    return pA; // null если нет пересечения, или узел пересечения
}
```

**Идея:** если списки имеют длины `m` и `n` и пересекаются, то `pA` пройдёт `m + n` шагов, как и `pB` — они встретятся в точке пересечения. Если не пересекаются — оба станут `null` одновременно.

`O(m + n)` время, `O(1)` память.


## Q17. (!) Слияние двух отсортированных списков?

Идём по обоим спискам сразу и на каждом шаге прицепляем к результату меньший из текущих узлов. Когда один список кончился, остаток второго (уже отсортированный) пришиваем целиком. Узлы не копируем — переиспользуем существующие, поэтому память `O(1)`.

```java
ListNode mergeTwoLists(ListNode l1, ListNode l2) {
    ListNode dummy = new ListNode(0);
    ListNode curr = dummy;

    while (l1 != null && l2 != null) {
        if (l1.val <= l2.val) {
            curr.next = l1;
            l1 = l1.next;
        } else {
            curr.next = l2;
            l2 = l2.next;
        }
        curr = curr.next;
    }
    curr.next = (l1 != null) ? l1 : l2; // прицепляем хвост
    return dummy.next;
}
```

`O(n + m)` время, `O(1)` память. Dummy node избавляет от обработки пустого результата и выбора первого узла.


## Q18. (!) Слияние K отсортированных списков?

Наивно слить списки попарно по очереди стоило бы `O(N·k)`. Идея лучше — всегда брать глобально минимальный из голов всех списков; для этого держим их в min-heap.

**Через PriorityQueue (min-heap):** `O(N log k)`, где `N` — общее число узлов. В куче одновременно не больше `k` элементов (по одной голове на список), поэтому каждое извлечение/вставка — `O(log k)`.

```java
ListNode mergeKLists(ListNode[] lists) {
    PriorityQueue<ListNode> pq = new PriorityQueue<>(
        Comparator.comparingInt(n -> n.val)
    );
    for (ListNode head : lists) {
        if (head != null) pq.offer(head);
    }

    ListNode dummy = new ListNode(0);
    ListNode curr = dummy;
    while (!pq.isEmpty()) {
        ListNode node = pq.poll();
        curr.next = node;
        curr = node;
        if (node.next != null) pq.offer(node.next);
    }
    return dummy.next;
}
```

**Альтернатива через Divide and Conquer:** сливаем списки попарно «турнирной сеткой» (`k → k/2 → k/4 → …`), используя `mergeTwoLists` из Q17. Та же асимптотика `O(N log k)`, но на практике обычно быстрее: нет накладных расходов на heap и лучше cache locality.


## Q19. (!) Merge Sort на связном списке?

Merge Sort — оптимальный выбор для linked list: стабильная сортировка `O(n log n)`, не требующая случайного доступа и дополнительных массивов (расход — лишь `O(log n)` стека на рекурсию). Схема классическая: найти середину (через slow/fast из Q14), рекурсивно отсортировать половины и слить их (через `mergeTwoLists` из Q17).

```java
ListNode sortList(ListNode head) {
    if (head == null || head.next == null) return head;

    // 1. Найти середину и разделить
    ListNode slow = head, fast = head, prev = null;
    while (fast != null && fast.next != null) {
        prev = slow;
        slow = slow.next;
        fast = fast.next.next;
    }
    prev.next = null; // разрываем

    // 2. Рекурсивно отсортировать половины
    ListNode left = sortList(head);
    ListNode right = sortList(slow);

    // 3. Слияние
    return mergeTwoLists(left, right);
}
```

**Почему не Quick Sort.** На массивах Quick Sort обычно быстрее — он in-place и cache-friendly. Но Quick Sort опирается на случайный доступ (выбор pivot, разбиение к индексам), а у linked list доступ к элементу — `O(n)`. Поэтому здесь побеждает Merge Sort: он работает чисто последовательным обходом, без случайных прыжков по памяти.


## Q20. Сложить два числа, представленных списками?

Цифры хранятся в обратном порядке (младший разряд — в `head`), поэтому складывать можно «в столбик» сразу при проходе, аккуратно перенося carry в следующий разряд. Обратный порядок здесь удобен: head обоих списков — это единицы, и идти можно слева направо без реверса.

Пример: `2 → 4 → 3` = 342, `5 → 6 → 4` = 465. Результат: `7 → 0 → 8` = 807.

```java
ListNode addTwoNumbers(ListNode l1, ListNode l2) {
    ListNode dummy = new ListNode(0);
    ListNode curr = dummy;
    int carry = 0;

    while (l1 != null || l2 != null || carry != 0) {
        int sum = carry;
        if (l1 != null) { sum += l1.val; l1 = l1.next; }
        if (l2 != null) { sum += l2.val; l2 = l2.next; }
        carry = sum / 10;
        curr.next = new ListNode(sum % 10);
        curr = curr.next;
    }
    return dummy.next;
}
```

`O(max(n, m))` время. Условие цикла `|| carry != 0` важно: оно дописывает финальный перенос (например, `5 + 5 = 0`, carry `1` → новый узел `1`). Если бы цифры шли в **прямом** порядке, пришлось бы сначала реверсировать списки либо складывать через два стека.


## Q21. (!) Проверить, что список — палиндром?

В односвязном списке нельзя идти назад, поэтому сравнить «с краёв к центру» напрямую не получится. Решение в три шага за `O(1)` памяти: найти середину (slow/fast), развернуть вторую половину и пройти две половины навстречу, сравнивая значения.

```java
boolean isPalindrome(ListNode head) {
    if (head == null || head.next == null) return true;

    // 1. Найти середину
    ListNode slow = head, fast = head;
    while (fast != null && fast.next != null) {
        slow = slow.next;
        fast = fast.next.next;
    }

    // 2. Реверсировать вторую половину
    ListNode second = reverse(slow);

    // 3. Сравнить две половины
    ListNode first = head;
    while (second != null) {
        if (first.val != second.val) return false;
        first = first.next;
        second = second.next;
    }
    return true;
}

ListNode reverse(ListNode head) {
    ListNode prev = null, curr = head;
    while (curr != null) {
        ListNode next = curr.next;
        curr.next = prev;
        prev = curr;
        curr = next;
    }
    return prev;
}
```

**Компромисс.** `O(n)` время, `O(1)` память. Проще скопировать значения в `ArrayList` и сравнить с двух концов — но это уже `O(n)` памяти. Минус подхода с реверсом: он мутирует исходный список (вторая половина оказывается развёрнутой); если нужно сохранить структуру, после проверки разверни её обратно.


## Q22. Удалить дубликаты из отсортированного списка?

Раз список отсортирован, все дубликаты идут подряд — достаточно одного прохода, сравнивая соседей. Ниже два варианта: оставить по одному экземпляру каждого значения (LeetCode 83) и удалить все значения, у которых были дубликаты, целиком (LeetCode 82).

```java
ListNode deleteDuplicates(ListNode head) {
    ListNode curr = head;
    while (curr != null && curr.next != null) {
        if (curr.val == curr.next.val) {
            curr.next = curr.next.next;
        } else {
            curr = curr.next;
        }
    }
    return head;
}

// Удалить ВСЕ дубликаты (LeetCode 82) — оставить только уникальные
ListNode deleteDuplicatesAll(ListNode head) {
    ListNode dummy = new ListNode(0, head);
    ListNode prev = dummy;
    while (head != null) {
        if (head.next != null && head.val == head.next.val) {
            while (head.next != null && head.val == head.next.val) head = head.next;
            prev.next = head.next;
        } else {
            prev = prev.next;
        }
        head = head.next;
    }
    return dummy.next;
}
```

`O(n)` время, `O(1)` память. Первый вариант обходится без dummy (head никогда не удаляется), второму он нужен — там может выпасть и сам `head`.


## Q23. Разделить список вокруг значения X (partition)?

Перераспределяем узлы так, чтобы все со значением `< x` шли до всех со значением `>= x`, сохраняя их исходный относительный порядок. Приём — два вспомогательных списка (`before` и `after`): раскладываем узлы по ним за один проход, затем сшиваем `before → after`.

```java
ListNode partition(ListNode head, int x) {
    ListNode beforeDummy = new ListNode(0), afterDummy = new ListNode(0);
    ListNode before = beforeDummy, after = afterDummy;

    while (head != null) {
        if (head.val < x) {
            before.next = head;
            before = before.next;
        } else {
            after.next = head;
            after = after.next;
        }
        head = head.next;
    }
    after.next = null;
    before.next = afterDummy.next;
    return beforeDummy.next;
}
```

Два dummy node — по одному на каждую часть, что избавляет от обработки пустых половин. Критична строка `after.next = null` перед сшиванием: без неё хвост `after` может ещё ссылаться на узел из `before` и замкнуть список в цикл. `O(n)` время, `O(1)` память.


## Q24. Копирование списка с random pointer?

Каждый узел имеет две ссылки — `next` и `random` (указывает на произвольный узел или `null`). Сложность в том, что при копировании `random` оригинала надо указывать на **копию** соответствующего узла, которой в момент клонирования может ещё не быть. Два решения: через `HashMap` (оригинал → копия) за `O(n)` памяти и трюк с interleaving за `O(1)` доп. памяти.

```java
class Node {
    int val;
    Node next, random;
    Node(int val) { this.val = val; }
}

// O(n) время, O(n) память — HashMap
Node copyRandomList(Node head) {
    if (head == null) return null;
    Map<Node, Node> map = new HashMap<>();

    // Первый проход — клонируем узлы
    Node curr = head;
    while (curr != null) {
        map.put(curr, new Node(curr.val));
        curr = curr.next;
    }

    // Второй проход — настраиваем next и random
    curr = head;
    while (curr != null) {
        map.get(curr).next = map.get(curr.next);
        map.get(curr).random = map.get(curr.random);
        curr = curr.next;
    }
    return map.get(head);
}

// O(n) время, O(1) доп. памяти — interleaving
Node copyRandomListO1(Node head) {
    if (head == null) return null;

    // 1. Вставить копии после оригиналов: A → A' → B → B'
    Node curr = head;
    while (curr != null) {
        Node copy = new Node(curr.val);
        copy.next = curr.next;
        curr.next = copy;
        curr = copy.next;
    }

    // 2. Настроить random для копий
    curr = head;
    while (curr != null) {
        if (curr.random != null) curr.next.random = curr.random.next;
        curr = curr.next.next;
    }

    // 3. Разделить два списка
    Node copyHead = head.next;
    curr = head;
    while (curr != null) {
        Node copy = curr.next;
        curr.next = copy.next;
        copy.next = (copy.next != null) ? copy.next.next : null;
        curr = curr.next;
    }
    return copyHead;
}
```

**Идея interleaving-варианта.** Вместо словаря «оригинал → копия» вставляем каждую копию **сразу за её оригиналом** (`A → A' → B → B' → …`). Тогда копия любого узла находится по фиксированному правилу `copy = orig.next`, и `random` копии вычисляется как `orig.random.next` — без всякой `HashMap`. В конце расщепляем переплетённый список на два. Это и даёт `O(1)` дополнительной памяти.


## Q25. (!) LRU Cache на основе doubly-linked list?

**LRU = Least Recently Used** — кэш фиксированного размера, который при переполнении вытесняет элемент, к которому дольше всего не обращались. Классическая комбинация: `HashMap` для `O(1)`-поиска по ключу плюс doubly-linked list, хранящий элементы в порядке свежести (самый свежий — в начале, кандидат на вытеснение — в конце).

```java
class LRUCache {
    private final int capacity;
    private final Map<Integer, Node> map = new HashMap<>();
    private final Node head = new Node(0, 0); // dummy
    private final Node tail = new Node(0, 0); // dummy

    public LRUCache(int capacity) {
        this.capacity = capacity;
        head.next = tail;
        tail.prev = head;
    }

    public int get(int key) {
        if (!map.containsKey(key)) return -1;
        Node node = map.get(key);
        moveToFront(node);
        return node.value;
    }

    public void put(int key, int value) {
        if (map.containsKey(key)) {
            Node node = map.get(key);
            node.value = value;
            moveToFront(node);
        } else {
            if (map.size() >= capacity) {
                Node lru = tail.prev;
                remove(lru);
                map.remove(lru.key);
            }
            Node node = new Node(key, value);
            addToFront(node);
            map.put(key, node);
        }
    }

    private void moveToFront(Node node) { remove(node); addToFront(node); }
    private void remove(Node node) {
        node.prev.next = node.next;
        node.next.prev = node.prev;
    }
    private void addToFront(Node node) {
        node.next = head.next;
        node.prev = head;
        head.next.prev = node;
        head.next = node;
    }

    private static class Node {
        int key, value;
        Node prev, next;
        Node(int k, int v) { key = k; value = v; }
    }
}
```

**Почему именно эта пара структур.** `get` и `put` — `O(1)`, потому что задачи разделены: `HashMap` за `O(1)` находит узел по ключу, а doubly-list за `O(1)` перевешивает его в начало (удалить по `prev`/`next` и вставить за `head`). Singly-list тут не подошёл бы: удаление узла из середины требовало бы поиска предшественника за `O(n)`. Два dummy-узла (`head`, `tail`) убирают граничные случаи пустого списка и операций с концами.

**Лайфхак для интервью:** в Java тот же LRU укладывается в **5 строк** через `LinkedHashMap` с `accessOrder = true` и переопределённым `removeEldestEntry` — он внутри устроен ровно так же (hash + doubly-list). См. [Hash Tables](hash-tables-interview.md) и `java.util.LinkedHashMap`.


## Q26. (!) Реализовать стек на linked list?

Стек (LIFO) ложится на singly-list идеально: и `push`, и `pop` работают только с одним концом — вершиной (`top`). Держим указатель на голову списка, вставляем и снимаем узлы только с неё — обе операции `O(1)`, без `tail` и без поиска.

```java
class LinkedStack<T> {
    private Node<T> top;
    private int size;

    void push(T value) {
        Node<T> node = new Node<>(value);
        node.next = top;
        top = node;
        size++;
    }

    T pop() {
        if (top == null) throw new EmptyStackException();
        T value = top.value;
        top = top.next;
        size--;
        return value;
    }

    T peek() {
        if (top == null) throw new EmptyStackException();
        return top.value;
    }

    boolean isEmpty() { return top == null; }
    int size() { return size; }

    private static class Node<T> {
        T value;
        Node<T> next;
        Node(T value) { this.value = value; }
    }
}
```

Все операции `O(1)`. **Рекомендация:** в реальном коде на Java вместо самописного стека берут `ArrayDeque` — он быстрее за счёт массива и лучшей cache locality (а `java.util.Stack` устарел из-за лишней синхронизации).


## Q27. (!) Реализовать очередь на linked list?

Очередь (FIFO) добавляет с одного конца, а удаляет с другого, поэтому одного указателя мало — нужны оба: `head` для `dequeue` и `tail` для `enqueue`. Singly-list тут достаточно, так как обход всегда идёт в одну сторону (от head к tail).

```java
class LinkedQueue<T> {
    private Node<T> head, tail;
    private int size;

    void enqueue(T value) {
        Node<T> node = new Node<>(value);
        if (tail == null) {
            head = tail = node;
        } else {
            tail.next = node;
            tail = node;
        }
        size++;
    }

    T dequeue() {
        if (head == null) throw new NoSuchElementException();
        T value = head.value;
        head = head.next;
        if (head == null) tail = null;
        size--;
        return value;
    }

    int size() { return size; }
    boolean isEmpty() { return head == null; }

    private static class Node<T> {
        T value;
        Node<T> next;
        Node(T value) { this.value = value; }
    }
}
```

`O(1)` для обеих операций — благодаря хранению `tail`. Без него `enqueue` пришлось бы каждый раз искать конец за `O(n)`. Важная деталь `dequeue`: когда из очереди ушёл последний элемент, обнуляем и `tail` (`if (head == null) tail = null`), иначе он повиснет на освобождённом узле.


## Q28. Skip list — что это и зачем?

**Skip list** — вероятностная структура: отсортированный linked list, в который добавлены «экспресс-полосы», чтобы искать за `O(log n)` вместо `O(n)`. Каждый узел случайно получает несколько уровней forward-ссылок; верхние уровни прорежены и позволяют перепрыгивать через множество узлов сразу.

```
Уровень 3: head ─────────────────→ 30 ────────→ null
Уровень 2: head ────→ 10 ────────→ 30 ────────→ null
Уровень 1: head ────→ 10 → 20 ──→ 30 → 40 ──→ null
Уровень 0: head ──→ 5→10→15→20→25→30→35→40 ──→ null
```

**Как работает поиск.** Начинаем с верхнего уровня и идём вправо, пока следующий узел не «перелетает» искомое значение; тогда спускаемся на уровень ниже и продолжаем. Так на каждом уровне отсекается примерно половина оставшихся узлов — отсюда `O(log n)` в среднем. Уровень нового узла выбирается случайно по геометрическому распределению (с вероятностью ½ узел поднимается ещё на уровень выше).

**Применение:** `ConcurrentSkipListMap` в Java — concurrent-аналог `TreeMap`. Skip list также используется в Redis (sorted sets), LevelDB, Cassandra.

**Преимущество над BST:** concurrent-версию реализовать заметно проще. Вставка/удаление меняют лишь несколько локальных ссылок и не требуют глобальной ребалансировки с блокировками, как в сбалансированных деревьях (AVL, red-black).


## Q29. (!) Можно ли использовать Binary Search для LinkedList?

**Формально — да, но это бессмысленно.** Binary search строится на `O(1)`-доступе к среднему элементу, а в linked list любой доступ по индексу — `O(n)`. Каждый из `log n` шагов вынужден заново «доходить» до середины за `O(n)`, поэтому итог — `O(n log n)`, что **хуже** обычного линейного поиска `O(n)`.

Альтернативы для отсортированных связных списков:
- **Skip List** — `O(log n)` поиск
- **Tree** — `O(log n)` если сбалансировано
- **Конвертация в массив** — `O(n)` копирование + `O(log n)` поиск

```java
// Это работает, но медленно для длинных списков
int idx = Collections.binarySearch(linkedList, key);
```

`Collections.binarySearch(LinkedList, key)` внутри использует итератор и `O(n)` доступ к середине.


## Q30. Почему LinkedList ест больше памяти, чем ArrayList?

Потому что каждый элемент `LinkedList` оборачивается в отдельный объект-узел со служебными ссылками, тогда как `ArrayList` хранит только сам массив ссылок.

| Структура | Память на элемент |
|-----------|-------------------|
| `ArrayList<Integer>` | ~16 байт (ссылка + Integer) |
| `LinkedList<Integer>` | ~48 байт (Node + 2 ссылки + Integer) |

**Из чего складывается overhead.** Каждый `Node` в `LinkedList` — это отдельный объект с тремя полями (`prev`, `next`, `item`) плюс ~16 байт JVM-заголовка на сам объект. Грубо: на 10⁶ `Integer` `ArrayList` займёт ~16MB, а `LinkedList` — ~48MB, то есть втрое больше.

**Скрытая цена — cache locality.** Узлы аллоцируются по отдельности и лежат в произвольных местах кучи. Поэтому проход по списку — это серия прыжков по случайным адресам, и почти каждое чтение `next` оборачивается cache miss. На практике это бьёт по скорости сильнее, чем сам перерасход памяти.


## Q31. (!) Когда стоит использовать LinkedList в Java?

Короткий ответ — **почти никогда**. Для FIFO/LIFO лучше `ArrayDeque`, для случайного доступа — `ArrayList`; оба выигрывают за счёт cache locality. `LinkedList` оправдан лишь в узкой нише, где удаление через итератор посередине действительно горячее.

Реальные случаи для `LinkedList`:
1. **`Iterator.remove()` в середине** — узел выкидывается за `O(1)` без сдвига массива (у `ArrayList` это `O(n)` на каждое удаление)
2. **Нужны сразу `List` и `Deque`** — `LinkedList` реализует оба интерфейса, если этого требует контракт
3. **Очень много вставок/удалений в середину при уже имеющемся итераторе** на нужную позицию

```java
// Удаление половины элементов — LinkedList выигрывает
Iterator<Integer> it = list.iterator();
while (it.hasNext()) {
    if (someCondition(it.next())) it.remove(); // O(1) для LinkedList
}
```

В большинстве enterprise-кода `LinkedList` появляется по инерции — нужно избегать.


## Q32. Что такое ConcurrentLinkedQueue?

**`ConcurrentLinkedQueue<T>`** — потокобезопасная неблокирующая FIFO-очередь. Вместо `synchronized`/локов она использует **lock-free** алгоритм Michael & Scott (1996): конкурентные вставки и удаления синхронизируются атомарными `CAS`-операциями (compare-and-swap) над ссылками `head`/`tail`. Поток в цикле пытается применить CAS и при неудаче (кто-то опередил) повторяет — без засыпания на блокировке.

```java
ConcurrentLinkedQueue<Integer> queue = new ConcurrentLinkedQueue<>();
queue.offer(1);  // потокобезопасно
queue.poll();    // потокобезопасно
```

**Плюсы:**
- Нет блокировок — потоки не засыпают, нет контеншена на локе и лишних context switch
- Хорошо масштабируется на много ядер при высокой конкуренции

**Минусы:**
- `size()` — `O(n)`: счётчик не хранится (его поддержка стоила бы контеншена), приходится пройти весь список, и результат всё равно приблизителен
- Нет блокирующих операций (`take()`, `put()`) — очередь не умеет ждать появления элемента
- Высокое давление на аллокатор: на каждый `offer` создаётся новый узел

**Когда что брать.** Если нужно, чтобы потребитель **ждал** элемент (паттерн producer-consumer), нужна блокирующая очередь — `LinkedBlockingQueue` или `ArrayBlockingQueue`. `ConcurrentLinkedQueue` — для сценариев, где ждать не надо и важна максимальная пропускная способность. Подробнее — в [Java Concurrency](../../programming-languages/java/java-concurrency-interview.md).


---

## See also

- [Алгоритмы (обзор)](../algorithms-interview.md) — карта алгоритмических тем
- [Массивы и строки](arrays-strings-interview.md) — параллельная категория линейных DS
- [Стеки и очереди](stacks-queues-interview.md) — где linked list — внутренняя реализация
- [Деревья](trees-interview.md) — расширение идеи связной структуры
- [Хеш-таблицы](hash-tables-interview.md) — chained hashing на linked lists
- [Алгоритмы сортировки](../sorting-searching/sorting-algorithms-interview.md) — Merge Sort on linked list
- [Two Pointers](../algorithmic-paradigms/two-pointers-sliding-window-interview.md) — slow/fast применяются здесь
- [Анализ сложности](../complexity/complexity-analysis-interview.md) — амортизированная сложность операций
- [Java Collections](../../programming-languages/java/java-collections-interview.md) — LinkedList vs ArrayList vs ArrayDeque
- [Java Concurrency](../../programming-languages/java/java-concurrency-interview.md) — ConcurrentLinkedQueue, lock-free
- [Графы](graphs-interview.md)
- [Кучи (Heaps)](heaps-interview.md)
