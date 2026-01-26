# Palindrome Check

Кратко: палиндром - это слово, фраза, число или другие последовательности символов, которые читаются одинаково в прямом и обратном порядке. Рассматриваются различные способы проверки палиндромов.

**Дата последнего обновления:** 2025-01-15

## Полезные ссылки

### Официальная документация
- [GeeksforGeeks: Check if a string is palindrome](https://www.geeksforgeeks.org/c-program-check-given-string-palindrome/)

### См. также
- `./palindromic-substrings.md` - палиндромные подстроки
- `./string-permutations.md` - перестановки строк

## Содержание

- [Описание алгоритма](#описание-алгоритма)
- [Java Implementation](#java-implementation)
- [Kotlin Implementation](#kotlin-implementation)
- [Сравнение подходов](#сравнение-подходов)
- [Сложность](#сложность)

## Описание алгоритма

Палиндром - это слово, фраза, число или другие последовательности символов, которые читаются так же, как в прямом, так и в обратном порядке, например, «мадам» или «гоночная машина».

В следующих разделах мы рассмотрим различные способы проверки того, является ли данная строка палиндромом или нет.

### Примеры палиндромов

- "madam" → палиндром
- "racecar" → палиндром
- "level" → палиндром
- "hello" → не палиндром
- "A man a plan a canal Panama" → палиндром (с игнорированием пробелов)

## Java Implementation

### Подход 1: Два указателя

Мы можем одновременно начать итерацию заданной строки вперед и назад, по одному символу за раз. Если есть совпадение, цикл продолжается; в противном случае цикл завершается:

```java
public boolean isPalindrome(String text) {
    String clean = text.replaceAll("\\s+", "").toLowerCase();
    int length = clean.length();
    int forward = 0;
    int backward = length - 1;
    
    while (backward > forward) {
        char forwardChar = clean.charAt(forward++);
        char backwardChar = clean.charAt(backward--);
        
        if (forwardChar != backwardChar) {
            return false;
        }
    }
    
    return true;
}
```

### Особенности

- Эффективен по памяти (O(1) дополнительной памяти)
- Проходит только половину строки
- Игнорирует пробелы и регистр

### Подход 2: Реверс строки

Есть несколько различных реализаций, которые подходят для этого варианта использования: мы можем использовать методы API из классов StringBuilder и StringBuffer при проверке палиндромов или мы можем обратить String без этих классов.

#### Реализация без API

Давайте сначала посмотрим на реализации кода без вспомогательных API:

```java
public boolean isPalindromeReverseTheString(String text) {
    StringBuilder reverse = new StringBuilder();
    String clean = text.replaceAll("\\s+", "").toLowerCase();
    char[] plain = clean.toCharArray();
    
    for (int i = plain.length - 1; i >= 0; i--) {
        reverse.append(plain[i]);
    }
    
    return (reverse.toString()).equals(clean);
}
```

В приведенном выше фрагменте мы просто повторяем заданную строку от последнего символа и добавляем каждый символ к следующему символу, вплоть до первого символа, тем самым изменяя заданную строку.

Наконец, мы проверяем равенство между заданной строкой и перевернутой строкой.

#### Использование StringBuilder API

Такого же поведения можно добиться с помощью методов API.

Давайте посмотрим быструю демонстрацию:

```java
public boolean isPalindromeUsingStringBuilder(String text) {
    String clean = text.replaceAll("\\s+", "").toLowerCase();
    StringBuilder plain = new StringBuilder(clean);
    StringBuilder reverse = plain.reverse();
    return (reverse.toString()).equals(clean);
}
```

#### Использование StringBuffer API

```java
public boolean isPalindromeUsingStringBuffer(String text) {
    String clean = text.replaceAll("\\s+", "").toLowerCase();
    StringBuffer plain = new StringBuffer(clean);
    StringBuffer reverse = plain.reverse();
    return (reverse.toString()).equals(clean);
}
```

Во фрагменте кода мы вызываем метод `reverse()` из API StringBuilder и StringBuffer, чтобы перевернуть заданную строку и проверить ее на равенство.

### Подход 3: Рекурсивный

Рекурсия - очень популярный метод решения подобных задач. В продемонстрированном примере мы рекурсивно перебираем заданную строку и проверяем, является ли она палиндромом или нет:

```java
public boolean isPalindromeRecursive(String text) {
    String clean = text.replaceAll("\\s+", "").toLowerCase();
    return recursivePalindrome(clean, 0, clean.length() - 1);
}

private boolean recursivePalindrome(String text, int forward, int backward) {
    if (forward == backward) {
        return true;
    }
    
    if ((text.charAt(forward)) != (text.charAt(backward))) {
        return false;
    }
    
    if (forward < backward + 1) {
        return recursivePalindrome(text, forward + 1, backward - 1);
    }
    
    return true;
}
```

### Особенности

- Элегантное решение
- Использует стек вызовов (O(n) памяти)
- Может быть медленнее из-за накладных расходов на рекурсию

### Подход 4: Stream API

Мы также можем использовать IntStream для предоставления решения:

```java
public boolean isPalindromeUsingIntStream(String text) {
    String temp = text.replaceAll("\\s+", "").toLowerCase();
    return IntStream.range(0, temp.length() / 2)
        .noneMatch(i -> temp.charAt(i) != temp.charAt(temp.length() - i - 1));
}
```

В приведенном выше фрагменте мы проверяем, что ни одна из пар символов с каждого конца строки не соответствует условию Predicate.

### Альтернативная версия с Stream

```java
public boolean isPalindromeUsingStream(String text) {
    String clean = text.replaceAll("\\s+", "").toLowerCase();
    return IntStream.range(0, clean.length() / 2)
        .allMatch(i -> clean.charAt(i) == clean.charAt(clean.length() - i - 1));
}
```

## Kotlin Implementation

В Kotlin проверка палиндрома может быть реализована следующим образом:

### Подход 1: Два указателя

```kotlin
fun isPalindrome(text: String): Boolean {
    val clean = text.replace("\\s+".toRegex(), "").lowercase()
    var forward = 0
    var backward = clean.length - 1

    while (backward > forward) {
        if (clean[forward++] != clean[backward--]) {
            return false
        }
    }

    return true
}
```

### Подход 2: Реверс строки

```kotlin
fun isPalindromeReverseTheString(text: String): Boolean {
    val clean = text.replace("\\s+".toRegex(), "").lowercase()
    val reverse = clean.reversed()
    return reverse == clean
}
```

Или используя StringBuilder:

```kotlin
fun isPalindromeUsingStringBuilder(text: String): Boolean {
    val clean = text.replace("\\s+".toRegex(), "").lowercase()
    val reverse = StringBuilder(clean).reverse().toString()
    return reverse == clean
}
```

### Подход 3: Рекурсивный

```kotlin
fun isPalindromeRecursive(text: String): Boolean {
    val clean = text.replace("\\s+".toRegex(), "").lowercase()
    return recursivePalindrome(clean, 0, clean.length - 1)
}

private fun recursivePalindrome(text: String, forward: Int, backward: Int): Boolean {
    if (forward == backward) return true
    if (text[forward] != text[backward]) return false
    if (forward < backward + 1) {
        return recursivePalindrome(text, forward + 1, backward - 1)
    }
    return true
}
```

### Подход 4: Функциональный стиль

```kotlin
fun isPalindromeFunctional(text: String): Boolean {
    val clean = text.replace("\\s+".toRegex(), "").lowercase()
    return clean.indices.all { i ->
        clean[i] == clean[clean.length - i - 1]
    }
}
```

Или более компактная версия:

```kotlin
fun isPalindromeCompact(text: String): Boolean {
    val clean = text.replace("\\s+".toRegex(), "").lowercase()
    return clean == clean.reversed()
}
```

Использование:

```kotlin
fun main() {
    println(isPalindrome("madam")) // true
    println(isPalindrome("racecar")) // true
    println(isPalindrome("hello")) // false
    println(isPalindrome("A man a plan a canal Panama")) // true
}
```

## Сравнение подходов

| Подход | Временная сложность | Пространственная сложность | Простота |
|--------|---------------------|----------------------------|----------|
| Два указателя | O(n) | O(1) | Простая |
| Реверс строки | O(n) | O(n) | Простая |
| Рекурсивный | O(n) | O(n) | Средняя |
| Stream API | O(n) | O(1) | Средняя |

## Сложность

### Временная сложность

- **Все подходы:** O(n), где n - длина строки

Каждый символ проверяется один раз.

### Пространственная сложность

- **Два указателя:** O(1) - только константная память
- **Реверс строки:** O(n) - для хранения перевернутой строки
- **Рекурсивный:** O(n) - для стека вызовов
- **Stream API:** O(1) - только константная память

## Особенности

- **Игнорирование пробелов:** Большинство реализаций игнорируют пробелы
- **Игнорирование регистра:** Сравнение без учета регистра
- **Эффективность:** Два указателя - самый эффективный подход

## Применение

Проверка палиндромов используется в:

- Обработке текста
- Валидации данных
- Алгоритмах сжатия
- Задачах на собеседованиях
- Анализе ДНК

## Варианты задачи

### Вариант 1: Палиндром с учетом регистра

```java
public boolean isPalindromeCaseSensitive(String text) {
    String clean = text.replaceAll("\\s+", "");
    int length = clean.length();
    int forward = 0;
    int backward = length - 1;
    
    while (backward > forward) {
        if (clean.charAt(forward++) != clean.charAt(backward--)) {
            return false;
        }
    }
    
    return true;
}
```

### Вариант 2: Палиндром с учетом пробелов

```java
public boolean isPalindromeWithSpaces(String text) {
    int forward = 0;
    int backward = text.length() - 1;
    
    while (backward > forward) {
        char forwardChar = text.charAt(forward);
        char backwardChar = text.charAt(backward);
        
        if (!Character.isLetterOrDigit(forwardChar)) {
            forward++;
            continue;
        }
        
        if (!Character.isLetterOrDigit(backwardChar)) {
            backward--;
            continue;
        }
        
        if (Character.toLowerCase(forwardChar) != Character.toLowerCase(backwardChar)) {
            return false;
        }
        
        forward++;
        backward--;
    }
    
    return true;
}
```

### Вариант 3: Числовой палиндром

```java
public boolean isNumericPalindrome(int number) {
    int original = number;
    int reversed = 0;
    
    while (number > 0) {
        reversed = reversed * 10 + number % 10;
        number /= 10;
    }
    
    return original == reversed;
}
```

## Когда использовать

### Используйте два указателя, когда:

- Нужна максимальная эффективность
- Важна экономия памяти
- Простая реализация достаточна

### Используйте реверс строки, когда:

- Нужна простота понимания
- Память не является ограничением
- Работаете с небольшими строками

### Используйте рекурсивный подход, когда:

- Нужна элегантность кода
- Обучаете рекурсию
- Стек вызовов не является проблемой

## Заключение

В этом руководстве мы рассмотрели различные способы проверки того, является ли данная строка палиндромом. Подход с двумя указателями является наиболее эффективным по памяти и времени, в то время как другие подходы могут быть более читаемыми или подходящими для определенных случаев использования.
