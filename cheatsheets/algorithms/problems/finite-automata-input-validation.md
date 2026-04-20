---
title: "Валидация ввода конечным автоматом (Finite Automata Input Validation)"
description: "Проверка строк по правилам грамматики с помощью конечного автомата: состояния, переходы по символам, финальные состояния. Пример — упрощённый валидатор JSON на Java и Kotlin. Варианты: email, числа."
tags:
  - algorithms
  - problems
  - finite-automata-input-validation
difficulty: "intermediate"
prerequisites: []
next: []
updated: "2026-04-20"
---
# Валидация ввода конечным автоматом (Finite Automata Input Validation)

Проверка строк по правилам грамматики с помощью конечного автомата: состояния, переходы по символам, финальные состояния. Пример — упрощённый валидатор JSON на Java и Kotlin. Варианты: email, числа.

## Полезные ссылки

### Официальная документация
- [Baeldung: Finite State Machines](https://www.baeldung.com/cs-finite-state-machines)

### См. также
- [[README|Регулярные выражения]] — алгоритмы со строками
- [[state-machine-with-enums|Конечный автомат с enum]] — state machine с enum

## Содержание

- [Описание алгоритма](#описание-алгоритма)
- [Концепция конечных автоматов](#концепция-конечных-автоматов)
- [Реализация на Java](#реализация-на-java)
- [Пример: Валидатор JSON](#пример-валидатор-json)
- [Реализация на Kotlin](#реализация-на-kotlin)
- [Сложность](#сложность)
- [Особенности](#особенности)
- [Применение](#применение)
- [Варианты задачи](#варианты-задачи)
  - [Вариант 1: Валидатор email](#вариант-1-валидатор-email)
  - [Вариант 2: Валидатор чисел](#вариант-2-валидатор-чисел)
- [Когда использовать](#когда-использовать)
- [Лучшие практики](#лучшие-практики)
- [Решение проблем](#решение-проблем)
- [Частые вопросы](#частые-вопросы)
- [Заключение](#заключение)

## Описание алгоритма

Если вы изучали `CS`, вы, несомненно, прошли курс по компиляторам или что-то подобное; на этих занятиях преподается концепция конечного автомата (finite state machine). Это способ формализации правил грамматики языков.

Так как же эта забытая концепция может быть полезна нам, программистам высокого уровня, которым не нужно беспокоиться о создании нового компилятора?

Что ж, оказывается, эта концепция может упростить множество бизнес-сценариев и дать нам инструменты для рассуждений о сложной логике.

В качестве быстрого примера мы также можем проверить ввод без внешней сторонней библиотеки.

## Концепция конечных автоматов

Автомат задаётся состояниями и переходами по символам. Проход по входу: для каждого символа проверяем, есть ли переход из текущего состояния; если да — переходим, иначе — невалидно. В конце проверяем, что автомат в финальном состоянии и вход исчерпан.

```text
for (char c in input) {
    if (automaton.accepts(c)) {
        automaton.switchState(c);
        input.pop(c);
    } else {
        break;
    }
}

if (automaton.canStop() && input.isEmpty()) {
    print("Valid");
} else {
    print("Invalid");
}
```

## Реализация на Java

Интерфейсы: автомат (switchState, canStop), состояние (transit, isFinal, with(transition)), переход (isPossible, state). Реализация неизменяемая: каждый switchState возвращает новый экземпляр автомата.

```java
// Конечный автомат: переход по символу и проверка допустимости остановки в финальном состоянии
public interface FiniteStateMachine {
    FiniteStateMachine switchState(CharSequence c);
    boolean canStop();
}

interface State {
    State with(Transition tr);
    State transit(CharSequence c);
    boolean isFinal();
}

interface Transition {
    boolean isPossible(CharSequence c);
    State state();
}
```


```java
public class RtFiniteStateMachine implements FiniteStateMachine {
    private State current;

    public RtFiniteStateMachine(State initial) {
        this.current = initial;
    }

    @Override
    public FiniteStateMachine switchState(CharSequence c) {
        return new RtFiniteStateMachine(this.current.transit(c));
    }

    @Override
    public boolean canStop() {
        return this.current.isFinal();
    }
}
```


```java
import java.util.ArrayList;
import java.util.List;

public class RtState implements State {
    private List<Transition> transitions;
    private boolean isFinal;

    public RtState() {
        this(false);
    }

    public RtState(boolean isFinal) {
        this.transitions = new ArrayList<>();
        this.isFinal = isFinal;
    }

    @Override
    public State transit(CharSequence c) {
        return transitions.stream()
            .filter(t -> t.isPossible(c))
            .map(Transition::state)
            .findAny()
            .orElseThrow(() -> new IllegalArgumentException("Input not accepted: " + c));
    }

    @Override
    public boolean isFinal() {
        return this.isFinal;
    }

    @Override
    public State with(Transition tr) {
        this.transitions.add(tr);
        return this;
    }
}
```

RtTransition: правило (строка или условие) и следующее состояние; isPossible проверяет символ.

```java
public class RtTransition implements Transition {
    private String rule;
    private State next;

    public RtTransition(String rule, State next) {
        this.rule = rule;
        this.next = next;
    }

    @Override
    public State state() {
        return this.next;
    }

    @Override
    public boolean isPossible(CharSequence c) {
        return this.rule.equalsIgnoreCase(String.valueOf(c));
    }
}
```

## Пример: Валидатор JSON

Упрощённый валидатор формата `{"key":"value"}`: состояния start openBrace key colon value comma/closeBrace; closeBrace — финальное.

```java
public class JsonStateMachineBuilder {
    public static FiniteStateMachine buildJsonStateMachine() {
        State start = new RtState();
        State openBrace = new RtState();
        State key = new RtState();
        State colon = new RtState();
        State value = new RtState();
        State comma = new RtState();
        State closeBrace = new RtState(true); // Финальное состояние

        start.with(new RtTransition("{", openBrace));
        openBrace.with(new RtTransition("\"", key));
        key.with(new RtTransition("\"", colon));
        colon.with(new RtTransition(":", value));
        value.with(new RtTransition("\"", comma));
        value.with(new RtTransition("}", closeBrace));
        comma.with(new RtTransition(",", openBrace));
        comma.with(new RtTransition("}", closeBrace));

        return new RtFiniteStateMachine(start);
    }
}
```

Использование: проход по символам, switchState для каждого; в конце canStop() и пустой ввод.

```java
public class JsonValidator {
    public static boolean isValid(String json) {
        FiniteStateMachine machine = JsonStateMachineBuilder.buildJsonStateMachine();

        for (int i = 0; i < json.length(); i++) {
            try {
                machine = machine.switchState(String.valueOf(json.charAt(i)));
            } catch (IllegalArgumentException e) {
                return false;
            }
        }

        return machine.canStop();
    }
}
```

```java
String json = "{\"key\":\"value\"}";
boolean isValid = JsonValidator.isValid(json);
assertTrue(isValid);

String invalidJson = "{\"key\":\"value\"";
boolean isInvalid = JsonValidator.isValid(invalidJson);
assertFalse(isInvalid);
```

## Реализация на Kotlin

```kotlin
interface FiniteStateMachineK {
    fun switchState(c: CharSequence): FiniteStateMachineK
    fun canStop(): Boolean
}

interface StateK {
    fun with(tr: TransitionK): StateK
    fun transit(c: CharSequence): StateK
    fun isFinal(): Boolean
}

interface TransitionK {
    fun isPossible(c: CharSequence): Boolean
    fun state(): StateK
}

class RtFiniteStateMachineK(private val current: StateK) : FiniteStateMachineK {
    override fun switchState(c: CharSequence): FiniteStateMachineK {
        return RtFiniteStateMachineK(current.transit(c))
    }

    override fun canStop(): Boolean = current.isFinal()
}

class RtStateK(private var isFinal: Boolean = false) : StateK {
    private val transitions = mutableListOf<TransitionK>()

    override fun transit(c: CharSequence): StateK {
        return transitions.firstOrNull { it.isPossible(c) }
            ?.state()
            ?: throw IllegalArgumentException("Input not accepted: $c")
    }

    override fun isFinal(): Boolean = isFinal

    override fun with(tr: TransitionK): StateK {
        transitions.add(tr)
        return this
    }
}

class RtTransitionK(private val rule: String, private val next: StateK) : TransitionK {
    override fun state(): StateK = next

    override fun isPossible(c: CharSequence): Boolean {
        return rule.equals(c.toString(), ignoreCase = true)
    }
}
```

```kotlin
object JsonValidatorK {
    fun isValid(input: String): Boolean {
        val automaton = buildJsonStateMachine()
        var currentAutomaton = automaton

        for (c in input) {
            currentAutomaton = currentAutomaton.switchState(c.toString())
        }

        return currentAutomaton.canStop()
    }

    private fun buildJsonStateMachine(): FiniteStateMachineK {
        val start = RtStateK()
        val openBrace = RtStateK()
        val key = RtStateK()
        val colon = RtStateK()
        val value = RtStateK()
        val comma = RtStateK()
        val closeBrace = RtStateK(true) // Финальное состояние

        start.with(RtTransitionK("{", openBrace))
        openBrace.with(RtTransitionK("\"", key))
        key.with(RtTransitionK(":", colon))
        colon.with(RtTransitionK("\"", value))
        value.with(RtTransitionK("\"", comma))
        comma.with(RtTransitionK(",", openBrace))
        comma.with(RtTransitionK("}", closeBrace))

        return RtFiniteStateMachineK(start)
    }
}
```

```kotlin
fun main() {
    val validJson = "{\"key\":\"value\"}"
    println(JsonValidatorK.isValid(validJson)) // true

    val invalidJson = "{\"key\":\"value\""
    println(JsonValidatorK.isValid(invalidJson)) // false
}
```

## Сложность

Валидация O(n) по длине строки; переход O(k) по числу переходов из состояния (обычно константа). Память: автомат O(s×t), валидация O(1).

## Особенности

Простые и наглядные правила; подходят для разных форматов. Для очень сложной грамматики число состояний и переходов растёт.

## Применение

Валидация входных данных, парсинг, компиляторы, регулярные выражения, протоколы связи.

## Варианты задачи

### Вариант 1: Валидатор email

```java
public static FiniteStateMachine buildEmailStateMachine() {
    State start = new RtState();
    State localPart = new RtState();
    State at = new RtState();
    State domain = new RtState();
    State dot = new RtState();
    State tld = new RtState(true);

    // Переходы для валидации email
    // ...

    return new RtFiniteStateMachine(start);
}
```

### Вариант 2: Валидатор чисел

```java
public static FiniteStateMachine buildNumberStateMachine() {
    State start = new RtState();
    State sign = new RtState();
    State integer = new RtState();
    State dot = new RtState();
    State decimal = new RtState(true);

    // Переходы для валидации чисел
    // ...

    return new RtFiniteStateMachine(start);
}
```

## Когда использовать

Конечные автоматы уместны при валидации структурированных данных с простыми и чёткими правилами, когда важна производительность и не хочется тянуть внешние библиотеки. Не подходят при очень сложной грамматике или контекстно-зависимых правилах; для сложных шаблонов часто удобнее регулярные выражения или парсер.

## Лучшие практики

Сначала опишите автомат (состояния, переходы, финальные) на диаграмме или в коде. Состояния и переходы лучше делать неизменяемыми. Для каждого состояния и допустимого символа должен быть переход; недопустимый символ — отказ или состояние «ошибка». Тестируйте валидные/невалидные строки, пустой ввод, граничные символы. При расширении правил добавляйте состояния и переходы, не дублируя логику.

## Решение проблем

| Симптом | Возможная причина | Решение |
|---------|-------------------|---------|
| IllegalArgumentException при валидной строке | Нет перехода по символу из текущего состояния | Добавить переход или состояние ошибки; проверить порядок символов в грамматике |
| canStop() = false в конце | Не попали в финальное состояние или лишние символы | Проверить, что все пути ведут к финальному состоянию; проверять isEmpty() входа |
| Сложно поддерживать автомат | Много состояний и переходов вручную | Вынести построение в билдер; документировать диаграммой состояний |

## Частые вопросы

**Чем конечный автомат лучше regex для валидации?** Для простых последовательностей (например, формат `{"k":"v"}`) автомат нагляден и даёт полный контроль; для сложных шаблонов regex или парсер часто проще.

**Нужна ли иммутабельность?** Не обязательна, но упрощает повторное использование одного и того же автомата и тестирование; текущее состояние можно хранить в отдельном объекте-обёртке.

**Как обрабатывать недопустимый символ?** Либо переход в отдельное «ошибка»-состояние (без переходов наружу), либо выброс исключения; в конце проверять, что не в состоянии ошибки и ввод исчерпан.

## Заключение

Конечные автоматы удобны для проверки структурированного ввода с простыми правилами. При сложной грамматике число состояний растёт; для одного символа на переход подходят регулярные выражения или парсеры.
