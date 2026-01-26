# Структуры данных

Кратко: Комплексное руководство по основным структурам данных - от простых массивов до сложных деревьев и графов с практическими примерами на Java.

**Дата последнего обновления:** 2026-01-25

## Полезные ссылки

### Официальная документация
- [Java Collections Framework](https://docs.oracle.com/javase/8/docs/technotes/guides/collections/overview.html) - Официальная документация Oracle

### Baeldung
- [Guide to Java Collections](https://www.baeldung.com/java-collections) - Подробное руководство по коллекциям

### См. также
- `../algorithms/README.md` - Алгоритмы для работы со структурами данных
- `../sorting/README.md` - Алгоритмы сортировки
- `../searching/README.md` - Алгоритмы поиска

## Содержание

- [Введение в структуры данных](#введение-в-структуры-данных)
- [Линейные структуры данных](#линейные-структуры-данных)
  - [Массивы (Arrays)](#массивы-arrays)
  - [Связные списки (Linked Lists)](#связные-списки-linked-lists)
  - [Стеки (Stacks)](#стеки-stacks)
  - [Очереди (Queues)](#очереди-queues)
- [Ассоциативные структуры](#ассоциативные-структуры)
  - [Хеш-таблицы (Hash Tables)](#хеш-таблицы-hash-tables)
  - [Деревья поиска (Search Trees)](#деревья-поиска-search-trees)
- [Древовидные структуры](#древовидные-структуры)
  - [Бинарные деревья (Binary Trees)](#бинарные-деревья-binary-trees)
  - [AVL деревья](#avl-деревья)
  - [Красно-черные деревья](#красно-черные-деревья)
- [Графовые структуры](#графовые-структуры)
  - [Представление графов](#представление-графов)
  - [Обход графов](#обход-графов)
- [Выбор структуры данных](#выбор-структуры-данных)

## Введение в структуры данных

Структуры данных определяют способ организации и хранения данных в памяти компьютера. Правильный выбор структуры данных критически влияет на эффективность алгоритмов.

### Основные характеристики структур данных

```java
/**
 * Интерфейс демонстрирующий основные операции над структурами данных
 */
public interface DataStructure<T> {

    /**
     * Добавление элемента
     * @param element добавляемый элемент
     * @return true если элемент добавлен успешно
     */
    boolean add(T element);

    /**
     * Удаление элемента
     * @param element удаляемый элемент
     * @return true если элемент был найден и удален
     */
    boolean remove(T element);

    /**
     * Проверка наличия элемента
     * @param element проверяемый элемент
     * @return true если элемент присутствует
     */
    boolean contains(T element);

    /**
     * Получение размера структуры
     * @return количество элементов
     */
    int size();

    /**
     * Проверка на пустоту
     * @return true если структура пуста
     */
    boolean isEmpty();
}
```

### Временная сложность основных операций

| Структура данных | Вставка | Удаление | Поиск | Доступ по индексу |
|------------------|---------|----------|-------|-------------------|
| Массив | O(n) | O(n) | O(n) | O(1) |
| Связный список | O(1) | O(1)* | O(n) | O(n) |
| Хеш-таблица | O(1) | O(1) | O(1) | O(1) |
| Бинарное дерево | O(log n) | O(log n) | O(log n) | - |
| AVL дерево | O(log n) | O(log n) | O(log n) | - |

## Линейные структуры данных

Линейные структуры данных хранят элементы в последовательном порядке.

### Массивы (Arrays)

Массивы - самая простая структура данных с фиксированным размером и произвольным доступом.

```java
public class ArrayExample {

    /**
     * Пример работы с массивами в Java
     */
    public static void demonstrateArrays() {
        // Создание массива примитивных типов
        int[] numbers = new int[5];
        numbers[0] = 10;
        numbers[1] = 20;
        numbers[2] = 30;
        numbers[3] = 40;
        numbers[4] = 50;

        // Создание массива объектов
        String[] names = {"Alice", "Bob", "Charlie", "Diana"};

        // Многомерные массивы
        int[][] matrix = {
            {1, 2, 3},
            {4, 5, 6},
            {7, 8, 9}
        };

        // Итерация по массиву
        System.out.println("Элементы массива:");
        for (int i = 0; i < numbers.length; i++) {
            System.out.println("numbers[" + i + "] = " + numbers[i]);
        }

        // Использование Arrays utility class
        int[] copy = Arrays.copyOf(numbers, numbers.length);
        Arrays.sort(copy);
        int index = Arrays.binarySearch(copy, 30);

        System.out.println("Отсортированная копия: " + Arrays.toString(copy));
        System.out.println("Индекс числа 30: " + index);
    }

    /**
     * Динамический массив на базе ArrayList
     */
    public static void demonstrateArrayList() {
        List<String> list = new ArrayList<>();

        // Добавление элементов
        list.add("Первый");
        list.add("Второй");
        list.add("Третий");

        // Вставка по индексу
        list.add(1, "Второй с половиной");

        // Удаление
        list.remove("Второй");

        // Поиск
        int index = list.indexOf("Третий");

        System.out.println("Список: " + list);
        System.out.println("Размер: " + list.size());
        System.out.println("Индекс 'Третий': " + index);
    }
}
```

#### Преимущества и недостатки массивов
- **Преимущества**: Быстрый доступ по индексу, простота использования
- **Недостатки**: Фиксированный размер, дорогие операции вставки/удаления

### Связные списки (Linked Lists)

Связные списки - динамические структуры, где каждый элемент содержит ссылку на следующий.

```java
public class LinkedListExample {

    /**
     * Узел связного списка
     */
    static class Node<T> {
        T data;           // Данные узла
        Node<T> next;     // Ссылка на следующий узел

        Node(T data) {
            this.data = data;
            this.next = null;
        }
    }

    /**
     * Простая реализация односвязного списка
     */
    static class SimpleLinkedList<T> {
        private Node<T> head;  // Голова списка
        private int size;      // Размер списка

        /**
         * Добавление элемента в начало списка
         */
        public void addFirst(T data) {
            Node<T> newNode = new Node<>(data);
            newNode.next = head;  // Новый узел указывает на старую голову
            head = newNode;       // Новая голова - новый узел
            size++;
        }

        /**
         * Добавление элемента в конец списка
         */
        public void addLast(T data) {
            Node<T> newNode = new Node<>(data);

            if (head == null) {
                head = newNode;  // Если список пуст, новый узел становится головой
                size++;
                return;
            }

            // Находим последний узел
            Node<T> current = head;
            while (current.next != null) {
                current = current.next;
            }

            current.next = newNode;  // Присоединяем новый узел
            size++;
        }

        /**
         * Удаление первого элемента
         */
        public T removeFirst() {
            if (head == null) {
                throw new NoSuchElementException("Список пуст");
            }

            T data = head.data;    // Сохраняем данные
            head = head.next;      // Новая голова - следующий узел
            size--;

            return data;
        }

        /**
         * Поиск элемента по значению
         */
        public boolean contains(T data) {
            Node<T> current = head;

            while (current != null) {
                if (Objects.equals(current.data, data)) {
                    return true;
                }
                current = current.next;
            }

            return false;
        }

        /**
         * Вывод списка
         */
        public void print() {
            Node<T> current = head;
            System.out.print("Связный список: ");

            while (current != null) {
                System.out.print(current.data + " -> ");
                current = current.next;
            }
            System.out.println("null");
        }
    }

    /**
     * Демонстрация работы связного списка
     */
    public static void demonstrateLinkedList() {
        SimpleLinkedList<String> list = new SimpleLinkedList<>();

        // Добавление элементов
        list.addFirst("Третий");
        list.addFirst("Второй");
        list.addFirst("Первый");

        list.addLast("Четвертый");

        System.out.println("После добавления:");
        list.print();

        // Удаление элемента
        String removed = list.removeFirst();
        System.out.println("Удален: " + removed);

        System.out.println("После удаления:");
        list.print();

        // Поиск
        System.out.println("Содержит 'Третий': " + list.contains("Третий"));
        System.out.println("Содержит 'Пятый': " + list.contains("Пятый"));
    }
}
```

#### Виды связных списков
- **Односвязный список**: Каждый узел содержит ссылку только на следующий элемент
- **Двусвязный список**: Каждый узел содержит ссылки на предыдущий и следующий элементы
- **Кольцевой список**: Последний элемент ссылается на первый

### Стеки (Stacks)

Стек - структура данных LIFO (Last In, First Out).

```java
public class StackExample {

    /**
     * Простая реализация стека на базе массива
     */
    static class ArrayStack<T> {
        private Object[] elements;  // Массив элементов
        private int size;           // Текущий размер
        private static final int DEFAULT_CAPACITY = 10;

        public ArrayStack() {
            elements = new Object[DEFAULT_CAPACITY];
            size = 0;
        }

        /**
         * Добавление элемента на вершину стека (push)
         */
        public void push(T element) {
            ensureCapacity();           // Убеждаемся, что есть место
            elements[size++] = element; // Добавляем элемент и увеличиваем размер
        }

        /**
         * Извлечение элемента с вершины стека (pop)
         */
        @SuppressWarnings("unchecked")
        public T pop() {
            if (isEmpty()) {
                throw new EmptyStackException();
            }

            T element = (T) elements[--size];  // Уменьшаем размер и получаем элемент
            elements[size] = null;             // Очищаем ссылку для GC

            return element;
        }

        /**
         * Просмотр верхнего элемента без извлечения (peek)
         */
        @SuppressWarnings("unchecked")
        public T peek() {
            if (isEmpty()) {
                throw new EmptyStackException();
            }

            return (T) elements[size - 1];
        }

        /**
         * Проверка на пустоту
         */
        public boolean isEmpty() {
            return size == 0;
        }

        /**
         * Получение размера стека
         */
        public int size() {
            return size;
        }

        /**
         * Увеличение емкости массива при необходимости
         */
        private void ensureCapacity() {
            if (size == elements.length) {
                int newCapacity = elements.length * 2;
                elements = Arrays.copyOf(elements, newCapacity);
            }
        }
    }

    /**
     * Применение стека: проверка сбалансированности скобок
     */
    public static boolean isBalanced(String expression) {
        ArrayStack<Character> stack = new ArrayStack<>();

        for (char ch : expression.toCharArray()) {
            switch (ch) {
                case '(':
                case '[':
                case '{':
                    stack.push(ch);  // Открывающая скобка - в стек
                    break;

                case ')':
                    if (stack.isEmpty() || stack.pop() != '(') {
                        return false;  // Несоответствующая закрывающая скобка
                    }
                    break;

                case ']':
                    if (stack.isEmpty() || stack.pop() != '[') {
                        return false;
                    }
                    break;

                case '}':
                    if (stack.isEmpty() || stack.pop() != '{') {
                        return false;
                    }
                    break;
            }
        }

        return stack.isEmpty();  // Все скобки должны быть закрыты
    }

    /**
     * Демонстрация работы стека
     */
    public static void demonstrateStack() {
        ArrayStack<String> stack = new ArrayStack<>();

        // Добавление элементов
        stack.push("Первый");
        stack.push("Второй");
        stack.push("Третий");

        System.out.println("Размер стека: " + stack.size());
        System.out.println("Верхний элемент: " + stack.peek());

        // Извлечение элементов
        while (!stack.isEmpty()) {
            System.out.println("Извлечен: " + stack.pop());
        }

        // Проверка скобок
        System.out.println("'(a + b)' сбалансировано: " + isBalanced("(a + b)"));
        System.out.println("'(a + b]' сбалансировано: " + isBalanced("(a + b]"));
        System.out.println("'((a + b)' сбалансировано: " + isBalanced("((a + b)"));
    }
}
```

### Очереди (Queues)

Очередь - структура данных FIFO (First In, First Out).

```java
public class QueueExample {

    /**
     * Простая реализация очереди на базе связного списка
     */
    static class LinkedQueue<T> {
        private Node<T> front;  // Голова очереди (для извлечения)
        private Node<T> rear;   // Хвост очереди (для добавления)
        private int size;

        private static class Node<T> {
            T data;
            Node<T> next;

            Node(T data) {
                this.data = data;
                this.next = null;
            }
        }

        /**
         * Добавление элемента в конец очереди (enqueue)
         */
        public void enqueue(T element) {
            Node<T> newNode = new Node<>(element);

            if (isEmpty()) {
                front = rear = newNode;  // Первый элемент
            } else {
                rear.next = newNode;     // Добавляем в конец
                rear = newNode;          // Обновляем хвост
            }

            size++;
        }

        /**
         * Извлечение элемента из начала очереди (dequeue)
         */
        public T dequeue() {
            if (isEmpty()) {
                throw new NoSuchElementException("Очередь пуста");
            }

            T data = front.data;     // Сохраняем данные
            front = front.next;      // Сдвигаем голову

            if (front == null) {
                rear = null;         // Очередь опустела
            }

            size--;
            return data;
        }

        /**
         * Просмотр первого элемента без извлечения (peek)
         */
        public T peek() {
            if (isEmpty()) {
                throw new NoSuchElementException("Очередь пуста");
            }

            return front.data;
        }

        public boolean isEmpty() {
            return front == null;
        }

        public int size() {
            return size;
        }
    }

    /**
     * Применение очереди: симуляция очереди печати
     */
    static class PrintQueue {
        private LinkedQueue<String> queue = new LinkedQueue<>();

        public void addDocument(String document) {
            queue.enqueue(document);
            System.out.println("Добавлен документ: " + document);
        }

        public void printNext() {
            if (!queue.isEmpty()) {
                String document = queue.dequeue();
                System.out.println("Печатается: " + document);
            } else {
                System.out.println("Очередь печати пуста");
            }
        }

        public void showQueue() {
            System.out.println("Документы в очереди: " + queue.size());
        }
    }

    /**
     * Демонстрация работы очереди
     */
    public static void demonstrateQueue() {
        LinkedQueue<String> queue = new LinkedQueue<>();

        // Добавление элементов
        queue.enqueue("Первый");
        queue.enqueue("Второй");
        queue.enqueue("Третий");

        System.out.println("Размер очереди: " + queue.size());
        System.out.println("Первый элемент: " + queue.peek());

        // Извлечение элементов
        while (!queue.isEmpty()) {
            System.out.println("Обработан: " + queue.dequeue());
        }

        // Симуляция очереди печати
        PrintQueue printQueue = new PrintQueue();
        printQueue.addDocument("Отчет.pdf");
        printQueue.addDocument("Презентация.pptx");
        printQueue.addDocument("Код.java");

        printQueue.printNext();
        printQueue.printNext();
        printQueue.showQueue();
    }
}
```

## Ассоциативные структуры

Ассоциативные структуры позволяют хранить пары ключ-значение.

### Хеш-таблицы (Hash Tables)

Хеш-таблицы обеспечивают быстрый доступ к элементам по ключу.

```java
public class HashTableExample {

    /**
     * Простая реализация хеш-таблицы
     */
    static class SimpleHashTable<K, V> {
        private static final int DEFAULT_CAPACITY = 16;
        private static final float LOAD_FACTOR = 0.75f;

        private Entry<K, V>[] table;  // Массив bucket'ов
        private int size;             // Количество элементов
        private int threshold;        // Порог для расширения

        private static class Entry<K, V> {
            K key;
            V value;
            Entry<K, V> next;  // Для разрешения коллизий

            Entry(K key, V value, Entry<K, V> next) {
                this.key = key;
                this.value = value;
                this.next = next;
            }
        }

        @SuppressWarnings("unchecked")
        public SimpleHashTable() {
            table = new Entry[DEFAULT_CAPACITY];
            threshold = (int) (DEFAULT_CAPACITY * LOAD_FACTOR);
        }

        /**
         * Вычисление хеш-кода для ключа
         */
        private int hash(K key) {
            return key == null ? 0 : Math.abs(key.hashCode()) % table.length;
        }

        /**
         * Добавление пары ключ-значение
         */
        public void put(K key, V value) {
            int hash = hash(key);
            Entry<K, V> entry = table[hash];

            // Проверяем, существует ли уже такой ключ
            while (entry != null) {
                if (Objects.equals(entry.key, key)) {
                    entry.value = value;  // Обновляем значение
                    return;
                }
                entry = entry.next;
            }

            // Добавляем новую запись в начало цепочки
            table[hash] = new Entry<>(key, value, table[hash]);
            size++;

            // Проверяем необходимость расширения
            if (size > threshold) {
                resize();
            }
        }

        /**
         * Получение значения по ключу
         */
        public V get(K key) {
            int hash = hash(key);
            Entry<K, V> entry = table[hash];

            while (entry != null) {
                if (Objects.equals(entry.key, key)) {
                    return entry.value;
                }
                entry = entry.next;
            }

            return null;
        }

        /**
         * Удаление элемента по ключу
         */
        public V remove(K key) {
            int hash = hash(key);
            Entry<K, V> entry = table[hash];
            Entry<K, V> prev = null;

            while (entry != null) {
                if (Objects.equals(entry.key, key)) {
                    if (prev == null) {
                        table[hash] = entry.next;  // Удаляем первый элемент цепочки
                    } else {
                        prev.next = entry.next;    // Удаляем из середины цепочки
                    }
                    size--;
                    return entry.value;
                }
                prev = entry;
                entry = entry.next;
            }

            return null;
        }

        /**
         * Расширение хеш-таблицы
         */
        @SuppressWarnings("unchecked")
        private void resize() {
            Entry<K, V>[] oldTable = table;
            table = new Entry[table.length * 2];
            threshold = (int) (table.length * LOAD_FACTOR);
            size = 0;

            // Перехешируем все элементы
            for (Entry<K, V> entry : oldTable) {
                while (entry != null) {
                    put(entry.key, entry.value);
                    entry = entry.next;
                }
            }
        }

        public int size() {
            return size;
        }
    }

    /**
     * Демонстрация работы хеш-таблицы
     */
    public static void demonstrateHashTable() {
        SimpleHashTable<String, Integer> table = new SimpleHashTable<>();

        // Добавление элементов
        table.put("Alice", 25);
        table.put("Bob", 30);
        table.put("Charlie", 35);

        System.out.println("Alice: " + table.get("Alice"));
        System.out.println("Bob: " + table.get("Bob"));

        // Обновление значения
        table.put("Alice", 26);
        System.out.println("Alice после обновления: " + table.get("Alice"));

        // Удаление
        table.remove("Bob");
        System.out.println("Bob после удаления: " + table.get("Bob"));

        System.out.println("Размер таблицы: " + table.size());
    }

    /**
     * Использование стандартной HashMap
     */
    public static void demonstrateHashMap() {
        Map<String, Person> people = new HashMap<>();

        people.put("alice", new Person("Alice", 25));
        people.put("bob", new Person("Bob", 30));

        // Итерация по элементам
        for (Map.Entry<String, Person> entry : people.entrySet()) {
            System.out.println(entry.getKey() + ": " + entry.getValue());
        }

        // Использование computeIfAbsent
        people.computeIfAbsent("charlie", k -> new Person("Charlie", 35));

        // Использование merge для обновления
        people.merge("alice", new Person("Alice", 26),
            (old, newVal) -> new Person(old.name, newVal.age));
    }

    static class Person {
        String name;
        int age;

        Person(String name, int age) {
            this.name = name;
            this.age = age;
        }

        @Override
        public String toString() {
            return name + " (" + age + ")";
        }
    }
}
```

### Деревья поиска (Search Trees)

Деревья поиска поддерживают упорядоченность элементов.

```java
public class BinarySearchTreeExample {

    /**
     * Узел бинарного дерева поиска
     */
    static class TreeNode<T extends Comparable<T>> {
        T value;
        TreeNode<T> left;
        TreeNode<T> right;

        TreeNode(T value) {
            this.value = value;
            this.left = null;
            this.right = null;
        }
    }

    /**
     * Бинарное дерево поиска
     */
    static class BinarySearchTree<T extends Comparable<T>> {
        private TreeNode<T> root;

        /**
         * Вставка элемента
         */
        public void insert(T value) {
            root = insertRec(root, value);
        }

        private TreeNode<T> insertRec(TreeNode<T> node, T value) {
            if (node == null) {
                return new TreeNode<>(value);
            }

            if (value.compareTo(node.value) < 0) {
                node.left = insertRec(node.left, value);
            } else if (value.compareTo(node.value) > 0) {
                node.right = insertRec(node.right, value);
            }

            return node;
        }

        /**
         * Поиск элемента
         */
        public boolean contains(T value) {
            return containsRec(root, value);
        }

        private boolean containsRec(TreeNode<T> node, T value) {
            if (node == null) {
                return false;
            }

            if (value.compareTo(node.value) == 0) {
                return true;
            }

            return value.compareTo(node.value) < 0
                ? containsRec(node.left, value)
                : containsRec(node.right, value);
        }

        /**
         * Удаление элемента
         */
        public void delete(T value) {
            root = deleteRec(root, value);
        }

        private TreeNode<T> deleteRec(TreeNode<T> node, T value) {
            if (node == null) {
                return null;
            }

            if (value.compareTo(node.value) < 0) {
                node.left = deleteRec(node.left, value);
            } else if (value.compareTo(node.value) > 0) {
                node.right = deleteRec(node.right, value);
            } else {
                // Узел найден
                if (node.left == null) {
                    return node.right;
                } else if (node.right == null) {
                    return node.left;
                }

                // Узел имеет двух потомков
                node.value = findMin(node.right);
                node.right = deleteRec(node.right, node.value);
            }

            return node;
        }

        private T findMin(TreeNode<T> node) {
            while (node.left != null) {
                node = node.left;
            }
            return node.value;
        }

        /**
         * Обход дерева в порядке возрастания (in-order)
         */
        public void inOrderTraversal() {
            inOrderRec(root);
            System.out.println();
        }

        private void inOrderRec(TreeNode<T> node) {
            if (node != null) {
                inOrderRec(node.left);
                System.out.print(node.value + " ");
                inOrderRec(node.right);
            }
        }

        /**
         * Получение высоты дерева
         */
        public int getHeight() {
            return getHeightRec(root);
        }

        private int getHeightRec(TreeNode<T> node) {
            if (node == null) {
                return 0;
            }

            return 1 + Math.max(getHeightRec(node.left), getHeightRec(node.right));
        }
    }

    /**
     * Демонстрация работы бинарного дерева поиска
     */
    public static void demonstrateBST() {
        BinarySearchTree<Integer> bst = new BinarySearchTree<>();

        // Вставка элементов
        int[] values = {50, 30, 70, 20, 40, 60, 80};
        for (int value : values) {
            bst.insert(value);
        }

        System.out.println("Обход дерева (in-order):");
        bst.inOrderTraversal();

        System.out.println("Высота дерева: " + bst.getHeight());

        // Поиск элементов
        System.out.println("Содержит 40: " + bst.contains(40));
        System.out.println("Содержит 25: " + bst.contains(25));

        // Удаление элементов
        bst.delete(30);
        System.out.println("После удаления 30:");
        bst.inOrderTraversal();
    }
}
```

## Древовидные структуры

### Бинарные деревья (Binary Trees)

Бинарные деревья - фундаментальная древовидная структура данных.

```java
public class BinaryTreeExample {

    /**
     * Узел бинарного дерева
     */
    static class TreeNode {
        int value;
        TreeNode left;
        TreeNode right;

        TreeNode(int value) {
            this.value = value;
            this.left = null;
            this.right = null;
        }
    }

    /**
     * Различные виды обхода бинарного дерева
     */
    static class TreeTraversals {

        /**
         * Прямой обход (pre-order): корень -> левый -> правый
         */
        public static void preOrder(TreeNode node) {
            if (node == null) return;

            System.out.print(node.value + " ");  // Посещаем корень
            preOrder(node.left);                 // Обходим левое поддерево
            preOrder(node.right);                // Обходим правое поддерево
        }

        /**
         * Симметричный обход (in-order): левый -> корень -> правый
         */
        public static void inOrder(TreeNode node) {
            if (node == null) return;

            inOrder(node.left);                  // Обходим левое поддерево
            System.out.print(node.value + " ");  // Посещаем корень
            inOrder(node.right);                 // Обходим правое поддерево
        }

        /**
         * Обратный обход (post-order): левый -> правый -> корень
         */
        public static void postOrder(TreeNode node) {
            if (node == null) return;

            postOrder(node.left);                // Обходим левое поддерево
            postOrder(node.right);               // Обходим правое поддерево
            System.out.print(node.value + " ");  // Посещаем корень
        }

        /**
         * Обход в ширину (breadth-first) - уровень за уровнем
         */
        public static void levelOrder(TreeNode root) {
            if (root == null) return;

            Queue<TreeNode> queue = new LinkedList<>();
            queue.offer(root);

            while (!queue.isEmpty()) {
                TreeNode node = queue.poll();
                System.out.print(node.value + " ");

                if (node.left != null) queue.offer(node.left);
                if (node.right != null) queue.offer(node.right);
            }
        }
    }

    /**
     * Операции над бинарным деревом
     */
    static class BinaryTreeOperations {

        /**
         * Вычисление высоты дерева
         */
        public static int getHeight(TreeNode node) {
            if (node == null) {
                return 0;
            }

            int leftHeight = getHeight(node.left);
            int rightHeight = getHeight(node.right);

            return 1 + Math.max(leftHeight, rightHeight);
        }

        /**
         * Подсчет количества узлов
         */
        public static int countNodes(TreeNode node) {
            if (node == null) {
                return 0;
            }

            return 1 + countNodes(node.left) + countNodes(node.right);
        }

        /**
         * Проверка сбалансированности дерева
         */
        public static boolean isBalanced(TreeNode node) {
            return checkBalance(node) != -1;
        }

        private static int checkBalance(TreeNode node) {
            if (node == null) {
                return 0;
            }

            int leftHeight = checkBalance(node.left);
            if (leftHeight == -1) return -1;

            int rightHeight = checkBalance(node.right);
            if (rightHeight == -1) return -1;

            if (Math.abs(leftHeight - rightHeight) > 1) {
                return -1; // Несбалансировано
            }

            return 1 + Math.max(leftHeight, rightHeight);
        }

        /**
         * Поиск наименьшего общего предка (LCA)
         */
        public static TreeNode findLCA(TreeNode root, int n1, int n2) {
            if (root == null) {
                return null;
            }

            // Если оба значения меньше корня, LCA в левом поддереве
            if (root.value > n1 && root.value > n2) {
                return findLCA(root.left, n1, n2);
            }

            // Если оба значения больше корня, LCA в правом поддереве
            if (root.value < n1 && root.value < n2) {
                return findLCA(root.right, n1, n2);
            }

            // В противном случае, текущий узел - LCA
            return root;
        }
    }

    /**
     * Создание примера бинарного дерева
     */
    public static TreeNode createSampleTree() {
        TreeNode root = new TreeNode(1);
        root.left = new TreeNode(2);
        root.right = new TreeNode(3);
        root.left.left = new TreeNode(4);
        root.left.right = new TreeNode(5);
        root.right.left = new TreeNode(6);
        root.right.right = new TreeNode(7);

        return root;
    }

    /**
     * Демонстрация различных видов обхода
     */
    public static void demonstrateTraversals() {
        TreeNode root = createSampleTree();

        System.out.println("Дерево:");
        System.out.println("    1");
        System.out.println("   / \\");
        System.out.println("  2   3");
        System.out.println(" / \\ / \\");
        System.out.println("4  5 6  7");

        System.out.print("\nPre-order: ");
        TreeTraversals.preOrder(root);

        System.out.print("\nIn-order: ");
        TreeTraversals.inOrder(root);

        System.out.print("\nPost-order: ");
        TreeTraversals.postOrder(root);

        System.out.print("\nLevel-order: ");
        TreeTraversals.levelOrder(root);

        System.out.println("\n\nВысота дерева: " + BinaryTreeOperations.getHeight(root));
        System.out.println("Количество узлов: " + BinaryTreeOperations.countNodes(root));
        System.out.println("Сбалансировано: " + BinaryTreeOperations.isBalanced(root));
    }
}
```

### AVL деревья

AVL деревья - самобалансирующиеся бинарные деревья поиска.

```java
public class AVLTreeExample {

    /**
     * Узел AVL дерева
     */
    static class AVLNode {
        int value;
        int height;  // Высота поддерева
        AVLNode left;
        AVLNode right;

        AVLNode(int value) {
            this.value = value;
            this.height = 1;  // Высота листа = 1
        }
    }

    /**
     * AVL дерево - самобалансирующееся бинарное дерево поиска
     */
    static class AVLTree {
        private AVLNode root;

        /**
         * Получение высоты узла
         */
        private int height(AVLNode node) {
            return node == null ? 0 : node.height;
        }

        /**
         * Вычисление баланса узла
         */
        private int getBalance(AVLNode node) {
            return node == null ? 0 : height(node.left) - height(node.right);
        }

        /**
         * Правый поворот
         */
        private AVLNode rightRotate(AVLNode y) {
            AVLNode x = y.left;
            AVLNode T2 = x.right;

            // Поворот
            x.right = y;
            y.left = T2;

            // Обновление высот
            y.height = Math.max(height(y.left), height(y.right)) + 1;
            x.height = Math.max(height(x.left), height(x.right)) + 1;

            return x;
        }

        /**
         * Левый поворот
         */
        private AVLNode leftRotate(AVLNode x) {
            AVLNode y = x.right;
            AVLNode T2 = y.left;

            // Поворот
            y.left = x;
            x.right = T2;

            // Обновление высот
            x.height = Math.max(height(x.left), height(x.right)) + 1;
            y.height = Math.max(height(y.left), height(y.right)) + 1;

            return y;
        }

        /**
         * Вставка элемента с балансировкой
         */
        public void insert(int value) {
            root = insertRec(root, value);
        }

        private AVLNode insertRec(AVLNode node, int value) {
            // Стандартная вставка в BST
            if (node == null) {
                return new AVLNode(value);
            }

            if (value < node.value) {
                node.left = insertRec(node.left, value);
            } else if (value > node.value) {
                node.right = insertRec(node.right, value);
            } else {
                return node; // Дубликаты не разрешены
            }

            // Обновление высоты
            node.height = 1 + Math.max(height(node.left), height(node.right));

            // Проверка баланса
            int balance = getBalance(node);

            // Левый-левый случай
            if (balance > 1 && value < node.left.value) {
                return rightRotate(node);
            }

            // Правый-правый случай
            if (balance < -1 && value > node.right.value) {
                return leftRotate(node);
            }

            // Левый-правый случай
            if (balance > 1 && value > node.left.value) {
                node.left = leftRotate(node.left);
                return rightRotate(node);
            }

            // Правый-левый случай
            if (balance < -1 && value < node.right.value) {
                node.right = rightRotate(node.right);
                return leftRotate(node);
            }

            return node;
        }

        /**
         * Поиск элемента
         */
        public boolean contains(int value) {
            return containsRec(root, value);
        }

        private boolean containsRec(AVLNode node, int value) {
            if (node == null) {
                return false;
            }

            if (value == node.value) {
                return true;
            }

            return value < node.value
                ? containsRec(node.left, value)
                : containsRec(node.right, value);
        }

        /**
         * Обход дерева (in-order)
         */
        public void inOrder() {
            inOrderRec(root);
            System.out.println();
        }

        private void inOrderRec(AVLNode node) {
            if (node != null) {
                inOrderRec(node.left);
                System.out.print(node.value + " ");
                inOrderRec(node.right);
            }
        }
    }

    /**
     * Демонстрация работы AVL дерева
     */
    public static void demonstrateAVL() {
        AVLTree tree = new AVLTree();

        // Вставка элементов (в худшем порядке для несбалансированного дерева)
        int[] values = {10, 20, 30, 40, 50, 25};

        System.out.println("Вставка элементов:");
        for (int value : values) {
            tree.insert(value);
            System.out.println("Вставлен " + value);
        }

        System.out.println("\nОбход дерева (in-order):");
        tree.inOrder();

        System.out.println("\nПоиск элементов:");
        System.out.println("Содержит 30: " + tree.contains(30));
        System.out.println("Содержит 15: " + tree.contains(15));
    }
}
```

## Графовые структуры

### Представление графов

Графы могут быть представлены различными способами.

```java
public class GraphExample {

    /**
     * Матрица смежности - для плотных графов
     */
    static class AdjacencyMatrix {
        private boolean[][] matrix;
        private int vertices;

        public AdjacencyMatrix(int vertices) {
            this.vertices = vertices;
            matrix = new boolean[vertices][vertices];
        }

        /**
         * Добавление ребра
         */
        public void addEdge(int source, int destination) {
            if (source >= 0 && source < vertices && destination >= 0 && destination < vertices) {
                matrix[source][destination] = true;
                matrix[destination][source] = true; // Для неориентированного графа
            }
        }

        /**
         * Проверка наличия ребра
         */
        public boolean hasEdge(int source, int destination) {
            return source >= 0 && source < vertices &&
                   destination >= 0 && destination < vertices &&
                   matrix[source][destination];
        }

        /**
         * Получение соседей вершины
         */
        public List<Integer> getNeighbors(int vertex) {
            List<Integer> neighbors = new ArrayList<>();
            for (int i = 0; i < vertices; i++) {
                if (matrix[vertex][i]) {
                    neighbors.add(i);
                }
            }
            return neighbors;
        }

        /**
         * Вывод матрицы
         */
        public void printMatrix() {
            System.out.println("Матрица смежности:");
            for (int i = 0; i < vertices; i++) {
                for (int j = 0; j < vertices; j++) {
                    System.out.print((matrix[i][j] ? 1 : 0) + " ");
                }
                System.out.println();
            }
        }
    }

    /**
     * Список смежности - для разреженных графов
     */
    static class AdjacencyList {
        private List<List<Integer>> adjacencyList;
        private int vertices;

        public AdjacencyList(int vertices) {
            this.vertices = vertices;
            adjacencyList = new ArrayList<>(vertices);
            for (int i = 0; i < vertices; i++) {
                adjacencyList.add(new ArrayList<>());
            }
        }

        /**
         * Добавление ребра
         */
        public void addEdge(int source, int destination) {
            if (source >= 0 && source < vertices && destination >= 0 && destination < vertices) {
                adjacencyList.get(source).add(destination);
                adjacencyList.get(destination).add(source); // Для неориентированного графа
            }
        }

        /**
         * Получение соседей вершины
         */
        public List<Integer> getNeighbors(int vertex) {
            return adjacencyList.get(vertex);
        }

        /**
         * Вывод списка смежности
         */
        public void printList() {
            System.out.println("Список смежности:");
            for (int i = 0; i < vertices; i++) {
                System.out.print(i + ": ");
                for (int neighbor : adjacencyList.get(i)) {
                    System.out.print(neighbor + " ");
                }
                System.out.println();
            }
        }
    }

    /**
     * Демонстрация различных представлений графов
     */
    public static void demonstrateGraphs() {
        System.out.println("=== Матрица смежности ===");
        AdjacencyMatrix matrix = new AdjacencyMatrix(5);

        matrix.addEdge(0, 1);
        matrix.addEdge(0, 4);
        matrix.addEdge(1, 2);
        matrix.addEdge(1, 3);
        matrix.addEdge(1, 4);
        matrix.addEdge(2, 3);
        matrix.addEdge(3, 4);

        matrix.printMatrix();

        System.out.println("\nСоседи вершины 1: " + matrix.getNeighbors(1));

        System.out.println("\n=== Список смежности ===");
        AdjacencyList list = new AdjacencyList(5);

        list.addEdge(0, 1);
        list.addEdge(0, 4);
        list.addEdge(1, 2);
        list.addEdge(1, 3);
        list.addEdge(1, 4);
        list.addEdge(2, 3);
        list.addEdge(3, 4);

        list.printList();

        System.out.println("Соседи вершины 1: " + list.getNeighbors(1));
    }
}
```

### Обход графов

```java
public class GraphTraversal {

    /**
     * Поиск в ширину (BFS)
     */
    public static void bfs(AdjacencyList graph, int startVertex) {
        boolean[] visited = new boolean[graph.getVertices()];
        Queue<Integer> queue = new LinkedList<>();

        visited[startVertex] = true;
        queue.offer(startVertex);

        System.out.print("BFS от вершины " + startVertex + ": ");

        while (!queue.isEmpty()) {
            int vertex = queue.poll();
            System.out.print(vertex + " ");

            for (int neighbor : graph.getNeighbors(vertex)) {
                if (!visited[neighbor]) {
                    visited[neighbor] = true;
                    queue.offer(neighbor);
                }
            }
        }

        System.out.println();
    }

    /**
     * Поиск в глубину (DFS)
     */
    public static void dfs(AdjacencyList graph, int startVertex) {
        boolean[] visited = new boolean[graph.getVertices()];

        System.out.print("DFS от вершины " + startVertex + ": ");
        dfsRecursive(graph, startVertex, visited);
        System.out.println();
    }

    private static void dfsRecursive(AdjacencyList graph, int vertex, boolean[] visited) {
        visited[vertex] = true;
        System.out.print(vertex + " ");

        for (int neighbor : graph.getNeighbors(vertex)) {
            if (!visited[neighbor]) {
                dfsRecursive(graph, neighbor, visited);
            }
        }
    }

    /**
     * Итеративный DFS с использованием стека
     */
    public static void dfsIterative(AdjacencyList graph, int startVertex) {
        boolean[] visited = new boolean[graph.getVertices()];
        Stack<Integer> stack = new Stack<>();

        stack.push(startVertex);

        System.out.print("DFS (итеративный) от вершины " + startVertex + ": ");

        while (!stack.isEmpty()) {
            int vertex = stack.pop();

            if (!visited[vertex]) {
                visited[vertex] = true;
                System.out.print(vertex + " ");

                // Добавляем соседей в обратном порядке для правильного обхода
                List<Integer> neighbors = new ArrayList<>(graph.getNeighbors(vertex));
                Collections.reverse(neighbors);

                for (int neighbor : neighbors) {
                    if (!visited[neighbor]) {
                        stack.push(neighbor);
                    }
                }
            }
        }

        System.out.println();
    }

    /**
     * Демонстрация обхода графов
     */
    public static void demonstrateTraversal() {
        AdjacencyList graph = new AdjacencyList(6);

        // Создаем связный граф
        graph.addEdge(0, 1);
        graph.addEdge(0, 2);
        graph.addEdge(1, 3);
        graph.addEdge(1, 4);
        graph.addEdge(2, 4);
        graph.addEdge(3, 4);
        graph.addEdge(3, 5);
        graph.addEdge(4, 5);

        System.out.println("Граф:");
        graph.printList();

        System.out.println();
        bfs(graph, 0);
        dfs(graph, 0);
        dfsIterative(graph, 0);
    }
}
```

## Выбор структуры данных

### Критерии выбора

| Требование | Рекомендуемая структура |
|------------|-------------------------|
| Быстрый доступ по индексу | Массив |
| Частые вставки/удаления в начале | Связный список |
| LIFO операции | Стек |
| FIFO операции | Очередь |
| Быстрый поиск | Хеш-таблица |
| Упорядоченные данные | Бинарное дерево поиска |
| Самобалансировка | AVL дерево, Красно-черное дерево |
| Сложные связи | Граф |

### Производительность по операциям

```java
/**
 * Сравнение производительности структур данных
 */
public class PerformanceComparison {

    /**
     * Тестирование производительности различных структур
     */
    public static void comparePerformance() {
        int operations = 100000;

        // Тестирование ArrayList
        List<Integer> arrayList = new ArrayList<>();
        long startTime = System.nanoTime();
        for (int i = 0; i < operations; i++) {
            arrayList.add(i);
        }
        long arrayListTime = System.nanoTime() - startTime;

        // Тестирование LinkedList
        List<Integer> linkedList = new LinkedList<>();
        startTime = System.nanoTime();
        for (int i = 0; i < operations; i++) {
            linkedList.add(i);
        }
        long linkedListTime = System.nanoTime() - startTime;

        // Тестирование HashSet
        Set<Integer> hashSet = new HashSet<>();
        startTime = System.nanoTime();
        for (int i = 0; i < operations; i++) {
            hashSet.add(i);
        }
        long hashSetTime = System.nanoTime() - startTime;

        System.out.println("Производительность вставки (" + operations + " операций):");
        System.out.println("ArrayList: " + arrayListTime / 1_000_000 + " ms");
        System.out.println("LinkedList: " + linkedListTime / 1_000_000 + " ms");
        System.out.println("HashSet: " + hashSetTime / 1_000_000 + " ms");
    }

    /**
     * Анализ выбора структуры данных
     */
    public static void analyzeChoice() {
        System.out.println("\nАнализ выбора структуры данных:");
        System.out.println("1. Для частого доступа по индексу - ArrayList");
        System.out.println("2. Для частых вставок/удалений в начале - LinkedList");
        System.out.println("3. Для быстрого поиска без порядка - HashSet");
        System.out.println("4. Для упорядоченных данных - TreeSet");
        System.out.println("5. Для пар ключ-значение - HashMap");
        System.out.println("6. Для кэширования с порядком - LinkedHashMap");
    }
}
```

### Заключение

Выбор правильной структуры данных критически важен для эффективности программ. Каждая структура имеет свои преимущества и недостатки, и правильный выбор зависит от специфики задачи:

- **Массивы**: Простота, быстрый доступ, но фиксированный размер
- **Связные списки**: Динамический размер, эффективные вставки/удаления, но медленный поиск
- **Хеш-таблицы**: Быстрый поиск, вставка, удаление в среднем O(1)
- **Деревья**: Упорядоченность, логарифмическая сложность операций
- **Графы**: Моделирование сложных связей и отношений

### Рекомендации

1. **Анализируйте требования**: Какие операции будут выполняться чаще всего?
2. **Учитывайте ограничения**: Размер данных, доступная память
3. **Тестируйте производительность**: Замеряйте время выполнения на реальных данных
4. **Комбинируйте структуры**: Часто оптимальное решение использует несколько структур
5. **Изучайте стандартные реализации**: Java Collections Framework предоставляет отличные реализации

Правильный выбор структуры данных может ускорить программу в десятки и сотни раз!
