# Алгоритмы и Структуры Данных

Комплексное руководство по алгоритмам, структурам данных и их реализации на Java. От базовых структур до продвинутых алгоритмов оптимизации.

## Основные Структуры Данных

### Массивы и Списки

#### Динамические Массивы (ArrayList)
```java
public class DynamicArray<T> {
    private Object[] elements;
    private int size;
    private static final int DEFAULT_CAPACITY = 10;

    public DynamicArray() {
        elements = new Object[DEFAULT_CAPACITY];
        size = 0;
    }

    public void add(T element) {
        ensureCapacity();
        elements[size++] = element;
    }

    public T get(int index) {
        checkIndex(index);
        return (T) elements[index];
    }

    public void remove(int index) {
        checkIndex(index);
        System.arraycopy(elements, index + 1, elements, index, size - index - 1);
        elements[--size] = null; // Prevent memory leak
    }

    private void ensureCapacity() {
        if (size == elements.length) {
            int newCapacity = elements.length * 2;
            elements = Arrays.copyOf(elements, newCapacity);
        }
    }

    private void checkIndex(int index) {
        if (index < 0 || index >= size) {
            throw new IndexOutOfBoundsException("Index: " + index + ", Size: " + size);
        }
    }
}
```

#### Связные Списки
```java
public class LinkedList<T> {
    private Node<T> head;
    private Node<T> tail;
    private int size;

    private static class Node<T> {
        T data;
        Node<T> next;
        Node<T> prev;

        Node(T data) {
            this.data = data;
        }
    }

    public void addFirst(T element) {
        Node<T> newNode = new Node<>(element);
        if (head == null) {
            head = tail = newNode;
        } else {
            newNode.next = head;
            head.prev = newNode;
            head = newNode;
        }
        size++;
    }

    public void addLast(T element) {
        Node<T> newNode = new Node<>(element);
        if (tail == null) {
            head = tail = newNode;
        } else {
            tail.next = newNode;
            newNode.prev = tail;
            tail = newNode;
        }
        size++;
    }

    public T removeFirst() {
        if (head == null) throw new NoSuchElementException();
        T data = head.data;
        head = head.next;
        if (head != null) {
            head.prev = null;
        } else {
            tail = null;
        }
        size--;
        return data;
    }

    public T removeLast() {
        if (tail == null) throw new NoSuchElementException();
        T data = tail.data;
        tail = tail.prev;
        if (tail != null) {
            tail.next = null;
        } else {
            head = null;
        }
        size--;
        return data;
    }
}
```

### Стеки и Очереди

#### Стек (Stack)
```java
public class Stack<T> {
    private LinkedList<T> list = new LinkedList<>();

    public void push(T element) {
        list.addFirst(element);
    }

    public T pop() {
        if (isEmpty()) throw new EmptyStackException();
        return list.removeFirst();
    }

    public T peek() {
        if (isEmpty()) throw new EmptyStackException();
        return list.getFirst();
    }

    public boolean isEmpty() {
        return list.isEmpty();
    }

    public int size() {
        return list.size();
    }
}
```

#### Очередь (Queue)
```java
public class Queue<T> {
    private LinkedList<T> list = new LinkedList<>();

    public void enqueue(T element) {
        list.addLast(element);
    }

    public T dequeue() {
        if (isEmpty()) throw new NoSuchElementException();
        return list.removeFirst();
    }

    public T peek() {
        if (isEmpty()) throw new NoSuchElementException();
        return list.getFirst();
    }

    public boolean isEmpty() {
        return list.isEmpty();
    }

    public int size() {
        return list.size();
    }
}
```

#### Двусторонняя Очередь (Deque)
```java
public class Deque<T> {
    private LinkedList<T> list = new LinkedList<>();

    public void addFirst(T element) {
        list.addFirst(element);
    }

    public void addLast(T element) {
        list.addLast(element);
    }

    public T removeFirst() {
        return list.removeFirst();
    }

    public T removeLast() {
        return list.removeLast();
    }

    public T peekFirst() {
        return list.getFirst();
    }

    public T peekLast() {
        return list.getLast();
    }

    public boolean isEmpty() {
        return list.isEmpty();
    }

    public int size() {
        return list.size();
    }
}
```

### Деревья

