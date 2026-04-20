---
title: "Go: коллекции"
description: "Полное руководство по коллекциям в Go: slices, maps, arrays, работа с данными, операции, производительность"
tags:
  - go
  - golang
  - collections
  - slices
  - maps
  - arrays
  - data-structures
difficulty: "intermediate"
prerequisites: ["go/go-basics.md"]
updated: "2026-02-06"
---

# Go: коллекции

## Полезные ссылки

- [Go Slices Documentation](https://go.dev/blog/slices)
- [Go Maps Documentation](https://go.dev/blog/maps)
- [Effective Go — Arrays and Slices](https://go.dev/doc/effective_go#arrays_slices_maps)

## Содержание

- [Go: коллекции](#go-коллекции)
- [Введение в коллекции](#введение-в-коллекции)
  - [Основные типы коллекций в Go](#основные-типы-коллекций-в-go)
  - [Выбор правильной коллекции](#выбор-правильной-коллекции)
- [Arrays (Массивы)](#arrays-массивы)
  - [Объявление массивов](#объявление-массивов)
  - [Доступ к элементам](#доступ-к-элементам)
  - [Итерация по массиву](#итерация-по-массиву)
  - [Многомерные массивы](#многомерные-массивы)
  - [Особенности массивов](#особенности-массивов)
- [Slices (Срезы)](#slices-срезы)
  - [Объявление срезов](#объявление-срезов)
  - [Длина и емкость](#длина-и-емкость)
  - [Добавление элементов](#добавление-элементов)
  - [Создание под-срезов](#создание-под-срезов)
  - [Копирование срезов](#копирование-срезов)
  - [Внутренняя структура срезов](#внутренняя-структура-срезов)
  - [Перераспределение памяти](#перераспределение-памяти)
- [Maps (Карты)](#maps-карты)
  - [Объявление карт](#объявление-карт)
  - [Работа с элементами](#работа-с-элементами)
  - [Итерация по карте](#итерация-по-карте)
  - [Особенности карт](#особенности-карт)
- [Операции над коллекциями](#операции-над-коллекциями)
  - [Фильтрация](#фильтрация)
  - [Трансформация](#трансформация)
  - [Поиск](#поиск)
  - [Агрегация](#агрегация)
- [Производительность](#производительность)
  - [Оптимизация срезов](#оптимизация-срезов)
  - [Оптимизация карт](#оптимизация-карт)
  - [Сортировка срезов](#сортировка-срезов)
  - [Поиск в отсортированных срезах](#поиск-в-отсортированных-срезах)
  - [Удаление элементов из среза](#удаление-элементов-из-среза)
  - [Вставка элементов в срез](#вставка-элементов-в-срез)
  - [Объединение срезов](#объединение-срезов)
  - [Группировка элементов](#группировка-элементов)
  - [Разделение среза на части](#разделение-среза-на-части)
  - [Перемешивание среза](#перемешивание-среза)
  - [Обращение среза](#обращение-среза)
  - [Работа с картами: вложенные карты](#работа-с-картами-вложенные-карты)
  - [Работа с картами: множества](#работа-с-картами-множества)
  - [Работа с картами: счетчики](#работа-с-картами-счетчики)
  - [Работа с картами: группировка](#работа-с-картами-группировка)
  - [Практические примеры: обработка данных](#практические-примеры-обработка-данных)
  - [Практические примеры: кэширование](#практические-примеры-кэширование)
  - [Практические примеры: индексация](#практические-примеры-индексация)
  - [Производительность: сравнение операций](#производительность-сравнение-операций)
  - [Производительность: оптимизация памяти](#производительность-оптимизация-памяти)
  - [Производительность: параллельная обработка](#производительность-параллельная-обработка)
  - [Практические примеры: Операции над срезами](#практические-примеры-операции-над-срезами)
  - [Практические примеры: Группировка данных](#практические-примеры-группировка-данных)
  - [Практические примеры: Операции над картами](#практические-примеры-операции-над-картами)
  - [Практические примеры: Поиск в коллекциях](#практические-примеры-поиск-в-коллекциях)
  - [Практические примеры: Сортировка срезов](#практические-примеры-сортировка-срезов)
  - [Практические примеры: Преобразование коллекций](#практические-примеры-преобразование-коллекций)
- [Лучшие практики](#лучшие-практики)
  - [Практические примеры: Эффективная работа с большими коллекциями](#практические-примеры-эффективная-работа-с-большими-коллекциями)
  - [Практические примеры: Индексация коллекций](#практические-примеры-индексация-коллекций)
- [Troubleshooting](#troubleshooting)
- [FAQ](#faq)
- [Заключение](#заключение)
- [Дополнительные ресурсы](#дополнительные-ресурсы)

## Введение в коллекции

Go предоставляет несколько типов коллекций для работы с данными: массивы (arrays), срезы (slices) и карты (maps). Понимание различий между этими типами и их правильное использование критично для эффективной работы с данными в Go.

### Основные типы коллекций в Go

1. **Arrays** — фиксированного размера последовательности элементов одного типа
2. **Slices** — динамические последовательности, построенные на основе массивов
3. **Maps** — ассоциативные массивы (словари) для хранения пар ключ-значение

### Выбор правильной коллекции

**Выбор правильной коллекции зависит от конкретной задачи:**

- **Arrays** — когда размер известен заранее и не изменяется
- **Slices** — для большинства случаев работы с последовательностями данных
- **Maps** — для ассоциативного хранения данных по ключам

## Arrays (Массивы)

Массивы в Go имеют фиксированный размер, который определяется при объявлении и не может быть изменен.

### Объявление массивов

```go
// Массив из 5 целых чисел
var arr [5]int

// Массив с инициализацией
var arr2 [5]int = [5]int{1, 2, 3, 4, 5}

// Короткая форма инициализации
arr3 := [5]int{1, 2, 3, 4, 5}

// Массив с автоматическим определением размера
arr4 := [...]int{1, 2, 3, 4, 5}
```

### Доступ к элементам

```go
arr := [5]int{1, 2, 3, 4, 5}

// Чтение элемента
first := arr[0]  // 1

// Запись элемента
arr[0] = 10

// Длина массива
length := len(arr)  // 5
```

### Итерация по массиву

```go
arr := [5]int{1, 2, 3, 4, 5}

// Итерация по индексу
for i := 0; i < len(arr); i++ {
    fmt.Println(arr[i])
}

// Итерация с range
for i, v := range arr {
    fmt.Printf("Index: %d, Value: %d\n", i, v)
}

// Итерация только по значениям
for _, v := range arr {
    fmt.Println(v)
}
```

### Многомерные массивы

```go
// Двумерный массив
var matrix [3][3]int

// Инициализация двумерного массива
matrix := [3][3]int{
    {1, 2, 3},
    {4, 5, 6},
    {7, 8, 9},
}

// Доступ к элементам
value := matrix[1][2]  // 6
```

### Особенности массивов

**Массивы в Go имеют следующие особенности:**

1. **Фиксированный размер** — размер массива является частью его типа
2. **Копирование по значению** — при присваивании создается копия массива
3. **Сравнение** — массивы можно сравнивать оператором `==`

```go
arr1 := [3]int{1, 2, 3}
arr2 := [3]int{1, 2, 3}
arr3 := [3]int{1, 2, 4}

fmt.Println(arr1 == arr2)  // true
fmt.Println(arr1 == arr3)  // false
```

## Slices (Срезы)

Срезы — это динамические последовательности, построенные на основе массивов. Срезы являются наиболее часто используемым типом коллекций в Go.

### Объявление срезов

```go
// Пустой срез
var slice []int

// Срез с инициализацией
slice := []int{1, 2, 3, 4, 5}

// Срез из массива
arr := [5]int{1, 2, 3, 4, 5}
slice := arr[:]  // срез всего массива

// Срез с помощью make
slice := make([]int, 5)        // длина 5, емкость 5
slice := make([]int, 5, 10)   // длина 5, емкость 10
```

### Длина и емкость

```go
slice := []int{1, 2, 3, 4, 5}

length := len(slice)    // 5 - количество элементов
capacity := cap(slice)  // 5 - емкость среза
```

### Добавление элементов

```go
slice := []int{1, 2, 3}

// Добавление одного элемента
slice = append(slice, 4)

// Добавление нескольких элементов
slice = append(slice, 5, 6, 7)

// Добавление другого среза
slice2 := []int{8, 9, 10}
slice = append(slice, slice2...)
```

### Создание под-срезов

```go
slice := []int{1, 2, 3, 4, 5, 6, 7, 8, 9, 10}

// Срез с индекса 2 до 5 (не включая 5)
subSlice := slice[2:5]  // [3, 4, 5]

// Срез от начала до индекса 5
subSlice2 := slice[:5]  // [1, 2, 3, 4, 5]

// Срез от индекса 5 до конца
subSlice3 := slice[5:]  // [6, 7, 8, 9, 10]

// Полный срез
subSlice4 := slice[:]   // [1, 2, 3, 4, 5, 6, 7, 8, 9, 10]
```

### Копирование срезов

```go
src := []int{1, 2, 3, 4, 5}

// Создание копии
dst := make([]int, len(src))
copy(dst, src)

// Копирование части среза
dst2 := make([]int, 3)
copy(dst2, src[1:4])  // копирует элементы с индекса 1 до 4
```

### Внутренняя структура срезов

**Срез в Go состоит из трех компонентов:**

1. **Указатель на массив** — указывает на первый элемент среза в базовом массиве
2. **Длина (length)** — количество элементов в срезе
3. **Емкость (capacity)** — максимальное количество элементов без перераспределения памяти

```go
// Внутренняя структура среза
type slice struct {
    ptr *int    // указатель на массив
    len int     // длина
    cap int     // емкость
}
```

### Перераспределение памяти

Когда срез переполняется, Go автоматически создает новый массив большего размера и копирует туда элементы.

```go
slice := make([]int, 0, 3)  // длина 0, емкость 3

slice = append(slice, 1)  // длина 1, емкость 3
slice = append(slice, 2)  // длина 2, емкость 3
slice = append(slice, 3)  // длина 3, емкость 3
slice = append(slice, 4)  // длина 4, емкость 6 (перераспределение)
```

## Maps (Карты)

Карты — это ассоциативные массивы для хранения пар ключ-значение.

### Объявление карт

```go
// Пустая карта
var m map[string]int

// Карта с инициализацией
m := map[string]int{
    "apple":  5,
    "banana": 3,
    "orange": 2,
}

// Карта с помощью make
m := make(map[string]int)
m := make(map[string]int, 10)  // с начальной емкостью
```

### Работа с элементами

```go
m := map[string]int{
    "apple":  5,
    "banana": 3,
}

// Добавление/обновление элемента
m["orange"] = 2

// Чтение элемента
value := m["apple"]  // 5

// Проверка существования ключа
value, exists := m["apple"]
if exists {
    fmt.Println("Apple exists:", value)
}

// Удаление элемента
delete(m, "banana")
```

### Итерация по карте

```go
m := map[string]int{
    "apple":  5,
    "banana": 3,
    "orange": 2,
}

// Итерация по ключам и значениям
for key, value := range m {
    fmt.Printf("%s: %d\n", key, value)
}

// Итерация только по ключам
for key := range m {
    fmt.Println(key)
}
```

### Особенности карт

**Карты в Go имеют следующие особенности:**

1. **Динамический размер** — карты могут расти и уменьшаться
2. **Ссылочный тип** — карты передаются по ссылке
3. **Нулевое значение** — нулевое значение карты это `nil`
4. **Порядок не гарантирован** — порядок итерации по карте не определен

```go
// Проверка на nil
var m map[string]int
if m == nil {
    fmt.Println("Map is nil")
}

// Инициализация nil карты
m = make(map[string]int)
```

## Операции над коллекциями

### Фильтрация

```go
numbers := []int{1, 2, 3, 4, 5, 6, 7, 8, 9, 10}

// Фильтрация четных чисел
var even []int
for _, n := range numbers {
    if n%2 == 0 {
        even = append(even, n)
    }
}
```

### Трансформация

```go
numbers := []int{1, 2, 3, 4, 5}

// Удвоение всех чисел
doubled := make([]int, len(numbers))
for i, n := range numbers {
    doubled[i] = n * 2
}
```

### Поиск

```go
numbers := []int{1, 2, 3, 4, 5}

// Поиск элемента
func find(slice []int, value int) (int, bool) {
    for i, v := range slice {
        if v == value {
            return i, true
        }
    }
    return -1, false
}

index, found := find(numbers, 3)
```

### Агрегация

```go
numbers := []int{1, 2, 3, 4, 5}

// Сумма
sum := 0
for _, n := range numbers {
    sum += n
}

// Максимум
max := numbers[0]
for _, n := range numbers {
    if n > max {
        max = n
    }
}
```

## Производительность

### Оптимизация срезов

```go
// Предварительное выделение памяти для среза
slice := make([]int, 0, 1000)  // емкость 1000

// Избегание перераспределения памяти
for i := 0; i < 1000; i++ {
    slice = append(slice, i)
}
```

### Оптимизация карт

```go
// Предварительное выделение памяти для карты
m := make(map[string]int, 1000)  // начальная емкость 1000

// Заполнение карты
for i := 0; i < 1000; i++ {
    m[fmt.Sprintf("key%d", i)] = i
}
```

### Сортировка срезов

Go предоставляет функцию `sort` для сортировки срезов различных типов.

```go
import "sort"

// Сортировка целых чисел
numbers := []int{5, 2, 8, 1, 9, 3}
sort.Ints(numbers)
fmt.Println(numbers)  // [1, 2, 3, 5, 8, 9]

// Сортировка строк
names := []string{"Charlie", "Alice", "Bob"}
sort.Strings(names)
fmt.Println(names)  // [Alice, Bob, Charlie]

// Сортировка с кастомной функцией сравнения
type Person struct {
    Name string
    Age  int
}

people := []Person{
    {"Alice", 30},
    {"Bob", 25},
    {"Charlie", 35},
}

sort.Slice(people, func(i, j int) bool {
    return people[i].Age < people[j].Age
})
```

### Поиск в отсортированных срезах

```go
import "sort"

numbers := []int{1, 2, 3, 5, 8, 9}
sort.Ints(numbers)

// Бинарный поиск
index := sort.SearchInts(numbers, 5)
fmt.Println(index)  // 3

// Проверка существования
exists := index < len(numbers) && numbers[index] == 5
fmt.Println(exists)  // true
```

### Удаление элементов из среза

```go
// Удаление элемента по индексу
func remove(slice []int, index int) []int {
    return append(slice[:index], slice[index+1:]...)
}

// Удаление элемента по значению
func removeValue(slice []int, value int) []int {
    for i, v := range slice {
        if v == value {
            return append(slice[:i], slice[i+1:]...)
        }
    }
    return slice
}

// Удаление всех вхождений
func removeAll(slice []int, value int) []int {
    result := make([]int, 0, len(slice))
    for _, v := range slice {
        if v != value {
            result = append(result, v)
        }
    }
    return result
}
```

### Вставка элементов в срез

```go
// Вставка элемента по индексу
func insert(slice []int, index int, value int) []int {
    return append(slice[:index], append([]int{value}, slice[index:]...)...)
}

// Вставка нескольких элементов
func insertMultiple(slice []int, index int, values ...int) []int {
    return append(slice[:index], append(values, slice[index:]...)...)
}
```

### Объединение срезов

```go
// Объединение двух срезов
func merge(slice1, slice2 []int) []int {
    result := make([]int, 0, len(slice1)+len(slice2))
    result = append(result, slice1...)
    result = append(result, slice2...)
    return result
}

// Объединение с удалением дубликатов
func mergeUnique(slice1, slice2 []int) []int {
    seen := make(map[int]bool)
    result := make([]int, 0)

    for _, v := range slice1 {
        if !seen[v] {
            seen[v] = true
            result = append(result, v)
        }
    }

    for _, v := range slice2 {
        if !seen[v] {
            seen[v] = true
            result = append(result, v)
        }
    }

    return result
}
```

### Группировка элементов

```go
// Группировка по условию
func groupBy(slice []int, fn func(int) bool) ([]int, []int) {
    var group1, group2 []int
    for _, v := range slice {
        if fn(v) {
            group1 = append(group1, v)
        } else {
            group2 = append(group2, v)
        }
    }
    return group1, group2
}

// Использование
numbers := []int{1, 2, 3, 4, 5, 6, 7, 8, 9, 10}
even, odd := groupBy(numbers, func(n int) bool {
    return n%2 == 0
})
```

### Разделение среза на части

```go
// Разделение среза на части заданного размера
func chunk(slice []int, size int) [][]int {
    var chunks [][]int
    for i := 0; i < len(slice); i += size {
        end := i + size
        if end > len(slice) {
            end = len(slice)
        }
        chunks = append(chunks, slice[i:end])
    }
    return chunks
}

// Использование
numbers := []int{1, 2, 3, 4, 5, 6, 7, 8, 9, 10}
chunks := chunk(numbers, 3)
// [[1, 2, 3], [4, 5, 6], [7, 8, 9], [10]]
```

### Перемешивание среза

```go
import (
    "math/rand"
    "time"
)

func shuffle(slice []int) []int {
    result := make([]int, len(slice))
    copy(result, slice)

    rand.Seed(time.Now().UnixNano())
    rand.Shuffle(len(result), func(i, j int) {
        result[i], result[j] = result[j], result[i]
    })

    return result
}
```

### Обращение среза

```go
// Обращение среза на месте
func reverse(slice []int) {
    for i, j := 0, len(slice)-1; i < j; i, j = i+1, j-1 {
        slice[i], slice[j] = slice[j], slice[i]
    }
}

// Создание обращенной копии
func reversed(slice []int) []int {
    result := make([]int, len(slice))
    for i, v := range slice {
        result[len(slice)-1-i] = v
    }
    return result
}
```

### Работа с картами: вложенные карты

```go
// Карта карт
var nestedMap map[string]map[string]int

nestedMap = make(map[string]map[string]int)
nestedMap["group1"] = make(map[string]int)
nestedMap["group1"]["item1"] = 10
nestedMap["group1"]["item2"] = 20

// Проверка существования вложенных ключей
if group, ok := nestedMap["group1"]; ok {
    if value, ok := group["item1"]; ok {
        fmt.Println(value)
    }
}
```

### Работа с картами: множества

```go
// Реализация множества через карту
type Set map[string]bool

func NewSet() Set {
    return make(Set)
}

func (s Set) Add(value string) {
    s[value] = true
}

func (s Set) Remove(value string) {
    delete(s, value)
}

func (s Set) Contains(value string) bool {
    return s[value]
}

func (s Set) Size() int {
    return len(s)
}

// Использование
set := NewSet()
set.Add("apple")
set.Add("banana")
fmt.Println(set.Contains("apple"))  // true
```

### Работа с картами: счетчики

```go
// Подсчет частоты элементов
func countFrequency(slice []string) map[string]int {
    counts := make(map[string]int)
    for _, item := range slice {
        counts[item]++
    }
    return counts
}

// Использование
items := []string{"apple", "banana", "apple", "orange", "banana", "apple"}
freq := countFrequency(items)
// map[apple:3 banana:2 orange:1]
```

### Работа с картами: группировка

```go
// Группировка элементов по ключу
func groupByKey(items []struct {
    Key   string
    Value int
}) map[string][]int {
    groups := make(map[string][]int)
    for _, item := range items {
        groups[item.Key] = append(groups[item.Key], item.Value)
    }
    return groups
}
```

### Практические примеры: обработка данных

```go
// Фильтрация и трансформация пользователей
type User struct {
    ID    int
    Name  string
    Age   int
    Email string
}

func processUsers(users []User) []string {
    var emails []string
    for _, user := range users {
        if user.Age >= 18 {
            emails = append(emails, user.Email)
        }
    }
    return emails
}

// Группировка пользователей по возрасту
func groupUsersByAge(users []User) map[int][]User {
    groups := make(map[int][]User)
    for _, user := range users {
        ageGroup := (user.Age / 10) * 10  // Группы по 10 лет
        groups[ageGroup] = append(groups[ageGroup], user)
    }
    return groups
}
```

### Практические примеры: кэширование

```go
// Простой кэш на основе карты
type Cache struct {
    data map[string]interface{}
    mu   sync.RWMutex
}

func NewCache() *Cache {
    return &Cache{
        data: make(map[string]interface{}),
    }
}

func (c *Cache) Get(key string) (interface{}, bool) {
    c.mu.RLock()
    defer c.mu.RUnlock()
    value, ok := c.data[key]
    return value, ok
}

func (c *Cache) Set(key string, value interface{}) {
    c.mu.Lock()
    defer c.mu.Unlock()
    c.data[key] = value
}

func (c *Cache) Delete(key string) {
    c.mu.Lock()
    defer c.mu.Unlock()
    delete(c.data, key)
}
```

### Практические примеры: индексация

```go
// Создание индекса для быстрого поиска
type Index struct {
    byID   map[int]*User
    byName map[string][]*User
    byAge  map[int][]*User
}

func NewIndex(users []User) *Index {
    idx := &Index{
        byID:   make(map[int]*User),
        byName: make(map[string][]*User),
        byAge:  make(map[int][]*User),
    }

    for i := range users {
        user := &users[i]
        idx.byID[user.ID] = user
        idx.byName[user.Name] = append(idx.byName[user.Name], user)
        idx.byAge[user.Age] = append(idx.byAge[user.Age], user)
    }

    return idx
}

func (idx *Index) FindByID(id int) (*User, bool) {
    user, ok := idx.byID[id]
    return user, ok
}

func (idx *Index) FindByName(name string) []*User {
    return idx.byName[name]
}
```

### Производительность: сравнение операций

```go
// Время доступа к элементам массива: O(1)
// Время доступа к элементам среза: O(1)
// Время доступа к элементам карты: O(1) в среднем

// Время поиска в срезе: O(n)
// Время поиска в карте: O(1) в среднем

// Время вставки в срез: O(1) амортизированное
// Время вставки в карту: O(1) в среднем
```

### Производительность: оптимизация памяти

```go
// Избегание утечек памяти при работе со срезами
func processLargeSlice(data []int) {
    // Создание под-среза может удерживать весь массив в памяти
    // Используйте копирование для освобождения памяти
    smallSlice := make([]int, 100)
    copy(smallSlice, data[:100])
    // Теперь data может быть освобожден сборщиком мусора
}

// Очистка карты для освобождения памяти
func clearMap(m map[string]int) {
    // Удаление всех элементов
    for k := range m {
        delete(m, k)
    }
    // Или создание новой карты
    m = make(map[string]int)
}
```

### Производительность: параллельная обработка

```go
import "sync"

// Параллельная обработка срезов
func processParallel(slice []int, numWorkers int) []int {
    chunkSize := len(slice) / numWorkers
    var wg sync.WaitGroup
    results := make([]int, len(slice))

    for i := 0; i < numWorkers; i++ {
        wg.Add(1)
        start := i * chunkSize
        end := start + chunkSize
        if i == numWorkers-1 {
            end = len(slice)
        }

        go func(start, end int) {
            defer wg.Done()
            for j := start; j < end; j++ {
                results[j] = slice[j] * 2
            }
        }(start, end)
    }

    wg.Wait()
    return results
}
```

### Практические примеры: Операции над срезами

```go
// Удаление элемента по индексу
func RemoveAt[T any](slice []T, index int) []T {
    if index < 0 || index >= len(slice) {
        return slice
    }
    return append(slice[:index], slice[index+1:]...)
}

// Вставка элемента
func InsertAt[T any](slice []T, index int, value T) []T {
    if index < 0 {
        index = 0
    }
    if index >= len(slice) {
        return append(slice, value)
    }
    slice = append(slice[:index+1], slice[index:]...)
    slice[index] = value
    return slice
}

// Перемешивание среза
func Shuffle[T any](slice []T) {
    rand.Seed(time.Now().UnixNano())
    for i := len(slice) - 1; i > 0; i-- {
        j := rand.Intn(i + 1)
        slice[i], slice[j] = slice[j], slice[i]
    }
}

// Переворачивание среза
func Reverse[T any](slice []T) {
    for i, j := 0, len(slice)-1; i < j; i, j = i+1, j-1 {
        slice[i], slice[j] = slice[j], slice[i]
    }
}

// Уникальные элементы
func Unique[T comparable](slice []T) []T {
    seen := make(map[T]bool)
    result := make([]T, 0)

    for _, item := range slice {
        if !seen[item] {
            seen[item] = true
            result = append(result, item)
        }
    }

    return result
}
```

### Практические примеры: Группировка данных

```go
// Группировка по ключу
func GroupBy[T any, K comparable](slice []T, keyFn func(T) K) map[K][]T {
    groups := make(map[K][]T)

    for _, item := range slice {
        key := keyFn(item)
        groups[key] = append(groups[key], item)
    }

    return groups
}

// Группировка пользователей по возрасту
type User struct {
    Name string
    Age  int
}

users := []User{
    {Name: "Alice", Age: 25},
    {Name: "Bob", Age: 30},
    {Name: "Charlie", Age: 25},
}

ageGroups := GroupBy(users, func(u User) int {
    return u.Age
})
```

### Практические примеры: Операции над картами

```go
// Фильтрация карты
func FilterMap[K comparable, V any](m map[K]V, fn func(K, V) bool) map[K]V {
    result := make(map[K]V)
    for k, v := range m {
        if fn(k, v) {
            result[k] = v
        }
    }
    return result
}

// Преобразование карты
func MapMap[K comparable, V any, U any](m map[K]V, fn func(K, V) U) map[K]U {
    result := make(map[K]U)
    for k, v := range m {
        result[k] = fn(k, v)
    }
    return result
}

// Объединение карт
func MergeMaps[K comparable, V any](maps ...map[K]V) map[K]V {
    result := make(map[K]V)
    for _, m := range maps {
        for k, v := range m {
            result[k] = v
        }
    }
    return result
}

// Инвертирование карты
func InvertMap[K, V comparable](m map[K]V) map[V]K {
    result := make(map[V]K)
    for k, v := range m {
        result[v] = k
    }
    return result
}
```

### Практические примеры: Поиск в коллекциях

```go
// Бинарный поиск
func BinarySearch[T comparable](slice []T, target T) int {
    left, right := 0, len(slice)-1

    for left <= right {
        mid := (left + right) / 2

        if slice[mid] == target {
            return mid
        }

        if slice[mid] < target {
            left = mid + 1
        } else {
            right = mid - 1
        }
    }

    return -1
}

// Линейный поиск
func LinearSearch[T comparable](slice []T, target T) int {
    for i, item := range slice {
        if item == target {
            return i
        }
    }
    return -1
}

// Поиск всех вхождений
func FindAll[T comparable](slice []T, target T) []int {
    indices := make([]int, 0)
    for i, item := range slice {
        if item == target {
            indices = append(indices, i)
        }
    }
    return indices
}
```

### Практические примеры: Сортировка срезов

```go
// Сортировка по пользовательской функции
func SortBy[T any](slice []T, less func(T, T) bool) {
    sort.Slice(slice, func(i, j int) bool {
        return less(slice[i], slice[j])
    })
}

// Сортировка по нескольким полям
type Person struct {
    Name string
    Age  int
}

func SortPeople(people []Person) {
    sort.Slice(people, func(i, j int) bool {
        if people[i].Age != people[j].Age {
            return people[i].Age < people[j].Age
        }
        return people[i].Name < people[j].Name
    })
}

// Стабильная сортировка
func StableSortBy[T any](slice []T, less func(T, T) bool) {
    sort.SliceStable(slice, func(i, j int) bool {
        return less(slice[i], slice[j])
    })
}
```

### Практические примеры: Преобразование коллекций

```go
// Преобразование слайса в карту
func SliceToMap[T any, K comparable](slice []T, keyFn func(T) K) map[K]T {
    result := make(map[K]T)
    for _, item := range slice {
        result[keyFn(item)] = item
    }
    return result
}

// Преобразование карты в слайс
func MapToSlice[K comparable, V any](m map[K]V) []V {
    result := make([]V, 0, len(m))
    for _, v := range m {
        result = append(result, v)
    }
    return result
}

// Преобразование карты в слайс пар
func MapToPairs[K comparable, V any](m map[K]V) []struct {
    Key   K
    Value V
} {
    result := make([]struct {
        Key   K
        Value V
    }, 0, len(m))

    for k, v := range m {
        result = append(result, struct {
            Key   K
            Value V
        }{k, v})
    }

    return result
}
```

## Лучшие практики

1. **Используйте срезы вместо массивов** — срезы более гибкие и удобные
2. **Предварительно выделяйте память** — используйте `make` с указанием емкости
3. **Проверяйте существование ключей** — всегда проверяйте второй возвращаемый параметр при чтении из карты
4. **Избегайте утечек памяти** — удаляйте неиспользуемые элементы из карт
5. **Используйте копирование** — используйте `copy` для копирования срезов
6. **Используйте карты для быстрого поиска** - `O(1)` вместо `O(n)` в срезах
7. **Избегайте создания под-срезов больших массивов** — используйте копирование
8. **Используйте sync.Map для конкурентного доступа** — для безопасного доступа из множества горутин
9. **Сортируйте срезы перед бинарным поиском** — для эффективного поиска
10. **Используйте карты для группировки и индексации** — для быстрого доступа к данным
11. **Используйте операции над коллекциями** — для упрощения кода
12. **Избегайте лишних копий** — используйте указатели для больших структур
13. **Используйте capacity** — предварительно выделяйте память для срезов
14. **Оптимизируйте операции** — выбирайте правильные структуры данных
15. **Тестируйте производительность** — измеряйте операции над коллекциями

### Практические примеры: Эффективная работа с большими коллекциями

```go
// Параллельная обработка большой коллекции
func ProcessInParallel[T any](items []T, processor func(T) T, workers int) []T {
    jobs := make(chan int, len(items))
    results := make(chan struct {
        index int
        value T
    }, len(items))

    // Запуск воркеров
    for w := 0; w < workers; w++ {
        go func() {
            for i := range jobs {
                results <- struct {
                    index int
                    value T
                }{i, processor(items[i])}
            }
        }()
    }

    // Отправка заданий
    for i := range items {
        jobs <- i
    }
    close(jobs)

    // Сбор результатов
    output := make([]T, len(items))
    for i := 0; i < len(items); i++ {
        result := <-results
        output[result.index] = result.value
    }

    return output
}
```

### Практические примеры: Индексация коллекций

```go
// Создание индекса для быстрого поиска
func IndexBy[T any, K comparable](items []T, keyFn func(T) K) map[K][]T {
    index := make(map[K][]T)
    for _, item := range items {
        key := keyFn(item)
        index[key] = append(index[key], item)
    }
    return index
}

// Поиск по индексу
func FindByIndex[T any, K comparable](index map[K][]T, key K) []T {
    return index[key]
}
```


## Решение проблем

Типичные проблемы и решения см. в официальной документации (блок «Полезные ссылки» в начале документа).

## Частые вопросы

Ответы на частые вопросы по теме см. в разделах «Введение» и «Лучшие практики» в документе.

## Заключение

Коллекции в Go предоставляют мощные инструменты для работы с данными. Понимание различий между массивами, срезами и картами, их особенностей, операций, параллельной обработки, индексации и правильное использование критично для создания эффективных приложений на Go. Правильный выбор коллекции, оптимизация операций, использование параллельной обработки и практических паттернов позволяют создавать высокопроизводительные, масштабируемые приложения, которые эффективно используют память и процессорное время.

## Дополнительные ресурсы

- [Go Slices Documentation](https://go.dev/blog/slices)
- [Go Maps Documentation](https://go.dev/blog/maps)
- [Effective Go — Arrays and Slices](https://go.dev/doc/effective_go#arrays_slices_maps)
