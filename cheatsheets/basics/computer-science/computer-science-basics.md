---
title: "Основы Computer Science"
description: "Кратко: Базовые концепции computer science - от вычислительной сложности до архитектуры компьютеров и сетей с практическими примерами на Java."
tags:
  - basics
  - computer-science
  - computer-science-basics
difficulty: "intermediate"
prerequisites: []
next: []
updated: "2026-04-20"
---
# Основы Computer Science

Кратко: Базовые концепции **computer science** — от вычислительной сложности до архитектуры компьютеров и сетей с практическими примерами на **Java**.

## Полезные ссылки

### Официальная документация
- [Computer Science Field Guide](https://www.csfieldguide.org.nz/) — интерактивное руководство по **CS**

### Ресурсы
- [Introduction to Computer Science](https://www.baeldung.com/cs) — введение в **Computer Science**

### См. также
- [Базовые концепции программирования](../programming-basics/)
- [Компьютерные сети](../networks/)
- [Операционные системы](../operating-systems/)

- [[clean-code|Принципы чистого кода]]
- [[git-basics|Основы Git]]
- [[java-basics|Java: основы]]
## Содержание

- [Введение в Computer Science](#введение-в-computer-science)
  - [Основные области Computer Science](#основные-области-computer-science)
  - [Важность Computer Science](#важность-computer-science)
- [Вычислительная сложность](#вычислительная-сложность)
  - [Классы сложности P и NP](#классы-сложности-p-и-np)
  - [Теория информации](#теория-информации)
- [Алгоритмы и структуры данных](#алгоритмы-и-структуры-данных)
  - [Основные структуры данных](#основные-структуры-данных)
  - [Алгоритмы сортировки](#алгоритмы-сортировки)
- [Архитектура компьютеров](#архитектура-компьютеров)
  - [Основные компоненты компьютера](#основные-компоненты-компьютера)
  - [Иерархия памяти](#иерархия-памяти)
  - [Параллельные вычисления](#параллельные-вычисления)
- [Операционные системы](#операционные-системы)
  - [Процессы и потоки](#процессы-и-потоки)
- [Базы данных](#базы-данных)
  - [Реляционная модель](#реляционная-модель)
  - [Нормализация данных](#нормализация-данных)
- [Компьютерные сети](#компьютерные-сети)
  - [Модель OSI](#модель-osi)
  - [TCP/IP стек](#tcpip-стек)
- [Безопасность](#безопасность)
  - [Основы криптографии](#основы-криптографии)
- [Теория вычислений](#теория-вычислений)
  - [Машины Тьюринга](#машины-тьюринга)
  - [Теорема о неполноте Гёделя](#теорема-о-неполноте-гёделя)
- [Решение проблем](#решение-проблем)
- [Частые вопросы](#частые-вопросы)
- [Заключение](#заключение)
  - [Ключевые концепции](#ключевые-концепции)
  - [Важность изучения CS](#важность-изучения-cs)
  - [Рекомендации по изучению](#рекомендации-по-изучению)

## Введение в Computer Science

**Computer Science** — это дисциплина, изучающая принципы, методы и приложения вычислительных систем. Она охватывает широкий спектр тем от аппаратного обеспечения до программного обеспечения и теоретических основ.

### Основные области Computer Science

```java
/**
 * Перечисление основных областей Computer Science
 */
public enum ComputerScienceFields {

    /**
     * Теоретическая информатика - математические основы вычислений
     * Включает: теорию алгоритмов, вычислительную сложность, теорию автоматов
     */
    THEORETICAL_COMPUTER_SCIENCE,

    /**
     * Алгоритмы и структуры данных - проектирование эффективных алгоритмов
     * Включает: анализ сложности, оптимизация, структуры данных
     */
    ALGORITHMS_AND_DATA_STRUCTURES,

    /**
     * Архитектура компьютеров - аппаратное обеспечение и организация систем
     * Включает: процессоры, память, шины, параллельные вычисления
     */
    COMPUTER_ARCHITECTURE,

    /**
     * Операционные системы - управление ресурсами и абстракции
     * Включает: процессы, память, файловые системы, безопасность
     */
    OPERATING_SYSTEMS,

    /**
     * Компиляторы и языки программирования
     * Включает: лексический анализ, парсинг, оптимизация, виртуальные машины
     */
    PROGRAMMING_LANGUAGES_AND_COMPILERS,

    /**
     * Базы данных - хранение и обработка данных
     * Включает: реляционные БД, NoSQL, распределенные системы
     */
    DATABASES,

    /**
     * Компьютерные сети - коммуникации и распределенные системы
     * Включает: TCP/IP, HTTP, распределенные алгоритмы
     */
    COMPUTER_NETWORKS,

    /**
     * Искусственный интеллект и машинное обучение
     * Включает: нейронные сети, поиск, оптимизация
     */
    ARTIFICIAL_INTELLIGENCE,

    /**
     * Информационная безопасность - защита систем и данных
     * Включает: криптография, аутентификация, безопасность сетей
     */
    COMPUTER_SECURITY,

    /**
     * Человеко-компьютерное взаимодействие
     * Включает: пользовательские интерфейсы, UX/UI, доступность
     */
    HUMAN_COMPUTER_INTERACTION,

    /**
     * Графика и компьютерное зрение
     * Включает: рендеринг, обработка изображений, распознавание
     */
    COMPUTER_GRAPHICS_AND_VISION
}
```

### Важность Computer Science

```java
public class ComputerScienceImportance {

    /**
     * Computer Science формирует фундамент современной технологии
     */
    public void demonstrateImportance() {
        System.out.println("Computer Science - основа современной цивилизации:");
        System.out.println("• Интернет и веб-технологии");
        System.out.println("• Мобильные приложения и устройства");
        System.out.println("• Искусственный интеллект и машинное обучение");
        System.out.println("• Финансовые системы и криптовалюты");
        System.out.println("• Медицинские технологии и биоинформатика");
        System.out.println("• Автоматизация производства (Industry 4.0)");
        System.out.println("• Социальные сети и коммуникации");
        System.out.println("• Кибербезопасность и защита данных");
    }

    /**
     * Влияние на повседневную жизнь
     */
    public void everydayImpact() {
        System.out.println("Влияние CS на повседневную жизнь:");
        System.out.println("• GPS навигация");
        System.out.println("• Онлайн-банкинг");
        System.out.println("• Стриминговые сервисы");
        System.out.println("• Электронная коммерция");
        System.out.println("• Социальные сети");
        System.out.println("• Умные дома и IoT");
        System.out.println("• Автопилоты и автономные системы");
    }
}
```

## Вычислительная сложность

Вычислительная сложность — мера ресурсов, необходимых для решения вычислительной задачи.

### Классы сложности P и `NP`

```java
    /**
     * Демонстрация классов сложности P и NP
 */
public class ComplexityClasses {

    /**
     * Класс P (Polynomial time) - задачи, решаемые за полиномиальное время
     * Примеры: сортировка, поиск, простые арифметические операции
     */
    public void polynomialTimeProblems() {
        System.out.println("Класс P - задачи с полиномиальной сложностью:");

        // O(1) - константное время
        int constant = 42;

        // O(log n) - логарифмическое время
        int logTime = binarySearch(new int[]{1, 2, 3, 4, 5}, 3);

        // O(n) - линейное время
        int linear = sumArray(new int[]{1, 2, 3, 4, 5});

        // O(n log n) - квазилинейное время
        int[] sorted = new int[]{5, 3, 1, 4, 2};
        Arrays.sort(sorted);

        // O(n²) - квадратичное время
        boolean hasDuplicates = checkDuplicates(new int[]{1, 2, 3, 4, 5});
    }

    /**
     * Класс NP (Nondeterministic Polynomial time) - задачи,
     * решение которых можно проверить за полиномиальное время
     */
    public void npProblems() {
        System.out.println("Класс NP - задачи с проверкой за полиномиальное время:");

        // Задача коммивояжера (TSP) - NP-трудная
        // Задача о рюкзаке (Knapsack) - NP-полная
        // Задача о раскраске графа (Graph Coloring) - NP-полная
        // Задача о выполнимости булевых формул (SAT) - NP-полная

        // Проверка решения TSP за полиномиальное время - просто
        // Нахождение решения - NP-трудно
    }

    /**
     * P vs NP проблема - одна из важнейших открытых проблем в CS
     */
    public void pVsNp() {
        System.out.println("P vs NP проблема:");
        System.out.println("P ⊆ NP - доказано");
        System.out.println("P = NP? - неизвестно (гипотеза: P ≠ NP)");
        System.out.println("Если P = NP, то все NP задачи можно решить эффективно");
        System.out.println("Это повлияло бы на криптографию, оптимизацию, ИИ");
    }

    private int binarySearch(int[] array, int target) {
        int left = 0, right = array.length - 1;
        while (left <= right) {
            int mid = left + (right - left) / 2;
            if (array[mid] == target) return mid;
            if (array[mid] < target) left = mid + 1;
            else right = mid - 1;
        }
        return -1;
    }

    private int sumArray(int[] array) {
        int sum = 0;
        for (int num : array) sum += num;
        return sum;
    }

    private boolean checkDuplicates(int[] array) {
        for (int i = 0; i < array.length; i++) {
            for (int j = i + 1; j < array.length; j++) {
                if (array[i] == array[j]) return true;
            }
        }
        return false;
    }
}
```

### Теория информации

```java
    /**
     * Основы теории информации
 */
public class InformationTheory {

    /**
     * Энтропия Шеннона - мера неопределенности
     */
    public double shannonEntropy(double[] probabilities) {
        double entropy = 0.0;

        for (double p : probabilities) {
            if (p > 0) {
                entropy -= p * (Math.log(p) / Math.log(2)); // log base 2
            }
        }

        return entropy;
    }

    /**
     * Пример: энтропия монеты
     */
    public void coinEntropy() {
        // Честная монета: равновероятные исходы
        double[] fairCoin = {0.5, 0.5};
        double fairEntropy = shannonEntropy(fairCoin);
        System.out.println("Энтропия честной монеты: " + fairEntropy + " бит");

        // Не честная монета
        double[] biasedCoin = {0.9, 0.1};
        double biasedEntropy = shannonEntropy(biasedCoin);
        System.out.println("Энтропия нечестной монеты: " + biasedEntropy + " бит");
    }

    /**
     * Кодирование Хаффмана - оптимальное префиксное кодирование
     */
    public class HuffmanCoding {

        static class HuffmanNode implements Comparable<HuffmanNode> {
            char character;
            int frequency;
            HuffmanNode left, right;

            HuffmanNode(char character, int frequency) {
                this.character = character;
                this.frequency = frequency;
            }

            @Override
            public int compareTo(HuffmanNode other) {
                return this.frequency - other.frequency;
            }
        }

        public Map<Character, String> buildHuffmanCodes(String text) {
            // Подсчет частот символов
            Map<Character, Integer> frequencyMap = new HashMap<>();
            for (char c : text.toCharArray()) {
                frequencyMap.put(c, frequencyMap.getOrDefault(c, 0) + 1);
            }

            // Создание очереди с приоритетом
            PriorityQueue<HuffmanNode> pq = new PriorityQueue<>();
            for (Map.Entry<Character, Integer> entry : frequencyMap.entrySet()) {
                pq.add(new HuffmanNode(entry.getKey(), entry.getValue()));
            }

            // Построение дерева Хаффмана
            while (pq.size() > 1) {
                HuffmanNode left = pq.poll();
                HuffmanNode right = pq.poll();

                HuffmanNode parent = new HuffmanNode('\0', left.frequency + right.frequency);
                parent.left = left;
                parent.right = right;

                pq.add(parent);
            }

            // Генерация кодов
            Map<Character, String> codes = new HashMap<>();
            generateCodes(pq.peek(), "", codes);

            return codes;
        }

        private void generateCodes(HuffmanNode node, String code, Map<Character, String> codes) {
            if (node == null) return;

            if (node.character != '\0') { // Лист
                codes.put(node.character, code);
                return;
            }

            generateCodes(node.left, code + "0", codes);
            generateCodes(node.right, code + "1", codes);
        }
    }

    /**
     * Демонстрация сжатия текста
     */
    public void demonstrateHuffman() {
        HuffmanCoding huffman = new HuffmanCoding();
        String text = "hello world";

        Map<Character, String> codes = huffman.buildHuffmanCodes(text);

        System.out.println("Коды Хаффмана для '" + text + "':");
        for (Map.Entry<Character, String> entry : codes.entrySet()) {
            System.out.println("'" + entry.getKey() + "': " + entry.getValue());
        }

        // Кодирование текста
        StringBuilder encoded = new StringBuilder();
        for (char c : text.toCharArray()) {
            encoded.append(codes.get(c));
        }

        System.out.println("Закодированный текст: " + encoded.toString());
        System.out.println("Длина оригинала: " + (text.length() * 8) + " бит");
        System.out.println("Длина сжатого: " + encoded.length() + " бит");
    }
}
```

## Алгоритмы и структуры данных

### Основные структуры данных

```java
    /**
     * Реализация основных структур данных
 */
public class DataStructures {

    /**
     * Стек (LIFO) - Last In, First Out
     */
    public static class Stack<T> {
        private LinkedList<T> list = new LinkedList<>();

        public void push(T item) { list.addFirst(item); }
        public T pop() { return list.removeFirst(); }
        public T peek() { return list.getFirst(); }
        public boolean isEmpty() { return list.isEmpty(); }
        public int size() { return list.size(); }
    }

    /**
     * Очередь (FIFO) - First In, First Out
     */
    public static class Queue<T> {
        private LinkedList<T> list = new LinkedList<>();

        public void enqueue(T item) { list.addLast(item); }
        public T dequeue() { return list.removeFirst(); }
        public T peek() { return list.getFirst(); }
        public boolean isEmpty() { return list.isEmpty(); }
        public int size() { return list.size(); }
    }

    /**
     * Бинарное дерево поиска
     */
    public static class BinarySearchTree {
        private Node root;

        private static class Node {
            int value;
            Node left, right;

            Node(int value) {
                this.value = value;
            }
        }

        public void insert(int value) {
            root = insertRec(root, value);
        }

        private Node insertRec(Node node, int value) {
            if (node == null) return new Node(value);

            if (value < node.value) {
                node.left = insertRec(node.left, value);
            } else if (value > node.value) {
                node.right = insertRec(node.right, value);
            }

            return node;
        }

        public boolean contains(int value) {
            return containsRec(root, value);
        }

        private boolean containsRec(Node node, int value) {
            if (node == null) return false;

            if (value == node.value) return true;

            return value < node.value
                ? containsRec(node.left, value)
                : containsRec(node.right, value);
        }

        public void inOrderTraversal() {
            inOrderRec(root);
            System.out.println();
        }

        private void inOrderRec(Node node) {
            if (node != null) {
                inOrderRec(node.left);
                System.out.print(node.value + " ");
                inOrderRec(node.right);
            }
        }
    }

    /**
     * Хеш-таблица
     */
    public static class SimpleHashMap<K, V> {
        private static final int DEFAULT_CAPACITY = 16;
        private List<Entry<K, V>>[] buckets;
        private int size;

        private static class Entry<K, V> {
            K key;
            V value;
            Entry<K, V> next;

            Entry(K key, V value, Entry<K, V> next) {
                this.key = key;
                this.value = value;
                this.next = next;
            }
        }

        @SuppressWarnings("unchecked")
        public SimpleHashMap() {
            buckets = new List[DEFAULT_CAPACITY];
            for (int i = 0; i < DEFAULT_CAPACITY; i++) {
                buckets[i] = new LinkedList<>();
            }
        }

        public void put(K key, V value) {
            int index = Math.abs(key.hashCode()) % buckets.length;
            List<Entry<K, V>> bucket = buckets[index];

            // Проверка существования ключа
            for (Entry<K, V> entry : bucket) {
                if (Objects.equals(entry.key, key)) {
                    entry.value = value;
                    return;
                }
            }

            bucket.add(new Entry<>(key, value, null));
            size++;
        }

        public V get(K key) {
            int index = Math.abs(key.hashCode()) % buckets.length;
            List<Entry<K, V>> bucket = buckets[index];

            for (Entry<K, V> entry : bucket) {
                if (Objects.equals(entry.key, key)) {
                    return entry.value;
                }
            }

            return null;
        }
    }
}
```

### Алгоритмы сортировки

```java
    /**
     * Реализация основных алгоритмов сортировки
 */
public class SortingAlgorithms {

    /**
     * Пузырьковая сортировка - O(n²)
     */
    public static void bubbleSort(int[] array) {
        int n = array.length;
        boolean swapped;

        for (int i = 0; i < n - 1; i++) {
            swapped = false;

            for (int j = 0; j < n - i - 1; j++) {
                if (array[j] > array[j + 1]) {
                    // Обмен элементов
                    int temp = array[j];
                    array[j] = array[j + 1];
                    array[j + 1] = temp;
                    swapped = true;
                }
            }

            // Если не было обменов, массив отсортирован
            if (!swapped) break;
        }
    }

    /**
     * Быстрая сортировка - O(n log n) в среднем
     */
    public static void quickSort(int[] array, int low, int high) {
        if (low < high) {
            int pivotIndex = partition(array, low, high);

            quickSort(array, low, pivotIndex - 1);
            quickSort(array, pivotIndex + 1, high);
        }
    }

    private static int partition(int[] array, int low, int high) {
        int pivot = array[high];
        int i = low - 1;

        for (int j = low; j < high; j++) {
            if (array[j] < pivot) {
                i++;
                // Обмен
                int temp = array[i];
                array[i] = array[j];
                array[j] = temp;
            }
        }

        // Поместить pivot на правильную позицию
        int temp = array[i + 1];
        array[i + 1] = array[high];
        array[high] = temp;

        return i + 1;
    }

    /**
     * Сортировка слиянием - O(n log n)
     */
    public static void mergeSort(int[] array) {
        if (array.length < 2) return;

        int mid = array.length / 2;
        int[] left = Arrays.copyOfRange(array, 0, mid);
        int[] right = Arrays.copyOfRange(array, mid, array.length);

        mergeSort(left);
        mergeSort(right);

        merge(array, left, right);
    }

    private static void merge(int[] result, int[] left, int[] right) {
        int i = 0, j = 0, k = 0;

        while (i < left.length && j < right.length) {
            if (left[i] <= right[j]) {
                result[k++] = left[i++];
            } else {
                result[k++] = right[j++];
            }
        }

        while (i < left.length) result[k++] = left[i++];
        while (j < right.length) result[k++] = right[j++];
    }

    /**
     * Сравнение производительности сортировок
     */
    public static void compareSortingAlgorithms() {
        int[] sizes = {1000, 10000, 100000};

        for (int size : sizes) {
            System.out.println("\nРазмер массива: " + size);

            // Генерация случайного массива
            int[] original = new Random().ints(size, 0, 1000000).toArray();

            // Bubble Sort
            int[] bubbleArray = Arrays.copyOf(original, original.length);
            long startTime = System.nanoTime();
            bubbleSort(bubbleArray);
            long bubbleTime = System.nanoTime() - startTime;

            // Quick Sort
            int[] quickArray = Arrays.copyOf(original, original.length);
            startTime = System.nanoTime();
            quickSort(quickArray, 0, quickArray.length - 1);
            long quickTime = System.nanoTime() - startTime;

            // Merge Sort
            int[] mergeArray = Arrays.copyOf(original, original.length);
            startTime = System.nanoTime();
            mergeSort(mergeArray);
            long mergeTime = System.nanoTime() - startTime;

            // Java Arrays.sort (Dual-Pivot QuickSort + Insertion Sort)
            int[] javaArray = Arrays.copyOf(original, original.length);
            startTime = System.nanoTime();
            Arrays.sort(javaArray);
            long javaTime = System.nanoTime() - startTime;

            System.out.println("Bubble Sort: " + bubbleTime / 1_000_000 + " ms");
            System.out.println("Quick Sort: " + quickTime / 1_000_000 + " ms");
            System.out.println("Merge Sort: " + mergeTime / 1_000_000 + " ms");
            System.out.println("Arrays.sort: " + javaTime / 1_000_000 + " ms");
        }
    }
}
```

## Архитектура компьютеров

### Основные компоненты компьютера

```java
    /**
     * Абстрактная модель архитектуры фон Неймана
 */
public class VonNeumannArchitecture {

    /**
     * Центральный процессор (CPU)
     */
    public static class CPU {
        private int[] registers;     // Регистры общего назначения
        private int programCounter;  // Счетчик команд
        private ALU alu;            // Арифметико-логическое устройство

        public CPU(int numRegisters) {
            registers = new int[numRegisters];
            programCounter = 0;
            alu = new ALU();
        }

    /**
     * Выполнение инструкции
         */
        public void execute(int instruction) {
            // Декодирование и выполнение инструкции
            programCounter++;
        }

    /**
     * Арифметико-логическое устройство
         */
        private static class ALU {
            public int add(int a, int b) { return a + b; }
            public int subtract(int a, int b) { return a - b; }
            public int multiply(int a, int b) { return a + b; } // Упрощенно
            public int and(int a, int b) { return a & b; }
            public int or(int a, int b) { return a | b; }
        }
    }

    /**
     * Оперативная память (RAM)
     */
    public static class Memory {
        private int[] memory;
        private int size;

        public Memory(int size) {
            this.size = size;
            memory = new int[size];
        }

        public int read(int address) {
            if (address >= 0 && address < size) {
                return memory[address];
            }
            throw new IllegalArgumentException("Неверный адрес памяти");
        }

        public void write(int address, int value) {
            if (address >= 0 && address < size) {
                memory[address] = value;
            } else {
                throw new IllegalArgumentException("Неверный адрес памяти");
            }
        }
    }

    /**
     * Устройства ввода-вывода
     */
    public static class IODevices {
        private List<String> inputBuffer;
        private List<String> outputBuffer;

        public IODevices() {
            inputBuffer = new LinkedList<>();
            outputBuffer = new LinkedList<>();
        }

        public void input(String data) {
            inputBuffer.add(data);
        }

        public String output() {
            return outputBuffer.isEmpty() ? null : outputBuffer.remove(0);
        }

        public void print(String message) {
            outputBuffer.add(message);
            System.out.println(message);
        }
    }

    /**
     * Шина - средство коммуникации между компонентами
     */
    public static class Bus {
        private CPU cpu;
        private Memory memory;
        private IODevices io;

        public Bus(CPU cpu, Memory memory, IODevices io) {
            this.cpu = cpu;
            this.memory = memory;
            this.io = io;
        }

        public void transferData() {
            // Передача данных между компонентами
        }
    }
}
```

### Иерархия памяти

```java
    /**
     * Иерархия памяти компьютера
 */
public class MemoryHierarchy {

    /**
     * Регистры - самые быстрые, но мало места
     */
    public static class Registers {
        private int[] registers;

        public Registers(int count) {
            registers = new int[count];
        }

        // Доступ за 1 такт процессора
        public int read(int index) { return registers[index]; }
        public void write(int index, int value) { registers[index] = value; }
    }

    /**
     * Кэш-память - быстрая, но дорогая
     */
    public static class Cache {
        private Map<Integer, Integer> cache;
        private int capacity;

        public Cache(int capacity) {
            this.capacity = capacity;
            cache = new LinkedHashMap<Integer, Integer>(capacity, 0.75f, true) {
                @Override
                protected boolean removeEldestEntry(Map.Entry eldest) {
                    return size() > capacity;
                }
            };
        }

        // Доступ за 10-20 тактов
        public Integer get(int key) { return cache.get(key); }
        public void put(int key, int value) { cache.put(key, value); }
    }

    /**
     * Оперативная память (RAM)
     */
    public static class RAM {
        private int[] memory;

        public RAM(int size) {
            memory = new int[size];
        }

        // Доступ за 100-200 тактов
        public int read(int address) { return memory[address]; }
        public void write(int address, int value) { memory[address] = value; }
    }

    /**
     * Виртуальная память
     */
    public static class VirtualMemory {
        private Map<Integer, Integer> pageTable;
        private RAM ram;
        private Disk disk;

        public VirtualMemory(RAM ram, Disk disk) {
            this.ram = ram;
            this.disk = disk;
            pageTable = new HashMap<>();
        }

        public int read(int virtualAddress) {
            int physicalAddress = translateAddress(virtualAddress);
            return ram.read(physicalAddress);
        }

        private int translateAddress(int virtualAddress) {
            // TLB lookup, page table lookup, etc.
            return virtualAddress % ram.getSize();
        }
    }

    /**
     * Жесткий диск
     */
    public static class Disk {
        private Map<String, byte[]> files;

        public Disk() {
            files = new HashMap<>();
        }

        // Доступ за миллионы тактов
        public void write(String filename, byte[] data) {
            files.put(filename, data.clone());
        }

        public byte[] read(String filename) {
            return files.get(filename);
        }
    }

    /**
     * Демонстрация иерархии памяти
     */
    public void demonstrateMemoryHierarchy() {
        System.out.println("Иерархия памяти (от быстрой к медленной):");
        System.out.println("1. Регистры CPU - 1 такт, ~1KB");
        System.out.println("2. L1 Cache - 4 такта, ~64KB");
        System.out.println("3. L2 Cache - 10 тактов, ~512KB");
        System.out.println("4. L3 Cache - 40 тактов, ~8MB");
        System.out.println("5. RAM - 100 тактов, ~16GB");
        System.out.println("6. SSD - 100,000 тактов, ~1TB");
        System.out.println("7. HDD - 10,000,000 тактов, ~4TB");

        System.out.println("\nПринцип локальности:");
        System.out.println("- Временная локальность: недавно использованные данные");
        System.out.println("- Пространственная локальность: соседние данные");
    }
}
```

### Параллельные вычисления

```java
    /**
     * Основы параллельного программирования
 */
public class ParallelComputing {

    /**
     * Закон Амдала - ограничение параллелизации
     */
    public static class AmdahlsLaw {

    /**
     * Расчет ускорения по закону Амдала
         * @param parallelFraction доля параллельного кода (0.0 - 1.0)
         * @param numProcessors количество процессоров
         * @return ускорение выполнения
         */
        public static double calculateSpeedup(double parallelFraction, int numProcessors) {
            double sequentialFraction = 1.0 - parallelFraction;
            return 1.0 / (sequentialFraction + parallelFraction / numProcessors);
        }

    /**
     * Демонстрация закона Амдала
         */
        public static void demonstrateAmdahlsLaw() {
            System.out.println("Закон Амдала - максимальное ускорение при параллелизации:");
            System.out.println();

            int[] processors = {1, 2, 4, 8, 16, 32, 64};

            for (double parallelFraction : new double[]{0.5, 0.75, 0.9, 0.95, 0.99}) {
                System.out.println("Доля параллельного кода: " + (parallelFraction * 100) + "%");
                for (int p : processors) {
                    double speedup = calculateSpeedup(parallelFraction, p);
                    System.out.printf("  Процессоры: %2d, Ускорение: %.2f%n", p, speedup);
                }
                System.out.println();
            }

            System.out.println("Вывод: последовательная часть ограничивает максимальное ускорение!");
        }
    }

    /**
     * Параллельная обработка данных
     */
    public static class ParallelProcessing {

    /**
     * Параллельное суммирование массива
         */
        public static long parallelSum(int[] array) {
            return Arrays.stream(array)
                    .parallel()
                    .mapToLong(i -> i)
                    .sum();
        }

    /**
     * Параллельная сортировка
         */
        public static void parallelSort(int[] array) {
            Arrays.parallelSort(array);
        }

    /**
     * Параллельная фильтрация
         */
        public static List<Integer> parallelFilter(List<Integer> list, Predicate<Integer> predicate) {
            return list.parallelStream()
                    .filter(predicate)
                    .collect(Collectors.toList());
        }

    /**
     * Сравнение последовательной и параллельной обработки
         */
        public static void compareSequentialVsParallel() {
            int[] largeArray = new Random().ints(10_000_000, 0, 100).toArray();

            // Последовательное суммирование
            long startTime = System.nanoTime();
            long sequentialSum = Arrays.stream(largeArray).sum();
            long sequentialTime = System.nanoTime() - startTime;

            // Параллельное суммирование
            startTime = System.nanoTime();
            long parallelSum = Arrays.stream(largeArray).parallel().sum();
            long parallelTime = System.nanoTime() - startTime;

            System.out.println("Суммирование массива из 10M элементов:");
            System.out.println("Последовательно: " + sequentialTime / 1_000_000 + " ms");
            System.out.println("Параллельно: " + parallelTime / 1_000_000 + " ms");
            System.out.println("Ускорение: " + (double) sequentialTime / parallelTime + "x");
            System.out.println("Результаты совпадают: " + (sequentialSum == parallelSum));
        }
    }

    /**
     * Проблемы параллельного программирования
     */
    public static class ConcurrencyIssues {

        private static int counter = 0;

    /**
     * Гонка данных (Race Condition)
         */
        public static void demonstrateRaceCondition() throws InterruptedException {
            Runnable incrementTask = () -> {
                for (int i = 0; i < 1000; i++) {
                    counter++; // Неатомарная операция!
                }
            };

            Thread t1 = new Thread(incrementTask);
            Thread t2 = new Thread(incrementTask);

            t1.start();
            t2.start();

            t1.join();
            t2.join();

            System.out.println("Ожидаемый результат: 2000");
            System.out.println("Фактический результат: " + counter);
            System.out.println("Гонка данных привела к неправильному результату!");
        }

        /**
     * Взаимная блокировка (Deadlock)
         */
        public static void demonstrateDeadlock() {
            Object lock1 = new Object();
            Object lock2 = new Object();

            Runnable task1 = () -> {
                synchronized (lock1) {
                    System.out.println("Поток 1 захватил lock1");
                    try { Thread.sleep(100); } catch (InterruptedException e) {}

                    synchronized (lock2) {
                        System.out.println("Поток 1 захватил lock2");
                    }
                }
            };

            Runnable task2 = () -> {
                synchronized (lock2) {
                    System.out.println("Поток 2 захватил lock2");
                    try { Thread.sleep(100); } catch (InterruptedException e) {}

                    synchronized (lock1) {
                        System.out.println("Поток 2 захватил lock1");
                    }
                }
            };

            new Thread(task1).start();
            new Thread(task2).start();
        }
    }
}
```

## Операционные системы

### Процессы и потоки

```java
    /**
     * Модель процессов и потоков
 */
public class ProcessesAndThreads {

    /**
     * Представление процесса
     */
    public static class Process {
        private int pid;
        private String name;
        private ProcessState state;
        private Map<String, Object> resources;

        public enum ProcessState {
            NEW, READY, RUNNING, WAITING, TERMINATED
        }

        public Process(int pid, String name) {
            this.pid = pid;
            this.name = name;
            this.state = ProcessState.NEW;
            this.resources = new HashMap<>();
        }

        public void allocateResource(String resource, Object value) {
            resources.put(resource, value);
        }

        public Object getResource(String resource) {
            return resources.get(resource);
        }

        public void setState(ProcessState state) {
            this.state = state;
        }

        public ProcessState getState() {
            return state;
        }
    }

    /**
     * Планировщик процессов
     */
    public static class ProcessScheduler {
        private Queue<Process> readyQueue;
        private Process currentProcess;

        public ProcessScheduler() {
            readyQueue = new LinkedList<>();
        }

        public void addProcess(Process process) {
            process.setState(Process.ProcessState.READY);
            readyQueue.add(process);
        }

        public void scheduleNext() {
            if (currentProcess != null) {
                currentProcess.setState(Process.ProcessState.READY);
                readyQueue.add(currentProcess);
            }

            currentProcess = readyQueue.poll();
            if (currentProcess != null) {
                currentProcess.setState(Process.ProcessState.RUNNING);
            }
        }

        public Process getCurrentProcess() {
            return currentProcess;
        }

        public void terminateCurrentProcess() {
            if (currentProcess != null) {
                currentProcess.setState(Process.ProcessState.TERMINATED);
                currentProcess = null;
            }
        }
    }

    /**
     * Управление памятью
     */
    public static class MemoryManager {
        private boolean[] memoryMap;
        private int totalMemory;

        public MemoryManager(int totalMemory) {
            this.totalMemory = totalMemory;
            memoryMap = new boolean[totalMemory];
        }

    /**
     * Выделение памяти (алгоритм первого подходящего)
         */
        public int allocate(int size) {
            for (int i = 0; i <= totalMemory - size; i++) {
                boolean canAllocate = true;

                for (int j = 0; j < size; j++) {
                    if (memoryMap[i + j]) {
                        canAllocate = false;
                        break;
                    }
                }

                if (canAllocate) {
                    // Выделяем память
                    for (int j = 0; j < size; j++) {
                        memoryMap[i + j] = true;
                    }
                    return i; // Возвращаем начальный адрес
                }
            }

            return -1; // Недостаточно памяти
        }

    /**
     * Освобождение памяти
         */
        public void deallocate(int address, int size) {
            for (int i = 0; i < size; i++) {
                memoryMap[address + i] = false;
            }
        }

    /**
     * Статистика использования памяти
         */
        public void printMemoryStats() {
            int used = 0;
            for (boolean usedBlock : memoryMap) {
                if (usedBlock) used++;
            }

            System.out.println("Всего памяти: " + totalMemory);
            System.out.println("Использовано: " + used);
            System.out.println("Свободно: " + (totalMemory - used));
            System.out.println("Процент использования: " + (used * 100.0 / totalMemory) + "%");
        }
    }

    /**
     * Демонстрация работы ОС
     */
    public static void demonstrateOS() {
        System.out.println("=== Демонстрация работы ОС ===");

        // Создание планировщика
        ProcessScheduler scheduler = new ProcessScheduler();

        // Создание процессов
        Process p1 = new Process(1, "Browser");
        Process p2 = new Process(2, "TextEditor");
        Process p3 = new Process(3, "Compiler");

        // Добавление процессов в планировщик
        scheduler.addProcess(p1);
        scheduler.addProcess(p2);
        scheduler.addProcess(p3);

        // Имитация планирования
        System.out.println("Планирование процессов:");
        for (int i = 0; i < 6; i++) {
            scheduler.scheduleNext();
            Process current = scheduler.getCurrentProcess();
            if (current != null) {
                System.out.println("Выполняется: " + current.name + " (PID: " + current.pid + ")");
            }
        }

        // Управление памятью
        System.out.println("\n=== Управление памятью ===");
        MemoryManager memoryManager = new MemoryManager(100);

        int addr1 = memoryManager.allocate(20);
        int addr2 = memoryManager.allocate(30);
        int addr3 = memoryManager.allocate(25);

        System.out.println("Выделенные блоки:");
        System.out.println("Блок 1: адрес " + addr1 + ", размер 20");
        System.out.println("Блок 2: адрес " + addr2 + ", размер 30");
        System.out.println("Блок 3: адрес " + addr3 + ", размер 25");

        memoryManager.printMemoryStats();

        // Освобождение памяти
        memoryManager.deallocate(addr2, 30);
        System.out.println("\nПосле освобождения блока 2:");
        memoryManager.printMemoryStats();
    }
}
```

## Базы данных

### Реляционная модель

```java
    /**
     * Простая реализация реляционной базы данных
 */
public class RelationalDatabase {

    /**
     * Таблица базы данных
     */
    public static class Table {
        private String name;
        private List<String> columns;
        private List<Map<String, Object>> rows;

        public Table(String name, List<String> columns) {
            this.name = name;
            this.columns = columns;
            this.rows = new ArrayList<>();
        }

        public void insert(Map<String, Object> row) {
            rows.add(new HashMap<>(row));
        }

        public List<Map<String, Object>> selectAll() {
            return new ArrayList<>(rows);
        }

        public List<Map<String, Object>> selectWhere(Predicate<Map<String, Object>> condition) {
            return rows.stream()
                    .filter(condition)
                    .collect(Collectors.toList());
        }

        public void updateWhere(Predicate<Map<String, Object>> condition,
                              Function<Map<String, Object>, Map<String, Object>> updater) {
            for (int i = 0; i < rows.size(); i++) {
                if (condition.test(rows.get(i))) {
                    rows.set(i, updater.apply(rows.get(i)));
                }
            }
        }

        public void deleteWhere(Predicate<Map<String, Object>> condition) {
            rows.removeIf(condition);
        }
    }

    /**
     * SQL-подобный запрос
     */
    public static class Query {
        private Table table;

        public Query(Table table) {
            this.table = table;
        }

        public List<Map<String, Object>> execute() {
            return table.selectAll();
        }
    }

    /**
     * Демонстрация работы с реляционной БД
     */
    public static void demonstrateRelationalDB() {
        // Создание таблицы пользователей
        Table users = new Table("users", Arrays.asList("id", "name", "email", "age"));

        // Вставка данных
        users.insert(Map.of("id", 1, "name", "Alice", "email", "alice@example.com", "age", 25));
        users.insert(Map.of("id", 2, "name", "Bob", "email", "bob@example.com", "age", 30));
        users.insert(Map.of("id", 3, "name", "Charlie", "email", "charlie@example.com", "age", 35));

        System.out.println("Все пользователи:");
        for (Map<String, Object> user : users.selectAll()) {
            System.out.println(user);
        }

        // Выборка по условию
        System.out.println("\nПользователи старше 28:");
        List<Map<String, Object>> adults = users.selectWhere(
            user -> (Integer) user.get("age") > 28
        );
        adults.forEach(System.out::println);

        // Обновление данных
        users.updateWhere(
            user -> user.get("name").equals("Alice"),
            user -> {
                Map<String, Object> updated = new HashMap<>(user);
                updated.put("age", 26);
                return updated;
            }
        );

        // Удаление данных
        users.deleteWhere(user -> (Integer) user.get("age") < 30);

        System.out.println("\nПосле обновления и удаления:");
        users.selectAll().forEach(System.out::println);
    }
}
```

### Нормализация данных

```java
    /**
     * Принципы нормализации реляционных баз данных
 */
public class DatabaseNormalization {

    /**
     * Не нормализованная таблица (0NF)
     */
    public static class UnnormalizedOrder {
        // Все данные в одной таблице
        int orderId;
        String customerName;
        String customerAddress;
        List<String> productNames;     // Список продуктов
        List<Integer> productPrices;   // Цены продуктов
        List<Integer> quantities;      // Количества
        double totalAmount;

        // Проблемы:
        // - Избыточность данных
        // - Аномалии обновления
        // - Аномалии удаления
        // - Аномалии вставки
    }

    /**
     * Первая нормальная форма (1NF)
     */
    public static class FirstNormalForm {
        // Атомарные значения, но все еще избыточность

        static class OrderItem {
            int orderId;
            String customerName;      // Избыточно для каждого товара
            String customerAddress;   // Избыточно для каждого товара
            String productName;
            int productPrice;
            int quantity;
            double lineTotal;
        }

        // Проблема: избыточность customer данных
    }

    /**
     * Вторая нормальная форма (2NF)
     */
    public static class SecondNormalForm {

        static class Customer {
            int customerId;
            String name;
            String address;
        }

        static class Order {
            int orderId;
            int customerId;        // Ссылка на Customer
            Date orderDate;
            double totalAmount;
        }

        static class OrderItem {
            int orderId;          // Составной ключ
            int productId;        // Составной ключ
            int quantity;
            double lineTotal;
        }

        static class Product {
            int productId;
            String name;
            int price;
        }
    }

    /**
     * Третья нормальная форма (3NF)
     */
    public static class ThirdNormalForm {

        static class Customer {
            int customerId;
            String name;
            int addressId;        // Избегаем транзитивной зависимости
        }

        static class Address {
            int addressId;
            String street;
            String city;
            String postalCode;
        }

        // Order и OrderItem остаются без изменений
        static class Order {
            int orderId;
            int customerId;
            Date orderDate;
            double totalAmount;
        }

        static class OrderItem {
            int orderId;
            int productId;
            int quantity;
            double lineTotal;
        }

        static class Product {
            int productId;
            String name;
            int price;
        }
    }

    /**
     * Преимущества нормализации
     */
    public void normalizationBenefits() {
        System.out.println("Преимущества нормализации:");
        System.out.println("1. Устранение избыточности данных");
        System.out.println("2. Предотвращение аномалий обновления");
        System.out.println("3. Улучшение целостности данных");
        System.out.println("4. Упрощение запросов и обновлений");
        System.out.println("5. Лучшая производительность для большинства операций");

        System.out.println("\nНедостатки нормализации:");
        System.out.println("1. Увеличение количества таблиц");
        System.out.println("2. Более сложные запросы (JOIN)");
        System.out.println("3. Потенциальное снижение производительности для сложных запросов");
    }
}
```

## Компьютерные сети

### Модель OSI

```java
    /**
     * Реализация модели OSI
 */
public class OSIModel {

    /**
     * Уровни модели OSI
     */
    public enum OSILayer {
        PHYSICAL(1, "Физический"),
        DATA_LINK(2, "Канальный"),
        NETWORK(3, "Сетевой"),
        TRANSPORT(4, "Транспортный"),
        SESSION(5, "Сеансовый"),
        PRESENTATION(6, "Представительский"),
        APPLICATION(7, "Прикладной");

        private final int level;
        private final String name;

        OSILayer(int level, String name) {
            this.level = level;
            this.name = name;
        }
    }

    /**
     * Сообщение, проходящее через уровни OSI
     */
    public static class OSIMessage {
        private String data;
        private Map<OSILayer, String> headers;

        public OSIMessage(String data) {
            this.data = data;
            this.headers = new EnumMap<>(OSILayer.class);
        }

        public void addHeader(OSILayer layer, String header) {
            headers.put(layer, header);
        }

        public String getHeader(OSILayer layer) {
            return headers.get(layer);
        }

        public String getData() {
            return data;
        }

        public void encapsulate(OSILayer layer, String encapsulationData) {
            data = encapsulationData + "|" + data;
        }

        public String decapsulate() {
            int separatorIndex = data.indexOf('|');
            if (separatorIndex != -1) {
                String encapsulation = data.substring(0, separatorIndex);
                data = data.substring(separatorIndex + 1);
                return encapsulation;
            }
            return data;
        }
    }

    /**
     * Узел сети
     */
    public static class NetworkNode {
        private String name;
        private Map<OSILayer, Object> layerImplementations;

        public NetworkNode(String name) {
            this.name = name;
            this.layerImplementations = new EnumMap<>(OSILayer.class);
        }

        public void setLayerImplementation(OSILayer layer, Object implementation) {
            layerImplementations.put(layer, implementation);
        }

    /**
     * Отправка сообщения через стек протоколов
         */
        public void sendMessage(NetworkNode destination, OSIMessage message) {
            System.out.println(name + " отправляет сообщение '" + message.getData() + "'");

            // Проход сверху вниз по стеку протоколов
            for (OSILayer layer : OSILayer.values()) {
                processOutgoing(layer, message);
            }

            // Имитация передачи по сети
            System.out.println("Сообщение передается по сети...");

            // Получатель обрабатывает сообщение снизу вверх
            destination.receiveMessage(this, message);
        }

    /**
     * Получение сообщения
         */
        public void receiveMessage(NetworkNode sender, OSIMessage message) {
            System.out.println(name + " получает сообщение");

            // Проход снизу вверх по стеку протоколов
            OSILayer[] layers = OSILayer.values();
            for (int i = layers.length - 1; i >= 0; i--) {
                processIncoming(layers[i], message);
            }

            System.out.println(name + " обработал сообщение: '" + message.getData() + "'");
        }

        private void processOutgoing(OSILayer layer, OSIMessage message) {
            System.out.println("  " + layer.name + " уровень: обработка исходящего сообщения");
            message.encapsulate(layer, layer.name() + "_HEADER");
        }

        private void processIncoming(OSILayer layer, OSIMessage message) {
            System.out.println("  " + layer.name + " уровень: обработка входящего сообщения");
            message.decapsulate();
        }
    }

    /**
     * Демонстрация модели OSI
     */
    public static void demonstrateOSI() {
        NetworkNode client = new NetworkNode("Клиент");
        NetworkNode server = new NetworkNode("Сервер");

        OSIMessage message = new OSIMessage("Привет, мир!");
        client.sendMessage(server, message);
    }
}
```

### TCP/`IP` стек

```java
    /**
     * Реализация TCP/IP стека
 */
public class TCPIPStack {

    /**
     * IP адрес
     */
    public static class IPAddress {
        private int[] octets;

        public IPAddress(String ipString) {
            String[] parts = ipString.split("\\.");
            if (parts.length != 4) throw new IllegalArgumentException("Неверный IP адрес");

            octets = new int[4];
            for (int i = 0; i < 4; i++) {
                octets[i] = Integer.parseInt(parts[i]);
                if (octets[i] < 0 || octets[i] > 255) {
                    throw new IllegalArgumentException("Неверный октет: " + octets[i]);
                }
            }
        }

        @Override
        public String toString() {
            return octets[0] + "." + octets[1] + "." + octets[2] + "." + octets[3];
        }

        public boolean isPrivate() {
            // 192.168.x.x, 172.16-31.x.x, 10.x.x.x
            return (octets[0] == 192 && octets[1] == 168) ||
                   (octets[0] == 172 && octets[1] >= 16 && octets[1] <= 31) ||
                   (octets[0] == 10);
        }
    }

    /**
     * Порт
     */
    public static class Port {
        private int port;

        public Port(int port) {
            if (port < 0 || port > 65535) {
                throw new IllegalArgumentException("Неверный порт: " + port);
            }
            this.port = port;
        }

        public boolean isWellKnown() { return port < 1024; }
        public boolean isRegistered() { return port >= 1024 && port < 49152; }
        public boolean isDynamic() { return port >= 49152; }

        @Override
        public String toString() { return String.valueOf(port); }
    }

    /**
     * TCP соединение
     */
    public static class TCPConnection {
        private IPAddress localIP, remoteIP;
        private Port localPort, remotePort;
        private TCPState state;

        public enum TCPState {
            CLOSED, LISTEN, SYN_SENT, SYN_RECEIVED,
            ESTABLISHED, FIN_WAIT_1, FIN_WAIT_2,
            CLOSE_WAIT, CLOSING, LAST_ACK, TIME_WAIT
        }

        public TCPConnection(IPAddress localIP, Port localPort,
                           IPAddress remoteIP, Port remotePort) {
            this.localIP = localIP;
            this.localPort = localPort;
            this.remoteIP = remoteIP;
            this.remotePort = remotePort;
            this.state = TCPState.CLOSED;
        }

    /**
     * Трехстороннее рукопожатие
         */
        public void establishConnection() {
            System.out.println("Установление TCP соединения:");

            // SYN
            state = TCPState.SYN_SENT;
            System.out.println("Клиент: SYN отправлен");

            // SYN-ACK
            state = TCPState.SYN_RECEIVED;
            System.out.println("Сервер: SYN-ACK отправлен");

            // ACK
            state = TCPState.ESTABLISHED;
            System.out.println("Клиент: ACK отправлен");
            System.out.println("Соединение установлено!");
        }

    /**
     * Закрытие соединения
         */
        public void closeConnection() {
            System.out.println("Закрытие TCP соединения:");

            // FIN
            state = TCPState.FIN_WAIT_1;
            System.out.println("Клиент: FIN отправлен");

            // ACK
            state = TCPState.FIN_WAIT_2;
            System.out.println("Сервер: ACK отправлен");

            // FIN
            state = TCPState.TIME_WAIT;
            System.out.println("Сервер: FIN отправлен");

            // ACK
            state = TCPState.CLOSED;
            System.out.println("Клиент: ACK отправлен");
            System.out.println("Соединение закрыто!");
        }
    }

    /**
     * HTTP запрос
     */
    public static class HTTPRequest {
        private String method;
        private String path;
        private String version;
        private Map<String, String> headers;
        private String body;

        public HTTPRequest(String method, String path) {
            this.method = method;
            this.path = path;
            this.version = "HTTP/1.1";
            this.headers = new HashMap<>();
        }

        public void addHeader(String name, String value) {
            headers.put(name, value);
        }

        public String toString() {
            StringBuilder sb = new StringBuilder();
            sb.append(method).append(" ").append(path).append(" ").append(version).append("\r\n");

            for (Map.Entry<String, String> header : headers.entrySet()) {
                sb.append(header.getKey()).append(": ").append(header.getValue()).append("\r\n");
            }

            sb.append("\r\n");
            if (body != null) sb.append(body);

            return sb.toString();
        }
    }

    /**
     * Демонстрация TCP/IP стека
     */
    public static void demonstrateTCPIP() {
        System.out.println("=== TCP/IP стек ===");

        // IP адреса
        IPAddress clientIP = new IPAddress("192.168.1.100");
        IPAddress serverIP = new IPAddress("192.168.1.200");

        System.out.println("Клиент IP: " + clientIP + " (приватный: " + clientIP.isPrivate() + ")");
        System.out.println("Сервер IP: " + serverIP + " (приватный: " + serverIP.isPrivate() + ")");

        // Порты
        Port clientPort = new Port(49153); // Динамический порт
        Port serverPort = new Port(80);    // HTTP порт

        System.out.println("Клиент порт: " + clientPort + " (динамический: " + clientPort.isDynamic() + ")");
        System.out.println("Сервер порт: " + serverPort + " (well-known: " + serverPort.isWellKnown() + ")");

        // TCP соединение
        TCPConnection connection = new TCPConnection(clientIP, clientPort, serverIP, serverPort);
        connection.establishConnection();

        // HTTP запрос
        HTTPRequest request = new HTTPRequest("GET", "/index.html");
        request.addHeader("Host", "example.com");
        request.addHeader("User-Agent", "Java HTTP Client");

        System.out.println("\nHTTP запрос:");
        System.out.println(request.toString());

        connection.closeConnection();
    }
}
```

## Безопасность

### Основы криптографии

```java
    /**
     * Основы криптографии
 */
public class CryptographyBasics {

    /**
     * Простая реализация шифра Цезаря
     */
    public static class CaesarCipher {
        private static final int ALPHABET_SIZE = 26;

        public static String encrypt(String text, int shift) {
            StringBuilder result = new StringBuilder();

            for (char ch : text.toCharArray()) {
                if (Character.isLetter(ch)) {
                    char base = Character.isUpperCase(ch) ? 'A' : 'a';
                    char encrypted = (char) (base + (ch - base + shift) % ALPHABET_SIZE);
                    result.append(encrypted);
                } else {
                    result.append(ch);
                }
            }

            return result.toString();
        }

        public static String decrypt(String text, int shift) {
            return encrypt(text, ALPHABET_SIZE - shift);
        }
    }

    /**
     * Простая реализация XOR шифрования
     */
    public static class XORCipher {

        public static String encrypt(String text, String key) {
            StringBuilder result = new StringBuilder();

            for (int i = 0; i < text.length(); i++) {
                char textChar = text.charAt(i);
                char keyChar = key.charAt(i % key.length());
                char encrypted = (char) (textChar ^ keyChar);
                result.append(encrypted);
            }

            return result.toString();
        }

        public static String decrypt(String encrypted, String key) {
            return encrypt(encrypted, key); // XOR - симметричная операция
        }
    }

    /**
     * Хеш-функция (простая реализация)
     */
    public static class SimpleHash {
        private static final int HASH_SIZE = 16;

        public static String hash(String input) {
            int hash = 0;

            for (char ch : input.toCharArray()) {
                hash = (hash * 31 + ch) % (1 << HASH_SIZE);
            }

            return String.format("%04x", hash);
        }

        // Проверка коллизий
        public static void checkCollisions() {
            Set<String> hashes = new HashSet<>();
            Set<String> inputs = new HashSet<>();

            for (int i = 0; i < 10000; i++) {
                String input = "test" + i;
                String hash = hash(input);

                if (!hashes.add(hash)) {
                    System.out.println("Коллизия найдена для: " + input);
                    break;
                }
                inputs.add(input);
            }

            System.out.println("Проверено " + inputs.size() + " значений, хешей: " + hashes.size());
        }
    }

    /**
     * Аутентификация и авторизация
     */
    public static class Authentication {

        private Map<String, String> userPasswords; // В реальности - хешированные пароли
        private Map<String, Set<String>> userRoles;

        public Authentication() {
            userPasswords = new HashMap<>();
            userRoles = new HashMap<>();

            // Инициализация тестовых данных
            userPasswords.put("admin", "admin123");
            userPasswords.put("user", "user123");

            userRoles.put("admin", Set.of("ADMIN", "USER"));
            userRoles.put("user", Set.of("USER"));
        }

        public boolean authenticate(String username, String password) {
            String storedPassword = userPasswords.get(username);
            return storedPassword != null && storedPassword.equals(password);
        }

        public boolean authorize(String username, String requiredRole) {
            Set<String> roles = userRoles.get(username);
            return roles != null && roles.contains(requiredRole);
        }

        public void demonstrateAuth() {
            System.out.println("Аутентификация:");
            System.out.println("admin/admin123: " + authenticate("admin", "admin123"));
            System.out.println("admin/wrong: " + authenticate("admin", "wrong"));

            System.out.println("\nАвторизация:");
            System.out.println("admin имеет роль ADMIN: " + authorize("admin", "ADMIN"));
            System.out.println("user имеет роль ADMIN: " + authorize("user", "ADMIN"));
        }
    }

    /**
     * Демонстрация криптографических концепций
     */
    public static void demonstrateCryptography() {
        String message = "Hello, World!";
        int shift = 3;

        System.out.println("Оригинал: " + message);

        // Шифр Цезаря
        String caesarEncrypted = CaesarCipher.encrypt(message, shift);
        String caesarDecrypted = CaesarCipher.decrypt(caesarEncrypted, shift);

        System.out.println("Цезарь (сдвиг " + shift + "): " + caesarEncrypted);
        System.out.println("Расшифровано: " + caesarDecrypted);

        // XOR шифрование
        String key = "secret";
        String xorEncrypted = XORCipher.encrypt(message, key);
        String xorDecrypted = XORCipher.decrypt(xorEncrypted, key);

        System.out.println("XOR шифр: " + xorEncrypted);
        System.out.println("Расшифровано: " + xorDecrypted);

        // Хеширование
        String hash1 = SimpleHash.hash("password");
        String hash2 = SimpleHash.hash("password");

        System.out.println("Хеш 'password': " + hash1);
        System.out.println("Хеш 'password' снова: " + hash2);
        System.out.println("Хеши совпадают: " + hash1.equals(hash2));

        // Аутентификация
        Authentication auth = new Authentication();
        auth.demonstrateAuth();
    }
}
```

## Теория вычислений

### Машины Тьюринга

```java
    /**
     * Простая реализация машины Тьюринга
 */
public class TuringMachine {

    /**
     * Состояние машины Тьюринга
     */
    public enum State {
        Q0, Q1, Q2, Q3, HALT
    }

    /**
     * Команда машины Тьюринга
     */
    public static class Transition {
        State currentState;
        char readSymbol;
        State nextState;
        char writeSymbol;
        Direction direction;

        public enum Direction { LEFT, RIGHT, STAY }

        public Transition(State current, char read, State next, char write, Direction dir) {
            this.currentState = current;
            this.readSymbol = read;
            this.nextState = next;
            this.writeSymbol = write;
            this.direction = dir;
        }
    }

    /**
     * Лента машины Тьюринга
     */
    public static class Tape {
        private List<Character> tape;
        private int position;

        public Tape(String initial) {
            tape = new ArrayList<>();
            for (char c : initial.toCharArray()) {
                tape.add(c);
            }
            position = 0;
        }

        public char read() {
            ensureCapacity();
            return tape.get(position);
        }

        public void write(char symbol) {
            ensureCapacity();
            tape.set(position, symbol);
        }

        public void moveLeft() {
            if (position > 0) {
                position--;
            } else {
                tape.add(0, ' '); // Расширение ленты влево
            }
        }

        public void moveRight() {
            position++;
            ensureCapacity();
        }

        private void ensureCapacity() {
            while (position >= tape.size()) {
                tape.add(' '); // Расширение ленты вправо
            }
        }

        @Override
        public String toString() {
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < tape.size(); i++) {
                if (i == position) {
                    sb.append('[').append(tape.get(i)).append(']');
                } else {
                    sb.append(tape.get(i));
                }
            }
            return sb.toString();
        }
    }

    /**
     * Машина Тьюринга для инкремента двоичного числа
     */
    public static class BinaryIncrementMachine {
        private Tape tape;
        private State currentState;
        private List<Transition> transitions;

        public BinaryIncrementMachine(String binaryNumber) {
            tape = new Tape(binaryNumber);
            currentState = State.Q0;
            initializeTransitions();
        }

        private void initializeTransitions() {
            transitions = Arrays.asList(
                // q0: поиск правого конца числа
                new Transition(State.Q0, '0', State.Q0, '0', Transition.Direction.RIGHT),
                new Transition(State.Q0, '1', State.Q0, '1', Transition.Direction.RIGHT),
                new Transition(State.Q0, ' ', State.Q1, ' ', Transition.Direction.LEFT),

                // q1: инкремент числа
                new Transition(State.Q1, '0', State.HALT, '1', Transition.Direction.STAY),
                new Transition(State.Q1, '1', State.Q2, '0', Transition.Direction.LEFT),
                new Transition(State.Q1, ' ', State.HALT, '1', Transition.Direction.STAY),

                // q2: перенос
                new Transition(State.Q2, '0', State.HALT, '1', Transition.Direction.STAY),
                new Transition(State.Q2, '1', State.Q2, '0', Transition.Direction.LEFT),
                new Transition(State.Q2, ' ', State.HALT, '1', Transition.Direction.STAY)
            );
        }

        public void run() {
            System.out.println("Начальное состояние: " + tape);

            int steps = 0;
            while (currentState != State.HALT && steps < 100) {
                char symbol = tape.read();

                // Поиск подходящего перехода
                Transition transition = findTransition(currentState, symbol);

                if (transition != null) {
                    // Выполнение перехода
                    tape.write(transition.writeSymbol);

                    switch (transition.direction) {
                        case LEFT:
                            tape.moveLeft();
                            break;
                        case RIGHT:
                            tape.moveRight();
                            break;
                        case STAY:
                            // Остаемся на месте
                            break;
                    }

                    currentState = transition.nextState;

                    System.out.println("Шаг " + (++steps) + ": " + tape +
                                     " (состояние: " + currentState + ")");
                } else {
                    System.out.println("Ошибка: нет перехода для состояния " +
                                     currentState + " и символа '" + symbol + "'");
                    break;
                }
            }

            if (currentState == State.HALT) {
                System.out.println("Машина завершила работу успешно");
            } else {
                System.out.println("Машина не завершила работу (возможно, зациклилась)");
            }
        }

        private Transition findTransition(State state, char symbol) {
            for (Transition t : transitions) {
                if (t.currentState == state && t.readSymbol == symbol) {
                    return t;
                }
            }
            return null;
        }
    }

    /**
     * Демонстрация машины Тьюринга
     */
    public static void demonstrateTuringMachine() {
        System.out.println("=== Машина Тьюринга: инкремент двоичного числа ===");

        String[] testCases = {"1", "10", "11", "101", "111"};

        for (String binary : testCases) {
            System.out.println("\nИнкремент числа: " + binary);
            BinaryIncrementMachine machine = new BinaryIncrementMachine(binary);
            machine.run();
        }
    }
}
```

### Теорема о неполноте Гёделя

```java
    /**
     * Иллюстрация теоремы Гёделя о неполноте
 */
public class GodelsIncompleteness {

    /**
     * Простая формальная система (арифметика)
     */
    public static class FormalSystem {
        private Set<String> axioms;    // Аксиомы системы
        private Set<String> theorems;  // Доказанные теоремы

        public FormalSystem() {
            axioms = new HashSet<>();
            theorems = new HashSet<>();

            // Добавляем базовые аксиомы
            axioms.add("0 = 0");              // Рефлексивность
            axioms.add("∀x(x = x)");          // Рефлексивность для всех
            axioms.add("∀x∀y(x = y → y = x)"); // Симметричность
        }

    /**
     * Добавление теоремы
         */
        public void addTheorem(String theorem) {
            theorems.add(theorem);
        }

    /**
     * Проверка, является ли утверждение теоремой
         */
        public boolean isTheorem(String statement) {
            return theorems.contains(statement) || axioms.contains(statement);
        }

    /**
     * Самореферентное утверждение (по Гёделю)
         * "Это утверждение не является теоремой данной системы"
         */
        public String getSelfReferentialStatement() {
            return "Это утверждение не является теоремой данной системы";
        }

    /**
     * Демонстрация парадокса лжеца
         */
        public void demonstrateLiarParadox() {
            String liarStatement = "Это утверждение ложно";

            System.out.println("Парадокс лжеца:");
            System.out.println("Утверждение: \"" + liarStatement + "\"");

            System.out.println("Если оно истинно, то оно ложно (противоречие)");
            System.out.println("Если оно ложно, то оно истинно (противоречие)");

            System.out.println("Это показывает ограниченность формальных систем!");
        }

    /**
     * Демонстрация теоремы Гёделя
         */
        public void demonstrateGodelsTheorem() {
            System.out.println("\nТеорема Гёделя о неполноте:");
            System.out.println("Любая достаточно мощная формальная система");
            System.out.println("либо неполна (содержит недоказуемые истинные утверждения),");
            System.out.println("либо противоречива (содержит ложные теоремы).");

            String selfRef = getSelfReferentialStatement();
            System.out.println("\nСамореферентное утверждение:");
            System.out.println("\"" + selfRef + "\"");

            System.out.println("\nЕсли система полна и непротиворечива:");
            System.out.println("- Если это утверждение истинно, оно должно быть теоремой");
            System.out.println("- Но оно утверждает, что не является теоремой (противоречие)");
            System.out.println("- Следовательно, система либо неполна, либо противоречива");
        }
    }

    /**
     * Демонстрация теоремы Гёделя
     */
    public static void demonstrateIncompleteness() {
        FormalSystem system = new FormalSystem();

        system.demonstrateLiarParadox();
        system.demonstrateGodelsTheorem();

        System.out.println("\nВыводы:");
        System.out.println("1. Математика не может быть полностью формализована");
        System.out.println("2. Существуют истинные математические утверждения, недоказуемые в данной системе");
        System.out.println("3. Компьютерные программы не могут доказать свою собственную корректность");
    }
}
```


## Решение проблем

- **Сложно оценить сложность алгоритма** — см. раздел «Вычислительная сложность»; учитывать циклы, рекурсию и размер входа.
- **Неясно, какая структура данных подходит** — см. разделы «Алгоритмы и структуры данных» и «Основные структуры данных»; сопоставить операции и их стоимость.
- **Переход к практике** — см. смежные документы: [алгоритмы](../../algorithms/), [структуры данных](../../algorithms/), [[networks-basics|сети]], [[operating-systems-basics|ОС]].

## Частые вопросы

- **Что входит в Computer Science?** Теория алгоритмов и структур данных, архитектура ЭВМ, ОС, сети, базы данных, криптография, теория вычислений; см. «Введение» в документе.
- **Чем классы сложности P и NP отличаются?** P — задачи, решаемые за полиномиальное время; NP — проверка решения за полиномиальное время; см. «Вычислительная сложность».
- **См. также:** разделы «Полезные ссылки», «Алгоритмы и структуры данных» и подразделы по сетям и ОС в документе.


## Заключение

**Computer Science** — это обширная и быстро развивающаяся область знания. Она объединяет теоретические основы, практические алгоритмы и инженерные решения для создания современных вычислительных систем.

### Ключевые концепции

1. **Вычислительная сложность**: Классы P, `NP`, экспоненциальная сложность
2. **Алгоритмы и структуры данных**: Эффективные решения типовых задач
3. **Архитектура компьютеров**: От процессоров до распределенных систем
4. **Операционные системы**: Управление ресурсами и абстракции
5. **Базы данных**: Хранение и обработка структурированных данных
6. **Компьютерные сети**: Коммуникации и распределенные вычисления
7. **Безопасность**: Криптография и защита систем
8. **Теория вычислений**: Границы возможного в вычислениях

### Важность изучения `CS`

- **Фундаментальные знания**: Понимание принципов работы компьютеров
- **Практические навыки**: Решение реальных задач эффективными способами
- **Критическое мышление**: Анализ и оптимизация алгоритмов
- **Инновации**: Создание новых технологий и решений
- **Этика**: Понимание социальных последствий технологий

### Рекомендации по изучению

1. **Начинайте с основ**: Изучите базовые алгоритмы и структуры данных
2. **Практикуйте регулярно**: Решайте задачи на платформах типа **LeetCode**
3. **Читаите книги**: "**Introduction** to **Algorithms**" (CLRS), "**Computer Science**: An **Interdisciplinary Approach**"
4. **Изучайте языки**: **Java**, **Python**, C++ для разных типов задач
5. **Следите за развитием**: `CS` - быстро меняющаяся область
6. **Комбинируйте теорию и практику**: Понимайте почему и как работают алгоритмы

**Computer Science** — это не просто программирование, это способ мышления и решения проблем!
