# Pangram Check

Кратко: панграмма - это любая строка, содержащая все буквы заданного набора алфавитов хотя бы один раз. Рассматриваются различные подходы для проверки панграмм: с использованием массива, Stream API, и проверка идеальных панграмм.

**Дата последнего обновления:** 2025-01-15

## Полезные ссылки

### Официальная документация
- [GeeksforGeeks: Pangram Checking](https://www.geeksforgeeks.org/pangram-checking/)

### См. также
- `./caesar-cipher.md` - шифр Цезаря
- `./word-count.md` - подсчет слов

## Содержание

- [Описание алгоритма](#описание-алгоритма)
- [Java Implementation](#java-implementation)
- [Kotlin Implementation](#kotlin-implementation)
- [Идеальная панграмма](#идеальная-панграмма)
- [Сравнение подходов](#сравнение-подходов)
- [Сложность](#сложность)

## Описание алгоритма

В этом уроке мы научимся проверять, является ли данная строка допустимой панграммой или нет, используя простую программу Java. Панграмма - это любая строка, содержащая все буквы заданного набора алфавитов хотя бы один раз.

Панграммы применимы не только к английскому языку, но и к любому другому языку, имеющему фиксированный набор символов.

### Примеры

Например, общеизвестная английская панграмма звучит так: «Быстрая коричневая лиса перепрыгивает через ленивую собаку». Точно так же они доступны и на других языках.

- "The quick brown fox jumps over the lazy dog" → панграмма
- "Two driven jocks help fax my big quiz" → панграмма
- "Hello world" → не панграмма (отсутствуют многие буквы)

## Java Implementation

### Подход 1: Использование массива

Во-первых, давайте попробуем цикл for. Мы заполним логический массив маркерами для каждого символа алфавита.

Код возвращает true, когда все значения в массиве маркеров установлены в true:

```java
private static final int ALPHABET_COUNT = 26;

public static boolean isPangram(String str) {
    if (str == null) {
        return false;
    }
    
    Boolean[] alphabetMarker = new Boolean[ALPHABET_COUNT];
    Arrays.fill(alphabetMarker, false);
    
    int alphabetIndex = 0;
    str = str.toUpperCase();
    
    for (int i = 0; i < str.length(); i++) {
        if ('A' <= str.charAt(i) && str.charAt(i) <= 'Z') {
            alphabetIndex = str.charAt(i) - 'A';
            alphabetMarker[alphabetIndex] = true;
        }
    }
    
    for (boolean index : alphabetMarker) {
        if (!index) {
            return false;
        }
    }
    
    return true;
}
```

### Проверка

Проверим нашу реализацию:

```java
@Test
public void givenValidString_isPanagram_shouldReturnSuccess() {
    String input = "Two driven jocks help fax my big quiz";
    assertTrue(Pangram.isPangram(input));
}
```

### Альтернативная версия с Set

```java
public static boolean isPangramWithSet(String str) {
    if (str == null) {
        return false;
    }
    
    Set<Character> alphabetSet = new HashSet<>();
    str = str.toUpperCase();
    
    for (char c : str.toCharArray()) {
        if (c >= 'A' && c <= 'Z') {
            alphabetSet.add(c);
        }
    }
    
    return alphabetSet.size() == ALPHABET_COUNT;
}
```

## Подход 2: Использование Stream API

Альтернативный подход предполагает использование Java Streams API. Мы можем создать отфильтрованный поток символов из данного входного текста и создать карту алфавита, используя этот поток.

Код возвращает успех, если размер карты равен размеру алфавита. Для английского языка ожидаемый размер равен 26:

```java
public static boolean isPangramWithStreams(String str) {
    if (str == null) {
        return false;
    }
    
    String strUpper = str.toUpperCase();
    Stream<Character> filteredCharStream = strUpper.chars()
        .filter(item -> ((item >= 'A' && item <= 'Z')))
        .mapToObj(c -> (char) c);
    
    Map<Character, Boolean> alphabetMap = filteredCharStream
        .collect(Collectors.toMap(item -> item, k -> Boolean.TRUE, (p1, p2) -> p1));
    
    return alphabetMap.size() == ALPHABET_COUNT;
}
```

И, конечно же, протестируем:

```java
@Test
public void givenValidString_isPangramWithStreams_shouldReturnSuccess() {
    String input = "The quick brown fox jumps over the lazy dog";
    assertTrue(Pangram.isPangramWithStreams(input));
}
```

### Улучшенная версия с Stream

```java
public static boolean isPangramStreamImproved(String str) {
    if (str == null) {
        return false;
    }
    
    long distinctLetters = str.toUpperCase()
        .chars()
        .filter(c -> c >= 'A' && c <= 'Z')
        .distinct()
        .count();
    
    return distinctLetters == ALPHABET_COUNT;
}
```

## Идеальная панграмма

Идеальная панграмма немного отличается от обычной панграммы. Идеальная панграмма состоит из каждой буквы алфавита ровно один раз, в отличие от по крайней мере одного раза для панграммы.

Код возвращает значение true, когда размер карты равен размеру алфавита, а частота каждого символа в алфавите равна единице:

```java
public static boolean isPerfectPangram(String str) {
    if (str == null) {
        return false;
    }
    
    String strUpper = str.toUpperCase();
    Stream<Character> filteredCharStream = strUpper.chars()
        .filter(item -> ((item >= 'A' && item <= 'Z')))
        .mapToObj(c -> (char) c);
    
    Map<Character, Long> alphabetFrequencyMap = filteredCharStream
        .collect(Collectors.groupingBy(Function.identity(), Collectors.counting()));
    
    return alphabetFrequencyMap.size() == ALPHABET_COUNT &&
        alphabetFrequencyMap.values().stream().allMatch(item -> item == 1);
}
```

И давайте тестировать:

```java
@Test
public void givenPerfectPangramString_isPerfectPangram_shouldReturnSuccess() {
    String input = "abcdefghijklmNoPqrStuVwxyz";
    assertTrue(Pangram.isPerfectPangram(input));
}
```

В идеальной панграмме каждый символ должен быть ровно один раз. Итак, наша предыдущая панграмма должна потерпеть неудачу:

```java
String input = "Two driven jocks help fax my big quiz";
assertFalse(Pangram.isPerfectPangram(input));
```

В приведенном выше коде заданная строка ввода имеет несколько дубликатов, например, два o. Следовательно, вывод является ложным.

### Альтернативная реализация идеальной панграммы

```java
public static boolean isPerfectPangramImproved(String str) {
    if (str == null) {
        return false;
    }
    
    Map<Character, Integer> frequency = new HashMap<>();
    str = str.toUpperCase();
    
    for (char c : str.toCharArray()) {
        if (c >= 'A' && c <= 'Z') {
            frequency.put(c, frequency.getOrDefault(c, 0) + 1);
        }
    }
    
    if (frequency.size() != ALPHABET_COUNT) {
        return false;
    }
    
    return frequency.values().stream().allMatch(count -> count == 1);
}
```

## Kotlin Implementation

### Подход 1: Использование массива

```kotlin
private const val ALPHABET_COUNT = 26

fun isPangramK(str: String?): Boolean {
    if (str == null) {
        return false
    }
    
    val alphabetMarker = BooleanArray(ALPHABET_COUNT) { false }
    val upperStr = str.uppercase()
    
    for (char in upperStr) {
        if (char in 'A'..'Z') {
            val alphabetIndex = char - 'A'
            alphabetMarker[alphabetIndex] = true
        }
    }
    
    return alphabetMarker.all { it }
}
```

### Подход 2: Использование Set

```kotlin
fun isPangramWithSetK(str: String?): Boolean {
    if (str == null) {
        return false
    }
    
    val alphabetSet = mutableSetOf<Char>()
    val upperStr = str.uppercase()
    
    for (char in upperStr) {
        if (char in 'A'..'Z') {
            alphabetSet.add(char)
        }
    }
    
    return alphabetSet.size == ALPHABET_COUNT
}
```

### Функциональный стиль

```kotlin
fun isPangramFunctionalK(str: String?): Boolean {
    if (str == null) {
        return false
    }
    
    return str.uppercase()
        .filter { it in 'A'..'Z' }
        .toSet()
        .size == ALPHABET_COUNT
}
```

### Идеальная панграмма

```kotlin
fun isPerfectPangramK(str: String?): Boolean {
    if (str == null) {
        return false
    }
    
    val frequency = mutableMapOf<Char, Int>()
    val upperStr = str.uppercase()
    
    for (char in upperStr) {
        if (char in 'A'..'Z') {
            frequency[char] = frequency.getOrDefault(char, 0) + 1
        }
    }
    
    if (frequency.size != ALPHABET_COUNT) {
        return false
    }
    
    return frequency.values.all { it == 1 }
}
```

### Найти недостающие буквы

```kotlin
fun findMissingLettersK(str: String?): List<Char> {
    if (str == null) {
        return emptyList()
    }
    
    val foundLetters = str.uppercase()
        .filter { it in 'A'..'Z' }
        .toSet()
    
    return ('A'..'Z').filter { it !in foundLetters }
}
```

### Пример использования

```kotlin
fun main() {
    val pangram = "The quick brown fox jumps over the lazy dog"
    val notPangram = "Hello world"
    
    // Проверка панграммы
    println(isPangramK(pangram)) // true
    println(isPangramK(notPangram)) // false
    
    // Функциональный стиль
    println(isPangramFunctionalK(pangram)) // true
    
    // Идеальная панграмма
    println(isPerfectPangramK(pangram)) // false (есть дубликаты)
    
    // Найти недостающие буквы
    val missing = findMissingLettersK(notPangram)
    println(missing) // [A, B, C, D, E, F, G, H, I, J, K, L, M, N, P, Q, R, S, T, U, V, X, Y, Z]
}
```

## Сравнение подходов

| Подход | Простота | Производительность | Читаемость |
|--------|----------|-------------------|------------|
| Массив | Высокая | Высокая | Средняя |
| Set | Средняя | Высокая | Высокая |
| Stream API | Средняя | Средняя | Высокая |

## Сложность

### Временная сложность

- **Массив/Set:** O(n), где n - длина строки
- **Stream API:** O(n) - один проход по строке

### Пространственная сложность

- **Массив:** O(1) - фиксированный размер 26
- **Set:** O(1) - максимум 26 элементов
- **Stream API:** O(1) - для Map максимум 26 элементов

## Особенности

- **Эффективность:** Все подходы эффективны
- **Простота:** Массив подход самый простой
- **Гибкость:** Stream API наиболее гибкий

## Применение

Проверка панграмм используется в:

- Обработке текста
- Валидации данных
- Играх и головоломках
- Тестировании клавиатур
- Лингвистических исследованиях

## Варианты задачи

### Вариант 1: Панграмма с учетом регистра

```java
public static boolean isPangramCaseSensitive(String str) {
    if (str == null) {
        return false;
    }
    
    Set<Character> upperCase = new HashSet<>();
    Set<Character> lowerCase = new HashSet<>();
    
    for (char c : str.toCharArray()) {
        if (c >= 'A' && c <= 'Z') {
            upperCase.add(c);
        } else if (c >= 'a' && c <= 'z') {
            lowerCase.add((char) (c - 'a' + 'A'));
        }
    }
    
    return upperCase.size() == ALPHABET_COUNT || 
           lowerCase.size() == ALPHABET_COUNT;
}
```

### Вариант 2: Панграмма для другого алфавита

```java
public static boolean isPangramForAlphabet(String str, String alphabet) {
    if (str == null || alphabet == null) {
        return false;
    }
    
    Set<Character> alphabetSet = new HashSet<>();
    for (char c : alphabet.toCharArray()) {
        alphabetSet.add(Character.toUpperCase(c));
    }
    
    Set<Character> foundLetters = new HashSet<>();
    str = str.toUpperCase();
    
    for (char c : str.toCharArray()) {
        if (alphabetSet.contains(c)) {
            foundLetters.add(c);
        }
    }
    
    return foundLetters.size() == alphabetSet.size();
}
```

### Вариант 3: Найти недостающие буквы

```java
public static List<Character> findMissingLetters(String str) {
    if (str == null) {
        return Collections.emptyList();
    }
    
    Set<Character> foundLetters = new HashSet<>();
    str = str.toUpperCase();
    
    for (char c : str.toCharArray()) {
        if (c >= 'A' && c <= 'Z') {
            foundLetters.add(c);
        }
    }
    
    List<Character> missing = new ArrayList<>();
    for (char c = 'A'; c <= 'Z'; c++) {
        if (!foundLetters.contains(c)) {
            missing.add(c);
        }
    }
    
    return missing;
}
```

## Когда использовать

### Используйте массив, когда:

- Нужна максимальная производительность
- Простота важна
- Работаете с английским алфавитом

### Используйте Set, когда:

- Нужна гибкость
- Работаете с разными алфавитами
- Важна читаемость

### Используйте Stream API, когда:

- Нужна функциональная парадигма
- Работаете с Java 8+
- Нужна цепочка операций

## Заключение

В этой статье мы рассмотрели различные подходы к решению, чтобы выяснить, является ли данная строка допустимой панграммой или нет. Мы также обсудили еще одну разновидность панграмм, которая называется идеальной панграммой, и как ее идентифицировать программно.
