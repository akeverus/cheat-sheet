---
title: "Руководство по алгоритму HyperLogLog"
description: "Руководство по использованию структуры данных HyperLogLog для оценки кардинальности больших наборов данных с минимальным использованием памяти."
tags:
  - algorithms
  - ai-ml
  - hyperloglog
type: "reference"
difficulty: "intermediate"
aliases:
  - "hyperloglog"
prerequisites: []
next: []
updated: "2026-04-20"
---
# Руководство по алгоритму HyperLogLog

Руководство по использованию структуры данных **HyperLogLog** для оценки кардинальности больших наборов данных с минимальным использованием памяти.

## Полезные ссылки

### Официальная документация
- [HyperLogLog (Wikipedia)](https://en.wikipedia.org/wiki/HyperLogLog)
- [HyperLogLog (stream-lib GitHub)](https://github.com/addthis/stream-lib)

### См. также
- [Практические примеры Big O](../data-structures/collections-big-o.md)
- [Временная сложность коллекций](../data-structures/collections-complexity.md) — Big O коллекций

- [Логистическая регрессия](logistic-regression.md)
- [Реализация CNN с помощью Deeplearning4j](cnn-deeplearning4j.md)
- [Обзор ИИ библиотек](ai-libraries.md)
## Содержание

- [Обзор](#обзор)
- [Что такое HyperLogLog?](#что-такое-hyperloglog)
- [Настройка проекта](#настройка-проекта)
- [Реализация на Java](#реализация-на-java)
  - [Создание и использование HLL (Java)](#создание-и-использование-hll-java)
- [Операция объединения](#операция-объединения)
- [Сравнение с наивной реализацией](#сравнение-с-наивной-реализацией)
  - [Таблица сравнения](#таблица-сравнения)
- [Параметры конфигурации](#параметры-конфигурации)
  - [log2m](#log2m)
  - [regwidth](#regwidth)
- [Примеры использования](#примеры-использования)
  - [Подсчет уникальных посетителей](#подсчет-уникальных-посетителей)
  - [Объединение данных из разных источников](#объединение-данных-из-разных-источников)
- [Ограничения](#ограничения)
- [Когда использовать HyperLogLog](#когда-использовать-hyperloglog)
  - [Используйте HLL, когда:](#используйте-hll-когда)
  - [Не используйте HLL, когда:](#не-используйте-hll-когда)
- [Лучшие практики](#лучшие-практики)
- [Решение проблем](#решение-проблем)
- [Частые вопросы](#частые-вопросы)
- [Резюме](#резюме)
- [Реализация на Kotlin](#реализация-на-kotlin)
  - [Создание и использование HLL (Kotlin)](#создание-и-использование-hll-kotlin)
  - [Операция объединения](#операция-объединения-1)
  - [Подсчет уникальных посетителей](#подсчет-уникальных-посетителей-1)

## Обзор

Структура данных **HyperLogLog** (HLL) — это вероятностная структура данных, используемая для оценки кардинальности набора данных.

Предположим, что у нас есть миллионы пользователей, и мы хотим подсчитать количество отдельных посещений нашей веб-страницы. Наивной реализацией было бы хранить каждый уникальный идентификатор пользователя в наборе, и тогда размер набора был бы нашей кардинальностью.

Когда мы имеем дело с очень большими объемами данных, подсчет кардинальности таким образом будет очень неэффективным, поскольку набор данных будет занимать много памяти.

Но если нас устраивает оценка в пределах нескольких процентов и нам не нужно точное количество уникальных посещений, то мы можем использовать **HLL**, так как он был разработан именно для такого варианта использования — оценки количества миллионов или даже миллиардов различных значений.

## Что такое HyperLogLog?

**HyperLogLog** — это вероятностный алгоритм для оценки количества уникальных элементов (кардинальности) в мультимножестве. Он использует очень мало памяти (обычно несколько килобайт) для оценки кардинальности очень больших наборов данных.

**Основные преимущества:**
- Очень компактное хранение
- Быстрая обработка
- Приемлемая точность (обычно в пределах 1-2%)
- Поддержка операций объединения

## Настройка проекта

Зависимость Maven для библиотеки `hll`:

```xml
<!-- HyperLogLog: оценка кардинальности с минимальной памятью -->
<dependency>
    <groupId>net.agkn</groupId>
    <artifactId>hll</artifactId>
    <version>1.6.0</version>
</dependency>
```

## Реализация на Java

### Создание и использование HLL (Java)

Перейдем сразу к делу: у конструктора **HLL** есть два аргумента, которые мы можем настроить в соответствии с нашими потребностями:

- **log2m** (логарифмическая база 2) — это количество регистров, используемых внутри **HLL** (примечание: мы указываем m)
- **regwidth** — это количество битов, используемых в регистре

Если нам нужна более высокая точность, нам нужно установить для них более высокие значения. Такая конфигурация будет иметь дополнительные накладные расходы, потому что наш **HLL** будет занимать больше памяти. Если нас устраивает более низкая точность, мы можем уменьшить эти параметры, и наш **HLL** будет занимать меньше памяти.

Давайте создадим **HLL** для подсчета различных значений для набора данных со `100` миллионами записей. Мы установим параметр **log2m** равным 14 и **regwidth** равным 5 — разумные значения для набора данных такого размера.

Когда каждый новый элемент вставляется в **HLL**, его необходимо предварительно хэшировать. Мы будем использовать `Hashing.murmur3_128()` из библиотеки **Guava** (включенной в зависимость hll), потому что она точна и быстра.

```java
// Оценка кардинальности больших множеств: HyperLogLog с MurmurHash, log2m и regwidth задают точность.
import net.agkn.hll.HLL;
import com.google.common.hash.HashFunction;
import com.google.common.hash.Hashing;
import java.util.stream.LongStream;
import org.assertj.core.data.Offset;

public class HyperLogLogExample {

    public void estimateCardinality() {
        HashFunction hashFunction = Hashing.murmur3_128();
        long numberOfElements = 100_000_000;
        long toleratedDifference = 1_000_000;

        HLL hll = new HLL(14, 5);

        // Вставляем элементы
        LongStream.range(0, numberOfElements).forEach(element -> {
            long hashedValue = hashFunction.newHasher()
                .putLong(element)
                .hash()
                .asLong();
            hll.addRaw(hashedValue);
        });

        // Проверяем оценку
        long cardinality = hll.cardinality();

        assertThat(cardinality)
            .isCloseTo(numberOfElements, Offset.offset(toleratedDifference));
    }
}
```

Выбор этих параметров должен дать нам частоту ошибок ниже одного процента (1 `000 000` элементов). Мы проверим это через мгновение.

## Операция объединения

**HLL** имеет одно полезное свойство при выполнении союзов. Когда мы возьмем объединение двух **HLL**, созданных из разных наборов данных, и измерим его кардинальность, мы получим тот же порог ошибки для объединения, который мы получили бы, если бы использовали один **HLL** и вычислили хеш-значения для всех элементов обоих наборов данных с самого начала.

Обратите внимание, что когда мы объединяем два **HLL**, оба должны иметь одинаковые параметры **log2m** и **regwidth**, чтобы получить правильные результаты.

**Давайте проверим это свойство, создав два **HLL** — один заполняется значениями от 0 до `100` миллионов, а второй — значениями от `100` миллионов до `200` миллионов:**

```java
public void unionExample() {
    HashFunction hashFunction = Hashing.murmur3_128();
    long numberOfElements = 100_000_000;
    long toleratedDifference = 1_000_000;

    HLL firstHll = new HLL(15, 5);
    HLL secondHLL = new HLL(15, 5);

    // Заполняем первый HLL
    LongStream.range(0, numberOfElements).forEach(element -> {
        long hashedValue = hashFunction.newHasher()
            .putLong(element)
            .hash()
            .asLong();
        firstHll.addRaw(hashedValue);
    });

    // Заполняем второй HLL
    LongStream.range(numberOfElements, numberOfElements * 2).forEach(element -> {
        long hashedValue = hashFunction.newHasher()
            .putLong(element)
            .hash()
            .asLong();
        secondHLL.addRaw(hashedValue);
    });

    // Объединяем
    firstHll.union(secondHLL);
    long cardinality = firstHll.cardinality();

    assertThat(cardinality)
        .isCloseTo(numberOfElements * 2, Offset.offset(toleratedDifference * 2));
}
```

Обратите внимание, что мы настроили параметры конфигурации **HLL**, увеличив параметр **log2m** с 14, как показано в предыдущем разделе, до 15 для этого примера, поскольку результирующее объединение **HLL** будет содержать в два раза больше элементов.

## Сравнение с наивной реализацией

Мы можем рассчитать, сколько памяти займет наш **HLL** из предыдущего раздела, используя следующую формулу: `numberOfBits = 2^log2m * regwidth`.

В нашем примере это будет `2^14 * 5` бит (примерно `81000` бит или `8100` байт). Таким образом, оценка мощности набора из `100` миллионов элементов с использованием **HLL** заняла всего `8100` байт памяти.

Давайте сравним это с наивной реализацией множества. В такой реализации нам нужно иметь **Set** из `100` миллионов значений **Long**, который бы занимал `100 `000 000` * 8 байт = `800 000 000` байт`.

Мы видим, что разница поразительно велика. При использовании **HLL** нам нужно всего `8100` байт, тогда как при использовании простой реализации **Set** нам потребуется примерно `800` мегабайт.

Когда мы рассматриваем большие наборы данных, разница между **HLL** и наивной реализацией **Set** становится еще больше.

### Таблица сравнения

| Размер данных | **Set** (байт) | **HLL** (байт) | Экономия |
|---------------|------------|------------|----------|
| 1 миллион | 8,`000`,`000` | ~8,`100` | 99.9% |
| `100` миллионов | `800`,`000`,`000` | ~8,`100` | 99.999% |
| 1 миллиард | 8,`000`,`000`,`000` | ~8,`100` | 99.9999% |

## Параметры конфигурации

### log2m

Параметр **log2m** определяет количество регистров в **HLL**. Чем больше **log2m**, тем выше точность, но и больше памяти.

- **log2m = 10**: ~1 КБ памяти, точность ~3%
- **log2m = 14**: ~8 КБ памяти, точность ~1%
- **log2m = 18**: ~128 КБ памяти, точность ~0.3%

### regwidth

Параметр **regwidth** определяет количество битов на регистр. Обычно используется значение 5 или 6.

- **regwidth = 4**: Меньше памяти, но может быть недостаточно для больших наборов
- **regwidth = 5**: Хороший баланс (рекомендуется)
- **regwidth = 6**: Выше точность, больше памяти

## Примеры использования

### Подсчет уникальных посетителей

```java
public class UniqueVisitorsCounter {
    private HLL hll;

    public UniqueVisitorsCounter() {
        // log2m=14, regwidth=5 для ~100 миллионов уникальных посетителей
        this.hll = new HLL(14, 5);
    }

    public void recordVisit(String userId) {
        HashFunction hashFunction = Hashing.murmur3_128();
        long hashedValue = hashFunction.newHasher()
            .putString(userId, Charsets.UTF_8)
            .hash()
            .asLong();
        hll.addRaw(hashedValue);
    }

    public long getUniqueVisitorsCount() {
        return hll.cardinality();
    }
}
```

### Объединение данных из разных источников

```java
public class DistributedCardinality {

    public HLL mergeCounters(List<HLL> hlls) {
        if (hlls.isEmpty()) {
            return new HLL(14, 5);
        }

        HLL result = hlls.get(0);
        for (int i = 1; i < hlls.size(); i++) {
            result.union(hlls.get(i));
        }

        return result;
    }
}
```

## Ограничения

1. **Точность**: **HLL** дает оценку, а не точное значение
2. **Требуется хеширование**: Все элементы должны быть хешированы перед добавлением
3. **Параметры**: Нужно правильно выбрать **log2m** и **regwidth** для вашего случая использования
4. **Нельзя удалять элементы**: **HLL** не поддерживает удаление элементов

## Когда использовать HyperLogLog

### Используйте HLL, когда:
- Нужна оценка кардинальности больших наборов данных
- Точность в пределах 1-2% приемлема
- Важна экономия памяти
- Нужно объединять данные из разных источников

### Не используйте HLL, когда:
- Нужна точная кардинальность
- Нужно удалять элементы
- Набор данных очень маленький (меньше `1000` элементов)
- Нужно знать, какие именно элементы присутствуют

## Лучшие практики

Увеличивайте `log2m` и `regwidth` для большей точности (больше памяти); для порядка 100M элементов обычно log2m=14, regwidth=5. Используйте MurmurHash3 (Guava `Hashing.murmur3_128`) и хешируйте элементы перед добавлением в HLL. Несколько HLL с одинаковыми параметрами можно объединять для оценки кардинальности объединения потоков. Для точного подсчёта или поддержки удаления используйте Set или Count-Min Sketch.

## Решение проблем

| Симптом | Возможная причина | Решение |
|--------|-------------------|---------|
| Оценка сильно занижена или завышена | Слишком малые log2m/regwidth или плохой хеш | Увеличить log2m/regwidth; использовать MurmurHash3, хешировать до добавления |
| Нельзя объединить два HLL | Разные параметры (log2m, regwidth) | Создавать HLL с одинаковыми параметрами при необходимости union |
| Нужен точный подсчёт | HLL — вероятностная оценка | Использовать Set (или другой точный подсчёт) при ограниченном объёме |

## Частые вопросы

**Какая типичная точность HyperLogLog?** Стандартная ошибка порядка 1–2% при корректных параметрах; точность повышается с ростом log2m и regwidth за счёт памяти.

**Можно ли удалять элементы из HLL?** Нет, HLL не поддерживает удаление; для сценариев с удалением нужны другие структуры (например, Count-Min Sketch с decrement или точные структуры).

**Когда объединять несколько HLL?** При агрегации кардинальности по нескольким потокам или узлам (например, уникальные посетители по регионам) — у всех HLL должны совпадать log2m и regwidth.

## Резюме

В этом уроке мы рассмотрели алгоритм **HyperLogLog**.

Мы увидели, как использовать **HLL** для оценки мощности множества. Мы также увидели, что **HLL** очень компактен по сравнению с простым решением. И мы выполнили операцию объединения на двух **HLL** и убедились, что объединение ведет себя так же, как и один **HLL**.

**Ключевые моменты:**
- **HLL** использует очень мало памяти для оценки кардинальности
- Точность обычно в пределах 1-2%
- Поддерживает операцию объединения
- Требует хеширования элементов перед добавлением
- Идеально подходит для больших наборов данных

**HyperLogLog** — это инструмент для работы с большими данными, когда точная кардинальность не критична, но важна экономия памяти.

## Реализация на Kotlin

### Создание и использование HLL (Kotlin)

```kotlin
import net.agkn.hll.HLL
import com.google.common.hash.HashFunction
import com.google.common.hash.Hashing

class HyperLogLogExampleK {
    fun estimateCardinality() {
        val hashFunction: HashFunction = Hashing.murmur3_128()
        val numberOfElements = 100_000_000L
        val toleratedDifference = 1_000_000L

        val hll = HLL(14, 5)

        // Вставляем элементы
        (0 until numberOfElements).forEach { element ->
            val hashedValue = hashFunction.newHasher()
                .putLong(element)
                .hash()
                .asLong()
            hll.addRaw(hashedValue)
        }

        // Проверяем оценку
        val cardinality = hll.cardinality()

        println("Estimated cardinality: $cardinality")
        println("Actual cardinality: $numberOfElements")
        println("Difference: ${Math.abs(cardinality - numberOfElements)}")
    }
}
```

### Операция объединения

```kotlin
fun unionExampleK() {
    val hashFunction: HashFunction = Hashing.murmur3_128()
    val numberOfElements = 100_000_000L
    val toleratedDifference = 1_000_000L

    val firstHll = HLL(15, 5)
    val secondHLL = HLL(15, 5)

    // Заполняем первый HLL
    (0 until numberOfElements).forEach { element ->
        val hashedValue = hashFunction.newHasher()
            .putLong(element)
            .hash()
            .asLong()
        firstHll.addRaw(hashedValue)
    }

    // Заполняем второй HLL
    (numberOfElements until numberOfElements * 2).forEach { element ->
        val hashedValue = hashFunction.newHasher()
            .putLong(element)
            .hash()
            .asLong()
        secondHLL.addRaw(hashedValue)
    }

    // Объединяем
    firstHll.union(secondHLL)
    val cardinality = firstHll.cardinality()

    println("Union cardinality: $cardinality")
    println("Expected: ${numberOfElements * 2}")
}
```

### Подсчет уникальных посетителей

```kotlin
class UniqueVisitorsCounterK {
    private val hll: HLL = HLL(14, 5)
    private val hashFunction: HashFunction = Hashing.murmur3_128()

    fun addVisitor(visitorId: String) {
        val hashedValue = hashFunction.newHasher()
            .putString(visitorId, Charsets.UTF_8)
            .hash()
            .asLong()
        hll.addRaw(hashedValue)
    }

    fun getUniqueVisitorsCount(): Long {
        return hll.cardinality()
    }
}
```