#### Бинарное Дерево Поиска (BST)
```java
public class BinarySearchTree<T extends Comparable<T>> {
    private Node root;

    private class Node {
        T data;
        Node left, right;

        Node(T data) {
            this.data = data;
        }
    }

    public void insert(T data) {
        root = insertRec(root, data);
    }

    private Node insertRec(Node root, T data) {
        if (root == null) {
            root = new Node(data);
            return root;
        }

        if (data.compareTo(root.data) < 0) {
            root.left = insertRec(root.left, data);
        } else if (data.compareTo(root.data) > 0) {
            root.right = insertRec(root.right, data);
        }

        return root;
    }

    public boolean search(T data) {
        return searchRec(root, data);
    }

    private boolean searchRec(Node root, T data) {
        if (root == null) return false;

        if (data.compareTo(root.data) == 0) return true;

        return data.compareTo(root.data) < 0
            ? searchRec(root.left, data)
            : searchRec(root.right, data);
    }

    public void delete(T data) {
        root = deleteRec(root, data);
    }

    private Node deleteRec(Node root, T data) {
        if (root == null) return root;

        if (data.compareTo(root.data) < 0) {
            root.left = deleteRec(root.left, data);
        } else if (data.compareTo(root.data) > 0) {
            root.right = deleteRec(root.right, data);
        } else {
            // Node with only one child or no child
            if (root.left == null) return root.right;
            else if (root.right == null) return root.left;

            // Node with two children: Get the inorder successor
            root.data = minValue(root.right);

            // Delete the inorder successor
            root.right = deleteRec(root.right, root.data);
        }

        return root;
    }

    private T minValue(Node root) {
        T minv = root.data;
        while (root.left != null) {
            minv = root.left.data;
            root = root.left;
        }
        return minv;
    }
}
```

#### AVL Дерево (Самобалансирующееся)
```java
public class AVLTree<T extends Comparable<T>> {
    private Node root;

    private class Node {
        T data;
        Node left, right;
        int height;

        Node(T data) {
            this.data = data;
            this.height = 1;
        }
    }

    private int height(Node node) {
        return node == null ? 0 : node.height;
    }

    private int getBalance(Node node) {
        return node == null ? 0 : height(node.left) - height(node.right);
    }

    private Node rightRotate(Node y) {
        Node x = y.left;
        Node T2 = x.right;

        x.right = y;
        y.left = T2;

        y.height = Math.max(height(y.left), height(y.right)) + 1;
        x.height = Math.max(height(x.left), height(x.right)) + 1;

        return x;
    }

    private Node leftRotate(Node x) {
        Node y = x.right;
        Node T2 = y.left;

        y.left = x;
        x.right = T2;

        x.height = Math.max(height(x.left), height(x.right)) + 1;
        y.height = Math.max(height(y.left), height(y.right)) + 1;

        return y;
    }

    public void insert(T data) {
        root = insertRec(root, data);
    }

    private Node insertRec(Node node, T data) {
        if (node == null) return new Node(data);

        if (data.compareTo(node.data) < 0) {
            node.left = insertRec(node.left, data);
        } else if (data.compareTo(node.data) > 0) {
            node.right = insertRec(node.right, data);
        } else {
            return node; // Duplicate data not allowed
        }

        node.height = 1 + Math.max(height(node.left), height(node.right));

        int balance = getBalance(node);

        // Left Left Case
        if (balance > 1 && data.compareTo(node.left.data) < 0) {
            return rightRotate(node);
        }

        // Right Right Case
        if (balance < -1 && data.compareTo(node.right.data) > 0) {
            return leftRotate(node);
        }

        // Left Right Case
        if (balance > 1 && data.compareTo(node.left.data) > 0) {
            node.left = leftRotate(node.left);
            return rightRotate(node);
        }

        // Right Left Case
        if (balance < -1 && data.compareTo(node.right.data) < 0) {
            node.right = rightRotate(node.right);
            return leftRotate(node);
        }

        return node;
    }
}
```

### Хеш-таблицы

