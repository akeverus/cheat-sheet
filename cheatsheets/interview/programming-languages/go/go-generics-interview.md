---
title: "Вопросы на собеседовании: Go Generics"
description: "Generics в Go 1.18+: type parameters, constraints, comparable, type sets, реализация (monomorphization vs dictionary), ограничения, common patterns"
tags:
  - interview
  - programming-languages
  - go-generics-interview
aliases:
  - "Go generics interview"
  - "Type parameters Go interview"
  - "Go 1.18 generics"
difficulty: "intermediate"
updated: "2026-04-18"
---
# Вопросы на собеседовании: `Go Generics`

Generics появились в Go только с **версии 1.18 (март 2022)** — на 13 лет позже релиза языка. Реализация — гибрид monomorphization и dictionary passing. Constraints через interfaces, type sets, comparable. Существенно ограничены по сравнению с Java/C#/Rust.

## Полезные ссылки

### Официальная документация и авторитетные источники

- [Type Parameters Proposal](https://github.com/golang/proposal/blob/master/design/43651-type-parameters.md)
- [Tutorial: Getting started with generics](https://go.dev/doc/tutorial/generics)
- [Generics Implementation — Go Blog](https://go.dev/blog/intro-generics)
- [The Generics Implementation Design](https://github.com/golang/proposal/blob/master/design/generics-implementation-gcshape.md)
- [Generics by Example — Eli Bendersky](https://eli.thegreenplace.net/2022/an-extensive-look-at-go-generics/)

## Содержание

- [Полезные ссылки](#полезные-ссылки)
- [See also](#see-also)

**Базовые понятия**
- [Q1. (!) Что такое generics в Go?](#q1--что-такое-generics-в-go)
- [Q2. (!) Почему generics добавили только в Go 1.18?](#q2--почему-generics-добавили-только-в-go-118)
- [Q3. (!) Базовый синтаксис generics?](#q3--базовый-синтаксис-generics)

**Constraints**
- [Q4. (!) Что такое constraint?](#q4--что-такое-constraint)
- [Q5. (!) Что такое any и comparable?](#q5--что-такое-any-и-comparable)
- [Q6. (!) Type sets — что это?](#q6--type-sets--что-это)
- [Q7. (!) constraints package — что внутри?](#q7--constraints-package--что-внутри)
- [Q8. ~ оператор для underlying types?](#q8--оператор-для-underlying-types)

**Generic типы и структуры**
- [Q9. (!) Generic struct?](#q9--generic-struct)
- [Q10. Methods на generic types?](#q10-methods-на-generic-types)
- [Q11. (!) Generic interface?](#q11--generic-interface)

**Реализация**
- [Q12. (!) Как реализованы generics — monomorphization или dictionary?](#q12--как-реализованы-generics--monomorphization-или-dictionary)
- [Q13. GCShape — что это?](#q13-gcshape--что-это)
- [Q14. Производительность generics?](#q14-производительность-generics)

**Patterns**
- [Q15. (!) Generic Map/Filter/Reduce?](#q15--generic-mapfilterreduce)
- [Q16. (!) Generic коллекции (Stack, Queue, Set)?](#q16--generic-коллекции-stack-queue-set)
- [Q17. Generic constraints для math операций?](#q17-generic-constraints-для-math-операций)

**Ограничения**
- [Q18. (!) Какие минусы и ограничения generics в Go?](#q18--какие-минусы-и-ограничения-generics-в-go)
- [Q19. Почему нет generic methods (с typeparams) на receiver?](#q19-почему-нет-generic-methods-с-typeparams-на-receiver)
- [Q20. Type inference — где работает?](#q20-type-inference--где-работает)

**Сравнения**
- [Q21. (!) Go generics vs Java generics (erasure)?](#q21--go-generics-vs-java-generics-erasure)
- [Q22. Go generics vs C++ templates?](#q22-go-generics-vs-c-templates)
- [Q23. Go generics vs Rust?](#q23-go-generics-vs-rust)

**Применение**
- [Q24. (!) Когда использовать generics, а когда interface{}?](#q24--когда-использовать-generics-а-когда-interface)
- [Q25. (!) Стандартная библиотека и generics?](#q25--стандартная-библиотека-и-generics)
- [Q26. (!) golang.org/x/exp/slices, maps?](#q26--golangorgxexpslices-maps)

## Q1. (!) Что такое generics в Go?

**Generics** — параметризация типов. Позволяет писать код, работающий с **разными типами**, без потери type safety.

```go
// Без generics — для каждого типа отдельная функция или interface{}
func MaxInt(a, b int) int    { if a > b { return a }; return b }
func MaxFloat(a, b float64) float64 { ... }

// С generics — одна функция
func Max[T constraints.Ordered](a, b T) T {
    if a > b { return a }
    return b
}

Max(3, 5)         // 5 (int)
Max(3.14, 2.71)   // 3.14 (float64)
Max("apple", "banana") // "banana" (string)
```

## Q2. (!) Почему generics добавили только в Go 1.18?

Создатели Go (Rob Pike, Ken Thompson) изначально **намеренно** избегали generics:
- Усложнение языка
- Сложности с error handling и type inference
- Negative experience с C++ templates (compile time, error messages)

С 2009 по 2022 — работали над дизайном. Главная сложность — **что выбрать в реализации**:
- **Monomorphization (C++/Rust)** — отдельный код для каждого типа. Быстро в runtime, медленно компилируется, большие binaries.
- **Dictionary passing (Haskell, Java)** — один код, передаёт type info. Медленнее runtime, но меньше binaries.

Go 1.18 выбрал **гибрид (GCShape)**: разные shapes используют разный код, одинаковые shapes — общий.

## Q3. (!) Базовый синтаксис generics?

```go
// Type parameters в []
func PrintSlice[T any](s []T) {
    for _, v := range s {
        fmt.Println(v)
    }
}

// Несколько type параметров
func Map[T, U any](s []T, f func(T) U) []U {
    result := make([]U, len(s))
    for i, v := range s {
        result[i] = f(v)
    }
    return result
}

// Generic struct
type Stack[T any] struct {
    items []T
}

func (s *Stack[T]) Push(item T) {
    s.items = append(s.items, item)
}

// Использование
PrintSlice([]int{1, 2, 3})
PrintSlice[string]([]string{"a", "b"})

s := Stack[int]{}
s.Push(42)
```

## Q4. (!) Что такое constraint?

**Constraint** — ограничение на тип параметра. Это **interface**, описывающий какие типы допустимы.

```go
type Number interface {
    int | int32 | int64 | float32 | float64
}

func Sum[T Number](nums []T) T {
    var sum T
    for _, n := range nums {
        sum += n
    }
    return sum
}

Sum([]int{1, 2, 3})        // 6
Sum([]float64{1.1, 2.2})    // 3.3
Sum([]string{"a", "b"})     // ОШИБКА компиляции
```

Constraint = interface с **type set** (новая фича Go 1.18).

## Q5. (!) Что такое any и comparable?

**`any`** = `interface{}` — алиас, добавлен в Go 1.18. Любой тип удовлетворяет.

```go
func Print[T any](v T) { fmt.Println(v) }
```

**`comparable`** — встроенный constraint, типы которые можно сравнивать через `==` и `!=`.

```go
func Contains[T comparable](slice []T, target T) bool {
    for _, v := range slice {
        if v == target { return true }
    }
    return false
}

Contains([]int{1, 2, 3}, 2)        // true
Contains([]string{"a", "b"}, "c")  // false
```

`comparable` включает: примитивы, structs (если все поля comparable), pointers, channels, arrays. **НЕ включает:** slices, maps, functions.

## Q6. (!) Type sets — что это?

`Type set` interface — новый синтаксис в Go 1.18: union типов через `|`.

```go
type Signed interface {
    int | int8 | int16 | int32 | int64
}

type Unsigned interface {
    uint | uint8 | uint16 | uint32 | uint64
}

type Integer interface {
    Signed | Unsigned
}

type Number interface {
    Integer | float32 | float64
}
```

Type set ≠ обычный interface — нельзя использовать как тип переменной (только как constraint):

```go
var x Number  // ERROR — Number это constraint
func Process[T Number](x T) // OK
```

## Q7. (!) constraints package — что внутри?

`golang.org/x/exp/constraints` (стандарт пока не включил) — предопределённые constraints:

```go
import "golang.org/x/exp/constraints"

constraints.Signed     // int, int8, int16, int32, int64 (+ их named types)
constraints.Unsigned   // uint, uint8, ...
constraints.Integer    // Signed | Unsigned
constraints.Float      // float32 | float64
constraints.Complex    // complex64 | complex128
constraints.Ordered    // Integer | Float | ~string

func Min[T constraints.Ordered](a, b T) T {
    if a < b { return a }
    return b
}
```

`Ordered` — для типов, поддерживающих `<`, `>`, `<=`, `>=`.

## Q8. ~ оператор для underlying types?

`~T` — типы с **underlying type T** (включая named types).

```go
type MyInt int

type Number interface {
    int | float64  // ТОЛЬКО int и float64
}

type Number2 interface {
    ~int | ~float64 // int, MyInt, и любой type X int с underlying int
}

func sum1[T Number](a, b T) T  { return a + b }
func sum2[T Number2](a, b T) T { return a + b }

var x MyInt = 5
sum1(x, x) // ОШИБКА — MyInt не является int
sum2(x, x) // OK
```

`~` критично для библиотек — позволяет работать со всеми типами, основанными на примитивах (например, `time.Duration` — это `~int64`).

## Q9. (!) Generic struct?

```go
type Pair[K, V any] struct {
    Key   K
    Value V
}

p := Pair[string, int]{Key: "alice", Value: 30}
fmt.Println(p.Key, p.Value)

// С constraints
type Stack[T any] struct {
    items []T
}

func (s *Stack[T]) Push(item T) {
    s.items = append(s.items, item)
}

func (s *Stack[T]) Pop() (T, bool) {
    if len(s.items) == 0 {
        var zero T
        return zero, false
    }
    n := len(s.items) - 1
    item := s.items[n]
    s.items = s.items[:n]
    return item, true
}
```

`var zero T` — получить zero value generic типа.

## Q10. Methods на generic types?

```go
func (s *Stack[T]) Size() int {
    return len(s.items)
}

// Можно использовать T внутри метода
func (s *Stack[T]) Top() (T, bool) {
    if len(s.items) == 0 {
        var zero T
        return zero, false
    }
    return s.items[len(s.items)-1], true
}
```

**Ограничение:** методы **не могут вводить новые** type params. Они доступны только через type параметры структуры.

```go
// НЕЛЬЗЯ
func (s *Stack[T]) Map[U any](f func(T) U) *Stack[U] { ... }
// ERROR: methods cannot have type parameters
```

Это часто критикуется — нет fluent generic API.

## Q11. (!) Generic interface?

```go
type Comparable[T any] interface {
    Compare(other T) int
}

type IntWrapper struct{ v int }
func (i IntWrapper) Compare(other IntWrapper) int { return i.v - other.v }

func Sort[T Comparable[T]](items []T) {
    // ...
}
```

Generic interfaces — один из самых интересных аспектов. Можно описывать **рекурсивные** constraints (`T Comparable[T]`).

## Q12. (!) Как реализованы generics — monomorphization или dictionary?

Go использует **гибрид (GCShape stenciling)**:

- **Monomorphization** (как C++) для разных GCShape
- **Dictionary passing** для одинаковых shapes

**GCShape** — то, что важно для GC: размер типа, наличие указателей в нём.

```go
func Print[T any](v T) { fmt.Println(v) }

Print[int](42)      // одна shape
Print[int64](42)    // та же shape (8 байт, без pointers)
Print[string]("hi") // другая shape
Print[*User](&u)    // ещё одна (pointer)
```

Для одинаковых shape — **общий код** (с dictionary). Для разных — **отдельные копии**.

**Trade-off:**
- Меньше binary, чем pure monomorphization
- Чуть медленнее, чем pure monomorphization
- Меньше работы для GC

## Q13. GCShape — что это?

`GCShape` группирует типы по характеристикам, **критичным для GC**:
- Размер (8 байт, 16 байт, ...)
- Содержит ли pointers (для GC scanning)
- Layout указателей

```
int, int64, *User, etc — каждое имеет свою shape
```

Когда дженерик вызывается с типами одной shape — generated один код. С разными — несколько копий.

## Q14. Производительность generics?

**Микро-overhead** vs не-generic кода:
- **Без указателей в T** — почти нет (~ 1-3% slower)
- **С указателями** — больше (need dictionary для GC)
- **Interface boxing** — generics быстрее `interface{}` (нет boxing для value types)

Generics **обычно быстрее** `interface{}` подхода, потому что нет boxing/unboxing.

```go
// Generic — быстрее
func SumGeneric[T constraints.Number](nums []T) T

// interface{} — медленнее (boxing на каждое значение)
func SumInterface(nums []interface{}) interface{}
```

## Q15. (!) Generic Map/Filter/Reduce?

```go
func Map[T, U any](s []T, f func(T) U) []U {
    result := make([]U, len(s))
    for i, v := range s {
        result[i] = f(v)
    }
    return result
}

func Filter[T any](s []T, pred func(T) bool) []T {
    var result []T
    for _, v := range s {
        if pred(v) {
            result = append(result, v)
        }
    }
    return result
}

func Reduce[T, U any](s []T, initial U, f func(U, T) U) U {
    acc := initial
    for _, v := range s {
        acc = f(acc, v)
    }
    return acc
}

// Использование
nums := []int{1, 2, 3, 4, 5}
doubled := Map(nums, func(x int) int { return x * 2 })
evens := Filter(nums, func(x int) bool { return x%2 == 0 })
sum := Reduce(nums, 0, func(acc, x int) int { return acc + x })
```

В отличие от Java Stream API или Kotlin/Scala, в Go это **не цепочки** (`xs.map(...).filter(...).sum()`), а вложенные вызовы — нет fluent syntax.

## Q16. (!) Generic коллекции (Stack, Queue, Set)?

```go
type Set[T comparable] struct {
    items map[T]struct{}
}

func NewSet[T comparable]() *Set[T] {
    return &Set[T]{items: make(map[T]struct{})}
}

func (s *Set[T]) Add(item T)    { s.items[item] = struct{}{} }
func (s *Set[T]) Has(item T) bool { _, ok := s.items[item]; return ok }
func (s *Set[T]) Remove(item T) { delete(s.items, item) }
func (s *Set[T]) Size() int    { return len(s.items) }

// Использование
s := NewSet[int]()
s.Add(1); s.Add(2); s.Add(2)
fmt.Println(s.Size()) // 2
```

В Go нет встроенного Set — generic Set часто пишется вручную.

## Q17. Generic constraints для math операций?

```go
type Numeric interface {
    constraints.Integer | constraints.Float
}

func Sum[T Numeric](nums []T) T {
    var sum T
    for _, n := range nums {
        sum += n
    }
    return sum
}

func Average[T Numeric](nums []T) T {
    if len(nums) == 0 {
        var zero T
        return zero
    }
    return Sum(nums) / T(len(nums))
}
```

Через `Numeric` constraint доступны **арифметические** операторы (`+`, `-`, `*`, `/`).

**Ограничение:** нельзя сделать `T(len(nums))` если len и T разных типов — нужна conversion.

## Q18. (!) Какие минусы и ограничения generics в Go?

1. **Methods не могут иметь свои type parameters** — fluent API невозможен
2. **Нет higher-kinded types** (HKT) — нельзя `[F[_]]`
3. **Нет специализации** — нельзя сделать оптимизированный код для конкретного T
4. **Нет const generics** (как в Rust) — `[N int]` для compile-time чисел нет
5. **Type inference иногда не работает** — приходится явно указывать
6. **Constraints — это interfaces, не type traits** (Rust)
7. **Чем больше generics, тем дольше компиляция**
8. **Нет sum types через generic** — нет `Either[L, R]` идиоматично
9. **GCShape деление** — не настолько оптимально, как pure monomorphization

В целом, Go generics — **наименее мощные** среди современных языков, но **достаточные** для большинства задач.

## Q19. Почему нет generic methods (с typeparams) на receiver?

```go
type List[T any] struct { ... }

// НЕЛЬЗЯ — введение нового type parameter в методе
func (l *List[T]) Map[U any](f func(T) U) *List[U] { ... }
```

Это сделано **намеренно** — упрощает реализацию, type inference. Но цена — **нет fluent generic API**.

Workaround — top-level функция:

```go
func Map[T, U any](l *List[T], f func(T) U) *List[U] { ... }

// Использование
list := NewList[int]()
mapped := Map(list, func(x int) string { ... })
```

Не так красиво, как `list.Map(f)`.

## Q20. Type inference — где работает?

Type inference работает для **аргументов функций**:

```go
func Min[T constraints.Ordered](a, b T) T { ... }

Min(1, 2)         // T выводится как int
Min[int](1, 2)    // явное указание

// Структура — тип нужно указывать
s := Stack[int]{}
// s := Stack{} // ОШИБКА
```

Type inference в Go менее мощный, чем в Rust/Scala. Часто приходится явно указывать.

## Q21. (!) Go generics vs Java generics (erasure)?

| Критерий | Go | Java |
|----------|-----|------|
| Реализация | Гибрид (monomorphization + dict) | Type erasure |
| Runtime типы | Доступны | Стираются |
| `T.class` | Нет, но есть reflect | Нет (erasure) |
| Performance | Часто быстрее (no boxing) | Boxing для primitives |
| Generic arrays | Не поддерживаются | Не поддерживаются |
| Wildcards | Нет | Да (`? extends T`) |
| Bounds | Type sets (`int | float64`) | `T extends Number` |

Java generics — **runtime пустые** (стираются в Object). Go generics — **есть в runtime** (через GCShape).

**Java boxing проблема:** `List<Integer>` хранит `Integer` (heap), не `int` (stack). Go дженерики **не имеют** этой проблемы.

## Q22. Go generics vs C++ templates?

| Критерий | Go | C++ |
|----------|-----|-----|
| Реализация | Гибрид | Pure monomorphization |
| Compile time | Быстрее | Медленнее (template instantiation) |
| Binary size | Меньше | Больше (по копии для каждого типа) |
| Runtime perf | Очень близко | Максимум (zero overhead) |
| Type sets | Да | C++20 concepts |
| Error messages | Понятные | Известно ужасные |

C++ templates — **более мощные**, но и **более сложные**. Go generics — **более простые**, но и **более ограниченные**.

## Q23. Go generics vs Rust?

| Критерий | Go | Rust |
|----------|-----|------|
| Реализация | GCShape гибрид | Pure monomorphization |
| Constraints | Type sets | Traits (намного мощнее) |
| Higher-kinded types | Нет | Нет (но есть associated types) |
| Const generics | Нет | Да |
| Specialization | Нет | Да (nightly) |
| Type inference | Слабый | Сильный |
| Performance | ~95% | 100% (zero-cost) |

Rust traits **намного мощнее** Go interfaces. Можно описывать сложные ограничения:

```rust
fn sum<T: Add<Output = T> + Default + Copy>(items: &[T]) -> T { ... }
```

В Go аналог проще, но ограниченнее.

## Q24. (!) Когда использовать generics, а когда interface{}?

**Generics:**
- Type safety критична
- Производительность важна (no boxing)
- Алгоритмы на коллекциях (Sort, Min, Max, Sum)

**`interface{}` / `any`:**
- Heterogeneous данные (разные типы в одном месте)
- Когда нужен type assertion в runtime
- Простота важнее performance

```go
// Generic — type-safe, fast
func MinGeneric[T constraints.Ordered](a, b T) T { ... }

// interface — flexible
func MinAny(a, b any) any { ... } // нужен type switch внутри
```

**Не превращай весь код в generic** — оставляй для библиотек и общих утилит.

## Q25. (!) Стандартная библиотека и generics?

С Go 1.21 — **`slices`** и **`maps`** пакеты в стандарте:

```go
import "slices"
import "maps"

// slices
slices.Sort([]int{3, 1, 2})
slices.Contains([]string{"a", "b"}, "a")
slices.Index([]int{1, 2, 3}, 2)
slices.Min([]int{3, 1, 4, 1, 5, 9})
slices.Reverse([]int{1, 2, 3})

// maps
maps.Keys(m)    // []K
maps.Values(m)  // []V
maps.Equal(m1, m2)
```

С Go 1.22-1.23 добавляются ещё функции. Стандарт очень осторожно вводит generic API.

## Q26. (!) golang.org/x/exp/slices, maps?

До Go 1.21 — `slices`/`maps` были в `golang.org/x/exp`:

```go
import "golang.org/x/exp/slices"
import "golang.org/x/exp/maps"
import "golang.org/x/exp/constraints"

slices.Sort(...)
maps.Keys(...)
constraints.Ordered
```

С Go 1.21 большинство переехало в стандарт (`slices`, `maps`). `constraints` пока остаётся в exp.

`golang.org/x/exp` — **experimental**, может меняться. Для production — стандартная библиотека.

---

## See also

- [Go (базовый)](go-interview.md) — основы языка
- [Go Standard Library](go-stdlib-interview.md) — slices, maps пакеты
- [Go Concurrency](go-concurrency-interview.md) — generic channels (нет, но channel of T)
- [Java Generics](../java/java-generics-interview.md) — для сравнения erasure
- [Java Collections](../java/java-collections-interview.md) — Java generic коллекции
- [Kotlin](../kotlin/kotlin-interview.md) — generics в Kotlin
- [Scala](../scala/scala-interview.md) — самые мощные generics на JVM
- [Design Patterns](../../design-patterns/design-patterns-interview.md) — где generics упрощают

- [Go Concurrency](go-concurrency-interview.md)
- [Go](go-interview.md)
- [Go Memory и GC](go-memory-gc-interview.md)
- [Go Modules](go-modules-interview.md)
- [Go Standard Library](go-stdlib-interview.md)
- [Go Testing](go-testing-interview.md)
- [Шпаргалка: Go: Generics](../../../languages/go/go-generics.md) — теория
