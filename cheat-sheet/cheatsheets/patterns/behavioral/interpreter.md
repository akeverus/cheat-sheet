# Interpreter (Интерпретатор)

Паттерн Interpreter (Интерпретатор) — это поведенческий паттерн проектирования, который определяет грамматику конкретного языка объектно-ориентированным способом и позволяет интерпретировать предложения этого языка. Короче говоря, шаблон определяет грамматику конкретного языка объектно-ориентированным способом, который может быть оценен самим интерпретатором.

**Дата последнего обновления:** 2025-01-15

## Содержание

- [Полезные ссылки](#�-олезн�-е-�-�-�-лки)
  - [Официальная документация](#�-�-и�-иал�-ная-док�-мен�-а�-ия)
  - [Baeldung](#baeldung)
  - [См. также](#�-м-�-акже)
- [Оглавление](#�-главление)
- [Структура паттерна](#�-�-�-�-к�-�-�-а-па�-�-е�-на)
- [Java Implementation](#java-implementation)
  - [Пример реализации](#�-�-име�-�-еализа�-ии)



## Полезные ссылки

### Официальная документация
- [Design Patterns: Elements of Reusable Object-Oriented Software](https://en.wikipedia.org/wiki/Design_Patterns)

### Baeldung
- [Interpreter Pattern in Java](https://www.baeldung.com/java-interpreter-pattern)
- [Interpreter Pattern in Kotlin](https://www.baeldung.com/kotlin-interpreter-pattern)

### См. также
- [Visitor (Посетитель)](visitor.md)
- [Command (Команда)](command.md)

## Оглавление

- [Введение](#введение)
- [Структура паттерна](#структура-паттерна)
- [Java Implementation](#java-implementation)
- [Kotlin Implementation](#kotlin-implementation)
- [Использование в JDK](#использование-в-jdk)

Имея это в виду, технически мы могли бы создать наше собственное регулярное выражение, собственный интерпретатор DSL или мы могли бы проанализировать любой из человеческих языков, построить абстрактные синтаксические деревья, а затем запустить интерпретацию.

Это лишь некоторые из потенциальных вариантов использования, но если мы немного подумаем, мы могли бы найти еще больше вариантов его использования, например, в наших IDE, поскольку они постоянно интерпретируют код, который мы пишем, и, таким образом, предоставляют нам бесценные подсказки.

Шаблон интерпретатора обычно следует использовать, когда грамматика относительно проста.

В противном случае его будет сложно поддерживать.

## Структура паттерна

На приведенной выше диаграмме показаны две основные сущности: контекст и выражение.

Теперь любой язык должен быть каким-то образом выражен, и слова (выражения) будут иметь какое-то значение в зависимости от данного контекста.

AbstractExpression определяет один абстрактный метод, который принимает контекст в качестве параметра. Благодаря этому каждое выражение будет влиять на контекст, менять свое состояние и либо продолжать интерпретацию, либо возвращать само результат.

Следовательно, контекст будет держателем глобального состояния обработки и будет повторно использоваться в течение всего процесса интерпретации.

Итак, в чем разница между TerminalExpression и NonTerminalExpression?

Выражение NonTerminalExpression может иметь одно или несколько других связанных с ним абстрактных выражений, поэтому его можно интерпретировать рекурсивно. В конце концов, процесс интерпретации должен завершиться выражением TerminalExpression, которое вернет результат.

Стоит отметить, что NonTerminalExpression является составным.

Наконец, роль клиента заключается в создании или использовании уже созданного абстрактного синтаксического дерева, которое представляет собой не что иное, как предложение, определенное в созданном языке.

## Java Implementation

### Пример реализации

Чтобы показать шаблон в действии, мы создадим простой объектно-ориентированный синтаксис, подобный SQL, который затем будет интерпретирован и вернет нам результат.

Сначала мы определим выражения Select, From и Where, построим синтаксическое дерево в клиентском классе и запустим интерпретацию.

Интерфейс Expression будет иметь метод интерпретации:

```java
List<String> interpret(Context ctx);
```

Далее мы определяем первое выражение, класс Select:

```java
class Select implements Expression {
    private String column;
    private From from;

    @Override
    public List<String> interpret(Context ctx) {
        ctx.setColumn(column);
        return from.interpret(ctx);
    }
}
```

Он получает имя столбца, которое нужно выбрать, и другое конкретное выражение типа From в качестве параметров в конструкторе.

Обратите внимание, что в переопределенном методе terpret() он устанавливает состояние контекста и передает интерпретацию дальше другому выражению вместе с контекстом.

Таким образом, мы видим, что это NonTerminalExpression.

Другим выражением является класс From:

```java
class From implements Expression {
    private String table;
    private Where where;

    @Override
    public List<String> interpret(Context ctx) {
        ctx.setTable(table);
        if (where == null) {
            return ctx.search();
        }
        return where.interpret(ctx);
    }
}
```

Теперь в SQL предложение where является необязательным, поэтому этот класс является либо терминальным, либо нетерминальным выражением.

Если пользователь решит не использовать предложение where, выражение From будет завершено вызовом `ctx.search()` и возвратит результат. В противном случае это будет интерпретироваться дополнительно.

Выражение Where снова изменяет контекст, устанавливая необходимый фильтр, и завершает интерпретацию поисковым вызовом:

```java
class Where implements Expression {
    private Predicate<String> filter;

    @Override
    public List<String> interpret(Context ctx) {
        ctx.setFilter(filter);
        return ctx.search();
    }
}
```

Например, класс Context содержит данные, имитирующие таблицу базы данных.

Обратите внимание, что у него есть три ключевых поля, которые изменяются каждым подклассом Expression и методом поиска:

```java
class Context {
    private static Map<String, List<Row>> tables = new HashMap<>();
    
    static {
        List<Row> list = new ArrayList<>();
        list.add(new Row("John", "Doe"));
        list.add(new Row("Jan", "Kowalski"));
        list.add(new Row("Dominic", "Doom"));
        tables.put("people", list);
    }

    private String table;
    private String column;
    private Predicate<String> whereFilter;

    List<String> search() {
        List<String> result = tables.entrySet()
            .stream()
            .filter(entry -> entry.getKey().equalsIgnoreCase(table))
            .flatMap(entry -> Stream.of(entry.getValue()))
            .flatMap(Collection::stream)
            .map(Row::toString)
            .flatMap(columnMapper)
            .filter(whereFilter)
            .collect(Collectors.toList());
        clear();
        return result;
    }
}
```

После завершения поиска контекст очищается, поэтому для столбца, таблицы и фильтра устанавливаются значения по умолчанию.

Таким образом, каждая интерпретация не повлияет на другую.

В целях тестирования давайте взглянем на класс InterpreterDemo:

```java
public class InterpreterDemo {
    public static void main(String[] args) {
        Expression query = new Select("name", new From("people"));
        Context ctx = new Context();
        List<String> result = query.interpret(ctx);
        System.out.println(result);

        Expression query2 = new Select("*", new From("people"));
        List<String> result2 = query2.interpret(ctx);
        System.out.println(result2);

        Expression query3 = new Select("name",
            new From("people",
                new Where(name -> name.toLowerCase().startsWith("d"))));
        List<String> result3 = query3.interpret(ctx);
        System.out.println(result3);
    }
}
```

Сначала мы строим синтаксическое дерево с созданными выражениями, инициализируем контекст и затем запускаем интерпретацию. Контекст используется повторно, но, как мы показали выше, очищается после каждого поискового вызова.

Запустив программу, вывод должен быть следующим:

```
[John, Jan, Dominic]
[John Doe, Jan Kowalski, Dominic Doom]
[Dominic]
```

Когда грамматика становится более сложной, ее становится труднее поддерживать.

Это видно на представленном примере. Было бы достаточно легко добавить еще одно выражение, например Limit, но его будет не так просто поддерживать, если мы решим продолжать расширять его всеми другими выражениями.

Шаблон проектирования интерпретатора отлично подходит для относительно простой интерпретации грамматики, которую не нужно сильно развивать и расширять.

В приведенном выше примере мы показали, что можно построить SQL-подобный запрос объектно-ориентированным способом с помощью шаблона интерпретатора.

## Kotlin Implementation

В Kotlin паттерн Interpreter может быть реализован следующим образом:

```kotlin
interface Expression {
    fun interpret(ctx: Context): List<String>
}

class Select(private val column: String, private val from: From) : Expression {
    override fun interpret(ctx: Context): List<String> {
        ctx.setColumn(column)
        return from.interpret(ctx)
    }
}

class From(private val table: String, private val where: Where? = null) : Expression {
    override fun interpret(ctx: Context): List<String> {
        ctx.setTable(table)
        return where?.interpret(ctx) ?: ctx.search()
    }
}

class Where(private val filter: (String) -> Boolean) : Expression {
    override fun interpret(ctx: Context): List<String> {
        ctx.setFilter(filter)
        return ctx.search()
    }
}

class Context {
    private val tables = mapOf(
        "people" to listOf(
            Row("John", "Doe"),
            Row("Jan", "Kowalski"),
            Row("Dominic", "Doom")
        )
    )

    private var table: String = ""
    private var column: String = ""
    private var whereFilter: (String) -> Boolean = { true }

    fun setTable(table: String) {
        this.table = table
    }

    fun setColumn(column: String) {
        this.column = column
    }

    fun setFilter(filter: (String) -> Boolean) {
        this.whereFilter = filter
    }

    fun search(): List<String> {
        val result = tables[table]
            ?.flatMap { row ->
                when (column) {
                    "*" -> listOf(row.toString())
                    "name" -> listOf(row.firstName)
                    else -> emptyList()
                }
            }
            ?.filter(whereFilter)
            ?: emptyList()
        clear()
        return result
    }

    private fun clear() {
        table = ""
        column = ""
        whereFilter = { true }
    }
}

data class Row(val firstName: String, val lastName: String) {
    override fun toString() = "$firstName $lastName"
}
```

Использование:

```kotlin
fun main() {
    val ctx = Context()
    
    val query1 = Select("name", From("people"))
    println(query1.interpret(ctx)) // [John, Jan, Dominic]
    
    val query2 = Select("*", From("people"))
    println(query2.interpret(ctx)) // [John Doe, Jan Kowalski, Dominic Doom]
    
    val query3 = Select("name", From("people", Where { name ->
        name.toLowerCase().startsWith("d")
    }))
    println(query3.interpret(ctx)) // [Dominic]
}
```

## Использование в JDK

Наконец, вы можете найти использование этого шаблона в JDK, в частности, в `java.util.Pattern`, `java.text.Format` или `java.text.Normalizer`.