#### Простая Хеш-таблица
```java
public class HashTable<K, V> {
    private static final int DEFAULT_CAPACITY = 16;
    private static final float LOAD_FACTOR = 0.75f;

    private Entry<K, V>[] table;
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
    public HashTable() {
        table = new Entry[DEFAULT_CAPACITY];
        size = 0;
    }

    public void put(K key, V value) {
        int hash = hash(key);
        int index = hash % table.length;

        Entry<K, V> entry = table[index];
        while (entry != null) {
            if (entry.key.equals(key)) {
                entry.value = value;
                return;
            }
            entry = entry.next;
        }

        // Key not found, add new entry
        Entry<K, V> newEntry = new Entry<>(key, value, table[index]);
        table[index] = newEntry;
        size++;

        if (size > table.length * LOAD_FACTOR) {
            resize();
        }
    }

    public V get(K key) {
        int hash = hash(key);
        int index = hash % table.length;

        Entry<K, V> entry = table[index];
        while (entry != null) {
            if (entry.key.equals(key)) {
                return entry.value;
            }
            entry = entry.next;
        }
        return null;
    }

    public boolean remove(K key) {
        int hash = hash(key);
        int index = hash % table.length;

        Entry<K, V> entry = table[index];
        Entry<K, V> prev = null;

        while (entry != null) {
            if (entry.key.equals(key)) {
                if (prev == null) {
                    table[index] = entry.next;
                } else {
                    prev.next = entry.next;
                }
                size--;
                return true;
            }
            prev = entry;
            entry = entry.next;
        }
        return false;
    }

    private int hash(K key) {
        return key == null ? 0 : Math.abs(key.hashCode());
    }

    @SuppressWarnings("unchecked")
    private void resize() {
        Entry<K, V>[] oldTable = table;
        table = new Entry[table.length * 2];

        for (Entry<K, V> entry : oldTable) {
            while (entry != null) {
                int hash = hash(entry.key);
                int index = hash % table.length;

                Entry<K, V> next = entry.next;
                entry.next = table[index];
                table[index] = entry;

                entry = next;
            }
        }
    }
}
```

### Графы

#### Представление Графов
```java
public class Graph {
    private Map<Integer, List<Integer>> adjacencyList;

    public Graph() {
        adjacencyList = new HashMap<>();
    }

    public void addVertex(int vertex) {
        adjacencyList.putIfAbsent(vertex, new ArrayList<>());
    }

    public void addEdge(int source, int destination) {
        addVertex(source);
        addVertex(destination);
        adjacencyList.get(source).add(destination);
        // For undirected graph, add reverse edge
        // adjacencyList.get(destination).add(source);
    }

    public List<Integer> getNeighbors(int vertex) {
        return adjacencyList.getOrDefault(vertex, new ArrayList<>());
    }

    public Set<Integer> getVertices() {
        return adjacencyList.keySet();
    }
}
```

## Алгоритмы Сортировки

### Сравнительные Сортировки

#### Быстрая Сортировка (Quick Sort)
```java
public class QuickSort {
    public static void quickSort(int[] arr, int low, int high) {
        if (low < high) {
            int pi = partition(arr, low, high);

            quickSort(arr, low, pi - 1);
            quickSort(arr, pi + 1, high);
        }
    }

    private static int partition(int[] arr, int low, int high) {
        int pivot = arr[high];
        int i = (low - 1);

        for (int j = low; j < high; j++) {
            if (arr[j] < pivot) {
                i++;
                swap(arr, i, j);
            }
        }

        swap(arr, i + 1, high);
        return i + 1;
    }

    private static void swap(int[] arr, int i, int j) {
        int temp = arr[i];
        arr[i] = arr[j];
        arr[j] = temp;
    }
}
```

#### Сортировка Слиянием (Merge Sort)
```java
public class MergeSort {
    public static void mergeSort(int[] arr, int left, int right) {
        if (left < right) {
            int middle = (left + right) / 2;

            mergeSort(arr, left, middle);
            mergeSort(arr, middle + 1, right);

            merge(arr, left, middle, right);
        }
    }

    private static void merge(int[] arr, int left, int middle, int right) {
        int n1 = middle - left + 1;
        int n2 = right - middle;

        int[] leftArr = new int[n1];
        int[] rightArr = new int[n2];

        System.arraycopy(arr, left, leftArr, 0, n1);
        System.arraycopy(arr, middle + 1, rightArr, 0, n2);

        int i = 0, j = 0, k = left;

        while (i < n1 && j < n2) {
            if (leftArr[i] <= rightArr[j]) {
                arr[k++] = leftArr[i++];
            } else {
                arr[k++] = rightArr[j++];
            }
        }

        while (i < n1) {
            arr[k++] = leftArr[i++];
        }

        while (j < n2) {
            arr[k++] = rightArr[j++];
        }
    }
}
```

#### Сортировка Кучей (Heap Sort)
```java
public class HeapSort {
    public static void heapSort(int[] arr) {
        int n = arr.length;

        // Build heap (rearrange array)
        for (int i = n / 2 - 1; i >= 0; i--) {
            heapify(arr, n, i);
        }

        // One by one extract an element from heap
        for (int i = n - 1; i > 0; i--) {
            // Move current root to end
            swap(arr, 0, i);

            // call max heapify on the reduced heap
            heapify(arr, i, 0);
        }
    }

    private static void heapify(int[] arr, int n, int i) {
        int largest = i;
        int left = 2 * i + 1;
        int right = 2 * i + 2;

        // If left child is larger than root
        if (left < n && arr[left] > arr[largest]) {
            largest = left;
        }

        // If right child is larger than largest so far
        if (right < n && arr[right] > arr[largest]) {
            largest = right;
        }

        // If largest is not root
        if (largest != i) {
            swap(arr, i, largest);

            // Recursively heapify the affected sub-tree
            heapify(arr, n, largest);
        }
    }

    private static void swap(int[] arr, int i, int j) {
        int temp = arr[i];
        arr[i] = arr[j];
        arr[j] = temp;
    }
}
```

