---
title: "Печать треугольников с помощью циклов for (Triangle with For Loops)"
description: "Руководство по печати различных типов треугольников в Java с использованием циклов for, включая прямоугольные и равнобедренные треугольники."
tags:
  - algorithms
  - problems
  - triangle-with-for-loops
difficulty: "intermediate"
prerequisites: []
next: []
updated: "2026-02-11"
---
# Печать треугольников с помощью циклов for (`Triangle with For Loops`)

Руководство по печати различных типов треугольников в **Java** с использованием циклов **for**, включая прямоугольные и равнобедренные треугольники.

## Полезные ссылки

### Официальная документация
- [Java Control Flow Statements](https://docs.oracle.com/javase/tutorial/java/nutsandbolts/flow.html)
- [Apache Commons Lang StringUtils](https://commons.apache.org/proper/commons-lang/javadocs/api-release/org/apache/commons/lang3/StringUtils.html)

### См. также
- [[README|Задачи и алгоритмы]]
- [[README|Алгоритмы со строками]]

## Содержание

- [Печать треугольников с помощью циклов for](#печать-треугольников-с-помощью-циклов-for)
- [Java Implementation](#java-implementation)
  - [Right-Angled Triangle (Java)](#right-angled-triangle-java)
  - [Isosceles Triangle (Java)](#isosceles-triangle-java)
  - [Using StringUtils.repeat()](#using-stringutilsrepeat)
  - [Using Substring Method (Java)](#using-substring-method-java)
- [Complexity Analysis](#complexity-analysis)
  - [Summary Table](#summary-table)
- [Complete Example](#complete-example)
- [Kotlin Implementation](#kotlin-implementation)
  - [Using String.repeat()](#using-stringrepeat)
  - [Example Usage](#example-usage)
- [Лучшие практики](#лучшие-практики)
- [Решение проблем](#решение-проблем)
- [Частые вопросы](#частые-вопросы)
- [Заключение](#заключение)

## Обзор

In **this lesson**, we'll **look** at **several ways** to **print triangles** in **Java**. **Naturally**, **there are many types** of **triangles**. **Here** we'll **consider only** a **couple** of **them**: **right-angled and isosceles triangles**.

## Java Implementation

### Right-Angled Triangle (Java)

**A **right-angled triangle** is **the simplest type** of **triangle** we're **going** to **study**. **Let**'s **quickly look** at **the output** we **want** to **get**:**

```text
*

*

*
```

**Here** we **notice that the triangle consists** `of 5` **rows**, **each having** a **number** of **stars equal** to **the current row number**. Of **course**, **this observation can** be **generalized**: **for each row from** 1 to N, we **need** to **output** r **stars**, **where** r is **the current row and** N is **the total number** of **rows**.

**So, **let**'s **build the triangle using two for loops**:**

```java
// Прямоугольный треугольник: в строке r выводим r звёздочек (два вложенных цикла for)
public static String printARightTriangle(int N) {
StringBuilder result = new StringBuilder();

for (int r = 1; r <= N; r++) {
for (int j = 1; j <= r; j++) {
result.append("*");
}
result.append(System.lineSeparator());
}

return result.toString();
}
```

### Isosceles Triangle (Java)

**Now let**'s **look** at **the form** of an **isosceles triangle**:**

```text
*
*
*
*
*
```

**What** do we **see** in **this case**? We **notice that besides stars**, we **also need** to **print spaces for each row**. So, we **need** to **figure out how many spaces and stars** we **should print for each row**. Of **course**, **the number** of **spaces and stars depends** on **the current row**.

**First**, we **see that** we **need** to **print** 4 **spaces for the first row**, **and** as we go **down the triangle**, we **need** 3 **spaces**, 2 **spaces**, 1 **space**, **and** no **spaces** at **all for the last row**. **Generalizing**, we **need** to **output** N — r **spaces for each row**.

**Second**, **comparing with the first example**, we **understand that here** we **need** an **odd number** of **stars**: 1, 3, 5, 7...

So, we **need** to **output** r × 2 — 1 **stars for each row**.

**Based** on **the above observations**, **let**'s **create our second example**:**

```java
public static String printAnIsoscelesTriangle(int N) {
StringBuilder result = new StringBuilder();

for (int r = 1; r <= N; r++) {
        // Print spaces
for (int sp = 1; sp <= N - r; sp++) {
result.append(" ");
}

        // Print stars
        for (int c = 1; c <= (r * 2) - 1; c++) {
result.append("*");
}

result.append(System.lineSeparator());
}

return result.toString();
}
```

### Using StringUtils.repeat()

**Actually**, we **have another way**, **consisting** of **only one for loop** — it **uses the Apache Commons Lang** 3 **library**.

**We're **going** to **use** a **for loop** to **iterate through the rows** of **the triangle**, as we **did** in **the previous examples**. **Then** we'll **use the** `StringUtils.repeat()` **method** to **generate the necessary characters for each row**:**

```java
import org.apache.commons.lang3.StringUtils;

public static String printAnIsoscelesTriangleUsingStringUtils(int N) {
StringBuilder result = new StringBuilder();

for (int r = 1; r <= N; r++) {
result.append(StringUtils.repeat(' ', N - r));
result.append(StringUtils.repeat('*', 2 * r - 1));
result.append(System.lineSeparator());
}

return result.toString();
}
```

### Using Substring Method (Java)

`Or we` **can** do a **clever trick with the** `substring()` **method**.

We **can extract the described** `StringUtils.repeat()` **methods** to **create** a **helper string**, **and then apply the** `String.substring()` **method** to it. **The helper string** is a **concatenation** of **the maximum number** of **spaces and the maximum number** of **stars** we **need** to **print the rows** of **the triangle**.

**Looking** at **the previous examples**, we **notice that** we **need** a **maximum** of N — 1 **spaces for the first row and** a **maximum** of N × 2 — 1 **stars for the last row**:**

```java
String helperString = StringUtils.repeat(' ', N - 1) + StringUtils.repeat('*', N * 2 - 1);
// For N = 5, helperString = "    *"
```

**For example**, **when** N = 5 **and** r = 3, we **need** to **print** "**", **which** is **included** in **the helperString variable**. **All** we **need** to do is **find the right formula for the** `substring()` **method**.

**Now let**'s **look** at **the complete example**:**

```java
public static String printAnIsoscelesTriangleUsingSubstring(int N) {
StringBuilder result = new StringBuilder();

String helperString = StringUtils.repeat(' ', N - 1) + StringUtils.repeat('*', N * 2 - 1);

for (int r = 0; r < N; r++) {
result.append(helperString.substring(r, N + 2 * r));
result.append(System.lineSeparator());
}

return result.toString();
}
```

**Similarly**, **with** a **bit** of **work**, we **could make** an **inverted triangle**.

## Complexity Analysis

`If we` **look** at **the first example again**, we **notice the outer and inner loops**, **each having** a **maximum** of N **steps**. **Consequently**, we **have** a **time complexity** of `O(N²)`, **where** N is **the number** of **rows** of **the triangle**.

**The second example** is **similar** — **with the only difference that** we **have two inner loops**, **which are sequential and don**'t **increase the time complexity**.

**However**, in **the third example**, we **use only** a **for loop with** N **steps**. **But** at **each step**, we **call either the** `StringUtils.repeat()` **method** or **the** `substring()` **method** of **the helper string**, **each** of **which has** `O(N)` **complexity**. **Thus**, **the overall time complexity remains the same**.

**Finally**, if we **talk about auxiliary space**, we **can quickly understand that for all examples**, **the complexity remains** in **the StringBuilder variable**. By **adding the entire triangle** to **the result variable**, we **can**'t **have complexity less than** `O(N²)`.

Of **course**, if we **were** to **print the characters directly**, we **would have constant space complexity for the first two examples**. **But** in **the third example**, we **use** a **helper string**, **and the space complexity will** be `O(N)`.

### Summary Table

| **Approach** | **Time Complexity** | **Space Complexity** | **Code Lines** |
|----------|----------------|------------------|------------|
| **Nested loops** (right-angled) | `O(N²)` | `O(N²)` | ~10 |
| **Nested loops** (isosceles) | `O(N²)` | `O(N²)` | ~15 |
| **StringUtils.repeat**() | `O(N²)` | `O(N²)` | ~8 |
| **Substring method** | `O(N²)` | `O(N)` | ~10 |

## Complete Example

**Here**'s a **complete example demonstrating all approaches**:**

```java
import org.apache.commons.lang3.StringUtils;

public class TrianglePrinter {

    public static void main(String[] args) {
        int rows = 5;

        System.out.println("Right-Angled Triangle:");
        System.out.println(printARightTriangle(rows));

        System.out.println("Isosceles Triangle (Nested Loops):");
        System.out.println(printAnIsoscelesTriangle(rows));

        System.out.println("Isosceles Triangle (StringUtils):");
        System.out.println(printAnIsoscelesTriangleUsingStringUtils(rows));

        System.out.println("Isosceles Triangle (Substring):");
        System.out.println(printAnIsoscelesTriangleUsingSubstring(rows));
    }

    // ... (all methods from above)
}
```

## Kotlin Implementation

### Right-Angled Triangle (Kotlin)

```kotlin
fun printARightTriangleK(n: Int): String {
    val result = StringBuilder()

    for (r in 1..n) {
        for (j in 1..r) {
            result.append("*")
        }
        result.appendLine()
    }

    return result.toString()
}
```

### Isosceles Triangle (Kotlin)

```kotlin
fun printAnIsoscelesTriangleK(n: Int): String {
    val result = StringBuilder()

    for (r in 1..n) {
        // Print spaces
        for (sp in 1..(n - r)) {
            result.append(" ")
        }

        // Print stars
        for (c in 1..(r * 2 - 1)) {
            result.append("*")
        }

        result.appendLine()
    }

    return result.toString()
}
```

### Using String.repeat()

```kotlin
fun printAnIsoscelesTriangleUsingRepeatK(n: Int): String {
    val result = StringBuilder()

    for (r in 1..n) {
        result.append(" ".repeat(n - r))
        result.append("*".repeat(2 * r - 1))
        result.appendLine()
    }

    return result.toString()
}
```

### Using Substring Method (Kotlin)

```kotlin
fun printAnIsoscelesTriangleUsingSubstringK(n: Int): String {
    val result = StringBuilder()
    val helperString = " ".repeat(n - 1) + "*".repeat(n * 2 - 1)

    for (r in 0 until n) {
        result.append(helperString.substring(r, n + 2 * r))
        result.appendLine()
    }

    return result.toString()
}
```

### Example Usage

```kotlin
fun main() {
    val rows = 5

    println("Right-Angled Triangle:")
    println(printARightTriangleK(rows))

    println("Isosceles Triangle (Nested Loops):")
    println(printAnIsoscelesTriangleK(rows))

    println("Isosceles Triangle (String.repeat()):")
    println(printAnIsoscelesTriangleUsingRepeatK(rows))

    println("Isosceles Triangle (Substring):")
    println(printAnIsoscelesTriangleUsingSubstringK(rows))
}
```

## Лучшие практики

Прямоугольный треугольник: внешний цикл по строкам (1..N), внутренний по символам в строке (1..r); используйте `StringBuilder` для накопления вывода. Равнобедренный: сначала пробелы (N — r), затем звёзды (2*r — 1); можно заменить внутренние циклы на `String.repeat()` или `substring` для краткости. Проверяйте N > 0; при N = 0 возвращайте пустую строку; при больших N учитывайте размер результата. В тестах покройте N = 1, N = 5, граничные значения; проверяйте количество строк и символов в строке. Расширяемость: выносите символ заполнения (звезда/пробел) в параметр или константу.

## Решение проблем

| Симптом | Возможная причина | Решение |
|--------|-------------------|---------|
| Неверное количество строк или символов | Ошибка в границах циклов (off-by-one) | Внешний цикл 1..N, внутренний для прямоугольного 1..r; для равнобедренного пробелы (N-r), звёзды (2r-1) |
| Пустой вывод при N=0 или отрицательном | Не обработан граничный случай | Проверять N > 0; при N <= 0 возвращать пустую строку |
| Медленно на больших N | Конкатенация строк в цикле | Использовать StringBuilder для накопления вывода |

## Частые вопросы

**Когда использовать String.repeat() вместо циклов?** Для краткости кода и читаемости; для учебных целей циклы показывают логику. В продакшене `repeat()` удобнее.

**Как вывести равнобедренный треугольник?** Для каждой строки r: (N — r) пробелов, затем (2*r — 1) звёзд; можно реализовать вложенными циклами или через `" ".repeat(N-r)` и `"*".repeat(2*r-1)`.

**Нужно ли проверять максимальный N?** Для учебного примера часто не обязательно; для больших N учтите, что вывод растёт как N² по символам — при очень больших N возможны проблемы с памятью.

## Заключение

В этом документе рассмотрены два типа треугольников, которые можно вывести в `Java` с помощью вложенных циклов. Прямоугольный треугольник — простейший вариант. Равнобедренный треугольник реализован двумя способами: только через циклы `for` и с использованием `StringUtils.repeat()` / `String.substring()`, что сокращает объём кода. Для каждого примера приведён анализ временной и пространственной сложности.
