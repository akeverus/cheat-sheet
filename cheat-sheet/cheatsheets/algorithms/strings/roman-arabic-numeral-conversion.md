# Roman-Arabic Numeral Conversion

Кратко: преобразование между римскими и арабскими цифрами. Рассматриваются алгоритмы для преобразования римских цифр в арабские и наоборот, включая обработку вычитающей записи.

**Дата последнего обновления:** 2025-01-15

## Полезные ссылки

### Официальная документация
- [GeeksforGeeks: Converting Roman Numerals to Decimal](https://www.geeksforgeeks.org/converting-roman-numerals-decimal-lying-1-3999/)

### См. также
- `./word-count.md` - подсчет слов
- `./caesar-cipher.md` - шифр Цезаря

## Содержание

- [Описание алгоритма](#описание-алгоритма)
- [Римские цифры](#римские-цифры)
- [Java Implementation](#java-implementation)
- [Kotlin Implementation](#kotlin-implementation)
- [Сложность](#сложность)

## Описание алгоритма

Древние римляне разработали собственную систему счисления, называемую римскими цифрами. Система использует буквы с разными значениями для представления чисел. Римские цифры до сих пор используются в некоторых второстепенных приложениях.

В этом уроке мы реализуем простые преобразователи, которые будут преобразовывать числа из одной системы в другую.

## Римские цифры

В римской системе у нас есть 7 символов, которые представляют числа:

1. I представляет 1
2. V представляет 5
3. X представляет 10
4. L представляет 50
5. C представляет 100
6. D представляет 500
7. M представляет 1000

### Вычитающая запись

Первоначально люди представляли 4 с IIII или 40 с XXXX. Это может быть довольно неудобно читать. Также легко принять четыре символа рядом друг с другом за три символа.

Римские цифры используют вычитающую запись, чтобы избежать таких ошибок. Вместо того, чтобы говорить «четыре раза один» (IIII), можно сказать, что на один меньше пяти (IV).

Насколько это важно с нашей точки зрения? Это важно, потому что вместо простого сложения чисел символ за символом нам может потребоваться проверить следующий символ, чтобы определить, нужно ли прибавлять или вычитать число.

### Enum для римских цифр

Давайте определим перечисление для представления римских цифр:

```java
enum RomanNumeral {
    I(1), IV(4), V(5), IX(9), X(10),
    XL(40), L(50), XC(90), C(100),
    CD(400), D(500), CM(900), M(1000);
    
    private int value;
    
    RomanNumeral(int value) {
        this.value = value;
    }
    
    public int getValue() {
        return value;
    }
    
    public static List<RomanNumeral> getReverseSortedValues() {
        return Arrays.stream(values())
            .sorted(Comparator.comparing((RomanNumeral e) -> e.value).reversed())
            .collect(Collectors.toList());
    }
}
```

Обратите внимание, что мы определили дополнительные символы, чтобы помочь с вычитающей записью. Мы также определили дополнительный метод с именем `getReverseSortedValues()`.

Этот метод позволит нам явно получить определенные римские цифры в порядке убывания значения.

## Java Implementation

### Преобразование римских в арабские

Римские цифры могут представлять только целые числа от 1 до 4000. Мы можем использовать следующий алгоритм для преобразования римской цифры в арабскую (перебор символов в обратном порядке от M до I):

```
LET numeral be the input String representing an Roman Numeral
LET symbol be initially set to RomanNumeral.values()[0]
WHILE numeral.length > 0:
    IF numeral starts with symbol's name:
        add symbol's value to the result
        remove the symbol's name from the numeral's beginning
    ELSE:
        set symbol to the next symbol
```

Далее мы можем реализовать алгоритм на Java:

```java
public static int romanToArabic(String input) {
    String romanNumeral = input.toUpperCase();
    int result = 0;
    
    List<RomanNumeral> romanNumerals = RomanNumeral.getReverseSortedValues();
    int i = 0;
    
    while ((romanNumeral.length() > 0) && (i < romanNumerals.size())) {
        RomanNumeral symbol = romanNumerals.get(i);
        
        if (romanNumeral.startsWith(symbol.name())) {
            result += symbol.getValue();
            romanNumeral = romanNumeral.substring(symbol.name().length());
        } else {
            i++;
        }
    }
    
    if (romanNumeral.length() > 0) {
        throw new IllegalArgumentException(input + " cannot be converted to a Roman Numeral");
    }
    
    return result;
}
```

Наконец, мы можем протестировать реализацию:

```java
@Test
void given2018Roman_WhenConvertingToArabic_ThenReturn2018() {
    String roman2018 = "MMXVIII";
    int result = RomanArabicConverter.romanToArabic(roman2018);
    assertThat(result).isEqualTo(2018);
}
```

### Альтернативная реализация

```java
public static int romanToArabicAlternative(String s) {
    Map<Character, Integer> map = new HashMap<>();
    map.put('I', 1);
    map.put('V', 5);
    map.put('X', 10);
    map.put('L', 50);
    map.put('C', 100);
    map.put('D', 500);
    map.put('M', 1000);
    
    int result = 0;
    int prevValue = 0;
    
    for (int i = s.length() - 1; i >= 0; i--) {
        int currentValue = map.get(s.charAt(i));
        if (currentValue < prevValue) {
            result -= currentValue;
        } else {
            result += currentValue;
        }
        prevValue = currentValue;
    }
    
    return result;
}
```

## Преобразование арабских в римские

Мы можем использовать следующий алгоритм для преобразования арабских цифр в римские (перебирая символы в обратном порядке от M до I):

```
LET number be an integer between 1 and 4000
LET symbol be RomanNumeral.values()[0]
LET result be an empty String
WHILE number > 0:
    IF symbol's value <= number:
        append the result with the symbol's name
        subtract symbol's value from number
    ELSE:
        pick the next symbol
```

Теперь мы можем реализовать алгоритм:

```java
public static String arabicToRoman(int number) {
    if ((number <= 0) || (number > 4000)) {
        throw new IllegalArgumentException(number + " is not in range (0,4000]");
    }
    
    List<RomanNumeral> romanNumerals = RomanNumeral.getReverseSortedValues();
    int i = 0;
    StringBuilder sb = new StringBuilder();
    
    while ((number > 0) && (i < romanNumerals.size())) {
        RomanNumeral currentSymbol = romanNumerals.get(i);
        
        if (currentSymbol.getValue() <= number) {
            sb.append(currentSymbol.name());
            number -= currentSymbol.getValue();
        } else {
            i++;
        }
    }
    
    return sb.toString();
}
```

Наконец, мы можем протестировать реализацию:

```java
@Test
void given1999Arabic_WhenConvertingToRoman_ThenReturnMCMXCIX() {
    int arabic1999 = 1999;
    String result = RomanArabicConverter.arabicToRoman(arabic1999);
    assertThat(result).isEqualTo("MCMXCIX");
}
```

### Альтернативная реализация

```java
public static String arabicToRomanAlternative(int num) {
    String[] thousands = {"", "M", "MM", "MMM"};
    String[] hundreds = {"", "C", "CC", "CCC", "CD", "D", "DC", "DCC", "DCCC", "CM"};
    String[] tens = {"", "X", "XX", "XXX", "XL", "L", "LX", "LXX", "LXXX", "XC"};
    String[] ones = {"", "I", "II", "III", "IV", "V", "VI", "VII", "VIII", "IX"};
    
    return thousands[num / 1000] +
           hundreds[(num % 1000) / 100] +
           tens[(num % 100) / 10] +
           ones[num % 10];
}
```

## Сложность

### Временная сложность

- **Римские → Арабские:** O(n), где n - длина римской строки
- **Арабские → Римские:** O(1) - константное время, так как максимальное число ограничено

### Пространственная сложность

- **Римские → Арабские:** O(1) - только константная память
- **Арабские → Римские:** O(1) - только константная память

## Особенности

- **Простота:** Алгоритмы просты для понимания
- **Эффективность:** Оба преобразования эффективны
- **Ограничения:** Работают только с числами от 1 до 4000

## Применение

Преобразование римских цифр используется в:

- Обработке исторических документов
- Образовательных приложениях
- Играх и головоломках
- Валидации данных
- Форматировании дат

## Варианты задачи

### Вариант 1: Валидация римских цифр

```java
public static boolean isValidRoman(String s) {
    if (s == null || s.isEmpty()) {
        return false;
    }
    
    String pattern = "^M{0,3}(CM|CD|D?C{0,3})(XC|XL|L?X{0,3})(IX|IV|V?I{0,3})$";
    return s.matches(pattern);
}
```

### Вариант 2: Преобразование с валидацией

```java
public static int romanToArabicWithValidation(String input) {
    if (!isValidRoman(input)) {
        throw new IllegalArgumentException("Invalid Roman numeral: " + input);
    }
    return romanToArabic(input);
}
```

### Вариант 3: Форматирование чисел

```java
public static String formatNumberAsRoman(int number) {
    if (number <= 0 || number > 4000) {
        return String.valueOf(number);
    }
    return arabicToRoman(number);
}
```

## Kotlin Implementation

### Enum для римских цифр

```kotlin
enum class RomanNumeralK(val value: Int) {
    I(1), IV(4), V(5), IX(9), X(10),
    XL(40), L(50), XC(90), C(100),
    CD(400), D(500), CM(900), M(1000);
    
    companion object {
        fun getReverseSortedValues(): List<RomanNumeralK> {
            return values().sortedByDescending { it.value }
        }
    }
}
```

### Преобразование римских в арабские

```kotlin
fun romanToArabicK(input: String): Int {
    var romanNumeral = input.uppercase()
    var result = 0
    val romanNumerals = RomanNumeralK.getReverseSortedValues()
    var i = 0
    
    while (romanNumeral.isNotEmpty() && i < romanNumerals.size) {
        val symbol = romanNumerals[i]
        
        if (romanNumeral.startsWith(symbol.name)) {
            result += symbol.value
            romanNumeral = romanNumeral.substring(symbol.name.length)
        } else {
            i++
        }
    }
    
    if (romanNumeral.isNotEmpty()) {
        throw IllegalArgumentException("$input cannot be converted to a Roman Numeral")
    }
    
    return result
}
```

### Альтернативная реализация с Map

```kotlin
fun romanToArabicAlternativeK(s: String): Int {
    val map = mapOf(
        'I' to 1, 'V' to 5, 'X' to 10, 'L' to 50,
        'C' to 100, 'D' to 500, 'M' to 1000
    )
    
    var result = 0
    var prevValue = 0
    
    for (i in s.length - 1 downTo 0) {
        val currentValue = map[s[i]] ?: 0
        if (currentValue < prevValue) {
            result -= currentValue
        } else {
            result += currentValue
        }
        prevValue = currentValue
    }
    
    return result
}
```

### Преобразование арабских в римские

```kotlin
fun arabicToRomanK(number: Int): String {
    if (number <= 0 || number > 4000) {
        throw IllegalArgumentException("$number is not in range (0,4000]")
    }
    
    val romanNumerals = RomanNumeralK.getReverseSortedValues()
    var num = number
    val result = StringBuilder()
    var i = 0
    
    while (num > 0 && i < romanNumerals.size) {
        val currentSymbol = romanNumerals[i]
        
        if (currentSymbol.value <= num) {
            result.append(currentSymbol.name)
            num -= currentSymbol.value
        } else {
            i++
        }
    }
    
    return result.toString()
}
```

### Альтернативная реализация с массивами

```kotlin
fun arabicToRomanAlternativeK(num: Int): String {
    val thousands = arrayOf("", "M", "MM", "MMM")
    val hundreds = arrayOf("", "C", "CC", "CCC", "CD", "D", "DC", "DCC", "DCCC", "CM")
    val tens = arrayOf("", "X", "XX", "XXX", "XL", "L", "LX", "LXX", "LXXX", "XC")
    val ones = arrayOf("", "I", "II", "III", "IV", "V", "VI", "VII", "VIII", "IX")
    
    return thousands[num / 1000] +
           hundreds[(num % 1000) / 100] +
           tens[(num % 100) / 10] +
           ones[num % 10]
}
```

### Валидация римских цифр

```kotlin
fun isValidRomanK(s: String?): Boolean {
    if (s.isNullOrEmpty()) {
        return false
    }
    
    val pattern = "^M{0,3}(CM|CD|D?C{0,3})(XC|XL|L?X{0,3})(IX|IV|V?I{0,3})$"
    return s.matches(pattern.toRegex())
}
```

### Пример использования

```kotlin
fun main() {
    // Римские → Арабские
    println(romanToArabicK("MMXVIII")) // 2018
    println(romanToArabicAlternativeK("MCMXCIX")) // 1999
    
    // Арабские → Римские
    println(arabicToRomanK(1999)) // "MCMXCIX"
    println(arabicToRomanAlternativeK(2018)) // "MMXVIII"
    
    // Валидация
    println(isValidRomanK("MMXVIII")) // true
    println(isValidRomanK("IIII")) // false
}
```

## Когда использовать

### Используйте enum подход, когда:

- Нужна читаемость
- Важна структурированность
- Работаете с ограниченным набором значений

### Используйте Map подход, когда:

- Нужна гибкость
- Простота реализации
- Работаете с небольшими числами

## Заключение

В этом уроке мы реализовали простые преобразователи, которые преобразуют числа из римской системы в арабскую и наоборот. Алгоритмы эффективны и просты для понимания, работая с числами от 1 до 4000.