### Линейные Сортировки

#### Сортировка Подсчетом (Counting Sort)
```java
public class CountingSort {
    public static void countingSort(int[] arr) {
        int max = Arrays.stream(arr).max().orElse(0);
        int min = Arrays.stream(arr).min().orElse(0);
        int range = max - min + 1;

        int[] count = new int[range];
        int[] output = new int[arr.length];

        // Count occurrences
        for (int num : arr) {
            count[num - min]++;
        }

        // Cumulative count
        for (int i = 1; i < count.length; i++) {
            count[i] += count[i - 1];
        }

        // Build output array
        for (int i = arr.length - 1; i >= 0; i--) {
            output[count[arr[i] - min] - 1] = arr[i];
            count[arr[i] - min]--;
        }

        // Copy output to original array
        System.arraycopy(output, 0, arr, 0, arr.length);
    }
}
```

#### Поразрядная Сортировка (Radix Sort)
```java
public class RadixSort {
    public static void radixSort(int[] arr) {
        int max = Arrays.stream(arr).max().orElse(0);

        // Do counting sort for every digit
        for (int exp = 1; max / exp > 0; exp *= 10) {
            countingSortByDigit(arr, exp);
        }
    }

    private static void countingSortByDigit(int[] arr, int exp) {
        int n = arr.length;
        int[] output = new int[n];
        int[] count = new int[10];

        // Count occurrences of digits
        for (int num : arr) {
            count[(num / exp) % 10]++;
        }

        // Cumulative count
        for (int i = 1; i < 10; i++) {
            count[i] += count[i - 1];
        }

        // Build output array
        for (int i = n - 1; i >= 0; i--) {
            int digit = (arr[i] / exp) % 10;
            output[count[digit] - 1] = arr[i];
            count[digit]--;
        }

        // Copy output to original array
        System.arraycopy(output, 0, arr, 0, n);
    }
}
```

## Алгоритмы Поиска

### Линейный Поиск
```java
public class LinearSearch {
    public static int linearSearch(int[] arr, int target) {
        for (int i = 0; i < arr.length; i++) {
            if (arr[i] == target) {
                return i;
            }
        }
        return -1;
    }

    // Generic version
    public static <T> int linearSearch(T[] arr, T target) {
        for (int i = 0; i < arr.length; i++) {
            if (arr[i] != null && arr[i].equals(target)) {
                return i;
            }
        }
        return -1;
    }
}
```

### Бинарный Поиск
```java
public class BinarySearch {
    public static int binarySearch(int[] arr, int target) {
        return binarySearch(arr, target, 0, arr.length - 1);
    }

    private static int binarySearch(int[] arr, int target, int left, int right) {
        if (left > right) {
            return -1;
        }

        int mid = left + (right - left) / 2;

        if (arr[mid] == target) {
            return mid;
        } else if (arr[mid] > target) {
            return binarySearch(arr, target, left, mid - 1);
        } else {
            return binarySearch(arr, target, mid + 1, right);
        }
    }

    // Iterative version
    public static int binarySearchIterative(int[] arr, int target) {
        int left = 0;
        int right = arr.length - 1;

        while (left <= right) {
            int mid = left + (right - left) / 2;

            if (arr[mid] == target) {
                return mid;
            } else if (arr[mid] > target) {
                right = mid - 1;
            } else {
                left = mid + 1;
            }
        }

        return -1;
    }
}
```

### Интерполяционный Поиск
```java
public class InterpolationSearch {
    public static int interpolationSearch(int[] arr, int target) {
        int low = 0;
        int high = arr.length - 1;

        while (low <= high && target >= arr[low] && target <= arr[high]) {
            if (low == high) {
                if (arr[low] == target) return low;
                return -1;
            }

            // Estimate position
            int pos = low + (((high - low) / (arr[high] - arr[low])) * (target - arr[low]));

            if (arr[pos] == target) {
                return pos;
            }

            if (arr[pos] < target) {
                low = pos + 1;
            } else {
                high = pos - 1;
            }
        }
        return -1;
    }
}
```

## Графовые Алгоритмы

### Обход Графов

