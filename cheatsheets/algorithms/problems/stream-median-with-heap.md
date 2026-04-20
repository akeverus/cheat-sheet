---
title: "Медиана потока на кучах (Stream Median with Heap)"
description: "Кратко: как поддерживать медиану входящего потока чисел в O(log n) на вставку и O(1) на запрос, используя две кучи (max-heap для левой части и min-heap для правой). Разбираем инварианты балансировки, Java/Kotlin реализации с комментариями, тесты, тройблшутинг и чек-лист для прода"
tags:
  - algorithms
  - problems
  - stream-median-with-heap
difficulty: "intermediate"
prerequisites: []
next: []
updated: "2026-02-11"
---
# Медиана потока на кучах (Stream Median with Heap)

Кратко: как поддерживать медиану входящего потока чисел в `O(log n)` на вставку и `O(1)` на запрос, используя две кучи (`max-heap` для левой части и `min-heap` для правой). Разбираем инварианты балансировки, Java/Kotlin реализации с комментариями, тесты, тройблшутинг и чек-лист для продакшена.

## Полезные ссылки

### Официальная документация
- [PriorityQueue (Java SE 8)](https://docs.oracle.com/javase/8/docs/api/java/util/PriorityQueue.html)
- [Baeldung: Stream Median](https://www.baeldung.com/)

### См. также
- [Бинарное дерево](../trees/) — бинарное дерево
- [[standard-deviation|Стандартное отклонение]] — стандартное отклонение

## Содержание

- [Описание алгоритма](#описание-алгоритма)
- [Определение медианы](#определение-медианы)
  - [Формулировка задачи](#формулировка-задачи)
  - [Примеры](#примеры)
- [Реализация на Java](#реализация-на-java)
  - [Подход 1: Отсортированный список](#подход-1-отсортированный-список)
- [Подход 2: Две кучи](#подход-2-две-кучи)
  - [Реализация](#реализация)
- [Подход 3: Балансируемые кучи](#подход-3-балансируемые-кучи)
- [Сравнение подходов](#сравнение-подходов)
  - [Временная сложность операций с кучей](#временная-сложность-операций-с-кучей)
- [Реализация на Kotlin](#реализация-на-kotlin)
- [Сложность](#сложность)
  - [Временная сложность](#временная-сложность)
  - [Пространственная сложность](#пространственная-сложность)
- [Особенности](#особенности)
- [Применение](#применение)
- [Варианты задачи](#варианты-задачи)
  - [Вариант 1: Медиана с удалением](#вариант-1-медиана-с-удалением)
  - [Вариант 2: Медиана с весами](#вариант-2-медиана-с-весами)
- [Когда использовать](#когда-использовать)
- [Лучшие практики](#лучшие-практики)
- [Тестирование](#тестирование)
- [Решение проблем](#решение-проблем)
- [Частые вопросы](#частые-вопросы)
- [Глоссарий](#глоссарий)
- [Дополнительные примеры использования](#дополнительные-примеры-использования)
  - [Пример 1: Ленивая очистка при удалении](#пример-1-ленивая-очистка-при-удалении)
  - [Пример 2: Потокобезопасный вариант](#пример-2-потокобезопасный-вариант)
  - [Пример 3: Взвешенная медиана](#пример-3-взвешенная-медиана)
  - [Пример 4: Быстрый JMH-бенчмарк](#пример-4-быстрый-jmh-бенчмарк)
- [Проверка входа и контракты](#проверка-входа-и-контракты)
- [Наблюдаемость и профилирование](#наблюдаемость-и-профилирование)
- [Edge cases и ограничения](#edge-cases-и-ограничения)
- [Чек-лист перед продакшеном](#чек-лист-перед-продакшеном)
- [Заключение](#заключение)


## Описание алгоритма

В этом материале разбираем, как поддерживать медиану потока чисел так, чтобы вставка стоила `O(log n)`, а запрос — `O(1)`. Сначала формализуем задачу и разберём примеры, затем посмотрим на три подхода (отсортированный список, две кучи, балансируемые кучи), дадим Java/Kotlin код, тесты и рекомендации для эксплуатации.

## Определение медианы

Медиана — значение, которое делит отсортированный набор пополам: столько же элементов меньше, сколько больше. При нечётном размере это центральный элемент, при чётном — среднее двух середины (например, `{5,7,8,10}` `(7+8)/2 = 7.5`). В потоковом варианте медиана считается для всех чисел, прочитанных к текущему моменту, и должна обновляться онлайн.

### Формулировка задачи

Требуется класс, который умеет добавлять новое число и мгновенно возвращать текущую медиану набора, накопленного из потока.

### Примеры

```text
# Пример последовательных добавлений и изменения медианы
add 5   -> медиана 5
add 7   -> медиана (5+7)/2 = 6
add 10  -> медиана 7
add 8   -> медиана (7+8)/2 = 7.5
```

Интерфейс минимален: `add(int num)` и `getMedian()`. Даже если поток потенциально бесконечен, мы можем хранить все элементы либо в двух кучах, либо в отсортированной структуре, выбирая алгоритм по требованиям к сложности.

## Реализация на Java

### Подход 1: Отсортированный список

Простой вариант — держать отсортированный список и брать середину за `O(1)`, но вставка требует `O(n)` из-за поиска позиции и сдвига хвоста. Годится только для коротких потоков.

## Подход 2: Две кучи

Делим поток на левую и правую половины: левая часть в `max-heap`, правая — в `min-heap`. Инвариант — разница размеров не больше 1, медиана читается из корней. Вставка стоит `O(log n)` из-за операций кучи.

### Пошаговый разбор

Возьмём поток `5, 7, 10, 8`. После `5` левая куча `[5]`, правая `[]`, медиана `5`. Добавляем `7`: левая `[5]`, правая `[7]`, медиана `(5+7)/2 = 6`. Добавляем `10`: правая получает `10`, затем переносит минимум, получаем левая `[7,5]`, правая `[10]`, медиана `7`. Добавляем `8`: число идёт вправо, баланс остаётся, медиана `(7+8)/2 = 7.5`. Инвариант «разница размеров ≤ 1» соблюдается на каждом шаге.

### Реализация

```java
// Медиана потока: баланс двух куч, медиана берётся из корней
import java.util.Comparator;
import java.util.PriorityQueue;
import java.util.Queue;

class MedianOfIntegerStream {
    private final Queue<Integer> minHeap; // правая половина (минимум сверху)
    private final Queue<Integer> maxHeap; // левая половина (максимум сверху)

    MedianOfIntegerStream() {
        minHeap = new PriorityQueue<>();
        maxHeap = new PriorityQueue<>(Comparator.reverseOrder());
    }

    // Добавляем число, выбирая кучу и поддерживая баланс
    void add(int num) {
        if (!minHeap.isEmpty() && num > minHeap.peek()) {
            minHeap.offer(num);                    // больше минимума правой — кладём вправо
        } else {
            maxHeap.offer(num);                    // иначе — в левую кучу
        }
        rebalance();                               // sizes differ by at most 1
    }

    // Медиана: корень большей кучи или среднее корней
    double getMedian() {
        if (minHeap.size() > maxHeap.size()) {
            return minHeap.peek();
        }
        if (maxHeap.size() > minHeap.size()) {
            return maxHeap.peek();
        }
        return (minHeap.peek() + maxHeap.peek()) / 2.0;
    }

    private void rebalance() {
        if (minHeap.size() > maxHeap.size() + 1) {
            maxHeap.offer(minHeap.poll());         // переносим минимум справа
        } else if (maxHeap.size() > minHeap.size() + 1) {
            minHeap.offer(maxHeap.poll());         // переносим максимум слева
        }
    }
}
```

## Подход 3: Балансируемые кучи

Чистый вариант: если размеры равны, кладём в `max-heap` и переносим максимум вправо; если нет — кладём в `min-heap` и переносим минимум влево. Инвариант разницы размеров ≤ 1 выдерживается без дополнительных сравнений с корнями.

```java
// Альтернативная балансировка: вставка + перекладка сразу поддерживают инвариант
import java.util.Comparator;
import java.util.PriorityQueue;
import java.util.Queue;

class MedianOfIntegerStreamBalanced {
    private final Queue<Integer> minHeap = new PriorityQueue<>();
    private final Queue<Integer> maxHeap = new PriorityQueue<>(Comparator.reverseOrder());

    void add(int num) {
        if (minHeap.size() == maxHeap.size()) {
            maxHeap.offer(num);            // кладём в левую кучу
            minHeap.offer(maxHeap.poll()); // переносим максимум слева вправо
        } else {
            minHeap.offer(num);            // кладём вправо
            maxHeap.offer(minHeap.poll()); // переносим минимум справа влево
        }
    }

    double getMedian() {
        if (minHeap.size() > maxHeap.size()) {
            return minHeap.peek();
        }
        return (minHeap.peek() + maxHeap.peek()) / 2.0;
    }
}
```

В обоих подходах `add` выполняется за `O(log n)`, `getMedian` — за `O(1)`.

## Сравнение подходов

| Подход | **add**() | **getMedian**() | Пространство |
|--------|-------|-------------|--------------|
| Отсортированный список | `O(n)` | `O(1)` | `O(n)` |
| Две кучи (подход 1) | `O(log n)` | `O(1)` | `O(n)` |
| Две кучи (подход 2) | `O(log n)` | `O(1)` | `O(n)` |

### Временная сложность операций с кучей

- **find-min/`find-max`:** `O(1)`
- **delete-min/`delete-max`:** `O(log(n)`)
- **insert:** `O(log(n)`)

Таким образом, операция **getMedian** может быть выполнена за время `O(1)`, поскольку для нее требуются только функции **find-min** и **find-max**. Временная сложность операции добавления составляет `O(log n)`.

## Реализация на Kotlin

```kotlin
import java.util.PriorityQueue

// Две кучи: правая min-heap, левая max-heap
class MedianOfIntegerStreamK {
    private val minHeap = PriorityQueue<Int>()                         // правая половина
    private val maxHeap = PriorityQueue<Int>(compareByDescending { it }) // левая половина

    // Балансируем после каждой вставки
    fun add(num: Int) {
        if (minHeap.size == maxHeap.size) {
            maxHeap.offer(num)             // кладём в левую
            minHeap.offer(maxHeap.poll())  // переносим максимум слева вправо
        } else {
            minHeap.offer(num)             // кладём вправо
            maxHeap.offer(minHeap.poll())  // переносим минимум справа влево
        }
    }

    // Медиана за O(1)
    fun getMedian(): Double {
        return if (minHeap.size > maxHeap.size) {
            minHeap.peek().toDouble()
        } else {
            (minHeap.peek() + maxHeap.peek()) / 2.0
        }
    }
}

// Вариант с явной проверкой корня правой кучи
class MedianOfIntegerStreamImprovedK {
    private val minHeap = PriorityQueue<Int>()
    private val maxHeap = PriorityQueue<Int>(compareByDescending { it })

    fun add(num: Int) {
        if (minHeap.isNotEmpty() && num > minHeap.peek()) {
            minHeap.offer(num)
        } else {
            maxHeap.offer(num)
        }
        rebalance()
    }

    fun getMedian(): Double = when {
        minHeap.size > maxHeap.size -> minHeap.peek().toDouble()
        maxHeap.size > minHeap.size -> maxHeap.peek().toDouble()
        else -> (minHeap.peek() + maxHeap.peek()) / 2.0
    }

    private fun rebalance() {
        if (minHeap.size > maxHeap.size + 1) {
            maxHeap.offer(minHeap.poll())
        } else if (maxHeap.size > minHeap.size + 1) {
            minHeap.offer(maxHeap.poll())
        }
    }
}

fun main() {
    val medianStream = MedianOfIntegerStreamK()
    medianStream.add(5)
    println("Median: ${medianStream.getMedian()}") // 5.0
    medianStream.add(7)
    println("Median: ${medianStream.getMedian()}") // 6.0
    medianStream.add(10)
    println("Median: ${medianStream.getMedian()}") // 7.0
    medianStream.add(8)
    println("Median: ${medianStream.getMedian()}") // 7.5
}
```

## Сложность

`add` в обоих вариантах требует `O(log n)` из‑за операций кучи, `getMedian` — `O(1)`, так как читает только корни. Память линейная `O(n)`, потому что все элементы хранятся в двух кучах; дополнительные структуры не нужны.

## Особенности

Две кучи дают сбалансированный компромисс между скоростью и простотой: код короче, чем дерево поиска, и быстрее, чем отсортированный список. Подход прозрачен: инвариант «разница размеров ≤ 1» легко проверять и логировать.

## Применение

Онлайн медиана полезна в потоковой аналитике (латентности сервисов, метрики мониторинга), в финтехе для оценки распределений цен/сделок, в обработке телеметрии IoT и медицинских показателей, а также в системах алертинга, где порог зависит от медианных значений.

## Варианты задачи

### Вариант 1: Медиана с удалением

```java
import java.util.HashMap;
import java.util.Map;

// Ленивое удаление: помечаем число, удаляем при извлечении корня
class MedianStreamWithRemoval extends MedianOfIntegerStream {
    private final Map<Integer, Integer> toDelete = new HashMap<>();

    void remove(int num) {
        toDelete.put(num, toDelete.getOrDefault(num, 0) + 1);
    }

    @Override
    double getMedian() {
        cleanup(); // удаляем помеченные элементы перед вычислением
        return super.getMedian();
    }

    private void cleanup() {
        prune(minHeap);
        prune(maxHeap);
    }

    private void prune(Queue<Integer> heap) {
        while (!heap.isEmpty()) {
            int v = heap.peek();
            int mark = toDelete.getOrDefault(v, 0);
            if (mark == 0) break;      // вершина не помечена — выходим
            if (mark == 1) {
                toDelete.remove(v);    // снимаем пометку
            } else {
                toDelete.put(v, mark - 1);
            }
            heap.poll();               // удаляем устаревший элемент
        }
    }
}
```

### Вариант 2: Медиана с весами

```java
// Заготовка для взвешенной медианы: идея та же, но учёт весов при балансировке
class WeightedMedianStream {
    // реализация опущена; при балансировке учитывайте суммарный вес, а не размер куч
}
```

## Когда использовать

Две кучи выбирают, когда поток длинный и нужны `O(log n)` на вставку и `O(1)` на медиану. Отсортированный список пригодится только для коротких входов или когда вставок мало, а чтений очень много, и простота важнее производительности.

## Лучшие практики

Держите инвариант «разница размеров ≤ 1» и проверяйте его в тестах. При чётном количестве элементов возвращайте среднее двух корней, не теряя точности (используйте `double` или `BigDecimal` для финансов). Логируйте шаги балансировки при отладке, а в многопоточном сценарии синхронизируйте доступ или оборачивайте операции в один лок. Поддерживайте проверку входа (нет `null`, разумные диапазоны значений) и покрывайте тестами пустой поток, один элемент, монотонные и случайные последовательности.

## Тестирование

### JUnit

```java
@Test
void median_singleElement() {
    MedianOfIntegerStream s = new MedianOfIntegerStream();
    s.add(5);
    assertEquals(5.0, s.getMedian());
}

@Test
void median_balancesHeaps() {
    MedianOfIntegerStream s = new MedianOfIntegerStream();
    s.add(5); s.add(7); s.add(10); s.add(8);
    assertEquals(7.5, s.getMedian(), 1e-9);
}
```

### Kotlin

```kotlin
@Test
fun `median updates after inserts`() {
    val s = MedianOfIntegerStreamK()
    s.add(5); s.add(7); s.add(10); s.add(8)
    assertEquals(7.5, s.getMedian())
}
```

### Property-based (kotest)

```kotlin
@Property
fun `median within min..max`(@ForAll data: List<Int>) {
    val s = MedianOfIntegerStreamK()
    data.forEach { s.add(it) }
    if (data.isNotEmpty()) {
        val m = s.getMedian()
        assertTrue(m >= data.min()!! && m <= data.max()!!)
    }
}
```

## Решение проблем

Если медиана скачет: проверьте, что переносите элементы между кучами при каждом добавлении и что разница размеров не превышает 1. Если точность теряется для больших значений — используйте `double` или `BigDecimal` при делении средней пары. При подозрении на утечку памяти убедитесь, что не храните лишние копии данных и не оставляете помеченные к удалению элементы без очистки.

## Частые вопросы

**Почему две кучи, а не дерево поиска?** Кучи проще и дают `O(log n)` на вставку и `O(1)` на медиану без сложного API.
**Можно ли поддерживать удаление?** Да, через ленивое удаление и периодическую очистку корней.
**Как быть с дубликатами?** Кучи поддерживают дубликаты, инвариант разницы размеров сохраняется.
**Поддерживает ли код потоки с миллионами элементов?** Да, но учитывайте память `O(n)`; для очень длинных потоков нужна агрегация или выборочное хранение.

## Глоссарий

`min-heap` — куча, где минимум на вершине.
`max-heap` — куча, где максимум на вершине.
`rebalance` — операция выравнивания размеров куч.
`median` — значение, делящее отсортированный набор пополам.
`lazy deletion` — пометка на удаление с фактическим удалением при обращении к элементу.

## Альтернативы и оптимизации

Для скользящего окна (`window median`) кучи дополняют ленивым удалением и счётчиком «протухших» элементов. Если нужны порядковые статистики разных рангов, пригодится дерево с порядковыми статистиками или `Order Statistic Tree` — дороже в коде, но универсально. Для очень длинных потоков, где допустима аппроксимация, используйте `Quantile Digest` или `t-digest`: они работают в подлинейной памяти и дают близкую к медиане оценку.

### Пример: скользящая медиана

```java
// Идея: две кучи + map с отложенными удалениями; при выходе окна помечаем элемент и чистим корни
void removeExpired(int value) {
    toDelete.put(value, toDelete.getOrDefault(value, 0) + 1);
    cleanupRoots(); // см. пример ленивой очистки
}
```

## Дополнительные примеры использования

### Пример 1: Ленивая очистка при удалении

```java
private void cleanupRoots() {
    // Пока вершина помечена, удаляем её из кучи
    while (!minHeap.isEmpty() && toDelete.getOrDefault(minHeap.peek(), 0) > 0) {
        toDelete.put(minHeap.peek(), toDelete.get(minHeap.peek()) - 1);
        minHeap.poll();
    }
    while (!maxHeap.isEmpty() && toDelete.getOrDefault(maxHeap.peek(), 0) > 0) {
        toDelete.put(maxHeap.peek(), toDelete.get(maxHeap.peek()) - 1);
        maxHeap.poll();
    }
}
```

### Пример 2: Потокобезопасный вариант

```java
class SynchronizedMedianStream {
    private final MedianOfIntegerStream delegate = new MedianOfIntegerStream();
    synchronized void add(int num) { delegate.add(num); }
    synchronized double getMedian() { return delegate.getMedian(); }
}
```

### Пример 3: Взвешенная медиана

```java
record WeightedValue(int value, double weight) {}
// При балансировке учитывайте суммарный вес левой и правой половин, не только размер
```

### Пример 4: Быстрый JMH-бенчмарк

```java
@State(Scope.Benchmark)
public class MedianBench {
    private MedianOfIntegerStream stream;
    @Setup
    public void setup() {
        stream = new MedianOfIntegerStream();
        for (int i = 0; i < 10_000; i++) stream.add(i);
    }
    @Benchmark
    public double addAndMedian() {
        stream.add(42);
        return stream.getMedian();
    }
}
```

## Проверка входа и контракты

Всегда фиксируйте предположения: числа умещаются в `int`, поток не содержит `null`, операции `add` и `getMedian` вызываются из одного потока (если нет — синхронизируйте). При чётном количестве элементов результат храните в `double`, чтобы не терять дробную часть. При необходимости ограничить память вводите политику «скользящего окна» и чётко документируйте её.

### Пример валидации

```java
private void validate(int num) {
    // Для финансов можно проверять диапазон
    if (num < -1_000_000_000 || num > 1_000_000_000) {
        throw new IllegalArgumentException("value out of supported range");
    }
}
```

## Наблюдаемость и профилирование

Полезно логировать размер куч и медиану в debug-режиме, чтобы ловить нарушения инварианта. Для производительности — JMH с разными сценариями: монотонная вставка, случайные данные, батчи с удалением. Метрические счётчики (`add` latency, частота rebalance, размер куч) помогают выявлять регрессии после изменений.

### Пример метрики в pseudo-code

```java
long start = System.nanoTime();
stream.add(value);
metrics.timer("median.add").record(System.nanoTime() - start, TimeUnit.NANOSECONDS);
metrics.gauge("median.minHeap.size", minHeap::size);
metrics.gauge("median.maxHeap.size", maxHeap::size);
```

## Edge cases и ограничения

Пустой поток должен отдавать контрактно определённое значение (исключение или `NaN`) — зафиксируйте это в документации. Дубликаты допустимы и не ломают инвариант. Для больших данных учтите переполнение при суммировании корней: используйте `double` или `long` перед делением. Если поток очень длинный, подумайте о сбросе/агрегации и хранении только последних `k` элементов.

## Заключение

Две кучи дают быстрый и простой способ поддерживать медиану в онлайне: вставка `O(log n)`, запрос `O(1)`, минимум кода и чёткий инвариант. Для продакшена важно проверить балансировку на тестах, выбрать тип для точности и, при необходимости, добавить потокобезопасность и ленивое удаление. Так решатель остаётся предсказуемым и готовым к нагрузке.
