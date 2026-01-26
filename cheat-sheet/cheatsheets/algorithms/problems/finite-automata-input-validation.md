# Finite Automata Input Validation

Кратко: проверка ввода с помощью конечных автоматов (конечных автоматов). Рассматривается реализация валидатора JSON с использованием концепции конечных автоматов для формализации правил грамматики.

**Дата последнего обновления:** 2025-01-15

## Полезные ссылки

### Официальная документация
- [Baeldung: Finite State Machines](https://www.baeldung.com/java-finite-state-machine)

### См. также
- `../strings/regex-token-replacement.md` - регулярные выражения
- `./state-machine-with-enums.md` - конечный автомат с enum

## Содержание

- [Описание алгоритма](#описание-алгоритма)
- [Концепция конечных автоматов](#концепция-конечных-автоматов)
- [Java Implementation](#java-implementation)
- [Kotlin Implementation](#kotlin-implementation)
- [Пример: Валидатор JSON](#пример-валидатор-json)
- [Сложность](#сложность)

## Описание алгоритма

Если вы изучали CS, вы, несомненно, прошли курс по компиляторам или что-то подобное; на этих занятиях преподается концепция конечного автомата (также известного как конечный автомат). Это способ формализации правил грамматики языков.

Так как же эта забытая концепция может быть полезна нам, программистам высокого уровня, которым не нужно беспокоиться о создании нового компилятора?

Что ж, оказывается, эта концепция может упростить множество бизнес-сценариев и дать нам инструменты для рассуждений о сложной логике.

В качестве быстрого примера мы также можем проверить ввод без внешней сторонней библиотеки.

## Концепция конечных автоматов

Короче говоря, такая машина объявляет состояния и способы перехода из одного состояния в другое. Если вы пропускаете поток через него, вы можете проверить его формат с помощью следующего алгоритма (псевдокода):

```java
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

Мы говорим, что автомат «принимает» данный символ, если есть какая-либо стрелка, идущая из текущего состояния, на котором есть символ. Переключение состояний означает, что выполняется следование за указателем и текущее состояние заменяется состоянием, на которое указывает стрелка.

Наконец, когда цикл завершен, мы проверяем, «может ли автомат остановиться» (текущее состояние обведено двойным кружком) и исчерпаны ли входные данные.

## Java Implementation

### Реализация

Реализовать конечный автомат довольно просто. У нас есть следующее:

```java
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

Отношения между ними таковы:

1. Конечный автомат имеет одно текущее состояние и сообщает нам, может ли он остановиться или нет (является ли состояние окончательным или нет)
2. Состояние имеет список переходов, которым можно следовать (исходящие стрелки)
3. Переход сообщает нам, принят ли персонаж, и дает нам следующее состояние

### Реализация FiniteStateMachine

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

Обратите внимание, что реализация FiniteStateMachine является неизменной. В основном это делается для того, чтобы один экземпляр можно было использовать несколько раз.

### Реализация State

Далее у нас есть реализация RtState. Метод `with(Transition)` возвращает экземпляр после добавления перехода для беглости. Состояние также сообщает нам, является ли оно окончательным (обведено двойным кружком) или нет:

```java
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

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

### Реализация Transition

И, наконец, RtTransition, который проверяет правило перехода и может выдать следующее состояние:

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

Давайте напишем простой валидатор для объекта JSON, чтобы увидеть алгоритм в действии.

Обратите внимание, что значение может быть одним из следующих: строка, целое число, логическое значение, нуль или другой объект JSON. Для краткости в нашем примере мы будем рассматривать только строки.

### Построение автомата для JSON

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

### Использование валидатора

С этой реализацией вы сможете построить любой конечный автомат. Алгоритм, описанный в начале, прост:

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

### Пример использования

```java
String json = "{\"key\":\"value\"}";
boolean isValid = JsonValidator.isValid(json);
assertTrue(isValid);

String invalidJson = "{\"key\":\"value\"";
boolean isInvalid = JsonValidator.isValid(invalidJson);
assertFalse(isInvalid);
```

## Kotlin Implementation

### Интерфейсы и классы

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

### Валидатор JSON

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

### Пример использования

```kotlin
fun main() {
    val validJson = "{\"key\":\"value\"}"
    println(JsonValidatorK.isValid(validJson)) // true
    
    val invalidJson = "{\"key\":\"value\""
    println(JsonValidatorK.isValid(invalidJson)) // false
}
```

## Сложность

### Временная сложность

- **Валидация:** O(n) - где n - длина входной строки
- **Переход:** O(k) - где k - количество переходов из состояния (обычно константа)

### Пространственная сложность

- **Хранение автомата:** O(s × t) - где s - количество состояний, t - среднее количество переходов
- **Валидация:** O(1) - только текущее состояние

## Особенности

- **Простота:** Легко понять и реализовать
- **Гибкость:** Можно построить автомат для различных форматов
- **Ограничения:** Может усложниться для сложного ввода

## Применение

Конечные автоматы используются в:

- Валидации входных данных
- Парсинге
- Компиляторах
- Регулярных выражениях
- Протоколах связи

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

### Используйте конечные автоматы, когда:

- Нужна валидация структурированных данных
- Правила валидации простые и четкие
- Важна производительность
- Не хотите использовать внешние библиотеки

### Не используйте, когда:

- Правила валидации очень сложные
- Нужна поддержка регулярных выражений
- Валидация требует контекстно-зависимых правил

## Заключение

Конечные автоматы - отличные инструменты, которые можно использовать для проверки структурированных данных.

Однако они малоизвестны, потому что могут усложниться, когда дело доходит до сложного ввода (поскольку переход можно использовать только для одного символа). Тем не менее, они отлично подходят для проверки простого набора правил.