#### Поиск в Глубину (DFS)
```java
public class DFS {
    public static void dfs(Graph graph, int startVertex) {
        Set<Integer> visited = new HashSet<>();
        dfsRecursive(graph, startVertex, visited);
    }

    private static void dfsRecursive(Graph graph, int vertex, Set<Integer> visited) {
        visited.add(vertex);
        System.out.println(vertex);

        for (int neighbor : graph.getNeighbors(vertex)) {
            if (!visited.contains(neighbor)) {
                dfsRecursive(graph, neighbor, visited);
            }
        }
    }

    // Iterative DFS
    public static void dfsIterative(Graph graph, int startVertex) {
        Set<Integer> visited = new HashSet<>();
        Stack<Integer> stack = new Stack<>();

        stack.push(startVertex);

        while (!stack.isEmpty()) {
            int vertex = stack.pop();

            if (!visited.contains(vertex)) {
                visited.add(vertex);
                System.out.println(vertex);

                // Push neighbors in reverse order to maintain order
                List<Integer> neighbors = new ArrayList<>(graph.getNeighbors(vertex));
                Collections.reverse(neighbors);

                for (int neighbor : neighbors) {
                    if (!visited.contains(neighbor)) {
                        stack.push(neighbor);
                    }
                }
            }
        }
    }
}
```

#### Поиск в Ширину (BFS)
```java
public class BFS {
    public static void bfs(Graph graph, int startVertex) {
        Set<Integer> visited = new HashSet<>();
        Queue<Integer> queue = new LinkedList<>();

        visited.add(startVertex);
        queue.offer(startVertex);

        while (!queue.isEmpty()) {
            int vertex = queue.poll();
            System.out.println(vertex);

            for (int neighbor : graph.getNeighbors(vertex)) {
                if (!visited.contains(neighbor)) {
                    visited.add(neighbor);
                    queue.offer(neighbor);
                }
            }
        }
    }

    // BFS with levels
    public static List<List<Integer>> bfsLevels(Graph graph, int startVertex) {
        List<List<Integer>> levels = new ArrayList<>();
        Set<Integer> visited = new HashSet<>();
        Queue<Integer> queue = new LinkedList<>();

        visited.add(startVertex);
        queue.offer(startVertex);

        while (!queue.isEmpty()) {
            int size = queue.size();
            List<Integer> level = new ArrayList<>();

            for (int i = 0; i < size; i++) {
                int vertex = queue.poll();
                level.add(vertex);

                for (int neighbor : graph.getNeighbors(vertex)) {
                    if (!visited.contains(neighbor)) {
                        visited.add(neighbor);
                        queue.offer(neighbor);
                    }
                }
            }

            levels.add(level);
        }

        return levels;
    }
}
```

### Кратчайшие Пути

#### Алгоритм Дейкстры
```java
public class Dijkstra {
    public static Map<Integer, Integer> shortestPath(Graph graph, int startVertex) {
        Map<Integer, Integer> distances = new HashMap<>();
        PriorityQueue<Node> pq = new PriorityQueue<>(Comparator.comparingInt(n -> n.distance));
        Set<Integer> visited = new HashSet<>();

        // Initialize distances
        for (int vertex : graph.getVertices()) {
            distances.put(vertex, vertex == startVertex ? 0 : Integer.MAX_VALUE);
        }

        pq.offer(new Node(startVertex, 0));

        while (!pq.isEmpty()) {
            Node current = pq.poll();
            int vertex = current.vertex;

            if (visited.contains(vertex)) continue;
            visited.add(vertex);

            for (int neighbor : graph.getNeighbors(vertex)) {
                if (visited.contains(neighbor)) continue;

                // Assuming unit weights, modify for weighted graphs
                int newDistance = distances.get(vertex) + 1;

                if (newDistance < distances.get(neighbor)) {
                    distances.put(neighbor, newDistance);
                    pq.offer(new Node(neighbor, newDistance));
                }
            }
        }

        return distances;
    }

    private static class Node {
        int vertex;
        int distance;

        Node(int vertex, int distance) {
            this.vertex = vertex;
            this.distance = distance;
        }
    }
}
```

