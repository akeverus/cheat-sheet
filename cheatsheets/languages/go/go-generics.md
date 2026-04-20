---
title: "Go: Generics"
description: "Полное руководство по Generics в Go: type parameters, constraints, type inference, практические примеры"
tags:
  - go
  - golang
  - generics
  - type-parameters
  - constraints
difficulty: "advanced"
prerequisites: ["go/go-basics.md"]
updated: "2026-02-06"
---

# Go: **Generics**

## Полезные ссылки

- [Go Generics Tutorial](https://go.dev/doc/tutorial/generics)
- [Go Generics Proposal](https://go.googlesource.com/proposal/+/refs/heads/master/design/43651-type-parameters.md)
- [Type Parameters Proposal](https://go.dev/blog/intro-generics)

## Содержание

- [Go: **Generics**](#go-generics)
- [Введение в **Generics**](#введение-в-generics)
  - [Преимущества **Generics**](#преимущества-generics)
  - [Когда использовать **Generics**](#когда-использовать-generics)
- [**Type Parameters**](#type-parameters)
  - [Базовый синтаксис](#базовый-синтаксис)
  - [Функции с несколькими **type parameters**](#функции-с-несколькими-type-parameters)
  - [Типы с **type parameters**](#типы-с-type-parameters)
- [**Constraints**](#constraints)
  - [Встроенные **constraints**](#встроенные-constraints)
  - [Кастомные **constraints**](#кастомные-constraints)
  - [**Constraints** с методами](#constraints-с-методами)
  - [Комбинированные **constraints**](#комбинированные-constraints)
- [**Type Inference**](#type-inference)
  - [Автоматический вывод типов](#автоматический-вывод-типов)
  - [Явное указание типов](#явное-указание-типов)
- [Практические примеры](#практические-примеры)
  - [Обобщенные коллекции](#обобщенные-коллекции)
  - [Обобщенные алгоритмы](#обобщенные-алгоритмы)
  - [Обобщенные структуры данных](#обобщенные-структуры-данных)
  - [Обобщенные функции сравнения](#обобщенные-функции-сравнения)
  - [Обобщенные функции поиска](#обобщенные-функции-поиска)
  - [Обобщенные функции преобразования](#обобщенные-функции-преобразования)
  - [Обобщенные функции сортировки](#обобщенные-функции-сортировки)
  - [Обобщенные функции группировки](#обобщенные-функции-группировки)
  - [Обобщенные функции объединения](#обобщенные-функции-объединения)
  - [Обобщенные функции уникальности](#обобщенные-функции-уникальности)
  - [Обобщенные структуры данных: **Set**](#обобщенные-структуры-данных-set)
  - [Обобщенные структуры данных: **Heap**](#обобщенные-структуры-данных-heap)
  - [Обобщенные структуры данных: **Linked List**](#обобщенные-структуры-данных-linked-list)
  - [Обобщенные структуры данных: **Tree**](#обобщенные-структуры-данных-tree)
  - [Практические примеры: Обобщенный кэш](#практические-примеры-обобщенный-кэш)
  - [Практические примеры: Обобщенный репозиторий](#практические-примеры-обобщенный-репозиторий)
  - [Практические примеры: **Generic** функциональные утилиты](#практические-примеры-generic-функциональные-утилиты)
  - [Практические примеры: **Generic** деревья](#практические-примеры-generic-деревья)
  - [Практические примеры: **Generic** графы](#практические-примеры-generic-графы)
  - [Практические примеры: **Generic** очереди с приоритетами](#практические-примеры-generic-очереди-с-приоритетами)
  - [Практические примеры: **Generic** кэш](#практические-примеры-generic-кэш)
  - [Практические примеры: **Generic** функциональные опции](#практические-примеры-generic-функциональные-опции)
- [Лучшие практики](#лучшие-практики)
  - [Практические примеры: **Generic** структуры данных](#практические-примеры-generic-структуры-данных)
  - [Практические примеры: **Generic** алгоритмы](#практические-примеры-generic-алгоритмы)
- [Troubleshooting](#troubleshooting)
- [FAQ](#faq)
- [Заключение](#заключение)
- [Дополнительные ресурсы](#дополнительные-ресурсы)

## Введение в **Generics**

**Generics** (**обобщения**) были добавлены в `Go 1.18` и позволяют писать код, который работает с различными типами данных, сохраняя типобезопасность.

### Преимущества **Generics**

1. **Типобезопасность** — проверка типов на этапе компиляции
2. **Переиспользование кода** — один код для различных типов
3. **Производительность** — нет накладных расходов **runtime**
4. **Читаемость** — более выразительный код

### Когда использовать **Generics**

**Generics** полезны для:**
- Функций, работающих с различными типами
- Структур данных (**списки, стеки, очереди**)
- Алгоритмов, работающих с различными типами

## **Type Parameters**

**Type Parameters** позволяют определять функции и типы, работающие с различными типами.

### Базовый синтаксис

```go
// Функция с type parameter
func Print[T any](value T) {
    fmt.Println(value)
}

// Использование
Print(42)        // int
Print("hello")   // string
Print(3.14)      // float64
```

### Функции с несколькими **type parameters**

```go
func Swap[T, U any](a T, b U) (U, T) {
    return b, a
}

// Использование
x, y := Swap(1, "hello")
// x = "hello", y = 1
```

### Типы с **type parameters**

```go
// Стек с type parameter
type Stack[T any] struct {
    items []T
}

func NewStack[T any]() *Stack[T] {
    return &Stack[T]{items: []T{}}
}

func (s *Stack[T]) Push(item T) {
    s.items = append(s.items, item)
}

func (s *Stack[T]) Pop() (T, bool) {
    if len(s.items) == 0 {
        var zero T
        return zero, false
    }
    item := s.items[len(s.items)-1]
    s.items = s.items[:len(s.items)-1]
    return item, true
}
```

## **Constraints**

**Constraints** ограничивают типы, которые могут быть использованы в качестве **type parameters**.

### Встроенные **constraints**

```go
// any - любой тип (эквивалент interface{})
func Print[T any](value T) {
    fmt.Println(value)
}

// comparable - типы, которые можно сравнивать
func Find[T comparable](slice []T, value T) int {
    for i, v := range slice {
        if v == value {
            return i
        }
    }
    return -1
}
```

### Кастомные **constraints**

```go
// Определение constraint
type Number interface {
    int | int8 | int16 | int32 | int64 |
    uint | uint8 | uint16 | uint32 | uint64 |
    float32 | float64
}

// Использование constraint
func Sum[T Number](numbers []T) T {
    var sum T
    for _, n := range numbers {
        sum += n
    }
    return sum
}
```

### **Constraints** с методами

```go
// Constraint с методами
type Stringer interface {
    String() string
}

func PrintString[T Stringer](value T) {
    fmt.Println(value.String())
}
```

### Комбинированные **constraints**

```go
// Constraint с типами и методами
type Numeric interface {
    Number
    String() string
}

func FormatNumber[T Numeric](value T) string {
    return value.String()
}
```

## **Type Inference**

**Type Inference** позволяет компилятору автоматически определять типы.

### Автоматический вывод типов

```go
// Типы выводятся автоматически
func Max[T comparable](a, b T) T {
    if a > b {
        return a
    }
    return b
}

// Использование без явного указания типов
result := Max(10, 20)  // T = int
result2 := Max(3.14, 2.71)  // T = float64
```

### Явное указание типов

```go
// Явное указание типов
result := Max[int](10, 20)
result2 := Max[float64](3.14, 2.71)
```

## Практические примеры

### Обобщенные коллекции

```go
// Обобщенный список
type List[T any] struct {
    items []T
}

func NewList[T any]() *List[T] {
    return &List[T]{items: []T{}}
}

func (l *List[T]) Add(item T) {
    l.items = append(l.items, item)
}

func (l *List[T]) Get(index int) (T, bool) {
    if index < 0 || index >= len(l.items) {
        var zero T
        return zero, false
    }
    return l.items[index], true
}

func (l *List[T]) Filter(fn func(T) bool) *List[T] {
    result := NewList[T]()
    for _, item := range l.items {
        if fn(item) {
            result.Add(item)
        }
    }
    return result
}
```

### Обобщенные алгоритмы

```go
// Map функция
func Map[T, U any](slice []T, fn func(T) U) []U {
    result := make([]U, len(slice))
    for i, v := range slice {
        result[i] = fn(v)
    }
    return result
}

// Filter функция
func Filter[T any](slice []T, fn func(T) bool) []T {
    var result []T
    for _, v := range slice {
        if fn(v) {
            result = append(result, v)
        }
    }
    return result
}

// Reduce функция
func Reduce[T, U any](slice []T, initial U, fn func(U, T) U) U {
    result := initial
    for _, v := range slice {
        result = fn(result, v)
    }
    return result
}
```

### Обобщенные структуры данных

```go
// Обобщенная очередь
type Queue[T any] struct {
    items []T
}

func NewQueue[T any]() *Queue[T] {
    return &Queue[T]{items: []T{}}
}

func (q *Queue[T]) Enqueue(item T) {
    q.items = append(q.items, item)
}

func (q *Queue[T]) Dequeue() (T, bool) {
    if len(q.items) == 0 {
        var zero T
        return zero, false
    }
    item := q.items[0]
    q.items = q.items[1:]
    return item, true
}
```

### Обобщенные функции сравнения

```go
// Max функция для comparable типов
func Max[T comparable](a, b T) T {
    if a > b {
        return a
    }
    return b
}

// Min функция
func Min[T comparable](a, b T) T {
    if a < b {
        return a
    }
    return b
}

// Использование
maxInt := Max(10, 20)           // int
maxFloat := Max(3.14, 2.71)     // float64
maxString := Max("a", "b")      // string
```

### Обобщенные функции поиска

```go
// Contains проверяет наличие элемента в слайсе
func Contains[T comparable](slice []T, value T) bool {
    for _, v := range slice {
        if v == value {
            return true
        }
    }
    return false
}

// Index возвращает индекс элемента
func Index[T comparable](slice []T, value T) int {
    for i, v := range slice {
        if v == value {
            return i
        }
    }
    return -1
}

// Count подсчитывает количество элементов
func Count[T comparable](slice []T, value T) int {
    count := 0
    for _, v := range slice {
        if v == value {
            count++
        }
    }
    return count
}
```

### Обобщенные функции преобразования

```go
// ToMap преобразует слайс в карту
func ToMap[T comparable, U any](slice []T, fn func(T) U) map[T]U {
    result := make(map[T]U)
    for _, v := range slice {
        result[v] = fn(v)
    }
    return result
}

// Keys возвращает ключи карты
func Keys[K comparable, V any](m map[K]V) []K {
    keys := make([]K, 0, len(m))
    for k := range m {
        keys = append(keys, k)
    }
    return keys
}

// Values возвращает значения карты
func Values[K comparable, V any](m map[K]V) []V {
    values := make([]V, 0, len(m))
    for _, v := range m {
        values = append(values, v)
    }
    return values
}
```

### Обобщенные функции сортировки

```go
// Sort сортирует слайс
func Sort[T comparable](slice []T, less func(T, T) bool) []T {
    result := make([]T, len(slice))
    copy(result, slice)

    sort.Slice(result, func(i, j int) bool {
        return less(result[i], result[j])
    })

    return result
}

// SortBy сортирует по ключу
func SortBy[T any, K comparable](slice []T, key func(T) K) []T {
    result := make([]T, len(slice))
    copy(result, slice)

    sort.Slice(result, func(i, j int) bool {
        return key(result[i]) < key(result[j])
    })

    return result
}
```

### Обобщенные функции группировки

```go
// GroupBy группирует элементы по ключу
func GroupBy[T any, K comparable](slice []T, key func(T) K) map[K][]T {
    result := make(map[K][]T)
    for _, v := range slice {
        k := key(v)
        result[k] = append(result[k], v)
    }
    return result
}

// Partition разделяет слайс на две части
func Partition[T any](slice []T, fn func(T) bool) ([]T, []T) {
    var truePart, falsePart []T
    for _, v := range slice {
        if fn(v) {
            truePart = append(truePart, v)
        } else {
            falsePart = append(falsePart, v)
        }
    }
    return truePart, falsePart
}
```

### Обобщенные функции объединения

```go
// Concat объединяет слайсы
func Concat[T any](slices ...[]T) []T {
    var result []T
    for _, slice := range slices {
        result = append(result, slice...)
    }
    return result
}

// Zip объединяет два слайса в пары
func Zip[T, U any](slice1 []T, slice2 []U) []Pair[T, U] {
    minLen := len(slice1)
    if len(slice2) < minLen {
        minLen = len(slice2)
    }

    result := make([]Pair[T, U], minLen)
    for i := 0; i < minLen; i++ {
        result[i] = Pair[T, U]{First: slice1[i], Second: slice2[i]}
    }
    return result
}

type Pair[T, U any] struct {
    First  T
    Second U
}
```

### Обобщенные функции уникальности

```go
// Unique возвращает уникальные элементы
func Unique[T comparable](slice []T) []T {
    seen := make(map[T]bool)
    var result []T

    for _, v := range slice {
        if !seen[v] {
            seen[v] = true
            result = append(result, v)
        }
    }

    return result
}

// DistinctBy возвращает уникальные элементы по ключу
func DistinctBy[T any, K comparable](slice []T, key func(T) K) []T {
    seen := make(map[K]bool)
    var result []T

    for _, v := range slice {
        k := key(v)
        if !seen[k] {
            seen[k] = true
            result = append(result, v)
        }
    }

    return result
}
```

### Обобщенные структуры данных: **Set**

```go
type Set[T comparable] struct {
    items map[T]bool
}

func NewSet[T comparable]() *Set[T] {
    return &Set[T]{items: make(map[T]bool)}
}

func (s *Set[T]) Add(item T) {
    s.items[item] = true
}

func (s *Set[T]) Remove(item T) {
    delete(s.items, item)
}

func (s *Set[T]) Contains(item T) bool {
    return s.items[item]
}

func (s *Set[T]) Size() int {
    return len(s.items)
}

func (s *Set[T]) ToSlice() []T {
    result := make([]T, 0, len(s.items))
    for item := range s.items {
        result = append(result, item)
    }
    return result
}

func (s *Set[T]) Union(other *Set[T]) *Set[T] {
    result := NewSet[T]()
    for item := range s.items {
        result.Add(item)
    }
    for item := range other.items {
        result.Add(item)
    }
    return result
}

func (s *Set[T]) Intersection(other *Set[T]) *Set[T] {
    result := NewSet[T]()
    for item := range s.items {
        if other.Contains(item) {
            result.Add(item)
        }
    }
    return result
}
```

### Обобщенные структуры данных: **Heap**

```go
type Heap[T any] struct {
    items []T
    less  func(T, T) bool
}

func NewHeap[T any](less func(T, T) bool) *Heap[T] {
    return &Heap[T]{
        items: make([]T, 0),
        less:  less,
    }
}

func (h *Heap[T]) Push(item T) {
    h.items = append(h.items, item)
    h.up(len(h.items) - 1)
}

func (h *Heap[T]) Pop() (T, bool) {
    if len(h.items) == 0 {
        var zero T
        return zero, false
    }

    item := h.items[0]
    h.items[0] = h.items[len(h.items)-1]
    h.items = h.items[:len(h.items)-1]

    if len(h.items) > 0 {
        h.down(0)
    }

    return item, true
}

func (h *Heap[T]) up(i int) {
    for {
        parent := (i - 1) / 2
        if i == 0 || !h.less(h.items[i], h.items[parent]) {
            break
        }
        h.items[i], h.items[parent] = h.items[parent], h.items[i]
        i = parent
    }
}

func (h *Heap[T]) down(i int) {
    for {
        left := 2*i + 1
        right := 2*i + 2
        smallest := i

        if left < len(h.items) && h.less(h.items[left], h.items[smallest]) {
            smallest = left
        }
        if right < len(h.items) && h.less(h.items[right], h.items[smallest]) {
            smallest = right
        }

        if smallest == i {
            break
        }

        h.items[i], h.items[smallest] = h.items[smallest], h.items[i]
        i = smallest
    }
}
```

### Обобщенные структуры данных: **Linked List**

```go
type Node[T any] struct {
    Value T
    Next  *Node[T]
}

type LinkedList[T any] struct {
    head *Node[T]
    size int
}

func NewLinkedList[T any]() *LinkedList[T] {
    return &LinkedList[T]{}
}

func (l *LinkedList[T]) Append(value T) {
    newNode := &Node[T]{Value: value}

    if l.head == nil {
        l.head = newNode
    } else {
        current := l.head
        for current.Next != nil {
            current = current.Next
        }
        current.Next = newNode
    }

    l.size++
}

func (l *LinkedList[T]) Prepend(value T) {
    newNode := &Node[T]{Value: value, Next: l.head}
    l.head = newNode
    l.size++
}

func (l *LinkedList[T]) Get(index int) (T, bool) {
    if index < 0 || index >= l.size {
        var zero T
        return zero, false
    }

    current := l.head
    for i := 0; i < index; i++ {
        current = current.Next
    }

    return current.Value, true
}

func (l *LinkedList[T]) Remove(index int) bool {
    if index < 0 || index >= l.size {
        return false
    }

    if index == 0 {
        l.head = l.head.Next
    } else {
        current := l.head
        for i := 0; i < index-1; i++ {
            current = current.Next
        }
        current.Next = current.Next.Next
    }

    l.size--
    return true
}
```

### Обобщенные структуры данных: **Tree**

```go
type TreeNode[T any] struct {
    Value T
    Left  *TreeNode[T]
    Right *TreeNode[T]
}

type BinaryTree[T comparable] struct {
    root *TreeNode[T]
    less func(T, T) bool
}

func NewBinaryTree[T comparable](less func(T, T) bool) *BinaryTree[T] {
    return &BinaryTree[T]{less: less}
}

func (t *BinaryTree[T]) Insert(value T) {
    t.root = t.insertNode(t.root, value)
}

func (t *BinaryTree[T]) insertNode(node *TreeNode[T], value T) *TreeNode[T] {
    if node == nil {
        return &TreeNode[T]{Value: value}
    }

    if t.less(value, node.Value) {
        node.Left = t.insertNode(node.Left, value)
    } else {
        node.Right = t.insertNode(node.Right, value)
    }

    return node
}

func (t *BinaryTree[T]) Search(value T) bool {
    return t.searchNode(t.root, value)
}

func (t *BinaryTree[T]) searchNode(node *TreeNode[T], value T) bool {
    if node == nil {
        return false
    }

    if node.Value == value {
        return true
    }

    if t.less(value, node.Value) {
        return t.searchNode(node.Left, value)
    }

    return t.searchNode(node.Right, value)
}
```

### Практические примеры: Обобщенный кэш

```go
type Cache[K comparable, V any] struct {
    data  map[K]V
    mu    sync.RWMutex
    ttl   time.Duration
    times map[K]time.Time
}

func NewCache[K comparable, V any](ttl time.Duration) *Cache[K, V] {
    c := &Cache[K, V]{
        data:  make(map[K]V),
        ttl:   ttl,
        times: make(map[K]time.Time),
    }

    go c.cleanup()
    return c
}

func (c *Cache[K, V]) Get(key K) (V, bool) {
    c.mu.RLock()
    defer c.mu.RUnlock()

    value, exists := c.data[key]
    if !exists {
        var zero V
        return zero, false
    }

    if time.Since(c.times[key]) > c.ttl {
        var zero V
        return zero, false
    }

    return value, true
}

func (c *Cache[K, V]) Set(key K, value V) {
    c.mu.Lock()
    defer c.mu.Unlock()

    c.data[key] = value
    c.times[key] = time.Now()
}

func (c *Cache[K, V]) cleanup() {
    ticker := time.NewTicker(c.ttl)
    defer ticker.Stop()

    for range ticker.C {
        c.mu.Lock()
        for key, t := range c.times {
            if time.Since(t) > c.ttl {
                delete(c.data, key)
                delete(c.times, key)
            }
        }
        c.mu.Unlock()
    }
}
```

### Практические примеры: Обобщенный репозиторий

```go
type Repository[T any, ID comparable] interface {
    FindByID(id ID) (T, error)
    FindAll() ([]T, error)
    Save(entity T) error
    Delete(id ID) error
}

type InMemoryRepository[T any, ID comparable] struct {
    data  map[ID]T
    mu    sync.RWMutex
    getID func(T) ID
}

func NewInMemoryRepository[T any, ID comparable](getID func(T) ID) *InMemoryRepository[T, ID] {
    return &InMemoryRepository[T, ID]{
        data:  make(map[ID]T),
        getID: getID,
    }
}

func (r *InMemoryRepository[T, ID]) FindByID(id ID) (T, error) {
    r.mu.RLock()
    defer r.mu.RUnlock()

    entity, exists := r.data[id]
    if !exists {
        var zero T
        return zero, fmt.Errorf("entity not found")
    }

    return entity, nil
}

func (r *InMemoryRepository[T, ID]) FindAll() ([]T, error) {
    r.mu.RLock()
    defer r.mu.RUnlock()

    result := make([]T, 0, len(r.data))
    for _, entity := range r.data {
        result = append(result, entity)
    }

    return result, nil
}

func (r *InMemoryRepository[T, ID]) Save(entity T) error {
    r.mu.Lock()
    defer r.mu.Unlock()

    id := r.getID(entity)
    r.data[id] = entity
    return nil
}

func (r *InMemoryRepository[T, ID]) Delete(id ID) error {
    r.mu.Lock()
    defer r.mu.Unlock()

    if _, exists := r.data[id]; !exists {
        return fmt.Errorf("entity not found")
    }

    delete(r.data, id)
    return nil
}
```

### Практические примеры: **Generic** функциональные утилиты

```go
// Map для преобразования элементов
func Map[T, U any](slice []T, fn func(T) U) []U {
    result := make([]U, len(slice))
    for i, item := range slice {
        result[i] = fn(item)
    }
    return result
}

// Filter для фильтрации элементов
func Filter[T any](slice []T, fn func(T) bool) []T {
    result := make([]T, 0)
    for _, item := range slice {
        if fn(item) {
            result = append(result, item)
        }
    }
    return result
}

// Reduce для агрегации
func Reduce[T, U any](slice []T, initial U, fn func(U, T) U) U {
    result := initial
    for _, item := range slice {
        result = fn(result, item)
    }
    return result
}

// Find для поиска элемента
func Find[T any](slice []T, fn func(T) bool) (T, bool) {
    for _, item := range slice {
        if fn(item) {
            return item, true
        }
    }
    var zero T
    return zero, false
}

// All для проверки всех элементов
func All[T any](slice []T, fn func(T) bool) bool {
    for _, item := range slice {
        if !fn(item) {
            return false
        }
    }
    return true
}

// Any для проверки любого элемента
func Any[T any](slice []T, fn func(T) bool) bool {
    for _, item := range slice {
        if fn(item) {
            return true
        }
    }
    return false
}
```

### Практические примеры: **Generic** деревья

```go
type TreeNode[T comparable] struct {
    Value T
    Left  *TreeNode[T]
    Right *TreeNode[T]
}

func NewTreeNode[T comparable](value T) *TreeNode[T] {
    return &TreeNode[T]{
        Value: value,
    }
}

func (tn *TreeNode[T]) Insert(value T) {
    if value < tn.Value {
        if tn.Left == nil {
            tn.Left = NewTreeNode(value)
        } else {
            tn.Left.Insert(value)
        }
    } else {
        if tn.Right == nil {
            tn.Right = NewTreeNode(value)
        } else {
            tn.Right.Insert(value)
        }
    }
}

func (tn *TreeNode[T]) Search(value T) bool {
    if tn.Value == value {
        return true
    }

    if value < tn.Value {
        if tn.Left == nil {
            return false
        }
        return tn.Left.Search(value)
    }

    if tn.Right == nil {
        return false
    }
    return tn.Right.Search(value)
}

func (tn *TreeNode[T]) Traverse(fn func(T)) {
    if tn.Left != nil {
        tn.Left.Traverse(fn)
    }
    fn(tn.Value)
    if tn.Right != nil {
        tn.Right.Traverse(fn)
    }
}
```

### Практические примеры: **Generic** графы

```go
type Graph[T comparable] struct {
    nodes map[T][]T
    mu    sync.RWMutex
}

func NewGraph[T comparable]() *Graph[T] {
    return &Graph[T]{
        nodes: make(map[T][]T),
    }
}

func (g *Graph[T]) AddNode(node T) {
    g.mu.Lock()
    defer g.mu.Unlock()
    if _, exists := g.nodes[node]; !exists {
        g.nodes[node] = make([]T, 0)
    }
}

func (g *Graph[T]) AddEdge(from, to T) {
    g.mu.Lock()
    defer g.mu.Unlock()
    g.nodes[from] = append(g.nodes[from], to)
}

func (g *Graph[T]) BFS(start T, visit func(T)) {
    g.mu.RLock()
    visited := make(map[T]bool)
    queue := []T{start}
    visited[start] = true
    g.mu.RUnlock()

    for len(queue) > 0 {
        node := queue[0]
        queue = queue[1:]
        visit(node)

        g.mu.RLock()
        neighbors := g.nodes[node]
        g.mu.RUnlock()

        for _, neighbor := range neighbors {
            if !visited[neighbor] {
                visited[neighbor] = true
                queue = append(queue, neighbor)
            }
        }
    }
}

func (g *Graph[T]) DFS(start T, visit func(T)) {
    visited := make(map[T]bool)
    g.dfsHelper(start, visit, visited)
}

func (g *Graph[T]) dfsHelper(node T, visit func(T), visited map[T]bool) {
    if visited[node] {
        return
    }

    visited[node] = true
    visit(node)

    g.mu.RLock()
    neighbors := g.nodes[node]
    g.mu.RUnlock()

    for _, neighbor := range neighbors {
        g.dfsHelper(neighbor, visit, visited)
    }
}
```

### Практические примеры: **Generic** очереди с приоритетами

```go
import "container/heap"

type PriorityQueue[T any] struct {
    items []*priorityItem[T]
    less  func(T, T) bool
}

type priorityItem[T any] struct {
    value    T
    priority int
    index    int
}

func NewPriorityQueue[T any](less func(T, T) bool) *PriorityQueue[T] {
    pq := &PriorityQueue[T]{
        items: make([]*priorityItem[T], 0),
        less:  less,
    }
    heap.Init(pq)
    return pq
}

func (pq *PriorityQueue[T]) Push(x interface{}) {
    n := len(pq.items)
    item := x.(*priorityItem[T])
    item.index = n
    pq.items = append(pq.items, item)
}

func (pq *PriorityQueue[T]) Pop() interface{} {
    old := pq.items
    n := len(old)
    item := old[n-1]
    old[n-1] = nil
    item.index = -1
    pq.items = old[0 : n-1]
    return item
}

func (pq *PriorityQueue[T]) Len() int {
    return len(pq.items)
}

func (pq *PriorityQueue[T]) Less(i, j int) bool {
    return pq.items[i].priority > pq.items[j].priority
}

func (pq *PriorityQueue[T]) Swap(i, j int) {
    pq.items[i], pq.items[j] = pq.items[j], pq.items[i]
    pq.items[i].index = i
    pq.items[j].index = j
}

func (pq *PriorityQueue[T]) Enqueue(value T, priority int) {
    item := &priorityItem[T]{
        value:    value,
        priority: priority,
    }
    heap.Push(pq, item)
}

func (pq *PriorityQueue[T]) Dequeue() (T, bool) {
    if pq.Len() == 0 {
        var zero T
        return zero, false
    }

    item := heap.Pop(pq).(*priorityItem[T])
    return item.value, true
}
```

### Практические примеры: **Generic** кэш

```go
type Cache[K comparable, V any] struct {
    data      map[K]V
    mu        sync.RWMutex
    maxSize   int
    eviction  func(K, V)
}

func NewCache[K comparable, V any](maxSize int) *Cache[K, V] {
    return &Cache[K, V]{
        data:    make(map[K]V),
        maxSize: maxSize,
    }
}

func (c *Cache[K, V]) Get(key K) (V, bool) {
    c.mu.RLock()
    defer c.mu.RUnlock()
    value, ok := c.data[key]
    return value, ok
}

func (c *Cache[K, V]) Set(key K, value V) {
    c.mu.Lock()
    defer c.mu.Unlock()

    if len(c.data) >= c.maxSize && len(c.data) > 0 {
        // LRU eviction
        for k, v := range c.data {
            delete(c.data, k)
            if c.eviction != nil {
                c.eviction(k, v)
            }
            break
        }
    }

    c.data[key] = value
}

func (c *Cache[K, V]) Delete(key K) {
    c.mu.Lock()
    defer c.mu.Unlock()
    delete(c.data, key)
}

func (c *Cache[K, V]) Clear() {
    c.mu.Lock()
    defer c.mu.Unlock()
    c.data = make(map[K]V)
}

func (c *Cache[K, V]) Size() int {
    c.mu.RLock()
    defer c.mu.RUnlock()
    return len(c.data)
}
```

### Практические примеры: **Generic** функциональные опции

```go
type Option[T any] func(*T)

func WithField[T any, F any](field *F, value F) Option[T] {
    return func(t *T) {
        // Использование рефлексии или интерфейсов для установки значения
    }
}

type Config struct {
    Host     string
    Port     int
    Timeout  time.Duration
    MaxRetries int
}

func NewConfig(opts ...Option[Config]) *Config {
    c := &Config{
        Host:       "localhost",
        Port:       8080,
        Timeout:    30 * time.Second,
        MaxRetries: 3,
    }

    for _, opt := range opts {
        opt(c)
    }

    return c
}

// Использование
config := NewConfig(
    func(c *Config) { c.Host = "example.com" },
    func(c *Config) { c.Port = 9090 },
    func(c *Config) { c.Timeout = 60 * time.Second },
)
```

## Лучшие практики

1. **Используйте constraints** — ограничивайте типы для безопасности
2. **Избегайте излишней обобщенности** — используйте **generics** только когда это необходимо
3. **Используйте type inference** — позволяйте компилятору выводить типы
4. **Документируйте constraints** — объясняйте ограничения типов
5. **Тестируйте с различными типами** — проверяйте работу с разными типами
6. **Используйте конкретные типы когда возможно** — избегайте излишней обобщенности
7. **Оптимизируйте производительность** — **generics** не добавляют **runtime overhead**
8. **Используйте именованные constraints** — для переиспользования
9. **Избегайте сложных constraints** — упрощайте ограничения типов
10. **Тестируйте edge cases** — проверяйте граничные случаи
11. **Используйте generic утилиты** — для часто используемых операций
12. **Избегайте type assertions** — используйте **generics** вместо **type assertions**
13. **Используйте constraints для ограничений** — применяйте **constraints** для безопасности
14. **Документируйте generic функции** — объясняйте использование **generics**
15. **Тестируйте производительность** — убедитесь, что **generics** не снижают производительность

### Практические примеры: **Generic** структуры данных

```go
// Generic Stack
type Stack[T any] struct {
    items []T
}

func NewStack[T any]() *Stack[T] {
    return &Stack[T]{
        items: make([]T, 0),
    }
}

func (s *Stack[T]) Push(item T) {
    s.items = append(s.items, item)
}

func (s *Stack[T]) Pop() (T, bool) {
    if len(s.items) == 0 {
        var zero T
        return zero, false
    }

    item := s.items[len(s.items)-1]
    s.items = s.items[:len(s.items)-1]
    return item, true
}

func (s *Stack[T]) Peek() (T, bool) {
    if len(s.items) == 0 {
        var zero T
        return zero, false
    }
    return s.items[len(s.items)-1], true
}

func (s *Stack[T]) Size() int {
    return len(s.items)
}
```

### Практические примеры: **Generic** алгоритмы

```go
// Generic сортировка с пользовательской функцией сравнения
func Sort[T any](slice []T, less func(T, T) bool) {
    sort.Slice(slice, func(i, j int) bool {
        return less(slice[i], slice[j])
    })
}

// Generic бинарный поиск
func BinarySearch[T comparable](slice []T, target T, less func(T, T) bool) int {
    left, right := 0, len(slice)-1

    for left <= right {
        mid := (left + right) / 2

        if slice[mid] == target {
            return mid
        }

        if less(slice[mid], target) {
            left = mid + 1
        } else {
            right = mid - 1
        }
    }

    return -1
}
```


## Решение проблем

Типичные проблемы и решения см. в официальной документации (блок «Полезные ссылки» в начале документа).

## Частые вопросы

Ответы на частые вопросы по теме см. в разделах «Введение» и «Лучшие практики» в документе.

## Заключение

**Generics** в Go предоставляют мощные инструменты для создания типобезопасного и переиспользуемого кода. Понимание **type parameters**, **constraints**, **type inference**, практических применений, **generic** структур данных, алгоритмов и паттернов использования критично для эффективного использования **generics** в Go. Правильное использование **generics** позволяет создавать более выразительный, типобезопасный, переиспользуемый код, сохраняя производительность Go, уменьшая дублирование кода и повышая качество программного обеспечения.

## Дополнительные ресурсы

- [Go Generics Tutorial](https://go.dev/doc/tutorial/generics)
- [Go Generics Proposal](https://go.googlesource.com/proposal/+/refs/heads/master/design/43651-type-parameters.md)
- [Type Parameters Proposal](https://go.dev/blog/intro-generics)

## См. также

- [[go-advanced-patterns|Go: продвинутые паттерны]]
- [[go-basics|Go: основы]]
- [[go-benchmarking|Go: бенчмаркинг]]
- [[go-best-practices|Go: лучшие практики]]
- [[go-build|Go: сборка и развертывание]]
