# Multiple Keywords Check

Кратко: обнаружение нескольких слов внутри строки. Рассматриваются различные подходы: String.contains(), String.indexOf(), регулярные выражения, Stream API и алгоритм Ахо-Корасика.

**Дата последнего обновления:** 2025-01-15

## Полезные ссылки

### Официальная документация
- [GeeksforGeeks: Check if a string contains multiple keywords](https://www.geeksforgeeks.org/check-if-a-string-contains-multiple-keywords/)

### См. также
- `./regex-token-replacement.md` - работа с регулярными выражениями
- `./word-count.md` - подсчет слов

## Содержание

- [Описание алгоритма](#описание-алгоритма)
- [Java Implementation](#java-implementation)
- [Kotlin Implementation](#kotlin-implementation)
- [Сравнение подходов](#сравнение-подходов)
- [Сложность](#сложность)

## Описание алгоритма

В этом кратком руководстве мы узнаем, как обнаружить несколько слов внутри строки.

Предположим, у нас есть строка:

```java
String inputString = "hello there, Baeldung";
```

Наша задача - найти, содержит ли inputString слова «hello» и «Baeldung».

Итак, давайте поместим наши ключевые слова в массив:

```java
String[] words = {"hello", "Baeldung"};
```

Кроме того, порядок слов не важен, и совпадения должны быть чувствительны к регистру.

## Java Implementation

### Подход 1: String.contains()

Для начала мы покажем, как использовать метод `String.contains()` для достижения нашей цели.

Давайте перейдем к массиву ключевых слов и проверим наличие каждого элемента внутри inputString:

```java
public static boolean containsWords(String inputString, String[] items) {
    boolean found = true;
    
    for (String item : items) {
        if (!inputString.contains(item)) {
            found = false;
            break;
        }
    }
    
    return found;
}
```

Метод `contains()` вернет true, если inputString содержит данный элемент. Когда у нас нет ни одного из ключевых слов внутри нашей строки, мы можем прекратить движение вперед и немедленно вернуть false.

Несмотря на то, что нам нужно написать больше кода, это решение работает быстро для простых случаев использования.

### Улучшенная версия

```java
public static boolean containsWordsImproved(String inputString, String[] items) {
    for (String item : items) {
        if (!inputString.contains(item)) {
            return false;
        }
    }
    return true;
}
```

## Подход 2: String.indexOf()

Подобно решению, использующему метод `String.contains()`, мы можем проверить индексы ключевых слов с помощью метода `String.indexOf()`. Для этого нам нужен метод, принимающий inputString и список ключевых слов:

```java
public static boolean containsWordsIndexOf(String inputString, String[] words) {
    boolean found = true;
    
    for (String word : words) {
        if (inputString.indexOf(word) == -1) {
            found = false;
            break;
        }
    }
    
    return found;
}
```

Метод `indexOf()` возвращает индекс слова внутри inputString. Когда у нас нет слова в тексте, индекс будет равен -1.

### Альтернативная версия

```java
public static boolean containsWordsIndexOfImproved(String inputString, String[] words) {
    for (String word : words) {
        if (inputString.indexOf(word) == -1) {
            return false;
        }
    }
    return true;
}
```

## Подход 3: Регулярные выражения

Теперь давайте используем регулярное выражение для сопоставления наших слов. Для этого мы будем использовать класс Pattern.

Во-первых, давайте определим строковое выражение. Поскольку нам нужно сопоставить два ключевых слова, мы построим наше правило регулярного выражения с двумя прогнозами:

```java
Pattern pattern = Pattern.compile("(?=.*hello)(?=.*Baeldung)");
```

И для общего случая:

```java
public static boolean containsWordsPatternMatch(String inputString, String[] words) {
    StringBuilder regexp = new StringBuilder();
    
    for (String word : words) {
        regexp.append("(?=.*").append(word).append(")");
    }
    
    Pattern pattern = Pattern.compile(regexp.toString());
    return pattern.matcher(inputString).find();
}
```

После этого мы будем использовать метод `matcher()` для поиска вхождений.

Но у регулярных выражений есть издержки производительности. Если у нас есть несколько слов для поиска, производительность этого решения может быть не оптимальной.

### Экранирование специальных символов

```java
public static boolean containsWordsPatternMatchEscaped(String inputString, String[] words) {
    StringBuilder regexp = new StringBuilder();
    
    for (String word : words) {
        regexp.append("(?=.*").append(Pattern.quote(word)).append(")");
    }
    
    Pattern pattern = Pattern.compile(regexp.toString());
    return pattern.matcher(inputString).find();
}
```

## Подход 4: Stream API

И, наконец, мы можем использовать Stream API Java 8. Но сначала давайте сделаем небольшие преобразования с нашими исходными данными:

```java
List<String> inputStringList = Arrays.asList(inputString.split(" "));
List<String> wordsList = Arrays.asList(words);
```

Теперь пришло время использовать Stream API:

```java
public static boolean containsWordsJava8(String inputString, String[] words) {
    List<String> inputStringList = Arrays.asList(inputString.split(" "));
    List<String> wordsList = Arrays.asList(words);
    
    return wordsList.stream().allMatch(inputStringList::contains);
}
```

Приведенный выше конвейер операций вернет true, если входная строка содержит все наши ключевые слова.

В качестве альтернативы мы можем просто использовать метод `containsAll()` фреймворка Collections для достижения желаемого результата:

```java
public static boolean containsWordsArray(String inputString, String[] words) {
    List<String> inputStringList = Arrays.asList(inputString.split(" "));
    List<String> wordsList = Arrays.asList(words);
    
    return inputStringList.containsAll(wordsList);
}
```

Однако этот метод работает только для целых слов. Таким образом, он найдет наши ключевые слова, только если они разделены пробелом в тексте.

### Улучшенная версия Stream API

```java
public static boolean containsWordsStreamImproved(String inputString, String[] words) {
    return Arrays.stream(words)
        .allMatch(inputString::contains);
}
```

## Подход 5: Алгоритм Ахо-Корасика

Проще говоря, алгоритм Ахо-Корасика предназначен для поиска текста по нескольким ключевым словам. Он имеет временную сложность O(n) независимо от того, сколько ключевых слов мы ищем или какова длина текста.

Давайте включим зависимость алгоритма Ахо-Корасика в наш pom.xml:

```xml
<dependency>
    <groupId>org.ahocorasick</groupId>
    <artifactId>ahocorasick</artifactId>
    <version>0.4.0</version>
</dependency>
```

Во-первых, давайте создадим конвейер trie с массивом ключевых слов words. Для этого мы будем использовать структуру данных Trie:

```java
Trie trie = Trie.builder().onlyWholeWords().addKeywords(words).build();
```

После этого вызовем метод парсера с текстом inputString, в котором мы хотели бы найти ключевые слова, и сохраним результаты в коллекции emits:

```java
Collection<Emit> emits = trie.parseText(inputString);
```

И, наконец, если мы напечатаем наши результаты:

```java
emits.forEach(System.out::println);
```

Для каждого ключевого слова мы увидим начальную позицию ключевого слова в тексте, конечную позицию и само ключевое слово:

```
0: 4=hello
13: 20=Baeldung
```

Наконец, давайте посмотрим на полную реализацию:

```java
public static boolean containsWordsAhoCorasick(String inputString, String[] words) {
    Trie trie = Trie.builder().onlyWholeWords().addKeywords(words).build();
    Collection<Emit> emits = trie.parseText(inputString);
    
    emits.forEach(System.out::println);
    
    boolean found = true;
    for (String word : words) {
        boolean contains = Arrays.toString(emits.toArray()).contains(word);
        if (!contains) {
            found = false;
            break;
        }
    }
    
    return found;
}
```

В этом примере мы ищем только целые слова. Итак, если мы хотим сопоставить не только inputString, но и «helloBaeldung», мы должны просто удалить атрибут `onlyWholeWords()` из конвейера построителя Trie.

Кроме того, имейте в виду, что мы также удаляем повторяющиеся элементы из коллекции emits, поскольку для одного и того же ключевого слова может быть несколько совпадений.

### Улучшенная версия

```java
public static boolean containsWordsAhoCorasickImproved(String inputString, String[] words) {
    Trie trie = Trie.builder().addKeywords(words).build();
    Collection<Emit> emits = trie.parseText(inputString);
    
    Set<String> foundWords = emits.stream()
        .map(Emit::getKeyword)
        .collect(Collectors.toSet());
    
    return foundWords.size() == words.length;
}
```

## Kotlin Implementation

### Подход 1: String.contains()

```kotlin
fun containsWordsK(inputString: String, items: Array<String>): Boolean {
    for (item in items) {
        if (item !in inputString) {
            return false
        }
    }
    return true
}
```

### Подход 2: String.indexOf()

```kotlin
fun containsWordsIndexOfK(inputString: String, words: Array<String>): Boolean {
    for (word in words) {
        if (inputString.indexOf(word) == -1) {
            return false
        }
    }
    return true
}
```

### Подход 3: Регулярные выражения

```kotlin
fun containsWordsRegexK(inputString: String, words: Array<String>): Boolean {
    val pattern = words.joinToString("", "(", ")") { "(?=.*$it)" }
    return pattern.toRegex().containsMatchIn(inputString)
}
```

### Подход 4: Функциональный стиль

```kotlin
fun containsWordsFunctionalK(inputString: String, words: Array<String>): Boolean {
    return words.all { it in inputString }
}

// С учетом регистра
fun containsWordsCaseInsensitiveK(inputString: String, words: Array<String>): Boolean {
    val lowerInput = inputString.lowercase()
    return words.all { it.lowercase() in lowerInput }
}
```

### Подсчет вхождений

```kotlin
fun countWordOccurrencesK(inputString: String, words: Array<String>): Map<String, Int> {
    return words.associateWith { word ->
        var count = 0
        var index = 0
        while (true) {
            index = inputString.indexOf(word, index)
            if (index == -1) break
            count++
            index += word.length
        }
        count
    }
}
```

### Поиск позиций

```kotlin
fun findWordPositionsK(inputString: String, words: Array<String>): Map<String, List<Int>> {
    return words.associateWith { word ->
        val positions = mutableListOf<Int>()
        var index = 0
        while (true) {
            index = inputString.indexOf(word, index)
            if (index == -1) break
            positions.add(index)
            index += word.length
        }
        positions
    }
}
```

### Пример использования

```kotlin
fun main() {
    val inputString = "hello there, Baeldung"
    val words = arrayOf("hello", "Baeldung")
    
    println(containsWordsK(inputString, words)) // true
    println(containsWordsFunctionalK(inputString, words)) // true
    
    val counts = countWordOccurrencesK("hello hello world", words)
    println(counts) // {hello=2, Baeldung=0}
}
```

## Сравнение подходов

| Подход | Временная сложность | Пространственная сложность | Когда использовать |
|--------|---------------------|----------------------------|-------------------|
| String.contains() | O(n*m) | O(1) | Небольшое количество слов |
| String.indexOf() | O(n*m) | O(1) | Небольшое количество слов |
| Регулярные выражения | O(n*m) | O(m) | Сложные паттерны |
| Stream API | O(n*m) | O(k) | Функциональный стиль |
| Ахо-Корасик | O(n) | O(m) | Много ключевых слов |

Где n - длина текста, m - длина ключевого слова, k - количество ключевых слов.

## Сложность

### Временная сложность

- **String.contains()/indexOf():** O(n*m*k), где n - длина текста, m - средняя длина слова, k - количество слов
- **Регулярные выражения:** O(n*m*k) - может быть медленнее из-за накладных расходов
- **Stream API:** O(n*m*k) - аналогично contains()
- **Ахо-Корасик:** O(n + m*k) - эффективен для множества ключевых слов

### Пространственная сложность

- **String.contains()/indexOf():** O(1) - только константная память
- **Регулярные выражения:** O(m) - для хранения паттерна
- **Stream API:** O(k) - для списка слов
- **Ахо-Корасик:** O(m*k) - для построения trie

## Особенности

- **Простота:** String.contains() самый простой
- **Эффективность:** Ахо-Корасик наиболее эффективен для множества слов
- **Гибкость:** Регулярные выражения наиболее гибкие

## Применение

Проверка нескольких ключевых слов используется в:

- Поиске в тексте
- Фильтрации контента
- Анализе документов
- Мониторинге
- NLP (обработка естественного языка)

## Варианты задачи

### Вариант 1: Проверка с учетом регистра

```java
public static boolean containsWordsCaseInsensitive(String inputString, String[] words) {
    String lowerInput = inputString.toLowerCase();
    return Arrays.stream(words)
        .map(String::toLowerCase)
        .allMatch(lowerInput::contains);
}
```

### Вариант 2: Подсчет вхождений

```java
public static Map<String, Integer> countWordOccurrences(String inputString, String[] words) {
    Map<String, Integer> counts = new HashMap<>();
    
    for (String word : words) {
        int count = 0;
        int index = 0;
        while ((index = inputString.indexOf(word, index)) != -1) {
            count++;
            index += word.length();
        }
        counts.put(word, count);
    }
    
    return counts;
}
```

### Вариант 3: Поиск позиций

```java
public static Map<String, List<Integer>> findWordPositions(String inputString, String[] words) {
    Map<String, List<Integer>> positions = new HashMap<>();
    
    for (String word : words) {
        List<Integer> wordPositions = new ArrayList<>();
        int index = 0;
        while ((index = inputString.indexOf(word, index)) != -1) {
            wordPositions.add(index);
            index += word.length();
        }
        positions.put(word, wordPositions);
    }
    
    return positions;
}
```

## Когда использовать

### Используйте String.contains(), когда:

- Небольшое количество ключевых слов
- Нужна простота
- Производительность не критична

### Используйте регулярные выражения, когда:

- Сложные паттерны
- Нужна гибкость
- Работаете с паттернами

### Используйте Ахо-Корасик, когда:

- Много ключевых слов
- Нужна максимальная производительность
- Частые поиски

## Заключение

В этом кратком руководстве мы рассмотрели различные подходы к обнаружению нескольких слов внутри строки. Выбор подхода зависит от конкретных требований: количества ключевых слов, размера текста и требований к производительности.
