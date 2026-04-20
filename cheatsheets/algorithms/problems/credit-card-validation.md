---
title: "Валидация банковских карт (Credit Card Validation)"
description: "Определение типа карты по префиксу (регулярные выражения) и проверка номера алгоритмом Луна (Luhn) в Java."
tags:
  - algorithms
  - problems
  - credit-card-validation
difficulty: "intermediate"
prerequisites: []
next: []
updated: "2026-02-11"
---
# Валидация банковских карт (`Credit Card Validation`)

Определение типа карты по префиксу (регулярные выражения) и проверка номера алгоритмом Луна (Luhn) в Java.



## Полезные ссылки

### Официальная документация
- [`ISO/IEC` 7812` - `Identification cards`](https://www.iso.org/standard/70486.html)
- [`Luhn Algorithm` - `Wikipedia`](https://en.wikipedia.org/wiki/Luhn_algorithm)

### См. также
- [[regex-token-replacement|Работа с регулярными выражениями]]
- [Алгоритмы со строками](../strings/README.md)

## Содержание

- [Credit Card Validation](#credit-card-validation)
- [Обзор](#обзор)
- [Primary Account Number (PAN)](#primary-account-number-pan)
- [Major Industry Identifier](#major-industry-identifier)
- [Issuer Identification Number (IIN)](#issuer-identification-number-iin)
- [Identifying Card Issuers](#identifying-card-issuers)
  - [Visa Cards](#visa-cards)
  - [American Express Cards](#american-express-cards)
  - [Mastercard Cards](#mastercard-cards)
- [Card Number Structure](#card-number-structure)
- [Luhn Algorithm](#luhn-algorithm)
- [Algorithm Steps](#algorithm-steps)
- [Example Validation](#example-validation)
- [Реализация на Java](#реализация-на-java)
- [Реализация на Kotlin](#реализация-на-kotlin)
  - [Credit Card Validator](#credit-card-validator)
  - [Пример использования](#пример-использования)
- [Limitations](#limitations)
- [Лучшие практики](#лучшие-практики)
- [Заключение](#заключение)

## Обзор

**This article explains how** to **identify credit card types** by **matching patterns against the card number using regular expressions**, **and how** to **validate card numbers using the Luhn algorithm**.

**The Primary Account Number** (**PAN**) is **another name for the credit card number**. **PANs typically consist** `of 16` **digits**, **though the number** of **digits can vary depending** on **the card issuer**.

## Primary Account Number (PAN)

**The Primary Account Number** (**PAN**) is **the credit card number**. **Currently**, **the Issuer Identification Number** (**IIN**) is **the first six digits** of **the PAN**. It **consists** of **one leading digit followed** by **five digits**.

**Important: This** is **the current situation**, as it **may change** in **the future**. As **early** `as 2015`, **work began** to **increase the IIN** to **the first eight digits**.

## Major Industry Identifier

**The Major Industry Identifier** is **the first digit** of **the card number**. We **can look** at **the first digit** of **the card number** to **determine the industry** to **which the card belongs**:**

1. **1, 2** - **Airlines** (**among others**)
2. **3** - **Travel and entertainment**
3. **4, 5** - **Banking**
4. **6** - **Retail and banking**
5. **7** - **Fuel industry**
6. **8** - **Healthcare and telecommunications**
7. **9** - **National authorities**
8. **0** - **Other**, **reserved for future use**

## Issuer Identification Number (IIN)

**Since** `1989`, **there has been** an **international standard defining the order** of **PAN allocation**. **The official IIN registry** is **not publicly available**.

**Fortunately**, **most leading card issuers have well-known IIN ranges**, so we **can use regular expressions** to **match the IIN** to **the card issuer**.

**Note: The list** of **IIN ranges** is **constantly changing**. `If we`'re **writing** an **application for this**, we **need** to **think about how** we **plan** to **keep** it up to **date**.

`As an` **alternative**, we **could import one** of **several open-source libraries available that contain more card types and are more thoroughly tested than** we **could probably manage ourselves**. **For example**, **using the Stripe API means card processing** is **handled for** us.

## Identifying Card Issuers

### Visa Cards

**Visa card numbers start with the digit** 4, so a **simple regular expression** to **identify** a **Visa card would** be:**

```regex
^4[0-9]{0,}$
```

**Note that** in **our example** we're **not checking the length** of **the number**. We've **been assuming the card number** is **valid**, so we're **not checking length here**.

### American Express Cards

**American Express cards start with** 34 `or 37`, so we **can identify them with**:**

```regex
^3[47][0-9]{0,}$
```

### Mastercard Cards

**Some card issuers have** a **wider range** of **IINs**. We **found that Mastercard cards typically start with** 51-55, **however over the last decade they**'ve **introduced cards** in **the BIN range** 222100-272099.

**This gives** us **the regular expression**:**

```regex
^(5[1-5]|222[1-9]|22[3-9]|2[3-6]|27[01]|2720)[0-9]{0,}$
```

We **can use** a **similar pattern** to **identify cards from any card issuer whose IIN range** is **known**.

## Card Number Structure

**The full PAN consists** `of 3` **parts**:**

1. **Issuer `Identification Number` (**IIN**)**
2. **Individual `Account Identification` Number**
3. **Check digit**

**Between the IIN and the last digit**, we **have the Individual Account Identification Number**. **The issuer determines what these middle digits mean**, so **they will have different meanings for different issuers**. **They indicate information such** as **the account type associated with the card number**.

**The check digit** is **the last digit** of **the card number**. **The check digit allows** us to **use the Luhn algorithm** to **quickly identify** an **invalid card number**.

## Luhn Algorithm

**Hans Peter Luhn developed the Luhn algorithm** in **the late** 1950s. It's **used** to **generate every modern credit card number** we **use today**, **ensuring that each card number has** a **certain property**.

**The Luhn algorithm uses every digit** in **the card number**, **which means** we **can use** it to **easily determine when** a **given card number** is **invalid**, **even** if **only one digit** is **entered incorrectly**.

**This means** we **can limit the number** of **unnecessary card processing functions**. **This** is **especially important** if we're **charged per transaction** we **request**!

## Algorithm Steps

**Let**'s **look** at **the steps involved** in **using the Luhn algorithm** to **validate** a **given card number**.

We'll **need** to **take the full credit card number**, **including the IIN**.

**Starting from the rightmost digit**, we'll **add all the digits together**, **performing** a **special step for every second digit**.

**Since** we're **starting from the right**, we **need** to **iterate through the card number** in **reverse order**, **identifying every second digit**:**

```java
for (int i = cardNumber.length() - 1; i >= 0; i--) {
    int digit = Integer.parseInt(cardNumber.substring(i, i + 1));
    
    if ((cardNumber.length() - i) % 2 == 0) {
        digit = doubleAndSumDigits(digit);
    }
    
    sum += digit;
}
```

**For every second digit**, we **must double** it **and then sum the remaining digits**.

## Example Validation

**Let**'s **see how this works** on a **short example** `of 4` **digits** (**instead of the `usual 16` digits**) - **let**'s **check** if **the number** `8642` **would** be a **valid card number**.

**Starting from the rightmost digit**, we'll **double every second digit**:**

1. **For** 2 (**first digit from right**) - no **changes**.
2. **Then** we **double the second digit**, 4, to **get** 8.
3. **After that**, **the third digit**, 6 - no **changes**.
4. **Finally**, we **double the fourth digit** 8 to **get** 16.

If **doubling** a **digit results** in a **two-digit number**, **then** we **need** to do an **additional step** to **get back** to a **single digit** - we're **going** to **add those digits together** to **get** a **single-digit number**, so **for** 16, **this would** `be 1`+6=7.

**This step** is **equivalent** to **subtracting** 9, so we **can implement** it in **code** as **follows**:**

```java
private static int doubleAndSumDigits(int digit) {
    int ret = digit * 2;
    if (ret > 9) {
        ret = ret - 9;
    }
    return ret;
}
```

**Finally**, to **complete our example**, **let**'s **add all the numbers together**: 2 + 8 + 6 + 7 = 23.

If **the result** of **the Luhn algorithm** is **divisible** `by 10`, **the card number** is **possibly valid**.

**We'll **return this** as **the result** of **our validation**:**

```java
return sum % 10 == 0;
```

In **our case**, 23 is **not divisible** `by 10`, `so 8642` is **not** a **valid card number**.

In **our example**, **the last digit** 2 **would** be **the check digit**. **For real card numbers**, **the check digit** is **calculated using the Luhn algorithm**.

**For example**, if we **change the check digit** `to 9` to **get** `8649`, **then the result** of **the Luhn algorithm would** `be 30`, **which** is **divisible** `by 10`, `so 8649` **would pass our Luhn check above**.

## Реализация на Java

**Here**'s a **complete Java implementation** of **the Luhn algorithm**:**

```java
// Валидатор номера карты: проверка по алгоритму Луна и определение типа эмитента
public class CreditCardValidator {
    
    public static boolean isValid(String cardNumber) {
        if (cardNumber == null || cardNumber.isEmpty()) {
            return false;
        }
        
        // Remove spaces and non-digit characters
        cardNumber = cardNumber.replaceAll("[^0-9]", "");
        
        if (cardNumber.length() < 13 || cardNumber.length() > 19) {
            return false;
        }
        
        int sum = 0;
        boolean alternate = false;
        
        // Process digits from right to left
        for (int i = cardNumber.length() - 1; i >= 0; i--) {
            int digit = Character.getNumericValue(cardNumber.charAt(i));
            
            if (alternate) {
                digit = doubleAndSumDigits(digit);
            }
            
            sum += digit;
            alternate = !alternate;
        }
        
        return sum % 10 == 0;
    }
    
    private static int doubleAndSumDigits(int digit) {
        int doubled = digit * 2;
        if (doubled > 9) {
            return doubled - 9;
        }
        return doubled;
    }
    
    public static String identifyCardType(String cardNumber) {
        if (cardNumber == null || cardNumber.isEmpty()) {
            return "Unknown";
        }
        
        // Remove spaces and non-digit characters
        cardNumber = cardNumber.replaceAll("[^0-9]", "");
        
        if (cardNumber.matches("^4[0-9]{12,15}$")) {
            return "Visa";
        } else if (cardNumber.matches("^3[47][0-9]{13}$")) {
            return "American Express";
        } else if (cardNumber.matches("^(5[1-5]|222[1-9]|22[3-9]|2[3-6]|27[01]|2720)[0-9]{12,15}$")) {
            return "Mastercard";
        } else if (cardNumber.matches("^6(?:011|5[0-9]{2})[0-9]{12,15}$")) {
            return "Discover";
        }
        
        return "Unknown";
    }
}
```

## Реализация на Kotlin

### Credit Card Validator

```kotlin
object CreditCardValidatorK {
    fun isValid(cardNumber: String?): Boolean {
        if (cardNumber.isNullOrEmpty()) {
            return false
        }
        
        // Remove spaces and non-digit characters
        val cleaned = cardNumber.replace(Regex("[^0-9]"), "")
        
        if (cleaned.length < 13 || cleaned.length > 19) {
            return false
        }
        
        var sum = 0
        var alternate = false
        
        // Process digits from right to left
        for (i in cleaned.length - 1 downTo 0) {
            var digit = cleaned[i].digitToInt()
            
            if (alternate) {
                digit = doubleAndSumDigits(digit)
            }
            
            sum += digit
            alternate = !alternate
        }
        
        return sum % 10 == 0
    }
    
    private fun doubleAndSumDigits(digit: Int): Int {
        val doubled = digit * 2
        return if (doubled > 9) doubled - 9 else doubled
    }
    
    fun identifyCardType(cardNumber: String?): String {
        if (cardNumber.isNullOrEmpty()) {
            return "Unknown"
        }
        
        // Remove spaces and non-digit characters
        val cleaned = cardNumber.replace(Regex("[^0-9]"), "")
        
        return when {
            cleaned.matches(Regex("^4[0-9]{12,15}$")) -> "Visa"
            cleaned.matches(Regex("^3[47][0-9]{13}$")) -> "American Express"
            cleaned.matches(Regex("^(5[1-5]|222[1-9]|22[3-9]|2[3-6]|27[01]|2720)[0-9]{12,15}$")) -> "Mastercard"
            cleaned.matches(Regex("^6(?:011|5[0-9]{2})[0-9]{12,15}$")) -> "Discover"
            else -> "Unknown"
        }
    }
}
```

### Пример использования

```kotlin
fun main() {
    val cardNumber = "4532015112830366"
    
    if (CreditCardValidatorK.isValid(cardNumber)) {
        println("Card number is valid")
        println("Card type: ${CreditCardValidatorK.identifyCardType(cardNumber)}")
    } else {
        println("Card number is invalid")
    }
    
    // Example validation
    val testNumber = "8642"
    val isValid = CreditCardValidatorK.isValid(testNumber)
    println("Is $testNumber valid? $isValid")
}
```

## Limitations

Of **course**, **our check doesn**'t **mean that** `8649` is **definitely** a **valid card number**. **Even though** it **passes our check**, it **may not have been issued** as an **actual card** by **the corresponding card issuer**.

**The only way** we **can definitively confirm that** a **card number** is **real** is to **contact the card issuer**.

**The Luhn algorithm still provides** us **with** a **useful way** to **confirm that** a **given card number** is **definitely invalid**.

**However**, **there are** a **few edge cases where our Luhn check cannot detect** a **typo** in **the card number**. **Fortunately**, **these edge cases are rare enough that** we're **unlikely** to **ever encounter them** in **real life**.

**Finally**, **the Luhn algorithm doesn**'t **account for the length** of **the card number**. In **fact**, we **know that although** `8649` **passes our Luhn check**, it's **too short** to be a **real credit card number**.

We **can implement** an **additional check for the length** of **the card number**, **but** we **must remember that numbers from different card issuers can vary** in **length**.

## Лучшие практики

Алгоритм Луна не проверяет длину номера — добавьте проверку длины (обычно 13–19 цифр) и при необходимости диапазоны по типу карты (Visa, AmEx 15 и т.д.). Нормализуйте ввод: удаляйте пробелы и нецифровые символы перед проверкой. Списки BIN/IIN меняются; для продакшена используйте актуальную базу или API (например, Stripe); регулярные выражения держите в конфигурации. Не логируйте и не храните полные номера карт; для отладки маскируйте (например, только последние 4 цифры). В тестах покройте валидные/невалидные номера по Luhn, разные типы карт, пустую строку, null, некорректные символы.

## Решение проблем

| Симптом | Возможная причина | Решение |
|--------|-------------------|---------|
| Валидный по Luhn номер не принимается платёжной системой | Длина или префикс не соответствуют типу карты | Добавить проверку длины и префиксов по типу (Visa, Mastercard и т.д.) |
| Ложные срабатывания на неверный ввод | В строке пробелы или дефисы | Нормализовать: удалять всё, кроме цифр, перед валидацией |
| Тип карты не определяется | Устаревшие или неполные диапазоны IIN | Обновить регулярные выражения/BIN-список; при необходимости использовать внешний API |

## Частые вопросы

**Достаточно ли алгоритма Луна для приёма платежей?** Нет. Luhn проверяет только контрольную сумму; для приёма платежей нужна авторизация через платёжный шлюз. Luhn полезен для быстрой отсечки опечаток на форме ввода.

**Проверяет ли Luhn длину номера?** Нет. Нужно отдельно проверять длину (13–19 для большинства карт) и при необходимости соответствие типу карты (например, AmEx — 15 цифр).

**Можно ли по номеру определить банк?** Приблизительно — по BIN/IIN (первые 6–8 цифр). Точный список принадлежности BIN банкам нужно брать из платёжных систем или специализированных баз.


## Заключение

In **this article**, we've **covered what each part** of a **card number can tell** us **about** a **credit card account**.

**First**, we **learned how** to **identify card issuers** by **matching regular expression patterns against the first digits**. **Then** we **saw that** we'll **need issuer information** to **understand what the middle** of **the card number tells** us **about the account**. **Finally**, we **covered how the Luhn algorithm works and implemented some code** to **validate** a **given card number**.
