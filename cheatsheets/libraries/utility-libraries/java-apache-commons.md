---
title: "Apache Commons: Обширная коллекция Java утилит"
description: "Комплексное руководство по использованию Apache Commons — набора высококачественных Java утилит, которые дополняют стандартную библиотеку Java."
tags:
  - libraries
  - utility-libraries
  - java-apache-commons
difficulty: "intermediate"
prerequisites: []
next: []
updated: "2026-04-20"
---
# Apache Commons: Обширная коллекция Java утилит

**Комплексное руководство по использованию `Apache Commons` — набора высококачественных `Java` утилит, которые дополняют стандартную библиотеку `Java`.**

## Полезные ссылки

### Официальная документация
- [Apache Commons](https://commons.apache.org/) — главный сайт проекта
- [Commons Lang](https://commons.apache.org/proper/commons-lang/) — **StringUtils**, **ObjectUtils** и др.
- [Commons IO](https://commons.apache.org/proper/commons-io/) — работа с файлами и `IO`
- [Commons Collections](https://commons.apache.org/proper/commons-collections/) — расширенные коллекции

### Модули
- [Commons Codec](https://commons.apache.org/proper/commons-codec/) — кодирование/декодирование
- [Commons Compress](https://commons.apache.org/proper/commons-compress/) — архивы
- [Commons CSV](https://commons.apache.org/proper/commons-csv/) — **CSV** обработка
- [Commons DBCP](https://commons.apache.org/proper/commons-dbcp/) — **Connection pooling**
- [Commons Math](https://commons.apache.org/proper/commons-math/) — математические функции


### См. также
- [Jackson: JSON сериализация в Java](../serialization/jackson.md)
- [Gson](../serialization/java-gson.md)
- [JUnit 5](../testing-libraries/java-junit5.md)
- [Mockito](../testing-libraries/java-mockito.md)
## Содержание

- [Введение в Apache Commons](#введение-в-apache-commons)
  - [Почему Apache Commons?](#почему-apache-commons)
  - [Основные модули](#основные-модули)
- [Установка и структура](#установка-и-структура)
  - [Maven зависимости](#maven-зависимости)
  - [Gradle](#gradle)
- [Commons Lang](#commons-lang)
  - [StringUtils](#stringutils)
  - [ObjectUtils](#objectutils)
  - [ArrayUtils](#arrayutils)
  - [NumberUtils](#numberutils)
  - [RandomStringUtils и RandomUtils](#randomstringutils-и-randomutils)
  - [ClassUtils](#classutils)
- [Commons IO](#commons-io)
  - [FileUtils](#fileutils)
  - [IOUtils](#ioutils)
  - [FilenameUtils](#filenameutils)
- [Commons Collections](#commons-collections)
  - [CollectionUtils](#collectionutils)
  - [MapUtils](#maputils)
  - [Bag](#bag)
  - [MultiMap](#multimap)
- [Commons Codec](#commons-codec)
  - [Base64](#base64)
  - [Hex](#hex)
  - [DigestUtils](#digestutils)
- [Commons Compress](#commons-compress)
  - [Zip](#zip)
- [Commons CSV](#commons-csv)
- [Другие полезные модули](#другие-полезные-модули)
  - [Commons Math](#commons-math)
  - [Commons CLI](#commons-cli)
- [Интеграция с Spring Boot](#интеграция-с-spring-boot)
  - [Автоматическая конфигурация](#автоматическая-конфигурация)
- [Best practices](#best-practices)
  - [1. Использование правильных модулей](#1-использование-правильных-модулей)
  - [2. Null-safety](#2-null-safety)
  - [3. Работа с файлами](#3-работа-с-файлами)
  - [4. Кодирование и безопасность](#4-кодирование-и-безопасность)
  - [5. Логирование и отладка](#5-логирование-и-отладка)
- [Заключение](#заключение)
  - [Преимущества Apache Commons](#преимущества-apache-commons)
  - [Основные паттерны использования](#основные-паттерны-использования)
  - [Когда использовать Apache Commons](#когда-использовать-apache-commons)
  - [Сравнение с альтернативами](#сравнение-с-альтернативами)
- [См. также](#см-также-1)

## Введение в Apache Commons

**Apache Commons** — это проект **Apache Software Foundation**, предоставляющий переиспользуемые **Java** компоненты. Это одна из старейших и наиболее зрелых библиотек утилит для **Java**.

### Почему Apache Commons?

**Apache Commons** предлагает множество преимуществ:**

1. **Широкая функциональность** — Более 50 различных модулей
2. **Высокое качество** — Тщательно протестированный код
3. **Backward compatibility** — Поддержка старых версий **Java**
4. **Активное развитие** — Регулярные обновления
5. **Модульная структура** — Добавляйте только нужные компоненты
6. **Apache License** — Свободная лицензия
7. **Широкое использование** — В тысячах проектов по всему миру
8. **Документация** — Подробная документация для каждого модуля

### Основные модули

- **Commons Lang** — Расширения для **java.lang**
- **Commons IO** — Утилиты для работы с файлами и `IO`
- **Commons Collections** — Расширенные коллекции
- **Commons Codec** — Кодирование/декодирование
- **Commons Compress** — Работа с архивами
- **Commons CSV** — Обработка **CSV** файлов
- **Commons Math** — Математические функции
- **Commons CLI** — Парсинг командной строки

## Установка и структура

### Maven зависимости

Зависимости **Maven** для **Commons Lang** и других модулей **Apache Commons**.

```xml
<!-- Commons Lang 3 (основной модуль) -->
<dependency>
    <groupId>org.apache.commons</groupId>
    <artifactId>commons-lang3</artifactId>
    <version>3.12.0</version>
</dependency>

<!-- Commons IO -->
<dependency>
    <groupId>commons-io</groupId>
    <artifactId>commons-io</artifactId>
    <version>2.11.0</version>
</dependency>

<!-- Commons Collections 4 -->
<dependency>
    <groupId>org.apache.commons</groupId>
    <artifactId>commons-collections4</artifactId>
    <version>4.4</version>
</dependency>

<!-- Commons Codec -->
<dependency>
    <groupId>commons-codec</groupId>
    <artifactId>commons-codec</artifactId>
    <version>1.15</version>
</dependency>
```

### Gradle

```kotlin
dependencies {
    implementation("org.apache.commons:commons-lang3:3.12.0")
    implementation("commons-io:commons-io:2.11.0")
    implementation("org.apache.commons:commons-collections4:4.4")
    implementation("commons-codec:commons-codec:1.15")
}
```

## Commons Lang

Основной модуль с расширениями для **java.lang**.

### StringUtils

Мощные утилиты для работы со строками.

```java
import org.apache.commons.lang3.StringUtils;

public class StringUtilsExample {

    public static void main(String[] args) {
        String text = "  Hello World  ";
        String empty = "";
        String nullStr = null;

        // Проверка на пустоту
        System.out.println(StringUtils.isEmpty(empty));      // true
        System.out.println(StringUtils.isNotEmpty(text));    // true
        System.out.println(StringUtils.isBlank("   "));      // true
        System.out.println(StringUtils.isNotBlank(text));    // true

        // Обработка null
        System.out.println(StringUtils.defaultString(nullStr, "default")); // "default"
        System.out.println(StringUtils.defaultIfEmpty("", "default"));      // "default"

        // Удаление пробелов
        System.out.println(StringUtils.trim(text));          // "Hello World"
        System.out.println(StringUtils.strip(text));          // "Hello World"

        // Преобразования
        System.out.println(StringUtils.upperCase(text));     // "  HELLO WORLD  "
        System.out.println(StringUtils.lowerCase(text));     // "  hello world  "
        System.out.println(StringUtils.capitalize("hello")); // "Hello"
        System.out.println(StringUtils.uncapitalize("HELLO"));// "hELLO"

        // Проверка содержимого
        System.out.println(StringUtils.contains(text, "World"));        // true
        System.out.println(StringUtils.startsWith(text, "  Hello"));    // true
        System.out.println(StringUtils.endsWith(text, "World  "));      // true

        // Разделение и объединение
        String[] parts = StringUtils.split("a,b,c", ",");
        System.out.println(StringUtils.join(parts, ";"));              // "a;b;c"

        // Повторение
        System.out.println(StringUtils.repeat("Ha", 3));               // "HaHaHa"

        // Аббревиатура
        System.out.println(StringUtils.abbreviate("Long text here", 10)); // "Long te..."
    }
}
```

### ObjectUtils

Утилиты для работы с объектами.

```java
import org.apache.commons.lang3.ObjectUtils;

public class ObjectUtilsExample {

    public static void main(String[] args) {
        String str1 = "Hello";
        String str2 = null;
        String str3 = "World";

        // Первое не-null значение
        String result1 = ObjectUtils.firstNonNull(str1, str2, str3);
        System.out.println(result1); // "Hello"

        String result2 = ObjectUtils.firstNonNull(str2, null, str3);
        System.out.println(result2); // "World"

        // Все не-null значения
        Object[] nonNulls = ObjectUtils.allNotNull(str1, str2, str3);
        System.out.println(nonNulls.length); // 2

        // Identity comparison
        String s1 = new String("test");
        String s2 = new String("test");
        System.out.println(ObjectUtils.notEqual(s1, s2)); // true (разные объекты)

        // Min/Max с null-safe
        Integer[] numbers = {3, 1, null, 5, 2};
        Integer min = ObjectUtils.min(numbers);
        Integer max = ObjectUtils.max(numbers);
        System.out.println("Min: " + min + ", Max: " + max); // Min: 1, Max: 5

        // Default значение
        String defaultValue = ObjectUtils.defaultIfNull(str2, "default");
        System.out.println(defaultValue); // "default"
    }
}
```

### ArrayUtils

Утилиты для работы с массивами.

```java
import org.apache.commons.lang3.ArrayUtils;

/
 * Демонстрация работы с ArrayUtils из Apache Commons Lang
 * ArrayUtils предоставляет множество утилитных методов для работы с массивами
 */
public class ArrayUtilsExample {

    /
     * Основной метод демонстрации различных операций с массивами через ArrayUtils
     */
    public static void main(String[] args) {
        // Создаем тестовые массивы для демонстрации
        int[] numbers = {1, 2, 3, 4, 5};  // Обычный массив с элементами
        int[] empty = {};                  // Пустой массив
        int[] nullArray = null;            // Null массив для демонстрации null-safe операций

        // Проверка на пустоту - безопасная проверка без NullPointerException
        System.out.println(ArrayUtils.isEmpty(empty));      // true - массив пустой
        System.out.println(ArrayUtils.isNotEmpty(numbers)); // true - массив не пустой и содержит элементы

        // Работа с null - ArrayUtils безопасно обрабатывает null массивы
        System.out.println(ArrayUtils.isEmpty(nullArray));  // true - null считается пустым массивом

        // Поиск элементов в массиве
        System.out.println(ArrayUtils.contains(numbers, 3));     // true - элемент 3 найден в массиве
        System.out.println(ArrayUtils.indexOf(numbers, 4));      // 3 - индекс первого вхождения элемента 4
        System.out.println(ArrayUtils.lastIndexOf(numbers, 2));  // 1 - индекс последнего вхождения элемента 2

        // Добавление элементов - создает новый массив с добавленным элементом
        int[] added = ArrayUtils.add(numbers, 6);  // Добавляем элемент 6 в конец массива
        System.out.println(ArrayUtils.toString(added)); // {1,2,3,4,5,6} - новый массив с добавленным элементом

        // Вставка элемента - создает новый массив с элементом вставленным по указанному индексу
        int[] inserted = ArrayUtils.insert(2, numbers, 99);  // Вставляем 99 на позицию 2
        System.out.println(ArrayUtils.toString(inserted)); // {1,2,99,3,4,5} - элемент вставлен, остальные сдвинуты

        // Удаление элементов - создает новый массив без указанного элемента
        int[] removed = ArrayUtils.remove(numbers, 2);  // Удаляем элемент по индексу 2 (элемент со значением 3)
        System.out.println(ArrayUtils.toString(removed)); // {1,2,4,5} - новый массив без удаленного элемента

        // Реверс массива - изменяет исходный массив на месте (in-place операция)
        ArrayUtils.reverse(numbers);  // Переворачивает массив: первый становится последним и наоборот
        System.out.println(ArrayUtils.toString(numbers)); // {5,4,3,2,1} - массив перевернут

        // Клонирование - создает точную копию массива
        int[] clone = ArrayUtils.clone(numbers);  // Создаем копию массива
        System.out.println(ArrayUtils.isSameLength(numbers, clone)); // true - массивы одинаковой длины

        // Преобразования - конвертация типов массивов
        String[] strArray = ArrayUtils.toStringArray(numbers);  // Преобразуем int[] в String[]
        System.out.println(ArrayUtils.toString(strArray)); // {"5","4","3","2","1"} - массив строк
    }
}
```

### NumberUtils

Утилиты для работы с числами.

```java
import org.apache.commons.lang3.math.NumberUtils;

/
 * Демонстрация работы с NumberUtils из Apache Commons Lang
 * NumberUtils предоставляет утилитные методы для работы с числами и их преобразованиями
 */
public class NumberUtilsExample {

    /
     * Основной метод демонстрации различных операций с числами через NumberUtils
     */
    public static void main(String[] args) {
        // Создание чисел из строк - безопасное преобразование с обработкой ошибок
        int intValue = NumberUtils.toInt("123");              // Преобразует строку в int, возвращает 0 при ошибке
        int intWithDefault = NumberUtils.toInt("abc", 42);    // Преобразует строку в int, возвращает 42 при ошибке (дефолтное значение)
        double doubleValue = NumberUtils.toDouble("3.14");    // Преобразует строку в double, возвращает 0.0 при ошибке
        long longValue = NumberUtils.toLong("999999999");      // Преобразует строку в long, возвращает 0L при ошибке

        System.out.println("Int: " + intValue);           // 123 - успешное преобразование
        System.out.println("Int with default: " + intWithDefault); // 42 - использовано дефолтное значение т.к. "abc" не число
        System.out.println("Double: " + doubleValue);     // 3.14 - успешное преобразование
        System.out.println("Long: " + longValue);         // 999999999 - успешное преобразование

        // Сравнение чисел - нахождение максимального и минимального значения
        System.out.println(NumberUtils.max(1, 3, 2, 5, 4)); // 5 - максимальное значение из переданных чисел
        System.out.println(NumberUtils.min(1, 3, 2, 5, 4)); // 1 - минимальное значение из переданных чисел

        // Проверка строк на соответствие числовым форматам
        System.out.println(NumberUtils.isDigits("12345"));     // true - строка содержит только цифры
        System.out.println(NumberUtils.isNumber("3.14"));      // true - строка является валидным числом (включая десятичные)
        System.out.println(NumberUtils.isParsable("123"));     // true - строка может быть распарсена как число

        // Округление чисел - округление до указанного количества знаков после запятой
        double rounded = NumberUtils.round(3.14159, 2);  // Округляем до 2 знаков после запятой
        System.out.println(rounded); // 3.14 - округленное значение

        // Диапазоны - создание массива чисел в указанном диапазоне
        int[] range = NumberUtils.range(1, 5);  // Создает массив [1, 2, 3, 4, 5] (включительно)
        System.out.println(java.util.Arrays.toString(range)); // [1, 2, 3, 4, 5] - массив чисел от 1 до 5
    }
}
```

### RandomStringUtils и RandomUtils

Генерация случайных значений.

```java
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.RandomUtils;

public class RandomUtilsExample {

    public static void main(String[] args) {
        // Случайные строки
        String randomAlphabetic = RandomStringUtils.randomAlphabetic(10);
        String randomNumeric = RandomStringUtils.randomNumeric(5);
        String randomAlphanumeric = RandomStringUtils.randomAlphanumeric(8);

        System.out.println("Alphabetic: " + randomAlphabetic);
        System.out.println("Numeric: " + randomNumeric);
        System.out.println("Alphanumeric: " + randomAlphanumeric);

        // Случайные числа
        int randomInt = RandomUtils.nextInt(1, 100);
        long randomLong = RandomUtils.nextLong(1, 1000);
        double randomDouble = RandomUtils.nextDouble(0.0, 1.0);

        System.out.println("Random int: " + randomInt);
        System.out.println("Random long: " + randomLong);
        System.out.println("Random double: " + randomDouble);

        // Случайные массивы
        int[] randomInts = RandomUtils.nextInt(5, 1, 50);
        System.out.println(java.util.Arrays.toString(randomInts));

        // Случайная строка из символов
        String randomFromChars = RandomStringUtils.random(10, "ABC123");
        System.out.println("From chars: " + randomFromChars);
    }
}
```

### ClassUtils

Утилиты для работы с классами.

```java
import org.apache.commons.lang3.ClassUtils;

public class ClassUtilsExample {

    public static void main(String[] args) {
        Class<?> clazz = String.class;

        // Получение имени класса
        System.out.println(ClassUtils.getShortClassName(clazz));        // "String"
        System.out.println(ClassUtils.getSimpleName(clazz));            // "String"
        System.out.println(ClassUtils.getPackageName(clazz));           // "java.lang"

        // Проверка примитивов
        System.out.println(ClassUtils.isPrimitiveOrWrapper(int.class)); // true
        System.out.println(ClassUtils.isPrimitiveOrWrapper(Integer.class)); // true

        // Преобразование примитивов
        Class<?> wrapper = ClassUtils.primitiveToWrapper(int.class);
        Class<?> primitive = ClassUtils.wrapperToPrimitive(Integer.class);

        System.out.println("Wrapper: " + wrapper);     // class java.lang.Integer
        System.out.println("Primitive: " + primitive); // int

        // Иерархия классов
        java.util.List<Class<?>> superclasses = ClassUtils.getAllSuperclasses(clazz);
        System.out.println("Superclasses: " + superclasses);

        java.util.List<Class<?>> interfaces = ClassUtils.getAllInterfaces(clazz);
        System.out.println("Interfaces: " + interfaces);
    }
}
```

## Commons `IO`

Утилиты для работы с файлами и `IO` операциями.

### FileUtils

Операции с файлами.

```java
import org.apache.commons.io.FileUtils;
import java.io.File;
import java.io.IOException;

public class FileUtilsExample {

    public static void main(String[] args) throws IOException {
        File sourceFile = new File("source.txt");
        File destFile = new File("dest.txt");
        File directory = new File("testDir");

        // Чтение файла целиком
        String content = FileUtils.readFileToString(sourceFile, "UTF-8");
        System.out.println("File content: " + content);

        // Запись в файл
        FileUtils.writeStringToFile(destFile, "Hello, Commons IO!", "UTF-8");

        // Копирование файлов
        FileUtils.copyFile(sourceFile, destFile);

        // Копирование директорий
        FileUtils.copyDirectory(directory, new File("backup"));

        // Перемещение
        FileUtils.moveFile(sourceFile, new File("moved.txt"));

        // Удаление
        FileUtils.deleteDirectory(new File("temp"));
        FileUtils.forceDelete(destFile);

        // Проверка размера
        long size = FileUtils.sizeOf(sourceFile);
        System.out.println("File size: " + size + " bytes");

        // Проверка свободного места
        long freeSpace = FileUtils.getTempDirectory().getFreeSpace();
        System.out.println("Free space: " + freeSpace + " bytes");

        // Поиск файлов
        java.util.Collection<File> files = FileUtils.listFiles(directory,
            new String[]{"txt", "java"}, true);
        System.out.println("Found files: " + files.size());
    }
}
```

### IOUtils

Утилиты для работы с потоками.

```java
import org.apache.commons.io.IOUtils;
import java.io.*;
import java.nio.charset.StandardCharsets;

public class IOUtilsExample {

    public static void main(String[] args) throws IOException {
        // Чтение из InputStream
        try (InputStream is = new FileInputStream("data.txt")) {
            String content = IOUtils.toString(is, StandardCharsets.UTF_8);
            System.out.println("Content: " + content);
        }

        // Запись в OutputStream
        try (OutputStream os = new FileOutputStream("output.txt")) {
            IOUtils.write("Hello World", os, StandardCharsets.UTF_8);
        }

        // Копирование потоков
        try (InputStream is = new FileInputStream("source.txt");
             OutputStream os = new FileOutputStream("copy.txt")) {
            long bytesCopied = IOUtils.copy(is, os);
            System.out.println("Copied " + bytesCopied + " bytes");
        }

        // Чтение по строкам
        try (InputStream is = new FileInputStream("data.txt")) {
            java.util.List<String> lines = IOUtils.readLines(is, StandardCharsets.UTF_8);
            System.out.println("Lines: " + lines.size());
        }

        // Работа с byte arrays
        byte[] data = "Hello Commons IO".getBytes(StandardCharsets.UTF_8);
        String text = IOUtils.toString(data, StandardCharsets.UTF_8);
        System.out.println("From bytes: " + text);

        // Работа с Readers/Writers
        try (StringReader reader = new StringReader("Test data");
             StringWriter writer = new StringWriter()) {
            IOUtils.copy(reader, writer);
            System.out.println("Copied: " + writer.toString());
        }
    }
}
```

### FilenameUtils

Утилиты для работы с именами файлов.

```java
import org.apache.commons.io.FilenameUtils;

public class FilenameUtilsExample {

    public static void main(String[] args) {
        String path = "/home/user/documents/readme.txt";

        // Получение компонентов пути
        System.out.println(FilenameUtils.getName(path));              // "readme.txt"
        System.out.println(FilenameUtils.getBaseName(path));          // "readme"
        System.out.println(FilenameUtils.getExtension(path));         // "txt"
        System.out.println(FilenameUtils.getPath(path));              // "/home/user/documents/"
        System.out.println(FilenameUtils.getFullPath(path));          // "/home/user/documents/"

        // Нормализация пути
        String normalized = FilenameUtils.normalize("/home/user/../user/documents/./readme.txt");
        System.out.println("Normalized: " + normalized);             // "/home/user/documents/readme.txt"

        // Конкатенация путей
        String concat = FilenameUtils.concat("/home/user", "documents/readme.txt");
        System.out.println("Concatenated: " + concat);               // "/home/user/documents/readme.txt"

        // Проверка расширения
        System.out.println(FilenameUtils.isExtension(path, "txt"));   // true
        System.out.println(FilenameUtils.isExtension(path, new String[]{"txt", "java"})); // true

        // Удаление расширения
        String withoutExt = FilenameUtils.removeExtension(path);
        System.out.println("Without extension: " + withoutExt);      // "/home/user/documents/readme"

        // Сепараторы
        String unixPath = FilenameUtils.separatorsToUnix("\\home\\user\\readme.txt");
        System.out.println("Unix path: " + unixPath);                // "/home/user/readme.txt"

        String windowsPath = FilenameUtils.separatorsToWindows("/home/user/readme.txt");
        System.out.println("Windows path: " + windowsPath);          // "\home\user\readme.txt"
    }
}
```

## Commons Collections

Расширенные коллекции и утилиты.

### CollectionUtils

Утилиты для работы с коллекциями.

```java
import org.apache.commons.collections4.CollectionUtils;
import java.util.*;

public class CollectionUtilsExample {

    public static void main(String[] args) {
        List<String> list1 = Arrays.asList("a", "b", "c");
        List<String> list2 = Arrays.asList("b", "c", "d");
        List<String> empty = Collections.emptyList();

        // Проверка на пустоту
        System.out.println(CollectionUtils.isEmpty(empty));      // true
        System.out.println(CollectionUtils.isNotEmpty(list1));   // true

        // Размер коллекции с null-safety
        System.out.println(CollectionUtils.size(null));          // 0
        System.out.println(CollectionUtils.size(list1));         // 3

        // Операции над множествами
        Collection<String> union = CollectionUtils.union(list1, list2);
        System.out.println("Union: " + union);                   // [a, b, c, d]

        Collection<String> intersection = CollectionUtils.intersection(list1, list2);
        System.out.println("Intersection: " + intersection);     // [b, c]

        Collection<String> subtract = CollectionUtils.subtract(list1, list2);
        System.out.println("Subtract: " + subtract);             // [a]

        // Фильтрация
        Collection<String> filtered = CollectionUtils.select(list1,
            s -> s.length() > 1);
        System.out.println("Filtered: " + filtered);

        // Трансформация
        Collection<String> transformed = CollectionUtils.collect(list1,
            s -> s.toUpperCase());
        System.out.println("Transformed: " + transformed);       // [A, B, C]

        // Проверка условий
        boolean allMatch = CollectionUtils.exists(list1, s -> s.equals("a"));
        System.out.println("Contains 'a': " + allMatch);         // true
    }
}
```

### MapUtils

Утилиты для работы с **Map**.

```java
import org.apache.commons.collections4.MapUtils;
import java.util.*;

public class MapUtilsExample {

    public static void main(String[] args) {
        Map<String, Integer> map = new HashMap<>();
        map.put("one", 1);
        map.put("two", 2);
        map.put("three", 3);

        // Безопасное получение значений
        Integer value1 = MapUtils.getInteger(map, "one");
        Integer value2 = MapUtils.getInteger(map, "missing", 42); // default value
        System.out.println("Value1: " + value1 + ", Value2: " + value2);

        // Проверка на пустоту
        System.out.println(MapUtils.isEmpty(map));      // false
        System.out.println(MapUtils.isNotEmpty(map));   // true

        // Безопасная итерация
        MapUtils.safeAddToMap(map, "four", 4);
        System.out.println("Added to map: " + map);

        // Создание immutable map
        Map<String, Integer> immutable = MapUtils.unmodifiableMap(map);
        try {
            immutable.put("five", 5); // Вызовет исключение
        } catch (UnsupportedOperationException e) {
            System.out.println("Cannot modify immutable map");
        }

        // Инвертирование map
        Map<Integer, String> inverted = MapUtils.invertMap(map);
        System.out.println("Inverted: " + inverted); // {1=one, 2=two, 3=three, 4=four}
    }
}
```

### Bag

Коллекция, которая может содержать дубликаты с подсчетом количества.

```java
import org.apache.commons.collections4.Bag;
import org.apache.commons.collections4.HashBag;

public class BagExample {

    public static void main(String[] args) {
        Bag<String> bag = new HashBag<>();

        // Добавление элементов (с дубликатами)
        bag.add("apple", 3);
        bag.add("banana", 2);
        bag.add("orange");

        System.out.println(bag); // [apple:3, banana:2, orange:1]

        // Получение количества
        System.out.println("Apples: " + bag.getCount("apple"));     // 3
        System.out.println("Grapes: " + bag.getCount("grape"));     // 0

        // Уникальные элементы
        System.out.println("Unique: " + bag.uniqueSet());           // [apple, banana, orange]

        // Общее количество
        System.out.println("Total: " + bag.size());                 // 6

        // Удаление
        bag.remove("apple", 2);
        System.out.println("After removal: " + bag);                // [apple:1, banana:2, orange:1]

        // Проверка наличия
        System.out.println("Contains apple: " + bag.contains("apple")); // true
    }
}
```

### MultiMap

**Map**, которая может содержать несколько значений для одного ключа.

```java
import org.apache.commons.collections4.MultiMap;
import org.apache.commons.collections4.MultiValuedMap;
import org.apache.commons.collections4.multimap.ArrayListValuedHashMap;

public class MultiMapExample {

    public static void main(String[] args) {
        MultiValuedMap<String, String> multiMap = new ArrayListValuedHashMap<>();

        // Добавление значений
        multiMap.put("fruits", "apple");
        multiMap.put("fruits", "banana");
        multiMap.put("fruits", "orange");
        multiMap.put("vegetables", "carrot");
        multiMap.put("vegetables", "potato");

        System.out.println(multiMap);
        // {fruits=[apple, banana, orange], vegetables=[carrot, potato]}

        // Получение всех значений для ключа
        Collection<String> fruits = multiMap.get("fruits");
        System.out.println("Fruits: " + fruits); // [apple, banana, orange]

        // Все ключи
        System.out.println("Keys: " + multiMap.keySet()); // [fruits, vegetables]

        // Все значения
        System.out.println("Values: " + multiMap.values()); // [apple, banana, orange, carrot, potato]

        // Проверка
        System.out.println("Contains fruit 'apple': " + multiMap.containsMapping("fruits", "apple")); // true

        // Удаление
        multiMap.remove("fruits", "banana");
        System.out.println("After removal: " + multiMap.get("fruits")); // [apple, orange]
    }
}
```

## Commons Codec

Кодирование и декодирование данных.

### Base64

Кодирование **Base64**.

```java
import org.apache.commons.codec.binary.Base64;
import java.nio.charset.StandardCharsets;

public class Base64Example {

    public static void main(String[] args) {
        String text = "Hello, Commons Codec!";
        byte[] data = text.getBytes(StandardCharsets.UTF_8);

        // Кодирование
        byte[] encoded = Base64.encodeBase64(data);
        String encodedString = new String(encoded, StandardCharsets.UTF_8);
        System.out.println("Encoded: " + encodedString);

        // URL-safe кодирование
        byte[] urlSafe = Base64.encodeBase64URLSafe(data);
        String urlSafeString = new String(urlSafe, StandardCharsets.UTF_8);
        System.out.println("URL-safe: " + urlSafeString);

        // MIME кодирование (с переносами строк)
        byte[] mimeEncoded = Base64.encodeBase64Chunked(data);
        String mimeString = new String(mimeEncoded, StandardCharsets.UTF_8);
        System.out.println("MIME:\n" + mimeString);

        // Декодирование
        byte[] decoded = Base64.decodeBase64(encoded);
        String decodedText = new String(decoded, StandardCharsets.UTF_8);
        System.out.println("Decoded: " + decodedText);

        // Проверка на Base64
        System.out.println("Is Base64: " + Base64.isBase64(encodedString)); // true
    }
}
```

### Hex

Шестнадцатеричное кодирование.

```java
import org.apache.commons.codec.binary.Hex;
import java.nio.charset.StandardCharsets;

public class HexExample {

    public static void main(String[] args) {
        String text = "Hello World";
        byte[] data = text.getBytes(StandardCharsets.UTF_8);

        // Кодирование в hex
        char[] hexChars = Hex.encodeHex(data);
        String hexString = new String(hexChars);
        System.out.println("Hex: " + hexString);

        // Кодирование в hex с lowercase
        String hexLower = Hex.encodeHexString(data, false);
        System.out.println("Hex lowercase: " + hexLower);

        // Декодирование
        byte[] decoded = Hex.decodeHex(hexChars);
        String decodedText = new String(decoded, StandardCharsets.UTF_8);
        System.out.println("Decoded: " + decodedText);

        // Работа с частичными данными
        byte[] partialData = new byte[]{72, 101, 108}; // "Hel"
        String partialHex = Hex.encodeHexString(partialData);
        System.out.println("Partial hex: " + partialHex);
    }
}
```

### DigestUtils

Хэширование данных.

```java
import org.apache.commons.codec.digest.DigestUtils;

public class DigestUtilsExample {

    public static void main(String[] args) {
        String text = "Hello, Commons Codec!";

        // MD5
        String md5 = DigestUtils.md5Hex(text);
        System.out.println("MD5: " + md5);

        // SHA-1
        String sha1 = DigestUtils.sha1Hex(text);
        System.out.println("SHA-1: " + sha1);

        // SHA-256
        String sha256 = DigestUtils.sha256Hex(text);
        System.out.println("SHA-256: " + sha256);

        // SHA-512
        String sha512 = DigestUtils.sha512Hex(text);
        System.out.println("SHA-512: " + sha512);

        // Хэширование файла
        try {
            String fileMd5 = DigestUtils.md5Hex(new java.io.FileInputStream("data.txt"));
            System.out.println("File MD5: " + fileMd5);
        } catch (java.io.IOException e) {
            System.out.println("File not found");
        }

        // Постепенное хэширование
        DigestUtils digest = new DigestUtils(DigestUtils.getMd5Digest());
        digest.update("Hello");
        digest.update(", ");
        digest.update("World!");
        String incrementalMd5 = digest.digestAsHex();
        System.out.println("Incremental MD5: " + incrementalMd5);
    }
}
```

## Commons Compress

Работа с архивами.

### Zip

```java
import org.apache.commons.compress.archivers.zip.ZipArchiveEntry;
import org.apache.commons.compress.archivers.zip.ZipArchiveInputStream;
import org.apache.commons.compress.archivers.zip.ZipArchiveOutputStream;
import java.io.*;

public class ZipExample {

    public static void createZip(String sourceDir, String zipFile) throws IOException {
        try (ZipArchiveOutputStream zipOut = new ZipArchiveOutputStream(new FileOutputStream(zipFile))) {
            File source = new File(sourceDir);
            addToZip(zipOut, source, "");
        }
    }

    private static void addToZip(ZipArchiveOutputStream zipOut, File file, String basePath) throws IOException {
        String entryName = basePath + file.getName();
        if (file.isDirectory()) {
            // Добавляем директорию
            if (!basePath.isEmpty()) {
                zipOut.putArchiveEntry(new ZipArchiveEntry(entryName + "/"));
                zipOut.closeArchiveEntry();
            }
            // Рекурсивно добавляем содержимое
            File[] children = file.listFiles();
            if (children != null) {
                for (File child : children) {
                    addToZip(zipOut, child, entryName + "/");
                }
            }
        } else {
            // Добавляем файл
            zipOut.putArchiveEntry(new ZipArchiveEntry(entryName));
            try (FileInputStream fis = new FileInputStream(file)) {
                byte[] buffer = new byte[8192];
                int length;
                while ((length = fis.read(buffer)) > 0) {
                    zipOut.write(buffer, 0, length);
                }
            }
            zipOut.closeArchiveEntry();
        }
    }

    public static void extractZip(String zipFile, String destDir) throws IOException {
        try (ZipArchiveInputStream zipIn = new ZipArchiveInputStream(new FileInputStream(zipFile))) {
            ZipArchiveEntry entry;
            while ((entry = zipIn.getNextZipEntry()) != null) {
                File outputFile = new File(destDir, entry.getName());

                if (entry.isDirectory()) {
                    outputFile.mkdirs();
                } else {
                    outputFile.getParentFile().mkdirs();
                    try (FileOutputStream fos = new FileOutputStream(outputFile)) {
                        byte[] buffer = new byte[8192];
                        int length;
                        while ((length = zipIn.read(buffer)) > 0) {
                            fos.write(buffer, 0, length);
                        }
                    }
                }
            }
        }
    }

    public static void main(String[] args) throws IOException {
        createZip("sourceDir", "archive.zip");
        extractZip("archive.zip", "extracted");
        System.out.println("ZIP operations completed");
    }
}
```

## Commons CSV

Обработка **CSV** файлов.

```java
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVPrinter;
import org.apache.commons.csv.CSVRecord;
import java.io.*;
import java.nio.charset.StandardCharsets;

public class CSVExample {

    public static void writeCSV(String fileName) throws IOException {
        try (CSVPrinter printer = new CSVPrinter(new FileWriter(fileName),
             CSVFormat.DEFAULT.withHeader("Name", "Age", "City"))) {

            printer.printRecord("John Doe", 30, "New York");
            printer.printRecord("Jane Smith", 25, "London");
            printer.printRecord("Bob Johnson", 35, "Paris");
        }
    }

    public static void readCSV(String fileName) throws IOException {
        try (CSVParser parser = CSVParser.parse(new File(fileName),
             StandardCharsets.UTF_8, CSVFormat.DEFAULT.withFirstRecordAsHeader())) {

            for (CSVRecord record : parser) {
                String name = record.get("Name");
                String age = record.get("Age");
                String city = record.get("City");

                System.out.println("Name: " + name + ", Age: " + age + ", City: " + city);
            }
        }
    }

    public static void main(String[] args) throws IOException {
        String csvFile = "data.csv";

        // Запись CSV
        writeCSV(csvFile);
        System.out.println("CSV file created");

        // Чтение CSV
        readCSV(csvFile);

        // Работа с различными форматами
        CSVFormat excelFormat = CSVFormat.EXCEL;
        CSVFormat tdfFormat = CSVFormat.TDF; // Tab-delimited
        CSVFormat mysqlFormat = CSVFormat.MYSQL;

        // Кастомный формат
        CSVFormat customFormat = CSVFormat.newFormat(';')
            .withQuote('"')
            .withRecordSeparator("\r\n")
            .withFirstRecordAsHeader();
    }
}
```

## Другие полезные модули

### Commons Math

Математические функции и статистика.

```java
import org.apache.commons.math3.stat.StatUtils;
import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;
import org.apache.commons.math3.util.FastMath;

public class MathExample {

    public static void main(String[] args) {
        double[] data = {1.0, 2.0, 3.0, 4.0, 5.0};

        // Основная статистика
        System.out.println("Mean: " + StatUtils.mean(data));
        System.out.println("Variance: " + StatUtils.variance(data));
        System.out.println("Max: " + StatUtils.max(data));
        System.out.println("Min: " + StatUtils.min(data));

        // DescriptiveStatistics для более детального анализа
        DescriptiveStatistics stats = new DescriptiveStatistics();
        for (double value : data) {
            stats.addValue(value);
        }

        System.out.println("Standard Deviation: " + stats.getStandardDeviation());
        System.out.println("Median: " + stats.getPercentile(50));
        System.out.println("Skewness: " + stats.getSkewness());

        // Математические функции
        System.out.println("Sqrt(16): " + FastMath.sqrt(16));
        System.out.println("Pow(2, 3): " + FastMath.pow(2, 3));
        System.out.println("Sin(PI/2): " + FastMath.sin(Math.PI / 2));
    }
}
```

### Commons CLI

Парсинг командной строки.

```java
import org.apache.commons.cli.*;

public class CLIExample {

    public static void main(String[] args) {
        // Определение опций
        Options options = new Options();

        Option help = new Option("h", "help", false, "show help");
        options.addOption(help);

        Option file = Option.builder("f")
            .longOpt("file")
            .hasArg()
            .argName("file")
            .required()
            .desc("input file")
            .build();
        options.addOption(file);

        Option verbose = Option.builder("v")
            .longOpt("verbose")
            .desc("verbose output")
            .build();
        options.addOption(verbose);

        // Парсинг
        CommandLineParser parser = new DefaultParser();
        try {
            CommandLine cmd = parser.parse(options, args);

            if (cmd.hasOption("help")) {
                HelpFormatter formatter = new HelpFormatter();
                formatter.printHelp("myapp", options);
                return;
            }

            String inputFile = cmd.getOptionValue("file");
            boolean isVerbose = cmd.hasOption("verbose");

            System.out.println("Input file: " + inputFile);
            System.out.println("Verbose: " + isVerbose);

            // Обработка оставшихся аргументов
            String[] remainingArgs = cmd.getArgs();
            System.out.println("Remaining args: " + java.util.Arrays.toString(remainingArgs));

        } catch (ParseException e) {
            System.out.println("Error parsing command line: " + e.getMessage());

            HelpFormatter formatter = new HelpFormatter();
            formatter.printHelp("myapp", options);
        }
    }
}
```

## Интеграция с Spring Boot

### Автоматическая конфигурация

```java
@Configuration
public class CommonsConfig {

    // Пример использования Commons Lang в Spring
    @Bean
    public StringUtils stringUtils() {
        return new StringUtils();
    }

    // Пример кастомного конвертера с Commons Lang
    @Bean
    public Converter<String, LocalDate> stringToLocalDateConverter() {
        return new Converter<String, LocalDate>() {
            @Override
            public LocalDate convert(String source) {
                if (StringUtils.isBlank(source)) {
                    return null;
                }
                try {
                    return LocalDate.parse(source);
                } catch (DateTimeParseException e) {
                    throw new IllegalArgumentException("Invalid date format: " + source, e);
                }
            }
        };
    }
}

// Использование в сервисе
@Service
public class FileProcessingService {

    public void processFile(String filePath) throws IOException {
        File file = new File(filePath);

        // Проверка файла с Commons IO
        if (!file.exists()) {
            throw new IllegalArgumentException("File does not exist: " + filePath);
        }

        // Чтение файла с Commons IO
        String content = FileUtils.readFileToString(file, StandardCharsets.UTF_8);

        // Обработка с Commons Lang
        if (StringUtils.isNotBlank(content)) {
            String processed = StringUtils.trim(content);
            // Дальнейшая обработка...
        }
    }
}
```

## Best practices

### 1. Использование правильных модулей

```java
// ✅ Хорошо - использовать подходящие модули
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.io.FileUtils;
import org.apache.commons.collections4.CollectionUtils;

public class GoodUsageExample {

    public String processInput(String input) {
        // Commons Lang для строк
        if (StringUtils.isBlank(input)) {
            return null;
        }

        // Commons Lang для преобразований
        return StringUtils.trim(input).toLowerCase();
    }

    public void processFiles(List<File> files) throws IOException {
        // Commons IO для файлов
        for (File file : files) {
            if (file.exists()) {
                String content = FileUtils.readFileToString(file, StandardCharsets.UTF_8);
                // Обработка...
            }
        }
    }

    public boolean hasValidItems(List<String> items) {
        // Commons Collections для коллекций
        return CollectionUtils.isNotEmpty(items) &&
               items.stream().allMatch(StringUtils::isNotBlank);
    }
}

// ❌ Плохо - игнорировать возможности Commons
public class BadUsageExample {

    public String processInput(String input) {
        // Ручная проверка вместо StringUtils
        if (input == null || input.trim().isEmpty()) {
            return null;
        }
        return input.trim().toLowerCase();
    }
}
```

### 2. Null-safety

```java
// ✅ Хорошо - использование null-safe методов
@Service
public class SafeService {

    public User findUserById(String id) {
        if (StringUtils.isBlank(id)) {
            throw new IllegalArgumentException("ID cannot be blank");
        }

        // Поиск пользователя...
        return userRepository.findById(id)
            .orElseThrow(() -> new UserNotFoundException("User not found: " + id));
    }

    public String formatUserName(User user) {
        // Null-safe конкатенация
        return StringUtils.defaultString(user.getFirstName(), "") +
               StringUtils.defaultIfBlank(user.getLastName(), "");
    }

    public List<String> filterValidEmails(List<String> emails) {
        if (CollectionUtils.isEmpty(emails)) {
            return new ArrayList<>();
        }

        return emails.stream()
            .filter(Objects::nonNull)
            .map(String::trim)
            .filter(email -> isValidEmail(email))
            .collect(Collectors.toList());
    }

    private boolean isValidEmail(String email) {
        return StringUtils.isNotBlank(email) && email.contains("@");
    }
}
```

### 3. Работа с файлами

```java
// ✅ Хорошо - безопасная работа с файлами
public class FileService {

    private static final long MAX_FILE_SIZE = 10 * 1024 * 1024; // 10MB

    public void saveFile(InputStream inputStream, String fileName) throws IOException {
        File targetFile = new File("uploads", fileName);

        // Создание директории если не существует
        FileUtils.forceMkdirParent(targetFile);

        // Копирование с проверкой размера
        try (InputStream is = inputStream) {
            long size = IOUtils.copyLarge(is, new FileOutputStream(targetFile));
            if (size > MAX_FILE_SIZE) {
                FileUtils.deleteQuietly(targetFile);
                throw new IllegalArgumentException("File too large: " + size);
            }
        } catch (IOException e) {
            FileUtils.deleteQuietly(targetFile); // Очистка при ошибке
            throw e;
        }
    }

    public String readFileSafe(String filePath) {
        try {
            File file = new File(filePath);
            if (!file.exists() || !file.canRead()) {
                return null;
            }

            return FileUtils.readFileToString(file, StandardCharsets.UTF_8);
        } catch (IOException e) {
            // Логирование ошибки
            return null;
        }
    }
}
```

### 4. Кодирование и безопасность

```java
// ✅ Хорошо - безопасное кодирование
@Service
public class SecurityService {

    private static final String SALT = "MyAppSalt123";

    public String hashPassword(String password) {
        if (StringUtils.isBlank(password)) {
            throw new IllegalArgumentException("Password cannot be blank");
        }

        // Использование DigestUtils для хэширования
        String saltedPassword = password + SALT;
        return DigestUtils.sha256Hex(saltedPassword);
    }

    public String encodeData(String data) {
        if (StringUtils.isBlank(data)) {
            return data;
        }

        // Base64 кодирование
        byte[] encoded = Base64.encodeBase64(data.getBytes(StandardCharsets.UTF_8));
        return new String(encoded, StandardCharsets.UTF_8);
    }

    public String decodeData(String encodedData) {
        if (StringUtils.isBlank(encodedData)) {
            return encodedData;
        }

        try {
            // Base64 декодирование
            byte[] decoded = Base64.decodeBase64(encodedData);
            return new String(decoded, StandardCharsets.UTF_8);
        } catch (Exception e) {
            throw new IllegalArgumentException("Invalid encoded data");
        }
    }

    public boolean verifyPassword(String password, String hash) {
        String computedHash = hashPassword(password);
        return StringUtils.equals(computedHash, hash);
    }
}
```

### 5. Логирование и отладка

```java
// ✅ Хорошо - информативное логирование
@Slf4j
@Service
public class DataProcessingService {

    public void processData(List<String> data) {
        log.info("Starting data processing. Items count: {}", CollectionUtils.size(data));

        if (CollectionUtils.isEmpty(data)) {
            log.warn("No data to process");
            return;
        }

        int processed = 0;
        int errors = 0;

        for (String item : data) {
            try {
                if (StringUtils.isNotBlank(item)) {
                    processItem(item);
                    processed++;
                } else {
                    log.debug("Skipping blank item: {}", item);
                }
            } catch (Exception e) {
                log.error("Error processing item: {}", item, e);
                errors++;
            }
        }

        log.info("Data processing completed. Processed: {}, Errors: {}", processed, errors);
    }

    private void processItem(String item) {
        // Обработка элемента
        log.debug("Processing item: {}", StringUtils.abbreviate(item, 50));
    }
}
```


## Заключение

**Apache Commons** — это фундаментальная библиотека для **Java** проектов, которая значительно расширяет возможности стандартной библиотеки и упрощает разработку.

### Преимущества Apache Commons

1. **Широкая функциональность** — Более 50 различных модулей
2. **Высокое качество** — Тщательно протестированный код
3. **Backward compatibility** — Поддержка старых версий **Java**
4. **Активное развитие** — Регулярные обновления
5. **Модульная структура** — Добавляйте только нужные компоненты
6. **Apache License** — Свободная лицензия
7. **Широкое использование** — В тысячах проектов по всему миру
8. **Документация** — Подробная документация для каждого модуля

### Основные паттерны использования

1. **StringUtils паттерн** — Для всех операций со строками
2. **CollectionUtils паттерн** — Для безопасной работы с коллекциями
3. **FileUtils паттерн** — Для операций с файлами
4. **IOUtils паттерн** — Для работы с потоками
5. **DigestUtils паттерн** — Для хэширования и безопасности
6. **Base64 паттерн** — Для кодирования данных

### Когда использовать Apache Commons

**Рекомендуется:**
- **Enterprise** приложения
- Проекты с **intensive** строковой обработкой
- Приложения с файловыми операциями
- Системы с высокими требованиями к надежности
- Проекты с **legacy** кодом

**Особенно полезно:**
- При работе с **CSV** файлами (Commons CSV)
- При архивных операциях (Commons Compress)
- При математических расчетах (Commons Math)
- При парсинге командной строки (Commons CLI)

### Сравнение с альтернативами

| Библиотека | Преимущества | Недостатки |
|------------|-------------|------------|
| **Apache Commons** | Полная экосистема, зрелость | Большой выбор модулей |
| **Guava** | Современный **API**, **Google quality** | Меньше модулей |
| **Spring Utils** | Интеграция с **Spring** | Только для **Spring** проектов |
| **Custom utils** | Полный контроль | Требует разработки и тестирования |

**Apache Commons** является стандартом де-факто для большинства **enterprise Java** проектов и рекомендуется как обязательная зависимость для серьезных приложений.


[⬆ Наверх](../)

## См. также

- [Google Guava: Утилиты для Java](java-guava.md)
