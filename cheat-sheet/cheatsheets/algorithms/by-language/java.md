## Algorithms (Array Sort) - Руководство по Heap Sort

Сортировка кучей опирается на структуру данных Heap (куча) — специализированное полное дерево, где значение каждого узла ≤ значений потомков (для Min-Heap) или ≥ (для Max-Heap). Наименьший (или наибольший) элемент всегда в корне. Часто используется для приоритетных очередей.

Правила полного бинарного дерева:
- Если единственный ребёнок — это левый.
- Только самый правый узел на самом глубоком уровне может иметь одного ребёнка.
- Листья только на самом глубоком уровне.

Вставка в Min-Heap:
1) Добавить новый лист в правый доступный слот на самом глубоком уровне.
2) Если элемент меньше родителя — swap.
3) Повторять, пока не станет корнем или меньше родителя.

Пример вставки 4 и 1 с пошаговыми swap, затем объяснение индексации в массиве (родитель (i-1)/2, левый 2i+1, правый 2i+2). Реализация через ArrayList:

```java
class BinaryTree<E> {
    List<E> elements = new ArrayList<>();
    void add(E e) { elements.add(e); }
    boolean isEmpty() { return elements.isEmpty(); }
    E elementAt(int index) { return elements.get(index); }
    int parentIndex(int index) { return (index - 1) / 2; }
    int leftChildIndex(int index) { return 2 * index + 1; }
    int rightChildIndex(int index) { return 2 * index + 2; }
}
```

Heap с подъёмом элемента:

```java
class Heap<E extends Comparable<E>> {
    void add(E e) {
        elements.add(e);
        int elementIndex = elements.size() - 1;
        while (!isRoot(elementIndex) && !isCorrectChild(elementIndex)) {
            int parentIndex = parentIndex(elementIndex);
            swap(elementIndex, parentIndex);
            elementIndex = parentIndex;
        }
    }
    // isRoot, isCorrectChild, isCorrect, isValidIndex, swap...
}
```

Удаление корня (pop): заменить корень на последний лист, удалить лист, просеять вниз до восстановления свойства кучи:

```java
E pop() {
    if (isEmpty()) throw new IllegalStateException("You cannot pop from an empty heap");
    E result = elementAt(0);
    int last = elements.size() - 1;
    swap(0, last);
    elements.remove(last);
    int idx = 0;
    while (!isLeaf(idx) && !isCorrectParent(idx)) {
        int child = smallerChildIndex(idx);
        swap(idx, child);
        idx = child;
    }
    return result;
}
```

Сортировка кучей: построить кучу, многократно pop корень и добавлять в результат:

```java
static <E extends Comparable<E>> List<E> sort(Iterable<E> elements) {
    Heap<E> heap = of(elements);
    List<E> result = new ArrayList<>();
    while (!heap.isEmpty()) {
        result.add(heap.pop());
    }
    return result;
}
```

Тест:

```java
@Test
void givenNotEmptyIterable_whenSortCalled_thenItShouldReturnElementsInSortedList() {
    List<Integer> elements = Arrays.asList(3, 5, 1, 4, 2);
    List<Integer> sortedElements = Heap.sort(elements);
    assertThat(sortedElements).isEqualTo(Arrays.asList(1, 2, 3, 4, 5));
}
```

Сложность: вставка и удаление по O(log n); повторяем n раз → O(n log n). Память — O(n) (можно in-place версию сложнее). Быстрая сортировка обычно быстрее на практике, но Heap Sort гарантирует O(n log n) в худшем случае.

