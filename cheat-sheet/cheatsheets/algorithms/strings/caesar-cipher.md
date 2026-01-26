# Caesar Cipher

Кратко: шифр Цезаря - это метод шифрования, который сдвигает буквы сообщения для создания другого, менее читаемого текста. Рассматриваются шифрование, расшифровка и взлом шифра с использованием статистики.

**Дата последнего обновления:** 2025-01-15

## Полезные ссылки

### Официальная документация
- [GeeksforGeeks: Caesar Cipher](https://www.geeksforgeeks.org/caesar-cipher/)

### См. также
- `./pangram-check.md` - проверка панграмм
- `./regex-token-replacement.md` - работа с регулярными выражениями

## Содержание

- [Описание алгоритма](#описание-алгоритма)
- [Java Implementation](#java-implementation)
- [Kotlin Implementation](#kotlin-implementation)
- [Взлом шифра](#взлом-шифра)
- [Сложность](#сложность)

## Описание алгоритма

В этом уроке мы собираемся изучить шифр Цезаря, метод шифрования, который сдвигает буквы сообщения для создания другого, менее читаемого.

Прежде всего, мы рассмотрим метод шифрования и посмотрим, как его реализовать на Java.

Затем мы увидим, как расшифровать зашифрованное сообщение, если мы знаем смещение, используемое для его шифрования.

И, наконец, мы узнаем, как взломать такой шифр и, таким образом, извлечь исходное сообщение из зашифрованного, не зная используемого смещения.

### Определение

Прежде всего, давайте определим, что такое шифр. Шифр - это метод шифрования сообщения с целью сделать его менее читаемым. Что касается шифра Цезаря, это шифр замены, который преобразует сообщение, сдвигая его буквы на заданное смещение.

Допустим, мы хотим сдвинуть алфавит на 3, тогда буква A будет преобразована в букву D, B в E, C в F и так далее.

### Пример соответствия

Вот полное соответствие между исходными и преобразованными буквами для смещения 3:

```
A B C D E F G H I J K L M N O P Q R S T U V W X Y Z
D E F G H I J K L M N O P Q R S T U V W X Y Z A B C
```

Как мы видим, как только преобразование выходит за пределы буквы Z, мы возвращаемся к началу алфавита, так что X, Y и Z преобразуются в A, B и C соответственно.

Следовательно, если мы выберем смещение больше или равное 26, мы зациклимся хотя бы один раз на всем алфавите. Давайте представим, что мы сдвинули сообщение на 28, что на самом деле означает, что мы сдвинули его на 2. Действительно, после сдвига на 26 все буквы совпадают друг с другом.

Действительно, мы можем преобразовать любое смещение в более простое смещение, выполнив над ним операцию по модулю 26:

```java
offset = offset % 26;
```

## Java Implementation

### Шифрование

Теперь давайте посмотрим, как реализовать шифр Цезаря на Java.

Во-первых, давайте создадим класс CaesarCipher, который будет содержать метод `cipher()`, принимающий сообщение и смещение в качестве параметров:

```java
public class CaesarCipher {
    String cipher(String message, int offset) {
        // Реализация
    }
}
```

Этот метод зашифрует сообщение с помощью шифра Цезаря.

Здесь мы предположим, что смещения положительны, а сообщения содержат только строчные буквы и пробелы. Затем мы хотим сдвинуть все буквенные символы на заданное смещение:

```java
StringBuilder result = new StringBuilder();

for (char character : message.toCharArray()) {
    if (character != ' ') {
        int originalAlphabetPosition = character - 'a';
        int newAlphabetPosition = (originalAlphabetPosition + offset) % 26;
        char newCharacter = (char) ('a' + newAlphabetPosition);
        result.append(newCharacter);
    } else {
        result.append(character);
    }
}

return result.toString();
```

Как мы видим, для достижения нашей цели мы полагаемся на коды ASCII букв алфавита.

Сначала мы вычисляем положение текущей буквы в алфавите, а для этого берем ее ASCII-код и вычитаем из него ASCII-код буквы a. Затем мы применяем смещение к этой позиции, осторожно используя модуль, чтобы оставаться в диапазоне алфавита. И, наконец, мы извлекаем новый символ, добавляя новую позицию в код ASCII буквы a.

### Примеры

Теперь давайте попробуем эту реализацию на сообщении «он сказал мне, что я никогда не смогу научить ламу водить машину» со смещением 3:

```java
CaesarCipher cipher = new CaesarCipher();
String cipheredMessage = cipher.cipher("he told me i could never teach a llama to drive", 3);
assertThat(cipheredMessage).isEqualTo("kh wrog ph l frxog qhyhu whdfk d oodpd wr gulyh");
```

Как мы видим, зашифрованное сообщение соответствует сопоставлению, определенному ранее для смещения 3.

Теперь этот конкретный пример имеет особенность не превышать букву z во время преобразования, поэтому не нужно возвращаться к началу алфавита. Таким образом, давайте попробуем еще раз со смещением 10, чтобы некоторые буквы были сопоставлены с буквами в начале алфавита, например, t, который будет сопоставлен с d:

```java
String cipheredMessage = cipher.cipher("he told me i could never teach a llama to drive", 10);
assertThat(cipheredMessage).isEqualTo("ro dyvn wo s myevn xofob dokmr k vvkwk dy nbsfo");
```

Он работает, как и ожидалось, благодаря операции по модулю. Эта операция также обрабатывает большие смещения. Допустим, мы хотим использовать 36 в качестве смещения, что эквивалентно 10, операция по модулю гарантирует, что преобразование даст тот же результат.

### Поддержка заглавных букв

Для поддержки заглавных букв:

```java
StringBuilder result = new StringBuilder();

for (char character : message.toCharArray()) {
    if (Character.isLetter(character)) {
        char base = Character.isUpperCase(character) ? 'A' : 'a';
        int originalAlphabetPosition = character - base;
        int newAlphabetPosition = (originalAlphabetPosition + offset) % 26;
        char newCharacter = (char) (base + newAlphabetPosition);
        result.append(newCharacter);
    } else {
        result.append(character);
    }
}

return result.toString();
```

## Расшифровка

Теперь давайте посмотрим, как расшифровать такое сообщение, когда мы знаем смещение, используемое для его шифрования.

Фактически, расшифровку сообщения, зашифрованного шифром Цезаря, можно рассматривать как его шифрование с отрицательным смещением или также с дополнительным смещением.

Итак, допустим, у нас есть сообщение, зашифрованное со смещением 3. Затем мы можем либо зашифровать его со смещением -3, либо зашифровать его со смещением 23. В любом случае мы получаем исходное сообщение.

К сожалению, наш алгоритм изначально не обрабатывает отрицательное смещение. У нас будут проблемы с преобразованием букв в конец алфавита (например, с преобразованием буквы a в букву z со смещением -1). Но мы можем вычислить дополнительное смещение, которое является положительным, а затем использовать наш алгоритм.

Итак, как получить это дополнительное смещение? Наивным способом сделать это было бы вычесть исходное смещение из 26. Конечно, это будет работать для смещений от 0 до 26, но в противном случае даст отрицательный результат.

Здесь мы снова воспользуемся оператором по модулю, непосредственно на исходном смещении, прежде чем выполнять вычитание. Таким образом, мы гарантируем, что всегда будет возвращаться положительное смещение.

Давайте теперь реализуем это на Java. Во-первых, мы добавим в наш класс метод `decipher()`:

```java
String decipher(String message, int offset) {
    return cipher(message, 26 - (offset % 26));
}
```

Все, наш алгоритм расшифровки настроен. Попробуем на примере со смещением 36:

```java
String decipheredSentence = cipher.decipher("ro dyvn wo s myevn xofob dokmr k vvkwk dy nbsfo", 36);
assertThat(decipheredSentence).isEqualTo("he told me i could never teach a llama to drive");
```

Как мы видим, мы получаем исходное сообщение.

## Kotlin Implementation

### Шифрование

```kotlin
fun cipherK(message: String, offset: Int): String {
    val normalizedOffset = offset % 26
    val result = StringBuilder()
    
    for (character in message) {
        if (character != ' ') {
            val originalAlphabetPosition = character - 'a'
            val newAlphabetPosition = (originalAlphabetPosition + normalizedOffset) % 26
            val newCharacter = 'a' + newAlphabetPosition
            result.append(newCharacter)
        } else {
            result.append(character)
        }
    }
    
    return result.toString()
}
```

### Поддержка заглавных букв

```kotlin
fun cipherWithCaseK(message: String, offset: Int): String {
    val normalizedOffset = offset % 26
    val result = StringBuilder()
    
    for (character in message) {
        if (character.isLetter()) {
            val base = if (character.isUpperCase()) 'A' else 'a'
            val originalAlphabetPosition = character - base
            val newAlphabetPosition = (originalAlphabetPosition + normalizedOffset) % 26
            val newCharacter = base + newAlphabetPosition
            result.append(newCharacter)
        } else {
            result.append(character)
        }
    }
    
    return result.toString()
}
```

### Расшифровка

```kotlin
fun decipherK(message: String, offset: Int): String {
    return cipherK(message, 26 - (offset % 26))
}
```

### Функциональный стиль

```kotlin
fun cipherFunctionalK(message: String, offset: Int): String {
    val normalizedOffset = offset % 26
    return message.map { char ->
        when {
            char == ' ' -> char
            char.isLetter() -> {
                val base = if (char.isUpperCase()) 'A' else 'a'
                val originalPosition = char - base
                val newPosition = (originalPosition + normalizedOffset) % 26
                base + newPosition
            }
            else -> char
        }
    }.joinToString("")
}
```

### ROT13 (частный случай)

```kotlin
fun rot13K(message: String): String {
    return cipherK(message, 13)
}
```

### Пример использования

```kotlin
fun main() {
    val message = "he told me i could never teach a llama to drive"
    
    // Шифрование
    val encrypted = cipherK(message, 3)
    println(encrypted) // "kh wrog ph l frxog qhyhu whdfk d oodpd wr gulyh"
    
    // Расшифровка
    val decrypted = decipherK(encrypted, 3)
    println(decrypted) // "he told me i could never teach a llama to drive"
    
    // ROT13
    val rot13Encrypted = rot13K(message)
    println(rot13Encrypted)
    
    // Функциональный стиль
    val functionalEncrypted = cipherFunctionalK(message, 10)
    println(functionalEncrypted)
}
```

## Взлом шифра

Теперь, когда мы рассмотрели шифрование и расшифровку сообщений с использованием шифра Цезаря, мы можем углубиться в то, как его взломать. То есть расшифровать зашифрованное сообщение, не зная сначала используемого смещения.

Для этого воспользуемся вероятностью найти английские буквы в тексте. Идея будет заключаться в том, чтобы расшифровать сообщение, используя смещения от 0 до 25, и проверить, какое смещение представляет собой распределение букв, аналогичное тому, которое используется в английских текстах.

Чтобы определить сходство двух распределений, мы будем использовать статистику Хи-квадрат.

Статистика хи-квадрат предоставит число, говорящее нам, похожи ли два распределения или нет. Чем меньше число, тем больше они похожи.

Итак, мы вычислим хи-квадрат для каждого смещения, а затем вернем значение с наименьшим хи-квадратом. Это должно дать нам смещение, используемое для шифрования сообщения.

Однако мы должны иметь в виду, что этот метод не является пуленепробиваемым, и если сообщение будет слишком коротким или в нем будут использованы слова, которые, к сожалению, не являются репрезентативными для стандартного английского текста, оно может вернуть неправильное смещение.

### Реализация

Давайте теперь посмотрим, как реализовать алгоритм взлома в Java.

Прежде всего, давайте создадим метод `breakCipher()` в нашем классе CaesarCipher, который будет возвращать смещение, используемое для шифрования сообщения:

```java
int breakCipher(String message) {
    // Реализация
}
```

Затем определим массив, содержащий вероятности найти определенную букву в английском тексте:

```java
double[] englishLettersProbabilities = {
    0.073, 0.009, 0.030, 0.044, 0.130, 0.028, 0.016, 0.035, 0.074,
    0.002, 0.003, 0.035, 0.025, 0.078, 0.074, 0.027, 0.003,
    0.077, 0.063, 0.093, 0.027, 0.013, 0.016, 0.005, 0.019, 0.001
};
```

Из этого массива мы сможем рассчитать ожидаемую частоту появления букв в заданном сообщении, умножив вероятности на длину сообщения:

```java
double[] expectedLettersFrequencies = Arrays.stream(englishLettersProbabilities)
    .map(probability -> probability * message.length())
    .toArray();
```

Например, в сообщении длиной 100 мы должны ожидать, что буква а появится 7.3 раза, а буква е - 13 раз.

Теперь мы собираемся вычислить хи-квадраты распределения букв расшифрованного сообщения и стандартного распределения английских букв.

Для этого нам потребуется импортировать библиотеку Apache Commons Math3, содержащую служебный класс для вычисления хи-квадратов:

```xml
<dependency>
    <groupId>org.apache.commons</groupId>
    <artifactId>commons-math3</artifactId>
    <version>3.6.1</version>
</dependency>
```

Что нам нужно сделать сейчас, так это создать массив, который будет содержать вычисленные хи-квадраты для каждого смещения от 0 до 25.

Таким образом, мы расшифруем зашифрованное сообщение, используя каждое смещение, а затем посчитаем буквы в этом сообщении.

Наконец, мы будем использовать метод `ChiSquareTest#chiSquare` для вычисления хи-квадрата между ожидаемым и наблюдаемым распределением букв:

```java
double[] chiSquares = new double[26];

for (int offset = 0; offset < chiSquares.length; offset++) {
    String decipheredMessage = decipher(message, offset);
    long[] lettersFrequencies = observedLettersFrequencies(decipheredMessage);
    double chiSquare = new ChiSquareTest().chiSquare(expectedLettersFrequencies, lettersFrequencies);
    chiSquares[offset] = chiSquare;
}

int probableOffset = 0;
for (int offset = 0; offset < chiSquares.length; offset++) {
    if (chiSquares[offset] < chiSquares[probableOffset]) {
        probableOffset = offset;
    }
}

return probableOffset;
```

Метод `observedLettersFrequencies()` просто подсчитывает количество букв от a до z в переданном сообщении:

```java
long[] observedLettersFrequencies(String message) {
    return IntStream.rangeClosed('a', 'z')
        .mapToLong(letter -> countLetter((char) letter, message))
        .toArray();
}

long countLetter(char letter, String message) {
    return message.chars()
        .filter(character -> character == letter)
        .count();
}
```

### Пример взлома

Давайте попробуем этот алгоритм на сообщении, зашифрованном с использованием смещения 10:

```java
int offset = algorithm.breakCipher("ro dyvn wo s myevn xofob dokmr k vvkwk dy nbsfo");
assertThat(offset).isEqualTo(10);
assertThat(algorithm.decipher("ro dyvn wo s myevn xofob dokmr k vvkwk dy nbsfo", offset))
    .isEqualTo("he told me i could never teach a llama to drive");
```

Как мы видим, метод извлекает правильное смещение, которое затем можно использовать для расшифровки сообщения и извлечения исходного.

## Сложность

### Временная сложность

- **Шифрование:** O(n), где n - длина сообщения
- **Расшифровка:** O(n), где n - длина сообщения
- **Взлом:** O(26 * n) = O(n) - для каждого смещения расшифровываем и анализируем

### Пространственная сложность

- **Шифрование/Расшифровка:** O(n) - для результата
- **Взлом:** O(1) - только константная память для статистики

## Особенности

- **Простота:** Легко понять и реализовать
- **Скорость:** Быстрое шифрование и расшифровка
- **Безопасность:** Низкая - легко взломать

## Применение

Шифр Цезаря используется в:

- Обучении криптографии
- Простых системах шифрования
- Играх и головоломках
- Исторических исследованиях

## Варианты задачи

### Вариант 1: Шифр с ключевым словом

```java
String cipherWithKeyword(String message, String keyword) {
    // Использование ключевого слова для создания смещения
}
```

### Вариант 2: ROT13

```java
String rot13(String message) {
    return cipher(message, 13);
}
```

## Когда использовать

### Используйте шифр Цезаря, когда:

- Нужна простота
- Безопасность не критична
- Обучаете криптографию

### НЕ используйте, когда:

- Нужна реальная безопасность
- Обрабатываете конфиденциальные данные
- Требуется стойкое шифрование

## Заключение

В этой статье мы рассмотрели шифр Цезаря. Мы научились шифровать и расшифровывать сообщение, сдвигая его буквы на заданное смещение. Мы также научились взламывать шифр. И мы видели все реализации Java, которые позволяют нам это делать.
