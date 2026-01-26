# Leap Year Calculation

Кратко: определение високосных лет в Java с использованием различных подходов: GregorianCalendar, Java 8 Date-Time API и Joda-Time. Рассматриваются правила определения високосных лет.

**Дата последнего обновления:** 2025-01-15

## Полезные ссылки

### Официальная документация
- [Java GregorianCalendar Documentation](https://docs.oracle.com/javase/8/docs/api/java/util/GregorianCalendar.html)
- [Java Year Documentation](https://docs.oracle.com/javase/8/docs/api/java/time/Year.html)

### См. также
- `../math/age-calculation.md` - вычисление возраста
- `../math/distance-between-points.md` - вычисление расстояния

## Содержание

- [Описание алгоритма](#описание-алгоритма)
- [Правила високосных лет](#правила-високосных-лет)
- [Java Implementation](#java-implementation)
- [Kotlin Implementation](#kotlin-implementation)
- [Сравнение подходов](#сравнение-подходов)

## Описание алгоритма

В этом руководстве мы покажем несколько способов определить, является ли данный год високосным в Java.

Високосный год - это год, который делится на 4 и 400 без остатка. Таким образом, годы, которые делятся на 100, но не делятся на 400, не подходят, даже если они делятся на 4.

### Правила определения високосного года

1. Год делится на 4
2. Если год делится на 100, он также должен делиться на 400

Или более формально:
- Год високосный, если он делится на 4
- Исключение: если год делится на 100, он не високосный
- Исключение из исключения: если год делится на 400, он високосный

## Java Implementation

### Правила високосных лет

#### Примеры

- **2000** - високосный (делится на 400)
- **2004** - високосный (делится на 4, но не на 100)
- **1900** - не високосный (делится на 100, но не на 400)
- **2001** - не високосный (не делится на 4)

### Подход 1: GregorianCalendar

Начиная с Java 1.1, класс GregorianCalendar позволяет нам проверять, является ли год високосным:

```java
import java.util.GregorianCalendar;

public class LeapYearChecker {
    public static boolean isLeapYear(int year) {
        GregorianCalendar calendar = new GregorianCalendar();
        return calendar.isLeapYear(year);
    }
}
```

Как и следовало ожидать, этот метод возвращает значение `true`, если данный год является високосным, и значение `false`, если год не является високосным.

Годы до нашей эры (до Рождества Христова) должны передаваться как отрицательные значения и рассчитываются как 1 - год. Например, 3 год до н.э. представлен как -2, поскольку 1 - 3 = -2.

### Пример использования

```java
System.out.println(isLeapYear(2000)); // true
System.out.println(isLeapYear(2004)); // true
System.out.println(isLeapYear(1900)); // false
System.out.println(isLeapYear(2001)); // false
```

### Подход 2: Java 8 Date-Time API

Java 8 представила `java.time` с гораздо лучшим API даты и времени.

Класс `Year` в `java.time` имеет статический метод для проверки того, является ли данный год високосным:

```java
import java.time.Year;

public class LeapYearCheckerJava8 {
    public static boolean isLeapYear(long year) {
        return Year.isLeap(year);
    }
    
    public static boolean isLeapYearInstance(int year) {
        Year yearObj = Year.of(year);
        return yearObj.isLeap();
    }
}
```

И у него также есть метод экземпляра, чтобы сделать то же самое:

```java
Year year = Year.of(2000);
boolean isLeap = year.isLeap(); // true
```

### Пример использования

```java
System.out.println(Year.isLeap(2000)); // true
System.out.println(Year.isLeap(2004)); // true
System.out.println(Year.isLeap(1900)); // false
```

### Подход 3: Joda-Time

Joda-Time API - одна из наиболее часто используемых сторонних библиотек среди проектов Java для утилит даты и времени.

Начиная с Java 8, эта библиотека находится в поддерживаемом состоянии, как указано в исходном репозитории Joda-Time GitHub.

Не существует предопределенного API-метода для поиска високосного года в Joda-Time. Однако мы можем использовать их классы `LocalDate` и `Days` для проверки високосного года:

```java
import org.joda.time.LocalDate;
import org.joda.time.Days;

public class LeapYearCheckerJoda {
    public static boolean isLeapYear(int year) {
        LocalDate localDate = new LocalDate(year, 1, 31);
        int numberOfDays = Days.daysBetween(
            localDate, 
            localDate.plusYears(1)
        ).getDays();
        return numberOfDays > 365;
    }
}
```

### Пример использования

```java
System.out.println(isLeapYear(2000)); // true
System.out.println(isLeapYear(2004)); // true
System.out.println(isLeapYear(1900)); // false
```

### Подход 4: Собственная реализация

Мы можем реализовать логику определения високосного года самостоятельно:

```java
public class LeapYearCheckerCustom {
    public static boolean isLeapYear(int year) {
        if (year % 4 != 0) {
            return false;
        } else if (year % 100 != 0) {
            return true;
        } else {
            return year % 400 == 0;
        }
    }
}
```

Или более компактная версия:

```java
public static boolean isLeapYearCompact(int year) {
    return (year % 4 == 0 && year % 100 != 0) || (year % 400 == 0);
}
```

### Пример использования

```java
System.out.println(isLeapYear(2000)); // true
System.out.println(isLeapYear(2004)); // true
System.out.println(isLeapYear(1900)); // false
System.out.println(isLeapYear(2001)); // false
```

## Kotlin Implementation

### Подход 1: GregorianCalendar

```kotlin
import java.util.GregorianCalendar

object LeapYearCheckerK {
    fun isLeapYear(year: Int): Boolean {
        val calendar = GregorianCalendar()
        return calendar.isLeapYear(year)
    }
}
```

### Подход 2: Java 8 Date-Time API

```kotlin
import java.time.Year

object LeapYearCheckerJava8K {
    fun isLeapYear(year: Long): Boolean {
        return Year.isLeap(year)
    }
    
    fun isLeapYearInstance(year: Int): Boolean {
        val yearObj = Year.of(year)
        return yearObj.isLeap
    }
}
```

### Подход 3: Собственная реализация

```kotlin
object LeapYearCheckerCustomK {
    fun isLeapYear(year: Int): Boolean {
        return (year % 4 == 0 && year % 100 != 0) || (year % 400 == 0)
    }
}
```

### Подход 4: Использование LocalDate

```kotlin
import java.time.LocalDate

fun isLeapYearLocalDateK(year: Int): Boolean {
    return try {
        val date = LocalDate.of(year, 2, 29)
        date.dayOfMonth == 29
    } catch (e: Exception) {
        false
    }
}
```

### Пример использования

```kotlin
fun main() {
    println(LeapYearCheckerK.isLeapYear(2000)) // true
    println(LeapYearCheckerK.isLeapYear(2004)) // true
    println(LeapYearCheckerK.isLeapYear(1900)) // false
    println(LeapYearCheckerK.isLeapYear(2001)) // false
    
    println(Year.isLeap(2000)) // true
    println(LeapYearCheckerCustomK.isLeapYear(2000)) // true
}
```

## Сравнение подходов

| Подход | Простота | Производительность | Зависимости | Рекомендуется |
|--------|----------|-------------------|-------------|---------------|
| GregorianCalendar | Средняя | Средняя | Нет | Для старых версий Java |
| Java 8 Date-Time | Высокая | Высокая | Нет | ✅ Рекомендуется |
| Joda-Time | Средняя | Средняя | Да | Для проектов с Joda-Time |
| Собственная реализация | Высокая | Высокая | Нет | Для простых случаев |

## Сложность

### Временная сложность

- **Все подходы:** O(1) - константное время

### Пространственная сложность

- **Все подходы:** O(1) - только константная память

## Особенности

- **Простота:** Все подходы просты в использовании
- **Производительность:** Все выполняются за константное время
- **Точность:** Все дают одинаковый результат

## Применение

Определение високосных лет используется в:

- Календарных приложениях
- Вычислениях дат
- Валидации дат
- Финансовых расчетах
- Научных вычислениях

## Варианты задачи

### Вариант 1: Подсчет високосных лет в диапазоне

```java
public static int countLeapYears(int startYear, int endYear) {
    int count = 0;
    for (int year = startYear; year <= endYear; year++) {
        if (isLeapYear(year)) {
            count++;
        }
    }
    return count;
}
```

### Вариант 2: Список високосных лет в диапазоне

```java
public static List<Integer> getLeapYears(int startYear, int endYear) {
    return IntStream.rangeClosed(startYear, endYear)
        .filter(LeapYearChecker::isLeapYear)
        .boxed()
        .collect(Collectors.toList());
}
```

### Вариант 3: Проверка с использованием LocalDate

```java
import java.time.LocalDate;

public static boolean isLeapYearLocalDate(int year) {
    try {
        LocalDate date = LocalDate.of(year, 2, 29);
        return date.getDayOfMonth() == 29;
    } catch (Exception e) {
        return false;
    }
}
```

## Когда использовать

### Используйте GregorianCalendar, когда:

- Работаете с Java версии до 8
- Нужна совместимость со старым кодом
- Используете другие методы GregorianCalendar

### Используйте Java 8 Date-Time API, когда:

- Работаете с Java 8+
- Нужен современный API
- ✅ Рекомендуется для новых проектов

### Используйте Joda-Time, когда:

- Проект уже использует Joda-Time
- Нужны дополнительные функции Joda-Time

### Используйте собственную реализацию, когда:

- Нужна максимальная простота
- Не нужны дополнительные зависимости
- Важна производительность

## Заключение

В этом руководстве мы рассмотрели несколько способов определения високосных лет в Java. Для новых проектов рекомендуется использовать Java 8 Date-Time API с классом `Year`, так как это самый современный и удобный подход.
