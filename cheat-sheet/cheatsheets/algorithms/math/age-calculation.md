# Age Calculation

Кратко: вычисление возраста с использованием библиотек Java 8, Java 7 и Joda-Time. Рассматриваются различные подходы для расчета возраста в годах на основе даты рождения и текущей даты.

**Дата последнего обновления:** 2025-01-15

## Полезные ссылки

### Официальная документация
- [Java 8 Period Documentation](https://docs.oracle.com/javase/8/docs/api/java/time/Period.html)
- [Joda-Time Years Documentation](http://www.joda.org/joda-time/apidocs/org/joda/time/Years.html)

### См. также
- `./standard-deviation.md` - стандартное отклонение
- `./fibonacci-sequence.md` - ряд Фибоначчи

## Содержание

- [Описание алгоритма](#описание-алгоритма)
- [Java Implementation](#java-implementation)
- [Kotlin Implementation](#kotlin-implementation)
- [Сравнение подходов](#сравнение-подходов)
- [Сложность](#сложность)

## Описание алгоритма

В этом кратком руководстве мы увидим, как рассчитать возраст с помощью библиотек Java 8, Java 7 и Joda-Time.

Во всех случаях мы возьмем дату рождения и текущую дату в качестве входных данных и вернем рассчитанный возраст в годах.

### Примеры

- Дата рождения: 1990-01-15, Текущая дата: 2025-01-15 → Возраст: 35 лет
- Дата рождения: 2000-06-20, Текущая дата: 2025-01-15 → Возраст: 24 года

## Java Implementation

### Подход 1: Java 8 (Period)

Java 8 представила новый API Date-Time для работы с датами и временем, в значительной степени основанный на библиотеке Joda-Time.

В Java 8 мы можем использовать `java.time.LocalDate` для нашей даты рождения и текущей даты, а затем использовать `Period` для вычисления их разницы в годах:

```java
import java.time.LocalDate;
import java.time.Period;

public int calculateAge(LocalDate birthDate, LocalDate currentDate) {
    return Period.between(birthDate, currentDate).getYears();
}
```

LocalDate полезен здесь, потому что представляет только дату, в отличие от Date в Java, который представляет и дату, и время. `LocalDate.now()` может дать нам текущую дату.

И период полезен, когда нам нужно думать о периодах времени в годах, месяцах и днях.

### Пример использования

```java
LocalDate birthDate = LocalDate.of(1990, 1, 15);
LocalDate currentDate = LocalDate.now();
int age = calculateAge(birthDate, currentDate);
```

### Получение более точного возраста

Если бы мы хотели получить более точный возраст, скажем, в секундах, то нам нужно было бы взглянуть на LocalDateTime и Duration соответственно (и, возможно, вместо этого вернуть long):

```java
import java.time.Duration;
import java.time.LocalDateTime;

public long calculateAgeInSeconds(LocalDateTime birthDateTime, LocalDateTime currentDateTime) {
    Duration duration = Duration.between(birthDateTime, currentDateTime);
    return duration.getSeconds();
}
```

### Получение возраста в месяцах и днях

```java
public String calculateDetailedAge(LocalDate birthDate, LocalDate currentDate) {
    Period period = Period.between(birthDate, currentDate);
    return String.format("%d years, %d months, %d days", 
        period.getYears(), period.getMonths(), period.getDays());
}
```

## Подход 2: Joda-Time

Если Java 8 не подходит, мы все равно можем получить такой же результат от Joda-Time, стандарта де-факто для операций даты и времени в мире до Java 8.

Нам нужно добавить зависимость Joda-Time к нашему pom:

```xml
<dependency>
    <groupId>joda-time</groupId>
    <artifactId>joda-time</artifactId>
    <version>2.10</version>
</dependency>
```

И затем мы можем написать аналогичный метод для вычисления возраста, на этот раз используя LocalDate и Years из Joda-Time:

```java
import org.joda.time.LocalDate;
import org.joda.time.Years;

public int calculateAgeWithJodaTime(LocalDate birthDate, LocalDate currentDate) {
    Years age = Years.yearsBetween(birthDate, currentDate);
    return age.getYears();
}
```

### Пример использования

```java
LocalDate birthDate = new LocalDate(1990, 1, 15);
LocalDate currentDate = new LocalDate();
int age = calculateAgeWithJodaTime(birthDate, currentDate);
```

## Подход 3: Java 7

Без выделенного API в Java 7 нам остается создавать свой собственный, поэтому существует довольно много подходов.

В качестве примера мы можем использовать `java.util.Date`:

```java
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public int calculateAgeWithJava7(Date birthDate, Date currentDate) {
    DateFormat formatter = new SimpleDateFormat("yyyyMMdd");
    int d1 = Integer.parseInt(formatter.format(birthDate));
    int d2 = Integer.parseInt(formatter.format(currentDate));
    int age = (d2 - d1) / 10000;
    return age;
}
```

Здесь мы конвертируем заданные объекты birthDate и currentDate в целые числа и находим разницу между ними, и до тех пор, пока мы не находимся на Java 7 через 8000 лет, этот подход должен работать до тех пор.

### Улучшенная версия для Java 7

```java
import java.util.Calendar;

public int calculateAgeWithCalendar(Date birthDate, Date currentDate) {
    Calendar birth = Calendar.getInstance();
    birth.setTime(birthDate);
    Calendar current = Calendar.getInstance();
    current.setTime(currentDate);
    
    int age = current.get(Calendar.YEAR) - birth.get(Calendar.YEAR);
    
    if (current.get(Calendar.DAY_OF_YEAR) < birth.get(Calendar.DAY_OF_YEAR)) {
        age--;
    }
    
    return age;
}
```

### Версия с учетом месяца и дня

```java
public int calculateAgeAccurate(Date birthDate, Date currentDate) {
    Calendar birth = Calendar.getInstance();
    birth.setTime(birthDate);
    Calendar current = Calendar.getInstance();
    current.setTime(currentDate);
    
    int age = current.get(Calendar.YEAR) - birth.get(Calendar.YEAR);
    
    int monthDiff = current.get(Calendar.MONTH) - birth.get(Calendar.MONTH);
    if (monthDiff < 0 || (monthDiff == 0 && 
        current.get(Calendar.DAY_OF_MONTH) < birth.get(Calendar.DAY_OF_MONTH))) {
        age--;
    }
    
    return age;
}
```

## Kotlin Implementation

### Подход 1: Java 8 (Period)

```kotlin
import java.time.LocalDate
import java.time.Period
import java.time.temporal.ChronoUnit

fun calculateAgeK(birthDate: LocalDate, currentDate: LocalDate): Int {
    return Period.between(birthDate, currentDate).years
}

fun calculateAgeInSecondsK(birthDateTime: java.time.LocalDateTime, currentDateTime: java.time.LocalDateTime): Long {
    return ChronoUnit.SECONDS.between(birthDateTime, currentDateTime)
}

fun calculateDetailedAgeK(birthDate: LocalDate, currentDate: LocalDate): String {
    val period = Period.between(birthDate, currentDate)
    return "${period.years} years, ${period.months} months, ${period.days} days"
}
```

### Пример использования

```kotlin
fun main() {
    val birthDate = LocalDate.of(1990, 1, 15)
    val currentDate = LocalDate.now()
    val age = calculateAgeK(birthDate, currentDate)
    println("Age: $age years")
    
    val detailedAge = calculateDetailedAgeK(birthDate, currentDate)
    println("Detailed age: $detailedAge")
}
```

### Проверка совершеннолетия

```kotlin
fun isAdultK(birthDate: LocalDate, currentDate: LocalDate): Boolean {
    return calculateAgeK(birthDate, currentDate) >= 18
}

fun calculateAgeInDaysK(birthDate: LocalDate, currentDate: LocalDate): Long {
    return ChronoUnit.DAYS.between(birthDate, currentDate)
}

fun calculateAgeInMonthsK(birthDate: LocalDate, currentDate: LocalDate): Long {
    return ChronoUnit.MONTHS.between(birthDate, currentDate)
}
```

## Сравнение подходов

| Подход | Простота | Точность | Производительность | Когда использовать |
|--------|----------|----------|-------------------|-------------------|
| Java 8 Period | Высокая | Высокая | Высокая | Java 8+ |
| Joda-Time | Высокая | Высокая | Высокая | Java 7 и ниже |
| Java 7 Calendar | Средняя | Высокая | Средняя | Java 7 |
| Java 7 DateFormat | Низкая | Низкая | Низкая | Не рекомендуется |

## Сложность

### Временная сложность

- **Все подходы:** O(1) - константное время

### Пространственная сложность

- **Все подходы:** O(1) - только константная память

## Особенности

- **Точность:** Java 8 Period и Joda-Time наиболее точны
- **Простота:** Java 8 Period самый простой
- **Совместимость:** Joda-Time для старых версий Java

## Применение

Вычисление возраста используется в:

- Валидации данных
- Формах регистрации
- Демографических исследованиях
- Финансовых приложениях
- Медицинских системах

## Варианты задачи

### Вариант 1: Проверка совершеннолетия

```java
public boolean isAdult(LocalDate birthDate, LocalDate currentDate) {
    return calculateAge(birthDate, currentDate) >= 18;
}
```

### Вариант 2: Возраст в днях

```java
public long calculateAgeInDays(LocalDate birthDate, LocalDate currentDate) {
    return ChronoUnit.DAYS.between(birthDate, currentDate);
}
```

### Вариант 3: Возраст в месяцах

```java
public long calculateAgeInMonths(LocalDate birthDate, LocalDate currentDate) {
    return ChronoUnit.MONTHS.between(birthDate, currentDate);
}
```

## Когда использовать

### Используйте Java 8 Period, когда:

- Работаете с Java 8+
- Нужна простота и точность
- Рекомендуется для новых проектов

### Используйте Joda-Time, когда:

- Работаете с Java 7 или ниже
- Нужна совместимость
- Требуется функциональность Java 8

### Используйте Java 7 Calendar, когда:

- Нельзя использовать внешние библиотеки
- Работаете с Java 7
- Нужна точность

## Заключение

В этом кратком руководстве мы рассмотрели различные способы вычисления возраста в Java. Java 8 Period является наиболее простым и рекомендуемым подходом для новых проектов, в то время как Joda-Time остается хорошим выбором для старых версий Java.
