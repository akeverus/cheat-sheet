# Word Count

Кратко: различные способы подсчета слов в заданной строке с использованием Java. Рассматриваются подходы с использованием StringTokenizer, регулярных выражений и ручной обработки символов.

**Дата последнего обновления:** 2025-01-15

## Полезные ссылки

### Официальная документация
- [Java StringTokenizer Documentation](https://docs.oracle.com/javase/8/docs/api/java/util/StringTokenizer.html)
- [Java Pattern Documentation](https://docs.oracle.com/javase/8/docs/api/java/util/regex/Pattern.html)

### См. также
- `./first-non-repeating-character.md` - поиск первого неповторяющегося символа
- `./regex-token-replacement.md` - работа с регулярными выражениями

## Содержание

- [Описание алгоритма](#описание-алгоритма)
- [Java Implementation](#java-implementation)
- [Kotlin Implementation](#kotlin-implementation)
- [Сравнение подходов](#сравнение-подходов)
- [Сложность](#сложность)

## Описание алгоритма

В этом уроке мы рассмотрим различные способы подсчета слов в заданной строке с использованием Java.

### Определение слова

Чтобы мы могли придумать осмысленное решение для этой задачи, нам нужно определить, что мы считаем словом: слово начинается с буквы и заканчивается либо пробелом, либо знаком препинания.

### Примеры

- "three blind mice" → 3 слова
- "see\thow\tthey\trun" → 4 слова
- "the farmer's wife--she was from Albuquerque" → 7 слов (с учетом дефисов)

## Java Implementation

### Подход 1: StringTokenizer

Простой способ подсчета слов в строке в Java - использовать класс StringTokenizer:

```java
assertEquals(3, new StringTokenizer("three blind mice").countTokens());
assertEquals(4, new StringTokenizer("see\thow\tthey\trun").countTokens());
```

Обратите внимание, что StringTokenizer автоматически позаботится о пробелах, таких как табуляция и возврат каретки.

### Проблемы с дефисами

Но в некоторых местах он может ошибаться, например, в дефисах:

```java
assertEquals(7, new StringTokenizer("the farmer's wife--she was from Albuquerque").countTokens());
```

В этом случае мы бы хотели, чтобы «жена» и «она» были разными словами, но поскольку между ними нет пробела, значения по умолчанию нас не устраивают.

### Использование разделителя

К счастью, StringTokenizer поставляется с другим конструктором. Мы можем передать разделитель в конструктор, чтобы сделать вышеописанное:

```java
assertEquals(7, new StringTokenizer("the farmer's wife--she was from Albuquerque", " -").countTokens());
```

Это удобно при попытке подсчитать количество слов в строке из чего-то вроде CSV-файла:

```java
assertEquals(10, new StringTokenizer("did,you,ever,see,such,a,sight,in,your,life", ",").countTokens());
```

Итак, StringTokenizer прост, и он помогает нам в этом.

### Особенности

- Простота использования
- Автоматическая обработка пробелов
- Возможность указать разделители

## Подход 2: Регулярные выражения

Давайте посмотрим, какую дополнительную мощность могут дать нам регулярные выражения.

### Реализация

Оказывается, нам действительно не нужно много делать, передача регулярного выражения `[\\pP\\s&&[^']]+` методу `split` класса String сделает свое дело:

```java
public static int countWordsUsingRegex(String arg) {
    if (arg == null) {
        return 0;
    }
    
    final String[] words = arg.split("[\\pP\\s&&[^']]+");
    return words.length;
}
```

Регулярное выражение `[\\pP\\s&&[^']]+` находит любую длину знаков препинания или пробелов и игнорирует знак препинания в виде апострофа.

### Примеры

```java
assertEquals(7, countWordsUsingRegex("the farmer's wife - - she was from Albuquerque"));
assertEquals(9, countWordsUsingRegex("no&one#should%ever-write-like,this;but:well"));
```

Нецелесообразно решать эту проблему, просто передавая разделитель в StringTokenizer, поскольку нам пришлось бы определить очень длинный разделитель, чтобы попытаться перечислить все возможные знаки препинания.

### Альтернативные регулярные выражения

```java
// Простое разделение по пробелам
public static int countWordsSimple(String arg) {
    if (arg == null || arg.trim().isEmpty()) {
        return 0;
    }
    return arg.trim().split("\\s+").length;
}

// Разделение по пробелам и знакам препинания
public static int countWordsWithPunctuation(String arg) {
    if (arg == null) {
        return 0;
    }
    return arg.split("[\\p{Punct}\\s]+").length;
}
```

## Подход 3: Ручная обработка

Другой метод состоит в том, чтобы иметь флаг, который отслеживает встречающиеся слова.

Мы устанавливаем флаг в СЛОВО при встрече с новым словом и увеличиваем количество слов, а затем возвращаемся к СЕПАРАТОРУ, когда встречаем не слово (знаки препинания или пробелы).

### Константы

```java
private static final int SEPARATOR = 0;
private static final int WORD = 1;
```

### Проверка допустимых символов

Этот подход дает нам те же результаты, что и с регулярными выражениями:

```java
assertEquals(9, countWordsManually("no&one#should%ever-write-like,this but well"));
```

Мы должны быть осторожны с особыми случаями, когда знаки препинания на самом деле не являются разделителями слов, например:

```java
assertEquals(6, countWordsManually("the farmer's wife--she was from Albuquerque"));
```

Здесь мы хотим посчитать «фермерский» как одно слово, хотя апостроф «'» является знаком препинания.

В версии с регулярным выражением у нас была возможность определять, что не считается символом, с помощью регулярного выражения. Но теперь, когда мы пишем собственную реализацию, мы должны определить это исключение в отдельном методе:

```java
private static boolean isAllowedInWord(char charAt) {
    return charAt == '\'' || Character.isLetter(charAt);
}
```

Итак, что мы сделали здесь, так это разрешили в слове все символы и допустимые знаки препинания, в данном случае апостроф.

### Реализация

Теперь мы можем использовать этот метод в нашей реализации:

```java
public static int countWordsManually(String arg) {
    if (arg == null) {
        return 0;
    }
    
    int flag = SEPARATOR;
    int count = 0;
    int stringLength = arg.length();
    int characterCounter = 0;
    
    while (characterCounter < stringLength) {
        if (isAllowedInWord(arg.charAt(characterCounter)) && flag == SEPARATOR) {
            flag = WORD;
            count++;
        } else if (!isAllowedInWord(arg.charAt(characterCounter))) {
            flag = SEPARATOR;
        }
        
        characterCounter++;
    }
    
    return count;
}
```

Первое условие помечает слово, когда оно встречается с ним, и увеличивает значение счетчика. Второе условие проверяет, не является ли символ буквой, и устанавливает флаг в SEPARATOR.

### Улучшенная версия

```java
public static int countWordsImproved(String arg) {
    if (arg == null || arg.trim().isEmpty()) {
        return 0;
    }
    
    int wordCount = 0;
    boolean inWord = false;
    
    for (char c : arg.toCharArray()) {
        if (Character.isLetter(c) || c == '\'') {
            if (!inWord) {
                wordCount++;
                inWord = true;
            }
        } else {
            inWord = false;
        }
    }
    
    return wordCount;
}
```

## Сравнение подходов

| Подход | Простота | Гибкость | Производительность | Когда использовать |
|--------|----------|----------|-------------------|-------------------|
| StringTokenizer | Высокая | Низкая | Средняя | Простые случаи |
| Регулярные выражения | Средняя | Высокая | Средняя | Сложные разделители |
| Ручная обработка | Средняя | Высокая | Высокая | Максимальный контроль |

## Сложность

### Временная сложность

- **StringTokenizer:** O(n), где n - длина строки
- **Регулярные выражения:** O(n) - один проход для split
- **Ручная обработка:** O(n) - один проход по строке

### Пространственная сложность

- **StringTokenizer:** O(n) - для хранения токенов
- **Регулярные выражения:** O(n) - для массива слов
- **Ручная обработка:** O(1) - только константная память

## Особенности

- **Простота:** StringTokenizer самый простой
- **Гибкость:** Регулярные выражения наиболее гибкие
- **Эффективность:** Ручная обработка наиболее эффективна по памяти

## Применение

Подсчет слов используется в:

- Обработке текста
- Анализе документов
- Статистике текста
- Поиске и индексации
- NLP (обработка естественного языка)

## Варианты задачи

### Вариант 1: Подсчет уникальных слов

```java
public static int countUniqueWords(String arg) {
    if (arg == null) {
        return 0;
    }
    
    String[] words = arg.toLowerCase().split("[\\pP\\s&&[^']]+");
    Set<String> uniqueWords = new HashSet<>(Arrays.asList(words));
    return uniqueWords.size();
}
```

### Вариант 2: Подсчет слов с частотой

```java
public static Map<String, Integer> countWordsWithFrequency(String arg) {
    if (arg == null) {
        return Collections.emptyMap();
    }
    
    String[] words = arg.toLowerCase().split("[\\pP\\s&&[^']]+");
    Map<String, Integer> frequency = new HashMap<>();
    
    for (String word : words) {
        if (!word.isEmpty()) {
            frequency.put(word, frequency.getOrDefault(word, 0) + 1);
        }
    }
    
    return frequency;
}
```

### Вариант 3: Подсчет слов по длине

```java
public static Map<Integer, Integer> countWordsByLength(String arg) {
    if (arg == null) {
        return Collections.emptyMap();
    }
    
    String[] words = arg.split("[\\pP\\s&&[^']]+");
    Map<Integer, Integer> lengthCount = new HashMap<>();
    
    for (String word : words) {
        if (!word.isEmpty()) {
            int length = word.length();
            lengthCount.put(length, lengthCount.getOrDefault(length, 0) + 1);
        }
    }
    
    return lengthCount;
}
```

## Kotlin Implementation

### Подход 1: Использование split

```kotlin
fun countWordsK(text: String?): Int {
    if (text.isNullOrBlank()) {
        return 0
    }
    return text.trim().split("\\s+".toRegex()).size
}
```

### Подход 2: Регулярные выражения

```kotlin
fun countWordsUsingRegexK(text: String?): Int {
    if (text == null) {
        return 0
    }
    val words = text.split("[\\pP\\s&&[^']]+".toRegex())
    return words.filter { it.isNotEmpty() }.size
}
```

### Подход 3: Ручная обработка

```kotlin
fun countWordsManualK(text: String?): Int {
    if (text.isNullOrBlank()) {
        return 0
    }
    
    var wordCount = 0
    var inWord = false
    
    for (char in text) {
        if (char.isLetter() || char == '\'') {
            if (!inWord) {
                wordCount++
                inWord = true
            }
        } else {
            inWord = false
        }
    }
    
    return wordCount
}
```

### Функциональный стиль

```kotlin
fun countWordsFunctionalK(text: String?): Int {
    return text?.trim()
        ?.split("\\s+".toRegex())
        ?.filter { it.isNotEmpty() }
        ?.size ?: 0
}
```

### Подсчет уникальных слов

```kotlin
fun countUniqueWordsK(text: String?): Int {
    if (text == null) {
        return 0
    }
    val words = text.lowercase().split("[\\pP\\s&&[^']]+".toRegex())
    return words.filter { it.isNotEmpty() }.toSet().size
}
```

### Подсчет слов с частотой

```kotlin
fun countWordsWithFrequencyK(text: String?): Map<String, Int> {
    if (text == null) {
        return emptyMap()
    }
    val words = text.lowercase().split("[\\pP\\s&&[^']]+".toRegex())
    return words.filter { it.isNotEmpty() }
        .groupingBy { it }
        .eachCount()
}
```

### Пример использования

```kotlin
fun main() {
    val text = "The quick brown fox jumps over the lazy dog"
    
    println(countWordsK(text)) // 9
    println(countWordsFunctionalK(text)) // 9
    println(countUniqueWordsK(text)) // 8 (the повторяется)
    println(countWordsWithFrequencyK(text))
    // {the=2, quick=1, brown=1, fox=1, jumps=1, over=1, lazy=1, dog=1}
}
```

## Когда использовать

### Используйте StringTokenizer, когда:

- Простые разделители (пробелы, табуляция)
- Нужна простота
- Производительность не критична

### Используйте регулярные выражения, когда:

- Сложные разделители
- Нужна гибкость
- Различные типы знаков препинания

### Используйте ручную обработку, когда:

- Максимальный контроль
- Важна производительность
- Специфические требования

## Заключение

В этом уроке мы рассмотрели способы подсчета слов с использованием нескольких подходов. Мы можем выбрать любой в зависимости от нашего конкретного варианта использования. StringTokenizer прост для базовых случаев, регулярные выражения гибки для сложных случаев, а ручная обработка дает максимальный контроль и производительность.
