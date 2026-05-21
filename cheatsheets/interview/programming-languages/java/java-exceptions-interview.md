---
title: "Вопросы на собеседовании: Java Exceptions"
description: "Комплексное руководство по вопросам собеседования на тему Java Exceptions: иерархия исключений, checked vs unchecked, try-with-resources, кастомные исключения, обработка в многопоточном коде и best practices."
tags:
  - interview
  - programming-languages
  - java-exceptions-interview
type: "interview"
difficulty: "intermediate"
aliases:
  - "Вопросы на собеседовании"
  - "Java Exceptions"
  - "Java Exceptions interview"
  - "Java Exceptions собеседование"
prerequisites:
  - "[[java-exceptions]]"
next: []
updated: "2026-05-20"
mcq_format_version: 2
---
# Вопросы на собеседовании: `Java Exceptions`

Комплексное руководство по вопросам собеседования на тему `Java Exceptions` для `Senior Java Developer`. Включает иерархию исключений, механизмы обработки, `try-with-resources`, кастомные исключения, обработку в многопоточном коде, лямбдах и best practices.

## Полезные ссылки

### Официальная документация

- [Java Exceptions Tutorial](https://docs.oracle.com/javase/tutorial/essential/exceptions/) — официальный туториал Oracle
- [Throwable Hierarchy](https://docs.oracle.com/en/java/javase/17/docs/api/java.base/java/lang/Throwable.html) — Javadoc иерархии `Throwable`
- [Java Exceptions Interview Questions (Baeldung)](https://www.baeldung.com/java-exceptions-interview-questions) — вопросы с ответами
- [Exception Handling in Java (Baeldung)](https://www.baeldung.com/java-exceptions) — обзор обработки исключений
- [Java Try with Resources (Baeldung)](https://www.baeldung.com/java-try-with-resources) — `try-with-resources` в деталях
- [Create a Custom Exception in Java (Baeldung)](https://www.baeldung.com/java-new-custom-exception) — создание кастомных исключений

## Содержание

- [Полезные ссылки](#полезные-ссылки)
- [See also](#see-also)

**Основы и классификация исключений**
- [Q1. (!) Что такое `Exception` и какова иерархия исключений в Java?](#q1--что-такое-exception-и-какова-иерархия-исключений-в-java)
- [Q2. Какова цель ключевых слов `throw` и `throws`?](#q2-какова-цель-ключевых-слов-throw-и-throws)
- [Q3. Как обработать `Exception` с помощью `try-catch-finally`?](#q3-как-обработать-exception-с-помощью-try-catch-finally)
- [Q4. Как поймать несколько `Exceptions`?](#q4-как-поймать-несколько-exceptions)
- [Q5. (!) В чём разница между `Checked` и `Unchecked Exception`?](#q5--в-чём-разница-между-checked-и-unchecked-exception)
- [Q6. (!) В чём разница между `Exception` и `Error`?](#q6--в-чём-разница-между-exception-и-error)
- [Q7. Какой `Exception` будет выброшен при выполнении следующего кода?](#q7-какой-exception-будет-выброшен-при-выполнении-следующего-кода)

**Цепочка, стек и иерархия**
- [Q8. Что такое `Exception Chaining`?](#q8-что-такое-exception-chaining)
- [Q9. (!) Что такое `Stacktrace` и как она связана с `Exception`?](#q9--что-такое-stacktrace-и-как-она-связана-с-exception)
- [Q10. Зачем создавать подклассы `Exception`?](#q10-зачем-создавать-подклассы-exception)
- [Q11. Каковы преимущества механизма `Exceptions`?](#q11-каковы-преимущества-механизма-exceptions)

**`try-with-resources` и управление ресурсами**
- [Q12. (!) Что такое `try-with-resources` и как он работает?](#q12--что-такое-try-with-resources-и-как-он-работает)
- [Q13. (!) Что такое `Suppressed Exceptions` в `try-with-resources`?](#q13--что-такое-suppressed-exceptions-в-try-with-resources)
- [Q14. Что такое `addSuppressed()` и `getSuppressed()`?](#q14-что-такое-addsuppressed-и-getsuppressed)

**Исключения в лямбдах и переопределении методов**
- [Q15. (!) Как выбросить `Exception` внутри лямбда-выражения?](#q15--как-выбросить-exception-внутри-лямбда-выражения)
- [Q16. Как переопределить метод, выбрасывающий `Exception`?](#q16-как-переопределить-метод-выбрасывающий-exception)
- [Q17. Будет ли компилироваться следующий код?](#q17-будет-ли-компилироваться-следующий-код)
- [Q18. Есть ли способ выбросить checked `Exception` из метода без `throws`?](#q18-есть-ли-способ-выбросить-checked-exception-из-метода-без-throws)

**Проектирование и кастомные исключения**
- [Q19. (!) Когда создавать кастомное исключение и как его правильно оформить?](#q19--когда-создавать-кастомное-исключение-и-как-его-правильно-оформить)
- [Q20. Когда использовать `RuntimeException` vs checked `Exception`?](#q20-когда-использовать-runtimeexception-vs-checked-exception)
- [Q21. (!) Best practices при проектировании иерархии исключений](#q21--best-practices-при-проектировании-иерархии-исключений)

**Порядок выполнения и подводные камни**
- [Q22. Что такое `try-catch-finally` и каков порядок выполнения?](#q22-что-такое-try-catch-finally-и-каков-порядок-выполнения)
- [Q23. Что произойдёт, если в `finally` есть `return`?](#q23-что-произойдёт-если-в-finally-есть-return)
- [Q24. Как перехватить все исключения (`catch Throwable`)?](#q24-как-перехватить-все-исключения-catch-throwable)

**Многопоточность и исключения**
- [Q25. (!) Как обработать исключения в многопоточном коде?](#q25--как-обработать-исключения-в-многопоточном-коде)
- [Q26. Что такое `UncaughtExceptionHandler`?](#q26-что-такое-uncaughtexceptionhandler)

**Логирование, тестирование и диагностика**
- [Q27. (!) Как правильно логировать исключения?](#q27--как-правильно-логировать-исключения)
- [Q28. Как тестировать код, выбрасывающий исключения?](#q28-как-тестировать-код-выбрасывающий-исключения)
- [Q29. Что такое `getCause()` и `initCause()`?](#q29-что-такое-getcause-и-initcause)

**Специальные типы и продвинутые темы**
- [Q30. Что такое `AssertionError` и когда использовать `assert`?](#q30-что-такое-assertionerror-и-когда-использовать-assert)
- [Q31. Как обработать `OutOfMemoryError` и `StackOverflowError`?](#q31-как-обработать-outofmemoryerror-и-stackoverflowerror)
- [Q32. Что такое `fail-fast` и `fail-safe` в контексте исключений?](#q32-что-такое-fail-fast-и-fail-safe-в-контексте-исключений)
- [Q33. Как обрабатывать исключения в `Spring` (`@ExceptionHandler`, `@ControllerAdvice`)?](#q33-как-обрабатывать-исключения-в-spring-exceptionhandler-controlleradvice)
- [Q34. Антипаттерны обработки исключений](#q34-антипаттерны-обработки-исключений)

**Продвинутые темы**
- [Q35. Exception Chaining — initCause(), getCause(), addSuppressed()](#q35-exception-chaining--initcause-getcause-addsuppressed)
- [Q36. Multi-catch (Java 7) — синтаксис, ограничения](#q36-multi-catch-java-7--синтаксис-ограничения)
- [Q37. Try-with-resources с AutoCloseable — как работает, порядок закрытия](#q37-try-with-resources-с-autocloseable--как-работает-порядок-закрытия)
- [Q38. Custom Exceptions — best practices, serialVersionUID](#q38-custom-exceptions--best-practices-serializationuid)
- [Q39. Exception в lambda — как обрабатывать checked exceptions](#q39-exception-в-lambda--как-обрабатывать-checked-exceptions)
- [Q40. Логирование исключений — правила, что включать в message](#q40-логирование-исключений--правила-что-включать-в-message)
- [Q41. Performance исключений — стоимость fillInStackTrace, дорогие stack traces](#q41-performance-исключений--стоимость-fillinstacktrace-дорогие-stack-traces)
- [Q42. Global Exception Handling в Spring — @ControllerAdvice, ProblemDetail](#q42-global-exception-handling-в-spring--controlleradvice-problemdetail)

---

## Q1. (!) Что такое `Exception` и какова иерархия исключений в Java?

Исключение (`Exception`) — это ненормальное событие, возникающее во время выполнения программы и нарушающее нормальный поток инструкций. В Java все исключения являются объектами и наследуются от класса `Throwable`.

Иерархия исключений в Java:

```mermaid
graph TD
    Throwable["java.lang.Throwable"]
    Throwable --> Error["java.lang.Error"]
    Throwable --> Exception["java.lang.Exception"]

    Error --> OOM["OutOfMemoryError"]
    Error --> SOE["StackOverflowError"]
    Error --> NCDFE["NoClassDefFoundError"]

    Exception --> RE["RuntimeException<br/>(unchecked)"]
    Exception --> IOE["IOException<br/>(checked)"]
    Exception --> SQLE["SQLException<br/>(checked)"]

    RE --> NPE["NullPointerException"]
    RE --> IAE["IllegalArgumentException"]
    RE --> ISE["IllegalStateException"]
    RE --> AIOOBE["ArrayIndexOutOfBoundsException"]
    RE --> CCE["ClassCastException"]

    IOE --> FNFE["FileNotFoundException"]

    style Error fill:#f66,stroke:#333
    style RE fill:#fc9,stroke:#333
    style IOE fill:#9cf,stroke:#333
    style SQLE fill:#9cf,stroke:#333
```

Ключевые моменты:
- `Throwable` — корень иерархии; имеет два прямых наследника: `Error` и `Exception`
- `Error` — серьёзные проблемы JVM, от которых обычно невозможно восстановиться
- `Exception` → **checked** (проверяемые): компилятор требует обработки
- `RuntimeException` → **unchecked** (непроверяемые): обработка не обязательна

> [!mcq] Какой класс является корнем иерархии исключений в Java и кто его прямые наследники?
>
> - [ ] A. Корнем иерархии исключений является `Exception`, от которого напрямую наследуются `Error` и `RuntimeException`.
>
>     **Что на самом деле.** Корень иерархии — `Throwable`, а `Error` и `Exception` — две его независимые прямые ветки. `RuntimeException` — подкласс `Exception`, а не его сосед.
>
>     **Откуда путаница.** В обиходе говорят «поймать исключение» имея в виду `Exception`, и кажется будто это и есть верхний уровень. Туториалы часто рисуют `Exception` в центре схемы, опуская `Throwable` как «технический корень».
>
>     **Если бы это было правдой.** Код `try { riskyCode(); } catch (Exception e) { ... }` ловил бы и `OutOfMemoryError`, и `StackOverflowError`. На практике после такого catch продолжать работу нельзя, но разработчик ждёт «универсальной защиты» — сервис тихо деградирует, метрика availability падает к утру.
>
>     **Как было бы правильно.** Признать `Throwable` корнем и две независимые ветки `Error` (JVM-уровень) и `Exception` (прикладной), а `RuntimeException` — подклассом `Exception`.
>
> - [ ] B. Корнем иерархии исключений является `RuntimeException`, от которого наследуются `Error` и `Exception`.
>
>     **Что на самом деле.** `RuntimeException extends Exception extends Throwable`. Через `throw` можно бросить только наследников `Throwable` — это его единственный смысл существования.
>
>     **Откуда путаница.** `RuntimeException` действительно «самый используемый» тип в Spring-приложениях (`IllegalArgumentException`, `IllegalStateException`), и для middle-разработчика он кажется «главным».
>
>     **Если бы это было правдой.** Тогда `catch (RuntimeException)` ловил бы `IOException` и `OutOfMemoryError` — но компилятор такого не позволит, потому что они вне иерархии `RuntimeException`.
>
>     **Как было бы правильно.** Поменять направление: `RuntimeException` — лист в дереве, корень — `Throwable`.
>
> - [x] C. Корнем иерархии исключений является `Throwable`, от которого наследуются две независимые ветки: `Error` и `Exception`.
>
>     **Развёрнутое объяснение.** `Throwable` — общий предок и единственный тип, который можно бросать через `throw` или ловить через `catch`. От него идут две независимые ветки: `Error` (для серьёзных проблем JVM — OOM, SOE, NoClassDefFoundError) и `Exception` (для прикладных ошибок). Внутри `Exception` особое место занимает `RuntimeException` — он наследует `Exception`, но компилятор не требует его декларации в `throws`. Все методы для работы с исключением (`getMessage`, `getCause`, `getStackTrace`, `addSuppressed`) определены в `Throwable`.
>
>     **Пример.** Spring `@ExceptionHandler(Throwable.class)` в `@ControllerAdvice` — last-resort fallback, который ловит и `Exception`, и `Error` для логирования перед возвратом 500 клиенту. В `Thread.setDefaultUncaughtExceptionHandler` сигнатура принимает `Throwable`, потому что в верхнем обработчике потока может прилететь и `OutOfMemoryError`.
>
>     **Когда применять.** Запомнить иерархию полезно при проектировании `@ExceptionHandler` в Spring, при чтении документации `ExecutorService.submit` (где `Future.get()` оборачивает любой `Throwable` в `ExecutionException`), при работе с `Thread.UncaughtExceptionHandler`.
>
>     **Подводные камни.** `Throwable` — слишком широкий тип для catch в бизнес-коде; `catch (Throwable)` затрагивает и `OutOfMemoryError`, после которого продолжать работу опасно. Используйте `Throwable` только на границах системы (top-level worker, default handler).
>
>     **Связанные вопросы.** [[java-exceptions-interview#Q5]] checked vs unchecked; [[java-exceptions-interview#Q6]] Exception vs Error; [[java-exceptions-interview#Q24]] catch Throwable на границе системы.
>
> - [ ] D. Корнем иерархии исключений является `Object`, от которого напрямую наследуются `Exception` и `Error` как обычные классы.
>
>     **Что на самом деле.** Все классы в Java наследуются от `Object`, но специализированная иерархия исключений стартует именно с `Throwable extends Object`. Только `Throwable` и его потомки могут бросаться через `throw`.
>
>     **Откуда путаница.** Java-новички помнят правило «всё наследуется от Object» и переносят это на специальную иерархию исключений.
>
>     **Если бы это было правдой.** `throw new Object()` компилировался бы. На деле компилятор выдаёт ошибку «incompatible types: Object cannot be converted to Throwable», новичок теряет полчаса на поиск «как бросить любой объект».
>
>     **Как было бы правильно.** Точнее: «`Throwable extends Object`, а специализированная exception-иерархия начинается именно с `Throwable`».

> [!mcq] Что определяет, является исключение checked или unchecked?
>
> - [ ] A. Все прямые наследники `Throwable` являются checked, а unchecked становятся только подклассы `RuntimeException`; `Error` относится к checked-ветке.
>
>     **Что на самом деле.** Unchecked — это `RuntimeException`+потомки и `Error`+потомки; всё остальное в иерархии `Exception` — checked. `Error` принципиально unchecked, несмотря на то, что не наследуется от `RuntimeException`.
>
>     **Откуда путаница.** Логика «`RuntimeException` — особый случай, всё остальное обычное» приводит к мысли, что и `Error` под общим правилом. На деле есть два независимых unchecked-острова.
>
>     **Если бы это было правдой.** Код `void load() throws Error` был бы обязателен; компилятор подсвечивал бы как ошибку отсутствие декларации `throws OutOfMemoryError` в любом методе с new-аллокацией.
>
>     **Как было бы правильно.** Запомнить две unchecked-ветки: `RuntimeException` и `Error`. Всё остальное — checked.
>
> - [ ] B. `Exception` — checked по факту наследования, поэтому `RuntimeException` как подкласс `Exception` тоже автоматически checked.
>
>     **Что на самом деле.** `RuntimeException` — явное исключение из правила: он `extends Exception`, но компилятор НЕ требует декларации `throws`. Это специально прописано в JLS §11.1.1 для эргономики языка.
>
>     **Откуда путаница.** Кажется, что наследование должно «передавать» свойство checked сверху вниз — как обычно работают модификаторы доступа или абстрактность. Для checked/unchecked иерархия работает не так.
>
>     **Если бы это было правдой.** Каждый метод, который может бросить `NullPointerException`, обязан декларировать `throws NullPointerException` — то есть почти все методы Java. Чем хорошим программам пришлось бы оправдывать `throws Exception` в сигнатуре `main()`.
>
>     **Как было бы правильно.** `RuntimeException` — явное архитектурное исключение из правила «всё что `extends Exception` — checked»; компилятор смотрит не на наследование, а на конкретный класс.
>
> - [ ] C. Checked/unchecked определяется аннотацией `@Checked` или `@Unchecked` на классе исключения, а наследование роли не играет.
>
>     **Что на самом деле.** Таких аннотаций в Java нет. Категория полностью определяется иерархией наследования относительно `RuntimeException` и `Error`.
>
>     **Откуда путаница.** В современной Java многое управляется аннотациями (`@Override`, `@Deprecated`, `@SuppressWarnings`), и кажется логичным иметь и для исключений. В Kotlin, наоборот, есть `@Throws` для совместимости с Java.
>
>     **Если бы это было правдой.** Разработчик помечал бы кастомное исключение `@Unchecked` и забывал про `throws`. На деле час уходит на поиски этой несуществующей аннотации, в итоге пишется `@SneakyThrows`-обвязка или просто `extends RuntimeException`.
>
>     **Как было бы правильно.** Сменить наследование с `extends Exception` на `extends RuntimeException` — это и есть «аннотация» в Java-терминах.
>
> - [x] D. Checked/unchecked определяется конкретным местом в иерархии: всё кроме `RuntimeException`+потомков и `Error`+потомков — checked, даже несмотря на общий корень `Throwable`.
>
>     **Развёрнутое объяснение.** Компилятор смотрит на конкретный класс выбрасываемого исключения. `IOException extends Exception` — checked, требует `try-catch` или `throws`. `NullPointerException extends RuntimeException extends Exception` — unchecked, обработка опциональна. `OutOfMemoryError extends VirtualMachineError extends Error` — unchecked. Существует два «острова» unchecked: `RuntimeException`+потомки и `Error`+потомки; всё, что между ними (`Exception` напрямую, `IOException`, `SQLException`, `ClassNotFoundException`) — checked.
>
>     **Пример.** В Spring Data `JpaRepository.findById()` бросает unchecked `DataAccessException`, потому что caller обычно не может осмысленно восстановиться после ошибки БД — лучше пробросить наверх до `@ControllerAdvice`. В стандартной библиотеке `Files.readString()` бросает checked `IOException`, потому что caller может реализовать retry или fallback.
>
>     **Когда применять.** При создании кастомного бизнес-исключения по Spring-стилю — наследовать от `RuntimeException`. От `Exception` (checked) — только если caller обязан явно решить, что делать: повторить, использовать fallback или эскалировать.
>
>     **Подводные камни.** Checked-иерархия в чужом API заставляет всю цепочку методов декларировать `throws` — это называется «exception pollution». Решение — оборачивать checked в unchecked на границе модуля через конструктор `new MyDomainException(msg, originalCheckedException)`.
>
>     **Связанные вопросы.** [[java-exceptions-interview#Q5]] checked vs unchecked в деталях; [[java-exceptions-interview#Q20]] когда выбрать checked, когда unchecked; [[java-exceptions-interview#Q6]] Exception vs Error.

## Q2. Какова цель ключевых слов `throw` и `throws`?

**`throws`** — объявляет в сигнатуре метода, что он может выбросить исключение. Обязывает вызывающий код обрабатывать checked-исключения:

```java
public void readFile(String path) throws IOException {
    Files.readAllBytes(Path.of(path));
}
```

**`throw`** — выбрасывает конкретный объект исключения, прерывая нормальный поток выполнения:

```java
public void setAge(int age) {
    if (age < 0) {
        throw new IllegalArgumentException("Возраст не может быть отрицательным: " + age);
    }
    this.age = age;
}
```

| Аспект | `throw` | `throws` |
|--------|---------|----------|
| Где используется | В теле метода | В сигнатуре метода |
| Что принимает | Объект исключения | Класс(ы) исключений |
| Количество | Одно исключение | Несколько через запятую |
| Обязательность | Программист решает | Компилятор требует для checked |

> [!mcq] В чём разница между ключевыми словами `throw` и `throws`?
>
> - [x] A. `throw` выбрасывает конкретный объект исключения в теле метода, а `throws` объявляет в сигнатуре метода потенциально выбрасываемые типы.
>
>     **Развёрнутое объяснение.** `throw` — это исполняемая инструкция: она принимает объект-наследника `Throwable` и немедленно прерывает нормальный поток выполнения, разворачивая стек до ближайшего подходящего `catch`. `throws` — это декларация в сигнатуре метода, перечисляющая через запятую типы checked-исключений, которые метод может бросить (явно или через делегирование). Компилятор использует `throws` для проверки контракта на стороне caller: если метод объявил `throws IOException`, caller обязан либо обработать через `catch`, либо протолкнуть выше своим `throws`. Для unchecked-исключений `throws` опционален и используется только как документация.
>
>     **Пример.** В Spring Framework `Assert.notNull(arg, "msg")` под капотом выполняет `if (arg == null) throw new IllegalArgumentException(msg)` — это `throw`. Метод `Files.readString(Path)` имеет сигнатуру `public static String readString(Path path) throws IOException` — это `throws`, информирующий caller, что нужно обработать `IOException`.
>
>     **Когда применять.** `throw` — для активного создания исключения при невалидном состоянии или входе. `throws` — для декларирования checked-исключений в API, чтобы caller был в курсе и обязан реагировать. В современном Spring-стиле `throws` встречается реже, потому что большинство доменных исключений — unchecked.
>
>     **Подводные камни.** При переопределении метода `throws` подчиняется правилам контравариантности: дочерний метод может только сужать список (LSP). Лишний `throws RuntimeException` или `throws Exception` в сигнатуре считается code smell и ругается IDE как «redundant».
>
>     **Связанные вопросы.** [[java-exceptions-interview#Q5]] checked vs unchecked; [[java-exceptions-interview#Q16]] переопределение методов с throws; [[java-exceptions-interview#Q18]] sneaky throw.
>
> - [ ] B. `throw` объявляет исключения в сигнатуре метода, а `throws` выбрасывает конкретный объект исключения внутри метода.
>
>     **Что на самом деле.** Всё перепутано: `throw` — это runtime-инструкция в теле метода, `throws` — compile-time декларация в сигнатуре. Синтаксис строгий: `throw new X()` и `void foo() throws X`.
>
>     **Откуда путаница.** Слова отличаются одной буквой `s`, и человек, не успевший набить руку, легко меняет их местами. На русском обе переводятся как «бросать».
>
>     **Если бы это было правдой.** Код `void foo() throw IOException { throws new IOException(); }` компилировался бы. На деле компилятор выдаёт две ошибки подряд: «expected `{`» в сигнатуре и «invalid statement» в теле — junior теряет 15 минут на «битву с компилятором».
>
>     **Как было бы правильно.** Запомнить мнемоническое правило: «`throw` — действие (один раз), `throws` — обещание (в сигнатуре, перечисление)».
>
> - [ ] C. `throw` используется только для unchecked-исключений, а `throws` — только для checked-исключений, и они никогда не комбинируются.
>
>     **Что на самом деле.** `throw` работает с любым `Throwable`, включая checked (`throw new IOException(...)`). Ограничение касается только `throws`: компилятор обязывает декларировать его для checked, для unchecked он опционален.
>
>     **Откуда путаница.** Из правила «checked требуют декларации» можно ошибочно вывести симметричное правило «unchecked не требуют декларации, значит и не выбрасываются через `throw`».
>
>     **Если бы это было правдой.** Код `throw new IOException("io")` внутри метода без `throws IOException` был бы запрещён всегда. На деле он запрещён только если caller не обработал, но сам синтаксис `throw new IOException(...)` корректен.
>
>     **Как было бы правильно.** `throw` универсален; `throws` обязателен только для checked в сигнатуре и его задача — информировать caller.
>
> - [ ] D. `throw` и `throws` — синонимы, которые можно использовать взаимозаменяемо в сигнатурах и теле метода.
>
>     **Что на самом деле.** Это разные ключевые слова с разным синтаксисом и семантикой; компилятор не примет `throw` в сигнатуре или `throws` в теле — это синтаксическая ошибка.
>
>     **Откуда путаница.** Похожее написание и общий корень «throw». Особенно подводит при копировании со StackOverflow без понимания контекста.
>
>     **Если бы это было правдой.** Можно было бы писать `void foo() throw IOException` и `throws new IOException()`. Компилятор отвергает оба варианта с сообщением «illegal start of expression» или «`{` expected».
>
>     **Как было бы правильно.** Признать их разными ключевыми словами: `throw` — оператор-инструкция, `throws` — модификатор сигнатуры метода.

## Q3. Как обработать `Exception` с помощью `try-catch-finally`?

Используя конструкцию `try-catch-finally`:

```java
try {
    // «Защищённый» код, который может выбросить исключение
    String content = Files.readString(Path.of("config.yml"));
} catch (NoSuchFileException ex) {
    // Обработка конкретного типа
    log.warn("Файл конфигурации не найден, используем значения по умолчанию", ex);
} catch (IOException ex) {
    // Обработка более общего типа
    log.error("Ошибка чтения конфигурации", ex);
} finally {
    // Выполняется ВСЕГДА — и при нормальном выходе, и при исключении
    log.info("Попытка чтения конфигурации завершена");
}
```

Правила:
- Блок `try` обязателен; `catch` и `finally` — хотя бы один из двух
- Блоки `catch` проверяются последовательно — **дочерний тип должен стоять раньше родительского**, иначе ошибка компиляции
- `finally` выполняется даже при `return` внутри `try` или `catch`

> [!mcq] В каком порядке проверяются блоки `catch` при обработке исключения?
>
> - [ ] A. Блоки `catch` проверяются в обратном порядке объявления, поэтому родительский тип должен стоять раньше дочернего для корректной обработки.
>
>     **Что на самом деле.** Блоки `catch` проверяются строго сверху вниз в порядке их написания. Если родительский тип (`Exception`) стоит раньше дочернего (`IOException`), компилятор выдаёт ошибку «unreachable catch block» — дочерний код никогда не выполнится.
>
>     **Откуда путаница.** Из принципа «более специфичное должно быть позже» в каких-то других языках или фреймворках (например, специализация в Scala pattern matching работает по приоритету). В Java логика обратная.
>
>     **Если бы это было правдой.** Код `try { ... } catch (Exception e) { } catch (IOException e) { }` компилировался бы и второй блок ловил бы конкретно `IOException`. На деле компилятор отвергает с ошибкой unreachable code, MR блокируется на CI.
>
>     **Как было бы правильно.** Перевернуть порядок: специфичные типы сверху, общие снизу. Тогда `catch (IOException)` ловит I/O-ошибки, а `catch (Exception)` — fallback для всего остального.
>
> - [ ] B. Блоки `catch` проверяются в произвольном порядке, определяемом JVM на основе фактического типа исключения в runtime.
>
>     **Что на самом деле.** Порядок строго детерминирован компилятором и закодирован в exception table байткода метода. JVM просто идёт по таблице сверху вниз и берёт первый подходящий `catch`.
>
>     **Откуда путаница.** В современных языках есть идея «pattern matching» с выбором лучшей альтернативы (Scala, Kotlin sealed classes). На Java исключения работают по правилу first-match.
>
>     **Если бы это было правдой.** Разработчик мог бы писать `catch (Exception)` сверху для «подстраховки», специфичные `catch (IOException)` ниже — и JVM сама выбрала бы нужный. На деле IOException никогда не сработает; в логах все ошибки видны как generic Exception, диагностика теряется.
>
>     **Как было бы правильно.** Принять детерминированный сверху-вниз порядок и явно ставить специфичные `catch` первыми.
>
> - [x] C. Блоки `catch` проверяются последовательно сверху вниз, поэтому дочерний тип должен стоять раньше родительского для корректной обработки.
>
>     **Развёрнутое объяснение.** JVM использует таблицу исключений метода (exception table в байткоде), которая хранит блоки в порядке их написания в исходнике. При выбросе исключения JVM идёт по таблице сверху вниз и выполняет первый подходящий `catch` — тот, чей объявленный тип совместим с типом выброшенного через `instanceof`. Компилятор статически проверяет, чтобы более специфичные типы шли раньше общих: если `catch (Exception)` стоит перед `catch (IOException)`, последний помечается как unreachable code и билд падает.
>
>     **Пример.** Spring `JdbcTemplate.translateException` ловит подтипы `SQLException` в строгом порядке: сначала `DataIntegrityViolationException` (нарушение constraint), потом `OptimisticLockingFailureException` (concurrent update), затем общий `DataAccessException` как fallback. В JDBC-коде каскад `catch (SQLIntegrityConstraintViolationException) → catch (SQLTransientException) → catch (SQLException)` позволяет различать retryable и постоянные сбои.
>
>     **Когда применять.** При написании любого `try-catch` с несколькими типами исключений: располагать специфичные исключения сверху, общие — снизу. При интеграции с внешними API (JDBC, HTTP-клиент) — первыми ловить retryable-ошибки (`SocketTimeoutException`), затем — постоянные (`ConnectException`, `UnknownHostException`), последним — общий `IOException`.
>
>     **Подводные камни.** Multi-catch (`catch (A | B)`) усложняет порядок: если в multi-catch есть `IOException`, а ниже стоит `catch (SocketTimeoutException)`, последний unreachable. Также порядок важен в Spring `@ExceptionHandler` внутри `@ControllerAdvice` — Spring сам сортирует handler-ы по специфичности, но при ручной композиции может потребоваться `@Order`.
>
>     **Связанные вопросы.** [[java-exceptions-interview#Q4]] multi-catch и порядок типов; [[java-exceptions-interview#Q22]] try-catch-finally порядок; [[java-exceptions-interview#Q33]] Spring @ExceptionHandler приоритеты.
>
> - [ ] D. Блоки `catch` проверяются параллельно, и JVM автоматически выбирает наиболее специфичный тип независимо от порядка объявления.
>
>     **Что на самом деле.** Никакой «параллельной» проверки нет — exception table обрабатывается строго последовательно, и специфичность задаёт программист через порядок объявления.
>
>     **Откуда путаница.** Современные IDE и static-analysis инструменты иногда подсказывают «наиболее специфичный handler», и кажется, что эта логика встроена в runtime.
>
>     **Если бы это было правдой.** Разработчик в hotfix менял бы местами `catch`-блоки без последствий, JVM «сама бы разобралась». На деле — `FileNotFoundException` ловится как `IOException` без специфичной обработки (нет «файл не найден, использую default»), пользователь получает generic error.
>
>     **Как было бы правильно.** Признать порядок детерминированным и важным; код compose-а должен явно ставить специфичные типы первыми.

## Q4. Как поймать несколько `Exceptions`?

Три способа:

**1. Несколько блоков `catch`** (порядок от более специфичного к общему):

```java
try {
    parseAndSave(data);
} catch (JsonParseException ex) {
    log.error("Невалидный JSON", ex);
} catch (IOException ex) {
    log.error("Ошибка ввода-вывода", ex);
}
```

**2. `Multi-catch` блок** (`Java 7+`) — один блок для нескольких несвязанных типов:

```java
try {
    parseAndSave(data);
} catch (JsonParseException | SQLException ex) {
    // ex неявно final; нельзя переприсвоить
    log.error("Ошибка обработки данных", ex);
}
```

**3. Общий `catch`** (антипаттерн — использовать осторожно):

```java
try {
    riskyOperation();
} catch (Exception ex) {
    // Перехватывает ВСЁ, включая непредвиденные RuntimeException
    log.error("Неожиданная ошибка", ex);
}
```

В `multi-catch` типы не могут быть связаны наследованием — `catch (IOException | FileNotFoundException ex)` не скомпилируется, т.к. `FileNotFoundException` — подкласс `IOException`.

> [!mcq] Какие ограничения действуют на multi-catch блок (`catch (A | B | C)`)?
>
> - [ ] A. В multi-catch переменная исключения mutable и её можно переприсваивать любым типом из объявленных через `|`.
>
>     **Что на самом деле.** Переменная исключения в multi-catch неявно `final` (effectively final по JLS §14.20). Попытка переприсваивания (`ex = new IOException("normalized")`) вызывает ошибку компиляции «multi-catch parameter ex may not be assigned».
>
>     **Откуда путаница.** В обычном single-catch (`catch (IOException ex)`) переменную теоретически можно переприсвоить, и кажется, что это распространяется и на multi-catch. До Java 7 этого ограничения не было — оно появилось вместе с multi-catch.
>
>     **Если бы это было правдой.** Разработчик мог бы написать `catch (A | B ex) { ex = new A(); throw ex; }`, но компилятор не знает, какой именно тип `ex` имеет после переприсваивания — нарушается type safety. Реальный код не компилируется, разработчик костыли костыли вокруг лишней переменной.
>
>     **Как было бы правильно.** Принять, что переменная в multi-catch effectively final; для «нормализации» создавать локальную переменную: `Throwable wrapped = new MyException(ex); throw wrapped;`.
>
> - [x] B. В multi-catch типы не могут быть связаны наследованием, а переменная исключения неявно `final` и не переприсваивается.
>
>     **Развёрнутое объяснение.** Multi-catch (Java 7+, JLS §14.20.1) накладывает два ограничения. Первое: типы должны быть disjoint — нельзя писать `catch (IOException | FileNotFoundException)`, потому что FileNotFoundException — наследник IOException, и упоминание обоих избыточно (компилятор: «Types in multi-catch must be disjoint»). Второе: переменная исключения effectively final — нельзя переприсвоить `ex = ...` внутри блока. Тип переменной в байткоде — наименьший общий супертип всех перечисленных (часто `Throwable` или `Exception`).
>
>     **Пример.** В DAO-слое Spring типичный паттерн `try { ... } catch (JsonProcessingException | SQLException e) { throw new RepositoryException("Failed to persist", e); }` — обе ошибки оборачиваются в доменное исключение с одинаковым handling-ом. В реактивном коде с WebClient часто видна конструкция `catch (WebClientResponseException | TimeoutException e) { return Mono.error(new ExternalServiceException(e)); }`.
>
>     **Когда применять.** Multi-catch уместен, когда несколько disjoint-типов имеют одинаковую логику обработки — wrapping в общий тип, логирование, fallback. Особенно полезен при работе с двумя независимыми API (JDBC и Jackson), где исключения не связаны иерархически.
>
>     **Подводные камни.** При добавлении нового подкласса в иерархию ловимого типа multi-catch может неожиданно стать unreachable (если новый подкласс попадает в другую часть multi-catch). Для проброса (`throw ex`) тип переменной — общий супертип, что может потребовать явного `throws` более широкого типа в сигнатуре метода.
>
>     **Связанные вопросы.** [[java-exceptions-interview#Q3]] порядок catch блоков; [[java-exceptions-interview#Q36]] multi-catch синтаксис; [[java-exceptions-interview#Q4]] способы поймать несколько Exceptions.
>
> - [ ] C. В multi-catch допускается указывать связанные наследованием типы, поскольку компилятор автоматически выберет более специфичный.
>
>     **Что на самом деле.** Связанные типы запрещены — `catch (IOException | FileNotFoundException)` не компилируется с сообщением «Types in multi-catch must be disjoint». Компилятор справедливо считает дочерний тип избыточным.
>
>     **Откуда путаница.** В обычной последовательности `catch`-блоков можно ловить и родителя, и ребёнка (в правильном порядке). Кажется, что multi-catch — синтаксический сахар для этого.
>
>     **Если бы это было правдой.** Код `catch (IOException | FileNotFoundException e)` компилировался бы, и компилятор «выбрал бы» более специфичный. На деле — code-review бракует MR с дублирующимися типами; разработчик 10 минут разбирается в сообщении «must be disjoint».
>
>     **Как было бы правильно.** Заменить на `catch (IOException e)` — IOException автоматически ловит FileNotFoundException как наследника.
>
> - [ ] D. В multi-catch можно ловить только unchecked-исключения, потому что checked-типы требуют отдельных блоков catch.
>
>     **Что на самом деле.** Multi-catch работает с любыми Throwable, включая checked. Ограничение только одно — на наследственные связи между типами в одном `catch`.
>
>     **Откуда путаница.** Checked-исключения требуют декларации в `throws` или обработки в `try-catch`, и кажется, что для них «более жёсткие правила» и multi-catch недоступен.
>
>     **Если бы это было правдой.** Разработчик плодил бы три одинаковых `catch (IOException) { wrap(e); } catch (SQLException) { wrap(e); } catch (JsonProcessingException) { wrap(e); }` — 30 строк копипасты с риском разойтись при правке.
>
>     **Как было бы правильно.** Использовать `catch (IOException | SQLException | JsonProcessingException e) { wrap(e); }` — один блок, единая логика.

## Q5. (!) В чём разница между `Checked` и `Unchecked Exception`?

| Критерий | `Checked Exception` | `Unchecked Exception` |
|----------|--------------------|-----------------------|
| Наследование | `Exception` (кроме `RuntimeException`) | `RuntimeException` и его подклассы |
| Проверка компилятором | Да — обязательно `try-catch` или `throws` | Нет — обработка опциональна |
| Типичная причина | Внешние условия (I/O, сеть, БД) | Ошибки программирования (логика, валидация) |
| Примеры | `IOException`, `SQLException`, `ClassNotFoundException` | `NullPointerException`, `IllegalArgumentException`, `ArrayIndexOutOfBoundsException` |
| Восстановимость | Предполагается, что можно восстановиться | Обычно указывает на баг в коде |

```java
// Checked — компилятор заставляет обрабатывать
public void readConfig() throws IOException {  // ОБЯЗАТЕЛЬНО
    Files.readString(Path.of("app.conf"));
}

// Unchecked — компилятор не требует обработки
public void process(String value) {
    Objects.requireNonNull(value); // бросает NullPointerException
}
```

На собеседовании важно упомянуть дискуссию о checked exceptions: многие фреймворки (включая `Spring`) предпочитают unchecked-исключения для снижения boilerplate-кода. В `Kotlin`, например, checked exceptions вообще отсутствуют (подробнее в [вопросах по Kotlin Exceptions](../kotlin/kotlin-exceptions-interview.md)).

> [!mcq] В чём ключевое различие между checked и unchecked исключениями?
>
> - [ ] A. Checked-исключения всегда unchecked в runtime, а unchecked-исключения не ловятся в блоках catch вообще.
>
>     **Что на самом деле.** Разделение чисто compile-time: checked требуют декларации/обработки, unchecked — нет. В runtime оба типа ведут себя одинаково — оба наследуют `Throwable`, оба ловятся через `catch`, оба разворачивают стек.
>
>     **Откуда путаница.** Слово «unchecked» воспринимается буквально — «не проверяется», и это переносится с compile-time на runtime.
>
>     **Если бы это было правдой.** Разработчик не писал бы `catch (NullPointerException)` веря, что «не поймается». На деле NPE отлично ловится; проблема в том, что в production такие ситуации идут через generic `@ExceptionHandler(Exception.class)` без специфичной диагностики.
>
>     **Как было бы правильно.** Compile-time vs runtime — это вопрос о том, кто проверяет (компилятор vs JVM), а не о том, ловится ли исключение вообще.
>
> - [x] B. Checked-исключения проверяются компилятором и требуют `try-catch` или `throws`, а unchecked (`RuntimeException` и наследники) — нет.
>
>     **Развёрнутое объяснение.** Compile-time проверка — ключевое архитектурное различие. Для checked-типов (`IOException`, `SQLException`, `ClassNotFoundException`) компилятор обязывает caller либо обернуть вызов в `try-catch`, либо объявить `throws` в своей сигнатуре. Идея была: «если ошибка восстановима, заставим разработчика явно решить, что с ней делать». Для unchecked (`RuntimeException`+потомки и `Error`+потомки) проверки нет — они считаются либо багами программиста (`NullPointerException`), либо невосстановимыми JVM-проблемами (`OutOfMemoryError`).
>
>     **Пример.** Spring и Hibernate сознательно делают все исключения unchecked (`DataAccessException`, `JpaSystemException`) — caller не обязан декларировать `throws` в каждом методе сервисного слоя. Это решение принято после того, как стало понятно: checked-исключения провоцируют антипаттерн «поймал и проглотил» (`catch (Exception e) {}`).
>
>     **Когда применять.** Знание различия критично при дизайне API. Если caller может и должен восстановиться (retry, fallback) — checked. Если ошибка означает баг программиста или невосстановима — unchecked. Современная практика (Spring, Kotlin без checked) склоняется к unchecked для большинства доменных исключений.
>
>     **Подводные камни.** Checked-исключения плохо сочетаются с лямбдами и Stream API — стандартные `Function`/`Consumer` не объявляют `throws`, поэтому checked в лямбде приходится оборачивать в `UncheckedIOException` или sneaky throw. Это одна из причин трендa на unchecked.
>
>     **Связанные вопросы.** [[java-exceptions-interview#Q1]] иерархия Throwable; [[java-exceptions-interview#Q20]] когда выбрать checked vs unchecked; [[java-exceptions-interview#Q39]] checked exceptions в lambda.
>
> - [ ] C. Checked-исключения наследуются от `Error`, а unchecked — от `Exception`, и эти ветки никогда не пересекаются.
>
>     **Что на самом деле.** Оба типа checked/unchecked живут внутри ветки `Exception` (плюс ветка `Error` целиком unchecked). Checked — это `Exception` и его потомки кроме `RuntimeException`; unchecked — `RuntimeException`+потомки и `Error`+потомки.
>
>     **Откуда путаница.** Поверхностное знание «есть Exception и есть Error» приводит к симметричной модели «один = checked, другой = unchecked».
>
>     **Если бы это было правдой.** Разработчик объявлял бы `class BizError extends Error` для «бизнес-сбоя». Spring `@ExceptionHandler(Exception.class)` его не поймал бы (Error не наследует Exception), и на проде клиент увидел бы голый stack trace в response.
>
>     **Как было бы правильно.** Запомнить: `Error` — это JVM-уровень (OOM, SOE), бизнес-исключения наследуют `RuntimeException` или `Exception` в зависимости от того, нужна ли compile-time проверка.
>
> - [ ] D. Checked-исключения можно бросать только в статических методах, а unchecked — только в инстанс-методах.
>
>     **Что на самом деле.** Статичность метода никак не связана с типом бросаемых исключений. Ограничения определяются только сигнатурой `throws` и иерархией исключения.
>
>     **Откуда путаница.** Не существует объективного источника путаницы — это полностью искусственное правило, которого нет в спецификации.
>
>     **Если бы это было правдой.** Разработчик переписывал бы дизайн, делая все DAO-методы статическими «для checked». Это ломает архитектуру: невозможно мокать в тестах через Mockito, нельзя инжектить зависимости через конструктор, теряется полиморфизм.
>
>     **Как было бы правильно.** Статичность — ортогональная характеристика. Любой метод (static, instance, abstract) может бросать любые типы исключений с любым сочетанием checked/unchecked.

> [!mcq] Что происходит с checked-исключениями из `close()` в `try-with-resources`?
>
> - [x] A. Если `close()` объявляет checked-исключение, `try-with-resources` обязывает его обработать или объявить в `throws` — даже если в `try` этого исключения не было.
>
>     **Развёрнутое объяснение.** `try-with-resources` компилируется в эквивалент `try-finally`, где `finally` неявно вызывает `close()` на каждом ресурсе в обратном порядке. Если интерфейс `AutoCloseable.close()` (или его наследник `Closeable.close()`) объявляет `throws IOException`, компилятор учитывает этот неявный вызов при проверке checked. Это означает, что метод, содержащий `try (BufferedReader r = new BufferedReader(new FileReader(path)))`, обязан либо обернуть всю конструкцию в `try-catch (IOException)`, либо декларировать `throws IOException` в сигнатуре, даже если тело `try` само по себе не бросает.
>
>     **Пример.** Любая работа с `BufferedReader`, `InputStream`, `FileChannel`, `Connection` в `try-with-resources` требует `throws IOException` (или `SQLException` для JDBC) в сигнатуре enclosing метода. Spring `JdbcTemplate.query()` под капотом использует `try (Connection conn = dataSource.getConnection(); PreparedStatement ps = conn.prepareStatement(...))` и оборачивает `SQLException` в unchecked `DataAccessException` для удобства caller.
>
>     **Когда применять.** При проектировании кастомного `AutoCloseable`: если ресурс действительно может бросить checked при закрытии (внешний сокет, файл) — объявить `throws IOException`. Если закрытие безопасно (логирование таймера, освобождение lock) — переопределить `close()` без `throws`, чтобы упростить использование.
>
>     **Подводные камни.** При множественных ресурсах в одном `try-with-resources` исключение из `close()` каждого ресурса может добавиться как suppressed к primary. Тип `throws` в сигнатуре должен покрывать все checked-типы из всех `close()` и из тела `try`.
>
>     **Связанные вопросы.** [[java-exceptions-interview#Q12]] try-with-resources механика; [[java-exceptions-interview#Q13]] suppressed exceptions; [[java-exceptions-interview#Q37]] AutoCloseable порядок закрытия.
>
> - [ ] B. В `try-with-resources` checked-исключение из `close()` затирается основным исключением `try`-блока, поэтому декларация `throws` не нужна.
>
>     **Что на самом деле.** Исключение из `close()` не затирается, а добавляется как suppressed к primary. Если в `try` исключения не было, исключение из `close()` становится primary и требует `throws` в сигнатуре.
>
>     **Откуда путаница.** До Java 7 в ручном `try-finally` исключение из `finally.close()` действительно затирало исключение из `try`. `try-with-resources` решил именно эту проблему через `addSuppressed`, но люди помнят старое поведение.
>
>     **Если бы это было правдой.** Разработчик убирал бы `throws IOException` из сигнатуры в надежде на «магическое затирание». На деле билд красный, демо у клиента отменяется.
>
>     **Как было бы правильно.** Признать, что `close()` участвует в checked-проверке наравне с телом `try`, и декларировать `throws` или ловить.
>
> - [ ] C. `try-with-resources` автоматически конвертирует все checked-исключения из `close()` в `RuntimeException`, поэтому `throws` никогда не нужен.
>
>     **Что на самом деле.** Никакой автоконвертации нет — оригинальный тип сохраняется. `IOException` из `close()` так и пробрасывается как `IOException`, а не как `RuntimeException`.
>
>     **Откуда путаница.** В стандартной библиотеке есть `UncheckedIOException` — обёртка вокруг `IOException`. Кажется, что `try-with-resources` делает обёртку автоматически.
>
>     **Если бы это было правдой.** Код `try (BufferedReader r = ...) { ... }` компилировался бы без `throws`, а в runtime caller ловил бы `RuntimeException`. На деле билд падает на этапе компиляции с «unhandled exception».
>
>     **Как было бы правильно.** Делать обёртку вручную: `try { ... } catch (IOException e) { throw new UncheckedIOException(e); }`.
>
> - [ ] D. `AutoCloseable.close()` всегда объявляет только unchecked-исключения, поэтому checked в этом контексте невозможны.
>
>     **Что на самом деле.** `AutoCloseable.close() throws Exception` — самый общий checked-тип в Java. `Closeable.close() throws IOException` — checked, более узкий вариант для I/O. Оба интерфейса допускают checked.
>
>     **Откуда путаница.** В коде с unchecked-семантикой (например, кастомный `try (MeasuredTimer t = ...)`) `close()` обычно без `throws`, и это создаёт впечатление общего правила.
>
>     **Если бы это было правдой.** Разработчик писал бы `throws SQLException` в `close()` своего ресурса, ожидая, что caller не обязан ловить. На деле — каждый caller вынужден оборачивать в `try-catch (SQLException)`, API замусорен.
>
>     **Как было бы правильно.** Прочитать Javadoc `AutoCloseable.close()`: «It is strongly advised to declare concrete implementations of the close method to throw more specific exceptions, or to throw no exception at all if the close operation cannot fail».

## Q6. (!) В чём разница между `Exception` и `Error`?

| Критерий | `Exception` | `Error` |
|----------|------------|---------|
| Наследование | `Throwable` → `Exception` | `Throwable` → `Error` |
| Восстановимость | Можно и нужно обрабатывать | Обычно невосстановимые |
| Источник | Логика приложения, внешние системы | JVM, системные ресурсы |
| Обработка | Рекомендуется | Не рекомендуется (кроме логирования) |

Основные `Error`:
- **`OutOfMemoryError`** — JVM не может выделить память, `GC` не помогает
- **`StackOverflowError`** — переполнение стека (глубокая рекурсия)
- **`NoClassDefFoundError`** — класс был доступен при компиляции, но не найден в runtime
- **`ExceptionInInitializerError`** — исключение в статическом инициализаторе
- **`UnsupportedClassVersionError`** — `.class` файл скомпилирован более новой версией Java

```java
// Пример StackOverflowError — бесконечная рекурсия
public int factorial(int n) {
    return n * factorial(n - 1); // нет базового условия!
}
```

> [!mcq] В чём принципиальное различие между `Exception` и `Error`?
>
> - [ ] A. `Error` сигнализирует о проблемах в бизнес-логике приложения, а `Exception` — о критических сбоях JVM, требующих перезапуска.
>
>     **Что на самом деле.** Разделение перевёрнуто: `Error` (`OutOfMemoryError`, `StackOverflowError`, `NoClassDefFoundError`) — это JVM-уровень, проблемы исполняющей среды; `Exception` — это прикладные ошибки в бизнес-логике и при взаимодействии с внешними системами.
>
>     **Откуда путаница.** Слово «Error» в обиходе звучит как «настоящая ошибка приложения» (как в HTTP 500 «server error»), а «Exception» — как «нештатная ситуация», что выглядит мельче.
>
>     **Если бы это было правдой.** Разработчик создавал бы `class OrderError extends Error` для бизнес-сбоя «заказ не найден». Spring `@ControllerAdvice(Exception.class)` его не поймал бы (Error не наследует Exception), клиент получал бы голый stack trace без `ProblemDetail`.
>
>     **Как было бы правильно.** Прочитать Javadoc `Error`: «indicates serious problems that a reasonable application should not try to catch». Бизнес-исключения — это `Exception` или `RuntimeException`, не `Error`.
>
> - [x] B. `Error` сигнализирует о проблемах уровня JVM (память, стек, classloader) и обычно невосстановим, а `Exception` — это восстановимые ошибки уровня приложения.
>
>     **Развёрнутое объяснение.** `Error` представляет фундаментальные сбои исполняющей среды: исчерпание heap (`OutOfMemoryError`), переполнение стека потока (`StackOverflowError`), отсутствие класса в classpath (`NoClassDefFoundError`), несовместимая версия class-файла (`UnsupportedClassVersionError`). После такого сбоя состояние JVM непредсказуемо — потоки могут быть в полу-разрушенном состоянии, locks не отпущены, finalize не вызвался. `Exception` — это уровень приложения: `IOException` при недоступности файла, `SQLException` при потере коннекта к БД, `IllegalArgumentException` при невалидном входе. Эти ошибки предполагают осмысленную обработку: retry, fallback, валидация на входе.
>
>     **Пример.** В Kubernetes-окружении правильная реакция на `OutOfMemoryError` — это `-XX:+ExitOnOutOfMemoryError`, который завершает JVM, чтобы liveness probe увидела pod как unhealthy и оркестратор поднял свежий контейнер. Альтернатива (поймать `OutOfMemoryError` и продолжить работу) приводит к каскадным сбоям: в Knight Capital 2012 неперезапущенный legacy-сервис продолжал работу с разрушенным состоянием и нанёс $440M убытка за 45 минут.
>
>     **Когда применять.** При построении observability: метрики `Error`-инцидентов (через JFR, `jcmd`, heap dumps) идут отдельно от `Exception`-метрик. При проектировании restart-стратегии: после `Error` нужен process restart, после `Exception` — обычно достаточно retry на уровне запроса.
>
>     **Подводные камни.** `Error` всё равно можно поймать через `catch (Throwable)` или `catch (Error)` — компилятор разрешает. Но делать это можно только в верхнем обработчике потока для логирования и graceful shutdown, не для продолжения работы. `StackOverflowError` иногда восстановим (если стек глубокий, но конечный) — но это редкий случай.
>
>     **Связанные вопросы.** [[java-exceptions-interview#Q1]] иерархия Throwable; [[java-exceptions-interview#Q31]] OutOfMemoryError и StackOverflowError; [[java-exceptions-interview#Q24]] catch Throwable.
>
> - [ ] C. `Error` и `Exception` — полные синонимы в Java, и разница только в исторически сложившихся именах классов.
>
>     **Что на самом деле.** Они принципиально различаются семантически и по рекомендуемой обработке. `Error` обычно невосстановим, `Exception` — восстановим. Это отражено в Javadoc и закреплено практикой 25 лет разработки.
>
>     **Откуда путаница.** В английском «error» и «exception» в обиходе часто взаимозаменяемы. Кто-то переносит это бытовое восприятие на Java-классы.
>
>     **Если бы это было правдой.** Разработчик писал бы `catch (Throwable)` для универсальности, ловил бы `OutOfMemoryError` и продолжал работу с разрушенным состоянием. В БД писались бы частичные данные, биллинг расходился бы с фактическими операциями, и инцидент находили бы через сутки в аудите.
>
>     **Как было бы правильно.** Признать, что Java разделяет эти типы специально, и опираться на это разделение в коде: `catch (Exception)` для бизнеса, `Throwable`-handler — только на границе системы.
>
> - [ ] D. `Error` в Java всегда checked и требует обработки, а `Exception` всегда unchecked и обработки не требует.
>
>     **Что на самом деле.** `Error` — unchecked (отдельная ветка `Throwable`, не подкласс `Exception`). `Exception` делится: checked (требует обработки) и unchecked (`RuntimeException`+потомки). Категория checked/unchecked не совпадает с категорией Error/Exception.
>
>     **Откуда путаница.** Логика «Error звучит серьёзнее, значит требует обработки явно» отражает обиходное мышление, не реальные правила Java.
>
>     **Если бы это было правдой.** Разработчик писал бы `throws Error` в сигнатуре каждого метода, ожидая compile-time гарантии. IDE подсвечивает как redundant, MR блокируется с пометкой «basics».
>
>     **Как было бы правильно.** `Error` — unchecked всегда; `Exception` — может быть checked или unchecked в зависимости от подкласса.

> [!mcq] Какова правильная стратегия обработки `OutOfMemoryError` в production-сервисе?
>
> - [ ] A. Достаточно поймать `OutOfMemoryError` в catch и сделать retry — следующий вызов получит свежую память от JVM.
>
>     **Что на самом деле.** Heap уже исчерпан, и любая последующая аллокация (включая создание новых объектов для retry-логики, логирования, создания `String`) с высокой вероятностью снова бросит OOM или приведёт к каскадным сбоям. JVM не «выдаёт свежую память» — она просто продолжает работать с тем же heap, GC которого не помог.
>
>     **Откуда путаница.** Аналогия с обычными `RuntimeException`, где retry часто помогает (transient network error, race condition). Для OOM аналогия неверна.
>
>     **Если бы это было правдой.** Сервис в `while (true) { try { work(); } catch (OutOfMemoryError e) { } }` — каждый цикл аллоцирует, каждый раз падает; Kubernetes liveness probe не видит проблему (поток жив, отвечает на heartbeat), 100% CPU тратится на бесполезные попытки, никакой полезной работы.
>
>     **Как было бы правильно.** Не ловить OOM в бизнес-коде; настроить `-XX:+ExitOnOutOfMemoryError`, чтобы JVM завершила процесс и оркестратор поднял свежий pod.
>
> - [ ] B. `OutOfMemoryError` нужно обязательно обрабатывать в каждом сервисе, иначе JVM не вызовет `System.gc()` для освобождения памяти.
>
>     **Что на самом деле.** `System.gc()` — это лишь подсказка JVM сделать GC, и она не связана с catch-блоками. JVM пытается сделать full GC автоматически перед тем, как бросить OOM, и если это не помогло — память не освободится никаким `System.gc()`.
>
>     **Откуда путаница.** Знание о существовании `System.gc()` + надежда «есть способ освободить память вручную». На деле — нет.
>
>     **Если бы это было правдой.** Разработчик окружал бы каждый метод `try { } catch (OutOfMemoryError oom) { System.gc(); }`. Production-OOM по-прежнему происходит, но stack traces исчезают (catch проглатывает, не логирует через `log.error`), диагностика инцидента невозможна.
>
>     **Как было бы правильно.** Анализировать heap dump (`-XX:+HeapDumpOnOutOfMemoryError`) через Eclipse MAT — найти утечку и исправить причину, а не маскировать симптом.
>
> - [ ] C. `OutOfMemoryError` — recoverable error, после catch JVM автоматически перезапускает поток с очищенным локальным стеком.
>
>     **Что на самом деле.** Автоматического перезапуска потоков в Java нет. После OOM состояние JVM непредсказуемо: некоторые потоки могли упасть (например, finalizer), некоторые locks могли остаться захваченными, GC мог быть в полу-разрушенном состоянии.
>
>     **Откуда путаница.** В концепции Erlang-style supervisor'ов (Akka, Spring Retry) есть автоматический перезапуск actor'ов. Перенос идеи в core Java неверен — там нет встроенного supervisor'а.
>
>     **Если бы это было правдой.** Можно было бы спокойно ловить OOM и продолжать работу. На деле — после OOM все worker-потоки в неопределённом состоянии, jobs встают, Kafka consumer lag растёт до 1M, downstream-сервисы видят таймауты.
>
>     **Как было бы правильно.** Использовать внешний supervisor (Kubernetes restart policy, systemd, Cloud Foundry) для перезапуска целого процесса JVM.
>
> - [x] D. `OutOfMemoryError` ловить для retry бессмысленно: heap уже исчерпан, и любая последующая аллокация (включая создание объекта в catch-блоке) с высокой вероятностью снова бросит OOM.
>
>     **Развёрнутое объяснение.** При выбросе OOM JVM уже исчерпала возможности GC освободить достаточно памяти. Catch-блок выполняется в том же heap — даже строка `log.error("OOM!", e)` может упасть, потому что для форматирования message и stack trace нужны новые `String`-объекты. Это означает, что catch на OOM практически бесполезен с точки зрения восстановления. Корректная стратегия — graceful shutdown через `Thread.setDefaultUncaughtExceptionHandler` (где можно попробовать отправить алёрт через pre-allocated буфер) и завершение процесса. Дальше pod поднимается оркестратором.
>
>     **Пример.** Production-конфигурация для JVM 17 в Kubernetes: `-XX:+ExitOnOutOfMemoryError` (завершить JVM при OOM) + `-XX:+HeapDumpOnOutOfMemoryError -XX:HeapDumpPath=/dumps/heap.hprof` (снять heap dump перед exit) + `-XX:+CrashOnOutOfMemoryError` (для немедленного crash без cleanup в редких случаях). Kubernetes restartPolicy=Always или Deployment с `livenessProbe` поднимает свежий pod.
>
>     **Когда применять.** Всегда в production-сервисах на JVM, развёрнутых под контейнерным оркестратором. Heap dump анализируется через Eclipse MAT (Dominator Tree, Leak Suspects), VisualVM или async-profiler.
>
>     **Подводные камни.** Heap dump на больших heaps (16-64 GB) занимает минуты — pod может умереть от termination grace period перед его завершением. Решение — `--terminationGracePeriodSeconds` побольше или dump на отдельный volume. Также важно не дампить часто (security: heap содержит PII, пароли, токены).
>
>     **Связанные вопросы.** [[java-exceptions-interview#Q24]] catch Throwable; [[java-exceptions-interview#Q31]] обработка OOM и SOE; [[java-exceptions-interview#Q26]] UncaughtExceptionHandler.

## Q7. Какой `Exception` будет выброшен при выполнении следующего кода?

```java
Integer[][] ints = {{1, 2, 3}, {null}, {7, 8, 9}};
System.out.println("value = " + ints[1][1].intValue());
```

Будет выброшен `ArrayIndexOutOfBoundsException`. Второй вложенный массив `{null}` содержит только один элемент (с индексом 0), а мы обращаемся к индексу 1 (`ints[1][1]`), который выходит за границы массива.

Обратите внимание: если бы обращение было к `ints[1][0].intValue()`, то был бы `NullPointerException`, т.к. `ints[1][0]` равно `null`.

> [!mcq] Какое исключение бросит код `Integer[][] ints = {{1,2,3},{null},{7,8,9}}; ints[1][1].intValue();`?
>
> - [ ] A. `NullPointerException`, потому что элемент по индексу `[1][1]` равен `null` и разыменование невозможно.
>
>     **Что на самом деле.** До разыменования `null` дело не доходит. `ints[1]` — это массив `{null}` длиной 1 с единственным валидным индексом `0`. Обращение `ints[1][1]` уже выходит за границы массива, и JVM бросает `ArrayIndexOutOfBoundsException` до того, как добраться до `intValue()`.
>
>     **Откуда путаница.** В массиве встречается слово `null`, и взгляд цепляется за него как за источник проблемы. NPE действительно бы возник, но только при `ints[1][0].intValue()`.
>
>     **Если бы это было правдой.** Разработчик добавил бы `if (ints[1][1] == null) return defaultValue` для защиты. Фактическая ошибка AIOOBE остаётся, но теперь скрыта за лишней `null`-проверкой — debug идёт по ложному пути «почему такой странный null-check всё равно падает».
>
>     **Как было бы правильно.** Сначала проверить границы (`if (ints[1].length > 1)`), потом разыменовать (`if (ints[1][1] != null)`). Bound-check всегда предшествует null-check.
>
> - [ ] B. `ClassCastException`, потому что `Integer[]` не может быть приведён к `int` автоматически.
>
>     **Что на самом деле.** Авто-боксинг/анбоксинг (`Integer.intValue()` → `int`) работает в Java без CCE. `ClassCastException` возникает только при некорректном приведении объектных типов (`(String) someObject`), не при работе с примитивами.
>
>     **Откуда путаница.** Java имеет два разных понятия типа: примитивные (`int`) и объектные (`Integer`). На стыке между ними есть авто-боксинг, и кажется, что там может возникнуть CCE.
>
>     **Если бы это было правдой.** Разработчик писал бы `(int) ints[1][1]` для «ручного приведения», получал бы ту же AIOOBE, тратил время на гипотезу о type-casting вместо реальной проблемы с границами.
>
>     **Как было бы правильно.** Понимать, что CCE — это про объектное приведение, не про bounds или null.
>
> - [x] C. `ArrayIndexOutOfBoundsException`, потому что `ints[1]` имеет длину 1, а мы обращаемся к индексу 1 (выход за границу).
>
>     **Развёрнутое объяснение.** В Java вычисление `ints[1][1]` происходит слева направо. Сначала JVM получает `ints[1]` — это ссылка на внутренний массив `{null}` длиной 1. Затем выполняется индексация `[1]` — и здесь JVM проверяет границы (bound-check): запрашиваемый индекс 1, длина массива 1, поэтому индекс выходит за границу `[0, length-1] = [0, 0]`. JVM бросает `ArrayIndexOutOfBoundsException` до того, как добраться до `.intValue()`. Bound-check всегда выполняется до разыменования значения, поэтому NPE недостижим в этой точке.
>
>     **Пример.** В legacy-парсерах CSV распространённый паттерн `for (int i = 0; i < rows.length; i++) { values[i] = parse(rows[i][2]); }` падает с AIOOBE, если хотя бы одна строка короче 3 колонок (например, последняя строка с trailing newline). Защита — `if (rows[i].length > 2)` перед обращением, либо использовать `Arrays.stream(rows[i]).skip(2).findFirst()`.
>
>     **Когда применять.** При работе с jagged-массивами (массивы массивов разной длины), при парсинге внешних данных (CSV, JSON-массивы), при манипуляциях с матрицами в алгоритмических задачах. Современный подход — использовать `List<List<T>>` с явными `.size()`-проверками или `Optional`-обёртки.
>
>     **Подводные камни.** В hot path bound-check замедляет код, но JIT часто его элиминирует через PRE (partial redundancy elimination), если видит что индексы безопасны. AIOOBE — unchecked, поэтому компилятор не заставит ловить — нужно тестировать edge cases. В JDK 11+ сообщение AIOOBE содержит фактический индекс и длину массива, что упрощает диагностику.
>
>     **Связанные вопросы.** [[java-exceptions-interview#Q1]] иерархия Throwable; [[java-exceptions-interview#Q5]] checked vs unchecked (AIOOBE — unchecked); [[java-exceptions-interview#Q9]] stack trace для диагностики.
>
> - [ ] D. `NumberFormatException`, потому что `null` нельзя преобразовать в числовое значение через `intValue()`.
>
>     **Что на самом деле.** `NumberFormatException` связан только с парсингом строк (`Integer.parseInt("abc")`), не с `intValue()` на wrapper-типах. Вызов `intValue()` на `null`-ссылке дал бы NPE, не NFE.
>
>     **Откуда путаница.** Имя `intValue()` похоже на «преобразовать в int», что ассоциируется с парсингом. На деле `Integer.intValue()` — это просто анбоксинг (получение примитивного `int` из объекта `Integer`).
>
>     **Если бы это было правдой.** Разработчик оборачивает в `try { ... } catch (NumberFormatException e) { return 0; }`, реальная AIOOBE улетает выше как unhandled, мониторинг алертит на необработанные runtime-ошибки, post-mortem показывает, что catch ловил несуществующий тип ошибки.
>
>     **Как было бы правильно.** Понимать сценарии для каждого типа: NFE — парсинг строки в число; NPE — разыменование null; AIOOBE — выход за границы массива.

## Q8. Что такое `Exception Chaining`?

Цепочка исключений (`Exception Chaining`) — это механизм оборачивания одного исключения в другое для сохранения полной истории ошибки. Оригинальное исключение передаётся как `cause`:

```java
public Order processOrder(OrderRequest request) {
    try {
        return repository.save(mapToOrder(request));
    } catch (DataAccessException ex) {
        throw new OrderProcessingException(
            "Не удалось сохранить заказ: " + request.getId(), ex  // ex — cause
        );
    }
}
```

Преимущества:
- Сохраняется полный стек вызовов от первоначальной ошибки
- Верхний уровень видит доменное исключение, а не инфраструктурное
- Логгер выводит всю цепочку: основное → cause → cause of cause

Доступ к причине: `exception.getCause()` возвращает вложенное исключение.

> [!mcq]
> - [ ] `Exception Chaining` — это автоматическое объединение нескольких одновременно выброшенных исключений в один composite-объект. | Это описание `addSuppressed`, а не chaining; chaining связывает «причину и следствие», а не параллельные ошибки. ❌ ПОСЛЕДСТВИЕ: разработчик путает термины на собеседовании, на проде использует `addSuppressed` вместо `initCause` — log-аппендер форматирует только primary, root cause теряется в `Suppressed:` секции.
> - [ ] `Exception Chaining` — это механизм повторного выброса одного и того же объекта исключения на разных уровнях стека без потери контекста. | Повторный выброс (`throw ex`) сам по себе не создаёт цепочку — chaining оборачивает одно исключение в другое через `cause`. ❌ ПОСЛЕДСТВИЕ: разработчик пишет `catch (SQLException e) { throw e; }` в каждом слое, на верху ловится `SQLException` без доменного контекста (`OrderId=123`) — диагностика инцидента дольше в разы.
> - [ ] `Exception Chaining` — это последовательность `catch`-блоков от специфичного к общему, формирующая «цепочку» обработки. | Порядок `catch`-блоков — отдельная концепция; chaining относится к связи между объектами исключений через поле `cause`. ❌ ПОСЛЕДСТВИЕ: разработчик «правильно выстраивает цепочку catch», но не оборачивает причину — `getCause()` возвращает `null`, `ExceptionUtils.getRootCause` бесполезен.
> - [x] `Exception Chaining` — это механизм оборачивания одного исключения в другое через поле `cause` для сохранения первопричины. | `throw new OrderException("msg", ex)` сохраняет оригинальную `DataAccessException` как cause; stack trace выводит «Caused by:» цепочку. ✓ ПРИМЕНЯТЬ: Spring `JdbcTemplate.translateException` оборачивает `SQLException` в `DataAccessException` с сохранением cause — caller видит доменный тип, а root cause доступен через `getCause()`. 📋 ПРАВИЛО: «cause через конструктор при wrap». 🔗 См. Q29, Q35.

## Q9. (!) Что такое `Stacktrace` и как она связана с `Exception`?

`Stack trace` — это снимок стека вызовов в момент создания исключения. Показывает цепочку вызовов методов от точки возникновения до корня потока:

```
com.app.OrderService.processOrder(OrderService.java:45)
com.app.OrderController.createOrder(OrderController.java:23)
...
java.lang.Thread.run(Thread.java:829)
```

Ключевые методы:
- `exception.getStackTrace()` — возвращает `StackTraceElement[]`
- `exception.printStackTrace()` — выводит в `System.err` (не использовать в production!)
- `exception.setStackTrace(StackTraceElement[])` — позволяет изменять стек (редко используется)

Полезные советы:
- Стек формируется при **создании** исключения (`new`), а не при `throw`
- Создание стека — **дорогая операция**; для исключений, которые бросаются часто, можно переопределить `fillInStackTrace()` и вернуть `this` для оптимизации
- Используйте `log.error("message", ex)` вместо `ex.printStackTrace()` — подробнее в [вопросах по логированию](../../logging/logging-interview.md)

> [!mcq]
> - [ ] Стек формируется в момент вызова `throw`, а создание объекта только аллоцирует память без обхода стека. | `fillInStackTrace()` вызывается из конструктора `Throwable` — создание исключения уже дорого, даже если его не бросить. ❌ ПОСЛЕДСТВИЕ: разработчик кеширует `new RuntimeException()` как поле класса для «оптимизации», ожидая дешёвой аллокации, но stack trace зафиксирован в момент инициализации (init-thread) — все логи показывают неверный поток.
> - [x] Стек формируется в момент создания объекта исключения (`new`) через `fillInStackTrace()`, а не в момент `throw`. | Именно поэтому существует sentinel-паттерн (закэшировать исключение, бросать многократно) и конструктор `Throwable(msg, cause, suppression, writableStackTrace=false)` для оптимизации. ✓ ПРИМЕНЯТЬ: Netty `Recycler` и Hystrix используют sentinel-исключения с отключённым stack trace для горячих путей. 📋 ПРАВИЛО: «стек снимается в `new`, не в `throw`». 🔗 См. Q9, Q41.
> - [ ] Стек формируется только при первом вызове `getStackTrace()`, реализуя lazy-инициализацию. | Никакой lazy-инициализации нет — стек собирается сразу в конструкторе; `getStackTrace()` лишь возвращает готовый массив. ❌ ПОСЛЕДСТВИЕ: разработчик создаёт исключение «впрок» и долго его держит для последующего `getStackTrace()`, ожидая, что цена отложится — на самом деле цена уже уплачена в `new`, и stack trace показывает не тот поток.
> - [ ] Стек формируется в момент `catch`, чтобы показать путь от обработчика до источника. | `catch` не модифицирует стек — он фиксируется при создании и далее неизменен. ❌ ПОСЛЕДСТВИЕ: разработчик удивляется, почему в `@ExceptionHandler` stack trace «обрывается на сервисе» — на самом деле он полный с момента `new`, а ниже handler-а ничего и быть не может.

> [!mcq]
> - [x] `fillInStackTrace()` — native-вызов, обходящий все кадры стека потока, и его стоимость на порядок (примерно 10×) выше, чем создание обычного объекта; для hot path рекомендуется кэшировать sentinel-исключение или переопределять метод как `return this`. | Бенчмарки показывают разницу 10-20× между `new RuntimeException()` и `new Object()`; JIT иногда оптимизирует через `OmitStackTraceInFastThrow`. ✓ ПРИМЕНЯТЬ: Netty/Hystrix кешируют sentinel-исключения через 4-аргументный конструктор `Throwable(msg, null, true, false)` с `writableStackTrace=false`. 📋 ПРАВИЛО: «exception в hot path = sentinel + writableStackTrace=false». 🔗 См. Q41.
> - [ ] Создание `Throwable` дешёвое, потому что `fillInStackTrace()` — это просто копирование указателя на текущий thread, без обхода кадров стека. | `fillInStackTrace` — native-метод, проходящий по всем кадрам стека и формирующий `StackTraceElement[]`; стоимость пропорциональна глубине стека. ❌ ПОСЛЕДСТВИЕ: разработчик использует `Integer.parseInt` через try-catch для валидации пользовательского ввода в hot path, p99 поднимается с 5ms до 200ms, мониторинг алертит на latency, root cause не очевиден.
> - [ ] Стоимость создания исключения сравнима с обычным методом, поэтому исключения можно безопасно использовать для control flow. | Использование исключений для control flow — известный антипаттерн; производительность падает на порядки, семантика искажается. ❌ ПОСЛЕДСТВИЕ: команда строит CSV-парсер на `try { Integer.parseInt } catch`, при батче 100K строк с 30% невалидных — обработка идёт 60 секунд вместо 3, ETL-job не успевает в SLA-окно.
> - [ ] `Throwable.getStackTrace()` собирает стек только при первом вызове и кэширует результат — поэтому повторные вызовы стоят O(1). | Стек собирается в конструкторе через `fillInStackTrace`; `getStackTrace()` лишь возвращает массив — стоимость размазана между конструктором (тяжёлый) и геттером (лёгкий), а не наоборот. ❌ ПОСЛЕДСТВИЕ: разработчик пишет «отложенное логирование» через `if (logger.isDebugEnabled()) logger.debug("", e)` думая, что без `getStackTrace()` стек не строится, latency не падает.

## Q10. Зачем создавать подклассы `Exception`?

Создание подклассов (subclassing) нужно, когда:
1. Ни одно стандартное исключение не описывает ситуацию семантически точно
2. Нужно добавить дополнительный контекст (поля с кодами ошибок, ID ресурса и т.д.)
3. Нужна доменная иерархия для группировки ошибок

```java
public class PaymentException extends RuntimeException {
    private final String paymentId;
    private final ErrorCode errorCode;

    public PaymentException(String message, String paymentId, ErrorCode errorCode) {
        super(message);
        this.paymentId = paymentId;
        this.errorCode = errorCode;
    }

    public PaymentException(String message, String paymentId, ErrorCode errorCode, Throwable cause) {
        super(message, cause);
        this.paymentId = paymentId;
        this.errorCode = errorCode;
    }
    // getters
}
```

Правило выбора базового класса: если вызывающий код **может и должен** восстановиться — наследовать от `Exception` (checked). Если это ошибка программиста или невосстановимая ситуация — от `RuntimeException` (unchecked).

> [!mcq]
> - [x] Подклассы создают для доменной семантики, добавления контекстных полей и группировки связанных ошибок в иерархию. | `OrderNotFoundException` с полем `orderId` несёт бизнес-смысл; общий `PaymentException` позволяет ловить все платёжные ошибки без перечисления конкретных подтипов. ✓ ПРИМЕНЯТЬ: Spring `DataAccessException` иерархия — общий тип + специфичные подтипы (`DuplicateKeyException`, `OptimisticLockingFailureException`). 📋 ПРАВИЛО: «доменный смысл + контекст + иерархия». 🔗 См. Q19, Q21, Q38.
> - [ ] Подклассы создают, чтобы увеличить размер stack trace и получить более детальную диагностическую информацию от JVM. | Размер stack trace не зависит от типа исключения, а определяется глубиной стека; пользовательские подклассы не меняют механизм сборки трейса. ❌ ПОСЛЕДСТВИЕ: разработчик создаёт 50 подклассов «для лучшей диагностики», getCause-цепочка раздувается, обработка `@ExceptionHandler` усложняется без прироста полезной информации.
> - [ ] Подклассы создают только ради обхода требования `throws` на checked-исключения. | Обход через `RuntimeException` — побочный эффект; основная причина — доменная семантика, а выбор checked/unchecked зависит от того, обязан ли caller восстановиться. ❌ ПОСЛЕДСТВИЕ: разработчик плодит классы «чтобы убрать `throws Exception`» без бизнес-смысла, иерархия плоская, на собеседовании не может объяснить разницу `OrderNotFound` vs `OrderInvalid`.
> - [ ] Подклассы обязательны для любого исключения — стандартные типы Java нельзя использовать напрямую. | `IllegalArgumentException`, `IllegalStateException`, `UnsupportedOperationException` рекомендуется переиспользовать там, где они уместны. ❌ ПОСЛЕДСТВИЕ: разработчик создаёт `class MyServiceIllegalArgumentException extends RuntimeException` для каждого сервиса, code-base раздувается, новички не знают, какой конкретный тип ловить.

## Q11. Каковы преимущества механизма `Exceptions`?

1. **Разделение логики и обработки ошибок** — основной код не засорён проверками возвращаемых кодов
2. **Распространение по стеку вызовов** — исключение автоматически поднимается до обработчика, не нужно передавать вручную
3. **Группировка по типам** — можно ловить `IOException` для всех I/O-ошибок, а не каждую по отдельности
4. **Обязательность обработки** — для checked-исключений компилятор гарантирует, что ошибка не проигнорирована
5. **Информативность** — объект исключения несёт message, cause, stack trace

В отличие от подхода с кодами ошибок (как в C), механизм исключений Java предотвращает ситуацию, когда ошибка «тихо проглатывается».

> [!mcq]
> - [ ] Механизм исключений в Java быстрее и эффективнее по памяти, чем проверка кодов возврата из функций. | Наоборот: создание исключения с `fillInStackTrace` в 10-100× дороже простого `if`-check; преимущества — в структурированности, не в перфомансе. ❌ ПОСЛЕДСТВИЕ: разработчик заменяет `if (input.isEmpty()) return false` на `throw new ValidationException`, в hot path при 10K RPS — CPU тратится на построение stack trace, p95 вырастает в разы.
> - [ ] Механизм исключений позволяет пропускать ошибки без всякой обработки, что уменьшает boilerplate-код. | Checked-исключения как раз требуют явной обработки или декларации; «тихое проглатывание» (`catch (Exception e) {}`) — антипаттерн, а не фича. ❌ ПОСЛЕДСТВИЕ: команда пишет пустые catch-блоки «для лаконичности», `OrderProcessingException` теряется без логов, биллинг-несоответствия выявляются через неделю аудита.
> - [ ] Механизм исключений автоматически откатывает все изменения состояния при выбросе, подобно транзакциям в БД. | Java не предоставляет автоматический rollback — восстановление состояния делается программистом через `finally` или `try-with-resources`. ❌ ПОСЛЕДСТВИЕ: разработчик полагается на «магический rollback», файл частично записан, кэш частично обновлён — после исключения система в неконсистентном состоянии, требуется ручная чистка.
> - [x] Механизм исключений отделяет happy-path от обработки ошибок, автоматически распространяет их по стеку вызовов и гарантирует обработку checked-типов. | Не нужно проверять `if (result == ERROR)` после каждого вызова; исключение само поднимается до обработчика, компилятор не даёт забыть про checked. ✓ ПРИМЕНЯТЬ: Spring `@Transactional` rollback на `RuntimeException` — естественное распространение по стеку до прокси-обёртки, без ручного «передаём ошибку наверх». 📋 ПРАВИЛО: «happy path чистый, ошибки идут отдельным каналом». 🔗 См. Q5, Q34.

## Q12. (!) Что такое `try-with-resources` и как он работает?

`try-with-resources` (`Java 7+`) — конструкция, которая автоматически закрывает ресурсы, реализующие интерфейс `AutoCloseable`, после выхода из блока `try`:

```java
// Java 7+ — ресурс объявляется в try(...)
try (var reader = new BufferedReader(new FileReader("data.csv"));
     var writer = new BufferedWriter(new FileWriter("output.csv"))) {

    String line;
    while ((line = reader.readLine()) != null) {
        writer.write(transform(line));
        writer.newLine();
    }
} // reader и writer закрываются автоматически, в обратном порядке
```

```java
// Java 9+ — можно использовать effectively final переменные
BufferedReader reader = new BufferedReader(new FileReader("data.csv"));
try (reader) {  // reader — effectively final
    return reader.readLine();
}
```

Порядок работы:

```mermaid
graph TD
    A["Создание ресурсов<br/>(в порядке объявления)"] --> B["Выполнение блока try"]
    B -->|"Нормальное завершение"| C["Закрытие ресурсов<br/>(в обратном порядке)"]
    B -->|"Исключение в try"| D["Закрытие ресурсов<br/>(в обратном порядке)"]
    D -->|"close() успешен"| E["Выброс основного исключения"]
    D -->|"close() тоже бросает"| F["Исключение close() добавляется<br/>как suppressed к основному"]
    F --> E
    C --> G["Продолжение выполнения"]
```

Преимущества перед ручным `finally`:
- Нет boilerplate-кода проверки на `null` и вызова `close()`
- Гарантия закрытия даже при исключении
- Suppressed exceptions не теряются (в отличие от ручного `finally`, где исключение в `close()` затирает основное)

> [!mcq]
> - [x] `try-with-resources` автоматически закрывает объекты, реализующие `AutoCloseable`, в обратном порядке объявления, и сохраняет исключения через `addSuppressed`. | Три ключевых свойства: LIFO-закрытие, требование только `AutoCloseable`, сохранение ошибок из `close()` как suppressed. ✓ ПРИМЕНЯТЬ: типичный JDBC-паттерн `try (Connection conn = ds.getConnection(); PreparedStatement ps = conn.prepareStatement(sql); ResultSet rs = ps.executeQuery())` — корректное закрытие в обратном порядке. 📋 ПРАВИЛО: «LIFO-close + suppressed для cleanup». 🔗 См. Q13, Q14, Q37.
> - [ ] `try-with-resources` закрывает ресурсы в том же порядке, в котором они были объявлены в блоке `try(...)`. | Порядок обратный (LIFO): `Connection` открывается первым, но закрывается последним — иначе зависимый ресурс упадёт на мёртвом родителе. ❌ ПОСЛЕДСТВИЕ: разработчик пишет ручной finally `conn.close(); stmt.close(); rs.close();` — `rs.close()` бросает «ResultSet is closed», suppressed-ошибки заполняют логи, реальная ошибка теряется.
> - [ ] `try-with-resources` требует, чтобы ресурсы реализовывали интерфейс `Closeable` (не `AutoCloseable`), т.к. только он гарантирует корректное закрытие. | Достаточно `AutoCloseable` — он введён именно для `try-with-resources` в Java 7; `Closeable extends AutoCloseable` для I/O. ❌ ПОСЛЕДСТВИЕ: разработчик заставляет бизнес-класс `MeasuredTimer implements Closeable` бросать `throws IOException` — caller вынужден ловить ненужный checked, API замусорен.
> - [ ] `try-with-resources` не поддерживает несколько ресурсов в одной конструкции — для каждого нужен отдельный вложенный блок. | Несколько ресурсов объявляются через `;` в одной паре скобок и закрываются в обратном порядке. ❌ ПОСЛЕДСТВИЕ: разработчик плодит вложенные `try (a) { try (b) { try (c) { ... } } }` — pyramid of doom, читать невозможно, suppressed-семантика теряется при ручной вложенности.

> [!mcq]
> - [ ] В Java 9+ внутри `try(...)` всегда нужно объявлять новую переменную, даже если ресурс уже создан выше — синтаксис без объявления остался запрещён. | Именно в Java 9 (JEP 213) разрешили использовать существующую effectively final ссылку без переобъявления. ❌ ПОСЛЕДСТВИЕ: разработчик плодит alias-переменные `try (var r = existingResource)` для legacy-API, читаемость падает, junior на code-review не понимает зачем нужен `r`.
> - [ ] В Java 9+ переменная в `try(...)` может быть mutable и переприсваиваться внутри блока — управление закрытием возьмёт JVM на момент выхода. | Ссылка должна быть effectively final, иначе нет гарантии, что закроется именно тот объект, что был открыт. ❌ ПОСЛЕДСТВИЕ: разработчик переприсваивает `resource = newResource` внутри try, ожидая закрытия обоих, на деле — leak первого ресурса, после серии операций OOM по file descriptors.
> - [ ] Синтаксис `try (resource) { }` без объявления был доступен с Java 7, а Java 9 только формализовал требование effectively final. | В Java 7-8 синтаксис `try(...)` строго требовал нового объявления переменной внутри скобок; поддержка использования существующих effectively final переменных появилась именно в Java 9 (JEP 213). ❌ ПОСЛЕДСТВИЕ: разработчик пишет код для Java 8 в новом синтаксисе, локально на Java 11 всё ОК, билд CI на Java 8 падает с syntax error, релиз сдвигается на день, мерж конфликты копятся.
> - [x] Java 9+ (JEP 213) позволяет использовать существующую `final` или effectively final переменную в `try(resource)` без переобъявления, что устраняет «глупую» локальную переменную из старого синтаксиса. | До Java 9 приходилось писать `try (var r = existingResource) { }` где `r` — лишний alias; теперь `try (existingResource) { }`. Требование effectively final гарантирует закрытие ровно того объекта, что был открыт. ✓ ПРИМЕНЯТЬ: в Spring Cloud Gateway фильтрах удобно использовать `try (request) { ... }` для уже полученного closable-объекта без переобъявления. 📋 ПРАВИЛО: «Java 9+ — try на existing effectively final переменной». 🔗 См. Q12.

## Q13. (!) Что такое `Suppressed Exceptions` в `try-with-resources`?

Когда в блоке `try` возникает исключение И при закрытии ресурса (`close()`) тоже возникает исключение — исключение из `close()` не теряется, а добавляется к основному как **suppressed**:

```java
public class FaultyResource implements AutoCloseable {
    public void doWork() {
        throw new RuntimeException("Ошибка в doWork");
    }

    @Override
    public void close() {
        throw new RuntimeException("Ошибка в close");
    }
}

try (var resource = new FaultyResource()) {
    resource.doWork();
}
// Результат:
// Exception: "Ошибка в doWork"
//   Suppressed: "Ошибка в close"
```

Доступ к suppressed:

```java
try {
    // ...
} catch (RuntimeException ex) {
    log.error("Основная ошибка: {}", ex.getMessage());
    for (Throwable suppressed : ex.getSuppressed()) {
        log.error("  Suppressed: {}", suppressed.getMessage());
    }
}
```

Без `try-with-resources` (ручной `finally`) исключение в `close()` **затирает** исключение из `try` — suppressed-механизм решает эту проблему.

> [!mcq]
> - [ ] Suppressed exceptions — это исключения, которые полностью игнорируются JVM и не попадают в стектрейс ни при каких условиях. | Они сохраняются именно для диагностики и выводятся в stack trace под меткой `Suppressed:` через `printStackTrace`. ❌ ПОСЛЕДСТВИЕ: разработчик не проверяет `ex.getSuppressed()` в логировании, в production вторая ошибка (close() rollback) остаётся невидимой, root cause utility-проблем не находится неделями.
> - [ ] Suppressed exceptions — это исключения, подавленные аннотацией `@SuppressWarnings` и не генерирующие предупреждений компилятора. | `@SuppressWarnings` работает с compile-time warnings, никак не связано с runtime-механизмом `Throwable.addSuppressed`. ❌ ПОСЛЕДСТВИЕ: на собеседовании разработчик путает термины, теряет балл; в коде ставит `@SuppressWarnings("exception")` ожидая магии, реальные ошибки продолжают падать.
> - [x] Suppressed exceptions — это исключения из `close()`, которые прикрепляются к основному исключению из `try`-блока через `addSuppressed` без его подмены. | В ручном `finally` второе исключение затёрло бы первое; `try-with-resources` автоматически сохраняет обе ошибки — основное и suppressed доступны через `getSuppressed()`. ✓ ПРИМЕНЯТЬ: при batch-процессинге через `try (Connection)` — если транзакция упала и rollback в close() тоже, оба видны в логах. 📋 ПРАВИЛО: «primary throws, secondary suppressed — обе сохраняются». 🔗 См. Q12, Q14, Q35.
> - [ ] Suppressed exceptions — это те, которые не распространяются по стеку и автоматически конвертируются в `RuntimeException`. | Suppressed не влияют на распространение основного исключения и не конвертируются — они просто «прицеплены» через поле. ❌ ПОСЛЕДСТВИЕ: разработчик ожидает «фоновую» обработку suppressed, не пишет catch для основного, исключение всё равно поднимается, поток падает с unhandled.

> [!mcq]
> - [ ] `addSuppressed(this)` — допустимый способ пометить исключение как «не распространяемое», JVM проигнорирует такое исключение при распространении. | Добавление исключения к самому себе запрещено — `IllegalArgumentException` (защита `Throwable` от циклических ссылок). ❌ ПОСЛЕДСТВИЕ: разработчик пишет «трюк» для тестов с `e.addSuppressed(e)`, тест падает с IAE, отладка занимает час, тест-фреймворк CI помечает падение как flaky.
> - [ ] `addSuppressed` — package-private метод, доступный только из пакета `java.lang`, поэтому пользовательский код не может его вызывать. | Метод public с Java 7 — любой код может его вызывать на любом `Throwable` (если suppression не отключён 4-аргументным конструктором). ❌ ПОСЛЕДСТВИЕ: разработчик создаёт обходной reflection-вызов, считая метод закрытым, ломает sealed-API в Java 17, тратит часы на «обход» несуществующей проблемы.
> - [ ] `addSuppressed` можно вызвать только один раз — повторный вызов перезаписывает предыдущий suppressed-Throwable. | Путаница с `initCause` (тот действительно один раз); `addSuppressed` накапливает в массив без перезаписи — все добавленные сохраняются. ❌ ПОСЛЕДСТВИЕ: при batch-cleanup нескольких ресурсов разработчик «бережёт» один вызов, теряет 3 из 4 ошибок closeAll — диагностика мульти-ресурсного fail невозможна.
> - [x] `Throwable.addSuppressed(Throwable)` — публичный API для ручного добавления suppressed-исключений, но запрещено добавлять `null` (NPE) и сам объект (`IllegalArgumentException`); вызывать можно многократно. | Метод предназначен и для JVM (try-with-resources), и для пользовательского кода; suppressed-механизм можно отключить через `Throwable(message, cause, enableSuppression=false, ...)`. ✓ ПРИМЕНЯТЬ: ручная агрегация ошибок при `closeAll(List<AutoCloseable>)` — primary + addSuppressed для остальных. 📋 ПРАВИЛО: «addSuppressed многократен; null/this запрещены». 🔗 См. Q13, Q14.

## Q14. Что такое `addSuppressed()` и `getSuppressed()`?

Методы класса `Throwable` для работы с подавленными исключениями:

- `addSuppressed(Throwable)` — добавляет подавленное исключение
- `getSuppressed()` — возвращает массив `Throwable[]` подавленных исключений

JVM использует их автоматически в `try-with-resources`. В пользовательском коде полезны при освобождении нескольких ресурсов вручную:

```java
public void closeAll(List<AutoCloseable> resources) {
    Throwable primary = null;
    for (AutoCloseable resource : resources) {
        try {
            resource.close();
        } catch (Exception ex) {
            if (primary == null) {
                primary = ex;
            } else {
                primary.addSuppressed(ex); // не теряем ошибки при close
            }
        }
    }
    if (primary != null) {
        throw new RuntimeException("Ошибка при закрытии ресурсов", primary);
    }
}
```

> [!mcq]
> - [ ] `addSuppressed` и `getSuppressed` — это методы `Exception`, доступные только в checked-ветке иерархии. | Методы определены в базовом `Throwable`, поэтому доступны и `Exception`, и `Error`, и `RuntimeException`. ❌ ПОСЛЕДСТВИЕ: разработчик не пользуется suppressed для `OutOfMemoryError`-тёго cleanup'а, теряет вторичную диагностику в OOM-инцидентах.
> - [ ] `addSuppressed` можно вызывать только один раз — повторный вызов приведёт к `IllegalStateException` (как у `initCause`). | Ограничение «один раз» — у `initCause`; `addSuppressed` можно вызывать многократно, исключения накапливаются в массиве. ❌ ПОСЛЕДСТВИЕ: разработчик пишет if-guard «уже добавлено», теряет последующие ошибки в multi-resource cleanup, диагностика батч-операций неполная.
> - [x] `addSuppressed` и `getSuppressed` — это методы `Throwable`, используемые `try-with-resources` автоматически и доступные для ручной работы. | JVM вызывает `addSuppressed` при сбое в `close()`; программист использует для своей логики cleanup или агрегации ошибок. ✓ ПРИМЕНЯТЬ: Apache Commons `IOUtils.closeQuietly` устаревший — современная замена через try-with-resources с автоматическим suppressed. 📋 ПРАВИЛО: «`addSuppressed`/`getSuppressed` — на любом `Throwable`». 🔗 См. Q13, Q35.
> - [ ] `getSuppressed()` возвращает `List<Throwable>`, изменения в котором автоматически отражаются в объекте исключения. | Возвращаемый тип — `Throwable[]` (массив), это копия, не живая ссылка; изменения не влияют на внутреннее состояние. ❌ ПОСЛЕДСТВИЕ: разработчик пишет `e.getSuppressed().clear()` ожидая «очистки», получает UnsupportedOperationException на List или просто игнорирование на массиве — confusing API behaviour.

## Q15. (!) Как выбросить `Exception` внутри лямбда-выражения?

Стандартные функциональные интерфейсы (`Function`, `Consumer`, `Supplier`) **не объявляют checked exceptions** в сигнатуре. Поэтому:

**Unchecked — работают напрямую:**

```java
List<Integer> numbers = List.of(3, 9, 7, 0, 10);
numbers.forEach(i -> {
    if (i == 0) {
        throw new IllegalArgumentException("Ноль недопустим");
    }
    System.out.println(100 / i);
});
```

**Checked — нужен обходной путь.** Три подхода:

1. **Обернуть в unchecked внутри лямбды:**
```java
files.forEach(path -> {
    try {
        Files.readString(path);
    } catch (IOException e) {
        throw new UncheckedIOException(e); // стандартный враппер
    }
});
```

2. **Создать свой функциональный интерфейс с `throws`:**
```java
@FunctionalInterface
public interface CheckedConsumer<T> {
    void accept(T t) throws Exception;
}

public static <T> Consumer<T> unchecked(CheckedConsumer<T> consumer) {
    return t -> {
        try {
            consumer.accept(t);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    };
}

// Использование
files.forEach(unchecked(path -> Files.readString(path)));
```

3. **Sneaky throws** (трюк со стиранием типов — см. Q18)

Подробнее о лямбдах — в [вопросах по Java 8](java-8-interview.md).

> [!mcq]
> - [ ] Стандартные функциональные интерфейсы (`Function`, `Consumer`) объявляют `throws Exception`, поэтому checked-исключения внутри лямбд бросаются без проблем. | Они НЕ объявляют `throws` checked — именно поэтому требуется обёртывание (`UncheckedIOException`) или кастомный `ThrowingFunction`. ❌ ПОСЛЕДСТВИЕ: разработчик пишет `paths.stream().map(Files::readString)` уверенный в работе, билд красный, копирует обходные паттерны со SO без понимания — техдолг растёт.
> - [ ] Unchecked-исключения внутри лямбды компилятор запрещает бросать, требуя оборачивать их в `try-catch`. | Unchecked (`RuntimeException`, NPE) работают в лямбдах напрямую — проблема только с checked. ❌ ПОСЛЕДСТВИЕ: разработчик оборачивает каждый `IllegalArgumentException` в try-catch внутри stream-pipeline, читаемость падает, защитный код вытесняет бизнес-логику.
> - [x] Checked-исключения внутри лямбды нужно оборачивать в unchecked (например, `UncheckedIOException`) или использовать кастомный функциональный интерфейс с `throws`. | Стандартные интерфейсы не объявляют checked в сигнатуре, поэтому обходные пути — обёртка через `UncheckedIOException` или `ThrowingFunction.wrap` с собственным `throws Exception`. ✓ ПРИМЕНЯТЬ: `Files.lines(path)` использует `UncheckedIOException` как стандарт JDK для I/O в stream-цепочках. 📋 ПРАВИЛО: «checked в lambda — обернуть в unchecked». 🔗 См. Q15, Q39.
> - [ ] Любые исключения из лямбды автоматически проглатываются Stream API, поэтому обработку нужно делать через `Optional`. | Stream API не проглатывает исключения — они пробрасываются из терминальной операции (`collect`, `forEach`); `Optional` не связан с error-handling. ❌ ПОСЛЕДСТВИЕ: разработчик надеется на «магическое проглатывание», NPE из `map` уходит в default-handler, batch-обработка из 1000 элементов падает на первом — потеря 999 успешных.

> [!mcq]
> - [ ] В асинхронных pipeline `CompletableFuture` исключения из лямбд `thenApply`/`thenCompose` обрываются молча — без `try-catch` они просто игнорируются. | Они оборачиваются в `CompletionException` и пробрасываются по цепочке до первого `exceptionally`/`handle`/`get()` — не игнорируются, а откладываются. ❌ ПОСЛЕДСТВИЕ: команда не ставит `.exceptionally`, future остаётся в failed-состоянии без логов; `cf.thenAccept(...)` молчит, через сутки QA замечает «событие не отправлено» — исходная ошибка утеряна.
> - [ ] `CompletableFuture.exceptionally` ловит только checked-исключения, для unchecked нужно использовать `handle` или `whenComplete`. | `exceptionally` принимает `Function<Throwable, T>` и срабатывает на любой `Throwable`, включая unchecked, checked и `Error`; разделения по checked/unchecked у CompletableFuture нет в принципе — все упаковываются в `CompletionException`. ❌ ПОСЛЕДСТВИЕ: разработчик дублирует логику recovery в `whenComplete` для NPE и в `exceptionally` для IOException, поведение расходится между ветками, refactor болезненный, на проде fallback срабатывает не для всех типов сбоев — частичная деградация сервиса.
> - [ ] В `CompletableFuture` исключение из лямбды автоматически конвертируется в `RuntimeException` и теряет оригинальный тип, поэтому `instanceof`-проверки бесполезны. | Оригинальный `Throwable` сохраняется как cause внутри `CompletionException`; через `getCause()` или `Throwable.unwrap` тип доступен. ❌ ПОСЛЕДСТВИЕ: разработчик пишет `if (ex instanceof IOException)` и проверка не срабатывает (видит `CompletionException`), retry на сетевые ошибки не работает, биллинг-сервис теряет транзакции под нагрузкой.
> - [x] Для асинхронных лямбд в `CompletableFuture` используются `exceptionally(Throwable -> T)` для recovery и `handle(BiFunction<T,Throwable,U>)` для unified-обработки результата и ошибки в одной точке. | `exceptionally` срабатывает только при ошибке и даёт fallback; `handle` всегда вызывается и получает либо результат, либо причину; `whenComplete` — side-effect без трансформации. ✓ ПРИМЕНЯТЬ: Spring `@Async` сервисы с downstream-вызовами через `WebClient` — `exceptionally` для retry/fallback, `handle` для metrics + конвертации. 📋 ПРАВИЛО: «exceptionally — recovery, handle — unified, whenComplete — side-effect». 🔗 См. Q15, Q25.

## Q16. Как переопределить метод, выбрасывающий `Exception`?

Правила контракта `throws` при переопределении (контравариантность по исключениям):

| Родительский метод | Дочерний метод может |
|---|---|
| Нет `throws` | Только unchecked (любые) |
| `throws IOException` | `throws FileNotFoundException` (уже или равно), не `throws Exception` (шире) |
| `throws RuntimeException` | Любые unchecked |

```java
class Parent {
    void process() throws IOException { }
}

class Child extends Parent {
    @Override
    void process() throws FileNotFoundException { }  // OK: подтип IOException
    // void process() throws Exception { }           // ОШИБКА: шире чем IOException
    // void process() throws SQLException { }        // ОШИБКА: другой checked
}
```

Причина: вызывающий код работает с типом `Parent` и обрабатывает только `IOException`. Если бы `Child` мог бросать `SQLException`, обработчик бы его не поймал.

Это связано с принципом подстановки Лисков (LSP) — подробнее в [вопросах по Java OOP](java-oop-interview.md).

> [!mcq]
> - [ ] Дочерний метод может расширять объявленный список checked-исключений, добавляя новые типы, не указанные в родительском. | Расширение нарушает LSP — caller, работающий с родителем, не обрабатывает новые типы; компилятор запрещает. ❌ ПОСЛЕДСТВИЕ: разработчик пишет `class ChildImpl { void process() throws SQLException }` поверх `Parent.process() throws IOException`, билд падает с «overridden method does not throw SQLException», MR блокируется.
> - [ ] Дочерний метод обязан объявлять тот же список `throws`, что и родительский, без каких-либо изменений. | Список можно сужать (убирать или заменять на подтипы) — лишь бы не расширять; это контравариантность по исключениям. ❌ ПОСЛЕДСТВИЕ: разработчик копирует `throws IOException, SQLException, JsonProcessingException` в каждой реализации интерфейса, API замусорен, caller вынужден ловить лишнее.
> - [ ] Дочерний метод может бросать любые unchecked-исключения, но только если родитель их явно объявил в `throws`. | Unchecked не требуют декларации в `throws` ни на каком уровне иерархии; их можно бросать свободно. ❌ ПОСЛЕДСТВИЕ: разработчик переопределяет интерфейс `Repository.find()` без объявленных runtime-исключений, не пишет `Objects.requireNonNull` веря, что unchecked нужно декларировать — NPE из `null`-аргумента уходит без диагностики.
> - [x] Дочерний метод может сужать список checked-исключений (до подтипа или убирать совсем) и свободно бросать unchecked — но не расширять. | Контравариантность по исключениям из LSP: `throws FileNotFoundException` вместо `throws IOException` корректно, `throws Exception` — нет. ✓ ПРИМЕНЯТЬ: Spring Data JPA `Repository.findAll()` не объявляет checked-исключения; реализация `JpaRepository` не может расширять контракт checked'ом. 📋 ПРАВИЛО: «дочерний throws ⊆ родительского». 🔗 См. Q2, Q5.

## Q17. Будет ли компилироваться следующий код?

```java
void doSomething() {
    throw new RuntimeException(new Exception("Chained Exception"));
}
```

**Да**, код компилируется. `RuntimeException` — unchecked, поэтому не требует `throws` в сигнатуре. Внутренний `Exception` передаётся как `cause` через конструктор `RuntimeException(Throwable cause)` — это обычная цепочка исключений. Компилятор проверяет только тип **выбрасываемого** исключения, а не его причины.

> [!mcq]
> - [ ] Код не компилируется, потому что `Exception` — checked, и его нужно либо обрабатывать в `try-catch`, либо декларировать в `throws`. | `Exception` здесь — аргумент конструктора, не самостоятельно выбрасываемое исключение; декларация `throws` нужна только для реально бросаемых checked-типов. ❌ ПОСЛЕДСТВИЕ: разработчик добавляет `throws Exception` в `doSomething()` «на всякий случай», IDE подсвечивает как redundant, на code-review MR откатывают за «избыточный контракт».
> - [ ] Код не компилируется, потому что `RuntimeException` нельзя создавать с `cause` типа `Exception` — только `RuntimeException`. | Конструктор `RuntimeException(Throwable)` принимает любой `Throwable`, включая checked — это базовая семантика exception chaining. ❌ ПОСЛЕДСТВИЕ: разработчик не оборачивает `IOException` в `RuntimeException` думая, что нельзя, теряет cause при пробросе через слой — root cause недоступен в логах.
> - [x] Код компилируется, потому что бросается `RuntimeException` (unchecked), а `Exception` внутри — всего лишь `cause` и проверяется только тип самого выброса. | Компилятор смотрит на тип объекта в `throw`, а не на его поля; cause может быть любым `Throwable`, включая checked. ✓ ПРИМЕНЯТЬ: Spring `JdbcTemplate` оборачивает checked `SQLException` в unchecked `DataAccessException` с сохранением cause — caller не обязан декларировать `throws`. 📋 ПРАВИЛО: «`throws` проверяет тип выброса, не cause». 🔗 См. Q5, Q8, Q35.
> - [ ] Код компилируется, но только если добавить `throws Exception` в сигнатуру метода `doSomething`. | Декларация `throws` не требуется — бросается unchecked-тип, compiler доволен. ❌ ПОСЛЕДСТВИЕ: разработчик из перестраховки пишет `throws Exception` везде, caller вынужден ловить общий `Exception`, типизация теряется, статический анализ бесполезен.

## Q18. Есть ли способ выбросить checked `Exception` из метода без `throws`?

Да, через трюк с дженериками и стиранием типов (**sneaky throw**):

```java
@SuppressWarnings("unchecked")
public static <T extends Throwable> void sneakyThrow(Throwable ex) throws T {
    throw (T) ex;  // компилятор выводит T = RuntimeException
}

public void methodWithoutThrows() {
    sneakyThrow(new IOException("Checked, но без throws!"));
    // Компилятор считает, что бросается RuntimeException
    // В runtime реально летит IOException
}
```

Этот подход используется в библиотеках (`Lombok @SneakyThrows`, `Vavr`). Однако это **антипаттерн** в бизнес-коде — вызывающий код не ожидает checked exception и не обрабатывает его.

> [!mcq]
> - [ ] Нет, компилятор строго запрещает бросать checked-исключения без `throws` и не обойти это средствами языка. | Через type erasure и generic-метод (sneaky throw) компилятор обманывается; `Lombok @SneakyThrows` и Vavr используют этот приём. ❌ ПОСЛЕДСТВИЕ: разработчик не знает о sneaky throw, на собеседовании теряет балл; в коде вместо чистого Spring-`@SneakyThrows` плодит ручные `throws` через все слои API.
> - [ ] Да, но только через JNI и вызов нативного кода, реализующего `Throwable.throw` на низком уровне JVM. | JNI не нужен — трюк реализуется на чистой Java через generics и type erasure. ❌ ПОСЛЕДСТВИЕ: разработчик пишет JNI-код для «безобидного hack'а», добавляет нативную зависимость, ломает кросс-платформенность билда, deployment-pipeline ломается на ARM64.
> - [ ] Да, но только для исключений, помеченных аннотацией `@Unchecked`, введённой в Java 17. | Такой аннотации в Java нет; checked/unchecked определяется иерархией наследования от `RuntimeException`. ❌ ПОСЛЕДСТВИЕ: разработчик добавляет `@Unchecked` ожидая магии, IDE подсвечивает как unknown аннотацию, compile проходит, но семантика не меняется — путаница и потеря времени.
> - [x] Да, через sneaky throw — трюк со стиранием типов и generic-методом `<T extends Throwable>`, который обманывает compile-time проверку. | Во время компиляции `T` выводится как `RuntimeException`, в runtime летит реальный checked-тип; `Lombok @SneakyThrows`/Vavr — стандартные реализации. ✓ ПРИМЕНЯТЬ: `Lombok @SneakyThrows` в lambda-обёртках Stream API, чтобы избежать `try-catch` boilerplate. Использовать осознанно — caller не подозревает о checked. 📋 ПРАВИЛО: «sneaky throw — обход компилятора через generic erasure». 🔗 См. Q15, Q39.

## Q19. (!) Когда создавать кастомное исключение и как его правильно оформить?

**Когда создавать:**
- Нужна доменная семантика (например, `OrderNotFoundException`, `InsufficientFundsException`)
- Нужно передать дополнительный контекст (коды ошибок, ID ресурсов)
- Стандартные исключения недостаточно точно описывают ситуацию

**Правила оформления:**

```java
public class OrderNotFoundException extends RuntimeException {

    private final String orderId;

    // Минимум 2 конструктора: с message и с message + cause
    public OrderNotFoundException(String orderId) {
        super("Заказ не найден: " + orderId);
        this.orderId = orderId;
    }

    public OrderNotFoundException(String orderId, Throwable cause) {
        super("Заказ не найден: " + orderId, cause);
        this.orderId = orderId;
    }

    public String getOrderId() {
        return orderId;
    }
}
```

**Best practices:**
- Суффикс `Exception` в имени класса
- Наследовать от наиболее подходящего типа, а не просто от `Exception`
- Обязательно конструктор с `Throwable cause` для цепочки
- Сделать поля `final` и класс `Serializable` (если может сериализоваться)
- Не создавать слишком глубокую иерархию — обычно достаточно 1-2 уровней
- Переиспользовать стандартные исключения (`IllegalArgumentException`, `IllegalStateException`) где уместно

> [!mcq]
> - [x] Кастомное исключение должно иметь конструкторы с message и с message+cause, включать контекстные поля (final) и суффикс `Exception` в имени. | Три правила обеспечивают chaining (cause), диагностический контекст (контекстные поля) и читаемость (суффикс); опционально `serialVersionUID` при сериализации. ✓ ПРИМЕНЯТЬ: Spring `OptimisticLockingFailureException(String message, Throwable cause)` + наследники — образцовая реализация. 📋 ПРАВИЛО: «message+cause+context+суффикс Exception». 🔗 См. Q10, Q21, Q38.
> - [ ] Кастомное исключение должно иметь только один конструктор без параметров — `message` и `cause` устанавливаются через сеттеры после создания. | `Throwable` не имеет сеттеров для `message`; стандарт — минимум два конструктора (message и message+cause). ❌ ПОСЛЕДСТВИЕ: разработчик создаёт `MyException()` + setter-методы, обнаруживает что `setMessage` не существует, костылит через reflection, тесты падают на immutability проверках.
> - [ ] Кастомное исключение должно наследоваться строго от `Exception` или `Throwable`, но не от `RuntimeException` — последний зарезервирован для JVM. | `RuntimeException` предназначен для наследования (`IllegalArgumentException`, `IllegalStateException`); современная практика — большинство доменных исключений unchecked. ❌ ПОСЛЕДСТВИЕ: команда выбирает checked-наследование, каждый сервисный метод обвешен `throws OrderException, PaymentException`, при добавлении нового исключения переписывается 50 сигнатур.
> - [ ] Кастомное исключение должно иметь метод `handle()` для самостоятельной обработки — это снимает ответственность с вызывающего кода. | Обработка — ответственность catch-блока, не самого исключения; `handle()` в классе исключения смешивает данные и поведение. ❌ ПОСЛЕДСТВИЕ: разработчик пишет `e.handle()` в catch вместо нормального error-handling, при тестировании mock-фреймворк не знает что мокать, тесты переплетены с self-handling логикой.

## Q20. Когда использовать `RuntimeException` vs checked `Exception`?

| Критерий | Checked `Exception` | `RuntimeException` |
|----------|---------------------|--------------------|
| Когда | Вызывающий может и должен восстановиться | Ошибка программиста или невосстановимая ситуация |
| Примеры | `IOException`, `SQLException` | `NullPointerException`, `IllegalArgumentException` |
| Тренд индустрии | Реже в новых API | Чаще — `Spring`, `Hibernate`, `Kotlin` |
| `throws` в сигнатуре | Обязательно | Нет |

Современная практика (особенно в `Spring`-экосистеме): почти все доменные исключения — **unchecked**. Checked используются только для ситуаций, где вызывающий код **обязан** принять решение (повторить, использовать fallback и т.д.).

> [!mcq]
> - [ ] Использовать `RuntimeException` только для критических ошибок (JVM), а все бизнес-ошибки делать checked для явной обработки. | Устаревший взгляд: Spring/Hibernate почти все доменные исключения делают unchecked для снижения boilerplate. ❌ ПОСЛЕДСТВИЕ: команда строит на checked, через 6 месяцев каждый сервисный метод обвешан `throws BusinessException, IOException, SQLException`, разработчики массово начинают писать `throws Exception` — checked-механизм теряет смысл.
> - [ ] Использовать checked exception всегда, когда ошибка возможна — это гарантирует, что компилятор заставит её обработать. | Избыточный checked приводит к `throws Exception`-загрязнению и паттерну «поймал и проглотил» через пустой catch. ❌ ПОСЛЕДСТВИЕ: разработчики, чтобы убрать compile errors, пишут `catch (Exception e) {}` пустыми, реальные ошибки теряются — `OrderProcessingException` не логируется, биллинг ошибки находятся через сутки.
> - [ ] Использовать `RuntimeException` для ошибок на уровне контроллера, а checked — для сервисного слоя, разделяя по архитектурным слоям. | Тип определяется семантикой ошибки (восстановима или нет), а не слоем; checked везде — это про reusability контракта, а не про слой. ❌ ПОСЛЕДСТВИЕ: при перемещении логики между слоями приходится перебрасывать checked в unchecked, тесты падают на mock-фреймворке, deployment затягивается.
> - [x] Использовать checked, когда вызывающий может и должен восстановиться (retry/fallback), иначе — `RuntimeException` для ошибок программиста и невосстановимых ситуаций. | Оригинальный критерий Джошуа Блоха: `IOException` при сетевой ошибке (можно повторить) — checked, `IllegalArgumentException` (баг) — unchecked. ✓ ПРИМЕНЯТЬ: `WebClient` бросает unchecked `WebClientResponseException`; Resilience4j retry на специфичных подтипах. 📋 ПРАВИЛО: «checked — обязан восстановиться, unchecked — баг или фатально». 🔗 См. Q5, Q19, Q21.

## Q21. (!) Best practices при проектировании иерархии исключений

1. **Наследовать от подходящего базового типа** — не от `Exception` напрямую, если есть более подходящий
2. **Включать контекст** — сообщение, cause, дополнительные поля
3. **Документировать `@throws`** в Javadoc
4. **Переиспользовать стандартные** где уместно:
   - `IllegalArgumentException` — невалидный аргумент
   - `IllegalStateException` — объект в неправильном состоянии
   - `UnsupportedOperationException` — операция не поддерживается
   - `NullPointerException` — аргумент `null` (с Java 14 — информативное сообщение)
5. **Не создавать исключение на каждую ошибку** — группировать по смыслу
6. **Именование**: `<Причина>Exception` — `OrderNotFoundException`, `PaymentDeclinedException`

```java
// Хорошая иерархия для платёжной системы
public abstract class PaymentException extends RuntimeException { ... }
public class PaymentDeclinedException extends PaymentException { ... }
public class PaymentTimeoutException extends PaymentException { ... }
public class InsufficientFundsException extends PaymentException { ... }

// Вызывающий код может ловить как конкретные, так и общий PaymentException
try {
    paymentService.charge(order);
} catch (InsufficientFundsException ex) {
    notifyUser("Недостаточно средств");
} catch (PaymentException ex) {
    retryLater(order);
}
```

> [!mcq]
> - [ ] Создавать одно общее исключение на весь модуль без подтипов — простота важнее гранулярности. | Теряется семантика: caller не различит «недостаточно средств» (показать UI) и «timeout» (retry) по типу — придётся парсить строки message. ❌ ПОСЛЕДСТВИЕ: фронт делает `if (err.message.contains("funds"))` — рефакторинг сообщений в backend ломает UI без compile-ошибок, инциденты находят пользователи.
> - [ ] Наследовать все доменные исключения напрямую от `Throwable`, минуя `Exception` — это уменьшает overhead. | Наследование от `Throwable` обходит инфраструктуру catch-блоков, которые ловят `Exception`/`RuntimeException`; overhead одинаков. ❌ ПОСЛЕДСТВИЕ: разработчик создаёт `MyDomainError extends Throwable`, `@ExceptionHandler(Exception.class)` его не ловит, в production клиент видит unhandled stack trace.
> - [ ] Создавать максимально глубокую иерархию (5+ уровней) для каждого нюанса ошибки — это упрощает рефакторинг. | Глубокая иерархия усложняет поддержку: catch-блок не знает какой уровень ловить, рефакторинг каскадный. Практика — 1-2 уровня. ❌ ПОСЛЕДСТВИЕ: иерархия `Throwable → DomainException → BusinessException → PaymentException → CardException → DeclinedException` — 6 уровней, при добавлении нового типа разработчик не знает куда вставить, copy-paste разрастается.
> - [x] Создать абстрактный базовый класс домена (`PaymentException`) и наследовать от него конкретные подтипы, чтобы можно было ловить как общий родитель, так и частный случай. | Гибкость: caller может отреагировать на конкретный `InsufficientFundsException` или fallback на общий `PaymentException` для retry. ✓ ПРИМЕНЯТЬ: Spring Data `DataAccessException` → `DataIntegrityViolationException` → `DuplicateKeyException` — образцовая 3-уровневая иерархия с reuse в `@ExceptionHandler`. 📋 ПРАВИЛО: «1-2 уровня: domain root + concrete subtypes». 🔗 См. Q10, Q19, Q38.

## Q22. Что такое `try-catch-finally` и каков порядок выполнения?

Порядок выполнения:

```mermaid
graph TD
    A["try блок"] -->|"Нет исключения"| B["finally блок"]
    A -->|"Исключение"| C{"catch соответствует?"}
    C -->|"Да"| D["catch блок"]
    C -->|"Нет"| E["finally блок"]
    D --> F["finally блок"]
    B --> G["Продолжение программы"]
    F --> G
    E --> H["Исключение пробрасывается выше"]
```

```java
public String readFirstLine(String path) {
    BufferedReader reader = null;
    try {
        reader = new BufferedReader(new FileReader(path));
        return reader.readLine(); // (1) return здесь
    } catch (IOException ex) {
        log.error("Ошибка чтения", ex);
        return "default";          // (2) или return здесь
    } finally {
        // (3) Выполнится ВСЕГДА — даже после return!
        if (reader != null) {
            try { reader.close(); } catch (IOException ignored) {}
        }
    }
}
```

Начиная с `Java 7`, предпочтительнее использовать `try-with-resources` (см. Q12).

> [!mcq]
> - [ ] Блок `finally` выполняется только если в `try` не было исключения, для очистки ресурсов после успешной операции. | `finally` выполняется всегда: и при нормальном завершении, и при исключении, и при `return` из `try`/`catch` — в этом его суть. ❌ ПОСЛЕДСТВИЕ: разработчик помещает `connection.close()` после `try`-блока (а не в finally), при exception — connection leak, через 24 часа сервис исчерпывает pool, новые запросы зависают.
> - [ ] Блок `finally` выполняется только если соответствующий `catch` пойман, иначе исключение проходит мимо него к вышестоящему обработчику. | `finally` выполняется даже если ни один `catch` не подошёл, после чего исключение пробрасывается дальше по стеку. ❌ ПОСЛЕДСТВИЕ: разработчик пишет cleanup в catch блоке (один из), при unhandled exception cleanup не происходит — file handle leak, при загрузке постепенно ломается.
> - [ ] Блок `finally` можно опустить, только если есть минимум три блока `catch`, иначе компилятор потребует его наличия. | Правило простое: `try` требует минимум один `catch` ИЛИ `finally`; количество `catch` не влияет на обязательность finally. ❌ ПОСЛЕДСТВИЕ: разработчик плодит лишние catch-блоки «для соблюдения правила трёх», compiler доволен, но dead code раздувает code-base.
> - [x] Блок `finally` выполняется всегда: и после успешного `try`, и после пойманного `catch`, и даже при `return` из них. | Единственный способ не выполнить `finally` — `System.exit()`, фатальный сбой JVM или бесконечный цикл в `try`. ✓ ПРИМЕНЯТЬ: legacy-cleanup до Java 7 (без try-with-resources) — `finally { closeQuietly(stream); }` гарантирует освобождение ресурсов. 📋 ПРАВИЛО: «`finally` всегда, кроме System.exit и infinite loop». 🔗 См. Q3, Q23, Q37.

## Q23. Что произойдёт, если в `finally` есть `return`?

`return` в блоке `finally` **перезаписывает** `return` из `try` или `catch` — это известный антипаттерн:

```java
public int getValue() {
    try {
        return 1;
    } finally {
        return 2; // ВСЕГДА вернёт 2!
    }
}
```

Ещё хуже — `return` в `finally` **подавляет исключения**:

```java
public int getValue() {
    try {
        throw new RuntimeException("Ошибка!");
    } finally {
        return 0; // Исключение ПОТЕРЯНО! Метод тихо вернёт 0
    }
}
```

Это одна из причин, почему **никогда не следует использовать `return` в `finally`**. IDE и статические анализаторы предупреждают об этом.

> [!mcq]
> - [ ] `return` в `finally` игнорируется JVM, фактический возврат идёт из `try`/`catch`. | Неверно: JIT-байткод выполняет `finally` после загрузки return-значения, и `return` в `finally` перетирает его. ❌ ПОСЛЕДСТВИЕ: разработчик уверен что вернул `1`, юнит-тест зелёный на mock'е, в проде функция возвращает `2` — silent data corruption.
> - [x] `return` в `finally` перезаписывает `return` из `try`/`catch` И подавляет проброс исключения. | `finally` выполняется последним, его `return` затирает значение и «съедает» исключение. ✓ ПРИМЕНЯТЬ: никогда не возвращать из `finally` — только `close`/`unlock`. 📋 ПРАВИЛО: «finally for cleanup, never for control flow». 🔗 См. Q22.
> - [ ] Компилятор Java запрещает `return` в `finally` начиная с Java 7. | Неверно: компилятор лишь выдаёт warning (`finally block does not complete normally`), код собирается и работает. ❌ ПОСЛЕДСТВИЕ: команда полагается на «компилятор поймает» — баг доезжает до прода и тихо ломает API-контракт.
> - [ ] `return` в `finally` пробрасывает исключение, но возвращает значение из `finally` через `getCause()`. | Неверно: исключение полностью теряется, метод возвращается нормально, у вызывающего нет способа узнать о сбое. ❌ ПОСЛЕДСТВИЕ: `OrderService.charge()` бросает `PaymentDeclined`, `finally { return null; }` глотает — биллинг видит «успех» при отказе банка.

## Q24. Как перехватить все исключения (`catch Throwable`)?

`catch (Throwable t)` перехватывает и `Exception`, и `Error`:

```java
try {
    riskyOperation();
} catch (Throwable t) {
    log.error("Критическая ошибка", t);
    // Попытка graceful shutdown
}
```

**Когда допустимо ловить `Throwable`:**
- В верхнеуровневом обработчике потока (логирование перед завершением)
- В фреймворках (контейнер сервлетов, `Spring`)
- В `UncaughtExceptionHandler`

**Когда НЕ надо:**
- В обычном бизнес-коде — ловить конкретные типы
- Нельзя «восстановиться» после `OutOfMemoryError` или `StackOverflowError`

Правило: в бизнес-логике ловите `Exception` или конкретные подтипы; `Throwable` — только на границах системы.

> [!mcq]
> - [ ] `catch (Throwable t)` безопасно использовать везде — он ловит и `Exception`, и `Error`, упрощая код. | Неверно: после `OutOfMemoryError` или `StackOverflowError` JVM в неконсистентном состоянии, продолжать работу нельзя. ❌ ПОСЛЕДСТВИЕ: сервис ловит OOM, «возвращается» в while-loop, через минуту всё снова падает — Kubernetes liveness не видит проблему, restart не происходит.
> - [ ] `catch (Throwable t)` идентичен `catch (Exception e)` — `Error` всё равно ловится через `Exception`. | Неверно: `Error` и `Exception` — независимые подклассы `Throwable`, `catch (Exception)` не поймает `OutOfMemoryError`. ❌ ПОСЛЕДСТВИЕ: разработчик уверен что «всё под контролем», `OutOfMemoryError` пролетает мимо, поток умирает молча, jobs встают.
> - [x] `catch (Throwable t)` оправдан только на границе системы (top-level worker, `UncaughtExceptionHandler`) для логирования и graceful shutdown. | В бизнес-коде ловить конкретные подтипы; `Throwable` — последняя линия обороны для алёрта. ✓ ПРИМЕНЯТЬ: `Thread.setDefaultUncaughtExceptionHandler` для всех потоков пула. 📋 ПРАВИЛО: «Throwable only at system boundaries». 🔗 См. Q25.
> - [ ] `catch (Throwable t)` достаточно для перехвата `System.exit()` и нативных сбоев JVM. | Неверно: `System.exit()` бросает `SecurityException` только при наличии `SecurityManager`; SIGSEGV и hard-VM-error через try/catch не ловятся. ❌ ПОСЛЕДСТВИЕ: вера в «универсальный catch» приводит к отсутствию external supervisor — JVM падает без алёрта.

## Q25. (!) Как обработать исключения в многопоточном коде?

Исключение в потоке **не пробрасывается** в создавший его поток — каждый поток имеет свой стек. Способы обработки:

**1. `Thread.setUncaughtExceptionHandler`** — для «сырых» потоков:

```java
Thread thread = new Thread(() -> {
    throw new RuntimeException("Ошибка в потоке");
});
thread.setUncaughtExceptionHandler((t, ex) ->
    log.error("Необработанное исключение в потоке {}: {}", t.getName(), ex.getMessage(), ex)
);
thread.start();
```

**2. `Future.get()`** — через `ExecutorService`:

```java
ExecutorService executor = Executors.newSingleThreadExecutor();
Future<String> future = executor.submit(() -> {
    throw new IOException("Ошибка I/O");
});

try {
    String result = future.get(); // блокирующий вызов
} catch (ExecutionException ex) {
    Throwable cause = ex.getCause(); // IOException — оригинальное исключение
    log.error("Задача завершилась с ошибкой", cause);
}
```

**3. `CompletableFuture`** — реактивная обработка:

```java
CompletableFuture.supplyAsync(() -> riskyOperation())
    .exceptionally(ex -> {
        log.error("Ошибка: {}", ex.getMessage());
        return fallbackValue;
    })
    .thenAccept(result -> process(result));
```

Подробнее — в [вопросах по Java Concurrency](java-concurrency-interview.md).

> [!mcq]
> - [ ] Исключение из `Runnable.run()` автоматически пробрасывается в поток, вызвавший `thread.start()`. | Неверно: каждый поток имеет свой стек, исключение не пересекает границы потоков, вызывающий не узнает о сбое. ❌ ПОСЛЕДСТВИЕ: `executor.submit(task)` без `future.get()` — таска кидает `NPE`, лог пуст, бизнес-операция не выполнена, инцидент обнаружится через сутки.
> - [ ] `Future.get()` возвращает `null` при сбое в задаче, проверять надо через `future.isDone()`. | Неверно: `future.get()` бросает `ExecutionException` с оригинальным `cause`, `isDone()` true и при успехе, и при сбое. ❌ ПОСЛЕДСТВИЕ: код проверяет только `isDone()`, считает результат успехом, NPE-ошибка из таски молча игнорируется.
> - [x] Использовать `Future.get()` (ловить `ExecutionException` и разворачивать `getCause()`) или `CompletableFuture.exceptionally()` для реактивного pipeline. | Только эти механизмы поднимают ошибку из worker-потока в caller. ✓ ПРИМЕНЯТЬ: `submit` для проверяемого результата, `CompletableFuture` для async-цепочек. 📋 ПРАВИЛО: «exception crosses threads only via Future or handler». 🔗 См. Q26.
> - [ ] `executor.execute(runnable)` гарантирует пробрасывание исключения через `RejectedExecutionException`. | Неверно: `RejectedExecutionException` бросается только при отказе очереди (saturation), runtime-ошибки внутри задачи теряются. ❌ ПОСЛЕДСТВИЕ: батч из 1000 заказов через `execute()` — половина падает с `NPE`, лог пуст, метрика «processed» врёт.

## Q26. Что такое `UncaughtExceptionHandler`?

Интерфейс `Thread.UncaughtExceptionHandler` вызывается, когда поток завершается из-за необработанного исключения:

```java
// Глобальный обработчик для всех потоков
Thread.setDefaultUncaughtExceptionHandler((thread, ex) -> {
    log.error("FATAL: Необработанное исключение в потоке '{}': {}",
        thread.getName(), ex.getMessage(), ex);
    // Можно отправить алерт, записать метрику и т.д.
});
```

Порядок поиска обработчика JVM:
1. Обработчик конкретного потока (`thread.setUncaughtExceptionHandler`)
2. Обработчик `ThreadGroup`
3. Дефолтный обработчик (`Thread.setDefaultUncaughtExceptionHandler`)
4. Если не найден — стек-трейс в `System.err`

> [!mcq]
> - [x] `UncaughtExceptionHandler` срабатывает только при необработанном исключении в потоке: ищется handler потока → `ThreadGroup` → default → `System.err`. | Цепочка делегирования: установить default-handler — last-resort защита. ✓ ПРИМЕНЯТЬ: `setDefaultUncaughtExceptionHandler` в `main()` для алёрта на необработанные ошибки. 📋 ПРАВИЛО: «every thread leak must hit default handler». 🔗 См. Q26.
> - [ ] `UncaughtExceptionHandler` вызывается для любого исключения в потоке, включая пойманные через `try/catch`. | Неверно: handler срабатывает только когда исключение поднимается до `Thread.run()` без обработки. ❌ ПОСЛЕДСТВИЕ: рассчитывать на handler как на «общий лог» — он молчит при наличии `catch (Exception e)`, реальные ошибки не попадают в alerting.
> - [ ] `UncaughtExceptionHandler` для `ExecutorService` устанавливается через `executor.setUncaughtExceptionHandler()`. | Неверно: такого метода у `ExecutorService` нет — handler ставится через `ThreadFactory` при создании executor'а. ❌ ПОСЛЕДСТВИЕ: код компилируется через рефлексию IDE-подсказки, runtime — `NoSuchMethodError`, либо handler никогда не цепляется.
> - [ ] `Thread.setDefaultUncaughtExceptionHandler(null)` удаляет handler и восстанавливает поведение по умолчанию из JLS. | Неверно: после `null` уже установленный default остаётся (метод не сбрасывает) — точнее, он сбрасывает, но `getDefaultUncaughtExceptionHandler()` возвращает `null`, и поток печатает в `System.err` напрямую. ❌ ПОСЛЕДСТВИЕ: misconception приводит к попыткам «сбросить» handler в тестах, остаются глобальные побочные эффекты между тестами.

## Q27. (!) Как правильно логировать исключения?

```java
// ПРАВИЛЬНО — передаём исключение как последний аргумент
log.error("Ошибка обработки заказа orderId={}", orderId, exception);

// НЕПРАВИЛЬНО — теряется stack trace!
log.error("Ошибка: " + exception.getMessage());

// НЕПРАВИЛЬНО — конкатенация строк (performance)
log.error("Ошибка: " + exception);

// НЕПРАВИЛЬНО — printStackTrace() вместо логгера
exception.printStackTrace(); // идёт в System.err, не в лог-файл
```

| Уровень | Когда использовать |
|---------|-------------------|
| `ERROR` | Неожиданные ошибки, требующие внимания |
| `WARN` | Ожидаемые ошибки: retry, fallback |
| `DEBUG` | Бизнес-валидация, информационные |

**Антипаттерны:**
- «Проглотить» исключение: пустой `catch` без логирования
- Логировать И пробрасывать — двойное логирование одной ошибки
- Логировать только `getMessage()` без стек-трейса

Подробнее — в [вопросах по логированию](../../logging/logging-interview.md).

> [!mcq]
> - [ ] `log.error("Ошибка: " + e.getMessage())` — корректный паттерн, лаконично включает суть ошибки. | Неверно: конкатенация теряет stack trace, `getMessage()` часто `null` (NPE), нет cause-цепочки. ❌ ПОСЛЕДСТВИЕ: в логе строка «Ошибка: null» — невозможно понять что упало, post-mortem занимает часы вместо минут.
> - [ ] `e.printStackTrace()` достаточно для production — JVM перенаправит вывод в лог-файл. | Неверно: `printStackTrace()` пишет в `System.err`, не в logback/log4j; в k8s/Docker stderr может быть отдельным потоком, MDC не применяется. ❌ ПОСЛЕДСТВИЕ: трейсы попадают в stderr контейнера, ELK их не парсит, MDC (`requestId`, `userId`) теряется.
> - [x] `log.error("Ошибка обработки orderId={}", orderId, exception)` — exception как последний аргумент, контекст через placeholder'ы. | SLF4J распознаёт последний `Throwable`-аргумент и форматирует stack trace отдельно. ✓ ПРИМЕНЯТЬ: всегда передавать exception как последний параметр + бизнес-ID. 📋 ПРАВИЛО: «exception always last, never in message». 🔗 См. Q27.
> - [ ] Логировать на каждом уровне catch-блоков — для полной картины stack trace в логах. | Неверно: double-logging — одна ошибка попадает в лог 3-5 раз, заполняя storage и путая investigation. ❌ ПОСЛЕДСТВИЕ: 10MB логов на 1 ошибку, alerting срабатывает 5 раз на один инцидент, дежурный получает дубликаты.

## Q28. Как тестировать код, выбрасывающий исключения?

**JUnit 5 — `assertThrows`:**

```java
@Test
void shouldThrowWhenOrderNotFound() {
    var exception = assertThrows(OrderNotFoundException.class,
        () -> orderService.findById("non-existent-id")
    );

    assertEquals("non-existent-id", exception.getOrderId());
    assertThat(exception.getMessage()).contains("не найден");
}
```

**AssertJ — более выразительный синтаксис:**

```java
@Test
void shouldThrowWhenInvalidInput() {
    assertThatThrownBy(() -> service.process(null))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessageContaining("не может быть null")
        .hasNoCause();
}

// Или через catchThrowable
Throwable thrown = catchThrowable(() -> service.process(null));
assertThat(thrown).isInstanceOf(IllegalArgumentException.class);
```

**Проверка, что исключение НЕ выбрасывается:**

```java
@Test
void shouldNotThrow() {
    assertDoesNotThrow(() -> service.process(validInput));
}
```

Подробнее о тестировании — в [вопросах по модульному тестированию](../../testing/unit-testing-interview.md).

> [!mcq]
> - [ ] `try { service.process(null); fail(); } catch (Exception e) { /* ok */ }` — классический способ проверки исключения в JUnit. | Неверно: ловит `Exception` слишком широко (включая `NullPointerException` от багов в самом тесте), не проверяет тип исключения и сообщение. ❌ ПОСЛЕДСТВИЕ: тест зелёный когда метод бросает `NPE` вместо ожидаемого `IllegalArgumentException` — баг в production проходит в релиз.
> - [x] `assertThrows(OrderNotFoundException.class, () -> service.findById("x"))` или `assertThatThrownBy(...).isInstanceOf(...).hasMessageContaining(...)` | `JUnit 5` `assertThrows` возвращает само исключение для дальнейших проверок; `AssertJ` даёт fluent-API для типа, message, cause. ✓ ПРИМЕНЯТЬ: всегда проверять конкретный тип + ключевые поля сообщения. 📋 ПРАВИЛО: «assertThrows + проверка типа и контекста». 🔗 См. Q28.
> - [ ] `@Test(expected = OrderNotFoundException.class)` — современный идиоматичный способ в `JUnit 5`. | Неверно: атрибут `expected` существовал в `JUnit 4`, в `JUnit 5` он удалён в пользу `assertThrows`; нельзя проверить сообщение или поля. ❌ ПОСЛЕДСТВИЕ: код не компилируется на `JUnit 5`, миграция тестов превращается в массовые правки.
> - [ ] `assertDoesNotThrow(() -> service.process(input))` для всех тестов — гарантирует отсутствие непредвиденных ошибок. | Неверно: `assertDoesNotThrow` нужен только когда мы хотим явно зафиксировать, что метод НЕ бросает; в обычном happy-path он избыточен — достаточно проверить результат. ❌ ПОСЛЕДСТВИЕ: тесты раздуваются, теряется фокус на бизнес-логике, важные ассерты на возвращаемые значения теряются.

## Q29. Что такое `getCause()` и `initCause()`?

Методы `Throwable` для работы с цепочкой исключений:

```java
// getCause() — получить причину
try {
    processOrder();
} catch (OrderProcessingException ex) {
    Throwable root = ex.getCause();          // DataAccessException
    Throwable deeper = root.getCause();      // SQLException
    log.error("Корневая причина: {}", deeper.getMessage());
}

// initCause() — установить причину (один раз!)
IOException ioEx = new IOException("Ошибка чтения");
ioEx.initCause(new DiskFailureException("Диск повреждён"));
// Повторный вызов initCause() бросит IllegalStateException
```

Предпочтительнее использовать конструктор с `Throwable cause` вместо `initCause()` — это более идиоматично. Метод `initCause()` существует для обратной совместимости с исключениями, у которых нет конструктора с `cause`.

> [!mcq]
> - [ ] `initCause()` можно вызывать многократно — каждый вызов добавляет новую причину в цепочку. | Неверно: `initCause()` можно вызвать ТОЛЬКО ОДИН РАЗ; повторный вызов бросает `IllegalStateException`. Для нескольких связанных ошибок есть `addSuppressed()`. ❌ ПОСЛЕДСТВИЕ: код в catch-блоке падает с `IllegalStateException` поверх оригинальной ошибки — debugging усложняется, теряется первопричина.
> - [ ] `getCause()` возвращает корневую причину (root cause), пройдя всю цепочку до конца. | Неверно: `getCause()` возвращает только НЕПОСРЕДСТВЕННУЮ причину (один шаг), а не root cause; чтобы дойти до корня — `ExceptionUtils.getRootCause()` (Apache Commons) или ручной обход в while-цикле. ❌ ПОСЛЕДСТВИЕ: лог показывает промежуточный `DataAccessException` вместо реального `SQLException("connection refused")` — диагностика идёт по ложному пути.
> - [x] Предпочитать конструктор `new MyException(message, cause)` вместо `initCause()` — компилятор не даст забыть и код читается лучше. | Конструктор `Throwable(String, Throwable)` явно фиксирует причину при создании; `initCause` — legacy-API для исключений без такого конструктора. ✓ ПРИМЕНЯТЬ: всегда определять конструктор с `cause` в кастомных исключениях. 📋 ПРАВИЛО: «cause через конструктор, не через setter». 🔗 См. Q29.
> - [ ] Если у исключения нет cause, `getCause()` возвращает `null`, поэтому всегда нужен null-check. | Неверно: `getCause()` возвращает `null` только если cause не был установлен или равен `this` (отсутствует); для безопасного обхода цепочки используют `ExceptionUtils.getRootCause()` или цикл `while (cause.getCause() != null)`. ❌ ПОСЛЕДСТВИЕ: NPE в логирующем коде при попытке `e.getCause().getMessage()` — handler сам падает, оригинальная ошибка теряется.

## Q30. Что такое `AssertionError` и когда использовать `assert`?

`AssertionError` — наследник `Error`, выбрасывается при нарушении assert-условия:

```java
public void processAge(int age) {
    assert age >= 0 : "Возраст не может быть отрицательным: " + age;
    // ...
}
```

Ключевые моменты:
- По умолчанию assertions **отключены** в JVM; включаются флагом `-ea` (`-enableassertions`)
- Используются для **инвариантов** и **внутренних допущений** (не для проверки входных данных!)
- В тестах используется `assert` из JUnit/AssertJ, а не `assert` из языка
- **Не заменяют** валидацию аргументов — для этого `Objects.requireNonNull()`, `IllegalArgumentException`

```java
// НЕ использовать assert для проверки аргументов публичного API!
public void setName(String name) {
    // НЕПРАВИЛЬНО: assert name != null;
    // ПРАВИЛЬНО:
    Objects.requireNonNull(name, "name must not be null");
}
```

> [!mcq]
> - [x] `assert` — для внутренних инвариантов и допущений; для валидации публичного API использовать `Objects.requireNonNull` и `IllegalArgumentException`. | Assertions по умолчанию ВЫКЛЮЧЕНЫ в JVM (нужен флаг `-ea`); полагаться на них для валидации входных данных нельзя. ✓ ПРИМЕНЯТЬ: `assert` — только для проверки «такого не может быть» внутри метода. 📋 ПРАВИЛО: «assert для инвариантов, requireNonNull для контракта». 🔗 См. Q30.
> - [ ] `assert` идеально подходит для валидации параметров публичного API — он атомарен и быстрее `if`. | Неверно: `assert` ОТКЛЮЧЁН по умолчанию, в production проверка не сработает; кроме того `AssertionError` наследует `Error`, а не `Exception`, и не подходит для бизнес-валидации. ❌ ПОСЛЕДСТВИЕ: prod без флага `-ea` принимает `null`/невалидные значения, NPE/`IllegalStateException` всплывает на 5 уровней глубже — debugging идёт через всё приложение.
> - [ ] `AssertionError` следует ловить и обрабатывать в catch-блоках для надёжности кода. | Неверно: `AssertionError` — наследник `Error`, сигнализирует о баге в коде (нарушенный инвариант); ловить и продолжать — маскировать баг и оставлять JVM в неконсистентном состоянии. ❌ ПОСЛЕДСТВИЕ: программа продолжает работать с нарушенными предположениями, данные тихо портятся, баг проявляется через часы или дни в другом месте.
> - [ ] Assertions включены в JVM по умолчанию начиная с Java 11. | Неверно: assertions ВСЕГДА выключены по умолчанию во всех версиях Java; включаются явно флагом `-ea` (для всех классов) или `-ea:com.example...` (для пакета). ❌ ПОСЛЕДСТВИЕ: разработчик уверен, что `assert` сработает в prod, но проверки молча игнорируются — критические инварианты не проверяются.

## Q31. Как обработать `OutOfMemoryError` и `StackOverflowError`?

Оба наследуют `Error` и обычно сигнализируют о серьёзных проблемах, не решаемых в runtime.

**`OutOfMemoryError`:**
- Не ловить в бизнес-коде — после OOM состояние JVM непредсказуемо
- Диагностика: `-XX:+HeapDumpOnOutOfMemoryError` для автоматического heap dump
- Профилактика: мониторинг памяти, поиск утечек (Eclipse MAT, VisualVM), увеличение `-Xmx`
- Подробнее — в [вопросах по управлению памятью](../../performance/memory-management-interview.md)

**`StackOverflowError`:**
- Причина: слишком глубокая рекурсия (обычно бесконечная)
- Исправление: добавить базовый случай рекурсии, заменить на итеративный алгоритм
- Увеличить стек потока: `-Xss` (временная мера)

```java
// Безопасная рекурсия с базовым случаем
public int factorial(int n) {
    if (n < 0) throw new IllegalArgumentException("n < 0");
    if (n <= 1) return 1;        // базовый случай!
    return n * factorial(n - 1);
}
```

Ловить `Error` допустимо только для логирования и graceful shutdown.

> [!mcq]
> - [ ] При `OutOfMemoryError` нужно поймать его и освободить память (`cache.clear()`, `System.gc()`) для восстановления. | Неверно: после OOM JVM в непредсказуемом состоянии — finalize-методы могли частично сработать, потоки могли упасть в неконсистентном состоянии, lock-и могут быть не отпущены; `System.gc()` — лишь подсказка. ❌ ПОСЛЕДСТВИЕ: приложение продолжает работать с разрушенным состоянием, данные в БД пишутся частично, инциденты усложняются, root cause-утечка не диагностируется.
> - [ ] `StackOverflowError` решается увеличением `-Xss` до 32MB — это снимает проблему с рекурсией. | Неверно: `-Xss` — паллиатив, реальная причина обычно бесконечная или нерациональная рекурсия (отсутствие базового случая, циклические ссылки в `toString`); увеличение стека просто отодвигает падение. ❌ ПОСЛЕДСТВИЕ: при 1000 потоках памяти на стеки нужно `1000 × 32MB = 32GB` — OOM при старте; основной баг (рекурсия без выхода) остаётся.
> - [x] `OutOfMemoryError` не ловить в бизнес-коде; настроить `-XX:+HeapDumpOnOutOfMemoryError` и анализировать через Eclipse MAT/VisualVM; `StackOverflowError` исправлять в коде (базовый случай, итерация). | Оба — наследники `Error`, говорят о фундаментальных проблемах JVM/архитектуры; правильный путь — диагностика и фикс root cause, а не try-catch. ✓ ПРИМЕНЯТЬ: heap dump on OOM + jstack для SOE + переход к итерации/tail-call-замене. 📋 ПРАВИЛО: «Error диагностируем, не ловим». 🔗 См. Q31.
> - [ ] `Error` в Java можно безопасно ловить через `catch (Throwable e)` — это рекомендованный способ для критичных систем. | Неверно: `catch Throwable` ловит ещё и `ThreadDeath`, `OutOfMemoryError`, `LinkageError` — после них JVM/поток в неконсистентном состоянии; единственный валидный use-case — в самом верхнем слое (main/handler) для logging+graceful shutdown. ❌ ПОСЛЕДСТВИЕ: системные ошибки маскируются, потоки кажутся живыми, но не делают полезной работы; production «зависает» молча, без алертов.

## Q32. Что такое `fail-fast` и `fail-safe` в контексте исключений?

| Критерий | `Fail-fast` | `Fail-safe` |
|----------|------------|-------------|
| Поведение | При ошибке немедленно бросить исключение | Продолжить работу, избежав сбоя |
| Пример в коллекциях | `ArrayList` iterator → `ConcurrentModificationException` | `CopyOnWriteArrayList` — работает с копией |
| Пример в API | `Objects.requireNonNull()` в начале метода | Возврат default-значения или `Optional.empty()` |
| Когда использовать | Баги должны обнаруживаться рано | Доступность важнее строгой корректности |

```java
// Fail-fast: валидация аргументов в начале метода
public void createUser(String email, String name) {
    Objects.requireNonNull(email, "email");
    Objects.requireNonNull(name, "name");
    if (!email.contains("@")) {
        throw new IllegalArgumentException("Невалидный email: " + email);
    }
    // ... основная логика только после проверок
}

// Fail-fast в коллекциях
List<String> list = new ArrayList<>(List.of("a", "b", "c"));
for (String s : list) {
    list.remove(s); // ConcurrentModificationException!
}
```

Рекомендация: в бизнес-логике — **fail-fast** (обнаруживать баги рано); на границах системы — **fail-safe** с логированием (не ронять весь сервис из-за одного запроса).

> [!mcq]
> - [ ] `fail-safe` всегда лучше `fail-fast` — приложение должно работать любой ценой, даже при невалидных данных. | Неверно: `fail-safe` без проверок маскирует баги — ошибочные данные расползаются по системе и портят БД/кэш; `fail-fast` ловит баги на входе, ближе к источнику. ❌ ПОСЛЕДСТВИЕ: невалидный `userId=null` тихо проходит, в БД пишется запись с null-foreign-key, через неделю отчёт показывает «-30% выручки» — root cause искать нереально.
> - [ ] `ConcurrentModificationException` от `ArrayList.iterator` — это пример `fail-safe` поведения, защищающего данные. | Неверно: `ConcurrentModificationException` — это именно `fail-fast`-механизм; iterator замечает модификацию через `modCount` и СРАЗУ бросает исключение, не позволяя получить неконсистентный результат. `fail-safe` — это `CopyOnWriteArrayList`, который работает с копией. ❌ ПОСЛЕДСТВИЕ: путаница терминов на собеседовании, неправильный выбор коллекции — `ArrayList` в многопоточном коде падает с CME под нагрузкой.
> - [x] `fail-fast` — в бизнес-логике (баги обнаруживать рано: `Objects.requireNonNull` на входе); `fail-safe` — на границах системы (HTTP-handler, message consumer) с fallback и логированием. | `fail-fast` минимизирует blast radius бага; `fail-safe` сохраняет доступность сервиса для остальных запросов. ✓ ПРИМЕНЯТЬ: внутри — fail-fast, на границе — fail-safe + log + retry. 📋 ПРАВИЛО: «fail-fast внутри, fail-safe на границе». 🔗 См. Q32.
> - [ ] `Optional.empty()` всегда лучше, чем бросать исключение — это идиоматичный fail-safe. | Неверно: `Optional` подходит для «ожидаемого отсутствия» (поиск, не нашли); для НАРУШЕНИЯ контракта (невалидный аргумент, нарушенный инвариант) нужно исключение — иначе caller легко проигнорирует `Optional.empty()` и баг утечёт дальше. ❌ ПОСЛЕДСТВИЕ: метод возвращает `Optional.empty()` при невалидном email, caller вызывает `.orElse(defaultEmail)` — невалидный email (`""`) попадает в БД, рассылка падает на этапе SMTP.

## Q33. Как обрабатывать исключения в `Spring` (`@ExceptionHandler`, `@ControllerAdvice`)?

`Spring MVC` предоставляет централизованный механизм обработки исключений для REST API:

```java
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(OrderNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse handleNotFound(OrderNotFoundException ex) {
        return new ErrorResponse(
            HttpStatus.NOT_FOUND.value(),
            ex.getMessage(),
            LocalDateTime.now()
        );
    }

    @ExceptionHandler(IllegalArgumentException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleBadRequest(IllegalArgumentException ex) {
        return new ErrorResponse(
            HttpStatus.BAD_REQUEST.value(),
            ex.getMessage(),
            LocalDateTime.now()
        );
    }

    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ErrorResponse handleGeneral(Exception ex) {
        log.error("Необработанная ошибка", ex);
        return new ErrorResponse(
            HttpStatus.INTERNAL_SERVER_ERROR.value(),
            "Внутренняя ошибка сервера",  // Не раскрывать детали клиенту!
            LocalDateTime.now()
        );
    }
}

public record ErrorResponse(int status, String message, LocalDateTime timestamp) {}
```

Приоритет поиска обработчика:
1. `@ExceptionHandler` в самом контроллере
2. `@ExceptionHandler` в `@ControllerAdvice` / `@RestControllerAdvice`
3. Дефолтный обработчик Spring

Подробнее — в [вопросах по Spring Boot](../../frameworks/spring/spring-boot-interview.md) и [Spring MVC](../../frameworks/spring/spring-mvc-interview.md).

> [!mcq]
> - [ ] `@ExceptionHandler` в каждом контроллере — гарантирует, что исключения обрабатываются локально, ближе к источнику. | Неверно: дублирование handler-методов в каждом контроллере = массовый copy-paste; ошибочный JSON-формат расходится по сервисам, поведение для одних и тех же исключений отличается. ❌ ПОСЛЕДСТВИЕ: фронт получает разные форматы ошибок от разных эндпоинтов, error-handling в UI превращается в спагетти, OpenAPI-документация не отражает единый формат.
> - [x] Централизованный `@RestControllerAdvice` с `@ExceptionHandler`-методами для каждого типа бизнес-исключения; общий fallback `Exception.class` — без раскрытия деталей клиенту. | Один источник правды для error-формата (`ProblemDetail`/`ErrorResponse`), приоритет более специфичных handler-ов над общими. ✓ ПРИМЕНЯТЬ: один `@RestControllerAdvice` на сервис + специфичные handler-ы на бизнес-исключения. 📋 ПРАВИЛО: «один advice — один формат ошибок». 🔗 См. Q33.
> - [ ] `@ExceptionHandler(Exception.class)` должен возвращать `ex.getMessage()` клиенту — это удобно для debug. | Неверно: `getMessage()` может содержать SQL-запрос, путь к файлу, имя класса — утечка информации помогает атакующему; в prod детали логируем, клиенту — generic-сообщение. ❌ ПОСЛЕДСТВИЕ: `org.postgresql.util.PSQLException: ERROR: relation "users_secret_table" does not exist` уходит во фронт — атакующий получает структуру БД, имя схемы, версию драйвера.
> - [ ] `@ControllerAdvice` обрабатывает только `RuntimeException`; checked exceptions нужно ловить вручную в каждом контроллере. | Неверно: `@ExceptionHandler` обрабатывает ЛЮБОЙ `Throwable` — checked, unchecked, error; ограничения только в типе аргумента метода-handler. ❌ ПОСЛЕДСТВИЕ: разработчик пишет лишний `try-catch` в каждом методе контроллера, дублирует логику обработки `IOException`/`SQLException`, advice не работает.

## Q34. Антипаттерны обработки исключений

Наиболее частые ошибки при работе с исключениями:

**1. Пустой `catch` (swallowing exceptions):**
```java
// ПЛОХО — ошибка полностью потеряна
try {
    riskyOperation();
} catch (Exception e) {
    // ничего
}
```

**2. `catch (Exception e)` везде:**
```java
// ПЛОХО — ловит всё подряд, включая NullPointerException
try {
    process();
} catch (Exception e) {
    return defaultValue;
}
```

**3. Логирование + пробрасывание (double logging):**
```java
// ПЛОХО — одна ошибка залогируется дважды
try {
    process();
} catch (IOException e) {
    log.error("Ошибка", e);
    throw e;  // Обработчик выше тоже залогирует
}
```

**4. Использование исключений для flow control:**
```java
// ПЛОХО — исключения дорогие, не использовать для обычной логики
try {
    int value = Integer.parseInt(input);
    return value;
} catch (NumberFormatException e) {
    return 0; // "нормальный" сценарий через исключение
}
```

**5. `throw new Exception()` (потеря типизации):**
```java
// ПЛОХО — вызывающий код не знает, что именно произошло
throw new Exception("Что-то пошло не так");
// ХОРОШО — конкретный тип
throw new OrderNotFoundException(orderId);
```

Следование этим правилам — признак зрелого разработчика, что ценится на собеседовании.

> [!mcq]
> - [ ] `try { ... } catch (Exception e) { return defaultValue; }` — корректный fallback-паттерн, защищающий от падений. | Неверно: широкий `catch (Exception e)` ловит `NullPointerException` (баг!), `IllegalStateException` (баг!), `RuntimeException` от Spring proxy — все они маскируются под «временную ошибку», baseline-bug-и не видны. ❌ ПОСЛЕДСТВИЕ: NPE в коде маскируется default-значением, неверные данные пишутся в БД, реальный баг живёт месяцами и проявляется в виде расхождения отчётов.
> - [ ] `throw new Exception("что-то сломалось")` — приемлемо для прототипа, можно уточнить позже. | Неверно: тип `Exception` теряет всю типизацию — caller не может различить разные ошибки и вынужден ловить общий `Exception`; усиливает каскадный антипаттерн. ❌ ПОСЛЕДСТВИЕ: catch-блок в API-слое не знает, отдавать 404 (not found) или 503 (downstream down), все ошибки уходят как 500, мониторинг не различает severity.
> - [x] Антипаттерны: пустой catch, ловля `Exception` подряд, double-logging (log + throw), исключения для flow control, `throw new Exception("...")` без типа. | Каждый из них либо теряет информацию, либо маскирует баги, либо имеет производительностные последствия. ✓ ПРИМЕНЯТЬ: ловить узкий тип, либо логировать, либо пробрасывать; исключения только для исключительных ситуаций. 📋 ПРАВИЛО: «узкий catch, no double-log, no flow-control». 🔗 См. Q34.
> - [ ] `Integer.parseInt` в try-catch — нормальный способ проверить, что строка число (вместо regex). | Неверно: создание `NumberFormatException` стоит в 1000-100000 раз дороже if-проверки из-за `fillInStackTrace`; для парсинга чисел в hot path использовать `StringUtils.isNumeric` (Apache) или regex/manual-check. ❌ ПОСЛЕДСТВИЕ: при 10k RPS на endpoint с парсингом 50% невалидных строк CPU тратится на построение stack trace, latency p95 растёт с 5ms до 200ms.

---

## Q35. Exception Chaining — initCause(), getCause(), addSuppressed()

**Exception Chaining** — механизм сохранения первопричины исключения при оборачивании в другое.

**initCause() и getCause():**
```java
// При оборачивании всегда передавать original exception как cause
public User loadUser(long id) {
    try {
        return userRepository.findById(id);
    } catch (SQLException e) {
        // ХОРОШО — cause сохранён
        throw new UserLoadException("Failed to load user " + id, e);
        // Эквивалентно:
        // UserLoadException ex = new UserLoadException("...");
        // ex.initCause(e); throw ex;
    }
}

// Доступ к причине
try {
    loadUser(42L);
} catch (UserLoadException e) {
    Throwable cause = e.getCause();          // SQLException
    Throwable rootCause = ExceptionUtils.getRootCause(e);  // Apache Commons — идёт по цепочке
    log.error("Root cause: {}", rootCause.getMessage());
}
```

**addSuppressed() / getSuppressed():**
```java
// Исключение в блоке finally не должно подавлять основное
Exception primaryException = null;
try {
    connection.execute(sql);
} catch (Exception e) {
    primaryException = e;
    throw e;
} finally {
    try {
        connection.close();
    } catch (Exception closeException) {
        if (primaryException != null) {
            primaryException.addSuppressed(closeException);  // не теряем info
        }
        // Иначе просто пробрасываем closeException
    }
}
// try-with-resources делает это автоматически!

// Получить suppressed
for (Throwable suppressed : exception.getSuppressed()) {
    log.warn("Suppressed: {}", suppressed.getMessage());
}
```

**Правило:** никогда не создавать исключение без передачи cause — `throw new ServiceException("error")` без `e` теряет стектрейс первопричины.

> [!mcq]
> - [ ] `catch (SQLException e) { throw new ServiceException("DB error: " + e.getMessage()); }` — правильное оборачивание, message сохранён. | Неверно: cause НЕ передан в конструктор — stack trace `SQLException` потерян; `getMessage()` часто `null` для DB-исключений; debug в production невозможен. ❌ ПОСЛЕДСТВИЕ: лог показывает «ServiceException: DB error: null», исходный SQL и проблемный stack-frame утеряны, вместо 5 минут diagnose занимает 5 часов.
> - [x] `throw new ServiceException("Failed to load user " + id, e)` — конструктор с `cause`, или `addSuppressed(closeException)` для ошибки в finally. | Сохраняем оригинальный stack trace через `cause`; `addSuppressed` — для дополнительных ошибок (например, ошибка close), которые не должны затирать primary exception. ✓ ПРИМЕНЯТЬ: всегда передавать `cause`; в `finally` — `addSuppressed`, а не `throw`. 📋 ПРАВИЛО: «cause при wrap, suppressed при cleanup». 🔗 См. Q35.
> - [ ] `addSuppressed(throwable)` — то же самое, что `initCause(throwable)`, оба добавляют в цепочку. | Неверно: это разные механизмы. `cause` — ОДНА первопричина (что вызвало это исключение); `suppressed` — МАССИВ дополнительных ошибок (например, primary бросил, и close() в finally тоже бросил). `try-with-resources` использует именно `addSuppressed`. ❌ ПОСЛЕДСТВИЕ: путаница в реализации кастомного try-finally, вторая ошибка либо теряется, либо затирает первую — потеря важной диагностической информации.
> - [ ] `getRootCause()` — стандартный метод `Throwable`, возвращает корневое исключение в цепочке. | Неверно: в JDK у `Throwable` НЕТ `getRootCause()`; есть только `getCause()` (один шаг). Метод `getRootCause()` есть в `Apache Commons Lang` (`ExceptionUtils.getRootCause`) или Spring (`NestedExceptionUtils`). ❌ ПОСЛЕДСТВИЕ: код не компилируется без зависимости, разработчик пишет ручной обход цепочки с риском бесконечного цикла при self-referencing causes.

---

## Q36. Multi-catch (Java 7) — синтаксис, ограничения

**Multi-catch** — перехват нескольких типов исключений в одном `catch`:

```java
// До Java 7 — дублирование
try {
    processRequest(request);
} catch (IOException e) {
    log.error("IO error", e);
    throw new ServiceException(e);
} catch (ParseException e) {
    log.error("Parse error", e);
    throw new ServiceException(e);
}

// Java 7+ — multi-catch (pipe separator)
try {
    processRequest(request);
} catch (IOException | ParseException e) {
    // e имеет тип: наиболее специфичный общий предок (Throwable, если нет общего)
    log.error("Processing error", e);
    throw new ServiceException(e);
}
```

**Ограничения multi-catch:**
```java
// 1. Нельзя поймать связанные иерархически типы
try { ... }
catch (Exception | IOException e) { }  // ОШИБКА КОМПИЛЯЦИИ: IOException extends Exception

// 2. Переменная e в multi-catch — effectively final (нельзя переприсвоить)
catch (IOException | ParseException e) {
    e = new IOException("other");  // ОШИБКА КОМПИЛЯЦИИ
}

// 3. Если нужна разная обработка — отдельные блоки catch
catch (IOException e) { handleIo(e); }
catch (ParseException e) { handleParse(e); }
```

**Практическое применение:**
```java
// Упрощение catch аналогичных «технических» исключений
catch (JdbcException | DataAccessException e) {
    throw new RepositoryException("DB error", e);
}
```

> [!mcq]
> - [ ] `catch (Exception | IOException e)` — корректный multi-catch для всех ошибок ввода-вывода. | Неверно: `IOException extends Exception` — нельзя указывать в multi-catch типы из ОДНОЙ иерархии (родитель + потомок); компилятор ругается «Types in multi-catch must be disjoint». ❌ ПОСЛЕДСТВИЕ: код не компилируется, разработчик не понимает причину ошибки и пишет два отдельных catch-блока с дублированной логикой.
> - [x] `catch (IOException | ParseException e)` — multi-catch для несвязанных типов с одинаковой обработкой; `e` — effectively final, тип — общий супертип (часто `Exception`). | Multi-catch (Java 7+) убирает дублирование catch-блоков для несвязанных типов; внутри блока `e` нельзя переприсваивать. ✓ ПРИМЕНЯТЬ: для одинаковой обработки несвязанных исключений (типичный случай — wrapping в один общий тип). 📋 ПРАВИЛО: «multi-catch — только для disjoint типов». 🔗 См. Q36.
> - [ ] В multi-catch можно переприсваивать переменную: `catch (IOException | ParseException e) { e = new IOException(); throw e; }` | Неверно: переменная multi-catch — implicitly final (Java 7+); попытка присвоения вызывает ошибку компиляции. Это сделано, чтобы `throw e` работал предсказуемо для любой ветки. ❌ ПОСЛЕДСТВИЕ: код не компилируется при попытке «нормализовать» исключение; разработчик использует обходной путь с лишней переменной.
> - [ ] Multi-catch вынуждает указывать `throws` всех типов в сигнатуре метода — даже если бросается только один. | Неверно: компилятор анализирует, какие именно типы могут бросаться внутри `try`, и в `throws` нужны только реально возможные; multi-catch улучшает анализ, но не расширяет требования к сигнатуре. ❌ ПОСЛЕДСТВИЕ: разработчик добавляет лишние `throws` в сигнатуру, API метода захламляется, caller вынужден ловить несуществующие исключения.

---

## Q37. Try-with-resources с AutoCloseable — как работает, порядок закрытия

**try-with-resources** компилируется в код с finally и addSuppressed. Ресурсы закрываются в **обратном порядке** объявления.

```java
// Несколько ресурсов — закрытие в обратном порядке (LIFO)
try (
    Connection conn = dataSource.getConnection();      // открыт первым
    PreparedStatement stmt = conn.prepareStatement(sql);  // открыт вторым
    ResultSet rs = stmt.executeQuery()                 // открыт третьим
) {
    while (rs.next()) {
        process(rs.getString("name"));
    }
}
// Закрытие: rs.close() → stmt.close() → conn.close()
// Каждый close() вызывается даже если предыдущий бросил исключение
```

**Кастомный AutoCloseable:**
```java
public class MeasuredTimer implements AutoCloseable {
    private final String name;
    private final long start = System.nanoTime();

    public MeasuredTimer(String name) { this.name = name; }

    @Override
    public void close() {
        long elapsed = System.nanoTime() - start;
        log.info("{} took {}ms", name, elapsed / 1_000_000);
    }
}

// Использование
try (MeasuredTimer t = new MeasuredTimer("processOrder")) {
    orderService.process(order);
}
// После выполнения блока — автоматически логирует время

// Lock в try-with-resources:
public class AutoLock implements AutoCloseable {
    private final Lock lock;
    public AutoLock(Lock lock) { this.lock = lock; lock.lock(); }
    @Override public void close() { lock.unlock(); }
}

try (AutoLock lock = new AutoLock(reentrantLock)) {
    // критическая секция — lock всегда освободится
}
```

**Что происходит при исключении в close():**
- Если тело блока бросило исключение И close() тоже — основное исключение пробрасывается, исключение из close() добавляется через `addSuppressed`.
- Если тело не бросало исключений — исключение из close() пробрасывается как основное.

> [!mcq]
> - [ ] Ресурсы в `try-with-resources` закрываются в порядке объявления (сверху вниз: `conn → stmt → rs`). | Неверно: ресурсы закрываются в ОБРАТНОМ порядке (LIFO): сначала `rs`, потом `stmt`, потом `conn` — иначе закрытие зависимого ресурса сломает зависимый. ❌ ПОСЛЕДСТВИЕ: при ручной реализации без try-with-resources разработчик закрывает `Connection` до `ResultSet` — `rs.close()` бросает `Connection is closed`, suppressed-ошибки заполняют логи.
> - [x] Ресурсы закрываются в обратном порядке (LIFO); если тело И `close()` бросают исключения — основное пробрасывается, ошибка `close()` идёт в `getSuppressed()`. | Компилятор разворачивает try-with-resources в эквивалент try-finally с `addSuppressed`, который сохраняет ОБА исключения. ✓ ПРИМЕНЯТЬ: всегда try-with-resources для `AutoCloseable`; не реализовывать вручную. 📋 ПРАВИЛО: «LIFO close + suppressed для cleanup-ошибок». 🔗 См. Q37.
> - [ ] Если тело блока бросило исключение, `close()` НЕ вызывается — JVM прерывает выполнение. | Неверно: `close()` вызывается ВСЕГДА (как в `finally`), даже если тело бросило; именно поэтому try-with-resources заменяет ручной try-finally — гарантирует освобождение ресурсов. ❌ ПОСЛЕДСТВИЕ: разработчик добавляет лишний `finally { resource.close(); }` — двойное закрытие, либо exception "stream closed", либо лишний код.
> - [ ] Кастомный класс должен реализовывать `Closeable` (не `AutoCloseable`), чтобы работать в try-with-resources. | Неверно: try-with-resources работает с любым `AutoCloseable` (Java 7+); `Closeable extends AutoCloseable` и существовал для I/O ещё до Java 7, но НЕ обязателен. ❌ ПОСЛЕДСТВИЕ: разработчик заставляет свой класс бросать `IOException` (контракт `Closeable`) для бизнес-ресурса, который к I/O отношения не имеет — caller ловит лишнее checked-исключение.

---

## Q38. Custom Exceptions — best practices, serialVersionUID

**Правила создания кастомных исключений:**

```java
// 1. Расширяй подходящий базовый тип
// RuntimeException — unchecked (не требует объявления throws)
// Exception        — checked (требует объявления или обработки)

public class OrderNotFoundException extends RuntimeException {
    // 2. serialVersionUID обязателен (Serializable → Exception → Throwable)
    @Serial
    private static final long serialVersionUID = 1L;

    // 3. Включать бизнес-контекст в конструктор
    private final Long orderId;

    // 4. Конструкторы: message, message+cause, cause
    public OrderNotFoundException(Long orderId) {
        super("Order not found: " + orderId);
        this.orderId = orderId;
    }

    public OrderNotFoundException(Long orderId, Throwable cause) {
        super("Order not found: " + orderId, cause);
        this.orderId = orderId;
    }

    // 5. Геттер для контекстных данных (для логирования/обработки)
    public Long getOrderId() { return orderId; }
}

// 6. Иерархия: базовое исключение домена + конкретные подтипы
public abstract class DomainException extends RuntimeException {
    protected DomainException(String message) { super(message); }
    protected DomainException(String message, Throwable cause) { super(message, cause); }
}

public class InsufficientStockException extends DomainException {
    @Serial private static final long serialVersionUID = 1L;
    private final int requested;
    private final int available;

    public InsufficientStockException(int requested, int available) {
        super(String.format("Insufficient stock: requested=%d, available=%d", requested, available));
        this.requested = requested;
        this.available = available;
    }
}
```

**Зачем serialVersionUID:** `Exception` реализует `Serializable`. Если поле отсутствует, Java генерирует его автоматически на основе сигнатуры класса — при изменении класса ID меняется и возможна `InvalidClassException` при десериализации. Явный `1L` даёт контроль.

> [!mcq]
> - [ ] Кастомное исключение должно расширять `Exception` (checked) — это заставляет вызывающий код явно обрабатывать ошибку. | Неверно: для бизнес-исключений в современной Java принято расширять `RuntimeException` — checked-исключения создают шумные сигнатуры, ухудшают композицию (lambdas, streams) и часто игнорируются вызывающими через `throws Exception`. ❌ ПОСЛЕДСТВИЕ: каждый метод сервиса засоряется `throws OrderException, PaymentException, ...`; разработчики начинают писать `throws Exception` — checked-механизм теряет смысл.
> - [x] Расширять `RuntimeException` для бизнес-исключений; добавлять `serialVersionUID`, конструктор `(String, Throwable)`, контекстные поля (`orderId`, `userId`) с геттерами для логирования. | Контекст в полях (а не только в message) позволяет handler-ам структурно реагировать; `serialVersionUID` страхует от `InvalidClassException` при сериализации. ✓ ПРИМЕНЯТЬ: иерархия `DomainException` → конкретные подтипы + контекстные поля. 📋 ПРАВИЛО: «RuntimeException + контекст + serialVersionUID». 🔗 См. Q38.
> - [ ] Поле `serialVersionUID` нужно только если класс используется с RMI или JMS — для обычных исключений необязательно. | Неверно: `Exception → Throwable → Serializable`, поэтому ЛЮБОЕ исключение сериализуемо; без явного `serialVersionUID` Java генерирует его автоматически на основе сигнатуры — изменение класса (новое поле, метод) ломает сериализацию. ❌ ПОСЛЕДСТВИЕ: при репликации сессии в кластере (`HttpSession`) или в логах с serializable-фреймворком после деплоя — `InvalidClassException`, кластерные ноды не понимают друг друга.
> - [ ] Кастомное исключение должно быть immutable — поэтому всегда `final` класс с `final` полями и без сеттеров. | Не совсем неверно для полей, но: класс не должен быть `final` — это ломает наследование (типичная иерархия `DomainException → OrderException → OrderNotFoundException`); поля должны быть `private final`, но сам класс открыт для расширения. ❌ ПОСЛЕДСТВИЕ: невозможно построить иерархию доменных исключений, для каждого случая создаются несвязанные классы — нет общей точки для `@ExceptionHandler(DomainException.class)`.

---

## Q39. Exception в lambda — как обрабатывать checked exceptions

**Проблема:** функциональные интерфейсы (`Function`, `Consumer`, `Supplier`) не объявляют checked exceptions → компилятор запрещает их бросать.

```java
// ПРОБЛЕМА: Files.readString бросает IOException (checked)
List<String> contents = paths.stream()
    .map(path -> Files.readString(path))  // ОШИБКА КОМПИЛЯЦИИ
    .collect(toList());
```

**Решения:**

**1. Обернуть в try-catch внутри лямбды:**
```java
List<String> contents = paths.stream()
    .map(path -> {
        try {
            return Files.readString(path);
        } catch (IOException e) {
            throw new UncheckedIOException(e);  // оборачиваем в unchecked
        }
    })
    .collect(toList());
```

**2. Вспомогательный wrapper-метод:**
```java
@FunctionalInterface
public interface ThrowingFunction<T, R> {
    R apply(T t) throws Exception;

    static <T, R> Function<T, R> wrap(ThrowingFunction<T, R> f) {
        return t -> {
            try { return f.apply(t); }
            catch (RuntimeException e) { throw e; }
            catch (Exception e) { throw new RuntimeException(e); }
        };
    }
}

// Использование
List<String> contents = paths.stream()
    .map(ThrowingFunction.wrap(Files::readString))
    .collect(toList());
```

**3. Sneaky throw (Lombok @SneakyThrows):**
```java
@SneakyThrows  // Lombok — "пробрасывает" checked без объявления
public String readFile(Path path) {
    return Files.readString(path);
}
// Компилятор доволен, но exception всё равно летит — осторожно с обработкой!
```

**4. Unchecked-обёртки стандартной библиотеки:**
```java
// java.io предоставляет UncheckedIOException
throw new UncheckedIOException(ioException);
```

**Рекомендация:** предпочитать вариант 1 (явный try-catch) или вариант 2 (ThrowingFunction). Sneaky throw скрывает информацию от вызывающего кода.

> [!mcq]
> - [ ] Стандартный `Function<T,R>` поддерживает checked exceptions — достаточно объявить `throws IOException` в lambda. | Неверно: интерфейсы `Function/Consumer/Supplier` НЕ объявляют `throws` checked-исключений; компилятор не разрешит бросить `IOException` из lambda без обёртки. ❌ ПОСЛЕДСТВИЕ: ошибка компиляции «unhandled exception type IOException» в `stream().map(Files::readString)`, разработчик вынужден переписывать код через for-each.
> - [x] Обернуть в try-catch внутри lambda и пробросить как `UncheckedIOException`/RuntimeException; либо использовать вспомогательный `ThrowingFunction.wrap(...)` для явного контракта. | Сохраняет stream-композицию, не ломает функциональные интерфейсы; `UncheckedIOException` — стандарт JDK для I/O-ошибок в lambda. ✓ ПРИМЕНЯТЬ: для одноразовых случаев — try-catch внутри lambda; для частых — общий `ThrowingFunction` wrapper. 📋 ПРАВИЛО: «оборачивать checked в unchecked внутри lambda». 🔗 См. Q39.
> - [ ] `@SneakyThrows` (Lombok) — лучшее решение, потому что не меняет сигнатуру и не требует try-catch. | Неверно (как «лучшее»): `@SneakyThrows` обходит компилятор, но checked-исключение всё равно летит — caller не подозревает о нём и не пишет catch; усложняет debug, скрывает контракт. Использовать только осознанно для конкретных случаев. ❌ ПОСЛЕДСТВИЕ: caller вызывает метод без catch, в production вылетает `IOException`, который Spring-обработчик не ожидает — 500-ка с непонятной причиной.
> - [ ] `CompletableFuture.supplyAsync` автоматически разворачивает checked exceptions в RuntimeException — поэтому в lambda можно бросать что угодно. | Неверно: `Supplier<T>` в `supplyAsync` не объявляет `throws` — checked-исключение нужно обернуть вручную; `CompletableFuture` запоминает исключение и пробрасывает через `CompletionException` при `.get()`/`.join()`. ❌ ПОСЛЕДСТВИЕ: код не компилируется в lambda с `Files.readString`; разработчик не понимает, почему `Function`-обёртка не помогает, и сваливается обратно на synchronous-код.

---

## Q40. Логирование исключений — правила, что включать в message

**Правила логирования:**

```java
// ПРАВИЛО 1: передавать exception как второй аргумент (не в строку!)
// ПЛОХО — теряем стектрейс в некоторых logback appenders
log.error("Error processing order: " + e.getMessage());

// ХОРОШО — логгер сам форматирует стектрейс
log.error("Error processing order {}", orderId, e);  // {} + exception как 3й аргумент

// ПРАВИЛО 2: включать контекст (что, где, с какими данными)
log.error("Failed to process order [orderId={}, userId={}, amount={}]",
          orderId, userId, amount, e);

// ПРАВИЛО 3: не логировать и не пробрасывать одновременно (double logging)
// ПЛОХО:
catch (Exception e) {
    log.error("Error", e);
    throw new ServiceException(e);  // обработчик выше залогирует снова!
}
// ХОРОШО — либо логировать, либо пробрасывать:
catch (Exception e) {
    throw new ServiceException("Failed to process order " + orderId, e);
}
// Логировать на верхнем уровне (GlobalExceptionHandler)

// ПРАВИЛО 4: уровни логирования
// ERROR — непредвиденные ошибки (баги, недоступность внешних систем)
// WARN  — предвиденные, но нежелательные ситуации (retry, fallback)
// INFO  — бизнес-события (заказ создан, платёж принят)
// DEBUG — технические детали (SQL, запросы, параметры)

// ПРАВИЛО 5: структурированное логирование (MDC)
MDC.put("orderId", String.valueOf(orderId));
MDC.put("userId", String.valueOf(userId));
try {
    processOrder();
} catch (Exception e) {
    log.error("Order processing failed", e);  // MDC-поля автоматически включены
} finally {
    MDC.clear();
}
```

**Что включать в message:** ключевые ID (orderId, userId, requestId), состояние входных данных (кратко), что пытались сделать. Не включать: пароли, токены, PII (персональные данные).

> [!mcq]
> - [ ] `log.error("Order failed: " + e.getMessage())` — компактный паттерн логирования. | Неверно: конкатенация теряет stack trace целиком (передаём только String, не Throwable); `getMessage()` часто `null` для NPE/SQLException — лог покажет «Order failed: null». ❌ ПОСЛЕДСТВИЕ: при инциденте видим «Order failed: null» без stack trace и без cause — debug невозможен, нужен повторный запуск с DEBUG-логированием для воспроизведения.
> - [x] `log.error("Order failed [orderId={}, userId={}]", orderId, userId, e)` — exception ПОСЛЕДНИМ аргументом, бизнес-контекст через placeholder'ы; данные в MDC; в message не включать пароли/PII. | SLF4J распознаёт последний `Throwable` и форматирует stack trace отдельно; MDC даёт сквозные поля (`requestId`, `userId`) во всех записях запроса. ✓ ПРИМЕНЯТЬ: exception всегда последний; контекст — через placeholder'ы или MDC. 📋 ПРАВИЛО: «exception last, context structured». 🔗 См. Q40.
> - [ ] При логировании в catch-блоке нужно ВСЕГДА вызвать `log.error` ИЛИ пробросить — но не оба, иначе double-log. Поэтому правило: log.error везде. | Неверно про «log.error везде»: правильно — либо логировать на ВЕРХНЕМ уровне (`@ControllerAdvice`/handler), либо пробрасывать с обогащением; промежуточные слои должны добавлять контекст и пробрасывать, не логируя. ❌ ПОСЛЕДСТВИЕ: одна ошибка попадает в лог 5 раз (по числу слоёв), alerting срабатывает 5 раз на инцидент, дежурный получает дубликаты, sizing storage вырастает.
> - [ ] `e.printStackTrace()` пишет в SLF4J-логгер, поэтому это валидная альтернатива `log.error`. | Неверно: `printStackTrace()` пишет напрямую в `System.err` (не через логгер), не использует appender, не попадает в файлы logback/log4j, MDC игнорируется; в k8s/Docker stderr идёт отдельным потоком. ❌ ПОСЛЕДСТВИЕ: stack trace отсутствует в Kibana/ELK (логи берутся из stdout), MDC-контекст потерян, корреляция запросов невозможна.

---

## Q41. Performance исключений — стоимость fillInStackTrace, дорогие stack traces

**Создание исключения — дорогая операция**, потому что конструктор `Throwable` вызывает `fillInStackTrace()` — нативный метод, который обходит стек вызовов и создаёт массив `StackTraceElement`.

**Измерения (примерные):**
```
Создание Exception (с стектрейсом) ~ 1-10 мкс (зависит от глубины стека)
Создание Exception без стектрейса  ~ 10-100 нс
Обычный if-check                   ~ 1 нс
→ Exception в 1000-100000x дороже condition-check
```

**Не использовать исключения для flow control:**
```java
// ПЛОХО — исключение для "нормального" сценария
public int parseOrDefault(String s, int defaultVal) {
    try {
        return Integer.parseInt(s);
    } catch (NumberFormatException e) {
        return defaultVal;  // это "нормальный" путь — не нужно исключение
    }
}
// ХОРОШО:
public int parseOrDefault(String s, int defaultVal) {
    if (s == null || s.isBlank()) return defaultVal;
    try { return Integer.parseInt(s); }
    catch (NumberFormatException e) { return defaultVal; }
}
// Или: Optional.ofNullable(s).map(Integer::parseInt).orElse(defaultVal)
```

**Оптимизация — кастомное исключение без стектрейса:**
```java
// Для sentinel-исключений (например, сигнал "нет элемента" в потоке)
public class NoMoreElementsException extends RuntimeException {
    public static final NoMoreElementsException INSTANCE = new NoMoreElementsException();

    private NoMoreElementsException() {
        super(null, null, true, false);  // 4й параметр: writableStackTrace=false
    }
}

// Базовый класс с отключённым стектрейсом
public class LightweightException extends RuntimeException {
    public LightweightException(String message) {
        super(message, null, true, false);  // writableStackTrace = false
    }
}
```

**JIT и исключения:** JVM может оптимизировать повторяющиеся исключения. В HotSpot: если исключение бросается очень часто из одного места, JIT может убрать fillInStackTrace (стектрейс будет пустым). Это неожиданное поведение в production — логи теряют стектрейс.

> [!mcq]
> - [ ] Создание `Exception` дешёвое — это просто аллокация объекта на heap. | Неверно: основная стоимость не в аллокации, а в нативном `fillInStackTrace()` — обходе всего стека вызовов и материализации `StackTraceElement[]`; на глубоком стеке может быть микросекундами против наносекунд для обычной операции. ❌ ПОСЛЕДСТВИЕ: использование исключений как flow-control в hot path (parseOrDefault, find-or-throw) увеличивает latency p95 в десятки раз; CPU тратится на построение stack trace вместо полезной работы.
> - [x] Создание `Exception` ~ 1-10 мкс из-за `fillInStackTrace`; для sentinel-исключений в hot path — конструктор `super(msg, null, true, false)` отключает stack trace. | `Throwable(String, Throwable, boolean, boolean)` (Java 7+) с `writableStackTrace=false` создаёт исключение без обхода стека; стоимость падает на 2-3 порядка. ✓ ПРИМЕНЯТЬ: для sentinel-исключений (внутренние сигналы, `NoSuchElementException`-аналоги в горячих путях) — отключать stack trace. 📋 ПРАВИЛО: «не использовать exceptions для flow control; отключать стек для sentinel». 🔗 См. Q41.
> - [ ] HotSpot JIT всегда сохраняет полный stack trace — это гарантировано спецификацией JVM. | Неверно: HotSpot имеет оптимизацию (`-XX:-OmitStackTraceInFastThrow` чтобы выключить): после многократного бросания исключения из одного и того же места JIT может скомпилировать его БЕЗ stack trace для скорости — в логах видно «Exception» без trace. ❌ ПОСЛЕДСТВИЕ: в production видим `NullPointerException` без stack trace после прогрева JIT, разработчик 2 часа ищет баг в неверном месте, пока не вспомнит про эту оптимизацию.
> - [ ] `try { Integer.parseInt(input); } catch (NumberFormatException e) {}` — приемлемо для парсинга 10000 значений в секунду. | Неверно: на 50% невалидных данных и 10k RPS — это 5000 исключений/сек × ~5 мкс = 25 мс CPU/сек на hot path; latency p95 значительно деградирует. Использовать pre-validation regex или `StringUtils.isNumeric`. ❌ ПОСЛЕДСТВИЕ: endpoint парсинга показывает p95=200ms вместо 5ms, мониторинг алертит на latency, real cause скрыт за NumberFormatException stack traces в логах.

---

## Q42. Global Exception Handling в Spring — @ControllerAdvice, ProblemDetail

**@ControllerAdvice + @ExceptionHandler** — централизованная обработка исключений для всех контроллеров:

```java
@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    // Бизнес-исключения → 404
    @ExceptionHandler(OrderNotFoundException.class)
    public ResponseEntity<ProblemDetail> handleNotFound(OrderNotFoundException ex) {
        ProblemDetail problem = ProblemDetail.forStatusAndDetail(
            HttpStatus.NOT_FOUND,
            ex.getMessage()
        );
        problem.setTitle("Order Not Found");
        problem.setProperty("orderId", ex.getOrderId());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(problem);
    }

    // Ошибки валидации → 422
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ProblemDetail> handleValidation(MethodArgumentNotValidException ex) {
        ProblemDetail problem = ProblemDetail.forStatus(HttpStatus.UNPROCESSABLE_ENTITY);
        problem.setTitle("Validation Failed");
        List<String> errors = ex.getBindingResult().getFieldErrors().stream()
            .map(e -> e.getField() + ": " + e.getDefaultMessage())
            .toList();
        problem.setProperty("errors", errors);
        return ResponseEntity.unprocessableEntity().body(problem);
    }

    // Неожиданные ошибки → 500
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ProblemDetail> handleUnexpected(Exception ex, HttpServletRequest request) {
        log.error("Unexpected error on {}", request.getRequestURI(), ex);
        ProblemDetail problem = ProblemDetail.forStatusAndDetail(
            HttpStatus.INTERNAL_SERVER_ERROR,
            "An unexpected error occurred"
        );
        return ResponseEntity.internalServerError().body(problem);
    }
}
```

**ProblemDetail (RFC 7807 / Spring 6+):**
```java
// Spring Boot 3+ включает ProblemDetail из коробки
// application.yml:
// spring.mvc.problemdetails.enabled: true

// Стандартный формат ответа:
// {
//   "type": "https://example.com/errors/order-not-found",
//   "title": "Order Not Found",
//   "status": 404,
//   "detail": "Order not found: 12345",
//   "instance": "/api/orders/12345",
//   "orderId": 12345    ← кастомное расширение
// }

problem.setType(URI.create("https://api.example.com/errors/order-not-found"));
problem.setInstance(URI.create(request.getRequestURI()));
```

**Порядок приоритетов обработчиков:** более специфичный тип исключения имеет приоритет над более общим. Если несколько `@ControllerAdvice` — использовать `@Order` для управления порядком.

> [!mcq]
> - [ ] `@ControllerAdvice(annotations = RestController.class)` — недопустимое сужение, advice должен быть глобальным. | Неверно: сужение через `annotations`, `basePackages`, `assignableTypes` — стандартная фича `@ControllerAdvice` для разделения ошибок REST и MVC-страниц; рекомендованный подход в больших приложениях. ❌ ПОСЛЕДСТВИЕ: разработчик не использует сужение, REST-API возвращает HTML error pages вместо JSON `ProblemDetail`, фронт ломается при парсинге.
> - [x] Один `@RestControllerAdvice` с handler-ами для бизнес-исключений + общий fallback на `Exception.class`; формат — `ProblemDetail` (RFC 7807) со status, title, detail, instance, кастомные поля через `setProperty`. | RFC 7807 — стандартный формат ошибок HTTP API; `ProblemDetail` встроен в Spring 6+, поддерживает кастомные расширения. ✓ ПРИМЕНЯТЬ: единый формат ошибок через `ProblemDetail`, специфичные handler-ы для известных типов, generic-fallback для неизвестных. 📋 ПРАВИЛО: «один advice, один формат, ProblemDetail + спецификация». 🔗 См. Q42.
> - [ ] `@ExceptionHandler(Exception.class)` в advice должен возвращать `ResponseEntity<String>(ex.toString(), 500)` — это даёт максимум информации клиенту. | Неверно: `ex.toString()` раскрывает имя класса исключения и сообщение (часто SQL/путь/PII) — утечка внутренней информации; для клиента нужно generic-сообщение, детали — только в логе. ❌ ПОСЛЕДСТВИЕ: атакующий через `?id=' OR 1=1--` получает `org.postgresql.util.PSQLException: syntax error...` — узнаёт версию БД, схему, имя драйвера; готовит точечную атаку.
> - [ ] При наличии нескольких `@ControllerAdvice` Spring выбирает их в случайном порядке — для предсказуемости нужен только один advice. | Неверно: порядок управляется `@Order` или реализацией `Ordered`; более низкое значение `@Order` = выше приоритет; несколько advice — нормальная архитектура (разделение по слоям/модулям). ❌ ПОСЛЕДСТВИЕ: разработчик объединяет всё в один файл из 500 строк, поддержка усложняется, конфликты handler-ов между модулями становятся неразрешимыми.

---

## See also

- [Java Core](java-core-interview.md) — базовые вопросы по Java
- [Java 8](java-8-interview.md) — лямбды, `Stream API`, `Optional`
- [Java I/O и NIO](java-io-nio-interview.md) — ввод-вывод и работа с ресурсами
- [Java Concurrency](java-concurrency-interview.md) — многопоточность и обработка ошибок в потоках
- [Java OOP](java-oop-interview.md) — наследование и полиморфизм (контракт `throws`)
- [Spring Boot](../../frameworks/spring/spring-boot-interview.md) — `@ExceptionHandler`, `@ControllerAdvice`
- [Логирование](../../logging/logging-interview.md) — логирование исключений

- [Java 17-21](java-17-21-interview.md)
- [Java 8](java-8-interview.md)
- [Java Annotations](java-annotations-interview.md)
- [Java Collections](java-collections-interview.md)
- [Java Concurrency](java-concurrency-interview.md)
- [Java Conditional Statements](java-conditional-statements-interview.md)
- [Шпаргалка: Java: обработка исключений](../../../languages/java/java-exceptions.md) — теория
