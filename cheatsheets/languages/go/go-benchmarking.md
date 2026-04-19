---
title: "Go: бенчмаркинг"
description: "Полное руководство по бенчмаркингу в Go: написание benchmarks, анализ результатов, оптимизация"
tags:
  - go
  - golang
  - benchmarking
  - performance
  - testing
difficulty: "intermediate"
prerequisites: ["go/go-basics.md", "go/go-testing.md"]
updated: "2026-02-06"
---

# Go: бенчмаркинг

## Полезные ссылки

- [Go Benchmarking](https://go.dev/doc/effective_go#testing)
- [Go testing Package](https://pkg.go.dev/testing)

## Содержание

- [Go: бенчмаркинг](#go-бенчмаркинг)
- [Введение в бенчмаркинг](#введение-в-бенчмаркинг)
  - [Основные концепции](#основные-концепции)
- [Написание **benchmarks**](#написание-benchmarks)
  - [Базовый **benchmark**](#базовый-benchmark)
  - [**Benchmark** с подготовкой данных](#benchmark-с-подготовкой-данных)
  - [**Benchmark** с параметрами](#benchmark-с-параметрами)
  - [**Benchmark** с памятью](#benchmark-с-памятью)
- [Запуск **benchmarks**](#запуск-benchmarks)
  - [Базовый запуск](#базовый-запуск)
- [Запуск всех benchmarks](#запуск-всех-benchmarks)
- [Запуск конкретного benchmark](#запуск-конкретного-benchmark)
- [Запуск с подробным выводом](#запуск-с-подробным-выводом)
  - [Параметры запуска](#параметры-запуска)
- [Указание времени выполнения](#указание-времени-выполнения)
- [Указание количества итераций](#указание-количества-итераций)
- [Параллельное выполнение](#параллельное-выполнение)
- [Показ аллокаций памяти](#показ-аллокаций-памяти)
  - [Сравнение результатов](#сравнение-результатов)
- [Сохранение результатов](#сохранение-результатов)
- [После изменений](#после-изменений)
- [Сравнение](#сравнение)
- [Анализ результатов](#анализ-результатов)
  - [Понимание вывода](#понимание-вывода)
  - [Анализ производительности](#анализ-производительности)
- [Оптимизация **benchmarks**](#оптимизация-benchmarks)
  - [Избежание оптимизаций компилятора](#избежание-оптимизаций-компилятора)
  - [Использование **b.StopTimer** и **b.StartTimer**](#использование-bstoptimer-и-bstarttimer)
  - [Параллельные **benchmarks**](#параллельные-benchmarks)
  - [Детальные техники бенчмаркинга](#детальные-техники-бенчмаркинга)
  - [**Benchmark** с различными алгоритмами](#benchmark-с-различными-алгоритмами)
  - [**Benchmark** с подготовкой и очисткой](#benchmark-с-подготовкой-и-очисткой)
  - [**Benchmark** с параллельным выполнением](#benchmark-с-параллельным-выполнением)
  - [**Benchmark** с различными **CPU**](#benchmark-с-различными-cpu)
  - [Практические примеры: **Benchmark** для строковых операций](#практические-примеры-benchmark-для-строковых-операций)
  - [Практические примеры: **Benchmark** для **map** операций](#практические-примеры-benchmark-для-map-операций)
  - [Практические примеры: **Benchmark** для **slice** операций](#практические-примеры-benchmark-для-slice-операций)
  - [Практические примеры: **Benchmark** для **JSON** операций](#практические-примеры-benchmark-для-json-операций)
  - [Практические примеры: **Benchmark** для **HTTP** операций](#практические-примеры-benchmark-для-http-операций)
  - [Практические примеры: **Benchmark** для **database** операций](#практические-примеры-benchmark-для-database-операций)
  - [Практические примеры: Сравнение результатов](#практические-примеры-сравнение-результатов)
- [Скрипт для сравнения benchmarks](#скрипт-для-сравнения-benchmarks)
- [Ваши изменения здесь](#ваши-изменения-здесь)
  - [Практические примеры: **Benchmark** с профилированием](#практические-примеры-benchmark-с-профилированием)
  - [Практические примеры: **Benchmark** для конкурентных операций](#практические-примеры-benchmark-для-конкурентных-операций)
  - [Практические примеры: **Benchmark** с различными входными данными](#практические-примеры-benchmark-с-различными-входными-данными)
  - [Практические примеры: **Benchmark** для кэширования](#практические-примеры-benchmark-для-кэширования)
  - [Практические примеры: Сравнение алгоритмов](#практические-примеры-сравнение-алгоритмов)
  - [Практические примеры: Бенчмарки с различными размерами данных](#практические-примеры-бенчмарки-с-различными-размерами-данных)
  - [Практические примеры: Бенчмарки с **memory profiling**](#практические-примеры-бенчмарки-с-memory-profiling)
  - [Практические примеры: Сравнение результатов бенчмарков](#практические-примеры-сравнение-результатов-бенчмарков)
- [Запуск бенчмарков и сохранение результатов](#запуск-бенчмарков-и-сохранение-результатов)
- [Сравнение с benchstat](#сравнение-с-benchstat)
- [Сравнение с benchcmp](#сравнение-с-benchcmp)
- [Лучшие практики](#лучшие-практики)
- [Troubleshooting](#troubleshooting)
- [FAQ](#faq)
- [Заключение](#заключение)
- [Дополнительные ресурсы](#дополнительные-ресурсы)

## Введение в бенчмаркинг

Бенчмаркинг позволяет измерять производительность кода и выявлять узкие места. Go предоставляет встроенную поддержку бенчмарков через пакет `**testing**`.

### Основные концепции

1. **Benchmark функции** - функции с префиксом **Benchmark**
2. **b.N** - количество итераций, определяемое автоматически
3. **Результаты** - время выполнения и аллокации памяти
4. **Сравнение** - сравнение производительности различных реализаций

## Написание **benchmarks**

### Базовый **benchmark**

```go
import (
    "testing"
)

func BenchmarkAdd(b *testing.B) {
    for i := 0; i < b.N; i++ {
        Add(2, 3)
    }
}
```

### **Benchmark** с подготовкой данных

```go
func BenchmarkProcess(b *testing.B) {
    // Подготовка данных (не учитывается в времени)
    data := make([]int, 1000)
    for i := range data {
        data[i] = i
    }
    
    // Сброс таймера после подготовки
    b.ResetTimer()
    
    for i := 0; i < b.N; i++ {
        Process(data)
    }
}
```

### **Benchmark** с параметрами

```go
func BenchmarkProcess(b *testing.B) {
    sizes := []int{10, 100, 1000}
    
    for _, size := range sizes {
        b.Run(fmt.Sprintf("size-%d", size), func(b *testing.B) {
            data := make([]int, size)
            for i := range data {
                data[i] = i
            }
            
            b.ResetTimer()
            for i := 0; i < b.N; i++ {
                Process(data)
            }
        })
    }
}
```

### **Benchmark** с памятью

```go
func BenchmarkAllocate(b *testing.B) {
    b.ReportAllocs()
    
    for i := 0; i < b.N; i++ {
        data := make([]int, 1000)
        _ = data
    }
}
```

## Запуск **benchmarks**

### Базовый запуск

```bash
# Запуск всех benchmarks
go test -bench=.

# Запуск конкретного benchmark
go test -bench=BenchmarkAdd

# Запуск с подробным выводом
go test -bench=. -v
```

### Параметры запуска

```bash
# Указание времени выполнения
go test -bench=. -benchtime=10s

# Указание количества итераций
go test -bench=. -benchtime=1000x

# Параллельное выполнение
go test -bench=. -cpu=1,2,4,8

# Показ аллокаций памяти
go test -bench=. -benchmem
```

### Сравнение результатов

```bash
# Сохранение результатов
go test -bench=. -benchmem > old.txt

# После изменений
go test -bench=. -benchmem > new.txt

# Сравнение
benchcmp old.txt new.txt
```

## Анализ результатов

### Понимание вывода

```
BenchmarkAdd-8         1000000000    0.234 ns/op    0 B/op    0 allocs/op
```

- `**BenchmarkAdd-8**` - имя **benchmark** и количество **CPU**
- `1000000000` - количество итераций
- **ns/op** — наносекунды на операцию
- **B/op** — байты на операцию
- **allocs/op** — аллокации на операцию

### Анализ производительности

```go
func BenchmarkCompare(b *testing.B) {
    b.Run("method1", func(b *testing.B) {
        for i := 0; i < b.N; i++ {
            Method1()
        }
    })
    
    b.Run("method2", func(b *testing.B) {
        for i := 0; i < b.N; i++ {
            Method2()
        }
    })
}
```

## Оптимизация **benchmarks**

### Избежание оптимизаций компилятора

```go
var result int

func BenchmarkAdd(b *testing.B) {
    var r int
    for i := 0; i < b.N; i++ {
        r = Add(2, 3)
    }
    result = r  // Предотвращает оптимизацию
}
```

### Использование **b.StopTimer** и **b.StartTimer**

```go
func BenchmarkWithSetup(b *testing.B) {
    for i := 0; i < b.N; i++ {
        b.StopTimer()
        data := prepareData()
        b.StartTimer()
        
        Process(data)
    }
}
```

### Параллельные **benchmarks**

```go
func BenchmarkParallel(b *testing.B) {
    b.RunParallel(func(pb *testing.PB) {
        for pb.Next() {
            Process()
        }
    })
}
```

### Детальные техники бенчмаркинга

```go
// Benchmark с различными входными данными
func BenchmarkProcessDifferentSizes(b *testing.B) {
    sizes := []int{10, 100, 1000, 10000}
    
    for _, size := range sizes {
        b.Run(fmt.Sprintf("size-%d", size), func(b *testing.B) {
            data := make([]int, size)
            for i := range data {
                data[i] = i
            }
            
            b.ResetTimer()
            b.ReportAllocs()
            
            for i := 0; i < b.N; i++ {
                Process(data)
            }
        })
    }
}
```

### **Benchmark** с различными алгоритмами

```go
func BenchmarkAlgorithms(b *testing.B) {
    data := make([]int, 1000)
    for i := range data {
        data[i] = rand.Intn(1000)
    }
    
    algorithms := []struct {
        name string
        fn   func([]int) []int
    }{
        {"bubbleSort", bubbleSort},
        {"quickSort", quickSort},
        {"mergeSort", mergeSort},
    }
    
    for _, alg := range algorithms {
        b.Run(alg.name, func(b *testing.B) {
            b.ResetTimer()
            for i := 0; i < b.N; i++ {
                testData := make([]int, len(data))
                copy(testData, data)
                alg.fn(testData)
            }
        })
    }
}
```

### **Benchmark** с подготовкой и очисткой

```go
func BenchmarkWithSetupTeardown(b *testing.B) {
    // Подготовка один раз для всех итераций
    setup()
    defer teardown()
    
    b.ResetTimer()
    
    for i := 0; i < b.N; i++ {
        // Подготовка для каждой итерации
        b.StopTimer()
        data := prepareData()
        b.StartTimer()
        
        Process(data)
    }
}
```

### **Benchmark** с параллельным выполнением

```go
func BenchmarkParallel(b *testing.B) {
    b.RunParallel(func(pb *testing.PB) {
        for pb.Next() {
            Process()
        }
    })
}

func BenchmarkParallelWithData(b *testing.B) {
    data := make([]int, 1000)
    for i := range data {
        data[i] = i
    }
    
    b.ResetTimer()
    b.RunParallel(func(pb *testing.PB) {
        for pb.Next() {
            Process(data)
        }
    })
}
```

### **Benchmark** с различными **CPU**

```go
func BenchmarkWithCPUs(b *testing.B) {
    cpus := []int{1, 2, 4, 8}
    
    for _, cpu := range cpus {
        b.Run(fmt.Sprintf("cpu-%d", cpu), func(b *testing.B) {
            runtime.GOMAXPROCS(cpu)
            b.ResetTimer()
            
            for i := 0; i < b.N; i++ {
                Process()
            }
        })
    }
}
```

### Практические примеры: **Benchmark** для строковых операций

```go
func BenchmarkStringConcatenation(b *testing.B) {
    strings := []string{"hello", "world", "test", "benchmark"}
    
    b.Run("plus", func(b *testing.B) {
        for i := 0; i < b.N; i++ {
            result := ""
            for _, s := range strings {
                result += s
            }
            _ = result
        }
    })
    
    b.Run("strings.Builder", func(b *testing.B) {
        for i := 0; i < b.N; i++ {
            var builder strings.Builder
            for _, s := range strings {
                builder.WriteString(s)
            }
            _ = builder.String()
        }
    })
    
    b.Run("bytes.Buffer", func(b *testing.B) {
        for i := 0; i < b.N; i++ {
            var buf bytes.Buffer
            for _, s := range strings {
                buf.WriteString(s)
            }
            _ = buf.String()
        }
    })
}
```

### Практические примеры: **Benchmark** для **map** операций

```go
func BenchmarkMapOperations(b *testing.B) {
    sizes := []int{10, 100, 1000, 10000}
    
    for _, size := range sizes {
        b.Run(fmt.Sprintf("size-%d", size), func(b *testing.B) {
            m := make(map[int]int, size)
            for i := 0; i < size; i++ {
                m[i] = i
            }
            
            b.ResetTimer()
            b.Run("read", func(b *testing.B) {
                for i := 0; i < b.N; i++ {
                    _ = m[i%size]
                }
            })
            
            b.Run("write", func(b *testing.B) {
                for i := 0; i < b.N; i++ {
                    m[i%size] = i
                }
            })
            
            b.Run("delete", func(b *testing.B) {
                for i := 0; i < b.N; i++ {
                    delete(m, i%size)
                    m[i%size] = i // Восстановление для следующей итерации
                }
            })
        })
    }
}
```

### Практические примеры: **Benchmark** для **slice** операций

```go
func BenchmarkSliceOperations(b *testing.B) {
    sizes := []int{10, 100, 1000, 10000}
    
    for _, size := range sizes {
        b.Run(fmt.Sprintf("size-%d", size), func(b *testing.B) {
            b.Run("append", func(b *testing.B) {
                for i := 0; i < b.N; i++ {
                    slice := make([]int, 0)
                    for j := 0; j < size; j++ {
                        slice = append(slice, j)
                    }
                }
            })
            
            b.Run("preallocated", func(b *testing.B) {
                for i := 0; i < b.N; i++ {
                    slice := make([]int, 0, size)
                    for j := 0; j < size; j++ {
                        slice = append(slice, j)
                    }
                }
            })
            
            b.Run("fixed", func(b *testing.B) {
                for i := 0; i < b.N; i++ {
                    slice := make([]int, size)
                    for j := 0; j < size; j++ {
                        slice[j] = j
                    }
                }
            })
        })
    }
}
```

### Практические примеры: **Benchmark** для **JSON** операций

```go
type User struct {
    ID    int    `json:"id"`
    Name  string `json:"name"`
    Email string `json:"email"`
}

func BenchmarkJSONOperations(b *testing.B) {
    user := User{ID: 1, Name: "Alice", Email: "alice@example.com"}
    
    b.Run("marshal", func(b *testing.B) {
        for i := 0; i < b.N; i++ {
            _, err := json.Marshal(user)
            if err != nil {
                b.Fatal(err)
            }
        }
    })
    
    jsonData, _ := json.Marshal(user)
    
    b.Run("unmarshal", func(b *testing.B) {
        for i := 0; i < b.N; i++ {
            var u User
            err := json.Unmarshal(jsonData, &u)
            if err != nil {
                b.Fatal(err)
            }
        }
    })
}
```

### Практические примеры: **Benchmark** для **HTTP** операций

```go
func BenchmarkHTTPOperations(b *testing.B) {
    handler := http.HandlerFunc(func(w http.ResponseWriter, r *http.Request) {
        w.WriteHeader(http.StatusOK)
        w.Write([]byte("OK"))
    })
    
    server := httptest.NewServer(handler)
    defer server.Close()
    
    client := &http.Client{
        Timeout: 5 * time.Second,
    }
    
    b.Run("get", func(b *testing.B) {
        for i := 0; i < b.N; i++ {
            resp, err := client.Get(server.URL)
            if err != nil {
                b.Fatal(err)
            }
            resp.Body.Close()
        }
    })
}
```

### Практические примеры: **Benchmark** для **database** операций

```go
func BenchmarkDatabaseOperations(b *testing.B) {
    db := setupTestDB()
    defer db.Close()
    
    b.Run("insert", func(b *testing.B) {
        for i := 0; i < b.N; i++ {
            _, err := db.Exec("INSERT INTO users (name, email) VALUES ($1, $2)", 
                fmt.Sprintf("user%d", i), fmt.Sprintf("user%d@example.com", i))
            if err != nil {
                b.Fatal(err)
            }
        }
    })
    
    b.Run("select", func(b *testing.B) {
        for i := 0; i < b.N; i++ {
            var name string
            err := db.QueryRow("SELECT name FROM users WHERE id = $1", i%100+1).Scan(&name)
            if err != nil && err != sql.ErrNoRows {
                b.Fatal(err)
            }
        }
    })
}
```

### Практические примеры: Сравнение результатов

```bash
#!/bin/bash
# Скрипт для сравнения benchmarks

echo "Running benchmarks..."
go test -bench=. -benchmem -count=5 > old.txt

echo "Making changes..."
# Ваши изменения здесь

echo "Running benchmarks again..."
go test -bench=. -benchmem -count=5 > new.txt

echo "Comparing results..."
benchstat old.txt new.txt
```

### Практические примеры: **Benchmark** с профилированием

```go
func BenchmarkWithProfiling(b *testing.B) {
    // CPU профилирование
    cpuFile, _ := os.Create("cpu.prof")
    defer cpuFile.Close()
    pprof.StartCPUProfile(cpuFile)
    defer pprof.StopCPUProfile()
    
    // Memory профилирование
    memFile, _ := os.Create("mem.prof")
    defer memFile.Close()
    
    b.ResetTimer()
    for i := 0; i < b.N; i++ {
        Process()
    }
    
    pprof.WriteHeapProfile(memFile)
}
```

### Практические примеры: **Benchmark** для конкурентных операций

```go
func BenchmarkConcurrentOperations(b *testing.B) {
    b.Run("sequential", func(b *testing.B) {
        for i := 0; i < b.N; i++ {
            Process()
        }
    })
    
    b.Run("concurrent-2", func(b *testing.B) {
        b.SetParallelism(2)
        b.RunParallel(func(pb *testing.PB) {
            for pb.Next() {
                Process()
            }
        })
    })
    
    b.Run("concurrent-4", func(b *testing.B) {
        b.SetParallelism(4)
        b.RunParallel(func(pb *testing.PB) {
            for pb.Next() {
                Process()
            }
        })
    })
}
```

### Практические примеры: **Benchmark** с различными входными данными

```go
func BenchmarkWithDifferentInputs(b *testing.B) {
    inputs := []struct {
        name string
        data []int
    }{
        {"sorted", []int{1, 2, 3, 4, 5}},
        {"reversed", []int{5, 4, 3, 2, 1}},
        {"random", []int{3, 1, 4, 2, 5}},
        {"duplicates", []int{1, 1, 2, 2, 3}},
    }
    
    for _, input := range inputs {
        b.Run(input.name, func(b *testing.B) {
            for i := 0; i < b.N; i++ {
                testData := make([]int, len(input.data))
                copy(testData, input.data)
                Sort(testData)
            }
        })
    }
}
```

### Практические примеры: **Benchmark** для кэширования

```go
func BenchmarkCacheOperations(b *testing.B) {
    cache := NewCache(1000)
    
    // Заполнение кэша
    for i := 0; i < 1000; i++ {
        cache.Set(fmt.Sprintf("key%d", i), fmt.Sprintf("value%d", i))
    }
    
    b.Run("hit", func(b *testing.B) {
        for i := 0; i < b.N; i++ {
            cache.Get(fmt.Sprintf("key%d", i%1000))
        }
    })
    
    b.Run("miss", func(b *testing.B) {
        for i := 0; i < b.N; i++ {
            cache.Get(fmt.Sprintf("key%d", i+1000))
        }
    })
}
```

### Практические примеры: Сравнение алгоритмов

```go
func BenchmarkSorting(b *testing.B) {
    data := generateRandomData(1000)
    
    b.Run("quickSort", func(b *testing.B) {
        for i := 0; i < b.N; i++ {
            arr := make([]int, len(data))
            copy(arr, data)
            quickSort(arr)
        }
    })
    
    b.Run("mergeSort", func(b *testing.B) {
        for i := 0; i < b.N; i++ {
            arr := make([]int, len(data))
            copy(arr, data)
            mergeSort(arr)
        }
    })
    
    b.Run("builtin", func(b *testing.B) {
        for i := 0; i < b.N; i++ {
            arr := make([]int, len(data))
            copy(arr, data)
            sort.Ints(arr)
        }
    })
}
```

### Практические примеры: Бенчмарки с различными размерами данных

```go
func BenchmarkWithSizes(b *testing.B) {
    sizes := []int{10, 100, 1000, 10000}
    
    for _, size := range sizes {
        b.Run(fmt.Sprintf("size-%d", size), func(b *testing.B) {
            data := generateData(size)
            b.ResetTimer()
            
            for i := 0; i < b.N; i++ {
                processData(data)
            }
        })
    }
}
```

### Практические примеры: Бенчмарки с **memory profiling**

```go
func BenchmarkWithMemory(b *testing.B) {
    b.ReportAllocs()
    b.ReportMetric(float64(runtime.NumGoroutine()), "goroutines")
    
    b.Run("withAllocations", func(b *testing.B) {
        for i := 0; i < b.N; i++ {
            result := processWithAllocations()
            _ = result
        }
    })
    
    b.Run("withoutAllocations", func(b *testing.B) {
        result := make([]byte, 1000)
        b.ResetTimer()
        for i := 0; i < b.N; i++ {
            processWithoutAllocations(result)
        }
    })
}
```

### Практические примеры: Сравнение результатов бенчмарков

```bash
# Запуск бенчмарков и сохранение результатов
go test -bench=. -benchmem > old.txt

# После изменений
go test -bench=. -benchmem > new.txt

# Сравнение с benchstat
benchstat old.txt new.txt

# Сравнение с benchcmp
benchcmp old.txt new.txt
```

## Лучшие практики

1. **Используйте b.`ResetTimer()`** - для исключения подготовки из результатов
2. **Используйте b.`ReportAllocs()`** - для отслеживания аллокаций
3. **Запускайте несколько раз** - для получения стабильных результатов
4. **Используйте benchstat** - для сравнения результатов
5. **Тестируйте различные размеры** - для понимания масштабируемости
6. **Избегайте оптимизаций компилятора** - используйте результаты вычислений
7. **Используйте b.`StopTimer()` и b.`StartTimer()`** - для исключения **setup**/**teardown**
8. **Тестируйте параллельные операции** - используйте **b.RunParallel**()
9. **Используйте профилирование** - для понимания узких мест
10. **Документируйте результаты** - сохраняйте результаты для сравнения
11. **Сравнивайте алгоритмы** - используйте бенчмарки для выбора алгоритма
12. **Тестируйте с разными размерами** - проверяйте масштабируемость
13. **Мониторьте память** - отслеживайте аллокации в бенчмарках
14. **Используйте инструменты сравнения** - **benchstat** и **benchcmp** для анализа
15. **Автоматизируйте бенчмарки** - включайте в `CI/CD` **pipeline**


## Решение проблем

Типичные проблемы и решения см. в официальной документации (блок «Полезные ссылки» в начале документа).

## Частые вопросы

Ответы на частые вопросы по теме см. в разделах «Введение» и «Лучшие практики» в документе.

## Заключение

Бенчмаркинг в Go предоставляет мощные инструменты для измерения и оптимизации производительности. Понимание написания **benchmarks**, запуска, анализа результатов, сравнения алгоритмов, тестирования различных размеров, мониторинга памяти, использования инструментов сравнения и оптимизации критично для эффективного бенчмаркинга в Go. Правильное использование бенчмарков позволяет выявлять узкие места, сравнивать решения, оптимизировать производительность и обеспечивать стабильность производительности приложений.

## Дополнительные ресурсы

- [Go Benchmarking](https://go.dev/doc/effective_go#testing)
- [Go testing Package](https://pkg.go.dev/testing)
