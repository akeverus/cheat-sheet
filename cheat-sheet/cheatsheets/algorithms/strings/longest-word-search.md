# Longest Word Search

Кратко: поиск одного или всех самых длинных слов в предложении. Рассматриваются подходы с использованием Stream API и Collections для нахождения слов максимальной длины.

**Дата последнего обновления:** 2025-01-15

## Полезные ссылки

### Официальная документация
- [GeeksforGeeks: Find the longest word in a sentence](https://www.geeksforgeeks.org/find-the-longest-word-in-a-sentence/)

### См. также
- `./word-count.md` - подсчет слов
- `./first-non-repeating-character.md` - поиск первого неповторяющегося символа

## Содержание

- [Описание алгоритма](#описание-алгоритма)
- [Java Implementation](#java-implementation)
- [Kotlin Implementation](#kotlin-implementation)
- [Альтернативные подходы](#альтернативные-подходы)
- [Сложность](#сложность)

## Описание алгоритма

В этом уроке мы будем искать одно или все самые длинные слова в предложении.

Предложение - это набор слов. Мы представим его с помощью Java String. Кроме того, мы предполагаем, что каждый непробельный символ является частью слова. Наконец, мы подчеркнем технические пограничные случаи: нулевая, пустая или пустая строка не имеет самого длинного слова.

### Примеры

- "This is a phrase with words" → самое длинное слово: "phrase"
- "Baeldung is another word of size eight in this sentence" → самые длинные слова: "Baeldung", "sentence"

## Java Implementation

### Поиск одного самого длинного слова

Сначала найдем самое длинное слово предложения. Например, в предложении: «Это фраза со словами» самое длинное слово - фраза. Если разные слова имеют одинаковую длину, любое из них является приемлемым ответом. Если в предложении нет слова, нет и результата. Следовательно, наш метод возвращает Optional:

```java
public Optional<String> findLongestWord(String sentence) {
    return Optional.ofNullable(sentence)
        .filter(string -> !string.trim().isEmpty())
        .map(string -> string.split("\\s"))
        .map(Arrays::asList)
        .map(list -> Collections.max(list, Comparator.comparingInt(String::length)));
}
```

Мы начали с того, что обернули наше предложение в Optional и отфильтровали все пустые и пустые String s. Затем мы применили метод `split()` класса String для извлечения массива слов. Нам нужно было передать `\\s` в качестве параметра, чтобы использовать пробелы в качестве разделителей. Затем мы преобразовали наш массив в список благодаря `Arrays.asList()`. И последнее, но не менее важное: мы использовали `Collections.max()`, чтобы получить слово максимальной длины. Этот метод имеет два атрибута:

- список, максимум которого определяется
- Компаратор для определения максимального

В нашем случае мы сравниваем слова по их длине. Мы назвали наш класс LongestWordFinder, поэтому теперь мы можем выполнить модульное тестирование нашего примерного предложения:

```java
@Test
void givenAPhraseWithALongestWord_whenFindLongestWord_thenLongestWordOfThePhrase() {
    assertThat(new LongestWordFinder().findLongestWord("This is a phrase with words"))
        .hasValue("phrase");
}
```

### Альтернативная реализация с Stream

```java
public Optional<String> findLongestWordWithStream(String sentence) {
    if (sentence == null || sentence.trim().isEmpty()) {
        return Optional.empty();
    }
    
    return Arrays.stream(sentence.split("\\s"))
        .max(Comparator.comparingInt(String::length));
}
```

### Реализация с ручной обработкой

```java
public Optional<String> findLongestWordManual(String sentence) {
    if (sentence == null || sentence.trim().isEmpty()) {
        return Optional.empty();
    }
    
    String[] words = sentence.split("\\s");
    String longestWord = words[0];
    
    for (String word : words) {
        if (word.length() > longestWord.length()) {
            longestWord = word;
        }
    }
    
    return Optional.of(longestWord);
}
```

## Поиск всех самых длинных слов

Теперь мы перечислим все самые длинные слова. Например, Baeldung и предложение - это два самых длинных слова в предложении: «Baeldung - еще одно слово восьмого размера в этом предложении».

Для начала мы избавимся от пограничных случаев без слов и в таких случаях вернем пустой список. Кроме того, мы еще раз разобьем предложение на массив слов. Однако на этот раз наша цель будет заключаться в том, чтобы сначала вычислить максимальную длину и использовать ее для поиска всех слов, имеющих эту длину:

```java
public List<String> findLongestWords(String sentence) {
    if (sentence == null || sentence.trim().isEmpty()) {
        return Collections.emptyList();
    }
    
    String[] words = sentence.split("\\s");
    
    int maxWordLength = Arrays.stream(words)
        .mapToInt(String::length)
        .max()
        .orElseThrow();
    
    return Arrays.stream(words)
        .filter(word -> word.length() == maxWordLength)
        .collect(Collectors.toList());
}
```

Как мы видим, чтобы вычислить максимальную длину, мы сначала создали поток из нашего массива слов. Затем мы применили промежуточную операцию `mapToInt()` с параметром `String::length`. Таким образом, мы преобразовали наш поток в поток длин слов. Наконец, мы получили максимальное значение Stream.

В заключение, все, что нам нужно было сделать, это отфильтровать слова, имеющие эту максимальную длину. Мы использовали другой поток, чтобы сделать это и собрать совпадающие слова в список результатов. Теперь давайте проверим, что `findLongestWords()` возвращает ожидаемый результат для нашего примерного предложения:

```java
@Test
void givenAPhraseWithVariousWordsOfMaxLength_whenFindLongestWords_thenAllLongestsWords() {
    assertThat(new LongestWordFinder().findLongestWords(
        "Baeldung is another word of size eight in this sentence"))
        .containsExactly("Baeldung", "sentence");
}
```

### Альтернативная реализация

```java
public List<String> findLongestWordsAlternative(String sentence) {
    if (sentence == null || sentence.trim().isEmpty()) {
        return Collections.emptyList();
    }
    
    String[] words = sentence.split("\\s");
    int maxLength = 0;
    
    for (String word : words) {
        if (word.length() > maxLength) {
            maxLength = word.length();
        }
    }
    
    List<String> longestWords = new ArrayList<>();
    for (String word : words) {
        if (word.length() == maxLength) {
            longestWords.add(word);
        }
    }
    
    return longestWords;
}
```

### Реализация с одним проходом

```java
public List<String> findLongestWordsSinglePass(String sentence) {
    if (sentence == null || sentence.trim().isEmpty()) {
        return Collections.emptyList();
    }
    
    String[] words = sentence.split("\\s");
    int maxLength = 0;
    List<String> longestWords = new ArrayList<>();
    
    for (String word : words) {
        if (word.length() > maxLength) {
            maxLength = word.length();
            longestWords.clear();
            longestWords.add(word);
        } else if (word.length() == maxLength) {
            longestWords.add(word);
        }
    }
    
    return longestWords;
}
```

## Kotlin Implementation

### Поиск одного самого длинного слова

```kotlin
fun findLongestWordK(sentence: String?): String? {
    if (sentence.isNullOrBlank()) {
        return null
    }
    
    return sentence.trim()
        .split("\\s+".toRegex())
        .maxByOrNull { it.length }
}
```

### Альтернативная реализация с Stream

```kotlin
fun findLongestWordStreamK(sentence: String?): String? {
    if (sentence.isNullOrBlank()) {
        return null
    }
    
    return sentence.split("\\s+".toRegex())
        .maxByOrNull { it.length }
}
```

### Ручная обработка

```kotlin
fun findLongestWordManualK(sentence: String?): String? {
    if (sentence.isNullOrBlank()) {
        return null
    }
    
    val words = sentence.trim().split("\\s+".toRegex())
    var longestWord = words[0]
    
    for (word in words) {
        if (word.length > longestWord.length) {
            longestWord = word
        }
    }
    
    return longestWord
}
```

### Поиск всех самых длинных слов

```kotlin
fun findLongestWordsK(sentence: String?): List<String> {
    if (sentence.isNullOrBlank()) {
        return emptyList()
    }
    
    val words = sentence.trim().split("\\s+".toRegex())
    val maxLength = words.maxOfOrNull { it.length } ?: return emptyList()
    
    return words.filter { it.length == maxLength }
}
```

### Реализация с одним проходом

```kotlin
fun findLongestWordsSinglePassK(sentence: String?): List<String> {
    if (sentence.isNullOrBlank()) {
        return emptyList()
    }
    
    val words = sentence.trim().split("\\s+".toRegex())
    var maxLength = 0
    val longestWords = mutableListOf<String>()
    
    for (word in words) {
        when {
            word.length > maxLength -> {
                maxLength = word.length
                longestWords.clear()
                longestWords.add(word)
            }
            word.length == maxLength -> longestWords.add(word)
        }
    }
    
    return longestWords
}
```

### Функциональный стиль

```kotlin
fun findLongestWordsFunctionalK(sentence: String?): List<String> {
    if (sentence.isNullOrBlank()) {
        return emptyList()
    }
    
    val words = sentence.trim().split("\\s+".toRegex())
    val maxLength = words.maxOfOrNull { it.length } ?: 0
    
    return words.filter { it.length == maxLength }
}
```

### Поиск с учетом знаков препинания

```kotlin
fun findLongestWordWithPunctuationK(sentence: String?): String? {
    if (sentence.isNullOrBlank()) {
        return null
    }
    
    val words = sentence.split("[\\p{Punct}\\s]+".toRegex())
    return words.filter { it.isNotEmpty() }
        .maxByOrNull { it.length }
}
```

### Поиск слов длиннее определенного значения

```kotlin
fun findWordsLongerThanK(sentence: String?, minLength: Int): List<String> {
    if (sentence.isNullOrBlank()) {
        return emptyList()
    }
    
    return sentence.split("\\s+".toRegex())
        .filter { it.length >= minLength }
}
```

### Пример использования

```kotlin
fun main() {
    val sentence1 = "This is a phrase with words"
    val sentence2 = "Baeldung is another word of size eight in this sentence"
    
    // Одно самое длинное слово
    println(findLongestWordK(sentence1)) // "phrase"
    
    // Все самые длинные слова
    println(findLongestWordsK(sentence2)) // ["Baeldung", "sentence"]
    
    // Функциональный стиль
    println(findLongestWordsFunctionalK(sentence2)) // ["Baeldung", "sentence"]
    
    // С одним проходом
    println(findLongestWordsSinglePassK(sentence2)) // ["Baeldung", "sentence"]
}
```

## Альтернативные подходы

### Поиск с учетом знаков препинания

```java
public Optional<String> findLongestWordWithPunctuation(String sentence) {
    if (sentence == null || sentence.trim().isEmpty()) {
        return Optional.empty();
    }
    
    String[] words = sentence.split("[\\p{Punct}\\s]+");
    return Arrays.stream(words)
        .filter(word -> !word.isEmpty())
        .max(Comparator.comparingInt(String::length));
}
```

### Поиск с минимальной длиной

```java
public List<String> findWordsLongerThan(String sentence, int minLength) {
    if (sentence == null || sentence.trim().isEmpty()) {
        return Collections.emptyList();
    }
    
    return Arrays.stream(sentence.split("\\s"))
        .filter(word -> word.length() >= minLength)
        .collect(Collectors.toList());
}
```

### Поиск с индексами

```java
public Map<String, Integer> findLongestWordsWithIndices(String sentence) {
    if (sentence == null || sentence.trim().isEmpty()) {
        return Collections.emptyMap();
    }
    
    String[] words = sentence.split("\\s");
    int maxLength = Arrays.stream(words)
        .mapToInt(String::length)
        .max()
        .orElse(0);
    
    Map<String, Integer> result = new LinkedHashMap<>();
    int index = 0;
    
    for (String word : words) {
        if (word.length() == maxLength) {
            result.put(word, index);
        }
        index += word.length() + 1; // +1 for space
    }
    
    return result;
}
```

## Сложность

### Временная сложность

- **Поиск одного слова:** O(n), где n - количество слов
- **Поиск всех слов:** O(n) - два прохода по массиву слов

### Пространственная сложность

- **Поиск одного слова:** O(1) - только константная память
- **Поиск всех слов:** O(k), где k - количество самых длинных слов

## Особенности

- **Простота:** Stream API делает код читаемым
- **Эффективность:** Один или два прохода по массиву
- **Гибкость:** Легко адаптировать для различных требований

## Применение

Поиск самых длинных слов используется в:

- Обработке текста
- Анализе документов
- Статистике текста
- NLP (обработка естественного языка)
- Валидации данных

## Варианты задачи

### Вариант 1: Самое длинное слово без повторяющихся символов

```java
public Optional<String> findLongestWordNoRepeats(String sentence) {
    if (sentence == null || sentence.trim().isEmpty()) {
        return Optional.empty();
    }
    
    return Arrays.stream(sentence.split("\\s"))
        .filter(word -> word.chars().distinct().count() == word.length())
        .max(Comparator.comparingInt(String::length));
}
```

### Вариант 2: Самое длинное слово с определенными символами

```java
public Optional<String> findLongestWordContaining(String sentence, char ch) {
    if (sentence == null || sentence.trim().isEmpty()) {
        return Optional.empty();
    }
    
    return Arrays.stream(sentence.split("\\s"))
        .filter(word -> word.indexOf(ch) != -1)
        .max(Comparator.comparingInt(String::length));
}
```

## Когда использовать

### Используйте Stream API, когда:

- Нужна читаемость
- Работаете с Java 8+
- Нужна функциональная парадигма

### Используйте ручную обработку, когда:

- Нужна максимальная производительность
- Работаете с очень большими текстами
- Важна экономия памяти

## Заключение

В этой статье мы разбили предложение на список слов и использовали API коллекций, чтобы найти одно из самых длинных слов. Мы также увидели, как использовать Java Streams, чтобы найти их все.
