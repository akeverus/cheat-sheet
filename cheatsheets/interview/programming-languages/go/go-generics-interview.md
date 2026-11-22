---
title: "Вопросы на собеседовании: Дженерики Go"
description: "Generics в Go 1.18–1.27: type parameters, constraints, generic methods, type inference, реализация и ограничения"
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
updated: "2026-10-05"
---
# Вопросы на собеседовании: Дженерики Go

Generics появились в Go только с **версии 1.18 (март 2022)** — на 13 лет позже релиза языка. Параметры типов ограничиваются интерфейсами и множествами типов. Стандартный компилятор может делить код между инстанциациями, но стратегия компиляции не входит в контракт языка.

## Полезные ссылки

### Официальная документация и авторитетные источники

- [Type Parameters Proposal](https://github.com/golang/proposal/blob/master/design/43651-type-parameters.md)
- [Tutorial: Getting started with generics](https://go.dev/doc/tutorial/generics)
- [Generics Implementation — Go Blog](https://go.dev/blog/intro-generics)
- [Go 1.27 Release Notes — generic methods и type inference](https://go.dev/doc/go1.27)
- [The Generics Implementation Design](https://github.com/golang/proposal/blob/master/design/generics-implementation-gcshape.md)
- [Generics by Example — Eli Bendersky](https://eli.thegreenplace.net/2022/an-extensive-look-at-go-generics/)

## Содержание

- [Полезные ссылки](#полезные-ссылки)
- [See also](#see-also)

**Базовые понятия**
- [Q1. (!) Что такое generics в Go и как они помогают повторно использовать код с проверкой типов?](#q1--что-такое-generics-в-go-и-как-они-помогают-повторно-использовать-код-с-проверкой-типов)
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
- [Q10. Как метод generic-типа использует параметр типа своего receiver?](#q10-как-метод-generic-типа-использует-параметр-типа-своего-receiver)
- [Q11. (!) Что такое generic-интерфейс?](#q11--что-такое-generic-интерфейс)

**Реализация**
- [Q12. (!) Как реализованы generics — monomorphization или dictionary?](#q12--как-реализованы-generics--monomorphization-или-dictionary)
- [Q13. Что такое GCShape?](#q13-что-такое-gcshape)
- [Q14. Какова производительность generics?](#q14-какова-производительность-generics)

**Patterns**
- [Q15. (!) Как написать generic Map / Filter / Reduce?](#q15--как-написать-generic-map--filter--reduce)
- [Q16. (!) Как реализовать generic Set на основе map?](#q16--как-реализовать-generic-set-на-основе-map)
- [Q17. Как описать constraint для арифметических операций?](#q17-как-описать-constraint-для-арифметических-операций)

**Ограничения**
- [Q18. (!) Какие минусы и ограничения generics в Go?](#q18--какие-минусы-и-ограничения-generics-в-go)
- [Q19. Что Go 1.27 разрешил методам конкретных типов и какая граница осталась у интерфейсов?](#q19-что-go-127-разрешил-методам-конкретных-типов-и-какая-граница-осталась-у-интерфейсов)
- [Q20. Где работает вывод типов (type inference)?](#q20-где-работает-вывод-типов-type-inference)

**Сравнения**
- [Q21. (!) Чем Go generics отличаются от Java generics (type erasure)?](#q21--чем-go-generics-отличаются-от-java-generics-type-erasure)
- [Q22. Чем Go generics отличаются от C++ templates?](#q22-чем-go-generics-отличаются-от-c-templates)
- [Q23. Чем Go generics отличаются от Rust generics?](#q23-чем-go-generics-отличаются-от-rust-generics)

**Применение**
- [Q24. (!) Когда использовать generics, а когда interface{}?](#q24--когда-использовать-generics-а-когда-interface)
- [Q25. (!) Как generics используются в стандартной библиотеке?](#q25--как-generics-используются-в-стандартной-библиотеке)
- [Q26. (!) Что такое golang.org/x/exp/slices и maps?](#q26--что-такое-golangorgxexpslices-и-maps)

## Q1. (!) Что такое generics в Go и как они помогают повторно использовать код с проверкой типов?

**Generics** — это параметризация типов: функция или структура принимает тип как параметр и работает с любым типом, удовлетворяющим заданному ограничению, не теряя при этом type safety.

До generics алгоритм для нескольких типов часто приходилось дублировать или строить вокруг `interface{}` с проверкой типов во время выполнения. Параметры типов позволяют выразить связь между входом и результатом и проверить допустимые операции компилятором.

```go
// Без generics — для каждого типа отдельная функция или interface{}
func MaxInt(a, b int) int    { if a > b { return a }; return b }
func MaxFloat(a, b float64) float64 { ... }

// С generics — одна функция
func Max[T cmp.Ordered](a, b T) T {
    if a > b { return a }
    return b
}

Max(3, 5)         // 5 (int)
Max(3.14, 2.71)   // 3.14 (float64)
Max("apple", "banana") // "banana" (string)
```

## Q2. (!) Почему generics добавили только в Go 1.18?

Ранний Go не включал параметрический полиморфизм ради простоты языка. Позже задачи повторного использования — например, один типобезопасный алгоритм для разных срезов — показали цену этого решения. Разработчики искали форму, которая сохраняет ясность программ и не требует дублирования или динамических проверок там, где типы известны компилятору.

Параметры типов вошли в Go 1.18 после нескольких обсуждений и вариантов дизайна. Историю решения нельзя свести к одной технической причине вроде сборщика мусора, формата байткода или единственного выбора между копированием кода и словарями. [Go FAQ](https://go.dev/doc/faq), [Why Generics?](https://go.dev/blog/why-generics), [Go 1.18 release notes](https://go.dev/doc/go1.18).

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

**`any`** — это алиас для `interface{}`, добавленный в Go 1.18. Ему удовлетворяет любой тип, поэтому внутри функции с `T any` доступны только операции, допустимые для всех типов из его множества. Например, `T any` нельзя сравнить с `nil`, поскольку T может оказаться числом.

```go
func Print[T any](v T) { fmt.Println(v) }
```

**`comparable`** — встроенный constraint, позволяющий применять `==` и `!=` к параметру типа. Он нужен, например, для ключей map и generic-множества.

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

**Обычные slices, maps и functions** нельзя сравнивать друг с другом через `==`, поэтому они не подходят для ключей map или элементов generic-множества. Интерфейсный аргумент типа требует отдельной оговорки ниже.

**Нюанс Go 1.20 (частый senior follow-up):** с этой версии **интерфейсные типы** (включая `any`) удовлетворяют constraint'у `comparable`, хотя строго сравнимыми не являются. То есть `Contains[any](...)` компилируется — но если внутри интерфейсов окажутся несравнимые динамические типы (например, два slice), сравнение `==` упадёт в runtime: `panic: comparing uncomparable type []int`. Вывод: `comparable` гарантирует, что `==` скомпилируется, но для интерфейсов не гарантирует отсутствие паники.

## Q6. (!) Type sets — что это?

**Type set** — множество типов, задаваемое интерфейсом. Даже обычный методный интерфейс описывает множество типов, у которых есть требуемые методы. С Go 1.18 в интерфейсе можно также писать термы типов: `int | string` объединяет типы, `~int` включает все типы с underlying type `int`, а отдельные элементы интерфейса пересекают ограничения.

```go
type Integer interface {
    ~int | ~int8 | ~int64 | ~uint | ~uint64
}

func Twice[T Integer](x T) T { return x + x }
```

Интерфейс с не-методными термами используют как constraint, но не как тип переменной. Операции внутри generic-функции должны быть допустимы для всех типов из набора. [Go specification: Interface types](https://go.dev/ref/spec#Interface_types).

## Q7. (!) constraints package — что внутри?

`golang.org/x/exp/constraints` — это набор готовых constraint'ов, чтобы не описывать руками типовые наборы числовых и упорядоченных типов. На момент написания пакет ещё не включён в стандартную библиотеку и живёт в `golang.org/x/exp`.

```go
import "golang.org/x/exp/constraints"

constraints.Signed     // int, int8, int16, int32, int64 (+ их named types)
constraints.Unsigned   // uint, uint8, ...
constraints.Integer    // Signed | Unsigned
constraints.Float      // float32 | float64
constraints.Complex    // complex64 | complex128
constraints.Ordered    // сейчас псевдоним cmp.Ordered

func Min[T constraints.Ordered](a, b T) T {
    if a < b { return a }
    return b
}
```

Самый ходовой из них — `Ordered`: он покрывает все типы, поддерживающие операторы `<`, `>`, `<=`, `>=`, то есть числа и строки. Именно на нём строятся обобщённые `Min`, `Max`, сортировки.

Для упорядоченных типов с Go 1.21 можно использовать стандартный `cmp.Ordered`. Текущий `constraints.Ordered` является его псевдонимом; если нужен только Ordered, зависимость от x/exp не требуется.

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

Используйте `~`, когда функция должна поддерживать и именованные типы с нужным underlying type, например `time.Duration` для `~int64`. Если API намеренно принимает только точный тип, тильда не нужна.

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

## Q10. Как метод generic-типа использует параметр типа своего receiver?

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

Параметр `T` принадлежит типу `Stack[T]`; метод использует его без повторного объявления после имени. Если методу нужен собственный новый параметр типа, это отдельная возможность Go 1.27, разобранная в Q19.

## Q11. (!) Что такое generic-интерфейс?

Интерфейс тоже может быть параметризован типом: type-параметр объявляется в `[...]` после имени и используется в сигнатурах методов. Это позволяет описывать поведение, зависящее от конкретного типа, — например «уметь сравнивать себя с другим объектом того же типа».

```go
type Comparable[T any] interface {
    Compare(other T) int
}

type IntWrapper struct{ v int }
func (i IntWrapper) Compare(other IntWrapper) int {
    if i.v < other.v { return -1 }
    if i.v > other.v { return 1 }
    return 0
}

func Sort[T Comparable[T]](items []T) {
    // ...
}
```

Самое мощное здесь — **рекурсивные** constraints вида `T Comparable[T]`: «`T` обязан уметь сравнивать себя с самим собой». Такой приём (self-referential constraint) — основа типобезопасных обобщённых сортировок и упорядоченных структур, где элемент должен знать, как сравнивать себя с соседями.

## Q12. (!) Как реализованы generics — monomorphization или dictionary?

Язык задаёт поведение generic-кода и проверку ограничений, но не обязывает компилятор использовать одну конкретную схему генерации. По [Go FAQ](https://go.dev/doc/faq#how_are_generics_implemented), стандартный компилятор обычно выпускает одну инстанциацию для аргументов типа с одинаковой *shape*. Среди свойств shape — размер значения и расположение указателей.

Общая реализация может получать дополнительные сведения о конкретном типе. Это отличается и от обязательной полной копии кода для каждого типа, и от стирания всех аргументов типа в `interface{}`. Точные группы и итоговая производительность относятся к версии компилятора и программе; их нельзя вывести только из синтаксиса вызова.

## Q13. Что такое GCShape?

**Shape** — внутренняя группировка аргументов типа, которую стандартный компилятор Go использует для совместного выполнения generic-кода. В [Go FAQ](https://go.dev/doc/faq#how_are_generics_implemented) названы свойства представления, включая размер и расположение указателей.

Типы одного размера с разной раскладкой указателей могут требовать разных инстанциаций. Но универсальное правило «все указатели всегда вместе, каждый value-тип отдельно» не является гарантией языка. Для анализа размера бинарника смотрите результат сборки конкретной версии компилятора.

## Q14. Какова производительность generics?

У generics нет фиксированного процента накладных расходов. Стандартный компилятор может разделять код между типами одной shape, а оптимизатор по-разному встраивает и упрощает вызовы. Сравнение с реализацией на конкретном типе или `any` зависит от данных и доступа к ним.

`interface{}` не означает обязательную heap-аллокацию каждого значения; generics тоже не обещают отсутствие всех аллокаций. Выбирайте параметры типов, когда они делают контракт типобезопасным и избавляют от дублирования. Если скорость существенна, сравните варианты бенчмарком на своей нагрузке. [When To Use Generics](https://go.dev/blog/when-generics), [Go FAQ](https://go.dev/doc/faq#how_are_generics_implemented).

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

**Версионная граница:** показанные функции подходят и для Go 1.18–1.26. Начиная с Go 1.27 преобразование, меняющее тип, можно оформить generic-методом конкретного типа (Q10/Q19); однако для произвольного `[]T` нельзя объявить метод без собственного определённого типа receiver.

## Q16. (!) Как реализовать generic Set на основе map?

Для множества на основе map нужен `Set[T comparable]`: элементы становятся ключами, а ключ должен допускать сравнение. Методы могут работать с T без type assertion. Сравнимость интерфейсного аргумента типа требует оговорки из Q5.

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

**Подводный камень:** результаты разных типов не смешиваются автоматически. В `Average` нельзя написать `Sum(nums) / len(nums)`, потому что `len` возвращает `int`, а сумма имеет тип `T`. Нужна явная конверсия `T(len(nums))`. Для целочисленного `T` результат деления тоже целочисленный; если нужна дробная средняя, интерфейс функции должен явно задавать соответствующий тип результата.

## Q18. (!) Какие минусы и ограничения generics в Go?

У generics остаются ограничения, но часть старых списков устарела после Go 1.27:

1. **Интерфейсные методы не могут объявлять type-параметры**, а generic-метод не реализует метод интерфейса. Обычные методы могут вводить параметры с Go 1.27 (Q19).
2. **Нет higher-kinded types (HKT)** — нельзя параметризоваться «контейнером» `[F[_]]`, как в Haskell/Scala; обобщить «любой Functor» не получится.
3. **Нет специализации** — нельзя написать особую, оптимизированную ветку кода для конкретного `T`.
4. **Нет const generics** (как в Rust) — нельзя параметризоваться числом `[N int]` для массивов фиксированного размера, известного на этапе компиляции.
5. **Type inference не универсален** — в части случаев параметры приходится указывать явно; в Go 1.27 расширен вывод для присваивания generic-функции подходящему функциональному типу (Q20).
6. **Constraints — это интерфейсы, а не type traits** — выразительность ниже, чем у Rust traits.
7. **Цена компиляции зависит от программы** — множество инстанцирований может увеличить время сборки и размер кода; измерять на конкретном проекте.
8. **Нет идиоматичных sum types** — `Either[L, R]` или полноценные tagged unions через generics не выражаются красиво.
9. **Оптимизация зависит от реализации и кода** — общий код через dictionary в некоторых сценариях может уступать специализации; универсального вывода о скорости нет.

**Вывод:** границы выразительности важны для дизайна API; не следует выдавать ограничения Go 1.18–1.26 за действующие правила Go 1.27.

## Q19. Что Go 1.27 разрешил методам конкретных типов и какая граница осталась у интерфейсов?

До Go 1.27 метод мог пользоваться параметрами типа receiver, но не мог объявлять собственные. В **Go 1.27** запрет для методов конкретных типов снят:

```go
type Box[T any] struct { value T }

func (b Box[T]) Map[U any](f func(T) U) Box[U] {
    return Box[U]{value: f(b.value)}
}
```

`Box[int]{value: 1}.Map(func(n int) string { return "ok" })` возвращает `Box[string]`. Для Go 1.18–1.26 ту же операцию оформляли функцией верхнего уровня.

**Оставшаяся граница:** методы **интерфейсов** не объявляют type-параметры; generic-метод конкретного типа не может служить реализацией интерфейсного метода. Для поддержки Go 1.18–1.26 можно сохранить функцию верхнего уровня:

```go
func Map[T, U any](b Box[T], f func(T) U) Box[U] {
    return Box[U]{value: f(b.value)}
}
```

Для интерфейсного контракта зафиксируйте тип результата в сигнатуре метода. Например, адаптер выбирает `string` и делегирует преобразование generic-методу:

```go
type StringMapper interface {
    Map(func(int) string) Box[string]
}

type IntBoxMapper struct{ box Box[int] }

func (m IntBoxMapper) Map(f func(int) string) Box[string] {
    return m.box.Map(f)
}

var _ StringMapper = IntBoxMapper{}
```

`Box[int]` сам не удовлетворяет `StringMapper`: при выборе реализации интерфейса Go не подставляет `string` в параметр `U`. Если вызывающему коду достаточно сохранить преобразование как функцию, `box.Map[string]` фиксирует `U` и даёт значение типа `func(func(int) string) Box[string]` для `box` типа `Box[int]`. [Спецификация Go — инстанцирование](https://go.dev/ref/spec#Instantiations).

[Официальные release notes Go 1.27](https://go.dev/doc/go1.27) фиксируют обе границы.

## Q20. Где работает вывод типов (type inference)?

Go выводит type-параметры из аргументов вызова функции. С **Go 1.21** подходящий функциональный тип уже позволяет вывести параметры при присваивании или возврате функции. С **Go 1.27** вывод generic-функции расширен и на все контексты присваивания или преобразования к подходящему функциональному типу. Для создания экземпляра generic-типа аргументы типа по-прежнему указывают явно.

```go
func Identity[T any](v T) T { return v }

Identity(1)                      // T выводится как int
var identityInt func(int) int = Identity // вывод из функционального типа, Go 1.21+

type IntIdentity func(int) int

converted := IntIdentity(Identity)      // Go 1.27+
explicit := IntIdentity(Identity[int])  // Go 1.18+

// Структура — тип нужно указывать
s := Stack[int]{}
// s := Stack{} // ОШИБКА
```

В присваивании тип переменной уже известен. При явном преобразовании Go 1.27 также берёт сигнатуру из целевого типа `IntIdentity` и выводит `T = int`. В Go 1.21–1.26 строка с присваиванием работает, а для преобразования нужна инстанциация `Identity[int]`.

**Подводный камень:** `Stack{}` не скомпилируется: для generic-типа нужен явный аргумент типа, например `Stack[int]{}`. Если контекст не даёт компилятору вывести параметры generic-функции, их также задают явно. Например, при `f := Identity` функциональный тип ещё неизвестен, а `f := Identity[int]` фиксирует его как `func(int) int`. [Go 1.21 — вывод типов](https://go.dev/doc/go1.21#language), [Go 1.27 — изменения языка](https://go.dev/doc/go1.27#language).

## Q21. (!) Чем Go generics отличаются от Java generics (type erasure)?

Java использует **type erasure**: аргумент типа у экземпляра `List<Integer>` не становится отдельным runtime-классом списка. При этом generic-объявления могут сохраняться в метаданных сигнатур, поэтому фраза «все сведения о generics в runtime исчезают» слишком сильна.

В Go инстанцированные generic-типы различимы в системе типов и при reflection. Способ генерации машинного кода остаётся выбором компилятора. `List<Integer>` хранит ссылки на boxed Integer, а Go `[]int` — значения int; из этого не следует универсальный коэффициент скорости или памяти для любых программ. [Go FAQ](https://go.dev/doc/faq#how_are_generics_implemented), [Java type erasure](https://docs.oracle.com/javase/tutorial/java/generics/erasure.html).

## Q22. Чем Go generics отличаются от C++ templates?

C++ templates и Go generics позволяют писать код для нескольких типов, но проверяют допустимые операции по-разному. В Go операции над `T` должны быть разрешены constraint при проверке generic-функции. В C++ используются подстановка шаблона и, если задано, ограничения `concepts`.

C++ допускает пользовательскую специализацию шаблонов для отдельных типов. Go не предоставляет такую форму специализации. Размер бинарника, время компиляции и скорость выполнения зависят от конкретных компиляторов и программ; обещать победителя без измерения нельзя.

## Q23. Чем Go generics отличаются от Rust generics?

Rust описывает требования к типам через **traits** и поддерживает связанные типы и const generics. Go использует интерфейсы с методами и наборами типов; const generics в нём нет.

```rust
fn sum<T: Add<Output = T> + Default + Copy>(items: &[T]) -> T { /* ... */ }
```

В Go оператор `+` разрешают через ограничение, включающее подходящие типы. Различие в выразительности не задаёт фиксированный процент производительности. Экспериментальные возможности Rust, в том числе specialization, нельзя описывать как безусловно доступный стабильный API.

## Q24. (!) Когда использовать generics, а когда interface{}?

Используйте generics, когда один алгоритм или контейнер должен сохранять связь между конкретными типами при компиляции: например, `func First[T any](xs []T) T`. Используйте `any`, когда значения намеренно разнородны или тип известен только во время выполнения.

Если функция работает с одним конкретным типом, обычная сигнатура часто проще. Ни generics, ни `any` сами по себе не дают гарантии скорости: для горячего пути измеряйте реализацию и аллокации. [When To Use Generics](https://go.dev/blog/when-generics).

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
maps.Equal(m1, m2)           // Go 1.21
maps.Keys(m)                 // iter.Seq[K] — итератор, std с Go 1.23
maps.Values(m)               // iter.Seq[V] — итератор, std с Go 1.23
slices.Collect(maps.Keys(m)) // []K — слайс собирается через slices.Collect
```

Важный нюанс с `maps.Keys`/`maps.Values`: в стандартном пакете `maps` (Go 1.21) этих функций НЕ было — они появились только в Go 1.23 и возвращают **итератор** `iter.Seq[K]`, а не слайс; слайс из него собирают через `slices.Collect`. Вариант с сигнатурой «сразу слайс» — это другой пакет, `golang.org/x/exp/maps`, прежний путь до стандартизации.

Сюда же относится пакет **`cmp`** (std с Go 1.21): его `cmp.Ordered` — стандартная замена `constraints.Ordered`, и именно на нём построены `slices.Sort`, `slices.Min`, `slices.Max`.

В Go 1.22–1.23 набор функций расширяется (итераторы, `slices.Collect`), но команда добавляет generic API в стандарт очень осторожно — чтобы не закрепить неудачные сигнатуры, которые потом нельзя будет менять из-за гарантий совместимости.

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

С Go 1.21 стандарт содержит пакеты `slices` и `maps`. `constraints` остаётся в x/exp, а его `Ordered` сейчас является псевдонимом `cmp.Ordered` из стандартного пакета `cmp`. API одноимённых экспериментальных пакетов и стандартных пакетов могут отличаться по версии.

**Рекомендация:** `golang.org/x/exp` помечен как **экспериментальный** — его API может меняться без гарантий совместимости. В production по возможности используйте стандартные `slices`/`maps`/`cmp`, а к `golang.org/x/exp` обращайтесь только за тем, чего в стандарте ещё нет (например, `constraints.Integer`/`Float`).

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
