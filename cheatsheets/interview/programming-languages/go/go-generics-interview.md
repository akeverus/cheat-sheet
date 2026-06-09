---
title: "Вопросы на собеседовании: Go Generics"
description: "Generics в Go 1.18+: type parameters, constraints, comparable, type sets, реализация (monomorphization vs dictionary), ограничения, common patterns"
tags:
  - interview
  - programming-languages
  - go-generics-interview
type: "interview"
difficulty: "intermediate"
aliases:
  - "Вопросы на собеседовании"
  - "Go Generics"
  - "Go generics interview"
  - "Type parameters Go interview"
prerequisites:
  - "[[go-generics]]"
next: []
updated: "2026-04-25"
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
- [Q8. Что означает оператор `~` в constraint (underlying types)?](#q8-что-означает-оператор--в-constraint-underlying-types)

**Generic типы и структуры**
- [Q9. (!) Как объявить и использовать generic-структуру?](#q9--как-объявить-и-использовать-generic-структуру)
- [Q10. Как объявляются методы на generic-типах?](#q10-как-объявляются-методы-на-generic-типах)
- [Q11. (!) Что такое generic-интерфейс?](#q11--что-такое-generic-интерфейс)

**Реализация**
- [Q12. (!) Как реализованы generics — monomorphization или dictionary?](#q12--как-реализованы-generics--monomorphization-или-dictionary)
- [Q13. Что такое GCShape?](#q13-что-такое-gcshape)
- [Q14. Какова производительность generics?](#q14-какова-производительность-generics)

**Patterns**
- [Q15. (!) Как написать generic Map / Filter / Reduce?](#q15--как-написать-generic-map--filter--reduce)
- [Q16. (!) Как реализовать generic-коллекции (Stack, Queue, Set)?](#q16--как-реализовать-generic-коллекции-stack-queue-set)
- [Q17. Как описать constraint для арифметических операций?](#q17-как-описать-constraint-для-арифметических-операций)

**Ограничения**
- [Q18. (!) Какие минусы и ограничения generics в Go?](#q18--какие-минусы-и-ограничения-generics-в-go)
- [Q19. Почему методы не могут вводить свои type-параметры?](#q19-почему-методы-не-могут-вводить-свои-type-параметры)
- [Q20. Где работает вывод типов (type inference)?](#q20-где-работает-вывод-типов-type-inference)

**Сравнения**
- [Q21. (!) Чем Go generics отличаются от Java generics (type erasure)?](#q21--чем-go-generics-отличаются-от-java-generics-type-erasure)
- [Q22. Чем Go generics отличаются от C++ templates?](#q22-чем-go-generics-отличаются-от-c-templates)
- [Q23. Чем Go generics отличаются от Rust generics?](#q23-чем-go-generics-отличаются-от-rust-generics)

**Применение**
- [Q24. (!) Когда использовать generics, а когда interface{}?](#q24--когда-использовать-generics-а-когда-interface)
- [Q25. (!) Как generics используются в стандартной библиотеке?](#q25--как-generics-используются-в-стандартной-библиотеке)
- [Q26. (!) Что такое golang.org/x/exp/slices и maps?](#q26--что-такое-golangorgxexpslices-и-maps)

## Q1. (!) Что такое generics в Go?

**Generics** — это параметризация типов: функция или структура принимает тип как параметр и работает с любым типом, удовлетворяющим заданному ограничению, не теряя при этом type safety.

До generics было два пути и оба плохие: либо дублировать одну и ту же логику под каждый конкретный тип, либо принимать `interface{}` и платить за это потерей проверок на этапе компиляции (плюс boxing). Generics убирают эту дилемму — один обобщённый код, проверяемый компилятором.

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

Это был сознательный выбор: создатели Go (Rob Pike, Ken Thompson) много лет **намеренно** жили без generics, считая, что цена выше выгоды.

**Чего боялись:**
- усложнения языка — Go ценит минимализм и читаемость;
- проблем со взаимодействием generics с error handling и type inference;
- негативного опыта C++ templates — долгая компиляция и нечитаемые сообщения об ошибках.

Поэтому с 2009 по 2022 язык развивался без них, а команда параллельно искала дизайн, который не принесёт этих болей. Ключевой вопрос был **не «делать ли», а «как реализовать»** — и тут два полюса:

- **Monomorphization (C++/Rust)** — компилятор генерирует отдельную копию кода под каждый тип. Максимальная скорость в runtime, но медленная компиляция и раздувание бинарника.
- **Dictionary passing (Haskell, Java)** — один общий код, информация о типе передаётся отдельно через «словарь». Бинарник меньше, но runtime медленнее.

Go 1.18 не стал выбирать крайность, а взял **гибрид (GCShape)**: типы с разной разметкой памяти получают отдельный код, типы с одинаковой — делят общий. Это компромисс между размером бинарника и скоростью.

## Q3. (!) Базовый синтаксис generics?

Type-параметры объявляются в квадратных скобках `[...]` сразу после имени функции или типа, перед обычными аргументами. Каждый параметр — это пара «имя — constraint» (например `[T any]`); внутри тела `T` используется как обычный тип.

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

**Constraint** — это ограничение, которое говорит, какие типы можно подставить вместо type-параметра. Технически constraint всегда является **интерфейсом**: он описывает множество допустимых типов и/или набор методов, которые тип обязан иметь.

Зачем нужно ограничение: компилятор должен заранее знать, какие операции над `T` законны. Если параметр объявлен как `any`, разрешён только тот минимум, что доступен любому типу. Чтобы внутри функции писать `a + b` или `a < b`, нужно ограничить `T` типами, которые эти операции поддерживают — это и делает constraint.

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

То есть constraint — это интерфейс с **type set** (новая возможность Go 1.18): он перечисляет конкретные типы через `|`, а не методы. Подробнее про type sets — в Q6.

## Q5. (!) Что такое any и comparable?

Это два встроенных constraint'а на разных концах спектра: `any` не ограничивает ничего, `comparable` ограничивает типами, которые можно сравнивать.

**`any`** — это алиас для `interface{}`, добавленный в Go 1.18. Ему удовлетворяет любой тип, поэтому внутри функции с `T any` доступен только тот минимум операций, что есть у всех типов (присваивание, передача, сравнение с `nil` через интерфейс).

```go
func Print[T any](v T) { fmt.Println(v) }
```

**`comparable`** — встроенный constraint для типов, которые можно сравнивать через `==` и `!=`. Это даёт ровно то, что нужно для ключей map'ов, множеств и поиска совпадений.

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

**Что входит в `comparable`:** примитивы, structs (если все поля сами comparable), pointers, channels, arrays.

**Что НЕ входит:** slices, maps, functions — их в Go нельзя сравнивать через `==`, поэтому они не удовлетворяют constraint'у и не могут быть ключами map или элементами generic-множества.

## Q6. (!) Type sets — что это?

**Type set** — это множество конкретных типов, которое описывает интерфейс-constraint. Синтаксис появился в Go 1.18: типы перечисляются как union через `|`, и тип удовлетворяет такому интерфейсу, если он входит в это множество.

Идея в том, что обычный интерфейс описывает типы через методы, а type set описывает их напрямую — списком. Это и позволяет ограничивать `T` так, чтобы внутри были доступны операторы (`+`, `<`), а не только вызовы методов. Type set'ы можно комбинировать, ссылаясь на другие интерфейсы:

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

**Важное ограничение:** интерфейс с type set — это **не** обычный интерфейс. Его нельзя использовать как тип переменной или поля — только как constraint в `[...]`:

```go
var x Number  // ERROR — Number это constraint
func Process[T Number](x T) // OK
```

Причина — у такого интерфейса нет общего поведения (методов), которое можно было бы вызвать через значение; он осмыслен лишь как ограничение на этапе компиляции.

## Q7. (!) constraints package — что внутри?

`golang.org/x/exp/constraints` — это набор готовых constraint'ов, чтобы не описывать руками типовые наборы числовых и упорядоченных типов. На момент написания пакет ещё не включён в стандартную библиотеку и живёт в `golang.org/x/exp`.

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

Самый ходовой из них — `Ordered`: он покрывает все типы, поддерживающие операторы `<`, `>`, `<=`, `>=`, то есть числа и строки. Именно на нём строятся обобщённые `Min`, `Max`, сортировки.

## Q8. Что означает оператор `~` в constraint (underlying types)?

`~T` в type set означает «все типы, у которых **underlying type** равен `T`», а не только сам `T`. Это включает любые named-типы, объявленные как `type X T`.

Зачем это нужно: в Go часто объявляют свои типы поверх примитивов (`type MyInt int`, `time.Duration` поверх `int64`). Без `~` constraint `int | float64` принял бы только «голые» `int` и `float64`, но отверг бы `MyInt`, хотя по сути это тот же `int`. Тильда снимает это ограничение.

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

**Рекомендация:** в библиотечных constraint'ах почти всегда используют `~`, чтобы обобщённый код работал и с пользовательскими типами поверх примитивов (например, `time.Duration` имеет underlying `int64`). Без тильды библиотека была бы непригодна для типобезопасных доменных обёрток.

## Q9. (!) Как объявить и использовать generic-структуру?

Структура объявляет свои type-параметры в `[...]` после имени и использует их в типах полей. При создании значения параметры подставляются явно: `Pair[string, int]{...}`. Методы такого типа автоматически получают доступ к этим параметрам (см. Q10).

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

**Приём:** `var zero T` — стандартный способ получить нулевое значение generic-типа. Конкретного литерала вроде `0` или `""` написать нельзя (тип неизвестен на этапе написания кода), а `var zero T` даёт правильный zero value для любого `T`.

## Q10. Как объявляются методы на generic-типах?

В сигнатуре метода receiver указывает type-параметры структуры в скобках: `func (s *Stack[T]) ...`. После этого `T` доступен внутри метода как обычный тип — можно объявлять переменные, возвращать значения, использовать в аргументах.

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

**Ключевое ограничение:** метод **не может вводить собственные** type-параметры. Ему доступны только параметры, объявленные у самой структуры.

```go
// НЕЛЬЗЯ
func (s *Stack[T]) Map[U any](f func(T) U) *Stack[U] { ... }
// ERROR: methods cannot have type parameters
```

Это частый предмет критики: из-за него нельзя написать fluent generic API вида `stack.Map(...).Filter(...)`, где результат меняет тип. Обходной путь — выносить такие операции в обычные функции верхнего уровня (см. Q19).

## Q11. (!) Что такое generic-интерфейс?

Интерфейс тоже может быть параметризован типом: type-параметр объявляется в `[...]` после имени и используется в сигнатурах методов. Это позволяет описывать поведение, зависящее от конкретного типа, — например «уметь сравнивать себя с другим объектом того же типа».

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

Самое мощное здесь — **рекурсивные** constraints вида `T Comparable[T]`: «`T` обязан уметь сравнивать себя с самим собой». Такой приём (self-referential constraint) — основа типобезопасных обобщённых сортировок и упорядоченных структур, где элемент должен знать, как сравнивать себя с соседями.

## Q12. (!) Как реализованы generics — monomorphization или dictionary?

Ни то ни другое в чистом виде — Go использует **гибрид, который называется GCShape stenciling**. Компилятор группирует типы по их «GCShape» и для каждой группы генерирует одну копию кода:

- **отдельный код (как monomorphization)** — для разных GCShape;
- **общий код + dictionary** — для типов с одинаковой GCShape.

**GCShape** — это то, что важно сборщику мусора: размер типа и расположение указателей внутри него. Если две подстановки `T` неразличимы для GC, им незачем иметь разный машинный код.

```go
func Print[T any](v T) { fmt.Println(v) }

Print[int](42)      // одна shape
Print[int64](42)    // та же shape (8 байт, без pointers)
Print[string]("hi") // другая shape
Print[*User](&u)    // ещё одна (pointer)
```

В примере выше `int` и `int64` (оба 8 байт, без указателей) делят одну GCShape, поэтому используют общий код; `string` и `*User` имеют другие shape и получают свои копии.

**Компромисс этого подхода:**
- бинарник меньше, чем при чистом monomorphization (не плодим копию на каждый тип);
- чуть медленнее, чем чистый monomorphization (общий код обращается к dictionary за информацией о типе);
- меньше нагрузки на GC, потому что разметка памяти внутри одной shape одинакова.

## Q13. Что такое GCShape?

**GCShape** — это группировка типов по признакам, которые важны сборщику мусора, а не системе типов. В одну shape попадают типы, неотличимые с точки зрения раскладки памяти:
- одинаковый **размер** (8 байт, 16 байт, ...);
- **наличие/отсутствие указателей** внутри (нужно ли GC сканировать значение);
- одинаковый **layout указателей** — где именно в структуре лежат поля-указатели.

```
int, int64, *User, etc — каждое имеет свою shape
```

Практический смысл: когда generic-код инстанцируется типами одной shape, компилятор генерирует **одну** копию машинного кода на всех; для разных shape — несколько копий. Это та самая «единица переиспользования», на которой держится гибридная реализация из Q12.

## Q14. Какова производительность generics?

Короткий ответ: generics дают **небольшой overhead** по сравнению с написанным вручную не-generic кодом, но при этом **обычно быстрее**, чем подход на `interface{}`.

Откуда берётся overhead относительно специализированного кода:
- **`T` без указателей** — накладные расходы минимальны (порядка 1–3%);
- **`T` с указателями** — overhead заметнее: общий код обращается к dictionary, чтобы GC знал разметку;
- **сравнение с `interface{}`** — здесь generics выигрывают: для value-типов нет boxing'а.

Главная причина выигрыша над `interface{}` — отсутствие boxing/unboxing. Через `interface{}` каждое значение примитива оборачивается (часто с уходом в кучу), а generic-код работает с типом напрямую.

```go
// Generic — быстрее
func SumGeneric[T constraints.Number](nums []T) T

// interface{} — медленнее (boxing на каждое значение)
func SumInterface(nums []interface{}) interface{}
```

## Q15. (!) Как написать generic Map / Filter / Reduce?

Это канонические примеры пользы generics: одна типобезопасная реализация для срезов любого типа. `Map` меняет тип элементов (`T → U`), поэтому требует два параметра; `Filter` сохраняет тип; `Reduce` сворачивает срез в одно значение типа аккумулятора.

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

**Подводный камень:** в отличие от Java Stream API или Kotlin/Scala, в Go нет fluent-цепочек `xs.map(...).filter(...).sum()`. Причина — методы не могут вводить новые type-параметры (Q10/Q19), а `Map` меняет тип. Поэтому такие операции пишутся обычными функциями и комбинируются вложенными вызовами `Filter(Map(...))`, что читается менее линейно.

## Q16. (!) Как реализовать generic-коллекции (Stack, Queue, Set)?

Generic-структуры идеально подходят для контейнеров — одна типобезопасная реализация на любой тип элемента. Обратите внимание на constraint: у `Set` это `comparable`, а не `any`, потому что элементы становятся ключами map'а, а ключ обязан поддерживать `==`.

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

**Сценарий применения:** в Go нет встроенного типа Set, поэтому его обычно реализуют именно так — поверх `map[T]struct{}`. Пустая структура `struct{}` в значениях не занимает память, а generic-параметр даёт типобезопасность без `interface{}`.

## Q17. Как описать constraint для арифметических операций?

Чтобы внутри generic-функции были доступны `+`, `-`, `*`, `/`, нужно ограничить `T` числовыми типами. Удобнее всего собрать собственный constraint из готовых `constraints.Integer` и `constraints.Float`:

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

Так constraint `Numeric` открывает внутри функции **арифметические** операторы (`+`, `-`, `*`, `/`), которые недоступны при `T any`.

**Подводный камень:** результаты разных типов не смешиваются автоматически. В `Average` нельзя написать `Sum(nums) / len(nums)`, потому что `len` возвращает `int`, а сумма имеет тип `T`. Нужна явная конверсия `T(len(nums))` — она в коде выше и приводит длину к типу `T`.

## Q18. (!) Какие минусы и ограничения generics в Go?

Go сознательно сделал generics минималистичными, поэтому набор ограничений довольно широкий:

1. **Методы не могут иметь собственные type-параметры** — отсюда невозможность fluent-цепочек, меняющих тип (`.map().filter()`).
2. **Нет higher-kinded types (HKT)** — нельзя параметризоваться «контейнером» `[F[_]]`, как в Haskell/Scala; обобщить «любой Functor» не получится.
3. **Нет специализации** — нельзя написать особую, оптимизированную ветку кода для конкретного `T`.
4. **Нет const generics** (как в Rust) — нельзя параметризоваться числом `[N int]` для массивов фиксированного размера, известного на этапе компиляции.
5. **Type inference иногда не срабатывает** — в части случаев параметры приходится указывать явно (см. Q20).
6. **Constraints — это интерфейсы, а не type traits** — выразительность ниже, чем у Rust traits.
7. **Рост времени компиляции** — чем больше инстанцирований generics, тем дольше сборка.
8. **Нет идиоматичных sum types** — `Either[L, R]` или полноценные tagged unions через generics не выражаются красиво.
9. **Деление по GCShape — не предел оптимизации** — общий код через dictionary чуть медленнее, чем pure monomorphization.

**Вывод:** среди современных языков Go generics — **самые слабые** по выразительности, но при этом **достаточные** для подавляющего большинства практических задач (контейнеры, утилиты над коллекциями, типобезопасные библиотеки).

## Q19. Почему методы не могут вводить свои type-параметры?

Это намеренное проектное решение: разрешить методам объявлять новые type-параметры — значит резко усложнить реализацию и type inference, а команда Go выбрала простоту. Метод может пользоваться параметрами своей структуры, но добавить свой (например `[U any]`) — нет.

```go
type List[T any] struct { ... }

// НЕЛЬЗЯ — введение нового type parameter в методе
func (l *List[T]) Map[U any](f func(T) U) *List[U] { ... }
```

**Цена этого решения** — отсутствие fluent generic API. Зато реализация и вывод типов остаются предсказуемыми.

**Обходной путь** — вынести операцию в функцию верхнего уровня, где можно объявить оба параметра `[T, U any]`:

```go
func Map[T, U any](l *List[T], f func(T) U) *List[U] { ... }

// Использование
list := NewList[int]()
mapped := Map(list, func(x int) string { ... })
```

Не так красиво, как `list.Map(f)`.

## Q20. Где работает вывод типов (type inference)?

Главное правило: Go выводит type-параметры **из аргументов функции**, но **не** при создании generic-структур. Если компилятор может определить `T` по переданным значениям — указывать его не нужно; если значений для вывода нет, тип придётся задать явно.

```go
func Min[T constraints.Ordered](a, b T) T { ... }

Min(1, 2)         // T выводится как int
Min[int](1, 2)    // явное указание

// Структура — тип нужно указывать
s := Stack[int]{}
// s := Stack{} // ОШИБКА
```

**Подводный камень:** для структур вывода нет — `Stack[int]{}` обязателен, `Stack{}` не скомпилируется, ведь по пустому литералу тип элемента определить нечем. В целом type inference в Go слабее, чем в Rust/Scala, и явное указание параметров встречается чаще.

## Q21. (!) Чем Go generics отличаются от Java generics (type erasure)?

Принципиальная разница в том, **доживает ли информация о типе до runtime**. В Java generics стираются (type erasure) — после компиляции `List<Integer>` неотличим от `List<Object>`. В Go информация о типе сохраняется (через GCShape), поэтому generic-код видит реальный тип и не оборачивает примитивы.

| Критерий | Go | Java |
|----------|-----|------|
| Реализация | Гибрид (monomorphization + dict) | Type erasure |
| Runtime типы | Доступны | Стираются |
| `T.class` | Нет, но есть reflect | Нет (erasure) |
| Performance | Часто быстрее (no boxing) | Boxing для primitives |
| Generic arrays | Не поддерживаются | Не поддерживаются |
| Wildcards | Нет | Да (`? extends T`) |
| Bounds | Type sets (`int | float64`) | `T extends Number` |

**Главное следствие erasure в Java** — boxing примитивов: `List<Integer>` хранит объекты `Integer` в куче, а не примитивы `int` на стеке, что бьёт по памяти и cache-locality. Go generics этой проблемы лишены — `[]int` остаётся срезом `int` без обёрток.

## Q22. Чем Go generics отличаются от C++ templates?

C++ templates используют **чистый monomorphization**: компилятор генерирует отдельную копию кода под каждый тип. Это даёт максимум runtime-производительности (zero overhead), но ценой долгой компиляции, раздувания бинарника и печально известных нечитаемых ошибок. Go выбрал гибрид — менее быстрый, но проще и компактнее.

| Критерий | Go | C++ |
|----------|-----|-----|
| Реализация | Гибрид | Pure monomorphization |
| Compile time | Быстрее | Медленнее (template instantiation) |
| Binary size | Меньше | Больше (по копии для каждого типа) |
| Runtime perf | Очень близко | Максимум (zero overhead) |
| Type sets | Да | C++20 concepts |
| Error messages | Понятные | Известно ужасные |

C++ templates — **более мощные**, но и **более сложные**. Go generics — **более простые**, но и **более ограниченные**.

## Q23. Чем Go generics отличаются от Rust generics?

Rust, как и C++, использует **чистый monomorphization** и даёт zero-cost абстракции. Но главное отличие — в системе ограничений: Rust ограничивает типы через **traits**, которые намного выразительнее интерфейсов Go (associated types, const generics, специализация). Go проще и ограниченнее, Rust мощнее и сложнее.

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

**Эмпирическое правило:** generics — когда все значения одного типа и важна типобезопасность/скорость; `interface{}`/`any` — когда типы разнородны и тип определяется только в runtime.

**Выбирать generics, если:**
- type safety критична — ошибки типов ловятся компилятором, а не в runtime;
- важна производительность — нет boxing'а value-типов;
- это алгоритм над однотипной коллекцией (Sort, Min, Max, Sum).

**Выбирать `interface{}` / `any`, если:**
- данные heterogeneous — в одной структуре лежат значения разных типов;
- тип всё равно проверяется в runtime через type assertion / type switch;
- простота важнее производительности.

```go
// Generic — type-safe, fast
func MinGeneric[T constraints.Ordered](a, b T) T { ... }

// interface — flexible
func MinAny(a, b any) any { ... } // нужен type switch внутри
```

**Рекомендация:** не превращай весь код в generic. Generics оправданы прежде всего в библиотеках и общих утилитах; в обычном прикладном коде они чаще усложняют, чем упрощают.

## Q25. (!) Как generics используются в стандартной библиотеке?

Стандартная библиотека внедряет generics осторожно и точечно. Главный результат — пакеты **`slices`** и **`maps`**, появившиеся в стандарте с Go 1.21: это типобезопасные обобщённые операции над срезами и map'ами, которые раньше пришлось бы писать вручную под каждый тип.

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

В Go 1.22–1.23 набор функций расширяется, но команда добавляет generic API в стандарт очень осторожно — чтобы не закрепить неудачные сигнатуры, которые потом нельзя будет менять из-за гарантий совместимости.

## Q26. (!) Что такое golang.org/x/exp/slices и maps?

Это «прародители» стандартных пакетов: до Go 1.21 обобщённые `slices`, `maps` и `constraints` жили в экспериментальном модуле `golang.org/x/exp`. Туда складывают новые API на обкатку перед тем, как зафиксировать их в стандартной библиотеке.

```go
import "golang.org/x/exp/slices"
import "golang.org/x/exp/maps"
import "golang.org/x/exp/constraints"

slices.Sort(...)
maps.Keys(...)
constraints.Ordered
```

С Go 1.21 бо́льшая часть переехала в стандарт (`slices`, `maps`), а `constraints` пока остаётся в `exp`.

**Рекомендация:** `golang.org/x/exp` помечен как **экспериментальный** — его API может меняться без гарантий совместимости. В production по возможности используйте стандартные `slices`/`maps`, а к `golang.org/x/exp` обращайтесь только за тем, что ещё не переехало (например, `constraints`).

## See also

- [Go (базовый)](go-interview.md) — основы языка
- [Go Standard Library](go-stdlib-interview.md) — slices, maps пакеты
- [Go Concurrency](go-concurrency-interview.md) — generic channels (нет, но channel of T)
- [Java Generics](../java/java-generics-interview.md) — для сравнения erasure
- [Java Collections](../java/java-collections-interview.md) — Java generic коллекции
- [Kotlin](../kotlin/kotlin-interview.md) — generics в Kotlin
- [Scala](../scala/scala-interview.md) — самые мощные generics на JVM
- [Design Patterns](../../design-patterns/design-patterns-interview.md) — где generics упрощают

- [Go](go-interview.md)
- [Go Memory и GC](go-memory-gc-interview.md)
- [Go Modules](go-modules-interview.md)
- [Go Standard Library](go-stdlib-interview.md)
- [Go Testing](go-testing-interview.md)
- [Шпаргалка: Go: Generics](../../../languages/go/go-generics.md) — теория
