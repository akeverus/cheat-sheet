# First Non-Repeating Character

Кратко: поиск первого неповторяющегося символа в строке. Рассматриваются различные подходы: грубая сила, использование HashMap, и оптимизация с массивом фиксированного размера.

**Дата последнего обновления:** 2025-01-15

## Полезные ссылки

### Официальная документация
- [GeeksforGeeks: First non-repeating character](https://www.geeksforgeeks.org/given-a-string-find-its-first-non-repeating-character/)

### См. также
- `./word-count.md` - подсчет слов
- `./palindrome-check.md` - проверка палиндромов

## Содержание

- [Описание алгоритма](#описание-алгоритма)
- [Java Implementation](#java-implementation)
- [Kotlin Implementation](#kotlin-implementation)
- [Сравнение подходов](#сравнение-подходов)
- [Сложность](#сложность)

## Описание алгоритма

В этом уроке мы рассмотрим различные способы поиска первого неповторяющегося символа в строке в Java.

Мы также попытаемся проанализировать сложность решения во время выполнения.

### Требования

Наконец, вот несколько дополнительных моментов, которые следует учитывать при решении проблемы:

1. Входная строка может быть любой длины и может содержать сочетание символов верхнего и нижнего регистра
2. Наше решение должно заботиться о пустом или нулевом вводе
3. Во входной строке не может быть неповторяющихся символов, или, другими словами, может быть ввод, в котором все символы повторяются хотя бы один раз, и в этом случае вывод будет нулевым

### Примеры

- Вход: "leetcode" → Выход: 'l'
- Вход: "loveleetcode" → Выход: 'v'
- Вход: "aabb" → Выход: null (все символы повторяются)

## Java Implementation

### Подход 1: Грубая сила

С этим пониманием попробуем подойти к проблеме.

Во-первых, мы пытаемся разработать метод грубой силы для поиска первого неповторяющегося символа в строке. Мы начинаем с начала строки и берем по одному символу за раз и сравниваем символ с каждым другим символом строки. Если мы находим совпадение, это означает, что этот символ повторяется в другом месте строки, поэтому мы переходим к следующему символу. Если нет совпадения для символа, мы нашли решение и выходим из программы с символом.

### Реализация 1: Двойной цикл

Код выглядит следующим образом:

```java
public Character firstNonRepeatingCharBruteForceNaive(String inputString) {
    if (null == inputString || inputString.isEmpty()) {
        return null;
    }
    
    for (int outer = 0; outer < inputString.length(); outer++) {
        boolean repeat = false;
        
        for (int inner = 0; inner < inputString.length(); inner++) {
            if (inner != outer && inputString.charAt(outer) == inputString.charAt(inner)) {
                repeat = true;
                break;
            }
        }
        
        if (!repeat) {
            return inputString.charAt(outer);
        }
    }
    
    return null;
}
```

Временная сложность приведенного выше решения составляет O(n²) из-за двух вложенных циклов, которые у нас есть. Для каждого символа, который мы посещаем, мы посещаем все символы входной строки.

### Реализация 2: Использование indexOf и lastIndexOf

Ниже также представлено более компактное решение того же кода, в котором используются методы `lastIndexOf` класса String. Когда мы находим символ, чей первый индекс в строке также является последним индексом, это устанавливает, что символ существует только по этому индексу в строке и, следовательно, становится первым неповторяющимся символом.

Временная сложность этого также O(n²). Следует отметить, что метод `lastIndexOf` выполняется за время O(n) в дополнение к уже запущенному внешнему циклу, в котором мы берем символ за раз, что делает это решение O(n²), аналогичное предыдущему.

```java
public Character firstNonRepeatingCharBruteForce(String inputString) {
    if (null == inputString || inputString.isEmpty()) {
        return null;
    }
    
    for (Character c : inputString.toCharArray()) {
        int indexOfC = inputString.indexOf(c);
        if (indexOfC == inputString.lastIndexOf(c)) {
            return c;
        }
    }
    
    return null;
}
```

## Подход 2: Использование HashMap

Посмотрим, сможем ли мы сделать лучше. Узким местом подходов, которые мы обсуждали, является то, что мы сравниваем каждый символ с каждым другим символом, который появляется в строке, и мы продолжаем это, пока не достигнем конца строки или не найдем ответ. Вместо этого, если мы сможем вспомнить, сколько раз появляется каждый символ, нам не нужно будет сравнивать каждый раз, а вместо этого просто искать частоту появления символа. Для этой цели мы можем использовать Map, точнее, HashMap.

### Алгоритм

Карта будет хранить символ в качестве ключа и его частоту в его значении. Когда мы посещаем каждого персонажа, у нас есть два варианта:

1. Если персонаж уже есть на карте, мы увеличиваем его значение
2. Если персонаж не появляется на построенной карте до сих пор, это новый персонаж, и мы устанавливаем его частоту в 1

После того, как мы завершим вычисления для всей строки, у нас будет карта, которая сообщает нам количество каждого символа в строке. Все, что нам осталось сделать, это еще раз перебрать String, и первый символ, чей размер значения на карте равен единице, является нашим ответом.

### Реализация

Вот как выглядит код:

```java
public Character firstNonRepeatingCharWithMap(String inputString) {
    if (null == inputString || inputString.isEmpty()) {
        return null;
    }
    
    Map<Character, Integer> frequency = new HashMap<>();
    
    for (int outer = 0; outer < inputString.length(); outer++) {
        char character = inputString.charAt(outer);
        frequency.put(character, frequency.getOrDefault(character, 0) + 1);
    }
    
    for (Character c : inputString.toCharArray()) {
        if (frequency.get(c) == 1) {
            return c;
        }
    }
    
    return null;
}
```

Приведенное выше решение намного быстрее, учитывая, что поиск на карте - это операция с постоянным временем, или O(1). Это означает, что время получения результата не будет увеличиваться с увеличением размера входной строки.

### Альтернативная реализация с LinkedHashMap

Для сохранения порядка вставки можно использовать LinkedHashMap:

```java
public Character firstNonRepeatingCharWithLinkedHashMap(String inputString) {
    if (null == inputString || inputString.isEmpty()) {
        return null;
    }
    
    Map<Character, Integer> frequency = new LinkedHashMap<>();
    
    for (char c : inputString.toCharArray()) {
        frequency.put(c, frequency.getOrDefault(c, 0) + 1);
    }
    
    return frequency.entrySet().stream()
        .filter(entry -> entry.getValue() == 1)
        .map(Map.Entry::getKey)
        .findFirst()
        .orElse(null);
}
```

## Подход 3: Оптимизация с массивом

Мы должны обсудить некоторые предостережения относительно оптимизированного решения, которое мы обсуждали ранее. Исходный вопрос утверждает, что ввод может быть любой длины и может содержать любой символ. Это делает выбор Карты для поиска более эффективным.

Однако, если бы мы могли ограничить набор входных символов только символами нижнего регистра/символами верхнего регистра/символами английского алфавита и т. д., было бы лучше использовать массив фиксированного размера над картой.

### Реализация для строчных букв

Например, если бы ввод был ограничен только строчными буквами латинского алфавита, мы могли бы использовать массив размером 26, где каждый индекс в массиве относится к алфавиту, а значение могло бы обозначать частоту символа в строке. Наконец, первый символ в строке, значение которого в массиве равно 1, является ответом. Вот код для него:

```java
public Character firstNonRepeatingCharWithArray(String inputString) {
    if (null == inputString || inputString.isEmpty()) {
        return null;
    }
    
    int[] frequency = new int[26];
    
    for (int outer = 0; outer < inputString.length(); outer++) {
        char character = inputString.charAt(outer);
        frequency[character - 'a']++;
    }
    
    for (Character c : inputString.toCharArray()) {
        if (frequency[c - 'a'] == 1) {
            return c;
        }
    }
    
    return null;
}
```

Обратите внимание, что временная сложность по-прежнему O(n), но мы улучшили нашу пространственную сложность до постоянного пространства. Это связано с тем, что независимо от длины строки длина вспомогательного пространства (массива), которое мы используем для хранения частоты, будет постоянной.

### Реализация для ASCII символов

Для всех ASCII символов (256 символов):

```java
public Character firstNonRepeatingCharWithASCIIArray(String inputString) {
    if (null == inputString || inputString.isEmpty()) {
        return null;
    }
    
    int[] frequency = new int[256];
    
    for (int i = 0; i < inputString.length(); i++) {
        frequency[inputString.charAt(i)]++;
    }
    
    for (int i = 0; i < inputString.length(); i++) {
        if (frequency[inputString.charAt(i)] == 1) {
            return inputString.charAt(i);
        }
    }
    
    return null;
}
```

## Сравнение подходов

| Подход | Временная сложность | Пространственная сложность | Когда использовать |
|--------|---------------------|----------------------------|-------------------|
| Грубая сила | O(n²) | O(1) | Малые строки |
| HashMap | O(n) | O(k), k - уникальные символы | Общий случай |
| LinkedHashMap | O(n) | O(k) | Нужен порядок |
| Массив (26) | O(n) | O(1) | Только строчные буквы |
| Массив (256) | O(n) | O(1) | Только ASCII |

## Сложность

### Временная сложность

- **Грубая сила:** O(n²) - для каждого символа проверяем все остальные
- **HashMap/LinkedHashMap:** O(n) - два прохода по строке
- **Массив:** O(n) - два прохода по строке

### Пространственная сложность

- **Грубая сила:** O(1) - только константная память
- **HashMap/LinkedHashMap:** O(k), где k - количество уникальных символов
- **Массив (26):** O(1) - фиксированный размер 26
- **Массив (256):** O(1) - фиксированный размер 256

## Особенности

- **Эффективность:** HashMap подход оптимален для общего случая
- **Память:** Массив подход эффективнее по памяти для ограниченного набора символов
- **Простота:** Грубая сила проще всего понять

## Применение

Поиск первого неповторяющегося символа используется в:

- Обработке текста
- Парсинге
- Валидации данных
- Задачах на собеседованиях
- Анализе строк

## Варианты задачи

### Вариант 1: Все неповторяющиеся символы

```java
public List<Character> allNonRepeatingChars(String inputString) {
    if (null == inputString || inputString.isEmpty()) {
        return Collections.emptyList();
    }
    
    Map<Character, Integer> frequency = new LinkedHashMap<>();
    
    for (char c : inputString.toCharArray()) {
        frequency.put(c, frequency.getOrDefault(c, 0) + 1);
    }
    
    return frequency.entrySet().stream()
        .filter(entry -> entry.getValue() == 1)
        .map(Map.Entry::getKey)
        .collect(Collectors.toList());
}
```

### Вариант 2: Индекс первого неповторяющегося символа

```java
public int firstNonRepeatingCharIndex(String inputString) {
    if (null == inputString || inputString.isEmpty()) {
        return -1;
    }
    
    Map<Character, Integer> frequency = new HashMap<>();
    
    for (int i = 0; i < inputString.length(); i++) {
        char c = inputString.charAt(i);
        frequency.put(c, frequency.getOrDefault(c, 0) + 1);
    }
    
    for (int i = 0; i < inputString.length(); i++) {
        if (frequency.get(inputString.charAt(i)) == 1) {
            return i;
        }
    }
    
    return -1;
}
```

## Kotlin Implementation

### Подход 1: Грубая сила

```kotlin
fun firstNonRepeatingCharBruteForceK(inputString: String?): Char? {
    if (inputString.isNullOrEmpty()) {
        return null
    }
    
    for (i in inputString.indices) {
        var repeat = false
        for (j in inputString.indices) {
            if (i != j && inputString[i] == inputString[j]) {
                repeat = true
                break
            }
        }
        if (!repeat) {
            return inputString[i]
        }
    }
    
    return null
}
```

### Использование indexOf и lastIndexOf

```kotlin
fun firstNonRepeatingCharIndexK(inputString: String?): Char? {
    if (inputString.isNullOrEmpty()) {
        return null
    }
    
    for (char in inputString) {
        if (inputString.indexOf(char) == inputString.lastIndexOf(char)) {
            return char
        }
    }
    
    return null
}
```

### Подход 2: Использование HashMap

```kotlin
fun firstNonRepeatingCharHashMapK(inputString: String?): Char? {
    if (inputString.isNullOrEmpty()) {
        return null
    }
    
    val frequency = mutableMapOf<Char, Int>()
    
    for (char in inputString) {
        frequency[char] = frequency.getOrDefault(char, 0) + 1
    }
    
    for (char in inputString) {
        if (frequency[char] == 1) {
            return char
        }
    }
    
    return null
}
```

### Подход 3: Оптимизация с массивом (только строчные буквы)

```kotlin
fun firstNonRepeatingCharArrayK(inputString: String?): Char? {
    if (inputString.isNullOrEmpty()) {
        return null
    }
    
    val frequency = IntArray(26)
    
    for (char in inputString) {
        if (char in 'a'..'z') {
            frequency[char - 'a']++
        }
    }
    
    for (char in inputString) {
        if (char in 'a'..'z' && frequency[char - 'a'] == 1) {
            return char
        }
    }
    
    return null
}
```

### Для всех ASCII символов

```kotlin
fun firstNonRepeatingCharASCIIK(inputString: String?): Char? {
    if (inputString.isNullOrEmpty()) {
        return null
    }
    
    val frequency = IntArray(256)
    
    for (char in inputString) {
        frequency[char.code]++
    }
    
    for (char in inputString) {
        if (frequency[char.code] == 1) {
            return char
        }
    }
    
    return null
}
```

### Функциональный стиль

```kotlin
fun firstNonRepeatingCharFunctionalK(inputString: String?): Char? {
    if (inputString.isNullOrEmpty()) {
        return null
    }
    
    val frequency = inputString.groupingBy { it }.eachCount()
    
    return inputString.firstOrNull { frequency[it] == 1 }
}
```

### Все неповторяющиеся символы

```kotlin
fun allNonRepeatingCharsK(inputString: String?): List<Char> {
    if (inputString.isNullOrEmpty()) {
        return emptyList()
    }
    
    val frequency = inputString.groupingBy { it }.eachCount()
    
    return inputString.filter { frequency[it] == 1 }.distinct()
}
```

### Индекс первого неповторяющегося символа

```kotlin
fun firstNonRepeatingCharIndexK(inputString: String?): Int {
    if (inputString.isNullOrEmpty()) {
        return -1
    }
    
    val frequency = inputString.groupingBy { it }.eachCount()
    
    return inputString.indexOfFirst { frequency[it] == 1 }
}
```

### Пример использования

```kotlin
fun main() {
    val input1 = "leetcode"
    val input2 = "loveleetcode"
    val input3 = "aabb"
    
    println(firstNonRepeatingCharHashMapK(input1)) // 'l'
    println(firstNonRepeatingCharHashMapK(input2)) // 'v'
    println(firstNonRepeatingCharHashMapK(input3)) // null
    
    // Функциональный стиль
    println(firstNonRepeatingCharFunctionalK(input1)) // 'l'
    
    // Индекс
    println(firstNonRepeatingCharIndexK(input1)) // 0
}
```

## Когда использовать

### Используйте грубую силу, когда:

- Строки очень короткие
- Нужна простота реализации
- Производительность не критична

### Используйте HashMap, когда:

- Общий случай
- Нужна максимальная производительность
- Набор символов не ограничен

### Используйте массив, когда:

- Набор символов ограничен (например, только ASCII)
- Важна экономия памяти
- Нужна максимальная производительность

## Заключение

В этом уроке мы рассмотрели различные подходы к поиску первого неповторяющегося символа в строке. Подход с использованием HashMap является оптимальным для общего случая, в то время как подход с массивом может быть более эффективным для ограниченного набора символов.
