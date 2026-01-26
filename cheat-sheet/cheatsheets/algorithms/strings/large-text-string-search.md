# Large Text String Search

Кратко: несколько алгоритмов поиска шаблона в большом тексте. Рассматриваются наивный алгоритм, Рабина-Карпа, Кнута-Морриса-Пратта и Бойера-Мура-Хорспула с анализом сложности.

**Дата последнего обновления:** 2025-01-15

## Полезные ссылки

### Официальная документация
- [GeeksforGeeks: Pattern Searching](https://www.geeksforgeeks.org/algorithms-gq/pattern-searching/)

### См. также
- `./suffix-tree-pattern-matching.md` - поиск с использованием дерева суффиксов
- `./multiple-keywords-check.md` - проверка нескольких ключевых слов

## Содержание

- [Описание алгоритма](#описание-алгоритма)
- [Java Implementation](#java-implementation)
- [Kotlin Implementation](#kotlin-implementation)
- [Сравнение алгоритмов](#сравнение-алгоритмов)
- [Сложность](#сложность)

## Описание алгоритма

В этой статье мы покажем несколько алгоритмов поиска шаблона в большом тексте. Мы опишем каждый алгоритм с предоставленным кодом и простой математической базой.

Обратите внимание, что предоставленные алгоритмы - не лучший способ выполнения полнотекстового поиска в более сложных приложениях. Чтобы правильно выполнять полнотекстовый поиск, мы можем использовать Solr или ElasticSearch.

## Java Implementation

### Алгоритм 1: Наивный поиск

Название этого алгоритма описывает его лучше, чем любое другое объяснение. Это самое естественное решение:

```java
public static int simpleTextSearch(char[] pattern, char[] text) {
    int patternSize = pattern.length;
    int textSize = text.length;
    int i = 0;
    
    while ((i + patternSize) <= textSize) {
        int j = 0;
        while (text[i + j] == pattern[j]) {
            j += 1;
            if (j >= patternSize) {
                return i;
            }
        }
        i += 1;
    }
    
    return -1;
}
```

Идея этого алгоритма проста: перебрать текст и, если есть совпадение для первой буквы шаблона, проверить, все ли буквы шаблона совпадают с текстом.

Если m - количество букв в шаблоне, а n - количество букв в тексте, временная сложность этих алгоритмов равна O(m*(n-m+1)).

Наихудший сценарий возникает в случае, когда String имеет много частичных вхождений:

```
Text: baeldunbaeldunbaeldunbaeldun
Pattern: baeldung
```

Как упоминалось выше, алгоритм простого текстового поиска очень неэффективен при длинных шаблонах и при наличии большого количества повторяющихся элементов шаблона.

## Алгоритм 2: Рабина-Карпа

Идея алгоритма Рабина-Карпа состоит в том, чтобы использовать хеширование для поиска шаблона в тексте. В начале алгоритма нам нужно вычислить хэш шаблона, который позже используется в алгоритме. Этот процесс называется вычислением отпечатков пальцев.

Важной особенностью этапа предварительной обработки является то, что его временная сложность составляет O(m), а итерация по тексту займет O(n), что дает временную сложность всего алгоритма O(m+n).

### Вспомогательные методы

Прежде чем мы начнем, давайте определим простые методы вычисления простых чисел, которые мы используем в алгоритме Рабина-Карпа:

```java
public static long getBiggerPrime(int m) {
    BigInteger prime = BigInteger.probablePrime(getNumberOfBits(m) + 1, new Random());
    return prime.longValue();
}

private static int getNumberOfBits(int number) {
    return Integer.SIZE - Integer.numberOfLeadingZeros(number);
}
```

### Реализация

Код алгоритма:

```java
public static int RabinKarpMethod(char[] pattern, char[] text) {
    int patternSize = pattern.length;
    int textSize = text.length;
    long prime = getBiggerPrime(patternSize);
    long r = 1;
    
    for (int i = 0; i < patternSize - 1; i++) {
        r *= 2;
        r = r % prime;
    }
    
    long[] t = new long[textSize];
    t[0] = 0;
    long pfinger = 0;
    
    for (int j = 0; j < patternSize; j++) {
        t[0] = (2 * t[0] + text[j]) % prime;
        pfinger = (2 * pfinger + pattern[j]) % prime;
    }
    
    int i = 0;
    boolean passed = false;
    int diff = textSize - patternSize;
    
    for (i = 0; i <= diff; i++) {
        if (t[i] == pfinger) {
            passed = true;
            for (int k = 0; k < patternSize; k++) {
                if (text[i + k] != pattern[k]) {
                    passed = false;
                    break;
                }
            }
            if (passed) {
                return i;
            }
        }
        
        if (i < diff) {
            long value = 2 * (t[i] - r * text[i]) + text[i + patternSize];
            t[i + 1] = ((value % prime) + prime) % prime;
        }
    }
    
    return -1;
}
```

В худшем случае временная сложность этого алгоритма составляет O(m*(n-m+1)). Однако в среднем этот алгоритм имеет временную сложность O(n+m).

Кроме того, существует версия этого алгоритма Монте-Карло, которая быстрее, но может привести к неправильным совпадениям (ложным срабатываниям).

## Алгоритм 3: Кнута-Морриса-Пратта (KMP)

В алгоритме простого текстового поиска мы видели, как алгоритм может работать медленно, если есть много частей текста, соответствующих шаблону.

Идея алгоритма Кнута-Морриса-Пратта заключается в вычислении таблицы сдвигов, которая предоставляет нам информацию о том, где мы должны искать наши кандидаты в шаблоны.

Java-реализация алгоритма KMP:

```java
public static int KnuthMorrisPrattSearch(char[] pattern, char[] text) {
    int patternSize = pattern.length;
    int textSize = text.length;
    int i = 0, j = 0;
    int[] shift = KnuthMorrisPrattShift(pattern);
    
    while ((i + patternSize) <= textSize) {
        while (text[i + j] == pattern[j]) {
            j += 1;
            if (j >= patternSize) {
                return i;
            }
        }
        
        if (j > 0) {
            i += shift[j - 1];
            j = Math.max(j - shift[j - 1], 0);
        } else {
            i++;
            j = 0;
        }
    }
    
    return -1;
}
```

А вот как мы рассчитываем таблицу смен:

```java
public static int[] KnuthMorrisPrattShift(char[] pattern) {
    int patternSize = pattern.length;
    int[] shift = new int[patternSize];
    shift[0] = 1;
    int i = 1, j = 0;
    
    while ((i + j) < patternSize) {
        if (pattern[i + j] == pattern[j]) {
            shift[i + j] = i;
            j++;
        } else {
            if (j == 0) {
                shift[i] = i + 1;
            }
            if (j > 0) {
                i = i + shift[j - 1];
                j = Math.max(j - shift[j - 1], 0);
            } else {
                i = i + 1;
                j = 0;
            }
        }
    }
    
    return shift;
}
```

Временная сложность этого алгоритма также O(m+n).

## Алгоритм 4: Бойера-Мура-Хорспула

Двум ученым, Бойеру и Муру, пришла в голову другая идея. Почему бы не сравнить шаблон с текстом справа налево, а не слева направо, сохраняя при этом направление смещения:

```java
public static int BoyerMooreHorspoolSimpleSearch(char[] pattern, char[] text) {
    int patternSize = pattern.length;
    int textSize = text.length;
    int i = 0, j = 0;
    
    while ((i + patternSize) <= textSize) {
        j = patternSize - 1;
        while (text[i + j] == pattern[j]) {
            j--;
            if (j < 0) {
                return i;
            }
        }
        i++;
    }
    
    return -1;
}
```

Как и ожидалось, это будет выполняться за время O(m * n). Но этот алгоритм привел к реализации эвристики вхождения и совпадения, что значительно ускорило алгоритм.

Существует много вариантов эвристической реализации алгоритма Бойера-Мура, и самый простой из них - вариант Хорспула.

Эта версия алгоритма называется Бойера-Мура-Хорспула, и эта вариация решает проблему отрицательных сдвигов (мы можем прочитать о проблеме отрицательных сдвигов в описании алгоритма Бойера-Мура).

Как и в алгоритме Бойера-Мура, временная сложность сценария в наихудшем случае составляет O(m * n), а средняя сложность - O(n). Использование пространства не зависит от размера шаблона, а только от размера алфавита, который равен 256, поскольку это максимальное значение символа ASCII в английском алфавите:

```java
public static int BoyerMooreHorspoolSearch(char[] pattern, char[] text) {
    int shift[] = new int[256];
    
    for (int k = 0; k < 256; k++) {
        shift[k] = pattern.length;
    }
    
    for (int k = 0; k < pattern.length - 1; k++) {
        shift[pattern[k]] = pattern.length - 1 - k;
    }
    
    int i = 0, j = 0;
    
    while ((i + pattern.length) <= text.length) {
        j = pattern.length - 1;
        while (text[i + j] == pattern[j]) {
            j -= 1;
            if (j < 0) {
                return i;
            }
        }
        i = i + shift[text[i + pattern.length - 1]];
    }
    
    return -1;
}
```

## Kotlin Implementation

### Алгоритм 1: Наивный поиск

```kotlin
fun simpleTextSearchK(pattern: CharArray, text: CharArray): Int {
    val patternSize = pattern.size
    val textSize = text.size
    var i = 0
    
    while (i + patternSize <= textSize) {
        var j = 0
        while (text[i + j] == pattern[j]) {
            j++
            if (j >= patternSize) {
                return i
            }
        }
        i++
    }
    
    return -1
}
```

### Алгоритм 2: Рабина-Карпа

```kotlin
fun getBiggerPrimeK(m: Int): Long {
    val bits = Integer.SIZE - Integer.numberOfLeadingZeros(m) + 1
    return BigInteger.probablePrime(bits, Random()).toLong()
}

fun rabinKarpMethodK(pattern: CharArray, text: CharArray): Int {
    val patternSize = pattern.size
    val textSize = text.size
    val prime = getBiggerPrimeK(patternSize)
    var r = 1L
    
    for (i in 0 until patternSize - 1) {
        r = (2 * r) % prime
    }
    
    var fp = 0L
    var ft = 0L
    
    for (i in 0 until patternSize) {
        fp = ((2 * fp) + pattern[i].toInt()) % prime
        ft = ((2 * ft) + text[i].toInt()) % prime
    }
    
    var j = 0
    
    while ((j + patternSize) <= textSize) {
        if (fp == ft) {
            var matches = true
            for (i in 0 until patternSize) {
                if (pattern[i] != text[j + i]) {
                    matches = false
                    break
                }
            }
            if (matches) {
                return j
            }
        }
        
        if (j + patternSize < textSize) {
            ft = (2 * (ft - text[j].toInt() * r) + text[j + patternSize].toInt()) % prime
            if (ft < 0) {
                ft += prime
            }
        }
        j++
    }
    
    return -1
}
```

### Алгоритм 3: Кнута-Морриса-Пратта (KMP)

```kotlin
fun knuthMorrisPrattShiftK(pattern: CharArray): IntArray {
    val patternSize = pattern.size
    val shift = IntArray(patternSize)
    shift[0] = 1
    var i = 1
    var j = 0
    
    while (i + j < patternSize) {
        if (pattern[i + j] == pattern[j]) {
            shift[i + j] = i
            j++
        } else {
            if (j == 0) {
                shift[i] = i + 1
            }
            if (j > 0) {
                i += shift[j - 1]
                j = maxOf(j - shift[j - 1], 0)
            } else {
                i++
                j = 0
            }
        }
    }
    
    return shift
}

fun knuthMorrisPrattSearchK(pattern: CharArray, text: CharArray): Int {
    val patternSize = pattern.size
    val textSize = text.size
    var i = 0
    var j = 0
    val shift = knuthMorrisPrattShiftK(pattern)
    
    while (i + patternSize <= textSize) {
        while (text[i + j] == pattern[j]) {
            j++
            if (j >= patternSize) {
                return i
            }
        }
        
        if (j > 0) {
            i += shift[j - 1]
            j = maxOf(j - shift[j - 1], 0)
        } else {
            i++
            j = 0
        }
    }
    
    return -1
}
```

### Алгоритм 4: Бойера-Мура-Хорспула

```kotlin
fun boyerMooreHorspoolSearchK(pattern: CharArray, text: CharArray): Int {
    val shift = IntArray(256) { pattern.size }
    
    for (k in 0 until pattern.size - 1) {
        shift[pattern[k].code] = pattern.size - 1 - k
    }
    
    var i = 0
    
    while (i + pattern.size <= text.size) {
        var j = pattern.size - 1
        while (text[i + j] == pattern[j]) {
            j--
            if (j < 0) {
                return i
            }
        }
        i += shift[text[i + pattern.size - 1].code]
    }
    
    return -1
}

fun boyerMooreHorspoolSimpleSearchK(pattern: CharArray, text: CharArray): Int {
    val patternSize = pattern.size
    val textSize = text.size
    var i = 0
    
    while (i + patternSize <= textSize) {
        var j = patternSize - 1
        while (text[i + j] == pattern[j]) {
            j--
            if (j < 0) {
                return i
            }
        }
        i++
    }
    
    return -1
}
```

### Пример использования

```kotlin
fun main() {
    val text = "baeldunbaeldunbaeldunbaeldung".toCharArray()
    val pattern = "baeldung".toCharArray()
    
    // Наивный поиск
    val index1 = simpleTextSearchK(pattern, text)
    println("Наивный поиск: $index1")
    
    // Рабина-Карпа
    val index2 = rabinKarpMethodK(pattern, text)
    println("Рабина-Карпа: $index2")
    
    // KMP
    val index3 = knuthMorrisPrattSearchK(pattern, text)
    println("KMP: $index3")
    
    // Бойера-Мура-Хорспула
    val index4 = boyerMooreHorspoolSearchK(pattern, text)
    println("Бойера-Мура-Хорспула: $index4")
}
```

## Сравнение алгоритмов

| Алгоритм | Лучший случай | Средний случай | Худший случай | Пространственная сложность |
|----------|---------------|----------------|---------------|----------------------------|
| Наивный | O(n) | O(n*m) | O(n*m) | O(1) |
| Рабина-Карпа | O(n+m) | O(n+m) | O(n*m) | O(1) |
| KMP | O(n+m) | O(n+m) | O(n+m) | O(m) |
| Бойера-Мура-Хорспула | O(n/m) | O(n) | O(n*m) | O(256) |

## Сложность

### Временная сложность

- **Наивный:** O(n*m) - для каждого символа текста проверяем весь шаблон
- **Рабина-Карпа:** O(n+m) в среднем, O(n*m) в худшем случае
- **KMP:** O(n+m) - гарантированная линейная сложность
- **Бойера-Мура-Хорспула:** O(n) в среднем, O(n*m) в худшем случае

### Пространственная сложность

- **Наивный:** O(1) - только константная память
- **Рабина-Карпа:** O(1) - только константная память
- **KMP:** O(m) - для таблицы сдвигов
- **Бойера-Мура-Хорспула:** O(256) - для таблицы сдвигов

## Особенности

- **Простота:** Наивный алгоритм самый простой
- **Эффективность:** KMP наиболее эффективен в худшем случае
- **Практичность:** Бойера-Мура-Хорспула часто быстрее на практике

## Применение

Алгоритмы поиска строк используются в:

- Текстовых редакторах
- Поисковых системах
- Компиляторах
- Биоинформатике
- Обработке данных

## Когда использовать

### Используйте наивный алгоритм, когда:

- Тексты короткие
- Нужна простота
- Производительность не критична

### Используйте Рабина-Карпа, когда:

- Нужна средняя производительность
- Работаете с несколькими шаблонами
- Хеширование подходит

### Используйте KMP, когда:

- Нужна гарантированная производительность
- Шаблоны имеют повторяющиеся паттерны
- Важна худшая производительность

### Используйте Бойера-Мура-Хорспула, когда:

- Нужна практическая производительность
- Шаблоны длинные
- Алфавит ограничен

## Заключение

В этой статье мы представили несколько алгоритмов текстового поиска. Поскольку для некоторых алгоритмов требуется более сильная математическая подготовка, мы попытались представить основную идею каждого алгоритма и изложить ее в простой форме.
