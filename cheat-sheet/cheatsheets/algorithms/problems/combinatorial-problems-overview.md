# Combinatorial Problems Overview

Кратко: обзор комбинаторных задач: перестановки, наборы мощности (powerset) и комбинации. Рассматриваются рекурсивные алгоритмы для генерации всех возможных комбинаций.

**Дата последнего обновления:** 2025-01-15

## Полезные ссылки

### Официальная документация
- [Baeldung: Combinatorial Problems](https://www.baeldung.com/java-combinatorial-algorithms)

### См. также
- `../strings/string-permutations.md` - перестановки строк
- `../math/factorial-calculation.md` - вычисление факториала
- `../math/pascal-triangle.md` - треугольник Паскаля

## Содержание

- [Описание алгоритма](#описание-алгоритма)
- [Java Implementation](#java-implementation)
- [Kotlin Implementation](#kotlin-implementation)
- [Сравнение задач](#сравнение-задач)
- [Сложность](#сложность)

## Описание алгоритма

В этом уроке мы узнаем, как решить несколько распространенных комбинаторных задач. В повседневной работе они, скорее всего, не очень полезны; однако они интересны с алгоритмической точки зрения. Мы можем найти их удобными для целей тестирования.

Имейте в виду, что существует множество различных подходов к решению этих проблем. Мы постарались сделать представленные решения простыми для понимания.

## Java Implementation

### Перестановки

Перестановка - это перестановка последовательности таким образом, чтобы она имела другой порядок.

Как мы знаем из математики, для последовательности из n элементов существует n! разные перестановки. n! называется факториальной операцией:

```
n! = 1 * 2 * … * n
```

Так, например, для последовательности [1, 2, 3] имеется шесть перестановок:

- [1, 2, 3]
- [1, 3, 2]
- [2, 1, 3]
- [2, 3, 1]
- [3, 1, 2]
- [3, 2, 1]

Факториал растет очень быстро - для последовательности из 10 элементов у нас есть 3,628,800 различных перестановок! В этом случае мы говорим о перестановочных последовательностях, где каждый отдельный элемент отличается.

### Алгоритм генерации перестановок

Это хорошая идея подумать о генерации перестановок рекурсивным способом. Введем понятие состояния. Он будет состоять из двух вещей: текущей перестановки и индекса обрабатываемого в данный момент элемента.

Единственная работа, которую нужно сделать в таком состоянии, - поменять местами элемент со всеми оставшимися и выполнить переход в состояние с измененной последовательностью и индексом, увеличенным на единицу.

### Реализация

Алгоритм, написанный на Java:

```java
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

Наша функция принимает три параметра: обрабатываемую в данный момент последовательность, результаты (перестановки) и индекс обрабатываемого в данный момент элемента.

Первое, что нужно сделать, это проверить, достигли ли мы последнего элемента. Если это так, мы добавляем последовательность в список результатов.

Затем в цикле for мы выполняем обмен, делаем рекурсивный вызов метода, а затем обмениваем элемент обратно.

Последняя часть представляет собой небольшой трюк с производительностью: мы можем все время работать с одним и тем же объектом последовательности, не создавая новую последовательность для каждого рекурсивного вызова.

### Пример использования

```java
List<Integer> sequence = Arrays.asList(1, 2, 3);
List<List<Integer>> permutations = Permutations.generatePermutations(sequence);
// Результат: [[1,2,3], [1,3,2], [2,1,3], [2,3,1], [3,1,2], [3,2,1]]
```

**Важно:** Показанный алгоритм будет работать только для последовательностей уникальных элементов! Применение того же алгоритма к последовательностям с повторяющимися элементами даст нам повторения.

## Набор мощности (Powerset)

Другая популярная задача - генерация набора мощности множества. Начнем с определения:

**Powerset** (или power set) набора S - это набор всех подмножеств S, включая пустой набор и само S.

Так, например, для набора [a, b, c] набор мощности содержит восемь подмножеств:

- []
- [a]
- [b]
- [c]
- [a, b]
- [a, c]
- [b, c]
- [a, b, c]

Из математики мы знаем, что для набора, состоящего из n элементов, набор мощности должен содержать 2^n подмножеств. Это число также быстро растет, но не так быстро, как факториал.

### Алгоритм генерации powerset

На этот раз мы также будем думать рекурсивно. Теперь наше состояние будет состоять из двух вещей: индекса обрабатываемого в данный момент элемента в наборе и аккумулятора.

Нам нужно принять решение с двумя вариантами выбора в каждом состоянии: помещать текущий элемент в аккумулятор или нет. Когда наш индекс достигает конца набора, у нас есть одно возможное подмножество. Таким образом, мы можем сгенерировать все возможные подмножества.

### Реализация

Наш алгоритм, написанный на Java:

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

Наша функция принимает четыре параметра: набор, для которого мы хотим сгенерировать подмножества, результирующий набор мощности, аккумулятор и индекс обрабатываемого в данный момент элемента.

Для простоты мы храним наши наборы в списках. Мы хотим иметь быстрый доступ к элементам, указанным индексом, чего мы можем добиться с помощью List, но не с помощью Set.

Во-первых, мы проверяем, превышает ли индекс установленный размер. Если это так, то мы помещаем аккумулятор в набор результатов, иначе мы:

1. Помещаем текущий рассматриваемый элемент в аккумулятор
2. Делаем рекурсивный вызов с увеличенным индексом и расширенным аккумулятором
3. Удаляем последний элемент из аккумулятора, который мы добавили ранее
4. Делаем вызов снова с неизменным аккумулятором и увеличенным индексом

### Пример использования

```java
List<Character> set = Arrays.asList('a', 'b', 'c');
List<List<Character>> powerset = Powerset.generatePowerset(set);
// Результат: [[], [a], [b], [a,b], [c], [a,c], [b,c], [a,b,c]]
```

## Комбинации

Теперь пришло время заняться комбинациями. Мы определяем его следующим образом:

**k-комбинация** множества S - это подмножество из k различных элементов из S, где порядок элементов не имеет значения.

Количество k-комбинаций описывается биномиальным коэффициентом:

```
C(n, k) = n! / (k! × (n-k)!)
```

Так, например, для множества [a, b, c] имеем три 2-комбинации:

- [a, b]
- [a, c]
- [b, c]

Комбинации имеют множество комбинаторных применений и объяснений. В качестве примера предположим, что у нас есть футбольная лига, состоящая из 16 команд. Сколько разных совпадений мы можем увидеть?

Ответ: C(16, 2) = 120, который оценивается как 120.

### Алгоритм генерации комбинаций

Концептуально мы сделаем что-то похожее на предыдущий алгоритм для наборов мощности. У нас будет рекурсивная функция, состояние которой состоит из индекса обрабатываемого в данный момент элемента и аккумулятора.

Опять же, у нас есть одно и то же решение для каждого состояния: добавить элемент в аккумулятор? Однако на этот раз у нас есть дополнительное ограничение - наш аккумулятор не может содержать более k элементов.

### Реализация

Давайте посмотрим на реализацию алгоритма в Java:

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

На этот раз наша функция имеет пять параметров: набор входных данных, параметр k, список результатов, аккумулятор и индекс текущего обрабатываемого элемента.

Начнем с определения вспомогательных переменных:

1. **needToAccumulate** - указывает, сколько еще элементов нам нужно добавить в наш аккумулятор, чтобы получить правильную комбинацию
2. **canAccumulate** - указывает, сколько еще элементов мы можем добавить в наш аккумулятор

Теперь мы проверяем, равен ли размер нашего аккумулятора k. Если это так, то мы можем поместить скопированный массив в список результатов.

В другом случае, если у нас еще достаточно элементов в оставшейся части набора, мы делаем два отдельных рекурсивных вызова: с помещением в аккумулятор текущего обрабатываемого элемента и без него. Эта часть аналогична тому, как мы создали набор мощности ранее.

### Пример использования

```java
List<Integer> inputSet = Arrays.asList(1, 2, 3, 4);
List<List<Integer>> combinations = Combinations.combinations(inputSet, 2);
// Результат: [[1,2], [1,3], [1,4], [2,3], [2,4], [3,4]]
```

## Сравнение задач

| Задача | Количество результатов | Формула | Пример |
|--------|----------------------|---------|--------|
| Перестановки | n! | n! | n=3 → 6 |
| Powerset | 2^n | 2^n | n=3 → 8 |
| Комбинации | C(n,k) | n!/(k!(n-k)!) | n=4, k=2 → 6 |

### Стоит отметить

Биномиальный коэффициент не обязательно должен быть огромным числом. Например:

- C(100, 2) = 4950
- C(100, 50) имеет 30 цифр!

## Kotlin Implementation

### Перестановки

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

### Набор мощности (Powerset)

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

### Комбинации

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

### Пример использования

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

### Временная сложность

- **Перестановки:** O(n! × n) - генерация всех перестановок
- **Powerset:** O(2^n × n) - генерация всех подмножеств
- **Комбинации:** O(C(n,k) × k) - генерация всех комбинаций

### Пространственная сложность

- **Все задачи:** O(n) - для рекурсивного стека и аккумулятора

## Особенности

- **Рекурсивность:** Все алгоритмы используют рекурсию
- **Экспоненциальный рост:** Количество результатов растет очень быстро
- **Простота:** Алгоритмы относительно просты для понимания

## Применение

Комбинаторные задачи используются в:

- Тестировании (генерация тестовых данных)
- Криптографии
- Оптимизации
- Исследованиях в области ИИ
- Обучении алгоритмам

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

### Используйте эти алгоритмы, когда:

- Нужно сгенерировать все возможные варианты
- Тестируете алгоритмы
- Решаете комбинаторные задачи
- Обучаете алгоритмам

### Не используйте, когда:

- Размер входных данных очень большой
- Нужно только количество, а не все варианты
- Важна производительность

## Заключение

В этой статье мы обсудили различные комбинаторные задачи. Кроме того, мы показали простые алгоритмы для их решения с реализациями на Java. В некоторых случаях эти алгоритмы могут помочь с необычными потребностями тестирования.