#### Алгоритм Беллмана-Форда
```java
public class BellmanFord {
    public static Map<Integer, Integer> shortestPath(WeightedGraph graph, int startVertex) {
        Map<Integer, Integer> distances = new HashMap<>();
        Map<Integer, Integer> predecessors = new HashMap<>();

        // Initialize distances
        for (int vertex : graph.getVertices()) {
            distances.put(vertex, vertex == startVertex ? 0 : Integer.MAX_VALUE);
            predecessors.put(vertex, null);
        }

        // Relax edges |V| - 1 times
        for (int i = 1; i < graph.getVertices().size(); i++) {
            for (WeightedEdge edge : graph.getEdges()) {
                int u = edge.source;
                int v = edge.destination;
                int weight = edge.weight;

                if (distances.get(u) != Integer.MAX_VALUE &&
                    distances.get(u) + weight < distances.get(v)) {
                    distances.put(v, distances.get(u) + weight);
                    predecessors.put(v, u);
                }
            }
        }

        // Check for negative cycles
        for (WeightedEdge edge : graph.getEdges()) {
            int u = edge.source;
            int v = edge.destination;
            int weight = edge.weight;

            if (distances.get(u) != Integer.MAX_VALUE &&
                distances.get(u) + weight < distances.get(v)) {
                throw new IllegalArgumentException("Graph contains negative cycle");
            }
        }

        return distances;
    }
}
```

### Минимальные Остовные Деревья

#### Алгоритм Краскала
```java
public class Kruskal {
    public static List<WeightedEdge> kruskalMST(WeightedGraph graph) {
        List<WeightedEdge> mst = new ArrayList<>();
        UnionFind uf = new UnionFind(graph.getVertices().size());

        // Sort edges by weight
        List<WeightedEdge> edges = new ArrayList<>(graph.getEdges());
        edges.sort(Comparator.comparingInt(e -> e.weight));

        for (WeightedEdge edge : edges) {
            int u = edge.source;
            int v = edge.destination;

            if (uf.find(u) != uf.find(v)) {
                mst.add(edge);
                uf.union(u, v);
            }
        }

        return mst;
    }

    private static class UnionFind {
        private int[] parent;
        private int[] rank;

        UnionFind(int size) {
            parent = new int[size];
            rank = new int[size];
            for (int i = 0; i < size; i++) {
                parent[i] = i;
                rank[i] = 0;
            }
        }

        int find(int x) {
            if (parent[x] != x) {
                parent[x] = find(parent[x]); // Path compression
            }
            return parent[x];
        }

        void union(int x, int y) {
            int xRoot = find(x);
            int yRoot = find(y);

            if (xRoot == yRoot) return;

            // Union by rank
            if (rank[xRoot] < rank[yRoot]) {
                parent[xRoot] = yRoot;
            } else if (rank[xRoot] > rank[yRoot]) {
                parent[yRoot] = xRoot;
            } else {
                parent[yRoot] = xRoot;
                rank[xRoot]++;
            }
        }
    }
}
```

#### Алгоритм Прима
```java
public class Prim {
    public static List<WeightedEdge> primMST(WeightedGraph graph) {
        List<WeightedEdge> mst = new ArrayList<>();
        Set<Integer> visited = new HashSet<>();
        PriorityQueue<WeightedEdge> pq = new PriorityQueue<>(Comparator.comparingInt(e -> e.weight));

        // Start from vertex 0
        visited.add(0);

        // Add all edges from starting vertex
        for (WeightedEdge edge : graph.getEdgesFromVertex(0)) {
            pq.offer(edge);
        }

        while (!pq.isEmpty() && visited.size() < graph.getVertices().size()) {
            WeightedEdge edge = pq.poll();

            int u = edge.source;
            int v = edge.destination;

            if (visited.contains(u) && visited.contains(v)) continue;

            mst.add(edge);

            // Add the new vertex to visited
            int newVertex = visited.contains(u) ? v : u;
            visited.add(newVertex);

            // Add all edges from the new vertex
            for (WeightedEdge nextEdge : graph.getEdgesFromVertex(newVertex)) {
                if (!visited.contains(nextEdge.source) || !visited.contains(nextEdge.destination)) {
                    pq.offer(nextEdge);
                }
            }
        }

        return mst;
    }
}
```

## Динамическое Программирование

### Задача о Рюкзаке (0/1 Knapsack)
```java
public class Knapsack {
    public static int knapsack(int[] weights, int[] values, int capacity) {
        int n = weights.length;
        int[][] dp = new int[n + 1][capacity + 1];

        for (int i = 0; i <= n; i++) {
            for (int w = 0; w <= capacity; w++) {
                if (i == 0 || w == 0) {
                    dp[i][w] = 0;
                } else if (weights[i - 1] <= w) {
                    dp[i][w] = Math.max(
                        values[i - 1] + dp[i - 1][w - weights[i - 1]], // Include item
                        dp[i - 1][w] // Exclude item
                    );
                } else {
                    dp[i][w] = dp[i - 1][w];
                }
            }
        }

        return dp[n][capacity];
    }

    // Space optimized version
    public static int knapsackOptimized(int[] weights, int[] values, int capacity) {
        int n = weights.length;
        int[] dp = new int[capacity + 1];

        for (int i = 0; i < n; i++) {
            for (int w = capacity; w >= weights[i]; w--) {
                dp[w] = Math.max(dp[w], dp[w - weights[i]] + values[i]);
            }
        }

        return dp[capacity];
    }
}
```

