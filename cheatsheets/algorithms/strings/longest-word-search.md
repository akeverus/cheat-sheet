---
title: "Поиск самого длинного слова (Longest Word Search)"
description: "Поиск одного или всех самых длинных слов в предложении. Рассматриваются подходы с использованием Stream API и Collections для нахождения слов максимальной длины. Каждый подход имеет свои преимущества и применяется в зависимости от требований приложения, необходимости производител"
tags:
  - algorithms
  - strings
  - longest-word-search
difficulty: "intermediate"
prerequisites: []
next: []
updated: "2026-04-20"
---
# Поиск самого длинного слова (Longest Word Search)

Поиск одного или всех самых длинных слов в предложении. Рассматриваются подходы с использованием `Stream API` и `Collections` для нахождения слов максимальной длины. Каждый подход имеет свои преимущества и применяется в зависимости от требований приложения, необходимости производительности и стиля программирования.

## Полезные ссылки

### Официальная документация
- [Find the longest word in a sentence — GeeksforGeeks](https://www.geeksforgeeks.org/find-longest-word-given-string/)

### См. также
- [Подсчет слов](word-count.md) — подсчёт слов
- [Поиск первого неповторяющегося символа](first-non-repeating-character.md) — первый неповторяющийся символ

- [Конвертация римских и арабских чисел (Roman-Arabic Numeral Conversion)](roman-arabic-numeral-conversion.md)
- [Баланс скобок (Balanced Parentheses)](balanced-parentheses.md)
- [Поиск по суффиксному дереву (Suffix Tree Pattern Matching)](suffix-tree-pattern-matching.md)
## Содержание

- [Описание алгоритма](#описание-алгоритма)
  - [Примеры](#примеры)
- [Реализация на Java](#реализация-на-java)
  - [Поиск одного самого длинного слова (Java)](#поиск-одного-самого-длинного-слова-java)
  - [Альтернативная реализация с Stream (Java)](#альтернативная-реализация-с-stream-java)
  - [Реализация с ручной обработкой](#реализация-с-ручной-обработкой)
- [Поиск всех самых длинных слов](#поиск-всех-самых-длинных-слов)
  - [Альтернативная реализация](#альтернативная-реализация)
  - [Реализация с одним проходом](#реализация-с-одним-проходом)
- [Реализация на Kotlin](#реализация-на-kotlin)
  - [Поиск одного самого длинного слова (Kotlin)](#поиск-одного-самого-длинного-слова-kotlin)
  - [Альтернативная реализация с Stream (Kotlin)](#альтернативная-реализация-с-stream-kotlin)
  - [Ручная обработка](#ручная-обработка)
  - [Поиск всех самых длинных слов](#поиск-всех-самых-длинных-слов-1)
  - [Реализация с одним проходом](#реализация-с-одним-проходом-1)
  - [Функциональный стиль](#функциональный-стиль)
  - [Поиск с учетом знаков препинания](#поиск-с-учетом-знаков-препинания)
  - [Поиск слов длиннее определенного значения](#поиск-слов-длиннее-определенного-значения)
  - [Пример использования](#пример-использования)
- [Альтернативные подходы](#альтернативные-подходы)
  - [Поиск с учетом знаков препинания](#поиск-с-учетом-знаков-препинания-1)
  - [Поиск с минимальной длиной](#поиск-с-минимальной-длиной)
  - [Поиск с индексами](#поиск-с-индексами)
- [Сложность](#сложность)
  - [Временная сложность](#временная-сложность)
  - [Пространственная сложность](#пространственная-сложность)
- [Особенности](#особенности)
- [Применение](#применение)
- [Варианты задачи](#варианты-задачи)
  - [Вариант 1: Самое длинное слово без повторяющихся символов](#вариант-1-самое-длинное-слово-без-повторяющихся-символов)
  - [Вариант 2: Самое длинное слово с определенными символами](#вариант-2-самое-длинное-слово-с-определенными-символами)
- [Когда использовать](#когда-использовать)
- [Лучшие практики](#лучшие-практики)
- [Решение проблем](#решение-проблем)
- [Частые вопросы](#частые-вопросы)
- [Глоссарий терминов](#глоссарий-терминов)
- [Дополнительные примеры использования](#дополнительные-примеры-использования)
- [Заключение](#заключение)

## Описание алгоритма

В этом уроке мы будем искать одно или все самые длинные слова в предложении. Предложение — это набор слов. Мы представим его с помощью `Java String`. Кроме того, мы предполагаем, что каждый непробельный символ является частью слова. Наконец, мы подчеркнём технические пограничные случаи: нулевая, пустая или пустая строка не имеет самого длинного слова.

### Примеры

Примеры показывают различные случаи поиска самых длинных слов. Для предложения `"This is a phrase with words"` самое длинное слово — `"phrase"`. Для предложения `"Baeldung is another word of size eight in this sentence"` самые длинные слова — `"Baeldung"` и `"sentence"`, так как оба имеют одинаковую максимальную длину.

## Реализация на Java

### Поиск одного самого длинного слова (Java)

Сначала найдём самое длинное слово предложения. Например, в предложении: «Это фраза со словами» самое длинное слово — фраза. Если разные слова имеют одинаковую длину, любое из них является приемлемым ответом. Если в предложении нет слова, нет и результата. Следовательно, наш метод возвращает `Optional`. Ниже — поиск одного самого длинного слова через `Optional` и `Collections.max` (`Java`).
```java
// Поиск одного самого длинного слова через Optional и Collections.max
public Optional<String> findLongestWord(String sentence) {
    return Optional.ofNullable(sentence)
        .filter(string -> !string.trim().isEmpty())
        .map(string -> string.split("\\s"))
        .map(Arrays::asList)
        .map(list -> Collections.max(list, Comparator.comparingInt(String::length)));
}
```

Мы начали с того, что обернули наше предложение в `Optional` и отфильтровали все пустые и пустые `String`. Затем мы применили метод `split()` класса `String` для извлечения массива слов. Нам нужно было передать `\\s` в качестве параметра, чтобы использовать пробелы в качестве разделителей. Затем мы преобразовали наш массив в список благодаря `Arrays.asList()`. И последнее, но не менее важное: мы использовали `Collections.max()`, чтобы получить слово максимальной длины. Этот метод имеет два атрибута: список, максимум которого определяется, и компаратор для определения максимального. В нашем случае мы сравниваем слова по их длине. Мы назвали наш класс `LongestWordFinder`, поэтому теперь мы можем выполнить модульное тестирование нашего примерного предложения.

```java
@Test
void givenAPhraseWithALongestWord_whenFindLongestWord_thenLongestWordOfThePhrase() {
    assertThat(new LongestWordFinder().findLongestWord("This is a phrase with words"))
        .hasValue("phrase");
}
```

### Альтернативная реализация с Stream (Java)

Альтернативная реализация с `Stream` использует более функциональный подход и может быть более читаемой для разработчиков, знакомых с `Stream API`. Этот подход также эффективен и обеспечивает хорошую производительность.

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

Теперь мы перечислим все самые длинные слова. Например, `Baeldung` и предложение — это два самых длинных слова в предложении: «Baeldung — ещё одно слово восьмого размера в этом предложении». Для начала мы избавимся от пограничных случаев без слов и в таких случаях вернём пустой список. Кроме того, мы ещё раз разобьём предложение на массив слов. Однако на этот раз наша цель будет заключаться в том, чтобы сначала вычислить максимальную длину и использовать её для поиска всех слов, имеющих эту длину.

Как мы видим, чтобы вычислить максимальную длину, мы сначала создали поток из нашего массива слов. Затем мы применили промежуточную операцию `mapToInt()` с параметром `String::length`. Таким образом, мы преобразовали наш поток в поток длин слов. Наконец, мы получили максимальное значение `Stream`. В заключение, всё, что нам нужно было сделать, это отфильтровать слова, имеющие эту максимальную длину. Мы использовали другой поток, чтобы сделать это и собрать совпадающие слова в список результатов. Теперь давайте проверим, что `findLongestWords()` возвращает ожидаемый результат для нашего примерного предложения.

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

```java
@Test
void givenAPhraseWithVariousWordsOfMaxLength_whenFindLongestWords_thenAllLongestsWords() {
    assertThat(new LongestWordFinder().findLongestWords(
        "Baeldung is another word of size eight in this sentence"))
        .containsExactly("Baeldung", "sentence");
}
```

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

## Реализация на Kotlin

### Поиск одного самого длинного слова (Kotlin)

В `Kotlin` поиск самого длинного слова может быть реализован различными способами, включая функциональный стиль и ручную обработку. Каждый подход имеет свои преимущества и применяется в зависимости от стиля программирования и требований приложения.

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

### Альтернативная реализация с Stream (Kotlin)

Альтернативная реализация с `Stream` в `Kotlin` использует встроенные функции для более функционального подхода. Этот подход более идиоматичен для `Kotlin` и обеспечивает хорошую читаемость кода.

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

Временная сложность алгоритма зависит от количества слов в предложении. Поиск одного слова имеет временную сложность `O(n)`, где `n` — количество слов, так как требуется один проход по массиву слов для нахождения максимального. Поиск всех слов имеет временную сложность `O(n)` — два прохода по массиву слов: один для нахождения максимальной длины и один для фильтрации слов с этой длиной. Однако реализация с одним проходом также имеет временную сложность `O(n)`, что делает её более эффективной.

### Пространственная сложность

Пространственная сложность также зависит от количества слов и результатов. Поиск одного слова требует только константной памяти `O(1)`, так как не требуется хранение промежуточных результатов. Поиск всех слов требует `O(k)` памяти, где `k` — количество самых длинных слов, так как необходимо хранить все слова максимальной длины.

## Особенности

`Stream API` делает код читаемым и выразительным, что упрощает понимание и поддержку кода. Эффективность обеспечивается одним или двумя проходами по массиву слов, что делает алгоритм быстрым для большинства практических применений. Гибкость позволяет легко адаптировать алгоритм для различных требований, таких как поиск слов с определёнными характеристиками или фильтрация по дополнительным критериям.

## Применение

Поиск самых длинных слов используется в различных областях. В обработке текста поиск самых длинных слов используется для анализа текстовых данных и определения характеристик текста. В анализе документов поиск самых длинных слов используется для категоризации и индексации документов. В статистике текста поиск самых длинных слов используется для вычисления различных метрик и характеристик текста. В `NLP` (обработка естественного языка) поиск самых длинных слов используется для анализа текста и извлечения информации. В валидации данных поиск самых длинных слов используется для проверки корректности входных данных и обеспечения соответствия определённым требованиям.

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

Используйте `Stream API`, когда нужна читаемость и работаете с `Java` 8+. Этот подход подходит, когда нужна функциональная парадигма и важнее выразительность кода, чем максимальная производительность. `Stream API` обеспечивает элегантный и читаемый способ решения задачи поиска самых длинных слов.

Используйте ручную обработку, когда нужна максимальная производительность и работаете с очень большими текстами. Этот подход подходит, когда важна экономия памяти и требуется оптимальная производительность. Ручная обработка позволяет минимизировать создание временных объектов и оптимизировать использование памяти.

## Лучшие практики

Разделители требуют правильного выбора в зависимости от формата входных данных. Используйте `split("\\s")` для пробелов или `split("\\s+")` для серий пробелов; при знаках препинания — `split("[\\p{Punct}\\s]+")` и фильтрацию пустых строк. Это гарантирует корректное разбиение предложения на слова независимо от формата входных данных.

Граничные случаи требуют тщательной обработки. Проверяйте `null`, пустую и состоящую только из пробелов строку; для пустого результата возвращайте `Optional.empty()` или `Collections.emptyList()`. Это гарантирует корректное поведение алгоритма во всех возможных случаях.

Один проход более эффективен для поиска всех самых длинных слов. Для поиска всех самых длинных слов один проход с обновлением максимума и списка эффективнее двух проходов (сначала max, потом filter). Это позволяет оптимизировать производительность и снизить количество операций.

Читаемость важна при выборе подхода. `Stream API` с `Comparator.comparingInt(String::length)` и `max()`/`maxByOrNull` упрощает код; для очень больших текстов рассмотрите ручной цикл. Это позволяет выбрать оптимальное решение в зависимости от требований к читаемости и производительности.

Тестирование должно покрывать все граничные случаи. Проверьте одно слово, несколько слов одной максимальной длины, пустое предложение, только пробелы, слова с знаками препинания. Это гарантирует корректную работу алгоритма во всех возможных ситуациях.

## Решение проблем

При работе с поиском самых длинных слов могут возникать различные проблемы. Если алгоритм не находит самое длинное слово в валидном предложении, убедитесь, что вы правильно обрабатываете разделители и фильтруете пустые строки. Если возникают проблемы с производительностью, рассмотрите возможность использования реализации с одним проходом вместо двух проходов.

Проблемы с памятью могут возникать при работе с очень большими текстами. В таких случаях рекомендуется использовать ручную обработку вместо `Stream API`, так как она создаёт меньше временных объектов. Также убедитесь, что вы правильно обрабатываете граничные случаи, такие как пустые предложения и предложения, состоящие только из пробелов.

Проблемы с разделителями могут возникать при работе с различными форматами текста. Убедитесь, что вы используете правильные регулярные выражения для разбиения предложения на слова, что гарантирует корректную работу алгоритма с различными форматами входных данных.

## Частые вопросы

**Какой подход лучше использовать для поиска самых длинных слов?** Для большинства случаев используйте `Stream API`, так как он обеспечивает хорошую читаемость и приемлемую производительность. Для очень больших текстов используйте ручную обработку или реализацию с одним проходом.

**Как обрабатывать знаки препинания?** Используйте регулярное выражение `"[\\p{Punct}\\s]+"` для разбиения предложения и фильтруйте пустые строки. Это гарантирует корректную обработку слов с знаками препинания.

**Как оптимизировать производительность?** Используйте реализацию с одним проходом для поиска всех самых длинных слов. Это позволяет избежать двух проходов по массиву и улучшить производительность.

**Как обрабатывать пустые предложения?** Явно проверяйте `null` и пустые строки, возвращая `Optional.empty()` или `Collections.emptyList()`. Это гарантирует корректное поведение алгоритма во всех возможных случаях.

## Глоссарий терминов

**Самое длинное слово** — слово в предложении, имеющее максимальную длину среди всех слов. В предложении может быть несколько слов одинаковой максимальной длины.

`Optional` — контейнер в `Java`, который может содержать значение или быть пустым. В контексте поиска самого длинного слова `Optional` используется для представления результата, который может отсутствовать.

`Stream API` — набор классов и интерфейсов в `Java` 8+ для работы с потоками данных. В контексте поиска самых длинных слов `Stream API` используется для функциональной обработки массива слов.

**Разделитель** — символ или последовательность символов, используемая для разделения текста на слова. В контексте поиска самых длинных слов разделителями обычно являются пробелы и знаки препинания.

**Компаратор** — объект, используемый для сравнения элементов. В контексте поиска самых длинных слов компаратор используется для сравнения слов по их длине.

## Дополнительные примеры использования

Поиск самых длинных слов используется в различных областях программирования. В системах обработки текста поиск самых длинных слов используется для анализа текстовых данных и определения характеристик текста, что может быть полезно для лингвистических исследований. В системах анализа документов поиск самых длинных слов используется для категоризации и индексации документов, что позволяет автоматически организовывать большие объёмы документов.

В системах статистики текста поиск самых длинных слов используется для вычисления различных метрик и характеристик текста, что помогает понять структуру и особенности текста. В системах `NLP` поиск самых длинных слов используется для анализа текста и извлечения информации, что критически важно для понимания смысла текста.

## Заключение

В этой статье мы разбили предложение на список слов и использовали `API` коллекций, чтобы найти одно из самых длинных слов. Мы также увидели, как использовать `Java Streams`, чтобы найти их все. Понимание особенностей различных подходов и их применения критически важно для выбора оптимального решения для конкретной задачи поиска самых длинных слов. Правильный выбор подхода может значительно улучшить производительность и эффективность приложения, работающего с текстовыми данными.
