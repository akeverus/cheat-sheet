# Palindromic Substrings

Кратко: поиск всех подстрок в заданной строке, которые являются палиндромами. Рассматриваются три подхода: грубая сила (O(n³)), расширение от центра (O(n²)), и алгоритм Манахера (O(n)).

**Дата последнего обновления:** 2025-01-15

## Полезные ссылки

### Официальная документация
- [GeeksforGeeks: Count All Palindromic Subsequence](https://www.geeksforgeeks.org/count-palindromic-subsequence-given-string/)

### См. также
- `./palindrome-check.md` - проверка палиндромов
- `./string-permutations.md` - перестановки строк

## Содержание

- [Описание алгоритма](#описание-алгоритма)
- [Java Implementation](#java-implementation)
- [Kotlin Implementation](#kotlin-implementation)
- [Сравнение подходов](#сравнение-подходов)
- [Сложность](#сложность)

## Описание алгоритма

В этом кратком руководстве мы рассмотрим различные подходы к поиску всех подстрок в заданной строке, которые являются палиндромами. Мы также отметим временную сложность каждого подхода.

### Примеры

- Вход: "abc" → Выход: {"a", "b", "c"}
- Вход: "aaa" → Выход: {"a", "aa", "aaa"}
- Вход: "racecar" → Выход: {"r", "a", "c", "e", "c", "a", "r", "ce", "ace", "racecar", ...}

## Java Implementation

### Подход 1: Грубая сила

В этом подходе мы просто перебираем входную строку, чтобы найти все подстроки. Заодно проверим, является подстрока палиндромом или нет:

```java
public Set<String> findAllPalindromesUsingBruteForceApproach(String input) {
    Set<String> palindromes = new HashSet<>();
    
    for (int i = 0; i < input.length(); i++) {
        for (int j = i + 1; j <= input.length(); j++) {
            if (isPalindrome(input.substring(i, j))) {
                palindromes.add(input.substring(i, j));
            }
        }
    }
    
    return palindromes;
}
```

В приведенном выше примере мы просто сравниваем подстроку с обратной, чтобы увидеть, является ли она палиндромом:

```java
private boolean isPalindrome(String input) {
    StringBuilder plain = new StringBuilder(input);
    StringBuilder reverse = plain.reverse();
    return (reverse.toString()).equals(input);
}
```

Конечно, мы можем легко выбрать один из нескольких других подходов.

### Оптимизированная проверка палиндрома

Более эффективная проверка палиндрома:

```java
private boolean isPalindrome(String input) {
    int left = 0;
    int right = input.length() - 1;
    
    while (left < right) {
        if (input.charAt(left) != input.charAt(right)) {
            return false;
        }
        left++;
        right--;
    }
    
    return true;
}
```

Временная сложность этого подхода составляет O(n³). Хотя это может быть приемлемо для небольших входных строк, нам понадобится более эффективный подход, если мы проверяем палиндромы в больших объемах текста.

## Подход 2: Расширение от центра

Идея подхода централизации состоит в том, чтобы рассматривать каждый символ как точку опоры и расширяться в обоих направлениях, чтобы найти палиндромы.

Мы расширимся только в том случае, если символы слева и справа совпадают, что сделает строку палиндромом. В противном случае мы переходим к следующему символу.

Давайте посмотрим быструю демонстрацию, в которой мы будем рассматривать каждый символ как центр палиндрома:

```java
public Set<String> findAllPalindromesUsingCenter(String input) {
    Set<String> palindromes = new HashSet<>();
    
    for (int i = 0; i < input.length(); i++) {
        palindromes.addAll(findPalindromes(input, i, i + 1)); // Четная длина
        palindromes.addAll(findPalindromes(input, i, i));     // Нечетная длина
    }
    
    return palindromes;
}
```

В приведенном выше цикле мы расширяемся в обоих направлениях, чтобы получить набор всех палиндромов с центром в каждой позиции. Мы найдем палиндромы как четной, так и нечетной длины, дважды вызвав метод `findPalindromes` в цикле:

```java
private Set<String> findPalindromes(String input, int low, int high) {
    Set<String> result = new HashSet<>();
    
    while (low >= 0 && high < input.length() && input.charAt(low) == input.charAt(high)) {
        result.add(input.substring(low, high + 1));
        low--;
        high++;
    }
    
    return result;
}
```

### Визуализация

Для строки "aba":
- Центр в позиции 1 (нечетная длина): расширяемся от "b" → "aba"
- Центр в позиции 0-1 (четная длина): проверяем "ab" → не палиндром
- Центр в позиции 1-2 (четная длина): проверяем "ba" → не палиндром

Временная сложность этого подхода составляет O(n²). Это улучшение по сравнению с нашим подходом грубой силы, но мы можем сделать еще лучше, как мы увидим в следующем разделе.

## Подход 3: Алгоритм Манахера

Алгоритм Манахера находит самую длинную палиндромную подстроку за линейное время. Мы будем использовать этот алгоритм, чтобы найти все подстроки, являющиеся палиндромами.

### Инициализация

Прежде чем мы углубимся в алгоритм, мы инициализируем несколько переменных.

Во-первых, мы защитим входную строку граничным символом в начале и в конце, прежде чем преобразовать результирующую строку в массив символов:

```java
String formattedInput = "@" + input + "#";
char inputCharArr[] = formattedInput.toCharArray();
```

Затем мы будем использовать двумерный массив radius с двумя строками - одна для хранения длин палиндромов нечетной длины, а другая - для хранения длин палиндромов четной длины:

```java
int radius[][] = new int[2][input.length() + 1];
```

### Основной алгоритм

Затем мы пройдемся по входному массиву, чтобы найти длину палиндрома с центром в позиции i и сохраним эту длину в radius[][]:

```java
Set<String> palindromes = new HashSet<>();
int max;

for (int j = 0; j <= 1; j++) {
    radius[j][0] = max = 0;
    int i = 1;
    
    while (i <= input.length()) {
        palindromes.add(Character.toString(inputCharArr[i]));
        
        while (inputCharArr[i - max - 1] == inputCharArr[i + j + max]) {
            max++;
        }
        
        radius[j][i] = max;
        int k = 1;
        
        while ((radius[j][i - k] != max - k) && (k < max)) {
            radius[j][i + k] = Math.min(radius[j][i - k], max - k);
            k++;
        }
        
        max = Math.max(max - k, 0);
        i += k;
    }
}
```

Временная сложность этого подхода составляет O(n). Наконец, мы пройдемся по массиву radius[][] для вычисления палиндромных подстрок с центром в каждой позиции:

```java
for (int i = 1; i <= input.length(); i++) {
    for (int j = 0; j <= 1; j++) {
        for (max = radius[j][i]; max > 0; max--) {
            palindromes.add(input.substring(i - max - 1, max + j + i - 1));
        }
    }
}
```

### Особенности алгоритма Манахера

Хотя алгоритм Манахера имеет временную сложность O(n), логика объединения всех палиндромов в множество будет работать за O(n²). Это происходит потому, что метод подстроки каждый раз возвращает новую строку. Снижение производительности аналогично неэффективной конкатенации строк.

### Упрощенная версия алгоритма Манахера

Для поиска только количества палиндромных подстрок:

```java
public int countPalindromicSubstrings(String s) {
    int count = 0;
    
    for (int i = 0; i < s.length(); i++) {
        count += expandAroundCenter(s, i, i);     // Нечетная длина
        count += expandAroundCenter(s, i, i + 1); // Четная длина
    }
    
    return count;
}

private int expandAroundCenter(String s, int left, int right) {
    int count = 0;
    
    while (left >= 0 && right < s.length() && s.charAt(left) == s.charAt(right)) {
        count++;
        left--;
        right++;
    }
    
    return count;
}
```

## Kotlin Implementation

### Подход 1: Грубая сила

```kotlin
fun findAllPalindromesBruteForceK(input: String): Set<String> {
    val palindromes = mutableSetOf<String>()
    
    for (i in input.indices) {
        for (j in i + 1..input.length) {
            val substring = input.substring(i, j)
            if (isPalindromeK(substring)) {
                palindromes.add(substring)
            }
        }
    }
    
    return palindromes
}

private fun isPalindromeK(input: String): Boolean {
    var left = 0
    var right = input.length - 1
    
    while (left < right) {
        if (input[left] != input[right]) {
            return false
        }
        left++
        right--
    }
    
    return true
}
```

### Подход 2: Расширение от центра

```kotlin
fun findAllPalindromesCenterK(input: String): Set<String> {
    val palindromes = mutableSetOf<String>()
    
    for (i in input.indices) {
        // Нечетная длина
        expandAroundCenterK(input, i, i, palindromes)
        // Четная длина
        expandAroundCenterK(input, i, i + 1, palindromes)
    }
    
    return palindromes
}

private fun expandAroundCenterK(
    input: String,
    left: Int,
    right: Int,
    palindromes: MutableSet<String>
) {
    var l = left
    var r = right
    
    while (l >= 0 && r < input.length && input[l] == input[r]) {
        palindromes.add(input.substring(l, r + 1))
        l--
        r++
    }
}
```

### Подсчет палиндромных подстрок

```kotlin
fun countPalindromicSubstringsK(s: String): Int {
    var count = 0
    
    for (i in s.indices) {
        count += expandAroundCenterCountK(s, i, i)      // Нечетная длина
        count += expandAroundCenterCountK(s, i, i + 1)  // Четная длина
    }
    
    return count
}

private fun expandAroundCenterCountK(s: String, left: Int, right: Int): Int {
    var count = 0
    var l = left
    var r = right
    
    while (l >= 0 && r < s.length && s[l] == s[r]) {
        count++
        l--
        r++
    }
    
    return count
}
```

### Самая длинная палиндромная подстрока

```kotlin
fun longestPalindromeK(s: String): String {
    if (s.isEmpty()) {
        return ""
    }
    
    var start = 0
    var end = 0
    
    for (i in s.indices) {
        val len1 = expandAroundCenterLengthK(s, i, i)
        val len2 = expandAroundCenterLengthK(s, i, i + 1)
        val len = maxOf(len1, len2)
        
        if (len > end - start) {
            start = i - (len - 1) / 2
            end = i + len / 2
        }
    }
    
    return s.substring(start, end + 1)
}

private fun expandAroundCenterLengthK(s: String, left: Int, right: Int): Int {
    var l = left
    var r = right
    
    while (l >= 0 && r < s.length && s[l] == s[r]) {
        l--
        r++
    }
    
    return r - l - 1
}
```

### Функциональный стиль

```kotlin
fun findAllPalindromesFunctionalK(input: String): Set<String> {
    return input.indices.flatMap { i ->
        (i + 1..input.length).mapNotNull { j ->
            val substring = input.substring(i, j)
            if (isPalindromeK(substring)) substring else null
        }
    }.toSet()
}

fun countPalindromicSubstringsFunctionalK(s: String): Int {
    return s.indices.sumOf { i ->
        expandAroundCenterCountK(s, i, i) + expandAroundCenterCountK(s, i, i + 1)
    }
}
```

### Пример использования

```kotlin
fun main() {
    val input = "racecar"
    
    // Грубая сила
    val palindromes1 = findAllPalindromesBruteForceK(input)
    println(palindromes1) // {r, a, c, e, ace, cec, racecar, ...}
    
    // Расширение от центра
    val palindromes2 = findAllPalindromesCenterK(input)
    println(palindromes2)
    
    // Подсчет
    println(countPalindromicSubstringsK("abc")) // 3
    println(countPalindromicSubstringsK("aaa")) // 6
    
    // Самая длинная
    println(longestPalindromeK("babad")) // "bab" или "aba"
    println(longestPalindromeK("racecar")) // "racecar"
}
```

## Сравнение подходов

| Подход | Временная сложность | Пространственная сложность | Когда использовать |
|--------|---------------------|----------------------------|-------------------|
| Грубая сила | O(n³) | O(n²) | Малые строки |
| Расширение от центра | O(n²) | O(n²) | Средние строки |
| Алгоритм Манахера | O(n) | O(n) | Большие строки |

## Сложность

### Временная сложность

- **Грубая сила:** O(n³) - для каждой подстроки проверяем палиндром
- **Расширение от центра:** O(n²) - для каждого центра расширяемся
- **Алгоритм Манахера:** O(n) - один проход, но сбор результатов O(n²)

### Пространственная сложность

- **Грубая сила:** O(n²) - для хранения всех подстрок
- **Расширение от центра:** O(n²) - для хранения всех подстрок
- **Алгоритм Манахера:** O(n) - для массива radius

## Особенности

- **Эффективность:** Алгоритм Манахера наиболее эффективен
- **Простота:** Расширение от центра проще понять
- **Память:** Грубая сила требует больше памяти

## Применение

Поиск палиндромных подстрок используется в:

- Обработке текста
- Анализе ДНК
- Криптографии
- Задачах на собеседованиях
- Поиске паттернов

## Варианты задачи

### Вариант 1: Самая длинная палиндромная подстрока

```java
public String longestPalindrome(String s) {
    if (s == null || s.length() < 1) {
        return "";
    }
    
    int start = 0, end = 0;
    
    for (int i = 0; i < s.length(); i++) {
        int len1 = expandAroundCenter(s, i, i);
        int len2 = expandAroundCenter(s, i, i + 1);
        int len = Math.max(len1, len2);
        
        if (len > end - start) {
            start = i - (len - 1) / 2;
            end = i + len / 2;
        }
    }
    
    return s.substring(start, end + 1);
}

private int expandAroundCenter(String s, int left, int right) {
    while (left >= 0 && right < s.length() && s.charAt(left) == s.charAt(right)) {
        left--;
        right++;
    }
    return right - left - 1;
}
```

### Вариант 2: Количество палиндромных подстрок

```java
public int countSubstrings(String s) {
    int count = 0;
    
    for (int i = 0; i < s.length(); i++) {
        count += countPalindromes(s, i, i);     // Нечетная длина
        count += countPalindromes(s, i, i + 1); // Четная длина
    }
    
    return count;
}

private int countPalindromes(String s, int left, int right) {
    int count = 0;
    
    while (left >= 0 && right < s.length() && s.charAt(left) == s.charAt(right)) {
        count++;
        left--;
        right++;
    }
    
    return count;
}
```

## Когда использовать

### Используйте грубую силу, когда:

- Строки очень короткие
- Нужна простота реализации
- Производительность не критична

### Используйте расширение от центра, когда:

- Средние строки
- Нужен баланс между простотой и эффективностью
- Не нужна максимальная производительность

### Используйте алгоритм Манахера, когда:

- Большие строки
- Нужна максимальная производительность
- Важна временная сложность

## Заключение

В этой быстрой статье мы обсудили временные сложности различных подходов к поиску подстрок, являющихся палиндромами. Алгоритм Манахера является наиболее эффективным, но расширение от центра проще понять и реализовать.
