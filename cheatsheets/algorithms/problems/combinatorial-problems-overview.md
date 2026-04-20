---
title: "Обзор комбинаторных задач (Combinatorial Problems Overview)"
description: "Обзор трёх типов задач: перестановки (n!), набор мощности — powerset (2^n подмножеств), k-комбинации C(n,k). Рекурсивные алгоритмы с откатом (backtracking), примеры на Java и Kotlin."
tags:
  - algorithms
  - problems
  - combinatorial-problems-overview
difficulty: "intermediate"
prerequisites: []
next: []
updated: "2026-02-11"
---
# Обзор комбинаторных задач (`Combinatorial Problems Overview`)

Обзор трёх типов задач: перестановки (n!), набор мощности — powerset (2^n подмножеств), k-комбинации C(n,k). Рекурсивные алгоритмы с откатом (backtracking), примеры на Java и Kotlin.

## Полезные ссылки

### Официальная документация
- [`Baeldung`: `Combinatorial Problems`](https://www.baeldung.com/)

### См. также
- [Перестановки строк](../strings/README.md) — алгоритмы со строками
- [[factorial-calculation|Вычисление факториала]] — факториал
- [[pascal-triangle|Треугольник Паскаля]] — треугольник Паскаля

## Содержание

- [Описание алгоритма](#описание-алгоритма)
- [Реализация на Java](#реализация-на-java)
- [Набор мощности (Powerset)](#набор-мощности-powerset)
- [Комбинации](#комбинации)
- [Сравнение задач](#сравнение-задач)
- [Реализация на Kotlin](#реализация-на-kotlin)
- [Сложность](#сложность)
- [Особенности](#особенности)
- [Применение](#применение)
- [Варианты задачи](#варианты-задачи)
- [Когда использовать](#когда-использовать)
- [Лучшие практики](#лучшие-практики)
- [Решение проблем](#решение-проблем)
- [Частые вопросы](#частые-вопросы)
- [Заключение](#заключение)


## Описание алгоритма

Комбинаторные задачи: генерация всех перестановок (порядок важен), всех подмножеств (powerset), всех k-комбинаций (подмножества размера k без учёта порядка). Полезны в тестах, обучении и задачах перебора; решения рекурсивные с откатом.

## Реализация на Java

Перестановки: для n элементов их число n! = 1·2·…·n. Рекурсия: состояние — последовательность и индекс; на каждом шаге меняем элемент по индексу со всеми последующими, рекурсивно генерируем для index+1, откатываем обмен.

Пример для [1,2,3]: 6 перестановок. Для n=10 уже 3 628 800. Алгоритм ниже применим только к последовательностям без повторяющихся элементов.

```java
// Генерация всех перестановок последовательности рекурсивным обменом (swap) и откатом
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Permutations {
    private static void swap(List<Integer> sequence, int i, int j) {
        Collections.swap(sequence, i, j);
    }
    
    private static void permutationsInternal(
        List<Integer> sequence, 
        List<List<Integer>> results, 
        int index
    ) {
        if (index == sequence.size() - 1) {
            results.add(new ArrayList<>(sequence));
            return;
        }
        
        for (int i = index; i < sequence.size(); i++) {
            swap(sequence, i, index);
            permutationsInternal(sequence, results, index + 1);
            swap(sequence, i, index); // Откат изменений
        }
    }
    
    public static List<List<Integer>> generatePermutations(List<Integer> sequence) {
        List<List<Integer>> permutations = new ArrayList<>();
        permutationsInternal(sequence, permutations, 0);
        return permutations;
    }
}
```

Пример:

```java
List<Integer> sequence = Arrays.asList(1, 2, 3);
List<List<Integer>> permutations = Permutations.generatePermutations(sequence);
// Результат: [[1,2,3], [1,3,2], [2,1,3], [2,3,1], [3,1,2], [3,2,1]]
```

## Набор мощности (Powerset)

Powerset множества S — множество всех подмножеств S (включая ∅ и S). Для n элементов подмножеств 2^n. Рекурсия: на каждом индексе два варианта — включить элемент в аккумулятор или нет; по достижении конца набора аккумулятор — одно подмножество.

```java
import java.util.ArrayList;
import java.util.List;

public class Powerset {
    private static void powersetInternal(
        List<Character> set, 
        List<List<Character>> powerset, 
        List<Character> accumulator, 
        int index
    ) {
        if (index == set.size()) {
            powerset.add(new ArrayList<>(accumulator));
        } else {
            // Включаем текущий элемент
            accumulator.add(set.get(index));
            powersetInternal(set, powerset, accumulator, index + 1);
            
            // Не включаем текущий элемент
            accumulator.remove(accumulator.size() - 1);
            powersetInternal(set, powerset, accumulator, index + 1);
        }
    }
    
    public static List<List<Character>> generatePowerset(List<Character> sequence) {
        List<List<Character>> powerset = new ArrayList<>();
        powersetInternal(sequence, powerset, new ArrayList<>(), 0);
        return powerset;
    }
}
```

Пример:

```java
List<Character> set = Arrays.asList('a', 'b', 'c');
List<List<Character>> powerset = Powerset.generatePowerset(set);
// Результат: [[], [a], [b], [a,b], [c], [a,c], [b,c], [a,b,c]]
```

## Комбинации

k-комбинация — подмножество из k различных элементов, порядок не важен. Число: C(n,k) = n!/(k!(n−k)!). Пример: C(16,2)=120 пар команд. Рекурсия как у powerset, но аккумулятор ограничен размером k; отсекаем ветки, когда оставшихся элементов не хватает до k.

```java
import java.util.ArrayList;
import java.util.List;

public class Combinations {
    private static void combinationsInternal(
        List<Integer> inputSet, 
        int k, 
        List<List<Integer>> results, 
        ArrayList<Integer> accumulator, 
        int index
    ) {
        int needToAccumulate = k - accumulator.size();
        int canAccumulate = inputSet.size() - index;
        
        if (accumulator.size() == k) {
            results.add(new ArrayList<>(accumulator));
        } else if (needToAccumulate <= canAccumulate) {
            // Не включаем текущий элемент
            combinationsInternal(inputSet, k, results, accumulator, index + 1);
            
            // Включаем текущий элемент
            accumulator.add(inputSet.get(index));
            combinationsInternal(inputSet, k, results, accumulator, index + 1);
            accumulator.remove(accumulator.size() - 1);
        }
    }
    
    public static List<List<Integer>> combinations(List<Integer> inputSet, int k) {
        List<List<Integer>> results = new ArrayList<>();
        combinationsInternal(inputSet, k, results, new ArrayList<>(), 0);
        return results;
    }
}
```

Пример:

```java
List<Integer> inputSet = Arrays.asList(1, 2, 3, 4);
List<List<Integer>> combinations = Combinations.combinations(inputSet, 2);
// Результат: [[1,2], [1,3], [1,4], [2,3], [2,4], [3,4]]
```

## Сравнение задач

| Задача | Количество | Формула | Пример |
|--------|------------|---------|--------|
| Перестановки | n! | n! | n=3 → 6 |
| Powerset | 2^n | 2^n | n=3 → 8 |
| Комбинации | C(n,k) | n!/(k!(n−k)!) | n=4, k=2 → 6 |

C(100,2)=4950; C(100,50) — число с десятками цифр.

## Реализация на Kotlin

```kotlin
object PermutationsK {
    private fun swap(sequence: MutableList<Int>, i: Int, j: Int) {
        val temp = sequence[i]
        sequence[i] = sequence[j]
        sequence[j] = temp
    }
    
    private fun permutationsInternal(
        sequence: MutableList<Int>,
        results: MutableList<List<Int>>,
        index: Int
    ) {
        if (index == sequence.size - 1) {
            results.add(ArrayList(sequence))
            return
        }
        
        for (i in index until sequence.size) {
            swap(sequence, i, index)
            permutationsInternal(sequence, results, index + 1)
            swap(sequence, i, index) // Откат изменений
        }
    }
    
    fun generatePermutations(sequence: List<Int>): List<List<Int>> {
        val permutations = mutableListOf<List<Int>>()
        permutationsInternal(sequence.toMutableList(), permutations, 0)
        return permutations
    }
}
```


```kotlin
object PowersetK {
    private fun powersetInternal(
        inputSet: List<Int>,
        accumulator: MutableList<Int>,
        results: MutableList<List<Int>>,
        index: Int
    ) {
        if (index == inputSet.size) {
            results.add(ArrayList(accumulator))
            return
        }
        
        accumulator.add(inputSet[index])
        powersetInternal(inputSet, accumulator, results, index + 1)
        accumulator.removeAt(accumulator.size - 1)
        powersetInternal(inputSet, accumulator, results, index + 1)
    }
    
    fun generatePowerset(inputSet: List<Int>): List<List<Int>> {
        val powerset = mutableListOf<List<Int>>()
        powersetInternal(inputSet, mutableListOf(), powerset, 0)
        return powerset
    }
}
```

```kotlin
object CombinationsK {
    private fun combinationsInternal(
        inputSet: List<Int>,
        accumulator: MutableList<Int>,
        results: MutableList<List<Int>>,
        index: Int,
        k: Int
    ) {
        if (accumulator.size == k) {
            results.add(ArrayList(accumulator))
            return
        }
        
        if (index >= inputSet.size) {
            return
        }
        
        accumulator.add(inputSet[index])
        combinationsInternal(inputSet, accumulator, results, index + 1, k)
        accumulator.removeAt(accumulator.size - 1)
        combinationsInternal(inputSet, accumulator, results, index + 1, k)
    }
    
    fun generateCombinations(inputSet: List<Int>, k: Int): List<List<Int>> {
        val combinations = mutableListOf<List<Int>>()
        combinationsInternal(inputSet, mutableListOf(), combinations, 0, k)
        return combinations
    }
}
```

```kotlin
fun main() {
    // Перестановки
    val sequence = listOf(1, 2, 3)
    val permutations = PermutationsK.generatePermutations(sequence)
    println("Permutations: $permutations")
    // [[1,2,3], [1,3,2], [2,1,3], [2,3,1], [3,1,2], [3,2,1]]
    
    // Powerset
    val inputSet = listOf(1, 2, 3)
    val powerset = PowersetK.generatePowerset(inputSet)
    println("Powerset: $powerset")
    // [[], [1], [2], [1,2], [3], [1,3], [2,3], [1,2,3]]
    
    // Комбинации
    val combinations = CombinationsK.generateCombinations(listOf(1, 2, 3, 4), 2)
    println("Combinations: $combinations")
    // [[1,2], [1,3], [1,4], [2,3], [2,4], [3,4]]
}
```

## Сложность

Время: перестановки O(n!·n), powerset O(2^n·n), комбинации O(C(n,k)·k). Память O(n) на стек и аккумулятор.

## Особенности

Все три задачи решаются рекурсией с откатом. Число результатов растёт быстро (n!, 2^n, C(n,k)); при больших n предпочтительна генерация по одному элементу (итератор/поток).

## Применение

Генерация тестовых данных, криптография, перебор в оптимизации и ИИ, обучение алгоритмам.

## Варианты задачи

### Вариант 1: Перестановки с повторениями

```java
public static List<List<Integer>> generatePermutationsWithRepetitions(
    List<Integer> sequence, 
    int length
) {
    // Генерация перестановок с повторениями
}
```

### Вариант 2: Комбинации с повторениями

```java
public static List<List<Integer>> combinationsWithRepetitions(
    List<Integer> inputSet, 
    int k
) {
    // Генерация комбинаций с повторениями
}
```

## Когда использовать

Уместны, когда нужно сгенерировать все варианты (тесты, перебор, обучение). Не подходят при очень большом n или когда достаточно только количества — тогда используйте формулы без генерации.

## Лучшие практики

Перестановки: swap и откат на месте, без копирования последовательности на каждом шаге. Powerset: рекурсия «включить/не включить» или битовые маски. Комбинации: отсекать ветки, где оставшихся элементов меньше чем нужно до k. При n>12 объём перестановок огромен — рассмотрите итератор/поток. В тестах проверять размер результата (n!, 2^n, C(n,k)).

## Решение проблем

| Симптом | Возможная причина | Решение |
|---------|-------------------|---------|
| Дубликаты в перестановках | Входные данные с повторяющимися элементами | Использовать алгоритм для мультимножеств (учёт частот) или фильтровать дубликаты |
| StackOverflowError | Слишком глубокий рекурсивный вызов при большом n | Итеративная реализация со стеком или генерация по одной (ленивый итератор) |
| Неверное количество комбинаций | Ошибка в отсечении веток или индексации | Проверить условие needToAccumulate <= canAccumulate; тест на размер результата C(n,k) |

## Частые вопросы

**Чем комбинации отличаются от перестановок?** В комбинациях порядок не важен (подмножество); в перестановках важен порядок всех n элементов. Число комбинаций C(n,k) ≤ число перестановок n!.

**Как генерировать перестановки по одной, не храня все?** Алгоритм Куна (Heap's algorithm) или лексикографический перебор: следующая перестановка за O(n), память O(n). Можно обернуть в итератор.

**Как учесть повторяющиеся элементы в перестановках?** Хранить частоты элементов; на каждом шаге выбирать элемент с ненулевой частотой, уменьшать частоту, рекурсировать, восстанавливать частоту. Число перестановок n!/(n1!·n2!·…).

## Заключение

Перестановки, powerset и k-комбинации решаются рекурсивно с откатом. Реализации на Java и Kotlin пригодны для тестов и обучения; при больших n предпочтительна ленивая генерация.
