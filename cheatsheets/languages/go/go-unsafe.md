---
title: "Go: unsafe операции"
description: "Полное руководство по unsafe операциям в Go: unsafe.Pointer, преобразование типов, низкоуровневые операции"
tags:
  - go
  - golang
  - unsafe
  - pointer
  - low-level
difficulty: "advanced"
prerequisites: ["go/go-basics.md", "go/go-interfaces.md"]
updated: "2026-02-06"
---

# Go: **unsafe** операции

## Полезные ссылки

- [Go unsafe Package](https://pkg.go.dev/unsafe)
- [Go unsafe Pointer](https://go.dev/blog/unsafe)

## Содержание

- [Go: **unsafe** операции](#go-unsafe-операции)
- [Введение в **unsafe**](#введение-в-unsafe)
  - [Когда использовать **unsafe**](#когда-использовать-unsafe)
  - [Предупреждения](#предупреждения)
- [**unsafe.Pointer**](#unsafepointer)
  - [Базовое использование](#базовое-использование)
  - [Правила **unsafe.Pointer**](#правила-unsafepointer)
- [Преобразование типов](#преобразование-типов)
  - [Преобразование между структурами](#преобразование-между-структурами)
  - [Преобразование слайсов](#преобразование-слайсов)
- [Арифметика указателей](#арифметика-указателей)
  - [**unsafe.Offsetof**](#unsafeoffsetof)
  - [**unsafe.Sizeof**](#unsafesizeof)
  - [**unsafe.Alignof**](#unsafealignof)
- [Практические примеры](#практические-примеры)
  - [Преобразование строки в []**byte** без копирования](#преобразование-строки-в-byte-без-копирования)
  - [Оптимизация доступа к полям структуры](#оптимизация-доступа-к-полям-структуры)
  - [Преобразование между **float32** и **uint32**](#преобразование-между-float32-и-uint32)
  - [Детальное использование **unsafe.Sizeof**](#детальное-использование-unsafesizeof)
  - [Детальное использование **unsafe.Alignof**](#детальное-использование-unsafealignof)
  - [Детальное использование **unsafe.Offsetof**](#детальное-использование-unsafeoffsetof)
  - [Преобразование между различными типами](#преобразование-между-различными-типами)
  - [Работа с массивами через **unsafe**](#работа-с-массивами-через-unsafe)
  - [Оптимизация доступа к структурам](#оптимизация-доступа-к-структурам)
  - [Преобразование строк и байтов без копирования](#преобразование-строк-и-байтов-без-копирования)
  - [Работа с **union-like** структурами](#работа-с-union-like-структурами)
  - [Практические примеры: Быстрое копирование памяти](#практические-примеры-быстрое-копирование-памяти)
  - [Практические примеры: Доступ к внутренним полям](#практические-примеры-доступ-к-внутренним-полям)
  - [Практические примеры: Преобразование между **float32** и **uint32**](#практические-примеры-преобразование-между-float32-и-uint32)
  - [Практические примеры: **Zero-copy** преобразования](#практические-примеры-zero-copy-преобразования)
  - [Практические примеры: Оптимизация структур](#практические-примеры-оптимизация-структур)
  - [Практические примеры: Работа с C структурами](#практические-примеры-работа-с-c-структурами)
  - [Практические примеры: **Memory-mapped** структуры](#практические-примеры-memory-mapped-структуры)
  - [Практические примеры: Безопасные обертки](#практические-примеры-безопасные-обертки)
  - [Практические примеры: Небезопасное чтение структур](#практические-примеры-небезопасное-чтение-структур)
  - [Практические примеры: Преобразование слайсов](#практические-примеры-преобразование-слайсов)
  - [Практические примеры: Обход GC для больших данных](#практические-примеры-обход-gc-для-больших-данных)
- [Лучшие практики](#лучшие-практики)
- [Troubleshooting](#troubleshooting)
- [FAQ](#faq)
- [Заключение](#заключение)
- [Дополнительные ресурсы](#дополнительные-ресурсы)

## Введение в **unsafe**

Пакет `**unsafe**` предоставляет возможность обхода системы типов Go для низкоуровневых операций. Использование **unsafe** требует особой осторожности.

### Когда использовать **unsafe**

**Unsafe** полезен для:**
- Преобразования между несовместимыми типами
- Оптимизации производительности
- Взаимодействия с C кодом
- Низкоуровневых операций с памятью

### Предупреждения

1. **Нарушение типобезопасности** — **unsafe** обходит проверки типов
2. **Портативность** — код может быть непереносимым
3. **Сложность отладки** — ошибки сложнее найти
4. **Изменения в Go** — код может сломаться при обновлении Go

## **unsafe.Pointer**

`**unsafe.Pointer**` - это специальный тип указателя, который может быть преобразован в любой другой тип указателя.

### Базовое использование

```go
import "unsafe"

func convertPointer() {
    var x int = 42
    ptr := unsafe.Pointer(&x)

    // Преобразование в другой тип указателя
    var y *float64 = (*float64)(ptr)
    fmt.Println(*y)
}
```

### Правила **unsafe.Pointer**

1. Указатель любого типа может быть преобразован в `**unsafe.Pointer**`
2. `**unsafe.Pointer**` может быть преобразован в указатель любого типа
3. `**uintptr**` может быть преобразован в `**unsafe.Pointer**` и обратно

## Преобразование типов

### Преобразование между структурами

```go
import "unsafe"

type A struct {
    X int
    Y int
}

type B struct {
    X int
    Y int
}

func convertStruct() {
    a := A{X: 1, Y: 2}

    // Преобразование через unsafe.Pointer
    b := (*B)(unsafe.Pointer(&a))
    fmt.Println(b.X, b.Y)  // 1, 2
}
```

### Преобразование слайсов

```go
func convertSlice() {
    intSlice := []int{1, 2, 3, 4}

    // Преобразование в []byte
    byteSlice := *(*[]byte)(unsafe.Pointer(&intSlice))

    // Изменение размера слайса
    header := (*reflect.SliceHeader)(unsafe.Pointer(&intSlice))
    header.Len *= 4  // размер int в байтах
    header.Cap *= 4

    byteSlice = *(*[]byte)(unsafe.Pointer(header))
}
```

## Арифметика указателей

### **unsafe.Offsetof**

```go
import "unsafe"

type Struct struct {
    A int
    B string
    C float64
}

func getFieldOffset() {
    var s Struct

    // Смещение поля B
    offset := unsafe.Offsetof(s.B)
    fmt.Printf("Offset of B: %d\n", offset)

    // Доступ к полю через указатель
    ptr := unsafe.Pointer(&s)
    bPtr := (*string)(unsafe.Pointer(uintptr(ptr) + offset))
    *bPtr = "hello"
    fmt.Println(s.B)  // "hello"
}
```

### **unsafe.Sizeof**

```go
func getSize() {
    var x int
    size := unsafe.Sizeof(x)
    fmt.Printf("Size of int: %d bytes\n", size)

    var s string
    size = unsafe.Sizeof(s)
    fmt.Printf("Size of string: %d bytes\n", size)
}
```

### **unsafe.Alignof**

```go
func getAlignment() {
    var x int
    align := unsafe.Alignof(x)
    fmt.Printf("Alignment of int: %d\n", align)
}
```

## Практические примеры

### Преобразование строки в []**byte** без копирования

```go
func stringToBytes(s string) []byte {
    return *(*[]byte)(unsafe.Pointer(&struct {
        string
        int
    }{s, len(s)}))
}

func bytesToString(b []byte) string {
    return *(*string)(unsafe.Pointer(&b))
}
```

### Оптимизация доступа к полям структуры

```go
type LargeStruct struct {
    A, B, C, D, E, F, G, H int
}

func getFieldFast(s *LargeStruct, fieldIndex int) *int {
    base := uintptr(unsafe.Pointer(s))
    offset := unsafe.Sizeof(int(0)) * uintptr(fieldIndex)
    return (*int)(unsafe.Pointer(base + offset))
}
```

### Преобразование между **float32** и **uint32**

```go
func float32ToUint32(f float32) uint32 {
    return *(*uint32)(unsafe.Pointer(&f))
}

func uint32ToFloat32(u uint32) float32 {
    return *(*float32)(unsafe.Pointer(&u))
}
```

### Детальное использование **unsafe.Sizeof**

```go
import "unsafe"

func demonstrateSizeof() {
    // Размеры примитивных типов
    fmt.Printf("int: %d bytes\n", unsafe.Sizeof(int(0)))
    fmt.Printf("int8: %d bytes\n", unsafe.Sizeof(int8(0)))
    fmt.Printf("int16: %d bytes\n", unsafe.Sizeof(int16(0)))
    fmt.Printf("int32: %d bytes\n", unsafe.Sizeof(int32(0)))
    fmt.Printf("int64: %d bytes\n", unsafe.Sizeof(int64(0)))

    // Размеры указателей
    var x int
    fmt.Printf("pointer to int: %d bytes\n", unsafe.Sizeof(&x))

    // Размеры структур
    type Example struct {
        A int
        B int32
        C int64
    }
    fmt.Printf("Example struct: %d bytes\n", unsafe.Sizeof(Example{}))
}
```

### Детальное использование **unsafe.Alignof**

```go
func demonstrateAlignof() {
    // Выравнивание примитивных типов
    fmt.Printf("int alignment: %d\n", unsafe.Alignof(int(0)))
    fmt.Printf("int64 alignment: %d\n", unsafe.Alignof(int64(0)))

    // Выравнивание структур
    type Unaligned struct {
        A int8
        B int64
    }
    fmt.Printf("Unaligned struct alignment: %d\n", unsafe.Alignof(Unaligned{}))

    // Оптимизированное выравнивание
    type Aligned struct {
        B int64
        A int8
    }
    fmt.Printf("Aligned struct alignment: %d\n", unsafe.Alignof(Aligned{}))
}
```

### Детальное использование **unsafe.Offsetof**

```go
type ComplexStruct struct {
    A int8
    B int64
    C int32
    D int16
    E int64
}

func demonstrateOffsetof() {
    var s ComplexStruct

    fmt.Printf("Offset of A: %d\n", unsafe.Offsetof(s.A))
    fmt.Printf("Offset of B: %d\n", unsafe.Offsetof(s.B))
    fmt.Printf("Offset of C: %d\n", unsafe.Offsetof(s.C))
    fmt.Printf("Offset of D: %d\n", unsafe.Offsetof(s.D))
    fmt.Printf("Offset of E: %d\n", unsafe.Offsetof(s.E))

    // Доступ к полям через offset
    ptr := unsafe.Pointer(&s)
    bOffset := unsafe.Offsetof(s.B)
    bPtr := (*int64)(unsafe.Pointer(uintptr(ptr) + bOffset))
    *bPtr = 42
    fmt.Printf("B value: %d\n", s.B)
}
```

### Преобразование между различными типами

```go
// Преобразование int в float64
func intToFloat64(i int) float64 {
    return *(*float64)(unsafe.Pointer(&i))
}

// Преобразование float64 в int
func float64ToInt(f float64) int {
    return *(*int)(unsafe.Pointer(&f))
}

// Преобразование между различными целочисленными типами
func convertIntTypes() {
    var i32 int32 = 42
    var i64 int64 = *(*int64)(unsafe.Pointer(&i32))
    fmt.Printf("int32 %d -> int64 %d\n", i32, i64)
}
```

### Работа с массивами через **unsafe**

```go
func arrayToSlice(arr *[10]int) []int {
    return (*[10]int)(unsafe.Pointer(arr))[:]
}

func sliceToArray(slice []int, size int) *[10]int {
    if len(slice) < size {
        panic("slice too small")
    }
    return (*[10]int)(unsafe.Pointer(&slice[0]))
}
```

### Оптимизация доступа к структурам

```go
type LargeStruct struct {
    Data [1000]int
}

// Быстрый доступ к элементу массива в структуре
func getElementFast(s *LargeStruct, index int) int {
    if index < 0 || index >= 1000 {
        panic("index out of range")
    }

    base := uintptr(unsafe.Pointer(s))
    offset := unsafe.Offsetof(s.Data)
    elementSize := unsafe.Sizeof(int(0))

    ptr := (*int)(unsafe.Pointer(base + offset + uintptr(index)*elementSize))
    return *ptr
}

// Установка элемента
func setElementFast(s *LargeStruct, index int, value int) {
    if index < 0 || index >= 1000 {
        panic("index out of range")
    }

    base := uintptr(unsafe.Pointer(s))
    offset := unsafe.Offsetof(s.Data)
    elementSize := unsafe.Sizeof(int(0))

    ptr := (*int)(unsafe.Pointer(base + offset + uintptr(index)*elementSize))
    *ptr = value
}
```

### Преобразование строк и байтов без копирования

```go
// Безопасное преобразование string в []byte
func stringToBytes(s string) []byte {
    if len(s) == 0 {
        return nil
    }

    return *(*[]byte)(unsafe.Pointer(&struct {
        ptr unsafe.Pointer
        len int
        cap int
    }{
        ptr: unsafe.Pointer((*reflect.StringHeader)(unsafe.Pointer(&s)).Data),
        len: len(s),
        cap: len(s),
    }))
}

// Безопасное преобразование []byte в string
func bytesToString(b []byte) string {
    if len(b) == 0 {
        return ""
    }

    return *(*string)(unsafe.Pointer(&b))
}

// Использование
func demonstrateStringBytes() {
    s := "hello"
    b := stringToBytes(s)
    fmt.Printf("String: %s, Bytes: %v\n", s, b)

    s2 := bytesToString(b)
    fmt.Printf("Back to string: %s\n", s2)
}
```

### Работа с **union-like** структурами

```go
// Эмуляция union через unsafe
type Union struct {
    data [8]byte
}

func (u *Union) SetInt32(value int32) {
    *(*int32)(unsafe.Pointer(&u.data[0])) = value
}

func (u *Union) GetInt32() int32 {
    return *(*int32)(unsafe.Pointer(&u.data[0]))
}

func (u *Union) SetFloat32(value float32) {
    *(*float32)(unsafe.Pointer(&u.data[0])) = value
}

func (u *Union) GetFloat32() float32 {
    return *(*float32)(unsafe.Pointer(&u.data[0]))
}
```

### Практические примеры: Быстрое копирование памяти

```go
// Быстрое копирование через unsafe
func fastCopy(dst, src []byte) {
    if len(dst) < len(src) {
        panic("destination too small")
    }

    dstPtr := unsafe.Pointer(&dst[0])
    srcPtr := unsafe.Pointer(&src[0])
    size := uintptr(len(src))

    copy(*(*[]byte)(unsafe.Pointer(&struct {
        ptr unsafe.Pointer
        len int
        cap int
    }{dstPtr, len(dst), cap(dst)})), src)
}
```

### Практические примеры: Доступ к внутренним полям

```go
// Доступ к внутренним полям слайса
type SliceHeader struct {
    Data uintptr
    Len  int
    Cap  int
}

func getSliceHeader(slice []int) *SliceHeader {
    return (*SliceHeader)(unsafe.Pointer(&slice))
}

func modifySliceLen(slice []int, newLen int) []int {
    header := getSliceHeader(slice)
    if newLen > header.Cap {
        panic("new length exceeds capacity")
    }
    header.Len = newLen
    return *(*[]int)(unsafe.Pointer(header))
}
```

### Практические примеры: Преобразование между **float32** и **uint32**

```go
// Быстрое преобразование float32 <-> uint32
func Float32Bits(f float32) uint32 {
    return *(*uint32)(unsafe.Pointer(&f))
}

func Float32FromBits(b uint32) float32 {
    return *(*float32)(unsafe.Pointer(&b))
}

// Использование для битовых операций
func manipulateFloatBits(f float32) float32 {
    bits := Float32Bits(f)
    // Инвертирование знака
    bits ^= 0x80000000
    return Float32FromBits(bits)
}
```

### Практические примеры: **Zero-copy** преобразования

```go
// Zero-copy преобразование между совместимыми типами
func zeroCopyConvert[T, U any](src []T) []U {
    if len(src) == 0 {
        return nil
    }

    // Проверка совместимости размеров
    if unsafe.Sizeof(T(0)) != unsafe.Sizeof(U(0)) {
        panic("types have different sizes")
    }

    srcHeader := (*reflect.SliceHeader)(unsafe.Pointer(&src))
    dstHeader := &reflect.SliceHeader{
        Data: srcHeader.Data,
        Len:  srcHeader.Len,
        Cap:  srcHeader.Cap,
    }

    return *(*[]U)(unsafe.Pointer(dstHeader))
}
```

### Практические примеры: Оптимизация структур

```go
// Оптимизация доступа к полям структуры
type OptimizedStruct struct {
    fields [100]int
}

func getFieldOptimized(s *OptimizedStruct, index int) int {
    base := uintptr(unsafe.Pointer(s))
    offset := unsafe.Offsetof(s.fields)
    elementSize := unsafe.Sizeof(int(0))

    ptr := (*int)(unsafe.Pointer(base + offset + uintptr(index)*elementSize))
    return *ptr
}
```

### Практические примеры: Работа с C структурами

```go
/*
#include <stdint.h>
typedef struct {
    int32_t x;
    int32_t y;
} Point;
*/
import "C"
import "unsafe"

type GoPoint struct {
    X int32
    Y int32
}

func convertToCPoint(p *GoPoint) *C.Point {
    return (*C.Point)(unsafe.Pointer(p))
}

func convertFromCPoint(cp *C.Point) *GoPoint {
    return (*GoPoint)(unsafe.Pointer(cp))
}
```

### Практические примеры: **Memory-mapped** структуры

```go
// Работа с memory-mapped структурами
type MemoryMappedStruct struct {
    Header [16]byte
    Data   [1024]byte
}

func readFromMemory(addr uintptr) *MemoryMappedStruct {
    return (*MemoryMappedStruct)(unsafe.Pointer(addr))
}

func writeToMemory(addr uintptr, data *MemoryMappedStruct) {
    dst := (*MemoryMappedStruct)(unsafe.Pointer(addr))
    *dst = *data
}
```

### Практические примеры: Безопасные обертки

```go
// Безопасная обертка для unsafe операций
type SafeUnsafe struct{}

func (s *SafeUnsafe) ConvertStringToBytes(str string) ([]byte, error) {
    if len(str) == 0 {
        return nil, nil
    }

    // Проверка валидности строки
    if !utf8.ValidString(str) {
        return nil, fmt.Errorf("invalid UTF-8 string")
    }

    return stringToBytes(str), nil
}

func (s *SafeUnsafe) ConvertBytesToString(b []byte) (string, error) {
    if len(b) == 0 {
        return "", nil
    }

    // Проверка валидности UTF-8
    if !utf8.Valid(b) {
        return "", fmt.Errorf("invalid UTF-8 bytes")
    }

    return bytesToString(b), nil
}
```

### Практические примеры: Небезопасное чтение структур

```go
// Чтение приватных полей структуры (только для отладки)
type PrivateStruct struct {
    private int
    public  string
}

func ReadPrivateField(s *PrivateStruct) int {
    // Получение указателя на первое поле
    ptr := unsafe.Pointer(s)

    // Чтение int значения
    return *(*int)(ptr)
}

// Чтение с учетом смещения полей
func ReadFieldByOffset(s *PrivateStruct, offset uintptr) unsafe.Pointer {
    base := uintptr(unsafe.Pointer(s))
    return unsafe.Pointer(base + offset)
}
```

### Практические примеры: Преобразование слайсов

```go
// Преобразование []byte в []int32 (небезопасно)
func BytesToInt32s(b []byte) []int32 {
    if len(b)%4 != 0 {
        return nil
    }

    header := (*reflect.SliceHeader)(unsafe.Pointer(&b))
    header.Len /= 4
    header.Cap /= 4

    return *(*[]int32)(unsafe.Pointer(header))
}

// Преобразование []int32 в []byte
func Int32sToBytes(i []int32) []byte {
    header := (*reflect.SliceHeader)(unsafe.Pointer(&i))
    header.Len *= 4
    header.Cap *= 4

    return *(*[]byte)(unsafe.Pointer(header))
}
```

### Практические примеры: Обход `GC` для больших данных

```go
import "runtime"

// Выделение памяти без GC
func AllocateNoGC(size int) unsafe.Pointer {
    ptr := make([]byte, size)
    runtime.KeepAlive(ptr) // Предотвращение сборки мусора

    header := (*reflect.SliceHeader)(unsafe.Pointer(&ptr))
    return unsafe.Pointer(header.Data)
}

// Освобождение памяти (осторожно!)
func FreeNoGC(ptr unsafe.Pointer, size int) {
    // В Go обычно не требуется явное освобождение,
    // но для специальных случаев может быть необходимо
    runtime.KeepAlive(ptr)
}
```

### Практические примеры: Оптимизация структур

```go
// Получение размера структуры
func GetStructSize(v interface{}) uintptr {
    return reflect.TypeOf(v).Size()
}

// Получение смещения полей
func GetFieldOffset(typ reflect.Type, fieldIndex int) uintptr {
    field := typ.Field(fieldIndex)
    return field.Offset
}

// Проверка выравнивания структуры
func CheckAlignment(typ reflect.Type) bool {
    size := typ.Size()
    align := typ.Align()
    return size%align == 0
}

// Оптимизация порядка полей
type OptimizedStruct struct {
    // 8 байт
    Field1 int64

    // 4 байта
    Field2 int32

    // 1 байт + 3 байта padding
    Field3 bool
}
```

## Лучшие практики

1. **Избегайте unsafe когда возможно** — используйте обычный код для безопасности
2. **Документируйте использование** — объясняйте, почему используется **unsafe**
3. **Тестируйте тщательно** — **unsafe** код требует особого тестирования
4. **Проверяйте границы** — убедитесь, что операции безопасны
5. **Используйте комментарии** — объясняйте неочевидные преобразования
6. **Проверяйте выравнивание** — учитывайте **alignment** при работе с памятью
7. **Используйте безопасные обертки** — создавайте безопасные **API** поверх **unsafe**
8. **Тестируйте на разных платформах** — **unsafe** код может быть непереносимым
9. **Избегайте в production** — используйте **unsafe** только когда действительно необходимо
10. **Проверяйте валидность данных** — всегда валидируйте данные перед **unsafe** операциями
11. **Используйте для оптимизации** — только когда измерения показывают необходимость
12. **Понимайте memory model** — знайте, как Go управляет памятью
13. **Используйте для совместимости** — для работы с C кодом или системными вызовами
14. **Проверяйте на race conditions** — **unsafe** код может создавать **race conditions**
15. **Используйте осторожно** — **unsafe** код может привести к неопределенному поведению


## Решение проблем

Типичные проблемы и решения см. в официальной документации (блок «Полезные ссылки» в начале документа).

## Частые вопросы

Ответы на частые вопросы по теме см. в разделах «Введение» и «Лучшие практики» в документе.

## Заключение

**Unsafe** операции в Go предоставляют возможность обхода системы типов для низкоуровневых операций. Понимание **unsafe.Pointer**, преобразования типов, арифметики указателей, работы с памятью, оптимизации структур и практических применений критично для эффективного использования **unsafe** в Go, но требует особой осторожности. Правильное использование **unsafe** позволяет создавать высокопроизводительный код для специальных случаев, но должно быть тщательно продумано, задокументировано, протестировано и использоваться только когда действительно необходимо.

## Дополнительные ресурсы

- [Go unsafe Package](https://pkg.go.dev/unsafe)
- [Go unsafe Pointer](https://go.dev/blog/unsafe)

## См. также

- [[go-advanced-patterns|Go: продвинутые паттерны]]
- [[go-basics|Go: основы]]
- [[go-benchmarking|Go: бенчмаркинг]]
- [[go-best-practices|Go: лучшие практики]]
- [[go-build|Go: сборка и развертывание]]
