# Balanced Parentheses

Кратко: задача сбалансированных скобок - это проверка, правильно ли расставлены скобки в строке. Скобки считаются сбалансированными, если каждая открывающая скобка имеет соответствующую закрывающую скобку в правильном порядке.

**Дата последнего обновления:** 2025-01-15

## Полезные ссылки

### Официальная документация
- [GeeksforGeeks: Check for balanced parentheses](https://www.geeksforgeeks.org/check-for-balanced-parentheses-in-an-expression/)

### См. также
- `./palindrome-check.md` - проверка палиндромов
- `./regex-token-replacement.md` - работа с регулярными выражениями

## Содержание

- [Описание алгоритма](#описание-алгоритма)
- [Определение сбалансированности](#определение-сбалансированности)
- [Java Implementation](#java-implementation)
- [Kotlin Implementation](#kotlin-implementation)
- [Сравнение подходов](#сравнение-подходов)
- [Сложность](#сложность)

## Описание алгоритма

Задача сбалансированных скобок являются распространенной проблемой программирования.

В этом уроке мы проверим, сбалансированы ли скобки в данной строке или нет.

Этот тип строк является частью того, что известно как язык Дайка.

Скобкой считается любой из следующих символов - «(», «)», «[», «]», «{», «}».

Набор скобок считается совпадающей парой, если открывающая скобка «(», «[» и «{» стоит слева от соответствующей закрывающей скобки «)», «]» и «}», соответственно.

Однако строка, содержащая пары квадратных скобок, не сбалансирована, если набор заключенных в нее скобок не совпадает.

Точно так же строка, содержащая символы без квадратных скобок, такие как a-z, A-Z, 0-9 или другие специальные символы, такие как #, $, @, также считается несбалансированной.

### Примеры

Например, если ввод «{[(])}», пара квадратных скобок «[]» заключает в себя одну несбалансированную открывающую круглую скобку «(». Аналогично, пара круглых скобок «()», заключает в себя одну несбалансированную закрывающую квадратную скобку «]». Таким образом, входная строка «{[(])}» несбалансированная.

## Определение сбалансированности

Таким образом, строка, содержащая символы квадратных скобок, называется сбалансированной, если:

1. Соответствующая открывающая скобка находится слева от каждой соответствующей закрывающей скобки
2. Скобки, заключенные в сбалансированные скобки, также являются сбалансированными
3. Он не содержит символов, не являющихся скобками
4. Следует помнить о нескольких особых случаях: значение null считается несбалансированным, а пустая строка считается сбалансированным

### Примеры сбалансированных скобок

```
()
[()]
{[()]}
([{{[(())]}}])
```

### Примеры несбалансированных скобок

```
abc[](){}  // содержит не-скобки
{{[]()}}}  // непарные скобки
{[(])}     // неправильный порядок
```

## Java Implementation

### Подход 1: Использование String методов

Теперь, когда мы лучше понимаем нашу проблему, давайте посмотрим, как ее решить!

Существуют разные способы решения этой проблемы. В этом уроке мы рассмотрим два подхода.

Давайте сначала создадим метод, который будет возвращать true, если вход сбалансирован, и false, если вход несбалансирован:

```java
public boolean isBalanced(String str)
```

### Проверки входной строки

Рассмотрим основные проверки входной строки:

1. Если передается нулевой вход, то он не сбалансирован
2. Чтобы строка была сбалансированной, пары открывающих и закрывающих скобок должны совпадать. Следовательно, можно с уверенностью сказать, что входная строка нечетной длины не будет сбалансирована, поскольку она будет содержать по крайней мере одну несовпадающую скобку
3. Согласно постановке задачи, сбалансированное поведение должно быть проверено в скобках. Следовательно, любая входная строка, содержащая символы без скобок, является несбалансированной строкой

Учитывая эти правила, мы можем реализовать проверки:

```java
if (null == str || ((str.length() % 2) != 0)) {
    return false;
} else {
    char[] ch = str.toCharArray();
    for (char c : ch) {
        if (!(c == '{' || c == '[' || c == '(' || c == '}' || c == ']' || c == ')')) {
            return false;
        }
    }
}
```

### Реализация с replaceAll

В этом подходе мы будем перебирать входную строку, удаляя вхождения «()», «[]» и «{}» из строки, используя `String.replaceAll`. Мы продолжаем этот процесс до тех пор, пока во входной строке не будет найдено больше вхождений.

После завершения процесса, если длина нашей строки равна нулю, все совпадающие пары скобок удаляются, и входная строка уравновешивается. Если же длина не равна нулю, то в строке все же присутствуют непарные открывающие или закрывающие скобки. Следовательно, входная строка несбалансирована.

Давайте посмотрим полную реализацию:

```java
public boolean isBalanced(String str) {
    if (null == str || ((str.length() % 2) != 0)) {
        return false;
    } else {
        char[] ch = str.toCharArray();
        for (char c : ch) {
            if (!(c == '{' || c == '[' || c == '(' || c == '}' || c == ']' || c == ')')) {
                return false;
            }
        }
    }
    
    while (str.contains("()") || str.contains("[]") || str.contains("{}")) {
        str = str.replaceAll("\\(\\)", "")
            .replaceAll("\\[\\]", "")
            .replaceAll("\\{\\}", "");
    }
    
    return (str.length() == 0);
}
```

### Особенности

- Простая реализация
- Может быть медленной для длинных строк
- Многократные проходы по строке

## Подход 2: Использование Deque/Stack

Deque - это форма очереди, которая обеспечивает операции добавления, извлечения и просмотра на обоих концах очереди.

Мы будем использовать функцию сортировки «последним пришел - первым вышел» (LIFO) этой структуры данных, чтобы проверить баланс во входной строке.

### Реализация с Deque

Во-первых, давайте создадим нашу Deque:

```java
Deque<Character> deque = new LinkedList<>();
```

Обратите внимание, что здесь мы использовали LinkedList, потому что он обеспечивает реализацию интерфейса Deque.

Теперь, когда наша двухсторонняя очередь построена, мы будем перебирать каждый символ входной строки один за другим. Если символ является открывающей скобкой, то мы добавим его первым элементом в Deque:

```java
if (ch == '{' || ch == '[' || ch == '(') {
    deque.addFirst(ch);
}
```

Но, если символ является закрывающей скобкой, то мы выполним некоторые проверки LinkedList.

Во-первых, мы проверяем, является ли LinkedList пустым или нет. Пустой список означает, что закрывающая скобка не имеет соответствия. Следовательно, входная строка несбалансирована. Поэтому мы возвращаем false.

Однако, если LinkedList не пуст, мы просматриваем его последний символ, используя метод `peekFirst`. Если его можно поставить в пару с закрывающей скобкой, то удаляем этот самый верхний символ из списка с помощью метода `removeFirst` и переходим к следующей итерации цикла:

```java
if (!deque.isEmpty()
    && ((deque.peekFirst() == '{' && ch == '}')
    || (deque.peekFirst() == '[' && ch == ']')
    || (deque.peekFirst() == '(' && ch == ')'))) {
    deque.removeFirst();
} else {
    return false;
}
```

К концу цикла все символы проверяются на баланс, поэтому мы можем вернуть true. Ниже приведена полная реализация подхода на основе Deque:

```java
public boolean isBalanced(String str) {
    if (null == str || ((str.length() % 2) != 0)) {
        return false;
    } else {
        char[] ch = str.toCharArray();
        for (char c : ch) {
            if (!(c == '{' || c == '[' || c == '(' || c == '}' || c == ']' || c == ')')) {
                return false;
            }
        }
    }
    
    Deque<Character> deque = new LinkedList<>();
    
    for (char ch : str.toCharArray()) {
        if (ch == '{' || ch == '[' || ch == '(') {
            deque.addFirst(ch);
        } else {
            if (!deque.isEmpty()
                && ((deque.peekFirst() == '{' && ch == '}')
                || (deque.peekFirst() == '[' && ch == ']')
                || (deque.peekFirst() == '(' && ch == ')'))) {
                deque.removeFirst();
            } else {
                return false;
            }
        }
    }
    
    return deque.isEmpty();
}
```

### Реализация с Stack

Альтернативная реализация с использованием Stack:

```java
public boolean isBalancedWithStack(String str) {
    if (str == null || str.length() % 2 != 0) {
        return false;
    }
    
    Stack<Character> stack = new Stack<>();
    
    for (char ch : str.toCharArray()) {
        if (ch == '{' || ch == '[' || ch == '(') {
            stack.push(ch);
        } else if (ch == '}' || ch == ']' || ch == ')') {
            if (stack.isEmpty()) {
                return false;
            }
            
            char top = stack.pop();
            if ((ch == '}' && top != '{')
                || (ch == ']' && top != '[')
                || (ch == ')' && top != '(')) {
                return false;
            }
        } else {
            return false; // Не-скобки
        }
    }
    
    return stack.isEmpty();
}
```

### Улучшенная версия с Map

Для более чистого кода можно использовать Map для хранения соответствий скобок:

```java
public boolean isBalancedWithMap(String str) {
    if (str == null || str.length() % 2 != 0) {
        return false;
    }
    
    Map<Character, Character> brackets = new HashMap<>();
    brackets.put(')', '(');
    brackets.put(']', '[');
    brackets.put('}', '{');
    
    Stack<Character> stack = new Stack<>();
    
    for (char ch : str.toCharArray()) {
        if (brackets.containsValue(ch)) {
            stack.push(ch);
        } else if (brackets.containsKey(ch)) {
            if (stack.isEmpty() || stack.pop() != brackets.get(ch)) {
                return false;
            }
        } else {
            return false;
        }
    }
    
    return stack.isEmpty();
}
```

## Kotlin Implementation

### Подход 1: Использование String методов

```kotlin
fun isBalancedStringK(str: String?): Boolean {
    if (str == null || str.length % 2 != 0) {
        return false
    }
    
    var modified = str
    while (modified.contains("()") || modified.contains("[]") || modified.contains("{}")) {
        modified = modified.replace("()", "")
            .replace("[]", "")
            .replace("{}", "")
    }
    
    return modified.isEmpty()
}
```

### Подход 2: Использование Stack/Deque

```kotlin
fun isBalancedStackK(str: String?): Boolean {
    if (str == null || str.length % 2 != 0) {
        return false
    }
    
    val stack = ArrayDeque<Char>()
    
    for (ch in str) {
        if (ch == '{' || ch == '[' || ch == '(') {
            stack.addFirst(ch)
        } else {
            if (stack.isNotEmpty() && 
                ((stack.first() == '{' && ch == '}') ||
                 (stack.first() == '[' && ch == ']') ||
                 (stack.first() == '(' && ch == ')'))) {
                stack.removeFirst()
            } else {
                return false
            }
        }
    }
    
    return stack.isEmpty()
}
```

### Улучшенная версия с Map

```kotlin
fun isBalancedMapK(str: String?): Boolean {
    if (str == null || str.length % 2 != 0) {
        return false
    }
    
    val brackets = mapOf(')' to '(', ']' to '[', '}' to '{')
    val stack = ArrayDeque<Char>()
    
    for (ch in str) {
        when {
            brackets.values.contains(ch) -> stack.addFirst(ch)
            brackets.containsKey(ch) -> {
                if (stack.isEmpty() || stack.removeFirst() != brackets[ch]) {
                    return false
                }
            }
            else -> return false
        }
    }
    
    return stack.isEmpty()
}
```

### Только круглые скобки

```kotlin
fun isBalancedParenthesesK(str: String): Boolean {
    var count = 0
    for (ch in str) {
        when (ch) {
            '(' -> count++
            ')' -> {
                count--
                if (count < 0) return false
            }
        }
    }
    return count == 0
}
```

### Функциональный стиль

```kotlin
fun isBalancedFunctionalK(str: String?): Boolean {
    if (str == null || str.length % 2 != 0) return false
    
    val brackets = mapOf(')' to '(', ']' to '[', '}' to '{')
    
    return str.fold(ArrayDeque<Char>()) { stack, ch ->
        when {
            brackets.values.contains(ch) -> stack.apply { addFirst(ch) }
            brackets.containsKey(ch) -> {
                if (stack.isEmpty() || stack.removeFirst() != brackets[ch]) {
                    return false
                }
                stack
            }
            else -> return false
        }
    }.isEmpty()
}
```

### Пример использования

```kotlin
fun main() {
    // Сбалансированные
    println(isBalancedStackK("()")) // true
    println(isBalancedStackK("[()]")) // true
    println(isBalancedStackK("{[()]}")) // true
    
    // Несбалансированные
    println(isBalancedStackK("abc[](){}")) // false
    println(isBalancedStackK("{{[]()}}}")) // false
    println(isBalancedStackK("{[(])}")) // false
    
    // Только круглые скобки
    println(isBalancedParenthesesK("((()))")) // true
    println(isBalancedParenthesesK("(()")) // false
}
```

## Сравнение подходов

| Подход | Временная сложность | Пространственная сложность | Простота |
|--------|---------------------|----------------------------|----------|
| String replaceAll | O(n²) | O(n) | Простая |
| Deque/Stack | O(n) | O(n) | Средняя |
| Stack с Map | O(n) | O(n) | Средняя |

## Сложность

### Подход 1: String replaceAll

- **Временная сложность:** O(n²) - в худшем случае может потребоваться n/2 проходов
- **Пространственная сложность:** O(n) - для хранения строки

### Подход 2: Deque/Stack

- **Временная сложность:** O(n) - один проход по строке
- **Пространственная сложность:** O(n) - для стека в худшем случае

## Особенности

- **Эффективность:** Stack/Deque подход более эффективен
- **Простота:** String replaceAll проще понять
- **Гибкость:** Легко расширить для других типов скобок

## Применение

Проверка сбалансированности скобок используется в:

- Парсерах и компиляторах
- Валидации выражений
- Обработке JSON/XML
- Редакторах кода (подсветка синтаксиса)
- Калькуляторах

## Варианты задачи

### Вариант 1: Только круглые скобки

```java
public boolean isBalancedParentheses(String str) {
    int count = 0;
    for (char ch : str.toCharArray()) {
        if (ch == '(') {
            count++;
        } else if (ch == ')') {
            count--;
            if (count < 0) {
                return false;
            }
        }
    }
    return count == 0;
}
```

### Вариант 2: С учетом других символов

```java
public boolean isBalancedWithOtherChars(String str) {
    Stack<Character> stack = new Stack<>();
    
    for (char ch : str.toCharArray()) {
        if (ch == '{' || ch == '[' || ch == '(') {
            stack.push(ch);
        } else if (ch == '}' || ch == ']' || ch == ')') {
            if (stack.isEmpty()) {
                return false;
            }
            char top = stack.pop();
            if ((ch == '}' && top != '{')
                || (ch == ']' && top != '[')
                || (ch == ')' && top != '(')) {
                return false;
            }
        }
        // Игнорируем другие символы
    }
    
    return stack.isEmpty();
}
```

## Когда использовать

### Используйте String replaceAll, когда:

- Строки короткие
- Нужна простота реализации
- Производительность не критична

### Используйте Stack/Deque, когда:

- Строки длинные
- Нужна максимальная производительность
- Важна эффективность

## Заключение

В этом уроке мы рассмотрели два подхода к проверке сбалансированности скобок. Подход с использованием Stack/Deque является более эффективным и рекомендуется для большинства случаев использования, в то время как подход с String replaceAll может быть полезен для простых случаев или обучения.