### Наибольшая Общая Подпоследовательность (LCS)
```java
public class LCS {
    public static int longestCommonSubsequence(String text1, String text2) {
        int m = text1.length();
        int n = text2.length();
        int[][] dp = new int[m + 1][n + 1];

        for (int i = 0; i <= m; i++) {
            for (int j = 0; j <= n; j++) {
                if (i == 0 || j == 0) {
                    dp[i][j] = 0;
                } else if (text1.charAt(i - 1) == text2.charAt(j - 1)) {
                    dp[i][j] = dp[i - 1][j - 1] + 1;
                } else {
                    dp[i][j] = Math.max(dp[i - 1][j], dp[i][j - 1]);
                }
            }
        }

        return dp[m][n];
    }

    // Reconstruct LCS
    public static String getLCS(String text1, String text2) {
        int m = text1.length();
        int n = text2.length();
        int[][] dp = new int[m + 1][n + 1];

        // Fill dp table
        for (int i = 0; i <= m; i++) {
            for (int j = 0; j <= n; j++) {
                if (i == 0 || j == 0) {
                    dp[i][j] = 0;
                } else if (text1.charAt(i - 1) == text2.charAt(j - 1)) {
                    dp[i][j] = dp[i - 1][j - 1] + 1;
                } else {
                    dp[i][j] = Math.max(dp[i - 1][j], dp[i][j - 1]);
                }
            }
        }

        // Reconstruct LCS
        StringBuilder lcs = new StringBuilder();
        int i = m, j = n;
        while (i > 0 && j > 0) {
            if (text1.charAt(i - 1) == text2.charAt(j - 1)) {
                lcs.append(text1.charAt(i - 1));
                i--;
                j--;
            } else if (dp[i - 1][j] > dp[i][j - 1]) {
                i--;
            } else {
                j--;
            }
        }

        return lcs.reverse().toString();
    }
}
```

### Задача о Размене Монет
```java
public class CoinChange {
    public static int coinChange(int[] coins, int amount) {
        int[] dp = new int[amount + 1];
        Arrays.fill(dp, amount + 1);
        dp[0] = 0;

        for (int i = 1; i <= amount; i++) {
            for (int coin : coins) {
                if (coin <= i) {
                    dp[i] = Math.min(dp[i], dp[i - coin] + 1);
                }
            }
        }

        return dp[amount] > amount ? -1 : dp[amount];
    }

    // Count number of ways
    public static int coinChangeWays(int[] coins, int amount) {
        int[] dp = new int[amount + 1];
        dp[0] = 1;

        for (int coin : coins) {
            for (int i = coin; i <= amount; i++) {
                dp[i] += dp[i - coin];
            }
        }

        return dp[amount];
    }
}
```

## Жадные Алгоритмы

### Задача о Разрезании Палки
```java
public class RodCutting {
    public static int rodCutting(int[] prices, int length) {
        int[] dp = new int[length + 1];

        for (int i = 1; i <= length; i++) {
            int maxVal = Integer.MIN_VALUE;
            for (int j = 0; j < i; j++) {
                maxVal = Math.max(maxVal, prices[j] + dp[i - j - 1]);
            }
            dp[i] = maxVal;
        }

        return dp[length];
    }

    // With cuts tracking
    public static Result rodCuttingWithCuts(int[] prices, int length) {
        int[] dp = new int[length + 1];
        int[] cuts = new int[length + 1];

        for (int i = 1; i <= length; i++) {
            int maxVal = Integer.MIN_VALUE;
            for (int j = 0; j < i; j++) {
                if (maxVal < prices[j] + dp[i - j - 1]) {
                    maxVal = prices[j] + dp[i - j - 1];
                    cuts[i] = j + 1;
                }
            }
            dp[i] = maxVal;
        }

        List<Integer> cutPoints = new ArrayList<>();
        int remaining = length;
        while (remaining > 0) {
            cutPoints.add(cuts[remaining]);
            remaining -= cuts[remaining];
        }

        return new Result(dp[length], cutPoints);
    }

    public static class Result {
        public final int maxValue;
        public final List<Integer> cuts;

        public Result(int maxValue, List<Integer> cuts) {
            this.maxValue = maxValue;
            this.cuts = cuts;
        }
    }
}
```

