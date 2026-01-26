# Suffix Tree Pattern Matching

Кратко: быстрое сопоставление строк с образцом с использованием дерева суффиксов. Рассматривается реализация структуры данных дерева суффиксов для эффективного поиска паттернов в тексте за линейное время.

**Дата последнего обновления:** 2025-01-15

## Полезные ссылки

### Официальная документация
- [GeeksforGeeks: Suffix Tree](https://www.geeksforgeeks.org/pattern-searching-using-suffix-tree/)

### См. также
- `./large-text-string-search.md` - алгоритмы поиска строк
- `./multiple-keywords-check.md` - проверка нескольких ключевых слов

## Содержание

- [Описание алгоритма](#описание-алгоритма)
- [Java Implementation](#java-implementation)
- [Kotlin Implementation](#kotlin-implementation)
- [Сложность](#сложность)

## Описание алгоритма

В этом уроке мы рассмотрим концепцию сопоставления строк с образцом и то, как мы можем сделать это быстрее. Затем мы рассмотрим его реализацию на Java.

В строках сопоставление с образцом - это процесс проверки заданной последовательности символов, называемой шаблоном, в последовательности символов, называемой текстом.

### Основные ожидания

Основные ожидания от сопоставления с шаблоном, когда шаблон не является регулярным выражением:

1. Совпадение должно быть точным, а не частичным
2. Результат должен содержать все совпадения, а не только первое совпадение

### Пример

Давайте используем пример, чтобы понять простую задачу сопоставления с образцом:

```
Pattern: NA
Text: HAVANABANANA
Match1: ----NA--------
Match2: --------NA----
Match3: -----------NA
```

Мы видим, что паттерн NA встречается в тексте трижды. Чтобы получить этот результат, мы можем подумать о перемещении шаблона вниз по тексту по одному символу за раз и проверке совпадения.

Однако это метод грубой силы с временной сложностью O(p*t), где p - длина шаблона, а t - длина текста.

### Trie и Suffix Trie

Мы можем сократить время поиска, сохранив шаблоны в структуре данных trie, которая известна своим быстрым поиском элементов.

Мы знаем, что структура данных trie хранит символы строки в древовидной структуре. Итак, для двух строк {NA, NAB} мы получим дерево с двумя путями.

Создание дерева позволяет перемещать группу шаблонов вниз по тексту и проверять совпадения всего за одну итерацию.

Обратите внимание, что мы используем символ $ для обозначения конца строки.

Суффикс trie, с другой стороны, представляет собой структуру данных trie, построенную с использованием всех возможных суффиксов одной строки.

Для предыдущего примера HAVANABANANA мы можем построить суффиксное дерево.

Попытки суффикса создаются для текста и обычно выполняются как часть этапа предварительной обработки. После этого можно быстро выполнить поиск шаблонов, найдя путь, соответствующий последовательности шаблонов.

Однако известно, что дерево суффиксов занимает много места, поскольку каждый символ строки хранится в ребре.

### Suffix Tree

Суффиксное дерево - это просто сжатое суффиксное дерево. Это означает, что, соединяя ребра, мы можем хранить группу символов и тем самым значительно уменьшать объем памяти.

Каждый путь, начинающийся от корня к листу, представляет собой суффикс строки.

Дерево суффиксов также хранит положение суффикса в листовом узле. Например, BANANA$ - это суффикс, начинающийся с седьмой позиции. Следовательно, его значение будет равно шести с использованием нумерации, начинающейся с нуля.

Итак, посмотрев на вещи в перспективе, мы можем видеть, что совпадение с шаблоном происходит, когда мы можем получить путь, начинающийся от корневого узла с ребрами, полностью совпадающими с заданным шаблоном позиционно.

Если путь заканчивается на листовом узле, мы получаем совпадение суффикса. В противном случае мы получим просто совпадение подстроки. Например, шаблон NA является суффиксом HAVANABANA[NA] и подстрокой HAVA[NA]BANANA.

## Java Implementation

### Структуры данных

Давайте создадим структуру данных дерева суффиксов. Нам понадобятся два класса домена.

### Класс Node

Во-первых, нам нужен класс для представления узла дерева. Он должен хранить ребра дерева и его дочерние узлы. Кроме того, когда это конечный узел, ему необходимо хранить позиционное значение суффикса.

Итак, давайте создадим наш класс Node:

```java
public class Node {
    private String text;
    private List<Node> children;
    private int position;
    
    public Node(String word, int position) {
        this.text = word;
        this.position = position;
        this.children = new ArrayList<>();
    }
    
    // Getters and setters
    public String getText() { return text; }
    public void setText(String text) { this.text = text; }
    public List<Node> getChildren() { return children; }
    public int getPosition() { return position; }
    public void setPosition(int position) { this.position = position; }
}
```

### Класс SuffixTree

Во-вторых, нам нужен класс для представления дерева и хранения корневого узла. Он также должен хранить полный текст, из которого генерируются суффиксы.

Следовательно, у нас есть класс SuffixTree:

```java
public class SuffixTree {
    private static final String WORD_TERMINATION = "$";
    private static final int POSITION_UNDEFINED = -1;
    private Node root;
    private String fullText;
    
    public SuffixTree(String text) {
        root = new Node("", POSITION_UNDEFINED);
        fullText = text;
        
        for (int i = 0; i < text.length(); i++) {
            addSuffix(text.substring(i) + WORD_TERMINATION, i);
        }
    }
}
```

## Построение дерева суффиксов

Прежде чем мы напишем нашу основную логику для хранения данных, давайте добавим несколько вспомогательных методов.

### Вспомогательные методы

#### Добавление дочернего узла

```java
private void addChildNode(Node parentNode, String text, int index) {
    parentNode.getChildren().add(new Node(text, index));
}
```

#### Поиск общего префикса

```java
private String getLongestCommonPrefix(String str1, String str2) {
    int compareLength = Math.min(str1.length(), str2.length());
    for (int i = 0; i < compareLength; i++) {
        if (str1.charAt(i) != str2.charAt(i)) {
            return str1.substring(0, i);
        }
    }
    return str1.substring(0, compareLength);
}
```

#### Разделение узла

```java
private void splitNodeToParentAndChild(Node parentNode, String parentNewText, String childNewText) {
    Node childNode = new Node(childNewText, parentNode.getPosition());
    
    if (parentNode.getChildren().size() > 0) {
        while (parentNode.getChildren().size() > 0) {
            childNode.getChildren().add(parentNode.getChildren().remove(0));
        }
    }
    
    parentNode.getChildren().add(childNode);
    parentNode.setText(parentNewText);
    parentNode.setPosition(POSITION_UNDEFINED);
}
```

### Обход дерева

Теперь давайте напишем нашу логику для обхода дерева, если мы можем позиционно сопоставить заданный шаблон:

```java
private List<Node> getAllNodesInTraversePath(String pattern, Node startNode, boolean isAllowPartialMatch) {
    List<Node> nodes = new ArrayList<>();
    
    for (int i = 0; i < startNode.getChildren().size(); i++) {
        Node currentNode = startNode.getChildren().get(i);
        String nodeText = currentNode.getText();
        
        if (pattern.charAt(0) == nodeText.charAt(0)) {
            if (isAllowPartialMatch && pattern.length() <= nodeText.length()) {
                nodes.add(currentNode);
                return nodes;
            }
            
            int compareLength = Math.min(nodeText.length(), pattern.length());
            
            for (int j = 1; j < compareLength; j++) {
                if (pattern.charAt(j) != nodeText.charAt(j)) {
                    if (isAllowPartialMatch) {
                        nodes.add(currentNode);
                    }
                    return nodes;
                }
            }
            
            nodes.add(currentNode);
            
            if (pattern.length() > compareLength) {
                List<Node> nodes2 = getAllNodesInTraversePath(
                    pattern.substring(compareLength), 
                    currentNode, 
                    isAllowPartialMatch
                );
                
                if (nodes2.size() > 0) {
                    nodes.addAll(nodes2);
                } else if (!isAllowPartialMatch) {
                    nodes.add(null);
                }
            }
            
            return nodes;
        }
    }
    
    return nodes;
}
```

### Добавление суффикса

Теперь мы можем написать нашу логику для хранения данных. Начнем с определения нового метода addSuffix:

```java
private void addSuffix(String suffix, int position) {
    List<Node> nodes = getAllNodesInTraversePath(suffix, root, true);
    
    if (nodes.size() == 0) {
        addChildNode(root, suffix, position);
    } else {
        Node lastNode = nodes.remove(nodes.size() - 1);
        String newText = suffix;
        
        if (nodes.size() > 0) {
            String existingSuffixUptoLastNode = nodes.stream()
                .map(a -> a.getText())
                .reduce("", String::concat);
            newText = newText.substring(existingSuffixUptoLastNode.length());
        }
        
        extendNode(lastNode, newText, position);
    }
}
```

### Расширение узла

```java
private void extendNode(Node node, String newText, int position) {
    String currentText = node.getText();
    String commonPrefix = getLongestCommonPrefix(currentText, newText);
    
    if (commonPrefix != currentText) {
        String parentText = currentText.substring(0, commonPrefix.length());
        String childText = currentText.substring(commonPrefix.length());
        splitNodeToParentAndChild(node, parentText, childText);
    }
    
    String remainingText = newText.substring(commonPrefix.length());
    addChildNode(node, remainingText, position);
}
```

## Поиск паттернов

Определив нашу древовидную структуру суффиксов для хранения данных, теперь мы можем написать логику для выполнения нашего поиска.

### Получение позиций

```java
private List<Integer> getPositions(Node node) {
    List<Integer> positions = new ArrayList<>();
    
    if (node.getText().endsWith(WORD_TERMINATION)) {
        positions.add(node.getPosition());
    }
    
    for (int i = 0; i < node.getChildren().size(); i++) {
        positions.addAll(getPositions(node.getChildren().get(i)));
    }
    
    return positions;
}
```

### Поиск текста

```java
public List<String> searchText(String pattern) {
    List<String> result = new ArrayList<>();
    List<Node> nodes = getAllNodesInTraversePath(pattern, root, false);
    
    if (nodes.size() > 0) {
        Node lastNode = nodes.get(nodes.size() - 1);
        if (lastNode != null) {
            List<Integer> positions = getPositions(lastNode);
            positions = positions.stream()
                .sorted()
                .collect(Collectors.toList());
            
            positions.forEach(m -> result.add(markPatternInText(m, pattern)));
        }
    }
    
    return result;
}
```

### Отметка паттерна в тексте

```java
private String markPatternInText(Integer startPosition, String pattern) {
    String matchingTextLHS = fullText.substring(0, startPosition);
    String matchingText = fullText.substring(startPosition, startPosition + pattern.length());
    String matchingTextRHS = fullText.substring(startPosition + pattern.length());
    return matchingTextLHS + "[" + matchingText + "]" + matchingTextRHS;
}
```

### Пример использования

```java
SuffixTree suffixTree = new SuffixTree("havanabanana");

// Поиск паттерна "a"
List<String> matches = suffixTree.searchText("a");
matches.stream().forEach(m -> System.out.println(m));
// Вывод:
// h[a]vanabanana
// hav[a]nabanana
// havan[a]banana
// havanab[a]nana
// havanaban[a]na
// havanabanan[a]

// Поиск паттерна "nab"
matches = suffixTree.searchText("nab");
matches.stream().forEach(m -> System.out.println(m));
// Вывод:
// hava[nab]anana
```

## Kotlin Implementation

### Класс Node

```kotlin
class NodeK(
    var text: String = "",
    var children: MutableList<NodeK> = mutableListOf(),
    var position: Int = -1
)
```

### Класс SuffixTree

```kotlin
class SuffixTreeK(private val fullText: String) {
    private val root = NodeK()
    
    init {
        buildTree()
    }
    
    private fun buildTree() {
        for (i in fullText.indices) {
            addSuffix(fullText.substring(i), i)
        }
    }
    
    private fun addSuffix(suffix: String, position: Int) {
        var node = root
        var i = 0
        
        while (i < suffix.length) {
            var child: NodeK? = null
            val ch = suffix[i]
            
            for (c in node.children) {
                if (c.text.startsWith(ch)) {
                    child = c
                    break
                }
            }
            
            if (child == null) {
                child = NodeK(suffix.substring(i), mutableListOf(), position)
                node.children.add(child)
                break
            } else {
                val j = getMatchingLength(child.text, suffix.substring(i))
                if (j < child.text.length) {
                    val parent = NodeK(
                        child.text.substring(0, j),
                        mutableListOf(),
                        child.position
                    )
                    child.text = child.text.substring(j)
                    parent.children.add(child)
                    node.children.remove(child)
                    node.children.add(parent)
                    node = parent
                } else {
                    node = child
                    i += j
                }
            }
        }
    }
    
    private fun getMatchingLength(str1: String, str2: String): Int {
        var count = 0
        val min = minOf(str1.length, str2.length)
        for (i in 0 until min) {
            if (str1[i] == str2[i]) {
                count++
            } else {
                break
            }
        }
        return count
    }
    
    private fun getAllNodesInTraversePath(
        pattern: String,
        startNode: NodeK,
        isAllowPartialMatch: Boolean
    ): List<NodeK> {
        val nodes = mutableListOf<NodeK>()
        for (i in 0 until startNode.children.size) {
            val currentNode = startNode.children[i]
            val nodeText = currentNode.text
            
            if (fullText.substring(fullText.length - nodeText.length) == nodeText &&
                pattern.length > nodeText.length &&
                pattern.substring(0, nodeText.length) == nodeText) {
                nodes.add(currentNode)
                nodes.addAll(getAllNodesInTraversePath(
                    pattern.substring(nodeText.length),
                    currentNode,
                    isAllowPartialMatch
                ))
            } else if (pattern == nodeText || (isAllowPartialMatch && pattern.startsWith(nodeText))) {
                nodes.add(currentNode)
            } else if (nodeText.startsWith(pattern)) {
                nodes.add(currentNode)
            }
        }
        return nodes
    }
    
    private fun getPositions(node: NodeK): List<Int> {
        val positions = mutableListOf<Int>()
        if (node.position != -1) {
            positions.add(node.position)
        }
        
        for (child in node.children) {
            positions.addAll(getPositions(child))
        }
        
        return positions
    }
    
    fun searchText(pattern: String): List<String> {
        val result = mutableListOf<String>()
        val nodes = getAllNodesInTraversePath(pattern, root, false)
        
        if (nodes.isNotEmpty()) {
            val lastNode = nodes.lastOrNull()
            if (lastNode != null) {
                val positions = getPositions(lastNode).sorted()
                positions.forEach { pos ->
                    result.add(markPatternInText(pos, pattern))
                }
            }
        }
        
        return result
    }
    
    private fun markPatternInText(startPosition: Int, pattern: String): String {
        val matchingTextLHS = fullText.substring(0, startPosition)
        val matchingText = fullText.substring(startPosition, startPosition + pattern.length)
        val matchingTextRHS = fullText.substring(startPosition + pattern.length)
        return matchingTextLHS + "[" + matchingText + "]" + matchingTextRHS
    }
}
```

### Пример использования

```kotlin
fun main() {
    val suffixTree = SuffixTreeK("havanabanana")
    
    // Поиск паттерна "a"
    val matches1 = suffixTree.searchText("a")
    matches1.forEach { println(it) }
    // Вывод:
    // h[a]vanabanana
    // hav[a]nabanana
    // havan[a]banana
    // havanab[a]nana
    // havanaban[a]na
    // havanabanan[a]
    
    // Поиск паттерна "nab"
    val matches2 = suffixTree.searchText("nab")
    matches2.forEach { println(it) }
    // Вывод:
    // hava[nab]anana
}
```

## Сложность

### Временная сложность

- **Построение дерева:** O(t), где t - длина текста
- **Поиск паттерна:** O(p), где p - длина паттерна
- **Общая сложность:** O(t + p) для одного паттерна

### Пространственная сложность

- **Дерево суффиксов:** O(t) - линейная по длине текста

## Особенности

- **Эффективность:** Поиск за линейное время
- **Память:** Линейная по длине текста
- **Применение:** Эффективен для множественных поисков

## Применение

Дерево суффиксов используется в:

- Биоинформатике (поиск в ДНК)
- Поисковых системах
- Сжатии данных
- Анализе текста
- Обработке строк

## Когда использовать

### Используйте дерево суффиксов, когда:

- Множественные поиски в одном тексте
- Нужна максимальная производительность поиска
- Текст статичен или редко меняется

### Альтернативы:

- **Trie:** Для множества паттернов
- **KMP:** Для одного паттерна
- **Boyer-Moore:** Для практических применений

## Заключение

При построении суффиксного дерева для заданного текста длины t временная сложность составляет O(t). Тогда для поиска шаблона длины p временная сложность равна O(p). Вспомните, что для поиска методом грубой силы это было O(p*t). Таким образом, поиск шаблона становится быстрее после предварительной обработки текста.

В этой статье мы впервые поняли концепции трех структур данных - trie, suffix trie и suffix tree. Затем мы увидели, как дерево суффиксов можно использовать для компактного хранения суффиксов. Позже мы увидели, как использовать дерево суффиксов для хранения данных и выполнения поиска по шаблону.