### Задача о Выборе Работ
```java
public class JobScheduling {
    public static class Job {
        int id, deadline, profit;

        Job(int id, int deadline, int profit) {
            this.id = id;
            this.deadline = deadline;
            this.profit = profit;
        }
    }

    public static List<Job> jobScheduling(Job[] jobs, int maxDeadline) {
        // Sort jobs by profit in descending order
        Arrays.sort(jobs, (a, b) -> b.profit - a.profit);

        // Track available time slots
        boolean[] slots = new boolean[maxDeadline];
        List<Job> scheduledJobs = new ArrayList<>();

        for (Job job : jobs) {
            // Find a free slot for this job (starting from the last possible slot)
            for (int j = Math.min(maxDeadline - 1, job.deadline - 1); j >= 0; j--) {
                if (!slots[j]) {
                    slots[j] = true;
                    scheduledJobs.add(job);
                    break;
                }
            }
        }

        return scheduledJobs;
    }

    public static int maxProfit(Job[] jobs, int maxDeadline) {
        List<Job> scheduled = jobScheduling(jobs, maxDeadline);
        return scheduled.stream().mapToInt(job -> job.profit).sum();
    }
}
```

## Сложность Алгоритмов

### Big O Нотация

#### Временная Сложность
| Алгоритм | Лучший случай | Средний случай | Худший случай |
|----------|---------------|----------------|----------------|
| Быстрая сортировка | O(n log n) | O(n log n) | O(n²) |
| Сортировка слиянием | O(n log n) | O(n log n) | O(n log n) |
| Сортировка кучей | O(n log n) | O(n log n) | O(n log n) |
| Линейный поиск | O(1) | O(n) | O(n) |
| Бинарный поиск | O(1) | O(log n) | O(log n) |
| DFS/BFS | O(V + E) | O(V + E) | O(V + E) |
| Дейкстра | O((V + E) log V) | O((V + E) log V) | O((V + E) log V) |
| Беллман-Форд | O(V × E) | O(V × E) | O(V × E) |
| Краскал | O(E log E) | O(E log E) | O(E log E) |
| Прим | O((V + E) log V) | O((V + E) log V) | O((V + E) log V) |

#### Пространственная Сложность
| Структура/Алгоритм | Пространственная сложность |
|-------------------|---------------------------|
| Массив | O(n) |
| Связный список | O(n) |
| Стек/Очередь | O(n) |
| Бинарное дерево | O(n) |
| AVL дерево | O(n) |
| Хеш-таблица | O(n) |
| Граф (список смежности) | O(V + E) |
| Быстрая сортировка | O(log n) |
| Сортировка слиянием | O(n) |
| Динамическое программирование | O(n) - O(n²) |

### Анализ Производительности

#### Практические Соображения
```java
public class PerformanceAnalysis {

    // Measure algorithm performance
    public static long measureTime(Runnable algorithm) {
        long startTime = System.nanoTime();
        algorithm.run();
        long endTime = System.nanoTime();
        return endTime - startTime;
    }

    // Memory usage analysis
    public static long measureMemory(Runnable algorithm) {
        Runtime runtime = Runtime.getRuntime();
        System.gc(); // Run garbage collector

        long beforeMemory = runtime.totalMemory() - runtime.freeMemory();
        algorithm.run();
        long afterMemory = runtime.totalMemory() - runtime.freeMemory();

        return afterMemory - beforeMemory;
    }

    // Benchmark different sorting algorithms
    public static void benchmarkSorts() {
        int[] sizes = {1000, 10000, 100000};

        for (int size : sizes) {
            int[] arr1 = generateRandomArray(size);
            int[] arr2 = Arrays.copyOf(arr1, size);
            int[] arr3 = Arrays.copyOf(arr1, size);

            System.out.println("Array size: " + size);

            // Quick Sort
            long quickTime = measureTime(() -> QuickSort.quickSort(arr1, 0, size - 1));
            System.out.println("Quick Sort: " + quickTime / 1_000_000 + " ms");

            // Merge Sort
            long mergeTime = measureTime(() -> MergeSort.mergeSort(arr2, 0, size - 1));
            System.out.println("Merge Sort: " + mergeTime / 1_000_000 + " ms");

            // Heap Sort
            long heapTime = measureTime(() -> HeapSort.heapSort(arr3));
            System.out.println("Heap Sort: " + heapTime / 1_000_000 + " ms");

            System.out.println();
        }
    }

    private static int[] generateRandomArray(int size) {
        int[] arr = new int[size];
        Random random = new Random();
        for (int i = 0; i < size; i++) {
            arr[i] = random.nextInt(size * 10);
        }
        return arr;
    }
}
```

---

**Категория:** Algorithms & Data Structures
**Фокус:** Implementation, Analysis, Optimization
**Язык:** Java
